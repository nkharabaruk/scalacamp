package example

/**
  * Implement validator typeclass that should validate arbitrary value [T].
  * @tparam T the type of the value to be validated.
  */
trait Validator[T] {
  /**
    * Validates the value.
    * @param value value to be validated.
    * @return Right(value) in case the value is valid, Left(message) on invalid value
    */
  def validate(value: T): Either[String, T]

  /**
    * And combinator.
    * @param other validator to be combined with 'and' with this validator.
    * @return the Right(value) only in case this validator and <code>other</code> validator returns valid value,
    *         otherwise Left with error messages from both validators.
    */
  def and(other: Validator[T]): Validator[T] = new Validator[T] {
    override def validate(value: T): Either[String, T] = {
      val a = this.validate(value)
      val b = other.validate(value)
      Either.cond(a.isRight && b.isRight, value, a.left.getOrElse("") + b.left.getOrElse(""))
    }
  }

  /**
    * Or combinator.
    * @param other validator to be combined with 'or' with this validator.
    * @return the Right(value) only in case either this validator or <code>other</code> validator returns valid value,
    *         otherwise Left with error messages from the failed validator or from both if both failed.
    */
  def or(other: Validator[T]): Validator[T] = new Validator[T] {
    override def validate(value: T): Either[String, T] = {
      val a = this.validate(value)
      val b = other.validate(value)
      Either.cond(a.isRight || b.isRight, value, a.left.getOrElse("") + b.left.getOrElse(""))
    }
  }
}


object Validator {
  val positiveInt : Validator[Int] = new Validator[Int] {
    override def validate(t: Int): Either[String, Int] = Either.cond(t > 0, t, "Value should be positive. ")
  }

  def lessThan(n: Int): Validator[Int] = new Validator[Int] {
    override def validate(t: Int): Either[String, Int] = Either.cond(t < n, t, "Value should be less than given. ")
  }

  val nonEmpty : Validator[String] = new Validator[String] {
    override def validate(t: String): Either[String, String] = Either.cond(t.nonEmpty, t, "Value should not be empty. ")
  }

  val isPersonValid = new Validator[Person] {
    // Returns valid only when the name is not empty and age is in range [1-99].
    override def validate(value: Person): Either[String, Person] = Either.cond(value.name.nonEmpty && (1 to 99).contains(value.age),
        value, "Person is invalid - either name is empty, either age is not in range [1-99]. ")
  }

  implicit class IntImprovements(val n: Int) {
    def validate(implicit t:Validator[Int] = Validator.positiveInt): Either[String, Int] = t.validate(n)
  }

  implicit class StringImprovements(val n: String) {
    def validate(implicit t:Validator[String] = Validator.nonEmpty): Either[String, String] = t.validate(n)
  }

  implicit class PersonImprovements(val n: Person) {
    def validate(implicit t:Validator[Person] = Validator.isPersonValid): Either[String, Person] = t.validate(n)
  }
}

object ValidApp {
  import Validator._

  // uncomment make possible next code to compile
  2 validate (positiveInt and lessThan(10))

  // uncomment make possible next code to compile
  "" validate Validator.nonEmpty

  // uncomment make possible next code to compile
  Person(name = "John", age = 25) validate isPersonValid
}

object ImplicitValidApp {
  import Validator._

  // uncomment next code and make it compilable and workable
  Person(name = "John", age = 25) validate

  "asdasd" validate

  234.validate
}

case class Person(name: String, age: Int)