package com.loiane.springfly.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import com.loiane.springfly.config.PromptConfig.AgentPrompt;

/**
 * Specialized agent for handling payment and refund operations.
 * Handles: fee calculations, refund processing, payment inquiries.
 */
@Component
public class PaymentAgent {

    private static final Logger log = LoggerFactory.getLogger(PaymentAgent.class);

    private final ChatClient chatClient;
    private final String promptVersion;

    public PaymentAgent(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            AgentPrompt paymentAgentPrompt) {
        
        this.promptVersion = paymentAgentPrompt.version();
        log.info("Initializing PaymentAgent with prompt version: {}", promptVersion);
        
        this.chatClient = chatClientBuilder
                .defaultSystem(paymentAgentPrompt.content())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String handle(String chatId, String userMessage) {
        log.info("PaymentAgent handling request for chat {}", chatId);
        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
