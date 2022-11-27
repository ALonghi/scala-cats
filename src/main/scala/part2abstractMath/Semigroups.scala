package part2abstractMath

object Semigroups {

  // Semigroups COMBINE elements of the same type

  import cats.Semigroup
  import cats.instances.int._

  val naturalIntSemigroup = Semigroup[Int]
  val intCombination = naturalIntSemigroup.combine(2, 46) // addition

  import cats.instances.string._

  val naturalStringSemigroup = Semigroup[String]
  val stringCombination = naturalStringSemigroup.combine("I love ", "Cats")

  // specific API (don't really need the semigroups
  def reduceInts(list: List[Int]): Int = list.reduce(naturalIntSemigroup.combine)

  def reduceStrings(list: List[String]): String = list.reduce(naturalStringSemigroup.combine)

  // general API
  def reduceThings[T](list: List[T])(implicit semigroup: Semigroup[T]): T = list.reduce(semigroup.combine)

  // for custom types
  case class Expense(id: Long, amount: Double)

  implicit val expenseSemigroup = Semigroup.instance[Expense] { (a1, a2) =>
    a1.copy(amount = a1.amount + a2.amount)
  }

  // extension methods for Semigroup - |+|

  import cats.syntax.semigroup._

  def reduceThings2[T](list: List[T])(implicit semigroup: Semigroup[T]): T = list.reduce(_ |+| _)

  val anIntSum = 2 |+| 3
  val anStringSum = "I like" |+| " Cats"
  val anExpensesSum = Expense(1, 10) |+| Expense(2, 15)


  def main(args: Array[String]): Unit = {
    println(intCombination)
    println(stringCombination)

    // specific API
    val numbers = (1 to 10).toList
    println(reduceInts(numbers))
    val strings = List("I'm ", "starting to", "like ", "Semigroups")
    println(reduceStrings(strings))

    // generic API
    println(reduceThings(numbers)) // compiler injects the implicit Semigroup[Int]
    println(reduceThings(strings)) // compiler injects the implicit Semigroup[String]
    import cats.instances.option._
    val numberOptions: List[Option[Int]] = numbers.map(Some.apply)
    println(reduceThings(numberOptions)) // an Option[Int] containing the sum of all the options

    // with custom types
    val expenses = (1 to 5).map(n => Expense(n.toLong, n.toDouble)).toList
    println(reduceThings(expenses))
    println(reduceThings2(expenses))
  }
}
