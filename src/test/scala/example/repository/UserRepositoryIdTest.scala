package example.repository

import example.domain.User
import org.scalatest.{FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

class UserRepositoryIdTest extends FlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val userRepository = new UserRepositoryId(db)
  db.run(userRepository.users.schema.create)
  private val username = "John Smith"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1

  "Register users" should "return valid results" in {
    val registeredUser = userRepository.registerUser(username, address, email)
    registeredUser.id shouldEqual userId
    registeredUser.username shouldEqual username
    registeredUser.address shouldEqual address
    registeredUser.email shouldEqual email

    val anotherUsername = "Bread Pitt"
    val registeredUser2 = userRepository.registerUser(anotherUsername, address, email)
    registeredUser2.id shouldEqual 2
    registeredUser2.username shouldEqual anotherUsername
    registeredUser2.address shouldEqual address
    registeredUser2.email shouldEqual email
  }

  "Retrieve user by id" should "return valid result" in {
    val retrievedById = userRepository.getById(userId).get
    retrievedById.id shouldEqual userId
    retrievedById.username shouldEqual username
    retrievedById.address shouldEqual address
    retrievedById.email shouldEqual email
  }

  "Retrieve user with invalid id" should "return none" in {
    userRepository.getById(3) shouldBe None
  }

  "Retrieve user by username" should "return valid result" in {
    val retrievedByUsername = userRepository.getByUsername(username).get
    retrievedByUsername.id shouldEqual userId
    retrievedByUsername.username shouldEqual username
    retrievedByUsername.address shouldEqual address
    retrievedByUsername.email shouldEqual email
  }

  "Retrieve user with invalid username" should "return none" in {
    userRepository.getByUsername("Random user name") shouldBe None
  }

  "Register user with the same name" should "return valid result" in {
    val registeredUser = userRepository.registerUser(username, address, email)
    registeredUser.id shouldEqual 3
    registeredUser.username shouldEqual username
    registeredUser.address shouldEqual address
    registeredUser.email shouldEqual email
  }

  "Retrieve all users" should "return not empty collection" in {
    val usersFromEmptyDB = userRepository.getAll
    usersFromEmptyDB.isEmpty shouldEqual true

    val user = User(1, username, address, email)
    userRepository.registerUser(user.username, user.address, user.email)

    val allUsers = userRepository.getAll
    allUsers.nonEmpty shouldEqual true
    allUsers.head shouldEqual user
  }
}
