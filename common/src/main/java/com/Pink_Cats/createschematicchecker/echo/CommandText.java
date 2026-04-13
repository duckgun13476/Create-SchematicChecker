package com.Pink_Cats.createschematicchecker.echo;

public class CommandText {

    public static String shorten(String value) {
        if (value == null) {
            return "null";
        }
        if (value.length() <= 180) {
            return value;
        }
        return value.substring(0, 180) + "...";
    }
}
