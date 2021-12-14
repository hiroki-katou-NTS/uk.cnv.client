import { _, Vue, moment } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { KafS00AComponent, KafS00BComponent, KafS00CComponent } from 'views/kaf/s00';
import { KafS00ShrComponent, AppType, Application, InitParam } from 'views/kaf/s00/shr';
import { RemainVacationInfoDto, TimeZoneNewDto, ReflectWorkHourCondition, TimeZoneWithWorkNoDto, WorkInformationDto, WorkTypeDto, MaxNumberDayType, AppAbsenceStartInfoDto, StartMobileParam, NotUseAtr, TimeZoneUseDto, HolidayAppTypeDispNameDto, ManageDistinct, TargetWorkTypeByApp, ApplicationType, HolidayAppType, DateSpecHdRelationOutput, ChangeDateParamMobile, SelectWorkTypeHolidayParam, SelectWorkTimeHolidayParam, MaxHolidayDayParamMobile, ApplyForLeaveDto, ReflectFreeTimeAppDto, TimeDigestApplicationDto, VacationRequestInfoDto, SupplementInfoVacationDto, ApplyforSpecialLeaveDto, CheckInsertMobileParam, RegisterAppAbsenceMobileCommand, WorkTypeUnit, MaxDaySpecHdDto, VacationCheckOutputDto, UpdateAppAbsenceMobileCommand, WorkAtr, WorkTypeClassification, RemainDaysHoliday } from '../a/define.interface';
import { KDL002Component } from '../../../kdl/002';
import { Kdl001Component } from '../../../kdl/001';
import { KdlS35Component } from '../../../kdl/s35';
import { KdlS36Component } from '../../../kdl/s36';
import { CmmS45CComponent } from '../../../cmm/s45/c/index';
@component({
    name: 'kafs06a',
    route: '/kaf/s06/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        inputA9_5: {
            constraint: 'AttendanceTime'
        },
        inputA9_7: {
            constraint: 'AttendanceTime'
        },
        inputA9_9: {
            constraint: 'AttendanceTime'
        },
        inputA9_11: {
            constraint: 'AttendanceTime'
        },
        inputA9_13: {
            constraint: 'AttendanceTime'
        },
        relationshipReason: {
            constraint: 'RelationshipReasonPrimitive',
            required: true
        },
        selectedValueHolidayType: {
            required: true,
            validate: true
        },
        workType: {
            code: {
                required: true,
                validate: true
            }
        }, 
        workHours1: {
            required: true,
            timeRange: true
        },
        workHours2: {
            required: false,
            timeRange: true
        }
    },
    constraints: [
        'nts.uk.ctx.at.shared.dom.common.time.AttendanceTime',
        'nts.uk.ctx.at.request.dom.application.appabsence.appforspecleave.RelationshipReasonPrimitive',
    ],

    components: {
        'kafs00-a': KafS00AComponent,
        'kafs00-b': KafS00BComponent,
        'kafs00-c': KafS00CComponent,
        'worktype': KDL002Component,
        'worktime': Kdl001Component,
        'kdls35': KdlS35Component,
        'kdls36': KdlS36Component,
        'cmms45c': CmmS45CComponent
    }
})
export class KafS06AComponent extends KafS00ShrComponent {
    public readonly DATE_PATTERN: string = 'YYYY/MM/DD';
    public text1: string = null;
    public isValidateAll: boolean = true;
    public user: any = null;
    public modeNew: boolean = true;
    public application: Application = null;
    public workHours1: {start: number, end: number} = null;
    public workHours2: {start: number, end: number} = null;
    public selectedValueHolidayType: any = null;

    public isFirstUpdate = false; /// turn off watch while firstly go to update mode
    public maxDaySpecHdDto: MaxDaySpecHdDto;
    public time: number = 0;
    public checkBoxC7: boolean = false;
    public relationshipReason: string = '';
    public model: Model = {} as Model;
    public inputA9_5: number = null;
    public inputA9_7: number = null;
    public inputA9_9: number = null;
    public inputA9_11: number = null;
    public inputA9_13: number = null;

    public A10_3: number = null;
    public linkWithVacation = [] as Array<any>;
    public linkWithDraw = [];
    public dropdownList = [];

    public dropdownRelationship = [];
    public workType = {
        code: null,
        name: '',
        time: ''
    } as WorkInfo;
    public workTime = {
        code: null,
        name: '',
        time: ''
    } as WorkInfo;
    @Watch('workHours2', {deep: true})
    public validateWorkHours2(data: any) {
        const self = this;
        
    }
    @Watch('isValidateWorkHours1', {deep: true})
    public updateValidateWorkHours1(data: boolean) {
        const self = this;

        if (!data) {
            self.$updateValidator('workHours1', {
                validate: false
            });
            self.$updateValidator('workHours2', {
                timeRange: false
            });
        } else {
            self.$updateValidator('workHours1', {
                validate: true
            });
            self.$updateValidator('workHours2', {
                timeRange: true
            });
            // self.$validate('workHours1');
            // self.$validate('workHours2');

        }
    }
    @Watch('c20', {deep: true})
    public updateValidateRelationshipReason(data: boolean) {
        const self = this;

        if (data) {
            self.$updateValidator('relationshipReason', {
                validate: true
            });
        } else {
            self.$updateValidator('relationshipReason', {
                validate: false
            });
        }
    }

    @Watch('selectedValueHolidayType', {deep: true})
    public watchSelectHolidayType(data: any) {
        const self = this;

        if (data && !self.isFirstUpdate) {
            self.selectedHolidayType(data);
            self.$validate('selectedValueHolidayType');
        }
    }
    // 続柄・喪主を選択する
    public selectedRelationship = null;
    @Watch('selectedRelationship', {deep: true})
    public watchSelectRelationship(data: any) {
        const self = this;

        if (data) {

            self.updateByRelationship(data);
        }
    }
    public mournerFlag: boolean = false;
    @Watch('mournerFlag')
    public changeMourner(data: any) {
        const self = this;

        self.bindNumOfDay(self.maxDaySpecHdDto);
    }
    
    @Watch('checkBoxC7', {deep: true})
    public changeValueC7(data: any) {
        const self = this;

        self.changeUseWorkTime(data);
    }


    @Prop() 
    public params: InitParam;
    // @Watch('c9') 
    // public updateValidate(data: boolean) {
    //     const self = this;
    //     if (data) {
    //         self.$updateValidator('workHours1', {
    //             required: true,
    //             timeRange: true
    //         });
    //     }
    // }

    public get grantDays(): string {
        const self = this;
        let model = self.model as Model;
        
        if (!model.appAbsenceStartInfoDto.remainVacationInfo.grantDate) {
            return self.$i18n('KAFS06_56') + self.$i18n('KAFS06_57');
        }

        return self.$i18n('KAFS06_56') + model.appAbsenceStartInfoDto.remainVacationInfo.grantDate + '　' + model.appAbsenceStartInfoDto.remainVacationInfo.grantDays + '日';
    }

    public get remainDays(): Array<RemainDaysHoliday> {
        const self = this;
        let model = self.model as Model;

        const remainVacationInfo = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo') as RemainVacationInfoDto;
        if (_.isNil(remainVacationInfo)) {

            return [];
        }
        const {subHdRemain, subVacaRemain, yearRemain, remainingHours, subVacaHourRemain, yearHourRemain} = remainVacationInfo;
        const remainDaysHoliday = {
            subHdRemain,
            subVacaRemain, 
            yearRemain,
            remainingHours,
            subVacaHourRemain, 
            yearHourRemain
        } as RemainDaysHoliday;

        return [remainDaysHoliday];

    }
    public get A9_2() {
        const self = this;
        let model = self.model as Model;
        let time = _.get(model, 'appAbsenceStartInfoDto.requiredVacationTime') || 0;

        return self.$dt.timewd(time);
    }

    public get KAFS06_27() {
        const self = this;

        return self.$i18n('KAFS06_27'); 
    }

