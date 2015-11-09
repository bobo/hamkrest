package org.hamcrest.kotlin


public fun <T> delimit(v: T): String {
    return when (v) {
        is String -> "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
        else -> v.toString()
    }
}

public fun match(comparisonResult: Boolean, describeMismatch: () -> String): MatchResult =
        if (comparisonResult) {
            MatchResult.Match
        } else {
            MatchResult.Mismatch(describeMismatch())
        }


public sealed class MatchResult {
    object Match : MatchResult();

    class Mismatch(private val description: String) : MatchResult() {
        fun description(): String {
            return description;
        }
    }
}


public sealed class Matcher<in T> : (T) -> MatchResult {
    public abstract fun description(): String;
    public open fun negatedDescription(): String = "not " + description();

    public open operator fun not(): Matcher<T> {
        return Negation(this);
    }

    public class Negation<in T>(private val negated: Matcher<T>) : Matcher<T>() {
        override fun invoke(actual: T): MatchResult =
                when (negated.invoke(actual)) {
                    MatchResult.Match -> {
                        MatchResult.Mismatch("was " + negated.description())
                    }
                    is MatchResult.Mismatch -> {
                        MatchResult.Match
                    }
                }

        override fun description(): String = negated.negatedDescription()
        override fun negatedDescription(): String = negated.description()
    }

    public abstract class Primitive<in T> : Matcher<T>()
}