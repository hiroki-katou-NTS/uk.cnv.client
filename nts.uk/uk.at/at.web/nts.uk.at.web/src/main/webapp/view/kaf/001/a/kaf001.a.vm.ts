module kaf001.a.viewmodel {
    import block  = nts.uk.ui.block;
    import dialog = nts.uk.ui.dialog;
    import jump   = nts.uk.request.jump;
    import getText = nts.uk.resource.getText;
    import modal = nts.uk.ui.windows.sub.modal;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;
    export class ScreenModel {

        selectedDate: KnockoutObservable<string> = ko.observable(moment().format('YYYY/MM/DD'));

        //_____CCG001________
        ccg001ComponentOption : GroupOption;
        selectedEmployee      : KnockoutObservableArray<EmployeeSearchDto>      = ko.observableArray([]);
        //_____KCP005________
        kcp005ComponentOption: any;
        selectedEmployeeCode : KnockoutObservableArray<string>                  = ko.observableArray([]);
        alreadySettingList   : KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);
        employeeList         : KnockoutObservableArray<UnitModel>               = ko.observableArray([]);
        // isVisiableAppWindow           : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableOverTimeEarly       : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableOverTimeNormal      : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableOverTimeEarlyNormal : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableOverTimeMultiple    : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableAbsenceApp          : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableWorkChangeApp       : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableBusinessTripApp     : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableGoReturnDirectlyApp : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableBreakTimeApp        : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableAnnualHolidayApp    : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableEarlyLeaveCanceApp  : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableComplementLeaveApp  : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableStampApp            : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableStampAppOnlMode     : KnockoutObservable<boolean>             = ko.observable(false);
        isVisiableOptionalItemApp     : KnockoutObservable<boolean>             = ko.observable(false);
        //app Name
        appNameDis: KnockoutObservable<ObjNameDis> = ko.observable(null);
        constructor() {
            let self = this;

            //_____CCG001________
            self.ccg001ComponentOption = {
                /** Common properties */
                systemType: 2,
                showEmployeeSelection: false,
                showQuickSearchTab: true,
                showAdvancedSearchTab: true,
                showBaseDate: true,
                showClosure: false,
                showAllClosure: false,
                showPeriod: false,
                periodFormatYM: false,

                /** Required parameter */
                baseDate: self.selectedDate,
                periodStartDate: moment().toISOString(),
                periodEndDate: moment().toISOString(),
                inService: true,
                leaveOfAbsence: true,
                closed: true,
                retirement: true,

                /** Quick search tab options */
                showAllReferableEmployee: true,
                showOnlyMe: false,
                showSameWorkplace: true,
                showSameWorkplaceAndChild: true,

                /** Advanced search properties */
                showEmployment: true,
                showWorkplace: true,
                showClassification: true,
                showJobTitle: true,
                showWorktype: true,
                isMutipleCheck: true,

                /**
                * Self-defined function: Return data from CCG001
                * @param: data: the data return from CCG001
                */
                returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                    self.applyKCP005ContentSearch(data.listEmployee);
                    self.selectedEmployee(data.listEmployee);
                    self.selectedDate(data.baseDate);
                }
            };

//            $('#ccg001component').ntsGroupComponent(self.ccg001ComponentOption);

//            self.applyKCP005ContentSearch([]);
//            $('#kcp005component').ntsListComponent(self.kcp005ComponentOption);
        }

        /**
         * Apply CCG001 search data to KCP005
         */
        applyKCP005ContentSearch(dataList: EmployeeSearchDto[]): void {
            let self = this;
            self.employeeList.removeAll();
            let employeeSearchs: Array<UnitModel> = [];
            for (let employeeSearch of dataList) {
                let employee: UnitModel = {
                    code: employeeSearch.employeeCode,
                    name: employeeSearch.employeeName,
                    affiliationName: employeeSearch.affiliationName
                };
                employeeSearchs.push(employee);
            }
            self.employeeList(employeeSearchs);
            self.kcp005ComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: true,
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeList,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedEmployeeCode,
                isDialog: false,
                isShowNoSelectRow: false,
                alreadySettingList: self.alreadySettingList,
                isShowWorkPlaceName: true,
                isShowSelectAllButton: true,
                maxWidth: 450,
                maxRows: 20
            };
            self.start();
        }

        start(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            self.getAllProxyApplicationSetting().done(() => {
                dfd.resolve();
            }).fail(() => {
                dfd.reject();
            });
            $("#A4_2").focus();
            return dfd.promise();
        }


        getAllProxyApplicationSetting(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            block.invisible();
            service.getAppDispName().done((res) => {
                let obj: ObjNameDis = new ObjNameDis('', '', '', '',
                    '', '', '', '',
                    '', '', '', '', '', '','');
                if(res) {
                    _.each(res, function(app){
                        switch (app.programId) {
                            case ApplicationScreenID.OVER_TIME_APPLICATION : {
                                if (app.queryString.split("=")[1] == 0) {
                                    self.isVisiableOverTimeEarly(true);
                                    obj.overTimeEarly = app.displayName;
                                }
                                if (app.queryString.split("=")[1] == 1) {
                                    self.isVisiableOverTimeNormal(true);
                                    obj.overTimeNormal = app.displayName;
                                }
                                if (app.queryString.split("=")[1] == 2) {
                                    self.isVisiableOverTimeEarlyNormal(true);
                                    obj.overTimeEarlyDepart = app.displayName;
                                }
                                if (app.queryString.split("=")[1] == 3) {
                                    self.isVisiableOverTimeMultiple(true);
                                    obj.overTimeMultiple = app.displayName;
                                }
                                break;
                            }
                            case ApplicationScreenID.ABSENCE_APPLICATION : {
                                self.isVisiableAbsenceApp(true);
                                obj.absence = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.WORK_CHANGE_APPLICATION : {
                                self.isVisiableWorkChangeApp(true);
                                obj.workChange = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.BUSINESS_TRIP_APPLICATION : {
                                self.isVisiableBusinessTripApp(true);
                                obj.businessTrip = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.GO_RETURN_DIRECTLY_APPLICATION : {
                                self.isVisiableGoReturnDirectlyApp(true);
                                obj.goBack = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.BREAK_TIME_APPLICATION : {
                                self.isVisiableBreakTimeApp(true);
                                obj.holiday = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.COMPLEMENT_LEAVE_APPLICATION : {
                                self.isVisiableComplementLeaveApp(true);
                                obj.complt = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.ANNUAL_HOLIDAY_APPLICATION : {
                                self.isVisiableAnnualHolidayApp(true);
                                obj.annualHd = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.EARLY_LEAVE_CANCEL_APPLICATION : {
                                self.isVisiableEarlyLeaveCanceApp(true);
                                obj.earlyLeaveCancel = app.displayName;
                                break;
                            }
                            case ApplicationScreenID.STAMP_APPLICATION : {
                                if (app.screenId == "A") {
                                    self.isVisiableStampApp(true);
                                    obj.stamp = app.displayName;
                                }
                                if (app.screenId == "B") {
                                    self.isVisiableStampAppOnlMode(true);
                                    obj.stampOnline = app.displayName;
                                }
                                break;
                            }
                            case ApplicationScreenID.OPTIONAL_ITEM_APPLICATION : {
                                self.isVisiableOptionalItemApp(true);
                                obj.optionalItem = app.displayName;
                            }

                        }
                    });
                }
                self.appNameDis(obj);
                dfd.resolve();
            }).fail((err) => {
                dialog.alertError(err).then(function () {
                    nts.uk.request.jump("com", "view/ccg/008/a/index.xhtml");
                });
                dfd.reject();
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }

        /**
         * 申請種類選択
         */
        selectApplicationByType(applicationType: number, mode? : number) {
            block.invisible();

            let self = this;
            let vm = new ko.ViewModel();
            let employeeIds : Array<string> = [];

            _.each(self.selectedEmployeeCode(), x => {
               let employee = _.find(self.selectedEmployee(), x1 => { return x1.employeeCode === x });
               if (employee) {
                   employeeIds.push(employee.employeeId);
               }
            });

            let employeeParamCheck = {
                appType: applicationType,
                baseDate: moment(self.selectedDate()).isValid() ? self.selectedDate() : null,
                lstEmployeeId: employeeIds,
                overtimeAppAtr: mode
            };

            service.checkEmployee(employeeParamCheck).done(() => {
                let transfer: AppInitParam = {
                    appType: applicationType,
                    employeeIds,
                    baseDate: self.selectedDate(),
					isAgentMode: true
                };
                switch (applicationType) {
                    case ApplicationType.OVER_TIME_APPLICATION: {
                        if (mode != null) {
                            switch (mode) {
                                case 0:
                                    //KAF005-残業申請（早出）
                                    vm.$jump("/view/kaf/005/a/index.xhtml?overworkatr=0", transfer);
                                    break;

                                case 1:
                                    //KAF005-残業申請（通常）
                                    vm.$jump("/view/kaf/005/a/index.xhtml?overworkatr=1", transfer);
                                    break;

                                case 2:
                                    //KAF005-残業申請（早出・通常）
                                    vm.$jump("/view/kaf/005/a/index.xhtml?overworkatr=2", transfer);
                                    break;
                                case 3:
                                    //KAF005-残業申請（multiple）
                                    vm.$jump("/view/kaf/005/a/index.xhtml?overworkatr=3", transfer);
                                    break;
                            }
                        }
                        break;
                    }
                    case ApplicationType.ABSENCE_APPLICATION: {
                        vm.$jump("/view/kaf/006/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.WORK_CHANGE_APPLICATION: {
                        vm.$jump("/view/kaf/007/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.BUSINESS_TRIP_APPLICATION: {
                        vm.$jump("/view/kaf/008/a/index.xhtml", transfer);
                        // jump("at", "/view/kaf/008/a/index.xhtml", {success : true});
                        break;
                    }
                    case ApplicationType.GO_RETURN_DIRECTLY_APPLICATION: {
                        vm.$jump("/view/kaf/009/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.BREAK_TIME_APPLICATION: {
                        vm.$jump("/view/kaf/010/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.ANNUAL_HOLIDAY_APPLICATION: {
                        vm.$jump("/view/kaf/012/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION: {
                        vm.$jump("/view/kaf/004/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.COMPLEMENT_LEAVE_APPLICATION: {
                        vm.$jump("/view/kaf/011/a/index.xhtml", transfer);
                        break;
                    }
                    case ApplicationType.STAMP_APPLICATION: {
                        if (mode == 0) {
                            vm.$jump("/view/kaf/002/a/index.xhtml", transfer);
                        }
                        if (mode == 1) {
                            vm.$jump("/view/kaf/002/b/index.xhtml", transfer);
                        }
                        break;
                    }
                    case ApplicationType.OPTIONAL_ITEM_APPLICATION: {
                        vm.$jump("/view/kaf/020/a/index.xhtml", transfer);
                        break;
                    }
                }
            }).fail((err) => {

                dialog.alertError(err);

            }).always(() => {
                block.clear();
            });

        }
    }

    export enum ApplicationType {
        OVER_TIME_APPLICATION                   = 0,    /**残業申請*/
        ABSENCE_APPLICATION                     = 1,    /**休暇申請*/
        WORK_CHANGE_APPLICATION                 = 2,    /**勤務変更申請*/
        BUSINESS_TRIP_APPLICATION               = 3,    /**出張申請*/
        GO_RETURN_DIRECTLY_APPLICATION          = 4,    /**直行直帰申請*/
        BREAK_TIME_APPLICATION                  = 6,    /**休出時間申請*/
        STAMP_APPLICATION                       = 7,    /**打刻申請*/
        ANNUAL_HOLIDAY_APPLICATION              = 8,    /**時間年休申請*/
        EARLY_LEAVE_CANCEL_APPLICATION          = 9,    /**遅刻早退取消申請*/
        COMPLEMENT_LEAVE_APPLICATION            = 10,   /**振休振出申請*/
        STAMP_NR_APPLICATION                    = 11,   /**打刻申請（NR形式）*/
        LONG_BUSINESS_TRIP_APPLICATION          = 12,   /**連続出張申請*/
        BUSINESS_TRIP_APPLICATION_OFFICE_HELPER = 13,   /**出張申請オフィスヘルパー*/
        APPLICATION_36                          = 14,   /**３６協定時間申請*/
        OPTIONAL_ITEM_APPLICATION               = 15,   /**任意項目申請*/
    }

    export enum ApplicationScreenID {
        OVER_TIME_APPLICATION                   = "KAF005",    /**残業申請*/
        ABSENCE_APPLICATION                     = "KAF006",    /**休暇申請*/
        WORK_CHANGE_APPLICATION                 = "KAF007",    /**勤務変更申請*/
        BUSINESS_TRIP_APPLICATION               = "KAF008",    /**出張申請*/
        GO_RETURN_DIRECTLY_APPLICATION          = "KAF009",    /**直行直帰申請*/
        BREAK_TIME_APPLICATION                  = "KAF010",    /**休出時間申請*/
        STAMP_APPLICATION                       = "KAF002",    /**打刻申請*/
        ANNUAL_HOLIDAY_APPLICATION              = "KAF012",    /**時間年休申請*/
        EARLY_LEAVE_CANCEL_APPLICATION          = "KAF004",    /**遅刻早退取消申請*/
        COMPLEMENT_LEAVE_APPLICATION            = "KAF011",   /**振休振出申請*/
        STAMP_NR_APPLICATION                    = "",   /**打刻申請（NR形式）*/
        LONG_BUSINESS_TRIP_APPLICATION          = "",   /**連続出張申請*/
        BUSINESS_TRIP_APPLICATION_OFFICE_HELPER = "",   /**出張申請オフィスヘルパー*/
        APPLICATION_36                          = "KAF021",   /**３６協定時間申請*/
        OPTIONAL_ITEM_APPLICATION               = "KAF020"     /**任意項目申請*/
    }

    //Interfaces
    export interface GroupOption {
        /** Common properties */
        showEmployeeSelection: boolean; // 検索タイプ
        systemType: number; // システム区分
        showQuickSearchTab: boolean; // クイック検索
        showAdvancedSearchTab: boolean; // 詳細検索
        showBaseDate: boolean; // 基準日利用
        showClosure: boolean; // 就業締め日利用
        showAllClosure: boolean; // 全締め表示
        showPeriod: boolean; // 対象期間利用
        periodFormatYM: boolean; // 対象期間精度
        isInDialog?: boolean;

        /** Required parameter */
        baseDate?: string; // 基準日
        periodStartDate?: string; // 対象期間開始日
        periodEndDate?: string; // 対象期間終了日
        inService: boolean; // 在職区分
        leaveOfAbsence: boolean; // 休職区分
        closed: boolean; // 休業区分
        retirement: boolean; // 退職区分

        /** Quick search tab options */
        showAllReferableEmployee: boolean; // 参照可能な社員すべて
        showOnlyMe: boolean; // 自分だけ
        showSameWorkplace: boolean; // 同じ職場の社員
        showSameWorkplaceAndChild: boolean; // 同じ職場とその配下の社員

        /** Advanced search properties */
        showEmployment: boolean; // 雇用条件
        showWorkplace: boolean; // 職場条件
        showClassification: boolean; // 分類条件
        showJobTitle: boolean; // 職位条件
        showWorktype: boolean; // 勤種条件
        isMutipleCheck: boolean; // 選択モード

        /** Data returned */
        returnDataFromCcg001: (data: Ccg001ReturnedData) => void;
    }

    export interface EmployeeSearchDto {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        workplaceId: string;
        workplaceName: string;
    }

    export interface Ccg001ReturnedData {
        baseDate: string; // 基準日
        closureId?: number; // 締めID
        periodStart: string; // 対象期間（開始)
        periodEnd: string; // 対象期間（終了）
        listEmployee: Array<EmployeeSearchDto>; // 検索結果
    }

    export class ListType {
        static EMPLOYMENT     = 1;
        static CLASSIFICATION = 2;
        static JOB_TITLE      = 3;
        static EMPLOYEE       = 4;
    }

    export interface UnitModel {
        code: string;
        name?: string;
        affiliationName?: string;
        isAlreadySetting?: boolean;
    }

    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL              = 2;
        static SELECT_FIRST_ITEM       = 3;
        static NO_SELECT               = 4;
    }

    export interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }
    export class ObjNameDis{
        overTimeEarly : string;
        overTimeNormal: string;
        overTimeEarlyDepart: string;
        overTimeMultiple: string;//A2_17
        absence: string;//A2_3
        workChange: string;//A2_4
        businessTrip: string;//A2_5
        goBack: string;//A2_6
        holiday: string;//A2_7
        annualHd: string;//A2_8
        earlyLeaveCancel: string;//A2_9
        complt: string;//A2_10
        stamp: string;//A2_11
        stampOnline: string;
        optionalItem: string;
        constructor(overTimeEarly: string, overTimeNormal: string, overTimeEarlyDepart: string, overtimeMultiple: string,
                    absence: string, workChange: string,
                    businessTrip: string, goBack: string, holiday: string,
                    annualHd: string, earlyLeaveCancel: string, complt: string, stamp: string, stampOnline: string, optionalItem: string) {
            this.overTimeEarly = overTimeEarly;
            this.overTimeNormal = overTimeNormal;
            this.overTimeEarlyDepart = overTimeEarlyDepart;
            this.overTimeMultiple = overtimeMultiple;
            this.absence = absence;
            this.workChange = workChange;
            this.businessTrip = businessTrip;
            this.goBack = goBack;
            this.holiday = holiday;
            this.annualHd = annualHd;
            this.earlyLeaveCancel = earlyLeaveCancel;
            this.complt = complt;
            this.stamp = stamp;
            this.stampOnline = stampOnline;
            this.optionalItem = optionalItem;
        }
    }
}