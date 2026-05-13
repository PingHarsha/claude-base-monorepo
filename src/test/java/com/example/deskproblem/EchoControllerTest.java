package com.example.deskproblem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EchoControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void echoesMessage() throws Exception {
        mockMvc.perform(get("/api/echo").param("message", "hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("hello"))
                .andExpect(jsonPath("$.length").value(5));
    }

    @Test
    void rejectsMissingMessage() throws Exception {
        mockMvc.perform(get("/api/echo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("message is required"));
    }

    @Test
    void rejectsEmptyMessage() throws Exception {
        mockMvc.perform(get("/api/echo").param("message", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("message is required"));
    }

    @Test
    void acceptsMessageAtMaxLength() throws Exception {
        String atCap = "a".repeat(EchoController.MAX_MESSAGE_LENGTH);
        mockMvc.perform(get("/api/echo").param("message", atCap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length").value(EchoController.MAX_MESSAGE_LENGTH));
    }

    @Test
    void rejectsMessageOverMaxLength() throws Exception {
        String overCap = "a".repeat(EchoController.MAX_MESSAGE_LENGTH + 1);
        mockMvc.perform(get("/api/echo").param("message", overCap))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "message must be at most " + EchoController.MAX_MESSAGE_LENGTH + " characters"));
    }
}
