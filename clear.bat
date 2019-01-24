@echo off
if "%DIR_TEMP%"=="" (
call config.bat %1 %2
)

if exist %DIR_TEMP% rd /s /q %DIR_TEMP%
if exist %DIR_RELEASE% rd /s /q %DIR_RELEASE%

REM if not "%1"=="source" (
	REM del /f /q %DIR_SRC_DEFINE%\*.xls.h
	REM del /f /q %DIR_DATA%\*.bin
REM )
