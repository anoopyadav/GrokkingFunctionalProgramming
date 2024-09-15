object ch07 extends App {
  //  case class Genre(name: String)
  //  case class Active(start: Int, end: Int)
  //  case class Location(name: String)
  //  case class Artist(name: String, genre: Genre, origin: Location, active: Active)

  // Type wrappers
  private object model {
    opaque type Location = String
    opaque type Genre = String
    opaque type Year = Int

    object Location {
      def apply(value: String): Location = value

      extension (a: Location)
        def name: String = a
    }

    object Genre {
      def apply(value: String): Genre = value

      extension (a: Genre)
        def name: String = a
    }

    object Year {
      def apply(value: Int): Year = value

      extension (a: Year)
        def value: Int = a
    }

    case class PeriodInYears(start: Year, end: Option[Year])

    case class Artist(name: String, genre: Genre, origin: Location, yearsActive: PeriodInYears)
  }
  {
    import model.*

    def filterByGenre(artists: List[Artist], filter: List[String]): List[Artist] =
      if filter.isEmpty then artists else artists.filter(artist => filter.contains(artist.genre.name))

    def filterByLocation(artists: List[Artist], filter: List[String]): List[Artist] =
      if filter.isEmpty then artists else artists.filter(artist => filter.contains(artist.origin.name))

    def filterByYears(artists: List[Artist], yearsStart: Int, yearsEnd: Int): List[Artist] =
      artists.filter(artist => artist.yearsActive.start.value <= yearsEnd && artist.yearsActive.end.forall(_.value >= yearsStart))

    def searchArtists(
                       artists: List[Artist],
                       genres: List[String], locations: List[String],
                       searchByActiveYears: Boolean, activeAfter: Int,
                       activeBefore: Int
                     ): List[Artist] =
      val byGenre = filterByGenre(artists, genres)
      val byLocation = filterByLocation(byGenre, locations)
      if searchByActiveYears then filterByYears(byLocation, activeAfter, activeBefore) else byLocation

    // Data
    val metallica = Artist("Metallica", Genre("Heavy Metal"), Location("U.S."), PeriodInYears(Year(1981), None))
    val ledZeppelin = Artist("Led Zeppelin", Genre("Hard Rock"), Location("England"), PeriodInYears(Year(1968), Some(Year(1980))))
    val beeGees = Artist("Bee Gees", Genre("Pop"), Location("England"), PeriodInYears(Year(1958), Some(Year(2003))))
    val artists = List(metallica, ledZeppelin, beeGees)

    // Tests
    assert(searchArtists(artists, List("Pop"), List("England"), true, 1950, 2022) == List(beeGees))
    assert(searchArtists(artists, List.empty, List("England"), true, 1950, 2022) == List(ledZeppelin, beeGees))
    assert(searchArtists(artists, List.empty, List.empty, true, 1981, 2003) == List(metallica, beeGees))
    assert(searchArtists(artists, List.empty, List("U.S."), false, 0, 0) == List(metallica))
    assert(searchArtists(artists, List.empty, List.empty, false, 2019, 2022) == List(metallica, ledZeppelin, beeGees))
  }
  {
    // coffee break
    case class User(name: String, city: Option[String], favoriteArtists: List[String])

    def usersInMelbourneOrNowhere(users: List[User]): List[User] = users.filter(_.city.forall(_.contains("Melbourne")))

    def usersInLagos(users: List[User]): List[User] = users.filter(_.city.contains("Lagos"))

    def usersFansOfBeeGees(users: List[User]): List[User] = users.filter(_.favoriteArtists.contains("BeeGees"))

    def usersLivingInT(users: List[User]): List[User] = users.filter(_.city.exists(_.startsWith("T")))

    def usersFansOfLongNames(users: List[User]): List[User] = users.filter(_.favoriteArtists.forall(_.length > 8))

    def usersFansOfSomeM(users: List[User]): List[User] = users.filter(_.favoriteArtists.exists(_.startsWith("M")))

    val alice = User("Alice", Some("Melbourne"), List("BeeGees"))
    val bob = User("Bob", Some("Lagos"), List("BeeGees"))
    val eve = User("Eve", Some("Tokyo"), List.empty)
    val mal = User("Mallory", None, List("Metallica", "BeeGees"))
    val trent = User("Trent", Some("Buenos Aires"), List("Led Zeppelin"))
    val users = List(alice, bob, eve, mal, trent)

    // Tests
    assert(usersInMelbourneOrNowhere(users) == List(alice, mal))
    assert(usersInLagos(users) == List(bob))
    assert(usersFansOfBeeGees(users) == List(alice, bob, mal))
    assert(usersLivingInT(users) == List(eve))
    assert(usersFansOfLongNames(users) == List(eve, trent))
    assert(usersFansOfSomeM(users) == List(mal))
  }

  // Use ADTs
  private object modelV2 {
    enum Genre {
      case HeavyMetal
      case Pop
      case HardRock
    }

    opaque type Location = String

    object Location {
      def apply(value: String): Location = value

      extension (x: Location)
        def value: String = x
    }

    enum Period {
      case activeBetween(start: Int, end: Int)

      case activeSince(start: Int)
    }

    case class Artist(name: String, genre: Genre, origin: Location, period: Period)
  }
  {
    import modelV2.*

    val metallica = Artist("Metallica", Genre.HeavyMetal, Location("U.S."), Period.activeSince(1981))
    val ledZeppelin = Artist("Led Zeppelin", Genre.HardRock, Location("England"), Period.activeBetween(1968, 1980))
    val beeGees = Artist("Bee Gees", Genre.Pop, Location("England"), Period.activeBetween(1958, 2003))
    val artists = List(metallica, ledZeppelin, beeGees)

    def wasArtistActive(period: Period, activeAfter: Int, activeBefore: Int): Boolean =
      period match {
        case Period.activeSince(start) => start <= activeBefore
        case Period.activeBetween(start, end) => start <= activeBefore && end >= activeAfter
      }

    def searchArtists(
                       artists: List[Artist],
                       genres: List[Genre],
                       locations: List[String],
                       searchByActiveYears: Boolean,
                       activeAfter: Int,
                       activeBefore: Int
                     ) = {
      artists.filter(artist => genres.isEmpty || genres.contains(artist.genre))
        .filter(artist => locations.isEmpty || locations.contains(artist.origin.value))
        .filter(artist => !searchByActiveYears || wasArtistActive(artist.period, activeAfter, activeBefore))
    }

    assert(searchArtists(artists, List(Genre.Pop), List("England"), true, 1950, 2022) == List(beeGees))
    assert(searchArtists(artists, List.empty, List("England"), true, 1950, 2022) == List(ledZeppelin, beeGees))
    assert(searchArtists(artists, List.empty, List.empty, true, 1981, 2003) == List(metallica, beeGees))
    assert(searchArtists(artists, List.empty, List("U.S."), false, 0, 0) == List(metallica))
    assert(searchArtists(artists, List.empty, List.empty, false, 2019, 2022) == List(metallica, ledZeppelin, beeGees))
  }
}
