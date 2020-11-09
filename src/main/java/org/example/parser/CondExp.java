package org.example.parser;

import org.example.lexer.Token;

public class CondExp  extends AST{

    private final AST left;
    private final AST right;
    private final AST expr;

    public CondExp(AST left, AST right, AST expr) {
        this.left = left;
        this.right = right;
        this.expr = expr;
    }

    @Override
    public AST getExpr() {
        return expr;
    }

    @Override
    public AST getLeft() {
        return left;
    }

    @Override
    public AST getRight() {
        return right;
    }

    @Override
    public Token getToken() {
        return null;
    }

    @Override
    public Token getOp() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
