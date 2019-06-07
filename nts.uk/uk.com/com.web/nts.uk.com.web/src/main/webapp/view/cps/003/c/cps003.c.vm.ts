module cps003.c.vm {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import modeless = nts.uk.ui.windows.sub.modeless;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import textUK = nts.uk.text;
    import block = nts.uk.ui.block.grayout;
    import unblock = nts.uk.ui.block.clear;
    
    export class ScreenModel {

        baseDate: KnockoutObservable<Date> = ko.observable();
        updateMode: KnockoutObservable<any> = ko.observable();
        perInfoCatePermission: KnockoutObservable<any> = ko.observable();
        category: {
            catId: KnockoutObservable<string>;
            catCode: KnockoutObservable<string>;
            cate: KnockoutObservable<any>;
        } = {
            catId: ko.observable(""),
            catCode: ko.observable(""),
            cate: ko.observable()
        };
        
        gridOptions = { dataSource: [], columns: [], features: [], ntsControls: [] };
        initDs: Array<Record>;
        dataTypes: any = {};
        
        constructor() {
            let self = this;
            cps003.control.selectButton();
            cps003.control.relateButton();
        }

        start() {
            let self = this;
            
            let param = getShared("CPS003G_PARAM"), paramB: GridDto = getShared("CPS003B_PARAM");
            
            block();
            self.baseDate(param.baseDate);
            self.updateMode(param.updateMode);
            self.category.catId(param.catId);
            self.category.cate(param.cate);
            self.category.catCode(param.cate.categoryCode);
            self.perInfoCatePermission(param.perInfoCatePermission);
            
            if (paramB && paramB.headDatas) {
                self.convertData(paramB).done(() => {
                    self.loadGrid();
                    self.initDs = _.cloneDeep(self.gridOptions.dataSource);
                    let $grid = $("#grid");
                    $grid.mGrid("validate");
                    let errors = $grid.mGrid("errors");
                    if (errors.length > 0) {
                        let errGroup = _.groupBy(errors, "rowId");
                        _.forEach(_.keys(errGroup), id => {
                            $grid.mGrid("updateCell", id, "status", "エラー(" + errGroup[id].length + "件)", true);
                        });
                    }
                    
                    unblock();
                });
            }
        }
        
        convertData(data: GridDto) {
            let self = this, dfd = $.Deferred();
            if (data.headDatas) {
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
                
                let item, control, parent = {}, hideRowAdd;
                self.dataTypes = {};
                
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
                    { headerText: nts.uk.resource.getText("CPS003_50"), key: "register", dataType: "boolean", width: "30px", ntsControl: "RegCheckBox", bound: true },
                    { headerText: nts.uk.resource.getText("CPS003_51"), key: "status", dataType: "string", width: "100px", ntsControl: "Label" },
                    { headerText: nts.uk.resource.getText("CPS003_52"), key: "employeeCode", dataType: "string", width: "100px", ntsControl: "Label" },
                    { headerText: nts.uk.resource.getText("CPS003_53"), key: "employeeName", dataType: "string", width: "140px", ntsControl: "Label" },
                    { headerText: nts.uk.resource.getText("CPS003_130"), key: "rowAdd", dataType: "string", width: "40px", ntsControl: "RowAdd", hidden: hideRowAdd }
                ];
                
                self.gridOptions.ntsControls = [
                    { name: 'RegCheckBox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                    { name: "RowAdd", source: "plus-button", cssClass: "blue-color", controlType: "Image", copy: 2 }
                ];
                let headerStyles = { name: "HeaderStyles", columns: [] };
                
                _.forEach(data.headDatas, (d: GridEmpHead) => {
                    let controlType = d.itemTypeState.dataTypeState;
                    if (controlType) {
                        let name;
                        
                        if (_.isNil(d.itemParentCode) || d.itemParentCode === "") {
                            parent[d.itemCode] = d.itemName + d.itemCode;
                            name = d.itemName + d.itemCode;
                        } else {
                            name = parent[d.itemParentCode] + "-" + d.itemName + d.itemCode;
                            parent[d.itemCode] = name;
                        }
                        
                        item = { headerText: name, itemId: d.itemId, itemName: d.itemName, key: d.itemCode, required: d.required, parentCode: d.itemParentCode, dataType: "string", width: "100px", perInfoTypeState: controlType };
                        controlType.required = d.required;
                        control = self.getControlType(controlType, item);
                        self.gridOptions.columns.push(item);
                        
                        if (control) {
                            self.gridOptions.ntsControls.push(control);
                            let combo = cps003.control.COMBOBOX[self.category.catCode() + "_" + d.itemCode]; 
                            if (combo) {
                                control.inputProcess = combo;
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
                        parent[d.itemCode] = d.itemName + d.itemCode;
                    }
                });
                
                self.gridOptions.features = [{ name: "Resizing" }, { name: "Copy" }, { name: "Tooltip", error: true }, { name: "WidthSaving", reset: true }];
                // TODO: Get fixed columns
                let columnFixing = { name: "ColumnFixing", columnSettings: [] };
                _.forEach([ "register", "status", "employeeCode", "employeeName", "rowAdd" ], f => {
                    columnFixing.columnSettings.push({ columnKey: f, isFixed: true });
                });
                
                self.gridOptions.features.push(columnFixing);
                self.gridOptions.features.push(headerStyles);
            }
            
            if (data.employees) {
                self.gridOptions.dataSource = [];
                let states = [], workTimeCodes = [], nullWorkTimeCodes = [], workTimeItems = [], nullWorkTimeItems = [], codes = {}, displayItems = [];
                
                _.forEach(self.gridOptions.columns, (column, i) => {
                    if (i < 5) return;
                    displayItems.push(column.key);
                });
                
                data.employees.sort((emp1, emp2) => {
                    if (_.isNil(emp1.employeeCode) && _.isNil(emp2.employeeCode)) return 0;
                    if (_.isNil(emp1.employeeCode)) return -1;
                    if (_.isNil(emp2.employeeCode)) return 1;
                    return emp1.employeeCode.compareTo(emp2.employeeCode);
                });
                
                _.forEach(data.employees, (d: EmployeeRowDto) => {
                    let record = new Record(d), disItems = _.cloneDeep(displayItems);
                    _.forEach(d.items, (item: ItemRowDto) => {
                        let dt = self.dataTypes[item.itemCode], disabled;
                        if (!dt) return;
                        if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.DATE && dt.cls.dateItemType === DateType.YEARMONTHDAY) {
                            record[item.itemCode] = _.isNil(item.value) || item.value === "" ? item.value : moment.utc(item.value, "YYYY/MM/DD").toDate();
                            if (self.category.catCode() === "CS00070" && (item.itemCode === "IS00781" || item.itemCode === "IS00782")) {
                                states.push(new State(record.id, item.itemCode, ["mgrid-disable"]));
                                disabled = true;
                            }
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIMEPOINT && !_.isNil(item.value)) {
                            let toNumber = Number(item.value);
                            record[item.itemCode] = isNaN(toNumber) ? item.value : nts.uk.time.minutesBased.clock.dayattr.create(toNumber).shortText;
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.TIME && !_.isNil(item.value)) {
                            let parsed = nts.uk.time.parseTime(item.value, true);
                            record[item.itemCode] = parsed.success ? parsed.format() : item.value;
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.READONLY) {
                            if (self.category.catCode() === "CS00024" && item.itemCode === "IS00289") {
                                record[item.itemCode] = !_.isNil(item.value) && item.value !== "" ? nts.uk.time.parseTime(item.value).format() : "";
                            } else {
                                record[item.itemCode] = item.value;
                            }
                        } else {
                            record[item.itemCode] = item.value;
                        }
                        
                        if (item.actionRole === ACTION_ROLE.VIEW_ONLY && !disabled) {
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
                            
                            self.combobox(record.id, item, states);
                        } else if (dt.cls.dataTypeValue === ITEM_SINGLE_TYPE.SEL_BUTTON) {
                            dt.specs.pattern.push(item.lstComboBoxValue);
                            dt.specs.list[item.recordId] = dt.specs.pattern.length - 1;
                            if (cps003.control.WORK_TIME[item.itemCode]) {
                                if (!_.isNil(item.value)) {
                                    workTimeCodes.push(item.value);
                                    workTimeItems.push(item.itemCode);
                                } else {
                                    nullWorkTimeCodes.push(item.value);
                                    nullWorkTimeItems.push(item.itemCode);
                                }
                                
                                if (_.has(codes, item.value)) {
                                    codes[item.value].push(item.recordId);
                                } else {
                                    codes[item.value] = [ item.recordId ];
                                }
                            }
                        }
                    });
                    
                    _.forEach(disItems, itm => states.push(new State(record.id, itm, ["mgrid-lock"])));
                    self.gridOptions.dataSource.push(record);
                });
                
                if (workTimeCodes.length > 0) {
                    cps003.control.fetch.check_mt_se({ workTimeCodes: workTimeCodes }).done(mt => {
                        _.forEach(workTimeCodes, (c, i) => {
                            let head = _.find(mt, f => f.workTimeCode === c),
                                itemCode = workTimeItems[i],
                                workTime = cps003.control.WORK_TIME[itemCode];
                            if (head) {
                                if (workTime.firstTimes && !head.startEnd) {
                                    _.forEach(codes[c], r => {
                                        states.push(new State(r, workTime.firstTimes.start, ["mgrid-disable"]));
                                        states.push(new State(r, workTime.firstTimes.end, ["mgrid-disable"]));
                                    });
                                }
                                
                                if (workTime.secondTimes && (!head.startEnd || !head.multiTime)) {
                                    _.forEach(codes[c], r => {
                                        states.push(new State(r, workTime.secondTimes.start, ["mgrid-disable"]));
                                        states.push(new State(r, workTime.secondTimes.end, ["mgrid-disable"]));
                                    });
                                }
                            } else {
                                if (workTime.firstTimes) {
                                    _.forEach(codes[c], r => {
                                        states.push(new State(r, workTime.firstTimes.start, ["mgrid-disable"]));
                                        states.push(new State(r, workTime.firstTimes.end, ["mgrid-disable"]));
                                    });
                                }
                                
                                if (workTime.secondTimes) {
                                    _.forEach(codes[c], r => {
                                        states.push(new State(r, workTime.secondTimes.start, ["mgrid-disable"]));
                                        states.push(new State(r, workTime.secondTimes.end, ["mgrid-disable"]));
                                    });
                                }
                            }
                        });
                        
                        dfd.resolve();
                    });
                } else dfd.resolve();
                
                _.forEach(nullWorkTimeCodes, (c, i) => {
                    let itemCode = nullWorkTimeItems[i],
                        workTime = cps003.control.WORK_TIME[itemCode];    
                    if (workTime.firstTimes) {
                        _.forEach(codes[c], r => {
                            states.push(new State(r, workTime.firstTimes.start, ["mgrid-disable"]));
                            states.push(new State(r, workTime.firstTimes.end, ["mgrid-disable"]));
                        });
                    }
                    
                    if (workTime.secondTimes) {
                        _.forEach(codes[c], r => {
                            states.push(new State(r, workTime.secondTimes.start, ["mgrid-disable"]));
                            states.push(new State(r, workTime.secondTimes.end, ["mgrid-disable"]));
                        });
                    }
                });
                
                self.gridOptions.features.push({ name: "CellStyles", states: states });
            }
            
            return dfd.promise();
        }
        
        combobox(id: any, item: ItemRowDto, states) {
            switch (this.category.cate().categoryCode) {
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
                            if (item.value === "0") {
                                _.remove(states, s => s.columnKey === "IS00124" || s.columnKey === "IS00125" || s.columnKey === "IS00126");
                                if (!_.find(states, s => s.columnKey === "IS00127")) {
                                    states.push(new State(id, "IS00127", ["mgrid-disable"]));
                                }
                            } else if (item.value === "1") {
                                if (!_.find(states, s => s.columnKey === "IS00124")) {
                                    states.push(new State(id, "IS00124", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.columnKey === "IS00125")) {
                                    states.push(new State(id, "IS00125", ["mgrid-disable"]));
                                }
                                _.remove(states, s => s.columnKey === "IS00126" || s.columnKey === "IS00127");
                            } else if (item.value === "2") {
                                if (!_.find(states, s => s.columnKey === "IS00124")) {
                                    states.push(new State(id, "IS00124", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.columnKey === "IS00125")) {
                                    states.push(new State(id, "IS00125", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.columnKey === "IS00126")) {
                                    states.push(new State(id, "IS00126", ["mgrid-disable"]));
                                }
                                if (!_.find(states, s => s.columnKey === "IS00127")) {
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
                                states.push(new State(id, "IS00300", ["mgrid-disable"]));
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
                                states.push(new State(id, "IS00307", ["mgrid-disable"]));
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
                                states.push(new State(id, "IS00314", ["mgrid-disable"]));
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
                                states.push(new State(id, "IS00321", ["mgrid-disable"]));
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
                                states.push(new State(id, "IS00328", ["mgrid-disable"]));
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
                                states.push(new State(id, "IS00335", ["mgrid-disable"]));
                                states.push(new State(id, "IS00336", ["mgrid-disable"]));
                            }
                            break;
                    }
                    break;
                case "CS00031":
                    switch (item.itemCode) {
                        case "IS00338":
                            if (item.value === "0") {
                                _.forEach(['IS00339', 'IS00340', 'IS00341', 'IS00342', 'IS00343'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00032":
                    switch (item.itemCode) {
                        case "IS00345":
                            if (item.value === "0") {
                                _.forEach(['IS00346', 'IS00347', 'IS00348', 'IS00349', 'IS00350'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00033":
                    switch (item.itemCode) {
                        case "IS00352":
                            if (item.value === "0") {
                                _.forEach(['IS00353', 'IS00354', 'IS00355', 'IS00356', 'IS00357'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00034":
                    switch (item.itemCode) {
                        case "IS00359":
                            if (item.value === "0") {
                                _.forEach(['IS00360', 'IS00361', 'IS00362', 'IS00363', 'IS00364'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
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
                                _.forEach(['IS00376', 'IS00377', 'IS00378', 'IS00379'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                        case "IS00380":
                            if (item.value === "0") {
                                _.forEach(['IS00381', 'IS00382', 'IS00383', 'IS00384'], code => {
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
                                _.forEach(['IS00561', 'IS00562', 'IS00563', 'IS00564', 'IS00565'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00050":
                    switch (item.itemCode) {
                        case "IS00567":
                            if (item.value === "0") {
                                _.forEach(['IS00568', 'IS00569', 'IS00570', 'IS00571', 'IS00572'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00051":
                    switch (item.itemCode) {
                        case "IS00574":
                            if (item.value === "0") {
                                _.forEach(['IS00575', 'IS00576', 'IS00577', 'IS00578', 'IS00579'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00052":
                    switch (item.itemCode) {
                        case "IS00581":
                            if (item.value === "0") {
                                _.forEach(['IS00582', 'IS00583', 'IS00584', 'IS00585', 'IS00586'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00053":
                    switch (item.itemCode) {
                        case "IS00588":
                            if (item.value === "0") {
                                _.forEach(['IS00589', 'IS00590', 'IS00591', 'IS00592', 'IS00593'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00054":
                    switch (item.itemCode) {
                        case "IS00595":
                            if (item.value === "0") {
                                _.forEach(['IS00596', 'IS00597', 'IS00598', 'IS00599', 'IS00600'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00055":
                    switch (item.itemCode) {
                        case "IS00602":
                            if (item.value === "0") {
                                _.forEach(['IS00603', 'IS00604', 'IS00605', 'IS00606', 'IS00607'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00056":
                    switch (item.itemCode) {
                        case "IS00609":
                            if (item.value === "0") {
                                _.forEach(['IS00610', 'IS00611', 'IS00612', 'IS00613', 'IS00614'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00057":
                    switch (item.itemCode) {
                        case "IS00616":
                            if (item.value === "0") {
                                _.forEach(['IS00617', 'IS00618', 'IS00619', 'IS00620', 'IS00621'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
                            }
                            break;
                    }
                    break;
                case "CS00058":
                    switch (item.itemCode) {
                        case "IS00623":
                            if (item.value === "0") {
                                _.forEach(['IS00624', 'IS00625', 'IS00626', 'IS00627', 'IS00628'], code => {
                                    states.push(new State(id, code, ["mgrid-disable"]));
                                });
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
                    
                    break;
                case ITEM_SINGLE_TYPE.NUMERIC:
                    item.dataType = "number";
                    let timeNumber = cps003.control.NUMBER[self.category.catCode() + "_" + item.key];
                    if (timeNumber) item.inputProcess = timeNumber;
                    break;
                case ITEM_SINGLE_TYPE.DATE:
                    item.columnCssClass = "halign-right";
                    if (controlType.dateItemType === DateType.YEARMONTHDAY) {
                        name = "DatePickerYMD" + item.key;
                        item.constraint.type = "ymd";
                        control = { name: name, format: "ymd", controlType: "DatePicker" };
                        let dp = cps003.control.DATE_TIME[self.category.catCode() + "_" + item.key];
                        if (dp) control.inputProcess = dp;
                    } else if (controlType.dateItemType === DateType.YEARMONTH) {
                        name = "DatePickerYM" + item.key;
                        item.constraint.type = "ym";
                        control = { name: name, format: "ym", controlType: "DatePicker" };
                    } else {
                        name = "DatePickerY" + item.key;
                        item.constraint.type = "y";
                        control = { name: name, format: "y", controlType: "DatePicker" };
                    }
                    
                    item.ntsControl = name;
                    break;
                case ITEM_SINGLE_TYPE.TIME:
                    item.columnCssClass = "halign-right";
                    timeNumber = cps003.control.NUMBER[self.category.catCode() + "_" + item.key];
                    if (timeNumber) item.inputProcess = timeNumber;
                    break;    
                case ITEM_SINGLE_TYPE.TIMEPOINT:
                    item.columnCssClass = "halign-right";
                    timeNumber = cps003.control.NUMBER[self.category.catCode() + "_" + item.key];
                    if (timeNumber) item.inputProcess = timeNumber;
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
                    break;
                case ITEM_SINGLE_TYPE.SEL_BUTTON:
                    name = "ReferButton" + item.key;
                    let notFoundMes = nts.uk.resource.getText("CPS001_107"); 
                    control = { name: name, enable: true, optionsValue: "optionValue", optionsText: "optionText", text: "参照", notFound: notFoundMes, pattern: [], list: {}, controlType: "ReferButton" };
                    let selectBtn = cps003.control.SELECT_BUTTON[self.category.catCode() + "_" + item.key];
                    control.click = selectBtn && selectBtn.bind(null, item.required);
                    self.dataTypes[item.key].specs = control;
                    item.ntsControl = name;
                    break;
                case ITEM_SINGLE_TYPE.READONLY:
                    item.ntsControl = "Label";
                    break;
                case ITEM_SINGLE_TYPE.RELATE_CATEGORY:
                    name = "RelateButton" + item.key;
                    control = { name: name, enable: true, text: "詳細情報", labelPosition: "before", controlType: "ReferButton" };
                    let selectBtn = cps003.control.RELATE_BUTTON[self.category.catCode() + "_" + item.key];
                    control.click = selectBtn && selectBtn.bind(null);
                    item.ntsControl = name;
                    break;
                case ITEM_SINGLE_TYPE.NUMBERIC_BUTTON:
                    item.dataType = "number";
                    break;
                case ITEM_SINGLE_TYPE.READONLY_BUTTON:
                    break;
            }
            
            return control;
        }
        
        loadGrid() {
            let self = this;
            if ($("#grid").data("mGrid")) $("#grid").mGrid("destroy");
            new nts.uk.ui.mgrid.MGrid($("#grid")[0], {
                width: "1000px",
                height: "800px",
                headerHeight: "80px",
                subHeight: "140px",
                subWidth: "100px",
                dataSource: self.gridOptions.dataSource,
                primaryKey: "id",
                virtualization: true,
                virtualizationMode: "continuous",
                enter: "right",
                autoFitWindow: true,
                errorColumns: [ "employeeId", "employeeCode", "employeeName", "rowNumber" ],
                idGen: (id) => id + "_" + nts.uk.util.randomId(),
                notice: () => {
                    let $grid = $("#grid");
                    if (arguments[0] === true) {
                        let matches = /(\d+)/.exec(arguments[3].status);
                        if (matches) {
                            $grid.mGrid("updateCell", arguments[1], "status", Number(matches[1]) - 1 == 0 ? "正常" : "エラー(" + (Number(matches[1]) - 1) + "件)", true);
                        }
                    } else {
                        let matches = /(\d+)/.exec(arguments[2].status), status;
                        $grid.mGrid("updateCell", arguments[0], "status", "エラー(" + (matches ? (Number(matches[1]) + 1) : 1) + "件)", true);
                    }
                },
                columns: self.gridOptions.columns,
                features: self.gridOptions.features,
                ntsControls: self.gridOptions.ntsControls
            }).create();
        }
        
        register() {
            let self = this,
                command, employees = [], recId = {}, $grid = $("#grid");
            
            confirm({ messageId: self.updateMode() === 1 ? "Msg_749" : "Msg_748" }).ifYes(() => {
                $grid.mGrid("validate");
                let itemErrors = $grid.mGrid("errors");
                if (itemErrors && itemErrors.length > 0) {
                    setShared("CPS003G_ERROR_LIST", _.map(itemErrors, err => { 
                        return { employeeId: err.employeeId, empCd: err.employeeCode, empName: err.employeeName, no: err.index + 1, 
                                 isDisplayRegister: true, errorType: 0, itemName: err.columnName, message: err.message }; }));
                    modeless("/view/cps/003/g/index.xhtml").onClosed(() => {
                        
                    });
                    
                    return;
                }
                
                let updates = $("#grid").mGrid("updatedCells");
                if (updates.length === 0) return;
                block();
                _.forEach($("#grid").mGrid("dataSource"), d => recId[d.id] = d);
                let cateName, cateType, regId = {};
                if (self.category.cate()) {
                    cateName = self.category.cate().categoryName;
                    cateType = self.category.cate().categoryType;
                }
                
                _.forEach(updates, item => {
                    if (item.columnKey === "register") return;
                    let recData: Record = recId[item.rowId];
                    let regEmp = regId[recData.id];
                    if (!regEmp) {
                        regEmp = { personId: recData.personId, employeeId: recData.employeeId, employeeCd: recData.employeeCode, employeeName: recData.employeeName, order: recData.rowNumber };
                        regEmp.input = { categoryId: self.category.catId(), categoryCd: self.category.catCode(), categoryName: cateName, categoryType: cateType, recordId: recData.id, delete: false, items: [] };
                        regId[recData.id] = regEmp;
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
                        
                        regEmp.input.items.push({ definitionId: col.itemId, itemCode: col.key, itemName: col.itemName, value: _.isObject(text) ? text.value : val, text: _.isObject(text) ? text.text : text, defValue: _.isObject(defText) ? defText.value : defValue, defText: _.isObject(defText) ? defText.text : defText, type: col.perInfoTypeState.dataTypeValue, logType: col.perInfoTypeState.dataTypeValue });
                    }
                    
                    employees.push(regEmp);
                });
                
                command = { baseDate: self.baseDate(), editMode: self.updateMode(), employees: employees };
                service.push.register(command).done((errorList) => {
                    info({ messageId: "Msg_15" }).then(() => {
                        unblock();
                        setShared("CPS003C_REG_DONE", true);
                        self.close();
                    });
                }).fail((res) => {
                    unblock();
                    alert(res.message);
                });
            }).ifNo(() => {});
        }
        
        getText(perInfoTypeState: any, value: any, id: any, itemCode: any, $grid: any) {
            if (!perInfoTypeState) return value;
            switch (perInfoTypeState.dataTypeValue) {
                case ITEM_SINGLE_TYPE.STRING:
                case ITEM_SINGLE_TYPE.NUMERIC:
                    return value;
                case ITEM_SINGLE_TYPE.TIME:
                    if (!_.isNil(value)) return { value: nts.uk.time.parseTime(value).toValue(), text: value };
                case ITEM_SINGLE_TYPE.TIMEPOINT:
                    if (!_.isNil(value)) return { value: nts.uk.time.parseTime(value).toValue(), text: nts.uk.time.minutesBased.clock.dayattr.create(this.value).fullText() };
                case ITEM_SINGLE_TYPE.DATE:
                    if (value instanceof moment && !value.isValid()) {
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
            $grid.mGrid("validate");
            let errors = $grid.mGrid("errors");
            if (errors.length === 0) {
                nts.uk.ui.dialog.info({ messageId: "Msg_1463" });
                return;
            }
            
            setShared("CPS003G_ERROR_LIST", _.map(errors, err => { 
                return { employeeId: err.employeeId, empCd: err.employeeCode, empName: err.employeeName, no: err.index + 1, 
                         isDisplayRegister: false, errorType: 0, itemName: err.columnName, message: err.message }; }));
            modeless("/view/cps/003/g/index.xhtml").onClosed(() => {
                
            });
        }
        
        close() {
            nts.uk.ui.windows.close();
        }
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
        id: string;
        personId: string;
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        register: boolean;
        status: string;
        
        constructor(data: EmployeeRowDto) {
            if (!data) return this;
            this.id = (data.items && data.items[0] && data.items[0].recordId) || nts.uk.util.randomId() + "_noData";
            this.personId = data.personId;
            this.employeeId = data.employeeId;
            this.employeeCode = data.employeeCode;
            this.employeeName = data.employeeName;
            this.register = false;
            this.status = "正常";
        }
    }
    
    interface GridDto {
        employees: Array<EmployeeRowDto>;
        errorItems: ItemRowDto;
        headDatas: GridEmpHead;
    }

    export interface EmployeeRowDto {
        employeeCode: string;
        employeeId: string;
        employeeName: string;
        items: Array<ItemRowDto>;
        numberOfError: number;
        personId: string;
    }

    interface ItemRowDto {
        itemCode: string;
        itemName: string;
        itemOrder: number;
        lstComboBoxValue: ComboBoxObject;
        recordId: string;
        textValue: string;
        value: any;
    }

    interface ComboBoxObject {
        optionText: string;
        optionValue: string;
    }

    interface GridEmpHead {
        childs: Array<GridEmpHead>;
        itemCode: string;
        itemId: string;
        itemName: string;
        itemOrder: number; 
        itemParentCode: string;
        itemTypeState: ISingleItem;
        required: boolean; 
        resourceId: string;
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

    export enum ACTION_ROLE {
        HIDDEN = <any>"HIDDEN",
        VIEW_ONLY = <any>"VIEW_ONLY",
        EDIT = <any>"EDIT"
    }

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

    export enum ITEM_SELECT_TYPE {
        // 1:専用マスタ(DesignatedMaster)
        DESIGNATED_MASTER = <any>"DESIGNATED_MASTER",
        // 2:コード名称(CodeName)
        CODE_NAME = <any>"CODE_NAME",
        // 3:列挙型(Enum)
        ENUM = <any>"ENUM"
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
    
    enum DateType {
        YEARMONTHDAY = 1,
        YEARMONTH = 2,
        YEAR = 3
    }
    
    export enum IT_CAT_TYPE {
        SINGLE = 1, // Single info
        MULTI = 2, // Multi info
        CONTINU = 3, // Continuos history
        NODUPLICATE = 4, //No duplicate history
        DUPLICATE = 5, // Duplicate history,
        CONTINUWED = 6 // Continuos history with end date
    }
}
