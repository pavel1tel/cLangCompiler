package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.lexer.Type;
import org.example.parser.Parser;

import java.io.*;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        File file = new File("test.c");
        Scanner scanner = new Scanner(file);
        StringBuilder text = new StringBuilder();
        while (scanner.hasNextLine()) {
            text.append(scanner.nextLine()).append(System.getProperty( "line.separator" ));
        }
        Logger logger = Logger.getLogger("logger");
        logger.setUseParentHandlers(false);
        FileHandler fh = new FileHandler("build.log");
        logger.addHandler(fh);
        scanner.close();
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        Token token;
        Lexer lex = new Lexer(text.toString());
        do {
            token = lex.getNextToken();
            logger.info(token.toString());
        }while(!token.getType().equals(Type.EOF));
        Lexer lexer = new Lexer(text.toString());
        Parser parser = new Parser(lexer);
        File sourceFile = new File("source.asm");
        FileWriter sourceWriter = new FileWriter(sourceFile);
        Interpreter interpreter = new Interpreter(sourceWriter, parser);
        interpreter.interpreter();

    }
}
