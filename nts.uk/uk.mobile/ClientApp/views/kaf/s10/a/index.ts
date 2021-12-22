import { _, Vue } from '@app/provider';
import { PrePostAtr, AppDateContradictionAtr, ParamChangeWorkMobile, OverTimeShiftNight, TrackRecordAtr, BreakTime, TimeZoneNew, TimeZoneWithWorkNo, AppOverTime, ParamCalculateMobile, ParamSelectWorkMobile, InfoWithDateApplication, ParamStartMobile, OvertimeAppAtr, Model, NotUseAtr, ApplicationTime, OvertimeApplicationSetting, AttendanceType, HolidayMidNightTime, StaturoryAtrOfHolidayWork, ParamBreakTime, WorkInformation, WorkHoursDto, AppHolidayWork, AppHdWorkDispInfo, HdWorkDispInfoWithDateOutput } from '../a/define.interface';
import { component, Prop, Watch } from '@app/core/component';
import { StepwizardComponent } from '@app/components';
import { KafS10Step1Component } from '../step1';
import { HolidayTime, KafS10Step2Component } from '../step2';
import { KafS10Step3Component } from '../step3';
import { KDL002Component } from '../../../kdl/002';
import { Kdl001Component } from '../../../kdl/001';
import { KafS00ShrComponent, AppType, Application, InitParam } from 'views/kaf/s00/shr';
import { OverTime } from '../step2/index';
import { OverTimeWorkHoursDto } from '../../s00/sub/p2';
import { ExcessTimeStatus } from '../../s00/sub/p1';
import { CmmS45CComponent } from '../../../cmm/s45/c/index';

@component({
    name: 'kafs10a',
    route: '/kaf/s10/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: [],
    components: {
        'step-wizard': StepwizardComponent,
        'kafS10Step1Component': KafS10Step1Component,
        'kafS10Step2Component': KafS10Step2Component,
        'kafS10Step3Component': KafS10Step3Component,
        'worktype': KDL002Component,
        'worktime': Kdl001Component,
        'cmms45c': CmmS45CComponent
    }
})
export class KafS10Component extends KafS00ShrComponent {
    public title: string = 'KafS10A';

    private numb: number = 1;
    public isValidateAll: boolean = true;
    public modeNew: boolean = true;
    public appId: string;
    public application: Application = null;
    public user: any = null;
    public date: string;

    public model: Model = {} as Model;

    public isMsg_1556: boolean = false;
    public isMsg_1557: boolean = false;
    
    @Prop()
    public params: InitParam;

    public get getNumb(): number {
        const self = this;

        return self.numb;
    }

    @Watch('numb', {deep: true})
    public changeNumb(data: any) {
        const self = this;

        if (self.numb == 1) {
            self.pgName = 'kafs10step1';
        } else if (self.numb == 2) {
            self.pgName = 'kafs10step2';
        } else {
            self.pgName = 'kafs10step3';
        }
    }

    public get step() {
        return `step_${this.numb}`;
    }

    public get overTimeWorkHoursDto(): OverTimeWorkHoursDto {
        const self = this;
        let model = self.model as Model;
        
        return _.get(model, 'appHdWorkDispInfo.otWorkHoursForApplication') || null;
    }

    public created() {
        const vm = this;

        if (!_.isNil(vm.params) && vm.params.isDetailMode) {
            vm.modeNew = false;
            let model = {} as Model;
            model.appHolidayWork = vm.params.appDetail.appHolidayWork as AppHolidayWork;
            model.appHdWorkDispInfo = vm.params.appDetail.appHdWorkDispInfo as AppHdWorkDispInfo;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
            vm.model = model;
        }
    }
    public mounted() {
        const vm = this;
        vm.fetchData();
    }
    public fetchData() {
        const vm = this;
        vm.$mask('show');
        if (vm.modeNew) {
            vm.application = vm.createApplicationInsert(AppType.LEAVE_TIME_APPLICATION);
            vm.application.employeeIDLst = _.get(vm.params, 'employeeID') ? [_.get(vm.params, 'employeeID')] : [];
            vm.application.appDate = _.get(vm.params, 'date');
        } else {
            vm.application = vm.createApplicationUpdate(vm.appDispInfoStartupOutput.appDetailScreenInfo);
        }
        vm.$auth.user.then((user: any) => {
            vm.user = user;
        }).then(() => {
            if (vm.modeNew) {
                return vm.loadCommonSetting(
                    AppType.LEAVE_TIME_APPLICATION,
                    _.isEmpty(vm.application.employeeIDLst) ? null : vm.application.employeeIDLst[0], 
                    null, 
                    vm.application.appDate ? [vm.application.appDate] : [], 
                    null);
            }

            return true;
        }).then((loadData: any) => {
            if (loadData) {
                vm.updateKaf000_A_Params(vm.user);
                vm.updateKaf000_B_Params(vm.modeNew, vm.application.appDate);
                vm.updateKaf000_C_Params(vm.modeNew);
                if (vm.modeNew) {
                    vm.kaf000_B_Params.newModeContent.useMultiDaySwitch = false;
                }
                let command = {} as ParamStartMobile;
                let url = self.location.search.split('=')[1];
                command.mode = vm.modeNew;
                command.companyId = vm.user.companyId;
                command.employeeId = !_.isEmpty(vm.application.employeeIDLst) ? vm.application.employeeIDLst[0] : vm.user.employeeId;
                command.appDispInfoStartupOutput = vm.appDispInfoStartupOutput;
                command.appDate = vm.application.appDate;

                if (vm.modeNew) {
                    return vm.$http.post('at', API.start, command);
                }

                return true;
            }
        }).then((result: any) => {
            if (result) {
                if (vm.modeNew) {
                    let modelClone = {} as Model;
                    modelClone.appHdWorkDispInfo = result.data.appHdWorkDispInfo;
                    vm.model = modelClone;
                }
                let step1 = vm.$refs.step1 as KafS10Step1Component;
                step1.loadData(vm.model.appHdWorkDispInfo);
                step1.createHoursWorkTime();
            }
        }).catch((error: any) => {
            vm.handleErrorCustom(error).then((result) => {
                if (result) {
                    vm.handleErrorCommon(error);
                }
            });
        }).then(() => vm.$mask('hide'));
    }

