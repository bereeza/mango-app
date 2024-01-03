import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  name = '';
  title = 'mango-app';
  nicknameStatus = false;

  checkStatus() {
    if (this.name !== '') {
      this.nicknameStatus = true;
    }
    setTimeout(() => {
      this.nicknameStatus = false;
    }, 1500);
  }
}
