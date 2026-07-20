import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Affectation {
  id?: number;
  dateAffectation?: string;
  utilisateur?: { id: number; nom: string; prenom: string };
  projet?: {
    id: number;
    titre: string;
    statut?: string;
    tauxAvancement?: number;
    budgetInitial?: number;
    dateDemarrage?: string;
    dateFinPrevu?: string;
  };
  profil?: { id: number; nom: string; code: string };
}

export interface AffectationRequest {
  utilisateurId: number;
  projetId: number;
  profilId: number;
}

@Injectable({ providedIn: 'root' })
export class AffectationService {
  private api = 'http://localhost:8080/api/affectations';

  constructor(private http: HttpClient) {}

  // Affecter un membre → estimation créée automatiquement côté backend
  affecter(request: AffectationRequest): Observable<Affectation> {
    return this.http.post<Affectation>(this.api, request);
  }

  getMembresProjet(projetId: number): Observable<Affectation[]> {
    return this.http.get<Affectation[]>(`${this.api}/projet/${projetId}`);
  }

  getProjetsUser(userId: number): Observable<Affectation[]> {
    return this.http.get<Affectation[]>(`${this.api}/user/${userId}`);
  }

  getMesProjets(): Observable<Affectation[]> {
    return this.http.get<Affectation[]>(`${this.api}/mes-projets`);
  }

  // Retirer un membre → estimation supprimée automatiquement côté backend
  retirer(affectationId: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${affectationId}`);
  }
}