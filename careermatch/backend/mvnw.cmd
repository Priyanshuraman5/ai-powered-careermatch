@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one or more
@REM contributor license agreements. See the NOTICE file distributed with this
@REM work for additional information regarding copyright ownership. The ASF
@REM licenses this file to you under the Apache License, Version 2.0.
@REM ----------------------------------------------------------------------------
@REM Standard Maven Wrapper launcher script (Windows).
@REM Requires network access to repo.maven.apache.org on first run to fetch
@REM the wrapper jar defined in .mvn\wrapper\maven-wrapper.properties.
@REM ----------------------------------------------------------------------------
@ECHO OFF

SET WRAPPER_DIR=%~dp0
SET WRAPPER_JAR=%WRAPPER_DIR%.mvn\wrapper\maven-wrapper.jar

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO maven-wrapper.jar not found.
  ECHO This sandbox had no network access to download it. On a machine with
  ECHO internet access, run:  mvn -N io.takari:maven:wrapper -Dmaven=3.9.9
  ECHO or install Maven directly and run 'mvn spring-boot:run' instead.
  EXIT /B 1
)

IF DEFINED JAVA_HOME (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_EXE=java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%WRAPPER_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
