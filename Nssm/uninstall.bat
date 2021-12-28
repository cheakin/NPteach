@echo off

%~dp0\nssm stop app
%~dp0\nssm remove app

pause