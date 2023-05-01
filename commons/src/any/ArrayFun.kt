@file:JvmName("ArrayFun")

package lb.kutil.commons.any


/**
 * Performs the given [action] on each element in the revers order.
 */
inline fun <T> Array<out T>.forEachReversed(action: (T) -> Unit) {
    for (i in this.lastIndex downTo 0) action(this[i])
}
