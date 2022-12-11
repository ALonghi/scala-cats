package part1intro

/**
 * Type class variance
 * Variance tells us if a type constructor (equivalent to a generic type in Java)
 * is a subtype of another type constructor.
 *
 * - covariant enables the preference of more specific TC instance
 * - contravariant ability to use the superclass TC instance
 */
object TCVariance {

  // variance - is a generic type annotation that will allow you to propagate subtyping to the generic type
  class Animal

  class Cat extends Animal

  // covariant type: subtyping is propagated to the generic type
  class Cage[+T]

  val cage: Cage[Animal] = new Cage[Cat] // Cat <: Animal, so Cage[Cat] <: Cage[Animal]

  // contravariant type: subtyping is propagated BACKWARDS to the genric type
  class Vet[-T]

  val vet: Vet[Cat] = new Vet[Animal] // if Cat <: Animal, then Vet[Animal] <: Vet[Cat]

  // rule of thumb: if a generic type "HAS a T" = covariant, "ACTS on T" = contravariant
  // variance affect how TC instances are being fetched

  // contravariant TC
  // can use the superclass TC instance if nothing available strictly for that type
  trait SoundMaker[-T]

  implicit object AnimalSoundMaker extends SoundMaker[Animal]

  def makeSound[T](implicit soundMaker: SoundMaker[T]): Unit = println("Wow") // implementation not important

  makeSound[Animal] // ok - TC instance defined above
  makeSound[Cat] // ok - TC instance for Animal is also applicable to Cat(s)
  // rule 1: contravariant TCs can use the superclass instances if nothing is available strictly for that type

  // has implications for subtypes
  implicit object OptionSoundMaker extends SoundMaker[Option[Int]]

  makeSound[Option[Int]]
  makeSound[Some[Int]]

  // covariant TC
  // will always use the more specific type class instance for the type
  // and will throw a compilation error as the compiler will be confused if the general instance is also present (in scope)
  trait AnimalShow[+T] {
    def show: String
  }

  implicit object GeneralAnimalShow extends AnimalShow[Animal] {
    override def show: String = "animals everywhere"
  }

  implicit object CatShow extends AnimalShow[Cat] {
    override def show: String = "so many cats"
  }

  def organizeShow[T](implicit event: AnimalShow[T]): String = event.show
  // rule 2: co

  def main(args: Array[String]): Unit = {
    println(organizeShow[Cat]) // ok - the compiler will inject CatsShow as implicit
    // println(organizeShow[Animal]) // will not compile - ambiguous values
  }

}
