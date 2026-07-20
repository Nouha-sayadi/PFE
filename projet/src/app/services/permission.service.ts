import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Permission } from 'app/shared/models/profil';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PermissionService {
  private baseUrl = 'http://localhost:8080/api/permissions';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Permission[]> {
    return this.http.get<Permission[]>(this.baseUrl);
  }
  
}
