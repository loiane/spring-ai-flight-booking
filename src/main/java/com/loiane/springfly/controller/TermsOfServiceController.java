package com.loiane.springfly.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/terms")
public class TermsOfServiceController {

    private final VectorStore vectorStore;

    public TermsOfServiceController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Search through the terms of service using vector similarity search
     */
    @GetMapping("/search")
    public List<Document> searchTerms(@RequestParam String query, 
                                     @RequestParam(defaultValue = "5") int limit) {
        SearchRequest searchRequest = SearchRequest.builder()
            .query(query)
            .topK(limit)
            .build();
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        if (results == null) {
            return Collections.emptyList();
        }
        
        // Filter to only return terms of service documents
        return results.stream()
            .filter(doc -> "springfly-terms-of-service".equals(doc.getMetadata().get("source")))
            .toList();
    }

    /**
     * Get specific information about a policy (booking, changes, cancellation, etc.)
     */
    @GetMapping("/policy/{policyType}")
    public List<Document> getPolicyInfo(@PathVariable String policyType) {
        String searchQuery = switch (policyType.toLowerCase()) {
            case "booking" -> "booking flights payment requirements";
            case "changes", "change" -> "changing bookings fees modification";
            case "cancellation", "cancel" -> "cancelling bookings refund fees";
            case "baggage" -> "baggage policy carry-on checked";
            case "delays" -> "flight delays cancellations compensation";
            case "rights" -> "passenger rights assistance";
            case "liability" -> "liability limitations insurance";
            default -> policyType + " policy terms";
        };
        
        SearchRequest searchRequest = SearchRequest.builder()
            .query(searchQuery)
            .topK(3)
            .build();
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        if (results == null) {
            return Collections.emptyList();
        }
        
        // Filter to only return terms of service documents
        return results.stream()
            .filter(doc -> "springfly-terms-of-service".equals(doc.getMetadata().get("source")))
            .toList();
    }
}
