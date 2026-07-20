import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Projet, ProjetService } from 'app/services/projet.service';
import { Estimation, EstimationService } from 'app/services/estimation.service';
import { CoutReel, CoutReelService } from 'app/services/cout-reel.service';
import { CoutPrev, CoutPrevService, Comparaison } from 'app/services/cout-prev.service';
import { TCC, TCCService } from 'app/services/tcc.service';
import { UserService } from 'app/services/user.service';
import { NomenclatureService } from 'app/services/nomenclature.service';
import { HttpClient } from '@angular/common/http';
import { Contrat, ContratService } from 'app/services/contrat.service';
import { Echeance, EcheanceService } from 'app/services/echeance.service';
import { KpiMensuel, KpiService } from 'app/services/kpi.service';
import { Livrable, LivrableService } from 'app/services/livrable.service';
import { AvenantService, Avenant } from 'app/services/avenant.service';
import { RisqueService, Risque } from 'app/services/risque.service';
import { ActionService, ActionProjet } from 'app/services/action.service';
import { forkJoin } from 'rxjs';
import { IaService } from 'app/services/ia.service';


@Component({
  selector: 'app-projet-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './projet-detail.component.html',
  styleUrls: ['./projet-detail.component.css']
})
export class ProjetDetailComponent implements OnInit {

  projetId!: number;
  projet: Projet | null = null;
  activeTab = 'infos';

  estimations: Estimation[] = [];
  coutsReels: CoutReel[] = [];
  coutsPrevs: CoutPrev[] = [];
  comparaison: Comparaison | null = null;
  contrats: Contrat[] = [];
echeances: Echeance[] = [];
kpis: KpiMensuel[] = [];
  allUsers: any[] = [];
  membresAffectes: any[] = [];
  affectations: any[] = []; 
risques: Risque[] = [];
actions: ActionProjet[] = [];
showRisqueModal = false;
showActionModal = false;
risqueForm: any = {};
actionForm: any = {};
editingRisqueId: number | null = null;
editingActionId: number | null = null;
activeRisqueTab: 'risques' | 'actions' = 'risques';



  tccParUser: Map<number, TCC> = new Map();

  showContratModal = false;
showEcheanceModal = false;
showKpiModal = false;
contratForm: any = {};
echeanceForm: any = {};
kpiForm: any = {};
editingContratId: number | null = null;
contratNouveauxLivrables: { numero: number; designation: string; phase: string; dateLivraisonPrevue: string | null }[] = [];
editingEcheanceId: number | null = null;
editingKpiId: number | null = null;
kpiCalculLoading = false;


livrables: Livrable[] = [];
deliveryGlobal = 0;
showLivrableModal = false;
livrableForm: any = {};
editingLivrableId: number | null = null;

  
  showCoutPrevModal = false;
  showDeleteModal = false;
  deleteTarget: { type: string; id: number } | null = null;

  
  coutPrevForm: any = {};
  editingId: number | null = null;
  

  moisProjet: { mois: string; label: string; charge: number | null }[] = [];
  selectedRessourceId: number | null = null;

  avenants: Avenant[] = [];
showAvenantModal = false;
avenantForm: any = {};
editingAvenantId: number | null = null;


synthese = '';
loadingIa = false;  

  toast = { visible: false, message: '', type: 'success' };

  constructor(
    private route: ActivatedRoute,
    private projetService: ProjetService,
    private estimationService: EstimationService,
    private coutReelService: CoutReelService,
    private coutPrevService: CoutPrevService,
    private tccService: TCCService,
    private userService: UserService,
    private http: HttpClient,
    private contratService: ContratService,
  private echeanceService: EcheanceService,
  private kpiService: KpiService,
  private livrableService: LivrableService,
    private nomenclatureService: NomenclatureService,
    private avenantService: AvenantService,
    private risqueService: RisqueService,
  private actionService: ActionService,
  private iaService: IaService
  ) {}

  ngOnInit() {
    this.projetId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadAll();
    this.loadUsers();
    this.loadMembresAffectes();
    this.loadContrats();
    this.loadEcheances();
    this.loadKpis();
    this.loadLivrables();
     this.loadAvenants();
     this.loadRisques();
  this.loadActions();
  }

  loadRisques() {
  this.risqueService.getByProjet(this.projetId)
    .subscribe(data => this.risques = data);
}

loadActions() {
  this.actionService.getByProjet(this.projetId)
    .subscribe(data => this.actions = data);
}

  loadAvenants() {
  this.avenantService.getByProjet(this.projetId)
    .subscribe(data => this.avenants = data);
}

  loadLivrables() {
  this.livrableService.getByProjet(this.projetId).subscribe(data => {
    this.livrables = data;
    // Mettre à jour delivery global
    if (data.length > 0) {
      this.deliveryGlobal = data[0].deliveryGlobal || 0;
    }
  });
}

