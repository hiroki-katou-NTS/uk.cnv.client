
import { _, Vue, moment } from '@app/provider';
import { component, Watch, Prop } from '@app/core/component';
import { FixTableComponent } from '@app/components/fix-table';
import { TimeDuration, TimeWithDay } from '@app/utils/time';
import { storage } from '@app/utils';
import { KdwS03BComponent } from 'views/kdw/s03/b';
import { KdwS03AMenuComponent } from 'views/kdw/s03/a/menu';
import { KdwS03CComponent } from 'views/kdw/s03/c';
import { KdwS03DComponent } from 'views/kdw/s03/d';

@component({
    name: 'kdws03a',
    route: '/kdw/s03/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    constraints: [],
    components: {
        'fix-table': FixTableComponent,
        'kdws03b': KdwS03BComponent,
        'kdws03amenu': KdwS03AMenuComponent,
        'kdws03c': KdwS03CComponent,
        'kdws03d': KdwS03DComponent
    },
    validations: {
        yearMonth: {
            required: true
        },
        selectedDate: {
            required: true
        },
    }
})
export class Kdws03AComponent extends Vue {

    @Prop({ default: () => ({
        screenMode: 0,
        closureID: null,
        changePeriodAtr: true,
        displayFormat: 0,
        targetEmployee: '',
        closureYM: 0,
        dateTarget: null,
        initClock: null,
        transitionDesScreen: null,
        errorRefStartAtr: false
    }) })
    public readonly params: Params;

    public title: string = 'Kdws03A';
    public isFirstLoad: boolean = true;
    public rownum: number = 0;
    public rowHeight: number = 0;
    public displayFormat: any = '0';
    public formatCode: string = '';
    public lstDataSourceLoad: Array<any> = [];
    public lstDataHeader: Array<any> = [];
    public optionalHeader: Array<any> = [];
    public cellStates: Array<any> = [];
    public fixHeaders: Array<any> = [];
    public showPrincipal: boolean = true;
    public showSupervisor: boolean = true;
    public hasLstHeader: boolean = true;
    public displayDataLst: Array<any> = [];
    public displayHeaderLst: Array<any> = [];
    public displaySumLst: any = {};
    public displayDataLstEx: Array<any> = [];
    public lstCellDisByLock: Array<any> = [];
    public lstEmployee: Array<any> = [];
    public yearMonth: number = 0;
    public actualTimeOptionDisp: Array<any> = [];
    public timePeriodAllInfo: any = null;
    public actualTimeSelectedCode: number = 0;
    public selectedEmployee: string = '';
    public selectedDate: Date = null;
    public resetTable: number = 0;
    public previousState: string = 'button-deactive';
    public nextState: string = 'button-deactive';
    public itemStart: number = 0;
    public itemEnd: number = 0;
    // backup item to recover when error
    public actualTimeSelectedCodeTemp: number = 0;
    public yearMonthTemp: number = 0;
    public selectedEmployeeTemp: string = '';
    public selectedDateTemp: Date = null;
    public dPCorrectionMenuDto: any = {
        allConfirmButtonDis: false,
        errorReferButtonDis: false,
        restReferButtonDis: false,
        monthActualReferButtonDis: false,
        timeExcessReferButtonDis: false
    };
    public lstAttendanceItem: Array<any> = [];
    public autBussCode: Array<any> = [];
    //combobox
    public comboItemsCalc: any = null;
    public comboItemsReason: any = null;
    public comboItemsDoWork: any = null;
    public comboItemsCompact: any = null;
    public comboTimeLimit: any = null;
    // param B screen
    public paramData: any = null;

    @Watch('$route')
    public changeParamUrl(value: any, valueOld: any) {
        window.location.reload();
    }

    @Watch('yearMonth')
    public changeYearMonth(value: any, valueOld: any) {
        this.yearMonthTemp = valueOld;
        this.selectedEmployeeTemp = this.selectedEmployee;
        this.actualTimeSelectedCodeTemp = this.actualTimeSelectedCode;

        this.initActualTime(value, this.selectedEmployee);
    }

    @Watch('selectedEmployee')
    public changeEmployee(value: any, valueOld: any) {
        if (_.isNil(valueOld) || '' == valueOld) {
            return;
        }
        this.selectedEmployeeTemp = valueOld;
        this.yearMonthTemp = this.yearMonth;
        this.actualTimeSelectedCodeTemp = this.actualTimeSelectedCode;

        this.initActualTime(this.yearMonth, this.selectedEmployee);
    }

