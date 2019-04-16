package example

import scala.annotation.tailrec
import scala.concurrent.duration._

class Retrier {

  @tailrec
  final def retry[A](block: () => A,
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): A = {
    val result = block.apply()
    val accept = if (result != null) acceptResult.apply(result) else false
    if (accept || retries.isEmpty)
      result
    else {
      if (retries.nonEmpty) Thread.sleep(retries.head.toMillis)
      retry(block, acceptResult, retries.tail)
    }
  }
}
