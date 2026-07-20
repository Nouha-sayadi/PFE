import { Component, inject, OnInit } from '@angular/core';
import { ModalService } from '../../../services/modal.service';
import { InputFieldComponent } from '../../form/input/input-field.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { LabelComponent } from '../../form/label/label.component';
import { ModalComponent } from '../../ui/modal/modal.component';
import { UserService } from 'app/services/user.service';
import { User } from 'app/shared/models/user';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-info-card',
  standalone: true,
  imports: [InputFieldComponent, ButtonComponent, LabelComponent, ModalComponent, CommonModule],
  templateUrl: './user-info-card.component.html',
  styles: ``
})
export class UserInfoCardComponent implements OnInit {

  private userService = inject(UserService);
  constructor(public modal: ModalService) {}

  isOpen = false;
  isSaving = false;
  saveSuccess = false;

  user: User = { nom: '', prenom: '', email: '', profils: [] };

  // ✅ Getters/Setters pour les champs dynamiques
  get telephone(): string { return (this.user as any).telephone || ''; }
  set telephone(v: string) { (this.user as any).telephone = v; }

  get pays(): string { return (this.user as any).pays || ''; }
  set pays(v: string) { (this.user as any).pays = v; }

  get codePostal(): string { return (this.user as any).codePostal || ''; }
  set codePostal(v: string) { (this.user as any).codePostal = v; }

  openModal() { this.isOpen = true; this.saveSuccess = false; }
  closeModal() { this.isOpen = false; }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (data) => { this.user = data; },
      error: (err) => console.error(err)
    });
  }

  handleSave() {
    if (!this.user?.id) return;
    this.isSaving = true;

    const payload = {
      nom: this.user.nom,
      prenom: this.user.prenom,
      email: this.user.email,
      telephone: this.telephone,
      pays: this.pays,
      codePostal: this.codePostal,
      profilIds: this.user.profils?.map((p: any) => p.id) ?? []
    };

    this.userService.updateUser(this.user.id!, payload).subscribe({
      next: (res) => {
        this.user = res;
        this.isSaving = false;
        this.saveSuccess = true;
        setTimeout(() => { this.saveSuccess = false; this.closeModal(); }, 1500);
      },
      error: (err) => { console.error(err); this.isSaving = false; }
    });
  }
}