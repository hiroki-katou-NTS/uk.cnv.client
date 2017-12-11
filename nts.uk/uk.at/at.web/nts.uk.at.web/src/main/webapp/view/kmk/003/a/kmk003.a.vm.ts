module nts.uk.at.view.kmk003.a {

    import CommonRestSettingDto = service.model.common.CommonRestSettingDto;
    import FlowRestSetDto = service.model.common.FlowRestSetDto;
    import FlowFixedRestSetDto = service.model.common.FlowFixedRestSetDto;
    import FlowWorkRestSettingDetailDto = service.model.common.FlowWorkRestSettingDetailDto;
    import FlowWorkRestSettingDto = service.model.common.FlowWorkRestSettingDto;
    import TimeRoundingSettingDto = service.model.common.TimeRoundingSettingDto;
    import TimeZoneRoundingDto = service.model.common.TimeZoneRoundingDto;
    import HDWorkTimeSheetSettingDto = service.model.common.HDWorkTimeSheetSettingDto;
    import DeductionTimeDto = service.model.common.DeductionTimeDto;
    import TimezoneOfFixedRestTimeSetDto = service.model.common.TimezoneOfFixedRestTimeSetDto;
    import FlowRestSettingDto = service.model.common.FlowRestSettingDto;
    import FlowRestTimezoneDto = service.model.common.FlowRestTimezoneDto;
    import FlowWorkRestTimezoneDto = service.model.common.FlowWorkRestTimezoneDto;
    import IntervalTimeDto = service.model.common.IntervalTimeDto;
    import IntervalTimeSettingDto = service.model.common.IntervalTimeSettingDto;
    import DesignatedTimeDto = service.model.common.DesignatedTimeDto;
    import SubHolTransferSetDto = service.model.common.SubHolTransferSetDto;
    import WorkTimezoneOtherSubHolTimeSetDto = service.model.common.WorkTimezoneOtherSubHolTimeSetDto;
    import WorkTimezoneMedicalSetDto = service.model.common.WorkTimezoneMedicalSetDto;
    import TotalRoundingSetDto = service.model.common.TotalRoundingSetDto;
    import GoOutTimeRoundingSettingDto = service.model.common.GoOutTimeRoundingSettingDto;
    import DeductGoOutRoundingSetDto = service.model.common.DeductGoOutRoundingSetDto;
    import GoOutTypeRoundingSetDto = service.model.common.GoOutTypeRoundingSetDto;
    import GoOutTimezoneRoundingSetDto = service.model.common.GoOutTimezoneRoundingSetDto;
    import WorkTimezoneGoOutSetDto = service.model.common.WorkTimezoneGoOutSetDto;
    import WorkTimezoneCommonSetDto = service.model.common.WorkTimezoneCommonSetDto;
    import InstantRoundingDto = service.model.common.InstantRoundingDto;
    import RoundingSetDto = service.model.common.RoundingSetDto;
    import PrioritySettingDto = service.model.common.PrioritySettingDto;
    import WorkTimezoneStampSetDto = service.model.common.WorkTimezoneStampSetDto;
    import WorkTimezoneLateNightTimeSetDto = service.model.common.WorkTimezoneLateNightTimeSetDto;
    import WorkTimezoneShortTimeWorkSetDto = service.model.common.WorkTimezoneShortTimeWorkSetDto;
    import HolidayFramsetDto = service.model.common.HolidayFramsetDto;
    import ExtraordWorkOTFrameSetDto = service.model.common.ExtraordWorkOTFrameSetDto;
    import FlexWorkSettingSaveCommand = service.model.command.FlexWorkSettingSaveCommand;
    import TimeSheetDto = service.model.flexset.TimeSheetDto;
    import FlexOffdayWorkTimeDto = service.model.flexset.FlexOffdayWorkTimeDto;
    import CoreTimeSettingDto = service.model.flexset.CoreTimeSettingDto;
    import FlexWorkSettingDto = service.model.flexset.FlexWorkSettingDto;
    export module viewmodel {

        export class ScreenModel {

            workFormOptions: KnockoutObservableArray<ItemWorkForm>;
            selectedWorkForm: KnockoutObservable<string>;

            settingMethodOptions: KnockoutObservableArray<ItemSettingMethod>;
            selectedSettingMethod: KnockoutObservable<string>;

            workTimezoneItems: KnockoutObservableArray<WorkTimeItem>;
            columns: KnockoutObservable<any>;
            selectedWorkTimezone: KnockoutObservable<string>;

            //screen mode
            isUpdateMode: KnockoutObservable<boolean>;
            //sift code input
            siftCode: KnockoutObservable<string>;
            siftCodeOption: KnockoutObservable<any>;
            
            

            siftName: KnockoutObservable<string>;
            siftNameOption: KnockoutObservable<any>;

            siftShortName: KnockoutObservable<string>;
            siftShortNameOption: KnockoutObservable<any>;

            siftSymbolName: KnockoutObservable<string>;
            siftSymbolNameOption: KnockoutObservable<any>;

            //color
            pickColor: KnockoutObservable<string>;

            siftRemark: KnockoutObservable<string>;
            siftRemarkOption: KnockoutObservable<any>;

            memo: KnockoutObservable<string>;
            memoOption: KnockoutObservable<any>;

            //tab mode
            tabModeOptions: KnockoutObservableArray<any>;
            tabMode: KnockoutObservable<string>;

            //use half day
            useHalfDayOptions: KnockoutObservableArray<any>;
            useHalfDay: KnockoutObservable<string>;

            //tabs
            tabs: KnockoutObservableArray<any>;
            selectedTab: KnockoutObservable<string>;

            //data
            data: KnockoutObservable<any>;
            isClickSave: KnockoutObservable<boolean>;
            constructor() {
                let self = this;
                self.workFormOptions = ko.observableArray([
                    new ItemWorkForm('1', '通常勤務・変形労働用'),
                    new ItemWorkForm('2', 'フレックス勤務用')
                ]);
                self.selectedWorkForm = ko.observable('1');
                self.settingMethodOptions = ko.observableArray([
                    new ItemSettingMethod('1', "固定勤務"),
                    new ItemSettingMethod('2', "時差勤務"),
                    new ItemSettingMethod('3', "流動勤務")
                ]);
                self.selectedSettingMethod = ko.observable('1');
                
                self.workTimezoneItems = ko.observableArray([]);
                self.columns = ko.observableArray([
                    { headerText: nts.uk.resource.getText("KMK003_10"), prop: 'code', width: 100 },
                    { headerText: nts.uk.resource.getText("KMK003_11"), prop: 'name', width: 130 },
                    { headerText: nts.uk.resource.getText("KMK003_12"), prop: 'description', width: 50 }
                ]);
                self.selectedWorkTimezone = ko.observable('');

                self.isUpdateMode = ko.observable(true);

                self.siftCode = ko.observable('');
                self.siftCodeOption = ko.observable(new nts.uk.ui.option.TextEditorOption({
                    width: "60"
                }));
                self.siftName = ko.observable('');
                self.siftNameOption = ko.observable(new nts.uk.ui.option.TextEditorOption({
                    width: "150"
                }));
                self.siftShortName = ko.observable('');
                self.siftShortNameOption = ko.observable(new nts.uk.ui.option.TextEditorOption({
                    width: "60"
                }));

                self.siftSymbolName = ko.observable('');
                self.siftSymbolNameOption = ko.observable(new nts.uk.ui.option.TextEditorOption({
                    width: "50"
                }));

                //color
                self.pickColor = ko.observable('');

                self.siftRemark = ko.observable('');
                self.siftRemarkOption = ko.observable(new nts.uk.ui.option.TextEditorOption({
                    width: "200"
                }));

                self.memo = ko.observable('');
                self.memoOption = ko.observable(new nts.uk.ui.option.TextEditorOption({
                    width: "200"
                }));

                //tab mode
                self.tabModeOptions = ko.observableArray([
                    { code: "1", name: nts.uk.resource.getText("KMK003_190") },
                    { code: "2", name: nts.uk.resource.getText("KMK003_191") }
                ]);

                self.tabMode = ko.observable("2");

                //use half day

                self.useHalfDayOptions = ko.observableArray([
                    { code: "1", name: nts.uk.resource.getText("KMK003_49") },
                    { code: "2", name: nts.uk.resource.getText("KMK003_50") }
                ]);

                self.useHalfDay = ko.observable("1");

                //
                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: nts.uk.resource.getText("KMK003_17"), content: '.tab-a1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: nts.uk.resource.getText("KMK003_18"), content: '.tab-a2', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-3', title: nts.uk.resource.getText("KMK003_89"), content: '.tab-a3', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-4', title: nts.uk.resource.getText("KMK003_19"), content: '.tab-a4', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-5', title: nts.uk.resource.getText("KMK003_20"), content: '.tab-a5', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-6', title: nts.uk.resource.getText("KMK003_90"), content: '.tab-a6', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-7', title: nts.uk.resource.getText("KMK003_21"), content: '.tab-a7', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-8', title: nts.uk.resource.getText("KMK003_200"), content: '.tab-a8', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-9', title: nts.uk.resource.getText("KMK003_23"), content: '.tab-a9', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-10', title: nts.uk.resource.getText("KMK003_24"), content: '.tab-a10', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-11', title: nts.uk.resource.getText("KMK003_25"), content: '.tab-a11', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-12', title: nts.uk.resource.getText("KMK003_26"), content: '.tab-a12', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-13', title: nts.uk.resource.getText("KMK003_27"), content: '.tab-a13', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-14', title: nts.uk.resource.getText("KMK003_28"), content: '.tab-a14', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-15', title: nts.uk.resource.getText("KMK003_29"), content: '.tab-a15', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-16', title: nts.uk.resource.getText("KMK003_30"), content: '.tab-a16', enable: ko.observable(true), visible: ko.observable(true) },
                ]);
                self.selectedTab = ko.observable('tab-1');

                //data get from service
                self.data = ko.observable();
                self.isClickSave = ko.observable(false);
            }

            /**
             * Start page.
             */
            public startPage(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                service.findAllWorkTimeSet().done(function(data: any) {
                    self.pushDataWorkTime(data);
                    self.data(data);
                });
                service.getPredByWorkTimeCode("AAA").done(function(data: any) {
                    alert();
                });
                // set ntsFixedTable style
                dfd.resolve();
                return dfd.promise();
            }
            
            private pushDataWorkTime(data: any) {
                let self = this;
                let listData:any = [];
                data.forEach(function(item: any, index: any) {
                    listData.push(new WorkTimeItem(item.worktimeCode
                        , item.workTimeName));
                });
                self.workTimezoneItems(listData);
            }
            
            private save() {
                let self = this;
                let data = self.data();
                self.tabMode('2');
                self.isClickSave(true);
                service.savePred(data).done(function() {
                    self.isClickSave(false);
                });
                service.saveFlexWorkSetting(self.collectDataFlex()).done(function() {

                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                });
            }
            
            /**
             * function get flow mode by selection ui
             */
            private getFlowModeBySelected(selectedSettingMethod: string): boolean {
                return (selectedSettingMethod === '3');
            }
            
            /**
             * function collection data flex mode 
             */
            private collectDataFlex(): FlexWorkSettingSaveCommand{
                var command: FlexWorkSettingSaveCommand;
                command = {
                    flexWorkSetting: null,
                    predseting: null,
                    worktimeSetting: null
                };
                return command;     
            }
          
        }
        
        
        export class TimeSheetModel {
            startTime: KnockoutObservable<number>;
            endTime: KnockoutObservable<number>;

            constructor() {
                this.startTime = ko.observable(0);
                this.endTime = ko.observable(0);
            }

            updateData(data: TimeSheetDto) {
                this.startTime(data.startTime);
                this.endTime(data.endTime);
            }
            toDto(): TimeSheetDto {
                var dataDTO: TimeSheetDto = {
                    startTime: this.startTime(),
                    endTime: this.endTime(),
                };
                return dataDTO;
            }
        }

        export class CoreTimeSettingModel {
            coreTimeSheet: TimeSheetModel;
            timesheet: KnockoutObservable<number>;
            minWorkTime: KnockoutObservable<number>;
            
            constructor(){
                this.coreTimeSheet = new TimeSheetModel();
                this.timesheet = ko.observable(0);    
                this.minWorkTime = ko.observable(0);    
            }
            
            updateData(data: CoreTimeSettingDto) {
                this.coreTimeSheet.updateData(data.coreTimeSheet);
                this.timesheet(data.timesheet);
                this.minWorkTime(data.minWorkTime);
            }
            
            toDto(): CoreTimeSettingDto {
                var dataDTO: CoreTimeSettingDto = {
                    coreTimeSheet: this.coreTimeSheet.toDto(),
                    timesheet: this.timesheet(),
                    minWorkTime: this.minWorkTime()
                };
                return dataDTO;
            }
        }
        
        export class CommonRestSettingModel {
            calculateMethod: KnockoutObservable<number>;
            constructor(){
                this.calculateMethod = ko.observable(0);    
            }
            
            updateData(data: CommonRestSettingDto) {
                this.calculateMethod(data.calculateMethod);
            }
            
            toDto(): CommonRestSettingDto {
                var dataDTO: CommonRestSettingDto = {
                    calculateMethod: this.calculateMethod()
                };
                return dataDTO;
            }
        }
        
        export class FlowRestSetModel {
            useStamp: KnockoutObservable<boolean>;
            useStampCalcMethod: KnockoutObservable<number>;
            timeManagerSetAtr: KnockoutObservable<number>;
            calculateMethod: KnockoutObservable<number>;
            
            constructor(){
                this.useStamp = ko.observable(false);
                this.useStampCalcMethod = ko.observable(0);
                this.timeManagerSetAtr = ko.observable(0);
                this.calculateMethod = ko.observable(0);    
            }
            
            updateData(data: FlowRestSetDto) {
                this.useStamp(data.useStamp);
                this.useStampCalcMethod(data.useStampCalcMethod);
                this.timeManagerSetAtr(data.timeManagerSetAtr);
                this.calculateMethod(data.calculateMethod);
            }
            
            toDto(): FlowRestSetDto {
                var dataDTO: FlowRestSetDto = {
                    useStamp: this.useStamp(),
                    useStampCalcMethod: this.useStampCalcMethod(),
                    timeManagerSetAtr: this.timeManagerSetAtr(),
                    calculateMethod: this.calculateMethod()
                };
                return dataDTO;

            }
        }
        
        export class FlowFixedRestSetModel {
            isReferRestTime: KnockoutObservable<boolean>;
            usePrivateGoOutRest: KnockoutObservable<boolean>;
            useAssoGoOutRest: KnockoutObservable<boolean>;
            calculateMethod: KnockoutObservable<number>;

            constructor() {
                this.isReferRestTime = ko.observable(false);
                this.usePrivateGoOutRest = ko.observable(false);
                this.useAssoGoOutRest = ko.observable(false);
                this.calculateMethod = ko.observable(0);
            }

            updatedData(data: FlowFixedRestSetDto) {
                this.isReferRestTime(data.isReferRestTime);
                this.usePrivateGoOutRest(data.usePrivateGoOutRest);
                this.useAssoGoOutRest(data.useAssoGoOutRest);
                this.calculateMethod(data.calculateMethod);
            }
            
            toDto(): FlowFixedRestSetDto{
                var dataDTO: FlowFixedRestSetDto = {
                     isReferRestTime: this.isReferRestTime(),   
                     usePrivateGoOutRest: this.usePrivateGoOutRest(),   
                     useAssoGoOutRest: this.useAssoGoOutRest(),   
                     calculateMethod: this.calculateMethod()   
                };
                return dataDTO;    
            }
        }

        export class FlowWorkRestSettingDetailModel {
            flowRestSetting: FlowRestSetModel;
            flowFixedRestSetting: FlowFixedRestSetModel;
            usePluralWorkRestTime: KnockoutObservable<boolean>;
            
            constructor() {
                this.flowRestSetting = new FlowRestSetModel();
                this.flowFixedRestSetting = new FlowFixedRestSetModel();
                this.usePluralWorkRestTime = ko.observable(false);
            }
            
            updateData(data: FlowWorkRestSettingDetailDto){
                this.flowRestSetting.updateData(data.flowRestSetting);
                this.flowFixedRestSetting.updatedData(data.flowFixedRestSetting);
                this.usePluralWorkRestTime(data.usePluralWorkRestTime);
            }
            
            toDto(): FlowWorkRestSettingDetailDto {
                var dataDTO: FlowWorkRestSettingDetailDto = {
                    flowRestSetting: this.flowRestSetting.toDto(),
                    flowFixedRestSetting: this.flowFixedRestSetting.toDto(),
                    usePluralWorkRestTime: this.usePluralWorkRestTime()
                };
                return dataDTO;
            }
        }
        
        export class FlowWorkRestSettingModel {
            commonRestSetting: CommonRestSettingModel;
            flowRestSetting: FlowWorkRestSettingDetailModel;

            constructor() {
                this.commonRestSetting = new CommonRestSettingModel();
                this.flowRestSetting = new FlowWorkRestSettingDetailModel();
            }
            
            updateData(data: FlowWorkRestSettingDto){
                this.commonRestSetting.updateData(data.commonRestSetting);
                this.flowRestSetting.updateData(data.flowRestSetting);
            }
            
            toDto(): FlowWorkRestSettingDto{
                var dataDTO: FlowWorkRestSettingDto = {
                    commonRestSetting: this.commonRestSetting.toDto(),
                    flowRestSetting: this.flowRestSetting.toDto()
                };
                return dataDTO;    
            }
        }
        
        export class TimeRoundingSettingModel {
            roundingTime: KnockoutObservable<number>;
            rounding: KnockoutObservable<number>;

            constructor() {
                this.roundingTime = ko.observable(0);
                this.rounding = ko.observable(0);
            }

            updateData(data: TimeRoundingSettingDto) {
                this.roundingTime(data.roundingTime);
                this.rounding(data.rounding);
            }

            toDto(): TimeRoundingSettingDto {
                var dataDTO: TimeRoundingSettingDto = {
                    roundingTime: this.roundingTime(),    
                    rounding: this.rounding(),    
                };
                return dataDTO;
            }
        }
        
        export class TimeZoneRoundingModel {
            rounding: TimeRoundingSettingModel;
            start: KnockoutObservable<number>;
            end: KnockoutObservable<number>;

            constructor() {
                this.rounding = new TimeRoundingSettingModel();
                this.start = ko.observable(0);
                this.end = ko.observable(0);
            }
            
            updateData(data: TimeZoneRoundingDto){
                this.rounding.updateData(data.rounding);
                this.start(data.start);
                this.end(data.end);
            }
            
            toDto(): TimeZoneRoundingDto {
                var dataDTO: TimeZoneRoundingDto = {
                    rounding: this.rounding.toDto(),
                    start: this.start(),
                    end: this.end()
                };
                return dataDTO;
            }
        }
        
        
        export class HDWorkTimeSheetSettingModel {
            workTimeNo: KnockoutObservable<number>;
            timezone: TimeZoneRoundingModel;
            isLegalHolidayConstraintTime: KnockoutObservable<boolean>;
            inLegalBreakFrameNo: KnockoutObservable<boolean>;
            isNonStatutoryDayoffConstraintTime: KnockoutObservable<boolean>;
            outLegalBreakFrameNo: KnockoutObservable<number>;
            isNonStatutoryHolidayConstraintTime: KnockoutObservable<boolean>;
            outLegalPubHDFrameNo: KnockoutObservable<number>;
            
            constructor() {
                this.workTimeNo = ko.observable(0);
                this.timezone = new TimeZoneRoundingModel();
                this.isLegalHolidayConstraintTime = ko.observable(false);
                this.inLegalBreakFrameNo = ko.observable(false);
                this.isNonStatutoryDayoffConstraintTime = ko.observable(false);
                this.outLegalBreakFrameNo = ko.observable(0);
                this.isNonStatutoryHolidayConstraintTime = ko.observable(false);
                this.outLegalPubHDFrameNo = ko.observable(0);
            }
            
            updateData(data: HDWorkTimeSheetSettingDto){
                this.workTimeNo(data.workTimeNo);
                this.timezone.updateData(data.timezone);
                this.isLegalHolidayConstraintTime(data.isLegalHolidayConstraintTime);
                this.inLegalBreakFrameNo(data.inLegalBreakFrameNo);
                this.isNonStatutoryDayoffConstraintTime(data.isNonStatutoryDayoffConstraintTime);
                this.outLegalBreakFrameNo(data.outLegalBreakFrameNo);
                this.isNonStatutoryHolidayConstraintTime(data.isNonStatutoryHolidayConstraintTime);
                this.outLegalPubHDFrameNo(data.outLegalPubHDFrameNo);
            }
            
            toDto(): HDWorkTimeSheetSettingDto{
                var dataDTO: HDWorkTimeSheetSettingDto = {
                     workTimeNo: this.workTimeNo(),   
                     timezone: this.timezone.toDto(),   
                     isLegalHolidayConstraintTime: this.isLegalHolidayConstraintTime(),   
                     inLegalBreakFrameNo: this.inLegalBreakFrameNo(),   
                     isNonStatutoryDayoffConstraintTime: this.isNonStatutoryDayoffConstraintTime(),   
                     outLegalBreakFrameNo: this.outLegalBreakFrameNo(),   
                     isNonStatutoryHolidayConstraintTime: this.isNonStatutoryHolidayConstraintTime(),   
                     outLegalPubHDFrameNo: this.outLegalPubHDFrameNo()   
                };
                return dataDTO;    
            }
        }
        
        export class DeductionTimeModel {
            start: KnockoutObservable<number>;
            end: KnockoutObservable<number>;

            constructor() {
                this.start = ko.observable(0);
                this.end = ko.observable(0);
            }

            updateData(data: DeductionTimeDto) {
                this.start(data.start);
                this.end(data.end);
            }

            toDto(): DeductionTimeDto {
                var dataDTO: DeductionTimeDto = {
                    start: this.start(),
                    end: this.end()
                };
                return dataDTO;
            }
        }
        
        export class TimezoneOfFixedRestTimeSetModel {
            timezones: DeductionTimeModel[];
            
            constructor(){
                this.timezones = [];    
            }
            
            updateData(data: TimezoneOfFixedRestTimeSetDto) {
                this.timezones = [];
                for (var dataItem of data.timezones) {
                    var dataModel: DeductionTimeModel = new DeductionTimeModel();
                    dataModel.updateData(dataItem);
                    this.timezones.push(dataModel);
                }
            }
            
            toDto(): TimezoneOfFixedRestTimeSetDto {
                var timezones: DeductionTimeDto[] = [];
                for (var dataModel of this.timezones) {
                    timezones.push(dataModel.toDto());
                }
                var dataDTO: TimezoneOfFixedRestTimeSetDto = {
                    timezones: timezones
                };
                return dataDTO;
            }
        }
        
        export class FlowRestSettingModel {
            flowRestTime: KnockoutObservable<number>;
            flowPassageTime: KnockoutObservable<number>;

            constructor() {
                this.flowRestTime = ko.observable(0);
                this.flowPassageTime = ko.observable(0);
            }

            updateData(data: FlowRestSettingDto) {
                this.flowRestTime(data.flowRestTime);
                this.flowPassageTime(data.flowPassageTime);
            }

            toDto(): FlowRestSettingDto {
                var dataDTO: FlowRestSettingDto = {
                    flowRestTime: this.flowRestTime(),
                    flowPassageTime: this.flowPassageTime()
                };
                return dataDTO;
            }
        }
        
        export class FlowRestTimezoneModel {
            flowRestSets: FlowRestSettingModel[];
            useHereAfterRestSet: KnockoutObservable<boolean>;
            hereAfterRestSet: FlowRestSettingModel;

            constructor() {
                this.flowRestSets = [];
                this.useHereAfterRestSet = ko.observable(false);
                this.hereAfterRestSet = new FlowRestSettingModel();
            }

            updateData(data: FlowRestTimezoneDto) {
                this.flowRestSets = [];
                for (var dataDTO of data.flowRestSets) {
                    var dataModel: FlowRestSettingModel = new FlowRestSettingModel();
                    dataModel.updateData(dataDTO);
                    this.flowRestSets.push(dataModel);
                }
                this.useHereAfterRestSet(data.useHereAfterRestSet);
                this.hereAfterRestSet.updateData(data.hereAfterRestSet);
            }

            toDto(): FlowRestTimezoneDto {
                var flowRestSets: FlowRestSettingDto[] = [];
                for (var dataModel of this.flowRestSets) {
                    flowRestSets.push(dataModel.toDto());
                }
                var dataDTO: FlowRestTimezoneDto = {
                    flowRestSets: flowRestSets,
                    useHereAfterRestSet: this.useHereAfterRestSet(),
                    hereAfterRestSet: this.hereAfterRestSet.toDto()
                };
                return dataDTO;
            }
        }
        
        
        export class FlowWorkRestTimezoneModel {
            fixRestTime: KnockoutObservable<boolean>;
            fixedRestTimezone: TimezoneOfFixedRestTimeSetModel;
            flowRestTimezone: FlowRestTimezoneModel;

            constructor() {
                this.fixRestTime = ko.observable(false);
                this.fixedRestTimezone = new TimezoneOfFixedRestTimeSetModel();
                this.flowRestTimezone = new FlowRestTimezoneModel();
            }
            
            updateData(data: FlowWorkRestTimezoneDto){
                this.fixRestTime(data.fixRestTime);
                this.fixedRestTimezone.updateData(data.fixedRestTimezone);
                this.flowRestTimezone.updateData(data.flowRestTimezone);
            }
            
            toDto(): FlowWorkRestTimezoneDto {
                var dataDTO: FlowWorkRestTimezoneDto = {
                    fixRestTime: this.fixRestTime(),
                    fixedRestTimezone: this.fixedRestTimezone.toDto(),
                    flowRestTimezone: this.flowRestTimezone.toDto()
                };
                return dataDTO;
            }
        }
        
        
        export class FlexOffdayWorkTimeModel {
            lstWorkTimezone: HDWorkTimeSheetSettingModel[];
            restTimezone: FlowWorkRestTimezoneModel;

            constructor() {
                this.lstWorkTimezone = [];
                this.restTimezone = new FlowWorkRestTimezoneModel();
            }
            
            updateData(data: FlexOffdayWorkTimeDto){
                this.lstWorkTimezone = [];
                for(var dataDTO of data.lstWorkTimezone){
                    var dataModel: HDWorkTimeSheetSettingModel = new HDWorkTimeSheetSettingModel();
                    dataModel.updateData(dataDTO);
                    this.lstWorkTimezone.push(dataModel);
                }
                this.restTimezone.updateData(data.restTimezone);
            }
            
            toDto(): FlexOffdayWorkTimeDto {
                var lstWorkTimezone: HDWorkTimeSheetSettingDto[] = [];
                for (var dataModel of this.lstWorkTimezone) {
                    lstWorkTimezone.push(dataModel.toDto());
                }
                var dataDTO: FlexOffdayWorkTimeDto = {
                    lstWorkTimezone: lstWorkTimezone,
                    restTimezone: this.restTimezone.toDto()
                };
                return dataDTO;
            }
        }

        export class IntervalTimeModel {
            intervalTime: KnockoutObservable<number>;
            rounding: TimeRoundingSettingModel;

            constructor() {
                this.intervalTime = ko.observable(0);
                this.rounding = new TimeRoundingSettingModel();
            }

            updateData(data: IntervalTimeDto) {
                this.intervalTime(data.intervalTime);
                this.rounding.updateData(data.rounding);
            }

            toDto(): IntervalTimeDto {
                var dataDTO: IntervalTimeDto = {
                    intervalTime: this.intervalTime(),
                    rounding: this.rounding.toDto()
                };
                return dataDTO;
            }
        }

        export class IntervalTimeSettingModel {
            useIntervalExemptionTime: KnockoutObservable<boolean>;
            intervalExemptionTimeRound: TimeRoundingSettingModel;
            intervalTime: IntervalTimeModel;
            useIntervalTime: KnockoutObservable<boolean>;

            constructor() {
                this.useIntervalExemptionTime = ko.observable(false);
                this.intervalExemptionTimeRound = new TimeRoundingSettingModel();
                this.intervalTime = new IntervalTimeModel();
                this.useIntervalTime = ko.observable(false);
            }

            updateData(data: IntervalTimeSettingDto) {
                this.useIntervalExemptionTime(data.useIntervalExemptionTime);
                this.intervalExemptionTimeRound.updateData(data.intervalExemptionTimeRound);
                this.intervalTime.updateData(data.intervalTime);
                this.useIntervalTime(data.useIntervalTime);
            }

            toDto(): IntervalTimeSettingDto {
                var dataDTO: IntervalTimeSettingDto = {
                    useIntervalExemptionTime: this.useIntervalExemptionTime(),
                    intervalExemptionTimeRound: this.intervalExemptionTimeRound.toDto(),
                    intervalTime: this.intervalTime.toDto(),
                    useIntervalTime: this.useIntervalTime()
                };
                return dataDTO;
            }
        }
        
        export class DesignatedTimeModel {
            oneDayTime: KnockoutObservable<number>;
            halfDayTime: KnockoutObservable<number>;

            constructor() {
                this.oneDayTime = ko.observable(0);
                this.halfDayTime = ko.observable(0);
            }

            updataData(data: DesignatedTimeDto) {
                this.oneDayTime(data.oneDayTime);
                this.halfDayTime(data.halfDayTime);
            }

            toDto(): DesignatedTimeDto {
                var dataDTO: DesignatedTimeDto = {
                    oneDayTime: this.oneDayTime(),
                    halfDayTime: this.halfDayTime()
                };
                return dataDTO;
            }
        }

        export class SubHolTransferSetModel {
            certainTime: KnockoutObservable<number>;
            useDivision: KnockoutObservable<boolean>;
            designatedTime: DesignatedTimeModel;
            subHolTransferSetAtr: KnockoutObservable<number>;

            constructor() {
                this.certainTime = ko.observable(0);
                this.useDivision = ko.observable(false);
                this.designatedTime = new DesignatedTimeModel();
                this.subHolTransferSetAtr = ko.observable(0);
            }

            updateData(data: SubHolTransferSetDto) {
                this.certainTime(data.certainTime);
                this.useDivision(data.useDivision);
                this.designatedTime.updataData(data.designatedTime);
                this.subHolTransferSetAtr(data.subHolTransferSetAtr);
            }

            toDto(): SubHolTransferSetDto {
                var dataDTO: SubHolTransferSetDto = {
                      certainTime: this.certainTime(),  
                      useDivision: this.useDivision(),  
                      designatedTime: this.designatedTime.toDto(),  
                      subHolTransferSetAtr: this.subHolTransferSetAtr(),  
                };
                return dataDTO;
            }
        }
        
        export class WorkTimezoneOtherSubHolTimeSetModel {
            subHolTimeSet: SubHolTransferSetModel;
            workTimeCode: KnockoutObservable<string>;
            originAtr: KnockoutObservable<number>;

            constructor() {
                this.subHolTimeSet = new SubHolTransferSetModel();
                this.workTimeCode = ko.observable('');
                this.originAtr = ko.observable(0);
            }

            updateData(data: WorkTimezoneOtherSubHolTimeSetDto) {
                this.subHolTimeSet.updateData(data.subHolTimeSet);
                this.workTimeCode(data.workTimeCode);
                this.originAtr(data.originAtr);
            }

            toDto(): WorkTimezoneOtherSubHolTimeSetDto {
                var dataDTO: WorkTimezoneOtherSubHolTimeSetDto = {
                    subHolTimeSet: this.subHolTimeSet.toDto(),
                    workTimeCode: this.workTimeCode(),
                    originAtr: this.originAtr()
                };
                return dataDTO;
            }
        }

        
        export class WorkTimezoneMedicalSetModel {
            roundingSet: TimeRoundingSettingModel;
            workSystemAtr: KnockoutObservable<number>;
            applicationTime: KnockoutObservable<number>;

            constructor() {
                this.roundingSet = new TimeRoundingSettingModel();
                this.workSystemAtr = ko.observable(0);
                this.applicationTime = ko.observable(0);
            }

            updateData(data: WorkTimezoneMedicalSetDto) {
                this.roundingSet.updateData(data.roundingSet);
                this.workSystemAtr(data.workSystemAtr);
                this.applicationTime(data.applicationTime);
            }

            toDto(): WorkTimezoneMedicalSetDto {
                var dataDTO: WorkTimezoneMedicalSetDto = {
                    roundingSet: this.roundingSet.toDto(),
                    workSystemAtr: this.workSystemAtr(),
                    applicationTime: this.applicationTime()
                };
                return dataDTO;
            }
        }
        
        
        export class TotalRoundingSetModel {
            setSameFrameRounding: KnockoutObservable<number>;
            frameStraddRoundingSet: KnockoutObservable<number>;

            constructor() {
                this.setSameFrameRounding = ko.observable(0);
                this.frameStraddRoundingSet = ko.observable(0);
            }

            updateData(data: TotalRoundingSetDto) {
                this.setSameFrameRounding(data.setSameFrameRounding);
                this.frameStraddRoundingSet(data.frameStraddRoundingSet);
            }

            toDto(): TotalRoundingSetDto {
                var dataDTO: TotalRoundingSetDto = {
                    setSameFrameRounding: this.setSameFrameRounding(),
                    frameStraddRoundingSet: this.frameStraddRoundingSet()
                };
                return dataDTO;
            }
        }
        
        export class GoOutTimeRoundingSettingModel {
            roundingMethod: KnockoutObservable<number>;
            roundingSetting: TimeRoundingSettingModel;

            constructor() {
                this.roundingMethod = ko.observable(0);
                this.roundingSetting = new TimeRoundingSettingModel();
            }

            updataData(data: GoOutTimeRoundingSettingDto) {
                this.roundingMethod(data.roundingMethod);
                this.roundingSetting.updateData(data.roundingSetting);
            }

            toDto(): GoOutTimeRoundingSettingDto {
                var dataDTO: GoOutTimeRoundingSettingDto = {
                    roundingMethod: this.roundingMethod(),
                    roundingSetting: this.roundingSetting.toDto()
                };
                return dataDTO;
            }
        }
        
        export class DeductGoOutRoundingSetModel {
            deductTimeRoundingSetting: GoOutTimeRoundingSettingModel;
            approTimeRoundingSetting: GoOutTimeRoundingSettingModel;

            constructor() {
                this.deductTimeRoundingSetting = new GoOutTimeRoundingSettingModel();
                this.approTimeRoundingSetting = new GoOutTimeRoundingSettingModel();
            }

            updateData(data: DeductGoOutRoundingSetDto) {
                this.deductTimeRoundingSetting.updataData(data.deductTimeRoundingSetting);
                this.approTimeRoundingSetting.updataData(data.approTimeRoundingSetting);
            }

            toDto(): DeductGoOutRoundingSetDto {
                var dataDTO: DeductGoOutRoundingSetDto = {
                    deductTimeRoundingSetting: this.deductTimeRoundingSetting.toDto(),
                    approTimeRoundingSetting: this.approTimeRoundingSetting.toDto()
                };
                return dataDTO;
            }
        }
        
        
        export class GoOutTypeRoundingSetModel {
            officalUseCompenGoOut: DeductGoOutRoundingSetModel;
            privateUnionGoOut: DeductGoOutRoundingSetModel;

            constructor() {
                this.officalUseCompenGoOut = new DeductGoOutRoundingSetModel();
                this.privateUnionGoOut = new DeductGoOutRoundingSetModel();
            }

            updateData(data: GoOutTypeRoundingSetDto) {
                this.officalUseCompenGoOut.updateData(data.officalUseCompenGoOut);
                this.privateUnionGoOut.updateData(data.privateUnionGoOut);
            }

            toDto(): GoOutTypeRoundingSetDto {
                var dataDTO: GoOutTypeRoundingSetDto = {
                    officalUseCompenGoOut: this.officalUseCompenGoOut.toDto(),
                    privateUnionGoOut: this.privateUnionGoOut.toDto()
                };
                return dataDTO;
            }
        }
        
        export class GoOutTimezoneRoundingSetModel {
            pubHolWorkTimezone: GoOutTypeRoundingSetModel;
            workTimezone: GoOutTypeRoundingSetModel;
            oTTimezone: GoOutTypeRoundingSetModel;

            constructor() {
                this.pubHolWorkTimezone = new GoOutTypeRoundingSetModel();
                this.workTimezone = new GoOutTypeRoundingSetModel();
                this.oTTimezone = new GoOutTypeRoundingSetModel();
            }

            updateData(data: GoOutTimezoneRoundingSetDto) {
                this.pubHolWorkTimezone.updateData(data.pubHolWorkTimezone);
                this.workTimezone.updateData(data.workTimezone);
                this.oTTimezone.updateData(data.oTTimezone);
            }

            toDto(): GoOutTimezoneRoundingSetDto {
                var dataDTO: GoOutTimezoneRoundingSetDto = {
                    pubHolWorkTimezone: this.pubHolWorkTimezone.toDto(),
                    workTimezone: this.workTimezone.toDto(),
                    oTTimezone: this.oTTimezone.toDto()
                };
                return dataDTO;
            }
        }
        
        
        export class WorkTimezoneGoOutSetModel {
            totalRoundingSet: TotalRoundingSetModel;
            diffTimezoneSetting: GoOutTimezoneRoundingSetModel;

            constructor() {
                this.totalRoundingSet = new TotalRoundingSetModel();
                this.diffTimezoneSetting = new GoOutTimezoneRoundingSetModel();
            }

            updateData(data: WorkTimezoneGoOutSetDto) {
                this.totalRoundingSet.updateData(data.totalRoundingSet);
                this.diffTimezoneSetting.updateData(data.diffTimezoneSetting);
            }

            toDto(): WorkTimezoneGoOutSetDto {
                var dataDTO: WorkTimezoneGoOutSetDto = {
                    totalRoundingSet: this.totalRoundingSet.toDto(),
                    diffTimezoneSetting: this.diffTimezoneSetting.toDto()
                };
                return dataDTO;
            }
        }
        
        export class InstantRoundingModel {
            fontRearSection: KnockoutObservable<number>;
            roundingTimeUnit: KnockoutObservable<number>;

            constructor() {
                this.fontRearSection = ko.observable(0);
                this.roundingTimeUnit = ko.observable(0);
            }

            updateData(data: InstantRoundingDto) {
                this.fontRearSection(data.fontRearSection);
                this.roundingTimeUnit(data.roundingTimeUnit);
            }

            toDto(): InstantRoundingDto {
                var dataDTO: InstantRoundingDto = {
                    fontRearSection: this.fontRearSection(),
                    roundingTimeUnit: this.roundingTimeUnit()
                };
                return dataDTO;
            }
        }
        
        export class RoundingSetModel {
            roundingSet: InstantRoundingModel;
            section: KnockoutObservable<number>;

            constructor() {
                this.roundingSet = new InstantRoundingModel();
                this.section = ko.observable(0);
            }

            updateData(data: RoundingSetDto) {
                this.roundingSet.updateData(data.roundingSet);
                this.section(data.section);
            }

            toDto(): RoundingSetDto {
                var dataDTO: RoundingSetDto = {
                    roundingSet: this.roundingSet.toDto(),
                    section: this.section()
                };
                return dataDTO;
            }
        }
        
        export class PrioritySettingModel {
            priorityAtr: KnockoutObservable<number>;
            stampAtr: KnockoutObservable<number>;

            constructor() {
                this.priorityAtr = ko.observable(0);
                this.stampAtr = ko.observable(0);
            }

            updateData(data: PrioritySettingDto) {
                this.priorityAtr(data.priorityAtr);
                this.stampAtr(data.stampAtr);
            }

            toDto(): PrioritySettingDto {
                var dataDTO: PrioritySettingDto = {
                    priorityAtr: this.priorityAtr(),
                    stampAtr: this.stampAtr()
                };
                return dataDTO;
            }
        }
        
        export class WorkTimezoneStampSetModel {
            roundingSets: RoundingSetModel[];
            prioritySets: PrioritySettingModel[];
            
            constructor(){
                this.roundingSets = [];
                this.prioritySets = [];    
            }
            
            updateData(data: WorkTimezoneStampSetDto) {
                this.roundingSets = [];
                for(var dataRoundingDTO of data.roundingSets){
                    var dataRoundingModel: RoundingSetModel = new RoundingSetModel();
                    dataRoundingModel.updateData(dataRoundingDTO);
                    this.roundingSets.push(dataRoundingModel);   
                }
                
                this.prioritySets = [];
                for(var dataPriorityDTO of data.prioritySets){
                    var dataPriorityModel: PrioritySettingModel = new PrioritySettingModel();
                    dataPriorityModel.updateData(dataPriorityDTO);
                    this.prioritySets.push(dataPriorityModel);   
                }
            }
        }
        
        export class WorkTimezoneLateNightTimeSetModel {
            roundingSetting: TimeRoundingSettingModel;

            constructor() {
                this.roundingSetting = new TimeRoundingSettingModel();
            }

            updateData(data: WorkTimezoneLateNightTimeSetDto) {
                this.roundingSetting.updateData(data.roundingSetting);
            }

            toDto(): WorkTimezoneLateNightTimeSetDto {
                var dataDTO: WorkTimezoneLateNightTimeSetDto = {
                    roundingSetting: this.roundingSetting.toDto()
                };
                return dataDTO;
            }
        }

        
        export class WorkTimezoneShortTimeWorkSetModel {
            nursTimezoneWorkUse: KnockoutObservable<boolean>;
            employmentTimeDeduct: KnockoutObservable<boolean>;
            childCareWorkUse: KnockoutObservable<boolean>;

            constructor() {
                this.nursTimezoneWorkUse = ko.observable(false);
                this.employmentTimeDeduct = ko.observable(false);
                this.childCareWorkUse = ko.observable(false);
            }

            updateData(data: WorkTimezoneShortTimeWorkSetDto) {
                this.nursTimezoneWorkUse(data.nursTimezoneWorkUse);
                this.employmentTimeDeduct(data.employmentTimeDeduct);
                this.childCareWorkUse(data.childCareWorkUse);
            }

            toDto(): WorkTimezoneShortTimeWorkSetDto {
                var dataDTO: WorkTimezoneShortTimeWorkSetDto = {
                    nursTimezoneWorkUse: this.nursTimezoneWorkUse(),
                    employmentTimeDeduct: this.employmentTimeDeduct(),
                    childCareWorkUse: this.childCareWorkUse()
                };
                return dataDTO;
            }
        }
        

        export class HolidayFramsetModel {
            inLegalBreakoutFrameNo: KnockoutObservable<number>;
            outLegalBreakoutFrameNo: KnockoutObservable<number>;
            outLegalPubHolFrameNo: KnockoutObservable<number>;

            constructor() {
                this.inLegalBreakoutFrameNo = ko.observable(0);
                this.outLegalBreakoutFrameNo = ko.observable(0);
                this.outLegalPubHolFrameNo = ko.observable(0);
            }

            updataData(data: HolidayFramsetDto) {
                this.inLegalBreakoutFrameNo(data.inLegalBreakoutFrameNo);
                this.outLegalBreakoutFrameNo(data.outLegalBreakoutFrameNo);
                this.outLegalPubHolFrameNo(data.outLegalPubHolFrameNo);
            }

            toDto(): HolidayFramsetDto {
                var dataDTO: HolidayFramsetDto = {
                    inLegalBreakoutFrameNo: this.inLegalBreakoutFrameNo(),
                    outLegalBreakoutFrameNo: this.outLegalBreakoutFrameNo(),
                    outLegalPubHolFrameNo: this.outLegalPubHolFrameNo()
                };
                return dataDTO;
            }
        }

        export class ExtraordWorkOTFrameSetModel {
            oTFrameNo: KnockoutObservable<number>;
            inLegalWorkFrameNo: KnockoutObservable<number>;
            settlementOrder: KnockoutObservable<number>;

            constructor() {
                this.oTFrameNo = ko.observable(0);
                this.inLegalWorkFrameNo = ko.observable(0);
                this.settlementOrder = ko.observable(0);
            }

            updateData(data: ExtraordWorkOTFrameSetDto) {
                this.oTFrameNo(data.oTFrameNo);
                this.inLegalWorkFrameNo(data.inLegalWorkFrameNo);
                this.settlementOrder(data.settlementOrder);
            }

            toDto(): ExtraordWorkOTFrameSetDto {
                var dataDTO: ExtraordWorkOTFrameSetDto = {
                    oTFrameNo: this.oTFrameNo(),
                    inLegalWorkFrameNo: this.inLegalWorkFrameNo(),
                    settlementOrder: this.settlementOrder()
                };
                return dataDTO;
            }

        }
        export class WorkTimezoneExtraordTimeSetModel {
            holidayFrameSet: HolidayFramsetModel;
            timeRoundingSet: TimeRoundingSettingModel;
            oTFrameSet: ExtraordWorkOTFrameSetModel;
            calculateMethod: KnockoutObservable<number>;
            
            constructor(){
                this.holidayFrameSet = new HolidayFramsetModel();
                this.timeRoundingSet = new TimeRoundingSettingModel();
                this.oTFrameSet = new ExtraordWorkOTFrameSetModel();
                this.calculateMethod = ko.observable(0);
            }
        }
        
        
        export class WorkTimezoneCommonSetModel {
            zeroHStraddCalculateSet: KnockoutObservable<boolean>;
            intervalSet: IntervalTimeSettingModel;
            subHolTimeSet: WorkTimezoneOtherSubHolTimeSetModel;
            raisingSalarySet: KnockoutObservable<string>;
            medicalSet: WorkTimezoneMedicalSetModel[];
            goOutSet: WorkTimezoneGoOutSetModel;
            stampSet: WorkTimezoneStampSetModel;
            lateNightTimeSet: WorkTimezoneLateNightTimeSetModel;
            shortTimeWorkSet: WorkTimezoneShortTimeWorkSetModel;
            /*
            extraordTimeSet: WorkTimezoneExtraordTimeSetDto;
            lateEarlySet: WorkTimezoneLateEarlySetDto;
            */

            constructor() {
                this.zeroHStraddCalculateSet = ko.observable(false);
                this.intervalSet = new IntervalTimeSettingModel();
                this.subHolTimeSet = new WorkTimezoneOtherSubHolTimeSetModel();
                this.raisingSalarySet = ko.observable('');
                this.medicalSet = [];
                this.goOutSet = new WorkTimezoneGoOutSetModel();
                this.stampSet = new WorkTimezoneStampSetModel();
                this.lateNightTimeSet = new WorkTimezoneLateNightTimeSetModel();
                this.shortTimeWorkSet = new WorkTimezoneShortTimeWorkSetModel();
            }

            updateData(data: WorkTimezoneCommonSetDto) {
                this.zeroHStraddCalculateSet(data.zeroHStraddCalculateSet);
                this.intervalSet.updateData(data.intervalSet);
                this.subHolTimeSet.updateData(data.subHolTimeSet);
                this.raisingSalarySet(data.raisingSalarySet);
                this.goOutSet.updateData(data.goOutSet);
                this.stampSet.updateData(data.stampSet);
                this.lateNightTimeSet.updateData(data.lateNightTimeSet);
                this.shortTimeWorkSet.updateData(data.shortTimeWorkSet);
            }
            /*
           toDto(): WorkTimezoneCommonSetDto{
                var dataDTO: WorkTimezoneCommonSetDto = {
                     zeroHStraddCalculateSet: this.zeroHStraddCalculateSet(),   
                     intervalSet: this.intervalSet.toDto(),   
                     subHolTimeSet: this.subHolTimeSet.toDto(),   
                     raisingSalarySet: this.raisingSalarySet.toDto(),   
                     goOutSet: this.goOutSet.toDto(),   
                     stampSet: this.stampSet.toDto(),   
                     lateNightTimeSet: this.lateNightTimeSet.toDto(),   
                     shortTimeWorkSet: this.shortTimeWorkSet.toDto(),   
                };
                return dataDTO;    
            }
            */

        }
        
        export class FlexWorkSettingModel {
            workTimeCode: KnockoutObservable<string>;
            useHalfDayShift: KnockoutObservable<boolean>;
            coreTimeSetting: CoreTimeSettingModel;
            restSetting: FlowWorkRestSettingModel;
            offdayWorkTime: FlexOffdayWorkTimeModel;
            commonSetting: WorkTimezoneCommonSetModel;
            /*
            lstHalfDayWorkTimezone: FlexHalfDayWorkTimeDto[];
            lstStampReflectTimezone: common.StampReflectTimezoneDto[];
            calculateSetting: FlexCalcSettingDto;
            */    
            constructor(){
                var self = this;
                self.workTimeCode = ko.observable('');
                self.useHalfDayShift = ko.observable(false);   
                self.coreTimeSetting = new CoreTimeSettingModel();
                self.restSetting = new FlowWorkRestSettingModel();
                self.commonSetting = new WorkTimezoneCommonSetModel();
            }
            
            updateData(data: FlexWorkSettingDto){
                this.workTimeCode(data.workTimeCode);
                this.useHalfDayShift(data.useHalfDayShift);
                this.coreTimeSetting.updateData(data.coreTimeSetting);
                this.restSetting.updateData(data.restSetting);
                this.commonSetting.updateData(data.commonSetting);
            }
        }
        
         export class WorkTimeItem {
            code: string;
            name: string;

            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
        
        export class ItemWorkForm {
            code: string;
            name: string;

            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }

        export class ItemSettingMethod {
            code: string;
            name: string;

            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
    }
}