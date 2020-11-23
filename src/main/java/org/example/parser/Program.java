package org.example.parser;

import org.example.lexer.Token;

import java.util.List;

public class Program extends AST{
    private List<AST> children;

    public Program(List<AST> children) {
        this.children = children;
    }

    @Override
    public List<AST> getChildren() {
        return children;
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
}
