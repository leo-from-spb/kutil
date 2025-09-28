package lb.kutil.commons.tree

/**
 * Abstract tree.
 *
 * This class 
 */
class AbstractTree<N: Any> {

    // TREE DEFINITION METHODS

    /**
     * The root of the tree. Null means that the root is unknown.
     */
    val root: N?

    /**
     * Function that returns the parent of the given node.
     */
    val parentFunction: (N) -> N?

    /**
     * Function that iterates children of the given node.
     */
    val childrenFunction: (N) -> Iterable<N>


    // COMPANION

    companion object {

        @JvmStatic
        fun <N: Any> treeWithRoot(root: N?): AbstractTree<N> =
            AbstractTree(root)

    }


    // CONSTRUCTOR AND "BUILD" METHODS

    /**
     * Instantiates the instance.
     *
     * Primary tree components:
     * * the root;
     * * a function to get parent of a node;
     * * a function to get children of a node.
     *
     * Not all three components are always required.
     */
    constructor(root: N? = null, parentFunction: (N) -> N? = { null }, childrenFunction: (N) -> Iterable<N> = { emptySet() }) {
        this.root = root
        this.parentFunction = parentFunction
        this.childrenFunction = childrenFunction
    }


    fun withParent(parentFunction: (N) -> N?): AbstractTree<N> =
        AbstractTree<N>(this.root, parentFunction, this.childrenFunction)

    fun withChildren(childrenFunction: (N) -> Iterable<N>) =
        AbstractTree<N>(this.root, this.parentFunction, childrenFunction)

    //@JvmName("withChildrenSequence")
    //fun withChildren(childrenFunction: (N) -> Sequence<N>) =
    //    AbstractTree<N>(this.root, this.parentFunction) { node: N -> childrenFunction(node).asIterable() }


    // ALGORITHM METHODS

    fun traverseDepthFirst(): Iterator<N> =
        if (root != null) traverseDepthFirst(root)
        else emptyList<N>().iterator()

    fun traverseDepthFirst(fromNode: N): Iterator<N> =
        DFSIterator(fromNode)

    /**
     * Implements the depth-first traversal algorithm.
     */
    private inner class DFSIterator(val root: N): Iterator<N> {

        /**
         * The node that is got from the tree but not returned yet.
         * It is the node that will be returned by the [next] function call.
         * Initially points to the root.
         * Null means no more nodes to return (end of the traversal).
         */
        private var currNode: N? = root

        /**
         * The iterator the [currNode] was got from.
         */
        private var currIterator: Iterator<N>? = null

        /**
         * Stack to push the [currIterator] when the [currNode] has children.
         */
        private val stack: ArrayDeque<Iterator<N>> = ArrayDeque()

        /**
         * Retrieves the next node from the tree.
         */
        private fun processNext() {
            val theNode = currNode ?: return
            val currIterator = this.currIterator
            val newChildrenIterable: Iterable<N> = childrenFunction(theNode)
            val newChildrenIterator = newChildrenIterable.iterator()
            if (newChildrenIterator.hasNext()) {
                // enter into the lower level
                if (currIterator != null) stack.addLast(currIterator)
                currNode = newChildrenIterator.next()
                this.currIterator = newChildrenIterator
            }
            else if (currIterator != null && currIterator.hasNext()) {
                // traverse to the next node
                currNode = currIterator!!.next()
            }
            else {
                // return to the upper level
                currNode = null
                while (stack.isNotEmpty()) {
                    val lastIterator = stack.removeLast()
                    if (lastIterator.hasNext()) {
                        currNode = lastIterator.next()
                        this.currIterator = lastIterator
                        break
                    }
                }
            }
        }

        override fun hasNext(): Boolean = currNode != null

        override fun next(): N {
            val node = currNode ?: throw NoSuchElementException("No more elements")
            processNext()
            return node
        }
    }


    fun traverseBreadthFirst() =
        if (root != null) traverseBreadthFirst(root)
        else emptyList<N>().iterator()

    fun traverseBreadthFirst(fromNode: N): Iterator<N> =
        BFSIterator(fromNode)

    /**
     * Implements the breadth-first traversal algorithm.
     */
    private inner class BFSIterator(val root: N): Iterator<N> {

        /**
         * The queue to store the nodes that are not yet returned by the [next] function.
         */
        private val queue: ArrayDeque<N> = ArrayDeque()

        init {
            queue.add(root)
        }

        override fun hasNext(): Boolean = queue.isNotEmpty()

        override fun next(): N {
            val node = queue.removeFirst()
            queue.addAll(childrenFunction(node))
            return node
        }

    }


}