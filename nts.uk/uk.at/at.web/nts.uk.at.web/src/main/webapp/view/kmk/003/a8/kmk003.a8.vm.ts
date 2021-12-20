module a8 {
    
    import WorkTimeDailyAtr = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeDailyAtr;
    import WorkTimeMethodSet = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeMethodSet
    import WorkTimeSettingEnumDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeSettingEnumDto;
    import EnumConstantDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.EnumConstantDto;
    
    import WorkTimezoneGoOutSetModel = nts.uk.at.view.kmk003.a.viewmodel.common.WorkTimezoneGoOutSetModel;
    import TotalRoundingSetModel = nts.uk.at.view.kmk003.a.viewmodel.common.TotalRoundingSetModel;
    import GoOutTypeRoundingSetModel = nts.uk.at.view.kmk003.a.viewmodel.common.GoOutTypeRoundingSetModel;
    import GoOutTimeRoundingSettingModel = nts.uk.at.view.kmk003.a.viewmodel.common.GoOutTimeRoundingSettingModel;
    import TimeRoundingSettingModel = nts.uk.at.view.kmk003.a.viewmodel.common.TimeRoundingSettingModel;
    
    import MainSettingModel = nts.uk.at.view.kmk003.a.viewmodel.MainSettingModel;
    import TabMode = nts.uk.at.view.kmk003.a.viewmodel.TabMode;
    
    /**
     * Screen Model - Tab 8
     * 就業時間帯の共通設定 -> 外出設定
     * WorkTimeCommonSet -> WorkTimeGoOutSet
     */
    class ScreenModel {

        isNewMode: KnockoutObservable<boolean>;
        isFlow : KnockoutObservable<boolean>;
        // Screen mode
        isDetailMode: KnockoutObservable<boolean>;
        
        // Screen data model
        model: MainSettingModel;
        settingEnum: WorkTimeSettingEnumDto;

        // UI component
        workTimeTabs: KnockoutObservableArray<any>;
        workTimeSelectedTab: KnockoutObservable<string>;
        
        // Detail mode - Data       
        totalRoundingSameFrameRoundingSet: KnockoutObservable<number>;
        totalRoundingFrameStraddRoundingSet: KnockoutObservable<number>;

        otTimePersonApproTimeSetting: TimeRoundingSetting;
        workTimePersonApproTimeSetting: TimeRoundingSetting;
        pubHolWorkTimePersonApproTimeSetting: TimeRoundingSetting;
        
        otTimePersonDeductTimeSetting: TimeRoundingSetting;
        workTimePersonDeductTimeSetting: TimeRoundingSetting;
        pubHolWorkTimePersonDeductTimeSetting: TimeRoundingSetting;
        
        otTimePublicApproTimeSetting: TimeRoundingSetting;
        workTimePublicApproTimeSetting: TimeRoundingSetting;
        pubHolWorkTimePublicApproTimeSetting: TimeRoundingSetting;
        
        listRoundingBreakTimezone: KnockoutObservableArray<any>;

        screenMode: any;
        
        // Simple mode - Data (nothing)      
        
        /**
         * Constructor.
         */
        constructor(isNewMode: KnockoutObservable<boolean>, screenMode: any, model: MainSettingModel, settingEnum: WorkTimeSettingEnumDto) {
            let _self = this;
            _self.isNewMode = isNewMode;

            _self.isNewMode.subscribe((v) => {
                // Set default value for switch button
                if (v) {
                    if (!nts.uk.util.isNullOrUndefined(_self.otTimePersonApproTimeSetting)) _self.otTimePersonApproTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.workTimePersonApproTimeSetting)) _self.workTimePersonApproTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.pubHolWorkTimePersonApproTimeSetting)) _self.pubHolWorkTimePersonApproTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.otTimePersonDeductTimeSetting)) _self.otTimePersonDeductTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.workTimePersonDeductTimeSetting)) _self.workTimePersonDeductTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.pubHolWorkTimePersonDeductTimeSetting)) _self.pubHolWorkTimePersonDeductTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.otTimePublicApproTimeSetting)) _self.otTimePublicApproTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.workTimePublicApproTimeSetting)) _self.workTimePublicApproTimeSetting.setDefaultRounding();
                    if (!nts.uk.util.isNullOrUndefined(_self.pubHolWorkTimePublicApproTimeSetting)) _self.pubHolWorkTimePublicApproTimeSetting.setDefaultRounding();
                }    
            });
            
            // Check exist
            if (nts.uk.util.isNullOrUndefined(model) || nts.uk.util.isNullOrUndefined(settingEnum)) {
                // Stop rendering page
                return;    
            }
            
            // Binding data
            _self.screenMode = screenMode;
            _self.model = model; 
            _self.settingEnum = settingEnum;
            _self.isFlow = model.workTimeSetting.isFlow;
            
            // Init UI
            _self.workTimeTabs = ko.observableArray([
                { id: 'personal-setting-tab', title: nts.uk.resource.getText("KMK003_194"), content: '.personal-setting-tab-content', enable: ko.observable(true), visible: ko.observable(true) },
                { id: 'official-setting-tab', title: nts.uk.resource.getText("KMK003_195"), content: '.official-setting-tab-content', enable: ko.observable(true), visible: ko.observable(true) }
            ]);
            _self.workTimeSelectedTab = ko.observable('personal-setting-tab');
            
            // Init all data                                    
            _self.otTimePersonApproTimeSetting = new TimeRoundingSetting(settingEnum);
            _self.workTimePersonApproTimeSetting = new TimeRoundingSetting(settingEnum);
            _self.pubHolWorkTimePersonApproTimeSetting = new TimeRoundingSetting(settingEnum);
            
            _self.otTimePersonDeductTimeSetting = new TimeRoundingSetting(settingEnum);
            _self.workTimePersonDeductTimeSetting = new TimeRoundingSetting(settingEnum);
            _self.pubHolWorkTimePersonDeductTimeSetting = new TimeRoundingSetting(settingEnum);
        
            _self.otTimePublicApproTimeSetting = new TimeRoundingSetting(settingEnum);
            _self.workTimePublicApproTimeSetting = new TimeRoundingSetting(settingEnum);
            _self.pubHolWorkTimePublicApproTimeSetting = new TimeRoundingSetting(settingEnum);          
            
            _self.listRoundingBreakTimezone = ko.observableArray([
                { value: 1, localizedName: nts.uk.resource.getText("KMK003_198") },
                { value: 0, localizedName: nts.uk.resource.getText("KMK003_199") }
            ]);          
            //_self.listRoundingBreakTimezone(_self.settingEnum.roundingBreakTimezone);  
            
            _self.changeBinding(_self.model.commonSetting.goOutSet);
            
            // Detail mode and simple mode is same
            _self.isDetailMode = ko.observable(null);
            _self.isDetailMode.subscribe(newValue => {
                // Nothing to do
            });                                                        
            // Subscribe Detail/Simple mode 
            screenMode.subscribe((value: any) => {
                value == TabMode.DETAIL ? _self.isDetailMode(true) : _self.isDetailMode(false);
            }); 
        }
        
        /**
         * Start tab
         */
        public startTab(screenMode: any): void {
            let _self = this;
            screenMode() == TabMode.DETAIL ? _self.isDetailMode(true) : _self.isDetailMode(false);
        }     
        
        /**
         * UI - All: create new Binding data
         */
        private createBinding(): WorkTimezoneGoOutSetModel {
            let _self = this;      
                 
            let result: WorkTimezoneGoOutSetModel = new WorkTimezoneGoOutSetModel();           
            return result;
        }        
        
        /**
         * UI - Detail: change Binding Detail mode
         */
        private changeBinding(goOutSet: WorkTimezoneGoOutSetModel): void {
            let _self = this;
            
            _self.totalRoundingSameFrameRoundingSet = goOutSet.totalRoundingSet.setSameFrameRounding;
            _self.totalRoundingFrameStraddRoundingSet = goOutSet.totalRoundingSet.frameStraddRoundingSet;
            
            // Update binding for Time Setting model
            _self.otTimePersonApproTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.ottimezone.privateUnionGoOut.approTimeRoundingSetting);
            _self.workTimePersonApproTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.workTimezone.privateUnionGoOut.approTimeRoundingSetting);
            _self.pubHolWorkTimePersonApproTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.pubHolWorkTimezone.privateUnionGoOut.approTimeRoundingSetting);
            
            _self.otTimePersonDeductTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.ottimezone.privateUnionGoOut.deductTimeRoundingSetting);
            _self.workTimePersonDeductTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.workTimezone.privateUnionGoOut.deductTimeRoundingSetting);
            _self.pubHolWorkTimePersonDeductTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.pubHolWorkTimezone.privateUnionGoOut.deductTimeRoundingSetting);
            
            _self.otTimePublicApproTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.ottimezone.officalUseCompenGoOut.approTimeRoundingSetting);
            _self.workTimePublicApproTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.workTimezone.officalUseCompenGoOut.approTimeRoundingSetting);
            _self.pubHolWorkTimePublicApproTimeSetting.updateBinding(goOutSet.diffTimezoneSetting.pubHolWorkTimezone.officalUseCompenGoOut.approTimeRoundingSetting);         
        }
        
        /**
         * Handle when using tab button (switch child tab)
         */
        public changeWorkingTab(parent: any, data: any, e: any) {
            if (e.which == 9) {
                let tabindex = e.target.attributes.tabindex.value;
                if (nts.uk.util.isNullOrUndefined(tabindex)) {
                    return;    
                } 
                
                if (tabindex === '117' && parent.pubHolWorkTimePersonDeductTimeSetting.timeRoundingMethod() === 0) {
                    parent.workTimeSelectedTab('official-setting-tab'); 
                } else if (tabindex === '119') {
                    parent.workTimeSelectedTab('official-setting-tab'); 
                } else {      
                    $("[tabindex='" + (Number(tabindex) + 1).toString() + "']").focus();                                        
                }                          
            }
        }

    }
    
    class TimeRoundingSetting {
        timeRoundingMethod: KnockoutObservable<number>;
        timeRoundingTime: KnockoutObservable<number>;
        timeRounding: KnockoutObservable<number>;        
        isEnable: KnockoutObservable<boolean>;
        
        listRoundingBreakTime: KnockoutObservableArray<any>;
        listRoundingBreakTimeOvertime: KnockoutObservableArray<any>;
        listRoundingBreakTimeHoliday: KnockoutObservableArray<any>;
        listRoundingTimeValue: KnockoutObservableArray<EnumConstantDto>;
        listRoundingValue: KnockoutObservableArray<EnumConstantDto>;
        
        constructor(settingEnum: WorkTimeSettingEnumDto) {
            let _self = this;
            
            _self.listRoundingBreakTime = ko.observableArray([
                { value: 0, localizedName: nts.uk.resource.getText("KMK003_87") },
                { value: 1, localizedName: nts.uk.resource.getText("KMK003_88") }
            ]);
            _self.listRoundingBreakTimeOvertime = ko.observableArray([
              { value: 0, localizedName: nts.uk.resource.getText("KMK003_330") },
              { value: 1, localizedName: nts.uk.resource.getText("KMK003_88") }
          ]);
            _self.listRoundingBreakTimeHoliday = ko.observableArray([
              { value: 0, localizedName: nts.uk.resource.getText("KMK003_331") },
              { value: 1, localizedName: nts.uk.resource.getText("KMK003_88") }
          ]);
            //_self.listRoundingBreakTime(settingEnum.roundingBreakTime);
            _self.listRoundingTimeValue = ko.observableArray(settingEnum.roundingTime);
            _self.listRoundingValue = ko.observableArray(settingEnum.roundingSimple);           
            
            _self.isEnable = ko.observable(false);
        }
        
        public setDefaultRounding() {
            let _self = this;
            
            _self.timeRounding(1);
        }
        
        public updateBinding(modelValue: GoOutTimeRoundingSettingModel) {
            let _self = this;
            
            // Get model value into view model
            _self.timeRoundingMethod = modelValue.roundingMethod;
            _self.timeRoundingTime = modelValue.roundingSetting.roundingTime;
            _self.timeRounding = modelValue.roundingSetting.rounding;
            
            _self.timeRoundingMethod.valueHasMutated();
            
            // Update into model in case of data change
            _self.timeRoundingMethod.subscribe(newValue => {
                if (newValue === 0) {
                    _self.isEnable(false);
                } else {
                    _self.isEnable(true);
                }
            });
        }
    }
    
    /**
     * Knockout Binding Handler - Tab 8
     */
    class KMK003A8BindingHandler implements KnockoutBindingHandler {
        /**
         * Constructor.
         */
        constructor() {
        }

        /**
         * Init.
         */
        init(element: any, valueAccessor: () => any, allBindingsAccessor: () => any, viewModel: any, bindingContext: KnockoutBindingContext): void {}

        /**
         * Update
         */
        update(element: any, valueAccessor: () => any, allBindingsAccessor: () => any, viewModel: any, bindingContext: KnockoutBindingContext): void {
            var webserviceLocator = nts.uk.request.location.siteRoot
                .mergeRelativePath(nts.uk.request.WEB_APP_NAME["at"] + '/')
                .mergeRelativePath('/view/kmk/003/a8/index.xhtml').serialize();
            //get data
            let input = valueAccessor();
            let screenMode = input.screenMode;
            let model = input.model;
            let settingEnum = input.enum;

            let screenModel = new ScreenModel(input.isNewMode, screenMode, model, settingEnum);
            $(element).load(webserviceLocator, function() {
                ko.cleanNode($(element)[0]);
                ko.applyBindingsToDescendants(screenModel, $(element)[0]);
                screenModel.startTab(screenMode);
                $('.personal-setting-tab-content').css({"width":"608px","overflow-x":"scroll"});
            });
        }

    }
    ko.bindingHandlers['ntsKMK003A8'] = new KMK003A8BindingHandler();
}
