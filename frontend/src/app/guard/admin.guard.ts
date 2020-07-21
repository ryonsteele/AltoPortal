import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {UserService} from '../service';
import {Observable, Subject} from "rxjs";
import {AuthService} from '../service';
import {map, take, tap} from "rxjs/operators";

@Injectable()
export class AdminGuard implements CanActivate {
  constructor(private router: Router, private userService: UserService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // var subject = new Subject<boolean>();
    // if (!this.userService.currentUser) {
    //   this.userService.initUser();
    // }

      // if (localStorage.getItem('currentUser')) {
      //   return true;
      // } else {
      //   return false;
      // }


     if (this.userService.currentUser) {
      if (JSON.stringify(this.userService.currentUser.authorities).search('ROLE_ADMIN') !== -1) {
        return true;
      } else {
        this.router.navigate(['/403']);
        return false;
      }

    } else {
      console.log('NOT AN ADMIN ROLE');
      this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
      return false;
    }
      return false;
  }
}

