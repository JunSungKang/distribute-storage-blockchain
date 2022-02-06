import { Component } from '@angular/core';

export interface Section {
  name: string;
  updated: Date;
  damaged: boolean;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  fileName: string = "";
  folders: Section[] = [
    {
      name: 'Photos',
      updated: new Date('1/1/16'),
      damaged: false,
    },
    {
      name: 'Recipes',
      updated: new Date('1/17/16'),
      damaged: false,
    },
    {
      name: 'Work',
      updated: new Date('1/28/16'),
      damaged: false,
    },
  ];

  onFileSelected = (event: any) => {

    const file:File = event.target.files[0];

    if (file) {
      this.fileName = file.name;
      const formData = new FormData();
      formData.append("thumbnail", file);
      console.log(formData);
    }
  }

  fileDownload = (folder: Section) => {
    alert(folder.name);
  }
}
