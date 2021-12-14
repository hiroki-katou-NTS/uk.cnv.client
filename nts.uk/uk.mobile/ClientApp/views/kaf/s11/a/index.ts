import { Vue } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { KafS00ShrComponent, AppType } from 'views/kaf/s00/shr';
import {
    KafS00AComponent,
    KafS00CComponent,
    ScreenMode
} from 'views/kaf/s00';
import * as _ from 'lodash';
import * as moment from 'moment';
import { Kdl001Component } from 'views/kdl/001';
import { KDL002Component } from 'views/kdl/002';
import { KdlS35Component } from 'views/kdl/s35';
import { KdlS36Component } from 'views/kdl/s36';
import { CmmS45CComponent } from '../../../cmm/s45/c/index';

@component({
    name: 'kafs11a',
    route: '/kaf/s11/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        prePostAtr: {
            selectCheck: {
                test(value: number) {
                    const vm = this;
                    if (value == null || value < 0 || value > 1) {
                        document.getElementById('prePostSelect').className += ' invalid';

                        return false;
                    }
                    let prePostSelectElement = document.getElementById('prePostSelect');
                    if (!_.isNull(prePostSelectElement)) {
                        prePostSelectElement.classList.remove('invalid');
                    }

                    return true;
                },
                messageId: 'MsgB_30'
            }
        },
        complementLeaveAtr: {
            required: true
        },
        complementDate: {
            required: true
        },
        leaveDate: {
            required: true
        },
        complementWorkInfo: {
            timeRange1: {
                timeRange: true,
                required: true    
            },
            timeRange2: {
                timeRange: true
            }
        },
        leaveWorkInfo: {
            timeRange1: {
                timeRange: true,
                required: true    
            },
            timeRange2: {
                timeRange: true
            }
        }
    },
    constraints: [],
    components: {
        'kafs00-a': KafS00AComponent,
        'kafs00-c': KafS00CComponent,
        'kdls01': Kdl001Component,
        'kdls02': KDL002Component,
        'kdls35': KdlS35Component,
        'kdls36': KdlS36Component,
        'cmms45c': CmmS45CComponent
    }
})
export class KafS11AComponent extends KafS00ShrComponent {
    @Prop({ default: () => ({}) })
    public params: KAFS11Params;
    public prePostResource: Array<Object> = [];
    public complementLeaveAtrResource: Array<Object> = [];
    public prePostAtr: number = null;
    public complementLeaveAtr: number = ComplementLeaveAtr.COMPLEMENT_LEAVE;
    public complementDate: Date = null;
    public leaveDate: Date = null;
    public complementWorkInfo: WorkInfo = { 
        workTypeCD: null,
        workTimeCD: null,
        timeRange1: { start: null, end: null },
        timeRange2: { start: null, end: null }
    };
    public leaveWorkInfo: WorkInfo = { 
        workTypeCD: null,
        workTimeCD: null,
        timeRange1: { start: null, end: null },
        timeRange2: { start: null, end: null }
    };
    public opAppStandardReasonCD: string = '';
    public opAppReason: string = '';
    public displayInforWhenStarting: any = null;
    public workTimeLstFullData: Array<any> = [];
    public isValidateAll: boolean = true;
    public user: any = null;
    public recHolidayMngLst: Array<any> = [];
    public absHolidayMngLst: Array<any> = [];
    public absWorkMngLst: Array<any> = [];
    
    public created() {
        const vm = this;
        vm.prePostResource = [{
            code: 0,
            text: 'KAFS00_10'
        }, {
            code: 1,
            text: 'KAFS00_11'
        }];
        vm.complementLeaveAtrResource = [{
            code: 0,
            text: 'KAFS11_5'
        }, {
            code: 1,
            text: 'KAFS11_6'
        }, {
            code: 2,
            text: 'KAFS11_7'
        }];
    }

    public mounted() {
        const vm = this;
        vm.initFromParam();
    }

