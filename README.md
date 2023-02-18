# Scala Random

This is a utility library containing a small collection of stateless random number generators for use in Scala.

**Supported algorithms:**

* splitmix64
* xoshiro256**
* linear congruential generators
  * rand48

## Using RNG's

Individual algorithms implement the `RNG` trait and the value generators are modeled as _state transition functions_ of 
the form `RNG => (RNG, A)`. The `Rand` object defines the state transition functions and adds the methods `map()`, 
`ap()`, and `flatMap()` to the functions.

**Example usage:**

```scala
import green.thisfieldwas.random.Rand._
import green.thisfieldwas.random.Xoshiro256ss

val intAndDoubleRand = for {
  x <- nextInt(255)
  y <- nextDouble()
} yield (x, y)

val seed = 42
val (_, (i, d)) = intAndDoubleRand(Xoshiro256ss(seed))
println(s"Int = $i")
println(s"Double = $i")
```
