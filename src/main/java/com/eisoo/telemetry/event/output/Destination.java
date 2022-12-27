package com.eisoo.telemetry.event.output;

import java.io.IOException;

public interface Destination {
    void write(String string) throws IOException;
}
