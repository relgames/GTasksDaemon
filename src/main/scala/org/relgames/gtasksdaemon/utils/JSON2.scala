package org.relgames.gtasksdaemon.utils

import util.parsing.combinator.JavaTokenParsers

object JSON2 extends JavaTokenParsers {
        def obj: Parser[Map[String, Any]] =
                "{"~> repsep(member, ",") <~"}" ^^ (Map() ++ _)

        def arr: Parser[List[Any]] =
                "["~> repsep(value, ",") <~"]"

        def member: Parser[(String, Any)] =
                stringLiteral~":"~value ^^
                        { case name~":"~value => (name, value) }

        def value: Parser[Any] = (
                obj
                | arr
                | stringLiteral
                | floatingPointNumber ^^ (_.toInt)
                | "null" ^^ (x => null)
                | "true" ^^ (x => true)
                | "false" ^^ (x => false)
                )

        def fromString(s: String) = parseAll(value, s)
}
