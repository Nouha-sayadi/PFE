import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


export interface Projet {
  id?: number;
  titre?: string;
  description?: string;
  statut?: string;
  tauxAvancement?: number;
  budgetInitial?: number;
  montantTotal?: number;
  margeNette?: number;
  dateDemarrage?: any;
  dateFinPrevu?: any;
  dateDemarrageEffective?: any;
  client?: { id?: number; code?: string; nom?: string };
  bailleur?: { id?: number; code?: string; nom?: string };
  partenaire?: { id?: number; code?: string; nom?: string };
  devise?: { id?: number; code?: string; nom?: string };
  modeleBusiness?: { id?: number; code?: string; nom?: string };
  // IDs pour le formulaire
  clientId?: number;
  bailleurId?: number;
  partenaireId?: number;
  deviseId?: number;
  modeleBusinessId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class ProjetService {
  private api = 'http://localhost:8080/api/projets';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Projet[]> {
    return this.http.get<Projet[]>(this.api);
  }

  getById(id: number): Observable<Projet> {
    return this.http.get<Projet>(`${this.api}/${id}`);
  }

  create(projet: any) {
  return this.http.post<any>(this.api, projet, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

  update(id: number, projet: Partial<Projet>): Observable<Projet> {
    return this.http.put<Projet>(`${this.api}/${id}`, projet);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
  getNomenclatures(id: number) {
  return this.http.get<any[]>(`${this.api}/${id}/nomenclatures`);
}

affecterNomenclatures(id: number, nomenclatureIds: number[]) {
  return this.http.post(
    `${this.api}/${id}/nomenclatures`,
    nomenclatureIds
  );
}
  
}





