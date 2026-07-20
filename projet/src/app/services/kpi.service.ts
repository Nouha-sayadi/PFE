import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface KpiMensuel {
  id?: number;
  mois?: string;
  earnedValue?: number;
  delivery?: number;
  consommationDelais?: number;
  tauxFacturation?: number;
  chargePrevueMois?: number;
  chargeConsommeeMois?: number;
  chargeConsommeeCumul?: number;
  resteAFaireETC?: number;
  resteAFaireEAC?: number;
  derive?: number;
  coutReel?: number;
  coutReelCumul?: number;
  coutETC?: number;
  coutEAC?: number;
  caProduction?: number;
  caFacture?: number;
  stock?: number;
  margeNetteMois?: number;
  margeNetteActuelle?: number;
  margeNetteEAC?: number;
  margeNetteEACPct?: number;
  faitsMarquants?: string;
}

@Injectable({ providedIn: 'root' })
export class KpiService {
  private api = 'http://localhost:8080/api/kpis';

  constructor(private http: HttpClient) {}

  getByProjet(projetId: number): Observable<KpiMensuel[]> {
    return this.http.get<KpiMensuel[]>(`${this.api}/projet/${projetId}`);
  }

  save(data: any): Observable<KpiMensuel> {
    return this.http.post<KpiMensuel>(this.api, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  // ✅ Auto-calcul depuis les données réelles
  calculer(projetId: number, mois: string): Observable<any> {
    return this.http.get<any>(
      `${this.api}/projet/${projetId}/calculer/${mois}`
    );
  }

  // ✅ Recalculer avec EV
  calculerAvecEV(projetId: number, mois: string, ev: number): Observable<any> {
    return this.http.get<any>(
      `${this.api}/projet/${projetId}/calculer/${mois}/ev/${ev}`
    );
  }
}