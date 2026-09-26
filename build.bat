@echo off
echo ===================================================
echo             TEXTHACK - BUILD SYSTEM
echo ===================================================
echo [1/2] Creating output directory (bin)...
if not exist bin mkdir bin

echo [2/2] Compiling Java source files...
javac -d bin -sourcepath src src/practical/*.java src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================
    echo [SUCCESS] Compilation successful!
    echo Run 'run.bat' or 'java -cp bin practical.Main' to start.
    echo ===================================================
) else (
    echo.
    echo ===================================================
    echo [ERROR] Compilation failed. Please check errors above.
    echo ===================================================
)
