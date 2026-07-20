import { Component, Inject } from '@angular/core';
import { CheckboxComponent } from '../../form/input/checkbox.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-signup-form',
  standalone: true,
  imports: [
    CheckboxComponent,
    RouterModule,
    FormsModule
  ],
  templateUrl: './signup-form.component.html'
})
export class SignupFormComponent {

   nom: string = '';
  prenom: string = '';
  email: string = '';
  password: string = '';

  showPassword = false;
  isChecked = false;


  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {

  }
}