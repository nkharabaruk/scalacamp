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

  "The retry method call with empty retries" should "return first result" in {
    retrier.retry[Int](
      block = () => 1 + 2,
      acceptResult = res => res % 2 == 0,
      retries = List.empty
    ) shouldEqual 3
  }

  "The retry method call" should "call 4 times and return last invalid result" in {
    var counter = 0
    retrier.retry[Int](
      block = () => { counter = counter + 1; counter },
      acceptResult = res => res > 10,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 4
    counter shouldEqual 4
  }

  "The retry method call" should "wait 3 seconds and return last invalid result" in {
    val start = System.currentTimeMillis()
    retrier.retry[Int](
      block = () => 1 + 1,
      acceptResult = res => false,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 2
    val end = System.currentTimeMillis()
    val duration = ( end - start ) / 1000
    duration shouldEqual 3
  }
}
