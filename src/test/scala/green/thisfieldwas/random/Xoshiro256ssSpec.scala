package green.thisfieldwas.random

import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.collection.mutable

class Xoshiro256ssSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {

  "Xoshiro256ss" can {
    "nextLong" which {
      "generates Long values within some bound" in {
        forAll(arbitrary[Long].map(Xoshiro256ss(_))) { rng =>
          val (_, x) = rng.nextLong(1000L)
          x shouldBe >=(0L)
          x shouldBe <(1000L)
        }
      }

      "generates a distribution of Long values within some bound" in {
        val total = 10000000
        val values = mutable.Map[Long, Long]()
        var rng: RNG = Xoshiro256ss(42)

        (0 to total).foreach { _ =>
          val (nextRNG, x) = rng.nextLong(1000L)
          values.put(x, values.getOrElse(x, 0L) + 1)
          rng = nextRNG
        }

        val percentages = values.mapValues(_ / total.toDouble)
        for ((_, percentage) <- percentages) {
          percentage shouldBe 0.001 +- 0.00005
        }
      }
    }

    "nextInt" which {
      "generates Int values within some bound" in {
        forAll(arbitrary[Long].map(Xoshiro256ss(_))) { rng =>
          val (_, x) = rng.nextInt(1000)
          x shouldBe >=(0)
          x shouldBe <(1000)
        }
      }

      "generates a distribution of Int values within some bound" in {
        val total = 10000000
        val values = mutable.Map[Int, Int]()
        var rng: RNG = Xoshiro256ss(42)

        (0 to total).foreach { _ =>
          val (nextRNG, x) = rng.nextInt(1000)
          values.put(x, values.getOrElse(x, 0) + 1)
          rng = nextRNG
        }

        val percentages = values.mapValues(_ / total.toDouble)
        for ((_, percentage) <- percentages) {
          percentage shouldBe 0.001 +- 0.00005
        }
      }
    }

    "nextDouble" which {
      "generates a Double value between 0.0 and 1.0" in {
        forAll(arbitrary[Long].map(Xoshiro256ss(_))) { rng =>
          val (_, x) = rng.nextDouble()
          x shouldBe >=(0.0)
          x shouldBe <(1.0)
        }
      }
    }

    "nextAsciiChar" which {
      "generates a distribution of ascii chars" in {
        val total = 10000000
        val values = mutable.Map[Char, Int]()
        var rng: RNG = Xoshiro256ss(42)

        (0 to total).foreach { _ =>
          val (nextRNG, x) = rng.nextAsciiChar()
          values.put(x, values.getOrElse(x, 0) + 1)
          rng = nextRNG
        }

        val percentages = values.mapValues(_ / total.toDouble)
        for ((_, percentage) <- percentages) {
          percentage shouldBe 0.007 +- 0.001
        }
      }
    }
  }
}
