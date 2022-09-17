package lb.kutil.commons.hold

import java.util.function.Consumer
import java.util.function.Supplier

/**
 * External value.
 * Provides a read access to an external value.
 */
abstract class Val<out V> {

    /**
     * The value.
     */
    abstract val value: V

}


/**
 * External variable.
 * Provides read and write access to an external variable.
 */
abstract class Var<V> : Val<V>() {

    /**
     * The variable.
     */
    abstract override var value: V

}


/**
 * Simple variable holder.
 * @property value the variable.
 */
open class Holder<V> (final override var value: V) : Var<V>() {

    override fun toString() = "value = $value"

}


/**
 * Named variable holder.
 * @property name the name of the variable.
 */
open class NamedHolder<V> (@JvmField val name: String, value: V) : Holder<V>(value) {

    override fun toString() = "$name = $value"

}


////// UTILITY FUNCTIONS \\\\\\


val <V> Val<V>.asSupplier: Supplier<V>
    get() = ValAsSupplier(this)

val <V> Var<V>.asConsumer: Consumer<V>
    get() = VarAsConsumer(this)


private class ValAsSupplier<V>(private val v: Val<V>) : Supplier<V> {
    override fun get(): V = v.value
}

private class VarAsConsumer<V>(private val v: Var<V>) : Consumer<V> {
    override fun accept(t: V) {
        v.value = t
    }
}
