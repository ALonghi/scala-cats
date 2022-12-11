package part2abstractMath

import scala.util.Try

/**
 * Functors are higher-kinded type class (a way of abstracting over entities
 * that take type constructors) that providing a map method
 * are important when we want to generalize transformations in the following use cases:
 * - specialized data structures for high-performance algorithms
 * - any "mappable" structures under the same high-level API
 */
object Functors {

  // higher kinded type class
  trait MyFunctor[F[_]] {
    def map[A, B](initialValue: F[A])(f: A => B): F[B]
  }

  // part 1 - add typeclass instance

  import cats.Functor
  import cats.instances.list._ // includes Functor[List]

  val listFunctor = Functor[List]

  // part 2 -
  val incrementedNumbers = listFunctor.map(List(1, 2, 3))(_ + 1)

  import cats.instances.option._

  val optionFunctor = Functor[Option]
  val incrementedOption = optionFunctor.map(Option(1))(_ + 1)

  import cats.instances.try_._

  val tryFunctor = Functor[Try]
  val incrementedTry = tryFunctor.map(Try(1))(_ + 1)

  // generalizing an API
  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)

  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)

  def do10xTry(attempt: Try[Int]): Try[Int] = attempt.map(_ * 10)

  def do10x[F[_]](container: F[Int])(implicit functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  // define a custom functor
  trait Tree[+T]

  object Tree {
    // "smart" constructors
    def leaf[T](value: T): Leaf[T] = Leaf(value)

    def branch[T](value: T, left: Tree[T], right: Tree[T]): Tree[T] = Branch(value, left, right)
  }

  case class Leaf[+T](value: T) extends Tree[T]

  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]

  implicit object TreeFunctor extends Functor[Tree] {
    override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
      case Leaf(value)                => Leaf(f(value))
      case Branch(value, left, right) => Branch(f(value), map(left)(f), map(right)(f))
    }
  }

  // extension methods - map

  import cats.syntax.functor._

  val tree: Tree[Int] = Tree.branch(40, Tree.branch(5, Tree.leaf(10), Tree.leaf(20)), Tree.leaf(20))
  val incrementedTree = tree.map(_ + 1)

  def do10xShorter[F[_]: Functor](container: F[Int]): F[Int] = // is equal as   def do10x[F[_]](container: F[Int])(implicit f: Functor) = ???
    container.map(_ * 10)

  // 14:44
  def main(args: Array[String]): Unit = {
    println(do10x(List(1, 2, 3)))
    println(do10x(Option(2)))
    println(do10x(Try(25)))
    println(do10x(Tree.branch(30, Tree.leaf(10), Tree.leaf(20))))
    println(do10xShorter(Tree.branch(30, Tree.leaf(10), Tree.leaf(20))))
  }
}
