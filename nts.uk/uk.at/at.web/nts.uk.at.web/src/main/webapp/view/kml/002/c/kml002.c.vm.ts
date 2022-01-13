/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kml002.c {
  const PATH = {
    personalCounterGetById: 'ctx/at/schedule/budget/personalCounter/getById',
    personalCounterRegister: 'ctx/at/schedule/budget/personalCounter/register',
    getNumberCounterDetails: 'screen/at/kml002/g/getInfo', //screen G
  }

  @bean()
  class ViewModel extends ko.ViewModel {

    estimatedMonthlySalary: KnockoutObservable<number> = ko.observable(Usage.Use);
    estimatedAnnualSalary: KnockoutObservable<number> = ko.observable(Usage.Use);
    comparisonStandardWorkingHours: KnockoutObservable<number> = ko.observable(Usage.Use);
    workingTime: KnockoutObservable<number> = ko.observable(Usage.Use);
    nightShiftTime: KnockoutObservable<number> = ko.observable(Usage.Use);
    weeklyHolidayDays: KnockoutObservable<number> = ko.observable(Usage.Use);
    attendanceHolidayDays: KnockoutObservable<number> = ko.observable(Usage.Use);

    count1: KnockoutObservable<number> = ko.observable(Usage.Use);
    count1Details: KnockoutObservableArray<any> = ko.observableArray([]);//回数集計 1 
    count2: KnockoutObservable<number> = ko.observable(Usage.Use);
    count2Details: KnockoutObservableArray<any> = ko.observableArray([]);//回数集計	2	
    count3: KnockoutObservable<number> = ko.observable(Usage.Use);
    count3Details: KnockoutObservableArray<any> = ko.observableArray([]);//回数集計	3	

    switchOptions: KnockoutObservableArray<any> = ko.observableArray([]);
    functionItems: KnockoutObservableArray<any> = ko.observableArray([]);
    numberCounterSetting: KnockoutObservableArray<boolean> = ko.observableArray([]);

    constructor(params: any) {
      super();
      const vm = this;

      vm.switchOptions = ko.observableArray([
        { code: Usage.Use, name: vm.$i18n('KML002_20') },
        { code: Usage.NotUse, name: vm.$i18n('KML002_21') }
      ]);

      vm.numberCounterSetting([false, false, false]);
      vm.createLinkButtonOnRight();
      vm.personalCounterGetById();
    }

    created(params: any) {
      const vm = this;
      //_.extend(window, { vm });
    }

    mounted() {
      const vm = this;

      $('#C322').focus();
    }

    openDialogScreenG(count: number) {
      const vm = this;
      if (count > 3 || count <= 0) count = 1;

      vm.$window.storage('KML002_SCREEN_G_INPUT', { countingType: count }).then(() => {
        vm.$window.modal('/view/kml/002/g/index.xhtml').then(() => {
          vm.$window.storage('KML002_SCREEN_G_OUTPUT').then((data) => {           
            if (!_.isNil(data) && !!data.setting) {
              vm.numberCounterSetting()[count - 1] = data.setting;//true || false || null cls
            }
          });
        });
      });
    }

    openDialogScreenH() {
      const vm = this;     
      // vm.$window.modeless('/view/kml/002/h/index.xhtml');     
      vm.$window.modal('/view/kml/002/h/index.xhtml');
    }

    //スケジュール職場計情報を登録する時
    registerSchedulePersonalInfor() {
      const vm = this;

      //Workplace Total Categor
      /* 
      ・「人件費・時間」の利用区分＝＝利用するが「人件費・時間」の詳細設定はまだ設定られない。
      ・「回数集計」の利用区分＝＝利用するが「回数集計」の詳細設定はまだ設定られない。
      ・「時間帯人数」の利用区分＝＝利用するが「時間帯人数」の詳細設定はまだ設定られない。
      */
      let errorParams = []; const maxMsg = 3;
      let settingList = vm.numberCounterSetting();
      if (vm.count1() === Usage.Use && !settingList[0]) {
        errorParams.push(vm.$i18n('KML002_119') + vm.$i18n('KML002_69'));
      }

      if (vm.count2() === Usage.Use && !settingList[1]) {
        errorParams.push(vm.$i18n('KML002_119') + vm.$i18n('KML002_72'));
      }

      if (vm.count3() === Usage.Use && !settingList[2]) {
        errorParams.push(vm.$i18n('KML002_119') + vm.$i18n('KML002_75'));
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
      let params = { personalCategory: wpCategory };
      vm.$ajax(PATH.personalCounterRegister, params).done(() => {
        vm.$dialog.info({ messageId: Msg_id, messageParams: showMsg }).then(() => {
          vm.$blockui('hide');
        });
      })
        .fail()
        .always();

    }
    /**
     * 
     */
    personalCounterGetById() {
      const vm = this;
      vm.$blockui('show');
      vm.$ajax(PATH.personalCounterGetById).done((data) => {
        vm.fillDataToGrid(data);
        vm.$blockui('hide');
      })
        .fail()
        .always(() => vm.$blockui('hide'));    
    }    

    fillDataToGrid(data: any) {
      const vm = this;

      if (!_.isNil(data) && data.length > 0) {
        //月間想定給与額
        vm.estimatedMonthlySalary(data[0].use ? Usage.Use : Usage.NotUse);
        //年間想定給与額
        vm.estimatedAnnualSalary(Usage.NotUse);
        //基準労働時間比較
        // vm.comparisonStandardWorkingHours(data[2].use ? Usage.Use : Usage.NotUse);
        vm.comparisonStandardWorkingHours(Usage.NotUse);
        //労働時間
        vm.workingTime(data[3].use ? Usage.Use : Usage.NotUse);
        //夜勤時間
        // vm.nightShiftTime(data[4].use ? Usage.Use : Usage.NotUse);
        vm.nightShiftTime(Usage.NotUse);
        //週間休日日数
        // vm.weeklyHolidayDays(data[5].use ? Usage.Use : Usage.NotUse);
        vm.weeklyHolidayDays(Usage.NotUse);
        //出勤・休日日数
        vm.attendanceHolidayDays(data[6].use ? Usage.Use : Usage.NotUse);
        //回数集計１
        vm.count1(data[7].use ? Usage.Use : Usage.NotUse);
        vm.numberCounterSetting()[0] = data[7].setting;
        //回数集計2
        vm.count2(data[8].use ? Usage.Use : Usage.NotUse);
        vm.numberCounterSetting()[1] = data[8].setting;
        //回数集計3
        vm.count3(data[9].use ? Usage.Use : Usage.NotUse);
        vm.numberCounterSetting()[2] = data[9].setting;
      }
    }

    createParamsToSave() {
      const vm = this;

      let wpCategory: any = [];

      if (vm.estimatedMonthlySalary() === Usage.Use) wpCategory.push(0); //月間想定給与額      
      if (vm.estimatedAnnualSalary() === Usage.Use) wpCategory.push(1); //年間想定給与額      
      if (vm.comparisonStandardWorkingHours() === Usage.Use) wpCategory.push(2); //基準労働時間比較
      if (vm.workingTime() === Usage.Use) wpCategory.push(3); //労働時間      
      if (vm.nightShiftTime() === Usage.Use) wpCategory.push(4); //夜勤時間
      if (vm.weeklyHolidayDays() === Usage.Use) wpCategory.push(5); //週間休日日数
      if (vm.attendanceHolidayDays() === Usage.Use) wpCategory.push(6); //出勤・休日日数
      if (vm.count1() === Usage.Use) wpCategory.push(7); //回数集計１
      if (vm.count2() === Usage.Use) wpCategory.push(8); //回数集計3
      if (vm.count3() === Usage.Use) wpCategory.push(9); //回数集計3

      return wpCategory;
    }

    createLinkButtonOnRight() {
      const vm = this;
      let links = [
        { icon: "images/go-out.png", link: '/view/kml/001/a/index.xhtml', text: vm.$i18n('KML002_11') },
        { icon: "images/go-out.png", link: '/view/kmf/002/g/index.xhtml', text: vm.$i18n('KML002_45') },
        // { icon: "images/go-out.png", link: '/view/ksm/001/a/index.xhtml', text: vm.$i18n('KML002_46') },
        //{ icon: "images/go-out.png", link: '#', text: vm.$i18n('KML002_118') },
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