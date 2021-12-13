/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.at.view.kmf002.g {
  const API = {
    export: "/masterlist/report/print",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    public openDialogCDL028() {
      const vm = this;
      const param = {
        date: moment.utc().toDate(),
        mode: 5
      };
      nts.uk.ui.windows.setShared("CDL028_INPUT", param);

      nts.uk.ui.windows.sub.modal("com", "/view/cdl/028/a/index.xhtml").onClosed(function () {
        var data = nts.uk.ui.windows.getShared("CDL028_A_PARAMS");
        if (data.status) {
          const startDate = moment.utc(data.startDateFiscalYear, "YYYY/MM/DD").toISOString();
          const endDate = moment.utc(data.endDateFiscalYear, "YYYY/MM/DD").toISOString();
          vm.exportExcel(startDate, endDate);
        }
      });
    }

    public openScreenM() {
      const vm = this;
      vm.$jump("/view/kmf/002/m/index.xhtml");
    }

    public openDialogA() {
      const vm = this;
      vm.$window.modal("/view/kmf/002/a/index.xhtml").then(() => {

      });
    }

    public openDialogF() {
      const vm = this;
      vm.$window.modal("/view/kmf/002/f/index.xhtml").then(() => {

      });
    }

    public openDialogH() {
      const vm = this;
      vm.$window.modal("/view/kmf/002/h/index.xhtml");
    }

    public openKDL058() {
      const vm = this;
      nts.uk.ui.windows.setShared("KDL058_A_PARAM", {
        yearMonth: moment.utc().format("YYYYMM")
      });
      vm.$window.modal("/view/kdl/058/a/index.xhtml");
    }

    public openSidebarScreen() {
      const vm = this;
      vm.$jump("/view/kmf/002/e/index.xhtml");
    }

    private exportExcel(startDate: string, endDate: string) {
      const vm = this;
      vm.$blockui("grayout");
      vm.saveAsExcel(startDate, endDate)
        .fail(err => vm.$dialog.alert({ messageId: err.messageId }))
        .always(() => vm.$blockui("clear"));
    }

    private saveAsExcel(startDate: string, endDate: string): JQueryPromise<any> {
      const program = nts.uk.ui._viewModel.kiban.programName().split(" ");
      let domainType = "KMF002"+ "休日設定";
      if (program.length > 1) {
        program.shift();
        domainType = domainType + program.join(" ");
      }
      return nts.uk.request.exportFile('/masterlist/report/print',
        {
          domainId: "HolidaySetting",
          domainType: domainType,
          languageId: 'ja', reportType: 0,
          startDate: moment.utc(startDate).format(),
          endDate: moment.utc(endDate).format()
        });
    }
  }
}