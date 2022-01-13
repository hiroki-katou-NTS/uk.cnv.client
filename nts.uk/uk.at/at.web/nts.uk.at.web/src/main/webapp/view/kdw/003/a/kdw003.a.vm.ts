module nts.uk.at.view.kdw003.a.viewmodel {
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getText = nts.uk.resource.getText;
    import character = nts.uk.characteristics;
    let __viewContext: any = window["__viewContext"] || {};
    export interface EmployeeSearchDto {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        workplaceCode: string;
        workplaceId: string;
        workplaceName: string;
    }

    var LIST_FIX_HEADER = [
        { headerText: 'ID', key: 'id', dataType: 'String', width: '30px', ntsControl: 'Label' },
        { headerText: '状<br/>態', key: 'state', dataType: 'String', width: '30px', ntsControl: 'Label' },
        { headerText: getText("KDW003_129"), key: 'error', dataType: 'String', width: '60px', ntsControl: 'Label' },
        { headerText: getText("KDW003_41"), key: 'date', dataType: 'String', width: '90px', ntsControl: 'Label' },
        { headerText: getText("KDW003_42"), key: 'sign', dataType: 'boolean', width: '35px', ntsControl: 'Checkbox' },
        { headerText: getText("KDW003_32"), key: 'employeeCode', dataType: 'String', width: '87px', ntsControl: 'Label' },
        { headerText: getText("KDW003_33"), key: 'employeeName', dataType: 'String', width: '162px', ntsControl: 'Label' },
        { headerText: '', key: "picture-person", dataType: "string", width: '35px', ntsControl: 'Image' }
    ];

    var CHECK_INPUT = {
        "759": "760", "760": "759", "761": "762",
        "762": "761", "763": "764", "764": "763",
        "765": "766", "766": "765", "157": "159",
        "159": "157", "163": "165", "165": "163",
        "169": "171", "171": "169",
        "175": "177", "177": "175", "181": "183",
        "183": "181", "187": "189", "189": "187",
        "193": "195", "195": "193", "199": "201",
        "201": "199", "205": "207", "207": "205",
        "211": "213", "213": "211",
        "7": "8", "8": "7", "9": "10",
        "10": "9", "11": "12", "12": "11",
        "13": "14", "14": "13", "15": "16",
        "16": "15",
        "17": "18", "18": "17", "19": "20",
        "20": "19", "21": "22", "22": "21",
        "23": "24", "24": "23", "25": "26",
        "26": "25"
    }

    var ITEM_CHANGE = [28, 29, 31, 34, 41, 44, 859, 860, 623, 625];

    var DEVIATION_REASON_MAP = { "438": 1, "443": 2, "448": 3, "453": 4, "458": 5, "801": 6, "806": 7, "811": 8, "816": 9, "821": 10 };

	var WORK_FRAME_MAP = {
		1: [924,934,944,954,964,974,984,994,1004,1014,1024,1034,1044,1054,1064,1074,1084,1094,1104,1114],
		2: [925,935,945,955,965,975,985,995,1005,1015,1025,1035,1045,1055,1065,1075,1085,1095,1105,1115],
		3: [926,936,946,956,966,976,986,996,1006,1016,1026,1036,1046,1056,1066,1076,1086,1096,1106,1116],
		4: [927,937,947,957,967,977,987,997,1007,1017,1027,1037,1047,1057,1067,1077,1087,1097,1107,1117],
		5: [928,938,948,958,968,978,988,998,1008,1018,1028,1038,1048,1058,1068,1078,1088,1098,1108,1118]
	};

    export class ScreenModel {
        fixHeaders: KnockoutObservableArray<any> = ko.observableArray([]);

        legendOptions: any;
        //grid user setting
        cursorMoveDirections: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: "縦" },
            { code: 1, name: "横" }
        ]);
        selectedDirection: KnockoutObservable<any> = ko.observable(0);
        displayWhenZero: KnockoutObservable<boolean> = ko.observable(false);
        showHeaderNumber: KnockoutObservable<boolean> = ko.observable(false);
        showProfileIcon: KnockoutObservable<boolean> = ko.observable(false);
        //ccg001 component: search employee
        ccg001: any;
        baseDate: KnockoutObservable<Date> = ko.observable(new Date());
        //kcp009 component: employee picker
        selectedEmployee: KnockoutObservable<any> = ko.observable(null);
        lstEmployee: KnockoutObservableArray<any> = ko.observableArray([]);;
        //data grid
        displayFormatOptions: KnockoutObservableArray<any>;
        displayFormat: KnockoutObservable<number> = ko.observable(null);
        headersGrid: any = [];
        columnSettings: KnockoutObservableArray<any> = ko.observableArray([]);
        sheetsGrid: KnockoutObservableArray<any> = ko.observableArray([]);
        fixColGrid: KnockoutObservableArray<any>;
        dailyPerfomanceData: KnockoutObservableArray<any> = ko.observableArray([]);
        cellStates: KnockoutObservableArray<any> = ko.observableArray([]);
        rowStates: KnockoutObservableArray<any> = ko.observableArray([]);
        dpData: Array<any> = [];
        headerColors: any = [];
        textColors: KnockoutObservableArray<any> = ko.observableArray([]);
        lstDate: KnockoutObservableArray<any> = ko.observableArray([]);
        optionalHeader: Array<any> = [];
        employeeModeHeader: Array<any> = [];
        dateModeHeader: Array<any> = [];
        errorModeHeader: Array<any> = [];
        formatCodes: KnockoutObservableArray<any> = ko.observableArray([]);
        autBussCode: KnockoutObservableArray<any> = ko.observableArray([]);
        lstAttendanceItem: KnockoutObservableArray<any> = ko.observableArray([]);
        //A13_1 コメント
        comment: KnockoutObservable<any> = ko.observable(null);
        employeeModeFixCol: Array<any> = [
            { columnKey: 'id', isFixed: true },
            { columnKey: 'state', isFixed: true },
            { columnKey: 'error', isFixed: true },
            { columnKey: 'date', isFixed: true }
        ];
        dateModeFixCol: Array<any> = [
            { columnKey: 'id', isFixed: true },
            { columnKey: 'state', isFixed: true },
            { columnKey: 'error', isFixed: true },
            { columnKey: 'employeeCode', isFixed: true },
            { columnKey: 'employeeName', isFixed: true },
            { columnKey: 'picture-person', isFixed: true }
        ];
        errorModeFixCol: Array<any> = [
            { columnKey: 'id', isFixed: true },
            { columnKey: 'state', isFixed: true },
            { columnKey: 'error', isFixed: true },
            { columnKey: 'employeeCode', isFixed: true },
            { columnKey: 'employeeName', isFixed: true },
            { columnKey: 'date', isFixed: true },
            { columnKey: 'picture-person', isFixed: true }
        ];
        // date ranger component
        dateRanger: KnockoutObservable<any> = ko.observable(null);

        datePicker: KnockoutObservable<any> = ko.observable(null);
        // date picker component
        selectedDate: KnockoutObservable<any> = ko.observable(null);

        showButton: KnockoutObservable<AuthorityDetailModel> = ko.observable(null);

        referenceVacation: KnockoutObservable<ReferenceVacation> = ko.observable(new ReferenceVacation(false, false, false, false, false, null));

        employmentCode: KnockoutObservable<any> = ko.observable("");

        editValue: KnockoutObservableArray<InfoCellEdit> = ko.observableArray([]);

        itemValueAll: KnockoutObservableArray<any> = ko.observableArray([]);

        itemValueAllTemp: KnockoutObservableArray<any> = ko.observableArray([]);

        lockMessage: KnockoutObservable<any> = ko.observable("");

        dataHoliday: KnockoutObservable<DataHoliday> = ko.observable(new DataHoliday(null, null, null, null, null, null, null, null));
        comboItems: KnockoutObservableArray<any> = ko.observableArray([new ItemModel('1', '基本給'),
            new ItemModel('2', '役職手当'),
            new ItemModel('3', '基本給2')]);

        comboColumns: KnockoutObservableArray<any> = ko.observableArray([{ prop: 'code', length: 1 },
            { prop: 'name', length: 2 }]);
        comboColumnsCalc: KnockoutObservableArray<any> = ko.observableArray([{ prop: 'code', length: 1 },
            { prop: 'name', length: 8 }]);
        comboItemsDoWork: KnockoutObservableArray<any> = ko.observableArray([]);
        comboItemsReason: KnockoutObservableArray<any> = ko.observableArray([]);
        comboItemsCalc: KnockoutObservableArray<any> = ko.observableArray([]);
        comboItemsCompact: KnockoutObservableArray<any> = ko.observableArray([]);
        comboTimeLimit: KnockoutObservableArray<any> = ko.observableArray([]);
        showPrincipal: KnockoutObservable<any> = ko.observable(true);
        showSupervisor: KnockoutObservable<any> = ko.observable(true);
        dataAll: KnockoutObservable<any> = ko.observable(null);
        hasLstHeader: boolean = true;
        dPErrorDto: KnockoutObservable<any> = ko.observable();
        listCareError: KnockoutObservableArray<any> = ko.observableArray([]);
        listCareInputError: KnockoutObservableArray<any> = ko.observableArray([]);
        listCheckHolidays: KnockoutObservableArray<any> = ko.observableArray([]);
        listCheck28: KnockoutObservableArray<any> = ko.observableArray([]);
        listCheckDeviation: any = [];
        listErrorMonth: any = [];
        lstErrorFlex: any = [];
        listErAlHolidays: any = [];
        lstErOldHoliday: any = [];
        hasErrorCalc: boolean = false;

        employIdLogin: any;
        dialogShow: any;
        //contain data share
        screenModeApproval: KnockoutObservable<any> = ko.observable(null);
        changePeriod: KnockoutObservable<any> = ko.observable(true);
        errorReference: KnockoutObservable<any> = ko.observable(true);
        activationSourceRefer: KnockoutObservable<any> = ko.observable(null);
        tighten: KnockoutObservable<any> = ko.observable(null);
        //button A2_6
        showTighProcess: KnockoutObservable<any> = ko.observable(true);
        indentityMonth: KnockoutObservable<IndentityMonth> = ko.observable(null);
        //get object share
        shareObject: KnockoutObservable<ShareObject> = ko.observable(new ShareObject());

        initScreenSPR: any = 0;
        showDateRange: KnockoutObservable<any> = ko.observable(true);

        flexShortage: KnockoutObservable<FlexShortage> = ko.observable(new FlexShortage(null, null, null));
        optionNoOfHolidays: any = {
            width: "70",
            decimallength: 1,
            unitID: 'DAYS'
        }
        calcFlex: KnockoutObservable<CalcFlex> = ko.observable(null);
        showFlex: KnockoutObservable<any> = ko.observable(false);
        breakTimeDay: KnockoutObservable<BreakTimeDay> = ko.observable(null);
        canFlex: KnockoutObservable<any> = ko.observable(false);

        sprStampSourceInfo: KnockoutObservable<any> = ko.observable(null);

        itemMonth: any = [];
        itemValueMonthParent: any = {};
        valueUpdateMonth: any = null;
        valueFlexCheck: any;
        errorBackGroundFlex: boolean = false;

        textStyles: any = [];
        showTextStyle: boolean = true;
        clickFromExtract: boolean = true;

        sprRemoveApprovalAll: any;

        dataSourceLoadOld: any = {};

        lstHeaderReceive: any;

        workTypeNotFound: any = [];

        isVisibleMIGrid: KnockoutObservable<boolean> = ko.observable(false);
        isStartScreen: KnockoutObservable<boolean> = ko.observable(true);
        listAttendanceItemId: KnockoutObservableArray<any> = ko.observableArray([]);
        monthYear: KnockoutObservable<string> = ko.observable(null);

        agreementInfomation: KnockoutObservable<AgreementInfomation> = ko.observable(new AgreementInfomation());
        ntsMControl: any = [];
        hasEmployee: boolean = true;
        hasErrorBuss: boolean = false;

        lstDomainOld: any = [];
        lstDataSourceLoad: any;
        lstDomainEdit: any = [];

        flagCalculation: boolean = false;
        lstErrorAfterCalcUpdate: any = [];

        showLock: KnockoutObservable<boolean> = ko.observable(true);
        unLock: KnockoutObservable<boolean> = ko.observable(false);
        hideLock: KnockoutObservable<boolean> = ko.observable(true);
        lockDisableFlex : KnockoutObservable<boolean> = ko.observable(true);
        periodCheckLock : any = null;

        itemChange: any = [];

        characteristics: Characteristics = {};
        loadFirst: boolean = true;

        clickCounter: CLickCount = new CLickCount();

        itemInputName: any = [];

        periodCdl027: KnockoutObservable<any> = ko.observable({});
        showDialogError: boolean = false;

        transitionDesScreen: boolean = false;

        openedScreenB: boolean = false;

        closureId: any = null;

        lstCellDisByLock: any = [];

        mapIndentityCheck: any = {};
        mapApprovalCheck: any = {};

         //Date YYYYMM picker
        yearMonth: KnockoutObservable<number> = ko.observable(0);
        //Combobox display actual time
        actualTimeOptionDisp: KnockoutObservableArray<any> =  ko.observableArray([]);
        actualTimeSelectedCode: KnockoutObservable<number> = ko.observable(0);
        timePeriodAllInfo: any = {};
        displayFormatOld: number = null;
        initFromScreenOther: boolean = false;
        clickChangeMonth: boolean = true;

        dateReferCCg: KnockoutObservable<any> = ko.observable(0);
        changeConditionExtract:  KnockoutObservable<boolean> = ko.observable(false);
		showWorkLoad: KnockoutObservable<boolean> = ko.observable(false);

        checkUnLock: KnockoutObservable<boolean> = ko.observable(false);

        constructor(dataShare: any) {
            var self = this;

            self.initLegendButton();
            self.initDateRanger();
            self.initDisplayFormat();
            self.headersGrid = self.employeeModeHeader;
            self.fixColGrid = ko.observableArray(self.employeeModeFixCol);
            // show/hide header number
            self.showHeaderNumber.subscribe((val) => {
                if (!self.loadFirst) {
//                    self.characteristics.showNumberHeader = val;
//                    character.save('characterKdw003a', self.characteristics);
                    self.dislayNumberHeaderText();
                }
            });
            // show/hide profile icon
            self.showProfileIcon.subscribe((val) => {
                //if (self.displayFormat() == 1 || self.displayFormat() ==2) {
                if (!self.loadFirst) {
//                    self.characteristics.showProfile = val;
//                    character.save('characterKdw003a', self.characteristics);
                    self.displayProfileIcon(self.displayFormat());
                    // }
                }
            });

            self.displayWhenZero.subscribe(val => {
                //self.displayWhenZero();
                if (!self.loadFirst) {
//                    self.characteristics.showZero = val;
//                    character.save('characterKdw003a', self.characteristics);
                    self.displayNumberZero();
                }
            });

            self.selectedDirection.subscribe((value) => {
                if (!self.loadFirst) {
                    if (value == 0) {
                        $("#dpGrid").mGrid("directEnter", "below");
//                        self.characteristics.moveMouse = 0;
                    } else {
                        $("#dpGrid").mGrid("directEnter", "right");
//                        self.characteristics.moveMouse = 1;
                    }
//                    character.save('characterKdw003a', self.characteristics);
                }
            });

            self.yearMonth.subscribe(value => {
                  if (!self.loadFirst && self.clickChangeMonth && self.displayFormat() == 0) {
                      if (nts.uk.ui.errors.getErrorByElement($("#yearMonthPicker")).length != 0) {
                          return;
                      }
                      let param = {
                          yearMonth: value,
                          empTarget: _.isEmpty(self.selectedEmployee()) ? null : self.selectedEmployee()
                       };
                      self.clickChangeMonth = false;
                      service.genDate(param).done((data) => {
                          if (data) {
                              self.yearMonth(data.yearMonth);
                              //Combobox display actual time
                              self.initActualTime(data);
                              self.timePeriodAllInfo = data;
                              self.clickChangeMonth = true;
                              self.genDateExtract(false);
                              self.dateReferCCg(value);
                              self.dateReferCCg(self.dateRanger().endDate);
                          }else{
                               self.yearMonth(self.timePeriodAllInfo.yearMonth);
                               self.clickChangeMonth = true;
                          }
                          self.changeConditionExtract(true);
                      });
                  }
            });

            self.selectedDate.subscribe(value =>{
                self.dateReferCCg(value);
                if (!self.loadFirst) {
                    self.changeConditionExtract(true);
                }
            });

            self.actualTimeSelectedCode.subscribe(value =>{
                self.changeConditionExtract(true);
                self.closureId = value;
            });

            self.displayFormat.subscribe(value =>{
                self.changeConditionExtract(true);
            });

            self.selectedEmployee.subscribe(value =>{
                if (!self.loadFirst && self.displayFormat() == 0 && !_.isEmpty(value)) {
                     let param = {
                          yearMonth: self.yearMonth(),
                          empTarget: value,
                       };
                      self.clickChangeMonth = false;
                      service.genDate(param).done((data) => {
                          if (data) {
                              self.yearMonth(data.yearMonth);
                              //Combobox display actual time
                              self.initActualTime(data);
                              self.timePeriodAllInfo = data;
                              self.clickChangeMonth = true;
                              self.genDateExtract(false);
                              self.dateReferCCg(value);
                              self.dateReferCCg(self.dateRanger().endDate);
                          }else{
                               self.yearMonth(self.timePeriodAllInfo.yearMonth);
                               self.clickChangeMonth = true;
                          }
                          self.changeConditionExtract(true);
                      });
                }
            });

            //$("#fixed-table").ntsFixedTable({ height: 50, width: 300 });
            $(document).mouseup(function(e) {
                var container = $(".ui-tooltip");
                if (!container.is(e.target) &&
                    container.has(e.target).length === 0) {
                    $("#tooltip").hide();
                }
            });
            if (dataShare != undefined) {
                let checkDataShare: boolean = self.shareObject().mapDataShare(dataShare.initParam, dataShare.extractionParam, dataShare.dataSPR);
                if (checkDataShare) {
                    self.showDateRange(_.isEmpty(self.shareObject().changePeriodAtr) ? true : self.shareObject().changePeriodAtr);
                    self.transitionDesScreen = _.isEmpty(self.shareObject().transitionDesScreen) ? false : true;
                    self.closureId = (self.shareObject().targetClosure != undefined && self.shareObject().targetClosure != null) ? self.shareObject().targetClosure : null;
                } else {
                    self.shareObject(new ShareObject());
                }
            }

            //            self.flexShortage.subscribe((val:any) => {
            //            });
            self.flexShortage.valueHasMutated();
            self.isVisibleMIGrid.subscribe((value) => {
                if (value) {
                    self.getNameMonthly();
                }
            });

            $(".grid-container").attr('style', 'height: ' + (window.innerHeight - 250) + 'px !IMPORTANT');

            $(window).on('resize', function() {
                var win = $(this); //this = window
                $(".grid-container").attr('style', 'height: ' + (win.height() - 250) + 'px !IMPORTANT');
            });

            self.dataHoliday.subscribe(val => {
                if (val.dispCompensationDay || val.dispCompensationTime)
                    $('#fixed-table td:nth-child(1), #fixed-table th:nth-child(1)').show();
                else
                    $('#fixed-table td:nth-child(1), #fixed-table th:nth-child(1)').hide();
                if (val.dispSubstitute)
                    $('#fixed-table td:nth-child(2), #fixed-table th:nth-child(2)').show();
                else
                    $('#fixed-table td:nth-child(2), #fixed-table th:nth-child(2)').hide();
                if (val.dispAnnualDay || val.dispAnnualTime)
                    $('#fixed-table td:nth-child(3), #fixed-table th:nth-child(3)').show();
                else
                    $('#fixed-table td:nth-child(3), #fixed-table th:nth-child(3)').hide();
                if (val.dispReserve)
                    $('#fixed-table td:nth-child(4), #fixed-table th:nth-child(4)').show();
                else
                    $('#fixed-table td:nth-child(4), #fixed-table th:nth-child(4)').hide();

				if (val.dispChildCare)
                    $('#fixed-table td:nth-child(5), #fixed-table th:nth-child(5)').show();
                else
                    $('#fixed-table td:nth-child(5), #fixed-table th:nth-child(5)').hide();

				if (val.dispLongTermCare)
                    $('#fixed-table td:nth-child(6), #fixed-table th:nth-child(6)').show();
                else
                    $('#fixed-table td:nth-child(6), #fixed-table th:nth-child(6)').hide();
            });
            $(window).on('resize', function() {
                var win = $(this); //this = window
                //$(".grid-container").attr('style', 'height: ' + (win.height() -150) + 'px !IMPORTANT');
            });
           nts.uk.characteristics.remove(__viewContext.user.employeeId + __viewContext.user.companyId);
        }

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

        initLegendButton() {
            var self = this;
            self.legendOptions = {
                items: [
                    { colorCode: '#94B7FE', labelText: '手修正（本人）' },
                    { colorCode: '#CEE6FF', labelText: '手修正（他人）' },
                    { colorCode: '#BFEA60', labelText: '申請反映' },
                    { colorCode: '#F69164', labelText: '計算値' },
                    { colorCode: '#FD4D4D', labelText: getText("KDW003_44") },
                    { colorCode: '#F6F636', labelText: getText("KDW003_45") },
                ]
            };
        }

        initDateRanger() {
            var self = this;
            self.dateRanger.subscribe((dateRange) => {
                if (dateRange && dateRange.startDate && dateRange.endDate) {
                    self.dateReferCCg(dateRange.endDate);
                    self.selectedDate(dateRange.endDate);
                    var elementDate = dateRange.startDate;
                    //                    if (moment(elementDate, "YYYY/MM/DD").isValid()) {
                    //                        while (!moment(elementDate, "YYYY/MM/DD").isAfter(moment(dateRange.endDate, "YYYY/MM/DD"))) {
                    //                            self.lstDate.push({ date: elementDate });
                    //                            elementDate = moment(elementDate, "YYYY/MM/DD").add(1, 'd').format("YYYY/MM/DD");
                    //                        }
                    //                    }
                    if (self.displayFormat() == 1) {
                        self.datePicker().startDate = dateRange.startDate;
                        self.datePicker().endDate = dateRange.endDate;
                        self.datePicker.valueHasMutated();
                    }
                     self.changeConditionExtract(true);
                }
            });
            self.datePicker.subscribe((dateRange) => {
                if (dateRange && dateRange.startDate && dateRange.endDate) {
                    //                    setTimeout(function (){
                    self.selectedDate(dateRange.startDate);
                    //                    }, 10);
                }
            });
            self.datePicker({
                startDate: moment().add(-1, "M").add(1, "d").format("YYYY/MM/DD"),
                endDate: moment().format("YYYY/MM/DD")
            });
            self.dateRanger({
                startDate: moment().add(-1, "M").add(1, "d").format("YYYY/MM/DD"),
                endDate: moment().format("YYYY/MM/DD")
            });
        }

        initDisplayFormat() {
            var self = this;
            self.displayFormatOptions = ko.observableArray([
                { code: 0, name: getText("Enum_DisplayFormat_Individual") },
                { code: 1, name: getText("Enum_DisplayFormat_ByDate") },
                { code: 2, name: getText("Enum_DisplayFormat_ErrorAlarm") }
            ]);
            self.displayFormat(0);
        }

        createSumColumn(data: any) {
            var self = this;
            _.each(data.lstControlDisplayItem.columnSettings, function(item) {
                if (self.displayFormat() == 0) {
                    if (item.columnKey == "date") {
                        item['summaryCalculator'] = "合計";
                    }
                } else {
                    if (item.columnKey == "employeeCode") {
                        item.allowSummaries = true;
                        item['summaryCalculator'] = "合計";
                    }
                }
                if (item.typeFormat != null && item.typeFormat != undefined) {
                    if (item.typeFormat == 2) {
                        //so lan
                        item['summaryCalculator'] = "Number";
                    }
                    else if (item.typeFormat == 5) {
                        //thoi gian
                        item['summaryCalculator'] = "Time"
                    }
                    else if (item.typeFormat == 3) {
                        //so tien
                        item['summaryCalculator'] = "Number"
                    } else {
                        if (item['summaryCalculator'] != "合計") item['summaryCalculator'] = ""
                    }
                } else {
                    item['summaryCalculator'] = ""
                }
                delete item.typeFormat;
                self.columnSettings(data.lstControlDisplayItem.columnSettings);
            });
        }

        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            character.restore("characterKdw003a").done((obj: Characteristics) => {
                if (obj != undefined && __viewContext.user.employeeId == obj.employeeId && __viewContext.user.companyId == obj.companyId) {
                    self.characteristics = obj;
                    self.showHeaderNumber(obj.showNumberHeader);
                    self.showProfileIcon(obj.showProfile);
                    self.displayWhenZero(obj.showZero);

                    self.displayFormat(obj.formatExtract);

                    self.formatCodes(obj.authenSelectFormat);

                    self.selectedDirection(obj.moveMouse)
                }

                var param = {
                    dateRange: dateRangeParam ? {
                        startDate: moment(dateRangeParam.startDate).utc().toISOString(),
                        endDate: moment(dateRangeParam.endDate).utc().toISOString()
                    } : null,
                    displayFormat: _.isEmpty(self.shareObject()) ? (_.isEmpty(self.characteristics) ? 0 : self.characteristics.formatExtract) : self.shareObject().displayFormat,
                    initScreen: (_.isEmpty(self.characteristics) || !_.isEmpty(self.shareObject())) ? 0 : 1,
                    mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                    lstEmployee: [],
                    formatCodes: self.formatCodes(),
                    objectShare: _.isEmpty(self.shareObject()) ? null : self.shareObject(),
                    showError: _.isEmpty(self.shareObject()) ? null : self.shareObject().errorRefStartAtr,
                    closureId: self.closureId,
                    initFromScreenOther: self.initFromScreenOther,
                    changeFormat: false
                };
                // delete grid in localStorage
                self.deleteGridInLocalStorage();

                let dateRangeParam = nts.uk.ui.windows.getShared('DateRangeKDW003');
                if (!(_.isEmpty(self.shareObject()))) {
                    self.displayFormat(param.displayFormat);
                    if (self.shareObject().transitionDesScreen == undefined || self.shareObject().transitionDesScreen == null) {
                        $("#back-navigate").css("visibility", "hidden");
                    }
                } else {
                    self.displayFormat(param.displayFormat);
                    $("#back-navigate").css("visibility", "hidden");
                }
                self.hideComponent();
                self.showTextStyle = true;
                nts.uk.ui.block.invisible();
                nts.uk.ui.block.grayout();
                self.hasErrorBuss = false;
                //asyntask
                service.initParam(param).done((data) => {
                    //self.processMapData(data);
                    if (data.lstEmployee == undefined || data.lstEmployee.length == 0 || data.errorInfomation != 0) {
                        let messageId = "Msg_1342";
                        if (data.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) {
                            messageId = "Msg_916";
                            self.hasErrorBuss = true;
                        } else if (data.errorInfomation == DCErrorInfomation.ITEM_HIDE_ALL) {
                            messageId = "Msg_1452";
                            self.hasErrorBuss = true;
                        } else if (data.errorInfomation == DCErrorInfomation.NOT_EMP_IN_HIST) {
                            messageId = "Msg_1543";
                            self.hasErrorBuss = true;
                        }
                        nts.uk.ui.dialog.alert({ messageId: messageId }).then(function() {
                            //self.hasEmployee = false;
                            nts.uk.ui.block.clear();
                            dfd.resolve({ bindDataMap: true, data: data });
                        });
                    } else if (!_.isEmpty(data.errors)) {
                        let errors = [];
                        _.forEach(data.errors, error => {
                            errors.push({
                                message: error.message,
                                messageId: error.messageId,
                                supplements: {}
                            })
                        });
                        nts.uk.ui.dialog.bundledErrors({ errors: errors });
                        self.hasErrorBuss = true;
                        dfd.resolve({ bindDataMap: true, data: data });
                    }
                    else {
                        let paramMonth: any = { loadAfterCalc: false }
                        $.when(service.loadMonth(paramMonth), service.startScreen(param)).done((dataMonth, dataDaily) => {
                            dataDaily.monthResult = dataMonth.monthResult;
                            dataDaily.indentityMonthResult = dataMonth.indentityMonthResult;
                            dataDaily.showTighProcess = dataMonth.showTighProcess;
                            self.lstCellDisByLock = dataDaily.lstCellDisByLock;
                            dfd.resolve({ bindDataMap: true, data: dataDaily });
                        })
                    }
                }).fail(function(error) {
                    if (error.messageId == "Msg_672") {
                        nts.uk.ui.dialog.info({ messageId: "Msg_672" });
                    } else if (error.messageId != undefined && error.messageId != "KDW/003/a") {
                        nts.uk.ui.dialog.alert((error.messageId == "Msg_1430" || error.messageId == "") ? error.message : { messageId: error.messageId }).then(function() {
                            nts.uk.request.jumpToTopPage();
                        });

                    } else if ((error.messageId == undefined && error.errors.length > 0)) {
                        nts.uk.ui.dialog.bundledErrors({ errors: error.errors }).then(function() {
                            nts.uk.request.jumpToTopPage();
                        });
                    } else {
                        setShared("selectedPerfFmtCodeList", "");
                        modal("/view/kdw/003/c/index.xhtml").onClosed(() => {
                            let res = nts.uk.ui.windows.getShared('KDW003C_Err');
                            if(!_.isEmpty(res) && res.jumpToppage){
                                nts.uk.request.jumpToTopPage();
                            }
                            var dataTemp = nts.uk.ui.windows.getShared('KDW003C_Output');
                            if (dataTemp != undefined) {
                                let data = [dataTemp];
								self.formatCodes(data);
                                let param = {
                                    dateRange: dateRangeParam ? {
                                        startDate: moment(dateRangeParam.startDate).utc().toISOString(),
                                        endDate: moment(dateRangeParam.endDate).utc().toISOString()
                                    } : null,
                                    displayFormat: _.isEmpty(self.shareObject()) ? (_.isEmpty(self.characteristics) ? 0 : self.characteristics.formatExtract) : self.shareObject().displayFormat,
                                    initScreen: _.isEmpty(self.characteristics) ? 0 : 1,
                                    mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                                    lstEmployee: [],
                                    formatCodes: data,
                                    objectShare: _.isEmpty(self.shareObject()) ? null : self.shareObject(),
                                    changeFormat: false
                                };
                                self.characteristics.authenSelectFormat = param.formatCodes;
                                self.characteristics.employeeId = __viewContext.user.employeeId;
                                self.characteristics.companyId = __viewContext.user.companyId;
                                self.characteristics.formatExtract = param.displayFormat;
                                character.save('characterKdw003a', self.characteristics);
                                nts.uk.ui.block.invisible();
                                nts.uk.ui.block.grayout();
                                //self.processMapData(data);
                                let paramMonth: any = { loadAfterCalc: false }
                                service.initParam(param).done((data) => {
                                    //self.processMapData(data);
                                    if (data.lstEmployee == undefined || data.lstEmployee.length == 0 || data.errorInfomation != 0) {
                                        let messageId = "Msg_1342";
                                        if (data.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) {
                                            messageId = "Msg_916";
                                            self.hasErrorBuss = true;
                                        } else if (data.errorInfomation == DCErrorInfomation.ITEM_HIDE_ALL) {
                                            messageId = "Msg_1452";
                                            self.hasErrorBuss = true;
                                        } else if (data.errorInfomation == DCErrorInfomation.NOT_EMP_IN_HIST) {
                                            messageId = "Msg_1543";
                                            self.hasErrorBuss = true;
                                        }
                                        nts.uk.ui.dialog.alert({ messageId: messageId }).then(function() {
                                            //self.hasEmployee = false;
                                            nts.uk.ui.block.clear();
                                            dfd.resolve({ bindDataMap: true, data: data });
                                        });
                                    } else if (!_.isEmpty(data.errors)) {
                                        let errors = [];
                                        _.forEach(data.errors, error => {
                                            errors.push({
                                                message: error.message,
                                                messageId: error.messageId,
                                                supplements: {}
                                            })
                                        });
                                        nts.uk.ui.dialog.bundledErrors({ errors: errors });
                                        self.hasErrorBuss = true;
                                        dfd.resolve({ bindDataMap: true, data: data });
                                    }
                                    else {
                                        let paramMonth: any = { loadAfterCalc: false }
                                        $.when(service.loadMonth(paramMonth), service.startScreen(param)).done((dataMonth, dataDaily) => {
                                            dataDaily.monthResult = dataMonth.monthResult;
                                            dataDaily.indentityMonthResult = dataMonth.indentityMonthResult;
                                            dataDaily.showTighProcess = dataMonth.showTighProcess;
                                            self.lstCellDisByLock = dataDaily.lstCellDisByLock;
                                            dfd.resolve({ bindDataMap: true, data: dataDaily });
                                        })
                                    }
                                })
                            }
                        });

                    };
                });
            })
            return dfd.promise();
        }

        processMapData(data) {
            var self = this;
            let startTime: number = performance.now();
            self.closureId = data.closureId;
            //self.lstDomainOld = data.domainOld;
            //self.lstDomainEdit = _.cloneDeep(data.domainOld);
            if (data.typeBussiness != localStorage.getItem('kdw003_type')) {
                localStorage.removeItem(window.location.href + '/dpGrid');
            }
            localStorage.setItem('kdw003_type', data.typeBussiness);
            self.dateRanger().startDate = data.dateRange.startDate;
            self.dateRanger().endDate = data.dateRange.endDate;
            self.dateRanger.valueHasMutated();
            //change doc
               //Date YYYYMM picker
            self.initFromScreenOther = false;
            self.yearMonth(data.periodInfo.yearMonth);
//            self.yearMonth.valueHasMutated();
            //Combobox display actual time
            self.initActualTime(data.periodInfo);
            self.timePeriodAllInfo = data.periodInfo;
            //if(self.displayFormat() == 1) //self.selectedDate(data.dateRange.startDate);
            // pair name input
            self.itemInputName = data.lstControlDisplayItem.itemInputName;
            self.dataAll(data);
            self.itemValueAll(data.itemValues);
            self.comment(data.comment != null ? '■ ' + data.comment : null);
			if(data.lstControlDisplayItem.formatCode !=  null){
           	 	self.formatCodes(data.lstControlDisplayItem.formatCode);
			}
            self.autBussCode(data.autBussCode);
            self.createSumColumn(data);
            // combo box
            self.comboItemsCalc(data.lstControlDisplayItem.comboItemCalc);
            self.comboItemsReason(data.lstControlDisplayItem.comboItemReason);
            self.comboItemsDoWork(data.lstControlDisplayItem.comboItemDoWork);
            self.comboItemsCompact(data.lstControlDisplayItem.comboItemCalcCompact);
            self.comboTimeLimit(data.lstControlDisplayItem.comboTimeLimit);

            self.employmentCode(data.employmentCode);
            self.lstAttendanceItem(data.lstControlDisplayItem.lstAttendanceItem);
            let showCheckbox = _.isEmpty(self.shareObject()) ? data.showPrincipal : data.showSupervisor;
			let confirmEmployment = data.confirmEmployment ? data.confirmEmployment : false;
            self.showButton(new AuthorityDetailModel(data.authorityDto, data.lstControlDisplayItem.settingUnit, showCheckbox, confirmEmployment));
			self.showWorkLoad(data.showWorkLoad);
            self.hideLock(self.showButton().available12());
            self.showLock(true);
            self.unLock(false);
            self.lockDisableFlex(data.lockDisableFlex);
            self.periodCheckLock = data.rangeLock;
            //            self.referenceVacation(new ReferenceVacation(data.yearHolidaySettingDto == null ? false : data.yearHolidaySettingDto.manageAtr, data.substVacationDto == null ? false : data.substVacationDto.manageAtr, data.compensLeaveComDto == null ? false : data.compensLeaveComDto.manageAtr, data.com60HVacationDto == null ? false : data.com60HVacationDto.manageAtr, self.showButton()));
            // Fixed Header
            self.fixHeaders(data.lstFixedHeader);
            self.showPrincipal(data.showPrincipal);
            self.showSupervisor(data.showSupervisor);
            self.lstHeaderReceive = _.cloneDeep(data.lstControlDisplayItem.lstHeader);
            if (data.lstControlDisplayItem.lstHeader.length == 0) self.hasLstHeader = false;
            if (self.showPrincipal() || data.lstControlDisplayItem.lstHeader.length == 0) {
                self.employeeModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[3], self.fixHeaders()[4]];
                self.dateModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6], self.fixHeaders()[4]];
                self.errorModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6], self.fixHeaders()[3], self.fixHeaders()[4]];
            } else {
                self.employeeModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[3]];
                self.dateModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6]];
                self.errorModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6], self.fixHeaders()[3]];
            }
            if (self.showSupervisor()) {
                self.employeeModeHeader.push(self.fixHeaders()[8]);
                self.dateModeHeader.push(self.fixHeaders()[8]);
                self.errorModeHeader.push(self.fixHeaders()[8]);
            }
            self.lstEmployee(_.orderBy(data.lstEmployee, ['code'], ['asc']));
            self.receiveData(data);
            //                let employeeLogin: any = _.find(self.lstEmployee(), function(data){
            //                    return data.loginUser == true;
            //                });
            self.employIdLogin = __viewContext.user.employeeId;
            self.selectedEmployee(_.isEmpty(self.shareObject()) ? self.employIdLogin : (self.shareObject().displayFormat == 0 ? self.shareObject().individualTarget : (self.lstEmployee().length == 0 ? "" : self.lstEmployee()[0].id)));
            self.initCcg001();
            self.loadCcg001();
            self.showTighProcess(data.showTighProcess);
            self.indentityMonth(data.indentityMonthResult);
            if (!self.hasEmployee || data.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) return;
            self.loadKcp009();
            if (!_.isEmpty(data.errors) || data.errorInfomation != 0) {
              self.loadFirst = false;
              return;
            }
            self.extractionData();
            console.log("khoi tao Object: " + (performance.now() - startTime));
            if ((_.isEmpty(data.lstControlDisplayItem.lstHeader)) || data.errorInfomation != 0) {
                let messageId = "Msg_1342";
                if (data.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) {
                    messageId = "Msg_916";
                    self.hasErrorBuss = true;
                } else if (data.errorInfomation == DCErrorInfomation.ITEM_HIDE_ALL) {
                    messageId = "Msg_1452";
                    self.hasErrorBuss = true;
                } else if (data.errorInfomation == DCErrorInfomation.NOT_EMP_IN_HIST) {
                    messageId = "Msg_1543";
                    self.hasErrorBuss = true;
                }
                nts.uk.ui.dialog.alert({ messageId: messageId }).then(function() {
                    //self.hasEmployee = false;
                    nts.uk.ui.block.clear();
                     return ;
                });
            }
            self.loadGrid();
            console.log("thoi gian load grid: " + (performance.now() - startTime));
            //  self.extraction();
            console.log("thoi gian load ccg: " + (performance.now() - startTime));
            // no20
            self.dPErrorDto(data.dperrorDto);
           // self.displayNumberZero();
            self.displayProfileIcon(self.displayFormat());
            self.dislayNumberHeaderText();
            //set hide control approval
            _.forEach(data.lstHideControl, hide =>{
                $("#dpGrid").mGrid("setState", "_"+hide.rowId, hide.columnKey, ["mgrid-hide"]);
            })
            console.log("thoi gian load 0: " + (performance.now() - startTime));
            //set SPR
            if (data.showErrorDialog) {
                self.showDialogError = true;
                self.showErrorDialog();
            }
            self.loadFirst = false;
            //alert("time load ALL: "+ (performance.now() - startTime));
        }

        initActualTime(data) {
            let self = this,
                selectItem = 0;
            if (data.lstRangeCls && data.lstRangeCls.length > 0) {
                self.actualTimeOptionDisp([]);
                for (let i = 0; i < data.lstRangeCls.length; i++) {
                    let startDate = data.lstRangeCls[i].startDate,
                        endDate =  data.lstRangeCls[i].endDate;
                    if(data.targetRange.startDate == startDate){
                       selectItem = data.lstRangeCls[i].closureId;
                    }
                    self.actualTimeOptionDisp.push({ code: data.lstRangeCls[i].closureId, name: (data.lstRangeCls[i].closureId) + ": " + moment(startDate).format("M/D") + "～" + moment(endDate).format("M/D")});
                }
            }
            self.actualTimeSelectedCode(selectItem);
            self.closureId = selectItem;
        };

        loadRemainNumberTable() {
            let self = this;
            let param = {
                employeeId: self.selectedEmployee(),
                closureDate: self.dateRanger().startDate
            }
            service.getRemainNum(param).done((data: any) => {
                self.dataHoliday(new DataHoliday(data.annualLeave, data.reserveLeave, data.compensatoryLeave, data.substitutionLeave, data.nextGrantDate, data.grantDays, data.childCareVacation, data.longTermCareVacation));
                self.referenceVacation(
                    new ReferenceVacation(
                        data.annualLeave == null ? false : data.annualLeave.manageYearOff,
                        data.reserveLeave == null ? false : data.reserveLeave.manageRemainNumber,
                        data.substitutionLeave == null ? false : data.substitutionLeave.manageAtr,
                        data.compensatoryLeave == null ? false : data.compensatoryLeave.manageCompenLeave,
                        data.com60HVacation == null ? false : data.com60HVacation.manageAtr,
                        self.showButton()));
            });
        }

        setSprFromItem(data: any) {
            let self = this;
            if (!_.isEmpty(self.shareObject()) && self.shareObject().initClock != null && self.initScreenSPR == 0) {
                if(!_.isEmpty(data.changeSPR.messageIdError)){
                   nts.uk.ui.dialog.error({ messageId: data.changeSPR.messageIdError});
                    return;
                }
                if (data.showQuestionSPR == SPRCheck.SHOW_CONFIRM) {
                    let sprStamp = { employeeId: "", date: "", change31: false, change34: false };
                    // show dialog confirm
                    nts.uk.ui.dialog.confirm({ messageId: "Msg_1214" }).ifYes(() => {
                        // update check
                        if (data.changeSPR.change31) {
                            //let objectName = {};
                            //                            objectName["A31"] = "" + self.convertMinute(self.shareObject().initClock.goOut);
                            //                            $("#dpGrid").ntsGrid("updateRow", "_" + data.changeSPR.rowId31, objectName);
                            //$("#dpGrid").mGrid("updateCell", "_" + data.changeSPR.rowId31, "A31", self.convertMinute(self.shareObject().initClock.goOut));
                            self.updateCellSpr("_" + data.changeSPR.rowId31, "A31", self.convertMinute(self.shareObject().initClock.goOut), self.shareObject().initClock.canEdit, false);
                            sprStamp.change31 = true;
                        }

                        if (data.changeSPR.change34) {
                            //let objectName = {};
                            //objectName["A34"] = "" + self.convertMinute(self.shareObject().initClock.liveTime);
                            self.updateCellSpr("_" + data.changeSPR.rowId34, "A34", self.convertMinute(self.shareObject().initClock.liveTime), false, self.shareObject().initClock.canEdit);
                            //$("#dpGrid").ntsGrid("updateRow", "_" + data.changeSPR.rowId34, objectName);
                            sprStamp.change34 = true;
                        }

                        if (data.changeSPR.showPrincipal) {
                            //let objectName = {};
                            //objectName["sign"] = false;
                            $("#dpGrid").mGrid("updateCell", "_" + data.changeSPR.rowId31, "sign", false);
                            //$("#dpGrid").ntsGrid("updateRow", "_" + data.changeSPR.rowId31, objectName);
                        }

                        if (data.changeSPR.showSupervisor) {
                            //let objectName = {};
                            //objectName["approval"] = false;
                            // $("#dpGrid").ntsGrid("updateRow", "_" + data.changeSPR.rowId31, objectName);
                            $("#dpGrid").mGrid("updateCell", "_" + data.changeSPR.rowId31, "approval", false);
                            self.sprRemoveApprovalAll = "_" + data.changeSPR.rowId31;
                        }
                        if ((data.changeSPR.change31 || data.changeSPR.change34) && self.shareObject().initClock.canEdit) {
                            self.sprStampSourceInfo(sprStamp);
                        }
                    });
                } else if (data.showQuestionSPR == SPRCheck.INSERT) {
                    let sprStamp = { employeeId: "", date: "", change31: false, change34: false };
                    if (data.changeSPR.change31) {
                        //let objectName = {};
                        //objectName["A31"] = "" + self.convertMinute(self.shareObject().initClock.goOut);
                        //$("#dpGrid").ntsGrid("updateRow", "_" + data.changeSPR.rowId31, objectName);
                        self.updateCellSpr("_" + data.changeSPR.rowId31, "A31", self.convertMinute(self.shareObject().initClock.goOut), self.shareObject().initClock.canEdit, false);
                        sprStamp.change31 = true;
                    }

                    if (data.changeSPR.change34) {
                        //let objectName = {};
                        //objectName["A34"] = "" + self.convertMinute(self.shareObject().initClock.liveTime);
                        //$("#dpGrid").ntsGrid("updateRow", "_" + data.changeSPR.rowId34, objectName);
                        self.updateCellSpr("_" + data.changeSPR.rowId34, "A34", self.convertMinute(self.shareObject().initClock.liveTime), false, self.shareObject().initClock.canEdit);
                        sprStamp.change34 = true;
                    }
                    if ((data.changeSPR.change31 || data.changeSPR.change34) && self.shareObject().initClock.canEdit) {
                        self.sprStampSourceInfo(sprStamp);
                    }
                }
                //update
            }
        }

        updateCellSpr(rowId: any, item: any, value: any, changeSpr31 ?: boolean, changeSpr34 ?: boolean) {
            let self = this;
            let dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            $("#dpGrid").mGrid("updateCell", rowId, item, value, false, true);
            self.inputProcessCommon(rowId, item, value, changeSpr31, changeSpr34).done(value => {
                _.each(value.cellEdits, itemResult => {
                    $("#dpGrid").mGrid("updateCell", itemResult.id, itemResult.item, itemResult.value, true, true);
                })
                nts.uk.ui.block.clear();
                dfd.resolve();
            });
            return dfd.promise();
        }

        convertMinute(value): string {
            return Math.floor(value / 60) + ':' + value % 60;
        }

        processFlex(data, showListError): JQueryPromise<any> {
            let dfd = $.Deferred(),
                self = this;
            if (self.displayFormat() === 0 && !_.isEmpty(self.selectedEmployee())) {
                self.loadRemainNumberTable();
            }
            let monthResult = data.monthResult;
            if (monthResult != null) {
                // set agreementInfo
                self.agreementInfomation().mapDataAgreement(monthResult.agreementInfo);
                let listFormatDaily: any[] = monthResult.formatDaily;
                self.listAttendanceItemId((monthResult.results != null && monthResult.results.length != 0) ? monthResult.results[0].items : []);

                _.each(listFormatDaily, (item) => {
                    let formatDailyItem = _.find(self.listAttendanceItemId(), { 'itemId': item.attendanceItemId });
                    if (formatDailyItem) {
                        formatDailyItem['columnWidth'] = (!_.isNil(item) && !!item.columnWidth) ? item.columnWidth : 100;
                        formatDailyItem['order'] = item.order;
                    }
                });
                let arr: any[] = _.orderBy(self.listAttendanceItemId(), ['order'], ['asc']);
                self.listAttendanceItemId(arr);
                self.monthYear(nts.uk.time.formatYearMonth(data.monthResult.month));
                // reload MiGrid
                // delete localStorage miGrid
                localStorage.removeItem(window.location.href + '/miGrid');
                self.isVisibleMIGrid(data.monthResult.hasItem);
                self.isVisibleMIGrid.valueHasMutated();
                //
            } else {
                self.agreementInfomation().mapDataAgreement({ showAgreement: false });
            }

            if (monthResult != null && monthResult.flexShortage != null && monthResult.flexShortage.showFlex && self.displayFormat() == 0) {
                self.showFlex(true);
                self.canFlex(monthResult.flexShortage.canflex);
                self.breakTimeDay(monthResult.flexShortage.breakTimeDay);
                self.calcFlex(new CalcFlex(monthResult.flexShortage.value18, monthResult.flexShortage.value19, monthResult.flexShortage.value21, monthResult.flexShortage.value189, monthResult.flexShortage.value190, monthResult.flexShortage.value191));
                self.itemMonth = [];
                self.valueFlexCheck = monthResult.flexShortage.calc;
                let fst: FlexShortage = self.flexShortage();

                //fst.parent = ko.observable(self);
                fst.bindDataChange(ko.toJS(self.calcFlex), monthResult.flexShortage.redConditionMessage, monthResult.flexShortage.messageNotForward, monthResult.flexShortage.error, monthResult.flexShortage.messageError, showListError && self.canFlex())
                // set error dialog b
                //if(self.canFlex()) self.lstErrorFlex = data.monthResult.flexShortage.messageError;
                self.itemMonthLayout(data);
                self.itemValueMonthParent = data.monthResult.flexShortage.monthParent;
                self.valueUpdateMonth = self.itemValueMonthParent;
                self.valueUpdateMonth["redConditionMessage"] = monthResult.flexShortage.redConditionMessage;
                //co can kiem tra loi flex ko
                self.valueUpdateMonth["hasFlex"] = true && self.canFlex();
                dfd.resolve();
                nts.uk.ui.block.clear();
                //self.flexShortage(new FlexShortage(self, self.calcFlex(),  self.breakTimeDay()));
                // アルゴリズム「フレックス不足の相殺が実施できるかチェックする」
            } else {
                self.showFlex(false);
                self.lstErrorFlex = [];
                if (self.displayFormat() === 0) {
                    self.itemValueMonthParent = data.monthResult == undefined ? {} : data.monthResult.flexShortage.monthParent;
                    self.valueUpdateMonth = self.itemValueMonthParent;
                    self.valueUpdateMonth["hasFlex"] = false;
                }
                nts.uk.ui.block.clear();
                dfd.resolve();
            }
            if (self.displayFormat() === 0) self.valueUpdateMonth["needCallCalc"] = (data.monthResult == null || data.monthResult == undefined) ? false : data.monthResult.needCallCalc;
            return dfd.promise();
        }

        itemMonthLayout(data: any) {
            let self = this;
            self.itemMonth = [];
            //self.itemMonth.push(data.flexShortage.value18);
            self.itemMonth.push(data.monthResult.flexShortage.value189);
            self.itemMonth.push(data.monthResult.flexShortage.value190);
        }
        receiveData(data) {
            var self = this;
            self.dpData = data.lstData;
            self.cellStates(data.lstCellState);
            self.optionalHeader = data.lstControlDisplayItem.lstHeader;
            self.sheetsGrid(data.lstControlDisplayItem.lstSheet);
            self.sheetsGrid.valueHasMutated();
            if (self.showTextStyle || self.clickFromExtract) {
                self.textStyles = data.textStyles;
            }
        }

        proceed() {
            var self = this;
            this.insertUpdate();
        }

        insertUpdate(confirmMonth ?: boolean): JQueryPromise<any> {
            var self = this;
            let dfd = $.Deferred();
            if (!self.hasEmployee || self.hasErrorBuss) {
                dfd.resolve();
                return;
            }
            if (self.dialogShow != undefined && self.dialogShow.$dialog != null) {
                self.dialogShow.close();
            }
            // check error calc
//            if (self.flagCalculation && self.hasErrorCalc) {
//                self.showErrorDialog()
//                dfd.resolve();
//                return
//            }

//            if (self.workTypeNotFound.length > 0) {
//                self.showErrorDialog();
//                dfd.resolve();
//                return;
//            }
            // insert flex
            if (self.hasErrorGrid()) {
                self.showErrorDialog();
                dfd.resolve();
                return;
            }

            //let errorGrid: any = $("#dpGrid").mGrid("errors");
            let checkDataCare: boolean = true;
            //if (errorGrid == undefined || errorGrid.length == 0) {
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            self.listErAlHolidays = [];
            if (!self.flagCalculation) {
                self.listCareError([]);
                self.listCareInputError([]);
                self.listCheck28([]);
                self.listCheckDeviation = [];
                self.listErrorMonth = [];
                self.lstErrorAfterCalcUpdate = [];
            }
            self.listCheckHolidays([]);
            let dataChange: any = _.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual);

            var dataSource = $("#dpGrid").mGrid("dataSource");
            let dataChangeProcess: any = [];
            let dataCheckSign: any = [];
            let dataCheckApproval: any = [];
            let sprStampSourceInfo: any = null;
            _.each(dataChange, (data: any) => {
                let dataTemp = _.find(dataSource, (item: any) => {
                    return item.id == data.rowId;
                });

                if (data.columnKey != "sign" && data.columnKey != "approval") {
                    if (data.columnKey.indexOf("Code") == -1 && data.columnKey.indexOf("NO") == -1) {
                        if (data.columnKey.indexOf("Name") != -1) {
                        } else {

                            // check itemCare
//                            let groupCare = self.checkItemCare(Number(data.columnKey.substring(1, data.columnKey.length)));
//                            if (groupCare == 0 || groupCare == 1 || groupCare == 2) {
//                                if (self.checkErrorData(groupCare, data, dataSource) == false || self.listCareInputError().length > 0) {
//                                    checkDataCare = false;
//                                }
//                            }
                            //get layout , and type
                            let layoutAndType: any = _.find(self.itemValueAll(), (item: any) => {
                                return item.itemId == data.columnKey.substring(1, data.columnKey.length);
                            });
                            let item = _.find(self.lstAttendanceItem(), (value) => {
                                return String(value.id) === data.columnKey.substring(1, data.columnKey.length);
                            })
                            let value: any;
                            value = self.getPrimitiveValue(data.value, item.attendanceAtr, item.primitive);
							if (value === true || value === false) {
								value = value ? 1 : 0
							}
                            let dataMap = new InfoCellEdit(data.rowId, data.columnKey.substring(1, data.columnKey.length), value, layoutAndType == undefined ? "" : layoutAndType.valueType, layoutAndType == undefined ? "" : layoutAndType.layoutCode, dataTemp.employeeId, dataTemp.dateDetail.utc().toISOString(), 0);
                            dataChangeProcess.push(dataMap);
                        }
                    } else {
                        let columnKey: any;
                        let item: any;
                        if (data.columnKey.indexOf("Code") != -1) {
                            columnKey = data.columnKey.substring(4, data.columnKey.length);
                        } else {
                            columnKey = data.columnKey.substring(2, data.columnKey.length);
                        }
                        //TO Thanh: move find logic out if condition
                        item = _.find(self.lstAttendanceItem(), (data) => {
                            return String(data.id) === columnKey;
                        })

                        let layoutAndType: any = _.find(self.itemValueAll(), (item: any) => {
                            return item.itemId == columnKey;
                        });
                        let dataMap = new InfoCellEdit(data.rowId, columnKey, String(data.value), layoutAndType.valueType, layoutAndType.layoutCode, dataTemp.employeeId, dataTemp.dateDetail.utc().toISOString(), item.typeGroup);
                        dataChangeProcess.push(dataMap);
                    }
                } else {
                    if (data.columnKey == "sign") {
                        dataCheckSign.push({ rowId: data.rowId, itemId: "sign", value: data.value, employeeId: dataTemp.employeeId, date: dataTemp.dateDetail.utc().toISOString(), flagRemoveAll: false });
                    } else {
                        let flag = false;
                        if (data.rowId == self.sprRemoveApprovalAll) {
                            flag = true;
                            self.sprRemoveApprovalAll = null;
                        }
                        dataCheckApproval.push({ rowId: data.rowId, itemId: "approval", value: data.value, employeeId: dataTemp.employeeId, date: dataTemp.dateDetail.utc().toISOString(), flagRemoveAll: flag });
                    }
                }
            });
            if (!_.isEmpty(self.shareObject()) && self.shareObject().initClock != null && self.initScreenSPR == 0) {
                if (self.sprStampSourceInfo() != null) {
                    sprStampSourceInfo = self.sprStampSourceInfo();
                    sprStampSourceInfo.employeeId = self.shareObject().initClock.employeeId;
                    sprStampSourceInfo.date = self.shareObject().initClock.dateSpr.utc().toISOString();
                }
            }
            let dataParent = {
                itemValues: dataChangeProcess,
                dataCheckSign: dataCheckSign,
                dataCheckApproval: dataCheckApproval,
                mode: self.displayFormat(),
                spr: sprStampSourceInfo,
                // dailyOlds: self.lstDomainOld,
                //dailyEdits: self.lstDomainEdit,
                flagCalculation: self.flagCalculation,
                lstNotFoundWorkType: self.workTypeNotFound,
                showDialogError: self.showDialogError
            }
            if (self.displayFormat() == 0) {
                if (!_.isEmpty(self.shareObject()) && self.shareObject().initClock != null) {
                    dataParent["employeeId"] = self.shareObject().initClock.employeeId;
                } else {
                    dataParent["employeeId"] = dataSource.length > 0 ? dataSource[0].employeeId : null;
                }
                dataParent["monthValue"] = self.valueUpdateMonth;
                dataParent["dateRange"] = dataSource.length > 0 ? { startDate: dataSource[0].dateDetail, endDate: dataSource[dataSource.length - 1].dateDetail } : null;
            } else {
                dataParent["dateRange"] = dataSource.length > 0 ? { startDate: dataSource[0].dateDetail, endDate: dataSource[0].dateDetail } : null;
            }

            let checkDailyChange = (dataChangeProcess.length > 0 || dataCheckSign.length > 0 || dataCheckApproval.length > 0 || self.sprStampSourceInfo() != null) && checkDataCare;
            dataParent["checkDailyChange"] = (dataChangeProcess.length > 0 || self.sprStampSourceInfo()) ? true : false;
            dataParent["showFlex"] = self.showFlex();
            dataParent["checkUnLock"] = self.checkUnLock();
            if (checkDailyChange || (self.valueUpdateMonth != null && !_.isEmpty(self.valueUpdateMonth.items)) || self.flagCalculation || !_.isEmpty(sprStampSourceInfo)) {
                service.addAndUpdate(dataParent).done((dataAfter) => {
                    // alert("done");
                    let onlyCheckBox: boolean = false;
                    if(dataAfter.onlyLoadCheckBox == true){
                        onlyCheckBox = true;
                        self.mapApprovalCheck = dataAfter.mapApprovalCheck;
                        self.mapIndentityCheck = dataAfter.mapIndentityCheck;
                        self.canFlex(dataAfter.canFlex);
                        if(!self.errorBackGroundFlex) self.lstErrorFlex = [];
                        if (self.errorBackGroundFlex && self.canFlex()) {
                            $("#next-month").attr('style', 'background-color: red !important');
                        } else {
                            $("#next-month").attr('style', 'background-color: white !important');
                        }
                        self.lstErOldHoliday  =  dataAfter.lstErOldHoliday;
                    }
                    let errorNoReload: boolean  = false;
                    //dataChange = {};
                    let errorFlex = false;
                    if (!_.isEmpty(dataAfter.flexShortage)) {
                        if (dataAfter.flexShortage.error && dataAfter.flexShortage.messageError.length != 0) {
                            $("#next-month").ntsError("clear");
                            self.lstErrorFlex = [];
                            _.each(dataAfter.flexShortage.messageError, value => {
                                //$("#next-month").ntsError("set", value.message, value.messageId);
                                self.lstErrorFlex.push({employeeId:"", message: value.message});
                            });
                            if(self.canFlex()) {
                                $("#next-month").attr('style', 'background-color: red !important');
                                 errorFlex = true;
                                 errorNoReload = true;
                                 self.flexShortage().binDataChangeError(dataAfter.flexShortage.dataCalc);
                            }

                        } else {
                            $("#next-month").attr('style', 'background-color: white !important');
                            $("#next-month").ntsError("clear");
                            self.lstErrorFlex = [];
                        }
                        //nts.uk.ui.block.clear();
                    }else if((onlyCheckBox && !self.errorBackGroundFlex) || (!onlyCheckBox)){
                         self.lstErrorFlex = [];
                    }

                  // list row data error
                    self.lstErrorAfterCalcUpdate;
                    _.each(dataAfter.errorMap, (value, key) => {
                        if (key != "6") {
                            if (key != "5") {
                                _.each(value, errorResult => {
                                    // if error after calc
                                    if (_.isEmpty(errorResult.rowId)) {
                                        errorResult.rowId = _.map(_.filter(dataSource, tempSource => {
                                            return tempSource.employeeId == errorResult.employeeId && tempSource.dateDetail._i == errorResult.date;
                                        }), temp2 => { return temp2.id })
                                    }
                                    let dataTemp1 = _.map(_.filter(dataChange, temp => {
                                        return temp.rowId == errorResult.rowId;
                                    }), temp2 => {return temp2.rowId})

                                    if (!_.isEmpty(dataTemp1)) self.lstErrorAfterCalcUpdate.push.apply(self.lstErrorAfterCalcUpdate, dataTemp1);
                                })
                            } else {
                                let empId = _.map(value, item2 => {
                                    return item2.employeeId;
                                })
                            }
                            self.lstErrorAfterCalcUpdate.push.apply(self.lstErrorAfterCalcUpdate, (_.map(_.filter(dataSource, (v) => _.includes(empId, v.employeeId)), temp => { return temp.id })));
                        }

                    });
                 self.lstErrorAfterCalcUpdate = _.uniqBy(self.lstErrorAfterCalcUpdate, temp => {return temp});
                   if ((_.isEmpty(dataAfter.errorMap) && dataAfter.errorMap[5] == undefined)) {
                        if (self.valueUpdateMonth != null || self.valueUpdateMonth != undefined) {
                            self.valueUpdateMonth.items = [];
                        }
                        self.initScreenSPR = 1;
                        self.clickFromExtract = false;
                        self.showTextStyle = false;
                        if (!self.flagCalculation) {
                            if (checkDailyChange) {
                                // self.reloadScreen();
                                self.loadRowScreen(false, false, onlyCheckBox, errorFlex).done(() =>{
                                    nts.uk.ui.block.clear();
                                    if (!_.isEmpty(dataAfter.messageAlert) && dataAfter.messageAlert == "Msg_15" && _.isEmpty(confirmMonth)) {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                            if (dataAfter.showErrorDialog && dataAfter.errorMap[6] == undefined) self.showErrorDialog();
                                        });
                                    }
                                    if (dataAfter.errorMap[6] != undefined) {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_1455" }).then(() => {
                                            if (dataAfter.showErrorDialog) self.showErrorDialog();
                                        });
                                    }
                                });
                            } else {
                                self.loadRowScreen(true, false, onlyCheckBox, errorFlex).done(() =>{
                                    nts.uk.ui.block.clear();
                                    if (!_.isEmpty(dataAfter.messageAlert) && dataAfter.messageAlert == "Msg_15" && _.isEmpty(confirmMonth)) {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                            if (dataAfter.showErrorDialog && dataAfter.errorMap[6] == undefined) self.showErrorDialog();
                                        });
                                    }
                                    if (dataAfter.errorMap[6] != undefined) {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_1455" }).then(() => {
                                            if (dataAfter.showErrorDialog) self.showErrorDialog();
                                        });
                                    }
                                });
                                //nts.uk.ui.block.clear();
                            }
                        } else {
                            self.loadRowScreen(false, true, onlyCheckBox, errorFlex).done(() =>{
                                nts.uk.ui.block.clear();
                                if (!_.isEmpty(dataAfter.messageAlert) && dataAfter.messageAlert == "Msg_15" && _.isEmpty(confirmMonth)) {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                            if (dataAfter.showErrorDialog && dataAfter.errorMap[6] == undefined) self.showErrorDialog();
                                        });
                                }
                                if (dataAfter.errorMap[6] != undefined) {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_1455" }).then(() => {
                                        if (dataAfter.showErrorDialog) self.showErrorDialog();
                                    });
                                }
                            });
                        }

