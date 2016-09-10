
はじめに
==============

このファイルは、
blancoRest
のREADMEファイルです。

blancoRest はJava によるRestful APIを生成するためのツールです。
1.電文処理定義書・電文定義書から Reqeust, Response, AbstractApi コードを自動生成します。
 2.Apache Antタスクの形式で配布されています。
 > - ant -f task.xml clean で一時ファイルは全てクリーンされます
 > - ant -f task.xml meta で最低限必要な Java ソースを生成します（一回目はエラーになりますが気にしないで下さい）
 > - ant -f task.xml compile で必要な Java ソースをコンパイルします
 > - ant meta
 > - ant compile
 > - ant jar : ここで作成されたjarファイルを各プロジェクトで使用します．

##開発者
うえだうえお(tueda)

##ライセンス
 blancoRest は ライセンス として GNU Lesser General Public License を採用しています。

##依存するライブラリ
blancoRestは下記のライブラリを利用しています。
※各オープンソース・プロダクトの提供者に感謝します。

 1.JExcelApi - Java Excel API - A Java API to read, write and modify Excel spreadsheets
     http://jexcelapi.sourceforge.net/
     http://sourceforge.net/projects/jexcelapi/
     http://www.andykhan.com/jexcelapi/
   概要: JavaからExcelブック形式を読み書きするためのライブラリです。
   ライセンス: GNU Lesser General Public License

 2.blancoCodeGenerator
   概要: ソースコード生成ライブラリ
   ライセンス: GNU Lesser General Public License

 3.blancoCommons
   概要: blanco Framework共通ライブラリ
         メタ情報ファイルを読み込む際に利用しています。
   ライセンス: GNU Lesser General Public License

==============
設定ファイル
==============

/etc/blancorest/config.xml を配置します。

```config.xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
   <properties>
    <comment>Runtime Properties for blancoRest</comment>
    <entry key="ApiUrl">http://localhost/dapanda/</entry>
    <entry key="AuthId"></entry>
    <entry key="AuthPass"></entry>
    <entry key="UserAgent">BlancoRestAPI test client</entry>
    <entry key="SocketTimeout">3</entry>
    <entry key="ConnectionTimeout">3</entry>
    <entry key="Loglevel">LOG_DEBUG</entry>
   </properties>
```

