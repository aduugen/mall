@echo off
setlocal

REM Function to run a command in a new command prompt window
:run_in_new_window
REM %* is used to pass all arguments to the function
start cmd /k "%~1"
goto :eof

REM Run the first docker-compose command in a new window
set CMD1=docker-compose -f docker-compose-env.yml up
echo Running: %CMD1%
call :run_in_new_window "%CMD1%"

REM Wait for 3 minutes (180 seconds)
timeout /t 180 /nobreak >nul

REM Run the second docker-compose command in a new window
set CMD2=docker-compose -f docker-compose-app.yml up
echo Running: %CMD2%
call :run_in_new_window "%CMD2%"

endlocal
exit /b