    @Watch('dateRanger', { deep: true })
    public changeDateRange(value: any, valueOld: any) {
        if (_.isNil(value) || this.displayFormat == '1' || this.isFirstLoad) {
            this.isFirstLoad = false;
            
            return;
        } else {
            this.startPage();
        }
    }

    @Watch('actualTimeSelectedCode')
    public changeTimeCode(value: any, valueOld: any) {
        this.actualTimeSelectedCodeTemp = valueOld;
        this.selectedEmployeeTemp = this.selectedEmployee;
        this.yearMonthTemp = this.yearMonth;
    }

    @Watch('selectedDate')
    public changeDate(value: any, valueOld: any) {
        if (_.isNil(valueOld) || (!_.isNil(this.selectedDateTemp) && this.selectedDate.toString() == this.selectedDateTemp.toString())) {
            return;
        }
        this.selectedDateTemp = valueOld;
        this.startPage();
    }

    get dateRanger() {
        let self = this;
        if (!_.isNil(self.timePeriodAllInfo)) {
            for (let i = 0; i < self.timePeriodAllInfo.lstRangeCls.length; i++) {
                if (self.actualTimeSelectedCode == self.timePeriodAllInfo.lstRangeCls[i].closureId) {
                    return ({ startDate: self.timePeriodAllInfo.lstRangeCls[i].startDate, endDate: self.timePeriodAllInfo.lstRangeCls[i].endDate });
                }
            }
        }
    }

    // 起動
    public created() {
        if (this.$route.query.displayformat == '0' || this.$route.query.displayformat == '1') {
            this.displayFormat = this.$route.query.displayformat;
        } else {
            this.displayFormat = this.params.displayFormat;
        }

        if (this.displayFormat == '0') {
            this.rownum = Math.floor((window.innerHeight - 183) / 42) - 1;
            this.rowHeight = 42 + (window.innerHeight - 183 -  (this.rownum + 1) * 42) / (this.rownum + 1);
        } else {
            this.rownum = Math.floor((window.innerHeight - 140) / 42);
            this.rowHeight = 42 + (window.innerHeight - 140 -  (this.rownum + 1) * 42) / (this.rownum + 1);
        }
        
        if (this.params.screenMode == 0) {
            this.pgName = this.displayFormat == '0' ? 'name1' : 'name2';
        } else {
            this.pgName = this.displayFormat == '0' ? 'name3' : 'name4';
        }

        this.$auth.user.then((user: null | { employeeId: string }) => {
            this.selectedEmployee = user.employeeId;
            this.startPage();
        });

    }

    public mounted() {
        this.$mask('show', { message: true });
    }

    //fix-tableのレイアウトを変更する
    public updated() {
        let styleTagAr: any = [];
        styleTagAr = document.querySelectorAll('.btn-sm');
        _.forEach(styleTagAr, (x) => x.style.fontSize = '11px');

        let styleContainer: any = [];
        styleContainer = document.querySelectorAll('.container-fluid');
        if (!_.isEmpty(styleContainer)) {
            styleContainer[0].style.overflow = 'hidden';
        }

        let styleTableBody: any = [];
        styleTableBody = document.querySelectorAll('.table-body');
        if (!_.isEmpty(styleTableBody)) {
            if (this.displayFormat == '0') {
                styleTableBody[0].style.height = this.rownum * this.rowHeight + 'px';
            } else {
                styleTableBody[0].style.height = this.rownum * this.rowHeight + 6 + 'px';
            }
            if (this.displayDataLst.length <= this.rownum) {
                styleTableBody[0].style.overflow = 'hidden';
            }    
        }
    }

