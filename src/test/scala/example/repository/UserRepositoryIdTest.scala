package example.repository

import org.scalatest.{FlatSpec, Matchers}

class UserRepositoryIdTest extends FlatSpec with Matchers {

  private val userRepositoryId = new UserRepositoryId
  private val username = "John Smith"
  private val userId = 1

  "Register users" should "return valid results" in {
    val registeredUser = userRepositoryId.registerUser(username)
    registeredUser.id shouldEqual userId
    registeredUser.username shouldEqual username

    val anotherUsername = "Bread Pitt"
    val registeredUser2 = userRepositoryId.registerUser(anotherUsername)
    registeredUser2.id shouldEqual 2
    registeredUser2.username shouldEqual anotherUsername
  }

  "Retrieve user by id" should "return valid result" in {
    val retrievedById = userRepositoryId.getById(userId).get
    retrievedById.id shouldEqual userId
    retrievedById.username shouldEqual username
  }

  "Retrieve user with invalid id" should "return none" in {
    userRepositoryId.getById(3) shouldBe None
  }

  "Retrieve user by username" should "return valid result" in {
    val retrievedByUsername = userRepositoryId.getByUsername(username).get
    retrievedByUsername.id shouldEqual userId
    retrievedByUsername.username shouldEqual username
  }

  "Retrieve user with invalid username" should "return none" in {
    userRepositoryId.getByUsername("Random user name") shouldBe None
  }

  "Register user with the same name" should "return valid result" in {
    val registeredUser = userRepositoryId.registerUser(username)
    registeredUser.id shouldEqual 3
    registeredUser.username shouldEqual username
  }
}
