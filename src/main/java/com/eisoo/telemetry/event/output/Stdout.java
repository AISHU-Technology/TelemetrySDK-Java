package com.eisoo.telemetry.event.output;

import java.io.PrintStream;

public class Stdout implements Destination {
    private PrintStream out = System.out;

    @Override
    public void write(String string) {
        out.println("[" + string + "]");
    }
}
