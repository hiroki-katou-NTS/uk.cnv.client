
import { Vue, _, moment } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { TimeDuration, TimeWithDay } from '@app/utils';
import { KdwS03DComponent } from 'views/kdw/s03/d';
import { Kdl001Component } from 'views/kdl/001';
import { KDL002Component } from 'views/kdl/002';
import { Cdl008AComponent } from 'views/cdl/s08/a';
import { CdlS03AComponent } from 'views/cdl/s03/a';
import { CdlS04AComponent } from 'views/cdl/s04/a';
import { CdlS02AComponent } from 'views/cdl/s02/a';
import { CdlS24AComponent } from 'views/cdl/s24/a';
import { Kdls12Component } from 'views/kdl/s12';

@component({
    name: 'kdws03b',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        screenData: {},
        fixedConstraint: {
            AttendanceTime: { constraint: 'AttendanceTime' },
            AttendanceTimeOfExistMinus: { constraint: 'AttendanceTimeOfExistMinus' },
            WorkTimes: { constraint: 'WorkTimes' },
            WorkTypeCode: { constraint: 'WorkTypeCode' },
            WorkTimeCode: { constraint: 'WorkTimeCode' },
            WorkLocationCD: { constraint: 'WorkLocationCD' },
            EmploymentCode: { constraint: 'EmploymentCode' },
            ClassificationCode: { constraint: 'ClassificationCode' },
            JobTitleCode: { constraint: 'JobTitleCode' },
            WorkplaceCode: { constraint: 'WorkplaceCode' },
            DivergenceReasonContent: { constraint: 'DivergenceReasonContent' },
            BreakTimeGoOutTimes: { constraint: 'BreakTimeGoOutTimes' },
            RecordRemarks: { constraint: 'RecordRemarks' },
            DiverdenceReasonCode: { constraint: 'DiverdenceReasonCode' },
            TimeWithDayAttr: { constraint: 'TimeWithDayAttr' },
            BusinessTypeCode: { constraint: 'BusinessTypeCode' },
            AnyItemAmount: { constraint: 'AnyItemAmount' },
            AnyAmountMonth: { constraint: 'AnyAmountMonth' },
            AnyItemTime: { constraint: 'AnyItemTime' },
            AnyTimeMonth: { constraint: 'AnyTimeMonth' },
            AnyItemTimes: { constraint: 'AnyItemTimes' },
            AnyTimesMonth: { constraint: 'AnyTimesMonth' },
            SuppNumValue: { constraint: 'SuppNumValue' }
        }
    },
    constraints: [
        'nts.uk.ctx.at.shared.dom.common.time.AttendanceTime',
        'nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus',
        'nts.uk.ctx.at.record.dom.daily.WorkTimes',
        'nts.uk.ctx.at.schedule.dom.shift.pattern.WorkTypeCode',
        'nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.WorkTimeCode',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD',
        'nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode',
        'nts.uk.ctx.at.schedule.dom.shift.basicworkregister.ClassificationCode',
        'nts.uk.ctx.bs.employee.dom.jobtitle.info.JobTitleCode',
        'nts.uk.shr.com.primitive.WorkplaceCode',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceReasonContent',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeGoOutTimes',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.remarks.RecordRemarks',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode',
        'nts.uk.shr.com.time.TimeWithDayAttr',
        'nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemAmount',
        'nts.uk.ctx.at.shared.dom.common.anyitem.AnyAmountMonth',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTime',
        'nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimeMonth',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTimes',
        'nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimesMonth',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode',
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppNumValue'
    ],
    components: {
        'kdws03d': KdwS03DComponent,
        'kdls01': Kdl001Component,
        'kdls02': KDL002Component,
        'cdls08a': Cdl008AComponent,
        'cdls03a': CdlS03AComponent,
        'cdls04a': CdlS04AComponent,
        'cdls02a': CdlS02AComponent,
        'cdls24a': CdlS24AComponent,
        'kdls12a': Kdls12Component
    },
})
export class KdwS03BComponent extends Vue {
    @Prop({
        default: () => ({
            employeeID: '',
            employeeName: '',
            date: new Date(),
            rowData: {},
            paramData: {},
            displayformat: 0
        })
    })
    public readonly params!: {
        employeeID: string,
        employeeName: string,
        date: Date,
        rowData: any,
        paramData: any,
        displayformat: number
    };
    public checked1s: Array<number> = [];
    public screenData: any = {};
    public screenData1: any = {};
    private masterData: any = {
        servicePlace: [],
        reason: [],
        workPlace: [],
        lstDoWork: [],
        lstCalc: [],
        lstCalcCompact: [],
        lstReasonGoOut: [],
        lstTimeLimit: []
    };
    private masterDialogParam: Array<number> = [];
    private masterItemIdParam: Array<number> = [];
    private oldData: any = [];
    private oldCheckBox: Array<number> = [];
    private listAutoCalc: any = [];
    private isReady = false;
    private workTypeNotFound = false;

    get lstAttendanceItem() {
        let self = this;

        return self.params.paramData.lstControlDisplayItem.lstAttendanceItem;
    }

    get contentType() {
        let self = this;

        return self.params.paramData.lstControlDisplayItem.lstHeader;
    }    

    get itemValues() {
        let self = this;

        return self.params.paramData.itemValues;
    }

    get itemType() {
        return ItemType;
    }

    get masterType() {
        return MasterType;
    }

    get canRegister() {
        let self = this;

        return _.find(self.params.paramData.authorityDto, (item) => item.functionNo == 1 && item.availability);
    }

