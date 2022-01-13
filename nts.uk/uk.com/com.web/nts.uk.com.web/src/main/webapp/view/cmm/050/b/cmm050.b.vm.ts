module nts.uk.com.view.cmm050.b {
  import getShared = nts.uk.ui.windows.getShared;

  export module viewmodel {
    export class ScreenModel {
      emailFrom: KnockoutObservable<string> = ko.observable(null);
      emailTo: KnockoutObservable<string> = ko.observable(null);
  
      constructor() {
        const self = this;
        const param = getShared("CMM050Params");
        self.emailFrom(param.emailAuth);
      }
  
      public proceedTestEmail() {
        const self = this;
        (nts.uk.ui as any).block.grayout();
        const param = new model.MailServerTest(self.emailFrom(), self.emailTo(), new model.MailContents());
        service.testMailServerSetting(param)
        .then(() => nts.uk.ui.dialog.info({ messageId: "Msg_534" }))
        .fail(err => (nts.uk.ui.dialog as any).alertError({ messageId: err.messageId }))
        .always(() => (nts.uk.ui as any).block.clear().then(() => self.closeDialog()));
      }
  
      public closeDialog() {
        nts.uk.ui.windows.close();
      }
    }
  }
}