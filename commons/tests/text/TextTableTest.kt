package lb.kutil.commons.text

import lb.kutil.commons.text.TextTable.Align.LEFT
import lb.kutil.commons.text.TextTable.Align.RIGHT
import lb.kutil.commons.text.TextTable.Column
import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.junit.jupiter.api.Test


class TextTableTest {

    @Test
    fun `1_column_mono`() {
        val content = listOf("Eins", "Zwei", "Drei", "Vier")
        val tt =
            TextTable<String>(arrayOf(
                Column("Zahl") { s -> s }
            ))
        val text = tt.processContent(content)
        expect that text equalsTo """|====
                                     |Zahl
                                     |----
                                     |Eins
                                     |Zwei
                                     |Drei
                                     |Vier
                                     |----
                                     |
                                  """.trimMargin()
    }

    @Test
    fun `1_column_mono_box`() {
        val content = listOf("Eins", "Zwei", "Drei", "Vier")
        val tt =
            TextTable<String>(arrayOf(
                Column("Zahl") { s -> s }
            ))
        tt.look.grid = tt.look.grid.copy(leftLine = "! ", rightLine = " !")
        val text = tt.processContent(content)
        expect that text equalsTo """|========
                                     |! Zahl !
                                     |--------
                                     |! Eins !
                                     |! Zwei !
                                     |! Drei !
                                     |! Vier !
                                     |--------
                                     |
                                  """.trimMargin()
    }

    @Test
    fun `2_columns_mono`() {
        val content = mapOf(1 to "Eins", 2 to "Zwei", 3 to "Drei", 4 to "Vier")
        val tt =
            TextTable<Map.Entry<Int,String>>(arrayOf(
                Column("#") { r -> r.key.toString() },
                Column("Zahl") { r -> r.value }
            ))
        tt.look.grid = tt.look.grid.copy(cellGap = " ! ")
        val text = tt.processContent(content.entries)
        expect that text equalsTo """|========
                                     |# ! Zahl
                                     |--------
                                     |1 ! Eins
                                     |2 ! Zwei
                                     |3 ! Drei
                                     |4 ! Vier
                                     |--------
                                     |
                                  """.trimMargin()
    }

    @Test
    fun `2_columns_left`() {
        val content = listOf("Apfel", "Birne", "Kiwi", "Mandarine", "Johannisbeere", "Mango")
        val tt =
            TextTable<String>(arrayOf(
                Column("Obst", LEFT) { s -> s },
                Column("#", LEFT) { s -> s.length.toString() }
            ))
        tt.look.grid = tt.look.grid.copy(leftLine = "! ", cellGap = " ! ", rightLine = " !")
        val text = tt.processContent(content)
        expect that text equalsTo """|======================
                                     |! Obst          ! #  !
                                     |----------------------
                                     |! Apfel         ! 5  !
                                     |! Birne         ! 5  !
                                     |! Kiwi          ! 4  !
                                     |! Mandarine     ! 9  !
                                     |! Johannisbeere ! 13 !
                                     |! Mango         ! 5  !
                                     |----------------------
                                     |
                                  """.trimMargin()
    }

    @Test
    fun `2_columns_right`() {
        val content = listOf("Apfel", "Birne", "Kiwi", "Mandarine", "Johannisbeere", "Mango")
        val tt =
            TextTable<String>(arrayOf(
                Column("Obst", RIGHT) { s -> s },
                Column("#", RIGHT) { s -> s.length.toString() }
            ))
        tt.look.grid = tt.look.grid.copy(leftLine = "! ", cellGap = " ! ", rightLine = " !")
        val text = tt.processContent(content)
        expect that text equalsTo """|======================
                                     |!          Obst !  # !
                                     |----------------------
                                     |!         Apfel !  5 !
                                     |!         Birne !  5 !
                                     |!          Kiwi !  4 !
                                     |!     Mandarine !  9 !
                                     |! Johannisbeere ! 13 !
                                     |!         Mango !  5 !
                                     |----------------------
                                     |
                                  """.trimMargin()
    }


}