import { Component, OnInit, Inject, ViewEncapsulation } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBar } from '@angular/material';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class CommentComponent implements OnInit {
  commentId = '';
  comment = '';

  constructor(@Inject (MAT_SNACK_BAR_DATA) public data: any, public snackBar:MatSnackBar) { }

  ngOnInit() {
    this.commentId = this.data.commentId;
    this.comment = this.data.comments[this.commentId];
  }

  close() {
    this.snackBar.dismiss();
  }

}
