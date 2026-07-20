import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Profil, ProfilCreateRequest } from 'app/shared/models/profil';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfilService {

  private baseUrl = 'http://localhost:8080/api/profils';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Profil[]> {
    return this.http.get<Profil[]>(this.baseUrl);
  }

  createWithPermissions(payload: { nom: string; code: string; permissionIds: number[] }): Observable<Profil> {
    return this.http.post<Profil>(`${this.baseUrl}/create`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updatePermissions(id: number, permissionIds: number[]): Observable<Profil> {
  return this.http.put<Profil>(`${this.baseUrl}/${id}/permissions`, permissionIds);
}
}
