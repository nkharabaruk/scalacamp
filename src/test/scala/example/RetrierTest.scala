package example

import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.duration._

class RetrierTest extends FlatSpec with Matchers {

  val sut = new Retrier()

  "The Retrier object" should "return 2" in {
    sut.retry[Int](
      block = () => 1 + 1,
      acceptResult = res => res % 2 == 0,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 2
  }
}
