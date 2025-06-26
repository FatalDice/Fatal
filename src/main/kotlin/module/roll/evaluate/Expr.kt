package uk.akane.fatal.module.roll.evaluate

import uk.akane.fatal.utils.IllegalSyntaxException
import kotlin.math.pow

sealed class Expr {
    data class Literal(val value: Long) : Expr()
    data class Grouping(val expr: Expr) : Expr()
    data class Unary(val op: Op, val right: Expr) : Expr()
    data class Binary(val op: Op, val left: Expr, val right: Expr) : Expr()
    data class ModifiableBinary(val op: Op, val left: Expr, val right: Expr) : Expr()
    data class Modifier(val op: Op, val source: Expr, val parameter: Expr) : Expr()
}

sealed class Op {
    object Plus : Op()
    object Minus : Op()
    object Multiply : Op()
    object Divide : Op()
    object Power : Op()
    object Dice : Op()
    object KeepHighest : Op()
    object KeepLowest : Op()
    object Minimum : Op()
    object Negative : Op()
}

fun evaluate(expr: Expr): Long = when (expr) {
    is Expr.Literal -> expr.value
    is Expr.Grouping -> evaluate(expr.expr)
    is Expr.Unary -> evaluateUnary(expr.op, expr.right)
    is Expr.Binary -> evaluateBinary(expr.op, expr.left, expr.right)
    is Expr.ModifiableBinary -> evaluateModifiableBinary(expr.op, expr.left, expr.right)
    is Expr.Modifier -> evaluateModifier(expr.op, expr.source, expr.parameter).sum()
}


fun evaluateBinary(op: Op, left: Expr, right: Expr): Long {
    val leftValue = evaluate(left)
    val rightValue = evaluate(right)

    return when (op) {
        Op.Plus -> leftValue + rightValue
        Op.Minus -> leftValue - rightValue
        Op.Multiply -> leftValue * rightValue
        Op.Divide -> leftValue / rightValue
        Op.Power -> leftValue.toDouble().pow(rightValue.toDouble()).toLong()
        else -> throw IllegalSyntaxException("Illegal operator $op")
    }
}

fun evaluateUnary(op: Op, right: Expr): Long {
    val rightValue = evaluate(right)
    return when (op) {
        Op.Negative -> -rightValue
        else -> throw IllegalSyntaxException("Illegal operator $op")
    }
}

fun evaluateModifiableBinary(op: Op, left: Expr, right: Expr): Long {
    val leftValue = evaluate(left)
    val rightValue = evaluate(right)

    return when (op) {
        Op.Dice -> {
            val rolled = DiceUtils.rollDice(leftValue.toInt(), rightValue.toInt())
            rolled.sum()
        }
        else -> throw IllegalSyntaxException("Illegal operator $op")
    }
}

fun evaluateModifiableBinary(expr: Expr): List<Long> = when (expr) {
    is Expr.ModifiableBinary -> {
        if (expr.op != Op.Dice)
            throw IllegalSyntaxException("Only dice expressions can be modified.")
        val leftValue = evaluate(expr.left)
        val rightValue = evaluate(expr.right)
        DiceUtils.rollDice(leftValue.toInt(), rightValue.toInt())
    }

    is Expr.Modifier -> evaluateModifier(expr.op, expr.source, expr.parameter)

    else -> throw IllegalSyntaxException("Expression cannot be modified: $expr")
}

fun evaluateModifier(op: Op, source: Expr, param: Expr): List<Long> {
    val raw = evaluateModifiableBinary(source)
    val paramValue = evaluate(param)

    val selected = when (op) {
        Op.KeepHighest -> raw.sortedDescending().take(paramValue)
        Op.KeepLowest -> raw.sorted().take(paramValue)
        else -> throw IllegalSyntaxException("Unsupported modifier op: $op")
    }

    return selected
}

private fun List<Long>.take(n: Long) = this.take(n.toInt())