    public created() {
        let self = this;
        self.params.date = moment(self.params.date).utc().toDate();
        if (self.params.rowData.sign) {
            self.checked1s.push(2);
        }
        self.oldCheckBox = self.toJS(self.checked1s);
        self.$mask('show');
        self.addCustomValid();
        self.oldData = self.toJS(self.screenData[0]);
        self.createMasterComboBox();
        self.getListTaskSup(self.masterItemIdParam);
        self.$http.post('at', API.masterDialogData, {
            types: self.masterDialogParam,
            date: moment(self.params.date).utc().toDate(),
            employeeID: self.params.employeeID,
            workTypeCD: self.screenData1.A28
        }).then(async (masterData: any) => {
            await new Promise((next) => {
                self.createMasterData(masterData.data);
                next();
            });           
            self.$mask('hide');
        }).catch((res: any) => {
            self.$mask('hide');
            self.$modal.error(res.messageId)
                .then(() => {
                    self.$close();
                });
        });
        self.screenDataWatch();
    }

    public getListTaskSup(keys: number[])  {
        let self = this;        
        self.$http.post('at', API.masterTaskSupOpt, {            
            itemIds: self.masterItemIdParam,
            date: moment(self.params.date).utc().toDate(),
        }).then((dataAll: any) => {
            _.forEach(keys, (key) => {
                self.masterData[key] = [];
                _.forEach(dataAll.data[key], (o) => {
                    self.masterData[key].push({ code: o.code, name: o.name });                    
                }); 
                self.masterData[key].push({ code: '', name: 'なし' });
            }); 
            _.forEach(self.screenData1, (value, key) => {
                let idKey = key.replace('A', '');
                if (!(self.getItemType(key) == ItemType.InputStringCode || self.getItemType(key) == ItemType.ButtonDialog)) {
                    return;
                }
                if (!_.find(self.masterData[idKey], (item) => item.code == value)) {
                    self.masterData[idKey].push({ code: value, name: 'マスタ未登録' });
                }
                  
                self.getRowComboBox(key);
            });            
            self.$mask('hide');
        }).catch((res: any) => {
            self.$mask('hide');
            self.$modal.error(res.messageId)
                .then(() => {
                    self.$close();
                });
        });
    }

    public beforeUpdate() {
        let self = this;
        if (self.validations.fixedConstraint) {
            self.addCustomConstraint();
            self.isReady = true;
        }
    }

    public getLockContent() {
        let self = this;
        let data: any = self.params.rowData.state;
        if (data != '') {
            let lock = data.split('|');
            let tempD = `<span>`;
            _.forEach(lock, (char) => {
                switch (char) {
                    case 'D':
                        tempD += self.$i18n('KDW003_66') + `<br/>`;
                        break;
                    case 'M':
                        tempD += self.$i18n('KDW003_66') + `<br/>`;
                        break;
                    case 'C':
                        tempD += self.$i18n('KDW003_67') + `<br/>`;
                        break;
                    case 'S':
                        tempD += self.$i18n('KDW003_113') + `<br/>`;
                        break;
                    case 'CM':
                        tempD += self.$i18n('KDW003_112') + `<br/>`;
                        break;
                    case 'AM':
                        tempD += self.$i18n('KDW003_68') + `<br/>`;
                        break;
                    case 'H':
                        tempD += self.$i18n('KDW003_70') + `<br/>`;
                        break;
                    case 'A':
                        tempD += self.$i18n('KDW003_69') + `<br/>`;
                        break;
                    default:
                        break;
                }
            });
            tempD += `</span>`;

            return tempD;
        }
    }

    public getItemLock(key: string) {
        let self = this;
        let lstCellState = _.filter(self.params.paramData.lstCellState, (item) => item.rowId == self.params.rowData.id);
        if (_.isEmpty(lstCellState)) {
            return false;
        }
        let itemHeader = _.find(self.contentType, (item) => item.key == key);
        if (_.isEmpty(itemHeader)) {
            return false;
        }
        let cellKey = '';
        if (_.isEmpty(itemHeader.group)) {
            cellKey = itemHeader.key;
        } else {
            cellKey = itemHeader.group[0].key;
        }
        let cellState = _.find(lstCellState, (item) => item.columnKey == cellKey);
        if (_.isUndefined(cellState)) {
            return false;
        }

        return _.find(cellState.state, (item) => item.includes('mgrid-disable'));
    }

    get isDisplayError() {
        let self = this;

        return !_.isEmpty(self.params.rowData.ERAL);
    }

    private createMasterComboBox() {
        let self = this;
        let lstControlDisplayItem: any = self.params.paramData.lstControlDisplayItem;
        _.forEach(lstControlDisplayItem.comboItemDoWork, (o) => {
            self.masterData.lstDoWork.push({ code: o.code, name: o.name });
        });
        _.forEach(lstControlDisplayItem.comboItemCalc, (o) => {
            self.masterData.lstCalc.push({ code: o.code, name: o.name });
        });
        _.forEach(lstControlDisplayItem.comboItemCalcCompact, (o) => {
            self.masterData.lstCalcCompact.push({ code: o.code, name: o.name });
        });
        _.forEach(lstControlDisplayItem.comboItemReason, (o) => {
            self.masterData.lstReasonGoOut.push({ code: o.code, name: o.name });
        });
        _.forEach(lstControlDisplayItem.comboTimeLimit, (o) => {
            self.masterData.lstTimeLimit.push({ code: o.code, name: o.name });
        });
    }

