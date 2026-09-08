@echo off
REM ====================================================================
REM Environment Variables Verification Script
REM ====================================================================
REM Run this in a NEW command prompt to verify your environment
REM variables are set correctly
REM ====================================================================

echo.
echo ========================================================
echo  Environment Variables Verification
echo ========================================================
echo.

if defined TRELLO_EMAIL (
    echo [OK] TRELLO_EMAIL is set
    echo      Value: %TRELLO_EMAIL%
) else (
    echo [FAIL] TRELLO_EMAIL is NOT set
    echo        Please run setup-env.bat
)

echo.

if defined TRELLO_PASSWORD (
    echo [OK] TRELLO_PASSWORD is set
    echo      Value: ******** ^(hidden for security^)
) else (
    echo [FAIL] TRELLO_PASSWORD is NOT set
    echo        Please run setup-env.bat
)

echo.
echo ========================================================

if defined TRELLO_EMAIL (
    if defined TRELLO_PASSWORD (
        echo.
        echo  Status: ALL GOOD! You're ready to run tests.
        echo.
        echo  Next steps:
        echo    1. Make sure IntelliJ IDEA is restarted
        echo    2. Open the project in IntelliJ
        echo    3. Run mvn clean install
        echo    4. Run LoginTests to verify everything works
        echo.
    ) else (
        echo.
        echo  Status: INCOMPLETE - Password not set
        echo.
    )
) else (
    echo.
    echo  Status: INCOMPLETE - Email not set
    echo.
)

echo ========================================================
echo.

pause
