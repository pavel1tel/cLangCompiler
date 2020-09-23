package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.parser.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("src/main/java/org/example/test.c");
        Scanner scanner = new Scanner(file);
        StringBuilder text = new StringBuilder();
        while (scanner.hasNextLine()) {
            text.append(scanner.nextLine() + " ");
        }
        scanner.close();
        Lexer lexer = new Lexer(text.toString());
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        System.out.println(interpreter.interpreter());

    }
}
