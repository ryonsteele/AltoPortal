
import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../service/api.service';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {ConfigService} from "../service";
import {ActivatedRoute, Router} from "@angular/router";
import {map} from "rxjs/operators";



export class Temp {
  name: string;
  tempid: string;

  constructor(name: string, tempid: string) {
    this.name = name;
    this.tempid = tempid;
  }
}

export class Shift {
  user: string;
  tempid: string;
  client: string;
  shiftstart: string;
  shiftend: string;
  orderid: string;
  confirmed: boolean;

  constructor(user: string, tempid: string, client: string,  shiftstart: string, shiftend: string, orderid: string, confirmed: boolean) {
    this.user = user;
    this.tempid = tempid;
    this.client = client;
    this.shiftstart = shiftstart;
    this.shiftend = shiftend;
    this.orderid = orderid;
    this.confirmed = confirmed;
  }
}


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(
    private apiService: ApiService,
    private config: ConfigService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {

  }
  tempsFC = new FormControl();
  messageFC = new FormControl('', [
    Validators.maxLength(170)
  ]);
  tempList: Temp[] = [];
  shiftList: Shift[] = [];
  dtoShiftList: Shift[] = [];

  // User  Client  Shift  Location
  displayedColumns: string[] = ['user', 'tempid', 'client', 'shiftstart', 'shiftend', 'orderid', 'action'];
  dataSource = new MatTableDataSource<Shift>(this.shiftList);
  @ViewChild(MatPaginator) paginator: MatPaginator;

  ngOnInit() {
    this.dataSource.paginator = this.paginator;

    this.apiService.get(this.config.temps_url).pipe(
      map((arr) => arr.map(x => new Temp(x.firstname  + ' ' + x.lastname, x.tempid)))).subscribe(lists => {
      lists.forEach(temp => {
        // console.log(temp.firstname);
        this.tempList.push(temp);
      });
    });
    this.updateTable();
  }

 updateTable() {
   this.shiftList = [];
   this.apiService.get(this.config.shifts_url).pipe(
      map((arr) => arr.map(x => new Shift(x.username, x.tempid, x.clientName, x.shiftStartTime, x.shiftEndTime, x.orderid, false))))
      .subscribe(lists => {
        lists.forEach(shift => {
          this.shiftList.push(shift);
        });
        this.dataSource = new MatTableDataSource(this.shiftList);
      });
  }

  filter(data) {
    console.log(data.value);
  }

  confirmDialog(obj) {
    console.log(obj);
    obj.confirmed = true;
    this.dtoShiftList = [];
    this.dtoShiftList.push(obj);
    for (let item of this.shiftList) {
      if (item.tempid != obj.tempid && item.orderid != obj.orderid) {
        this.dtoShiftList.push(item);
      }
    }
    console.log(JSON.stringify({records: JSON.parse(JSON.stringify(this.dtoShiftList))}));
    this.apiService.post(this.config.confirm_url, {records: JSON.parse(JSON.stringify(this.dtoShiftList))})
      .pipe(map(() => {
        console.log('Confirm success');
        this.updateTable();
      })).subscribe( item =>
      console.log('Confirm success bugaloo')
    );

  }

  Submit() {
    console.log({msgBody: this.messageFC.value, temps: JSON.parse(JSON.stringify(this.tempsFC.value))});
    let data = JSON.stringify(this.tempsFC.value);

    this.apiService.post(this.config.pns_url, {msgBody: this.messageFC.value, temps: JSON.parse(data)})
      .pipe(map(() => {
        console.log('Push Notify success');
        this.messageFC.setValue('');
      })).subscribe( item =>
      console.log('Push Notify success bugaloo')
    );
  }


}

