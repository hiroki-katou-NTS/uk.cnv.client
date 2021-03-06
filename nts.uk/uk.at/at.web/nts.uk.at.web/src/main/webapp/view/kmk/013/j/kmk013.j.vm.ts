module nts.uk.at.view.kmk013.j {
    
    import blockUI = nts.uk.ui.block;
    
    export module viewmodel {
        export class ScreenModel {
            transAttendMethod: KnockoutObservableArray<ItemModel>;
            selectedTransAttendMethod: KnockoutObservable<number>; // J2_4
            
            lstWorkTypeGivenDays: KnockoutObservableArray<WorkType>;
            lstWorkTypeAttendanceDays: KnockoutObservableArray<WorkType>;
            
            workTypeList: KnockoutObservableArray<any>;
            currentItem: KnockoutObservable<WorktypeDisplayDto>;
            currentItemAttendance: KnockoutObservable<WorktypeDisplayDto>;
            workTypeNames: KnockoutObservable<string>;
            workTypeNamesAttendance: KnockoutObservable<string>;
            items: KnockoutObservableArray<WorktypeDisplayDto>;
            useAtr: KnockoutObservable<number>;

            controlSetItems: KnockoutObservableArray<any>;
            selectedControlSetItem: KnockoutObservable<number> = ko.observable(0);
            calculationMethod: KnockoutObservableArray<any>;
            selectedCalculationMethod: KnockoutObservable<number> = ko.observable(0);
            nonCalculationMethod: KnockoutObservableArray<any>;
            selectedNonCalculationMethod: KnockoutObservable<number> = ko.observable(0);
            useAtrItems: KnockoutObservableArray<any>;
            selectedWorkDayItem: KnockoutObservable<number> = ko.observable(0);
            selectedNonWorkDayItem: KnockoutObservable<number> = ko.observable(0);

            verticalTotalMethodOfMon: VerticalTotalMethodOfMon;
            
            constructor() {
                var self = this;
                self.transAttendMethod = ko.observableArray<ItemModel> ([
                    new ItemModel(0, nts.uk.resource.getText("KMK013_311")),
                    new ItemModel(1, nts.uk.resource.getText("KMK013_312"))
                ]);
                self.selectedTransAttendMethod = ko.observable(0);
                self.lstWorkTypeGivenDays = ko.observableArray([]);
                self.lstWorkTypeAttendanceDays = ko.observableArray([]);
                self.items = ko.observableArray([]);
                self.useAtr = ko.observable(0);
                self.workTypeList = ko.observableArray([]);
                self.currentItem = ko.observable(new WorktypeDisplayDto({}));
                self.currentItemAttendance = ko.observable(new WorktypeDisplayDto({}));
                self.workTypeNames = ko.observable("");
                self.workTypeNamesAttendance = ko.observable("");
                self.workTypeNames.subscribe(function(data){
                    // Set tooltip
                    $('#itemname_absenceDay').text(data);
                });
                self.workTypeNamesAttendance.subscribe(function(data) {
                    // Set tooltip
                    $('#itemname_attendanceDay').text(data);
                });
                // Ver 27
                self.controlSetItems = ko.observableArray<ItemModel>([
                    new ItemModel(0, nts.uk.resource.getText("KMK013_353")),
                    new ItemModel(1, nts.uk.resource.getText("KMK013_354")),
                    new ItemModel(2, nts.uk.resource.getText("KMK013_355"))
                ]);
                self.calculationMethod = ko.observableArray<ItemModel>([
                    new ItemModel(0, nts.uk.resource.getText("KMK013_358")),
                    new ItemModel(1, nts.uk.resource.getText("KMK013_359")),
                    new ItemModel(2, nts.uk.resource.getText("KMK013_360"))
                ]);
                self.nonCalculationMethod = ko.observableArray<ItemModel>([
                    new ItemModel(0, nts.uk.resource.getText("KMK013_316")),
                    new ItemModel(1, nts.uk.resource.getText("KMK013_317")),
                    new ItemModel(2, nts.uk.resource.getText("KMK013_318"))
                ]);
                self.useAtrItems = ko.observableArray([
                    { code: 1, name: nts.uk.resource.getText("KMK013_209") },
                    { code: 0, name: nts.uk.resource.getText("KMK013_210") }
                ]);
            }

            // ????????????
            public startPage(): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                var self = this;
                nts.uk.ui.block.invisible();
                $.when(service.init()).done(function(res: IVerticalTotalMethodOfMon) {
                    if (res) {
                        let data = new VerticalTotalMethodOfMon(res);
                        self.verticalTotalMethodOfMon = data;
                        self.selectedControlSetItem(data.config);
                        self.selectedCalculationMethod(data.calculationMethod);
                        self.selectedTransAttendMethod(data.countingDay);
                        self.selectedNonCalculationMethod(data.countingCon);
                        self.selectedWorkDayItem(data.countingWorkDay);
                        self.selectedNonWorkDayItem(data.countingNonWorkDay);
                    }
                    dfd.resolve();
                }).fail((err) => {
                    nts.uk.ui.dialog.alertError({ messageId: err.messageId });
                }).always(() => {
                    nts.uk.ui.block.clear();
                    $('#J3_2').focus();
                });
                return dfd.promise();
            }

            openScreenO() {
                var self = this;

                let vacationPriority = self.verticalTotalMethodOfMon.getOffsetPriority();
                let data = null;
                if (vacationPriority.substituteHoliday == 0 &&
                    vacationPriority.sixtyHourVacation == 0 &&
                    vacationPriority.specialHoliday == 0 &&
                    vacationPriority.annualHoliday == 0) {
                    data = {
                        annual: 2,
                        substitute: 0,
                        sixtyHour: 1,
                        special: 3
                    };
                } else {
                    data = {
                        annual: vacationPriority.annualHoliday,
                        substitute: vacationPriority.substituteHoliday,
                        sixtyHour: vacationPriority.sixtyHourVacation,
                        special: vacationPriority.specialHoliday
                    };
                }

                nts.uk.ui.block.invisible();
                nts.uk.ui.windows.setShared('KMK013_0_Order', data);
                nts.uk.ui.windows.sub.modal("/view/kmk/013/o/index.xhtml").onClosed(() => {

                    var res = nts.uk.ui.windows.getShared("KMK013_O_NewOrder");
                    if (res) {
                        self.verticalTotalMethodOfMon.updateOffsetPriority(
                            res.substitute,
                            res.sixtyHour,
                            res.special,
                            res.annual
                        );
                    }

                });
            }

            // ????????????
            saveData(): void {
                let self = this;
                blockUI.grayout();
                let data = {
                    config: self.selectedControlSetItem(),
                    calculationMethod: self.selectedCalculationMethod(),
                    annualHoliday: self.verticalTotalMethodOfMon.vacationOffset.annualHoliday,
                    substituteHoliday: self.verticalTotalMethodOfMon.vacationOffset.substituteHoliday,
                    sixtyHourVacation: self.verticalTotalMethodOfMon.vacationOffset.sixtyHourVacation,
                    specialHoliday: self.verticalTotalMethodOfMon.vacationOffset.specialHoliday,
                    countingDay: self.selectedTransAttendMethod(),
                    countingCon: self.selectedNonCalculationMethod(),
                    countingWorkDay:  self.selectedWorkDayItem(),
                    countingNonWorkDay: self.selectedNonWorkDayItem(),
                };

                $.when(service.registerVertical(data)).done(()=>{
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                $('#J3_2').focus();
                    });
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                }).always(() => {
                    blockUI.clear();
                });
            }
        }
        
        
        // Class ItemModel
        class ItemModel {
            id: number;
            name: string;
            constructor(id: number, name: string) {
                this.id = id;
                this.name = name;
            }
        }
        
        // Class work type
        class WorkType {
            code: string;
            name: string;
            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
    }
    
    export class WorkTypeModal {
        workTypeCode: string;
        name: string;
        memo: string;
        constructor(param: IWorkTypeModal) {
            this.workTypeCode = param.workTypeCode;
            this.name = param.name;
            this.memo = param.memo;
        }
    }

    export interface IWorkTypeModal {
        workTypeCode: string;
        name: string;
        memo: string;
    }
    
    export class WorktypeDisplayDto {
        useAtr: KnockoutObservable<number>;
        workTypeList: KnockoutObservableArray<WorktypeDisplaySetDto>;
        constructor(param: IWorktypeDisplayDto) {
            this.useAtr = ko.observable(param.useAtr || 0);
            this.workTypeList = ko.observableArray(param.workTypeList || null);
        }
    }

    export interface IWorktypeDisplayDto {
        useAtr?: number;
        workTypeList?: Array<WorktypeDisplaySetDto>;
    }


    export class WorktypeDisplaySetDto {
        workTypeCode: string;
        constructor(param: IWorktypeDisplaySetDto) {
            this.workTypeCode = param.workTypeCode;
        }
    }

    export interface IWorktypeDisplaySetDto {
        workTypeCode?: string;
    }

    class VerticalTotalMethodOfMon {
        config: number; // ?????????????????????????????????.??????
        calculationMethod: number; // ????????????????????????.????????????
        vacationOffset: ITimeVacationOffsetPriority; // // ??????????????????????????????
        countingDay: number; // ???????????????????????????.????????????.??????????????????????????????
        countingCon: number; // ???????????????????????????.?????????.????????????????????????????????????
        countingWorkDay: number; // ???????????????????????????.?????????.??????????????????????????????????????????
        countingNonWorkDay: number; // ???????????????????????????.?????????.????????????????????????????????????????????????

        constructor (param: IVerticalTotalMethodOfMon) {
            this.config = param.config;
            this.calculationMethod = param.calculationMethod;
            this.vacationOffset = {
                annualHoliday: param.annualHoliday,
                substituteHoliday: param.substituteHoliday,
                sixtyHourVacation: param.sixtyHourVacation,
                specialHoliday: param.specialHoliday
            };
            this.countingDay = param.countingDay;
            this.countingCon = param.countingCon;
            this.countingWorkDay = param.countingWorkDay;
            this.countingNonWorkDay = param.countingNonWorkDay;
        }

        updateOffsetPriority(substitute: number, sixtyHour: number, special: number, annual: number) {
            this.vacationOffset.annualHoliday = annual;
            this.vacationOffset.substituteHoliday = substitute;
            this.vacationOffset.sixtyHourVacation = sixtyHour;
            this.vacationOffset.specialHoliday = special;
        }

        getOffsetPriority() {
            return this.vacationOffset;
        }

    }

    interface IVerticalTotalMethodOfMon {
        config: number; // ?????????????????????????????????.??????
        calculationMethod: number; // ????????????????????????.????????????
        annualHoliday: number; // ??????????????????????????????.??????
        substituteHoliday: number; // ??????????????????????????????.??????
        sixtyHourVacation: number; // ??????????????????????????????.60H??????
        specialHoliday: number; // ??????????????????????????????.????????????
        countingDay: number; // ???????????????????????????.????????????.??????????????????????????????
        countingCon: number; // ???????????????????????????.?????????.????????????????????????????????????
        countingWorkDay: number; // ???????????????????????????.?????????.??????????????????????????????????????????
        countingNonWorkDay: number; // ???????????????????????????.?????????.????????????????????????????????????????????????
    }

    interface ITimeVacationOffsetPriority {
        annualHoliday: number; // ??????????????????????????????.??????
        substituteHoliday: number; // ??????????????????????????????.??????
        sixtyHourVacation: number; // ??????????????????????????????.60H??????
        specialHoliday: number; // ??????????????????????????????.????????????
    }

}