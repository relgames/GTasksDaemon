package book.scala.beginning

import scala.util.parsing.combinator._

object JSON extends RegexParsers with RunParser {
  // translation from ECMAScript spec
  // http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-262.pdf

  /*
   Whitespace
   */
  lazy val spaces: Parser[String] = """\s*""".r

  /*
   Source characters (any valid unicode character)
   */
  lazy val sourceCharacter: Parser[Char] = elem("Source Character", c => true)

  /*
   HexDigit :: one of
   0 1 2 3 4 5 6 7 8 9 a b c d e f A B C D E F
   */
  lazy val hexDigit: Parser[Char] =
  elem("Hex Digit", c => ((c >= '0' && c <= '9') ||
                          (c >= 'a' && c <= 'f') ||
                          (c >= 'A' && c <= 'F')))

  /*
   7.8 Literals
   Syntax
   Literal ::
   NullLiteral
   BooleanLiteral
   NumericLiteral
   StringLiteral
   */
  lazy val literal: Parser[Any] =
  nullLiteral |
  booleanLiteral |
  numericLiteral |
  stringLiteral

  /*
   7.8.1 Null Literals
   Syntax
   NullLiteral ::
   null
   */
  lazy val nullLiteral: Parser[Any] = spaces ~ "null" ~ spaces ^^^ None

  /*
   7.8.2 Boolean Literals
   Syntax
   BooleanLiteral ::
   true
   false
   */
  lazy val booleanLiteral: Parser[Boolean] = spaces ~>
  ("true" ^^^ true | "false" ^^^ false) <~ spaces

  /*
   7.8.3 Numeric Literals
   Syntax
   NumericLiteral ::
   DecimalLiteral
   HexIntegerLiteral
   */
  lazy val numericLiteral: Parser[Double] = spaces ~>
  (hexIntegerLiteral | decimalLiteral) <~ spaces

  /*
   DecimalLiteral ::
   DecimalIntegerLiteral . DecimalDigits(opt) ExponentPart(opt)
   . DecimalDigits ExponentPart(opt)
   DecimalIntegerLiteral ExponentPart(opt)
   */
  lazy val decimalLiteral: Parser[Double] =
  (decimalIntegerLiteral ~ '.' ~ opt(decimalDigits) ~ opt(exponentPart)) ^^
  {case lit ~ _ ~ frac ~ optExp =>
      val d: Double = frac.map(f =>
        (lit.toString + "." + f.mkString).toDouble) getOrElse  lit.toDouble
      optExp.map(_(d)) getOrElse d
  }  |
  '.' ~> decimalDigits ~ opt(exponentPart) ^^ {
    case dd ~ optExp =>
      val d = ("." + dd.mkString).toDouble
      optExp.map(_(d)) getOrElse d
  } |
  decimalIntegerLiteral ~ opt(exponentPart) ^^ {
    case dd ~ optExp =>
      optExp.map(_(dd)) getOrElse dd
  }

  /*
   DecimalIntegerLiteral ::
   0
   NonZeroDigit DecimalDigits(opt)
   */
  lazy val decimalIntegerLiteral: Parser[Long] = '0' ^^^ 0L |
  nonZeroDigit ~ opt(decimalDigits) ^^ {
    case first ~ rest => (first :: (rest getOrElse Nil)).mkString.toLong
  }

  /*
   DecimalDigits ::
   DecimalDigit
   DecimalDigits DecimalDigit
   */
  lazy val decimalDigits: Parser[List[Char]] = rep1(decimalDigit)

  /*
   DecimalDigit :: one of
   0 1 2 3 4 5 6 7 8 9
   */
  lazy val decimalDigit = elem("Decimal Digit", c => c >= '0' && c <= '9')

  /*
   NonZeroDigit :: one of
   1 2 3 4 5 6 7 8 9
   */
  lazy val nonZeroDigit = elem("Non-zero Digit", c => c >= '1' && c <= '9')

  /*
   ExponentPart ::
   ExponentIndicator SignedInteger
   */
  lazy val exponentPart: Parser[Double => Double] =
  exponentIndicator ~> signedInteger ^^ {
    si => n => n.doubleValue * Math.pow(10.0, si.doubleValue)
  }

