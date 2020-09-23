package org.example;

import org.example.parser.AST;
import org.example.parser.BinOp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//todo add unary node support
public class TreePrinter {
    public static <T extends Comparable<?>> void printAST(AST root) {
        int maxLevel = TreePrinter.maxLevel(root);

        printASTInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private static <T extends Comparable<?>> void printASTInternal(List<AST> ASTs, int level, int maxLevel) {
        if (ASTs.isEmpty() || TreePrinter.isAllElementsNull(ASTs))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        TreePrinter.printWhitespaces(firstSpaces);

        List<AST> newASTs = new ArrayList<AST>();
        for (AST AST : ASTs) {
            if (AST instanceof BinOp) {
                System.out.print(AST.getOp().getValue());
                newASTs.add(AST.getLeft());
                newASTs.add(AST.getRight());
            } else if (AST != null) {
                System.out.print(AST.getValue());
                newASTs.add(null);
                newASTs.add(null);
            } else {
                TreePrinter.printWhitespaces(betweenSpaces);
            }
            TreePrinter.printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (AST ast : ASTs) {
                TreePrinter.printWhitespaces(firstSpaces - i);
                if (ast == null) {
                    TreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if (ast.getLeft() != null)
                    System.out.print("/");
                else
                    TreePrinter.printWhitespaces(1);

                TreePrinter.printWhitespaces(i + i - 1);

                if (ast.getRight() != null)
                    System.out.print("\\");
                else
                    TreePrinter.printWhitespaces(1);

                TreePrinter.printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printASTInternal(newASTs, level + 1, maxLevel);
    }

    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private static <T extends Comparable<?>> int maxLevel(AST AST) {
        if (AST == null)
            return 0;

        return Math.max(TreePrinter.maxLevel(AST.getLeft()), TreePrinter.maxLevel(AST.getRight())) + 1;
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }

}
