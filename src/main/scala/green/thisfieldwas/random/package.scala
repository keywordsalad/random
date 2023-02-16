package green.thisfieldwas

package object random {

  type Rand[A] = RNG => (RNG, A)
}
