package lb.kutil.commons.hold

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.util.function.Consumer
import java.util.function.Supplier


@TestMethodOrder(MethodOrderer.MethodName::class)
class HolderTest {

    @Test
    fun `01_Holder_basic_example`() {
        fun simplify(v: Var<Number>) {
            if (v.value is Long && v.value.toLong() >= Int.MIN_VALUE && v.value.toLong() <= Int.MAX_VALUE)
                v.value = v.value.toInt()
        }

        val v: Var<Number> = Holder(1234L)
        expect that v.value iz Long::class
        simplify(v)
        expect that v.value iz Integer::class
    }

    @Test
    fun `02_Val_as_Supplier`() {
        fun foo(supplier: Supplier<Number>) {
            if (supplier.get() == 42) println("It's bad")
        }

        val v = Holder<Byte>(26)
        foo(v.asSupplier)
    }

    @Test
    fun `03_Var_as_Consumer`() {
        fun foo(consumer: Consumer<in Long>) {
            consumer.accept(26L)
        }

        val v = Holder<Number>(33)
        foo(v.asConsumer)
        expect that v.value equalsTo 26L
    }



    @Test
    fun `11_Holder_toString`() {
        val v: Val<Number> = Holder<Byte>(42)
        expect that v.toString() contains "42"
    }

    @Test
    fun `12_NamedHolder_toString`() {
        val v: Val<Number> = NamedHolder<Byte>("everything", 42)
        expect that v.toString() complies {
            this contains "everything"
            this contains "42"
        }
    }


}