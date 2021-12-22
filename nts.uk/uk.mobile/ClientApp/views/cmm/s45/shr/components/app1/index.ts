import { Vue, _ } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { TimeZoneWithWorkNoDto, AppForLeaveStartOutputDto, ManageDistinct, MaxNumberDayType, NotUseAtr, TimeZoneUseDto, HolidayAppType, WorkTypeUnit, WorkAtr, WorkTypeDto, HolidayAppTypeDispNameDto, DateSpecHdRelationOutput, WorkTypeClassification, ReflectWorkHourCondition } from 'views/kaf/s06/a/define.interface';
import { isEmpty, isNil, times } from 'lodash';

@component({
    name: 'cmms45shrcomponentsapp1',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class CmmS45ShrComponentsApp1Component extends Vue {
    
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

    public title: string = 'CmmS45ShrComponentsApp1';

    public dataOutput = {} as AppForLeaveStartOutputDto;

    public user: any;

    public workInfo: WorkInfo = {} as WorkInfo;

    public workHours1: WorkHours = {
        start: '',
        end: ''
    } as WorkHours;

    public workHours2: WorkHours = {
        start: '',
        end: ''
    } as WorkHours;

    public created() {
        const self = this;

        
    }
    // list data
    // 休出代休紐付け管理．紐付け．発生日
    public get B7(): Array<LinkWithVacation> {
        const self = this;
        let items = _.get(self.dataOutput, 'appAbsenceStartInfoDto.leaveComDayOffManas');

        return (!_.isEmpty(items) && !isNil(items)) ? items : [];
    }

    // list data
    public get B8(): Array<LinkWithVacation> {
        const self = this;
        let items = _.get(self.dataOutput, 'appAbsenceStartInfoDto.payoutSubofHDManas');

        return (!_.isEmpty(items) && !_.isNil(items)) ? items : [];
   }

    public get B1_2() {
        const self = this;
        let dispNames = _.get(self.dataOutput, 'appAbsenceStartInfoDto.hdAppSet.dispNames') || [];
        let holidayAppType = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.holidayApplicationType') || 0;

        
        return _.get(_.find(dispNames, (item: HolidayAppTypeDispNameDto) => item.holidayAppType == holidayAppType), 'displayName') || 'not exist type holiday';
    }
    public get B5_2() {
        const self = this;

        let time = _.get(self.dataOutput, 'appAbsenceStartInfoDto.requiredVacationTime') || 0;

        return time;
    }
    public get B5_3() {
        const self = this;

        return self.B5_4 + self.B5_5 + self.B5_6 + self.B5_7 + self.B5_8;
    }
    public get B5_4() {
        const self = this;
        let time = _.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.timeDegestion.overtime60H') || 0;
        
        return time;
    }

    public get B5_5() {
        const self = this;
        let time = _.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.timeDegestion.timeOff') || 0;
        
        return time;
    }

    public get B5_6() {
        const self = this;
        let time = _.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.timeDegestion.timeAnualLeave') || 0;
        
        return time;
    }

    public get B5_7() {
        const self = this;
        let time = _.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.timeDegestion.childTime') || 0;
        
        return time;
    }

    public get B5_8() {
        const self = this;
        let time = _.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.timeDegestion.nursingTime') || 0;
        
        return time;
    }

    public get B6_3() {
        const self = this;

        // 画面描画情報．補足情報．特別休暇．続柄コード
        const relationCDOp = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.info.applyForSpeLeave.relationshipCD');
        // 特別休暇表示情報．続柄毎の上限日数リスト．続柄名
        const dateSpecHdRelationLst = _.get(self.dataOutput, 'appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst') as Array<DateSpecHdRelationOutput>;

        const dateSpecHdRelation = _.findLast(dateSpecHdRelationLst, (item: DateSpecHdRelationOutput) => item.relationCD == relationCDOp) as DateSpecHdRelationOutput;

        return _.get(dateSpecHdRelation, 'relationName') || self.$i18n('KAFS06_50');
    }
    public get B6_5() {
        const self = this;

        const mournerFlag = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.info.applyForSpeLeave.mournerFlag');
        
        return mournerFlag ? self.$i18n('KAFS06_25') : self.$i18n('KAFS06_53');
    }
    public get B6_7() {
        const self = this;

        const relationshipReason = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.info.applyForSpeLeave.relationshipReason');
        
        return relationshipReason || '';
    }
    public get _() {
        return _;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．勤務情報、出退勤を反映する．就業時間帯を反映する = 反映しない
    public get c2() {
        const self = this;
        let c2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.vacationApplicationReflect.workAttendanceReflect.reflectWorkHour') == ReflectWorkHourCondition.NOT_REFLECT;

        return c2;
    }
    //休暇申請. 反映情報．勤務情報．就業時間帯変更する
    public get isChangeWork() {
        const self = this;
        let isChangeWork = _.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.workChangeUse') == NotUseAtr.USE;

        return isChangeWork;
    }

    // 休暇申請起動時の表示情報．就業時間帯表示フラグ = true
    public get c9() {
        const self = this;
        let c9 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.workHoursDisp');

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
        let c10_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles');
        
        return c10_1;
    }
    // 「休暇申請起動時の表示情報．勤務時間帯一覧．勤務NO = 2」がある
    public get c10_2() {
        const self = this;
        let c10_2 = _.findLast(_.get(self.dataOutput, 'appAbsenceStartInfoDto.workTimeLst'), (item: TimeZoneUseDto) => item.workNo == 2);

        return !_.isNil(c10_2);
    }
    // 「A4_3」が「時間消化」を選択している
    public get c12() {
        const self = this;
        let c12 = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.holidayApplicationType') == HolidayAppType.DIGESTION_TIME;
        
        return c12;
    }
    // ※12 = ○　AND　※13-1 = ○　AND　※13-2 = ○
    public get c13() {
        const self = this;

        return self.c12 && self.c13_1 && self.c13_2;
    }
    // 休暇申請起動時の表示情報．休暇申請の反映．時間休暇を反映する．60H超休 = する
    public get c13_1() {
        const self = this;
        let c13_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.superHoliday60H') == NotUseAtr.USE;
        
        return c13_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．60H超休管理．60H超休管理区分 = true
    public get c13_2() {
        const self = this;
        let c13_2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.overtime60hManagement.overrest60HManagement') == ManageDistinct.YES;
        
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
        let c14_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.substituteLeaveTime') == NotUseAtr.USE;
        
        return c14_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．代休管理区分．時間代休管理区分 = true
    public get c14_2() {
        const self = this;
        let c14_2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement') == ManageDistinct.YES;
        
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
        let c15_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.annualVacationTime') == NotUseAtr.USE;
        
        return c15_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．年休管理．時間年休管理区分 = true
    public get c15_2() {
        const self = this;
        let c15_2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.annualLeaveManagement.timeAnnualLeaveManage') == ManageDistinct.YES;
        
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
        let c16_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.childNursing') == NotUseAtr.USE;
        
        return c16_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．子の看護管理区分 = true
    public get c16_2() {
        const self = this;
        let c16_2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.childNursingManagement') == ManageDistinct.YES;
        
        return c16_2;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．時間子の看護の管理区分 = true
    public get c16_3() {
        const self = this;
        let c16_3 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.timeChildNursingManagement') == ManageDistinct.YES;
        
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
        let c17_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.vacationApplicationReflect.timeLeaveReflect.nursing') == NotUseAtr.USE;
        
        return c17_1;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．介護管理区分 = true
    public get c17_2() {
        const self = this;
        let c17_2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.longTermCareManagement') == ManageDistinct.YES;
        
        return c17_2;
    }
    // 休暇申請起動時の表示情報．休暇残数情報．介護看護休暇管理．時間介護の管理区分 = true
    public get c17_3() {
        const self = this;
        let c17_3 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.nursingCareLeaveManagement.timeCareManagement') == NotUseAtr.USE;
        
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
        let c18_1 = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.holidayApplicationType') == HolidayAppType.SPECIAL_HOLIDAY;
        
        return c18_1;
    }
    // 休暇申請起動時の表示情報．特別休暇表情報．事象に応じた特休フラグ = true 
    public get c18_2() {
        const self = this;
        let c18_2 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.specAbsenceDispInfo.specHdForEventFlag');
        
        return c18_2;
    }
    // 休暇申請起動時の表示情報．特別休暇表情報．事象に対する特別休暇．上限日数．種類 =「続柄ごとに上限を設定する」
    public get c18_3() {
        const self = this;
        let c18_3 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.specAbsenceDispInfo.specHdEvent.maxNumberDay') == MaxNumberDayType.REFER_RELATIONSHIP;

        return c18_3;
    }
    // ※18 = ○　AND　休暇申請起動時の表示情報．特別休暇表情報．事象に対する特別休暇．忌引とする = true
    public get c19() {
        const self = this;
        let c19 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.specAbsenceDispInfo.specHdEvent.makeInvitation') == NotUseAtr.USE;
        
        return self.c18 && c19;
    }
    // 選択している続柄に対する「3親等以内とする」情報を確認する
    public get c20() {

        const self = this;
        const dateSpecHdRelationLst = _.get(self.dataOutput, 'appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst');
        const relationCDOp = _.get(self.dataOutput, 'applyForLeaveDto.vacationInfo.info.applyForSpeLeave.relationshipCD');
        const indexSelect = _.findLastIndex(dateSpecHdRelationLst , (o: any) => o.relationCD == relationCDOp);
        const c20 = _.get(self.dataOutput, `appAbsenceStartInfoDto.specAbsenceDispInfo.dateSpecHdRelationLst[${indexSelect == -1 ? 0 : indexSelect}].threeParentOrLess`);

        return c20;
    }
    // ※21-1 = ○　AND　※21-2 = ○
    public get c21() {
        const self = this;

        return self.c21_1 && self.c21_2;
    }
    // 休暇申請起動時の表示情報. 休暇残数情報．代休管理．紐づけ管理区分 = 管理する
    public get c21_1() {
        const self = this;
        let c21_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.substituteLeaveManagement.linkingManagement') == ManageDistinct.YES;
        
        return c21_1;
    }
    public get c21_2() {
        const self = this;
        
        const workType = self.workInfo.workType;
        const workTypeInfo = _.findLast(_.get(self.dataOutput, 'appAbsenceStartInfoDto.workTypeLst'),
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

        return self.c22_2;
    }
    // 休暇申請起動時の表示情報. 休暇残数情報．振休管理．紐づけ管理区分」= 管理する
    public get c22_1() {
        const self = this;
        let c22_1 = _.get(self.dataOutput, 'appAbsenceStartInfoDto.remainVacationInfo.holidayManagement.linkingManagement') == ManageDistinct.YES;
        
        return c22_1;
    }
    public get c22_2() {
        const self = this;
        
        const workType = self.workInfo.workType;
        const workTypeInfo = _.findLast(_.get(self.dataOutput, 'appAbsenceStartInfoDto.workTypeLst'),
            (item: any) => item.workTypeCode == workType.code) as WorkTypeDto;
        // const workTypeSet = _.get(workTypeInfo, 'workTypeSets[0]');
        if (!workTypeInfo) {
            
            return false;
        }
        let workAtr = workTypeInfo.workAtr;
        if (workAtr == WorkTypeUnit.OneDay) {

            return false;
            // return workTypeInfo.oneDayCls == WorkTypeClassification.Pause;
        } else {

            return (workTypeInfo.morningCls == WorkTypeClassification.Pause || workTypeInfo.afternoonCls == WorkTypeClassification.Pause);
        }
    }
    public mounted() {
        const self = this;

        self.$auth.user.then((usr: any) => {
            self.user = usr;
        }).then((res: any) => {
            self.fetchData(self.params);
        });
        self.$watch('params.appDispInfoStartupOutput', (newV, oldV) => {
            self.fetchData(self.params);
        });
    }
    public fetchData(getParams: any) {
        const self = this;

        let command = self.createCommand() as any;
        self.$http.post('at', API.getDetailInfo, command)
                    .then((res: any) => {
                        self.dataOutput = res.data;
                        self.params.appDetail = self.dataOutput;

                        self.bindComponent();
                    })
                    .catch((res: any) => {
                        self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
                    })
                    .then(() => self.$emit('loading-complete'));
    }

    public createCommand() {
        const self = this;

        if (self.user) {

            return {
                companyId: self.user.companyId,
                appId: self.params.appDispInfoStartupOutput.appDetailScreenInfo.application.appID,
                appDispInfoStartup: self.params.appDispInfoStartupOutput
            };
        }
    }
    public bindComponent() {
        const self = this;
        self.bindWorkInfo();
        self.bindWorkHours();
    }

    public bindWorkInfo() {
        const self = this;
        let workInfo = self.dataOutput.applyForLeaveDto.reflectFreeTimeApp.workInfo;
        self.createWorkInfo(_.get(workInfo, 'workType'), _.get(workInfo, 'workTime'));

    }

    public createWorkInfo(codeType?: string, codeTime?: string) {
        const self = this;

        let workType = {} as Work;
        workType.code = codeType || '';

        let workTime = {} as Work;
        workTime.code = codeTime || (self.isChangeWork ? self.$i18n('KAFS06_51') : self.$i18n('KAFS06_55'));
        let workTypes = _.get(self.dataOutput, 'appAbsenceStartInfoDto.workTypeLst');

        let resultWorkType = 
            _.find(workTypes, (i: any) => i.workTypeCode == workType.code);
        workType.name = resultWorkType ? (resultWorkType.name || '')  : self.$i18n('KAFS06_50');

        let workTimes = self.dataOutput.appAbsenceStartInfoDto.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst;
        if (codeTime) {
            let resultWorkTime = 
                    _.find(workTimes, (i: any) => i.worktimeCode == workTime.code);
            workTime.name = resultWorkTime ? (_.get(resultWorkTime, 'workTimeDisplayName.workTimeName') || '') : self.$i18n('KAFS06_50');
        }
  
        
        let workInfo = {} as WorkInfo;
        workInfo.workType = workType;
        workInfo.workTime = workTime;

        self.workInfo = workInfo;
    }

    public createWorkHours(start: number, end: number) {
        const self = this;
        if (!_.isNumber(start) || !_.isNumber(end)) {

            return {
                start: self.$i18n('KAFS06_52'),
                end: ''
            } as WorkHours;
        }

        return {
            start: self.$dt.timedr(start || 0),
            end: self.$dt.timedr(end || 0)
        } as WorkHours;
    }

    public bindWorkHours() {
        const self = this;
        let workHours1 = _.findLast(_.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.workingHours'), (item: TimeZoneWithWorkNoDto) => item.workNo == 1);

        let workHours2 = _.findLast(_.get(self.dataOutput, 'applyForLeaveDto.reflectFreeTimeApp.workingHours'), (item: TimeZoneWithWorkNoDto) => item.workNo == 2);
        // 1
        self.workHours1 = self.createWorkHours(
            _.get(workHours1, 'timeZone.startTime'),
            _.get(workHours1, 'timeZone.endTime'));
        // 2
        self.workHours2 = self.createWorkHours(
            _.get(workHours2, 'timeZone.startTime'),
            _.get(workHours2, 'timeZone.endTime'));
    }
}

const API = {
    getDetailInfo: 'at/request/application/appforleave/mobile/getDetailInfo'
};

interface WorkInfo {
    workType: Work;
    workTime: Work;
}
interface Work {
    code: string;
    name: string;
    time?: string;
}
interface WorkHours {
    start: string;
    end: string;
}

export interface LinkWithVacation {
    // 社員ID
    sid: string;

    // 逐次休暇の紐付け情報 . 発生日
    outbreakDay: string;

    // 逐次休暇の紐付け情報 . 使用日
    dateOfUse: string;

    // 逐次休暇の紐付け情報 . 使用日数
    dayNumberUsed: number;

    // 逐次休暇の紐付け情報 . 対象選択区分
    targetSelectionAtr: number;
}