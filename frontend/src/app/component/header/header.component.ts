import {Component, OnInit} from '@angular/core';
import {AuthService, UserService} from '../../service';
import {Router} from '@angular/router';
import {MyModalComponent} from "../../my-modal/my-modal.component";
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  name: string;
  color: string;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    public dialog: MatDialog
  ) {
  }

  ngOnInit() {
  }

  logout() {
    this.authService.logout().subscribe(res => {
      this.router.navigate(['/']);
    });
  }

  hasSignedIn() {
    if (!this.userService.currentUser) { this.authService.logout(); }
    return !!this.userService.currentUser;
  }

  userName() {
    const user = this.userService.currentUser;
    return user.firstname + ' ' + user.lastname;
  }

  sessionsDialog() {
    const dialogRef = this.dialog.open(MyModalComponent, {
      width: '250px',
      data: {name: this.name, color: this.color}
    });

    dialogRef.afterClosed().subscribe(res => {
      this.color = res;
    });
  }

}