    private createMasterData(data: any) {
        let self = this;
        self.masterData[MasterType.KDLS01_WorkTime] = data[MasterType.KDLS01_WorkTime];
        self.masterData[MasterType.KDLS02_WorkType] = data[MasterType.KDLS02_WorkType];
        self.masterData[MasterType.CDLS08_WorkPlace] = data[MasterType.CDLS08_WorkPlace];
        self.masterData[MasterType.CDLS03_Classification] = data[MasterType.CDLS03_Classification];
        self.masterData[MasterType.CDLS04_Possition] = data[MasterType.CDLS04_Possition];
        self.masterData[MasterType.CDLS02_Employment] = data[MasterType.CDLS02_Employment];
        self.masterData[MasterType.CDLS24_BusinessType] = data[MasterType.CDLS24_BusinessType];
        self.masterData[MasterType.KDLS12_TaskSelection] = data[MasterType.KDLS12_TaskSelection];

        // tạo dữ liệu conbo box tạm thời cho các dialog chưa dc làm
        _.forEach(data[MasterType.KDLS10_ServicePlace], (o) => {
            self.masterData.servicePlace.push({ code: o.code, name: o.name });
        }); 
        if (!_.find(self.masterData.servicePlace, (item) => item.code == '')) {
            self.masterData.servicePlace.push({ code: '', name: 'なし' });
        }
        _.forEach(data[MasterType.KDLS32_Reason], (o) => {
            self.masterData.reason.push({ code: o.code, name: o.name });
        });

        if (!_.find(self.masterData.reason, (item) => item.code == '')) {
            self.masterData.reason.push({ code: '', name: 'なし' });
        }        
        _.forEach(self.screenData1, (value, key) => {            
            let attendanceItem = self.getAttendanceItem(key);
            if (!(self.getItemType(key) == ItemType.InputStringCode || self.getItemType(key) == ItemType.ButtonDialog)) {
                return;
            }
            let rowData = _.find(self.params.rowData.rowData, (rowData: RowData) => rowData.key == key);
            switch (attendanceItem.typeGroup) {
                case MasterType.KDLS10_ServicePlace:
                    rowData.comboLst = self.masterData.servicePlace;
                    if (!_.find(rowData.comboLst, (item) => item.code == value)) {
                        rowData.comboLst.push({ code: value, name: 'マスタ未登録' });
                    }
                    break;
                case MasterType.KDLS32_Reason:
                    rowData.comboLst = self.masterData.reason;
                    if (!_.find(rowData.comboLst, (item) => item.code == value)) {
                        rowData.comboLst.push({ code: value, name: 'マスタ未登録' });
                    }
                    break;

                default: break;
            }
            self.getRowComboBox(key);
        });
    }

    public isAvaiableDialog(key: string) {
        let self = this;

        return self.getItemMasterType(key) == MasterType.KDLS02_WorkType ||
                self.getItemMasterType(key) == MasterType.KDLS01_WorkTime || 
                self.getItemMasterType(key) == MasterType.CDLS08_WorkPlace || 
                self.getItemMasterType(key) == MasterType.CDLS03_Classification ||
                self.getItemMasterType(key) == MasterType.CDLS04_Possition ||
                self.getItemMasterType(key) == MasterType.CDLS02_Employment ||
                self.getItemMasterType(key) == MasterType.CDLS24_BusinessType ||
                self.getItemMasterType(key) == MasterType.KDLS12_TaskSelection;
    }

    public getRowComboBox(key: string) {
        let self = this;
        let idKey = key.replace('A', '');
        let masterType = self.getItemMasterType(key);
        switch (masterType) {
            case MasterType.DoWork:
                return self.masterData.lstDoWork;
            case MasterType.Calc:
                if (self.isSpecCalcLst(key)) {
                    return self.masterData.lstCalcCompact;
                } else {
                    return self.masterData.lstCalc;
                }   
            case MasterType.ReasonGoOut:
                return self.masterData.lstReasonGoOut;
            case MasterType.TimeLimit:
                return self.masterData.lstTimeLimit;
            case MasterType.KDL013_TaskSupOption:    
                return !_.isUndefined(self.masterData[idKey]) ? self.masterData[idKey] : null ;
            default:             
                return _.find(self.params.rowData.rowData, (rowData: RowData) => rowData.key == key).comboLst;
        }
    }

    private formatData(rowData: RowData) {
        let self = this;
        let attendanceItem = self.getAttendanceItem(rowData.key);
        switch (attendanceItem.attendanceAtr) {
            case ItemType.InputNumber:
                rowData.value = _.isEmpty(rowData.value) ? null : _.toNumber(rowData.value);
                break;
            case ItemType.InputNumericValue:
                    rowData.value = _.isEmpty(rowData.value) ? null : _.toNumber(rowData.value);
                    break;
            case ItemType.InputMoney:
                rowData.value = _.isEmpty(rowData.value) ? null : _.toNumber(rowData.value);
                break;
            case ItemType.Time:
                rowData.value = _.isEmpty(rowData.value) ? null : new TimeDuration(rowData.value).toNumber();
                break;
            case ItemType.TimeWithDay:
                rowData.value = _.isEmpty(rowData.value) ? null : new TimeWithDay(rowData.value).toNumber();
                break;
            default:
                break;
        }
    }

    public getCheckboxType(key: string) {
        let self = this;
        let contentType = _.find(self.contentType, (item: ItemHeader) => item.key == key);

        return contentType.ntsControl;
    }

    public getItemType(key: string) {
        let self = this;
        let attendanceItem = self.getAttendanceItem(key);

        return attendanceItem.attendanceAtr;
    }

    public getItemText(key: string) {
        let self = this;

        return _.find(self.contentType, (item: ItemHeader) => item.key == key).headerText;
    }

    public getItemMasterType(key: string) {
        let self = this;
        let attendanceItem = self.getAttendanceItem(key);

        return attendanceItem.typeGroup;
    }

    public isSpecCalcLst(key: string) {
        let self = this;
        let specLst = [628, 630, 631, 632];
        let attendanceItem = self.getAttendanceItem(key);

        return _.includes(specLst, attendanceItem.id);
    }

    public getIcon(key: string) {
        let self = this;
        let rowClass = _.find(self.params.rowData.rowData, (rowData: RowData) => rowData.key == key).class;
        if (rowClass.includes('mgrid-error')) {
            return 'fas fa-exclamation-circle align-bottom text-danger';
        }
        if (rowClass.includes('mgrid-alarm')) {
            return 'fas fa-exclamation-triangle align-bottom uk-text-yellow';
        }

        return '';
    }

