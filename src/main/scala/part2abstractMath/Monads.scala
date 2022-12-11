package part2abstractMath

import java.util.concurrent.Executors
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Monads are also Functor (extends Functor )
 * they are a mechanism for sequencing computations as they provide the .flatMap method and others
 * import cats.syntax.applicative._ -> provides the pure extension method
 * import cats.syntax.functor._ -> provides the map extension method
 * import cats.syntax.flatMap._ -> provides the flatMap extension method
 *
 * Use cases:
 *  - list combinations
 *  - option transformations
 *  - asynchronous chained computations
 *  - dependent computations
 */
object Monads {

  val numbersList = List(1, 2, 3)
  val charsList = List('a', 'b', 'c')
  // 1) Create a combination with all of (number, char)
  val combinationList: Seq[(Int, Char)] = for {
    n <- numbersList
    c <- charsList
  } yield (n, c)

  // 2) Create a combination with options
  val numberOption = Option(2)
  val charOption = Option('a')
  val combinationOption: Option[(Int, Char)] = for {
    n <- numberOption
    c <- charOption
  } yield (n, c)

  // 3) Create a combination with futures
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  val numberFuture = Future(42)
  val charFuture = Future('a')
  val combinationFuture: Future[(Int, Char)] = for {
    n <- numberFuture
    c <- charFuture
  } yield (n, c)

  trait MyMonad[M[_]] {
    def pure[A](value: A): M[A]

    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

    def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(v => pure(f(v)))
  }

  // Cats Monad

  import cats.Monad
  import cats.instances.option._ // implicit Monad[Option]

  val optionMonad = Monad[Option]
  val anOption = optionMonad.pure(4) // Option(4) == Some(4)
  val aTransformedOption = optionMonad.flatMap(anOption)(x => Some(x + 1))

  import cats.instances.list._

  val listMonad = Monad[List]
  val aList = listMonad.pure(1) // List(1)
  val aTransformedList = listMonad.flatMap(aList)(x => List(x, x + 1)) // List(3, 4)

  // use a Monad[Future]

  import cats.instances.future._

  val futureMonad = Monad[Future]
  val aFuture = futureMonad.pure(1)
  val aTransformedFuture = futureMonad.flatMap(aFuture)(v => Future(v + 1)) // Future(2)

  // specialized API
  def getPairsList(numbers: List[Int], chars: List[Char]): List[(Int, Char)] =
    for {
      n <- numbers
      c <- chars
    } yield (n, c)

  def getPairs[M[_], A, B](ma: M[A], mb: M[B])(implicit monad: Monad[M]): M[(A, B)] = {
    monad.flatMap(ma)(a => monad.map(mb)(b => (a, b)))
  }

  // extension methods - weirder imports - pure, flatMap

  import cats.syntax.applicative._ // pure is here

  val oneOption = 1.pure[Option] // implicit Monad[Option] will be used
  val oneList = 1.pure[List] // implicit Monad[List] will be used

  import cats.syntax.flatMap._ // flatMap is here

  val oneOptionTransformed = oneOption.flatMap(x => (x + 1).pure[Option])

  // Monad extends Functors
  val oneOptionMapped = Monad[Option].map(Option(2))(_ + 1) //

  import cats.syntax.functor._ // .map is here

  val oneOptionMapped2 = oneOption.map(_ + 2)

  // for-comprehensions
  val composedOptionFor: Option[Int] = for {
    one <- 1.pure[Option]
    two <- 2.pure[Option]
  } yield one + two

  // implement a shorter version of getParis using for-comprehension
  def getPairsWithFor[M[_]: Monad, A, B](ma: M[A], mb: M[B]): M[(A, B)] = {
    for {
      a <- ma
      b <- mb
    } yield (a, b)
  }

  def main(args: Array[String]): Unit = {
    println(getPairsList(numbersList, charsList))
    println(getPairs(numberOption, charOption))
    getPairs(numberFuture, charFuture).foreach(println)
    println(getPairsWithFor(numberOption, charOption))
  }
}
