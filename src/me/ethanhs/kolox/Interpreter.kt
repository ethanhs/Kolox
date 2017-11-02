package me.ethanhs.kolox

import me.ethanhs.kolox.TokenType.*

internal class LoxRuntimeError(val tok: Token?,
                            msg: String) : RuntimeException(msg)

class Interpreter: ExprVisitor<Any?> {

    fun stringify(value: Any?): String {
        return when (value) {
            null -> "nil"
            is String -> "\"$value\""
            else -> value.toString()
        }
    }

    fun interpret(expression: Expr) {
        try {
            val value = eval(expression)
            println(stringify(value))
        } catch (error: LoxRuntimeError) {
            err(-1, "Runtime error: halting interpreter")
        }

    }

    fun eval(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw LoxRuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token,
                                    left: Any,
                                    right: Any) {
        if (left is Double && right is Double) return
        throw LoxRuntimeError(operator, "Operands must be numbers.")
    }

    private fun isTruthy(o: Any?): Boolean {
        return when (o) {
            null -> false
            is Boolean -> o
            0.0 -> false
            else -> true
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        return when (a) {
            null -> false
            else -> a == b
        }
    }

    override fun visitBinaryExpr(expr: Binary): Any? {
        val left = eval(expr.left)
        val right = eval(expr.right)

        when {
            left is Double && right is Double -> {
                checkNumberOperands(expr.op, left, right)
                return when (expr.op.type) {
                    PLUS -> left + right
                    MINUS -> left - right
                    SLASH -> left / right
                    STAR -> left * right
                    GREATER -> left > right
                    GREATER_EQUAL -> left >= right
                    LESS -> left < right
                    LESS_EQUAL -> left <= right
                    else -> null
                }
            }
            else -> return when (expr.op.type) {
                BANG_EQUAL -> !isEqual(left, right)
                EQUAL_EQUAL -> isEqual(left, right)
                PLUS -> {
                    return when {
                        left is String && right is String -> left + right
                        else -> LoxRuntimeError(expr.op,"Cannot add ${stringify(left)} to ${stringify(right)}")
                    }
                }
                else -> null
            }
        }
    }

    override fun visitGroupingExpr(expr: Grouping): Any? {
        return eval(expr.expr)
    }

    override fun visitLiteralExpr(expr: Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Unary): Any? {
        val right = eval(expr.right)
        return when (expr.op.type) {
            MINUS -> {
                checkNumberOperand(expr.op, right);
                -(right as Double)
            }
            BANG -> !isTruthy(right)
            else -> null
        }
    }

    override fun visitInvalidExpr(expr: InvalidExpr): Any? {
        throw LoxRuntimeError(null,"Woooah we should never reach here.")
    }
}