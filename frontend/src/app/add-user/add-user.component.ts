import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {DisplayMessage} from '../shared/models/display-message';
import {AuthService} from '../service';
import {map, mergeMap} from 'rxjs/operators';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss']
})
export class AddUserComponent implements OnInit {

  form: FormGroup;
  /**
   * Boolean used in telling the UI
   * that the form has been submitted
   * and is awaiting a response
   */
  submitted = false;

  /**
   * Diagnostic message from received
   * form request error
   */
  notification: DisplayMessage;

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
  }

  ngOnInit() {

    this.form = this.formBuilder.group({
      userName: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(32)])],
      newPassword: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(32)])],
      firstName: ['', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(25)])],
      lastName: ['', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(25)])],
      email: ['', Validators.compose([Validators.required, Validators.email])]
    });

  }


  onSubmit() {
    /**
     * Innocent until proven guilty
     */
    this.notification = undefined;
    this.submitted = true;


    console.log(JSON.stringify(this.form.value ) );


    this.authService.addUser(this.form.value)
      .pipe(map(() => console.log('add success')))
      .subscribe(() => {
        this.router.navigate(['/']);
        // console.log('add success bugaloo');
      }, error => {
        this.submitted = false;
        console.error('Error');
      });

  }

}
