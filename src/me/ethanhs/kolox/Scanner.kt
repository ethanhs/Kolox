package me.ethanhs.kolox

import me.ethanhs.kolox.TokenType.*

class Scanner(private var src: String) {
    private var toks = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1
    private val keywords = HashMap<String, TokenType>()

    init {
        keywords.put("and", AND)
        keywords.put("class", CLASS)
        keywords.put("else", ELSE)
        keywords.put("false", FALSE)
        keywords.put("for", FOR)
        keywords.put("fun", FUN)
        keywords.put("if", IF)
        keywords.put("nil", NIL)
        keywords.put("or", OR)
        keywords.put("print", PRINT)
        keywords.put("return", RETURN)
        keywords.put("super", SUPER)
        keywords.put("this", THIS)
        keywords.put("true", TRUE)
        keywords.put("var", VAR)
        keywords.put("while", WHILE)
    }

    private fun isEOF(): Boolean = current >= src.length

    fun scanTokens(): List<Token> {
        while (!isEOF()) {
            start = current
            scanToken()
        }
        toks.add(Token(EOF, "", null, line))
        return toks
    }

    private fun advance(): Char {
        current++
        return src[current - 1]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        toks.add(Token(type, src.substring(start, current), literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isEOF()) return false
        if (src[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char {
        if (isEOF()) return '\u0000'
        return src[current]
    }

    private fun string() {
        while (peek() != '"' && !isEOF()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isEOF()) err(line, "End of File while parsing string literal.")
        advance()
        addToken(STRING, src.substring(start + 1, current - 1))
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun peekNext(): Char {
        return if (current + 1 >= src.length) '\u0000' else src[current + 1]
    }

    private fun number() {
        while (isDigit(peek())) advance()
        if (peek() == '.' && isDigit(peekNext())) {
            advance()
            while (isDigit(peek())) advance()
        }
        addToken(NUMBER, java.lang.Double.parseDouble(src.substring(start, current)))
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isAlphaNum(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun identifier() {
        while (isAlphaNum(peek())) advance()
        var type = keywords[src.substring(start, current)]
        if (type == null) type = IDENTIFIER
        addToken(type)
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            ')' -> addToken(LEFT_PAREN)
            '(' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else EQUAL)
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isEOF()) advance()
                } else {
                    addToken(SLASH)
                }
            }
            ' ', '\r', '\t' -> {
            }
            '\n' -> line++
            '"' -> string()
            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlphaNum(c)) {
                    identifier()
                } else {
                    err(line, "Invalid character.")
                }
            }
        }
    }
}