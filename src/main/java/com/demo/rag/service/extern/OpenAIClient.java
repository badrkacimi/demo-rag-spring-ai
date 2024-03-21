package com.demo.rag.service.extern;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAIClient {
    @Value("${spring.ai.openai.api-key}")
    String apiKey;

    public OpenAiChatClient getOpenAiChatClient() {
        OpenAiApi openAiApi = new OpenAiApi(apiKey);
        OpenAiChatOptions options = new OpenAiChatOptions.Builder()
                .withModel("gpt-3.5-turbo")
                .build();
        return new OpenAiChatClient(openAiApi, options);
    }
}
