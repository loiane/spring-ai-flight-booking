import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

interface Message {
  id: string;
  content: string;
  isUser: boolean;
  timestamp: Date;
  data?: FlightBooking[];
}

interface FlightBooking {
  bookingNumber: number;
  firstName: string;
  lastName: string;
  date: string;
  bookingStatus: string;
  from: string;
  to: string;
  seat: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatTableModule,
    MatChipsModule,
    MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent {
  // Signals for reactive state management
  messages = signal<Message[]>([
    {
      id: '1',
      content: 'To book a flight with Funair, you\'ll need to visit our website or use our mobile app. There you can select your desired flight, fill in the necessary personal information, and make a full payment to complete the booking.\n\nIf you have any other questions or need assistance during the booking process, feel free to ask! 😊✈️',
      isUser: false,
      timestamp: new Date(),
      data: [
        {
          bookingNumber: 101,
          firstName: 'John',
          lastName: 'Doe',
          date: '2025-05-31',
          bookingStatus: 'CONFIRMED',
          from: 'SFO',
          to: 'LHR',
          seat: '10A'
        },
        {
          bookingNumber: 102,
          firstName: 'Jane',
          lastName: 'Smith',
          date: '2025-06-02',
          bookingStatus: 'CANCELLED',
          from: 'CDG',
          to: 'ARN',
          seat: '14A'
        },
        {
          bookingNumber: 103,
          firstName: 'Michael',
          lastName: 'Johnson',
          date: '2025-06-04',
          bookingStatus: 'CONFIRMED',
          from: 'SJC',
          to: 'SJC',
          seat: '17A'
        },
        {
          bookingNumber: 104,
          firstName: 'Sarah',
          lastName: 'Williams',
          date: '2025-06-06',
          bookingStatus: 'CONFIRMED',
          from: 'SFO',
          to: 'TXL',
          seat: '13A'
        },
        {
          bookingNumber: 105,
          firstName: 'Robert',
          lastName: 'Taylor',
          date: '2025-06-08',
          bookingStatus: 'CONFIRMED',
          from: 'LAX',
          to: 'SFO',
          seat: '19A'
        }
      ]
    }
  ]);

  currentMessage = signal('');
  isLoading = signal(false);

  // Computed signal for display columns
  displayedColumns = signal(['bookingNumber', 'firstName', 'lastName', 'date', 'bookingStatus', 'from', 'to', 'seat']);

  // Computed signal to check if there are any messages with data
  hasDataMessages = computed(() =>
    this.messages().some(message => message.data && message.data.length > 0)
  );

  sendMessage() {
    const messageText = this.currentMessage().trim();
    if (!messageText) return;

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      content: messageText,
      isUser: true,
      timestamp: new Date()
    };

    this.messages.update(messages => [...messages, userMessage]);
    this.currentMessage.set('');
    this.isLoading.set(true);

    // Simulate AI response
    setTimeout(() => {
      const aiResponse: Message = {
        id: (Date.now() + 1).toString(),
        content: this.generateAIResponse(messageText),
        isUser: false,
        timestamp: new Date()
      };

      this.messages.update(messages => [...messages, aiResponse]);
      this.isLoading.set(false);
    }, 1500);
  }

  private generateAIResponse(userMessage: string): string {
    const lowerMessage = userMessage.toLowerCase();

    if (lowerMessage.includes('book') || lowerMessage.includes('flight')) {
      return 'I can help you book a flight! Please visit our website or mobile app to search for available flights, select your preferred option, and complete the booking process.';
    } else if (lowerMessage.includes('cancel')) {
      return 'To cancel your booking, please provide your booking number and I\'ll assist you with the cancellation process.';
    } else if (lowerMessage.includes('status') || lowerMessage.includes('booking')) {
      return 'I can check your booking status. Please provide your booking number or name, and I\'ll look it up for you.';
    } else {
      return 'I\'m here to help with your flight booking needs. You can ask me about booking flights, checking status, cancellations, or any other flight-related questions!';
    }
  }

  onKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  getStatusChipColor(status: string): string {
    switch (status.toUpperCase()) {
      case 'CONFIRMED':
        return 'primary';
      case 'CANCELLED':
        return 'warn';
      case 'PENDING':
        return 'accent';
      default:
        return 'primary';
    }
  }
}
