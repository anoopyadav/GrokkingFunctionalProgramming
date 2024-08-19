object ch06 extends App {
  case class TvShow(title: String, start: Int, end: Int)

  def sortShows(shows: List[TvShow]): List[TvShow] = {
    shows.sortBy(show => show.end - show.start).reverse
  }

  def parseShows(shows: List[String]): List[Option[TvShow]] =
    shows.map(parseShow)

  def extractName(rawShow: String): Option[String] =
    for {
      bracketOpen <- Some(rawShow.indexOf('('))
      name <- if bracketOpen == -1 then None else Some(rawShow.substring(0, bracketOpen).trim)
    } yield name

  def extractStart(rawShow: String): Option[Int] =
    for {
      bracketOpen <- Some(rawShow.indexOf('('))
      dash <- Some(rawShow.indexOf('-'))
      startYearString <- if bracketOpen == -1 || dash == -1 || dash < bracketOpen then None else Some(rawShow.substring(bracketOpen + 1, dash).trim)
      startYearInt <- startYearString.toIntOption
    } yield startYearInt

  def extractEnd(rawShow: String): Option[Int] =
    for {
      dash <- Some(rawShow.indexOf('-'))
      bracketClose <- Some(rawShow.indexOf(')'))
      endYearString <- if dash == -1 || bracketClose == -1 || dash > bracketClose then None else Some(rawShow.substring(dash + 1, bracketClose).trim)
      endYearInt <- endYearString.toIntOption
    } yield endYearInt

  def parseShow(show: String): Option[TvShow] =
    for {
      title <- extractName(show)
      start <- extractStart(show)
      end <- extractEnd(show)
    } yield TvShow(title, start, end)

  //////////
  // Tests//
  //////////
  val shows = List(
    TvShow("Breaking Bad", 2008, 2013),
    TvShow("The Wire", 2002, 2008),
    TvShow("Mad Men", 2007, 2015)
  )
  assert(
    sortShows(shows) ==
      List(
        TvShow("Mad Men", 2007, 2015),
        TvShow("The Wire", 2002, 2008),
        TvShow("Breaking Bad", 2008, 2013)
      )
  )

  // Test parseShow
  assert(parseShow("Breaking Bad (2008-2013)") == Option(TvShow("Breaking Bad", 2008, 2013)))

  val rawShows = List(
    "Breaking Bad (2008 - 2013)",
    "The Wire (2002 - 2008)",
    "Mad Men (2007 - 2015)"
  )
  assert(parseShows(rawShows) ==
    List(
      Option(TvShow("Breaking Bad", 2008, 2013)),
      Option(TvShow("The Wire", 2002, 2008)),
      Option(TvShow("Mad Men", 2007, 2015))
    )
  )

  assert(parseShow("Stranger Things (2016-)") == None)
  assert(parseShow("Chernobyl (2019)") == None)
}
