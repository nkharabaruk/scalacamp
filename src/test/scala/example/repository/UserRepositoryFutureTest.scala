package example.repository

import org.scalatest.{AsyncFlatSpec, Matchers}

class UserRepositoryFutureTest extends AsyncFlatSpec with Matchers {

  val userRepositoryFuture = new UserRepositoryFuture()

  "Register users" should "return valid results" in {
    val johnUsername = "John Smith"
    userRepositoryFuture.registerUser(johnUsername).map { registeredUser =>
      registeredUser.id shouldEqual 1
      registeredUser.username shouldEqual johnUsername
    }

    val breadUsername = "Bread Pitt"
    userRepositoryFuture.registerUser(breadUsername).map { registeredUser =>
      registeredUser.id shouldEqual 2
      registeredUser.username shouldEqual breadUsername
    }
  }

  "Retrieve user by id" should "return valid result" in {
    val johnUsername = "John Smith"
    val johnId = 1
    userRepositoryFuture.getById(johnId).map { retrievedById =>
      retrievedById.get.id shouldEqual johnId
      retrievedById.get.username shouldEqual johnUsername
    }
  }

  "Retrieve user with invalid id" should "return none" in {
    userRepositoryFuture.getById(3).map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
  }

  "Retrieve user by username" should "return valid result" in {
    val johnUsername = "John Smith"
    val johnId = 1
    userRepositoryFuture.getByUsername(johnUsername).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual johnId
      retrievedByUsername.get.username shouldEqual johnUsername
    }
  }

  "Retrieve user with invalid username" should "return none" in {
    userRepositoryFuture.getByUsername("Random user name").map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
  }

  "Register user with the same name" should "return valid result" in {
    val username = "John Smith"
    userRepositoryFuture.registerUser("John Smith").map { registeredWithSameName =>
      registeredWithSameName.id shouldEqual 3
    }
  }
}
