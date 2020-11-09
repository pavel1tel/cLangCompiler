package org.example.parser;

import org.example.lexer.Token;

import java.util.List;

public abstract class AST {
    private AST left;
    private AST right;
    private Token token;
    private Token op;
    private Token value;
    private AST expr;
    private List<AST> children;
    private AST valueType;

    public abstract AST getLeft();
    public abstract AST getRight();
    public abstract Token getToken();
    public abstract Token getOp();
    public abstract String getValue();

    public AST getExpr() {
        return null;
    }

    public List<AST> getChildren() {
        return null;
    }

    public AST getValueType(){
        return null;
    }
}
