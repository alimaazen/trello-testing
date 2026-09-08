@echo off
REM ====================================================================
REM Trello Selenium Automation - Environment Setup Script for Windows
REM ====================================================================
REM This script helps you set up environment variables for your Trello
REM test credentials. These will be stored in your USER environment
REM variables (persistent across sessions).
REM ====================================================================

echo.
echo ========================================================
echo  Trello Automation - Environment Variables Setup
echo ========================================================
echo.
echo This script will set up your Trello credentials as
echo USER environment variables (persistent).
echo.
echo These variables will be available in:
echo   - Current session (after restart)
echo   - All future PowerShell/CMD sessions
echo   - Your IDE (IntelliJ, Eclipse, VS Code, etc.)
echo.
echo ========================================================
echo.

REM Prompt for Trello email
set /p TRELLO_EMAIL="Enter your Trello test account email: "

REM Prompt for Trello password
set /p TRELLO_PASSWORD="Enter your Trello test account password: "

echo.
echo Setting environment variables...
echo.

REM Set USER environment variables (persistent)
setx TRELLO_EMAIL "%TRELLO_EMAIL%"
setx TRELLO_PASSWORD "%TRELLO_PASSWORD%"

echo.
echo ========================================================
echo  Environment variables set successfully!
echo ========================================================
echo.
echo Variables set:
echo   TRELLO_EMAIL = %TRELLO_EMAIL%
echo   TRELLO_PASSWORD = ********
echo.
echo IMPORTANT NEXT STEPS:
echo   1. RESTART your IDE (IntelliJ, Eclipse, VS Code, etc.)
echo   2. RESTART any open terminal/command prompt windows
echo   3. These variables are now available in new sessions
echo.
echo To verify, open a NEW command prompt and run:
echo   echo %%TRELLO_EMAIL%%
echo.
echo ========================================================
echo.

pause
