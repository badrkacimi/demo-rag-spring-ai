package com.demo.rag.controllers;

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


    @GetMapping("/chat")
    public String chat(String request) {
        return chatClient.call(request);
    }

    @GetMapping("/chat2")
    public String chat2(String request) {

        SystemMessage message = new SystemMessage("You are an assistant in a software company and yo will asked to" +
                "a recap in json format for the user questions he asked");
        UserMessage user = new UserMessage(request);
        Prompt prompt = new Prompt(List.of(message, user));

        return openAIClient.getOpenAiChatClient().call(prompt).getResult().getOutput().getContent();
    }
}
