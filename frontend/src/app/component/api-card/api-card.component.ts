import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-api-card',
  templateUrl: './api-card.component.html',
  styleUrls: ['./api-card.component.scss']
})
export class ApiCardComponent implements OnInit {

  @Input() title: string;
  @Input() subTitle: string;
  @Input() imgUrl: string;
  @Input() content: string;
  @Input() apiText: string;
  @Input() responseObj: any;

  @Output() apiClick: EventEmitter<any> = new EventEmitter();

  constructor(   private router: Router) {
  }

  ngOnInit() {
    console.log(this.responseObj);
  }

  onButtonClick() {
    this.apiClick.next(this.apiText);
    this.router.navigate(['dashboard']);
  }

  responsePanelClass() {
    const rClass = ['response'];
    if (this.responseObj.status) {

    }
    return rClass.join(' ');
  }

}
