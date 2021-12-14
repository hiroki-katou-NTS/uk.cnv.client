import { Vue, _ } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { AppTimeType, ReflectSetting, TimeLeaveManagement, TimeLeaveRemaining } from '../';
import { TimeDuration } from '@app/utils/time';

@component({
    name: 'kafs12-apply-time',
    template: require('./index.vue'),
    components: {},
    validations: {
        params: {
            substituteAppTime: {
                constraint: 'AttendanceTime'
            },
            annualAppTime:  {
                constraint: 'AttendanceTime'
            },
            childNursingAppTime: {
                constraint: 'AttendanceTime'
            },
            nursingAppTime: {
                constraint: 'AttendanceTime'
            },
            super60AppTime: {
                constraint: 'AttendanceTime'
            },
            specialAppTime: {
                constraint: 'AttendanceTime'
            },
        }
    },
    constraints: [
        'nts.uk.ctx.at.shared.dom.common.time.AttendanceTime'
    ]
})
export class KafS12ApplyTimeComponent extends Vue {
    @Prop({ default: null })
    public readonly reflectSetting: ReflectSetting;
    @Prop({ default: null })
    public readonly timeLeaveManagement: TimeLeaveManagement;
    @Prop({ default: null })
    public readonly timeLeaveRemaining: TimeLeaveRemaining;
    @Prop({ default: null })
    public readonly appDispInfoStartupOutput: any;
    @Prop({ default: null })
    public readonly params: any;
    @Prop({ default: null })
    public readonly specialLeaveFrame: any;
    @Prop({ default: null })
    public readonly calculatedData: any;

    public comboBoxValue: number = null;

    public created() {
        const self = this;
        self.comboBoxValue = self.specialLeaveFrame;
    }

    get comboBoxOptions() {
        const self = this;

        return self.timeLeaveManagement ? self.timeLeaveManagement.timeSpecialLeaveMng.listSpecialFrame : [];
    }

    @Watch('comboBoxValue')
    public comboBoxValueWatcher(value: number) {
        const self = this;
        self.$emit('changeSpecialLeaveFrame', value);
    }

    @Watch('specialLeaveFrame')
    public specialLeaveFrameWatcher(value: number) {
        const self = this;
        self.comboBoxValue = value;
    }

    @Watch('params.super60AppTime')
    public valueWatcher1(value: number) {
        const self = this;
        if (_.isNil(value)) {
            self.params.super60AppTime = 0;
        }
    }

    @Watch('params.substituteAppTime')
    public valueWatcher2(value: number) {
        const self = this;
        if (_.isNil(value)) {
            self.params.substituteAppTime = 0;
        }
    }

    @Watch('params.annualAppTime')
    public valueWatcher3(value: number) {
        const self = this;
        if (_.isNil(value)) {
            self.params.annualAppTime = 0;
        }
    }

    @Watch('params.childNursingAppTime')
    public valueWatcher4(value: number) {
        const self = this;
        if (_.isNil(value)) {
            self.params.childNursingAppTime = 0;
        }
    }

    @Watch('params.nursingAppTime')
    public valueWatcher5(value: number) {
        const self = this;
        if (_.isNil(value)) {
            self.params.nursingAppTime = 0;
        }
    }

    @Watch('params.specialAppTime')
    public valueWatcher6(value: number) {
        const self = this;
        if (_.isNil(value)) {
            self.params.specialAppTime = 0;
        }
    }

