# GPS（GPX）データをInterSystems IRISに高速に取り込む方法を体験できる実行環境テンプレート

このテンプレートでは、Java の実行環境用コンテナから InterSystems IRIS のコンテナへ接続し、GPS（[GPX](https://ja.wikipedia.org/wiki/GPX)）データを高速に取り込む処理をご体験いただけます。

**処理のイメージ**
![](https://github.com/iijimam/doc-images/blob/master/IRIS-GSP-XEP-Teamplate/GPX-xep-IRIS.png)

サンプルコードの中では、GPS データを直接受信するのではなく、Google マイマップやサンプル GPS データから GPX ファイルに変換したデータを入力に使用しています。


## 1) 処理概要
GPX データを IRIS へ渡す迄の流れにリアクティブプログラミングが行える RxJava2 のライブラリを使用しています。

実行時、複数（数十～千）の GPS データが含まれる GPX ファイルを引数に指定し情報を入力します。
XMLからリストを作成し、作成したリストを RxJava2 の [Flowable](http://reactivex.io/RxJava/javadoc/io/reactivex/Flowable.html) に渡し、データのフィルタリングを行い、IRIS へデータを渡しています。

>サンプルの GPX ファイルは、Google マイマップから作成しています。マイマップから作成した GPS には、残念ながら速度が含まれません。　弊社パートナーの[ビズベース様](https://www.big-advance.site/s/161/1496)より、速度が含まれる GPS データをご提供いただきました（ファイル：[Test-DriveData13.gpx.xml](./Java/GPXSamples/Test-DriveData13.gpx.xml) ）。[ビズベース様](https://www.big-advance.site/s/161/1496)提供データ [Test-DriveData13.gpx.xml](./Java/GPXSamples/Test-DriveData13.gpx.xml) を入力した場合は、0km/h より速いデータだけを処理します。

個々のデータを受信したタイミングで、IRIS へデータを渡すこともできますが、サンプルの中では GPX ファイル全体の中から、指定速度より速いデータのみを抽出し、一括で IRIS へ渡し登録しています。


### 1-1) Java から IRIS へ接続する方法

4 手法あります（サンプルでは、この中の 1 つを利用しています）。
1) SQLでアクセスする場合に便利な **JDBC** の利用
2) 大量データを高速に登録したい場合に最適な **XEP**
3) キーバリュー形式でデータを設定／取得したい場合の **Native API**
4) **Hibernate** を利用する方法

サンプルは、**2) 大量データを高速に登録したい場合に最適な XEP（ゼップ）** を利用しています。


### 1-2) [XEP](https://docs.intersystems.com/irislatestj/csp/docbook/Doc.View.cls?KEY=BJAVXEP) について

[XEP](https://docs.intersystems.com/irislatestj/csp/docbook/Doc.View.cls?KEY=BJAVXEP) は、Javaで作成したオブジェクトを IRIS に永続化する際に使用する接続方法です（事前に IRIS 側でクラス／テーブルの作成は不要）。

データ登録までの流れは以下の通りです。

1) IRIS に永続化したいオブジェクトを Java クラスで用意
2) XEP を利用して IRIS に接続し、Java クラスのスキーマ情報を IRIS へ投影（Java クラスを分析し、スキーマをインポート）
3) 永続化したい Java オブジェクを 1 つ以上作成し、Java の配列にセット
4) IRIS へ渡して永続化（store() メソッドを利用して登録）

