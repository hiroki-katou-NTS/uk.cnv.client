import { _, Vue } from '@app/provider';
import { component, Watch } from '@app/core/component';
import { KafS00SubP1Component } from 'views/kaf/s00/sub/p1';
import { KafS00SubP2Component } from 'views/kaf/s00/sub/p2';
import { KafS00SubP3Component } from 'views/kaf/s00/sub/p3';
import { KafS00AComponent, KafS00BComponent, KafS00CComponent } from 'views/kaf/s00';
import { TimeZoneWithWorkNo, BreakTime, TimeZoneNew, WorkHoursDto, AppHdWorkDispInfo, ParamBreakTime } from '../a/define.interface';
import { KafS10Component} from '../a/index';

@component({
    name: 'kafs10step1',
    route: '/kaf/s10/step1',
    style: require('./style.scss'),
    template: require('./index.vue'),
    validations: {
        // breakTimes: {
        //     valueHours: {
        //         timeRange: true,
        //     }
        // },
        workInfo: {
            workType: {
                code: {
                    required: true,
                }
            },
            workTime: {
                code: {
                    required: true,
                }
            }
        },
        workHours1: {
            required: true,
            timeRange: true,
        },
        workHours2: {
            required: false,
            timeRange: true
        }
    },
    constraints: [],
    components: {
        'kafs00subp1': KafS00SubP1Component,
        'kafs00subp2': KafS00SubP2Component,
        'kafs00subp3': KafS00SubP3Component,
        'kafs00-a': KafS00AComponent,
        'kafs00-b': KafS00BComponent,
        'kafs00-c': KafS00CComponent
    }
})
export class KafS10Step1Component extends Vue {
    public title: string = 'KafS10Step1';

    public workInfo: WorkInfo = {} as WorkInfo;

    public workHours1: ValueTime = null;

    public workHours2?: ValueTime = null;

    public goWorkAtr?: boolean = false;

    public backHomeAtr?: boolean = false;

    public isGoWorkResource?: Array<Object> = [];

    public isBackHomeResource?: Array<Object> = [];

    public breakTimes: Array<BreakTime> = [];

    public displayNumberBreakTime = 3;

    public isFirstModeUpdate: boolean = true;

    public worktimeTimeLabel: string = null;

    public get isAddFrameBreakTime() {
        const self = this;

        return self.displayNumberBreakTime < 10; 
    }

    public created() {
        const self = this;  

        if (self.$appContext.getNumb == 1) {
            self.pgName = 'kafs10step1';
        } else if (self.$appContext.getNumb == 2) {
            self.pgName = 'kafs10step2';
        } else {
            self.pgName = 'kafs10step3';
        }
        self.isGoWorkResource = [{
            code: true,
            text: 'KAFS07_11'
        }, {
            code: false,
            text: 'KAFS07_12'
        }];
        self.isBackHomeResource = [{
            code: true,
            text: 'KAFS07_13'
        }, {
            code: false,
            text: 'KAFS07_14'
        }];
        self.loadData();
    }

    public mounted() {
        const self = this;
    }

    get $appContext(): KafS10Component {
        const self = this;

        return self.$parent as KafS10Component;
    }

