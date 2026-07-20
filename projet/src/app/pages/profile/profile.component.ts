
import { Component } from '@angular/core';
import { PageBreadcrumbComponent } from '../../shared/components/common/page-breadcrumb/page-breadcrumb.component';
import { UserInfoCardComponent } from '../../shared/components/user-profile/user-info-card/user-info-card.component';

@Component({
  selector: 'app-profile',
  imports: [
    PageBreadcrumbComponent,
   
    UserInfoCardComponent,
    
],
  templateUrl: './profile.component.html',
  styles: ``
})
export class ProfileComponent {

}
