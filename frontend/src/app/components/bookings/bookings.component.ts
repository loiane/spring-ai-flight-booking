import { Component, signal, computed, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { BookingService } from '../../services/booking.service';
import { FlightBooking } from '../../types/booking.types';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatCardModule,
    MatToolbarModule,
    MatBadgeModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.scss']
})
export class BookingsComponent {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private readonly bookingService = inject(BookingService);

  // Signals for component state
  searchTerm = signal('');
  selectedStatus = signal('ALL');
  dateFrom = signal<Date | null>(null);
  dateTo = signal<Date | null>(null);

  // Data signals from service
  bookings = this.bookingService.filteredBookings;
  stats = this.bookingService.bookingStats;

  // Table configuration
  displayedColumns = signal<string[]>([
    'bookingNumber',
    'firstName',
    'lastName',
    'date',
    'bookingStatus',
    'from',
    'to',
    'seat',
    'actions'
  ]);

  // Data source for Material Table
  dataSource = computed(() => {
    const data = new MatTableDataSource(this.bookings());
    // Set paginator and sort after view init
    setTimeout(() => {
      if (this.paginator) data.paginator = this.paginator;
      if (this.sort) data.sort = this.sort;
    });
    return data;
  });

  // Status options for filter
  statusOptions = [
    { value: 'ALL', label: 'All Status' },
    { value: 'CONFIRMED', label: 'Confirmed' },
    { value: 'CANCELLED', label: 'Cancelled' },
    { value: 'PENDING', label: 'Pending' }
  ];

  ngAfterViewInit() {
    // Initialize table with paginator and sort
    const dataSource = this.dataSource();
    if (this.paginator) dataSource.paginator = this.paginator;
    if (this.sort) dataSource.sort = this.sort;
  }

  // Filter methods
  onSearchChange() {
    this.updateFilters();
  }

  onStatusChange() {
    this.updateFilters();
  }

  onDateFromChange() {
    this.updateFilters();
  }

  onDateToChange() {
    this.updateFilters();
  }

  private updateFilters() {
    this.bookingService.updateFilters({
      searchTerm: this.searchTerm(),
      status: this.selectedStatus() === 'ALL' ? undefined : this.selectedStatus(),
      dateFrom: this.dateFrom()?.toISOString().split('T')[0],
      dateTo: this.dateTo()?.toISOString().split('T')[0]
    });
  }

  clearFilters() {
    this.searchTerm.set('');
    this.selectedStatus.set('ALL');
    this.dateFrom.set(null);
    this.dateTo.set(null);
    this.bookingService.clearFilters();
  }

  // Status chip color mapping
  getStatusChipColor(status: string): 'primary' | 'accent' | 'warn' {
    switch (status.toUpperCase()) {
      case 'CONFIRMED':
        return 'primary';
      case 'PENDING':
        return 'accent';
      case 'CANCELLED':
        return 'warn';
      default:
        return 'primary';
    }
  }

  // Action methods
  viewBookingDetails(booking: FlightBooking) {
    console.log('View details for booking:', booking.bookingNumber);
    // Future implementation: Navigate to booking details page or open details modal
    alert(`Viewing details for booking ${booking.bookingNumber}\nPassenger: ${booking.firstName} ${booking.lastName}\nFlight: ${booking.from} → ${booking.to}\nStatus: ${booking.bookingStatus}`);
  }

  editBooking(booking: FlightBooking) {
    console.log('Edit booking:', booking.bookingNumber);
    // Future implementation: Navigate to edit form or open edit modal
    alert(`Edit functionality for booking ${booking.bookingNumber} will be implemented in future updates.`);
  }

  cancelBooking(booking: FlightBooking) {
    if (confirm(`Are you sure you want to cancel booking ${booking.bookingNumber}?`)) {
      this.bookingService.updateBookingStatus(booking.bookingNumber, 'CANCELLED');
    }
  }

  // Export functionality
  exportToCSV() {
    const csvData = this.bookings().map(booking => ({
      'Booking Number': booking.bookingNumber,
      'First Name': booking.firstName,
      'Last Name': booking.lastName,
      'Date': booking.date,
      'Status': booking.bookingStatus,
      'From': booking.from,
      'To': booking.to,
      'Seat': booking.seat
    }));

    const csvString = this.convertToCSV(csvData);
    this.downloadCSV(csvString, 'flight-bookings.csv');
  }

  private convertToCSV(data: any[]): string {
    if (data.length === 0) return '';

    const headers = Object.keys(data[0]);
    const csvHeaders = headers.join(',');

    const csvRows = data.map(row =>
      headers.map(header => {
        const value = row[header];
        return typeof value === 'string' && value.includes(',')
          ? `"${value}"`
          : value;
      }).join(',')
    );

    return [csvHeaders, ...csvRows].join('\n');
  }

  private downloadCSV(csvString: string, filename: string) {
    const blob = new Blob([csvString], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
