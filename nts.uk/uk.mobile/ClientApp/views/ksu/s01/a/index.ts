import { _, Vue, moment } from '@app/provider';
import { Watch, component, Prop } from '@app/core/component';
import { KDL002Component } from '../../../kdl/002';
import { KSUS01BComponent } from '../b/index';

@component({
    name: 'ksus01a',
    route: '/ksu/s01/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        yearMonth: {
            required: true
        }
    },
    constraints: [],
    components: {
        'worktype': KDL002Component,
        'ksus01b': KSUS01BComponent
    },
    methods: {
        resize() {
            let vh = window.innerHeight * 0.01;
            document.documentElement.style.setProperty('--vh', `${vh}px`);
        }
    },
    created() {
        window.addEventListener('resize', this.resize);
    },
    beforeDestroy() {
        document.body.style.removeProperty('overflowY');
        document.body.style.removeProperty('position');
        document.body.style.removeProperty('top');
        document.body.style.removeProperty('width');
    },
    watch: {
        isDetailShow() {
            let scrollPos = 0;
            if (this.isDetailShow) {
                // scrollPos = window.pageYOffset;
                document.body.style.overflowY = 'hidden';
                document.body.style.position = 'fixed';
                document.body.style.top = `-${scrollPos}px`;
                document.body.style.width = '100%';

                return;
            }
            scrollPos = window.pageYOffset;
            document.body.style.removeProperty('overflowY');
            document.body.style.removeProperty('position');
            document.body.style.removeProperty('top');
            document.body.style.removeProperty('width');
        }
    }
})
export class KSUS01AComponent extends Vue {
    public title: string = 'ksus01a';

    public yearMonthOldVal: string = moment().format('YYYYMM');
    public yearMonth: string = moment().format('YYYYMM');
    public startDate: string = '';
    public endDate: string = '';
    public publicOpAtr: boolean = false;
    public workDesiredOpAtr: boolean = false;
    public endDatePublicationPeriod: string = null;

    public today = moment().format('YYYY/MM/DD');

    public dateCellList: Array<DateCell> = [];
    public listWorkSchedule: Array<WorkScheduleDto> = [];
    public listDesiredSubmissionStatusByDate: Array<DesiredSubmissionStatusByDate> = [];
    public listPublicHolidayDto: Array<PublicHolidayDto> = [];

    public dateHeaderList: Array<DateHeader> = [];

    public detailCell: DateCell = null;
    public isDetailShow: boolean = false;
    public rowFocus: number = null;
    public dateIndexFocus: number = null;

    public xDown: number = null;
    public yDown: number = null;

    public memo: string = '';
    public workDesireInputMode: number = WorkDesireInputMode.SHIFT;

    public watchYearMonth(data?: number) {
        let self = this;

        if (data) {
            self.yearMonth = data.toString();
        }
        let diffMonth = moment(self.yearMonth, 'YYYY/MM/DD').diff(moment(self.yearMonthOldVal, 'YYYY/MM/DD'), 'months');

        // self.startDate = moment(self.startDate, 'YYYY/MM/DD').add(diffMonth, 'months').format('YYYY/MM/DD');
        // self.endDate = moment(self.endDate, 'YYYY/MM/DD').add(diffMonth, 'months').format('YYYY/MM/DD');
        self.yearMonthOldVal = self.yearMonth;

        let dataResult = self.getDatePeriodDto(self.startDate, self.endDate, diffMonth, 1);
        self.startDate = dataResult.startDate;
        self.endDate = dataResult.endDate;

        self.showDetail(false);
        self.getInforOnTargetPeriod();
    }

