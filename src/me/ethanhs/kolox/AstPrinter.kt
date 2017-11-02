package me.ethanhs.kolox

class AstPrinter: ExprVisitor<String> {

    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()

        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept(this))
        }
        builder.append(")")

        return builder.toString()
    }

    override fun visitGroupingExpr(expr: Grouping): String {
        return parenthesize("group", expr.expr)
    }

    override fun visitBinaryExpr(expr: Binary): String {
        return parenthesize(expr.op.lexeme, expr.left, expr.right)
    }

    override fun visitLiteralExpr(expr: Literal): String {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Unary): String {
        return parenthesize(expr.op.lexeme, expr.right)
    }

    override fun visitInvalidExpr(expr: InvalidExpr): String {
        return "(<>)"
    }
}