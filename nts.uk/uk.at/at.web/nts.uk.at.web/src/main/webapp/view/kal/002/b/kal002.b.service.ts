module nts.uk.at.view.kal002.b {
    import ajax = nts.uk.request.ajax;
    export module service {
        export class Service {
            paths = {
                getAllMailSet: "at/function/alarm/mailsetting/getinformailseting",
                addMailSet: "at/function/alarm/mailSetting/addMailSetting"
            }
            constructor() {}
            
            getAllMailSet(): JQueryPromise<any> {
                return ajax("at", this.paths.getAllMailSet);
            }
            addMailSet(mailSet: any): JQueryPromise<any> {
                return ajax("at", this.paths.addMailSet, mailSet);
            }
            
        }
    }
   
}