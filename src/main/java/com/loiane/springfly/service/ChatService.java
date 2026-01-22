package com.loiane.springfly.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.loiane.springfly.agents.SupervisorAgent;

/**
 * Main chat service that delegates to the multi-agent system.
 * The SupervisorAgent orchestrates routing to specialized agents.
 */
@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  
  private final SupervisorAgent supervisorAgent;

  public ChatService(SupervisorAgent supervisorAgent) {
    this.supervisorAgent = supervisorAgent;
    log.info("ChatService initialized with multi-agent architecture");
  }

  public String chat(String chatId, String userMessage) {
    log.info("Processing chat {} with message: {}", chatId, userMessage);
    return supervisorAgent.handle(chatId, userMessage);
  }
}
