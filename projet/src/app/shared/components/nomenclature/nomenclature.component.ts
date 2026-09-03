import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NomenclatureItem, NomenclatureService } from 'app/services/nomenclature.service';
import { UserService } from 'app/services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PageHeaderComponent } from '../page-header/page-header.component';

interface NomenclatureTab {
  label: string;
  type: string;
  icon: string;
}

@Component({
  selector: 'app-nomenclature',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeaderComponent],
  templateUrl: './nomenclature.component.html',
  styleUrl: './nomenclature.component.css'
})
export class NomenclatureComponent implements OnInit {

  tabs: NomenclatureTab[] = [
    { label: 'Clients',          type: 'clients',     icon: '👤' },
    { label: 'Bailleurs',        type: 'bailleurs',   icon: '🏦' },
    { label: 'Partenaires',      type: 'partenaires', icon: '🤝' },
    { label: 'Devises',          type: 'devises',     icon: '💱' },
    { label: 'Modèles Business', type: 'modeles',     icon: '📋' },
  ];

  activeTab = 'clients';
  items: NomenclatureItem[] = [];
  loading = false;

  showModal = false;
  showDeleteModal = false;
  editingId: number | null = null;
  deleteTarget: NomenclatureItem | null = null;

  form: any = {};
  toast = { visible: false, message: '', type: 'success' };
  userPermissions: string[] = [];

  constructor(
    private nomenclatureService: NomenclatureService,
    private userService: UserService,
     private route: ActivatedRoute, 
     private router: Router  
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
    if (params['tab']) {
      this.activeTab = params['tab'];
    }
    this.loadItems();
  });

  this.userService.getMyPermissions().subscribe({
    next: (data) => {
      this.userPermissions = (data?.permissions ?? []).map((p: any) => p.code);
    },
    error: () => {
      this.userPermissions = this.userService.getPermissions() ?? [];
    }
  });
  }

  getPermissionPrefix(): string {
  switch (this.activeTab) {
    case 'clients':     return 'CLIENT';
    case 'bailleurs':   return 'BAILLEUR';
    case 'partenaires': return 'PARTENAIRE';
    case 'devises':     return 'DEVISE';
    case 'modeles':     return 'MODELE';
    default:            return 'NOMENCLATURE';
  }
}

canCreate(): boolean {
  const p = this.getPermissionPrefix();
  return this.userPermissions.includes(`POST_${p}`) ||
         this.userPermissions.includes('ADMINISTRATION');
}

canEdit(): boolean {
  const p = this.getPermissionPrefix();
  return this.userPermissions.includes(`PUT_${p}`) ||
         this.userPermissions.includes('ADMINISTRATION');
}

canDelete(): boolean {
  const p = this.getPermissionPrefix();
  return this.userPermissions.includes(`DELETE_${p}`) ||
         this.userPermissions.includes('ADMINISTRATION');
}

