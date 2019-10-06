import { Component, OnInit } from '@angular/core';
import { SocialService } from '../../social.service';
import { SocialRefer } from '../../socialRefer';


@Component({
    selector: 'app-extended-table-cmp',
    templateUrl: 'mysubscriptions.component.html'
})

export class MySubscriptionsComponent implements OnInit {
    constructor(private socialService: SocialService) {}
    referData:SocialRefer=new SocialRefer(); 
    ngOnInit() {
        this.getReferrals();
    }
    getReferrals(): void {
        console.log('enter');
        this.socialService
            .getReferralByCode('MAND9874DFDS')
            .subscribe(data => this.referData = data);
            console.log(this.referData);
      }
}
