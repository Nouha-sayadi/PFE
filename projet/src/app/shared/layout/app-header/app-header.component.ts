import { Component, OnInit, OnDestroy } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, ActivatedRoute, RouterModule } from '@angular/router';
import { ThemeToggleButtonComponent } from '../../components/common/theme-toggle/theme-toggle-button.component';
import { NotificationDropdownComponent } from '../../components/header/notification-dropdown/notification-dropdown.component';
import { UserDropdownComponent } from '../../components/header/user-dropdown/user-dropdown.component';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  imports: [
    CommonModule,
    RouterModule,
    ThemeToggleButtonComponent,
    NotificationDropdownComponent,
    UserDropdownComponent,
  ],
  templateUrl: './app-header.component.html',
  styleUrls: ['./app-header.component.css'],
})
export class AppHeaderComponent implements OnInit, OnDestroy {
  isApplicationMenuOpen = false;
  readonly isMobileOpen$;
  pageTitle = 'Dashboard';
  private navSub?: Subscription;

  constructor(
    public sidebarService: SidebarService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {
    this.isMobileOpen$ = this.sidebarService.isMobileOpen$;
  }

  ngOnInit(): void {
    this.pageTitle = this.resolveTitle();
    this.navSub = this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => (this.pageTitle = this.resolveTitle()));
  }

  ngOnDestroy(): void {
    this.navSub?.unsubscribe();
  }

  private resolveTitle(): string {
    let r = this.activatedRoute;
    while (r.firstChild) r = r.firstChild;
    const raw = r.snapshot.title ?? r.snapshot.data?.['title'] ?? '';
    return raw
      .replace(/^ST2I\s+/i, '')
      .replace(/^Angular\s+/i, '')
      .trim() || 'Dashboard';
  }

  handleToggle() {
    if (window.innerWidth >= 1280) {
      this.sidebarService.toggleExpanded();
    } else {
      this.sidebarService.toggleMobileOpen();
    }
  }

  toggleApplicationMenu() {
    this.isApplicationMenuOpen = !this.isApplicationMenuOpen;
  }
}
