package com.demo.rag.controllers;

import com.demo.rag.service.RAGService;
import com.demo.rag.service.extern.OpenAIClient;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OpenAIRestController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private RAGService ragService;

    @GetMapping("/chat-without-rag")
    public String chat(String question) {
        return chatClient.call(question);
    }

    @GetMapping("/chat-with-rag")
    public String chat2(String context, String question) {
        SystemMessage message = new SystemMessage(context);
        UserMessage user = new UserMessage(question);
        Prompt prompt = new Prompt(List.of(message, user));

        return openAIClient.getOpenAiChatClient().call(prompt).getResult().getOutput().getContent();
    }
    @GetMapping("/your-rag")
    public String chatRAG(String question) {
        return ragService.askLLM(question);
    }
}
