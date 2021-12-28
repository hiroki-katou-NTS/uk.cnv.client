module nts.uk.at.view.kdw008.b {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import block = nts.uk.ui.block;
    export module viewmodel {
        export class ScreenModel {
            hasdata: boolean;
            checkInitSheetNo: boolean;
            // list businessType
            businessTypeList: KnockoutObservableArray<BusinessTypeModel>;
            columnsBusinessType: KnockoutObservableArray<NtsGridListColumn>;
            selectedCode: KnockoutObservable<any>;
            currentBusinessTypeName: KnockoutObservable<string>;

            //combobox select sheetNo
            sheetNoList: KnockoutObservableArray<SheetNoModel>;
            selectedSheetNo: KnockoutObservable<number>;
            selectedSheetName: KnockoutObservable<string>;

            //swap list tab 1
            columns1: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            businessTypeFormatDailyValue: KnockoutObservableArray<AttendanceItemDto>;
            dailyDataSource: KnockoutObservableArray<AttendanceItemDto>;
            dailyAttItems: KnockoutObservableArray<AttendanceItemDto>;

            //swap list tab 2
            columns2: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            businessTypeFormatMonthlyValue: KnockoutObservableArray<AttendanceItemDto>;
            monthlyDataSource: KnockoutObservableArray<AttendanceItemDto>;
            monthlyAttItems: KnockoutObservableArray<AttendanceItemDto>;

            //swap list tab 3
            columns3: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            monthlyCorrectedDataSource: KnockoutObservableArray<AttendanceItemDto>;
            sheetCorrectedMonthly: KnockoutObservableArray<SheetCorrectedMonthlyDto>;
            monthlyCorrected: KnockoutObservableArray<AttendanceItemDto>;

            tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            selectedTab: KnockoutObservable<string>;

            mobileTabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            selectedMobileTab: KnockoutObservable<string>;

            //is daily
            isDaily: boolean;
            isMobile: boolean;

            sideBar: KnockoutObservable<number>;
            enableSheetNo: KnockoutObservable<boolean>;

            listBusinessCode: KnockoutObservableArray<string> = ko.observableArray([]);
            dailyHasData: boolean = false;
            copyButtonStatus: KnockoutObservable<boolean> = ko.observable(false);

            constructor(dataShare: any) {
                var self = this;
                //check daily
                self.isDaily = dataShare.ShareObject;
                self.isMobile = dataShare.mobile;
                self.sideBar = ko.observable(1);
                if (!self.isDaily) {
                    self.sideBar(2);
                }

                self.hasdata = true;
                self.checkInitSheetNo = false;
                self.enableSheetNo = ko.observable(false);

                self.currentBusinessTypeName = ko.observable(null);
                self.selectedSheetName = ko.observable(null);
                self.businessTypeList = ko.observableArray([]);
                self.columnsBusinessType = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'businessTypeCode', width: 100 },
                    { headerText: getText('KDW008_26'), key: 'businessTypeName', width: 180, formatter: _.escape }
                ]);
                this.selectedCode = ko.observable(null);
                self.selectedCode.subscribe(newValue => {
                    if (nts.uk.text.isNullOrEmpty(newValue)) return;
                    block.invisible();
                    let businessType = _.find(self.businessTypeList(), bus => {
                        return bus.businessTypeCode == newValue;
                    });
                    self.currentBusinessTypeName(businessType.businessTypeName);

                    if (self.isDaily) {
                        self.getMonthlyDetail(newValue).done(() => {
                            self.initSelectedSheetNoHasMutated();
                        });
                        
                    } else {
                        self.getMonthRight(newValue, self.selectedSheetNo()).done(() => {
                            self.initSelectedSheetNoHasMutated();
                        });
                    }
                });

                self.columns1 = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'attendanceItemDisplayNumber', width: 70 },
                    { headerText: getText('KDW008_7'), key: 'attendanceItemId', hidden: true, width: 100 },
                    { headerText: getText('KDW008_8'), key: 'attendanceItemName', width: 200, formatter: _.escape }
                ]);

                self.columns2 = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'attendanceItemDisplayNumber', width: 70 },
                    { headerText: getText('KDW008_7'), key: 'attendanceItemId', hidden: true, width: 100 },
                    { headerText: getText('KDW008_8'), key: 'attendanceItemName', width: 200, formatter: _.escape }
                ]);

                self.columns3 = ko.observableArray([
                    { headerText: getText('KDW008_7'), key: 'attendanceItemDisplayNumber', width: 70 },
                    { headerText: getText('KDW008_7'), key: 'attendanceItemId', hidden: true, width: 100 },
                    { headerText: getText('KDW008_8'), key: 'attendanceItemName', width: 200, formatter: _.escape }
                ]);

                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: getText('KDW008_14'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(self.isDaily) },
                    { id: 'tab-2', title: getText('KDW008_13'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(self.isDaily) },
                    { id: 'tab-3', title: getText('KDW008_13'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(!self.isDaily) },
                ]);
                
                if (self.isDaily) {
                    self.selectedTab = ko.observable('tab-1');
                    self.selectedMobileTab = ko.observable('tab-1');
                } else {
                    self.selectedTab = ko.observable('tab-3');
                    self.selectedMobileTab = ko.observable('tab-3');
                }

                self.mobileTabs = ko.observableArray([
                    { id: 'tab-1', title: getText('KDW008_14'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(self.isDaily) },
                    { id: 'tab-2', title: getText('KDW008_13'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(self.isDaily) },
                    { id: 'tab-3', title: getText('KDW008_13'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(!self.isDaily) },
                ]);
                // self.selectedMobileTab = ko.observable('tab-1');
               
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
                    if (self.isDaily) {
                        self.getDailyDetail(self.selectedCode(), value).done(() => {
                            service.checkMode(self.selectedCode()).done((res) => {
                                self.dailyHasData = res == 0 ? false : true;
                                if (self.hasdata && self.dailyHasData || self.hasdata && self.businessTypeFormatMonthlyValue().length !== 0) {
                                    self.copyButtonStatus(true);
                                } else {
                                    self.copyButtonStatus(false);
                                }
                                block.clear();
                            })
                            
                        })
                    } else {
                        self.getMonthRightDetail(value);
                        if (self.hasdata && self.sheetCorrectedMonthly().length !== 0) {
                            self.copyButtonStatus(true);
                        } else {
                            self.copyButtonStatus(false);
                        }
                        block.clear();
                    }
                });

                //swap list 1
                self.businessTypeFormatDailyValue = ko.observableArray([]);
                self.dailyDataSource = ko.observableArray([]);

                //swap list 2
                self.businessTypeFormatMonthlyValue = ko.observableArray([]);
                self.monthlyDataSource = ko.observableArray([]);

                self.dailyAttItems = ko.observableArray([]);
                self.monthlyAttItems = ko.observableArray([]);

                //swap list 3
                self.monthlyCorrectedDataSource = ko.observableArray([]);
                self.sheetCorrectedMonthly = ko.observableArray([]);
                self.monthlyCorrected = ko.observableArray([]);
            }

            initSelectedSheetNoHasMutated() {
                let self = this;
                if(self.checkInitSheetNo){
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
            
            initSelectedCodeHasMutated() {
                let self = this;
                self.checkInitSheetNo = true;
                self.selectedCode.valueHasMutated();
            }

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                block.invisible();
                self.getBusinessType().done(() => {
                    if (self.isDaily) {
                        let dtdGetDailyAttItem = self.getDailyAttItem();
                        let dtdGetMonthlyAttItem = self.getMonthlyAttItem();
                        $.when(dtdGetDailyAttItem, dtdGetMonthlyAttItem).done(() => {
                            let businessItem = self.businessTypeList()[0];
                            self.selectedCode(businessItem.businessTypeCode);
                        }).always(() => {
                            block.clear();
                        })
                    } else {
                        let dtdGetMonthlyAttItem = self.getMonthlyAttItem();
                        $.when(dtdGetMonthlyAttItem).done(() => {
                            let businessItem = self.businessTypeList()[0];
                            self.selectedCode(businessItem.businessTypeCode);
                        }).always(() => {
                            block.clear();
                        })
                    }
                    dfd.resolve();
                }).fail(() => {
                    block.clear();
                    nts.uk.ui.dialog.alert({ messageId: "Msg_242" });
                    self.hasdata = false;
                    dfd.resolve();
                })
                return dfd.promise();
            }


            copy() {
                let self = this;
                block.invisible();
                service.getListMonthlyRecordWorkType().done((res: Array<MonthlyRecordWorkTypeDto>) => {
                    self.listBusinessCode(res.map((item) => item.businessTypeCode));

                    let param = {
                        code: self.selectedCode(),
                        name: self.currentBusinessTypeName(),
                        targetType: 9,
                        itemListSetting: self.listBusinessCode()
                    };
                    nts.uk.ui.windows.setShared("CDL023Input", param);
                    nts.uk.ui.windows.sub.modal("com", "/view/cdl/023/a/index.xhtml").onClosed(() => {
                        let output: Array<string> = nts.uk.ui.windows.getShared("CDL023Output");
                        if (output) {
                            let command = {
                                businessTypeCode: self.selectedCode(),
                                listBusinessTypeCode: output
                            }

                            console.log(command, 'param');

                            if (self.isDaily) {
                                service.copyDaily(command).done(() => {
                                    nts.uk.ui.dialog.info({ messageId: 'Msg_15' }).then(() => {
                                        self.initSelectedCodeHasMutated();
                                        console.log('msg15');
                                    });
                                })
                                .fail(() => {});

    
                            } else {
                                service.copyMonthly(command).done(() => {
                                    nts.uk.ui.dialog.info({ messageId: 'Msg_15' }).then(() => {
                                        self.initSelectedCodeHasMutated();
                                        console.log('msg15');
                                    });
                                }).fail(() => {});
                            }
                        }
                    })

                }).fail((err) => {})
                .always(() => block.clear());
            }

            getBusinessType(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                service.getBusinessType().done(function(data: Array<IBusinessType>) {
                    if (data && data.length > 0) {
                        self.businessTypeList(_.map(data, item => { return new BusinessTypeModel(item) }));
                        dfd.resolve();
                    } else {
                        dfd.reject();
                    }
                }).fail(function(res) {
                    dfd.reject();
                })
                return dfd.promise();
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

            getDailyDetail(businessTypeCode: string, sheetNo: string): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                service.getDailyDetail(businessTypeCode, self.selectedSheetNo(), self.isMobile).done(data => {
                    $("#swap-list2-grid2").igGridSelection("clearSelection");
                    $("#swap-list2-grid1").igGridSelection("clearSelection");
                    self.businessTypeFormatDailyValue.removeAll();
                    self.dailyDataSource.removeAll();
                    self.dailyDataSource(_.cloneDeep(self.dailyAttItems()));

                    if (data) {
                        self.selectedSheetName(data.sheetName);
                        self.businessTypeFormatDailyValue(self.mapAttItemFormatDetail(self.dailyAttItems(), data.businessTypeFormatDetailDtos));
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

            getMonthlyDetail(businessTypeCode: string): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                service.getMonthlyDetail(businessTypeCode, self.isMobile).done(data => {
                    $("#swap-list-grid2").igGridSelection("clearSelection");
                    $("#swap-list-grid1").igGridSelection("clearSelection");
                    self.businessTypeFormatMonthlyValue.removeAll();
                    self.monthlyDataSource.removeAll();
                    self.monthlyDataSource(_.cloneDeep(self.monthlyAttItems()));
                    if (data) {
                        let dat = self.mapAttItemFormatDetail(self.monthlyAttItems(), data.businessTypeFormatMonthlyDtos);
                        self.businessTypeFormatMonthlyValue(dat);
                    }
                    dfd.resolve();
                }).fail(err => {
                    dfd.reject(err);
                })
                return dfd.promise();
            }

            mapAttItemFormatDetail(attItems: Array<AttendanceItemDto>, details): Array<AttendanceItemDto> {
                details = BusinessTypeFormatDetailDto.fromApp(details);
                let attItemDetail: Array<AttendanceItemDto> = [];
                for(let i = 0;i<details.length;i++){
                    let item = details[i];
                    let dto: AttendanceItemDto = new AttendanceItemDto(null);
                    dto.attendanceItemId = item.attendanceItemId;
                    dto.columnWidth = item.columnWidth;
                    let attItem: AttendanceItemDto = _.find(attItems, (att: AttendanceItemDto) => {
                        return att.attendanceItemId == item.attendanceItemId;
                    })
                    if (attItem) {
                        dto.attendanceItemName = attItem.attendanceItemName;
                        dto.attendanceItemDisplayNumber = attItem.attendanceItemDisplayNumber;
                        attItemDetail.push(dto);
                    }
                }
                return attItemDetail;
            }

            getMonthRight(code: string, no: number): JQueryPromise<any> {
                let self = this;
                dfd = $.Deferred();

                service.getListMonthRight(code).done(function(data) {
                    if (data) {
                        self.sheetCorrectedMonthly(SheetCorrectedMonthlyDto.fromApp(data.displayItem.listSheetCorrectedMonthly));
                        
                    }else{
                        self.sheetCorrectedMonthly(SheetCorrectedMonthlyDto.fromApp([]));
                    }
                    console.log(self.sheetCorrectedMonthly(), 'sheet corected monthly');
                    dfd.resolve();
                }).fail(err => {
                    dfd.reject(err);
                });
                return dfd.promise();
            }

            getMonthRightDetail(sheetNo: string) {
                let self = this;
                $("#swap-list3-grid2").igGridSelection("clearSelection") ;
                self.selectedSheetName(null);
                self.monthlyCorrected.removeAll();
                self.monthlyCorrectedDataSource.removeAll();
                self.monthlyCorrectedDataSource(_.cloneDeep(self.monthlyAttItems()));
                let sheetItem: SheetCorrectedMonthlyDto = _.find(self.sheetCorrectedMonthly(), (item: SheetCorrectedMonthlyDto) => {
                    return item.sheetNo.toString() == sheetNo;
                })
                if (sheetItem) {
                    self.selectedSheetName(sheetItem.sheetName);
                    self.monthlyCorrected(this.mapAttItemMonthRightDetail(self.monthlyAttItems(), sheetItem.listDisplayTimeItem));
                }
            }

            mapAttItemMonthRightDetail(attItems: Array<AttendanceItemDto>, details: Array<DisplayTimeItemDto>): Array<AttendanceItemDto> {
                let attItemDetail: Array<AttendanceItemDto> = [];
//                attItemDetail = _.map(details, function(item: DisplayTimeItemDto) {
                for(let i = 0;i<details.length;i++){
                    let item = details[i];
                    let dto: AttendanceItemDto = new AttendanceItemDto(null);
                    dto.attendanceItemId = item.itemDaily;
                    dto.columnWidth = item.columnWidthTable;
                    let attItem: AttendanceItemDto = _.find(attItems, (att: AttendanceItemDto) => {
                        return att.attendanceItemId == item.itemDaily;
                    })
                    if (attItem) {
                        dto.attendanceItemName = attItem.attendanceItemName;
                        dto.attendanceItemDisplayNumber = attItem.attendanceItemDisplayNumber;
                        attItemDetail.push(dto);
                    }
                }
//                    let dto: AttendanceItemDto = new AttendanceItemDto(null);
//                    dto.attendanceItemId = item.itemDaily;
//                    dto.columnWidth = item.columnWidthTable;
//                    let attItem: AttendanceItemDto = _.find(attItems, (att: AttendanceItemDto) => {
//                        return att.attendanceItemId == item.itemDaily;
//                    })
//                    if (attItem) {
//                        dto.attendanceItemName = attItem.attendanceItemName;
//                        dto.attendanceItemDisplayNumber = attItem.attendanceItemDisplayNumber;
//                    }
//                    return dto;
//                })
                return attItemDetail;
            }

            jumpTo(sidebar) {
                let self = this;
                if(!self.isMobile) {
                    nts.uk.request.jump("/view/kdw/006/a/index.xhtml", { ShareObject: sidebar() });  
                } else {
                    nts.uk.request.jump("com","/view/ksp/001/a/index.xhtml", { ShareObject: sidebar() });
                }
            }

            btnSheetNo() {
                let self = this;
                if (self.isDaily) {
                    let deleteBySheet = {
                        businessTypeCode: self.selectedCode(),
                        sheetNo: self.selectedSheetNo()
                    };
                    confirm({ messageId: "Msg_18" }).ifYes(() => {
                        block.invisible();
                        service.deleteBusiFormatBySheet(deleteBySheet).done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_991" }).then(() => {
                                self.initSelectedCodeHasMutated();
                            });
                        }).fail(function(error) {
                            $('#currentCode').ntsError('set', error);
                        }).always(function() {
                            block.clear();
                        });
                    })
                } else {
                    //monthly                    
                    confirm({ messageId: "Msg_18" }).ifYes(() => {
                        block.invisible();
                        service.updateMonthly(this.getMonthlyRightCmdDel()).done(function() {
                            nts.uk.ui.dialog.info({ messageId: "Msg_991" }).then(() => {
                                self.initSelectedCodeHasMutated();
                            });
                            $("#currentName").focus();
                        }).fail(function(error) {
                            $('#currentCode').ntsError('set', error);
                        }).always(function() {
                            block.clear();
                        });
                    });
                }
            }

            addOrUpdateClick() {
                let self = this;
                if (self.isDaily) {
                    let self = this; $(".need-check").trigger("validate");
                    if (nts.uk.ui.errors.hasError()) {
                        return;
                    }
                    self.register();
                } else {
                    $("#selectedSheetName").trigger("validate");
                    if (nts.uk.ui.errors.hasError()) {
                        return;
                    }
                    if (self.monthlyCorrected().length <= 0) {
                        nts.uk.ui.dialog.alert({ messageId: "Msg_920" });
                    } else {
                        self.registerMonthly();
                    }
                }
            }

            private getMonthlyRightCmdDel(): MonthlyRecordWorkTypeDto {
                let self = this;
                let listDisplayTimeItem: Array<IDisplayTimeItemDto> = [];
                return this.getMonthlyRightCmd(listDisplayTimeItem);
            }

            private getMonthlyRightCmdUpdate(): MonthlyRecordWorkTypeDto {
                let self = this;
                let listDisplayTimeItem: Array<IDisplayTimeItemDto> = _.map(self.monthlyCorrected(), (item: AttendanceItemDto) => {
                    let indexOfItem = _.findIndex(self.monthlyCorrected(), { attendanceItemId: item.attendanceItemId });
                    let obj: IDisplayTimeItemDto = {};
                    obj.itemDaily = item.attendanceItemId
                    obj.columnWidthTable = item.columnWidth;
                    obj.displayOrder = indexOfItem;
                    return obj;
                })

                return this.getMonthlyRightCmd(listDisplayTimeItem);
            }

            private getMonthlyRightCmd(listDisplayTimeItem: Array<IDisplayTimeItemDto>): MonthlyRecordWorkTypeDto {
                let self = this;
                let sheetDto: ISheetCorrectedMonthlyDto = {};
                sheetDto.sheetNo = self.selectedSheetNo();
                sheetDto.sheetName = self.selectedSheetName();
                sheetDto.listDisplayTimeItem = listDisplayTimeItem;
                let listSheetMonthly = [new SheetCorrectedMonthlyDto(sheetDto)];

                return new MonthlyRecordWorkTypeDto("", self.selectedCode(),
                    new MonthlyActualResultsDto(listSheetMonthly)
                );
            }

            registerMonthly() {
                let self = this;
                block.invisible();
                service.updateMonthly(this.getMonthlyRightCmdUpdate()).done(function() {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        self.initSelectedCodeHasMutated();
                    });
                    $("#currentName").focus();
                }).fail(function(error) {
                    $('#currentCode').ntsError('set', error);
                }).always(function() {
                    block.clear();
                });
            }

            register() {
                let self = this;
                //add or update Monthly
                let listMonthlyDetail = _.map(self.businessTypeFormatMonthlyValue(), (item: AttendanceItemDto) => {
                    let indexOfItem = _.findIndex(self.businessTypeFormatMonthlyValue(), { attendanceItemId: item.attendanceItemId });
                    let monthlyAdd: IBusinessTypeFormatDetailDto = {};
                    monthlyAdd.attendanceItemId = item.attendanceItemId;
                    monthlyAdd.columnWidth = item.columnWidth;
                    monthlyAdd.order = indexOfItem;
                    return new BusinessTypeFormatDetailDto(monthlyAdd);
                })
                let addOrUpdateBusinessFormatMonthly = new AddBusinessFormatMonthly(self.selectedCode(), listMonthlyDetail);

                //add or update Daily
                let businessTypeFormatDetailDailyDto = _.map(self.businessTypeFormatDailyValue(), (item: AttendanceItemDto) => {
                    let indexOfItem = _.findIndex(self.businessTypeFormatDailyValue(), { attendanceItemId: item.attendanceItemId });
                    let dailyAdd: IBusinessTypeFormatDetailDto = {};
                    dailyAdd.attendanceItemId = item.attendanceItemId;
                    dailyAdd.columnWidth = item.columnWidth;
                    dailyAdd.order = indexOfItem;
                    return new BusinessTypeFormatDetailDto(dailyAdd);
                });
                let addOrUpdateBusinessFormatDaily = new AddBusinessFormatDaily(self.selectedCode(), self.selectedSheetNo(), self.selectedSheetName(), businessTypeFormatDetailDailyDto);

                let addOrUpdateBusFormat = new AddOrUpdateBusFormat(addOrUpdateBusinessFormatMonthly, addOrUpdateBusinessFormatDaily);

                service.addDailyDetail(addOrUpdateBusFormat, self.isMobile).done(function() {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        self.initSelectedCodeHasMutated();
                    });
                    nts.uk.ui.block.clear();
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_920' });
                });
            }

            dialog() {
                let self = this;
                nts.uk.ui.windows.setShared("openC", self.isDaily);
                nts.uk.ui.windows.setShared("isMob", self.isMobile);
                nts.uk.ui.windows.sub.modal("../c/index.xhtml");
            }

            startMobilePage() {
                let self = this;
                let dfd = $.Deferred();
                block.invisible();
                self.getBusinessType().done(() => {
                    let dtdGetDailyAttItem = self.getDailyAttItem();
                    let dtdGetMonthlyAttItem = self.getMonthlyAttItem();
                    $.when(dtdGetDailyAttItem, dtdGetMonthlyAttItem).done(() => {
                        let businessItem = self.businessTypeList()[0];
                        self.selectedCode(businessItem.businessTypeCode);
                    }).always(() => {
                        block.clear();
                    })
                   
                    dfd.resolve();
                }).fail(() => {
                    block.clear();
                    nts.uk.ui.dialog.alert({ messageId: "Msg_242" });
                    self.hasdata = false;
                    dfd.resolve();
                })
                return dfd.promise();
            }

        }

        //monthly

        class MonthlyRecordWorkTypeDto {
            companyID: string;
            businessTypeCode: string;
            displayItem: MonthlyActualResultsDto;
            constructor(companyID: string,
                businessTypeCode: string,
                displayItem: MonthlyActualResultsDto) {
                this.companyID = companyID;
                this.businessTypeCode = businessTypeCode;
                this.displayItem = displayItem;

            }
        }

        class MonthlyActualResultsDto {
            listSheetCorrectedMonthly: Array<SheetCorrectedMonthlyDto>;
            constructor(listSheetCorrectedMonthly: Array<SheetCorrectedMonthlyDto>) {
                this.listSheetCorrectedMonthly = listSheetCorrectedMonthly;
            }
        }

        class SheetNoModel {
            sheetNoId: string;
            sheetNoName: string;
            constructor(sheetNoId: string, sheetNoName: string) {
                this.sheetNoId = sheetNoId;
                this.sheetNoName = sheetNoName;
            }
        }

        class AddOrUpdateBusFormat {
            busTypeMonthlyCommand: KnockoutObservable<AddBusinessFormatMonthly>;
            busTypeDailyCommand: KnockoutObservable<AddBusinessFormatDaily>;
            constructor(addOrUpdateAuthorityFormatMonthly: KnockoutObservable<AddBusinessFormatMonthly>, addOrUpdateAuthorityFormatDaily: KnockoutObservable<AddBusinessFormatDaily>) {
                let self = this;
                self.busTypeMonthlyCommand = addOrUpdateAuthorityFormatMonthly;
                self.busTypeDailyCommand = addOrUpdateAuthorityFormatDaily;
            }
        }

        class AddBusinessFormatMonthly {
            businesstypeCode: string;
            businessTypeFormatDetailDtos: Array<BusinessTypeFormatDetailDto>;
            constructor(businessTypeCode: string, businessTypeFormatDetailDtos: Array<BusinessTypeFormatDetailDto>) {
                let self = this;
                self.businesstypeCode = businessTypeCode || "";
                self.businessTypeFormatDetailDtos = businessTypeFormatDetailDtos || [];
            }
        }

        class AddBusinessFormatDaily {
            businesstypeCode: string;
            sheetNo: number;
            sheetName: string;
            businessTypeFormatDetailDtos: Array<BusinessTypeFormatDetailDto>;
            constructor(businesstypeCode: string, sheetNo: number, sheetName: string, businessTypeFormatDetailDtos: Array<BusinessTypeFormatDetailDto>) {
                this.businesstypeCode = businesstypeCode || "";
                this.sheetNo = sheetNo || 0;
                this.sheetName = sheetName || null;
                this.businessTypeFormatDetailDtos = businessTypeFormatDetailDtos || [];
            }
        }

        class BusinessTypeModel {
            businessTypeCode: string;
            businessTypeName: string;
            constructor(data: IBusinessType) {
                let self = this;
                if (!data) return;
                self.businessTypeCode = data.businessTypeCode || "";
                self.businessTypeName = data.businessTypeName || "";
            }
        }

        class AttendanceItemDto {
            attendanceItemId: number;
            attendanceItemName: string;
            displayName: string;
            attendanceItemDisplayNumber: number;
            columnWidth?: number;
            constructor(data: IAttendanceItemDto) {
                if (!data) return;
                this.attendanceItemId = data.attendanceItemId;
                //this.attendanceItemName = data.attendanceItemName || "";
                if(data.displayName && data.displayName.length > 0) {
                    this.attendanceItemName = data.displayName;
                }
                else {
                    this.attendanceItemName = data.attendanceItemName
                }
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
            displayName: string;
            attendanceItemDisplayNumber: number;
        }

        class BusinessTypeFormatDetailDto {
            attendanceItemId: number;
            order: number;
            columnWidth: number;
            constructor(data: IBusinessTypeFormatDetailDto) {
                if (!data) return;
                this.attendanceItemId = data.attendanceItemId || 0;
                this.order = data.order || 0;
                this.columnWidth = data.columnWidth || 0;
            }
            static fromApp(app): Array<BusinessTypeFormatDetailDto> {
                let items = _.map(app, (item: IBusinessTypeFormatDetailDto) => {
                    return new BusinessTypeFormatDetailDto(item);
                })
                return _.sortBy(items, ["order"]);
            }
        }
        interface IBusinessTypeFormatDetailDto {
            attendanceItemId: number;
            order: number;
            columnWidth: number;
        }

        class SheetCorrectedMonthlyDto {
            sheetNo: number;
            sheetName: string;
            listDisplayTimeItem: Array<DisplayTimeItemDto>;
            constructor(app: ISheetCorrectedMonthlyDto) {
                this.sheetNo = app.sheetNo;
                this.sheetName = app.sheetName;
                this.listDisplayTimeItem = DisplayTimeItemDto.fromApp(app.listDisplayTimeItem);
            }
            static fromApp(appItems: ISheetCorrectedMonthlyDto): SheetCorrectedMonthlyDto {
                let items = _.map(appItems, (item: ISheetCorrectedMonthlyDto) => {
                    return new SheetCorrectedMonthlyDto(item);
                })
                return items;
            }
        }
        interface ISheetCorrectedMonthlyDto {
            sheetNo: number;
            sheetName: string;
            listDisplayTimeItem: Array<IDisplayTimeItemDto>;
        }

        class DisplayTimeItemDto {
            displayOrder: number;
            itemDaily: number
            columnWidthTable: number;
            constructor(app: IDisplayTimeItemDto) {
                this.displayOrder = app.displayOrder;
                this.itemDaily = app.itemDaily;
                this.columnWidthTable = app.columnWidthTable;
            }
            static fromApp(appItems: Array<IDisplayTimeItemDto>): Array<DisplayTimeItemDto> {
                let items = _.map(appItems, (item: IDisplayTimeItemDto) => {
                    return new DisplayTimeItemDto(item);
                })
                return _.sortBy(items, ["displayOrder"]);
            }
        }
        interface IDisplayTimeItemDto {
            displayOrder: number;
            itemDaily: number
            columnWidthTable: number;
        }
    }

}
