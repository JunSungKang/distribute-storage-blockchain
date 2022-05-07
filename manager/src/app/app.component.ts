import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { saveAs } from 'file-saver';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from "./component/dialog/dialog.component";
import { MESSAGE } from "../assets/common.message";

export interface Section {
  name: string;
  damaged: Number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private http: HttpClient, private dialog: MatDialog) {
  }

  ngOnInit() {
    // File list update.
    this.onFileListRefresh();
  }
  fileName: string = "";
  files: any = [];

  onFileListRefresh = () => {
    let fileList = this.http.get("http://localhost:20040/file/list");
    fileList.subscribe((value: any) => {
      let header = value["header"];
      let body = value["body"];

      if (header.code != 200) {
        console.warn("File Not Found.");
        return;
      }
      for (let i = 0; i < body.length; i++) {
        this.fileCheck(body[i]);
      }
    })
  }

  onUploadFileSelected = (event: any) => {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList) {
      this.fileName = fileList[0].name;
    }
  }

  fileUpload = (folder: HTMLInputElement) => {
    // TODO: You must use hard-coded file paths and file names as dynamic variables.
    let fileName = "";

    // 선택된 파일이 없거나 HTML DOM 구조에 손상이 있는 경우 (unll check)
    if (folder.files == null || folder.files.length < 1) {
      this.dialog.open(DialogComponent, {
        data: {
          title: MESSAGE.COMMON(MESSAGE.KO_KR, "ERROR_TITLE_01"),
          content: MESSAGE.COMMON(MESSAGE.KO_KR, "ERROR_CONTENT_01")
        },
      });
      return;
    }

    let formData: FormData = new FormData();
    formData.append("file", folder.files[0]);

    const upload$ = this.http.post("http://localhost:20040/file/upload?fileName=" +folder.files[0].name, formData);
    upload$.subscribe();
  }

  fileDownload = (folder: Section) => {
    if (folder.damaged > 3) {
      this.dialog.open(DialogComponent, {
        data: {
          title: MESSAGE.COMMON(MESSAGE.KO_KR, "ERROR_TITLE_01"),
          content: MESSAGE.COMMON(MESSAGE.KO_KR, "ERROR_CONTENT_02")
        },
      });
      return;
    }

    let response = this.http.get("/file/download?fileName="+ folder.name, { responseType: "blob" });
    response.subscribe(blob => {
      saveAs(blob, folder.name);
    });
  }

  fileCheck = (fileName: string) => {
    this.http.get("http://localhost:20040/file/damage-check?fileName=" + fileName)
      .subscribe((value: any) => {
        let header = value["header"];
        let body = value["body"];
        let damageSize = 0;

        if (header.code != 200) {
          console.warn("File Not Found.");
          damageSize = -1;
        }
        damageSize = body.length;

        this.files.push({
          name: fileName,
          damaged: damageSize
        });
      });
  }
}
