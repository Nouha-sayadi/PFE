import { CommonModule } from '@angular/common';
import { Component, ElementRef, QueryList, ViewChildren, ChangeDetectorRef, OnInit, OnDestroy } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { SafeHtmlPipe } from '../../pipe/safe-html.pipe';
import { combineLatest, Subscription } from 'rxjs';
import { UserService } from '../../../services/user.service';

type NavItem = {
  name: string;
  icon: string;
  path?: string;
  new?: boolean;
  permission?: string;
  subItems?: { name: string; path: string; pro?: boolean; new?: boolean; permission?: string; }[];
};

@Component({
  selector: 'app-sidebar',
  imports: [
    CommonModule,
    RouterModule,
    SafeHtmlPipe,
  ],
  templateUrl: './app-sidebar.component.html',
  styleUrls: ['./app-sidebar.component.css'],
})
export class AppSidebarComponent implements OnInit, OnDestroy {

  private readonly allNavItems: NavItem[] = [
    {
      icon: `<svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" clip-rule="evenodd" d="M5.5 3.25C4.25736 3.25 3.25 4.25736 3.25 5.5V8.99998C3.25 10.2426 4.25736 11.25 5.5 11.25H9C10.2426 11.25 11.25 10.2426 11.25 8.99998V5.5C11.25 4.25736 10.2426 3.25 9 3.25H5.5ZM4.75 5.5C4.75 5.08579 5.08579 4.75 5.5 4.75H9C9.41421 4.75 9.75 5.08579 9.75 5.5V8.99998C9.75 9.41419 9.41421 9.74998 9 9.74998H5.5C5.08579 9.74998 4.75 9.41419 4.75 8.99998V5.5ZM5.5 12.75C4.25736 12.75 3.25 13.7574 3.25 15V18.5C3.25 19.7426 4.25736 20.75 5.5 20.75H9C10.2426 20.75 11.25 19.7427 11.25 18.5V15C11.25 13.7574 10.2426 12.75 9 12.75H5.5ZM4.75 15C4.75 14.5858 5.08579 14.25 5.5 14.25H9C9.41421 14.25 9.75 14.5858 9.75 15V18.5C9.75 18.9142 9.41421 19.25 9 19.25H5.5C5.08579 19.25 4.75 18.9142 4.75 18.5V15ZM12.75 5.5C12.75 4.25736 13.7574 3.25 15 3.25H18.5C19.7426 3.25 20.75 4.25736 20.75 5.5V8.99998C20.75 10.2426 19.7426 11.25 18.5 11.25H15C13.7574 11.25 12.75 10.2426 12.75 8.99998V5.5ZM15 4.75C14.5858 4.75 14.25 5.08579 14.25 5.5V8.99998C14.25 9.41419 14.5858 9.74998 15 9.74998H18.5C18.9142 9.74998 19.25 9.41419 19.25 8.99998V5.5C19.25 5.08579 18.9142 4.75 18.5 4.75H15ZM15 12.75C13.7574 12.75 12.75 13.7574 12.75 15V18.5C12.75 19.7426 13.7574 20.75 15 20.75H18.5C19.7426 20.75 20.75 19.7427 20.75 18.5V15C20.75 13.7574 19.7426 12.75 18.5 12.75H15ZM14.25 15C14.25 14.5858 14.5858 14.25 15 14.25H18.5C18.9142 14.25 19.25 14.5858 19.25 15V18.5C19.25 18.9142 18.9142 19.25 18.5 19.25H15C14.5858 19.25 14.25 18.9142 14.25 18.5V15Z" fill="currentColor"></path></svg>`,
      name: "Dashboard",
      subItems: [
        { name: "Accueil", path: "/" },
      ],
    },
    {
      icon: `<svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" clip-rule="evenodd" d="M12 3.5C7.30558 3.5 3.5 7.30558 3.5 12C3.5 14.1526 4.3002 16.1184 5.61936 17.616C6.17279 15.3096 8.24852 13.5955 10.7246 13.5955H13.2746C15.7509 13.5955 17.8268 15.31 18.38 17.6167C19.6996 16.119 20.5 14.153 20.5 12C20.5 7.30558 16.6944 3.5 12 3.5ZM17.0246 18.8566V18.8455C17.0246 16.7744 15.3457 15.0955 13.2746 15.0955H10.7246C8.65354 15.0955 6.97461 16.7744 6.97461 18.8455V18.856C8.38223 19.8895 10.1198 20.5 12 20.5C13.8798 20.5 15.6171 19.8898 17.0246 18.8566ZM2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12ZM11.9991 7.25C10.8847 7.25 9.98126 8.15342 9.98126 9.26784C9.98126 10.3823 10.8847 11.2857 11.9991 11.2857C13.1135 11.2857 14.0169 10.3823 14.0169 9.26784C14.0169 8.15342 13.1135 7.25 11.9991 7.25ZM8.48126 9.26784C8.48126 7.32499 10.0563 5.75 11.9991 5.75C13.9419 5.75 15.5169 7.32499 15.5169 9.26784C15.5169 11.2107 13.9419 12.7857 11.9991 12.7857C10.0563 12.7857 8.48126 11.2107 8.48126 9.26784Z" fill="currentColor"></path></svg>`,
      name: "Gestion Utilisateurs",
      subItems: [
        {
          name: "Mon Profil",
          path: "/profileUser"     
        },
        {
          name: "Liste Utilisateurs",
          path: "/users",
          permission: "GET_USER"    
        },
      ],
    },
    {
      icon: `<svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" clip-rule="evenodd" d="M3.25 5.5C3.25 4.25736 4.25736 3.25 5.5 3.25H18.5C19.7426 3.25 20.75 4.25736 20.75 5.5V18.5C20.75 19.7426 19.7426 20.75 18.5 20.75H5.5C4.25736 20.75 3.25 19.7426 3.25 18.5V5.5ZM5.5 4.75C5.08579 4.75 4.75 5.08579 4.75 5.5V8.58325L19.25 8.58325V5.5C19.25 5.08579 18.9142 4.75 18.5 4.75H5.5ZM19.25 10.0833H15.416V13.9165H19.25V10.0833ZM13.916 10.0833L10.083 10.0833V13.9165L13.916 13.9165V10.0833ZM8.58301 10.0833H4.75V13.9165H8.58301V10.0833ZM4.75 18.5V15.4165H8.58301V19.25H5.5C5.08579 19.25 4.75 18.9142 4.75 18.5ZM10.083 19.25V15.4165L13.916 15.4165V19.25H10.083ZM15.416 19.25V15.4165H19.25V18.5C19.25 18.9142 18.9142 19.25 18.5 19.25H15.416Z" fill="currentColor"></path></svg>`,
      name: "Gestion Profils",
      subItems: [
        {
          name: "Profils",
          path: "/Profils",
          permission: "GET_PROFIL"     // admin
        },
      ],
    },

    {
  icon: `<svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" clip-rule="evenodd" d="M3 5C3 3.89543 3.89543 3 5 3H19C20.1046 3 21 3.89543 21 5V7C21 8.10457 20.1046 9 19 9H5C3.89543 9 3 8.10457 3 7V5ZM5 5H19V7H5V5ZM3 11C3 9.89543 3.89543 9 5 9H11C12.1046 9 13 9.89543 13 11V19C13 20.1046 12.1046 21 11 21H5C3.89543 21 3 20.1046 3 19V11ZM5 11H11V19H5V11ZM15 11C15 9.89543 15.8954 9 17 9H19C20.1046 9 21 9.89543 21 11V19C21 20.1046 20.1046 21 19 21H17C15.8954 21 15 20.1046 15 19V11ZM17 11H19V19H17V11Z" fill="currentColor"/></svg>`,
  name: "Gestion Projets",
  subItems: [
    {
      name: "Projets",
      path: "/projets",
      permission: "GET_PROJET"
    },
    {
  name: "Mes Projets",
  path: "/mes-projets",
  permission: "GET_MES_PROJETS" 
}
  ],
},
{
  name: "Nomenclatures",
  icon: `...`,
  subItems: [
    { name: "Clients",          path: "/nomenclatures/clients",     permission: "GET_CLIENT" },
    { name: "Bailleurs",        path: "/nomenclatures/bailleurs",   permission: "GET_BAILLEUR" },
    { name: "Partenaires",      path: "/nomenclatures/partenaires", permission: "GET_PARTENAIRE" },
    { name: "Devises",          path: "/nomenclatures/devises",     permission: "GET_DEVISE" },
    { name: "Modèles Business", path: "/nomenclatures/modeles",     permission: "GET_MODELE" },
  ]
},

{
  icon: `<svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path fill-rule="evenodd" clip-rule="evenodd" d="M12 3.25C7.16751 3.25 3.25 6.85817 3.25 11.25C3.25 13.1096 3.93493 14.8184 5.08056 16.1852C5.24759 16.3844 5.31431 16.6494 5.26126 16.9042L4.71636 19.5228C4.63008 19.9375 5.03645 20.2833 5.42535 20.1265L8.36512 18.9421C8.55155 18.8669 8.75908 18.8608 8.94957 18.9251C9.90079 19.2469 10.9285 19.4166 12 19.4166C14.6357 19.4166 17.0111 18.3901 18.7238 16.7261C20.4321 15.0663 21.5 12.7898 21.5 10.25L21.4938 10.0034C21.3689 6.02397 17.7245 3.25 12 3.25ZM4.75 11.25C4.75 7.62735 8.06767 4.75 12 4.75C17.1755 4.75 19.9138 7.20952 19.9954 10.0426L20 10.25C20 12.3699 19.0821 14.2793 17.6801 15.6444C16.2734 17.014 14.2432 18.9166 12 18.9166C11.0731 18.9166 10.1836 18.7686 9.36044 18.4936C8.90083 18.3388 8.40325 18.3506 7.95245 18.5322L6.05 19.303L6.43356 17.1876C6.55345 16.5266 6.36707 15.8659 5.95269 15.3596C4.98105 14.1785 4.75 12.7473 4.75 11.25Z" fill="currentColor"/>
    <circle cx="8" cy="11" r="1" fill="currentColor"/>
    <circle cx="12" cy="11" r="1" fill="currentColor"/>
    <circle cx="16" cy="11" r="1" fill="currentColor"/>
  </svg>`,
  name: "Assistant IA",
  path: "/assistant-ia",
},


    
  ];

