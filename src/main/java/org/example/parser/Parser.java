package org.example.parser;

import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.lexer.Type;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    public AST mainFunction() {
        eat(Type.MAIN);
        eat(Type.LPARENT);
        eat(Type.RPARENT);
        return new MainBlock(compoundStatement());
    }

    public AST compoundStatement() {
        eat(Type.LBRACER);
        List<AST> nodes = statementList();
        eat(Type.RBRACER);
        return new Compound(nodes);
    }

    public List<AST> statementList() {
        AST node = statement();
        List<AST> result = new ArrayList<>();
        result.add(node);
        while (currentToken.getType().equals(Type.SEMI)) {
            eat(Type.SEMI);
            result.add(statement());
        }
        return result;
    }

    public AST statement() {
        if (currentToken.getType().equals(Type.LBRACER)) {
            return compoundStatement();
        } else if (currentToken.getType().equals(Type.RETURN)) {
            return returnStatement();
        } else {
            return new NoOp();
        }
    }

    public AST returnStatement() {
        eat(Type.RETURN);
        return new ReturnOp(expr());
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
        if (token.getType().equals(Type.PLUS)) {
            eat(Type.PLUS);
            return new UnaryOp(token, factor());
        }
        if (token.getType().equals(Type.MINUS)) {
            eat(Type.MINUS);
            return new UnaryOp(token, factor());
        }
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
        while (currentToken.getType().equals(Type.DIV) || currentToken.getType().equals(Type.MUL)) {
            Token token = currentToken;
            if (token.getType().equals(Type.DIV)) {
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
        while (currentToken.getType().equals(Type.PLUS) || currentToken.getType().equals(Type.MINUS)) {
            Token token = currentToken;
            if (token.getType().equals(Type.PLUS)) {
                eat(Type.PLUS);
            } else {
                eat(Type.MINUS);
            }
            node = new BinOp(node, term(), token);
        }
        return node;
    }

    public AST parse() {
        return mainFunction();
    }
}
