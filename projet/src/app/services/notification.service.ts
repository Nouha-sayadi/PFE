import { HttpClient } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Client } from '@stomp/stompjs';
import { keycloak } from '../keycloak/keyclock.config';

export interface AppNotification {
  id: number;
  type: 'PROJECT_ASSIGNMENT' | 'POINTAGE_REMINDER' | 'DEADLINE_EXCEEDED';
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
  projectId: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationService implements OnDestroy {
  private api = 'http://localhost:8080/api/notifications';
  private stompClient: Client | null = null;

  notifications$ = new BehaviorSubject<AppNotification[]>([]);
  unreadCount$   = new BehaviorSubject<number>(0);
  // émet chaque nouvelle notif reçue en temps réel (pour le toast)
  newNotif$      = new BehaviorSubject<AppNotification | null>(null);

  constructor(private http: HttpClient) {
    this.loadAll();
    this.connectWebSocket();
  }

  loadAll() {
    this.http.get<AppNotification[]>(this.api).subscribe(list => {
      this.notifications$.next(list);
      this.unreadCount$.next(list.filter(n => !n.read).length);
    });
  }

  markAsRead(id: number) {
    this.http.put(`${this.api}/${id}/read`, {}).subscribe(() => {
      const updated = this.notifications$.value.map(n =>
        n.id === id ? { ...n, read: true } : n
      );
      this.notifications$.next(updated);
      this.unreadCount$.next(updated.filter(n => !n.read).length);
    });
  }

  markAllAsRead() {
    this.http.put(`${this.api}/read-all`, {}).subscribe(() => {
      const updated = this.notifications$.value.map(n => ({ ...n, read: true }));
      this.notifications$.next(updated);
      this.unreadCount$.next(0);
    });
  }

 private connectWebSocket() {
  this.stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws', 
    reconnectDelay: 5000,

    beforeConnect: async () => {
      try { await keycloak.updateToken(30); } catch (_) {}
      this.stompClient!.connectHeaders = {
        Authorization: `Bearer ${keycloak.token ?? ''}`,
      };
    },

    onConnect: () => {
      console.log('[WS] connecté');
      this.stompClient!.subscribe('/user/queue/notifications', (msg) => {
        const notif: AppNotification = JSON.parse(msg.body);
        const current = [notif, ...this.notifications$.value];
        this.notifications$.next(current);
        this.unreadCount$.next(current.filter(n => !n.read).length);
        this.newNotif$.next(notif);
      });
    },

    onStompError:     (f) => console.error('[WS] STOMP error:', f.headers['message'], f.body),
    onWebSocketError: (e) => console.error('[WS] WebSocket error:', e),
    onWebSocketClose: ()  => console.warn('[WS] connexion fermée'),
  });

  this.stompClient.activate();
}


  timeAgo(isoDate: string): string {
    const diff = Date.now() - new Date(isoDate).getTime();
    const m = Math.floor(diff / 60000);
    if (m < 1)  return 'à l\'instant';
    if (m < 60) return `il y a ${m} min`;
    const h = Math.floor(m / 60);
    if (h < 24) return `il y a ${h}h`;
    const d = Math.floor(h / 24);
    if (d === 1) return 'hier';
    return `il y a ${d} jours`;
  }

  ngOnDestroy() {
    this.stompClient?.deactivate();
  }
}
