package org.sciam.generator;

import java.util.Arrays;

public class StringPadding {

    public enum Position {
        LEFT,   // Align left (pad right)
        RIGHT,  // Align right (pad left)
        CENTER  // Center (pad both sides)
    }

    public static String padString(String input, int width, Position position, char padChar) {
        if (input == null) {
            input = "";
        }

        int inputLength = input.length();
        if (inputLength >= width) {
            return input;
        }

        int padLength = width - inputLength;
        char[] result = new char[width];

        switch (position) {
            case LEFT:
                // Copier l'input puis ajouter les padding
                input.getChars(0, inputLength, result, 0);
                Arrays.fill(result, inputLength, width, padChar);
                break;
            case RIGHT:
                // Ajouter les padding puis copier l'input
                Arrays.fill(result, 0, padLength, padChar);
                input.getChars(0, inputLength, result, padLength);
                break;
            case CENTER:
                int leftPad = padLength / 2;
                int rightPad = padLength - leftPad;
                Arrays.fill(result, 0, leftPad, padChar);
                input.getChars(0, inputLength, result, leftPad);
                Arrays.fill(result, leftPad + inputLength, width, padChar);
                break;
            default:
                return input;
        }

        return new String(result);
    }

}
