@echo off
if not exist bin\practical\Main.class (
    echo [INFO] Binaries not found. Running build.bat first...
    call build.bat
)
java -cp bin practical.Main
pause
