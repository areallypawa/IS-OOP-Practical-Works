
$ErrorActionPreference = "Stop"

$sourceFiles = Get-ChildItem `
    -Path "src/main" `
    -Filter "*.java" `
    -Recurse |
    ForEach-Object { $_.FullName }

if (-not $sourceFiles) {
    Write-Host "Исходники Java не найдены."
    exit 1
}

New-Item -ItemType Directory -Force -Path "out" |
    Out-Null

Write-Host "`nКомпиляция игры..."

& javac -d out $sourceFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Ошибка компиляции."
    exit $LASTEXITCODE
}

Write-Host "`nЗапуск игры...`n"

& java -cp out Main
