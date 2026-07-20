import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ActionProjet {
  id?: number;
  code?: string;
  description?: string;
  typeAction?: string;
  responsable?: string;
  datePrevue?: string;
  dateReelle?: string;
  statut?: string;
  commentaire?: string;
  risqueId?: number;
  projetId?: number;
}

@Injectable({ providedIn: 'root' })
export class ActionService {
  private api = 'http://localhost:8080/api/actions';
  constructor(private http: HttpClient) {}

  getByProjet(id: number): Observable<ActionProjet[]> {
    return this.http.get<ActionProjet[]>(`${this.api}/projet/${id}`);
  }
  create(data: any): Observable<ActionProjet> {
    return this.http.post<ActionProjet>(this.api, data);
  }
  update(id: number, data: any): Observable<ActionProjet> {
    return this.http.put<ActionProjet>(`${this.api}/${id}`, data);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
