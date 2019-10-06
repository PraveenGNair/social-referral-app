import { Component, OnInit, ElementRef } from '@angular/core';
import { SocialService } from '../../social.service';
import { ActivatedRoute, ParamMap,Params } from '@angular/router';
import { SocialRefer } from '../../socialRefer';
declare var $: any;
import * as myGlobals from '../../global';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login-cmp',
    templateUrl: './login.component.html'
})

export class LoginComponent implements OnInit {
    test: Date = new Date();
    private toggleButton: any;
    private sidebarVisible: boolean;
    private nativeElement: Node;

    private offer:string;
    loginData:SocialRefer=new SocialRefer();
    loggedUser:SocialRefer=new SocialRefer();

    constructor(private element: ElementRef,private route: ActivatedRoute,private socialService:SocialService,
        private router: Router) {
        this.nativeElement = element.nativeElement;
        this.sidebarVisible = false;
    }
  

    ngOnInit() {
        this.offer=this.route.snapshot.params['offer'];
        if(this.offer){
            this.socialService.referredFlag=true;           
        }
        var navbar : HTMLElement = this.element.nativeElement;
        this.toggleButton = navbar.getElementsByClassName('navbar-toggle')[0];

        setTimeout(function() {
            // after 1000 ms we add the class animated to the login/register card
            $('.card').removeClass('card-hidden');
        }, 700);
    }
    sidebarToggle() {
        var toggleButton = this.toggleButton;
        var body = document.getElementsByTagName('body')[0];
        var sidebar = document.getElementsByClassName('navbar-collapse')[0];
        if (this.sidebarVisible == false) {
            setTimeout(function() {
                toggleButton.classList.add('toggled');
            }, 500);
            body.classList.add('nav-open');
            this.sidebarVisible = true;
        } else {
            this.toggleButton.classList.remove('toggled');
            this.sidebarVisible = false;
            body.classList.remove('nav-open');
        }
    }

    login(emailId,passwd,offer){

        if(emailId!=null && passwd!=null){
        this.loginData.email=emailId;
        this.loginData.subscribedto=offer;
        this.socialService.login(emailId)
        .subscribe(data => this.loggedUser = data);
        console.log(this.loggedUser);
      //  this.loggedUser=this.socialService.loginUser;
        this.router.navigate(['/dashboard']);

        }
        
        console.log(emailId + passwd+offer);
    }
}
