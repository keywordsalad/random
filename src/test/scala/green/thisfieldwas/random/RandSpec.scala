package green.thisfieldwas.random

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class RandSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {

  implicit val arbitraryRng: Arbitrary[RNG] = Arbitrary(arbitrary[Long].map(ConstRng))

  import Rand._

  "Rand" can {
    "nextLong()" which {
      "generates any Long within some bound" in {
        forAll(arbitrary[RNG]) { rng =>
          val bound = 1337L
          val (_, x) = nextLong(bound)(rng)
          x shouldBe >=(0L)
          x shouldBe <(bound)
        }
      }
    }

    "nextInt()" which {
      "generates any Int within some bound" in {
        forAll(arbitrary[RNG]) { rng =>
          val bound = 1337
          val (_, x) = nextInt(bound)(rng)
          x shouldBe >=(0)
          x shouldBe <(bound)
        }
      }
    }

    "nextDouble()" which {
      "generates any Double between 0.0 and 1.0" in {
        forAll(arbitrary[RNG]) { rng =>
          val (_, x) = nextDouble()(rng)
          x shouldBe >=(0.0)
          x shouldBe <(1.0)
        }
      }
      "generates any Double within some bound" in {
        forAll(arbitrary[RNG]) { rng =>
          val bound = 0.5
          val (_, x) = nextDouble(bound)(rng)
          x shouldBe >=(0.0)
          x shouldBe <(0.5)
        }
      }
    }
  }

  case class ConstRng(x: Long) extends RNG {

    override def next64(): (RNG, Long) = (this, x)
  }
}
