import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CoutReel {
  id?: number;
  mois?: string;
  nbrJoursReel?: number;
  coutReel?: number;
  ressource?: { id: number; nom: string; prenom: string };
  projet?: { id: number; titre: string };
}

@Injectable({ providedIn: 'root' })
export class CoutReelService {
  private api = 'http://localhost:8080/api/cout-reels';
  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<CoutReel[]> {
    return this.http.get<CoutReel[]>(`${this.api}/projet/${projetId}`);
  }

  // ✅ Déclencher recalcul depuis les pointages
  recalculer(projetId: number): Observable<void> {
    return this.http.post<void>(`${this.api}/projet/${projetId}/recalculer`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}