    public getBackGroundColor(key: string) {
        let self = this,
            rowData = _.get(self, 'params.rowData.rowData') || [];

        return _.get(_.find(rowData, (r: any) => r.key === key), 'class');
    }

    public getItemDialogName(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (rowData: RowData) => rowData.key == key);
        let attendanceItem = self.getAttendanceItem(key);
        let item: any = {};
        item = _.find(self.masterData[attendanceItem.typeGroup], (o) => o.code == rowData.value0);
        if (item) {
            return item.name;
        } else {
            return rowData.value;
        }
    }

    private addCustomValid() {
        let self = this;
        let screenDataValid: any = {};
        _.forEach(self.params.rowData.rowData, (rowData: any, index) => {
            self.formatData(rowData);
            self.addMasterDialogParam(rowData);
            let attendanceItem = self.getAttendanceItem(rowData.key);
            let contraint = _.find(self.contentType, (item: ItemHeader) => item.key == rowData.key).constraint;            
            switch (attendanceItem.attendanceAtr) {
                case ItemType.InputStringCode:
                    self.$set(self.screenData1, rowData.key, rowData.value0);
                    break;
                case ItemType.ButtonDialog:
                    self.$set(self.screenData1, rowData.key, rowData.value0);
                    break;
                case ItemType.InputNumber:
                    if (rowData.displayvalue) {
                        self.$set(self.screenData1, rowData.key, 1);
                    } else if (!rowData.displayvalue) {   
                        self.$set(self.screenData1, rowData.key, 0);
                    } else {
                        self.$set(self.screenData1, rowData.key, rowData.value);
                    }
                    
                    if (!_.isNull(contraint)) {
                        if (contraint.cdisplayType == 'Primitive') {
                            screenDataValid[rowData.key] = {
                                loop: true,
                                required: contraint.required
                            };
                        } else {
                            screenDataValid[rowData.key] = {
                                loop: true,
                                required: contraint.required,
                                min: _.toNumber(contraint.min),
                                max: _.toNumber(contraint.max)
                            };
                        }
                    }                        
                    break;
                
                case ItemType.InputMoney:
                    self.$set(self.screenData1, rowData.key, rowData.value);
                    if (contraint.cdisplayType == 'Primitive') {
                        screenDataValid[rowData.key] = {
                            loop: true,
                            required: contraint.required
                        };
                    } else {
                        screenDataValid[rowData.key] = {
                            loop: true,
                            required: contraint.required,
                            min: _.toNumber(contraint.min),
                            max: _.toNumber(contraint.max),
                            valueType: 'Integer'
                        };
                    }
                    break;
                case ItemType.ComboBox:
                    self.$set(self.screenData1, rowData.key, rowData.value0);
                    break;
                case ItemType.Time:
                    self.$set(self.screenData1, rowData.key, rowData.value);
                    if (contraint.cdisplayType == 'Primitive') {
                        screenDataValid[rowData.key] = {
                            loop: true,
                            required: contraint.required
                        };
                    } else {
                        screenDataValid[rowData.key] = {
                            loop: true,
                            required: contraint.required,
                            min: new TimeDuration(contraint.min).toNumber(),
                            max: new TimeDuration(contraint.max).toNumber(),
                            valueType: 'Duration'
                        };
                    }
                    break;
                case ItemType.InputStringChar:
                    self.$set(self.screenData1, rowData.key, rowData.value);
                    if (contraint.cdisplayType == 'Primitive') {
                        screenDataValid[rowData.key] = {
                            loop: true,
                            required: contraint.required
                        };
                    } else {
                        screenDataValid[rowData.key] = {
                            loop: true,
                            required: contraint.required
                        };
                    }
                    break;

                case  ItemType.InputNumericValue:
                    if (rowData.value) {
                        self.$set(self.screenData1, rowData.key, rowData.value);
                    } else if (!rowData.value) {   
                        self.$set(self.screenData1, rowData.key, 0);
                    } else {
                        self.$set(self.screenData1, rowData.key, rowData.value);
                    }
                    
                    if (!_.isNull(contraint)) {
                        if (contraint.cdisplayType == 'Primitive') {
                            screenDataValid[rowData.key] = {
                                loop: true,
                                required: contraint.required
                            };
                        } else {
                            screenDataValid[rowData.key] = {
                                loop: true,
                                required: contraint.required,
                                min: _.toNumber(contraint.min),
                                max: _.toNumber(contraint.max)
                            };
                        }
                    }                        
                    break;
                default:
                    self.$set(self.screenData1, rowData.key, rowData.value);
                    break;
            }
        });
        self.screenData = [self.screenData1];
        self.$updateValidator('screenData', screenDataValid);
        // self.$updateValidator(`screenData.${index}`, newObj);
    }

    public addCustomConstraint() {
        let self = this;
        _.forEach(self.params.rowData.rowData, (rowData: RowData, index) => {
            let attendanceItem = self.getAttendanceItem(rowData.key);
            let contraint = _.find(self.contentType, (item: ItemHeader) => item.key == rowData.key).constraint;
            let constraintObj: any = {};
            switch (attendanceItem.attendanceAtr) {
                case ItemType.InputNumber:
                    if ( !_.isNull(contraint) && contraint.cdisplayType == 'Primitive') {
                        constraintObj = _.get(self.validations.fixedConstraint, PrimitiveAll['No' + attendanceItem.primitive]);
                        constraintObj.loop = true;
                        constraintObj.required = contraint.required;
                        self.$updateValidator(`screenData.${rowData.key}`, constraintObj);
                    }
                    break;
                case ItemType.InputMoney:
                    if (contraint.cdisplayType == 'Primitive') {
                        constraintObj = _.get(self.validations.fixedConstraint, PrimitiveAll['No' + attendanceItem.primitive]);
                        constraintObj.loop = true;
                        constraintObj.required = contraint.required;
                        self.$updateValidator(`screenData.${rowData.key}`, constraintObj);
                    }
                    break;
                case ItemType.Time:
                    if (contraint.cdisplayType == 'Primitive') {
                        constraintObj = _.get(self.validations.fixedConstraint, PrimitiveAll['No' + attendanceItem.primitive]);
                        constraintObj.loop = true;
                        constraintObj.required = contraint.required;
                        self.$updateValidator(`screenData.${rowData.key}`, constraintObj);
                    }
                    break;
                case ItemType.InputStringChar:
                    if (contraint.cdisplayType == 'Primitive') {
                        constraintObj = _.get(self.validations.fixedConstraint, PrimitiveAll['No' + attendanceItem.primitive]);
                        constraintObj.loop = true;
                        constraintObj.required = contraint.required;
                        self.$updateValidator(`screenData.${rowData.key}`, constraintObj);
                        // self.$updateValidator( {screenData : { [rowData.key] : constraintObj}});
                    }
                    break;
                case ItemType.InputNumericValue:
                    if ( !_.isNull(contraint) && contraint.cdisplayType == 'Primitive') {
                        constraintObj = _.get(self.validations.fixedConstraint, PrimitiveAll['No' + attendanceItem.primitive]);
                        constraintObj.loop = true;
                        constraintObj.required = contraint.required;
                        self.$updateValidator(`screenData.${rowData.key}`, constraintObj);
                    }
                    break;
                default:
                    break;
            }
        });
        delete self.validations.fixedConstraint;
    }

    private addMasterDialogParam(rowData: RowData) {
        let self = this;
        let idKey = rowData.key.replace('A', '');
        let attendanceItem = _.find(self.lstAttendanceItem, (item: any) => item.id == idKey);
        switch (attendanceItem.attendanceAtr) {
            case ItemType.InputStringCode:
                if (!_.includes(self.masterDialogParam, attendanceItem.typeGroup)) {
                    self.masterDialogParam.push(attendanceItem.typeGroup);                    
                }

                if (attendanceItem.typeGroup == 19) {
                    self.masterItemIdParam.push(attendanceItem.id);
                }                        
                break;
            case ItemType.ButtonDialog:
                if (!_.includes(self.masterDialogParam, attendanceItem.typeGroup)) {
                    self.masterDialogParam.push(attendanceItem.typeGroup);
                }
                break;
            case ItemType.ComboBox:
                if (!_.includes(self.masterDialogParam, attendanceItem.typeGroup)) {
                    self.masterDialogParam.push(attendanceItem.typeGroup);
                }
                break;
            default: break;
        }
    }

    public openDScreen(key?: string) {
        let self = this;
        let idKey: any;
        if (!_.isEmpty(key)) {
            idKey = key.replace('A', '');
        } 
        self.$modal('kdws03d', {
            employeeID: self.params.employeeID,
            employeeName: self.params.employeeName,
            startDate: self.params.date,
            endDate: self.params.date,
            attendanceItemID: idKey
        });
    }

    public openDialog(key: string) {
        let self = this;
        let type: MasterType = self.getItemMasterType(key);
        switch (type) {
            case MasterType.KDLS02_WorkType: self.openKDLS02(key); break;
            case MasterType.KDLS01_WorkTime: self.openKDLS01(key); break;
            case MasterType.CDLS08_WorkPlace: self.openCDLS08(key); break;
            case MasterType.CDLS03_Classification: self.openCDLS03(key); break;
            case MasterType.CDLS04_Possition: self.openCDLS04(key); break;
            case MasterType.CDLS02_Employment: self.openCDLS02(key); break;
            case MasterType.CDLS24_BusinessType: self.openCDLS24(key); break;
            case MasterType.KDLS12_TaskSelection: self.openKDLS12(key); break;
            default: break;
        }
    }

    private openKDLS02(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        let workTypeCDLst = _.map(self.masterData[MasterType.KDLS02_WorkType], (o) => o.code);
        self.$modal(
            'kdls02',
            {
                seledtedWkTypeCDs: workTypeCDLst,
                selectedWorkTypeCD: selectedCD,
                isAddNone: false,
                seledtedWkTimeCDs: null,
                selectedWorkTimeCD: null,
                isSelectWorkTime: false
            }
        ).then((data: any) => {
            if (data) {
                self.screenData[0][key] = data.selectedWorkType.workTypeCode;
                rowData.value0 = data.selectedWorkType.workTypeCode;
                let result = _.find(self.masterData[MasterType.KDLS02_WorkType], (o) => o.code == rowData.value0);
                if (result) {
                    rowData.value = result.name;
                } else {
                    rowData.value = '選択なし';
                }

            }
        });
    }

    private openKDLS01(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        let workTimeCDLst = _.map(self.masterData[MasterType.KDLS01_WorkTime], (o) => o.code);
        self.$modal(
            'kdls01',
            {
                isAddNone: true,
                seledtedWkTimeCDs: workTimeCDLst,
                selectedWorkTimeCD: selectedCD,
                isSelectWorkTime: false
            }
        ).then((data: any) => {
            if (data) {
                self.screenData[0][key] = data.selectedWorkTime.code;
                rowData.value0 = data.selectedWorkTime.code;
                let result = _.find(self.masterData[MasterType.KDLS01_WorkTime], (o) => o.code == rowData.value0);
                if (result) {
                    rowData.value = result.name;
                } else {
                    rowData.value = '選択なし';
                }

            }
        });
    }

    private openKDLS12(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        let keyList = _.keys(WORK_FRAME_MAP);
        let dateParam15: any = null, taskFrameNo: any = null;
       
        dateParam15 =  moment(self.params.date).format('YYYY/MM/DD');       
        for (let itemTaskKey in keyList) {
            if (_.includes(_.get(WORK_FRAME_MAP, keyList[itemTaskKey]), parseInt(self.getAttendanceItem(key).id))) {
                taskFrameNo = parseInt(keyList[itemTaskKey]);
                break;	
            }
        }
        self.$modal(
            'kdls12a',
            {
                isAddNone: true,
                isMultiple: false,
                showExpireDate: false,
                baseDate: dateParam15,
                taskFrameNo,
                selectionCodeList: [selectedCD],
                sid: self.params.employeeID,
                taskCode: selectedCD == '' ? null : selectedCD
            }
        ).then((data: any) => {
            if (data) {
                self.screenData[0][key] = data.selectedTask.code;
                rowData.value0 = data.selectedTask.code;
                rowData.value = data.selectedTask.taskName;
            }
        });
    }


    private openCDLS08(key: string) {
        let self = this;
        let id = '';
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedItem: any = _.find(self.masterData[MasterType.CDLS08_WorkPlace], (o) => o.code == self.screenData[0][key]);
        if (!_.isUndefined(selectedItem)) {
            id = selectedItem.id;
        }
        self.$modal(
            'cdls08a',
            {
                workPlaceType: 0,
                startMode: false,
                baseDate: self.params.date,
                systemType: 2,
                referenceRangeNarrow: true,
                selectedItem: [id],
                isSelectionRequired: true
            },
            {
                title: 'CDLS08_1'
            }
        ).then((data: any) => {
            if (data) {
                self.screenData[0][key] = data.code;
                rowData.value0 = data.code;
                rowData.value = data.name;
            }
        });
    }
    
    private openCDLS03(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        self.$modal(
            'cdls03a',
            {
                isShowNoSelectRow: false,
                selectedCode: selectedCD
            }
        ).then((data: any) => {
            if (data) {
                self.screenData[0][key] = data.code;
                rowData.value0 = data.code;
                rowData.value = data.name;
            }
        });
    }
    
    private openCDLS04(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        self.$modal(
            'cdls04a',
            {
                isShowNoSelectRow: false,
                selectedCode: selectedCD,
                date: self.params.date
            }
        ).then((data: any) => {
            if (data) {
                self.screenData[0][key] = data.code;
                rowData.value0 = data.code;
                rowData.value = data.name;
            }
        });
    }
    
    private openCDLS02(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        self.$modal(
            'cdls02a',
            {
                isDisplayClosureSelection: true,
                isShowNoSelectRow: false,
                selectedCode: selectedCD
            }
        ).then((data: any) => {
            if (data == 'back') {
                return;
            }
            if (data) {
                self.screenData[0][key] = data.code;
                rowData.value0 = data.code;
                rowData.value = data.name;
            }
        });
    }
    
    private openCDLS24(key: string) {
        let self = this;
        let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
        let selectedCD = self.screenData[0][key];
        self.$modal(
            'cdls24a',
            {
                selectedCode: selectedCD
            }
        ).then((data: any) => {
            if (data == 'back') {
                return;
            }
            if (data) {
                self.screenData[0][key] = data.code;
                rowData.value0 = data.code;
                rowData.value = data.name;
            }
        });
    }
    
    private isChangeDataRegister() {
        let self = this;
        let isChangeCheckBox = JSON.stringify(self.oldCheckBox).localeCompare(JSON.stringify(self.checked1s)) != 0;
        let registerParam = self.createRegisterParam();

        return isChangeCheckBox || !_.isEmpty(registerParam.itemValues);
    }

    public register() {
        let self = this;
        if (self.workTypeNotFound) {
            self.$modal.error('Msg_1293');
            
            return;
        }
        let registerParam = self.createRegisterParam();
        if (!self.isChangeDataRegister()) {
            self.$modal.error('Msg_1569');
            
            return;
        }
        self.$mask('show');
        self.$http.post('at', API.register, registerParam)
            .then((data: any) => {
                self.$mask('hide');
                let dataAfter = data.data;
                if (dataAfter.optimisticLock === true) {
                    self.$modal.error('Msg_1528').then(() => {
                        self.$close({ reload: true });
                    });

                    return;
                }
                if (dataAfter.messageAlert == 'Msg_15') {
                    self.$modal.info('Msg_15').then(() => {
                        self.$close({ reload: true });
                    });

                    return;
                }
                let errorOutput = [];
                if (dataAfter.errorMap[0] != undefined) {
                    _.forEach(dataAfter.errorMap[0], (value) => {
                        errorOutput.push(self.$i18n('Msg_996'));
                    });
                }
                if (dataAfter.errorMap[1] != undefined) {
                    _.forEach(dataAfter.errorMap[1], (value) => {
                        let item = _.find(self.contentType, (data) => {
                            return String(data.key) === 'A' + value.itemId;
                        });
                        let itemName = (item == undefined) ? '' : item.headerText;
                        let itemOtherInGroup = CHECK_INPUT[value.itemId + ''];
                        let itemGroup = self.params.paramData.lstControlDisplayItem.itemInputName[Number(itemOtherInGroup)];
                        let nameGroup: any = (itemGroup == undefined) ? '' : itemGroup;
                        let message = self.$i18n(value.message, [itemName, nameGroup]);
                        errorOutput.push(message);
                    });
                }
                if (dataAfter.errorMap[3] != undefined) {
                    _.forEach(dataAfter.errorMap[3], (value) => {
                        errorOutput.push(self.$i18n(value.layoutCode));
                    });
                }
                if (dataAfter.errorMap[4] != undefined) {
                    _.forEach(dataAfter.errorMap[4], (value) => {
                        let item = _.find(self.contentType, (data) => {
                            if (data.group != undefined && data.group != null) {
                                return String(data.group[0].key) === 'Code' + value.itemId;
                            } else {
                                return data.key != undefined && String(data.key) === 'A' + value.itemId;
                            }
                        });
                        let itemName = (item == undefined) ? '' : item.headerText;
                        let message = self.$i18n('Msg_1298', [itemName, value.value]);
                        errorOutput.push(message);
                    });
                }
                if (dataAfter.errorMap[5] != undefined) {
                    _.forEach(dataAfter.errorMap[5], (value) => {
                        errorOutput.push(self.$i18n(value.message));
                    });
                }
                errorOutput = _.uniq(errorOutput);
                self.$modal.error('').then(() => {
                });
                let msgHtml = '';
                _.forEach(errorOutput, (text, index) => {
                    msgHtml += text;
                    if (index != errorOutput.length - 1) {
                        msgHtml += '<br/>';
                    }
                });
                document.getElementsByClassName('type-error')[0].getElementsByClassName('modal-body')[0].getElementsByClassName('text-justify')[0].innerHTML = msgHtml;
            }).catch((res: any) => {
                self.$mask('hide');
                self.$modal.error(res.messageId)
                    .then(() => {
                    });
            });
    }

    private createRegisterParam() {
        let self = this;
        let itemValues: any = self.getItemChange();
        let checkValue = false;
        if (!_.isEmpty(self.checked1s)) {
            checkValue = true;
        }
        let dataCheckSign = [
            {
                rowId: self.params.rowData.id,
                itemId: '',
                value: checkValue,
                valueType: '',
                employeeId: self.params.employeeID,
                date: self.params.date,
                flagRemoveAll: false
            }
        ];

        return {
            'mode': self.params.displayformat,
            'employeeId': self.params.employeeID,
            'itemValues': itemValues,
            'dataCheckSign': dataCheckSign,
            'dataCheckApproval': [],
            'dateRange': {
                'startDate': self.params.date,
                'endDate': self.params.date
            },
            'lstNotFoundWorkType': []
        };
    }

    private getItemChange() {
        let self = this;
        let itemValues: any = [];
        _.forEach(Object.keys(self.screenData[0]), (key: string) => {
            if (_.includes(self.listAutoCalc, key)) {
                return;
            }
            let itemValue: DPItemValue;
            let attendanceItem = self.getAttendanceItem(key);
            let oldRow = self.oldData[key];
            let newRow = self.screenData[0][key];
            if (JSON.stringify(oldRow).localeCompare(JSON.stringify(newRow)) != 0) {
                switch (attendanceItem.attendanceAtr) {
                    case ItemType.ComboBox:
                        itemValue = new DPItemValue(attendanceItem, parseInt(newRow), self.params, self.itemValues);
                        break;
                    default:
                        itemValue = new DPItemValue(attendanceItem, newRow, self.params, self.itemValues);
                        break;
                }
                itemValues.push(itemValue);
            }
        });

        return itemValues;
    }

    private getAttendanceItem(value: any) {
        let self = this;
        let idKey = value.replace('A', '');

        return _.find(self.lstAttendanceItem, (item: any) => item.id == idKey);
    }

    public screenDataWatch() {
        let self = this;
        _.forEach(self.screenData1, (item, key) => {
            let idKey = key.replace('A', '');
            if (_.includes(watchItem, idKey)) {
                self.$watch(`screenData1.${key}`, () => {
                    if (!self.isReady) {
                        return;
                    }
                    let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
                    if (rowData.isCalc) {
                        rowData.isCalc = false;

                        return;
                    }
                    let attendanceItem = self.getAttendanceItem(key);
                    let itemValues = self.getItemChange();
                    _.forEach(itemValues, (item) => {
                        if (item.itemId != attendanceItem.id) {
                            item.columnKey = 'USE';
                        }
                    });
                    if (!_.find(itemValues, (item) => item.itemId == attendanceItem.id)) {
                        let mainObj = new DPItemValue(attendanceItem, self.screenData[0][key], self.params, self.itemValues);
                        itemValues.push(mainObj);
                    }
                    _.remove(self.listAutoCalc, key);
                    let oldRow = self.oldData[key];
                    let notChangeCellValue = (JSON.stringify(oldRow).localeCompare(JSON.stringify(self.screenData[0][key])) == 0) ? true : false;
                    let param = {
                        dailyEdits: [],
                        itemEdits: itemValues,
                        changeSpr31: false,
                        changeSpr34: false,
                        notChangeCell: notChangeCellValue
                    };
                    self.$mask('show');
                    self.$http.post('at', API.linkItemCalc, param)
                        .then((data: any) => {
                            if (data.data.errorFindMaster28 || data.data.errorFindMaster29) {
                                self.workTypeNotFound = true;
                            } else {
                                self.workTypeNotFound = false;    
                            }
                            self.$mask('hide');
                            _.forEach(data.data.cellEdits, (item: any) => {
                                if (!_.includes(self.listAutoCalc, item.item)) {
                                    self.listAutoCalc.push(item.item);
                                }
                            });
                            _.forEach(data.data.cellEdits, (item: any) => {
                                if (!_.isUndefined(self.screenData1[item.item])) {
                                    let rowDataLoop = _.find(self.params.rowData.rowData, (o) => o.key == item.item);
                                    rowDataLoop.isCalc = true;
                                    let attendanceItemLoop = self.getAttendanceItem(item.item);
                                    switch (attendanceItemLoop.attendanceAtr) {
                                        case ItemType.InputNumber:
                                            self.screenData1[item.item] = _.isEmpty(item.value) ? null : _.toNumber(item.value);
                                            break;
                                        case ItemType.InputMoney:
                                            self.screenData1[item.item] = _.isEmpty(item.value) ? null : _.toNumber(item.value);
                                            break;
                                        case ItemType.Time:
                                            self.screenData1[item.item] = _.isEmpty(item.value) ? null : new TimeDuration(item.value).toNumber();
                                            break;
                                        case ItemType.TimeWithDay:
                                            self.screenData1[item.item] = _.isEmpty(item.value) ? null : new TimeWithDay(item.value).toNumber();
                                            break;
                                        default:
                                            self.screenData1[item.item] = item.value;
                                            break;
                                    }
                                }
                            });
                        }).catch((res: any) => {
                            self.$mask('hide');
                            self.$modal.error(res.messageId)
                                .then(() => {
                                });
                        });
                });
            } else {
                self.$watch(`screenData1.${key}`, () => {
                    if (!self.isReady) {
                        return;
                    }
                    let rowData = _.find(self.params.rowData.rowData, (o) => o.key == key);
                    if (rowData.isCalc) {
                        rowData.isCalc = false;

                        return;
                    }
                    _.remove(self.listAutoCalc, key);
                });
            }
        });

    }
}

