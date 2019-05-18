package example.repository

import org.scalatest.{AsyncFlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

class UserRepositoryFutureTest extends AsyncFlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val userRepositoryFuture = new UserRepositoryFuture(db)
  db.run(userRepositoryFuture.users.schema.create)
  private val username = "John Smith"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1

  "Register users" should "return valid results" in {
    userRepositoryFuture.registerUser(username, address, email).map { registeredUser =>
      registeredUser.id shouldEqual userId
      registeredUser.username shouldEqual username
      registeredUser.address shouldEqual address
      registeredUser.email shouldEqual email
    }

    val anotherUsername = "Bread Pitt"
    userRepositoryFuture.registerUser(anotherUsername, address, email).map { registeredUser =>
      registeredUser.id shouldEqual 2
      registeredUser.username shouldEqual anotherUsername
      registeredUser.address shouldEqual address
      registeredUser.email shouldEqual email
    }
  }

  "Retrieve user by id" should "return valid result" in {
    userRepositoryFuture.getById(userId).map { retrievedById =>
      retrievedById.get.id shouldEqual userId
      retrievedById.get.username shouldEqual username
      retrievedById.get.address shouldEqual address
      retrievedById.get.email shouldEqual email
    }
  }

  "Retrieve user with invalid id" should "return none" in {
    userRepositoryFuture.getById(3).map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
  }

  "Retrieve user by username" should "return valid result" in {
    userRepositoryFuture.getByUsername(username).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual userId
      retrievedByUsername.get.username shouldEqual username
      retrievedByUsername.get.address shouldEqual address
      retrievedByUsername.get.email shouldEqual email
    }
  }

  "Retrieve user with invalid username" should "return none" in {
    userRepositoryFuture.getByUsername("Random user name").map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
  }

  "Register user with the same name" should "return valid result" in {
    userRepositoryFuture.registerUser(username, address, email).map { registeredWithSameName =>
      registeredWithSameName.id shouldEqual 3
    }
  }
}
