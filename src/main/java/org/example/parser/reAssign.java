package org.example.parser;

import org.example.lexer.Token;

public class reAssign extends AST{

    private final AST right;
    private final Token op;
    private final AST left;

    public reAssign(AST right, Token op, AST left) {
        this.right = right;
        this.op = op;
        this.left = left;
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
        return op;
    }

    @Override
    public String getValue() {
        return null;
    }
}
