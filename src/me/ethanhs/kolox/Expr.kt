package me.ethanhs.kolox


interface ExprVisitor<T> {
    //fun visitAssignExpr(expr: Assign): T
    fun visitBinaryExpr(expr: Binary): T
    //fun visitCallExpr(expr: Call): T
    //fun visitGetExpr(expr: Get): T
    fun visitGroupingExpr(expr: Grouping): T
    fun visitLiteralExpr(expr: Literal): T
    //fun visitLogicalExpr(expr: Logical): T
    //fun visitSetExpr(expr: Set): T
    //fun visitSuperExpr(expr: Super): T
    //fun visitThisExpr(expr: This): T
    fun visitUnaryExpr(expr: Unary): T
    //fun visitVariableExpr(expr: Variable): T
    fun visitInvalidExpr(expr: InvalidExpr): T
}

abstract class Expr {
    abstract fun <T> accept(visitor: ExprVisitor<T>): T
}

class InvalidExpr: Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitInvalidExpr(this)
    }
}

data class Binary(val left: Expr,
                      val op: Token,
                      val right: Expr): Expr() {

    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitBinaryExpr(this)
    }
}

data class Grouping(val expr: Expr): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitGroupingExpr(this)
    }
}

data class Literal(val value: Any?): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitLiteralExpr(this)
    }
}

data class Unary(val op: Token,
                 val right: Expr): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitUnaryExpr(this)
    }
}