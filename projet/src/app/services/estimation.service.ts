import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Estimation {
  id?: number;
  nbrJours?: number;
  tarifHJ?: number;
  coutEstime?: number;
  projet?: { id: number; titre: string };
  ressource?: { id: number; nom: string; prenom: string };
}

@Injectable({ providedIn: 'root' })
export class EstimationService {
  private api = 'http://localhost:8080/api/estimations';

  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<Estimation[]> {
    return this.http.get<Estimation[]>(`${this.api}/projet/${projetId}`);
  }

  getTotal(projetId: number): Observable<number> {
    return this.http.get<number>(`${this.api}/projet/${projetId}/total`);
  }

  create(data: { projetId: number; ressourceId: number }): Observable<Estimation> {
    return this.http.post<Estimation>(this.api, data);
  }

  update(id: number, data: { nbrJours: number; tarifHJ?: number }): Observable<Estimation> {
    return this.http.put<Estimation>(`${this.api}/${id}`, data);
  }

  recalculer(id: number): Observable<Estimation> {
    return this.http.put<Estimation>(`${this.api}/${id}/recalculer`, {});
  }

  recalculerTout(projetId: number): Observable<void> {
    return this.http.put<void>(`${this.api}/projet/${projetId}/recalculer-tout`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}