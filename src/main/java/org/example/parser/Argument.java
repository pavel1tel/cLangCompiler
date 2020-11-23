package org.example.parser;

import org.example.lexer.Token;

public class Argument extends AST{
    private AST left;
    private AST right;

    public Argument(AST left, AST right) {
        this.left = left;
        this.right = right;
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
