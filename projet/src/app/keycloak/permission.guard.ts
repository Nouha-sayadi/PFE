import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { UserService } from 'app/services/user.service';

export const permissionGuard: CanActivateFn = (route): Observable<boolean> => {
  const userService = inject(UserService);
  const router = inject(Router);

  const required = route.data?.['permission'] as string | undefined;
  if (!required) return of(true);

  const allow = (codes: string[]): boolean =>
    codes.includes('ADMIN') ||
    codes.includes('ADMINISTRATION') ||
    codes.includes(required);

  return userService.getMyPermissions().pipe(
    map((data: any): boolean => {
      const codes: string[] = (data?.permissions ?? []).map((p: any) => p.code);
      if (allow(codes)) return true;
      router.navigate(['/']);
      return false;
    }),
    catchError((): Observable<boolean> => {
      const codes = userService.getPermissions() ?? [];
      const ok = allow(codes);
      if (!ok) router.navigate(['/']);
      return of(ok);
    })
  );
};