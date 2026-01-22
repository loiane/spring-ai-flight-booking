package com.loiane.springfly.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import com.loiane.springfly.config.PromptConfig.AgentPrompt;

/**
 * Supervisor agent that orchestrates routing to specialized agents.
 * Analyzes customer intent and delegates to the appropriate specialist.
 */
@Component
public class SupervisorAgent {

    private static final Logger log = LoggerFactory.getLogger(SupervisorAgent.class);

    private final ChatClient chatClient;
    private final BookingAgent bookingAgent;
    private final PaymentAgent paymentAgent;
    private final EscalationAgent escalationAgent;
    private final String promptVersion;

    public SupervisorAgent(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            VectorStore vectorStore, BookingAgent bookingAgent, 
            PaymentAgent paymentAgent, EscalationAgent escalationAgent,
            AgentPrompt supervisorAgentPrompt) {
        
        this.bookingAgent = bookingAgent;
        this.paymentAgent = paymentAgent;
        this.escalationAgent = escalationAgent;
        this.promptVersion = supervisorAgentPrompt.version();
        
        log.info("Initializing SupervisorAgent with prompt version: {}", promptVersion);

        this.chatClient = chatClientBuilder
                .defaultSystem(supervisorAgentPrompt.content())
                .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                    QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .build();
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String handle(String chatId, String userMessage) {
        log.info("SupervisorAgent analyzing request for chat {}", chatId);

        // Get routing decision from the supervisor
        String routingDecision = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId + "-supervisor"))
                .call()
                .content();

        log.info("Routing decision: {}", routingDecision);

        // Route to the appropriate specialist agent
        return routeToAgent(routingDecision.trim().toUpperCase(), chatId, userMessage);
    }

    private String routeToAgent(String decision, String chatId, String userMessage) {
        return switch (decision) {
            case "BOOKING" -> {
                log.info("Routing to BookingAgent");
                yield bookingAgent.handle(chatId, userMessage);
            }
            case "PAYMENT" -> {
                log.info("Routing to PaymentAgent");
                yield paymentAgent.handle(chatId, userMessage);
            }
            case "ESCALATION" -> {
                log.info("Routing to EscalationAgent");
                yield escalationAgent.handle(chatId, userMessage);
            }
            default -> {
                log.warn("Unknown routing decision: {}, defaulting to BookingAgent", decision);
                yield bookingAgent.handle(chatId, userMessage);
            }
        };
    }
}
