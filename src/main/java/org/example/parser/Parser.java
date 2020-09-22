package org.example.parser;

import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.lexer.Type;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    private void eat(Type type) {
        if (currentToken.getType().equals(type)) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException();
        }
    }

    private AST factor() {
        // factor : INTEGER | LPAREN expr RPAREN
        Token token = currentToken;
        if (token.getType().equals(Type.INTEGER)) {
            eat(Type.INTEGER);
            return new Num(token);
        } else if (token.getType().equals(Type.LPARENT)) {
            eat(Type.LPARENT);
            AST node = expr();
            eat(Type.RPARENT);
            return node;
        }
        throw new RuntimeException();
    }

    private AST term() {
        // term : factor ((MUL | DIV) factor)
        AST node = factor();
        while (currentToken.getType().equals(Type.DIV) || currentToken.getType().equals(Type.MUL)){
            Token token = currentToken;
            if (token.getType().equals(Type.DIV)){
                eat(Type.DIV);
            } else if (token.getType().equals(Type.MUL)) {
                eat(Type.MUL);
            }
            node = new BinOp(node, factor(), token);
        }
        return node;
    }

    private AST expr() {
        // expr: term(PLUS | MINUS) term)
        AST node = term();
        while (currentToken.getType().equals(Type.PLUS) || currentToken.getType().equals(Type.MINUS)){
            Token token = currentToken;
            if (token.getType().equals(Type.PLUS)){
                eat(Type.PLUS);
            } else {
                eat(Type.MINUS);
            }
            node = new BinOp(node, term(), token);
        }
        return node;
    }

    public AST parse() {
        return expr();
    }
}
