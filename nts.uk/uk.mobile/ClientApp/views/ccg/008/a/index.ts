import { Vue, _, moment } from '@app/provider';
import { component, Watch } from '@app/core/component';
import { BaseDto, ServerAlertDto, DisplayNotifiDto, AgreementTimeDetail, TimeStatus, DisplayItemType, WidgetDisplayItemType, OverTimeInfo } from './ccgs08Model';
import { Ccg008BComponent } from '../b';
import { TableComponent } from './components/table/';

const servicePath = {
    getAllStartUpData: 'screen/at/mobile/ccgs08/',
    getDisplayNotify: 'screen/at/mobile/ccgs08/displaynotif',
    getOverTimeList: 'screen/at/mobile/ccgs08/overtime/',
    getOvertimeToppage: 'screen/at/mobile/ccgs08/overtime/toppage',
    getKtg029:'screen/at/mobile/ccgs08/ktg029',
    getTopAlert: 'ctx/sys/gateway/stopsetting/find/',
    getVisibleConfig:'screen/at/mobile/ccgs08/visibleConfig'
};

enum SystemOperationMode {
    RUNNING = 0,
    IN_PROGESS = 1,
    STOP = 2
}

enum User {
    ADMIN = 1,
    COMPANY = 0
}

@component({
    name: 'ccg008a',
    route: '/ccg/008/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    components:{
        'ccgs08b': Ccg008BComponent,
        'nts-ccgs008-table': TableComponent
    }
})
export class Ccg008AComponent extends Vue {
    
    public title: string = 'Top Page';
    public labelConstraint = { required: true, min: 100, max: 1000 };
    public serverAlert: ServerAlertDto = new ServerAlertDto();
    public displayNotifisVissible = false;
    public displayNotifis: Array<BaseDto> = [];
    public overtime: AgreementTimeDetail = new AgreementTimeDetail();
    public timeStatus: TimeStatus = new TimeStatus();
    public showFull: boolean = false;
    public normalAgreementLst: Array<any> = [];
    public fullAgreementLst: Array<any> = [];
    public agreementButton: boolean = false;
    public closureID: number = 0;
    public closureYM: number = 0;
    
    public model = {
        date: moment(new Date()).format('YYYYMM')
    };

    public promise: boolean = false;
    
    @Watch('model.date')
    public getOverTimeList(val, oldVal) {
        let self = this;
        self.loadOverTimeInfo();
    }

    public openModal(title: any) {
        let self = this;
        switch (title) {
            case 'CCGS08_11': 
                self.$goto('cmms45b'); 
                
                return;
            case 'CCGS08_14': 
                self.$goto('kdws03a', {
                    screenMode: 0, 
                    closureID: self.closureID, 
                    changePeriodAtr: true, 
                    displayFormat: 0, 
                    targetEmployee: '',
                    closureYM: self.closureYM,
                    dateTarget: null,
                    initClock: null,
                    transitionDesScreen: null,
                    errorRefStartAtr: true 
                }); 
                
                return;
            default: 
                return;
        }
    }

    public created() {
        let self = this;
        Promise.all([
            self.$http.post(servicePath.getTopAlert + User.COMPANY),
            self.$http.post('at', servicePath.getVisibleConfig)
        ]).then( (data: any) => {
            self.checkSystemStopSetting(data[0].data);
            let visibleConfig = data[1].data;
            self.overtime.visible = visibleConfig.overtimeHoursDto ? visibleConfig.overtimeHoursDto.visible : false;
            self.timeStatus.visible = visibleConfig.ktg029 ? visibleConfig.ktg029.visible : false;
            self.displayNotifisVissible = visibleConfig.displayNotifiDto ? visibleConfig.displayNotifiDto.visible : false;
            self.closureID = visibleConfig.closureID;
            self.closureYM = visibleConfig.closureYearMonth;
        }).then( () => {
            self.loadData();
        });
    }

    public loadData() {
        let self = this;
        // display notif
        if (self.displayNotifisVissible) {
            self.$http.post('at', servicePath.getDisplayNotify).then((res: any) => {
                let data = res.data;
                console.log(data);
                if (data && data.visible) {
                    self.checkDisplayNotifi(data);
                }
            }).catch((err: any) => {
    
            });   
        }
        // overtime
        if (self.overtime.visible) {
            self.loadOverTimeInfo();
        }
        // ktg029
        if (self.timeStatus.visible) {
            self.timeStatus.tableConfigs.loading = true;
            self.$http.post('at', servicePath.getKtg029).then((res: any) => {
                let data = res.data;
                if (data && data.data && data.visible) {
                    let displayItems = self.convertToDisplayItem(data.data);
                    self.timeStatus.visible = displayItems && displayItems.length > 0;
                    self.timeStatus.tableConfigs.items = displayItems;
                }
                self.timeStatus.tableConfigs.loading = false;
            }).catch((err: any) => {
                self.timeStatus.tableConfigs.noDataMessage = err.errorMessage;
            });
        }
    }