    //日別実績データの取得
    public startPage() {
        let self = this;
        let selectedCode: Array<any> = [];
        let cache: any = storage.session.getItem('selectedCode');
        self.$mask('show', { message: true });

        // if (!_.isNil(self.formatCode) && self.formatCode != '') {
        //     selectedCode.push(self.formatCode);
        // }    
        if (!_.isNil(cache)) {
            selectedCode.push(cache.selectedCode);
        }  

        let param = {
            changePeriodAtr: self.params.changePeriodAtr,
            screenMode: self.params.screenMode,
            errorRefStartAtr: self.params.errorRefStartAtr,
            initDisplayDate: null,
            employeeID: self.selectedEmployee == '' ? self.params.targetEmployee : self.selectedEmployee,
            objectDateRange: self.displayFormat == '0' ?
                (!_.isNil(self.dateRanger) ? { startDate: self.$dt.fromString(self.dateRanger.startDate), endDate: self.$dt.fromString(self.dateRanger.endDate) } : null) :
                (!_.isNil(self.selectedDate) ? { startDate: self.selectedDate, endDate: self.selectedDate } : null),
            lstEmployee: [],
            initClock: self.params.initClock,
            displayFormat: self.displayFormat,
            displayDateRange: null,
            transitionDesScreen: self.params.transitionDesScreen,
            closureId: self.params.closureID,
            formatCodes: selectedCode
        };

        self.$http.post('at', servicePath.initMOB, param).then(async (result: { data: any }) => {
            let dataInit = result.data;
            if (dataInit.lstEmployee == undefined || dataInit.lstEmployee.length == 0 || dataInit.errorInfomation != 0) {
                let messageId = 'Msg_1342';
                if (dataInit.errorInfomation == DCErrorInfomation.APPROVAL_NOT_EMP) {
                    messageId = 'Msg_916';
                } else if (dataInit.errorInfomation == DCErrorInfomation.ITEM_HIDE_ALL) {
                    messageId = 'Msg_1452';
                } else if (dataInit.errorInfomation == DCErrorInfomation.NOT_EMP_IN_HIST) {
                    messageId = 'Msg_1543';
                }
                self.$modal.error({ messageId }).then(() => {
                    self.$mask('hide');
                    if (self.isFirstLoad) {
                        self.$goto('ccg008a');
                    } else {
                        if (self.displayFormat == '0') {
                            self.actualTimeSelectedCode = self.actualTimeSelectedCodeTemp;
                            self.yearMonth = self.yearMonthTemp;
                            self.selectedEmployee = self.selectedEmployeeTemp;
                        } else {
                            self.selectedDate = self.selectedDateTemp;
                        }
                    }
                });
            } else if (!_.isEmpty(dataInit.errors)) {
                for (let i = 0; i < dataInit.errors.length; i++) {
                    await new Promise((next) => {
                        let employeeCode = (_.find(dataInit.lstEmployee, (x) => x.id == dataInit.changeEmployeeIds[0])).code;
                        let employeeName = (_.find(dataInit.lstEmployee, (x) => x.id == dataInit.changeEmployeeIds[0])).businessName;
                        self.$modal.error(dataInit.errors[i].messageId == 'Msg_1403' ?
                            { messageId: 'Msg_1403', messageParams: [employeeCode + ' ' + employeeName] } :
                            { messageId: dataInit.errors[i].messageId }).then(() => {
                                if (i == dataInit.errors.length - 1) {
                                    self.$mask('hide');
                                    if (self.displayFormat == '0') {
                                        self.actualTimeSelectedCode = self.actualTimeSelectedCodeTemp;
                                        self.yearMonth = self.yearMonthTemp;
                                        self.selectedEmployee = self.selectedEmployeeTemp;
                                    } else {
                                        self.selectedDate = self.selectedDateTemp;
                                    }
                                }
                                next();
                            });
                    });
                }
            } else {
                await new Promise((next) => {
                    self.processMapData(result.data);
                    next();
                });
                let dateR: any = self.displayFormat == '0' ?
                    (!_.isNil(self.dateRanger) ? { startDate: self.$dt.fromString(self.dateRanger.startDate), endDate: self.$dt.fromString(self.dateRanger.endDate) } : null) :
                    { startDate: self.selectedDate, endDate: self.selectedDate };
                storage.session.setItem('dailyCorrectionState', {
                    screenMode: self.params.screenMode,
                    displayFormat: self.displayFormat,
                    selectedEmployee: self.selectedEmployee,
                    lstEmployee: self.lstEmployee,
                    dateRange: dateR,
                    cellDataLst: self.displayDataLst,
                    headerLst: self.displayHeaderLst,
                    timePeriodAllInfo: _.assign({}, self.timePeriodAllInfo, { closureId: ClosureId[self.timePeriodAllInfo.closureId] }),
                    autBussCode: self.autBussCode,
                    paramData: self.paramData,
                    dPCorrectionMenuDto: self.dPCorrectionMenuDto
                });
                
                //パラメータ「日別実績の修正の状態．表示形式」をチェックする
                //パラメータ「日別実績の修正の起動．エラー参照を起動する」をチェックする
                if (self.displayFormat == 0 && self.params.errorRefStartAtr && !_.isNil(dateR)) {//表示形式　＝　個人別 && エラー参照を起動する = TRUE
                    self.params.errorRefStartAtr = false;
                    //D：エラー参照（個人）を起動する
                    this.$modal('kdws03d', {
                        employeeID: self.selectedEmployee,
                        employeeName: (_.find(self.lstEmployee, (x) => x.id == self.selectedEmployee)).businessName,
                        startDate: dateR.startDate,
                        endDate: dateR.endDate
                    }).then((param: any) => {
                        if (param != undefined && param.openB) {
                            //open B
                            let rowData = null;
                            if (self.displayFormat == '0') {
                                rowData = _.find(self.displayDataLst, (x) => x.dateDetail == param.date);
                            } else {
                                rowData = _.find(self.displayDataLst, (x) => x.employeeId == param.employeeId);
                            }

                            self.$modal('kdws03b', {
                                'employeeID': param.employeeId,
                                'employeeName': param.employeeName,
                                'date': param.date,
                                'rowData': rowData,
                                'paramData': self.paramData
                            }).then((v: any) => {
                                if (v.reload) {
                                    this.startPage();
                                } else {
                                    self.$http.post('at', servicePath.resetCacheDomain).then((result: { data: any }) => { });
                                }
                            });
                        }
                    });
                }
                self.$mask('hide');
            }
        }).catch((res: any) => {
            if (res.messageId == 'Msg_672') {
                self.$modal.info({ messageId: res.messageId }).then(() => {
                    self.$mask('hide');
                });
            } else {
                if (res.messageId != undefined) {
                    self.$modal.error(res.messageId == 'Msg_1430' ? res.message : { messageId: res.messageId }).then(() => {
                        self.$mask('hide');
                        self.$goto('ccg008a');
                    });

                } else if ((res.messageId == undefined && res.errors.length > 0)) {
                    //nts.uk.ui.dialog.bundledErrors({ errors: res.errors }).then(function () {
                    //nts.uk.request.jumpToTopPage();
                    //});

                }
            }
        });
    }

