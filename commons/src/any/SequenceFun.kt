@file:JvmName("SequenceFun")

package lb.kutil.commons.any


/**
 * Collects the sequence values to an array.
 */
inline fun<reified T> Sequence<T>.toArray(): Array<T> = this.toList().toTypedArray()
