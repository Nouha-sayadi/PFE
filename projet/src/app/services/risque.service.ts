import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Risque {
  id?: number;
  code?: string;
  description?: string;
  categorie?: string;
  probabilite?: number;
  impact?: number;
  criticite?: number;
  statut?: string;
  mesuresMitigation?: string;
  dateIdentification?: string;
  dateEcheance?: string;
  projetId?: number;
}

@Injectable({ providedIn: 'root' })
export class RisqueService {
  private api = 'http://localhost:8080/api/risques';
  constructor(private http: HttpClient) {}

  getByProjet(id: number): Observable<Risque[]> {
    return this.http.get<Risque[]>(`${this.api}/projet/${id}`);
  }
  create(data: any): Observable<Risque> {
    return this.http.post<Risque>(this.api, data);
  }
  update(id: number, data: any): Observable<Risque> {
    return this.http.put<Risque>(`${this.api}/${id}`, data);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}