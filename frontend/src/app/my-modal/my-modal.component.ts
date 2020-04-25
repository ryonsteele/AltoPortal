import {Component, Inject, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormControl, FormGroup} from "@angular/forms";
import {ApiService} from '../service/api.service';
import {map} from "rxjs/operators";
import {ConfigService} from "../service";
import { AngularCsv } from 'angular7-csv/dist/Angular-csv';
import {Shift} from "../dashboard";


export class Session {
  orderid: string;
  name: string;
  username: string;
  tempid: string;
  shiftstart: string;
  shiftend: string;
  status: string;
  breaks: string;
  shiftstartactual: string;
  shiftendactual: string;
  shiftstartsignoff: string;
  shiftendsignoff: string;
  clientid: string;
  clientname: string;
  orderSpec: string;
  orderCert: string;
  floor: string;
  shiftnumber: string;
  inaddy: string;
  inlat: string;
  inlon: string;
  outaddy: string;
  outlat: string;
  outlon: string;

  constructor( orderid: string, name: string, username: string, tempid: string, shiftstart: string, shiftend: string,
               status: string, breaks: string, shiftstartactual: string, shiftendactual: string,
               shiftstartsignoff: string, shiftendsignoff: string, clientid: string, clientname: string, orderSpec: string,
               orderCert: string, floor: string, shiftnumber: string, inaddy: string, inlat: string, inlon: string,
               outaddy: string, outlat: string, outlon: string) {

    this.orderid = orderid;
    this.name = name;
    this.username = username;
    this.tempid = tempid;
    this.shiftstart = shiftstart;
    this.shiftend = shiftend;
    this.status = status;
    this.breaks = breaks;
    this.shiftstartactual = shiftstartactual;
    this.shiftendactual = shiftendactual;
    this.shiftstartsignoff = shiftstartsignoff;
    this.shiftendsignoff = shiftendsignoff;
    this.clientid = clientid;
    this.clientname = clientname;
    this.orderSpec = orderSpec;
    this.orderCert = orderCert;
    this.floor = floor;
    this.shiftnumber = shiftnumber;
    this.inaddy = inaddy;
    this.inlat = inlat;
    this.inlon = inlon;
    this.outaddy = outaddy;
    this.outlat = outlat;
    this.outlon = outlon;
  }
}

@Component({
  selector: 'app-my-modal',
  templateUrl: './my-modal.component.html',
  styleUrls: ['./my-modal.component.css']
})
export class MyModalComponent implements OnInit {

  date: string;
  minDate: Date = new Date();
  maxDate: Date = new Date();
  startDate: Date = new Date();
  endDate: Date = new Date();
  endDateform: FormControl = new FormControl(new Date());
  startDateform: FormControl = new FormControl(new Date());
  shiftList: Session[] = [];


  constructor(
    public dialogRef: MatDialogRef<MyModalComponent>, private apiService: ApiService, private config: ConfigService) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onRangeClick(): void {
    this.dialogRef.close();

    this.startDate = this.startDateform.value;
    this.endDate = this.endDateform.value;

    // console.log(JSON.stringify({start: JSON.parse(JSON.stringify(this.startDate.toISOString())), end: JSON.parse(JSON.stringify(this.endDate.toISOString()))} ));
    this.apiService.post(this.config.sessions_url, {start: JSON.parse(JSON.stringify(this.startDate.toDateString())), end: JSON.parse(JSON.stringify(this.endDate.toDateString()))} )
      .pipe(map((arr) => arr.map(x => new Session(x.orderid, x.tempName, x.username, x.tempid,
        x.shiftStartTime, x.shiftEndTime, x.status, x.breaks, x.shiftStartTimeActual,
        x.shiftEndTimeActual, x.shiftStartSignoff, x.shiftEndSignoff, x.clientId, x.clientName, x.orderSpecialty,
        x.orderCertification, x.floor, x.shiftNumber, x.clockInAddress, x.checkinLat, x.checkinLon, x.clockoutAddress,
        x.checkoutLat, x.checkoutLon) )))
      .subscribe(lists => {
        lists.forEach(shift => {
          this.shiftList.push(shift);
        });

        let options = {
          fieldSeparator: ',',
          quoteStrings: '"',
          decimalseparator: '.',
          showLabels: true,
          showTitle: true,
          title: 'Full Sessions',
          useBom: true,
          noDownload: false,
          headers: ['OrderId', 'TempName', 'Username', 'TempId', 'ShiftStart', 'ShiftEnd', 'ShiftStatus', 'Breaks',
                    'ClockIn', 'ClockOut', 'ClockInSignoff', 'ClockOutSignoff', 'ClientId', 'ClientName', 'OrderSpec', 'OrderCert',
                    'floor', 'ShiftNumber', 'ClockInAddress', 'CheckInLat', 'CheckInLon', 'ClockOutAddress', 'CheckOutLat', 'CheckOutLon']
        };

        console.log(this.shiftList);
        new AngularCsv(this.shiftList, 'full_sessions', options);

      });
  }

  ngOnInit() {
    this.minDate.setDate(this.minDate.getDate() - 180);
    this.maxDate.setDate(this.minDate.getDate() + 180);
  }

}
