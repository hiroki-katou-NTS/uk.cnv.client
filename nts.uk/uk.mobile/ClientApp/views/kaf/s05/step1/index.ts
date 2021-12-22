import { _, Vue } from '@app/provider';
import { component, Watch } from '@app/core/component';
import { KafS00SubP3Component } from 'views/kaf/s00/sub/p3';
import { KafS00SubP1Component } from 'views/kaf/s00/sub/p1';
import { KafS00SubP2Component } from 'views/kaf/s00/sub/p2';
import { KafS00AComponent, KafS00BComponent, KafS00CComponent } from 'views/kaf/s00';
import { TimeZoneWithWorkNo, BreakTime, TimeZoneNew, WorkHoursDto, AppOverTime, InfoWithDateApplication , DisplayInfoOverTime, TimeZone, ParamBreakTime, BreakTimeZoneSetting} from '../a/define.interface';
import { KafS05Component} from '../a/index';
@component({
    name: 'kafs05step1',
    route: '/kaf/s05/step1',
    style: require('./style.scss'),
    template: require('./index.vue'),
    validations: {
        workHours1: {
            required: false,
            timeRange: false
        },
        workHours2: {
            required: false,
            timeRange: true
        }
    },
    constraints: [],
    components: {
        'kafs00subp3': KafS00SubP3Component,
        'kafs00subp1': KafS00SubP1Component,
        'kafs00subp2': KafS00SubP2Component,
        'kafs00-a': KafS00AComponent,
        'kafs00-b': KafS00BComponent,
        'kafs00-c': KafS00CComponent
    }
})
export class KafS05Step1Component extends Vue {
    public title: string = 'KafS05Step1';

    public workInfo: WorkInfo = {} as WorkInfo;

    public workHours1: ValueTime = null;

    public workHours2?: ValueTime = null;

    public breakTimes: Array<BreakTime> = [];

    public displayNumberBreakTime = 3;

    public isFirstModeUpdate: boolean = true;

    public get isAddFrameBreakTime() {
        const self = this;

        return self.displayNumberBreakTime != 10; 
    }
    @Watch('$appContext.c3') 
    public updateValidator(data: any) {
        const self = this;
        if (data) {
            self.$updateValidator('workHours1', {
                required: true,
                timeRange: true
            });
        }
    }
    @Watch('workHours1', {deep: true})
    public changeWorkHours1(data: ValueTime) {
        const self = this;
        if (_.isNil(_.get(data,'start')) || _.isNil(_.get(data, 'end')) || (self.isFirstModeUpdate && !self.$appContext.modeNew)) {
            self.isFirstModeUpdate = false;

            return;
        }
        let parent = self.$parent as KafS05Component;
        let command = {

        } as ParamBreakTime;
        command.workTypeCode = self.workInfo.workType.code;
        command.workTimeCode = self.workInfo.workTime.code;
        command.startTime = self.workHours1.start;
        command.endTime = self.workHours1.end;
        command.companyId = parent.user.companyId;
        command.actualContentDisplayDtos = _.get(parent.model.displayInfoOverTime,'appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst');
        parent.getBreakTime(command);
        
    }

    @Watch('workHours2', {deep: true})
    public changeWorkHours2(data: any) {
        console.log(data);
    }


    public kafS00P1Params1: any = {
        preAppDisp: false,
        preAppTime: null,
        preAppExcess: null,
        actualDisp: false,
        actualTime: null,
        actualExcess: null,
        scheduleDisp: true,
        scheduleTime: null,
        scheduleExcess: null
    };

    public created() {
        const self = this;

        if (self.$appContext.getoverTimeClf == 0) {
            self.pgName = 'kafs05step1';
        } else if (self.$appContext.getoverTimeClf == 1) {
            self.pgName = 'kafs05step2';
        } else {
            self.pgName = 'kafs05step3';
        }
        self.loadData();     
    }

    public mounted() {
        const self = this;
        if (!self.$appContext.modeNew) {
            if (self.$appContext.c3) {
                self.$updateValidator('workHours1', {
                    required: true,
                    timeRange: true
                });
            }
        }
    }

    
    public getWorkHours1() {
        const self = this;

        return self.workHours1;
    }