const API = {
    getPrimitiveAll: 'screen/at/correctionofdailyperformance/getPrimitiveAll',
    masterDialogData: 'screen/at/correctionofdailyperformance/getMasterDialogMob',
    masterTaskSupOpt: 'screen/at/correctionofdailyperformance/getMasterTaskSupMob',
    register: 'screen/at/correctionofdailyperformance/addUpMobile',
    linkItemCalc: 'screen/at/correctionofdailyperformance/calcTime'
};

const watchItem = ['28', '29', '31', '34', '41', '44', '859', '860', '623', '625', '924'];

const CHECK_INPUT = {
    '759': '760', '760': '759', '761': '762',
    '762': '761', '763': '764', '764': '763',
    '765': '766', '766': '765', '157': '159',
    '159': '157', '163': '165', '165': '163',
    '169': '171', '171': '169',
    '175': '177', '177': '175', '181': '183',
    '183': '181', '187': '189', '189': '187',
    '193': '195', '195': '193', '199': '201',
    '201': '199', '205': '207', '207': '205',
    '211': '213', '213': '211',
    '7': '8', '8': '7', '9': '10',
    '10': '9', '11': '12', '12': '11',
    '13': '14', '14': '13', '15': '16',
    '16': '15',
    '17': '18', '18': '17', '19': '20',
    '20': '19', '21': '22', '22': '21',
    '23': '24', '24': '23', '25': '26',
    '26': '25'
};

