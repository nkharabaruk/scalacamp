package example

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.annotation.tailrec
import scala.concurrent.duration._

class Retrier {

  @tailrec
  final def retry[A](block: () => Future[A],
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): Future[A] = {
    val result = block()
    var accept = false
    for { a <- result } yield accept = acceptResult.apply(a)
    if (accept || retries.isEmpty)
      result
    else {
      Thread.sleep(retries.head.toMillis)
      retry(block, acceptResult, retries.tail)
    }
  }
}
