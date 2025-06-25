package uk.akane.fatal.components

import uk.akane.fatal.module.CommandModule
import kotlin.text.iterator

class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var commandModule: CommandModule? = null
}

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

    private class TrieNode<T> {
        val children: MutableMap<Char, TrieNode<T>> = mutableMapOf()
        var value: T? = null
    }
}
