import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { TCCService, TCC } from 'app/services/tcc.service';
import { PageHeaderComponent } from '../page-header/page-header.component';

@Component({
  selector: 'app-users',
  imports: [CommonModule, FormsModule, PageHeaderComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css',
})
export class UsersComponent implements OnInit {

  users: any[] = [];
  filteredUsers: any[] = [];
  allProfils: any[] = [];
  searchQuery = '';

  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  showTCCModal = false;
  showDetailModal = false;

  createForm = { nom: '', prenom: '', email: '', password: '', profilCodes: [] as string[] };
  editForm: any = { id: 0, nom: '', prenom: '', email: '', profils: [] as any[] };
  deleteTarget: any = null;

  // ── Détail ──
  detailUser: any = null;
  detailTCCList: TCC[] = [];
  detailLoadingTCC = false;

  // ── TCC ──
  selectedUser: any = null;
  tccList: TCC[] = [];
  tccForm: any = {};
  editingTccId: number | null = null;

  // ── Permissions ──
  userPermissions: string[] = [];

  toast = { visible: false, message: '', type: 'success' };

  constructor(
    private userService: UserService,
    private tccService: TCCService
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.userService.getAllProfils().subscribe(p => this.allProfils = p);
    this.loadPermissions();
  }

  // ── Permissions ──
  loadPermissions() {
    this.userService.getMyPermissions().subscribe({
      next: (data) => {
        this.userPermissions = (data?.permissions ?? []).map((p: any) => p.code);
      },
      error: () => {
        this.userPermissions = this.userService.getPermissions() ?? [];
      }
    });
  }

  canCreate(): boolean    { return this.userPermissions.includes('POST_USER'); }
  canEdit(): boolean      { return this.userPermissions.includes('PUT_USER'); }
  canDelete(): boolean    { return this.userPermissions.includes('DELETE_USER'); }
  canManageTCC(): boolean {
    return this.userPermissions.includes('GET_TCC')    ||
           this.userPermissions.includes('POST_TCC')   ||
           this.userPermissions.includes('PUT_TCC')    ||
           this.userPermissions.includes('DELETE_TCC');
  }
  canCreateTCC(): boolean { return this.userPermissions.includes('POST_TCC'); }
  canEditTCC(): boolean   { return this.userPermissions.includes('PUT_TCC'); }
  canDeleteTCC(): boolean { return this.userPermissions.includes('DELETE_TCC'); }

  // ── Chargement users ──
  loadUsers() {
    this.userService.getAll().subscribe({
      next: u => { this.users = u; this.applyFilter(); },
      error: (err) => console.error('Erreur loadUsers:', err)
    });
  }

  applyFilter() {
    const q = this.searchQuery.toLowerCase();
    if (!q) { this.filteredUsers = [...this.users]; return; }
    this.filteredUsers = this.users.filter(u =>
      u.nom?.toLowerCase().includes(q) ||
      u.prenom?.toLowerCase().includes(q) ||
      u.email?.toLowerCase().includes(q)
    );
  }

  get adminCount() {
    return this.users.filter(u =>
      u.profils?.some((p: any) => p.code === 'ADMIN')
    ).length;
  }

  avatarInitials(u: any): string {
    return ((u.prenom?.[0] || '') + (u.nom?.[0] || '')).toUpperCase();
  }

  getProfilsLabel(u: any): string {
    if (!u?.profils?.length) return 'Aucun';
    return u.profils.map((p: any) => p.nom || p.code).join(', ');
  }

  // ── DÉTAIL ──
  openDetail(u: any) {
    this.detailUser = u;
    this.detailTCCList = [];
    this.detailLoadingTCC = true;
    this.showDetailModal = true;
    this.tccService.getByUser(u.id).subscribe({
      next: (list) => {
        this.detailTCCList = list.sort((a, b) =>
          new Date(b.annee || '').getTime() - new Date(a.annee || '').getTime()
        );
        this.detailLoadingTCC = false;
      },
      error: () => { this.detailLoadingTCC = false; }
    });
  }

  get latestTCC(): TCC | null {
    return this.detailTCCList.length > 0 ? this.detailTCCList[0] : null;
  }

  // ── CREATE ──
  openCreate() {
    this.createForm = { nom: '', prenom: '', email: '', password: '', profilCodes: [] };
    this.showCreateModal = true;
  }

  toggleCreateProfil(code: string) {
    const i = this.createForm.profilCodes.indexOf(code);
    i >= 0
      ? this.createForm.profilCodes.splice(i, 1)
      : this.createForm.profilCodes.push(code);
  }

  submitCreate() {
    const payload = {
      nom: this.createForm.nom,
      prenom: this.createForm.prenom,
      email: this.createForm.email,
      password: this.createForm.password,
      profilCodes: this.createForm.profilCodes
    };
    this.userService.createUserAdmin(payload).subscribe({
      next: () => {
        this.showCreateModal = false;
        this.loadUsers();
        this.showToast('Utilisateur créé');
      },
      error: () => this.showToast('Erreur lors de la création', 'error')
    });
  }

  // ── EDIT ──
  openEdit(u: any) {
    this.editForm = {
      id: u.id, nom: u.nom, prenom: u.prenom,
      email: u.email, profils: u.profils ? [...u.profils] : []
    };
    this.showEditModal = true;
  }

  isProfilSelected(profilId: number): boolean {
    return this.editForm.profils.some((p: any) => p.id === profilId);
  }

  toggleEditProfil(profil: any) {
    const i = this.editForm.profils.findIndex((p: any) => p.id === profil.id);
    i >= 0
      ? this.editForm.profils.splice(i, 1)
      : this.editForm.profils.push(profil);
  }

  submitEdit() {
    const payload = {
      nom: this.editForm.nom,
      prenom: this.editForm.prenom,
      email: this.editForm.email,
      profils: this.editForm.profils
    };
    this.userService.updateUser(this.editForm.id, payload).subscribe({
      next: () => {
        this.showEditModal = false;
        this.loadUsers();
        this.showToast('Utilisateur mis à jour');
      },
      error: () => this.showToast('Erreur lors de la mise à jour', 'error')
    });
  }

  // ── DELETE ──
  openDelete(u: any) {
    this.deleteTarget = u;
    this.showDeleteModal = true;
  }

  submitDelete() {
    this.userService.delete(this.deleteTarget.id).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.loadUsers();
        this.showToast('Utilisateur supprimé');
      },
      error: () => this.showToast('Erreur lors de la suppression', 'error')
    });
  }

  // ── TCC ──
  openTCC(u: any) {
    this.selectedUser = u;
    this.tccForm = this.emptyTccForm();
    this.editingTccId = null;
    this.tccService.getByUser(u.id).subscribe(list => this.tccList = list);
    this.showTCCModal = true;
  }

  emptyTccForm() {
    // ✅ montant supprimé
    return { annee: '', tccBase: null, tccAvecFG: null };
  }

  

  // ✅ Calcul correct : (tccBase + tccAvecFG) / 264
  get tarifHJPreview(): number {
    const tccBase   = this.tccForm.tccBase   || 0;
    const tccAvecFG = this.tccForm.tccAvecFG || 0;
    if (!tccBase && !tccAvecFG) return 0;
    return (tccBase + tccAvecFG) / 264;
  }

  submitTCC() {
  const payload = {
    utilisateurId: this.selectedUser.id,
    annee: this.tccForm.annee
      ? new Date(this.tccForm.annee + '-01-01').toISOString().split('T')[0]
      : null,
    tccBase:   this.tccForm.tccBase,
    tccAvecFG: this.tccForm.tccAvecFG
  };

  this.tccService.create(payload).subscribe({
    next: () => {
      this.tccService.getByUser(this.selectedUser.id)
        .subscribe(list => this.tccList = list);
      this.tccForm = this.emptyTccForm();
      this.showToast('TCC ajouté ✅');
    },
    error: () => this.showToast('Erreur TCC', 'error')
  });
}

  deleteTCC(id: number) {
    this.tccService.delete(id).subscribe({
      next: () => {
        this.tccList = this.tccList.filter(t => t.id !== id);
        this.showToast('TCC supprimé');
      },
      error: () => this.showToast('Erreur suppression', 'error')
    });
  }

  

  showToast(message: string, type = 'success') {
    this.toast = { visible: true, message, type };
    setTimeout(() => this.toast.visible = false, 3000);
  }
}