canViewTab(type: string): boolean {
  const map: Record<string, string> = {
    'clients':     'GET_CLIENT',
    'bailleurs':   'GET_BAILLEUR',
    'partenaires': 'GET_PARTENAIRE',
    'devises':     'GET_DEVISE',
    'modeles':     'GET_MODELE'
  };
  return this.userPermissions.includes(map[type]) ||
         this.userPermissions.includes('ADMINISTRATION');
}

  get currentTab(): NomenclatureTab {
    return this.tabs.find(t => t.type === this.activeTab)!;
  }

  setTab(type: string) {
    this.activeTab = type;
  this.router.navigate(['/nomenclatures', type]); 
  this.loadItems();
  }

  loadItems() {
    this.loading = true;
    this.nomenclatureService.getAll(this.activeTab).subscribe({
      next: data => { this.items = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  // ✅ Formulaire vide selon le type
  emptyForm(): any {
    switch (this.activeTab) {
      case 'clients':
        return { code: '', nom: '', raisonSociale: '', adresse: '',
                 telephone: '', email: '', matriculeFiscale: '',
                 registreCommerce: '', secteurActivite: '', pays: 'Tunisie' };
      case 'bailleurs':
        return { code: '', nom: '', adresse: '', telephone: '',
                 email: '', typeFinancement: '', pays: 'Tunisie' };
      case 'partenaires':
        return { code: '', nom: '', raisonSociale: '', adresse: '',
                 telephone: '', email: '', pays: 'Tunisie', partGroupement: null };
      case 'devises':
        return { code: '', libelle: '', tauxChange: 1.0, estDeviseBase: false };
      case 'modeles':
        return { code: '', libelle: '', typeGroupement: 'SEUL',
                 partenaireObligatoire: false };
      default:
        return { code: '', nom: '' };
    }
  }

  openCreate() {
    this.form = this.emptyForm();
    this.editingId = null;
    this.showModal = true;
  }

  openEdit(item: NomenclatureItem) {
    this.form = { ...item };
    this.editingId = item.id!;
    this.showModal = true;
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  submitForm() {
    if (!this.formValide) {
      this.showToast('Veuillez remplir les champs obligatoires', 'error');
      return;
    }
    if (this.form.email && !this.isValidEmail(this.form.email)) {
      this.showToast('L\'email n\'est pas valide', 'error');
      return;
    }
    // ✅ Adapter le payload selon le type
    const payload = this.buildPayload();
    const obs = this.editingId
      ? this.nomenclatureService.update(this.activeTab, this.editingId, payload)
      : this.nomenclatureService.create(this.activeTab, payload);
    obs.subscribe({
      next: () => {
        this.showModal = false;
        this.loadItems();
        this.showToast(this.editingId ? 'Modifié ✅' : 'Créé ✅');
      },
      error: () => this.showToast('Erreur', 'error')
    });
  }

  // ✅ Construire payload selon le type
  buildPayload(): any {
    switch (this.activeTab) {
      case 'devises':
        return {
          code:          this.form.code?.toUpperCase(),
          libelle:       this.form.libelle,
          tauxChange:    this.form.estDeviseBase ? 1.0 : this.form.tauxChange,
          estDeviseBase: this.form.estDeviseBase
        };
      case 'modeles':
        return {
          code:                   this.form.code,
          libelle:                this.form.libelle,
          typeGroupement:         this.form.typeGroupement,
          partenaireObligatoire:  this.form.typeGroupement === 'GROUPEMENT'
        };
      case 'clients':
        return {
          code: this.form.code, nom: this.form.nom,
          raisonSociale: this.form.raisonSociale,
          adresse: this.form.adresse, telephone: this.form.telephone,
          email: this.form.email, matriculeFiscale: this.form.matriculeFiscale,
          registreCommerce: this.form.registreCommerce,
          secteurActivite: this.form.secteurActivite, pays: this.form.pays
        };
      case 'bailleurs':
        return {
          code: this.form.code, nom: this.form.nom,
          adresse: this.form.adresse, telephone: this.form.telephone,
          email: this.form.email, typeFinancement: this.form.typeFinancement,
          pays: this.form.pays
        };
      case 'partenaires':
        return {
          code: this.form.code, nom: this.form.nom,
          raisonSociale: this.form.raisonSociale,
          adresse: this.form.adresse, telephone: this.form.telephone,
          email: this.form.email, pays: this.form.pays,
          partGroupement: this.form.partGroupement
        };
      default:
        return this.form;
    }
  }

  // ✅ Validation selon le type
  get formValide(): boolean {
    switch (this.activeTab) {
      case 'devises':
        return !!this.form.code && !!this.form.libelle &&
               (this.form.estDeviseBase || !!this.form.tauxChange);
      case 'modeles':
        return !!this.form.code && !!this.form.libelle && !!this.form.typeGroupement;
      default:
        return !!this.form.code && !!this.form.nom;
    }
  }

  // ✅ Helpers devise
  onDeviseBaseChange() {
    if (this.form.estDeviseBase) {
      this.form.tauxChange = 1.0;
    } else {
      this.form.tauxChange = null;
    }
  }

  get tauxInverseDT(): string {
    if (!this.form.tauxChange || this.form.tauxChange === 0) return '—';
    return (1 / this.form.tauxChange).toFixed(4);
  }

  // ✅ Helper modele
  onTypeGroupementChange() {
    this.form.partenaireObligatoire = this.form.typeGroupement === 'GROUPEMENT';
  }

  // ✅ Affichage résumé selon type
  getItemSummary(item: NomenclatureItem): string {
    switch (this.activeTab) {
      case 'devises':
        return item.estDeviseBase
          ? 'Devise de base (1.0)'
          : `1 DT = ${item.tauxChange} ${item.code}`;
      case 'modeles':
        return item.typeGroupement === 'GROUPEMENT'
          ? '🤝 En groupement (partenaire requis)'
          : '👤 Seul';
      case 'clients':
        return [item.secteurActivite, item.pays].filter(Boolean).join(' · ');
      case 'bailleurs':
        return [item.typeFinancement, item.pays].filter(Boolean).join(' · ');
      case 'partenaires':
        return item.partGroupement
          ? `${item.partGroupement}% de participation`
          : item.pays || '';
      default:
        return '';
    }
  }

  openDelete(item: NomenclatureItem) {
    this.deleteTarget = item;
    this.showDeleteModal = true;
  }

  submitDelete() {
    this.nomenclatureService.delete(this.activeTab, this.deleteTarget!.id!).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.loadItems();
        this.showToast('Supprimé ✅');
      },
      error: () => this.showToast('Erreur suppression', 'error')
    });
  }

  showToast(message: string, type = 'success') {
    this.toast = { visible: true, message, type };
    setTimeout(() => this.toast.visible = false, 3000);
  }
}