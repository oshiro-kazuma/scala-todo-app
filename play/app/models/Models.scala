package models

import org.mindrot.jbcrypt.BCrypt

case class Account(id: Int, name: String, hashedPassword: String) {
  def validate(pass: String): Boolean = {
    BCrypt.checkpw(pass, hashedPassword)
  }
}

case class Task(id: Int, accountId: Int, name: String, status: String)
