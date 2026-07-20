import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  private readonly MB = 'http://localhost:3000';
  private sessionToken: string | null = null;
  private refreshTimer?: any;

  projets: any[] = [];
  selectedProjet: string = 'all';
  allProjets:   any[] = [];


  loading = true;

  totalProjets     = 0;
  projetsActifs    = 0;
  totalBudget      = 0;
  totalCoutReel    = 0;
  risquesCritiques = 0;

  
  readonly TOKENS = {
    evDelivery:       'daf7b6e1-c570-4d73-9cec-06f72b4de41b',
    projetsParStatut: 'f1fc5a4f-7ce5-416c-bca5-ea2256e2b524',
    caCout:           '747e9a40-aed3-46d2-a149-5c5c88e53cfa',
    chargeHj:         'e030c8af-8820-41b6-8900-89bb3551e32f',
    marge:            '07965787-6e90-47dd-bd20-748d2a0bdb6e',
    factCaCout:       '7390fe96-116f-42b1-ac52-789d5cf68bd4',
    coutsEvol:        'f67defa3-2966-47bc-84d2-1c828aa9adea',
    factEcheance:     '7ce2b493-abdc-4824-b4c5-33d0508c1f61',
  };

  iframes: { [key: string]: SafeResourceUrl } = {};

  constructor(
    private sanitizer: DomSanitizer,
    private http: HttpClient
  ) {}

  ngOnInit() {
  this.buildIframes();
    this.loadKPIs();
    this.refreshTimer = setInterval(() => this.loadKPIs(), 60000);
  }

   ngOnDestroy() {
    if (this.refreshTimer) { clearInterval(this.refreshTimer); }
  }

  // 🔎 Construit les iframes en injectant le filtre projet (?projet=<id>)
  buildIframes() {
    Object.entries(this.TOKENS).forEach(([key, token]) => {
      let url = `${this.MB}/public/question/${token}`;
      if (this.selectedProjet !== 'all') {
        url += `?projet=${this.selectedProjet}`;
      }
      url += '#titled=false&bordered=false&hide_parameters=projet';  // le hash doit rester en dernier
      this.iframes[key] = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    });
  }

 
  onProjetChange() {
    this.buildIframes();
    this.computeProjectKpis();  
  this.loadCostAndRisks();
    
  }
private readonly API = 'http://localhost:8080/api';

  loadKPIs() {
   this.http.get<any[]>(`${this.API}/projets`).subscribe({
    next: (projets) => {
      this.allProjets = projets;
      this.projets    = projets;          // menu déroulant
      this.computeProjectKpis();          // budget / projets actifs
      this.loadCostAndRisks();            // coût réel + risques (endpoints par projet)
      this.loading    = false;
    },
    error: () => { this.loading = false; }
  });
  }

  computeProjectKpis() {
  const all = this.selectedProjet === 'all';
  const projets = all
    ? this.allProjets
    : this.allProjets.filter(p => String(p.id) === String(this.selectedProjet));

  this.totalProjets  = projets.length;
  this.projetsActifs = projets.filter(p => p.statut === 'EN_COURS').length;
  this.totalBudget   = projets.reduce((s, p) => s + (p.budgetInitial || 0), 0);
}

loadCostAndRisks() {
  const ids = this.selectedProjet === 'all'
    ? this.allProjets.map(p => p.id)
    : [Number(this.selectedProjet)];

  if (ids.length === 0) {
    this.totalCoutReel = 0;
    this.risquesCritiques = 0;
    return;
  }

  const costCalls = ids.map(id =>
    this.http.get<number>(`${this.API}/cout-reels/projet/${id}/total`)
        .pipe(catchError(() => of(0)))
  );
  forkJoin(costCalls).subscribe(totaux => {
    this.totalCoutReel = totaux.reduce((s, v) => s + (v || 0), 0);
  });

  const riskCalls = ids.map(id =>
    this.http.get<any[]>(`${this.API}/risques/projet/${id}`)
        .pipe(catchError(() => of([])))
  );
  forkJoin(riskCalls).subscribe(listes => {
    const tous = listes.flat();
    this.risquesCritiques = tous.filter(r => (r.criticite || 0) >= 15).length;
  });
}



  num(v: number) {
    return (v || 0).toLocaleString('fr-FR', { maximumFractionDigits: 0 });
  }
}