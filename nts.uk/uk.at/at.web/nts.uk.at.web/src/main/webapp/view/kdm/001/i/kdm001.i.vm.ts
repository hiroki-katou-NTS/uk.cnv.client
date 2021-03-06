module nts.uk.at.view.kdm001.i.viewmodel {
    import model = kdm001.share.model;
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    import dialog = nts.uk.ui.dialog;
    import modal = nts.uk.ui.windows.sub.modal;
    import getText = nts.uk.resource.getText;
    export class ScreenModel {
        employeeId: KnockoutObservable<string> = ko.observable('');
        workCode: KnockoutObservable<string> = ko.observable('');
        workplaceName: KnockoutObservable<string> = ko.observable('');
        employeeCode: KnockoutObservable<string> = ko.observable('');
        employeeName: KnockoutObservable<string> = ko.observable('');
        dayRemaining: KnockoutObservable<string> = ko.observable("");
        checkedHoliday: KnockoutObservable<boolean> = ko.observable(true);
        checkedSubHoliday: KnockoutObservable<boolean> = ko.observable(true);
        checkedSplit: KnockoutObservable<boolean> = ko.observable(false);
        dateHoliday: KnockoutObservable<string> = ko.observable('');
        duedateHoliday: KnockoutObservable<string> = ko.observable('');
        dateSubHoliday: KnockoutObservable<string> = ko.observable('');
        dateOptionSubHoliday: KnockoutObservable<string> = ko.observable('');
        dateOffOptionSubHoliday: KnockoutObservable<string> = ko.observable('');
        itemListHoliday: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getNumberDays());
        selectedCodeHoliday: KnockoutObservable<number> = ko.observable(null);
        itemListSubHoliday: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getNumberDays());
        selectedCodeSubHoliday: KnockoutObservable<number> = ko.observable(null);
        itemListOptionSubHoliday: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getNumberDays());
        selectedCodeOptionSubHoliday: KnockoutObservable<string> = ko.observable(null);
        closureId: KnockoutObservable<number> = ko.observable(0);
        listLinkingDate: KnockoutObservableArray<string> = ko.observableArray([]);
        //RemaningDay
        numberHoliday: KnockoutObservable<string> = ko.observable('');
        numberSubHoliday: KnockoutObservable<string> = ko.observable('');
        numberSplitHoliday: KnockoutObservable<string> = ko.observable('');
        totalDay: KnockoutObservable<number> = ko.observable(null);
        unitDay: KnockoutObservable<string> = ko.observable(getText('KDM001_27'));
        baseDate: KnockoutObservable<string> = ko.observable('');
        isDisableOpenKDL036: KnockoutObservable<boolean> = ko.observable(true);
        leaveManagementDatas: LeaveManagementData[] = [];

        // Update ver 48
        residualNumber: KnockoutObservable<number> = ko.observable(0);
        kdl036Shared: KnockoutObservableArray<HolidayWorkSubHolidayLinkingMng> = ko.observableArray([]);
        displayLinkingDate: KnockoutComputed<any[]> = ko.computed(() => {
            let displayLinkingDate: any[] = [];
            if (!this.checkedSubHoliday()) {
                return displayLinkingDate;
            }
            if (this.checkedHoliday()) {
                displayLinkingDate = !_.isEmpty(this.dateHoliday())
                    ? [{outbreakDay: moment.utc(this.dateHoliday()).format('YYYY/MM/DD'), dayNumberUsed: 0}]
                    : [];
            } else if (!_.isEmpty(this.kdl036Shared())) {
                displayLinkingDate = this.kdl036Shared();
            } else {
                if (this.selectedCodeSubHoliday() === 0.5) {
                    let item: LeaveManagementData = _.find(this.leaveManagementDatas, item => item.unUsedDays === 0.5)
                    if (_.isNil(item)) {
                        item = _.find(this.leaveManagementDatas, item => item.unUsedDays === 1);
                        if (!_.isNil(item)) {
                            displayLinkingDate = [{outbreakDay: moment.utc(item.dayoffDate).format('YYYY/MM/DD'), dayNumberUsed: 1.0}];
                        }
                    } else {
                        displayLinkingDate = [{outbreakDay: moment.utc(item.dayoffDate).format('YYYY/MM/DD'), dayNumberUsed: 0.5}];
                    }
                }

                if (this.selectedCodeSubHoliday() === 1.0) {
                    const item: LeaveManagementData = _.find(this.leaveManagementDatas, item => item.unUsedDays === 1);
                    if (!_.isNil(item)) {
                        return [{outbreakDay: moment.utc(item.dayoffDate).format('YYYY/MM/DD'), dayNumberUsed: 1.0}];
                    }
                    displayLinkingDate = [];
                    _.forEach(this.leaveManagementDatas, item => {
                        if (displayLinkingDate.length <= 2 && item.unUsedDays === 0.5) {
                            displayLinkingDate.push({
                                outbreakDay: moment.utc(item.dayoffDate).format('YYYY/MM/DD'),
                                dayNumberUsed: item.unUsedDays
                            });
                        }
                    });
                }
            }
            return displayLinkingDate;
        });

        linkingDate: KnockoutComputed<number> = ko.computed(() => {
            if (this.checkedHoliday()) {
                return 0.0;
            }
            let total = 0.0;
            _.forEach(this.kdl036Shared(), (item: any) => total += item.dayNumberUsed);
            return total;
        });

        displayRemainDays: KnockoutComputed<number> = ko.computed(() => this.residualNumber() + parseFloat(this.dayRemaining()));
        // End update ver48

        constructor() {
            let self = this;
            self.initScreen();
            //????????????????????????
            self.checkedHoliday.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: v,
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: self.checkedSplit(),
                    value1: self.selectedCodeHoliday(),
                    value2: self.selectedCodeSubHoliday(),
                    value3: self.selectedCodeOptionSubHoliday()
                };
                self.dayRemaining(self.getRemainDay(remainDayObject));
                if (!v) {
                    $("#I6_1").ntsError('clear');
                    $("#I6_3").ntsError('clear');
                    $("#I8_1").ntsError('clear');
                } else {
                  self.baseDate(self.dateHoliday());
                    _.defer(() => { $("#I6_3").ntsError('clear'); });
                }
                if(!v && self.checkedSubHoliday()) {
                  self.isDisableOpenKDL036(false);
                }else {
                  self.isDisableOpenKDL036(true)
                }
            });
            self.checkedSubHoliday.subscribe((v) => {

                let remainDayObject = {
                    checkBox1: self.checkedHoliday(),
                    checkBox2: v,
                    checkBox3: self.checkedSplit(),
                    value1: self.selectedCodeHoliday(),
                    value2: self.selectedCodeSubHoliday(),
                    value3: self.selectedCodeOptionSubHoliday()
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));
                if (!v) {
                    $("#I11_1").ntsError('clear');
                    $("#I11_3").ntsError('clear');
                } else {
                    $("#I11_3").ntsError('clear');
                }
                if(v && !self.checkedHoliday()) {
                  self.isDisableOpenKDL036(false);
                }else {
                  self.isDisableOpenKDL036(true);
                }
            });
            self.checkedSplit.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: self.checkedHoliday(),
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: v,
                    value1: self.selectedCodeHoliday(),
                    value2: self.selectedCodeSubHoliday(),
                    value3: self.selectedCodeOptionSubHoliday()
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));

                if (!v) {
                    $("#I12_2").ntsError('clear');
                    $("#I12_4").ntsError('clear');
                } else {
                    $("#I12_4").ntsError('clear');
                }
            });
            self.selectedCodeHoliday.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: self.checkedHoliday(),
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: self.checkedSplit(),
                    value1: v,
                    value2: self.selectedCodeSubHoliday(),
                    value3: self.selectedCodeOptionSubHoliday(),
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));
            });
            self.selectedCodeSubHoliday.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: self.checkedHoliday(),
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: self.checkedSplit(),
                    value1: self.selectedCodeHoliday(),
                    value2: v,
                    value3: self.selectedCodeOptionSubHoliday(),
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));
            });
            self.selectedCodeOptionSubHoliday.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: self.checkedHoliday(),
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: self.checkedSplit(),
                    value1: self.selectedCodeHoliday(),
                    value2: self.selectedCodeSubHoliday(),
                    value3: v,
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));
            });
            
            self.checkedSubHoliday.subscribe((v) => {
                let self = this;
                if (!self.checkedSubHoliday()) {
                    self.checkedSplit(false);
                }
            });
            
        }

        getRemainDay(remainObject: any): string {
         const vm  = this;
            if ((!remainObject.checkBox1 && !remainObject.checkBox2) || (!remainObject.value1 && !remainObject.value2)) {
                return "";
            }
            //??????.????????????
            let value1 = !remainObject.checkBox1 || !remainObject.value1? 0 : remainObject.value1;
            //??????.????????????
            let value2 = !remainObject.checkBox2 || !remainObject.value2 ? 0 : remainObject.value2;
            //????????????.????????????
            let value3 = !remainObject.checkBox2 || !remainObject.checkBox3 || !remainObject.value3 ? 0 : remainObject.value3;

            return (value1 - (value2 + value3)).toString();
        }

        initScreen(): void {
            block.invisible();
            let self = this,
                info = getShared("KDM001_I_PARAMS");
            if (info) {
                self.workCode(info.selectedEmployee.workplaceCode);
                self.workplaceName(info.selectedEmployee.workplaceName);
                self.employeeId(info.selectedEmployee.employeeId);
                self.employeeCode(info.selectedEmployee.employeeCode);
                self.employeeName(info.selectedEmployee.employeeName);
                if (info.closure && info.closure.closureId) {
                    self.closureId(info.closure.closureId);
                }
                self.residualNumber(info.residualNumber);

                service.getByIdAndUnUse(self.employeeId(), info.closure.closureId)
                    .then(response => {
                        self.leaveManagementDatas = response;
                    });
            }
            block.clear();
        }
        /**
         * closeDialog
         */
        public submitData() {
            const vm = this;
            errors.clearAll();
            if (vm.checkedHoliday()) {
                $("#I6_1").trigger("validate");
                $("#I6_3").trigger("validate");
                $("#I8_1").trigger("validate");
            }
            if (vm.checkedSubHoliday()) {
                $("#I11_1").trigger("validate");
                $("#I11_3").trigger("validate");
                if (vm.checkedSplit()) {
                    $("#I12_2").trigger("validate");
                    $("#I12_4").trigger("validate");
                }
            }
            if (!errors.hasError()) {
                block.invisible();

                const linkingDates = _.map(vm.displayLinkingDate(), item => moment.utc(item.outbreakDay).format('YYYY-MM-DD'));
                const selectedCodeHoliday: number = vm.checkedHoliday() ? vm.selectedCodeHoliday() : 0;0;
                const selectedCodeSubHoliday: number = vm.checkedSubHoliday() ? vm.selectedCodeSubHoliday() : 0.0;
                const selectedCodeOptionSubHoliday: number = vm.checkedSplit() ? parseFloat(vm.selectedCodeOptionSubHoliday()) : 0.0;
                const linkingDate: number = _.reduce(vm.displayLinkingDate(), (sum, item) => sum + item.dayNumberUsed, 0);
                const dayRemaining = linkingDate + selectedCodeHoliday - selectedCodeSubHoliday - selectedCodeOptionSubHoliday;
                let data = {
                    employeeId: vm.employeeId(),
                    checkedHoliday: vm.checkedHoliday(),
                    dateHoliday: moment.utc(vm.dateHoliday(), 'YYYY/MM/DD').toISOString(),
                    selectedCodeHoliday: vm.selectedCodeHoliday(),
                    duedateHoliday: moment.utc(vm.duedateHoliday(), 'YYYY/MM/DD').toISOString(),
                    checkedSubHoliday: vm.checkedSubHoliday(),
                    dateSubHoliday: moment.utc(vm.dateSubHoliday(), 'YYYY/MM/DD').toISOString(),
                    selectedCodeSubHoliday: vm.selectedCodeSubHoliday(),
                    checkedSplit: vm.checkedSplit(),
                    dateOptionSubHoliday: moment.utc(vm.dateOptionSubHoliday(), 'YYYY/MM/DD').toISOString(),
                    selectedCodeOptionSubHoliday: vm.selectedCodeOptionSubHoliday(),
                    dayRemaining: dayRemaining,
                    closureId: vm.closureId(),
                    lstLinkingDate: linkingDates,
                    linkingDate: vm.linkingDate(),
                    displayRemainDays: vm.displayRemainDays()
                };
                if (!vm.checkedSubHoliday()) {
                    data.selectedCodeSubHoliday = 0;
                }
                service.add(data).done(result => {
                    if (result && result.length > 0) {
                        for (let errorId of result) {
                            if (errorId === "Msg_737_holiday") {
                                $('#I6_1').ntsError('set', { messageId: "Msg_737" });
                            }
                            else if (errorId === "Msg_1440") {
                                $('#I11_1').ntsError('set', { messageId: "Msg_1440" });
                            }
                            else if (errorId === "Msg_728") {
                                $('#I4').ntsError('set', { messageId: errorId });
                            }
                            else if (errorId === "Msg_737_sub_holiday") {
                                $('#I11_1').ntsError('set', { messageId: "Msg_737" });
                            }
                            else if (errorId === "Msg_737_sub_option_holiday") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_737" });
                            }
                            else if (errorId === "Msg_737_sub_option_holiday_2") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_737" });
                            }
                            else if (errorId === "Msg_1439") {
                                $('#I6_1').ntsError('set', { messageId: "Msg_1439" });
                            }
                            else if (errorId === "Msg_745_2") {
                                $('#I11_1').ntsError('set', { messageId: "Msg_745" });
                            }
                            else if (errorId === "Msg_730") {
                                $('#I11_1').ntsError('set', { messageId: "Msg_730" });
                            }
                            else if (errorId === "Msg_730_1") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_730" });
                            }
                            else if (errorId === "Msg_1442") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_1442" });
                            }
                            else if (errorId === "Msg_1441") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_1441" });
                            }
                            else if (errorId === "Msg_1259") {
                                $('#I11_3').ntsError('set', { messageId: "Msg_1259" });
                            }
                            else if (errorId === "Msg_1256_1") {
                                $('#I11_3').ntsError('set', { messageId: "Msg_1256" });
                            }
                            else if (errorId === "Msg_1256_2") {
                                $('#I12_4').ntsError('set', { messageId: "Msg_1256" });
                            }
                            else if (errorId === "Msg_1256_3") {
                                $('#I6_3').ntsError('set', { messageId: "Msg_1256" });
                            }
                        }
                        return;
                    }
                    //????????????????????????Msg_15 ???????????????????????????????????????
                    dialog.info({ messageId: "Msg_15" }).then(() => {
                        setShared('KDM001_I_PARAMS_RES', { isChanged: true });
                        setShared('KDM001_I_SUCCESS', {isSuccess: true})
                        nts.uk.ui.windows.close();
                    });
                })
                .fail(err => {
                    dialog.info(err);
                });
                block.clear();
            }
        }
        public closeDialog() {
            nts.uk.ui.windows.close();
        }

        public openKDL036() {
            const vm = this;
            $("#I11_1").trigger("validate");
            if (!nts.uk.ui.errors.hasError()) {
                let info = getShared("KDM001_I_PARAMS");
                const params: any = {
                    employeeId: info.selectedEmployee.employeeId,
                    period: {
                        startDate: moment.utc(vm.dateSubHoliday()).format('YYYY/MM/DD'),
                        endDate: moment.utc(vm.dateSubHoliday()).format('YYYY/MM/DD')
                    },
                    daysUnit: vm.selectedCodeSubHoliday(),
                    targetSelectionAtr: TargetSelectionAtr.MANUAL,
                    actualContentDisplayList: [],
                    managementData: vm.kdl036Shared()
                };
                setShared('KDL036_PARAMS', params);
                modal("/view/kdl/036/a/index.xhtml").onClosed(() => {
                    const kdl036Shared = getShared('KDL036_RESULT');
                    vm.kdl036Shared(kdl036Shared);
                    let remainDayObject = {
                        checkBox1: vm.checkedHoliday(),
                        checkBox2: vm.checkedSubHoliday(),
                        checkBox3: vm.checkedSplit(),
                        value1: vm.selectedCodeHoliday(),
                        value2: vm.selectedCodeSubHoliday(),
                        value3: vm.selectedCodeOptionSubHoliday(),
                    }
                    vm.dayRemaining(vm.getRemainDay(remainDayObject));
                });
            }
        }
    }

    interface HolidayWorkSubHolidayLinkingMng {
        employeeId: string;
        outbreakDay: string;
        dateOfUse: string;
        dayNumberUsed: number;
        targetSelectionAtr: number;
    }

    interface LeaveManagementData {
        id: string;
        dayoffDate: any;
        unUsedDays: number;
    }

    enum TargetSelectionAtr {
        AUTOMATIC = 0,
        REQUEST = 1,
        MANUAL = 2
    }
}