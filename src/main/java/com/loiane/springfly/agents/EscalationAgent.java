package com.loiane.springfly.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import com.loiane.springfly.config.PromptConfig.AgentPrompt;

/**
 * Specialized agent for handling escalations and complex issues.
 * Handles: complaints, complex issues, human handoff preparation.
 */
@Component
public class EscalationAgent {

    private static final Logger log = LoggerFactory.getLogger(EscalationAgent.class);

    private final ChatClient chatClient;
    private final String promptVersion;

    public EscalationAgent(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            AgentPrompt escalationAgentPrompt) {
        
        this.promptVersion = escalationAgentPrompt.version();
        log.info("Initializing EscalationAgent with prompt version: {}", promptVersion);
        
        this.chatClient = chatClientBuilder
                .defaultSystem(escalationAgentPrompt.content())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String handle(String chatId, String userMessage) {
        log.info("EscalationAgent handling request for chat {}", chatId);
        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