    //サーバから取得データをUIに表示するように処理する
    public processMapData(data: any) {
        let self = this;
        // param B screen
        self.paramData = data;
        // combo box
        self.comboItemsCalc = data.lstControlDisplayItem.comboItemCalc;
        self.comboItemsReason = data.lstControlDisplayItem.comboItemReason;
        self.comboItemsDoWork = data.lstControlDisplayItem.comboItemDoWork;
        self.comboItemsCompact = data.lstControlDisplayItem.comboItemCalcCompact;
        self.comboTimeLimit = data.lstControlDisplayItem.comboTimeLimit;

        self.autBussCode = data.autBussCode;
        self.dPCorrectionMenuDto = data.dpcorrectionMenuDto;
        self.lstDataSourceLoad = self.formatDate(data.lstData);
        self.optionalHeader = data.lstControlDisplayItem.lstHeader;
        self.lstAttendanceItem = data.lstControlDisplayItem.lstAttendanceItem;
        self.cellStates = data.lstCellState;

        if (self.isFirstLoad) {
            self.timePeriodAllInfo = data.periodInfo;
            self.yearMonth = data.periodInfo.yearMonth;
            if (self.params.closureYM != 0) {
                self.yearMonth = self.params.closureYM;
            }
        }

        if (self.displayFormat == 1 && self.isFirstLoad) {
            self.selectedDate = this.$dt.fromString(self.timePeriodAllInfo.targetRange.startDate);
        }

        self.fixHeaders = data.lstFixedHeader;
        self.showPrincipal = data.showPrincipal;
        self.showSupervisor = data.showSupervisor;
        if (data.lstControlDisplayItem.lstHeader.length == 0) {
            self.hasLstHeader = false;
        }

        if (self.lstEmployee.length == 0) {
            self.lstEmployee = _.orderBy(data.lstEmployee, ['code'], ['asc']);
        }

        _.remove(self.displayDataLst);
        _.remove(self.displayHeaderLst);
        _.remove(self.displaySumLst);

        // create header data
        let headers = (_.filter(self.optionalHeader, (o) => o.hidden == false));
        headers.forEach((header: any) => {
            let setting = _.find(data.lstControlDisplayItem.columnSettings, (x) => x.columnKey == header.key);
            self.displayHeaderLst.push({
                key: header.key,
                headerText: header.headerText,
                color: header.color,
                constraint: header.constraint,
                typeFormat: setting.typeFormat
            });
        });

        // create body data
        self.lstDataSourceLoad.forEach((rowDataSrc: any) => {
            let rowData = [];
            headers.forEach((header: any) => {
                let setting = _.find(data.lstControlDisplayItem.columnSettings, (x) => x.columnKey == header.key);
                if (_.has(rowDataSrc, header.key)) {
                    rowData.push({
                        key: header.key,
                        value: rowDataSrc[header.key],
                        class: setting.typeFormat == 3 ? 'currency-symbol row-style text-truncate' : (_.includes([2, 5], setting.typeFormat) ? 'row-style' : ''),
                        displayvalue: this.formatDisplay(rowDataSrc[header.key], setting.typeFormat),
                    });
                } else {
                    let resultValue = this.getFromCombo(header.group[1].ntsControl, rowDataSrc[header.group[1].key]);
                    rowData.push({
                        key: header.key,
                        groupKey: header.group[1].key,
                        value: resultValue,
                        groupKey0: header.group[0].key,
                        value0: rowDataSrc[header.group[0].key],
                        class: '',
                        displayvalue: resultValue,
                    });
                }
            });

            if (self.displayFormat == '0') {
                self.displayDataLst.push({
                    rowData,
                    date: rowDataSrc.date,
                    dateDetail: rowDataSrc.dateDetail,
                    id: rowDataSrc.id,
                    ERAL: rowDataSrc.error,
                    state: rowDataSrc.state,
                    sign: rowDataSrc.sign
                });
            } else {
                self.displayDataLst.push({
                    rowData,
                    employeeName: rowDataSrc.employeeName,
                    employeeId: rowDataSrc.employeeId,
                    employmentCode: rowDataSrc.employmentCode,
                    id: rowDataSrc.id,
                    ERAL: rowDataSrc.error,
                    state: rowDataSrc.state,
                    sign: rowDataSrc.sign
                });
            }
        });

        // set cell color
        self.displayDataLst.forEach((row: any) => {
            let states = _.filter(self.cellStates, (x) => x.rowId == row.id);
            row.rowData.forEach((cell: any) => {
                if (!_.isNil(_.find(states, (x) => x.columnKey == cell.key || x.columnKey == cell.groupKey))) {
                    let classArray = _.find(states, (x) => x.columnKey == cell.key || x.columnKey == cell.groupKey).state;
                    _.forEach(classArray, (x) => cell.class = cell.class + ' ' + x);
                }
            });
            if (!_.isNil(_.find(states, (x) => x.columnKey == 'date'))) {
                row.dateColor = '';
                let classArray = _.find(states, (x) => x.columnKey == 'date').state;
                _.forEach(classArray, (x) => row.dateColor = row.dateColor + ' ' + x);
            }
            if (!_.isNil(_.find(states, (x) => x.columnKey == 'sign'))) {
                let classArray = _.find(states, (x) => x.columnKey == 'sign').state;
                row.confirmDisable = _.includes(classArray, 'mgrid-disable') ? true : false;
            } else {
                row.confirmDisable = false;
            }
        });

        // fill blank row when no data
        if (self.displayFormat == 0) {
            self.displayDataLstEx = self.displayDataLst.slice();
            if (self.lstDataSourceLoad.length < this.rownum) {
                for (let i = 1; i <= (this.rownum - self.lstDataSourceLoad.length); i++) {
                    let rowData = [];
                    headers.forEach((header: any) => {
                        rowData.push({ key: header.key, value: '', class: '' });
                    });
                    self.displayDataLstEx.push({ rowData, date: '', id: '' });
                }
            }
        } else {
            if (self.displayDataLst.length <= this.rownum) {
                if (self.displayDataLst.length == 0) {
                    self.itemStart = 0;
                    self.itemEnd = 0;
                    self.previousState = 'button-deactive';
                    self.nextState = 'button-deactive';
                } else {
                    self.itemStart = 1;
                    self.itemEnd = self.displayDataLst.length;
                    self.previousState = 'button-deactive';
                    self.nextState = 'button-deactive';
                }
                self.displayDataLstEx = self.displayDataLst.slice();
                for (let i = 1; i <= (this.rownum - self.displayDataLst.length); i++) {
                    let rowData = [];
                    headers.forEach((header: any) => {
                        rowData.push({ key: header.key, value: '', class: '' });
                    });
                    self.displayDataLstEx.push({ rowData, employeeName: '', id: '' });
                }
            } else if (this.rownum < self.displayDataLst.length && self.displayDataLst.length < 20) {
                self.displayDataLstEx = _.slice(self.displayDataLst, 0, 20);
                self.itemStart = 1;
                self.itemEnd = self.displayDataLst.length;
                self.previousState = 'button-deactive';
                self.nextState = 'button-deactive';
            } else {
                self.displayDataLstEx = _.slice(self.displayDataLst, 0, 20);
                self.itemStart = 1;
                self.itemEnd = 20;
                self.previousState = 'button-deactive';
                self.nextState = 'button-active';
            }
        }

        // create sum row
        let sumTimeKey = _.map((_.filter(data.lstControlDisplayItem.columnSettings, (o) => o.typeFormat == 5)), (x) => x.columnKey);
        let sumNumKey = _.map((_.filter(data.lstControlDisplayItem.columnSettings, (o) => o.typeFormat == 2)), (x) => x.columnKey);
        let sumMoneyKey = _.map((_.filter(data.lstControlDisplayItem.columnSettings, (o) => o.typeFormat == 3)), (x) => x.columnKey);
        headers.forEach((header: any) => {
            if (_.includes(sumTimeKey, header.key)) {
                self.displaySumLst[header.key] = '0:00';
            } else if (_.includes(sumNumKey, header.key) || _.includes(sumMoneyKey, header.key)) {
                self.displaySumLst[header.key] = 0;
            } else {
                self.displaySumLst[header.key] = '';
            }

        });
        self.displayDataLst.forEach((row: any) => {
            row.rowData.forEach((cell: any) => {
                if (!_.isNil(cell.value) && '' != cell.value) {
                    if (_.includes(sumTimeKey, cell.key)) {
                        self.displaySumLst[cell.key] = this.$dt.timedr((new TimeDuration(self.displaySumLst[cell.key])).toNumber() + (new TimeDuration(cell.value)).toNumber());        
                    } else if (_.includes(sumNumKey, cell.key) || _.includes(sumMoneyKey, cell.key)) {
                        self.displaySumLst[cell.key] = self.displaySumLst[cell.key] + Number(cell.value);
                    }
                }
            });
        });

        headers.forEach((header: any) => {
            if (_.includes(sumTimeKey, header.key)) {    
                self.displaySumLst[header.key] = this.$dt.timedr(self.displaySumLst[header.key]).substring(0, 1) != '0' ? this.$dt.timedr(self.displaySumLst[header.key]) : this.$dt.timedr(self.displaySumLst[header.key]).substring(1, 5);
            } else if (_.includes(sumNumKey, header.key)) {
                self.displaySumLst[header.key] = this.formatDisplay(self.displaySumLst[header.key].toString(), '2');
            } else if (_.includes(sumMoneyKey, header.key)) {
                self.displaySumLst[header.key] = [this.formatDisplay(self.displaySumLst[header.key].toString(), '3'), 'currency-symbol'];
            }
        });

        self.resetTable++;
    }

