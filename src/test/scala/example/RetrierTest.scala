package example

import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.duration._

class RetrierTest extends FlatSpec with Matchers {

  val retrier = new Retrier()

  "The retry method call" should "return valid result" in {
    retrier.retry[Int](
      block = () => 1 + 1,
      acceptResult = res => res % 2 == 0,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 2
  }

  "The retry method call with empty retries" should "does not fail and return first valid result" in {
    retrier.retry[Int](
      block = () => 1 + 2,
      acceptResult = res => res % 2 == 0,
      retries = List.empty
    ) shouldEqual 3
  }

  "The retry method call" should "do not receive accept edge and return last invalid result" in {
    var i = 0
    retrier.retry[Int](
      block = () => { i = i + 1; i },
      acceptResult = res => res > 10,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 4
  }

  "The retry method call with always false accept" should "return last invalid result" in {
    var i = 0
    retrier.retry[Int](
      block = () => { i = i + 1; i },
      acceptResult = res => false,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 4
  }

  "The retry method call" should "throw exception" in {
    assertThrows[Exception](
      retrier.retry[Int](
        block = () => throw new Exception,
        acceptResult = res => res % 2 == 0,
        retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
      )
    )
  }

  "The retry method call with string" should "return valid result" in {
    retrier.retry[String](
      block = () => "not empty string",
      acceptResult = res => res.isEmpty,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual "not empty string"
  }

  "The retry method call with null string" should "return null" in {
    retrier.retry[String](
      block = () => null,
      acceptResult = res => res.isEmpty,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual null
  }

  "The retry method call with null block" should "return throw IllegalArgumentException" in {
    assertThrows[Exception](
      retrier.retry[String](
        null,
        acceptResult = res => res.isEmpty,
        retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
      )
    )
  }

  "The retry method call with null acceptResult" should "return throw IllegalArgumentException" in {
    assertThrows[Exception](
      retrier.retry[String](
        block = () => "not empty string",
        null,
        retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
      )
    )
  }
}
