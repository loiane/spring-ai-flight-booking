import { Component } from '@angular/core';

import { ChatComponent } from './components/chat/chat.component';
import { BookingsComponent } from './components/bookings/bookings.component';

@Component({
  selector: 'app-root',
  imports: [ChatComponent, BookingsComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'Flight Booking System';
}