    public initFromParam() {
        const vm = this;
        vm.$mask('show');
        if (vm.mode == ScreenMode.NEW) {
            let employeeIDLst = _.get(vm.params, 'employeeID') ? [_.get(vm.params, 'employeeID')] : [],
                appDate = _.get(vm.params, 'date');
            vm.$auth.user.then((userData: any) => {
                vm.user = userData;

                return vm.loadCommonSetting(
                    AppType.COMPLEMENT_LEAVE_APPLICATION,
                    _.isEmpty(employeeIDLst) ? null : employeeIDLst[0], 
                    null, 
                    appDate ? [appDate] : [], 
                    null);
            }).then((data: any) => {
                if (data) {
                    vm.updateKaf000_A_Params(vm.user);
                    vm.updateKaf000_C_Params(true);
                    let newMode = vm.mode == ScreenMode.NEW ? true : false,
                        employeeID = !_.isEmpty(employeeIDLst) ? employeeIDLst[0] : vm.user.employeeId,
                        dateLst = appDate ? [appDate] : [],
                        displayInforWhenStarting = null,
                        appDispInfoStartupCmd = vm.appDispInfoStartupOutput,
                        command = { newMode, employeeID, dateLst, displayInforWhenStarting, appDispInfoStartupCmd };

                    return vm.$http.post('at', API.start, command);
                }
            }).then((data: any) => {
                if (data) {
                    vm.initData(data.data);
                    let wkTimeCodes = [
                        vm.complementWorkInfo.workTimeCD,
                        vm.leaveWorkInfo.workTimeCD
                    ];

                    return vm.$http.post('at', API.getWorkTimeByCDLst, { wkTimeCodes });
                }
            }).then((data: any) => {
                if (data) {
                    vm.workTimeLstFullData = data.data;
                    if (appDate) {
                        if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT_LEAVE) {
                            vm.complementDate = appDate;
                            vm.leaveDate = appDate;
                        } else if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT) {
                            vm.complementDate = appDate;
                        } else {
                            vm.leaveDate = appDate;
                        }
                    }
                }
            }).catch((error: any) => {
                vm.handleErrorCustom(error).then((result) => {
                    if (result) {
                        vm.handleErrorCommon(error);
                    }
                });
            }).then(() => vm.$mask('hide'));
        } else {
            vm.$auth.user.then((userData: any) => {
                vm.user = userData;
            }).then(() => {
                vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
                vm.updateKaf000_A_Params(vm.user);
                vm.updateKaf000_C_Params(false);
                vm.initData(vm.params.appDetail);
                let wkTimeCodes = [
                    vm.complementWorkInfo.workTimeCD,
                    vm.leaveWorkInfo.workTimeCD
                ];
                
                return vm.$http.post('at', API.getWorkTimeByCDLst, { wkTimeCodes });
            }).then((data: any) => {
                vm.workTimeLstFullData = data.data;
            }).catch((error: any) => {
                vm.handleErrorCustom(error).then((result) => {
                    if (result) {
                        vm.handleErrorCommon(error);
                    }
                });
            }).then(() => vm.$mask('hide'));
        }
    }

    @Watch('complementLeaveAtr')
    public complementLeaveAtrWatcher(value) {
        const vm = this;
        vm.updateValidate();
    }

    private initData(displayInforWhenStarting: any) {
        const vm = this;
        vm.formatDateResult(displayInforWhenStarting);
        vm.initDataCommon();
        vm.initDataComplement();
        vm.initDataLeave();
        vm.updateValidate();
    }

    private formatDateResult(displayInforWhenStarting: any) {
        const vm = this;
        vm.displayInforWhenStarting = displayInforWhenStarting;
        if (vm.displayInforWhenStarting.rec) {
            _.forEach(vm.displayInforWhenStarting.rec.leaveComDayOffMana, (item) => {
                item.outbreakDay = new Date(item.outbreakDay).toISOString();
                item.dateOfUse = new Date(item.dateOfUse).toISOString();
            });
            _.forEach(vm.displayInforWhenStarting.rec.leaveComDayOffManaOld, (item) => {
                item.outbreakDay = new Date(item.outbreakDay).toISOString();
                item.dateOfUse = new Date(item.dateOfUse).toISOString();
            });
        }
        if (vm.displayInforWhenStarting.abs) {
            _.forEach(vm.displayInforWhenStarting.abs.leaveComDayOffMana, (item) => {
                item.outbreakDay = new Date(item.outbreakDay).toISOString();
                item.dateOfUse = new Date(item.dateOfUse).toISOString();
            });
            _.forEach(vm.displayInforWhenStarting.abs.leaveComDayOffManaOld, (item) => {
                item.outbreakDay = new Date(item.outbreakDay).toISOString();
                item.dateOfUse = new Date(item.dateOfUse).toISOString();
            });
            _.forEach(vm.displayInforWhenStarting.abs.payoutSubofHDManagements, (item) => {
                item.outbreakDay = new Date(item.outbreakDay).toISOString();
                item.dateOfUse = new Date(item.dateOfUse).toISOString();
            });
            _.forEach(vm.displayInforWhenStarting.abs.payoutSubofHDManagementsOld, (item) => {
                item.outbreakDay = new Date(item.outbreakDay).toISOString();
                item.dateOfUse = new Date(item.dateOfUse).toISOString();
            });
        }
    }

    private initDataCommon() {
        const vm = this;
        vm.prePostAtr = vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoWithDateOutput.prePostAtr;
        if (vm.mode == ScreenMode.DETAIL) {
            vm.prePostAtr = vm.displayInforWhenStarting.appDispInfoStartup.appDetailScreenInfo.application.prePostAtr;
            if (vm.displayInforWhenStarting.rec) {
                vm.complementDate = new Date(moment(vm.displayInforWhenStarting.rec.application.appDate).format('YYYY/MM/DD'));
            }
            if (vm.displayInforWhenStarting.abs) {
                vm.leaveDate = new Date(moment(vm.displayInforWhenStarting.abs.application.appDate).format('YYYY/MM/DD'));
            }
        }
    }

    private initDataComplement() {
        const vm = this;
        if (vm.mode == ScreenMode.NEW) {
            if (vm.displayInforWhenStarting.applicationForWorkingDay.workType) {
                vm.complementWorkInfo.workTypeCD = vm.displayInforWhenStarting.applicationForWorkingDay.workType;
            } else {
                let headWorkType: any = _.head(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList);
                if (headWorkType) {
                    vm.complementWorkInfo.workTypeCD = headWorkType.workTypeCode;
                }
            }
            vm.complementWorkInfo.workTimeCD = vm.displayInforWhenStarting.applicationForWorkingDay.workTime;
            vm.complementWorkInfo.timeRange1 = {
                start: vm.displayInforWhenStarting.applicationForWorkingDay.startTime,
                end: vm.displayInforWhenStarting.applicationForWorkingDay.endTime
            };
            vm.complementWorkInfo.timeRange2 = {
                start: vm.displayInforWhenStarting.applicationForWorkingDay.startTime2,
                end: vm.displayInforWhenStarting.applicationForWorkingDay.endTime2
            };
        } else {
            if (vm.displayInforWhenStarting.rec) {
                vm.complementWorkInfo.workTypeCD = vm.displayInforWhenStarting.rec.workInformation.workType;
                vm.complementWorkInfo.workTimeCD = vm.displayInforWhenStarting.rec.workInformation.workTime;
                let timeRange1 = _.find(vm.displayInforWhenStarting.rec.workingHours, (o) => o.workNo == 1);
                if (timeRange1) {
                    vm.complementWorkInfo.timeRange1 = {
                        start: timeRange1.timeZone.startTime,
                        end: timeRange1.timeZone.endTime
                    };    
                }
                let timeRange2 = _.find(vm.displayInforWhenStarting.rec.workingHours, (o) => o.workNo == 2);
                if (timeRange2) {
                    vm.complementWorkInfo.timeRange2 = {
                        start: timeRange2.timeZone.startTime,
                        end: timeRange2.timeZone.endTime
                    };    
                }
                vm.recHolidayMngLst = vm.displayInforWhenStarting.rec.leaveComDayOffMana;
            }
        }
    }

    private initDataLeave() {
        const vm = this;
        if (vm.mode == ScreenMode.NEW) {
            if (vm.displayInforWhenStarting.applicationForHoliday.workType) {
                vm.leaveWorkInfo.workTypeCD = vm.displayInforWhenStarting.applicationForHoliday.workType;
            } else {
                let headWorkType: any = _.head(vm.displayInforWhenStarting.applicationForHoliday.workTypeList);
                if (headWorkType) {
                    vm.leaveWorkInfo.workTypeCD = headWorkType.workTypeCode;
                }
            }
            vm.leaveWorkInfo.workTimeCD = vm.displayInforWhenStarting.applicationForHoliday.workTime;
            vm.leaveWorkInfo.timeRange1 = {
                start: vm.displayInforWhenStarting.applicationForHoliday.startTime,
                end: vm.displayInforWhenStarting.applicationForHoliday.endTime
            };
            vm.leaveWorkInfo.timeRange2 = {
                start: vm.displayInforWhenStarting.applicationForHoliday.startTime2,
                end: vm.displayInforWhenStarting.applicationForHoliday.endTime2
            };
        } else {
            if (vm.displayInforWhenStarting.abs) {
                vm.leaveWorkInfo.workTypeCD = vm.displayInforWhenStarting.abs.workInformation.workType;
                vm.leaveWorkInfo.workTimeCD = vm.displayInforWhenStarting.abs.workInformation.workTime;
                let timeRange1 = _.find(vm.displayInforWhenStarting.abs.workingHours, (o) => o.workNo == 1);
                if (timeRange1) {
                    vm.leaveWorkInfo.timeRange1 = {
                        start: timeRange1.timeZone.startTime,
                        end: timeRange1.timeZone.endTime
                    };    
                }
                let timeRange2 = _.find(vm.displayInforWhenStarting.abs.workingHours, (o) => o.workNo == 2);
                if (timeRange2) {
                    vm.leaveWorkInfo.timeRange2 = {
                        start: timeRange2.timeZone.startTime,
                        end: timeRange2.timeZone.endTime
                    };    
                }
                vm.absHolidayMngLst = vm.displayInforWhenStarting.abs.leaveComDayOffMana;
                vm.absWorkMngLst = vm.displayInforWhenStarting.abs.payoutSubofHDManagements;
            }
        }
    }

    private updateValidate() {
        const vm = this;
        if (vm.mode == ScreenMode.NEW) {
            if (vm.complementLeaveAtr == ComplementLeaveAtr.LEAVE) {
                vm.$updateValidator('complementDate', { validate: false });
            } else {
                vm.$updateValidator('complementDate', { validate: true });
            }
            if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT) {
                vm.$updateValidator('leaveDate', { validate: false });
            } else {
                vm.$updateValidator('leaveDate', { validate: true });
            }
            if (vm.dispComplementContent) {
                if (vm.enableComplementTimeRange) {
                    vm.$updateValidator('complementWorkInfo.timeRange1', { validate: true });
                } else {
                    vm.$updateValidator('complementWorkInfo.timeRange1', { validate: false });
                }
            } else {
                vm.$updateValidator('complementWorkInfo.timeRange1', { validate: false });
            }
            if (vm.dispComplementTimeRange2) {
                if (vm.enableComplementTimeRange) {
                    vm.$updateValidator('complementWorkInfo.timeRange2', { validate: true });
                } else {
                    vm.$updateValidator('complementWorkInfo.timeRange2', { validate: false });
                }
            } else {
                vm.$updateValidator('complementWorkInfo.timeRange2', { validate: false });
            }
            if (vm.dispLeaveTimeRange1) {
                if (vm.enableLeaveTimeRange) {
                    vm.$updateValidator('leaveWorkInfo.timeRange1', { validate: true });
                } else {
                    vm.$updateValidator('leaveWorkInfo.timeRange1', { validate: false });
                }
            } else {
                vm.$updateValidator('leaveWorkInfo.timeRange1', { validate: false });
            }
            if (vm.dispLeaveTimeRange2) {
                if (vm.enableLeaveTimeRange) {
                    vm.$updateValidator('leaveWorkInfo.timeRange2', { validate: true });
                } else {
                    vm.$updateValidator('leaveWorkInfo.timeRange2', { validate: false });
                }
            } else {
                vm.$updateValidator('leaveWorkInfo.timeRange2', { validate: false });
            }
        } else {
            if (vm.displayInforWhenStarting.rec) {
                vm.$updateValidator('complementDate', { validate: true });
            } else {
                vm.$updateValidator('complementDate', { validate: false });
            }
            if (vm.displayInforWhenStarting.abs) {
                vm.$updateValidator('leaveDate', { validate: true });
            } else {
                vm.$updateValidator('leaveDate', { validate: false });
            }
            if (vm.dispComplementContent) {
                if (vm.enableComplementTimeRange) {
                    vm.$updateValidator('complementWorkInfo.timeRange1', { validate: true });
                } else {
                    vm.$updateValidator('complementWorkInfo.timeRange1', { validate: false });
                }
            } else {
                vm.$updateValidator('complementWorkInfo.timeRange1', { validate: false });
            }
            if (vm.dispComplementTimeRange2) {
                if (vm.enableComplementTimeRange) {
                    vm.$updateValidator('complementWorkInfo.timeRange2', { validate: true });
                } else {
                    vm.$updateValidator('complementWorkInfo.timeRange2', { validate: false });
                }
            } else {
                vm.$updateValidator('complementWorkInfo.timeRange2', { validate: false });
            }
            if (vm.dispLeaveTimeRange1) {
                if (vm.enableLeaveTimeRange) {
                    vm.$updateValidator('leaveWorkInfo.timeRange1', { validate: true });
                } else {
                    vm.$updateValidator('leaveWorkInfo.timeRange1', { validate: false });
                }
            } else {
                vm.$updateValidator('leaveWorkInfo.timeRange1', { validate: false });
            }
            if (vm.dispLeaveTimeRange2) {
                if (vm.enableLeaveTimeRange) {
                    vm.$updateValidator('leaveWorkInfo.timeRange2', { validate: true });
                } else {
                    vm.$updateValidator('leaveWorkInfo.timeRange2', { validate: false });
                }
            } else {
                vm.$updateValidator('leaveWorkInfo.timeRange2', { validate: false });
            }
        }
    }

    public getCDFormat(code: string) {
        if (code) {
            return code;
        }
        
        return '';
    }

    public getWorkTypeName(workTypeCD: string, isComplement: boolean) {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return '';
        }
        let workTypeLst = [];
        if (isComplement) {
            if (vm.displayInforWhenStarting.applicationForWorkingDay) {
                workTypeLst = vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList;
            }
        } else {
            if (vm.displayInforWhenStarting.applicationForHoliday) {
                workTypeLst = vm.displayInforWhenStarting.applicationForHoliday.workTypeList;
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
        if (!vm.displayInforWhenStarting) {
            return '';
        }
        let workTime: any = _.find(vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst, 
            (o) => o.worktimeCode == workTimeCD);
        if (workTime) {
            return workTime.workTimeDisplayName.workTimeName;
        }
        // if (vm.mode == ScreenMode.DETAIL) {
        //     return vm.getCDFormat(workTimeCD) + ' ' + vm.$i18n('KAFS11_32');
        // }

        return '';
    }

    public getWorkTimeLabel(workTimeCD: string, isComplement: boolean) {
        const vm = this;
        let workTimeFull = _.find(vm.workTimeLstFullData, (o) => o.code == workTimeCD);
        if (!workTimeFull) {
            return '';        
        }
        let result = '';
        // if (workTimeFull.workTime2) {
        //     let startTime2 = '', endTime2= '';
        //     if (isComplement) {
        //         startTime2 = vm.complementWorkInfo.timeRange2.start == null ? '' : vm.$dt.timewd(vm.complementWorkInfo.timeRange2.start),
        //         endTime2 = vm.complementWorkInfo.timeRange2.end == null ? '' : vm.$dt.timewd(vm.complementWorkInfo.timeRange2.end);
        //     } else {
        //         startTime2 = vm.leaveWorkInfo.timeRange2.start == null ? '' : vm.$dt.timewd(vm.leaveWorkInfo.timeRange2.start),
        //         endTime2 = vm.leaveWorkInfo.timeRange2.end == null ? '' : vm.$dt.timewd(vm.leaveWorkInfo.timeRange2.end);
        //     }
        //     result = '<div>' + startTime2 + '～' + endTime2 + '</div>';
        // }
        if (workTimeFull.workTime1) {
            let startTime1 = '', endTime1= '';
            if (isComplement) {
                startTime1 = vm.complementWorkInfo.timeRange1.start == null ? '' : vm.$dt.timewd(vm.complementWorkInfo.timeRange1.start),
                endTime1 = vm.complementWorkInfo.timeRange1.end == null ? '' : vm.$dt.timewd(vm.complementWorkInfo.timeRange1.end);
            } else {
                startTime1 = vm.leaveWorkInfo.timeRange1.start == null ? '' : vm.$dt.timewd(vm.leaveWorkInfo.timeRange1.start),
                endTime1 = vm.leaveWorkInfo.timeRange1.end == null ? '' : vm.$dt.timewd(vm.leaveWorkInfo.timeRange1.end);
            }
            result = '<div>' + startTime1 + '～' + endTime1 + '</div>';
        }

        return result;
    }

    get ScreenMode() {
        return ScreenMode;
    }

    get mode() {
        const vm = this;
        if (vm.params && vm.params.appDetail && vm.params.isDetailMode) {
            return ScreenMode.DETAIL;
        }

        return ScreenMode.NEW;
    }

    get leaveWorkInfoTitle() {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return '';
        }

        if (vm.displayInforWhenStarting.workInfoAttendanceReflect.reflectWorkHour == 0) {
            return 'KAFS11_16';
        } else {
            return 'KAFS11_15';
        }
    }

    get dispComplementContent() {
        const vm = this;
        if (vm.mode == ScreenMode.DETAIL) {
            // ※4-1	
            if (!vm.displayInforWhenStarting) {
                return false;
            }

            return vm.displayInforWhenStarting.rec;
        }
        // ※3-1

        return vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT_LEAVE || vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT;
    }

    // ※5
    get cdtComplementTimeRange2() {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return false;
        }
        let workTimeFull = _.find(vm.workTimeLstFullData, (o) => o.code == vm.complementWorkInfo.workTimeCD);
        if (!workTimeFull) {
            return false;        
        }
        if (!workTimeFull.workTime2) {
            return false;
        }

        return vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoNoDateOutput.managementMultipleWorkCycles;
    }

    // ※5-1, ※5-2
    get dispComplementTimeRange2() {
        const vm = this;

        return vm.dispComplementContent && vm.cdtComplementTimeRange2;
    }

    // ※6
    public cdtSubMngComplementDailyType() {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return false;
        }
        let workType: any = _.find(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList, 
            (o) => o.workTypeCode == vm.complementWorkInfo.workTypeCD);
        if (!workType) {
            return false;
        }
        if (workType.workAtr == 0) {
            return false;
        }
        if ((workType.morningCls == 6 && vm.displayInforWhenStarting.substituteManagement == 1) ||
            (workType.afternoonCls == 6 && vm.displayInforWhenStarting.substituteManagement == 1)) {
            return true;
        }

        return false;
    }

    // ※7
    public cdtSubstituteWorkAppReflect() {
        const vm = this;
        if (vm.displayInforWhenStarting) {
            return vm.displayInforWhenStarting.substituteWorkAppReflect.reflectAttendanceAtr == 1;
        }

        return false;
    }

    get enableComplementTimeRange() {
        const vm = this;
        // if (vm.mode == ScreenMode.DETAIL) {
        //
        //     return vm.cdtSubstituteWorkAppReflect();
        // }

        return vm.cdtSubstituteWorkAppReflect();
        // return vm.cdtSubMngComplementDailyType();
    }

    // ※6-1, ※6-2
    get dispLeaveLinkContent1() {
        const vm = this;

        return vm.dispComplementContent && vm.cdtSubMngComplementDailyType();
    }

    get dispLeaveContent() {
        const vm = this;
        if (vm.mode == ScreenMode.DETAIL) {
            // ※4-2
            if (!vm.displayInforWhenStarting) {
                return false;
            }

            return vm.displayInforWhenStarting.abs;
        }
        // ※3-2

        return vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT_LEAVE || vm.complementLeaveAtr == ComplementLeaveAtr.LEAVE;
    }

    // ※8
    private cdtLeaveDailyType(conditionType: number) {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return false;
        }
        let workType: any = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
        if (!workType) {
            return false;
        }
        if (vm.displayInforWhenStarting.workInfoAttendanceReflect.reflectWorkHour == 2) {
            if (workType.workAtr == 0) {
                return false;
            }

            return vm.cdtHalfDay(workType.morningCls, workType.afternoonCls, true);
        }
        if (vm.displayInforWhenStarting.workInfoAttendanceReflect.reflectWorkHour == 1) {
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

    // ※8-3, ※8-4
    get dispLeaveWorkTime() {
        const vm = this;

        return vm.dispLeaveContent && vm.cdtLeaveDailyType(1);
    }

    // ※8-5, ※8-6
    get dispLeaveTimeRange1() {
        const vm = this;

        return vm.dispLeaveContent && vm.cdtLeaveDailyType(2);
    }

    // ※9
    get cdtLeaveTimeRange2() {
        const vm = this;
        let usage = true;
        if (!vm.displayInforWhenStarting) {
            return false;
        }
        let workTimeFull = _.find(vm.workTimeLstFullData, (o) => o.code == vm.leaveWorkInfo.workTimeCD);
        if (!workTimeFull) {
            return false;        
        }
        if (!workTimeFull.workTime2) {
            return false;
        }

        return vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoNoDateOutput.managementMultipleWorkCycles;
    }

    // ※9-1, ※9-2
    get dispLeaveTimeRange2() {
        const vm = this;

        return vm.cdtLeaveTimeRange2 && vm.cdtLeaveDailyType(2) && vm.dispLeaveContent;
    }

    // ※10
    get enableLeaveTimeRange() {
        const vm = this;

        return vm.displayInforWhenStarting.workInfoAttendanceReflect.reflectAttendance == 1;
    }

    // ※11
    private cdtSubMngLeaveDailyType() {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return false;
        }
        let workType: any = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
        if (!workType) {
            return false;
        }
        if (workType.workAtr == 0) {
            return false;
        }
        if ((workType.morningCls == 6 && vm.displayInforWhenStarting.substituteManagement == 1) ||
            (workType.afternoonCls == 6 && vm.displayInforWhenStarting.substituteManagement == 1)) {
            return true;
        }

        return false;
    }

    // ※11-1, ※11-2
    get dispLeaveLinkContent2() {
        const vm = this;

        return vm.dispLeaveContent && vm.cdtSubMngLeaveDailyType();
    }

    // ※12
    public cdtHdMngLeaveDailyType() {
        const vm = this;
        if (!vm.displayInforWhenStarting) {
            return false;
        }
        let workType: any = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
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

    // ※12-1, ※12-2
    get dispComplementLinkContent() {
        const vm = this;
        let onlyAbs = false;
        if (vm.mode == ScreenMode.NEW) {
            // ※3-3
            if (vm.complementLeaveAtr == ComplementLeaveAtr.LEAVE) {
                onlyAbs = true;
            }    
        } else {
            // ※4-3
            if (vm.displayInforWhenStarting) {
                if (!vm.displayInforWhenStarting.rec) {
                    onlyAbs = true;
                }
            }
        }

        return onlyAbs && vm.cdtHdMngLeaveDailyType();
    }

    // ※A13
    get dispPrePostAtr() {
        const vm = this;
        if (!vm.appDispInfoStartupOutput) {
            return false;
        }

        return vm.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.prePostDisplayAtr == 1;
    }

    get dispComplementLeaveAtr() {
        const vm = this;
        if (vm.mode == ScreenMode.DETAIL) {
            return false;
        }
        // ※2
        if (!vm.displayInforWhenStarting) {
            return false;
        }

        return vm.displayInforWhenStarting.substituteHdWorkAppSet.simultaneousApplyRequired == 1 ? false : true;
    }
    
    get enablePrePostAtr() {
        const vm = this;
        if (vm.mode == ScreenMode.DETAIL) {
            return false;
        }
        // ※A14
        if (!vm.appDispInfoStartupOutput) {
            return false;
        }
        let appTypeSetting: any = _.find(vm.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting,
            (o) => o.appType == AppType.COMPLEMENT_LEAVE_APPLICATION);
        if (appTypeSetting) {
            return appTypeSetting.canClassificationChange;
        }

        return false;
    }

    // ※A15, ※A16
    public isSelectMngLst(mngLst: Array<any>) {
        return !_.isEmpty(mngLst);
    }

    public kaf000CChangeReasonCD(opAppStandardReasonCD) {
        const vm = this;
        vm.opAppStandardReasonCD = opAppStandardReasonCD;
    }

    public kaf000CChangeAppReason(opAppReason) {
        const vm = this;
        vm.opAppReason = opAppReason;
    }

    @Watch('complementDate')
    public complementDateWatcher(value) {
        const vm = this;
        if (vm.mode == ScreenMode.DETAIL) {
            return;
        }
        let command = {
            workingDate: moment(value).format('YYYY/MM/DD'),
            holidayDate: vm.leaveDate ? moment(vm.leaveDate).format('YYYY/MM/DD') : null,
            displayInforWhenStarting: vm.displayInforWhenStarting
        };
        vm.$mask('show');
        vm.$http.post('at', API.changeRecDate, command).then((data: any) => {
            vm.formatDateResult(data.data);
            vm.initDataComplement();
            vm.updateValidate();
            vm.$mask('hide');
        });
    }

    @Watch('leaveDate')
    public leaveDateWatcher(value) {
        const vm = this;
        if (vm.mode == ScreenMode.DETAIL) {
            return;
        }
        let command = {
            workingDate: vm.complementDate ? moment(vm.complementDate).format('YYYY/MM/DD') : null,
            holidayDate: moment(value).format('YYYY/MM/DD'),
            displayInforWhenStarting: vm.displayInforWhenStarting
        };
        vm.$mask('show');
        vm.$http.post('at', API.changeAbsDate, command).then((data: any) => {
            vm.formatDateResult(data.data);
            vm.initDataLeave();
            vm.updateValidate();
            vm.$mask('hide');
        });
    }

    public openKDLS02(isComplement: boolean) {
        const vm = this;
        let param = {},
            workTimeCDLst = _.map(vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst, (o) => o.worktimeCode);
        if (isComplement) {
            param = {
                isAddNone: false,
                seledtedWkTimeCDs: workTimeCDLst,
                selectedWorkTimeCD: vm.complementWorkInfo.workTimeCD,
                selectedWorkTypeCD: vm.complementWorkInfo.workTypeCD,
                isSelectWorkTime: false,
                seledtedWkTypeCDs: _.map(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList, (o) => o.workTypeCode)
            };
        } else {
            param = {
                isAddNone: false,
                seledtedWkTimeCDs: workTimeCDLst,
                selectedWorkTimeCD: vm.leaveWorkInfo.workTimeCD,
                selectedWorkTypeCD: vm.leaveWorkInfo.workTypeCD,
                isSelectWorkTime: false,
                seledtedWkTypeCDs: _.map(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, (o) => o.workTypeCode)
            };
        }
        vm.$modal('kdls02', param).then((result: any) => {
            if (result) {
                let isChangeWorkType = true,
                    workTypeOld = null,
                    workTypeNew = null,
                    workTimeCD = '',
                    leaveComDayOffMana = [],
                    payoutSubofHDManagements = [];
                if (isComplement) {
                    let workTypeOldSelect = _.find(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList, (o) => o.workTypeCode == vm.complementWorkInfo.workTypeCD);
                    if (workTypeOldSelect) {
                        workTypeOld = workTypeOldSelect;
                    }
                    vm.complementWorkInfo.workTypeCD = result.selectedWorkType.workTypeCode;
                    let workTypeNewSelect = _.find(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList, (o) => o.workTypeCode == vm.complementWorkInfo.workTypeCD);
                    if (workTypeNewSelect) {
                        workTypeNew = workTypeNewSelect;
                    }
                    workTimeCD = vm.complementWorkInfo.workTimeCD;
                    leaveComDayOffMana = vm.recHolidayMngLst;
                } else {
                    let workTypeOldSelect = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
                    if (workTypeOldSelect) {
                        workTypeOld = workTypeOldSelect;
                    }
                    vm.leaveWorkInfo.workTypeCD = result.selectedWorkType.workTypeCode;
                    let workTypeNewSelect = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
                    if (workTypeNewSelect) {
                        workTypeNew = workTypeNewSelect;
                    }
                    workTimeCD = vm.leaveWorkInfo.workTimeCD;
                    leaveComDayOffMana = vm.absHolidayMngLst;
                    payoutSubofHDManagements = vm.absWorkMngLst;
                }
                vm.$mask('show');

                return vm.$http.post('at', API.getTimeZoneValue, { isChangeWorkType, workTypeOld, workTypeNew, workTimeCD, leaveComDayOffMana, payoutSubofHDManagements });
            }
        }).then((result: any) => {
            if (result) {
                vm.updateTimeRange(isComplement, result.data);
                if (isComplement) {
                    if (result.data.vacationCheckOutput.clearManageSubsHoliday) {
                        vm.recHolidayMngLst = [];
                    }
                } else {
                    if (result.data.vacationCheckOutput.clearManageSubsHoliday) {
                        vm.absHolidayMngLst = [];
                    }
                    if (result.data.vacationCheckOutput.clearManageHolidayString) {
                        vm.absWorkMngLst = [];
                    }
                }
                vm.updateValidate();
            }
            vm.$mask('hide');
        });
    }

    public openKDLS01(isComplement: boolean) {
        const vm = this;
        let param = {},
            workTimeCDLst = _.map(vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst, (o) => o.worktimeCode);
        if (isComplement) {
            param = {
                isAddNone: false,
                seledtedWkTimeCDs: workTimeCDLst,
                selectedWorkTimeCD: vm.complementWorkInfo.workTimeCD,
                selectedWorkType: vm.complementWorkInfo.workTypeCD
            };
        } else {
            param = {
                isAddNone: false,
                seledtedWkTimeCDs: workTimeCDLst,
                selectedWorkTimeCD: vm.leaveWorkInfo.workTimeCD,
                selectedWorkType: vm.leaveWorkInfo.workTypeCD
            };
        }
        vm.$modal('kdls01', param).then((result: any) => {
            if (result) {
                let isChangeWorkType = false,
                    workTypeOld = null,
                    workTypeNew = null,
                    workTimeCD = '',
                    leaveComDayOffMana = [],
                    payoutSubofHDManagements = [];
                if (isComplement) {
                    let workTypeNewSelect = _.find(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList, (o) => o.workTypeCode == vm.complementWorkInfo.workTypeCD);
                    if (workTypeNewSelect) {
                        workTypeNew = workTypeNewSelect;
                    }
                    vm.complementWorkInfo.workTimeCD = result.selectedWorkTime.code;
                    workTimeCD = vm.complementWorkInfo.workTimeCD;              
                } else {
                    let workTypeNewSelect = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
                    if (workTypeNewSelect) {
                        workTypeNew = workTypeNewSelect;
                    }
                    vm.leaveWorkInfo.workTimeCD = result.selectedWorkTime.code;
                    workTimeCD = vm.leaveWorkInfo.workTimeCD;
                }
                vm.$mask('show');

                return vm.$http.post('at', API.getTimeZoneValue, { isChangeWorkType, workTypeOld, workTypeNew, workTimeCD, leaveComDayOffMana, payoutSubofHDManagements });
            }
        }).then((result: any) => {
            if (result) {
                vm.updateTimeRange(isComplement, result.data);
                let wkTimeCodes = [
                    vm.complementWorkInfo.workTimeCD,
                    vm.leaveWorkInfo.workTimeCD
                ];

                return vm.$http.post('at', API.getWorkTimeByCDLst, { wkTimeCodes });
            }    
        }).then((result: any) => {
            if (result) {
                vm.workTimeLstFullData = result.data;
                vm.updateValidate();
            }
            vm.$mask('hide');
        });
    }

    private updateTimeRange(isComplement: boolean, result: any) {
        const vm = this;
        let timeZone1 = _.find(result.timeZoneLst, (o) => o.workNo == 1),
            timeZone2 = _.find(result.timeZoneLst, (o) => o.workNo == 2);
        if (isComplement) {
            if (timeZone1) {
                vm.complementWorkInfo.timeRange1 = { start: timeZone1.timeZone.startTime, end: timeZone1.timeZone.endTime };
            } else {
                vm.complementWorkInfo.timeRange1 = { start: null, end: null };
            }
            if (timeZone2) {
                vm.complementWorkInfo.timeRange2 = { start: timeZone2.timeZone.startTime, end: timeZone2.timeZone.endTime };
            } else {
                vm.complementWorkInfo.timeRange2 = { start: null, end: null };
            }
        } else {
            if (timeZone1) {
                vm.leaveWorkInfo.timeRange1 = { start: timeZone1.timeZone.startTime, end: timeZone1.timeZone.endTime };
            } else {
                vm.leaveWorkInfo.timeRange1 = { start: null, end: null };
            }
            if (timeZone2) {
                vm.leaveWorkInfo.timeRange2 = { start: timeZone2.timeZone.startTime, end: timeZone2.timeZone.endTime };
            } else {
                vm.leaveWorkInfo.timeRange2 = { start: null, end: null };   
            }
        }
    }

    public openKDLS36Complement() {
        const vm = this;
        let actualContentDispList = vm.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst,
            param: any = {
                employeeId: vm.user.employeeId,
                period: { startDate: vm.complementDate, endDate: vm.complementDate },
                targetSelectionAtr: 1,
                actualContentDisplayList: actualContentDispList ? actualContentDispList : [],
                managementData: vm.recHolidayMngLst,
            },
            workType: any = _.find(vm.displayInforWhenStarting.applicationForWorkingDay.workTypeList, 
            (o) => o.workTypeCode == vm.complementWorkInfo.workTypeCD);
        if (workType.workAtr == 0) {
            param.daysUnit = 1;
        } else {
            param.daysUnit = 0.5;
        }
        vm.$modal('kdls36', param).then((result: any) => {
            if (result) {
                vm.recHolidayMngLst = result.mngDisp;
                vm.formatMngLst();
            }
        });
    }

    public openKDLS36Leave() {
        const vm = this;
        let actualContentDispList = vm.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst,
            param: any = {
                employeeId: vm.user.employeeId,
                period: { startDate: vm.leaveDate, endDate: vm.leaveDate },
                targetSelectionAtr: 1,
                actualContentDisplayList: actualContentDispList ? actualContentDispList : [],
                managementData: vm.absHolidayMngLst,
            },
            workType: any = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
        if (workType.workAtr == 0) {
            param.daysUnit = 1;
        } else {
            param.daysUnit = 0.5;
        }
        vm.$modal('kdls36', param).then((result: any) => {
            if (result) {
                vm.absHolidayMngLst = result.mngDisp;
                vm.formatMngLst();
            }
        });
    }

    public openKDLS35Leave() {
        const vm = this;
        let actualContentDispList = vm.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst,
            param: any = {
                employeeId: vm.user.employeeId,
                period: { startDate: vm.leaveDate, endDate: vm.leaveDate },
                targetSelectionAtr: 1,
                actualContentDisplayList: actualContentDispList ? actualContentDispList : [],
                managementData: vm.absWorkMngLst,
            },
            workType: any = _.find(vm.displayInforWhenStarting.applicationForHoliday.workTypeList, 
            (o) => o.workTypeCode == vm.leaveWorkInfo.workTypeCD);
        if (workType.workAtr == 0) {
            param.daysUnit = 1;
        } else {
            param.daysUnit = 0.5;
        }
        vm.$modal('kdls35', param).then((result: any) => {
            if (result) {
                vm.absWorkMngLst = result.mngDisp;
                vm.formatMngLst();
            }
        });
    }

    private formatMngLst() {
        const vm = this;
        _.forEach(vm.recHolidayMngLst, (item) => {
            item.outbreakDay = new Date(item.outbreakDay).toISOString();
            item.dateOfUse = new Date(item.dateOfUse).toISOString();
        });
        _.forEach(vm.absHolidayMngLst, (item) => {
            item.outbreakDay = new Date(item.outbreakDay).toISOString();
            item.dateOfUse = new Date(item.dateOfUse).toISOString();
        });
        _.forEach(vm.absWorkMngLst, (item) => {
            item.outbreakDay = new Date(item.outbreakDay).toISOString();
            item.dateOfUse = new Date(item.dateOfUse).toISOString();
        });
    }

    public customValidate(viewModel: any) {
        const vm = this;
        let validAllChild = true;
        for (let child of viewModel.$children) {
            let validChild = true;
            if (child.$children) {
                validChild = vm.customValidate(child); 
            }
            child.$validate();
            if (!child.$valid || !validChild) {
                validAllChild = false;
            }
        }

        return validAllChild;
    }

    public register() {
        const vm = this;
        vm.isValidateAll = vm.customValidate(vm);
        vm.$validate();
        if (!vm.$valid || !vm.isValidateAll) {
            window.scrollTo(500, 0);
            
            return;
        }
        if (vm.mode == ScreenMode.NEW) {
            if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT_LEAVE) {
                if (_.isEmpty(vm.complementWorkInfo.workTypeCD) || _.isEmpty(vm.complementWorkInfo.workTimeCD)) {
                    vm.$modal.error({ messageId: 'Msg_218', messageParams: [vm.$i18n('KAFS11_9')] });
        
                    return;
                }
                if (_.isEmpty(vm.leaveWorkInfo.workTypeCD)) {
                    vm.$modal.error({ messageId: 'Msg_218', messageParams: [vm.$i18n('KAFS11_16')] });
                    
                    return;
                }
            } else if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT) {
                if (_.isEmpty(vm.complementWorkInfo.workTypeCD) || _.isEmpty(vm.complementWorkInfo.workTimeCD)) {
                    vm.$modal.error({ messageId: 'Msg_218', messageParams: [vm.$i18n('KAFS11_9')] });
        
                    return;
                }
            } else {
                if (_.isEmpty(vm.leaveWorkInfo.workTypeCD)) {
                    vm.$modal.error({ messageId: 'Msg_218', messageParams: [vm.$i18n('KAFS11_16')] });
                    
                    return;
                }
            }
        }
        vm.$mask('show');
        let command = vm.getCommandSubmit();
        vm.$http.post('at', API.checkBeforeSubmit, command)
        .then((result: any) => {
            if (result) {
                // xử lý confirmMsg
                return vm.handleConfirmMessage(result.data);
            }
        }).then((result: any) => {
            if (result) {
                // đăng kí 
                return vm.$http.post('at', API.submit, command).then((data: any) => {
                    vm.$http.post('at', API.reflectApp, data.data.reflectAppIdLst);

                    return data;
                });
            }
        }).then((result: any) => {
            if (result) {
                return result;
            }
        }).then((result: any) => {
            if (result) {
                // vm.$goto('kafs11a1', { mode: vm.mode, appID: result.data.appID });
                vm.$goto('kafs11a1', { mode: vm.mode, appID: result.data.appIDLst[0] });
            }
        }).catch((failData) => {
            // xử lý lỗi nghiệp vụ riêng
            vm.handleErrorCustom(failData).then((result: any) => {
                if (result) {
                    // xử lý lỗi nghiệp vụ chung
                    vm.handleErrorCommon(failData);
                }
            });
        }).then(() => {
            vm.$mask('hide');    	
        });    
    }

    public handleErrorCustom(failData: any): any {
        const vm = this;

        return new Promise((resolve) => {
            if (failData.messageId == 'Msg_197') {
                vm.$modal.error({ messageId: 'Msg_197', messageParams: [] }).then(() => {
                    let appID = vm.mode == ScreenMode.NEW ? '' : vm.displayInforWhenStarting.appDispInfoStartup.appDetailScreenInfo.application.appID;
                    vm.$modal('cmms45c', { 'listAppMeta': [appID], 'currentApp': appID }).then((newData: KAFS11Params) => {
                        vm.params = newData;
                        vm.initFromParam();
                    });
                });
    
                return;
            }
            if (failData.messageId == 'Msg_323') {
                vm.$modal.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
                .then(() => {
                    if (vm.displayInforWhenStarting.appDispInfoStartup.appDispInfoNoDateOutput.applicationSetting.recordDate == 0) {
                        vm.$goto('ccg008a');
                    }
                });

                return resolve(false);		
            }

            return resolve(true);
        });
    }

    public handleConfirmMessage(listMes: any): any {
        const vm = this;

        return new Promise((resolve) => {
            if (_.isEmpty(listMes)) {
                return resolve(true);
            }
            let msg = listMes[0];
    
            return vm.$modal.confirm({ messageId: msg.msgID, messageParams: msg.paramLst })
            .then((value) => {
                if (value === 'yes') {
                    return vm.handleConfirmMessage(_.drop(listMes)).then((result) => {
                        if (result) {
                            return resolve(true);    
                        }

                        return resolve(false);
                    });
                }
                
                return resolve(false);
            });
        });
    }

    private getCommandSubmit() {
        const vm = this;
        let cmd: any = {
            newMode: vm.mode == ScreenMode.NEW,
            displayInforWhenStarting: vm.displayInforWhenStarting,
            recHolidayMngLst: vm.recHolidayMngLst,
            absHolidayMngLst: vm.absHolidayMngLst,
            absWorkMngLst: vm.absWorkMngLst
        };
        if (vm.mode == ScreenMode.NEW) {
            if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT_LEAVE || vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT) {
                cmd.rec = {
                    applicationInsert: {
                        prePostAtr: vm.prePostAtr,
                        appType: AppType.COMPLEMENT_LEAVE_APPLICATION,
                        appDate: moment(vm.complementDate).format('YYYY/MM/DD'),
                        opAppReason: vm.opAppReason,
                        opAppStandardReasonCD: vm.opAppStandardReasonCD,
                        opAppStartDate: moment(vm.complementDate).format('YYYY/MM/DD'),
                        opAppEndDate: moment(vm.complementDate).format('YYYY/MM/DD')
                    },
                    workInformation: {
                        workType: vm.complementWorkInfo.workTypeCD,
                        workTime: vm.complementWorkInfo.workTimeCD
                    },
                    workingHours: []
                };
            }
            if (vm.complementLeaveAtr == ComplementLeaveAtr.COMPLEMENT_LEAVE || vm.complementLeaveAtr == ComplementLeaveAtr.LEAVE) {
                cmd.abs = {
                    applicationInsert: {
                        prePostAtr: vm.prePostAtr,
                        appType: AppType.COMPLEMENT_LEAVE_APPLICATION,
                        appDate: moment(vm.leaveDate).format('YYYY/MM/DD'),
                        opAppReason: vm.opAppReason,
                        opAppStandardReasonCD: vm.opAppStandardReasonCD,
                        opAppStartDate: moment(vm.leaveDate).format('YYYY/MM/DD'),
                        opAppEndDate: moment(vm.leaveDate).format('YYYY/MM/DD')
                    },
                    workInformation: {
                        workType: vm.leaveWorkInfo.workTypeCD,
                        workTime: vm.leaveWorkInfo.workTimeCD
                    },
                    workingHours: [],
                    workChangeUse: _.isEmpty(vm.leaveWorkInfo.workTimeCD) ? false : true
                };
                if (!vm.dispLeaveWorkTime) {
                    cmd.abs.workInformation.workTime = null;
                }
            }
        } else {
            if (vm.displayInforWhenStarting.rec) {
                cmd.rec = {
                    application: vm.displayInforWhenStarting.rec.application,
                    applicationUpdate: {
                        opAppReason: vm.opAppReason,
                        opAppStandardReasonCD: vm.opAppStandardReasonCD
                    },
                    workInformation: {
                        workType: vm.complementWorkInfo.workTypeCD,
                        workTime: vm.complementWorkInfo.workTimeCD
                    },
                    workingHours: []
                };
                cmd.recOldHolidayMngLst = vm.displayInforWhenStarting.rec.leaveComDayOffMana;
            }
            if (vm.displayInforWhenStarting.abs) {
                cmd.abs = {
                    application: vm.displayInforWhenStarting.abs.application,
                    applicationUpdate: {
                        opAppReason: vm.opAppReason,
                        opAppStandardReasonCD: vm.opAppStandardReasonCD
                    },
                    workInformation: {
                        workType: vm.leaveWorkInfo.workTypeCD,
                        workTime: vm.leaveWorkInfo.workTimeCD
                    },
                    workingHours: [],
                    workChangeUse: _.isEmpty(vm.leaveWorkInfo.workTimeCD) ? false : true
                };
                if (!vm.dispLeaveWorkTime) {
                    cmd.abs.workInformation.workTime = null;
                }
                cmd.absOldHolidayMngLst = vm.displayInforWhenStarting.abs.leaveComDayOffMana;
                cmd.absOldWorkMngLst = vm.displayInforWhenStarting.abs.payoutSubofHDManagements;
            }
        }
        if (cmd.rec) {
            if (!_.isNull(vm.complementWorkInfo.timeRange1.start) && !_.isNull(vm.complementWorkInfo.timeRange1.end)) {
                cmd.rec.workingHours.push({
                    workNo: 1,
                    timeZone: {
                        startTime: vm.complementWorkInfo.timeRange1.start,
                        endTime: vm.complementWorkInfo.timeRange1.end
                    }
                });
            }
            if (!_.isNull(vm.complementWorkInfo.timeRange2.start) && !_.isNull(vm.complementWorkInfo.timeRange2.end)) {
                cmd.rec.workingHours.push({
                    workNo: 2,
                    timeZone: {
                        startTime: vm.complementWorkInfo.timeRange2.start,
                        endTime: vm.complementWorkInfo.timeRange2.end
                    }
                });
            }
        }
        if (cmd.abs) {
            if (cmd.abs.workChangeUse) {
                if (!_.isNull(vm.leaveWorkInfo.timeRange1.start) && !_.isNull(vm.leaveWorkInfo.timeRange1.end)) {
                    cmd.abs.workingHours.push({
                        workNo: 1,
                        timeZone: {
                            startTime: vm.leaveWorkInfo.timeRange1.start,
                            endTime: vm.leaveWorkInfo.timeRange1.end
                        }
                    });
                }
                if (!_.isNull(vm.leaveWorkInfo.timeRange2.start) && !_.isNull(vm.leaveWorkInfo.timeRange2.end)) {
                    cmd.abs.workingHours.push({
                        workNo: 2,
                        timeZone: {
                            startTime: vm.leaveWorkInfo.timeRange2.start,
                            endTime: vm.leaveWorkInfo.timeRange2.end
                        }
                    });
                }
            }
        }

        return cmd;
    }

    @Watch('params')
    public paramsWatcher() {
        const vm = this;
        vm.initFromParam();
    }
}

const API = {
    start: 'at/request/application/holidayshipment/mobile/start',
    changeRecDate: 'at/request/application/holidayshipment/changeRecDate',
    changeAbsDate: 'at/request/application/holidayshipment/changeAbsDate',
    getWorkTimeByCDLst: 'at/shared/worktimesetting/get_worktime_by_codes',
    checkBeforeSubmit: 'at/request/application/holidayshipment/mobile/checkBeforeSubmit',
    submit: 'at/request/application/holidayshipment/mobile/submit',
    getTimeZoneValue: 'at/request/application/holidayshipment/mobile/getTimeZoneValue',
    reflectApp: 'at/request/application/reflect-app'
};

export interface KAFS11Params {
    appDispInfoStartupOutput: any;
    appDetail: any;
    isDetailMode: boolean;
}

interface WorkInfo {
    workTypeCD: string;
    workTimeCD: string;
    timeRange1: any;
    timeRange2: any;
}

enum ComplementLeaveAtr {
    // 振休＋振出
    COMPLEMENT_LEAVE = 0,
    // 振出
    COMPLEMENT = 1,
    // 振休
    LEAVE = 2
}
