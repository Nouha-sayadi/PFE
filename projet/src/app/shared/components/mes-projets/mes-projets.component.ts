import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Affectation, AffectationService } from 'app/services/affectation.service';
import { CoutPrev, CoutPrevService } from 'app/services/cout-prev.service';
import { UserService } from 'app/services/user.service';

@Component({
  selector: 'app-mes-projets',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './mes-projets.component.html',
  styleUrl: './mes-projets.component.css',
})
export class MesProjetsComponent implements OnInit {

  affectations: Affectation[] = [];
  loading = true;
  searchQuery = '';
  userPermissions: string[] = [];
  currentUserId: number | null = null;
  chargeParProjet: Map<number, CoutPrev[]> = new Map();

  constructor(
    private affectationService: AffectationService,
    private coutPrevService: CoutPrevService,
    private userService: UserService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserId = user.id ?? null;
        this.loadAffectations();
      },
      error: () => this.loadAffectations()
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

  loadAffectations() {
    this.affectationService.getMesProjets().subscribe({
      next: (data) => {
        this.affectations = data;
        this.loading = false;
        data.forEach(a => {
          if (a.projet?.id && this.currentUserId !== null) {
            this.coutPrevService
              .getByProjetAndUser(a.projet.id, this.currentUserId)
              .subscribe(charges => {
                this.chargeParProjet.set(a.projet!.id!, charges);
              });
          }
        });
      },
      error: () => { this.loading = false; }
    });
  }

  getChargeProjet(projetId: number | undefined): CoutPrev[] {
    if (!projetId) return [];
    return this.chargeParProjet.get(projetId) || [];
  }

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

  goToCharge(projetId: number) {
    this.router.navigate(['/mes-charges', projetId]);
  }

  goToDetail(projetId: number) {
    this.router.navigate(['/projets', projetId, 'detail']);
  }

  canViewDetail(): boolean {
    return this.userPermissions.includes('GET_PROJET');
  }

  get filtered() {
    const q = this.searchQuery.toLowerCase();
    return this.affectations.filter(a =>
      a.projet?.titre?.toLowerCase().includes(q)
    );
  }

  get enCours() {
    return this.affectations.filter(a => a.projet?.statut === 'EN_COURS').length;
  }

  get termines() {
    return this.affectations.filter(a => a.projet?.statut === 'TERMINE').length;
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
      'A_DEMARRER': 'À démarrer', 'EN_COURS': 'En cours',
      'TERMINE': 'Terminé', 'EN_ATTENTE': 'En attente',
      'ANNULE': 'Annulé', 'PLANIFIE': 'Planifié'
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