/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmf001.n {
  const API = {
    findOne: "at/shared/scherec/leaveCount/get",
    register: "at/shared/scherec/leaveCount/register"
  };
  const LEAVE_TYPE = 3;

  @bean()
  export class ScreenModel extends ko.ViewModel {
    manageDistinctList: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ManageDistinct);
    selectedManageDistinct: KnockoutObservable<number> = ko.observable(0);

    mounted() {
      const vm = this;
      vm.$blockui("grayout");
      vm.$ajax(API.findOne).then((result: WorkDaysNumberOnLeaveCountDto) => {
        const isCounting = !!_.includes(result.countedLeaveList, LEAVE_TYPE);
        vm.selectedManageDistinct(isCounting ? 1 : 0);
        // Fix tabindex
        vm.$nextTick(() => $("#N1_4").attr("tabindex", 1));
      }).always(() => vm.$blockui("clear"));
    }

    public processSave() {
      const vm = this;
      vm.$blockui("grayout");
      const param = {
        isCounting: vm.selectedManageDistinct() === 1,
        leaveType: LEAVE_TYPE
      };
      vm.$ajax(API.register, param).always(() => vm.$dialog.info({ messageId: "Msg_15" })
        .then(() => vm.$blockui("clear")).then(() => vm.processCloseDialog()));
    }

    public processCloseDialog() {
      const vm = this;
      vm.$window.close();
    }
  }

  export interface WorkDaysNumberOnLeaveCountDto {
    // 会社ID
    cid: string;

    // カウントする休暇一覧
    countedLeaveList: Number[];
  }
}