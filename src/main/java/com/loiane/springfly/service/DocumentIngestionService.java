package com.loiane.springfly.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;
    private final ResourceLoader resourceLoader;

    public DocumentIngestionService(VectorStore vectorStore, ResourceLoader resourceLoader) {
        this.vectorStore = vectorStore;
        this.resourceLoader = resourceLoader;
    }
    
    /**
     * Custom exception for document ingestion errors.
     */
    class DocumentIngestionException extends RuntimeException {
        public DocumentIngestionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting document ingestion process...");
        ingestSpringflyTermsOfService();
        logger.info("Document ingestion completed successfully.");
    }


    private void ingestSpringflyTermsOfService() {
        try {
            // Load the terms of service document
            Resource resource = resourceLoader.getResource("classpath:rag/springfly-terms-of-service.md");
            
            if (!resource.exists()) {
                logger.warn("Terms of service document not found at: classpath:rag/springfly-terms-of-service.md");
                return;
            }

            logger.info("Loading Springfly Terms of Service document...");
            
            // Create a text reader for the markdown file
            TextReader textReader = new TextReader(resource);
            textReader.getCustomMetadata().put("source", "springfly-terms-of-service");
            textReader.getCustomMetadata().put("type", "terms-of-service");
            textReader.getCustomMetadata().put("airline", "Springfly Airlines");
            textReader.getCustomMetadata().put("last-updated", "2025-05-31");
            
            // Read the document
            List<Document> documents = textReader.get();
            
            // Split the document into smaller chunks for better vector search
            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            
            // Add metadata to each split document
            splitDocuments.forEach(doc -> {
                doc.getMetadata().putAll(Map.of(
                    "source", "springfly-terms-of-service",
                    "type", "terms-of-service", 
                    "airline", "Springfly Airlines",
                    "last-updated", "2025-05-31"
                ));
            });

            logger.info("Split document into {} chunks", splitDocuments.size());
            
            // Check if documents already exist to avoid duplicates
            if (shouldIngestDocuments()) {
            throw new DocumentIngestionException("Failed to ingest Springfly terms of service", e);
                vectorStore.add(splitDocuments);
                logger.info("Successfully ingested {} document chunks into vector store", splitDocuments.size());
            } else {
                logger.info("Terms of service documents already exist in vector store, skipping ingestion");
            }
            
        } catch (Exception e) {
            logger.error("Error during document ingestion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to ingest Springfly terms of service", e);
        }
    }

    private boolean shouldIngestDocuments() {
        try {
            // Search for existing terms of service documents
            SearchRequest searchRequest = SearchRequest.builder()
                .query("Springfly Airlines terms")
                .topK(1)
                .build();
            List<Document> existingDocs = vectorStore.similaritySearch(searchRequest);
            boolean hasExisting = !existingDocs.isEmpty() && 
                existingDocs.stream().anyMatch(doc -> 
                    "springfly-terms-of-service".equals(doc.getMetadata().get("source")));
            
            return !hasExisting;
        } catch (Exception e) {
            logger.warn("Could not check for existing documents, proceeding with ingestion: {}", e.getMessage());
            return true;
        }
    }
}