    public getWorkHours2() {
        const self = this;

        return self.workHours2;
    }

    public getWorkType() {
        const self = this;

        return self.workInfo.workType.code;
    }

    public getWorkTime() {
        const self = this;
        
        return self.workInfo.workTime.code;
    }

    public getBreakTimes(): Array<BreakTime> {
        const self = this;

        return self.breakTimes;
    }
    public setWorkTime(
        workTimeCD?: string,
        workTimeName?: string,
        workTimeHours?: string,
    ) {

        const self = this;
        let workType = {} as Work;
        workType.code = self.workInfo.workType.code;
        workType.name = self.workInfo.workType.name;
        let workTime = {} as Work;
        workTime.code = workTimeCD;
        workTime.name = workTimeName;
        let workInfo = {} as WorkInfo;
        workInfo.workType = workType;
        workInfo.workTime = workTime;
        self.workInfo = workInfo;
        
        return;

    }

    public setWorkCode(
        workTypeCD?: string,
        workTypeName?: string,
        workTimeCD?: string,
        workTimeName?: string,
        workTimeHours?: string,
        displayOver?: DisplayInfoOverTime) {
        
        const self = this;
        let workType = {} as Work;
        workType.code = workTypeCD;
        workType.name = workTypeName;
        let workTime = {} as Work;
        workTime.code = workTimeCD;
        workTime.name = workTimeName;
        let workInfo = {} as WorkInfo;
        workInfo.workType = workType;
        workInfo.workTime = workTime;
        self.workInfo = workInfo;  

        return;
    }

    get $appContext(): KafS05Component {
        const self = this;

        return self.$parent as KafS05Component;
    }
    public createBreakTimeModel() {
        const self = this;
        for (let i = 0; i < 10; i++) {
            let item = {} as BreakTime;
            item.valueHours = null as ValueTime;
            item.frameNo = i + 1;
            item.title = self.$i18n('KAFS05_69', String(item.frameNo));
            self.breakTimes.push(item);
        }
    }
    public createBreakTime(timeZone?: any) {
        const self = this;
        let numberDisplay = 0;
        if (_.isEmpty(self.breakTimes)) {
            self.createBreakTimeModel();
        }
        _.forEach(self.breakTimes, (item: any) => {
            item.valueHours = null as ValueTime;
        });
        _.forEach(timeZone, (item: any, index: number) => {
            let resultBreakTime = _.find(self.breakTimes, (i: any) => i.frameNo == (index + 1)) as BreakTime;
            if (!_.isNil(resultBreakTime)) {
                resultBreakTime.valueHours = {} as ValueTime;
                resultBreakTime.valueHours.start = item.start;
                resultBreakTime.valueHours.end = item.end;
                numberDisplay++;
            } else {
                self.breakTimes[index].valueHours = null as ValueTime;
            }
        });
        if (numberDisplay == 0) {
            numberDisplay++;
        }
        self.displayNumberBreakTime = numberDisplay;
    }

