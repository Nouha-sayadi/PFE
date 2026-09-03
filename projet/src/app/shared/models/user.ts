export interface Profile {
  id: number;
  nom: string;
  code: string;
  permissions: any[];
}

export interface User {
  id?: number;
  nom: string;
  prenom: string;
  email: string;motDePasse?: string;
  keycloakId?: string;
  photoUrl?: string | null;
  profils: Profile[];
}