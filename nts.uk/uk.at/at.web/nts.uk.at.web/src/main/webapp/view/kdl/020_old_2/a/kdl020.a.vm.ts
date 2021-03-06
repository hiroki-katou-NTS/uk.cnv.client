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
        /** ??????????????? */
        grantDate: KnockoutObservable<String> = ko.observable("");
        /** ???????????? */
        grantDays: KnockoutObservable<number> = ko.observable(null);
        /** ?????? */
        times: KnockoutObservable<number> = ko.observable(null);
        /** ???????????????????????? */
        timeAnnualLeaveMaxDays: KnockoutObservable<number> = ko.observable(null);
        /** ???????????????????????? */
        timeAnnualLeaveMaxTime: KnockoutObservable<number> = ko.observable(null);
        /** ???????????????????????? */
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
        /** ????????????(???)*/
        annualLeaveRemainNumber: KnockoutObservable<AnnualLeaveRemainingNumber> = ko.observable(new AnnualLeaveRemainingNumber());

        /** ??????????????????(???)*/
        annualLeaveGrants: KnockoutObservableArray<AnnualLeaveGrant> = ko.observableArray([]);

        /** ??????????????????(???)*/
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
        /* ?????????????????????????????????*/
        annualLeaveGrantPreDay: KnockoutObservable<number> = ko.observable(0);
        /* ????????????????????????????????? */
        annualLeaveGrantPreTime: KnockoutObservable<number> = ko.observable(0);
        /* ?????????????????????????????????*/
        numberOfRemainGrantPre: KnockoutObservable<number> = ko.observable(0);
        /* ?????????????????????????????????*/
        timeAnnualLeaveWithMinusGrantPre: KnockoutObservable<number> = ko.observable(0);
        /* ????????????????????????????????? */
        annualLeaveGrantPostDay: KnockoutObservable<number> = ko.observable(0);
        /* ?????????????????????????????????*/
        annualLeaveGrantPostTime: KnockoutObservable<number> = ko.observable(0);
        /* ?????????????????????????????????*/
        numberOfRemainGrantPost: KnockoutObservable<number> = ko.observable(0);
        /* ????????????????????????????????????*/
        timeAnnualLeaveWithMinusGrantPost: KnockoutObservable<number> = ko.observable(0);
        /*?????????????????? */
        annualLeaveGrantDay: KnockoutObservable<number> = ko.observable(0);
        /* ??????????????????*/
        annualLeaveGrantTime: KnockoutObservable<number> = ko.observable(null);
        /* ??????????????????*/
        numberOfRemainGrant: KnockoutObservable<number> = ko.observable(null);
        /* ??????????????????*/
        timeAnnualLeaveWithMinusGrant: KnockoutObservable<number> = ko.observable(null);
        /* ?????????*/
        attendanceRate: KnockoutObservable<number> = ko.observable(0);
        /* ????????????*/
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
        /*????????? */
        grantDate: KnockoutObservable<string> = ko.observable("");
        /* ?????????*/
        grantNumber: KnockoutObservable<number> = ko.observable(0);
        /* ???????????? */
        daysUsedNo: KnockoutObservable<number> = ko.observable(0);
        /* ????????????*/
        usedMinutes: KnockoutObservable<number> = ko.observable(0);
        /* ?????????*/
        remainDays: KnockoutObservable<number> = ko.observable(0);
        /* ?????????*/
        remainMinutes: KnockoutObservable<number> = ko.observable(0);
        /* ??????*/
        deadline: KnockoutObservable<String> = ko.observable("");
        // ???????????????
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
        /** ????????????????????? */
        nextHolidayGrantDate: KnockoutObservable<String> = ko.observable("");
        /** ???????????????????????? */
        nextHolidayGrantDays: KnockoutObservable<number> = ko.observable(0);
        /** ????????? */
        attendanceRate: KnockoutObservable<number> = ko.observable(0);
        /** ???????????? */
        attendanceDays: KnockoutObservable<number> = ko.observable(0);
        /** ???????????? */
        predeterminedDays: KnockoutObservable<number> = ko.observable(0);
        /** ?????????????????? */
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
        /* ?????????????????????????????????*/
        annualLeaveGrantPreDay: number;
        /*?????????????????????????????????*/
        annualLeaveGrantPreTime: number;
        /* ????????????????????????????????? */
        numberOfRemainGrantPre: number;
        /* ?????????????????????????????????*/
        timeAnnualLeaveWithMinusGrantPre: number;
        /* ?????????????????????????????????*/
        annualLeaveGrantPostDay: number;
        /* ?????????????????????????????????*/
        annualLeaveGrantPostTime: number;
        /* ????????????????????????????????? */
        numberOfRemainGrantPost: number;
        /* ????????????????????????????????????*/
        timeAnnualLeaveWithMinusGrantPost: number;
        /* ?????????*/
        attendanceRate: number;
        /*????????????*/
        workingDays: number;
    }

    export interface IAnnualLeaveGrantImport {
        /* ?????????*/
        grantDate: Date;
        /* ?????????*/
        grantNumber: number;
        /* ????????????*/
        daysUsedNo: number;
        /* ????????????*/
        usedMinutes: number;
        /* ?????????*/
        remainDays: number;
        /* ?????????*/
        remainMinutes: number;
        /* ?????? */
        deadline: Date;
        // ???????????????
        expiredInCurrentMonthFg: boolean;
    }

    export interface IAnnualLeaveManageInforImport {
        /* ?????????*/
        ymd: Date;
        /* ????????????*/
        daysUsedNo: number;
        /* ????????????*/
        usedMinutes: number;
        /* ??????????????????*/
        scheduleRecordAtr: number;
        /* ????????????*/
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
        /** ?????? */
        "??????",
        /** ?????? */
        "??????",
        /** ??????(??????) */
        "??????(??????)",
        /** ??????(??????) */
        "??????(??????)",
        /**?????????????????????   */
        "?????????????????????"
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