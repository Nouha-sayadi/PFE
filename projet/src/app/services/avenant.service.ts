import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Avenant {
  id?: number;
  numero?: string;
  objet?: string;
  montantInitial?: number;
  montantRevise?: number;
  deltaMontant?: number;
  impactDelais?: number;
  dateSignature?: string;
  dateEffet?: string;
  statut?: string;
  commentaire?: string;
  projetId?: number;
  contratId?: number;
}

@Injectable({ providedIn: 'root' })
export class AvenantService {
  private api = 'http://localhost:8080/api/avenants';
  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<Avenant[]> {
    return this.http.get<Avenant[]>(`${this.api}/projet/${projetId}`);
  }

  create(data: any): Observable<Avenant> {
    return this.http.post<Avenant>(this.api, data);
  }

  update(id: number, data: any): Observable<Avenant> {
    return this.http.put<Avenant>(`${this.api}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}