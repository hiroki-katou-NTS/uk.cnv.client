module nts.uk.at.view.kdw008.a {
    export module viewmodel {
        import getText = nts.uk.resource.getText;
        import confirm = nts.uk.ui.dialog.confirm;
        import block = nts.uk.ui.block;
        export class ScreenModel {
            checkInitSheetNo: boolean;

            // isMobile
            isMobile: boolean;

            //isDaily
            isUpdate: KnockoutObservable<boolean>;
            isRemove: KnockoutObservable<boolean>;
            showCode: KnockoutObservable<boolean>;
            checked: KnockoutObservable<boolean>;

            currentDailyFormatCode: KnockoutObservable<string>;
            currentDailyFormatName: KnockoutObservable<string>;

            //combobox select sheetNo
            sheetNoList: KnockoutObservableArray<SheetNoModel>;
            selectedSheetNo: KnockoutObservable<number>;
            selectedSheetName: KnockoutObservable<string>;

            tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            mobileTabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            selectedTab: KnockoutObservable<string>;
            selectedMobileTab: KnockoutObservable<string>;

            //is daily
            isDaily: KnockoutObservable<boolean>;
            enableSheetNo: KnockoutObservable<boolean>;
            isSetFormatToDefault: KnockoutObservable<boolean>;
            sideBar: KnockoutObservable<number>;

            columnsFormatCodde: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            formatCodeItems: KnockoutObservableArray<FormatCode>;
            selectedCode: KnockoutObservable<any>;

            //swap list tab 1
            columns1: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            dailyAttItems: KnockoutObservableArray<AttendanceItemDto>;
            authorityFormatDailyValue: KnockoutObservableArray<AttendanceItemDto>;
            dailyDataSource: KnockoutObservableArray<AttendanceItemDto>;

            //swap list tab 2
            columns2: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            monthlyAttItems: KnockoutObservableArray<AttendanceItemDto>;
            authorityFormatMonthlyValue: KnockoutObservableArray<AttendanceItemDto>;
            monthlyDataSource: KnockoutObservableArray<AttendanceItemDto>;

            //swap list tab 3
            columns3: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            monthCorrectionFormatList: KnockoutObservableArray<MonPfmCorrectionFormatDto>;
            monthCorrectionFormat: KnockoutObservable<MonPfmCorrectionFormatDto>;
            monthCorrectionValue: KnockoutObservableArray<AttendanceItemDto>;
            monthCorrectionDataSource: KnockoutObservableArray<AttendanceItemDto>;

            constructor(dataShare: any) {
                let self = this;
                self.checkInitSheetNo = false;

                self.isMobile = dataShare.mobile;
                console.log(self.isMobile);
                

                self.formatCodeItems = ko.observableArray([]);
                self.columnsFormatCodde = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'formatCode', width: 90 },
                    { headerText: getText('KDW008_8'), key: 'formatName', width: 120, formatter: _.escape }
                ]);

                self.dailyAttItems = ko.observableArray([]);
                self.monthlyAttItems = ko.observableArray([]);

                self.monthCorrectionFormatList = ko.observableArray([]);
                self.monthCorrectionFormat = ko.observable({});
                self.monthCorrectionValue = ko.observableArray([]);
                self.monthCorrectionDataSource = ko.observableArray([]);

                self.enableSheetNo = ko.observable(false);

                self.isSetFormatToDefault = ko.observable(false);
                self.sideBar = ko.observable(1);
                //isdaily
                self.isDaily = ko.observable(dataShare.ShareObject);
                if (!self.isDaily()) {
                    self.sideBar(2);
                }

                self.isUpdate = ko.observable(true);
                self.isRemove = ko.observable(true);
                self.showCode = ko.observable(false);

                self.checked = ko.observable(true);

                self.currentDailyFormatCode = ko.observable('');
                self.currentDailyFormatName = ko.observable('');

                self.selectedSheetName = ko.observable('');

                this.selectedCode = ko.observable();

                self.columns1 = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'attendanceItemDisplayNumber', width: 70 },
                    { headerText: 'number', key: 'attendanceItemId', hidden: true, width: 100 },
                    { headerText: getText('KDW008_8'), key: 'attendanceItemName', width: 200, formatter: _.escape }
                ]);
                self.columns2 = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'attendanceItemDisplayNumber', width: 70 },
                    { headerText: 'number', key: 'attendanceItemId', hidden: true, width: 100 },
                    { headerText: getText('KDW008_8'), key: 'attendanceItemName', width: 200, formatter: _.escape }
                ]);

                self.columns3 = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'attendanceItemDisplayNumber', width: 70 },
                    { headerText: 'number', key: 'attendanceItemId', hidden: true, width: 100 },
                    { headerText: getText('KDW008_8'), key: 'attendanceItemName', width: 200, formatter: _.escape }
                ]);

                //swap list 2
                self.authorityFormatMonthlyValue = ko.observableArray([]);
                self.monthlyDataSource = ko.observableArray([]);

                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: getText('KDW008_14'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(self.isDaily()) },
                    { id: 'tab-2', title: getText('KDW008_13'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(self.isDaily()) },
                    { id: 'tab-3', title: getText('KDW008_13'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(!self.isDaily()) }
                ]);

                self.mobileTabs = ko.observableArray([
                    { id: 'tab-1', title: getText('KDW008_14'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(self.isDaily()) },
                    { id: 'tab-2', title: getText('KDW008_13'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(self.isDaily()) },
                    { id: 'tab-3', title: getText('KDW008_13'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(!self.isDaily()) }
                ]);
                
                if (self.isDaily()) {
                    self.selectedTab = ko.observable('tab-1');
                    self.selectedMobileTab = ko.observable('tab-1');
                } else {
                    self.selectedTab = ko.observable('tab-3');
                    self.selectedMobileTab = ko.observable('tab-3');
                }
                

                //combobox select sheetNo tab2
                self.sheetNoList = ko.observableArray([
                    new SheetNoModel('1', '1'),
                    new SheetNoModel('2', '2'),
                    new SheetNoModel('3', '3'),
                    new SheetNoModel('4', '4'),
                    new SheetNoModel('5', '5'),
                    new SheetNoModel('6', '6'),
                    new SheetNoModel('7', '7'),
                    new SheetNoModel('8', '8'),
                    new SheetNoModel('9', '9'),
                    new SheetNoModel('10', '10')
                ]);
                self.selectedSheetNo = ko.observable(1);
                self.selectedSheetNo.subscribe((value) => {
                    nts.uk.ui.errors.clearAll();
                    block.invisible();
                    if (value == 1) {
                        self.enableSheetNo(false);
                    } else {
                        self.enableSheetNo(true);
                    }
                    if (self.isDaily()) {
                        nts.uk.ui.errors.clearAll();
                        self.getDailyDetail(self.currentDailyFormatCode(), value).done(() => {
                            block.clear();
                        })
                    } else {
                        self.getMonthCorrectionDetail(value);
                        block.clear();
                    }
                });

                self.authorityFormatDailyValue = ko.observableArray([]);
                self.dailyDataSource = ko.observableArray([]);

                self.selectedCode.subscribe(newValue => {
                    if (nts.uk.text.isNullOrEmpty(newValue)) return;
                    block.invisible();
                    self.isUpdate(true);
                    self.showCode(false); 
                    self.isRemove(true);                   
                    _.defer(() => { $("#currentName").focus(); });

                    let formatSelect: FormatCode = _.find(self.formatCodeItems(), (item: FormatCode) => {
                        return item.formatCode == newValue;
                    });
                    self.currentDailyFormatCode(formatSelect.formatCode)
                    self.currentDailyFormatName(formatSelect.formatName);
                    self.isSetFormatToDefault(!formatSelect.isSetDefault);
                    self.checked(formatSelect.isSetDefault);

                    if (self.isDaily()) {
                        self.getMonthlyDetail(newValue).done(() => {
                            self.initSelectedSheetNoHasMutated();
                        });
                    } else {
                        self.getMonPfmCorrectionFormat(self.currentDailyFormatCode());
                        self.initSelectedSheetNoHasMutated();
                    }
                });

            }

            jumpTo(sidebar) {
                let self = this;
                if(!self.isMobile) {
                    nts.uk.request.jump("/view/kdw/006/a/index.xhtml", { ShareObject: sidebar() });  
                } else {
                    nts.uk.request.jump("at","/view/ksp/001/a/index.xhtml", { ShareObject: sidebar() });
                }
            }

            initSelectedSheetNoHasMutated() {
                let self = this;
                if (self.checkInitSheetNo) {
                    self.checkInitSheetNo = false;
                    self.selectedSheetNo.valueHasMutated();
                    return;
                }
                if (self.selectedSheetNo() == 1) {
                    self.selectedSheetNo.valueHasMutated();
                } else {
                    self.selectedSheetNo(1);
                }
            }

            initSelectedCodeHasMutated(code) {
                let self = this;
                self.checkInitSheetNo = true;
                if (self.selectedCode() == code) {
                    self.selectedCode.valueHasMutated();
                } else {
                    self.selectedCode(code);
                }
            }

            startPage(): JQueryPromise<any> {
                let self = this;
                return self.loadData();
            }

            loadData(): JQueryPromise<any> {
                let self = this;
                    let dfd = $.Deferred();
                    block.invisible();
                    let oldIndex = this.getOldIndex();
                    if (self.isDaily()) {
                        service.getListAuthorityDailyFormatCode(self.isMobile).done((data) => {
                            let dtdGetDailyAttItem = self.getDailyAttItem();
                            let dtdGetMonthlyAttItem = self.getMonthlyAttItem();
                            $.when(dtdGetDailyAttItem, dtdGetMonthlyAttItem).done(() => {
                                if (data && data.length > 0) {
                                    self.formatCodeItems(FormatCode.fromDaily(data));
                                    let formatCodeItem: FormatCode = self.formatCodeItems()[this.getIndex(oldIndex)];
                                    self.initSelectedCodeHasMutated(formatCodeItem.formatCode);
                                } else {
                                    self.formatCodeItems([]);
                                    self.setNewMode();
                                }
                            }).always(() => {
                                block.clear();
                            })
                            dfd.resolve();
                        })
                    } else {
                        service.getListMonPfmCorrectionFormat().done((data) => {
                            let dtdGetMonthlyAttItem = self.getMonthlyAttItem();
                            $.when(dtdGetMonthlyAttItem).done(() => {
                                if (data && data.length > 0) {
                                    self.formatCodeItems(FormatCode.fromMonthly(data));
                                    self.monthCorrectionFormatList(MonPfmCorrectionFormatDto.fromApp(data));
                                    let formatCodeItem: FormatCode = self.formatCodeItems()[this.getIndex(oldIndex)];
                                    self.initSelectedCodeHasMutated(formatCodeItem.formatCode);
                                } else {
                                    self.formatCodeItems([]);
                                    self.setNewMode();
                                }
                            }).always(() => {
                                block.clear();
                            })
                            dfd.resolve();
                        })
                    }
                    return dfd.promise();
            }
            
            getIndex(oldIndex: number) {
                let self = this;
                let index = _.findIndex(self.formatCodeItems(), item => {
                    return item.formatCode == self.currentDailyFormatCode();
                });
                if (index < 0) {
                    if (self.formatCodeItems().length == 1) {
                        return 0;
                    }
                    if (oldIndex >= self.formatCodeItems().length - 1) {
                        return self.formatCodeItems().length - 1
                    }
                    return oldIndex;
                }
                return index;
            }

            getOldIndex() {
                let self = this;
                let index = _.findIndex(self.formatCodeItems(), item => {
                    return item.formatCode == self.currentDailyFormatCode();
                });
                if (index < 0) {
                    index = 0
                }
                return index;
            }

            getDailyAttItem(): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                service.getDailyAttItem().done(data => {
                    if (data) {
                        self.dailyAttItems(AttendanceItemDto.fromApp(data));
                    }
                    dfd.resolve();
                }).fail(err => {
                    dfd.reject(err);
                })
                return dfd.promise();
            }

            getDailyDetail(code: string, sheetNo: string): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                service.getDailyDetail(code, self.selectedSheetNo(), self.isMobile).done(data => {
                    $("#swap-list2-grid2").igGridSelection("clearSelection");
                    $("#swap-list2-grid1").igGridSelection("clearSelection");
                    self.authorityFormatDailyValue.removeAll();
                    self.dailyDataSource.removeAll();
                    self.dailyDataSource(_.cloneDeep(self.dailyAttItems()));

                    if (data) {
                        self.selectedSheetName(data.sheetName);
                        self.authorityFormatDailyValue(self.mapAttItemFormatDetail(self.dailyAttItems(), data.dailyAttendanceAuthorityDetailDtos));
                    }
                    dfd.resolve();
                }).fail(err => {
                    dfd.reject(err);
                })
                return dfd.promise();
            }

            getMonthlyAttItem(): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                service.getMonthlyAttItem().done(data => {
                    if (data) {
                        self.monthlyAttItems(AttendanceItemDto.fromApp(data));
                    }
                    dfd.resolve();
                }).fail(err => {
                    dfd.reject(err);
                })
                return dfd.promise();
            }

            getMonthlyDetail(code: string): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                service.getMonthlyDetail(code, self.isMobile).done(data => {
                    $("#swap-list-grid2").igGridSelection("clearSelection");
                    $("#swap-list-grid1").igGridSelection("clearSelection");
                    self.authorityFormatMonthlyValue.removeAll();
                    self.monthlyDataSource.removeAll();
                    self.monthlyDataSource(_.cloneDeep(self.monthlyAttItems()));

                    if (data) {
                        self.authorityFormatMonthlyValue(self.mapAttItemFormatDetail(self.monthlyAttItems(), data));
                    }
                    dfd.resolve();
                }).fail(err => {
                    dfd.reject(err);
                })
                return dfd.promise();
            }

            mapAttItemFormatDetail(attItems: Array<AttendanceItemDto>, details): Array<AttendanceItemDto> {
                details = DailyAttendanceAuthorityDetailDto.fromApp(details);
                let attItemDetail: Array<AttendanceItemDto> = [];
                attItemDetail = _.map(details, function(item: DailyAttendanceAuthorityDetailDto) {
                    let dto: AttendanceItemDto = new AttendanceItemDto(null);
                    dto.attendanceItemId = item.attendanceItemId;
                    dto.columnWidth = item.columnWidth;
                    let attItem: AttendanceItemDto = _.find(attItems, (att: AttendanceItemDto) => {
                        return att.attendanceItemId == item.attendanceItemId;
                    })
                    if (attItem) {
                        dto.attendanceItemName = attItem.attendanceItemName;
                        dto.attendanceItemDisplayNumber = attItem.attendanceItemDisplayNumber;
                    }
                    return dto;
                })
                return attItemDetail;
            }


            getMonPfmCorrectionFormat(formatCode){
                let self = this;
                let monthItem: MonPfmCorrectionFormatDto = _.find(self.monthCorrectionFormatList(), (item: MonPfmCorrectionFormatDto) => {
                    return item.monthlyPfmFormatCode == formatCode;
                })
                self.monthCorrectionFormat(monthItem);
            }

            getMonthCorrectionDetail(sheetNo: string) {
                let self = this;
                $("#swap-list3-grid2").igGridSelection("clearSelection") ;
                self.selectedSheetName(null);
                self.monthCorrectionValue.removeAll();
                self.monthCorrectionDataSource.removeAll();
                self.monthCorrectionDataSource(_.cloneDeep(self.monthlyAttItems()));
                if(self.monthCorrectionFormat() == null) {
                    return;    
                }
                let monthItem: MonPfmCorrectionFormatDto = self.monthCorrectionFormat();
                self.isSetFormatToDefault(monthItem.isSetFormatToDefault);
                self.checked(!monthItem.isSetFormatToDefault);
                let sheetItem: SheetCorrectedMonthlyDto = _.find(monthItem.displayItem.listSheetCorrectedMonthly, (item: SheetCorrectedMonthlyDto) => {
                    return item.sheetNo.toString() == sheetNo;
                })
                if (sheetItem) {
                    self.selectedSheetName(sheetItem.sheetName);
                    self.monthCorrectionValue(this.mapAttItemMonthRightDetail(self.monthlyAttItems(), sheetItem.listDisplayTimeItem));
                }
            }

            mapAttItemMonthRightDetail(attItems: Array<AttendanceItemDto>, details: Array<DisplayTimeItemDto>) {
                let attItemDetail: Array<AttendanceItemDto> = [];
                attItemDetail = _.map(details, function(item: DisplayTimeItemDto) {
                    let dto: AttendanceItemDto = new AttendanceItemDto(null);
                    dto.attendanceItemId = item.itemDaily;
                    dto.columnWidth = item.columnWidthTable;
                    let attItem: AttendanceItemDto = _.find(attItems, (att: AttendanceItemDto) => {
                        return att.attendanceItemId == item.itemDaily;
                    })
                    if (attItem) {
                        dto.attendanceItemName = attItem.attendanceItemName;
                        dto.attendanceItemDisplayNumber = attItem.attendanceItemDisplayNumber;
                    }
                    return dto;
                })
                return attItemDetail;
            }

            setNewMode() {
                let self = this;
                self.currentDailyFormatCode(null);
                self.currentDailyFormatName('');
                self.selectedCode(null);

                self.checked(true);
                self.showCode(true);
                self.isUpdate(false);
                self.isRemove(false);

                self.authorityFormatDailyValue.removeAll();
                self.authorityFormatMonthlyValue.removeAll();
                self.monthCorrectionValue.removeAll();
                self.selectedSheetName("");

                if (self.isDaily()) {
                    self.authorityFormatDailyValue.removeAll();
                    self.dailyDataSource.removeAll();
                    self.dailyDataSource(_.cloneDeep(self.dailyAttItems()));
                    self.isSetFormatToDefault(true);
                    self.authorityFormatMonthlyValue.removeAll();
                    self.monthlyDataSource.removeAll();
                    self.monthlyDataSource(_.cloneDeep(self.monthlyAttItems()));
                } else {
                    self.isSetFormatToDefault(true);
                    self.monthCorrectionFormat(null)
                    self.monthCorrectionValue.removeAll();
                    self.monthCorrectionDataSource.removeAll();
                    self.monthCorrectionDataSource(_.cloneDeep(self.monthlyAttItems()));
                }
                $("#currentCode").focus();
                _.defer(() => { nts.uk.ui.errors.clearAll(); });
            }

            btnSheetNo() {
                let self = this;
                if (self.isDaily()) {
                    //add or update Monthly
                    let deleteBySheet = {
                        dailyPerformanceFormatCode: self.currentDailyFormatCode(),
                        sheetNo: self.selectedSheetNo()
                    };
                    nts.uk.ui.block.invisible();
                    confirm({ messageId: "Msg_18" }).ifYes(() => {
                        service.deleteAuthBySheet(deleteBySheet).done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_991" }).then(() => {
                                //self.reloadData(self.currentDailyFormatCode());
                                self.initSelectedSheetNoHasMutated();
                            });
                        }).always(function() {
                            nts.uk.ui.block.clear();
                        }).fail(function(error) {
                            $('#currentCode').ntsError('set', error);
                        });
                    }).ifNo(() => {
                        nts.uk.ui.block.clear();
                    });

                } else {
                    //monthly
                    let listDisplayTimeItem = [];
                    let listSheetMonthly = [new SheetCorrectedMonthly(
                        self.selectedSheetNo(),
                        "",
                        listDisplayTimeItem
                    )];

                    let temp = new MonPfmCorrectionFormat("", self.currentDailyFormatCode(), self.currentDailyFormatName(),
                        new MonthlyActualResults(listSheetMonthly),
                        self.checked()
                    );
                    nts.uk.ui.block.invisible();
                    confirm({ messageId: "Msg_18" }).ifYes(() => {
                        service.updateMonPfmCorrectionFormat(temp).done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_991" }).then(() => {
                                service.getListMonPfmCorrectionFormat().done(function(data) {
                                	self.loadData();
                                    self.initSelectedSheetNoHasMutated();
                                });
                            });
                        }).always(function() {
                            nts.uk.ui.block.clear();
                        }).fail(function(error) {
                            $('#currentCode').ntsError('set', error);
                        });
                    }).ifNo(() => {
                        nts.uk.ui.block.clear();
                    });
                }
            }

            remove() {
                let self = this;
                if (self.isDaily()) {
                    let isDefaultInitial = 0;
                    if (self.checked() == true) {
                        let isDefaultInitial = 1;
                    }
                    let removeAuthorityDto = {
                        dailyPerformanceFormatCode: self.currentDailyFormatCode(),
                        isDefaultInitial: isDefaultInitial
                    }
                    nts.uk.ui.dialog.confirm(nts.uk.resource.getMessage("Msg_18", []))
                        .ifYes(() => {
                            service.removeAuthorityDailyFormat(removeAuthorityDto, self.isMobile).done(function() {
                                nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                                    self.loadData();
                                });
                            }).fail(function(error) {
                            });

                        });
                } else {
                    let deleteMonPfmCmd = {
                        companyID: "",
                        monthlyPfmFormatCode: self.currentDailyFormatCode()
                    };
                    nts.uk.ui.dialog.confirm(nts.uk.resource.getMessage("Msg_18", []))
                        .ifYes(() => {
                            service.deleteMonPfmCorrectionFormat(deleteMonPfmCmd).done(function() {
                                nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                                    self.loadData();
                                })
                            }).fail(function(error) {
                            });
                        });
                }
            }

            addOrUpdateClick() {
                let self = this;
                $("#currentName").trigger("validate");

                if (!nts.uk.ui.errors.hasError()) {
                    if (self.isDaily()) {
                        $("#checkSheetNameIsDaily").trigger("validate");
                        if (!nts.uk.ui.errors.hasError()) {
                            if (self.authorityFormatDailyValue().length <= 0) {
                                nts.uk.ui.dialog.alert({ messageId: "Msg_920" });
                            } else {
                                self.register();
                            }
                        }
                    } else {
                        $("#currentCode").trigger("validate");
                        $("#currentName").trigger("validate");
                        $("#selectedSheetName").trigger("validate");
                        if (!nts.uk.ui.errors.hasError()) {
                            if (self.monthCorrectionValue().length <= 0) {
                                nts.uk.ui.dialog.alert({ messageId: "Msg_920" });
                            } else {
                                self.register();
                            }
                        }

                    }
                }
            }

            register() {
                let self = this;
                if (self.isDaily()) {
                    $(".need-check").trigger("validate");
                    if (!nts.uk.ui.errors.hasError()) {
                        //add or update Monthly
                        let authorityFormatDetailDtos = _.map(self.authorityFormatMonthlyValue(), item => {
                            let indexOfItem = _.findIndex(self.authorityFormatMonthlyValue(), { attendanceItemId: item.attendanceItemId });
                            let monthly: IDailyAttendanceAuthorityDetailDto = {};
                            monthly.attendanceItemId = item.attendanceItemId;
                            monthly.order = indexOfItem;
                            monthly.columnWidth = item.columnWidth == null ? 0 : item.columnWidth;
                            return new DailyAttendanceAuthorityDetailDto(monthly);
                        });
                        let addOrUpdateBusinessFormatMonthly = new AddAuthorityFormatMonthly(self.currentDailyFormatCode(), authorityFormatDetailDtos);

                        //add or update Daily
                        let businessTypeFormatDetailDailyDto = _.map(self.authorityFormatDailyValue(), item => {
                            let indexOfItem = _.findIndex(self.authorityFormatDailyValue(), { attendanceItemId: item.attendanceItemId });
                            let daily: IDailyAttendanceAuthorityDetailDto = {};
                            daily.attendanceItemId = item.attendanceItemId;
                            daily.order = indexOfItem;
                            daily.columnWidth = item.columnWidth == null ? 0 : item.columnWidth;
                            return new DailyAttendanceAuthorityDetailDto(daily);
                        });

                        let addOrUpdateBusinessFormatDaily: AddAuthorityFormatDaily = {};
                        if (self.checked() == true) {
                            addOrUpdateBusinessFormatDaily = new AddAuthorityFormatDaily(self.currentDailyFormatCode(), self.currentDailyFormatName(), self.selectedSheetNo(), self.selectedSheetName(), businessTypeFormatDetailDailyDto, 1);
                        } else {
                            addOrUpdateBusinessFormatDaily = new AddAuthorityFormatDaily(self.currentDailyFormatCode(), self.currentDailyFormatName(), self.selectedSheetNo(), self.selectedSheetName(), businessTypeFormatDetailDailyDto, 0);
                        }

                        let addOrUpdateDailyFormat = new AddOrUpdateDailyFormat(addOrUpdateBusinessFormatMonthly, addOrUpdateBusinessFormatDaily);
                        block.invisible();
                        if (self.isUpdate() == true) {
                            service.updateDailyDetail(addOrUpdateDailyFormat, self.isMobile).done(function() {
                                nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                    self.loadData();
                                });
                                $("#currentName").focus();
                            }).fail(function(error) {
                                nts.uk.ui.dialog.alertError(error.message);
                            }).always(function() {
                                block.clear();
                            });
                        } else {
                            service.addDailyDetail(addOrUpdateDailyFormat, self.isMobile).done(function() {
                                nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                    self.loadData();
                                });

                                $("#currentName").focus();
                            }).fail(function(error) {
                                $('#currentCode').ntsError('set', error);
                            }).always(function() {
                                block.clear();
                            });
                        }
                    }
                } else {
                    //monthly
                    let listDisplayTimeItem = [];
                    for (let i = 0; i < self.monthCorrectionValue().length; i++) {
                        var indexOfItem = _.findIndex(self.monthCorrectionValue(), { attendanceItemId: self.monthCorrectionValue()[i].attendanceItemId });
                        let obj = new DisplayTimeItem(
                            indexOfItem,
                            self.monthCorrectionValue()[i].attendanceItemId,
                            null
                        );
                        listDisplayTimeItem.push(obj);
                    }


                    let listSheetMonthly = [new SheetCorrectedMonthly(
                        self.selectedSheetNo(),
                        self.selectedSheetName(),
                        listDisplayTimeItem
                    )];

                    let temp = new MonPfmCorrectionFormat("", self.currentDailyFormatCode(), self.currentDailyFormatName(),
                        new MonthlyActualResults(listSheetMonthly),
                        self.checked()
                    );
                    if (!self.isRemove()) {
                        block.invisible();
                        service.addMonPfmCorrectionFormat(temp).done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                self.loadData();
                            });
                            $("#currentName").focus();
                        }).fail(function(error) {
                            $('#currentCode').ntsError('set', error);
                        }).always(function() {
                            block.clear();
                        });
                    } else {
                        block.invisible();
                        service.updateMonPfmCorrectionFormat(temp).done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                self.loadData();
                            });
                            $("#currentName").focus();
                        }).fail(function(error) {
                            $('#currentCode').ntsError('set', error);
                        }).always(function() {
                            block.clear();
                        });
                    }
                }
            }

            // mobile
            startMobilePage(): JQueryPromise<any> {
                let self = this;
                return self.loadData();
            }

            loadMobileData() {
                // let self = this;
                // let dfd = $.Deferred();
                // let oldIndex = this.getOldIndex();
                // service.getListAuthorityDailyFormatCode(self.isMobile).done((data) => {
                //     let dtdGetDailyAttItem = self.getDailyAttItem();
                //     $.when(dtdGetDailyAttItem).done(() => {
                //         if (data && data.length > 0) {
                //             self.formatCodeItems(FormatCode.fromDaily(data));
                //             let formatCodeItem: FormatCode = self.formatCodeItems()[this.getIndex(oldIndex)];
                //             self.initSelectedCodeHasMutated(formatCodeItem.formatCode);
                //         } else {
                //             self.formatCodeItems([]);
                //             self.setNewMode();
                //         }
                //     }).always(() => {
                //         block.clear();
                //     })
                //     dfd.resolve();
                // })
                
                // return dfd.promise();
            }
        }

        //monthly class :
        export class MonPfmCorrectionFormat {
            companyID: string;
            monthlyPfmFormatCode: string;
            monPfmCorrectionFormatName: string;
            displayItem: MonthlyActualResults;
            setFormatToDefault: boolean;
            constructor(companyID: string,
                monthlyPfmFormatCode: string,
                monPfmCorrectionFormatName: string,
                displayItem: MonthlyActualResults,
                setFormatToDefault: boolean) {
                this.companyID = companyID;
                this.monthlyPfmFormatCode = monthlyPfmFormatCode;
                this.monPfmCorrectionFormatName = monPfmCorrectionFormatName;
                this.displayItem = displayItem;
                this.setFormatToDefault = setFormatToDefault;

            }
        }

        export class MonthlyActualResults {
            listSheetCorrectedMonthly: Array<SheetCorrectedMonthly>;
            constructor(listSheetCorrectedMonthly: Array<SheetCorrectedMonthly>) {
                this.listSheetCorrectedMonthly = listSheetCorrectedMonthly;
            }
        }

        export class SheetCorrectedMonthly {
            sheetNo: number;
            sheetName: string;
            listDisplayTimeItem: Array<DisplayTimeItem>;
            constructor(sheetNo: number,
                sheetName: string,
                listDisplayTimeItem: Array<DisplayTimeItem>) {
                this.sheetNo = sheetNo;
                this.sheetName = sheetName;
                this.listDisplayTimeItem = listDisplayTimeItem;
            }
        }

        export class DisplayTimeItem {
            displayOrder: number;
            itemDaily: number;
            columnWidthTable: Number;
            constructor(
                displayOrder: number,
                itemDaily: number,
                columnWidthTable: Number) {
                this.displayOrder = displayOrder;
                this.itemDaily = itemDaily;
                this.columnWidthTable = (columnWidthTable == null ? null : columnWidthTable);
            }
        }

        export class SheetNoModel {
            sheetNoId: string;
            sheetNoName: string;
            constructor(sheetNoId: string, sheetNoName: string) {
                this.sheetNoId = sheetNoId;
                this.sheetNoName = sheetNoName;
            }
        }

        export class AddOrUpdateDailyFormat {
            authorityMonthlyCommand: AddAuthorityFormatMonthly;
            authorityDailyCommand: AddAuthorityFormatDaily;
            constructor(addOrUpdateAuthorityFormatMonthly: AddAuthorityFormatMonthly, addOrUpdateAuthorityFormatDaily: AddAuthorityFormatDaily) {
                let self = this;
                self.authorityMonthlyCommand = addOrUpdateAuthorityFormatMonthly;
                self.authorityDailyCommand = addOrUpdateAuthorityFormatDaily;
            }
        }

        export class AddAuthorityFormatMonthly {
            dailyPerformanceFormatCode: string;
            dailyAttendanceAuthorityDetailDtos: Array<DailyAttendanceAuthorityDetailDto>;
            constructor(dailyPerformanceFormatCode: string, dailyAttendanceAuthorityDetailDtos: Array<DailyAttendanceAuthorityDetailDto>) {
                let self = this;
                self.dailyPerformanceFormatCode = dailyPerformanceFormatCode || "";
                self.dailyAttendanceAuthorityDetailDtos = dailyAttendanceAuthorityDetailDtos || [];
            }
        }

        export class AddAuthorityFormatDaily {
            dailyPerformanceFormatCode: string;
            dailyPerformanceFormatName: string;
            sheetNo: number;
            sheetName: string;
            dailyAttendanceAuthorityDetailDtos: Array<DailyAttendanceAuthorityDetailDto>;
            isDefaultInitial: number;
            constructor(dailyPerformanceFormatCode: string, dailyPerformanceFormatName: string, sheetNo: number, sheetName: string, dailyAttendanceAuthorityDetailDtos: Array<DailyAttendanceAuthorityDetailDto>, isDefaultInitial: number) {
                this.dailyPerformanceFormatCode = dailyPerformanceFormatCode || "";
                this.dailyPerformanceFormatName = dailyPerformanceFormatName || "";
                this.sheetNo = sheetNo || 0;
                this.sheetName = sheetName || null;
                this.dailyAttendanceAuthorityDetailDtos = dailyAttendanceAuthorityDetailDtos || [];
                this.isDefaultInitial = isDefaultInitial;
            }
        }

        export class BusinessTypeModel {
            dailyPerformanceFormatCode: string;
            dailyPerformanceFormatName: string;
            constructor(data: IDailyPerformanceFormatType) {
                let self = this;
                if (!data) return;
                self.dailyPerformanceFormatCode = data.dailyPerformanceFormatCode || "";
                self.dailyPerformanceFormatName = data.dailyPerformanceFormatName || "";
            }
        }

        class FormatCode {
            formatCode: string;
            formatName: string;
            isSetDefault: boolean;
            constructor(data: IFormatCode) {
                this.formatCode = data.formatCode;
                this.formatName = data.formatName;
                this.isSetDefault = data.isSetDefault;
            }

            static fromDaily(listItem): Array<FormatCode> {
                let result = _.map(listItem, item => {
                    let dto: IFormatCode = {};
                    dto.formatCode = item.dailyPerformanceFormatCode;
                    dto.formatName = item.dailyPerformanceFormatName;
                    dto.isSetDefault = item.setFormatToDefault;
                    return new FormatCode(dto);
                })
                return _.sortBy(result, ["formatCode"]);
            }

            static fromMonthly(listItem): Array<FormatCode> {
                let result = _.map(listItem, item => {
                    let dto: IFormatCode = {};
                    dto.formatCode = item.monthlyPfmFormatCode;
                    dto.formatName = item.monPfmCorrectionFormatName;
                    dto.isSetDefault = item.setFormatToDefault;
                    return new FormatCode(dto);
                })
                return _.sortBy(result, ["formatCode"]);
            }
        }

        interface IFormatCode {
            formatCode: string;
            formatName: string;
            isSetDefault: boolean;
        }

        class AttendanceItemDto {
            attendanceItemId: number;
            attendanceItemName: string;
            attendanceItemDisplayNumber: number;
            columnWidth: number;
            constructor(data: IAttendanceItemDto) {
                if (!data) return;
                this.attendanceItemId = data.attendanceItemId;
                this.attendanceItemName = data.attendanceItemName || "";
                this.attendanceItemDisplayNumber = data.attendanceItemDisplayNumber;
                this.columnWidth = null;
            }
            static fromApp(app: Array<IAttendanceItemDto>): Array<AttendanceItemDto> {
                let items = _.map(app, (item: IAttendanceItemDto) => {
                    return new AttendanceItemDto(item);
                })
                return _.sortBy(items, ["attendanceItemDisplayNumber"])
            }
        }

        interface IAttendanceItemDto {
            attendanceItemId: number;
            attendanceItemName: string;
            attendanceItemDisplayNumber: number;
        }

        class DailyAttendanceAuthorityDetailDto {
            attendanceItemId: number;
            order: number;
            columnWidth: number;
            constructor(data: IDailyAttendanceAuthorityDetailDto) {
                this.attendanceItemId = data.attendanceItemId;
                this.order = data.order;
                this.columnWidth = data.columnWidth
            }

            static fromApp(listItem: Array<IDailyAttendanceAuthorityDetailDto>): Array<DailyAttendanceAuthorityDetailDto> {
                let result = _.map(listItem, (item: IDailyAttendanceAuthorityDetailDto) => {
                    return new DailyAttendanceAuthorityDetailDto(item);
                })
                return _.sortBy(result, ["order"]);
            }
        }

        interface IDailyAttendanceAuthorityDetailDto {
            attendanceItemId: number;
            order: number;
            columnWidth: number;
        }

        // Month Correction

        class MonPfmCorrectionFormatDto {
            monthlyPfmFormatCode: string;
            monPfmCorrectionFormatName: string;
            isSetFormatToDefault: boolean;
            displayItem: MonthlyActualResultsDto;
            constructor(data: IMonPfmCorrectionFormatDto) {
                this.monthlyPfmFormatCode = data.monthlyPfmFormatCode;
                this.monPfmCorrectionFormatName = data.monPfmCorrectionFormatName;
                this.isSetFormatToDefault = data.setFormatToDefault;
                this.displayItem = MonthlyActualResultsDto.fromApp(data.displayItem);
            }

            static fromApp(listItem: Array<IMonPfmCorrectionFormatDto>): Array<MonPfmCorrectionFormatDto> {
                let result = _.map(listItem, (item: IMonPfmCorrectionFormatDto) => {
                    return new MonPfmCorrectionFormatDto(item);
                })
                return result;
            }
        }

        interface IMonPfmCorrectionFormatDto {
            monthlyPfmFormatCode: string;
            monPfmCorrectionFormatName: string;
            setFormatToDefault: boolean;
            displayItem: IMonthlyActualResultsDto;
        }

        class MonthlyActualResultsDto {
            listSheetCorrectedMonthly: Array<SheetCorrectedMonthlyDto>;
            constructor(data: IMonthlyActualResultsDto) {
                this.listSheetCorrectedMonthly = SheetCorrectedMonthlyDto.fromApp(data.listSheetCorrectedMonthly);
            }

            static fromApp(item: IMonthlyActualResultsDto): MonthlyActualResultsDto {
                return new MonthlyActualResultsDto(item);
            }
        }

        interface IMonthlyActualResultsDto {
            listSheetCorrectedMonthly: Array<ISheetCorrectedMonthlyDto>;
        }

        class SheetCorrectedMonthlyDto {
            sheetNo: number;
            sheetName: string;
            listDisplayTimeItem: Array<DisplayTimeItemDto>;
            constructor(data: ISheetCorrectedMonthlyDto) {
                this.sheetNo = data.sheetNo;
                this.sheetName = data.sheetName;
                this.listDisplayTimeItem = DisplayTimeItemDto.fromApp(data.listDisplayTimeItem);
            }

            static fromApp(listItem: Array<ISheetCorrectedMonthlyDto>): Array<SheetCorrectedMonthlyDto> {
                return _.map(listItem, (item: ISheetCorrectedMonthlyDto) => {
                    return new SheetCorrectedMonthlyDto(item);
                })
            }
        }

        interface ISheetCorrectedMonthlyDto {
            sheetNo: number;
            sheetName: string;
            listDisplayTimeItem: Array<IDisplayTimeItemDto>;
        }

        class DisplayTimeItemDto {
            displayOrder: number;
            itemDaily: number;
            columnWidthTable: number;
            constructor(data: IDisplayTimeItemDto) {
                this.displayOrder = data.displayOrder;
                this.itemDaily = data.itemDaily;
                this.columnWidthTable = data.columnWidthTable;
            }

            static fromApp(listItem: Array<IDisplayTimeItemDto>): Array<DisplayTimeItemDto> {
                let result = _.map(listItem, (item: IDisplayTimeItemDto) => {
                    return new DisplayTimeItemDto(item);
                })
                return _.sortBy(result, ["displayOrder"]);
            }
        }

        interface IDisplayTimeItemDto {
            displayOrder: number;
            itemDaily: number;
            columnWidthTable: number;
        }
    }
}

