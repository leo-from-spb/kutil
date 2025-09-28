@file:JvmName("IteratorFun")

package lb.kutil.commons.any


fun <T> Iterator<T>.toList(): List<T> {
    val list = ArrayList<T>()
    while (this.hasNext()) list.add(this.next())
    return if (list.isNotEmpty()) list else emptyList<T>()
}
