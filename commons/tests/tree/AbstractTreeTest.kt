package lb.kutil.commons.tree

import lb.kutil.commons.UnitTest
import lb.kutil.commons.any.toList
import lb.yaka.base.expectations.containsExactly
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.iz
import lb.yaka.base.expectations.sameAs
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.*
import java.util.*


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AbstractTreeTest : UnitTest {

    lateinit var treeOfJustSingleRoot: AbstractTree<Number>
    lateinit var treeOfChain:          AbstractTree<String>
    lateinit var treeOfABC:            AbstractTree<String>


    @BeforeAll
    fun seedTheForest() {
        treeOfJustSingleRoot =
            AbstractTree(root = 26L)
        treeOfChain =
            AbstractTree.treeWithRoot("")
                .withParent { n: String -> if (n.isEmpty()) null else n.dropLast(1) }
                .withChildren { n: String -> if (n.length < 5) listOf(n + 'X') else emptyList() }
        treeOfABC =
            AbstractTree.treeWithRoot("")
                .withParent { n: String -> if (n.isEmpty()) null else n.dropLast(1) }
                .withChildren { n: String -> if (n.length < 3) listOf(n + 'A', n + 'B', n + 'C') else emptyList() }
    }



    @Test @Order(101)
    fun pathFromRoot_onlyRoot() {
        val path = treeOfJustSingleRoot.pathFromRoot(26L)
        expect that path equalsTo listOf(26L)
    }

    @Test @Order(102)
    fun pathFromRoot_basic() {
        val path = treeOfChain.pathFromRoot("XXX")
        expect that path equalsTo listOf("", "X", "XX", "XXX")
    }

    @Test @Order(103)
    fun pathToRoot_basic() {
        val path = treeOfChain.pathToRoot("XXX")
        expect that path equalsTo listOf("XXX", "XX", "X", "")
    }


    @Test @Order(105)
    fun commonAncestor_commonPathFromRoot_basic() {
        val nodeA = "ABAX"
        val nodeB = "ABBA"
        expect that treeOfABC.commonAncestor(nodeA, nodeB) equalsTo "AB"
        expect that treeOfABC.commonPathFromRoot(nodeA, nodeB) equalsTo listOf("", "A", "AB")
    }

    @Test @Order(106)
    fun commonAncestor_commonPathFromRoot_differentLengths() {
        val nodeA = "ABC"
        val nodeB = "ABANDON"
        expect that treeOfABC.commonAncestor(nodeA, nodeB) equalsTo "AB"
        expect that treeOfABC.commonPathFromRoot(nodeA, nodeB) equalsTo listOf("", "A", "AB")
    }

    @Test @Order(107)
    fun commonAncestor_commonPathFromRoot_onlyRoot() {
        val nodeA = "ABC"
        val nodeB = "CABIN!"
        expect that treeOfABC.commonAncestor(nodeA, nodeB) equalsTo ""
        expect that treeOfABC.commonPathFromRoot(nodeA, nodeB) equalsTo listOf("")
    }

    @Test @Order(108)
    fun commonAncestor_commonPathFromRoot_oneIsPartOfAnother_1() {
        val nodeA = "ABC"
        val nodeB = "ABCDE"
        expect that treeOfABC.commonAncestor(nodeA, nodeB) equalsTo "ABC"
        expect that treeOfABC.commonPathFromRoot(nodeA, nodeB) equalsTo listOf("", "A", "AB", "ABC")
    }

    @Test @Order(108)
    fun commonAncestor_commonPathFromRoot_oneIsPartOfAnother_2() {
        val nodeA = "ABCDE"
        val nodeB = "ABC"
        expect that treeOfABC.commonAncestor(nodeA, nodeB) equalsTo "ABC"
        expect that treeOfABC.commonPathFromRoot(nodeA, nodeB) equalsTo listOf("", "A", "AB", "ABC")
    }


    @Test @Order(111)
    fun traverseDepthFirst_oneNode() {
        val traversed = treeOfJustSingleRoot.traverseDepthFirst().toList()
        expect that traversed equalsTo listOf(26L)
    }

    @Test @Order(112)
    fun traverseDepthFirst_regular() {
        val traversed = treeOfABC.traverseDepthFirst().toList()
        val expectedNodes = arrayOf(
            "",
            "A", "AA", "AAA", "AAB", "AAC",
                 "AB", "ABA", "ABB", "ABC",
                 "AC", "ACA", "ACB", "ACC",
            "B", "BA", "BAA", "BAB", "BAC",
                 "BB", "BBA", "BBB", "BBC",
                 "BC", "BCA", "BCB", "BCC",
            "C", "CA", "CAA", "CAB", "CAC",
                 "CB", "CBA", "CBB", "CBC",
                 "CC", "CCA", "CCB", "CCC")
        expect that traversed equalsTo expectedNodes
    }

    @Test @Order(113)
    fun traverseDepthFirst_chain() {
        val traversed = treeOfChain.traverseDepthFirst().toList()
        val expectedNodes = arrayOf("", "X", "XX", "XXX", "XXXX", "XXXXX")
        expect that traversed equalsTo expectedNodes
    }


    @Test @Order(121)
    fun traverseBreadthFirst_oneNode() {
        val traversed = treeOfJustSingleRoot.traverseBreadthFirst().toList()
        expect that traversed equalsTo listOf(26L)
    }

    @Test @Order(122)
    fun traverseBreadthFirst_regular() {
        val traversed = treeOfABC.traverseBreadthFirst().toList()
        val expectedNodes = arrayOf(
            "",
            "A", "B", "C",
            "AA", "AB", "AC",
            "BA", "BB", "BC",
            "CA", "CB", "CC",
            "AAA", "AAB", "AAC",
            "ABA", "ABB", "ABC",
            "ACA", "ACB", "ACC",
            "BAA", "BAB", "BAC",
            "BBA", "BBB", "BBC",
            "BCA", "BCB", "BCC",
            "CAA", "CAB", "CAC",
            "CBA", "CBB", "CBC",
            "CCA", "CCB", "CCC")
        expect that traversed equalsTo expectedNodes
    }

    @Test @Order(123)
    fun traverseBreadthFirst_chain() {
        val traversed = treeOfChain.traverseBreadthFirst().toList()
        val expectedNodes = arrayOf("", "X", "XX", "XXX", "XXXX", "XXXXX")
        expect that traversed equalsTo expectedNodes
    }


    @Test @Order(130)
    fun filterTopNodes_empty() {
        val original = emptySet<Number>()
        val result = treeOfJustSingleRoot.filterTopNodes(original)
        expect that result sameAs original
    }

    @Test @Order(130)
    fun filterTopNodes_justOneNode() {
        val original = setOf("ABC")
        val result = treeOfABC.filterTopNodes(original)
        expect that result sameAs original
    }

    @Test @Order(131)
    fun filterTopNodes_trivialSame() {
        val original = listOf("ABA", "ABC", "BA", "C")
        val result = treeOfABC.filterTopNodes(original)
        expect that result sameAs original
    }

    @Test @Order(131)
    fun filterTopNodes_goodSorted_noGaps() {
        val original = listOf("X", "XX", "XXX", "XXXX", "XXXXX")
        val result = treeOfChain.filterTopNodes(original)
        expect that result equalsTo listOf("X")
    }

    @Test @Order(132)
    fun filterTopNodes_goodSorted_withGaps() {
        val original = listOf("X", "XXX", "XXXXX")
        val result = treeOfChain.filterTopNodes(original)
        expect that result equalsTo listOf("X")
    }

    @Test @Order(133)
    fun filterTopNodes_badSorted_noGaps() {
        val original = listOf("XXXXX", "XXXX", "XXX", "XX", "X")
        val result = treeOfChain.filterTopNodes(original)
        expect that result equalsTo listOf("X")
    }

    @Test @Order(134)
    fun filterTopNodes_badSorted_withGaps() {
        val original = listOf("XXXXX", "XXX", "X")
        val result = treeOfChain.filterTopNodes(original)
        expect that result equalsTo listOf("X")
    }

    @Test @Order(135)
    fun filterTopNodes_removeTwoChildren_SetWithUnusualSorting() {
        val unusualComparator: Comparator<String> = compareBy<String> { -it.length } . thenBy { it }
        val originalSet = TreeSet(unusualComparator)
        originalSet += arrayOf("C", "ABA", "ABB", "ABC", "A")
        val result = treeOfABC.filterTopNodes(originalSet)
        expect that result containsExactly setOf("C", "A")
        expect that result iz Set::class
    }

    @Test @Order(139)
    fun filterTopNodes_containsRoot() {
        val original = listOf("C", "AAA", "AAC", "", "B", "BA", "BC")
        val result = treeOfABC.filterTopNodes(original)
        expect that result equalsTo listOf("")
    }

    @Test @Order(139)
    fun filterTopNodes_containsRoot_set() {
        val originalSet = hashSetOf("C", "AAA", "AAC", "", "B", "BA", "BC", "BCA", "BCB", "BCC")
        val result = treeOfABC.filterTopNodes(originalSet)
        expect that result equalsTo setOf("")
        expect that result iz Set::class
    }
}