    public createWorkHours(mode: boolean) {
        const self = this;
 
        let workHours = _.get(self.$appContext.model.displayInfoOverTime, 'infoWithDateApplicationOp.workHours') as WorkHoursDto;
        let workHours1 = {} as ValueTime;
        let workHours2 = {} as ValueTime;
        if (mode) {
            if (workHours) {
                workHours1.start = workHours.startTimeOp1;
                workHours1.end = workHours.endTimeOp1;
                workHours2.start = workHours.startTimeOp2;
                workHours2.end = workHours.endTimeOp2;
            }
        } else {
            _.sortBy(self.$appContext.model.appOverTime.workHoursOp, (i: TimeZoneWithWorkNo) => i.workNo);
            let workHoursOp1 = _.get(self.$appContext.model.appOverTime, 'workHoursOp[0].timeZone') as TimeZoneNew;
            let workHoursOp2 = _.get(self.$appContext.model.appOverTime, 'workHoursOp[1].timeZone') as TimeZoneNew;

            workHours1.start = workHoursOp1 ? workHoursOp1.startTime : null;
            workHours1.end = workHoursOp1 ? workHoursOp1.endTime : null;

            workHours2.start = workHoursOp2 ? workHoursOp2.startTime : null;
            workHours2.end = workHoursOp2 ? workHoursOp2.endTime : null;

        }
        self.workHours1 = (_.isNumber(workHours1.start) || _.isNumber(workHours1.end)) ?  workHours1 : null;
        self.workHours2 = (_.isNumber(workHours2.start) || _.isNumber(workHours2.end)) ?  workHours2 : null;

    }
    public loadData(displayInfoOverTime?: DisplayInfoOverTime, inputByUser?: boolean, isOpenKDL?: boolean) {
        const self = this;
        if (!_.isNil(displayInfoOverTime)) {

            if (!self.$appContext.modeNew && !inputByUser) { // bind from appovertime within update mode
                let appOverTime = self.$appContext.model.appOverTime;
                self.createWorkInfo(_.get(appOverTime, 'workInfoOp.workType'), _.get(appOverTime, 'workInfoOp.workTime'));
                self.createBreakTime(_.map(_.get(appOverTime, 'breakTimeOp'), (x: any) => {
                    
                    return {
                        start: x.timeZone.startTime,
                        end: x.timeZone.endTime
                    };
                }));
                self.createWorkHours(false);


                return;
            }
            let codeType = isOpenKDL ? self.workInfo.workType.code : _.get(displayInfoOverTime, 'infoWithDateApplicationOp.workTypeCD');
            let codeTime = isOpenKDL ? self.workInfo.workTime.code : _.get(displayInfoOverTime, 'infoWithDateApplicationOp.workTimeCD');
            self.createWorkInfo(codeType, codeTime);
            self.createBreakTime(_.get(displayInfoOverTime, 'infoWithDateApplicationOp.breakTime.timeZones'));

            // load work hours
            self.createWorkHours(true);

            return;
        } else {
            self.createWorkInfo();
            self.createBreakTime();
        }

           

    }
    // bind when change date, select worktype or worktime
    public createHoursWorkTime() {
        const self = this;
        if (!_.isNil(self.workHours1)) {
            self.workInfo.workTime.time = self.handleTimeWithDay(self.workHours1.start) + '～' + self.handleTimeWithDay(self.workHours1.end);
        }
    }

    public handleTimeWithDay(time: number) {
        const self = this;
        const nameTime = '当日';
        if (!time) {

            return;
        }

        return (0 <= time && time < 1440) ? nameTime + self.$dt.timewd(time) : self.$dt.timewd(time);
    }

    public createWorkInfo(codeType?: string, codeTime?: string) {
        const self = this;

        let workType = {} as Work;
        workType.code = codeType || '';

        let workTime = {} as Work;
        workTime.code = codeTime || '';
        let displayInfoOverTime = self.$appContext.model.displayInfoOverTime;
        if (displayInfoOverTime) {
            let workTypes = displayInfoOverTime.infoBaseDateOutput.worktypes;
            let resultWorkType = 
                _.find(workTypes, (i: any) => i.workTypeCode == workType.code);
            workType.name = resultWorkType ? (resultWorkType.name || '')  : self.$i18n('KAFS05_55');

            let workTimes = displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst;
            let resultWorkTime = 
                    _.find(workTimes, (i: any) => i.worktimeCode == workTime.code);
            workTime.name = resultWorkTime ? (_.get(resultWorkTime, 'workTimeDisplayName.workTimeName') || '') : self.$i18n('KAFS05_55');
  
        }
        let workInfo = {} as WorkInfo;
        workInfo.workType = workType;
        workInfo.workTime = workTime;

        self.workInfo = workInfo;
    }

    public addBreakHour() {
        const self= this;
        if (self.displayNumberBreakTime < 10) {
            self.displayNumberBreakTime ++;
        }
    }


}
interface WorkInfo {
    workType: Work;
    workTime: Work;
}
interface Work {
    code: string;
    name: string;
    time?: string;
}

interface ValueTime {
    start: number;
    end: number;
}
