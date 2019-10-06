import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


import { TablesRoutes } from './tables.routing';

import { MyReferralsComponent } from './myreferrals/myreferrals.component';
import { MyRewardsComponent } from './myrewards/myrewards.component';
import { MySubscriptionsComponent } from './mysubscriptions/mysubscriptions.component';



@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(TablesRoutes),
    FormsModule
  ],
  declarations: [
    MyReferralsComponent,
    MyRewardsComponent,
    MySubscriptionsComponent
  ]
})

export class TablesModule {}
