module nts.uk.at.view.kdl003.a {
    export module viewmodel {
        export class ScreenModel extends ko.ViewModel {

            // Search options.
            startTimeOption: KnockoutObservable<number>;
            startTime: KnockoutObservable<number>;
            endTimeOption: KnockoutObservable<number>;
            endTime: KnockoutObservable<number>;

            readonly: KnockoutObservable<boolean>;
            enable1: KnockoutObservable<boolean>;
            enable2: KnockoutObservable<boolean>;

            currentCode: KnockoutObservable<any>;
            currentCode2: KnockoutObservable<any>;
            currentCode3: KnockoutObservable<any>;
            currentCode4: KnockoutObservable<any>;

            startBTime: KnockoutObservable<string>;
            endBTime: KnockoutObservable<string>;

            startBBTime: KnockoutObservable<string>;
            endBBTime: KnockoutObservable<string>;

            timeZone1: first;
            timeZone2: second;

            // Data list & selected code.
            listWorkTime: KnockoutObservableArray<WorkTimeSet>;
            selectedWorkTimeCode: KnockoutObservable<string>;
            listWorkType: KnockoutObservableArray<WorkType>;
            selectedWorkTypeCode: KnockoutObservable<string>;

            listTimeBreak1: KnockoutObservableArray<BreakTime>;
            listTimeBreakBreak1: KnockoutObservableArray<BreakTime>;
            listTimeBreak2: KnockoutObservableArray<BreakTime>;
            listTimeBreakBreak2: KnockoutObservableArray<BreakTime>;

            styleWorkTime: KnockoutObservable<string>;

            // Define columns.
            workTimeColumns: KnockoutObservableArray<any>;
            workTypeColumns: KnockoutObservableArray<any>;

            breakColumns1: KnockoutObservableArray<any>;
            breakBreakColumns1: KnockoutObservableArray<any>;
            breakColumns2: KnockoutObservableArray<any>;
            breakBreakColumns2: KnockoutObservableArray<any>;

            // Initial work time code list..
            initialWorkTimeCodes: Array<String>;

            // Parameter from caller screen.
            callerParameter: CallerParameter;
            searchCode: KnockoutObservable<string> = ko.observable(null);

            isAllCheckStatus: KnockoutObservable<boolean> = ko.observable(false);
            isCheckStatus: KnockoutObservable<boolean> = ko.observable(false);

            allWorkHours: KnockoutObservableArray<WorkTimeSet> = ko.observableArray([]);
            selectableWorkHours: KnockoutObservableArray<WorkTimeSet> = ko.observableArray([]);
            currentWorkHours: KnockoutObservableArray<WorkTimeSet> = ko.observableArray([]);
            isWorkTimeSettingNeeded: KnockoutObservable<number> = ko.observable(0);

			paramKsu003 : any;
			
            constructor(parentData: CallerParameter) {
                super();

                var self = this;

                self.readonly = ko.observable(true);
                self.enable1 = ko.observable(true);
                self.enable2 = ko.observable(true);

                // Search options
                self.startTimeOption = ko.observable(1);
                self.startTime = ko.observable(null);
                self.endTimeOption = ko.observable(1);
                self.endTime = ko.observable(null);

                self.startBTime = ko.observable(null);
                self.endBTime = ko.observable(null);
                self.startBBTime = ko.observable(null);
                self.endBBTime = ko.observable(null);

                self.timeZone1 = <first>{};
                self.timeZone2 = <second>{};

                self.styleWorkTime = ko.observable(null);

                self.listWorkTime = ko.observableArray([]);
                self.selectedWorkTimeCode = ko.observable('');
                self.listWorkType = ko.observableArray([]);
                self.selectedWorkTypeCode = ko.observable('');

                self.listTimeBreak1 = ko.observableArray([]);
                self.listTimeBreakBreak1 = ko.observableArray([]);
                self.listTimeBreak2 = ko.observableArray([]);
                self.listTimeBreakBreak2 = ko.observableArray([]);

                self.currentCode = ko.observable();
                self.currentCode2 = ko.observable();
                self.currentCode3 = ko.observable();
                self.currentCode4 = ko.observable();

                // Define gridlist's columns
                self.workTypeColumns = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KDL003_5'), prop: 'workTypeCode', width: 50 },
                    { headerText: nts.uk.resource.getText('KDL003_6'), prop: 'name', width: 100 },
                    { headerText: nts.uk.resource.getText('KDL003_7'), prop: 'memo', width: 130 }
                ]);

