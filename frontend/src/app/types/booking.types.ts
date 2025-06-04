export interface FlightBooking {
  bookingNumber: number;
  firstName: string;
  lastName: string;
  date: string;
  bookingStatus: 'CONFIRMED' | 'CANCELLED' | 'PENDING';
  from: string;
  to: string;
  seatNumber: string;
  bookingClass: string;
}

export interface BookingFilter {
  status?: string;
  dateFrom?: string;
  dateTo?: string;
  searchTerm?: string;
}