  loadContrats() {
  this.contratService.getByProjet(this.projetId).subscribe(d => this.contrats = d);
}
loadEcheances() {
  this.echeanceService.getByProjet(this.projetId).subscribe(d => this.echeances = d);
}
loadKpis() {
  this.kpiService.getByProjet(this.projetId).subscribe(d => this.kpis = d);
}

get totalMontantPrevu(): number {
  return this.echeances.reduce((s, e) => s + (e.montantPrevu || 0), 0);
}
get totalMontantFacture(): number {
  return this.echeances.reduce((s, e) => s + (e.montantFacture || 0), 0);
}

// ── Dérive ──
getDerive(e: Echeance): number {
  if (!e.dateInitiale || !e.dateReelle) return 0;
  const d1 = new Date(e.dateInitiale);
  const d2 = new Date(e.dateReelle);
  return Math.round((d2.getTime() - d1.getTime()) / (1000*60*60*24));
}

// ── CONTRAT ──
openContratCreate() {
  this.contratForm = { projetId: this.projetId, numeroContrat: '', intitule: '',
    montantTotal: null, dateSignature: null, dateEcheance: null, conditionsPaiement: '' };
  this.editingContratId = null;
  this.contratNouveauxLivrables = [];
  this.showContratModal = true;
}
openContratEdit(c: Contrat) {
  this.contratForm = { ...c, projetId: this.projetId };
  this.editingContratId = c.id!;
  this.contratNouveauxLivrables = [];
  this.showContratModal = true;
}
addContratLivrable() {
  this.contratNouveauxLivrables.push({
    numero: this.livrables.length + this.contratNouveauxLivrables.length + 1,
    designation: '',
    phase: '',
    dateLivraisonPrevue: null
  });
}
removeContratLivrable(i: number) {
  this.contratNouveauxLivrables.splice(i, 1);
}
submitContrat() {
  const obs = this.editingContratId
    ? this.contratService.update(this.editingContratId, this.contratForm)
    : this.contratService.create(this.contratForm);
  obs.subscribe({
    next: () => {
      const valides = this.contratNouveauxLivrables.filter(l => l.designation?.trim());
      if (!this.editingContratId && valides.length > 0) {
        forkJoin(valides.map(l => this.livrableService.create({ ...l, projetId: this.projetId }))).subscribe({
          next: () => { this.showContratModal = false; this.loadContrats(); this.loadLivrables(); this.showToast('Contrat et livrables enregistrés'); },
          error: () => { this.showContratModal = false; this.loadContrats(); this.loadLivrables(); this.showToast('Contrat créé, erreur livrables', 'error'); }
        });
      } else {
        this.showContratModal = false; this.loadContrats(); this.loadLivrables(); this.showToast('Contrat enregistré');
      }
    },
    error: (err: any) => this.showToast(err?.error?.message || 'Erreur', 'error')
  });
}
deleteContrat(id: number) {
  if (confirm('Supprimer ce contrat ?')) {
    this.contratService.delete(id).subscribe(() => { this.loadContrats(); this.showToast('Supprimé'); });
  }
}

// ── ECHEANCE ──
openEcheanceCreate() {
  this.echeanceForm = { projetId: this.projetId, numero: null, objet: '',
    pourcentage: null, montantPrevu: null, montantFacture: null,
    dateInitiale: null, datePrevActualisee: null, dateReelle: null,
    emise: false, commentaire: '' };
  this.editingEcheanceId = null;
  this.showEcheanceModal = true;
}
openEcheanceEdit(e: Echeance) {
  this.echeanceForm = { ...e, projetId: this.projetId };
  this.editingEcheanceId = e.id!;
  this.showEcheanceModal = true;
}
submitEcheance() {
  const obs = this.editingEcheanceId
    ? this.echeanceService.update(this.editingEcheanceId, this.echeanceForm)
    : this.echeanceService.create(this.echeanceForm);
  obs.subscribe({
    next: () => { this.showEcheanceModal = false; this.loadEcheances(); this.loadKpis(); this.showToast('Échéance enregistrée'); },
    error: () => this.showToast('Erreur', 'error')
  });
}
deleteEcheance(id: number) {
  if (confirm('Supprimer cette échéance ?')) {
    this.echeanceService.delete(id).subscribe(() => { this.loadEcheances(); this.showToast('Supprimé'); });
  }
}

// ── KPI ──
openKpiCreate() {
  const today = new Date();
  const mois = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}`;
  
  this.kpiForm = { projetId: this.projetId, mois, earnedValue: null, faitsMarquants: '' };
  this.editingKpiId = null;
  this.kpiCalculLoading = true;
  this.showKpiModal = true;

  this.kpiService.calculer(this.projetId, mois).subscribe({
    next: (res: any) => {
      console.log('✅ KPI calculé:', res);
      this.kpiForm = {
        ...res,
        projetId: this.projetId, 
        mois: res.mois || mois,   
        earnedValue: null,
        faitsMarquants: ''
      };
      this.kpiCalculLoading = false;
    },
    error: (err: any) => {
      console.error('❌ Erreur calcul KPI:', err);
      // ✅ Garder le form avec le mois initial même en cas d'erreur
      this.kpiCalculLoading = false;
    }
  });
}
onKpiMoisChange() {
  if (!this.kpiForm.mois) return;
  this.kpiCalculLoading = true;
  const ev = this.kpiForm.earnedValue;
  const fm = this.kpiForm.faitsMarquants;
  this.kpiService.calculer(this.projetId, this.kpiForm.mois).subscribe({
    next: (data) => {
      this.kpiForm = { ...data, earnedValue: ev, faitsMarquants: fm };
      this.kpiCalculLoading = false;
      if (ev) this.onEVChange();
    },
    error: () => { this.kpiCalculLoading = false; }
  });
}

// ✅ Quand EV change → recalculer CA + Marges
onEVChange() {
  if (!this.kpiForm.earnedValue || !this.kpiForm.mois) return;
  
  let ev = Number(this.kpiForm.earnedValue);
  if (ev > 1) {
    ev = ev / 100; // ✅ Convertir si saisi en pourcentage
    this.kpiForm.earnedValue = ev;
  }
  
  this.kpiService.calculerAvecEV(
    this.projetId,
    this.kpiForm.mois,
    ev
  ).subscribe({
    next: (data) => {
      const fm = this.kpiForm.faitsMarquants;
      this.kpiForm = { ...data, earnedValue: ev, faitsMarquants: fm };
    }
  });
}

openKpiEdit(k: KpiMensuel) {
  const moisStr = k.mois ? new Date(k.mois).toISOString().substring(0,7) : '';
  this.kpiForm = { ...k, projetId: this.projetId, mois: moisStr };
  this.editingKpiId = k.id!;
  this.showKpiModal = true;
}
submitKpi() {if (!this.kpiForm.mois) {
    this.showToast('Le mois est obligatoire', 'error');
    return;
  }

  const payload = {
    ...this.kpiForm,
    projetId:            this.projetId,
    mois:                this.kpiForm.mois, // ✅ S'assurer que mois est présent
    chargePrevueMois:    this.kpiForm.chargePrevueMois    ?? 0,
    chargeConsommeeMois: this.kpiForm.chargeConsommeeMois ?? 0,
    chargeConsommeeCumul:this.kpiForm.chargeConsommeeCumul ?? 0,
    resteAFaireETC:      this.kpiForm.resteAFaireETC      ?? 0,
    resteAFaireEAC:      this.kpiForm.resteAFaireEAC      ?? 0,
    derive:              this.kpiForm.derive              ?? 0,
    coutReel:            this.kpiForm.coutReel            ?? 0,
    coutReelCumul:       this.kpiForm.coutReelCumul       ?? 0,
    coutETC:             this.kpiForm.coutETC             ?? 0,
    coutEAC:             this.kpiForm.coutEAC             ?? 0,
    caProduction:        this.kpiForm.caProduction        ?? 0,
    caFacture:           this.kpiForm.caFacture           ?? 0,
    delivery:            this.kpiForm.delivery            ?? 0,
    consommationDelais:  this.kpiForm.consommationDelais  ?? 0,
    tauxFacturation:     this.kpiForm.tauxFacturation     ?? 0,
    margeNetteEAC:       this.kpiForm.margeNetteEAC       ?? 0,
    margeNetteEACPct:    this.kpiForm.margeNetteEACPct    ?? 0,
    margeNetteActuelle:  this.kpiForm.margeNetteActuelle  ?? 0,
    stock:               this.kpiForm.stock               ?? 0,
    earnedValue:         this.kpiForm.earnedValue         ?? 0,
  };

  console.log('📤 Payload KPI:', payload);

  this.kpiService.save(payload).subscribe({
    next: () => {
      this.showKpiModal = false;
      this.loadKpis();
      this.showToast('KPI enregistré ✅');
    },
    error: (err: any) => {
      console.error('❌ Erreur KPI:', err);
      this.showToast('Erreur', 'error');
    }
  });
}
deleteKpi(id: number) {
  if (confirm('Supprimer ce KPI ?')) {
    this.kpiService.delete(id).subscribe(() => { this.loadKpis(); this.showToast('Supprimé'); });
  }
}

  

 loadMembresAffectes() {
  this.http.get<any[]>(`http://localhost:8080/api/affectations/projet/${this.projetId}`)
    .subscribe(affectations => {
      this.membresAffectes = affectations.map(a => ({
        id:     a.utilisateur?.id,
        nom:    a.utilisateur?.nom,
        prenom: a.utilisateur?.prenom
      })).filter(u => u.id != null);
    });
}

  loadAll() {
    this.projetService.getById(this.projetId).subscribe(p => this.projet = p);
    this.loadEstimations();
    this.loadCoutsReels();
    this.loadCoutsPrevs();
    this.loadComparaison();
    
  }

  loadUsers() {
    this.userService.getAll().subscribe(users => {
      this.allUsers = users;
      users.forEach(u => {
        if (!u.id) return;
        this.tccService.getByUser(u.id).subscribe(tccs => {
          if (tccs.length > 0) {
            const latest = tccs.sort((a, b) =>
              new Date(b.annee || '').getTime() - new Date(a.annee || '').getTime()
            )[0];
            this.tccParUser.set(u.id!, latest);
          }
        });
      });
      this.refreshMembresAffectes();
    });
  }

  loadEstimations() {
    this.estimationService.getByProjet(this.projetId).subscribe(data => {
          console.log('estimations reçues:', data); // ← vérifier ici

      this.estimations = data;
      this.refreshMembresAffectes();
    });
  }

  
  loadCoutsReels() {
  this.coutReelService.getByProjet(this.projetId).subscribe(data => {
    this.coutsReels = data;
    this.buildGroupesReels();
    this.loadComparaison();
  });
}

  loadCoutsPrevs() {
  this.coutPrevService.getByProjet(this.projetId).subscribe(data => {
    this.coutsPrevs = data;
    this.buildGroupes();
    this.selectedGroupIndex = 0;
    this.loadComparaison();
  });
}


  loadComparaison() {
  const prev  = this.totalCoutPrev;
  const reel  = this.totalCoutReel;
  const ecart = reel - prev;

  this.comparaison = {
    totalPrevisionnel: prev,
    totalReel:         reel,
    ecart:             ecart,
    ecartPourcentage:  prev > 0 ? Math.round((ecart / prev) * 1000) / 10 : 0,
    statut:            ecart > 0 ? 'DEPASSEMENT' : 'OK'
  } as Comparaison;
}

  refreshMembresAffectes() {}

  setTab(tab: string) { this.activeTab = tab; }

  get membresDisponiblesEstimation(): any[] {
    const dejaDans = new Set(this.estimations.map(e => e.ressource?.id));
    return this.membresAffectes.filter(m => !dejaDans.has(m.id));
  }

  get membresDisponiblesCoutPrev(): any[] {
    const dejaDans = new Set(this.coutsPrevs.map(c => c.ressource?.id));
    return this.membresAffectes.filter(m => !dejaDans.has(m.id));
  }

  // ── TOTAUX ──
  get totalEstimation(): number {
    return this.estimations.reduce((s, e) => s + (e.coutEstime || 0), 0);
  }

  get totalCoutReel(): number {
    return this.coutsReels.reduce((s, c) => s + (c.coutReel || 0), 0);
  }

  get totalCoutPrev(): number {
    return this.coutsPrevs.reduce((s, c) => s + (c.coutPrev || 0), 0);
  }

  // ── TCC Helpers ──
  getTarifHJ(userId: number | undefined): number {
  if (!userId) return 0;
  const tcc = this.tccParUser.get(Number(userId));
  if (!tcc) return 0;
  
  if (tcc.tarifHJ) return tcc.tarifHJ;
  
  const tccBase   = tcc.tccBase   || 0;
  const tccAvecFG = tcc.tccAvecFG || 0;
  return (tccBase + tccAvecFG) / 264;
}

  getTccAvecFG(userId: number | undefined): number {
    if (!userId) return 0;
    return this.tccParUser.get(userId)?.tccAvecFG || 0;
  }

  getInitiales(ressource: any): string {
    return ((ressource?.prenom?.[0] || '') + (ressource?.nom?.[0] || '')).toUpperCase();
  }

  getNbrJoursPrevus(): number {
    if (this.projet?.dateDemarrageEffective && this.projet?.dateFinPrevu) {
      const d1 = new Date(this.projet.dateDemarrageEffective);
      const d2 = new Date(this.projet.dateFinPrevu);
      return Math.ceil((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
    if (this.projet?.dateDemarrage && this.projet?.dateFinPrevu) {
      const d1 = new Date(this.projet.dateDemarrage);
      const d2 = new Date(this.projet.dateFinPrevu);
      return Math.ceil((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
    return 0;
  }

  // ── Modal Estimation ──
showEstimationModal = false;
estimationForm: any = {};
editingEstimationId: number | null = null;

openEstimationCreate() {
  this.estimationForm = { ressourceId: null, nbrJours: null };
  this.editingEstimationId = null;
  this.showEstimationModal = true;
}

openEstimationEdit(e: Estimation) {
  this.estimationForm = {
    ressourceId: e.ressource?.id,
    nom: e.ressource?.nom,
    prenom: e.ressource?.prenom,
    nbrJours: e.nbrJours,
    tarifHJ: e.tarifHJ
  };
  this.editingEstimationId = e.id!;
  this.showEstimationModal = true;
}



submitEstimation() {
  if (!this.estimationForm.nbrJours || this.estimationForm.nbrJours <= 0) {
    this.showToast('Saisissez le nombre de jours estimés', 'error');
    return;
  }
  if (this.editingEstimationId !== null) {
    const payload = {
      nbrJours: Number(this.estimationForm.nbrJours),
      tarifHJ:  Number(this.estimationForm.tarifHJ)
    };
    this.estimationService.update(this.editingEstimationId, payload).subscribe({
      next: () => {
        this.showEstimationModal = false;
        this.editingEstimationId = null;
        this.loadEstimations();
        this.showToast('Estimation mise à jour');
      },
      error: () => this.showToast('Erreur', 'error')
    });
  } else {
    const payload = {
      projetId:    this.projetId,
      ressourceId: Number(this.estimationForm.ressourceId),
      nbrJours:    Number(this.estimationForm.nbrJours)
    };
    this.estimationService.create(payload).subscribe({
      next: () => {
        this.showEstimationModal = false;
        this.loadEstimations();
        this.showToast('Estimation créée');
      },
      error: () => this.showToast('Erreur', 'error')
    });
  }
}
getCoutEstimePreview(): number {
  if (this.editingEstimationId !== null) {
    if (!this.estimationForm.nbrJours || !this.estimationForm.tarifHJ) return 0;
    return Number(this.estimationForm.nbrJours) * Number(this.estimationForm.tarifHJ);
  }
  if (!this.estimationForm.ressourceId || !this.estimationForm.nbrJours) return 0;
  const userId = Number(this.estimationForm.ressourceId);
  const tcc = this.tccParUser.get(userId);
  if (!tcc) return 0;
  const tccBase   = tcc.tccBase   || 0;
  const tccAvecFG = tcc.tccAvecFG || 0;
  const tarifHJ   = (tccBase + tccAvecFG) / 264;
  return tarifHJ * this.estimationForm.nbrJours;
}



  // ── Groupes CoutReel ──
groupesCoutsReels: { ressource: any; lignes: CoutReel[]; totalJours: number; totalCout: number }[] = [];
selectedGroupReelIndex = 0;

get selectedGroupReel() {
  return this.groupesCoutsReels[this.selectedGroupReelIndex] || null;
}

buildGroupesReels(): void {
  const map = new Map<number, { ressource: any; lignes: CoutReel[]; totalJours: number; totalCout: number }>();
  this.coutsReels.forEach(c => {
    const id = c.ressource?.id;
    if (!id) return;
    if (!map.has(id)) {
      map.set(id, { ressource: c.ressource, lignes: [], totalJours: 0, totalCout: 0 });
    }
    const group = map.get(id)!;
    group.lignes.push(c);
    group.totalJours += c.nbrJoursReel || 0;
    group.totalCout  += c.coutReel    || 0;
  });
  this.groupesCoutsReels = Array.from(map.values());
}



// Recalculer depuis les pointages
recalculerCoutsReels() {
  this.coutReelService.recalculer(this.projetId).subscribe({
    next: () => {
      this.loadCoutsReels();
      this.showToast('Coûts réels recalculés depuis les pointages ✅');
    },
    error: () => this.showToast('Erreur recalcul', 'error')
  });
}

  // ── COUT PREV ──
  get coutsPrevGroupes(): { ressource: any; lignes: CoutPrev[]; totalCharge: number; totalCout: number }[] {
    const map = new Map<number, { ressource: any; lignes: CoutPrev[]; totalCharge: number; totalCout: number }>();
    this.coutsPrevs.forEach(c => {
      const id = c.ressource?.id;
      if (!id) return;
      if (!map.has(id)) {
        map.set(id, { ressource: c.ressource, lignes: [], totalCharge: 0, totalCout: 0 });
      }
      const group = map.get(id)!;
      group.lignes.push(c);
      group.totalCharge += c.chargePrevuM || 0;
      group.totalCout   += c.coutPrev    || 0;
    });
    return Array.from(map.values());
  }

  // Groupe sélectionné

 
groupesCoutsPrev: { ressource: any; lignes: CoutPrev[]; totalCharge: number; totalCout: number }[] = [];
selectedGroupIndex = 0;

 get selectedGroup() {
  if (this.groupesCoutsPrev.length === 0) return null;
  return this.groupesCoutsPrev[this.selectedGroupIndex] || this.groupesCoutsPrev[0];
}
buildGroupes(): void {
  const map = new Map<number, { ressource: any; lignes: CoutPrev[]; totalCharge: number; totalCout: number }>();
  this.coutsPrevs.forEach(c => {
    const id = c.ressource?.id;
    if (!id) return;
    if (!map.has(id)) {
      map.set(id, { ressource: c.ressource, lignes: [], totalCharge: 0, totalCout: 0 });
    }
    const group = map.get(id)!;
    group.lignes.push(c);
    group.totalCharge += c.chargePrevuM || 0;
    group.totalCout   += c.coutPrev    || 0;
  });
  this.groupesCoutsPrev = Array.from(map.values());
}
selectGroup(i: number): void {
  this.selectedGroupIndex = i;
}
 

  genererMoisProjet(): void {
    const debut = this.projet?.dateDemarrage || this.projet?.dateDemarrageEffective;
    const fin   = this.projet?.dateFinPrevu;
    if (!debut || !fin) return;

    const mois: { mois: string; label: string; charge: number | null }[] = [];
    const current = new Date(new Date(debut).getFullYear(), new Date(debut).getMonth(), 1);
    const finMois  = new Date(new Date(fin).getFullYear(), new Date(fin).getMonth(), 1);

    while (current <= finMois) {
      const yyyy  = current.getFullYear();
      const mm    = String(current.getMonth() + 1).padStart(2, '0');
      const label = current.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
      mois.push({ mois: `${yyyy}-${mm}`, label, charge: null });
      current.setMonth(current.getMonth() + 1);
    }
    this.moisProjet = mois;
  }

  openCoutPrevCreate() {
    this.selectedRessourceId = null;
    this.editingId = null;
    this.genererMoisProjet();
    this.refreshMembresAffectes();
    this.showCoutPrevModal = true;
  }

  onRessourceSelectChange(): void {
    if (!this.selectedRessourceId) return;
    const existantes = this.coutsPrevs.filter(
      c => c.ressource?.id === this.selectedRessourceId
    );
    this.moisProjet.forEach(m => {
      const existing = existantes.find(c => c.mois === m.mois);
      m.charge = existing ? (existing.chargePrevuM ?? null) : null;
    });
  }

  getCoutPrevMoisPreview(charge: number | null): number {
    if (!charge || !this.selectedRessourceId) return 0;
    return charge * this.getTarifHJ(this.selectedRessourceId);
  }

  get totalChargePrevu(): number {
    return this.moisProjet.reduce((s, m) => s + (m.charge || 0), 0);
  }

  get totalCoutPrevPreview(): number {
    if (!this.selectedRessourceId) return 0;
    return this.totalChargePrevu * this.getTarifHJ(this.selectedRessourceId);
  }

  submitCoutPrevBatch() {
    if (!this.selectedRessourceId) return;
    const lignes = this.moisProjet.filter(m => m.charge && m.charge > 0);
    if (lignes.length === 0) {
      this.showToast('Saisissez au moins un mois', 'error');
      return;
    }
    const payloads = lignes.map(m => ({
      projetId:     this.projetId,
      ressourceId:  this.selectedRessourceId,
      mois:         m.mois,
      chargePrevuM: m.charge
    }));
    this.coutPrevService.createBatch(payloads).subscribe({
      next: () => {
        this.showCoutPrevModal = false;
        this.loadCoutsPrevs();
        this.loadComparaison();
        this.showToast(`${lignes.length} coût(s) prévisionnel(s) créés ✅`);
      },
      error: () => this.showToast('Erreur', 'error')
    });
  }

  openCoutPrevEdit(c: CoutPrev) {
    this.coutPrevForm = {
      ressourceId:  c.ressource?.id,
      mois:         c.mois,
      chargePrevuM: c.chargePrevuM
    };
    this.editingId = c.id!;
    this.showCoutPrevModal = true;
  }

  getCoutPrevPreview(): number {
    if (!this.coutPrevForm.ressourceId || !this.coutPrevForm.chargePrevuM) return 0;
    return this.coutPrevForm.chargePrevuM * this.getTarifHJ(this.coutPrevForm.ressourceId);
  }

  submitCoutPrevEdit() {
    const payload = {
      projetId:     this.projetId,
      ressourceId:  this.coutPrevForm.ressourceId,
      mois:         this.coutPrevForm.mois,
      chargePrevuM: this.coutPrevForm.chargePrevuM
    };
    this.coutPrevService.update(this.editingId!, payload).subscribe({
      next: () => {
        this.showCoutPrevModal = false;
        this.editingId = null;
        this.loadCoutsPrevs();
        this.loadComparaison();
        this.showToast('Coût prévisionnel mis à jour');
      },
      error: () => this.showToast('Erreur', 'error')
    });
  }

  // ── DELETE ──
  openDelete(type: string, id: number) {
    this.deleteTarget = { type, id };
    this.showDeleteModal = true;
  }

  submitDelete() {
    if (!this.deleteTarget) return;
    const { type, id } = this.deleteTarget;
    const obs =
      type === 'estimation' ? this.estimationService.delete(id) :
      type === 'coutReel'   ? this.coutReelService.delete(id) :
      this.coutPrevService.delete(id);
    obs.subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.loadAll();
        this.showToast('Supprimé');
      },
      error: () => this.showToast('Erreur suppression', 'error')
    });
  }

  // ── HELPERS ──
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
      'A_DEMARRER': 'À démarrer', 'EN_COURS': 'En cours',
      'TERMINE': 'Terminé', 'EN_ATTENTE': 'En attente',
      'ANNULE': 'Annulé', 'PLANIFIE': 'Planifié'
    };
    return map[statut || ''] || statut || '';
  }

//_____Livrable _____

get totalLivrables(): number {
  return this.livrables.length;
}

get realisesCount(): number {
  return this.livrables.filter(l => l.realise).length;
}

get livresCount(): number {
  return this.livrables.filter(l => l.livre).length;
}

getEcartClass(ecart: number | null | undefined): string {
  if (ecart === null || ecart === undefined) return '';
  if (ecart > 0) return 'text-red';
  if (ecart < 0) return 'text-green';
  return 'text-muted';
}

getEcartLabel(ecart: number | null | undefined): string {
  if (ecart === null || ecart === undefined) return '—';
  if (ecart === 0) return 'Dans les délais';
  return (ecart > 0 ? '+' : '') + ecart + ' j';
}

// ── CRUD LIVRABLE ──
openLivrableCreate() {
  this.livrableForm = {
    projetId: this.projetId,
    numero: this.livrables.length + 1,
    designation: '', phase: '',
    dateLivraisonPrevue: null,
    dateLivraisonReelle: null,
    realise: false, livre: false,
    commentaire: ''
  };
  this.editingLivrableId = null;
  this.showLivrableModal = true;
}

openLivrableEdit(l: Livrable) {
  this.livrableForm = {
    projetId: this.projetId,
    numero: l.numero,
    designation: l.designation,
    phase: l.phase,
    dateLivraisonPrevue: l.dateLivraisonPrevue,
    dateLivraisonReelle: l.dateLivraisonReelle,
    realise: l.realise,
    livre: l.livre,
    commentaire: l.commentaire
  };
  this.editingLivrableId = l.id!;
  this.showLivrableModal = true;
}

submitLivrable() {
  const obs = this.editingLivrableId
    ? this.livrableService.update(this.editingLivrableId, this.livrableForm)
    : this.livrableService.create(this.livrableForm);
  obs.subscribe({
    next: () => {
      this.showLivrableModal = false;
      this.loadLivrables();
      this.showToast('Livrable enregistré ✅');
    },
    error: () => this.showToast('Erreur', 'error')
  });
}

deleteLivrable(id: number) {
  if (confirm('Supprimer ce livrable ?')) {
    this.livrableService.delete(id).subscribe(() => {
      this.loadLivrables();
      this.showToast('Supprimé');
    });
  }
}

calcEcartJours(): number {
  if (!this.livrableForm.dateLivraisonPrevue || !this.livrableForm.dateLivraisonReelle) return 0;
  const d1 = new Date(this.livrableForm.dateLivraisonPrevue);
  const d2 = new Date(this.livrableForm.dateLivraisonReelle);
  return Math.round((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
}
//AVENANT

get totalAvenants(): number { return this.avenants.length; }
get montantReviseTotal(): number {
  const last = this.avenants.find(a => a.statut === 'APPROUVE');
  return last?.montantRevise || this.projet?.montantTotal || 0;
}
get deltaTotal(): number {
  return this.avenants
    .filter(a => a.statut === 'APPROUVE')
    .reduce((s, a) => s + (a.deltaMontant || 0), 0);
}

// ── CRUD AVENANT ──
openAvenantCreate() {
  const montantActuel = this.projet?.montantTotal 
      || this.projet?.budgetInitial 
      || 0;

  this.avenantForm = {
    projetId: this.projetId,
    montantInitial: montantActuel, // ✅ Envoyer explicitement
    numero: `A0${this.avenants.length + 1}`,
    objet: '',
    montantRevise: montantActuel,
    impactDelais: 0,
    dateSignature: null,
    dateEffet: null,
    statut: 'EN_ATTENTE',
    commentaire: ''
  };
  this.editingAvenantId = null;
  this.showAvenantModal = true;
}

openAvenantEdit(a: Avenant) {
  this.avenantForm = {
    projetId: this.projetId,
    numero: a.numero,
    objet: a.objet,
    montantRevise: a.montantRevise,
    impactDelais: a.impactDelais,
    dateSignature: a.dateSignature,
    dateEffet: a.dateEffet,
    statut: a.statut,
    commentaire: a.commentaire
  };
  this.editingAvenantId = a.id!;
  this.showAvenantModal = true;
}

submitAvenant() {
  const obs = this.editingAvenantId
    ? this.avenantService.update(this.editingAvenantId, this.avenantForm)
    : this.avenantService.create(this.avenantForm);
  obs.subscribe({
    next: () => {
      this.showAvenantModal = false;
      this.loadAvenants();
      // ✅ Recharger le projet si avenant approuvé
      if (this.avenantForm.statut === 'APPROUVE') {
        this.loadAll();
      }
      this.showToast('Avenant enregistré ✅');
    },
    error: () => this.showToast('Erreur', 'error')
  });
}

deleteAvenant(id: number) {
  if (confirm('Supprimer cet avenant ?')) {
    this.avenantService.delete(id).subscribe(() => {
      this.loadAvenants();
      this.showToast('Supprimé');
    });
  }
}

// ✅ Couleur statut
getAvenantStatutClass(statut?: string): string {
  const map: Record<string, string> = {
    'EN_ATTENTE': 'badge-attente',
    'APPROUVE':   'badge-termine',
    'REJETE':     'badge-annule'
  };
  return map[statut || ''] || 'badge-attente';
}

getAvenantStatutLabel(statut?: string): string {
  const map: Record<string, string> = {
    'EN_ATTENTE': '⏳ En attente',
    'APPROUVE':   '✅ Approuvé',
    'REJETE':     '❌ Rejeté'
  };
  return map[statut || ''] || statut || '';
}



// ── RISQUE&ACTION ──

get risquesCritiques(): number {
  return this.risques.filter(r => (r.criticite || 0) >= 15).length;
}
get actionsEnRetard(): number {
  const today = new Date().toISOString().split('T')[0];
  return this.actions.filter(a =>
    a.statut !== 'TERMINE' && a.datePrevue && a.datePrevue < today
  ).length;
}

getCriticiteClass(c?: number): string {
  if (!c) return '';
  if (c >= 15) return 'text-red';
  if (c >= 8)  return 'text-orange';
  return 'text-green';
}

getCriticiteLabel(c?: number): string {
  if (!c) return '—';
  if (c >= 15) return '🔴 Critique';
  if (c >= 8)  return '🟡 Élevé';
  if (c >= 4)  return '🟠 Modéré';
  return '🟢 Faible';
}
get today(): string {
  return new Date().toISOString().split('T')[0];
}

openRisqueCreate() {
  this.risqueForm = {
    projetId: this.projetId,
    code: `R0${this.risques.length + 1}`,
    description: '', categorie: 'TECHNIQUE',
    probabilite: 3, impact: 3,
    statut: 'IDENTIFIE',
    mesuresMitigation: '',
    dateIdentification: new Date().toISOString().split('T')[0],
    dateEcheance: null
  };
  this.editingRisqueId = null;
  this.showRisqueModal = true;
}

openRisqueEdit(r: Risque) {
  this.risqueForm = { ...r, projetId: this.projetId };
  this.editingRisqueId = r.id!;
  this.showRisqueModal = true;
}

submitRisque() {
  const obs = this.editingRisqueId
    ? this.risqueService.update(this.editingRisqueId, this.risqueForm)
    : this.risqueService.create(this.risqueForm);
  obs.subscribe({
    next: () => {
      this.showRisqueModal = false;
      this.loadRisques();
      this.showToast('Risque enregistré ✅');
    },
    error: () => this.showToast('Erreur', 'error')
  });
}

deleteRisque(id: number) {
  if (confirm('Supprimer ce risque ?')) {
    this.risqueService.delete(id).subscribe(() => {
      this.loadRisques();
      this.showToast('Supprimé');
    });
  }
}

openActionCreate() {
  this.actionForm = {
    projetId: this.projetId,
    code: `A0${this.actions.length + 1}`,
    description: '', typeAction: 'CORRECTIVE',
    responsable: '', datePrevue: null,
    dateReelle: null, statut: 'A_FAIRE',
    commentaire: '', risqueId: null
  };
  this.editingActionId = null;
  this.showActionModal = true;
}

openActionEdit(a: ActionProjet) {
  this.actionForm = { ...a, projetId: this.projetId };
  this.editingActionId = a.id!;
  this.showActionModal = true;
}

submitAction() {
  const obs = this.editingActionId
    ? this.actionService.update(this.editingActionId, this.actionForm)
    : this.actionService.create(this.actionForm);
  obs.subscribe({
    next: () => {
      this.showActionModal = false;
      this.loadActions();
      this.showToast('Action enregistrée ✅');
    },
    error: () => this.showToast('Erreur', 'error')
  });
}

deleteAction(id: number) {
  if (confirm('Supprimer cette action ?')) {
    this.actionService.delete(id).subscribe(() => {
      this.loadActions();
      this.showToast('Supprimé');
    });
  }
}

getActionStatutClass(s?: string): string {
  const map: Record<string, string> = {
    'A_FAIRE':  'badge-attente',
    'EN_COURS': 'badge-encours',
    'TERMINE':  'badge-termine',
    'ANNULE':   'badge-annule'
  };
  return map[s || ''] || 'badge-attente';
}

getActionStatutLabel(s?: string): string {
  const map: Record<string, string> = {
    'A_FAIRE':  '📋 À faire',
    'EN_COURS': '🔄 En cours',
    'TERMINE':  '✅ Terminé',
    'ANNULE':   '❌ Annulé'
  };
  return map[s || ''] || s || '';
}

genererSynthese() {
  this.loadingIa = true;
  this.synthese = '';
  this.iaService.getSynthese(this.projetId).subscribe({
    next: (txt) => { this.synthese = txt; this.loadingIa = false; },
    error: () => {
      this.synthese = '';
      this.loadingIa = false;
      this.showToast('Erreur lors de la génération IA', 'error');
    }
  });
}



  showToast(message: string, type = 'success') {
    this.toast = { visible: true, message, type };
    setTimeout(() => this.toast.visible = false, 3000);
  }
}