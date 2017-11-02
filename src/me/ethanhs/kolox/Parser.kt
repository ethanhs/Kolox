package me.ethanhs.kolox

import me.ethanhs.kolox.TokenType.*


class Parser(private val tokens: List<Token>) {
    private var current: Int = 0

    private class ParseError : RuntimeException()

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun isEOF(): Boolean {
        return peek().type == EOF
    }

    private fun advance(): Token {
        if (!isEOF()) current++
        return previous()
    }

    private fun checkType(type: TokenType): Boolean {
        if (isEOF()) return false
        return peek().type == type
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (checkType(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun error(token: Token, message: String): ParseError {
        report_err(token, message)
        return ParseError()
    }

    private fun consume(type: TokenType, msg: String): Token {
        if (checkType(type)) return advance()

        throw error(peek(), msg)
    }

    private fun synchronize() {
        advance()

        while (!isEOF()) {
            if (previous().type === SEMICOLON) return

            when (peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return

            }

            advance()
        }
    }

    private fun primary(): Expr {
        if (match(FALSE)) return Literal(false)
        if (match(TRUE)) return Literal(true)
        if (match(NIL)) return Literal(null)

        if (match(NUMBER, STRING)) {
            return Literal(previous().literal)
        }

        if (match(LEFT_PAREN)) {
            val expr = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")
            return Grouping(expr)
        }
        throw error(peek(), "Expected an expression.")
    }

    private fun unary(): Expr {
        if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

    private fun multiplication(): Expr {
        var expr = unary()

        while (match(STAR, SLASH)) {
            val op = previous()
            val right = unary()
            expr = Binary(expr, op, right)
        }

        return expr
    }

    private fun addition(): Expr {
        var expr = multiplication()

        while (match(PLUS, MINUS)) {
            val op = previous()
            val right = multiplication()
            expr = Binary(expr, op, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = addition()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val op = previous()
            val right = addition()
            expr = Binary(expr, op, right)
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val op = previous()
            val right = comparison()
            expr = Binary(expr, op, right)
        }
        return expr
    }

    private fun expression(): Expr {
        return equality()
    }

    fun parse(): Expr {
        return try {
            expression()
        } catch (err: ParseError) {
            return InvalidExpr()
        }
    }

}