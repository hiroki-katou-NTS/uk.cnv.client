module cps003.a.vm {
    import info = nts.uk.ui.dialog.info;
    import alert = nts.uk.ui.dialog.alert;
    import alertError = nts.uk.ui.dialog.alertError;
    import text = nts.uk.resource.getText;
    import format = nts.uk.text.format;
    import confirm = nts.uk.ui.dialog.confirm;
    import modal = nts.uk.ui.windows.sub.modal;
    import modeless = nts.uk.ui.windows.sub.modeless;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import showDialog = nts.uk.ui.dialog;
    import hasError = nts.uk.ui.errors.hasError;
    import clearError = nts.uk.ui.errors.clearAll;
    import liveView = nts.uk.request.liveView;

    const REPL_KEY = '__REPLACE',
        __viewContext: any = window['__viewContext'] || {},
        block = window["nts"]["uk"]["ui"]["block"]["grayout"],
        unblock = window["nts"]["uk"]["ui"]["block"]["clear"],
        invisible = window["nts"]["uk"]["ui"]["block"]["invisible"];

    export class ViewModel {
        ccgcomponent: any = {
            /** Common properties */
            systemType: 1, // シスッ�区�
            showEmployeeSelection: true, // 検索タイ�
            showQuickSearchTab: true, // クイヂ�検索
            showAdvancedSearchTab: true, // 詳細検索
            showBaseDate: false, // 基準日利用
            showClosure: false, // 就業�め日利用
            showAllClosure: true, // 全�め表示
            showPeriod: false, // 対象期間利用
            periodFormatYM: true, // 対象期間精度

            /** Required parame*/
            baseDate: moment.utc().toISOString(), // 基準日
            periodStartDate: moment.utc("1900/01/01", "YYYY/MM/DD").toISOString(), // 対象期間開始日
            periodEndDate: moment.utc("9999/12/31", "YYYY/MM/DD").toISOString(), // 対象期間終亗�
            inService: true, // 在職区�
            leaveOfAbsence: true, // 休�区�
            closed: true, // 休業区�
            retirement: false, // 退職区�

            /** Quick search tab options */
            showAllReferableEmployee: true, // 参�可能な社員すべて
            showOnlyMe: true, // 自刁��
            showSameWorkplace: true, // 同じ職場の社員
            showSameWorkplaceAndChild: true, // 同じ職場とそ�配下�社員

            /** Advanced search properties */
            showEmployment: true, // 雔�条件
            showWorkplace: true, // 職場条件
            showClassification: true, // 刡�条件
            showJobTitle: true, // 職位条件
            showWorktype: false, // 勤種条件
            isMutipleCheck: true, // 選択モー�

            /** Return data */
            returnDataFromCcg001: (data: any) => {
                let self = this;
                
                self.employees(data.listEmployee);
                $.blockUI({
                    message: '<div class="block-ui-message">' + nts.uk.ui.toBeResource.plzWait + '</div>',
                    fadeIn: 200,
                    onUnblock: () => block()
                });
                
                setTimeout(() => self.requestData(null, true), 700);
            }
        };

        gridList = {
            inData: {
                employees: ko.observableArray([]),
                itemDefitions: ko.observableArray([])
            },
            outData: ko.observableArray([])
        }

        headDatas: any;
        gridOptions: any = { columns: [], ntsControls: [], dataSource: [] };
        dataTypes: any = {};
        initDs: Array<Record>;
        batchSettingItems: any = [];
        lockColumns: Array<any> = [];

        baseDate: KnockoutObservable<Date> = ko.observable();
        baseDateEnable: KnockoutObservable<boolean> = ko.observable(true);
        baseDateChangeHist: Array<any> = [];
        histType: KnockoutObservable<string> = ko.observable();
        updateMode: KnockoutObservable<number> = ko.observable(1);
        updateModeEnable: KnockoutObservable<boolean> = ko.observable(true);
        perInfoCatePermission: KnockoutObservable<any> = ko.observable();

        category: {
            cate: KnockoutObservable<any>;
            catId: KnockoutObservable<string>;
            catCode: KnockoutObservable<string>;
            items: KnockoutObservableArray<any>;
        } = {
            cate: ko.observable(),
            catId: ko.observable(''),
            catCode: ko.observable(''),
            items: ko.observableArray([])
        };

        settings: ISettingData = {
            matrixDisplay: ko.observable({}),
            perInfoData: ko.observableArray([])
        };

        hiddenRows = [];
        hiddenEmpIds = [];
        // for employee info.
        employees: KnockoutObservableArray<IEmployee> = ko.observableArray([]);
        hasError: KnockoutObservable<boolean> = ko.observable(false);
        fixedColumns = [ "rowNumber", "register", "print", "showControl", "employeeCode", "employeeName", 
                        "rowAdd", "deptName", "workplaceName", "positionName", "employmentName", "className" ];

        isCS00100: KnockoutObservable<boolean> = ko.observable(false);
        isFromCPS018: KnockoutObservable<boolean> = ko.observable(false);
    
        constructor() {
            let self = this;

            let params = getShared("CPS003A_PARAMS") || { isFromCPS018: false };
            self.isFromCPS018(params.isFromCPS018);
            nts.uk.sessionStorage.removeItem(nts.uk.request.STORAGE_KEY_TRANSFER_DATA);

            cps003.control.selectButton();
            cps003.control.relateButton();
            cps003.control.validateDateRange();
            cps003.control.extendTimeRange();
//            self.baseDate(nts.uk.time.today().format("YYYY/MM/DD"));

            //fetch all category by login 
            service.fetch.category(__viewContext.user.employeeId)
                .done(data => {
                    if (!data || data.length === 0) {
                        alertError({ messageId: "Msg_1460" }).then(() => {
                            nts.uk.request.jumpToTopPage();
                        });
                        
                        return;
                    }
                    
                    self.category.items(data);
                    
                    nts.uk.request.syncAjax("com", "server/time/today/").done(function(res) {
                        self.baseDate(res);
                    });
                });
            
            self.baseDate.subscribe(date => {
                if (!$("#base-date").ntsError("hasError") && self.category.catId() !== "" && !_.isNil(self.category.catId())) {
                    self.requestData();
                }
                
                if (!_.isNil(date) 
                    && (date !== self.baseDateChangeHist[self.baseDateChangeHist.length - 1] || self.baseDateChangeHist.length == 0)) {
                    self.baseDateChangeHist.push(date);
                }
            });

            self.category.catId.subscribe((cid: string) => { 
                if (cid) {
                    let cate = _.find(self.category.items(), c => c.id === self.category.catId());
                    if (cate) {
                        if (cate.categoryCode == 'CS00100') {
                            self.isCS00100(true);
                        } else {
                            self.isCS00100(false);
                        }
                        self.category.cate(cate);
                        self.category.catCode(cate.categoryCode);
                        let roleId = __viewContext.user.role.personalInfo;
                        
                        service.fetch.permission(roleId, cid).done(permission => {
                            if (permission) self.perInfoCatePermission(permission);
                        });
                        
                        // fetch all setting
                        service.fetch.setting(cid).done((data: ISettingData) => {
                            self.requestData(data).done(() => {
                                if (cate.categoryType === IT_CAT_TYPE.SINGLE) {
                                    self.histType(nts.uk.resource.getText("CPS003_108"));
                                    self.updateMode(1);
                                    self.updateModeEnable(false);
                                    self.baseDateEnable(true);
                                } else {
                                    let permission = self.perInfoCatePermission();
                                    switch (cate.categoryType) {
                                        case IT_CAT_TYPE.SINGLE:
                                            break;
                                        case IT_CAT_TYPE.MULTI:
                                            self.histType(nts.uk.resource.getText("CPS003_109"));
                                            self.updateMode(1);
                                            self.updateModeEnable(false);
                                            self.baseDateEnable(true);
                                            break;
                                        case IT_CAT_TYPE.CONTINU:
                                        case IT_CAT_TYPE.CONTINUWED:
                                            self.histType(nts.uk.resource.getText("CPS003_110"));
                                            if (permission && permission.otherFutureHisAuth === 1 && permission.otherPastHisAuth === 1
                                                && permission.selfFutureHisAuth === 1 && permission.selfPastHisAuth === 1) {
                                                self.baseDateEnable(false);
                                            } else {
                                                self.baseDateEnable(true);
                                            }
                                            
                                            if (permission && permission.otherAllowAddHis === 0 && permission.selfAllowAddHis === 0) {
                                                self.updateMode(1);
                                                self.updateModeEnable(false);
                                            } else {
                                                self.updateModeEnable(true);
                                            }
                                            break;
                                        case IT_CAT_TYPE.NODUPLICATE:
                                            self.histType(nts.uk.resource.getText("CPS003_111"));
                                            if (permission && permission.otherFutureHisAuth === 1 && permission.otherPastHisAuth === 1
                                                && permission.selfFutureHisAuth === 1 && permission.selfPastHisAuth === 1) { 
                                                self.baseDateEnable(false);
                                            } else {
                                                self.baseDateEnable(true);
                                            }
                                            
                                            if (permission && permission.otherAllowAddHis === 0 && permission.selfAllowAddHis === 0) {
                                                self.updateMode(1);
                                                self.updateModeEnable(false);
                                            } else {
                                                self.updateModeEnable(true);
                                            }
                                            break;
                                        case IT_CAT_TYPE.DUPLICATE:
                                            self.histType(nts.uk.resource.getText("CPS003_112"));
                                            if (permission && permission.otherFutureHisAuth === 1 && permission.otherPastHisAuth === 1
                                                && permission.selfFutureHisAuth === 1 && permission.selfPastHisAuth === 1) {
                                                self.baseDateEnable(false);
                                            } else {
                                                self.baseDateEnable(true);
                                            }
                                            
                                            if (permission && permission.otherAllowAddHis === 0 && permission.selfAllowAddHis === 0) {
                                                self.updateMode(1);
                                                self.updateModeEnable(false);
                                            } else {
                                                self.updateModeEnable(true);
                                            }
                                            break;
                                    }
                                    
                                    if (self.category.cate().categoryCode === "CS00003"
                                        || self.category.cate().categoryCode === "CS00070") {
                                        self.updateModeEnable(false);
                                    }
                                }
                            });
                            
                            if (ko.isObservable(self.settings.matrixDisplay)) {
                                if (_.size(self.settings.matrixDisplay()) == 0) {
                                    self.settings.matrixDisplay(data.matrixDisplay);
                                }
                            }
    
                            if (ko.isObservable(self.settings.perInfoData)) {
                                self.settings.perInfoData(data.perInfoData);
                            }
                        });
                    }
                    
                }
            });

            setTimeout(() => {
                $('#ccgcomponent').ntsGroupComponent(self.ccgcomponent).done(() => { });
            }, 1);
            
            self.baseDateEnable.subscribe(enable => {
                nts.uk.request.syncAjax("com", "server/time/today/").done(function(res) {
                    self.baseDate(res);
                });
            });
            
            self.settings.matrixDisplay.subscribe(matrix => {
                let $grid = $("#grid");
                if (!$grid.data("mGrid")) return;
                $grid.mGrid("directEnter", matrix && matrix.cursorDirection == CURSOR_DIRC.VERTICAL ? "below" : "right");
                $grid.mGrid(!matrix || matrix.clsATR === IUSE_SETTING.NOT_USE ? "hideColumn" : "showColumn", "className");
                $grid.mGrid(!matrix || matrix.jobATR === IUSE_SETTING.NOT_USE ? "hideColumn" : "showColumn", "positionName");
                $grid.mGrid(!matrix || matrix.workPlaceATR === IUSE_SETTING.NOT_USE ? "hideColumn" : "showColumn", "workplaceName");
                $grid.mGrid(!matrix || matrix.departmentATR === IUSE_SETTING.NOT_USE ? "hideColumn" : "showColumn", "deptName");
                $grid.mGrid(!matrix || matrix.employmentATR === IUSE_SETTING.NOT_USE ? "hideColumn" : "showColumn", "employmentName");
            });
            
            self.updateMode.subscribe(mode => {
                let $grid = $("#grid");
                if (!$grid.data("mGrid")) return;
                if (mode === 1) {
                    if (self.category.cate().categoryType === IT_CAT_TYPE.DUPLICATE
                        || self.category.cate().categoryType === IT_CAT_TYPE.SINGLE
                        || self.category.cate().categoryType === IT_CAT_TYPE.CONTINUWED
                        || self.category.cate().categoryType === IT_CAT_TYPE.CONTINU
                        || self.category.cate().categoryType === IT_CAT_TYPE.NODUPLICATE
                        || (self.category.cate().categoryType === IT_CAT_TYPE.MULTI
                            && self.perInfoCatePermission().otherAllowAddMulti === 0 
                            && self.perInfoCatePermission().selfAllowAddMulti === 0)) {
                        $grid.mGrid("hideColumn", "rowAdd");
                    } else {
                        $grid.mGrid("showColumn", "rowAdd");
                    }
                    
                    let insertions = $grid.mGrid("insertions");
                    if (insertions.length > 0) {
                        confirm({ messageId: "Msg_1468" }).ifYes(() => {
                            $grid.mGrid("removeInsertions");
                        }).ifCancel(() => {
                            self.updateMode(2);
                        });
                    }
                } else {
                    if (self.category.cate().categoryType === IT_CAT_TYPE.DUPLICATE
                        || (self.category.cate().categoryType === IT_CAT_TYPE.MULTI
                            && (self.perInfoCatePermission().otherAllowAddMulti === 1 
                                || self.perInfoCatePermission().selfAllowAddMulti === 1))) {
                        $grid.mGrid("showColumn", "rowAdd");
                    } else {
                        $grid.mGrid("hideColumn", "rowAdd");
                    }
                }
            });
        }

        start() {
            let self = this;
        }

        saveData() {
            let self = this,
                command: {
                };

            // trigger change of all control in layout
            _.each(__viewContext.primitiveValueConstraints, x => {
                if (_.has(x, "itemCode")) {
                    $('#' + x.itemCode).trigger('change');
                }
            })

            if (hasError()) {
                $('#func-notifier-errors').trigger('click');
                return;
            }

            // push data to webservice
            block();
            service.push.data(command).done(() => {
                info({ messageId: "Msg_15" }).then(function() {
                    unblock();
                    self.start();
                });
            }).fail((mes) => {
                unblock();
                alert(mes.message);
            });
        }
        
        register() {
            let self = this, $grid = $("#grid"),
                command, employees = [], recId = {};
            
            if (hasError()) {
                $("#func-notifier-errors").trigger("click");
                return;
            }
            
            if (_.chain($grid.mGrid("updatedCells")).filter(item => item.columnKey === "register").value().length === 0) return;
            confirm({ messageId: self.updateMode() === 1 ? "Msg_749" : "Msg_748" }).ifYes(() => {
                $grid.mGrid("validate", false, data => data.register);
                let errObj = {}, dataToG,
                    dataSource = $grid.mGrid("dataSource"),
                    regCount = 0,
                    itemErrors = _.filter($grid.mGrid("errors"), e => {
                        let d = dataSource[e.index];
                        return d && d.register;
                    });
                
                if (itemErrors && itemErrors.length > 0) {
                    dataToG = _.map(itemErrors, err => {
                        if (_.has(errObj, err.rowId)) {
                            errObj[err.rowId].push(err.columnKey);
                        } else {
                            errObj[err.rowId] = [ err.columnKey ];
                        }
                        
                        return { rowId: err.rowId, employeeId: err.employeeId, empCd: err.employeeCode, empName: err.employeeName, no: err.rowNumber, 
                                 isDisplayRegister: true, errorType: 0, itemName: err.columnName, message: err.message }; 
                    });
                }
                
                block();
                _.forEach($grid.mGrid("dataSource"), d => {
                    recId[d.id] = d;
                    if (d.register) regCount++;
                });
                let cateName, cateType, regId = {};
                if (self.category.cate()) {
                    cateName = self.category.cate().categoryName;
                    cateType = self.category.cate().categoryType;
                }
                
                let updates = $grid.mGrid("updatedCells"), updateDone = [];
                if (self.updateMode() === 2) {
                    let dataSource = $grid.mGrid("dataSource");
                    _.forEach($grid.mGrid("insertions"), i => {
                        _.forEach(self.settings.perInfoData(), (pInfo: IPersonInfoSetting) => {
                            let rec = dataSource[i];
                            if (pInfo.regulationAtr && rec) {
                                updates.push({ rowId: rec.id, columnKey: pInfo.itemCD, value: rec[pInfo.itemCD] }); 
                            }
                        });
                    });
                }
                
                let regChecked = [];
                _.forEach(updates, item => {
                    if (item.columnKey === "register") {
                        if (item.value && _.isNil(find(self.hiddenRows, (id) => id === item.rowId))) {
                            regChecked.push(item.rowId);
                            
                            // Add employee without items
                            if (errObj[item.rowId]) return;
                            let recData: Record = recId[item.rowId];
                            let regEmp = regId[recData.id];
                            if (!regEmp) {
                                regEmp = { rowId: item.rowId, personId: recData.personId, employeeId: recData.employeeId, employeeCd: recData.employeeCode, employeeName: recData.employeeName, order: recData.rowNumber };
                                regEmp.input = { categoryId: self.category.catId(), categoryCd: self.category.catCode(), categoryName: cateName, categoryType: cateType, recordId: recData instanceof Record ? recData.id : null, delete: false, items: [] };
                                regId[recData.id] = regEmp;
                                employees.push(regEmp);
                            }
                        }
                        
                        return;
                    }
                    
                    if (errObj[item.rowId]) return;
                    let recData: Record = recId[item.rowId];
                    let regEmp = regId[recData.id];
                    updateDone.push(item);
                    if (!regEmp) {
                        regEmp = { rowId: item.rowId, personId: recData.personId, employeeId: recData.employeeId, employeeCd: recData.employeeCode, employeeName: recData.employeeName, order: recData.rowNumber };
                        regEmp.input = { categoryId: self.category.catId(), categoryCd: self.category.catCode(), categoryName: cateName, categoryType: cateType, recordId: recData instanceof Record ? recData.id : null, delete: false, items: [] };
                        regId[recData.id] = regEmp;
                        employees.push(regEmp);
                    }
                    
                    let col = _.find(self.gridOptions.columns, column => column.key === item.columnKey);
                    if (col && col.perInfoTypeState.dataTypeValue !== ITEM_SINGLE_TYPE.READONLY && col.perInfoTypeState.dataTypeValue !== ITEM_SINGLE_TYPE.READONLY_BUTTON && col.perInfoTypeState.dataTypeValue !== ITEM_SINGLE_TYPE.RELATE_CATEGORY) {
                        let val = item.value;
                        let text, defValue, defText, initData = _.find(self.initDs, initRec => initRec.id === item.rowId);
                        if (initData) {
                            text = self.getText(col.perInfoTypeState, val, item.rowId, col.key, $grid);
                            defValue = initData[col.key];
                            defText = self.getText(col.perInfoTypeState, defValue, item.rowId, col.key, $grid);
                        }
                        
                        regEmp.input.items.push({ definitionId: col.itemId, itemCode: col.key, itemName: col.itemName, value: _.isObject(text) ? text.value : val, text: _.isObject(text) ? text.text : text, defValue: _.isObject(defText) ? defText.value : defValue, defText: _.isObject(defText) ? defText.text : defText, type: self.convertType(col.perInfoTypeState, val), logType: col.perInfoTypeState.dataTypeValue });
                    }
                });
                
//                if (employees.length === 0) {
//                    unblock();
//                    return;
//                }
                
                self.validateSpecial(regChecked, dataSource);
                itemErrors = _.filter($grid.mGrid("errors"), e => {
                    let d = dataSource[e.index];
                    return d && d.register;
                });
                
                errObj = {};
                if (itemErrors && itemErrors.length > 0) {
                    dataToG = _.map(itemErrors, err => {
                        if (_.has(errObj, err.rowId)) {
                            errObj[err.rowId].push(err.columnKey);
                        } else {
                            errObj[err.rowId] = [ err.columnKey ];
                        }
                        
                        return { rowId: err.rowId, employeeId: err.employeeId, empCd: err.employeeCode, empName: err.employeeName, no: err.rowNumber, 
                                 isDisplayRegister: true, errorType: 0, itemName: err.columnName, message: err.message }; 
                    });
                }
                
                employees = _.filter(employees, e => {
                    return _.find(regChecked, r => r === e.rowId) && !_.find(itemErrors, ie => ie.rowId === e.rowId)
                        && ((e.input || {}).items || []).length > 0;
                });
                
                dataToG = _.filter(dataToG, d => {
                    return _.find(regChecked, r => r === d.rowId);
                });
                
                if (dataToG && dataToG.length > 0) {
                    self.hasError(true);
                }
                
                command = { baseDate: moment.utc(self.baseDate(), "YYYY/MM/DD").toISOString(), editMode: self.updateMode(), employees: employees };
                if (command.employees && command.employees.length == 0) {
                    if (dataToG && dataToG.length > 0) {
                        setShared("CPS003G_ERROR_LIST", dataToG);
                        let msgId = _.keys(errObj).length === regCount ? "Msg_1462" : "Msg_1461";
                        alertError({ messageId: msgId }).then(() => {
                            forEach(updateDone, d => {
                                if (!_.has(errObj, d.rowId)) {
                                    $grid.mGrid("updateCell", d.rowId, d.columnKey, d.value, true);
                                    $grid.mGrid("updateCell", d.rowId, "register", false, true);
                                } 
                            });
                            
                            modeless("/view/cps/003/g/index.xhtml").onClosed(() => {
                            
                            });
                        });
                    }
                    
                    unblock();
                    return;
                }
                
                service.push.register(command).done((errorList) => {
                    if (dataToG && dataToG.length > 0) {
                        setShared("CPS003G_ERROR_LIST", dataToG);
                        let msgId = _.keys(errObj).length === regCount ? "Msg_1462" : "Msg_1461";
                        alertError({ messageId: msgId }).then(() => {
                            forEach(updateDone, d => {
                                if (!_.has(errObj, d.rowId)) {
                                    $grid.mGrid("updateCell", d.rowId, d.columnKey, d.value, true);
                                    $grid.mGrid("updateCell", d.rowId, "register", false, true);
                                } 
                            });
                            
                            modeless("/view/cps/003/g/index.xhtml").onClosed(() => {
                            
                            });
                        });
                    } else {
                        if (!errorList || errorList.length === 0) {
                            info({ messageId: "Msg_15" }).then(() => {
                                unblock();
                                self.requestData();
                            });
                        } else {
                            let errLst = _.map(errorList, e => e);
                            let ds = $grid.mGrid("dataSource");
                            let errIds = _.chain(errLst).map(e => e.no - 1).uniq().map(no => (ds[no] || {}).id).value();
                            forEach(updateDone, d => {
                                if (!_.find(errIds, id => id === d.rowId)) {
                                    $grid.mGrid("updateCell", d.rowId, d.columnKey, d.value, true);
                                    $grid.mGrid("updateCell", d.rowId, "register", false, true);
                                } 
                            });
                            
                            setShared("CPS003G_ERROR_LIST", errLst);
                            modeless("/view/cps/003/g/index.xhtml").onClosed(() => {
                                
                            });
                        }
                    }
                }).fail((res) => {
                    unblock();
                    alert(res.message);
                });
            }).ifNo(() => {
                
            });
        }
        
        validateSpecial(regChecked: any, dataSource: any) {
            let self = this, dateRanges, timeRanges, selectButtons, $grid = $("#grid");
            forEach(dataSource, (data, i) => {
                if (i == 0) {
                    dateRanges = findAll(cps003.control.dateRange, range => self.category.catCode() === range.ctgCode);
                    timeRanges = findAll(cps003.control.timeRange, range => self.category.catCode() === range.ctgCode);
                    selectButtons = findAll(cps003.control.selectGroups, select => self.category.catCode() === select.ctgCode);
                }
                
                if (_.isNil(find(regChecked, r => r === data.id))) return;
                
                forEach(dateRanges, range => {
                    let column = find(self.gridOptions.columns, c => c.key === range.start);
                    if (column) {
                        let control = find(self.gridOptions.ntsControls, c => c.name === column.ntsControl);
                        if (control) {
                            let vd = cps003.control.DATE_RANGE[range.ctgCode + "_" + range.start];
                            if (_.isFunction(vd)) vd(column.required, control.format, data[range.start], data);
                        }
                    }
                });
                
                forEach(timeRanges, range => {
                    let column = find(self.gridOptions.columns, c => c.key === range.start);
                    if (column) {
                        let vd = cps003.control.TIME_RANGE[range.ctgCode + "_" + range.start];
                        if (_.isFunction(vd)) { 
                            let timeRangeGroup = cps003.control.TIME_RANGE_GROUP[range.ctgCode + "_" + range.start];
                            vd(column.required, column.constraint.primitiveValue, column.headerText, data.id, range.start, data[range.start], data).fail(hasError => {
                                if (hasError) return;
                                if (timeRangeGroup) {
                                    timeRangeGroup(data.id, range.start, data[range.start], data);
                                }   
                            });
                        }
                    }
                });
                
                forEach(selectButtons, select => {
                    forEach([ "workplace", "workType", "workTime" ], sType => {
                        let wpColumn = find(self.gridOptions.columns, c => c.key === select[sType]);
                        if (wpColumn) {
                            let workplaceVal = data[select[sType]];
                            let optionsList = $grid.mGrid("optionsList", data.id, select[sType]);
                            if (wpColumn.required && _.isNil(find(optionsList, itm => itm.optionValue === workplaceVal))) {
                                let index = _.findIndex(dataSource, d => d.id === data.id),
                                    message = nts.uk.resource.getMessage("FND_E_REQ_SELECT", [wpColumn.headerText]);
                                $grid.mGrid("setErrors", [{ id: data.id, index: index, columnKey: select[sType], message: message }], null, true);
                            } else {
                                $grid.mGrid("clearErrors", [{ id: data.id, columnKey: select[sType] }]);
                            }
                        }
                    });
                });
                
                if (self.category.catCode() === "CS00002") {
                    forEach([ "IS00003", "IS00004", "IS00015", "IS00016" ], item => {
                        let column = find(self.gridOptions.columns, c => c.key === item);
                        if (column) {
                            let vd = cps003.control.STRING[self.category.catCode() + "_" + item];
                            if (_.isFunction(vd)) vd(column.required, data.id, item, data[item], data);
                        } 
                    });
                }
            });
        }
        
        convertType(perInfoTypeState: any, value: any) {
            if (!perInfoTypeState) return 1;
            switch (perInfoTypeState.dataTypeValue) {
                case ITEM_SINGLE_TYPE.STRING:
                    return 1;
                case ITEM_SINGLE_TYPE.NUMERIC:
                case ITEM_SINGLE_TYPE.TIME:
                case ITEM_SINGLE_TYPE.TIMEPOINT:
                    return 2;
                case ITEM_SINGLE_TYPE.DATE:
                    return 3;
                case ITEM_SINGLE_TYPE.SELECTION:
                case ITEM_SINGLE_TYPE.SEL_RADIO:
                case ITEM_SINGLE_TYPE.SEL_BUTTON:
                    switch (perInfoTypeState.referenceType) {
                        case ITEM_SELECT_TYPE.ENUM:
                            return 2;
                        case ITEM_SELECT_TYPE.CODE_NAME:
                            return 1;
                        case ITEM_SELECT_TYPE.DESIGNATED_MASTER:
                            if (!_.isNil(value) && !isNaN(Number(value)) && String(Number(value)) === String(value)) {
                                return 2;
                            }
                            return 1;
                    }
                case ITEM_SINGLE_TYPE.READONLY:
                case ITEM_SINGLE_TYPE.RELATE_CATEGORY:
                case ITEM_SINGLE_TYPE.READONLY_BUTTON:
                    return null;
                case ITEM_SINGLE_TYPE.NUMBERIC_BUTTON:
                    return 2;
            }
        }
        
        getText(perInfoTypeState: any, value: any, id: any, itemCode: any, $grid: any) {
            if (!perInfoTypeState || value === "") return value;
            switch (perInfoTypeState.dataTypeValue) {
                case ITEM_SINGLE_TYPE.STRING:
                case ITEM_SINGLE_TYPE.NUMERIC:
                    return value;
                case ITEM_SINGLE_TYPE.TIME:
                    if (!_.isNil(value)) return { value: nts.uk.time.parseTime(value).toValue(), text: value };
                case ITEM_SINGLE_TYPE.TIMEPOINT:
                    if (!_.isNil(value)) { 
                        let res = { value: nts.uk.time.parseTime(value).toValue() };
                        res.text = nts.uk.time.minutesBased.clock.dayattr.create(res.value).fullText;
                        return res;
                    }
                case ITEM_SINGLE_TYPE.DATE:
                    if (_.isNil(value) || (value instanceof moment && !value.isValid())) {
                        return { value: null, text: null };    
                    }
                    
                    let date = moment(value).format("YYYY/MM/DD");
                    return { value: date, text: date };
                case ITEM_SINGLE_TYPE.SELECTION:
                case ITEM_SINGLE_TYPE.SEL_RADIO:
                case ITEM_SINGLE_TYPE.SEL_BUTTON:
                    let optionItem = _.find($grid.mGrid("optionsList", id, itemCode), item => item.optionValue === value); 
                    if (optionItem) {
                        return optionItem.optionText;
                    }
                case ITEM_SINGLE_TYPE.READONLY:
                case ITEM_SINGLE_TYPE.RELATE_CATEGORY:
                case ITEM_SINGLE_TYPE.READONLY_BUTTON:
                case ITEM_SINGLE_TYPE.NUMBERIC_BUTTON:
                    return value;
                default:
                    return value;
            }
        }
        
        checkError() {
            let self = this, $grid = $("#grid");
            let dataSource = $grid.mGrid("dataSource"), regChecked = [];
            
            forEach($grid.mGrid("updatedCells"), item => {
                if (item.columnKey === "register" && item.value) {
                    regChecked.push(item.rowId);
                }
            });
            
            self.validateSpecial(regChecked, dataSource);
            $grid.mGrid("validate", false, data => data.register);
            let errors = _.filter($grid.mGrid("errors"), e => {
                let d = dataSource[e.index];
                return d && d.register;
            });
            
            if (errors.length === 0) {
                nts.uk.ui.dialog.info({ messageId: "Msg_1463" });
                return;
            }
            
            setShared("CPS003G_ERROR_LIST", _.map(errors, err => { 
                return { employeeId: err.employeeId, empCd: err.employeeCode, empName: err.employeeName, no: err.rowNumber, 
                         isDisplayRegister: false, errorType: 0, itemName: err.columnName, message: err.message }; }));
            self.hasError(true);
            modeless("/view/cps/003/g/index.xhtml").onClosed(() => {
                
            });
        }

        openBDialog() {
            let self = this,
                category = _.find(self.category.items(), x =>{ if(x.id == self.category.catId()){ return x;}}),
                params = {
                    systemDate: moment(self.baseDate()).format("YYYY/MM/DD"),
                    categoryId: self.category.catId(),
                    categoryName: self.category.cate().categoryName,
                    mode: self.updateMode(),
                    updateModeEnable: self.updateModeEnable(),
                    columnChange: _.filter(self.headDatas, (data: IDataHead) => _.find(self.settings.perInfoData(), (info: IPersonInfoSetting) => info.regulationAtr && info.itemCD === data.itemCode)),
                    sids: _.map(self.gridOptions.dataSource, data => data.employeeId)
                };

            setShared('CPS003B_VALUE', params);
            setShared("CPS003G_PARAM", { baseDate: self.baseDate(), updateMode: self.updateMode(), 
                catId: self.category.catId(), cate: self.category.cate(), perInfoCatePermission: self.perInfoCatePermission() });

            modal("/view/cps/003/b/index.xhtml").onClosed(() => {
                let sharedParam = getShared('CPS003C_VALUE');
                if (sharedParam) {
                    setShared('CPS003B_PARAM', sharedParam);
                    modal("/view/cps/003/c/index.xhtml").onClosed(() => {
                        if (getShared("CPS003C_REG_DONE")) {
                            self.requestData(null, null, getShared("CPS003C_REG_EMPID"));
                        }
                    });
                }
            });
        }
        
        exportFile() {
            block();
            let self = this, $grid = $("#grid"), 
                matrixData = { categoryId: self.category.catId(), categoryCode: self.category.catCode(), categoryName: self.category.cate().categoryName,
                    fixedHeader: { isShowDepartment: (self.settings.matrixDisplay() || {}).departmentATR === IUSE_SETTING.USE,
                                   isShowWorkplace: (self.settings.matrixDisplay() || {}).workPlaceATR === IUSE_SETTING.USE,
                                   isShowPosition: (self.settings.matrixDisplay() || {}).jobATR === IUSE_SETTING.USE,
                                   isShowEmployment: (self.settings.matrixDisplay() || {}).employmentATR === IUSE_SETTING.USE,
                                   isShowClassification: (self.settings.matrixDisplay() || {}).clsATR === IUSE_SETTING.USE },
                    dynamicHeader: _.filter(self.headDatas, (data: IDataHead) => _.find(self.settings.perInfoData(), (info: IPersonInfoSetting) => info.regulationAtr && info.itemCD === data.itemCode)),
                    width: nts.uk.localStorage.getItem(nts.uk.request.location.current.rawUrl + "/grid").map(w => {
                        w = JSON.parse(w);
                        let items = {};
                        _.forEach(self.settings.perInfoData(), (info: IPersonInfoSetting) => {
                            let itemWidth = w && w.default && w.default[info.itemCD];
                            if (!info.regulationAtr || _.isNil(itemWidth)) return;
                            items[info.itemCD] = itemWidth;
                        });
                        
                        _.forEach(["rowNumber", "employeeCode", "employeeName", "deptName", "workplaceName", "positionName", "employmentName", "className"], column => {
                            let itemWidth = w && w.reparer && w.reparer[column];
                            if (!_.isNil(itemWidth)) items[column] = itemWidth;
                        });
                        
                        return items;
                    }).orElse(null),
                    order: $grid.mGrid("columnOrder"),
                    detailData: []
                };
            
            _.forEach($grid.mGrid("dataSource"), (record: Record) => {
                if (/*find(self.hiddenRows, id => id === record.id)*/ find(self.hiddenEmpIds, id => id === record.employeeId)) return;
                matrixData.detailData.push(new GridEmployeeInfoDataSource(record, matrixData.dynamicHeader, $grid));
            });
            
            nts.uk.request.exportFile("com", "/person/matrix/report/printMatrix", matrixData).done(data => {
                
            }).fail(mes => {
                console.log(mes);
            }).always(() => {
                unblock();
            });
            
        }
        
        saveWidth() {
            let self = this, width = nts.uk.localStorage.getItem(nts.uk.request.location.current.rawUrl + "/grid"), items = [], invalid;
            width.ifPresent(w => {
                w = JSON.parse(w);
                _.forEach(self.settings.perInfoData(), (info: IPersonInfoSetting) => {
                    let itemWidth = w && w.default && w.default[info.itemCD];
                    if (!info.regulationAtr || _.isNil(itemWidth)) return;
                    if (itemWidth < 20 || itemWidth > 9999) {
                        let column = find(self.gridOptions.columns, c => c.key === info.itemCD);
                        if (column) {
                            let msg = nts.uk.resource.getMessage("Msg_1467", [ column.headerText ]);
                            $("#grid").ntsError("set", msg, "Msg_1467");
                            invalid = true;
                        }
                        
                        return;
                    }
                     
                    items.push({ 
                        pInfoCategoryID: self.category.catId(), 
                        pInfoItemDefiID: info.perInfoItemDefID, 
                        columnWidth: itemWidth, 
                        regulationATR: Number(info.regulationAtr)
                    });
                });
                
                if (items.length > 0 && !invalid) {
                    cps003.a.service.push.setting({
                        personInfoItems: items
                    });
                    
                    service.fetch.setting(self.category.catId()).done((data: ISettingData) => {
                        if (ko.isObservable(self.settings.matrixDisplay)) {
                            self.settings.matrixDisplay(data.matrixDisplay);
                        }    
                        
                        if (ko.isObservable(self.settings.perInfoData)) {
                            self.settings.perInfoData(data.perInfoData);
                        }
                    });
                    
                    $("#grid").ntsError("clear");
                }
            });
        }

        loadGrid() {
            let self = this;
            if ($("#grid").data("mGrid")) $("#grid").mGrid("destroy");
            self.hasError(false);
            new nts.uk.ui.mgrid.MGrid($("#grid")[0], {
                width: "1000px",
                height: "800px",
                headerHeight: "40px",
                subHeight: "330px",
                subWidth: "160px",
                dataSource: self.gridOptions.dataSource,
                primaryKey: "id",
                useOptions: true,
                virtualization: true,
                virtualizationMode: "continuous",
                enter: "right",
                autoFitWindow: true,
                errorColumns: [ "employeeId", "employeeCode", "employeeName", "rowNumber" ],
                errorOccurred: () => self.hasError(true),
                errorResolved: () => self.hasError(false),
                errorDismissed: () => {
                    let $grid = $("#grid");
                    let dataSource = $grid.mGrid("dataSource"),
                        itemErrors = _.filter($grid.mGrid("errors"), e => {
                        let d = dataSource[e.index];
                        return d && d.register;
                    });
                    
                    if (itemErrors.length > 0) {
                        self.hasError(true);
                    } else {
                        self.hasError(false);
                    }
                },
                idGen: (id) => id + "_" + nts.uk.util.randomId(),
                columns: self.gridOptions.columns,
                features: self.gridOptions.features,
                ntsControls: self.gridOptions.ntsControls
            }).create();
        }

        requestData(settingData: ISettingData, employeeSelect: any, regEmpIds: any) {
            // { categoryId: 'COM1_00000000000000000000000_CS00020', lstEmployee: [], standardDate: '2818/01/01' };
            let self = this, dfd = $.Deferred();
            if ($("#base-date").ntsError("hasError")) return;
            block();
            
            let employeeIds = [];
            if (!regEmpIds || regEmpIds.length == 0) {
                employeeIds = _.map(self.employees(), e => e.employeeId);
            } else {
                let tmpSet = new Set();
                forEach(self.employees(), e => {
                    tmpSet.add(e.employeeId);
                });
                
                forEach(regEmpIds, e => {
                    tmpSet.add(e);
                });
                
                self.employees.removeAll();
                tmpSet.forEach(e => {
                    employeeIds.push(e);
                    self.employees.push({ employeeId: e });
                });
            }
            
            let param = { categoryId: self.category.catId(), lstEmployee: employeeIds, standardDate: moment.utc(self.baseDate(), "YYYY/MM/DD").toISOString() };
            
            if (self.category.catCode()) {
                param.categoryCode = self.category.catCode();
            }
            
            nts.uk.request.ajax('com', 'ctx/pereg/grid-layout/get-data', param).done(data => {
                if (data.bodyDatas && data.bodyDatas.length == 0 && !_.isNil(data.baseDate)) {
//                    self.baseDate(data.baseDate);
                }
                
                if (employeeSelect) {
                    self.hiddenRows = [];
                    self.hiddenEmpIds = [];
                }
                
                self.convertData(data, settingData).done(() => {
                    self.loadGrid();
                    self.initDs = _.cloneDeep(self.gridOptions.dataSource);
                    if (self.hiddenEmpIds.length > 0) {
                        let $grid = $("#grid");
                        forEach(self.initDs, (obj, i) => {
                            if (find(self.hiddenEmpIds, empId => obj.employeeId === empId)) {
                                $grid.mGrid("hideRow", i);
                            }
                        });
                    }
                    
                    self.settings.matrixDisplay.valueHasMutated();
                    self.disableCS00035();
                    unblock();
                    if ($(window).data("blockUI.isBlocked") === 1) unblock();
                    dfd.resolve();
                });
            }).fail(res => {
                alertError({ messageId: res.messageId }).then(() => {
                    if (self.baseDateChangeHist.length > 1) {
                        self.baseDate(self.baseDateChangeHist[self.baseDateChangeHist.length - 2]);
                    }
                });
            });
            
            return dfd.promise();
        }
        
        disableCS00035() {
            let self = this;
            if (self.category.catCode() !== "CS00035") return;
            let $grid = $("#grid");
            _.forEach(self.gridOptions.dataSource, s => {
                cps003.control.fetch.check_remain_days(s.employeeId).done(x => {
                    $grid.mGrid(x ? "enableNtsControlAt" : "disableNtsControlAt", s.id, "IS00366", null, true);
                });
                
                cps003.control.fetch.check_remain_left(s.employeeId).done(x => {
                    $grid.mGrid(x ? "enableNtsControlAt": "disableNtsControlAt", s.id, "IS00368", null, true);
                });
            });
        }

        convertData(data: IRequestData, gridSettings: ISettingData) {
            let self = this, dfd = $.Deferred(), hideRowAdd;
            if (data.headDatas) {
                self.batchSettingItems = [];
                data.headDatas.sort((a, b) => {
                    if (a.itemOrder === b.itemOrder) {
                        let cmp = a.itemParentCode.compareTo(b.itemParentCode);
                        if (!cmp) {
                            return a.itemCode.compareTo(b.itemCode);
                        }
                        return cmp;
                    }
                    
                    return a.itemOrder - b.itemOrder;
                });
                
                let item, control, parent = {}, sort;
                self.dataTypes = {};
                gridSettings = gridSettings || ko.toJS(self.settings);
                
                if (self.category.cate().categoryType === IT_CAT_TYPE.SINGLE
                    || self.category.cate().categoryType === IT_CAT_TYPE.CONTINUWED
                    || self.category.cate().categoryType === IT_CAT_TYPE.CONTINU
                    || self.category.cate().categoryType === IT_CAT_TYPE.NODUPLICATE) {
                    hideRowAdd = true;
                } else if (self.category.cate().categoryType === IT_CAT_TYPE.DUPLICATE) {
                    if (self.updateMode() === 1) {
                        hideRowAdd = true;
                    } else hideRowAdd = false;
                } else if (self.category.cate().categoryType === IT_CAT_TYPE.MULTI) {
                    if (self.perInfoCatePermission().otherAllowAddMulti === 0 && self.perInfoCatePermission().selfAllowAddMulti === 0) {
                        hideRowAdd = true;
                    } else hideRowAdd = false;
                }
                
                self.gridOptions.columns = [
                    { headerText: "", key: "rowNumber", dataType: "number", width: "30px" },
                    { headerText: nts.uk.resource.getText("CPS003_50"), key: "register", dataType: "boolean", width: "30px", ntsControl: "RegCheckBox", bound: true },
//                    { headerText: "印刷対象", key: "print", dataType: "boolean", width: "30px", ntsControl: "PrintCheckBox", bound: true },
                    { headerText: nts.uk.resource.getText("CPS003_114"), key: "showControl", dataType: "string", width: "40px", ntsControl: "ImageShow" },
                    { headerText: nts.uk.resource.getText("CPS003_28"), key: "employeeCode", dataType: "string", width: "100px", ntsControl: "Label" },
                    { headerText: nts.uk.resource.getText("CPS003_29"), key: "employeeName", dataType: "string", width: "140px", ntsControl: "Label" },
                    { headerText: nts.uk.resource.getText("CPS003_130"), key: "rowAdd", dataType: "string", width: "40px", ntsControl: "RowAdd", hidden: hideRowAdd },
                    { headerText: nts.uk.resource.getText("CPS003_30"), key: "deptName", dataType: "string", width: "100px", ntsControl: "Label", hidden: !gridSettings || !gridSettings.matrixDisplay || gridSettings.matrixDisplay.departmentATR === IUSE_SETTING.NOT_USE },
                    { headerText: nts.uk.resource.getText("CPS003_31"), key: "workplaceName", dataType: "string", width: "100px", ntsControl: "Label", hidden: !gridSettings || !gridSettings.matrixDisplay || gridSettings.matrixDisplay.workPlaceATR === IUSE_SETTING.NOT_USE },
                    { headerText: nts.uk.resource.getText("CPS003_32"), key: "positionName", dataType: "string", width: "100px", ntsControl: "Label", hidden: !gridSettings || !gridSettings.matrixDisplay || gridSettings.matrixDisplay.jobATR === IUSE_SETTING.NOT_USE },
                    { headerText: nts.uk.resource.getText("CPS003_33"), key: "employmentName", dataType: "string", width: "100px", ntsControl: "Label", hidden: !gridSettings || !gridSettings.matrixDisplay || gridSettings.matrixDisplay.employmentATR === IUSE_SETTING.NOT_USE },
                    { headerText: nts.uk.resource.getText("CPS003_34"), key: "className", dataType: "string", width: "100px", ntsControl: "Label", hidden: !gridSettings || !gridSettings.matrixDisplay || gridSettings.matrixDisplay.clsATR === IUSE_SETTING.NOT_USE } 
                ];
                
                self.gridOptions.ntsControls = [
                    { name: 'RegCheckBox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true,
                        onChange: (i, k, v, d) => {
                            if (!v || !self.hasError()) {
                                let $grid = $("#grid");
                                let dataSource = $grid.mGrid("dataSource"),
                                    itemErrors = _.filter($grid.mGrid("errors"), e => {
                                    let d = dataSource[e.index];
                                    return d && d.register;
                                });
                                
                                if (itemErrors.length > 0) {
                                    self.hasError(true);
                                } else {
                                    self.hasError(false);
                                }
                            }
                        }    
                    },
//                    { name: 'PrintCheckBox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                    { name: "ImageShow", source: "hidden-button", controlType: "Image", click: (idx) => {
                        if (_.isNil(idx)) return;
                        let ds = $("#grid").mGrid("dataSource");
                        if (_.isNil(ds[idx])) return;
                        let obj = ds[idx];
                        self.hiddenRows.push(obj.id);
                        self.hiddenEmpIds.push(obj.employeeId);
                    }},
                    { name: "RowAdd", source: "plus-button", cssClass: "blue-color", controlType: "Image", copy: 2 }];
                let headerStyles = { name: "HeaderStyles", columns: [] },
                    columnSettings = _.cloneDeep(self.settings.perInfoData()),
                    sorting = { name: "Sorting", columnSettings: [
                        { columnKey: "rowNumber", allowSorting: true, type: "Number" },
                        { columnKey: "employeeCode", allowSorting: true },
                        { columnKey: "employeeName", allowSorting: true },
                        { columnKey: "deptName", allowSorting: true },
                        { columnKey: "workplaceName", allowSorting: true },
                        { columnKey: "positionName", allowSorting: true },
                        { columnKey: "employmentName", allowSorting: true },
                        { columnKey: "className", allowSorting: true }
                    ]};
                
                self.headDatas = data.headDatas;
                forEach(data.headDatas, (d: IDataHead) => {
                    let controlType = d.itemTypeState.dataTypeState;
                    if (controlType) {
                        let name;
                        
                        if (_.isNil(d.itemParentCode) || d.itemParentCode === "") {
                            parent[d.itemCode] = d.itemName /*+ d.itemCode*/;
                            name = d.itemName /*+ d.itemCode*/;
                        } else {
                            name = parent[d.itemParentCode] + "-" + d.itemName /*+ d.itemCode*/;
                            parent[d.itemCode] = name;
                        }
                        
                        let colSetting = _.remove(columnSettings, s => s.itemCD === d.itemCode);
                        item = { headerText: name, itemId: d.itemId, itemName: d.itemName, key: d.itemCode, required: d.required, parentCode: d.itemParentCode, dataType: "string", width: colSetting.length > 0 ? colSetting[0].width + "px" : "200px", perInfoTypeState: controlType };
                        if (gridSettings) {
                             colSetting = [ _.find(gridSettings.perInfoData, itemInfo => itemInfo.itemCD === d.itemCode) ];
                        }
                        if (colSetting.length > 0 && colSetting[0]) {
                            item.hidden = !colSetting[0].regulationAtr;        
                        }
                        
                        controlType.required = d.required;
                        sort = {};
                        control = self.getControlType(controlType, item, sort);
                        self.gridOptions.columns.push(item);
                        if (sort.columnKey) {
                            sorting.columnSettings.push(sort);
                        }
                        
                        if (control) {
                            self.gridOptions.ntsControls.push(control);
                            let combo = cps003.control.COMBOBOX[self.category.catCode() + "_" + d.itemCode]; 
                            
                            if (combo) {
                                control.inputProcess = combo;
                            }
                            
                            if (control.controlType === "DatePicker") {
                                let dp = cps003.control.DATE_RANGE[self.category.catCode() + "_" + d.itemCode];
                                if (dp) {
                                    if (control.inputProcess) {
                                        let existedProcess = control.inputProcess;
                                        let format = control.format;
                                        control.inputProcess = function() {
                                            existedProcess.apply(null, arguments);
                                            dp.apply(null, _.concat(d.required, format, arguments));
                                        };
                                    } else {
                                        control.inputProcess = dp.bind(null, d.required, control.format);
                                    }
                                }
                            }
                        }
                        
                        if (d.required) {
                            headerStyles.columns.push({ key: d.itemCode, colors: ["required"] });
                        }
                        
                        cps003.control.writePrimitiveConstraint(d);
                        if (item.constraint.primitiveValue === "StampNumber") {
                            cps003.control.fetch.get_stc_setting().done((stc: StampCardEditing) => {
                                let pv = (__viewContext.primitiveValueConstraints || {}).StampNumber; 
                                if (!pv) return;
                                if (!_.isNil(stc.digitsNumber)) {
                                    __viewContext.primitiveValueConstraints.StampNumber.maxLength = stc.digitsNumber;
                                }
                                
                                switch (stc.method) {
                                    case EDIT_METHOD.PreviousZero:
                                        pv.formatOption = { autoFill: true, fillDirection: "left", fillCharacter: "0" };
                                        break;
                                    case EDIT_METHOD.AfterZero:
                                        pv.formatOption = { autoFill: true, fillDirection: "right", fillCharacter: "0" };
                                        break;
                                    case EDIT_METHOD.PreviousSpace:
                                        pv.formatOption = { autoFill: true, fillDirection: "left", fillCharacter: " " };
                                        break;
                                    case EDIT_METHOD.AfterSpace:
                                        pv.formatOption = { autoFill: true, fillDirection: "right", fillCharacter: " " };
                                        break;
                                }
                            });
                        }
                    } else {
                        parent[d.itemCode] = d.itemName /*+ d.itemCode*/;
                    }
                });
                
                self.gridOptions.features = [{ name: "Resizing" }, { name: "ColumnMoving" }, { name: "Copy" }, { name: "Tooltip", error: true }, { name: "WidthSaving", reset: true }];
                // TODO: Get fixed columns
                let columnFixing = { name: "ColumnFixing", columnSettings: [] };
                forEach(self.fixedColumns, f => {
                    columnFixing.columnSettings.push({ columnKey: f, isFixed: true });
                });
                
                self.gridOptions.features.push(columnFixing);
                self.gridOptions.features.push(headerStyles);
                self.gridOptions.features.push(sorting);
            }
            
            if (data.bodyDatas) {
                self.gridOptions.dataSource = [];
                let states = [], workTimeCodes = [], nullWorkTimeCodes = [], workTimeItems = [], nullWorkTimeItems = [], codes = {}, displayItems = [];
                forEach(self.gridOptions.columns, (column, i) => {
                    if (i < 11) return;
                    displayItems.push(column.key);
                });
                
                forEach(data.bodyDatas, (d: IDataBody, ri: any) => {
                    let record = new Record(d, ri), disItems = _.cloneDeep(displayItems);
                    forEach(d.items, (item: IColumnData, i: number) => {
                        let dt = self.dataTypes[item.itemCode], disabled;
                        if (!dt) return;
                        if (_.isNil(item.recordId)) {
                            states.push(new State(record.id, "employeeCode", ["red-color"]));
                            states.push(new State(record.id, "employeeName", ["red-color"]));
                            states.push(new State(record.id, "deptName", ["red-color"]));
                            states.push(new State(record.id, "workplaceName", ["red-color"]));
                            states.push(new State(record.id, "positionName", ["red-color"]));
                            states.push(new State(record.id, "employmentName", ["red-color"]));
                            states.push(new State(record.id, "className", ["red-color"]));
                        }
                        
                        if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.DATE) {
                            record[item.itemCode] = _.isNil(item.value) || item.value === "" ? item.value : moment.utc(item.value, "YYYY/MM/DD").toDate();
                            if (self.category.catCode() === "CS00070" && (item.itemCode === "IS00781" || item.itemCode === "IS00782")) {
                                states.push(new State(record.id, item.itemCode, ["mgrid-disable"]));
                                disabled = true;
                            }
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIMEPOINT && !_.isNil(item.value)) {
                            record[item.itemCode] = nts.uk.time.minutesBased.clock.dayattr.create(item.value).shortText;
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIME && !_.isNil(item.value)) {
                            record[item.itemCode] = nts.uk.time.parseTime(item.value, true).format();
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.READONLY) {
                            if (self.category.catCode() === "CS00024" && item.itemCode === "IS00289") {
                                record[item.itemCode] = !_.isNil(item.value) && item.value !== "" ? nts.uk.time.parseTime(item.value, true).format() : "";
                            } else {
                                record[item.itemCode] = item.value;
                            }
                        } else {
                            record[item.itemCode] = item.value;
                        }
                        
                        if (item.actionRole === ACTION_ROLE.VIEW_ONLY && dt.cls.dataTypeValue !== ITEM_SINGLE_TYPE.READONLY && !disabled) {
                            states.push(new State(record.id, item.itemCode, ["mgrid-disable"]));
                        }
                        _.remove(disItems, itm => itm === item.itemCode);
                        if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.SELECTION || dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.SEL_RADIO) {
                            if (dt.cls.referenceType === ITEM_SELECT_TYPE.ENUM || dt.cls.referenceType === ITEM_SELECT_TYPE.CODE_NAME 
                                || (dt.cls.referenceType === ITEM_SELECT_TYPE.DESIGNATED_MASTER && item.itemCode !== "IS00079")) {
                                dt.specs.options = item.lstComboBoxValue;
                            } else if (!dt.specs.options) {
                                dt.specs.options = item.lstComboBoxValue;  
                            } else {
                                dt.specs.pattern.push(item.lstComboBoxValue);
                                dt.specs.list[item.recordId] = dt.specs.pattern.length - 1;
                            }
                            
                            self.combobox(record.id, item, states, record);
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.SEL_BUTTON) {
                            dt.specs.pattern.push(item.lstComboBoxValue);
                            dt.specs.list[item.recordId] = dt.specs.pattern.length - 1;
                            if (cps003.control.WORK_TIME[item.itemCode]) {
                                if (!_.isNil(item.value)) {
//                                    if (_.isNil(find(workTimeCodes, wt => wt === item.value))) {
                                        workTimeCodes.push(item.value);
                                        workTimeItems.push(item.itemCode);
//                                    }
                                } else /*if (_.isNil(find(nullWorkTimeItems, nw => nw === item.itemCode)))*/ {
                                    nullWorkTimeCodes.push(item.value);
                                    nullWorkTimeItems.push(item.itemCode);
                                }
                                
                                if (_.has(codes, item.value)) {
                                    let codeArr = codes[item.value], found;
                                    for (let k = 0; k < codeArr.length; k++) {
                                        if (codeArr[k] && codeArr[k].id === record.id && codeArr[k].column === item.itemCode) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    
                                    if (!found) {
                                        codes[item.value].push({ id: record.id, column: item.itemCode });
                                    }
                                } else {
                                    codes[item.value] = [{ id: record.id, column: item.itemCode }];
                                }
                            }
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.RELATE_CATEGORY) {
                            if (item.itemCode === "IS00301" && (_.isNil(item.value) || item.value === "")) {
                                record[item.itemCode] = "0.0日";
                            }
                        }
                    });
                    
                    forEach(disItems, itm => states.push(new State(record.id, itm, ["mgrid-lock"])));
                    self.gridOptions.dataSource.push(record);
//                    if (hideRowAdd === false) {
                        if (self.category.cate().categoryType === IT_CAT_TYPE.DUPLICATE) {
                            if (__viewContext.user.employeeId === record.employeeId) {
                                if (self.perInfoCatePermission().selfAllowAddHis === 0 
                                    || self.perInfoCatePermission().selfFutureHisAuth === 1 || self.perInfoCatePermission().selfFutureHisAuth === 2) {
                                    states.push(new State(record.id, "rowAdd", ["mgrid-disable"]));
                                }
                            } else {
                                if (self.perInfoCatePermission().otherAllowAddHis === 0
                                    || self.perInfoCatePermission().otherFutureHisAuth === 1 || self.perInfoCatePermission().otherFutureHisAuth === 2) {
                                    states.push(new State(record.id, "rowAdd", ["mgrid-disable"]));
                                }     
                            }
                        } else if (self.category.cate().categoryType === IT_CAT_TYPE.MULTI) {
                            if (__viewContext.user.employeeId === record.employeeId && self.perInfoCatePermission().selfAllowAddMulti === 0) {
                                states.push(new State(record.id, "rowAdd", ["mgrid-disable"]));
                            } else if (__viewContext.user.employeeId !== record.employeeId && self.perInfoCatePermission().otherAllowAddMulti === 0) {
                                states.push(new State(record.id, "rowAdd", ["mgrid-disable"]));  
                            }
                        }
//                    }
                });
                
                if (workTimeCodes.length > 0) {
                    cps003.control.fetch.check_mt_se({ workTimeCodes: workTimeCodes }).done(mt => {
                        forEach(workTimeCodes, (c, i) => {
                            let head = _.find(mt, f => f.workTimeCode === c),
                                itemCode = workTimeItems[i],
                                workTime = cps003.control.WORK_TIME[itemCode];
                            if (head) {
                                if (workTime.firstTimes && !head.startEnd) {
                                    forEach(codes[c], r => {
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.firstTimes.start, ["mgrid-disable"]));
                                        }
                                        
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.firstTimes.end, ["mgrid-disable"]));
                                        }
                                    });
                                }
                                
                                if (workTime.secondTimes && (!head.startEnd || !head.multiTime)) {
                                    forEach(codes[c], r => {
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.secondTimes.start, ["mgrid-disable"]));
                                        }
                                        
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.secondTimes.end, ["mgrid-disable"]));
                                        }
                                    });
                                }
                            } else {
                                if (workTime.firstTimes) {
                                    forEach(codes[c], r => {
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.firstTimes.start, ["mgrid-disable"]));
                                        }
                                        
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.firstTimes.end, ["mgrid-disable"]));
                                        }
                                    });
                                }
                                
                                if (workTime.secondTimes) {
                                    forEach(codes[c], r => {
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.secondTimes.start, ["mgrid-disable"]));
                                        }
                                        
                                        if (r.column === itemCode) {
                                            states.push(new State(r.id, workTime.secondTimes.end, ["mgrid-disable"]));
                                        }
                                    });
                                }
                            }
                        });
                        
                        dfd.resolve();
                    });
                } else dfd.resolve();
                
                forEach(nullWorkTimeCodes, (c, i) => {
                    let itemCode = nullWorkTimeItems[i],
                        workTime = cps003.control.WORK_TIME[itemCode];    
                    if (workTime.firstTimes) {
                        forEach(codes[c], r => {
                            if (r.column === itemCode) {
                                states.push(new State(r.id, workTime.firstTimes.start, ["mgrid-disable"]));
                            }
                            
                            if (r.column === itemCode) {
                                states.push(new State(r.id, workTime.firstTimes.end, ["mgrid-disable"]));
                            }
                        });
                    }
                    
                    if (workTime.secondTimes) {
                        forEach(codes[c], r => {
                            if (r.column === itemCode) {
                                states.push(new State(r.id, workTime.secondTimes.start, ["mgrid-disable"]));
                            }
                            
                            if (r.column === itemCode) {
                                states.push(new State(r.id, workTime.secondTimes.end, ["mgrid-disable"]));
                            }
                        });
                    }
                });
                
                self.gridOptions.features.push({ name: "CellStyles", states: states });
            }
            
            return dfd.promise();
        }
        
        redisplayEmp() {
            let self = this;
            $("#grid").mGrid("showHiddenRows");
            self.hiddenRows = [];
            self.hiddenEmpIds = [];
        }
        
        combobox(id: any, item: IColumnData, states, record: any) {
            switch (this.category.catCode()) {
                case "CS00020":
                    switch (item.itemCode) {
                        case "IS00248":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00249", ["mgrid-disable"]));
                                states.push(new State(id, "IS00250", ["mgrid-disable"]));
                                states.push(new State(id, "IS00251", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00121":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00123", ["mgrid-disable"]));
                                states.push(new State(id, "IS00124", ["mgrid-disable"]));
                                states.push(new State(id, "IS00125", ["mgrid-disable"]));
                                states.push(new State(id, "IS00126", ["mgrid-disable"]));
                                states.push(new State(id, "IS00127", ["mgrid-disable"])); 
                            }
                            break;
                        case "IS00123":
                            if (record.IS00121 === "0") break;
                            if (item.value === "0") {
                                _.remove(states, s => s.rowId === id && (s.columnKey === "IS00124" || s.columnKey === "IS00125" || s.columnKey === "IS00126"));
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00127")) {
                                    states.push(new State(id, "IS00127", ["mgrid-disable"]));
                                }
                            } else if (item.value === "1") {
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00124")) {
                                    states.push(new State(id, "IS00124", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00125")) {
                                    states.push(new State(id, "IS00125", ["mgrid-disable"]));
                                }
                                _.remove(states, s => s.rowId === id && (s.columnKey === "IS00126" || s.columnKey === "IS00127"));
                            } else if (item.value === "2") {
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00124")) {
                                    states.push(new State(id, "IS00124", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00125")) {
                                    states.push(new State(id, "IS00125", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00126")) {
                                    states.push(new State(id, "IS00126", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.rowId === id && s.columnKey === "IS00127")) {
                                    states.push(new State(id, "IS00127", ["mgrid-disable"]));
                                }   
                            }
                            break;
                    }
                    break;
                case "CS00025":
                    switch (item.itemCode) {
                        case "IS00296":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00297", ["mgrid-disable"]));
                                states.push(new State(id, "IS00298", ["mgrid-disable"]));
                                states.push(new State(id, "IS00299", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00300", ["mgrid-disable"]));
                                states.push(new State(id, "IS00301", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00297":
                            if (item.value === "0" && record.IS00296 === "1") {
                                states.push(new State(id, "IS00298", ["mgrid-disable"]));
                                states.push(new State(id, "IS00299", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00300", ["mgrid-disable"]));
                                states.push(new State(id, "IS00301", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00026":
                    switch (item.itemCode) {
                        case "IS00303":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00304", ["mgrid-disable"]));
                                states.push(new State(id, "IS00305", ["mgrid-disable"]));
                                states.push(new State(id, "IS00306", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00307", ["mgrid-disable"]));
                                states.push(new State(id, "IS00308", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00304":
                            if (item.value === "0" && record.IS00303 === "1") {
                                states.push(new State(id, "IS00305", ["mgrid-disable"]));
                                states.push(new State(id, "IS00306", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00307", ["mgrid-disable"]));
                                states.push(new State(id, "IS00308", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00027":
                    switch (item.itemCode) {
                        case "IS00310":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00311", ["mgrid-disable"]));
                                states.push(new State(id, "IS00312", ["mgrid-disable"]));
                                states.push(new State(id, "IS00313", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00314", ["mgrid-disable"]));
                                states.push(new State(id, "IS00315", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00311":
                            if (item.value === "0" && record.IS00310 === "1") {
                                states.push(new State(id, "IS00312", ["mgrid-disable"]));
                                states.push(new State(id, "IS00313", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00314", ["mgrid-disable"]));
                                states.push(new State(id, "IS00315", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00028":
                    switch (item.itemCode) {
                        case "IS00317":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00318", ["mgrid-disable"]));
                                states.push(new State(id, "IS00319", ["mgrid-disable"]));
                                states.push(new State(id, "IS00320", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00321", ["mgrid-disable"]));
                                states.push(new State(id, "IS00322", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00318":
                            if (item.value === "0" && record.IS00317 === "1") {
                                states.push(new State(id, "IS00319", ["mgrid-disable"]));
                                states.push(new State(id, "IS00320", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00321", ["mgrid-disable"]));
                                states.push(new State(id, "IS00322", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00029":
                    switch (item.itemCode) {
                        case "IS00324":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00325", ["mgrid-disable"]));
                                states.push(new State(id, "IS00326", ["mgrid-disable"]));
                                states.push(new State(id, "IS00327", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00328", ["mgrid-disable"]));
                                states.push(new State(id, "IS00329", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00325":
                            if (item.value === "0" && record.IS00324 === "1") {
                                states.push(new State(id, "IS00326", ["mgrid-disable"]));
                                states.push(new State(id, "IS00327", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00328", ["mgrid-disable"]));
                                states.push(new State(id, "IS00329", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00030":
                    switch (item.itemCode) {
                        case "IS00331":
                            if (item.value === "0") {
                                states.push(new State(id, "IS00332", ["mgrid-disable"]));
                                states.push(new State(id, "IS00333", ["mgrid-disable"]));
                                states.push(new State(id, "IS00334", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00335", ["mgrid-disable"]));
                                states.push(new State(id, "IS00336", ["mgrid-disable"]));
                            }
                            break;
                        case "IS00332":
                            if (item.value === "0" && record.IS00331 === "1") {
                                states.push(new State(id, "IS00333", ["mgrid-disable"]));
                                states.push(new State(id, "IS00334", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00335", ["mgrid-disable"]));
                                states.push(new State(id, "IS00336", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00031":
                    switch (item.itemCode) {
                        case "IS00338":
                            if (item.value === "0") {
                                _.forEach(['IS00339', 'IS00340', 'IS00341'/*, 'IS00342'*/, 'IS00343'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00339":
                            if (item.value === "0" && record.IS00338 === "1") {
                                states.push(new State(id, "IS00340", ["mgrid-disable"]));
                                states.push(new State(id, "IS00341", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00342", ["mgrid-disable"]));
                                states.push(new State(id, "IS00343", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00032":
                    switch (item.itemCode) {
                        case "IS00345":
                            if (item.value === "0") {
                                _.forEach(['IS00346', 'IS00347', 'IS00348'/*, 'IS00349'*/, 'IS00350'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00346":
                            if (item.value === "0" && record.IS00345 === "1") {
                                states.push(new State(id, "IS00347", ["mgrid-disable"]));
                                states.push(new State(id, "IS00348", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00349", ["mgrid-disable"]));
                                states.push(new State(id, "IS00350", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00033":
                    switch (item.itemCode) {
                        case "IS00352":
                            if (item.value === "0") {
                                _.forEach(['IS00353', 'IS00354', 'IS00355'/*, 'IS00356'*/, 'IS00357'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00353":
                            if (item.value === "0" && record.IS00352 === "1") {
                                states.push(new State(id, "IS00354", ["mgrid-disable"]));
                                states.push(new State(id, "IS00355", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00356", ["mgrid-disable"]));
                                states.push(new State(id, "IS00357", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00034":
                    switch (item.itemCode) {
                        case "IS00359":
                            if (item.value === "0") {
                                _.forEach(['IS00360', 'IS00361', 'IS00362'/*, 'IS00363'*/, 'IS00364'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00360":
                            if (item.value === "0" && record.IS00359 === "1") {
                                states.push(new State(id, "IS00361", ["mgrid-disable"]));
                                states.push(new State(id, "IS00362", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00363", ["mgrid-disable"]));
                                states.push(new State(id, "IS00364", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00035":
                    switch (item.itemCode) {
                        case "IS00370":
                            if (item.value === "0") {
                                _.forEach(['IS00371', 'IS00372', 'IS00374'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00036":
                    switch (item.itemCode) {
                        case "IS00375":
                            if (item.value === "0") {
                                _.forEach(['IS00376', 'IS00377', 'IS00378', 'IS00379', 'IS01101'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00380":
                            if (item.value === "0") {
                                _.forEach(['IS00381', 'IS00382', 'IS00383', 'IS00384', 'IS01102'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00049":
                    switch (item.itemCode) {
                        case "IS00560":
                            if (item.value === "0") {
                                _.forEach(['IS00561', 'IS00562', 'IS00563'/*, 'IS00564'*/, 'IS00565'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00561":
                            if (item.value === "0" && record.IS00560 === "1") {
                                states.push(new State(id, "IS00562", ["mgrid-disable"]));
                                states.push(new State(id, "IS00563", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00564", ["mgrid-disable"]));
                                states.push(new State(id, "IS00565", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00050":
                    switch (item.itemCode) {
                        case "IS00567":
                            if (item.value === "0") {
                                _.forEach(['IS00568', 'IS00569', 'IS00570'/*, 'IS00571'*/, 'IS00572'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00568":
                            if (item.value === "0" && record.IS00567 === "1") {
                                states.push(new State(id, "IS00569", ["mgrid-disable"]));
                                states.push(new State(id, "IS00570", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00571", ["mgrid-disable"]));
                                states.push(new State(id, "IS00572", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00051":
                    switch (item.itemCode) {
                        case "IS00574":
                            if (item.value === "0") {
                                _.forEach(['IS00575', 'IS00576', 'IS00577'/*, 'IS00578'*/, 'IS00579'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00575":
                            if (item.value === "0" && record.IS00574 === "1") {
                                states.push(new State(id, "IS00576", ["mgrid-disable"]));
                                states.push(new State(id, "IS00577", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00578", ["mgrid-disable"]));
                                states.push(new State(id, "IS00579", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00052":
                    switch (item.itemCode) {
                        case "IS00581":
                            if (item.value === "0") {
                                _.forEach(['IS00582', 'IS00583', 'IS00584'/*, 'IS00585'*/, 'IS00586'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00582":
                            if (item.value === "0" && record.IS00581 === "1") {
                                states.push(new State(id, "IS00583", ["mgrid-disable"]));
                                states.push(new State(id, "IS00584", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00585", ["mgrid-disable"]));
                                states.push(new State(id, "IS00586", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00053":
                    switch (item.itemCode) {
                        case "IS00588":
                            if (item.value === "0") {
                                _.forEach(['IS00589', 'IS00590', 'IS00591'/*, 'IS00592'*/, 'IS00593'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00589":
                            if (item.value === "0" && record.IS00588 === "1") {
                                states.push(new State(id, "IS00590", ["mgrid-disable"]));
                                states.push(new State(id, "IS00591", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00592", ["mgrid-disable"]));
                                states.push(new State(id, "IS00593", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00054":
                    switch (item.itemCode) {
                        case "IS00595":
                            if (item.value === "0") {
                                _.forEach(['IS00596', 'IS00597', 'IS00598'/*, 'IS00599'*/, 'IS00600'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00596":
                            if (item.value === "0" && record.IS00595 === "1") {
                                states.push(new State(id, "IS00597", ["mgrid-disable"]));
                                states.push(new State(id, "IS00598", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00599", ["mgrid-disable"]));
                                states.push(new State(id, "IS00600", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00055":
                    switch (item.itemCode) {
                        case "IS00602":
                            if (item.value === "0") {
                                _.forEach(['IS00603', 'IS00604', 'IS00605'/*, 'IS00606'*/, 'IS00607'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00603":
                            if (item.value === "0" && record.IS00602 === "1") {
                                states.push(new State(id, "IS00604", ["mgrid-disable"]));
                                states.push(new State(id, "IS00605", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00606", ["mgrid-disable"]));
                                states.push(new State(id, "IS00607", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00056":
                    switch (item.itemCode) {
                        case "IS00609":
                            if (item.value === "0") {
                                _.forEach(['IS00610', 'IS00611', 'IS00612'/*, 'IS00613'*/, 'IS00614'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00610":
                            if (item.value === "0" && record.IS00609 === "1") {
                                states.push(new State(id, "IS00611", ["mgrid-disable"]));
                                states.push(new State(id, "IS00612", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00613", ["mgrid-disable"]));
                                states.push(new State(id, "IS00614", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00057":
                    switch (item.itemCode) {
                        case "IS00616":
                            if (item.value === "0") {
                                _.forEach(['IS00617', 'IS00618', 'IS00619'/*, 'IS00620'*/, 'IS00621'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00617":
                            if (item.value === "0" && record.IS00616 === "1") {
                                states.push(new State(id, "IS00618", ["mgrid-disable"]));
                                states.push(new State(id, "IS00619", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00620", ["mgrid-disable"]));
                                states.push(new State(id, "IS00621", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00058":
                    switch (item.itemCode) {
                        case "IS00623":
                            if (item.value === "0") {
                                _.forEach(['IS00624', 'IS00625', 'IS00626'/*, 'IS00627'*/, 'IS00628'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00624":
                            if (item.value === "0" && record.IS00623 === "1") {
                                states.push(new State(id, "IS00625", ["mgrid-disable"]));
                                states.push(new State(id, "IS00626", ["mgrid-disable"]));
//                                states.push(new State(id, "IS00627", ["mgrid-disable"]));
                                states.push(new State(id, "IS00628", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
            }
        }
        
        getControlType(controlType: IItemDefinitionData, item: any, sort: any) {
            if (_.isNil(controlType)) return;
            let self = this, control, name;
            self.dataTypes[item.key] = { cls: controlType };
            if (item.key === "IS00779") {
                item.constraint = { primitiveValue: "StampNumber", isCheckExpression: true };
            } else {
                item.constraint = { primitiveValue: item.itemId.replace(/[-_]/g, "") };
            }
            
            switch (controlType.dataTypeValue) {
                case ITEM_SINGLE_TYPE.STRING:
                    if (controlType.stringItemType === ITEM_STRING_TYPE.EMPLOYEE_CODE) {
                        item.constraint = { primitiveValue: "EmployeeCode" };
                    }
                    
                    let spaceCheck = cps003.control.STRING[self.category.catCode() + "_" + item.key];
                    if (spaceCheck) item.inputProcess = spaceCheck.bind(null, item.required);
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;
                case ITEM_SINGLE_TYPE.NUMERIC:
                    if (controlType.numericItemAmount === 1) {
                        let constraint = (item.constraint || {});
                        constraint.decimalLength = controlType.decimalPart;
                        item.constraint = constraint;
                    }
                    
                    item.dataType = "number";
                    let timeNumber = cps003.control.NUMBER[self.category.catCode() + "_" + item.key],
                        numberType = cps003.control.NUMBER_Lan[self.category.catCode() + "_" + item.key];
                    if (numberType) item.inputProcess = numberType;
                    if (timeNumber) item.inputProcess = timeNumber;
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    sort.type = "Number";
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;
                case ITEM_SINGLE_TYPE.DATE:
                    item.columnCssClass = "halign-right";
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    if (controlType.dateItemType === DateType.YEARMONTHDAY) {
                        name = "DatePickerYMD" + item.key;
                        item.constraint.type = "ymd";
                        control = { name: name, format: "ymd", controlType: "DatePicker" };
                        sort.type = "FullDate";
                        let dp = cps003.control.DATE_TIME[self.category.catCode() + "_" + item.key];
                        if (dp) control.inputProcess = dp;
                    } else if (controlType.dateItemType === DateType.YEARMONTH) {
                        name = "DatePickerYM" + item.key;
                        item.constraint.type = "ym";
                        control = { name: name, format: "ym", controlType: "DatePicker" };
                        sort.type = "YearMonth";
                    } else {
                        name = "DatePickerY" + item.key;
                        item.constraint.type = "y";
                        control = { name: name, format: "y", controlType: "DatePicker" };
                    }
                    
                    item.ntsControl = name;
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;
                case ITEM_SINGLE_TYPE.TIME:
                    item.columnCssClass = "halign-right";
                    timeNumber = cps003.control.NUMBER[self.category.catCode() + "_" + item.key];
                    if (timeNumber) item.inputProcess = timeNumber;
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    sort.type = "Time";
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;    
                case ITEM_SINGLE_TYPE.TIMEPOINT:
                    item.columnCssClass = "halign-right";
                    timeNumber = cps003.control.NUMBER[self.category.catCode() + "_" + item.key];
                    if (timeNumber) item.inputProcess = timeNumber;
                    let timeRange = cps003.control.TIME_RANGE[self.category.catCode() + "_" + item.key];
                    let timeRangeGroup = cps003.control.TIME_RANGE_GROUP[self.category.catCode() + "_" + item.key];
                    if (timeRange && timeRangeGroup) {
                        item.inputProcess = () => {
                            let dfd = $.Deferred(), args = arguments;
                            timeRange.apply(void 0, [item.required, item.constraint.primitiveValue, item.headerText].concat(...arguments)).fail(hasError => {
                                if (hasError) return;
                                timeRangeGroup(...args);
                            });
                            
                            dfd.reject();
                            return dfd.promise();
                        };
                    } else if (timeRange) item.inputProcess = timeRange.bind(null, item.required, item.constraint.primitiveValue, item.headerText);
                    else if (timeRangeGroup) item.inputProcess = timeRangeGroup;
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    sort.type = "Time";
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;
                case ITEM_SINGLE_TYPE.SELECTION:
                case ITEM_SINGLE_TYPE.SEL_RADIO:
                    name = "Combobox" + item.key;
                    control = { name: name, optionsValue: "optionValue", optionsText: "optionText", displayMode: "name", enable: true, controlType: "ComboBox" };
                    if (controlType.referenceType === ITEM_SELECT_TYPE.CODE_NAME
                        || (controlType.referenceType === ITEM_SELECT_TYPE.DESIGNATED_MASTER && item.key === "IS00079")) {
                        control.pattern = [];
                        control.list = {};
                    }
                    
                    self.dataTypes[item.key].specs = control;
                    item.ntsControl = name; 
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;
                case ITEM_SINGLE_TYPE.SEL_BUTTON:
                    name = "ReferButton" + item.key;
                    let notFoundMes = nts.uk.resource.getText("CPS001_107"); 
                    control = { name: name, enable: true, optionsValue: "optionValue", optionsText: "optionText", text: "参照", notFound: notFoundMes, pattern: [], list: {}, controlType: "ReferButton" };
                    let selectBtn = cps003.control.SELECT_BUTTON[self.category.catCode() + "_" + item.key];
                    control.click = selectBtn && selectBtn.bind(null, item.required);
                    self.dataTypes[item.key].specs = control;
                    item.ntsControl = name;
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    if (!item.hidden) self.batchSettingItems.push(item.itemId);
                    break;
                case ITEM_SINGLE_TYPE.READONLY:
                    item.ntsControl = "Label";
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    break;
                case ITEM_SINGLE_TYPE.RELATE_CATEGORY:
                    name = "RelateButton" + item.key;
                    control = { name: name, enable: true, text: "詳細情報", labelPosition: "before", controlType: "ReferButton" };
                    let selectBtn = cps003.control.RELATE_BUTTON[self.category.catCode() + "_" + item.key];
                    control.click = selectBtn && selectBtn.bind(null);
                    item.ntsControl = name;
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    break;
                case ITEM_SINGLE_TYPE.NUMBERIC_BUTTON:
                    item.dataType = "number";
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    sort.type = "Number";
                    break;
                case ITEM_SINGLE_TYPE.READONLY_BUTTON:
                    item.ntsControl = "Label";
                    sort.columnKey = item.key;
                    sort.allowSorting = true;
                    break;
            }
            
            return control;
        }
        
        selectButtonClick(itemCode: string) {
            let selectButtonDlg = {};
            let itemCodes = [ "IS00130", "IS00131", "IS00128", "IS00139", "IS00140", "IS00157", "IS00158",
                "IS00166", "IS00167", "IS00175", "IS00176", "IS00148", "IS00149", "IS00193", "IS00194", "IS00202",
                "IS00203", "IS00211", "IS00212", "IS00220", "IS00221", "IS00229", "IS00230", "IS00238", "IS00239",
                "IS00184", "IS00185", "IS00084", "IS00085" ];
            
            _.forEach(itemCodes, code => {
                selectButtonDlg[code] = () => {
                    // Param KDL002
                    // List code to display in grid (lstComboboxValue)
                    nts.uk.ui.windows.setShared("KDL002_AllItemObj");
                    // Selected code list (value)
                    nts.uk.ui.windows.setShared("KDL002_SelectedItemId");
                    // Single/multiple (boolean)
                    nts.uk.ui.windows.setShared("KDL002_Multiple", false);
                    // Not select -> click button 決定 -> show msg10 or not (boolean)
                    nts.uk.ui.windows.setShared("KDL002_isAcceptSelectNone", false);
                    // Show item "選択なし" or not
                    nts.uk.ui.windows.setShared("KDL002_isShowNoSelectRow", false);
                    nts.uk.ui.windows.sub.modal("/view/kdl/002/a/index.xhtml", { title: "" }).onClosed(() => {
                        // Selected result
                        let ls = nts.uk.ui.windows.getShared('KDL002_SelectedNewItem');
                    });
                };      
            });
        }

        settingColumns() {
            let self = this,
                id = self.category.catId(),
                ctg = _.find(self.category.items(), m => m.id == id);

            setShared('CPS003D_PARAM', {
                id: id,
                name: ctg.categoryName
            });

            modal("/view/cps/003/d/index.xhtml").onClosed(() => {
                let $grid = $("#grid"), columns = getShared('CPS003D_VALUE');
                if (!columns) return;
                
                if (self.employees.length === 0) {
                    service.fetch.setting(self.category.catId()).done((data: ISettingData) => {
                        if (ko.isObservable(self.settings.matrixDisplay)) {
                            self.settings.matrixDisplay(data.matrixDisplay);
                        }    
                        
                        if (ko.isObservable(self.settings.perInfoData)) {
                            self.settings.perInfoData(data.perInfoData);
                        }
                    });
                    
                    self.requestData();
                    return;
                }
                
                _.forEach(self.settings.perInfoData(), (itemInfo: IPersonInfoSetting) => {
                    if (_.find(columns, itemId => itemId === itemInfo.perInfoItemDefID)) {
                        $grid.mGrid("showColumn", itemInfo.itemCD, true);
                        let item = _.find(self.gridOptions.columns, column => column.key === itemInfo.itemCD);
                        if (!_.find(self.batchSettingItems, batchItem => batchItem === itemInfo.perInfoItemDefID)
                            && item && item.perInfoTypeState.dataTypeValue !== ITEM_SINGLE_TYPE.READONLY
                            && item.perInfoTypeState.dataTypeValue !== ITEM_SINGLE_TYPE.RELATE_CATEGORY
                            && item.perInfoTypeState.dataTypeValue !== ITEM_SINGLE_TYPE.NUMBERIC_BUTTON) {
                            self.batchSettingItems.push(itemInfo.perInfoItemDefID);
                        }
                    } else {
                        $grid.mGrid("hideColumn", itemInfo.itemCD, true);
                        _.remove(self.batchSettingItems, batchItem => batchItem === itemInfo.perInfoItemDefID);
                    }
                });
                
                service.fetch.setting(self.category.catId()).done((data: ISettingData) => {
                    if (ko.isObservable(self.settings.matrixDisplay)) {
                        self.settings.matrixDisplay(data.matrixDisplay);
                    }    
                    
                    if (ko.isObservable(self.settings.perInfoData)) {
                        self.settings.perInfoData(data.perInfoData);
                    }
                });
            });
        }

        specialItems = {
            standardDate: [ "IS00279", "IS00295", "IS00302", "IS00309", "IS00316", "IS00323", "IS00330", "IS00337", "IS00344", "IS00351", 
                            "IS00358", "IS00559", "IS00566", "IS00573", "IS00580", "IS00587", "IS00594", "IS00601", "IS00608", "IS00615", "IS00622" ],
            workTime: [ "IS00131", "IS00140", "IS00158", "IS00167", "IS00176", "IS00149", "IS00194", "IS00203", "IS00212", "IS00221", "IS00230", "IS00239", "IS00185" ],
            holidayLimit: [ "IS00287" ],
            workplace: [ "IS00084", "IS00085" ],
            department: [ "IS00073" ]
        }
        
        settingBatchs() {
            let self = this, $grid = $("#grid"),
                id = self.category.catId(),
                ctg = _.first(self.category.items(), m => m.id == id);

            setShared('CPS003F_PARAM', {
                id: id,
                baseDate: moment.utc(self.baseDate(), "YYYY/MM/DD").toISOString(),
                // push list ids of item show in grid
                itemsDefIds: self.batchSettingItems
            });

            modal("/view/cps/003/f/index.xhtml").onClosed(() => {
                let replaceValue: IReplaceValueDto = getShared('CPS003F_VALUE');
                if (!replaceValue) return;
                if (_.find(self.specialItems.standardDate, it => it === replaceValue.targetItem)) {
                    if (replaceValue.replaceFormat === REPLACE_FORMAT.VALUE) {
                        $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                            if (find(self.hiddenRows, id => id === obj.id)) return false;
                            if (replaceValue.replaceAll) return true;
                            if (value instanceof Date) {
                                return replaceValue.matchValue === `${value.getFullYear()}/${value.toLocaleDateString("en-US", { month: "2-digit" }).replace(/[^0-9-]/g, "")}/${value.toLocaleDateString("en-US", { day: "2-digit" }).replace(/[^0-9-]/g, "")}`;    
                            }
                            
                            if (value instanceof moment) {
                                if (value.isValid()) {
                                    return replaceValue.matchValue === value.format("YYYY/MM/DD");
                                } else {
                                    return ((_.isNil(value._i) || value._i === "") && (_.isNil(replaceValue.matchValue) || replaceValue.matchValue === ""))
                                        || value._i === replaceValue.matchValue;
                                }
                            }
                            
                            return replaceValue.matchValue === value;
                        }, () => replaceValue.replaceValue, true);
                    } else if (replaceValue.replaceFormat === REPLACE_FORMAT.GRAND_DATE) { //　年休付与基準日
                        // Get 年休社員基本情報
                        let empIdList = _.map($grid.mGrid("dataSource"), (ds: Record) => ds.employeeId);
                        if (empIdList.length > 0) {
                            service.fetch.basicHolidayEmpInfo({ listEmpID: empIdList }).done((infos: Array<AnnualLeaveEmpBasicInfo>) => {
                                let groupByEmpId = _.groupBy(infos, "employeeId");
                                $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                                    if (find(self.hiddenRows, id => id === obj.id)) return false;
                                    if (replaceValue.replaceAll) return true;
                                    if (value instanceof Date) {
                                        return replaceValue.matchValue === `${value.getFullYear()}/${value.toLocaleDateString("en-US", { month: "2-digit" }).replace(/[^0-9-]/g, "")}/${value.toLocaleDateString("en-US", { day: "2-digit" }).replace(/[^0-9-]/g, "")}`;
                                    }
                                    
                                    if (value instanceof moment) {
                                        if (value.isValid()) {
                                            return replaceValue.matchValue === value.format("YYYY/MM/DD");
                                        } else {
                                            return ((_.isNil(value._i) || value._i === "") && (_.isNil(replaceValue.matchValue) || replaceValue.matchValue === ""))
                                                || value._i === replaceValue.matchValue;
                                        }
                                    }
                                    
                                    return replaceValue.matchValue === value;
                                }, (value, rec) => {
                                    let holidayInfo: Array<AnnualLeaveEmpBasicInfo> = groupByEmpId[rec.employeeId];
                                    if (holidayInfo) {
                                        return holidayInfo[0].grantRule.grantStandardDate;
                                    }
                                }, true);
                            });
                        }
                    } else if (replaceValue.replaceFormat === REPLACE_FORMAT.HIRE_DATE) {
                        // TODO: From standardDate and employeeId list, get historyItem and replace
                        let empIdList = _.map($grid.mGrid("dataSource"), (ds: Record) => ds.employeeId);
                        if (empIdList.length > 0) {
                            service.fetch.affiliatedCompanyHist({ listEmpID: empIdList, baseDate: moment.utc(self.baseDate(), "YYYY/MM/DD").toISOString() }).done((histItems: Array<AffCompanyEmpHistItem>) => {
                                // Group histItems by employeeId
                                let groupByEmpId = _.groupBy(histItems, "employeeID");
                                
                                $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                                    if (find(self.hiddenRows, id => id === obj.id)) return false;
                                    if (replaceValue.replaceAll) return true;
                                    if (value instanceof Date) {
                                        return replaceValue.matchValue === `${value.getFullYear()}/${value.toLocaleDateString("en-US", { month: "2-digit" }).replace(/[^0-9-]/g, "")}/${value.toLocaleDateString("en-US", { day: "2-digit" }).replace(/[^0-9-]/g, "")}`;
                                    }
                                    
                                    if (value instanceof moment) {
                                        if (value.isValid()) {
                                            return replaceValue.matchValue === value.format("YYYY/MM/DD");
                                        } else {
                                            return ((_.isNil(value._i) || value._i === "") && (_.isNil(replaceValue.matchValue) || replaceValue.matchValue === ""))
                                                || value._i === replaceValue.matchValue;
                                        }
                                    }
                                    
                                    return replaceValue.matchValue === value;
                                }, (value, rec) => {
                                    let histItem = groupByEmpId[rec.employeeId];
                                    if (!_.isNil(histItem)) {
                                        return histItem[0].startDate;
                                    }
                                }, true);
                            });
                        }
                    } else if (replaceValue.replaceFormat === REPLACE_FORMAT.DESI_YEAR_OE) {
                        // TODO: From standardDate and employeeId list, get historyItem and replace
                        let empIdList = _.map($grid.mGrid("dataSource"), (ds: Record) => ds.employeeId);
                        if (empIdList.length > 0) {
                            service.fetch.affiliatedCompanyHist({ listEmpID: empIdList, baseDate: moment.utc(self.baseDate(), "YYYY/MM/DD").toISOString() }).done((histItems: Array<AffCompanyEmpHistItem>) => {
                                let groupByEmpId = _.groupBy(histItems, "employeeID");
                                
                                if (replaceValue.replaceValue[0] === YEAR_OF_JOIN.SAME) {
                                    // TODO: Replace by historyItem
                                    $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                                        if (find(self.hiddenRows, id => id === obj.id)) return false;
                                        if (replaceValue.replaceAll) return true;
                                        let v;
                                        if (value instanceof Date) {
                                            v = moment(value).format("YYYY/MM/DD");
                                        } else if (value instanceof moment) {
                                            if (value.isValid()) {
                                                v = value.format("YYYY/MM/DD");
                                            } else {
                                                return ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value._i) || value._i === ""))
                                                    || replaceValue.matchValue === value._i;
                                            }
                                        } else v = value;
                                        
                                        return replaceValue.matchValue === v;
                                    }, (value, rec) => {
                                        let histItem = groupByEmpId[rec.employeeId];
                                        if (!_.isNil(histItem)) {
                                            let month = Math.floor(replaceValue.replaceValue[1] / 100),
                                                day = replaceValue.replaceValue[1] % 100,
                                                year = moment(histItem[0].startDate).year(),
                                                date = moment([ year, month - 1, day ]);
                                            
                                            if (!date.isValid()) {
                                                return moment([ year, month - 1, day - 1 ]).format("YYYY/MM/DD");
                                            }
                                            
                                            return date.format("YYYY/MM/DD");
                                        }
                                    }, true);
                                } else if (replaceValue.replaceValue[0] === YEAR_OF_JOIN.PREV) {
                                    $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                                        if (find(self.hiddenRows, id => id === obj.id)) return false;
                                        if (replaceValue.replaceAll) return true;
                                        let v;
                                        if (value instanceof Date) {
                                            v = moment(value).format("YYYY/MM/DD");
                                        } else if (value instanceof moment) {
                                            v = value.format("YYYY/MM/DD");
                                        } else v = value;
                                        
                                        return replaceValue.matchValue === v;
                                    }, (value, rec) => {
                                        let histItem = groupByEmpId[rec.employeeId];
                                        if (!_.isNil(histItem)) {
                                            let month = Math.floor(replaceValue.replaceValue[1] / 100),
                                                day = replaceValue.replaceValue[1] % 100,
                                                year = moment(histItem[0].startDate).year() - 1,
                                                date = moment([ year, month - 1, day ]);
                                            
                                            if (!date.isValid()) {
                                                return moment([ year, month - 1, day - 1 ]).format("YYYY/MM/DD"); 
                                            }
                                            
                                            return date.format("YYYY/MM/DD");
                                        }
                                    }, true);
                                } else {
                                    $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                                        if (find(self.hiddenRows, id => id === obj.id)) return false;
                                        if (replaceValue.replaceAll) return true;
                                        let v;
                                        if (value instanceof Date) {
                                            v = moment(value).format("YYYY/MM/DD");
                                        } else if (value instanceof moment) {
                                            v = value.format("YYYY/MM/DD");
                                        } else v = value;
                                        
                                        return replaceValue.matchValue === v;
                                    }, (value, rec) => {
                                        let histItem = groupByEmpId[rec.employeeId];
                                        if (!_.isNil(histItem)) {
                                            let month = Math.floor(replaceValue.replaceValue[1] / 100),
                                                day = replaceValue.replaceValue[1] % 100,
                                                year = moment(histItem[0].startDate).year() + 1,
                                                date = moment([ year, month - 1, day ]);
                                            
                                            if (!date.isValid()) {
                                                return moment([ year, month - 1, day - 1 ]).format("YYYY/MM/DD");
                                            }
                                            
                                            return date.format("YYYY/MM/DD");
                                        }
                                    }, true);
                                }
                            });
                        }
                    }
                } else if (_.isArray(replaceValue.targetItem) && _.find(self.specialItems.workTime, it => it === replaceValue.targetItem[0])) {
                    for (let i = replaceValue.targetItem.length - 1; i >= 0; i--) {
                        let item = replaceValue.targetItem[i];
                        let replaced = replaceValue.replaceValue[i], dt = self.dataTypes[item];
                        if (dt && dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIMEPOINT && !_.isNil(replaced) && replaced !== "") {
                            replaced = nts.uk.time.minutesBased.clock.dayattr.create(replaced).shortText;
                        } else if (dt && dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIME && !_.isNil(replaced) && replaced !== "") {
                            replaced = nts.uk.time.parseTime(replaced, true).format();
                        }
                        
                        $grid.mGrid("replace", item, (value, rec) => {
                            if (find(self.hiddenRows, id => id === rec.id)) return false; 
                            if (replaceValue.replaceAll) return true;
                            return replaceValue.matchValue === rec[replaceValue.targetItem[0]]; 
                        }, () => replaced, true);
                    }
                } else if (self.specialItems.holidayLimit[0] === replaceValue.targetItem) {
                    if (replaceValue.replaceFormat === REPLACE_FORMAT.VALUE) { // 値指定
                        let replaced = nts.uk.time.parseTime(replaceValue.replaceValue, true).format();
                        if (!_.isNil(replaceValue.matchValue) && replaceValue.matchValue !== "") {
                            replaceValue.matchValue = nts.uk.time.parseTime(replaceValue.matchValue, true).format();
                        }
                        
                        $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                            if (find(self.hiddenRows, id => id === obj.id)) return false;
                            if (replaceValue.replaceAll) return true;
                            return replaceValue.matchValue === value 
                                || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""));
                        }, (value, obj) => {
                            setTimeout(() => {
                                let afterProc = cps003.control.NUMBER[self.category.catCode() + "_" + replaceValue.targetItem];
                                if (afterProc) {
                                    afterProc(obj.id, replaceValue.targetItem, replaced, obj).done(res => {
                                        forEach(res, s => {
                                            $grid.mGrid("updateCell", s.id, s.item, s.value);
                                        });
                                    });
                                }
                            }, 1);
                            
                            return replaced;
                        }, true);
                    } else if (replaceValue.replaceFormat === REPLACE_FORMAT.CONTRACT_TIME) { // 契約時間
                        // TODO: Get contract time
                        let empIdList = _.map($grid.mGrid("dataSource"), (ds: Record) => ds.employeeId);
                        service.fetch.contractTime({ baseDate: moment.utc(self.baseDate(), "YYYY/MM/DD").toISOString(), listEmpID: empIdList }).done(contractTimes => {
                            let groupByEmpId = _.groupBy(contractTimes, "employeeID");
                            if (!_.isNil(replaceValue.matchValue) && replaceValue.matchValue !== "") {
                                replaceValue.matchValue = nts.uk.time.parseTime(replaceValue.matchValue, true).format();
                            }
                            
                            $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                                if (find(self.hiddenRows, id => id === obj.id)) return false;
                                if (replaceValue.replaceAll) return true;
                                return replaceValue.matchValue === value
                                    || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""));
                            }, (value, rec) => {
                                let contractTime = groupByEmpId[rec.employeeId];
                                let replaced = "";
                                if (!_.isNil(contractTime)) {
                                    replaced = nts.uk.time.parseTime(replaceValue.replaceValue * contractTime[0].contractTime, true).format();
                                }
                                
                                setTimeout(() => {
                                    let afterProc = cps003.control.NUMBER[self.category.catCode() + "_" + replaceValue.targetItem];
                                    if (afterProc) {
                                        afterProc(rec.id, replaceValue.targetItem, replaced, rec).done(res => {
                                            forEach(res, s => {
                                                $grid.mGrid("updateCell", s.id, s.item, s.value);
                                            });
                                        });
                                    }
                                }, 1);
                                
                                return replaced;
                            }, true);
                        });
                    }
                } else if (replaceValue.mode === APPLY_MODE.AMOUNT) {
                    if (replaceValue.replaceFormat === REPLACE_FORMAT.ADD_OR_SUB) { // 加減算
                        $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                            if (find(self.hiddenRows, id => id === obj.id)) return false;
                            if (_.isNil(value) || value === "") return false;
                            if (replaceValue.replaceAll) return true;
                            return replaceValue.matchValue === value
                                || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""));
                        }, (value) => String(replaceValue.replaceValue + value), true); 
                    } else if (replaceValue.replaceFormat === REPLACE_FORMAT.VALUE) { // 値指定
                        $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                            if (find(self.hiddenRows, id => id === obj.id)) return false;
                            if (replaceValue.replaceAll) return true;
                            return replaceValue.matchValue === value
                                || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""));
                        }, () => {
                            return !_.isNil(replaceValue.replaceValue) ? String(replaceValue.replaceValue) : null;
                        }, true);
                    }
                } else if (_.find(self.specialItems.workplace, it => it === replaceValue.targetItem)) {
                    let wpName = {}, promises = [], dates = [];
                    _.forEach($grid.mGrid("dataSource"), ds => {
                        let dateStr = moment.utc(ds["IS00082"]).toISOString();
                        if (!_.isNil(wpName[dateStr])) return;
                        promises.push(service.fetch.workplaceInfo({ baseDate: dateStr, listWorkplaceID: [ replaceValue.replaceValue ]}));   
                        dates.push(dateStr);
                    });
                    
                    $.when.apply($, promises).done(() => {
                        for (let i = 0; i < arguments.length; i++) {
                            if (arguments[i] && arguments[i].length > 0) {
                                wpName[dates[i]] = arguments[i][0].workplaceId;
                            }
                        }
                        
                        $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                            if (find(self.hiddenRows, id => id === obj.id)) return false;
                            if (replaceValue.replaceAll) return true;
                            return replaceValue.matchValue === value;
                        }, (value, rec) => {
                            let dateStr = moment.utc(rec["IS00082"]).toISOString();
                            return wpName[dateStr] || null;
                        }, true);
                    });
                } else if (_.find(self.specialItems.department, it => it === replaceValue.targetItem)) {
                } else if (replaceValue.mode === APPLY_MODE.SELECTION) {
                    $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                        if (find(self.hiddenRows, id => id === obj.id)) return false;
                        if (replaceValue.replaceAll) return true;
                        
                        return replaceValue.matchValue === value
                            || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""))
                            || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && !find($grid.mGrid("optionsList", obj.id, replaceValue.targetItem), opt => opt.optionValue === value));
                    }, () => replaceValue.replaceValue, true);
                } else {
                    let replaced = replaceValue.replaceValue, dt = self.dataTypes[replaceValue.targetItem];
                    if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIMEPOINT) {
                        if (!_.isNil(replaced) && replaced !== "") {
                            replaced = nts.uk.time.minutesBased.clock.dayattr.create(replaced).shortText;
                        }
                        
                        if (!replaceValue.replaceAll && !_.isNil(replaceValue.matchValue) && replaceValue.matchValue !== "") {
                            replaceValue.matchValue = nts.uk.time.minutesBased.clock.dayattr.create(replaceValue.matchValue).shortText;
                        }
                    } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIME) {
                        if (!_.isNil(replaced) && replaced !== "") {
                            replaced = nts.uk.time.parseTime(replaced, true).format();
                        }
                        
                        if (!replaceValue.replaceAll && !_.isNil(replaceValue.matchValue) && replaceValue.matchValue !== "") {
                            replaceValue.matchValue = nts.uk.time.parseTime(replaceValue.matchValue, true).format();
                        }
                    } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.DATE) {
                        if (_.isNil(replaced)) replaced = "";
                    }
                    
                    $grid.mGrid("replace", replaceValue.targetItem, (value, obj) => {
                        if (find(self.hiddenRows, id => id === obj.id)) return false;
                        if (replaceValue.replaceAll 
                            || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""))) return true;
                        
                        if (value instanceof Date) {
                            return replaceValue.matchValue === `${value.getFullYear()}/${value.toLocaleDateString("en-US", { month: "2-digit" }).replace(/[^0-9-]/g, "")}/${value.toLocaleDateString("en-US", { day: "2-digit" }).replace(/[^0-9-]/g, "")}`;    
                        } else if (value instanceof moment) {
                            if (!value.isValid()) {
                                return replaceValue.matchValue === value._i
                                    || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value._i) || value._i === ""));
                            }
                            
                            return replaceValue.matchValue === value.format("YYYY/MM/DD");
                        }   
                        
                        return (_.isString(replaceValue.matchValue) && _.trim(replaceValue.matchValue) === value) 
                            || replaceValue.matchValue === value
                            || ((_.isNil(replaceValue.matchValue) || replaceValue.matchValue === "") && (_.isNil(value) || value === ""));
                    }, (value, obj) => {
                        setTimeout(() => {
                            let afterProc = cps003.control.NUMBER[self.category.catCode() + "_" + replaceValue.targetItem];
                            if (afterProc) {
                                afterProc(obj.id, replaceValue.targetItem, replaced, obj).done(res => {
                                    forEach(res, s => {
                                        $grid.mGrid("updateCell", s.id, s.item, s.value);
                                    });
                                });
                            }
                        }, 1);
                        
                        return replaced;
                    }, true);
                }
            });
        }
    }
    
    function find(arr, jb) {
        if (!arr) return;
        for (let i = 0; i < arr.length; i++) {
            if (jb(arr[i], i)) return arr[i];
        }
        
        return null;
    }
    
    function findAll(arr, jb) {
        if (!arr) return;
        let result = [];
        for (let i = 0; i < arr.length; i++) {
            if (jb(arr[i], i)) result.push(arr[i]);
        }
        
        return result;
    }
    
    function forEach(arr, jb) {
        if (!arr) return;
        for (let i = 0; i < arr.length; i++) {
            if (jb(arr[i], i) === false) break;
        }
    }

    interface IEmployee {
    }

    class Employee {
    }

    /* Dữ liệu nhận về từ  */
    interface IRequestData {
        baseDate: string;
        categoryId: string;
        headDatas: IDataHead[];
        bodyDatas: IDataBody[];
    }

    /* Dữ liệu  */
    export interface IDataHead {
        itemId: string;
        itemCode: string;
        itemName: string;
        itemOrder: number;
        itemParentCode: string;
        itemTypeState: ISingleItem;
        required: boolean;
        resourceId: string;
    }

    /* Dữ liệu body (điều chỉnh thêm) */
    interface IDataBody {
        personId: string;
        employeeId: string;
        classification: ICodeName;
        department: ICodeName;
        employee: ICodeName;
        employeeBirthday: string;
        employment: ICodeName;
        position: ICodeName;
        workplace: ICodeName;
        items: IColumnData[];
    }

    interface ICodeName {
        code: string;
        name: string;
    }

    /* Dữ liệu tương ứng từng cột */
    interface IColumnData {
        actionRole: ACTION_ROLE;
        itemCode: string;
        itemParentCode: string;

        lstComboBoxValue: any[]; // list data để validate 

        recordId: string | null; // id bản ghi trong db
        textValue: string | null; // giá trị hiển thị 
        value: Object | null; // giá trị
    }

    export enum ACTION_ROLE {
        HIDDEN = <any>"HIDDEN",
        VIEW_ONLY = <any>"VIEW_ONLY",
        EDIT = <any>"EDIT"
    }

    // define ITEM_SINGLE_TYPE
    // type of item if it's single item
    export enum ITEM_SINGLE_TYPE {
        STRING = 1,
        NUMERIC = 2,
        DATE = 3,
        TIME = 4,
        TIMEPOINT = 5,
        SELECTION = 6,
        SEL_RADIO = 7,
        SEL_BUTTON = 8,
        READONLY = 9,
        RELATE_CATEGORY = 10,
        NUMBERIC_BUTTON = 11,
        READONLY_BUTTON = 12
    }

    // define ITEM_STRING_DATA_TYPE
    export enum ITEM_STRING_DTYPE {
        FIXED_LENGTH = 1, // fixed length
        VARIABLE_LENGTH = 2 // variable length
    }

    export enum ITEM_STRING_TYPE {
        ANY = 1,
        // 2:全ての半角文字(AnyHalfWidth)
        ANYHALFWIDTH = 2,
        // 3:半角英数字(AlphaNumeric)
        ALPHANUMERIC = 3,
        // 4:半角数字(Numeric)
        NUMERIC = 4,
        // 5:全角カタカナ(Kana)
        KANA = 5,
        // 6: カードNO
        CARDNO = 6,
        // 7: 社員コード
        EMPLOYEE_CODE = 7
    }

    // define ITEM_SELECT_TYPE
    // type of item if it's selection item
    export enum ITEM_SELECT_TYPE {
        // 1:専用マスタ(DesignatedMaster)
        DESIGNATED_MASTER = <any>"DESIGNATED_MASTER",
        // 2:コード名称(CodeName)
        CODE_NAME = <any>"CODE_NAME",
        // 3:列挙型(Enum)
        ENUM = <any>"ENUM"
    }

    enum DateType {
        YEARMONTHDAY = 1,
        YEARMONTH = 2,
        YEAR = 3
    }

    interface ISingleItem {
        itemType: number;
        dataTypeState?: IItemDefinitionData // Single item value
    }

    interface IItemDefinitionData extends IItemTime, IItemDate, IItemString, IItemTimePoint, IItemNumeric, IItemSelection {
        dataTypeValue: ITEM_SINGLE_TYPE; // type of value of item
    }

    interface IItemTime {
        min?: number;
        max?: number;
    }

    interface IItemDate {
        dateItemType?: DateType;
    }

    interface IItemString {
        stringItemDataType?: ITEM_STRING_DTYPE;
        stringItemLength?: number;
        stringItemType?: ITEM_STRING_TYPE;
    }

    interface IItemTimePoint {
        timePointItemMin?: number;
        timePointItemMax?: number;
    }

    interface IItemNumeric {
        numericItemMinus?: number;
        numericItemAmount?: number;
        integerPart?: number;
        decimalPart?: number;
        numericItemMin?: number;
        numericItemMax?: number;
    }

    interface IItemSelection extends IItemMasterSelection, IItemEnumSelection, IItemCodeNameSelection {
        referenceType?: ITEM_SELECT_TYPE;
    }

    interface IItemMasterSelection {
        masterType?: string;
    }

    interface IItemEnumSelection {
        typeCode?: string;
    }

    interface IItemCodeNameSelection {
        enumName?: string;
    }
    
    class State {
        rowId: number;
        columnKey: string;
        state: Array<any>
        constructor(rowId: string, columnKey: string, state: Array<any>) {
            this.rowId = rowId;
            this.columnKey = columnKey;
            this.state = state;
        }
    }
    
    class Record {
        rowNumber: number;
        id: string;
        personId: string;
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        classCode: string;
        className: string;
        deptCode: string;
        deptName: string;
        employmentCode: string;
        employmentName: string;
        positionCode: string;
        positionName: string;
        workplaceCode: string;
        workplaceName: string;
        register: boolean;
        print: boolean;
        
        constructor(data: IDataBody, rowNumber: number) {
            if (!data) return this;
            this.rowNumber = rowNumber + 1;
            this.id = (data.items && data.items[0] && data.items[0].recordId) || nts.uk.util.randomId() + "_noData";
            this.personId = data.personId;
            this.employeeId = data.employeeId;
            this.employeeCode = data.employee.code;
            this.employeeName = data.employee.name;
            this.classCode = data.classification.code;
            this.className = data.classification.name;
            this.deptCode = data.department.code;
            this.deptName = data.department.name;
            this.employmentCode = data.employment.code;
            this.employmentName = data.employment.name;
            this.positionCode = data.position.code;
            this.positionName = data.position.name;
            this.workplaceCode = data.workplace.code;
            this.workplaceName = data.workplace.name;
            this.register = false;
            this.print = false;
        }
    }
    
    class GridEmployeeInfoDataSource {
        personId: string;
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        departmentName: string;
        workplaceName: string;   
        positionName: string;
        employmentName: string;
        classificationName: string;  
        items: Array<GridEmpBodyDataSource> = [];
        
        constructor(record: Record, heads: Array<IDataHead>, $grid: any) {
            this.personId = record.personId;
            this.employeeId = record.employeeId;
            this.employeeCode = record.employeeCode;
            this.employeeName = record.employeeName;
            this.departmentName = record.deptName;
            this.workplaceName = record.workplaceName;
            this.positionName = record.positionName;
            this.employmentName = record.employmentName;
            this.classificationName = record.className;
//            _.forEach(_.keys(record), f => {
//                if (_.find(heads, (h: IDataHead) => h.itemCode === f)) {
//                    this.items.push(new GridEmpBodyDataSource(f, record, $grid));
//                }
//            });
            
            _.forEach(heads, (h: IDataHead) => {
                this.items.push(new GridEmpBodyDataSource(h.itemCode, record, $grid));
            });
        }
    }
    
    class GridEmpBodyDataSource {
        recordId: string;
        itemCode: string;
        value: any;
        lstComboBoxValue: Array<ComboBoxObject>;
        
        constructor(itemCode: string, record: Record, $grid: any) {
            this.recordId = record.id;
            this.itemCode = itemCode;
            let value = record[itemCode];
            if (value instanceof Date) {
                this.value = moment(value).format("YYYY/MM/DD");
            } else if (value instanceof moment) {
                this.value = value._i;
            } else this.value = value;
            
            this.lstComboBoxValue = $grid.mGrid("optionsList", this.recordId, this.itemCode);
        }
    }
    
    class ComboBoxObject {
        optionValue: string;
        optionText: string;
    }
    
    interface StampCardEditing {
        method: EDIT_METHOD;
        digitsNumber: number;
    }
    
    enum EDIT_METHOD {
        PreviousZero = 1,
        AfterZero = 2,
        PreviousSpace = 3,
        AfterSpace = 4
    }

    interface ISettingData {
        "perInfoData": KnockoutObservableArray<IPersonInfoSetting> | Array<IPersonInfoSetting>;
        "matrixDisplay": KnockoutObservable<IMatrixDisplay> | IMatrixDisplay;
    }

    interface IPersonInfoSetting {
        "perInfoItemDefID": string;
        "itemCD": string;
        "itemName": string;
        "regulationAtr": boolean;
        "dispOrder": number;
        "width": number;
        "required": boolean;
    }

    interface IMatrixDisplay {
        "companyID"?: String;
        "userID"?: String;
        "cursorDirection": CURSOR_DIRC,
        "clsATR": IUSE_SETTING;
        "jobATR": IUSE_SETTING;
        "workPlaceATR": IUSE_SETTING,
        "departmentATR": IUSE_SETTING;
        "employmentATR": IUSE_SETTING;
    }
    
    class AnnualLeaveEmpBasicInfo {
        employeeId: string;
        companyId: string;
        grantRule: AnnualLeaveGrantRule;
    }
    
    class AnnualLeaveGrantRule {
        grantStandardDate: string;
    }
    
    class AffCompanyEmpHistItem {
        employeeID: string;
        destinationData: boolean;
        endDate: string;
        historyId: string;
        startDate: string;
    }
    
    class DatePeriod {
        start: string;
        end: string;
    }

    enum IUSE_SETTING {
        USE = <any>'USE',
        NOT_USE = <any>'NOT_USE'
    }

    enum CURSOR_DIRC {
        VERTICAL = <any>'VERTICAL',
        HORIZONTAL = <any>'HORIZONTAL'
    }
    
    export enum IT_CAT_TYPE {
        SINGLE = 1, // Single info
        MULTI = 2, // Multi info
        CONTINU = 3, // Continuos history
        NODUPLICATE = 4, //No duplicate history
        DUPLICATE = 5, // Duplicate history,
        CONTINUWED = 6 // Continuos history with end date
    }
    
    interface IReplaceValueDto {
        mode: APPLY_MODE;
        // 全て置換する
        replaceAll: boolean;
        // 対象項目
        targetItem: string | string[];
        // 一致する値
        matchValue: any;
        // 値
        replaceValue: any | any[]; // 入社年指定 or 対象項目 (string[]), replaceValue is any[]
        // 置換形式 
        replaceFormat: REPLACE_FORMAT;
    }
    
    enum APPLY_MODE {
        DATE = 1,
        STRING = 2,
        TIME = 3,
        CLOCK = 4,
        NUMBER = 5,
        AMOUNT = 6,
        SELECTION = 7,
        WORKTIME = 8,
        GRANDDATE = 9,
        TIMEYEAR = 10
    }
    
    enum REPLACE_FORMAT {
        VALUE = 0,          //値指定
        ADD_OR_SUB = 1,     //加減算
        HIRE_DATE = 2,      //入社日
        GRAND_DATE = 3,     //年休付与基準日
        DESI_YEAR_OE = 4,   //入社年指定
        CONTRACT_TIME = 5   //契約時間
    }
    
    enum YEAR_OF_JOIN {
        SAME = 0, //同年
        PREV = 1,  //前年
        NEXT = 2 //翌年
    }
}