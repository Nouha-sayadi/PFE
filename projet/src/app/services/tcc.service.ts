import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TCC {
  id?: number;
  annee?: string;
  tccBase?: number;
  tccAvecFG?: number;
  tarifHJ?: number;
  utilisateur?: { id: number; nom: string; prenom: string };
}

@Injectable({ providedIn: 'root' })
export class TCCService {
  private api = 'http://localhost:8080/api/tcc';

  constructor(private http: HttpClient) {}

  getAll(): Observable<TCC[]> {
    return this.http.get<TCC[]>(this.api);
  }

  getByUser(userId: number): Observable<TCC[]> {
    return this.http.get<TCC[]>(`${this.api}/user/${userId}`);
  }

  create(data: any): Observable<TCC> {
    return this.http.post<TCC>(this.api, data);
  }

  

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}