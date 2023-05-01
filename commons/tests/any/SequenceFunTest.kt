package lb.kutil.commons.any

import lb.kutil.commons.UnitTest
import lb.yaka.base.expectations.containsExactly
import lb.yaka.base.expectations.hasSize
import lb.yaka.base.expectations.iz
import lb.yaka.base.gears.empty
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.Test

class SequenceFunTest: UnitTest {

    @Test
    fun toArray_basic() {
        val seq: Sequence<Number> = sequenceOf(11, 22, 33)
        val arr: Array<Number> = seq.toArray()
        expect that arr hasSize 3 containsExactly arrayOf(11, 22, 33)
    }
    
    @Test
    fun toArray_empty() {
        val seq: Sequence<Number> = emptySequence()
        val arr: Array<Number> = seq.toArray()
        expect that arr iz empty
    }

}