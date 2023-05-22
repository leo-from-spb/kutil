package lb.kutil.commons.hold

import java.util.concurrent.atomic.AtomicReference


/**
 * Caches one value-like content with the ability to re-compute this content incrementally.
 * The cached content is usually an immutable structure.
 *
 * Initially, the status is _stale_. When one requests the content, it checks whether the content is _ready_
 * and [re-]computes the content when it is not.
 *
 * Any thread can invalidate the content, this changes the status to _stale_ but the current content is not dropped.
 * On the next demand, the re-computing method receives the current (old) content.
 *
 * @param emptyValue the initial value;
 *                   it's used for the first initialization and on reset (so this object is held inside).
 * @param recompute the function that re-computes (or actualizes) the content,
 *                  it receives the current content.
 *
 * @property content the cached content;
 *                   when the state is not _ready_ the re-computing is invoked before returning the content
 *                   in order to actualize it, so the returned content is always actual.
 * @property ready whether the content is _ready_ (true) or _stale_ (false).
 */
class AtomicIncrementalValueCache<X> (private val emptyValue: X, private val recompute: (X) -> X) {

    private val ref: AtomicReference<Wrap<X>> = AtomicReference(Wrap(emptyValue, false))

    /**
     * The content.
     */
    val content: X
        get() = obtainActualWrap().content

    val ready: Boolean
        get() = ref.get().ready


    private fun obtainActualWrap(): Wrap<X> =
        ref.updateAndGet {
            w -> if (w.ready) w
        else Wrap(recompute(w.content), true)
        }

    /**
     * Marks the content as _stale_.
     */
    fun invalidate() {
        ref.updateAndGet {
            w -> if (w.ready) Wrap(w.content, false)
        else w
        }
    }

    /**
     * Unconditional reset.
     * It drops the current content, uses the [emptyValue] as a placeholder and marks it as _stale_.
     */
    fun reset() {
        ref.set(Wrap(emptyValue, false))
    }

    override fun toString() = ref.get().toString()


    /// INNER STUFF \\\

    private class Wrap<X> (@JvmField val content: X, @JvmField val ready: Boolean) {
        override fun toString() = (if (ready) "ready " else "stale ") + content
    }

}
