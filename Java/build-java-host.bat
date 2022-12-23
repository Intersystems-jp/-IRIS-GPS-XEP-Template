@echo off
bitsadmin /transfer myDownloadJob /download /priority normal https://github.com/iijimam/IRISModules/tree/master/java/intersystems-jdbc-3.6.1.jar %CD%/jar/intersystems-jdbc-3.6.1.jar
bitsadmin /transfer myDownloadJob /download /priority normal https://github.com/iijimam/IRISModules/tree/master/java/intersystems-xep-3.2.1.jar %CD%/jar/intersystems-xep-3.2.1.jar


echo -------------------------------------------------------
echo ** install IRIS jdbc jar files to maven local repogitory
echo -------------------------------------------------------
call mvn install:install-file -Dfile=jar/intersystems-jdbc-3.6.1.jar -DgroupId=com.intersystems -DartifactId=intersystems-jdbc -Dversion=3.6.1 -Dpackaging=jar -DgeneratePom=true

echo -------------------------------------------------------
echo ** install IRIS xep files to maven local repogitory
echo -------------------------------------------------------
call mvn install:install-file -Dfile=jar/intersystems-xep-3.2.1.jar -DgroupId=com.intersystems -DartifactId=intersystems-xep -Dversion=3.2.1 -Dpackaging=jar -DcreateChecksum=true

call mvn clean

echo -------------------------------------------------------
echo ** running mvn package
echo -------------------------------------------------------
call mvn package

echo -------------------------------------------------------
echo ** completed! **
echo -------------------------------------------------------
copy /B target\gps-xep-template-1.0-jar-with-dependencies.jar sample.jar

call ../params.bat
set iriscontainerchk1=""
set iriscontainerchk2=""
for /f "usebackq" %%t in (`docker container ls -q -f name^=%IRIS_CONTAINER%`) do set iriscontainerchk1=%%t
for /f "usebackq" %%t in (`docker container ls -q -a -f name^=%IRIS_CONTAINER%`) do set iriscontainerchk2=%%t

if %iriscontainerchk1%=="" if not %iriscontainerchk2%=="" (
    echo ***** Running IRIS container *****
    docker-compose -f ../docker-compose.yml start iris    
)

if %iriscontainerchk1%=="" if %iriscontainerchk2%=="" (
    echo ***** Running IRIS container *****
    docker-compose -f ../docker-compose.yml up -d iris 
)
docker-compose -f ../docker-compose.yml ps 


echo ----------------------------------------------------------------------------
echo ** [Example]
echo     runhost.bat GPXSamples/Sakurajima.gpx.xml
echo **
echo **
echo ** you need to update host name before running.
echo    1. open host-java-params.bat
echo    2. edit host name or ip address and save.
echo    3. running runhost.bat (please refer [Example])
echo ----------------------------------------------------------------------------
