module nts.uk.at.view.kmw003.a.viewmodel {
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
        dailyPerfomanceData: KnockoutObservableArray<any> = ko.observableArray([]);
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
        displayWhenZero: KnockoutObservable<boolean> = ko.observable(false);
        
        showProfileIcon: KnockoutObservable<boolean> = ko.observable(false);
        //ccg001 component: search employee
        ccg001: any;
        baseDate: KnockoutObservable<Date> = ko.observable(new Date());
        //kcp009 component: employee picker
        selectedEmployee: KnockoutObservable<any> = ko.observable(null);
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
        // date ranger component
        dateRanger: KnockoutObservable<any> = ko.observable(null);
       

        showButton: KnockoutObservable<AuthorityDetailModel> = ko.observable(null);

        

        employmentCode: KnockoutObservable<any> = ko.observable("");

        editValue: KnockoutObservableArray<InfoCellEdit> = ko.observableArray([]);
        
        itemValueAll: KnockoutObservableArray<any> = ko.observableArray([]);
        
        itemValueAllTemp: KnockoutObservableArray<any> = ko.observableArray([]);
        
        lockMessage: KnockoutObservable<any> = ko.observable("");

        dataHoliday: KnockoutObservable<DataHoliday> =  ko.observable(new DataHoliday("0","0","0","0","0","0"));
        

        comboColumns: KnockoutObservableArray<any> = ko.observableArray([{ prop: 'code', length: 1 },
            { prop: 'name', length: 2 }]);
        comboColumnsCalc: KnockoutObservableArray<any> = ko.observableArray([{ prop: 'code', length: 1 },
            { prop: 'name', length: 8 }]);        
        