    public getDatePeriodDto(startDate: string, endDate: string, monthChange: number,shiftWorkUnit: number) {
        let startD = '';
        let startE = '';
        let yearMonth = startD.substring(0, 4) + startD.substring(5, 7);
        if (shiftWorkUnit == 1) { //mode month
            if (parseInt(startDate.substring(8, 10)) == 1) {
                startD = moment(startDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
                let lastDate = new Date(parseInt(startD.substring(0, 4)), parseInt(startD.substring(5, 7)), 0).getDate();
                startE = startD.substring(0, 8) + lastDate;
            } else {
                startD = moment(startDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
                startE = moment(endDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
                yearMonth = moment(yearMonth, 'YYYYMM').add(1, 'M').format('YYYYMM');
                let lastDate = new Date(parseInt(yearMonth.substring(0, 4)), parseInt(yearMonth.substring(4, 6)), 0).getDate();
                let endNextMonth = new Date(yearMonth.substring(0, 4) + '/' + yearMonth.substring(4, 6) + '/' + lastDate);
                let date = new Date(startE);
                if (date < endNextMonth) {
                    startE = startE.substring(0, 8) + '/' + lastDate;
                }
            }
        } else { //mode week
            startD = moment(startDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
            startE = moment(startD).add(6, 'days').format('YYYY/MM/DD').toString();
            
        }
        
        let dataResult = {
            startDate: startD,
            endDate: startE
        };

        return dataResult;
    }

    public created() {
        let self = this;
        self.initData();
        self.loadData();
    }

    public mounted() {
        let self = this;
        let containerFluid = document.getElementsByClassName('container-fluid');
        if (containerFluid.length > 0) {
            containerFluid[0].className = 'container-fluid px-3'; //#115384
        }
    }

    public initData() {
        let self = this;
        self.$mask('show');
        self.$http.post('at', API.start).then((res: any) => {
            let data: InitInformation = res.data;

            self.startDate = data.start;
            self.endDate = data.end;
            self.endDatePublicationPeriod = data.endDatePublicationPeriod;
            self.publicOpAtr = data.publicOpAtr;
            self.workDesiredOpAtr = data.workDesiredOpAtr;

            self.yearMonth = moment(self.startDate, 'YYYY/MM/DD').format('YYYYMM');
            self.yearMonthOldVal = moment(self.startDate, 'YYYY/MM/DD').format('YYYYMM');

            self.getInforOnTargetPeriod();
        }).catch((error: any) => {
            self.errorHandler(error);
        }).then(() => {});

        self.createDateHeaderList();
        self.createDateCellList();
    }

    public getInforOnTargetPeriod() {
        let self = this;

        let desiredPeriodWork: PeriodCommand = {
            start: '',
            end: ''
        };
        if (self.workDesiredOpAtr) {
            desiredPeriodWork.start = self.startDate;
            desiredPeriodWork.end = self.endDate;
        }

        let scheduledWorkingPeriod: PeriodCommand = {
            start: '',
            end: ''
        };
        if (self.publicOpAtr) {
            let diffStart = moment(self.endDatePublicationPeriod, 'YYYY/MM/DD').diff(moment(self.startDate, 'YYYY/MM/DD'), 'days');
            if (diffStart >= 0) {
                scheduledWorkingPeriod.start = self.startDate;
                let diffEnd = moment(self.endDate, 'YYYY/MM/DD').diff(moment(self.endDatePublicationPeriod, 'YYYY/MM/DD'), 'days');
                if (diffEnd >= 0) {
                    scheduledWorkingPeriod.end = self.endDatePublicationPeriod;
                } else {
                    scheduledWorkingPeriod.end = self.endDate;
                }
            }
        } else {
            scheduledWorkingPeriod.start = self.startDate;
            scheduledWorkingPeriod.end = self.endDate;
        }

        let targetPeriod: PeriodCommand = {
            start: self.startDate,
            end: self.endDate
        };

        let command: InforOnTargetPeriodInput = {
            desiredPeriodWork,
            scheduledWorkingPeriod,
            targetPeriod
        };
        self.$mask('show');
        self.$http.post('at', API.changeDatePeriod, command).then((res: any) => {
            let data: InforOnTargetPeriodDto = res.data;
            self.listWorkSchedule = data.listWorkSchedule;
            self.listDesiredSubmissionStatusByDate = data.listDesiredSubmissionStatusByDate;
            self.listPublicHolidayDto = data.listPublicHolidayDto;
            self.bindDateCellList();
        }).catch((error: any) => {
            self.errorHandler(error);
        }).then(() => self.$mask('hide'));
    }

    public createDateHeaderList() {
        let self = this;
        for (let index = 0; index < 7; index++) {
            self.dateHeaderList.push({
                headerName:  self.$i18n('KSUS01_' + (3 + index)),
                weekDayIndex: index
            });
        }
    }

    public createDateCellList() {
        let self = this;
        //1: 一ヶ月間以内の連続期間を表示される
        for (let index = 0; index < 6 * 7; index++) {
            self.dateCellList.push({
                isActive: false,
                isFocused: false,
                rowNumber: Math.floor(index / 7),
                weekDayIndex: index % 7,
                displayData: {} as DisplayData,
                workScheduleStyle: 'padding: 0.25em 1em; font-weight: bold; border-radius: 0.25rem; display: inline-block;'
            });
        }
    }

    public loadData() {
        let self = this;
        self.bindDateCellList();
    }

    public handleTouchStart(evt) {
        let self = this;
        self.xDown = evt.touches[0].clientX;
        self.yDown = evt.touches[0].clientY;
    }

    public handleTouchEnd(evt: TouchEvent) {
        let self = this;
        if (self.rowFocus == null || !self.xDown || !self.yDown) {
            return;
        }

        let xDiff = self.xDown - evt.changedTouches[0].clientX;
        let yDiff = self.yDown - evt.changedTouches[0].clientY;

        self.xDown = null;
        self.yDown = null;

        if (Math.abs(xDiff) > 50 && Math.abs(xDiff) > Math.abs(yDiff)) {
            if (xDiff > 0) {    // right-left swipe
                self.changeWeek(true);
            } else {    //left-right swipe
                self.changeWeek(false);
            }

            return;
        }
    }

    public changeWeek(isNext: boolean) {
        let self = this;
        if (isNext && self.rowFocus < 5) {
            let findResult = _.findLast(self.dateCellList, (el) => {
                return el.isActive;
            });
            if (findResult && findResult.rowNumber == self.rowFocus) {
                return;
            }
            self.rowFocus += 1;
            self.changeFocus(isNext);
        }
        if (!isNext && self.rowFocus > 0) {
            self.rowFocus -= 1;
            self.changeFocus(isNext);
        }
    }

    public changeFocus(isNext: boolean) {
        let self = this;
        let findResult = self.dateCellList.filter((el) => {
            return el.rowNumber == self.rowFocus && el.weekDayIndex == self.dateIndexFocus;
        });
        if (findResult.length > 0 && findResult[0].isActive) {
            self.dateCellList.map((el) => {el.isFocused = false;});
            findResult[0].isFocused = true;
            self.bindDetail(findResult[0]);
        } else {
            if (isNext) {
                let findResult = _.findLast(self.dateCellList, (el) => {
                    return el.isActive;
                });
                self.dateCellList.map((el) => {el.isFocused = false;});
                findResult.isFocused = true;
                self.dateIndexFocus = findResult.weekDayIndex;
                self.bindDetail(findResult);
            } else {
                let findResult = _.find(self.dateCellList, (el) => {
                    return el.isActive;
                });
                self.dateCellList.map((el) => {el.isFocused = false;});
                findResult.isFocused = true;
                self.dateIndexFocus = findResult.weekDayIndex;
                self.bindDetail(findResult);
            }
        }
    }

    public bindDateCellList() {
        let self = this;
        let isFirstDay = false;
        let isActive = false;

        let count = 0;
        self.dateCellList = self.dateCellList.map((el, index) => {
            el = {
                isActive: false,
                isFocused: false,
                rowNumber: el.rowNumber,
                weekDayIndex: el.weekDayIndex,
                displayData: {} as DisplayData,
                workScheduleStyle: 'padding: 0.25em 1em; font-weight: bold; border-radius: 0.25rem; display: inline-block;'
            };
            if (!isFirstDay && el.weekDayIndex == moment(self.startDate).day()) {
                isActive = true;
                isFirstDay = true;
            }
            if (isActive) {
                el.date = moment(self.startDate).add(count, 'days').format('YYYY/MM/DD');
                if (el.date > self.endDate) {
                    isActive = false;
                    el.date = null;
                }
                if (el.date) {
                    el.isActive = self.publicOpAtr ? (isActive ? el.date <= self.endDatePublicationPeriod : isActive) : isActive;
                    el.formatedDate = moment(el.date).format('D');

                    //default
                    el.displayData.workScheduleTimeZone = [];
                    el.displayData.listWorkDesire = [];

                    let desiredSubmissionStatusByDate = _.find(self.listDesiredSubmissionStatusByDate, (desiredSubmissionStatusByDate) => {
                        return desiredSubmissionStatusByDate.date == el.date;
                    });
                    if (desiredSubmissionStatusByDate) {
                        el.displayData.workDesireDate = desiredSubmissionStatusByDate.date;
                        el.displayData.workDesireStatus = desiredSubmissionStatusByDate.status;
                    }
                    let workSchedule = _.find(self.listWorkSchedule, (workSchedule) => {
                        return workSchedule.ymd == el.date;
                    });
                    if (workSchedule) {
                        el.displayData.workScheduleDate = workSchedule.ymd;
                        el.displayData.workScheduleAtr = workSchedule.workAtr;
                        el.displayData.workScheduleTimeZone = workSchedule.listAttendaceDto;
                        el.displayData.workScheduleName = workSchedule.shiftMaster.shiftMasterName;
                        el.displayData.workScheduleColor = workSchedule.shiftMaster.color;
                    } else {
                        el.displayData.workScheduleAtr = null;
                        el.displayData.workScheduleName = self.$i18n('KSUS01_14');
                        el.displayData.workScheduleColor = 'fabf8f';
                    }
                    el.workScheduleStyle = self.setWorkScheduleStyle(el);
                    // el.workDesireStyle = self.setWorkDesireStyle(el);

                    if (el.formatedDate == '1') {
                        el.formatedDate = moment(el.date).format('M/D');
                    }
                    count++;
                }
            }

            return el;
        });
    }

    public setWorkScheduleStyle(dateCell: DateCell) {
        return dateCell.workScheduleStyle + ' background-color: #' + dateCell.displayData.workScheduleColor + ';';
    }

    public bindDetail(dateCell: DateCell) {
        let self = this;
        self.detailCell = dateCell;
        self.detailCell.formatedLongMdwDate = moment(self.detailCell.date).format('M月D日 (ddd)');

        let command: InforOnTargetDateInput = {
            desiredSubmissionStatus: self.detailCell.displayData.workDesireStatus,
            workHolidayAtr: self.detailCell.displayData.workScheduleAtr,
            targetDate: self.detailCell.date
        };

        self.$mask('show');
        self.$http.post('at', API.getDateDetail, command).then((res: any) => {
            let data: InforOnTargetDateDto = res.data;
            self.detailCell.displayData.otherStaffs = data.businessNames.join('、');
            self.detailCell.displayData.workDesireMemo = data.memo;
            self.detailCell.displayData.workScheduleTimeZone = data.listAttendanceDto;
            self.detailCell.displayData.listWorkDesire = [];
            self.workDesireInputMode = data.type;
            data.listWorkInforAndTimeZone.map((el) => {
                let detailWorkDesire: DetailWorkDesire = {};

                detailWorkDesire.workDesireName = el.wishName;
                detailWorkDesire.workDesireColor = el.color;
                detailWorkDesire.workDesireAtr = el.workStyle;
                detailWorkDesire.workDesireTimeZone = el.timezones;
                detailWorkDesire.workDesireStyle = self.setWorkDesireStyle(detailWorkDesire);

                self.detailCell.displayData.listWorkDesire.push(detailWorkDesire);
            });

            self.memo = self.detailCell.displayData.workDesireMemo;
        }).catch((error: any) => {
            self.errorHandler(error);
        }).then(() => self.$mask('hide'));
    }

    public setWorkDesireStyle(detailWorkDesire: DetailWorkDesire) {
        let defaultStyle = 'padding: 0.25em 1em; font-weight: bold; border-radius: 0.25rem; display: inline-block;';

        return defaultStyle + ' background-color: #' + detailWorkDesire.workDesireColor + ';';
    }

    //3: 年月を変更する方法
    //■next/backボタンをクリックする
    public changeYearMonth(isNext: boolean) {
        let self = this;
        self.yearMonth = isNext ? moment(self.yearMonth, 'YYYYMM').add(1, 'months').format('YYYYMM') : moment(self.yearMonth, 'YYYYMM').add(-1, 'months').format('YYYYMM');
        self.watchYearMonth();
    }

    //3: 年月を変更する方法
    //■「今月に戻る」にクリックする
    public backCurrentMonth() {
        let self = this;
        self.yearMonth = moment().format('YYYYMM');
        self.watchYearMonth();
    }

    public showDetail(isShow: boolean, dateCell?: DateCell) {
        let self = this;
        if (dateCell) {
            if (!dateCell.isActive) {
                return;
            }
            self.dateCellList.map((el) => {el.isFocused = false;});
            dateCell.isFocused = true;
        }
        if (isShow) {
            self.rowFocus = dateCell.rowNumber;
            self.dateIndexFocus = dateCell.weekDayIndex;
            self.bindDetail(dateCell);
        } else {
            self.detailCell = null;
            self.rowFocus = null;
            self.dateIndexFocus = null;
        }
        self.isDetailShow = isShow;
    }

    //2: 背景色と文字色
    //■曜日ヘッダ
    public setHeaderColor(weekDayIndex) {
        let defaultClass = 'date-header ';
        switch (weekDayIndex) {
            case WeekDay.SUN:
                return defaultClass + 'uk-bg-schedule-sunday uk-text-red';
            case WeekDay.SAT:
                return defaultClass + 'uk-bg-schedule-saturday uk-text-blue';
            default:
                return defaultClass + 'uk-bg-disable uk-text-weekdays';
        }
    }

    public setCellColor(dateCell: DateCell) {
        let self = this;

        let cellClass = 'date-cell ';
        if (dateCell.isActive) {
            if (dateCell.isFocused) {
                cellClass += 'uk-bg-schedule-focus ';
            }
        } else {
            if (dateCell.date && dateCell.date.length > 0) {
                cellClass += 'uk-bg-silver ';
            } else {
                cellClass += 'uk-bg-white-smoke ';
            }
        }
        let publicHoliday = _.find(self.listPublicHolidayDto, (publicHoliday) => {
            return publicHoliday.date == dateCell.date;
        });
        if (publicHoliday) {
            return cellClass + 'uk-text-red';
        }
        switch (dateCell.weekDayIndex) {
            case WeekDay.SUN:
                return cellClass + 'uk-text-red';
            case WeekDay.SAT:
                return cellClass + 'uk-text-blue';
            default:
                return cellClass;
        }
    }

    public errorHandler(error: any) {
        const vm = this;
        switch (error.messageId) {
            case 'Msg_2049':
                vm.$modal.error({ messageId: error.messageId, messageParams: error.parameterIds }).then(() => {
                    vm.$mask('hide');
                    vm.$goto('ccg008a');
                });
                break;
            default:
                vm.$modal.error({ messageId: error.messageId, messageParams: error.parameterIds }).then(() => {
                    vm.$mask('hide');
                    vm.$goto('ccg008a');
                });
                break;
        }
    }

    public openKSUS01B() {
        let self = this;
        let params: ParamB = {
            // listWorkSchedule: self.listWorkSchedule,
            baseYM: self.yearMonth,
            targetPeriod: {
                start: self.startDate,
                end: self.endDate,
            },
        };
        self.$modal(
            'ksus01b',
            params,
            { type: 'dropback' }
        ).then((v) => {
            console.log('close ksus01b and return ', v);
        });
    }
}

const API = {
    start: 'screen/at/ksus01/a/getinforinitial',
    getDateDetail: 'screen/at/ksus01/a/getinfortargetdate',
    changeDatePeriod: 'screen/at/ksus01/a/getinfortargetperiod',
};

// for UI
export interface DateCell {
    isActive: boolean;
    weekDayIndex: WeekDay;
    rowNumber: number;
    isFocused: boolean;
    displayData: DisplayData;
    workScheduleStyle: string;
    
    date?: string;
    formatedDate?: string;
    formatedLongMdwDate?: string;
}

export interface DisplayData {
    workDesireDate?: string;
    workDesireStatus?: WorkDesireStatus;
    workDesireMemo?: string;
    listWorkDesire?: Array<DetailWorkDesire>;

    workScheduleDate?: string;
    workScheduleName?: string;
    workScheduleColor?: string;
    workScheduleAtr?: WorkHolidayAtr;
    workScheduleTimeZone?: Array<AttendanceDto>;
    otherStaffs?: string;
}

export interface DetailWorkDesire {
    workDesireName?: string;
    workDesireColor?: string;
    workDesireAtr?: WorkHolidayAtr;
    workDesireTimeZone?: Array<TimeZoneDto>;
    workDesireStyle?: string;
}

export interface DateHeader {
    headerName: string;
    weekDayIndex: WeekDay;
}

// enum
export enum WeekDay {
    SUN,
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT
}

export enum WorkDesireStatus {
    NO_HOPE,
    HOLIDAY_HOPE,
    COMMUTING_HOPE
}

export enum WorkHolidayAtr {
    ONE_DAY_REST,
    MORNING_WORK,
    AFTERNOON_WORK,
    ONE_DAY_WORK
}

export enum WorkDesireInputMode {
    HOLIDAY,
    SHIFT
}

// api input
export interface InforOnTargetDateInput {
    desiredSubmissionStatus: number;
    workHolidayAtr: number;
    targetDate: String;
}

export interface InforOnTargetPeriodInput {
    desiredPeriodWork: PeriodCommand;
    scheduledWorkingPeriod: PeriodCommand;
    targetPeriod: PeriodCommand;
}

export interface PeriodCommand {
    start: string;
    end: string;
}

export interface ParamB {
    // listWorkSchedule: Array<WorkScheduleDto>;
    baseYM: string;
    targetPeriod: PeriodCommand;
}

// api dto
export interface InitInformation {
    publicOpAtr: boolean;
    workDesiredOpAtr: boolean;
    endDatePublicationPeriod: string;
    start: string;
    end: string;
}

export interface InforOnTargetPeriodDto {
    listWorkSchedule: Array<WorkScheduleDto>;
    listDesiredSubmissionStatusByDate: Array<DesiredSubmissionStatusByDate>;
    listPublicHolidayDto: Array<PublicHolidayDto>;
}

export interface PublicHolidayDto {
    companyId: string;
    date: string;
    holidayName: string;
}

export interface WorkScheduleDto {
    ymd: string;
    shiftMaster: ShiftMasterDto;
    workAtr: number;
    listAttendaceDto: Array<AttendanceDto>;
    workInfo: WorkInformationDto;
}

export interface WorkInformationDto {
    workType: string;
    workTime: string;
}

export interface ShiftMasterDto {
    shiftMasterName: string;
    color: string;
}

export interface DesiredSubmissionStatusByDate {
    date: string;
    status: number;
}

export interface InforOnTargetDateDto {
    businessNames: Array<string>;
    listWorkInforAndTimeZone: Array<WorkInforAndTimeZoneByShiftMasterDto>;
    memo: string;
    listAttendanceDto: Array<AttendanceDto>;
    type: number;
}

export interface WorkInforAndTimeZoneByShiftMasterDto {
    wishName: string;
    timezones: Array<TimeZoneDto>;
    workStyle: WorkHolidayAtr;
    color: string;
}

export interface TimeZoneDto {
    start: string;
    end: string;
}

export interface AttendanceDto {
    attendanceStamp: string;
    leaveStamp: string;
}


