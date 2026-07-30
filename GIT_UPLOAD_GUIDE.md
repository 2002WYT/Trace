# Publish Trace entirely from the command line / 全程使用命令行发布 Trace

This guide uses PowerShell, Git, and GitHub CLI (`gh`). It does not require the GitHub web interface. Keep one PowerShell window open for the whole release so the variables remain available.

本指南只使用 PowerShell、Git 和 GitHub CLI（`gh`），不需要操作 GitHub 网页。发布期间请保持同一个 PowerShell 窗口，确保变量持续有效。

## 0. Release configuration / 发布配置

Change the version only on the marked line. Every later command derives the tag, release title, release-notes path, APK filename, artifact name, and download URL from it.

每次发布只修改标记出的版本号。后续命令会自动生成标签、Release 标题、发布说明路径、APK 文件名、构建产物名称和下载地址。

```powershell
# ===== ONLY CHANGE THIS VERSION LINE / 每次发布只改这一行 =====
$Version = "1.2.0"

# Fixed project settings / 固定项目设置
$ProjectRoot = "D:\WORK\codexworks\guessing trace\Trace"
$Repo = "2002WYT/Trace"
$Branch = "main"
$PagesUrl = "https://2002wyt.github.io/Trace/"
$SigningAlias = "trace-release"
$Keystore = Join-Path $env:USERPROFILE ".trace-signing\trace-release.jks"

# Derived values: do not edit / 以下内容自动生成，不要修改
if ($Version -notmatch '^\d+\.\d+\.\d+$') {
    throw "Version must use x.y.z format / 版本号必须使用 x.y.z 格式"
}

$Tag = "v$Version"
$ArtifactBase = "Trace-$Tag"
$ReleaseTitle = "Trace $Tag / 词迹 $Tag"
$ReleaseNotes = Join-Path $ProjectRoot "docs\releases\$Tag.md"
$ReleaseApk = Join-Path $ProjectRoot "$ArtifactBase.apk"
$ChecksumFile = "$ReleaseApk.sha256"
$DownloadUrl = "https://github.com/$Repo/releases/download/$Tag/$ArtifactBase.apk"
$GradleFile = Join-Path $ProjectRoot "android\app\build.gradle"
$ReadmeFile = Join-Path $ProjectRoot "README.md"
$ChangelogFile = Join-Path $ProjectRoot "CHANGELOG.md"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

Set-Location -LiteralPath $ProjectRoot

function Wait-TraceWorkflow {
    param(
        [Parameter(Mandatory = $true)][string]$Workflow,
        [Parameter(Mandatory = $true)][string]$Commit,
        [string]$Event = ""
    )

    $RunId = $null
    for ($Attempt = 0; $Attempt -lt 24 -and -not $RunId; $Attempt++) {
        $Arguments = @(
            "run", "list",
            "--repo", $Repo,
            "--workflow", $Workflow,
            "--commit", $Commit,
            "--limit", "1",
            "--json", "databaseId",
            "--jq", ".[0].databaseId"
        )
        if ($Event) {
            $Arguments += @("--event", $Event)
        }

        $Candidate = & gh @Arguments
        if ($Candidate) {
            $RunId = $Candidate.Trim()
        } else {
            Start-Sleep -Seconds 5
        }
    }

    if (-not $RunId) {
        throw "Workflow run was not found: $Workflow / 未找到工作流：$Workflow"
    }

    gh run watch $RunId --repo $Repo --exit-status
    if ($LASTEXITCODE -ne 0) {
        throw "Workflow failed: $Workflow / 工作流失败：$Workflow"
    }
    return $RunId
}

Write-Host "Release / 发布版本: $Tag"
Write-Host "APK: $ReleaseApk"
Write-Host "Notes / 发布说明: $ReleaseNotes"
```

Run this complete block once at the beginning of every new PowerShell session. Do not replace `$Version`, `$Tag`, or the other variables with literal version numbers in later commands.

每次新开 PowerShell 后先完整运行一次此代码块。后面的命令不要再把 `$Version`、`$Tag` 等变量替换成写死的版本号。

## 1. Check command-line tools and authentication / 检查命令行工具与认证

```powershell
git --version
gh --version
java -version
gh auth status
```

