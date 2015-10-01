blancoRestPhp はPHP によるRestful APIを生成するためのツールです。
 1.電文処理定義書・電文定義書といった様式から各種ファイルを自動生成します。
   (1)電文処理定義書・電文定義書から Reqeust, Response, AbstractApi コードを自動生成します。
 2.Apache Antタスクの形式で配布されています。
  - ant -f task.xml clean で一時ファイルは全てクリーンされます
  - ant -f task.xml meta で最低限必要な Java ソースを生成します（一回目はエラーになりますが気にしないで下さい）
  - ant -f task.xml compile で必要な Java ソースをコンパイルします
  - 再度 ant meta または ant build で php ソースコードを生成します

[開発者]
 1.うえだうえお(tueda)

[ライセンス]
 1.blancoRestPhp は ライセンス として GNU Lesser General Public License を採用しています。

[依存するライブラリ]
blancoRestPhpは下記のライブラリを利用しています。
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
