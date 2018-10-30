package repositories

import com.google.inject.Inject
import models.Account
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait AccountRepository {

  def all(): Future[Seq[Account]]

  def insert(Account: Account): Future[Unit]

}

class AccountRepositoryMySQL @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends AccountRepository with HasDatabaseConfigProvider[JdbcProfile] {

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
