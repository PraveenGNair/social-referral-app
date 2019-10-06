import { Injectable }    from '@angular/core';
import { Headers, Http } from '@angular/http';
import { Observable } from 'rxjs';
import "rxjs/Rx";


import { SocialRefer } from './socialRefer';

@Injectable()
export class SocialService {

  private headers = new Headers({'Content-Type': 'application/json'});
  private socialReferralURL = 'http://localhost:8090/rest/v1/referrals';  // URL to web api

  public referredFlag=false;
  public referralCode:string;
  public user:string;
  public loginUser:SocialRefer=new SocialRefer();
  constructor(private http: Http) { }

  getReferrals(): Observable<SocialRefer[]> {
    return this.http.get(this.socialReferralURL).map(response=>{
        return <SocialRefer[]>response.json()})
        .catch(this.handleError);       
               
  }


  getReferralByCode(id: string): Observable<SocialRefer> {
    const url = `${this.socialReferralURL}/${id}`;
    console.log(url);
    return this.http.get(url).map(response=>{
        return <SocialRefer>response.json()})
        .catch(this.handleError);
  }
//SIGNUP
  signUpUser(signupData:SocialRefer):Observable<SocialRefer>{
    console.log('SignupUser');
    return this.http.post(this.socialReferralURL+'/signup',signupData).map(response=>{
      return <SocialRefer>response.json()})
      .catch(this.handleError);
    }
  
  //LOGIN
login(email:string):Observable<SocialRefer>{
  console.log('loginUser');
  const url = `${this.socialReferralURL}/login/${email}/`;
    console.log(url);
  return this.http.get(url).map(response=>{
    this.user=response.json().name;
    return <SocialRefer>response.json()})
    .catch(this.handleError);
    

}
  private handleError(error: Response) {
    return Observable.throw(error.body);
}
}

