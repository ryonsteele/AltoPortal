
import {Component, OnDestroy, OnInit, QueryList, TemplateRef, ViewChild, ViewChildren} from '@angular/core';
import {ApiService} from '../service/api.service';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {ConfigService, UserService} from "../service";
import {ActivatedRoute, Router} from "@angular/router";
import {map} from "rxjs/operators";
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import {HttpErrorResponse} from "@angular/common/http";
import {AngularCsv} from "angular7-csv";
import {Session} from "../my-modal/my-modal.component";
import {DatePipe} from "@angular/common";
import {MatSort} from "@angular/material/sort";



export class Temp {
  checked: boolean;
  user: string;
  tempid: string;
  certs: string;

  constructor(checked: boolean, user: string, tempid: string, certs: string) {
    this.checked = checked;
    this.user = user;
    this.tempid = tempid;
    this.certs = certs;
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

export class Reset {
  reset: string;
  tempid: string;

  constructor(reset: string, tempid: string) {
    this.reset = reset;
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
  audit: string;
  certs: string;

  constructor(user: string, name: string, tempid: string, client: string,  shiftstart: string, shiftend: string, orderid: string,
              confirmed: boolean, certs: string) {

    this.user = user;
    this.name = name;
    this.tempid = tempid;
    this.client = client;
    this.shiftstart = shiftstart;
    this.shiftend = shiftend;
    this.orderid = orderid;
    this.confirmed = confirmed;
    this.certs = certs;
  }
}

export class SessionUpdate {
  shiftstart: string;
  shiftend: string;
  orderid: string;

  constructor(shiftstart: string, shiftend: string, orderid: string) {
    this.shiftstart = shiftstart;
    this.shiftend = shiftend;
    this.orderid = orderid;
  }
}




@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  modalRef: BsModalRef;
  InvalidClockVal: boolean;
  InvalidClockResp: boolean;
  InvalidResetResp: boolean;
  InvalidUpdateResp: boolean;
  showSessionUpdate: boolean;
  interval: any;
  checkbox: boolean;

  updateOrderId: string;

  startDate: Date = new Date();
  endDate: Date = new Date();
  endDateform: FormControl = new FormControl(new Date());
  startDateform: FormControl = new FormControl(new Date());

  constructor(
    private userService: UserService,
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
  resetFC = new FormControl('', [
    Validators.required, Validators.minLength(4)
  ]);
  resetConfFC = new FormControl('', [
    Validators.required, Validators.minLength(4)
  ]);
  updateFC = new FormControl('', [
    Validators.required, Validators.pattern('^[0-9]*$')
  ]);
  tempList: Temp[] = [];
  shiftList: Shift[] = [];
  dtoShiftList: Shift[] = [];

  // User  Client  Shift  Location
  displayedColumns: string[] = ['name', 'tempid', 'tempcerts', 'client', 'shiftstart', 'shiftend', 'orderid', 'action'];
  usertableColumns: string[] = ['checked', 'user', 'tempid', 'certs'];
  dataSource = new MatTableDataSource<Shift>(this.shiftList);
  usersDataSource = new MatTableDataSource<Temp>(this.tempList);
  @ViewChildren(MatPaginator) paginator = new QueryList<MatPaginator>();
  @ViewChildren(MatSort) sort = new QueryList<MatSort>();

  ngOnInit() {
    this.dataSource.paginator = this.paginator.toArray()[0];
    this.usersDataSource.paginator = this.paginator.toArray()[1];
    this.InvalidClockVal = false;
    this.InvalidClockResp = false;
    this.InvalidUpdateResp = false;
    this.showSessionUpdate = false;
    this.updateShiftsTable();
    this.updateTempsTable();


    this.interval = setInterval(() => {
      this.updateShiftsTable();
    }, 60000);
  }

  ngOnDestroy() {
    if (this.interval) {
      clearInterval(this.interval);
    }
  }

selectAll(e) {
   if(e.target.checked){
	   for (let item of this.tempList) {
         item.checked = true;
       }
   }else{
	   for (let item of this.tempList) {
         item.checked = false;
       }
   }
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

  openResetModal(template: TemplateRef<any>) {
    this.InvalidClockVal = false;
    let myChecked = [];
    for (let item of this.tempList) {
      if (item.checked) {
        myChecked.push(item.tempid);
      }
    }
    if (myChecked.length == 1) {
      this.InvalidResetResp = false;
      this.modalRef = this.modalService.show(template);
    } else {
      this.InvalidClockVal = true;
    }
  }

  openUpdateSessionModal(template: TemplateRef<any>) {
      this.modalRef = this.modalService.show(template);
  }

 updateShiftsTable() {

   this.shiftList = [];
   this.apiService.get(this.config.shifts_url).pipe(
      map((arr) => arr.map(x => new Shift(x.username, x.fullName, x.tempid, x.clientName, x.shiftStartTime, x.shiftEndTime, x.orderid,
        false, x.certs))))
      .subscribe(lists => {
        lists.forEach(shift => {
          this.shiftList.push(shift);
        });
        this.dataSource = new MatTableDataSource(this.shiftList);
        this.dataSource.paginator = this.paginator.toArray()[0];
      });
  }

  updateTempsTable() {

    this.tempList = [];
    this.apiService.get(this.config.temps_url).pipe(
      map((arr) => arr.map(x => new Temp(false, x.firstname  + ' ' + x.lastname, x.tempid, x.certs)))).subscribe(lists => {
      lists.forEach(temp => {
        this.tempList.push(temp);
      });
      this.usersDataSource = new MatTableDataSource(this.tempList);
      this.usersDataSource.paginator = this.paginator.toArray()[1];
    });
  }


  confirmDialog(obj) {
    console.log(obj);
    obj.confirmed = true;
    obj.audit = this.userService.currentUser.username;
    this.dtoShiftList = [];
    this.dtoShiftList.push(obj);
    for (let item of this.shiftList) {
      if (item.tempid != obj.tempid && item.orderid != obj.orderid) {
        item.audit = this.userService.currentUser.username;
        this.dtoShiftList.push(item);
      }
    }
    // console.log(JSON.stringify({records: JSON.parse(JSON.stringify(this.dtoShiftList))}));
    this.apiService.post(this.config.confirm_url, {records: JSON.parse(JSON.stringify(this.dtoShiftList))})
      .pipe(map(() => {
        console.log('Confirm success');
        this.updateShiftsTable();
      })).subscribe();

  }

  removeDialog(obj) {
    console.log(obj);
    this.dtoShiftList = [];
    obj.confirmed = true;
    obj.audit = this.userService.currentUser.username;
    this.dtoShiftList.push(obj);

    // console.log(JSON.stringify({records: JSON.parse(JSON.stringify(this.dtoShiftList))}));
    this.apiService.post(this.config.remove_url, {records: JSON.parse(JSON.stringify(this.dtoShiftList))})
      .pipe(map(() => {
        console.log('Remove success');
        this.updateShiftsTable();
      })).subscribe( item =>
      console.log('Remove success bugaloo')
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
    if (this.clockFC.invalid) {
      return;
    }

    let myChecked = [];
    for (let item of this.tempList) {
      if (item.checked) {
        myChecked.push(item.tempid);
      }
    }
    this.apiService.post(this.config.clock_url, JSON.parse(JSON.stringify(new Clock(this.clockFC.value, myChecked[0]) ) ) )
      .pipe(map((response: Response) => {
        console.log(response.status);
      })).subscribe( item =>
        this.Handle(item),
      error => this.HandleError(error)
    );

  }

  Reset() {
    this.InvalidResetResp = false;
    if (this.resetFC.invalid || this.resetFC.value !== this.resetConfFC.value) {
      return;
    }
    let myChecked = [];
    for (let item of this.tempList) {
      if (item.checked) {
        myChecked.push(item.tempid);
      }
    }
    this.apiService.post(this.config.reset_url, JSON.parse(JSON.stringify(new Reset(this.resetFC.value, myChecked[0]) ) ) )
      .pipe(map((response: Response) => {
      })).subscribe( item =>
        this.Handle(item),
      error => this.HandleError(error)
    );
  }

  UpdateShift() {
    this.InvalidClockResp = false;
    if (this.updateFC.invalid) {
      return;
    }
    this.apiService.get(this.config.shifts_url + '/' + this.updateFC.value)
      .pipe(map(x => new Shift(x.username, x.fullName, x.tempid, x.clientName, x.shiftStartTimeActual, x.shiftEndTimeActual, x.orderid,
        false, x.certs))).subscribe(data => {
        this.startDate = new Date(data.shiftstart);
        this.endDate = new Date(data.shiftend);
        this.showSessionUpdate = true;
        this.updateOrderId = data.orderid;
        },
      error => this.HandleShiftUpdateError(error)
    );

  }

  onNoClick(): void {
    this.modalRef.hide();
    this.showSessionUpdate = false;
  }

  onRangeClick(): void {
    this.modalRef.hide();
    this.showSessionUpdate = false;

    let datePipe = new DatePipe('en-US');
    let startvalue = datePipe.transform(this.startDate, 'yyyy-MM-dd\'T\'HH:mm:ss');
    let endvalue = datePipe.transform(this.endDate, 'yyyy-MM-dd\'T\'HH:mm:ss');
    let session = new SessionUpdate(startvalue, endvalue, this.updateOrderId);

    // console.log(JSON.stringify(JSON.parse(JSON.stringify(session))));
    this.apiService.post(this.config.shifts_url + '/' + this.updateOrderId,  JSON.parse(JSON.stringify(session)))
      .pipe(map(() => {
        console.log('update session success');
      })).subscribe();
  }

  Handle(resp: any) {
    this.modalRef.hide();
    this.resetFC.setValue('');
    this.resetConfFC.setValue('');
    for ( let t of this.tempList ) {
      t.checked = false;
    }
  }

  HandleError(error: any) {
    // this.modalRef.hide();
    if (error.status === 400) {
      this.InvalidClockResp = true;
    } else if ( error.status === 200) {
      this.modalRef.hide();
    }
  }

  HandleShiftUpdateError(error: any) {
    // this.modalRef.hide();
    if (error.status === 400) {
      this.InvalidUpdateResp = true;
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
    if(this.checkbox){
      this.usersDataSource.filteredData.forEach(item => myChecked.push(item.tempid))
    }else {
      for (let item of this.tempList) {
        if (item.checked) {
          myChecked.push(item.tempid);
        }
      }
    }
     // console.log(JSON.stringify({msgBody: this.messageFC.value, temps: JSON.parse(JSON.stringify(myChecked))}));
    let data = JSON.stringify(myChecked);

    this.apiService.post(this.config.pns_url, {msgBody: this.messageFC.value, audit: this.userService.currentUser.username, temps: JSON.parse(data)})
      .pipe(map(() => {
        console.log('Push Notify success');
        this.checkbox = false;
        this.updateTempsTable();
        this.messageFC.setValue('');

      })).subscribe( );
  }


}

