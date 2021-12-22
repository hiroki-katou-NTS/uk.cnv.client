module nts.uk.at.view.kmk006.a {

    import Enum = service.model.Enum;
    import setShared = nts.uk.ui.windows.setShared;
    // Import Dto
    import ComAutoCalSettingDto = a.service.model.ComAutoCalSettingDto;
    import JobAutoCalSettingDto = service.model.JobAutoCalSettingDto;
    import WkpAutoCalSettingDto = service.model.WkpAutoCalSettingDto;
    import WkpJobAutoCalSettingDto = service.model.WkpJobAutoCalSettingDto;
    import AutoCalcOfLeaveEarlySettingDto = service.model.AutoCalcOfLeaveEarlySettingDto;
    import AutoCalRaisingSalarySettingDto = service.model.AutoCalRaisingSalarySettingDto;

    // Import Setting Dto
    import AutoCalOvertimeSettingDto = service.model.AutoCalOvertimeSettingDto;
    import AutoCalRestTimeSettingDto = service.model.AutoCalRestTimeSettingDto;
    import AutoCalFlexOvertimeSettingDto = service.model.AutoCalFlexOvertimeSettingDto;
    import AutoCalSettingDto = service.model.AutoCalSettingDto;
    
    // Import Base unit for Setting
    import UnitAutoCalSettingDto = nts.uk.at.view.kmk006.e.service.model.UnitAutoCalSettingDto;

    export module viewmodel {
        export class ScreenModel { 
            totalSelectedWorkplaceId: KnockoutObservable<string>;
            multiSelectedWorkplaceId: KnockoutObservable<string>;
            wkpAlreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
            treeOptionsWkp: any;
            treeOptionsWkpTotal: any;
            autoCalAtrOvertimeEnum: Array<Enum>;
            autoCalAtrOvertimeEnumWithoutTimeRecorder: Array<Enum>;
            selectedTab: KnockoutObservable<string>;
            timeLimitUpperLimitEnum: Array<Enum>;
            itemComAutoCalModel: ComAutoCalSettingModel;
            itemWkpAutoCalModel: WkpAutoCalSettingModel;
            itemJobAutoCalModel: JobAutoCalSettingModel;
            itemWkpJobAutoCalModel: WkpJobAutoCalSettingModel;

            // define value Enum
            valueEnumNorEarLi: KnockoutObservable<number>;
            valueEnumNorEarAtr: KnockoutObservable<number>;
            valueEnumNorEarMidLi: KnockoutObservable<number>;
            valueEnumNorEarMidAtr: KnockoutObservable<number>;
            valueEnumNorNorLi: KnockoutObservable<number>;
            valueEnumNorNorAtr: KnockoutObservable<number>;
            valueEnumNorNorMidLi: KnockoutObservable<number>;
            valueEnumNorNorMidAtr: KnockoutObservable<number>;
            valueEnumNorLegLi: KnockoutObservable<number>;
            valueEnumNorLegAtr: KnockoutObservable<number>;
            valueEnumNorLegMidLi: KnockoutObservable<number>;
            valueEnumNorLegMidAtr: KnockoutObservable<number>;
            valueEnumFleTimeLi: KnockoutObservable<number>;
            valueEnumFleTimeAtr: KnockoutObservable<number>;
            valueEnumResResLi: KnockoutObservable<number>;
            valueEnumResResAtr: KnockoutObservable<number>;
            valueEnumResLatLi: KnockoutObservable<number>;
            valueEnumResLatAtr: KnockoutObservable<number>;
            
            // define value for autoCalcOfLeaveEarlySetting
            autoCalcOfLeaveLateSetting: Array<any>;
            autoCalcOfLeaveLate: KnockoutObservable<boolean>;
            autoCalcOfLeaveEarlySetting: Array<any>;
            autoCalcOfLeaveEarly: KnockoutObservable<boolean>;
            
            // define value for autoCalRaisingSalarySetting
            autoCalRaisingSalarySetting: Array<any>;
            raisingSalaryCalcAtr: KnockoutObservable<boolean>;
            autoCalSpecificRaisingSalarySetting: Array<any>;
            specificRaisingSalaryCalcAtr: KnockoutObservable<boolean>;
            
            // define value for autoCalcSetOfDivergenceTime
            autoCalcSetOfDivergenceTime: Array<any>;
            divergenceTime: KnockoutObservable<number>;

            jobListOptions: any;
            jobTotalListOptions: any;
            selectedCode: KnockoutObservable<string>;
            totalSelectedCode: KnockoutObservable<string>;
            multiSelectedCode: KnockoutObservableArray<string>;
            isShowAlreadySet: KnockoutObservable<boolean>;
            jobAlreadySettingList: KnockoutObservableArray<JobAlreadySettingModel>;
            isDialog: KnockoutObservable<boolean>;
            isShowNoSelectRow: KnockoutObservable<boolean>;
            isMultiSelect: KnockoutObservable<boolean>;
            isMultiSelectKcp: KnockoutObservable<boolean>;
            jobTitleList: KnockoutObservableArray<UnitModel>;
            selectedCurrentJob: KnockoutObservable<string>;
            selectedCurrentWkp: KnockoutObservable<string>;
            useUnitAutoCalSettingModel: KnockoutObservable<UnitAutoCalSettingView>;
            treeItemName: KnockoutObservable<string>;
            componentItemName: KnockoutObservable<string>;
            treeItemCode: KnockoutObservable<string>;
            componentItemCode: KnockoutObservable<string>;
            enableEnumNorLegLi: KnockoutObservable<boolean>;
            enableEnumNorLegMidLi: KnockoutObservable<boolean>;
            enableEnumNorNorLi: KnockoutObservable<boolean>;
            enableEnumNorNorMidLi: KnockoutObservable<boolean>;
            enableEnumNorEarLi: KnockoutObservable<boolean>;
            enableEnumNorEarMidLi: KnockoutObservable<boolean>;
            enableEnumFleTimeLi: KnockoutObservable<boolean>;
            enableEnumResResLi: KnockoutObservable<boolean>;
            enableEnumResLatLi: KnockoutObservable<boolean>;
            baseDateJobList: KnockoutObservable<Date>;
            baseDateTreeList: KnockoutObservable<Date>;
            baseDateJobListTotal: KnockoutObservable<Date>;
            baseDateTreeListTotal: KnockoutObservable<Date>;
            inputDate: KnockoutObservable<Date>;
            isLoading: KnockoutObservable<boolean>;
            date: KnockoutObservable<string>;
            yearMonth: KnockoutObservable<number>;

            // Common
            tabs: KnockoutObservableArray<any>;
            
            // UI 
            createModeScreenB: KnockoutObservable<boolean>;
            createModeScreenC: KnockoutObservable<boolean>;
            createModeScreenD: KnockoutObservable<boolean>;

            //export
            baseDate: KnockoutObservable<string> = ko.observable('9999/12/31');
            langId: KnockoutObservable<string> = ko.observable('ja');

            constructor() {
                var self = this;
                self.baseDateJobList = ko.observable(moment(new Date()).toDate());
                self.baseDateTreeList = ko.observable(moment(new Date()).toDate());
                self.baseDateJobListTotal = ko.observable(moment(new Date()).toDate());
                self.baseDateTreeListTotal = ko.observable(moment(new Date()).toDate());
                self.inputDate = ko.observable(moment(new Date()).toDate());
                self.isMultiSelectKcp = ko.observable(false);
                self.date = ko.observable('20000101');
                self.yearMonth = ko.observable(200001);
                // Initial common data.
                self.useUnitAutoCalSettingModel = ko.observable(new UnitAutoCalSettingView(true, true, true));
                this.tabs = ko.observableArray([
                    { id: 'tab-1', title: nts.uk.resource.getText("KMK006_14"), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: nts.uk.resource.getText("KMK006_15"), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-3', title: nts.uk.resource.getText("KMK006_16"), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-4', title: nts.uk.resource.getText("KMK006_40"), content: '.tab-content-4', enable: ko.observable(true), visible: ko.observable(true) }
                ]);
                
                self.initDataSource();
                self.initNextTabFeature();
                
                self.autoCalcOfLeaveLate = ko.observable(false);
                self.autoCalcOfLeaveEarly = ko.observable(false);
                self.raisingSalaryCalcAtr = ko.observable(false);
                self.specificRaisingSalaryCalcAtr = ko.observable(false);
                self.divergenceTime = ko.observable(0);

                self.multiSelectedWorkplaceId = ko.observable('');
                self.multiSelectedWorkplaceId.extend({ notify: 'always' });
                self.totalSelectedWorkplaceId = ko.observable('');
                self.totalSelectedWorkplaceId.extend({ notify: 'always' });
                self.wkpAlreadySettingList = ko.observableArray([]);
                self.treeOptionsWkp = {
                    isShowAlreadySet: true,
                    isMultiSelect: false,
                    isMultipleUse: false,
                    treeType: TreeType.WORK_PLACE,
                    selectedId: self.multiSelectedWorkplaceId,
                    baseDate: self.baseDateTreeList,
                    selectType: SelectionType.SELECT_FIRST_ITEM,
                    isShowSelectButton: false,
                    isDialog: false,
                    alreadySettingList: self.wkpAlreadySettingList,
                    maxRows: 20,
                    systemType: 2,
                    tabindex: -1
                };
                self.treeOptionsWkpTotal = {
                    isShowAlreadySet: false,
                    isMultiSelect: false,
                    isMultipleUse: true,
                    treeType: TreeType.WORK_PLACE,
                    selectedId: self.totalSelectedWorkplaceId,
                    baseDate: self.baseDateTreeListTotal,
                    selectType: SelectionType.SELECT_FIRST_ITEM,
                    isShowSelectButton: false,
                    isDialog: false,
                    maxRows: 10,
                    systemType: 2,
                    tabindex: -1
                };
                self.itemComAutoCalModel = new ComAutoCalSettingModel();
                self.itemJobAutoCalModel = new JobAutoCalSettingModel();
                self.itemWkpAutoCalModel = new WkpAutoCalSettingModel();
                self.itemWkpJobAutoCalModel = new WkpJobAutoCalSettingModel();
                self.selectedTab = ko.observable('tab-1');
                self.isLoading = ko.observable(false);

                self.autoCalAtrOvertimeEnum = [];
                self.autoCalAtrOvertimeEnumWithoutTimeRecorder = [];

                self.timeLimitUpperLimitEnum = [];                
                self.valueEnumNorEarLi = ko.observable(2);
                self.valueEnumNorEarAtr = ko.observable(2);
                self.valueEnumNorEarMidLi = ko.observable(2);
                self.valueEnumNorEarMidAtr = ko.observable(2);
                self.valueEnumNorNorLi = ko.observable(2);
                self.valueEnumNorNorAtr = ko.observable(2);
                self.valueEnumNorNorMidLi = ko.observable(2);
                self.valueEnumNorNorMidAtr = ko.observable(2);
                self.valueEnumNorLegLi = ko.observable(2);
                self.valueEnumNorLegAtr = ko.observable(2);
                self.valueEnumNorLegMidLi = ko.observable(2);
                self.valueEnumNorLegMidAtr = ko.observable(2);
                self.valueEnumFleTimeLi = ko.observable(2);
                self.valueEnumFleTimeAtr = ko.observable(2);
                self.valueEnumResResLi = ko.observable(2);
                self.valueEnumResResAtr = ko.observable(2);
                self.valueEnumResLatLi = ko.observable(2);
                self.valueEnumResLatAtr = ko.observable(2);

                self.selectedCode = ko.observable('');
                self.selectedCode.extend({ notify: 'always' });
                self.totalSelectedCode = ko.observable('');
                self.totalSelectedCode.extend({ notify: 'always' });
                self.selectedCurrentJob = ko.observable('');
                self.selectedCurrentWkp = ko.observable('');
                self.multiSelectedCode = ko.observableArray(['0', '1', '4']);
                self.isShowAlreadySet = ko.observable(false);
                self.jobAlreadySettingList = ko.observableArray([]);
                self.isDialog = ko.observable(false);
                self.isShowNoSelectRow = ko.observable(false);
                self.treeItemName = ko.observable('');
                self.componentItemName = ko.observable('');
                self.treeItemCode = ko.observable('');
                self.componentItemCode = ko.observable('');
                self.jobListOptions = {
                    isShowAlreadySet: true,
                    baseDate: self.baseDateJobList,
                    isMultiSelect: false,
                    isMultipleUse: false,
                    listType: ListType.JOB_TITLE,
                    selectType: SelectType.SELECT_BY_SELECTED_CODE,
                    selectedCode: self.selectedCode,
                    isDialog: self.isDialog(),
                    isShowNoSelectRow: self.isShowNoSelectRow(),
                    alreadySettingList: self.jobAlreadySettingList,
                    maxRows: 20,
                    tabindex: -1
                };
                self.jobTotalListOptions = {
                    isShowAlreadySet: false,
                    baseDate: self.baseDateJobListTotal,
                    isMultiSelect: false,
                    isMultipleUse: true,
                    listType: ListType.JOB_TITLE,
                    selectType: SelectType.SELECT_BY_SELECTED_CODE,
                    selectedCode: self.totalSelectedCode,
                    isDialog: self.isDialog(),
                    isShowNoSelectRow: self.isShowNoSelectRow(),
                    maxRows: 10,
                    tabindex: -1
                };
                self.jobTitleList = ko.observableArray<UnitModel>([]);
                
                self.createModeScreenB = ko.observable(false);
                self.createModeScreenC = ko.observable(false);
                self.createModeScreenD = ko.observable(false);  


                //subscribe
                self.multiSelectedWorkplaceId.subscribe(function(codeChanged) {
                    if ($("#sidebar").ntsSideBar("getCurrent") != SIDEBAR_TAB_INDEX.WORKPLACE) return;
                    self.selectedCurrentWkp(codeChanged);
                    if (!_.isEmpty(codeChanged) && !$('#work-place-base-date').ntsError('hasError')) {
                        self.loadWkpAutoCal(codeChanged);

                        nts.uk.ui.block.invisible();
                        self.treeItemCode($('#tree-grid-srcc').getRowSelected()[0].workplaceCode);
                        let wkplId: string = $('#component-items-list').getRowSelected()[0].id;
                        var params: any = {
                            "workplaceId": wkplId,
                            "baseDate"   : self.baseDateTreeList()
                        };
                        service.getDetailWkpl(params).done(function(data: any){
                            nts.uk.ui.block.clear();
                            self.treeItemName(data.workplaceName);                    
                        });
                    } else {
                        self.treeItemCode("");
                        self.treeItemName("");
                        if (self.itemWkpAutoCalModel) {
                            self.itemWkpAutoCalModel.resetData();
                            self.reLoadListEnum(self.itemWkpAutoCalModel);
                        }                        
                    }
                    service.getWkpAutoCal(codeChanged).done((data) => {
                        if (data) {
                            self.createModeScreenB(true);             
                        } else {
                            self.createModeScreenB(false);    
                        }
                    });  
                });

                //subscribe 
                self.totalSelectedWorkplaceId.subscribe(function(codeChanged) {
                    if ($("#sidebar").ntsSideBar("getCurrent") != SIDEBAR_TAB_INDEX.WORKPLACE_JOBTITLE) return;                    
                    self.selectedCurrentWkp(codeChanged);
                    if (!_.isEmpty(codeChanged) && !$('#kmk0006-basedate').ntsError('hasError')) {
                        if(!nts.uk.text.isNullOrEmpty(self.totalSelectedCode())){
                            self.loadWkpJobAutoCal(codeChanged, self.totalSelectedCode());
                            service.getWkpJobAutoCal(codeChanged, self.totalSelectedCode()).done((data) => {
                                if (data) {
                                    self.createModeScreenD(true);             
                                } else {
                                    self.createModeScreenD(false);    
                                }
                            });
                        }
                        nts.uk.ui.block.invisible();
                        self.treeItemCode($('#tree-grid').getRowSelected()[0].workplaceCode);
                        let wkplId: string = $('#tree-grid').getRowSelected()[0].id;
                        var params: any = {
                            "workplaceId": wkplId,
                            "baseDate"   : self.inputDate()
                        };
                        service.getDetailWkpl(params).done(function(data: any){
                            nts.uk.ui.block.clear();
                            self.treeItemName(data.workplaceName);                    
                        });
                    } else {
                        self.treeItemCode("");
                        self.treeItemName("");
                        if (self.itemWkpJobAutoCalModel) {
                            self.itemWkpJobAutoCalModel.resetData();
                            self.reLoadListEnum(self.itemWkpJobAutoCalModel);                       
                        }
                    }
                });

                //subscribe 
                self.totalSelectedCode.subscribe(function(codeChanged) {
                    if ($("#sidebar").ntsSideBar("getCurrent") != SIDEBAR_TAB_INDEX.WORKPLACE_JOBTITLE) return;
                    self.selectedCurrentJob(codeChanged);
                    self.loadWkpJobAutoCal(self.totalSelectedWorkplaceId(), codeChanged);
                    let data = $('#jobtitles').getDataList();
                    for (let ent of data) {
                        if (ent.id == codeChanged) {
                            self.componentItemName(ent.name);
                            self.componentItemCode(ent.code);
                        }
                    }
                    if (nts.uk.text.isNullOrEmpty(codeChanged)) {
                        self.componentItemCode("");
                        self.componentItemName("");  
                        if (self.itemWkpJobAutoCalModel) {
                            self.itemWkpJobAutoCalModel.resetData();
                            self.reLoadListEnum(self.itemWkpJobAutoCalModel);
                        }   
                    }
                    service.getWkpJobAutoCal(self.totalSelectedWorkplaceId(), codeChanged).done((data) => {
                        if (data) {
                            self.createModeScreenD(true);             
                        } else {
                            self.createModeScreenD(false);    
                        }
                    });
                });

                //subscribe 
                self.selectedCode.subscribe(function(codeChanged) {    
                    if ($("#sidebar").ntsSideBar("getCurrent") != SIDEBAR_TAB_INDEX.JOBTITLE) return;         
                    self.selectedCurrentJob(codeChanged);
                    self.loadJobAutoCal(codeChanged);
                    let data = $('#component-items-list').getDataList();
                    for (let ent of data) {
                        if (ent.id == codeChanged) {
                            self.componentItemName(ent.name);
                            self.componentItemCode(ent.code);
                        }
                    }
                    if (nts.uk.text.isNullOrEmpty(codeChanged)) {
                        self.componentItemCode("");
                        self.componentItemName("");    
                        if (self.itemJobAutoCalModel) {
                            self.itemJobAutoCalModel.resetData();
                            self.reLoadListEnum(self.itemJobAutoCalModel);
                        }
                    }
                    service.getJobAutoCal(codeChanged).done((data) => {
                        if (data) {
                            self.createModeScreenC(true);             
                        } else {
                            self.createModeScreenC(false);    
                        }
                    });
                });


                self.enableEnumNorLegLi = ko.computed(function() {
                    return self.valueEnumNorLegAtr() != 0;
                });
                self.enableEnumNorLegMidLi = ko.computed(function() {
                    return self.valueEnumNorLegMidAtr() != 0;
                });
                self.enableEnumNorNorLi = ko.computed(function() {
                    return self.valueEnumNorNorAtr() != 0;
                });
                self.enableEnumNorNorMidLi = ko.computed(function() {
                    return self.valueEnumNorNorMidAtr() != 0;
                });
                self.enableEnumNorEarLi = ko.computed(function() {
                    return self.valueEnumNorEarAtr() != 0;
                });
                self.enableEnumNorEarMidLi = ko.computed(function() {
                    return self.valueEnumNorEarMidAtr() != 0;
                });
                self.enableEnumFleTimeLi = ko.computed(function() {
                    return self.valueEnumFleTimeAtr() != 0;
                });
                self.enableEnumResResLi = ko.computed(function() {
                    return self.valueEnumResResAtr() != 0;
                });
                self.enableEnumResLatLi = ko.computed(function() {
                    return self.valueEnumResLatAtr() != 0;
                }); 
            }


            /**
            * start page data 
            */
            public startPage(): JQueryPromise<any> {
                nts.uk.ui.block.invisible();
                var _self = this;
                var dfd = $.Deferred<any>();

                // Initial settings.
                $.when(_self.loadTimeLimitUpperLimitSettingEnum(), 
                    _self.loadAutoCalAtrOvertimeEnum(),
                    _self.loadAutoCalAtrOvertimeEnumWithoutTimeRecorder(),
                    _self.loadUseUnitAutoCalSettingModel())
                    .done(() => {
                        _self.onSelectCompany();
                        dfd.resolve(_self);
                    });

                return dfd.promise();
            }
            
            /**
             * Initial data source 
             */
            public initDataSource(): void {
                let self = this;
                self.autoCalcOfLeaveLateSetting = [
                    { value: true, name: nts.uk.resource.getText("KMK006_41") },
                    { value: false, name: nts.uk.resource.getText("KMK006_42") }
                ];

                self.autoCalcOfLeaveEarlySetting = [
                    { value: true, name: nts.uk.resource.getText("KMK006_41") },
                    { value: false, name: nts.uk.resource.getText("KMK006_42") }
                ];

                self.autoCalRaisingSalarySetting = [
                    { value: true, name: nts.uk.resource.getText("KMK006_41") },
                    { value: false, name: nts.uk.resource.getText("KMK006_42") }
                ];

                self.autoCalSpecificRaisingSalarySetting = [
                    { value: true, name: nts.uk.resource.getText("KMK006_41") },
                    { value: false, name: nts.uk.resource.getText("KMK006_42") }
                ];

                self.autoCalcSetOfDivergenceTime = [
                    { code: 1, name: nts.uk.resource.getText("KMK006_41") },
                    { code: 0, name: nts.uk.resource.getText("KMK006_42") }
                ];
            }
            
            //init next tab
            public initNextTabFeature() {
                var self = this;
                const TAB_KEY_CODE = 9;

                // when tab to last item of tab 1
                $("[tabindex='7']").on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE) {
                        self.selectedTab('tab-2');
                    }
                });

                // when tab to last item of tab 2
                $("[tabindex='17']").on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE) {
                        self.selectedTab('tab-3');
                    }
                });

                // when tab to last item of tab 3
                $("[tabindex='21']").on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE) {
                        self.selectedTab('tab-4');
                    }
                });
            }

            //load workPlace-job already setting
            public loadWkpJobAlreadySettingList(): JQueryPromise<void> {
                let _self = this;
                var jobSettingList: any = [];
                var wkpSettingList: any = [];
                var dfd = $.Deferred<any>();

                service.getAllWplJobAutoCal().done(function(data) {
                    data.forEach(c => {
                        let job_item: JobAlreadySettingModel = { id: c.jobId, isAlreadySetting: true };
                        let wkp_item: UnitAlreadySettingModel = { workplaceId: c.wkpId, isAlreadySetting: true };
                        wkpSettingList.push(wkp_item);
                        jobSettingList.push(job_item);
                    });
                    _self.wkpAlreadySettingList(wkpSettingList);
                    _self.jobAlreadySettingList(jobSettingList);
                    dfd.resolve();
                });
                return dfd.promise();
            }

            //load job already setting
            public loadJobAlreadySettingList(): JQueryPromise<void> {
                let _self = this;
                var settingList: any = [];
                var dfd = $.Deferred<any>();
                service.getAllJobAutoCal().done(function(data) {
                    data.forEach(c => {
                        let item: JobAlreadySettingModel = { id: c.jobId, isAlreadySetting: true };
                        settingList.push(item);
                    });
                    _self.jobAlreadySettingList(settingList);
                    dfd.resolve();
                });
                return dfd.promise();
            }


            //load workPlace already setting
            public loadWkpAlreadySettingList(): JQueryPromise<void> {
                let _self = this;
                var settingList: any = [];
                var dfd = $.Deferred<any>();
                service.getAllWkpAutoCal().done(function(data) {
                    data.forEach(c => {
                        let item: UnitAlreadySettingModel = { workplaceId: c.wkpId, isAlreadySetting: true };
                        settingList.push(item);
                    });
                    _self.wkpAlreadySettingList(settingList);
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
          * call service load UseUnitAutoCalSettingModel
          */
            private loadUseUnitAutoCalSettingModel(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<void>();
                nts.uk.at.view.kmk006.e.service.getUseUnitAutoCal().done(function(data) {
                    self.useUnitAutoCalSettingModel(new UnitAutoCalSettingView(data.useWkpSet, data.useJobSet, data.useJobwkpSet));
                    dfd.resolve();
                }).fail(res => {
                    nts.uk.ui.dialog.alertError(res);
                });
                return dfd.promise();
            }

            // All function
            // load AutoCalAtrOvertimeEnum
            private loadAutoCalAtrOvertimeEnum(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<any>();

                nts.uk.ui.block.invisible();

                // get setting
                service.findEnumAutoCalAtrOvertime().done(function(dataRes: Array<Enum>) {                  
                    self.autoCalAtrOvertimeEnum = dataRes;                  
                    nts.uk.ui.block.clear();

                    dfd.resolve();
                });

                return dfd.promise();
            }
            // All function
            // load AutoCalAtrOvertimeEnumWithoutTimeRecorder
            private loadAutoCalAtrOvertimeEnumWithoutTimeRecorder(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<any>();

                nts.uk.ui.block.invisible();

                // get setting
                service.findEnumAutoCalAtrOvertimeWithoutTimeRecorder().done(function(dataRes: Array<Enum>) {

                    self.autoCalAtrOvertimeEnumWithoutTimeRecorder = dataRes;

                    nts.uk.ui.block.clear();

                    dfd.resolve();
                });

                return dfd.promise();
            }
            // All function
            // load AutoCalAtrOvertimeEnum
            private loadTimeLimitUpperLimitSettingEnum(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<any>();

                nts.uk.ui.block.invisible();

                // get setting
                service.findEnumTimeLimitUpperLimitSetting().done(function(dataRes: Array<Enum>) {

                    self.timeLimitUpperLimitEnum = dataRes;

                    nts.uk.ui.block.clear();

                    dfd.resolve();
                });

                return dfd.promise();
            }

            /**
             * Screen A: Load ComAutoCal
             */
            private loadComAutoCal(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<any>();

                service.getComAutoCal().done((data) => {
                    if (data) {
                        self.itemComAutoCalModel.updateData(data);
                    } else {
                        self.itemComAutoCalModel.resetData();
                    }
                    self.reLoadListEnum(self.itemComAutoCalModel);
                    dfd.resolve();
                }).fail((res) => {
                    nts.uk.ui.dialog.alertError(res);
                    dfd.reject(res);
                });

                return dfd.promise();
            }

            /**
             * Screen B: Load WkpAutoCal
             */
            private loadWkpAutoCal(wkpId: string): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred<any>();
               
                if (wkpId) {
                    service.getWkpAutoCal(wkpId).done((data) => {
                        if (data) {
                            self.itemWkpAutoCalModel.updateData(data);
                        } else {
                            self.itemWkpAutoCalModel.resetData();
                        }
                        self.reLoadListEnum(self.itemWkpAutoCalModel);
                        dfd.resolve();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res);
                        dfd.reject(res);
                    });
                } else {
                    dfd.resolve();    
                }               

                return dfd.promise();
            }
            
            /**
             * Screen C: Load JobAutoCal
             */
            private loadJobAutoCal(jobId: string): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred<any>();                

                if (jobId) {
                    service.getJobAutoCal(jobId).done((data) => {
                        if (data) {
                            self.itemJobAutoCalModel.updateData(data);                           
                        } else {
                            self.itemJobAutoCalModel.resetData();
                        }
                        self.reLoadListEnum(self.itemJobAutoCalModel);
                        dfd.resolve();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res);
                        dfd.reject(res);
                    });
                } else {
                    dfd.resolve();    
                }               

                return dfd.promise();
            }
            
            // load  JobAutoCal
            private loadWkpJobAutoCal(wkpId: string, jobId: string): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred<any>();

                if (wkpId && jobId) {
                    service.getWkpJobAutoCal(wkpId, jobId).done((data) => {             
                        if (data) {
                            self.itemWkpJobAutoCalModel.updateData(data);
                        } else {
                            self.itemWkpJobAutoCalModel.resetData();
                        }
                        self.reLoadListEnum(self.itemWkpJobAutoCalModel);
                        dfd.resolve();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res);
                        dfd.reject(res);
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                } else {
                    dfd.resolve();    
                } 
                
                return dfd.promise();
            }

            private reLoadListEnum(list: any): void {
                var self = this;
                self.valueEnumNorEarLi(list.normalOTTime.earlyOtTime.upLimitOtSet());
                self.valueEnumNorEarAtr(list.normalOTTime.earlyOtTime.calAtr());
                self.valueEnumNorEarMidLi(list.normalOTTime.earlyMidOtTime.upLimitOtSet());
                self.valueEnumNorEarMidAtr(list.normalOTTime.earlyMidOtTime.calAtr());
                self.valueEnumNorNorLi(list.normalOTTime.normalOtTime.upLimitOtSet());
                self.valueEnumNorNorAtr(list.normalOTTime.normalOtTime.calAtr());
                self.valueEnumNorNorMidLi(list.normalOTTime.normalMidOtTime.upLimitOtSet());
                self.valueEnumNorNorMidAtr(list.normalOTTime.normalMidOtTime.calAtr());
                self.valueEnumNorLegLi(list.normalOTTime.legalOtTime.upLimitOtSet());
                self.valueEnumNorLegAtr(list.normalOTTime.legalOtTime.calAtr());
                self.valueEnumNorLegMidLi(list.normalOTTime.legalMidOtTime.upLimitOtSet());
                self.valueEnumNorLegMidAtr(list.normalOTTime.legalMidOtTime.calAtr());
                self.valueEnumFleTimeLi(list.flexOTTime.flexOtTime.upLimitOtSet());
                self.valueEnumFleTimeAtr(list.flexOTTime.flexOtTime.calAtr());
                self.valueEnumResResLi(list.restTime.restTime.upLimitOtSet());
                self.valueEnumResResAtr(list.restTime.restTime.calAtr());                
                self.valueEnumResLatLi(list.restTime.lateNightTime.upLimitOtSet());
                self.valueEnumResLatAtr(list.restTime.lateNightTime.calAtr());
                self.autoCalcOfLeaveLate(list.leaveEarly.autoCalcOfLeaveLate());
                self.autoCalcOfLeaveEarly(list.leaveEarly.autoCalcOfLeaveEarly());
                self.raisingSalaryCalcAtr(list.raisingSalary.raisingSalaryCalcAtr());
                self.specificRaisingSalaryCalcAtr(list.raisingSalary.specificRaisingSalaryCalcAtr());
                self.divergenceTime(list.divergenceTime());
            }

            private saveListEnum(list: any): void {
                var self = this;
                list.normalOTTime.earlyOtTime.upLimitOtSet(self.valueEnumNorEarLi());
                list.normalOTTime.earlyOtTime.calAtr(self.valueEnumNorEarAtr());
                list.normalOTTime.earlyMidOtTime.upLimitOtSet(self.valueEnumNorEarMidLi());
                list.normalOTTime.earlyMidOtTime.calAtr(self.valueEnumNorEarMidAtr());
                list.normalOTTime.normalOtTime.upLimitOtSet(self.valueEnumNorNorLi());
                list.normalOTTime.normalOtTime.calAtr(self.valueEnumNorNorAtr());
                list.normalOTTime.normalMidOtTime.upLimitOtSet(self.valueEnumNorNorMidLi());
                list.normalOTTime.normalMidOtTime.calAtr(self.valueEnumNorNorMidAtr());
                list.normalOTTime.legalOtTime.upLimitOtSet(self.valueEnumNorLegLi());
                list.normalOTTime.legalOtTime.calAtr(self.valueEnumNorLegAtr());
                list.normalOTTime.legalMidOtTime.upLimitOtSet(self.valueEnumNorLegMidLi());
                list.normalOTTime.legalMidOtTime.calAtr(self.valueEnumNorLegMidAtr());
                list.flexOTTime.flexOtTime.upLimitOtSet(self.valueEnumFleTimeLi());
                list.flexOTTime.flexOtTime.calAtr(self.valueEnumFleTimeAtr());
                list.restTime.restTime.upLimitOtSet(self.valueEnumResResLi());
                list.restTime.restTime.calAtr(self.valueEnumResResAtr());
                list.restTime.lateNightTime.upLimitOtSet(self.valueEnumResLatLi());
                list.restTime.lateNightTime.calAtr(self.valueEnumResLatAtr());
                list.leaveEarly.autoCalcOfLeaveLate(self.autoCalcOfLeaveLate());
                list.leaveEarly.autoCalcOfLeaveEarly(self.autoCalcOfLeaveEarly());
                list.raisingSalary.raisingSalaryCalcAtr(self.raisingSalaryCalcAtr());
                list.raisingSalary.specificRaisingSalaryCalcAtr(self.specificRaisingSalaryCalcAtr());
                list.divergenceTime(self.divergenceTime());
            }

            /**
             * function on click saveCompanyAutoCal action
             */
            public saveCompanyAutoCal(): void {
                if ($('.nts-input').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.block.invisible();
                var self = this;

                // save enum
                self.saveListEnum(self.itemComAutoCalModel);
                
                var dto: ComAutoCalSettingDto = self.itemComAutoCalModel.toDto();
                service.saveComAutoCal(dto).done(function() {
                    // show message 15
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                        // reload pa    
                        self.loadComAutoCal();
                    });
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }

            /**
            * function on click saveCompanyAutoCal action
            */
            public saveJobAutoCal(): void {
                if ($('.nts-input').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.block.invisible();
                var self = this;
                
                if (nts.uk.text.isNullOrEmpty(self.selectedCode())){
                    nts.uk.ui.block.clear();
                    return;    
                }

                // save enum
                self.saveListEnum(self.itemJobAutoCalModel);
                var jobId = self.selectedCurrentJob();

                var dto: JobAutoCalSettingDto = self.itemJobAutoCalModel.toDto();
                dto.jobId = jobId;

                service.saveJobAutoCal(dto).done(function() {
                     self.loadJobAlreadySettingList().done(function() {
                        // show message 15
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
//                            $('#component-items-list').ntsListComponent(self.jobListOptions).done(function() {
                                // reload pa    
                                self.selectedCode(jobId);
                                self.loadJobAutoCal(jobId);
                                self.createModeScreenC(true);  
//                            });
                        });
                    });
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }

            /**
            * function on click saveCompanyAutoCal action
            */
            public saveWkpAutoCal(): void {
                if ($('.nts-input').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.block.invisible();
                var self = this;
                
                if (nts.uk.text.isNullOrEmpty(self.multiSelectedWorkplaceId())) {
                    nts.uk.ui.block.clear();
                    return;    
                }

                // save enum
                self.saveListEnum(self.itemWkpAutoCalModel);
                var wkpId = self.selectedCurrentWkp();

                var dto: WkpAutoCalSettingDto = self.itemWkpAutoCalModel.toDto();
                dto.wkpId = wkpId; 

                service.saveWkpAutoCal(dto).done(function() {
                    self.loadWkpAlreadySettingList().done(function() {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                            // reload pa    
                            self.multiSelectedWorkplaceId(wkpId);
                            self.loadWkpAutoCal(wkpId);
                            self.createModeScreenB(true);
                        });
                    });
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }


            public saveWkpJobAutoCal(wkpId: string, jobId: string): void {         
                nts.uk.ui.block.invisible();
                var self = this;
                
                self.clearAllError();
                let isError: boolean = false;
                //$('.nts-input').ntsEditor('validate');
                if ($('.nts-input').ntsError('hasError')) {
                    isError = true;
                    nts.uk.ui.block.clear();
                }             
                if (nts.uk.text.isNullOrEmpty(self.totalSelectedWorkplaceId())){
                    isError = true;
                    $('#tree-grid').ntsError('set', {messageId:"Msg_719"});
                    nts.uk.ui.block.clear();
                }
                if (nts.uk.text.isNullOrEmpty(self.totalSelectedCode())){
                    isError = true;
                    $('#jobtitles').ntsError('set', {messageId:"Msg_720"});
                    nts.uk.ui.block.clear();                   
                }
                if (isError) {
                    return;       
                }

                // Save enum
                self.saveListEnum(self.itemWkpJobAutoCalModel);
                var jobId = self.selectedCurrentJob();
                var wkpId = self.selectedCurrentWkp();


                var dto: WkpJobAutoCalSettingDto = self.itemWkpJobAutoCalModel.toDto();
                dto.jobId = jobId;
                dto.wkpId = wkpId;

                service.saveWkpJobAutoCal(dto).done(function() {
                    // show message 15
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                        // reload pa    
                        self.loadWkpJobAutoCal(wkpId, jobId);
                        self.createModeScreenD(true);
                    });
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });                       
            }

            /**
             * Apply base date
             */
            public applyBaseDate(): void {
                $('#input-date').ntsError('clear');
                $('#input-date').ntsEditor('validate');
                if ($('#input-date').ntsError('hasError')) {
                    return;
                };
                var self = this;

                // Apply input date
                if (!self.inputDate()) {
                    return;
                } 
                
                let emptyBaseDate: boolean = (nts.uk.text.isNullOrEmpty(self.baseDateJobListTotal().toString()) 
                || nts.uk.text.isNullOrEmpty(self.baseDateTreeListTotal().toString())) 
                self.baseDateJobListTotal(self.inputDate());
                self.baseDateTreeListTotal(self.inputDate());
                
                // Reload table
                if (!emptyBaseDate) {
                    $('#tree-grid').ntsTreeComponent(self.treeOptionsWkpTotal).done(function() {
                        let code = $('#tree-grid').getDataList()[0].workplaceId;
                        self.totalSelectedWorkplaceId(code);
                    });
    
                    $('#jobtitles').ntsListComponent(self.jobTotalListOptions).done(function() {
                        let code = $('#jobtitles').getDataList()[0].id;
                        self.totalSelectedCode(code);
                        self.loadWkpJobAutoCal(self.multiSelectedWorkplaceId(),  self.totalSelectedCode());
                        // load ready setting
                        self.loadWkpJobAlreadySettingList().done(function() {
    
                        });
                    });   
                }               
            }
            
            // delete Pattern
            public deleteJobAutoCal() {
                let self = this;
                
                if (nts.uk.text.isNullOrEmpty(self.selectedCode())) {
                    return;    
                }

                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    nts.uk.ui.block.grayout();

                    service.deleteJobAutoCal(self.selectedCurrentJob()).done(function() {
                        self.loadJobAlreadySettingList().done(function() {
                            // show message 16
                            nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
//                                $('#component-items-list').ntsListComponent(self.jobListOptions).done(function() {
                                    self.loadJobAutoCal(self.selectedCurrentJob());
                                    self.selectedCode(self.selectedCurrentJob());
                                    self.createModeScreenC(false);
//                                });
                            });
                        });
                       
                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError(res.message).then(() => { nts.uk.ui.block.clear(); });
                    }).always(function() {
                        nts.uk.ui.block.clear();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                });

            }

            // delete Pattern
            public deleteWkpAutoCal() {
                let self = this;
                
                if (nts.uk.text.isNullOrEmpty(self.multiSelectedWorkplaceId())) {
                    return;    
                }

                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    nts.uk.ui.block.grayout();

                    service.deleteWkpAutoCal(self.selectedCurrentWkp()).done(function() {
                        self.loadWkpAlreadySettingList().done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
//                                $('#tree-grid-srcc').ntsTreeComponent(self.treeOptionsWkp).done(function() {
                                    self.loadWkpAutoCal(self.selectedCurrentWkp());
                                    self.multiSelectedWorkplaceId(self.selectedCurrentWkp());
                                    self.createModeScreenB(false);
//                                });
                            });
                           
                        });
                        
                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError(res.message).then(() => { nts.uk.ui.block.clear(); });
                    }).always(function() {
                        nts.uk.ui.block.clear();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                });

            }

            // delete Pattern
            public deleteWkpJobAutoCal() {
                let self = this;
                
                if (nts.uk.text.isNullOrEmpty(self.totalSelectedWorkplaceId())) {
                    return;    
                }
                if (nts.uk.text.isNullOrEmpty(self.totalSelectedCode())) {
                    return;    
                }

                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    nts.uk.ui.block.grayout();

                    service.deleteWkpJobAutoCal(self.selectedCurrentWkp(), self.selectedCurrentJob()).done(function() {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
                            self.loadWkpJobAutoCal(self.selectedCurrentJob(), self.selectedCurrentWkp());
                            self.loadWkpJobAlreadySettingList();
                            self.createModeScreenD(false);
                        });
                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError(res.message).then(() => { nts.uk.ui.block.clear(); });
                    }).always(function() {
                        nts.uk.ui.block.clear();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                });

            }

            /**
         * on click tab panel company action event
         */
            public onSelectCompany(): JQueryPromise<void> {            
                var self = this;
                self.selectedTab('tab-1');
                var dfd = $.Deferred<void>();

                self.clearAllError();
                self.isLoading(true);
                nts.uk.ui.block.grayout();
                self.loadComAutoCal().then(() => { nts.uk.ui.block.clear(); })
                self.isLoading(false);
                $(window).resize();
                dfd.resolve();

                return dfd.promise();
            }

            public onSelectJobTitle(): void {
                var self = this;
                self.selectedTab('tab-1');

                self.clearAllError();
                self.baseDateJobList(moment(new Date()).toDate());
                self.isLoading(true);
                                   
                nts.uk.ui.block.grayout();
                self.loadJobAlreadySettingList().done(function() {
                    $('#component-items-list').ntsListComponent(self.jobListOptions).done(function() {
                        let code = $('#component-items-list').getDataList()[0].id;
                        self.selectedCode(code);
                        self.loadJobAutoCal(code);
                        nts.uk.ui.block.clear();
                    });
                })
                self.isLoading(false);
                $(window).resize();
            }


            /**
          * on click tab panel employment action event
          */
            public onSelectWorkplace(): void {
                var self = this;
                self.selectedTab('tab-1');

                self.clearAllError();
                self.baseDateTreeList(moment(new Date()).toDate());
                self.isLoading(true);
                
                nts.uk.ui.block.grayout();
                self.loadWkpAlreadySettingList().done(function() {
                    $('#tree-grid-srcc').ntsTreeComponent(self.treeOptionsWkp).done(function() {
                        self.loadWkpAutoCal(self.multiSelectedWorkplaceId()).then(() => {nts.uk.ui.block.clear();});                       
                    });                  
                })
                self.isLoading(false);
                $(window).resize();
            }

            public onSelectWkpJob(): void {
                var self = this;
                self.selectedTab('tab-1');
                self.inputDate(new Date());
                self.clearAllError();
                self.baseDateJobListTotal(moment(new Date()).toDate());
                self.baseDateTreeListTotal(moment(new Date()).toDate());
                self.isLoading(true);

                // Check Msg_374
                if (nts.uk.text.isNullOrEmpty(self.baseDateJobListTotal().toString()) 
                || nts.uk.text.isNullOrEmpty(self.baseDateTreeListTotal().toString())) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_374" });   
                    return;                 
                }
                
                //load grid          
                nts.uk.ui.block.grayout();
                $.when($('#tree-grid').ntsTreeComponent(self.treeOptionsWkpTotal), $('#jobtitles').ntsListComponent(self.jobTotalListOptions))
                    .done(function() {
                        let code = $('#jobtitles').getDataList()[0].id;
                        self.totalSelectedCode(code);
                                              
                        // load ready setting
                        self.loadWkpJobAlreadySettingList().done(function() {
                            self.loadWkpJobAutoCal(self.totalSelectedWorkplaceId(), code).done(() => {nts.uk.ui.block.clear();});                          
                        });
                    })

                    self.isLoading(false);
                $(window).resize();
            }

            /**
             * call service find all by employee id
             */
            public findAllByEmployeeIds(employeeIds: string[]): JQueryPromise<UnitAlreadySettingModel[]> {
                var dfd = $.Deferred();
                var dataRes: UnitAlreadySettingModel[] = [];
                dfd.resolve(dataRes);
                return dfd.promise();
            }

            /**
             * open dialog openDialogUsageSettingModel (view model E)
             */
            private openDialogUsageSettingModel(): void {
                var self = this;
                nts.uk.ui.windows.sub.modal("/view/kmk/006/e/index.xhtml").onClosed(function() {
                    self.loadUseUnitAutoCalSettingModel().done(function(){});
                });
            }

            private exportExcel(domainId: string) {
                var self = this;
                let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
                let domainType = "KMK006";
                if (program.length > 1){
                    program.shift();
                    domainType = domainType + program.join(" ");
                }
                let baseDate: any = moment.utc(self.baseDateTreeList(), 'YYYY/MM/DD').toISOString();
                let useUnit: any = self.useUnitAutoCalSettingModel();
                service.exportExcel(self.langId(), domainId, domainType, useUnit, baseDate)
                    .fail(function (res) {
                        nts.uk.ui.dialog.alertError(res);
                    });
            }
            
            private clearAllError() {
                nts.uk.ui.errors.clearAll();
            }          
        }
        // exportclass
        export class TreeType {
            static WORK_PLACE = 1;
        }
        //        SELECTIONTYPE
        export class SelectionType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;

        }

        export class BaseAutoCalSettingModel {
            normalOTTime: AutoCalOvertimeSettingModel;
            flexOTTime: AutoCalFlexOvertimeSettingModel;
            restTime: AutoCalRestTimeSettingModel;
            leaveEarly: AutoCalcOfLeaveEarlySettingModel;
            raisingSalary: AutoCalRaisingSalarySettingModel;
            divergenceTime: KnockoutObservable<number>;

            constructor() {
                this.normalOTTime = new AutoCalOvertimeSettingModel();
                this.flexOTTime = new AutoCalFlexOvertimeSettingModel();
                this.restTime = new AutoCalRestTimeSettingModel();
                this.leaveEarly = new AutoCalcOfLeaveEarlySettingModel();
                this.raisingSalary = new AutoCalRaisingSalarySettingModel();
                this.divergenceTime = ko.observable(1);

            }
        }

        export class WkpJobAutoCalSettingModel extends BaseAutoCalSettingModel {
            wkpId: KnockoutObservable<string>;
            jobId: KnockoutObservable<string>;

            constructor() {
                super();
                this.wkpId = ko.observable('');
                this.jobId = ko.observable('');
            }

            updateData(dto: WkpJobAutoCalSettingDto) {
                this.wkpId(dto.wkpId);
                this.jobId(dto.jobId);
                this.normalOTTime.updateData(dto.normalOTTime);
                this.flexOTTime.updateData(dto.flexOTTime);
                this.restTime.updateData(dto.restTime);
                this.leaveEarly.updateData(dto.leaveEarly);
                this.raisingSalary.updateData(dto.raisingSalary);
                this.divergenceTime(dto.divergenceTime);

            }

            toDto(): WkpJobAutoCalSettingDto {
                var dto: WkpJobAutoCalSettingDto = {
                    wkpId: this.wkpId(),
                    jobId: this.jobId(),
                    normalOTTime: this.normalOTTime.toDto(),
                    flexOTTime: this.flexOTTime.toDto(),
                    restTime: this.restTime.toDto(),
                    leaveEarly: this.leaveEarly.toDto(),
                    raisingSalary: this.raisingSalary.toDto(),
                    divergenceTime: this.divergenceTime()
                };
                return dto;
            }
            resetData() {
                this.wkpId('');
                this.jobId('');
                this.normalOTTime.resetData();
                this.flexOTTime.resetData();
                this.restTime.resetData();
                this.leaveEarly.resetData();
                this.raisingSalary.resetData();
                this.divergenceTime(0);
            }
        }

        export class WkpAutoCalSettingModel extends BaseAutoCalSettingModel {
            wkpId: KnockoutObservable<string>;

            constructor() {
                super();
                this.wkpId = ko.observable('');
            }

            updateData(dto: WkpAutoCalSettingDto) {
                this.wkpId(dto.wkpId);
                this.normalOTTime.updateData(dto.normalOTTime);
                this.flexOTTime.updateData(dto.flexOTTime);
                this.restTime.updateData(dto.restTime);
                this.leaveEarly.updateData(dto.leaveEarly);
                this.raisingSalary.updateData(dto.raisingSalary);
                this.divergenceTime(dto.divergenceTime);

            }

            toDto(): WkpAutoCalSettingDto {
                var dto: WkpAutoCalSettingDto = {
                    wkpId: this.wkpId(),
                    normalOTTime: this.normalOTTime.toDto(),
                    flexOTTime: this.flexOTTime.toDto(),
                    restTime: this.restTime.toDto(),
                    leaveEarly: this.leaveEarly.toDto(),
                    raisingSalary: this.raisingSalary.toDto(),
                    divergenceTime: this.divergenceTime()
                };
                return dto;
            }
            resetData() {
                this.wkpId('');
                this.normalOTTime.resetData();
                this.flexOTTime.resetData();
                this.restTime.resetData();
                this.leaveEarly.resetData();
                this.raisingSalary.resetData();
                this.divergenceTime(0);
            }
        }


        export class JobAutoCalSettingModel extends BaseAutoCalSettingModel {
            jobId: KnockoutObservable<string>;

            constructor() {
                super();
                this.jobId = ko.observable('');
            }

            updateData(dto: JobAutoCalSettingDto) {
                this.jobId(dto.jobId);
                this.normalOTTime.updateData(dto.normalOTTime);
                this.flexOTTime.updateData(dto.flexOTTime);
                this.restTime.updateData(dto.restTime);
                this.leaveEarly.updateData(dto.leaveEarly);
                this.raisingSalary.updateData(dto.raisingSalary);
                this.divergenceTime(dto.divergenceTime);

            }

            toDto(): JobAutoCalSettingDto {
                var dto: JobAutoCalSettingDto = {
                    jobId: this.jobId(),
                    normalOTTime: this.normalOTTime.toDto(),
                    flexOTTime: this.flexOTTime.toDto(),
                    restTime: this.restTime.toDto(),
                    leaveEarly: this.leaveEarly.toDto(),
                    raisingSalary: this.raisingSalary.toDto(),
                    divergenceTime: this.divergenceTime()
                };
                return dto;
            }
            resetData() {
                this.jobId('');
                this.normalOTTime.resetData();
                this.flexOTTime.resetData();
                this.restTime.resetData();
                this.leaveEarly.resetData();
                this.raisingSalary.resetData();
                this.divergenceTime(0);
            }
        }

        //        ComAutoCalSettingModel
        export class ComAutoCalSettingModel extends BaseAutoCalSettingModel {
            constructor() {
                super();
            }

            updateData(dto: ComAutoCalSettingDto) {
                this.normalOTTime.updateData(dto.normalOTTime);
                this.flexOTTime.updateData(dto.flexOTTime);
                this.restTime.updateData(dto.restTime);
                this.leaveEarly.updateData(dto.leaveEarly);
                this.raisingSalary.updateData(dto.raisingSalary);
                this.divergenceTime(dto.divergenceTime);

            }

            toDto(): ComAutoCalSettingDto {
                var dto: ComAutoCalSettingDto = {
                    normalOTTime: this.normalOTTime.toDto(),
                    flexOTTime: this.flexOTTime.toDto(),
                    restTime: this.restTime.toDto(),
                    leaveEarly: this.leaveEarly.toDto(),
                    raisingSalary: this.raisingSalary.toDto(),
                    divergenceTime: this.divergenceTime()
                };
                return dto;
            }
            resetData() {
                this.normalOTTime.resetData();
                this.flexOTTime.resetData();
                this.restTime.resetData();
                this.leaveEarly.resetData();
                this.raisingSalary.resetData();
                this.divergenceTime(0);
            }
        }
        //        AutoCalOvertimeSettingDto
        export class AutoCalFlexOvertimeSettingModel {
            flexOtTime: AutoCalSettingModel;

            constructor() {
                this.flexOtTime = new AutoCalSettingModel();

            }

            updateData(dto: AutoCalFlexOvertimeSettingDto) {
                this.flexOtTime.updateData(dto.flexOtTime);

            }

            toDto(): AutoCalFlexOvertimeSettingDto {
                var dto: AutoCalFlexOvertimeSettingDto = {
                    flexOtTime: this.flexOtTime.toDto(),

                };
                return dto;
            }
            resetData() {
                this.flexOtTime.resetDataNormal();
            }
        }

        //        AutoCalRestTimeSettingDto
        export class AutoCalRestTimeSettingModel {
            restTime: AutoCalSettingModel;
            lateNightTime: AutoCalSettingModel;

            constructor() {
                this.restTime = new AutoCalSettingModel();
                this.lateNightTime = new AutoCalSettingModel();

            }

            updateData(dto: AutoCalRestTimeSettingDto) {
                this.restTime.updateData(dto.restTime);
                this.lateNightTime.updateData(dto.lateNightTime);

            }

            toDto(): AutoCalRestTimeSettingDto {
                var dto: AutoCalRestTimeSettingDto = {
                    restTime: this.restTime.toDto(),
                    lateNightTime: this.lateNightTime.toDto(),

                };
                return dto;
            }
            resetData() {
                this.restTime.resetDataNormal();
                this.lateNightTime.resetDataNormal();
            }
        }
        //        AutoCalFlexOvertimeSettingDto
        export class AutoCalOvertimeSettingModel {
            earlyOtTime: AutoCalSettingModel;
            earlyMidOtTime: AutoCalSettingModel;
            normalOtTime: AutoCalSettingModel;
            normalMidOtTime: AutoCalSettingModel;
            legalOtTime: AutoCalSettingModel;
            legalMidOtTime: AutoCalSettingModel;

            constructor() {
                this.earlyOtTime = new AutoCalSettingModel();
                this.earlyMidOtTime = new AutoCalSettingModel();
                this.normalOtTime = new AutoCalSettingModel();
                this.normalMidOtTime = new AutoCalSettingModel();
                this.legalOtTime = new AutoCalSettingModel();
                this.legalMidOtTime = new AutoCalSettingModel();

            }

            updateData(dto: AutoCalOvertimeSettingDto) {
                this.earlyOtTime.updateData(dto.earlyOtTime);
                this.earlyMidOtTime.updateData(dto.earlyMidOtTime);
                this.normalOtTime.updateData(dto.normalOtTime);
                this.normalMidOtTime.updateData(dto.normalMidOtTime);
                this.legalOtTime.updateData(dto.legalOtTime);
                this.legalMidOtTime.updateData(dto.legalMidOtTime);

            }

            toDto(): AutoCalOvertimeSettingDto {
                var dto: AutoCalOvertimeSettingDto = {
                    earlyOtTime: this.earlyOtTime.toDto(),
                    earlyMidOtTime: this.earlyMidOtTime.toDto(),
                    normalOtTime: this.normalOtTime.toDto(),
                    normalMidOtTime: this.normalMidOtTime.toDto(),
                    legalOtTime: this.legalOtTime.toDto(),
                    legalMidOtTime: this.legalMidOtTime.toDto()

                };
                return dto;
            }
            resetData() {
                this.earlyOtTime.resetDataNormal();
                this.earlyMidOtTime.resetDataNormal();
                this.normalOtTime.resetDataNormal();
                this.normalMidOtTime.resetDataNormal();
                this.legalOtTime.resetDataNormal();
                this.legalMidOtTime.resetDataNormal();
            }
        }
        
        // AutoCalRaisingSalarySettingDto
        export class AutoCalRaisingSalarySettingModel{
            raisingSalaryCalcAtr: KnockoutObservable<boolean>;
            specificRaisingSalaryCalcAtr: KnockoutObservable<boolean>;    
            
            constructor(){
                this.raisingSalaryCalcAtr = ko.observable(false);
                this.specificRaisingSalaryCalcAtr = ko.observable(false);
            }
            
            updateData(dto: AutoCalRaisingSalarySettingDto) {
                this.raisingSalaryCalcAtr(dto.raisingSalaryCalcAtr);
                this.specificRaisingSalaryCalcAtr(dto.specificRaisingSalaryCalcAtr);
            }

            toDto(): AutoCalRaisingSalarySettingDto {
                var dto: AutoCalRaisingSalarySettingDto = {
                    raisingSalaryCalcAtr: this.raisingSalaryCalcAtr(),
                    specificRaisingSalaryCalcAtr: this.specificRaisingSalaryCalcAtr(),
                };
                return dto;
            }
            resetData() {
                this.raisingSalaryCalcAtr(false);
                this.specificRaisingSalaryCalcAtr(false);
            }
        }
        
        // AutoCalcOfLeaveEarlySettingDto
        export class AutoCalcOfLeaveEarlySettingModel{
            autoCalcOfLeaveLate: KnockoutObservable<boolean>;
            autoCalcOfLeaveEarly: KnockoutObservable<boolean>;            
            constructor(){
                this.autoCalcOfLeaveLate = ko.observable(false);
                this.autoCalcOfLeaveEarly = ko.observable(false);    
            }
            
            updateData(dto: AutoCalcOfLeaveEarlySettingDto) {
                this.autoCalcOfLeaveLate(dto.late);
                this.autoCalcOfLeaveEarly(dto.leaveEarly);
            }

            toDto(): AutoCalcOfLeaveEarlySettingDto {
                var dto: AutoCalcOfLeaveEarlySettingDto = {
                    late: this.autoCalcOfLeaveLate(),
                    leaveEarly: this.autoCalcOfLeaveEarly(),
                };
                return dto;
            }
            resetData() {
                this.autoCalcOfLeaveLate(false);
                this.autoCalcOfLeaveEarly(false);
            }
        }
               
        //        AutoCalSettingDto
        export class AutoCalSettingModel {
            upLimitOtSet: KnockoutObservable<number>;
            calAtr: KnockoutObservable<number>;
            constructor() {
                this.upLimitOtSet = ko.observable(1);
                this.calAtr = ko.observable(1);
            }
            updateData(dto: AutoCalSettingDto) {
                this.upLimitOtSet(dto.upLimitOtSet);
                this.calAtr(dto.calAtr);
            }

            toDto(): AutoCalSettingDto {
                var dto: AutoCalSettingDto = {
                    upLimitOtSet: this.upLimitOtSet(),
                    calAtr: this.calAtr(),
                };
                return dto;
            }
            resetData() {
                this.upLimitOtSet(1);
                this.calAtr(1);
            }
            
            resetDataNormal() {
                this.upLimitOtSet(0);
                this.calAtr(0);
            }
        }

        export class UnitAutoCalSettingView {
            useWkpSet: any;
            useJobSet: any;
            useJobwkpSet: any;

            constructor(useWkpSet: boolean, useJobSet: boolean, useJobwkpSet: boolean) {
                this.useWkpSet = useWkpSet;
                this.useJobSet = useJobSet;
                this.useJobwkpSet = useJobwkpSet;
            }
        }


        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export interface UnitModel {
            code: string;
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
            workplaceId: string;
            isAlreadySetting: boolean;
        }

        export interface JobAlreadySettingModel {
            id: string;
            isAlreadySetting: boolean;
        }

        export class SIDEBAR_TAB_INDEX {
            static WORKPLACE = 1;
            static JOBTITLE = 2;
            static WORKPLACE_JOBTITLE = 3;
        }

    }
}