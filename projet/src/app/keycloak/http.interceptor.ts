import { HttpInterceptorFn } from '@angular/common/http';
import { keycloak } from './keyclock.config';

export const httpInterceptor: HttpInterceptorFn = (req, next) => {

  if (keycloak.token) {

    const token = keycloak.token;

    if (!token) {
  return next(req);
}
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  return next(req);
};