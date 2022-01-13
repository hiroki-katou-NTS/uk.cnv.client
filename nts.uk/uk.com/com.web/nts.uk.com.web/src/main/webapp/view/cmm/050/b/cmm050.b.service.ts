module nts.uk.com.view.cmm050.b {
  export module service {
    /**
     * define path to service
     */
    var path = {
      testMailServerSetting: "sys/env/mailserver/testMailSetting",
    };

    /**
     * 
     */
    export function testMailServerSetting(data: model.MailServerTest): JQueryPromise<any> {
      return nts.uk.request.ajax(path.testMailServerSetting, data);
    }
  }

  /**
   * Model define.
   */
  export module model {
    export class MailServerTest {
      mailFrom: string;
      mailTo: string;
      contents: MailContents;

      constructor(mailFrom: string, mailTo: string, contents: MailContents) {

        this.mailFrom = mailFrom;
        this.mailTo = mailTo;
        this.contents = contents;
      }
    }

    export class MailContents {
      subject: string = nts.uk.resource.getText("CMM050_31");
      body: string = nts.uk.resource.getText("CMM050_32");
    }
  }

}