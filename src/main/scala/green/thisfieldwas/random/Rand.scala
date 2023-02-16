package green.thisfieldwas.random

case class Rand[A](run: RNG => (RNG, A)) {

  def runA(rng: RNG): A = run(rng)._2

  def map[B](f: A => B): Rand[B] =
    Rand { rng =>
      val (nextRNG, x) = run(rng)
      (nextRNG, f(x))
    }

  def flatMap[B](f: A => Rand[B]): Rand[B] =
    Rand { rng =>
      val (nextRNG, x) = run(rng)
      f(x).run(nextRNG)
    }
}

object Rand {

  def next(): Rand[Long] = Rand(_.next())

  def next(numBits: Int): Rand[Long] = Rand(_.next(numBits))

  def nextInt(bound: Int = Int.MaxValue): Rand[Int] = Rand(_.nextInt(bound))

  def nextLong(bound: Long = Long.MaxValue): Rand[Long] = Rand(_.nextLong(bound))

  def nextDouble(bound: Double = 1.0): Rand[Double] = Rand(_.nextDouble(bound))

  def nextBoolean(): Rand[Boolean] = Rand(_.nextBoolean())

  def nextChar(): Rand[Char] = Rand(_.nextChar())

  def nextAsciiChar(): Rand[Char] = Rand(_.nextAsciiChar())

  def nextIdChar(): Rand[Char] = Rand(_.nextIdChar())

  def nextAsciiString(length: Int): Rand[String] = Rand(_.nextAsciiString(length))

  def nextIdString(length: Int): Rand[String] = Rand(_.nextIdString(length))
}
