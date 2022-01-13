module nts.uk.at.view.kmw003.a.viewmodel {
    import getText = nts.uk.resource.getText;
    import queryString = nts.uk.request.location.current.queryString;
    import setShared = nts.uk.ui.windows.setShared;
    import modal = nts.uk.ui.windows.sub.modal;
    export interface EmployeeSearchDto {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        workplaceCode: string;
        workplaceId: string;
        workplaceName: string;
    }
    export class ScreenModel {
        /**
         * Grid setting
         */
        //ColumnFixing
        fixHeaders: KnockoutObservableArray<any> = ko.observableArray([]);
        showHeaderNumber: KnockoutObservable<boolean> = ko.observable(false);
        headersGrid: KnockoutObservableArray<any>;
        columnSettings: KnockoutObservableArray<any> = ko.observableArray([]);
        sheetsGrid: KnockoutObservableArray<any> = ko.observableArray([]);
        fixColGrid: KnockoutObservableArray<any>;
        cellStates: KnockoutObservableArray<any> = ko.observableArray([]);
        rowStates: KnockoutObservableArray<any> = ko.observableArray([]);
        dpData: Array<any> = [];
        headerColors: KnockoutObservableArray<any> = ko.observableArray([]);
        textColors: KnockoutObservableArray<any> = ko.observableArray([]);
        legendOptions: any;
        
        //grid user setting
        cursorMoveDirections: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: "縦" },
            { code: 1, name: "横" }
        ]);
        selectedDirection: KnockoutObservable<any> = ko.observable(0);

        //a4_7
        closureInfoItems: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedClosure: KnockoutObservable<any> = ko.observable(0);

        displayWhenZero: KnockoutObservable<boolean> = ko.observable(false);

        showProfileIcon: KnockoutObservable<boolean> = ko.observable(false);
        //ccg001 component: search employee
        ccg001: any;
        lstEmployee: KnockoutObservableArray<any> = ko.observableArray([]);
        displayFormat: KnockoutObservable<number> = ko.observable(null);
        lstDate: KnockoutObservableArray<any> = ko.observableArray([]);
        optionalHeader: Array<any> = [];
        employeeModeHeader: Array<any> = [];
        dateModeHeader: Array<any> = [];
        errorModeHeader: Array<any> = [];
        formatCodes: KnockoutObservableArray<any> = ko.observableArray([]);
        lstAttendanceItem: KnockoutObservableArray<any> = ko.observableArray([]);
        //A13_1 コメント
        comment: KnockoutObservable<string> = ko.observable('');
        closureName: KnockoutObservable<string> = ko.observable('');
        showButton: KnockoutObservable<AuthorityDetailModel> = ko.observable(null);
        employmentCode: KnockoutObservable<any> = ko.observable("");
        editValue: KnockoutObservableArray<InfoCellEdit> = ko.observableArray([]);
        itemValueAll: KnockoutObservableArray<any> = ko.observableArray([]);
        itemValueAllTemp: KnockoutObservableArray<any> = ko.observableArray([]);
        lockMessage: KnockoutObservable<any> = ko.observable("");
        dataHoliday: KnockoutObservable<DataHoliday> = ko.observable(new DataHoliday("0", "0", "0", "0", "0", "0"));
        comboColumns: KnockoutObservableArray<any> = ko.observableArray([{ prop: 'code', length: 1 },
            { prop: 'name', length: 2 }]);
        comboColumnsCalc: KnockoutObservableArray<any> = ko.observableArray([{ prop: 'code', length: 1 },
            { prop: 'name', length: 8 }]);

        dataAll: KnockoutObservable<any> = ko.observable(null);
        dataBackup: any;
        hasLstHeader: boolean = true;
        dPErrorDto: KnockoutObservable<any> = ko.observable();
        listCareError: KnockoutObservableArray<any> = ko.observableArray([]);
        listCareInputError: KnockoutObservableArray<any> = ko.observableArray([]);
        employIdLogin: any;
        dialogShow: any;
        //contain data share
        screenModeApproval: KnockoutObservable<any> = ko.observable(null);
        changePeriod: KnockoutObservable<any> = ko.observable(true);
        errorReference: KnockoutObservable<any> = ko.observable(true);
        activationSourceRefer: KnockoutObservable<any> = ko.observable(null);
        tighten: KnockoutObservable<any> = ko.observable(null);

        selectedDate: KnockoutObservable<string> = ko.observable(null);
        //起動モード　＝　修正モード
        initMode: KnockoutObservable<number> = ko.observable(0);
        selectedErrorCodes: KnockoutObservableArray<any> = ko.observableArray([]);

        //Parameter setting
        monthlyParam: KnockoutObservable<any> = ko.observable(null);
//        reloadParam: KnockoutObservable<any> = ko.observable(null);
        //Date YYYYMM picker
        yearMonth: KnockoutObservable<number>;
        //Combobox display actual time
        actualTimeOptionDisp: KnockoutObservableArray<any>;
        actualTimeSelectedCode: KnockoutObservable<number> = ko.observable(0);
        actualTimeDats: KnockoutObservableArray<any> = ko.observableArray([]);;
        actualTimeSelectedDat: KnockoutObservable<any> = ko.observable(null);

        closureId: KnockoutObservable<number> = ko.observable(0);

        closureDateDto: KnockoutObservable<any> = ko.observable(null);

        dailyPerfomanceData: KnockoutObservableArray<any> = ko.observableArray([]);

        isStartScreen: KnockoutObservable<any> = ko.observable(true);
        noCheckColumn: KnockoutObservable<any> = ko.observable(false);

        isCache : boolean = false;
        isStarted: boolean = false;
        
        clickCounter: CLickCount = new CLickCount();
        workTypeNotFound: any = [];
        flagSelectEmployee: boolean = false;
		// 就業確定を利用する ← 就業確定の機能制限.就業確定を行う
		employmentConfirm: boolean = false;
        
        constructor(value:boolean) {
            let self = this;
            self.isCache = value;
            self.yearMonth = ko.observable(Number(moment(new Date()).format("YYYYMM")));
            self.initLegendButton();
            //self.initActualTime();
            self.headersGrid = ko.observableArray([]);
            self.fixColGrid = ko.observableArray([]);

            if (!_.isEmpty(queryString.items)) {
                self.initMode(Number(queryString.items["initmode"]));
            }

            self.monthlyParam({
                yearMonth: 0,
                actualTime: null,
                initMenuMode: self.initMode(),
                //抽出対象社員一覧
                lstEmployees: [],
                formatCodes: self.formatCodes(),
                dispItems: [],
                correctionOfMonthly: null,
                errorCodes: [],
                lstLockStatus: [],
                closureId: null
            });

//            self.reloadParam({
//                processDate: self.yearMonth(),
//                initScreenMode: self.initMode(),
//                //抽出対象社員一覧
//                lstEmployees: [],
//                closureId: self.closureId(),
//                lstAtdItemUnique: []
//            });

            self.actualTimeSelectedCode.subscribe(value => {
                self.actualTimeSelectedDat(self.actualTimeDats()[value]);
                //                if(self.monthlyParam().actualTime ==null){
                //                    let temp = {startDate : moment(self.actualTimeSelectedDat().startDate).toISOString(), endDate : moment(self.actualTimeSelectedDat().endDate).toISOString()  };
                //                    self.monthlyParam().actualTime = temp;    
                //                }else{
                //                    self.monthlyParam().actualTime.startDate = moment(self.actualTimeSelectedDat().startDate).toISOString();
                //                    self.monthlyParam().actualTime.(self.actualTimeSelectedDat().endDate).toISOString();    
                //                }

                //                self.initScreen();
                //実績期間を変更する
                self.updateActualTime();
            });
            
            self.selectedDirection.subscribe((value) => {
                if (value == 0) {
                    $("#dpGrid").mGrid("directEnter", "below");
                } else {
                    $("#dpGrid").mGrid("directEnter", "right");
                }
            });
            
            self.yearMonth.subscribe(value => {
                //期間を変更する
                if (nts.uk.ui._viewModel && nts.uk.ui.errors.getErrorByElement($("#yearMonthPicker")).length == 0 && value != undefined && !self.isStartScreen())
                    self.updateDate(value);
            });
            $(document).mouseup(function(e) {
                let container = $(".ui-tooltip");
                if (!container.is(e.target) &&
                    container.has(e.target).length === 0) {
                    $("#tooltip").hide();
                }
            });

            self.showProfileIcon.subscribe((val) => {
                if ($("#dpGrid").data('mGrid')) {
                    $("#dpGrid").mGrid("destroy");
                    $("#dpGrid").off();
                }
                self.reloadGrid();
            });

            self.showHeaderNumber.subscribe((val) => {
                _.each(self.optionalHeader, header => {
                    if (header.headerText != "提出済みの申請" && header.headerText != "申請") {
                        if (header.group == undefined && header.group == null) {
                            if (self.showHeaderNumber()) {
                                let headerText = header.headerText + " " + header.key.substring(1, header.key.length);
                                $("#dpGrid").mGrid("headerText", header.key, headerText, false);
                            } else {
                                //let headerText = header.headerText.split(" ")[0];
                                let headerText = header.headerText;
                                $("#dpGrid").mGrid("headerText", header.key, headerText, false);
                            }
                        } else {
                            if (self.showHeaderNumber()) {
                                let headerText = header.headerText + " " + header.group[1].key.substring(4, header.group[1].key.length);
                                $("#dpGrid").mGrid("headerText", header.headerText, headerText, true);
                            } else {
                                //let headerText = header.headerText.split(" ")[0];
                                let headerText = header.headerText;
                                $("#dpGrid").mGrid("headerText", header.headerText, headerText, true);
                            }
                        }
                    }
                });
            });

            self.displayWhenZero.subscribe(val => {
                // $("#dpGrid").igGrid("option", "dataSource", self.displayNumberZero(self.formatDate(self.dpData)));
                self.displayNumberZero1();
            });

            $(window).on('resize', function() {
            	self.setScreenSize();
            });
        }

        displayNumberZero1() {
            let self = this;
            if (!self.displayWhenZero()) {
                $("#dpGrid").mGrid("hideZero", true)
            } else {
                $("#dpGrid").mGrid("hideZero", false)
            }
        }

