package org.example

import kotlin.test.*

private fun exampleHierarchy(): Hierarchy = ArrayBasedHierarchy(
    intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
    intArrayOf(0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2)
)

class FilterTestProvided {
    @Test
    fun testFilter_promptSample() {
        val unfiltered = exampleHierarchy()
        val filteredActual: Hierarchy = unfiltered.filter { nodeId -> nodeId % 3 != 0 }
        val filteredExpected: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 5, 8, 10, 11),
            intArrayOf(0, 1, 1, 0, 1, 2))
        assertEquals(filteredExpected.formatString(), filteredActual.formatString())
    }
}

class AdditionalFilterTests {
    @Test
    fun emptyHierarchy() {
        val empty = ArrayBasedHierarchy(intArrayOf(), intArrayOf())
        val filtered = empty.filter { true }
        assertEquals("[]", filtered.formatString())
    }

    @Test
    fun allRejected() {
        val tree = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3),
            intArrayOf(0, 1, 2)
        )
        val filtered = tree.filter { false }
        assertEquals("[]", filtered.formatString())
    }

    @Test
    fun allAccepted_identity() {
        val ids = intArrayOf(1, 2, 3, 4, 5)
        val depths = intArrayOf(0, 1, 2, 1, 0)
        val h = ArrayBasedHierarchy(ids, depths)
        val f = h.filter { true }
        assertEquals(h.formatString(), f.formatString())
    }

    @Test
    fun pruneSubtree_andResumeOnSibling() {
        val unfiltered = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6),
            intArrayOf(0, 1, 2, 3, 1, 0)
        )
        val filtered = unfiltered.filter { id -> id != 2 }
        val expected = ArrayBasedHierarchy(
            intArrayOf(1, 5, 6),
            intArrayOf(0, 1, 0)
        )
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun multipleRoots_variedAccept_corrected() {
        val unfiltered = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5),
            intArrayOf(0, 0, 1, 0, 1)
        )
        val filtered = unfiltered.filter { it % 2 != 0 }
        val expected = ArrayBasedHierarchy(
            intArrayOf(1),
            intArrayOf(0)
        )
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun deepChain_partialFilter() {
        val unfiltered = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6),
            intArrayOf(0, 1, 2, 3, 4, 5)
        )
        val filtered = unfiltered.filter { it != 3 }
        val expected = ArrayBasedHierarchy(
            intArrayOf(1, 2),
            intArrayOf(0, 1)
        )
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun predicateNotCalledInsidePrunedSubtree() {
        val h = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5),
            intArrayOf(0, 1, 2, 2, 0)
        )
        var calls = 0
        val filtered = h.filter { id ->
            calls++
            id != 2
        }
        assertEquals(3, calls)
        assertEquals("[1:0, 5:0]", filtered.formatString())
    }

    @Test
    fun consecutivePrunesAcrossRoots() {
        // roots: 1 (fail), 2 (fail), 3 (pass) -> only 3 remains
        val h = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3),
            intArrayOf(0, 0, 0)
        )
        val f = h.filter { it == 3 }
        assertEquals("[3:0]", f.formatString())
    }

    @Test
    fun pruneThenUnpruneOnShallowDepth() {
        // 1 (pass)
        // ├─ 2 (fail)  -> prunes 3
        // │   └─ 3
        // └─ 4 (pass)  -> should resume evaluation outside the pruned subtree
        val h = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 1, 2, 1)
        )
        val f = h.filter { it != 2 }
        assertEquals("[1:0, 4:1]", f.formatString())
    }


}
