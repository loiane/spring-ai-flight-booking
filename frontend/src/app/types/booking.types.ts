export interface FlightBooking {
  bookingNumber: number;
  firstName: string;
  lastName: string;
  date: string;
  bookingStatus: 'CONFIRMED' | 'CANCELLED' | 'PENDING';
  from: string;
  to: string;
  seat: string;
}

export interface BookingFilter {
  status?: string;
  dateFrom?: string;
  dateTo?: string;
  searchTerm?: string;
}
