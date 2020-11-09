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
        if (currentToken.getType().equals(Type.INT)) {
            eat(Type.INT);
        } else if (currentToken.getType().equals(Type.CHAR)) {
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
        if (node instanceof Compound) {
            result.add(statement());
        }
        while (currentToken.getType().equals(Type.SEMI)) {
            eat(Type.SEMI);
            AST res = statement();
            result.add(res);
            if (res instanceof Compound) {
                result.add(statement());
            }
        }
        return result;
    }

    public AST statement() {
        if (currentToken.getType().equals(Type.LBRACER)) {
            return compoundStatement();
        } else if (currentToken.getType().equals(Type.RETURN)) {
            return returnStatement();
        } else if (currentToken.getType().equals(Type.INT)) {
            return assignmentStatement();
        } else if (currentToken.getType().equals(Type.CHAR)) {
                return assignmentStatement();
        } else if (currentToken.getType().equals(Type.ID)) {
            return reAssignStatment();
        } else {
            return new NoOp();
        }
    }

    public AST returnStatement() {
        eat(Type.RETURN);
        VType vType = new VType(currentToken);
        return new ReturnOp(logic(), vType);
    }

    public AST assignmentStatement() {
        VType type;
        if (currentToken.getType().equals(Type.INT)){
            type = new VType(new Token(Type.DECIMAL, "DECIMAL"));
            eat(Type.INT);
        } else if (currentToken.getType().equals(Type.CHAR)){
            type = new VType(new Token(Type.CHAR, "CHAR"));
            eat(Type.CHAR);
        } else {
            type = null;
        }
        AST left = variable();
        Token token = currentToken;
        if (currentToken.getType().equals(Type.SEMI)) {
            return new Assing(null, token, left, type);
        }
        eat(Type.ASSIGN);
        AST right = logic();
        if (currentToken.getType().equals(Type.VOPROS)){
            AST expr = right;
            eat(Type.VOPROS);
            AST leftt = logic();
            eat(Type.DOTS);
            AST rightt = logic();
            return new Assing(new CondExp(leftt, rightt, expr), token, left, type);
        }
        return new Assing(right, token, left, type);
    }

    public AST reAssignStatment() {
        AST left = variable();
        eat(Type.ASSIGN);
        Token token = currentToken;
        AST right = logic();
        if (currentToken.getType().equals(Type.VOPROS)){
            AST expr = right;
            eat(Type.VOPROS);
            AST leftt = logic();
            eat(Type.DOTS);
            AST rightt = logic();
            return new reAssign(new CondExp(leftt, rightt, expr), token, left);
        }
        return new reAssign(right, token, left);
    }

    public AST variable() {
        AST node = new Var(currentToken);
        eat(Type.ID);
        return node;
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
        if (token.getType().equals(Type.CHAR)) {
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
        } else {
            try {
                return variable();
            } catch (Exception exception) {
                logger.warning("expected value on line" + lexer.getLine() + " at " + pos);
                throw new RuntimeException("expected value on line " + lexer.getLine() + " at " + pos);
            }
        }
    }

    private AST logic() {
        AST node = expr();
        while (currentToken.getType().equals(Type.OR)) {
            Token token = currentToken;
            if (token.getType().equals(Type.OR)) {
                eat(Type.OR);
            }
            node = new BinOp(node, expr(), token);
        }
        return node;
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
