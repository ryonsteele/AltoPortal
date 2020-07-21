import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {DisplayMessage} from '../shared/models/display-message';
import {AuthService, ConfigService, UserService} from '../service';
import {Subject} from 'rxjs/Subject';
import {map, takeUntil} from 'rxjs/operators';
import {MatTableDataSource} from '@angular/material';
import {Shift} from '../dashboard';
import {ApiService} from '../service/api.service';

@Component({
  selector: 'app-remove-user',
  templateUrl: './remove-user.component.html',
  styleUrls: ['./remove-user.component.scss']
})
export class RemoveUserComponent implements OnInit, OnDestroy {
  title = 'Delete User';
  form: FormGroup;

  /**
   * Boolean used in telling the UI
   * that the form has been submitted
   * and is awaiting a response
   */
  submitted = false;

  /**
   * Notification message from received
   * form request or router
   */
  notification: DisplayMessage;

  returnUrl: string;
  private ngUnsubscribe: Subject<void> = new Subject<void>();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private apiService: ApiService,
    private config: ConfigService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {

  }

  ngOnInit() {
    this.route.params
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params: DisplayMessage) => {
        this.notification = params;
      });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.form = this.formBuilder.group({
      username: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(64)])],
      usertype: false
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }


  onSubmit() {
    /**
     * Innocent until proven guilty
     */
    this.notification = undefined;
    this.submitted = true;

    this.apiService.get(this.config.shifts_url).pipe(
      map((arr) => arr.map(x => new Shift(x.username, x.fullName, x.tempid, x.clientName, x.shiftStartTime, x.shiftEndTime, x.orderid,
        false, x.certs))))
      .subscribe(item => console.log('User Removed') )
      , error => {
      this.submitted = false;
      console.log('User Remove error' + JSON.stringify(error));
      this.notification = {msgType: 'error', msgBody: error['error'].errorMessage};
    };


    this.authService.signup(this.form.value)
      .subscribe(data => {
          console.log(data);
          this.authService.login(this.form.value).subscribe(() => {
            this.userService.getMyInfo().subscribe();
          });
          this.router.navigate([this.returnUrl]);
        },
        error => {
          this.submitted = false;
          console.log('Sign up error' + JSON.stringify(error));
          this.notification = {msgType: 'error', msgBody: error['error'].errorMessage};
        });

  }


}