XEPについて詳しくは、[ドキュメント](https://docs.intersystems.com/irislatestj/csp/docbook/Doc.View.cls?KEY=BJAVXEP_xep)をご参照ください。


## 2) 実行環境テンプレートの使用方法

テンプレートはコンテナを利用しています。
Docker、docker-compose、git が利用できる環境でお試しください。

**使用するコンテナのイメージ**
![](https://github.com/iijimam/doc-images/blob/master/IRIS-GSP-XEP-Teamplate/containers.png)

データ登録後、ストリートビューで表示を確認できます。

URL:　http://お使いのホスト:62774/csp/user/StreetView.html

>ストリートビューの正確な表示には、[GoogleのAPIキー](https://developers.google.com/maps/documentation/android-sdk/get-api-key?hl=ja)が必要となります。

実行手順は以下の通りです。

---

- [2-1) ダウンロード (git clone)](#2-1-%E3%83%80%E3%82%A6%E3%83%B3%E3%83%AD%E3%83%BC%E3%83%89-git-clone)
- [2-2) ストリートビューの表示をお試しいただくための準備](#2-2-%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%88%E3%83%93%E3%83%A5%E3%83%BC%E3%81%AE%E8%A1%A8%E7%A4%BA%E3%82%92%E3%81%8A%E8%A9%A6%E3%81%97%E3%81%84%E3%81%9F%E3%81%A0%E3%81%8F%E3%81%9F%E3%82%81%E3%81%AE%E6%BA%96%E5%82%99)
- [2-3) Java実行環境用コンテナを使う場合](#2-3-java%E5%AE%9F%E8%A1%8C%E7%92%B0%E5%A2%83%E7%94%A8%E3%82%B3%E3%83%B3%E3%83%86%E3%83%8A%E3%82%92%E4%BD%BF%E3%81%86%E5%A0%B4%E5%90%88)
- [3) Java の実行をホストで行う場合](#3-java-%E3%81%AE%E5%AE%9F%E8%A1%8C%E3%82%92%E3%83%9B%E3%82%B9%E3%83%88%E3%81%A7%E8%A1%8C%E3%81%86%E5%A0%B4%E5%90%88)
    - [3-1) Linuxの場合](#3-1-linux%E3%81%AE%E5%A0%B4%E5%90%88)
    - [3-2) Windows の場合](#3-2-windows-%E3%81%AE%E5%A0%B4%E5%90%88)

---

### 2-1) ダウンロード (git clone)

```
git clone https://github.com/Intersystems-jp/IRIS-GPS-XEP-Template.git
```

### 2-2) ストリートビューの表示をお試しいただくための準備

ストリートビューの正確な表示には、[GoogleのAPIキー](https://developers.google.com/maps/documentation/android-sdk/get-api-key?hl=ja)が必要となります。

APIキーを入手されたら、コンテナビルド前に [apikey.txt](./IRIS/web/apikey.txt) にキー情報を保存しておいてください。

> IRIS用コンテナビルド時に API キーを HTML に反映しています。ビルド完了後に反映／変更される場合は、再度ビルドを行ってください。

コンテナビルド方法詳細は、[2-3) Java実行環境用コンテナを使う場合](#2-3-java%E5%AE%9F%E8%A1%8C%E7%92%B0%E5%A2%83%E7%94%A8%E3%82%B3%E3%83%B3%E3%83%86%E3%83%8A%E3%82%92%E4%BD%BF%E3%81%86%E5%A0%B4%E5%90%88) をご参照ください。

### 2-3) Java実行環境用コンテナを使う場合

**操作例**
![](https://github.com/iijimam/doc-images/blob/master/IRIS-GSP-XEP-Teamplate/examplecontainer.gif)

1) コンテナをビルドする方法

    ```
    docker-compose build
    ```
    ※ IRIS のイメージのダウンロードや Java 実行環境の作成を行うため、少し時間がかかります。

    【メモ】
    このサンプルで使用しているIRISのイメージは、[コンテナレジストリ](https://containers.intersystems.com/contents)からPullしています。
    
    操作される環境で初めてPullする場合は、実行前に `docker login` を行ってください。

    ```
    docker login -u="ユーザ名" -p="パスワード" containers.intersystems.com
    ```
    
    詳細な手順はコミュニティの記事：[InterSystems Container Registryのご紹介](https://jp.community.intersystems.com/node/496571) の「ICRへの認証」をご覧ください。

2) コンテナを開始する方法

    ```
    docker-compose up -d iris
    ```
    この実行で **IRIS 用コンテナ** が開始します（Java 用コンテナは Java 実行時に開始します）。

    
    - 停止したコンテナを再開する方法（Java 用コンテナは Java 実行時に開始します）。
        ```
        docker-compose start iris
        ```


3) サンプルを動かす方法

    Java実行後、ストリートビューの表示に使用する **視点の向き** を計算するため IRIS 内ルーチンを実行します。

    手順は以下の通りです。

    お好みの [GPX ファイル](./Java/GPXSamples)を引数に指定して Java を実行します。

    ```
    $ docker-compose run java GPXSamples/Sakurajima.gpx.xml
    Creating iris-gps-xep-template_java_run ... done
    読むファイル : GPXSamples/Sakurajima.gpx.xml
    リストの個数＝37
    EmitData 開始！
    onNext：31.5631-130.55755-null
        ＜表示省略＞
    フィルタ後の個数： 37
    Elapsed server write time: 0
    データ登録完了!
    $          
    ```

    別のサンプルファイルを入力する場合は、[GPXSamples](./Java/GPXSamples) ディレクトリの他ファイルを指定してください。

    例）Enoshima.gpx.xml を使う例
    
    ```
    $ docker-compose run java GPXSamples/Enoshima.gpx.xml
    ```

    Java 実行後、ストリートビューの表示に使用する **視点の向き** を計算するため IRIS 内ルーチンを実行します。
    
    ```
    $ docker-compose exec iris iris session iris enrich
    70.603534585300067761
    71.462647720575148468
        ＜表示省略＞
    $
    ```

4) 入力した GPS データをストリートビューで表示する方法

    例）　http://localhost:62774/csp/user/StreetView.html

    ※ [localhost]は、お使いの環境に合わせご変更ください。


5) コンテナを停止する方法
    

    ```
    docker-compose stop
    ```

    IRIS／Java のコンテナを**破棄したい場合**は **down** を指定して実行します。

    ```
    docker-compose down
    ```

5) Javaのソースコードを変えた場合の反映方法
    
    ```
    docker-compose build java
    ```

    
## 3) Java の実行をホストで行う場合

ホストに、OpenJDK 8、Maven がインストールされている状態でお試し下さい。

ソースコードは、[Java/src/main/java/JavaXEPSample](./Java/src/main/java/JavaXEPSample) にあります。

Java の 接続先 IRIS はコンテナの IRIS を使用しています。

**Javaの実行をホストで行う場合の流れ**
![](https://github.com/iijimam/doc-images/blob/master/IRIS-GSP-XEP-Teamplate/examplehost.gif)

### 3-1) Linuxの場合

Java から IRIS へ接続するときのホスト名に **localhost** を指定しています。

実行環境に合わせてホスト名を変更できるように、[host-java-params.sh](./Java/host-java-params.sh) にホスト名を指定し、環境変数に設定しています。

localhost 以外の場合は、以降に登場するシェル実行前に [host-java-params.sh](./Java/host-java-params.sh) の以下行を環境に合わせて変更してください。

```
IRISHOSTNAME="localhost"
```

準備ができたら以下の手順で実行してください。

Maven を使用したビルドと、IRIS 用コンテナを開始します。

```
~/IRIS-GPS-XEP-Template$ cd Java
~/IRIS-GPS-XEP-Template/Java$ source ./host-java-params.sh
~/IRIS-GPS-XEP-Template/Java$ ./build-java-host.sh           
```

Javaの実行には、[runhost.sh](./Java/runhost.sh) を使用します。
引数に [GPXファイル](./Java/GPXSamples) を指定してください。

実行例）
```
$ ./runhost.sh GPXSamples/Sakurajima.gpx.xml
読むファイル : GPXSamples/Sakurajima.gpx.xml
リストの個数＝37
EmitData 開始！
onNext：31.5631-130.55755-null
onNext：31.56313-130.55765-null
    ＜表示省略＞
フィルタ後の個数： 37
Elapsed server write time: 0
データ登録完了!

StreetViewの向きを前向きにするための処理を実行します（IRIS内ルーチンを実行します）

----------------------
** 処理終了しました **
----------------------
$    
```

### 3-2) Windows の場合

Java から IRIS へ接続するときのホスト名に **localhost** を指定しています。

実行環境に合わせてホスト名を変更できるように、[host-java-params.bat](./Java/host-java-params.bat) にホスト名を指定し、環境変数に設定しています。

localhost 以外の場合は、以降に登場するシェル実行前に [host-java-params.bat](./Java/host-java-params.bat) の以下行を環境に合わせて変更してください。

```
SET IRISHOSTNAME=localhost
```

準備ができたら以下の手順で実行してください。

Maven を使用したビルドと、IRIS 用コンテナを開始します。


```
~/IRIS-GPS-XEP-Template> cd Java
~/IRIS-GPS-XEP-Template/Java> host-java-params.bat
~/IRIS-GPS-XEP-Template/Java> build-java-host.bat           
```

Javaの実行には、[runhost.bat](./Java/runhost.bat) を使用します。
引数に [GPXファイル](./Java/GPXSamples) を指定してください。

実行例）
```
> runhost.bat GPXSamples/Sakurajima.gpx.xml
読むファイル : GPXSamples/Sakurajima.gpx.xml
リストの個数＝37
EmitData 開始！
onNext：31.5631-130.55755-null
onNext：31.56313-130.55765-null
    ＜表示省略＞
フィルタ後の個数： 37
Elapsed server write time: 0
データ登録完了!
-----------------------------------------------------------------
[Prepating for StreetView.html] running enrich routine on IRIS
-----------------------------------------------------------------
-----------------------
** completed !! **
-----------------------

>     
```


**READY SET CODE!!**