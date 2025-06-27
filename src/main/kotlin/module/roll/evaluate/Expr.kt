package uk.akane.fatal.module.roll.evaluate

import uk.akane.fatal.module.roll.evaluate.DiceUtils.SHOW_STEP_COUNT_MAX
import uk.akane.fatal.utils.Bundle
import uk.akane.fatal.utils.IllegalSyntaxException
import uk.akane.fatal.utils.keepHighest
import uk.akane.fatal.utils.keepLowest
import uk.akane.fatal.utils.roundUpToMinimum
import kotlin.math.pow

object Expr {

    sealed class Expr {
        data class Literal(val value: Long) : Expr() { override fun toString(): String = value.toString() }
        data class Grouping(val expr: Expr) : Expr() { override fun toString(): String = "($expr)" }
        data class Unary(val op: Op, val right: Expr) : Expr() { override fun toString(): String = "$op $right"}
        data class Binary(val op: Op, val left: Expr, val right: Expr) : Expr() { override fun toString() = "$left $op $right"}
        data class ModifiableBinary(val op: Op, val left: Expr, val right: Expr) : Expr() { override fun toString() = "$left$op$right"}
        data class Modifier(val op: Op, val source: Expr, val parameter: Expr) : Expr() { override fun toString() = "$source$op$parameter"}
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
            is Expr.Modifier -> evaluateModifier(expr.op, expr.source, expr.parameter, bundle).first.sum()
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

            is Expr.Modifier -> evaluateModifier(expr.op, expr.source, expr.parameter, bundle).first

            else -> throw IllegalSyntaxException("Expression cannot be modified: $expr")
        }

        return endList
    }

    private fun evaluateModifier(op: Op, source: Expr, param: Expr, bundle: Bundle): Pair<List<Long>, Bundle> {
        val raw = evaluateModifiableBinary(source, bundle)
        val paramValue = evaluateInternal(param, bundle)

        val selected = when (op) {
            Op.KeepHighest -> raw.keepHighest(paramValue)
            Op.KeepLowest -> raw.keepLowest(paramValue)
            Op.Minimum -> raw.roundUpToMinimum(paramValue)
            else -> throw IllegalSyntaxException("Unsupported modifier op: $op")
        }

        val key = Expr.Modifier(op, source, param).toString()
        bundle.extendOnDemand(
            key,
            "$op$paramValue → " +
                if (selected.size <= SHOW_STEP_COUNT_MAX)
                    "$selected"
                else
                    "[${selected.sum()}]"
            ,
            op,
            param
        )

        return selected to bundle
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