package lb.kutil.commons.operations

import lb.kutil.commons.text.TextTable
import lb.kutil.commons.text.TextTable.Align.RIGHT
import lb.kutil.commons.text.TextTable.Column
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Measures time for operations and produces statistics.
 *
 * Every operation has its name.
 *
 * For measuring an operation, use function [measure].
 * For register a metric use [considerDuration].
 *
 * Synchronized.
 */
open class TimeMeter {

    private val operations = ConcurrentHashMap<String, OperationEntry>()
    private val sequence = AtomicInteger()

    protected open fun instantiateOperationEntry(name: String): OperationEntry =
        OperationEntry(sequence.incrementAndGet(), name)

    private fun obtainOperationEntry(name: String): OperationEntry {
        return operations.getOrPut(name) { instantiateOperationEntry(name) }
    }

    /**
     * Adds one operation metric.
     */
    fun considerDuration(operationName: String, duration: Long) {
        val operation = obtainOperationEntry(operationName)
        operation.considerDuration(duration)
    }

    /**
     * Perform the given operation and saves the duration.
     */
    fun <R> measure(operationName: String, operation: () -> R): R {
        val time1: Long = System.currentTimeMillis()
        try {
            return operation()
        }
        finally {
            val time2: Long = System.currentTimeMillis()
            considerDuration(operationName, time2 - time1)
        }
    }

    /**
     * Provides the gathered metrics.
     */
    fun getMetrics(): List<OperationEntry> {
        val list = ArrayList(operations.values)
        sortEntries(list)
        return list
    }

    /**
     * Sorts a list of entries before reporting.
     * By default, it uses the natural order of the entries.
     * An inheritor can override this method in order to use other ordering factors.
     */
    protected open fun sortEntries(list: ArrayList<OperationEntry>) {
        list.sort()
    }


    /**
     * Produces a report that looks like a text table with the following columns:
     * * operation name,
     * * invocation count,
     * * minimal duration time,
     * * average duration time,
     * * maximal duration time,
     * * total time.
     */
    fun produceReport(): CharSequence {
        val entries = getMetrics()
        val tt =
            TextTable<OperationEntry>(arrayOf(
                Column("Operation") { e -> e.name },
                Column("Cnt", RIGHT) { e -> e.count.toString() },
                Column("Min", RIGHT) { e -> e.min.toString() },
                Column("Avg", RIGHT) { e -> e.avg.toString() },
                Column("Max", RIGHT) { e -> e.max.toString() },
                Column("Sum", RIGHT) { e -> e.sum.toString() }
            ))
        return tt.processContent(entries)
    }


    /// OPERATION CLASS \\\

    open class OperationEntry(val order: Int, val name: String) : Comparable<OperationEntry> {

        private val syncLock = Object()

        var count: Int = 0
            private set

        var min: Long = 0L
            private set

        var max: Long = 0L
            private set

        var sum: Long = 0L
            private set

        val avg: Long
            get() {
                synchronized(syncLock) {
                    val cnt = count
                    if (cnt == 0) return 0
                    return (sum + (cnt / 2)) / cnt
                }
            }

        internal fun considerDuration(duration: Long) {
            synchronized(syncLock) {
                if (count == 0) {
                    count++
                    min = duration
                    max = duration
                    sum = duration
                }
                else {
                    count++
                    if (duration < min) min = duration
                    if (duration > max) max = duration
                    sum += duration
                }
            }
        }

        override fun compareTo(other: OperationEntry): Int {
            var z = this.order compareTo other.order
            if (z != 0) return z
            z = this.name compareTo other.name
            if (z != 0) return z
            z = this.count compareTo other.count
            if (z != 0) return z
            z = this.min compareTo other.min
            if (z != 0) return z
            z = this.max compareTo other.max
            if (z != 0) return z
            z = this.sum compareTo other.sum
            return z
        }

    }



}