    public loadData(appHdWorkDispInfo?: AppHdWorkDispInfo, inputByUser?: boolean) {
        const self = this;
        if (!_.isNil(appHdWorkDispInfo)) {

            if (!self.$appContext.modeNew && !inputByUser) { // bind from appovertime within update mode
                let appHolidayWork = self.$appContext.model.appHolidayWork;
                let nameType = _.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.initWorkTypeName');
                let nameTime = _.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.initWorkTimeName');
                self.createWorkInfo(_.get(appHolidayWork, 'workInformation.workType'), _.get(appHolidayWork, 'workInformation.workTime'));
                self.createBreakTime(_.map(_.get(appHolidayWork, 'breakTimeList'), (x: any) => {
                    
                    return {
                        start: x.timeZone.startTime,
                        end: x.timeZone.endTime
                    };
                }));
                self.createWorkHours(false);
                self.goWorkAtr = appHolidayWork.goWorkAtr;
                self.backHomeAtr = appHolidayWork.backHomeAtr;
                self.createHoursWorkTime();
                
                return;
            }
            let codeType = _.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.initWorkType');
            let codeTime = _.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.initWorkTime');
            let nameType = _.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.initWorkTypeName');
            let nameTime = _.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.initWorkTimeName');
            self.createWorkInfo(codeType, codeTime, nameType, nameTime);
            self.createBreakTime(_.get(appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.breakTimeZoneSettingList.timeZones'));

            // load work hours
            self.createWorkHours(true);

            return;
        } else {
            self.createWorkInfo();
            self.createBreakTime();
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

    public createBreakTimeModel() {
        const self = this;
        for (let i = 0; i < 10; i++) {
            let item = {} as BreakTime;
            item.valueHours = null as ValueTime;
            item.frameNo = i + 1;
            item.title = self.$i18n('KAFS10_10', String(item.frameNo));
            self.breakTimes.push(item);
        }
    }

    public createWorkHours(mode: boolean) {
        const self = this;
 
        let workHours = _.get(self.$appContext.model.appHdWorkDispInfo, 'hdWorkDispInfoWithDateOutput.workHours') as WorkHoursDto;
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
            _.sortBy(self.$appContext.model.appHolidayWork.workingTimeList, (i: TimeZoneWithWorkNo) => i.workNo);
            let workHoursOp1 = _.get(self.$appContext.model.appHolidayWork, 'workingTimeList[0].timeZone') as TimeZoneNew;
            let workHoursOp2 = _.get(self.$appContext.model.appHolidayWork, 'workingTimeList[1].timeZone') as TimeZoneNew;
            workHours1.start = workHoursOp1 ? workHoursOp1.startTime : null;
            workHours1.end = workHoursOp1 ? workHoursOp1.endTime : null;

            workHours2.start = workHoursOp2 ? workHoursOp2.startTime : null;
            workHours2.end = workHoursOp2 ? workHoursOp2.endTime : null;

        }
        self.workHours1 = (_.isNumber(workHours1.start) || _.isNumber(workHours1.end)) ?  workHours1 : null;
        self.workHours2 = (_.isNumber(workHours2.start) || _.isNumber(workHours2.end)) ?  workHours2 : null;

    }

    public createWorkInfo(codeType?: string, codeTime?: string, nameType?: string, nameTime?: string) {
        const self = this;

        let workType = {} as Work;
        workType.code = codeType || '';

        let workTime = {} as Work;
        workTime.code = codeTime || '';
        let appHdWorkDispInfo = self.$appContext.model.appHdWorkDispInfo;
        if (appHdWorkDispInfo) {
            if (nameType) {
                workType.name = nameType;
            } else {
                let workTypes = appHdWorkDispInfo.hdWorkDispInfoWithDateOutput.workTypeList;
                let resultWorkType = 
                    _.find(workTypes, (i: any) => i.workTypeCode == workType.code);
                workType.name = resultWorkType ? (resultWorkType.name || '')  : self.$i18n('KAFS10_28');
            }
            if (nameTime) {
                workTime.name = nameTime;
            } else {
                let workTimes = appHdWorkDispInfo.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst;
                let resultWorkTime = 
                        _.find(workTimes, (i: any) => i.worktimeCode == workTime.code);
                workTime.name = resultWorkTime ? (_.get(resultWorkTime, 'workTimeDisplayName.workTimeName') || '') : self.$i18n('KAFS10_28');
            }
        }
        let workInfo = {} as WorkInfo;
        workInfo.workType = workType;
        workInfo.workTime = workTime;

        self.workInfo = workInfo;
    }

    @Watch('workHours1', {deep: true})
    public changeWorkHours1(data: ValueTime) {
        const self = this;
        if (_.isNil(_.get(data,'start')) || _.isNil(_.get(data, 'end')) || (self.isFirstModeUpdate && !self.$appContext.modeNew)) {
            self.isFirstModeUpdate = false;

            return;
        }
        let parent = self.$parent as KafS10Component;
        let command = {} as ParamBreakTime;
        command.companyId = parent.user.companyId;
        command.appDate = parent.application.appDate;
        command.workTypeCode = self.workInfo.workType.code;
        command.workTimeCode = self.workInfo.workTime.code;
        command.startTime = self.workHours1.start;
        command.endTime = self.workHours1.end;
        command.appHdWorkDispInfo = parent.model.appHdWorkDispInfo;
        parent.getBreakTime(command);
    }

    @Watch('workHours2', {deep: true})
    public changeWorkHours2(data: any) {
        console.log(data);
    }

    // bind when change date, select worktype or worktime
    public createHoursWorkTime() {
        const self = this;
        if (!_.isNil(self.workHours1)) {
            self.workInfo.workTime.time = self.handleTimeWithDay(self.workHours1.start) + '～' + self.handleTimeWithDay(self.workHours1.end);
        }

        let wkTimeCodes = [self.getWorkTime()];
        self.$http.post('at', API.getWorkTimeByCDLst, { wkTimeCodes }).then((data: any) => {
            let workTimeInfo = _.find(data.data, (o: any) => o.code == self.getWorkTime());
            if (workTimeInfo) {
                self.worktimeTimeLabel = workTimeInfo.workTime1;
            } else {
                self.worktimeTimeLabel = '';
            }
        });
    }

    public handleTimeWithDay(time: number) {
        const self = this;
        const nameTime = '当日';
        if (_.isNil(time)) {

            return;
        }

        return (0 <= time && time < 1440) ? nameTime + self.$dt.timewd(time) : self.$dt.timewd(time);
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

    public getWorktimeTimeLabel() {
        const self = this;

        return self.worktimeTimeLabel;
    }

    public setWorkType(
        workTypeCD?: string,
        workTypeName?: string) {
        const self = this;
        let workType = {} as Work;
        workType.code = workTypeCD;
        workType.name = workTypeName;
        let workTime = {} as Work;
        workTime.code = self.workInfo.workTime.code;
        workTime.name = self.workInfo.workTime.name;
        let workInfo = {} as WorkInfo;
        workInfo.workType = workType;
        workInfo.workTime = workTime;
        self.workInfo = workInfo;
    }

    public setWorkTime(
        workTimeCD?: string,
        workTimeName?: string) {
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
    }

    public setWorkInfo(
        workTypeCD?: string,
        workTypeName?: string,
        workTimeCD?: string,
        workTimeName?: string) {
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

const API = {
    getWorkTimeByCDLst: 'at/shared/worktimesetting/get_worktime_by_codes'
};
