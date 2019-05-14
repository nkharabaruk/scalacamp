package example.service

import example.domain.User
import example.repository.UserRepositoryId
import org.scalatest.{FlatSpec, Matchers}

class UserServiceIdTest extends FlatSpec with Matchers {

  private val userRepositoryId = new UserRepositoryId()
  private val userService = new UserServiceId(userRepositoryId)
  private val username = "John Smith"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1

  "Register user" should "return valid result" in {
    val registeredUser = userService.registerUser(username, address, email)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username
    existingUser.address shouldEqual address
    existingUser.email shouldEqual email
  }

  "Register already existed user" should "return error message" in {
    val nonRegisteredUser = userService.registerUser(username, address, email)
    nonRegisteredUser.isLeft shouldEqual true
    nonRegisteredUser shouldEqual Left(s"example.User ${User(userId, username, address, email)} already exists.")
  }

  "Retrieve user by username" should "return valid result" in {
    val retrievedByUsername = userService.getByUsername(username).get
    retrievedByUsername.id shouldEqual userId
    retrievedByUsername.username shouldEqual username
    retrievedByUsername.address shouldEqual address
    retrievedByUsername.email shouldEqual email
  }

  "Retrieve user with invalid username" should "return none" in {
    userService.getByUsername("Random user name") shouldEqual None
  }

  "Retrieve user by id" should "return valid result" in {
    val retrievedById = userService.getById(userId).get
    retrievedById.id shouldEqual userId
    retrievedById.username shouldEqual username
    retrievedById.address shouldEqual address
    retrievedById.email shouldEqual email
  }

  "Retrieve user with invalid id" should "return none" in {
    userService.getById(2) shouldEqual None
  }
}
