import { Component } from '@angular/core';
import { SignupFormComponent } from '../auth/signup-form/signup-form.component';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CheckboxComponent } from '../form/input/checkbox.component';
import { UserService } from '../../../services/user.service';
@Component({                                    
  selector: 'app-st2i',
  standalone: true,
  imports: [
  
    RouterModule,
    FormsModule
  ],
  templateUrl: './st2i.component.html',
  styleUrls: ['./st2i.component.css'],
})
export class St2iComponent { nom: string = '';
  prenom: string = '';
  email: string = '';
  password: string = '';

  showPassword = false;
  isChecked = false;

  constructor(private userService: UserService) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {

    if (!this.isChecked) {
      alert("Veuillez accepter les conditions");
      return;
    }

    const user = {
      nom: this.nom,
      prenom: this.prenom,
      email: this.email,
      password: this.password,
      profilCodes: ['USER']
    };

    console.log("USER SENT:", user);

    this.userService.register(user).subscribe({
      next: (res: any) => {
        console.log("SUCCESS:", res);
        alert("User created successfully ✅");
        this.resetForm();
      },
      error: (err: any) => {
        console.error("ERROR:", err);
        alert("Error while creating user ❌");
      }
    });
  }

  resetForm() {
    this.nom = '';
    this.prenom = '';
    this.email = '';
    this.password = '';
    this.isChecked = false;
  }
}


