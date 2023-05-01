package lb.kutil.commons.any

import lb.kutil.commons.UnitTest
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.iz
import lb.yaka.base.gears.empty
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.Test


class CharFunTest: UnitTest {

    @Test
    fun replicate_basic() {
        expect that 'A'.replicate(0) iz empty
        expect that 'A'.replicate(1) equalsTo "A"
        expect that 'A'.replicate(2) equalsTo "AA"
        expect that 'A'.replicate(3) equalsTo "AAA"
    }

}