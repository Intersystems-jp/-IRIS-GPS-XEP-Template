@echo off
call host-java-params.bat

java -jar %CD%/sample.jar %1

echo -----------------------------------------------------------------
echo  [Prepating for StreetView.html] running enrich routine on IRIS
echo -----------------------------------------------------------------

docker-compose exec -f ../docker-compose.yml iris iris session iris enrich

echo -----------------------
echo  ** completed !! **
echo -----------------------