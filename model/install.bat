@echo off
setlocal enabledelayedexpansion

set ENV_NAME=reyo

if not exist "%CONDA_ROOT%\Scripts\conda.exe" (
    echo [ERROR] Conda not found at %CONDA_ROOT%
    echo Install Miniconda or correct the path in this script
    pause
    exit /b 1
)

call "%CONDA_ROOT%\Scripts\activate.bat" %CONDA_ROOT%

echo Checking existing environment
conda env list | find "%ENV_NAME%" >nul && (
    echo Removing existing environment
    conda env remove -n %ENV_NAME% --yes
    if errorlevel 1 (
        echo [ERROR] Failed to remove existing environment
        pause
        exit /b 1
    )
)

echo Creating environment
conda env create -n %ENV_NAME% -f environment.yml
if errorlevel 1 (
    echo [ERROR] Environment creation failed
    pause
    exit /b 1
)

call "%CONDA_ROOT%\Scripts\activate.bat" %ENV_NAME%
if errorlevel 1 (
    echo [ERROR] Activation failed
    pause
    exit /b 1
)

set PYTHON_PATH=%CONDA_ROOT%\envs\%ENV_NAME%\python.exe
if not exist "%PYTHON_PATH%" (
    echo [ERROR] Python not found at %PYTHON_PATH%
    pause
    exit /b 1
)

echo Verifying packages
"%PYTHON_PATH%" -c "
import sys, pkg_resources
deps = {'numpy':'1.23.5', 'tensorflow':'2.10.0'}
errors = []
for pkg,ver in deps.items():
    try:
        installed = pkg_resources.get_distribution(pkg).version
        if installed != ver:
            errors.append(f'{pkg} (need {ver}, got {installed})')
    except:
        errors.append(f'{pkg} missing')
if errors:
    print('VALIDATION FAILED!')
    print('\n'.join(errors))
    sys.exit(1)
print('All packages verified!')
"
if errorlevel 1 (
    echo [ERROR] Package verification failed
    pause
    exit /b 1
)

echo Testing TensorFlow
"%PYTHON_PATH%" -c "
import tensorflow as tf
print(f'TensorFlow {tf.__version__}')
print('GPU available:', bool(tf.config.list_physical_devices('GPU')))
"
if errorlevel 1 (
    echo [ERROR] TensorFlow test failed
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Environment %ENV_NAME% is ready!
echo Python path: %PYTHON_PATH%
echo.
pause