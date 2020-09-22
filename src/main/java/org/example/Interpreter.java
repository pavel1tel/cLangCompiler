package org.example;

public class Interpreter {
    private final Lexer lexer;
    private Token currentToken;

    public Interpreter(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }



    public void eat(Type type) {
        if (currentToken.getType().equals(type)) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException();
        }
    }

    public Integer factor() {
        // factor : INTEGER
        Token token = currentToken;
        eat(Type.INTEGER);
        return Integer.parseInt(token.getValue());
    }

    public int term() {
        // term : factor ((MUL | DIV) factor)
        Integer result = factor();
        while (currentToken.getType().equals(Type.DIV) || currentToken.getType().equals(Type.MUL)){
            Token token = currentToken;
            if (token.getType().equals(Type.DIV)){
                eat(Type.DIV);
                result = result / factor();
            } else if (token.getType().equals(Type.MUL)) {
                eat(Type.MUL);
                result = result * factor();
            }
        }
        return result;
    }

    public int expr() {
        // expr: term(PLUS | MINUS) term)
        Integer result = term();
        while (currentToken.getType().equals(Type.PLUS) || currentToken.getType().equals(Type.MINUS)){
            Token token = currentToken;
            if (token.getType().equals(Type.PLUS)){
                eat(Type.PLUS);
                result = result + term();
            } else {
                eat(Type.MINUS);
                result = result - term();
            }
        }
        return result;
    }
}
