/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kml002.b {

  const PATH = {
    workplaceCounterGetById: 'ctx/at/schedule/budget/workplaceCounter/getById',
    workplaceCounterRegister: 'ctx/at/schedule/budget/workplaceCounter/register',
    getLaborCostTimeDetails: 'ctx/at/schedule/budget/wkpLaborCostAndTime/getById', //screen D
    getTimeZonDetails: 'ctx/at/schedule/budget/wkpTimeZone/getById', //scree E
    getNumberCounterDetails: 'screen/at/kml002/g/getInfo', //screen G
  }

  @bean()
  class ViewModel extends ko.ViewModel {

    laborCostTime: KnockoutObservable<number> = ko.observable(Usage.Use);
    laborCostTimeDetails: KnockoutObservable<any> = ko.observable(null);//人件費・時間	
    countingNumberTimes: KnockoutObservable<number> = ko.observable(Usage.Use);
    countingNumberTimesDetails: KnockoutObservableArray<any> = ko.observableArray(null);//回数集計		
    timeZoneNumberPeople: KnockoutObservable<number> = ko.observable(Usage.Use);
    timeZoneNumberPeopleDetails: KnockoutObservableArray<any> = ko.observableArray(null); //時間帯人数			

    externalBudgetResults: KnockoutObservable<number> = ko.observable(Usage.Use);
    numberPassengersWorkingHours: KnockoutObservable<number> = ko.observable(Usage.Use);
    numberOfEmployees: KnockoutObservable<number> = ko.observable(Usage.Use);
    numberOfPeopleClassified: KnockoutObservable<number> = ko.observable(Usage.Use);
    numberOfPositions: KnockoutObservable<number> = ko.observable(Usage.Use);

    switchOptions: KnockoutObservableArray<any> = ko.observableArray([]);
    functionItems: KnockoutObservableArray<any> = ko.observableArray([]);
    //not setting yet
    laborCostTimeSetting: KnockoutObservable<boolean> = ko.observable(false);
    numberCounterSetting: KnockoutObservable<boolean> = ko.observable(false);
    workplaceTimeZoneSetting: KnockoutObservable<boolean> = ko.observable(false);

    constructor(params: any) {
      super();
      const vm = this;

      vm.switchOptions = ko.observableArray([
        { code: Usage.Use, name: vm.$i18n('KML002_20') },
        { code: Usage.NotUse, name: vm.$i18n('KML002_21') }
      ]);

      vm.createLinkButtonOnRight();

      vm.workplaceCounterGetById();
    }

    created(params: any) {
      const vm = this;
      //_.extend(window, { vm });
    }

    mounted() {
      const vm = this;
      $('#B322').focus();
    }

    openDialogKDL024() {
      const vm = this;

      vm.$window.modal('/view/kdl/024/a/index.xhtml').then(() => {
      });
    }

    openDialogScreenD() {
      const vm = this;
      vm.$window.modal('/view/kml/002/d/index.xhtml').then(() => {
        vm.$window.storage('LABOR_COST_TIME_SETTING').then((data) => {         
          if (!_.isNil(data) && !!data.setting)
            vm.laborCostTimeSetting(data.setting); //true || false || null     
        });
      });
    }

    openDialogScreenG() {
      const vm = this;
      vm.$window.storage('KML002_SCREEN_G_INPUT', { countingType: 0 }).then(() => {
        vm.$window.modal('/view/kml/002/g/index.xhtml').then(() => {
          vm.$window.storage('KML002_SCREEN_G_OUTPUT').then((data) => {           
            if (!_.isNil(data) && !!data.setting)
              vm.numberCounterSetting(data.setting); //true || false || null            
          });
        });
      });
    }

    openDialogScreenE() {
      const vm = this;
      vm.$window.modal('/view/kml/002/e/index.xhtml').then(() => {
        vm.$window.storage('KML002_SCREEN_E_OUTPUT').then((data) => {           
          if (!_.isNil(data) && !!data.setting)
            vm.workplaceTimeZoneSetting(data.setting); //true || false || null            
        });
      });
    }

    registerScheduleRosterInfor() {
      const vm = this;
      /* 
      ・「人件費・時間」の利用区分＝＝利用するが「人件費・時間」の詳細設定はまだ設定られない。
      ・「回数集計」の利用区分＝＝利用するが「回数集計」の詳細設定はまだ設定られない。
      ・「時間帯人数」の利用区分＝＝利用するが「時間帯人数」の詳細設定はまだ設定られない。
      */
      const maxMsg = 3;
      let errorParams = [];

      if (vm.laborCostTime() === Usage.Use && !vm.laborCostTimeSetting()) {
        errorParams.push(vm.$i18n('KML002_119') + vm.$i18n('KML002_18'));
      }

      if (vm.countingNumberTimes() === Usage.Use && !vm.numberCounterSetting()) {
        errorParams.push(vm.$i18n('KML002_119') + vm.$i18n('KML002_27'));
      }

      if (vm.timeZoneNumberPeople() === Usage.Use && !vm.workplaceTimeZoneSetting()) {
        errorParams.push(vm.$i18n('KML002_119') + vm.$i18n('KML002_33'));
      }
     
      let showMsg = [],
        Msg_id: string = errorParams.length > 0 ? 'Msg_1850' : 'Msg_15';
      if (errorParams.length > 0) {
        _.forEach(errorParams, (x, index) => {
          if (x !== '') showMsg.push(x);
        });
        for (let i = showMsg.length; i < maxMsg; i++) {
          showMsg.push('');
        }
      }

      vm.$blockui('show');

      let wpCategory = vm.createParamsToSave();
      let params = { workplaceCategory: wpCategory }
      vm.$ajax(PATH.workplaceCounterRegister, params).done((data) => {
        vm.$dialog.info({ messageId: Msg_id, messageParams: showMsg }).then(() => {
          vm.$blockui('hide');
        });
      })
        .fail()
        .always(() => vm.$blockui('show'));

    }

    /**
     * 
     */
    workplaceCounterGetById() {
      const vm = this;
      vm.$blockui('show');
      vm.$ajax(PATH.workplaceCounterGetById).done((data) => {
        vm.fillDataToGrid(data);
        vm.$blockui('hide');
      })
        .fail(() => vm.$blockui('hide'))
        .always(() => vm.$blockui('hide'));
    }

    fillDataToGrid(data: any) {
      const vm = this;

      if (!_.isNil(data) && data.length > 0) {
        //人件費・時間
        vm.laborCostTime(data[0].use ? Usage.Use : Usage.NotUse);
        vm.laborCostTimeSetting(data[0].setting);
        //外部予算実績
        vm.externalBudgetResults(data[1].use ? Usage.Use : Usage.NotUse);
        //回数集計
        vm.countingNumberTimes(data[2].use ? Usage.Use : Usage.NotUse);
        vm.numberCounterSetting(data[2].setting);
        //就業時間帯別の利用人数
        // vm.numberPassengersWorkingHours(data[3].use ? Usage.Use : Usage.NotUse);
        vm.numberPassengersWorkingHours(Usage.NotUse);
        //時間帯人数
        // vm.timeZoneNumberPeople(data[4].use ? Usage.Use : Usage.NotUse);
        vm.timeZoneNumberPeople(Usage.NotUse);
        vm.workplaceTimeZoneSetting(data[4].setting);
        //雇用人数
        vm.numberOfEmployees(data[5].use ? Usage.Use : Usage.NotUse);
        //分類人数        
        vm.numberOfPeopleClassified(data[6].use ? Usage.Use : Usage.NotUse);
        //職位人数
        vm.numberOfPositions(data[7].use ? Usage.Use : Usage.NotUse);
      }
    }

    createParamsToSave() {
      const vm = this;

      let wpCategory: any = [];
      //人件費・時間
      if (vm.laborCostTime() === Usage.Use) wpCategory.push(0);
      //外部予算実績
      if (vm.externalBudgetResults() === Usage.Use) wpCategory.push(1);
      //回数集計
      if (vm.countingNumberTimes() === Usage.Use) wpCategory.push(2);
      //就業時間帯別の利用人数
      if (vm.numberPassengersWorkingHours() === Usage.Use) wpCategory.push(3);
      //時間帯人数
      if (vm.timeZoneNumberPeople() === Usage.Use) wpCategory.push(4);
      //雇用人数
      if (vm.numberOfEmployees() === Usage.Use) wpCategory.push(5);
      //分類人数
      if (vm.numberOfPeopleClassified() === Usage.Use) wpCategory.push(6);
      //職位人数
      if (vm.numberOfPositions() === Usage.Use) wpCategory.push(7);

      return wpCategory;
    }

    createLinkButtonOnRight() {
      const vm = this;
      let links = [
        { icon: "images/go-out.png", link: '/view/kml/001/a/index.xhtml', text: vm.$i18n('KML002_11') },
        { icon: "images/go-out.png", link: '/view/kmk/009/a/index.xhtml', text: vm.$i18n('KML002_12') },
        // Comment #117983 - Không release
        // { icon: "images/go-out.png", link: '/view/ksu/006/a/index.xhtml', text: vm.$i18n('KML002_117') },
      ];
      _.forEach(links, (item) => {
        vm.functionItems.push({
          ...item, action: function () {
            vm.$jump(item.link);
          }
        });
      });
    }
  }

  export enum Usage {
    NotUse = 0,
    Use = 1
  }
}