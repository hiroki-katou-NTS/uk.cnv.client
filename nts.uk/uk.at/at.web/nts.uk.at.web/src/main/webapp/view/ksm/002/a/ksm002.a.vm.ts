module ksm002.a.viewmodel {
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    export class ScreenModel {
        //MODE
        isNew: KnockoutObservable<boolean>;
        //PANE
        boxItemList: KnockoutObservableArray<BoxModel>;
        fullBoxItemList: KnockoutObservableArray<BoxModel>;
        selectedIds: KnockoutObservableArray<number>;
        enable: KnockoutObservable<boolean>;
        //current Date
        currentYear: KnockoutObservable<String>;
        currentMonth: KnockoutObservable<String>;
        endDateOfMonth: KnockoutObservable<number>;
        //Calendar
        calendarData: KnockoutObservable<any>;
        yearMonthPicked: KnockoutObservable<number>;
        cssRangerYM: any;
        optionDates: KnockoutObservableArray<OptionalDate>;
        firstDay: KnockoutObservable<number>;
        yearMonth: KnockoutObservable<number>;
        startDate: number;
        endDate: number;
        workplaceId: KnockoutObservable<string>;
        eventDisplay: KnockoutObservable<boolean>;
        eventUpdatable: KnockoutObservable<boolean>;
        holidayDisplay: KnockoutObservable<boolean>;
        cellButtonDisplay: KnockoutObservable<boolean>;
        workplaceName: KnockoutObservable<string>;
        //data server 
        serverSource: Array<OptinalDate> = [];
        dateFormat = "YYYY/MM/DD";
        constructor() {
            var self = this;
            self.isNew = ko.observable(true);
            self.boxItemList = ko.observableArray([]);
            self.fullBoxItemList = ko.observableArray([]);
            self.selectedIds = ko.observableArray([]);
            self.enable = ko.observable(true);
            //Calendar
            self.yearMonthPicked = ko.observable(Number(moment(new Date()).format("YYYYMM")));
            self.cssRangerYM = {};
            self.optionDates = ko.observableArray([]);

            self.firstDay = ko.observable(0);
            self.startDate = 1;
            self.endDate = 31;
            self.workplaceId = ko.observable("0");
            self.workplaceName = ko.observable("");
            self.eventDisplay = ko.observable(true);
            self.eventUpdatable = ko.observable(false);
            self.holidayDisplay = ko.observable(true);
            self.cellButtonDisplay = ko.observable(true);
            
            $("#calendar").ntsCalendar("init", {
                buttonClick: function(date) {
                    self.openKsm002EDialog(date);
                },
                cellClick: function(date) {
                    let param: IData = { date: date, selectable: _.map(self.boxItemList(), 'id'), selecteds: self.selectedIds() };
                    self.setSpecificItemToSelectedDate(param);
                }
            });
            //Side bar tab change
            $("#sidebar").ntsSideBar("init", {
                active: 0,
                activate: (event, info) => {
                    if (info.newIndex == 1) {
                        nts.uk.ui._viewModel.content.viewModelB.start(true);
                    } else {
                        self.start();
                    }
                }
            });
            //Change Month 
            self.yearMonthPicked.subscribe(function(value) {
                let arrOptionaDates: Array<OptionalDate> = [];
                self.getDataToOneMonth(value).done(function(arrOptionaDates) {
                    if (arrOptionaDates.length > 0) {
                        self.optionDates(arrOptionaDates);
                        self.optionDates.valueHasMutated();
                        self.isNew(false);
                    } else {
                        self.isNew(true);
                    }
                })
            })
        }

        /** 
         *Start page 
         */
        start(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();
            let isUse: number = 1;
            let arrOptionaDates: Array<OptionalDate> = [];
//            self.showExportBtn();
            service.getSpecificDateByIsUse(isUse).done(function(lstSpecifiDate: any) {
                if (lstSpecifiDate.length > 0) {
                    //getAll SpecDate
                    self.getAllSpecDate();
                    //Set Start Day of Company
                    nts.uk.characteristics.restore('IndividualStartDay').done(function(data) {
                        if (nts.uk.util.isNullOrEmpty(data)) {
                            self.getComStartDay().done(function(startDay: number) {
                                self.firstDay(startDay);
                            });
                        } else {
                            self.firstDay(data);
                        }
                    });
                    //set parameter to calendar
                    let lstBoxCheck: Array<BoxModel> = [];
                    _.forEach(lstSpecifiDate, function(item) {
                        lstBoxCheck.push(new BoxModel(item.specificDateItemNo, item.specificName));
                    });
                    self.boxItemList(_.orderBy(lstBoxCheck, ['id'], ['asc']));
                    //Set data to calendar
                    self.getDataToOneMonth(self.yearMonthPicked()).done(function(arrOptionaDates) {
                        if (arrOptionaDates.length > 0) {
                            self.optionDates(arrOptionaDates);
                            self.optionDates.valueHasMutated();
                            self.isNew(false);
                        }
                        dfd.resolve();
                    })
                } else {
                    //In Case no Data, openCDialog                    self.openKsm002CDialog();
                    dfd.resolve();
                };
            }).fail(function(res) {
                nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
                dfd.reject();
            });
            return dfd.promise();
        }
        
        showExportBtn() {
            if (nts.uk.util.isNullOrUndefined(__viewContext.user.role.attendance)
                && nts.uk.util.isNullOrUndefined(__viewContext.user.role.payroll)
                && nts.uk.util.isNullOrUndefined(__viewContext.user.role.officeHelper)
                && nts.uk.util.isNullOrUndefined(__viewContext.user.role.personnel)) {
                $("#print-button_1").hide();
            } else {
                $("#print-button_1").show();
            }
        }

        /**
         * Fill data to each month
         */
        getDataToOneMonth(processMonth: string): JQueryPromise<Array<OptionalDate>> {
            var self = this;
            let dfd = $.Deferred<any>();
            let endOfMonth: number = moment(processMonth, "YYYYMM").endOf('month').date();
            let isUse: number = 1;
            let arrOptionaDates: Array<OptionalDate> = [];
            let root: Array<IOptionalDate> = [];
            //Array Name to fill on  one Date
            let arrName: Array<string> = [];
            let arrId: Array<string> = [];
            let selectedDate = moment(processMonth, self.dateFormat);
            service.getCompanySpecificDateByCompanyDateWithName(selectedDate.format(self.dateFormat)).done(function(lstComSpecDate: any) {
                if (lstComSpecDate.length > 0) {
                    self.isNew(false);
                    for (let j = 1; j < endOfMonth + 1; j++) {
                        let processDay: string = processMonth + _.padStart(j, 2, '0');
                        processDay = moment(processDay).format(self.dateFormat);
                        arrName = [];
                        arrId = [];
                        //Loop in each Day
                        _.forEach(_.orderBy(lstComSpecDate,'specificDateItemNo','asc'), function(comItem) {
                            if (comItem.specificDate == processDay) {
                                arrName.push(comItem.specificDateItemName);
                                arrId.push(comItem.specificDateItemNo);
                            };
                        });
                        arrOptionaDates.push(new OptionalDate(moment(processDay).format("YYYY-MM-DD"), arrName, arrId));
                    };
                }
                //Return Array of Data in Month
                self.serverSource = _.cloneDeep(arrOptionaDates);
                dfd.resolve(arrOptionaDates);
            }).fail(function(res) {
                nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
                dfd.reject();
            });
            return dfd.promise(); 
        }

        /**
         * get Start Day of Company
         */
        getComStartDay(): JQueryPromise<number> {
            let dfd = $.Deferred<any>();
            //get Company Start Day
            service.getCompanyStartDay().done(function(startDayComapny: any) {
                if (!nts.uk.util.isNullOrUndefined(startDayComapny)) {
                    dfd.resolve(startDayComapny.startDay);
                };
            }).fail(function(res) {
                nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
                dfd.reject();
            });
            return dfd.promise();
        }

        /**
         * get full selectable item
         */
        getAllSpecDate() {
            var self = this;
            service.getAllSpecificDate().done(data => {
                data.forEach(item => {
                    self.fullBoxItemList.push(new BoxModel(item.specificDateItemNo, item.specificName));
                });
            }).fail(res => {
                nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
            });
        }

        /**
         * set SpecificItem To Select Date
         * get data from E Dialog
         */
        setSpecificItemToSelectedDate(param: IData) {
            var self = this;
            //get process date
            let selectedDate: string = moment(param.date).format("YYYY-MM-DD");            //find exist item 
            let selectedOptionalDate: OptionalDate = _.find(self.optionDates(), function(o) { return o.start == selectedDate; });

            if (nts.uk.util.isNullOrUndefined(selectedOptionalDate)) {
                self.optionDates.push(new OptionalDate(selectedDate, self.getNamefromSpecId(param.selecteds), param.selecteds));
            } else {
                self.optionDates.remove(selectedOptionalDate);
                selectedOptionalDate.start = selectedDate;
                selectedOptionalDate.listId = param.selecteds;
                selectedOptionalDate.listText = self.getNamefromSpecId(param.selecteds);
                self.optionDates.push(selectedOptionalDate);
            }
        }
        /**
         * Insert Calendar data
         */
        insertCompanySpecDate(lstComSpecificDateCommand: Array<ICompanySpecificDateCommand>) {
            var self = this;
            nts.uk.ui.block.invisible();
            service.insertComSpecificDate(lstComSpecificDateCommand).done(function(res: Array<any>) {
                nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                    self.start();
                    nts.uk.ui.block.clear();
                });
            }).fail(function(res) {
                nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
            });
        }

        /**
         * Delete Calendar data
         */
        DeleteOneMonth() {
            let dfd = $.Deferred<any>();
            var self = this;
            nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                nts.uk.ui.block.invisible();
                //delete
                service.deleteComSpecificDate({ startDate:moment(self.yearMonthPicked(), "YYYYMM").startOf('month').format(self.dateFormat), endDate:moment(self.yearMonthPicked(), "YYYYMM").endOf('month').format(self.dateFormat) }).done(function(res: any) {
                    nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
                        //Set dataSource to Null
                        self.optionDates([]);
                        self.isNew(true);
                    });
                    nts.uk.ui.block.clear();
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
                    dfd.reject();
                });
                return dfd.promise();
            }).ifNo(function() {
                // do nothing           
            });
        }

        /**
         * Register Process
         */
        Register() {
            var self = this;
            let dfd = $.Deferred<any>();
            //Check Is used item 
            if (self.hasItemSpecNotUse()) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_139" });
            } else {
                if (self.isNew()) {
                    // INSERT
                    self.insertCompanySpecDate(self.getInsertCommand())
                } else {
                    // UDPATE  
                    service.updateComSpecificDate(self.getUpdateCommand()).done(function(res: Array<any>) {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                            if(_.flattenDeep(_.map(self.optionDates(), o => o.listId)).length == 0){
                                self.isNew(true);    
                            };
                            self.start();
                            nts.uk.ui.block.clear();
                        });
                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError(res.message).then(function() { nts.uk.ui.block.clear(); });
                    });
                }
                //focus the first CheckBox
                $(".chkBox ").find("label").eq(0).focus();
                return dfd.promise();
            }
        }
        /**
         * get Update Command
         */
        getUpdateCommand() {
            var self = this;
            let arrCommand = [];
            self.optionDates().forEach(item => {
                let before = _.find(self.serverSource, o => o.start == item.start);
                if (nts.uk.util.isNullOrUndefined(before)) {
                    arrCommand.push({
                        specificDate: moment(item.start, 'YYYYMMDD').format(self.dateFormat),
                        specificDateItemNo: self.getSpecIdfromName(item.listText),
                        isUpdate: false
                    });
                } else {
                    let current = {
                        specificDate: moment(item.start, 'YYYYMMDD').format(self.dateFormat),
                        specificDateItemNo: self.getSpecIdfromName(item.listText)
                    };
                    if (!_.isEqual(ko.mapping.toJSON(before), ko.mapping.toJSON(current))) {
                        current["isUpdate"] = true;
                        arrCommand.push(current);
                    }
                }
            });
            return arrCommand;
        }
        /**
         * convert data to INSERT command (delete, insert)
         * where : self.optionDates().length > 0
         */
        getInsertCommand() {
            var self = this;
            let lstComSpecificDateCommand: Array<ICompanySpecificDateCommand> = [];
            _.forEach(self.optionDates(), function(processDay) {
                if (processDay.listId.length > 0) {
                    _.forEach(processDay.listId, function(id) {
                        lstComSpecificDateCommand.push({ specificDate: moment(processDay.start, 'YYYYMMDD').format(self.dateFormat), specificDateNo: Number(id) });
                    });
                };
            });
            return lstComSpecificDateCommand;
        };

        /**
         * check spec item is Use
         */
        hasItemSpecNotUse(): boolean {
            var self = this;
            let allCode = [];
            let isUseCode = _.map(self.boxItemList(), o => o.id);
            _.forEach(self.optionDates(), function(k) {
                allCode = _.concat(allCode, self.getSpecIdfromName(k.listText));
            });
            allCode = _.uniq(allCode);
            let arrDiff: Array<string> = _.difference(allCode, isUseCode);
            if (arrDiff.length > 0)
                return true;
            else
                return false;
        }

        /**
         * get List ID from List NAME
         */
        getSpecIdfromName(arrSpecDateName: string[]): number[] {
            var self = this;
            let arrSpecId = [];
            arrSpecDateName.forEach(item => {
                let specDateItem = _.find(self.fullBoxItemList(), o => { return o.name == item });
                arrSpecId.push(specDateItem.id);
            });
            return arrSpecId;
        }

        /**
         * get Name from ID
         */
        getNamefromSpecId(arrId: number[]): string[] {
            var self = this;
            let arrName: Array<string> = [];
            arrName = _.chain(self.boxItemList()).filter((item) => {
                return (arrId.indexOf(item.id) > -1);
            }).map('name').value();
            return arrName;
        }

        /**
         * Open Dialog Specific Date Setting 
         */
        openKsm002CDialog() {
            var self = this;
            nts.uk.ui.windows.sub.modal('/view/ksm/002/c/index.xhtml',{ title: "?????????????????????", dialogClass: "no-close" }).onClosed(function(): any {
                self.start().done(function(){
                    
                    $(".chkBox ").find("label").eq(0).focus();
                });
            })
        }

        /**
         * open dialog D event
         */
        openKsm002DDialog() {
            var self = this;
            nts.uk.ui.windows.setShared('KSM002_D_PARAM',
                {
                    util: 1,
                    startDate: Number(moment(self.yearMonthPicked().toString(), 'YYYYMM').startOf('month').format('YYYYMMDD')),
                    endDate: Number(moment(self.yearMonthPicked().toString(), 'YYYYMM').endOf('month').format('YYYYMMDD'))
                });
            nts.uk.ui.windows.sub.modal("/view/ksm/002/d/index.xhtml", { title: "?????????????????????", dialogClass: "no-close" }).onClosed(function() {
                self.start();
            });
        }

        /**
         * Process open E Dialog
         */
        openKsm002EDialog(selectedDate: string) {
            var self = this;
            //get data in process date
            let selectedOptionalDate: OptionalDate = _.find(self.optionDates(), function(o) { return o.start == selectedDate; });
            //get list id selected OptinonalDate
            let arrSelecteds: Array<string> = [];
            if (!nts.uk.util.isNullOrUndefined(selectedOptionalDate)){
                arrSelecteds = selectedOptionalDate.listId;
            }; 
            setShared('KSM002_E_PARAM', { date: moment(selectedDate).format('YYYY/MM/DD'), selectable: _.map(self.boxItemList(), 'id'), selecteds: arrSelecteds });
            nts.uk.ui.windows.sub.modal('/view/ksm/002/e/index.xhtml', { title: '????????????????????????????????????', }).onClosed(function() {
                let param: IData = getShared('KSM002E_VALUES');
                if (param !== undefined) {
                    self.setSpecificItemToSelectedDate(param);
                };
            });
        }
        
         /**
         * closeDialog
         */
        public opencdl028Dialog() {
            var self = this;
            let params = {
                date: moment(new Date()).toDate(),
                mode: 2 //YEAR_PERIOD_FINANCE
            };

            nts.uk.ui.windows.setShared("CDL028_INPUT", params);

            nts.uk.ui.windows.sub.modal("com", "/view/cdl/028/a/index.xhtml").onClosed(function() {
                var params = nts.uk.ui.windows.getShared("CDL028_A_PARAMS");
                if (params.status) {
                    self.exportExcel(params.mode, params.startDateFiscalYear, params.endDateFiscalYear);
                 }
            });

        }
        
        /**
         * Print file excel
         */
        exportExcel(mode: string, startDate: string, endDate: string) : void {
            var self = this;
            nts.uk.ui.block.grayout();
            service.saveAsExcel(mode, startDate, endDate).done(function() {
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
            }).always(function() {
                nts.uk.ui.block.clear();
            });
        }
    }

    interface IData {
        date: any,
        selectable: Array<any>,
        selecteds: Array<any>
    }

    class BoxModel {
        id: number;
        name: string;
        constructor(id: number, name: string) {
            var self = this;
            self.id = id;
            self.name = name;
        }
    }

    class SpecItem {
        specItemNo: number;
        specItemName: string;
        constructor(specItemNo: string, specItemName: string) {
            var self = this;
            self.specItemNo = specItemNo;
            self.specItemName = specItemName;
        }
    }

    class OptionalDate {
        start: string;
        textColor: string;
        backgroundColor: string;
        listText: Array<string>;
        listId: Array<string>;
        constructor(start: string, listText: Array<string>, listId: Array<string>) {
            var self = this;
            self.start = start;
            this.backgroundColor = 'white';
            this.textColor = '#31859C';
            self.listText = listText;
            self.listId = _.cloneDeep(listId);
        }
    }

    export module model {
        export class ItemModel2 {
            code: string;
            name: string;
            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
    }
    export class ICompanySpecificDateCommand {
        specificDate: string;
        specificDateNo: number;
        isUpdate?: boolean;
    }
}