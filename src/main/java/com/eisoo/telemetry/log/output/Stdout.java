package com.eisoo.telemetry.log.output;

import java.io.PrintStream;

public class Stdout implements Destination{
    private final PrintStream out = System.out;

    @Override
    public void write(String string) {
        out.println(string);
    }
}
