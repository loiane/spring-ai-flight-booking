package com.loiane.springfly.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import com.loiane.springfly.config.PromptConfig.AgentPrompt;
import com.loiane.springfly.service.BookingTools;
import com.loiane.springfly.service.ValidationTools;

/**
 * Specialized agent for handling booking operations.
 * Handles: reservations, changes, cancellations, booking inquiries.
 */
@Component
public class BookingAgent {

    private static final Logger log = LoggerFactory.getLogger(BookingAgent.class);

    private final ChatClient chatClient;
    private final String promptVersion;

    public BookingAgent(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            BookingTools bookingTools, ValidationTools validationTools,
            AgentPrompt bookingAgentPrompt) {
        
        this.promptVersion = bookingAgentPrompt.version();
        log.info("Initializing BookingAgent with prompt version: {}", promptVersion);
        
        this.chatClient = chatClientBuilder
                .defaultSystem(bookingAgentPrompt.content())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(bookingTools, validationTools)
                .build();
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String handle(String chatId, String userMessage) {
        log.info("BookingAgent handling request for chat {}", chatId);
        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
