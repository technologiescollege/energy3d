@echo off
REM Bundle portable Windows : exe + app/ + runtime/ (JRE inclus). Aucun Java requis sur la machine cible.
REM Prérequis : JDK 8 (pour compiler), Launch4j installe (https://launch4j.sourceforge.net/)

set "JAVA_HOME_JDK8=C:\Program Files\Java\jdk1.8.0_45"
if not exist "%JAVA_HOME_JDK8%\bin\javac.exe" (
    echo Erreur: JDK 8 introuvable dans %JAVA_HOME_JDK8%
    echo Adaptez JAVA_HOME_JDK8 dans ce fichier.
    pause
    exit /b 1
)

cd /d "%~dp0"

set "JAVA_HOME=%JAVA_HOME_JDK8%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Compilation et bundle portable (Launch4j + JRE embarque)...
call ant -DJAVA_HOME="%JAVA_HOME_JDK8%" deploy-portable
if errorlevel 1 (
    echo Echec. Verifiez que Launch4j est installe (build.xml : launch4j.exe).
    pause
    exit /b 1
)

echo.
echo Termine. Bundle portable dans : dist\Energy3D_portable\
echo Lancez dist\Energy3D_portable\Energy3D.exe (aucun Java requis sur la machine).
pause