This computer is already authenticated when `gh auth status` succeeds. Make Git use the same GitHub CLI credentials:

如果 `gh auth status` 成功，表示本机已经完成认证。让 Git 复用 GitHub CLI 的凭据：

```powershell
gh auth setup-git
gh repo set-default $Repo
```

If authentication is missing, provide an existing GitHub token without putting it in command history:

如果尚未认证，可在终端中安全输入已有的 GitHub Token，不把 Token 留在命令历史中：

```powershell
$SecureToken = Read-Host "GitHub token" -AsSecureString
$TokenPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureToken)
try {
    $env:GH_TOKEN = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($TokenPointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($TokenPointer)
}
gh auth status
```

The token needs repository and workflow permissions. `GH_TOKEN` is limited to the current terminal session.

Token 需要仓库和工作流权限。`GH_TOKEN` 只在当前终端会话中有效。

Check the repository and remote:

检查仓库和远程地址：

```powershell
Set-Location -LiteralPath $ProjectRoot
git status
git branch --show-current
git remote -v

$ExpectedRemote = "https://github.com/$($Repo).git"
git remote get-url origin 2>$null
if ($LASTEXITCODE -eq 0) {
    git remote set-url origin $ExpectedRemote
} else {
    git remote add origin $ExpectedRemote
}

if ((git branch --show-current) -ne $Branch) {
    throw "Switch to $Branch before releasing / 发布前请切换到 $Branch"
}
```

Configure the identity once if needed:

如未配置提交身份，只需执行一次：

```powershell
git config --global user.name "YOUR_GITHUB_NAME"
git config --global user.email "YOUR_GITHUB_EMAIL"
```

Ensure GitHub Pages is configured for the Actions workflow, also without using the website:

使用命令行确认 GitHub Pages 已配置为 Actions 工作流：

```powershell
gh api "repos/$Repo/pages" --silent 2>$null
if ($LASTEXITCODE -ne 0) {
    gh api -X POST "repos/$Repo/pages" -f build_type=workflow
}
```

## 2. Apply the single version value to Android / 把唯一版本号写入 Android

This idempotent block updates `versionName` and increases `versionCode` once when the version changes. Running it again with the same version does not increase `versionCode` again.

此代码块会更新 `versionName`，并在版本发生变化时把 `versionCode` 增加一次。相同版本重复运行不会重复增加。

```powershell
$GradleText = [IO.File]::ReadAllText($GradleFile)
$VersionMatch = [regex]::Match($GradleText, 'versionName\s+"([^"]+)"')
$CodeMatch = [regex]::Match($GradleText, 'versionCode\s+(\d+)')

if (-not $VersionMatch.Success -or -not $CodeMatch.Success) {
    throw "Cannot read versionName or versionCode from $GradleFile"
}

$CurrentVersion = $VersionMatch.Groups[1].Value
$CurrentVersionCode = [int]$CodeMatch.Groups[1].Value

if ($CurrentVersion -ne $Version) {
    $NextVersionCode = $CurrentVersionCode + 1
    $GradleText = ([regex]'versionName\s+"[^"]+"').Replace(
        $GradleText,
        "versionName `"$Version`"",
        1
    )
    $GradleText = ([regex]'versionCode\s+\d+').Replace(
        $GradleText,
        "versionCode $NextVersionCode",
        1
    )
    [IO.File]::WriteAllText($GradleFile, $GradleText, $Utf8NoBom)
    Write-Host "Updated Android to $Tag, versionCode $NextVersionCode"
} else {
    Write-Host "Android already uses $Tag, versionCode $CurrentVersionCode"
}

Select-String -Path $GradleFile -Pattern 'versionCode|versionName'
```

Update every versioned APK download link in the README from the same variable:

用同一个变量自动更新 README 中所有带版本号的 APK 下载链接：

```powershell
$ReadmeText = [IO.File]::ReadAllText($ReadmeFile)
$DownloadPrefix = [regex]::Escape("https://github.com/$Repo/releases/download/")
$DownloadPattern = $DownloadPrefix + 'v[^/]+/Trace-v[^)\s"]+\.apk'
$UpdatedReadme = [regex]::Replace($ReadmeText, $DownloadPattern, $DownloadUrl)

