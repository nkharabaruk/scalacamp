package example

import example.Validator._
import org.scalatest.{FlatSpec, Matchers}

class ValidatorTest extends FlatSpec with Matchers {

  "The int validate method calls" should "return valid result" in {
    val positiveErrorMessage = "Value should be positive. "
    val lessErrorMessage = "Value should be less than given. "

    (234 validate positiveInt) shouldEqual Right(234)
    (0 validate positiveInt) shouldEqual Left(positiveErrorMessage)

    234.validate shouldEqual Right(234)
    (0 validate) shouldEqual Left(positiveErrorMessage)

    234.validate() shouldEqual Right(234)
    0.validate shouldEqual Left(positiveErrorMessage)

    234 validate lessThan(235) shouldEqual Right(234)
    234 validate lessThan(10) shouldEqual Left(lessErrorMessage)

    2 validate (positiveInt and lessThan(10)) shouldEqual Right(2)
    0 validate (positiveInt and lessThan(10)) shouldEqual Left(positiveErrorMessage)
    11 validate (positiveInt and lessThan(10)) shouldEqual Left(lessErrorMessage)
    0 validate (positiveInt and lessThan(-1)) shouldEqual Left(positiveErrorMessage + lessErrorMessage)

    2 validate (positiveInt or lessThan(10)) shouldEqual Right(2)
    0 validate (positiveInt or lessThan(10)) shouldEqual Right(0)
    11 validate (positiveInt or lessThan(10)) shouldEqual Right(11)
    0 validate (positiveInt or lessThan(-1)) shouldEqual Left(positiveErrorMessage + lessErrorMessage)
  }

  "The string validate method calls" should "return valid result" in {
    val errorMessage = "Value should not be empty. "
    "" validate Validator.nonEmpty shouldEqual Left(errorMessage)
    ("" validate) shouldEqual Left(errorMessage)

    "asdasd" validate Validator.nonEmpty shouldEqual Right("asdasd")
    ("asdasd" validate) shouldEqual Right("asdasd")
  }


  "The person validate method calls" should "return valid result" in {
    val person = Person(name = "John", age = 25)
    person validate isPersonValid shouldEqual Right(person)
    (person validate) shouldEqual Right(person)

    val errorMessage = "Person is invalid - either name is empty, either age is not in range [1-99]. "
    Person("", 25) validate isPersonValid shouldEqual Left(errorMessage)
    Person("John", 100) validate isPersonValid shouldEqual Left(errorMessage)
    Person("", 0) validate isPersonValid shouldEqual Left(errorMessage)
  }
}