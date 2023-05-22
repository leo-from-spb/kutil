package lb.kutil.commons.hold

import lb.kutil.commons.UnitTest
import lb.yaka.base.expectations.contains
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.sameAs
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.Test


class AtomicIncrementalValueCacheTest: UnitTest {

    private class C (val v: Long)

    @Test
    fun basic1() {
        val x = AtomicIncrementalValueCache<C>(C(100L)) { old -> C(old.v + 1) }
        expect that x.content.v equalsTo 101L
        x.invalidate()
        expect that x.content.v equalsTo 102L
        x.invalidate()
        expect that x.content.v equalsTo 103L
    }

    @Test
    fun basic2() {
        val x = AtomicIncrementalValueCache<C>(C(100L)) { old -> C(old.v + 1) }
        x.invalidate()
        expect that x.content.v equalsTo 101L
        x.invalidate()
        expect that x.content.v equalsTo 102L
    }

    @Test
    fun initWithEmptyValue() {
        val emptyValue = C(-10L)
        var oldValue: C? = null

        val x = AtomicIncrementalValueCache<C>(emptyValue) { oldValue = it; C(0L) }
        expect that x.ready equalsTo false

        x.content
        expect that oldValue sameAs emptyValue
    }

    @Test
    fun initializedStale() {
        val x = AtomicIncrementalValueCache<Number>(0L) { 42L }
        expect that x.ready equalsTo false

        x.content
        expect that x.ready equalsTo true
    }

    @Test
    fun invalidateThenOneTimeCompute() {
        val x = AtomicIncrementalValueCache<C>(C(0L)) { old -> C(old.v + 1) }
        expect that x.content.v equalsTo 1L

        x.invalidate()
        x.invalidate()
        x.invalidate()

        expect that x.content.v equalsTo 2L
    }

    @Test
    fun resetToEmptyValue() {
        val emptyValue = C(26L)
        var oldValue: C? = null
        val x = AtomicIncrementalValueCache<C>(emptyValue) { old -> oldValue = old; C(42L) }
        x.content
        x.content

        x.reset()
        oldValue = null
        expect that x.ready equalsTo false
        x.content
        expect that oldValue sameAs emptyValue
    }

    @Test
    fun toStringStaleReady() {
        val x = AtomicIncrementalValueCache<Number>(0L) { 42L }
        expect that x.ready equalsTo false
        expect that x.toString() contains "stale"

        x.content
        expect that x.ready equalsTo true
        expect that x.toString() contains "ready"
    }

    @Test
    fun toStringContainContent() {
        val x = AtomicIncrementalValueCache<Number>(0L) { 2600000042L }
        x.content
        expect that x.toString() contains "2600000042"
    }

}