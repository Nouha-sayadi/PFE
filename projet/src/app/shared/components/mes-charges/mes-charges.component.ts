import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CoutPrev, CoutPrevService } from 'app/services/cout-prev.service';
import { Pointage, PointageService } from 'app/services/pointage.service';
import { ProjetService, Projet } from 'app/services/projet.service';
import { UserService } from 'app/services/user.service';
import { FindByMoisPrevPipe, FindByMoisReelPipe } from 'app/shared/pipe/find-by-mois.pipe';
import { SumChargePipe } from 'app/shared/pipe/SumChargePipe';
import { SumCoutPipe } from 'app/shared/pipe/SumCoutPipe';

@Component({
  selector: 'app-mes-charges',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SumChargePipe, SumCoutPipe, FindByMoisPrevPipe, FindByMoisReelPipe],
  templateUrl: './mes-charges.component.html',
  styleUrl: './mes-charges.component.css'
})
export class MesChargesComponent implements OnInit {

  projetId!: number;
  projet: Projet | null = null;
  currentUserId: number | null = null;

  // ── Charge prévue ──
  charges: CoutPrev[] = [];

  // ── Pointage réel ──
  pointages: Pointage[] = [];

  // ── Modal ──
  showPointageModal = false;
  moisPointage: { mois: string; label: string; joursReel: number | null; commentaire: string }[] = [];

  loading = true;
  toast = { visible: false, message: '', type: 'success' };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projetService: ProjetService,
    private coutPrevService: CoutPrevService,
    private pointageService: PointageService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.projetId = Number(this.route.snapshot.paramMap.get('id'));
    this.projetService.getById(this.projetId).subscribe(p => this.projet = p);

    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserId = user.id ?? null;
        this.loadCharges();
        this.loadPointages();
      },
      error: () => {
        this.loadCharges();
        this.loadPointages();
      }
    });
  }

  loadCharges() {
    if (this.currentUserId !== null) {
      this.coutPrevService
        .getByProjetAndUser(this.projetId, this.currentUserId)
        .subscribe({
          next: (data) => { this.charges = data; this.loading = false; },
          error: () => this.loading = false
        });
    }
  }

  loadPointages() {
    this.pointageService.getMesPointages(this.projetId)
      .subscribe(data => this.pointages = data);
  }

  // ── Totaux charge prévue ──
  get totalJoursPrevus(): number {
    return this.charges.reduce((s, c) => s + (c.chargePrevuM || 0), 0);
  }

  get totalCoutPrev(): number {
    return this.charges.reduce((s, c) => s + (c.coutPrev || 0), 0);
  }

  // ── Totaux pointage réel ──
  get totalJoursReels(): number {
    return this.pointages.reduce((s, p) => s + (p.nbrJoursReel || 0), 0);
  }

  get totalCoutReel(): number {
    return this.pointages.reduce((s, p) => s + (p.coutReel || 0), 0);
  }

  // ── Écart ──
  get ecart(): number {
    return this.totalJoursReels - this.totalJoursPrevus;
  }

  // ── Helpers ──
  couleurCharge(charge: number | null | undefined): string {
    if (!charge || charge === 0) return '#f3f4f6';
    if (charge >= 15) return '#185FA5';
    if (charge >= 8)  return '#3b82f6';
    if (charge >= 3)  return '#93c5fd';
    return '#dbeafe';
  }

  hauteurCharge(charge: number | null | undefined): number {
    if (!charge) return 0;
    return Math.min((charge / 22) * 100, 100);
  }

  getPointageMois(mois: string): Pointage | undefined {
    return this.pointages.find(p => p.mois === mois);
  }

  getChargePrevMois(mois: string): number {
    return this.charges.find(c => c.mois === mois)?.chargePrevuM || 0;
  }

  getPointageReelMois(mois: string): number {
    return this.pointages.find(p => p.mois === mois)?.nbrJoursReel || 0;
  }

  // ── Union tous les mois ──
  get tousLesMois(): string[] {
    const set = new Set<string>();
    this.charges.forEach(c => { if (c.mois) set.add(c.mois); });
    this.pointages.forEach(p => { if (p.mois) set.add(p.mois); });
    return Array.from(set).sort();
  }

  // ── Modal pointage ──
  openPointageModal() {
    this.genererMoisPointage();
    this.showPointageModal = true;
  }

  genererMoisPointage() {
    const debut = this.projet?.dateDemarrage || this.projet?.dateDemarrageEffective;
    const fin   = this.projet?.dateFinPrevu;
    if (!debut || !fin) return;

    const now = new Date();
    // Borne max : le 1er jour du mois actuel — impossible de pointer des mois futurs
    const moisActuelMax = new Date(now.getFullYear(), now.getMonth(), 1);
    const finProjet = new Date(new Date(fin).getFullYear(), new Date(fin).getMonth(), 1);
    // On prend le minimum entre la fin du projet et le mois en cours
    const finMois = finProjet < moisActuelMax ? finProjet : moisActuelMax;

    const mois: { mois: string; label: string; joursReel: number | null; commentaire: string }[] = [];
    const current = new Date(new Date(debut).getFullYear(), new Date(debut).getMonth(), 1);

    while (current <= finMois) {
      const yyyy    = current.getFullYear();
      const mm      = String(current.getMonth() + 1).padStart(2, '0');
      const label   = current.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
      const moisStr = `${yyyy}-${mm}`;
      const existing = this.pointages.find(p => p.mois === moisStr);

      mois.push({
        mois: moisStr,
        label,
        joursReel: existing ? (existing.nbrJoursReel ?? null) : null,
        commentaire: existing?.commentaire || ''
      });
      current.setMonth(current.getMonth() + 1);
    }
    this.moisPointage = mois;
  }

  getTotalMoisPointage(): number {
    return this.moisPointage.reduce((s, m) => s + (m.joursReel || 0), 0);
  }

  submitPointage() {
    const lignes = this.moisPointage.filter(m => m.joursReel !== null && m.joursReel >= 0);
    if (lignes.length === 0) {
      this.showToast('Saisissez au moins un mois avec un nombre de jours valide', 'error');
      return;
    }
    if (lignes.some(m => (m.joursReel as number) > 31)) {
      this.showToast('Le nombre de jours ne peut pas dépasser 31', 'error');
      return;
    }

    const payloads = lignes.map(m => ({
      projetId:     this.projetId,
      mois:         m.mois,
      nbrJoursReel: m.joursReel,
      commentaire:  m.commentaire
    }));

    this.pointageService.pointerBatch(payloads).subscribe({
      next: () => {
        this.showPointageModal = false;
        this.loadPointages();
        this.showToast('Pointage enregistré ✅');
      },
      error: () => this.showToast('Erreur', 'error')
    });
  }

  showToast(message: string, type = 'success') {
    this.toast = { visible: true, message, type };
    setTimeout(() => this.toast.visible = false, 3000);
  }

  goBack() {
    window.history.back();
  }
}