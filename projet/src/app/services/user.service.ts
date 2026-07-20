import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from 'app/shared/models/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  register(user: any): Observable<any> {
    return this.http.post('http://localhost:8080/api/users/register', user);
  }

createUserAdmin(user: any): Observable<any> {
  const token = localStorage.getItem('token');
  const headers = { Authorization: `Bearer ${token}` };
  return this.http.post('http://localhost:8080/api/users/register', user, { headers });
}


  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateUser(id: number, user: any): Observable<any> {
  const payload = {
    nom: user.nom,
    prenom: user.prenom,
    email: user.email,
    profilIds: user.profils?.map((p: any) => p.id) ?? []
  };
  return this.http.put<any>(`${this.apiUrl}/${id}`, payload);
}






getMyPermissions(): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/me/permissions`);
}

getAll(): Observable<User[]> {
  return this.http.get<User[]>(this.apiUrl);
}

delete(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`);
}

getAllProfils(): Observable<any[]> {
  return this.http.get<any[]>('http://localhost:8080/api/profils');
}


getUserFromStorage(): any {
  return JSON.parse(localStorage.getItem('user') || '{}');
}

getPermissions(): string[] {
  const user = this.getUserFromStorage();
  if (!user?.profils) return [];
  return user.profils
    .flatMap((profil: any) => profil.permissions || [])
    .map((perm: any) => perm.code);
}

getRoles(): string[] {
  const user = this.getUserFromStorage();
  if (!user?.profils) return [];
  return user.profils.map((p: any) => p.code);
}

hasPermission(permission: string): boolean {
  return this.getPermissions().includes(permission);
}

hasRole(role: string): boolean {
  return this.getRoles().includes(role);
}






 


  
}
