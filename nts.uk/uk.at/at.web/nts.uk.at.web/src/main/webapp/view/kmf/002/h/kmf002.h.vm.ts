/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.at.view.kmf002.h.viewmodel {

  const PATHS = {
    getWeeklyWorkDay: "at/schedule/shift/weeklywrkday/getAll",
    register: "at/schedule/shift/weeklywrkday/register",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    // select day
    workdayGroup: KnockoutObservableArray<any>;
    workdayValues: KnockoutObservableArray<model.WorkdayPatternItem> = ko.observableArray([]);
    // selectedMon: KnockoutObservable<number> = ko.observable(0);
    // selectedTue: KnockoutObservable<number> = ko.observable(0);
    // selectedWed: KnockoutObservable<number> = ko.observable(0);
    // selectedThu: KnockoutObservable<number> = ko.observable(0);
    // selectedFri: KnockoutObservable<number> = ko.observable(0);
    // selectedSat: KnockoutObservable<number> = ko.observable(0);
    // selectedSun: KnockoutObservable<number> = ko.observable(0);

    constructor() {
      super();
      const vm = this;
      //workdayGroup
      vm.workdayGroup = ko.observableArray([
        { code: 0, name: nts.uk.resource.getText('KMF002_130') },
        { code: 1, name: nts.uk.resource.getText('KMF002_131') },
        { code: 2, name: nts.uk.resource.getText('KMF002_132') }
      ]);
      $("#table-bottom").ntsFixedTable({});
    }

    mounted(): JQueryPromise<any> {
      const vm = this;
      vm.$blockui("grayout");
      return vm.$ajax(PATHS.getWeeklyWorkDay).then((result: model.IWeeklyWorkday) => {
        if (result && result.workdayPatternItemDtoList.length > 0) {
          vm.workdayValues(_.chain(result.workdayPatternItemDtoList).orderBy("dayOfWeek")
            .map(data => new model.WorkdayPatternItem(data)).value());
        }
      }).always(() => vm.$blockui("clear"));
    }

    processSave() {
      const vm = this;
      vm.$blockui("grayout");
      const param = {
        workdayPatternItemDtoList: ko.toJS(vm.workdayValues)
      };
      vm.$ajax(PATHS.register, param).then(() => vm.$dialog.info({ messageId: "Msg_15" }))
        .then(() => vm.closeDialog())
        .always(() => vm.$blockui("clear"));
    }

    closeDialog() {
      const vm = this;
      vm.$window.close();
    }
  }

  export module model {

    export class WorkdayPatternItem {
      dayOfWeek: number;
      workdayDivision: KnockoutObservable<number>;

      constructor(data: IWorkdayPatternItem) {
        this.dayOfWeek = data.dayOfWeek;
        this.workdayDivision = ko.observable(data.workdayDivision);
      }
    }

    export interface IWeeklyWorkday {
      companyId: string;
      workdayPatternItemDtoList: IWorkdayPatternItem[];
    }

    interface IWorkdayPatternItem {
      dayOfWeek: number;
      workdayDivision: number;
    }
  }
}