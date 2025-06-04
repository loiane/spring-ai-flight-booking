import { Component, signal, computed, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';

import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatCardModule,
    MatToolbarModule
  ],
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.scss']
})
export class BookingsComponent {
  @ViewChild(MatSort) sort!: MatSort;

  private readonly bookingService = inject(BookingService);

  // Data signals from service
  bookings = this.bookingService.bookings;

  // Table configuration
  displayedColumns = signal<string[]>([
    'bookingNumber',
    'name',
    'date',
    'bookingStatus',
    'from',
    'to',
    'seat'
  ]);

  // Data source for Material Table
  dataSource = computed(() => {
    const data = new MatTableDataSource(this.bookings());
    // Set sort after view init
    setTimeout(() => {
      if (this.sort) data.sort = this.sort;
    });
    return data;
  });

  ngAfterViewInit() {
    // Initialize table with sort
    const dataSource = this.dataSource();
    if (this.sort) dataSource.sort = this.sort;
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

  // Export functionality
  exportToCSV() {
    const csvData = this.bookings().map(booking => ({
      'Booking Number': booking.bookingNumber,
      'Name': `${booking.firstName} ${booking.lastName}`,
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
