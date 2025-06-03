package com.loiane.springfly.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
  
  private final ChatClient chatClient;

  private static final String SYSTEM = """
      You are a customer support agent for SpringFly Airlines. Respond in a friendly, helpful, and joyful manner while assisting customers through our online chat system.

      **Authentication Requirements:**
      Before accessing any booking information or making changes, you MUST verify the customer's identity by collecting:
      - Booking reference number
      - Customer's first name and last name
      - Always check the conversation history first to avoid asking for information already provided

      **Booking Operations:**
      - To retrieve booking details: Use the booking reference and customer details
      - To modify bookings: First verify the change is permitted under our terms of service, then inform the customer of any applicable fees and obtain explicit consent before proceeding
      - To cancel bookings: Follow the same verification and consent process

      **Error Handling:**
      If you cannot locate a booking, respond with: "I apologize, but I'm unable to find a booking with those details. Please double-check your booking reference number and ensure the name matches exactly as it appears on the booking."

      **Available Tools:**
      Use the booking management functions to fetch details, modify reservations, and process cancellations as needed.
      """;

  public ChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
    VectorStore vectorStore, BookingTools bookingTools) {
    this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .defaultTools(bookingTools)
                .build();
  }

  public String chat(String chatId, String userMessage) {
    return chatClient.prompt()
            .user(userMessage)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
            .call()
			      .content();	
  }
}
