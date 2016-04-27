# MetaVersion - SVNベースの並行開発の支援ツール

MetaVersionはSVNリポジトリによりバージョン管理を行っているシステム開発を支援するツールです。
このツールは各プロジェクトによるコミットの情報を記録し、同一期間に同一パスに対する変更を行ったプロジェクトを把握することを容易にします。
また履歴表示や差分表示に相応のレスポンスタイムを必要とするSVNコマンドに対する部分的な代替手段を提供します。

## どうしてこのようなツールが必要なのか？

システム開発の現場ではしばしば複数のプロジェクトが並行して進んでいます。
保守運用フェーズともなれば比較的大規模な開発プロジェクトだけでなく、小規模で期間も短いシステム改修も多く発生します。

こうした並行開発状況においては、各プロジェクトにおいてどのようなコミットを行ってきたか把握することが必要になります。
またとくに複数プロジェクト間で同一パスに対する変更が発生していないかを確認することが必要になります。
このようなニーズに応えるためMetaVersionは構想されました。

## このツールで何ができるの？

バージョン`1.0.x`においてはおよそ次のような機能が提供されています：

* あるプロジェクトが行ったSVNコミットの一覧と詳細の表示
* あるプロジェクトが行ったSVNコミットによる重複のない変更パス一覧の表示
* 　　　　　　　　　　　　　　〃　　　　　　　　　　　　　　　　　のCSVダウンロード
* あるプロジェクトに並行して同一パスへの変更を行ったプロジェクト一覧の表示
* 　　　　　　　　　　　　　　〃　　　　　　　　　　　　　　　　　　のCSVダウンロード
* あるSVNリポジトリに行われたコミットの一覧と詳細の表示
* SVNリポジトリのコミット履歴の自動インポート
* SVNリポジトリのコミットと各プロジェクトの自動/手動での紐付け

バージョン`1.1.x`ではコミット一覧やコミット詳細に対象ブランチの情報表示が追加され、CSVダウンロードやコミット紐づけの機能について存在していた不具合の解消が行われました。

## どのようなアーキテクチャでつくられているの？

MetaVersionはJava EE環境で稼働するアプリケーションです。ターゲットとしているバージョンは`7`です。
アプリケーションは、データ参照・更新の機能を持つAPIを提供をもっぱらとするサーバサイドと
それらのAPIを使用してページの画面制御を行うクライアントサイドとから構成されます。

サーバサイドは `Spring Boot`およびそこに組み込まれた`Spring Web MVC`、`Spring Security`と、
JSONとJavaオブジェクトを相互変換するためのライブラリ`Jackson`、
ORマッピングのためのフレームワーク`MyBatis`や SVNリポジトリ・アクセスのためのライブラリ`SVNKit`、
HTMLテンプレート処理ライブラリ`Thymeleaf`やCSV加工処理ライブラリ`Apache Commons CSV`などを使用して構築されています。
データベースにはPostgreSQLの`9.4`を利用しています。

クライアントサイドは`AngularJS`の主要モジュールと`Bootstrap`のCSSを使用して構築されています。コーディングには`TypeScript`が利用されています。

## どのようにしてセットアップするの？

MetaVersionのセットアップにはJDKのバージョン`7`以上が必要になります。またコマンドラインで`mvn`が利用できること、PostgreSQLのバージョン`9.4`以上のDBクラスタ（DBインスタンス）に接続できる状態になっていることも前提になります。

Githubからプロジェクトのリソース一式をダウンロードしたら、プロジェクトのルート・ディレクトリに移動します。
`ext/ddl`ディレクトリ配下にアプリケーションが必要とするDBオブジェクトを作成するためのDDLが格納されています。
これをPostgreSQL上のDBクラスタで実行して必要なテーブル、ビュー、シーケンス、索引を作成します。

次に`src/resources/application.properties`に記載されたPostgreSQLの接続情報を適宜編集します：

```
spring.datasource.url=jdbc:postgresql://localhost:5432/metaversion
spring.datasource.username=postgres
spring.datasource.password=********
```

`mvn spring-boot:run`コマンドを実行します。これにより必要なライブラリのダウンロードが行われたあとでビルドが行われ、それが終わると組込み型の`Tomcat`が起動してアプリケーションがデプロイされます。

独立して稼働しているTomcatにデプロイを行う場合は、`mvn package`コマンドを実行してください。同様にビルドが行われて、`target`ディレクトリ配下に`*.war`ファイルが作成されますので、これをTomcatの`webapps`ディレクトリにコピーします。

ビルドとデプロイが終わったらWebブラウザから`http://localhost:8080/init`にアクセスしてください。独立型のTomcatにデプロイした場合は`http://localhost:8080/(*.warファイル名)/init`になります。このページを表示することでアプリケーションの動作に必要なマスタデータと`administrator`ユーザの登録が行われます。

初期化が成功した旨のページが表示されたら`http://localhost:8080/`にアクセスしてください。独立型のTomcatにデプロイした場合は`http://localhost:8080/(*.warファイル名)/`になります。自動的にログイン・ページにリダイレクトされるので、最前の初期化処理で作成された`administrator`ユーザでログインを行います。初期ログイン情報は`src/resources/application.properties`に記載されています。

ログインに成功したら必要なユーザ、リポジトリ、プロジェクトの情報を登録していきます。リポジトリからのコミット情報のインポートやコミット情報とプロジェクトとの紐付けの処理は、アプリケーションへのユーザ・アクセスをトリガーにして、直近1時間に処理が行われていない場合に自動で起動します（近いうちに任意のタイミングで処理を起動するための機能を追加する予定です）。







