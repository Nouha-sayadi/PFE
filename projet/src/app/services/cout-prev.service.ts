import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CoutPrev {
  id?: number;
  mois?: string;
  chargePrevuM?: number;
  coutPrev?: number;
  nbrJours?: number;
  dateDemarrageEffective?: string;
  dateFinPrevu?: string;
  projet?: { id: number; titre: string };
  ressource?: { id: number; nom: string; prenom: string };
}

export interface Comparaison {
  totalPrevisionnel: number;
  totalReel: number;
  ecart: number;
  ecartPourcentage: number;
  statut: string;
}

@Injectable({ providedIn: 'root' })
export class CoutPrevService {
  private api = 'http://localhost:8080/api/cout-prevs';

  constructor(private http: HttpClient) {}

  create(data: any): Observable<CoutPrev> {
    return this.http.post<CoutPrev>(this.api, data);
  }

  createBatch(data: any[]): Observable<CoutPrev[]> {
  return this.http.post<CoutPrev[]>(`${this.api}/batch`, data);
}

  getByProjet(projetId: number): Observable<CoutPrev[]> {
    return this.http.get<CoutPrev[]>(`${this.api}/projet/${projetId}`);
  }

  getTotal(projetId: number): Observable<number> {
    return this.http.get<number>(`${this.api}/projet/${projetId}/total`);
  }

  getCumul(projetId: number): Observable<number> {
    return this.http.get<number>(`${this.api}/projet/${projetId}/cumul`);
  }

  getTotalParMois(projetId: number): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(
      `${this.api}/projet/${projetId}/total-par-mois`
    );
  }

  getComparaison(projetId: number): Observable<Comparaison> {
    return this.http.get<Comparaison>(
      `${this.api}/projet/${projetId}/comparaison`
    );
  }

  update(id: number, data: any): Observable<CoutPrev> {
    return this.http.put<CoutPrev>(`${this.api}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  getByProjetAndUser(projetId: number, userId: number): Observable<CoutPrev[]> {
  return this.http.get<CoutPrev[]>(
    `${this.api}/projet/${projetId}/user/${userId}`
  );
}
}