import { keycloak } from './keyclock.config';

export function initializeKeycloak() {
  return () =>
    keycloak.init({
      onLoad: 'check-sso',
      checkLoginIframe: false,
      pkceMethod: 'S256'
    })
    .then((authenticated) => {
      console.log('🔥 Keycloak READY:', authenticated);
      //if (!authenticated) {
        //keycloak.login();
      //}
    })
    .catch((err) => {
      console.error('Keycloak init failed', err);
      throw err;
    });
}