  navItems: NavItem[] = [];
  userPermissions: string[] = [];

  openSubmenu: string | null | number = null;
  subMenuHeights: { [key: string]: number } = {};
  @ViewChildren('subMenu') subMenuRefs!: QueryList<ElementRef>;

  readonly isExpanded$;
  readonly isMobileOpen$;
  readonly isHovered$;

  private subscription: Subscription = new Subscription();

  constructor(
    public sidebarService: SidebarService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private userService: UserService
  ) {
    this.isExpanded$ = this.sidebarService.isExpanded$;
    this.isMobileOpen$ = this.sidebarService.isMobileOpen$;
    this.isHovered$ = this.sidebarService.isHovered$;
  }

  ngOnInit() {

  this.userService.getMyPermissions().subscribe({
    next: (data) => {

      console.log("RAW DATA =>", data);

      this.userPermissions = (data?.permissions ?? []).map((p: any) => p.code);

      console.log("CODES EXTRACTED =>", this.userPermissions);

      this.filterNavItems();
      this.setActiveMenuFromRoute(this.router.url);
      this.cdr.detectChanges();
    },

    error: () => {

      this.userPermissions = this.userService.getPermissions() ?? [];

      console.log("FALLBACK PERMISSIONS =>", this.userPermissions);

      this.filterNavItems();
      this.setActiveMenuFromRoute(this.router.url);
      this.cdr.detectChanges();
    }
  });
}



