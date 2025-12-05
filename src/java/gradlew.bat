@echo off
set JAVA_EXE=java.exe
if defined JAVA_HOME set JAVA_EXE=%JAVA_HOME%\bin\java.exe
"%JAVA_EXE%" -jar gradle\wrapper\gradle-wrapper.jar %*
