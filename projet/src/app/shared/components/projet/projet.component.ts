import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Projet, ProjetService } from 'app/services/projet.service';
import { Affectation, AffectationService } from 'app/services/affectation.service';
import { UserService } from 'app/services/user.service';
import { NomenclatureItem, NomenclatureService } from 'app/services/nomenclature.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-projet',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projet.component.html',
  styleUrl: './projet.component.css',
})
export class ProjetComponent implements OnInit {

  projets: Projet[] = [];
  filteredProjets: Projet[] = [];
  searchQuery = '';


  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  showDetailModal = false;
  showAffectModal = false;

  selectedProjet: Projet | null = null;
    allProjets:   any[] = [];
allCoutReels: any[] = [];
allRisques:   any[] = [];
  deleteTarget: Projet | null = null;

  // ── Affectation ──
  membresProjet: Affectation[] = [];
  allUsers: any[] = [];
  affectForm = { utilisateurId: 0, profilId: 0 };
  profilsUserSelectionne: any[] = [];
  userPermissions: string[] = [];

  // ── Nomenclatures ──
  clients: NomenclatureItem[] = [];
  bailleurs: NomenclatureItem[] = [];
  partenaires: NomenclatureItem[] = [];
  devises: NomenclatureItem[] = [];
  modeles: any[] = [];

  statuts = ['A_DEMARRER', 'EN_COURS', 'TERMINE', 'EN_ATTENTE', 'ANNULE', 'PLANIFIE'];
  createForm: any = this.emptyForm();
  editForm: any = {};
  toast = { visible: false, message: '', type: 'success' };

  constructor(
    private projetService: ProjetService,
    private affectationService: AffectationService,
    private userService: UserService,
    private nomenclatureService: NomenclatureService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProjets();
    this.loadNomenclatures();
    this.userService.getAll().subscribe(u => this.allUsers = u);
    this.userService.getMyPermissions().subscribe({
      next: (data) => {
        this.userPermissions = (data?.permissions ?? []).map((p: any) => p.code);
      },
      error: () => {
        this.userPermissions = this.userService.getPermissions() ?? [];
      }
    });
  }



  // ✅ Charger toutes les nomenclatures
  loadNomenclatures() {
    this.nomenclatureService.getAll('clients').subscribe(d => this.clients = d);
    this.nomenclatureService.getAll('bailleurs').subscribe(d => this.bailleurs = d);
    this.nomenclatureService.getAll('partenaires').subscribe(d => this.partenaires = d);
    this.nomenclatureService.getAll('devises').subscribe(d => this.devises = d);
    this.nomenclatureService.getAll('modeles').subscribe(d => this.modeles = d);
  }

  canCreate(): boolean  { return this.userPermissions.includes('POST_PROJET'); }
  canEdit(): boolean    { return this.userPermissions.includes('PUT_PROJET'); }
  canDelete(): boolean  { return this.userPermissions.includes('DELETE_PROJET'); }
  canAffecter(): boolean { return this.userPermissions.includes('PUT_PROJET'); }

  goToDetail(id: number) {
    this.router.navigate(['/projets', id, 'detail']);
  }

  get partenaireObligatoireCreate(): boolean {
  if (!this.createForm.modeleBusinessId) return false;
  const modele = this.modeles.find(m => m.id === Number(this.createForm.modeleBusinessId));
  return modele?.typeGroupement === 'GROUPEMENT';
}

get partenaireObligatoireEdit(): boolean {
  if (!this.editForm.modeleBusinessId) return false;
  const modele = this.modeles.find(m => m.id === Number(this.editForm.modeleBusinessId));
  return modele?.typeGroupement === 'GROUPEMENT';
}



  emptyForm(): any {
    return {
      titre: '', description: '', statut: 'A_DEMARRER',
      budgetInitial: undefined,
      dateDemarrage: undefined, dateFinPrevu: undefined,
      tauxAvancement: 0,
      clientId: null, bailleurId: null,
      partenaireId: null, deviseId: null,
      modeleBusinessId: null
    };
  }

  loadProjets() {
    this.projetService.getAll().subscribe({
      next: p => { this.projets = p; this.applyFilter(); },
      error: () => this.showToast('Erreur chargement projets', 'error')
    });
  }

  applyFilter() {
    const q = this.searchQuery.toLowerCase();
    this.filteredProjets = this.projets.filter(p =>
      p.titre?.toLowerCase().includes(q) ||
      p.description?.toLowerCase().includes(q) ||
      p.statut?.toLowerCase().includes(q)
    );
  }

  get enCours() { return this.projets.filter(p => p.statut === 'EN_COURS').length; }
  get termines() { return this.projets.filter(p => p.statut === 'TERMINE').length; }
  get planifies() { return this.projets.filter(p => p.statut === 'A_DEMARRER' || p.statut === 'PLANIFIE').length; }

  // ── CREATE ──
  openCreate() { this.createForm = this.emptyForm(); this.showCreateModal = true; }

  submitCreate() {

  const payload: any = {
    titre: this.createForm.titre,
    description: this.createForm.description,
    statut: this.createForm.statut,
    tauxAvancement: this.createForm.tauxAvancement,
    budgetInitial: this.createForm.budgetInitial,
    dateDemarrage: this.createForm.dateDemarrage,
    dateFinPrevu: this.createForm.dateFinPrevu,

    client: this.createForm.clientId
      ? { id: this.createForm.clientId }
      : null,

    bailleur: this.createForm.bailleurId
      ? { id: this.createForm.bailleurId }
      : null,

    partenaire: this.createForm.partenaireId
      ? { id: this.createForm.partenaireId }
      : null,

    devise: this.createForm.deviseId
      ? { id: this.createForm.deviseId }
      : null,

    modeleBusiness: this.createForm.modeleBusinessId
      ? { id: this.createForm.modeleBusinessId }
      : null
  };

console.log(JSON.stringify(payload, null, 2));
  this.projetService.create(payload).subscribe({
    next: () => {
      this.showCreateModal = false;
      this.loadProjets();
      this.showToast('Projet créé');
    },
    error: (err) => {
      console.error(err);
      this.showToast('Erreur création', 'error');
    }
  });
}

