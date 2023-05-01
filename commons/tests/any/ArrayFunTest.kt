package lb.kutil.commons.any

import lb.kutil.commons.UnitTest
import lb.yaka.base.expectations.containsExactly
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.Test

class ArrayFunTest: UnitTest {

    @Test
    fun forEachReversed_basic() {
        val arr: Array<Number> = arrayOf(33, 44, 55)
        val list = ArrayList<Number>()
        arr.forEachReversed { value -> list += value }
        expect that list containsExactly arrayOf(55, 44, 33)
    }
    
}