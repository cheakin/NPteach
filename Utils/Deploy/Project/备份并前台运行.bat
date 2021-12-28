@echo off
set filename=app

REM 备份文件
if exist %filename%.jar (
	if not exist history ( md history )
	move .\%filename%.jar  .\history\%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%_%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%.jar
	echo %filename%.jar is backup!
)

REM 将jar包重命名
for %%i in (*.jar) do (
	ren "%%i" "%filename%".jar
	echo "%%i" renamed "%filename%".jar
)

REM 启动
if not exist %filename%.jar (
	echo %filename%.jar is not exist!
	pause	REM 按任意键退出
) else (
	cmd /k java -jar %filename%.jar --spring.profiles.active=test
)