    public changeDate(date: string) {
        const self = this;
        if (!self.modeNew) {

            return;
        }
        self.$mask('show');
        let command = {
            companyId: self.user.companyId,
            appDate: date,
            appHdWorkDispInfo: self.model.appHdWorkDispInfo
        };
        self.$http.post('at',
            API.changeDate,
            command
        ).then((res: any) => {
            self.model.appHdWorkDispInfo = res.data;
            let step1 = self.$refs.step1 as KafS10Step1Component;
            step1.loadData(self.model.appHdWorkDispInfo);
            step1.createHoursWorkTime();
            // エラーメッセージ(Msg_1556)を画面項目「A_A3_1」に表示する
            // 帰ってきた「休日出勤申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．表示する実績内容．実績詳細」== empty
            let c1 = _.isNil(_.get(self.model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail'));
            // 帰ってきた「休日出勤申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．表示する実績内容．実績詳細．実績スケ区分」= スケジュール
            let c2 = false;
            if (!c1) {
                c2 = _.get(self.model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.trackRecordAtr') == TrackRecordAtr.SCHEDULE;
            }
            // 帰ってきた「休日出勤申請起動時の表示情報．休出申請設定．残業休出申請共通設定．実績超過区分」= チェックする（登録不可）
            let c3 = _.get(self.model, 'appHdWorkDispInfo.holidayWorkAppSet.overtimeLeaveAppCommonSet.performanceExcessAtr') == AppDateContradictionAtr.CHECKNOTREGISTER;
            if ((c1 || c2) && c3) {
                self.isMsg_1556 = true;
            } else {
                self.isMsg_1556 = false;
            }
            self.$mask('hide');
        }).catch((res: any) => {
            self.$nextTick(() => {
                self.$mask('hide');
            });
            // xử lý lỗi nghiệp vụ riêng
            self.handleErrorCustom(res).then((result: any) => {
                if (result) {
                    // xử lý lỗi nghiệp vụ chung
                    self.handleErrorCommon(res);
                }
            });
        });
    }

    public toStep(value: number) {
        const vm = this;
        // step 1 -> step 2
        if (vm.numb == 1 && value == 2) {
            vm.$mask('show');
            let step1 = vm.$refs.step1 as KafS10Step1Component;
            vm.isValidateAll = vm.customValidate(step1);
            step1.$validate();
            window.scrollTo(500, 0);
            if (!vm.$valid || !vm.isValidateAll || !step1.$valid) {
                vm.$nextTick(() => {
                    vm.$mask('hide');
                });

                return;
            }
            let command = {} as ParamCalculateMobile;
            vm.model.appHolidayWork = vm.toAppHolidayWork();
            command.companyId = vm.user.companyId;
            command.employeeId = vm.user.employeeId;
            command.appDate = vm.modeNew ? vm.date : vm.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;
            command.mode = vm.modeNew;
            command.appHdWorkDispInfo = vm.model.appHdWorkDispInfo;
            command.isAgent = false;
            if (vm.modeNew) {
                command.appHolidayWorkInsert = vm.model.appHolidayWork;
            } else {
                command.appHolidayWorkUpdate = vm.model.appHolidayWork;
            }
            vm.$http.post(
                'at',
                API.calculate,
                command
            ).then((res: any) => {
                vm.model.appHdWorkDispInfo = res.data;
                vm.isMsg_1557 = _.get(vm.model, 'appHdWorkDispInfo.calculationResult.actualOvertimeStatus.isExistApp');
                vm.isMsg_1556 = _.get(vm.model, 'appHdWorkDispInfo.calculationResult.actualOvertimeStatus.achivementStatus') == ExcessTimeStatus.ALARM;
                vm.numb = value;
                let step2 = vm.$refs.step2 as KafS10Step2Component;
                step2.loadAllData();
                vm.$nextTick(() => {
                    vm.$mask('hide');
                    step2.$forceUpdate();
                });
            }).catch((res: any) => {
                vm.$nextTick(() => {
                    vm.$mask('hide');
                });
                // xử lý lỗi nghiệp vụ riêng
                vm.handleErrorCustom(res).then((result: any) => {
                    if (result) {
                        // xử lý lỗi nghiệp vụ chung
                        vm.handleErrorCommon(res);
                    }
                });
            });
        } else if (vm.numb == 2 && value == 1) { // step 2 -> step 1
            let step2 = vm.$refs.step2 as KafS10Step2Component;
            step2.holidayTimes = [];
            step2.overTimes = [];
            vm.numb = value;
        } else if (vm.numb == 2 && value == 3) {
            let step3 = vm.$refs.step3 as KafS10Step3Component;
            // step3.setParam();
            vm.numb = value;
        } else {
            vm.numb = value;

        }

    }

    public toAppHolidayWork() {
        const self = this;
        let step1 = self.$refs.step1 as KafS10Step1Component;
        let appHolidayWork = self.modeNew ? {} as AppHolidayWork : self.model.appHolidayWork;
        if (step1) {
            if (self.modeNew) {
                if (self.model.appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.prePostDisplayAtr != 1) {
                    self.application.prePostAtr = self.model.appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.prePostAtr;
                }
                appHolidayWork.application = self.application as any;
            } else {
                appHolidayWork.application = self.model.appHdWorkDispInfo.appDispInfoStartupOutput.appDetailScreenInfo.application;
            }
            if (step1.getWorkType() && step1.getWorkTime()) {
                appHolidayWork.workInformation = {} as WorkInformation;
                appHolidayWork.workInformation.workType = step1.getWorkType();
                appHolidayWork.workInformation.workTime = step1.getWorkTime();
            }
            appHolidayWork.goWorkAtr = step1.goWorkAtr;
            appHolidayWork.backHomeAtr = step1.backHomeAtr;
            appHolidayWork.workingTimeList = [] as Array<TimeZoneWithWorkNo>;
            {   
                let start = _.get(step1.getWorkHours1(), 'start');
                let end = _.get(step1.getWorkHours1(), 'end');
                if (_.isNumber(start) && _.isNumber(end)) {
                    let timeZone = {} as TimeZoneWithWorkNo;
                    timeZone.workNo = 1;
                    timeZone.timeZone = {} as TimeZoneNew;
                    timeZone.timeZone.startTime = start;
                    timeZone.timeZone.endTime = end;
                    appHolidayWork.workingTimeList.push(timeZone);
                }
                if (self.c3) {
                    let start = _.get(step1.getWorkHours2(), 'start');
                    let end = _.get(step1.getWorkHours2(), 'end');
                    if (_.isNumber(start) && _.isNumber(end)) {
                        let timeZone = {} as TimeZoneWithWorkNo;
                        timeZone.workNo = 2;
                        timeZone.timeZone = {} as TimeZoneNew;
                        timeZone.timeZone.startTime = start;
                        timeZone.timeZone.endTime = end;
                        appHolidayWork.workingTimeList.push(timeZone);
                    }
                }
            }
            if (self.c4) {
                appHolidayWork.breakTimeList = [] as Array<TimeZoneWithWorkNo>;
                let breakTimes = step1.getBreakTimes() as Array<BreakTime>;
                _.forEach(breakTimes, (item: BreakTime, index: number) => {
                    let start = _.get(item, 'valueHours.start');
                    let end = _.get(item, 'valueHours.end');
                    if (_.isNumber(start) && _.isNumber(end)) {
                        let timeZone = {} as TimeZoneWithWorkNo;
                        timeZone.workNo = index + 1;
                        timeZone.timeZone = {} as TimeZoneNew;
                        timeZone.timeZone.startTime = start;
                        timeZone.timeZone.endTime = end;
                        appHolidayWork.breakTimeList.push(timeZone);
                    }
                });
            }
        }

        return appHolidayWork;
    }

    public toAppHolidayWorkForRegister() {
        const self = this;
        let appHolidayWork = self.model.appHolidayWork as AppHolidayWork;
        let step2 = self.$refs.step2 as KafS10Step2Component;

        let overTimes = step2.overTimes as Array<OverTime>;
        let holidayTimes = step2.holidayTimes as Array<HolidayTime>;
        let applicationTime = appHolidayWork.applicationTime = {} as ApplicationTime;
        let applicationTimes = applicationTime.applicationTime = [] as Array<OvertimeApplicationSetting>;
        _.forEach(overTimes, (item: OverTime) => {
            // AttendanceType.NORMALOVERTIME
            if (item.type == AttendanceType.NORMALOVERTIME && item.applicationTime > 0) {
                let overtimeApplicationSetting = {} as OvertimeApplicationSetting;
                overtimeApplicationSetting.attendanceType = AttendanceType.NORMALOVERTIME;
                overtimeApplicationSetting.frameNo = Number(item.frameNo);
                overtimeApplicationSetting.applicationTime = item.applicationTime;
                applicationTimes.push(overtimeApplicationSetting);
            }
            if (item.type == AttendanceType.MIDNIGHT_OUTSIDE && item.applicationTime > 0) {
                if (_.isNil(applicationTime.overTimeShiftNight)) {
                    applicationTime.overTimeShiftNight = {} as OverTimeShiftNight;
                }
                applicationTime.overTimeShiftNight.overTimeMidNight = item.applicationTime;
            }
        });
        _.forEach(holidayTimes, (item: HolidayTime) => {
            // AttendanceType.BREAKTIME
            if (item.type == AttendanceType.BREAKTIME && item.applicationTime > 0) {
                let overtimeApplicationSetting = {} as OvertimeApplicationSetting;
                overtimeApplicationSetting.attendanceType = AttendanceType.BREAKTIME;
                overtimeApplicationSetting.frameNo = Number(item.frameNo);
                overtimeApplicationSetting.applicationTime = item.applicationTime;
                applicationTimes.push(overtimeApplicationSetting);
            }
            if (item.type == AttendanceType.MIDDLE_BREAK_TIME && item.applicationTime > 0) {
                self.toHolidayMidNightTime(item, applicationTime);
            } else if (item.type == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY && item.applicationTime > 0) {
                self.toHolidayMidNightTime(item, applicationTime);
            } else if (item.type == AttendanceType.MIDDLE_HOLIDAY_HOLIDAY && item.applicationTime > 0) {
                self.toHolidayMidNightTime(item, applicationTime);
            }
        });
        appHolidayWork.applicationTime.reasonDissociation = [step2.getReasonDivergence()];
        if (!self.modeNew) {
            appHolidayWork.application.opAppReason = self.application.opAppReason || self.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppReason as any;
            appHolidayWork.application.opAppStandardReasonCD = self.application.opAppStandardReasonCD || self.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppStandardReasonCD as any;
        }
        
        // assign value to overtime and holidaytime
        return appHolidayWork;
    }

    public toHolidayMidNightTime(overTime: HolidayTime, applicationTime: ApplicationTime) {
        if (_.isNil(applicationTime.overTimeShiftNight)) {
            applicationTime.overTimeShiftNight = {} as OverTimeShiftNight;
        }
        if (_.isNil(applicationTime.overTimeShiftNight.midNightHolidayTimes)) {
            applicationTime.overTimeShiftNight.midNightHolidayTimes = [] as Array<HolidayMidNightTime>;
        }
        let holidayMidNightTime = {} as HolidayMidNightTime;
        if (overTime.type == AttendanceType.MIDDLE_BREAK_TIME) {
            holidayMidNightTime.legalClf = StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork;
        } else if (overTime.type == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY) {
            holidayMidNightTime.legalClf = StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork;
        } else {
            holidayMidNightTime.legalClf = StaturoryAtrOfHolidayWork.PublicHolidayWork;
        }
        holidayMidNightTime.attendanceTime = overTime.applicationTime;

        return applicationTime.overTimeShiftNight.midNightHolidayTimes.push(holidayMidNightTime);
    }

    public getBreakTime(command: ParamBreakTime) {
        const self = this;
        self.$mask('show');
        self.$http.post(
            'at',
            API.getBreakTime,
            command
        ).then((res: any) => {
            let appHdWorkDispInfo = _.get(self.model, 'appHdWorkDispInfo') as AppHdWorkDispInfo;
            if (!_.isNil(appHdWorkDispInfo)) {
                appHdWorkDispInfo = res.data;
            } else {
                appHdWorkDispInfo = {} as AppHdWorkDispInfo;
                appHdWorkDispInfo = res.data;
            }
            let step1 = self.$refs.step1 as KafS10Step1Component;
            if (!_.isNil(step1)) {
                step1.createBreakTime(appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.breakTimeZoneSettingList.timeZones);
            }

        }).catch((res: any) => {
            self.handleErrorMessage(res);
        }).then(() => self.$mask('hide'));
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

    public openKDLmodal(name: string) {
        const self = this;
        let step1 = self.$refs.step1 as KafS10Step1Component;
        if (name == 'worktype') {
            self.$modal('worktype', {

                seledtedWkTypeCDs: _.map(_.uniqBy(self.model.appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.workTypeList, (e: any) => e.workTypeCode), (item: any) => item.workTypeCode),
                selectedWorkTypeCD: step1.getWorkType(),
                seledtedWkTimeCDs: _.map(self.model.appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst, (item: any) => item.worktimeCode),
                selectedWorkTimeCD: step1.getWorkTime(),
                isSelectWorkTime: 1,
            }).then((f: any) => {
                    let workTypeCode;
                    let workTimeCode;
                    workTypeCode = f.selectedWorkType.workTypeCode;
                    workTimeCode = f.selectedWorkTime.code;
                    step1.setWorkInfo(
                        workTypeCode,
                        f.selectedWorkType.name,
                        workTimeCode,
                        f.selectedWorkTime.name,
                    );
                    let command = {} as ParamChangeWorkMobile;
                    command.companyId = self.user.companyId;
                    command.appDate = self.modeNew ? self.date : self.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;
                    command.workTypeCode = step1.workInfo.workType.code;
                    command.workTimeCode = step1.workInfo.workTime.code;
                    // command.startTime = step1.workHours1.start;
                    // command.endTime = step1.workHours1.end;
                    command.appHdWorkDispInfo = self.model.appHdWorkDispInfo;
                    self.$mask('show');

                    return self.$http.post(
                        'at',
                        API.selectWorkInfo,
                        command
                    );

                }).then((res: any) => {
                    let step1 = self.$refs.step1 as KafS10Step1Component;
                    // call API select work info
                    let appHdWorkDispInfo = _.get(self.model, 'appHdWorkDispInfo') as AppHdWorkDispInfo;
                    if (!_.isNil(appHdWorkDispInfo)) {
                        appHdWorkDispInfo = res.data;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkType = step1.workInfo.workType.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTypeName = step1.workInfo.workType.name;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTime = step1.workInfo.workTime.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTimeName = step1.workInfo.workTime.name;
                    } else {
                        appHdWorkDispInfo = {} as AppHdWorkDispInfo;
                        appHdWorkDispInfo = res.data;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkType = step1.workInfo.workType.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTypeName = step1.workInfo.workType.name;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTime = step1.workInfo.workTime.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTimeName = step1.workInfo.workTime.name;
                    }
                    self.model.appHdWorkDispInfo = appHdWorkDispInfo;
                    step1.loadData(self.model.appHdWorkDispInfo, true);
                    step1.createHoursWorkTime();
                }).catch((res: any) => {
                    self.handleErrorMessage(res);
                }).then(() => self.$mask('hide'));
        } else {
            self.$modal(
                'worktime',
                {
                    isAddNone: 0,
                    seledtedWkTimeCDs: _.map(self.model.appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst, (item: any) => item.worktimeCode),
                    selectedWorkTimeCD: step1.getWorkTime(),
                    isSelectWorkTime: 1
                }
            ).then((f: any) => {
                if (f) {
                    step1.setWorkTime(
                        f.selectedWorkTime.code,
                        f.selectedWorkTime.name,
                    );

                    let command = {} as ParamChangeWorkMobile;
                    command.companyId = self.user.companyId;
                    command.appDate = self.modeNew ? self.date : self.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;
                    command.workTypeCode = step1.workInfo.workType.code;
                    command.workTimeCode = step1.workInfo.workTime.code;
                    // command.startTime = step1.workHours1.start;
                    // command.endTime = step1.workHours1.end;
                    command.appHdWorkDispInfo = self.model.appHdWorkDispInfo;
                    self.$mask('show');

                    return self.$http.post(
                        'at',
                        API.selectWorkInfo,
                        command
                    );
                }
            }).then((res: any) => {
                    // call API select work info
                    let step1 = self.$refs.step1 as KafS10Step1Component;
                    let appHdWorkDispInfo = _.get(self.model, 'appHdWorkDispInfo') as AppHdWorkDispInfo;
                    if (!_.isNil(appHdWorkDispInfo)) {
                        appHdWorkDispInfo = res.data;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkType = step1.workInfo.workType.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTypeName = step1.workInfo.workType.name;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTime = step1.workInfo.workTime.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTimeName = step1.workInfo.workTime.name;
                    } else {
                        appHdWorkDispInfo = {} as AppHdWorkDispInfo;
                        appHdWorkDispInfo = res.data;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkType = step1.workInfo.workType.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTypeName = step1.workInfo.workType.name;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTime = step1.workInfo.workTime.code;
                        appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.initWorkTimeName = step1.workInfo.workTime.name;
                    }
                    self.model.appHdWorkDispInfo = appHdWorkDispInfo;
                    step1.loadData(self.model.appHdWorkDispInfo, true);
                    step1.createHoursWorkTime();
            }).catch((res: any) => {
                    self.handleErrorMessage(res);
            }).then(() => self.$mask('hide'));
        }
    }

    public register() {
        const vm = this;
        vm.$mask('show');
        let step2 = vm.$refs.step2 as KafS10Step2Component;
        vm.isValidateAll = vm.customValidate(step2);
        // step2.$validate();
        if (!step2.$valid || !vm.isValidateAll) {
            window.scrollTo(500, 0);
            vm.$nextTick(() => vm.$mask('hide'));

            return;
        }
        vm.model.appHolidayWork = vm.toAppHolidayWorkForRegister();
        vm.$http.post('at', API.checkBeforeRegister, {
            require: true,
            mode: vm.modeNew,
            companyId: vm.user.companyId,
            appHdWorkDispInfo: vm.model.appHdWorkDispInfo,
            appHolidayWorkInsert: vm.model.appHolidayWork,
            appHolidayWorkUpdate: vm.model.appHolidayWork,
        }).then((result: any) => {
            if (result) {
                // xử lý confirmMsg
                return vm.handleConfirmMessage(result.data.confirmMsgOutputs);
            }
        }).then((result: any) => {
            if (result) {
                // đăng kí 
                return vm.$http.post('at', API.register, {
                    mode: vm.modeNew,
                    companyId: vm.user.companyId,
                    appHolidayWorkInsert: vm.model.appHolidayWork,
                    appHolidayWorkUpdate: vm.model.appHolidayWork,
                    appTypeSetting: vm.model.appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting[0],
                    appHdWorkDispInfo: vm.model.appHdWorkDispInfo
                }).then((result: any) => {
                    vm.$http.post('at', API.reflectApp, result.data.reflectAppIdLst);
                    vm.appId = result.data.appIDLst[0];
                    vm.toStep(3);
                });
            }
        }).then((result: any) => {
            if (result) {
                // gửi mail sau khi đăng kí
                // return vm.$ajax('at', API.sendMailAfterRegisterSample);
                return true;
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
            vm.$nextTick(() => vm.$mask('hide'));
        });
    }

    public kaf000BChangeDate(objectDate) {
        const self = this;
        if (objectDate.startDate) {
            if (self.modeNew) {
                self.application.appDate = self.$dt.date(objectDate.startDate, 'YYYY/MM/DD');
                self.application.opAppStartDate = self.$dt.date(objectDate.startDate, 'YYYY/MM/DD');
                self.application.opAppEndDate = self.$dt.date(objectDate.endDate, 'YYYY/MM/DD');
                self.date = objectDate.startDate;
            }
            self.changeDate(objectDate.startDate);
        }
    }

    public kaf000BChangePrePost(prePostAtr) {
        const self = this;
        self.application.prePostAtr = prePostAtr;
    }

    public kaf000CChangeReasonCD(opAppStandardReasonCD) {
        const self = this;
        self.application.opAppStandardReasonCD = opAppStandardReasonCD;
    }

    public kaf000CChangeAppReason(opAppReason) {
        const self = this;
        self.application.opAppReason = opAppReason;
    }

    public handleErrorMessage(res: any) {
        const self = this;
        self.$mask('hide');
        if (res.messageId) {
            return self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {

            if (_.isArray(res.errors)) {
                return self.$modal.error({ messageId: res.errors[0].messageId, messageParams: res.parameterIds });
            } else {
                return self.$modal.error({ messageId: res.errors.messageId, messageParams: res.parameterIds });
            }
        }
    }

    public handleErrorCustom(failData: any): any {
        const vm = this;

        return new Promise((resolve) => {
            switch (failData.messageId) {
                case 'Msg_197':
                    vm.$modal.error({ messageId: 'Msg_197', messageParams: [] }).then(() => {
                        let appID = vm.appDispInfoStartupOutput.appDetailScreenInfo.application.appID;
                        vm.$modal('cmms45c', { 'listAppMeta': [appID], 'currentApp': appID }).then((newData: InitParam) => {
                            vm.params = newData;
                            vm.modeNew = false;
                            let model = {} as Model;
                            model.appHolidayWork = vm.params.appDetail.appHolidayWork as AppHolidayWork;
                            model.appHdWorkDispInfo = vm.params.appDetail.appHdWorkDispInfo as AppHdWorkDispInfo;
                            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
                            vm.model = model;
                            vm.fetchData();
                        });
                    });
        
                    return resolve(false);
                case 'Msg_26':
                    vm.$modal.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
                    .then(() => {
                        vm.$goto('ccg008a');
                    });

                    return resolve(false);
                case 'Msg_1556':
                    vm.isMsg_1556 = true;

                    return resolve(false);
                default:

                    return resolve(true);
            }
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

    public backToStep1(res: InitParam) {
        const vm = this;
        vm.toStep(1);
        vm.modeNew = false;
        let model = {} as Model;
        model.appHolidayWork = res.appDetail.appHolidayWork as AppHolidayWork;
        model.appHdWorkDispInfo = res.appDetail.appHdWorkDispInfo as AppHdWorkDispInfo;
        vm.appDispInfoStartupOutput = res.appDispInfoStartupOutput;
        vm.model = model;
        vm.fetchData();
    }

    //  休日出勤申請起動時の表示情報．休出申請設定．残業休出申請共通設定」．時間外表示区分＝表示する
    public get c1() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.overtimeLeaveAppCommonSet.extratimeDisplayAtr');

        return displayAtr == NotUseAtr.USE;
    }

    //  シート「UI処理」の№1
    //  表示条件：「A1_8_1」「A2_8_1」をクリックする時、必須･Primitiveのチェックにエラーがある場合「A1_3_1」を表示する
    public get c2() {
        const self = this;
        let model = self.model as Model;

        return !self.isValidateAll;
    }

    public get c2_A1_3_1() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = self.c2;

        if (displayAtr) {
            //  「休日出勤申請起動時の表示情報．休出申請設定．残業休出申請共通設定」．実績超過区分
            let value = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.overtimeLeaveAppCommonSet.performanceExcessAtr');
            if (value == AppDateContradictionAtr.CHECKNOTREGISTER) {
                return true;
            }
        }
        
        return false;
    }

    public get c2_A1_3_2() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = self.c2;

        if (displayAtr) {
            //  「休日出勤申請起動時の表示情報．休出申請設定．残業休出申請共通設定」．実績超過区分
            let value = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.overtimeLeaveAppCommonSet.performanceExcessAtr');
            if (value == AppDateContradictionAtr.CHECKREGISTER) {
                return true;
            }
        }
        
        return false;
    }

    //  「休日出勤申請起動時の表示情報．申請表示情報．申請設定（基準日関係なし）．複数回勤務の管理」＝TRUE
    public get c3() {
        const self = this;
        let model = self.model as Model;

        return _.get(model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles');
    }

    //  「休日出勤申請起動時の表示情報．残業休日出勤申請の反映．休日出勤申請の反映．事後休日出勤申請の反映．休憩・外出を申請反映する」．休憩を反映する＝反映する
    public get c4() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = _.get(model, 'appHdWorkDispInfo.hdWorkOvertimeReflect.holidayWorkAppReflect.after.breakLeaveApplication.breakReflectAtr');

        return displayAtr == NotUseAtr.USE;
    }

    public get c6() {
        const self = this;
        let model = self.model as Model;
        //  「休日出勤申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係なし)．申請承認設定．申請設定．申請表示設定」．事前事後区分表示＝表示する
        let displayAtr = _.get(model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.prePostDisplayAtr');
        if (self.modeNew) {
            if (displayAtr == NotUseAtr.USE) {
                //「事後」を選択
                return self.application.prePostAtr == PrePostAtr.POST;
            } else {
                //「休日出勤申請起動時の表示情報．申請表示情報．申請設定（基準日関係あり）」．事前事後区分＝事後
                return _.get(model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.prePostAtr') == PrePostAtr.POST;
            }
        } else {
            return _.get(model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.prePostAtr') == PrePostAtr.POST;
        }
    }

    //  「休日出勤申請起動時の表示情報．残業休日出勤申請の反映」．時間外深夜時間を反映する＝反映する
    public get c7() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = _.get(model, 'appHdWorkDispInfo.hdWorkOvertimeReflect.nightOvertimeReflectAtr');

        return displayAtr == NotUseAtr.USE;
    }

    //  ※7 = ○　AND　※6 = ○
    public get c7_1() {
        const self = this;

        return self.c7 && self.c6;
    }

    //  「休日出勤申請起動時の表示情報．休出申請設定．申請詳細設定」．時間入力利用区分 ＝true
    public get c8() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.applicationDetailSetting.timeInputUse');

        return displayAtr == NotUseAtr.USE;
    }

    public get timeCalUse() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.applicationDetailSetting.timeCalUse');

        return displayAtr == NotUseAtr.USE;
    }

    //  「休日出勤の計算結果(従業員)」．申請時間．申請時間．申請時間（type = 残業時間）に項目がある
    public get c9() {
        const self = this;
        let model = self.model as Model;
        if (!_.isNil(_.get(model, 'appHdWorkDispInfo.calculationResult'))) {
            if (!_.isNil(_.get(model, 'appHdWorkDispInfo.calculationResult.applicationTime'))) {
                let applicationTimes = _.get(model, 'appHdWorkDispInfo.calculationResult.applicationTime.applicationTime');
                if (!_.isEmpty(applicationTimes)
                 && applicationTimes.filter((applicationTime) => applicationTime.attendanceType == AttendanceType.NORMALOVERTIME && applicationTime.applicationTime > 0).length > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    //  「休日出勤の計算結果(従業員)」．申請時間．申請時間．申請時間（type = 残業時間）に項目がある
    public get c9_appHolidayWork() {
        const self = this;
        let model = self.model as Model;
        if (!_.isNil(_.get(model, 'appHolidayWork'))) {
            if (!_.isNil(_.get(model, 'appHolidayWork.applicationTime'))) {
                let applicationTimes = _.get(model, 'appHolidayWork.applicationTime.applicationTime');
                if (!_.isEmpty(applicationTimes)
                 && applicationTimes.filter((applicationTime) => applicationTime.attendanceType == AttendanceType.NORMALOVERTIME && applicationTime.applicationTime > 0).length > 0) {
                    return true;
                }
            }
        }
        
        return false;
    }

    //  ※7 = ○　AND　※9 = ○
    public get c9_1() {
        const self = this;

        return self.c7 && (self.c9 || self.c9_appHolidayWork);
    }

    //  ※6 = ○　AND　※7 = ○　AND　※9 = ○
    public get c9_2() {
        const self = this;

        return self.c6 && self.c7 && (self.c9 || self.c9_appHolidayWork);
    }

    //  ※6 = ○　AND　※9 = ○
    public get c9_3() {
        const self = this;

        return self.c6 && (self.c9 || self.c9_appHolidayWork);
    }

    //  ＄：休日出勤申請起動時の表示情報
    //  「＄．利用する乖離理由」<> empty
    //  AND 「＄．利用する乖離理由．乖離理由の選択肢を利用する」がtrue 
    //  AND 「＄．残業休日出勤申請の反映．休日出勤申請の反映．事後休日出勤申請の反映．その他項目の反映．乖離理由を反映する」が反映する
    public get c10() {
        const self = this;
        let model = self.model as Model;
        if (!_.isNil(self.application) && self.application.prePostAtr == 1) {
            if (_.get(model, 'appHdWorkDispInfo.hdWorkOvertimeReflect.holidayWorkAppReflect.after.othersReflect.reflectDivergentReasonAtr') == NotUseAtr.USE && 
                (_.get(model, 'appHdWorkDispInfo.divergenceReasonInputMethod').length > 0 && _.get(model, 'appHdWorkDispInfo.divergenceReasonInputMethod[0].divergenceReasonSelected'))) {
                return true;
            }
        }

        return false;
    }

    //  ＄：休日出勤申請起動時の表示情報
    //  「＄．利用する乖離理由」<> empty
    //  AND 「＄．利用する乖離理由．乖離理由の入力を利用する」がtrue 
    //  AND 「＄．残業休日出勤申請の反映．休日出勤申請の反映．事後休日出勤申請の反映．その他項目の反映．乖離理由を反映する」が反映する
    public get c11() {
        const self = this;
        let model = self.model as Model;
        if (!_.isNil(self.application) && self.application.prePostAtr == 1) {
            if (_.get(model, 'appHdWorkDispInfo.hdWorkOvertimeReflect.holidayWorkAppReflect.after.othersReflect.reflectDivergentReasonAtr') == NotUseAtr.USE && 
                (_.get(model, 'appHdWorkDispInfo.divergenceReasonInputMethod').length > 0 && _.get(model, 'appHdWorkDispInfo.divergenceReasonInputMethod[0].divergenceReasonInputed'))) {
                return true;
            }
        }

        return false;
    }

    //  ※10 = ○　OR　※11 = ○
    public get c11_1() {
        const self = this;
        
        return self.c10 || self.c11;
    }

    //  「休日出勤申請起動時の表示情報．申請表示情報．申請詳細画面情報．申請」．事前事後区分=事後
    public get c12() {
        const self = this;
        let model = self.model as Model;
        let displayAtr = _.get(model, 'appHdWorkDispInfo.appDispInfoStartupOutput.appDetailScreenInfo.application.prePostAtr');

        return displayAtr == PrePostAtr.POST;
    }

    //  ※7 = ○　AND　※12 = ○
    public get c12_1() {
        const self = this;

        return self.c7 && self.c12;
    }

    //  起動時取得した「休日出勤申請」．申請時間．申請時間．申請時間（Type＝残業時間）に項目がある
    public get c13() {
        const self = this;
        let model = self.model as Model;
        if (!_.isNil(_.get(model, 'appHolidayWork'))) {
            if (!_.isNil(_.get(model, 'appHolidayWork.applicationTime'))) {
                let applicationTimes = _.get(model, 'appHolidayWork.applicationTime.applicationTime');
                if (!_.isEmpty(applicationTimes)
                 && applicationTimes.filter((applicationTime) => applicationTime.attendanceType == AttendanceType.NORMALOVERTIME && applicationTime.applicationTime > 0).length > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    //  ※12 = ○　AND　※13 = ○
    public get c13_1() {
        const self = this;

        return self.c12 && self.c13;
    }

    //  ※7 = ○　AND　※13 = ○
    public get c13_2() {
        const self = this;

        return self.c7 && self.c13;
    }

    //  ※7 = ○　AND　※12 = ○　AND　※13 = ○
    public get c13_3() {
        const self = this;

        return self.c7 && self.c12 && self.c13;
    }

    //  「休日出勤申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係なし)．申請承認設定のOutputを利用
    //  「休出申請設定」．直行直帰の機能の利用設定 == true AND 「申請詳細設定．時刻計算利用区分」が利用する
    public get c14() {
        const self = this;
        let model = self.model as Model;
        let useDirectBounceFunction = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.useDirectBounceFunction');
        let timeCalUse = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.applicationDetailSetting.timeCalUse');

        return useDirectBounceFunction == NotUseAtr.USE && timeCalUse == NotUseAtr.USE;
    }


    //  「休日出勤申請起動時の表示情報．休出申請設定．申請詳細設定．時刻計算利用区分」が利用する
    public get c15() {
        const self = this;
        const {model} = self;
        const timeCalUse = _.get(model, 'appHdWorkDispInfo.holidayWorkAppSet.applicationDetailSetting.timeCalUse');

        return timeCalUse == NotUseAtr.USE;

    }


    @Watch('params')
    public paramsWatcher() {
        const vm = this;
        if (!_.isNil(vm.params) && vm.params.isDetailMode) {
            vm.modeNew = false;
            let model = {} as Model;
            model.appHolidayWork = vm.params.appDetail.appHolidayWork as AppHolidayWork;
            model.appHdWorkDispInfo = vm.params.appDetail.appHdWorkDispInfo as AppHdWorkDispInfo;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
            vm.model = model;
        } else {
            vm.modeNew = true;
        }
        vm.fetchData();
    }
}

const API = {
    start: 'at/request/application/holidaywork/mobile/start',
    changeDate: 'at/request/application/holidaywork/mobile/changeDate',
    getBreakTime: 'at/request/application/holidaywork/mobile/changeWorkHours',
    selectWorkInfo: 'at/request/application/holidaywork/mobile/selectWork',
    calculate: 'at/request/application/holidaywork/mobile/calculate',
    checkBeforeRegister: 'at/request/application/holidaywork/mobile/checkBeforeRegister',
    register: 'at/request/application/holidaywork/mobile/register',
    sendMailAfterRegisterSample: '',
    reflectApp: 'at/request/application/reflect-app'
};

