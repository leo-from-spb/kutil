@file:Suppress("KotlinConstantConditions")

package lb.kutil.commons.text

import lb.kutil.commons.any.appendIf
import lb.kutil.commons.any.appendIfNotNull
import lb.kutil.commons.any.replicate
import java.lang.Integer.max

/**
 * Makes a text table.
 */
class TextTable<R> (val columns: Array<Column<R>>) {

    enum class Align {
        LEFT,
        RIGHT
    }

    data class Grid (
        val topLine: String? = "==",
        val headLine: String? = "--",
        val bottomLine: String? = "--",
        val cellGap: String = "  ",
        val leftLine: String = "",
        val rightLine: String = ""
    )

    data class Look (
        var grid: Grid = Grid(),
        var tableIndent: String? = null
    )

    class Column<R> (val title: String?, val alignment: Align = Align.LEFT, val fetcher: (R) -> CharSequence?) {
        var width = title?.length ?: 0
    }


    val look = Look()

    val columnCnt = columns.size



    fun processContent(content: Iterable<R>): CharSequence {
        assert(columnCnt > 0) { "A table must have at least one column" }

        val grid = look.grid

        // compute column widths
        var rowCnt = 0
        val columnWidths = IntArray(columnCnt) { i -> columns[i].width }
        for (row in content) {
            rowCnt++
            for ((i, column) in columns.withIndex()) {
                val cellStr = column.fetcher(row)
                if (cellStr != null) {
                    val w = calculateTextWidth(cellStr)
                    if (columnWidths[i] < w) columnWidths[i] = w
                }
            }
        }

        // compute sizes
        val columnWidthSum: Int = columnWidths.reduce { m, w -> m + w }
        val longestCellWidth = columnWidths.reduce { m, w -> max(m, w) }
        val tableWidth = columnWidthSum + (columnCnt - 1)*grid.cellGap.length + grid.leftLine.length + grid.rightLine.length
        val tableWidthWithIndent = tableWidth + (look.tableIndent?.length ?: 0)
        val tableSize: Int = tableWidthWithIndent * (rowCnt + 4)
        val hasHeader = columns.any { it.title != null }

        // lines
        val topLine = grid.topLine?.replicateTillWidth(tableWidth)
        val headLine = grid.headLine?.replicateTillWidth(tableWidth)
        val bottomLine = grid.bottomLine?.replicateTillWidth(tableWidth)

        val spaces = ' '.replicate(longestCellWidth)

        // print header
        val b = StringBuilder(tableSize)
        if (topLine != null) {
            b.indent().append(topLine).appendLine()
        }
        if (hasHeader) {
            b.indent()
            b.append(grid.leftLine)
            for ((i, column) in columns.withIndex()) {
                val width = columnWidths[i]
                if (i > 0) b.appendIfNotNull(grid.cellGap)
                val h = (column.title ?: " ")
                val hp = when (column.alignment) {
                    Align.LEFT -> h.padEnd(width)
                    Align.RIGHT -> h.padStart(width)
                }
                b.append(hp)
            }
            b.append(grid.rightLine)
            b.appendLine()
            b.indent().appendIfNotNull(headLine).appendLine()
        }

        // print the main content
        for (row in content) {
            b.indent()
            b.append(grid.leftLine)
            for ((i, column) in columns.withIndex()) {
                if (i > 0) b.appendIfNotNull(grid.cellGap)
                val width = columnWidths[i]
                val x = column.fetcher(row)
                if (x != null) {
                    val w = x.length
                    when {
                        w < width  -> b.appendIf(column.alignment == Align.RIGHT, spaces, w,width)
                                       .append(x)
                                       .appendIf(column.alignment == Align.LEFT, spaces, w,width)
                        w == width -> b.append(x)
                        w > width  -> b.append(x, 0, width)
                    }
                }
                else {
                    b.append(spaces, 0, width)
                }
            }
            b.append(grid.rightLine)
            b.appendLine()
        }

        // print footer
        if (bottomLine != null) {
            b.indent().append(bottomLine).appendLine()
        }

        // ok
        return b
    }


    private fun calculateTextWidth(text: CharSequence): Int {
        return text.length
    }


    private fun StringBuilder.indent(): StringBuilder {
        appendIfNotNull(look.tableIndent)
        return this
    }

    private fun String.replicateTillWidth(width: Int): CharSequence {
        val pl = length
        if (pl == width) return this
        if (pl > width) return substring(0, pl)

        val b = StringBuilder(width + pl)
        while (b.length < width) b.append(this)
        if (b.length > width) b.delete(width, b.length)
        return b
    }

}