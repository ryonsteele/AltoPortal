import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {DisplayMessage} from '../shared/models/display-message';
import {AuthService, ConfigService, UserService} from '../service';
import {Subject} from 'rxjs/Subject';
import {map, takeUntil} from 'rxjs/operators';
import {ApiService} from '../service/api.service';
import {ModalDialogComponent} from '../mat-dialog/mat-dialog.component';
import {MatDialog} from '@angular/material';
import {BsModalRef} from 'ngx-bootstrap/modal';




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
    private formBuilder: FormBuilder,
    public dialog: MatDialog
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

    if (this.form.invalid) { return; }
    this.notification = undefined;
    this.submitted = true;

    console.log(JSON.stringify(this.form.value ) );
    this.apiService.post(this.config.remove_user_url, this.form.value)
      .subscribe(item => {console.log('User Removed');
                          this.submitted = false;
                          this.postProcess("User Removed");
                          // this.router.navigate([this.returnUrl]);
                          }
      , error => {
      this.submitted = false;
      console.log('User Remove error' + JSON.stringify(error));
      this.postProcess('Error: An exception occurred or user does not exist to be removed');
      this.notification = {msgType: 'error', msgBody: error['error'].errorMessage};
    });

  }

  private postProcess(generatedResponse: string) {
    const dialogRef = this.dialog.open(ModalDialogComponent, {
      width: '250px',
      data: generatedResponse,
      panelClass: 'custom-dialog-container',

    });
    dialogRef.afterClosed().subscribe(result => {
      this.router.navigate([this.returnUrl]);
    });
  }


}
