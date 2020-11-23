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
        File file = new File("5-11-Java-IV-82-Ivashchenko.txt");
        Scanner scanner = new Scanner(file);
        StringBuilder text = new StringBuilder();
        while (scanner.hasNextLine()) {
            text.append(scanner.nextLine()).append(System.getProperty( "line.separator" ));
        }
        Logger logger = Logger.getLogger("logger");
        logger.setUseParentHandlers(false);
        FileHandler fh = new FileHandler("5-11-Java-IV-82-Ivashchenko.log");
        logger.addHandler(fh);
        scanner.close();
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        Lexer lexer = new Lexer(text.toString());
        Parser parser = new Parser(lexer);
        File sourceFile = new File("5-11-Java-IV-82-Ivashchenko.asm");
        FileWriter sourceWriter = new FileWriter(sourceFile);
        Interpreter interpreter = new Interpreter(sourceWriter, parser);
        interpreter.interpreter();
        fh.close();
    }
}
