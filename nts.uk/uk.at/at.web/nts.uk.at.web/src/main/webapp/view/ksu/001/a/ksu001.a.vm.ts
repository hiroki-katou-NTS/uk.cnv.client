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

    /**
     * load screen O->Q->A
     * reference file a.start.ts
     */
    export class ScreenModel {

        employeeIdLogin: string = __viewContext.user.employeeId;
        key: string;
        rowIndexOfEmpLogin : number;

        empItems: KnockoutObservableArray<PersonModel> = ko.observableArray([]);
        
        enableBtnReg : KnockoutObservable<boolean> = ko.observable(false);
        visibleShiftPalette: KnockoutObservable<boolean> = ko.observable(true);
        mode: KnockoutObservable<string> = ko.observable('edit'); // edit || confirm 
        showA9: boolean;

        // A4 popup-area6 
        // A4_4
        selectedModeDisplayInBody: KnockoutObservable<number> = ko.observable(undefined);

        // A4_7
        achievementDisplaySelected: KnockoutObservable<number> = ko.observable(undefined); // 1 || 2

        // A4_12
        backgroundColorSelected: KnockoutObservable<string> = ko.observable(undefined);  // 0 || 1
        showComboboxA4_12: KnockoutObservable<boolean> = ko.observable(true);

        isEnableCompareMonth: KnockoutObservable<boolean> = ko.observable(true);

        popupVal: KnockoutObservable<string> = ko.observable('');
        selectedDate: KnockoutObservable<string> = ko.observable('');

        //Date time A3_1

        currentDate: Date = new Date();
        dtPrev: KnockoutObservable<Date> = ko.observable(new Date()); // A3_1_2
        dtAft: KnockoutObservable<Date> = ko.observable(new Date());  // A3_1_4
        dateTimePrev: KnockoutObservable<string>;
        dateTimeAfter: KnockoutObservable<string>;

        //Switch  A3_2
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

        affiliationId: any = null;
        affiliationName: KnockoutObservable<string> = ko.observable('');
        dataWScheduleState: KnockoutObservableArray<WorkScheduleState> = ko.observableArray([]);
        listStateWorkTypeCode: KnockoutObservableArray<any> = ko.observableArray([]);
        listCheckNeededOfWorkTime: KnockoutObservableArray<any> = ko.observableArray([]);
        dataWkpSpecificDate: KnockoutObservableArray<any> = ko.observableArray([]);
        dataComSpecificDate: KnockoutObservableArray<any> = ko.observableArray([]);
        dataPublicHoliday: KnockoutObservableArray<any> = ko.observableArray([]);
        dataWorkEmpCombine: KnockoutObservableArray<any> = ko.observableArray([]);
        dataScheduleDisplayControl: KnockoutObservable<any> = ko.observableArray([]);
        isInsuranceStatus: boolean = false;

        flag: boolean = true;
        arrLockCellInit: KnockoutObservableArray<Cell> = ko.observableArray([]);
        // 表示形式 ＝ 日付別(固定) = 0
        displayFormat: KnockoutObservable<number> = ko.observable(0);
        hasEmployee: KnockoutObservable<boolean> = ko.observable(false);
        KEY: string = 'nts.uk.characteristics.ksu001Data';
        dataCell: any; // data để paste vào grid
        listPageInfo : any;
        targetShiftPalette : any;
        workPlaceId : any;
        
        // data grid
        arrListCellLock = [];
        listCellNotEditBg = [];
        listCellNotEditColor = [];
        detailContentDeco = [];
        detailContentDecoModeConfirm = [];
        detailColumns = [];
        detailContentDs = [];
        dataSource = {};
        listEmpInfo = [];
        listWorkScheduleWorkInfor  = [];
        listWorkScheduleShift      = [];
        listPersonalConditions     = [];
        displayControlPersonalCond = {};
        listDateInfo     = [];
        listBgOfCellSelfOther     = [];
        updatedCellsInModeShift   = [];
        
        showTeamCol = false;
        showRankCol = false;
        showQualificCol = false;
        widthMid : number = 0;
        numberCellsUpdatedAfterUndo = 0;
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
        
        listTimeDisable = []; // lưu nhưng cell bí disalble do không có worktime
        
        // dùng cho trường hợp thay đổi modeBackground
        hasChangeModeBg = false; 
        listCellUpdatedWhenChangeModeBg = [];
        
        listWorkTypeInfo = [];// listWorkTypecombobox
        listCellRetained = [];
        listCellError = []; // chưa những cell not valid khi sửa time 
        
        widthA8 : number = 200;
        widthBtnToLeftToRight : number = 32;
        distanceLeftToGrid : number = 30;
        timeColor = '#595959';
        
        constructor() {
            let self = this;

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
            
            self.selectedDisplayPeriod.subscribe(function(value) { // value = 1 || 2 || 3
                if (value == null || value == undefined || value == 2)
                    return;
                if (value == 3) {
                    let date = new Date(self.dateTimeAfter());
                    let firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
                    let lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
                    
                    let startDate = moment(firstDay).format('YYYY/MM/DD');
                    let endDate   = moment(lastDay).format('YYYY/MM/DD');
                    
                    if ((self.dateTimeAfter() != endDate) || (self.dateTimePrev() != startDate)) {
                        self.getDataWhenChangeModePeriod(startDate, endDate);
                    }
                }
            });

            self.achievementDisplaySelected.subscribe(function(newValue) {
                if(newValue == null || newValue == undefined)
                    return;
                
                if(self.flag == true)
                    return;
                
                if (newValue == 1) {
                    self.isEnableCompareMonth(true);
                    
                } else {
                    self.isEnableCompareMonth(false);
                }
                uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                    let userInfor = JSON.parse(data);
                    userInfor.achievementDisplaySelected = (newValue == 1) ? true : false;
                    uk.localStorage.setItemAsJson(self.KEY, userInfor);
                });
                nts.uk.ui.block.grayout();
                let viewMode = self.selectedModeDisplayInBody();
                self.getNewData(viewMode).done(() => {
                    if (self.mode() == 'confirm') {
                        $("#extable").exTable("updateMode", "determine");
                        $(".editMode").addClass("A6_not_hover").removeClass("A6_hover");
                        $(".confirmMode").addClass("A6_hover").removeClass("A6_not_hover");
                        if (self.selectedModeDisplayInBody() == 'time' || self.selectedModeDisplayInBody() == 'shortName') {
                            // disable combobox workType, workTime
                            __viewContext.viewModel.viewAB.enableListWorkType(false);
                            __viewContext.viewModel.viewAB.disabled(true);
                        } else {
                            self.shiftPalletControlDisable();
                        }
                        self.confirmMode();
                    }
                    self.mode() === 'edit' ? self.editMode() : self.confirmMode();
                    if (self.mode() == 'edit' && self.selectedModeDisplayInBody() == 'time'){
                        self.diseableCellsTime();    
                    }
                    
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    nts.uk.ui.block.clear();
                });
            });

            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor: IUserInfor = JSON.parse(data);
                // A4_4 表示形式の初期選択と画面モード (Chọn default của các hình thức hiển thị và mode màn hình)
                if (userInfor.disPlayFormat == 'shift') {
                    self.selectedModeDisplayInBody('shift');
                    self.visibleShiftPalette(true);
                } else if (userInfor.disPlayFormat == 'shortName') {
                    self.selectedModeDisplayInBody('shortName');
                    self.visibleShiftPalette(false);
                } else if (userInfor.disPlayFormat == 'time') {
                    self.selectedModeDisplayInBody('time');
                    self.visibleShiftPalette(false);
                }
            }).ifEmpty((data) => {
                self.selectedModeDisplayInBody('time');
                self.visibleShiftPalette(false);
            });

            self.selectedModeDisplayInBody.subscribe(function(viewMode) {
                if (viewMode == null)
                    return;
                if(self.flag == true)
                    return;
                
                nts.uk.ui.errors.clearAll();
                nts.uk.ui.block.grayout();
                // close screen O1 when change mode
                if (viewMode == 'shift') { // mode シフト表示   
                    self.shiftModeStart().done(() => {
                        self.mode() === 'edit' ? self.editMode() : self.confirmMode();
                        nts.uk.ui.block.clear();
                    });
                } else if (viewMode == 'shortName') { // mode 略名表示
                    self.shortNameModeStart().done(() => {
                        self.mode() === 'edit' ? self.editMode() : self.confirmMode();
                        nts.uk.ui.block.clear();
                    });
                } else if (viewMode == 'time') {  // mode 勤務表示 
                    self.timeModeStart().done(() => {
                        self.mode() === 'edit' ? self.editMode() : self.confirmMode();
                        self.diseableCellsTime();
                        nts.uk.ui.block.clear();
                    });
                }
            });

            self.backgroundColorSelected.subscribe((value) => {
                if (util.isNullOrUndefined(value) || util.isNullOrEmpty(value))
                    return;
                // update lại màu background phần detail
                let self = this;
                if (self.flag == true)
                    return;
                self.hasChangeModeBg = true;
                let shiftMasterWithWorkStyleLst;
                let detailContentDeco = [];
                nts.uk.ui.block.grayout();
                let updatedCells = $("#extable").exTable('updatedCells');
                if (updatedCells.length > 0) {
                    self.updatedCellsInModeShift = $.merge(self.updatedCellsInModeShift, updatedCells);
                    _.forEach(self.updatedCellsInModeShift, function(itemUpdate) {
                        // loại bỏ những item đã update mà nằm trong danh sách listBgOfCellSelfOther. 
                        _.remove(self.listBgOfCellSelfOther, function(n) {
                            return n.columnKey === itemUpdate.columnKey && n.rowId === (itemUpdate.rowIndex + '');
                        });
                    });

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
                }

                uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                    let userInfor: IUserInfor = JSON.parse(data);
                    userInfor.backgroundColor = value;
                    shiftMasterWithWorkStyleLst = userInfor.shiftMasterWithWorkStyleLst;
                    uk.localStorage.setItemAsJson(self.KEY, userInfor);
                });
                
                if (value == 0) {
                    detailContentDeco = self.listBgOfCellSelfOther;
                }
                
                // khi chuyển mode backGround thì gọi hàm updateGrid, sau khi gọi hàm này thì những cell đã edit trc đo không còn nữa, nên disable 2 button này đi.
                self.enableBtnUndo(false);
                self.enableBtnRedo(false);

                // lay data tai thời điểm chuyển combobox
                let dataSource = $("#extable").exTable('dataSource', 'detail').body;

                for (let i = 0; i < self.listEmpInfo.length; i++) {
                    let rowId = i + '';
                    let emp: IEmpInfo = self.listEmpInfo[i];
                    let objDetailContentDs = new Object();

                    let listWorkScheduleShiftByEmp: Array<IWorkScheduleShiftInforDto> = _.filter(self.listWorkScheduleShift, function(workSchedul: IWorkScheduleShiftInforDto) { return workSchedul.employeeId === emp.employeeId });
                    let dataOnGrid: any = _.filter(dataSource, function(workSchedul: any) { return workSchedul.employeeId === emp.employeeId })[0];

                    // set data to detailContent : datasource v        
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleShiftByEmpSort = _.orderBy(listWorkScheduleShiftByEmp, ['date'], ['asc']);

                    let item = uk.localStorage.getItem(self.KEY);
                    let userInfor: IUserInfor = JSON.parse(item.get());
                    let shiftMasterWithWorkStyleLst = userInfor.shiftMasterWithWorkStyleLst;

                    for (let j = 0; j < listWorkScheduleShiftByEmpSort.length; j++) {
                        let cell: IWorkScheduleShiftInforDto = listWorkScheduleShiftByEmpSort[j];
                        let rowOfSelf = cell.employeeId == self.employeeIdLogin ? true : false;
                        let time = new Time(new Date(cell.date));
                        let ymd = time.yearMonthDay;

                        let dataCellOnGrid = dataOnGrid['_' + ymd];

                        // set Deco background
                        if (value == 1) {
                            let shiftMasterWithWorkStyleLst = userInfor.shiftMasterWithWorkStyleLst;
                            if (dataCellOnGrid.shiftCode != null) {
                                let objShiftMasterWithWorkStyle = _.filter(shiftMasterWithWorkStyleLst, function(o) { return o.shiftMasterCode == dataCellOnGrid.shiftCode; });
                                if (objShiftMasterWithWorkStyle.length > 0) {
                                    let color = '#' + objShiftMasterWithWorkStyle[0].color;
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, color, 0));
                                } else {
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "background-white", 0));
                                }
                            }
                        } else if (value == 0) {
                            let updatedCellsByEmpLogin  = _.filter(self.updatedCellsInModeShift, function(o) { return o.rowIndex === self.rowIndexOfEmpLogin });
                            let updatedCellsByEmpOther  = _.filter(self.updatedCellsInModeShift, function(o) { return o.rowIndex !=  self.rowIndexOfEmpLogin });
                            // set Deco BackGround
                            let rowIndex = _.indexOf(self.listSid(), cell.employeeId);
                            let updatedCellByEmpLogin = _.filter(updatedCellsByEmpLogin, function(o) { return o.columnKey === '_' + ymd && o.rowIndex === rowIndex });
                            if (updatedCellByEmpLogin.length > 0) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                            }
                            
                            let updatedCellByEmpOther = _.filter(updatedCellsByEmpOther, function(o) { return o.columnKey === '_' + ymd && o.rowIndex === rowIndex });
                            if (updatedCellByEmpOther.length > 0) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                            }
                        }

                        // set Deco text color
                        // A10_color⑥ スケジュール明細の文字色  (Màu chữ của "Schedule detail")  
                        if (util.isNullOrUndefined(dataCellOnGrid.shiftCode) || util.isNullOrEmpty(dataCellOnGrid.shiftCode)) {
                            // デフォルト（黒）  Default (black)
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                        } else {
                            let cellCanNotEdit = _.filter(self.listCellNotEditColor, function(o) { return o.columnKey == '_' + ymd && o.rowId == rowId; });
                            if (cellCanNotEdit.length > 0 && value == 0 && self.achievementDisplaySelected() == 1 && dataCellOnGrid.achievements == true) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                            } else {
                                let workStyle = self.getWorkStyle(shiftMasterWithWorkStyleLst, dataCellOnGrid.shiftCode);
                                let cellColor = self.setColorCell(workStyle, ymd, rowId);
                                detailContentDeco.push(cellColor);
                            }
                        }
                    };
                }

                self.updateExTableWhenChangeModeBg($.merge(detailContentDeco, self.listCellNotEditBg));

                self.setIconEventHeader();

                let item = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor = JSON.parse(item.get());
                if (userInfor.updateMode == 'copyPaste') {
                    self.coppyData();
                }
                nts.uk.ui.block.clear();
            });
         
            self.showComboboxA4_12 = ko.computed(function() {
                return self.selectedModeDisplayInBody() == 'shift' && self.mode() == 'edit' ;
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = {};
            if (item.isPresent()) {
                userInfor = JSON.parse(item.get());
            }
            let viewMode = item.isPresent() ? userInfor.disPlayFormat : 'time';
            let updateMode = item.isPresent() ? userInfor.updateMode : 'stick';
            
            let param = {
                viewMode: viewMode,
                startDate: null,
                endDate  : null,
                shiftPalletUnit: item.isPresent() ? userInfor.shiftPalletUnit : 1, // 1: company , 2 : workPlace 
                pageNumberCom: item.isPresent() ? userInfor.shiftPalettePageNumberCom : 1,
                pageNumberOrg: item.isPresent() ? userInfor.shiftPalettePageNumberOrg : 1,
                getActualData: false,
                listShiftMasterNotNeedGetNew: item.isPresent() ? userInfor.shiftMasterWithWorkStyleLst : [], // List of shifts không cần lấy mới
                listSid: self.listSid(),
                unit: item.isPresent() ? userInfor.unit : 0,
                workplaceId     : null,
                workplaceGroupId: null,
            }

            service.getDataStartScreen(param).done((data: IDataStartScreen) => {
                // ẩn hiển A1_7 ※1
                if (data.dataBasicDto.useWorkAvailabilityAtr == false) {
                    $('#A1_7').css('visibility','hidden');
                    $('#A1_7_1').css('display','none');
                    $('#A1_7').off('click');
                }
                
                // ẩn hiện A1_5 ※27
                if (data.dataBasicDto.usePublicAtr == false) {
                    $('#A1_5').css('visibility','hidden');
                    $('#A1_5').off('click');
                }
                
                self.scheduleModifyStartDate = data.dataBasicDto.scheduleModifyStartDate;
                self.saveDataGrid(data);

                // khởi tạo data localStorage khi khởi động lần đầu.
                self.creatDataLocalStorege(data.dataBasicDto);
                
                __viewContext.viewModel.viewAB.filter(data.dataBasicDto.unit == 0 ? true : false);
                __viewContext.viewModel.viewAB.workplaceIdKCP013(data.dataBasicDto.unit == 0 ? data.dataBasicDto.workplaceId : data.dataBasicDto.workplaceGroupId);
                
                self.getSettingDisplayWhenStart(viewMode, true);

                if (viewMode == 'shift') {
                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                    self.bingdingToShiftPallet(data);
                }
                
                // set data Header
                self.bindingToHeader(data);
                
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, viewMode);
                self.initExTable(dataBindGrid, viewMode, updateMode);
                
                self.setUpdateMode();
                self.setDataWorkType(data.listWorkTypeInfo);
                self.checkEnableCombWTime();
                self.setHeightScreen();
                self.setPositionButonToRightToLeft();
                self.bindingEventClickFlower();
                self.setTextResourceA173();
                if (viewMode == 'time') {
                    self.diseableCellsTime();
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
        
        getDataWhenChangeModePeriod(startDate, endDate) {
            let self = this;
            let viewMode = self.selectedModeDisplayInBody();
            nts.uk.ui.block.grayout();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());

            let param = {
                viewMode: self.selectedModeDisplayInBody(), // time | shortName | shift
                startDate: startDate,
                endDate: endDate,
                workplaceId: userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId,
                unit: userInfor.unit,
                getActualData: item.isPresent() ? userInfor.achievementDisplaySelected : false,
                listShiftMasterNotNeedGetNew: userInfor.shiftMasterWithWorkStyleLst,
                listSid: self.listSid()
            };

            service.getDataWhenChangeModePeriod(param).done((data: any) => {
                if (userInfor.disPlayFormat == 'shift') {
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
                    listWorkScheduleShift: data.listWorkScheduleShift
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());

                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());

                self.setUpdateMode();
                
                if (self.mode() == 'confirm') {
                    $("#extable").exTable("updateMode", "determine");
                }

                self.setPositionButonToRightToLeft();
                
                // fix bug khong coppyPaste dc 
                if (userInfor.updateMode == 'copyPaste') {
                    $("#extable").exTable("updateMode", "stick");
                    $("#extable").exTable("updateMode", "copyPaste");
                }

                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
        }

        getNewData(viewMode): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            if (viewMode == 'shift') { // mode シフト表示   
                self.shiftModeStart().done(() => {
                    dfd.resolve();
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    dfd.reject();
                });
            } else if (viewMode == 'shortName') { // mode 略名表示
                self.shortNameModeStart().done(() => {
                    dfd.resolve();
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    dfd.reject();
                });
            } else if (viewMode == 'time') {  // mode 勤務表示 
                self.timeModeStart().done(() => {
                    dfd.resolve();
                    nts.uk.ui.block.clear();
                }).fail(function() {
                    dfd.reject();
                });
            }
            return dfd.promise();
        }

        creatDataLocalStorege(dataBasic: IDataBasicDto) {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            if (!item.isPresent()) {
                let userInfor: IUserInfor = {};
                userInfor.disPlayFormat = self.selectedModeDisplayInBody();
                userInfor.backgroundColor = 0; // 0 : 通常; 1: シフト   // mau nền default của shiftMode
                userInfor.achievementDisplaySelected = false;
                userInfor.shiftPalletUnit = 1;
                userInfor.shiftPalettePageNumberCom = 1;
                userInfor.shiftPalletPositionNumberCom = { column : 0 , row : 0 };
                userInfor.shiftPalettePageNumberOrg = 1;
                userInfor.shiftPalletPositionNumberOrg = { column : 0 , row : 0 };
                userInfor.gridHeightSelection = 1;
                userInfor.heightGridSetting = '';
                userInfor.unit = dataBasic.unit;
                userInfor.workplaceId= dataBasic.workplaceId;
                userInfor.workplaceGroupId = dataBasic.workplaceGroupId;
                userInfor.workPlaceName = dataBasic.targetOrganizationName;
                userInfor.code = dataBasic.code;
                userInfor.workType = {};
                userInfor.workTime = {};
                userInfor.shiftMasterWithWorkStyleLst = [];
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            } else {
                uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                    let userInfor: IUserInfor = JSON.parse(data);
                    userInfor.achievementDisplaySelected = false;
                    uk.localStorage.setItemAsJson(self.KEY, userInfor);
                });
            }
        }
        
        setUpdateMode() {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = {};
            if (item.isPresent()) {
                userInfor = JSON.parse(item.get());
            }
            let updateMode = item.isPresent() ? (userInfor.updateMode == undefined ? 'stick' : userInfor.updateMode) : 'stick';

            if (updateMode == 'stick') {
                self.pasteData();
            } else if (updateMode == 'copyPaste') {
                self.coppyData();
            } else if (updateMode == 'edit') {
                self.inputData();
            }
        }

        shiftModeStart(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            let param = {
                viewMode : 'shift',
                startDate: self.dateTimePrev() ,
                endDate  : self.dateTimeAfter(),
                shiftPalletUnit : userInfor.shiftPalletUnit, // 1: company , 2 : workPlace 
                pageNumberCom   : userInfor.shiftPalettePageNumberCom,
                pageNumberOrg   : userInfor.shiftPalettePageNumberOrg,
                getActualData   : item.isPresent() ? userInfor.achievementDisplaySelected : false, 
                listShiftMasterNotNeedGetNew: userInfor.shiftMasterWithWorkStyleLst, // List of shifts không cần lấy mới
                listSid: self.listSid(),
                unit: item.isPresent() ? userInfor.unit : 0,
                workplaceId     : userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId
            };
            self.saveModeGridToLocalStorege('shift');
            self.visibleShiftPalette(true);
            self.visibleBtnInput(false);
            service.getDataOfShiftMode(param).done((data: IDataStartScreen) => {
                
                self.saveDataGrid(data);
                // set hiển thị ban đầu theo data đã lưu trong localStorege
                self.getSettingDisplayWhenStart('shift', false);
                //WORKPLACE(0), //WORKPLACE_GROUP(1);
                __viewContext.viewModel.viewAC.workplaceModeName(data.dataBasicDto.designation);
                 $('#Aa1_2 > label:nth-child(2) > span').html(data.dataBasicDto.designation);

                self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                // set data shiftPallet
                __viewContext.viewModel.viewAC.flag = false;
                
                __viewContext.viewModel.viewAC.selectedpalletUnit(userInfor.shiftPalletUnit);
                if(userInfor.shiftPalletUnit == 1){
                    __viewContext.viewModel.viewAC.handleInitCom(data.listPageInfo,data.targetShiftPalette.shiftPalletCom,userInfor.shiftPalettePageNumberCom);
                }else{
                    __viewContext.viewModel.viewAC.handleInitWkp(data.listPageInfo,data.targetShiftPalette.shiftPalletWorkPlace,userInfor.shiftPalettePageNumberOrg);
                }
                __viewContext.viewModel.viewAC.flag = true;

                if (__viewContext.viewModel.viewAC.listPageComIsEmpty == true) {
                    $('.ntsButtonTableButton').addClass('nowithContent');
                }
                if (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true) {
                    $('.ntsButtonTableButton').addClass('nowithContent');
                }

                if (self.mode() == 'confirm') {
                    self.shiftPalletControlDisable();
                }
                
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, 'shift');
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, 'shift');
                
                self.pasteData();
                
                self.setPositionButonToRightToLeft();
                
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            let setWorkTypeTime = userInfor.disPlayFormat == 'shift' ? true : false;
            let param = {
                viewMode: 'shortName',
                startDate: self.dateTimePrev(),
                endDate:   self.dateTimeAfter() ,
                getActualData: item.isPresent() ? userInfor.achievementDisplaySelected : false,
                unit: item.isPresent() ? userInfor.unit : 0,
                workplaceId     : userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId
            };
            
            self.visibleShiftPalette(false);
            self.visibleBtnInput(false);
            self.saveModeGridToLocalStorege('shortName');
            
            service.getDataOfShortNameMode(param).done((data: IDataStartScreen) => {
                if (setWorkTypeTime) {
                    self.setWorkTypeTime(data.listWorkTypeInfo, userInfor);
                }
                
                self.saveDataGrid(data);
                // set hiển thị ban đầu theo data đã lưu trong localStorege
                self.getSettingDisplayWhenStart('shortName',false);
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, 'shortName');
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, 'shortName');
                
                self.pasteData();
                
                self.setPositionButonToRightToLeft();
                
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            let setWorkTypeTime = userInfor.disPlayFormat == 'shift' ? true : false;
            let param = {
                viewMode: 'time',
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                getActualData: item.isPresent() ? userInfor.achievementDisplaySelected : false,
                unit: item.isPresent() ? userInfor.unit : 0,
                workplaceId     : userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId
            };

            self.visibleShiftPalette(false);
            self.visibleBtnInput(true);
            
            self.saveModeGridToLocalStorege('time');
            service.getDataOfTimeMode(param).done((data: IDataStartScreen) => {
                if (setWorkTypeTime) {
                    self.setWorkTypeTime(data.listWorkTypeInfo, userInfor);
                }
                
                self.saveDataGrid(data);
                // set hiển thị ban đầu theo data đã lưu trong localStorege
                self.getSettingDisplayWhenStart('time', false);
                // set data Grid
                let dataBindGrid = self.convertDataToGrid(data, 'time');
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, 'time');

                self.pasteData();

                self.setPositionButonToRightToLeft();

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
            if (self.mode() == 'edit') {
                __viewContext.viewModel.viewAB.enableListWorkType(true);
            } else if (self.mode() == 'confirm'){

            }
            
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = {};
            if (item.isPresent()) {
                userInfor = JSON.parse(item.get());
            }
            
            let workTypeCodeSave = item.isPresent() ? userInfor.workTypeCodeSelected : '';
            let workTimeCodeSave = item.isPresent() ? userInfor.workTimeCodeSelected : '';
            
            let workTimeCode = '';
            if (workTimeCodeSave != '') {
                if (workTimeCodeSave === 'none') {
                    workTimeCode = '';
                } else if (workTimeCodeSave === 'deferred') {
                    workTimeCode = ' ';
                } else {
                    workTimeCode = workTimeCodeSave;
                }
            }
            self.setDataWorkType(listWorkTypeInfo);
            __viewContext.viewModel.viewAB.selectedWorkTypeCode(workTypeCodeSave);
            __viewContext.viewModel.viewAB.selected(workTimeCode);
            __viewContext.viewModel.viewAB.workplaceIdKCP013(userInfor.unit == 0 ? userInfor.workplaceId : userInfor.workplaceGroupId);
            __viewContext.viewModel.viewAB.filter(userInfor.unit == 0 ? true : false);
        }
        
        checkEnableCombWTime() {
            let self = this;
            if (self.selectedModeDisplayInBody() == 'shift')
                return;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor = {};
            if (item.isPresent()) {
                userInfor = JSON.parse(item.get());
            }
            
            let workTypeCodeSave = item.isPresent() ? userInfor.workTypeCodeSelected : '';
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
        
        destroyAndCreateGrid(dataBindGrid,viewMode){
            let self = this;
            $("#extable").children().remove();
            $("#extable").removeData();
            let extable = $("#extable")[0];
            $("#extable").replaceWith(extable.cloneNode(true));
            let updateMode = self.mode() === 'edit' ? 'stick' : 'determine'
            self.initExTable(dataBindGrid, viewMode, updateMode);
            if (!self.showA9) {
                $(".toLeft").css("display", "none");
            }
            self.bindingEventCellUpdatedGrid();
        }
        
        bindingEventCellUpdatedGrid() {
            let self = this;
            $("#extable").on("extablecellupdated", (dataCell) => {
                let itemLocal = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor = JSON.parse(itemLocal.get());
                if (userInfor.disPlayFormat == 'time' && userInfor.updateMode == 'edit') {
                    self.validTimeInEditMode(dataCell, userInfor, false);
                } else if (userInfor.disPlayFormat == 'time' && userInfor.updateMode == 'stick') {
                    // check xem cell vừa được stick data có nằm trong list cell lỗi do edit time hay không, nếu nằm trong list đấy thì rmove cell đó khỏi list lỗi đi.
                    self.validTimeStickMode(dataCell, userInfor);
                }  else if (userInfor.disPlayFormat == 'time' && userInfor.updateMode == 'copyPaste') {
                    // check xem cell vừa được stick data có nằm trong list cell lỗi do edit time hay không, nếu nằm trong list đấy thì rmove cell đó khỏi list lỗi đi.
                    self.validTimeCopyPaste(dataCell, userInfor);
                } else {
                    
                    self.checkExitCellUpdated();
                }
            });

            $("#extable").on("extablecellretained", (dataCell) => {
                let itemLocal = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor = JSON.parse(itemLocal.get());
                if (userInfor.disPlayFormat == 'time' && userInfor.updateMode == 'edit') {
                    self.addCellRetaine(dataCell);
                    self.validTimeInEditMode(dataCell, userInfor, true);
                }
            });

            $("#extable").on("extablerowupdated", (dataCell) => {
                self.checkExitCellUpdated();
            });
        }

        addCellRetaine(dataCellRetaine: any) {
            let self = this;
            let startTime, endTime, workTypeCode, workTimeCode;
            let rowIndex = dataCellRetaine.originalEvent.detail.rowIndex;
            let columnKey = dataCellRetaine.originalEvent.detail.columnKey;
            let innerIdx = dataCellRetaine.originalEvent.detail.innerIdx;
            let dataSource = $("#extable").exTable('dataSource', 'detail').body;
            let cellDataOnGrid = dataSource[rowIndex][columnKey];
            workTypeCode = cellDataOnGrid.workTypeCode;
            workTimeCode = cellDataOnGrid.workTimeCode;
            if (innerIdx == 3) {
                endTime = dataCellRetaine.originalEvent.detail.value;
                startTime = cellDataOnGrid.startTime;
            } else if (innerIdx == 2) {
                startTime = dataCellRetaine.originalEvent.detail.value;
                endTime = cellDataOnGrid.endTime;
            }
            
            let objRetaind = {rowIndex: rowIndex, columnKey: columnKey, startTime: startTime, endTime: endTime, workTypeCode: workTypeCode, workTimeCode: workTimeCode}
            
            let exit = _.filter(self.listCellRetained, function(o) { return o.rowIndex == rowIndex && o.columnKey == columnKey; });
            if (exit.length > 0) {
                _.remove(self.listCellRetained, function(e) {
                    return e.rowIndex == rowIndex && e.columnKey == columnKey;
                });
                self.listCellRetained.push(objRetaind);
            } else {
                self.listCellRetained.push(objRetaind);
            }
        }
        
        validTimeInEditMode(dataCellUpdated: any, userInfor: any, isRetaine: boolean) {
            let self = this;
            let strTime, endTime, workTypeCode, workTimeCode, rowIndex, columnKey;
            rowIndex = dataCellUpdated.originalEvent.detail.rowIndex;
            columnKey = dataCellUpdated.originalEvent.detail.columnKey;
            if (!isRetaine) {
                strTime = dataCellUpdated.originalEvent.detail.value.startTime;
                endTime = dataCellUpdated.originalEvent.detail.value.endTime;
            } else {
                let dataSource = $("#extable").exTable('dataSource', 'detail').body;
                let innerIdx = dataCellUpdated.originalEvent.detail.innerIdx;
                let cellData = dataSource[rowIndex][columnKey];
                workTypeCode = cellData.workTypeCode;
                workTimeCode = cellData.workTimeCode;
                if (innerIdx == 3) {
                    endTime = dataCellUpdated.originalEvent.detail.value;
                    strTime = cellData.startTime;
                } else if (innerIdx == 2) {
                    strTime = dataCellUpdated.originalEvent.detail.value;
                    endTime = cellData.endTime;
                }
            }

            let startTimeCal = nts.uk.time.minutesBased.duration.parseString(strTime).toValue();
            let endTimeCal = nts.uk.time.minutesBased.duration.parseString(endTime).toValue();

            if (startTimeCal < 0 && endTimeCal < 0) {
                startTimeCal = startTimeCal * -1;
                endTimeCal = endTimeCal * -1;
            }

            if (startTimeCal >= endTimeCal) {
                self.addCellNotValidInTimeInputMode(rowIndex+'', columnKey);
                self.checkExitCellUpdated();
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_54' });
                return;
            }

            if (strTime == '' || endTime == '' || _.isNaN(startTimeCal) || _.isNaN(endTimeCal)) {
                self.addCellNotValidInTimeInputMode(rowIndex+'', columnKey);
                self.checkExitCellUpdated();
                return;
            }
            
            if(isRetaine == true){
                self.removeCellNotValidInTimeInputMode(rowIndex+'', columnKey);
            } 

            nts.uk.ui.block.grayout();
            let param = {
                workType: isRetaine == true ? workTypeCode : dataCellUpdated.originalEvent.detail.value.workTypeCode,
                workTime: isRetaine == true ? workTimeCode : dataCellUpdated.originalEvent.detail.value.workTimeCode,
                workTime1: {
                    startTime: {
                        time: startTimeCal,
                        dayDivision: 0
                    },
                    endTime: {
                        time: endTimeCal,
                        dayDivision: 0
                    }
                },
                workTime2: null
            }

            // call alg : <<Query>> 時刻が不正かチェックする 6666
            service.checkTimeIsIncorrect(param).done((result) => {
                let errors = [];
                for (let i = 0; i < result.length; i++) {
                    if (!result[i].check) {
                        if (result[i].timeSpan == null) {
                            errors.push({
                                message: nts.uk.resource.getMessage('Msg_439', getText('KDL045_12')),
                                messageId: "Msg_439",
                                supplements: {}
                            });
                        } else {
                            if (result[i].timeSpan.startTime == result[i].timeSpan.endTime) {
                                errors.push({
                                    message: nts.uk.resource.getMessage('Msg_2058', [result[i].nameError, formatById("Clock_Short_HM", result[i].timeSpan.startTime)]),
                                    messageId: "Msg_2058",
                                    supplements: {}
                                });
                            } else {
                                errors.push({
                                    message: nts.uk.resource.getMessage('Msg_1772', [result[i].nameError, formatById("Clock_Short_HM", result[i].timeSpan.startTime), formatById("Clock_Short_HM", result[i].timeSpan.endTime)]),
                                    messageId: "Msg_1772",
                                    supplements: {}
                                });
                            }
                        }
                    }
                }

                if (errors.length > 0) {
                    nts.uk.ui.block.clear();
                    self.enableBtnReg(false);
                    let errorsInfo = _.uniqBy(errors, x => { return x.message });
                    self.addCellNotValidInTimeInputMode(rowIndex+'', columnKey);
                    self.checkExitCellUpdated();
                    bundledErrors({ errors: errorsInfo }).then(() => {});
                } else {
                    self.removeCellNotValidInTimeInputMode(rowIndex+'', columnKey);
                    self.checkExitCellUpdated();
                    nts.uk.ui.block.clear();
                }
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
        }
        
        validTimeStickMode(dataCellUpdated: any, userInfor: any) {
            let self = this;
            let rowIndex = dataCellUpdated.originalEvent.detail.rowIndex;
            let columnKey = dataCellUpdated.originalEvent.detail.columnKey;
             // vi data stick là data khong sai được, nên là nếu stick vào những cell nằm trong list cell sửa tay bị lỗi. thì xóa cell đó khoi list error đi.
            _.remove(self.listCellError, function(cell) {
                return cell.rowId == rowIndex && cell.columnId == columnKey;
            });
            self.checkExitCellUpdated();
        }

        validTimeCopyPaste(dataCellUpdated: any, userInfor: any) {
            let self = this;
            let rowIndex = dataCellUpdated.originalEvent.detail.rowIndex;
            let columnKey = dataCellUpdated.originalEvent.detail.columnKey;
            // copy paste hiện tại đang không lấy được cell nguồn
            // nên là đang khong biết có coppy từ cell bị lỗi hay không.
            
        }
        
        saveDataGrid(data: any) {
            let self = this;
            self.listEmpInfo = data.listEmpInfo;
            self.listWorkScheduleWorkInfor = data.listWorkScheduleWorkInfor;
            self.listWorkScheduleShift = data.listWorkScheduleShift;
            self.listPersonalConditions = data.listPersonalConditions;
            self.displayControlPersonalCond = data.displayControlPersonalCond;
            self.listDateInfo = data.listDateInfo;
        }

        // binding ket qua cua <<ScreenQuery>> 初期起動の情報取得 
        bindingToHeader(data: IDataStartScreen) {
            let self = this;
            let dataBasic: IDataBasicDto = data.dataBasicDto;
            self.dtPrev(dataBasic.startDate);
            self.dtAft(dataBasic.endDate);
            self.targetOrganizationName(dataBasic.targetOrganizationName);
            
            // save data to local Storage
            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor: IUserInfor  = JSON.parse(data);
                userInfor.unit             = dataBasic.unit;
                userInfor.workplaceId      = dataBasic.workplaceId;
                userInfor.workplaceGroupId = dataBasic.workplaceGroupId;
                userInfor.workPlaceName    = dataBasic.targetOrganizationName;
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
        }
        
        saveShiftMasterToLocalStorage(shiftMasterWithWorkStyleLst: Array<IShiftMasterMapWithWorkStyle>) {
            let self = this;
            // save data to local Storage
            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor: IUserInfor = JSON.parse(data);
                userInfor.shiftMasterWithWorkStyleLst = shiftMasterWithWorkStyleLst;
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
        }
        
        bingdingToShiftPallet(data: any) {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());

            // set data shiftPallet
            __viewContext.viewModel.viewAC.flag = false;
            __viewContext.viewModel.viewAC.workplaceModeName(data.dataBasicDto.designation);
            __viewContext.viewModel.viewAC.palletUnit([
                { code: 1, name: getText("Com_Company") },
                { code: 2, name: data.dataBasicDto.designation }
            ]);
            
            __viewContext.viewModel.viewAC.selectedpalletUnit(userInfor.shiftPalletUnit);
            if (userInfor.shiftPalletUnit == 1) {
                __viewContext.viewModel.viewAC.handleInitCom(
                    data.listPageInfo,
                    data.targetShiftPalette.shiftPalletCom,
                    userInfor.shiftPalettePageNumberCom);
            } else {
                __viewContext.viewModel.viewAC.handleInitWkp(
                    data.listPageInfo,
                    data.targetShiftPalette.shiftPalletWorkPlace,
                    userInfor.shiftPalettePageNumberOrg);
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
        
        // convert data lấy từ server để đẩy vào Grid 8888
        private convertDataToGrid(data: IDataStartScreen, viewMode: string) {
            let self = this;
            let result = {};
            let leftmostDs        = [];
            let middleDs          = [];
            let detailColumns     = [];
            let objDetailHeaderDs = {};
            let detailHeaderDeco  = [];
            let detailContentDs   = [];
            let detailContentDeco = [];
            let detailContentDecoModeConfirm = [];
            let htmlToolTip       = [];
            let listCellNotEditBg = [];
            let listCellNotEditColor = [];
            let arrListCellLock = [];
            let scheduleModifyStartDate = self.scheduleModifyStartDate;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            self.listEmpData = [];
            self.listSid([]);
            self.arrListCellLock = [];
            self.listCellNotEditBg = [];
            self.listCellNotEditColor = [];
            self.dataSource = data;
            self.updatedCellsInModeShift = [];
            self.listBgOfCellSelfOther   = [];
            
            self.detailContentDeco            = [];
            self.detailContentDecoModeConfirm = [];
            self.tooltipShare = data.listDateInfo;
            
            self.listTimeDisable = [];
            self.listWorkTypeInfo = data.listWorkTypeInfo;
            self.listCellRetained = [];
            self.listCellError = [];
            
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
                if (viewMode == 'shift') {
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleShiftByEmpSort = _.orderBy(listWorkScheduleShiftByEmp, ['date'],['asc']);
                    let item = uk.localStorage.getItem(self.KEY);
                    let userInfor: IUserInfor = JSON.parse(item.get());
                    let shiftMasterWithWorkStyleLst = userInfor.shiftMasterWithWorkStyleLst;
                    
                    for (let j = 0; j < listWorkScheduleShiftByEmpSort.length; j++) {
                        let cell: IWorkScheduleShiftInforDto = listWorkScheduleShiftByEmpSort[j];
                        let time = new Time(new Date(cell.date));
                        let date = moment(cell.date, 'YYYY/MM/DD');
                        
                        // check ngày có thể chỉnh sửa 日 < A画面パラメータ.修正可能開始日 の場合
                        let canModifyStartDate = true;
                        if(moment(cell.date, 'YYYY/MM/DD') < moment(scheduleModifyStartDate, 'YYYY/MM/DD')){
                            canModifyStartDate = false;
                        }
                        
                        let ymd = time.yearMonthDay;
                        let shiftName = '';
                        shiftName = (cell.haveData == true && (cell.shiftName == null || cell.shiftName == '')) ? getText("KSU001_94") : cell.shiftName;
                        if (cell.needToWork == false)
                            shiftName = '';
                        objDetailContentDs['_' + ymd] = new ExCell('', '', '', '', '', '', shiftName, cell.shiftCode, cell.confirmed , cell.achievements, cell.workHolidayCls);

                        // set Deco background
                        if (userInfor.backgroundColor == 1 && self.mode() == 'edit' ) {
                            // A10_color② シフト表示：シフトの背景色  (Hiển thị Shift: màu nền của shift) 
                            if (cell.shiftCode != null) {
                                let objShiftMasterWithWorkStyle = _.filter(shiftMasterWithWorkStyleLst, function(o) { return o.shiftMasterCode == cell.shiftCode; });
                                if (objShiftMasterWithWorkStyle.length > 0) {
                                    let color = '#' + objShiftMasterWithWorkStyle[0].color;
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, color, 0));
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, color, 0));
                                } else {
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "background-white", 0));
                                }
                            }
                            
                            //
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === 0) {
                                // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                self.listBgOfCellSelfOther.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                            }
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === 1) {
                                //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                self.listBgOfCellSelfOther.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                            }
                            if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === 2) {
                                //REFLECT_APPLICATION(2), 申請反映
                                self.listBgOfCellSelfOther.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                            }
                        } else if ((userInfor.backgroundColor == 0) || (userInfor.backgroundColor == 1 && self.mode() == 'confirm' )) {
                            // A10_color③ シフト表示：通常の背景色  (hiển thị shift: màu nền normal)                                                     
                            if (cell.achievements == true || cell.needToWork == false) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            } else if (cell.supportCategory != SupportCategory.NotCheering) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-schedule-support", 0));
                            } else {
                                if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                    self.listBgOfCellSelfOther.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                }
                                if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                    self.listBgOfCellSelfOther.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                }
                                if (cell.shiftEditState != null && cell.shiftEditState.editStateSetting === 2) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                    self.listBgOfCellSelfOther.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }
                        }
                        // set Deco text color
                        // A10_color⑥ スケジュール明細の文字色  (Màu chữ của "Schedule detail")                                                         
                        if (cell.achievements == true) {
                            if (userInfor.backgroundColor == 0) {
                                if (cell.shiftCode == '' || cell.shiftCode == null) {
                                    // デフォルト（黒）  Default (black) 
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                } else {
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                                }
                            } else {
                                if (cell.shiftCode == '' || cell.shiftCode == null) {
                                    // デフォルト（黒）  Default (black) 
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                    detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                } else {
                                    let workHolidayCls = cell.workHolidayCls == null ? self.getWorkStyle(shiftMasterWithWorkStyleLst, cell.shiftCode) : cell.workHolidayCls;
                                    let cellColor = self.setColorCell(workHolidayCls, ymd, rowId);
                                    detailContentDeco.push(cellColor);
                                    detailContentDecoModeConfirm.push(cellColor);
                                }
                            }
                        } else {
                            if (cell.shiftCode == '' || cell.shiftCode == null) {
                                // デフォルト（黒）  Default (black) 
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-default", 0));
                            } else {
                                let workHolidayCls = cell.workHolidayCls == null ? self.getWorkStyle(shiftMasterWithWorkStyleLst , cell.shiftCode) : cell.workHolidayCls;
                                let cellColor      = self.setColorCell(workHolidayCls,ymd, rowId);
                                detailContentDeco.push(cellColor);
                                detailContentDecoModeConfirm.push(cellColor);
                            }
                        }
                        
                        // điều kiện ※Aa1
                        if (canModifyStartDate == false || cell.isEdit == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            listCellNotEditBg.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            listCellNotEditColor.push(new CellColor('_' + ymd, rowId, "color-schedule-performance", 0));
                        }
                        // điều kiện ※Aa2
                        if (cell.isActive == false) {}
                        
                        if (canModifyStartDate == false || cell.needToWork == false || cell.achievements == true || cell.supportCategory == 3) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            arrListCellLock.push({ rowId: rowId, columnId: '_' + ymd });
                        } else if (cell.confirmed == true) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 0));
                        }
                        
                        if (cell.confirmed == true) {
                            arrListCellLock.push({ rowId: rowId, columnId: '_' + ymd });
                        }
                        
                    };
                    detailContentDs.push(objDetailContentDs);
                    self.arrListCellLock = arrListCellLock;
                    
                } else if (viewMode == 'shortName') {
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleInforByEmpSort = _.orderBy(listWorkScheduleInforByEmp, ['date'],['asc']);
                    _.each(listWorkScheduleInforByEmpSort, (cell: IWorkScheduleWorkInforDto) => {
                        
                        // check ngày có thể chỉnh sửa 日 < A画面パラメータ.修正可能開始日 の場合
                        let canModifyStartDate = true;
                        if (moment(cell.date, 'YYYY/MM/DD') < moment(scheduleModifyStartDate, 'YYYY/MM/DD')) {
                            canModifyStartDate = false;
                        }
                        let time = new Time(new Date(cell.date));
                        let ymd = time.yearMonthDay;
                        let workTypeName = ((cell.workTypeCode != null && (cell.workTypeName == '' || _.isNil(cell.workTypeName))) || cell.workTypeIsNotExit == true ) ? (cell.workTypeCode == null ? '' : cell.workTypeCode) + getText("KSU001_22") : cell.workTypeName;
                        let workTimeName = ((cell.workTimeCode != null && (cell.workTimeName == '' || _.isNil(cell.workTimeName))) || cell.workTimeIsNotExit == true ) ? (cell.workTimeCode == null ? '' : cell.workTimeCode) + getText("KSU001_22") : cell.workTimeName;
                        if (cell.needToWork == false) {
                            workTypeName = '';
                            workTimeName = '';
                        }
                        objDetailContentDs['_' + ymd] = new ExCell(cell.workTypeCode, workTypeName, cell.workTimeCode, workTimeName, '', '', '', '',cell.confirmed , cell.achievements, cell.workHolidayCls);

                        // set Deco background
                        // A10_color⑤ 勤務略名表示の背景色 (Màu nền hiển thị "chuyên cần, tên viết tắt")                                                   
                        if (cell.achievements == true || cell.needToWork == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                        } else {
                            if (cell.workTypeEditStatus != null) {
                                if (cell.workTypeEditStatus.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === 2) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }

                            if (cell.workTimeEditStatus != null) {
                                if (cell.workTimeEditStatus.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === 2) {
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
                            if (cell.workHolidayCls == AttendanceHolidayAttr.FULL_TIME) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                            }
                            if (cell.workHolidayCls == AttendanceHolidayAttr.MORNING) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == AttendanceHolidayAttr.AFTERNOON) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == AttendanceHolidayAttr.HOLIDAY) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                            }
                        }
                        
                        // điều kiện ※Abc1 dieu kien edit
                        if (canModifyStartDate == false || cell.isEdit == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                        }
                        // điều kiện ※Abc2
                        if (cell.isActive == false) {}
                        
                        if (canModifyStartDate == false || cell.needToWork == false || cell.achievements == true || cell.supportCategory == 3) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            arrListCellLock.push({ rowId: rowId, columnId: '_' + ymd });
                        } else if (cell.confirmed == true) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 1));
                        }
                        
                        if (cell.confirmed == true) {
                            arrListCellLock.push({ rowId: rowId, columnId: '_' + ymd });
                        }
                    });
                    detailContentDs.push(objDetailContentDs);
                    self.arrListCellLock = arrListCellLock;

                } else if (viewMode == 'time') {
                    objDetailContentDs['sid'] = i.toString();
                    objDetailContentDs['employeeId'] = emp.employeeId;
                    let listWorkScheduleInforByEmpSort = _.orderBy(listWorkScheduleInforByEmp, ['date'],['asc']);
                    _.each(listWorkScheduleInforByEmpSort, (cell: IWorkScheduleWorkInforDto) => {
                        // set dataSource
                        
                        // check ngày có thể chỉnh sửa 日 < A画面パラメータ.修正可能開始日 の場合
                        let canModifyStartDate = true;
                        if (moment(cell.date, 'YYYY/MM/DD') < moment(scheduleModifyStartDate, 'YYYY/MM/DD')) {
                            canModifyStartDate = false;
                        }
                        let time = new Time(new Date(cell.date));
                        let ymd = time.yearMonthDay;
                        let workTypeName = ((cell.workTypeCode != null && (cell.workTypeName == '' || _.isNil(cell.workTypeName))) || cell.workTypeIsNotExit == true ) ? (cell.workTypeCode == null ? '' : cell.workTypeCode) + getText("KSU001_22") : cell.workTypeName;
                        let workTimeName = ((cell.workTimeCode != null && (cell.workTimeName == '' || _.isNil(cell.workTimeName))) || cell.workTimeIsNotExit == true ) ? (cell.workTimeCode == null ? '' : cell.workTimeCode) + getText("KSU001_22") : cell.workTimeName;
                        let startTime    = cell.startTime == null ? '' : formatById("Clock_Short_HM", cell.startTime);
                        let endTime      = cell.endTime   == null ? '' : formatById("Clock_Short_HM", cell.endTime);
                        let workTypeCode = cell.workTypeCode;
                        let workTimeCode = cell.workTimeCode;
                        if (cell.needToWork == false) {
                            workTypeName = '';
                            workTimeName = '';
                            startTime    = '';
                            endTime      = '';
                        }
                        
                        if(cell.startTime == 0 && cell.endTime == 0){
                            startTime    = '';
                            endTime      = '';
                        }
                        
                        objDetailContentDs['_' + ymd] = new ExCell(workTypeCode, workTypeName, workTimeCode, workTimeName, startTime, endTime, '', '', cell.confirmed , cell.achievements, cell.workHolidayCls);
                        // set Deco background
                        // A10_color⑤ 勤務略名表示の背景色 (Màu nền hiển thị "chuyên cần, tên viết tắt")
                        if (cell.achievements == true || cell.needToWork == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 2));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 3));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 2));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 3));
                        } else {
                            if (cell.workTypeEditStatus != null) {
                                if (cell.workTypeEditStatus.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 0));
                                } else if (cell.workTypeEditStatus.editStateSetting === 2) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 0));
                                }
                            }
                            
                            if (cell.workTimeEditStatus != null) {
                                if (cell.workTimeEditStatus.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 1));
                                } else if (cell.workTimeEditStatus.editStateSetting === 2) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 1));
                                }
                            }
                            
                            if (cell.startTimeEditState != null) {
                                if (cell.startTimeEditState.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 2));
                                } else if (cell.startTimeEditState.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 2));
                                } else if (cell.startTimeEditState.editStateSetting === 2) {
                                    //REFLECT_APPLICATION(2), 申請反映
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-reflect-application", 2));
                                }
                            }
                            
                            if (cell.endTimeEditState != null) {
                                if (cell.endTimeEditState.editStateSetting === 0) {
                                    // HAND_CORRECTION_MYSELF(0), 手修正（本人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-self", 3));
                                } else if (cell.endTimeEditState.editStateSetting === 1) {
                                    //HAND_CORRECTION_OTHER(1), 手修正（他人）
                                    detailContentDeco.push(new CellColor('_' + ymd, rowId, "bg-daily-alter-other", 3));
                                } else if (cell.endTimeEditState.editStateSetting === 2) {
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
                            if (cell.workHolidayCls == AttendanceHolidayAttr.FULL_TIME) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-attendance", 1));
                            }
                            if (cell.workHolidayCls == AttendanceHolidayAttr.MORNING) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == AttendanceHolidayAttr.AFTERNOON) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-half-day-work", 1));
                            }
                            if (cell.workHolidayCls == AttendanceHolidayAttr.HOLIDAY) {
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDeco.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 0));
                                detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "color-holiday", 1));
                            }
                        }
                        
                         // điều kiện ※Abc1
                         // dieu kien ※Ac
                        if (canModifyStartDate == false || cell.isEdit == false) {
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 2));
                            detailContentDeco.push(new CellColor('_' + ymd, rowId, "xseal", 3));
                        }
                            
                        // điều kiện ※Abc2
                        if (cell.isActive == false) {}
                        
                        if (canModifyStartDate == false || cell.needToWork == false || cell.achievements == true || cell.supportCategory == 3) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 2));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xseal", 3));
                            arrListCellLock.push({ rowId: rowId, columnId: '_' + ymd });
                        } else if (cell.confirmed == true) {
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 0));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 1));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 2));
                            detailContentDecoModeConfirm.push(new CellColor('_' + ymd, rowId, "xdet", 3));
                        } else {
                            // ver1.8
                            if (cell.workHolidayCls == 0 || _.isNil(cell.workTimeCode) || cell.workTimeCode == '' ) {
                                let obj = new TimeDisable(rowId, '_' + ymd);
                                self.listTimeDisable.push(obj);
                            }
                        }
                        
                        if (cell.confirmed == true) {
                            arrListCellLock.push({ rowId: rowId, columnId: '_' + ymd });
                        }
                        
                    });
                    detailContentDs.push(objDetailContentDs);
                    self.arrListCellLock = arrListCellLock;
                }
            }
            
            // truyen sids vao kcp015
            self.sids(self.listSid());
            
            self.listCellNotEditBg    = listCellNotEditBg;
            self.listCellNotEditColor = listCellNotEditColor;

            // set width cho column cho tung mode
            let widthColumn = 0;
            if (viewMode == 'time') {
                widthColumn = 160;
            } else if (viewMode == 'shortName') {
                widthColumn = 80;
            } else if (viewMode == 'shift') {
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
            
            detailColumns.push({ key: "sid", width: "5px", headerText: "ABC", visible: false });
            objDetailHeaderDs['sid'] = "";
            self.arrDay = [];
            _.each(data.listDateInfo, (dateInfo: IDateInfo) => {
                self.arrDay.push(new Time(new Date(dateInfo.ymd)));
                let time = new Time(new Date(dateInfo.ymd));
                detailColumns.push({
                    key: "_" + time.yearMonthDay, width: widthColumn + "px", handlerType: "input", dataType: "label/label/duration/duration", primitiveValue: "TimeWithDayAttr", headerControl: "link"
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
                arrListCellLock: arrListCellLock
            };
            self.detailContentDs = detailContentDs;
            self.detailColumns = detailColumns;
            self.detailContentDeco = detailContentDeco;
            self.detailContentDecoModeConfirm = detailContentDecoModeConfirm;
            
            let empLogin = _.filter(detailContentDs, function(o) { return o.employeeId == self.employeeIdLogin; });
            if (empLogin.length > 0) {
                self.key = empLogin[0].sid;
                self.rowIndexOfEmpLogin = _.indexOf(detailContentDs, empLogin[0]);
            } else {
                self.key = 0;
                self.rowIndexOfEmpLogin = -1;
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
            if (workHolidayCls == AttendanceHolidayAttr.FULL_TIME) {
                return new CellColor('_' + ymd, rowId, "color-attendance", 0);
            }
            if (workHolidayCls == AttendanceHolidayAttr.MORNING) {
                return new CellColor('_' + ymd, rowId, "color-half-day-work", 0);
            }
            if (workHolidayCls == AttendanceHolidayAttr.AFTERNOON) {
                return new CellColor('_' + ymd, rowId, "color-half-day-work", 0);
            }
            if (workHolidayCls == AttendanceHolidayAttr.HOLIDAY) {
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
                if (self.mode() === 'edit') {
                    self.bindingEventClickFlower();
                }
            }, 1);
        }
            
        saveModeGridToLocalStorege(mode) {
            let self = this;
            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor = JSON.parse(data);
                userInfor.disPlayFormat = mode;
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
        }

        /**
         * get setting ban dau
         */
        getSettingDisplayWhenStart(viewMode, isStart) {
            let self = this;
            
            $(".editMode").addClass("A6_hover").removeClass("A6_not_hover");
            $(".confirmMode").addClass("A6_not_hover").removeClass("A6_hover");

            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor: IUserInfor = JSON.parse(data);

                // A4_7
                if(isStart){
                    self.achievementDisplaySelected(2);
                }else{
                    self.achievementDisplaySelected(userInfor.achievementDisplaySelected == false ? 2 : 1);
                }
                
                // A4_12 背景色の初期選択   (Chọn default màu nền)
                self.backgroundColorSelected(userInfor.backgroundColor);

                // get setting height grid
                if (userInfor.gridHeightSelection == 1) {
                    self.selectedTypeHeightExTable(1);
                    self.isEnableInputHeight(false);
                } else {
                    self.heightGridSetting(userInfor.heightGridSetting);
                    self.selectedTypeHeightExTable(2);
                    self.isEnableInputHeight(true);
                }
                
                if (viewMode == 'time') {
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
            });
        }
        // 9999 dangky
        saveData(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();

            if (nts.uk.ui.errors.hasError() || self.mode() === 'confirm')
                return;
            
            $('div > iframe').contents().find('#btnClose').trigger('click');

            nts.uk.ui.block.grayout();
            let itemLocal = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(itemLocal.get());
            let updatedCells = $("#extable").exTable("updatedCells");
            let viewMode = userInfor.disPlayFormat;
            let params = [];
            let cellsGroup;
            if (userInfor.disPlayFormat == 'shift' && self.hasChangeModeBg == true) {
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

            let data = self.buidDataReg(userInfor.disPlayFormat, cellsGroup);
            
            if (viewMode == 'time') {
                self.checkCellRetained(data);    
            }
            
            // check trường hợp starttime|end == ''  thì return luôn. 
            let validData = self.validData(data, userInfor.disPlayFormat);
            if (validData  == false) {
                nts.uk.ui.block.clear();
                return;
            }

            service.regWorkSchedule(data).done((rs) => {
                if (rs.hasError == false) {

                    let $grid = $('div.ex-body-detail');
                    self.updateAfterSaveData($grid[0]);
                    self.listCellRetained = [];

                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    nts.uk.ui.block.clear();
                } else {
                    let $grid = $('div.ex-body-detail');
                    self.updateAfterSaveData($grid[0]);
                    self.listCellRetained = [];
                    if(rs.listErrorInfo.length > 0){
                        self.openKDL053(rs);    
                    }
                }
                self.hasChangeModeBg = false;
                self.listCellUpdatedWhenChangeModeBg = [];
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }
 
        checkCellRetained(dataSave: any) {
            let self = this;
            if (self.listCellRetained.length == 0) {
                return;
            }
            let dataSource = $("#extable").exTable('dataSource', 'detail').body;
            for (let i = 0; i < self.listCellRetained.length; i++) {
                let cellRetaine = self.listCellRetained[i];
                let exit = _.filter(dataSave, function(o) { return o.rowIndex == cellRetaine.rowIndex && o.columnKey == cellRetaine.columnKey; });
                if (exit.length == 0) {
                    let ymd = moment(cellRetaine.columnKey.slice(1)).format('YYYY/MM/DD');
 
                    let startTimeCal =  cellRetaine.startTime == '' ? '' : nts.uk.time.minutesBased.duration.parseString(cellRetaine.startTime).toValue();
                    let endTimeCal   = cellRetaine.endTime  = '' ? '' : nts.uk.time.minutesBased.duration.parseString(cellRetaine.endTime).toValue();
 
                    dataSave.push({
                        sid: self.listSid()[cellRetaine.rowIndex],
                        ymd: ymd,
                        viewMode: 'time',
                        workTypeCd:  cellRetaine.workTypeCode,
                        workTimeCd:  cellRetaine.workTimeCode,
                        startTime: startTimeCal,
                        endTime: endTimeCal,
                        isChangeTime: true,
                        rowIndex: cellRetaine.rowIndex,
                        columnKey: cellRetaine.columnKey
                    });
                }
            }
        }
            
        // 6666 check truong hop mode time, worktype la required la khong co starttime or endtime
        validData(data: any, vMode : any) {
            let self = this;
            if (data.length == 0) {
                return false;
            }
            
            if (vMode != 'time')
                return true;

            let check = true;
            _.forEach(data, function(cell) {
                let exit = _.filter(self.listTimeDisable, function(o) { return o.rowId + '' == cell.rowIndex && o.columnId == cell.columnKey });
                if (exit.length == 0) {
                    if (cell.startTime == null || cell.endTime == null) {
                        console.log('startTime or endTime = null. Please check data...');
                        check = false;
                        return false;
                    }
                }
            });
            return check;
        }

        buidDataReg(viewMode, cellsGroup) {
            let self = this;
            let dataReg = [];
            if (viewMode == 'time') {
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
            } else if (viewMode == 'shortName') {
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
            } else if (viewMode == 'shift') {
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
            nts.uk.ui.block.grayout();
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

            nts.uk.ui.block.clear();
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
        
        bindDataAfterSave() {
            let self = this;

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
            let itemLocal = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(itemLocal.get());
            let bodyHeightMode = userInfor.gridHeightSelection == 1 ? "dynamic" : "fixed";
            // phần leftMost
            let leftmostColumns = [];
            let leftmostHeader = {};
            let leftmostContent = {};
            let leftmostDs = dataBindGrid.leftmostDs;

            leftmostColumns = [{
                key: "codeNameOfEmp", headerText: getText("KSU001_205"), width: self.widthA8+"px", icon: { for: "body", class: "icon-leftmost", width: "25px" },
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
            let detailContentDeco = dataBindGrid.detailContentDeco;
            let detailContentDs = dataBindGrid.detailContentDs;
            let detailColumns = dataBindGrid.detailColumns;
            let objDetailHeaderDs = dataBindGrid.objDetailHeaderDs;
            let htmlToolTip = dataBindGrid.htmlToolTip;
            let timeRanges = [];

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
            detailColumns.forEach((col: any) => {
                if (col.visible === false) return;
                col.headerHandler = (ui: any) => {
                    let detailContentData: any = [];
                    for (let i = 0; i < detailContentDs.length; i++) {
                        detailContentData.add({ employeeId: detailContentDs[i].employeeId, datas: detailContentDs[i][ui.columnKey] });
                    }
                    let dayEdit = new Date();
                    let param = {
                        detailContentDs: detailContentData,
                        unit: userInfor.unit,
                        workplaceId: userInfor.workplaceId,
                        workplaceGroupId: userInfor.workplaceGroupId,
                        workplaceName: userInfor.workPlaceName,
                        listEmp: self.listEmpData,
                        daySelect: moment(ui.columnKey.slice(1)).format('YYYY/MM/DD'),
                        startDate: self.dateTimePrev(),
                        endDate: self.dateTimeAfter(),
                        dayEdit: moment(self.scheduleModifyStartDate).format('YYYY/MM/DD') 
                    }
                    setShared("dataFromA", param);
                    setShared("dataTooltip", self.tooltipShare);
                    nts.uk.ui.windows.sub.modal("/view/ksu/003/a/index.xhtml").onClosed(() => { });
                    return false;
                };
            });

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
                fields: ["workTypeCode", "workTypeName", "workTimeCode", "workTimeName", "shiftName", "startTime", "endTime", "shiftCode", "confirmed", "achievements"],
            };
            
            function customValidate(idx, key, innerIdx, value, obj) {
                let startTime, endTime;
                if (innerIdx === 2) {
                    startTime = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                    endTime = nts.uk.time.minutesBased.duration.parseString(obj.endTime).toValue();
                } else if (innerIdx === 3) {
                    startTime = nts.uk.time.minutesBased.duration.parseString(obj.startTime).toValue();
                    endTime = nts.uk.time.minutesBased.duration.parseString(value).toValue();
                }
                
                if (startTime < 0 && endTime < 0) {
                    startTime = startTime * -1;
                    endTime = endTime * -1;
                }

                if (startTime >= endTime) {
                    let messInfo = nts.uk.resource.getMessage('Msg_54');
                    return { isValid: false, message: messInfo };
                }
                
                if (innerIdx === 2 && value == '') {
                    return { isValid: false };
                } else if (innerIdx === 3 && value == '') {
                    return { isValid: false };
                }
                
                if (innerIdx === 2) {
                    return { isValid: true, innerErrorClear: [3] };
                } else if (innerIdx === 3) {
                    return { isValid: true, innerErrorClear: [2] };
                }

                return { isValid: true };
            };

            let start = performance.now();
            
            if (self.showA9) {
                new nts.uk.ui.exTable.ExTable($("#extable"), {
                    headerHeight: "60px",
                    bodyRowHeight: viewMode == 'shift' ? "35px" : "50px",
                    bodyHeight: "500px",
                    horizontalSumHeaderHeight: "0px",
                    horizontalSumBodyHeight: "0px",
                    horizontalSumBodyRowHeight: "0px",
                    areaResize: true,
                    bodyHeightMode: bodyHeightMode,
                    windowXOccupation: windowXOccupation,
                    windowYOccupation: windowYOccupation,
                    manipulatorId: self.key,
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
                })
                    .LeftmostHeader(leftmostHeader).LeftmostContent(leftmostContent)
                    .MiddleHeader(middleHeader).MiddleContent(middleContent)
                    .DetailHeader(detailHeader).DetailContent(detailContent)
                    .create();
            } else {
                new nts.uk.ui.exTable.ExTable($("#extable"), {
                    headerHeight: "60px",
                    bodyRowHeight: viewMode == 'shift' ? "35px" : "50px",
                    bodyHeight: "500px",
                    horizontalSumHeaderHeight: "0px",
                    horizontalSumBodyHeight: "0px",
                    horizontalSumBodyRowHeight: "0px",
                    areaResize: true,
                    bodyHeightMode: bodyHeightMode,
                    windowXOccupation: windowXOccupation,
                    windowYOccupation: windowYOccupation,
                    manipulatorId: self.key,
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
                })
                    .LeftmostHeader(leftmostHeader).LeftmostContent(leftmostContent)
                    .DetailHeader(detailHeader).DetailContent(detailContent)
                    .create();
            }
            
            // set height grid theo localStorage đã lưu
            self.setPositionButonDownAndHeightGrid();
            $('#btnControlLeftRight').width($("#extable").width() + 10);
            $("#sub-content-main").width($('#extable').width() + 30);
            console.log('time buid grid: '+ (performance.now() - start));
        }
        
        bindingEventClickFlower() {
            let self = this;
            $('.extable-header-detail a').css('width', '30px');
            $('.extable-header-detail img').css('margin-top', '2px');
            let itemLocal = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(itemLocal.get());
            $(".header-image-no-event, .header-image-event").unbind('click');
            $(".header-image-no-event, .header-image-event").on("click", function(event) {
                if(self.mode() == 'confirm') return;
                let index = $(event.target).parent().index();
                let columnKey = self.detailColumns[index].key;
                let param = {
                    dateSelected: moment(columnKey.slice(1)).format('YYYY/MM/DD'),
                    workplace: {
                        workPlaceID: userInfor.workplaceId == null ? userInfor.workplaceGroupId : userInfor.workplaceId,
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
                            if (userInfor.disPlayFormat == 'time') {
                                // enable những cell đã disable trước đó đi rồi sau khi update grid mới disable đi được
                                self.enableCellsTime();
                                self.diseableCellsTime();
                            }
                        });
                    }
                });
            });
        }
        
        // update grid header after closed dialog kdl049
        updateHeader(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            nts.uk.ui.block.grayout();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            
            //
            let objDetailHeaderDs = {};
            let detailHeaderDeco  = [];
            let htmlToolTip       = [];
            
            let param = {
                startDate: self.dateTimePrev(),
                endDate  : self.dateTimeAfter(),
                wkpId  : userInfor.workplaceId,
                wkpGrId: userInfor.workplaceGroupId
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
                fields: ["workTypeCode", "workTypeName", "workTimeCode", "workTimeName", "shiftName", "startTime", "endTime", "shiftCode"],
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
        
        // khi thay đổi combobox backgrounndMode | thay đổi mode Edit <--> Confirm
        updateExTableWhenChangeModeBg(detailContentDecoUpdate): void {
            let self = this;
             
            // update Phần Detail
            let detailContentDeco = detailContentDecoUpdate;
            let detailContentDs = self.detailContentDs;
            let detailColumns = self.detailColumns;

            let detailContentUpdate = {
                columns: detailColumns,
                dataSource: detailContentDs,
                primaryKey: "sid",
                //highlight: false,
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
                fields: ["workTypeCode", "workTypeName", "workTimeCode", "workTimeName", "shiftName", "startTime", "endTime", "shiftCode"],
            };

            $("#extable").exTable("updateTable", "detail", {}, detailContentUpdate);

            self.setStyler();
        }
        
        setStyler() {
            let self = this;
            // get listShiftMaster luu trong localStorage
            let itemLocal = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(itemLocal.get());
            let listShiftMasterSaveLocal = userInfor.shiftMasterWithWorkStyleLst;

            // set color for cell
            $("#extable").exTable("stickStyler", function(rowIdx, key, innerIdx, data, stickOrigData) {
                if (userInfor.disPlayFormat == 'shift') {
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
                        if (workStyle == AttendanceHolidayAttr.FULL_TIME + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#0000ff", background: "#" + workInfo[0].color }; // color-attendance
                            } else {
                                return { textColor: "#0000ff" }; // color-attendance
                            }
                        }
                        if (workStyle == AttendanceHolidayAttr.MORNING + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#FF7F27", background: "#" + workInfo[0].color };// color-half-day-work
                            } else {
                                return { textColor: "#FF7F27" };// color-half-day-work
                            }
                        }
                        if (workStyle == AttendanceHolidayAttr.AFTERNOON + '') {
                            if (modeBackGround == 1) {
                                return { textColor: "#FF7F27", background: "#" + workInfo[0].color };// color-half-day-work
                            } else {
                                return { textColor: "#FF7F27" };// color-half-day-work
                            }
                        }
                        if (workStyle == AttendanceHolidayAttr.HOLIDAY + '') {
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
                } else if (userInfor.disPlayFormat == 'time') {
                    let workStyle = data.workHolidayCls;
                    if (!_.isNil(data)) {
                        // case coppy từ cell là ngày lễ, ngày nghỉ nên phải disable cell starttime, endtime đi.
                        if (workStyle == AttendanceHolidayAttr.HOLIDAY) {
                            self.diseableCellStartEndTime(rowIdx + '', key);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', key);
                        }
                        if (workStyle == AttendanceHolidayAttr.FULL_TIME) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#0000ff" }; // color-attendance
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (workStyle == AttendanceHolidayAttr.MORNING) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (workStyle == AttendanceHolidayAttr.AFTERNOON) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (workStyle == AttendanceHolidayAttr.HOLIDAY) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#ff0000" }; // color-holiday
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                        if (nts.uk.util.isNullOrUndefined(workStyle) || nts.uk.util.isNullOrEmpty(workStyle)) {
                            // デフォルト（黒）  Default (black)
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: self.timeColor }; // デフォルト（黒）  Default (black)
                            else if (innerIdx === 2 || innerIdx === 3) return { textColor: self.timeColor };
                        }
                    }
                } else if (userInfor.disPlayFormat == 'shortName') {
                    if (!_.isNil(data)) {
                        let workStyle = data.workHolidayCls;
                        if (workStyle == AttendanceHolidayAttr.FULL_TIME) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#0000ff" }; // color-attendance
                        }
                        if (workStyle == AttendanceHolidayAttr.MORNING) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                        }
                        if (workStyle == AttendanceHolidayAttr.AFTERNOON) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#FF7F27" }; // color-half-day-work
                        }
                        if (workStyle == AttendanceHolidayAttr.HOLIDAY) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: "#ff0000" }; // color-holiday
                        }
                        if (nts.uk.util.isNullOrUndefined(workStyle) || nts.uk.util.isNullOrEmpty(workStyle)) {
                            if (innerIdx === 0 || innerIdx === 1) return { textColor: self.timeColor }; // デフォルト（黒）  Default (black)
                        }
                    }
                }
            });
        }
        
        // khi thay đổi mode edit va confirm
        updateExTableWhenChangeMode(viewMode, updateMode): void {
            let self = this;
            // save scroll's position
            nts.uk.ui.block.grayout();
             
            // update Phần Detail
            let detailContentDeco = self.detailContentDeco;
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
                fields: ["workTypeCode", "workTypeName", "workTimeCode", "workTimeName", "shiftName", "startTime", "endTime", "shiftCode"],
            };

            $("#extable").exTable("mode", viewMode, updateMode, null, [{
                name: "BodyCellStyle",
                decorator: detailContentDeco
            }]);

           $("#extable").exTable("updateTable", "detail", {}, detailContentUpdate);
        }

        // save setting hight cua grid vao localStorage
        saveHeightGridToLocal() {
            let self = this;

            $('#input-heightExtable').trigger("validate");
            if (nts.uk.ui.errors.hasError())
                return;

            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor = JSON.parse(data);
                userInfor.gridHeightSelection = self.selectedTypeHeightExTable();
                if (self.selectedTypeHeightExTable() == TypeHeightExTable.DEFAULT) {
                    userInfor.heightGridSetting = '';
                    setTimeout(() => {
                        self.updateGridHeightMode("dynamic", null);
                    }, 1);
                    $('#A16').ntsPopup('hide');
                } else if (self.selectedTypeHeightExTable() == TypeHeightExTable.SETTING) {
                    userInfor.heightGridSetting = self.heightGridSetting();
                    setTimeout(() => {
                        self.updateGridHeightMode("fixed", self.heightGridSetting());
                    }, 1);
                    $('#A16').ntsPopup('hide');
                }
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
        }
        
        updateGridHeightMode(mode, height) {
            let self = this;
            if (mode == "dynamic") {
                let h = window.innerHeight - document.getElementById('extable').offsetTop - 60 -40; // 60 : height header, 40, khaong cách grid đên bottom page.;
                $("#extable").exTable("setHeight", h);
                $("#extable").exTable("gridHeightMode", "dynamic");
            } else {
                $("#extable").exTable("setHeight", height);
                $("#extable").exTable("gridHeightMode", "fixed");
            }
            self.setPositionButonDownAndHeightGrid();
            self.setHeightScreen();
        }

        // xử lý cho button A13
        toLeft() {
            let self = this;
            if (self.indexBtnToLeft % 2 == 0) {
                if (self.showA9) {
                    $("#extable").exTable("hideMiddle");
                }
                $('.iconToLeft').css('background-image', 'url(' + self.pathToRight + ')');
                
                $(".toLeft").css("margin-left", self.widthA8 + self.distanceLeftToGrid+"px");
                
                let marginleft = $('#extable').width() - self.widthA8 - self.widthBtnToLeftToRight*2 - self.distanceLeftToGrid;
                $(".toRight").css('margin-left', marginleft + 'px');
            } else {
                if (self.showA9) {
                    $("#extable").exTable("showMiddle");
                }
                $('.iconToLeft').css('background-image', 'url(' + self.pathToLeft + ')');
                let marginleftOfbtnToLeft: number = self.widthA8 + self.distanceLeftToGrid + self.widthMid;
                $(".toLeft").css("margin-left", marginleftOfbtnToLeft + 'px');
                
                let marginleftOfbtnToRight = $("#extable").width() - self.widthA8 - self.widthMid - self.widthBtnToLeftToRight*2 - self.distanceLeftToGrid;
                $(".toRight").css('margin-left', marginleftOfbtnToRight + 'px');
            }
            $('#btnControlLeftRight').width($("#extable").width() + 10);
            $("#sub-content-main").width($('#extable').width() + 30);
            self.indexBtnToLeft = self.indexBtnToLeft + 1;
        }

        toRight() {
            let self = this;
            if (self.indexBtnToRight % 2 == 0) {
                $('.iconToRight').css('background-image', 'url(' + self.pathToLeft + ')');
            } else {
                $('.iconToRight').css('background-image', 'url(' + self.pathToRight + ')');
            }
            self.indexBtnToRight = self.indexBtnToRight + 1;
        }

        toDown() {
            let self = this;
            if (self.indexBtnToDown % 2 == 0) {
                $('.iconToDown').css('background-image', 'url(' + self.pathToUp + ')');
            } else {
                $('.iconToDown').css('background-image', 'url(' + self.pathToDown + ')');
            }
            self.indexBtnToDown = self.indexBtnToDown + 1;
        }
        
        setPositionButonToRightToLeft() {
            let self = this;
            self.indexBtnToLeft = 0;
            $('#btnControlLeftRight').width($("#extable").width() + 10);

            let marginleftOfbtnToRight: number = 0;
            let marginleftOfbtnToLeft: number = self.widthA8 + self.distanceLeftToGrid + self.widthMid;
            if (self.showA9) {
                $(".toLeft").css("margin-left", marginleftOfbtnToLeft + 'px');
                marginleftOfbtnToRight = $("#extable").width() - self.widthA8 - self.widthMid - self.widthBtnToLeftToRight*2 - self.distanceLeftToGrid;
            } else {
                $(".toLeft").css("display", "none");
                marginleftOfbtnToRight = $("#extable").width() - self.widthBtnToLeftToRight - 3;
            }
            $(".toRight").css('margin-left', marginleftOfbtnToRight + 'px');
        }
        
        setHeightScreen() {
            let self = this;
            let itemLocal = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(itemLocal.get());
            if (userInfor.gridHeightSelection == 1) {
                $("#content-main").css('height', 'auto');
                $("#main-area").css('overflow-y', 'hidden');
            } else {
                let heightGrid: number = parseInt(userInfor.heightGridSetting);
                let heightHerder = _.isNil(document.getElementById('header')) ? 0 : document.getElementById('header').offsetHeight;
                $("#main-area").css('height', window.innerHeight -  document.getElementById('main-area').offsetTop - (heightHerder == 0 ? 95 : 0)+ 'px');
                $("#main-area").css('overflow-y', 'scroll');
                if(window.innerHeight > $("#extable").height() + document.getElementById('extable').offsetTop){
                    $("#main-area").css('overflow-y', 'hidden');
                }
            }
            $("#sub-content-main").width($('#extable').width() + 30);
        }
        
        setPositionButonToRight() {
            let self = this;
            let marginleftOfbtnToRight: number = 0;
            $('#btnControlLeftRight').width($("#extable").width() + 10);
            if (self.showA9) {
                let displayA9 = $('.ex-body-middle').css('display');
                if(displayA9 == 'none'){
                    marginleftOfbtnToRight = $('#extable').width() - self.widthA8 - self.widthBtnToLeftToRight*2 - self.distanceLeftToGrid;
                }else{
                    marginleftOfbtnToRight = $('#extable').width() - self.widthA8 - self.widthMid - self.widthBtnToLeftToRight*2 - self.distanceLeftToGrid;
                }
            } else {
                marginleftOfbtnToRight = $('#extable').width() - self.widthBtnToLeftToRight - 3;
            }
            $('.toRight').css('margin-left', marginleftOfbtnToRight <= 101 ? 101 : marginleftOfbtnToRight + 'px');
        }
        
        setPositionButonDownAndHeightGrid() {
            let self = this;
            if (uk.localStorage.getItem(self.KEY).isPresent()) {
                let userInfor = JSON.parse(uk.localStorage.getItem(self.KEY).get());
                if (userInfor.gridHeightSelection == 2) {
                    $("#extable").exTable("setHeight", userInfor.heightGridSetting);
                    let heightBodySetting: number = + userInfor.heightGridSetting;
                    let heightBody = heightBodySetting + 60 - 25 - 16; // 60 chieu cao header, 25 chieu cao button
                    $(".toDown").css({ "margin-top": heightBody + 'px' });
                } else {
                    let heightExtable = $("#extable").height();
                    let margintop = heightExtable - 52;
                    $(".toDown").css({ "margin-top": margintop + 'px' });
                }
            } else {
                let heightExtable = $("#extable").height();
                let margintop = heightExtable - 52;
                $(".toDown").css({ "margin-top": margintop + 'px' });
            }
        }
        
        setWidthButtonnInPopupA1_12(){
            let self = this;
            let widthA1_12_4  = $('#A1_12_4').width();
            let widthA1_12_6  = $('#A1_12_6').width();
            let widthA1_12_8  = $('#A1_12_8').width();
            let widthA1_12_12 = $('#A1_12_12').width();
            let widthA1_12_16 = $('#A1_12_16').width();
            let widthA1_12_18 = $('#A1_12_18').width();
            let widthA1_12_20 = $('#A1_12_20').width();
            
            let maxWidth = widthA1_12_4;
            if (widthA1_12_6 > maxWidth)
                maxWidth = widthA1_12_6;
            
            if (widthA1_12_8 > maxWidth)
                maxWidth = widthA1_12_8;
            
            if (widthA1_12_12 > maxWidth)
                maxWidth = widthA1_12_12;
            
            if (widthA1_12_16 > maxWidth)
                maxWidth = widthA1_12_16;
            
            if (widthA1_12_18 > maxWidth)
                maxWidth = widthA1_12_18;
            
            if (widthA1_12_20 > maxWidth)
                maxWidth = widthA1_12_20;
        
            $('#A1_12_4, #A1_12_6, #A1_12_8, #A1_12_12, #A1_12_16, #A1_12_18, #A1_12_20').width(maxWidth);
        }


        /**
        * next a month
        */
        nextMonth(): void {
            let self = this;
            if(self. selectedDisplayPeriod() == 2)
                return;
            
            nts.uk.ui.block.grayout();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            
            let param = {
                viewMode : self.selectedModeDisplayInBody(), // time | shortName | shift
                startDate: self.dateTimePrev(),
                endDate  : self.dateTimeAfter(),
                isNextMonth : true,
                cycle28Day : self. selectedDisplayPeriod() == 2 ? true : false,
                workplaceId     : userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId,
                unit:             userInfor.unit,
                getActualData   : item.isPresent() ? userInfor.achievementDisplaySelected : false, 
                listShiftMasterNotNeedGetNew: userInfor.shiftMasterWithWorkStyleLst, 
                listSid: self.listSid(),
                modePeriod : self. selectedDisplayPeriod()
            };
            
            service.getDataChangeMonth(param).done((data: any) => {
                if (userInfor.disPlayFormat == 'shift') {
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
                    listWorkScheduleShift: data.listWorkScheduleShift
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());

                self.setUpdateMode();
                
                if (self.mode() == 'confirm') {
                    $("#extable").exTable("updateMode", "determine");
                }

                self.setPositionButonToRightToLeft();
                
                self.mode() === 'edit' ? self.editMode() : self.confirmMode();

                if (userInfor.disPlayFormat == 'time') {
                    self.diseableCellsTime();
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
            if (self. selectedDisplayPeriod() == 2)
                return;

            nts.uk.ui.block.grayout();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());

            let param = {
                viewMode: self.selectedModeDisplayInBody(), // time | shortName | shift
                startDate: self.dateTimePrev(),
                endDate: self.dateTimeAfter(),
                isNextMonth: false,
                cycle28Day: self. selectedDisplayPeriod() == 2 ? true : false,
                workplaceId: userInfor.workplaceId,
                workplaceGroupId: userInfor.workplaceGroupId,
                unit: userInfor.unit,
                getActualData: item.isPresent() ? userInfor.achievementDisplaySelected : false,
                listShiftMasterNotNeedGetNew: userInfor.shiftMasterWithWorkStyleLst,
                listSid: self.listSid(),
                modePeriod : self. selectedDisplayPeriod()
            };

            service.getDataChangeMonth(param).done((data: any) => {
                if (userInfor.disPlayFormat == 'shift') {
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
                    listWorkScheduleShift: data.listWorkScheduleShift
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());

                self.setUpdateMode();

                if (self.mode() == 'confirm') {
                    $("#extable").exTable("updateMode", "determine");
                }

                self.setPositionButonToRightToLeft();
                
                self.mode() === 'edit' ? self.editMode() : self.confirmMode();

                if (userInfor.disPlayFormat == 'time') {
                    self.diseableCellsTime();
                }

                nts.uk.ui.block.clear();
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
        }
        
        editMode() {
            let self = this;
            let arrCellUpdated = $("#extable").exTable("updatedCells");
            let arrTmp = _.clone(arrCellUpdated);
            let lockCells = $("#extable").exTable("lockCells");
            
            if (lockCells.length > 0 || arrCellUpdated.length > 0) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_1732" }).ifYes(() => {
                    let item = uk.localStorage.getItem(self.KEY);
                    let userInfor: IUserInfor = JSON.parse(item.get());
                    self.editModeAct();
                    self.convertDataToGrid(self.dataSource, self.selectedModeDisplayInBody());
                    self.updateExTableWhenChangeMode(self.selectedModeDisplayInBody(), userInfor.updateMode);
                    self.setUpdateMode();
                    self.diseableCellsTime();
                    nts.uk.ui.block.clear();
                    
                }).ifNo(() => {});
            } else {
                self.editModeAct();
                self.setUpdateMode();
            }
        }
        
        editModeAct() {
            let self = this;
            self.mode('edit');
            $(".editMode").addClass("btnControlSelected").removeClass("btnControlUnSelected");
            $(".confirmMode").addClass("btnControlUnSelected").removeClass("btnControlSelected");
            nts.uk.ui.block.grayout();
            // set enable btn A7_1, A7_2, A7_3, A7_4, A7_5
            self.enableBtnPaste(true);
            self.enableBtnCoppy(true);
            self.enableHelpBtn(true);
            self.updateExTableWhenChangeModeBg(self.detailContentDeco);
            self.setIconEventHeader();
            $('div.ex-body-leftmost a').css("pointer-events", "");
            $('div.ex-header-detail.xheader a').css("pointer-events", "");
            
            if (self.selectedModeDisplayInBody() == 'time' || self.selectedModeDisplayInBody() == 'shortName') {
                // enable combobox workType, workTime
                __viewContext.viewModel.viewAB.enableListWorkType(true);
                
                let wTypeCdSelected = __viewContext.viewModel.viewAB.selectedWorkTypeCode();
                let objWtime = _.filter(__viewContext.viewModel.viewAB.listWorkType(), function(o) { return o.workTypeCode == wTypeCdSelected; });
                if (objWtime[0].workTimeSetting != 2) {
                    __viewContext.viewModel.viewAB.disabled(false);
                }
                if (self.selectedModeDisplayInBody() == 'time') {
                    self.visibleBtnInput(true);
                    self.enableBtnInput(true);
                }
            } else {
                self.visibleBtnInput(false);
                self.enableBtnInput(false);
                self.shiftPalletControlEnable();
            }
            
            if (self.selectedModeDisplayInBody() == 'time') {
                self.diseableCellsTime();
            }
            nts.uk.ui.block.clear();
        }
        
        confirmMode() {
            let self = this;
            let arrCellUpdated = $("#extable").exTable("updatedCells");

            if (arrCellUpdated.length > 0) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_1732" }).ifYes(() => {
                    self.confirmModeAct();
                    self.enableBtnReg(false);
                    self.listCellError = [];
                    self.convertDataToGrid(self.dataSource, self.selectedModeDisplayInBody());
                    self.updateExTableWhenChangeMode(self.selectedModeDisplayInBody() , "determine");
                    self.listCellRetained = [];
                    nts.uk.ui.block.clear();
                }).ifNo(() => {});
            } else {
                self.confirmModeAct();
                $("#extable").exTable("updateMode", "determine");
            }
        }
        
        confirmModeAct() {
            let self = this;
            self.mode('confirm');
            $(".confirmMode").addClass("btnControlSelected").removeClass("btnControlUnSelected");
            $(".editMode").addClass("btnControlUnSelected").removeClass("btnControlSelected");
            nts.uk.ui.block.grayout();
            // set enable btn A7_1, A7_2,, A7_3, A7_4, A7_5
            self.enableBtnPaste(false);
            self.enableBtnCoppy(false);
            self.enableHelpBtn(false);
            self.enableBtnRedo(false);
            self.enableBtnUndo(false);

            if (self.selectedModeDisplayInBody() == 'time' || self.selectedModeDisplayInBody() == 'shortName') {
                // disable combobox workType, workTime
                __viewContext.viewModel.viewAB.disabled(true);
                __viewContext.viewModel.viewAB.enableListWorkType(false);
                if (self.selectedModeDisplayInBody() == 'time') {
                    self.visibleBtnInput(true);
                    self.enableBtnInput(false);
                }
            } else {
                self.visibleBtnInput(false);
                self.enableBtnInput(false);
                self.shiftPalletControlDisable();
            }
            self.updateExTableWhenChangeModeBg(self.detailContentDecoModeConfirm);
            $('div.ex-body-leftmost a').css("pointer-events", "none");
            $('div.ex-header-detail.xheader a').css("pointer-events", "none");
            self.setIconEventHeader();
            if (self.selectedModeDisplayInBody() == 'time'){
                self.enableCellsTime();    
            }
            nts.uk.ui.block.clear();
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
        
        // add cell có time sửa tay không đúng (bao gồm trương hợp bằng'', NaN, startTime>endTime)(mode TimeInput)
        addCellNotValidInTimeInputMode(rowIdx, key) {
            let self = this;
            let exit = _.filter(self.listCellError, function(o: TimeError) { return o.rowId == rowIdx + '' && o.columnId == key + '' });
            if (exit.length == 0) {
                self.listCellError.push(new TimeError(rowIdx, key));
            }
        }

        // remove cell có time sửa tay không đúng trước đấy (bao gồm trương hợp bằng'', NaN, startTime>endTime)(mode TimeInput)
        removeCellNotValidInTimeInputMode(rowIdx, key) {
            let self = this;
            _.remove(self.listCellError, function(cell: TimeDisable) {
                return cell.rowId == rowIdx && cell.columnId == key;
            });
        }

        /**
         * paste data on cell
         * stick
         */
        pasteData(): void {
            let self = this;
            if (self.mode() == 'confirm')
                return;
            nts.uk.ui.block.grayout();
            $("#paste").addClass("btnControlSelected A6_hover").removeClass("btnControlUnSelected A6_not_hover");
            $("#coppy").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#input").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            
            $("#extable").exTable("updateMode", "stick");
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor : IUserInfor = JSON.parse(data);
                userInfor.updateMode = 'stick';
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
            
            if (self.selectedModeDisplayInBody() == 'time' || self.selectedModeDisplayInBody() == 'shortName') {
                $("#extable").exTable("stickMode", "single");
                if (self.selectedModeDisplayInBody() == 'time'){
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
                
            } else if (self.selectedModeDisplayInBody() == 'shift') {
                $("#extable").exTable("stickMode", "multi");
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
                let item = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor = JSON.parse(item.get());

                if (userInfor.disPlayFormat == 'time' || userInfor.disPlayFormat == 'shortName') {
                    let resolve = false;
                    let obj = _.find(self.arrListCellLock, function(o) { return o.rowId == rowIdx + '' && o.columnId == key; });
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

                    } else if ((userInfor.disPlayFormat == 'time') && (data.workHolidayCls == 1 || data.workHolidayCls == 2)) {
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
                        if (userInfor.disPlayFormat == 'time') {
                            if (data.workHolidayCls == 0) {
                                self.diseableCellStartEndTime(rowIdx+'', key);
                            } else {
                                self.enableCellStartEndTime(rowIdx+'', key);
                            }
                        }
                        dfd.resolve(true);
                    }

                } else if (userInfor.disPlayFormat == 'shift') {
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
                        if (userInfor.disPlayFormat == 'time') {
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
                        if (userInfor.disPlayFormat == 'time') {
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
            if (self.mode() == 'confirm')
                return;
            $("#extable").exTable("updateMode", "copyPaste");
            $("#extable").exTable("updateMode", "stick");
            $("#extable").exTable("updateMode", "copyPaste");
            nts.uk.ui.block.grayout();
            $("#paste").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#coppy").addClass("btnControlSelected A6_hover").removeClass("btnControlUnSelected A6_not_hover");
            $("#input").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor : IUserInfor = JSON.parse(data);
                userInfor.updateMode =  'copyPaste';
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
            
            self.setStyler();
            
            $("#extable").exTable("pasteValidate", function(rowIdx, key, data) {
                let dfd = $.Deferred();
                let item = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor = JSON.parse(item.get());
                let param = [];
                nts.uk.ui.block.grayout();
                if (_.isNil(data))
                    data = rowIdx;
                _.forEach(data, d => {
                    let shiftCode = d.shiftCode;
                    let shiftMasterWithWorkStyleLst = userInfor.shiftMasterWithWorkStyleLst;
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
                let item = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor = JSON.parse(item.get());
                if (userInfor.disPlayFormat == 'time') {
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
            if (self.mode() == 'confirm')
                return;
            nts.uk.ui.block.grayout();
            $("#paste").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#coppy").addClass("btnControlUnSelected A6_not_hover").removeClass("btnControlSelected A6_hover");
            $("#input").addClass("btnControlSelected A6_hover").removeClass("btnControlUnSelected A6_not_hover");
            
            $("#extable").exTable("updateMode", "edit");
            self.enableBtnUndo(false);
            self.enableBtnRedo(false);
            uk.localStorage.getItem(self.KEY).ifPresent((data) => {
                let userInfor: IUserInfor = JSON.parse(data);
                userInfor.updateMode = 'edit';
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
            });
            
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(item.get());
            if (userInfor.updateMode == 'stick') {
                $("#extable").exTable("stickUndo", function(rowIdx, columnKey, innerIdx, cellData) {
                    if (userInfor.disPlayFormat == 'time') {
                        if ((cellData.workHolidayCls === 0) || (cellData.workTimeCode == null && cellData.workTimeName == null && cellData.workTypeCode == null && cellData.workTypeName == null)) {
                            self.diseableCellStartEndTime(rowIdx+ '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (userInfor.updateMode == 'copyPaste') {
                $("#extable").exTable("copyUndo", function(rowIdx, columnKey, cellData) {
                    if (userInfor.disPlayFormat == 'time') {
                        if ((cellData.workHolidayCls === 0) || (cellData.workTimeCode == null && cellData.workTimeName == null && cellData.workTypeCode == null && cellData.workTypeName == null)) {
                            self.diseableCellStartEndTime(rowIdx + '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (userInfor.updateMode == 'edit') {
                $("#extable").exTable("editUndo");
            }
            self.checkExitCellUpdated();
            self.enableBtnRedo(true);
        }

        redoData(): void {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(item.get());
            if (userInfor.updateMode == 'stick') {
                $("#extable").exTable("stickRedo", function(rowIdx, columnKey, innerIdx, cellData) {
                    if (userInfor.disPlayFormat == 'time') {
                        if ((cellData.workHolidayCls === 0) || (cellData.startTime === '' && cellData.endTime === '')) {
                            self.diseableCellStartEndTime(rowIdx + '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (userInfor.updateMode == 'copyPaste') {
                $("#extable").exTable("copyRedo", function(rowIdx, columnKey, cellData) {
                    if (userInfor.disPlayFormat == 'time') {
                        if ((cellData.workHolidayCls === 0) || (cellData.startTime === '' && cellData.endTime === '')) {
                            self.diseableCellStartEndTime(rowIdx + '', columnKey);
                        } else {
                            self.enableCellStartEndTime(rowIdx + '', columnKey);
                        }
                    }
                });
            } else if (userInfor.updateMode == 'edit') {
                $("#extable").exTable("editRedo");
            }
            self.checkExitCellUpdated();
        }
        
        checkExitCellUpdated() {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor = JSON.parse(item.get());
            console.log('listCellError: ');
            console.log(self.listCellError);
            
            setTimeout(() => {
               
                let updatedCells = $("#extable").exTable("updatedCells");
                if (_.size(updatedCells) > 0 ) {
                    self.enableBtnReg(true);
                    if (self.listCellError.length > 0) {
                        self.enableBtnReg(false);
                    }
                } else {
                    self.enableBtnReg(false);
                }

                if (_.size($("#extable").data("errors")) > 0)
                    self.enableBtnReg(false);

                if (userInfor.updateMode == 'stick') {
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
                } else if (userInfor.updateMode == 'copyPaste') {
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
                } else if (userInfor.updateMode == 'edit') {
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor : IUserInfor = JSON.parse(item.get());

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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            
            $('div > iframe').contents().find('#btnCloseG').trigger('click');

            // listEmpData : {id : '' , code : '', name : ''}
            setShared('dataShareDialogG', {
                startDate   : moment(self.dtPrev()).format('YYYY/MM/DD'),
                endDate     : moment(self.dtAft()).format('YYYY/MM/DD'),
                employeeIDs : self.sids(),
            });
            $('#A1_7_1').ntsPopup('hide');
            nts.uk.ui.windows.sub.modeless("/view/ksu/001/g/index.xhtml").onClosed(() => {});
        }

        // A2_1
        openKDL046() {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());

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
                self.updateScreen(dataFrom046);
            });
        }
        
        updateScreen(input: any): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            nts.uk.ui.block.grayout();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
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
                wkpId: input.unit == 0 ? input.workplaceId : input.workplaceGroupID
            };
            
            service.changeWokPlace(param).done((data: IDataStartScreen) => {
                
                self.targetOrganizationName(input.unit == 0 ? input.workplaceName : input.workplaceGroupName);
                
                let item = uk.localStorage.getItem(self.KEY);
                let userInfor: IUserInfor  = JSON.parse(item.get());
                userInfor.unit             = input.unit;
                userInfor.workplaceId      = input.unit == 0 ? input.workplaceId : '';
                userInfor.workplaceGroupId = input.unit == 0 ? '' : input.workplaceGroupID;
                userInfor.workPlaceName    = input.unit == 0 ? input.workplaceName : input.workplaceGroupName;
                userInfor.code             = input.workplaceGroupCode;
                uk.localStorage.setItemAsJson(self.KEY, userInfor);
                
                if (userInfor.disPlayFormat === 'time' || userInfor.disPlayFormat === 'shortName') {
                    __viewContext.viewModel.viewAB.check(false);
                    __viewContext.viewModel.viewAB.filter(input.unit == 0 ? true : false);
                    __viewContext.viewModel.viewAB.workplaceIdKCP013(input.unit == 0 ? input.workplaceId : input.workplaceGroupID);
                } else {
                    if (input.unit == 0) {
                        $('#Aa1_2 > label:nth-child(2) > span').html(getText('Com_Workplace'));
                    } else {
                        $('#Aa1_2 > label:nth-child(2) > span').html(getText('Com_WorkplaceGroup'));
                    }

                    self.saveShiftMasterToLocalStorage(data.shiftMasterWithWorkStyleLst);
                    // set data shiftPallet
                    __viewContext.viewModel.viewAC.flag = false;
                    __viewContext.viewModel.viewAC.selectedpalletUnit(userInfor.shiftPalletUnit);
                    
                    if (userInfor.shiftPalletUnit == 1) {
                        __viewContext.viewModel.viewAC.handleInitCom(data.listPageInfo,data.targetShiftPalette.shiftPalletCom,userInfor.shiftPalettePageNumberCom);
                    } else {
                        __viewContext.viewModel.viewAC.handleInitWkp(data.listPageInfo,data.targetShiftPalette.shiftPalletWorkPlace,userInfor.shiftPalettePageNumberOrg);
                    }
                    __viewContext.viewModel.viewAC.flag = true;
                    if (__viewContext.viewModel.viewAC.listPageComIsEmpty == true) {
                        $('.ntsButtonTableButton').addClass('nowithContent');
                    }
                    if (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true) {
                        $('.ntsButtonTableButton').addClass('nowithContent');
                    }
                    
                    if (self.mode() == 'confirm') {
                        self.shiftPalletControlDisable();
                    }
                }

                self.saveDataGrid(data);
                
                let dataGrid: any = {
                    listDateInfo: data.listDateInfo,
                    listEmpInfo: data.listEmpInfo,
                    displayControlPersonalCond: data.displayControlPersonalCond,
                    listPersonalConditions: data.listPersonalConditions,
                    listWorkScheduleWorkInfor: data.listWorkScheduleWorkInfor,
                    listWorkScheduleShift: data.listWorkScheduleShift
                }
                let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());
                
                // remove va tao lai grid
                self.destroyAndCreateGrid(dataBindGrid, self.selectedModeDisplayInBody());

                self.setUpdateMode();
                
                if (self.mode() == 'confirm') {
                    $("#extable").exTable("updateMode", "determine");
                    $('div.ex-body-leftmost a').css("pointer-events", "none");
                    $('div.ex-header-detail.xheader a').css("pointer-events", "none");
                }

                self.setPositionButonToRightToLeft();
                
                self.mode() === 'edit' ? self.editMode() : self.confirmMode();
                
                if (userInfor.disPlayFormat === 'time') {
                    self.diseableCellsTime();
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor : IUserInfor = JSON.parse(item.get());
            
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
            setShared("baseDate", ko.observable(self.dateTimeAfter()));
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            
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
                    if (userInfor.disPlayFormat === 'time') {
                        self.enableCellsTime();
                    }
                    
                    let dataBindGrid = self.convertDataToGrid(dataGrid, self.selectedModeDisplayInBody());

                    self.updateExTableAfterSortEmp(dataBindGrid, self.selectedModeDisplayInBody(), userInfor.updateMode, true, true, true);
                    
                    if (userInfor.disPlayFormat === 'time') {
                        self.diseableCellsTime();
                    }

                    nts.uk.ui.block.clear();
                }
                dfd.resolve();
            }).fail(function() {
                dfd.reject();
            });
            return dfd.promise();
        }
        
        btnOpenKDL055(): void {
            let self = this;
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
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
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
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

        saveDataInModeEdit(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let item = uk.localStorage.getItem(self.KEY);
            let userInfor: IUserInfor = JSON.parse(item.get());
            nts.uk.ui.block.grayout();
            let updatedCells = $("#extable").exTable("updatedCells");
            let params = [];
            let cellsGroup;
            if (userInfor.disPlayFormat == 'shift' && self.hasChangeModeBg == true) {
                // cập nhật lại list cells change.
                let updatedCells = $("#extable").exTable("updatedCells");
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

                cellsGroup = self.groupByRowIndAndColKey(self.listCellUpdatedWhenChangeModeBg, function(cell) {
                    return [cell.rowIndex, cell.columnKey];
                });
            } else {
                cellsGroup = self.groupByRowIndAndColKey(updatedCells, function(cell) {
                    return [cell.rowIndex, cell.columnKey];
                });
            }

            let data = self.buidDataReg(userInfor.disPlayFormat, cellsGroup);

            if (userInfor.disPlayFormat == 'time') {
                self.checkCellRetained(data);
            }

            // check trường hợp starttime|end == ''  thì return luôn. 
            let validData = self.validData(data, userInfor.disPlayFormat);
            if (validData == false) {
                nts.uk.ui.block.clear();
                return;
            }

            service.regWorkSchedule(data).done((rs) => {
                if (rs.hasError == false) {
                    let $grid = $('div.ex-body-detail');
                    self.updateAfterSaveData($grid[0]);
                    nts.uk.ui.block.clear();
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        self.openKDL055();
                    });
                } else {
                    let $grid = $('div.ex-body-detail');
                    self.updateAfterSaveData($grid[0]);
                    self.listCellRetained = [];
                    if(rs.listErrorInfo.length > 0){
                        self.openKDL053(rs); 
                        return;   
                    }
                }
                self.hasChangeModeBg = false;
                self.listCellUpdatedWhenChangeModeBg = [];
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
                dfd.reject();
            });
            return dfd.promise();
        }

        compareArrByRowIndexAndColumnKey(a: any, b: any): any {
            return a.rowIndex == b.rowIndex && a.comlumnKey == b.comlumnKey;
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
    }

    export enum viewMode {
        SYMBOL = 1,
        SHORTNAME = 2,
        TIME = 3
    }

    export enum TypeHeightExTable {
        DEFAULT = 1,
        SETTING = 2
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
        
        constructor(workTypeCode: string, workTypeName: string, workTimeCode: string, workTimeName: string, startTime?: string, endTime?: string, shiftName?: any, shiftCode? : any, confirmed? : any, achievements? : any, workHolidayCls? : number) {
            this.workTypeCode = workTypeCode;
            this.workTypeName = workTypeName;
            this.workTimeCode = workTimeCode;
            this.workTimeName = workTimeName;
            this.shiftName = shiftName !== null ? shiftName : '';
            this.startTime = ( startTime == undefined || startTime == null ) ? '' : startTime;
            this.endTime = ( endTime == undefined || endTime == null ) ? '' : endTime;
            this.shiftCode = shiftCode !== null ? shiftCode : '';
            this.confirmed = confirmed !== null ? confirmed : false;
            this.achievements = achievements !== null ? achievements : false;
            this.workHolidayCls = workHolidayCls !== null ? workHolidayCls : '';
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
    }
    
    interface IPageInfo {
        pageName: string,
        pageNumber: number,
    }

    interface IDataBasicDto {
        startDate: string,
        endDate: string,
        designation: string,
        targetOrganizationName: string,
        unit: number,
        workplaceId: string,
        workplaceGroupId: string,
        code: string,
        scheduleModifyStartDate: string,
        usePublicAtr: boolean,
        useWorkAvailabilityAtr: boolean
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
        attendanceItemId: number;
        editStateSetting: number; // enum AttendanceHolidayAttr
        date: Date;
    }

    interface IWorkScheduleWorkInforDto {
        employeeId: string; // 社員ID
        date: Date; // 年月日
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
    }

    interface IWorkScheduleShiftInforDto {
        employeeId: string; // 社員ID
        date: Date; // 年月日
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
        isEdit: boolean;
        isActive: boolean;
    }

    enum AttendanceHolidayAttr {
        FULL_TIME = 3, //(3, "１日出勤系"),
        MORNING = 1, //(1, "午前出勤系"),
        AFTERNOON = 2, //(2, "午後出勤系"),
        HOLIDAY = 0, //(0, "１日休日系");
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
        heightGridSetting: number;
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
}