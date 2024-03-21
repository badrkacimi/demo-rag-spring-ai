package com.demo.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class RagApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(VectorStore vectorStore, JdbcTemplate jdbcTemplate,
                                        @Value("classpath:black.pdf") Resource pdf) {
        return args -> {
            textEmbedding(vectorStore, jdbcTemplate, pdf);
        };
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
        List<String> chunks = tokenTextSplitter.split(content,1000);
        List<Document> chunksDocs = chunks.stream().map(Document::new).toList();

        //Integration/embedding
        vectorStore.accept(chunksDocs);
    }

}
