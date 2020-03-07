import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';

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

  constructor(
    public dialogRef: MatDialogRef<MyModalComponent>) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onRangeClick(): void {
    this.dialogRef.close();
    console.log(this.startDate);
  }

  ngOnInit() {
    this.minDate.setDate(this.minDate.getDate() - 180);
    this.maxDate.setDate(this.minDate.getDate() + 180);
    this.startDate.setDate(Date.now());
    this.endDate.setDate(Date.now());
  }

}
