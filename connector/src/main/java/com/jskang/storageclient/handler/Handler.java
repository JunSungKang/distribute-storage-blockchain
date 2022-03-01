package com.jskang.storageclient.handler;

import com.sun.net.httpserver.HttpExchange;

public interface Handler {
    void run(HttpExchange exchange);
}
