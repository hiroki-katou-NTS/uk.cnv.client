module nts.uk.at.view.kal004.share.model {

    export interface EnumConstantDto {
        value: number;
        fieldName: string;
        localizedName: string;
    }

    export interface AlarmPatternSettingDto {
        alarmPatternCD: string;
        alarmPatternName: string;
        alarmPerSet: AlarmPermissionSettingDto;
        checkConList: Array<CheckConditionDto>;
    }
    export interface AlarmPermissionSettingDto {
        authSetting: boolean;
        roleIds: Array<string>;
    }

    export interface CheckConditionDto {
        alarmCategory: number;
        checkConditionCodes: Array<string>;
        extractionDaily?: ExtractionDailyDto;
        extractionUnit?: PeriodUnitDto;
        listExtractionMonthly ?: Array<ExtractionPeriodMonthlyDto>;
        extractionYear ?: ExtractionRangeYearDto;
        extractionMutilMonth ?: ExtractionAverMonthDto;
    }



    export interface AlarmCheckConditonCodeDto {
        category: EnumConstantDto;
        checkConditonCode: string;
        checkConditionName: string;
    }


    export class ModelCheckConditonCode {
        GUID: string;
        category: number;
        categoryName: string;
        checkConditonCode: string;
        checkConditionName: string;
        cssClass: string;
        constructor(dto: AlarmCheckConditonCodeDto) {
            this.category = dto.category.value;
            this.categoryName = dto.category.localizedName;
            this.checkConditonCode = dto.checkConditonCode;
            this.checkConditionName = dto.checkConditionName;
            this.GUID = dto.category.value + dto.checkConditonCode;
            this.cssClass = "";
        }

        public static createNotFoundCheckConditonCode(category: EnumConstantDto, checkConditonCode: string): ModelCheckConditonCode {
            var result = new ModelCheckConditonCode({
                category: category,
                checkConditonCode: checkConditonCode,
                checkConditionName: nts.uk.resource.getText('KAL004_117'),
            });
            result.cssClass = "red-color";
            return result;
        }
    }

    export interface ExtractionDailyDto {
        extractionId: string;
        extractionRange: number;
        strSpecify: number;
        strPreviousDay?: number;
        strMakeToDay?: number;
        strDay?: number;
        strPreviousMonth?: number;
        strCurrentMonth?: number;
        strMonth?: number;
        endSpecify: number;
        endPreviousDay?: number;
        endMakeToDay?: number;
        endDay?: number;
        endPreviousMonth?: number;
        endCurrentMonth?: number;
        endMonth?: number;
    }


    export class ExtractionDaily {
        extractionId: KnockoutObservable<string>;
        extractionRange: KnockoutObservable<number>;
        strSpecify: KnockoutObservable<number>;
        strPreviousDay: KnockoutObservable<number>;
        strMakeToDay: KnockoutObservable<number>;
        strDay: KnockoutObservable<number>;
        strPreviousMonth: KnockoutObservable<number>;
        strCurrentMonth: KnockoutObservable<number>;
        strMonth: KnockoutObservable<number>;
        endSpecify: KnockoutObservable<number>;
        endPreviousDay: KnockoutObservable<number>;
        endMakeToDay: KnockoutObservable<number>;
        endDay: KnockoutObservable<number>;
        endPreviousMonth: KnockoutObservable<number>;
        endCurrentMonth: KnockoutObservable<number>;
        endMonth: KnockoutObservable<number>;

        constructor(dto: ExtractionDailyDto) {
            var self = this;
            self.extractionId = ko.observable(dto.extractionId);
            self.extractionRange = ko.observable(dto.extractionRange);
            self.strSpecify = ko.observable(dto.strSpecify);
            self.strPreviousDay = ko.observable(dto.strPreviousDay);
            self.strMakeToDay = ko.observable(dto.strMakeToDay);
            self.strDay = ko.observable(dto.strDay);
            self.strPreviousMonth = ko.observable(dto.strPreviousMonth);
            self.strCurrentMonth = ko.observable(dto.strCurrentMonth);
            self.strMonth = ko.observable(dto.strMonth);
            self.endSpecify = ko.observable(dto.endSpecify);
            self.endPreviousDay = ko.observable(dto.endPreviousDay);
            self.endMakeToDay = ko.observable(dto.endMakeToDay);
            self.endDay = ko.observable(dto.endDay);
            self.endPreviousMonth = ko.observable(dto.endPreviousMonth);
            self.endCurrentMonth = ko.observable(dto.endCurrentMonth);
            self.endMonth = ko.observable(dto.endMonth);
        }
    }

    export interface PeriodUnitDto {
        extractionId: string;
        extractionRange: number;
        segmentationOfCycle: number;
    }

    export interface ExtractionPeriodMonthlyDto{
         extractionId: string;
         extractionRange: number;
         unit: number;        
         strSpecify : number;        
         yearType: number;
         specifyMonth: number;
         strMonth: number;
         strCurrentMonth: number;
         strPreviousAtr: number;
         endSpecify: number;
         extractPeriod: number;
         endMonth: number;
         endCurrentMonth: number;
         endPreviousAtr: number;                    
    }
    
    export interface ExtractionRangeYearDto{
         extractionId: string;
         extractionRange: number;
         year : number;
         thisYear : number;           
    }
    
    export interface ExtractionAverMonthDto{
         extractionId: string;
         extractionRange: number;
         strMonth: number;
        
    }
    
    export interface ExtractionEDto {
        extractionId: string;
        extractionRange: number;
        strSpecify: number;
        strSpecifyMonth?: number;
        strPreviousMonth?: number;
        strCurrentMonth?: number;
        strMonth?: number;
        strYearSpecifiedType?: number;
        endSpecify: number;
        endPreviousMonth?: number;
        endCurrentMonth?: number;
        endMonth?: number;
        endFromStrMonth?: number;
        processingYm?: string;
    }

    //Command
    export class AddAlarmPatternSettingCommand {
        alarmPatternCD: string;
        alarmPatternName: string;
        alarmPerSet: AlarmPermissionSettingCommand;
        checkConditionList: Array<CheckConditionCommand>;

        constructor(alarmPatternCD: string, alarmPatternName: string, alarmPerSet: AlarmPermissionSettingCommand, checkConditionList: Array<CheckConditionCommand>) {
            this.alarmPatternCD = alarmPatternCD;
            this.alarmPatternName = alarmPatternName;
            this.alarmPerSet = alarmPerSet;
            this.checkConditionList = checkConditionList;
        }
    }

    export class AlarmPermissionSettingCommand {
        authSetting: boolean;
        roleIds: Array<string>;
        constructor(authSetting: boolean, roleIds: Array<string>) {
            this.authSetting = authSetting;
            this.roleIds = roleIds;
        }

    }
    export class CheckConditionCommand {
        alarmCategory: number;
        extractionPeriodDaily: ExtractionPeriodDailyCommand;
        checkConditionCodes: Array<string>;
        extractionPeriodUnit: PeriodUnitCommand;
        listExtractionMonthly : Array<ExtractionPeriodMonthlyCommand>=[];
        extractionYear: ExtractionRangeYearCommand;
        extractionAverMonth: ExtractionAverageMonthCommand;
        extractionScheYear: ExtractionPeriodECommand;
        
        constructor(alarmCategory: number, checkConditionCodes: Array<string>, extractionPeriodDaily: ExtractionPeriodDailyCommand,
            extractionPeriodUnit: PeriodUnitCommand, listExtractionMonthly: Array<ExtractionPeriodMonthlyCommand>, 
            extractionYear:  ExtractionRangeYearCommand, extractionAverMonth: ExtractionAverageMonthCommand, extractionScheYear: ExtractionPeriodECommand) {
            this.alarmCategory = alarmCategory;
            this.checkConditionCodes = checkConditionCodes;
            
            
            if (alarmCategory ==5  || alarmCategory == 13 || alarmCategory == 8 || alarmCategory == 0 || alarmCategory == 6) {
                if(nts.uk.util.isNullOrUndefined(extractionPeriodDaily)){
                    this.setDefaultDaily();
                } else {
                    this.extractionPeriodDaily = extractionPeriodDaily;
                }
            }
            
            
            
            if(alarmCategory == 2){
                if(nts.uk.util.isNullOrUndefined(extractionPeriodUnit)){
                    this.setDefaulttUnit();
                }else{
                    this.extractionPeriodUnit =  extractionPeriodUnit;
                }
            }
            
            
            if(alarmCategory == 7  || alarmCategory == 9 || alarmCategory == 3){
                if(listExtractionMonthly.length==0){
                    this.setDefaultMonthly(3);
                }else{
                    this.listExtractionMonthly =  listExtractionMonthly;
                }
            }
            
            if(alarmCategory ==12){
                if(listExtractionMonthly.length ==0){                                        
                    this.setDefaultMonthly(0);
                    this.setDefaultMonthly(1);
                    this.setDefaultMonthly(2);        
                }else{
                    this.listExtractionMonthly =  listExtractionMonthly;    
                }
                
                if(extractionPeriodDaily==null){
                    this.setDefaultDaily();                        
                }else{
                    this.extractionPeriodDaily=    extractionPeriodDaily;                        
                }
                
                if(extractionYear ==null){
                    this.setDefaultYear();   
                }else{
                    this.extractionYear =    extractionYear; 
                }
                if(extractionAverMonth == null){
                 this.setDefaultMultiMonth();   
                }else{
                   this.extractionAverMonth = extractionAverMonth; 
                }
            }
            if(alarmCategory ==11){
                this.listExtractionMonthly = null;
            }
            
            // set default data for schedule year
            if (alarmCategory == AlarmCategory.SCHEDULE_YEAR) {
                this.setDefaultScheYear(extractionScheYear);
            }
        }

        public setExtractPeriod(extractionPeriodDaily: ExtractionPeriodDailyCommand) {
            this.extractionPeriodDaily = extractionPeriodDaily;
        }
        
        public setExtractUnit(extractionPeriodUnit: PeriodUnitCommand) {
            this.extractionPeriodUnit = extractionPeriodUnit;
        } 
        
        public setExtractMonthly(listExtractionMonthly: Array<ExtractionPeriodMonthlyCommand>) {
            this.listExtractionMonthly = listExtractionMonthly;
        }
        public setExtractYearly( extractionYear : ExtractionRangeYearCommand){
            this.extractionYear = extractionYear;
        }
        public setExtractionAverMonth( extractionAverMonth : ExtractionAverageMonthCommand){
            this.extractionAverMonth = extractionAverMonth;
        }
        
        public setDefaultPer(){
            
        }
        
        public setDefaultDaily() {
               this.extractionPeriodDaily = new ExtractionPeriodDailyCommand({
                                                extractionId: "",
                                                extractionRange: 0,
                                                strSpecify: 1,
                                                strPreviousDay: null,
                                                strMakeToDay: null,
                                                strDay: null,
                                                strPreviousMonth: 0,
                                                strCurrentMonth: 1,
                                                strMonth: 0,
                                                endSpecify: 1,
                                                endPreviousDay: null,
                                                endMakeToDay: null,
                                                endDay: null,
                                                endPreviousMonth: 0,
                                                endCurrentMonth: 1,
                                                endMonth: 0
                                            });
        }
        
        public setDefaulttUnit() {
                this.extractionPeriodUnit = new PeriodUnitCommand({
                                                extractionId: "",
                                                extractionRange: 3,
                                                segmentationOfCycle: 1
                                            });            
        }
        
        public setDefaultMonthly(unit: number){
            this.listExtractionMonthly.push( new ExtractionPeriodMonthlyCommand({
                                                     extractionId: "",
                                                     extractionRange: 0,
                                                     unit: unit,       
                                                     strSpecify : 1,        
                                                     yearType: 2,
                                                     specifyMonth: 0,
                                                     strMonth: 0,
                                                     strCurrentMonth: 1,
                                                     strPreviousAtr: 0,
                                                     endSpecify: 2,
                                                     extractPeriod: 12,
                                                     endMonth: 0,
                                                     endCurrentMonth: 1,
                                                     endPreviousAtr: 0     
                                             }) );
           
        }
        
        public setDefaultYear(){
            this.extractionYear = new ExtractionRangeYearCommand({
                                        extractionId: "",
                                        extractionRange: 1,
                                        year: 0,
                                        thisYear: 1    
                                    });    
        }
        
        public setDefaultMultiMonth(){
            this.extractionAverMonth = new ExtractionAverageMonthCommand({
                                        extractionId: "",
                                        extractionRange: 4,
                                        strMonth: 0   
                                    });    
        }
        
        /**
         * Default data Schedule Year
         */
        public setDefaultScheYear(data: ExtractionPeriodECommand) {
            if (data) {
                this.extractionScheYear =  data;
                return;
            }
            
            this.extractionScheYear = new ExtractionPeriodECommand({
                                                extractionId: "",
                                                extractionRange: 0,
                                                strSpecify: SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH,
                                                strPreviousDay: null,
                                                strMakeToDay: null,
                                                strSpecifyMonth: null,
                                                strPreviousMonth: 0,
                                                strCurrentMonth: 1,
                                                strMonth: 0,
                                                strYearSpecifiedType: 0,
                                                endSpecify: SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH,
                                                endPreviousDay: null,
                                                endMakeToDay: null,
                                                endDay: null,
                                                endPreviousMonth: 0,
                                                endCurrentMonth: 1,
                                                endMonth: 0,
                                                endFromStrMonth: 0,
                                            });
        }
    }

    export class ExtractionPeriodDailyCommand {
        extractionId: string;
        extractionRange: number;
        strSpecify: number;
        strPreviousDay: number;
        strMakeToDay: number;
        strDay: number;
        strPreviousMonth: number;
        strCurrentMonth: number;
        strMonth: number;
        endSpecify: number;
        endPreviousDay: number;
        endMakeToDay: number;
        endDay: number;
        endPreviousMonth: number;
        endCurrentMonth: number;
        endMonth: number;

        constructor(extractionDailyDto: ExtractionDailyDto) {
            this.extractionId = extractionDailyDto.extractionId;
            this.extractionRange = extractionDailyDto.extractionRange;
            this.strSpecify = extractionDailyDto.strSpecify;
            this.strPreviousDay = extractionDailyDto.strPreviousDay;
            this.strMakeToDay = extractionDailyDto.strMakeToDay;
            this.strDay = extractionDailyDto.strDay;
            this.strPreviousMonth = extractionDailyDto.strPreviousMonth;
            this.strCurrentMonth = extractionDailyDto.strCurrentMonth;
            this.strMonth = extractionDailyDto.strMonth;
            this.endSpecify = extractionDailyDto.endSpecify;
            this.endPreviousDay = extractionDailyDto.endPreviousDay;
            this.endMakeToDay = extractionDailyDto.endMakeToDay;
            this.endDay = extractionDailyDto.endDay;
            this.endPreviousMonth = extractionDailyDto.endPreviousMonth;
            this.endCurrentMonth = extractionDailyDto.endCurrentMonth;
            this.endMonth = extractionDailyDto.endMonth;
        }
    }
    
    export class PeriodUnitCommand {
        extractionId: string;
        extractionRange: number;
        segmentationOfCycle: number;
        constructor(periodUnitDto: PeriodUnitDto) {
            this.extractionId = periodUnitDto.extractionId;
            this.extractionRange = periodUnitDto.extractionRange;
            this.segmentationOfCycle = periodUnitDto.segmentationOfCycle;
            
        }
    }
    
    export class  ExtractionPeriodMonthlyCommand{
         extractionId: string;
         extractionRange: number;
         unit: number;        
         strSpecify : number;        
         yearType: number;
         specifyMonth: number;
         strMonth: number;
         strCurrentMonth: number;
         strPreviousAtr: number;
         endSpecify: number;
         extractPeriod: number;
         endMonth: number;
         endCurrentMonth: number;
         endPreviousAtr: number;     
        
        constructor(dto: ExtractionPeriodMonthlyDto){
            this.extractionId = dto.extractionId;
            this.extractionRange = dto.extractionRange;
            this.unit = dto.unit;
            this.strSpecify = dto.strSpecify;
            this.yearType = dto.yearType;
            this.specifyMonth = dto.specifyMonth;
            this.strMonth = dto.strMonth;
            this.strCurrentMonth = dto.strCurrentMonth;
            this.strPreviousAtr = dto.strPreviousAtr;
            this.endSpecify = dto.endSpecify;
            this.extractPeriod = dto.extractPeriod;
            this.endMonth = dto.endMonth;
            this.endCurrentMonth = dto.endCurrentMonth;
            this.endPreviousAtr = dto.endPreviousAtr;
            
        }
    }
    
    export class ExtractionRangeYearCommand{
        extractionId: string;
        extractionRange: number;
        year: number;
        thisYear: number;
        constructor(dto: ExtractionRangeYearDto){
            this.extractionId = dto.extractionId;
            this.extractionRange = dto.extractionRange;
            this.year = dto.year;
            this.thisYear = dto.thisYear;
        }
    }
    
    export class ExtractionAverageMonthCommand{
        extractionId: string;
        extractionRange: number;
        strMonth: number;
        constructor(dto: ExtractionAverMonthDto){
            this.extractionId = dto.extractionId;
            this.extractionRange = dto.extractionRange;
            this.strMonth = dto.strMonth;
        }
    }
    
    export class ExtractionPeriodECommand {
        extractionId: string;
        extractionRange: number;
        strSpecify: number;
        strSpecifyMonth: number;
        strPreviousMonth: number;
        strCurrentMonth: number;
        strMonth: number;
        strYearSpecifiedType: number;
        endSpecify: number;
        endPreviousMonth: number;
        endCurrentMonth: number;
        endMonth: number;
        endFromStrMonth: number;
        processingYm: string;

        constructor(extractionDailyDto: ExtractionEDto) {
            this.extractionId = extractionDailyDto.extractionId;
            this.extractionRange = extractionDailyDto.extractionRange;
            this.strSpecify = extractionDailyDto.strSpecify;
            this.strSpecifyMonth = extractionDailyDto.strSpecifyMonth;
            this.strPreviousMonth = extractionDailyDto.strPreviousMonth;
            this.strCurrentMonth = extractionDailyDto.strCurrentMonth;
            this.strMonth = extractionDailyDto.strMonth;
            this.strYearSpecifiedType = extractionDailyDto.strYearSpecifiedType;
            this.endSpecify = extractionDailyDto.endSpecify;
            this.endPreviousMonth = extractionDailyDto.endPreviousMonth;
            this.endCurrentMonth = extractionDailyDto.endCurrentMonth;
            this.endMonth = extractionDailyDto.endMonth;
            this.endFromStrMonth = extractionDailyDto.endFromStrMonth;
            this.processingYm = extractionDailyDto.processingYm;
        }
    }
    
    /**
     * 開始日
     */
    export enum StartSpecify{
        // 開始日当日から
        DAYS = 0,
        // 開始日締め開始日
        MONTH = 1
    }
    
    /**
     * 終了日
     */
    export enum EndSpecify{
        // 終了日当日から
        DAYS = 0,
        // 終了日締め終了日
        MONTH = 1
    }
    
    /**
     * Previous Classification
     */
    export enum PreviousClassification {
        /*** 前 */
        BEFORE = 0,
        /*** 先*/
        AHEAD= 1
    }
    
    /**
     * 開始月の指定方法
     */
    export enum SpecifyStartMonth {
        // 締め開始月を指定する
        DESIGNATE_CLOSE_START_MONTH = 1,
        // 固定の月度を指定する
        SPECIFY_FIXED_MOON_DEGREE = 2
    }
    
    /**
     * 終了月の指定方法
     */
    export enum SpecifyEndMonth {
        // 開始から期間を指定する
        SPECIFY_PERIOD_FROM_START_MONTH = 1,

        // 締め終了月を指定する
        SPECIFY_CLOSE_END_MONTH = 2    
    }
    
    /**
     * アラームリストで指定する年の種類
     */
    export enum YearSpecifiedType {
         // 本年
        FISCAL_YEAR = 1,
        // 本年度
        CURRENT_YEAR = 2    
    }
    
    /*
     * Alarm Category 
     */   
    export enum AlarmCategory {
        SCHEDULE_DAILY = 0,
        SCHEDULE_WEEKLY = 1,
        SCHEDULE_4_WEEK = 2,
        SCHEDULE_MONTHLY = 3,
        SCHEDULE_YEAR = 4,
        DAILY = 5,
        WEEKLY = 6,
        MONTHLY = 7,
        APPLICATION_APPROVAL = 8,
        MULTIPLE_MONTHS = 9,
        ANY_PERIOD = 10,
        ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS = 11,
        _36_AGREEMENT = 12,
        MAN_HOUR_CHECK = 13,
        MASTER_CHECK = 14,
    }

}