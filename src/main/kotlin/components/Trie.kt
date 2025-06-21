package uk.akane.fatal.components

import uk.akane.fatal.module.CommandModule
import kotlin.text.iterator

class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var commandModule: CommandModule? = null
}

class Trie {
    private val root = TrieNode()

    fun insert(command: String, module: CommandModule) {
        var currentNode = root
        for (char in command) {
            currentNode = currentNode.children.computeIfAbsent(char) { TrieNode() }
        }
        currentNode.commandModule = module
    }

    fun find(commandPrefix: String): CommandModule? {
        var currentNode = root
        for (char in commandPrefix) {
            currentNode = currentNode.children[char] ?: return null
        }
        return currentNode.commandModule
    }
}
