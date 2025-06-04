import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatMessage {
  text: string;
}

export interface ChatResponse {
  text: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly apiUrl = '/api/customer-support';

  constructor(private readonly httpClient: HttpClient) {}

  sendMessage(message: string): Observable<ChatResponse> {
    const chatMessage: ChatMessage = { text: message };
    return this.httpClient.post<ChatResponse>(this.apiUrl, chatMessage);
  }
}
