package lb.kutil.commons.text

/**
 * Combines the given string [parts] which are non-null into a phrase of these parts using the specified [delimiter], with [prefix] and [suffix].
 * When no non-null parts it returns the specified [emptyText] without prefixes and suffixes.
 *
 * @param parts string parts, non-null parts will be included in the result phrase, nulls will be skipped.
 * @param delimiter delimiter between parts.
 * @param prefix prefix before the first part.
 * @param suffix suffix after the last part.
 * @param emptyText the text that is returned when no non-null parts.
 */
fun phraseOf(vararg parts: CharSequence?, delimiter: String = " ", prefix: String = "", suffix: String = "", emptyText: String = ""): String =
    phraseOf(parts.asIterable(), delimiter, prefix, suffix, emptyText)

/**
 * Combines the given string [parts] which are non-null into a phrase of these parts using the specified [delimiter], with [prefix] and [suffix].
 * When no non-null parts it returns the specified [emptyText] without prefixes and suffixes.
 *
 * @param parts string parts, non-null parts will be included in the result phrase, nulls will be skipped.
 * @param delimiter delimiter between parts.
 * @param prefix prefix before the first part.
 * @param suffix suffix after the last part.
 * @param emptyText the text that is returned when no non-null parts.
 */
fun phraseOf(parts: Iterable<CharSequence?>, delimiter: String = " ", prefix: String = "", suffix: String = "", emptyText: String = ""): String {
    val b = StringBuilder()
    var was = false
    for (part in parts) {
        if (part != null) {
            b.append(if (was) delimiter else prefix)
            b.append(part)
            was = true
        }
    }
    if (was) b.append(suffix)
    return if (was) b.toString() else emptyText
}


/**
 * Attempts to match this string against the specified pattern.
 * @return the [MatchResult] if successful, or null if not matched.
 */
infix fun CharSequence.match(regex: Regex): MatchResult? =
    regex.matchEntire(this)

/**
 * Matches this string against the specified pattern,
 * and returns the group value specified by the index.
 * @param regex the pattern to match.
 * @param index index of the group that value to return.
 * @return the group value, or null if not matched.
 */
fun CharSequence.extract(regex: Regex, index: Int): String? {
    val m = regex.find(this) ?: return null
    val group: MatchGroup = m.groups[index] ?: return null
    return group.value
}

