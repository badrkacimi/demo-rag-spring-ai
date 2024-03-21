package com.demo.rag;

import com.demo.rag.service.extern.OpenAIClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class RagApplication {
    @Autowired
    private OpenAIClient openAiChatClient;

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    private static void textEmbedding(VectorStore vectorStore, JdbcTemplate jdbcTemplate, Resource pdf) {
        jdbcTemplate.update("delete from vector_store");

        //Convert to text
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdf, config);
        List<Document> documentList = pdfDocumentReader.get();
        String content = documentList.stream().map(Document::getContent).collect(Collectors.joining("\n"));

        //Split to chunks
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content, 1000);
        List<Document> chunksDocs = chunks.stream().map(Document::new).toList();

        //Integration/embedding
        vectorStore.accept(chunksDocs);
    }

    @Bean
    CommandLineRunner commandLineRunner(VectorStore vectorStore, JdbcTemplate jdbcTemplate,
                                        @Value("classpath:pdfs/black.pdf") Resource pdf) {
        return args -> {
            //textEmbedding(vectorStore, jdbcTemplate, pdf);
            String query = "What the text is about ?";
            //Search vector/documents similarities
            List<Document> similarities = vectorStore.similaritySearch(query);
            //Prompt
            String systemMessageTemplate = """
                    Answer the following question based on the provided CONTEXT
                    "If the answer is not found in the context,respond "I don't know".
                    CONTEXT:
                      {CONTEXT}
                    """;
            Message systemMessage = new SystemPromptTemplate(systemMessageTemplate)
                    .createMessage(Map.of("CONTEXT", similarities));
            UserMessage userMessage = new UserMessage(query);
            Prompt prompt = new Prompt(List.of(systemMessage, userMessage));


            String response = openAiChatClient.getOpenAiChatClient().call(prompt).getResult().getOutput().getContent();


            System.out.println(response);
        };
    }

}
