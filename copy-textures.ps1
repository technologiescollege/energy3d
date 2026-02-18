# Copie grass.jpg et boxTree.jpg depuis Sweet Home 3D vers Energy3D (textures fondation + murs)
# Source: Sweet Home 3D textures
# Destinations: Energy3D scene/images (textures 3D) et gui/icons (icones menu)

$ErrorActionPreference = "Stop"
$sourceDir = "Z:\SweetEnergy3D\SweetHome3D-7.5-src\src\com\eteks\sweethome3d\io\resources\textures"

# Cibles relatives au dossier energy3d (ou au script)
$energy3dRoot = if ($PSScriptRoot) { $PSScriptRoot } else { "Z:\SweetEnergy3D\energy3d" }
$targetImagesDir = Join-Path $energy3dRoot "src\main\resources\org\concord\energy3d\scene\images"
$targetIconsDir  = Join-Path $energy3dRoot "src\main\resources\org\concord\energy3d\gui\icons"

# Verifier source
if (-not (Test-Path $sourceDir)) {
    Write-Host "ERREUR: Repertoire source introuvable: $sourceDir" -ForegroundColor Red
    exit 1
}

# Creer repertoires cible
foreach ($dir in $targetImagesDir, $targetIconsDir) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "Cree: $dir" -ForegroundColor Green
    }
}

Add-Type -AssemblyName System.Drawing

function SaveAsPng {
    param([string]$src, [string]$dest)
    $img = [System.Drawing.Image]::FromFile($src)
    $img.Save($dest, [System.Drawing.Imaging.ImageFormat]::Png)
    $img.Dispose()
}

function SaveAsPngSmall {
    param([string]$src, [string]$dest, [int]$size = 32)
    $img = [System.Drawing.Image]::FromFile($src)
    $thumb = New-Object System.Drawing.Bitmap($size, $size)
    $g = [System.Drawing.Graphics]::FromImage($thumb)
    $g.DrawImage($img, 0, 0, $size, $size)
    $g.Dispose()
    $img.Dispose()
    $thumb.Save($dest, [System.Drawing.Imaging.ImageFormat]::Png)
    $thumb.Dispose()
}

# --- Fondation: grass.jpg -> foundation_02.png ---
$grass = Join-Path $sourceDir "grass.jpg"
if (Test-Path $grass) {
    SaveAsPng -src $grass -dest (Join-Path $targetImagesDir "foundation_02.png")
    Write-Host "OK texture fondation: foundation_02.png (scene/images)" -ForegroundColor Green
    SaveAsPngSmall -src $grass -dest (Join-Path $targetIconsDir "foundation_02.png")
    Write-Host "OK icone fondation: foundation_02.png (gui/icons)" -ForegroundColor Green
}
if (-not (Test-Path $grass)) { Write-Host "ERREUR: $grass introuvable" -ForegroundColor Red }

# --- Murs: boxTree.jpg -> wall_08.png ---
$boxTree = Join-Path $sourceDir "boxTree.jpg"
if (Test-Path $boxTree) {
    SaveAsPng -src $boxTree -dest (Join-Path $targetImagesDir "wall_08.png")
    Write-Host "OK texture mur: wall_08.png (scene/images)" -ForegroundColor Green
    SaveAsPngSmall -src $boxTree -dest (Join-Path $targetIconsDir "wall_08.png")
    Write-Host "OK icone mur: wall_08.png (gui/icons)" -ForegroundColor Green
}
if (-not (Test-Path $boxTree)) { Write-Host "ERREUR: $boxTree introuvable" -ForegroundColor Red }

Write-Host ""
Write-Host "Termine. Textures: $targetImagesDir | Icones: $targetIconsDir" -ForegroundColor Cyan
