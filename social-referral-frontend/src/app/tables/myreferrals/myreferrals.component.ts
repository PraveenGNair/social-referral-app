import { Component, OnInit } from '@angular/core';
import { SocialService } from '../../social.service';
import { SocialRefer } from '../../socialRefer';

@Component({
    selector: 'app-regular-table-cmp',
    templateUrl: 'myreferrals.component.html'
})

export class MyReferralsComponent implements OnInit {

    constructor(private socialService: SocialService) {}
    referData:SocialRefer=new SocialRefer(); 

  

    ngOnInit() {
    }

    getReferrals(): void {
        console.log('enter');
        this.socialService
            .getReferralByCode('MAND9874DFDS')
            .subscribe(data => this.referData = data);
            console.log(this.referData);
      }
}