//                        if ((dataAfter.showErrorDialog == null && self.showDialogError) || dataAfter.showErrorDialog) {
//                            self.showDialogError = true;
//                            self.showErrorDialog(dataAfter.messageAlert);
//                        } else
                        if(errorFlex|| (self.flagCalculation && self.hasErrorCalc)){
                            self.showErrorDialog(dataAfter.messageAlert).done(() => {
                                if (dataAfter.showErrorDialog) self.showErrorDialog();
                            });
                        }
//                       else {
//                            //self.showDialogError = false;
//                            if (dataAfter.showErrorDialog) self.showErrorDialog();
//                        }
                       // nts.uk.ui.block.clear();
                    } else {
                        let errorAll = false,
                            errorReleaseCheckbox = false, errorMonth = false;
                        errorNoReload = true;
                        if (dataAfter.errorMap[6] != undefined) {
                            errorReleaseCheckbox = true;
                        }

                        if (dataAfter.errorMap[0] != undefined) {
                            self.listCareError(dataAfter.errorMap[0])
                            // nts.uk.ui.dialog.alertError({ messageId: "Msg_996" })
                            errorAll = true;
                        }
                        if (dataAfter.errorMap[1] != undefined) {
                            self.listCareInputError(dataAfter.errorMap[1])
                            // nts.uk.ui.dialog.alertError({ messageId: "Msg_1108" })
                            errorAll = true;
                        }
                        if (dataAfter.errorMap[2] != undefined) {
                            //self.listCheckHolidays(dataAfter.errorMap[2]);
                            self.listErAlHolidays = dataAfter.errorMap[2];
                            if (self.valueUpdateMonth != null || self.valueUpdateMonth != undefined) {
                                self.valueUpdateMonth.items = [];
                            }
                            self.initScreenSPR = 1;
                            self.clickFromExtract = false;
                            self.showTextStyle = false;
                            //errorAll = false;
                            errorNoReload = false;
                        }

                        if (dataAfter.errorMap[3] != undefined) {
                            self.listCheck28(dataAfter.errorMap[3]);
                            errorAll = true;
                        }

                        if (dataAfter.errorMap[4] != undefined) {
                            self.listCheckDeviation = dataAfter.errorMap[4];
                            errorAll = true;
                        }

                        if (dataAfter.errorMap[5] != undefined) {
                            self.listErrorMonth = dataAfter.errorMap[5];
                            errorMonth = true;
                            errorAll = true;
                        }

                        if (dataAfter.errorMap[7] != undefined) {
                            errorAll = true;
                        }

                        self.loadRowScreen(false, self.flagCalculation, onlyCheckBox, errorFlex || errorMonth).done(() => {
                            nts.uk.ui.block.clear();
                            if (!_.isEmpty(dataAfter.messageAlert) && dataAfter.messageAlert == "Msg_15" && _.isEmpty(confirmMonth)) {
                                if (errorReleaseCheckbox) {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_1455" }).then(() => {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                            if (dataAfter.showErrorDialog) self.showErrorDialog();
                                        });
                                    });
                                } else {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                        if (dataAfter.showErrorDialog) self.showErrorDialog();
                                    });
                                }
                            } else {
                                let errorShowMessage = (errorAll || errorFlex || self.hasErrorCalc);
                                if (errorShowMessage && errorReleaseCheckbox) {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_1455" }).then(() => {
                                        self.showErrorDialog(dataAfter.messageAlert).done(() => {
                                            if (dataAfter.showErrorDialog) self.showErrorDialog();
                                        });
                                    });
                                } else if (errorShowMessage) {
                                    self.showErrorDialog(dataAfter.messageAlert).done(() => {
                                        if (dataAfter.showErrorDialog) self.showErrorDialog();
                                    });
                                } else if (errorReleaseCheckbox) {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_1455" }).then(() => {
                                    });
                                } else {
                                    if (dataAfter.showErrorDialog) self.showErrorDialog();
                                }
                            }
                            self.hasErrorCalc = false;
                        });
                    }
                    dfd.resolve(errorNoReload);
                }).fail((data) => {
                    self.lstErrorFlex = [];
                    nts.uk.ui.block.clear();
                    if(data.optimisticLock === true){
                        nts.uk.ui.dialog.error({ messageId: 'Msg_1528' }).then(() => {
                            self.reloadScreen();
                        });
                    } else {
                        nts.uk.ui.dialog.alert(data.message);
                        dfd.resolve(true);
                    }
                });
            } else {
                nts.uk.ui.block.clear();
                if (!checkDataCare) {
                    // nts.uk.ui.dialog.alertError({ messageId: "Msg_996" })
                    self.showErrorDialog();
                }
                dfd.resolve(!_.isEmpty(self.lstErrorFlex));
            }
            return dfd.promise();
            //    }
        }

        btnCalculation_Click() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            self.calculate(false);
        }

        btnReCalculation_Click() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            //update All
            self.calculate(true);
        }

        calculate(updateAll: boolean) {
            var self = this;
            if (self.dialogShow != undefined && self.dialogShow.$dialog != null) {
                self.dialogShow.close();
            }
//            if (self.workTypeNotFound.length > 0) {
//                self.showErrorDialog();
//                return;
//            }
            // insert flex
            if (self.hasErrorGrid()) {
                self.showErrorDialog();
                return;
            }
            // let errorGrid: any = $("#dpGrid").mGrid("errors");
            let checkDataCare: boolean = true;
            //            if (errorGrid == undefined || errorGrid.length == 0) {
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            self.listCareError([]);
            self.listCareInputError([]);
            self.listCheckHolidays([]);
            self.listCheck28([]);
            self.listCheckDeviation = [];
            self.listErrorMonth = [];
            self.lstErrorFlex = [];
            let dataChange: any = _.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual);
            var dataSource = $("#dpGrid").mGrid("dataSource");
            if (_.isEmpty(dataSource)) {
                nts.uk.ui.block.clear();
                return;
            }
            let dataChangeProcess: any = [];
            let dataCheckSign: any = [];
            let dataCheckApproval: any = [];
            let sprStampSourceInfo: any = null;
            _.each(dataChange, (data: any) => {
                let dataTemp = _.find(dataSource, (item: any) => {
                    return item.id == data.rowId;
                });
                if (data.columnKey != "sign" && data.columnKey != "approval") {
                    if (data.columnKey.indexOf("Code") == -1 && data.columnKey.indexOf("NO") == -1) {
                        if (data.columnKey.indexOf("Name") != -1) {
                        } else {
                            // check itemCare
//                            let groupCare = self.checkItemCare(Number(data.columnKey.substring(1, data.columnKey.length)));
//                            if (groupCare == 0 || groupCare == 1 || groupCare == 2) {
//                                if (self.checkErrorData(groupCare, data, dataSource) == false || self.listCareInputError().length > 0) {
//                                    checkDataCare = false;
//                                }
//                            }
                            //get layout , and type
                            let layoutAndType: any = _.find(self.itemValueAll(), (item: any) => {
                                return item.itemId == data.columnKey.substring(1, data.columnKey.length);
                            });
                            let item = _.find(self.lstAttendanceItem(), (value) => {
                                return String(value.id) === data.columnKey.substring(1, data.columnKey.length);
                            })
                            let value: any;
                            value = self.getPrimitiveValue(data.value, item.attendanceAtr, item.primitive);
                            let dataMap = new InfoCellEdit(data.rowId, data.columnKey.substring(1, data.columnKey.length), value, layoutAndType == undefined ? "" : layoutAndType.valueType, layoutAndType == undefined ? "" : layoutAndType.layoutCode, dataTemp.employeeId, dataTemp.dateDetail.utc().toISOString(), 0, data.columnKey);
                            dataChangeProcess.push(dataMap);
                        }
                    } else {
                        let columnKey: any;
                        let item: any;
                        if (data.columnKey.indexOf("Code") != -1) {
                            columnKey = data.columnKey.substring(4, data.columnKey.length);
                        } else {
                            columnKey = data.columnKey.substring(2, data.columnKey.length);
                        }
                        item = _.find(self.lstAttendanceItem(), (data) => {
                            return String(data.id) === columnKey;
                        })

                        let layoutAndType: any = _.find(self.itemValueAll(), (item: any) => {
                            return item.itemId == columnKey;
                        });
                        let dataMap = new InfoCellEdit(data.rowId, columnKey, String(data.value), layoutAndType.valueType, layoutAndType.layoutCode, dataTemp.employeeId, dataTemp.dateDetail.utc().toISOString(), item.typeGroup, data.columnKey);
                        dataChangeProcess.push(dataMap);
                    }
                }
            });
            // get cell Edit
            let dataChageUI = updateAll ? [] : _.map(_.filter(dataChange, row => {
                return row.columnKey != "sign" && row.columnKey != "approval" && row.columnKey.indexOf("Name") == -1;
            }), allValue => {
                let itemTemp, keyIdRow;
                keyIdRow = Number(allValue.columnKey.replace(/\D/g, ""));
                return new CellEdit(allValue.rowId, keyIdRow)
            });

            //if (!_.isEmpty(self.lstDomainOld)) {
            let dataParent = {
                itemValues: dataChangeProcess,
                dataCheckSign: dataCheckSign,
                dataCheckApproval: dataCheckApproval,
                mode: self.displayFormat(),
                spr: sprStampSourceInfo,
                //dailyOlds: self.lstDomainOld,
                //dailyEdits: self.lstDomainEdit,
                flagCalculation: updateAll,
                cellEdits: dataChageUI,
                lstNotFoundWorkType: self.workTypeNotFound,
                showDialogError: self.showDialogError
            }
            if (self.displayFormat() == 0) {
                if (!_.isEmpty(self.shareObject()) && self.shareObject().initClock != null) {
                    dataParent["employeeId"] = self.shareObject().initClock.employeeId;
                    dataParent["dateRange"] = { startDate: self.shareObject().initClock.dateSpr.utc(), endDate: self.shareObject().initClock.dateSpr.utc() };
                } else {
                    dataParent["employeeId"] = dataSource.length > 0 ? dataSource[0].employeeId : null;
                    dataParent["dateRange"] = dataSource.length > 0 ? { startDate: dataSource[0].dateDetail, endDate: dataSource[dataSource.length - 1].dateDetail } : null;
                }
                dataParent["monthValue"] = self.valueUpdateMonth;
            }else{
                 dataParent["dateRange"] = {
                    startDate: moment(self.dateRanger().startDate).toISOString(),
                    endDate: moment(self.dateRanger().endDate).toISOString()
                }
            }

            let checkDailyChange = (dataChangeProcess.length > 0 || dataCheckSign.length > 0 || dataCheckApproval.length > 0 || self.sprStampSourceInfo() != null) && checkDataCare;
            dataParent["checkDailyChange"] = (dataChangeProcess.length > 0 || self.sprStampSourceInfo()) ? true : false;
            dataParent["showFlex"] = self.showFlex();
            dataParent["checkUnLock"] = self.checkUnLock();
            //self.removeErrorRefer();
            let dfd = $.Deferred();
            service.calculation(dataParent).done((data) => {
                if(data.dailyCorrectDto){
                      self.processFlex(data.dailyCorrectDto, true);
                }
                self.flagCalculation = data.flagCalculation;
                if (data.resultError != null && !_.isEmpty(data.resultError.flexShortage)) {
                    if (data.resultError.flexShortage.error && data.resultError.flexShortage.messageError.length != 0) {
                        $("#next-month").ntsError("clear");
                        _.each(data.resultError.flexShortage.messageError, value => {
                            //$("#next-month").ntsError("set", value.message, value.messageId);
                            self.lstErrorFlex.push({employeeId: "", message: value.message});
                        });
                       // self.showErrorDialog();
                    } else {
                        $("#next-month").ntsError("clear");
                        self.lstErrorFlex = [];
                    }
                }
                //if (_.isEmpty(data.resultError) || _.isEmpty(data.resultError.errorMap)) {
                    self.hasErrorCalc = false;
                    //self.lstDomainEdit = data.calculatedRows;
                    let lstValue = data.resultValues;
                    _.forEach(self.dpData, row => {
                        let cellDatas = row.cellDatas;
                        let rrow = _.find(lstValue, (r: any) => { return row.employeeId == r.employeeId && r.date == row.date });
                        _.forEach(cellDatas, cell => {
                            if (cell.columnKey != 'Code623' && cell.columnKey != 'Code625' && !_.isEmpty(rrow)) {
                                // cell chinh sua man hinh
                                let editedCell = _.find(dataChangeProcess, (item: any) => { return (item.rowId.indexOf(row.id) >= 0 && item.columnKey == cell.columnKey); });
                                // cell sau tinh toan
                                let editedCell2 = _.find(self.cellStates(), (item: any) => { return (item.rowId.indexOf(row.id) >= 0 && item.columnKey == cell.columnKey); });
                                if ((editedCell == null
                                    && (editedCell2 == null || (!editedCell2.state.contains("mgrid-manual-edit-other") && !editedCell2.state.contains("mgrid-manual-edit-target"))))
                                    || updateAll) {
                                    let itemId = self.getItemIdFromColumnKey(cell.columnKey);
                                    let itemValue = _.find(rrow.items, (i: any) => { return i.itemId == itemId });
                                    if (itemValue) {
                                        let itemCalcTemp = _.find(data.lstCellStateCalc, itemCalc => {
                                            return itemCalc.rowId == "_" + row.id && itemCalc.columnKey == cell.columnKey;
                                        })
                                        if (itemCalcTemp != undefined) {
                                            $("#dpGrid").mGrid("updateCell", "_" + row.id, cell.columnKey, itemValue.value == null ? "" : itemValue.value, true, true);
                                        } else {
                                            if (editedCell && updateAll) {
                                                $("#dpGrid").mGrid("updateCell", "_" + row.id, cell.columnKey, itemValue.value == null ? "" : itemValue.value, false, true);
                                            } else {
                                                $("#dpGrid").mGrid("updateCell", "_" + row.id, cell.columnKey, itemValue.value == null ? "" : itemValue.value, true, true);
                                            }
                                        }
                                    }
                                    // $("#dpGrid").mGrid("setState", "_" + row.id, cell.columnKey, ["mgrid-calc"]);
                                }
                            }
                        });
                    });

                    _.each(data.lstCellStateCalc, (valt) => {
                        console.log("column key:" + valt.columnKey);
                        $("#dpGrid").mGrid("setState", valt.rowId, valt.columnKey, valt.state);
                    });

                     if (!self.displayWhenZero()) {
                       // $("#dpGrid").mGrid("hideZero", true)
                        $("#dpGrid").mGrid("hideZero", false)
                        $("#dpGrid").mGrid("hideZero", true)
                    } else {
                        //$("#dpGrid").mGrid("hideZero", false)
                        $("#dpGrid").mGrid("hideZero", true)
                        $("#dpGrid").mGrid("hideZero", false)
                    }
                    nts.uk.ui.block.clear();
                //} else {
                    nts.uk.ui.block.clear();
                    let hasError: boolean = false;
                    if (data.resultError.errorMap[0] != undefined) {
                        self.listCareError(data.resultError.errorMap[0])
                        // nts.uk.ui.dialog.alertError({ messageId: "Msg_996" })
                        hasError = true;
                    }
                    if (data.resultError.errorMap[1] != undefined) {
                        self.listCareInputError(data.resultError.errorMap[1])
                        // nts.uk.ui.dialog.alertError({ messageId: "Msg_1108" })
                         hasError = true;
                    }
                    if (data.resultError.errorMap[2] != undefined) {
                        self.listCheckHolidays(data.resultError.errorMap[2]);
                        self.loadRowScreen(false, true);
                    }

                    if (data.resultError.errorMap[3] != undefined) {
                        self.listCheck28(data.resultError.errorMap[3]);
                        hasError = true;
                    }

                    if (data.resultError.errorMap[4] != undefined) {
                        self.listCheckDeviation = data.resultError.errorMap[4];
                        hasError = true;
                    }

                    if (data.resultError.errorMap[7] != undefined) {
                        hasError = true;
                    }
                    if (data.resultError != null && data.resultError.errorMap[5] != null) {
                        self.listErrorMonth = data.resultError.errorMap[5];
                        hasError = true;
                    }

                    // list row data error
                    self.lstErrorAfterCalcUpdate = [];
                    _.each(data.resultError.errorMap, (value, key) => {
                        if (key != "6") {
                            if (key != "5") {
                                _.each(value, errorResult => {
                                    let dataTemp1 = _.map(_.filter(dataChange, temp => {
                                        return temp.rowId == errorResult.rowId;
                                    }), temp2 => { return temp2.rowId })

                                    if (!_.isEmpty(dataTemp1)) self.lstErrorAfterCalcUpdate.push.apply(self.lstErrorAfterCalcUpdate, dataTemp1);
                                })
                            } else {
                                let empId = _.map(value, item2 => {
                                    return item2.employeeId;
                                })
                            }
                            self.lstErrorAfterCalcUpdate.push.apply(self.lstErrorAfterCalcUpdate, (_.map(_.filter(dataSource, (v) => _.includes(empId, v.employeeId)), temp => { return temp.id })));
                        }

                    });
                    self.lstErrorAfterCalcUpdate = _.uniqBy(self.lstErrorAfterCalcUpdate, temp => { return temp });

                    self.hasErrorCalc = hasError;
                    if (!_.isEmpty(data.resultError.messageAlert) && data.resultError.messageAlert == "Msg_1491") {
                        nts.uk.ui.dialog.info({ messageId: "Msg_1491" });
                    } else {
                        if (hasError) self.showErrorDialog(data.resultError.messageAlert);
                    }
                dfd.resolve();
            }).fail((data) => {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alert(data.message);
                dfd.resolve();
            });
            dfd.promise();
            //                } else
            //                    nts.uk.ui.block.clear();
            //            }
        }

        getItemIdFromColumnKey(columnKey: string): string {
            let itemId: string = "";
            if (columnKey != "sign" && columnKey != "approval") {
                if (columnKey.indexOf("Code") == -1 && columnKey.indexOf("NO") == -1) {
                    if (columnKey.indexOf("Name") != -1) {
                        itemId = "";
                    } else {
                        itemId = columnKey.substring(1, columnKey.length);
                    }
                } else {
                    if (columnKey.indexOf("Code") != -1) {
                        itemId = columnKey.substring(4, columnKey.length);
                    } else {
                        itemId = columnKey.substring(2, columnKey.length);
                    }
                }
            }
            return itemId;
        }

        loadRowScreen(onlyLoadMonth: boolean, onlyCalc: boolean, onlyCheckBox ?: boolean, errorFlex ?: boolean) : JQueryPromise<any> {
            var self = this;
            let dfd = $.Deferred();
            let lstEmployee = [];
            self.sprStampSourceInfo(null);
            if (self.displayFormat() === 0) {
                let lst = _.find(self.lstEmployee(), (employee) => {
                    return employee.id === self.selectedEmployee();
                });
                if (lst != undefined) lstEmployee.push(lst);
            } else {
                lstEmployee = self.lstEmployee();
            }

            let dataSource = $("#dpGrid").mGrid("dataSource");

            let dataChange: any = _.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual);
            let rowIdsTemp = _.uniqBy(dataChange, function(e) {
                return e.rowId;
            });

            //if (onlyCalc) {
                rowIdsTemp = _.map(_.uniqBy(dataSource, function(e) {
                    return e.id;
                }), mapRow => {
                    return { rowId: mapRow.id }
                });
            //}

            let rowIds = _.map(_.cloneDeep(rowIdsTemp), (value) => {
                return value.rowId.substring(1, value.rowId.length);
            });
            let lstData = _.map(_.sortBy(_.filter(self.dailyPerfomanceData(), (v) => _.includes(rowIds, v.id)), (sort) => {
                return new Date(sort.date);
            }), (map) => {
                map.date = moment(map.date).format('YYYY-MM-DD') + 'T00:00:00.000Z';
                map.state = "";
                map.error = "";
                map.sign = false;
                map.approval = false;
                map.typeGroup = "";
                return map;
            });

            // only load checkBox
                if (onlyCheckBox === true) {
                        let paramVer = { lstDataChange: {}, dateRange: {},  displayFormat: 0 }, lstDataChange = [];
                        let modeApprovalOrNormal = _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode;
                        let dataChangeApproval: any = _.filter(dataChange, temp => {
                            return temp.columnKey == "approval"
                        }),
                            dataChangeSign: any = _.filter(dataChange, temp => {
                                return temp.columnKey == "sign"
                            });
                      setTimeout(() => {
                        // xu ly sign
                        _.forEach(dataChangeSign, temp => {
                            let findRow: any = _.find(dataSource, temp2 => {
                                return temp.rowId == temp2.id;
                            });
                            //                        if (!lstDataChange.includes({ employeeId: findRow.employeeId, date: findRow.dateDetail})){
                            //                             lstDataChange.push({ employeeId: findRow.employeeId, date: findRow.dateDetail});
                            //                        }
                            let checkBoxSignChange = temp.value;

                            let checkBoxResult = _.find(self.mapIndentityCheck, (value) => { return value.employeeId == findRow.employeeId && findRow.dateDetail._i == value.date });
                            if (!checkBoxSignChange) {
                                // da check va bo check
                                _.forEach(findRow, (value2, key2) => {
                                    if ((key2.indexOf("A") != -1 || key2.indexOf("NO") != -1
                                        || key2.indexOf("Name") != -1 || key2.indexOf("Code") != -1) && key2.indexOf("Application") == -1
                                        && !(_.find(self.lstCellDisByLock, tempp => { return tempp.rowId == findRow.id && tempp.columnKey == key2 }) != undefined)) {
                                        //enable
                                        $("#dpGrid").mGrid("enableNtsControlAt", findRow.id, key2);
                                    }
                                })
                                $("#dpGrid").mGrid("updateCell", findRow.id, "sign", false, true, true)
                                let stateLock = "";
                                if (findRow.state != "lock|S" && findRow.state != "") {
                                    stateLock = findRow.state.replace("|S", '');
                                }
                                $("#dpGrid").mGrid("updateCell", findRow.id, "state", stateLock, true, true)
                            } else {
                                // ko check -> check
                                if (checkBoxResult) {
                                    //check ok
                                    if (modeApprovalOrNormal == 0 && self.showLock()) {
                                        _.forEach(findRow, (value2, key2) => {
                                            if ((key2.indexOf("A") != -1 || key2.indexOf("NO") != -1
                                                || key2.indexOf("Name") != -1 || key2.indexOf("Code") != -1) && key2.indexOf("Application") == -1) {
                                                //disable
                                                $("#dpGrid").mGrid("disableNtsControlAt", findRow.id, key2);
                                            }
                                        })
                                    }
                                    $("#dpGrid").mGrid("updateCell", findRow.id, "sign", true, true, true);
                                    let stateLock = findRow.state;
                                    if (findRow.state != "" && findRow.state != "lock|S") {
                                        stateLock += "|S";
                                    } else {
                                        stateLock = "lock|S";
                                    }
                                    if (self.showLock()) $("#dpGrid").mGrid("updateCell", findRow.id, "state", stateLock, true, true)
                                } else {
                                    // khong check duoc
                                    $("#dpGrid").mGrid("updateCell", findRow.id, "sign", false, true, true)
                                }
                            }
                        });

                        //xu ly approval
                        _.forEach(dataChangeApproval, temp => {
                            let findRow: any = _.find(dataSource, temp2 => {
                                return temp.rowId == temp2.id;
                            }),

                                checkBoxAppChange = temp.value,

                                checkBoxResult = _.find(self.mapApprovalCheck, (value) => { return value.employeeId == findRow.employeeId && findRow.dateDetail._i == value.date });

//                            if (!lstDataChange.includes({ employeeId: findRow.employeeId, date: findRow.dateDetail })) {
//                                lstDataChange.push({ employeeId: findRow.employeeId, date: findRow.dateDetail });
//                            }
                            if (!checkBoxAppChange) {
                                // da check va bo check
                                _.forEach(findRow, (value2, key2) => {
                                    if ((key2.indexOf("A") != -1 || key2.indexOf("NO") != -1
                                        || key2.indexOf("Name") != -1 || key2.indexOf("Code") != -1) && key2.indexOf("Application") == -1
                                        && !(_.find(self.lstCellDisByLock, tempp => { return tempp.rowId == findRow.id && tempp.columnKey == key2 }) != undefined)) {
                                        //enable
                                        $("#dpGrid").mGrid("enableNtsControlAt", findRow.id, key2);
                                    }
                                })
                                $("#dpGrid").mGrid("updateCell", findRow.id, "approval", false, true, true);
                                let stateLock = "";
                                if (findRow.state != "lock|A" && findRow.state != "") {
                                    stateLock = findRow.state.replace("|A", '');
                                }
                                $("#dpGrid").mGrid("updateCell", findRow.id, "state", stateLock, true, true);
                            } else {
                                // ko check -> check
                                if (checkBoxResult) {
                                    //check ok
                                    if (self.showLock()) {
                                        _.forEach(findRow, (value2, key2) => {
                                            if ((key2.indexOf("A") != -1 || key2.indexOf("NO") != -1
                                                || key2.indexOf("Name") != -1 || key2.indexOf("Code") != -1) && key2.indexOf("Application") == -1) {
                                                //disable
                                                $("#dpGrid").mGrid("disableNtsControlAt", findRow.id, key2);
                                            }
                                        })
                                    }
                                    $("#dpGrid").mGrid("updateCell", findRow.id, "approval", true, true, true);
                                    if (self.showLock()) {
                                        let stateLock = findRow.state;
                                        if (findRow.state != "" && findRow.state != "|A") {
                                            stateLock += "|A";
                                        } else {
                                            stateLock = "lock|A";
                                        }
                                        $("#dpGrid").mGrid("updateCell", findRow.id, "state", stateLock, true, true);
                                    }
                                } else {
                                    // khong check duoc
                                    $("#dpGrid").mGrid("updateCell", findRow.id, "approval", false, true, true);
                                }
                            }
                        });
                    }, 100);

                    _.forEach(dataChangeSign, temp => {
                        let findRow: any = _.find(dataSource, temp2 => {
                            return temp.rowId == temp2.id;
                        });
                        if (!_.includes(lstDataChange, lstTemp => {return lstTemp.employeeId == findRow.employeeId && lstTemp.date == findRow.dateDetail })) {
                            lstDataChange.push({ employeeId: findRow.employeeId, date: findRow.dateDetail });
                        }
                    })
                    _.forEach(dataChangeApproval, temp => {
                        let findRow: any = _.find(dataSource, temp2 => {
                            return temp.rowId == temp2.id;
                        });
                        if (!_.includes(lstDataChange, lstTemp => {return lstTemp.employeeId == findRow.employeeId && lstTemp.date == findRow.dateDetail })) {
                            lstDataChange.push({ employeeId: findRow.employeeId, date: findRow.dateDetail });
                        }
                    })

                    //set ERROR holiday
                    //_.forEach(self.listErAlHolidays,
                    //error holiday old
                    _.each(self.lstErOldHoliday, valueTemp => {
                        let dataCon = _.find(dataSource, (item: any) => {
                            return item.employeeId == valueTemp.employeeId && item.dateDetail._i == valueTemp.date;
                        })
                            stateErrAl = (valueTemp.errorCode == ErrorAlarmClassification.ERROR ? "mgrid-error" : "mgrid-alarm"),
                            typeErrAl = (valueTemp.errorCode == ErrorAlarmClassification.ERROR ? "ER" : "AL");
                        if (valueTemp.onlyErrorHoliday) {
                            $("#dpGrid").mGrid("clearState", dataCon.id, "Code28", [stateErrAl]);
                            $("#dpGrid").mGrid("clearState", dataCon.id, "Name28", [stateErrAl]);
                        }
                        if (valueTemp.itemId == 0 && dataCon.error == typeErrAl) {
                            $("#dpGrid").mGrid("updateCell", dataCon.id, "error", "", true, true);
                        } else if (valueTemp.itemId == 0 && dataCon.error != typeErrAl) {
                            $("#dpGrid").mGrid("updateCell", dataCon.id, "error", valueTemp.layoutCode == ErrorAlarmClassification.ERROR ? "AL" : "ER", true, true);
                        }
                    });
                    // update error holiday new
                    _.each(self.listErAlHolidays, (valueTemp: any) => {
                        let dataCon = _.find(dataSource, (item: any) => {
                            return item.employeeId == valueTemp.employeeId && item.dateDetail._i == valueTemp.date;
                        });
                        let typeErrAl = (valueTemp.layoutCode == ErrorAlarmClassification.ERROR ? "ER" : "AL");
                        let typeErrorGrid = dataCon.error;
                        $("#dpGrid").mGrid("updateCell", dataCon.id, "error", self.appendTextError(typeErrorGrid, typeErrAl), true, true);
                        $("#dpGrid").mGrid("setState", dataCon.id, "Code28", [typeErrAl == "ER" ? "mgrid-error" : "mgrid-alarm"]);
                        $("#dpGrid").mGrid("setState", dataCon.id, "Name28", [typeErrAl == "ER" ? "mgrid-error" : "mgrid-alarm"]);
                    });

                    paramVer.lstDataChange = lstDataChange;
                    paramVer.dateRange = {
                        startDate: lstData[0].date,
                        endDate: lstData[lstData.length - 1].date
                    };
                    paramVer.displayFormat = self.displayFormat();
                    service.loadVerRow(paramVer).done((data) => {
                        self.indentityMonth(data.indentityMonthResult);
                        self.flagCalculation = false;
                        dfd.resolve();
                    });
                    return dfd.promise();
                }

            // get cell Edit
            let dataChageUI = _.map(_.filter(dataChange, row => {
                return row.columnKey != "sign" && row.columnKey != "approval" && row.columnKey.indexOf("Name") == -1;
            }), allValue => {
                let itemTemp, keyIdRow;
                keyIdRow = Number(allValue.columnKey.replace(/\D/g, ""));
                return new CellEdit(allValue.rowId, keyIdRow)
            });

            let param = {
                lstAttendanceItem: self.lstAttendanceItem(),
                lstEmployee: lstEmployee,
                dateRange: {
                    startDate: onlyLoadMonth ? null : lstData[0].date,
                    endDate: onlyLoadMonth ? null : lstData[lstData.length - 1].date
                },
                mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                displayFormat: self.displayFormat(),
                lstData: lstData,
                lstHeader: self.lstHeaderReceive,
                autBussCode: self.autBussCode(),
                dateMonth: moment(self.dateRanger().endDate).utc().toISOString(),
                onlyLoadMonth: onlyLoadMonth,
                //dailys: self.lstDomainEdit,
                dateExtract: {
                    startDate: moment(self.dateRanger().startDate).toISOString(),
                    endDate: moment(self.dateRanger().endDate).toISOString()
                },
                identityProcess: self.dataAll().identityProcessDto,
                showLock: self.showLock(),
                cellEdits: dataChageUI
            }

            service.loadRow(param).done((data) => {
                self.flagCalculation = false;
                if (onlyLoadMonth && errorFlex == false) {
                    self.processFlex(data, true);
                    nts.uk.ui.block.clear();
                    return dfd.resolve();
                }else if(onlyLoadMonth && errorFlex == true){
                     return dfd.resolve();
                }
                self.showTighProcess(data.showTighProcess);
                self.indentityMonth(data.indentityMonthResult);
                //self.lstDomainEdit = data.domainOld;
                //self.lstDomainOld = _.cloneDeep(data.domainOld);
                if(errorFlex == false) self.processFlex(data, true);
                let dataSourceRow, dataSource, dataSourceNew, dataRowTemp = [];
                dataSourceRow = _.cloneDeep(self.formatDate(data.lstData));
                _.forEach(dataSourceRow, (valueUpate) => {
                     if(!_.includes(self.lstErrorAfterCalcUpdate, valueUpate.id)) {
                        _.each(valueUpate, (value, key) => {
                             $("#dpGrid").mGrid("updateCell", valueUpate.id, key, value, true, true)
                         });
                    }
                  })

                setTimeout(() => {
                    rowIdsTemp = _.filter(rowIdsTemp, (v) => !_.includes(self.lstErrorAfterCalcUpdate, v.rowId));
                    data.lstCellState = _.filter(data.lstCellState, (v) => !_.includes(self.lstErrorAfterCalcUpdate, v.rowId));
                    data.lstCellStateCalc = _.filter(data.lstCellStateCalc, (v) => !_.includes(self.lstErrorAfterCalcUpdate, v.rowId));
                    let lstRowReload = _.map(rowIdsTemp, (value) => {
                        return value.rowId;
                    })
                    $("#dpGrid").mGrid("clearState", lstRowReload);
                    _.forEach(lstRowReload, tmp => {
                       $("#dpGrid").mGrid("clearState", tmp, "approval", ["mgrid-hide"]);
                    });

                    _.forEach(data.lstHideControl, hide => {
                        $("#dpGrid").mGrid("setState", "_" + hide.rowId, hide.columnKey, ["mgrid-hide"]);
                    })
                    _.each(data.lstCellState, (valt) => {
                        $("#dpGrid").mGrid("setState", valt.rowId, valt.columnKey, valt.state);
                    });

                    _.each(data.lstCellStateCalc, (valt) => {
                        console.log("column key:" + valt.columnKey);
                        $("#dpGrid").mGrid("setState", valt.rowId, valt.columnKey, valt.state);
                    });

                    if (!self.displayWhenZero()) {
                        //$("#dpGrid").mGrid("hideZero", true)
//                        $("#dpGrid").mGrid("hideZero", false)
//                        $("#dpGrid").mGrid("hideZero", true)
                    } else {
                        //$("#dpGrid").mGrid("hideZero", false)
//                        $("#dpGrid").mGrid("hideZero", true)
//                        $("#dpGrid").mGrid("hideZero", false)
                    }
                 dfd.resolve();
                }, 100);
                //nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }

        appendTextError(textOld: string, textNew: string): any {
            if(_.isEmpty(textNew)) return textOld;
            if(_.isEmpty(textOld)) return textNew;
            if(textOld.indexOf(textNew) != -1){
               return textOld;
            }else{
               return "ER/AL"
            }
        }

        getNameMonthly(): JQueryPromise<any> {
            let dfd = $.Deferred(), self = this, arrItemId: string[] = [];
            if (self.listAttendanceItemId().length <= 0) {
                dfd.resolve();
                return;
            }

            _.each(self.listAttendanceItemId(), (attendanceItemId) => {
                arrItemId.push(attendanceItemId.itemId);
            });

            service.getNameMonthlyAttItem(arrItemId).done(data => {
                if (!self.isStartScreen()) {
                    // neu dang khoi dong man hinh thi k dc destroy
                    $('#miGrid').igGrid("destroy");
                    self.isStartScreen(false);
                }
                self.loadMIGrid(data);
                dfd.resolve();
            }).fail(() => {
                dfd.reject();
            });
            return dfd.promise();
        }

        checkIsColumn(dataCell: any, key: any): boolean {
            let check = false;
            _.each(dataCell, (item: any) => {
                if (item.columnKey.indexOf("NO" + key) != -1) {
                    check = true;
                    return;
                }
            });
            return check;
        }

        getPrimitiveValue(value: any, atr: any, primitive?: any): string {
            var self = this;
            let valueResult: string = "";
            if (atr != undefined && atr != null) {
                if (atr == 6) {
                    // Time
                    valueResult = value == "" ? null : String(self.getHoursAll(value));
                } else if (atr == 5) {
                    valueResult = value == "" ? null : String(self.getHoursTime(value));
				} else if (atr == 3) {
					if(primitive==54) {
						valueResult = _.isEmpty(value) ? null : value;		
					}  else {
						valueResult = value;	
					}
                } else {
                    valueResult = value;
                }
            } else {
                valueResult = value;
            }
            return valueResult;
        }

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
        }

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
        }

        convertToHours(value: any): string {
            let self = this;
            let hours = value < 0 ? "-" + String(Math.floor(Math.abs(value / 60))) : String(Math.floor(value / 60));
            let minutes = String(Math.abs(value) % 60);
            if (Number(minutes) < 10) minutes = "0" + minutes;
            return hours + ":" + minutes;
        }

        //check data item in care and childCare
        // child care = 0 , care = 1, other = 2;
        checkItemCare(itemId: any): number {
            if (itemId == 759 || itemId == 760 || itemId == 761 || itemId == 762) {
                return 0;
            } else if (itemId == 763 || itemId == 764 || itemId == 765 || itemId == 766) {
                return 1;
            } else {
                return 2;
            }
        }

        // check data error group care , child care
        checkErrorData(group: number, data: any, dataSource: any): boolean {
            var self = this;
            data["itemId"] = data.columnKey.substring(1, data.columnKey.length);
            let rowItemSelect: any = _.find(dataSource, function(value: any) {
                return value.id == data.rowId;
            });
            self.checkInputCare(data, rowItemSelect);
            if (group != 2) {
                if (data.value != "") {
                    if (group == 0) {
                        if ((rowItemSelect.A763 != undefined && rowItemSelect.A763 != "") || (rowItemSelect.A763 != undefined && rowItemSelect.A763 != "")
                            || (rowItemSelect.A763 != undefined && rowItemSelect.A763 != "") || (rowItemSelect.A763 != undefined && rowItemSelect.A763 != "")) {
                            // alert Error
                            self.listCareError.push(data);
                            return false;
                            // nts.uk.ui.dialog.alertError({ messageId: "Msg_996" })
                        }

                    } else if (group == 1) {
                        if ((rowItemSelect.A759 != undefined && rowItemSelect.A759 != "") || (rowItemSelect.A760 != undefined && rowItemSelect.A760 != "")
                            || (rowItemSelect.A761 != undefined && rowItemSelect.A761 != "") || (rowItemSelect.A762 != undefined && rowItemSelect.A762 != "")) {
                            // alert Error
                            self.listCareError.push(data);
                            return false;
                            //nts.uk.ui.dialog.alertError({ messageId: "Msg_996" })
                        }
                    }
                }
            }
            return true;
        }

        checkInputCare(data: any, rowItemSelect: any) {
            var self = this;
            if (CHECK_INPUT[data.itemId] != undefined
                && ((self.isNNUE(rowItemSelect["A" + CHECK_INPUT[data.itemId]]) && data.value == "")
                    || (data.value != "" && !self.isNNUE(rowItemSelect["A" + CHECK_INPUT[data.itemId]])))) {
                data["itemId"] = data.columnKey.substring(1, data.columnKey.length);
                data["group"] = Number(CHECK_INPUT[data.itemId]);
                self.listCareInputError.push(data);
            }
        }

        isNNUE(value: any): boolean {
            if (value != "") return true;
            else return false;
        }

        hideComponent() {
            var self = this;
            if (self.displayFormat() == 0) {
                self.displayFormatOld = 0;
                $("#emp-component").css("display", "block");
                $("#daterangepicker").css("display", "block");
                $("#cbListDate").css("display", "none");
                $("#daterangepickererror").css("display", "none");
                $("#btnVacationRemaining").show();
                $('#numberHoliday').show();
                $('#fixed-table').show();
                $('#content-all-grid').attr('style', 'top: -5px !IMPORTANT ; position: relative; clear: both');
                $('#content-grid-not').attr('style', 'margin-top: -3px !IMPORTANT');
                $('#date-extract').attr('style', 'padding-top: 0px; height: 42px');
                //  $("#content-grid").attr('style', 'top: 244px !IMPORTANT');
            } else if (self.displayFormat() == 1) {
                self.displayFormatOld = 1;
                $("#daterangepicker").css("display", "none");
                $("#daterangepickererror").css("display", "none");
                $("#cbListDate").css("display", "block");
                $("#emp-component").css("display", "none");
                $("#btnVacationRemaining").hide();
                $('#numberHoliday').hide();
                $('#fixed-table').hide();
                $('#flex').hide();
                $('#content-all-grid').attr('style', 'top: -12px !IMPORTANT ; position: relative; clear: both');
                // $("#content-grid").attr('style', 'top: 225px !IMPORTANT');
                $('#content-grid-not').attr('style', 'margin-top: -45px !IMPORTANT');
                $('#date-extract').attr('style', 'margin-top: -10px; height: 42px');
            } else {
                self.displayFormatOld = 2;
                $("#daterangepickererror").css("display", "block");
                $("#daterangepicker").css("display", "none");
                $("#cbListDate").css("display", "none");
                $("#emp-component").css("display", "none");
                $("#btnVacationRemaining").hide();
                $('#numberHoliday').hide();
                $('#fixed-table').hide();
                $('#flex').hide();
                $('#content-all-grid').attr('style', 'top: -12px !IMPORTANT ; position: relative; clear: both');
                $('#content-grid-not').attr('style', 'margin-top: -45px !IMPORTANT');
                // $("#content-grid").attr('style', 'top: 180px !IMPORTANT');
                $('#date-extract').attr('style', 'margin-top: -10px; height: 42px');
            }
        }

        reloadScreen() {
            var self = this;
            console.log(self.dailyPerfomanceData());
            // if (!nts.uk.ui.errors.hasError()) {
            nts.uk.ui.errors.clearAll();
            self.removeErrorRefer();
            let hasChangeFormat: boolean = (self.displayFormatOld == self.displayFormat()) ? false : true;
            $.when(self.findEmployee(hasChangeFormat)).done(data => {
                let lstEmployee = data;
//                if (self.displayFormat() === 1) {
//                    if (self.datePicker().startDate !== self.dateRanger().startDate &&
//                        self.datePicker().endDate !== self.dateRanger().endDate) {
//                        self.datePicker().startDate = self.dateRanger().startDate;
//                        self.datePicker().endDate = self.dateRanger().endDate;
//                        self.datePicker.valueHasMutated();
//                    }
//                }
                if(!self.initFromScreenOther) self.genDateExtract(hasChangeFormat);

                let dateRangeTemp: any = null;
                if((self.hasEmployee && !hasChangeFormat) || self.initFromScreenOther){
                    dateRangeTemp = {
                        startDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().startDate).utc().toISOString(),
                        endDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().endDate).utc().toISOString()
                    }
                }else if((self.hasEmployee && hasChangeFormat)){
                     dateRangeTemp = {
                        startDate: moment(self.dateRanger().startDate).utc().toISOString(),
                        endDate: moment(self.dateRanger().endDate).utc().toISOString()
                    }
                }
                let param = {
                    dateRange: dateRangeTemp,
                    displayFormat: self.hasEmployee ? self.displayFormat() : _.isEmpty(self.shareObject()) ? 0 : self.shareObject().displayFormat,
                    initScreen: self.hasEmployee ? 1 : 0,
                    mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                    lstEmployee: lstEmployee,
                    formatCodes: self.formatCodes()==null?[]:self.formatCodes(),
                    objectShare: null,
                    showLock: self.showLock(),
                    closureId: self.closureId,
                    initFromScreenOther: self.initFromScreenOther,
                    changeFormat: hasChangeFormat
                };
                self.characteristics.formatExtract = param.displayFormat;
                character.save('characterKdw003a', self.characteristics);
                nts.uk.ui.block.invisible();
                nts.uk.ui.block.grayout();
                self.hideComponent();
                service.initParam(param).done((dataInit) => {
                    if (!_.isEmpty(dataInit.errors)) {
                        let errors = [];
                        _.forEach(dataInit.errors, error => {
                            errors.push({
                                message: error.message,
                                messageId: error.messageId,
                                supplements: {}
                            })
                        });
                        self.hasErrorBuss = true;
                        nts.uk.ui.dialog.bundledErrors({ errors: errors });
                        self.destroyGrid();
                        nts.uk.ui.block.clear();
                        return;
                    } else {
                        self.hasErrorBuss = false;
                    }

                    if ((_.isEmpty(dataInit.lstEmployee) && _.isEmpty(dataInit.lstControlDisplayItem.lstHeader)) || dataInit.errorInfomation != 0) {
                        let messageId = "";
                        if (dataInit.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) {
                            messageId = "Msg_916";
                            self.hasErrorBuss = true;
                        } else if (dataInit.errorInfomation == DCErrorInfomation.ITEM_HIDE_ALL) {
                            messageId = "Msg_1452";
                            self.hasErrorBuss = true;
                        } else if (dataInit.errorInfomation == DCErrorInfomation.NOT_EMP_IN_HIST){
                            messageId = "Msg_1543";
                            self.hasErrorBuss = true;
                        }
                        self.destroyGrid();
                        self.processFlex(dataInit, true);

                        if (messageId != "") nts.uk.ui.dialog.alert({ messageId: messageId }).then(function() {
                            return;
                        });
                        nts.uk.ui.block.clear();
                    } else {
                        let paramMonth: any = {loadAfterCalc: false}
                        $.when(service.loadMonth(paramMonth), service.startScreen(param)).done((dataMonth, data) => {
                            //update mobile
                            if((hasChangeFormat && self.displayFormat() === 0) || self.initFromScreenOther){
                                self.yearMonth(data.periodInfo.yearMonth);
                                //Combobox display actual time
                                self.initActualTime(data.periodInfo);
                                self.timePeriodAllInfo = data.periodInfo;
                            }else if(hasChangeFormat && self.displayFormat() === 1 || self.initFromScreenOther){
                                self.selectedDate(data.periodInfo.targetRange.startDate);
                            }else if(hasChangeFormat && self.displayFormat() === 2 || self.initFromScreenOther){
                                 self.dateRanger({ startDate: data.periodInfo.targetRange.startDate, endDate: data.periodInfo.targetRange.endDate });
                            }
                            self.loadFirst = false;
                            self.initFromScreenOther = false;
                            data.monthResult = dataMonth.monthResult;
                            data.indentityMonthResult = dataMonth.indentityMonthResult;
                            data.showTighProcess = dataMonth.showTighProcess;
                            self.lockDisableFlex(data.lockDisableFlex);
                            self.periodCheckLock = data.rangeLock;
                            self.lstCellDisByLock = data.lstCellDisByLock;
                            self.closureId = data.closureId;
                            self.initScreenSPR = 1;
                            self.hasErrorBuss = false;
                            //self.lstDomainOld = data.domainOld;
                            //self.lstDomainEdit = _.cloneDeep(data.domainOld);
                            if (data.typeBussiness != localStorage.getItem('kdw003_type')) {
                                localStorage.removeItem(window.location.href + '/dpGrid');
                            }
                            localStorage.setItem('kdw003_type', data.typeBussiness);
                            self.dataAll(data);
                            self.itemInputName = data.lstControlDisplayItem.itemInputName;
                            self.formatCodes(data.lstControlDisplayItem.formatCode);
                            self.autBussCode(data.autBussCode);
                            self.lstAttendanceItem(data.lstControlDisplayItem.lstAttendanceItem);
                            self.itemValueAll(data.itemValues);
                            self.createSumColumn(data);
                            self.columnSettings(data.lstControlDisplayItem.columnSettings);
                            self.showPrincipal(data.showPrincipal);
                            self.showSupervisor(data.showSupervisor);
                            self.showTighProcess(data.showTighProcess);
                            self.indentityMonth(data.indentityMonthResult);
                            self.lstHeaderReceive = _.cloneDeep(data.lstControlDisplayItem.lstHeader);

                            // combo box
                            self.comboItemsCalc(data.lstControlDisplayItem.comboItemCalc);
                            self.comboItemsReason(data.lstControlDisplayItem.comboItemReason);
                            self.comboItemsDoWork(data.lstControlDisplayItem.comboItemDoWork);
                            self.comboItemsCompact(data.lstControlDisplayItem.comboItemCalcCompact);
                            self.comboTimeLimit(data.lstControlDisplayItem.comboTimeLimit);
                            //self.showLock(self.showButton().available12());
                            //self.unLock(false);
                            if (data.lstControlDisplayItem.lstHeader.length == 0) {
                                self.hasLstHeader = false;
                            } else {
                                self.hasLstHeader = true;
                            }

                            if (self.showPrincipal() || data.lstControlDisplayItem.lstHeader.length == 0) {
                                self.employeeModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[3], self.fixHeaders()[4]];
                                self.dateModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6], self.fixHeaders()[4]];
                                self.errorModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6], self.fixHeaders()[3], self.fixHeaders()[4]];
                            } else {
                                self.employeeModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[3]];
                                self.dateModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6]];
                                self.errorModeHeader = [self.fixHeaders()[0], self.fixHeaders()[1], self.fixHeaders()[2], self.fixHeaders()[5], self.fixHeaders()[6], self.fixHeaders()[3]];
                            }
                            if (self.showSupervisor()) {
                                self.employeeModeHeader.push(self.fixHeaders()[8]);
                                self.dateModeHeader.push(self.fixHeaders()[8]);
                                self.errorModeHeader.push(self.fixHeaders()[8]);
                            }
                            self.receiveData(data);
                            self.extraction();
                            // no20
                            self.dPErrorDto(data.dperrorDto);
                            // flex
                            self.processFlex(data, true);
                            self.displayNumberZero();
                            //self.displayProfileIcon(self.displayFormat());
                            self.dislayNumberHeaderText();
                            // load hide Checkbox approval
                            _.forEach(data.lstHideControl, hide => {
                                $("#dpGrid").mGrid("setState", "_" + hide.rowId, hide.columnKey, ["mgrid-hide"]);
                            })
                            //check visable MIGrid
                            if (self.displayFormat() != 0) {
                                self.isVisibleMIGrid(false);
                            }
                            if (!self.hasEmployee) {
                                self.loadKcp009();
                                self.hasEmployee = true;
                            }
                            if (data.showErrorDialog) {
                                self.showDialogError = true;
                                self.showErrorDialog();
                            } else {
                                self.showDialogError = false;
                            }
                            setTimeout(() => {
                                 self.changeConditionExtract(false);
                            }, 500);
                            nts.uk.ui.block.clear();
                        });
                    }
                }).fail(function(error) {
                    if (error.messageId == "Msg_672") {
                        nts.uk.ui.dialog.info({ messageId: "Msg_672" })
                    } else {
                        if (error.messageId != undefined && error.messageId != "KDW/003/a") {
                            nts.uk.ui.dialog.alert(error.messageId == "Msg_1430" ? error.message : { messageId: error.messageId }).then(function() {
                                nts.uk.request.jumpToTopPage();
                            });

                        } else if ((error.messageId == undefined && error.errors.length > 0)) {
                            nts.uk.ui.dialog.bundledErrors({ errors: error.errors }).then(function() {
                                nts.uk.request.jumpToTopPage();
                            });

                        } else {
                        setShared("selectedPerfFmtCodeList", "");
                        modal("/view/kdw/003/c/index.xhtml").onClosed(() => {
                            let res = nts.uk.ui.windows.getShared('KDW003C_Err');
                            if(!_.isEmpty(res) && res.jumpToppage){
                                nts.uk.request.jumpToTopPage();
                            }
                            var dataTemp = nts.uk.ui.windows.getShared('KDW003C_Output');
                            if (dataTemp != undefined) {
                                let data = [dataTemp];
								self.formatCodes(data);
                                let param = {
                                    dateRange: dateRangeParam ? {
                                        startDate: moment(dateRangeParam.startDate).utc().toISOString(),
                                        endDate: moment(dateRangeParam.endDate).utc().toISOString()
                                    } : null,
                                    displayFormat: _.isEmpty(self.shareObject()) ? (_.isEmpty(self.characteristics) ? 0 : self.characteristics.formatExtract) : self.shareObject().displayFormat,
                                    initScreen: _.isEmpty(self.characteristics) ? 0 : 1,
                                    mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                                    lstEmployee: [],
                                    formatCodes: data,
                                    objectShare: _.isEmpty(self.shareObject()) ? null : self.shareObject(),
                                    changeFormat: false
                                };
                                self.characteristics.authenSelectFormat = param.formatCodes;
                                self.characteristics.employeeId = __viewContext.user.employeeId;
                                self.characteristics.companyId = __viewContext.user.companyId;
                                self.characteristics.formatExtract = param.displayFormat;
                                character.save('characterKdw003a', self.characteristics);
                                nts.uk.ui.block.invisible();
                                nts.uk.ui.block.grayout();
                                //self.processMapData(data);
                                let paramMonth: any = { loadAfterCalc: false }
                                service.initParam(param).done((data) => {
                                    //self.processMapData(data);
                                    if (data.lstEmployee == undefined || data.lstEmployee.length == 0 || data.errorInfomation != 0) {
                                        let messageId = "Msg_1342";
                                        if (data.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) {
                                            messageId = "Msg_916";
                                            self.hasErrorBuss = true;
                                        } else if (data.errorInfomation == DCErrorInfomation.ITEM_HIDE_ALL) {
                                            messageId = "Msg_1452";
                                            self.hasErrorBuss = true;
                                        } else if (data.errorInfomation == DCErrorInfomation.NOT_EMP_IN_HIST) {
                                            messageId = "Msg_1543";
                                            self.hasErrorBuss = true;
                                        }
                                        nts.uk.ui.dialog.alert({ messageId: messageId }).then(function() {
                                            //self.hasEmployee = false;
                                            nts.uk.ui.block.clear();
                                            dfd.resolve({ bindDataMap: true, data: data });
                                        });
                                    } else if (!_.isEmpty(data.errors)) {
                                        let errors = [];
                                        _.forEach(data.errors, error => {
                                            errors.push({
                                                message: error.message,
                                                messageId: error.messageId,
                                                supplements: {}
                                            })
                                        });
                                        nts.uk.ui.dialog.bundledErrors({ errors: errors });
                                        self.hasErrorBuss = true;
                                        dfd.resolve({ bindDataMap: true, data: data });
                                    }
                                    else {
                                        let paramMonth: any = { loadAfterCalc: false }
                                        $.when(service.loadMonth(paramMonth), service.startScreen(param)).done((dataMonth, dataDaily) => {
                                            dataDaily.monthResult = dataMonth.monthResult;
                                            dataDaily.indentityMonthResult = dataMonth.indentityMonthResult;
                                            dataDaily.showTighProcess = dataMonth.showTighProcess;
                                            self.lstCellDisByLock = dataDaily.lstCellDisByLock;
                                            dfd.resolve({ bindDataMap: true, data: dataDaily });
                                        })
                                    }
                                })
                            }
                        });

                    };
                    }
                    nts.uk.ui.block.clear();
                });
            });
        }

        genDateExtract(hasChangeFormat: boolean): void{
            let self = this;
            if(self.displayFormat() == 0 && !hasChangeFormat){
                  self.actualTimeSelectedCode();
                  for(i = 0 ; i< self.timePeriodAllInfo.lstRangeCls.length; i++){
                      if(self.actualTimeSelectedCode() == self.timePeriodAllInfo.lstRangeCls[i].closureId){
                          self.dateRanger({ startDate: self.timePeriodAllInfo.lstRangeCls[i].startDate, endDate: self.timePeriodAllInfo.lstRangeCls[i].endDate });
                          break;
                      }
                  }
            }
        }

        findEmployee(hasChangeFormat: boolean): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred()
            let lstEmployee = [];
            if (self.displayFormat() === 0) {
                __viewContext.user.employeeId
                let lst: any;
                if(hasChangeFormat){
                    lst = _.find(self.lstEmployee(), (employee) => {
                        return employee.id === __viewContext.user.employeeId;
                    });
                    if(lst == undefined){
                        lst = self.lstEmployee()[0];
                    }
                    self.selectedEmployee(lst.id);
                }else{
                    lst = _.find(self.lstEmployee(), (employee) => {
                        return employee.id === self.selectedEmployee();
                    });
                }

//                let lst = _.find(self.lstEmployee(), (employee) => {
//                    return employee.id === self.selectedEmployee();
//                });

                if (lst != undefined) {
                    lstEmployee.push(lst);
                    dfd.resolve(lstEmployee);
                }else if (!_.isEmpty(self.selectedEmployee())) {
                    //let dfd2 = $.Deferred();
                    self.searchEmployee().done(data => {
                        dfd.resolve(data);
                        //  dfd2.resolve();
                    });
                    // dfd2.promise();
                }else{
                    self.lstEmployee(lstEmployee);
                    dfd.resolve(lstEmployee);
                }
            } else {
                lstEmployee = self.lstEmployee();
                dfd.resolve(lstEmployee);
            }
            return dfd.promise();
        }

        searchEmployee(): JQueryPromise<any> {
            let dfd = $.Deferred(), self = this,lstEmployee = [];;
            service.searchEmployee($('#search-input-'+'emp-component').val()).done(data => {
                let emp = {
                    id: data.employeeId,
                    code: data.employeeCode,
                    businessName: data.businessName,
                    workplaceName: data.wkpDisplayName,
                    workplaceId: "",
                    depName: '',
                    isLoginUser: false
                }
                lstEmployee.push(emp);
                self.lstEmployee(lstEmployee);
                dfd.resolve(lstEmployee);
                //  dfd2.resolve();
            }).fail(() => {
                dfd.reject();
            });
            return dfd.promise();
        }

        removeErrorRefer() {
            var self = this;
            if (!self.hasEmployee) return;
            self.listCareError([]);
            self.listCareInputError([]);
            self.listCheckHolidays([]);
            self.listCheck28([]);
            self.workTypeNotFound = [];
            self.listCheckDeviation = [];
            self.lstErrorFlex = [];
        }

        btnExtraction_Click() {
            let self = this;
            if (!self.hasEmployee || nts.uk.ui.errors.hasError()) return;
            self.showTextStyle = false;
            self.clickFromExtract = true;
            self.clickCounter = new CLickCount();
            if (self.isVisibleMIGrid()) {
                //set = false de co the jump den ham subscribe
                self.isVisibleMIGrid(false);
            }
            self.flagCalculation = false;
            self.listErrorMonth = [];

          //  self.shareObject(new ShareObject());
            character.restore("characterKdw003a").done((obj: Characteristics) => {
                self.characteristics.employeeId = __viewContext.user.employeeId;
                self.characteristics.companyId = __viewContext.user.companyId;
                self.characteristics.showZero = self.displayWhenZero();
                self.characteristics.showProfile = self.showProfileIcon();
                self.characteristics.showNumberHeader = self.showHeaderNumber();
                self.characteristics.formatExtract = self.displayFormat();
                self.characteristics.authenSelectFormat = self.formatCodes();
                self.characteristics.moveMouse = self.selectedDirection();
                character.save('characterKdw003a', self.characteristics);
            });
            self.reloadScreen();
        }

        hasErrorGrid(): boolean {
            let uiErrors: any = $("#dpGrid").mGrid("errors");
            return !_.isEmpty(uiErrors);
        }

        getErrorGrid(): any {
            var self = this;
            let uiErrors: any = $("#dpGrid").mGrid("errors");
            let errorValidateScreeen: any = [];
            if (self.displayFormat() === 0) {
                _.each(uiErrors, value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.id == value.rowId.substring(1, value.rowId.length);
                    });

                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: value.message, itemName: "", columnKey: value.columnKey };
                    let item = _.find(self.optionalHeader, (data) => {
                        if (data.group != undefined && data.group != null) {
                            return String(data.group[0].key) === value.columnKey;
                        } else {
                            return String(data.key) === value.columnKey;
                        }
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    errorValidateScreeen.push(object);
                });
            } else {
                _.each(uiErrors, value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.id == value.rowId.substring(1, value.rowId.length);
                    });

                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: value.message, itemName: "", columnKey: value.columnKey };
                    let item = _.find(self.optionalHeader, (data) => {
                        return String(data.key) === value.columnKey;
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    errorValidateScreeen.push(object);
                });
            }
            return errorValidateScreeen;
        }

        showErrorDialog(messageAlert ?: string): JQueryPromise<any> {
            var self = this;
            let dfd = $.Deferred();
            if(self.openedScreenB) return;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            let lstEmployee = [];
            let errorValidateScreeen: any = [];

            if (_.isEmpty(messageAlert) || !_.isString(messageAlert)) {
                errorValidateScreeen = self.getErrorGrid();
            } else {
                // get error insert , update
                // item28 , search
                _.each(self.workTypeNotFound, value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.id == value.rowId.substring(1, value.rowId.length);
                    });
                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: value.message, itemName: "", columnKey: value.itemId };
                    let item = _.find(self.optionalHeader, (data) => {
                        if (data.group != undefined && data.group != null) {
                            return String(data.group[0].key) === value.columnKey;
                        }
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    errorValidateScreeen.push(object);
                });

                _.each(self.listCareError(), value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.id == value.rowId.substring(1, value.rowId.length);
                    });
                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: nts.uk.resource.getMessage("Msg_996"), itemName: "", columnKey: value.itemId };
                    let item = _.find(self.optionalHeader, (data) => {
                        return String(data.key) === "A" + value.itemId;
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    errorValidateScreeen.push(object);
                });

                // careinput
                _.each(self.listCareInputError(), value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.id == value.rowId.substring(1, value.rowId.length);
                    });
                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: value.message, itemName: "", columnKey: value.itemId };
                    let item = _.find(self.optionalHeader, (data) => {
                        return String(data.key) === "A" + value.itemId;
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    let itemOtherInGroup = CHECK_INPUT[value.itemId + ""];
                    let itemGroup = self.itemInputName[Number(itemOtherInGroup)];
                    let nameGroup: any = (itemGroup == undefined) ? "" : itemGroup;
					if(_.includes(value.message,'_')){
                    	object.message = nts.uk.resource.getMessage(value.message, [object.itemName, nameGroup]);
					}
                    errorValidateScreeen.push(object);
                });

                //CheckHolidays
                _.each(self.listCheckHolidays(), value => {
                    let object = { date: value.date, employeeCode: self.dpData[0].employeeCode, employeeName: self.dpData[0].employeeName, message: value.valueType, itemName: value.rowId, columnKey: value.itemId };
                    errorValidateScreeen.push(object);
                });

                // check item28 input listCheck28
                _.each(self.listCheck28(), value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.id == value.rowId.substring(1, value.rowId.length);
                    });
                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: value.layoutCode, itemName: "", columnKey: value.itemId };
                    //                let item = _.find(self.optionalHeader, (data) => {
                    //                    return String(data.key) === "A" + value.itemId;
                    //                })
                    let item = _.find(self.optionalHeader, (data) => {
                        if (data.group != undefined && data.group != null) {
                            return String(data.group[0].key) === "Code" + value.itemId;
                        } else {
                            return String(data.key) === "A" + value.itemId;
                        }
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    errorValidateScreeen.push(object);
                });

                //error check PC
                _.each(self.listCheckDeviation, value => {
                    let dateCon = _.find(self.dpData, (item: any) => {
                        return item.employeeId == value.employeeId && item.date == value.date;
                    });
                    let object = { date: dateCon.date, employeeCode: dateCon.employeeCode, employeeName: dateCon.employeeName, message: value.valueType, itemName: "", columnKey: value.itemId };
                    let item = _.find(self.optionalHeader, (data) => {
                        if (data.group != undefined && data.group != null) {
                            return String(data.group[0].key) === "Code" + value.itemId;
                        } else {
                            return data.key != undefined && String(data.key) === "A" + value.itemId;
                        }
                    })
                    object.itemName = (item == undefined) ? "" : item.headerText.replace("<br/>", "");
                    object.message = nts.uk.resource.getMessage("Msg_1298", [object.itemName, value.value])
                    errorValidateScreeen.push(object);
                });

                //error month
                _.each(self.listErrorMonth, value => {
                    let emp = _.find(self.lstEmployee(), (employee) => {
                        return employee.id === value.employeeId;
                    });
                    let object = { date: "", employeeCode: emp.code, employeeName: emp.businessName, value, message: value.message, columnKey: "" };
                    errorValidateScreeen.push(object);
                })

            // error month flex
            if (self.displayFormat() === 0) {
                let lst = _.find(self.lstEmployee(), (employee) => {
                    return employee.id === self.selectedEmployee();
                });
                _.each(self.lstErrorFlex, value => {
                    let object = { date: "", employeeCode: lst.code, employeeName: lst.businessName, value, message: value.message, columnKey: "" };
                    errorValidateScreeen.push(object);
                });
            }
            }
            if (self.displayFormat() === 0) {
                let emp = _.find(self.lstEmployee(), (employee) => {
                    return employee.id === self.selectedEmployee();
                })
                if (emp != null && emp != undefined) {
                    lstEmployee.push(emp);
                }
            } else {
                lstEmployee = self.lstEmployee();
            }
            if (lstEmployee.length > 0) {
                let param = {
                    dateRange: {
                        startDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().startDate).utc().toISOString(),
                        endDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().endDate).utc().toISOString()
                    },
                    lstEmployee: lstEmployee,
                    selectFormat:  self.formatCodes()
                };
				setShared("displayFormat", self.displayFormat());
                setShared("paramToGetError", param);
                setShared("errorValidate", errorValidateScreeen);
                setShared("messageKdw003a", (_.isEmpty(messageAlert) || !_.isString(messageAlert)) ? null : messageAlert);
                self.openedScreenB = true;
                let dialogSize = (_.isEmpty(messageAlert) || !_.isString(messageAlert)) ? {width : 1260, height : 530} : {width : 900, height : 560}
                self.dialogShow = nts.uk.ui.windows.sub.modeless("/view/kdw/003/b/index.xhtml", dialogSize).onClosed(() =>{
                     self.openedScreenB = false;
                     dfd.resolve();
                });
            }
           return dfd.promise();
        }

        changeExtractionCondition() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (!nts.uk.ui.errors.hasError()) {
                self.hideComponent();
                let lstEmployee = [];
                if (self.displayFormat() === 0) {
                    lstEmployee.push(_.find(self.lstEmployee(), (employee) => {
                        return employee.id === self.selectedEmployee();
                    }));
                } else {
                    lstEmployee = self.lstEmployee();
                }
                //  let errorCodes =["0001","0002","003"];
                let errorParam = { initMode: 0, selectedItems: [] };
                setShared("KDW003D_ErrorParam", errorParam);
                self.flagCalculation = false;
                modal("/view/kdw/003/d/index.xhtml").onClosed(() => {
                    nts.uk.ui.block.clear();
                    let errorCodes = nts.uk.ui.windows.getShared('KDW003D_Output');
                    if (errorCodes != undefined && errorCodes.length > 0) {
                        let param = {
                            dateRange: {
                                startDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().startDate).utc().toISOString(),
                                endDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().endDate).utc().toISOString()
                            },
                            lstEmployee: lstEmployee,
                            displayFormat: self.displayFormat(),
                            mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                            errorCodes: errorCodes,
                            formatCodes: self.formatCodes(),
                            showLock: self.showLock()
                        };
                        nts.uk.ui.block.invisible();
                        nts.uk.ui.block.grayout();
                        service.selectErrorCode(param).done((data) => {
                            //self.lstDomainOld = data.domainOld;
                            //self.lstDomainEdit = _.cloneDeep(data.domainOld);
                            self.dataAll(data);
                            self.removeErrorRefer();
                            self.createSumColumn(data);
                            self.columnSettings(data.lstControlDisplayItem.columnSettings);
                            self.receiveData(data);
                            self.extraction();
                            self.displayNumberZero();
                            nts.uk.ui.block.clear();
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alert(error.message);
                            nts.uk.ui.block.clear();
                        });
                    }
                });
            }
        }

        selectDisplayItem() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (!nts.uk.ui.errors.hasError()) {
                self.flagCalculation = false;
                setShared("selectedPerfFmtCodeList", self.formatCodes());
                modal("/view/kdw/003/c/index.xhtml").onClosed(() => {
                    let res = nts.uk.ui.windows.getShared('KDW003C_Err');
                    if(!_.isEmpty(res) && res.jumpToppage){
                        nts.uk.request.jumpToTopPage();
                    }
                    var dataTemp = nts.uk.ui.windows.getShared('KDW003C_Output');
                    if (dataTemp != undefined) {
                        let data = [dataTemp];
                        self.formatCodes(data);
                        self.hideComponent();
                        let lstEmployee = [];
                        if (self.displayFormat() === 0) {
                            lstEmployee.push(_.find(self.lstEmployee(), (employee) => {
                                return employee.id === self.selectedEmployee();
                            }));
                        } else {
                            lstEmployee = self.lstEmployee();
                        }
                        /*let hasChangeFormat: boolean = (self.displayFormatOld == self.displayFormat()) ? false : true;
                        let param = {
                            dateRange: (self.hasEmployee && !hasChangeFormat) ? {
                                startDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().startDate).utc().toISOString(),
                                endDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().endDate).utc().toISOString()
                            } : null,
                            displayFormat: self.hasEmployee ? self.displayFormat() : _.isEmpty(self.shareObject()) ? 0 : self.shareObject().displayFormat,
                            initScreen: self.hasEmployee ? 1 : 0,
                            mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                            lstEmployee: lstEmployee,
                            formatCodes: data,
                            objectShare: null,
                            showLock: self.showLock(),
                            changeFormat: false
                        };*/
						
                        self.characteristics.authenSelectFormat = data;
                        self.characteristics.employeeId = __viewContext.user.employeeId;
                        self.characteristics.companyId = __viewContext.user.companyId;
                        self.characteristics.formatExtract = self.hasEmployee ? self.displayFormat() : _.isEmpty(self.shareObject()) ? 0 : self.shareObject().displayFormat;
                        character.save('characterKdw003a', self.characteristics);
						self.formatCodes(data);
						self.reloadScreen();
						
                       /* nts.uk.ui.block.invisible();
                        nts.uk.ui.block.grayout();
                        service.initParam(param).done((dataInit) => {
                            let paramMonth: any = {loadAfterCalc: false}
                            $.when(service.loadMonth(paramMonth), service.startScreen(param)).done((dataMonth, data) => {
                                data.monthResult = dataMonth.monthResult;
                                data.indentityMonthResult = dataMonth.indentityMonthResult;
                                data.showTighProcess = dataMonth.showTighProcess;
                                //self.lstDomainOld = data.domainOld;
                                //self.lstDomainEdit = _.cloneDeep(data.domainOld);
                                self.lstCellDisByLock = data.lstCellDisByLock;
                                self.lockDisableFlex(data.lockDisableFlex);
                                self.dataAll(data);
                                self.removeErrorRefer();
                                self.createSumColumn(data);
                                self.columnSettings(data.lstControlDisplayItem.columnSettings);
                                self.receiveData(data);
                                self.extraction();
                                self.displayNumberZero();
	
								self.reloadScreen();

                                nts.uk.ui.block.clear();
                            });
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alert(error.message);
                            nts.uk.ui.block.clear();
                        });*/
                    }
                });
            }
        }

        historyModification() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (self.displayFormat() == 0) {
                let listEmployee = [];
                listEmployee.push(self.selectedEmployee());
                let params: Params = {
                    pgid: __viewContext.program.programId,
                    functionId: 2,
                    listEmployeeId: listEmployee,
                    period: self.dateRanger(),
                    displayFormat: self.displayFormat(),
                };
                setShared("CDL027Params", params);
                modal("com", "/view/cdl/027/a/index.xhtml");
            } else if (self.displayFormat() == 1) {
                let listEmployee = [];
                for (let i = 0; i < self.dailyPerfomanceData().length; i++) {
                    let check = false;
                    for (let j = 0; j < listEmployee.length; j++) {
                        if (self.dailyPerfomanceData()[i].employeeId == listEmployee[j]) {
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        listEmployee.push(self.dailyPerfomanceData()[i].employeeId);
                    }
                }

                let params: Params = {
                    pgid: __viewContext.program.programId,
                    functionId: 2,
                    listEmployeeId: listEmployee,
                    period: self.periodCdl027(),
                    displayFormat: self.displayFormat(),
                };
                params.period = {
                    startDate: moment(self.selectedDate()).format("YYYY/MM/DD"),
                    endDate: moment(self.selectedDate()).format("YYYY/MM/DD")
                };
                setShared("CDL027Params", params);
                modal("com", "/view/cdl/027/a/index.xhtml");
            } else {
                let listEmployee = [];
                for (let i = 0; i < self.dailyPerfomanceData().length; i++) {
                    let check = false;
                    for (let j = 0; j < listEmployee.length; j++) {
                        if (self.dailyPerfomanceData()[i].employeeId == listEmployee[j]) {
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        listEmployee.push(self.dailyPerfomanceData()[i].employeeId);
                    }
                }

                let params: Params = {
                    pgid: __viewContext.program.programId,
                    functionId: 2,
                    listEmployeeId: listEmployee,
                    period: self.dateRanger(),
                    displayFormat: self.displayFormat(),
                };
                setShared("CDL027Params", params);
                modal("com", "/view/cdl/027/a/index.xhtml");

            }
        }

        referencesActualResult() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (!nts.uk.ui.errors.hasError()) {
                let lstEmployee: any[] = [];
                if (self.displayFormat() === 0) {
                    lstEmployee.push(_.find(self.lstEmployee(), (employee) => {
                        return employee.id === self.selectedEmployee();
                    }));
                    let param = {
                        startDate: moment(self.timePeriodAllInfo.targetRange.startDate).utc().toISOString(), 
                        endDate: moment(self.timePeriodAllInfo.targetRange.endDate).utc().toISOString(), 
                        mode: "0", 
                        listEmp: [lstEmployee[0].id]
                    };
                    setShared("KDL014-PARAM", param);
                    modal("/view/kdl/014/a/index.xhtml").onClosed(() => {
                    });

                } else if (self.displayFormat() === 1) {
                    lstEmployee = self.lstEmployee().map((data) => {
                        return data.id;
                    });
                    let param = {
                        startDate: moment(self.selectedDate()).utc().toISOString(), 
                        endDate: moment(self.selectedDate()).utc().toISOString(), 
                        mode: "1", 
                        listEmp: lstEmployee
                    };
                    setShared("KDL014-PARAM", param);
                    modal("/view/kdl/014/a/index.xhtml").onClosed(() => {
                    });
                } else {
                    lstEmployee = self.lstEmployee().map((data) => {
                        return data.id;
                    });
                    let param = {
                        startDate: moment(self.dateRanger().startDate).utc().toISOString(), 
                        endDate: moment(self.dateRanger().endDate).utc().toISOString(), 
                        mode: "1", 
                        listEmp: lstEmployee
                    };
                    setShared("KDL014-PARAM", param);
                    modal("/view/kdl/014/a/index.xhtml").onClosed(() => {
                    });
                }
            }
        }

        employmentOk() {
            let _self = this;
            if (!_self.hasEmployee || self.hasErrorBuss) return;
            if (!nts.uk.ui.errors.hasError()) {
                nts.uk.ui.block.grayout();
                modal("/view/kdl/006/a/index.xhtml").onClosed(() => {
                    nts.uk.ui.block.clear();
                });
            }
        }

        tighProcessShow() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            var dataSource = $("#dpGrid").mGrid("dataSource");
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            let dataRowEnd = dataSource[dataSource.length - 1];
            if (self.showFlex()) {
                self.insertUpdate("Tight").done((loadContinue: boolean) => {
                    if(!loadContinue){
                    service.addClosure({ employeeId: dataRowEnd.employeeId, date: dataRowEnd.dateDetail, showFlex: self.showFlex() }).done((data) => {
                        self.processLockButton(self.showLock());
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            //if (self.showDialogError) self.showErrorDialog();
                        });
                        nts.uk.ui.block.clear();
                    });
                    }else{
                        self.showErrorDialog("Msg_1489");
                        nts.uk.ui.block.clear();
                    }
                });
            } else {
                service.addClosure({ employeeId: dataRowEnd.employeeId, date: dataRowEnd.dateDetail, showFlex: self.showFlex() }).done((data) => {
                    self.processLockButton(self.showLock());
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        if (self.showDialogError) self.showErrorDialog();
                    });
                    nts.uk.ui.block.clear();
                });
                nts.uk.ui.block.clear();
            }
        }

        tighProcessRelease() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            var dataSource = $("#dpGrid").mGrid("dataSource");
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            let dataRowEnd = dataSource[dataSource.length - 1];
            service.releaseClosure({ employeeId: dataRowEnd.employeeId, date: dataRowEnd.dateDetail }).done((data) => {
                if (!_.isEmpty(data)) {
                    nts.uk.ui.dialog.info({ messageId: data }).then(() => {
                    });
                } else {
                    self.processLockButton(self.showLock());
                    nts.uk.ui.dialog.info({ messageId: "Msg_1445" }).then(() => {
                        if (self.showDialogError) self.showErrorDialog();
                    });
                }
                nts.uk.ui.block.clear();
            }).fail((data) => {
                nts.uk.ui.dialog.info({ messageId: "Msg_1501" }).then(() => {
                    self.reloadScreen();
                });
            });
            nts.uk.ui.block.clear();
        }

		showSupportWorkDialog() {
			let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (!nts.uk.ui.errors.hasError()) {
				let dataShare: any = {}, lstEmployee = [];
                if (self.displayFormat() === 0) {
                    lstEmployee.push(_.find(self.lstEmployee(), (employee) => {
                        return employee.id === self.selectedEmployee();
                    }));
                } else {
                    lstEmployee = self.lstEmployee();
                }
				dataShare.baseDate = moment(self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().startDate).utc().toISOString()).format("YYYY/MM/DD");
				dataShare.employeeIds = _.map(lstEmployee, item => item.id);
	
				setShared('dataShareKdw003g', dataShare);
                nts.uk.ui.block.grayout();
                modal("/view/kdw/003/g/index.xhtml").onClosed(() => {
                    nts.uk.ui.block.clear();
                });
            }	
		}

        proceedLock() {
            let self = this;
            //Msg_984
            nts.uk.ui.dialog.info({ messageId: "Msg_984" });
            if (!self.hasEmployee || self.hasErrorBuss) return;
            self.checkUnLock(false);
            self.showLock(true);
            self.unLock(false);
            self.processLockButton(true);
        }

        proceedUnLock() {
            let self = this;
            //Msg_982
            nts.uk.ui.dialog.confirm({ messageId: "Msg_982" }).ifYes(() => {
                if (!self.hasEmployee || self.hasErrorBuss) return;
                self.checkUnLock(true);
                self.showLock(false);
                self.unLock(true);
                self.processLockButton(false);
            });
            //nts.uk.ui.dialog.info({ messageId: "Msg_982" });
        }

        processLockButton(showLock: boolean) {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss || self.dailyPerfomanceData().length == 0) return;
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            let lstData = _.map(self.dailyPerfomanceData(), (map) => {
                map.date = moment(map.date).toISOString();
                map.state = "";
                return map;
            });
            let lstEmployee = [];
            if (self.displayFormat() === 0) {
                let lst: any = _.find(self.lstEmployee(), (employee) => {
                    return employee.id === self.selectedEmployee();
                });
                if (lst != undefined) lstEmployee.push(lst);
            } else {
                lstEmployee = self.lstEmployee();
            }
            let param: any = {
                lstAttendanceItem: self.lstAttendanceItem(),
                lstEmployee: lstEmployee,
                dateRange: self.hasEmployee ? {
                    startDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().startDate).utc().toISOString(),
                    endDate: self.displayFormat() === 1 ? moment(self.selectedDate()) : moment(self.dateRanger().endDate).utc().toISOString()
                } : null,
                mode: _.isEmpty(self.shareObject()) ? 0 : self.shareObject().screenMode,
                displayFormat: self.displayFormat(),
                lstData: lstData,
                lstHeader: self.lstHeaderReceive,
                showLock: showLock,
                periodLock: self.periodCheckLock == null ? null : {startDate: moment(self.periodCheckLock.startDate), endDate: moment(self.periodCheckLock.endDate)}
            }

            let dfd = $.Deferred();
            service.lock(param).done((data) => {
                nts.uk.ui.block.clear();
                self.lockDisableFlex(data.lockDisableFlex);
                self.indentityMonth(data.indentityMonthResult);
                let dataUpdate = $("#dpGrid").mGrid("updatedCells");
                let dataSourceRow = _.cloneDeep(self.formatDate(data.lstData));
                _.forEach(dataSourceRow, (valueUpdate) => {
                    $("#dpGrid").mGrid("updateCell", valueUpdate.id, "state", valueUpdate.state, true, true)
                })
                setTimeout(() => {
                    let rowIdsTemp = _.uniqBy(data.lstCellState, function(e) {
                        return e.rowId;
                    });
                    $("#dpGrid").mGrid("clearState", _.map(rowIdsTemp, (value) => {
                        return value.rowId;
                    }))
                    _.each(data.lstCellState, (valt) => {
                        console.log("column key:" + valt.columnKey);
                        $("#dpGrid").mGrid("setState", valt.rowId, valt.columnKey, valt.state);
                    });
                     _.forEach(dataUpdate, (valueUpdate) => {
                        $("#dpGrid").mGrid("updateCell", valueUpdate.rowId, valueUpdate.columnKey, valueUpdate.value, false, true);
                    })
                    nts.uk.ui.block.clear();
                }, 1000);
                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }

        btnSetting_Click() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            var container = $("#setting-content");
            if (container.css("visibility") === 'hidden') {
                container.css("visibility", "visible");
                //                container.css("top", "0px");
                //                container.css("left", "0px");
            }
            $(document).mouseup(function(e) {
                // if the target of the click isn't the container nor a descendant of the container
                if (!container.is(e.target) && container.has(e.target).length === 0) {
                    container.css("visibility", "hidden");
                    //                    container.css("top", "-9999px");
                    //                    container.css("left", "-9999px");
                }
            });
        }

        btnVacationRemaining_Click() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            var container = $("#vacationRemaining-content");
            if (container.css("visibility") === 'hidden') {
                container.css("visibility", "visible");
                container.css("top", "0px");
                container.css("left", "0px");
            }
            $(document).mouseup(function(e) {
                // if the target of the click isn't the container nor a descendant of the container
                if (!container.is(e.target) && container.has(e.target).length === 0) {
                    container.css("visibility", "hidden");
                    container.css("top", "-9999px");
                    container.css("left", "-99990px");
                }
            });
        }

        btnSaveColumnWidth_Click() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            let command = {
                lstHeader: {},
                formatCode: self.autBussCode(),
                lstHeaderMiGrid: {}
            };
            let jsonColumnWith = localStorage.getItem(window.location.href + '/dpGrid');
            let jsonColumnWidthMiGrid = localStorage.getItem(window.location.href + '/miGrid');

            let valueTemp = 0;
            _.forEach($.parseJSON(jsonColumnWith), (valueP, keyP) => {
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

            if (self.isVisibleMIGrid() && jsonColumnWidthMiGrid != null) {
                let columnWidthMiGrid = $.parseJSON(jsonColumnWidthMiGrid.replace(/_/g, ''));
                delete columnWidthMiGrid.monthYear;
                command.lstHeaderMiGrid = columnWidthMiGrid;
            }

            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            service.saveColumnWidth(command).done(() => {
                nts.uk.ui.block.clear();
            }).fail(() => {
                nts.uk.ui.block.clear();
            });
        }

        deleteGridInLocalStorage(): void {
            // delete localStorage dpGrid
            localStorage.removeItem(window.location.href + '/dpGrid');
            // delete localStorage miGrid
            localStorage.removeItem(window.location.href + '/miGrid');
        }

        extractionData() {
            var self = this;
            self.headersGrid = [];
            self.fixColGrid([]);
            if (self.displayFormat() == 0) {
                self.fixColGrid(self.employeeModeFixCol);
            } else if (self.displayFormat() == 1) {
                self.fixColGrid(self.dateModeFixCol);
            } else if (self.displayFormat() == 2) {
                self.fixColGrid(self.errorModeFixCol);
            }
            if (self.showPrincipal() && self.hasLstHeader) {
                let sign = _.find(self.fixColGrid(), (data: any) => {
                    return data.columnKey === 'sign';
                }
                )
                if (sign == undefined) self.fixColGrid.push({ columnKey: 'sign', isFixed: true });
            }
            if (self.showSupervisor() && self.hasLstHeader) {
                let sign = _.find(self.fixColGrid(), (data: any) => {
                    return data.columnKey === 'approval';
                }
                )
                if (sign == undefined) self.fixColGrid.push({ columnKey: 'approval', isFixed: true });
            }
            self.loadHeader(self.displayFormat());
            self.dailyPerfomanceData(_.cloneDeep(self.dpData));
        }

        extraction() {
            var self = this;
            if (self.hasEmployee || self.hasErrorBuss) self.destroyGrid();
            self.extractionData();
            self.loadGrid();
            self.displayProfileIcon(self.displayFormat());
        }

        isDisableRow(id) {
            var self = this;
            for (let i = 0; i < self.rowStates().length; i++) {
                return self.rowStates()[i].rowId == id;
            }
        }

        isDisableSign(id, data) {
            var self = this;
            return _.filter(data, (value: any) => {
                return value.rowId == id && (_.filter(value.state, state => {
                    return state == "ntsgrid-disable"
                }).length > 0);
            });
        }

        lstDisableSign() {
            var self = this;
            return _.filter(self.cellStates(), (data: any) => {
                return data.columnKey == "sign";
            });
        }

        lstDisableApproval() {
            var self = this;
            return _.filter(self.cellStates(), (data: any) => {
                return data.columnKey == "approval";
            });
        }

        signAll() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            $("#dpGrid").mGrid("checkAll", "sign", true);
            $("#dpGrid").mGrid("checkAll", "approval", true);
        }

        releaseAll() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            $("#dpGrid").mGrid("uncheckAll", "sign", true);
            $("#dpGrid").mGrid("uncheckAll", "approval", true);
        }

        destroyGrid() {
            if ($("#dpGrid").hasClass("mgrid")) {
                $("#dpGrid").mGrid("destroy");
                $("#dpGrid").removeClass("mgrid");
                $("#dpGrid").off();
            }
        }

        setColorWeekend() {
            var self = this;
            self.textColors([]);
            _.forEach(self.dailyPerfomanceData(), (data) => {
                if (moment(data.date, "YYYY/MM/DD").day() == 6) {
                    self.textColors.push({
                        rowId: "_" + data.id,
                        columnKey: 'date',
                        color: '#4F81BD'
                    });
                } else if (moment(data.date, "YYYY/MM/DD").day() == 0) {
                    self.textColors.push({
                        rowId: "_" + data.id,
                        columnKey: 'date',
                        color: '#e51010'
                    });
                }
            });
        }

        setHeaderColor() {
            var self = this;
            self.headerColors = [];
            _.forEach(self.headersGrid, (header) => {

                if (header.group != null && header.group != undefined && header.group.length > 0 && header.group[0].color != "") {
                    self.headerColors.push({
                        key: header.group[0].key,
                        colors: [header.group[0].color]
                    });
                    self.headerColors.push({
                        key: header.group[1].key,
                        colors: [header.group[1].color]
                    });
                } else {
                    if (header.color && header.color != "") {
                        self.headerColors.push({
                            key: header.key,
                            colors: [header.color]
                        });
                    }
                }
            });
        }

        formatDate(lstData) {
            var self = this;
            let data = lstData.map((data) => {
                var object = {
                    id: "_" + data.id,
                    state: data.state,
                    error: data.error,
                    date: moment(data.date, "YYYY/MM/DD").format("MM/DD(dd)"),
                    sign: data.sign,
                    approval: data.approval,
                    employeeId: data.employeeId,
                    employeeCode: data.employeeCode,
                    employeeName: data.employeeName,
                    workplaceId: data.workplaceId,
                    employmentCode: data.employmentCode,
                    dateDetail: moment(data.date, "YYYY/MM/DD"),
                    typeGroup: data.typeGroup
                }
                _.each(data.cellDatas, function(item) {
                    object[item.columnKey] = item.value;
                });
                return object;
            });
            return data;
        }

        filterData(mode: number) {
            var self = this;
            if (mode == 0) {
                return _.filter(self.dpData, (data) => { return data.employeeId == self.selectedEmployee() });
            } else if (mode == 1) {
                return _.filter(self.dpData, (data) => { return data.date === moment(self.selectedDate()).format('YYYY/MM/DD') });
            } else if (mode == 2) {
                return _.filter(self.dpData, (data) => { return data.error !== '' });
            }
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
        //load kcp009 component: employee picker
        loadKcp009() {
            let self = this;
            var kcp009Options = {
                systemReference: 1,
                isDisplayOrganizationName: true,
                employeeInputList: self.lstEmployee,
                targetBtnText: getText("KCP009_3"),
                selectedItem: self.selectedEmployee,
                tabIndex: 1
            };
            // Load listComponent
            $('#emp-component').ntsLoadListComponent(kcp009Options);
        }

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
                /** Common properties */
                systemType: 2,
                showEmployeeSelection: false, // 検索タイプ
                showQuickSearchTab: true, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: true, // 基準日利用
                showClosure: false, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: false, // 対象期間利用
                periodFormatYM: false, // 対象期間精度

                /** Required parameter */
                //baseDate: self.baseDate().toISOString(), // 基準日
                //                periodStartDate: self.dateRanger() == null ? new Date().toISOString() :  self.dateRanger().startDate, // 対象期間開始日
                //                periodEndDate: self.dateRanger() == null ? new Date().toISOString() : self.dateRanger().endDate, // 対象期間終了日
                //dateRangePickerValue: self.dateRanger,
                inService: true, // 在職区分
                leaveOfAbsence: true, // 休職区分
                closed: true, // 休業区分
                retirement: false, // 退職区分
                baseDate: self.dateReferCCg,

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
                // 選択モード

                /** Return data */
                returnDataFromCcg001: function(dataList: any) {
                     nts.uk.ui.block.invisible();
                     nts.uk.ui.block.grayout();
                    setTimeout(() => {
                     // self.selectedEmployee(dataList.listEmployee);
                    self.lstEmployee(dataList.listEmployee.map((data: EmployeeSearchDto) => {
                        return {
                            id: data.employeeId,
                            code: data.employeeCode,
                            businessName: data.employeeName,
                            workplaceName: data.affiliationName,
                            workplaceId: data.workplaceId,
                            depName: '',
                            isLoginUser: false
                        };
                    }));
                    self.lstEmployee(_.orderBy(self.lstEmployee(), ['code'], ['asc']));
                    let employeeLoginInList = _.find(self.lstEmployee(), (emp) =>{
                       return __viewContext.user.employeeId == emp.id;
                    })
                    self.selectedEmployee(_.isEmpty(employeeLoginInList) ? self.lstEmployee()[0].id : employeeLoginInList.id);
                    //#120496
                    //self.dateRanger({ startDate: dataList.periodStart, endDate: dataList.periodEnd });
                    self.hasEmployee = true;
                    self.closureId = dataList.closureId;
                    self.loadKcp009();
                    self.initFromScreenOther = true;
                    self.btnExtraction_Click();
                    }, 50);
                },
            }
        }

        createKeyLoad(): any {
            var self = this;
            let lstEmployee = [];
            let data = [];
            if (self.displayFormat() === 0) {
                lstEmployee.push(_.find(self.lstEmployee(), (employee) => {
                    return employee.id === self.selectedEmployee();
                }));
            } else {
                lstEmployee = self.lstEmployee();
            }
            let format = "YYYY-MM-DD";
            moment(self.dateRanger().startDate).format(format)
            let startDate = self.displayFormat() === 1 ? moment(self.selectedDate()).format(format) : moment(self.dateRanger().startDate).format(format);
            let endDate = self.displayFormat() === 1 ? moment(self.selectedDate()).format(format) : moment(self.dateRanger().endDate).format(format);
            let i = 0;
            if (self.displayFormat() === 0) {
                _.each(self.createDateList(self.dateRanger().startDate, (self.dateRanger().endDate)), value => {
                    data.push(self.displayFormat() + "_" + self.lstEmployee()[0].id + "_" + self.convertDateToString(value) + "_" + endDate + "_" + i)
                    i++;
                });
            } else if (self.displayFormat() === 1) {
                _.each(lstEmployee, value => {
                    data.push(self.displayFormat() + "_" + value.id + "_" + startDate + "_" + endDate)
                    i++;
                });
            } else {
                _.each(lstEmployee, value => {
                    _.each(self.createDateList(self.dateRanger().startDate, (self.dateRanger().endDate)), value2 => {
                        data.push(self.displayFormat() + "_" + value.id + "_" + self.convertDateToString(value2) + "_" + endDate + "_" + i)
                        i++;
                    });
                });
            }
            return data;
        }

        createDateList(startDate: any, endDate: any): any {
            var dateArray = [];
            var currentDate: any = moment(startDate);
            var stopDate: any = moment(endDate);
            while (currentDate <= stopDate) {
                dateArray.push(moment(currentDate).format('YYYY-MM-DD'))
                currentDate = moment(currentDate).add(1, 'days');
            }
            return dateArray;
        }
        convertDateToString(date: any): any {
            return moment(date).format('YYYY-MM-DD');
        }
        //load ccg001 component: search employee
        loadCcg001() {
            var self = this;
            $('#ccg001').ntsGroupComponent(self.ccg001);
        }

        loadGrid() {
            var self = this;
            var summary: ISummaryColumn = {
                columnKey: 'salary',
                allowSummaries: true
            }
            var summaries = [];
            var rowState = {
                rowId: 0,
                disable: true
            }
            self.setHeaderColor();
            //self.setColorWeekend();
            //console.log(self.formatDate(self.dailyPerfomanceData()));
            self.createNtsMControl();
            self.lstDataSourceLoad = self.formatDate(_.cloneDeep(self.dailyPerfomanceData()));
            
            let startTime = performance.now();
            let subWidth = "50px";
            if (self.displayFormat() === 0) {
                subWidth = "135px";
            } else if (self.displayFormat() === 1) {
                subWidth = "135px";
            } else {
                subWidth = "155px";
            }
			let features = [
                { name: 'ColumnFixing', fixingDirection: 'left', showFixButtons: false, columnSettings: self.fixColGrid() },
                { name: 'Resizing', columnSettings: [{ columnKey: 'id', allowResizing: false, minimumWidth: 0 }] },
                { name: 'MultiColumnHeaders' },
                {
                    name: 'Summaries',
                    columnSettings: self.columnSettings()
                },
                { name: 'HeaderStyles', columns: self.headerColors },
                { name: 'CellStyles', states: self.cellStates() },
                {
                    name: "Sheet",
                    initialDisplay: self.sheetsGrid()[0].name,
                    sheets: self.sheetsGrid()
                },
                { name: 'Copy' }
            ];
			if(self.displayFormat() === 1) {
				features.push({ name: 'Paging', pageSize: 31, currentPageIndex: 0 });
			}
            new nts.uk.ui.mgrid.MGrid($("#dpGrid")[0], {
                subWidth: subWidth,
                subHeight: '285px',
                height: (window.screen.availHeight - 240) + "px",
                headerHeight: '40px',
                dataSource: self.lstDataSourceLoad,
                //                minRows: 31,"
                //                maxRows: 50,
                primaryKey: 'id',
                rowVirtualization: true,
                virtualization: true,
                virtualizationMode: 'continuous',
                enter: self.selectedDirection() == 0 ? 'below' : 'right',
                autoFitWindow: true,
                preventEditInError: false,
                columns: self.headersGrid,
                hidePrimaryKey: true,
                userId: self.employIdLogin,
                hideZero: !self.displayWhenZero(),
                getUserId: function(primaryKey) {
                    let ids = primaryKey.split("_");
                    return ids[2] + "-" + ids[3] + "-" + ids[4];
                },
                features: features,
                ntsControls: self.ntsMControl
            }).create();
            console.log("load grid detail:" + (performance.now() - startTime));
        }

        createNtsFeatures() {
            var self = this;
            let lstNtsFeature = [
                { name: 'CopyPaste' },
                { name: 'CellEdit' },
                { name: 'CellState', rowId: 'rowId', columnKey: 'columnKey', state: 'state', states: self.cellStates() },
                { name: 'RowState', rows: self.rowStates() },
                { name: 'TextColor', rowId: 'rowId', columnKey: 'columnKey', color: 'color', colorsTable: self.textColors() },
                { name: 'HeaderStyles', columns: self.headerColors() },
                {
                    name: 'TextStyle',
                    rowId: 'rowId',
                    columnKey: 'columnKey',
                    style: 'style',
                    styles: self.textStyles
                },
            ];
            //            let lzyLoad = {
            //                name: "LoadOnDemand",
            //                allKeysPath: "/kdw003/lazyload/keys",
            //                pageRecordsPath: "/kdw003/lazyload/data",
            //            }
            //            if(self.displayFormat() == 1){
            //               lstNtsFeature.push(lzyLoad);
            //            }
            if (self.sheetsGrid().length > 0) {
                lstNtsFeature.push({
                    name: "Sheet",
                    initialDisplay: self.sheetsGrid()[0].name,
                    sheets: self.sheetsGrid()
                });
            }
            return lstNtsFeature;
        }

        createNtsMControl(): void {
            let self = this;

            let isConfirmMonth: boolean = true;
            _.each(self.dailyPerfomanceData(), item => {
                if(item.state == ''){
                    isConfirmMonth = false;
                }
            })

			let comboDoWork: any = _.filter(self.headersGrid, (h : any) => {
				return _.includes(h.group && h.group[1].ntsControl,"ComboboxDoWork");
			});
			
            self.ntsMControl = [
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
                { name: 'TextEditorNumberSeparated', controlType: 'TextEditor', constraint: { valueType: 'Integer', required: true, format: "Number_Separated" } },
                { name: 'TextEditorTimeShortHM', controlType: 'TextEditor', constraint: { valueType: 'Time', required: true, format: "Time_Short_HM" } },
                { name: 'ComboboxCalc', options: self.comboItemsCalc(), optionsValue: 'code', optionsText: 'name', columns: self.comboColumnsCalc(), editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true, spaceSize: 'small' },
                { name: 'ComboboxReason', options: self.comboItemsReason(), optionsValue: 'code', optionsText: 'name', columns: self.comboColumns(), editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true, spaceSize: 'small' },
                { name: 'ComboItemsCompact', options: self.comboItemsCompact(), optionsValue: 'code', optionsText: 'name', columns: self.comboColumns(), editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true, spaceSize: 'small' },
                { name: 'ComboboxTimeLimit', options: self.comboTimeLimit(), optionsValue: 'code', optionsText: 'name', columns: self.comboColumns(), editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true, spaceSize: 'small' },

                {
                    name: 'FlexImage', source: 'ui-icon ui-icon-locked', click: function(key, rowId, evt) {
                        let data = $("#dpGrid").mGrid("getCellValue", rowId, key);
                        if (data != "") {
                            let lock = data.split("|");
                            let tempD = "<span>";
                            for (let i = 1; i < lock.length; i++) {
                                if (lock[i] == "D" || lock[i] == "M") tempD += getText("KDW003_66") + '<br/>';
                                if (lock[i] == "C") tempD += getText("KDW003_67") + '<br/>';
                                if (lock[i] == "S") tempD += getText("KDW003_113") + '<br/>';
                                if (lock[i] == "CM") tempD += getText("KDW003_112") + '<br/>';
                                if (lock[i] == "AM") tempD += getText("KDW003_68") + '<br/>';
                                if (lock[i] == "H") tempD += getText("KDW003_70") + '<br/>';
                                if (lock[i] == "A") tempD += getText("KDW003_69") + '<br/>';
                            }
                            if(isConfirmMonth){
                                tempD += getText("KDW003_68") + '<br/>';
                            } 
                            tempD += '</span>'
                            $('#textLock').html(tempD);
                        }
                        self.helps(evt, "");
                    },
                    controlType: 'FlexImage'
                },
                { name: 'Image', source: 'image-icon', controlType: 'Image' },
                {
                    name: 'Button', controlType: 'Button', text: getText("KDW003_63"), enable: true, click: function(data) {
                        __viewContext.vm.clickButtonApplication(data);
                    }
                },
                {
                    name: 'ButtonList', controlType: 'Button', text: getText("KDW003_110"), enable: true, click: function(data) {
                        __viewContext.vm.clickButtonList(data);
                    }
                }
            ];
			
			_.forEach(comboDoWork, (c, i) => {
				self.ntsMControl.push({ name: 'ComboboxDoWork' + c.group[1].key, options: self.comboItemsDoWork(), optionsValue: 'code', optionsText: 'name', columns: self.comboColumns(), editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true, spaceSize: 'small', 
					onChange: self.inputProcess });
			});
        }

        loadMIGrid(data: any): void {
            let self = this,
                dataSourceMIGrid: any[] = [{ monthYear: self.monthYear() }],
                columnsMIGrid: any[] = [{ headerText: getText("KDW003_40"), key: 'monthYear', dataType: 'string', width: '75px' }],
                totalWidthColumn: number = 75,
                maxWidth: number = window.innerWidth - 200;

            _.forEach(self.listAttendanceItemId(), (id) => {
                let attendanceItemId: any = _.find(data, { 'attendanceItemId': id.itemId }),
                    cDisplayType = self.convertToCDisplayType(id.valueType);
                columnsMIGrid.push(
                    {
                        headerText: attendanceItemId.attendanceItemName, key: '_' + attendanceItemId.attendanceItemId, width: id.columnWidth + 'px',
                        constraint: {
                            cDisplayType: cDisplayType,
                            required: false
                        }
                    }
                );
                let valueUI = ""
                if (id.value != null && cDisplayType == 'Clock') {
                    valueUI = self.convertToHours(Number(id.value))
                } else if (cDisplayType == 'Currency') {
                    valueUI = self.convertMoney(id.value, "¥");
                } else {
                    valueUI = id.value;
                }
                dataSourceMIGrid[0]['_' + attendanceItemId.attendanceItemId] = valueUI;
                totalWidthColumn += id.columnWidth;
            });

            $("#miGrid").ntsGrid({
                width: (totalWidthColumn >= maxWidth) ? maxWidth + 'px' : totalWidthColumn + 'px',
                dataSource: dataSourceMIGrid,
                primaryKey: 'monthYear',
                autoFitWindow: false,
                columns: columnsMIGrid,
                features: [
                    { name: 'ColumnFixing', fixingDirection: 'left', showFixButtons: false, columnSettings: [{ columnKey: 'monthYear', isFixed: true }] },
                    { name: 'Resizing' }
                ],
                ntsFeatures: [],
                ntsControls: []
            });
        }

        convertToCDisplayType(value: string): string {
            switch (value) {
                case "TIME":
                case "CLOCK":
                    return "Clock";
                case "DAYS": return "Decimal";
                case "COUNT": return "Integer";
                case "AMOUNT": return "Currency";
                default: return "String";
            }
        }

        convertMoney(value: any, unit: string): any {
            let self = this;
            if (_.isEmpty(value)) return unit + "";
            else return unit + " " + self.formatMoney(Number(value), 0, '.', ',');
        }

        formatMoney(n: number, c, d, t): any {
            let c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
            return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
        };

        clickButtonApplication(data) {
            let self = this;
            let source: any = $("#dpGrid").mGrid("dataSource");
            let rowItemSelect: any = _.find(source, function(value: any) {
                return value.id == data.id;
            })
            let dfd = $.Deferred();
            let dataShare: any = {
                date: rowItemSelect.dateDetail.format("YYYY/MM/DD"),
                listValue: []
            }
            let date = moment(dataShare.date, "YYYY/MM/DD");
            $.when(service.getApplication()).done((data) => {
                let dataToShare = [];
                let appTypeEnum = __viewContext.enums.ApplicationType;  
                let app = [];
                _.forEach(data, (obj) => {
                    app = _.filter(appTypeEnum, function(o) { return o.value == obj; });
                    dataToShare.push({value: obj, fieldName: app.length > 0 ? app[0].name : ''});
                });
                dataShare.listValue = dataToShare;
                setShared("shareToKdw003e", dataShare);
                modal("/view/kdw/003/e/index.xhtml").onClosed(() => {
                    let screen = nts.uk.ui.windows.getShared("shareToKdw003a");
                    if (screen == undefined) screen = 1905;
                    let transfer = {
                        baseDate: dataShare.date,
                        uiType: 1,
                        employeeIDs: [],
                        stampRequestMode: 1,
                        screenMode: 0,
						screenCode: 0
                    };
                    var vmNew = new ko.ViewModel();

                    switch (screen) {
                        case 0:
                            //KAF005-残業申請（早出）
                            vmNew.$jump.blank("/view/kaf/005/a/index.xhtml?overworkatr=0", transfer);
                            // nts.uk.request.jump("/view/kaf/005/a/index.xhtml?overworkatr=0", transfer);
                            break;

                        case 1:
                            //KAF005-残業申請（通常）
                            vmNew.$jump.blank("/view/kaf/005/a/index.xhtml?overworkatr=1", transfer);
                            // nts.uk.request.jump("/view/kaf/005/a/index.xhtml?overworkatr=1", transfer);
                            break;

                        case 2:
                            //KAF005-残業申請（早出・通常）
                            vmNew.$jump.blank("/view/kaf/005/a/index.xhtml?overworkatr=2", transfer);
                            //nts.uk.ui.windows.sub.modal("/view/kaf/005/a/index.xhtml?overworkatr=2", transfer);
                            //  nts.uk.request.jump("/view/kaf/005/a/index.xhtml?overworkatr=2", transfer);
                            break;

                        case 3:
                            //KAF006-休暇申請
                            vmNew.$jump.blank("/view/kaf/006/a/index.xhtml", transfer);
                            //   nts.uk.request.jump("/view/kaf/006/a/index.xhtml", transfer);
                            break;

                        case 4:
                            //KAF007-勤務変更申請
                            vmNew.$jump.blank("/view/kaf/007/a/index.xhtml", transfer);
                            // nts.uk.request.jump("/view/kaf/007/a/index.xhtml", transfer);
                            break;

                        case 5:
                            //KAF008-出張申請
                            vmNew.$jump.blank("/view/kaf/008/a/index.xhtml", transfer);
                            // nts.uk.request.jump("/view/kaf/008/a/index.xhtml", transfer);
                            break;

                        case 6:
                            //KAF009-直行直帰申請
                            vmNew.$jump.blank("/view/kaf/009/a/index.xhtml", transfer);
                            // nts.uk.request.jump("/view/kaf/009/a/index.xhtml", transfer);
                            break;

                        case 7:
                            //KAF010-休出時間申請
                            transfer.uiType = 0;
                            vmNew.$jump.blank("/view/kaf/010/a/index.xhtml", transfer);
                            //nts.uk.request.jump("/view/kaf/010/a/index.xhtml", transfer);
                            break;

                        case 8:
                            //KAF002-打刻申請（外出許可）
                            transfer.stampRequestMode = 0;
                            transfer.screenMode = 1;
                            vmNew.$jump.blank("/view/kaf/002/a/index.xhtml", transfer);
                            //nts.uk.request.jump("/view/kaf/002/a/index.xhtml", transfer);
                            break;

                        case 9:
                            //KAF002-打刻申請（出退勤打刻漏れ）
                            transfer.stampRequestMode = 1;
                            transfer.screenMode = 1;
                            vmNew.$jump.blank("/view/kaf/002/b/index.xhtml", transfer);
                            // nts.uk.request.jump("/view/kaf/002/b/index.xhtml", transfer);
                            break;

                        case 10:
                            //KAF002-打刻申請（打刻取消）
                            transfer.stampRequestMode = 2;
                            transfer.screenMode = 1;
                            vmNew.$jump.blank("/view/kaf/012/a/index.xhtml", transfer);
                            //nts.uk.request.jump("/view/kaf/012/a/index.xhtml", transfer);
                            break;

                        case 11:
                            //KAF004
                            transfer.stampRequestMode = 3;
                            transfer.screenMode = 1;
                            vmNew.$jump.blank("/view/kaf/004/a/index.xhtml", transfer);
                            // nts.uk.request.jump("/view/kaf/004/a/index.xhtml", transfer);
                            break;

                        case 12:

                            transfer.stampRequestMode = 4;
                            transfer.screenMode = 1;
                            vmNew.$jump.blank("/view/kaf/011/a/index.xhtml", transfer);
                            // nts.uk.request.jump("/view/kaf/011/a/index.xhtml", transfer);
                            break;

                        case 13:
                            vmNew.$jump.blank("/view/kaf/020/a/index.xhtml", transfer);
                            //nts.uk.request.jump("/view/kaf/020/a/index.xhtml", transfer);
                            break;

                        case 14:
                            //KAF011-振休振出申請
                            vmNew.$jump.blank("at", "/view/cmm/045/a/index.xhtml?a=0");
                            //   nts.uk.request.jump("at", "/view/cmm/045/a/index.xhtml?a=0");
                            //   nts.uk.request.jump("view/cmm/045/a/index.xhtml?a=1", transfer);
                            break;
                        default:
                            break;
                    }
                });
                dfd.resolve();
            });
            dfd.promise();
        }

        clickButtonList(data) {

            let self = this;
            let source: any = $("#dpGrid").mGrid("dataSource");
            let rowItemSelect: any = _.find(source, function(value: any) {
                return value.id == data.id;
            })

            let dataShareCmm = {
				/** 期間開始日 */
				periodStartDate: __viewContext.vm.displayFormat() === 1 ? moment(__viewContext.vm.selectedDate()).format("YYYY/MM/DD") : moment(__viewContext.vm.dateRanger().startDate).format("YYYY/MM/DD"),
				/** 期間終了日 */
				periodEndDate: __viewContext.vm.displayFormat() === 1 ? moment(__viewContext.vm.selectedDate()).format("YYYY/MM/DD") : moment(__viewContext.vm.dateRanger().endDate).format("YYYY/MM/DD"),
				/** 事後出力 */
				postOutput: true,
				/** 事前出力 */
				preOutput: true,
				/** 申請一覧区分 */
				appListAtr: 0,
				/** 申請表示順 */
				appDisplayOrder: 0,
				/** 表の幅登録 */
				tableWidthRegis: false,
				/** 社員IDリスト */
				opListEmployeeID: [rowItemSelect.employeeId],
				/** 承認状況＿差戻 */
				opRemandStatus: false,
				/** 承認状況＿取消 */
				opCancelStatus: false,
				/** 承認状況＿承認済 */
				opApprovalStatus: false,
				/** 承認状況＿代行承認済 */
				opAgentApprovalStatus: false,
				/** 承認状況＿否認 */
				opDenialStatus: false,
				/** 承認状況＿未承認 */
				opUnapprovalStatus: true,
				/** 申請種類 */
				opAppTypeLst: [],
				/** 申請種類リスト */
				opListOfAppTypes: []
            }
            nts.uk.characteristics.remove("AppListExtractCondition").done(function() {
                parent.nts.uk.characteristics.save('AppListExtractCondition', dataShareCmm).done(function() {
                    nts.uk.localStorage.setItem('UKProgramParam', 'a=0');
                    nts.uk.request.jump("/view/cmm/045/a/index.xhtml");
                });
            });
        }

        reloadGrid() {
            var self = this;
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            //            self.destroyGrid();
            //            self.createSumColumn(self.dataAll());
            //            self.columnSettings(self.dataAll().lstControlDisplayItem.columnSettings);
            //            self.receiveData(self.dataAll());
            //            self.extractionData();
            //            self.loadGrid();
            self.extraction();
            nts.uk.ui.block.clear();
        }

        loadHeader(mode) {
            var self = this;
            let tempList = [];
            if (mode == 0) {
                _.forEach(self.employeeModeHeader, (header) => {
                    delete header.group;
                    if (header.constraint == null) {
                        delete header.constraint;
                    }
                    tempList.push(header);
                });
            } else if (mode == 1) {
                //self.displayProfileIcon(mode);
                _.forEach(self.dateModeHeader, (header) => {
                    if (header.constraint == null) {
                        delete header.constraint;
                    }
                    delete header.group;
                    tempList.push(header);
                });
            } else if (mode == 2) {
                //self.displayProfileIcon(mode);
                _.forEach(self.errorModeHeader, (header) => {
                    if (header.constraint == null) {
                        delete header.constraint;
                    }
                    delete header.group;
                    tempList.push(header);
                });
            }
            _.forEach(self.optionalHeader, (header) => {
                if (header.inputProcess != null && header.inputProcess != undefined) {
                    header.inputProcess = self.inputProcess;
                } else {
                    delete header.inputProcess;
                }
                if (header.constraint == null || header.constraint == undefined) {
                    delete header.constraint;
                } else {
                    header.constraint["cDisplayType"] = header.constraint.cdisplayType;
                    if (header.constraint.cDisplayType != null && header.constraint.cDisplayType != undefined) {
                        if (header.constraint.cDisplayType != "Primitive" && header.constraint.cDisplayType != "Combo") {
                            if (header.constraint.cDisplayType.indexOf("Currency") != -1) {
                                header["columnCssClass"] = "currency-symbol halign-right";
                                header.constraint["min"] = header.constraint.min;
                                header.constraint["max"] = header.constraint.max;
                            } else if (header.constraint.cDisplayType == "Clock") {
                                header["columnCssClass"] = "halign-right";
                                header.constraint["min"] = header.constraint.min;
                                header.constraint["max"] = header.constraint.max;
                            } else if (header.constraint.cDisplayType == "Integer") {
                                header["columnCssClass"] = "halign-right";
                            } else if (header.constraint.cDisplayType == "TimeWithDay") {
                                header.constraint["min"] = "-12:00";
                                header.constraint["max"] = "71:59"
                            }
                            delete header.constraint.primitiveValue;
                        } else {

                            if (header.constraint.cDisplayType == "Primitive") {
                                if (header.group == undefined || header.group.length == 0) {
                                    delete header.constraint.cDisplayType;
                                    if (header.constraint.primitiveValue.indexOf("AttendanceTime") != -1) {
                                        header["columnCssClass"] = "halign-right";
                                    }
                                    if (header.constraint.primitiveValue == "BreakTimeGoOutTimes" || header.constraint.primitiveValue == "WorkTimes" ||  header.constraint.primitiveValue == "AnyItemTimes" || header.constraint.primitiveValue == "AnyTimesMonth") {
                                        header["columnCssClass"] = "halign-right";
                                        header.constraint["decimallength"] = 2;
                                    }
                                } else {
                                    delete header.group[0].constraint.cDisplayType;
                                    delete header.group[0].values;
                                    delete header.constraint;
                                    delete header.group[1].constraint;
                                }
                            } else if (header.constraint.cDisplayType == "Combo") {
                                if (header.group[0].constraint.values == undefined && header.group[0].constraint != null) {
                                    header.group[0].constraint["min"] = 0;
                                    header.group[0].constraint["max"] = Number(header.group[0].constraint.primitiveValue);
                                    delete header.group[0].values;
                                } else {
                                    delete header.group[0].constraint.min;
                                    delete header.group[0].constraint.max;
                                }
                                header.group[0].constraint["cDisplayType"] = header.group[0].constraint.cdisplayType;
                                delete header.group[0].constraint.cdisplayType
                                delete header.group[0].constraint.primitiveValue;
                                delete header.constraint;
                                delete header.group[1].constraint;
                            }
                        }
                    }
                    if (header.constraint != undefined) {
                        delete header.constraint.cdisplayType;
                        delete header.constraint.values;
                    }
                }
                if (header.group != null && header.group != undefined) {
                    if (header.group.length > 0) {
                        if (header.group[0].constraint == undefined) delete header.group[0].constraint;
                        delete header.group[1].constraint;
                        delete header.group[0].group;
                        // delete header.key;
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
						if(header.group[1].key != "Name859" && header.group[1].key != "Name860"){
							delete header.group[1].onChange;
                        	delete header.group[1].inputProcess;
						}
                        delete header.inputProcess;

                        if (header.group[0].inputProcess != null && header.group[0].inputProcess != undefined) {
                            header.group[0].inputProcess = self.inputProcess;
                        } else {
                            delete header.group[0].inputProcess;
                        }
						
						if(header.group[1].key == "Name859" && header.group[1].key == "Name860"){
							if (!_.isNil(header.group[1].onChange)) {
								header.group[1].onChange = self.inputProcess;
							} else {
								delete header.group[1].onChange;
							}
						}

                        if (header.group[0].dataType == "String") {
                            header.group[0].onChange = self.search;
                            // delete header.group[0].onChange;
                            delete header.group[0].ntsControl;
                        } else {
                            delete header.group[0].onChange;
                            delete header.group[0].ntsControl;
                        }
                        delete header.group[1].group;
						header.key = _.replace(header.headerText, '<br/>', '');
						header.headerText = '<div style="max-height: 20px;">' + header.headerText + '</div>';
                    } else {
                        delete header.group;
						header.headerText = '<div style="max-height: 40px;">' + header.headerText + '</div>';
                    }
                } else {
					header.headerText = '<div style="max-height: 40px;">' + header.headerText + '</div>';
				}
                tempList.push(header);
            });
            self.headersGrid = tempList;
            return self.headersGrid;
        }

        pushDataEdit(evt, ui): InfoCellEdit {
            var self = this;
            var dataEdit: InfoCellEdit
            let edit: InfoCellEdit = _.find(self.editValue, function(item: any) {
                return item.rowID == ui.rowID && item.columnKey == ui.columnKey;
            });

            if (edit) {
                edit.value = ui.value;
                return edit;
            }
            else {
                let dateCon = _.find(self.dpData, (item: any) => {
                    return item.id == ui.rowID.substring(1, ui.rowID.length);
                });

                let employeeIdSelect: any = _.find(self.dailyPerfomanceData(), function(item: any) {
                    return item.id == ui.rowID.substring(1, ui.rowID.length);
                });
                dataEdit = new InfoCellEdit(ui.rowID, ui.columnKey, ui.value, 1, "", employeeIdSelect.employeeId, moment(dateCon.date).utc().toISOString(), 0);
                self.editValue.push(dataEdit);
                return dataEdit;
            }
        }

        displayProfileIcon(mode) {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (mode == 0) return;
//            if (self.showProfileIcon()) {
//                $("#dpGrid").mGrid("showColumn", "picture-person");
//            } else {
//                $("#dpGrid").mGrid("hideColumn", "picture-person");
//            }
        }

        dislayNumberHeaderText() {
            var self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            let headerText;
            _.each(self.optionalHeader, header => {
                if (header.headerText != "提出済みの申請" && header.headerText != "申請" && header.headerText != "申請一覧") {
                    if (header.group == undefined && header.group == null) {
                        if (self.showHeaderNumber()) {
							let id = header.key.substring(1, header.key.length);
							if(_.isNaN(_.toNumber(id))) {
								headerText = '<div style="max-height: 40px;">' + header.attendanceName + '</div>';
							} else {
								headerText = '<div style="max-height: 40px;">' + "[" + header.displayNumber + "]" + " " + header.attendanceName + '</div>';
							}
							$("#dpGrid").mGrid("headerText", header.key, headerText, false);
                        } else {
                            $("#dpGrid").mGrid("headerText", header.key, '<div style="max-height: 40px;">' + header.headerText + '</div>', false);
                        }
                    } else {
						if(!nts.uk.util.isNullOrUndefined(self.showHeaderNumber())) {
	                        if (self.showHeaderNumber()) {
	                            headerText = "[" + header.displayNumber + "]" + " " + header.attendanceName;
	                            $("#dpGrid").mGrid("headerText", header.key, '<div style="max-height: 20px;">' + headerText + '</div>', true);
								header.key = _.replace(headerText, '<br/>', '');
	                        } else {
	                            $("#dpGrid").mGrid("headerText", header.key, header.headerText, true);
								let keyHeader = _.replace(header.headerText, '<div style="max-height: 20px;">', '');
								keyHeader = _.replace(keyHeader, '</div>', '');
								keyHeader = _.replace(keyHeader, '<br/>', '');
								header.key = keyHeader;
	                        }
						}
                    }
                } else {
					header.headerText = '<div style="max-height: 40px;">' + header.headerText + '</div>';
				}
            });
        }

        displayNumberZero() {
            let self = this;
            if (!self.hasEmployee || self.hasErrorBuss) return;
            if (!self.displayWhenZero()) {
                $("#dpGrid").mGrid("hideZero", true)
            } else {
                $("#dpGrid").mGrid("hideZero", false)
            }
        }

        search(columnKey, rowId, val, valOld) {
            let dfd = $.Deferred();
            let i = 0;
            let data: any = $("#dpGrid").mGrid("dataSource");
            let rowItemSelect: any = _.find(data, function(value: any) {
                return value.id == rowId;
            })
            let typeGroup;
            let splitTypeGroup = rowItemSelect.typeGroup.split("|");
            if (splitTypeGroup != undefined) {
                typeGroup = _.find(splitTypeGroup, function(value: any) {
                    let splitData = value.split(":");
                    return "Code" + splitData[0] === columnKey;
                })
            }

            //remove error
            _.remove(__viewContext.vm.workTypeNotFound, error => {
                return error.columnKey == columnKey && error.rowId == rowId;
            })

            if (typeGroup != undefined && typeGroup != null) {
                let param = {
                    typeDialog: typeGroup.split(":")[1],
                    param: {
                        workTypeCode: typeGroup.split(":")[1] == 4 ? columnKey.substring(4, columnKey.length) : val,
                        employmentCode: rowItemSelect.employmentCode,
                        workplaceId: rowItemSelect.workplaceId,
                        date: rowItemSelect.dateDetail,
                        selectCode: val,
                        employeeId: rowItemSelect.employeeId,
                        itemId: Number(columnKey.substring(4, columnKey.length)),
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

        inputProcess(rowId, columnKey, value) {
            return __viewContext.vm.inputProcessCommon(rowId, columnKey, value);
        }

        inputProcessCommon(rowId, columnKey, value, changeSpr31 ?: boolean, changeSpr34 ?: boolean): JQueryPromise<any>{
            let dfd = $.Deferred(),
                keyId: any,
                valueError: any,
                dataChange: any,
                dataChageRow: any,
                errorGrid: any;
            __viewContext.vm.flagCalculation = false;
            $("#next-month").ntsError("clear");
            // get error grid
            errorGrid = $("#dpGrid").mGrid("errors");

            let hasErrorValidate = _.find(errorGrid, row => {
                return row.rowId == rowId;
            })
            _.remove(__viewContext.vm.listCheck28(), error => {
                return error.rowId == rowId;
            })
            if (columnKey.indexOf("Code") != -1) {
                keyId = columnKey.substring(4, columnKey.length);
                valueError = _.find(__viewContext.vm.workTypeNotFound, data => {
                    return data.columnKey == columnKey && data.rowId == rowId;
                });
                _.remove(__viewContext.vm.listCheck28(), error => {
                    return Number(error.itemId) == Number(keyId) && error.rowId == rowId;
                })
            } else {
                if (columnKey.indexOf("NO") != -1) {
                    keyId = columnKey.substring(2, columnKey.length);
                } else {
                    keyId = columnKey.substring(1, columnKey.length);
                }
				
				if (columnKey.indexOf("Name") != -1) {
					keyId = columnKey.substring(4, columnKey.length);
				}

            }

            let valueErrorRow = _.find(__viewContext.vm.workTypeNotFound, data => {
                    return data.rowId == rowId;
                });

            let itemChange = _.find(ITEM_CHANGE, function(o) { return o === Number(keyId); });
            if (itemChange == undefined) {
                dfd.resolve({ id: rowId, item: columnKey, value: value });
            }
            else {
                //                 let rowError = _.find(__viewContext.vm.listCheck28(), data => {
                //                    return data.rowId == rowId && columnKey != "A29";
                //                });
                if (valueError != undefined || hasErrorValidate != undefined || valueErrorRow != undefined) {
                    dfd.resolve({ id: rowId, item: columnKey, value: value })
                } else {
                    //nts.uk.ui.block.invisible();
                    nts.uk.ui.block.grayout();
                    let e = document.createEvent("HTMLEvents");
                    e.initEvent("mouseup", false, true);
                    $("#dpGrid")[0].dispatchEvent(e);

                    let dataTemp = _.find(__viewContext.vm.lstDataSourceLoad, (item: any) => {
                        return item.id == rowId;
                    });

                    let layoutAndType: any = _.find(__viewContext.vm.itemValueAll(), (item: any) => {
                        return item.itemId == keyId;
                    });
                    let item = _.find(__viewContext.vm.lstAttendanceItem(), (value) => {
                        return String(value.id) === keyId;
                    })
                    let valuePrimitive: any = __viewContext.vm.getPrimitiveValue(value, item.attendanceAtr);
                    let dataMap = new InfoCellEdit(rowId, Number(keyId), valuePrimitive, layoutAndType == undefined ? "" : layoutAndType.valueType, layoutAndType == undefined ? "" : layoutAndType.layoutCode, dataTemp.employeeId, dataTemp.dateDetail.utc().toISOString(), item.typeGroup, columnKey);

                    // get item change in row
                    dataChange = _.uniqWith($("#dpGrid").mGrid("updatedCells", true),  _.isEqual);
                    dataChageRow = _.map(_.filter(dataChange, row => {
                        return row.columnKey != "sign" && row.columnKey != "approval" && row.columnKey.indexOf("Name") == -1 && row.rowId == rowId && row.columnKey != columnKey;
                    }), allValue => {
                        let itemTemp, keyIdRow;
                        keyIdRow = Number(allValue.columnKey.replace(/\D/g, ""));
                        itemTemp = _.find(__viewContext.vm.lstAttendanceItem(), (value) => {
                            return value.id === keyIdRow;
                        })
                        let valueTemp: any = __viewContext.vm.getPrimitiveValue(allValue.value, itemTemp.attendanceAtr);
                        let layoutTemp: any = _.find(__viewContext.vm.itemValueAll(), (item: any) => {
                            return item.itemId == keyIdRow;
                        });
                        return new InfoCellEdit(rowId, keyIdRow, valueTemp, layoutTemp == undefined ? "" : layoutTemp.valueType, layoutTemp == undefined ? "" : layoutTemp.layoutCode, dataTemp.employeeId, dataTemp.dateDetail.utc().toISOString(), itemTemp.typeGroup, "USE");
                    });
                    dataChageRow.push(dataMap);


                    let param = {
                        //dailyEdits: __viewContext.vm.lstDomainEdit,
                        itemEdits: dataChageRow,
                        changeSpr31: changeSpr31,
                        changeSpr34: changeSpr34,
                        notChangeCell: false
                    };

                    //check cell has update
                    let itemSelectChange = _.find(dataChange, (itemTemp) =>{
                        return itemTemp.columnKey == columnKey &&  itemTemp.rowId == rowId;
                    })

                    if(_.isEmpty(itemSelectChange)){
                        param.notChangeCell = true;
                    }

					if(columnKey.indexOf("Name859") != -1 || columnKey.indexOf("Name860") != -1){
						if(param.itemEdits.length > 1){
							param.itemEdits = _.filter(param.itemEdits, (it : any) => {
								return it.columnKey == "Name859" || it.columnKey == "Name860";
							});
						}
					}

                    service.calcTime(param).done((value) => {
                        // workType, workTime not found

                        if (value.errorFindMaster28 || value.errorFindMaster29) {

                            let rowItemSelect: any = _.find($("#dpGrid").mGrid("dataSource"), function(value: any) {
                                return value.id == rowId;
                            })

                            if (value.errorFindMaster28) {
                                let typeError28 = _.find(__viewContext.vm.workTypeNotFound, data => {
                                    return data.columnKey == "Code28" && data.rowId == rowId;
                                });

                                if (typeError28 == undefined) {
                                    __viewContext.vm.workTypeNotFound.push({ columnKey: "Code28", rowId: rowId, message: nts.uk.resource.getMessage("Msg_1293"), employeeId: rowItemSelect.employeeId, date: moment(rowItemSelect.dateDetail) });
                                } else {
                                    typeError28.message = nts.uk.resource.getMessage("Msg_1293");
                                }
                            } else {
                                let typeError29 = _.find(__viewContext.vm.workTypeNotFound, data => {
                                    return data.columnKey == "Code29" && data.rowId == rowId;
                                });

                                if (typeError29 == undefined) {
                                    __viewContext.vm.workTypeNotFound.push({ columnKey: "Code29", rowId: rowId, message: nts.uk.resource.getMessage("Msg_1293"), employeeId: rowItemSelect.employeeId, date: moment(rowItemSelect.dateDetail) });
                                } else {
                                    typeError29.message = nts.uk.resource.getMessage("Msg_1293");
                                }
                            }
                        }else{
                            _.remove(__viewContext.vm.workTypeNotFound, dataTemp => {
                                return (dataTemp.columnKey == "Code28" ||  dataTemp.columnKey == "Code29")  && dataTemp.rowId == rowId;
                            });
                        }

                        //__viewContext.vm.lstDomainEdit = value.dailyEdits;
                        _.each(value.cellEdits, itemResult => {
                            $("#dpGrid").mGrid("updateCell", itemResult.id, itemResult.item, itemResult.value, true, true);
							if (itemResult.id.indexOf("Code")) {
								let data = __viewContext.vm.search(itemResult.item, itemResult.id, itemResult.value).done((v : any) => {
									let name = "Name" + itemResult.item.substring(4)
									$("#dpGrid").mGrid("updateCell", itemResult.id, name, v, true, true);
								});
							}
                        });
                        _.each(value.clearStates, itemResult => {
                            $("#dpGrid").mGrid("clearState", itemResult.rowId, itemResult.columnKey, itemResult.state);
                        })

                        nts.uk.ui.block.clear();
                        //dfd.resolve(value.cellEdits);
                        dfd.resolve({});
                    }).fail(error => {
                        __viewContext.vm.listCheck28.push({ itemId: keyId, layoutCode: error.message, rowId: rowId });
                        nts.uk.ui.block.clear();
                        nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                        let e = document.createEvent("HTMLEvents");
                        e.initEvent("mouseup", false, true);
                        $("#dpGrid")[0].dispatchEvent(e);
                        dfd.resolve({ id: rowId, item: columnKey, value: value });
                    });
                }
            }
            return dfd.promise();
        }
        getDataShare(data: any) {
            var self = this;
            var param = {
                dateRange: data.dateRangeParam ? {
                    startDate: moment(data.dateRangeParam.startDate).utc().toISOString(),
                    endDate: moment(data.dateRangeParam.endDate).utc().toISOString()
                } : null,
                displayFormat: 0,
                initScreen: 0,
                lstEmployee: [],
                formatCodes: self.formatCodes()
            };
        }

        navigateView() {
            //
            let self = this;
            // パラメータ「日別実績の修正の起動．表示期間」の有無をチェックする(check có hay không parameter)
            let yearMonth : any = null,
                closureId: any = null;
            if (_.isNil(self.shareObject().targetClosure)) {
                // パラメータ「日別実績の修正の状態．対象期間」 -> パラメータ「日別実績の修正の状態．表示形式」
                yearMonth = self.yearMonth();
                closureId = self. closureId
            } else {
                // パラメータ「日別実績の修正の起動．表示期間」 -> パラメータ「日別実績の修正の状態．表示期間」
                yearMonth = self.shareObject().yearMonthKDW004
                closureId = self. shareObject().targetClosure
            }

            let dataTransfer = {
                isKmwCall: false,
                closureId: closureId,
                yearMonth: yearMonth
            };

            if (self.shareObject().transitionDesScreen.indexOf("kmw") > 0) {
                dataTransfer = {
                    isKmwCall: true,
                    closureId: self.closureId,
                    yearMonth: self.yearMonth()
                };
                localStorage.setItem('isKmw', true);
            }
            let path: any = _.isEmpty(self.shareObject()) ? "" : self.shareObject().transitionDesScreen;
            nts.uk.request.jump("at", path, dataTransfer);
        }

        openKDL020Dialog() {
            let self = this;
			setShared('KDL020_DATA', [self.selectedEmployee()]);
			if ([self.selectedEmployee()].length > 1)
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 1040, height: 660 });
			else
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 730, height: 660 });
        }

        openKDL009Dialog() {
            var self = this;
            var param = {
                employeeIds: [self.selectedEmployee()],
                baseDate: moment(new Date()).toISOString().split("T")[0].replace('-', '').replace('-', '')
            };
            setShared('KDL009_DATA', param.employeeIds);
            if (param.employeeIds.length > 1) {
                modal("/view/kdl/009/a/index.xhtml",{width: 1100, height: 650})
            } else {
                modal("/view/kdl/009/a/index.xhtml",{width: 770, height: 650});
            }
        }

        openkdl029Dialog() {
            let self = this;
            setShared('KDL029_DATA', [self.selectedEmployee()]);
			if ([self.selectedEmployee()].length > 1)
				modal("/view/kdl/029/a/index.xhtml",{  width: 1060, height: 600 });
			else
				modal("/view/kdl/029/a/index.xhtml",{  width: 710, height: 600 });
        }

        openKDL005Dialog() {
            var self = this;
            setShared('KDL005_DATA', [self.selectedEmployee()]);
			if ([self.selectedEmployee()].length > 1){
				 nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml", {  width: 1160, height: 640 });
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml",{  width: 860, height: 640 });
            }
            
        }

		openKDL051Dialog() {
            let self = this;
            let param = {
                employeeIds: [self.selectedEmployee()],
                baseDate: new Date()
            }
            setShared('KDL051A_PARAM', param.employeeIds);
            nts.uk.ui.windows.sub.modal("/view/kdl/051/a/index.xhtml",{width: 650, height: 530});
        }

		openKDL052Dialog() {
            let self = this;
            let param = {
                employeeIds: [self.selectedEmployee()],
                baseDate: moment(new Date()).format("YYYY/MM/DD")
            }
			setShared('KDL052A_PARAM', param);// nts.uk.characteristics.OPEN_WINDOWS_DATA
            modal('/view/kdl/052/a/index.xhtml');
        }

    }

    export class AuthorityDetailModel {
        available1: KnockoutObservable<boolean> = ko.observable(false);
        available4: KnockoutObservable<boolean> = ko.observable(false);
        available2: KnockoutObservable<boolean> = ko.observable(false);
        available3: KnockoutObservable<boolean> = ko.observable(false);
        available5: KnockoutObservable<boolean> = ko.observable(false);
        available8: KnockoutObservable<boolean> = ko.observable(false);
        available9: KnockoutObservable<boolean> = ko.observable(false);
        available8Authority: KnockoutObservable<boolean> = ko.observable(false);
        available11: KnockoutObservable<boolean> = ko.observable(false);
        available12: KnockoutObservable<boolean> = ko.observable(false);
        available22: KnockoutObservable<boolean> = ko.observable(false);
        available24: KnockoutObservable<boolean> = ko.observable(false);
        available7: KnockoutObservable<boolean> = ko.observable(false);
        available23: KnockoutObservable<boolean> = ko.observable(false);
        available25: KnockoutObservable<boolean> = ko.observable(false);
        available17: KnockoutObservable<boolean> = ko.observable(false);
        available18: KnockoutObservable<boolean> = ko.observable(false);
        available19: KnockoutObservable<boolean> = ko.observable(false);
        available20: KnockoutObservable<boolean> = ko.observable(false);
        available21: KnockoutObservable<boolean> = ko.observable(false);
		available26Confirm: KnockoutObservable<boolean> = ko.observable(false);
        constructor(data: Array<DailyPerformanceAuthorityDto>, authority: any, showCheckbox: any, confirmEmployment: any) {
            var self = this;
            if (!data) return;
            this.available1(self.checkAvailable(data, 1));
            this.available4(self.checkAvailable(data, 4));
            this.available2(self.checkAvailable(data, 2));
            this.available3(self.checkAvailable(data, 3));
            this.available5(self.checkAvailable(data, 5));
            this.available8(self.checkAvailable(data, 8));
            this.available9(self.checkAvailable(data, 9));
            this.available12(self.checkAvailable(data, 12));
            this.available22(self.checkAvailable(data, 22));
            this.available24(self.checkAvailable(data, 24));
            this.available7(self.checkAvailable(data, 7));
            this.available23(self.checkAvailable(data, 23));
            this.available25(self.checkAvailable(data, 25));
            if (self.checkAvailable(data, 25) && showCheckbox) {
                $("#btn-signAll").css("visibility", "visible");
                $("#btn-releaseAll").css("visibility", "visible");

            } else {
                $("#btn-signAll").css("visibility", "hidden");
                $("#btn-releaseAll").css("visibility", "hidden");
            }
            // $("#btn-signAll").css("visibility", "hidden");
            this.available17(self.checkAvailable(data, 17));
            this.available18(self.checkAvailable(data, 18));
            this.available19(self.checkAvailable(data, 19));
            this.available20(self.checkAvailable(data, 20));
            this.available21(self.checkAvailable(data, 21));
            this.available8Authority(this.available8() && authority)
            this.available11(self.checkAvailable(data, 11));
			this.available26Confirm(self.checkAvailable(data, 26) && confirmEmployment);
        }
        checkAvailable(data: Array<DailyPerformanceAuthorityDto>, value: number): boolean {
            let self = this;
            var check = _.find(data, function(o) {
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
    export interface DailyPerformanceAuthorityDto {
        isDefaultInitial: number;
        roleID: string;
        functionNo: number;
        availability: boolean;
    }

    export class ReferenceVacation {
        yearHoliday20: KnockoutObservable<boolean> = ko.observable(false);
        yearHoliday19: KnockoutObservable<boolean> = ko.observable(false);
        substVacation: KnockoutObservable<boolean> = ko.observable(false);
        compensLeave: KnockoutObservable<boolean> = ko.observable(false);
        com60HVacation: KnockoutObservable<boolean> = ko.observable(false);
        authentication: KnockoutObservable<AuthorityDetailModel> = ko.observable(null);
        allVacation: KnockoutObservable<boolean> = ko.observable(false);

        constructor(yearHoliday: any, reserveHoliday: any, substVacation: any, compensLeave: any, com60HVacation: any, authentication: AuthorityDetailModel) {
            var self = this;
            this.yearHoliday20(yearHoliday && authentication.available20());
            this.substVacation(substVacation && authentication.available18());
            this.yearHoliday19(reserveHoliday && authentication.available19());
            this.compensLeave(compensLeave && authentication.available17());
            this.com60HVacation(com60HVacation && authentication.available21());
            this.allVacation(this.yearHoliday20() || this.substVacation() || this.yearHoliday19() || this.compensLeave() || this.com60HVacation());
        }
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
            var self = this;
            var selfParent = parent;
            let item: DPAttendanceItem;
            let codeName: any;
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            item = _.find(self.data, function(data) {
                return data.id == self.attendenceId;
            })
			// TypesMasterRelatedDailyAttendanceItem (日次の勤怠項目に関連するマスタの種類)
            switch (item.typeGroup) {
                case 1:
                    // KDL002
                    let dfd = $.Deferred();
                    let dataSource: any = $("#dpGrid").mGrid("dataSource");
                    let rowItemSelect: any = _.find(dataSource, function(value: any) {
                        return value.id == self.rowId();
                    })

                    let dataSourceOld: any = selfParent.formatDate(selfParent.dailyPerfomanceData());
                    let item28Old = _.find(dataSourceOld, valueData => {
                        return valueData.id == self.rowId() && valueData["Code28"] != undefined;
                    });

                    let param1 = {
                        typeDialog: 1,
                        param: {
                            workTypeCode: self.selectedCode(),
                            employmentCode: selfParent.employmentCode(),
                            workplaceId: "",
                            date: rowItemSelect.dateDetail,
                            selectCode: self.selectedCode(),
                            employeeId: rowItemSelect.employeeId,
                            itemId: item.id,
                            valueOld: item28Old == undefined ? null : item28Old["Code28"]
                        }
                    }
                    service.findAllCodeName(param1).done((data) => {
                        self.listCode([]);
                        self.listCode(_.map(data, 'code'));
                        setShared('KDL002_Multiple', false, true);
                        //all possible items
                        setShared('KDL002_AllItemObj', nts.uk.util.isNullOrEmpty(self.listCode()) ? [] : self.listCode(), true);
                        //selected items
                        setShared('KDL002_SelectedItemId', [self.selectedCode()], true);
                        modal('/view/kdl/002/a/index.xhtml', { title: '乖離時間の登録＞対象項目' }).onClosed(function(): any {
                            let lst = nts.uk.ui.windows.getShared('KDL002_SelectedNewItem');
                            if (lst != undefined && lst.length > 0 && lst[0].code != "") {
                                self.updateCodeName(self.rowId(), self.attendenceId, lst[0].name, lst[0].code, self.selectedCode());
                            } else {
                                self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            }
                        })
                        dfd.resolve();
                    });
                    dfd.promise();
                    break;
                case 2:
                    //KDL001
                    let dfd2 = $.Deferred();
                    let employeeIdSelect: any = _.find(selfParent.dailyPerfomanceData(), function(item: any) {
                        return item.id == self.rowId().substring(1, self.rowId().length);
                    });
                    let employee: any = _.find(selfParent.lstEmployee(), function(item: any) {
                        return item.id == employeeIdSelect.employeeId;
                    });
                    let param2 = {
                        typeDialog: 2,
                        param: {
                            workplaceId: employee.workplaceId
                        }
                    }
                    let dateParam2: any = null;
                    
                    let row: any = _.find(selfParent.dailyPerfomanceData(), item => item.id==self.rowId().substring(1));
                    let workplaceCode: any = null;
                    let workplaceId: any = null;
                    if(row){
                        dateParam2 = moment(row.date.substring(0,10)).format("YYYY/MM/DD");
                        let workplaceIDObj = _.find(row.cellDatas, (item: any) => item.columnKey=='Code623')
                        if(workplaceIDObj) {
                            workplaceCode = workplaceIDObj.value;	
                        }
                    };

                    let findWkpParam = {
                        companyId: __viewContext.user.companyId, 
                        wkpCode: workplaceCode, 
                        baseDate: dateParam2
                    };

                    $.when(service.findWplIDByCode(findWkpParam), service.findAllCodeName(param2)).done((res1, res2) => {
                        if (res1) {
                            workplaceId = res1.workplaceId;
                        }
                        self.listCode([]);
                            self.listCode(_.map(res2, 'code'));
                            setShared('kml001selectAbleCodeList', []);
                            setShared('kml001selectedCodeList', [self.selectedCode()]);
                            setShared('kml001multiSelectMode', false);
                            setShared('kdl00showNoSelectionRow', true);
                            setShared('kml001WorkPlaceId', workplaceId);
                            setShared('kml001BaseDate', dateParam2);
                            modal("/view/kdl/001/a/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {
                                let codes: any = nts.uk.ui.windows.getShared("kml001selectedCodeList");
                                if (codes) {
                                    codeName = _.find(res2, (item: any) => {
                                        return item.code == codes[0];
                                    });
                                    if (codeName != undefined && codes[0] != "") {
                                        self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                    } else {
                                        self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                                    }
                                }
                            });
                            dfd2.resolve();
                    })
                    // service.findWplIDByCode(findWkpParam).then((data: any) => {
                    //     if (data) {
                    //         workplaceId = data;
                    //     }
                    // }).then(() => {
                    //     service.findAllCodeName(param2).done((data: any) => {
                    //         self.listCode([]);
                    //         self.listCode(_.map(data, 'code'));
                    //         setShared('kml001selectAbleCodeList', []);
                    //         setShared('kml001selectedCodeList', [self.selectedCode()]);
                    //         setShared('kml001multiSelectMode', false);
                    //         setShared('kdl00showNoSelectionRow', true);
                    //         setShared('kml001WorkPlaceId', workplaceId);
                    //         setShared('kml001BaseDate', dateParam2);
                    //         modal("/view/kdl/001/a/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {
                    //             let codes: any = nts.uk.ui.windows.getShared("kml001selectedCodeList");
                    //             if (codes) {
                    //                 codeName = _.find(data, (item: any) => {
                    //                     return item.code == codes[0];
                    //                 });
                    //                 if (codeName != undefined && codes[0] != "") {
                    //                     self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                    //                 } else {
                    //                     self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                    //                 }
                    //             }
                    //         });
                    //         dfd2.resolve();
                    //     });
                    // });
                    dfd2.promise()
                    break;
                case 3:
                    //KDL010
                    let dfd3 = $.Deferred();
                    nts.uk.ui.block.invisible();
                    setShared('KDL010SelectWorkLocation', self.selectedCode());
                    modal("/view/kdl/010/a/index.xhtml", { dialogClass: "no-close" }).onClosed(() => {
                        var self = this;
                        var returnWorkLocationCD = nts.uk.ui.windows.getShared("KDL010workLocation");
                        if (returnWorkLocationCD !== undefined && returnWorkLocationCD != "") {
                            let dataKDL: any;
                            let param3 = {
                                typeDialog: 3
                            }
                            service.findAllCodeName(param3).done((data: any) => {
                                codeName = _.find(data, (item: any) => {
                                    return item.code == returnWorkLocationCD;
                                });
                                if (codeName != undefined) {
                                    self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                }
                                dfd3.resolve();
                            });
                        }
                        else {
                            if (returnWorkLocationCD == "") self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            dfd3.resolve();
                        }
                    });
                    dfd3.promise()
                    break;
                case 4:
                    //KDL032
                    let dfd4 = $.Deferred();
                    let no = DEVIATION_REASON_MAP[self.attendenceId];
                    nts.uk.ui.block.invisible();
                    let dataSetShare = {
                        reasonCD: self.selectedCode(),
                        divergenceTimeID: no
                    };
                    setShared('KDL032', dataSetShare);
                    modal("/view/kdl/032/a/index.xhtml", { dialogClass: "no-close" }).onClosed(() => {
                        var self = this;
                        var returnData = nts.uk.ui.windows.getShared("ReturnData");
                        if (returnData !== undefined && returnData != "") {
                            let dataKDL: any;
                            let param3 = {
                                typeDialog: 4,
                                param: {
                                    workTypeCode: no,
                                    selectCode: self.selectedCode()
                                }
                            };
                            service.findAllCodeName(param3).done((data: any) => {
                                let codeName = _.find(data, (item: any) => {
                                    return item.code == returnData && item.id == "" + no;
                                });
                                self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                dfd4.resolve();
                            });
                        }
                        else {
                            if (returnData == "") self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            nts.uk.ui.block.clear();
                            dfd4.resolve();
                        }
                    });
                    dfd4.promise()
                    break;
                case 5:
                    //CDL008
                    let dateCon = selfParent.displayFormat() === 1 ? moment(selfParent.selectedDate()) : moment(selfParent.dateRanger().endDate).utc().toISOString();

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
                        //                        setShared('inputCDL008', {
                        //                            selectedCodes: codeName == undefined ? "" : codeName.id,
                        //                            baseDate: dateCon,
                        //                            isMultiple: false
                        //                        }, true);
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
                case 6:
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
                case 7:
                    //KCP003
                    let dateCon7 = selfParent.displayFormat() === 1 ? moment(selfParent.selectedDate()) : moment(selfParent.dateRanger().endDate).utc().toISOString();
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
                case 8:
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

                case 14:
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
				case 15:
                    //KDL012
                    let dfd15 = $.Deferred();
					let dateParam15: any = null;
					let currentRow: any = _.find(selfParent.dailyPerfomanceData(), item => item.id==self.rowId().substring(1));
					if(currentRow){
						dateParam15 = currentRow.date;
					};
					let workFrameNoSelection: any = null;
					let keyList = _.keys(WORK_FRAME_MAP);
					for(let itemTaskKey in keyList) {
						if(_.includes(_.get(WORK_FRAME_MAP, keyList[itemTaskKey]), parseInt(self.attendenceId))) {
							workFrameNoSelection = parseInt(keyList[itemTaskKey]);
							break;	
						}
					}
                    nts.uk.ui.block.invisible();
                    let taskCode = self.selectedCode() == '' ? null : self.selectedCode();
                    let paramsKDL012 = {
						isMultiple: false,
				        showExpireDate: false,
				        referenceDate: dateParam15,
				        workFrameNoSelection,
				        selectionCodeList: [self.selectedCode()],
                        sid: selfParent.selectedEmployee(),
                        taskCode
                    };
                    setShared('KDL012Params', paramsKDL012);
                    modal("/view/kdl/012/index.xhtml", { dialogClass: "no-close" }).onClosed(() => {
                        var self = this;
                        var returnData = nts.uk.ui.windows.getShared("KDL012Output");
                        if (returnData !== undefined && returnData != "") {
                            let dataKDL: any;
                            let param3 = {
                                typeDialog: 15,
								param: {
									itemId: self.attendenceId
								}
                            };
                            service.findAllCodeName(param3).done((data: any) => {
                                let codeName = _.find(data, (item: any) => {
                                    return item.code == returnData;
                                });
                                self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                                dfd15.resolve();
                            });
                        }
                        else {
                            if (returnData == "") self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                            __viewContext.vm.clickCounter.clickLinkGrid = false;
                            nts.uk.ui.block.clear();
                            dfd15.resolve();
                        }
                    });
                    dfd15.promise()
                    break;
				case 19 :
				// KDL013 - WORK_SUPPLEMENT_OPT(19,"作業補足選択肢");
                let dfd19 = $.Deferred();
				let baseDate : any = null;
				let rowSelect: any = _.find(selfParent.dailyPerfomanceData(), item => item.id==self.rowId().substring(1));
				
				if(rowSelect){
					baseDate = rowSelect.date;
				};
				
				setShared('KDL013Params', {
                        baseDate: moment(baseDate).format("YYYY/MM/DD"),
						atdId : item.id,
                        selectedCode: self.selectedCode()
                 	}, true);

				modal('/view/kdl/013/a/index.xhtml').onClosed(function(): any {
					let returnData = nts.uk.ui.windows.getShared('KDL013ParamsReturn');
                    if (returnData !== undefined && returnData != "") {
                        let dataKDL: any;
                        let param3 = {
                            typeDialog: 19,
                            param: {
                                itemId: item.id ,
                                date: moment(baseDate).utc().toISOString()
                            }
                        };
                        service.findAllCodeName(param3).done((data: any) => {
                            let codeName = _.find(data, (item: any) => {
                                return item.code == returnData;
                            });
                            self.updateCodeName(self.rowId(), self.attendenceId, codeName.name, codeName.code, self.selectedCode());
                            dfd19.resolve();
                        });
                    }
                    else {
                        if (returnData == "") self.updateCodeName(self.rowId(), self.attendenceId, getText("KDW003_82"), "", self.selectedCode());
                        __viewContext.vm.clickCounter.clickLinkGrid = false;
                        nts.uk.ui.block.clear();
                        dfd19.resolve();
                    }
					__viewContext.vm.clickCounter.clickLinkGrid = false;
                    nts.uk.ui.block.clear();
				});
                dfd19.promise();
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
                __viewContext.vm.inputProcess(rowId, "Code" + itemId, code).done(value => {
                    _.each(value.cellEdits, itemResult => {
                        $("#dpGrid").mGrid("updateCell", itemResult.id, itemResult.item, itemResult.value, true, true);
                    })
                    nts.uk.ui.block.clear();
                    dfd.resolve();
                });
                $("#dpGrid").mGrid("updateCell", rowId, "Name" + itemId, name)
                $("#dpGrid").mGrid("updateCell", rowId, "Code" + itemId, code);
            }
            return dfd.promise();
        }
    }

    export interface DPAttendanceItem {
        id: string;
        name: string;
        displayNumber: number;
        userCanSet: boolean;
        lineBreakPosition: number;
        attendanceAtr: number;
        /*DUTY(1, "勤務種類"),
        WORK_HOURS(2, "就業時間帯"),
        SERVICE_PLACE(3, "勤務場所"),
        REASON(4, "乖離理由"),
        WORKPLACE(5, "職場"),--
        CLASSIFICATION(6, "分類")
        POSSITION(7, "職位")--
        EMPLOYMENT(8, "雇用区分") */
        typeGroup: number;
    }

    class InfoCellEdit {
        rowId: any;
        itemId: any;
        value: any;
        valueType: any;
        layoutCode: string;
        employeeId: string;
        date: any;
        typeGroup: number;
        columnKey: string;
        constructor(rowId: any, itemId: any, value: any, valueType: any, layoutCode: string, employeeId: string, date: any, typeGroup: number, columnKey?: string) {
            this.rowId = rowId;
            this.itemId = itemId;
            this.value = value;
            this.valueType = valueType;
            this.layoutCode = layoutCode;
            this.employeeId = employeeId;
            this.date = date;
            this.typeGroup = typeGroup;
            this.columnKey = columnKey;
        }
    }

    class DataHoliday {
        dispCompensationDay: boolean;
        dispCompensationTime: boolean;
        dispSubstitute: boolean;
        dispAnnualDay: boolean;
        dispAnnualTime: boolean;
        dispReserve: boolean;
		dispChildCare: string;
		dispLongTermCare: string;
        compensationDay: string;
        compensationTime: string;
        substitute: string;
        annualDay: string;
        annualTime: string;
        reserve: string;
		childCareValue: string;
		longTermCareValue: string;

        dispSysDate: string = getText("KDW003_121", [moment(new Date()).format("YYYY/MM/DD")]);
        dispNextGrantDate: string;

		dispAnnualDayUI: KnockoutObservable<boolean> = ko.observable(false);
		displayAllUI: KnockoutObservable<boolean> = ko.observable(false);

        constructor(annualLeave: any, reserveLeave: any, compensatoryLeave: any, substitutionLeave: any, nextGrantDate: any, grantDays: any, childCareVacation: any, longTermCareVacation: any) {

            this.dispNextGrantDate = nextGrantDate != null ? getText("KDW003_122", [nextGrantDate, grantDays]) : getText("KDW003_123");
            this.dispCompensationDay = compensatoryLeave == null ? false : compensatoryLeave.manageCompenLeave;
            this.dispCompensationTime = compensatoryLeave == null ? false : compensatoryLeave.manageTimeOff;
            this.dispSubstitute = substitutionLeave == null ? false : substitutionLeave.manageAtr;
            this.dispAnnualDay = annualLeave == null ? false : annualLeave.manageYearOff;
            this.dispAnnualTime = annualLeave == null ? false : annualLeave.manageTimeOff;
            this.dispReserve = reserveLeave == null ? false : reserveLeave.manageRemainNumber;
			this.dispChildCare = childCareVacation == null ? false : childCareVacation.manageNursing;
			this.dispLongTermCare = longTermCareVacation == null ? false : longTermCareVacation.manageNursing;
            // if (this.dispCompensationDay && compensatoryLeave.compenLeaveRemain != null) {
            //     this.compensationDay = getText("KDW003_8", [compensatoryLeave.compenLeaveRemain]);
            //     if (compensatoryLeave.compenLeaveRemain < 0)
            //         $("#fixed-table td.remain-compen-day").css("color", "#ff0000");
            //     else
            //         $("#fixed-table td.remain-compen-day").css("color", "");
            // } else
            //     this.compensationDay = "";
            // if (this.dispCompensationTime && compensatoryLeave.timeRemain != null) {
            //     this.compensationTime = nts.uk.time.format.byId("Time_Short_HM", compensatoryLeave.timeRemain);
            //     if (compensatoryLeave.timeRemain < 0)
            //         $("#fixed-table td.remain-compen-time").css("color", "#ff0000");
            //     else
            //         $("#fixed-table td.remain-compen-time").css("color", "");
            // } else
            //     this.compensationTime = "";
            if (this.dispCompensationDay) {
                this.compensationDay = this.remainCompensation(compensatoryLeave.compenLeaveRemain, compensatoryLeave.timeRemain, compensatoryLeave.manageTimeOff);
            }
            if (this.dispSubstitute && substitutionLeave.holidayRemain != null) {
                this.substitute = getText("KDW003_8", [substitutionLeave.holidayRemain]);
                if (substitutionLeave.holidayRemain < 0)
                    $("#fixed-table td.remain-subst-day").css("color", "#ff0000");
                else
                    $("#fixed-table td.remain-subst-day").css("color", "#06c");
            } else
                this.substitute = "";
            if (this.dispAnnualDay && annualLeave.annualLeaveRemain != null) {
                this.annualDay = getText("KDW003_8", [annualLeave.annualLeaveRemain]);
                if (annualLeave.annualLeaveRemain < 0)
                    $("#fixed-table td.remain-annual-day").css("color", "#ff0000");
                else
                    $("#fixed-table td.remain-annual-day").css("color", "#06c");
            } else
                this.annualDay = "";
            if (this.dispAnnualDay) {
                if (annualLeave.timeRemain) {
                    let timeString = nts.uk.time.format.byId("Time_Short_HM", annualLeave.timeRemain);
                    if (annualLeave.annualLeaveRemain != null) {
                        this.annualDay = getText("KDW003_132", [annualLeave.annualLeaveRemain, timeString]);
                        if (annualLeave.annualLeaveRemain < 0)
                            $("#fixed-table td.remain-annual-day").css("color", "#ff0000");
                        else if (annualLeave.annualLeaveRemain == 0 && annualLeave.timeRemain < 0) 
                            $("#fixed-table td.remain-annual-day").css("color", "#ff0000");
                        else
                            $("#fixed-table td.remain-annual-day").css("color", "#06c");
                    }
                } else {
                    this.annualDay = getText("KDW003_8", [annualLeave.annualLeaveRemain]);
                }
            }
            if (this.dispReserve && reserveLeave.remainNumber != null) {
                this.reserve = getText("KDW003_8", [reserveLeave.remainNumber]);
                if (reserveLeave.remainNumber < 0)
                    $("#fixed-table td.remain-reserve-day").css("color", "#ff0000");
                else
                    $("#fixed-table td.remain-reserve-day").css("color", "#06c");
            } else
                this.reserve = "";

			if (this.dispChildCare) {
				if (_.isNull(childCareVacation.remainDays)) childCareVacation.remainDays = 0;
				if (childCareVacation.remainTime != 0) {
					this.childCareValue = getText("KDW003_132", [childCareVacation.remainDays, nts.uk.time.format.byId("Time_Short_HM", childCareVacation.remainTime)]);	
				} else {
					this.childCareValue = getText("KDW003_8", [childCareVacation.remainDays]);	
				}
                if (childCareVacation.remainDays < 0 || (childCareVacation.remainDays == 0 && childCareVacation.remainTime < 0))
                    $("#fixed-table td.remain-childCare-day").css("color", "#ff0000");
                else
                    $("#fixed-table td.remain-childCare-day").css("color", "#06c");
            } else
                this.childCareValue = "";

			if (this.dispLongTermCare) {
				if (_.isNull(longTermCareVacation.remainDays)) longTermCareVacation.remainDays = 0;
				if (longTermCareVacation.remainTime != 0) {
					this.longTermCareValue = getText("KDW003_132", [longTermCareVacation.remainDays, nts.uk.time.format.byId("Time_Short_HM", longTermCareVacation.remainTime)]);	
				} else {
					this.longTermCareValue = getText("KDW003_8", [longTermCareVacation.remainDays]);	
				}
                if (longTermCareVacation.remainDays < 0 || (longTermCareVacation.remainDays == 0 && longTermCareVacation.remainTime < 0))
                    $("#fixed-table td.remain-longTermCare-day").css("color", "#ff0000");
                else
                    $("#fixed-table td.remain-longTermCare-day").css("color", "#06c");
            } else
                this.longTermCareValue = "";

			this.dispAnnualDayUI(this.dispAnnualDay);
			this.displayAllUI(this.dispCompensationDay || this.dispSubstitute || this.dispAnnualDay || (this.dispAnnualDay && this.dispReserve));
        }

        public remainCompensation(day: any, time: any, manage: any) {
            let output = "";
            let timeString = nts.uk.time.format.byId("Time_Short_HM", time);
            let dayString = getText("KDW003_8", [day.toString()]);
            if (time) {
                if (time < 0)
                    $("#fixed-table td.remain-compen-day").css("color", "#ff0000");
                else {
                    $("#fixed-table td.remain-compen-day").css("color", "#06c");
                }
                output = timeString;
            } else if (day) {
                if (day < 0)
                    $("#fixed-table td.remain-compen-day").css("color", "#ff0000");
                else {
                    $("#fixed-table td.remain-compen-day").css("color", "#06c");
                }
                output = dayString;
            } else {
                if (manage) {
                    output = timeString; 
                    $("#fixed-table td.remain-compen-day").css("color", "#06c");
                } else {
                    output = dayString;
                    $("#fixed-table td.remain-compen-day").css("color", "#06c");
                }
            }

            return output;
        }
    }

    class ShareObject {
        changePeriodAtr: boolean; //期間を変更する có cho thay đổi khoảng thời gian hay không
        errorRefStartAtr: boolean; //エラー参照を起動する có hiện mode lỗi hay ko
        initClock: any; //打刻初期値-社員ID Optional giờ check tay SPR
        lstEmployeeShare: any; //社員一覧 社員ID danh sách nhân viên được chọn
        screenMode: any; //画面モード-日別実績の修正の画面モード  mode approval hay ko
        targetClosure: any; //処理締め-締めID targetClosure lấy closureId
        transitionDesScreen: any; //遷移先の画面 - Optional //truyền từ màn hình nào sang

        dateTarget: any; //日付別で起動- Optional ngày extract mode 2
        displayFormat: any; //表示形式 mode hiển thị
        individualTarget: any; //個人別で起動 ngày bắt đầu
        lstExtractedEmployee: any;//抽出した社員一覧
        startDate: any;//期間 khoảng thời gian
        endDate: any;//期間 khoảng thời gian
        startDateKDW004: any;//期間 khoảng thời gian KDW004
        endDateKDW004: any;//期間 khoảng thời gian KDW004
        yearMonth: any;
        yearMonthKDW004: any;
        constructor() {
        }
        mapDataShare(dataInit: any, dataExtract: any, dataSPR: any) : boolean {
            var self = this,
                checkDataShare = false;
            self.initFromScreenOther = true;
            if (dataInit != undefined) {
                self.changePeriodAtr = (dataInit.changePeriodAtr == null || dataInit.changePeriodAtr == undefined) ? true : dataInit.changePeriodAtr;
                self.errorRefStartAtr = dataInit.errorRefStartAtr;
                self.initClock = dataInit.initClock == undefined ? null : new SPRTime(dataInit.initClock);
                self.lstEmployeeShare = dataInit.lstEmployee;
                self.screenMode = dataInit.screenMode;
                self.targetClosure = dataInit.targetClosure;
                self.transitionDesScreen = dataInit.transitionDesScreen;
                if (self.screenMode == ScreenMode.APPROVAL) {
                    $("#ccg001").hide();
                }
                checkDataShare = true;
                self.yearMonth = dataInit.yearMonth;
            }
            if (dataExtract != undefined) {
                self.dateTarget = moment(dataExtract.dateTarget, "YYYY/MM/DD");
                self.displayFormat = dataExtract.displayFormat;
                self.individualTarget = dataExtract.individualTarget;
                self.lstExtractedEmployee = dataExtract.lstExtractedEmployee; //bug 109626
                self.startDate = moment(dataExtract.startDate, "YYYY/MM/DD");
                self.endDate = moment(dataExtract.endDate, "YYYY/MM/DD");
                self.startDateKDW004 =  dataExtract.startDateKDW004;
                self.endDateKDW004 =  dataExtract.endDateKDW004;
                checkDataShare = true;
                self.yearMonth = dataExtract.yearMonth;
                self.yearMonthKDW004 = dataExtract.yearMonthKDW004;
            }

            if (dataSPR != undefined) {
                self.changePeriodAtr = true;
                self.errorRefStartAtr = true;
                self.initClock = new SPRTime({ dateSpr: dataSPR.dateTarget, canEdit: true, employeeId: dataSPR.employeeId, liveTime: dataSPR.liveTime, goOut: dataSPR.goOut });
                self.lstEmployeeShare = [];
                self.screenMode = dataSPR.screenMode;
                self.targetClosure = null;
                self.transitionDesScreen = null;
                self.dateTarget = moment(dataSPR.dateTarget, "YYYY/MM/DD");
                self.displayFormat = dataExtract.displayFormat;
                self.individualTarget = null;
                self.lstExtractedEmployee = [];
                self.startDate = moment(dataSPR.dateTarget, "YYYY/MM/DD");
                self.endDate = moment(dataSPR.dateTarget, "YYYY/MM/DD");
                checkDataShare = true;
            }
            return checkDataShare;
        }
    }

    class SPRTime {
        dateSpr: any
        canEdit: any;
        employeeId: any;
        //退勤打刻
        liveTime: any;
        //出勤打刻
        goOut: any;
        constructor(data: any) {
            this.dateSpr = moment(data.dateSpr, "YYYY/MM/DD");
            this.canEdit = data.canEdit;
            this.employeeId = data.employeeId;
            this.liveTime = data.liveTime == undefined ? "" : data.liveTime;
            this.goOut = data.goOut == undefined ? "" : data.goOut;
        }
    }

    class FlexShortage {
        shortageTime: KnockoutObservable<any> = ko.observable();
        nextMonthTransferredMoneyTime: KnockoutObservable<string> = ko.observable("");

        noOfHolidays: KnockoutObservable<any> = ko.observable("");
        nameNoOfHolidays: any;
        noOfHolidaysOld: KnockoutObservable<any> = ko.observable("");

        absentDeductionTime: KnockoutObservable<any> = ko.observable();
        nameAbsentDeductionTime: any;
        absentDeductionTimeOld: KnockoutObservable<any> = ko.observable();

        initLoad = 0;
        messageRed: KnockoutObservable<any> = ko.observable();
        messageRedValue: KnockoutObservable<any> = ko.observable();
        messageNoForward: KnockoutObservable<any> = ko.observable();

        constructor(parent: any, dataCalc: CalcFlex, breakTimeDay: BreakTimeDay) {
            let self = this;
            this.nameNoOfHolidays = getText('Com_PaidHoliday');
            this.nameAbsentDeductionTime = getText('KDW003_79');
            self.bindDataChange(dataCalc, "", "", [], false);
            self.noOfHolidays.subscribe(val => {
                if (self.initLoad > 0) {
                    //if (Number(val) != Number(self.noOfHolidaysOld())) {
                    self.calc();
                    //}
                }
            });

            self.absentDeductionTime.subscribe(val => {
                if (self.initLoad > 0) {
                    //if (Number(val) != Number(self.absentDeductionTimeOld())) {
                    self.calc();
                    //}
                }
            });
        };

        bindDataChange(dataCalc: CalcFlex, redConditionMessage: any, messageNotForward: any, error: boolean, lstError: any, showLstError: boolean) {
            if (!dataCalc) {
                return;
            }

            let self = this,
                val18 = dataCalc.value18,
                val19 = dataCalc.value19,
                val21 = dataCalc.value21,
                val189 = dataCalc.value189,
                val190 = dataCalc.value190,
                val191 = dataCalc.value191;
            //set old value
            self.noOfHolidaysOld(val189);
            self.absentDeductionTimeOld(val190);

            self.messageRed(getText('KDW003_80', [redConditionMessage]));
            self.messageRedValue(redConditionMessage);
            self.messageNoForward(messageNotForward);

            //フレックス不足(内前月繰越)
            //self.shortageTime(getText("KDW003_89", [self.convertToHours((Number(val191) + Number(val21))), self.convertToHours(Number(val21))]));
            self.shortageTime(getText("KDW003_89", [self.convertToHours(Number(val191)), self.convertToHours(Number(val19) - Number(val191) <= 0 ? Number(val19) : Number(val191))]));

            //翌月繰越
            self.nextMonthTransferredMoneyTime(getText("KDW003_111", [self.convertToHours((Number(val18) + Number(val21)))]));
            //年休
            self.noOfHolidays(Number(val189));
            //欠勤控除
            self.absentDeductionTime(Number(val190));
            if(error){
               __viewContext.vm.errorBackGroundFlex = true;
            }else{
                __viewContext.vm.errorBackGroundFlex = false;
            }
            if (error && __viewContext.vm.canFlex()) {
                $("#next-month").attr('style', 'background-color: red !important');
            } else {
                $("#next-month").attr('style', 'background-color: white !important');
            }
            if (showLstError || !_.isEmpty(lstError)) {
                self.displayListError(lstError, showLstError);
            }
            self.initLoad = 1;
        }

        binDataChangeError(dataCalc: CalcFlex) {
            if(_.isEmpty(dataCalc)){
                return;
            }
            let self = this,
                val18 =  dataCalc.value18 == null ? 0 : dataCalc.value18.value,
                val19 = dataCalc.value19 == null ? 0 : dataCalc.value19.value,
                val21 = dataCalc.value21 == null ? 0 : dataCalc.value21.value,
                val189 = dataCalc.value189 == null ? 0 : dataCalc.value189.value,
                val190 = dataCalc.value190 == null ? 0 : dataCalc.value190.value,
                val191 = dataCalc.value191 == null ? 0 : dataCalc.value191.value;
            //フレックス不足(内前月繰越)
            //self.shortageTime(getText("KDW003_89", [self.convertToHours((Number(val191) + Number(val21))), self.convertToHours(Number(val21))]));
              self.shortageTime(getText("KDW003_89", [self.convertToHours(Number(val191)), self.convertToHours(Number(val19) - Number(val191) <= 0 ? Number(val19) : Number(val191))]));
            //翌月繰越
            self.nextMonthTransferredMoneyTime(getText("KDW003_111", [self.convertToHours((Number(val18) + Number(val21)))]));
        }

        calc(): JQueryPromise<any> {
            let dfd = $.Deferred();
            let self = this;
            $("#next-month").ntsError("clear");
            if ((__viewContext.vm.canFlex())) {
                __viewContext.vm.valueUpdateMonth = {};
                if (__viewContext.vm.itemMonth.length > 0) {
                    let dataFlexUpdate = __viewContext.vm.itemValueMonthParent,
                        items = _.map(__viewContext.vm.itemMonth, value => {
                            if (value.itemId == 189) {
                                value.value = self.noOfHolidays();
                            }
                            if (value.itemId == 190) {
                                value.value = self.absentDeductionTime();
                            }
                            return value;
                        });

                    if (dataFlexUpdate) {
                        dataFlexUpdate["items"] = items;
                        dataFlexUpdate["redConditionMessage"] = self.messageRedValue();
                        __viewContext.vm.valueUpdateMonth = dataFlexUpdate;
                    }
                } else {
                    __viewContext.vm.valueUpdateMonth["redConditionMessage"] = self.messageRedValue();
                }
                dfd.resolve();
            } else {
                dfd.resolve();
            }
            return dfd.promise();
        }

        natural(value: any): number {
            let temp = Number(value);
            return temp < 0 ? 0 - Math.floor(Math.abs(temp)) : Math.floor(temp);
        }

        convertToHours(value: any): string {
            let self = this;
            let hours = value < 0 ? "-" + String(Math.floor(Math.abs(value / 60))) : String(Math.floor(value / 60));
            let minutes = String(Math.abs(value) % 60);
            if (Number(minutes) < 10) minutes = "0" + minutes;
            return hours + ":" + minutes;
        }

        displayListError(lstError: any, showLstError: boolean) {
            if (lstError.length == 0) {
                $("#next-month").ntsError("clear");
            } else {
                $("#next-month").ntsError("clear");
                _.forEach(lstError, value => {
                    //$("#next-month").ntsError("set", value.message, value.messageId);
                    __viewContext.vm.lstErrorFlex = [];
                    __viewContext.vm.lstErrorFlex.push({employeeId: "", message: value.message});
                });
            }
        }

        checkColorDetail(timeCheck: any, value: any, breakTimeDay: BreakTimeDay) {
            let self = this;
            let check1174 = false,
                check1175 = false;
            if (check1174 || check1175) {
                $("#next-month").attr('style', 'background-color: red !important');
            } else {
                $("#next-month").attr('style', 'background-color: white !important');
            }
            if (check1174) {
                $("#next-month").ntsError("set", nts.uk.resource.getMessage("Msg_1174", [timeCheck]), "Msg_1174");
            } else {
                if ($("#next-month").ntsError("hasError", "Msg_1174")) $("#next-month").ntsError("clearByCode", "Msg_1174");
            }
            if (check1175) {
                $("#next-month").ntsError("set", nts.uk.resource.getMessage("Msg_1175"), "Msg_1175");
            } else {
                if ($("#next-month").ntsError("hasError", "Msg_1175")) $("#next-month").ntsError("clearByCode", "Msg_1175");
            }
        }
    }

    class CalcFlex {
        value18: any;
        value19: any;
        value21: any;
        value189: any;
        value190: any;
        value191: any;
        constructor(value18: any, value19: any, value21: any, value189: any, value190: any, value191: any) {
            this.value18 = Number(value18.value);
            this.value19 = Number(value19.value);
            this.value21 = Number(value21.value);
            this.value189 = Number(value189.value);
            this.value190 = Number(value190.value);
            this.value191 = Number(value191.value);
        }
    }

    interface BreakTimeDay {
        day: any;
        am: any;
        pm: any;
    }

    export enum ScreenMode {
        //通常
        NORMAL = 0,
        //承認
        APPROVAL = 1
    }

    export enum SPRCheck {
        NOT_INSERT = 0,
        INSERT = 1,
        SHOW_CONFIRM = 2

    }

    class AgreementInfomation {
        agreementTime: KnockoutObservable<string> = ko.observable("");
        cssAgree: any;

        agreementExcess: KnockoutObservable<string> = ko.observable("");
        cssFrequency: any;

        showAgreement: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            // this.agreementTime = "";
            this.cssAgree = "";

            // this.agreementExcess = "";
            this.cssFrequency = "";

        }

        mapDataAgreement(data: any): void {
            this.showAgreement(data.showAgreement);
            if (!data.showAgreement) return;
            this.agreementTime((data.agreementTime36 == null || data.maxTime == null) ? "" : getText("KDW003_74", [data.agreementTime36, data.maxTime]));
            this.cssAgree = data.cssAgree;
            this.agreementExcess((data.excessFrequency == null || data.maxNumber == null) ? "" : getText("KDW003_76", [data.excessFrequency, data.maxNumber]));
            this.cssFrequency = data.cssFrequency;

            this.processState(data.cssAgree, data.cssFrequency);
        }

        processState(cssAgree: any, cssFrequency: any) {
            if (cssAgree != "" && cssAgree != null) {
                $('#agree-time').removeClass();
                $("#agree-time").addClass(cssAgree);
            } else {
                $('#agree-time').removeClass();
            }

            if (cssFrequency != "" && cssFrequency != null) {
                $("#agree-excess").removeClass();
                $("#agree-excess").addClass(cssFrequency);
            } else {
                $("#agree-excess").removeClass();
            }
        }
    }

    interface Characteristics {

        companyId: string;
        employeeId: string;

        showZero: boolean;
        showProfile: boolean;
        showNumberHeader: boolean;

        formatExtract: number;

        authenSelectFormat: any;

        moveMouse: number;
    }

    class CLickCount {
        clickLinkGrid: boolean;
        clickErrorRefer: boolean;
        constructor() {
            this.clickLinkGrid = false;
            this.clickErrorRefer = false;
        }
    }

    interface Params {
        pgid: string; //__viewContext.program.programId
        functionId: number;
        listEmployeeId: Array<string>;
        period: any; // {startDate: string, endDate: string};  {startDate: string 'YYYYMM', endDate: string 'YYYYMMDD'} only from the monthly correction to calling
        displayFormat: number;
    }

    class CellEdit {
        rowId: string;
        columnKey: number;
        constructor(rowId: string, columnKey: number) {
            this.rowId = rowId;
            this.columnKey = columnKey;
        }
    }

    interface IndentityMonth {
        show26: boolean;
        enableButton: boolean;
        hideAll: boolean;
    }

    enum DCErrorInfomation {

        NORMAL = 0,

        APPROVAL_NOT_EMP = 1,

        ITEM_HIDE_ALL = 2,

        NOT_EMP_IN_HIST = 3
    }

     enum ErrorAlarmClassification {
         /* エラー */
         ERROR = 0,
         /* アラーム */
         ALARM = 1,
         /* その他 */
         OTHER = 2
    }
}