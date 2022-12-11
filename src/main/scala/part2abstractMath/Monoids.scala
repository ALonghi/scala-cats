package part2abstractMath

/**
 * Monoids are (a type class) that provides a natural extension of Semigroups
 * offering a "zero"/default value
 */
object Monoids {

  import cats.instances.int._
  import cats.syntax.semigroup._ // import the |+|

  val numbers = (1 to 100).toList
  // |+| is always associative (the result will always be the same no matter the order of the elements
  val sumLeft = numbers.foldLeft(0)(_ |+| _)
  val sumRight = numbers.foldRight(0)(_ |+| _)

  // define a general API
  //  def combineFold[T](list: List[T])(implicit semigroup: Semigroup[T]): T =
  //    list.foldLeft(/** WHAT? **/)(_ |+| _)

  import cats.Monoid

  val intMonoid = Monoid[Int]
  val combineInt = intMonoid.combine(1, 19) // 20
  val zero = intMonoid.empty

  import cats.instances.string._

  val combineString = Monoid[String].combine("I understand ", " Monoids") // 20
  val eemptyString = Monoid[String].empty

  val combineOption = Monoid[Option[Int]].combine(Option(1), Option.empty[Int]) // Some(1)
  val emptyOption = Monoid[Option[Int]].empty

  // import cats.syntax.monoid._ // imports |+|
  val combinedOptionFancy = Option(3) |+| Option(2)

  def combineFold[T](list: List[T])(implicit monoid: Monoid[T]): T =
    list.foldLeft(monoid.empty)(_ |+| _)

  val phoneBooks = List(
    Map(
      "Alice" -> 236,
      "Bob" -> 647
    ),
    Map(
      "Charlie" -> 372,
      "Daniel" -> 399
    ),
    Map(
      "Tina" -> 12
    )
  )

  // with custom types
  case class ShoppingCart(items: List[String], total: Double)

  implicit val shoppingCartsMonoids = Monoid.instance[ShoppingCart](ShoppingCart(List.empty, 0), (s1, s2) =>
    ShoppingCart(items = s1.items ++ s2.items, total = s1.total + s2.total))

  def checkout(shoppingCarts: List[ShoppingCart]): ShoppingCart = combineFold(shoppingCarts)

  def main(args: Array[String]): Unit = {
    println(sumLeft)
    println(combineFold(numbers))
    println(combineFold(phoneBooks))

    val carts = List(
      ShoppingCart(List("iphone", "samsung"), 799),
      ShoppingCart(List("remote", "tv"), 99),
      ShoppingCart(List(), 0)
    )
    println(checkout(carts))

  }
}
