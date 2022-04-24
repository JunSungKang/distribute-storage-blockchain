export class MESSAGE {

  static KO_KR = "ko_KR";
  static EN_US = "en_US";

  static COMMON = (language: string, logCode: string): string => {
    let log: any = {
      ko_KR: {
        "ERROR_TITLE_01": "이런...",
        "ERROR_CONTENT_01": "존재하지 않는 값이 있습니다.",
        "ERROR_CONTENT_02": "많은 파일이 손상되어 파일을 다운받을 수 없습니다."
      },
      en_US: {
        "ERROR_TITLE_01": "Oops...",
        "ERROR_CONTENT_01": "There is a value that does not exist.",
        "ERROR_CONTENT_02": "Many files are damaged and cannot be downloaded."
      }
    }
    return log[language][logCode];
  }
}


