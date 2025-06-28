package uk.akane.fatal.components

class Trie<T> {
    private val root = TrieNode<T>()

    fun insert(key: String, value: T) {
        var currentNode = root
        for (char in key) {
            currentNode = currentNode.children.computeIfAbsent(char) { TrieNode() }
        }
        currentNode.value = value
    }

    fun find(key: String): T? {
        var currentNode = root
        for (char in key) {
            currentNode = currentNode.children[char] ?: return null
        }
        return currentNode.value
    }

    fun findLongestPrefixMatch(input: String): Pair<T, Int>? {
        var currentNode = root
        var lastValueNode: TrieNode<T>? = null
        var lastMatchIndex = 0

        for ((index, char) in input.withIndex()) {
            currentNode = currentNode.children[char] ?: break
            if (currentNode.value != null) {
                lastValueNode = currentNode
                lastMatchIndex = index + 1
            }
        }

        return if (lastValueNode?.value != null) {
            lastValueNode.value!! to lastMatchIndex
        } else {
            null
        }
    }

    private class TrieNode<T> {
        val children: MutableMap<Char, TrieNode<T>> = mutableMapOf()
        var value: T? = null
    }
}

