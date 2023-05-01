@file:JvmName("CharFun")
package lb.kutil.commons.any


fun Char.replicate(n: Int): String =
    when {
        n == 1 -> this.toString()
        n >= 2 -> buildString(n) { for (i in 1..n) append(this@replicate) }
        else -> ""
    }
