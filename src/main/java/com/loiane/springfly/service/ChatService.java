package com.loiane.springfly.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.loiane.springfly.config.PromptConfig.SystemPrompt;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  
  private final ChatClient chatClient;
  private final String promptVersion;

  public ChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
      VectorStore vectorStore, BookingTools bookingTools, ValidationTools validationTools,
      SystemPrompt systemPrompt) {
    
    this.promptVersion = systemPrompt.version();
    log.info("Initializing ChatService with prompt version: {}", promptVersion);
    
    this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.content())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .defaultTools(bookingTools, validationTools)
                .build();
  }

  public String getPromptVersion() {
    return promptVersion;
  }

  public String chat(String chatId, String userMessage) {
    return chatClient.prompt()
            .user(userMessage)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
            .call()
			      .content();	
  }
}
