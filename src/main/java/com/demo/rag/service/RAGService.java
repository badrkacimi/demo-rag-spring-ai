package com.demo.rag.service;

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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RAGService {
    @Autowired
    private OpenAIClient openAiChatClient;
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String askLLM(String query) {
        //textEmbedding(pdfsResource);
        List<Document> similarities = vectorStore.similaritySearch(query);
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

        return openAiChatClient.getOpenAiChatClient().call(prompt).getResult().getOutput().getContent();
    }

    public void textEmbedding(MultipartFile file) throws IOException {
        jdbcTemplate.update("delete from vector_store");
        Resource resource = new InputStreamResource(file.getInputStream());

        //Convert to text
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource, config);
        List<Document> documentList = pdfDocumentReader.get();
        String content = documentList.stream().map(Document::getContent).collect(Collectors.joining("\n"));

        //Split to chunks
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content, 1000);
        List<Document> chunksDocs = chunks.stream().map(Document::new).toList();

        //Integration/embedding
        vectorStore.accept(chunksDocs);
    }
}