  /*
   ExponentIndicator :: one of
   e E
   */
  lazy val exponentIndicator = elem("exp ind", c => c == 'e' || c == 'E')

  /*
   SignedInteger ::
   DecimalDigits
   + DecimalDigits
   - DecimalDigits
   */
  lazy val signedInteger: Parser[Long] =
  decimalDigits ^^ (_.mkString.toLong) |
  '+' ~> decimalDigits ^^ (_.mkString.toLong) |
  '-' ~> decimalDigits ^^ (_.mkString.toLong * -1L)

  /*
   HexIntegerLiteral ::
   0x HexDigit
   0X HexDigit
   HexIntegerLiteral HexDigit
   */
  lazy val hexIntegerLiteral: Parser[Double] =
  (elem('0') ~ (elem('x') | 'X')) ~> rep1(hexDigit) ^^
  (s => java.lang.Long.parseLong(s.mkString, 16).toDouble)

  /*
   7.8.4 String Literals
   A string literal is zero or more characters enclosed in single or double quotes.
   Each character may be represented by an escape sequence.
   Syntax
   StringLiteral ::
   " DoubleStringCharacters(opt) "
   ' SingleStringCharacters(opt) '
   */
  lazy val stringLiteral: Parser[String] =
  '"' ~> opt(doubleStringCharacters) <~ '"'  ^^ (_ getOrElse "") |
  '\'' ~> opt(singleStringCharacters) <~ '\'' ^^ (_ getOrElse "")

  /*
   DoubleStringCharacters ::
   DoubleStringCharacter DoubleStringCharacters(opt)
   */
  lazy val doubleStringCharacters: Parser[String] =
  rep1(doubleStringCharacter) ^^ (_.mkString)

  /*
   SingleStringCharacters ::
   SingleStringCharacter SingleStringCharacters(opt)
   */
  lazy val singleStringCharacters: Parser[String] =
  rep1(singleStringCharacter) ^^ (_.mkString)

  /*   DoubleStringCharacter ::
   SourceCharacter but not double-quote " or backslash \ or LineTerminator
   \ EscapeSequence
   */
  lazy val doubleStringCharacter: Parser[Char] =
  ('\\' ~> escapeSequence) |
  ((not('"') ~ not('\\') ~ not(lineTerminator)) ~> sourceCharacter)

  /*
   LineTerminator ::
   <LF>
   <CR>
   <LS>
   <PS>
   */
  lazy val lineTerminator = elem("Line Terminator",
                                 c => (c == '\r' ||
                                       c == '\n' ||
                                       c == '\u2028' ||
                                       c == '\u2029'))

  /*
   SingleStringCharacter ::
   SourceCharacter but not single-quote ' or backslash \ or LineTerminator
   \ EscapeSequence
   */
  lazy val singleStringCharacter: Parser[Char] =
  ('\\' ~> escapeSequence) |
  ((not('\'') ~ not('\\') ~ not(lineTerminator)) ~> sourceCharacter)

  /*
   EscapeSequence ::
   CharacterEscapeSequence
   HexEscapeSequence
   UnicodeEscapeSequence
   */
  lazy val escapeSequence: Parser[Char] =
  characterEscapeSequence | hexEscapeSequence | unicodeEscapeSequence

  /*
   CharacterEscapeSequence ::
   SingleEscapeCharacter
   NonEscapeCharacter
   */
  lazy val characterEscapeSequence: Parser[Char] =
  singleEscapeCharacter | nonEscapeCharacter

  /*
   SingleEscapeCharacter :: one of
   ' " \ b f n r t
   */
  lazy val singleEscapeCharacter: Parser[Char] =
  '\'' ^^^ '\'' |
  '"' ^^^ '"' |
  '\\' ^^^ '\\' |
  'b' ^^^ '\b' |
  'f' ^^^ '\f' |
  'n' ^^^ '\n' |
  'r' ^^^ '\r' |
  't' ^^^ '\t'

  /*
   NonEscapeCharacter ::
   SourceCharacter but not EscapeCharacter or LineTerminator
   */
  lazy val nonEscapeCharacter: Parser[Char] =
  (not(escapeCharacter) ~ not(lineTerminator) ~> sourceCharacter)

