import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { keycloak } from './keyclock.config';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    if (keycloak.authenticated) {
      return true;
    }

    keycloak.login({
      redirectUri: window.location.origin
    });
    return false;
  }
}