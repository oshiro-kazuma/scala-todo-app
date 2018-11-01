package models

import org.mindrot.jbcrypt.BCrypt

case class Account(id: Int, name: String, hashedPassword: String) {
  def validate(pass: String): Boolean = {
    BCrypt.checkpw(pass, hashedPassword)
  }
}

case class Task(id: Int, accountId: Int, name: String, status: String)

object TaskStatus {

  sealed abstract class TaskStatus(val value: String) {}

  case object NotStarted extends TaskStatus("NotStarted")

  case object InProgress extends TaskStatus("InProgress")

  case object Completed extends TaskStatus("Completed")

  case object Unknown extends TaskStatus("Unknown")

  def apply(status: String): TaskStatus = {
    status match {
      case "NotStarted" => NotStarted
      case "InProgress" => InProgress
      case "Completed" => Completed
      case _ => Unknown
    }
  }

}