                self.workTimeColumns = ko.observableArray([]);
                /* self.workTimeColumns = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KDL003_5'), prop: 'code', width: 50 },
                    { headerText: nts.uk.resource.getText('KDL003_6'), prop: 'name', width: 100 },
                    { headerText: nts.uk.resource.getText('KDL003_19'), prop: 'workTime1', width: 200 },
                    { headerText: nts.uk.resource.getText('KDL003_20'), prop: 'workTime2', width: 200 },
                    { headerText: nts.uk.resource.getText('KDL003_21'), prop: 'workAtr', width: 130 },
                    { headerText: nts.uk.resource.getText('KDL003_7'), prop: 'remark', template: '<span>${remark}</span>' }
                ]);
                */
                self.breakColumns1 = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KDL003_33'), prop: 'code', width: 30 },
                    { headerText: nts.uk.resource.getText('KDL003_34'), prop: 'name', width: 170 }
                ]);
                self.breakBreakColumns1 = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KDL003_33'), prop: 'code', width: 30 },
                    { headerText: nts.uk.resource.getText('KDL003_34'), prop: 'name', width: 170 }
                ]);
                self.breakColumns2 = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KDL003_33'), prop: 'code', width: 30 },
                    { headerText: nts.uk.resource.getText('KDL003_34'), prop: 'name', width: 170 }
                ]);
                self.breakBreakColumns2 = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KDL003_33'), prop: 'code', width: 30 },
                    { headerText: nts.uk.resource.getText('KDL003_34'), prop: 'name', width: 170 }
                ]);

                // On selectedWorkTimeCode changed event.
                self.selectedWorkTimeCode.subscribe(code => {
                    if (code) {
                        self.getListTimeBySeleckedCode(code);
                        self.getTimeBySelectedCode(self.listWorkTime(), code);
                    }
                });

                self.isCheckStatus.subscribe((isChecked) => {
                    let listTimeWork = [];

                    self.searchCode(null);
                    self.startTime(null);
                    self.endTime(null);

                    listTimeWork = (isChecked) ? _.cloneDeep(self.allWorkHours()) : _.cloneDeep(self.selectableWorkHours());
                    listTimeWork = self.addShowNone(listTimeWork);
                    self.listWorkTime(listTimeWork);
                    self.currentWorkHours(listTimeWork);
                });
                //parent data
                self.callerParameter = parentData;
				
				// param 表示可能勤務種類リスト 
				self.paramKsu003 = nts.uk.ui.windows.getShared("paramKsu003Kdl003");
            }

            /**
             * Start page.
             */
            private startPage(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<void>();
                nts.uk.ui.block.invisible();

                $.when(self.loadWorkTime(self.callerParameter.selectedWorkTimeCode), self.loadWorkType())
                    .done(() => {
                        // Set initial selection.
                        //self.initWorkTypeSelection();
                        //self.setWorkTimeSelection();
						
						if (!_.isNil(self.paramKsu003)){
							if (self.paramKsu003.disWkTypeCon == 1){
								_.remove(self.listWorkType(), (wt : any) => {
									let check = _.find(self.paramKsu003.disAbleWkTypeCodeLst, (fd : any) => {
										return _.isEqual(wt.workTypeCode, fd);
									})
									if (_.isNil(check))
								  	return true;
							
								return false;
								});
							} 
							/*else {
								self.listWorkType().length = 0;
							}*/
							
						}
						
                        // On selectedWorkTypeCode changed event.
                        self.selectedWorkTypeCode.subscribe(code => {
                            if (nts.uk.util.isNullOrEmpty(self.listWorkTime())) {
                                return;
                            }
                            if (!code) {
                                return;
                            }
                            //check focus
                            service.isWorkTimeSettingNeeded(code).done(val => {

                                self.isWorkTimeSettingNeeded(val);

                                switch (val) {
                                    case SetupType.NOT_REQUIRED:
                                        if (!self.callerParameter.showNone && !nts.uk.util.isNullOrEmpty(self.listWorkTime())) {
                                            self.selectedWorkTimeCode(self.listWorkTime()[0].code);
                                        } else {
                                            self.selectedWorkTimeCode('');
                                        }
                                        break;
                                    default: // Do nothing.
                                }
                            });
                        });

                        // Set initial selection.
                        self.initWorkTypeSelection();
                        self.setWorkTimeSelection();

                        // Set initial work time list.
                        self.initialWorkTimeCodes = _.map(self.listWorkTime(), function (item) { return item.code })
                        dfd.resolve();
                    })
                    .fail(function (res) {
                        nts.uk.ui.dialog.alertError(res);
                    }).always(() => {
                    nts.uk.ui.block.clear();
                });

                return dfd.promise();
            }

            /**
             * Load work time.
             */
            private loadWorkTime(code: string): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                let params: any = {
                    baseDate: self.callerParameter.baseDate,
                    codes: self.callerParameter.workTimeCodes,
                    workPlaceId: self.callerParameter.workPlaceId
                }

                service.findAllWorkTimeNew(params)
                    .done((data: any) => {

                        self.getHeaderWorkTime(data.useATR === 1);
                        let isCheckStatus = _.isNil(data.allCheckStatus) ? true : data.allCheckStatus;
                        self.isAllCheckStatus(!isCheckStatus);
                        self.isCheckStatus(isCheckStatus);

                        let listWorkTime: Array<WorkTimeSet> = [];
                        if (!nts.uk.util.isNullOrEmpty(self.callerParameter.workTimeCodes)) {
                            listWorkTime = data.availableWorkingHours;
                        } else if (!nts.uk.util.isNullOrEmpty(self.callerParameter.workPlaceId)
                            && !nts.uk.util.isNullOrEmpty(self.callerParameter.baseDate)) {
                            listWorkTime = data.workingHoursByWorkplace;
                        } else {
                            listWorkTime = data.allWorkHours;
                        }

                        //backup data
                        self.allWorkHours(_.orderBy(data.allWorkHours, 'code', 'asc'));

                        let selectableWorkHours = !nts.uk.util.isNullOrEmpty(self.callerParameter.workTimeCodes)
                            ? data.availableWorkingHours : data.workingHoursByWorkplace;
                        self.selectableWorkHours(_.orderBy(selectableWorkHours, 'code', 'asc'));

                        listWorkTime = _.orderBy(listWorkTime, 'code', 'asc');
                        listWorkTime = self.addShowNone(listWorkTime);
                        self.currentWorkHours(listWorkTime);
                        self.listWorkTime(listWorkTime);
                        dfd.resolve();
                    })
                    .fail((error) => {
                        console.log(error);
                        dfd.reject();
                    });

                // Find work time by list code if caller's parameters exist.
                /* if (!nts.uk.util.isNullOrEmpty(self.callerParameter.workTimeCodes)) {
                    service.findWorkTimeByCodes(self.callerParameter.workTimeCodes)
                        .done(function (data) {
                            data = _.sortBy(data, item => item.code);
                            self.getTimeBySelectedCode(data, code);
                            if (nts.uk.util.isNullOrUndefined(self.callerParameter.showNone) || self.callerParameter.showNone) {
                                self.addFirstItem(data);
                            }
                            self.listWorkTime(data);
                            dfd.resolve();
                        });
                } else {
                    // Find all work time
                    service.findAllWorkTime()
                        .done(function (data) {
                            data = _.sortBy(data, item => item.code);
                            self.getTimeBySelectedCode(data, code);
                            if (nts.uk.util.isNullOrUndefined(self.callerParameter.showNone) || self.callerParameter.showNone) {
                                self.addFirstItem(data);
                            }
                            self.listWorkTime(data);
                            dfd.resolve();
                        });
                } */

                return dfd.promise();
            }

            /**
             * Load work type.
             */
            private loadWorkType(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                // Find work type by list code if caller's parameters exist.
                if (!nts.uk.util.isNullOrEmpty(self.callerParameter.workTypeCodes)) {
                    service.findWorkTypeByCodes(self.callerParameter.workTypeCodes)
                        .done(function (workTypeList: Array<WorkType>) {
                            let workTypes = self.sortbyList(_.uniqBy(workTypeList, 'workTypeCode'));
                            self.listWorkType(workTypes);
                            dfd.resolve();
                        });
                } else {
                    // Find all work type.
                    service.findAllWorkType()
                        .done(function (workTypeList: Array<WorkType>) {
                            let workTypes = self.sortbyList(_.uniqBy(workTypeList, 'workTypeCode'));
                            self.listWorkType(workTypes);
                            dfd.resolve();
                        });
                }
                return dfd.promise();
            }

            /**
             * sort list by:
             * 1. dispOrder
             * 2. Code
             */
            sortbyList(lstItem: Array<any>): Array<any> {
                let lwt: Array<any> = [];
                if (lstItem && !!lstItem.length) {
                    lwt = _.orderBy(lstItem, ['dispOrder', 'workTypeCode'], ['asc', 'asc']);
                }
                return lwt;
            }

            /**
             * Get Time By Selected Code
             */
            private getTimeBySelectedCode(data: Array<WorkTimeSet>, code: string): void {
                let self = this;

                //Set Time Zone1 and TimeZone2
                $.each(data, function (key, value) {
                    if (data[key].code == code) {
                        //Setting Time Zone1
                        if (!nts.uk.util.isNullOrEmpty(data[key].workTime1)) {
                            let codeTime = data[key].workTime1;
                            let array = codeTime.split(' ~ ');
                            self.startBTime(array[0].substring(2));
                            self.endBTime(array[1].substring(2));
                            self.enable1(true);
                            self.timeZone1.start = data[key].firstStartTime;
                            self.timeZone1.end = data[key].firstEndTime;

                        } else {
                            self.startBTime(":");
                            self.endBTime(":");
                            self.enable1(false);
                            self.timeZone1.start = null;
                            self.timeZone1.end = null;
                        };

                        //Setting Time Zone2
                        if (!nts.uk.util.isNullOrEmpty(data[key].workTime2)) {
                            let codeTime = data[key].workTime2;
                            let array = codeTime.split(' ~ ');
                            self.startBBTime(array[0].substring(2));
                            self.endBBTime(array[1].substring(2));
                            self.enable2(true);
                            self.timeZone2.start = data[key].secondStartTime;
                            self.timeZone2.end = data[key].secondEndTime;
                        } else {
                            self.startBBTime(":");
                            self.endBBTime(":");
                            self.enable2(false);
                            self.timeZone2.start = null;
                            self.timeZone2.end = null;
                        };

                        self.styleWorkTime(data[key].workAtr);
                    }
                });
            }

            /**
             * Get List Time By Selected Code
             */
            private getListTimeBySeleckedCode(code: string): void {
                let self = this;
                let emptyArray = { code: "", name: "" };
                let dataBB = [];
                let dataB = [];
                let dataEmpty = [];

                //Set Data Empty
                if (nts.uk.util.isNullOrEmpty(code)) {
                    for (let i = 0; i < 5; i++) {
                        dataEmpty.push(emptyArray);
                    }

                    self.listTimeBreak1(dataEmpty);
                    self.listTimeBreak2(dataEmpty);
                    self.listTimeBreakBreak1(dataEmpty);
                    self.listTimeBreakBreak2(dataEmpty);
                    self.startBBTime(":");
                    self.endBBTime(":");
                    self.enable2(false);
                    self.startBTime(":");
                    self.endBTime(":");
                    self.enable1(false);
                } else {
                    //Find Break By Codes
                    service.findBreakByCodes(code).done(function (data) {
                        dataBB = data.breakBreakTimeDto;
                        dataB = data.breakTimeDto;
                        if (nts.uk.util.isNullOrEmpty(dataB) && nts.uk.util.isNullOrEmpty(dataBB)) {
                            //                            if (dataB.length <= 5 && dataBB.length <= 5){
                            //                                for (let i = 0; i < 5; i++){
                            //                                    dataEmpty.push(emptyArray);
                            //                                }
                            //                                for (let i = dataB.length; i < 5; i++){
                            //                                    dataB.push(emptyArray);
                            //                                }
                            //                                for (let i = dataBB.length; i < 5; i++){
                            //                                    dataBB.push(emptyArray);
                            //                                }
                            //
                            //                                //Set list Break Time and list Break Break Time
                            //                                self.listTimeBreak1(dataB);
                            //                                self.listTimeBreak2(dataEmpty);
                            //                                self.listTimeBreakBreak1(dataBB);
                            //                                self.listTimeBreakBreak2(dataEmpty);
                            //                            } else {
                            //                                if(dataB.length > 5){
                            //                                    for (let i = 0; i < 5 ; i++){
                            //                                        self.listTimeBreak1(dataB[i]);
                            //                                    }
                            //                                    for (let i = 5; i < dataB.length ; i++){
                            //                                        self.listTimeBreak2(dataB[i]);
                            //                                    }
                            //                                } else {
                            //                                    for (let i = 0; i < 5 ; i++){
                            //                                        self.listTimeBreakBreak1(dataBB[i]);
                            //                                    }
                            //                                    for (let i = 5; i < dataB.length ; i++){
                            //                                        self.listTimeBreakBreak2(dataBB[i]);
                            //                                    }
                            //                                }
                            //                            }
                        }
                    });
                }
            }

            /**
             * Add first work time item.
             */
            private addFirstItem(data: Array<WorkTimeSet>): void {
                let self = this;
                //add item　なし
                data.unshift({
                    code: "",
                    name: "なし",
                    workTime1: "",
                    workTime2: "",
                    workAtr: "",
                    remark: "",
                    firstStartTime: null,
                    firstEndTime: null,
                    secondStartTime: null,
                    secondEndTime: null
                });
                self.listWorkTime(data);
            }

            /**
             * Initial work type selection
             */
            private initWorkTypeSelection(): void {
                let self = this;

                // Return if list work type is null or empty
                if (nts.uk.util.isNullOrEmpty(self.listWorkType())) {
                    return;
                };

                // Selected code from caller screen.
                let selectedWorkTypeCode = self.callerParameter.selectedWorkTypeCode;
                let isInSelectableCodes = selectedWorkTypeCode ? _.find(self.listWorkType(), item => selectedWorkTypeCode == item.workTypeCode) : false;
                if (selectedWorkTypeCode && isInSelectableCodes) {
                    self.selectedWorkTypeCode(selectedWorkTypeCode);
                } else {
                    // Select first item.
                    self.selectedWorkTypeCode(_.first(self.listWorkType()).workTypeCode);
                }
            }

            /**
             * Set work time selection.
             */
            private setWorkTimeSelection(): void {
                let self = this;

                // Return if list work time is null or empty
                if (nts.uk.util.isNullOrEmpty(self.listWorkTime())) {
                    return;
                };

                // Selected code from caller screen.
                let selectedWorkTimeCode = self.callerParameter.selectedWorkTimeCode;
                let isInSelectableCodes = selectedWorkTimeCode ? _.find(self.listWorkTime(), item => selectedWorkTimeCode == item.code) : false;
                if (selectedWorkTimeCode && isInSelectableCodes) {
                    self.selectedWorkTimeCode(selectedWorkTimeCode);
                } else {
                    // Select first item.
                    self.selectedWorkTimeCode(_.first(self.listWorkTime()).code);
                }
            }

            /**
             * Search work time.
             */
            public search(): void {
                var self = this;
                if (nts.uk.util.isNullOrEmpty(self.startTime()) && nts.uk.util.isNullOrEmpty(self.endTime())) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_307" });
                    return;
                }

                if ($('#inputEndTime').ntsError('hasError') || $('#inputStartTime').ntsError('hasError')) {
                    return;
                }

                nts.uk.ui.block.invisible();

                // Search command.
                let command = {
                    codelist: self.initialWorkTimeCodes,
                    startAtr: self.startTimeOption(),
                    startTime: nts.uk.util.isNullOrEmpty(self.startTime()) ? null : self.startTime(),
                    endAtr: self.endTimeOption(),
                    endTime: nts.uk.util.isNullOrEmpty(self.endTime()) ? null : self.endTime()
                }

                // Search & display data.
                service.findByTime(command)
                    .done(function (data) {
                        data = _.sortBy(data, item => item.code);
                        self.listWorkTime([]);
                        self.listWorkTime(data);
                        if (!nts.uk.util.isNullOrEmpty(data)) {
                            self.selectedWorkTimeCode(data[0].code);
                        }
                    })
                    .fail(function (res) {
                        nts.uk.ui.dialog.alertError(res);
                    })
                    .always(() => {
                        // Set focus.
                        $("[tabindex='10']").focus();

                        nts.uk.ui.block.clear();
                    });
            }

            //ver 13
            public searchByTime(): void {
                var self = this;
                if (nts.uk.util.isNullOrEmpty(self.startTime()) && nts.uk.util.isNullOrEmpty(self.endTime())) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_307" });
                    return;
                }

                if ($('#inputEndTime').ntsError('hasError') || $('#inputStartTime').ntsError('hasError')) {
                    return;
                }

                self.listWorkTime(_.cloneDeep(self.currentWorkHours()));
                let afterListWorkTime: Array<WorkTimeSet> = [];

                if (!nts.uk.util.isNullOrEmpty(self.startTime()) && nts.uk.util.isNullOrEmpty(self.endTime())) {
                    afterListWorkTime = _.filter(self.listWorkTime(), item => { return item.firstStartTime >= self.startTime() });
                } else if (nts.uk.util.isNullOrEmpty(self.startTime()) && !nts.uk.util.isNullOrEmpty(self.endTime())) {
                    afterListWorkTime = _.filter(self.listWorkTime(), item => { return item.firstEndTime <= self.endTime() });
                } else {
                    afterListWorkTime = _.filter(self.listWorkTime(), item => {
                        return item.firstStartTime >= self.startTime() && item.firstEndTime <= self.endTime();
                    });
                }

                self.listWorkTime.removeAll();
                afterListWorkTime = self.addShowNone(afterListWorkTime);
                self.listWorkTime(afterListWorkTime);
                $("#day-list-tbl").igGrid("container").focus();
            }
            /**
             * Clear search condition.
             */
            public clearSearchCondition(): void {
                nts.uk.ui.block.invisible();
                var self = this;

                // Reset search conditions
                self.startTime(null);
                self.endTime(null);
                self.searchCode(null);
                self.startTimeOption(1);
                self.endTimeOption(1);
                // Clear errors.
                nts.uk.ui.errors.clearAll();
                $('#inputEndTime').ntsError('clear');
                $('#inputStartTime').ntsError('clear');

                // Reload list work time.
                self.loadWorkTime(self.callerParameter.selectedWorkTimeCode).always(() => {
                    nts.uk.ui.block.clear();
                });

                // Set focus.
                //$("#day-list-tbl").igGrid("container").focus();
                //$("[tabindex='10']").focus();
            }

            /**
             * Submit.
             */
            public submit() {
                let self = this;
                let dfd = $.Deferred<void>();

                let workTypeCode = self.selectedWorkTypeCode();
                let workTimeCode = self.selectedWorkTimeCode();
                let time1 = self.timeZone1;
                let time2 = self.timeZone2;

                if (!workTypeCode) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_10" });
                    return;
                }

                // Loading, block ui.
                nts.uk.ui.block.invisible();

                // Set work time = null if list work time is empty.
                if (!workTimeCode || nts.uk.util.isNullOrEmpty(self.listWorkTime())) {
                    workTimeCode = '';
                    time1.start = time1.end = time2.start = time2.end = null;
                }

                // Check pair work type & work time.
                if (!nts.uk.util.isNullOrEmpty(workTimeCode)) {
                    service.checkPairWorkTypeWorkTime(workTypeCode, workTimeCode).done(() => {
                        let findWorkTime = _.find(self.listWorkTime(), (x) => x.code === workTimeCode);
                        // Set shared data.
                        let workTypeName = self.getWorkTypeName(workTypeCode);
                        let workTimeName = self.getWorkTimeName(workTimeCode);
                        let returnedData = {
                            selectedWorkTypeCode: workTypeCode,
                            selectedWorkTypeName: workTypeName,
                            selectedWorkTimeCode: workTimeCode,
                            selectedWorkTimeName: workTimeName,
                            first: time1,
                            second: time2,
                            remark: findWorkTime.remark,
                            workTimeSetting: self.isWorkTimeSettingNeeded()
                        };
                        nts.uk.ui.windows.setShared("childData", returnedData, false);

                        // Close dialog.
                        self.closeDialog();

                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                } else {
                    service.checkPairWorkTypeWorkTime2(workTypeCode).done(() => {

                        workTimeCode = '';
                        time1.start = time1.end = time2.start = time2.end = null;

                        // Set shared data.
                        let workTypeName = self.getWorkTypeName(workTypeCode);
                        let workTimeName = '';
                        let returnedData = {
                            selectedWorkTypeCode: workTypeCode,
                            selectedWorkTypeName: workTypeName,
                            selectedWorkTimeCode: workTimeCode,
                            selectedWorkTimeName: workTimeName,
                            first: time1,
                            second: time2,
                            workTimeSetting: self.isWorkTimeSettingNeeded()
                        };
                        nts.uk.ui.windows.setShared("childData", returnedData, false);

                        // Close dialog.
                        self.closeDialog();

                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                }
                return dfd.promise();
            }

            /**
             * Get work type name.
             */
            private getWorkTypeName(workTypeCode: string): string {
                let self = this;
                let name: string = '';
                if (self.listWorkTime()) {
                    let workType = _.find(self.listWorkType(), workType => workType.workTypeCode == workTypeCode);
                    name = workType ? workType.name : '';
                }
                return name;
            }

            /**
             * Get work time name.
             */
            private getWorkTimeName(workTimeCode: string): string {
                let self = this;
                let name = '';
                if (!nts.uk.util.isNullOrEmpty(self.listWorkTime())) {
                    let workTime = _.find(self.listWorkTime(), workTime => workTime.code == workTimeCode);
                    name = workTime ? workTime.name : '';
                }
                return name;
            }

            /**
             * Close dialog.
             */
            public closeDialog(): void {
                nts.uk.ui.windows.close();
            }

            searchByCodeName() {
                let self = this;
                if (nts.uk.util.isNullOrEmpty(self.searchCode())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_2073' }).then(() => {
                        $('#A4_20').focus();
                        nts.uk.ui.block.clear();
                    });
                    return;
                }

                self.listWorkTime(_.cloneDeep(self.currentWorkHours()));
                let afterListWorkTime: Array<WorkTimeSet> = [];

                afterListWorkTime = _.filter(self.listWorkTime(), item => {
                    return item.code.indexOf(self.searchCode()) > -1 || item.name.indexOf(self.searchCode()) > -1;
                });

                self.listWorkTime.removeAll();
                afterListWorkTime = self.addShowNone(afterListWorkTime);
                self.listWorkTime(afterListWorkTime);
                $("#day-list-tbl").igGrid("container").focus();
            }

            getHeaderWorkTime(useWorkTime2: boolean) {
                const self = this;
                if (useWorkTime2)
                    self.workTimeColumns([
                        { headerText: nts.uk.resource.getText('KDL003_5'), prop: 'code', width: 50 },
                        { headerText: nts.uk.resource.getText('KDL003_6'), prop: 'name', width: 100 },
                        { headerText: nts.uk.resource.getText('KDL003_19'), prop: 'workTime1', width: 200 },
                        { headerText: nts.uk.resource.getText('KDL003_20'), prop: 'workTime2', width: 200 },
                        { headerText: nts.uk.resource.getText('KDL003_21'), prop: 'workAtr', width: 130 },
                        { headerText: nts.uk.resource.getText('KDL003_7'), prop: 'remark', template: '<span>${remark}</span>' }
                    ]);
                else
                    self.workTimeColumns([
                        { headerText: nts.uk.resource.getText('KDL003_5'), prop: 'code', width: 50 },
                        { headerText: nts.uk.resource.getText('KDL003_6'), prop: 'name', width: 100 },
                        { headerText: nts.uk.resource.getText('KDL003_19'), prop: 'workTime1', width: 200 },
                        { headerText: nts.uk.resource.getText('KDL003_21'), prop: 'workAtr', width: 130 },
                        { headerText: nts.uk.resource.getText('KDL003_7'), prop: 'remark', template: '<span>${remark}</span>' }
                    ]);
            }

            addShowNone(data: Array<any>): Array<any> {
                const self = this;

                if (nts.uk.util.isNullOrUndefined(self.callerParameter.showNone) || self.callerParameter.showNone) {
                    let findItem = _.some(data, (x) => x.code === '');
                    if( findItem ) return data;

                    data.unshift({
                        code: "",
                        name: "なし",
                        workTime1: "",
                        workTime2: "",
                        workAtr: "",
                        remark: "",
                        firstStartTime: null,
                        firstEndTime: null,
                        secondStartTime: null,
                        secondEndTime: null
                    });
                }

                return data;
            }
        }

        interface BreakTime {
            code: string;
            name: string;
        }

        interface WorkTimeSet {
            code: string;
            name: string;
            workTime1: string;
            workTime2: string;
            workAtr: string;
            remark: string;
            firstStartTime: number;
            firstEndTime: number;
            secondStartTime: number;
            secondEndTime: number;
        }
        export class WorkType {
            abbreviationName: string;
            companyId: string;
            displayAtr: number;
            memo: string;
            name: string;
            sortOrder: number;
            symbolicName: string;
            workTypeCode: string;
        }
        interface CallerParameter {
            workTypeCodes: Array<string>;
            selectedWorkTypeCode: string;
            workTimeCodes: Array<string>;
            selectedWorkTimeCode: string;
            showNone: boolean;
            workPlaceId: string;
            baseDate: string;
        }
        interface ReturnedData {
            selectedWorkTypeCode: string;
            selectedWorkTypeName: string;
            selectedWorkTimeCode: string;
            selectedWorkTimeName: string;
            first: first;
            second: second;
            remark: string;
        }
        interface first {
            start: number;
            end: number;
        }
        interface second {
            start: number;
            end: number;
        }

        enum SetupType {
            REQUIRED = 0,
            OPTIONAL = 1,
            NOT_REQUIRED = 2
        }
    }
}