const WORK_FRAME_MAP = {
    1: [924,934,944,954,964,974,984,994,1004,1014,1024,1034,1044,1054,1064,1074,1084,1094,1104,1114],
    2: [925,935,945,955,965,975,985,995,1005,1015,1025,1035,1045,1055,1065,1075,1085,1095,1105,1115],
    3: [926,936,946,956,966,976,986,996,1006,1016,1026,1036,1046,1056,1066,1076,1086,1096,1106,1116],
    4: [927,937,947,957,967,977,987,997,1007,1017,1027,1037,1047,1057,1067,1077,1087,1097,1107,1117],
    5: [928,938,948,958,968,978,988,998,1008,1018,1028,1038,1048,1058,1068,1078,1088,1098,1108,1118]
};

export enum PrimitiveAll {
    No1 = 'AttendanceTime',
    No2 = 'AttendanceTimeOfExistMinus',
    No3 = 'WorkTimes',
    No4 = 'WorkTypeCode',
    No5 = 'WorkTimeCode',
    No6 = 'WorkLocationCD',
    No7 = 'EmploymentCode',
    No8 = 'ClassificationCode',
    No9 = 'JobTitleCode',
    No10 = 'WorkplaceCode',
    No11 = 'DivergenceReasonContent',
    No12 = 'BreakTimeGoOutTimes',
    No13 = 'RecordRemarks',
    No14 = 'DiverdenceReasonCode',
    No15 = 'TimeWithDayAttr',
    No21 = 'BusinessTypeCode',
    No54 = 'AnyItemAmount',
    No55 = 'AnyAmountMonth',
    No56 = 'AnyItemTime',
    No57 = 'AnyTimeMonth',
    No58 = 'AnyItemTimes',
    No59 = 'AnyTimesMonth',
    No60 = 'DiverdenceReasonCode',
    No73 = 'SuppNumValue',
}

