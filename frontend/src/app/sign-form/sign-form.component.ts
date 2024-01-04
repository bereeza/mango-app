import {Component} from '@angular/core';

@Component({
  selector: 'app-sign-form',
  templateUrl: './sign-form.component.html',
  styleUrls: ['./sign-form.component.css']
})
export class SignFormComponent {
  email: string = '';
  password: string = '';
  showMessage: boolean = false;

  onSave() {
    this.showMessage = true;
  }
}