if ($UpdatedReadme -ne $ReadmeText) {
    [IO.File]::WriteAllText($ReadmeFile, $UpdatedReadme, $Utf8NoBom)
}

Select-String -Path $ReadmeFile -Pattern ([regex]::Escape($DownloadUrl))
```

## 3. Test and build a signed release APK / 检查并构建签名版 APK

Load the installed Java and Android SDK paths into the current terminal:

把已安装的 Java 和 Android SDK 路径加载到当前终端：

```powershell
if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
}
if (-not $env:ANDROID_HOME) {
    $env:ANDROID_HOME = [Environment]::GetEnvironmentVariable("ANDROID_HOME", "User")
}
$env:JAVA_HOME = $env:JAVA_HOME.TrimEnd("\")
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:Path"

if (-not (Test-Path -LiteralPath "$env:JAVA_HOME\bin\java.exe")) {
    throw "Java is missing / 未找到 Java"
}
if (-not (Test-Path -LiteralPath "$env:ANDROID_HOME\build-tools")) {
    throw "Android Build Tools are missing / 未找到 Android Build Tools"
}
```

Run Lint and build the unsigned release package:

执行 Lint 并构建未签名的 Release 包：

```powershell
Set-Location -LiteralPath (Join-Path $ProjectRoot "android")
.\gradlew.bat lintDebug assembleRelease --no-daemon
if ($LASTEXITCODE -ne 0) {
    throw "Android build failed / Android 构建失败"
}
Set-Location -LiteralPath $ProjectRoot
```

Create the private signing key once. All prompts remain in the terminal. Store this file safely; losing it prevents future APK updates from using the same signature.

首次发布时创建私有签名密钥，所有信息均在终端输入。请妥善备份；丢失后将无法用相同签名继续更新 APK。

```powershell
$KeystoreDirectory = Split-Path -Parent $Keystore
New-Item -ItemType Directory -Force -Path $KeystoreDirectory | Out-Null

if (-not (Test-Path -LiteralPath $Keystore)) {
    & "$env:JAVA_HOME\bin\keytool.exe" `
        -genkeypair `
        -v `
        -keystore $Keystore `
        -alias $SigningAlias `
        -keyalg RSA `
        -keysize 4096 `
        -validity 10000
    if ($LASTEXITCODE -ne 0) {
        throw "Keystore creation failed / 签名密钥创建失败"
    }
}
```

Align, sign, verify, and checksum the APK entirely from PowerShell:

全程使用 PowerShell 对 APK 进行对齐、签名、验证和校验：

```powershell
$BuildTools = Get-ChildItem -LiteralPath "$env:ANDROID_HOME\build-tools" -Directory |
    Where-Object { $_.Name -match '^\d+(\.\d+){1,2}$' } |
    Sort-Object { [version]$_.Name } -Descending |
    Select-Object -First 1

if (-not $BuildTools) {
    throw "No Android Build Tools version found / 未找到 Android Build Tools"
}

$ZipAlign = Join-Path $BuildTools.FullName "zipalign.exe"
$ApkSigner = Join-Path $BuildTools.FullName "apksigner.bat"
$UnsignedApk = Join-Path $ProjectRoot "android\app\build\outputs\apk\release\app-release-unsigned.apk"
$ToolingDirectory = Join-Path $ProjectRoot ".android-tooling"
$AlignedApk = Join-Path $ToolingDirectory "$ArtifactBase-aligned.apk"

New-Item -ItemType Directory -Force -Path $ToolingDirectory | Out-Null
if (Test-Path -LiteralPath $AlignedApk) {
    Remove-Item -LiteralPath $AlignedApk -Force
}
if (Test-Path -LiteralPath $ReleaseApk) {
    Remove-Item -LiteralPath $ReleaseApk -Force
}

& $ZipAlign -f -P 16 4 $UnsignedApk $AlignedApk
if ($LASTEXITCODE -ne 0) {
    throw "zipalign failed"
}

& $ApkSigner sign `
    --ks $Keystore `
    --ks-key-alias $SigningAlias `
    --v4-signing-enabled false `
    --out $ReleaseApk `
    $AlignedApk
if ($LASTEXITCODE -ne 0) {
    throw "APK signing failed / APK 签名失败"
}

& $ApkSigner verify --verbose --print-certs $ReleaseApk
if ($LASTEXITCODE -ne 0) {
    throw "APK signature verification failed / APK 签名验证失败"
}

& $ZipAlign -c -P 16 4 $ReleaseApk
if ($LASTEXITCODE -ne 0) {
    throw "APK alignment verification failed / APK 对齐验证失败"
}

$ApkHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $ReleaseApk).Hash.ToLowerInvariant()
"$ApkHash  $ArtifactBase.apk" |
    Set-Content -LiteralPath $ChecksumFile -Encoding Ascii

Get-Item -LiteralPath $ReleaseApk
Get-Content -LiteralPath $ChecksumFile
```

Never add the keystore, its password, an APK, a token, or an `.env` file to Git.

切勿把签名密钥、密码、APK、Token 或 `.env` 文件加入 Git。

## 4. Generate release notes and changelog entries / 生成发布说明与更新日志

Enter one English summary and one Chinese summary in the terminal. They do not contain a hard-coded version.

在终端输入一条英文摘要和一条中文摘要，不需要手写版本号。

````powershell
$EnglishSummary = Read-Host "English release summary"
$ChineseSummary = Read-Host "中文版本摘要"

if (-not (Test-Path -LiteralPath $ReleaseNotes)) {
    $ReleaseText = @"
# Trace $Tag / 词迹 $Tag

$EnglishSummary

## Installation

- Install the attached signed APK.
- Existing settings are preserved when upgrading from an APK signed with the same key.

## Download

```text
$ArtifactBase.apk
SHA-256: $ApkHash
```

---

$ChineseSummary

## 安装说明

- 安装随 Release 附带的签名 APK。
- 使用相同密钥签名的 APK 可覆盖升级，并保留原有设置。

## 下载

```text
$ArtifactBase.apk
SHA-256: $ApkHash
```
"@
    [IO.File]::WriteAllText($ReleaseNotes, $ReleaseText, $Utf8NoBom)
} else {
    $ReleaseText = [IO.File]::ReadAllText($ReleaseNotes)
    $ReleaseText = [regex]::Replace(
        $ReleaseText,
        '(?m)^SHA-256:.*$',
        "SHA-256: $ApkHash"
    )
    [IO.File]::WriteAllText($ReleaseNotes, $ReleaseText, $Utf8NoBom)
}

$ChangelogText = [IO.File]::ReadAllText($ChangelogFile)
$EscapedVersion = [regex]::Escape($Version)

if ($ChangelogText -notmatch "(?m)^## \[$EscapedVersion\]") {
    $Date = Get-Date -Format "yyyy-MM-dd"
    $Entry = @"
## [$Version] - $Date

### Changed

- $EnglishSummary

### 改进

- $ChineseSummary

"@
    $FirstRelease = [regex]::Match($ChangelogText, '(?m)^## \[')
    if (-not $FirstRelease.Success) {
        throw "Cannot find the first release in CHANGELOG.md"
    }
    $ChangelogText = $ChangelogText.Insert($FirstRelease.Index, $Entry)
}

$LinkDefinition = "[$Version]: https://github.com/$Repo/releases/tag/$Tag"
if ($ChangelogText -notmatch "(?m)^\[$EscapedVersion\]:") {
    $ChangelogText = $ChangelogText.TrimEnd() + "`r`n" + $LinkDefinition + "`r`n"
}
[IO.File]::WriteAllText($ChangelogFile, $ChangelogText, $Utf8NoBom)

Get-Content -Encoding UTF8 -LiteralPath $ReleaseNotes
Select-String -Path $ChangelogFile -Pattern ([regex]::Escape("[$Version]"))
````

For detailed notes, assign multiline here-strings to `$EnglishSummary` and `$ChineseSummary` instead of `Read-Host`, then rerun this section before committing.

如需更详细的说明，可在提交前使用多行 here-string 给 `$EnglishSummary` 和 `$ChineseSummary` 赋值，再执行本节；仍然不需要重复填写版本号。

## 5. Review, commit, and push / 检查、提交并推送

```powershell
Set-Location -LiteralPath $ProjectRoot
git diff --check
git diff --stat
git status --short

if (-not (git status --porcelain)) {
    throw "There are no changes to commit / 没有需要提交的更改"
}

git add -A

$SensitiveFiles = git diff --cached --name-only |
    Select-String -Pattern '\.(jks|keystore|apk)$|(^|/)\.env$'
if ($SensitiveFiles) {
    $SensitiveFiles
    throw "Sensitive or binary release files are staged / 暂存区中存在敏感文件或 APK"
}

git status --short
git commit -m "Release $Tag"
if ($LASTEXITCODE -ne 0) {
    throw "Commit failed / 提交失败"
}

git push origin $Branch
if ($LASTEXITCODE -ne 0) {
    Write-Host "The remote may contain newer commits. Run:"
    Write-Host "git pull --rebase origin $Branch"
    Write-Host "git push origin $Branch"
    throw "Push failed; do not force-push / 推送失败，请勿强制推送"
}
```

Do not use force-push for a normal release.

正常发布不要使用强制推送。

## 6. Create and push the tag / 创建并推送标签

This block is safe to rerun. It refuses to reuse a local tag that points to another commit.

此代码块可以重复运行；如果同名本地标签指向其他提交，它会拒绝继续。

```powershell
$HeadCommit = git rev-parse HEAD
git show-ref --verify --quiet "refs/tags/$Tag"

if ($LASTEXITCODE -eq 0) {
    $TagCommit = git rev-list -n 1 $Tag
    if ($TagCommit -ne $HeadCommit) {
        throw "$Tag points to $TagCommit instead of $HeadCommit"
    }
} else {
    git tag -a $Tag -m "Trace $Tag"
}

git push origin $Tag
if ($LASTEXITCODE -ne 0) {
    throw "Tag push failed / 标签推送失败"
}
```

## 7. Watch GitHub Actions and download artifacts / 等待工作流并下载产物

The `main` push starts Pages, and the tag push starts the Android debug build. Both are monitored from the terminal:

推送 `main` 会启动 Pages，推送标签会启动 Android 调试包构建。两者都在终端中等待和检查：

```powershell
$HeadCommit = git rev-parse HEAD
$PagesRunId = Wait-TraceWorkflow `
    -Workflow "pages.yml" `
    -Commit $HeadCommit `
    -Event "push"

$AndroidRunId = Wait-TraceWorkflow `
    -Workflow "android-build.yml" `
    -Commit $HeadCommit `
    -Event "push"

$ArtifactName = "$ArtifactBase-debug"
$ArtifactDirectory = Join-Path $ProjectRoot "artifacts\$Tag"
New-Item -ItemType Directory -Force -Path $ArtifactDirectory | Out-Null

gh run download $AndroidRunId `
    --repo $Repo `
    --name $ArtifactName `
    --dir $ArtifactDirectory

Get-ChildItem -LiteralPath $ArtifactDirectory
```

The downloaded workflow APK uses a debug signature and is intended for testing. Publish the privately signed APK from section 3 as the permanent Release asset.

工作流下载的 APK 使用调试签名，只用于测试。正式 Release 应上传第 3 节使用私人密钥签名的 APK。

If local Android tools are unavailable on another computer, trigger the debug build from the command line:

如果以后在另一台没有 Android 本地环境的电脑上操作，可用命令行手动触发调试构建：

```powershell
gh workflow run "android-build.yml" --repo $Repo --ref $Branch
$RemoteCommit = gh api "repos/$Repo/commits/$Branch" --jq ".sha"
$ManualRunId = Wait-TraceWorkflow `
    -Workflow "android-build.yml" `
    -Commit $RemoteCommit `
    -Event "workflow_dispatch"
```

## 8. Create or update the GitHub Release / 创建或更新 GitHub Release

This command publishes the release notes, signed APK, and checksum. If the release already exists, it updates the text and replaces both assets.

以下命令会发布说明、签名 APK 和校验文件。如果 Release 已存在，则更新说明并替换两个附件。

```powershell
gh release view $Tag --repo $Repo *> $null

if ($LASTEXITCODE -eq 0) {
    gh release upload $Tag `
        $ReleaseApk `
        $ChecksumFile `
        --repo $Repo `
        --clobber
    if ($LASTEXITCODE -ne 0) {
        throw "Release asset upload failed / Release 附件上传失败"
    }

    gh release edit $Tag `
        --repo $Repo `
        --title $ReleaseTitle `
        --notes-file $ReleaseNotes `
        --latest
} else {
    gh release create $Tag `
        $ReleaseApk `
        $ChecksumFile `
        --repo $Repo `
        --verify-tag `
        --title $ReleaseTitle `
        --notes-file $ReleaseNotes `
        --latest
}

if ($LASTEXITCODE -ne 0) {
    throw "Release publishing failed / Release 发布失败"
}
```

Verify the published release and download the asset again without opening a browser:

不用浏览器，直接在终端验证 Release 并重新下载附件：

```powershell
gh release view $Tag `
    --repo $Repo `
    --json tagName,name,isDraft,isPrerelease,url,assets `
    --jq '{tag:.tagName,name:.name,draft:.isDraft,prerelease:.isPrerelease,url:.url,assets:[.assets[].name]}'

$VerificationDirectory = Join-Path `
    $env:TEMP `
    "trace-release-$Version-$(Get-Date -Format yyyyMMddHHmmss)"
New-Item -ItemType Directory -Force -Path $VerificationDirectory | Out-Null

gh release download $Tag `
    --repo $Repo `
    --pattern "$ArtifactBase.apk" `
    --dir $VerificationDirectory

$DownloadedApk = Join-Path $VerificationDirectory "$ArtifactBase.apk"
$DownloadedHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $DownloadedApk).Hash.ToLowerInvariant()

if ($DownloadedHash -ne $ApkHash) {
    throw "Downloaded APK checksum mismatch / 下载后的 APK 校验值不一致"
}
Write-Host "Release APK verified / Release APK 验证通过: $DownloadedHash"
```

## 9. Update and verify repository About / 更新并验证仓库 About

```powershell
$Description = "Offline Chinese idiom, English word, and arithmetic-equation solvers for web and Android, with English, 简体中文, 繁體中文, light mode, and dark mode. / 离线成语、英语单词与四则运算算式筛选工具，支持网页、Android、三语界面与深浅色模式。"

gh repo edit $Repo `
    --description $Description `
    --homepage $PagesUrl `
    --add-topic "android,offline,wordle,nerdle,chinese-idioms,word-game,math-puzzle,webview"

if ($LASTEXITCODE -ne 0) {
    throw "Repository metadata update failed / 仓库信息更新失败"
}

gh repo view $Repo `
    --json description,homepageUrl,repositoryTopics `
    --jq '{description:.description,homepage:.homepageUrl,topics:[.repositoryTopics[].name]}'
```

## 10. Verify GitHub Pages and finish / 验证 GitHub Pages 并完成发布

```powershell
gh api "repos/$Repo/pages" `
    --jq '{url:.html_url,build_type:.build_type,status:.status}'

$PagesResponse = Invoke-WebRequest -Uri $PagesUrl -UseBasicParsing
if ($PagesResponse.StatusCode -ne 200) {
    throw "GitHub Pages returned HTTP $($PagesResponse.StatusCode)"
}
if ($PagesResponse.Content -notmatch '<title>Trace</title>') {
    throw "GitHub Pages content is unexpected / GitHub Pages 内容不正确"
}

git status --short
git log -1 --oneline
git tag --points-at HEAD
gh release view $Tag --repo $Repo --json url --jq ".url"

Write-Host "Release complete / 发布完成: $Tag"
Write-Host "APK: $DownloadUrl"
Write-Host "Pages: $PagesUrl"
```

Expected final state:

预期最终状态：

- `git status --short` prints nothing.
- `git tag --points-at HEAD` includes `$Tag`.
- Both GitHub Actions workflows succeed.
- The GitHub Release contains `$ArtifactBase.apk` and its checksum file.
- The downloaded APK SHA-256 equals `$ApkHash`.
- GitHub Pages returns HTTP 200.

- `git status --short` 没有输出。
- `git tag --points-at HEAD` 包含 `$Tag`。
- 两个 GitHub Actions 工作流均成功。
- GitHub Release 包含 `$ArtifactBase.apk` 和校验文件。
- 重新下载的 APK SHA-256 与 `$ApkHash` 一致。
- GitHub Pages 返回 HTTP 200。
