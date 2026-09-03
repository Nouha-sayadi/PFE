import { Routes } from '@angular/router';
import { EcommerceComponent } from './pages/dashboard/ecommerce/ecommerce.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { FormElementsComponent } from './pages/forms/form-elements/form-elements.component';
import { BasicTablesComponent } from './pages/tables/basic-tables/basic-tables.component';
import { BlankComponent } from './pages/blank/blank.component';
import { NotFoundComponent } from './pages/other-page/not-found/not-found.component';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';
import { InvoicesComponent } from './pages/invoices/invoices.component';
import { LineChartComponent } from './pages/charts/line-chart/line-chart.component';
import { BarChartComponent } from './pages/charts/bar-chart/bar-chart.component';
import { AlertsComponent } from './pages/ui-elements/alerts/alerts.component';
import { AvatarElementComponent } from './pages/ui-elements/avatar-element/avatar-element.component';
import { BadgesComponent } from './pages/ui-elements/badges/badges.component';
import { ButtonsComponent } from './pages/ui-elements/buttons/buttons.component';
import { ImagesComponent } from './pages/ui-elements/images/images.component';
import { VideosComponent } from './pages/ui-elements/videos/videos.component';
import { SignInComponent } from './pages/auth-pages/sign-in/sign-in.component';
import { SignUpComponent } from './pages/auth-pages/sign-up/sign-up.component';
import { CalenderComponent } from './pages/calender/calender.component';
import { AuthGuard } from './keycloak/auth.guard';
import { St2iComponent } from './shared/components/st2i/st2i.component';
import { ProfilComponent } from './shared/components/profil/profil.component';
import { UsersComponent } from './shared/components/users/users.component';
import { ProjetComponent } from './shared/components/projet/projet.component';
import { MesProjetsComponent } from './shared/components/mes-projets/mes-projets.component';
import { ProjetDetailComponent } from './shared/components/projet-detail/projet-detail.component';
import { MesChargesComponent } from './shared/components/mes-charges/mes-charges.component';
import { NomenclatureComponent } from './shared/components/nomenclature/nomenclature.component';
import { DashboardComponent } from './shared/components/dashboard/dashboard.component';
import { permissionGuard } from './keycloak/permission.guard';
import { IaChatComponent } from './shared/components/ia-chat/ia-chat.component';
export const routes: Routes = [
  {
    path:'',
    component:AppLayoutComponent,
    canActivate: [AuthGuard],  
    children:[
      {
        path: '',
        component: DashboardComponent,
        pathMatch: 'full',
        title:
          'Dashboard ',
      },
      {
        path:'calendar',
        component:CalenderComponent,
        title:'Angular Calender '
      },
      {
        path:'profileUser',
        component:ProfileComponent,
        title:'Angular Profile Dashboard '
      },
      {
        path:'form-elements',
        component:FormElementsComponent,
        title:'Angular Form Elements Dashboard'
      },
      {
        path:'basic-tables',
        component:BasicTablesComponent,
        title:'Angular Basic Tables Dashboard '
      },
      {
        path:'blank',
        component:BlankComponent,
        title:'Angular Blank Dashboard '
      },
      // support tickets
      {
        path:'invoice',
        component:InvoicesComponent,
        title:'Angular Invoice Details Dashboard '
      },
      {
        path:'line-chart',
        component:LineChartComponent,
        title:'Angular Line Chart Dashboard '
      },
      {
        path:'bar-chart',
        component:BarChartComponent,
        title:'Angular Bar Chart Dashboard '
      },
      {
        path:'alerts',
        component:AlertsComponent,
        title:'Angular Alerts Dashboard '
      },
      {
        path:'avatars',
        component:AvatarElementComponent,
        title:'Angular Avatars Dashboard'
      },
      {
        path:'badge',
        component:BadgesComponent,
        title:'Angular Badges Dashboard '
      },
      {
        path:'buttons',
        component:ButtonsComponent,
        title:'Angular Buttons Dashboard '
      },
      {
        path:'images',
        component:ImagesComponent,
        title:'Angular Images Dashboard '
      },
      {
        path:'videos',
        component:VideosComponent,
        title:'Angular Videos Dashboard '
      },

      
    

   {
        path:'Profils',
        component:ProfilComponent,
        title:'ST2I Profils  '
      },
     
      {
        path:'users',
        component:UsersComponent,
        title:'ST2I Users  '
      },

      { path: 'projets',            component: ProjetComponent,       canActivate: [permissionGuard], data: { permission: 'GET_PROJET' },         title: 'Gestion des projets' },
      { path: 'mes-projets',        component: MesProjetsComponent,   canActivate: [permissionGuard], data: { permission: 'GET_MES_PROJETS' },    title: 'Mes Projets' },
      { path: 'projets/:id/detail', component: ProjetDetailComponent, canActivate: [permissionGuard], data: { permission: 'GET_PROJET' },         title: 'Détail projet' },
      { path: 'mes-charges/:id',    component: MesChargesComponent,   canActivate: [permissionGuard], data: { permission: 'GET_MES_PROJETS' },    title: 'Mes Charges' },
      { path: 'projets/:id/detail', component: ProjetDetailComponent, title: 'Détail projet' },
      { path: 'mes-charges/:id',    component: MesChargesComponent,   title: 'Mes Charges' },
      { path: 'nomenclatures',      component: NomenclatureComponent, title: 'Nomenclatures' },
      { path: 'nomenclatures/:tab', component: NomenclatureComponent, title: 'Nomenclatures' },
      {
  path: 'assistant-ia',
  loadComponent: () => import('./shared/components/ia-chat/ia-chat.component').then(m => m.IaChatComponent),
  title: 'Assistant IA'
}
     
    ]
    

  },


  {
        path:'st2i',
        component:St2iComponent,
        title:'Angular st2i Dashboard '
      },

  





  // auth pages
  {
    path:'signin',
    component:SignInComponent,
    title:'Angular Sign In Dashboard | TailAdmin - Angular Admin Dashboard Template'
  },
  
  // error pages
  {
    path:'**',
    component:NotFoundComponent,
    title:'Angular NotFound Dashboard | TailAdmin - Angular Admin Dashboard Template'
  },
];
