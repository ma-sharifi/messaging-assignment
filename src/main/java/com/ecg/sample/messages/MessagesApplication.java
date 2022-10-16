package com.ecg.sample.messages;

import com.sun.net.httpserver.HttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.OutputStream;
import java.net.InetSocketAddress;

@SpringBootApplication
@EnableScheduling
//This annotation ensures that a background task executor is created. Without it, nothing gets scheduled.
public class MessagesApplication {

    public static void main(String[] args) throws Exception {

        // starts the mock external service on `localhost:8181`
        HttpServer server = HttpServer.create(new InetSocketAddress(8181), 0);
        addEndpoint(server, "/mock_external_service"); // It can consume GET, POST, PUT,...
        new Thread(server::start).start();

        // starts the application
        SpringApplication.run(MessagesApplication.class, args);

    }

    private static void addEndpoint(HttpServer server, String uri) {
        server.createContext(uri, httpExchange -> {

            String response = "{\"result\": \"ok\"}";
            int statusCode = 200;

            byte[] responseBytes = response.getBytes();
            httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });
    }
}