    //各項目のデータを取得する
    public formatDate(lstData: any) {
        let data = lstData.map((data) => {
            let object = {
                id: '_' + data.id,
                state: data.state,
                error: data.error,
                date: this.$dt(data.date, 'Do(dd)'),
                sign: data.sign,
                approval: data.approval,
                employeeId: data.employeeId,
                employeeCode: data.employeeCode,
                employeeName: data.employeeName,
                workplaceId: data.workplaceId,
                employmentCode: data.employmentCode,
                dateDetail: this.$dt(data.date, 'YYYY/MM/DD'),
                typeGroup: data.typeGroup
            };
            _.each(data.cellDatas, function (item) {
                object[item.columnKey] = item.value;
            });

            return object;
        });

        return data;
    }

    //修正画面を開く
    public openEdit(id: any) {
        if (id == '') {
            return;
        }
        let self = this;
        let paramData = self.paramData;
        let rowData = _.find(self.displayDataLst, (x) => x.id == id);
        let employeeID = '';
        let employeeName = '';
        let date = new Date();
        let displayformat = 0;
        if (self.displayFormat == 0) {
            employeeID = self.selectedEmployee;
            employeeName = _.find(self.lstEmployee, (emp) => emp.id == employeeID).businessName;
            date = new Date(rowData.dateDetail);
        } else {
            employeeID = rowData.employeeId;
            employeeName = rowData.employeeName;
            date = self.selectedDate;
            displayformat = 1;
        }

        self.$modal('kdws03b', {
            'employeeID': employeeID,
            'employeeName': employeeName,
            'date': date,
            'rowData': rowData,
            'paramData': paramData,
            'displayformat': displayformat
        }).then((v: any) => {
            if (v.reload) {
                this.startPage();
            } else {
                self.$http.post('at', servicePath.resetCacheDomain).then((result: { data: any }) => { });
            }
        });
    }

