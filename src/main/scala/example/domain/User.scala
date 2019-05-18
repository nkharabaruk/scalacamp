package example.domain

case class User(id: Long, username: String, address: Option[String], email: String)

case class NewUser(username: String, address: Option[String], email: String)