  // ── Filtre les navItems selon les permissions du user ──
 filterNavItems(): void {

  const isAdmin =
    this.userPermissions.includes('ADMIN') ||
    this.userPermissions.includes('ADMINISTRATION');

   this.navItems = this.allNavItems
    .map(nav => {
      if (!nav.subItems) {
        return nav;
      }
      return {
        ...nav,
        subItems: nav.subItems.filter(sub =>
          isAdmin || !sub.permission || this.userPermissions.includes(sub.permission)
        )
      };
    })
    .filter(nav =>
      nav.subItems ? nav.subItems.length > 0 : true
    );
}

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  isActive(path: string): boolean {
  return this.router.url === path || this.router.url.startsWith(path);
  }

  toggleSubmenu(section: string, index: number) {
    const key = `${section}-${index}`;
    if (this.openSubmenu === key) {
      this.openSubmenu = null;
      this.subMenuHeights[key] = 0;
    } else {
      this.openSubmenu = key;
      setTimeout(() => {
        const el = document.getElementById(key);
        if (el) {
          this.subMenuHeights[key] = el.scrollHeight;
          this.cdr.detectChanges();
        }
      });
    }
  }

  onSidebarMouseEnter() {
    this.isExpanded$.subscribe(expanded => {
      if (!expanded) {
        this.sidebarService.setHovered(true);
      }
    }).unsubscribe();
  }

  private setActiveMenuFromRoute(currentUrl: string) {
    const menuGroups = [
      { items: this.navItems, prefix: 'main' },
    ];

    menuGroups.forEach(group => {
      group.items.forEach((nav, i) => {
        if (nav.subItems) {
          nav.subItems.forEach(subItem => {
            if (currentUrl === subItem.path) {
              const key = `${group.prefix}-${i}`;
              this.openSubmenu = key;
              setTimeout(() => {
                const el = document.getElementById(key);
                if (el) {
                  this.subMenuHeights[key] = el.scrollHeight;
                  this.cdr.detectChanges();
                }
              });
            }
          });
        }
      });
    });
  }

  onSubmenuClick() {
    this.isMobileOpen$.subscribe(isMobile => {
      if (isMobile) {
        this.sidebarService.setMobileOpen(false);
      }
    }).unsubscribe();
  }
}