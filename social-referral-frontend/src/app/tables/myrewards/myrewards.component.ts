import { Component , OnInit, OnDestroy } from '@angular/core';
import { SocialService } from '../../social.service';
import { SocialRefer } from '../../socialRefer';
@Component({
  selector: 'network-graph',
  templateUrl: './myrewards.component.html'
})
export class MyRewardsComponent implements OnInit{
 

  public ngOnInit(): void {
       this.getReferrals();
  }
  constructor(private socialService: SocialService) {}
  referData:SocialRefer=new SocialRefer(); 
  getReferrals(): void {
    console.log('enter');
    this.socialService
        .getReferralByCode('MAND9874DFDS')
        .subscribe(data => this.referData = data);
        console.log(this.referData);
  }
}