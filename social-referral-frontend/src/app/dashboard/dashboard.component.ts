import { Component, OnInit, AfterViewInit } from '@angular/core';
import { SweetAlertComponent } from '../components/sweetalert/sweetalert.component';
import {RegisterComponent} from '../pages/register/register.component';
import { SocialService } from '../social.service';

import * as Chartist from 'chartist';
import { SocialRefer } from '../socialRefer';
import {Promotion} from '../promotion';
//import * as myGlobals from '../global';

declare const $: any;

declare var swal: any;

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  constructor(private socialService:SocialService) { }
 
  //public registercom:RegisterComponent;
  public referData: SocialRefer;
  public promotionData:Promotion=new Promotion();
  
  ngOnInit() {
    if(this.socialService.referredFlag) {
        this.showNotification('top','center');
        
    }
    //this.changeData('netflix');
  //  this.globalData=myGlobals.HOTSTAR_OFFER_URL;
}

  showSwal(type) {
    if (type === 'basic') {
        swal({
            title: 'Congrats ! You have got 10 referral points.',
            buttonsStyling: false,
            confirmButtonClass: 'btn btn-success'
        });
    }
    else if (type === 'success-message') {
        swal({
            type: 'success',
            title: 'Congratulations !',
            text: 'You have successfully availed Netflix Offer. Click on My Subscriptions to view more details.',
            buttonsStyling: false,
            confirmButtonClass: 'btn btn-success'

        }); }
        else if (type === 'custom-html') {
            swal({
                title: 'HTML example',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-success',
                html:
                        'You can use <b>bold text</b>, ' +
                        '<a href="http://github.com">links</a> ' +
                        'and other HTML tags'
                });

            } 
    }
    showNotification(from: any, align: any) {
        const type = ['', 'info', 'success', 'warning', 'danger', 'rose', 'primary'];

        const color = 2;//Math.floor((Math.random() * 6) + 1);

        $.notify({
            icon: 'notifications',
            message: 'You have successfully availed <b>Netflix Offer</b>. Click on My Subscriptions to view more details.'
        }, {
            type: type[color],
            timer: 4000,
            placement: {
                from: from,
                align: align
            }
        });
    }
        changeData(offer){
            console.log(offer);
        if(offer=='netflix'){
            this.promotionData.description='Share the Netflix promotion offers with your colleagues';
            this.promotionData.image='../../assets/img/card-2.jpg';
            this.promotionData.link='http://localhost:4200/pages/login/netflix/';
        }
        else if (offer=='prime'){
            this.promotionData.description='Share the exclusive Amazon prime promotion offers with your colleagues and win reward '+
            'points';
            this.promotionData.image='../../assets/img/card-3.png';
            this.promotionData.link='http://localhost:4200/pages/login/prime/';
        }
        else if (offer=='hotstar'){
            this.promotionData.description='Share the Hotstar promotion offers with your colleagues and win reward '+
            'points';
            this.promotionData.image='../../assets/img/card-1.jpeg';
            this.promotionData.link='http://localhost:4200/pages/login/hotstar/';
        }
    }

    getSubscriptions(){
        
        this.socialService.getReferralByCode('MAND9874DFDS')
        .subscribe(data => this.referData = data);
    };

   ngAfterViewInit() {
       const breakCards = true;
       if (breakCards === true) {
           // We break the cards headers if there is too much stress on them :-)
           $('[data-header-animation="true"]').each(function(){
               const $fix_button = $(this);
               const $card = $(this).parent('.card');
               $card.find('.fix-broken-card').click(function(){
                   const $header = $(this).parent().parent().siblings('.card-header, .card-image');
                   $header.removeClass('hinge').addClass('fadeInDown');

                   $card.attr('data-count', 0);

                   setTimeout(function(){
                       $header.removeClass('fadeInDown animate');
                   }, 480);
               });

               $card.mouseenter(function(){
                   const $this = $(this);
                   const hover_count = parseInt($this.attr('data-count'), 10) + 1 || 0;
                   $this.attr('data-count', hover_count);
                   if (hover_count >= 20) {
                       $(this).children('.card-header, .card-image').addClass('hinge animated');
                   }
               });
           });
       }
       //  Activate the tooltips
       $('[rel="tooltip"]').tooltip();
   }


}
