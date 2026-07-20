import { Component ,inject } from '@angular/core';
import { DropdownComponent } from '../../ui/dropdown/dropdown.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DropdownItemTwoComponent } from '../../ui/dropdown/dropdown-item/dropdown-item.component-two';
import { User } from 'app/shared/models/user';
import { UserService } from 'app/services/user.service';
import { keycloak } from 'app/keycloak/keyclock.config';

@Component({
  selector: 'app-user-dropdown',
  templateUrl: './user-dropdown.component.html',
  imports:[CommonModule,RouterModule,DropdownComponent,DropdownItemTwoComponent]
})
export class UserDropdownComponent {
  isOpen = false;

   user!: User;

   private userService = inject(UserService)

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (data) => {
        this.user = data;
        console.log("USER CONNECTED:", data);
      },
      error: (err) => {
        console.error("Erreur load user", err);
      }
    });
  }

  logout() {
  // 1. nettoyer stockage local
  localStorage.clear();
  sessionStorage.clear();

  // 2. logout Keycloak
  keycloak.logout({
    redirectUri: window.location.origin
  });
}


  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  closeDropdown() {
    this.isOpen = false;
  }
}