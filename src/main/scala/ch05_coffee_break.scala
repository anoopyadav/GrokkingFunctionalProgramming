object ch05_coffee_break extends App {
  // Class definitions
  case class Book(title: String, authors: List[String])

  case class Movie(title: String)

  case class Point2d(x: Int, y: Int)

  case class Point3d(x: Int, y: Int, z: Int)

  // Functions
  def bookAdaptations(author: String): List[Movie] = {
    if author == "Tolkien" then
      List(Movie("An unexpected journey"), Movie("The desolation of Smaug"))
    else
      List.empty
  }

  // Get movie recommendations based on books
  def recommendationFeedViaNesting(books: List[Book]) = books.flatMap(book =>
    book.authors.flatMap(author =>
      bookAdaptations(author).map(movie =>
        s"You may like ${movie.title} because you liked ${author}'s book ${book.title}"
      )
    )
  )

  def recommendationFeedViaForComprehension(books: List[Book]): List[String] =
    for {
      book <- books
      author <- book.authors
      movie <- bookAdaptations(author)
    } yield s"You may like ${movie.title} because you liked ${author}'s book ${book.title}"

  // check if point is inside a circle
  def isInside(point: Point2d, radius: Int): Boolean = {
    radius * radius >= point.x * point.x + point.y * point.y
  }

  // the program
  val books = List(
    Book("FP in Scala", List("Chiusano", "Bjarnason")),
    Book("The Hobbit", List("Tolkien")),
    Book("Modern Java in Action", List("Urma", "Fusco", "Mycroft"))
  )

  // Get books with scala in the title
  assert(books.map(_.title).filter(_.contains("Scala")).size == 1)
  assert(
    recommendationFeedViaForComprehension(books) ==
      List(
        "You may like An unexpected journey because you liked Tolkien's book The Hobbit",
        "You may like The desolation of Smaug because you liked Tolkien's book The Hobbit"
      )
  )
  for {
    x <- List(1)
    y <- List(-2, 7)
  } yield Point2d(x, y)

  // 2D points
  assert(
    (
      for {
        x <- List(1)
        y <- List(-2, 7)
      } yield Point2d(x, y)
      )
      == List(Point2d(1, -2), Point2d(1, 7))
  )

  // 3d points
  assert(
    (
      for {
        x <- List(1)
        y <- List(-2, 7)
        z <- List(3, 4)
      } yield Point3d(x, y, z)
      ) == List(Point3d(1, -2, 3), Point3d(1, -2, 4), Point3d(1, 7, 3), Point3d(1, 7, 4))
  )

  // Given list of radii, check if the given points are inside the circle
  val radii = List(2, 1)
  val points = List(Point2d(5, 2), Point2d(1, 1))

  val results = for {
    radius <- radii
    point <- points
  } yield isInside(point, radius)

  assert(results == List(false, true, false, false))

  // Return only points that are inside the circle
  val resultPoints = for {
    radius <- radii
    point <- points if isInside(point, radius)
  } yield point

  assert(resultPoints == List(Point2d(1, 1)))

  // Filtering for invalid values
  val riskyRadii = List(-10, 0, 2)
  val riskyPointsFilteredOut = for {
    radius <- riskyRadii if radius > 0
    point <- points if isInside(point, radius)
  } yield point

  assert(riskyPointsFilteredOut == List(Point2d(1, 1)))

  // Filter using a function
  def insideFilter(point: Point2d, radius: Int): List[Point2d] =
    if isInside(point, radius) then List(point) else List.empty

  val riskyPointsFilteredUsingFunction = for {
    radius <- riskyRadii if radius > 0
    point <- points
    insidePoint <- insideFilter(point, radius)
  } yield insidePoint

  assert(riskyPointsFilteredUsingFunction == List(Point2d(1, 1)))
}
