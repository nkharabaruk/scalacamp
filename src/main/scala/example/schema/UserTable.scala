package example.schema

import example.domain.User
import slick.jdbc.H2Profile.api._

class UserTable(tag: Tag) extends Table[User](tag, "USERS") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def address = column[Option[String]]("address")
  def email = column[String]("email")

  def * = (id, username, address, email).mapTo[User]
}

