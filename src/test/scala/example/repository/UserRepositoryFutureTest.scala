package example.repository

import example.domain.User
import org.scalatest.{AsyncFlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

/**
  * Integration level testing of the User Repository Future.
  */
class UserRepositoryFutureTest extends AsyncFlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val userRepository = new UserRepositoryFuture(db)
  db.run(userRepository.users.schema.create)
  private val username = "John Smith"
  private val anotherUsername = "Bread Pitt"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1

  "Register users" should "return valid results" in {
    userRepository.registerUser(username, address, email).map { registeredUser =>
      registeredUser.id shouldEqual userId
      registeredUser.username shouldEqual username
      registeredUser.address shouldEqual address
      registeredUser.email shouldEqual email
    }

    userRepository.registerUser(anotherUsername, address, email).map { registeredUser =>
      registeredUser.id shouldEqual 2
      registeredUser.username shouldEqual anotherUsername
      registeredUser.address shouldEqual address
      registeredUser.email shouldEqual email
    }
  }

  "Retrieve user by id" should "return valid result" in {
    userRepository.getById(userId).map { retrievedById =>
      retrievedById.get.id shouldEqual userId
      retrievedById.get.username shouldEqual username
      retrievedById.get.address shouldEqual address
      retrievedById.get.email shouldEqual email
    }
  }

  "Retrieve user with invalid id" should "return none" in {
    userRepository.getById(3).map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
  }

  "Retrieve user by username" should "return valid result" in {
    userRepository.getByUsername(username).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual userId
      retrievedByUsername.get.username shouldEqual username
      retrievedByUsername.get.address shouldEqual address
      retrievedByUsername.get.email shouldEqual email
    }
  }

  "Retrieve user with invalid username" should "return none" in {
    userRepository.getByUsername("Random user name").map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
  }

  "Register user with the same name" should "return valid result" in {
    userRepository.registerUser(username, address, email).map { registeredWithSameName =>
      registeredWithSameName.id shouldEqual 3
    }
  }

  "Retrieve all users" should "return not empty collection" in {
    userRepository.getAll.map { emptyUsers =>
      emptyUsers.isEmpty shouldEqual true
    }

    val user = User(1, username, address, email)
    userRepository.registerUser(user.username, user.address, user.email).map { registeredUser =>
      registeredUser shouldEqual user
    }

    userRepository.getAll.map { allUsers =>
      allUsers.nonEmpty shouldEqual true
      allUsers.head shouldEqual user
    }
  }
}
