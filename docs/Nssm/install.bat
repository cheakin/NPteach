@echo off

REM 切换目录
%~d0
cd %~dp0

REM 备份
set stamp=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%
set "stamp=%stamp: =0%"
if exist app.jar (
	if not exist history (
		md history
	)
	move app.jar  history\app_%stamp%.jar
	@echo old jar is backup
)

REM 重命名
for %%i in (*.jar) do (
	ren %%i app.jar
	@echo new jar renamed app.jar
)

REM 启动
if not exist app.jar (
	exit
)
%cd%\nssm install app %cd%\jre\bin\java.exe 
%cd%\nssm set app AppParameters -jar app.jar --spring.profiles.active=dev
%cd%\nssm set app AppDirectory %cd%
%cd%\nssm set app AppStdout %cd%\app.log
%cd%\nssm set app AppStderr %cd%\error.log
%cd%\nssm set app AppStopMethodSkip 6
%cd%\nssm set app AppStopMethodConsole 1000
%cd%\nssm set app AppThrottle 5000
%cd%\nssm set app DisplayName app
%cd%\nssm set app start SERVICE_AUTO_START	
%cd%\nssm start app

echo start successful!
pause