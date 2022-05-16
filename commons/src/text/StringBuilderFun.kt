@file:JvmName("StringBuilderFun")

package lb.kutil.commons.text


fun StringBuilder.appendIfNotNull(s: CharSequence?): StringBuilder {
    if (s != null) this.append(s)
    return this
}

fun StringBuilder.appendIfNotNull(x: Any?): StringBuilder {
    if (x != null) this.append(x.toString())
    return this
}


fun StringBuilder.appendIfNotEmpty(c: Char): StringBuilder {
    if (this.isNotEmpty()) this.append(c)
    return this
}

fun StringBuilder.appendIfNotEmpty(s: String): StringBuilder {
    if (this.isNotEmpty()) this.append(s)
    return this
}

fun StringBuilder.appendIf(condition: Boolean, c: Char): StringBuilder {
    if (condition) this.append(c)
    return this
}

fun StringBuilder.appendIf(condition: Boolean, str: CharSequence): StringBuilder {
    if (condition) this.append(str)
    return this
}

fun StringBuilder.appendIf(condition: Boolean, str: CharSequence, start: Int, end: Int): StringBuilder {
    if (condition) this.append(str, start, end)
    return this
}
