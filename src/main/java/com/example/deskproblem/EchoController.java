package com.example.deskproblem;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EchoController {

    static final int MAX_MESSAGE_LENGTH = 200;

    @GetMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(
            @RequestParam(required = false) String message) {
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "message is required"));
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "message must be at most " + MAX_MESSAGE_LENGTH + " characters"));
        }
        return ResponseEntity.ok(Map.of("message", message, "length", message.length()));
    }
}