    //メニュー画面を開く
    public openMenu() {
        let self = this;
        let authory: any = _.find(self.paramData.authorityDto, (x) => x.functionNo == 8 ); 
        if (self.displayFormat == '0') {//個人別モード
            self.$modal('kdws03amenu',
                {
                    displayFormat: this.displayFormat,
                    allConfirmButtonDis: this.dPCorrectionMenuDto.allConfirmButtonDis,
                    errorReferButtonDis: this.dPCorrectionMenuDto.errorReferButtonDis,
                    restReferButtonDis: this.dPCorrectionMenuDto.restReferButtonDis,
                    monthActualReferButtonDis: this.dPCorrectionMenuDto.monthActualReferButtonDis,
                    timeExcessReferButtonDis: this.dPCorrectionMenuDto.timeExcessReferButtonDis,
                    selfConfirm: this.paramData.showPrincipal,
                    closureDate: self.dateRanger.startDate,
                    functionNoView: authory.availability,
                    authoryView: self.paramData.disItem.settingUnit,
                }).then((param: any) => {
                    if (param != undefined && param.openB) {
                        //open B
                        let rowData = null;
                        if (self.displayFormat == '0') {
                            rowData = _.find(self.displayDataLst, (x) => x.dateDetail == param.date);
                        } else {
                            rowData = _.find(self.displayDataLst, (x) => x.employeeId == param.employeeId);
                        }

                        self.$modal('kdws03b', {
                            'employeeID': param.employeeId,
                            'employeeName': param.employeeName,
                            'date': param.date,
                            'rowData': rowData,
                            'paramData': self.paramData
                        });
                    }

                    // close dialog I
                    if (!_.isNil(param)) {
                        self.formatCode = param;
                        this.startPage();
                    }

                    if (!_.isNil(param) && param.reload) {
                        this.startPage();
                    }

                });
        } else {//日付別モード
            self.$modal('kdws03c').then((paramOpenB: any) => {
                if (paramOpenB != undefined && paramOpenB.openB) {
                    //open B
                    let rowData = null;
                    if (self.displayFormat == '0') {
                        rowData = _.find(self.displayDataLst, (x) => x.dateDetail == paramOpenB.date);
                    } else {
                        rowData = _.find(self.displayDataLst, (x) => x.employeeId == paramOpenB.employeeId);
                    }

                    self.$modal('kdws03b', {
                        'employeeID': paramOpenB.employeeId,
                        'employeeName': paramOpenB.employeeName,
                        'date': paramOpenB.date,
                        'rowData': rowData,
                        'paramData': self.paramData
                    });
                }
            });
        }
    }

