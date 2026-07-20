import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

@Injectable({ providedIn: 'root' })
export class IaService {
  private readonly API = 'http://localhost:8080/api/ia';
  constructor(private http: HttpClient) {}

  getSynthese(projetId: number): Observable<string> {
    return this.http.get(`${this.API}/synthese/${projetId}`, { responseType: 'text' });
  }
chat(question: string): Observable<string> {
    return this.http.post(`${this.API}/chat`, { question }, { responseType: 'text' });
  }

}