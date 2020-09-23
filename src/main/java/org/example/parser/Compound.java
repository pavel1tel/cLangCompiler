package org.example.parser;

import org.example.lexer.Token;

import java.util.List;

public class Compound extends AST {

    private final List<AST> children;

    public Compound(List<AST> children) {
        this.children = children;
    }

    @Override
    public AST getLeft() {
        return null;
    }

    @Override
    public AST getRight() {
        return null;
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

    @Override
    public List<AST> getChildren() {
        return children;
    }
}
