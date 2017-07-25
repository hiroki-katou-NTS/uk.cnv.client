module nts.uk.at.view.kdl023.a.viewmodel {

    import WorkDayDivision = service.model.WorkDayDivision;
    import WeeklyWorkSetting = service.model.WeeklyWorkSetting;
    import PublicHoliday = service.model.PublicHoliday;
    import ReflectionMethod = service.model.ReflectionMethod;
    import WorkType = service.model.WorkType;
    import WorkTime = service.model.WorkTime;

    export class ScreenModel {
        itemList: KnockoutObservableArray<ItemModelCbb1>;
        selectedCode: KnockoutObservable<string>;
        listWorkType: KnockoutObservableArray<WorkType>;
        listWorkTime: KnockoutObservableArray<WorkTime>;

        patternReflection: PatternReflection;
        dailyPatternSetting: service.model.DailyPatternSetting;
        weeklyWorkSetting: WeeklyWorkSetting;
        listHoliday: Array<any>;

        // Calendar component
        calendarData: KnockoutObservable<any>;
        yearMonthPicked: KnockoutObservable<number>;
        cssRangerYM: any;
        optionDates: KnockoutObservableArray<OptionDate>;
        firstDay: number;
        yearMonth: KnockoutObservable<number>;
        startDate: number;
        endDate: number;
        workplaceId: KnockoutObservable<string>;
        eventDisplay: KnockoutObservable<boolean>;
        eventUpdatable: KnockoutObservable<boolean>;
        holidayDisplay: KnockoutObservable<boolean>;
        cellButtonDisplay: KnockoutObservable<boolean>;
        workplaceName: KnockoutObservable<string>;

        constructor() {
            let self = this;
            self.listHoliday = [];
            self.itemList = ko.observableArray([
                new ItemModelCbb1('1', '基本給'),
                new ItemModelCbb1('2', '役職手当'),
                new ItemModelCbb1('3', '基本給')
            ]);
            self.listWorkType = ko.observableArray([]);
            self.listWorkTime = ko.observableArray([]);
            self.selectedCode = ko.observable('1');

            self.dailyPatternSetting = {};
            self.dailyPatternSetting.patternCode = 'code';
            self.dailyPatternSetting.patternName = 'name';
            self.dailyPatternSetting.workPatterns = [{
                dispOrder: 1,
                workTypeCode: 'đi làm vì đam mê',
                workingHoursCode: '',
                days: 1,
            }, {
                    dispOrder: 2,
                    workTypeCode: 'đi làm cho vui',
                    workingHoursCode: '',
                    days: 2,
                }, {
                    dispOrder: 3,
                    workTypeCode: 'hư hỏng',
                    workingHoursCode: 'đi làm cho đỡ',
                    days: 3,
                }];

            // Calendar component
            self.yearMonthPicked = ko.observable(201707);
            self.cssRangerYM = {
            };
            self.optionDates = ko.observableArray<OptionDate>([]);
            self.firstDay = 0; // sunday.
            self.startDate = 1;
            self.endDate = 31;
            self.workplaceId = ko.observable("0");
            self.workplaceName = ko.observable("");
            self.eventDisplay = ko.observable(false);
            self.eventUpdatable = ko.observable(false);
            self.holidayDisplay = ko.observable(true);
            self.cellButtonDisplay = ko.observable(false);

        }

        /**
         * Start page event.
         */
        public startPage(): JQueryPromise<any> {
            nts.uk.ui.block.invisible();
            let self = this;
            let dfd = $.Deferred();
            $.when(service.findWeeklyWorkSetting(),
                service.getHolidayByListDate(self.getListDateOfMonth()),
                service.getAllWorktype())
                .done(function(
                    weeklyWorkSetting: WeeklyWorkSetting,
                    listHoliday,
                    listWorkType) {

                    // Set list holiday
                    self.listHoliday = listHoliday;

                    // Set list worktype.
                    self.listWorkType(listWorkType);

                    // Set weeklyWorkSetting
                    self.weeklyWorkSetting = weeklyWorkSetting;

                    // Set optionDates.
                    self.optionDates(self.getOptionDates());

                    // Set list working hours.
                    service.getListWorkingHour([]).done(list => {
                        self.listWorkTime(list);
                    });

                    //TODO: tam thoi the da
                    self.loadPatternReflection();

                    dfd.resolve();
                }).fail(res => {
                    nts.uk.ui.dialog.alert(res.message);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            return dfd.promise();
        }

        /**
         * Forward button clicked
         */
        public forward(): void {
            let self = this;
            self.forwardOneDay();
            self.optionDates(self.getOptionDates());
            // Set focus control
            $('#component-calendar-kcp006').focus();
        }

        /**
         * Backward button clicked.
         */
        public backward(): void {
            let self = this;
            self.backwardOneDay();
            self.optionDates(self.getOptionDates());
            // Set focus control
            $('#component-calendar-kcp006').focus();
        }

        /**
         * Event when click apply button.
         */
        public onBtnApplySettingClicked(): void {
            let self = this;
            nts.uk.ui.block.invisible();
            // Reload calendar
            self.optionDates(self.getOptionDates());
            // Set focus control
            $('#component-calendar-kcp006').focus();
            service.save('empId', ko.toJS(self.patternReflection)).always(() => {
                nts.uk.ui.block.clear();
            });
        }

        /**
         * Load patternReflection.
         */
        private loadPatternReflection(): JQueryPromise<void> {
            let self = this;
            let dfd = $.Deferred<void>();
            service.find('empId').done(function(patternReflection: service.model.PatternReflection) {
                 // Select first item if worktype code not exist.
                    if (!patternReflection.statutorySetting.workTypeCode) {
                        patternReflection.statutorySetting.workTypeCode = self.listWorkType()[0].workTypeCode;
                    }
                    if (!patternReflection.nonStatutorySetting.workTypeCode) {
                        patternReflection.nonStatutorySetting.workTypeCode = self.listWorkType()[0].workTypeCode;
                    }
                    if (!patternReflection.holidaySetting.workTypeCode) {
                        patternReflection.holidaySetting.workTypeCode = self.listWorkType()[0].workTypeCode;
                    }

                    // Set patternReflection.
                    self.patternReflection = new PatternReflection(patternReflection);
            });
            dfd.resolve();
            return dfd.promise();
        }

        /**
         * Get option dates for calendar.
         */
        private getOptionDates(): Array<OptionDate> {
            let self = this;
            let parsedYm = nts.uk.time.formatYearMonth(self.yearMonthPicked());
            let currentDate = moment(parsedYm);
            let lastDateOfMonth = moment(parsedYm).endOf('month');
            let result: Array<OptionDate> = [];

            while (currentDate.isSameOrBefore(lastDateOfMonth)) {
                // Work patterns loop.
                self.dailyPatternSetting.workPatterns.forEach(dailyPatternValue => {
                    let dayOfPattern = 1;
                    // Day of pattern loop.
                    while (dayOfPattern <= dailyPatternValue.days) {
                        // End loop if end of month.
                        if (currentDate.isAfter(lastDateOfMonth)) {
                            break;
                        }

                        // Neu la holiday.
                        let isAHoliday = self.isHoliday(currentDate);
                        if (self.isHolidaySettingChecked() && isAHoliday) {
                            result.push({
                                start: currentDate.format('YYYY-MM-DD'),
                                textColor: 'red',
                                backgroundColor: 'white',
                                listText: [
                                    self.getWorktypeNameByCode(self.patternReflection.holidaySetting.workTypeCode())
                                ]
                            });
                        }
                        // Neu khong phai la holiday
                        else {
                            // Ngay nghi theo luat
                            if (self.isStatutorySettingChecked() && self.getWorkDayDivision(currentDate.day()) == WorkDayDivision.NonWorkingDayInLaw) {
                                result.push({
                                    start: currentDate.format('YYYY-MM-DD'),
                                    textColor: 'red',
                                    backgroundColor: 'white',
                                    listText: [
                                        self.getWorktypeNameByCode(self.patternReflection.statutorySetting.workTypeCode())
                                    ]
                                });
                            }
                            // Ngay nghi ngoai luat
                            else if (self.isNonStatutorySettingChecked() && self.getWorkDayDivision(currentDate.day()) == WorkDayDivision.NonWorkingDayOutrage) {
                                result.push({
                                    start: currentDate.format('YYYY-MM-DD'),
                                    textColor: 'red',
                                    backgroundColor: 'white',
                                    listText: [
                                        self.getWorktypeNameByCode(self.patternReflection.nonStatutorySetting.workTypeCode())
                                    ]
                                });
                            }
                            // Ngay di lam
                            else {
                                // In ra worktype va worktime trong domain neu co data.
                                // Neu khong thi in ra KSM005_43
                                result.push({
                                    start: currentDate.format('YYYY-MM-DD'),
                                    textColor: 'blue',
                                    backgroundColor: 'white',
                                    listText: [
                                        'di lam',
                                        //self.getWorktypeNameByCode(dailyPatternValue.workTypeCode),
                                    ]
                                });
                            }
                        }
                        dayOfPattern++;
                        // Reserve dayOfPattern if FillInTheBlank is checked.
                        if (self.isHolidaySettingChecked() &&
                            isAHoliday &&
                            self.isFillInTheBlankChecked()) {
                            dayOfPattern--;
                        }
                        // Next day on calendar.
                        currentDate = currentDate.add(1, 'days');
                    }
                });
            }
            return result;
        }

        /**
         * Get worktype name by code.
         */
        private getWorktypeNameByCode(code: string): any {
            let self = this;
            let result = _.find(self.listWorkType(), wt => wt.workTypeCode == code);
            if (result) {
                return result.name;
            }
            return '';
        }

        /**
         * Get list date of selected yearmonth.
         */
        private getListDateOfMonth(): Array<string> {
            let self = this;
            let resultList = [];
            let parsedYm = nts.uk.time.formatYearMonth(self.yearMonthPicked());
            let currentDate = moment(parsedYm).startOf('month');
            let endDate = moment(parsedYm).endOf('month');
            while (currentDate.isSameOrBefore(endDate)) {
                resultList.push(currentDate.format('YYYYMMDD'));
                currentDate.add(1, 'days');
            }
            return resultList;
        }

        /**
         * Get work day division.
         */
        private getWorkDayDivision(dayOfWeek: number): WorkDayDivision {
            let self = this;
            switch (dayOfWeek) {
                case 0:
                    return self.weeklyWorkSetting.sunday;
                case 1:
                    return self.weeklyWorkSetting.monday;
                case 2:
                    return self.weeklyWorkSetting.tuesday;
                case 3:
                    return self.weeklyWorkSetting.wednesday;
                case 4:
                    return self.weeklyWorkSetting.thursday;
                case 5:
                    return self.weeklyWorkSetting.friday;
                case 6:
                    return self.weeklyWorkSetting.saturday;
                default:
                    return WorkDayDivision.WorkingDay;
            }
        }

        /**
         * Get isStatutorySetting checkbox value
         */
        private isStatutorySettingChecked(): boolean {
            let self = this;
            return self.patternReflection.statutorySetting.useClassification();
        }

        /**
         * Get isNonStatutorySetting checkbox value
         */
        private isNonStatutorySettingChecked(): boolean {
            let self = this;
            return self.patternReflection.nonStatutorySetting.useClassification();
        }

        /**
         * Get isHolidaySetting checkbox value
         */
        private isHolidaySettingChecked(): boolean {
            let self = this;
            return self.patternReflection.holidaySetting.useClassification();
        }

        /**
         * Check if isFillInTheBlank radio is selected.
         */
        private isFillInTheBlankChecked(): boolean {
            let self = this;
            return ReflectionMethod.FillInTheBlank == self.patternReflection.reflectionMethod;
        }

        /**
         * Backward 1 day.
         */
        private backwardOneDay(): void {
            let self = this;
            let temp = self.weeklyWorkSetting.sunday;
            self.weeklyWorkSetting.sunday = self.weeklyWorkSetting.monday;
            self.weeklyWorkSetting.monday = self.weeklyWorkSetting.tuesday;
            self.weeklyWorkSetting.tuesday = self.weeklyWorkSetting.wednesday;
            self.weeklyWorkSetting.wednesday = self.weeklyWorkSetting.thursday;
            self.weeklyWorkSetting.thursday = self.weeklyWorkSetting.friday;
            self.weeklyWorkSetting.friday = self.weeklyWorkSetting.saturday;
            self.weeklyWorkSetting.saturday = temp;
        }

        /**
         * Forward 1 day.
         */
        private forwardOneDay(): void {
            let self = this;
            let temp = self.weeklyWorkSetting.monday;
            self.weeklyWorkSetting.monday = self.weeklyWorkSetting.sunday;
            self.weeklyWorkSetting.sunday = self.weeklyWorkSetting.saturday;
            self.weeklyWorkSetting.saturday = self.weeklyWorkSetting.friday;
            self.weeklyWorkSetting.friday = self.weeklyWorkSetting.thursday;
            self.weeklyWorkSetting.thursday = self.weeklyWorkSetting.wednesday;
            self.weeklyWorkSetting.wednesday = self.weeklyWorkSetting.tuesday;
            self.weeklyWorkSetting.tuesday = temp;
        }

        /**
         * Check if the day is holiday
         * @param: day
         */
        private isHoliday(day: moment.Moment): boolean {
            let self = this;
            let result = _.find(self.listHoliday, d => d.date == parseInt(day.format('YYYYMMDD')));
            if (result) {
                return true;
            }
            return false;
        }
    }

    class ItemModelCbb1 {
        code: string;
        name: string;

        constructor(codeCbb1: string, nameCbb1: string) {
            this.code = codeCbb1;
            this.name = nameCbb1;
        }
    }
    class PatternReflection {
        employeeId: string;
        reflectionMethod: ReflectionMethod;
        patternClassification: service.model.PatternClassification;
        statutorySetting: DayOffSetting;
        nonStatutorySetting: DayOffSetting;
        holidaySetting: DayOffSetting;

        constructor(data: service.model.PatternReflection) {
            let self = this;
            self.employeeId = data.employeeId;
            self.reflectionMethod = data.reflectionMethod;
            self.patternClassification = data.patternClassification;
            self.statutorySetting = new DayOffSetting(data.statutorySetting);
            self.nonStatutorySetting = new DayOffSetting(data.nonStatutorySetting);
            self.holidaySetting = new DayOffSetting(data.holidaySetting);
        }
    }
    class DayOffSetting {
        useClassification: KnockoutObservable<boolean>;
        workTypeCode: KnockoutObservable<string>;
        constructor(data: service.model.DayOffSetting) {
            this.useClassification = ko.observable(data.useClassification);
            this.workTypeCode = ko.observable(data.workTypeCode);
        }
    }

    interface OptionDate {
        start: string; // YYYY-MM-DD
        textColor: string;
        backgroundColor: string;
        listText: Array<string>;
    }
}