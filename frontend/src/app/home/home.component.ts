import {Component, OnInit} from '@angular/core';
import {ConfigService, FooService, UserService} from '../service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  fooResponse = {};
  whoamIResponse = {};
  allUserResponse = {};

  constructor(
    private config: ConfigService,
    private fooService: FooService,
    private userService: UserService
  ) {
  }

  ngOnInit() {
  }

}