    get requiredAppTime() {
        const self = this;
        if (self.calculatedData) {
            switch (self.params.appTimeType) {
                case AppTimeType.ATWORK:
                    return TimeDuration.toString(self.calculatedData.timeBeforeWork1, 'h');
                case AppTimeType.OFFWORK:
                    return TimeDuration.toString(self.calculatedData.timeAfterWork1, 'h');
                case AppTimeType.ATWORK2:
                    return TimeDuration.toString(self.calculatedData.timeBeforeWork2, 'h');
                case AppTimeType.OFFWORK2:
                    return TimeDuration.toString(self.calculatedData.timeAfterWork2, 'h');
                case AppTimeType.PRIVATE:
                    return TimeDuration.toString(self.calculatedData.privateOutingTime, 'h');
                case AppTimeType.UNION:
                    return TimeDuration.toString(self.calculatedData.unionOutingTime, 'h');
                default:
                    return '0:00';
            }
        }

        return '0:00';
    }

    get totalAppTime() {
        const self = this;

        return TimeDuration.toString(self.params.substituteAppTime
            + self.params.annualAppTime
            + self.params.childNursingAppTime
            + self.params.nursingAppTime
            + self.params.super60AppTime
            + self.params.specialAppTime, 'h');
    }

    get super60HRemaining() {
        const self = this;
        if (self.timeLeaveRemaining) {
            return TimeDuration.toString(self.timeLeaveRemaining.super60HRemainingTime, 'h');
        }

        return '0:00';
    }

    get grantDate() {
        const self = this;
        if (self.timeLeaveRemaining && self.timeLeaveRemaining.grantDate) {
            return self.$i18n('KAFS12_54') + self.timeLeaveRemaining.grantDate + '　' + self.timeLeaveRemaining.grantedDays + '日';
        } else {
            return self.$i18n('KAFS12_54') + self.$i18n('KAFS12_55');
        }
    }

    get substituteRemaining() {
        const self = this;
        if (self.timeLeaveRemaining) {
            return TimeDuration.toString(self.timeLeaveRemaining.subTimeLeaveRemainingTime, 'h');
        }

        return '0:00';
    }

    get annualRemaining() {
        const self = this;
        if (self.timeLeaveRemaining) {
            if (self.timeLeaveRemaining.annualTimeLeaveRemainingTime == 0) {
                return self.$i18n('KAF012_49', self.timeLeaveRemaining.annualTimeLeaveRemainingDays.toString());
            } else {
                return self.$i18n('KAF012_50', [
                    self.timeLeaveRemaining.annualTimeLeaveRemainingDays.toString(),
                    TimeDuration.toString(self.timeLeaveRemaining.annualTimeLeaveRemainingTime, 'h')
                ]);
            }
        }

        return '0:00';
    }

    get childNursingRemaining() {
        const self = this;
        if (self.timeLeaveRemaining) {
            if (self.timeLeaveRemaining.childCareRemainingTime == 0) {
                return self.$i18n('KAF012_49', self.timeLeaveRemaining.childCareRemainingDays.toString());
            } else {
                return self.$i18n('KAF012_50', [
                    self.timeLeaveRemaining.childCareRemainingDays.toString(),
                    TimeDuration.toString(self.timeLeaveRemaining.childCareRemainingTime, 'h')
                ]);
            }
        }

        return '0:00';
    }

    get nursingRemaining() {
        const self = this;
        if (self.timeLeaveRemaining) {
            if (self.timeLeaveRemaining.careRemainingTime == 0) {
                return self.$i18n('KAF012_49', self.timeLeaveRemaining.careRemainingDays.toString());
            } else {
                return self.$i18n('KAF012_50', [
                    self.timeLeaveRemaining.careRemainingDays.toString(),
                    TimeDuration.toString(self.timeLeaveRemaining.careRemainingTime, 'h')
                ]);
            }
        }

        return '0:00';
    }

    get specialRemaining() {
        const self = this;
        if (self.timeLeaveRemaining && self.timeLeaveRemaining.specialTimeFrames.length > 0) {
            const tmp = _.find(self.timeLeaveRemaining.specialTimeFrames, (i) => i.specialFrameNo == self.specialLeaveFrame);
            if (tmp) {
                if (tmp.timeOfSpecialLeave == 0) {
                    return self.$i18n('KAF012_49', [tmp.dayOfSpecialLeave.toString()]);
                } else {
                    return self.$i18n(
                        'KAF012_50',
                        [
                            tmp.dayOfSpecialLeave.toString(),
                            TimeDuration.toString(tmp.timeOfSpecialLeave, 'h')
                        ]);
                }
            }
        }
        
        return self.$i18n('KAF012_49', ['0']);
    }

