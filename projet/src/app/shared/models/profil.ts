export interface Permission {
  id: number;
  code: string;
  libelle: string;
  module: string; 
  parent?: Permission;
  children?: Permission[];
  interfaceName: string; 
}

export interface Profil {
  id?: number;
  nom: string;
  code: string;
  permissions?: Permission[];
}

export interface ProfilCreateRequest {
  nom: string;
  code: string;
}