import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { ChatComponent } from './components/chat/chat.component';
import { BookingsComponent } from './components/bookings/bookings.component';

@Component({
  selector: 'app-root',
  imports: [CommonModule, MatTabsModule, MatIconModule, ChatComponent, BookingsComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'Flight Booking System';
  selectedTabIndex = signal(0);
}
