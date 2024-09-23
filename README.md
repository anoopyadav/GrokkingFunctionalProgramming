### FP basics

Chapter 1-4

#### Imperative vs Declarative

`calculateScore` vs `wordScore`

Imperative: Focus on how it's done
Declarative: Focus on the outcome - what needs to be done

Functional programming is imperative

Naming scheme - use a noun instead of verb to help brain switch to declarative mode

> [!Tip]
> ##### Pure function
>1. Always returns a single value
>2. Calculates return value based solely on arguments
>3. Doesn't mutate existing state/side-effects

#### Referential transparency

Calling a function multiple times with the same argument(s) yields the same result.

> [!Tip]
> ##### Immutable values
> FP relies on immutable values or state to minimize the amount of moving parts.

#### Using immutability

Focus on what needs to be done, not how it'done.
Define relations between the incoming values and values we need to create.

> [!Tip]
> Using _is_ in requirements is important so we can easily encode them as immutable values.
> example - Page 89

#### Functional programming

> [!Quote]
> Functional programming is using _pure_ functions to manipulate _immutable_ values.

> [!Note]
> Functions stored as values are at the core of FP

#### Function building-blocks

##### sortBy

`List[A].sortBy(f: A => B): List[A]`

##### map

`List[A].map(f: A => B): List[B]`

##### filter

`List[A].filter(f: A => Boolean): List[A]`

#### Currying

- With evolving requirements, classic trope is to add args to an existing function
    - Instead, curry the new arg as an addition param-list
    - Add new param lists from left to right
    - Currying (via param-list syntax) avoids manually writing the arrow function syntax in the function body

### Sequential programs

Chapter 5-10

#### Flatten

`.flatMap() == .map().flatten()`

- returns a list of individual elements, removes nesting
- filters out `List.empty`, `None` and `null`
- useful for nested looping - `flatMap` for outer loop and `map` for inner loop

> [!Important]
> `flatten` only removes 1 level of nesting

#### For comprehensions

- Alternative way of doing nested looping
- In this case, `collection1.flatMap()` is the outermost loop
- `collection3.map()` is the innermost loop

> [!Note]
> For comprehension
>```scala
>for {
>	val1 <- collection1
>	val2 <- collection2
>	val3 <- collection3
>} yield doSomething(val1, val2, val3)
>```

This is equivalent to
> [!Note]
> Nested flatMap
>```scala
>collection1.flatMap(val1 => 
>	collection2.flatMap(val2 => 
>		collection3.map(val3 => 
>			doSomething(val1, val2, val3)
>		)
>	)
>)
>```

> [!Important]
> The last enumerator in a for-expression is translated into a `map()`, while all the preceding ones are `flatMap()`

> [!Important]
> The container type of a `for comprehension` is the same as the container type of the first enumerator.

##### Statements vs expressions

- A statement operates on the global state, whereas an expression operates only on the local state.
- A statement needs to change the state of a program to be useful, an expression always returns a result to be used in
  the program.

##### Option type

- Return either `None` or `Some(T)`
- has access to `flatMap()`
- Use inside `for expression` to parse conditionally

##### Filtering

1. `filter` expression
2. `if` statement as a boolean expression
3. function that returns a list

##### Use for-expression to handle failures

> [!Tip]
> For-expression to handle exceptions
>```scala
>for {
>	result1 <- expr1
>	result2 <- expr2
>} yield doSomething(result1, result2)
>```

- `expr1` and `expr2` return `Option` type
- an `Exception` is indicated by a `None` value
- a `None` value short-circuits the remainder of the for-expression, yielding `None`

##### Either

- Same as `Option`, but has two usable values
    - `Left` that can encapsulate error-message
    - `Right` that can encapsulate validated field

> [!Tip]
> **Both `Option` and `Either` values can be be flat-mapped to obtain usable values.**
>
>`Option` uses `toList()` to filter out `None` values.
>
>`Either` uses `toSeq()` to filter out `Left()` values.

> [!Important]
> Example
>```scala
>val test: List[Either[String, Int]] = List(Left("error"), Right(2), Right(3), Left("error"))
>>val test: List[Either[String, Int]] = List(Left(error), Right(2), Right(3), Left(error))
>
>test.map(_.toSeq).flatten
>>val res: List[Int] = List(2, 3)

#### Requirements as Types

A requirement can be one of two types:

1. Behavioral - handled by defining functions
2. Data - handled by modelling `product` with appropriate types

> [!Important]
> FP focuses on **bulletproof modelling** so only valid combinations of data from the business domain are possible,
> cutting out invalid corner cases at compilation time.

##### Using Option type instead of boolean flags

- A boolean flag in combination with a param represents and either/or condition
    - If `true` then do something, if `false` then skip
    - This can be modelled with an `Option` type
- Can check if optional param is passed via `exists()`

##### More higher-order functions

> [!Note]
> `forall` return `true` if all elements satisfy the condition.
> For `Option` types with `None` as value, it short-circuits.


> [!Note]
> `exists` return `true` if atleast one of the elements satisfy the condition.
> For `Option` type with `None` as value, it returns `false`

> [!Important]
> When dealing with `Option` types:
> `exists` will only possibly return `true` for `Some` values.
> `forall` will only possibly return `false` for `Some` values.

##### Algebraic data types (ADTs)

1. Sum type - used to model a finite set of possibilities (+)
2. Product type - used to model all possible combinations of params (cartesian product)

_Sum_ types are modelled using `enum`
> [!Tip]
>```scala
>enum colors {
>	case RED
>	case GREEN
>	case BLUE
>}

_ADTs_ simply combine Sum type with Product type
> [!Tip]
>```scala
>enum Period {
>	case activeSince(start: Int)
>	case activeBetween(start: Int, end: Int)
>}

The multiple cases for ADTs are processed using pattern matching
> [!Tip]
>```scala
>def parsePeriod(period: Period) {
>	period match {
>		case activeSince(start) => ???
>		case activeBetween(start, end) => ???
>	}
>}

##### Opaque/newtype

- Allows defining custom types on top of primitive types

> [!Note]
>```scala
>// Define type
>opaque type Location = String
>// Define mapping
>object Location {
>	def apply(value: String): Location = value
>	// Location values will only be treated as a String here
>	extension (x: Location)
>		def value: String = x 
>}


