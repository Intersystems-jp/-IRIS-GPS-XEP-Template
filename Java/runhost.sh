#!/bin/sh
source ./host-java-params.sh

java -jar ${PWD}/sample.jar $1

echo "\nStreetViewの向きを前向きにするための処理を実行します（IRIS内ルーチンを実行します）\n"
docker-compose exec -f ../docker-compose.yml iris iris session iris enrich

echo "----------------------"
echo "** 処理終了しました **"
echo "----------------------"

