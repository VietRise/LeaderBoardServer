@echo off
cls
color 07

cd ..

:CONFIG
echo.
echo Config...
call config.bat

set ENVIRONMENT=LOCAL

if "%1" == "game" goto CHOOSE_GAME
if "%1" == "redis" goto CHOOSE_REDIS

:CHOOSE_GAME
set RUNNING_MODE=GAME
goto RUN

:CHOOSE_REDIS
start /B %DIR_REDIS%\redis-server.exe --port 6380
goto END

:RUN
set _CMD_=%JAVA% -cp %JAR_NAME%.jar;%LIBRARY% %MAIN_CLASS% %ENVIRONMENT% %RUNNING_MODE%
echo.
echo Run...	
echo.
cd %DIR_RELEASE%
echo %_CMD_%
call %_CMD_%
cd %PRJ_HOME%

goto END

:ERROR
color 4E
echo *************** HAVE ERROR ***************
pause
color 07

:END
echo.
echo Done.
cd %PRJ_HOME%

:FINISH
