module nts.uk.at.view.ksm001.f {

    import CommonGuidelineSettingDto = service.model.CommonGuidelineSettingDto;
    import UsageSettingDto = nts.uk.at.view.ksm001.a.service.model.UsageSettingDto;
    import UsageSettingModel = nts.uk.at.view.ksm001.a.viewmodel.UsageSettingModel;
    import EstimatedConditionDto = service.model.EstimatedConditionDto;
    import EstimatedAlarmColorDto = service.model.EstimatedAlarmColorDto;
    import ReferenceConditionDto = service.model.ReferenceConditionDto;

    export module viewmodel {

        export class ScreenModel {
            commonGuidelineSettingModel: CommonGuidelineSettingModel;
            comparisonTarget: ComparisonTarget;
//            selectedCompaTarget: KnockoutObservable<number>; 
//            comparisonTagetList: KnockoutObservableArray<any>;
//           
            constructor() {
                var self = this;
                self.commonGuidelineSettingModel = new CommonGuidelineSettingModel();
                self.comparisonTarget = new ComparisonTarget();
//                this.comparisonTagetList = ko.observableArray([
//                    {id: 1, name: nts.uk.resource.getText('KSM001_67')},
//                    {id: 2, name: nts.uk.resource.getText('KSM001_68')},
//                    {id: 3, name: nts.uk.resource.getText('KSM001_69')}
////                    new ComparisonTargetModel(EstComparison.PRE_DETERMINED, nts.uk.resource.getText('KSM001_67')),
////                    new ComparisonTargetModel(EstComparison.TOTAL_WORKING_HOURS, nts.uk.resource.getText('KSM001_68')),
////                    new ComparisonTargetModel(EstComparison.PER_COST_TIME, nts.uk.resource.getText('KSM001_69'))
//                ]);
//                this.selectedCompaTarget = ko.observable(1);
            }

            /**
            * start page data 
            */
            public startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();
                $.when(service.findCommonGuidelineSetting()).done(function(data){
                    if(data.alarmColors){
                        self.commonGuidelineSettingModel.updateData(data);
                    }
                    dfd.resolve();
                }).always(function() {
                    service.findEstimateComparison().done(function(data) {
                        self.comparisonTarget.selectedCompaTarget(data.comparisonAtr);
                    });
                });
                return dfd.promise();
            }
            
            /**
             * function on click save CommonGuidelineSetting
             */
            public saveCommonGuidelineSetting(): void {
                var self = this;

                nts.uk.ui.block.invisible();

                // set CommonGuidelineSettingDto
                let dto: CommonGuidelineSettingDto = self.commonGuidelineSettingModel.toDto();
                dto.estimateComparison = self.comparisonTarget.selectedCompaTarget();
                
                service.saveCommonGuidelineSetting(dto).done(function() {
                    // show message 15
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                        // close windows
                        nts.uk.ui.windows.close();
                    });
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
            }

            /**
             * Event on click cancel button.
             */
            public cancelSaveCommonGuidelineSetting(): void {
                nts.uk.ui.windows.close();
            }

        }     
        
        
        export class ReferenceConditionModel {
            yearlyDisplayCondition: KnockoutObservable<number>;
            monthlyDisplayCondition: KnockoutObservable<number>;
//            alarmCheckCondition: KnockoutObservable<number>;
            monthlyAlarmCkCondition: KnockoutObservable<number>;
            yearlyAlarmCkCondition: KnockoutObservable<number>;
            lstEstimateCondition: KnockoutObservableArray<EstimatedConditionDto>;

            constructor() {
                this.yearlyDisplayCondition = ko.observable(1);
                this.monthlyDisplayCondition = ko.observable(1);
//                this.alarmCheckCondition = ko.observable(1);
                this.monthlyAlarmCkCondition = ko.observable(1);
                this.yearlyAlarmCkCondition = ko.observable(1);
                this.lstEstimateCondition = ko.observableArray([
                    { code: 1, name: "条件1" },
                    { code: 2, name: "条件2" },
                    { code: 3, name: "条件3" },
                    { code: 4, name: "条件4" },
                    { code: 5, name: "条件5" },
                ]);
            }
            
            updateData(dto: ReferenceConditionDto) {
                this.yearlyDisplayCondition(dto.yearlyDisplayCondition);
                this.monthlyDisplayCondition(dto.monthlyDisplayCondition);
//                this.alarmCheckCondition(dto.alarmCheckCondition);
                this.monthlyAlarmCkCondition(dto.monthlyAlarmCkCondition);
                this.yearlyAlarmCkCondition(dto.yearlyAlarmCkCondition);
            }
            
            toDto(): ReferenceConditionDto {
                var dto: ReferenceConditionDto = 
                    {
                        yearlyDisplayCondition: this.yearlyDisplayCondition(),
                        monthlyDisplayCondition: this.monthlyDisplayCondition(),
//                        alarmCheckCondition: this.alarmCheckCondition()
                        monthlyAlarmCkCondition: this.monthlyAlarmCkCondition(),
                        yearlyAlarmCkCondition: this.yearlyAlarmCkCondition()
                    };
                return dto;
            }
        }
        export class CommonGuidelineSettingModel{
            estAlarmColors1st:   KnockoutObservable<string>;  
            estAlarmColors2nd:   KnockoutObservable<string>;  
            estAlarmColors3rd:   KnockoutObservable<string>;  
            estAlarmColors4th:   KnockoutObservable<string>;  
            estAlarmColors5th:   KnockoutObservable<string>;

