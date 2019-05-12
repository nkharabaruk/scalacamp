package example.repository

import org.scalatest.{AsyncFlatSpec, Matchers}

class UserRepositoryFutureTest extends AsyncFlatSpec with Matchers {

  "The user repository future method calls" should "return valid result" in {
    val userRepositoryFuture = new UserRepositoryFuture()

    val johnUsername = "John Smith"
    val johnId = 1

    userRepositoryFuture.registerUser(johnUsername).map { registeredUser =>
      registeredUser.id shouldEqual johnId
      registeredUser.username shouldEqual johnUsername
    }
    userRepositoryFuture.getByUsername(johnUsername).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual johnId
      retrievedByUsername.get.username shouldEqual johnUsername
    }

    userRepositoryFuture.getById(johnId).map { retrievedById =>
      retrievedById.get.id shouldEqual johnId
      retrievedById.get.username shouldEqual johnUsername
    }
    userRepositoryFuture.registerUser("Bread Pitt").map { registeredUser =>
      registeredUser.id shouldEqual 2
    }
    userRepositoryFuture.getById(3).map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
    userRepositoryFuture.getByUsername("Random user name").map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
    userRepositoryFuture.registerUser("John Smith").map { registredWithSameName =>
      registredWithSameName.id shouldEqual 3
    }
  }
}
