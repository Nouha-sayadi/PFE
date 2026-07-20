import { Component, OnInit, AfterViewChecked, ElementRef, QueryList, ViewChildren } from '@angular/core';
import { ProfilService } from '../../../services/profil.service';
import { Permission, Profil } from 'app/shared/models/profil';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PermissionService } from '../../../services/permission.service';
import { PageHeaderComponent } from '../page-header/page-header.component';

@Component({
  selector: 'app-profil',
  imports: [CommonModule, FormsModule, PageHeaderComponent],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css',
})
export class ProfilComponent implements OnInit, AfterViewChecked {

  @ViewChildren('moduleCheckbox') moduleCheckboxes!: QueryList<ElementRef>;

  profils: Profil[] = [];
  selectedProfil: Profil | null = null;

  showForm       = false;
  nom            = '';
  code           = '';
  isLoading      = false;
  isLoadingPerms = false;
  errorMsg       = '';
  successMsg     = '';
  showEditPermModal = false;
  editingProfil: Profil | null = null;
  editSelectedPermIds = new Set<number>();

  allPermissions: Permission[] = [];

  permissionsByModule: Record<string, Permission[]> = {};

  permsByModuleAndInterface: Record<string, Record<string, Permission[]>> = {};

  selectedPermIds = new Set<number>();
  openDetailModules = new Set<string>();

  constructor(
    private profilService: ProfilService,
    private permissionService: PermissionService
  ) {}

  

  ngOnInit(): void {
    this.loadProfils();
  }

  ngAfterViewChecked(): void {
    this.moduleCheckboxes?.forEach((ref, i) => {
      const mod = this.getModuleInterfaceKeys()[i];
      if (mod) {
        ref.nativeElement.indeterminate = this.isModulePartiallySelected(mod);
      }
    });
  }

  openModules = new Set<string>();

toggleModuleAccordion(mod: string): void {
  this.openModules.has(mod) 
    ? this.openModules.delete(mod) 
    : this.openModules.add(mod);
}

isModuleOpen(mod: string): boolean {
  return this.openModules.has(mod);
}

countSelectedInModule(mod: string): number {
  return (this.permissionsByModule[mod] ?? [])
    .filter(p => this.selectedPermIds.has(p.id)).length;
}

 

  loadProfils(): void {
  this.isLoading = true;
  this.profilService.getAll().subscribe({
    next: (data) => {
      this.profils = data;
      this.isLoading = false;
      if (this.selectedProfil) {
        this.selectedProfil = data.find(p => p.id === this.selectedProfil?.id) ?? null;
      }
    },
    error: () => {
      this.errorMsg = 'Impossible de charger les profils.';
      this.isLoading = false;
    }
  });
}

  loadPermissions(): void {
    this.isLoadingPerms = true;
    this.permissionService.getAll().subscribe({
      next: (data) => {
        this.allPermissions = data;
        this.buildPermissionsByModule();
        this.isLoadingPerms = false;
      },
      error: () => {
        this.errorMsg = 'Impossible de charger les permissions.';
        this.isLoadingPerms = false;
      }
    });
  }
  get p(): Profil | null {
  return this.selectedProfil;
}

get safePermissions(): Permission[] {
  return this.selectedProfil?.permissions ?? [];
}
toggleDetailModule(mod: string): void {
  this.openDetailModules.has(mod)
    ? this.openDetailModules.delete(mod)
    : this.openDetailModules.add(mod);
}

isDetailModuleOpen(mod: string): boolean {
  return this.openDetailModules.has(mod);
}






  buildPermissionsByModule(): void {
  this.permissionsByModule = {};
  this.permsByModuleAndInterface = {};

  this.allPermissions.forEach(p => {
    const mod   = p.module        || 'AUTRE';
    const iface = p.interfaceName || null;

    if (!iface) return; // ✅ Ignorer → plus de ligne "Général"

    if (!this.permissionsByModule[mod]) this.permissionsByModule[mod] = [];
    this.permissionsByModule[mod].push(p);

    if (!this.permsByModuleAndInterface[mod])
      this.permsByModuleAndInterface[mod] = {};
    if (!this.permsByModuleAndInterface[mod][iface])
      this.permsByModuleAndInterface[mod][iface] = [];
    this.permsByModuleAndInterface[mod][iface].push(p);
  });
}


