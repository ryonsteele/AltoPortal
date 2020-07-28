import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-mat-dialog',
  templateUrl: './mat-dialog.component.html',
  styleUrls: ['./mat-dialog.component.css']
})
export class ModalDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ModalDialogComponent >,
    @Inject(MAT_DIALOG_DATA) public generatedResponse: string) { }

  ngOnInit() {
  }


  public onOk() {
    console.log('onOk');
    this.dialogRef.close({
      data: {
        closed: true
      }
    });
  }
}