    public get A9_3() {
        const self = this;
        let time = (self.c13 ? (self.inputA9_5 || 0) : 0) + 
                    (self.c14 ? (self.inputA9_7 || 0) : 0) + 
                    (self.c15 ? (self.inputA9_9 || 0) : 0) + 
                    (self.c16 ? (self.inputA9_11 || 0) : 0) + 
                    (self.c17 ? (self.inputA9_13 || 0) : 0);

        return self.$dt.timedr(time);
    }
    // 休暇残数情報．60H超休残時間
    public get A9_5() {
        const self = this;
        let model = self.model as Model;
        let time = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.over60HHourRemain') || 0;

        return self.getFormatTime(0, time);
    }
    // 休暇残数情報．代休残時間
    public get A9_7() {
        const self = this;
        let model = self.model as Model;
        let substituteLeaveManagement = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement');
        let timeAllowanceManagement = substituteLeaveManagement.timeAllowanceManagement;

        if (timeAllowanceManagement) {
            return self.formatTimeFromMinute(_.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.subVacaHourRemain') || 0);
        } else {
            return self.$i18n('KAFS06_40', [_.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.subHdRemain').toString()]);
        }
        // let time = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.subVacaHourRemain') || 0;

        // return self.getFormatTime(0, time);
    }
    // 休暇残数情報．年休残数
    // 休暇残数情報．年休残時間
    public get A9_9() {
        const self = this;
        let model = self.model as Model;
        let timeRemain = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.yearRemain') || 0;
        let timeHourRemain = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.yearHourRemain') || 0;

        return self.getFormatTime(timeRemain, timeHourRemain);

    }
    // 休暇残数情報．子看護残数
    // 休暇残数情報．子看護残時間
    public get A9_11() {
        const self = this;
        let model = self.model as Model;
        let timeRemain = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.childNursingRemain') || 0;
        let timeHourRemain = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.childNursingHourRemain') || 0;

        return self.getFormatTime(timeRemain, timeHourRemain);
    }
    // 休暇残数情報．介護残数
    // 休暇残数情報．介護残時間
    public get A9_13() {
        const self = this;
        let model = self.model as Model;
        let timeRemain = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.nursingRemain') || 0;
        let timeHourRemain = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.nirsingHourRemain') || 0;

        return self.getFormatTime(timeRemain, timeHourRemain);
    }

    public get HolidayAppType() {

        return HolidayAppType;
    }

    // ※2 = ○　OR　※3 = ○　OR　※4 = ○　OR　※5 = ○　
    public get c1() {
        const self = this;

        return self.c2 || self.c3 || self.c4 || self.c5;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．勤務情報、出退勤を反映する．就業時間帯を反映する = 反映しない
    public c2_textResource() {
        const self = this;
        let c2 = _.get(self.model, 'appAbsenceStartInfoDto.vacationApplicationReflect.workAttendanceReflect.reflectWorkHour') == ReflectWorkHourCondition.NOT_REFLECT;

        return c2;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．代休管理. 代休管理区分　＝　管理する
    public get c2() {
        const self = this;
        let model = self.model as Model;
        let c2 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.substituteLeaveManagement') == ManageDistinct.YES;
        
        return c2;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．代休管理．時間代休管理区分　＝　管理する
    public get c2_1() {
        const self = this;
        let model = self.model as Model;
        let c2_1 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement') == ManageDistinct.YES;

        return c2_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．振休管理. 振休管理区分　＝　管理する
    public get c3() {
        const self = this;
        let model = self.model as Model;
        let c3 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.holidayManagement.holidayManagement') == ManageDistinct.YES;

        return c3;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．年休管理. 年休管理区分　＝　管理する
    public get c4() {
        const self = this;
        let model = self.model as Model;
        let c4 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.annualLeaveManagement.annualLeaveManageDistinct') == ManageDistinct.YES;

        return c4;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．年休管理．時間年休管理区分　＝　管理する
    public get c4_1() {
        const self = this;
        let model = self.model as Model;
        let c4_1 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.annualLeaveManagement.timeAnnualLeaveManage') == ManageDistinct.YES;

        return c4_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．積休管理. 積休管理区分　＝　管理する
    public get c5() {
        const self = this;
        let model = self.model as Model;
        let c5 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.accumulatedRestManagement.accumulatedManage') == ManageDistinct.YES;

        return c5;
    }
    // UI処理【1】
    public get c6() {

        return true;
    }
    // 「A6_7」にチェックある場合
    public get c7() {
        const self = this;

        return self.checkBoxC7 || false;
    }
    public get isValidateWorkHours1() {
        const self = this;

        return (self.c9 && self.c11);
    }

    // 休暇申請起動時の表示情報．就業時間帯表示フラグ = true
    public get c9() {
        const self = this;
        let model = self.model as Model;
        let c9 = _.get(model, 'appAbsenceStartInfoDto.workHoursDisp');

        return c9;
    }
    // ※9 = ○　AND　※10-1 = ○　AND　※10-2 = ○
    public get c10() {
        const self = this;

        return self.c9 && self.c10_1 && self.c10_2;
    }
    // 「休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係なし)．複数回勤務の管理」= true　
    public get c10_1() {
        const self = this;
        let model = self.model as Model;
        let c10_1 = _.get(model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles');
        
        return c10_1;
    }
    // 「休暇申請起動時の表示情報．勤務時間帯一覧．勤務NO = 2」がある
    public get c10_2() {
        const self = this;
        let model = self.model as Model;
        let c10_2 = _.find(_.get(model, 'appAbsenceStartInfoDto.workTimeLst'), (item: TimeZoneUseDto) => item.workNo == 2);

        return !_.isNil(c10_2);
    }
    // ※7 = ○　AND「休暇申請起動時の表示情報．休暇申請の反映．勤務情報、出退勤を反映する．出退勤を反映する」=する
    public get c11() {
        const self = this;
        let model = self.model as Model;
        let c11 = _.get(model, 'appAbsenceStartInfoDto.vacationApplicationReflect.workAttendanceReflect.reflectAttendance') == NotUseAtr.USE;
        
        return self.c7 && c11;
    }
    // 「A4_3」が「時間消化」を選択している
    public get c12() {
        const self = this;

        return self.selectedValueHolidayType == HolidayAppType.DIGESTION_TIME;
    }
    // ※12 = ○　AND　※13-1 = ○　AND　※13-2 = ○
    public get c13() {
        const self = this;
        let model = self.model as Model;
        
        return self.c12 && self.c13_1 && self.c13_2;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．時間休暇を反映する．60H超休 = する
    public get c13_1() {
        const self = this;
        let model = self.model as Model;
        let c13_1 = _.get(model, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.superHoliday60H') == NotUseAtr.USE;
        
        return c13_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．60H超休管理．60H超休管理区分 = true
    public get c13_2() {
        const self = this;
        let model = self.model as Model;
        let c13_2 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.overtime60hManagement.overrest60HManagement') == NotUseAtr.USE;
        
        return c13_2;
    }
    // ※12 = ○　AND　※14-1 = ○　AND　※14-2 = ○
    public get c14() {
        const self = this;
        
        return self.c12 && self.c14_1 && self.c14_2;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．時間休暇を反映する．時間代休 = する
    public get c14_1() {
        const self = this;
        let model = self.model as Model;
        let c14_1 = _.get(model, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.substituteLeaveTime') == NotUseAtr.USE;
       
        return c14_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．代休管理区分．時間代休管理区分 = true
    public get c14_2() {
        const self = this;
        let model = self.model as Model;
        let c14_2 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement') == ManageDistinct.YES;
        
        return c14_2;
    }
    // ※12 = ○　AND　※15-1 = ○　AND　※15-2 = ○
    public get c15() {
        const self = this;
        
        return self.c12 && self.c15_1 && self.c15_2;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．時間休暇を反映する．時間年休=する
    public get c15_1() {
        const self = this;
        let model = self.model as Model;
        let c15_1 = _.get(model, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.annualVacationTime') == NotUseAtr.USE;
        
        return c15_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．年休管理．時間年休管理区分 = true
    public get c15_2() {
        const self = this;
        let model = self.model as Model;
        let c15_2 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.annualLeaveManagement.timeAnnualLeaveManage') == ManageDistinct.YES;
        
        return c15_2;
    }

    // ※12 = ○　AND　※16-1 = ○　AND　※16-2 = ○　AND　※16-3 = ○
    public get c16() {
        const self = this;
        
        return self.c12 && self.c16_1 && self.c16_2 && self.c16_3;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．時間休暇を反映する．子看護= する
    public get c16_1() {
        const self = this;
        let model = self.model as Model;
        let c16_1 = _.get(model, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.childNursing') == NotUseAtr.USE;

        return c16_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．子の看護管理区分 = true
    public get c16_2() {
        const self = this;
        let model = self.model as Model;
        let c16_2 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.childNursingManagement') == ManageDistinct.YES;
        
        return c16_2;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．時間子の看護の管理区分 = true
    public get c16_3() {
        const self = this;
        let model = self.model as Model;
        let c16_3 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.timeChildNursingManagement') == ManageDistinct.YES; 
        
        return c16_3;
    }
    // ※12 = ○　AND　※17-1 = ○　AND　※17-2 = ○　AND　※17-3 = ○
    public get c17() {
        const self = this;
        
        return self.c12 && self.c17_1 && self.c17_2 && self.c17_3;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．時間休暇を反映する．介護 = する
    public get c17_1() {
        const self = this;
        let model = self.model as Model;
        let c17_1 = _.get(model, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.nursing') == NotUseAtr.USE;
        
        return c17_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．介護管理区分 = true
    public get c17_2() {
        const self = this;
        let model = self.model as Model;
        let c17_2 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.longTermCareManagement') == ManageDistinct.YES;

        return c17_2;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．時間介護の管理区分 = true
    public get c17_3() {
        const self = this;
        let model = self.model as Model;
        let c17_3 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.timeCareManagement') == ManageDistinct.YES;
        
        return c17_3;
    }
    // ※18-1 = ○　AND　※18-2 = ○　AND　※18-3 = ○
    public get c18() {
        const self = this;
        
        return self.c18_1 && self.c18_2 && self.c18_3;
    }
    // 「休暇種類」が「特別休暇」を選択している
    public get c18_1() {
        const self = this;
        
        return self.selectedValueHolidayType == HolidayAppType.SPECIAL_HOLIDAY;
    }
    // 休暇申請起動時の表示情報．特別休暇表情報．事象に応じた特休フラグ = true 
    public get c18_2() {
        const self = this;
        let model = self.model as Model;
        let c18_2 = _.get(model, 'appAbsenceStartInfoDto.specAbsenceDispInfo.specHdForEventFlag');

        return c18_2 || false;
    }
    // 休暇申請起動時の表示情報．特別休暇表情報．事象に対する特別休暇．上限日数．種類 =「続柄ごとに上限を設定する」
    public get c18_3() {
        const self = this;
        let model = self.model as Model;
        let c18_3 = _.get(model, 'appAbsenceStartInfoDto.specAbsenceDispInfo.specHdEvent.maxNumberDay') == MaxNumberDayType.REFER_RELATIONSHIP;

        return c18_3;
    }
    // ※18 = ○　AND　休暇申請起動時の表示情報．特別休暇表情報．事象に対する特別休暇．忌引とする = true
    public get c19() {
        const self = this;
        let model = self.model as Model;
        let c19 = _.get(model, 'appAbsenceStartInfoDto.specAbsenceDispInfo.specHdEvent.makeInvitation') == NotUseAtr.USE;
        
        return self.c18 && c19;
    }
    // ※18 = ○　AND　休暇申請起動時の表示情報．特別休暇表情報．続柄毎の上限日数リスト．3親等以内とする = true
    public get c20() {
        const self = this;
        let model = self.model as Model;

        const { selectedRelationship } = self;
        const dateSpecHdRelationLst = _.get(model, 'appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst');
        const indexSelect = _.findLastIndex(dateSpecHdRelationLst , (o: any) => o.relationCD == selectedRelationship);
        let c20 = _.get(model, `appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst[${indexSelect == -1 ? 0 : indexSelect}].threeParentOrLess`);
        
        return self.c18 && (c20 || false);
    }
    // ※21-1 = ○　AND　※21-2 = ○
    public get c21() {
        const self = this;
        
        return self.c21_1 && self.c21_2;
    }
    // 休暇申請起動時の表示情報. 休暇残数情報．代休管理．紐づけ管理区分 = 管理する
    public get c21_1() {
        const self = this;
        let model = self.model as Model;
        let c21_1 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.linkingManagement') == ManageDistinct.YES;
        
        return c21_1;
    }
    public get c21_2() {
        const self = this;
        
        const workType = self.workType;
        const workTypeInfo = _.findLast(_.get(self.model, 'appAbsenceStartInfoDto.workTypeLst'),
            (item: any) => item.workTypeCode == workType.code) as WorkTypeDto;
        // const workTypeSet = _.get(workTypeInfo, 'workTypeSets[0]');
        if (!workTypeInfo) {
            
            return false;
        }
        let workAtr = workTypeInfo.workAtr;
        if (workAtr == WorkTypeUnit.OneDay) {

            return workTypeInfo.oneDayCls == WorkTypeClassification.SubstituteHoliday;
        } else {

            return (workTypeInfo.morningCls == WorkTypeClassification.SubstituteHoliday || workTypeInfo.afternoonCls == WorkTypeClassification.SubstituteHoliday);
        }
    }
    // ※22-1 = ○　AND　※22-2 = ○
    public get c22() {
        const self = this;
        let model = self.model as Model;
        
        return self.c22_1 && self.c22_2;

    }
    // 休暇申請起動時の表示情報. 休暇残数情報．振休管理．紐づけ管理区分」= 管理する
    public get c22_1() {
        // const self = this;
        // let model = self.model as Model;
        // let c22_1 = _.get(model, 'appAbsenceStartInfoDto.remainVacationInfo.holidayManagement.linkingManagement') == ManageDistinct.YES;
        
        // return c22_1;
        return true;
    }
    public get c22_2() {
        const self = this;
        
        const workType = self.workType;
        const workTypeInfo = _.findLast(_.get(self.model, 'appAbsenceStartInfoDto.workTypeLst'),
            (item: any) => item.workTypeCode == workType.code) as WorkTypeDto;
        // const workTypeSet = _.get(workTypeInfo, 'workTypeSets[0]');
        if (!workTypeInfo) {
            
            return false;
        }
        let workAtr = workTypeInfo.workAtr;
        if (workAtr == WorkTypeUnit.OneDay) {

            return workTypeInfo.oneDayCls == WorkTypeClassification.Pause;
        } else {

            return (workTypeInfo.morningCls == WorkTypeClassification.Pause || workTypeInfo.afternoonCls == WorkTypeClassification.Pause);
        }

    }

    // 申請日が選択されている場合
    public get c23() {
        const self = this;
        
        return true;

    }


    public mounted() {
        const self = this;
        self.fetchData();
        if (!self.modeNew) {
            if (!self.isValidateWorkHours1) {
                self.$updateValidator('workHours1', {
                    validate: false
                });
            } else {
                self.$updateValidator('workHours1', {
                    validate: true
                });
            }
            if (self.c20) {
                self.$updateValidator('relationshipReason', {
                    validate: true
                });
            } else {
                self.$updateValidator('relationshipReason', {
                    validate: false
                });
            }
        }
    }
    public fetchData() {
        const vm = this;
        vm.$mask('show');
        if (vm.params && vm.params.isDetailMode) {
            vm.modeNew = false;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
        }
        if (vm.modeNew) {
            vm.application = vm.createApplicationInsert(AppType.ABSENCE_APPLICATION);
            vm.application.employeeIDLst = _.get(vm.params, 'employeeID') ? [_.get(vm.params, 'employeeID')] : [];
            vm.application.appDate = _.get(vm.params, 'date');
        } else {
            vm.application = vm.params.appDispInfoStartupOutput.appDetailScreenInfo.application;
        }
        vm.$auth.user.then((user: any) => {
            vm.user = user;
        }).then(() => {
            if (vm.modeNew) {
                return vm.loadCommonSetting(
                    AppType.ABSENCE_APPLICATION,
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
                if (!vm.modeNew) {
                    vm.updateKaf000_C_Params(vm.modeNew);
                }
                let command = {

                } as StartMobileParam;
                command.mode = vm.modeNew ? MODE_NEW : MODE_UPDATE;
                command.companyId = vm.user.companyId;
                command.employeeIdOp = !_.isEmpty(vm.application.employeeIDLst) ? vm.application.employeeIDLst[0] : vm.user.employeeId;
                command.datesOp = vm.application.appDate ? [vm.application.appDate] : [];
                command.appDispInfoStartupOutput = vm.appDispInfoStartupOutput;
                if (vm.modeNew) {
                    return vm.$http.post('at', API.start, command);  
                }
                
                return true;
            }
        }).then((result: any) => {
            if (result) {
                if (vm.modeNew) {
                    let data = result.data as Model;
                    vm.model = data;
                    vm.bindComponent();
                } else {
                    vm.bindFromDetailInfo();
                }   
            }
        }).catch((error: any) => {
            vm.handleErrorCustom(error).then((result) => {
                if (result) {
                    vm.handleErrorCommon(error);
                }
            });
        }).then(() => vm.$mask('hide'));
    }
    public created() {
        const self = this;

        if (self.params && self.params.isDetailMode) {
            self.modeNew = false;
            self.isFirstUpdate = true;
            self.appDispInfoStartupOutput = self.params.appDispInfoStartupOutput;
            self.model = {
                appAbsenceStartInfoDto: self.params.appDetail.appAbsenceStartInfoDto,
                applyForLeaveDto: self.params.appDetail.applyForLeaveDto
            } as Model;
        }
    }
    public cloneappAbsenceStartInfoDto(appAbsenceStartInfoDto: AppAbsenceStartInfoDto) {
        const self = this;

        let appAbsenceStartInfoDtoClone = _.cloneDeep(appAbsenceStartInfoDto);

        self.changeDateFromList(appAbsenceStartInfoDtoClone.leaveComDayOffManas);
        self.changeDateFromList(appAbsenceStartInfoDtoClone.payoutSubofHDManas);

        return appAbsenceStartInfoDtoClone;
    }

    public changeDate() {
        const self = this;

        self.$mask('show');
        let command = {} as ChangeDateParamMobile;
        command.companyId = self.user.companyId;
        command.dates = self.getDates();
        command.appAbsenceStartInfoDto = self.cloneappAbsenceStartInfoDto(self.model.appAbsenceStartInfoDto);

        command.applyForLeaveDto = self.model.applyForLeaveDto;
        let holidayAppType = self.selectedValueHolidayType || HolidayAppType.ANNUAL_PAID_LEAVE;
        command.appHolidayType = Number(holidayAppType);
        self.$http.post('at', API.changeDate, command)
                  .then((res: any) => {
                        let model = {} as Model;
                        model.applyForLeaveDto = self.model.applyForLeaveDto;
                        model.appAbsenceStartInfoDto = res.data;
                        self.model = model;
                        self.bindComponent();
                    })
                  .catch((res: any) => {
                        self.handleErrorCustom(res).then((result) => {
                            if (result) {
                                self.handleErrorCommon(res);
                            }
                        });
                    })
                  .then((res: any) => self.$mask('hide'));

    }
    public kaf000BChangeDate(objectDate) {
        const vm = this;

        if (objectDate.startDate) {
            if (vm.modeNew) {
                vm.application.appDate = vm.$dt.date(objectDate.startDate, vm.DATE_PATTERN);
                vm.application.opAppStartDate = vm.$dt.date(objectDate.startDate, vm.DATE_PATTERN);
                vm.application.opAppEndDate = vm.$dt.date(objectDate.endDate, vm.DATE_PATTERN);
                vm.changeDate();
            }
        }
    }
    
    public kaf000BChangePrePost(prePostAtr) {
        const vm = this;

        vm.application.prePostAtr = prePostAtr;
    }

    public kaf000CChangeReasonCD(opAppStandardReasonCD) {
        const vm = this;

        vm.application.opAppStandardReasonCD = opAppStandardReasonCD;
    }

    public kaf000CChangeAppReason(opAppReason) {
        const vm = this;

        vm.application.opAppReason = opAppReason;
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

        vm.$mask('show');

        if (
            vm.c11 &&
            ((_.isNumber(_.get(vm.workHours2, 'start')) && !_.isNumber(_.get(vm.workHours2, 'end'))) 
            || (_.isNumber(_.get(vm.workHours2, 'end') && !_.isNumber(_.get(vm.workHours2, 'start')))))
        ) {

            vm.$nextTick(() => {
                vm.$mask('hide');
            });
            vm.$modal.error({ messageId: 'Msg_307'})
                .then(() => {
                    
                });

            return;
        }
        vm.isValidateAll = vm.customValidate(vm);
        vm.$validate();
        if (!vm.$valid || !vm.isValidateAll) {
            window.scrollTo(500, 0);
            vm.$nextTick(() => vm.$mask('hide'));

            return;
        }
        let commandCheck = {} as CheckInsertMobileParam;
        vm.model.applyForLeaveDto = vm.toApplyForLeave();
        commandCheck.companyId = vm.user.companyId;
        
        commandCheck.appAbsenceStartInfoDto = vm.cloneappAbsenceStartInfoDto(vm.model.appAbsenceStartInfoDto);
        commandCheck.applyForLeave = vm.model.applyForLeaveDto;
        commandCheck.mode = vm.modeNew;
        if (vm.modeNew) {
            commandCheck.application = vm.toApplication();
        } else {
            commandCheck.applicationUpdate = vm.toApplication();
        }
        let appDates = [];
        vm.$http.post('at', API.checkBeforeInsert, commandCheck)
        .then((result: any) => {
            if (result) {
                appDates = result.data.holidayDateLst;

                // xử lý confirmMsg
                return vm.handleConfirmMessage(result.data.confirmMsgLst);
            }
        }).then((result: any) => {
            if (result) {

                let command = {} as RegisterAppAbsenceMobileCommand;
                let commandUpdate = {} as UpdateAppAbsenceMobileCommand;
                if (vm.modeNew) {
                    command.applyForLeave = commandCheck.applyForLeave;
                    command.appDates = appDates;
                    // 休暇申請起動時の表示情報．申請表示情報．申請設定（基準日関係なし）．メールサーバ設定済区分
                    command.mailServerSet = vm.model.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoNoDateOutput.mailServerSet;
                    command.approvalRoot = vm.model.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opListApprovalPhaseState;
                    command.application = commandCheck.application;
                    command.apptypeSetting = vm.model.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting[0];
                    
                    let linkWithVacation = _.clone(vm.linkWithVacation);
                    vm.changeDateFromList(linkWithVacation);
                    let linkWithDraw = _.clone(vm.linkWithDraw);
                    vm.changeDateFromList(linkWithDraw);
                    command.leaveComDayOffMana = linkWithVacation;
                    command.payoutSubofHDManagements = linkWithDraw;
                } else {
                    commandUpdate.application = commandCheck.applicationUpdate;
                    commandUpdate.applyForLeave = commandCheck.applyForLeave;
                    commandUpdate.appDispInfoStartupOutput = vm.appDispInfoStartupOutput;
                    commandUpdate.holidayAppDates = appDates;
                    let leaveComDayOffManaDto = _.cloneDeep(vm.model.appAbsenceStartInfoDto.leaveComDayOffManas);
                    vm.changeDateFromList(leaveComDayOffManaDto);
                    let payoutSubofHDManagementDto = _.cloneDeep(vm.model.appAbsenceStartInfoDto.payoutSubofHDManas);
                    vm.changeDateFromList(payoutSubofHDManagementDto);
                    let leaveComDayOffMana = _.cloneDeep(vm.linkWithVacation);
                    vm.changeDateFromList(leaveComDayOffMana);
                    let payoutSubofHDManagements = _.cloneDeep(vm.linkWithDraw);
                    vm.changeDateFromList(payoutSubofHDManagements);
                    commandUpdate.leaveComDayOffManaDto = leaveComDayOffManaDto;
                    commandUpdate.payoutSubofHDManagementDto = payoutSubofHDManagementDto;
                    commandUpdate.leaveComDayOffMana = leaveComDayOffMana;
                    commandUpdate.payoutSubofHDManagements = payoutSubofHDManagements;

                }
                
                // đăng kí 
                return vm.$http.post('at', vm.modeNew ? API.insert : API.update, vm.modeNew ? command : commandUpdate).then((res: any) => {
                    vm.$http.post('at', API.reflectApp, res.data.reflectAppIdLst);
                    vm.$goto('kafs06a1', { mode: vm.modeNew ? ScreenMode.NEW : ScreenMode.DETAIL, appID: res.data.appIDLst[0] });
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
            vm.$mask('hide');    	
        });
    }

    public changeDateFromList(linkWithVacation: any) {
        if (linkWithVacation) {

            linkWithVacation.forEach((x: HolidayWorkSubHolidayLinkingMng) => {
                x.dateOfUse = new Date(x.dateOfUse).toISOString();
                x.outbreakDay = new Date(x.outbreakDay).toISOString();
            }); 
        }
    }
    
    public handleErrorCustom(failData: any): any {
        const vm = this;

        return new Promise((resolve) => {
            if (failData.messageId == 'Msg_197') {
                vm.$modal.error({ messageId: 'Msg_197', messageParams: [] }).then(() => {
                    let appID = vm.params.appDispInfoStartupOutput.appDetailScreenInfo.application.appID;
                    vm.$modal('cmms45c', { 'listAppMeta': [appID], 'currentApp': appID }).then((newData: InitParam) => {
                        vm.params = newData;
                        vm.modeNew = false;
                        vm.isFirstUpdate = true;
                        vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
                        vm.model = {
                            appAbsenceStartInfoDto: vm.params.appDetail.appAbsenceStartInfoDto,
                            applyForLeaveDto: vm.params.appDetail.applyForLeaveDto
                        } as Model;
                        vm.fetchData();
                        if (!vm.isValidateWorkHours1) {
                            vm.$updateValidator('workHours1', {
                                validate: false
                            });
                        } else {
                            vm.$updateValidator('workHours1', {
                                validate: true
                            });
                        }
                        if (vm.c20) {
                            vm.$updateValidator('relationshipReason', {
                                validate: true
                            });
                        } else {
                            vm.$updateValidator('relationshipReason', {
                                validate: false
                            });
                        }
                    });
                });
    
                return resolve(false);
            }

            if (failData.messageId == 'Msg_26') {
                vm.$modal.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
                .then(() => {
                    vm.$goto('ccg008a');
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
    // 勤務種類を変更する
    public bindOpenWorkType(vacationCheckOutputDto?: VacationCheckOutputDto) {
        const self = this;

        // self.bindHolidayType();
        self.bindWorkInfo(true);
        self.bindWorkHours(true);
        self.bindRelationship();
        self.bindLinkWithVacation(vacationCheckOutputDto);
        
    }

    // 就業時間帯を選択する bind after select wortime
    public bindOpenWorkTime() {
        const self = this;

        let workTimeCode = self.model.appAbsenceStartInfoDto.selectedWorkTimeCD || null;
        let workTimeName = '';
        if (!_.isNil(workTimeCode)) {
            let findResult = _.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst'), (item: any) => item.worktimeCode == workTimeCode) as any;
            workTimeName = !_.isNil(findResult) ? (!_.isNil(findResult.workTimeDisplayName) ? findResult.workTimeDisplayName.workTimeName : '') : '';
        }
        // 表示形式：開始+"～"+終了
        let workTimeTime = '';
        let existTime = _.find(self.model.appAbsenceStartInfoDto.workTimeLst, (item: TimeZoneUseDto) => item.workNo == 1) as TimeZoneUseDto;
        if (!_.isNil(existTime)) {
            workTimeTime = self.handleTimeWithDay(existTime.startTime) + '～' + self.handleTimeWithDay(existTime.endTime);
        }


        self.workTime = {
            code: workTimeCode,
            name: workTimeName,
            time: workTimeTime
        } as WorkInfo;
        // change by user 
        self.bindWorkHours(true);
    }
    public bindComponent(vacationCheckOutputDto?: VacationCheckOutputDto) {
        const self = this;

        self.bindHolidayType();
        self.bindWorkInfo(true);
        self.bindWorkHours(true);
        self.bindRelationship();
        self.bindLinkWithVacation(vacationCheckOutputDto);
    }
    public bindLinkWithVacation(vacationCheckOutputDto: VacationCheckOutputDto) {
        const self = this;

        if (!vacationCheckOutputDto) {

            return;
        }
        if (vacationCheckOutputDto.clearManageSubsHoliday) {
            // clear
            self.linkWithVacation = [];
        } 
        if (vacationCheckOutputDto.clearManageHolidayString) {
            // clear
            self.linkWithDraw = [];
        } 

    }
    public bindLinkWithVacationDetai() {
        const self = this;

        self.linkWithVacation = self.model.appAbsenceStartInfoDto.leaveComDayOffManas;
        self.linkWithDraw = self.model.appAbsenceStartInfoDto.payoutSubofHDManas;
    }
    // init with edit mode
    public bindFromDetailInfo() {
        const self = this;

        self.bindHolidayTypeByDetailInfo();
        self.bindWorkHours(false);
        self.bindWorkInfo(false);
        self.bindTimeDegestionFromDetailInfo();
        self.bindRelationshipByDetail();
        self.bindLinkWithVacationDetai();

        // 
        self.checkBoxC7 = _.get(self.model, 'applyForLeaveDto.reflectFreeTimeApp.workChangeUse') == NotUseAtr.USE;
        self.$nextTick(() => {
            self.isFirstUpdate = false;
        });
    }
    public bindHolidayTypeByDetailInfo() {
        const self = this;

        self.bindHolidayType(true);
        self.selectedValueHolidayType = Number(self.model.applyForLeaveDto.vacationInfo.holidayApplicationType);

    }

    public bindTimeDegestionFromDetailInfo() {
        const self = this;
        
        const timeDegestion = _.get(self.model, 'applyForLeaveDto.reflectFreeTimeApp.timeDegestion');
        self.inputA9_5 = timeDegestion.overtime60H;
        self.inputA9_7 = timeDegestion.timeOff;
        self.inputA9_9 = timeDegestion.timeAnualLeave;
        self.inputA9_11 = timeDegestion.childTime;
        self.inputA9_13 = timeDegestion.nursingTime;
    }

    public bindRelationship() {
        const self = this;

        // 特別休暇表示情報．続柄毎の上限日数リスト．続柄名
        // 特別休暇表示情報．続柄毎の上限日数リスト．コード
        let dropdownRelationship = [] as Array<any>;
        let dateSpecHdRelationLst = _.get(self.model, 'appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst') as Array<DateSpecHdRelationOutput>;
        _.forEach(dateSpecHdRelationLst, (item: DateSpecHdRelationOutput) => {
            dropdownRelationship.push({
                code: item.relationCD,
                text: item.relationName
            });
        });
        self.dropdownRelationship = dropdownRelationship;
        if (!_.isEmpty(dropdownRelationship)) {
            self.selectedRelationship = dropdownRelationship[0].code;
        }

    }

    public bindRelationshipByDetail() {
        const self = this;

        // 特別休暇表示情報．続柄毎の上限日数リスト．続柄名
        // 特別休暇表示情報．続柄毎の上限日数リスト．コード
        let dropdownRelationship = [] as Array<any>;
        let dateSpecHdRelationLst = _.get(self.model, 'appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst') as Array<DateSpecHdRelationOutput>;
        _.forEach(dateSpecHdRelationLst, (item: DateSpecHdRelationOutput) => {
            dropdownRelationship.push({
                code: item.relationCD,
                text: item.relationName
            });
        });
        self.dropdownRelationship = dropdownRelationship;
        const applyForSpeLeave = _.get(self.model, 'applyForLeaveDto.vacationInfo.info.applyForSpeLeave');
        if (!_.isEmpty(dropdownRelationship)) {
            let relationshipCD = _.get(applyForSpeLeave, 'relationshipCD') || dropdownRelationship[0];
            self.selectedRelationship = relationshipCD;
            
        }
        self.mournerFlag = _.get(applyForSpeLeave, 'mournerFlag') || false;
        self.relationshipReason = _.get(applyForSpeLeave, 'relationshipReason') || '';
        const specAbsenceDispInfo = _.get(self.model, 'appAbsenceStartInfoDto.specAbsenceDispInfo');
        if (specAbsenceDispInfo) {
            self.maxDaySpecHdDto = {
                dayOfRela: specAbsenceDispInfo.dayOfRela || 0,
                maxDay: specAbsenceDispInfo.maxDay || 0
            } as MaxDaySpecHdDto;
            self.A10_3 = self.mournerFlag ? ( self.maxDaySpecHdDto.dayOfRela + self.maxDaySpecHdDto.maxDay ) : self.maxDaySpecHdDto.maxDay;
        }

    }
    public bindWorkHours(mode?: boolean) {
        const self = this;
 
        let workTimeLst;
        if (mode) {
            workTimeLst = _.get(self.model, 'appAbsenceStartInfoDto.workTimeLst') as Array<TimeZoneUseDto>;
        } else {
            workTimeLst = _.get(self.model, 'applyForLeaveDto.reflectFreeTimeApp.workingHours') as Array<TimeZoneWithWorkNoDto>;
        }
        
        let result1 = _.find(workTimeLst, (item: TimeZoneUseDto) => item.workNo == 1) as any;
        let result2 = _.find(workTimeLst, (item: TimeZoneUseDto) => item.workNo == 2 && (!mode || item.useAtr == NotUseAtr.USE)) as any;

        let start1 = null;
        let end1 = null;
        let start2 = null;
        let end2 = null;
        if (!_.isNil(result1)) {
            start1 = mode ? result1.startTime : result1.timeZone.startTime;
            end1 = mode ? result1.endTime : result1.timeZone.endTime;
        }
        if (_.isNil(self.workHours1)) {
            self.workHours1 = {
                start: null,
                end: null
            };
        }
        self.workHours1.start = start1;
        self.workHours1.end = end1;
        if (!_.isNil(result2)) {
            start2 = mode ? result2.startTime : result2.timeZone.startTime;
            end2 = mode ? result2.endTime : result2.timeZone.endTime;
            
        }
        if (_.isNil(self.workHours2)) {
            self.workHours2 = {
                start: null,
                end: null
            };
        }
        self.workHours2.start = start2;
        self.workHours2.end = end2;

    } 

    public bindWorkInfo(mode: boolean) {
        const self = this;

        // NULLの場合：「休暇申請起動時の表示情報．勤務種類一覧」の1個目を選択する
        let workTypeCode = self.model.appAbsenceStartInfoDto.selectedWorkTypeCD || _.get(self.model, 'appAbsenceStartInfoDto.workTypeLst[0].workTypeCode') || null;
        if (!mode) {
            workTypeCode = _.get(self.model, 'applyForLeaveDto.reflectFreeTimeApp.workInfo.workType') || null;
        }
        let workTypeName = '';
        if (!_.isNil(workTypeCode)) {
            let findResult = _.find(_.get(self.model, 'appAbsenceStartInfoDto.workTypeLst'), (item: WorkTypeDto) => item.workTypeCode == workTypeCode) as WorkTypeDto;
            workTypeName = !_.isNil(findResult) ? findResult.name : '';
        }

        let workTimeCode = self.model.appAbsenceStartInfoDto.selectedWorkTimeCD || null;
        if (!mode) {
            workTimeCode = _.get(self.model, 'applyForLeaveDto.reflectFreeTimeApp.workInfo.workTime') || null;
        }
        let workTimeName = '';
        if (!_.isNil(workTimeCode)) {
            let findResult = _.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst'), (item: any) => item.worktimeCode == workTimeCode) as any;
            workTimeName = !_.isNil(findResult) ? (!_.isNil(findResult.workTimeDisplayName) ? findResult.workTimeDisplayName.workTimeName : '') : '';
        }
        // 表示形式：開始+"～"+終了
        let workTimeTime = '';
        let existTime = _.find(self.model.appAbsenceStartInfoDto.workTimeLst, (item: TimeZoneUseDto) => item.workNo == 1) as TimeZoneUseDto;
        if (!_.isNil(existTime)) {
            workTimeTime = self.handleTimeWithDay(existTime.startTime) + '～' + self.handleTimeWithDay(existTime.endTime);
        }

        self.workType = {
            code: workTypeCode,
            name: workTypeName
        } as WorkInfo;

        self.workTime = {
            code: workTimeCode,
            name: workTimeName,
            time: workTimeTime
        } as WorkInfo;

    }
    public handleTimeWithDay(time: number) {
        const self = this;

        const nameTime = '当日';
        if (!_.isNumber(time)) {

            return;
        }

        return (0 <= time && time < 1440) ? nameTime + self.$dt.timewd(time) : self.$dt.timewd(time);
    }

    public bindHolidayType(isUpdateMode?: boolean) {
        const self = this;

        if (!self.model.appAbsenceStartInfoDto) {

            return;
        }
        let dropDownList = [];
        // 休暇申請起動時の表示情報．休暇申請設定．休暇申請種類表示名．表示名
        let dispNames = self.model.appAbsenceStartInfoDto.hdAppSet.dispNames as Array<HolidayAppTypeDispNameDto>;
        dropDownList = _.map(dispNames, (item: HolidayAppTypeDispNameDto) => {

            return {
                code: item.holidayAppType,
                text: item.displayName
            };
        });
        dropDownList = _.filter(dropDownList, 
            (item: any) => 
                                ((item.code == HolidayAppType.ANNUAL_PAID_LEAVE && self.isAnnualPaidLeave) ||
                                (item.code == HolidayAppType.SUBSTITUTE_HOLIDAY && self.isSubstituteHoliday) ||
                                (item.code == HolidayAppType.ABSENCE && self.isAbsence) ||
                                (item.code == HolidayAppType.SPECIAL_HOLIDAY && self.isSpecialHoliday) ||
                                (item.code == HolidayAppType.YEARLY_RESERVE && self.isYearlyReserve) ||
                                (item.code == HolidayAppType.HOLIDAY && self.isHoliday) ||
                                (item.code == HolidayAppType.DIGESTION_TIME && self.isDigestionTime))
                                || (isUpdateMode && item.code == _.get(self.model, 'applyForLeaveDto.vacationInfo.holidayApplicationType'))
            );


        dropDownList.unshift({
            code: null,
            text: '--- 選択してください ---'
        });
        self.dropdownList = dropDownList;
        // self.selectedValueHolidayType = null;
        // = !_.isNil(self.getSelectedValue()) ?  String(self.getSelectedValue()) : null;
        // 

    }

    public get isAnnualPaidLeave() {
        const self = this;


        // 休暇申請起動時の表示情報．休暇残数情報．年休管理区分　＝　管理する
        const c1_1 = _.get(self.model, 'appAbsenceStartInfoDto.remainVacationInfo.annualLeaveManagement.annualLeaveManageDistinct') == ManageDistinct.YES;
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 年次有休
        let c1_2 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_2 = true;
        } else {
            c1_2 = 
            !_.isNil(
                _.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
                , (item: TargetWorkTypeByApp) => (item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.ANNUAL_PAID_LEAVE && !item.opHolidayTypeUse))
            );
        }
        
        return c1_1 && c1_2;
    }

    public get isSubstituteHoliday() {

        const self = this;

        // 休暇申請起動時の表示情報．休暇残数情報．代休管理区分　＝　管理する
        const c1_3 = _.get(self.model, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.substituteLeaveManagement') == ManageDistinct.YES;
        //休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 代休
        let c1_4 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_4 = true;
        } else {
            c1_4 = 
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.SUBSTITUTE_HOLIDAY && !item.opHolidayTypeUse));
        }
        // ※1-3 = ○　AND　※1-4 = ○ -> 代休

        return c1_3 && c1_4;
    }

    public get isAbsence() {
        const self = this;

        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 欠勤
        let c1_5 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_5 = true;
        } else {
            c1_5 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.ABSENCE && !item.opHolidayTypeUse));
        }
        // ※1-5 = ○ -> 欠勤

        return c1_5;
    }

    public get isSpecialHoliday() {
        const self = this;

        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 特別休暇
        let c1_6 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_6 = true;
        } else {
            c1_6 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.SPECIAL_HOLIDAY && !item.opHolidayTypeUse));
        }
        // ※1-6 = ○ -> 特別休暇

        return c1_6;
    }

    public get isYearlyReserve() {
        const self = this;

        // 休暇申請起動時の表示情報．休暇残数情報．積休管理区分　＝　管理する
        const c1_7 = self.model.appAbsenceStartInfoDto.remainVacationInfo.accumulatedRestManagement.accumulatedManage == ManageDistinct.YES;
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 積立年休
        let c1_8 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_8 = true;
        } else {
            c1_8 = 
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.YEARLY_RESERVE && !item.opHolidayTypeUse));
        }
        // ※1-7 = ○　AND　※8 = ○ -> 積立年休

        return c1_7 && c1_8;
    }
    public get isHoliday() {
        const self = this;

        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 休日
        let c1_9 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_9 = true;
        } else {
            c1_9 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.HOLIDAY && !item.opHolidayTypeUse));
        }
        // ※1-9 = ○ -> 休日

        return c1_9;
    }
    public get isDigestionTime() {
        const self = this;

        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 時間消化
        let c1_10 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_10 = true;
        } else {
            c1_10 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.DIGESTION_TIME && !item.opHolidayTypeUse));
        }

        // ※1-10 = ○ -> 時間消化
        return c1_10;
    }

    public getSelectedValue(): HolidayAppType {
        const self = this;

        if (!self.model.appAbsenceStartInfoDto || !_.isNil(self.selectedValueHolidayType)) {

            return self.selectedValueHolidayType || null;
        }
        // 休暇申請起動時の表示情報．休暇残数情報．年休管理区分　＝　管理する
        let c1_1 = self.model.appAbsenceStartInfoDto.remainVacationInfo.annualLeaveManagement.annualLeaveManageDistinct == ManageDistinct.YES;
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 年次有休
        let c1_2 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_2 = true;
        } else {
            c1_2 = 
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.ANNUAL_PAID_LEAVE && !item.opHolidayTypeUse));
        }
        
        // ※1-1 = ○　AND　※1-2 = ○ -> 年次有給
        if (c1_1 && c1_2) {

            return HolidayAppType.ANNUAL_PAID_LEAVE;
        }
        
        // 休暇申請起動時の表示情報．休暇残数情報．代休管理区分　＝　管理する
        let c1_3 = self.model.appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.substituteLeaveManagement == ManageDistinct.YES;
        //休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 代休
        let c1_4 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_4 = true;
        } else {
            c1_4 = 
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.SUBSTITUTE_HOLIDAY && !item.opHolidayTypeUse));
        }
        // ※1-3 = ○　AND　※1-4 = ○ -> 代休
        if (c1_3 && c1_4) {

            return HolidayAppType.SUBSTITUTE_HOLIDAY;
        }
        
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 欠勤
        let c1_5 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_5 = true;
        } else {
            c1_5 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.ABSENCE && !item.opHolidayTypeUse));
        }
        // ※1-5 = ○ -> 欠勤
        if (c1_5) {

            return HolidayAppType.ABSENCE;
        }
        
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 特別休暇
        let c1_6 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_6 = true;
        } else {
            c1_6 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.SPECIAL_HOLIDAY && !item.opHolidayTypeUse));
        }
        // ※1-6 = ○ -> 特別休暇
        if (c1_6) {

            return HolidayAppType.SPECIAL_HOLIDAY;
        }
        
        // 休暇申請起動時の表示情報．休暇残数情報．積休管理区分　＝　管理する
        let c1_7 = self.model.appAbsenceStartInfoDto.remainVacationInfo.accumulatedRestManagement.accumulatedManage == ManageDistinct.YES;
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 積立年休
        let c1_8 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_8 = true;
        } else {
            c1_8 = 
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.YEARLY_RESERVE && !item.opHolidayTypeUse));
        }
        // ※1-7 = ○　AND　※8 = ○ -> 積立年休
        if (c1_7 && c1_8) {

            return HolidayAppType.YEARLY_RESERVE;
        }
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 休日
        let c1_9 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_9 = true;
        } else {
            c1_9 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.HOLIDAY && !item.opHolidayTypeUse));
        }
        // ※1-9 = ○ -> 休日
        if (c1_9) {

            return HolidayAppType.HOLIDAY;
        }
        
        // 休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇種類を利用しない = false
        // AND　休暇申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり)．雇用別申請承認設定．申請別対象勤務種類．休暇申請の種類 = 時間消化
        let c1_10 = true;
        if (_.isNil(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet'))) {
            c1_10 = true;
        } else {
            c1_10 =
            !_.isNil(_.find(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst')
            , (item: TargetWorkTypeByApp) => item.appType == ApplicationType.ABSENCE_APPLICATION && item.opHolidayAppType == HolidayAppType.DIGESTION_TIME && !item.opHolidayTypeUse));
        }

        // ※1-10 = ○ -> 時間消化
        if (c1_10) {

            return HolidayAppType.DIGESTION_TIME;
        }

        return null;
    }

    public openKDL002(type?: string) {
        const self = this;

        if (type == 'worktype') {
            self.$modal('worktype', {
                    seledtedWkTypeCDs: _.map(_.get(self.model, 'appAbsenceStartInfoDto.workTypeLst'), (item: WorkTypeDto) => item.workTypeCode),
                    selectedWorkTypeCD: self.workType.code,
                    seledtedWkTimeCDs: _.map(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst'), (item: any) => item.worktimeCode),
                    selectedWorkTimeCD: self.workTime.code,
                    isSelectWorkTime: self.checkBoxC7 ? 1 : 0,
                })
                .then((result: any) => {
                    if (result) {
                        let workTypeCodeBefore = _.findLast(_.get(self.model, 'appAbsenceStartInfoDto.workTypeLst'), (item: WorkTypeDto) => item.workTypeCode == self.workType.code) as WorkTypeDto;
                        let workTypeCodeAfter = _.findLast(_.get(self.model, 'appAbsenceStartInfoDto.workTypeLst'), (item: WorkTypeDto) => item.workTypeCode == result.selectedWorkType.workTypeCode) as WorkTypeDto;
                        let workTypeCode = result.selectedWorkType.workTypeCode;
                        let workTypeName = result.selectedWorkType.name;
                        self.workType.code = workTypeCode;
                        self.workType.name = workTypeName;
                        if (result.selectedWorkTime) {
                            let workTimeCode = result.selectedWorkTime.code;
                            let workTimeName = result.selectedWorkTime.name;
                            let workTimeTime = result.selectedWorkTime.workTime1;
                            self.workTime.code = workTimeCode;
                            self.workTime.name = workTimeName;
                            self.workTime.time = workTimeTime;
                        }
    
    
                        let command = {} as SelectWorkTypeHolidayParam;
                        command.companyId = self.user.companyId;
                        command.dates = self.getDates();
                        command.appAbsenceStartInfoOutput = self.cloneappAbsenceStartInfoDto(self.model.appAbsenceStartInfoDto);
                        if (result.selectedWorkTime) {
                            command.appAbsenceStartInfoOutput.selectedWorkTimeCD = result.selectedWorkTime.code;
                        }
                        command.workTypeCodeBeforeOp = workTypeCodeBefore;
                        command.workTypeCodeAfterOp = workTypeCodeAfter;
                        command.holidayAppType = self.selectedValueHolidayType || HolidayAppType.ANNUAL_PAID_LEAVE;                    
                        self.$mask('show');
    
                        return self.$http.post('at', API.selectWorkType, command);
    
                    }
                })
                .then((res: any) => {
                    if (res) {
                        let model = {} as Model;
                        model.appAbsenceStartInfoDto = res.data.appAbsenceStartInfoDto;
                        self.model = model;
                        self.bindOpenWorkType(res.data.vacationCheckOutputDto);
                    }
                })
                .catch((res: any) => {
                    self.handleErrorCustom(res).then((result) => {
                        if (result) {
                            self.handleErrorCommon(res);
                        }
                    });
                })
                .then(() => self.$mask('hide'));

        } else {
            if (!self.checkBoxC7) {

                return;
            }
            self.$modal(
                'worktime',
                {
                    isAddNone: 1,
                    seledtedWkTimeCDs: _.map(_.get(self.model, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst'), (item: any) => item.worktimeCode),
                    selectedWorkTimeCD: self.workTime.code,
                    isSelectWorkTime: 1
                }
            ).then((result: any) => {
                if (result) {
                    self.workTime.code = result.selectedWorkTime.code;
                    self.workTime.name = result.selectedWorkTime.name;
                    self.workTime.time = result.selectedWorkTime.workTime1;


                    let command = {} as SelectWorkTimeHolidayParam;
                    command.companyId = self.user.companyId;
                    command.appAbsenceStartInfoDto = self.cloneappAbsenceStartInfoDto(self.model.appAbsenceStartInfoDto);
                    command.workTypeCode = self.workType.code;
                    command.workTimeCodeOp = self.workTime.code;
                    command.employeeId = self.user.employeeId;
                    command.datesOp = self.getDates();
                    self.$mask('show');

                    return self.$http.post('at', API.selectWorkTime, command);
                }
            })
            .then((res: any) => {
                if (res) {
                    let model = {} as Model;
                    model.appAbsenceStartInfoDto = res.data;
                    self.model = model;
                    self.bindOpenWorkTime(); 
                }
            })
            .catch((res: any) => {
                self.handleErrorCustom(res).then((result) => {
                    if (result) {
                        self.handleErrorCommon(res);
                    }
                });
            })
            .then(() => self.$mask('hide'));
        }

        
    }

    public changeUseWorkTime(data: any) {
        const self = this;

        self.$mask('show');

        self.model.appAbsenceStartInfoDto.workTimeChange = self.checkBoxC7;
        let command = {
            sId: self.user.employeeId, 
            date: self.application.opAppStartDate, 
            workTypeCd: self.workType.code, 
            workTimeCd: self.checkBoxC7 ? self.workTime.code : null, 
            appAbsenceStartInfo: self.model.appAbsenceStartInfoDto
        };

        self.$http.post('at', API.changeUseingWorkTime, command)
            .then((res: any) => {
                if (res) {
                    self.model.appAbsenceStartInfoDto.requiredVacationTime = res.data.requiredVacationTime;
                }
            }).catch((res: any) => {
                self.handleErrorCustom(res).then((result) => {
                    if (result) {
                        self.handleErrorCommon(res);
                    }
                });
            })
            .then(() => self.$mask('hide'));
    }
    public updateByRelationship(data: any) {
        const self = this;

        self.$mask('show');
        let command = {} as MaxHolidayDayParamMobile;
        command.companyId = self.user.companyId;
        command.specHdFrame = self.model.appAbsenceStartInfoDto.specAbsenceDispInfo.frameNo;
        command.specHdEvent = self.model.appAbsenceStartInfoDto.specAbsenceDispInfo.specHdEvent;
        command.relationCDOp = String(data);

        self.$http.post('at', API.getMaxHoliDay, command)
                  .then((res: any) => {
                    self.maxDaySpecHdDto = res.data;
                    self.bindNumOfDay(self.maxDaySpecHdDto);
                  })
                  .catch((res) => {
                    self.handleErrorCustom(res).then((result) => {
                        if (result) {
                            self.handleErrorCommon(res);
                        }
                    });
                  })
                  .then(() => self.$mask('hide'));
    }
    public bindNumOfDay(res: MaxDaySpecHdDto) {
        const self = this;

        // ◆「A10_4」が表示　AND　チェックあり：
　      // 「上限日数」＋「喪主加算日数」
        // ◆それ以外：
　      //「上限日数」
        if (!res) {

            return;
        }
        self.A10_3 = self.mournerFlag ? (res.dayOfRela + res.maxDay) : res.maxDay;
    }
    public selectedHolidayType(data: any) {
        const self = this;

        self.$mask('show');
        let command = {
            companyId: self.user.companyId,
            dates: self.getDates(),
            appAbsenceStartInfoDto: self.cloneappAbsenceStartInfoDto(self.model.appAbsenceStartInfoDto),
            holidayAppType: Number(data)
        } as any;

        self.$http.post('at', API.selectTypeHoliday, command)
                  .then((res: any) => {
                        let model = {} as Model;
                        model.applyForLeaveDto = self.model.applyForLeaveDto;
                        model.appAbsenceStartInfoDto = res.data;
                        self.model = model;
                        self.appDispInfoStartupOutput.appDispInfoNoDateOutput = model.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoNoDateOutput;
                        self.updateKaf000_C_Params(self.modeNew);
                        self.bindComponent();
                  })
                  .catch((res: any) => {
                        self.handleErrorCustom(res).then((result) => {
                            if (result) {
                                self.handleErrorCommon(res);
                            }
                        });
                  })
                  .then((res: any) => self.$mask('hide'));

    }

    public getDates() {
        const vm = this;

        let startDate = vm.application.opAppStartDate;
        let endDate = vm.application.opAppEndDate;
        let listDate = [];
        let diffDate = moment(endDate).diff(moment(startDate), 'days');
        for (let i = 0; i <= diffDate; i++) {
            let loopDate = moment(moment(startDate, vm.DATE_PATTERN).add(i, 'day').format(vm.DATE_PATTERN));
            listDate.push(loopDate.format(vm.DATE_PATTERN));
        }

        return listDate;
    }

    public toApplication() {
        const self = this;

        if (!self.modeNew) {
            self.application.opAppReason = self.application.opAppReason as any;
            self.application.opAppStandardReasonCD = self.application.opAppStandardReasonCD as any;
        }

        return self.application;
    }

    public toApplyForLeave() {
        const self = this;

        let applyForLeave: ApplyForLeaveDto;
        // if (!self.modeNew) {
        //     applyForLeave = self.model.applyForLeaveDto as ApplyForLeaveDto;
        // } else {
        //     applyForLeave = {} as ApplyForLeaveDto;
        // }
        applyForLeave = {} as ApplyForLeaveDto;
        // applyForLeave.application = self.toApplication();
        let reflectFreeTimeApp = {} as ReflectFreeTimeAppDto;
        reflectFreeTimeApp.workChangeUse = self.checkBoxC7 ? 1 : 0;
        let workInfo = {} as WorkInformationDto;
        workInfo.workType = self.workType.code;
        let workingHours = [] as Array<TimeZoneWithWorkNoDto>;
        if (self.c9 && self.c11) {
            workInfo.workTime = self.workTime.code;
            let timeZoneWithWorkNoDto = {} as TimeZoneWithWorkNoDto;
            timeZoneWithWorkNoDto.workNo = 1;
            timeZoneWithWorkNoDto.timeZone = {
                startTime: self.workHours1.start,
                endTime: self.workHours1.end
            } as TimeZoneNewDto;
            workingHours.push(timeZoneWithWorkNoDto);

        }
        if (self.c10 && _.isNumber(_.get(self.workHours2, 'start')) && _.isNumber(_.get(self.workHours2, 'end')) && self.c11) {
            let timeZoneWithWorkNoDto = {} as TimeZoneWithWorkNoDto;
            timeZoneWithWorkNoDto.workNo = 2;
            timeZoneWithWorkNoDto.timeZone = {
                startTime: self.workHours2.start,
                endTime: self.workHours2.end
            } as TimeZoneNewDto;
            workingHours.push(timeZoneWithWorkNoDto);  
        }
        let timeDegestion = {} as TimeDigestApplicationDto;
        if (self.c13) {
            timeDegestion.overtime60H = self.inputA9_5 || 0; 
        }
        if (self.c14) {
            timeDegestion.timeOff = self.inputA9_7 || 0;
        }
        if (self.c15) {
            timeDegestion.timeAnualLeave = self.inputA9_9 || 0;
        }
        if (self.c16) {
            timeDegestion.childTime = self.inputA9_11 || 0;
        }
        if (self.c17) {
            timeDegestion.nursingTime = self.inputA9_13 || 0;
        }
        if (_.some(workingHours)) {
            reflectFreeTimeApp.workingHours = workingHours;
        }
        if (_.some(timeDegestion)) {
            reflectFreeTimeApp.timeDegestion = timeDegestion;
        }
        if (_.some(workInfo)) {
            reflectFreeTimeApp.workInfo = workInfo;
        }
        let vacationInfo =  {} as VacationRequestInfoDto;
        let info = {} as SupplementInfoVacationDto;
        let applyForSpeLeave = {} as ApplyforSpecialLeaveDto;
        // if (self.c18_1) {
        //     applyForSpeLeave.mournerFlag = false;
        // }
        if (self.c18) {
            // A10_2
            applyForSpeLeave.relationshipCD = self.selectedRelationship;
        }
        if (self.c19) {
            // A10_4
            applyForSpeLeave.mournerFlag = self.mournerFlag || false;
        }
        if (self.c20) {
            // A10_5
            applyForSpeLeave.relationshipReason = self.relationshipReason || null;
        }
        if (_.some(applyForSpeLeave) || self.c19) {
            if (_.isNil(applyForSpeLeave.mournerFlag)) {
                applyForSpeLeave.mournerFlag = false;
            }
            info.applyForSpeLeave = applyForSpeLeave;
        }
        vacationInfo.holidayApplicationType = self.selectedValueHolidayType;
        vacationInfo.info = info;
        
        applyForLeave.vacationInfo = vacationInfo;
        if (_.some(reflectFreeTimeApp)) {
            applyForLeave.reflectFreeTimeApp = reflectFreeTimeApp;
        }

        return applyForLeave;

    }

    public openKDLS36() {
        const self = this;

        // 選択中の「勤務種類．１日の勤務．勤務区分」= 1日　⇒　1を渡す
        // 選択中の「勤務種類．１日の勤務．勤務区分」= 午前と午後　⇒　0.5を渡す
        const workTypeInfo = _.findLast(
            self.model.appAbsenceStartInfoDto.workTypeLst,
            (item: any) => item.workTypeCode === self.workType.code) as WorkTypeDto;
        let daysUnit = 1;
        if (workTypeInfo) {
            daysUnit = workTypeInfo.workAtr == WorkTypeUnit.OneDay ? 1 : 0.5;
        }
        const actualContentDisplayList = self.model.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst;
            
        const params: any = {
            // 社員ID
            employeeId: self.user.employeeId,

            // 申請期間
            period: {
                startDate: self.application.opAppStartDate,
                endDate: self.application.opAppEndDate,
            },

            // 日数単位（1.0 / 0.5）
            daysUnit,

            // 対象選択区分（自動 / 申請 / 手動
            targetSelectionAtr: TargetSelectionAtr.REQUEST,

            // List<表示する実績内容>
            actualContentDisplayList,

            // List<振出振休紐付け管理>
            managementData: self.linkWithVacation || [],
        };
        self.$modal('kdls36', params)
            .then((result: any) => {
                if (result) {
                    self.linkWithVacation = result.mngDisp;
                }
            });
    }


    public openKDLS35() {
        const self = this;

        // 選択中の「勤務種類．１日の勤務．勤務区分」= 1日　⇒　1を渡す
        // 選択中の「勤務種類．１日の勤務．勤務区分」= 午前と午後　⇒　0.5を渡す
        const workTypeInfo = _.findLast(
            self.model.appAbsenceStartInfoDto.workTypeLst,
            (item: any) => item.workTypeCode === self.workType.code) as WorkTypeDto;
        let daysUnit = 1;
        if (workTypeInfo) {
            daysUnit = workTypeInfo.workAtr == WorkTypeUnit.OneDay ? 1 : 0.5;
        }
        const actualContentDisplayList = self.model.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst;
            
        const params: any = {
            // 社員ID
            employeeId: self.user.employeeId,

            // 申請期間
            period: {
                startDate: self.application.opAppStartDate,
                endDate: self.application.opAppEndDate,
            },

            // 日数単位（1.0 / 0.5）
            daysUnit,

            // 対象選択区分（自動 / 申請 / 手動
            targetSelectionAtr: TargetSelectionAtr.REQUEST,

            // List<表示する実績内容>
            actualContentDisplayList,

            // List<振出振休紐付け管理>
            managementData: self.linkWithVacation,
        };
        self.$modal('kdls35', params)
            .then((result: any) => {
                if (result) {
                    self.linkWithDraw = result.mngDisp;
                }
            });
    }

    // 残数表示形式
    public getFormatTime(timeRemain: number, timeHourRemain: number) {
        const self = this;

        // let time;

        // if (timeRemain > 0) {
        //     if (timeHourRemain > 0) {
        //         time = timeRemain.toString().concat('日と').concat(self.$dt.timedr(timeHourRemain));
        //     } else {
        //         time = timeRemain.toString().concat('日');
        //     }
        // } else {
        //     time = self.$dt.timedr(timeHourRemain);
        // } 

        // return time;
        if (!timeHourRemain) {
            return self.$i18n('KAFS06_40', [timeRemain.toString()]);
        }

        return self.$i18n('KAFS06_58', [timeRemain.toString(), self.formatTimeFromMinute(timeHourRemain)]);
    }

    public formatTimeFromMinute(time: number) {
        const self = this;

        if (time) {
            let timeStr: string = self.$dt.timedr(time);
            if (timeStr.startsWith('0')) {
                return timeStr.substr(1, timeStr.length);
            } else {
                return timeStr;
            }
        }

        return '0:00';
    }

    @Watch('params')
    public paramsWatcher() {
        const vm = this;
        if (vm.params && vm.params.isDetailMode) {
            vm.modeNew = false;
            vm.isFirstUpdate = true;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
            vm.model = {
                appAbsenceStartInfoDto: vm.params.appDetail.appAbsenceStartInfoDto,
                applyForLeaveDto: vm.params.appDetail.applyForLeaveDto
            } as Model;
        } else {
            vm.modeNew = true;
            vm.selectedValueHolidayType = null;
        }
        vm.fetchData();
    }
    
}

enum TargetSelectionAtr {
    //自動
    AUTOMATIC = 0,

    //申請
    REQUEST = 1,

    //手動
    MANUAL = 2,
}
interface Model {
    appAbsenceStartInfoDto: AppAbsenceStartInfoDto;
    applyForLeaveDto: ApplyForLeaveDto;
}
interface WorkInfo {
    code: string;
    name: string;
    time?: string;
}
const API = {
    start: 'at/request/application/appforleave/mobile/start',
    selectTypeHoliday: 'at/request/application/appforleave/mobile/selectTypeHoliday',
    changeDate: 'at/request/application/appforleave/mobile/findChangeAppdate',
    selectWorkType: 'at/request/application/appforleave/mobile/selectWorkType',
    selectWorkTime: 'at/request/application/appforleave/mobile/selectWorkTime',
    getMaxHoliDay: 'at/request/application/appforleave/mobile/getMaxHoliDay',
    changeUseingWorkTime: 'at/request/application/appforleave/findChangeUsingWorkTime',
    checkBeforeInsert: 'at/request/application/appforleave/mobile/checkBeforeInsert',
    insert: 'at/request/application/appforleave/mobile/insert',
    update: 'at/request/application/appforleave/mobile/update',
    registerSample: 'at/request/application/changeDataSample',
    sendMailAfterRegisterSample: '',
    reflectApp: 'at/request/application/reflect-app'
};
interface HolidayWorkSubHolidayLinkingMng {
    // 社員ID
    employeeId: string;

    // 逐次休暇の紐付け情報 . 発生日
    outbreakDay: string;

    // 逐次休暇の紐付け情報 . 使用日
    dateOfUse: string;

    //逐次休暇の紐付け情報 . 使用日数
    dayNumberUsed: number;

    //逐次休暇の紐付け情報 . 対象選択区分
    targetSelectionAtr: number;
}
const MODE_NEW = 0;
const MODE_UPDATE = 1;
enum ScreenMode {
    // 新規モード
    NEW = 0,
    // 詳細モード
    DETAIL = 1
}
