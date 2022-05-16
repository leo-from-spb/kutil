package lb.kutil.commons.text

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.junit.jupiter.api.Test


class CharFunTest {

    @Test
    fun replicate_basic() {
        expect that 'A'.replicate(0) iz empty
        expect that 'A'.replicate(1) equalsTo "A"
        expect that 'A'.replicate(2) equalsTo "AA"
        expect that 'A'.replicate(3) equalsTo "AAA"
    }

}