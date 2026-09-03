import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Echeance {
  id?: number;
  numero?: number;
  objet?: string;
  pourcentage?: number;
  montantPrevu?: number;
  montantFacture?: number;
  dateInitiale?: any;
  datePrevActualisee?: any;
  dateReelle?: any;
  emise?: boolean;
  commentaire?: string;
  derive?: number;
}

@Injectable({ providedIn: 'root' })
export class EcheanceService {
  private api = 'http://localhost:8080/api/echeances';
  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<Echeance[]> {
    return this.http.get<Echeance[]>(`${this.api}/projet/${projetId}`);
  }
  create(data: any): Observable<Echeance> {
    return this.http.post<Echeance>(this.api, data);
  }
  update(id: number, data: any): Observable<Echeance> {
    return this.http.put<Echeance>(`${this.api}/${id}`, data);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  generateFacture(id: number): Observable<HttpResponse<Blob>> {
    return this.http.post(`${this.api}/${id}/generate-facture`, null, {
      responseType: 'blob',
      observe: 'response'
    });
  }
}