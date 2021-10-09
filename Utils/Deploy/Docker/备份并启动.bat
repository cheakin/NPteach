@echo off
set filename=app

REM 备份文件
if exist %filename%.jar (
	md history
	move .\%filename%.jar  .\history\%filename%_%date:~0,4%%date:~5,2%%date:~8,2%.jar
	@echo %filename%.jar is backups
)

REM 将jar包重命名
for %%i in (*.jar) do (
	ren "%%i" "%filename%".jar
)

REM 启动
if not exist %filename%.jar (
	echo %filename%.jar is not exist!
	pause
) else (
	docker compose up --build -d
	cmd /k
)


