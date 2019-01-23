@echo off

cls
color 07

:CONFIG
rem echo.
rem echo Config...
call config.bat %1 %2

if "%1"=="src" goto COMPILE

:CLEAN_DIR
echo.
echo Clear folder...
call clear.bat %1 %2


:CREATE_DIR
echo.
echo Create folder...
if not exist %DIR_RELEASE% md %DIR_RELEASE%
if not exist %DIR_RELEASE_LOG% md %DIR_RELEASE_LOG%
if not exist %DIR_DATABASE% md %DIR_DATABASE%

if ERRORLEVEL 1 goto DATA_ERROR
if "%1"=="data" goto COPY_DATA

:COPY_DATA
if "%1"=="data" (
	if exist %DIR_RELEASE% rd /s /q %DIR_RELEASE%\data
)
echo.
echo Copy data files...
set PLATFORM_DIR=_LIVE
if "%2" == "" (
    set PLATFORM_DIR=_LOCALHOST
)
REM copy %DIR_DATA%\log\%PLATFORM_DIR% %DIR_DATA%\config /Y >nul
xcopy %DIR_DATA%\config %DIR_RELEASE%\data\config\ /Y /S /E >nul
REM xcopy %DIR_DATA%\xls %DIR_RELEASE%\data\xls\ /Y /S /E >nul
xcopy %DIR_DATA%\json %DIR_RELEASE%\data\json\ /Y /S /E >nul
xcopy %DIR_DATA%\geoip %DIR_RELEASE%\data\geoip\ /Y /S /E >nul
if "%1"=="data" goto END


:COMPILE
echo.
echo Compile source...
cd %DIR_TEMP_SRC%
dir /s /b %DIR_SRC%\*.java>%DIR_TEMP_SRC%\listjava.txt
%JAVAC% -encoding UTF-8 -Xlint:none -classpath %LIBRARY% -d "%DIR_TEMP_CLASS%" @listjava.txt
if ERRORLEVEL 1 goto ERROR


:MANIFEST
echo.
echo Create MANIFEST.MF...
cd %DIR_TEMP%

echo Manifest-Version: 1.0 > MANIFEST.MF
echo Build-Version: %BUILD_VERSION%>>MANIFEST.MF
echo Build-Time: %date% %time%>>MANIFEST.MF
echo Build-User: %username%>>MANIFEST.MF
echo Build-Computer: %COMPUTERNAME%>>MANIFEST.MF
echo Main-Class: %MAIN_CLASS%>>MANIFEST.MF

:JAR
echo.
echo Make jar...
cd %DIR_TEMP_CLASS%
%JAR% -cmf ..\MANIFEST.MF %DIR_RELEASE%\%JAR_NAME%.jar .
goto END

:DATA_ERROR
del /f /q %DIR_TEMP%\*.*
goto ERROR

:ERROR
color 4E
echo *************** HAVE ERROR ***************
pause
color 07

:END
echo.
echo Done.
cd %PRJ_HOME%