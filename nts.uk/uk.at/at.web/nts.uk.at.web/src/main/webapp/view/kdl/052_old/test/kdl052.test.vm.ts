module nts.uk.at.view.kdl052.test.screenModel {

  import dialog = nts.uk.ui.dialog.info;
  import text = nts.uk.resource.getText;
  import formatDate = nts.uk.time.formatDate;
  import block = nts.uk.ui.block;
  import jump = nts.uk.request.jump;
  import alError = nts.uk.ui.dialog.alertError;
  import modal = nts.uk.ui.windows.sub.modal;
  import setShared = nts.uk.ui.windows.setShared;

  export class ViewModel extends ko.ViewModel {

    selectedCodeList = ko.observable("xxxxxx000000000004-0004-000000000001," +
      "fe3b1f44-dbc8-44c0-ab32-f617f01f00a5," +
      "96c1e494-5cde-402c-8629-81b0dec7ac92," +
      "da1886cf-b80f-425c-af09-44a94a7643f2," +
      "ae69eb3f-4198-47e3-9f98-967d23c00997");
    baseDate = ko.observable(new Date());
    created() {


    }

    openKDL020Dialog() {
      const vm = this,
        data = {
          employeeIds: _.split(this.selectedCodeList(), ','),
          baseDate: this.baseDate()
        }
      // Input．社員IDリストをチェックする。
      if (data.employeeIds.length > 1) {
        vm.$window.modal('/view/kdl/052/multi.xhtml', data);
      } else {
        vm.$window.modal('/view/kdl/052/single.xhtml', data);
      }
    }
    start(): JQueryPromise<any> {

      var self = this,
        dfd = $.Deferred();


      dfd.resolve();

      return dfd.promise();
    }
  }
}