    private loadOverTimeInfo() {
        let self = this;
        if (self.overtime.visible) {
            self.overtime.tableConfigs.loading = true;
            self.$http.post('at', servicePath.getOvertimeToppage).then((res: any) => {
                let data = res.data;
                if (data && data.visible) {
                    self.normalAgreementLst = self.createNormalList(data.agreementTimeToppageLst, data.yearMonth);
                    self.fullAgreementLst = self.createFullList(data.agreementTimeToppageLst);
                    self.overtime.tableConfigs.items = self.normalAgreementLst;
                    if (data.dataStatus == 0) {
                        self.agreementButton = true;
                    }
                }
                self.overtime.tableConfigs.loading = false;
            }).catch((err: any) => {
                self.overtime.tableConfigs.loading = false;
                self.overtime.tableConfigs.noDataMessage = err.errorMessage;
            });
        }
    }

    public checkSystemStopSetting(setting: ServerAlertDto) { 
        let self = this; 
        if (setting.company) {
            setting.company.visible = SystemOperationMode.IN_PROGESS === setting.company.systemStatus;    
        }
        if (setting.system) {
            setting.system.visible = SystemOperationMode.IN_PROGESS === setting.system.systemStatus;
        }
        setting.visible = (setting.company && setting.company.visible) || (setting.system && setting.system.visible);

        self.serverAlert = setting;
    }

    public checkDisplayNotifi(data: DisplayNotifiDto) {
        let self = this; 
        if (!data.visible) {
            self.displayNotifis = [];    
        } else {
            self.displayNotifis = [
                {title: 'CCGS08_11', visible: data.ktg002},
                {title: 'CCGS08_14', visible: data.checkDailyErrorA2_2 }
            ];
            self.displayNotifisVissible = data.ktg002 || data.checkDailyErrorA2_2;
        }
    }

    public reverseShowAgreement() {
        let self = this;
        self.showFull = !self.showFull;
        if (self.showFull) {
            self.overtime.tableConfigs.items = self.fullAgreementLst;
        } else {
            self.overtime.tableConfigs.items = self.normalAgreementLst;
        }
    }

    public convertToDisplayItem(item: WidgetDisplayItemType): Array<DisplayItemType> {
        const vm = this;

        let results = [];
        let vacationSetting = item.vacationSetting;
    
        // yearlyHoliday
        let yearlyHld = item.yearlyHoliday;
        if (yearlyHld && yearlyHld.nextTimeInfo) {
            let timeRemain = (yearlyHld.nextTimeInfo.hours.hours) * 60 + yearlyHld.nextTimeInfo.hours.min;
            if (yearlyHld && !yearlyHld.calculationMethod && vacationSetting.annualManage) {
                    if (timeRemain) {
                        results.push({
                            name:'KTG029_23', 
                            value: vm.$i18n('CCGS08_37', [yearlyHld.nextTimeInfo.day.toString(), vm.getFormatTime(timeRemain)])
                        }); 
                    } else {
                        results.push({
                            name:'KTG029_23', 
                            value: vm.$i18n('CCGS08_36', [yearlyHld.nextTimeInfo.day.toString()])
                        });
                    }
                }
        }
        // next grantDate
        if (vacationSetting.annualManage) {
            if (yearlyHld && yearlyHld.nextGrantDate) {
                let grantDays = 0;
                if (yearlyHld.nextGrantDateInfo && yearlyHld.grantedDaysNo) {
                    grantDays = yearlyHld.grantedDaysNo;
                }
                results.push({
                    name: 'CCGS08_16', 
                    value: moment(new Date(yearlyHld.nextGrantDate)).format('YY/MM/DD') + '　' + grantDays + vm.$i18n('CCGS08_19'), 
                    // prefix: 'KTG029_60'
                });
            } else {
                results.push({
                    name: 'CCGS08_16', 
                    value: vm.$i18n('CCGS08_38'), 
                    // prefix: 'KTG029_60'
                });
            }
        }
        // 積立年休残数
        if (item.reservedYearsRemainNo && vacationSetting.accumAnnualManage) {
            if (item.reservedYearsRemainNo.showAfter) {
                results.push({name:'積立年休残数', value: item.reservedYearsRemainNo.before, prefix: 'KTG029_60'});
            }
        }
        //setRemainAlternationNoDay
        if (vacationSetting.substituteManage) {
            if (item.vacationSetting.substituteTimeManage) {
                let timeDisp = vm.$dt.timedr(item.remainAlternationNoDay);
                if (timeDisp.startsWith('0')) {
                    results.push({name:'代休残数', value: timeDisp.substr(1, timeDisp.length)});
                } else if (timeDisp.startsWith('-0')) {
                    results.push({name:'代休残数', value: timeDisp.slice(0, 1) + timeDisp.slice(2)});
                } else {
                    results.push({name:'代休残数', value: timeDisp});
                }
            } else {
                results.push({name:'代休残数', value: vm.$i18n('CCGS08_36', [item.remainAlternationDay.toString()])});
            }
        }
        if (vacationSetting.accomoManage) {
            if (item.remainsLeft || item.remainsLeft === 0) {
                results.push({name:'振休残数', value: item.remainsLeft, prefix: 'KTG029_60'});
            }
        }
        // 子看護管理区分
        if (!!item.childRemainNo && vacationSetting.childCaremanage) {
            const {before, after, showAfter} = item.childRemainNo;
            if (after) {
                results.push({
                    name: 'CCGS08_26',
                    value: vm.$i18n('CCGS08_37', [String(before), String(vm.getFormatTime(after))]),
                    isFormatNew: true
                    
                });
            } else {
                results.push({
                    name: 'CCGS08_26',
                    value: vm.$i18n('CCGS08_36', [String(before)]),
                    isFormatNew: true
                    
                });
            }
        } 

        // 介護管理区分
        if (!!item.careLeaveNo && vacationSetting.nursingManage) {
            const {before, after, showAfter} = item.careLeaveNo;

            if (after) {
                results.push({
                    name: 'CCGS08_27',
                    value: vm.$i18n('CCGS08_37', [String(before), String(vm.getFormatTime(after))]),
                    isFormatNew: true
                    
                });
            } else {
                results.push({
                    name: 'CCGS08_27',
                    value: vm.$i18n('CCGS08_36', [String(before)]),
                    isFormatNew: true
                    
                });
            }
        }


        // sphdramainNo
        if (item.sphdramainNo && item.sphdramainNo.length > 0) {
            results.push({name:'KTG029_31', value:''});
            _.forEach(item.sphdramainNo, function(sphd) {
                results.push({name: sphd.name, value: sphd.before, prefix: 'KTG029_60', sub: true});
            });
        }

        // ６０Ｈ超休残数
        // todo format time
        if (!!item.extraRest && vacationSetting.holiday60HManage) {
            const {hours, min} = item.extraRest;
            results.push({
                name: 'CCGS08_28',
                value: vm.$i18n('CCGS08_37', [String(0), String(hours) + ':' + (min >= 10 ? min : ('0' + min))]),
                isFormatNew: true
                
            });
        }



        
        return results;
    }

