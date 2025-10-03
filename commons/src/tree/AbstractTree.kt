package lb.kutil.commons.tree

import java.util.SortedSet
import java.util.TreeSet
import kotlin.collections.ArrayDeque
import kotlin.math.min

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

    /**
     * Returns the path from the root to the given node.
     * The path starts with the root node and ends with the given node.
     * If the given node is a root, the function returns a list with a single root element.
     */
    fun pathFromRoot(node: N): List<N> {
        val path = pathToRoot(node)
        return if (path.size == 1) path else path.reversed()
    }

    /**
     * Returns the path from the given node to the root.
     * The path starts with the given node and ends with the root node.
     * If the given node is a root, the function returns a list with a single root element.
     */
    fun pathToRoot(node: N): List<N> {
        val path = ArrayList<N>()
        var x: N? = node
        while (x != null) {
            path.add(x)
            x = parentFunction(x)
        }
        return if (path.size == 1) listOf(node) else path
    }

    /**
     * Determines whether the node [ancestor] is a direct or indirect parent of the node [descendant].
     *
     * Note: when both nodes are the same, the function returns *false*.
     */
    fun nodeIsAncestor(ancestor: N, descendant: N): Boolean {
        var p: N? = parentFunction(descendant)
        while (p != null) {
            if (p == ancestor) return true
            p = parentFunction(p)
        }
        return false
    }


    /**
     * Finds the nearest common ancestor of two nodes.
     * Both nodes must belong to the same tree, so they always have a common ancestor
     * (because any tree has the root node).
     * @see commonPathFromRoot
     */
    fun commonAncestor(node1: N, node2: N): N {
        if (node1 == node2) return node1
        val path1 = pathToRoot(node1)
        val path2 = pathToRoot(node2)
        var k1 = path1.lastIndex
        var k2 = path2.lastIndex
        assert(path1[k1] == path2[k2]) { "Expected two nodes of one tree have the same root but they don't (${path1[k1]} != ${path2[k2]})"}
        while (k1 > 0 && k2 > 0 && path1[k1-1] == path2[k2-1]) { k1--; k2-- }
        return path1[k1]
    }

    /**
     * Finds the longest common path from the root to the given nodes.
     * In other words, finds the path from the root to the nearest common ancestor of the two given nodes.
     * @see commonAncestor
     */
    fun commonPathFromRoot(node1: N, node2: N): List<N> {
        val path1 = pathFromRoot(node1)
        if (node1 == node2) return path1
        val path2 = pathFromRoot(node2)
        val lastIndex = min(path1.lastIndex, path2.lastIndex)
        var k = 0
        while (k < lastIndex && path1[k+1] == path2[k+1]) k++
        if (k == lastIndex) {
            if (path1.lastIndex == k) return path1
            else if (path2.lastIndex == k) return path2
        }
        return path1.subList(0, k+1)
    }


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


    /**
     * Filters (retains) the top nodes of the tree.
     *
     * It filters the given collection in such a way that from any pair of two nodes,
     * when one is a direct or indirect parent of the other, only the parent remains and the child is dropped.
     *
     * In the result collection, no node is a child of another node. And every node from the input collection
     * is either a node from the result collection or is a direct or indirect child of some node from the result collection.
     *
     * The complexity of the algorithm in the best case is *O(m × h)* (when the input nodes are sorted from top to bottom)
     * or *O(m² × h)* (when the input nodes are sorted from bottom to top),
     * where *m* — the number of nodes in the input collection, *h* — the height of the tree.
     * The complexity doesn't depend on the size of the tree.
     *
     * The result collection could be a new collection instance,
     * or the input collection itself, if the input collection does not contain any nodes
     * that are direct or indirect children of other nodes.
     *
     * @param nodes the input collection.
     * @return the filtered collection.
     */
    fun filterTopNodes(nodes: Collection<N>): Collection<N> {
        if (nodes.size < 2) return nodes

        val ancestors: MutableSet<N> = createApplicableMutableEmptySet(nodes)
        val result: MutableCollection<N> = createApplicableMutableEmptyCollection(nodes)

        for (node in nodes) {
            val path = pathToRoot(node)
            if (path.size == 1) { // this is a root, other nodes don't matter
                result.clear()
                result.add(node)
                break
            }
            if (ancestors.isEmpty()) { // this is the first node from the input collection
                ancestors.addAll(path)
                result.add(node)
                continue
            }
            val pathSet = path.toSet()
            if (result.any(pathSet::contains)) continue // it or it's ancestor already in the result
            if (node in ancestors) { // this node is an ancestor of some node that already was added to the result
                var removed = false
                var replaced = false
                val iterator: MutableIterator<N> = result.iterator()
                while (iterator.hasNext()) {
                    val theNode = iterator.next()
                    if (nodeIsAncestor(node, theNode)) {
                        iterator.remove()
                        removed = true
                        if (!replaced && iterator is MutableListIterator) {
                            iterator.add(node)
                            replaced = true
                        }
                    }
                }
                if (removed && !replaced) result.add(node)
                continue
            }
            // so, it's just another node
            ancestors.addAll(path)
            result.add(node)
        }

        return if (result.size < nodes.size) result else nodes
    }


    private fun <T> createApplicableMutableEmptyCollection(originalCollection: Collection<T>): MutableCollection<T> {
        if (originalCollection is Set) {
            return createApplicableMutableEmptySet(originalCollection)
        }
        else {
            return ArrayList<T>(originalCollection.size)
        }
    }

    private fun <T> createApplicableMutableEmptySet(originalCollection: Collection<T>): MutableSet<T> {
        if (originalCollection is SortedSet) {
            val comparator = originalCollection.comparator()
            return if (comparator != null) TreeSet(comparator) else TreeSet()
        }
        else {
            return HashSet(originalCollection.size)
        }
    }

}