//        displayNumberZero(dataSource: Array<any>): Array<any> {
//            let self = this;
//            let dataTemp = [];
//            if (!self.displayWhenZero()) {
//                _.each(dataSource, data => {
//                    var dtt: any = {};
//                    _.each(data, (val, indx) => {
//                        if (String(val) == "0" || String(val) == "0:00") {
//                            dtt[indx] = "";
//                        } else {
//                            dtt[indx] = val;
//                        }
//                    });
//                    dataTemp.push(dtt);
//                });
//            } else {
//                let dataSourceOld: any = self.formatDate(self.dailyPerfomanceData());
//                let dataChange: any = $("#dpGrid").mGrid("updatedCells");
//                let group: any = _.groupBy(dataChange, "rowId");
//                _.each(dataSourceOld, data => {
//                    var dtt: any = {};
//                    if (group[data.id]) {
//                        dtt = data;
//                        _.each(group[data.id], val => {
//                            dtt[val.columnKey] = val.value;
//                        });
//                        dataTemp.push(dtt);
//                    } else {
//                        dataTemp.push(data);
//                    }
//                });
//            }
//            return dataTemp;
//        }

        createSumColumn(data: any) {
            var self = this;
            _.each(data.lstControlDisplayItem.columnSettings, function(item) {

                if (item.typeFormat != null && item.typeFormat != undefined) {
                    if (item.typeFormat == 2) {
                        //so lan
                        item['summaryCalculator'] = "Number";
                    }
                    else if (item.typeFormat == 3) {
                        // so ngay 
                        item['summaryCalculator'] = "Number";
                    }
                    else if (item.typeFormat == 1) {
                        //thoi gian
                        item['summaryCalculator'] = "Time";
                    }
                    else if (item.typeFormat == 4) {
                        //so tien 
                        item['summaryCalculator'] = "Number";
                    }else{
                        if (self.displayFormat() == 0) {
                            if (item.columnKey == "date") {
                                item['summaryCalculator'] = "合計";
                            } else {
                                item['summaryCalculator'] = "";
                            }
                        } else {
                            if (item.columnKey == "employeeCode") {
                                item['summaryCalculator'] = "合計";
                            } else {
                                item['summaryCalculator'] = "";
                            }
                        }
                    }
                } else {
                    item['summaryCalculator'] = "Number";
                }

                delete item.typeFormat;
                self.columnSettings(data.lstControlDisplayItem.columnSettings);
            });
        }

        startPage(param?: any): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();

            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            self.initScreen(param).done((processDate, selectedClosure) => {
                //date process
				if(!_.isNil(processDate))
                self.yearMonth(processDate);

                if (selectedClosure) {
                    self.selectedClosure(selectedClosure);
                }

                self.selectedClosure.subscribe((value) => {
                    if (!value) return;
                    self.closureId(value);
                    self.updateDate(self.yearMonth());
                });
                if (self.initMode() == 0) {
                self.initCcg001();  
                self.loadCcg001();
                    }
                if (self.initMode() == 2) 
                    $('#ccg001').hide();
                nts.uk.ui.block.clear();
                dfd.resolve();
            }).fail(function(error) {
                //                nts.uk.ui.dialog.alert(error.message);
                if(!nts.uk.util.isNullOrUndefined(error)){
                    nts.uk.ui.dialog.alert({ messageId: error.messageId }).then(function() {
                        nts.uk.request.jumpToTopPage();
                    });
                }
                nts.uk.ui.block.clear();
                dfd.reject();
            });
            return dfd.promise();
        }
        
        setScreenSize() {
        	if (window.innerHeight < 447) {
        		return;
        	}
        	/*$('.mgrid-fixed').height(window.innerHeight - 364);
        	$('.mgrid-free').height(window.innerHeight - 364);
        	$('.grid-container').height(window.innerHeight - 240);
        	$('.mgrid-fixed-summaries').css({ top: window.innerHeight - 367 + 'px' });
        	$('.mgrid-free-summaries').css({ top: window.innerHeight - 367 + 'px' });
        	$('.mgrid-paging').css({ top: window.innerHeight - 321 + 'px' });
        	$('.mgrid-sheet').css({ top: window.innerHeight - 275 + 'px' });
            $('.mgrid-free').css({ top: '41px' });
            $('.mgrid-header').css({ top: '3px' });
        	$('.mgrid-free').width(window.innerWidth - 627);
        	$('.mgrid-free.mgrid-header').width(window.innerWidth - 644);
        	$('.mgrid-free-summaries').width(window.innerWidth - 644);
        	$('.mgrid-paging').width($('.mgrid-fixed').width() + $('.mgrid-free.mgrid-header').width() + 19);
        	$('.mgrid-sheet').width($('.mgrid-fixed').width() + $('.mgrid-free.mgrid-header').width() + 19);*/
        }
        /**********************************
        * Initialize Screen 
        **********************************/
        initScreen(param?: any): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            localStorage.removeItem(window.location.href + '/dpGrid');
            nts.uk.ui.errors.clearAllGridErrors();

            if(!_.isNil(param)){
                self.monthlyParam().closureId = param.closureId;
                self.monthlyParam().yearMonth = param.yearMonth;
            }   
                     
            self.monthlyParam().lstLockStatus = [];
            self.noCheckColumn(false);
            let checkLoadKdw: boolean = localStorage.getItem('isKmw');
            nts.uk.characteristics.restore("cacheKMW003").done(function (cacheData) { 
            if(cacheData != undefined && self.isCache == true && cacheData.yearMonth !=0 && checkLoadKdw){
                self.monthlyParam(cacheData);
            }
            localStorage.removeItem('isKmw');
            __viewContext.transferred.value = false;
            nts.uk.characteristics.save("cacheKMW003",self.monthlyParam());

			if (self.monthlyParam().actualTime) {
                self.monthlyParam().actualTime.startDate = moment.utc(self.monthlyParam().actualTime.startDate, "YYYY/MM/DD").toISOString();
                self.monthlyParam().actualTime.endDate = moment.utc(self.monthlyParam().actualTime.endDate, "YYYY/MM/DD").toISOString();
            }
            service.startScreen(self.monthlyParam()).done((data) => {
				self.employmentConfirm = data.useSetingOutput.employmentConfirm;
                if(self.employmentConfirm){
                    let check = _.find(data.authorityDto, function(o) {
                        return o.functionNo === 26;
                    })
                    if (check == null){
                        self.employmentConfirm = false;
                    } 
                }
                if (data.selectedClosure) {
                    let closureInfoArray = []
                    closureInfoArray = _.map(data.lstclosureInfoOuput, function(item: any) {
                        return { code: item.closureName, name: item.closureId };
                    });
                    self.closureInfoItems(closureInfoArray);
                }
                self.employIdLogin = __viewContext.user.employeeId;
                self.dataAll(data);
                self.monthlyParam(data.param);
                self.dataBackup = _.cloneDeep(data);
                
                self.itemValueAll(data.itemValues);
                self.receiveData(data);
                self.createSumColumn(data);
                self.columnSettings(data.lstControlDisplayItem.columnSettings);
                /*********************************
                 * Screen data
                 *********************************/
                // attendance item
                self.lstAttendanceItem(data.param.lstAtdItemUnique);
                // closure ID
                self.closureId(data.closureId);
//                self.reloadParam().closureId = data.closureId;
//                self.reloadParam().lstAtdItemUnique = data.param.lstAtdItemUnique;
                //Closure name
                self.closureName(data.closureName);
                // closureDateDto
                self.closureDateDto(data.closureDate);
                //actual times
                self.actualTimeDats(data.lstActualTimes);
                self.actualTimeSelectedDat(data.selectedActualTime);
                self.initActualTime();
                //comment
                self.comment(data.comment ? '■ ' + data.comment : null);
                /*********************************
                 * Grid data
                 *********************************/
                // Fixed Header
                self.setFixedHeader(data.lstFixedHeader);
                self.extractionData();
                self.loadGrid();
                _.forEach(data.mpsateCellHideControl, (cellHide =>{
                    $('#dpGrid').mGrid("setState", cellHide.rowId, cellHide.columnKey, ["mgrid-hide"])
                }))
                
                self.employmentCode(data.employmentCode);
                self.dailyPerfomanceData(self.dpData);
                self.lstEmployee(_.orderBy(data.lstEmployee, ['code'], ['asc']));

                //画面項目の非活制御をする
                self.showButton(new AuthorityDetailModel(data.authorityDto, data.actualTimeState, self.initMode(), data.formatPerformance.settingUnitType));
                if(self.initMode() == 2 && self.showButton().available_A4_7() == false) {
                    self.showButton().available_A4_7(true);
                    self.showButton().available_A1_11(false);
                    //A4_2
                    self.showButton().available_A4_2(false);
                }
                self.showButton().enable_multiActualTime(data.lstActualTimes.length > 1);
//                if (data.showRegisterButton == false) {
//                    self.showButton().enable_A1_1(data.showRegisterButton);
//                    self.showButton().enable_A1_2(data.showRegisterButton);
//                    self.showButton.valueHasMutated();
//                }
                nts.uk.ui.block.clear();
                dfd.resolve(data.processDate, data.selectedClosure);
            }).fail(function(error) {
                if(error.messageId=="Msg_1430"){
                    nts.uk.ui.dialog.error({ messageId: error.messageId, messageParams: error.parameterIds }).then(function() { 
                        nts.uk.request.jumpToTopPage();
                    });  
                 } else if(error.messageId=="Msg_916"){
                    nts.uk.ui.dialog.error({ messageId: error.messageId, messageParams: error.parameterIds }).then(function() { 
                        //nts.uk.request.jumpToTopPage();
                        nts.uk.ui.block.clear();
                    }); 
                }else if(error.messageId=="Msg_1452"){
                    nts.uk.ui.dialog.error({ messageId: error.messageId, messageParams: error.parameterIds }).then(function() { 
                        //nts.uk.request.jumpToTopPage();
                        nts.uk.ui.block.clear();
                    }); 
                }else {
                    if (error.messageId == "KMW003_SELECT_FORMATCODE") {
                        //Open KDM003C to select format code
                        self.displayItem().done((x) => {
                            dfd.resolve();
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alert({ messageId: error.messageId }).then(function() {
                                nts.uk.request.jumpToTopPage();
                            });
                            dfd.reject();
                        });
                    } else if (!_.isEmpty(error.errors)) {
                        nts.uk.ui.dialog.bundledErrors({ errors: error.errors }).then(function() {
                            if(error.errors[0].messageId !="Msg_1403"){
                                nts.uk.request.jumpToTopPage();
                            }
                            dfd.reject();
                        });
                    } else {
                        nts.uk.ui.dialog.alert({ messageId: error.messageId }).then(function() {
                            nts.uk.request.jumpToTopPage();
                        });
                        dfd.reject();
                    }
                }
            });
            });
            return dfd.promise();
        }

        initScreenFormat(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            localStorage.removeItem(window.location.href + '/dpGrid');
            nts.uk.ui.errors.clearAllGridErrors();
			self.monthlyParam().initMenuMode = self.initMode();
			self.monthlyParam().formatCodes = self.formatCodes();
            self.monthlyParam().lstLockStatus = [];
            if (self.monthlyParam().actualTime) {
                self.monthlyParam().actualTime.startDate = moment.utc(self.monthlyParam().actualTime.startDate, "YYYY/MM/DD").toISOString();
                self.monthlyParam().actualTime.endDate = moment.utc(self.monthlyParam().actualTime.endDate, "YYYY/MM/DD").toISOString();
            }
            service.startScreen(self.monthlyParam()).done((data) => {
                if (data.selectedClosure) {
                    let closureInfoArray = []
                    closureInfoArray = _.map(data.lstclosureInfoOuput, function(item: any) {
                        return { code: item.closureName, name: item.closureId };
                    });
                    self.closureInfoItems(closureInfoArray);
                    self.selectedClosure(data.selectedClosure);
                }
                self.employIdLogin = __viewContext.user.employeeId;
                self.dataAll(data);
                self.monthlyParam(data.param);
				nts.uk.characteristics.save("cacheKMW003",self.monthlyParam());
                self.dataBackup = _.cloneDeep(data);
                self.itemValueAll(data.itemValues);
                self.receiveData(data);
                self.createSumColumn(data);
                self.columnSettings(data.lstControlDisplayItem.columnSettings);
                /*********************************
                 * Screen data
                 *********************************/
                // attendance item
                self.lstAttendanceItem(data.param.lstAtdItemUnique);
                // closure ID
                self.closureId(data.closureId);
//                self.reloadParam().closureId = data.closureId;
//                self.reloadParam().lstAtdItemUnique = data.param.lstAtdItemUnique;
                //Closure name
                self.closureName(data.closureName);
                // closureDateDto
                self.closureDateDto(data.closureDate);
                //actual times
                self.actualTimeDats(data.lstActualTimes);
                self.actualTimeSelectedDat(data.selectedActualTime);
                self.initActualTime();
                //comment
                self.comment(data.comment != null ? '■ ' + data.comment : null);
                /*********************************
                 * Grid data
                 *********************************/
                // Fixed Header
                self.setFixedHeader(data.lstFixedHeader);
                self.extractionData();
				 /*if ($("#dpGrid").data('mGrid')) {
	                $("#dpGrid").mGrid("destroy");
	                $("#dpGrid").off();
	            }*/
				if ($("#dpGrid").hasClass("mgrid")) {
	                $("#dpGrid").mGrid("destroy");
	                $("#dpGrid").removeClass("mgrid");
	                $("#dpGrid").off(); 
	            }
                self.loadGrid();
                _.forEach(data.mpsateCellHideControl, (cellHide =>{
                    $('#dpGrid').mGrid("setState", cellHide.rowId, cellHide.columnKey, ["mgrid-hide"])
                }))
                self.employmentCode(data.employmentCode);
                self.dailyPerfomanceData(self.dpData);
                self.lstEmployee(_.orderBy(data.lstEmployee, ['code'], ['asc']));

                //画面項目の非活制御をする
                self.showButton(new AuthorityDetailModel(data.authorityDto, data.actualTimeState, self.initMode(), data.formatPerformance.settingUnitType));
                self.showButton().enable_multiActualTime(data.lstActualTimes.length > 1);
//                if (data.showRegisterButton == false) {
//                    self.showButton().enable_A1_1(data.showRegisterButton);
//                    self.showButton().enable_A1_2(data.showRegisterButton);
//                    self.showButton.valueHasMutated();
//                }
                nts.uk.ui.block.clear();
                dfd.resolve(data.processDate);
            }).fail(function(error) {
                nts.uk.ui.dialog.alert({ messageId: error.messageId }).then(function() {
                    nts.uk.request.jumpToTopPage();
                });
                dfd.reject();
            });
            return dfd.promise();
        }

        loadRowScreen() {
            let self = this, param = _.cloneDeep(self.monthlyParam()),
                dataChange: any =  _.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual),
                empIds = _.map(_.uniqBy(dataChange, (e: any) => { return e.rowId; }), (value: any) => {
                    return value.rowId;
                }),
                employees = _.filter(self.lstEmployee(), (e: any) => {
                    return _.includes(empIds, e.id);
                });
            param.lstEmployees = employees;
            param.lstLockStatus = [];
            param.actualTime.startDate = moment.utc(param.actualTime.startDate, "YYYY/MM/DD").toISOString();
            param.actualTime.endDate = moment.utc(param.actualTime.endDate, "YYYY/MM/DD").toISOString();
            let dfd = $.Deferred();
            service.updateScreen(param).done((data) => {
                let lstRowId = [] ;
                let dpDataNew = _.map(self.dpData, (value: any) => {
                    let val = _.find(data.lstData, (item: any) => {
                        return item.id == value.id;
                    });
                    return val != undefined ? val : value;
                });
                
                self.dpData = dpDataNew;
                let lstCellStateMerge = [] ;
                _.forEach(dpDataNew, (item: any) => {
                       lstRowId.push(item.rowId);
                    });
                for(let i = 0;i<data.lstCellState.length;i++){
                    if(data.lstCellState[i].columnKey != "approval"){
                        lstCellStateMerge.push(data.lstCellState[i]);
                        continue; 
                    }
                    if(data.lstCellState[i].columnKey == "approval" && _.indexOf(lstRowId, data.lstCellState[i].rowId) != -1 ){
                        for(let j =i+1;j<data.lstCellState.length;j++){
                            if(data.lstCellState[j].columnKey == "approval" && data.lstCellState[i].rowId == data.lstCellState[j].rowId){ 
                                data.lstCellState[i].state.push(data.lstCellState[j].state[0]);
                            }                            
                        }
                        _.remove(lstRowId, function(n) {
                            return n.rowId == data.lstCellState[i].rowId;
                        });
                        lstCellStateMerge.push(data.lstCellState[i]);
                    }
                }
                let cellStatesNew = _.map(self.cellStates(), (value: any) => {
                    let val = _.find(lstCellStateMerge, (item: any) => {
                        return item.rowId == value.rowId && item.columnKey == value.columnKey;
                    }); 
                    return val != undefined ? val : value;
                });
                _.each(data.lstCellState, (cs: any) => {
                    let val = _.find(cellStatesNew, (item: any) => {
                        return item.rowId == cs.rowId && item.columnKey == cs.columnKey;
                    });
                    if (val == undefined) {
                        cellStatesNew.push(cs);
                    }
                });
                self.cellStates(cellStatesNew);
                self.dailyPerfomanceData(dpDataNew);
                let rowIdUpdate =  _.uniq(_.map(_.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual), (itemTemp) => {return itemTemp.rowId}));
                $("#dpGrid").mGrid("destroy");
                $("#dpGrid").off();
                self.loadGrid();
                _.forEach(data.mpsateCellHideControl, (cellHide =>{
                    $('#dpGrid').mGrid("setState", cellHide.rowId, cellHide.columnKey, ["mgrid-hide"])
                }))
                
                _.forEach(
                    _.filter(self.cellStates(), (itemTemp) => { 
                        return itemTemp.columnKey == "approval" && rowIdUpdate.indexOf(itemTemp.rowId) == -1 && itemTemp.state.indexOf("mgrid-error") != -1
                    }),
                    (state) => {
                        $('#dpGrid').mGrid("setState", state.rowId, state.columnKey, ["mgrid-hide"]);
                    });
                dfd.resolve();
            });
            return dfd.promise();
        }

        initLegendButton() {
            let self = this;
            self.legendOptions = {
                items: [
                    { colorCode: '#94B7FE', labelText: '手修正（本人）' },
                    { colorCode: '#CEE6FF', labelText: '手修正（他人）' },
					{ colorCode: '#F69164', labelText: getText("KMW003_42") },
					{ colorCode: '#FFFF99', labelText: getText("KMW003_43") },
					{ colorCode: '#FF99CC', labelText: getText("KMW003_44") },
					{ colorCode: '#ff0000', labelText: getText("KMW003_45") },
                    { colorCode: '#DDDDD2', labelText: getText("KMW003_33") },
                ]
            };
        }

        /**********************************
         * Button Event 
         **********************************/
        /**
         * A1_1 確定ボタン
         */
        insertUpdate() {
            let self = this,
                errorGrid: any = $("#dpGrid").mGrid("errors"),
                dataChange: any = _.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual);

            if ((errorGrid == undefined || errorGrid.length == 0) && _.size(dataChange) > 0 && self.workTypeNotFound.length == 0 ) {
                let dataSource = $("#dpGrid").mGrid("dataSource"),
                    dataChangeProcess: any = [],
                    dataUpdate: any = {
                        /** 年月: 年月 */
                        yearMonth: self.yearMonth(),
                        /** 締めID: 締めID */
                        closureId: self.closureId(),
                        /** 締め日: 日付 */
                        closureDate: self.closureDateDto(),
                        mPItemDetails: [],
                        startDate: moment.utc(self.actualTimeSelectedDat().startDate, "YYYY/MM/DD"),
                        endDate: moment.utc(self.actualTimeSelectedDat().endDate, "YYYY/MM/DD"),
                        dataCheckSign: [],
                        dataCheckApproval: [],
                        dataLock: []
                    };

                _.each(dataChange, (data: any) => {
                    let dataTemp = _.find(dataSource, (item: any) => {
                        return item.id == data.rowId;
                    });
                    let current = _.find(self.dpData, (dt) => { return dt.id ===data.rowId; });
                    dataUpdate.dataLock.push({ rowId: data.rowId, version: current.version, employeeId: dataTemp.employeeId});
                    if (data.columnKey != "identify" && data.columnKey != "approval") {
                        if (data.columnKey.indexOf("Code") == -1) {
                            if (data.columnKey.indexOf("Name") != -1) {
                            } else {
                                //get layout , and type
                                let layoutAndType: any = _.find(self.itemValueAll(), (item: any) => {
                                    return item.itemId == data.columnKey.substring(1, data.columnKey.length);
                                });
                                let item = self.lstAttendanceItem()[Number(data.columnKey.substring(1, data.columnKey.length))];
                                let value: any;
                                value = self.getPrimitiveValue(data.value, item.attendanceAtr);
                                let dataMap = new InfoCellEdit(
                                                    data.rowId, 
                                                    data.columnKey.substring(1, data.columnKey.length), 
                                                    value, 
                                                    layoutAndType == undefined ? "" : layoutAndType.valueType, 
                                                    layoutAndType == undefined ? "" : layoutAndType.layoutCode, 
                                                    dataTemp.employeeId, 
                                                    0);
                                dataChangeProcess.push(dataMap);
                            } 
                        } else {
                            let columnKey: any;
                            let item: any;
                            columnKey = data.columnKey.substring(4, data.columnKey.length);
                            item = _.find(self.lstAttendanceItem(), (data) => {
                                return String(data.id) === columnKey;
                            })

                            let layoutAndType: any = _.find(self.itemValueAll(), (item: any) => {
                                return item.itemId == columnKey;
                            });
                            let dataMap = new InfoCellEdit(
                                                data.rowId, 
                                                columnKey, 
                                                String(data.value), 
                                                layoutAndType == undefined ? "" : layoutAndType.valueType,
                                                layoutAndType == undefined ? "" : layoutAndType.layoutCode,
                                                dataTemp.employeeId);
                            dataChangeProcess.push(dataMap);
                        }

                    } else {
                        if (data.columnKey == "identify") {
                            dataUpdate.dataCheckSign.push({ rowId: data.rowId, itemId: "identify", value: data.value, employeeId: dataTemp.employeeId });
                        } else {
                            dataUpdate.dataCheckApproval.push({ rowId: data.rowId, itemId: "approval", value: data.value, employeeId: dataTemp.employeeId });
                        }
                    }
                });
                dataUpdate.mPItemDetails = dataChangeProcess;
                nts.uk.ui.block.invisible();
                nts.uk.ui.block.grayout();
                service.addAndUpdate(dataUpdate).done((data) => {
                    nts.uk.ui.dialog.info({ messageId: 'Msg_15' });
					
					self.loadRowScreen().done(() => {
						self.updateCellIsCal(data);
						nts.uk.ui.block.clear();
					});
                }).fail(function(res: any) {
                    if(res.optimisticLock === true){
                        nts.uk.ui.dialog.error({ messageId: 'Msg_1528' }).then(() => {
                            self.loadRowScreen().done(() => {
                                nts.uk.ui.block.clear();
                            });
                        });
                    } else {
                        nts.uk.ui.dialog.error({ messageId: res.messageId, messageParams: res.parameterIds }).then(function() { nts.uk.ui.block.clear(); });    
                    }
                });
            }
        }

		updateCellIsCal(data:any){
			_.forEach(data, row => {
				_.forEach(row.itemChangeByCal, item => {
					$("#dpGrid").mGrid("setState", row.employeeId,"A" + item.itemId, ["mgrid-calc"]);
				});
			});
		}

        insertUpdate2() {
            let self = this;
            self.insertUpdate();
        }

        btnSaveColumnWidth_Click() {
            let self = this,
                command = {
                    lstHeader: {},
                    formatCode: self.dataAll().param.formatCodes
                },
                jsonColumnWith = localStorage.getItem(window.location.href + '/dpGrid');
            let valueTemp = 0;
            _.forEach($.parseJSON(jsonColumnWith), (valueP, keyP) =>{
                if (keyP != "reparer") {
                    _.forEach(valueP, (value, key) => {
                        if (key.indexOf('A') != -1) {
                            if (nts.uk.ntsNumber.isNumber(key.substring(1, key.length))) {
                                command.lstHeader[key.substring(1, key.length)] = value;
                            }
                        }
                        if (key.indexOf('Code') != -1 || key.indexOf('NO') != -1) {
                            valueTemp = value;
                        } else if (key.indexOf('Name') != -1) {
                            command.lstHeader[key.substring(4, key.length)] = value + valueTemp;
                            valueTemp = 0;
                        }
                    });
                }
            })
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            service.saveColumnWidth(command).done(() =>{
                nts.uk.ui.block.clear(); 
            }).fail(() => {
                nts.uk.ui.block.clear(); 
            });
        }

        getPrimitiveValue(value: any, atr: any): string {
            var self = this;
            let valueResult: string = "";
            if (atr != undefined && atr != null) {

                if (atr == 1) {
                    valueResult = value == "" ? null : String(self.getHoursTime(value));
                } else {
                    valueResult = value;
                }
            } else {
                valueResult = value;
            }
            return valueResult;
        };

        getHours(value: any): number {
            return Number(value.split(':')[0]) * 60 + Number(value.split(':')[1]);
        }

        //time
        getHoursTime(value): number {
            var self = this;
            if (value.indexOf(":") != -1) {
                if (value.indexOf("-") != -1) {
                    let valueTemp = value.split('-')[1];
                    return 0 - self.getHours(valueTemp);
                } else {
                    return self.getHours(value);
                }
            } else {
                return value;
            }
        };

        //time day
        getHoursAll(value: any): number {
            var self = this;
            if (value.indexOf(":") != -1) {
                if (value.indexOf("-") != -1) {
                    let valueTemp = value.split('-')[1];
                    return self.getHours(valueTemp) - 24 * 60;
                } else {
                    return self.getHours(value);
                }
            } else {
                return value;
            }
        };

        /**
         * 期間を変更する
         */
        updateDate(date: any) {
            let self = this;
            //let dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            //            self.monthlyParam().initMenuMode = self.initMode();
            self.monthlyParam().closureId = self.closureId(); 
            self.monthlyParam().yearMonth = date; 
            self.monthlyParam().lstEmployees = self.lstEmployee();

            if ($("#dpGrid").data('mGrid')) {
                $("#dpGrid").mGrid("destroy");
                $("#dpGrid").off();
            }
            //$("#dpGrid").off();

            $.when(self.initScreen()).done((processDate) => {
                nts.uk.ui.block.clear();
            });

        }
        /**
         * 実績期間: List<DatePeriod> actualTimeDats
         * 実績期間選択: DatePeriod actualTimeSelectedDat
         */
        initActualTime() {
            let self = this,
                selectedIndex = 0;
            if (self.actualTimeDats && self.actualTimeDats().length > 0) {
                self.actualTimeOptionDisp = ko.observableArray();
                for (let i = 0; i < self.actualTimeDats().length; i++) {
                    self.actualTimeOptionDisp.push({ code: i, name: self.actualTimeDats()[i].startDate + "～" + self.actualTimeDats()[i].endDate });
                    if (self.actualTimeDats()[i].startDate === self.actualTimeSelectedDat().startDate
                        && self.actualTimeDats()[i].endDate === self.actualTimeSelectedDat().endDate)
                        selectedIndex = i;
                }
            }
            self.actualTimeSelectedCode(0);
        };
        /*********************************/
        receiveData(data) {
            let self = this;
            self.dpData = data.lstData;
            self.cellStates(data.lstCellState);
            self.optionalHeader = data.lstControlDisplayItem.lstHeader;
            self.sheetsGrid(data.lstControlDisplayItem.lstSheet);
            self.sheetsGrid.valueHasMutated();
        };

        extractionData() {
            let self = this;
            self.headersGrid([]);
            self.fixColGrid = self.fixHeaders;

            self.loadHeader(self.displayFormat());
        };
        //init ccg001
        initCcg001() {
            let self = this;
            if ($('.ccg-sample-has-error').ntsError('hasError')) {
                return;
            }
            if (false && false && false) {
                nts.uk.ui.dialog.alertError("Base Date or Closure or Period must be shown!");
                return;
            }

            self.ccg001 = {
                // fix bug 100434
                maxPeriodRange: 'oneMonth',
                /** Common properties */
                systemType: 2,
                showEmployeeSelection: false, // 検索タイプ
                showQuickSearchTab: true, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: false, // 基準日利用
                showClosure: true, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: true, // 対象期間利用
                periodFormatYM: true, // 対象期間精度

                /** Required parameter */
                periodStartDate: self.yearMonth, // 対象期間開始日
                periodEndDate: self.yearMonth, // 対象期間終了日
                inService: true, // 在職区分
                leaveOfAbsence: true, // 休職区分
                closed: true, // 休業区分
                retirement: false, // 退職区分

                /** Quick search tab options */
                showAllReferableEmployee: true, // 参照可能な社員すべて
                showOnlyMe: true, // 自分だけ
                showSameWorkplace: true, // 同じ職場の社員
                showSameWorkplaceAndChild: true, // 同じ職場とその配下の社員

                /** Advanced search properties */
                showEmployment: true, // 雇用条件
                showWorkplace: true, // 職場条件
                showClassification: true, // 分類条件
                showJobTitle: true, // 職位条件
                showWorktype: true, // 勤種条件
                isMutipleCheck: true,// 選択モード

                /** Return data */
                returnDataFromCcg001: function(dataList: any) {
                    self.flagSelectEmployee = true;
                    self.lstEmployee(dataList.listEmployee.map((data: EmployeeSearchDto) => {
                        return {
                            id: data.employeeId,
                            code: data.employeeCode,
                            businessName: data.employeeName,
                            workplaceName: data.workplaceName,
                            workplaceId: data.workplaceId,
                            depName: '',
                            isLoginUser: false
                        };
                    }));
                    self.closureId(dataList.closureId);
                    self.lstEmployee(_.orderBy(self.lstEmployee(), ['code'], ['asc']));
                    //Reload screen                    
//                    self.reloadParam().lstEmployees = self.lstEmployee();
                    let yearMonthNew: any = +moment.utc(dataList.periodEnd, 'YYYYMMDD').format('YYYYMM'),
                        yearMonthOld = self.yearMonth();
                    self.yearMonth(yearMonthNew);
                    if (yearMonthNew == yearMonthOld) {
                        self.yearMonth.valueHasMutated();
                    }
                },
            }
        };

        //load ccg001 component: search employee
        loadCcg001() {
            let self = this;
            $('#ccg001').ntsGroupComponent(self.ccg001);
        };

        loadGrid() {
            let self = this;
            self.setHeaderColor();
            let dataSource = self.formatDate(self.dpData);
			let subWidth = "50px";
            if (self.displayFormat() === 0) {
                subWidth = "135px";
            } else if (self.displayFormat() === 1) {
                subWidth = "135px";
            } else {
                subWidth = "155px";
            }
			let comment = (window.screen.availHeight - 240 - 88) + "px";
			$('#comment-text').css("margin-top",comment);
            new nts.uk.ui.mgrid.MGrid($("#dpGrid")[0], {
				subWidth: subWidth,
                subHeight: '285px',
                height: (window.screen.availHeight - 240) + "px",
                width: (window.screen.availWidth - 200) + "px",
                headerHeight: '40px',
                dataSource: dataSource,
                dataSourceAdapter: function(ds) {
                    return ds;
                },
                primaryKey: 'id',
                rowVirtualization: true,
                virtualization: true,
                virtualizationMode: 'continuous',
                enter: self.selectedDirection() == 0 ? 'below' : 'right',
                autoFitWindow: false,
                preventEditInError: false,
                hidePrimaryKey: true,
                userId: self.employIdLogin,
                getUserId: function(k) { return String(k); },
                errorColumns: ["ruleCode"],
                errorsOnPage: true,
                columns: self.headersGrid(),
                features: self.getGridFeatures(),
                ntsFeatures: self.getNtsFeatures(),
                ntsControls: self.getNtsControls(self.initMode(), self.closureId())
            }).create();
            self.showHeaderNumber.valueHasMutated();
            self.displayNumberZero1();
            self.setScreenSize();
        };
        /**********************************
        * Grid Data Setting 
        **********************************/
        setFixedHeader(fixHeader: Array<any>) {
            let self = this;
            let fixedData = _.map(fixHeader, function(item: any) {
                return { columnKey: item.key, isFixed: true };
            });
            self.fixHeaders(fixedData);
        };
        checkIsColumn(dataCell: any, key: any): boolean {
            let check = false;
            _.each(dataCell, (item: any) => {
                if (item.columnKey.indexOf("NO" + key) != -1) {
                    check = true;
                    return;
                }
            });
            return check;
        };

        isDisableRow(id) {
            let self = this;
            for (let i = 0; i < self.rowStates().length; i++) {
                return self.rowStates()[i].rowId == id;
            }
        };

        isDisableSign(id) {
            let self = this;
            for (let i = 0; i < self.cellStates().length; i++) {
                return self.cellStates()[i].rowId == id && self.cellStates()[i].columnKey == 'sign';
            }
        };

        formatDate(lstData) {
            let self = this;
            let start = performance.now();
            let data = lstData.map((data) => {
                let object = {
                    id: data.id,
                    state: data.state,
                    error: data.error,
                    employeeId: data.employeeId,
                    employeeCode: data.employeeCode,
                    employeeName: data.employeeName,
                    workplaceId: data.workplaceId,
                    employmentCode: data.employmentCode,
                    identify: data.identify,
                    approval: data.approval,
                    dailyconfirm: data.dailyConfirm,
                    dailyCorrectPerformance: data.dailyCorrectPerformance,
                    typeGroup: data.typeGroup,
                    startDate: self.actualTimeSelectedDat().startDate,
                    endDate: self.actualTimeSelectedDat().endDate
                }
                _.each(data.cellDatas, function(item) { object[item.columnKey] = item.value });
                return object;
            });
            return data;
        }
        /**
         * Grid Setting features
         */
        getGridFeatures(): Array<any> {
            let self = this;
            let features;
            if(self.sheetsGrid().length > 0){
             features = [
                {
                    name: 'Resizing',
                    columnSettings: [{
                        columnKey: 'id', allowResizing: false, minimumWidth: 0
                    }]
                },
                { name: 'MultiColumnHeaders' },
                {
                    name: 'Paging',
                    pageSize: 100,
                    currentPageIndex: 0
                },
                {
                    name: 'ColumnFixing', fixingDirection: 'left',
                    showFixButtons: false,
                    columnSettings: self.fixHeaders()
                },
                {
                    name: 'Summaries',
                    showSummariesButton: false,
                    showDropDownButton: false,
                    columnSettings: self.columnSettings(),
                    resultTemplate: '{1}'
                }, {
                    name: 'CellStyles',
                    states: self.cellStates()
                }, {
                    name: 'HeaderStyles',
                    columns: self.headerColors()
                },
                {
                    name: "Sheet",
                    initialDisplay: self.sheetsGrid()[0].name,
                    sheets: self.sheetsGrid()
                }
            ];} else {
                let messId = self.dataAll().mess; 
                nts.uk.ui.dialog.info({ messageId: messId });
                self.noCheckColumn(true);
                if(self.initMode() != 2) {
                    $("#cbClosureInfo").hide();
                }
                features = [
                {
                    name: 'Resizing',
                    columnSettings: [{
                        columnKey: 'id', allowResizing: false, minimumWidth: 0
                    }]
                },
                { name: 'MultiColumnHeaders' },
                {
                    name: 'Paging',
                    pageSize: 100,
                    currentPageIndex: 0
                },
//                {
//                    name: 'ColumnFixing', fixingDirection: 'left',
//                    showFixButtons: false,
//                    columnSettings: self.fixHeaders()
//                },
                {
                    name: 'Summaries',
                    showSummariesButton: false,
                    showDropDownButton: false,
                    columnSettings: self.columnSettings(),
                    resultTemplate: '{1}'
                }, {
                    name: 'CellStyles',
                    states: self.cellStates()
                }, {
                    name: 'HeaderStyles',
                    columns: self.headerColors()
                }
            ];    
            }
            return features;
        }
        
        totalDay(data) {
            if (!$("#dpGrid").data("igGridPaging")) return;
            let numberBefore = 0;
            let numberAfter = 0;
            let currentPageIndex = $("#dpGrid").igGridPaging("option", "currentPageIndex");
            let pageSize = $("#dpGrid").igGridPaging("option", "pageSize");
            let startIndex: any = currentPageIndex * pageSize;
            let endIndex: any = startIndex + pageSize;
            _.forEach(data, function(d, i) {
                let before = String(d).split('.')[0];
                let after = String(d).split('.')[1];
                if (isNaN(after)) after = 0;
                let aaa = parseInt(before);
                let bbb = parseInt(after);
                numberBefore += aaa;
                numberAfter += bbb;
                if (numberAfter > 9) {
                    numberBefore++;
                    numberAfter = numberAfter - 10;
                }
            });
            return numberBefore + "." + numberAfter;
        }
        totalNumber(data) {
            if (!$("#dpGrid").data("igGridPaging")) return;
            let total = 0;
            let currentPageIndex = $("#dpGrid").igGridPaging("option", "currentPageIndex");
            let pageSize = $("#dpGrid").igGridPaging("option", "pageSize");
            let startIndex: any = currentPageIndex * pageSize;
            let endIndex: any = startIndex + pageSize;
            _.forEach(data, function(d, i) {
                if (i < startIndex || i >= endIndex) return;
                let n = parseInt(d);
                if (!isNaN(n)) total += n;
            });
            return total;
        }
        totalTime(data) {
            if (!$("#dpGrid").data("igGridPaging")) return;
            let currentPageIndex = $("#dpGrid").igGridPaging("option", "currentPageIndex");
            let pageSize = $("#dpGrid").igGridPaging("option", "pageSize");
            let startIndex: any = currentPageIndex * pageSize;
            let endIndex: any = startIndex + pageSize;
            let total = 0;
            _.forEach(data, function(d, i) {
                if (i < startIndex || i >= endIndex) return;
                if (d != "") {
                    total = total + moment.duration(d).asMinutes();
                }
            });
            let hours = total > 0 ? Math.floor(total / 60) : Math.ceil(total / 60);
            let minus = Math.abs(total % 60);
            minus = (minus < 10) ? '0' + minus : minus;
            return ((total < 0 && hours == 0) ? "-" + hours : hours) + ":" + minus;
        }

        totalMoney(data) {
            if (!$("#dpGrid").data("igGridPaging")) return;
            let total = 0;
            let currentPageIndex = $("#dpGrid").igGridPaging("option", "currentPageIndex");
            let pageSize = $("#dpGrid").igGridPaging("option", "pageSize");
            let startIndex: any = currentPageIndex * pageSize;
            let endIndex: any = startIndex + pageSize;
            _.forEach(data, function(d, i) {
                let valueResult = "";
                if (i < startIndex || i >= endIndex) return;
                if (String(d).indexOf(",") != -1) {
                    for (let i = 0; i < String(d).split(',').length; i++) {
                        valueResult += String(d).split(',')[i];
                    }
                    let n = parseFloat(valueResult);
                    if (!isNaN(n)) total += n;
                } else {
                    let n = parseFloat(d);
                    if (!isNaN(n)) total += n;
                }
            });
            return total.toLocaleString('en');
        }
        
        /**
         * Create NtsControls
         */
        getNtsControls(initMode: number, closureId: any): Array<any> {
            let self = this;
            let ntsControls: Array<any> = [
                { name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                {
                    name: 'Link2',
                    click: function(rowId, key, event) {
                        if (!self.clickCounter.clickLinkGrid) {
                            self.clickCounter.clickLinkGrid = true;
                            let value = $("#dpGrid").mGrid("getCellValue", rowId, "Code" + key.substring(4, key.length));
                            let dialog: TypeDialog = new TypeDialog(key.substring(4, key.length), self.lstAttendanceItem(), value, rowId);
                            dialog.showDialog(self);
                            nts.uk.ui.block.clear();
                        }
                    },
                    controlType: 'LinkLabel'
                },
                {
                    name: 'Button', controlType: 'Button', text: getText("KMW003_29"), enable: true, click: function(data) {
                        let self = this;
                        let source: any = $("#dpGrid").mGrid("dataSource");
                        let rowSelect = _.find(source, (value: any) => {
                            return value.id == data.id;
                        })
                        let initParam = new DPCorrectionInitParam(ScreenMode.NORMAL, [rowSelect.employeeId], false, false, closureId, __viewContext.vm.yearMonth(), '/view/kmw/003/a/index.xhtml?initmode='+ initMode);
                        let extractionParam = new DPCorrectionExtractionParam(DPCorrectionDisplayFormat.INDIVIDUAl, rowSelect.startDate, rowSelect.endDate, _.map(__viewContext.vm.dpData, data => data.employeeId), rowSelect.employeeId);
                        nts.uk.request.jump("/view/kdw/003/a/index.xhtml", { initParam: initParam, extractionParam: extractionParam });
                    }
                },
                {
                    name: 'FlexImage', source: 'ui-icon ui-icon-locked', click: function(key, rowId, evt) {
                        let data = $("#dpGrid").mGrid("getCellValue", rowId, key);
                        if (data != "") {
                            let lock = data.split("|");
                            let tempD = "<span>";
                            for (let i = 0; i < lock.length; i++) {
                                //月別実績のロック
                                if (lock[i] == "monthlyResultLock")
                                    tempD += getText("KMW003_35") + '<br/>';
                                //職場の就業確定
                                if (lock[i] == "employmentConfirmWorkplace")
                                    tempD += getText("KMW003_36") + '<br/>';
                                //月別実績の承認
                                if (lock[i] == "monthlyResultApprova")
                                    tempD += getText("KMW003_37") + '<br/>';
                                //日別実績の不足
                                if (lock[i] == "monthlyResultLack")
                                    tempD += getText("KMW003_38") + '<br/>';
                                //日別実績の確認
                                if (lock[i] == "monthlyResultConfirm")
                                    tempD += getText("KMW003_39") + '<br/>';
                                //日別実績のエラー
                                if (lock[i] == "monthlyResultError")
                                    tempD += getText("KMW003_40") + '<br/>';
                                //過去実績のロック
                                if (lock[i] == "pastPerformaceLock")
                                    tempD += getText("KMW003_41") + '<br/>';
                            }
                            tempD += '</span>';
                            $('#textLock').html(tempD);
                        }
                        self.helps(evt, "");
                    }, controlType: 'FlexImage'
                },
                { name: 'Image', source: 'ui-icon ui-icon-info', controlType: 'Image' },
                { name: 'TextEditor', controlType: 'TextEditor', constraint: { valueType: 'Integer', required: true, format: "Number_Separated" } }
            ];
            return ntsControls;
        }
        
         getNtsFeatures(): Array<any> {
            let self = this;
            //Dummy data
            let keys = [];
            for (let i = 0; i < 300; i++) {
                keys.push(i);
            }

            let features: Array<any> = [{ name: 'CopyPaste' },
                { name: 'CellEdit' },
                {
                    name: 'CellColor', columns: [
                        {
                            key: 'ruleCode',
                            parse: function(value) {
                                return value;
                            },
                            map: function(result) {
                                if (result <= 1) return "#00b050";
                                else if (result === 2) return "pink";
                                else return "#0ff";
                            }
                        }
                    ]
                }
            ];
            if (self.sheetsGrid().length > 0) {
                features.push({
                    name: "Sheet",
                    initialDisplay: self.sheetsGrid()[0].name,
                    sheets: self.sheetsGrid()
                });
            }
            return features;
        }
        
        /**
         * Setting header style
         */
        setHeaderColor() {
            let self = this;
            self.headerColors([]);
            _.forEach(self.headersGrid(), (header) => {
                //Setting color single header
                if (header.color) {
                    self.headerColors.push({
                        key: header.key,
                        colors: [header.color]
                    });
                }
                //Setting color group header
                if (header.group != null && header.group != undefined && header.group.length > 0) {
                    self.headerColors.push({
                        key: header.group[0].key,
                        colors: [header.group[0].color]
                    });
                    self.headerColors.push({
                        key: header.group[1].key,
                        colors: [header.group[1].color]
                    });
                }
            });
        }
        reloadGrid() {
            let self = this;
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            setTimeout(function() {
                self.columnSettings(self.dataAll().lstControlDisplayItem.columnSettings);
                self.receiveData(self.dataAll());
                self.createSumColumn(self.dataAll());
                self.extractionData();
                self.loadGrid();
                nts.uk.ui.block.clear();
            }, 500);
        }
        reloadGridLock(data) {
            let self = this;
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            setTimeout(function() {
                self.columnSettings(self.dataAll().lstControlDisplayItem.columnSettings);
                self.receiveData(self.dataAll());
                self.createSumColumn(self.dataAll());
                self.extractionData();
                self.loadGrid();
                _.forEach(data.mpsateCellHideControl, (cellHide => {
                    $('#dpGrid').mGrid("setState", cellHide.rowId, cellHide.columnKey, ["mgrid-hide"])
                }));
                nts.uk.ui.block.clear();
            }, 500);
        }
        loadHeader(mode) {
            let self = this;
            let tempList = [];
//            self.dislayNumberHeaderText();
            self.displayProfileIcon();
            _.forEach(self.optionalHeader, (header) => {
                if (header.constraint == null || header.constraint == undefined) {
                    delete header.constraint;
                } else {
                    if (header.constraint.cdisplayType == null || header.constraint.cdisplayType == undefined) {
                        header.constraint.cdisplayType = header.constraint.cDisplayType;
                    }
                    header.constraint["cDisplayType"] = header.constraint.cdisplayType;
                    if (header.constraint.cDisplayType != null && header.constraint.cDisplayType != undefined) {
                        if (header.constraint.cDisplayType != "Primitive" && header.constraint.cDisplayType != "Combo") {
                            if (header.constraint.cDisplayType.indexOf("Currency") != -1) {
                                header["columnCssClass"] = "currency-symbol halign-right";
                                header.constraint["min"] = header.constraint.min;
                                header.constraint["max"] = header.constraint.max;
                            } else if (header.constraint.cDisplayType == "Clock") {
                                header["columnCssClass"] = "right-align";
                                header.constraint["min"] = "0:00";
                                header.constraint["max"] = "999:59"
                            } else if (header.constraint.cDisplayType == "Integer") {
                                header["columnCssClass"] = "right-align";
                                header.constraint["min"] = "0";
                                header.constraint["max"] = "99"
                            } else if (header.constraint.cDisplayType == "HalfInt") {
                                header["columnCssClass"] = "right-align";
                                header.constraint["min"] = "0";
                                header.constraint["max"] = "99.5"
                            }
                            delete header.constraint.primitiveValue;
                        } else {

                            if (header.constraint.cDisplayType == "Primitive") {
                                if (header.group == undefined || header.group.length == 0) {
                                    delete header.constraint.cDisplayType;
//                                    if (header.constraint.primitiveValue.indexOf("AttendanceTime") != -1) {
//                                        header["columnCssClass"] = "halign-right";
//                                    }
//                                    if (header.constraint.primitiveValue == "BreakTimeGoOutTimes" || header.constraint.primitiveValue == "WorkTimes") {
//                                        header["columnCssClass"] = "halign-right";
//                                    }
                                    if (header.constraint.primitiveValue == "BreakTimeGoOutTimes" || header.constraint.primitiveValue == "WorkTimes" || header.constraint.primitiveValue == "AnyItemTimes" || header.constraint.primitiveValue == "AnyTimesMonth") {
                                        header["columnCssClass"] = "halign-right";
                                        header.constraint["decimallength"] = 2;
                                    }
                                } else {
                                    delete header.group[0].constraint.cDisplayType;
                                    delete header.constraint;
                                    delete header.group[1].constraint;
                                }
                            } else if (header.constraint.cDisplayType == "Combo") {
                                header.group[0].constraint["min"] = 0;
                                header.group[0].constraint["max"] = Number(header.group[0].constraint.primitiveValue);
                                header.group[0].constraint["cDisplayType"] = header.group[0].constraint.cdisplayType;
                                delete header.group[0].constraint.cdisplayType
                                delete header.group[0].constraint.primitiveValue;
                                delete header.constraint;
                                delete header.group[1].constraint;
                            }
                        }
                    }
                    if (header.constraint != undefined) delete header.constraint.cdisplayType;
                }
                if (header.group != null && header.group != undefined) {
                    if (header.group.length > 0) {
                        if (header.group[0].constraint == undefined) delete header.group[0].constraint;
                        delete header.group[1].constraint;
                        delete header.group[0].group;
                        delete header.key;
                        delete header.dataType;
                        // delete header.width;
                        delete header.ntsControl;
                        delete header.changedByOther;
                        delete header.changedByYou;
                        // delete header.color;
                        delete header.hidden;
                        delete header.ntsType;
                        delete header.onChange;
                        delete header.group[1].ntsType;
                        delete header.group[1].onChange;
                        if (header.group[0].dataType == "String") {
                            header.group[0].onChange = self.search;
                            // delete header.group[0].onChange;
                            delete header.group[0].ntsControl;
                        } else {
                            delete header.group[0].onChange;
                            delete header.group[0].ntsControl;
                        }
                        delete header.group[1].group;
                    } else {
                        delete header.group;
                    }
                }
                tempList.push(header);
            });
            return self.headersGrid(tempList);
        }

        displayProfileIcon() {
            let self = this;
            if (self.showProfileIcon()) {
                _.remove(self.optionalHeader, function(header) {
                    return header.key === "picture-person";
                });
                _.remove(self.fixColGrid(), function(header) {
                    return header.columnKey === "picture-person";
                });
                self.optionalHeader.splice(5, 0, { headerText: '', key: "picture-person", dataType: "string", width: '35px', ntsControl: 'Image' });
                self.fixColGrid().splice(5, 0, { columnKey: 'picture-person', isFixed: true });
            } else {
                _.remove(self.optionalHeader, function(header) {
                    return header.key === "picture-person";
                });
                _.remove(self.fixColGrid(), function(header) {
                    return header.columnKey === "picture-person";
                });
            }
        }

//        dislayNumberHeaderText() {
//            let self = this;
//            if (self.showHeaderNumber()) {
//                self.optionalHeader.map((header) => {
//                    if (header.headerText) {
//                        header.headerText = header.headerText + " " + header.key.substring(1, header.key.length);
//                    }
//                    return header;
//                });
//            } else {
//                self.optionalHeader.map((header) => {
//                    if (header.headerText) {
//                        header.headerText = header.headerText.split(" ")[0];
//                    }
//                    return header;
//                });
//            }
//        }
        
        helps(event, data) {
            var self = this;
            $('#tooltip').css({
                'left': event.pageX + 15,
                'top': event.pageY - 12,
                'display': 'none',
                'position': 'fixed',
            });
            self.lockMessage(data);
            $("#tooltip").show();
        }
        /**
         * 実績期間を変更する
         */
        updateActualTime() {
            let self = this;
            self.monthlyParam().actualTime = self.actualTimeSelectedDat();
            //            self.monthlyParam().actualTime.startDate = moment(self.actualTimeSelectedDat().startDate).toISOString();
            //            self.monthlyParam().actualTime.endDate = moment(self.actualTimeSelectedDat().endDate).toISOString();
            //            //self.actualTimeSelectedDat
            //            self.initScreen();
        }
        /**
         * Check all CheckBox
         */
        signAll() {
            let self = this;
            if(self.noCheckColumn()) return;
            $("#dpGrid").mGrid("checkAll", "identify", true);
            $("#dpGrid").mGrid("checkAll", "approval", true);
        }
        /**
         * UnCheck all CheckBox
         */
        releaseAll() {
            let self = this;
            if(self.noCheckColumn()) return;
            $("#dpGrid").mGrid("uncheckAll", "identify", true);
            $("#dpGrid").mGrid("uncheckAll", "approval", true);
        }
        /**
         * ロック解除ボタン　クリック
         */
        unlockProcess() {
            let self = this;
            //アルゴリズム「ロックを解除する」を実行する
            nts.uk.ui.dialog.confirm({ messageId: "Msg_983" }).ifYes(() => {
                //画面モードを「ロック解除モード」に変更する
                self.initMode(ScreenMode.UNLOCK);
                self.showButton(new AuthorityDetailModel(self.dataAll().authorityDto, self.dataAll().actualTimeState, self.initMode(), self.dataAll().formatPerformance.settingUnitType));
                //ロック状態を画面に反映する
                $("#dpGrid").mGrid("destroy");
                $("#dpGrid").off();
                self.monthlyParam().lstLockStatus = [];
                if (self.monthlyParam().actualTime) {
                    self.monthlyParam().actualTime.startDate = moment.utc(self.monthlyParam().actualTime.startDate, "YYYY/MM/DD").toISOString();
                    self.monthlyParam().actualTime.endDate = moment.utc(self.monthlyParam().actualTime.endDate, "YYYY/MM/DD").toISOString();
                }
                self.monthlyParam().initMenuMode = self.initMode();
                service.startScreen(self.monthlyParam()).done((data) => {
                    self.dataAll(data);
                    self.reloadGridLock(data);
                });
            }).ifNo(() => {

            });
        }
        /**
         * 再ロックボタン　クリック
         */
        lockProcess() {
            let self = this;
            // 画面モードを「修正モード」に変更する
            self.initMode(ScreenMode.NORMAL);
            self.showButton(new AuthorityDetailModel(self.dataAll().authorityDto, self.dataAll().actualTimeState, self.initMode(), self.dataAll().formatPerformance.settingUnitType));
            //アルゴリズム「ロック状態を表示する」を実行する
            //確認メッセージ「#Msg_984」を表示する
            $("#dpGrid").mGrid("destroy");
            $("#dpGrid").off();
            self.monthlyParam().lstLockStatus = [];
            if (self.monthlyParam().actualTime) {
                self.monthlyParam().actualTime.startDate = moment.utc(self.monthlyParam().actualTime.startDate, "YYYY/MM/DD").toISOString();
                self.monthlyParam().actualTime.endDate = moment.utc(self.monthlyParam().actualTime.endDate, "YYYY/MM/DD").toISOString();
            }
            self.monthlyParam().initMenuMode = self.initMode();
            service.startScreen(self.monthlyParam()).done((data) => {
                self.dataAll(data);
                self.reloadGridLock(data);
                nts.uk.ui.dialog.info({ messageId: "Msg_984" });
            });
        }
        /**
         * Grid setting
         */
        btnSetting_Click() {
            let container = $("#setting-content");
            if (container.css("visibility") === 'hidden') {
                container.css("visibility", "visible");
                container.css("top", "-1px");
                container.css("left", "258px");
            }
            $(document).mouseup(function(e) {
                // if the target of the click isn't the container nor a descendant of the container
                if (!container.is(e.target) && container.has(e.target).length === 0) {
                    container.css("visibility", "hidden");
                    container.css("top", "-9999px");
                    container.css("left", "-9999px");
                }
            });
        }
        /**
         * 「KDW003_D_抽出条件の選択」を起動する
         * 起動モード：月別 
         * 選択済項目：選択している「月別実績のエラーアラームコード」
         */
        extractCondition() {
            let self = this;
            let errorParam = { initMode: 1, selectedItems: self.selectedErrorCodes() };
            nts.uk.ui.windows.setShared("KDW003D_ErrorParam", errorParam);
            nts.uk.characteristics.save("cacheKMW003",self.dataAll());
            nts.uk.ui.windows.sub.modal("/view/kdw/003/d/index.xhtml").onClosed(() => {
                debugger;
                let errorCodes: any = nts.uk.ui.windows.getShared("KDW003D_Output");
                //アルゴリズム「選択した抽出条件に従って実績を表示する」を実行する
                if (!nts.uk.util.isNullOrUndefined(errorCodes)) {
                    //TODO Client filter
                    //選択されたエラーコード一覧
                }
            });
        }

        /**
         * 「KDW003_C_表示フォーマットの選択」を起動する
         * 起動モード：月別
         * 選択済項目：選択している「月別実績のフォーマットコード」
         */
        displayItem(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            let formatParam = { initMode: 1, selectedItem: "" };
            nts.uk.ui.windows.setShared("KDW003C_Param", formatParam);
            nts.uk.ui.windows.sub.modal("/view/kdw/003/c/index.xhtml").onClosed(() => {
                let res = nts.uk.ui.windows.getShared('KDW003C_Err');
                if(!_.isEmpty(res) && res.jumpToppage){
                    nts.uk.request.jumpToTopPage();
                }
                let formatCd = nts.uk.ui.windows.getShared('KDW003C_Output');
                if (formatCd) {
                    self.formatCodes.removeAll();
                    self.formatCodes.push(formatCd);
                    self.initScreenFormat().done((processDate) => {
						if(!_.isNil(processDate)){
							self.yearMonth(processDate);
						}
                        dfd.resolve();
                    }).fail(function(error) {
                        nts.uk.ui.dialog.alert({ messageId: error.messageId }).then(function() {
                            nts.uk.request.jumpToTopPage();
                        });
                        dfd.reject();
                    });
                } else {
                    dfd.reject();
                }
            });
            return dfd.promise();
        }
        
        openCDL027() {
            let self = this,
                period = {
                    startDate : self.yearMonth(),
                    endDate : self.actualTimeDats()[self.actualTimeSelectedCode()].endDate  
                },
                param = {
                    pgid: __viewContext.program.programId,
                    functionId: 3,
                    listEmployeeId: _.map(self.lstEmployee(), emp => { return emp.id; }),
                    period : period,
                    displayFormat: 1 
                };
            nts.uk.ui.windows.setShared("CDL027Params", param);
            nts.uk.ui.windows.sub.modal('com',"/view/cdl/027/a/index.xhtml");
        }

		openKDL006(){
		 	nts.uk.ui.block.grayout();
	        modal("/view/kdl/006/a/index.xhtml").onClosed(() => {
	            nts.uk.ui.block.clear();
	        });
		}
        
        search(columnKey, rowId, val, valOld) {
            let dfd = $.Deferred();
            let i = 0, baseDate = null;
            let data: any = $("#dpGrid").mGrid("dataSource");
            let rowItemSelect: any = _.find(data, function(value: any) {
                return value.id == rowId;
            })
            
            let columnId: number = +columnKey.substring(4, columnKey.length);
            let typeGroup = 0;
            if(columnId == 192 || columnId == 197){
                typeGroup = TypeGroup.EMPLOYMENT;
            }
            if(columnId == 193 || columnId == 198){
                typeGroup = TypeGroup.POSITION;
                if(columnId == 193){
                    baseDate = __viewContext.vm.actualTimeDats()[0].startDate;
                } else {
                    baseDate = __viewContext.vm.actualTimeDats()[0].endDate;
                }
                
            }
            if(columnId == 194 || columnId == 199){
                typeGroup = TypeGroup.WORKPLACE;
                if(columnId == 194){
                    baseDate = __viewContext.vm.actualTimeDats()[0].startDate;
                } else {
                    baseDate = __viewContext.vm.actualTimeDats()[0].endDate;
                }
            }
            if(columnId == 195 || columnId == 200){
                typeGroup = TypeGroup.CLASSIFICATION;
            }
            if(columnId == 196 || columnId == 201){
                typeGroup = TypeGroup.BUSINESSTYPE;
            }

            //remove error
            _.remove(__viewContext.vm.workTypeNotFound, error => {
                return error.columnKey == columnKey && error.rowId == rowId;
            })

            if (typeGroup != undefined && typeGroup != null) {
                let param = {
                    typeDialog: typeGroup,
                    param: {
                        employmentCode: rowItemSelect.employmentCode,
                        workplaceId: rowItemSelect.workplaceId,
                        date: moment.utc(baseDate),
                        selectCode: val,
                        employeeId: rowItemSelect.employeeId,
                        itemId: columnId,
                        valueOld: valOld
                    }
                }
                var object = {};
                if (val == "") {
                    dfd.resolve(getText("KDW003_82"));
                } else {
                    $.when(service.findCodeName(param)).done((data) => {
                        if (data != undefined) {
                            let typeError = _.find(__viewContext.vm.workTypeNotFound, data => {
                                return data.columnKey == columnKey && data.rowId == rowId;
                            });
                            if (data.errorFind == 1) {
                                let e = document.createEvent("HTMLEvents");
                                e.initEvent("mouseup", false, true);
                                $("#dpGrid")[0].dispatchEvent(e);
                                if (typeError == undefined) {
                                    __viewContext.vm.workTypeNotFound.push({ columnKey: columnKey, rowId: rowId, message: nts.uk.resource.getMessage("Msg_1293"), employeeId: rowItemSelect.employeeId, date: moment(rowItemSelect.dateDetail)});
                                } else {
                                    typeError.message = nts.uk.resource.getMessage("Msg_1293");
                                }
                                nts.uk.ui.dialog.alertError({ messageId: "Msg_1293" })
                            } else if (data.errorFind == 2) {
                                let e = document.createEvent("HTMLEvents");
                                e.initEvent("mouseup", false, true);
                                $("#dpGrid")[0].dispatchEvent(e);
                                if (typeError == undefined) {
                                    __viewContext.vm.workTypeNotFound.push({ columnKey: columnKey, rowId: rowId, message: nts.uk.resource.getMessage("Msg_1314"), employeeId: rowItemSelect.employeeId, date: moment(rowItemSelect.dateDetail) });
                                } else {
                                    typeError.message = nts.uk.resource.getMessage("Msg_1314");
                                }
                                nts.uk.ui.dialog.alertError({ messageId: "Msg_1314" })
                            } else {
                                _.remove(__viewContext.vm.workTypeNotFound, dataTemp => {
                                    return dataTemp.columnKey == columnKey && dataTemp.rowId == rowId;
                                });
                            }

                            dfd.resolve(data == undefined ? getText("KDW003_81") : data.name);
                        } else {
                            _.remove(__viewContext.vm.workTypeNotFound, dataTemp => {
                                return dataTemp.columnKey == columnKey && dataTemp.rowId == rowId;
                            });
                            dfd.resolve(data == undefined ? getText("KDW003_81") : data.name);
                        }
                    });
                }

            } else {
                dfd.resolve("");
            }
            return dfd.promise();
        }
        
    }
    
    export class AuthorityDetailModel {
        //A4_2           
        available_A4_2: KnockoutObservable<boolean> = ko.observable(true);
        //A4_7         
        available_A4_7: KnockoutObservable<boolean> = ko.observable(false);
        //A1_1
        available_A1_1: KnockoutObservable<boolean> = ko.observable(false);
        enable_A1_1:  /*boolean = false;*/KnockoutObservable<boolean> = ko.observable(false);
        //A1_2 
        available_A1_2: KnockoutObservable<boolean> = ko.observable(false);
        enable_A1_2: KnockoutObservable<boolean> = ko.observable(false);
        //A1_4
        available_A1_4: KnockoutObservable<boolean> = ko.observable(false);
        //A1_8
        available_A1_8: KnockoutObservable<boolean> = ko.observable(false);
        //A1_9
        available_A1_9: KnockoutObservable<boolean> = ko.observable(false);
        //A1_11           
        available_A1_11: KnockoutObservable<boolean> = ko.observable(false);
        //A5_4          
        available_A5_4: KnockoutObservable<boolean> = ko.observable(false);
        enable_multiActualTime: KnockoutObservable<boolean> = ko.observable(false);
        enable_A5_4: KnockoutObservable<boolean> = ko.observable(false);
        enable_A1_5: KnockoutObservable<boolean> = ko.observable(true);

        prevData: Array<DailyPerformanceAuthorityDto> = null;
        prevInitMode: number = 0;
        /**
         * formatPerformance: 権限 = 0, 勤務種別 = 1
         * initMode: 修正モード  = 0,  ロック解除モード    = 1  
         * actualTimeState: 過去 = 0, 当月 = 1, 未来 = 2
         */
        constructor(data: Array<DailyPerformanceAuthorityDto>, actualTimeState: number, initMode: number, formatPerformance: number) {
            let self = this;
            if (!data) return;
            $('#cbClosureInfo').hide();
            self.available_A4_7(false);
            self.available_A1_1(self.checkAvailable(data, 32));
            self.available_A1_2(self.checkAvailable(data, 33));
            self.available_A1_4(self.checkAvailable(data, 34));
            self.available_A5_4(self.checkAvailable(data, 11));
            if (initMode == 0) { //修正モード
                if (formatPerformance == 0) { //権限
                    self.enable_A1_1(actualTimeState == 1 || actualTimeState == 2);
                    self.enable_A1_2(actualTimeState == 1 || actualTimeState == 2);
                    self.enable_A5_4(actualTimeState == 1 || actualTimeState == 2);
                } else if (formatPerformance == 1) { //勤務種別
                    self.enable_A1_1(actualTimeState == 1 || actualTimeState == 2);
                    self.enable_A1_2(actualTimeState == 1 || actualTimeState == 2);
                    self.enable_A5_4(actualTimeState == 1 || actualTimeState == 2);
                    self.enable_A1_5(false);
                }
                self.available_A1_8(self.checkAvailable(data, 12));
                //A2_1
                $('#ccg001').show();
            } else if (initMode == 1) { //ロック解除モード 
                self.enable_A1_1(true);
                self.enable_A1_2(true);
                self.enable_A5_4(true);
                if (formatPerformance == 1) { //勤務種別
                    self.enable_A1_5(false);
                }
                self.available_A1_9(self.checkAvailable(data, 12));
                self.available_A1_11(self.checkAvailable(data, 12));
                //A2_1
                $('#ccg001').show();

            } else if (initMode == 2) {
                $('#cbClosureInfo').show();
                //A4_7
                self.available_A4_7(true);
                self.available_A1_11(false);
                //A2_1
                $('#ccg001').hide();
                //A4_2
                self.available_A4_2(false);

                /**
                 *Tu Test
                 **/
                //A1_1,A1_2
                self.enable_A1_1(true);
                self.enable_A1_2(true);
                //A5_4
                self.enable_A5_4(actualTimeState == 1 || actualTimeState == 2);
                if (formatPerformance == 0) { //権限
                    self.enable_A1_5(true);
                } else if (formatPerformance == 1) { //勤務種別
                    self.enable_A1_5(false);
                }
            }
            self.prevData = data;
            self.prevInitMode = initMode;
        }
        checkAvailable(data: Array<DailyPerformanceAuthorityDto>, value: number): boolean {
            let self = this;
            let check = _.find(data, function(o) {
                return o.functionNo === value;
            })
            if (check == null) return false;
            else return check.availability;
        };
    }
    
    class ItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    class CLickCount {
        clickLinkGrid: boolean;
        clickErrorRefer: boolean;
        constructor() {
            this.clickLinkGrid = false;
            this.clickErrorRefer = false;
        }
    }

    export interface DailyPerformanceAuthorityDto {
        isDefaultInitial: number;
        roleID: string;
        functionNo: number;
        availability: boolean;
    }

    //    export interface DPAttendanceItem {
    //        id: string;
    //        name: string;
    //        displayNumber: number;
    //        userCanSet: boolean;
    //        lineBreakPosition: number;
    //        attendanceAtr: number;
    //        typeGroup: number;
    //    }

    class InfoCellEdit {
        rowId: any;
        itemId: any;
        value: any;
        valueType: number;
        layoutCode: string;
        employeeId: string;
        typeGroup: number;
        version: number;
        constructor(rowId: any, itemId: any, value: any, valueType: number, layoutCode: string, employeeId: string, typeGroup?: number) {
            this.rowId = rowId;
            this.itemId = itemId;
            this.value = value;
            this.valueType = valueType;
            this.layoutCode = layoutCode;
            this.employeeId = employeeId;
            this.typeGroup = typeGroup;
        }
    }

    class DataHoliday {
        compensation: string;
        substitute: string;
        paidYear: string;
        paidHalf: string;
        paidHours: string;
        fundedPaid: string;
        constructor(compensation: string, substitute: string, paidYear: string, paidHalf: string, paidHours: string, fundedPaid: string) {
            this.compensation = getText("KMW003_8", [compensation])
            this.substitute = getText("KMW003_8", [substitute])
            this.paidYear = getText("KMW003_8", [paidYear])
            //            this.paidHalf = getText("KMW003_10", paidHalf)
            //            this.paidHours = getText("KMW003_11", paidHours)
            this.fundedPaid = getText("KMW003_8", [fundedPaid])
        }

    }
    class RowState {
        rowId: number;
        disable: boolean;
        constructor(rowId: number, disable: boolean) {
            this.rowId = rowId;
            this.disable = disable;
        }
    }
    class TextColor {
        rowId: number;
        columnKey: string;
        color: string;
        constructor(rowId: any, columnKey: string, color: string) {
            this.rowId = rowId;
            this.columnKey = columnKey;
            this.color = color;
        }
    }
    class CellState {
        rowId: number;
        columnKey: string;
        state: Array<any>
        constructor(rowId: number, columnKey: string, state: Array<any>) {
            this.rowId = rowId;
            this.columnKey = columnKey;
            this.state = state;
        }
    }
    export class DPCorrectionInitParam {
        //画面モード
        screenMode: ScreenMode;
        //社員一覧
        lstEmployee: any;
        //エラー参照を起動する
        errorRefStartAtr: boolean;
        //期間を変更する
        changePeriodAtr: boolean;
        //処理締め
        targetClosue: number;
        //Optional
        //打刻初期値
        initClock: any;
        //遷移先の画面
        transitionDesScreen: any;
        //対象年月
        yearMonth: any;

        constructor(screenMode, lstEmployee, errorRefStartAtr, changePeriodAtr, targetClosue, yearMonth, transitionDesScreen) {
            let self = this;
            self.screenMode = screenMode;
            self.lstEmployee = lstEmployee;
            self.errorRefStartAtr = errorRefStartAtr;
            self.changePeriodAtr = changePeriodAtr;
            self.targetClosure = targetClosue;
            self.transitionDesScreen = transitionDesScreen;
            self.yearMonth = yearMonth;
        }
    }

    export class DPCorrectionExtractionParam {
        //表示形式
        displayFormat: number;
        //期間
        startDate: string;
        endDate: string;
        //抽出した社員一覧
        lstExtractedEmployee: any;
        //Optional
        //日付別で起動
        dateTarget: any;

        individualTarget: any;

        constructor(displayFormat, startDate, endDate, lstExtractedEmployee, individualTarget) {
            let self = this;
            self.displayFormat = displayFormat;
            self.startDate = startDate;
            self.endDate = endDate;
            self.lstExtractedEmployee = lstExtractedEmployee;
            self.individualTarget = individualTarget;
        }
    }
    export enum DPCorrectionDisplayFormat {
        //個人別
        INDIVIDUAl = 0,
        //日付別
        DATE = 1,
        //エラー・アラーム
        ErrorAlarm = 2
    }

    export enum ScreenMode {
        //通常
        NORMAL = 0,
        
        UNLOCK = 1,
        //承認
        APPROVAL = 2
    }
    
    export enum TypeGroup {
        WORKPLACE = 5,
        CLASSIFICATION = 6,
        POSITION = 7,
        EMPLOYMENT = 8,
        BUSINESSTYPE = 14
    }
    
    export class TypeDialog {

        attendenceId: string;
        data: Array<DPAttendanceItem>;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        selectedCode: KnockoutObservable<string>;
        listCode: KnockoutObservableArray<any>;
        rowId: KnockoutObservable<any>;
        constructor(attendenceId: string, data: Array<DPAttendanceItem>, selectedCode: string, rowId: any) {
            var self = this;
            self.attendenceId = attendenceId;
            self.data = data;
            self.selectedCode = ko.observable(selectedCode);
            self.listCode = ko.observableArray([]);
            self.rowId = ko.observable(rowId);
        };

        showDialog(parent: any) {
            let self = this;
            let selfParent = parent;
            let item: DPAttendanceItem;
            let codeName: any;
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            item = _.find(self.data, function(data) {
                return data.id == self.attendenceId;
            })
            switch (item.id) {
                case 194:
                    //CDL008 
                    let dateCon194 = selfParent.displayFormat() === 1 ? moment(selfParent.selectedDate()) : moment(__viewContext.vm.actualTimeDats()[0].startDate).utc().toISOString();

                    let param194 = {
                        typeDialog: 5,
                        param: {
                            date: dateCon194
                        }
                    }
                    let data194: any;
                    let dfd194 = $.Deferred();
                    service.findAllCodeName(param194).done((data: any) => {
                        data194 = data;
                        codeName = _.find(data194, (item: any) => {
                            return item.code == self.selectedCode();
                        });
                        setShared('inputCDL008', {
                            selectedCodes: codeName == undefined ? "" : codeName.id,
                            baseDate: dateCon194,
                            isMultiple: false,
                            selectedSystemType: 2,
                            isrestrictionOfReferenceRange: true,
                            showNoSelection: false,
                            isShowBaseDate: false
                        }, true);
                        dfd194.resolve()
                    });
                    dfd194.promise();
                    modal('com', '/view/cdl/008/a/index.xhtml').onClosed(function(): any {
                        // Check is cancel.
                        if (nts.uk.ui.windows.getShared('CDL008Cancel')) {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            return;
                        }
                        //view all code of selected item 
                        var output = nts.uk.ui.windows.getShared('outputCDL008');
                        if (output != "") {
                            codeName = _.find(data194, (item: any) => {
                                return item.id == output;
                            });
                            self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                        } else {
                            self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                        }
                    })
                    break;
                case 199:
                    //CDL008 
                    let dateCon = selfParent.displayFormat() === 1 ? moment(selfParent.selectedDate()) : moment(__viewContext.vm.actualTimeDats()[0].endDate).utc().toISOString();

                    let param5 = {
                        typeDialog: 5,
                        param: {
                            date: dateCon
                        }
                    }
                    let data5: any;
                    let dfd5 = $.Deferred();
                    service.findAllCodeName(param5).done((data: any) => {
                        data5 = data;
                        codeName = _.find(data5, (item: any) => {
                            return item.code == self.selectedCode();
                        });
                        setShared('inputCDL008', {
                            selectedCodes: codeName == undefined ? "" : codeName.id,
                            baseDate: dateCon,
                            isMultiple: false,
                            selectedSystemType: 2,
                            isrestrictionOfReferenceRange: true,
                            showNoSelection: false,
                            isShowBaseDate: false
                        }, true);
                        dfd5.resolve()
                    });
                    dfd5.promise();
                    modal('com', '/view/cdl/008/a/index.xhtml').onClosed(function(): any {
                        // Check is cancel.
                        if (nts.uk.ui.windows.getShared('CDL008Cancel')) {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            return;
                        }
                        //view all code of selected item 
                        var output = nts.uk.ui.windows.getShared('outputCDL008');
                        if (output != "") {
                            codeName = _.find(data5, (item: any) => {
                                return item.id == output;
                            });
                            self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                        } else {
                            self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                        }
                    })
                    break;
                case 195:
                case 200:
                    //KCP002
                    let dfd6 = $.Deferred();
                    setShared('inputCDL003', {
                        selectedCodes: self.selectedCode(),
                        showNoSelection: false,
                        isMultiple: false
                    }, true);

                    modal('com', '/view/cdl/003/a/index.xhtml').onClosed(function(): any {
                        //view all code of selected item 
                        var output = nts.uk.ui.windows.getShared('outputCDL003');
                        if (output != undefined && output != "") {
                            let param6 = {
                                typeDialog: 6
                            }
                            service.findAllCodeName(param6).done((data: any) => {
                                codeName = _.find(data, (item: any) => {
                                    return item.code == output;
                                });
                                self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                dfd6.resolve()
                            });
                        } else {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            if (output == "") self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            dfd6.resolve()
                        }
                    })
                    dfd6.promise();
                    break;
                case 193:
                    //KCP003 
                    let dateCon193 = selfParent.displayFormat() === 1 ? moment(selfParent.selectedDate()) : moment(__viewContext.vm.actualTimeDats()[0].startDate).utc().toISOString();
                    let param193 = {
                        typeDialog: 7,
                        param: {
                            date: dateCon193
                        }
                    }
                    let data193: any;
                    let dfd193 = $.Deferred();
                    service.findAllCodeName(param193).done((data: any) => {
                        data193 = data;
                        codeName = _.find(data, (item: any) => {
                            return item.code == self.selectedCode();
                        });
                        setShared('inputCDL004', {
                            baseDate: dateCon193,
                            selectedCodes: codeName == undefined ? "" : codeName.id,
                            showNoSelection: false,
                            isMultiple: false,
                            isShowBaseDate: false
                        }, true);
                        dfd193.resolve();
                    });
                    dfd193.promise();
                    modal('com', '/view/cdl/004/a/index.xhtml').onClosed(function(): any {
                        var isCancel = nts.uk.ui.windows.getShared('CDL004Cancel');
                        if (isCancel) {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            return;
                        }
                        var output = nts.uk.ui.windows.getShared('outputCDL004');
                        if (output != "") {
                            codeName = _.find(data193, (item: any) => {
                                return item.id == output;
                            });
                            self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                        } else {
                            self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                        }
                    })
                    break;
                case 198:
                    //KCP003 
                    let dateCon7 = selfParent.displayFormat() === 1 ? moment(selfParent.selectedDate()) : moment(__viewContext.vm.actualTimeDats()[0].endDate).utc().toISOString();
                    let param7 = {
                        typeDialog: 7,
                        param: {
                            date: dateCon7
                        }
                    }
                    let data7: any;
                    let dfd7 = $.Deferred();
                    service.findAllCodeName(param7).done((data: any) => {
                        data7 = data;
                        codeName = _.find(data, (item: any) => {
                            return item.code == self.selectedCode();
                        });
                        setShared('inputCDL004', {
                            baseDate: dateCon7,
                            selectedCodes: codeName == undefined ? "" : codeName.id,
                            showNoSelection: false,
                            isMultiple: false,
                            isShowBaseDate: false
                        }, true);
                        dfd7.resolve();
                    });
                    dfd7.promise();
                    modal('com', '/view/cdl/004/a/index.xhtml').onClosed(function(): any {
                        var isCancel = nts.uk.ui.windows.getShared('CDL004Cancel');
                        if (isCancel) {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            return;
                        }
                        var output = nts.uk.ui.windows.getShared('outputCDL004');
                        if (output != "") {
                            codeName = _.find(data7, (item: any) => {
                                return item.id == output;
                            });
                            self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                        } else {
                            self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                        }
                    })
                    break;
                case 192:
                case 197:
                    let dfd8 = $.Deferred();
                    setShared('CDL002Params', {
                        isMultiple: false,
                        selectedCodes: [self.selectedCode()],
                        showNoSelection: false
                    }, true);

                    modal('com', '/view/cdl/002/a/index.xhtml').onClosed(function(): any {
                        // nts.uk.ui.block.clear();
                        var isCancel = nts.uk.ui.windows.getShared('CDL002Cancel');
                        if (isCancel) {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            return;
                        }
                        var output = nts.uk.ui.windows.getShared('CDL002Output');
                        if (output != "") {
                            let param8 = {
                                typeDialog: 8,
                            }
                            service.findAllCodeName(param8).done((data: any) => {
                                nts.uk.ui.block.clear();
                                codeName = _.find(data, (item: any) => {
                                    return item.code == output;
                                });
                                self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                dfd8.resolve();
                            });
                        } else {
                            self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            dfd8.resolve();
                        }
                    })
                    dfd8.promise();
                    break;
                 
                case 196:
                case 201:
                    let dfd14 = $.Deferred();
                    setShared('CDL024', {
                        codeList: [self.selectedCode()],
                        selectMultiple: false
                    });

                    modal('com', '/view/cdl/024/index.xhtml').onClosed(function(): any {
                        // nts.uk.ui.block.clear();
                        let output =  nts.uk.ui.windows.getShared("currentCodeList");
                        if (!output) {
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            return;
                        }

                        if (output != "") {
                            let param14 = {
                                typeDialog: 14,
                            }
                            service.findAllCodeName(param14).done((data: any) => {
                                nts.uk.ui.block.clear();
                                codeName = _.find(data, (item: any) => {
                                    return item.code == output;
                                });
                                self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                dfd14.resolve();
                            });
                        } else {
                            self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            dfd14.resolve();
                        }
                    })
                    dfd14.promise();
                    break;  
            }
            nts.uk.ui.block.clear();
        }

        updateCodeName(rowId: any, itemId: any, name: any, code: any, valueOld: any) {
            let dfd = $.Deferred();
            __viewContext.vm.clickCounter.clickLinkGrid = false;
            if (code == valueOld) {
                dfd.resolve();
            } else {
                nts.uk.ui.block.invisible();
                nts.uk.ui.block.grayout();
                _.remove(__viewContext.vm.workTypeNotFound, dataTemp => {
                    return dataTemp.columnKey == "Code" + itemId && dataTemp.rowId == rowId;
                });
                $("#dpGrid").mGrid("updateCell", rowId, "Name" + itemId, name)
                $("#dpGrid").mGrid("updateCell", rowId, "Code" + itemId, code);
            }
            nts.uk.ui.block.clear();
            return dfd.promise();
        }
    }

}