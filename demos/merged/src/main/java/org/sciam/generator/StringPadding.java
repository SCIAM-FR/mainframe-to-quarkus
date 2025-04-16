package org.sciam.generator;

public class StringPadding {

    public enum Position {
        LEFT,   // Align left (pad right)
        RIGHT,  // Align right (pad left)
        CENTER  // Center (pad both sides)
    }

    /**
     * Pads a string to position it within a specified width
     *
     * @param input    The string to pad
     * @param width    The total width of the output string
     * @param position The desired position (LEFT, RIGHT, or CENTER)
     * @param padChar  The character to use for padding
     * @return The padded string
     */
    public static String padString(String input, int width, Position position, char padChar) {
        if (input == null) {
            input = "";
        }

        if (input.length() >= width) {
            return input;
        }

        int padLength = width - input.length();

        switch (position) {
            case LEFT:
                return input + String.valueOf(padChar).repeat(padLength);
            case RIGHT:
                return String.valueOf(padChar).repeat(padLength) + input;
            case CENTER:
                int leftPad = padLength / 2;
                int rightPad = padLength - leftPad;
                return String.valueOf(padChar).repeat(leftPad) + input
                        + String.valueOf(padChar).repeat(rightPad);
            default:
                return input;
        }
    }

}