    //次ページを押下処理
    public nextPage() {
        if (this.nextState == 'button-deactive') {
            return;
        }
        this.itemStart = this.itemStart + 20;
        if (this.displayDataLst.length - this.itemEnd > 20) {
            this.itemEnd = this.itemEnd + 20;
            this.nextState = 'button-active';
            this.displayDataLstEx = _.slice(this.displayDataLst, this.itemStart - 1, this.itemStart + 19);
        } else {
            this.itemEnd = this.displayDataLst.length;
            this.nextState = 'button-deactive';
            this.displayDataLstEx = _.slice(this.displayDataLst, this.itemStart - 1);
            if (this.itemEnd - this.itemStart < this.rownum) {
                for (let i = 1; i <= (this.rownum - 1 - (this.itemEnd - this.itemStart)); i++) {
                    let rowData = [];
                    this.displayHeaderLst.forEach((header: any) => {
                        rowData.push({ key: header.key, value: '' });
                    });
                    this.displayDataLstEx.push({ rowData, employeeName: '', id: '' });
                }
            }
        }
        this.previousState = 'button-active';
        this.resetTable++;
    }

    //前ページを押下処理
    public previousPage() {
        if (this.previousState == 'button-deactive') {
            return;
        }

        this.itemStart = this.itemStart - 20;
        this.itemEnd = this.itemStart + 19;
        if (this.itemStart == 1) {
            this.previousState = 'button-deactive';
        }
        this.nextState = 'button-active';
        this.displayDataLstEx = _.slice(this.displayDataLst, this.itemStart - 1, this.itemStart + 19);
        this.resetTable++;
    }

