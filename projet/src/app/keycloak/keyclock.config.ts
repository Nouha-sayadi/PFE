import Keycloak from 'keycloak-js';

export const keycloak = new Keycloak({
  url: 'http://localhost:8081',
  realm: 'st2i-realm',
  clientId: 'st2i-frontend'
});

console.log('KEYCLOAK CONFIG LOADED');