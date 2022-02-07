import {Component, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';

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
export class AppComponent implements OnInit {
  constructor(private http: HttpClient) { }

  ngOnInit() {
    let fileList = this.http.get("http://127.0.0.1:20040/file/list");
    fileList.subscribe( (value: any) => {
      let header = value["header"];
      let body = value["body"];

      if (header.code != 200) {
        console.warn("File Not Found.");
        return;
      }
      for (let i=0; i<body.length; i++) {
        this.files.push({
          name: body[i],
          updated: new Date(),
          damaged: false
        });
      }
    })
  }

  fileName: string = "";
  files: any = [];
  /*
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
    }
   */

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
