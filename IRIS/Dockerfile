#イメージのタグはこちら（https://hub.docker.com/_/intersystems-iris-data-platform）でご確認ください
ARG IMAGE=containers.intersystems.com/intersystems/iris-community:2021.1.0.215.3
ARG IMAGE=store/intersystems/iris-community:2021.1.0.215.3
FROM $IMAGE

USER root

WORKDIR /opt/app
RUN chown ${ISC_PACKAGE_MGRUSER}:${ISC_PACKAGE_IRISGROUP} /opt/app
   
COPY  web .

RUN [ -f apikey.txt ] && \
	APIKEY=`cat apikey.txt` && sed -e "s/@@KEY@@/${APIKEY}/g" -e "s///" StreetView-noKey.html > StreetView.html && \
    cp StreetView.html ${ISC_PACKAGE_INSTALLDIR}/csp/user || \
	cp StreetView-noKey.html ${ISC_PACKAGE_INSTALLDIR}/csp/user/StreetView.html

USER ${ISC_PACKAGE_MGRUSER}

COPY  src src
COPY  iris.script /tmp/iris.script

# run iris and initial 
RUN iris start IRIS \
    && iris session IRIS < /tmp/iris.script \
    && iris stop IRIS quietly

