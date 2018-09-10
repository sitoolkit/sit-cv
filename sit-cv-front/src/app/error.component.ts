import { Component } from '@angular/core';

@Component({
  styles:["div h2{ background-color:#f00; }"],
  template: `
    <div class="component">
      <h2>エラー！</h2>
      <p>ページが見当たりません。</p>
    </div>
  `
})
export class ErrorComponent { }