  /*
   EscapeCharacter ::
   SingleEscapeCharacter
   DecimalDigit
   x
   u
   */
  lazy val escapeCharacter: Parser[Unit] =
  (singleEscapeCharacter | decimalDigit | 'x' | 'u') ^^^ ()

  /*
   HexEscapeSequence ::
   x HexDigit HexDigit
   */
  lazy val hexEscapeSequence: Parser[Char] =
  'x' ~> hexDigit ~ hexDigit ^^
  {case d1 ~ d2 => Integer.parseInt(d1.toString + d2, 16).toChar}

  /*
   UnicodeEscapeSequence ::
   u HexDigit HexDigit HexDigit HexDigit
   */
  lazy val unicodeEscapeSequence =
  'u' ~> hexDigit ~ hexDigit ~ hexDigit ~ hexDigit ^^
  {case d1 ~ d2 ~ d3 ~ d4 =>
      Integer.parseInt(d1.toString + d2 + d3 + d4, 16).toChar}

  /*
   ArrayLiteral :
   [ Elision(opt) ]
   [ ElementList ]
   [ ElementList , Elision(opt) ]
   */
  lazy val arrayLiteral: Parser[List[Any]] =
  spaces ~ '[' ~ spaces ~> elementList <~
  spaces ~ opt(elision) ~ spaces ~ ']' ~ spaces

  /*
   ElementList :
   Elision(opt) AssignmentExpression
   ElementList , Elision(opt) AssignmentExpression
   */
  lazy val elementList: Parser[List[Any]] =
  repsep(spaces ~> jsonObject, elision)

  /*
   Elision :
   ,
   Elision ,
   */
  lazy val elision: Parser[Unit] =
  rep1(spaces ~ ',' ~ spaces) ^^^ ()

  /*
   ObjectLiteral :
   { }
   { PropertyNameAndValueList }
   */
  lazy val objectLiteral: Parser[Map[String, Any]] =
  spaces ~ '{' ~ spaces ~ '}' ~ spaces ^^^ Map[String, Any]() |
  spaces ~ '{' ~ spaces ~> propertyNameAndValueList <~
  spaces ~'}' ~ spaces ^^ (vl => Map(vl :_*))

  /*
   PropertyNameAndValueList :
   PropertyName : AssignmentExpression
   PropertyNameAndValueList , PropertyName : AssignmentExpression
   */
  lazy val propertyNameAndValueList:Parser[List[(String, Any)]] =
  rep1sep((spaces ~> propertyName) ~
          (spaces ~ ':' ~ spaces ~> jsonObject) ^^ {
      case n ~ v => (n, v)},
          spaces ~ ',' ~ spaces)

  /*
   PropertyName :
   Identifier
   StringLiteral
   NumericLiteral
   */
  lazy val propertyName: Parser[String] = stringLiteral |
  numericLiteral ^^ (_.longValue.toString) | identifier

  /*
   IdentifierName ::
   IdentifierStart
   IdentifierName IdentifierPart
   */
  lazy val identifier: Parser[String] =
  identifierStart |
  identifier ~ identifierPart ^^ {
    case a ~ b => a+b
  }

  /*
   IdentifierStart ::
   UnicodeLetter
   $
   _
   */
  lazy val identifierStart: Parser[String] =
  '$' ^^^ "$" | '_' ^^^ "_" | unicodeLetter ^^ (_.toString)

  /*
   IdentifierPart ::
   IdentifierStart
   UnicodeDigit
   */
  lazy val identifierPart: Parser[String] =
  identifierStart | unicodeDigit ^^ (_.toString)

  lazy val unicodeLetter = elem("Letter", Character.isLetter)

  lazy val unicodeDigit = elem("Letter", Character.isDigit)

  lazy val jsonObject: Parser[Any] = objectLiteral | arrayLiteral | literal

  type RootType = Any

  def root = jsonObject
}

trait RunParser {
  this: RegexParsers =>

  type RootType

  def root: Parser[RootType]

  def run(in: String): ParseResult[RootType] = parseAll(root, in)
}
