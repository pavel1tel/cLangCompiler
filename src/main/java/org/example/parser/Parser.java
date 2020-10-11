package org.example.parser;

import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.lexer.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;
    private int pos;
    private int line;
    Logger logger = Logger.getLogger("logger");

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    public AST mainFunction() {
        AST valueType = typeSpec();
        eat(Type.MAIN);
        eat(Type.LPARENT);
        eat(Type.RPARENT);
        return new MainBlock(compoundStatement(), valueType);
    }

    public AST typeSpec() {
        Token token = currentToken;
        if(currentToken.getType().equals(Type.INT)){
            eat(Type.INT);
        } else if (currentToken.getType().equals(Type.CHAR)){
            eat(Type.CHAR);
        } else {
            logger.warning("Expected return type int or char at line " + line + " position " + pos);
            throw new RuntimeException("Expected return type int or char at line " + line + "position " + pos);
        }
        return new VType(token);
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
        VType vType = new VType(currentToken);
        return new ReturnOp(expr(), vType);
    }


    private void eat(Type type) {
        if (currentToken.getType().equals(type)) {
            pos = lexer.getPos() + 1;
            line = lexer.getLine();
            currentToken = lexer.getNextToken();
        } else {
            logger.warning("unexpected token in line " + line + " position " + pos + "\n Expected :" + type);
            throw new RuntimeException("unexpected token in line " + line + " position " + pos + "\n Expected :" + type);
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
        if (token.getType().equals(Type.TILDE)) {
            eat(Type.TILDE);
            return new UnaryOp(token, factor());
        }
        if (token.getType().equals(Type.DECIMAL)) {
            eat(Type.DECIMAL);
            return new Num(token, new VType(new Token(Type.DECIMAL, "DECIMAL")));
        }
        if (token.getType().equals(Type.CHAR)){
            eat(Type.CHAR);
            return new Char(token, new VType(new Token(Type.CHAR, "CHAR")));
        }
        if (token.getType().equals(Type.HEX)) {
            eat(Type.HEX);
            return new Num(token, new VType(new Token(Type.HEX, "HEX")));
        } else if (token.getType().equals(Type.LPARENT)) {
            eat(Type.LPARENT);
            AST node = expr();
            eat(Type.RPARENT);
            return node;
        }
        logger.warning("expected value on line" + lexer.getLine() + " at "+ pos);
        throw new RuntimeException("expected value on line " + lexer.getLine() + " at "+ pos);
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
