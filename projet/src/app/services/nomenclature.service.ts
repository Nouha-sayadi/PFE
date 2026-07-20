import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface NomenclatureItem {
  id?: number;
  code?: string;
  nom?: string;
  // Client
  raisonSociale?: string;
  adresse?: string;
  telephone?: string;
  email?: string;
  matriculeFiscale?: string;
  registreCommerce?: string;
  secteurActivite?: string;
  pays?: string;
  // Bailleur
  typeFinancement?: string;
  // Partenaire
  partGroupement?: number;
  // Devise
  libelle?: string;
  tauxChange?: number;
  estDeviseBase?: boolean;
  // ModeleBusiness
  typeGroupement?: string;
  partenaireObligatoire?: boolean;
}

@Injectable({ providedIn: 'root' })
export class NomenclatureService {
  private base = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}

  getAll(type: string): Observable<NomenclatureItem[]> {
    return this.http.get<NomenclatureItem[]>(`${this.base}/${type}`);
  }
  create(type: string, item: any): Observable<any> {
    return this.http.post<any>(`${this.base}/${type}`, item);
  }
  update(type: string, id: number, item: any): Observable<any> {
    return this.http.put<any>(`${this.base}/${type}/${id}`, item);
  }
  delete(type: string, id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${type}/${id}`);
  }
}