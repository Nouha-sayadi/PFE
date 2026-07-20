import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Livrable {
  id?: number;
  numero?: number;
  designation?: string;
  phase?: string;
  dateLivraisonPrevue?: string;
  dateLivraisonReelle?: string;
  realise?: boolean;
  livre?: boolean;
  delivery?: number;
  ecartJours?: number;
  commentaire?: string;
  deliveryGlobal?: number;
  projet?: { id: number; titre: string };
}

@Injectable({ providedIn: 'root' })
export class LivrableService {
  private api = 'http://localhost:8080/api/livrables';
  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<Livrable[]> {
    return this.http.get<Livrable[]>(`${this.api}/projet/${projetId}`);
  }

  getDelivery(projetId: number): Observable<number> {
    return this.http.get<number>(`${this.api}/projet/${projetId}/delivery`);
  }

  create(data: any): Observable<Livrable> {
    return this.http.post<Livrable>(this.api, data);
  }

  update(id: number, data: any): Observable<Livrable> {
    return this.http.put<Livrable>(`${this.api}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}