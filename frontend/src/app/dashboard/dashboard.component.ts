
import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../service/api.service';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {AuthService, UserService} from "../service";
import {ActivatedRoute, Router} from "@angular/router";


export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  { position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H' },
  { position: 2, name: 'Helium', weight: 4.0026, symbol: 'He' },
  { position: 3, name: 'Lithium', weight: 6.941, symbol: 'Li' },
  { position: 4, name: 'Beryllium', weight: 9.0122, symbol: 'Be' },
  { position: 5, name: 'Boron', weight: 10.811, symbol: 'B' },
  { position: 6, name: 'Carbon', weight: 12.0107, symbol: 'C' },
  { position: 7, name: 'Nitrogen', weight: 14.0067, symbol: 'N' },
  { position: 8, name: 'Oxygen', weight: 15.9994, symbol: 'O' },
  { position: 9, name: 'Fluorine', weight: 18.9984, symbol: 'F' },
  { position: 10, name: 'Neon', weight: 20.1797, symbol: 'Ne' },
];


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {

  }

  tempsFC = new FormControl();
  messageFC = new FormControl('', [
    Validators.maxLength(30)
  ]);
  tempList: string[] = ['Extra cheese', 'Mushroom', 'Onion', 'Pepperoni', 'Sausage', 'Tomato'];


  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol', 'action'];
  dataSource = new MatTableDataSource<PeriodicElement>(ELEMENT_DATA);
  @ViewChild(MatPaginator) paginator: MatPaginator;

  ngOnInit() {
    // this.toppings.setValue(['Mushroom', 'Onion']);
    this.dataSource.paginator = this.paginator;
  }

  filter(data) {
    console.log(data.value);
  }

  confirmDialog(obj) {
    console.log('confirmed!');
    console.log(obj);
  }

  Submit() {
    console.log(this.messageFC.value);
    this.messageFC.setValue('');
    // this.apiService.post(this.config.logout_url, {}
  }

}