    public getFormatTime(time) {
        const self = this;

        if (time) {
            let timeStr: string = self.$dt.timedr(time);
            if (timeStr.startsWith('0')) {
                return timeStr.substr(1, timeStr.length);
            } else if (timeStr.startsWith('-0')) {
                return timeStr.slice(0, 1) + timeStr.slice(2);
            } else {
                return timeStr;
            }
        }

        return '0:00';
    }

    public createNormalList(overtimes: Array<any>, currentYearMonth: any): Array<any> {
        let normalAgreementLst: Array<any> = [];
        if (overtimes && overtimes.length > 0) {
            let currentAgreement = _.find(overtimes, (o) => o.yearMonth == currentYearMonth);
            let status = currentAgreement.agreementTimeOfMonthlyDto.status;
            let icon = '';
            if (status === 4) {
                icon = '<i class="fa fa-exclamation-triangle" aria-hidden="true"></i>';
            } else if (status === 3) {
                icon = '<i class="fa fa-exclamation-circle" aria-hidden="true"></i>';
            } else {
                icon = '<i class="fa fa-exclamation-circle invisible"></i>';
            }
            let timeLimit = _.isNull(currentAgreement.agreementTimeOfMonthlyDto.exceptionLimitErrorTime) 
                            ? currentAgreement.agreementTimeOfMonthlyDto.limitErrorTime
                            : currentAgreement.agreementTimeOfMonthlyDto.exceptionLimitErrorTime;
            normalAgreementLst.push({
                month: moment(currentAgreement.yearMonth,'YYYYMM').format('YYYY/MM'),
                time1: timeLimit,
                time2: currentAgreement.agreementTimeOfMonthlyDto.agreementTime,
                time3: icon,
                _rowClass: 'overtime-status-' + status
            });
        }
        
        return normalAgreementLst;
    }

    public createFullList(overtimes: Array<any>): Array<any> {
        let fullAgreementLst: Array<any> = [];
        if (overtimes && overtimes.length > 0) {
            _.forEach(overtimes, (overtime: any) => {
                let statusLoop = overtime.agreementTimeOfMonthlyDto.status;
                let iconLoop = '';
                if (statusLoop === 4) {
                    iconLoop = '<i class="fa fa-exclamation-triangle" aria-hidden="true"></i>';
                } else if (statusLoop === 3) {
                    iconLoop = '<i class="fa fa-exclamation-circle" aria-hidden="true"></i>';
                } else {
                    iconLoop = '<i class="fa fa-exclamation-circle invisible"></i>';
                }
                let timeLimit = _.isNull(overtime.agreementTimeOfMonthlyDto.exceptionLimitErrorTime) 
                            ? overtime.agreementTimeOfMonthlyDto.limitErrorTime
                            : overtime.agreementTimeOfMonthlyDto.exceptionLimitErrorTime;
                fullAgreementLst.push({
                    month: moment(overtime.yearMonth,'YYYYMM').format('YYYY/MM'),
                    time1: timeLimit,
                    time2: overtime.agreementTimeOfMonthlyDto.agreementTime,
                    time3: iconLoop,
                    _rowClass: 'overtime-status-' + statusLoop
                });    
            });
        }
    
        return fullAgreementLst;
    }

}