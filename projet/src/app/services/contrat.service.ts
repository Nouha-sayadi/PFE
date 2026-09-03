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

export interface ContratExtract {
  numeroContrat?: string | null;
  intitule?: string | null;
  montantTotal?: number | null;
  dateSignature?: string | null;
  dateEcheance?: string | null;
  conditionsPaiement?: string | null;
}

export interface LivrableExtract {
  numero?: number | null;
  designation?: string | null;
  phase?: string | null;
  dateLivraisonPrevue?: string | null;
}

export interface ProjetSuggestion {
  dateDemarrage?: string | null;
  dateFinPrevu?: string | null;
  budgetInitial?: number | null;
}

export interface ContratExtractionResult {
  contrat: ContratExtract;
  livrables: LivrableExtract[];
  projetSuggestions?: ProjetSuggestion | null;
}

@Injectable({ providedIn: 'root' })
export class ContratService {
  private api = 'http://localhost:8080/api/contrats';
  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<Contrat[]> {
    return this.http.get<Contrat[]>(`${this.api}/projet/${projetId}`);
  }

  extractFromDocument(file: File, projetId?: number): Observable<ContratExtractionResult> {
    const formData = new FormData();
    formData.append('file', file);
    const url = projetId ? `${this.api}/extract?projetId=${projetId}` : `${this.api}/extract`;
    return this.http.post<ContratExtractionResult>(url, formData);
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