  // ── EDIT ──
  openEdit(p: Projet) {
    this.editForm = {
      ...p,
      clientId:         p.client?.id         || null,
      bailleurId:       p.bailleur?.id       || null,
      partenaireId:     p.partenaire?.id     || null,
      deviseId:         p.devise?.id         || null,
      modeleBusinessId: p.modeleBusiness?.id || null,
    };
    this.showEditModal = true;
  }

  submitEdit() {
    const f = this.editForm;
    const payload: any = {
      titre:           f.titre,
      description:     f.description,
      statut:          f.statut,
      tauxAvancement:  f.tauxAvancement,
      budgetInitial:   f.budgetInitial,
      montantTotal:    f.montantTotal,
      dateDemarrage:   f.dateDemarrage,
      dateFinPrevu:    f.dateFinPrevu,
      client:          f.clientId          ? { id: Number(f.clientId) }          : null,
      bailleur:        f.bailleurId        ? { id: Number(f.bailleurId) }        : null,
      partenaire:      f.partenaireId      ? { id: Number(f.partenaireId) }      : null,
      devise:          f.deviseId          ? { id: Number(f.deviseId) }          : null,
      modeleBusiness:  f.modeleBusinessId  ? { id: Number(f.modeleBusinessId) }  : null,
    };
    this.projetService.update(f.id!, payload).subscribe({
      next: () => { this.showEditModal = false; this.loadProjets(); this.showToast('Projet mis à jour'); },
      error: () => this.showToast('Erreur mise à jour', 'error')
    });
  }

  // ── DELETE ──
  openDelete(p: Projet) { this.deleteTarget = p; this.showDeleteModal = true; }

  submitDelete() {
    this.projetService.delete(this.deleteTarget!.id!).subscribe({
      next: () => { this.showDeleteModal = false; this.loadProjets(); this.showToast('Projet supprimé'); },
      error: () => this.showToast('Erreur suppression', 'error')
    });
  }

  // ── DETAIL ──
  openDetail(p: Projet) { this.selectedProjet = p; this.showDetailModal = true; }

  // ── AFFECTATION ──
  openAffect(p: Projet) {
    this.selectedProjet = p;
    this.affectForm = { utilisateurId: 0, profilId: 0 };
    this.profilsUserSelectionne = [];
    this.affectationService.getMembresProjet(p.id!).subscribe(m => this.membresProjet = m);
    this.showAffectModal = true;
  }

  get usersDisponibles(): any[] {
    const idsAffectes = this.membresProjet.map(m => m.utilisateur?.id).filter(id => id != null);
    return this.allUsers.filter(u => !idsAffectes.includes(u.id));
  }

  onUserChange() {
    const user = this.allUsers.find(u => u.id == this.affectForm.utilisateurId);
    this.profilsUserSelectionne = user?.profils ?? [];
    this.affectForm.profilId = 0;
  }

  submitAffectation() {
    if (!this.affectForm.utilisateurId || !this.affectForm.profilId) {
      this.showToast('Sélectionnez un utilisateur et un profil', 'error');
      return;
    }
    const request = {
      utilisateurId: this.affectForm.utilisateurId,
      projetId: this.selectedProjet!.id!,
      profilId: this.affectForm.profilId
    };
    this.affectationService.affecter(request).subscribe({
      next: () => {
        this.affectForm = { utilisateurId: 0, profilId: 0 };
        this.profilsUserSelectionne = [];
        this.affectationService.getMembresProjet(this.selectedProjet!.id!)
          .subscribe(m => this.membresProjet = m);
        this.showToast('Membre affecté');
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erreur affectation';
        this.showToast(msg, 'error');
      }
    });
  }

  retirerMembre(affectationId: number) {
    this.affectationService.retirer(affectationId).subscribe({
      next: () => {
        this.membresProjet = this.membresProjet.filter(m => m.id !== affectationId);
        this.showToast('Membre retiré');
      },
      error: () => this.showToast('Erreur suppression', 'error')
    });
  }

  avatarInitials(a: Affectation): string {
    return ((a.utilisateur?.prenom?.[0] || '') + (a.utilisateur?.nom?.[0] || '')).toUpperCase();
  }

  showToast(message: string, type = 'success') {
    this.toast = { visible: true, message, type };
    setTimeout(() => this.toast.visible = false, 3000);
  }

  statutClass(statut?: string): string {
    const map: Record<string, string> = {
      'A_DEMARRER': 'badge-planifie', 'EN_COURS': 'badge-encours',
      'TERMINE': 'badge-termine', 'EN_ATTENTE': 'badge-attente',
      'ANNULE': 'badge-annule', 'PLANIFIE': 'badge-planifie'
    };
    return map[statut || ''] || 'badge-planifie';
  }

  statutLabel(statut?: string): string {
    const map: Record<string, string> = {
      'A_DEMARRER': 'À démarrer', 'EN_COURS': 'En cours', 'TERMINE': 'Terminé',
      'EN_ATTENTE': 'En attente', 'ANNULE': 'Annulé', 'PLANIFIE': 'Planifié'
    };
    return map[statut || ''] || statut || '';
  }

  progressColor(taux?: number): string {
    if (!taux) return '#e5e7eb';
    if (taux >= 80) return '#0f6e56';
    if (taux >= 50) return '#185FA5';
    return '#f59e0b';
  }
}