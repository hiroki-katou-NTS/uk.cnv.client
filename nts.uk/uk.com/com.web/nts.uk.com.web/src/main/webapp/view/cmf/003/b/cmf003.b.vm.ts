module nts.uk.com.view.cmf003.b {

  import ScheduleBatchCorrectSettingSave = service.model.ScheduleBatchCorrectSettingSave;
  import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
  import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
  import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
  import model = cmf003.share.model;
  import getText = nts.uk.resource.getText;
  import alertError = nts.uk.ui.dialog.alertError;
  import block = nts.uk.ui.block;
  import setShared = nts.uk.ui.windows.setShared;
  import getShared = nts.uk.ui.windows.getShared;
  import modal = nts.uk.ui.windows.sub.modal;

  export module viewmodel {
    export class ScreenModel {

      buton_E_enable: KnockoutObservable<boolean> = ko.observable(true);

      // systemtype from C
      systemtypeFromC: KnockoutObservable<ItemCombobox>;

      // check compress password
      isCompressPass: KnockoutObservable<boolean>;

      // check password constraint 
      passwordConstraint: KnockoutObservable<string>;

      // check compress password
      isSymbol: KnockoutObservable<boolean>;

      // reference date
      referenceDate: KnockoutObservable<string> = ko.observable('');

      //input
      password: KnockoutObservable<string> = ko.observable('');
      confirmPassword: KnockoutObservable<string> = ko.observable('');
      explanation: KnockoutObservable<string> = ko.observable('');
      dataSaveSetName: KnockoutObservable<string> = ko.observable('');

      //wizard
      stepList: Array<NtsWizardStep> = [];
      //stepSelected: KnockoutObservable<NtsWizardStep>;
      activeStep: KnockoutObservable<number>;

      //gridlist
      categorys: KnockoutObservableArray<Category>;
      columnCategorys: KnockoutObservableArray<NtsGridListColumn>;
      columnEmployees: KnockoutObservableArray<NtsGridListColumn>;
      currentCode: KnockoutObservable<any>;
      currentCodeList: KnockoutObservableArray<any>;

      //          itemTitleAtr: KnockoutObservableArray<any>;
      //          selectedTitleAtr: KnockoutObservable<number>;
      //Date Ranger Picker : type fullDay
      dayEnable: KnockoutObservable<boolean>;
      dayRequired: KnockoutObservable<boolean>;
      dayValue: KnockoutObservable<any>;
      dayStartDateString: KnockoutObservable<string>;
      dayEndDateString: KnockoutObservable<string>;

      //Date Ranger Picker : type monthyear
      monthEnable: KnockoutObservable<boolean>;
      monthRequired: KnockoutObservable<boolean>;
      monthValue: KnockoutObservable<any>;
      monthStartDateString: KnockoutObservable<string>;
      monthEndDateString: KnockoutObservable<string>;

      //Date Ranger Picker : type year
      yearEnable: KnockoutObservable<boolean>;
      yearRequired: KnockoutObservable<boolean>;
      yearValue: KnockoutObservable<any>;
      yearStartDateString: KnockoutObservable<string>;
      yearEndDateString: KnockoutObservable<string>;

      //Help Button
      enable: KnockoutObservable<boolean>;

      //Radio button
      itemTitleAtr: KnockoutObservableArray<any>;
      selectedTitleAtr: KnockoutObservable<number> = ko.observable();

      ccgcomponent: GroupOption;

      // Options
      isQuickSearchTab: KnockoutObservable<boolean>;
      isAdvancedSearchTab: KnockoutObservable<boolean>;
      isAllReferableEmployee: KnockoutObservable<boolean>;
      isOnlyMe: KnockoutObservable<boolean>;
      isEmployeeOfWorkplace: KnockoutObservable<boolean>;
      isEmployeeWorkplaceFollow: KnockoutObservable<boolean>;
      isMutipleCheck: KnockoutObservable<boolean>;
      isSelectAllEmployee: KnockoutObservable<boolean>;
      periodStartDate: KnockoutObservable<moment.Moment>;
      periodEndDate: KnockoutObservable<moment.Moment>;
      baseDate: KnockoutObservable<moment.Moment>;
      selectedEmployee: KnockoutObservableArray<EmployeeSearchDto>;
      showEmployment: KnockoutObservable<boolean>; // 雇用条件
      showWorkplace: KnockoutObservable<boolean>; // 職場条件
      showClassification: KnockoutObservable<boolean>; // 分類条件
      showJobTitle: KnockoutObservable<boolean>; // 職位条件
      showWorktype: KnockoutObservable<boolean>; // 勤種条件
      inService: KnockoutObservable<boolean>; // 在職区分
      leaveOfAbsence: KnockoutObservable<boolean>; // 休職区分
      closed: KnockoutObservable<boolean>; // 休業区分
      retirement: KnockoutObservable<boolean>; // 退職区分
      systemType: KnockoutObservable<number>;
      showClosure: KnockoutObservable<boolean>; // 就業締め日利用
      showBaseDate: KnockoutObservable<boolean>; // 基準日利用
      showAllClosure: KnockoutObservable<boolean>; // 全締め表示
      showPeriod: KnockoutObservable<boolean>; // 対象期間利用
      periodFormatYM: KnockoutObservable<boolean>; // 対象期間精度

      selectedImplementAtrCode: KnockoutObservable<number>;
      checkReCreateAtrAllCase: KnockoutObservable<boolean>;
      checkReCreateAtrOnlyUnConfirm: KnockoutObservable<boolean>;
      checkProcessExecutionAtrRebuild: KnockoutObservable<boolean>;
      checkProcessExecutionAtrReconfig: KnockoutObservable<boolean>;
      checkCreateMethodAtrPersonalInfo: KnockoutObservable<boolean>;
      checkCreateMethodAtrPatternSchedule: KnockoutObservable<boolean>;
      checkCreateMethodAtrCopyPastSchedule: KnockoutObservable<boolean>;
      resetWorkingHours: KnockoutObservable<boolean>;
      resetDirectLineBounce: KnockoutObservable<boolean>;
      resetMasterInfo: KnockoutObservable<boolean>;
      resetTimeChildCare: KnockoutObservable<boolean>;
      resetAbsentHolidayBusines: KnockoutObservable<boolean>;
      resetTimeAssignment: KnockoutObservable<boolean>;
      workTypeInfo: KnockoutObservable<string>;
      workTypeCode: KnockoutObservable<string>;
      workTimeInfo: KnockoutObservable<string>;
      workTimeCode: KnockoutObservable<string>;

      periodDate: KnockoutObservable<any>;
      startDateString: KnockoutObservable<string>;
      endDateString: KnockoutObservable<string>;

      lstLabelInfomation: KnockoutObservableArray<string>;
      infoCreateMethod: KnockoutObservable<string>;
      infoPeriodDate: KnockoutObservable<string>;
      lengthEmployeeSelected: KnockoutObservable<string>;

      // Employee tab
      lstPersonComponentOption: any;
      selectedEmployeeCode: KnockoutObservableArray<string>;
      employeeName: KnockoutObservable<string>;
      employeeList: KnockoutObservableArray<TargetEmployee>;
      targetEmployee: KnockoutObservableArray<TargetEmployee>;
      alreadySettingPersonal: KnockoutObservableArray<UnitAlreadySettingModel>;
      ccgcomponentPerson: GroupOption;

      //for control field
      isReCreate: KnockoutObservable<boolean>;
      isReSetting: KnockoutObservable<boolean>;


      // date
      date: KnockoutObservable<string>;
      maxDaysCumulationByEmp: KnockoutObservable<number>;

      //Screen B ver27
      patternList: KnockoutObservableArray<Pattern> = ko.observableArray([]);
      patternColumns: KnockoutObservableArray<any> = ko.observableArray([
        { headerText: getText('CMF003_23'), key: 'displayCode', width: 75 },
        { headerText: getText('CMF003_632'), key: 'patternName', width: 185 }
      ]);
      systemTypes: KnockoutObservableArray<number> = ko.observableArray([]);
      selectedPatternId: KnockoutObservable<string> = ko.observable('');
      savedName: KnockoutObservable<string> = ko.observable('');
      nextButtonText: KnockoutObservable<string> = ko.observable('');

      constructor() {
        var self = this;

        self.isCompressPass = ko.observable(false);
        self.isSymbol = ko.observable(false);
        self.passwordConstraint = ko.observable("");

        self.isCompressPass.subscribe(function (value) {
          if (value) {
            self.passwordConstraint("FileCompressionPassword");
            $(".passwordInput").trigger("validate");
          } else {
            (nts.uk.util as any).value.reset($("#B4_32"), $("#B4_32").val());
            (nts.uk.util as any).value.reset($("#B4_41"), $("#B4_41").val());
            self.passwordConstraint("");
            $('.passwordInput').ntsError('clear');
          }
        });

        self.stepList = [
          { content: '.step-1' },
          { content: '.step-2' },
          { content: '.step-3' }
        ];
        self.activeStep = ko.observable(0);
        //self.stepSelected = ko.observable({ id: 'step-1', content: '.step-1' });

        //gridlist
        this.categorys = ko.observableArray([]);

        //referenceDate init toDay
        self.referenceDate(moment.utc().format("YYYY/MM/DD"));

        this.columnCategorys = ko.observableArray([
          { headerText: '', key: 'id', hidden: true },
          { headerText: getText('CMF003_30'), key: 'displayName', width: 250 },
          { headerText: getText('CMF003_31'), formatter: timeStore, key: 'periodDivision', width: 100 },
          { headerText: getText('CMF003_32'), formatter: storageRangeSaved, key: 'storeRange', width: 120 }
        ]);

        this.columnEmployees = ko.observableArray([
          { headerText: '', key: 'categoryId', width: 100, hidden: true },
          { headerText: getText('CMF003_163'), key: 'code', width: 130 },
          { headerText: getText('CMF003_164'), key: 'businessname', width: 270 }
        ]);

        self.itemTitleAtr = ko.observableArray([
          { value: 0, titleAtrName: resource.getText('CMF003_88') },
          { value: 1, titleAtrName: resource.getText('CMF003_89') }]);
        self.selectedTitleAtr = ko.observable(0);
        this.currentCode = ko.observable();
        this.currentCodeList = ko.observableArray([]);

        //Date Ranger Picker : type full day
        self.dayEnable = ko.observable(true);
        self.dayRequired = ko.observable(false);
        self.dayStartDateString = ko.observable("");
        self.dayEndDateString = ko.observable("");
        self.dayValue = ko.observable({
          startDate: moment.utc().subtract(1, "M").add(1, "d").format("YYYY/MM/DD"),
          endDate: moment.utc().format("YYYY/MM/DD")
        });

        self.dayStartDateString.subscribe(function (value) {
          self.dayValue().startDate = value;
          self.dayValue.valueHasMutated();
        });

        self.dayEndDateString.subscribe(function (value) {
          self.dayValue().endDate = value;
          self.dayValue.valueHasMutated();
        });

        //Date Ranger Picker : type month
        self.monthEnable = ko.observable(true);
        self.monthRequired = ko.observable(false);
        self.monthStartDateString = ko.observable("");
        self.monthEndDateString = ko.observable("");
        self.monthValue = ko.observable({ startDate: moment.utc().format("YYYY/MM"), endDate: moment.utc().format("YYYY/MM") });

        self.monthStartDateString.subscribe(function (value) {
          self.monthValue().startDate = value;
          self.monthValue.valueHasMutated();
        });

        self.monthEndDateString.subscribe(function (value) {
          self.monthValue().endDate = value;
          self.monthValue.valueHasMutated();
        });

        //Date Ranger Picker : type year
        self.yearEnable = ko.observable(true);
        self.yearRequired = ko.observable(false);
        self.yearStartDateString = ko.observable("");
        self.yearEndDateString = ko.observable("");
        self.yearValue = ko.observable({ startDate: moment.utc().format("YYYY"), endDate: moment.utc().format("YYYY") });

        self.yearStartDateString.subscribe(function (value) {
          self.yearValue().startDate = value;
          self.yearValue.valueHasMutated();
        });

        self.yearEndDateString.subscribe(function (value) {
          self.yearValue().endDate = value;
          self.yearValue.valueHasMutated();
        });

        self.dayValue.subscribe(function (value) {
          nts.uk.ui.errors.clearAll();
          $(".form-B").trigger("validate");
          $(".form-B .ntsDatepicker").trigger("validate");
        });

        self.monthValue.subscribe(function (value) {
          nts.uk.ui.errors.clearAll();
          $(".form-B").trigger("validate");
          $(".form-B .ntsDatepicker").trigger("validate");
        });

        self.yearValue.subscribe(function (value) {
          nts.uk.ui.errors.clearAll();
          $(".form-B").trigger("validate");
          $(".form-B .ntsDatepicker").trigger("validate");
        });

        //Defaut D4_7
        self.dateDefaut = ko.observable("2018/04/19");

        //Help Button
        self.enable = ko.observable(true);
        self.textHelp = ko.observable("”基準日説明画像”");

        //Radio button
        //                self.itemTitleAtr = ko.observableArray([
        //                    { value: 0, titleAtrName: resource.getText('CMF003_88') },
        //                    { value: 1, titleAtrName: resource.getText('CMF003_89') }]);
        //                self.selectedTitleAtr = ko.observable(0);
        self.enableGrid = ko.observable(false);
        $("#titleSeach").prop("disabled", true);
        _.defer(function () {
          $(".ntsSearchBox").prop('disabled', true);
        });

        // dump
        self.date = ko.observable('20181231');
        self.systemTypes = ko.observableArray([
          { name: 'システム管理者', value: 1 }, // PERSONAL_INFORMATION
          { name: '就業', value: 2 } // EMPLOYMENT
        ]);
        self.selectedEmployee = ko.observableArray([]);

        // initial ccg options
        self.setDefaultCcg001Option();

        // Init component.
        self.reloadCcg001();

        self.periodFormatYM.subscribe(item => {
          if (item) {
            self.showClosure(true);
          }
        });

        self.startDateString = ko.observable("20180101");
        self.endDateString = ko.observable("20181230");
        self.selectedEmployeeCode = ko.observableArray([]);
        self.alreadySettingPersonal = ko.observableArray([]);
        self.maxDaysCumulationByEmp = ko.observable(0);
        self.periodDate = ko.observable({
          startDate: self.startDateString(),
          endDate: self.endDateString()
        });
        self.checkReCreateAtrOnlyUnConfirm = ko.observable(false);
        self.checkReCreateAtrAllCase = ko.observable(true);
        self.checkProcessExecutionAtrRebuild = ko.observable(true);
        self.checkProcessExecutionAtrReconfig = ko.observable(false);
        self.resetWorkingHours = ko.observable(false);
        self.resetDirectLineBounce = ko.observable(false);
        self.resetMasterInfo = ko.observable(false);
        self.resetTimeChildCare = ko.observable(false);
        self.resetAbsentHolidayBusines = ko.observable(false);
        self.resetTimeAssignment = ko.observable(false);
        self.checkCreateMethodAtrPersonalInfo = ko.observable(true);
        self.checkCreateMethodAtrPatternSchedule = ko.observable(false);
        self.checkCreateMethodAtrCopyPastSchedule = ko.observable(false);
        self.employeeList = ko.observableArray([]);
        self.targetEmployee = ko.observableArray([]);
        self.workTypeInfo = ko.observable('');
        self.workTypeCode = ko.observable('');
        self.workTimeInfo = ko.observable('');
        self.workTimeCode = ko.observable('');

        self.startDateString.subscribe(function (value) {
          self.periodDate().startDate = value;
          self.periodDate.valueHasMutated();
        });

        self.endDateString.subscribe(function (value) {
          self.periodDate().endDate = value;
          self.periodDate.valueHasMutated();
        });

        self.isCompressPass.subscribe(value => {
          if (!value) {
            self.password('');
            self.confirmPassword('');
          }
        })

        //KCP005
        self.lstPersonComponentOption = {
          isShowAlreadySet: false,
          isMultiSelect: true,
          listType: ListType.EMPLOYEE,
          employeeInputList: self.employeeList,
          selectType: SelectType.SELECT_ALL,
          selectedCode: self.selectedEmployeeCode,
          isDialog: false,
          isShowNoSelectRow: false,
          alreadySettingList: self.alreadySettingPersonal,
          isShowWorkPlaceName: true,
          isShowSelectAllButton: false,
          maxWidth: 550,
          maxRows: 15,
          disableSelection: true
        };

        self.selectedTitleAtr.subscribe(function (value) {
          self.lstPersonComponentOption.disableSelection = value == 0 ? true : false;
          $('#employeeSearch').ntsListComponent(self.lstPersonComponentOption);
          self.nextButtonText(value === 0 ? getText('CMF003_171') : getText('CMF003_53'));
        });
        self.selectedTitleAtr(0);
        self.selectedTitleAtr.valueHasMutated();

        self.selectedPatternId.subscribe(value => {
          self.selectPattern(value);
        });

      }//end constructor

      /**
       * Set default ccg001 options
       */
      public setDefaultCcg001Option(): void {
        let self = this;
        self.isQuickSearchTab = ko.observable(true);
        self.isAdvancedSearchTab = ko.observable(true);
        self.isAllReferableEmployee = ko.observable(true);
        self.isOnlyMe = ko.observable(true);
        self.isEmployeeOfWorkplace = ko.observable(true);
        self.isEmployeeWorkplaceFollow = ko.observable(true);
        self.isMutipleCheck = ko.observable(true);
        self.isSelectAllEmployee = ko.observable(true);
        self.baseDate = ko.observable(moment());
        self.periodStartDate = ko.observable(moment());
        self.periodEndDate = ko.observable(moment());
        self.showEmployment = ko.observable(false); // 雇用条件
        self.showWorkplace = ko.observable(true); // 職場条件
        self.showClassification = ko.observable(true); // 分類条件
        self.showJobTitle = ko.observable(true); // 職位条件
        self.showWorktype = ko.observable(true); // 勤種条件
        self.inService = ko.observable(true); // 在職区分
        self.leaveOfAbsence = ko.observable(true); // 休職区分
        self.closed = ko.observable(true); // 休業区分
        self.retirement = ko.observable(true); // 退職区分
        self.systemType = ko.observable(1);
        self.showClosure = ko.observable(false); // 就業締め日利用
        self.showBaseDate = ko.observable(true); // 基準日利用
        self.showAllClosure = ko.observable(false); // 全締め表示
        self.showPeriod = ko.observable(false); // 対象期間利用
        self.periodFormatYM = ko.observable(false); // 対象期間精度
      }

      /**
       * Reload component CCG001
       */
      public reloadCcg001(): void {
        var self = this;
        var periodStartDate, periodEndDate: string;
        if (self.showBaseDate()) {
          periodStartDate = moment(self.periodStartDate()).format("YYYY-MM-DD");
          periodEndDate = moment(self.periodEndDate()).format("YYYY-MM-DD");
        } else {
          periodStartDate = moment(self.periodStartDate()).format("YYYY-MM");
          periodEndDate = moment(self.periodEndDate()).format("YYYY-MM"); // 対象期間終了日
        }

        if (!self.showBaseDate() && !self.showClosure() && !self.showPeriod()) {
          nts.uk.ui.dialog.alertError("Base Date or Closure or Period must be shown!");
          return;
        }
        self.ccgcomponent = {
          /** Common properties */
          systemType: self.systemType(), // システム区分
          showEmployeeSelection: false, // 検索タイプ
          showQuickSearchTab: self.isQuickSearchTab(), // クイック検索
          showAdvancedSearchTab: self.isAdvancedSearchTab(), // 詳細検索
          showBaseDate: self.showBaseDate(), // 基準日利用
          showClosure: self.showClosure(), // 就業締め日利用
          showAllClosure: self.showAllClosure(), // 全締め表示
          showPeriod: self.showPeriod(), // 対象期間利用
          periodFormatYM: self.periodFormatYM(), // 対象期間精度

          /** Required parameter */
          baseDate: moment(self.baseDate()).format("YYYY-MM-DD"), // 基準日
          periodStartDate: periodStartDate, // 対象期間開始日
          periodEndDate: periodEndDate, // 対象期間終了日
          inService: self.inService(), // 在職区分
          leaveOfAbsence: self.leaveOfAbsence(), // 休職区分
          closed: self.closed(), // 休業区分
          retirement: self.retirement(), // 退職区分

          /** Quick search tab options */
          showAllReferableEmployee: self.isAllReferableEmployee(), // 参照可能な社員すべて
          showOnlyMe: self.isOnlyMe(), // 自分だけ
          showSameWorkplace: self.isEmployeeOfWorkplace(), // 同じ職場の社員
          showSameWorkplaceAndChild: self.isEmployeeWorkplaceFollow(), // 同じ職場とその配下の社員

          /** Advanced search properties */
          showEmployment: self.showEmployment(), // 雇用条件
          showWorkplace: self.showWorkplace(), // 職場条件
          showClassification: self.showClassification(), // 分類条件
          showJobTitle: self.showJobTitle(), // 職位条件
          showWorktype: self.showWorktype(), // 勤種条件
          isMutipleCheck: self.isMutipleCheck(), // 選択モード

          /** Return data */
          returnDataFromCcg001: function (data: Ccg001ReturnedData) {
            self.selectedEmployee(data.listEmployee);
            self.applyKCP005ContentSearch(data.listEmployee).done(() => {
              setTimeout(function () {
                $("#employeeSearch div[id *= 'scrollContainer']").scrollTop(0);
              }, 1000);
            });
            self.referenceDate(moment.utc(data.baseDate).format("YYYY/MM/DD"));
          }
        }
        //$('#ccgcomponent').ntsGroupComponent(self.ccgcomponent);
      }

      /**
     * start page data 
     */
      public startPage(): JQueryPromise<any> {
        let self = this;
        let dfd = $.Deferred();
        setTimeout(function () { dfd.resolve(self); }, 100);
        self.initScreenB();
        return dfd.promise();
      }
      /**
      * apply ccg001 search data to kcp005
      */
      public applyKCP005ContentSearch(dataList: EmployeeSearchDto[]): JQueryPromise<any> {
        var self = this;
        var dfd = $.Deferred();
        var employeeSearchs: TargetEmployee[] = [];
        for (var i = 0; i < dataList.length; i++) {
          let employeeSearch = dataList[i];
          let employee: UnitModel = {
            code: employeeSearch.employeeCode,
            name: employeeSearch.employeeName,
            affiliationName: employeeSearch.affiliationName,
            sid: employeeSearch.employeeId,
            scd: employeeSearch.employeeCode,
            businessname: employeeSearch.employeeName
          };
          employeeSearchs.push(employee);

          if (i == (dataList.length - 1)) {
            dfd.resolve();
          }
        }

        self.employeeList(employeeSearchs);
        self.lstPersonComponentOption.disableSelection = self.selectedTitleAtr() == 0 ? true : false;
        $('#employeeSearch').ntsListComponent(self.lstPersonComponentOption);

        return dfd.promise();
      }

      /**
       * convert string to date
       */
      private toDate(strDate: string): Date {
        return moment(strDate, 'YYYY/MM/DD').toDate();
      }

      /**
       * function submit button
       */
      private btnSubmitClick() {
        let self = this;
        // check selection employee 
        if (self.selectedEmployeeCode && self.selectedEmployee() && self.selectedEmployeeCode().length > 0) {

        } else {
          nts.uk.ui.dialog.error({ messageId: "Msg_498", messageParams: ["X", "Y"] });
        }


      }

      /**
       * get selected title name from screen B
       */
      public getSelectedTitleName(): string {
        const self = this;
        return self.itemTitleAtr().filter(data => data.value === self.selectedTitleAtr()).pop().titleAtrName;
      }

      /**
       * function next wizard by on click button 
       */
      private next() {
        const self = this;
        if (self.selectedTitleAtr() === 1)
          $('#ex_accept_wizard').ntsWizard("next");
        else {
          self.initE();
          self.gotoscreenF();
        }
      }
      /**
       * function previous wizard by on click button 
       */
      private previous() {
        $('#ex_accept_wizard').ntsWizard("prev");
      }
      /**
      * function previous wizard by on click button 
      */
      private nextPageEmployee(): void {
        var self = this;

        // if (self.validateB()) {
        //   if (self.isCompressPass()) {
        //     if (self.password() == self.confirmPassword()) {
        //       if (self.categorys().length > 0) {
        self.next();
        $(".selectEmpType").focus();
        //       } else {
        //         alertError({ messageId: 'Msg_471' });
        //       }
        //     } else {
        //       alertError({ messageId: 'Msg_566' });
        //     }
        //   } else {
        //     if (self.categorys().length > 0) {
        //       self.next();
        //       $(".selectEmpType").focus();
        //     } else {
        //       alertError({ messageId: 'Msg_471' });
        //     }
        //   }
        // }
      }

      private setRangePickerRequire(): void {
        let self = this;

        self.dayRequired(false);
        self.monthRequired(false);
        self.yearRequired(false);
        for (var i = 0; i < self.categorys().length; i++) {
          if (self.categorys()[i].periodDivision == 1) {
            self.dayRequired(true);
          }
          else if (self.categorys()[i].periodDivision == 2) {
            self.monthRequired(true);
          }
          else if (self.categorys()[i].periodDivision == 3) {
            self.yearRequired(true);
          }
          else if (self.categorys()[i].periodDivision == 0) {
            $('.form-B .ntsDatepicker').ntsError('clear');
          }
        }
      }

      private initScreenB(): void {
        const self = this;
        block.grayout();
        service.screenDisplayProcess().done(res => {
          let arr: Pattern[] = [];
          _.forEach(res.patterns, x => {
            let p = new Pattern();
            p.code = x.patternCode;
            p.patternName = x.patternName;
            p.patternClassification = x.patternClassification;
            p.displayCode = x.patternClassification + x.patternCode;
            arr.push(p);
          });
          self.patternList(arr);
          self.patternList(_.orderBy(self.patternList(), ['patternClassification', 'code'], ['desc', 'asc']));
          self.systemTypes(res.systemTypes);
          self.selectedPatternId(self.patternList()[0].displayCode);
          self.selectPattern(self.patternList()[0].displayCode);
        }).fail((err) => {
          alertError({ messageId: 'Msg_1736' });
        }).always(() => {
          block.clear();
        });
      }

      private nextFromDToE(): void {
        var self = this;
        if (self.selectedTitleAtr() != 0 && self.selectedTitleAtr() != 1) {
          alertError({ messageId: 'Msg_463' });
        } else
          if (self.selectedTitleAtr() == 1 && self.selectedEmployeeCode().length == 0) {
            alertError({ messageId: 'Msg_498' });
          } else {
            // self.initE();
            // self.next();
            self.gotoscreenF();
            $("#E5_2").focus();
          }
      }

      private initE(): void {
        var self = this;
        self.setTargetEmployee();
        $("#E3_3").html(self.dataSaveSetName());
        $("#E3_5").html(self.explanation());
        $("#E3_37").html(self.selectedTitleAtr() == 1 ? self.referenceDate() : "");
      }

      private setTargetEmployee(): void {
        let self = this;
        let tempEmployee;

        if (self.selectedTitleAtr() == 0) {
          self.targetEmployee([]);
        }
        else {
          tempEmployee = _.filter(self.employeeList(), function (o) {
            return _.includes(self.selectedEmployeeCode(), o.code);
          });

          tempEmployee = _.sortBy(tempEmployee, ["code"]);
          self.targetEmployee(tempEmployee);
        }
      }

      private validateB(): boolean {
        $(".form-B").trigger("validate");
        $(".form-B .ntsDatepicker").trigger("validate");
        if (nts.uk.ui.errors.hasError()) {
          return false;
        }
        return true;
      };

      private previousB(): void {
        var self = this;
        self.previous();
        $(".selectEmpType").focus();
      }
      private backToA() {
        let self = this;
        nts.uk.request.jump("/view/cmf/003/a/index.xhtml");
      }

      private selectCategory(): void {
        let self = this;

        setShared("CMF003_B_CATEGORIES", self.categorys());
        setShared("CMF003_B_SYSTEMTYPE", self.systemtypeFromC);

        nts.uk.ui.windows.sub.modal('../c/index.xhtml').onClosed(() => {
          let categoryFromC = JSON.parse(getShared('CMF003_C_CATEGORIES'));
          let systemtypeFromC = getShared('CMF003_C_SYSTEMTYPE');

          if (systemtypeFromC) {
            self.systemtypeFromC = systemtypeFromC;
            $("#B4_24").html(systemtypeFromC.name);
          }

          if (categoryFromC && (categoryFromC.length > 0)) {
            self.categorys(categoryFromC);
            self.setRangePickerRequire();
          }

          $("#B4_2").focus();
        });
      }

      private gotoscreenF(): void {
        let self = this;
        self.saveManualSetting();
      }

      private saveManualSetting(): void {
        let self = this;
        if (self.employeeList().length !== self.selectedEmployeeCode().length) {
          self.employeeList(_.filter(self.employeeList(), e => _.includes(self.selectedEmployeeCode(), e.code)));
        }
        let manualSetting = new ManualSettingModal(Number(self.isCompressPass()), self.dataSaveSetName(),
          moment.utc(self.referenceDate(), 'YYYY/MM/DD').toISOString(), self.password(), moment.utc().toISOString(), moment.utc(self.dayValue().endDate, 'YYYY/MM/DD').toISOString(),
          moment.utc(self.dayValue().startDate, 'YYYY/MM/DD').toISOString(), moment.utc(self.monthValue().endDate, 'YYYY/MM/DD').toISOString(),
          moment.utc(self.monthValue().startDate, 'YYYY/MM/DD').toISOString(), self.explanation(), Number(self.yearValue().endDate), Number(self.yearValue().startDate),
          self.selectedTitleAtr(), self.employeeList(), self.categorys(), self.selectedPatternId().slice(1));

        service.addMalSet(manualSetting).done((res) => {
          if ((res != null) && (res != "")) {
            let params = {
              storeProcessingId: res,
              dataSaveSetName: self.dataSaveSetName(),
              dayValue: self.dayValue(),
              monthValue: self.monthValue(),
              yearValue: self.yearValue()
            };

            setShared("CMF001_E_PARAMS", params);
            nts.uk.ui.windows.sub.modal("/view/cmf/003/f/index.xhtml").onClosed(() => {
              self.buton_E_enable(self.selectedTitleAtr() === 0);
              self.buton_E_enable.valueHasMutated();
              $(".goback").focus();
            });
          }
        }).fail((err) => {
        });
      }

      private selectPattern(displayCode: string) {
        const self = this;
        block.grayout();
        const pattern = _.find(self.patternList(), { 'displayCode': displayCode });
        let param = {
          patternClassification: pattern.patternClassification,
          patternCode: pattern.code,
          systemType: self.systemTypes()
        };
        service.patternSettingSelect(param).then((res) => {
          self.savedName(res.patternName);
          self.dataSaveSetName(res.patternName);
          self.isCompressPass(res.withoutPassword === 1);
          self.password(res.patternCompressionPwd);
          self.confirmPassword(res.patternCompressionPwd);
          self.explanation(res.patternSuppleExplanation);

          if (res.selectCategories && res.selectCategories.length > 0) {
            const textToFormat: string = getText(res.selectCategories[0].holder.textToFormat);
            self.categorys(_.map(res.selectCategories, (x: any) => {
              const params: string[] = _.map(x.holder.params, (text: string) => getText(text));
              const category: Category = {
                categoryId: x.categoryId,
                categoryName: x.categoryName,
                displayName: x.categoryName + nts.uk.text.format(textToFormat, params),
                periodDivision: x.periodDivision,
                separateCompClassification: x.separateCompClassification,
                specifiedMethod: x.specifiedMethod,
                storeRange: x.storeRange,
                systemType: x.systemType,
                id: nts.uk.util.randomId()
              };
              return category;
            }));
          } else {
            self.categorys([]);
          }
          nts.uk.ui.errors.clearAll();
        }).then(() => {
          return service.getClosurePeriod().then(result => {
            const startDate = moment.utc(result.startDate, "YYYY/MM/DD");
            const endDate = moment.utc(result.endDate, "YYYY/MM/DD");
            
            self.dayValue({
              startDate: startDate.format("YYYY/MM/DD"),
              endDate: endDate.format("YYYY/MM/DD")
            });
            self.monthValue({
              startDate: startDate.format("YYYY/MM"),
              endDate: endDate.format("YYYY/MM")
            });
            self.yearValue({
              startDate: startDate.format("YYYY"),
              endDate: endDate.format("YYYY")
            });
            self.setRangePickerRequire();
          })
        }).always(() => {
          block.clear();
        });
      }
    }//end screemodule

    export class ManualSettingModal {
      passwordAvailability: number;
      saveSetName: string;
      referenceDate: string;
      compressedPassword: string;
      executionDateAndTime: string;
      daySaveEndDate: string;
      daySaveStartDate: string;
      monthSaveEndDate: string;
      monthSaveStartDate: string;
      suppleExplanation: string;
      endYear: number;
      startYear: number;
      presenceOfEmployee: number;
      employees: Array<TargetEmployee>;
      category: Array<Category>;
      patternCode: string;

      constructor(passwordAvailability: number, saveSetName: string, referenceDate: string, compressedPassword: string,
        executionDateAndTime: string, daySaveEndDate: string, daySaveStartDate: string, monthSaveEndDate: string, monthSaveStartDate: string,
        suppleExplanation: string, endYear: number, startYear: number, presenceOfEmployee: number,
        employees: Array<TargetEmployee>, category: Array<Category>, patternCode: string) {
        this.passwordAvailability = passwordAvailability;
        this.saveSetName = saveSetName;
        this.referenceDate = referenceDate;
        this.compressedPassword = compressedPassword;
        this.executionDateAndTime = executionDateAndTime;
        this.daySaveEndDate = daySaveEndDate ? daySaveEndDate : null;
        this.daySaveStartDate = daySaveStartDate ? daySaveStartDate : null;
        this.monthSaveEndDate = monthSaveEndDate ? monthSaveEndDate : null;
        this.monthSaveStartDate = monthSaveStartDate ? monthSaveStartDate : null;
        this.suppleExplanation = suppleExplanation;
        this.endYear = endYear ? endYear : null;
        this.startYear = startYear ? startYear : null;
        this.presenceOfEmployee = presenceOfEmployee;
        this.employees = employees;
        this.category = category;
        this.patternCode = patternCode;
      }
    }

    export class Pattern {
      code: string;
      patternName: string;
      patternClassification: number;
      displayCode?: string;
    }

    export class Category {
      categoryId: string;
      categoryName: string;
      periodDivision: number;
      systemType: number;
      separateCompClassification: number;
      specifiedMethod: number;
      storeRange: number;
      displayName: string;
      id: string;
    }

    export class CategoryModel {
      schelperSystem: number;
      categoryId: string;
      categoryName: string;
      possibilitySystem: number;
      storedProcedureSpecified: number;
      timeStore: number;
      otherCompanyCls: number;
      attendanceSystem: number;
      recoveryStorageRange: number;
      paymentAvailability: number;
      storageRangeSaved: number;
    }

    class ItemCombobox {
      code: string;
      name: string;

      constructor(code: string, name: string) {
        this.code = code;
        this.name = name;
      }
    }

    export class ListType {
      static EMPLOYMENT = 1;
      static Classification = 2;
      static JOB_TITLE = 3;
      static EMPLOYEE = 4;
    }

    export interface TargetEmployee {
      code: string;
      name?: string;
      affiliationName?: string;
      isAlreadySetting?: boolean;
      sid: string;
      scd: string;
      businessname: string;
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

    export interface EmployeeSearchDto {
      employeeId: string;

      employeeCode: string;

      employeeName: string;

      workplaceCode: string;

      workplaceId: string;

      workplaceName: string;
    }

    export interface GroupOption {
      baseDate?: KnockoutObservable<Date>;

      periodStartDate?: KnockoutObservable<Date>;

      periodEndDate?: KnockoutObservable<Date>;

      inService: boolean;

      leaveOfAbsence: boolean;

      closed: boolean;

      retirement: boolean;

      // クイック検索タブ
      isQuickSearchTab: boolean;
      // 参照可能な社員すべて
      isAllReferableEmployee: boolean;
      //自分だけ
      isOnlyMe: boolean;
      //おなじ部門の社員
      isEmployeeOfWorkplace: boolean;
      //おなじ＋配下部門の社員
      isEmployeeWorkplaceFollow: boolean;


      // 詳細検索タブ
      isAdvancedSearchTab: boolean;
      //複数選択 
      isMutipleCheck: boolean;

      //社員指定タイプ or 全社員タイプ
      isSelectAllEmployee: boolean;

      onSearchAllClicked: (data: EmployeeSearchDto[]) => void;

      onSearchOnlyClicked: (data: EmployeeSearchDto) => void;

      onSearchOfWorkplaceClicked: (data: EmployeeSearchDto[]) => void;

      onSearchWorkplaceChildClicked: (data: EmployeeSearchDto[]) => void;

      onApplyEmployee: (data: EmployeeSearchDto[]) => void;
    }

    function timeStore(value, row) {
      if (value && value === '0') {
        return getText('Enum_TimeStore_FULL_TIME');
      } else if (value && value === '1') {
        return getText('Enum_TimeStore_DAILY');
      } else if (value && value === '2') {
        return getText('Enum_TimeStore_MONTHLY');
      } else if (value && value === '3') {
        return getText('Enum_TimeStore_ANNUAL');

      }
    }

    function storageRangeSaved(value, row) {
      if (value && value === '0') {
        return getText('Enum_StorageRangeSaved_EARCH_EMP');
      } else if (value && value === '1') {
        return getText('Enum_StorageRangeSaved_ALL_EMP');
      }
    }
  }
}