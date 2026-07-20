import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Contrat {
  id?: number;
  numeroContrat?: string;
  intitule?: string;
  montantTotal?: number;
  dateSignature?: any;
  dateEcheance?: any;
  conditionsPaiement?: string;
  projet?: { id: number; titre: string };
}

@Injectable({ providedIn: 'root' })
export class ContratService {
  private api = 'http://localhost:8080/api/contrats';
  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<Contrat[]> {
    return this.http.get<Contrat[]>(`${this.api}/projet/${projetId}`);
  }
  create(data: any): Observable<Contrat> {
    return this.http.post<Contrat>(this.api, data);
  }
  update(id: number, data: any): Observable<Contrat> {
    return this.http.put<Contrat>(`${this.api}/${id}`, data);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}