scala-todo-app
===============

REST APIでタスク管理を行うPlay Frameworkを使用したScalaアプリケーション

## 使用ライブラリ

 - Play Framework 2.6
 - Slick 3
 - Scalaz 7
 
## 使用ミドルウェア
 - MySQL 8
 
## 実装方法/仕様

タスク管理を行うことができるWeb API(REST API)です。データフォーマットにはJSONを使用しています。認証方式にはJWTを使用しています。

タスクの状態(status)は以下のEnumのうちいずれかの状態を持つ。

| Enum名 | 説明 |
| --- | --- |
| NotStarted | 未着手 |
| InProgress | 着手中 |
| Completed | 完了 |

### endpoint 一覧

| メソッド | パス | 役割 |
| --- | --- | --- |
| GET | /tasks | タスクの一覧表示 |
| POST | /tasks | タスクの新規作成 |
| GET | /tasks/:id | タスクの詳細表示 |
| PUT | /tasks/:id | タスクの編集 |
| DELETE | /tasks/:id | タスクの削除 |

### 各endpointの詳細

各エンドポイントは `Authorization` Headerを必要とする。

#### GET /tasks
タスクの一覧を表示する。自身で作成したタスクの一覧のみを表示する。
```sh
curl "http://localhost:9000/tasks" \
     -H 'Content-Type: application/json'
```

#### POST /tasks
タスクを新規で追加する。

##### パラメーター
 - name(required) 件名
 - status(required) 状態

##### リクエスト例
```sh
curl -X "POST" "http://localhost:9000/tasks" \
     -H 'Content-Type: application/json' \
     -d $'{
  "name": "Scala TODO Appの実装",
  "status": "NotStarted"
}'
```

#### GET /tasks/:id

タスクの詳細を表示する。

##### リクエスト例
```sh
curl "http://localhost:9000/tasks/1"
```

#### PUT /tasks/:id

タスクを編集する。URLパラメーターで編集対象タスクのIDを受け取る。

name, statusのパラメータを必須で必要とし、与えられた値で情報を更新する。nameのみ、statusのみの更新はできない。

タスクの件名、状態を変更するにはこのエンドポイントを使用する。

##### パラメーター
 - name(required) 件名
 - status(required) 状態

##### リクエスト例
```sh
curl -X "PUT" "http://localhost:9000/tasks/1" \
     -H 'Content-Type: application/json' \
     -d $'{
  "name": "Scala TODO Appの実装",
  "status": "Completed"
}'
```

#### DELETE /tasks/:id

タスクを削除する。URLパラメーターで削除対象taskのIDを受け取る。

##### リクエスト例
```sh
curl -X "DELETE" "http://localhost:9000/tasks/1" \
     -H 'Content-Type: application/json'
```

### マイグレーション

Play Framework 標準の`evolutions` を使用しています。

## Setup

ビルドツールには `sbt` を使用しています。プログラムを実行するには、本リポジトリをチェックアウトした後に、下記コマンドを実行します。

```sh
sbt play/run
```
