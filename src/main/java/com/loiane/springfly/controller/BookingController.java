package com.loiane.springfly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loiane.springfly.service.ChatService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/customer-support")
public class BookingController {
  
  private final String chatId = UUID.randomUUID().toString();
  private final ChatService chatService;

  public BookingController(ChatService chatService) {
    this.chatService = chatService;
  }

  record ChatMessage(String text) {}

  @PostMapping
  public ChatMessage chat(@RequestBody  ChatMessage userMessage) {
    var message = chatService.chat(chatId, userMessage.text());
    return new ChatMessage(message);
  }
}
