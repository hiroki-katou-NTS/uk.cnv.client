/// <reference path='../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.at.view.kdl051.screenModel {
  import getShared = nts.uk.ui.windows.getShared;
  import text = nts.uk.resource.getText;
  import formatById = nts.uk.time.format.byId;

  const API = {
    startPage: "at/shared/holidaysetting/employee/startPage",
    changeId: "at/record/monthly/nursingleave/changeId"
  };
  @bean()
  export class ViewModel extends ko.ViewModel {
    listComponentOption: any;
    employeeList: KnockoutObservableArray<any> = ko.observableArray([]);
    selectedCode: KnockoutObservable<string> = ko.observable('');
    selectedName: KnockoutObservable<string> = ko.observable('');
    code:  KnockoutObservable<string> = ko.observable('');
    error: KnockoutObservable<boolean> = ko.observable(false);
    // A3_8
    limitDays: KnockoutObservable<string> = ko.observable('');
    alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);
    // A3_9
    childNursingUsed: KnockoutObservable<string> = ko.observable('');
    // A3_10
    childNursingRemaining: KnockoutObservable<string> = ko.observable('');
    // A3_2
    nextStartDate: KnockoutObservable<string> = ko.observable('');
    tableDatas: KnockoutObservableArray<any> = ko.observableArray([]);
    tableColumns: KnockoutObservableArray<any> = ko.observableArray([
        { headerText: '', width: 20, key: 'id'},
        { headerText: this.$i18n('KDL051_7'), prop: 'date', width: 150 },
        { headerText: this.$i18n('KDL051_8'), prop: 'periodDate', width: 100 },
        { headerText: this.$i18n('KDL051_9'), prop: 'classification', width: 100 }
      ]);

    created() {
      const vm =this;
      // get Param
      let shareParam = getShared('KDL051A_PARAM');
      let startParam = {
        baseDate: shareParam.baseDate,
        employeeIds: shareParam.employeeIds
      }
      vm.$blockui('grayout');
      // call API startPage
      vm.$ajax(API.startPage, startParam).then((res: any) => {
        if(res && res.lstEmp) {
          let mappedList: Employees[] =
                        _.map(res.lstEmp, (item: any) => {
                            return { id: item.employeeId, code: item.employeeCode, name: item.employeeName };
                        });
          vm.nextStartDate(res.nextStartMonthDay);
          let listSort = _.orderBy(mappedList, ["code"], ["asc"]);
          vm.employeeList(listSort);
          vm.selectedCode(listSort[0].code);
          if (!res.nursingLeaveSt) {
            vm.$dialog.info({messageId: 'Msg_1962'}).then(() => {
              vm.$window.close();
            });
          } else if (res.nursingLeaveSt.manageType !== null && res.nursingLeaveSt.manageType === 0) {
              vm.error(true);
          }
        }
      })
      .always(() => vm.$blockui('clear'));

      vm.alreadySettingList = ko.observableArray([
        { code: '1', isAlreadySetting: true },
        { code: '2', isAlreadySetting: true }
      ]);

      // KCP005
      vm.listComponentOption = {
        isShowAlreadySet: false,
        isMultiSelect: false,
        listType: ListType.EMPLOYEE,
        employeeInputList: vm.employeeList,
        selectType: SelectType.SELECT_BY_SELECTED_CODE,
        selectedCode: vm.selectedCode,
        isDialog: false,
        isShowNoSelectRow: false,
        alreadySettingList: ko.observableArray([]),
        isShowWorkPlaceName: false,
        isShowSelectAllButton: false,
        maxRows: 15
      };

      vm.selectedCode.subscribe(value =>{
        if(value){
          vm.selectedName(_.find(vm.employeeList(), ['code', value]).name);
          let selectedID = (_.find(vm.employeeList(), ['code', value]).id);
          vm.onChangeId(selectedID);
        }
      });
      $('#component-items-list').ntsListComponent(vm.listComponentOption);
    }

    mounted(){
      $('#btnCancel').focus();
    }

    // event when change employee
    public onChangeId(sId : string) {
      const vm = this;
      vm.$blockui('grayout');
      // call API changeId
      vm.$ajax(`${API.changeId}/${sId}`).then((res: any)=>{
        let startdateDays = res.aggrResultOfChildCareNurse.startdateDays;
        vm.limitDays(vm.genDateTime(startdateDays.thisYear.limitDays,0));
        let childNursingUsed = vm.genDateTime(startdateDays.thisYear.usedDays.usedDay, startdateDays.thisYear.usedDays.usedTimes);
        vm.childNursingUsed(childNursingUsed);
        let childNursingRemaining = vm.genDateTime(startdateDays.thisYear.remainingNumber.usedDays, startdateDays.thisYear.remainingNumber.usedTime)
        vm.childNursingRemaining(childNursingRemaining);
        let lstChildCareMana = res.lstChildCareMana
        let mappedList: any[] =
                        _.map(lstChildCareMana, (item: any) => {
                            return { 
                              id: res.lstChildCareMana.indexOf(item) + 1, 
                              date: item.ymd, 
                              periodDate: vm.genDateTime(item.usedDay, item.usedTimes),
                              classification: item.creatorAtr
                            };
                        });
        vm.tableDatas(mappedList);
      })
      .always(() => vm.$blockui('clear'));
    }

    // format data
    public genDateTime(date: number, time: number) {
      if(time) {
        return date + text('KDL051_17') + '  ' + formatById("Clock_Short_HM", time);
      }
      return date + text('KDL051_17')
    }

    public close() {
      const vm = this;
      vm.$window.close();
    }
  }
  export class ListType {
    static EMPLOYMENT = 1;
    static Classification = 2;
    static JOB_TITLE = 3;
    static EMPLOYEE = 4;
  }

  export class SelectType {
    static SELECT_BY_SELECTED_CODE = 1;
    static SELECT_ALL = 2;
    static SELECT_FIRST_ITEM = 3;
    static NO_SELECT = 4;
  }

  export interface UnitAlreadySettingModel {
    code: string;
    isAlreadySetting: boolean;
  }
  
  export class Employees {
    id: string;
    code: string;
    name: string;
    constructor(init?: Partial<Employees>) {
      $.extend(this, init);
    }
  }
}