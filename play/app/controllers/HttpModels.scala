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