            /** The estimate time. */
            estimateTime: ReferenceConditionModel;

            /** The estimate price. */
            estimatePrice: ReferenceConditionModel;

            /** The estimate number of days. */
            estimateNumberOfDays: ReferenceConditionModel;
            
            
            
            constructor() {
                this.estAlarmColors1st = ko.observable('#0000ff');
                this.estAlarmColors2nd = ko.observable('#ffff00');
                this.estAlarmColors3rd = ko.observable('#ff9900');
                this.estAlarmColors4th = ko.observable('#ff00ff');
                this.estAlarmColors5th = ko.observable('#ff0000');
                this.estimateTime = new ReferenceConditionModel();
                this.estimatePrice = new ReferenceConditionModel();
                this.estimateNumberOfDays = new ReferenceConditionModel();
            }
            
            updateData(dto: CommonGuidelineSettingDto) {
                for (var color of dto.alarmColors) {
                    switch(color.guidelineCondition){
                        case EstimatedCondition.CONDITION_1ST:
                        this.estAlarmColors1st(color.color);
                        break;    
                        case EstimatedCondition.CONDITION_2ND:
                        this.estAlarmColors2nd(color.color);
                        break;    
                        case EstimatedCondition.CONDITION_3RD:
                        this.estAlarmColors3rd(color.color);
                        break;    
                        case EstimatedCondition.CONDITION_4TH:
                        this.estAlarmColors4th(color.color);
                        break;    
                        case EstimatedCondition.CONDITION_5TH:
                        this.estAlarmColors5th(color.color);
                        break;    
                    }
                }
                this.estimateTime.updateData(dto.estimateTime);
                this.estimatePrice.updateData(dto.estimatePrice);
                this.estimateNumberOfDays.updateData(dto.estimateNumberOfDays);
            }
            
            toDto(): CommonGuidelineSettingDto {
                var alarmColors: EstimatedAlarmColorDto[] = [];
                alarmColors.push(
                    { guidelineCondition: EstimatedCondition.CONDITION_1ST, color: this.estAlarmColors1st() });
                alarmColors.push(
                    { guidelineCondition: EstimatedCondition.CONDITION_2ND, color: this.estAlarmColors2nd() });
                alarmColors.push(
                    { guidelineCondition: EstimatedCondition.CONDITION_3RD, color: this.estAlarmColors3rd() });
                alarmColors.push(
                    { guidelineCondition: EstimatedCondition.CONDITION_4TH, color: this.estAlarmColors4th() });
                alarmColors.push(
                    { guidelineCondition: EstimatedCondition.CONDITION_5TH, color: this.estAlarmColors5th() });
                var dto: CommonGuidelineSettingDto =
                    {
                        alarmColors: alarmColors,
                        estimateTime: this.estimateTime.toDto(),
                        estimatePrice: this.estimatePrice.toDto(),
                        estimateNumberOfDays: this.estimateNumberOfDays.toDto(),
                        estimateComparison: 0
                    };
                return dto;
            }
        }
        
        export class ComparisonTarget {
            selectedCompaTarget: KnockoutObservable<number>; 
            comparisonTagetList: Array<ComparisonTargetModel>; 
            
            constructor() {
//                this.comparisonTagetList = ko.observableArray([
//                    new ComparisonTargetModel(EstComparison.PRE_DETERMINED, nts.uk.resource.getText('KSM001_67')),
//                    new ComparisonTargetModel(EstComparison.TOTAL_WORKING_HOURS, nts.uk.resource.getText('KSM001_68')),
//                    new ComparisonTargetModel(EstComparison.PER_COST_TIME, nts.uk.resource.getText('KSM001_69'))
//                ]);
                this.selectedCompaTarget = ko.observable(EstComparison.PRE_DETERMINED);
                this.comparisonTagetList = [
                    new ComparisonTargetModel(EstComparison.PRE_DETERMINED, nts.uk.resource.getText('KSM001_67')),
                    new ComparisonTargetModel(EstComparison.TOTAL_WORKING_HOURS, nts.uk.resource.getText('KSM001_68')),
                    new ComparisonTargetModel(EstComparison.PER_COST_TIME, nts.uk.resource.getText('KSM001_69'))
                ]
            }
        }
        
        export class ComparisonTargetModel {
            id: number;
            name: string;
            
            constructor(id, name) {
                var self = this;
                self.id = id;
                self.name = name;
            }
        }
        
        // 目安比較対象区分
        export enum EstComparison {
            // 所定時間
            PRE_DETERMINED = 1,
            // 総労働時間
            TOTAL_WORKING_HOURS = 2,
            // 人件費時間
            PER_COST_TIME = 3
        }
        
        //  目安利用条件
        export enum EstimatedCondition {
            // 条件1
            CONDITION_1ST = 1,
            // 条件2
            CONDITION_2ND = 2,
            // 条件3
            CONDITION_3RD = 3,
            // 条件4
            CONDITION_4TH = 4,
            // 条件5
            CONDITION_5TH = 5,
        }
    }
}