import { Component, OnInit } from '@angular/core';
import { SocialService } from '../social.service';
import { SocialRefer } from '../socialRefer';

declare const $: any;

//Metadata
export interface RouteInfo {
    path: string;
    title: string;
    type: string;
    icontype: string;
    // icon: string;
    children?: ChildrenItems[];
}

export interface ChildrenItems {
    path: string;
    title: string;
    ab: string;
    type?: string;
}

//Menu Items
export const ROUTES: RouteInfo[] = [{
        path: '/dashboard',
        title: 'Dashboard',
        type: 'link',
        icontype: 'dashboard'
    },/* {
        path: '/components',
        title: 'Components',
        type: 'sub',
        icontype: 'apps',
        children: [
            {path: 'buttons', title: 'Buttons', ab:'B'},
            {path: 'grid', title: 'Grid System', ab:'GS'},
            {path: 'panels', title: 'Panels', ab:'P'},
            {path: 'sweet-alert', title: 'Sweet Alert', ab:'SA'},
            {path: 'notifications', title: 'Notifications', ab:'N'},
            {path: 'icons', title: 'Icons', ab:'I'},
            {path: 'typography', title: 'Typography', ab:'T'}
        ]
    } */,/* {
        path: '/forms',
        title: 'Forms',
        type: 'sub',
        icontype: 'content_paste',
        children: [
            {path: 'regular', title: 'Regular Forms', ab:'RF'},
            {path: 'extended', title: 'Extended Forms', ab:'EF'},
            {path: 'validation', title: 'Validation Forms', ab:'VF'},
            {path: 'wizard', title: 'Wizard', ab:'W'}
        ]
    } */,{
        path: '/tables',
        title: 'Promotions',
        type: 'sub',
        icontype: 'card_giftcard',
        children: [
            {path: 'referrals', title: 'My Referrals', ab:'YR'},
             {path: 'subscriptions', title: 'My Subscriptions', ab:'YS'},
            {path: 'rewards', title: 'My Rewards', ab:'MR'} 
        ]
    },/* {
        path: '/maps',
        title: 'Maps',
        type: 'sub',
        icontype: 'place',
        children: [
            {path: 'google', title: 'Google Maps', ab:'GM'},
            {path: 'fullscreen', title: 'Full Screen Map', ab:'FSM'},
            {path: 'vector', title: 'Vector Map', ab:'VM'}
        ]
    } */,/* {
        path: '/widgets',
        title: 'Widgets',
        type: 'link',
        icontype: 'widgets'

    } */,{
        path: '/charts',
        title: 'Network Graph',
        type: 'link',
        icontype: 'timeline'

    },{
        path: '/pages',
        title: 'Pages',
        type: 'sub',
        icontype: 'image',
        children: [
           
            {path: 'login', title: 'Login Page', ab:'LP'},
            {path: 'register', title: 'Register Page', ab:'RP'},
            {path: 'user', title: 'User Page', ab:'UP'}
        ]
    }
];
@Component({
    selector: 'app-sidebar-cmp',
    templateUrl: 'sidebar.component.html',
})
export class SidebarComponent implements OnInit {



constructor(private socialService:SocialService){}
    
    public loginUserDat:SocialRefer=new SocialRefer();
    public menuItems: any[];

    isNotMobileMenu() {
        if ($(window).width() > 991) {
            return false;
        }
        return true;
    };

    ngOnInit() {
      
        let isWindows = navigator.platform.indexOf('Win') > -1 ? true : false;
        if (isWindows) {
           // if we are on windows OS we activate the perfectScrollbar function
            const $sidebar = $('.sidebar-wrapper');
            $sidebar.perfectScrollbar();
            // if we are on windows OS we activate the perfectScrollbar function
            $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar();
            $('html').addClass('perfect-scrollbar-on');
        } else {
            $('html').addClass('perfect-scrollbar-off');
        }
        this.menuItems = ROUTES.filter(menuItem => menuItem);
        this.loginUserDat= this.socialService.loginUser;
        console.log("Side bar"+this.socialService.user);
    }
}
