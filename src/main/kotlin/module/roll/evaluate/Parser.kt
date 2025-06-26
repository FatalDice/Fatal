package uk.akane.fatal.module.roll.evaluate

import uk.akane.fatal.utils.ParseException
import uk.akane.fatal.module.roll.evaluate.Lexeme.TokenType
import uk.akane.fatal.module.roll.evaluate.Lexeme.Token
import uk.akane.fatal.module.roll.evaluate.Expr.Op
import uk.akane.fatal.module.roll.evaluate.Expr.Expr

object Parser {

    fun TokenType.toOp(): Op = when (this) {
        TokenType.PLUS -> Op.Plus
        TokenType.MINUS -> Op.Minus
        TokenType.MULTIPLY -> Op.Multiply
        TokenType.DIVIDE -> Op.Divide
        TokenType.POW -> Op.Power
        TokenType.DICE -> Op.Dice
        TokenType.KEEP_HIGHEST -> Op.KeepHighest
        TokenType.KEEP_LOWEST -> Op.KeepLowest
        TokenType.MINIMUM -> Op.Minimum
        else -> throw TypeCastException("${this.javaClass} has no destination for cast")
    }

    private fun parseBinary(
        nextPrecedence: (List<Token>) -> Pair<Expr, List<Token>>,
        tokens: List<Token>,
        ops: List<TokenType>
    ): Pair<Expr, List<Token>> {
        val (initialExpr, remainingTokens) = nextPrecedence(tokens)
        return processBinaryExpression(initialExpr, remainingTokens, nextPrecedence, ops)
    }

    private fun processBinaryExpression(
        expr: Expr,
        remainingTokens: List<Token>,
        nextPrecedence: (List<Token>) -> Pair<Expr, List<Token>>,
        ops: List<TokenType>
    ): Pair<Expr, List<Token>> {
        if (remainingTokens.isEmpty() || remainingTokens.first().type !in ops) {
            return expr to remainingTokens
        }

        val operator = remainingTokens.first().type.toOp()
        val (rightExpr, newRemainingTokens) = nextPrecedence(remainingTokens.drop(1))
        return processBinaryExpression(
            Expr.Binary(operator, expr, rightExpr),
            newRemainingTokens,
            nextPrecedence,
            ops
        )
    }

    private fun parseModifiableBinary(
        nextPrecedence: (List<Token>) -> Pair<Expr, List<Token>>,
        tokens: List<Token>,
        ops: List<TokenType>
    ): Pair<Expr, List<Token>> {
        val (left, remainingTokens) = nextPrecedence(tokens)
        if (remainingTokens.isEmpty() || remainingTokens.first().type !in ops) {
            return left to remainingTokens
        }

        val op = remainingTokens.first().type.toOp()
        val (right, rest) = nextPrecedence(remainingTokens.drop(1))

        return Expr.ModifiableBinary(op, left, right) to rest
    }

    private fun parseModifier(
        nextPrecedence: (List<Token>) -> Pair<Expr, List<Token>>,
        tokens: List<Token>,
        ops: List<TokenType>
    ): Pair<Expr, List<Token>> {
        var (expr, remainingTokens) = nextPrecedence(tokens)

        while (
            remainingTokens.isNotEmpty() &&
            remainingTokens.first().type in ops
        ) {
            val op = remainingTokens.first().type.toOp()
            val (param, rest) = nextPrecedence(remainingTokens.drop(1))

            expr = Expr.Modifier(op, expr, param)
            remainingTokens = rest
        }

        return expr to remainingTokens
    }


    private fun primary(tokens: List<Token>): Pair<Expr, List<Token>> {
        val token = tokens.firstOrNull() ?: throw ParseException("Unexpected end of input")

        return when (token.type) {
            TokenType.NUMBER -> {
                Expr.Literal(token.value as Long) to tokens.drop(1)
            }

            TokenType.LEFT_BRACE -> {
                val (expr, rest) = parse(tokens.drop(1))
                if (rest.firstOrNull()?.type == TokenType.RIGHT_BRACE) {
                    Expr.Grouping(expr) to rest.drop(1)
                } else {
                    throw ParseException("Expected ')' after expression.")
                }
            }

            TokenType.MINUS -> {
                val (rightExpr, rest) = primary(tokens.drop(1))
                Expr.Unary(Op.Negative, rightExpr) to rest
            }

            else -> throw ParseException("Unexpected token: $token")
        }
    }

    private fun modifiable(tokens: List<Token>): Pair<Expr, List<Token>> =
        parseModifiableBinary(::primary, tokens, listOf(TokenType.DICE))

    private fun modifier(tokens: List<Token>): Pair<Expr, List<Token>> =
        parseModifier(::modifiable, tokens, listOf(TokenType.KEEP_HIGHEST, TokenType.KEEP_LOWEST, TokenType.MINIMUM))

    private fun pow(tokens: List<Token>): Pair<Expr, List<Token>> =
        parseBinary(::modifier, tokens, listOf(TokenType.POW))

    private fun mul(tokens: List<Token>): Pair<Expr, List<Token>> =
        parseBinary(::pow, tokens, listOf(TokenType.MULTIPLY, TokenType.DIVIDE))

    private fun add(tokens: List<Token>): Pair<Expr, List<Token>> =
        parseBinary(::mul, tokens, listOf(TokenType.PLUS, TokenType.MINUS))

    fun parse(tokens: List<Token>): Pair<Expr, List<Token>> {
        return add(tokens)
    }

}