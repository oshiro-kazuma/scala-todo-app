package models

case class Account(id: Int, name: String, password: String, mail: String)

case class Task(id: Int, accountId: Int, name: String, status: String)