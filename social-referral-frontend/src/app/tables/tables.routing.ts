import { Routes } from '@angular/router';

import { MyReferralsComponent } from './myreferrals/myreferrals.component';
import { MyRewardsComponent } from './myrewards/myrewards.component';
import { MySubscriptionsComponent } from './mysubscriptions/mysubscriptions.component';

export const TablesRoutes: Routes = [
    {
      path: '',
      children: [ {
        path: 'referrals',
        component: MyReferralsComponent
    }]
    }, {
    path: '',
    children: [ {
      path: 'subscriptions',
      component: MySubscriptionsComponent
    }]
    }, {
      path: '',
      children: [ {
        path: 'rewards',
        component: MyRewardsComponent
        }]
    }
];
