@echo off
setlocal enabledelayedexpansion

set ENV_NAME=reyo
set ENV_FILE=environment.yml

if not exist "%CONDA_ROOT%\Scripts\conda.exe" (
    echo [ERROR] Miniconda not found at: %CONDA_ROOT%
    echo Please install Miniconda or update the path in this script
    pause
    exit /b 1
)

echo Checking for %ENV_NAME% environment...
"%CONDA_ROOT%\Scripts\conda.exe" env list | find "%ENV_NAME%" >nul

if %ERRORLEVEL% equ 0 (
    echo Updating existing environment...
    call "%CONDA_ROOT%\Scripts\activate.bat" %ENV_NAME%
    "%CONDA_ROOT%\Scripts\conda.exe" env update -f "%ENV_FILE%" --prune
) else (
    echo Creating new environment...
    "%CONDA_ROOT%\Scripts\conda.exe" env create -f "%ENV_FILE%"
)

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to create/update environment
    pause
    exit /b 1
)