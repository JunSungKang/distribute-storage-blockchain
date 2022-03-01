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

  downloadPath: string = "";
  fileName: string = "";
  files: any = [];

  onUploadFileSelected = (event: any) => {
    const file:File = event.target.files[0];

    if (file) {
      this.fileName = file.name;
      const formData = new FormData();
      formData.append("thumbnail", file);
    }
  }

  onDownloadPathSelected = (event: any) => {
    // TODO: 사용자가 다운 받을 경로를 선택하도록 해야함
    const file:File = event.target.files[0];
  }

  fileDownload = (folder: Section) => {
    let data = {
      fileName: folder.name,
      downloadPath: "D:\\test"
    }
    let response = this.http.post("http://127.0.0.1:8080/download", JSON.stringify(data));
    response.subscribe( value => {
      console.log(value);
    });
  }
}
