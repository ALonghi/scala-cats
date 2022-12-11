package part2abstractMath

import scala.util.{ Failure, Success, Try }

object UsingMonads {

  // applicable to Option, Try, Future

  import cats.Monad
  import cats.instances.list._

  val monadList = Monad[List] // fetch the implicit Monad[List]
  val aSimpleList = monadList.pure(2) // List(2)
  val anExtendedList = monadList.flatMap(aSimpleList)(x => List(x, x + 1))

  // either is also a monad
  val aManualEither: Either[String, Int] = Right(42)
  type LoadingOr[T] = Either[String, T]
  type ErrorOr[T] = Either[Throwable, T]

  import cats.instances.either._
  val loadingMonad = Monad[LoadingOr]

  val anEither = loadingMonad.pure(45)
  val aChangedLoading = loadingMonad.flatMap(anEither)(n =>
    if (n % 2 == 0) Right(n + 1)
    else Left("Loading the meaning of life.."))

  // imaginary online store
  case class OrderStatus(orderId: Long, status: String)

  def getOrderStatus(orderId: Long): LoadingOr[OrderStatus] =
    Right(OrderStatus(orderId, "Ready to ship"))
  def trackLocation(orderStatus: OrderStatus): LoadingOr[String] =
    if (orderStatus.orderId > 1000) Left("Not available yet..")
    else Right("Amsterdam, NL")

  val orderId = 457L
  val orderLocation = loadingMonad.flatMap(getOrderStatus(orderId))(trackLocation)

  // extension methods
  val orderLocationBetter = getOrderStatus(orderId).flatMap(trackLocation)
  val orderLocationFor = for {
    orderStatus <- getOrderStatus(orderId)
    location <- trackLocation(orderStatus)
  } yield location

  // TODO: the service layer API of a web app
  case class Connection(host: String, port: String)
  val config = Map(
    "host" -> "localhost",
    "port" -> "4040"
  )

  trait HttpService[M[_]] {
    def getConnection(cfg: Map[String, String]): M[Connection]
    def issueRequest(connection: Connection, payload: String): M[String]
  }

  import cats.syntax.flatMap._
  import cats.syntax.functor._
  def getResponse[M[_]](service: HttpService[M], payload: String)(implicit monad: Monad[M]): M[String] =
    for {
      conn <- service.getConnection(config)
      response <- service.issueRequest(conn, payload)
    } yield response

  object TryHttpService extends HttpService[Try] {
    override def getConnection(cfg: Map[String, String]): Try[Connection] =
      (cfg.get("host"), cfg.get("port")) match {
        case (Some(h), Some(p)) => Success(Connection(h, p))
        case _                  => Failure(new Exception("Did not find required host and port"))
      }

    override def issueRequest(connection: Connection, payload: String): Try[String] =
      if (payload.length > 20) Success("Payload accepted")
      else Failure(new Exception("Payload too short"))
  }

  val responseTry =
    TryHttpService.getConnection(config).flatMap { conn => TryHttpService.issueRequest(conn, "Hello, HTTP service") }
  val responseTryFor = for {
    conn <- TryHttpService.getConnection(config)
    response <- TryHttpService.issueRequest(conn, "Hello, HTTP service")
  } yield response

  // TODO implement another HttpService with LoadingOr or ErrorOr (type aliases for Either)
  object AggressiveHttpService extends HttpService[ErrorOr] {
    override def getConnection(cfg: Map[String, String]): ErrorOr[Connection] =
      if (!cfg.contains("host") || !cfg.contains("port")) {
        Left(new RuntimeException("Connection could not be established: invalid configuration"))
      } else {
        Right(Connection(cfg("host"), cfg("port")))
      }

    override def issueRequest(connection: Connection, payload: String): ErrorOr[String] =
      if (payload.length >= 20) Left(new RuntimeException("Payload is too large"))
      else Right(s"Request ($payload) was accepted")
  }

  val errorOrResponse: ErrorOr[String] = for {
    conn <- AggressiveHttpService.getConnection(config)
    response <- AggressiveHttpService.issueRequest(conn, "Hello ErrorOr")
  } yield response

  // 04:55
  def main(args: Array[String]): Unit = {
    println(getResponse(TryHttpService, "Hello Option"))
    println(getResponse(AggressiveHttpService, "Hello, ErrorOr"))
  }
}
