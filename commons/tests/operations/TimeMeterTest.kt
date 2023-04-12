package lb.kutil.commons.operations

import lb.kutil.commons.UnitTest
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.hasSize
import lb.yaka.base.gears.complies
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.MethodName::class)
class TimeMeterTest: UnitTest {

    @Test
    fun `1_operation_1_metric`() {
        val meter = TimeMeter()
        meter.considerDuration("Op", 99L)
        val stat = meter.getMetrics()
        expect that stat hasSize 1
        expect that stat.first() complies {
            property { ::name } equalsTo "Op"
            property { ::count } equalsTo 1
            property { ::min } equalsTo 99
            property { ::avg } equalsTo 99
            property { ::max } equalsTo 99
            property { ::sum } equalsTo 99
        }
    }

    @Test
    fun `1_operation_4_metrics`() {
        val meter = TimeMeter()
        // values: 10, 20, 30, 40
        meter.considerDuration("Op", 20L)
        meter.considerDuration("Op", 40L)
        meter.considerDuration("Op", 10L)
        meter.considerDuration("Op", 30L)
        val stat = meter.getMetrics()
        expect that stat hasSize 1
        expect that stat.first() complies {
            property { ::count } equalsTo 4
            property { ::min } equalsTo 10
            property { ::avg } equalsTo 25
            property { ::max } equalsTo 40
            property { ::sum } equalsTo 100
        }
    }

    @Test
    fun `3_operations`() {
        val meter = TimeMeter()
        meter.considerDuration("Aa", 10L)
        meter.considerDuration("Bb", 20L)
        meter.considerDuration("Cc", 30L)
        val stats = meter.getMetrics()
        expect that stats hasSize 3
        expect that stats.first { it.name == "Aa" } complies {
            property { ::count } equalsTo 1
            property { ::sum } equalsTo 10
        }
        expect that stats.first { it.name == "Bb" } complies {
            property { ::count } equalsTo 1
            property { ::sum } equalsTo 20
        }
        expect that stats.first { it.name == "Cc" } complies {
            property { ::count } equalsTo 1
            property { ::sum } equalsTo 30
        }
    }


    @Test
    fun report_basic() {
        val meter = TimeMeter()
        meter.considerDuration("Dig a pit", 700L)
        meter.considerDuration("Place the seedling", 100L)
        meter.considerDuration("Fill with soil", 200L)
        meter.considerDuration("Fill with soil", 300L)
        val text = meter.produceReport().toString()
        expect that text equalsTo """|===========================================
	                             |Operation           Cnt  Min  Avg  Max  Sum
	                             |-------------------------------------------
	                             |Dig a pit             1  700  700  700  700
	                             |Place the seedling    1  100  100  100  100
	                             |Fill with soil        2  200  250  300  500
	                             |-------------------------------------------
                                     |
                                  """.trimMargin()
    }



}