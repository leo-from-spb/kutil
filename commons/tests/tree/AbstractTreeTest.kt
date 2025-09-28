package lb.kutil.commons.tree

import lb.kutil.commons.UnitTest
import lb.kutil.commons.any.toList
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.gears.expect
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AbstractTreeTest : UnitTest {

    @Test @Order(111)
    fun traverseDepthFirst_oneNode() {
        val theOnlyNode: Number = 26L
        val tree = AbstractTree<Number>(theOnlyNode)
        val traversed = tree.traverseDepthFirst().toList()
        expect that traversed equalsTo listOf(theOnlyNode)
    }

    @Test @Order(112)
    fun traverseDepthFirst_regular() {
        val tree =
            AbstractTree.treeWithRoot("")
                .withChildren { n: String -> if (n.length < 3) listOf(n + 'A', n + 'B', n + 'C') else emptyList() }
        val traversed = tree.traverseDepthFirst().toList()
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
        val tree =
            AbstractTree.treeWithRoot("")
                .withChildren { n: String -> if (n.length < 5) listOf(n + 'X') else emptyList() }
        val traversed = tree.traverseDepthFirst().toList()
        val expectedNodes = arrayOf("", "X", "XX", "XXX", "XXXX", "XXXXX")
        expect that traversed equalsTo expectedNodes
    }


    @Test @Order(121)
    fun traverseBreadthFirst_oneNode() {
        val theOnlyNode: Number = 26L
        val tree = AbstractTree<Number>(theOnlyNode)
        val traversed = tree.traverseBreadthFirst().toList()
        expect that traversed equalsTo listOf(theOnlyNode)
    }

    @Test @Order(122)
    fun traverseBreadthFirst_regular() {
        val tree =
            AbstractTree.treeWithRoot("")
                .withChildren { n: String -> if (n.length < 3) listOf(n + 'A', n + 'B', n + 'C') else emptyList() }
        val traversed = tree.traverseBreadthFirst().toList()
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
        val tree =
            AbstractTree.treeWithRoot("")
                .withChildren { n: String -> if (n.length < 5) listOf(n + 'X') else emptyList() }
        val traversed = tree.traverseBreadthFirst().toList()
        val expectedNodes = arrayOf("", "X", "XX", "XXX", "XXXX", "XXXXX")
        expect that traversed equalsTo expectedNodes
    }




}