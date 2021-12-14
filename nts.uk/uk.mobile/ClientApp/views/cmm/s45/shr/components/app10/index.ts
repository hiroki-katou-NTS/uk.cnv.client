import { Vue } from '@app/provider';
import { component, Prop } from '@app/core/component';
import * as _ from 'lodash';

@component({
    name: 'cmms45shrcomponentsapp10',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class CmmS45ShrComponentsApp10Component extends Vue {
    @Prop({
        default: () => ({
            appDispInfoStartupOutput: null,
            appDetail: null
        })
    })
    public readonly params: {
        appDispInfoStartupOutput: any,
        appDetail: any
    };
    public workTimeLstFullData: Array<any> = [];

    public created() {
        const vm = this;
        vm.init();
        vm.$watch('params.appDispInfoStartupOutput', (newV, oldV) => {
            vm.init();
        });
    }

    private init() {
        const vm = this;
        let command = {
            appID: vm.params.appDispInfoStartupOutput.appDetailScreenInfo.application.appID,
            appDispInfoStartupDto: vm.params.appDispInfoStartupOutput
        };
        vm.$http.post('at', API.getDetail, command).then((data: any) => {
            vm.params.appDetail = data.data;
            let wkTimeCodes = [];
            if (vm.rec) {
                wkTimeCodes.push(vm.rec.workInformation.workTime);
            }
            if (vm.abs) {
                wkTimeCodes.push(vm.abs.workInformation.workTime);
            }
            vm.params.appDispInfoStartupOutput.appDispInfoWithDateOutput = data.data.appDispInfoStartup.appDispInfoWithDateOutput;
            
            return vm.$http.post('at', API.getWorkTimeByCDLst, { wkTimeCodes });
        }).then((data: any) => {
            vm.workTimeLstFullData = data.data;
            vm.$emit('loading-complete');
        });
    }

    get absWorkInfoTitle() {
        const vm = this;
        if (!vm.params.appDetail) {
            return '';
        }

        if (vm.params.appDetail.workInfoAttendanceReflect.reflectWorkHour == 0) {
            return 'KAFS11_16';
        } else {
            return 'KAFS11_15';
        }
    }

    // ※4-1
    get rec() {
        const vm = this;
        if (!vm.params.appDetail) {
            return null;
        }

        return vm.params.appDetail.rec;
    }

    // ※4-2
    get abs() {
        const vm = this;
        if (!vm.params.appDetail) {
            return null;
        }

        return vm.params.appDetail.abs;
    }

    get recLeaveComDayOffMana() {
        const vm = this;
        if (vm.rec) {
            return vm.rec.leaveComDayOffMana;
        }

        return [];
    }

    get absLeaveComDayOffMana() {
        const vm = this;
        if (vm.abs) {
            return vm.abs.leaveComDayOffMana;
        }

        return [];
    }

    get absPayoutSubofHDManagements() {
        const vm = this;
        if (vm.abs) {
            return vm.abs.payoutSubofHDManagements;
        }

        return [];
    }

    // ※13, ※16
    public cdtTimeRange2(isRec: boolean) {
        const vm = this;
        if (!vm.params.appDetail) {
            return false;
        }
        let workTimeCD = null;
        if (isRec) {
            if (vm.rec) {
                workTimeCD = vm.rec.workInformation.workTime;
            }
        } else {
            if (vm.abs) {
                workTimeCD = vm.abs.workInformation.workTime;
            }
        }
        let workTimeFull = _.find(vm.workTimeLstFullData, (o) => o.code == workTimeCD);
        if (!workTimeFull) {
            return false;        
        }
        if (!workTimeFull.workTime2) {
            return false;
        }

        return vm.params.appDetail.appDispInfoStartup.appDispInfoNoDateOutput.managementMultipleWorkCycles;
    }

    // ※14, ※17
    public cdtSubMngDailyType(isRec: boolean) {
        const vm = this;
        if (!vm.params.appDetail) {
            return false;
        }
        let workTypeCD = null;
        if (isRec) {
            if (vm.rec) {
                workTypeCD = vm.rec.workInformation.workType;
            }
        } else {
            if (vm.abs) {
                workTypeCD = vm.abs.workInformation.workType;
            }
        }
        let workTypeLst = [];
        if (isRec) {
            if (vm.params.appDetail.applicationForWorkingDay) {
                workTypeLst = vm.params.appDetail.applicationForWorkingDay.workTypeList;
            }
        } else {
            if (vm.params.appDetail.applicationForHoliday) {
                workTypeLst = vm.params.appDetail.applicationForHoliday.workTypeList;
            }
        }
        let workType: any = _.find(workTypeLst, (o) => o.workTypeCode == workTypeCD);
        if (!workType) {
            return false;
        }
        if (workType.workAtr == 0) {
            return false;
        }
        if ((workType.morningCls == 6 && vm.params.appDetail.substituteManagement == 1) ||
            (workType.afternoonCls == 6 && vm.params.appDetail.substituteManagement == 1)) {
            return true;
        }

        return false;
    }

    // ※15
    public cdtLeaveDailyType(conditionType: number) {
        const vm = this;
        if (!vm.params.appDetail) {
            return false;
        }
        if (!vm.params.appDetail.applicationForHoliday) {
            return false;
        }
        if (!vm.abs) {
            return false;
        }
        let workType: any = _.find(vm.params.appDetail.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.abs.workInformation.workType);
        if (!workType) {
            return false;
        }
        if (vm.params.appDetail.workInfoAttendanceReflect.reflectWorkHour == 2) {
            if (workType.workAtr == 0) {
                return false;
            }

            return vm.cdtHalfDay(workType.morningCls, workType.afternoonCls, true);
        }
        if (vm.params.appDetail.workInfoAttendanceReflect.reflectWorkHour == 1) {
            if (workType.workAtr == 0) {
                return false;
            }
            if (conditionType == 1) {
                return vm.cdtHalfDay(workType.morningCls, workType.afternoonCls, true) || 
                    vm.cdtHalfDay(workType.morningCls, workType.afternoonCls, false);
            }
            
            return vm.cdtHalfDay(workType.morningCls, workType.afternoonCls, true);
        }

        return false;
    }

    private cdtHalfDay(morningCls: number, afternoonCls: number, isHalfDayWork: boolean) {
        if (isHalfDayWork) {
            if ((morningCls == 8 && afternoonCls == 0) || (morningCls == 0 && afternoonCls == 8)) {
                return true;
            }

            return false;
        } else {
            if ((morningCls == 8 && _.includes([1, 2, 3, 4, 5, 6, 9], afternoonCls)) ||
                (afternoonCls == 8 && _.includes([1, 2, 3, 4, 5, 6, 9], morningCls))) {
                return true;
            }

            return false;
        }
    }

    // ※18
    public cdtHdMngLeaveDailyType() {
        const vm = this;
        if (!vm.params.appDetail) {
            return false;
        }
        if (!vm.params.appDetail.applicationForHoliday) {
            return false;
        }
        if (!vm.abs) {
            return false;
        }
        let workType: any = _.find(vm.params.appDetail.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.abs.workInformation.workType);
        if (!workType) {
            return false;
        }
        if (workType.workAtr == 0) {
            return true;
        }
        if (workType.morningCls == 8 || workType.afternoonCls == 8) {
            return true;
        }

        return false;
    }

    public prePost(prePostAtr: number) {
        const vm = this;
        if (!vm.params.appDispInfoStartupOutput) {
            return '';
        }
        let prePostResource = [{
            code: 0,
            text: 'KAFS00_10'
        }, {
            code: 1,
            text: 'KAFS00_11'
        }];

        return _.find(prePostResource, (o: any) => o.code == prePostAtr).text;
    }

    public getCDFormat(code: string) {
        if (code) {
            return code;
        }
        
        return '';
    }

    public getWorkTypeName(workTypeCD: string, isRec: boolean) {
        const vm = this;
        if (!vm.params.appDetail) {
            return '';
        }
        let workTypeLst = [];
        if (isRec) {
            if (vm.params.appDetail.applicationForWorkingDay) {
                workTypeLst = vm.params.appDetail.applicationForWorkingDay.workTypeList;
            }
        } else {
            if (vm.params.appDetail.applicationForHoliday) {
                workTypeLst = vm.params.appDetail.applicationForHoliday.workTypeList;
            }
        }
        let workType: any = _.find(workTypeLst, (o) => o.workTypeCode == workTypeCD);
        if (workType) {
            return workType.name;
        }

        return vm.getCDFormat(workTypeCD) + ' ' + vm.$i18n('KAFS11_32');
    }

    public getWorkTimeName(workTimeCD: string) {
        const vm = this;
        if (!vm.params.appDetail) {
            return '';
        }
        let workTime: any = _.find(vm.params.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst, 
            (o) => o.worktimeCode == workTimeCD);
        if (workTime) {
            return workTime.workTimeDisplayName.workTimeName;
        }

        return vm.getCDFormat(workTimeCD) + ' ' + vm.$i18n('KAFS11_32');
    }

    public getTimeRange(workingHours: Array<any>, workNo: number) {
        const vm = this;
        if (_.isEmpty(workingHours)) {
            return '';
        }
        let timeRange = _.find(workingHours, (o) => o.workNo == workNo);
        if (timeRange) {
            return vm.$dt.timewd(timeRange.timeZone.startTime) + ' ～ ' + vm.$dt.timewd(timeRange.timeZone.endTime);
        }

        return '';
    }
}

const API = {
    getDetail: 'at/request/application/holidayshipment/startPageBRefactor',
    getWorkTimeByCDLst: 'at/shared/worktimesetting/get_worktime_by_codes'
};