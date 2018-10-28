package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

import models.Account
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

class AccountDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Accounts = TableQuery[AccountsTable]

  def all(): Future[Seq[Account]] = db.run(Accounts.result)

  def insert(account: Account): Future[Unit] = db.run(Accounts += account).map { _ => () }

  private class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {

    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def displayName = column[String]("display_name")

    def mail = column[String]("mail")

    def password = column[String]("password")

    def * = (id, name, password, mail) <> (Account.tupled, Account.unapply)

  }

}
