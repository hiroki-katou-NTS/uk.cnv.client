module nts.uk.at.view.ksu001.a.viewmodel {
    import alert = nts.uk.ui.dialog.alert;
    import alertError = nts.uk.ui.dialog.alertError;
    import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    import blockUI = nts.uk.ui.block;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import formatById = nts.uk.time.format.byId;
    import duration = nts.uk.time.minutesBased.duration;
    import openDialog = nts.uk.ui.windows.sub.modal;
    import getText = nts.uk.resource.getText;
    import util = nts.uk.util;
    import bundledErrors = nts.uk.ui.dialog.bundledErrors;
    import characteristics = nts.uk.characteristics;

    /**
     * load screen O->Q->A
     * reference file a.start.ts
     */
    export class ScreenModel {
        userInfor: IUserInfor = {};
        employeeIdLogin: string = __viewContext.user.employeeId;
        keyGrid: string;
        rowIndexOfEmpLogin : number;

        enableBtnReg : KnockoutObservable<boolean> = ko.observable(false);
        visibleShiftPalette: KnockoutObservable<boolean> = ko.observable(true);
        mode: KnockoutObservable<string> = ko.observable('edit'); // edit || confirm 
        showA9: boolean;

        // A4_4
        modeDisplayList: KnockoutObservableArray<any>;
        selectedModeDisplayInBody: KnockoutObservable<number> = ko.observable(undefined);

        // A4_7
        achievementDisplaySelected: KnockoutObservable<number> = ko.observable(undefined); // 1 || 2

        // A4_12
        backgroundColorSelected: KnockoutObservable<string> = ko.observable(undefined);  // 0 || 1
        showComboboxA4_12: KnockoutObservable<boolean> = ko.observable(true);

        popupVal: KnockoutObservable<string> = ko.observable('');
        selectedDate: KnockoutObservable<string> = ko.observable('');

        //Date time A3_1

        currentDate: Date = new Date();
        dtPrev: KnockoutObservable<Date> = ko.observable(new Date()); // A3_1_2
        dtAft: KnockoutObservable<Date> = ko.observable(new Date());  // A3_1_4
        dateTimePrev: KnockoutObservable<string>;
        dateTimeAfter: KnockoutObservable<string>;

        //Switch  A3_2
        disPeriodSelectionList: KnockoutObservableArray<any>;
        selectedDisplayPeriod: KnockoutObservable<number> = ko.observable(1);

        // A2_2
        targetOrganizationName: KnockoutObservable<string> = ko.observable('');

        // popup Setting Grid
        selectedTypeHeightExTable: KnockoutObservable<number> = ko.observable(1);
        heightGridSetting: KnockoutObservable<string> = ko.observable('');
        isEnableInputHeight: KnockoutObservable<boolean> = ko.observable(false);

        // dùng cho xử lý của botton toLeft, toRight, toDown
        indexBtnToLeft: number = 0;
        indexBtnToRight: number = 0;
        indexBtnToDown: number = 0;

        enableBtnPaste: KnockoutObservable<boolean>  = ko.observable(true);
        enableBtnCoppy: KnockoutObservable<boolean>  = ko.observable(true);
        enableBtnInput: KnockoutObservable<boolean>  = ko.observable(true);
        visibleBtnInput: KnockoutObservable<boolean> = ko.observable(true);
        enableBtnUndo: KnockoutObservable<boolean>   = ko.observable(true);
        enableBtnRedo: KnockoutObservable<boolean>   = ko.observable(true);
        visibleBtnUndo: KnockoutObservable<boolean>  = ko.observable(true);
        visibleBtnRedo: KnockoutObservable<boolean>  = ko.observable(true);
        enableHelpBtn: KnockoutObservable<boolean>   = ko.observable(true);

        arrDay: Time[] = [];
        listSid: KnockoutObservableArray<string> = ko.observableArray([]);
        listEmpData = [];
        
        listCheckNeededOfWorkTime: KnockoutObservableArray<any> = ko.observableArray([]);

        flag: boolean = true;
        KEY: string = 'ksu001Data';
        dataCell: any; // data để paste vào grid
        
        // data grid
        detailContentDeco = [];
        detailContentDecoModeConfirm = [];
        detailColumns = [];
        detailContentDs = [];
        detailHeaderDeco: any = [];
        vertSumColumns: any = [];
        vertSumContentDs: any = [];
        vertContentDeco: any = [];
        horizontalDetailColumns: any = [];
        leftHorzContentDs: any = [];
        horizontalSumContentDs: any = [];
        rightHorzContentDs: any = [];
        listEmpInfo = [];
        listWorkScheduleWorkInforBase  = [];
        listWorkScheduleShiftBase      = [];
        listWorkScheduleWorkInfor  = [];
        listWorkScheduleShift      = [];
        listPersonalConditions     = [];
        displayControlPersonalCond = {};
        listDateInfo     = [];
        
        // shift mode
        detailContentDecoNormal = [];  // deco ở mode edit + background normal
        detailContentDecoShift = [];   // deco ở mode edit + background shift
        detailContentDecoModeConfirmNormal = []; // deco ở mode confirm + background normal
        detailContentDecoModeConfirmShift = [];  // deco ở mode confirm + background normal
        // dùng cho trường hợp thay đổi modeBackground
        hasChangeModeBg = false;
        listCellUpdatedWhenChangeModeBg = [];
        
        showTeamCol = false;
        showRankCol = false;
        showQualificCol = false;
        widthMid : number = 0;
        pathToLeft = '';
        pathToRight = '';
        pathToDown = '';
        pathToUp = '';
        
        // param kcp015
        baseDate: KnockoutObservable<string> = ko.observable('');
        sids: KnockoutObservableArray<any> = ko.observableArray([]);
                
        A1_7_3_line1: KnockoutObservable<string> = ko.observable('');
        A1_7_3_line2: KnockoutObservable<string> = ko.observable('');
        
        // share tooltip to ksu003 
        tooltipShare: Array<any> = [];
        scheduleModifyStartDate = null;
        canOpenKsu003 = true;
        
        listTimeDisable = []; // lưu nhưng cell bí disalble do không có worktime
        listLockCells = [];  // lưu những cell confirm khi khởi động
        listWorkTypeInfo = [];// listWorkTypecombobox
        
        visibleA4_234: KnockoutObservable<boolean>   = ko.observable(true);
        visibleA4_567: KnockoutObservable<boolean>   = ko.observable(true);
        visibleA4_8: KnockoutObservable<boolean>   = ko.observable(true);
        visibleA4_9: KnockoutObservable<boolean>   = ko.observable(true);
        canRegisterEvent = true;
        
        // 個人計カテゴリ
        useCategoriesPersonal: KnockoutObservableArray<any> = ko.observableArray([]);
        useCategoriesPersonalValue: KnockoutObservable<number> = ko.observable(null);
		dataAggreratePersonal: any = null;
		useCategoriesPersonalFull: any = [];
        
        // 職場計カテゴリ
        useCategoriesWorkplace: KnockoutObservableArray<any> = ko.observableArray([]);
        useCategoriesWorkplaceValue: KnockoutObservable<any> = ko.observable(null);
		dataAggrerateWorkplace: any = null;
		useCategoriesWorkplaceFull: any = [];
        
        // 締め日 (Deadline) , 初期起動時の期間 ( Initial startup period )
        closeDate = null;
        startDateInitStart = null;  // 初期起動時の期間
        endDateInitStart = null;    // 初期起動時の期間
        medicalOP = false;
        nursingCareOP = false;
        widthA8 : number = 200;
        widthBtnToLeftToRight : number = 30; // width button 
        offetLeftGrid : number = 30; // khoang cách từ mép trái vào đến grid (offetLeftGrid)
        widthVertSum : number = 200;
        widthScrollRG = 30;
        timeColor = '#595959';

        showA11: KnockoutObservable<boolean>   = ko.observable(false);
        showA12: KnockoutObservable<boolean>   = ko.observable(false);
        showA12_2: KnockoutObservable<boolean>   = ko.observable(false);
        funcNo15_WorkPlace: boolean = false;
        changeableWorks = [];
        callAlgSumA12 = true;
        taskId = null;
        
        constructor(dataLocalStorage) {
            let self = this;
            
            self.userInfor =  dataLocalStorage;
            
            //Date time
            self.dateTimeAfter = ko.observable(moment(self.dtAft()).format('YYYY/MM/DD'));
            self.dateTimePrev = ko.observable(moment(self.dtPrev()).format('YYYY/MM/DD'));

            self.dtPrev.subscribe((newValue) => {
                self.dateTimePrev(moment(self.dtPrev()).format('YYYY/MM/DD'));
            });
            
            self.dtAft.subscribe((newValue) => {
                self.dateTimeAfter(moment(self.dtAft()).format('YYYY/MM/DD'));
            });

            self.selectedTypeHeightExTable.subscribe((newValue) => {
                if (newValue == TypeHeightExTable.DEFAULT) { // 
                    self.isEnableInputHeight(false);
                    self.heightGridSetting('');
                    $('#input-heightExtable').ntsError('clear');
                } else if (newValue == TypeHeightExTable.SETTING) {
                    self.isEnableInputHeight(true);
                    setTimeout(() => {
                        $('#input-heightExtable').focus();
                    }, 1);
                }
            });
            
            self.disPeriodSelectionList = ko.observableArray([]);
            self.modeDisplayList  = ko.observableArray([]);
            
            self.selectedDisplayPeriod.subscribe(function(value) { // value = 1 || 2 || 3
                if (value == null || value == undefined)
                    return;
                if (value == DisplayPeriod.END ) { // 1日～末日で表示する  ：   A3_2_3選択時               
                    self.getDataInModeA3_2_3();
                } else if (value == DisplayPeriod.TH28) { // 28日周期で表示する  ：   A3_2_2選択時               
                    self.getDataInModeA3_2_2();
                } else if (value == DisplayPeriod.ANY_PERIOD) { // 抽出期間を表示する  ：   A3_2_1選択時           
                    self.getDataInModeA3_2_1();
                }
            });

            self.achievementDisplaySelected.subscribe(function(newValue) { // lấy data schedule | thực tế
                if(newValue == null || newValue == undefined || self.flag == true)
                    return;
                
                if (self.userInfor) {
                    self.userInfor.achievementDisplaySelected = (newValue == 1) ? true : false;
                    characteristics.save(self.KEY, self.userInfor);
                }
                
                nts.uk.ui.block.grayout();
                self.getNewData(self.selectedModeDisplayInBody()).done(() => {
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    nts.uk.ui.block.clear();
                });
            });
            
            self.selectedModeDisplayInBody.subscribe(function(viewMode) { // mode hiển thị workTime | Abname | Shift
                if (viewMode == null || self.flag == true)
                    return;
                nts.uk.ui.errors.clearAll();
                self.removeClass();
                nts.uk.ui.block.grayout();

                if (viewMode == ViewMode.SHIFT) {
                    let newLst = _.filter(self.useCategoriesWorkplace(), (item: any) => !_.includes(
                        [WorkplaceCounterCategory.LABOR_COSTS_AND_TIME, WorkplaceCounterCategory.EXTERNAL_BUDGET], item.value));
                    if (!_.isEmpty(newLst)) {
                        
                        self.callAlgSumA12 = false;
                        self.useCategoriesWorkplace(newLst);
                        self.showA12(true);
                        $('#horzDiv').css('display', '');
                    } else {
                        self.showA12(false);
                        $('#horzDiv').css('display', 'none');
                    }
                } else {
                    let addLst = _.filter(self.useCategoriesWorkplaceFull, (item: any) => _.includes([
                        WorkplaceCounterCategory.LABOR_COSTS_AND_TIME,
                        WorkplaceCounterCategory.EXTERNAL_BUDGET], item.value));
                    if (!_.isEmpty(addLst)) {
                        self.callAlgSumA12 = false;
                        self.useCategoriesWorkplace(_.sortBy(_.union(self.useCategoriesWorkplace(), addLst), ['value']));
                    }
                    if (_.isEmpty(self.useCategoriesWorkplace())) {
                        self.showA12(false);
                        $('#horzDiv').css('display', 'none');
                    } else {
                        self.showA12(true);
                        $('#horzDiv').css('display', '');
                    }
                }
                self.callAlgSumA12 = true;

                self.getNewData(viewMode).done(() => {
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    nts.uk.ui.block.clear();
                });
            });

            
            // update background trong mode shift giữa normal và shift
            self.backgroundColorSelected.subscribe((value) => {
                if (util.isNullOrUndefined(value) || util.isNullOrEmpty(value) || self.flag == true)
                    return;
                
                self.hasChangeModeBg = true;
                let shiftMasterWithWorkStyleLst;
                
                // update localstorage
                if (self.userInfor) {
                    self.userInfor.backgroundColor = value;
                    shiftMasterWithWorkStyleLst = self.userInfor.shiftMasterWithWorkStyleLst;
                    characteristics.save(self.KEY, self.userInfor);
                }
                
                // khi chuyển mode backGround thì gọi hàm updateGrid, sau khi gọi hàm này thì những cell đã edit trc đo không còn nữa, nên disable 2 button này đi.
                self.enableBtnUndo(false);
                self.enableBtnRedo(false);
                nts.uk.ui.block.grayout();
                
                let updatedCells = $("#extable").exTable('updatedCells');
                if(updatedCells.length == 0) {
                    self.updateBodyDetailGrid(ViewMode.SHIFT, self.mode());    
                } else {
                    // xử lý khi chuyển modebg, vì khi chuyển modebg sẽ update lai grid, những cell edit trước đó sẽ không còn nằm trong hàm get cellupdated,
                    // nên sẽ lưu lại những cell đã edit vào 1 biến global  
                    _.forEach(updatedCells, function(cell: any) {
                        let exit = _.filter(self.listCellUpdatedWhenChangeModeBg, function(o) { return o.rowIndex == cell.rowIndex && o.columnKey == cell.columnKey; });
                        if (exit.length > 0) {
                            _.remove(self.listCellUpdatedWhenChangeModeBg, function(e) {
                                return e.rowIndex == cell.rowIndex && e.columnKey == cell.columnKey;
                            });
                            self.listCellUpdatedWhenChangeModeBg.push(cell);
                        } else {
                            self.listCellUpdatedWhenChangeModeBg.push(cell);
                        }
                    });

                    // update datasource
                    self.updateDataSourceModeShiftWhenChangeBG(updatedCells);
                    self.updateDataBindGrid();
                    self.updateBodyDetailGrid(ViewMode.SHIFT, self.mode());
                }
                self.setIconEventHeader();
                self.setUpdateMode();
                nts.uk.ui.block.clear();
            });
         
            // ※19
            self.showComboboxA4_12 = ko.computed(function() {
                return self.selectedModeDisplayInBody() == ViewMode.SHIFT && self.mode() == UpdateMode.EDIT ;
            }, this);
            
            self.dataCell = {};

            self.bindingEventCellUpdatedGrid();
            
            self.pathToLeft  = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("152.png").serialize();
            self.pathToRight = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("153.png").serialize();
            self.pathToDown  = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("150.png").serialize();
            self.pathToUp    = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("151.png").serialize();
        }
        // end constructor
        
        startPage(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let viewMode   = _.isNil(self.userInfor) ? ViewMode.TIME : self.userInfor.disPlayFormat ;
            let updateMode = _.isNil(self.userInfor) ? UpdateMode.STICK: self.userInfor.updateMode;

            let param = {
                viewMode: viewMode,
                startDate: null,
                endDate: null,
                shiftPalletUnit: !_.isNil(self.userInfor) ? self.userInfor.shiftPalletUnit : 1, // 1: company , 2 : workPlace 
                pageNumberCom:   !_.isNil(self.userInfor) ? self.userInfor.shiftPalettePageNumberCom : 1,
                pageNumberOrg:   !_.isNil(self.userInfor) ? self.userInfor.shiftPalettePageNumberOrg : 1,
                getActualData: false,
                listShiftMasterNotNeedGetNew: !_.isNil(self.userInfor) ? self.userInfor.shiftMasterWithWorkStyleLst : [], // List of shifts không cần lấy mới
                listSid: self.listSid(),
                unit: !_.isNil(self.userInfor) ? self.userInfor.unit : 0,
                workplaceId: null,
                workplaceGroupId: null,
                personTotalSelected: _.isNil(self.userInfor) ? self.useCategoriesPersonalValue() : self.userInfor.useCategoriesPersonalValue, // A11_1
                workplaceSelected: _.isNil(self.userInfor) ? self.useCategoriesWorkplaceValue() : self.userInfor.useCategoriesWorkplaceValue// A12_1
            }

            service.getDataStartScreen(param).done((data: any) => {
                // khởi tạo data localStorage khi khởi động lần đầu.
				self.creatDataLocalStorege(data);

                self.displayButtonsHerder(data);
                
                self.checkSettingOpenKsu003(data);
                
                viewMode = self.selectedModeDisplayInBody();
                // trong trưởng hợp ở localstorage lưu viewMode = time, và updateMode = edit
                // Nhưng khi lấy setting từ server về lại chỉ có 2 viewMode là shortName và shift 
                // => phải set là updateMode , vì 2 mode shortName và shift không có updateMode = edit 
                if (viewMode != ViewMode.TIME && updateMode == UpdateMode.EDIT){
                    updateMode = UpdateMode.STICK;
                    self.userInfor.updateMode = UpdateMode.STICK;
                    characteristics.save(self.KEY, self.userInfor);
                 }
				
				self.useCategoriesPersonalFull = _.cloneDeep(data.dataBasicDto.useCategoriesPersonal);
				self.useCategoriesWorkplaceFull = _.cloneDeep(data.dataBasicDto.useCategoriesWorkplace);
				_.remove(data.dataBasicDto.useCategoriesPersonal, (item: any) => _.includes([
					PersonalCounterCategory.STANDARD_WORKING_HOURS_COMPARISON, 
					PersonalCounterCategory.NIGHT_SHIFT_HOURS, 
					PersonalCounterCategory.WEEKS_HOLIDAY_DAYS], item.value));
				if(!self.checkVisableByAuth(data.dataBasicDto.scheModifyAuthCtrlByWorkplace, 14)) {
					_.remove(data.dataBasicDto.useCategoriesPersonal, (item: any) => _.includes([
						PersonalCounterCategory.MONTHLY_EXPECTED_SALARY, 
						PersonalCounterCategory.CUMULATIVE_ESTIMATED_SALARY], item.value));
				}
                _.remove(data.dataBasicDto.useCategoriesWorkplace, (item: any) => item.value == WorkplaceCounterCategory.TIMEZONE_PEOPLE);
				if(viewMode == ViewMode.SHIFT) {
					_.remove(data.dataBasicDto.useCategoriesWorkplace, (item: any) => _.includes([
						WorkplaceCounterCategory.LABOR_COSTS_AND_TIME, 
						WorkplaceCounterCategory.EXTERNAL_BUDGET], item.value));
				}
                self.useCategoriesPersonal(data.dataBasicDto.useCategoriesPersonal);
                self.useCategoriesWorkplace(data.dataBasicDto.useCategoriesWorkplace);

                _.isEmpty(self.useCategoriesPersonal()) ? self.showA11(false) : self.showA11(true);
                _.isEmpty(self.useCategoriesWorkplace()) ? self.showA12(false) : self.showA12(true);
                
                if(self.userInfor && _.includes(_.map(self.useCategoriesPersonal(), o => o.value), self.userInfor.useCategoriesPersonalValue)) {
                    self.useCategoriesPersonalValue(self.userInfor.useCategoriesPersonalValue);     
                } else {
					if(!_.isEmpty(self.useCategoriesPersonal())) {
						self.useCategoriesPersonalValue(_.head(self.useCategoriesPersonal()).value);	
					}  
                }
                if(self.userInfor && _.includes(_.map(self.useCategoriesWorkplace(), o => o.value), self.userInfor.useCategoriesWorkplaceValue)) {
                    self.useCategoriesWorkplaceValue(self.userInfor.useCategoriesWorkplaceValue);       
                } else {
					if(!_.isEmpty(self.useCategoriesWorkplace())) {
						self.useCategoriesWorkplaceValue(_.head(self.useCategoriesWorkplace()).value);	
					}
                }
				self.userInfor.useCategoriesPersonalValue = self.useCategoriesPersonalValue();
				self.userInfor.useCategoriesWorkplaceValue = self.useCategoriesWorkplaceValue();
                characteristics.save(self.KEY, self.userInfor);
                self.useCategoriesPersonalValue.subscribe(value => {
                    $("#extable").exTable('saveScroll');
                    self.userInfor.useCategoriesPersonalValue = value;
                    characteristics.save(self.KEY, self.userInfor);
                    self.getAggregatedInfo(true, false);
                });
                
                self.useCategoriesWorkplaceValue.subscribe(value => {
                    $("#extable").exTable('saveScroll');
                    self.userInfor.useCategoriesWorkplaceValue = value;
                    characteristics.save(self.KEY, self.userInfor);
                    self.showA12_2(_.includes([WorkplaceCounterCategory.WORKTIME_PEOPLE, WorkplaceCounterCategory.LABOR_COSTS_AND_TIME], value) ||
                            (_.includes([WorkplaceCounterCategory.EXTERNAL_BUDGET], value) && self.funcNo15_WorkPlace));
                    if(self.callAlgSumA12){
                        self.getAggregatedInfo(false, true);
                    }
                });

                // ngày có thể chỉnh sửa schedule
                self.scheduleModifyStartDate = data.dataBasicDto.scheduleModifyStartDate;
                self.saveDataGrid(data);
                
                // ver5 : closeDate, startDateInit, endDateInit
                self.closeDate = data.dataBasicDto.closeDate;
                self.startDateInitStart = data.dataBasicDto.startDate;
                self.endDateInitStart = data.dataBasicDto.endDate;
                self.medicalOP = data.dataBasicDto.medicalOP;
                self.nursingCareOP = data.dataBasicDto.nursingCareOP;

                __viewContext.viewModel.viewAB.filter(data.dataBasicDto.unit == 0 ? true : false);
                __viewContext.viewModel.viewAB.workplaceIdKCP013(data.dataBasicDto.unit == 0 ? data.dataBasicDto.workplaceId : data.dataBasicDto.workplaceGroupId);
                
                self.getSettingDisplayWhenStart(viewMode, true);

                if (viewMode == ViewMode.SHIFT) {
                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                    self.bingdingToShiftPallet(data);
                }
                
                self.changeableWorks = _.isNil(data.dataBasicDto.scheFunctionControl) ? [] : data.dataBasicDto.scheFunctionControl.changeableWorks;
                
                // set data Header
                self.bindingToHeader(data);
                
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, viewMode);
                self.initExTable(dataBindGrid, viewMode, updateMode);
                
                $(".editMode").addClass("btnControlSelected").removeClass("btnControlUnSelected");
                $(".confirmMode").addClass("btnControlUnSelected").removeClass("btnControlSelected");
                self.setUpdateMode();
                self.setDataWorkType(data.listWorkTypeInfo);
                self.checkEnableCombWTime();
                self.bindingEventClickFlower();
                self.setTextResourceA173();
                if (viewMode == ViewMode.TIME) {
                    self.diseableCellsTime();
                }
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }
                
                self.flag = false;
                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }
        
        getNewData(viewMode): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            if (viewMode == ViewMode.SHIFT) { // mode シフト表示   
                self.shiftModeStart().done(() => {
                    dfd.resolve();
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    dfd.reject();
                });
            } else if (viewMode == ViewMode.SHORTNAME) { // mode 略名表示
                self.shortNameModeStart().done(() => {
                    dfd.resolve();
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    dfd.reject();
                });
            } else if (viewMode == ViewMode.TIME) {  // mode 勤務表示 
                self.timeModeStart().done(() => {
                    dfd.resolve();
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    dfd.reject();
                });
            }
            return dfd.promise();
        }
        
        creatDataLocalStorege(dataSetting) {
            let self = this;
            if (_.isNil(self.userInfor)) {
                let data : IUserInfor = {};
                data.disPlayFormat = dataSetting.dataBasicDto.viewModeSelected;
                data.backgroundColor = 0; // 0 : 通常; 1: シフト   // mau nền default của shiftMode
                data.achievementDisplaySelected = false;
                data.shiftPalletUnit = 1;
                data.shiftPalettePageNumberCom = 1;
                data.shiftPalletPositionNumberCom = { column : 0 , row : 0 };
                data.shiftPalettePageNumberOrg = 1;
                data.shiftPalletPositionNumberOrg = { column : 0 , row : 0 };
                data.gridHeightSelection = 1;
                data.heightGridSetting = '';
                data.unit = dataSetting.dataBasicDto.unit;
                data.workplaceId= dataSetting.dataBasicDto.workplaceId;
                data.workplaceGroupId = dataSetting.dataBasicDto.workplaceGroupId;
                data.workPlaceName = dataSetting.dataBasicDto.targetOrganizationName;
                data.code = dataSetting.dataBasicDto.code;
                data.workType = {};
                data.workTime = {};
                data.shiftMasterWithWorkStyleLst = [];
                self.userInfor = data;
                characteristics.save(self.KEY, self.userInfor);
            } else {
                self.userInfor.disPlayFormat = dataSetting.dataBasicDto.viewModeSelected;
                self.userInfor.achievementDisplaySelected = false;
                characteristics.save(self.KEY, self.userInfor);
            }
        }
        
        setUpdateMode() {
            let self = this;
            let updateMode = !_.isNil(self.userInfor) ? (self.userInfor.updateMode == undefined ? UpdateMode.STICK : self.userInfor.updateMode) : UpdateMode.STICK;

            if (updateMode == UpdateMode.STICK) {
                self.pasteData();
            } else if (updateMode == UpdateMode.COPY_PASTE) {
                self.coppyData();
            } else if (updateMode == UpdateMode.EDIT) {
                self.inputData();
            }
        }

        shiftModeStart(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let param = {
                viewMode : ViewMode.SHIFT,
                startDate: self.dateTimePrev() ,
                endDate  : self.dateTimeAfter(),
                shiftPalletUnit : self.userInfor.shiftPalletUnit, // 1: company , 2 : workPlace 
                pageNumberCom   : self.userInfor.shiftPalettePageNumberCom,
                pageNumberOrg   : self.userInfor.shiftPalettePageNumberOrg,
                getActualData   : self.userInfor.achievementDisplaySelected, 
                listShiftMasterNotNeedGetNew: self.userInfor.shiftMasterWithWorkStyleLst, // List of shifts không cần lấy mới
                listSid: self.listSid(),
                unit: self.userInfor.unit,
                workplaceId     : self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue(), // A12_1
                day: self.closeDate.day, 
                isLastDay: self.closeDate.lastDay
            };
            service.getDataOfShiftMode(param).done((data: IDataStartScreen) => {
                self.saveModeGridToLocalStorege(ViewMode.SHIFT);
                self.calculateDisPlayFormatA4Popup(data);
                self.visibleShiftPalette(true);
                self.visibleBtnInput(false);
                self.saveDataGrid(data);
                // set hiển thị ban đầu theo data đã lưu trong localStorege
                self.getSettingDisplayWhenStart(ViewMode.SHIFT, false);
                //WORKPLACE(0), //WORKPLACE_GROUP(1);
                __viewContext.viewModel.viewAC.workplaceModeName(data.dataBasicDto.designation);
                $('#Aa1_2 > label:nth-child(2) > span').html(data.dataBasicDto.designation);

                self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                // set data shiftPallet
                __viewContext.viewModel.viewAC.flag = false;
                
                __viewContext.viewModel.viewAC.selectedpalletUnit(self.userInfor.shiftPalletUnit);
                if(self.userInfor.shiftPalletUnit == 1){
                    __viewContext.viewModel.viewAC.handleInitCom(data.listPageInfo,data.targetShiftPalette.shiftPalletCom,self.userInfor.shiftPalettePageNumberCom);
                }else{
                    __viewContext.viewModel.viewAC.handleInitWkp(data.listPageInfo,data.targetShiftPalette.shiftPalletWorkPlace,self.userInfor.shiftPalettePageNumberOrg);
                }
                __viewContext.viewModel.viewAC.flag = true;

                if (__viewContext.viewModel.viewAC.listPageComIsEmpty == true) {
                    $('.ntsButtonTableButton').addClass('nowithContent');
                }
                if (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true) {
                    $('.ntsButtonTableButton').addClass('nowithContent');
                }

                if (self.mode() == UpdateMode.DETERMINE) {
                    self.shiftPalletControlDisable();
                }
                
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, ViewMode.SHIFT);
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, ViewMode.SHIFT);
                
                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);
                
                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }
                
                self.listCellUpdatedWhenChangeModeBg = [];
                self.hasChangeModeBg = false;
                
                // update A12
                if (data.aggrerateWorkplace) {
                    self.dataAggrerateWorkplace = data.aggrerateWorkplace;
                }

                self.createHorzSumData();
                self.updateHorzSumGrid();

                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }

        shortNameModeStart(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let setWorkTypeTime = self.userInfor.disPlayFormat == ViewMode.SHIFT ? true : false;
            let param = {
                viewMode: ViewMode.SHORTNAME,
                startDate: self.dateTimePrev(),
                endDate:   self.dateTimeAfter() ,
                getActualData: self.userInfor.achievementDisplaySelected,
                unit: self.userInfor.unit,
                workplaceId     : self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue(), // A12_1
                day: self.closeDate.day, 
                isLastDay: self.closeDate.lastDay,
            };
            
            service.getDataOfShortNameMode(param).done((data: IDataStartScreen) => {
                self.visibleShiftPalette(false);
                self.visibleBtnInput(false);
                self.saveModeGridToLocalStorege(ViewMode.SHORTNAME);
                self.calculateDisPlayFormatA4Popup(data);
                
                if (setWorkTypeTime) {
                    self.setWorkTypeTime(data.listWorkTypeInfo, self.userInfor);
                }
                
                self.saveDataGrid(data);
                // set hiển thị ban đầu theo data đã lưu trong localStorege
                self.getSettingDisplayWhenStart(ViewMode.SHORTNAME,false);
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, ViewMode.SHORTNAME);
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, ViewMode.SHORTNAME);
                
                // update A12
                if (data.aggrerateWorkplace) {
                    self.dataAggrerateWorkplace = data.aggrerateWorkplace;
                }
                self.createHorzSumData();
                self.updateHorzSumGrid();
                
                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);
                
                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }
                
                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }

        timeModeStart(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let setWorkTypeTime = self.userInfor.disPlayFormat == ViewMode.SHIFT ? true : false;
            let param = {
                viewMode: ViewMode.TIME,
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                getActualData: self.userInfor.achievementDisplaySelected,
                unit: self.userInfor.unit,
                workplaceId     : self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue(), // A12_1
                day: self.closeDate.day, 
                isLastDay: self.closeDate.lastDay,
            };

            service.getDataOfTimeMode(param).done((data: IDataStartScreen) => {
                self.visibleShiftPalette(false);
                self.visibleBtnInput(true);
                self.saveModeGridToLocalStorege(ViewMode.TIME);
                self.calculateDisPlayFormatA4Popup(data);
                if (setWorkTypeTime) {
                    self.setWorkTypeTime(data.listWorkTypeInfo, self.userInfor);
                }
                
                self.saveDataGrid(data);
                // set hiển thị ban đầu theo data đã lưu trong localStorege
                self.getSettingDisplayWhenStart(ViewMode.TIME, false);
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, ViewMode.TIME);
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, ViewMode.TIME);
                
                // update A12
                if (data.aggrerateWorkplace) {
                    self.dataAggrerateWorkplace = data.aggrerateWorkplace;
                }
                self.createHorzSumData();
                self.updateHorzSumGrid();

                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);
                
                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }

                dfd.resolve();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }

        setWorkTypeTime(listWorkTypeInfo, userInfor) {
            let self = this;
            if (self.mode() == UpdateMode.EDIT) {
                __viewContext.viewModel.viewAB.enableListWorkType(true);
            } else if (self.mode() == UpdateMode.DETERMINE){

            }
            self.setDataWorkType(listWorkTypeInfo);
        }
        
        checkEnableCombWTime() {
            let self = this;
            if (self.selectedModeDisplayInBody() == ViewMode.SHIFT)
                return;
            
            let workTypeCodeSave = !_.isNil(self.userInfor) ? self.userInfor.workTypeCodeSelected : '';
            if (workTypeCodeSave == '') {
                if (__viewContext.viewModel.viewAB.listWorkType()[0].workTimeSetting == 2) {
                    __viewContext.viewModel.viewAB.disabled(true);
                }
            } else {
                let objWtype = _.filter(__viewContext.viewModel.viewAB.listWorkType(), function(o) { return o.workTypeCode == workTypeCodeSave; });
                if (objWtype.length > 0 && objWtype[0].workTimeSetting == 2) {
                    __viewContext.viewModel.viewAB.disabled(true);
                }
            }
        }
        
        destroyAndCreateGrid(dataBindGrid, viewMode) {
            let self = this;
            $("#cacheDiv").append($('#vertDiv'));
            $("#cacheDiv").append($('#horzDiv'));
            $("#extable").children().remove();
            $("#extable").removeData();
            let extable = $("#extable")[0];
            $("#extable").replaceWith(extable.cloneNode(true));
            let updateMode = self.mode() === UpdateMode.EDIT ? UpdateMode.STICK : UpdateMode.DETERMINE;
            self.initExTable(dataBindGrid, viewMode, updateMode);
            if (!self.showA9) {
                if (!_.isNil(document.getElementById('A13'))) {
                    document.getElementById('A13').remove();
                }
            }
            self.bindingEventCellUpdatedGrid();
        }
        
        bindingEventCellUpdatedGrid() {
            let self = this;
            $("#extable").on("extablecellupdated", (dataCell) => {
                if (self.userInfor.disPlayFormat == ViewMode.TIME && self.userInfor.updateMode == UpdateMode.EDIT) {
                    // thằng này dùng cơ chể valid khác là => ajaxValidateTime
                } else if (self.userInfor.disPlayFormat == ViewMode.TIME && self.userInfor.updateMode == UpdateMode.STICK) {
                    // check xem cell vừa được stick data có nằm trong list cell lỗi do edit time hay không, nếu nằm trong list đấy thì rmove cell đó khỏi list lỗi đi.
                    self.validTimeStickMode(dataCell);
                }  else if (self.userInfor.disPlayFormat == ViewMode.TIME && self.userInfor.updateMode == UpdateMode.COPY_PASTE) {
                    // check xem cell vừa được stick data có nằm trong list cell lỗi do edit time hay không, nếu nằm trong list đấy thì rmove cell đó khỏi list lỗi đi.
                    self.validTimeCopyPaste(dataCell);
                } else {
                    self.checkExitCellUpdated();
                }
            });

            $("#extable").on("extablecellretained", (dataCell) => {});

            $("#extable").on("extablerowupdated", (dataCell) => {
                self.checkExitCellUpdated();
            });

            $("#extable").on("extablecelldetermined", (arg) => {
                setTimeout(() => {
                    if (self.getCellsConfirmReg().length > 0) {
                        self.enableBtnReg(true);
                    } else {
                        self.enableBtnReg(false);
                    }
                }, 100);
                //_.forEach(arg.detail, function(cell) {});
            });
        }

        validTimeStickMode(dataCellUpdated: any) {
            let self = this;
            let rowIndex = dataCellUpdated.originalEvent.detail.rowIndex;
            let columnKey = dataCellUpdated.originalEvent.detail.columnKey;
            // check cell stick có đang lỗi hay khong.
            // grid chưa support remove lỗi ở cell đang có lỗi, nên những cell đang bị lỗi đỏ bị stick vẫn còn đỏ.
            self.checkExitCellUpdated();
        }

        validTimeCopyPaste(dataCellUpdated: any) {
            let self = this;
            let rowIndex = dataCellUpdated.originalEvent.detail.rowIndex;
            let columnKey = dataCellUpdated.originalEvent.detail.columnKey;
            // copy paste hiện tại đang không lấy được cell nguồn
            // nên là đang khong biết có coppy từ cell bị lỗi hay không.
            // grid chưa support add lỗi vào cell, nên khi copy từ cell đang bị lỗi đỏ, sẽ không set cell đích thành cell lỗi được. 
            // nếu bên nhật log bug thì nhờ Mạnh public thêm hàm set error cho cell.
            self.checkExitCellUpdated();
        }

        saveDataGrid(data: any) {
            let self = this;
            self.listEmpInfo = data.listEmpInfo;
            self.listWorkScheduleWorkInfor = data.listWorkScheduleWorkInfor;
            self.listWorkScheduleShift = data.listWorkScheduleShift;
            self.listPersonalConditions = data.listPersonalConditions;
            self.displayControlPersonalCond = data.displayControlPersonalCond;
            self.listDateInfo = data.listDateInfo;
            
            self.cloneDataSource();
        }

        // binding ket qua cua <<ScreenQuery>> 初期起動の情報取得 
        bindingToHeader(data: IDataStartScreen) {
            let self = this;
            let dataBasic: IDataBasicDto = data.dataBasicDto;
            self.dtPrev(dataBasic.startDate);
            self.dtAft(dataBasic.endDate);
            self.targetOrganizationName(dataBasic.targetOrganizationName);
            
            // save data to local Storage
            self.userInfor.unit = dataBasic.unit;
            self.userInfor.workplaceId = dataBasic.workplaceId;
            self.userInfor.workplaceGroupId = dataBasic.workplaceGroupId;
            self.userInfor.workPlaceName = dataBasic.targetOrganizationName;
            characteristics.save(self.KEY, self.userInfor);
        }
        
        saveShiftMasterToLocalStorage(shiftMasterWithWorkStyleLst: Array<IShiftMasterMapWithWorkStyle>) {
            let self = this;
            // save data to local Storage
            self.userInfor.shiftMasterWithWorkStyleLst = shiftMasterWithWorkStyleLst;
            characteristics.save(self.KEY, self.userInfor);
        }
        
        bingdingToShiftPallet(data: any) {
            let self = this;
            // set data shiftPallet
            __viewContext.viewModel.viewAC.flag = false;
            __viewContext.viewModel.viewAC.workplaceModeName(data.dataBasicDto.designation);
            __viewContext.viewModel.viewAC.palletUnit([
                { code: 1, name: getText("Com_Company") },
                { code: 2, name: data.dataBasicDto.designation }
            ]);
            
            __viewContext.viewModel.viewAC.selectedpalletUnit(self.userInfor.shiftPalletUnit);
            if (self.userInfor.shiftPalletUnit == 1) {
                __viewContext.viewModel.viewAC.handleInitCom(
                    data.listPageInfo,
                    data.targetShiftPalette.shiftPalletCom,
                    self.userInfor.shiftPalettePageNumberCom);
            } else {
                __viewContext.viewModel.viewAC.handleInitWkp(
                    data.listPageInfo,
                    data.targetShiftPalette.shiftPalletWorkPlace,
                    self.userInfor.shiftPalettePageNumberOrg);
            }
            __viewContext.viewModel.viewAC.flag = true;
        }
        
        indexOfPageSelected(listPageInfo : any, shiftPalettePageNumber : any) {
            let index = _.findIndex(listPageInfo, function(o) { return o.pageNumber == shiftPalettePageNumber; });
            return index != -1 ? index : 0;
        }
        
        private setDataWorkType(listWorkTypeInfo: any) {
            let self = this;
            // set data cho combobox WorkType
            let listWorkType = [];
            _.each(listWorkTypeInfo, (emp: any, i) => {
                let workTypeDto: IWorkTypeDto = {
                    workTypeCode: emp.workTypeDto.workTypeCode, // 勤務種類コード - コード
                    name: emp.workTypeDto.name,         // 勤務種類名称  - 表示名
                    memo: emp.workTypeDto.memo,
                    workTimeSetting: emp.workTimeSetting, // 必須任意不要区分 :  必須である REQUIRED(0), 任意であるOPTIONAL(1), 不要であるNOT_REQUIRED(2)
                    workStyle: emp.workStyle,
                    abbName: emp.workTypeDto.abbName,
                }
                listWorkType.push(workTypeDto);
            });
            __viewContext.viewModel.viewAB.listWorkType(listWorkType);
        }
        
        // convert data lấy từ server để đẩy vào Grid
        private convertDataToGrid(data: any, viewMode: string) {
            let self = this;
            let result = {};
            let leftmostDs        = [];
            let middleDs          = [];
            let detailColumns     = [];
            let objDetailHeaderDs = {};
            let detailHeaderDeco: any = [];
            let detailContentDs   = [];
            let detailContentDeco = [];
            let detailContentDecoModeConfirm = [];
            let horizontalDetailColumns: any = [];
            let htmlToolTip       = [];
            
            // Deco ở mode shift
            let detailContentDecoNormal = [];  // deco ở mode edit + background normal
            let detailContentDecoShift = [];   // deco ở mode edit + background shift
            let detailContentDecoModeConfirmNormal = []; // deco ở mode confirm + background normal
            let detailContentDecoModeConfirmShift = [];  // deco ở mode confirm + background normal
            
            let scheduleModifyStartDate = self.scheduleModifyStartDate;
            self.listEmpData = [];
            self.listSid([]);
            self.listLockCells = [];
            
            self.detailContentDeco = [];
            self.detailContentDecoModeConfirm = [];
            self.detailContentDecoNormal = [];  // deco ở mode edit + background normal
            self.detailContentDecoShift = [];   // deco ở mode edit + background shift
            self.detailContentDecoModeConfirmNormal = []; // deco ở mode confirm + background normal
            self.detailContentDecoModeConfirmShift = [];  // deco ở mode confirm + background normal
            
            self.tooltipShare = data.listDateInfo;
            self.listWorkTypeInfo = data.listWorkTypeInfo;
            
            self.listTimeDisable = [];

            for (let i = 0; i < data.listEmpInfo.length; i++) {
                let rowId = i+'';
                let emp: IEmpInfo = data.listEmpInfo[i];
                let objDetailContentDs = new Object();
                // set data to detailLeftmost
                let businessName = emp.businessName == null || emp.businessName == undefined ? '' : emp.businessName.trim();
                leftmostDs.push({ sid: i.toString() ,employeeId: emp.employeeId, codeNameOfEmp: emp.employeeCode + ' ' + businessName });
                
                self.listSid.push(emp.employeeId);
                self.listEmpData.push({ id: emp.employeeId, code: emp.employeeCode, name : businessName });
                let listWorkScheduleInforByEmp: Array<IWorkScheduleWorkInforDto> = _.filter(data.listWorkScheduleWorkInfor, function(workSchedul: IWorkScheduleWorkInforDto) { return workSchedul.employeeId === emp.employeeId });
                let listWorkScheduleShiftByEmp: Array<IWorkScheduleShiftInforDto> = _.filter(data.listWorkScheduleShift, function(workSchedul: IWorkScheduleShiftInforDto) { return workSchedul.employeeId === emp.employeeId });
                // set data middle
                let personalCond: IPersonalConditions = _.filter(data.listPersonalConditions, function(o) { return o.sid == emp.employeeId; });
                if(personalCond.length > 0){
                   middleDs.push({ 
                    sid: i.toString(), 
                    employeeId: emp.employeeId, 
                    team: _.isNil(personalCond[0].teamName) ? '' : personalCond[0].teamName , 
                    rank: _.isNil(personalCond[0].rankName) ? '' : personalCond[0].rankName, 
                    qualification: _.isNil(personalCond[0].licenseClassification) ? '' : personalCond[0].licenseClassification });
                }
                
                // set data to detailContent : datasource va deco
                if (viewMode == ViewMode.SHIFT) {
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleShiftByEmpSort = _.orderBy(listWorkScheduleShiftByEmp, ['date'],['asc']);
                    let userInfor: IUserInfor = self.userInfor;
                    let shiftMasterWithWorkStyleLst = userInfor.shiftMasterWithWorkStyleLst;
                    
                    for (let j = 0; j < listWorkScheduleShiftByEmpSort.length; j++) {
                        let cell: IWorkScheduleShiftInforDto = listWorkScheduleShiftByEmpSort[j];
                        let time = new Time(new Date(cell.date));
                        let date = moment(cell.date, 'YYYY/MM/DD');
                        cell.condTargetdate = true;
                        
                        // check ngày có thể chỉnh sửa 日 < A画面パラメータ.修正可能開始日 の場合
                        if(moment(cell.date, 'YYYY/MM/DD') < moment(scheduleModifyStartDate, 'YYYY/MM/DD')){
                            cell.conditionAa1 = false;
                            cell.conditionAa2 = false;
                            cell.condTargetdate = false;
                        }
                        
                        let ymd = time.yearMonthDay;
                        let shiftName = null;
                        shiftName = (cell.haveData == true && (cell.shiftName == null || cell.shiftName == '')) ? getText("KSU001_94") : cell.shiftName;
                        if (cell.needToWork == false)
                            shiftName = null;
                        objDetailContentDs['_' + ymd] = new ExCell(null, null, null, null, null, null, shiftName, cell.shiftCode, cell.confirmed , cell.achievements, cell.workHolidayCls, cell.needToWork, cell.supportCategory, cell.condTargetdate);

                        // set Deco background
                        // điều kiện ※Aa1 editMode - background Normal                                              
                        if (cell.conditionAa1 == false) {
                            detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                        } else if (cell.supportCategory != SupportCategory.NotCheering) {
                            detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "bg-schedule-support", 0));
                        } else {
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                            }
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                            }
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                //REFLECT_APPLICATION(2), 申請反映
                                detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                            }
                        }
                        
                        
                        // điều kiện ※Aa1 editMode - background Shift                                             
                        if (cell.conditionAa1 == false) {
                            detailContentDecoShift.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                        } else {
                            // A10_color② シフト表示：シフトの背景色  (Hiển thị Shift: màu nền của shift) 
                            if (cell.shiftCode != null) {
                                let objShiftMasterWithWorkStyle = _.filter(shiftMasterWithWorkStyleLst, function(o) { return o.shiftMasterCode == cell.shiftCode; });
                                if (objShiftMasterWithWorkStyle.length > 0) {
                                    let color = '#' + objShiftMasterWithWorkStyle[0].color;
                                    detailContentDecoShift.push(new CellColor('_' + ymd, rowId, color, 0));
                                } else {
                                    detailContentDecoShift.push(new CellColor('_' + ymd, rowId, "background-white", 0));
                                }
                            } else {
                                detailContentDecoShift.push(new CellColor('_' + ymd, rowId, "background-white", 0));
                            }
                        }

                        // điều kiện ※Aa2 confirmMode - background Normal                                              
                        if (cell.conditionAa2 == false) {
                            detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                        } else if (cell.supportCategory != SupportCategory.NotCheering) {
                            detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "bg-schedule-support", 0));
                        } else if (cell.confirmed == true) {
                            self.listLockCells.push({ rowIndex: rowId, columnKey: '_' + ymd, confirm: true });
                        } else {
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                            }
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                            }
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                //REFLECT_APPLICATION(2), 申請反映
                                detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                            }
                        }
                        
                        // điều kiện ※Aa2 confirmMode - background Shift                                             
                        if (cell.conditionAa2 == false) {
                            detailContentDecoModeConfirmShift.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                        } else if (cell.confirmed == true) {
                            self.listLockCells.push({ rowIndex: rowId, columnKey: '_' + ymd, confirm: true });
                        } else {
                            // A10_color② シフト表示：シフトの背景色  (Hiển thị Shift: màu nền của shift) 
                            if (cell.shiftCode != null) {
                                let objShiftMasterWithWorkStyle = _.filter(shiftMasterWithWorkStyleLst, function(o) { return o.shiftMasterCode == cell.shiftCode; });
                                if (objShiftMasterWithWorkStyle.length > 0) {
                                    let color = '#' + objShiftMasterWithWorkStyle[0].color;
                                    detailContentDecoModeConfirmShift.push(new CellColor('_' + ymd, rowId, color, 0));
                                } else {
                                    detailContentDecoModeConfirmShift.push(new CellColor('_' + ymd, rowId, "background-white", 0));
                                }
                            } else {
                                detailContentDecoModeConfirmShift.push(new CellColor('_' + ymd, rowId, "background-white", 0));
                            }
                        }
                        
                        // set Deco text color
                        // A10_color⑥ スケジュール明細の文字色  (Màu chữ của "Schedule detail")                                                         
                        if (cell.achievements == true) {
                            if (cell.shiftCode == '' || cell.shiftCode == null) {
                                // デフォルト（黒）  Default (black) 
                                detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoShift.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoModeConfirmShift.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                            } else {
                                let workHolidayCls = cell.workHolidayCls == null ? self.getWorkStyle(shiftMasterWithWorkStyleLst, cell.shiftCode) : cell.workHolidayCls;
                                let cellColor = self.setColorCell(workHolidayCls, ymd, rowId);

                                detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                                detailContentDecoShift.push(cellColor);
                                detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                                detailContentDecoModeConfirmShift.push(cellColor);
                            }
                        } else {
                            if (cell.shiftCode == '' || cell.shiftCode == null) {
                                // デフォルト（黒）  Default (black) 
                                detailContentDecoNormal.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoShift.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoModeConfirmNormal.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoModeConfirmShift.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                            } else {
                                let workHolidayCls = cell.workHolidayCls == null ? self.getWorkStyle(shiftMasterWithWorkStyleLst, cell.shiftCode) : cell.workHolidayCls;
                                let cellColor = self.setColorCell(workHolidayCls, ymd, rowId);

                                detailContentDecoNormal.push(cellColor);
                                detailContentDecoShift.push(cellColor);
                                detailContentDecoModeConfirmNormal.push(cellColor);
                                detailContentDecoModeConfirmShift.push(cellColor);
                            }
                        }
       
                    };
                    detailContentDs.push(objDetailContentDs);
                    
                } else if (viewMode == ViewMode.SHORTNAME) {
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleInforByEmpSort = _.orderBy(listWorkScheduleInforByEmp, ['date'],['asc']);
                    _.each(listWorkScheduleInforByEmpSort, (cell: IWorkScheduleWorkInforDto) => {
                        
                        cell.condTargetdate = true;
                        // check ngày có thể chỉnh sửa 日 < A画面パラメータ.修正可能開始日 の場合
                        if (moment(cell.date, 'YYYY/MM/DD') < moment(scheduleModifyStartDate, 'YYYY/MM/DD')) {
                            cell.conditionAbc1 = false;
                            cell.conditionAbc2 = false;
                            cell.condTargetdate = false;
                        }
                        let time = new Time(new Date(cell.date));
                        let ymd = time.yearMonthDay;
                        let workTypeName = ((cell.workTypeCode != null && (cell.workTypeName == '' || _.isNil(cell.workTypeName))) || cell.workTypeIsNotExit == true ) ? (cell.workTypeCode == null ? null : cell.workTypeCode) + getText("KSU001_22") : cell.workTypeName;
                        let workTimeName = ((cell.workTimeCode != null && (cell.workTimeName == '' || _.isNil(cell.workTimeName))) || cell.workTimeIsNotExit == true ) ? (cell.workTimeCode == null ? null : cell.workTimeCode) + getText("KSU001_22") : cell.workTimeName;
                        if (cell.needToWork == false) {
                            workTypeName = null;
                            workTimeName = null;
                        }
                        objDetailContentDs['_' + ymd] = new ExCell(cell.workTypeCode, workTypeName, cell.workTimeCode, workTimeName, null, null, null, null,cell.confirmed , cell.achievements, cell.workHolidayCls, cell.needToWork, cell.supportCategory, cell.condTargetdate);

                        // set Deco background
                        // A10_color⑤ 勤務略名表示の背景色 (Màu nền hiển thị "chuyên cần, tên viết tắt")
                        // điều kiện ※Abc1 editMode                                                
                        if (cell.conditionAbc1 == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                        } else {
                            if (cell.workTypeEditStatus != null) {
                                if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }

                            if (cell.workTimeEditStatus != null) {
                                if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 1));
                                }
                            }
                        }

                        // set Deco text color
                        // A10_color⑥ スケジュール明細の文字色  (Màu chữ của "Schedule detail")
                        if (cell.achievements == true) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 1));
                        } else {
                            if (cell.workHolidayCls == WorkStyle.ONE_DAY_WORK) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                            }
                            if (cell.workHolidayCls == WorkStyle.MORNING_WORK) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == WorkStyle.AFTERNOON_WORK) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == WorkStyle.ONE_DAY_REST) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                            }
                        }
                        
                        // điều kiện ※Abc2 confirmMode
                        if (cell.conditionAbc2 == false) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                        } else if (cell.confirmed == true) {
                            self.listLockCells.push({ rowIndex: rowId, columnKey: '_' + ymd, confirm : true });
                        }  else {
                            if (cell.workTypeEditStatus != null) {
                                if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }

                            if (cell.workTimeEditStatus != null) {
                                if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 1));
                                }
                            }
                        }
                    });
                    detailContentDs.push(objDetailContentDs);

                } else if (viewMode == ViewMode.TIME) {
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleInforByEmpSort = _.orderBy(listWorkScheduleInforByEmp, ['date'],['asc']);
                    _.each(listWorkScheduleInforByEmpSort, (cell: IWorkScheduleWorkInforDto) => {
                        // set dataSource
                        cell.condTargetdate = true;
                        // check ngày có thể chỉnh sửa 日 < A画面パラメータ.修正可能開始日 の場合
                        if (moment(cell.date, 'YYYY/MM/DD') < moment(scheduleModifyStartDate, 'YYYY/MM/DD')) {
                            cell.conditionAbc1 = false;
                            cell.conditionAbc2 = false;
                            cell.condTargetdate = false;
                        }
                        
                        let time = new Time(new Date(cell.date));
                        let ymd = time.yearMonthDay;
                        let workTypeName = ((cell.workTypeCode != null && (cell.workTypeName == '' || _.isNil(cell.workTypeName))) || cell.workTypeIsNotExit == true ) ? (cell.workTypeCode == null ? null : cell.workTypeCode) + getText("KSU001_22") : cell.workTypeName;
                        let workTimeName = ((cell.workTimeCode != null && (cell.workTimeName == '' || _.isNil(cell.workTimeName))) || cell.workTimeIsNotExit == true ) ? (cell.workTimeCode == null ? null : cell.workTimeCode) + getText("KSU001_22") : cell.workTimeName;
                        let startTime    = cell.startTime == null ? null : formatById("Clock_Short_HM", cell.startTime);
                        let endTime      = cell.endTime   == null ? null : formatById("Clock_Short_HM", cell.endTime);
                        let workTypeCode = cell.workTypeCode;
                        let workTimeCode = cell.workTimeCode;
                        if (cell.needToWork == false) {
                            workTypeName = null;
                            workTimeName = null;
                            startTime    = null;
                            endTime      = null;
                        }
                        
                        if(cell.startTime == 0 && cell.endTime == 0){
                            startTime    = null;
                            endTime      = null;
                        }
                        
                        objDetailContentDs['_' + ymd] = new ExCell(workTypeCode, workTypeName, workTimeCode, workTimeName, startTime, endTime, null, null, cell.confirmed , cell.achievements, cell.workHolidayCls, cell.needToWork, cell.supportCategory, cell.condTargetdate);
                        // set Deco background
                        // A10_color⑤ 勤務略名表示の背景色 (Màu nền hiển thị "chuyên cần, tên viết tắt")
                        // điều kiện ※Abc1 editMode
                        if (cell.conditionAbc1 == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 2));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 3));
                        } else {
                            if (cell.workTypeEditStatus != null) {
                                if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }
                            
                            if (cell.workTimeEditStatus != null) {
                                if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 1));
                                }
                            }
                            
                            if (cell.startTimeEditState != null) {
                                if (cell.startTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 2));
                                } else if (cell.startTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 2));
                                } else if (cell.startTimeEditState.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 2));
                                }
                            }
                            
                            if (cell.endTimeEditState != null) {
                                if (cell.endTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 3));
                                } else if (cell.endTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 3));
                                } else if (cell.endTimeEditState.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 3));
                                }
                            }
                        }
                        
                        // set Deco text color
                        // A10_color⑥ スケジュール明細の文字色  (Màu chữ của "Schedule detail")
                        if (cell.achievements == true) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 1));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 2));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 3));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 2));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 3));
                        } else {
                            if (cell.workHolidayCls == WorkStyle.ONE_DAY_WORK) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                            }
                            if (cell.workHolidayCls == WorkStyle.MORNING_WORK) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == WorkStyle.AFTERNOON_WORK) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == WorkStyle.ONE_DAY_REST) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                            }
                        }
                        
                        // điều kiện ※Abc2 confirmMode
                        if (cell.conditionAbc2 == false) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 2));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 3));
                        } else if (cell.confirmed == true) {
                            self.listLockCells.push({ rowIndex: rowId, columnKey: '_' + ymd, confirm : true });
                        }  else {
                            if (cell.workTypeEditStatus != null) {
                                if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }

                            if (cell.workTimeEditStatus != null) {
                                if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 1));
                                }
                            }

                            if (cell.startTimeEditState != null) {
                                if (cell.startTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 2));
                                } else if (cell.startTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 2));
                                } else if (cell.startTimeEditState.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 2));
                                }
                            }

                            if (cell.endTimeEditState != null) {
                                if (cell.endTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_MYSELF) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 3));
                                } else if (cell.endTimeEditState.editStateSetting === EditStateSetting.HAND_CORRECTION_OTHER) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 3));
                                } else if (cell.endTimeEditState.editStateSetting === EditStateSetting.REFLECT_APPLICATION) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 3));
                                }
                            }    
                        }
                        
                        // dieu kien ※Ac
                        let conditionAc = cell.conditionAbc1 == true
                                          && ((!_.isNil(cell.workTimeCode) && cell.workTimeCode != '') || (cell.workHolidayCls != WorkStyle.ONE_DAY_REST))
                                          && (_.includes(self.changeableWorks, cell.workTimeForm));
                        if (!conditionAc) {
                            self.listTimeDisable.push(new TimeDisable(rowId, '_' + ymd));
                        }
                    });
                    detailContentDs.push(objDetailContentDs);
                }
            }
            
            // truyen sids vao kcp015
            self.sids(self.listSid());
            
            // set width cho column cho tung mode
            let widthColumn = 0;
            if (viewMode == ViewMode.TIME) {
                widthColumn = 160;
            } else if (viewMode == ViewMode.SHORTNAME) {
                widthColumn = 80;
            } else if (viewMode == ViewMode.SHIFT) {
                widthColumn = 35;
            }

            // イベント情報と個人条件のmapping (mapping "thông tin event" và "person condition")
            if (data.displayControlPersonalCond == null) {
                self.showA9 = false;
            } else {
                self.showA9 = true;
                let listConditionDisplayControl = data.displayControlPersonalCond.listConditionDisplayControl;
                let team = _.filter(listConditionDisplayControl, function(o) { return o.conditionATR == 1; });
                let rank = _.filter(listConditionDisplayControl, function(o) { return o.conditionATR == 2; });
                let qual = _.filter(listConditionDisplayControl, function(o) { return o.conditionATR == 4; });
                if (team.length > 0) {
                    if (team[0].displayCategory == 1) {
                        self.showTeamCol = true;
                    }
                }
                if (rank.length > 0) {
                    if (rank[0].displayCategory == 1) {
                        self.showRankCol = true;
                    }
                }
                if (qual.length > 0) {
                    if (qual[0].displayCategory == 1) {
                        self.showQualificCol = true;
                    }
                }
                if (self.showTeamCol == false && self.showRankCol == false && self.showQualificCol == false) {
                    self.showA9 = false;
                }
            }

            if (!self.showA9) {
                if (!_.isNil(document.getElementById('A13'))) {
                    document.getElementById('A13').remove();
                }
            }

            let ajaxValidateTime = {
                request: (rowIdx, columnKey, innerIdx, value) => {
                    let dfd = $.Deferred();
                    let time = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                    if (value === '' || _.isNaN(time)) {
                        return dfd.reject();
                    }

                    let startTime, endTime, workTypeCode, workTimeCode;
                    let dataSource = $("#extable").exTable('dataSource', 'detail').body;
                    let cellData = dataSource[rowIdx][columnKey];
                    workTypeCode = cellData.workTypeCode;
                    workTimeCode = cellData.workTimeCode;
                    if (innerIdx == 3) {
                        startTime = nts.uk.time.minutesBased.duration.parseString(cellData.startTime).toValue();
                        endTime = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                        if (endTime == '' || _.isNaN(endTime)) return dfd.reject();

                    } else if (innerIdx == 2) {
                        startTime = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                        endTime = nts.uk.time.minutesBased.duration.parseString(cellData.endTime).toValue();
                        if (startTime == '' || _.isNaN(startTime)) return dfd.reject();

                    }

                    if (startTime < 0 && endTime < 0) {
                        startTime = startTime * -1;
                        endTime = endTime * -1;
                    }

                    if (startTime >= endTime) {
                        nts.uk.ui.dialog.alertError({ messageId: 'Msg_54' });
                        return dfd.reject();
                    }
                    
                    // check NaN
                    let hasClassError = _.includes("x-error", $("#extable").exTable('cellByIndex', rowIdx, columnKey).children[2].classList);
                    if (innerIdx == InnerIndex.STARTTIME && _.isNaN(endTime) && hasClassError) {
                        return dfd.reject();
                    } else if (innerIdx == InnerIndex.STARTTIME && _.isNaN(endTime) && !hasClassError) {
                        return dfd.resolve();
                    } else if (innerIdx == InnerIndex.ENDTIME && _.isNaN(startTime) && hasClassError) {
                        return dfd.reject();
                    } else if (innerIdx == InnerIndex.ENDTIME && _.isNaN(startTime) && !hasClassError) {
                        return dfd.resolve();
                    }
                    
                    
                    let param = {
                        workType: workTypeCode,
                        workTime: workTimeCode,
                        workTime1: {
                            startTime: {
                                time: startTime,
                                dayDivision: 0
                            },
                            endTime: {
                                time: endTime,
                                dayDivision: 0
                            }
                        },
                        workTime2: null
                    }

                    // call alg : <<Query>> 時刻が不正かチェックする
                    nts.uk.ui.block.invisible();
                    service.checkTimeIsIncorrect(param).done((result) => {
                        let errors = [];
                        for (let i = 0; i < result.length; i++) {
                            if (!result[i].check) {
                                if (result[i].timeSpan == null) {
                                    errors.push({
                                        message: nts.uk.resource.getMessage('Msg_439', getText('KDL045_12')),
                                        messageId: "Msg_439",
                                        supplements: {},
                                        innerIdx: result[i].innerIdx
                                    });
                                } else {
                                    if (result[i].timeSpan.startTime == result[i].timeSpan.endTime) {
                                        errors.push({
                                            message: nts.uk.resource.getMessage('Msg_2058', [result[i].nameError, formatById("Clock_Short_HM", result[i].timeSpan.startTime)]),
                                            messageId: "Msg_2058",
                                            supplements: {},
                                            innerIdx: result[i].innerIdx
                                        });
                                    } else {
                                        errors.push({
                                            message: nts.uk.resource.getMessage('Msg_1772', [result[i].nameError, formatById("Clock_Short_HM", result[i].timeSpan.startTime), formatById("Clock_Short_HM", result[i].timeSpan.endTime)]),
                                            messageId: "Msg_1772",
                                            supplements: {},
                                            innerIdx: result[i].innerIdx
                                        });
                                    }
                                }
                            }
                        }

                        if (errors.length > 0) {
                            self.enableBtnReg(false);
                            let errorsInfo = _.uniqBy(errors, x => { return x.message });
                            bundledErrors({ errors: errorsInfo }).then(() => {
                                nts.uk.ui.block.clear();
                                self.checkExitCellUpdated();
                            });
                            if (errors.length == 2) {
                                dfd.reject();
                            } else if (errors.length == 1 && errors[0].innerIdx == InnerIndex.STARTTIME && innerIdx == InnerIndex.ENDTIME) {
                                dfd.resolve();
                            } else if (errors.length == 1 && errors[0].innerIdx == InnerIndex.ENDTIME && innerIdx == InnerIndex.STARTTIME) {
                                dfd.resolve();
                            } else {
                                dfd.reject();
                            }
                        } else {
                            self.checkExitCellUpdated();
                            nts.uk.ui.block.clear();
                            dfd.resolve();
                        }
                    }).fail(function(error) {
                        nts.uk.ui.block.clear();
                        nts.uk.ui.dialog.alertError(error);
                        return dfd.reject();
                    });
                    return dfd.promise();
                }, onValid: (a, b) => {
                    //alert(b);
                }, onFailed: (a, b) => {
                    //alert(b);
                }
            };

            detailColumns.push({ key: "sid", width: "5px", headerText: "ABC", visible: false });
            horizontalDetailColumns.push({ key: "sid", width: "5px", headerText: "ABC", visible: false });
            objDetailHeaderDs['sid'] = "";
            self.arrDay = [];
            _.each(data.listDateInfo, (dateInfo: IDateInfo) => {
                self.arrDay.push(new Time(new Date(dateInfo.ymd)));
                let time = new Time(new Date(dateInfo.ymd));
                detailColumns.push({
                    key: "_" + time.yearMonthDay, width: widthColumn + "px", handlerType: "input", dataType: "label/label/time/time", primitiveValue: "TimeWithDayAttr", headerControl: "link", ajaxValidate: ajaxValidateTime
                });
                horizontalDetailColumns.push({
                    key: "_" + time.yearMonthDay, width: widthColumn + "px", handlerType: "input", dataType: "label/label/duration/duration", primitiveValue: "TimeWithDayAttr"
                });
                let ymd = time.yearMonthDay;
                let field = '_' + ymd;
                if (dateInfo.isToday) {
                    
                    if (dateInfo.isHoliday) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-sunday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-sunday"));
                    } else if (dateInfo.dayOfWeek == 7) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-sunday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-sunday"));
                    } else if (dateInfo.dayOfWeek == 6) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-saturday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-saturday"));
                    } else if (dateInfo.dayOfWeek > 0 || dateInfo.dayOfWeek < 6) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-weekdays"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-weekdays"));
                    } else {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day"));
                    }
                    
                } else if (dateInfo.isSpecificDay) {
                    
                    if (dateInfo.isHoliday) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-sunday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-sunday"));
                    } else if (dateInfo.dayOfWeek == 7) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-sunday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-sunday"));
                    } else if (dateInfo.dayOfWeek == 6) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-saturday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-saturday"));
                    } else if (dateInfo.dayOfWeek > 0 || dateInfo.dayOfWeek < 6) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-weekdays"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-weekdays"));
                    } else {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date"));
                    }
                    
                    
                } else if (dateInfo.isHoliday) {
                    detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-sunday"));
                    detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-sunday"));
                } else if (dateInfo.dayOfWeek == 7) {
                    detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-sunday"));
                    detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-sunday"));
                } else if (dateInfo.dayOfWeek == 6) {
                    detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-saturday"));
                    detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-saturday"));
                } else if (dateInfo.dayOfWeek > 0 || dateInfo.dayOfWeek < 6) {
                    detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-weekdays"));
                    detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-weekdays"));
                }
                
                if (dateInfo.htmlTooltip != null) {
                    objDetailHeaderDs['_' + ymd] = "<img class='header-image-event'>";
                    htmlToolTip.push(new HtmlToolTip('_' + ymd, dateInfo.htmlTooltip));
                } else {
                    objDetailHeaderDs['_' + ymd] = "<img class='header-image-no-event'>";
                }
            });
            
            self.setIconEventHeader();
            
            result = {
                leftmostDs: leftmostDs,
                middleDs: middleDs,
                detailColumns: detailColumns,
                objDetailHeaderDs: objDetailHeaderDs,
                detailHeaderDeco: detailHeaderDeco,
                htmlToolTip: htmlToolTip,
                detailContentDs: detailContentDs,
                detailContentDeco: detailContentDeco,
                detailContentDecoModeConfirm: detailContentDecoModeConfirm,
                detailContentDecoNormal: detailContentDecoNormal,  // deco ở mode edit + background normal
                detailContentDecoShift: detailContentDecoShift,   // deco ở mode edit + background shift
                detailContentDecoModeConfirmNormal: detailContentDecoModeConfirmNormal, // deco ở mode confirm + background normal
                detailContentDecoModeConfirmShift: detailContentDecoModeConfirmShift  // deco ở mode confirm + background normal
            };
            
            self.detailContentDs = detailContentDs;
            self.detailColumns = detailColumns;
            self.detailHeaderDeco = detailHeaderDeco;
            self.detailContentDeco = detailContentDeco;
            self.detailContentDecoModeConfirm = detailContentDecoModeConfirm;
            
            self.detailContentDecoNormal = detailContentDecoNormal;  // deco ở mode edit + background normal
            self.detailContentDecoShift = detailContentDecoShift;   // deco ở mode edit + background shift
            self.detailContentDecoModeConfirmNormal = detailContentDecoModeConfirmNormal; // deco ở mode confirm + background normal
            self.detailContentDecoModeConfirmShift = detailContentDecoModeConfirmShift;  // deco ở mode confirm + background normal
			
			if(data.aggreratePersonal) {
				self.dataAggreratePersonal = data.aggreratePersonal;	
			}
			if(data.aggrerateWorkplace) {
				self.dataAggrerateWorkplace = data.aggrerateWorkplace;	
			}
            self.createVertSumData();
            self.createHorzSumData();

            self.horizontalDetailColumns = horizontalDetailColumns;
			if(!data.aggreratePersonal) {
				self.updateVertSumGrid();	
			}
			if(!data.aggrerateWorkplace) {
				self.updateHorzSumGrid();	
			}            

            let empLogin = _.filter(detailContentDs, function(o) { return o.employeeId == self.employeeIdLogin; });
            if (empLogin.length > 0) {
                self.keyGrid = empLogin[0].sid;
                self.rowIndexOfEmpLogin = _.indexOf(detailContentDs, empLogin[0]);
            } else {
                self.keyGrid = '0';
                self.rowIndexOfEmpLogin = 0;
            }
            return result;
        }
        
        // getWorkStyler cell ở mode 1 cell
        getWorkStyle(shiftMasterWithWorkStyleLst,shiftCode){
            let self = this;
            let objShiftMasterWithWorkStyle = _.filter(shiftMasterWithWorkStyleLst, function(o) { return o.shiftMasterCode == shiftCode; });
            if (objShiftMasterWithWorkStyle.length > 0) {
                return objShiftMasterWithWorkStyle[0].workStyle;
            }
        }
        
        // set cell color ở mode 1 cell
        setColorCell(workHolidayCls, ymd, rowId){
            if (workHolidayCls == WorkStyle.ONE_DAY_WORK) {
                return new CellColor('_' + ymd, rowId, "color-attendance", 0);
            }
            if (workHolidayCls == WorkStyle.MORNING_WORK) {
                return new CellColor('_' + ymd, rowId, "color-half-day-work", 0);
            }
            if (workHolidayCls == WorkStyle.AFTERNOON_WORK) {
                return new CellColor('_' + ymd, rowId, "color-half-day-work", 0);
            }
            if (workHolidayCls == WorkStyle.ONE_DAY_REST) {
                return new CellColor('_' + ymd, rowId, "color-holiday", 0);
            }
            if (util.isNullOrUndefined(workHolidayCls) || util.isNullOrEmpty(workHolidayCls)) {
                // デフォルト（黒）  Default (black)
                return new CellColor('_' + ymd, rowId, "color-default", 0);
            }
        }

        setIconEventHeader() {
            let self = this;
            setTimeout(() => {
                // set icon Employee
                let iconEmpPath = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("7.png").serialize();
                $('.icon-leftmost').css('background-image', 'url(' + iconEmpPath + ')');
                // set backgound image icon header
                let iconEventPath = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("120.png").serialize();
                $('.header-image-event').attr('src', iconEventPath);
                let iconNoEventPath = nts.uk.request.location.siteRoot.mergeRelativePath(nts.uk.request.WEB_APP_NAME["comjs"] + "/").mergeRelativePath("lib/nittsu/ui/style/stylesheets/images/icons/numbered/").mergeRelativePath("121.png").serialize();
                $('.header-image-no-event').attr('src', iconNoEventPath);
                if (self.mode() === UpdateMode.EDIT) {
                    self.bindingEventClickFlower();
                }
            }, 1);
        }
            
        saveModeGridToLocalStorege(mode) {
            let self = this;
            self.userInfor.disPlayFormat = mode;
            characteristics.save(self.KEY, self.userInfor);
        }

        /**
         * get setting ban dau
         */
        getSettingDisplayWhenStart(viewMode, isStart) {
            let self = this;

            $(".editMode").addClass("A6_hover").removeClass("A6_not_hover");
            $(".confirmMode").addClass("A6_not_hover").removeClass("A6_hover");

            if (!_.isNil(self.userInfor)) {
                // A4_7
                if (isStart) {
                    self.achievementDisplaySelected(2);
                } else {
                    self.achievementDisplaySelected(self.userInfor.achievementDisplaySelected == false ? 2 : 1);
                }

                // A4_12 背景色の初期選択   (Chọn default màu nền)
                self.backgroundColorSelected(self.userInfor.backgroundColor);

                // get setting height grid
                if (self.userInfor.gridHeightSelection == 1) {
                    self.selectedTypeHeightExTable(1);
                    self.isEnableInputHeight(false);
                } else {
                    self.heightGridSetting(self.userInfor.heightGridSetting);
                    self.selectedTypeHeightExTable(2);
                    self.isEnableInputHeight(true);
                }

                if (viewMode == ViewMode.TIME) {
                    self.visibleBtnInput(true);
                } else {
                    self.visibleBtnInput(false);
                }

                self.enableBtnRedo(false);
                self.enableBtnUndo(false);

                // enable| disable combobox workTime
                let workType = _.filter(__viewContext.viewModel.viewAB.listWorkType(), function(o) { return o.workTypeCode == __viewContext.viewModel.viewAB.selectedWorkTypeCode(); });
                if (workType.length > 0) {
                    // check workTimeSetting 
                    if (workType[0].workTimeSetting == 2) {
                        __viewContext.viewModel.viewAB.disabled(true);
                    } else {
                        __viewContext.viewModel.viewAB.disabled(false);
                    }
                }
            }
        }
        
        // dang ky data
        saveData() {
            let self = this;
            if (nts.uk.ui.errors.hasError()) return;

            // close dialog kdl053 nếu nó đang open
            $('div > iframe').contents().find('#btnClose').trigger('click');

            if (self.mode() == UpdateMode.EDIT) {
                self.saveDataInModeEdit();
            } else if (self.mode() == UpdateMode.DETERMINE) {
                self.saveDataInModeConfirm();
            }
        }

        saveDataInModeConfirm(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let listCellConfirmReg = self.getCellsConfirmReg();
            if (listCellConfirmReg.length == 0)
                return;

            nts.uk.ui.block.grayout();
            let dataReg = [];
            _.forEach(listCellConfirmReg, function(cell) {
                dataReg.push({
                    sid: self.listSid()[cell.rowIndex],
                    ymd: new Date(moment(cell.columnKey.slice(1)).format('YYYY/MM/DD')),
                    isConfirmed: cell.confirm
                });
            });
            service.changeConfirmedState(dataReg).done((rs) => {
                self.enableBtnReg(false);
                self.updateDataSource(listCellConfirmReg);
                //self.updateListCellLock(listCellConfirmReg);
                self.updateDataBindGrid();
                self.cloneDataSource();
                nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error); // Msg_1541
                dfd.reject();
            });
            return dfd.promise();
        }
        
        updateListCellLock(cellsChange: any) {  
            let self = this;
            self.listLockCells = [];
            _.forEach($("#extable").exTable("lockCells"), function(cell: any) {
                self.listLockCells.push({ rowIndex: cell.rowIndex, columnKey: cell.columnKey, confirm: true });
            });
        }
        
        updateDataSource(cellsChange : any) {
            let self = this;
            if (self.userInfor.disPlayFormat == ViewMode.SHIFT) {
                self.updateDataSourceModeShift(cellsChange);
            } else {
                self.updateDataSourceModeWorkInfo(cellsChange);
            }
        }

        updateDataSourceModeWorkInfo(cellsChange: any) {
            let self = this;
            _.forEach(cellsChange, function(cell: any) {
                let sid = self.listSid()[cell.rowIndex];
                let ymd = moment(cell.columnKey.slice(1)).format('YYYY/MM/DD');
                let cellGrid: IWorkScheduleWorkInforDto = _.find(self.listWorkScheduleWorkInfor, function(o: IWorkScheduleWorkInforDto) { return o.employeeId == sid && o.date == ymd; });
                cellGrid.confirmed = cell.confirm; // update lại field confirm
                // check lại điều kiện conditionAbc1 với conditionAbc2 xem còn thỏa mãn không.
                cellGrid.conditionAbc1 = true;
                if (cellGrid.achievements == true || cellGrid.confirmed == true || cellGrid.needToWork == false || cellGrid.supportCategory == SupportCategory.TimeSupport || cellGrid.condTargetdate == false) {
                    cellGrid.conditionAbc1 = false;
                }

                cellGrid.conditionAbc2 = true;
                if (cellGrid.achievements == true || cellGrid.needToWork == false || cellGrid.supportCategory == SupportCategory.TimeSupport || cellGrid.condTargetdate == false) {
                    cellGrid.conditionAbc2 = false;
                }
            });
        }

        updateDataSourceModeShift(cellsChange: any) {
            let self = this;
            _.forEach(cellsChange, function(cell: any) {
                let sid = self.listSid()[cell.rowIndex];
                let ymd = moment(cell.columnKey.slice(1)).format('YYYY/MM/DD');
                let cellGrid: IWorkScheduleShiftInforDto = _.find(self.listWorkScheduleShift, function(o: IWorkScheduleShiftInforDto) { return o.employeeId == sid && o.date == ymd; });
                cellGrid.confirmed = cell.confirm; // update lại field confirm
                // check lại điều kiện conditionAa1 với conditionAa2 xem còn thỏa mãn không.
                cellGrid.conditionAa1 = true;
                if (cellGrid.achievements == true || cellGrid.confirmed == true || cellGrid.needToWork == false || cellGrid.supportCategory == SupportCategory.TimeSupport || cellGrid.condTargetdate == false) {
                    cellGrid.conditionAa1 = false;
                }

                cellGrid.conditionAa2 = true;
                if (cellGrid.achievements == true || cellGrid.needToWork == false || cellGrid.supportCategory == SupportCategory.TimeSupport || cellGrid.condTargetdate == false) {
                    cellGrid.conditionAa2 = false;
                }
            });
        }
        
        // update datasource owr mode shift khi change giua 2 mode background
        updateDataSourceModeShiftWhenChangeBG(cellsChange: any) {
            let self = this;
            let listShiftMasterSaveLocal = self.userInfor.shiftMasterWithWorkStyleLst;
            _.forEach(cellsChange, function(cell: any) {
                let sid = self.listSid()[cell.rowIndex];
                let ymd = moment(cell.columnKey.slice(1)).format('YYYY/MM/DD');
                let cellGrid: IWorkScheduleShiftInforDto = _.find(self.listWorkScheduleShift, function(o: IWorkScheduleShiftInforDto) { return o.employeeId == sid && o.date == ymd; });
                // set cac điều kiện conditionAa1 với conditionAa2 
                cellGrid.haveData = true;
                cellGrid.achievements == false;
                cellGrid.confirmed = false;
                cellGrid.needToWork == true;
                cellGrid.supportCategory == SupportCategory.NotCheering;
                cellGrid.shiftCode = cell.value.shiftCode;
                cellGrid.shiftName = cell.value.shiftName;
                let editState : IEditStateOfDailyAttdDto = {
                    attendanceItemId: '',
                    editStateSetting: sid == self.employeeIdLogin ? EditStateSetting.HAND_CORRECTION_MYSELF : EditStateSetting.HAND_CORRECTION_OTHER,
                    date: ''
                }
                cellGrid.shiftEditState  = editState;
                let workInfo = _.filter(listShiftMasterSaveLocal, function(o) { return o.shiftMasterCode === cell.value.shiftCode; });
                cellGrid.workHolidayCls = workInfo.length > 0 ? workInfo[0].workStyle : null;
                cellGrid.condTargetdate == true;
                cellGrid.conditionAa1 = true;
                cellGrid.conditionAa2 = true;
            });
        }

        getCellsConfirmReg() {
            let self = this;
            let listCellConfirmReg = [];
            let listCellConfirmInit = self.listLockCells; // list cell confirm khi khoi động màn hình, lúc đăng ký xong cũng update lại list này
            let cellsConfirm = _.map($("#extable").exTable("lockCells"), function(n) {
                return { rowIndex: n.rowIndex, columnKey: n.columnKey, confirm :true };
            });

            // lấy ra những cell bi hủy confirm (ban đầu nó đang confirm 
            // => click thêm phát nữa nên bị hủy => những cell này isconfirm = false)
            let cellsCancelConfirm = _.differenceWith(listCellConfirmInit, cellsConfirm, 
            function (c1, c2){ return _.isEqual(c1.rowIndex.toString(), c2.rowIndex.toString()) 
                                   && _.isEqual(c1.columnKey.toString(),c2.columnKey.toString())});
            // map lại data để update field confirm thành false
            cellsCancelConfirm = _.map(cellsCancelConfirm, function(n) {
                return { rowIndex: n.rowIndex, columnKey: n.columnKey, confirm: false };
            });
            
            // những cell mới được confirm, thằng này không cần map lại vì field confirm = true sẵn rồi
            let cellsNewConfirm = _.differenceWith(cellsConfirm, listCellConfirmInit,
                function(c1, c2) { return _.isEqual(c1.rowIndex.toString(), c2.rowIndex.toString())
                                       && _.isEqual(c1.columnKey.toString(), c2.columnKey.toString())
                });
            
            let cellsChange = _.concat(cellsNewConfirm, cellsCancelConfirm);

            return cellsChange;
        }

        // dangky data ở mode Edit
        saveDataInModeEdit(isKsu003 ?, ui ?, detailContentDs ?): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            nts.uk.ui.block.grayout();
            let updatedCells = $("#extable").exTable("updatedCells");
            let params = [];
            let cellsGroup;
            if (self.userInfor.disPlayFormat == ViewMode.SHIFT && self.hasChangeModeBg == true) {
                // cập nhật lại list cells change.
                let updatedCells2 = $("#extable").exTable("updatedCells");
                _.forEach(updatedCells2, function(cell: any) {
                    let exit = _.filter(self.listCellUpdatedWhenChangeModeBg, function(o) { return o.rowIndex == cell.rowIndex && o.columnKey == cell.columnKey; });
                    if (exit.length > 0) {
                        _.remove(self.listCellUpdatedWhenChangeModeBg, function(e) {
                            return e.rowIndex == cell.rowIndex && e.columnKey == cell.columnKey;
                        });
                        self.listCellUpdatedWhenChangeModeBg.push(cell);
                    } else {
                        self.listCellUpdatedWhenChangeModeBg.push(cell);
                    }
                });

                cellsGroup = self.groupByRowIndAndColKey(self.listCellUpdatedWhenChangeModeBg, function(cell) {
                    return [cell.rowIndex, cell.columnKey];
                });
            } else {
                cellsGroup = self.groupByRowIndAndColKey(updatedCells, function(cell) {
                    return [cell.rowIndex, cell.columnKey];
                });
            }

            let data = self.buidDataReg(self.userInfor.disPlayFormat, cellsGroup);
            
            service.regWorkSchedule(data).done((rs) => {
                self.taskId = rs.taskInfor.id;
                self.checkStateAsyncTask(isKsu003, detailContentDs);
                
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }
        
        checkStateAsyncTask(isKsu003, detailContentDs) {
            let self = this;

            nts.uk.deferred.repeat(conf => conf
                .task(() => {
                    return nts.uk.request.asyncTask.getInfo(self.taskId).done(function(res: any) {
                        // finish task
                        if (res.succeeded || res.failed || res.cancelled) {
                            let arrayItems = [];
                            let dataResult: any = {};
                            dataResult.listErrorInfo = [];
                            dataResult.hasError = false;
                            dataResult.isRegistered = true; 
                            _.forEach(res.taskDatas, item => {
                                if (item.key == 'STATUS_REGISTER') {
                                    dataResult.isRegistered = item.valueAsBoolean;
                                } else if (item.key == 'STATUS_ERROR') {
                                    dataResult.hasError = item.valueAsBoolean;
                                } else {
                                    arrayItems.push(item);
                                }
                            });
                            
                            if (arrayItems.length > 0) {
                                let listErrorInfo = _.map(arrayItems, obj2 => {
                                    return new InforError(JSON.parse(obj2.valueAsString));
                                });
                                dataResult.listErrorInfo = listErrorInfo;
                            }
                            
                            if (dataResult.hasError == false) {
                                $("#extable").exTable('saveScroll');
                                let $grid = $('div.ex-body-detail');
                                self.updateAfterSaveData($grid[0]);
                                self.getAggregatedInfo(true, true, true, true).done(() => {
                                    if (isKsu003) {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                            self.openKsu003(ui, detailContentDs);
                                        });
                                    } else {
                                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                                    }
                                });
                            } else {
                                $("#extable").exTable('saveScroll');
                                let $grid = $('div.ex-body-detail');
                                self.updateAfterSaveData($grid[0]);
                                // get lại data A11.A12
                                self.getAggregatedInfo(true, true, true, true).done(() => {
                                    if (dataResult.listErrorInfo.length > 0) {
                                        self.openKDL053(dataResult);
                                    }
                                });
                            }
                            self.hasChangeModeBg = false;
                            self.listCellUpdatedWhenChangeModeBg = [];
                        }
                    });
                }).while(infor => {
                    return infor.pending || infor.running;
                }).pause(1000));
        }

        buidDataReg(viewMode, cellsGroup) {
            let self = this;
            let dataReg = [];
            if (viewMode == ViewMode.TIME) {
                let dataSource = $("#extable").exTable('dataSource', 'detail').body;
                _.forEach(cellsGroup, function(cells) {
                    if (cells.length > 0) {
                        let cell = cells[0];
                        let sid = self.listSid()[cell.rowIndex];
                        let ymd = moment(cell.columnKey.slice(1)).format('YYYY/MM/DD');

                        let cellData = dataSource[cell.rowIndex][cell.columnKey];

                        let cellStartTime = cellData.startTime;
                        let cellEndTime   = cellData.endTime;

                        let startTime = null, endTime = null;
                        let isChangeTime = false;
                        if (!_.isEmpty(cellStartTime) && !_.isNil(cellStartTime)) {
                            startTime = duration.parseString(cellStartTime).toValue();
                            isChangeTime = true;
                        }
                        if (!_.isEmpty(cellEndTime) && !_.isNil(cellEndTime)) {
                            endTime = duration.parseString(cellEndTime).toValue();
                            isChangeTime = true;
                        }

                        let dataCell = {
                            sid: sid,
                            ymd: ymd,
                            viewMode: viewMode,
                            workTypeCd: cell.value.workTypeCode,
                            workTimeCd: cell.value.workTimeCode,
                            startTime : startTime,
                            endTime   : endTime,
                            isChangeTime: isChangeTime,
                            rowIndex : cell.rowIndex,
                            columnKey: cell.columnKey
                        }
                        dataReg.push(dataCell);
                    }
                });
            } else if (viewMode == ViewMode.SHORTNAME) {
                _.forEach(cellsGroup, function(cells) {
                    if (cells.length > 0) {
                        let cell = cells[0];
                        let objWorkType = _.filter(__viewContext.viewModel.viewAB.listWorkType(), function(o) { return o.workTypeCode == cell.value.workTypeCode; });
                        let sid = self.listSid()[cell.rowIndex];
                        let ymd = moment(cell.columnKey.slice(1)).format('YYYY/MM/DD');
                        let dataCell = {
                            sid: sid,
                            ymd: ymd,
                            viewMode: viewMode,
                            workTypeCd: cell.value.workTypeCode,
                            workTimeCd: cell.value.workTimeCode,
                            startTime: null,
                            endTime: null,
                            isChangeTime: false
                        }
                        dataReg.push(dataCell);
                    }
                });
            } else if (viewMode == ViewMode.SHIFT) {
                    _.forEach(cellsGroup, function(cells) {
                    let cell = cells[0];
                    let sid = self.listSid()[cell.rowIndex];
                    let ymd = moment(cell.columnKey.slice(1)).format('YYYY/MM/DD');
                    let shiftCode;
                    if (!_.isNil(cell)) {
                        shiftCode = cell.value.shiftCode;
                    }
                    let dataCell = {
                        sid: sid,
                        ymd: ymd,
                        shiftCode: shiftCode,
                        viewMode : viewMode
                    }
                    dataReg.push(dataCell);
                });
            }
            return dataReg;
        }
        
        groupByRowIndAndColKey(array, f) {
            let groups = {};
            array.forEach(function(o) {
                let group = JSON.stringify(f(o));
                groups[group] = groups[group] || [];
                groups[group].push(o);
            });
            return Object.keys(groups).map(function(group) {
                return groups[group];
            })
        }

        // Clear states , remove cells updated
        // update grid sau khi dang ky data
        updateAfterSaveData($grid: HTMLElement) {
            let self = this;
            // Clear states
            $.data($grid, "copy-history", null);
            $.data($grid, "redo-stack", null);
            $.data($grid, "edit-history", null);
            $.data($grid, "edit-redo-stack", null);
            $.data($grid, "stick-history", null);
            $.data($grid, "stick-redo-stack", null);

            // remove cells updated
            $('#extable').data('extable').modifications = null;

            self.enableBtnReg(false);
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
        }

        openKDL053(dataReg : any) {
            let self = this;
            let param = {
                employeeIds: self.listSid(),                  // 社員の並び順
                isRegistered: dataReg.registered == true ? 1 : 0,           // 登録されたか
                errorRegistrationList: dataReg.listErrorInfo, // エラー内容リスト 
            }
            setShared('dataShareDialogKDL053', param);
            nts.uk.ui.windows.sub.modeless('/view/kdl/053/a/index.xhtml').onClosed(function(): any {});
            nts.uk.ui.block.clear();
        }

        /**
        * Create exTable
        */
        initExTable(dataBindGrid: any, viewMode: string, updateMode : string): void {
            let self = this,
                //Get dates in time period
                currentDay = new Date(),
                windowXOccupation = 65,
                windowYOccupation = 328;
            let bodyHeightMode = self.userInfor.gridHeightSelection == 1 ? BodyHeightSettingMode.DYNAMIC : BodyHeightSettingMode.FIXED;
            // phần leftMost
            let leftmostColumns = [];
            let leftmostHeader = {};
            let leftmostContent = {};
            let leftmostDs = dataBindGrid.leftmostDs;

            leftmostColumns = [{
                key: "codeNameOfEmp", headerText: getText("KSU001_205"), width: self.widthA8 +"px", icon: { for: "body", class: "icon-leftmost", width: "25px" },
                css: { whiteSpace: "pre" }, control: "link", handler: function(rData, rowIdx, key) { console.log(rowIdx); },
                headerControl: "link", headerHandler: function() {  }
            }];

            leftmostHeader = {
                columns: leftmostColumns,
                rowHeight: "60px",
                width: self.widthA8+"px"
            };

            leftmostContent = {
                columns: leftmostColumns,
                dataSource: leftmostDs,
                primaryKey: "sid"
            };

            // Phần middle
            let middleDs = dataBindGrid.middleDs;
            let updateMiddleDs = [];
            let middleColumns = [];
            let middleContentDeco = [];
            let middleHeader = {};
            let middleContent = {};
            
            if (self.showA9) {
                let widthMid : number = 0;
                middleColumns = [];
                if(self.showTeamCol){
                    widthMid = widthMid + 40;
                    middleColumns.push({ headerText: getText("KSU001_4023"), key: "team", width: "40px", css: { whiteSpace: "none" } });    
                }
                if(self.showRankCol){
                    widthMid = widthMid + 40;
                    middleColumns.push({ headerText: getText("KSU001_4024"), key: "rank", width: "40px", css: { whiteSpace: "none" } });
                }
                if(self.showQualificCol){
                    widthMid = widthMid + 40;
                    middleColumns.push({ headerText: getText("KSU001_4025"), key: "qualification", width: "40px", css: { whiteSpace: "none" } });
                }
                
                self.widthMid = widthMid;
                
                let key = request.location.current.rawUrl + "/extable/areawidths";
                uk.localStorage.getItem(key).ifPresent((data) => {
                    let areawidths = JSON.parse(data);
                    areawidths["ex-header-middle"] = widthMid;
                    uk.localStorage.setItemAsJson(key, areawidths);
                });
                
                middleHeader = {
                    columns: middleColumns,
                    width:   widthMid+"px",
                    features: [{
                        name: "HeaderRowHeight",
                        rows: { 0: "60px" }
                    }]
                };

                middleContent = {
                    columns: middleColumns,
                    dataSource: middleDs,
                    primaryKey: "sid",
                    features: [{
                        name: "BodyCellStyle",
                        decorator: middleContentDeco
                    }]
                };
            }

            // Phần detail
            let detailHeaderDeco = dataBindGrid.detailHeaderDeco;
            let detailHeaderDs = [];
            let detailContentDs = dataBindGrid.detailContentDs;
            let detailColumns = dataBindGrid.detailColumns;
            let objDetailHeaderDs = dataBindGrid.objDetailHeaderDs;
            let htmlToolTip = dataBindGrid.htmlToolTip;
            let timeRanges = [];
            let detailContentDeco = [];
            if (viewMode == ViewMode.SHIFT) {
                if (updateMode == UpdateMode.DETERMINE) {
                    detailContentDeco = self.userInfor.backgroundColor == BackGroundShiftMode.NORMAL ? dataBindGrid.detailContentDecoModeConfirmNormal : dataBindGrid.detailContentDecoModeConfirmShift;
                } else {
                    detailContentDeco = self.userInfor.backgroundColor == BackGroundShiftMode.NORMAL ? dataBindGrid.detailContentDecoNormal : dataBindGrid.detailContentDecoShift;
                }
            } else {
                detailContentDeco = updateMode == UpdateMode.DETERMINE ? dataBindGrid.detailContentDecoModeConfirm : dataBindGrid.detailContentDeco;
            }
            
            //create dataSource for detailHeader
            detailHeaderDs.push(new ExItem(undefined, null, null, null, true, self.arrDay));
            detailHeaderDs.push(objDetailHeaderDs);

            let detailHeader = {
                columns: detailColumns,
                dataSource: detailHeaderDs,
                width: "700px",
                features: [{
                    name: "HeaderRowHeight",
                    rows: { 0: "40px", 1: "20px" }
                }, {
                        name: "HeaderCellStyle",
                        decorator: detailHeaderDeco
                    }, {
                        name: "ColumnResizes"
                    }, {
                    }, {
                        name: "Hover",
                        selector: ".header-image-event",
                        enter: function(ui) {
                            if (ui.rowIdx === 1 && $(ui.target).is(".header-image-event")) {
                                let objTooltip = _.filter(htmlToolTip, function(o) { return o.key == ui.columnKey; });
                                if (objTooltip.length > 0) {
                                    let heightToolTip = objTooltip[0].heightToolTip;
                                    ui.tooltip("show", $("<div/>").css({ width: "max-content", height: "max-content" }).html(objTooltip[0].value));
                                } else {
                                    ui.tooltip("show", $("<div/>").css({ width: "60px", height: "60px" }).html(''));
                                }
                            }
                        },
                        exit: function(ui) {
                            ui.tooltip("hide");
                        }
                    }]
            };
            
            // Open KSU003
            if (self.canOpenKsu003) {
                detailColumns.forEach((col: any) => {
                    if (col.visible === false) return;
                    col.headerHandler = (ui: any) => {
                        self.checkBefOPenKsu003(ui, detailContentDs);
                        return false;
                    };
                });
            }

            let detailContent = {
                columns: detailColumns,
                dataSource: detailContentDs,
                primaryKey: "sid",
                //        highlight: false,
                features: [{
                    name: "BodyCellStyle",
                    decorator: detailContentDeco
                }, {
                        name: "TimeRange",
                        ranges: timeRanges
                    }],
                view: function(mode) {
                    switch (mode) {
                        case "shift":
                            return ["shiftName"];
                        case "shortName":
                            return ["workTypeName", "workTimeName"];
                        case "time":
                            return ["workTypeName", "workTimeName", "startTime", "endTime"];
                    }
                },
                fields: ["workTypeCode", "workTypeName", "workTimeCode", "workTimeName", "shiftName", "startTime", "endTime", "shiftCode", "confirmed", "achievements", "needToWork","supportCategory","condTargetdate"],
            };
            
            let vertSumHeader = self.createVertSumHeader();
            let vertSumContent = self.createVertSumContent();
            let leftHorzSumHeader = self.createLeftHorzSumHeader();
            let leftHorzSumContent = self.createLeftHorzSumContent();
            let horizontalSumHeader = self.createHorizontalSumHeader();
            let horizontalSumContent = self.createHorizontalSumContent();
            let rightHorzSumHeader = self.createRightHorzSumHeader();
            let rightHorzSumContent = self.createRightHorzSumContent();
            
            function customValidate(idx, key, innerIdx, value, obj) {
                let startTime, endTime;
                if (innerIdx === 2) {
                    startTime = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                    endTime = !_.isNil(obj.endTime) ? nts.uk.time.minutesBased.duration.parseString(obj.endTime).toValue() : 0;
                } else if (innerIdx === 3) {
                    startTime = !_.isNil(obj.startTime) ? nts.uk.time.minutesBased.duration.parseString(obj.startTime).toValue() : 0;
                    endTime = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                }
                
                if (startTime > endTime) {
                    return { isValid: false, message: "開始時刻と終了時刻の入力が不正です" };
                }

                if (innerIdx === 2) {
                    return { isValid: true, innerErrorClear: [3] };
                } else if (innerIdx === 3) {
                    return { isValid: true, innerErrorClear: [2] };
                }

                return { isValid: true };
            };

            let start = performance.now();

            let extbl = new nts.uk.ui.exTable.ExTable($("#extable"), {
                headerHeight: "60px",
                bodyRowHeight: viewMode == ViewMode.SHIFT ? "35px" : "50px",
                bodyHeight: "300px",
                horizontalSumHeaderHeight: "40px",
                horizontalSumBodyHeight: "140px",
                horizontalSumBodyRowHeight: "20px",
                areaResize: true,
                bodyHeightMode: bodyHeightMode,
                windowXOccupation: windowXOccupation,
                windowYOccupation: windowYOccupation,
                manipulatorId: self.keyGrid,
                manipulatorKey: "sid",
                updateMode: updateMode,
                pasteOverWrite: true,
                stickOverWrite: true,
                viewMode: viewMode,
                showTooltipIfOverflow: true,
                errorMessagePopup: true,
                customValidate: customValidate,
                determination: {
                    rows: [0],
                    columns: ["codeNameOfEmp"]
                },
                heightSetter: {
                    showBodyHeightButton: true,
                    click: function() {
                        alert("Show dialog");
                    }
                }
            });
            extbl.LeftmostHeader(leftmostHeader).LeftmostContent(leftmostContent);
            if (self.showA9) {
                extbl.MiddleHeader(middleHeader).MiddleContent(middleContent);
            }
            extbl.DetailHeader(detailHeader).DetailContent(detailContent);
            if (self.showA11()) {
                extbl.VerticalSumHeader(vertSumHeader).VerticalSumContent(vertSumContent);  
            }
            if (self.showA12()) {
                extbl.LeftHorzSumHeader(leftHorzSumHeader).LeftHorzSumContent(leftHorzSumContent);
                extbl.HorizontalSumHeader(horizontalSumHeader).HorizontalSumContent(horizontalSumContent);
                extbl.RightHorzSumHeader(rightHorzSumHeader).RightHorzSumContent(rightHorzSumContent);
            }
            extbl.create();

            $("#sub-content-main").width($('#extable').width() + 30);
            console.log(performance.now() - start);
            
            if (self.showA11()) {
                $("#vertDropDown").html(function() { return $('#vertDiv'); });
                $('#vertDiv').css('display', '');   
                $('#vertDiv').css('margin-left', 0 + 'px');
                
                $('.ex-body-vert-sum').scroll(() => {
                    $('#vertDiv').css('margin-left', $('.ex-body-vert-sum').scrollLeft().valueOf() + 'px');
                });
            }
            
            if (self.showA12()) {
                $("#horzDropDown").html(function() { return $('#horzDiv'); });
                $('#horzDiv').css('display', '');
            
                $('.extable-body-left-horz-sum tbody tr td:first-child()').css('border-right', '1px solid transparent');    
                
                self.showA12_2(_.includes([WorkplaceCounterCategory.WORKTIME_PEOPLE, WorkplaceCounterCategory.LABOR_COSTS_AND_TIME], self.useCategoriesWorkplaceValue()) ||
                            (_.includes([WorkplaceCounterCategory.EXTERNAL_BUDGET], self.useCategoriesWorkplaceValue()) && self.funcNo15_WorkPlace));
            }
            
            $("#extable").exTable("saveScroll");
        }

        createVertSumData() {
            let self = this,
                detailContentDs = self.detailContentDs,
                vertSumColumns: any = [],
                vertSumContentDs: any = [],
                vertContentDeco: any = [],
                group: any = [];
            switch(self.useCategoriesPersonalValue()) {
                // 月間想定給与額
                case PersonalCounterCategory.MONTHLY_EXPECTED_SALARY:   
                    group = [
                        { headerText: getText("KSU001_18"), key: "criterion", width: "100px" },
                        { headerText: getText("KSU001_19"), key: "salary", width: "100px" },
                    ];
                    _.forEach(detailContentDs, (item: any, index) => {
                        let object: any = _.find(self.dataAggreratePersonal.estimatedSalary, loopItem => loopItem.sid==item.employeeId);
                        if(object) {
                            vertSumContentDs.push({ 
								empID: item.employeeId, 
								sid: item.sid, 
								criterion: nts.uk.ntsNumber.formatNumber(object.criterion, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0})), 
								salary: nts.uk.ntsNumber.formatNumber(object.salary, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0})) 
							});
                            if(!_.isEmpty(object.background)) {
                                vertContentDeco.push(new CellColor("salary", item.sid, "#" + object.background));
                            }
                        } else {
                            vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, criterion: null, salary: null });
                        }
                    });
                    break;
                    
                // 年間想定給与額
                case PersonalCounterCategory.CUMULATIVE_ESTIMATED_SALARY: 
                    group = [
                        { headerText: getText("KSU001_18"), key: "criterion", width: "100px" },
                        { headerText: getText("KSU001_19"), key: "salary", width: "100px" },
                    ];
                    _.forEach(detailContentDs, (item: any, index) => {
                        let object: any = _.find(self.dataAggreratePersonal.estimatedSalary, loopItem => loopItem.sid==item.employeeId);
                        if(object) {
                            vertSumContentDs.push({ 
								empID: item.employeeId, 
								sid: item.sid, 
								criterion: nts.uk.ntsNumber.formatNumber(object.criterion, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0})), 
								salary: nts.uk.ntsNumber.formatNumber(object.salary, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0})) 
							});
                            if(!_.isEmpty(object.background)) {
                                vertContentDeco.push(new CellColor("salary", item.sid, "#" + object.background));  
                            }
                        } else {
                            vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, criterion: null, salary: null });
                        }
                    });
                    break;
                    
                // 労働時間
                case PersonalCounterCategory.WORKING_HOURS: 
                    group = [
                        { headerText: getText("KSU001_20"), key: "colum1", width: "100px" },
                        { headerText: getText("KSU001_50"), key: "colum2", width: "100px" },
                        { headerText: getText("KSU001_51"), key: "colum3", width: "100px" },
                    ];
                    _.forEach(detailContentDs, (item: any) => {
                        let object: any = _.find(self.dataAggreratePersonal.workHours, loopItem => loopItem.sid==item.employeeId);
                        if(object) {
                            // enum AttendanceTimesForAggregation
                            let colum1Object = _.find(object.workHours, (objectItem: any) => objectItem.key==AttendanceTimesForAggregation.WORKING_TOTAL),
                                colum2Object = _.find(object.workHours, (objectItem: any) => objectItem.key==AttendanceTimesForAggregation.WORKING_WITHIN),
                                colum3Object = _.find(object.workHours, (objectItem: any) => objectItem.key==AttendanceTimesForAggregation.WORKING_EXTRA);
                            vertSumContentDs.push({ 
                                empID: item.employeeId, 
								sid: item.sid,
                                colum1: _.isEmpty(colum1Object) ? '' : nts.uk.time.format.byId("Time_Short_HM", colum1Object.value), 
                                colum2: _.isEmpty(colum2Object) ? '' : nts.uk.time.format.byId("Time_Short_HM", colum2Object.value), 
                                colum3: _.isEmpty(colum3Object) ? '' : nts.uk.time.format.byId("Time_Short_HM", colum3Object.value)
                            });
                        } else {
                            vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, colum1: null, colum2: null, colum3: null });
                        }
                    });
                    break;
                    
                // 出勤・休日日数
                case PersonalCounterCategory.ATTENDANCE_HOLIDAY_DAYS: 
                    group = [
                        { headerText: getText("KSU001_62"), key: "colum1", width: "100px" },
                        { headerText: getText("KSU001_63"), key: "colum2", width: "100px" },
                    ];
                    _.forEach(detailContentDs, (item: any) => {
                        let object: any = _.find(self.dataAggreratePersonal.workHours, loopItem => loopItem.sid==item.employeeId);
                        if(object) {
                            // WorkClassificationAsAggregationTarget
                            let colum1Object = _.find(object.workHours, (objectItem: any) => objectItem.key==WorkClassificationAsAggregationTarget.WORKING),
                                colum2Object = _.find(object.workHours, (objectItem: any) => objectItem.key==WorkClassificationAsAggregationTarget.HOLIDAY);
                            vertSumContentDs.push({ 
                                empID: item.employeeId, 
								sid: item.sid,
                                colum1: _.isEmpty(colum1Object) ? '' : nts.uk.ntsNumber.formatNumber(colum1Object.value, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 1})), 
                                colum2: _.isEmpty(colum2Object) ? '' : nts.uk.ntsNumber.formatNumber(colum2Object.value, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 1}))
                            });
                        } else {
                            vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, colum1: null, colum2: null });
                        }
                    });
                    break;
                    
                // 回数集計１
                case PersonalCounterCategory.TIMES_COUNTING_1: 
                    let timeCount1: Array<any> = self.dataAggreratePersonal.timeCount,
                        timeCount1Value = _.filter(timeCount1, item => !_.isEmpty(item.totalTimesMapDto));
                    if(_.isEmpty(timeCount1Value)) {
                        group = [
                            { headerText: "", key: "colum1", width: "100px" },
                        ];
						_.forEach(detailContentDs, (item: any) => {
	                    	vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, colum1: null });
	                    });
                        break;
                    }
					let sumData1: any = [];
					_.forEach(timeCount1, timeCount1Item => {
						_.forEach(timeCount1Item.totalTimesMapDto, totalTimesItem => {
							totalTimesItem.sid = timeCount1Item.sid;
							sumData1.push(totalTimesItem);
						});
					});
					_.forEach(_.values(_.groupBy(sumData1, 'totalCountNo')), (groupItem: Array<any>, index: number) => {
						group.push({ headerText: _.get(_.head(groupItem), 'totalTimesName'), key: "colum" + (index+1), width: "100px" });
	                    _.forEach(detailContentDs, (item: any) => {
	                        let pushObject = { empID: item.employeeId, sid: item.sid },
								object: any = _.find(groupItem, loopItem => loopItem.sid==item.employeeId),
								existItem = _.find(vertSumContentDs, (vertSumContentDsItem: any) => vertSumContentDsItem.empID==pushObject.empID);
	                        if(object) {
								if(existItem) {
									_.set(existItem, "colum" + (index+1), object.value);
								} else {
									_.set(pushObject, "colum" + (index+1), object.value);
									vertSumContentDs.push(pushObject);
								}
	                        } else {
								if(existItem) {
									_.set(existItem, "colum" + (index+1), null);
								} else {
									_.set(pushObject, "colum" + (index+1), null);
									vertSumContentDs.push(pushObject);
								}
	                        }
	                    });		
					});
                    break;
                    
                // 回数集計２
                case PersonalCounterCategory.TIMES_COUNTING_2: 
                    let timeCount2: Array<any> = self.dataAggreratePersonal.timeCount,
                        timeCount2Value = _.filter(timeCount2, item => !_.isEmpty(item.totalTimesMapDto));
                    if(_.isEmpty(timeCount2Value)) {
                        group = [
                            { headerText: "", key: "colum1", width: "100px" },
                        ];
						_.forEach(detailContentDs, (item: any) => {
	                    	vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, colum1: null });
	                    });
                        break;
                    }
					let sumData2: any = [];
					_.forEach(timeCount2, timeCount2Item => {
						_.forEach(timeCount2Item.totalTimesMapDto, totalTimesItem => {
							totalTimesItem.sid = timeCount2Item.sid;
							sumData2.push(totalTimesItem);
						});
					});
                    _.forEach(_.values(_.groupBy(sumData2, 'totalCountNo')), (groupItem: Array<any>, index: number) => {
						group.push({ headerText: _.get(_.head(groupItem), 'totalTimesName'), key: "colum" + (index+1), width: "100px" });
	                    _.forEach(detailContentDs, (item: any) => {
	                        let pushObject = { empID: item.employeeId, sid: item.sid },
								object: any = _.find(groupItem, loopItem => loopItem.sid==item.employeeId),
								existItem = _.find(vertSumContentDs, (vertSumContentDsItem: any) => vertSumContentDsItem.empID==pushObject.empID);
	                        if(object) {
								if(existItem) {
									_.set(existItem, "colum" + (index+1), object.value);
								} else {
									_.set(pushObject, "colum" + (index+1), object.value);
									vertSumContentDs.push(pushObject);
								}
	                        } else {
								if(existItem) {
									_.set(existItem, "colum" + (index+1), null);
								} else {
									_.set(pushObject, "colum" + (index+1), null);
									vertSumContentDs.push(pushObject);
								}
	                        }
	                    });		
					});
                    break;
                    
                // 回数集計３
                case PersonalCounterCategory.TIMES_COUNTING_3: 
                    let timeCount3: Array<any> = self.dataAggreratePersonal.timeCount,
                        timeCount3Value = _.filter(timeCount3, item => !_.isEmpty(item.totalTimesMapDto));
                    if(_.isEmpty(timeCount3Value)) {
                        group = [
                            { headerText: "", key: "colum1", width: "100px" },
                        ];
						_.forEach(detailContentDs, (item: any) => {
	                    	vertSumContentDs.push({ empID: item.employeeId, sid: item.sid, colum1: null });
	                    });
                        break;
                    }
					let sumData3: any = [];
					_.forEach(timeCount3, timeCount3Item => {
						_.forEach(timeCount3Item.totalTimesMapDto, totalTimesItem => {
							totalTimesItem.sid = timeCount3Item.sid;
							sumData3.push(totalTimesItem);
						});
					});
                    _.forEach(_.values(_.groupBy(sumData3, 'totalCountNo')), (groupItem: Array<any>, index: number) => {
						group.push({ headerText: _.get(_.head(groupItem), 'totalTimesName'), key: "colum" + (index+1), width: "100px" });
	                    _.forEach(detailContentDs, (item: any) => {
	                        let pushObject = { empID: item.employeeId, sid: item.sid },
								object: any = _.find(groupItem, loopItem => loopItem.sid==item.employeeId),
								existItem = _.find(vertSumContentDs, (vertSumContentDsItem: any) => vertSumContentDsItem.empID==pushObject.empID);
	                        if(object) {
								if(existItem) {
									_.set(existItem, "colum" + (index+1), object.value);
								} else {
									_.set(pushObject, "colum" + (index+1), object.value);
									vertSumContentDs.push(pushObject);
								}
	                        } else {
								if(existItem) {
									_.set(existItem, "colum" + (index+1), null);
								} else {
									_.set(pushObject, "colum" + (index+1), null);
									vertSumContentDs.push(pushObject);
								}
	                        }
	                    });		
					});
                    break;
                    
                deault: break;
            }
            vertSumColumns = [
                { 
                    headerText: '<div id="vertDropDown"></div>',
                    icon: { for: "header", class: "", width: "0px" },
                    group: group
                }
            ];  
            self.vertSumColumns = vertSumColumns;
            self.vertSumContentDs = vertSumContentDs;
            self.vertContentDeco = vertContentDeco; 
        }
        
        createHorzSumData() {
            let self = this,
                keys = _.keys(new ExItem(undefined, null, null, null, true, self.arrDay)),
                leftHorzContentDs: any = [],
                horizontalSumContentDs: any = [],
                rightHorzContentDs: any = [];
            switch(self.useCategoriesWorkplaceValue()) {
                // 人件費・時間
                case WorkplaceCounterCategory.LABOR_COSTS_AND_TIME: 
                    let laborCostAndTime: Array<any> = self.dataAggrerateWorkplace.laborCostAndTime,
                        laborCostAndTimeValue = _.filter(laborCostAndTime, item => !_.isEmpty(item.laborCostAndTime));
                    if(_.isEmpty(laborCostAndTimeValue)) {
						leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
						let objectLaborCostAndTime = { sid: '' };
                    	_.set(objectLaborCostAndTime, 'id', 'id1');
						_.forEach(keys, key => {
							if(_.includes(['employeeId', 'sid'], key)) {
                                return; 
                            }
							_.set(objectLaborCostAndTime, key, '');
						});
						horizontalSumContentDs.push(objectLaborCostAndTime);    
                    	rightHorzContentDs.push({ id: 'id1', sum: '' });
                        break;
                    }
					let laborCostAndTimeData: any = [];
					_.forEach(laborCostAndTime, laborCostAndTimeItem => {
						_.forEach(laborCostAndTimeItem.laborCostAndTime, laborCostAndTimeSubItem => {
							laborCostAndTimeSubItem.date = laborCostAndTimeItem.date;
							laborCostAndTimeData.push(laborCostAndTimeSubItem);
						});
					});
                    for(let i=1; i<=7; i++) {
						let findValueObjectLst: any = [];
						switch(i) {
							case 1: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.WITHIN && findValueObjectItem.itemType==LaborCostItemType.TIME);
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id1', title: '', subtitle: getText("KSU001_59") });	
									}
                                    break;
                            case 2: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                    findValueObjectItem.unit==AggregationUnitOfLaborCosts.WITHIN && findValueObjectItem.itemType==LaborCostItemType.AMOUNT); 
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id2', title: '', subtitle: getText("KSU001_60") });
									}
                                    break;
                            case 3: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                    findValueObjectItem.unit==AggregationUnitOfLaborCosts.EXTRA && findValueObjectItem.itemType==LaborCostItemType.TIME); 
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id3', title: '', subtitle: getText("KSU001_59") });
									}
                                    break;
                            case 4: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                    findValueObjectItem.unit==AggregationUnitOfLaborCosts.EXTRA && findValueObjectItem.itemType==LaborCostItemType.AMOUNT); 
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id4', title: '', subtitle: getText("KSU001_60") });
									}
                                    break;
                            case 5: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                    findValueObjectItem.unit==AggregationUnitOfLaborCosts.TOTAL && findValueObjectItem.itemType==LaborCostItemType.TIME); 
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id5', title: '', subtitle: getText("KSU001_59") });
									}
                                    break;
                            case 6: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                    findValueObjectItem.unit==AggregationUnitOfLaborCosts.TOTAL && findValueObjectItem.itemType==LaborCostItemType.AMOUNT); 
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id6', title: '', subtitle: getText("KSU001_60") });
									}
                                    break;
                            case 7: findValueObjectLst = _.find(laborCostAndTimeData, (findValueObjectItem: any) => 
                                                    findValueObjectItem.unit==AggregationUnitOfLaborCosts.TOTAL && findValueObjectItem.itemType==LaborCostItemType.BUDGET); 
									if(!_.isEmpty(findValueObjectLst)) {
										leftHorzContentDs.push({ id: 'id7', title: '', subtitle: getText("KSU001_61") });
									}
                                    break;
                            default: break;		
						}
						if(_.isEmpty(findValueObjectLst)) {
							continue;	
						}
						let withinLeft = _.head(_.filter(leftHorzContentDs, (item: any) => _.includes(['id1', 'id2'], item.id)));
						if(!_.isEmpty(withinLeft)) {
							withinLeft.title = getText("KSU001_50"); 	
						}
						let extraLeft = _.head(_.filter(leftHorzContentDs, (item: any) => _.includes(['id3', 'id4'], item.id)));
						if(!_.isEmpty(extraLeft)) {
							extraLeft.title = getText("KSU001_51"); 	
						}
						let totalLeft = _.head(_.filter(leftHorzContentDs, (item: any) => _.includes(['id5', 'id6', 'id7'], item.id)));
						if(!_.isEmpty(totalLeft)) {
							totalLeft.title = getText("KSU001_58"); 	
						}
                        let objectLaborCostAndTime = { sid: '' }, sumLaborCostAndTime: any = '';
                        _.set(objectLaborCostAndTime, 'id', 'id'+i);
                        _.forEach(keys, key => {
                            if(_.includes(['employeeId', 'sid'], key)) {
                                return; 
                            }
                            let findObject: any = _.find(laborCostAndTime, item => key==moment(item.date).format('_YYYYMMDD'));
                            if(!_.isEmpty(findObject)) {
                                let findValueObject: any = null;
                                switch(i) {
                                    case 1: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.WITHIN && findValueObjectItem.itemType==LaborCostItemType.TIME); 
                                            break;
                                    case 2: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.WITHIN && findValueObjectItem.itemType==LaborCostItemType.AMOUNT); 
                                            break;
                                    case 3: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.EXTRA && findValueObjectItem.itemType==LaborCostItemType.TIME); 
                                            break;
                                    case 4: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.EXTRA && findValueObjectItem.itemType==LaborCostItemType.AMOUNT); 
                                            break;
                                    case 5: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.TOTAL && findValueObjectItem.itemType==LaborCostItemType.TIME); 
                                            break;
                                    case 6: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.TOTAL && findValueObjectItem.itemType==LaborCostItemType.AMOUNT); 
                                            break;
                                    case 7: findValueObject = _.find(findObject.laborCostAndTime, (findValueObjectItem: any) => 
                                                            findValueObjectItem.unit==AggregationUnitOfLaborCosts.TOTAL && findValueObjectItem.itemType==LaborCostItemType.BUDGET); 
                                            break;
                                    default: break;
                                }
								let value = null;
								if(!_.isEmpty(findValueObject)) {
									value = _.isNumber(findValueObject.value) ? findValueObject.value : 
											_.isEmpty(findValueObject.value) ? null : 
											_.isNaN(_.toNumber(findValueObject.value)) ? null : _.toNumber(findValueObject.value);
								}
								if(_.includes([1, 3, 5], i)) {
									_.set(objectLaborCostAndTime, key, _.isNumber(value) ? nts.uk.time.format.byId("Time_Short_HM", value) : '');	
								} else {
									_.set(objectLaborCostAndTime, key, _.isNumber(value) ? 
									nts.uk.ntsNumber.formatNumber(value, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0})) : '');	
								}
								if(_.isNumber(value)) {
									if(_.isNumber(sumLaborCostAndTime)) {
										sumLaborCostAndTime += value;	
									} else {
										sumLaborCostAndTime = value;
									}
								}
                            } else {
                                _.set(objectLaborCostAndTime, key, ''); 
                            }
                        });
                        horizontalSumContentDs.push(objectLaborCostAndTime);    
						if(_.isNumber(sumLaborCostAndTime)) {
							if(_.includes([1, 3, 5], i)) {
								sumLaborCostAndTime = nts.uk.time.format.byId("Time_Short_HM", sumLaborCostAndTime);	
							} else {
								sumLaborCostAndTime = nts.uk.ntsNumber.formatNumber(sumLaborCostAndTime, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0}));	
							}	
						}
                        rightHorzContentDs.push({ id: 'id'+i, sum: sumLaborCostAndTime });
                    }
                    break;
                    
                // 外部予算実績
                case WorkplaceCounterCategory.EXTERNAL_BUDGET: 
                    let externalBudget: Array<any> = self.dataAggrerateWorkplace.externalBudget,
                        externalBudgetValue = _.filter(externalBudget, item => !_.isEmpty(item.externalBudget));
                    if(_.isEmpty(externalBudgetValue)) {
						let objectExternalBudget = { sid: '' };
	                    leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
	                    _.set(objectExternalBudget, 'id', 'id1');
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                        _.set(objectExternalBudget, key, '');
	                    });
	                    horizontalSumContentDs.push(objectExternalBudget);
	                    rightHorzContentDs.push({ id: 'id1', sum: '' });
                        break;
                    }
					let externalBudgetData: any = [];
					_.forEach(externalBudget, externalBudgetItem => {
						_.forEach(externalBudgetItem.externalBudget, externalBudgetSubItem => {
							externalBudgetSubItem.date = externalBudgetItem.date;
							externalBudgetData.push(externalBudgetSubItem);
						});
					});
					_.forEach(_.values(_.groupBy(externalBudgetData, 'code')), (groupItem: Array<any>, index: number) => {
						let objectExternalBudget = { sid: '' }, sumExternalBudget: any = '';
	                    leftHorzContentDs.push({ id: 'id' + index, title: _.get(_.head(groupItem), 'name'), subtitle: '' });
	                    _.set(objectExternalBudget, 'id', 'id' + index);
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                        let findObject: any = _.find(groupItem, item => key==moment(item.date).format('_YYYYMMDD'));
	                        if(!_.isEmpty(findObject)) {
								let value = _.isNumber(findObject.value) ? findObject.value : 
											_.isEmpty(findObject.value) ? null : 
											_.isNaN(_.toNumber(findObject.value)) ? null : _.toNumber(findObject.value);
								if (findObject.budgetAtr==0) {
									_.set(objectExternalBudget, key, _.isNumber(value) ? nts.uk.time.format.byId("Time_Short_HM", value) : '');
								} else if (findObject.budgetAtr==2) {
									_.set(objectExternalBudget, key, _.isNumber(value) ? 
									nts.uk.ntsNumber.formatNumber(value, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0})) : '');	
								} else {
									_.set(objectExternalBudget, key, _.isNumber(value) ? value : '');
								}
								if(_.isNumber(value)) {
									if(_.isNumber(sumExternalBudget)) {
										sumExternalBudget += value;	
									} else {
										sumExternalBudget = value;
									}
								}
	                        } else {
	                            _.set(objectExternalBudget, key, '');   
	                        }
	                    });
	                    horizontalSumContentDs.push(objectExternalBudget);
						if(_.isNumber(sumExternalBudget)) {
							if (_.head(groupItem).budgetAtr==0) {
								sumExternalBudget = nts.uk.time.format.byId("Time_Short_HM", sumExternalBudget);
							} else if (_.head(groupItem).budgetAtr==2) {
								sumExternalBudget = nts.uk.ntsNumber.formatNumber(sumExternalBudget, new nts.uk.ui.option.NumberEditorOption({grouplength: 3, decimallength: 0}));
							}	
						}
						rightHorzContentDs.push({ id: 'id' + index, sum: sumExternalBudget });
					});
                    break;
                    
                // 回数集計
                case WorkplaceCounterCategory.TIMES_COUNTING: 
                    let timeCount: Array<any> = self.dataAggrerateWorkplace.timeCount,
                        timeCountValue = _.filter(timeCount, item => !_.isEmpty(item.timeCount));
                    if(_.isEmpty(timeCountValue)) {
						let objectTimeCount = { sid: '' };
	                    leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
	                    _.set(objectTimeCount, 'id', 'id1');
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                      	_.set(objectTimeCount, key, '');
	                    });
	                    horizontalSumContentDs.push(objectTimeCount);
	                    rightHorzContentDs.push({ id: 'id1', sum: '' });
                        break;
                    }
					let timeCountData: any = [];
					_.forEach(timeCount, timeCountItem => {
						_.forEach(timeCountItem.timeCount, timeCountSubItem => {
							timeCountSubItem.date = timeCountItem.date;
							timeCountData.push(timeCountSubItem);
						});
					});
					_.forEach(_.values(_.groupBy(timeCountData, 'totalCountNo')), (groupItem: Array<any>, index: number) => {
						let objectTimeCount = { sid: '' }, sumTimeCount: any = '';
	                    leftHorzContentDs.push({ id: 'id' + index, title: _.get(_.head(groupItem), 'totalTimesName'), subtitle: '' });
	                    _.set(objectTimeCount, 'id', 'id' + index);
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                        let findObject: any = _.find(groupItem, item => key==moment(item.date).format('_YYYYMMDD'));
	                        if(!_.isEmpty(findObject)) {
								let value = _.isNumber(findObject.value) ? findObject.value : 
											_.isEmpty(findObject.value) ? null : 
											_.isNaN(_.toNumber(findObject.value)) ? null : _.toNumber(findObject.value);
	                            _.set(objectTimeCount, key, _.isNumber(value) ? value : '');
								if(_.isNumber(value)) {
									if(_.isNumber(sumTimeCount)) {
										sumTimeCount += value;	
									} else {
										sumTimeCount = value;
									}
								}
	                        } else {
	                            _.set(objectTimeCount, key, '');    
	                        }
	                    });
	                    horizontalSumContentDs.push(objectTimeCount);
	                    rightHorzContentDs.push({ id: 'id' + index, sum: sumTimeCount });
					});
                    break;
                    
                // 就業時間帯別の利用人数
                case WorkplaceCounterCategory.WORKTIME_PEOPLE: 
                    let peopleMethod: Array<any> = self.dataAggrerateWorkplace.peopleMethod,
                        peopleMethodValue = _.filter(peopleMethod, item => !_.isEmpty(item.peopleMethod)),
                        actualMode =  self.achievementDisplaySelected() == MODE.ACTUAL,
                        idx = self.achievementDisplaySelected() == MODE.ACTUAL?3:2;
                    if(_.isEmpty(peopleMethodValue)) {
						leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
	                    leftHorzContentDs.push({ id: 'id2', title: '', subtitle: '' });
	                    if(actualMode) leftHorzContentDs.push({ id: 'id3', title: '', subtitle: '' });
                        for(let i=1; i<= idx; i++) {
	                        let objectPeopleMethod = { sid: '' };
	                        _.set(objectPeopleMethod, 'id', 'id'+i);
	                        _.forEach(keys, key => {
	                            if(_.includes(['employeeId', 'sid'], key)) {
	                                return; 
	                            }
	                           	_.set(objectPeopleMethod, key, '');
	                        });
	                        horizontalSumContentDs.push(objectPeopleMethod);    
	                        rightHorzContentDs.push({ id: 'id'+i, sum: '' });
	                    }
                        break;
                    }
					let peopleMethodData: any = [];
					_.forEach(peopleMethod, peopleMethodItem => {
						_.forEach(peopleMethodItem.peopleMethod, peopleMethodSubItem => {
							peopleMethodSubItem.date = peopleMethodItem.date;
							peopleMethodSubItem.code = peopleMethodSubItem.workMethod.code;
							peopleMethodSubItem.name = peopleMethodSubItem.workMethod.name;
							peopleMethodData.push(peopleMethodSubItem);
						});
					});
					_.forEach(_.values(_.groupBy(peopleMethodData, 'code')), (groupItem: Array<any>, index: number) => {
						leftHorzContentDs.push({ 
							id: 'id'+(index*3+1), 
							title: _.isEmpty(_.get(_.head(groupItem), 'name')) ? _.get(_.head(groupItem), 'code') + getText("KSU001_22") : _.get(_.head(groupItem), 'name'),
							subtitle: getText("KSU001_70") 
						});
                    	leftHorzContentDs.push({ id: 'id'+(index*3+2), title: '', subtitle: getText("KSU001_71") });
                    	if(actualMode) leftHorzContentDs.push({ id: 'id'+(index*3+3), title: '', subtitle: getText("KSU001_72") });
						for(let i=1; i<= idx; i++) {
	                        let objectPeopleMethod = { sid: '' }, sumPeopleMethod: any = '';
	                        _.set(objectPeopleMethod, 'id', 'id'+(index*3+i));
	                        _.forEach(keys, key => {
	                            if(_.includes(['employeeId', 'sid', 'empName'], key)) {
	                                return; 
	                            }
	                            let findObject: any = _.find(groupItem, item => key==moment(item.date).format('_YYYYMMDD'));
	                            if(!_.isEmpty(findObject)) {
									let value = null;
	                                switch(i) {
	                                    case 1: value = _.get(findObject, 'planNumber');
	                                            break;
	                                    case 2: value = _.get(findObject, 'scheduleNumber');
	                                            break;
	                                    case 3: value = _.get(findObject, 'actualNumber');
	                                            break;
	                                    default: break;
	                                }
									value = _.isNumber(value) ? value : 
											_.isEmpty(value) ? null : 
											_.isNaN(_.toNumber(value)) ? null : _.toNumber(value);
									_.set(objectPeopleMethod, key, _.isNumber(value) ? value : '');
									if(_.isNumber(value)) {
										if(_.isNumber(sumPeopleMethod)) {
											sumPeopleMethod += value;	
										} else {
											sumPeopleMethod = value;
										}
									}
	                            } else {
	                                _.set(objectPeopleMethod, key, ''); 
	                            }
	                        });
	                        horizontalSumContentDs.push(objectPeopleMethod);    
	                        rightHorzContentDs.push({ id: 'id'+(index*3+i), sum: sumPeopleMethod });
	                    }
					});
                    break;
                    
                // 雇用人数
                case WorkplaceCounterCategory.EMPLOYMENT_PEOPLE: 
                    let employment: Array<any> = self.dataAggrerateWorkplace.aggrerateNumberPeople.employment,
                        employmentValue = _.filter(employment, item => !_.isEmpty(item.numberPeople));
                    
                    let listMaxNumberPeopleEmp: Array<any> = [];
                    let lengthEmp = 0;
                    _.forEach(employment, item => {
                        if (item.numberPeople.length > lengthEmp) {
                            lengthEmp = item.numberPeople.length;
                            listMaxNumberPeopleEmp = item.numberPeople;
                        }
                    });
                    
                    if(_.isEmpty(employmentValue)) {
						let objectEmployment = { sid: '' };
	                    leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
	                    _.set(objectEmployment, 'id', 'id1');
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                      	_.set(objectEmployment, key, '');
	                    });
	                    horizontalSumContentDs.push(objectEmployment);
	                    rightHorzContentDs.push({ id: 'id1', sum: '' });
                        break;
                    }
					let employmentData: any = [];
					_.forEach(employment, employmentItem => {
						_.forEach(employmentItem.numberPeople, employmentSubItem => {
							employmentSubItem.date = employmentItem.date;
							employmentData.push(employmentSubItem);
						});
					});
                    
                    let objGroupEmp = _.groupBy(employmentData, 'code');
                    _.forEach(listMaxNumberPeopleEmp, (item, index: number) => {
                        let groupItem: Array<any> = objGroupEmp[item.code];
                        let objectEmployment = { sid: '' }, sumEmployment: any = '';
	                    leftHorzContentDs.push({ 
							id: 'id' + index, 
							title: _.isEmpty(_.get(_.head(groupItem), 'name')) ? _.get(_.head(groupItem), 'code') + getText("KSU001_22") : _.get(_.head(groupItem), 'name'), 
							subtitle: '' 
						});
	                    _.set(objectEmployment, 'id', 'id' + index);
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                        let findObject: any = _.find(groupItem, item => key==moment(item.date).format('_YYYYMMDD'));
	                        if(!_.isEmpty(findObject)) {
								let value = _.isNumber(findObject.value) ? findObject.value : 
											_.isEmpty(findObject.value) ? null : 
											_.isNaN(_.toNumber(findObject.value)) ? null : _.toNumber(findObject.value);
	                            _.set(objectEmployment, key, _.isNumber(value) ? value : '');
								if(_.isNumber(value)) {
									if(_.isNumber(sumEmployment)) {
										sumEmployment += value;	
									} else {
										sumEmployment = value;
									}
								}
	                        } else {
	                            _.set(objectEmployment, key, '');   
	                        }
	                    });
	                    horizontalSumContentDs.push(objectEmployment);
	                    rightHorzContentDs.push({ id: 'id' + index, sum: sumEmployment });
					});
                    break;
                    
                // 分類人数
                case WorkplaceCounterCategory.CLASSIFICATION_PEOPLE: 
                    let classification: Array<any> = self.dataAggrerateWorkplace.aggrerateNumberPeople.classification,
                        classificationValue = _.filter(classification, item => !_.isEmpty(item.numberPeople));
                    
                    let listMaxNumberPeopleCls: Array<any> = [];
                    let lengthCls = 0;
                    _.forEach(classification, item => {
                        if (item.numberPeople.length > lengthCls) {
                            lengthCls = item.numberPeople.length;
                            listMaxNumberPeopleCls = item.numberPeople;
                        }
                    });
                    
                    
                    if(_.isEmpty(classificationValue)) {
						let objectClassification = { sid: '' };
	                    leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
	                    _.set(objectClassification, 'id', 'id1');
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                      	_.set(objectClassification, key, '');
	                    });
	                    horizontalSumContentDs.push(objectClassification);
	                    rightHorzContentDs.push({ id: 'id1', sum: '' });
                        break;
                    }
					let classificationData: any = [];
					_.forEach(classification, classificationItem => {
						_.forEach(classificationItem.numberPeople, classificationSubItem => {
							classificationSubItem.date = classificationItem.date;
							classificationData.push(classificationSubItem);
						});
					});
                    

                    let objGroupCls = _.groupBy(classificationData, 'code');
                    _.forEach(listMaxNumberPeopleCls , (item, index: number) => {
                        let groupItem: Array<any> = objGroupCls[item.code];
                        let objectClassification = { sid: '' }, sumClassification: any = '';
	                    leftHorzContentDs.push({ 
							id: 'id' + index, 
							title: _.isEmpty(_.get(_.head(groupItem), 'name')) ? _.get(_.head(groupItem), 'code') + getText("KSU001_22") : _.get(_.head(groupItem), 'name'), 
							subtitle: '' 
						});
	                    _.set(objectClassification, 'id', 'id' + index);
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                        let findObject: any = _.find(groupItem, item => key==moment(item.date).format('_YYYYMMDD'));
	                        if(!_.isEmpty(findObject)) {
								let value = _.isNumber(findObject.value) ? findObject.value : 
											_.isEmpty(findObject.value) ? null : 
											_.isNaN(_.toNumber(findObject.value)) ? null : _.toNumber(findObject.value);
	                            _.set(objectClassification, key, _.isNumber(value) ? value : '');
								if(_.isNumber(value)) {
									if(_.isNumber(sumClassification)) {
										sumClassification += value;	
									} else {
										sumClassification = value;
									}
								}
	                        } else {
	                            _.set(objectClassification, key, '');   
	                        }
	                    });
	                    horizontalSumContentDs.push(objectClassification);
	                    rightHorzContentDs.push({ id: 'id' + index, sum: sumClassification });
					});
                    break;
                    
                // 職位人数
                case WorkplaceCounterCategory.POSITION_PEOPLE: 
                    let jobTitleInfo: Array<any> = self.dataAggrerateWorkplace.aggrerateNumberPeople.jobTitleInfo,
                        jobTitleInfoValue = _.filter(jobTitleInfo, item => !_.isEmpty(item.numberPeople));
                    
                    let listMaxNumberPeoplePos: Array<any> = [];
                    let lengthPos = 0;
                    _.forEach(jobTitleInfo, item => {
                        if (item.numberPeople.length > lengthPos) {
                            lengthPos = item.numberPeople.length;
                            listMaxNumberPeoplePos = item.numberPeople;
                        }
                    });

                    if(_.isEmpty(jobTitleInfoValue)) {
						let objectJobTitle = { sid: '' };
	                    leftHorzContentDs.push({ id: 'id1', title: '', subtitle: '' });
	                    _.set(objectJobTitle, 'id', 'id1');
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                       	_.set(objectJobTitle, key, '');
	                    });
	                    horizontalSumContentDs.push(objectJobTitle);
	                    rightHorzContentDs.push({ id: 'id1', sum: '' });
                        break;
                    }
					let jobTitleInfoData: any = [];
					_.forEach(jobTitleInfo, jobTitleInfoItem => {
						_.forEach(jobTitleInfoItem.numberPeople, jobTitleInfoSubItem => {
							jobTitleInfoSubItem.date = jobTitleInfoItem.date;
							jobTitleInfoData.push(jobTitleInfoSubItem);
						});
					});
                    
                    let objGroupPos = _.groupBy(jobTitleInfoData, 'code');
					_.forEach(listMaxNumberPeoplePos, (item, index: number) => {
                        let groupItem: Array<any> = objGroupPos[item.code];
						let objectJobTitle = { sid: '' }, sumJobTitleInfo: any = '';
	                    leftHorzContentDs.push({ 
							id: 'id' + index, 
							title: _.isEmpty(_.get(_.head(groupItem), 'name')) ? _.get(_.head(groupItem), 'code') + getText("KSU001_22") : _.get(_.head(groupItem), 'name'), 
							subtitle: '' 
						});
	                    _.set(objectJobTitle, 'id', 'id' + index);
	                    _.forEach(keys, key => {
	                        if(_.includes(['employeeId', 'sid'], key)) {
	                            return; 
	                        }
	                        let findObject: any = _.find(groupItem, item => key==moment(item.date).format('_YYYYMMDD'));
	                        if(!_.isEmpty(findObject)) {
								let value = _.isNumber(findObject.value) ? findObject.value : 
											_.isEmpty(findObject.value) ? null : 
											_.isNaN(_.toNumber(findObject.value)) ? null : _.toNumber(findObject.value);
	                            _.set(objectJobTitle, key, _.isNumber(value) ? value : '');
								if(_.isNumber(value)) {
									if(_.isNumber(sumJobTitleInfo)) {
										sumJobTitleInfo += value;	
									} else {
										sumJobTitleInfo = value;
									}
								}
	                        } else {
	                            _.set(objectJobTitle, key, ''); 
	                        }
	                    });
	                    horizontalSumContentDs.push(objectJobTitle);
	                    rightHorzContentDs.push({ id: 'id' + index, sum: sumJobTitleInfo });
					});
                    break;
                    
                default: break;
            }   
            self.leftHorzContentDs = leftHorzContentDs;
            self.horizontalSumContentDs = horizontalSumContentDs;
            self.rightHorzContentDs = rightHorzContentDs;
        }
        
        createVertSumHeader() {
            let self = this,
                vertSumHeader = {
                columns: self.vertSumColumns,
                width: self.widthVertSum+"px",
                features: [{
                    name: "HeaderRowHeight",
                    rows: { 0: "40px", 1: "20px" }   
                }]
            };
            return vertSumHeader;
        }
        
        createVertSumContent() {
            let self = this,
                vertSumContent = {
                columns: self.vertSumColumns,
                dataSource: self.vertSumContentDs,
                primaryKey: "sid",
                features: [{
                    name: "BodyCellStyle",
                    decorator: self.vertContentDeco
                }]
            };
            return vertSumContent;
        }
        
        createLeftHorzColumns() {
            let leftHorzColumns = [
                { 
                    headerText: '<div id="horzDropDown"></div>', 
                    icon: { for: "header", class: "aaaa", width: "0px"},
                    group: [
                        { headerText: '', key: "title", width: "110px" },
                        { headerText: '', key: "subtitle", width: "90px" }
                    ]
                }
            ];  
            return leftHorzColumns;
        }
        
        createLeftHorzSumHeader() {
            let self =this,
                leftHorzSumHeader = {
                    columns: self.createLeftHorzColumns(),
                    rowHeight: "40px"
                };
            return leftHorzSumHeader;
        }
        
        createLeftHorzSumContent() {
            let self = this,
                leftHorzSumContent = {
                    columns: self.createLeftHorzColumns(),
                    dataSource: self.leftHorzContentDs,
                    primaryKey: "id"
                };  
            return leftHorzSumContent;
        }
        
        createHorizontalSumHeader() {
            let self = this,
                horizontalSumHeader = {
                    columns: self.horizontalDetailColumns,
                    dataSource: [new ExItem(undefined, null, null, null, true, self.arrDay)],
                    rowHeight: "40px",
                    features: [{
                        name: "HeaderCellStyle",
                        decorator: self.detailHeaderDeco
                    }]
                };
            return horizontalSumHeader;
        }
        
        createHorizontalSumContent() {
            let self = this,
                horizontalSumContent = {
                    columns: self.horizontalDetailColumns,
                    dataSource: self.horizontalSumContentDs,
                    primaryKey: "id",
                };  
            return horizontalSumContent;
        }
        
        createRightHorzSumColumns() {
            let rightHorzSumColumns = [{ headerText: "合計", key: "sum", width: "35px" }];
            return rightHorzSumColumns;
        }
        
        createRightHorzSumHeader() {
            let self = this,
                rightHorzSumHeader = {
                    columns: self.createRightHorzSumColumns(),
                    rowHeight: "40px"
                };
            return rightHorzSumHeader;
        }
        createRightHorzSumContent() {
            let self = this,
                rightHorzSumContent = {
                    columns: self.createRightHorzSumColumns(),
                    dataSource: self.rightHorzContentDs,
                    primaryKey: "id"
                };
            return rightHorzSumContent;
        }
        
        // update A11
        updateVertSumGrid() {
            let self = this;
            if (self.showA11()) {
                $("#cacheDiv").append($('#vertDiv'));
                let vertSumHeader = self.createVertSumHeader();
                let vertSumContent = self.createVertSumContent();
                $("#extable").exTable("updateTable", "verticalSummaries", vertSumHeader, vertSumContent);
                $("#vertDropDown").html(function() { return $('#vertDiv'); });
                $('#vertDiv').css('display', '');
                $('#vertDiv').css('margin-left', 0 + 'px');
                
                $('.ex-body-vert-sum').scroll(() => {
                    $('#vertDiv').css('margin-left', $('.ex-body-vert-sum').scrollLeft().valueOf() + 'px');
                });
            }
        }
        
        // update A12
        updateHorzSumGrid() {
            let self = this;
            if (self.showA12()) {
                $("#cacheDiv").append($('#horzDiv'));
                let leftHorzSumHeader = self.createLeftHorzSumHeader();
                let leftHorzSumContent = self.createLeftHorzSumContent();
                let horizontalSumHeader = self.createHorizontalSumHeader();
                let horizontalSumContent = self.createHorizontalSumContent();
                let rightHorzSumHeader = self.createRightHorzSumHeader();
                let rightHorzSumContent = self.createRightHorzSumContent();
                $("#extable").exTable("updateTable", "leftHorizontalSummaries", leftHorzSumHeader, leftHorzSumContent);
                $("#extable").exTable("updateTable", "horizontalSummaries", horizontalSumHeader, horizontalSumContent);
                if(self.showA11()){ // chỉ trường hợp cả A11,A12 show thì mới có cục rightHorizontalSummaries
                     $("#extable").exTable("updateTable", "rightHorizontalSummaries", rightHorzSumHeader, rightHorzSumContent);
                }
                $("#horzDropDown").html(function() { return $('#horzDiv'); });
                $('#horzDiv').css('display', '');
            
                $('.extable-body-left-horz-sum tbody tr td:first-child()').css('border-right', '1px solid transparent');    
                
                self.showA12_2(_.includes([WorkplaceCounterCategory.WORKTIME_PEOPLE, WorkplaceCounterCategory.LABOR_COSTS_AND_TIME], self.useCategoriesWorkplaceValue()) ||
                            (_.includes([WorkplaceCounterCategory.EXTERNAL_BUDGET], self.useCategoriesWorkplaceValue()) && self.funcNo15_WorkPlace));
            }
        }
        
		clickA12_2() {
			const self = this;
			switch(self.useCategoriesWorkplaceValue()) {
				case WorkplaceCounterCategory.LABOR_COSTS_AND_TIME: // 人件費・時間
					setShared('targetR', {
						unit: self.userInfor.unit,
						id: self.userInfor.unit == 0 ? self.userInfor.workplaceId : self.userInfor.workplaceGroupId
					});
					setShared('periodR', {
						startDate: self.dateTimePrev() ,
						endDate: self.dateTimeAfter() 	
					});
					setShared('name', self.targetOrganizationName());
		            nts.uk.ui.windows.sub.modal("/view/ksu/001/r/index.xhtml").onClosed(() => {
						self.getAggregatedInfo(false, true);
					});
					break;
		        case WorkplaceCounterCategory.EXTERNAL_BUDGET: // 外部予算実績
					setShared('target', {
						unit: self.userInfor.unit,
						id: self.userInfor.unit == 0 ? self.userInfor.workplaceId : self.userInfor.workplaceGroupId
					});
					setShared('period', {
						startDate: self.dateTimePrev(),
						endDate: self.dateTimeAfter() 	
					});
					setShared('name', self.targetOrganizationName());
					nts.uk.ui.windows.sub.modal("/view/ksu/001/q/index.xhtml").onClosed(() => {
						self.getAggregatedInfo(false, true);
					});
					break;
//		        TIMES_COUNTING = 2, // 回数集計
//		        WORKTIME_PEOPLE = 3, // 就業時間帯別の利用人数
//		        TIMEZONE_PEOPLE = 4, // 時間帯人数
//		        EMPLOYMENT_PEOPLE = 5, // 雇用人数
//		        CLASSIFICATION_PEOPLE = 6, // 分類人数
//		        POSITION_PEOPLE = 7, // 職位人数		
				default:
					break;
			}	
		}
        
        bindingEventClickFlower() {
            let self = this;
            // 職13
            if(!self.canRegisterEvent)
                return;
            $('.extable-header-detail a').css('width', '30px');
            $('.extable-header-detail img').css('margin-top', '2px');
            $(".header-image-no-event, .header-image-event").unbind('click');
            $(".header-image-no-event, .header-image-event").on("click", function(event) {
                if(self.mode() == UpdateMode.DETERMINE) return;
                let index = $(event.target).parent().index();
                let columnKey = self.detailColumns[index].key;
                let param = {
                    dateSelected: moment(columnKey.slice(1)).format('YYYY/MM/DD'),
                    workplace: {
                        workPlaceID: self.userInfor.workplaceId == null ? self.userInfor.workplaceGroupId : self.userInfor.workplaceId,
                        targetOrgWorkplaceName: self.targetOrganizationName()
                    }
                }
                setShared("KDL049", param);
                nts.uk.ui.windows.sub.modal('/view/kdl/049/a/index.xhtml').onClosed(function(): any {
                    let rs = getShared('DataKDL049');
                    if (!_.isNil(rs)) {
                        let date             = rs.date;
                        let companyEventName = rs.companyEventName;
                        let wkpEventName     = rs.wkpEventName;
                        // update lai header grid
                        self.updateHeader().done(() => {
                            if (self.userInfor.disPlayFormat == ViewMode.TIME) {
                                // enable những cell đã disable trước đó đi rồi sau khi update grid mới disable đi được
                                self.enableCellsTime();
                                self.diseableCellsTime();
                            }
                        });;
                    }
                });
            });
        }
        
        // update grid header after closed dialog kdl049
        updateHeader(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            nts.uk.ui.block.grayout();
            
            let objDetailHeaderDs = {};
            let detailHeaderDeco  = [];
            let htmlToolTip       = [];
            
            let param = {
                startDate: self.dateTimePrev(),
                endDate  : self.dateTimeAfter(),
                wkpId  : self.userInfor.workplaceId,
                wkpGrId: self.userInfor.workplaceGroupId
            };

            service.getEvent(param).done((listDateInfo: any) => {
                objDetailHeaderDs['sid'] = "";
                _.each(listDateInfo, (dateInfo: IDateInfo) => {
                    let time = new Time(new Date(dateInfo.ymd));
                    let ymd = time.yearMonthDay;
                    let field = '_' + ymd;
                    if (dateInfo.isToday) {

                        if (dateInfo.isHoliday) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-sunday"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-sunday"));
                        } else if (dateInfo.dayOfWeek == 7) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-sunday"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-sunday"));
                        } else if (dateInfo.dayOfWeek == 6) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-saturday"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-saturday"));
                        } else if (dateInfo.dayOfWeek > 0 || dateInfo.dayOfWeek < 6) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day-color-schedule-weekdays"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day-color-schedule-weekdays"));
                        } else {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-that-day"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-that-day"));
                        }

                    } else if (dateInfo.isSpecificDay) {

                        if (dateInfo.isHoliday) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-sunday"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-sunday"));
                        } else if (dateInfo.dayOfWeek == 7) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-sunday"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-sunday"));
                        } else if (dateInfo.dayOfWeek == 6) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-saturday"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-saturday"));
                        } else if (dateInfo.dayOfWeek > 0 || dateInfo.dayOfWeek < 6) {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date-color-schedule-weekdays"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date-color-schedule-weekdays"));
                        } else {
                            detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-specific-date"));
                            detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-specific-date"));
                        }


                    } else if (dateInfo.isHoliday) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-sunday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-sunday"));
                    } else if (dateInfo.dayOfWeek == 7) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-sunday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-sunday"));
                    } else if (dateInfo.dayOfWeek == 6) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-schedule-saturday"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-schedule-saturday"));
                    } else if (dateInfo.dayOfWeek > 0 || dateInfo.dayOfWeek < 6) {
                        detailHeaderDeco.push(new CellColor("_" + ymd, 0, "bg-weekdays"));
                        detailHeaderDeco.push(new CellColor("_" + ymd, 1, "bg-weekdays"));
                    }

                    if (dateInfo.htmlTooltip != null) {
                        objDetailHeaderDs['_' + ymd] = "<img class='header-image-event'>";
                        htmlToolTip.push(new HtmlToolTip('_' + ymd, dateInfo.htmlTooltip));
                    } else {
                        objDetailHeaderDs['_' + ymd] = "<img class='header-image-no-event'>";
                    }
                });

                self.updateHeaderExTable(detailHeaderDeco, objDetailHeaderDs, htmlToolTip);
                self.setIconEventHeader();
                self.mode() === UpdateMode.EDIT ? self.editModeAct(true) : self.confirmModeAct(true);
                dfd.resolve();
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }

        // update header grid 
        updateHeaderExTable(detailHeaderDeco : any, objDetailHeaderDs: any, htmlToolTip: any) {
            let self = this;
            // data update Phần Header Detail
            let detailHeaderDeco = detailHeaderDeco;
            let detailHeaderDs   = [];
            let detailColumns    = self.detailColumns;
            let objDetailHeaderDs = objDetailHeaderDs;
            let htmlToolTip = htmlToolTip;

            //create dataSource for detailHeader
            detailHeaderDs.push(new ExItem(undefined, null, null, null, true, self.arrDay));
            detailHeaderDs.push(objDetailHeaderDs);
            let detailHeaderUpdate = {
                columns: detailColumns,
                dataSource: detailHeaderDs,
                features: [{
                    name: "HeaderRowHeight",
                    rows: { 0: "40px", 1: "20px" }
                }, {
                        name: "HeaderCellStyle",
                        decorator: detailHeaderDeco
                    }, {
                        name: "ColumnResizes"
                    }, {
                    }, {
                        name: "Hover",
                        selector: ".header-image-event",
                        enter: function(ui) {
                            if (ui.rowIdx === 1 && $(ui.target).is(".header-image-event")) {
                                let objTooltip = _.filter(htmlToolTip, function(o) { return o.key == ui.columnKey; });
                                if (objTooltip.length > 0) {
                                    let heightToolTip = objTooltip[0].heightToolTip;
                                    ui.tooltip("show", $("<div/>").css({ width: "max-content", height: "max-content" }).html(objTooltip[0].value));
                                } else {
                                    ui.tooltip("show", $("<div/>").css({ width: "60px", height: 60 + "px" }).html(''));
                                }
                            }
                        },
                        exit: function(ui) {
                            ui.tooltip("hide");
                        }
                    }, {
                        name: "Click",
                        handler: function(ui) {
                            console.log(`${ui.rowIdx}-${ui.columnKey}`);
                        }
                    }]
            };

            $("#extable").exTable("updateTable", "detail", detailHeaderUpdate, {});
        }

        updateExTableAfterSortEmp(dataBindGrid: any, viewMode : string ,updateMode: string, updateLeftMost : boolean, updateMiddle : boolean, updateDetail : boolean): void {
            let self = this;
            nts.uk.ui.block.grayout();
            // update phan leftMost
            let leftmostDs = dataBindGrid.leftmostDs;
            let leftmostColumns = [{
                key: "codeNameOfEmp", headerText: getText("KSU001_205"), width: self.widthA8+"px", icon: { for: "body", class: "icon-leftmost", width: "25px" },
                css: { whiteSpace: "pre" }, control: "link", handler: function(rData, rowIdx, key) { console.log(rowIdx); },
                headerControl: "link", headerHandler: function() { alert("Link!"); }
            }];

            let leftmostContentUpdate = {
                columns: leftmostColumns,
                dataSource: leftmostDs,
                primaryKey: "sid"
            };
            
            // update Phần Middle
            let middleDs = dataBindGrid.middleDs;
            let middleColumns = [];
            let middleContentDeco = [];
            let middleHeader = {};
            let middleContentUpdate = {};

            if (self.showA9) {
                middleColumns = [];
                if (self.showTeamCol) {
                    middleColumns.push({ headerText: getText("KSU001_4023"), key: "team", width: "40px", css: { whiteSpace: "none" } });
                }
                if (self.showRankCol) {
                    middleColumns.push({ headerText: getText("KSU001_4024"), key: "rank", width: "40px", css: { whiteSpace: "none" } });
                }
                if (self.showQualificCol) {
                    middleColumns.push({ headerText: getText("KSU001_4025"), key: "qualification", width: "40px", css: { whiteSpace: "none" } });
                }
            }

            middleContentUpdate = {
                columns: middleColumns,
                dataSource: middleDs,
                primaryKey: "sid",
                features: [{
                    name: "BodyCellStyle",
                    decorator: middleContentDeco
                }]
            };
             
            // update Phần Detail
            let detailHeaderDeco = dataBindGrid.detailHeaderDeco;
            let detailHeaderDs = [];
            let detailContentDeco = dataBindGrid.detailContentDeco;
            let detailContentDs = dataBindGrid.detailContentDs;
            let detailColumns = dataBindGrid.detailColumns;
            let objDetailHeaderDs = dataBindGrid.objDetailHeaderDs;
            let htmlToolTip = dataBindGrid.htmlToolTip;
            let timeRanges = [];

            //create dataSource for detailHeader
            detailHeaderDs.push(new ExItem(undefined, null, null, null, true, self.arrDay));
            detailHeaderDs.push(objDetailHeaderDs);
            let detailHeaderUpdate = {
                columns: detailColumns,
                dataSource: detailHeaderDs,
                features: [{
                    name: "HeaderRowHeight",
                    rows: { 0: "40px", 1: "20px" }
                }, {
                        name: "HeaderCellStyle",
                        decorator: detailHeaderDeco
                    }, {
                        name: "ColumnResizes"
                    }, {
                    }, {
                        name: "Hover",
                        selector: ".header-image-event",
                        enter: function(ui) {
                            if (ui.rowIdx === 1 && $(ui.target).is(".header-image-event")) {
                                let objTooltip = _.filter(htmlToolTip, function(o) { return o.key == ui.columnKey; });
                                if (objTooltip.length > 0) {
                                    let heightToolTip = objTooltip[0].heightToolTip;
                                    ui.tooltip("show", $("<div/>").css({ width: "315px", height: heightToolTip + "px" }).html(objTooltip[0].value));
                                } else {
                                    ui.tooltip("show", $("<div/>").css({ width: "60px", height: 60 + "px" }).html(''));
                                }
                            }
                        },
                        exit: function(ui) {
                            ui.tooltip("hide");
                        }
                    }, {
                        name: "Click",
                        handler: function(ui) {
                            console.log(`${ui.rowIdx}-${ui.columnKey}`);
                        }
                    }]
            };
            
            let detailContentUpdate = {
                columns: detailColumns,
                dataSource: detailContentDs,
                primaryKey: "sid",
                //        highlight: false,
                features: [{
                    name: "BodyCellStyle",
                    decorator: detailContentDeco
                }, {
                        name: "TimeRange",
                        ranges: timeRanges
                    }],
                view: function(mode) {
                    switch (mode) {
                        case "shift":
                            return ["shiftName"];
                        case "shortName":
                            return ["workTypeName", "workTimeName"];
                        case "time":
                            return ["workTypeName", "workTimeName", "startTime", "endTime"];
                    }
                },
                fields: ["workTypeCode", "workTypeName", "workTimeCode", "workTimeName", "shiftName", "startTime", "endTime", "shiftCode", "confirmed", "achievements", "needToWork","supportCategory","condTargetdate"],
            };

            if (updateLeftMost) {
                $("#extable").exTable("updateTable", "leftmost", {}, leftmostContentUpdate);
            }
            if (updateMiddle) {
                if (self.showA9) {
                    $("#extable").exTable("updateTable", "middle", {}, middleContentUpdate);
                }
            }
            
            $("#extable").exTable("mode", viewMode, updateMode, null, [{
                    name: "BodyCellStyle",
                    decorator: detailContentDeco
            }]);
            
            if (updateDetail) {
                $("#extable").exTable("updateTable", "detail", detailHeaderUpdate, detailContentUpdate);
            }
        }
        
        setStyler() {
            let self = this;
            // get listShiftMaster luu trong localStorage
            let listShiftMasterSaveLocal = self.userInfor.shiftMasterWithWorkStyleLst;

            // set color for cell
            $("#extable").exTable("stickStyler", function(rowIdx, key, innerIdx, data, stickOrigData) {
                if (self.userInfor.disPlayFormat == ViewMode.SHIFT) {
                    let modeBackGround = self.backgroundColorSelected(); // 0||1
                    let shiftCode;
                    if (_.isNil(stickOrigData)) {
                        shiftCode = data.shiftCode;
                    } else {
                        shiftCode = stickOrigData.shiftCode == "" ? data.shiftCode : stickOrigData.shiftCode;
                    }
                    let workInfo = _.filter(listShiftMasterSaveLocal, function(o) { return o.shiftMasterCode === shiftCode; });
                    if (workInfo.length > 0) {
                        let workStyle = workInfo[0].workStyle;
                        if (workStyle == WorkStyle.ONE_DAY_WORK + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#0000ff", background: "#" + workInfo[0].color }; // color-attendance
                            } else {
                                return { textColor: "#0000ff" }; // color-attendance
                            }
                        }
                        if (workStyle == WorkStyle.MORNING_WORK + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#FF7F27", background: "#" + workInfo[0].color };// color-half-day-work
                            } else {
                                return { textColor: "#FF7F27" };// color-half-day-work
                            }
                        }
                        if (workStyle == WorkStyle.AFTERNOON_WORK + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#FF7F27", background: "#" + workInfo[0].color };// color-half-day-work
                            } else {
                                return { textColor: "#FF7F27" };// color-half-day-work
                            }
                        }
                        if (workStyle == WorkStyle.ONE_DAY_REST + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#ff0000", background: "#" + workInfo[0].color };// color-holiday
                            } else {
                                return { textColor: "#ff0000" };// color-holiday
                            }

                        }
                        if (nts.uk.util.isNullOrUndefined(workStyle) || nts.uk.util.isNullOrEmpty(workStyle)) {
                            if (modeBackGround == 1) {
                                return { textColor: self.timeColor, background: "#ffffff" } // デフォルト（黒）  Default (black)
                            } else {
                                return { textColor: self.timeColor }
                            }
                        }
                    }
                } else if (self.userInfor.disPlayFormat == ViewMode.TIME) {
                    let workStyle = data.workHolidayCls;
                    if (!_.isNil(data)) {
                        // case coppy từ cell là ngày lễ, ngày nghỉ nên phải disable cell starttime, endtime đi.
                        if (workStyle == WorkStyle.ONE_DAY_REST) {
                            self.diseableCellStartEndTime(rowIdx + '', key);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', key);
                        }
                        if (workStyle == WorkStyle.ONE_DAY_WORK) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#0000ff" }; // color-attendance
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (workStyle == WorkStyle.MORNING_WORK) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (workStyle == WorkStyle.AFTERNOON_WORK) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (workStyle == WorkStyle.ONE_DAY_REST) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#ff0000" }; // color-holiday
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (nts.uk.util.isNullOrUndefined(workStyle) || nts.uk.util.isNullOrEmpty(workStyle)) {
                            // デフォルト（黒）  Default (black)
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: self.timeColor }; // デフォルト（黒）  Default (black)
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                    }
                } else if (self.userInfor.disPlayFormat == ViewMode.SHORTNAME) {
                    if (!_.isNil(data)) {
                        let workStyle = data.workHolidayCls;
                        if (workStyle == WorkStyle.ONE_DAY_WORK) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#0000ff" }; // color-attendance
                        }
                        if (workStyle == WorkStyle.MORNING_WORK) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                        }
                        if (workStyle == WorkStyle.AFTERNOON_WORK) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                        }
                        if (workStyle == WorkStyle.ONE_DAY_REST) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#ff0000" }; // color-holiday
                        }
                        if (nts.uk.util.isNullOrUndefined(workStyle) || nts.uk.util.isNullOrEmpty(workStyle)) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: self.timeColor }; // デフォルト（黒）  Default (black)
                        }
                    }
                }
            });
        }
        
        // update detail a10
        updateBodyDetailGrid(viewMode, updateMode): void {
            let self = this;
            // update Phần Detail
            let detailContentDeco = [];
            if (viewMode == ViewMode.SHIFT) {
                if (updateMode == UpdateMode.DETERMINE) {
                    detailContentDeco =  self.detailContentDecoModeConfirmNormal;
                } else {
                    detailContentDeco = self.userInfor.backgroundColor == BackGroundShiftMode.NORMAL ? self.detailContentDecoNormal : self.detailContentDecoShift;
                }
            } else {
                detailContentDeco = updateMode == UpdateMode.DETERMINE ? self.detailContentDecoModeConfirm : self.detailContentDeco;
            }
            
            let detailContentDs = self.detailContentDs;
            let detailColumns = self.detailColumns;

            let detailContentUpdate = {
                columns: detailColumns,
                dataSource: detailContentDs,
                primaryKey: "sid",
                //        highlight: false,
                features: [{
                    name: "BodyCellStyle",
                    decorator: detailContentDeco
                }, {
                        name: "TimeRange",
                        ranges: []
                    }],
                view: function(mode) {
                    switch (mode) {
                        case "shift":
                            return ["shiftName"];
                        case "shortName":
                            return ["workTypeName", "workTimeName"];
                        case "time":
                            return ["workTypeName", "workTimeName", "startTime", "endTime"];
                    }
                },
                fields: ["workTypeCode","workTypeName","workTimeCode","workTimeName","shiftName","startTime", "endTime","shiftCode","confirmed","achievements","needToWork","supportCategory","condTargetdate"],
            };

            $("#extable").exTable("mode", viewMode, updateMode, null, [{
                name: "BodyCellStyle",
                decorator: detailContentDeco
            }]);

            $("#extable").exTable("updateTable", "detail", null, detailContentUpdate);

            self.setStyler();
        }

        // save setting hight cua grid vao localStorage
        saveHeightGridToLocal() {
            let self = this;
            $('#input-heightExtable').trigger("validate");
            if (nts.uk.ui.errors.hasError())
                return;
            // nếu đang ở mode dynamic rồi, thì không return luôn.
            if (self.userInfor.gridHeightSelection == TypeHeightExTable.DEFAULT && self.selectedTypeHeightExTable() == TypeHeightExTable.DEFAULT) {
                $('#A16').ntsPopup('hide');
                return;
            }
            
            if (!_.isNil(self.userInfor)) {
                self.userInfor.gridHeightSelection = self.selectedTypeHeightExTable();
                if (self.selectedTypeHeightExTable() == TypeHeightExTable.DEFAULT) { // mode dynamic
                    self.userInfor.heightGridSetting = '';
                    setTimeout(() => {
                        self.updateGridHeightMode(BodyHeightSettingMode.DYNAMIC, null);
                    }, 1);
                    $('#A16').ntsPopup('hide');
                } else if (self.selectedTypeHeightExTable() == TypeHeightExTable.SETTING) { // mode fix
                    self.userInfor.heightGridSetting = self.heightGridSetting();
                    setTimeout(() => {
                        self.updateGridHeightMode(BodyHeightSettingMode.FIXED, self.heightGridSetting());
                    }, 1);
                    $('#A16').ntsPopup('hide');
                }
                characteristics.save(self.KEY, self.userInfor);
            };
        }

        updateGridHeightMode(mode, height) {
            let self = this;
            if (mode == BodyHeightSettingMode.DYNAMIC) {
                if(document.getElementById('main-area').scrollTop != 0){
                    document.getElementById('main-area').scrollTop=0;
                }
                let height_ex_body_detail = window.innerHeight - document.getElementsByClassName('ex-body-detail')[0].getBoundingClientRect().top - 33.6;
                if(self.showA12()){
                    let hieghtA12_header = document.getElementsByClassName('ex-header-left-horz-sum')[0].offsetHeight;
                    let hieghtA12_body   = document.getElementsByClassName('ex-body-left-horz-sum')[0].offsetHeight;
                    height_ex_body_detail = height_ex_body_detail - hieghtA12_header - hieghtA12_body - 10;
                }
                
                $("#extable").exTable("setHeight", height_ex_body_detail);
                $("#extable").exTable("gridHeightMode", BodyHeightSettingMode.DYNAMIC);
            } else {
                $("#extable").exTable("setHeight", height);
                $("#extable").exTable("gridHeightMode", BodyHeightSettingMode.FIXED);
            }
            self.setPositionButonA15();
            self.setHeightMainArea();
        }

        setPositionButonA15() {
            let self = this;
            let height_ex_header_leftmost = document.getElementsByClassName('ex-header-leftmost')[0].offsetHeight;
            let height_ex_body_leftmost = document.getElementsByClassName('ex-body-leftmost')[0].offsetHeight;
            let heightBtn = 30;
            let top = height_ex_header_leftmost + height_ex_body_leftmost - heightBtn;
            $(".toDown").css({ "margin-top": top + 'px' });
        }

        // xử lý cho button A13
        toLeft() {
            let self = this;
            if (!self.showA9)
                return;
            let offsetLeftGrid = document.getElementById('extable').offsetLeft;
            let offsetWidthA8 = document.getElementsByClassName('extable-header-leftmost')[0].offsetWidth;
            if (self.indexBtnToLeft % 2 == 0) {
                $("#extable").exTable("hideMiddle");
                $('.iconToLeft').css('background-image', 'url(' + self.pathToRight + ')');
                $(".toLeft").css("margin-left", offsetLeftGrid + offsetWidthA8 + "px");
            } else {
                $("#extable").exTable("showMiddle");
                $('.iconToLeft').css('background-image', 'url(' + self.pathToLeft + ')');
                let offsetWidthA9 = document.getElementsByClassName('ex-header-middle')[0].offsetWidth;
                $(".toLeft").css("margin-left", offsetLeftGrid + offsetWidthA8 + offsetWidthA9 + 'px');
            }
            if (!_.isNil(document.getElementById('A14'))) {
                let offsetWidthA14 = document.getElementsByClassName('ex-header-detail')[0].offsetWidth;
                $(".toRight").css('margin-left', offsetWidthA14 - self.widthBtnToLeftToRight * 2 + 'px');
            }
            self.indexBtnToLeft = self.indexBtnToLeft + 1;
        }

        toRight() {
            let self = this;
            if (self.showA11() == false)
                return;
            let marginleft = 0;
            let offsetLeftA14 = document.getElementById('A14').offsetLeft;
            if (self.indexBtnToRight % 2 == 0) {
                $('.iconToRight').css('background-image', 'url(' + self.pathToLeft + ')');
                $("#extable").exTable("hideVerticalSummary");
            } else {
                $('.iconToRight').css('background-image', 'url(' + self.pathToRight + ')');
                $("#extable").exTable("showVerticalSummary");
            }

            let offsetWidthA10 = document.getElementsByClassName('ex-header-detail')[0].offsetWidth;
            if (self.showA9) {
                $(".toRight").css('margin-left', offsetWidthA10 - self.widthBtnToLeftToRight * 2 + 'px');
            } else {
                let offsetWidthA8 = document.getElementsByClassName('ex-header-leftmost')[0].offsetWidth;
                let offsetLeftGrid = document.getElementById('extable').offsetLeft;
                $(".toRight").css('margin-left', offsetLeftGrid + offsetWidthA8 + offsetWidthA10 - self.widthBtnToLeftToRight + 'px');
            }
            self.indexBtnToRight = self.indexBtnToRight + 1;
        }

        toDown() {
            let self = this;
            if (!self.showA12())
                return;
            let heightHozSum = 200 + 30 + 2; // 200 laf height cua HozSum, 30 la khoang cach A8 va A12
            let heightBtn = 30;
            if (self.indexBtnToDown % 2 == 0) {
                $("#extable").exTable("hideHorizontalSummary");
                $('.iconToDown').css('background-image', 'url(' + self.pathToUp + ')');

                let heightEtbl = $("#extable").height();
                let margintop = heightEtbl - heightBtn - 24;
                $(".toDown").css({ "margin-top": margintop + 'px' });
            } else {
                $("#extable").exTable("showHorizontalSummary");
                $('.iconToDown').css('background-image', 'url(' + self.pathToDown + ')');
                
                let heightEtbl = $("#extable").height();
                let margintop = heightEtbl - heightHozSum  - heightBtn;
                $(".toDown").css({ "margin-top": margintop + 'px' });
            }
            self.indexBtnToDown = self.indexBtnToDown + 1;
        }
        
        setPositionButonA13A14A15() {
            let self = this;
            let offsetLeftGrid = document.getElementById('extable').offsetLeft;
            let offsetWidthA8 = document.getElementsByClassName('extable-header-leftmost')[0].offsetWidth;
            if (self.showA9) {
                self.indexBtnToLeft = 0;
                let offsetWidthA9 = document.getElementsByClassName('ex-header-middle')[0].offsetWidth;
                $(".toLeft").css("margin-left", offsetLeftGrid + offsetWidthA8 + offsetWidthA9 + 'px');
                $('.iconToLeft').css('background-image', 'url(' + self.pathToLeft + ')');
            }

            if (self.showA11()) {
                self.indexBtnToRight = 0;
                $('.iconToRight').css('background-image', 'url(' + self.pathToRight + ')');
                let offsetWidthA10 = document.getElementsByClassName('ex-header-detail')[0].offsetWidth;
                if (self.showA9) {
                    $(".toRight").css('margin-left', offsetWidthA10 - self.widthBtnToLeftToRight * 2 + 'px');
                } else {
                    $(".toRight").css('margin-left', offsetLeftGrid + offsetWidthA8 + offsetWidthA10 - self.widthBtnToLeftToRight + 'px');
                }
            }

            if (self.showA12()) {
                self.indexBtnToDown = 0;
                let height_ex_header_leftmost = document.getElementsByClassName('ex-header-leftmost')[0].offsetHeight;
                let height_ex_body_leftmost = document.getElementsByClassName('ex-body-leftmost')[0].offsetHeight;
                let heightBtn = 30;
                let top = height_ex_header_leftmost + height_ex_body_leftmost - heightBtn;
                $(".toDown").css("margin-top" , top + 'px');
                $('.iconToDown').css('background-image', 'url(' + self.pathToDown + ')');
            }
        }
        
        setHeightMainArea() {
            let self = this;
            $("#main-area").css('height', window.innerHeight - 96 + 'px');
            if (self.userInfor.gridHeightSelection == TypeHeightExTable.DEFAULT) {
                $("#main-area").css('overflow-y', 'hidden');
            } else {
                $("#main-area").css('overflow-y', 'scroll');
            }
            $("#sub-content-main").width($('#extable').width() + 30);
        }
        
        // call khi resize
        setPositionButonA14() {
            let self = this;
            let marginleftOfbtnToRight: number = 0;
            let offsetLeftGrid = document.getElementById('extable').offsetLeft;
            let widthA8 = parseInt(document.getElementsByClassName('ex-header-leftmost')[0].style.width);
            let widthA10 = parseInt(document.getElementsByClassName('ex-header-detail')[0].style.width);
            if (self.showA9) {
                marginleftOfbtnToRight = widthA10 - self.widthBtnToLeftToRight*2; 
            } else {
                marginleftOfbtnToRight = offsetLeftGrid + widthA8 + widthA10 - self.widthBtnToLeftToRight;
            }
            $('.toRight').css('margin-left', marginleftOfbtnToRight + 'px');
        }
        
        /**
        * next a month
        */
        nextMonth(): void {
            let self = this;
            nts.uk.ui.block.grayout();
            
            let param = {
                viewMode : self.selectedModeDisplayInBody(), // time | shortName | shift
                startDate: self.dateTimePrev(),
                endDate  : self.dateTimeAfter(),
                isNextMonth : true,
                cycle28Day : self. selectedDisplayPeriod() == 2 ? true : false,
                workplaceId     : self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                unit:             self.userInfor.unit,
                getActualData   : !_.isNil(self.userInfor) ? self.userInfor.achievementDisplaySelected : false, 
                listShiftMasterNotNeedGetNew: self.userInfor.shiftMasterWithWorkStyleLst, 
                sids: self.listSid(),
                modePeriod : self. selectedDisplayPeriod(),
                day: self.closeDate.day, 
                isLastDay: self.closeDate.lastDay,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue() // A12_1
            };
            
            service.getDataChangeMonth(param).done((data: any) => {
                if (self.userInfor.disPlayFormat == ViewMode.SHIFT) {
                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                }
                self.saveDataGrid(data);
                self.dtPrev(data.dataBasicDto.startDate);
                self.dtAft(data.dataBasicDto.endDate);
                
                let dataGrid: any = {
                    listDateInfo: data.listDateInfo,
                    listEmpInfo: data.listEmpInfo,
                    displayControlPersonalCond: data.displayControlPersonalCond,
                    listPersonalConditions: data.listPersonalConditions,
                    listWorkScheduleWorkInfor: data.listWorkScheduleWorkInfor,
                    listWorkScheduleShift: data.listWorkScheduleShift,
                    aggreratePersonal: data.aggreratePersonal,
                    aggrerateWorkplace: data.aggrerateWorkplace
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());
                
                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);
                
                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }
                
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
        }

        /**
        * come back a month
        */
        backMonth(): void {
            let self = this;
            nts.uk.ui.block.grayout();

            let param = {
                viewMode: self.selectedModeDisplayInBody(), // time | shortName | shift
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                isNextMonth: false,
                cycle28Day: self.selectedDisplayPeriod() == 2 ? true : false,
                workplaceId: self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                unit: self.userInfor.unit,
                getActualData: !_.isNil(self.userInfor) ? self.userInfor.achievementDisplaySelected : false,
                listShiftMasterNotNeedGetNew: self.userInfor.shiftMasterWithWorkStyleLst,
                sids: self.listSid(),
                modePeriod: self.selectedDisplayPeriod(),
                day: self.closeDate.day,
                isLastDay: self.closeDate.lastDay,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue() // A12_1
            };

            service.getDataChangeMonth(param).done((data: any) => {
                if (self.userInfor.disPlayFormat == ViewMode.SHIFT) {
                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                }
                
                self.saveDataGrid(data);
                self.dtPrev(data.dataBasicDto.startDate);
                self.dtAft(data.dataBasicDto.endDate);

                let dataGrid: any = {
                    listDateInfo: data.listDateInfo,
                    listEmpInfo: data.listEmpInfo,
                    displayControlPersonalCond: data.displayControlPersonalCond,
                    listPersonalConditions: data.listPersonalConditions,
                    listWorkScheduleWorkInfor: data.listWorkScheduleWorkInfor,
                    listWorkScheduleShift: data.listWorkScheduleShift,
                    aggreratePersonal: data.aggreratePersonal,
                    aggrerateWorkplace: data.aggrerateWorkplace
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());

                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);
                
                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }
                
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
        }
        
        editMode() {
            let self = this;
            if (self.mode() == UpdateMode.EDIT)
                return;
            if (self.getCellsConfirmReg().length > 0) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_1732" }).ifYes(() => {
                    self.editModeAct(true);
                }).ifNo(() => {$("#A6_2").focus()});
            } else {
                self.editModeAct(true);
            }
        }
        
        editModeAct(needUpdate: boolean) {
            let self = this;
            let dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            service.changemode().done(() => {
                self.mode(UpdateMode.EDIT);
                // set color button
                $(".editMode").addClass("A6_hover btnControlSelected").removeClass("A6_not_hover btnControlUnSelected");
                $(".confirmMode").addClass("A6_not_hover btnControlUnSelected").removeClass("A6_hover btnControlSelected");
                self.removeClass();

                // set enable btn A7_1, A7_2, A7_3, A7_4, A7_5
                self.enableBtnReg(false);
                self.enableBtnPaste(true);
                self.enableBtnCoppy(true);
                self.enableHelpBtn(true);
                if (needUpdate) {
                    self.updateDataBindGridBase();
                    self.updateBodyDetailGrid(self.selectedModeDisplayInBody(), self.userInfor.updateMode);
                }

                self.setUpdateMode();

                self.setIconEventHeader();

                $('div.ex-body-leftmost a').css("pointer-events", "");
                $('div.ex-header-detail.xheader a').css("pointer-events", "");

                if (self.selectedModeDisplayInBody() == ViewMode.TIME || self.selectedModeDisplayInBody() == ViewMode.SHORTNAME) {
                    // enable combobox workType, workTime
                    __viewContext.viewModel.viewAB.enableListWorkType(true);

                    let wTypeCdSelected = __viewContext.viewModel.viewAB.selectedWorkTypeCode();
                    let objWtime = _.filter(__viewContext.viewModel.viewAB.listWorkType(), function(o) { return o.workTypeCode == wTypeCdSelected; });
                    if (objWtime[0].workTimeSetting != 2) {
                        __viewContext.viewModel.viewAB.disabled(false);
                    }
                    if (self.selectedModeDisplayInBody() == ViewMode.TIME) {
                        self.visibleBtnInput(true);
                        self.enableBtnInput(true);
                    }
                } else {
                    self.visibleBtnInput(false);
                    self.enableBtnInput(false);
                    self.shiftPalletControlEnable();
                }

                if (self.selectedModeDisplayInBody() == ViewMode.TIME) {
                    self.diseableCellsTime();
                }
                self.calculateDisPlayA48A49();
                dfd.resolve();
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }
        
        confirmMode() {
            let self = this;
            if (self.mode() == UpdateMode.DETERMINE)
                return;
            let arrCellUpdated = [];
            if (self.userInfor.disPlayFormat == ViewMode.SHIFT && self.hasChangeModeBg){
                arrCellUpdated = self.listCellUpdatedWhenChangeModeBg;  // list cell update ở mode shift (khi có chuyển backGroundMode)
            } else  {
                arrCellUpdated = $("#extable").exTable("updatedCells"); // list cell update ở mode time & shortName
            }
            
            if (arrCellUpdated.length > 0) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_1732" }).ifYes(() => {
                    self.confirmModeAct(true);
                    nts.uk.ui.block.clear();
                }).ifNo(() => {$("#A6_1").focus()});
            } else {
                self.confirmModeAct(true);
            }
        }
        
        confirmModeAct(needUpdate: boolean) {
            let self = this;
            let dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            service.changemode().done(() => {
                self.mode(UpdateMode.DETERMINE);
                $(".confirmMode").addClass("btnControlSelected").removeClass("btnControlUnSelected");
                $(".editMode").addClass("btnControlUnSelected").removeClass("btnControlSelected");
                // set enable btn A7_1, A7_2,, A7_3, A7_4, A7_5
                self.enableBtnPaste(false);
                self.enableBtnCoppy(false);
                self.enableHelpBtn(false);
                self.enableBtnRedo(false);
                self.enableBtnUndo(false);
                self.enableBtnReg(false);
                if (self.selectedModeDisplayInBody() == ViewMode.TIME || self.selectedModeDisplayInBody() == ViewMode.SHORTNAME) {
                    // disable combobox workType, workTime
                    __viewContext.viewModel.viewAB.disabled(true);
                    __viewContext.viewModel.viewAB.enableListWorkType(false);
                    if (self.selectedModeDisplayInBody() == ViewMode.TIME) {
                        self.enableCellsTime();
                        self.visibleBtnInput(true);
                        self.enableBtnInput(false);
                    }
                } else {
                    self.visibleBtnInput(false);
                    self.enableBtnInput(false);
                    self.shiftPalletControlDisable();
                    self.listCellUpdatedWhenChangeModeBg = [];
                    self.hasChangeModeBg = false;
                }

                if (needUpdate) {
                    self.updateDataBindGridBase();
                    self.updateBodyDetailGrid(self.selectedModeDisplayInBody(), UpdateMode.DETERMINE);
                }

                $("#extable").exTable("updateMode", UpdateMode.DETERMINE);

                self.setConfirmCells();

                $('div.ex-body-leftmost a').css("pointer-events", "none");
                $('div.ex-header-detail.xheader a').css("pointer-events", "none");
                self.setIconEventHeader();
                self.calculateDisPlayA48A49();
                dfd.resolve();
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }
        
        updateDataBindGrid() {
            let self = this;
            let dataGrid: any = {
                listDateInfo: self.listDateInfo,
                listEmpInfo: self.listEmpInfo,
                displayControlPersonalCond: self.displayControlPersonalCond,
                listPersonalConditions: self.listPersonalConditions,
                listWorkScheduleWorkInfor: self.listWorkScheduleWorkInfor,
                listWorkScheduleShift: self.listWorkScheduleShift,
                aggreratePersonal: self.dataAggreratePersonal,
                aggrerateWorkplace: self.dataAggrerateWorkplace
            }
            self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
        }
        
        updateDataBindGridBase() {
            let self = this;
            let dataGrid: any = {
                listDateInfo: self.listDateInfo,
                listEmpInfo: self.listEmpInfo,
                displayControlPersonalCond: self.displayControlPersonalCond,
                listPersonalConditions: self.listPersonalConditions,
                listWorkScheduleWorkInfor: self.listWorkScheduleWorkInforBase,
                listWorkScheduleShift: self.listWorkScheduleShiftBase,
                aggreratePersonal: self.dataAggreratePersonal,
                aggrerateWorkplace: self.dataAggrerateWorkplace
            }
            self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
        }
        
        shiftPalletControlEnable() {
            let self = this;
            self.checkEnabDisableTblBtn();
            $('.listLink a').css("pointer-events", "");
            __viewContext.viewModel.viewAC.enableSwitchBtn(true);
            __viewContext.viewModel.viewAC.enableCheckBoxOverwrite(true);
            __viewContext.viewModel.viewAC.enableBtnOpenDialogJB1(true);
        }
        
        shiftPalletControlDisable() {
            let self = this;
            if (__viewContext.viewModel.viewAC.selectedpalletUnit() == 1) { // 1 : mode company , 2: mode workPlace
                $('#tableButton1 button').addClass('disabledShiftControl');
            } else {
                $('#tableButton2 button').addClass('disabledShiftControl');
            }
            $('.listLink a').css("pointer-events", "none");
            __viewContext.viewModel.viewAC.enableSwitchBtn(false);
            __viewContext.viewModel.viewAC.enableCheckBoxOverwrite(false);
            __viewContext.viewModel.viewAC.enableBtnOpenDialogJB1(false);
        }
        
        checkEnabDisableTblBtn() {
            if (__viewContext.viewModel.viewAC.selectedpalletUnit() == 1) { // 1 : mode company , 2: mode workPlace
                $('#tableButton1 button').removeClass('disabledShiftControl');
            } else {
                $('#tableButton2 button').removeClass('disabledShiftControl');
            }
        }
        
        removeClass() {
            let self = this;
            $("#paste").addClass("A6_not_hover").removeClass("A6_hover btnControlUnSelected btnControlSelected");
            $("#coppy").addClass("A6_not_hover").removeClass("A6_hover btnControlUnSelected btnControlSelected");
            $("#input").addClass("A6_not_hover").removeClass("A6_not_hover btnControlUnSelected btnControlSelected");
        }
        
        // set cell confirm
        setConfirmCells() {
            let self = this;
            _.forEach(self.listLockCells, function(cell: CellConfirm) {
                $("#extable").exTable('lockCell', cell.rowIndex + '', cell.columnKey + '');
            });
        }
        
        // dis những cell mà không có worktime.
        diseableCellsTime() {
            let self = this;
            _.forEach(self.listTimeDisable, function(obj: TimeDisable) {
                $("#extable").exTable('disableCell', 'detail', obj.rowId + '', obj.columnId + '', '2');
                $("#extable").exTable('disableCell', 'detail', obj.rowId + '', obj.columnId + '', '3');
            });
        }

        // dis những cell mà không có worktime.
        enableCellsTime() {
            let self = this;
            _.forEach(self.listTimeDisable, function(obj: TimeDisable) {
                $("#extable").exTable('enableCell', 'detail', obj.rowId + '', obj.columnId + '', '2');
                $("#extable").exTable('enableCell', 'detail', obj.rowId + '', obj.columnId + '', '3');
            });
        }
        
        // dis những cell mà không có worktime.
        diseableCellStartEndTime(rowIdx, key) {
            let self = this;
            let exit = _.filter(self.listTimeDisable, function(o: TimeDisable) { return o.rowId == rowIdx + '' && o.columnId == key + '' });
            if (exit.length == 0) {
                $("#extable").exTable('disableCell', 'detail', rowIdx + '', key + '', '2');
                $("#extable").exTable('disableCell', 'detail', rowIdx + '', key + '', '3');
                // them cell vừa bị disable vào list
                self.listTimeDisable.push(new TimeDisable(rowIdx, key));
            }
        }

        // eanble những cell mà không có worktime.
        enableCellStartEndTime(rowIdx, key) {
            let self = this;
            $("#extable").exTable('enableCell', 'detail', rowIdx + '', key + '', '2');
            $("#extable").exTable('enableCell', 'detail', rowIdx + '', key + '', '3');
            // remove cell vừa bị disable vào list
            _.remove(self.listTimeDisable, function(cell: TimeDisable) {
                return cell.rowId == rowIdx && cell.columnId == key;
            });
        }

        /**
         * paste data on cell
         * stick
         */
        pasteData(): void {
            let self = this;
            if (self.mode() == UpdateMode.DETERMINE)
                return;
            nts.uk.ui.block.grayout();
            $("#paste").addClass("btnControlSelected A6_hover").removeClass("btnControlUnSelected A6_not_hover");
            $("#coppy").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#input").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            
            $("#extable").exTable("updateMode", UpdateMode.STICK);
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
            self.userInfor.updateMode = UpdateMode.STICK;
            characteristics.save(self.KEY, self.userInfor);

            if (self.selectedModeDisplayInBody() == ViewMode.TIME || self.selectedModeDisplayInBody() == ViewMode.SHORTNAME) {
                $("#extable").exTable("stickMode", StickMode.SINGLE);
                if (self.selectedModeDisplayInBody() == ViewMode.TIME){
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName", "startTime", "endTime"]);
                } else {
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName"]);
                }
                
                // set lai data stick
                let objWorkTime = __viewContext.viewModel.viewAB.objWorkTime;
                if (objWorkTime != undefined) {
                    __viewContext.viewModel.viewAB.workTimeCode(objWorkTime.code);
                    __viewContext.viewModel.viewAB.updateDataCell(objWorkTime);
                }
                
            } else if (self.selectedModeDisplayInBody() == ViewMode.SHIFT) {
                $("#extable").exTable("stickMode", StickMode.MULTI);
                $("#extable").exTable("stickFields", ["shiftName"]);
                // set lai data stick
                if (__viewContext.viewModel.viewAC.selectedpalletUnit() == 1) {
                    let selectedBtnTblCom = __viewContext.viewModel.viewAC.selectedButtonTableCompany();
                    if (selectedBtnTblCom.hasOwnProperty('data')) {
                        __viewContext.viewModel.viewAC.selectedButtonTableCompany(selectedBtnTblCom);
                    }
                } else {
                    let selectedBtnTblWkp = __viewContext.viewModel.viewAC.selectedButtonTableWorkplace();
                    if (selectedBtnTblWkp.hasOwnProperty('data')) {
                        __viewContext.viewModel.viewAC.selectedButtonTableWorkplace(selectedBtnTblWkp);
                    }
                }
            }

            $("#extable").exTable("stickValidate", function(rowIdx, key, data) {
                let dfd = $.Deferred();
                let userInfor: IUserInfor = self.userInfor;
                if (userInfor.disPlayFormat == ViewMode.TIME || userInfor.disPlayFormat == ViewMode.SHORTNAME) {
                    let resolve = false;
                    let obj = _.find(self.listLockCells, function(o) { return o.rowIndex == rowIdx + '' && o.columnKey == key; });
                    if (!_.isNil(obj)) {
                        return;
                    }
                    
                    let cellDisableTime = _.find(self.listTimeDisable, function(o) { return o.rowId == rowIdx + '' && o.columnId == key; });

                    let workType = self.dataCell.objWorkType;
                    let workTime = self.dataCell.objWorkTime;

                    // truong hop chon workType = required va workTime = 選択なし 
                    if ((workType.workTimeSetting == 0 && workTime.code == '')) {
                        nts.uk.ui.dialog.alertError({ messageId: 'Msg_435' });
                        dfd.resolve(false);
                    } else if ((workType.workTimeSetting == 0 && workTime.code == ' ')) {
                        self.stickValDeferred(rowIdx, key, data, userInfor, cellDisableTime).done((data) => {
                            dfd.resolve(true);
                        }).fail(function() {
                            dfd.reject();
                        }).always((data) => {
                            dfd.reject();
                        });

                    } else if ((userInfor.disPlayFormat == ViewMode.TIME) && (data.workHolidayCls == 1 || data.workHolidayCls == 2)) {
                        // trương hơp là đi làm nửa ngày
                        self.stickValHalfDaySelected(rowIdx, key, data, userInfor, cellDisableTime).done((data) => {
                            dfd.resolve(true);
                        }).fail(function() {
                            dfd.reject();
                        }).always((data) => {
                            dfd.reject();
                        });

                    } else {
                        // enable | disable cell startTime,endtime
                        if (userInfor.disPlayFormat == ViewMode.TIME) {
                            if (data.workHolidayCls == 0) {
                                self.diseableCellStartEndTime(rowIdx+'', key);
                            } else {
                                self.enableCellStartEndTime(rowIdx+'', key);
                            }
                        }
                        dfd.resolve(true);
                    }

                } else if (userInfor.disPlayFormat == ViewMode.SHIFT) {
                    // trương hơp là đi làm nửa ngày
                    self.stickValShiftMode(rowIdx, key, data).done((data) => {
                        dfd.resolve(true);
                    }).fail(function() {
                        dfd.reject();
                    }).always((data) => {
                        dfd.reject();
                    });
                }
                return dfd.promise();
            });
            nts.uk.ui.block.clear();
        }
        
        // valid data trong mode shift khi stick
        stickValShiftMode(rowIdx, key, data) : JQueryPromise<void>{
            let self = this;
            let dfd = $.Deferred<void>();
            // truong hop listPage empty thi khong can validate
            if ((__viewContext.viewModel.viewAC.selectedpalletUnit()) == 1 && (__viewContext.viewModel.viewAC.listPageComIsEmpty == true)) {
                dfd.reject();
                return;
            }

            if ((__viewContext.viewModel.viewAC.selectedpalletUnit()) == 2 && (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true)) {
                dfd.reject();
                return;
            }

            // neu data = {} thi la co  shiftmaster đa bị xóa
            // => stick show mess 1728
            let isRemove = Object.keys(data).length === 0 && data.constructor === Object;
            if (isRemove) {
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_1727' });
            }

            nts.uk.ui.block.grayout();
            // khoi tao param ddeer truyen leen server check xem shiftmaster đã bị xóa đi hay chưa
            let param = [];
            for (const item in data) {
                let x = {
                    shiftmastercd: data[item].shiftCode,
                    workTypeCode: data[item].workTypeCode,
                    workTimeCode: data[item].workTimeCode
                };
                param.push(x);
            }
            service.validWhenPaste(param).done((data) => {
                nts.uk.ui.block.clear();
                if (data == true) {
                    dfd.resolve(true);
                } else {
                    dfd.reject();
                }
            }).fail(function() {
                nts.uk.ui.block.clear();
                dfd.reject();
            }).always((data) => {
                if (data.messageId == "Msg_1728") {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1728' });
                }
                nts.uk.ui.block.clear();
                dfd.reject();
            });
            return dfd.promise();
        }
        
        // stick validate khi là đi làm nửa ngày
        stickValHalfDaySelected(rowIdx, key, data, userInfor, cellDisableTime) : JQueryPromise<void>{
            let self = this;
            let dfd = $.Deferred<void>();
            // 午前出勤系 MORNING(1, "午前出勤系") 
            // 午後出勤系 AFTERNOON(2, "午後 
            nts.uk.ui.block.grayout();
            let param = {
                worktypeCode: data.workTypeCode,
                worktimeCode: data.workTimeCode
            }
            service.checkCorrectHalfday(param).done((rs) => {
                // set lai starttime, endtime cua object stick
                $("#extable").exTable("stickFields", ["workTypeName", "workTimeName", "startTime", "endTime"]);

                let startTime = rs.startTime == null ? '' : formatById("Clock_Short_HM", rs.startTime);
                let endTime = rs.endTime == null ? '' : formatById("Clock_Short_HM", rs.endTime);
                $("#extable").exTable("stickData", {
                    workTypeCode: data.workTypeCode,
                    workTypeName: data.workTypeName,
                    workTimeCode: data.workTimeCode,
                    workTimeName: data.workTimeName,
                    startTime: startTime,
                    endTime: endTime,
                    achievements: data.achievements,
                    workHolidayCls: data.workHolidayCls
                });

                // trường hợp cell này nằm trong list cell bị disable starttime, endtime 
                // thì enable cell đó lên, xóa cell đó khỏi danh sach cell bị disable starttime, endtime .
                if (!_.isNil(cellDisableTime)) {
                    self.enableCellStartEndTime(rowIdx + '', key);
                }

                nts.uk.ui.block.clear();
                dfd.resolve(true);
            }).fail(function() {
                nts.uk.ui.block.clear();
                dfd.reject();
            }).always((data) => {
                nts.uk.ui.block.clear();
                dfd.reject();
            });
            return dfd.promise();
        }
        
        // stick validate when selec deferred
        stickValDeferred(rowIdx, key, data, userInfor, cellDisableTime) : JQueryPromise<void>{
            let self = this;
            let dfd = $.Deferred<void>();
            // truong hop chon workType = required va workTime = 据え置き 
            // neu cell dc stick ma khong co workTime se alertError 435
            let dataSource = $("#extable").exTable('dataSource', 'detail').body;
            let cellData = dataSource[rowIdx][key];
            if (_.isNil(cellData.workTimeName)) {
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_435' });
                dfd.reject();
            } else {
                //xử lý cho ver1.9
                if (data.workHolidayCls != 0) {
                    let wtimeCd = cellData.workTimeCode;
                    let objWTime = _.find(__viewContext.viewModel.viewAB.listWorkTime2, function(o) { return o.code === wtimeCd; });
                    if (!_.isNil(objWTime)) {
                        if (userInfor.disPlayFormat == ViewMode.TIME) {
                            if (data.workHolidayCls === 3) { // đi làm fulltime
                                let startTime = _.isNil(objWTime) ? '' : formatById("Clock_Short_HM", objWTime.tzStart1);
                                let endTime   = _.isNil(objWTime) ? '' : formatById("Clock_Short_HM", objWTime.tzEnd1);

                                $("#extable").exTable("stickFields", ["workTypeName", "workTimeName", "startTime", "endTime"]);
                                $("#extable").exTable("stickData", {
                                    workTypeCode: data.workTypeCode,
                                    workTypeName: data.workTypeName,
                                    workTimeCode: cellData.workTimeCode,
                                    workTimeName: cellData.workTimeName,
                                    startTime: startTime,
                                    endTime: endTime,
                                    achievements: false,
                                    workHolidayCls: data.workHolidayCls
                                });

                                // trường hợp cell này nằm trong list cell bị disable starttime, endtime 
                                // thì enable cell đó lên, xóa cell đó khỏi danh sach cell bị disable starttime, endtime .
                                if (!_.isNil(cellDisableTime)) {
                                    self.enableCellStartEndTime(rowIdx + '', key);
                                }

                                dfd.resolve(true);
                            } else if (data.workHolidayCls == 1 || data.workHolidayCls == 2) { // làm nủa ngay
                                nts.uk.ui.block.grayout();
                                let param = {
                                    worktypeCode: data.workTypeCode,
                                    worktimeCode: objWTime.code
                                }
                                service.checkCorrectHalfday(param).done((rs) => {
                                    // set lai starttime, endtime cua object stick
                                    $("#extable").exTable("stickFields", ["workTypeName", "workTimeName", "startTime", "endTime"]);

                                    let startTime = rs.startTime == null ? '' : formatById("Clock_Short_HM", rs.startTime);
                                    let endTime = rs.endTime == null ? '' : formatById("Clock_Short_HM", rs.endTime);
                                    $("#extable").exTable("stickData", {
                                        workTypeCode: data.workTypeCode,
                                        workTypeName: data.workTypeName,
                                        workTimeCode: cellData.workTimeCode,
                                        workTimeName: cellData.workTimeName,
                                        startTime: startTime,
                                        endTime: endTime,
                                        achievements: false,
                                        workHolidayCls: data.workHolidayCls
                                    });

                                    // trường hợp cell này nằm trong list cell bị disable starttime, endtime 
                                    // thì enable cell đó lên, xóa cell đó khỏi danh sach cell bị disable starttime, endtime .
                                    if (!_.isNil(cellDisableTime)) {
                                        self.enableCellStartEndTime(rowIdx + '', key);
                                    }
                                    
                                    nts.uk.ui.block.clear();
                                    dfd.resolve(true);
                                }).fail(function() {
                                    nts.uk.ui.block.clear();
                                    dfd.reject();
                                }).always((data) => {
                                    nts.uk.ui.block.clear();
                                    dfd.reject();
                                });
                            }
                        } else {
                            $("#extable").exTable("stickFields", ["workTypeName", "workTimeName"]);
                            $("#extable").exTable("stickData", {
                                workTypeCode: data.workTypeCode,
                                workTypeName: data.workTypeName,
                                workTimeCode: cellData.workTimeCode,
                                workTimeName: cellData.workTimeName,
                                startTime: '',
                                endTime: '',
                                achievements: false,
                                workHolidayCls: data.workHolidayCls
                            });
                            dfd.resolve(true);
                        }
                    } else { // truong hop worktime không tồn tại trong list worktime => lấy trong datasource
                        if (userInfor.disPlayFormat == ViewMode.TIME) {
                            if (data.workHolidayCls === 3) { // đi làm fulltime
                                $("#extable").exTable("stickFields", ["workTypeName", "workTimeName", "startTime", "endTime"]);
                                $("#extable").exTable("stickData", {
                                    workTypeCode: data.workTypeCode,
                                    workTypeName: data.workTypeName,
                                    workTimeCode: cellData.workTimeCode,
                                    workTimeName: cellData.workTimeName,
                                    startTime: cellData.startTime,
                                    endTime: cellData.endTime,
                                    achievements: false,
                                    workHolidayCls: data.workHolidayCls
                                });

                                // trường hợp cell này nằm trong list cell bị disable starttime, endtime 
                                // thì enable cell đó lên, xóa cell đó khỏi danh sach cell bị disable starttime, endtime .
                                if (!_.isNil(cellDisableTime)) {
                                    self.enableCellStartEndTime(rowIdx + '', key);
                                }

                                dfd.resolve(true);
                            } else if (data.workHolidayCls == 1 || data.workHolidayCls == 2) { // làm nủa ngay
                                nts.uk.ui.block.grayout();
                                let param = {
                                    worktypeCode: data.workTypeCode,
                                    worktimeCode: cellData.workTimeCode
                                }
                                service.checkCorrectHalfday(param).done((rs) => {
                                    // set lai starttime, endtime cua object stick
                                    $("#extable").exTable("stickFields", ["workTypeName", "workTimeName", "startTime", "endTime"]);

                                    let startTime = rs.startTime == null ? '' : formatById("Clock_Short_HM", rs.startTime);
                                    let endTime = rs.endTime == null ? '' : formatById("Clock_Short_HM", rs.endTime);
                                    $("#extable").exTable("stickData", {
                                        workTypeCode: data.workTypeCode,
                                        workTypeName: data.workTypeName,
                                        workTimeCode: cellData.workTimeCode,
                                        workTimeName: cellData.workTimeName,
                                        startTime: startTime,
                                        endTime: endTime,
                                        achievements: false,
                                        workHolidayCls: data.workHolidayCls
                                    });

                                    // trường hợp cell này nằm trong list cell bị disable starttime, endtime 
                                    // thì enable cell đó lên, xóa cell đó khỏi danh sach cell bị disable starttime, endtime .
                                    if (!_.isNil(cellDisableTime)) {
                                        self.enableCellStartEndTime(rowIdx + '', key);
                                    }

                                    nts.uk.ui.block.clear();
                                    dfd.resolve(true);
                                }).fail(function() {
                                    nts.uk.ui.block.clear();
                                    dfd.reject();
                                }).always((data) => {
                                    nts.uk.ui.block.clear();
                                    dfd.reject();
                                });
                            }
                        } else {
                            $("#extable").exTable("stickFields", ["workTypeName", "workTimeName"]);
                            $("#extable").exTable("stickData", {
                                workTypeCode: data.workTypeCode,
                                workTypeName: data.workTypeName,
                                workTimeCode: cellData.workTimeCode,
                                workTimeName: cellData.workTimeName,
                                startTime: '',
                                endTime: '',
                                achievements: false,
                                workHolidayCls: data.workHolidayCls
                            });
                            dfd.resolve(true);
                        }
                    }
                }
            }
            return dfd.promise();
        }

        /**
         * copy data on cell
         */
        coppyData(): void {
            let self = this;
            if (self.mode() == UpdateMode.DETERMINE)
                return;
            $("#extable").exTable("updateMode", UpdateMode.COPY_PASTE);
            $("#extable").exTable("updateMode", UpdateMode.STICK);
            $("#extable").exTable("updateMode", UpdateMode.COPY_PASTE);
            nts.uk.ui.block.grayout();
            $("#paste").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#coppy").addClass("btnControlSelected A6_hover").removeClass("btnControlUnSelected A6_not_hover");
            $("#input").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
            
            self.userInfor.updateMode =  UpdateMode.COPY_PASTE;
            characteristics.save(self.KEY, self.userInfor);
            
            self.setStyler();
            
            $("#extable").exTable("pasteValidate", function(rowIdx, key, data) {
                let dfd = $.Deferred();
                let param = [];
                nts.uk.ui.block.grayout();
                if (_.isNil(data))
                    data = rowIdx;
                _.forEach(data, d => {
                    let shiftCode = d.shiftCode;
                    let shiftMasterWithWorkStyleLst = self.userInfor.shiftMasterWithWorkStyleLst;
                    let objShiftMaster = _.filter(shiftMasterWithWorkStyleLst, function(o) { return o.shiftMasterCode == shiftCode; });
                    if (objShiftMaster.length > 0) {
                        let x = {
                            shiftmastercd: shiftCode,
                            workTypeCode: objShiftMaster[0].workTypeCode,
                            workTimeCode: objShiftMaster[0].workTimeCode
                        };
                        param.push(x);
                    }
                });
                service.validWhenPaste(param).done((data) => {
                    if (data == true) {
                        dfd.resolve(true);
                    }
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    nts.uk.ui.block.clear();
                    dfd.reject();
                }).always((data) => {
                    if (data.messageId == "Msg_1728") {
                        nts.uk.ui.dialog.alertError({ messageId: 'Msg_1728' });
                    }
                    nts.uk.ui.block.clear();
                });

                return dfd.promise();
            });

            $("#extable").exTable("afterPaste", function(rowIdx, key, data) {
                if (self.userInfor.disPlayFormat == ViewMode.TIME) {
                    if (data.workHolidayCls == 0) {
                        self.diseableCellStartEndTime(rowIdx + '', key);
                    } else {
                        self.enableCellStartEndTime(rowIdx + '', key);
                    }
                }
            });
            
            nts.uk.ui.block.clear();
        }

        inputData(): void {
            let self = this;
            if (self.mode() == UpdateMode.DETERMINE)
                return;
            nts.uk.ui.block.grayout();
            $("#paste").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#coppy").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#input").addClass("btnControlSelected A6_hover").removeClass("btnControlUnSelected A6_not_hover");
            
            $("#extable").exTable("updateMode", UpdateMode.EDIT);
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
            self.userInfor.updateMode = UpdateMode.EDIT;
            characteristics.save(self.KEY, self.userInfor);

            nts.uk.ui.block.clear();
        }
        
        inputDataValidate(param): JQueryPromise<void> {
            let self = this;
            let dfd = $.Deferred<void>();
            nts.uk.ui.block.grayout();
            service.validWhenEditTime(param).done((data: any) => {
                if (data == true) {
                    dfd.resolve(true);
                }
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }
        
        undoData(): void {
            let self = this;
            if (self.userInfor.updateMode == UpdateMode.STICK) {
                $("#extable").exTable("stickUndo", function(rowIdx, columnKey, innerIdx, cellData) {
                    if (self.userInfor.disPlayFormat == ViewMode.TIME) {
                        if ((cellData.workHolidayCls === 0) || (cellData.workTimeCode == null && cellData.workTimeName == null && cellData.workTypeCode == null && cellData.workTypeName == null)) {
                            self.diseableCellStartEndTime(rowIdx+ '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (self.userInfor.updateMode == UpdateMode.COPY_PASTE) {
                $("#extable").exTable("copyUndo", function(rowIdx, columnKey, cellData) {
                    if (self.userInfor.disPlayFormat == ViewMode.TIME) {
                        if ((cellData.workHolidayCls === 0) || (cellData.workTimeCode == null && cellData.workTimeName == null && cellData.workTypeCode == null && cellData.workTypeName == null)) {
                            self.diseableCellStartEndTime(rowIdx + '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (self.userInfor.updateMode == UpdateMode.EDIT) {
                $("#extable").exTable("editUndo");
            }
            self.checkExitCellUpdated();
            self.enableBtnRedo(true);
        }

        redoData(): void {
            let self = this;
            let userInfor = self.userInfor;
            if (userInfor.updateMode == UpdateMode.STICK) {
                $("#extable").exTable("stickRedo", function(rowIdx, columnKey, innerIdx, cellData) {
                    if (userInfor.disPlayFormat == ViewMode.TIME) {
                        if ((cellData.workHolidayCls === 0) || (cellData.startTime === '' && cellData.endTime === '')) {
                            self.diseableCellStartEndTime(rowIdx + '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (userInfor.updateMode == UpdateMode.COPY_PASTE) {
                $("#extable").exTable("copyRedo", function(rowIdx, columnKey, cellData) {
                    if (userInfor.disPlayFormat == ViewMode.TIME) {
                        if ((cellData.workHolidayCls === 0) || (cellData.startTime === '' && cellData.endTime === '')) {
                            self.diseableCellStartEndTime(rowIdx + '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (userInfor.updateMode == UpdateMode.EDIT) {
                $("#extable").exTable("editRedo");
            }
            self.checkExitCellUpdated();
        }
        
        checkExitCellUpdated() {
            let self = this;
            let userInfor = self.userInfor;
            setTimeout(() => {
                let updatedCells = $("#extable").exTable("updatedCells");
                if (_.size(updatedCells) > 0) {
                    self.enableBtnReg(true);
                } else {
                    self.enableBtnReg(false);
                }
                
                if (_.size($("#extable").data("errors")) > 0)
                    self.enableBtnReg(false);
                
                if (userInfor.updateMode == UpdateMode.STICK) {
                    // check undo
                    let $grid1   = $("#extable").find("." + "ex-body-detail");
                    let histories = $grid1.data("stick-history");
                    if (!histories || histories.length === 0){
                        self.enableBtnUndo(false);
                    } else {
                        self.enableBtnUndo(true);
                    }
                    // check redo
                    let $grid2 = $("#extable").find(`.${"ex-body-detail"}`);
                    let redoStack = $grid2.data("stick-redo-stack");
                    if (!redoStack || redoStack.length === 0){
                        self.enableBtnRedo(false);
                    } else {
                        self.enableBtnRedo(true);
                    }
                } else if (userInfor.updateMode == UpdateMode.COPY_PASTE) {
                    // check undo
                    let $grid1 = $("#extable").find("." + "ex-body-detail");
                    let histories = $grid1.data("copy-history");
                    if (!histories || histories.length === 0){
                        self.enableBtnUndo(false);
                    } else {
                        self.enableBtnUndo(true);
                    }
                    
                    // check redo
                    let $grid2 = $("#extable").find("." + "ex-body-detail");
                    let redoStack = $grid2.data("redo-stack");
                    if (!redoStack || redoStack.length === 0) {
                        self.enableBtnRedo(false);
                    } else {
                        self.enableBtnRedo(true);
                    }
                } else if (userInfor.updateMode == UpdateMode.EDIT) {
                    // check undo
                    let $grid1 = $("#extable").find("." + "ex-body-detail");
                    let histories = $grid1.data("edit-history");
                    if (!histories || histories.length === 0){
                        self.enableBtnUndo(false);
                    } else {
                        self.enableBtnUndo(true);
                    }
                    
                    // check redo
                    let $grid2 = $("#extable").find("." + "ex-body-detail");
                    let redoStack = $grid2.data("edit-redo-stack");
                    if (!redoStack || redoStack.length === 0) {
                        self.enableBtnRedo(false);
                    } else {
                        self.enableBtnRedo(true);
                    }
                }
            }, 1);
        }
        
        /**
          * open dialog D
         */
        openDialogU(): void {
            let self = this;
            let userInfor : IUserInfor = self.userInfor;

             setShared('dataShareDialogU', {                
                startDate: moment(self.dtPrev()).format('YYYY/MM/DD'),
                endDate: moment(self.dtAft()).format('YYYY/MM/DD'),
                unit: userInfor.unit,
                workplaceId: userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId,
            });

            nts.uk.ui.windows.sub.modal("/view/ksu/001/u/index.xhtml").onClosed(() => {
                if (getShared("dataFromScreenD") && !getShared("dataFromScreenD").clickCloseDialog) {
                    // to do
                }
            });
        }
        
        /**
          * open dialog G
         */
        openDialogG(): void {
            let self = this;
            $('div > iframe').contents().find('#btnCloseG').trigger('click');
            // listEmpData : {id : '' , code : '', name : ''}
            setShared('dataShareDialogG', {
                startDate   : moment(self.dtPrev()).format('YYYY/MM/DD'),
                endDate     : moment(self.dtAft()).format('YYYY/MM/DD'),
                employeeIDs : self.listSid(),
            });
            $('#A1_7_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modeless("/view/ksu/001/g/index.xhtml").onClosed(() => {});
        }

        openKDL055() {
            let self = this;
        }

        openDialogKDL055() {
            let self = this;
            let param = {
                listSid: self.listSid(),
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
            };
            setShared('dataShareDialogKDL055', param);
            nts.uk.ui.windows.sub.modal('/view/kdl/055/a/index.xhtml').onClosed(function(): any { });
        }
        
        // A2_1
        openKDL046() {
            let self = this;
            let userInfor: IUserInfor = self.userInfor;

            let param = {
                unit: userInfor.unit == 0 ? '0' : '1',
                date: moment(self.dateTimeAfter()),
                workplaceId: userInfor.unit == 0 ? userInfor.workplaceId : null,
                workplaceGroupId: userInfor.unit == 0 ? null : userInfor.workplaceGroupId,
                showBaseDate : false
            }
            setShared('dataShareDialog046', param);
            $('#A1_10_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modal('/view/kdl/046/a/index.xhtml').onClosed(function(): any {
                let dataFrom046 = getShared('dataShareKDL046');
                if (dataFrom046 === undefined || dataFrom046 === null)
                    return;
                self.updateScreenAfterChangeWP(dataFrom046);
            });
        }
        
        updateScreenAfterChangeWP(input: any): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            nts.uk.ui.block.grayout();
            let userInfor: IUserInfor = self.userInfor;
            let param = {
                viewMode: userInfor.disPlayFormat,
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                shiftPalletUnit: userInfor.shiftPalletUnit, // 1: company , 2 : workPlace 
                pageNumberCom: userInfor.shiftPalettePageNumberCom,
                pageNumberOrg: userInfor.shiftPalettePageNumberOrg,
                getActualData: userInfor.achievementDisplaySelected,
                listShiftMasterNotNeedGetNew: userInfor.shiftMasterWithWorkStyleLst, // List of shifts không cần lấy mới
                unit: input.unit,
                wkpId: input.unit == WorkPlaceUnit.WORKPLACE ? input.workplaceId : input.workplaceGroupID,
                day: self.closeDate.day,
                isLastDay: self.closeDate.lastDay,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue() // A12_1
            };
            
            service.changeWokPlace(param).done((data: IDataStartScreen) => {
                
                self.targetOrganizationName(input.unit == WorkPlaceUnit.WORKPLACE ? input.workplaceName : input.workplaceGroupName);
                
                self.userInfor.unit             = input.unit;
                self.userInfor.workplaceId      = input.unit == WorkPlaceUnit.WORKPLACE ? input.workplaceId : '';
                self.userInfor.workplaceGroupId = input.unit == WorkPlaceUnit.WORKPLACE ? '' : input.workplaceGroupID;
                self.userInfor.workPlaceName    = input.unit == WorkPlaceUnit.WORKPLACE ? input.workplaceName : input.workplaceGroupName;
                self.userInfor.code             = input.workplaceGroupCode;
                characteristics.save(self.KEY, self.userInfor);
                
                if (self.userInfor.disPlayFormat === ViewMode.TIME || self.userInfor.disPlayFormat === ViewMode.SHORTNAME) {
                    __viewContext.viewModel.viewAB.check(false);
                    __viewContext.viewModel.viewAB.filter(input.unit == WorkPlaceUnit.WORKPLACE ? true : false);
                    __viewContext.viewModel.viewAB.workplaceIdKCP013(input.unit == 0 ? input.workplaceId : input.workplaceGroupID);
                } else {
                    if (input.unit == WorkPlaceUnit.WORKPLACE) {
                        $('#Aa1_2 > label:nth-child(2) > span').html(getText('Com_Workplace'));
                    } else {
                        $('#Aa1_2 > label:nth-child(2) > span').html(getText('Com_WorkplaceGroup'));
                    }

                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                    // set data shiftPallet
                    __viewContext.viewModel.viewAC.flag = false;
                    __viewContext.viewModel.viewAC.selectedpalletUnit(self.userInfor.shiftPalletUnit);
                    
                    if (self.userInfor.shiftPalletUnit == 1) {
                        __viewContext.viewModel.viewAC.handleInitCom(data.listPageInfo,data.targetShiftPalette.shiftPalletCom,self.userInfor.shiftPalettePageNumberCom);
                    } else {
                        __viewContext.viewModel.viewAC.handleInitWkp(data.listPageInfo,data.targetShiftPalette.shiftPalletWorkPlace,self.userInfor.shiftPalettePageNumberOrg);
                    }
                    __viewContext.viewModel.viewAC.flag = true;
                    if (__viewContext.viewModel.viewAC.listPageComIsEmpty == true) {
                        $('.ntsButtonTableButton').addClass('nowithContent');
                    }
                    if (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true) {
                        $('.ntsButtonTableButton').addClass('nowithContent');
                    }
                    
                    if (self.mode() == UpdateMode.DETERMINE) {
                        self.shiftPalletControlDisable();
                    }
                }
                  
                self.saveDataGrid(data);
                
                let dataBindGrid = self.convertDataToGrid(data, self.selectedModeDisplayInBody());
                // updatelaiA1112
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());

                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);

                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }
                
                nts.uk.ui.block.clear();
                
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }
        
        // A1_12_8
        openQDialog() {
            let self = this;
            let period = {
                    startDate: self.dateTimePrev(),
                    endDate: self.dateTimeAfter()
                };
            let userInfor  = self.userInfor;
            
            setShared('target', {
                unit: userInfor.unit,
                id: userInfor.unit == 0 ? userInfor.workplaceId : userInfor.workplaceGroupId
            });
            
            setShared('period', {
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter()
            });
            
            $('#A1_12_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modal("/view/ksu/001/q/index.xhtml").onClosed(() => {
            });
        }

        // A1_12_16
        openSDialog(): void {
            let self = this;
            setShared('KSU001S', {
                date: self.dtAft(),
                listEmpId: self.listEmpData
            });

            $('#A1_12_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modal("/view/ksu/001/s/a/index.xhtml").onClosed(() => {
                let dataShare = getShared("ksu001s-result");
                if (dataShare != 'Cancel'&& !_.isNil(dataShare)) {
                    nts.uk.ui.block.grayout();
                    self.getListEmpIdSorted().done(() => {
                        nts.uk.ui.block.clear();
                    });
                }
            });
        }
        
        // A1_12_18
        openLDialog(): void {
            let self = this;
            //hiện giờ truyền sang workplaceId va tất cả emmployee . Sau này sửa truyền list employee theo workplace id
            setShared("KSU001La", {
                date: self.dateTimeAfter(),
                listEmpData: self.listEmpData
            });
            $('#A1_12_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modal("/view/ksu/001/la/index.xhtml").onClosed(() => {
                let dataShare = getShared("ksu001la-result");
                if (dataShare != 'Cancel'&& !_.isNil(dataShare)) {
                    nts.uk.ui.block.grayout();
                    self.getListEmpIdSorted().done(() => {
                        nts.uk.ui.block.clear();
                    });
                }
            });
        }
        
        // A1_12_20
        openMDialog(): void {
            let self = this;
            setShared("KSU001M", self.listEmpData);
            $('#A1_12_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modal("/view/ksu/001/m/index.xhtml").onClosed(() => {
                let dataShare = getShared("ksu001m-result");
                if (dataShare != 'Cancel' && !_.isNil(dataShare)) {
                    nts.uk.ui.block.grayout();
                    self.getListEmpIdSorted().done(() => {
                        nts.uk.ui.block.clear();
                    });
                }
            });
        }

        getListEmpIdSorted(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            
            let param = {
                endDate: self.dateTimeAfter(),
                listEmpInfo: self.listEmpInfo
            }
            service.getListEmpIdSorted(param).done((data) => {
                // update lai grid
                if (data.lstEmp.length > 0) {
                    self.listEmpInfo = data.lstEmp;
                    self.listPersonalConditions = data.listPersonalCond;
                    let dataGrid: any = {
                        listEmpInfo: data.lstEmp,
                        listWorkScheduleWorkInfor: self.listWorkScheduleWorkInfor,
                        listWorkScheduleShift: self.listWorkScheduleShift,
                        listPersonalConditions: data.listPersonalCond,
                        displayControlPersonalCond: self.displayControlPersonalCond,
                        listDateInfo: self.listDateInfo
                    }
                    
                    // enable những cell đã disable trước đó đi rồi sau khi update grid mới disable đi được
                    if (self.userInfor.disPlayFormat === ViewMode.TIME) {
                        self.enableCellsTime();
                    }

                    let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());

                    self.updateExTableAfterSortEmp(dataBindGrid, self.selectedModeDisplayInBody(), self.userInfor.updateMode, true, true, true);

                    if (self.userInfor.disPlayFormat === ViewMode.TIME) {
                        self.diseableCellsTime();
                    }
                    
                    self.getAggregatedInfo(false, true);
                    
                    nts.uk.ui.block.clear();
                }
                dfd.resolve();
            }).fail(function() {
                dfd.reject();
            });
            return dfd.promise();
        }
        openDialogK() {
            let self = this;
            characteristics.restore(self.KEY).done((userInfor: IUserInfor) => {
                setShared('dataShareDialogK', {
                    orgUnit: userInfor.unit,
                    orgId: userInfor.unit == 0 ? userInfor.workplaceId : userInfor.workplaceGroupId,
                    startDate: moment(self.dtPrev()).toISOString(),
                    endDate: moment(self.dtAft()).toISOString(),
                    employeeIds : self.sids(),
                    closeDate: self.closeDate
                });
                nts.uk.ui.windows.sub.modal("/view/ksu/001/ka/index.xhtml");
            });
        }
        btnOpenKDL055(): void {
            let self = this;
            let arrCellUpdated = $("#extable").exTable("updatedCells");
            let arrTmp = _.clone(arrCellUpdated);
            let lockCells = $("#extable").exTable("lockCells");

            if (lockCells.length > 0 || arrCellUpdated.length > 0) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
                    // hiện tại mới chỉ đăng ký đc ở mode edit thôi
                    if (self.mode() == 'edit') {
                        self.saveDataInModeEdit();
                    } else {
                        self.openKDL055();
                    }
                }).ifNo(() => { self.openKDL055(); });
            } else {
                self.openKDL055();
            }
        }

        openKDL055(): void {
            let self = this, dfd = $.Deferred();
            let userInfor = self.userInfor;
            let param: any = { sIDs: self.sids(), startDate: moment(self.dateTimePrev()).format('YYYY/MM/DD'), endDate: moment(self.dateTimeAfter()).format('YYYY/MM/DD') };
            setShared('dataShareDialogKDL055A', param);
            nts.uk.ui.windows.sub.modal("/view/kdl/055/a/index.xhtml").onClosed(() => {
                let paramB = getShared('paramB');
                setShared('paramB', paramB);
                if (paramB) {
                    setShared('dataShareDialogKDL055B', paramB);
                    nts.uk.ui.windows.sub.modal("/view/kdl/055/b/index.xhtml").onClosed(() => {
                        let resultB = getShared('statusKDL055');
                        console.log(resultB);
                        let openAKDL055 = getShared('openA');
                        if (resultB == 'UPDATE') {
                            nts.uk.ui.block.grayout();
                            self.getNewData(userInfor.disPlayFormat).done(() => {
                                if (self.mode() == 'edit' && self.selectedModeDisplayInBody() == 'time') {
                                    self.diseableCellsTime();
                                    nts.uk.ui.block.clear();
                                }
                                
                                if(self.mode() == 'confirm'){
                                    self.confirmMode();    
                                }
                            });
                        } else if (resultB == 'CANCEL' && openAKDL055) {
                            self.openKDL055();
                        }
                    });
                }
            });
        }

        setTextResourceA173() {
            let self = this;
            let tr = getText('KSU001_14', ['#Com_Person']);
            let indexSp = _.indexOf(tr, '※');
            let line1 = tr.substring(0, indexSp - 1);
            let line2 = tr.substring(indexSp - 1, tr.length);
            self.A1_7_3_line1(line1);
            self.A1_7_3_line2(line2);
        }

        checkVisableByAuth(scheModifyAuth, functionNo) {
            let obj = _.find(scheModifyAuth, function(o) { return o.functionNo == functionNo; });
            return !_.isNil(obj) ? obj.isAvailable : false;
        }

        displayButtonsHerder(data) {
            let self = this;
            // các domain liên quan đến phần ẩn hiện
            // スケジュール修正の機能制御   - ScheFunctionControl                          
            // スケジュール修正職場別の機能制御    - ScheFunctionCtrlByWorkplace                            
            // スケジュール修正共通の権限制御    - ScheModifyAuthCtrlCommon                         
            // スケジュール修正職場別の権限制御  - ScheModifyAuthCtrlByWorkplace

            // lấy ra từ domian ScheFunctionCtrlByWorkplace.useCompletionAtr
            let scheFunctionCtrlByWorkplaceUse = data.dataBasicDto.scheFunctionCtrlByWorkplace.useCompletionAtr == 1 ? true : false;
            let medicalOP = data.dataBasicDto.medicalOP; // 医療OP

            // map với data domain sau
            // check hiển thị với Common và Authority
            let scheModifyAuthCtrlCommon = data.dataBasicDto.scheModifyAuthCtrlCommon;  // list
            let scheModifyAuthCtrlByWorkplace = data.dataBasicDto.scheModifyAuthCtrlByWorkplace; // list

            let funcNo1_Common = self.checkVisableByAuth(scheModifyAuthCtrlCommon, 1); // 休暇状況参照 Vacation status reference
            let funcNo2_Common = self.checkVisableByAuth(scheModifyAuthCtrlCommon, 2); // 修正履歴参照 Refer to revision history
            let funcNo1_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 1); // 登録 Registration
            let funcNo2_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 2); // 確定 Confirm
            let funcNo3_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 3); // 完了 Done
            let funcNo4_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 4); // アラームチェック Alarm check
            let funcNo5_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 5); // 出力 output
            let funcNo6_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 6); // 取り込み Capture
            let funcNo7_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 7)  // 勤務希望 Hope to work
            let funcNo8_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 8); // 公開 Release
            let funcNo9_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 9); // 応援登録 Support registration
            let funcNo10_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 10); // 並び順設定 Sort order setting
            let funcNo11_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 11); // チーム設定 Team settings
            let funcNo12_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 12); // ランク分け Ranking
            let funcNo13_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 13); // 行事登録 Event registration
            let funcNo14_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 14); // 集計欄の金額表示 Amount display in the summary column
            let funcNo15_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 15); // 予算実績入力 Budget actual input
            let funcNo16_WorkPlace = self.checkVisableByAuth(scheModifyAuthCtrlByWorkplace, 16); // 計画人数入力 Enter the planned number of people
            // button A1_1  職1
            if (funcNo1_WorkPlace == false)
                document.getElementById("A1_1").remove();

            // btn A1_2 職3 - ※35 
            if (funcNo3_WorkPlace == false || scheFunctionCtrlByWorkplaceUse == false)
                document.getElementById("A1_2").remove();

            // btn A1_3 職4
            if (funcNo4_WorkPlace == false)
                document.getElementById("A1_3").remove();

            // btn A1_5 職8 - ※27
            if (funcNo8_WorkPlace == false || data.dataBasicDto.usePublicAtr == false)
                document.getElementById("A1_5").remove();

            // btn A1_6 職5
            if (funcNo5_WorkPlace == false)
                document.getElementById("A1_6").remove();

            // btn A1_7 職7  - ※1
            if (funcNo7_WorkPlace == false || data.dataBasicDto.useWorkAvailabilityAtr == false)
                document.getElementById("A1_7").remove();

            // btn A1_8 職9  -  ※2 (tạm thời chưa đối ứng thằng ※2 này)
            if (funcNo9_WorkPlace == false)
                document.getElementById("A1_8").remove();

            // btn A1_9 職6
            if (funcNo6_WorkPlace == false)
                document.getElementById("A1_9").remove();

            // btn A1_10 共1
            if (funcNo1_Common == false)
                document.getElementById("A1_10").remove();

            // btn A1_11 共2
            if (funcNo2_Common == false)
                document.getElementById("A1_11").remove();

            // btn A6_1, A6_2 職2    
            if (funcNo2_WorkPlace == false) {
                $('#contain-view-left').empty();
                $('#contain-view-left').css('margin-left', '16px');
                $('#contain-view-right').css('width', '1177px');
            }

            // 職13
            if (funcNo13_WorkPlace == false) {
                self.canRegisterEvent = false;
            }

            self.calculateDisPlayPopupA12(funcNo10_WorkPlace, funcNo11_WorkPlace, funcNo12_WorkPlace, medicalOP);

            self.calculateDisPlaySwitchA32(data);
            
            self.calculateDisPlayFormatA4Popup(data);
            
            self.funcNo15_WorkPlace = funcNo15_WorkPlace;
        }
        
        calculateDisPlayFormatA4Popup(data){
            let self = this;
            self.calculateDisPlayFormatA4_234(data);

            self.calculateDisPlayFormatA4_567(data);

            self.calculateDisPlayA48A49();   
        }
        
        calculateDisPlayA48A49() {
            let self = this;
            // A4_8 ※30
            if (self.visibleA4_234() == true || self.visibleA4_567() == true) {
                self.visibleA4_8(true);
            } else {
                self.visibleA4_8(false);
            }

            // A4_9 ※32
            if ((self.showComboboxA4_12() == true) && (self.visibleA4_234() == true || self.visibleA4_567() == true)) {
                self.visibleA4_9(true);
            } else {
                self.visibleA4_9(false);
            }

            // ※31
            let condition31 = self.showComboboxA4_12() == true || self.visibleA4_567() == true || self.visibleA4_234() == true;
            if (!condition31) {
                $('#A4').css('visibility', 'hidden');
            }
        }
        
        checkSettingOpenKsu003(data){
            let self = this;
            // ※28 /** 日付別  - ByDate(0), /** 個人別   - ByPerson(1);
            // スケジュール修正職場別の機能制御    - ScheFunctionCtrlByWorkplace
            let pageCanBeStarted = [0,1];
            let canOpenKsu003 = _.find(data.dataBasicDto.scheFunctionCtrlByWorkplace.pageCanBeStarted, function(o) { return o == 0; });
            if(_.isNil(canOpenKsu003)){
                self.canOpenKsu003 = false;    
            }
        }

        calculateDisPlayPopupA12(funcNo10_WorkPlace, funcNo11_WorkPlace, funcNo12_WorkPlace, medicalOP) {
            let self = this;
            let numberItemHid = 0;
            $('#A1_12_16, #A1_12_18, #A1_12_20').width(75);
            // btn A1_16 職10 - ※11
            if (funcNo10_WorkPlace == false || medicalOP == false) {
                numberItemHid = numberItemHid + 1;
                $('#A1_12_1617').remove();
            }

            // btn A1_18 職11 - ※11
            if (funcNo11_WorkPlace == false || medicalOP == false) {
                numberItemHid = numberItemHid + 1;
                $('#A1_12_1819').remove();
            }

            // btn A1_20 職12 - ※11
            if (funcNo12_WorkPlace == false || medicalOP == false) {
                numberItemHid = numberItemHid + 1;
                $('#A1_12_2021').remove();
            }

            if (numberItemHid == 3) { // ※34
                $('#A1_12').css('visibility', 'hidden');
                $('#A1_12').off('click');
            }
        }

        // ※12,※13,※14
        calculateDisPlaySwitchA32(data) {
            let self = this;
            //lấy setting trong domain này スケジュール修正職場別の機能制御    - ScheFunctionCtrlByWorkplace.useDisplayPeriod
            // code tam ghep data server sau
            let useDisplayPeriods = data.dataBasicDto.scheFunctionCtrlByWorkplace.useDisplayPeriod;
            if (useDisplayPeriods.length == 0) {
                self.selectedDisplayPeriod(1);
                $('#A3_2').empty();
            } else if (useDisplayPeriods.length == 1) {
                self.disPeriodSelectionList().push({ id: 1, name: getText("KSU001_39") });
                if (useDisplayPeriods[0] == 0) {
                    self.disPeriodSelectionList().push({ id: 2, name: getText("KSU001_40") });
                } else if (useDisplayPeriods[0] == 1) {
                    self.disPeriodSelectionList().push({ id: 3, name: getText("KSU001_41") });
                }
            } else if (useDisplayPeriods.length == 2) {
                self.disPeriodSelectionList().push({ id: 1, name: getText("KSU001_39") });
                self.disPeriodSelectionList().push({ id: 2, name: getText("KSU001_40") });
                self.disPeriodSelectionList().push({ id: 3, name: getText("KSU001_41") });
            }

            // set css lại
        }

        calculateDisPlayFormatA4_234(data) {
            let self = this;
            //lấy setting trong domain này スケジュール修正職場別の機能制御    - ScheFunctionCtrlByWorkplace.useDisplayFormat
            //略名 AbbreviatedName(0)*/
            //勤務 WorkInfo(1)
            //シフト Shift(2)
            // code tam ghep data server sau
            // ※15,※16,※17,※29
            let useDisplayFormats = data.dataBasicDto.scheFunctionCtrlByWorkplace.useDisplayFormat;
            if (useDisplayFormats.length == 0) {
                self.visibleA4_234(false);
            } else if (useDisplayFormats.length == 1) {
                if (useDisplayFormats[0] == 0) {
                    self.modeDisplayList().push({ id: ViewMode.SHORTNAME, name: getText("KSU001_44") });
                } else if (useDisplayFormats[0] == 1) {
                    self.modeDisplayList().push({ id: ViewMode.TIME, name: getText("KSU001_43") });
                } else if (useDisplayFormats[0] == 2) {
                    self.modeDisplayList().push({ id: ViewMode.SHIFT, name: getText("KSU001_45") });
                }
                self.visibleA4_234(false);
            } else if (useDisplayFormats.length == 2) {
                if ((useDisplayFormats[0] == 0 || useDisplayFormats[0] == 1) && (useDisplayFormats[1] == 0 || useDisplayFormats[1] == 1)) {
                    self.modeDisplayList().push({ id: ViewMode.TIME, name: getText("KSU001_43") });
                    self.modeDisplayList().push({ id: ViewMode.SHORTNAME, name: getText("KSU001_44") });
                } else if ((useDisplayFormats[0] == 1 || useDisplayFormats[0] == 2) && (useDisplayFormats[1] == 1 || useDisplayFormats[1] == 2)) {
                    self.modeDisplayList().push({ id: ViewMode.TIME, name: getText("KSU001_43") });
                    self.modeDisplayList().push({ id: ViewMode.SHIFT, name: getText("KSU001_45") });
                } else if ((useDisplayFormats[0] == 0 || useDisplayFormats[0] == 2) && (useDisplayFormats[1] == 0 || useDisplayFormats[1] == 2)) {
                    self.modeDisplayList().push({ id: ViewMode.SHORTNAME, name: getText("KSU001_44") });
                    self.modeDisplayList().push({ id: ViewMode.SHIFT, name: getText("KSU001_45") });
                }
            } else if (useDisplayFormats.length == 3) {
                self.modeDisplayList().push({ id: ViewMode.TIME, name: getText("KSU001_43") });
                self.modeDisplayList().push({ id: ViewMode.SHORTNAME, name: getText("KSU001_44") });
                self.modeDisplayList().push({ id: ViewMode.SHIFT, name: getText("KSU001_45") });
            }

            self.setViewModeSelected(data.dataBasicDto.viewModeSelected);
        }

        // viewModeSelected la server tra ve
        setViewModeSelected(viewModeSelected) {
            let self = this;
            if (viewModeSelected == ViewMode.TIME) {
                self.selectedModeDisplayInBody(ViewMode.TIME);
                self.visibleShiftPalette(false);
            } else if (viewModeSelected == ViewMode.SHORTNAME) {
                self.selectedModeDisplayInBody(ViewMode.SHORTNAME);
                self.visibleShiftPalette(false);
            } else if (viewModeSelected == ViewMode.SHIFT) {
                self.selectedModeDisplayInBody(ViewMode.SHIFT);
                self.visibleShiftPalette(true);
            }
        }

        calculateDisPlayFormatA4_567(data) {
            let self = this;
            // ※18 - lay setting trong domain スケジュール修正の機能制御.実績表示できるか   - ScheFunctionControl.isDisplayActual
            let isDisplayActual = data.dataBasicDto.scheFunctionControl.isDisplayActual;
            self.visibleA4_567(isDisplayActual);
        }
        
        // click btnA1_6
        openKSU005() {
            let self = this;
            let userInfor: IUserInfor = self.userInfor;
            let param = {
                unit: userInfor.unit == 0 ? '0' : '1',
                workplaceId: userInfor.unit == 0 ? userInfor.workplaceId : null,
                workplaceGroupId: userInfor.unit == 0 ? null : userInfor.workplaceGroupId,
                startDate: moment(self.dtPrev()).format('YYYY/MM/DD'),
                endDate: moment(self.dtAft()).format('YYYY/MM/DD'),
                sids: self.listSid()
            }
            setShared('dataShareDialogKSU005', param);
            nts.uk.ui.windows.sub.modal('/view/ksu/005/a/index.xhtml').onClosed(function(): any {});
        }
        
        openKsu003(ui, detailContentDs) {
            let self = this;
            let detailContentData: any = [];
            for (let i = 0; i < detailContentDs.length; i++) {
                detailContentData.add({ employeeId: detailContentDs[i].employeeId, datas: detailContentDs[i][ui.columnKey] });
            }
            let dayEdit = new Date();
            let param = {
                detailContentDs: detailContentData,
                unit: self.userInfor.unit,
                workplaceId: self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                workplaceName: self.userInfor.workPlaceName,
                listEmp: self.listEmpData,
                daySelect: moment(ui.columnKey.slice(1)).format('YYYY/MM/DD'),
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                dayEdit: moment(self.scheduleModifyStartDate).format('YYYY/MM/DD')
            }
            setShared("dataFromA", param);
            setShared("dataTooltip", self.tooltipShare);
            nts.uk.ui.windows.sub.modal("/view/ksu/003/a/index.xhtml").onClosed(() => { });
        }

        // check cell edit truoc khi open ksu003
        checkBefOPenKsu003(ui, detailContentDs) {
            let self = this;
            let arrCellUpdated = $("#extable").exTable("updatedCells");
            if (arrCellUpdated.length > 0) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
                    self.saveDataInModeEdit(true, ui, detailContentDs)
                }).ifNo(() => { self.openKsu003(ui, detailContentDs); });
            } else {
                self.openKsu003(ui, detailContentDs);
            }
        }
        
        // get lại data A11, A12
        getAggregatedInfo(updateA11, updateA12, getWorkschedule?,blockScreen?) {
            let self = this;
            let dfd = $.Deferred();
            if(_.isNil(blockScreen))
                nts.uk.ui.block.grayout();
            let param = {
                listSid: self.listSid(),
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                day: self.closeDate.day,
                isLastDay: self.closeDate.lastDay,
                getActualData: !_.isNil(self.userInfor) ? self.userInfor.achievementDisplaySelected : false,
                workplaceId: self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                unit: self.userInfor.unit,
                isShiftMode: self.selectedModeDisplayInBody() == ViewMode.SHIFT ? true : false, // time | shortName | shift
                listShiftMasterNotNeedGetNew: !_.isNil(self.userInfor) ? self.userInfor.shiftMasterWithWorkStyleLst : [], // List of shifts không cần lấy mới
                getWorkschedule: _.isNil(getWorkschedule) ? false : getWorkschedule,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue() // A12_1
            };

            service.getAggregatedInfo(param).done((data: any) => {
                let start = Date.now();

                if (data.aggreratePersonal) {
                    self.dataAggreratePersonal = data.aggreratePersonal;
                }
                if (data.aggrerateWorkplace) {
                    self.dataAggrerateWorkplace = data.aggrerateWorkplace;
                }

                if (updateA11) {
                    self.createVertSumData();
                    self.updateVertSumGrid();
                }

                if (updateA12) {
                    self.createHorzSumData();
                    self.updateHorzSumGrid();
                }

                if (getWorkschedule) {
                    self.listWorkScheduleWorkInfor = data.listWorkScheduleWorkInfor;
                    self.listWorkScheduleShift = data.listWorkScheduleShift;
                    if (self.userInfor.disPlayFormat == ViewMode.SHIFT) {
                        self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                    }
                    self.cloneDataSource();
                    self.updateDataBindGrid();
                }
                let end = Date.now();
                let time = end - start;
                setTimeout(() => {
                    let key = request.location.current.rawUrl + "/extable/scroll";
                    uk.localStorage.getItem(key).ifPresent((data) => {
                        let scrollLength = JSON.parse(data);
                        $("#extable").exTable('scrollBack', 2, scrollLength);
                    });
                }, time + 100);

                dfd.resolve();
                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }
        
        // select A3-2-1
        getDataInModeA3_2_1() {
            let self = this;
            nts.uk.ui.block.grayout();
            let startDate = self.dateTimePrev(); // start Hiển thị trên màn hình
            let endDate = self.dateTimeAfter(); //end Hiển thị trên màn hình
            // A3_2_① - 表示切替の期間のチェック①
            if (startDate == self.startDateInitStart && endDate == self.endDateInitStart) {
                nts.uk.ui.block.clear();
                return;
            }
            self.getDataWhenChangeModePeriod(self.startDateInitStart, self.endDateInitStart);
        }
        // select A3-2-2
        getDataInModeA3_2_2() {
            let self = this;
            nts.uk.ui.block.grayout();
            // call <<ScreenQuery>> 28日の期間を取得する
            service.get28DayPeriod({ endDate: self.dateTimeAfter(), isNextMonth : true }).done((data: any) => {
                let startDateOnScreen = self.dateTimePrev(); // start Hiển thị trên màn hình
                let endDateOnScreen = self.dateTimeAfter(); //end Hiển thị trên màn hình
                // A3_2_② 表示切替の期間のチェック②
                if (data.start == startDateOnScreen && data.end == endDateOnScreen) {
                    nts.uk.ui.block.clear();
                    return;
                }
                self.getDataWhenChangeModePeriod(data.start, data.end);

            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
        }

        // select A3-2-3
        getDataInModeA3_2_3() {
            let self = this;
            nts.uk.ui.block.grayout();
            // A3_2_3  末日までの1ヶ月の期間を取得する    
            let date = new Date(self.endDateInitStart);
            let firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
            let lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);

            let startDate = moment(firstDay).format('YYYY/MM/DD');
            let endDate   = moment(lastDay).format('YYYY/MM/DD');

            // A3_2_② 表示切替の期間のチェック②
            if ((self.dateTimeAfter() == endDate) && (self.dateTimePrev() == startDate)) {
                nts.uk.ui.block.clear();
                return;
            }
            self.getDataWhenChangeModePeriod(startDate, endDate);
        }

        getDataWhenChangeModePeriod(startDate, endDate) {
            let self = this;
            let viewMode = self.selectedModeDisplayInBody();
            nts.uk.ui.block.grayout();

            let param = {
                viewMode: self.selectedModeDisplayInBody(), // time | shortName | shift
                startDate: startDate,
                endDate: endDate,
                workplaceId: self.userInfor.workplaceId,
                workplaceGroupId: self.userInfor.workplaceGroupId,
                unit: self.userInfor.unit,
                getActualData: !_.isNil(self.userInfor) ? self.userInfor.achievementDisplaySelected : false,
                listShiftMasterNotNeedGetNew: self.userInfor.shiftMasterWithWorkStyleLst,
                sids: self.listSid(),
                day: self.closeDate.day, 
                isLastDay: self.closeDate.lastDay,
                personTotalSelected: self.useCategoriesPersonalValue(), // A11_1
                workplaceSelected: self.useCategoriesWorkplaceValue(), // A12_1
            };

            service.getDataWhenChangeModePeriod(param).done((data: any) => {
                if (self.userInfor.disPlayFormat == ViewMode.SHIFT) {
                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                }
                
                self.saveDataGrid(data);
                self.dtPrev(data.dataBasicDto.startDate);
                self.dtAft(data.dataBasicDto.endDate);

                let dataGrid: any = {
                    listDateInfo: data.listDateInfo,
                    listEmpInfo: data.listEmpInfo,
                    displayControlPersonalCond: data.displayControlPersonalCond,
                    listPersonalConditions: data.listPersonalConditions,
                    listWorkScheduleWorkInfor: data.listWorkScheduleWorkInfor,
                    listWorkScheduleShift: data.listWorkScheduleShift,
                    aggreratePersonal: data.aggreratePersonal,
                    aggrerateWorkplace: data.aggrerateWorkplace
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());
                
                self.mode() == UpdateMode.DETERMINE ? self.confirmModeAct(false) : self.editModeAct(false);
                
                self.setPositionButonA13A14A15();
                
                if(!self.canOpenKsu003){
                    self.disableLinkDetailHeader();
                }

                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
        }
        
        cloneDataSource() {
            let self = this;
            self.listWorkScheduleWorkInforBase = _.cloneDeep(self.listWorkScheduleWorkInfor); 
            self.listWorkScheduleShiftBase = _.cloneDeep(self.listWorkScheduleShift);    
        }
        
        setExAreaAgency() {
            let self = this;
            let widthA8 = document.getElementsByClassName('ex-header-leftmost')[0].offsetWidth;
            if (self.showA9 && self.showA11()) {
                let widthA9 = document.getElementsByClassName('ex-header-middle')[0].offsetWidth;
                let widthA10 = document.getElementsByClassName('ex-header-detail')[0].offsetWidth;
                $(document.getElementsByClassName('ex-area-line')[1]).css('left', widthA8 + widthA9 - 1 + 'px');
                $(document.getElementsByClassName('ex-area-line')[2]).css('left', widthA8 + widthA9 + widthA10 - 2 + 'px');
            } else if (self.showA9 && !self.showA11()) {
                let widthA9 = document.getElementsByClassName('ex-header-middle')[0].offsetWidth;
                $(document.getElementsByClassName('ex-area-line')[1]).css('left', widthA8 + widthA9 - 1 + 'px');
            } else if (!self.showA9 && self.showA11()) {
                let widthA10 = document.getElementsByClassName('ex-header-detail')[0].offsetWidth;
                $(document.getElementsByClassName('ex-area-line')[1]).css('left', widthA8 + widthA10 - 1 + 'px');
            }
        }

        disableLinkDetailHeader() {
            let self = this;
            setTimeout(() => {
                $('.extable-header-detail a').css("pointer-events", "none");
            }, 500);
        }
    }

    export enum ViewMode {
        TIME = 'time',
        SHORTNAME = 'shortName',
        SHIFT = 'shift',
    }
    // Body height setting mode
    export enum BodyHeightSettingMode {
        DYNAMIC = "dynamic",
        FIXED = "fixed",
    }
    
    // Update mode
    export enum UpdateMode {
        COPY_PASTE = "copyPaste",
        EDIT = "edit",
        STICK = "stick",
        DETERMINE = "determine",
    }
    
    export enum StickMode {
        SINGLE = "single",
        MULTI = "multi",
    }

    export enum TypeHeightExTable {
        DEFAULT = 1,
        SETTING = 2
    }
    
    export enum BackGroundShiftMode {
        NORMAL = 0,
        SHIFT = 1
    }
    
    export enum WorkPlaceUnit {
        WORKPLACE = 0,
        WORKPLACE_GROUP = 1
    }
    
    class ExItem {
        sid: string;
        empName: string;

        constructor(sid: string, dsOfSid: BasicSchedule[], listWorkType: ksu001.common.modelgrid.WorkType[], listWorkTime: ksu001.common.modelgrid.WorkTime[], manual: boolean, arrDay: Time[]) {
            this.sid = sid;
            this.empName = sid;
            // create detailHeader (ex: 4/1 | 4/2)
            if (manual) {
                for (let i = 0; i < arrDay.length; i++) {
                    if ((i == 0 && +arrDay[0].day != 1) || (+arrDay[i].day == 1)) {
                        this['_' + arrDay[i].yearMonthDay] = arrDay[i].month + '/' + arrDay[i].day + "<br/>" + arrDay[i].weekDay;
                    } else {
                        this['_' + arrDay[i].yearMonthDay] = arrDay[i].day + "<br/>" + arrDay[i].weekDay;
                    }
                }
                return;
            }
        }
    }

    class CellColor {
        columnKey: any;
        rowId: any;
        innerIdx: any;
        clazz: any;
        constructor(columnKey: any, rowId: any, clazz: any, innerIdx?: any) {
            this.columnKey = columnKey;
            this.rowId = rowId;
            this.innerIdx = innerIdx;
            this.clazz = clazz;
        }
    }

    interface IPersonModel {
        employeeId: string,
        employeeCode: string,
        employeeName: string,
        workplaceId: string,
        wokplaceCode: string,
        workplaceName: string,
        baseDate?: number
    }

    class PersonModel {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        wokplaceCode: string;
        workplaceId: string;
        workplaceName: string;
        baseDate: number;

        constructor(param: IPersonModel) {
            this.employeeId = param.employeeId;
            this.employeeCode = param.employeeCode;
            this.employeeName = param.employeeName;
            this.wokplaceCode = param.wokplaceCode;
            this.workplaceId = param.workplaceId;
            this.workplaceName = param.workplaceName;
            this.baseDate = param.baseDate;
        }
    }

    class Time {
        year: string;
        month: string;
        day: string;
        weekDay: string;
        yearMonthDay: string;

        constructor(ymd: Date) {
            this.year = moment(ymd).format('YYYY');
            this.month = moment(ymd).format('M');
            this.day = moment(ymd).format('D');
            this.weekDay = moment(ymd).format('dd');
            this.yearMonthDay = this.year + moment(ymd).format('MM') + moment(ymd).format('DD');
        }
    }

    class ExCell {
        workTypeCode: string;
        workTypeName: string;
        workTimeCode: string;
        workTimeName: string;
        shiftName: string;
        startTime: any;
        endTime: any;
        shiftCode: string;
        confirmed: boolean;
        achievements: boolean;
        workHolidayCls:  number;
        needToWork: boolean;
        supportCategory:  number;
        condTargetdate: boolean;
        
        constructor(workTypeCode: string, workTypeName: string, workTimeCode: string, workTimeName: string, startTime?: string, endTime?: string, 
        shiftName?: any, shiftCode? : any, 
        confirmed? : any, achievements? : any, workHolidayCls? : number, needToWork?: boolean,supportCategory?:  number, condTargetdate?: boolean) {
            this.workTypeCode = workTypeCode;
            this.workTypeName = workTypeName;
            this.workTimeCode = workTimeCode;
            this.workTimeName = workTimeName;
            this.shiftName = shiftName !== null ? shiftName : null;
            this.startTime = ( startTime == undefined || startTime == null ) ? null : startTime;
            this.endTime = ( endTime == undefined || endTime == null ) ? null : endTime;
            this.shiftCode = shiftCode !== null ? shiftCode : null;
            this.confirmed = confirmed !== null ? confirmed : false;
            this.achievements = achievements !== null ? achievements : false;
            this.workHolidayCls = workHolidayCls !== null ? workHolidayCls : null;
            this.needToWork = needToWork !== null ? needToWork : null;
            this.supportCategory = supportCategory !== null ? supportCategory : null;
            this.condTargetdate = condTargetdate !== null ? condTargetdate : null;
        }
    }


    interface IDataStartScreen {
        dataBasicDto: IDataBasicDto,
        displayControlPersonalCond: IDisplayControlPersonalCond,
        listDateInfo: Array<IDateInfo>,
        listEmpInfo: Array<IEmpInfo>,
        listPersonalConditions: Array<IPersonalConditions>,
        
        listWorkTypeInfo: Array<IWorkTypeInfomation>,
        listWorkScheduleWorkInfor: Array<IWorkScheduleWorkInforDto>,
        
        listPageInfo: Array<IPageInfo>,
        shiftMasterWithWorkStyleLst : Array<IShiftMasterMapWithWorkStyle>,
        listWorkScheduleShift: Array<IWorkScheduleShiftInforDto>,
        aggreratePersonal: AggreratePersonalDto,
        aggrerateWorkplace: AggrerateWorkplaceDto,
    }
    
    interface IPageInfo {
        pageName: string,
        pageNumber: number,
    }

    interface IDataBasicDto {
        startDate: string,
        endDate: string,
        unit: number,
        workplaceId: string,
        workplaceGroupId: string,
        designation: string,
        targetOrganizationName: string,
        code: string,
        scheduleModifyStartDate: string,
        usePublicAtr: boolean,
        useWorkAvailabilityAtr: boolean,
        scheFunctionControl: IScheFunctionControlDto,
        useCategoriesWorkplace: [],
        useCategoriesPersonal: [],
        closeDate: any,
        medicalOP: any,
        nursingCareOP: any,
        viewModeSelected: string,
        
        
    }
    
    interface IScheFunctionControlDto {
        changeableWorks: [],
        isDisplayActual: boolean,
        displayWorkTypeControl: number,
        displayableWorkTypeCodeList: []
    }

    interface IDisplayControlPersonalCond {
        companyID: string,
        listQualificationCD: string,
        qualificationMark: string,
        listConditionDisplayControl: Array<IConditionDisplayControl>
    }

    interface IConditionDisplayControl {
        conditionATR: number,
        displayCategory: number
    }

    interface IDateInfo {
        ymd: string,
        dayOfWeek: number,
        isHoliday: boolean,
        isSpecificDay: boolean,
        optCompanyEventName: string,
        optWorkplaceEventName: string,
        listSpecDayNameCompany: Array<string>,
        listSpecDayNameWorkplace: Array<string>,
        isToday: boolean,
        htmlTooltip: string
    }

    interface IEmpInfo {
        employeeId: string,
        employeeCode: string,
        businessName: string
    }

    interface IPersonalConditions {
        sid: string,
        teamName: string,
        rankName: string,
        licenseClassification: string
    }

    class HtmlToolTip {
        key: string;
        value: string;
        constructor(key: string, value: string) {
            this.key = key;
            this.value = value;
        }
    }
    
    class TimeDisable {
        rowId: string;
        columnId: string;
        constructor(rowId: string, columnId: string) {
            this.rowId    = rowId;
            this.columnId = columnId;
        }
    }
    
    class CellConfirm {
        rowIndex: string;
        columnKey: string;
        confirm: boolean;
        constructor(rowIndex: string, columnKey: string, confirm: boolean) {
            this.rowIndex    = rowIndex;
            this.columnKey = columnKey;
            this.confirm  = confirm;
        }
    }

    class TimeError {
        rowId: string;
        columnId: string;
        constructor(rowId: string, columnId: string) {
            this.rowId = rowId;
            this.columnId = columnId;
        }
    }

    interface IWorkTypeInfomation {
        workTypeDto: IWorkTypeDto,    // 勤務種類選択 - 勤務種類 
        workTimeSetting: number,          // 必須任意不要区分    
        workStyle: number,          // 出勤休日区分
    }

    interface IWorkTypeDto {
        workTypeCode: string, // 勤務種類コード - コード
        name: string,         // 勤務種類名称  - 表示名
        memo: string,
        workTimeSetting: number,         // 必須任意不要区分 
        abbName: string
    }

    interface IEditStateOfDailyAttdDto {
        attendanceItemId: any;
        editStateSetting: any; // enum AttendanceHolidayAttr
        date: any;
    }

    interface IWorkScheduleWorkInforDto {
        employeeId: string; // 社員ID
        date: any; // 年月日
        haveData: boolean; // データがあるか
        achievements: boolean; // 実績か
        confirmed: boolean; // 確定済みか
        needToWork: boolean; // 勤務予定が必要か
        supportCategory: number; // 応援か tu 1-> 5
        //Khu vực Optional
        workTypeCode: string; // 勤務種類コード 
        workTypeName: string; // 勤務種類名
        workTypeEditStatus: IEditStateOfDailyAttdDto; // 勤務種類編集状態
        workTimeCode: string; // 就業時間帯コード
        workTimeName: string; // 就業時間帯名
        workTimeEditStatus: IEditStateOfDailyAttdDto; // 就業時間帯編集状態
        startTime: number; // 開始時刻
        startTimeEditState: IEditStateOfDailyAttdDto; // 開始時刻編集状態
        endTime: number; // 終了時刻
        endTimeEditState: IEditStateOfDailyAttdDto; // 終了時刻編集状態
        workHolidayCls: number; // 出勤休日区分
        isEdit: boolean;
        isActive: boolean;
        workTypeNameIsNull: boolean; 
        workTimeNameIsNull: boolean;
        workTypeIsNotExit : boolean;
        workTimeIsNotExit : boolean;
        conditionAbc1: boolean;
        conditionAbc2: boolean;
        workTimeForm: any;
        condTargetdate: boolean
    }

    interface IWorkScheduleShiftInforDto {
        employeeId: string; // 社員ID
        date: any; // 年月日
        haveData: boolean; // データがあるか
        achievements: boolean; // 実績か
        confirmed: boolean; // 確定済みか
        needToWork: boolean; // 勤務予定が必要か
        supportCategory: number; // 応援か tu 1-> 5
        //Khu vực Optional
        shiftCode: string; // シフトコード
        shiftName: string; // シフト名称
        shiftEditState: IEditStateOfDailyAttdDto; // シフトの編集状態
        workHolidayCls: number; // 出勤休日区分
        conditionAa1: boolean;
        conditionAa2: boolean;
        condTargetdate: boolean
    }

    interface AggreratePersonalDto {
        estimatedSalary: Array<any>;
        timeCount: Array<any>;
        workHours: Array<any>;  
    }
    
    interface AggrerateWorkplaceDto {
        aggrerateNumberPeople: any;
        externalBudget: Array<any>;
        laborCostAndTime: Array<any>;
        timeCount: Array<any>;
        peopleMethod: Array<any>;
    }
    
    enum EditStateSetting {
        HAND_CORRECTION_MYSELF = 0, // 手修正（本人）
        HAND_CORRECTION_OTHER = 1, // 手修正（他人）
        REFLECT_APPLICATION = 2, // 申請反映
        IMPRINT = 3, // 打刻反映
        DECLARE_APPLICATION = 4 // 申告反映
    }

    enum WorkStyle {
        ONE_DAY_REST = 0, //(1日休日系),
        MORNING_WORK = 1, //(午前出勤系),
        AFTERNOON_WORK = 2, //(午後出勤系),
        ONE_DAY_WORK = 3 //(1日出勤系);
    }

    enum SupportCategory {
        NotCheering = 1, // 応援ではない
        TimeSupporter = 2, // 時間帯応援元
        TimeSupport = 3, // 時間帯応援先
        SupportFrom = 4, // 終日応援元
        SupportTo = 5, // 終日応援先
    }

    interface IUserInfor {
        disPlayFormat: string;
        backgroundColor: number; // 背景色
        achievementDisplaySelected: boolean;
        shiftPalletUnit: number;
        shiftPalettePageNumberCom: number;
        shiftPalletPositionNumberCom: {};
        shiftPalettePageNumberOrg: number;
        shiftPalletPositionNumberOrg: {};
        gridHeightSelection: number;
        heightGridSetting: any;
        unit: number;
        workplaceId: string;
        workplaceGroupId: string;
        workPlaceName: string;
        workType: {};
        workTime: {};
        updateMode : string; // updatemode cua grid
        startDate : string;
        endDate : string;
        code: string;
        shiftMasterWithWorkStyleLst : Array<IShiftMasterMapWithWorkStyle>;
        workTypeCodeSelected: any;
        workTimeCodeSelected: any;
        useCategoriesPersonalValue: number; // 個人計カテゴリ
        useCategoriesWorkplaceValue: number; // 職場計カテゴリ
    }
    
    interface IShiftMasterMapWithWorkStyle {
        companyId: string;
        shiftMasterName: string;
        shiftMasterCode: string;
        color: string;
        remark: string;
        workTypeCode: string;
        workTimeCode: string;
        workStyle: string;
    }

    enum AggregationUnitOfLaborCosts {
        TOTAL = 0, // 合計
        WITHIN = 1, // 就業時間
        EXTRA = 2 // 時間外時間
    }

    enum LaborCostItemType {
        AMOUNT = 0, // 金額
        TIME = 1, // 時間
        BUDGET = 2 // 予算
    }

    enum AttendanceTimesForAggregation {
        WORKING_TOTAL = 0, // 総労働時間
        WORKING_WITHIN = 1, // 就業時間
        WORKING_EXTRA = 2, // 時間外時間
        NIGHTSHIFT = 3 // 夜勤時間
    }
    
    enum WorkClassificationAsAggregationTarget {
        WORKING = 0, // 出勤
        HOLIDAY = 1 // 休日
    }
    
    enum PersonalCounterCategory {
        MONTHLY_EXPECTED_SALARY = 0, // 月間想定給与額 
        CUMULATIVE_ESTIMATED_SALARY = 1, // 年間想定給与額
        STANDARD_WORKING_HOURS_COMPARISON = 2, // 基準労働時間比較
        WORKING_HOURS = 3, // 労働時間
        NIGHT_SHIFT_HOURS = 4, // 夜勤時間
        WEEKS_HOLIDAY_DAYS = 5, // 週間休日日数
        ATTENDANCE_HOLIDAY_DAYS = 6, // 出勤・休日日数
        TIMES_COUNTING_1 = 7, // 回数集計１
        TIMES_COUNTING_2 = 8, // 回数集計２
        TIMES_COUNTING_3 = 9, // 回数集計３
    }
    
    enum WorkplaceCounterCategory {
        LABOR_COSTS_AND_TIME = 0, // 人件費・時間 
        EXTERNAL_BUDGET = 1, // 外部予算実績
        TIMES_COUNTING = 2, // 回数集計
        WORKTIME_PEOPLE = 3, // 就業時間帯別の利用人数
        TIMEZONE_PEOPLE = 4, // 時間帯人数
        EMPLOYMENT_PEOPLE = 5, // 雇用人数
        CLASSIFICATION_PEOPLE = 6, // 分類人数
        POSITION_PEOPLE = 7, // 職位人数
    }
    
    enum DisplayPeriod {
        ANY_PERIOD = 1, // 任意期間
        TH28 = 2, // ２８日  
        END = 3, // 末日
    }

    enum InnerIndex {
        STARTTIME = 2,
        ENDTIME = 3,
    }
    
    enum MODE {
        SCHEDULE = 2,
        ACTUAL = 1,
    }

    interface IError {
        sid: string,
        scd: string,
        empName: string,
        date: string,
        attendanceItemId: string,
        errorMessage: string,
    }

    export class InforError {
        sid: string;
        scd: string;
        empName: string;
        date: string;
        attendanceItemId: string;
        errorMessage: string;
        constructor(param: IError) {
            let self = this;
            self.sid = param.sid;
            self.scd = param.scd;
            self.empName = param.empName;
            self.date = param.date;
            self.attendanceItemId = param.attendanceItemId;
            self.errorMessage = param.errorMessage;
        }
    }
}