  getPermModuleKeys(): string[] {
    return Object.keys(this.permissionsByModule);
  }

  
  getModuleInterfaceKeys(): string[] {
    return Object.keys(this.permsByModuleAndInterface);
  }

 
  getInterfaceKeys(mod: string): string[] {
    return Object.keys(this.permsByModuleAndInterface[mod] || {});
  }

 

  getActionLabel(action: string): string {
    const map: Record<string, string> = {
      POST:   'Création',
      DELETE: 'Suppression',
      GET:    'Lecture',
      PUT:    'Mise à jour'
    };
    return map[action] ?? action;
  }

  getPermByAction(mod: string, action: string): Permission | null {
    return this.permissionsByModule[mod]?.find(p => p.code.startsWith(action)) ?? null;
  }

  getPermByActionAndInterface(mod: string, iface: string, action: string): Permission | null {
    return this.permsByModuleAndInterface[mod]?.[iface]
      ?.find(p => p.code.startsWith(action)) ?? null;
  }


  toggleModule(mod: string): void {
    const perms = this.permissionsByModule[mod] ?? [];
    const allSelected = perms.every(p => this.selectedPermIds.has(p.id));
    const updated = new Set(this.selectedPermIds);
    perms.forEach(p => allSelected ? updated.delete(p.id) : updated.add(p.id));
    this.selectedPermIds = updated;
  }

  isModuleFullySelected(mod: string): boolean {
    const perms = this.permissionsByModule[mod] ?? [];
    return perms.length > 0 && perms.every(p => this.selectedPermIds.has(p.id));
  }

  isModulePartiallySelected(mod: string): boolean {
    const perms = this.permissionsByModule[mod] ?? [];
    const count = perms.filter(p => this.selectedPermIds.has(p.id)).length;
    return count > 0 && count < perms.length;
  }


  toggleInterface(mod: string, iface: string): void {
    const perms = this.permsByModuleAndInterface[mod]?.[iface] ?? [];
    const allSelected = perms.every(p => this.selectedPermIds.has(p.id));
    const updated = new Set(this.selectedPermIds);
    perms.forEach(p => allSelected ? updated.delete(p.id) : updated.add(p.id));
    this.selectedPermIds = updated;
  }

  isInterfaceFullySelected(mod: string, iface: string): boolean {
    const perms = this.permsByModuleAndInterface[mod]?.[iface] ?? [];
    return perms.length > 0 && perms.every(p => this.selectedPermIds.has(p.id));
  }

  isInterfacePartiallySelected(mod: string, iface: string): boolean {
    const perms = this.permsByModuleAndInterface[mod]?.[iface] ?? [];
    const count = perms.filter(p => this.selectedPermIds.has(p.id)).length;
    return count > 0 && count < perms.length;
  }

 

  togglePerm(id: number): void {
    const updated = new Set(this.selectedPermIds);
    updated.has(id) ? updated.delete(id) : updated.add(id);
    this.selectedPermIds = updated;
  }

  selectAll(): void {
    const updated = new Set(this.selectedPermIds);
    this.allPermissions.forEach(p => updated.add(p.id));
    this.selectedPermIds = updated;
  }

