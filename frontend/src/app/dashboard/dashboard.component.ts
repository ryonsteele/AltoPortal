
import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ApiService} from '../service/api.service';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {ConfigService} from "../service";
import {ActivatedRoute, Router} from "@angular/router";
import {map} from "rxjs/operators";
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import {HttpErrorResponse} from "@angular/common/http";



export class Temp {
  checked: boolean;
  user: string;
  tempid: string;

  constructor(checked: boolean, user: string, tempid: string) {
    this.checked = checked;
    this.user = user;
    this.tempid = tempid;
  }
}

export class Clock {
  orderid: string;
  tempid: string;

  constructor(orderid: string, tempid: string) {
    this.orderid = orderid;
    this.tempid = tempid;
  }
}

export class Shift {
  user: string;
  name: string;
  tempid: string;
  client: string;
  shiftstart: string;
  shiftend: string;
  orderid: string;
  confirmed: boolean;

  constructor(user: string, name: string, tempid: string, client: string,  shiftstart: string, shiftend: string, orderid: string,
              confirmed: boolean) {

    this.user = user;
    this.name = name;
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
  modalRef: BsModalRef;
  InvalidClockVal: boolean;
  InvalidClockResp: boolean;

  constructor(
    private apiService: ApiService,
    private config: ConfigService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private modalService: BsModalService
  ) {

  }
  messageFC = new FormControl('', [
    Validators.maxLength(170)
  ]);
  clockFC = new FormControl('', [
    Validators.required, Validators.pattern('^[0-9]*$')
  ]);
  tempList: Temp[] = [];
  shiftList: Shift[] = [];
  dtoShiftList: Shift[] = [];

  // User  Client  Shift  Location
  displayedColumns: string[] = ['name', 'user', 'tempid', 'client', 'shiftstart', 'shiftend', 'orderid', 'action'];
  usertableColumns: string[] = ['checked', 'user', 'tempid'];
  dataSource = new MatTableDataSource<Shift>(this.shiftList);
  usersDataSource = new MatTableDataSource<Temp>(this.tempList);
  @ViewChild(MatPaginator) paginator: MatPaginator;

  ngOnInit() {
    this.dataSource.paginator = this.paginator;
    this.usersDataSource.paginator = this.paginator;
    this.InvalidClockVal = false;
    this.InvalidClockResp = false;
    this.updateTable();
  }

  openModal(template: TemplateRef<any>) {
    this.InvalidClockVal = false;
    let myChecked = [];
    for (let item of this.tempList) {
      if (item.checked) {
        myChecked.push(item.tempid);
      }
    }
    if (myChecked.length == 1) {
      this.InvalidClockResp = false;
      this.modalRef = this.modalService.show(template);
    } else {
      this.InvalidClockVal = true;
    }
  }

 updateTable() {

   this.tempList = [];
   this.apiService.get(this.config.temps_url).pipe(
     map((arr) => arr.map(x => new Temp(false, x.firstname  + ' ' + x.lastname, x.tempid)))).subscribe(lists => {
     lists.forEach(temp => {
       // console.log(temp.firstname);
       this.tempList.push(temp);
     });
     this.usersDataSource = new MatTableDataSource(this.tempList);
     this.usersDataSource.paginator = this.paginator;
   });
   this.shiftList = [];
   this.apiService.get(this.config.shifts_url).pipe(
      map((arr) => arr.map(x => new Shift(x.username, x.fullName, x.tempid, x.clientName, x.shiftStartTime, x.shiftEndTime, x.orderid,
        false))))
      .subscribe(lists => {
        lists.forEach(shift => {
          this.shiftList.push(shift);
        });
        this.dataSource = new MatTableDataSource(this.shiftList);
        this.dataSource.paginator = this.paginator;
      });
  }

  filter(data) {
    // console.log(data.value);
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
    // console.log(JSON.stringify({records: JSON.parse(JSON.stringify(this.dtoShiftList))}));
    this.apiService.post(this.config.confirm_url, {records: JSON.parse(JSON.stringify(this.dtoShiftList))})
      .pipe(map(() => {
        console.log('Confirm success');
        this.updateTable();
      })).subscribe( item =>
      console.log('Confirm success bugaloo')
    );

  }

  applyUserFilter(filterValue: string) {
    this.usersDataSource.filter = filterValue.trim().toLowerCase();
  }

  applyShiftFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  Clock() {
    this.InvalidClockResp = false;
    console.log('Clock Submit');
    if (this.clockFC.invalid) {
      return;
    }

    let myChecked = [];
    for (let item of this.tempList) {
      if (item.checked) {
        myChecked.push(item.tempid);
      }
    }
    // let data = JSON.stringify(myChecked);

    // console.log(JSON.stringify( JSON.parse(JSON.stringify(new Clock(this.clockFC.value, this.tempList[0].tempid) ) ) ) );
    this.apiService.post(this.config.clock_url, JSON.parse(JSON.stringify(new Clock(this.clockFC.value, myChecked[0]) ) ) )
      .pipe(map((response: Response) => {
        console.log('Clock map');
        console.log(response.status);
      })).subscribe( item =>
        this.Handle(item),
      error => this.HandleError(error)
    );

  }

  Handle(resp: any) {
    this.modalRef.hide();
    console.log(resp);
  }

  HandleError(error: any) {
    // this.modalRef.hide();
    if (error.status === 400) {
      this.InvalidClockResp = true;
    } else if ( error.status === 200) {
      this.modalRef.hide();
    }
  }

  Submit() {
    console.log('Message Submit');
    if (this.messageFC.invalid) {
      return;
    }
    let myChecked = [];
    for (let item of this.tempList) {
      if (item.checked) {
        myChecked.push(item.tempid);
      }
    }
    // console.log(JSON.stringify({msgBody: this.messageFC.value, temps: JSON.parse(JSON.stringify(myChecked))}));
    let data = JSON.stringify(myChecked);

    this.apiService.post(this.config.pns_url, {msgBody: this.messageFC.value, temps: JSON.parse(data)})
      .pipe(map(() => {
        console.log('Push Notify success');
        this.updateTable();
        this.messageFC.setValue('');
      })).subscribe( item =>
      console.log('Push Notify success bugaloo')
    );
  }


}

