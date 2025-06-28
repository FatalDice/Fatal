package uk.akane.fatal.module.roll.evaluate

import uk.akane.fatal.module.roll.evaluate.DiceUtils.SHOW_STEP_COUNT_MAX
import uk.akane.fatal.utils.Bundle
import uk.akane.fatal.utils.IllegalSyntaxException
import uk.akane.fatal.utils.keepHighest
import uk.akane.fatal.utils.keepLowest
import uk.akane.fatal.utils.rerollWithCondition
import uk.akane.fatal.utils.roundUpToMinimum
import uk.akane.fatal.utils.toFancyStrikethrough
import kotlin.math.pow

object Expr {

    sealed class Expr {
        data class Literal(val value: Long) : Expr() { override fun toString(): String = value.toString() }
        data class Grouping(val expr: Expr) : Expr() { override fun toString(): String = "($expr)" }
        data class Unary(val op: Op, val right: Expr) : Expr() { override fun toString(): String = "$op $right"}
        data class Binary(val op: Op, val left: Expr, val right: Expr) : Expr() { override fun toString() = "$left $op $right"}
        data class ModifiableBinary(val op: Op, val left: Expr, val right: Expr) : Expr(), Modification {
            override fun toString() = "$left$op$right"
            override var modifiableLeft: Any? = 0
            override var modifiableRight: Any? = 0
        }
        data class Modifier(val op: Op, val source: Expr, val parameter: Expr,
        ) : Expr(), Modification {
            override fun toString() = "$source$op$parameter"
            override var modifiableLeft: Any? = 0
            override var modifiableRight: Any? = 0
        }

        interface Modification {
            var modifiableLeft: Any?
            var modifiableRight: Any?
        }
    }

    sealed class Op(val symbol: String) {
        object Plus : Op("+")
        object Minus : Op("-")
        object Multiply : Op("*")
        object Divide : Op("/")
        object Power : Op("^")
        object Dice : Op("d")
        object KeepHighest : Op("kh")
        object KeepLowest : Op("kl")
        object Minimum : Op("m")
        object Negative : Op("-")
        object RerollSmallerThan: Op("<")
        object RerollLargerThan: Op(">")

        override fun toString(): String = symbol
    }

    fun evaluate(expr: Expr): Pair<Long, Bundle> {
        val bundle = Bundle()
        return evaluateInternal(expr, bundle) to bundle
    }

    private fun evaluateInternal(expr: Expr, bundle: Bundle): Long =
        when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Grouping -> evaluateInternal(expr.expr, bundle)
            is Expr.Unary -> evaluateUnary(expr.op, expr.right, bundle)
            is Expr.Binary -> evaluateBinary(expr.op, expr.left, expr.right, bundle)
            is Expr.ModifiableBinary -> evaluateModifiableBinary(expr, bundle).sum()
            is Expr.Modifier -> evaluateModifier(expr, bundle).first.sum()
        }

    private fun evaluateBinary(op: Op, left: Expr, right: Expr, bundle: Bundle): Long {
        val leftValue = evaluateInternal(left, bundle)
        val rightValue = evaluateInternal(right, bundle)

        return when (op) {
            Op.Plus -> leftValue + rightValue
            Op.Minus -> leftValue - rightValue
            Op.Multiply -> leftValue * rightValue
            Op.Divide -> leftValue / rightValue
            Op.Power -> leftValue.toDouble().pow(rightValue.toDouble()).toLong()
            else -> throw IllegalSyntaxException("Illegal operator $op")
        }
    }

    private fun evaluateUnary(op: Op, right: Expr, bundle: Bundle): Long {
        val rightValue = evaluateInternal(right, bundle)
        return when (op) {
            Op.Negative -> -rightValue
            else -> throw IllegalSyntaxException("Illegal operator $op")
        }
    }

    private fun evaluateModifiableBinary(expr: Expr, bundle: Bundle): List<Long> {
        val endList = when (expr) {
            is Expr.ModifiableBinary -> {
                if (expr.op != Op.Dice)
                    throw IllegalSyntaxException("Only dice expressions can be modified.")
                val leftValue = evaluateInternal(expr.left, bundle)
                val rightValue = evaluateInternal(expr.right, bundle)
                expr.modifiableLeft = leftValue.toInt()
                expr.modifiableRight = rightValue.toInt()
                val rolled = DiceUtils.rollDice(leftValue.toInt(), rightValue.toInt())
                val key = expr.toString()
                bundle.put(
                    key,
                    if (rolled.size <= SHOW_STEP_COUNT_MAX)
                        rolled.toString()
                    else
                        "[${rolled.sum()}]"
                )
                rolled
            }

            is Expr.Modifier -> evaluateModifier(expr, bundle).first

            else -> throw IllegalSyntaxException("Expression cannot be modified: $expr")
        }

        return endList
    }

    private fun evaluateModifier(expr: Expr.Modifier, bundle: Bundle): Pair<List<Long>, Bundle> {
        val raw = evaluateModifiableBinary(expr.source, bundle)
        (expr.source as Expr.Modification).let {
            expr.modifiableLeft = it.modifiableLeft
            expr.modifiableRight = it.modifiableRight
        }
        val paramValue = evaluateInternal(expr.parameter, bundle)

        val (selected, formatted) = when (expr.op) {
            Op.KeepHighest -> raw.keepHighest(paramValue) to ""
            Op.KeepLowest -> raw.keepLowest(paramValue) to ""
            Op.Minimum -> raw.roundUpToMinimum(paramValue) to ""
            Op.RerollSmallerThan -> raw.rerollWithCondition({ it < paramValue }, expr.modifiableRight as Int)
            Op.RerollLargerThan -> raw.rerollWithCondition({ it > paramValue }, expr.modifiableRight as Int)
            else -> throw IllegalSyntaxException("Unsupported modifier op: ${expr.op}")
        }

        val key = Expr.Modifier(expr.op, expr.source, expr.parameter).toString()
        bundle.extendOnDemand(
            key,
            "${expr.op}$paramValue → " +
                if (selected.size <= SHOW_STEP_COUNT_MAX && formatted.isBlank())
                    "$selected"
                else if (selected.size <= SHOW_STEP_COUNT_MAX)
                    formatted
                else
                    "[${selected.sum()}]",
            expr.op,
            expr.parameter
        )

        return selected.map { it } to bundle
    }


    private fun Bundle.extendOnDemand(
        currentKey: String,
        currentValue: String,
        operator: Op,
        parameter: Expr
    ) {
        val baseKey = currentKey.removeSuffix("$operator$parameter")
        val prev = get<String>(baseKey)
        put(currentKey, (prev ?: "") + currentValue)
    }

    fun reassembleExpression(expr: Expr, bundle: Bundle): String =
        when (expr) {
            is Expr.Literal -> expr.value.toString()
            is Expr.Grouping -> "(${reassembleExpression(expr.expr, bundle)})"
            is Expr.Unary -> when (expr.op) {
                Op.Negative -> "-${reassembleExpression(expr.right, bundle)}"
                else -> throw IllegalSyntaxException("Unsupported unary operator: ${expr.op}")
            }

            is Expr.Binary -> {
                val left = reassembleExpression(expr.left, bundle)
                val right = reassembleExpression(expr.right, bundle)
                "$left ${expr.op} $right"
            }

            else -> {
                bundle.get<String>(expr.toString()) ?:
                    throw IllegalSyntaxException("Error encountered during reassemble")
            }
        }
}