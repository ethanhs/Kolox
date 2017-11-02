package me.ethanhs.kolox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


fun report_err(token: Token, msg: String) {
    if (token.type === TokenType.EOF) {
        report(token.line, " at end of file", msg)
    } else {
        report(token.line, " at '" + token.lexeme + "'", msg)
    }
}

fun report(line: Int, where: String, msg: String) {
    println("[line $line] Error $where: $msg")
}

fun err(line: Int, msg: String) {
    report(line, "", msg)
}

fun run(src: String): Boolean {
    val scanner = Scanner(src)
    val toks = scanner.scanTokens()
    val parser = Parser(toks)
    val expr = parser.parse()
    if (expr is InvalidExpr) return false // there was a parse error
    val interp = Interpreter()
    interp.interpret(expr)
    return true
}

fun runFile(path: String) {
    val f = Files.readAllBytes(Paths.get(path))
    val err = run(String(f))
    if (err) {
        exitProcess(42)
    }
}

fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)
    while (true) {
        print("~>")
        run(reader.readLine())
    }
}

fun main(args: Array<String>) {
    when {
        args.size > 1 -> println("Usage: kolox [script]")
        args.size == 1 -> runFile(args[0])
        else -> runPrompt()
    }
}