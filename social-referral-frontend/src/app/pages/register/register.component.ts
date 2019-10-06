import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap,Params } from '@angular/router';
import { SocialService } from '../../social.service';
import { SocialRefer } from '../../socialRefer';
import {FormsModule} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
    selector: 'app-register-cmp',
    templateUrl: './register.component.html'
})


export class RegisterComponent implements OnInit {
     
    constructor(private socialService: SocialService,private route: ActivatedRoute,private router: Router) {}
    test: Date = new Date();
    private id:string;
    referData:SocialRefer; 
    signupData:SocialRefer=new SocialRefer();
    warningFlag:Boolean=false;

    get referId(){
        return this.id;
    }
    ngOnInit() :void{
        this.id=this.route.snapshot.params['id'];
        console.log(this.id);
       
    }
    signup(name,email,passwd): void{
        if(passwd !=null && name!=null && email!=null){       
        console.log("Enter Signup");
        var randome="ABCDEFGHIJKLMNOPQRS1234567890";
        var end=Math.floor((Math.random() * 25) + 1);
        var start=Math.floor((Math.random() * 10) + 1);
        var referral=name.slice(0,3)+randome.slice(start,end);
        console.log('Referral Code-'+referral);
        this.signupData.referralcode=referral;
        this.signupData.name=name;
        this.signupData.email=email;
        this.signupData.id=100+randome.slice(start+2,end+2)
        this.socialService
        .signUpUser( this.signupData)
        .subscribe(data=>this.referData=data);
        this.router.navigate(['/dashboard']);
        
    }else{
        this.warningFlag=true;
     
    }
    }
    getByReferral():void{
        this.socialService
        .getReferralByCode(this.id)
        .subscribe(data => this.referData = data);
        
        console.log(this.referData);
    }
}
