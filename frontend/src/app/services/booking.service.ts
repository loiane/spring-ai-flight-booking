import { Injectable, signal, computed } from '@angular/core';
import { FlightBooking, BookingFilter } from '../types/booking.types';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  // Sample data that matches the screenshot
  private readonly _bookings = signal<FlightBooking[]>([
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
    },
    {
      bookingNumber: 106,
      firstName: 'Emily',
      lastName: 'Davis',
      date: '2025-06-10',
      bookingStatus: 'PENDING',
      from: 'JFK',
      to: 'LAX',
      seat: '22B'
    },
    {
      bookingNumber: 107,
      firstName: 'David',
      lastName: 'Wilson',
      date: '2025-06-12',
      bookingStatus: 'CONFIRMED',
      from: 'ORD',
      to: 'MIA',
      seat: '8C'
    },
    {
      bookingNumber: 108,
      firstName: 'Lisa',
      lastName: 'Brown',
      date: '2025-06-14',
      bookingStatus: 'CANCELLED',
      from: 'SEA',
      to: 'DEN',
      seat: '15D'
    }
  ]);

  private readonly _filters = signal<BookingFilter>({});

  // Public read-only signals
  bookings = this._bookings.asReadonly();
  filters = this._filters.asReadonly();

  // Computed filtered bookings
  filteredBookings = computed(() => {
    const bookings = this._bookings();
    const filters = this._filters();

    return bookings.filter(booking => {
      // Status filter
      if (filters.status && filters.status !== 'ALL' && booking.bookingStatus !== filters.status) {
        return false;
      }

      // Search term filter (searches in name, booking number, airports)
      if (filters.searchTerm) {
        const searchTerm = filters.searchTerm.toLowerCase();
        const searchableText = [
          booking.firstName,
          booking.lastName,
          booking.bookingNumber.toString(),
          booking.from,
          booking.to,
          booking.seat
        ].join(' ').toLowerCase();

        if (!searchableText.includes(searchTerm)) {
          return false;
        }
      }

      // Date range filter
      if (filters.dateFrom && booking.date < filters.dateFrom) {
        return false;
      }

      if (filters.dateTo && booking.date > filters.dateTo) {
        return false;
      }

      return true;
    });
  });

  // Computed statistics
  bookingStats = computed(() => {
    const bookings = this._bookings();
    return {
      total: bookings.length,
      confirmed: bookings.filter(b => b.bookingStatus === 'CONFIRMED').length,
      cancelled: bookings.filter(b => b.bookingStatus === 'CANCELLED').length,
      pending: bookings.filter(b => b.bookingStatus === 'PENDING').length
    };
  });

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