    //文字数の数え処理
    public countHalf(text: string) {
        let count = 0;
        for (let i = 0; i < text.length; i++) {
            let c = text.charCodeAt(i);

            // 0x20 ～ 0x80: 半角記号と半角英数字
            // 0xff61 ～ 0xff9f: 半角カタカナ
            if ((0x20 <= c && c <= 0x7e) || (0xff61 <= c && c <= 0xff9f)) {
                count += 1;
            } else {
                count += 2;
            }
        }

        return count;
    }

    //ComboBoxから項目値を取得する
    public getFromCombo(ntsControl: string, code: string) {
        switch (ntsControl) {
            case 'ComboboxCalc': {
                return (_.find(this.comboItemsCalc, (x) => x.code == code)).name;
            }
            case 'ComboboxReason': {
                return (_.find(this.comboItemsReason, (x) => x.code == code)).name;
            }
            case 'ComboboxDoWork': {
                return (_.find(this.comboItemsDoWork, (x) => x.code == code)).name;
            }
            case 'ComboItemsCompact': {
                return (_.find(this.comboItemsCompact, (x) => x.code == code)).name;
            }
            case 'ComboboxTimeLimit': {
                return (_.find(this.comboTimeLimit, (x) => x.code == code)).name;
            }
            default: {
                return code;
            }
        }
    }

    //フォーマット
    public formatDisplay(value: string, setting: any) {
        let chkArray = ['00:00', '0:00', '0.0', '0', ''];
        if (_.includes(chkArray, value)) {
            return '';
        } else {
            if (setting == '6') {
                return new TimeWithDay(value);
            } else if (setting == '3') {
                return Number(value) == 0 ? '' : Number(Number(value).toFixed(0)).toLocaleString('en-US');
            } else if (setting == '2') {
                return Number(value) == 0 ? '' : Number(value).toLocaleString('en-US');
            } else {
                return value;
            }
        }
    }

    //期間取得
    public initActualTime(value: any, selectedEmployee: string) {
        let self = this;
        self.$http.post('at', servicePath.genDate, { yearMonth: value, empTarget: selectedEmployee }).then((result: { data: any }) => {
            let data = result.data;
            if (_.isNil(data.lstRange)) {
                this.yearMonth = this.yearMonthTemp;

                return;
            }
            self.timePeriodAllInfo = data;
            _.remove(self.actualTimeOptionDisp);
            let selectItem = 0;
            if (data.lstRangeCls && data.lstRangeCls.length > 0) {
                for (let i = 0; i < data.lstRangeCls.length; i++) {
                    let startDate = data.lstRangeCls[i].startDate,
                        endDate = data.lstRangeCls[i].endDate;
                    if (data.targetRange.startDate == startDate) {
                        selectItem = data.lstRangeCls[i].closureId;
                    }
                    self.actualTimeOptionDisp.push({ code: data.lstRangeCls[i].closureId, name: (data.lstRangeCls[i].closureId) + ': ' + this.$dt(startDate, 'M/D') + '～' + this.$dt(endDate, 'M/D') });
                }
            }
            self.actualTimeSelectedCode = selectItem;
        });
    }

}

//サーバパス
const servicePath = {
    initMOB: 'screen/at/correctionofdailyperformance/initMOB',
    genDate: 'screen/at/correctionofdailyperformance/gendate',
    resetCacheDomain: 'screen/at/correctionofdailyperformance/resetCacheDomain',
};

//エラータイプ
enum DCErrorInfomation {
    NORMAL = 0,
    APPROVAL_NOT_EMP = 1,
    ITEM_HIDE_ALL = 2,
    NOT_EMP_IN_HIST = 3
}

//締めタイプ
enum ClosureId {
    RegularEmployee = 1,
    PartTimeJob = 2,
    ClosureThree = 3,
    ClosureFour = 4,
    ClosureFive = 5,
}

interface Params {
    // 画面モード
    screenMode: number;
    // 対象締め
    closureID: number;
    // 期間を変更する
    changePeriodAtr: boolean;
    // 表示形式
    displayFormat: number;
    // 対象社員
    targetEmployee: string;
    // 対象年月
    closureYM: number;
    // 対象年月日
    dateTarget: Date;
    // 打刻初期値
    initClock: any;
    // 遷移元の画面
    transitionDesScreen: any;
    // エラー参照を起動する
    errorRefStartAtr: boolean;

    formatCodes: Array<string>;
}