package controllers

import play.api.libs.json.Json

case class TaskResponse(id: Int, accountId: Int, name: String, status: String)

object TaskResponse {
  implicit val jsonWrites = Json.writes[TaskResponse]
  implicit val jsonReads = Json.reads[TaskResponse]
}

case class TaskStoreRequest(name: String, status: String)

object TaskStoreRequest {
  implicit val jsonWrites = Json.writes[TaskStoreRequest]
  implicit val jsonReads = Json.reads[TaskStoreRequest]
}

case class LoginRequest(name: String, password: String)

object LoginRequest {
  implicit val jsonWrites = Json.writes[LoginRequest]
  implicit val jsonReads = Json.reads[LoginRequest]
}

case class LoginResponse(token: String)

object LoginResponse {
  implicit val jsonWrites = Json.writes[LoginResponse]
  implicit val jsonReads = Json.reads[LoginResponse]
}

case class AccountRegisterRequest(name: String, password: String)

object AccountRegisterRequest {
  implicit val jsonWrites = Json.writes[AccountRegisterRequest]
  implicit val jsonReads = Json.reads[AccountRegisterRequest]
}
