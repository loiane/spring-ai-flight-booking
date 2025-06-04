import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FlightBooking, BookingFilter } from '../types/booking.types';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private readonly _bookings = signal<FlightBooking[]>([]);
  private readonly _filters = signal<BookingFilter>({});

  constructor(private readonly httpClient: HttpClient) {
    this.loadBookings();
  }

  // Public readonly signals
  readonly bookings = this._bookings.asReadonly();
  readonly filteredBookings = computed(() => {
    const bookings = this._bookings();
    const filters = this._filters();

    if (!bookings.length) return [];

    return bookings.filter(booking => {
      // Status filter
      if (filters.status && booking.bookingStatus !== filters.status) {
        return false;
      }

      // Date range filter
      if (filters.dateFrom && new Date(booking.date) < new Date(filters.dateFrom)) {
        return false;
      }
      if (filters.dateTo && new Date(booking.date) > new Date(filters.dateTo)) {
        return false;
      }

      // Search term filter (searches across multiple fields)
      if (filters.searchTerm) {
        const searchTerm = filters.searchTerm.toLowerCase();
        const searchableText = [
          booking.bookingNumber.toString(),
          booking.firstName,
          booking.lastName,
          booking.from,
          booking.to,
          booking.seatNumber,
          booking.bookingClass
        ].join(' ').toLowerCase();

        if (!searchableText.includes(searchTerm)) {
          return false;
        }
      }

      return true;
    });
  });

  readonly filters = this._filters.asReadonly();

  private loadBookings(): void {
    this.httpClient.get<any[]>('/api/booking').subscribe({
      next: (bookings) => {
        // Convert bookingNumber from string to number if needed
        const formattedBookings: FlightBooking[] = bookings.map(booking => ({
          ...booking,
          bookingNumber: booking.bookingNumber
        }));
        this._bookings.set(formattedBookings);
      },
      error: (error) => {
        console.error('Error loading bookings:', error);
        // Fallback to sample data if backend is not available
        this._bookings.set([
          {
            bookingNumber: 101,
            firstName: 'John',
            lastName: 'Doe',
            date: '2025-05-31',
            bookingStatus: 'CONFIRMED',
            from: 'SFO',
            to: 'LHR',
            seatNumber: '10A',
            bookingClass: 'ECONOMY'
          },
          {
            bookingNumber: 102,
            firstName: 'Jane',
            lastName: 'Smith',
            date: '2025-06-02',
            bookingStatus: 'CANCELLED',
            from: 'CDG',
            to: 'ARN',
            seatNumber: '14A',
            bookingClass: 'BUSINESS'
          }
        ]);
      }
    });
  }

  // Methods to update filters
  updateFilters(filters: Partial<BookingFilter>) {
    this._filters.update(current => ({ ...current, ...filters }));
  }

  clearFilters() {
    this._filters.set({});
  }

  // Method to get booking by number
  getBookingByNumber(bookingNumber: number) {
    return computed(() =>
      this._bookings().find(booking => booking.bookingNumber === bookingNumber)
    );
  }

  // Method to add new booking (for future use)
  addBooking(booking: FlightBooking) {
    this._bookings.update(bookings => [...bookings, booking]);
  }

  // Method to update booking status
  updateBookingStatus(bookingNumber: number, status: FlightBooking['bookingStatus']) {
    this._bookings.update(bookings =>
      bookings.map(booking =>
        booking.bookingNumber === bookingNumber
          ? { ...booking, bookingStatus: status }
          : booking
      )
    );
  }
}
