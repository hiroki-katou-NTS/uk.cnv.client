module nts.uk.at.view.kdm001.i.viewmodel {
    import model = kdm001.share.model;
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    import dialog = nts.uk.ui.dialog;
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
        itemListHoliday: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getNumberOfDays());
        selectedCodeHoliday: KnockoutObservable<number> = ko.observable(null);
        itemListSubHoliday: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getNumberOfDays());
        selectedCodeSubHoliday: KnockoutObservable<number> = ko.observable(null);
        itemListOptionSubHoliday: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getNumberOfDays());
        selectedCodeOptionSubHoliday: KnockoutObservable<string> = ko.observable('');
        closureId: KnockoutObservable<number> = ko.observable(0);
        //RemaningDay
        numberHoliday: KnockoutObservable<string> = ko.observable('');
        numberSubHoliday: KnockoutObservable<string> = ko.observable('');
        numberSplitHoliday: KnockoutObservable<string> = ko.observable('');
        totalDay: KnockoutObservable<number> = ko.observable(null);
        unit: KnockoutObservable<string> = ko.observable('');
        unitDay: KnockoutObservable<string> = ko.observable('日');
  
        constructor() {
            let self = this;
            self.initScreen();
            self.selectedCodeHoliday(self.itemListHoliday()[0].code);
            self.selectedCodeSubHoliday(self.itemListSubHoliday()[0].code);
            self.selectedCodeOptionSubHoliday(self.itemListOptionSubHoliday()[0].code);
            
            if(self.checkedHoliday() && self.checkedSubHoliday()){
                if(self.dateHoliday() == null && self.dateSubHoliday() == null){
                    self.dayRemaining("");
                }
            }
            
            //休出残数算出処理
            self.checkedHoliday.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: v,
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: self.checkedSplit(),
                    value1: self.selectedCodeHoliday(),
                    value2: self.selectedCodeSubHoliday(),
                    value3: self.selectedCodeOptionSubHoliday()
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));
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
            });
            self.selectedCodeHoliday.subscribe((v) => {
                let remainDayObject = {
                    checkBox1: self.checkedHoliday(),
                    checkBox2: self.checkedSubHoliday(),
                    checkBox3: self.checkedSplit(),
                    value1: v,
                    value2: self.selectedCodeSubHoliday(),
                    value3: self.selectedCodeOptionSubHoliday()
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
                    value3: self.selectedCodeOptionSubHoliday()
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
                    value3: v
                }
                self.dayRemaining(self.getRemainDay(remainDayObject));
            });
        }
        getRemainDay(remainObject: any): string {
            if (!remainObject.checkBox1 && !remainObject.checkBox2 && !remainObject.checkBox3) {
                return "";
            } else if (remainObject.checkBox1 && !remainObject.checkBox2 && !remainObject.checkBox3) {
                return remainObject.value1;
            } else if (remainObject.checkBox1 && remainObject.checkBox2 && !remainObject.checkBox3) {
                if (remainObject.value1 == null && remainObject.value2 == null) {
                    return "";
                } else {
                    return (remainObject.value1 - remainObject.value2).toString();
                }
            } else if (remainObject.checkBox1 && remainObject.checkBox2 && remainObject.checkBox3) {
                return (remainObject.value1 - remainObject.value2 - remainObject.value3).toString();
            } else if (remainObject.checkBox1 && !remainObject.checkBox2 && remainObject.checkBox3) {
                return (remainObject.value1 - remainObject.value3).toString();
            } else if (!remainObject.checkBox1 && remainObject.checkBox2 && !remainObject.checkBox3) {
                return (remainObject.checkBox1 - remainObject.value2).toString();
            } else if (!remainObject.checkBox1 && remainObject.checkBox2 && remainObject.checkBox3) { 
                return (remainObject.checkBox1 - (remainObject.value2 + remainObject.value3)).toString();
            } else if (!remainObject.checkBox1 && !remainObject.checkBox2 && remainObject.checkBox3) {
                return remainObject.value3;
            }
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
                self.closureId(info.closure.closureId);
            }
            block.clear();
        }
        /**
         * closeDialog
         */
        public submitData() {
            let self = this;
            errors.clearAll();
            if(self.checkedHoliday()){
                    $("#I6_1").trigger("validate");
                    $("#I8_1").trigger("validate");
                }
            if(self.checkedSubHoliday()){
                    $("#I11_1").trigger("validate");
                    if(self.checkedSplit()){
                        $("#I12_2").trigger("validate");
                    }
                }
 
            if (!errors.hasError()) {
                block.invisible();
                let data = {
                    employeeId: self.employeeId(),
                    checkedHoliday: self.checkedHoliday(),
                    dateHoliday: moment.utc(self.dateHoliday(), 'YYYY/MM/DD').toISOString(),
                    selectedCodeHoliday: self.selectedCodeHoliday(),
                    duedateHoliday: moment.utc(self.duedateHoliday(), 'YYYY/MM/DD').toISOString(),
                    checkedSubHoliday: self.checkedSubHoliday(),
                    dateSubHoliday: moment.utc(self.dateSubHoliday(), 'YYYY/MM/DD').toISOString(),
                    selectedCodeSubHoliday: self.selectedCodeSubHoliday(),
                    checkedSplit: self.checkedSplit(),
                    dateOptionSubHoliday: moment.utc(self.dateOptionSubHoliday(), 'YYYY/MM/DD').toISOString(),
                    selectedCodeOptionSubHoliday: self.selectedCodeOptionSubHoliday(),                 
                    dayRemaining: Math.abs(parseInt(self.dayRemaining())),
                    closureId: self.closureId()
                };
                if (!self.checkedSubHoliday()) {
                    data.selectedCodeSubHoliday = 0;
                }
                console.log(data);
                service.add(data).done(result => {
                    if (result && result.length > 0) {
                        for (let errorId of result) {
                            if (errorId === "Msg_737_holiday") {
                                $('#I6_1').ntsError('set', { messageId: "Msg_737" });
                            }
                            else if (errorId === "Msg_746") {
                                $('#I11_1').ntsError('set', { messageId: "Msg_746" });
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
                            else if (errorId === "Msg_745_1") {
                                $('#I6_1').ntsError('set', { messageId: "Msg_745" });
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
                            else if (errorId === "Msg_746_1") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_746" });
                            }
                            else if (errorId === "Msg_744") {
                                $('#I12_2').ntsError('set', { messageId: "Msg_744" });
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
                    //情報メッセージ　Msg_15 登録しました。を表示する。
                    dialog.info({ messageId: "Msg_15" }).then(() => {
                        setShared('KDM001_I_PARAMS_RES', { isChanged: true });
                        nts.uk.ui.windows.close();
                    });
                });
                block.clear();
            }
        }
        public closeDialog() {
            nts.uk.ui.windows.close();
        }
    }
}

