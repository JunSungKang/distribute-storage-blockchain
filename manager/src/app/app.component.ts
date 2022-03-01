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
    // File list update.
    this.onFileListRefresh();
  }

  downloadPath: string = "D:\\test";
  uploadPath: string = "D:\\test";
  fileName: string = "";
  files: any = [];

  onFileListRefresh = () => {
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

  onUploadFileSelected = (event: any) => {
    const file:File = event.target.files[0];

    if (file) {
      this.fileName = file.name;
      const formData = new FormData();
      formData.append("thumbnail", file);
    }
  }

  fileUpload = (folder: HTMLInputElement) => {
    // TODO: You must use hard-coded file paths and file names as dynamic variables.
    let data = {
      fileName: "소라카역관광.mp4",
      uploadPath: this.uploadPath
    }

    let response = this.http.post("http://127.0.0.1:8080/upload", JSON.stringify(data));
    response.subscribe( value => {
      // Update file list if successful.
      this.onFileListRefresh();
    });
  }

  onDownloadPathSelected = (event: any) => {
    // TODO: Let the user choose a path to download.
    const file:File = event.target.files[0];
  }

  fileDownload = (folder: Section) => {
    let data = {
      fileName: folder.name,
      downloadPath: this.downloadPath
    }

    let response = this.http.post("http://127.0.0.1:8080/download", JSON.stringify(data));
    response.subscribe( value => {
      console.log(value);
    });
  }
}