        dataAll: KnockoutObservable<any> = ko.observable(null);
        hasLstHeader : boolean  =  true;
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
        monthlyParam : KnockoutObservable<any> = ko.observable(null);
        //Date YYYYMM picker
        yearMonth: KnockoutObservable<number>;
        //Combobox display actual time
        actualTimeOptionDisp: KnockoutObservableArray<any>;
        actualTimeSelectedCode: KnockoutObservable<number> = ko.observable(0);
        actualTimeDats: KnockoutObservableArray<any> = ko.observableArray([]);;
        actualTimeSelectedDat: KnockoutObservable<any> = ko.observable(null);
        constructor() {
            let self = this;
            self.yearMonth = ko.observable(Number(moment(new Date()).format("YYYYMM")));
            self.initLegendButton();
            //self.initActualTime();
            self.headersGrid = ko.observableArray([]);
            self.fixColGrid = ko.observableArray([]);
            
            self.monthlyParam({
                yearMonth: self.yearMonth,
                actualTime: null,
                initMode: self.initMode(),
                //抽出対象社員一覧
                lstEmployees: [],
                formatCodes: self.formatCodes(),
                dispItems: [],
                correctionOfDaily: null,
                errorCodes: [],
                lstLockStatus: []
            });
            
            self.actualTimeSelectedCode.subscribe(value => {
                self.actualTimeSelectedDat(self.actualTimeDats()[value]);
                //実績期間を変更する
                self.updateActualTime();
            });
            self.yearMonth.subscribe(value => {
                //期間を変更する
                self.updateDate(value);
            });
        }
        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            
            nts.uk.ui.block.invisible();
            nts.uk.ui.block.grayout();
            self.initScreen().done(() => {
                //  self.extraction();
                self.initCcg001();
                self.loadCcg001();
                nts.uk.ui.block.clear();
                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.dialog.alert(error.message);
                nts.uk.ui.block.clear();
                dfd.reject();
            });
            return dfd.promise();
        }
         /**********************************
         * Initialize Screen 
         **********************************/
        initScreen(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.startScreen(self.monthlyParam()).done((data) => {
                self.dataAll(data);
                self.itemValueAll(data.itemValues);
                self.receiveData(data);
                /*********************************
                 * Screen data
                 *********************************/
                //Closure name
                self.closureName(data.closureName);
                //date process
                self.yearMonth(data.processDate);
                //actual times
                self.actualTimeDats(data.lstActualTimes);
                self.initActualTime();
                //comment
                self.comment(data.comment != null ? '■ ' + data.comment : null);
                /*********************************
                 * Grid data
                 *********************************/
                // Fixed Header
                self.setFixedHeader(data.lstFixedHeader);
                self.extractionData();
                self.loadGrid();      
                self.employmentCode(data.employmentCode);
                self.lstEmployee(_.orderBy(data.lstEmployee, ['code'], ['asc']));
                
                let employeeLogin: any = _.find(self.lstEmployee(), function(data){
                    return data.loginUser == true;
                });
                self.employIdLogin = employeeLogin;
                //画面項目の非活制御をする
                self.showButton = ko.observable(new AuthorityDetailModel(data.authorityDto, data.actualTimeState, self.initMode(),data.formatPerformance.settingUnitType ));
                self.showButton().enable_multiActualTime(data.lstActualTimes.length > 1);
                
                     
                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.dialog.alert(error.message);
                dfd.reject();
            });
            return dfd.promise();
        }
        initLegendButton() {
            let self = this;
            self.legendOptions = {
                items: [
                    { colorCode: '#94B7FE', labelText: '手修正（本人）' },
                    { colorCode: '#CEE6FF', labelText: '手修正（他人）' },
                    { colorCode: '#DDDDD2', labelText: nts.uk.resource.getText("KMW003_33") },
                ]
            };
        }
        initActualTime() {
            let self = this;
            if(self.actualTimeDats && self.actualTimeDats().length > 0){
                self.actualTimeOptionDisp = ko.observableArray();          
                for (let i = 0; i < self.actualTimeDats().length; i++) {
                    self.actualTimeOptionDisp.push({ code: i, name: self.actualTimeDats()[i].startDate + "～" + self.actualTimeDats()[i].endDate });
                }
            }            
            self.actualTimeSelectedCode(0);
        }
         /*********************************/
        receiveData(data) {
            let self = this;
            self.dpData = data.lstData;
            self.cellStates(data.lstCellState);
            self.optionalHeader = data.lstControlDisplayItem.lstHeader;
            self.sheetsGrid(data.lstControlDisplayItem.lstSheet);
            self.sheetsGrid.valueHasMutated();
        }
        
        getPrimitiveValue(value : any, atr: any): string{
            let self = this;
            let valueResult : string = "";
            if (atr != undefined && atr != null) {
                    if (atr == 6) {
                        // Time
                        valueResult = value == "" ? null : String(self.getHoursAll(value));
                    } else if(atr == 5){
                         valueResult =  value == "" ? null : String(self.getHoursTime(value));
                    } else{
                         valueResult = value;
                    }
            } else {
                valueResult = value;
            }
            return valueResult;
        }
        
        getHours(value: any) : number{
            return Number(value.split(':')[0]) * 60 + Number(value.split(':')[1]);
        }
        //time
        getHoursTime(value) : number{
              let self = this;
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
        getHoursAll(value: any) : number{
             let self = this;
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
        
        //check data item in care and childCare 
        // child care = 0 , care = 1, other = 2;
        checkItemCare(itemId : any): number {
            if(itemId == 759 || itemId == 760 || itemId == 761 || itemId == 762){
               return 0; 
            }else if(itemId == 763 || itemId == 764 || itemId == 765 || itemId == 766){
               return 1; 
            }else{
               return 2; 
            }
        }
        
        // check data error group care , child care
        checkErrorData(group : number, data: any, dataSource :any ) : boolean{
            let self = this;
            if (group != 2) {
                let rowItemSelect: any = _.find(dataSource, function(value: any) {
                        return value.id == data.rowId;
                    });
                data["itemId"] = data.columnKey.substring(1, data.columnKey.length);
                self.checkInputCare(data, rowItemSelect);
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
        
        checkInputCare(data: any, rowItemSelect : any){
            let self = this;
            switch(Number(data.itemId)){
                case 759:
                    if(!self.isNNUE(rowItemSelect.A760) || data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                       data["group"] = 760;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 760:
                 if(!self.isNNUE(rowItemSelect.A759)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                     data["group"] = 759;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 761:
                 if(!self.isNNUE(rowItemSelect.A762)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                       data["group"] = 762;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 762:
                 if(!self.isNNUE(rowItemSelect.A761)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                     data["group"] = 761;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 763:
                 if(!self.isNNUE(rowItemSelect.A764)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                       data["group"] = 764;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 764:
                 if(!self.isNNUE(rowItemSelect.A763)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                       data["group"] = 763;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 765:
                 if(!self.isNNUE(rowItemSelect.A766)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                       data["group"] = 766;
                       self.listCareInputError.push(data); 
                    }
                    break;
                case 766:
                 if(!self.isNNUE(rowItemSelect.A765)||data.value ==""){
                       data["itemId"] = data.columnKey.substring(1,data.columnKey.length);
                       data["group"] = 765;
                       self.listCareInputError.push(data); 
                    }
                    break;
            }
        }
        
        isNNUE(value : any) : boolean{
            if(value != undefined && value != "" && value != null) return true;
            else return false;
        }
        
       

        extractionData() {
            let self = this;
            self.headersGrid([]);
            self.fixColGrid = self.fixHeaders;
            
            self.loadHeader(self.displayFormat());
            self.dailyPerfomanceData(self.filterData(self.displayFormat()));
        }
        
        extraction() {
            let self = this;
            let start = performance.now();
            self.extractionData();
            console.log("calc load extractionData :" + (start- performance.now()));
            self.loadGrid();
        }
        

        filterData(mode: number) {
            let self = this;
            if (mode == 0) {
                return _.filter(self.dpData, (data) => { return data.employeeId == self.selectedEmployee() });
            } else if (mode == 1) {
                return _.filter(self.dpData, (data) => { return data.date === moment(self.selectedDate()).format('YYYY/MM/DD') });
            } else if (mode == 2) {
                return _.filter(self.dpData, (data) => { return data.error !== '' });
            }
        }
        
        
        //load kcp009 component: employee picker
        loadKcp009() {
            let self = this;
            let kcp009Options = {
                systemReference: 1,
                isDisplayOrganizationName: true,
                employeeInputList: self.lstEmployee,
                targetBtnText: nts.uk.resource.getText("KCP009_3"),
                selectedItem: self.selectedEmployee,
                tabIndex: 1
            };
            // Load listComponent
            //$('#emp-component').ntsLoadListComponent(kcp009Options);
        }

        //init ccg001
        initCcg001() {
             let self = this;
            if ($('.ccg-sample-has-error').ntsError('hasError')) {
                return;
            }
            if (false && false &&  false) {
                nts.uk.ui.dialog.alertError("Base Date or Closure or Period must be shown!");
                return;
            }
            
            self.ccg001 = {
                /** Common properties */
                systemType: 2,
                showEmployeeSelection: false, // 検索タイプ
                showQuickSearchTab: true, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: false, // 基準日利用
                showClosure: true, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: true, // 対象期間利用
                periodFormatYM: false, // 対象期間精度

                /** Required parameter */
                //baseDate: self.baseDate().toISOString(), // 基準日
                //periodStartDate: new Date(self.dateRanger().startDate).toISOString(), // 対象期間開始日
                //periodEndDate: new Date(self.dateRanger().endDate).toISOString(), // 対象期間終了日
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
                isMutipleCheck: false,// 選択モード

                /** Return data */
                returnDataFromCcg001: function(dataList: any) {
                   // self.selectedEmployee(dataList.listEmployee);
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
                    self.lstEmployee(_.orderBy(self.lstEmployee(), ['code'], ['asc']));
                    self.selectedEmployee(self.lstEmployee()[0].id);
                    self.loadKcp009();
                    
                    //Reload screen
                    self.monthlyParam().lstEmployees = self.lstEmployee();
                    self.initScreen();
                },
            }
        }
        
        createKeyLoad() : any {
            let self = this;
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
        
        createDateList(startDate: any, endDate: any) :any{
             let dateArray = [];
            let currentDate : any  = moment(startDate);
            let stopDate : any= moment(endDate);
            while (currentDate <= stopDate) {
                dateArray.push(moment(currentDate).format('YYYY-MM-DD'))
                currentDate = moment(currentDate).add(1, 'days');
            }
            return dateArray;
        }
        convertDateToString(date :any) : any{
          return moment(date).format('YYYY-MM-DD');
        }
        //load ccg001 component: search employee
        loadCcg001() {
            let self = this;
            $('#ccg001').ntsGroupComponent(self.ccg001);
        }
        
        loadGrid() {
            let self = this;
            let summary: ISummaryColumn = {
                columnKey: 'salary',
                allowSummaries: true
            }
            let summaries = [];
            let rowState = {
                rowId: 0,
                disable: true
            }
            //End dummy
            self.setHeaderColor();
            let dataSource = self.formatDate(self.dpData);
            $("#dpGrid").ntsGrid({
                width: (window.screen.availWidth - 200) + "px",
                height: '650px',
                dataSource: dataSource,
                dataSourceAdapter: function(ds) {
                    return ds;
                },
                primaryKey: 'id',
                rowVirtualization: true,
                virtualization: true,
                virtualizationMode: 'continuous',
                enter: self.selectedDirection() == 0 ? 'below' : 'right',
                autoFitWindow: true,
                preventEditInError: false,
                hidePrimaryKey: true,
                userId: "4",
                getUserId: function(k) { return String(k); },
                errorColumns: ["ruleCode"],

                columns: self.headersGrid(),
                features: self.getGridFeatures(),
                ntsFeatures: self.getNtsFeatures(),
                ntsControls: self.getNtsControls()
            });
        }
         /**********************************
         * Grid Data Setting 
         **********************************/ 
        setFixedHeader(fixHeader: Array<any>){
            let self = this;            
            let fixedData = _.map(fixHeader, function(item: any)  
            { 
                return { columnKey: item.key, isFixed: true };
            });
            self.fixHeaders(fixedData);
        }
        checkIsColumn(dataCell: any, key: any): boolean {
            let check = false;
          _.each(dataCell, (item: any) =>{
              if (item.columnKey.indexOf("NO" + key) != -1) {
                  check = true;
                  return;
              }
              });
            return check;
        }
        
        isDisableRow(id) {
            let self = this;
            for (let i = 0; i < self.rowStates().length; i++) {
                return self.rowStates()[i].rowId == id;
            }
        }

        isDisableSign(id) {
            let self = this;
            for (let i = 0; i < self.cellStates().length; i++) {
                return self.cellStates()[i].rowId == id && self.cellStates()[i].columnKey == 'sign';
            }
        }
        formatDate(lstData) {
            let self = this;
            let start = performance.now();
            let data =  lstData.map((data) => {
                let object = {
                    id: "_" + data.id,
                    state: data.state,
                    error: data.error,
                    employeeId: data.employeeId,
                    employeeCode: data.employeeCode,
                    employeeName: data.employeeName,
                    workplaceId: data.workplaceId,
                    employmentCode: data.employmentCode,
                    identify: data.identify,
                    approval: data.approval,
                    dailyConfirm: data.dailyConfirm,
                    dailyCorrectPerformance: data.dailyCorrectPerformance,
                    typeGroup: data.typeGroup
                }
                _.each(data.cellDatas, function(item) { object[item.columnKey] = item.value });
                return object;
            });
            return data;
        }
        /**
         * Grid Setting features
         */
        getGridFeatures(): Array<any>{
            let self = this;
            let features = [
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
                        columnSettings: [
                            {
                                columnKey: 'id', allowSummaries: false,
                                summaryOperands: [{ type: "custom", order: 0, summaryCalculator: function() { return "合計"; } }]
                            },
                            {
                                columnKey: 'state', allowSummaries: true,
                                summaryOperands: [{ type: "custom", order: 0, summaryCalculator: function() { return "合計"; } }]
                            },
                            { columnKey: 'error', allowSummaries: false },
                            { columnKey: 'employeeCode', allowSummaries: false },
                            { columnKey: 'employeeName', allowSummaries: false },
                            { columnKey: 'picture-person', allowSummaries: false },
                            { columnKey: 'identify', allowSummaries: false },
                            { columnKey: 'dailyconfirm', allowSummaries: false },
                            { columnKey: 'dailyperformace', allowSummaries: false },
                            {
                                columnKey: 'time', allowSummaries: true,
                                summaryOperands: [{
                                    rowDisplayLabel: "",
                                    type: 'custom',
                                    summaryCalculator: 1,
                                    order: 0
                                }]
                            },
                            
                            { columnKey: 'alert', allowSummaries: false }
                        ],
                        resultTemplate: '{1}'
                    }
                ];
            return features;
        }
        getNtsFeatures(): Array<any>{
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
                    },
                    {
                        name: 'CellState',
                        rowId: 'rowId',
                        columnKey: 'columnKey',
                        state: 'state',
                        states: self.cellStates()
                    },
                    {
                        name: 'RowState',
                        rows: self.rowStates()
                    },
                    {
                        name: 'TextColor',
                        rowId: 'rowId',
                        columnKey: 'columnKey',
                        color: 'color',
                        colorsTable: []
                    },
                    {
                        name: 'HeaderStyles',
                        columns: self.headerColors()
                    },                    
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
                        color: header.color
                    });
                }
                //Setting color group header
                if (header.group !=  null && header.group != undefined && header.group.length > 0) {
                    self.headerColors.push({
                        key: header.group[0].key,
                        color: header.group[0].color
                    });
                    self.headerColors.push({
                        key: header.group[1].key,
                        color: header.group[1].color
                    });
                }
            });
        }
        /**
         * Create NtsControls
         */
        getNtsControls(): Array<any>{            
            let ntsControls: Array<any> = [
                { name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                { name: 'Button', controlType: 'Button', text:  nts.uk.resource.getText("KMW003_29"), enable: true, click: function() { 
                        let self = this;
                        nts.uk.ui.windows.sub.modal("/view/kdw/003/a/index.xhtml").onClosed(() => {
                                                        
                        });
                
                    } 
                },
                { name: 'FlexImage', source: 'ui-icon ui-icon-locked', click: function() { alert('Show!'); }, controlType: 'FlexImage' },
                { name: 'Image', source: 'ui-icon ui-icon-info', controlType: 'Image' },
                { name: 'TextEditor', controlType: 'TextEditor', constraint: { valueType: 'Integer', required: true, format: "Number_Separated" } }
                ];
            return ntsControls;
        }
        reloadGrid() {
            let self = this;
            nts.uk.ui.block.invisible();
             nts.uk.ui.block.grayout();
            setTimeout(function() {
                 //self.createSumColumn(self.dataAll());
                    self.columnSettings(self.dataAll().lstControlDisplayItem.columnSettings);
                    self.receiveData(self.dataAll());
                    self.extractionData();
                    self.loadGrid();
                nts.uk.ui.block.clear();
            }, 500);
        }
        loadHeader(mode) {
            let self = this;
            let tempList = [];
            self.dislayNumberHeaderText();
            _.forEach(self.optionalHeader, (header) => {
                if (header.constraint == null || header.constraint == undefined) {
                    delete header.constraint;
                }else{
                    header.constraint["cDisplayType"] = header.constraint.cdisplayType;
                    if (header.constraint.cDisplayType != null && header.constraint.cDisplayType != undefined) {
                        if (header.constraint.cDisplayType != "Primitive" && header.constraint.cDisplayType != "Combo") {
                            if (header.constraint.cDisplayType.indexOf("Currency") != -1) {
                                header["columnCssClass"] = "currency-symbol";
                                header.constraint["min"] = "0";
                                header.constraint["max"] = "9999999999"
                            } else if (header.constraint.cDisplayType == "Clock") {
                                header["columnCssClass"] = "right-align";
                                header.constraint["min"] = "-10:00";
                                header.constraint["max"] = "100:30"
                            } else if (header.constraint.cDisplayType == "Integer") {
                                header["columnCssClass"] = "right-align";
                            }
                            delete header.constraint.primitiveValue;
                        } else {
                           
                            if (header.constraint.cDisplayType == "Primitive") {
                                delete header.group[0].constraint.cDisplayType
                            } else if (header.constraint.cDisplayType == "Combo") {
                                    header.group[0].constraint["min"] = 0;
                                    header.group[0].constraint["max"] = Number(header.group[0].constraint.primitiveValue);
                                    header.group[0].constraint["cDisplayType"] =  header.group[0].constraint.cdisplayType;
                                   delete header.group[0].constraint.cdisplayType
                                   delete header.group[0].constraint.primitiveValue;
                            }
                            delete header.constraint;
                            delete header.group[1].constraint;
                        }
                    }
                    if(header.constraint != undefined) delete header.constraint.cdisplayType;
                }
                if (header.group != null && header.group != undefined) {
                    if (header.group.length > 0) {
                       if( header.group[0].constraint == undefined) delete header.group[0].constraint;
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
                            //header.group[0].onChange = self.search;
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
        
          pushDataEdit(evt, ui) : InfoCellEdit {
            let self = this;
            let dataEdit: InfoCellEdit
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
                dataEdit = new InfoCellEdit(ui.rowID, ui.columnKey, ui.value, 1, "", employeeIdSelect.employeeId,  moment(dateCon.date).utc().toISOString(), 0);
                self.editValue.push(dataEdit);
                return dataEdit;
            }
        }
        
        displayProfileIcon() {
            let self = this;
            if (self.showProfileIcon()) {
                _.remove(self.dateModeHeader, function(header) {
                    return header.key === "picture-person";
                });
            } else {
                _.remove(self.dateModeHeader, function(header) {
                    return header.key === "picture-person";
                });
            }
        }

        dislayNumberHeaderText() {
            let self = this;
            if (self.showHeaderNumber()) {
                self.optionalHeader.map((header) => {
                    if (header.headerText) {
                        if (header.group == undefined || header.group == null) {
                            header.headerText = header.headerText + " " + header.key.substring(1, header.key.length);
                        }else{
                            header.headerText = header.headerText + " " + header.group[1].key.substring(4, header.group[1].key.length)
                        }
                    }
                    return header;
                });
            } else {
                self.optionalHeader.map((header) => {
                    if (header.headerText) {
                        header.headerText = header.headerText.split(" ")[0];
                    }
                    return header;
                });
            }
        }

        
        getDataShare(data : any){
            let self = this;
           let param = {
                dateRange: data.dateRangeParam? {
                    startDate: moment(data.dateRangeParam.startDate).utc().toISOString(),
                    endDate: moment(data.dateRangeParam.endDate).utc().toISOString()
                }: null,
                displayFormat : 0,
                initScreen: 0,
                lstEmployee: [],
                formatCodes: self.formatCodes()
            }; 
        }
        /**********************************
         * Button Event 
         **********************************/   
        /**
         * 期間を変更する
         */
        updateDate(date: any){
            let self = this;
            self.monthlyParam().yearMonth = date;
            self.initScreen();
        }
        /**
         * 実績期間を変更する
         */
        updateActualTime(){
            let self = this;
            self.monthlyParam().actualTime = self.actualTimeSelectedDat();
            //self.actualTimeSelectedDat
            self.initScreen();
        }
        /**
         * Check all CheckBox
         */
        signAll() {
            let self = this;
            $("#dpGrid").ntsGrid("checkAll", "identify");
            //$("#dpGrid").ntsGrid("checkAll", "approval");
        }
        /**
         * UnCheck all CheckBox
         */
        releaseAll() {
            let self = this;
            $("#dpGrid").ntsGrid("uncheckAll", "identify");
            //$("#dpGrid").ntsGrid("uncheckAll", "approval");
        }
        /**
         * ロック解除ボタン　クリック
         */
        unlockProcess(){
            let self = this;
            //アルゴリズム「ロックを解除する」を実行する
            nts.uk.ui.dialog.confirm({ messageId: "Msg_983" }).ifYes(() => {
                //画面モードを「ロック解除モード」に変更する
                self.initMode(1);
                //保持しているパラメータ「ロック状態一覧」のすべてのロック状態をアンロックに変更する
                //TODO Loop all param and change lock status to Unlock
                //ロック状態を画面に反映する
                self.reloadGrid();
            }).ifNo(() => {
                
            });
        }
        /**
         * 再ロックボタン　クリック
         */
        lockProcess() {
            let self = this;
            // 画面モードを「修正モード」に変更する
            self.initMode(0);
            //アルゴリズム「ロック状態を表示する」を実行する
            //Restore init lock status 
            //確認メッセージ「#Msg_984」を表示する
            nts.uk.ui.dialog.info({ messageId: "Msg_984" });
        }
        /**
         * 
         */
        btnSetting_Click() {
            let container = $("#setting-content");
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
                    container.css("left", "-9999px");
                }
            });
        }
        /**
         * 「KDW003_D_抽出条件の選択」を起動する
         * 起動モード：月別 
         * 選択済項目：選択している「月別実績のエラーアラームコード」
         */
        extractCondition(){
            let self = this;
            let errorParam = {initMode: 1, selectedItems: self.selectedErrorCodes()};
            nts.uk.ui.windows.setShared("KDW003D_ErrorParam", errorParam);
            nts.uk.ui.windows.sub.modal("/view/kdw/003/d/index.xhtml").onClosed(() => {
                debugger;
                let errorCodes: any = nts.uk.ui.windows.getShared("KDW003D_Output");
                //アルゴリズム「選択した抽出条件に従って実績を表示する」を実行する
                if(!nts.uk.util.isNullOrUndefined(errorCodes)){
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
        displayItem(){
            let self = this;
            let formatParam = {initMode: 1, selectedItem: ""};
            nts.uk.ui.windows.setShared("KDW003C_Param", formatParam);
            nts.uk.ui.windows.sub.modal("/view/kdw/003/c/index.xhtml").onClosed(() => {
                 let formatCd = nts.uk.ui.windows.getShared('KDW003C_Output');
            });
        }
    }
    /**
     * 
     */
    export interface MonthlyPerformanceParam{
        lstEmployees: KnockoutObservableArray<any>;
        actualTime: KnockoutObservable<any>;
        formatCodes : KnockoutObservableArray<any>;
        dispItems : KnockoutObservableArray<any>;
        correctionOfDaily : CorrectionOfMonthlyPerformance;
        initMode : KnockoutObservable<number>;
        errorCodes : KnockoutObservableArray<string>;
        lstLockStatus : KnockoutObservableArray<any>;
    }
    /**
     * 月別実績の修正
     */
    export interface CorrectionOfMonthlyPerformance{
        cursorMovementDirection : string;
        display : boolean;
        displayItemNumberInGridHeader : boolean;
        displayThePersonalProfileColumn : boolean;
        previousDisplayItem : string;
    }
    export class AuthorityDetailModel {
        //A1_1
        available_A1_1: KnockoutObservable<boolean> = ko.observable(false);
        enable_A1_1: KnockoutObservable<boolean> = ko.observable(false);       
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
        /**
         * formatPerformance: 権限 = 0, 勤務種別 = 1
         * initMode: 修正モード  = 0,  ロック解除モード    = 1  
         * actualTimeState: 過去 = 0, 当月 = 1, 未来 = 2
         */
        constructor(data: Array<DailyPerformanceAuthorityDto>, actualTimeState : number, initMode : number, formatPerformance : number) {
            let self = this;
            if (!data) return;
            this.available_A1_1(self.checkAvailable(data, 32));            
            this.available_A1_2(self.checkAvailable(data, 33));                       
            this.available_A1_4(self.checkAvailable(data, 34));   
            this.available_A5_4(self.checkAvailable(data, 11));
            if(initMode == 0){ //修正モード
                if (formatPerformance == 0) { //権限
                    this.enable_A1_1(actualTimeState == 1 || actualTimeState == 2);
                    this.enable_A1_2(actualTimeState == 1 || actualTimeState == 2);
                    this.enable_A5_4(actualTimeState == 1 || actualTimeState == 2);
                } else if (formatPerformance == 1) { //勤務種別
                    this.enable_A1_1(actualTimeState == 1 || actualTimeState == 2);
                    this.enable_A1_2(actualTimeState == 1 || actualTimeState == 2);
                    this.enable_A5_4(actualTimeState == 1 || actualTimeState == 2);
                    this.enable_A1_5(false);
                }
                this.available_A1_8(self.checkAvailable(data, 12));
            }else if(initMode == 1){ //ロック解除モード 
                if (formatPerformance == 1) { //勤務種別
                    this.enable_A1_5(false);
                }
                this.available_A1_9(self.checkAvailable(data, 12));
                this.available_A1_11(self.checkAvailable(data, 12)); 
            }            
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
    export interface DailyPerformanceAuthorityDto {
        isDefaultInitial: number;
        roleID: string;
        functionNo: number;
        availability: boolean;
    }
    
    export interface DPAttendanceItem {
        id: string;
        name: string;
        displayNumber: number;
        userCanSet: boolean;
        lineBreakPosition: number;
        attendanceAtr: number;
        typeGroup: number;
    }

    class InfoCellEdit {
        rowId: any;
        itemId: any;
        value: any;
        valueType: number;
        layoutCode: string;
        employeeId: string;
        date: any;
        typeGroup : number;
        constructor(rowId: any, itemId: any, value: any, valueType: number, layoutCode: string, employeeId: string, date: any, typeGroup: number) {
            this.rowId = rowId;
            this.itemId = itemId;
            this.value = value;
            this.valueType = valueType;
            this.layoutCode = layoutCode;
            this.employeeId = employeeId;
            this.date = date;
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
            this.compensation = nts.uk.resource.getText("KMW003_8", [compensation])
            this.substitute = nts.uk.resource.getText("KMW003_8",[substitute])
            this.paidYear = nts.uk.resource.getText("KMW003_8", [paidYear])
//            this.paidHalf = nts.uk.resource.getText("KMW003_10", paidHalf)
//            this.paidHours = nts.uk.resource.getText("KMW003_11", paidHours)
            this.fundedPaid = nts.uk.resource.getText("KMW003_8", [fundedPaid])
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
    
}