export enum ItemType {
    InputStringCode = 0,
    ButtonDialog = 1,
    InputNumber = 2,
    InputMoney = 3,
    ComboBox = 4,
    Time = 5,
    TimeWithDay = 6,
    InputStringChar = 7,
    InputNumericValue = 9
}

export enum MasterType {
    KDLS02_WorkType = 1,
    KDLS01_WorkTime = 2,
    KDLS10_ServicePlace = 3,
    KDLS32_Reason = 4,
    CDLS08_WorkPlace = 5,
    CDLS03_Classification = 6,
    CDLS04_Possition = 7,
    CDLS02_Employment = 8,
    DoWork = 9,
    Calc = 10,
    ReasonGoOut = 11,
    Remasks = 12,
    TimeLimit = 13,
    CDLS24_BusinessType = 14,
    KDLS12_TaskSelection = 15,
    KDL013_TaskSupOption = 19
}

interface RowData {
    class: any;
    key: string;
    value: any;
    groupKey: string;
    value0: any;
}

interface ItemHeader {
    color: string;
    constraint: ItemConstraint;
    headerText: string;
    key: string;
}

interface ItemConstraint {
    cdisplayType: string;
    max: string;
    min: string;
    primitiveValue: any;
    required: boolean;
    values: any;
}

class DPItemValue {
    public rowId: string;
    public columnKey: string;
    public itemId: number;
    public value: string;
    public valueType: string;
    public layoutCode: string;
    public employeeId: string;
    public date: Date;
    public typeGroup: number;
    public message: string;

    constructor(attendanceItem: any, value: any, params: any, itemValues: any) {
        this.rowId = params.rowData.id;
        this.columnKey = '';
        this.itemId = attendanceItem.id;
        this.value = value;
        this.valueType = _.find(itemValues, (o) => o.itemId == this.itemId).valueType;
        this.layoutCode = _.find(itemValues, (o) => o.itemId == this.itemId).layoutCode;
        this.employeeId = params.employeeID;
        this.date = params.date;
        this.typeGroup = attendanceItem.typeGroup;
        this.message = '';
    }

}   