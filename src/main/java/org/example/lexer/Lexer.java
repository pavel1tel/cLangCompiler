package org.example.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String text;
    private Integer pos;
    private Character currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
        currentChar = text.charAt(pos);
    }

    public Character peek() {
        int peek_pos = pos + 1;
        if(pos >(text.length()-1)) {
            currentChar = null;
        } else {
            return text.charAt(peek_pos);
        }
        throw new RuntimeException();
    }

    public Token id() {
        StringBuilder result = new StringBuilder();
        while (currentChar != null && (Character.isAlphabetic(currentChar) || Character.isDigit(currentChar))){
            result.append(currentChar.toString());
            advance();
        }
        return new Token(Type.valueOf(result.toString().toUpperCase()), result.toString());
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

    public String parseHex() {
        StringBuilder result = new StringBuilder();
        result.append("0x");
        List<Character> hexCHars = new ArrayList<>();
        hexCHars.add('a');
        hexCHars.add('b');
        hexCHars.add('c');
        hexCHars.add('d');
        hexCHars.add('e');
        hexCHars.add('f');
        while (currentChar != null && (Character.isDigit(currentChar) ||  hexCHars.contains(currentChar))){
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
            if (Character.isDigit(currentChar) && currentChar != '0') {
                return new Token(Type.DECIMAL, parseInteger());
            }

            if (Character.isDigit(currentChar) && currentChar == '0'){
                if(peek() == 'x') {
                    advance();
                    advance();
                    return new Token(Type.HEX, parseHex());
                }
            }
            if(Character.isAlphabetic(currentChar)) {
                return id();
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
            if (currentChar.equals(';')){
                advance();
                return new Token(Type.SEMI, ";");
            }
            if (currentChar.equals('{')){
                advance();
                return new Token(Type.LBRACER, "{");
            }
            if (currentChar.equals('}')){
                advance();
                return new Token(Type.RBRACER, "}");
            }
            throw new RuntimeException();
        }
        return new Token(Type.EOF, null);
    }
}
