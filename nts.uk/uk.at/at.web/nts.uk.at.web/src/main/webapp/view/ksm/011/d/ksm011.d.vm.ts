/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.ksm011.d {
  
  const PATH = {
    getSetting: 'screen/at/ksm/ksm011/d/settingschedule',
      register: "screen/at/ksm/ksm011/d/registersettingschedule"
  }

  @bean()
  class ViewModel extends ko.ViewModel {

    switchItems: KnockoutObservableArray<any>;
    switchItems1: KnockoutObservableArray<any>;
    completionMethod: KnockoutObservableArray<any>;
    conditionWorkSchedule: KnockoutObservableArray<any>;

    targetDate: KnockoutObservable<number> = ko.observable(1);
    deadline: KnockoutObservable<number> = ko.observable(0);
    completionFunction: KnockoutObservable<number> = ko.observable(0);
    alarmCheck: KnockoutObservable<number> = ko.observable(0);
    confirm: KnockoutObservable<number> = ko.observable(0);
    alarmConditionList: KnockoutObservableArray<string> = ko.observableArray([]);
    alarmConditionListText: KnockoutObservable<string> = ko.observable(null);

    selectedCode: KnockoutObservable<string>;

    daysList: KnockoutObservableArray<any>= ko.observableArray([]);
    workDisplay: KnockoutObservable<number> = ko.observable(1);
    abbreviationDisplay: KnockoutObservable<number> = ko.observable(1);
    shiftDisplay: KnockoutObservable<number> = ko.observable(1);
    lastDayDisplay: KnockoutObservable<number> = ko.observable(0);
    _28DayCycle: KnockoutObservable<number> = ko.observable(0);
    displayByDate: KnockoutObservable<number> = ko.observable(0);
    completionExecutionMethod: KnockoutObservable<number> = ko.observable(0);
    personalInforDisplay: KnockoutObservableArray<string> = ko.observableArray([]);

    enableCompletionExecutionMethod: KnockoutComputed<boolean>;
    enableD6_7_1: KnockoutComputed<boolean>;

    constructor(params: any) {
      super();
      const vm = this;

      vm.switchItems = ko.observableArray([
        { code: 1, name: vm.$i18n('KSM011_56') },
        { code: 0, name: vm.$i18n('KSM011_57') }
      ]);

      vm.switchItems1 = ko.observableArray([
        { code: 1, name: vm.$i18n('KSM011_21') },
        { code: 0, name: vm.$i18n('KSM011_22') }
      ]);
      
      vm.completionMethod = ko.observableArray([
        { code: 0, name: vm.$i18n('KSM011_80') },
        { code: 1, name: vm.$i18n('KSM011_81') }
      ]);
      
      vm.conditionWorkSchedule = ko.observableArray([
        // { code: CWSchedule.INSURANCE_STATUS, name: vm.$i18n('CWSchedule_Insurance_Status') },
        { code: CWSchedule.TEAM, name: vm.$i18n('CWSchedule_Team') },
        { code: CWSchedule.RANK, name: vm.$i18n('CWSchedule_Rank') },
        // { code: CWSchedule.QUALIFICATION, name: vm.$i18n('CWSchedule_Qualification') },
        { code: CWSchedule.LICENSE_ATR, name: vm.$i18n('CWSchedule_License') }     
      ]);
      vm.getSetting();
      vm.getDayList();
      vm.alarmCheck.subscribe(value => {
        if (value != 1) {
            $('#KSM011_D6_14').ntsError('clear');
        }
      });
        vm.completionFunction.subscribe(value => {
            if (value != 1) {
                $('#KSM011_D6_14').ntsError('clear');
            }
        });
        vm.completionExecutionMethod.subscribe(value => {
            if (value != 1) {
                $('#KSM011_D6_14').ntsError('clear');
            }
        });

        vm.enableCompletionExecutionMethod = ko.computed(() => {
            return vm.completionFunction() == 1;
        });
        vm.enablePreMethod = ko.computed(() => {
            return vm.completionFunction() == 1 && vm.completionExecutionMethod() == 1;
        });
    }

    mounted() {
      const vm = this;
      $('#KSM011_D24').focus();
    }

    openDialogScreenF() {
      const vm = this;

      vm.$window.modal('/view/ksm/011/f/index.xhtml', vm.alarmConditionList()).then((data) => {
        vm.$blockui('show');        
        if( data ) {
          let conditionListText: Array<string> = data.listItemsSelected.map((i: any) => i.name);
          vm.alarmConditionList(data.listItemsSelected.map((i: any) => i.code));
          vm.alarmConditionListText(_.join(conditionListText, '、'));
          vm.$blockui('hide');
            $('#KSM011_D6_14').ntsError('clear');
        } else 
          vm.$blockui('hide');
      });
    }

    getSetting() {
      const vm = this;
        vm.$blockui('show');
      vm.$ajax(PATH.getSetting).done((data) => {
        if( data ) {
         vm.targetDate(data.initDispMonth);
         vm.deadline(data.endDay);
         vm.workDisplay(data.modeFull);
         vm.abbreviationDisplay(data.modeAbbr);
         vm.shiftDisplay(data.modeShift);
          vm._28DayCycle(data.display28days);
          vm.lastDayDisplay(data.display1month);
          vm.displayByDate(data.openDispByDate);
          // vm.completionFunction(data.useCompletion);
          vm.completionExecutionMethod(data.completionMethod);
          vm.confirm(data.confirmUsage);
          vm.alarmCheck(data.alarmCheckUsage);
          vm.alarmConditionList(data.alarmConditions.map((i: any) => i.code));
          vm.alarmConditionListText(_.join(data.alarmConditions.map((i: any) => i.name), '、'));
          vm.personalInforDisplay(
              data.conditionDisplayControls
                  .filter((i: any) => i.displayCategory == 1)
                  .map((i: any) => ({code: i.conditionATRCode, name: i.conditionATRName}))
          );
        }
      }).fail(error => {
        vm.$dialog.error(error);
      }).always(() => {
          vm.$blockui('hide');
      });
    }

    saveData() {
      const vm = this;

      if( vm.workDisplay() === 0 && vm.shiftDisplay() === 0 && vm.abbreviationDisplay() === 0) {
        vm.$dialog.error({ messageId: 'Msg_2125'}).then(() => {});
        return;
      }

      if (vm.completionFunction() == 1 && vm.completionExecutionMethod() == 1 && vm.alarmCheck() == 1 && _.isEmpty(vm.alarmConditionList())) {
          $('#KSM011_D6_14').ntsError('set', {messageId:'Msg_1690', messageParams:[vm.$i18n("KSM011_87")]});
          return;
      }

      let params = {
          initDispMonth: vm.targetDate(),
          endDay: vm.deadline(),
          modeFull: vm.workDisplay(),
          modeAbbr: vm.abbreviationDisplay(),
          modeShift: vm.shiftDisplay(),
          display28days: vm._28DayCycle(),
          display1month: vm.lastDayDisplay(),
          openDispByDate: vm.displayByDate(),
          useCompletion: vm.completionFunction(),
          completionMethod: vm.completionExecutionMethod(),
          confirmUsage: vm.confirm(),
          alarmCheckUsage: vm.alarmCheck(),
          alarmConditions: vm.alarmConditionList(),
          conditionDisplayControls: vm.personalInforDisplay().map((i: any) => i.code)
      };
        vm.$blockui('show');
       vm.$ajax(PATH.register, params).done(() => {
           vm.$dialog.info({ messageId: 'Msg_15'});
      }).fail(error => {
           if (error.messageId == "Msg_1690" && error.parameterIds && error.parameterIds.indexOf(vm.$i18n("KSM011_87")) >= 0) {
               $('#KSM011_D6_14').ntsError('set', {messageId:'Msg_1690', messageParams:[vm.$i18n("KSM011_87")]});
           } else {
               vm.$dialog.error(error);
           }
      }).always(() => {
           vm.$blockui('hide');
       });
    }

    getDayList(){
      const vm = this;
      vm.daysList = ko.observableArray([]);
      let days = [];
      for( let day = 1; day <= 31; day++) {
        days.push( { day: day, name: day + vm.$i18n('KSM011_105')});
      }
      days.push( { day: 0, name: vm.$i18n('KSM011_106')});
      vm.daysList(days);
    }
  }

  //勤務予定の条件区分（ランク、チーム、介護区分のみ）
  export enum CWSchedule { //Condition Word Schedule
    // 	0:保険加入状況	
    INSURANCE_STATUS = 0, //(0, "保険加入状況"),  CWSchedule_Insurance_Status
    // 	1:チーム		
    TEAM = 1, //(1, "チーム"),    CWSchedule_Team
    //  2:ランク	 
    RANK = 2, //(2, "ランク"), CWSchedule_Rank
    
    // 3:資格		 
    QUALIFICATION = 3,// (3, "資格"), CWSchedule_Qualification
    
    // 4:免許区分 
    LICENSE_ATR = 4 //(4, "免許区分"); CWSchedule_License
  }  
}