import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AppNotification, NotificationService } from 'app/services/notification.service';
import { DropdownComponent } from '../../ui/dropdown/dropdown.component';

@Component({
  selector: 'app-notification-dropdown',
  templateUrl: './notification-dropdown.component.html',
  imports: [CommonModule, RouterModule, DropdownComponent]
})
export class NotificationDropdownComponent implements OnInit, OnDestroy {
  isOpen        = false;
  notifications: AppNotification[] = [];
  unreadCount   = 0;
  toast: AppNotification | null = null;
  private toastTimer: any;
  private subs: Subscription[] = [];

  constructor(
    public notifService: NotificationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.subs.push(
      this.notifService.notifications$.subscribe(list => {
        this.notifications = list;
      }),
      this.notifService.unreadCount$.subscribe(c => {
        this.unreadCount = c;
      }),
      this.notifService.newNotif$.subscribe(n => {
        if (n) this.showToast(n);
      })
    );
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  closeDropdown() {
    this.isOpen = false;
  }

  clickNotif(n: AppNotification) {
    if (!n.read) this.notifService.markAsRead(n.id);
    this.closeDropdown();
    if (n.projectId) {
      this.router.navigate(['/projet', n.projectId]);
    }
  }

  markAllAsRead() {
    this.notifService.markAllAsRead();
  }

  timeAgo(date: string): string {
    return this.notifService.timeAgo(date);
  }

  iconClass(type: string): string {
    switch (type) {
      case 'PROJECT_ASSIGNMENT': return 'notif-icon-blue';
      case 'POINTAGE_REMINDER':  return 'notif-icon-orange';
      case 'DEADLINE_EXCEEDED':  return 'notif-icon-red';
      default: return 'notif-icon-gray';
    }
  }

  iconLabel(type: string): string {
    switch (type) {
      case 'PROJECT_ASSIGNMENT': return 'P';
      case 'POINTAGE_REMINDER':  return 'R';
      case 'DEADLINE_EXCEEDED':  return '!';
      default: return 'i';
    }
  }

  private showToast(n: AppNotification) {
    this.toast = n;
    clearTimeout(this.toastTimer);
    this.toastTimer = setTimeout(() => (this.toast = null), 4000);
  }

  dismissToast() {
    this.toast = null;
    clearTimeout(this.toastTimer);
  }

  ngOnDestroy() {
    this.subs.forEach(s => s.unsubscribe());
    clearTimeout(this.toastTimer);
  }
}