  deselectAll(): void {
    this.selectedPermIds = new Set<number>();
  }

  

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (this.showForm) {
      this.loadPermissions();
      this.selectedPermIds = new Set<number>();
      this.nom = '';
      this.code = '';
      this.errorMsg = '';
      this.successMsg = '';
    }
  }

  createProfil(): void {
    if (!this.nom.trim() || !this.code.trim()) {
      this.errorMsg = 'Nom et code sont requis.';
      return;
    }
    const payload = {
      nom:           this.nom.trim(),
      code:          this.code.trim().toUpperCase(),
      permissionIds: Array.from(this.selectedPermIds)
    };
    this.profilService.createWithPermissions(payload).subscribe({
      next: (created) => {
        this.profils.push(created);
        this.successMsg = `Profil "${created.nom}" créé avec ${this.selectedPermIds.size} permission(s).`;
        this.errorMsg   = '';
        this.showForm   = false;
        this.selectedProfil = created;
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => { this.errorMsg = 'Erreur lors de la création.'; }
    });
  }



selectProfil(profil: Profil): void {
  if (this.selectedProfil?.id === profil.id) {
    this.selectedProfil = null;
    this.openDetailModules.clear();
  } else {
    this.selectedProfil = profil;
    this.openDetailModules.clear();
    // Ouvrir le premier module par défaut
    const keys = this.getModuleKeys(profil);
    if (keys.length > 0) this.openDetailModules.add(keys[0]);
  }
}
getActionShort(action: string): string {
  const map: Record<string, string> = {
    GET: 'R', POST: 'C', PUT: 'U', DELETE: 'D'
  };
  return map[action] ?? action;
}

  deleteProfil(id: number): void {
    if (!confirm('Supprimer ce profil ?')) return;
    this.profilService.delete(id).subscribe({
      next: () => {
        this.profils = this.profils.filter(p => p.id !== id);
        if (this.selectedProfil?.id === id) this.selectedProfil = null;
        this.successMsg = 'Profil supprimé.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => { this.errorMsg = 'Erreur lors de la suppression.'; }
    });
  }



  getPermissionsByModule(profil: Profil): Record<string, Permission[]> {
  const grouped: Record<string, Permission[]> = {};
  (profil.permissions ?? []).forEach(p => {  // ← changer || en ??
    const mod = p.module || 'AUTRE';
    if (!grouped[mod]) grouped[mod] = [];
    grouped[mod].push(p);
  });
  return grouped;
}

  getModuleKeys(profil: Profil): string[] {
  return Object.keys(this.getPermissionsByModule(profil));
}

  hasAction(permissions: Permission[], action: string): boolean {
    return permissions.some(p => p.code.startsWith(action));
  }

  countActions(profil: Profil): number {
  return new Set((profil.permissions ?? []).map(p => this.getAction(p.code))).size;
}

  getAction(code: string): string {
    for (const a of ['DELETE', 'POST', 'PUT', 'GET']) if (code.startsWith(a)) return a;
    return 'GET';
  }

  formatModule(mod: string): string {
    const map: Record<string, string> = {
      GESTION_USERS:       'Gestion utilisateurs',
      GESTION_PROFILS:     'Gestion profils',
      GESTION_PERMISSIONS: 'Gestion permissions',
      GESTION_PROJETS:     'Gestion projets',
      ADMINISTRATION:      'Administration',
    };
    return map[mod] ?? mod.replace(/_/g, ' ').toLowerCase();
  }

  getAvatarClass(code: string): string {
    if (code === 'ADMIN')   return 'av-admin';
    if (code === 'USER')    return 'av-user';
    if (code === 'MANAGER') return 'av-manager';
    return 'av-default';
  }

  getBadgeClass(code: string): string {
    if (code === 'ADMIN')   return 'pb-admin';
    if (code === 'USER')    return 'pb-user';
    if (code === 'MANAGER') return 'pb-manager';
    return 'pb-default';
  }
  openEditPermissions(profil: Profil| null): void {
  if (!profil) return;
  this.editingProfil = profil;
  this.editSelectedPermIds = new Set((profil.permissions || []).map(p => p.id));
  if (this.allPermissions.length === 0) {
    this.loadPermissionsForEdit();
  }
  this.showEditPermModal = true;
}

loadPermissionsForEdit(): void {
  this.permissionService.getAll().subscribe({
    next: (data) => {
      this.allPermissions = data;
      this.buildPermissionsByModule();
    }
  });
}
// Interfaces d'un profil pour un module donné
getInterfaceKeysForProfil(profil: Profil, mod: string): string[] {
  const grouped = this.getPermsByModuleAndInterfaceForProfil(profil);
  return Object.keys(grouped[mod] || {});
}

getPermsByModuleAndInterfaceForProfil(profil: Profil): Record<string, Record<string, Permission[]>> {
  const result: Record<string, Record<string, Permission[]>> = {};
  (profil.permissions || []).forEach(p => {
    const mod   = p.module        || 'AUTRE';
    const iface = p.interfaceName; // ✅ Pas de fallback
    if (!iface) return;            // ✅ Ignorer si pas d'interface
    if (!result[mod]) result[mod] = {};
    if (!result[mod][iface]) result[mod][iface] = [];
    result[mod][iface].push(p);
  });
  return result;
}

hasActionInInterface(profil: Profil, mod: string, iface: string, action: string): boolean {
  const grouped = this.getPermsByModuleAndInterfaceForProfil(profil);
  return (grouped[mod]?.[iface] || []).some(p => p.code.startsWith(action));
}



// Toggle permission dans le modal d'édition
toggleEditPerm(id: number): void {
  const updated = new Set(this.editSelectedPermIds);
  updated.has(id) ? updated.delete(id) : updated.add(id);
  this.editSelectedPermIds = updated;
}
toggleAllEditPerms(): void {
  if (this.editSelectedPermIds.size === this.allPermissions.length) {
    this.editSelectedPermIds = new Set<number>();
  } else {
    const updated = new Set<number>();
    this.allPermissions.forEach(p => updated.add(p.id));
    this.editSelectedPermIds = updated;
  }
}

toggleEditModule(mod: string): void {
  const perms = this.permissionsByModule[mod] ?? [];
  const allSelected = perms.every(p => this.editSelectedPermIds.has(p.id));
  const updated = new Set(this.editSelectedPermIds);
  perms.forEach(p => allSelected ? updated.delete(p.id) : updated.add(p.id));
  this.editSelectedPermIds = updated;
}

toggleEditInterface(mod: string, iface: string): void {
  const perms = this.permsByModuleAndInterface[mod]?.[iface] ?? [];
  const allSelected = perms.every(p => this.editSelectedPermIds.has(p.id));
  const updated = new Set(this.editSelectedPermIds);
  perms.forEach(p => allSelected ? updated.delete(p.id) : updated.add(p.id));
  this.editSelectedPermIds = updated;
}

isEditModuleFullySelected(mod: string): boolean {
  const perms = this.permissionsByModule[mod] ?? [];
  return perms.length > 0 && perms.every(p => this.editSelectedPermIds.has(p.id));
}

isEditModulePartiallySelected(mod: string): boolean {
  const perms = this.permissionsByModule[mod] ?? [];
  const count = perms.filter(p => this.editSelectedPermIds.has(p.id)).length;
  return count > 0 && count < perms.length;
}

isEditInterfaceFullySelected(mod: string, iface: string): boolean {
  const perms = this.permsByModuleAndInterface[mod]?.[iface] ?? [];
  return perms.length > 0 && perms.every(p => this.editSelectedPermIds.has(p.id));
}

countEditSelectedInModule(mod: string): number {
  return (this.permissionsByModule[mod] ?? [])
    .filter(p => this.editSelectedPermIds.has(p.id)).length;
}

saveEditPermissions(): void {
  if (!this.editingProfil) return;
  const ids = Array.from(this.editSelectedPermIds);
  this.profilService.updatePermissions(this.editingProfil.id!, ids).subscribe({
    next: (updated) => {
      const idx = this.profils.findIndex(p => p.id === updated.id);
      if (idx >= 0) this.profils[idx] = updated;
      if (this.selectedProfil?.id === updated.id) this.selectedProfil = updated;
      this.showEditPermModal = false;
      this.successMsg = 'Permissions mises à jour !';
      setTimeout(() => this.successMsg = '', 3000);
    },
    error: () => { this.errorMsg = 'Erreur lors de la mise à jour.'; }
  });
}


}