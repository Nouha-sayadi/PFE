import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Pointage {
  id?: number;
  mois?: string;
  nbrJoursReel?: number;
  tarifHJ?: number;
  coutReel?: number;
  statut?: string;
  commentaire?: string;
  ressource?: { id: number; nom: string; prenom: string };
  projet?: { id: number; titre: string };
}

@Injectable({ providedIn: 'root' })
export class PointageService {
  private api = 'http://localhost:8080/api/pointages';
  constructor(private http: HttpClient) {}

  getMesPointages(projetId: number): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.api}/mes-pointages/projet/${projetId}`);
  }

  getMesPointagesAll(): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.api}/mes-pointages`);
  }

  pointer(data: any): Observable<Pointage> {
    return this.http.post<Pointage>(`${this.api}/pointer`, data);
  }

  pointerBatch(data: any[]): Observable<Pointage[]> {
    return this.http.post<Pointage[]>(`${this.api}/pointer/batch`, data);
  }

  getByProjet(projetId: number): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.api}/projet/${projetId}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
