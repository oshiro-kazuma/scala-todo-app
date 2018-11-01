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
| POST | /auth/login | ログイン |
| POST | /auth/register | アカウント登録 |
| GET | /tasks | タスクの一覧表示 |
| POST | /tasks | タスクの新規作成 |
| GET | /tasks/:id | タスクの詳細表示 |
| PUT | /tasks/:id | タスクの編集 |
| DELETE | /tasks/:id | タスクの削除 |

### 各endpointの詳細

`/tasks` 配下のエンドポイントは `Authorization` Headerを必要とする。

#### POST /auth/register
ログインアカウントを新規作成する。

##### パラメーター
 - name(required) 件名
 - password(required) 状態

##### リクエスト例
```sh
curl -X "POST" "http://localhost:9000/auth/register" \
     -H 'Content-Type: application/json' \
     -d $'{
  "name": "oshiro",
  "password": "hoge"
}'
```

##### レスポンス例
```json
{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50Ijp7ImlkIjoxLCJuYW1lIjoib3NoaXJvIn19.nygJ9HfYWW_Ozax-sB_6hGcqsfZKeOsI6OLb00TmO-E"}
```

#### POST /auth/login
ログインを行い、ログイントークンを得る

##### パラメーター
 - name(required) 件名
 - password(required) 状態

##### リクエスト例
```sh
curl -X "POST" "http://localhost:9000/auth/login" \
     -H 'Content-Type: application/json' \
     -d $'{
  "name": "oshiro",
  "password": "hoge"
}'
```

##### レスポンス例
```json
{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50Ijp7ImlkIjoxLCJuYW1lIjoib3NoaXJvIn19.nygJ9HfYWW_Ozax-sB_6hGcqsfZKeOsI6OLb00TmO-E"}
```

以下タスク操作のAPIを呼び出すために、ログイントークンを取得しておく。
```sh
jwt=`curl -X "POST" "http://localhost:9000/auth/login" -H 'Content-Type: application/json' -d $'{
  "name": "oshiro2",
  "password": "hoge"
}
' | jq .token -r`
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
     -H "Authorization: Bearer $jwt" \
     -d $'{
  "name": "hoge",
  "status": "NotStarted"
}'
```

##### レスポンス例
```
Created
```

#### GET /tasks
タスクの一覧を表示する。自身で作成したタスクの一覧のみを表示する。
```sh
curl "http://localhost:9000/tasks" \
     -H 'Content-Type: application/json' \
     -H "Authorization: Bearer $jwt"
```

クエリパラメータ―でstatusを指定してフィルタすることが可能です。statusはカンマ区切りで複数指定できます。

完了タスクを表示する場合
```sh
curl "http://localhost:9000/tasks?status=Completed" \
     -H 'Content-Type: application/json' \
     -H "Authorization: Bearer $jwt"
```

未完了タスクを表示する場合
```sh
curl "http://localhost:9000/tasks?status=NotStarted,InProgress" \
     -H 'Content-Type: application/json' \
     -H "Authorization: Bearer $jwt"
```

##### レスポンス例
```json
[
  {
    "id": 2,
    "accountId": 1,
    "name": "hoge",
    "status": "NotStarted"
  }
]
```

#### GET /tasks/:id

タスクの詳細を表示する。

##### リクエスト例
```sh
curl "http://localhost:9000/tasks/1" \
     -H 'Content-Type: application/json' \
     -H "Authorization: Bearer $jwt"
```

##### レスポンス例
```json
{
  "id": 1,
  "accountId": 1,
  "name": "hoge",
  "status": "NotStarted"
}
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
     -H "Authorization: Bearer $jwt" \
     -d $'{
  "name": "changed",
  "status": "InProgress"
}'
```

##### レスポンス例
```
Updated
```

#### DELETE /tasks/:id

タスクを削除する。URLパラメーターで削除対象taskのIDを受け取る。

##### リクエスト例
```sh
## タスク削除
curl -X "DELETE" "http://localhost:9000/tasks/1" \
     -H 'Content-Type: application/json' \
     -H "Authorization: Bearer $jwt"
```

##### レスポンス例
```
Deleted
```


### マイグレーション

Play Framework 標準の`evolutions` を使用しています。

## Setup

ビルドツールには `sbt` を使用しています。プログラムを実行するには、本リポジトリをチェックアウトした後に、下記コマンドを実行します。

```sh
docker-compose up -d
sbt play/run
```

## 追加実装

 - docker-composeを使用してMySQL 8を起動し、ローカルでの開発で使用できるようにしています。
 - 状態を未着手、着手中、完了の3状態を扱えるようにしています
 - `/auth/register` エンドポイントでログインアカウントを登録できるようにしています。
 - DatabaseアクセスはRepositoryパターンを使用して分離し、インターフェースをきることでテスタブルな実装にしています。
 - ScalazのEitherT、OptionTなどのモナドトランスフォーマーを使用し、複数のモナドが混在する場合でも簡潔に記述しています。(主にFutureとなにか(Option, Either)を使う箇所)
 - テストでは `mockito` を使用してDBアクセスをモック化しています。一部はin-memoryなRepositoryをstubとして作成しています。
   - Repositoryのユニットテストコードは時間削減の為に作成していません。
