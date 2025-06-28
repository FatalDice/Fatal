package uk.akane.fatal.module.roll.evaluate

import uk.akane.fatal.utils.IllegalSyntaxException
import uk.akane.fatal.utils.ParseException

object Lexeme {
    data class Token(
        val type: TokenType,
        val lexeme: String,
        val value: Any?
    )

    enum class TokenType {
        NUMBER,
        LEFT_BRACE,
        RIGHT_BRACE,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        POW,
        DICE,
        KEEP_HIGHEST,
        KEEP_LOWEST,
        MINIMUM,
        REROLL_SMALLER_THAN,
        REROLL_LARGER_THAN
    }

    fun tokenize(input: String): List<Token> = tokenizeInternal(input.lowercase())

    private fun tokenizeInternal(input: String): List<Token> {

        fun scanToken(chars: List<Char>): Pair<Token, Int> {
            val first = chars.first()
            return when {
                first.isDigit() -> {
                    val number = chars.takeWhile { it.isDigit() }.joinToString("")
                    Token(TokenType.NUMBER, number, number.toLong()) to number.length
                }

                first == 'k' -> {
                    if (chars.size < 2) throw ParseException("Incomplete syntax was thrown into parser")
                    val op = chars.take(2).joinToString("")
                    val type =
                        when (op) {
                            "kh" -> TokenType.KEEP_HIGHEST
                            "kl" -> TokenType.KEEP_LOWEST
                            else -> throw ParseException("Incomplete syntax was thrown into parser")
                        }
                    Token(type, op, null) to 2
                }

                first == '+' -> Token(TokenType.PLUS, "+", null) to 1
                first == '-' -> Token(TokenType.MINUS, "-", null) to 1
                first == '*' -> Token(TokenType.MULTIPLY, "*", null) to 1
                first == '/' -> Token(TokenType.DIVIDE, "/", null) to 1
                first == '^' -> Token(TokenType.POW, "^", null) to 1
                first == '(' -> Token(TokenType.LEFT_BRACE, "(", null) to 1
                first == ')' -> Token(TokenType.RIGHT_BRACE, ")", null) to 1
                first == 'd' -> Token(TokenType.DICE, "d", null) to 1
                first == 'm' -> Token(TokenType.MINIMUM, "m", null) to 1
                first == '<' -> Token(TokenType.REROLL_SMALLER_THAN, "<", null) to 1
                first == '>' -> Token(TokenType.REROLL_LARGER_THAN, ">", null) to 1
                else -> throw IllegalSyntaxException("Invalid syntax was thrown into parser: {$first}")
            }
        }

        fun scanTokens(chars: List<Char>, start: Long = 0): List<Token> = when {
            chars.isEmpty() -> emptyList()
            chars.first().isWhitespace() -> scanTokens(chars.drop(1), start)
            else -> {
                val (token, length) = scanToken(chars)
                listOf(token) + scanTokens(chars.drop(length), start + length)
            }
        }

        return scanTokens(input.toList())
    }

}