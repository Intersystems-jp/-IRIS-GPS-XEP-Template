#FROM store/intersystems/iris-community:2021.1.0.215.3 as IRIS
FROM containers.intersystems.com/intersystems/irishealth-community:2022.3.0.555.0 as IRIS
FROM openjdk:8-jdk-alpine

LABEL maintainer="intersystems japan developer community"

# Section 1 - Taken from https://github.com/Zenika/alpine-maven

RUN apk add --update ca-certificates && rm -rf /var/cache/apk/* && \
  find /usr/share/ca-certificates/mozilla/ -name "*.crt" -exec keytool -import -trustcacerts \
  -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -noprompt \
  -file {} -alias {} \; && \
  keytool -list -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts --storepass changeit

ENV MAVEN_VERSION 3.6.0
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION /usr/lib/mvn

# End of Section 1

# Section 2 - For health check
RUN apk --update --no-cache add curl
# End of Section 2

# Section 3 - IRIS Specific

WORKDIR /irislib

# mvnローカルリポジトリにサンプルで使用するJarファイルをインストール
#===============================================================
# 注意：IRISのバージョンによりJDBC用JARのファイル名が異なります
# 2022/12/22時点で バージョン2022.2.0.368.0 と 2022.3.0.555.0 は intersystems-jdbc-3.6.1.jar、 2022.1.0.209.0 は intersystems-jdbc-3.3.1.jar
#===============================================================
COPY --from=IRIS /usr/irissys/dev/java/lib/1.8/*.jar /irislib/
RUN mvn install:install-file -Dfile=/irislib/intersystems-jdbc-3.6.1.jar \
-DgroupId=com.intersystems \
-DartifactId=intersystems-jdbc \
-Dversion=3.6.1 \
-Dpackaging=jar \
-DcreateChecksum=true && \
mvn install:install-file -Dfile=/irislib/intersystems-xep-3.2.1.jar \
-DgroupId=com.intersystems \
-DartifactId=intersystems-xep \
-Dversion=3.2.1 \
-Dpackaging=jar \
-DcreateChecksum=true

# End of Section 3

# Configuring make
RUN apk add --update alpine-sdk

# mavenプロジェクトを配置するディレクトリを作成
WORKDIR /usr/projects

# maven テンプレート作成
#RUN  mvn archetype:generate -DgroupId=JavaXEPSample -DartifactId=gps-xep-template \
# -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

# maven package に必要なファイルをコピー
COPY ./pom.xml .
COPY ./src/main/java/ ./src/main/java/

# ビルドで作成した jarファイルを操作しやすい名称に変更（sample.jar）
RUN mvn package && \
 cp /usr/projects/target/gps-xep-template-1.0-jar-with-dependencies.jar /usr/projects/sample.jar

COPY ./run.sh ./run1.sh

# Windows上でコンテナをビルドする場合の対応
RUN sed -e 's/\r//g' -e "s///" run1.sh > run.sh && \
 chmod 755 ./run.sh

ENTRYPOINT ["/usr/projects/run.sh"]