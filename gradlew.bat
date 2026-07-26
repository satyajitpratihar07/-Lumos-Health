@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem  Gradle startup script for Windows
@rem ##########################################################################

if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

if exist "S:\Andriod studio\jbr\bin\java.exe" set "JAVA_HOME=S:\Andriod studio\jbr"
if exist "C:\Program Files\Android\Android Studio\jbr\bin\java.exe" set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
if exist "C:\Program Files\Android\Android Studio\jre\bin\java.exe" set "JAVA_HOME=C:\Program Files\Android\Android Studio\jre"

if defined JAVA_HOME goto checkJavaHome
goto trySystemJava

:checkJavaHome
set "JAVA_EXE=%JAVA_HOME%/bin/java.exe"
if exist "%JAVA_EXE%" goto execute

:trySystemJava
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo ERROR: Could not find Java JDK. Please set JAVA_HOME to your Java installation directory.
goto fail

:execute
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
exit /b %ERRORLEVEL%

:mainEnd
if "%OS%"=="Windows_NT" endlocal
