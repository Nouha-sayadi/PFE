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
  errorMsg = '';

  toast = { visible: false, message: '', type: 'success' as 'success' | 'error' };

  showToast(message: string, type: 'success' | 'error' = 'success') {
    this.toast = { visible: true, message, type };
    setTimeout(() => this.toast.visible = false, 3000);
  }

  uploadingPhoto = false;
  photoErrorMsg = '';

  user: User = { nom: '', prenom: '', email: '', profils: [] };

  photoUrl(): string {
    return this.user?.id ? this.userService.getPhotoUrl(this.user.id) : '';
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file || !this.user?.id) return;

    this.photoErrorMsg = '';
    this.uploadingPhoto = true;
    this.userService.uploadMyPhoto(file).subscribe({
      next: (updated) => {
        this.uploadingPhoto = false;
        this.user = updated;
      },
      error: (err: any) => {
        this.uploadingPhoto = false;
        this.photoErrorMsg = err?.error?.message || "Échec de l'envoi de la photo.";
      }
    });
  }

  // ✅ Getters/Setters pour les champs dynamiques
  get telephone(): string { return (this.user as any).telephone || ''; }
  set telephone(v: string) { (this.user as any).telephone = v; }

  get pays(): string { return (this.user as any).pays || ''; }
  set pays(v: string) { (this.user as any).pays = v; }

  get codePostal(): string { return (this.user as any).codePostal || ''; }
  set codePostal(v: string) { (this.user as any).codePostal = v; }

  openModal() { this.isOpen = true; this.errorMsg = ''; }
  closeModal() { this.isOpen = false; }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (data) => { this.user = data; },
      error: (err) => console.error(err)
    });
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  handleSave() {
    if (!this.user?.id) return;
    this.errorMsg = '';
    if (!this.user.nom?.trim() || !this.user.prenom?.trim() || !this.user.email?.trim()) {
      this.errorMsg = 'Nom, prénom et email sont obligatoires.';
      return;
    }
    if (!this.isValidEmail(this.user.email)) {
      this.errorMsg = "L'email n'est pas valide.";
      return;
    }
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
        this.closeModal();
        this.showToast('Profil modifié avec succès');
      },
      error: (err) => {
        console.error(err);
        this.isSaving = false;
        this.errorMsg = err?.error?.message || 'Échec de la mise à jour du profil. Veuillez réessayer.';
      }
    });
  }
}