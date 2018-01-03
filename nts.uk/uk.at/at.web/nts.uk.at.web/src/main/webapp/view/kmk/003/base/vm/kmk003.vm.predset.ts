module nts.uk.at.view.kmk003.a {

    import BreakDownTimeDayDto = service.model.predset.BreakDownTimeDayDto;
    import PredetermineTimeDto = service.model.predset.PredetermineTimeDto;
    import TimezoneDto = service.model.predset.TimezoneDto;
    import PrescribedTimezoneSettingDto = service.model.predset.PrescribedTimezoneSettingDto;
    import PredetemineTimeSettingDto = service.model.predset.PredetemineTimeSettingDto;

    export module viewmodel {
        export module predset {

            export class BreakDownTimeDayModel {
                oneDay: KnockoutObservable<number>;
                morning: KnockoutObservable<number>;
                afternoon: KnockoutObservable<number>;

                constructor() {
                    this.oneDay = ko.observable(0);
                    this.morning = ko.observable(0);
                    this.afternoon = ko.observable(0);
                }

                updateData(data: BreakDownTimeDayDto) {
                    this.oneDay(data.oneDay);
                    this.morning(data.morning);
                    this.afternoon(data.afternoon);
                }

                toDto(): BreakDownTimeDayDto {
                    var dataDTO: BreakDownTimeDayDto = {
                        oneDay: this.oneDay(),
                        morning: this.morning(),
                        afternoon: this.afternoon()
                    };
                    return dataDTO;
                }
                
                resetData(){
                    this.oneDay(0);
                    this.morning(0);
                    this.afternoon(0);
                }
            }

            export class PredetermineTimeModel {
                addTime: BreakDownTimeDayModel;
                predTime: BreakDownTimeDayModel;

                constructor() {
                    this.addTime = new BreakDownTimeDayModel();
                    this.predTime = new BreakDownTimeDayModel();
                }

                updateData(data: PredetermineTimeDto) {
                    this.addTime.updateData(data.addTime);
                    this.predTime.updateData(data.predTime);
                }

                toDto(): PredetermineTimeDto {
                    var dataDTO: PredetermineTimeDto = {
                        addTime: this.addTime.toDto(),
                        predTime: this.predTime.toDto()
                    };
                    return dataDTO;
                }
                
                resetData(){
                    this.addTime.resetData();
                    this.predTime.resetData();    
                }
            }

            export class TimezoneModel {
                useAtr: KnockoutObservable<boolean>;
                workNo: KnockoutObservable<number>;
                start: KnockoutObservable<number>;
                end: KnockoutObservable<number>;

                constructor() {
                    this.useAtr = ko.observable(false);
                    this.workNo = ko.observable(0);
                    this.start = ko.observable(0);
                    this.end = ko.observable(0);
                }

                resetData(): void {
                    let self = this;
                    self.start(0);
                    self.end(0);
                }

                updateData(data: TimezoneDto) {
                    this.useAtr(data.useAtr);
                    this.workNo(data.workNo);
                    this.end(data.end);
                    this.start(data.start);
                }

                toDto(): TimezoneDto {
                    var dataDTO: TimezoneDto = {
                        useAtr: this.useAtr(),
                        workNo: this.workNo(),
                        end: this.end(),
                        start: this.start()
                    };
                    return dataDTO;
                }
            }


            export class PrescribedTimezoneSettingModel {
                morningEndTime: KnockoutObservable<number>;
                afternoonStartTime: KnockoutObservable<number>;
                lstTimezone: TimezoneModel[];

                constructor() {
                    this.morningEndTime = ko.observable(0);
                    this.afternoonStartTime = ko.observable(0);
                    this.lstTimezone = [];
                }

                updateData(data: PrescribedTimezoneSettingDto) {
                    this.morningEndTime(data.morningEndTime);
                    this.afternoonStartTime(data.afternoonStartTime);
                    this.updateTimeZone(data.lstTimezone);
                }

                updateTimeZone(data: TimezoneDto[]) {
                    for (var dataDTO of data) {
                        var dataModel: TimezoneModel = this.getTimezoneByWorkNo(dataDTO.workNo);
                        if (!dataModel) {
                            dataModel = new TimezoneModel();
                            dataModel.updateData(dataDTO);
                            this.lstTimezone.push(dataModel);
                        }
                        else {
                            dataModel.updateData(dataDTO);
                        }
                    }
                }
                
                // get time zone one
                getTimezoneOne(): TimezoneModel {
                    var self = this;
                    var data: TimezoneModel = _.find(self.lstTimezone, timezone => timezone.workNo() == 1);
                    if (!data) {
                        data = new TimezoneModel();
                        data.workNo(1);
                        this.lstTimezone.push(data);
                        return data;
                    }
                    return data;
                }
                
               
                getTimezoneByWorkNo(workNo: number): TimezoneModel {
                    var self = this;
                    var data: TimezoneModel = _.find(self.lstTimezone, timezone => timezone.workNo() == workNo);
                    if (!data) {
                        data = new TimezoneModel();
                        data.workNo(workNo);
                        this.lstTimezone.push(data);
                        return data;
                    }
                    return data;
                }

                // get time zone two
                getTimezoneTwo(): TimezoneModel {
                    var self = this;
                    var data: TimezoneModel = _.find(self.lstTimezone, timezone => timezone.workNo() == 2);
                    if (!data) {
                        data = new TimezoneModel();
                        data.workNo(2);
                        this.lstTimezone.push(data);
                        return data;
                    }
                    return data;
                }
                
                toDto(): PrescribedTimezoneSettingDto {
                    var lstTimezone: TimezoneDto[] = [];
                    for (var dataModel of this.lstTimezone) {
                        lstTimezone.push(dataModel.toDto());
                    }
                    var dataDTO: PrescribedTimezoneSettingDto = {
                        morningEndTime: this.morningEndTime(),
                        afternoonStartTime: this.afternoonStartTime(),
                        lstTimezone: lstTimezone
                    };
                    return dataDTO;
                }
                
                resetData() {
                    this.morningEndTime(0);
                    this.afternoonStartTime(0);
                    this.getTimezoneOne().resetData();
                    this.getTimezoneTwo().resetData();
                }
            }


            export class PredetemineTimeSettingModel {
                rangeTimeDay: KnockoutObservable<number>;
                workTimeCode: KnockoutObservable<string>;
                predTime: PredetermineTimeModel;
                nightShift: KnockoutObservable<number>;
                prescribedTimezoneSetting: PrescribedTimezoneSettingModel;
                startDateClock: KnockoutObservable<number>;
                predetermine: KnockoutObservable<boolean>;
                static ONE_DAY = 1440; // initial value of rangeTimeDay = 24h = 1440m

                constructor() {
                    this.rangeTimeDay = ko.observable(PredetemineTimeSettingModel.ONE_DAY); 
                    this.workTimeCode = ko.observable('');
                    this.predTime = new PredetermineTimeModel();
                    this.nightShift = ko.observable(0);
                    this.prescribedTimezoneSetting = new PrescribedTimezoneSettingModel();
                    this.startDateClock = ko.observable(0);
                    this.predetermine = ko.observable(false);
                }

                updateData(data: PredetemineTimeSettingDto) {
                    this.rangeTimeDay(data.rangeTimeDay);
                    this.workTimeCode(data.workTimeCode);
                    this.predTime.updateData(data.predTime);
                    this.nightShift(data.nightShift);
                    this.prescribedTimezoneSetting.updateData(data.prescribedTimezoneSetting);
                    this.startDateClock(data.startDateClock);
                    this.predetermine(data.predetermine);
                }

                toDto(): PredetemineTimeSettingDto {
                    var dataDTO: PredetemineTimeSettingDto = {
                        rangeTimeDay: this.rangeTimeDay(),
                        workTimeCode: this.workTimeCode(),
                        predTime: this.predTime.toDto(),
                        nightShift: this.nightShift(),
                        prescribedTimezoneSetting: this.prescribedTimezoneSetting.toDto(),
                        startDateClock: this.startDateClock(),
                        predetermine: this.predetermine()
                    };
                    return dataDTO;
                }
                
                resetData() {
                    this.rangeTimeDay(PredetemineTimeSettingModel.ONE_DAY);
                    this.workTimeCode('');
                    this.predTime.resetData();
                    this.nightShift(0);  
                    this.prescribedTimezoneSetting.resetData();
                    this.startDateClock(0);
                    this.predetermine(false);
                }
            }
        }
    }
}