    get display() {
        const self = this;
        switch (self.params.appTimeType) {
            case AppTimeType.ATWORK:
                return self.calculatedData
                    && self.calculatedData.timeBeforeWork1 > 0
                    && self.reflectSetting
                    && self.reflectSetting.destination.firstBeforeWork == 1;
            case AppTimeType.OFFWORK:
                return self.calculatedData
                    && self.calculatedData.timeAfterWork1 > 0
                    && self.reflectSetting
                    && self.reflectSetting.destination.firstAfterWork == 1;
            case AppTimeType.ATWORK2:
                return self.calculatedData
                    && self.calculatedData.timeBeforeWork2 > 0
                    && self.reflectSetting
                    && self.reflectSetting.destination.secondBeforeWork == 1
                    && self.appDispInfoStartupOutput
                    && self.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles;
            case AppTimeType.OFFWORK2:
                return (self.calculatedData
                    && self.calculatedData.timeAfterWork2 > 0
                    && self.reflectSetting
                    && self.reflectSetting.destination.secondAfterWork == 1
                    && self.appDispInfoStartupOutput
                    && self.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles) 
                    || self.checkTimeParam(self.params);
            case AppTimeType.PRIVATE:
                return (self.calculatedData
                    && self.calculatedData.privateOutingTime > 0
                    && self.reflectSetting
                    && self.reflectSetting.destination.privateGoingOut == 1) 
                    || self.checkTimeParam(self.params);
            case AppTimeType.UNION:
                return (self.calculatedData
                    && self.calculatedData.unionOutingTime > 0
                    && self.reflectSetting
                    && self.reflectSetting.destination.unionGoingOut == 1) 
                    || self.checkTimeParam(self.params);
            default:
                return false;
        }
    }

    get display60H() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.superHoliday60H == 1
            && self.timeLeaveManagement
            && self.timeLeaveManagement.super60HLeaveMng.super60HLeaveMngAtr;
    }

    get displaySubstitute() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.substituteLeaveTime == 1
            && self.timeLeaveManagement
            && self.timeLeaveManagement.timeSubstituteLeaveMng.timeSubstituteLeaveMngAtr;
    }

    get displayAnnual() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.annualVacationTime == 1
            && self.timeLeaveManagement
            && self.timeLeaveManagement.timeAnnualLeaveMng.timeAnnualLeaveMngAtr;
    }

    get displayChildNursing() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.childNursing == 1;
    }

    get displayChildNursingRemaining() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.childNursing == 1
            && self.timeLeaveManagement
            && self.timeLeaveManagement.nursingLeaveMng.timeChildCareLeaveMngAtr;
    }

    get displayNursing() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.nursing == 1;
    }

    get displayNursingRemaining() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.nursing == 1
            && self.timeLeaveManagement
            && self.timeLeaveManagement.nursingLeaveMng.timeCareLeaveMngAtr;
    }

    get displaySpecial() {
        const self = this;

        return self.reflectSetting
            && self.reflectSetting.condition.specialVacationTime == 1
            && self.timeLeaveManagement
            && self.timeLeaveManagement.timeSpecialLeaveMng.timeSpecialLeaveMngAtr;
    }

    public checkTimeParam(param: any) {
        if (param.substituteAppTime) {
            return true;
        }
        if (param.annualAppTime) {
            return true;
        }
        if (param.childNursingAppTime) {
            return true;
        }
        if (param.nursingAppTime) {
            return true;
        }
        if (param.super60AppTime) {
            return true;
        }
        if (param.specialAppTime) {
            return true;
        }
    }
}
