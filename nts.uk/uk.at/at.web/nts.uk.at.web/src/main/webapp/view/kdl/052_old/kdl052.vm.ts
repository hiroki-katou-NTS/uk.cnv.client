/// <reference path='../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.at.view.kdl052.screenModel {

  import setShared = nts.uk.ui.windows.setShared;
  import getShared = nts.uk.ui.windows.getShared;
  import formatById = nts.uk.time.format.byId;
  const API = {
    startPage: "ctx/at/share/vacation/setting/nursingleave/find/childnursingleave",
    changeId: "at/record/monthly/nursingleave/changeIdKdl052"
  };
  @bean()
  export class ViewModel extends ko.ViewModel {
    listComponentOption: any;
    selectedCode: KnockoutObservable<string> = ko.observable('');
    alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);
    employeeList: KnockoutObservableArray<EmployeeModel> = ko.observableArray([]);
    baseDate: any;
    selectedName: KnockoutObservable<string> = ko.observable('');
    manageDisabled: KnockoutObservable<boolean> = ko.observable(true);
    nextStartDate:  KnockoutObservable<string> = ko.observable('');
    // A3_8
    limitDays: KnockoutObservable<string> = ko.observable('');
    // A3_9
    childNursingUsed: KnockoutObservable<string> = ko.observable('');
    // A3_10
    childNursingRemaining: KnockoutObservable<string> = ko.observable('');
    // Value for ntsGridList A2_4
    currentCode: KnockoutObservable<any> = ko.observable('');
    tableDatas: KnockoutObservableArray<any> = ko.observableArray([]);
    tableColumns: KnockoutObservableArray<any> = ko.observableArray([
      { headerText: '', width: 25 , template:'<div style = "background-color: #CFF1A5;">${id}</div>', key: 'id'},
      { headerText: this.$i18n('KDL052_7'), prop: 'date', width: 112 },
      { headerText: this.$i18n('KDL052_8'), prop: 'periodDate', width: 95 },
      { headerText: this.$i18n('KDL052_9'), prop: 'classification', width: 95 }
    ]);

    created(params: any) {
      let vm = this;
      vm.getData(params);
      vm.selectedCode = ko.observable('')
      vm.baseDate = ko.observable(new Date());
      vm.selectedCode = ko.observable('1');
      vm.alreadySettingList = ko.observableArray([
        {code: '1', isAlreadySetting: true},
        {code: '2', isAlreadySetting: true}
      ]);
      
      vm.listComponentOption = {
        isShowAlreadySet: false,
        isMultiSelect: false,
        listType: ListType.EMPLOYEE,
        employeeInputList: vm.employeeList,
        selectType: SelectType.SELECT_BY_SELECTED_CODE,
        selectedCode: vm.selectedCode,
        isDialog: false,
        isShowNoSelectRow: false,
        alreadySettingList: vm.alreadySettingList,
        isShowWorkPlaceName: false,
        isShowSelectAllButton: false,
        disableSelection : false
      };

      vm.selectedCode.subscribe(value =>{
        if(value){
          let selectedID = (_.find(vm.employeeList(), ['code', value]).id);
          vm.selectedName(_.find(vm.employeeList(), ['code', value]).name);
          vm.onChangeId(selectedID);
        }
      });
    }

    mounted(){
      $('#btnCancel').focus();
    }

    getData(params: any) {
      const vm = this
          , command = {
            lstEmployees: params.employeeIds,
            baseDate: moment.utc(params.baseDate, 'YYYYMMDD').toISOString()
          }
      vm.$blockui('grayout');
      // Call api get data
      vm.$ajax(API.startPage, command).then((result: any) => {
        if (result && result.lstEmployee) {
            if (result && result.managementClassification) {
              if (result.managementClassification.manageType === 1) {
                vm.manageDisabled(true);
              } else {
                vm.manageDisabled(false);
              }
            } else {
              vm.$dialog.info({messageId: 'Msg_1962'}).then(() => {
                vm.$window.close();
              });
            }
            let mappedList =
                _.map(result.lstEmployee, (item: any) => {
                    return { id: item.employeeId, code: item.employeeCode, name: item.employeeName };
                });
            let listSort = _.orderBy(mappedList, ["code"], ["asc"]);
            vm.employeeList(listSort);

            if (!_.isEmpty(listSort)) {
              vm.selectedCode(listSort[0].code);
            }
            vm.nextStartDate(result.nextStartDate)
            $('#component-items-list').ntsListComponent(vm.listComponentOption);
        
        }
        
      })
      .always(() => vm.$blockui('clear'));
    }

    // event when change employee
    public onChangeId(sId : string) {
      const vm = this;
      vm.$blockui('grayout');
      // call API changeId
      vm.$ajax(`${API.changeId}/${sId}`).then((res: any) => {
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
      const vm = this;
      if(time) {
        return date + this.$i18n('KDL051_17') + '  ' + formatById("Clock_Short_HM", time);
      }
      return date + vm.$i18n('KDL051_17');
    }



    close() {
      this.$window.close();
    }
  }

  export class NtsGridListColumn {
    headerText?: string;
    prop?: string;
    width?: number;
    hidden?: boolean;
  }

  export class ItemModel {
    date: any;
    day: any;
    note: string;

    constructor(init?: Partial<ItemModel>) {
      $.extend(this, init);
    }
  }

  export class ListType {
    static EMPLOYMENT = 1;
    static Classification = 2;
    static JOB_TITLE = 3;
    static EMPLOYEE = 4;
  }

  export class EmployeeModel {
    id?: string;
    code?: string;
    name?: string;
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
}