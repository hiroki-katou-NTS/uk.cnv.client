module nts.uk.at.view.kdl020_old_2.a.screenModel {

    import dialog = nts.uk.ui.dialog.info;
    import text = nts.uk.resource.getText;
    import formatDate = nts.uk.time.formatDate;
    import block = nts.uk.ui.block;
    import jump = nts.uk.request.jump;
    import alError = nts.uk.ui.dialog.alertError;
    import getShared = nts.uk.ui.windows.getShared;
    import formatById = nts.uk.time.format.byId;


    export class ViewModel {

        value: KnockoutObservable<string> = ko.observable('');

        total: KnockoutObservable<string> = ko.observable('');

        listComponentOption: any;
        selectedCode: KnockoutObservable<string>;
        selectedName: KnockoutObservable<string>;
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        annualLeaveManagementFg: KnockoutObservable<boolean>;
        closingPeriod: KnockoutObservable<string>;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray<any>([]);
        baseDate = ko.observable(new Date());
        reNumAnnLeave: KnockoutObservable<ReNumAnnLeaReferenceDate> = ko.observable(new ReNumAnnLeaReferenceDate());
        displayAnnualLeaveGrant: KnockoutObservable<DisplayAnnualLeaveGrant> = ko.observable(new DisplayAnnualLeaveGrant());
        attendNextHoliday: KnockoutObservable<AttendRateAtNextHoliday> = ko.observable(new AttendRateAtNextHoliday());
        annualSet: KnockoutObservable<any> = ko.observable(null);
        constructor() {
            let self = this;
            self.selectedCode = ko.observable('');
            self.selectedName = ko.observable('');
            self.isShowAlreadySet = ko.observable(false);
            self.alreadySettingList = ko.observableArray([
                { code: '1', isAlreadySetting: true },
                { code: '2', isAlreadySetting: true }
            ]);
            self.annualLeaveManagementFg = ko.observable(false);
            self.closingPeriod = ko.observable('');
            self.isDialog = ko.observable(false);
            self.isShowNoSelectRow = ko.observable(false);
            self.isMultiSelect = ko.observable(false);
            self.isShowWorkPlaceName = ko.observable(false);
            self.isShowSelectAllButton = ko.observable(false);
            self.listComponentOption = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultiSelect: self.isMultiSelect(),
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeList,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedCode,
                isDialog: self.isDialog(),
                isShowNoSelectRow: self.isShowNoSelectRow(),
                alreadySettingList: self.alreadySettingList,
                isShowWorkPlaceName: self.isShowWorkPlaceName(),
                isShowSelectAllButton: self.isShowSelectAllButton()
            };

            self.selectedCode.subscribe((newCode) => {
                let self = this;
                if (newCode) {
                    let selectedID = _.find(self.employeeList(), ['code', newCode]).id,
                        changeIDParam = {
                            employeeId: selectedID,
                            baseDate: self.baseDate()
                        };
                    self.selectedName(_.find(self.employeeList(), ['code', newCode]).name);
                    block.invisible();
                    service.changeID(changeIDParam).done((data) => {
                        self.changeData(data);
                    }).fail((error) => {
                        dialog({ messageId: error.messageId });
                    }).always(() => {
                        block.clear();
                    });
                }
            });

            $("#holiday-info_table").ntsFixedTable({ height: 120, width: 526 });
            $("#holiday-use_table").ntsFixedTable({ height: 110, width: 335 });
        }

        start(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            let data: any = getShared('KDL020A_PARAM');
            self.baseDate(data.baseDate);
            //edit param
            let startParam = {
                baseDate: self.baseDate(),
                employeeIds: data.employeeIds
            }
            block.invisible();
            service.startPage(startParam).done((data: IAnnualHoliday) => {
                if (data) {
                    let mappedList =
                        _.map(data.employees, item => {
                            return { id: item.sid, code: item.scd, name: item.bussinessName };
                        });
                    self.employeeList(mappedList);
                    self.selectedCode(mappedList[0].code);
                    self.changeData(data);
                    self.annualSet(data.annualSet);
                    if (self.employeeList().length > 1) {
                        nts.uk.ui.windows.getSelf().$dialog.closest(".ui-dialog").width(1080);
                    }
                }
            }).fail((error) => {
                dialog({ messageId: error.messageId });
            }).always(() => {
                if (self.employeeList().length > 1) {
                    $('#component-items-list').ntsListComponent(self.listComponentOption);
                }
                block.clear();
                dfd.resolve();
            });

            return dfd.promise();

        }
        genDateText(data) {
            if (data == null) {
                return '';
            }
            return data + text('KDL020_14');
        }

        genRemainDays(remainDays, remainMinutes) {
            let self = this;
            if (remainDays == null) {
                return '';
            }

            return self.genDateText(remainDays) + "&nbsp;" + self.genTime(remainMinutes);
        }

        genDaysUsedNo(daysUsedNo, usedMinutes) {
            let self = this;
            if (daysUsedNo == null) {
                return '';
            }

            return self.genDateText(daysUsedNo) + "&nbsp;" + self.genTime(usedMinutes);
        }

        // format data to A11_3
        public genGrantDate(grantDate: string, deadline: string, expiredInCurrentMonthFg: boolean) {
            if (!grantDate && !deadline) {
                return '';
            }
            if (expiredInCurrentMonthFg) {
                return grantDate + nts.uk.resource.getText("KDL005_38", [deadline]);
            }
            return grantDate + nts.uk.resource.getText("KDL005_37", [deadline]);
        }

        genUsedNo(daysUsedNo, usedMinutes) {
            let self = this;
            if (daysUsedNo != null) {
                return self.genDateText(daysUsedNo);
            }
            if (usedMinutes != null) {
                return self.genTime(usedMinutes);
            }

            return '';
        }

        genTime(data) {
            if (data == null) {
                return '';
            }
            return formatById("Clock_Short_HM", data);
        }

        genScheduleRecordText(scheduleRecordAtr) {
            return CreateAtr[scheduleRecordAtr];
        }

        genAttendanceRate(attendanceRate) {
            if (attendanceRate == null) {
                return '';
            }
            return attendanceRate + text('KDL020_27');

        }

        genNextHolidayGrantDate(nextHolidayGrantDate) {
            if (nextHolidayGrantDate == null) {
                return '';

            }
            return nextHolidayGrantDate + text('KDL020_29');
        }

        changeData(data: IAnnualHoliday) {
            let self = this;
            self.reNumAnnLeave(new ReNumAnnLeaReferenceDate(data.reNumAnnLeave));
            self.displayAnnualLeaveGrant(new DisplayAnnualLeaveGrant(data.annualLeaveGrant[0]));
            self.attendNextHoliday(new AttendRateAtNextHoliday(data.attendNextHoliday));
            self.annualLeaveManagementFg(data.annualLeaveManagementFg);
            self.closingPeriod(data.closingPeriod);
            self.setTotal();
        }

        setTotal() {
            let self = this,
                grants = self.reNumAnnLeave().annualLeaveGrants(),
                totalDays = 0,
                totalTimes = 0;
            _.forEach(grants, function(item) {
                totalDays += item.remainDays();
                totalTimes += item.remainMinutes();
            });

            self.total(self.genDateText(totalDays) + "&nbsp;" + self.genTime(totalTimes));

        }
        addUseRecordData() {
            let self = this,
                randomNumber = Math.floor((Math.random() * 10) + 1),
                randomNumber2 = Math.floor((Math.random() * 3)),
                data = {
                    ymd: '2018/06/07',
                    daysUsedNo: randomNumber % 3 == 0 ? randomNumber : undefined,
                    usedMinutes: randomNumber,
                    scheduleRecordAtr: randomNumber2
                };

            self.reNumAnnLeave().annualLeaveManageInfors.push(new AnnualLeaveManageInfor(data));
        }

        close() {
            nts.uk.ui.windows.close();
        }
    }

    export class DisplayAnnualLeaveGrant {
        /** 付与年月日 */
        grantDate: KnockoutObservable<String> = ko.observable("");
        /** 付与日数 */
        grantDays: KnockoutObservable<number> = ko.observable(null);
        /** 回数 */
        times: KnockoutObservable<number> = ko.observable(null);
        /** 時間年休上限日数 */
        timeAnnualLeaveMaxDays: KnockoutObservable<number> = ko.observable(null);
        /** 時間年休上限時間 */
        timeAnnualLeaveMaxTime: KnockoutObservable<number> = ko.observable(null);
        /** 半日年休上限回数 */
        halfDayAnnualLeaveMaxTimes: KnockoutObservable<number> = ko.observable(null);
        constructor(data?) {
            if (data) {
                this.grantDate(data.grantDate);
                this.grantDays(data.grantDays);
                this.times(data.times);
                this.timeAnnualLeaveMaxDays(data.timeAnnualLeaveMaxDays);
                this.timeAnnualLeaveMaxTime(data.timeAnnualLeaveMaxTime);
                this.halfDayAnnualLeaveMaxTimes(data.halfDayAnnualLeaveMaxTimes);

            }
        }

    }

    export class ReNumAnnLeaReferenceDate {
        /** 年休残数(仮)*/
        annualLeaveRemainNumber: KnockoutObservable<AnnualLeaveRemainingNumber> = ko.observable(new AnnualLeaveRemainingNumber());

        /** 年休付与情報(仮)*/
        annualLeaveGrants: KnockoutObservableArray<AnnualLeaveGrant> = ko.observableArray([]);

        /** 年休管理情報(仮)*/
        annualLeaveManageInfors: KnockoutObservableArray<AnnualLeaveManageInfor> = ko.observableArray([]);
        constructor(data?) {
            if (data) {
                this.annualLeaveRemainNumber(new AnnualLeaveRemainingNumber(data.annualLeaveRemainNumberExport));
                this.annualLeaveGrants(_.map(data.annualLeaveGrantExports, x => {
                    return new AnnualLeaveGrant(x);
                }));
                this.annualLeaveManageInfors(_.map(data.annualLeaveManageInforExports, x => {
                    return new AnnualLeaveManageInfor(x);
                }));
            }
        }
    }
    export class AnnualLeaveRemainingNumber {
        /* 年休残数（付与前）日数*/
        annualLeaveGrantPreDay: KnockoutObservable<number> = ko.observable(0);
        /* 年休残数（付与前）時間 */
        annualLeaveGrantPreTime: KnockoutObservable<number> = ko.observable(0);
        /* 半休残数（付与前）回数*/
        numberOfRemainGrantPre: KnockoutObservable<number> = ko.observable(0);
        /* 時間年休上限（付与前）*/
        timeAnnualLeaveWithMinusGrantPre: KnockoutObservable<number> = ko.observable(0);
        /* 年休残数（付与後）日数 */
        annualLeaveGrantPostDay: KnockoutObservable<number> = ko.observable(0);
        /* 年休残数（付与後）時間*/
        annualLeaveGrantPostTime: KnockoutObservable<number> = ko.observable(0);
        /* 半休残数（付与後）回数*/
        numberOfRemainGrantPost: KnockoutObservable<number> = ko.observable(0);
        /* 時間年休上限（付与後））*/
        timeAnnualLeaveWithMinusGrantPost: KnockoutObservable<number> = ko.observable(0);
        /*年休残数日数 */
        annualLeaveGrantDay: KnockoutObservable<number> = ko.observable(0);
        /* 年休残数時間*/
        annualLeaveGrantTime: KnockoutObservable<number> = ko.observable(null);
        /* 半休残数回数*/
        numberOfRemainGrant: KnockoutObservable<number> = ko.observable(null);
        /* 時間年休上限*/
        timeAnnualLeaveWithMinusGrant: KnockoutObservable<number> = ko.observable(null);
        /* 出勤率*/
        attendanceRate: KnockoutObservable<number> = ko.observable(0);
        /* 労働日数*/
        workingDays: KnockoutObservable<number> = ko.observable(0);
        constructor(data?) {
            if (data) {
                this.annualLeaveGrantPreDay(data.annualLeaveGrantPreDay);
                this.annualLeaveGrantPreDay(data.annualLeaveGrantPreDay);
                this.annualLeaveGrantPreTime(data.annualLeaveGrantPreTime);
                this.numberOfRemainGrantPre(data.numberOfRemainGrantPre);
                this.timeAnnualLeaveWithMinusGrantPre(data.timeAnnualLeaveWithMinusGrantPre);
                this.annualLeaveGrantPostDay(data.annualLeaveGrantPostDay);
                this.annualLeaveGrantPostTime(data.annualLeaveGrantPostTime);
                this.numberOfRemainGrantPost(data.numberOfRemainGrantPost);
                this.timeAnnualLeaveWithMinusGrantPost(data.timeAnnualLeaveWithMinusGrantPost);
                this.attendanceRate(data.attendanceRate);
                this.workingDays(data.workingDays);
                this.annualLeaveGrantDay(data.annualLeaveGrantDay);
                this.annualLeaveGrantTime(data.annualLeaveGrantTime);
                this.numberOfRemainGrant(data.numberOfRemainGrant);
                this.timeAnnualLeaveWithMinusGrant(data.timeAnnualLeaveWithMinusGrant);
            }
        }
    }

    export class AnnualLeaveGrant {
        /*付与日 */
        grantDate: KnockoutObservable<string> = ko.observable("");
        /* 付与数*/
        grantNumber: KnockoutObservable<number> = ko.observable(0);
        /* 使用日数 */
        daysUsedNo: KnockoutObservable<number> = ko.observable(0);
        /* 使用時間*/
        usedMinutes: KnockoutObservable<number> = ko.observable(0);
        /* 残日数*/
        remainDays: KnockoutObservable<number> = ko.observable(0);
        /* 残時間*/
        remainMinutes: KnockoutObservable<number> = ko.observable(0);
        /* 期限*/
        deadline: KnockoutObservable<String> = ko.observable("");
        // で期限切れ
        expiredInCurrentMonthFg: KnockoutObservable<Boolean> = ko.observable(false);
        constructor(data?) {
            if (data) {
                this.grantDate(data.grantDate);
                this.grantNumber(data.grantNumber);
                this.daysUsedNo(data.daysUsedNo);
                this.usedMinutes(data.usedMinutes);
                this.remainDays(data.remainDays);
                this.remainMinutes(data.remainMinutes);
                this.deadline(data.deadline);
                this.expiredInCurrentMonthFg(data.expiredInCurrentMonthFg);
            }

        }
    }
    export class AnnualLeaveManageInfor {
        ymd: KnockoutObservable<String> = ko.observable("");
        daysUsedNo: KnockoutObservable<number> = ko.observable(0);
        usedMinutes: KnockoutObservable<number> = ko.observable(0);
        scheduleRecordAtr: KnockoutObservable<number> = ko.observable(0);
        workTypeCode: KnockoutObservable<string> = ko.observable("");
        workTypeName: KnockoutObservable<string> = ko.observable("");
        constructor(data?) {
            if (data) {
                this.ymd(data.ymd);
                this.daysUsedNo(data.daysUsedNo);
                this.usedMinutes(data.usedMinutes);
                this.scheduleRecordAtr(data.scheduleRecordAtr);
                this.workTypeCode(data.workTypeCode);
                this.workTypeName(data.workTypeName);
            }
        }
    }

    export interface IAnnualHoliday {
        employees: Array<any>;
        annualLeaveGrant: Array<any>;
        attendNextHoliday: any;
        reNumAnnLeave: IReNumAnnLeaReferenceDateImport;
        annualSet: any;
        annualLeaveManagementFg: boolean;
        closingPeriod: string;
    }

    export class AttendRateAtNextHoliday {
        /** 次回年休付与日 */
        nextHolidayGrantDate: KnockoutObservable<String> = ko.observable("");
        /** 次回年休付与日数 */
        nextHolidayGrantDays: KnockoutObservable<number> = ko.observable(0);
        /** 出勤率 */
        attendanceRate: KnockoutObservable<number> = ko.observable(0);
        /** 出勤日数 */
        attendanceDays: KnockoutObservable<number> = ko.observable(0);
        /** 所定日数 */
        predeterminedDays: KnockoutObservable<number> = ko.observable(0);
        /** 年間所定日数 */
        annualPerYearDays: KnockoutObservable<number> = ko.observable(0);

        constructor(data?) {
            if (data) {
                this.nextHolidayGrantDate(data.nextHolidayGrantDate);
                this.nextHolidayGrantDays(data.nextHolidayGrantDays);
                this.attendanceRate(data.attendanceRate);
                this.attendanceDays(data.attendanceDays);
                this.predeterminedDays(data.predeterminedDays);
                this.annualPerYearDays(data.annualPerYearDays);
            }
        }

    }

    export interface IReNumAnnLeaReferenceDateImport {

        annualLeaveRemainNumberExport: IAnnualLeaveRemainingNumberImport;

        annualLeaveGrantExports: Array<IAnnualLeaveGrantImport>;

        annualLeaveManageInforExports: Array<IAnnualLeaveManageInforImport>;
    }

    export interface IAnnualLeaveRemainingNumberImport {
        /* 年休残数（付与前）日数*/
        annualLeaveGrantPreDay: number;
        /*年休残数（付与前）時間*/
        annualLeaveGrantPreTime: number;
        /* 半休残数（付与前）回数 */
        numberOfRemainGrantPre: number;
        /* 時間年休上限（付与前）*/
        timeAnnualLeaveWithMinusGrantPre: number;
        /* 年休残数（付与後）日数*/
        annualLeaveGrantPostDay: number;
        /* 年休残数（付与後）時間*/
        annualLeaveGrantPostTime: number;
        /* 半休残数（付与後）回数 */
        numberOfRemainGrantPost: number;
        /* 時間年休上限（付与後））*/
        timeAnnualLeaveWithMinusGrantPost: number;
        /* 出勤率*/
        attendanceRate: number;
        /*労働日数*/
        workingDays: number;
    }

    export interface IAnnualLeaveGrantImport {
        /* 付与日*/
        grantDate: Date;
        /* 付与数*/
        grantNumber: number;
        /* 使用日数*/
        daysUsedNo: number;
        /* 使用時間*/
        usedMinutes: number;
        /* 残日数*/
        remainDays: number;
        /* 残時間*/
        remainMinutes: number;
        /* 期限 */
        deadline: Date;
        // で期限切れ
        expiredInCurrentMonthFg: boolean;
    }

    export interface IAnnualLeaveManageInforImport {
        /* 年月日*/
        ymd: Date;
        /* 使用日数*/
        daysUsedNo: number;
        /* 使用時間*/
        usedMinutes: number;
        /* 予定実績区分*/
        scheduleRecordAtr: number;
        /* 勤務種類*/
        workTypeCode: string;

        workTypeName: string;
    }

    export class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    export enum CreateAtr {
        /** 予定 */
        "予定",
        /** 実績 */
        "実績",
        /** 申請(事前) */
        "申請(事前)",
        /** 申請(事後) */
        "申請(事後)",
        /**フレックス補填   */
        "フレックス補填"
    }

    export interface UnitModel {
        code: string;
        id: string;
        name?: string;
        workplaceName?: string;
        isAlreadySetting?: boolean;
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