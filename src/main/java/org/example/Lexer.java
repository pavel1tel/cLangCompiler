package org.example;

public class Lexer {
    private String text;
    private Integer pos;
    private Character currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
        currentChar = text.charAt(pos);
    }

    public void advance() {
        pos++;
        if(pos >(text.length()-1)) {
            currentChar = null;
        } else {
            currentChar = text.charAt(pos);
        }
    }

    public void skipWhiteSpaces() {
        while (currentChar != null && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    public String parseInteger() {
        StringBuilder result = new StringBuilder();
        while (currentChar != null && Character.isDigit(currentChar)){
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    public Token getNextToken() {
        while (currentChar != null) {
            if (Character.isWhitespace(currentChar)) {
                skipWhiteSpaces();
                continue;
            }
            if (Character.isDigit(currentChar)) {
                return new Token(Type.INTEGER, parseInteger());
            }
            if (currentChar.equals('+')){
                advance();
                return new Token(Type.PLUS, "+");
            }
            if (currentChar.equals('-')){
                advance();
                return new Token(Type.MINUS, "-");
            }
            if (currentChar.equals('*')){
                advance();
                return new Token(Type.MUL, "*");
            }
            if (currentChar.equals('/')){
                advance();
                return new Token(Type.DIV, "/");
            }
            if (currentChar.equals('(')){
                advance();
                return new Token(Type.LPARENT, "(");
            }
            if (currentChar.equals(')')){
                advance();
                return new Token(Type.RPARENT, ")");
            }
            throw new RuntimeException();
        }
        return new Token(Type.EOF, null);
    }
}
