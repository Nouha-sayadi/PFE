import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

export type TypeEntiteDocument = 'CONTRAT' | 'LIVRABLE' | 'ECHEANCE_FACTURATION';

export interface AppDocument {
  id?: number;
  nomFichier?: string;
  nomStocke?: string;
  cheminFichier?: string;
  typeMime?: string;
  taille?: number;
  dateUpload?: string;
  entityType?: TypeEntiteDocument;
  entityId?: number;
}

export interface UploadProgress {
  progress: number;
  done: boolean;
  document?: AppDocument;
}

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private api = 'http://localhost:8080/api/documents';

  constructor(private http: HttpClient) {}

  getByEntity(entityType: TypeEntiteDocument, entityId: number): Observable<AppDocument[]> {
    return this.http.get<AppDocument[]>(`${this.api}/${entityType}/${entityId}`);
  }

  upload(entityType: TypeEntiteDocument, entityId: number, file: File): Observable<UploadProgress> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http
      .post<AppDocument>(`${this.api}/${entityType}/${entityId}`, formData, {
        reportProgress: true,
        observe: 'events',
      })
      .pipe(
        filter(
          (event: HttpEvent<AppDocument>) =>
            event.type === HttpEventType.UploadProgress || event.type === HttpEventType.Response
        ),
        map((event: HttpEvent<AppDocument>): UploadProgress => {
          if (event.type === HttpEventType.UploadProgress) {
            const progress = event.total ? Math.round((100 * event.loaded) / event.total) : 0;
            return { progress, done: false };
          }
          return { progress: 100, done: true, document: (event as any).body };
        })
      );
  }

  download(id: number, nomFichier: string): void {
    this.http.get(`${this.api}/${id}/download`, { responseType: 'blob' }).subscribe((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = window.document.createElement('a');
      a.href = url;
      a.download = nomFichier || 'document';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
