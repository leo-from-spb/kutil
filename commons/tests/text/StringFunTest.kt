package lb.kutil.commons.text

import lb.kutil.commons.UnitTest
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.iz
import lb.yaka.base.expectations.sameAs
import lb.yaka.base.gears.NotNull
import lb.yaka.base.gears.Null
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.Test


class StringFunTest: UnitTest {

    @Test
    fun phraseOf_basic() {
        expect that phraseOf("AAA") equalsTo "AAA"
        expect that phraseOf("AAA", "BBB", "CCC") equalsTo "AAA BBB CCC"
    }

    @Test
    fun phraseOf_basic_withNulls() {
        expect that phraseOf("AAA", null, "BBB") equalsTo "AAA BBB"
        expect that phraseOf("AAA", null, null, null, "BBB") equalsTo "AAA BBB"
        expect that phraseOf(null, "AAA", null, "BBB", null, "CCC", null) equalsTo "AAA BBB CCC"
    }

    @Test
    fun phraseOf_delimiter() {
        expect that phraseOf("AAA", delimiter = "-+-") equalsTo "AAA"
        expect that phraseOf("AAA", "BBB", "CCC", delimiter = "-+-") equalsTo "AAA-+-BBB-+-CCC"
    }

    @Test
    fun phraseOf_prefixAndSuffix() {
        expect that phraseOf("AAA", prefix = "<<", suffix = ">>") equalsTo "<<AAA>>"
        expect that phraseOf("AAA", "BBB", "CCC", prefix = "<<", suffix = ">>") equalsTo "<<AAA BBB CCC>>"
    }

    @Test
    fun phraseOf_empty() {
        expect that phraseOf(null, null) equalsTo ""
        expect that phraseOf(*emptyArray()) equalsTo ""
    }

    @Test
    fun phraseOf_emptyText() {
        expect that phraseOf(null, emptyText = "empty") equalsTo "empty"
        expect that phraseOf(null, null, emptyText = "empty") equalsTo "empty"
        expect that phraseOf(*emptyArray(), emptyText = "empty") equalsTo "empty"
    }

    @Test
    fun phraseOf_emptyText_noPrefixesAndSuffixes() {
        expect that phraseOf(null, null, prefix = "<<", suffix = ">>", emptyText = "empty") equalsTo "empty"
    }

    @Test
    fun phraseOf_emptyText_same() {
        val emptyText = "This is Empty"
        expect that phraseOf(null,  emptyText = emptyText) sameAs emptyText
    }


    @Test
    fun match_basic() {
        val s = "xxx-ABC-zzz"
        val m = s match Regex(".*(ABC).*")
        expect that m iz NotNull
        expect that m!!.groupValues[1] equalsTo "ABC"
    }

    @Test
    fun extract_basic() {
        val s = "xxx----ABC----zzz"
        val v = s.extract(Regex(".*(-*)(ABC)(-*).*"), 2)
        expect that v equalsTo "ABC"
    }

    @Test
    fun extract_notMatched() {
        val s = "xxx----ABC----zzz"
        val v = s.extract(Regex(".*(XYZ).*"), 2)
        expect that v iz Null
    }

}