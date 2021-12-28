module nts.uk.at.view.kmk003.a {

    import SimpleWorkTimeSettingDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.SimpleWorkTimeSettingDto;
    import WorkTimeSettingEnumDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeSettingEnumDto;
    import EnumConstantDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.EnumConstantDto;
    import WorkTimeSettingCondition = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeSettingCondition;

    import FlexWorkSettingDto = nts.uk.at.view.kmk003.a.service.model.flexset.FlexWorkSettingDto;
    
    import EmTimeZoneSetModel = nts.uk.at.view.kmk003.a.viewmodel.common.EmTimeZoneSetModel;
    import WorkTimeSettingInfoDto = nts.uk.at.view.kmk003.a.service.model.common.WorkTimeSettingInfoDto;
    
    import WorkTimeSettingModel = nts.uk.at.view.kmk003.a.viewmodel.worktimeset.WorkTimeSettingModel;
    import WorkTimeDisplayModeModel = nts.uk.at.view.kmk003.a.viewmodel.worktimeset.WorkTimeDisplayModeModel;
    import ManageEntryExitModel = nts.uk.at.view.kmk003.a.viewmodel.worktimeset.ManageEntryExitModel;
    import PredetemineTimeSettingModel = nts.uk.at.view.kmk003.a.viewmodel.predset.PredetemineTimeSettingModel;
    import TimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.predset.TimezoneModel;
    import WorkTimezoneCommonSetModel = nts.uk.at.view.kmk003.a.viewmodel.common.WorkTimezoneCommonSetModel;
    import FixedWorkSettingModel = nts.uk.at.view.kmk003.a.viewmodel.fixedset.FixedWorkSettingModel;
    import FlowWorkSettingModel = nts.uk.at.view.kmk003.a.viewmodel.flowset.FlowWorkSettingModel;
    import DiffTimeWorkSettingModel = nts.uk.at.view.kmk003.a.viewmodel.difftimeset.DiffTimeWorkSettingModel;
    import FlexWorkSettingModel = nts.uk.at.view.kmk003.a.viewmodel.flexset.FlexWorkSettingModel;
    
    import FixedWorkSettingSaveCommand = nts.uk.at.view.kmk003.a.service.model.command.FixedWorkSettingSaveCommand;
    import FlowWorkSettingSaveCommand = nts.uk.at.view.kmk003.a.service.model.command.FlowWorkSettingSaveCommand;
    import FlexWorkSettingSaveCommand = nts.uk.at.view.kmk003.a.service.model.command.FlexWorkSettingSaveCommand;
    import DiffTimeSettingSaveCommand = nts.uk.at.view.kmk003.a.service.model.command.DiffTimeWorkSettingSaveCommand;
    
    import WorkTimezoneCommonSetDto = service.model.common.WorkTimezoneCommonSetDto;
    import SubHolTransferSetDto = service.model.common.SubHolTransferSetDto;
    import NotUseAtr = nts.uk.at.view.kmk003.a.viewmodel.common.NotUseAtr;

    import EmTimeZoneSetDto = nts.uk.at.view.kmk003.a.service.model.common.EmTimeZoneSetDto;
    import WorkTimeDailyAtr = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeDailyAtr;
    import OverTimeOfTimeZoneSetModel = nts.uk.at.view.kmk003.a.viewmodel.common.OverTimeOfTimeZoneSetModel;
    
    export module viewmodel {

        export class ScreenModel {
            
            workTimeSettings: KnockoutObservableArray<SimpleWorkTimeSettingDto>;
            columnWorktimeSettings: KnockoutObservable<any>;
            selectedWorkTimeCode: KnockoutObservable<string>;

            //tab mode
            tabModeOptions: KnockoutObservableArray<any>;
            tabMode: KnockoutObservable<number>;

            //use half day
            useHalfDayOptions: KnockoutObservableArray<any>;
            useHalfDayWorking: KnockoutObservable<boolean>;
            useHalfDayOvertime: KnockoutObservable<boolean>;
            useHalfDayBreak: KnockoutObservable<boolean>;

            //tabs
            tabs: KnockoutObservableArray<TabItem>;
            selectedTab: KnockoutObservable<string>;
            //data
            isClickSave: KnockoutObservable<boolean>;
            
            mainSettingModel: MainSettingModel;
            workTimeSettingLoader: WorkTimeSettingLoader;
            
            settingEnum: WorkTimeSettingEnumDto;
            backupOptions: EnumConstantDto[];
            
            screenMode: KnockoutObservable<number>;
            otsukaMode: KnockoutObservable<boolean>
            isNewMode: KnockoutComputed<boolean>;
            isCopyMode: KnockoutComputed<boolean>;
            isNewOrCopyMode: KnockoutComputed<boolean>;
            isUpdateMode: KnockoutComputed<boolean>;
            isSimpleMode: KnockoutComputed<boolean>;
            isDetailMode: KnockoutComputed<boolean>;
            isLoading: KnockoutObservable<boolean>;
            
            flexWorkManaging: boolean;
            workMultiple: KnockoutObservable<boolean>;
            overTimeWorkFrameOptions: KnockoutObservableArray<any>;
            //update for storage tab 11
            backupCommonSetting: WorkTimezoneCommonSetDto
            
            langId: KnockoutObservable<string> = ko.observable('ja');
            isJapanese: KnockoutObservable<boolean> = ko.observable(true);
            lstWorkTimeLanguage: KnockoutObservableArray<IWorkTimeLanguage> = ko.observableArray([]);

            tabA2Text : KnockoutObservable<string> = ko.observable("");

            constructor() {
                let self = this;
                // initial tab mode
                self.tabMode = ko.observable(TabMode.DETAIL);
                self.selectedTab = ko.observable(TabID.TAB1);

                // initial screen mode
                self.screenMode = ko.observable(ScreenMode.NEW);

                //initial otsuka mode
                self.otsukaMode = ko.observable(false);

                self.workMultiple = ko.observable(false);

                self.initComputedValue();

                // init useHalfDay
                self.useHalfDayOptions = ko.observableArray([
                    { code: true, name: nts.uk.resource.getText("KMK003_321") },
                    { code: false, name: nts.uk.resource.getText("KMK003_322") }
                ]);
                self.useHalfDayWorking = ko.observable(false); // A19_1_2 initial value = false
                self.useHalfDayOvertime = ko.observable(false); // A19_2_2 initial value = false
                self.useHalfDayBreak = ko.observable(false); // A19_3_2 initial value = false


                self.mainSettingModel = new MainSettingModel(self.tabMode, self.isNewOrCopyMode, self.useHalfDayOptions, self.useHalfDayWorking, self.useHalfDayOvertime, self.useHalfDayBreak, self.isNewMode);
                self.selectedWorkTimeCode = ko.observable('');
                self.workTimeSettingLoader = new WorkTimeSettingLoader(self.mainSettingModel.workTimeSetting.worktimeCode);
                self.workTimeSettings = ko.observableArray([]);
                self.isLoading = ko.observable(false);

                // data get from service
                self.isClickSave = ko.observable(false);

                // initial data source
                self.initDatasource();

                // initial subscribe
                self.initSubscribe();

                //over time work frame options
                self.overTimeWorkFrameOptions = ko.observableArray([]);

                self.backupCommonSetting = null;

                self.langId.subscribe(() => {
                    let lang: string = ko.toJS(self.langId);

                    if (lang != 'ja') {
                        $("#tab-panel").addClass("disabled-panel");
                        self.isJapanese(false);
                        self.findWorkTimeLanguage();
                    } else {
                        $("#tab-panel").removeClass("disabled-panel");
                        self.isJapanese(true);
                        
                        $("#screen-content-left").css('width', '360');
                        $("#single-list").igGrid("option", "width", "300px");
                        //remove columns otherLanguageName
                        let cols = $("#single-list").igGrid("option", "columns");
                        cols.splice(2, 1);
                        $("#single-list").igGrid("option", "columns", cols);
                        self.selectedWorkTimeCode.valueHasMutated();
                    }
                });
                // enable/disable button 「多言語切替」
                self.isNewOrCopyMode.subscribe((value)=>{
                    if(value){
                        $('.ntsSwitchLanguage').attr('disabled', 'true');
                    } else {
                        $('.ntsSwitchLanguage').removeAttr("disabled");
                    }
                });
            }
           
            /**
             * Start page.
             */
            public startPage(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                self.bindFunction();
                // switch language
                $("#switch-language")['ntsSwitchMasterLanguage']();
                $("#switch-language").on("selectionChanged", (event: any) => self.langId(event['detail']['languageId']));
                
                service.findAllUsedOvertimeWorkFrame().done(v => {
                    self.overTimeWorkFrameOptions(v);
                    service.findSettingFlexWork().done(vl => {
                        self.flexWorkManaging = vl.flexWorkManaging == 1 ? true : false;

                        self.getAllEnums().done(() => {
                            self.backupOptions = self.settingEnum.workTimeMethodSet;
                            self.setFlexOptionVisibility();
                            self.loadListWorktime().done(() => dfd.resolve());
                        });
                    });
                });

                //work multiple
                service.findSettingWorkMultiple().done(rs => {
                    self.workMultiple(rs.workMultiple == NotUseAtr.USE)
                })

                return dfd.promise();
            }

            /**
             * Initial data source
             */
            private initDatasource(): void {
                let self = this;

                // Define workTimeSetting column 
                self.columnWorktimeSettings = ko.observableArray([
                    { headerText: nts.uk.resource.getText("KMK003_10"), prop: 'worktimeCode', width: 50 },
                    { headerText: nts.uk.resource.getText("KMK003_11"), prop: 'workTimeName', width: 180 },
                    { headerText: nts.uk.resource.getText("KMK003_12"), prop: 'isAbolish', width: 40,
                        formatter: (isAbolish: any):string => {
                            if (isAbolish === true || isAbolish === 'true') {
                                return '<img src="img/checked.png" style="margin-left: 15px; width: 20px; height: 20px;" />';
                            }
                            return '';
                        }
                    }
                ]);

                //tab mode
                self.tabModeOptions = ko.observableArray([
                    { code: TabMode.SIMPLE, name: nts.uk.resource.getText("KMK003_190") },
                    { code: TabMode.DETAIL, name: nts.uk.resource.getText("KMK003_191") }
                ]);

                // tabs data source
                    self.tabs = ko.observableArray([
                    new TabItem(TabID.TAB1, nts.uk.resource.getText("KMK003_17"), '.tab-a1', true, true),
                    new TabItem(TabID.TAB2, nts.uk.resource.getText("KMK003_18"), '.tab-a2', true, true),
                    new TabItem(TabID.TAB3, nts.uk.resource.getText("KMK003_89"), '.tab-a3', true, true),
                    new TabItem(TabID.TAB4, nts.uk.resource.getText("KMK003_19"), '.tab-a4', true, true),
                    new TabItem(TabID.TAB5, nts.uk.resource.getText("KMK003_20"), '.tab-a5', true, true),
                    new TabItem(TabID.TAB6, nts.uk.resource.getText("KMK003_90"), '.tab-a6', true, true),
                    new TabItem(TabID.TAB7, nts.uk.resource.getText("KMK003_21"), '.tab-a7', true, true),
                    new TabItem(TabID.TAB8, nts.uk.resource.getText("KMK003_200"), '.tab-a8', true, true),
                    new TabItem(TabID.TAB9, nts.uk.resource.getText("KMK003_23"), '.tab-a9', true, true),
                    new TabItem(TabID.TAB10, nts.uk.resource.getText("KMK003_24"), '.tab-a10', true, true),
                    new TabItem(TabID.TAB11, nts.uk.resource.getText("KMK003_25"), '.tab-a11', true, true),
                    new TabItem(TabID.TAB12, nts.uk.resource.getText("KMK003_26"), '.tab-a12', true, true),
                    new TabItem(TabID.TAB13, nts.uk.resource.getText("KMK003_27"), '.tab-a13', true, true),
                    new TabItem(TabID.TAB14, nts.uk.resource.getText("KMK003_28"), '.tab-a14', true, true),
                    new TabItem(TabID.TAB15, nts.uk.resource.getText("KMK003_29"), '.tab-a15', true, true),
                    new TabItem(TabID.TAB16, nts.uk.resource.getText("KMK003_30"), '.tab-a16', true, true),
                    new TabItem(TabID.TAB17, nts.uk.resource.getText("KMK003_219"), '.tab-a17', true, self.otsukaMode())
                ]);
            }
            
            /**
             * Initial subscribe & computed value
             */
            private initSubscribe(): void {
                let self = this;

                self.isNewMode.subscribe((v) => {
                    if (v){
                        self.useHalfDayWorking(false);
                        self.useHalfDayOvertime(false);
                        self.useHalfDayBreak(false);
                    }
                })

                self.mainSettingModel.workTimeSetting.workTimeDivision.workTimeDailyAtr.subscribe((val) => {

                    if (val == EnumWorkForm.FLEX ){
                        $($('.tabs-list')[0]).find('label:nth-child(2)').find('span').html(nts.uk.resource.getText('KMK003_317'));
                        $('#tab-panel > .tabs-content').removeClass('left-122');
                        $('#tab-panel > .tabs-content').addClass('left-178');
                    } else {
                        $($('.tabs-list')[0]).find('label:nth-child(2)').find('span').html(nts.uk.resource.getText('KMK003_18'));
                        $('#tab-panel > .tabs-content').removeClass('left-178');
                        $('#tab-panel > .tabs-content').addClass('left-122');
                    }

                    if (self.isNewMode()) {
                        self.clearAllError();
                        self.isLoading(false);
                        self.mainSettingModel.resetData(self.isNewMode());
                        self.isLoading(true);
                    }
                });
                self.mainSettingModel.workTimeSetting.workTimeDivision.workTimeMethodSet.subscribe(() => {
                    if (self.isNewMode()) {
                        self.clearAllError();
                        self.isLoading(false);
                        self.mainSettingModel.resetData(self.isNewMode());
                        self.isLoading(true);
                    }
                });

                self.tabMode.subscribe(newValue => {
                    if (newValue === TabMode.DETAIL) {
                        self.changeTabMode(true);
                    } else {                       
                        self.changeTabMode(false);
                    }   
                });

                self.useHalfDayWorking.subscribe(newVal => {
                    if (self.mainSettingModel.workTimeSetting.isFlex()) {
                        self.mainSettingModel.flexWorkSetting.halfDayWorkSet.workingTime(newVal);
                    }
                    if (self.mainSettingModel.workTimeSetting.isFixed()) {
                        self.mainSettingModel.fixedWorkSetting.halfDayWorkSet.workingTime(newVal);
                    }
                    if (self.mainSettingModel.workTimeSetting.isDiffTime()) {
                        self.mainSettingModel.diffWorkSetting.halfDayWorkSet.workingTime(newVal);
                    }
                });

                self.useHalfDayOvertime.subscribe(newVal => {
                    if (self.mainSettingModel.workTimeSetting.isFlex()) {
                        self.mainSettingModel.flexWorkSetting.halfDayWorkSet.overTime(newVal);
                    }
                    if (self.mainSettingModel.workTimeSetting.isFixed()) {
                        self.mainSettingModel.fixedWorkSetting.halfDayWorkSet.overTime(newVal);
                    }
                    if (self.mainSettingModel.workTimeSetting.isDiffTime()) {
                        self.mainSettingModel.diffWorkSetting.halfDayWorkSet.overTime(newVal);
                    }
                });

                self.useHalfDayBreak.subscribe(newVal => {
                    if (self.mainSettingModel.workTimeSetting.isFlex()) {
                        self.mainSettingModel.flexWorkSetting.halfDayWorkSet.breakingTime(newVal);
                    }
                    if (self.mainSettingModel.workTimeSetting.isFixed()) {
                        self.mainSettingModel.fixedWorkSetting.halfDayWorkSet.breakingTime(newVal);
                    }
                    if (self.mainSettingModel.workTimeSetting.isDiffTime()) {
                        self.mainSettingModel.diffWorkSetting.halfDayWorkSet.breakingTime(newVal);
                    }
                });

                self.selectedWorkTimeCode.subscribe(function(worktimeCode: string){
                    if (worktimeCode) {
                        self.loadWorktimeSetting(worktimeCode);
                        self.settingEnum.workTimeMethodSet = self.backupOptions;
                        // focus worktime atr
                        $('#search-daily-atr').focus();
                        //update value model tab 2
                        self.mainSettingModel.predetemineTimeSetting.prescribedTimezoneSetting.shiftOne.valueChangedNotifier.valueHasMutated();
                    }
                    else {
                        if (self.screenMode() != 2) {
                            self.enterNewMode();
                        }
                        else {
                            self.enterCopyMode();
                        }
                    }
                });

                $('#inp-worktimename').on("input", (event: any) => {
                  const val = event.target.value;
                  if (!nts.uk.text.isNullOrEmpty(val)) {
                    $('.workTimeAbName-input').ntsError('clear');
                    self.mainSettingModel.workTimeSetting.workTimeDisplayName.workTimeAbName(self.subString(val));
                  } else {
                    self.mainSettingModel.workTimeSetting.workTimeDisplayName.workTimeAbName("");
                  }
               });
            }

            /**
             * Initial computed value
             */
            initComputedValue(): void {
                let self = this;
                self.isNewMode = ko.computed(() => {
                    return self.screenMode() == ScreenMode.NEW;
                });
                self.isCopyMode = ko.computed(() => {
                    return self.screenMode() == ScreenMode.COPY;
                });
                self.isNewOrCopyMode = ko.computed(() => {
                    return self.isNewMode() || self.isCopyMode();
                });
                self.isUpdateMode = ko.computed(() => {
                    return self.screenMode() == ScreenMode.UPDATE;
                });
                self.isSimpleMode = ko.computed(() => {
                    return self.tabMode() == TabMode.SIMPLE;
                });
                self.isDetailMode = ko.computed(() => {
                    return self.tabMode() == TabMode.DETAIL;
                });
            }
            
            /**
            * find data WorkTypeLanguage
            */
            private findWorkTimeLanguage(): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred<void>();

                service.findByLangId(self.langId()).done((data) => {
                    self.lstWorkTimeLanguage(data);
                    // set property nameNotJP of workTimeSettings
                    _.each(self.workTimeSettings(), wtSet => {
                        let wtLang = _.find(data, ["workTimeCode", wtSet.worktimeCode]);
                        if (wtLang) {
                            wtSet.nameNotJP = wtLang.name;
                        }
                    });
                    
                    $("#single-list").igGrid("option", "width", "400px");
                    $("#screen-content-left").css('width', '480');
                    let cols = $("#single-list").igGrid("option", "columns");

                    if ($("#single-list").igGrid("option", "columns").length == 3) {
                        //add columns otherLanguageName   
                        let newColumn = { headerText: nts.uk.resource.getText('KMK003_313'), key: 'nameNotJP', width: 100, formatter: _.escape };
                        cols.splice(2, 0, newColumn);
                        $("#single-list").igGrid("option", "columns", cols);
                    }

                    self.selectedWorkTimeCode.valueHasMutated();
                    
                    dfd.resolve();
                }).fail(() => {
                    dfd.reject();
                });

                return dfd.promise();
            }
            
            /**
             * insert/update name and abbreviationName to WorkTimeLanguage
             */
            private insertWorkTimeLanguage(language: string): void {
                let self = this,
                    dfd = $.Deferred();

                let obj = {
                    langId: language,
                    name: self.mainSettingModel.workTimeSetting.workTimeDisplayName.workTimeName(),
                    abName: self.mainSettingModel.workTimeSetting.workTimeDisplayName.workTimeAbName(),
                    workTimeCode: self.selectedWorkTimeCode()
                };

                service.insertWorkTimeLang(obj).done(() => {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    // reload
                    self.reloadAfterSave();
                    dfd.resolve();
                }).fail(() => {
                    dfd.reject();
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
                
                dfd.promise();
            }

            /**
             * Bind workTimeSetting loader function
             */
            private bindFunction(): void {
                let self = this;
                self.workTimeSettingLoader.loadListWorktime = self.loadListWorktime.bind(self);
            }

            /**
             * Load list work time
             */
            private loadListWorktime(selectedCode?: string, selectedIndex?: number): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                // block ui.
                _.defer(() => nts.uk.ui.block.invisible());

                // call service get data
                service.findWithCondition(self.workTimeSettingLoader.getCondition()).done(data => {
                    // sort by work time code
                    data = _.sortBy(data, item => item.worktimeCode);
                    self.workTimeSettings(data); 
                    if(self.langId() != 'ja'){
                        self.findWorkTimeLanguage();
                    }             

                    // enter update mode if has data
                    if (data && data.length > 0) {
                        let found = _.find(self.workTimeSettings(), worktime => worktime.worktimeCode == selectedCode);
                        // select first item
                        if (!found && !selectedIndex) {
                            self.selectedWorkTimeCode(data[0].worktimeCode);
                        }

                        // select item by selected code
                        if (found) {
                            self.selectedWorkTimeCode(selectedCode);
                        }

                        // Select worktime by index
                        if (selectedIndex) {
                            self.selectWorktimeByIndex(selectedIndex);
                        }
                        self.settingEnum.workTimeMethodSet = self.backupOptions;
                    }
                    else {
                        // enter new mode
                        self.enterNewMode();
                    }
                    dfd.resolve();
                }).always(() => _.defer(() => nts.uk.ui.block.clear()));
                return dfd.promise();
            }

            /**
             * Select worktime by index
             */
            private selectWorktimeByIndex(selectedIndex: number): void {
                let self = this;
                let lastIndexOfCurrentList = self.workTimeSettings().length - 1;

                // select last item
                if (selectedIndex > lastIndexOfCurrentList) {
                    self.selectedWorkTimeCode(self.workTimeSettings()[lastIndexOfCurrentList].worktimeCode);
                } else {
                    // select item by index
                    self.selectedWorkTimeCode(self.workTimeSettings()[selectedIndex].worktimeCode);
                }
            }
                        
            //get all enums
            private getAllEnums(): JQueryPromise<void> {
                let self = this,
                    dfd = $.Deferred<void>();
                
                service.getEnumWorktimeSeting().done(function(setting: any) {
                    self.settingEnum = setting;
                    self.workTimeSettingLoader.setEnums(setting);
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
             * Set flex options visibility
             */
            private setFlexOptionVisibility(): void {
                let self = this;
                if (!self.flexWorkManaging) {
                    self.settingEnum.workTimeDailyAtr = _.filter(self.settingEnum.workTimeDailyAtr, item => item.fieldName != 'FLEX_WORK');
                    self.workTimeSettingLoader.workTimeAtrEnums = _.filter(self.workTimeSettingLoader.workTimeAtrEnums, item => item.fieldName != 'FLEX_WORK');
                }
            }

            /**
             * Load work time setting detail
             */
            private loadWorktimeSetting(worktimeCode: string): JQueryPromise<void> {
                if (worktimeCode) {
                    let self = this;
                    let dfd = $.Deferred<void>();
                    // clear all errors
                    self.clearAllError();
                    self.isLoading(false);
                    // block ui.
                    _.defer(() => nts.uk.ui.block.invisible());

                    service.findWorktimeSetingInfoByCode(worktimeCode).done(worktimeSettingInfo => {
                        //check ootsuka mode
                        self.otsukaMode(worktimeSettingInfo.modeOtsuka);
                        _.find(self.tabs(),['id', TabID.TAB17]).setVisible(self.otsukaMode());
                        // search workTimeLanguage
                        let workTimeLanguage = _.find(self.lstWorkTimeLanguage(), ["workTimeCode", self.selectedWorkTimeCode()]); 
                        // update mainSettingModel data
                        self.mainSettingModel.updateData(worktimeSettingInfo, worktimeCode, self.langId(), workTimeLanguage).done(()=>{
                            self.isLoading(false);
                            self.isLoading(true);
                            //convert 
                            self.backupCommonSetting = self.mainSettingModel.commonSetting.toDto();
                        });
                        self.mainSettingModel.isChangeItemTable.valueHasMutated();
                        
                        // enter update mode
                        self.enterUpdateMode();
                        dfd.resolve();
                    }).fail((error) => {
                        if (error) {
                            if (error.messageId == 'Msg_2182') {
                                if (self.workTimeSettings()[0].worktimeCode == worktimeCode) {
                                    nts.uk.ui.dialog.info({ messageId: error.messageId }).then(() => self.enterNewMode());
                                    // self.screenMode(ScreenMode.NEW);
                                } else {
                                    nts.uk.ui.dialog.info({ messageId: error.messageId }).then(() => location.reload());
                                    // location.reload();
                                }
                            } else {
                                nts.uk.ui.dialog.info({ messageId: error.messageId });
                            }
                        }
                    })
                    .always(() => _.defer(() => nts.uk.ui.block.clear()));
                    return dfd.promise();
                }
            }
            
            /**
             * Change tab mode
             */
            private changeTabMode(isDetail: boolean): void {              
                let _self = this;
                _self.clearAllError();
                if (isDetail) {
                    _.forEach(_self.tabs(), tab => tab.setVisible(true));
                } else {
                    let simpleTabsId: string[] = [TabID.TAB1, TabID.TAB2, TabID.TAB3, TabID.TAB4, TabID.TAB5, TabID.TAB6, TabID.TAB7, TabID.TAB9, TabID.TAB10, TabID.TAB11, TabID.TAB12, TabID.TAB17];
                    _.forEach(_self.tabs(), tab => {
                        if (_.findIndex(simpleTabsId, id => tab.id === id) === -1) {
                            tab.setVisible(false);
                        } else {
                            tab.setVisible(true);
                        }                        
                    });
                }
                // set visible tab 17 by otsuka mode
                _.find(_self.tabs(),['id', TabID.TAB17]).setVisible(_self.otsukaMode());

                if (_self.mainSettingModel.workTimeSetting.workTimeDivision.workTimeDailyAtr() === WorkTimeDailyAtr.FLEX_WORK ){
                    $($('.tabs-list')[0]).find('label:nth-child(2)').find('span').html(nts.uk.resource.getText('KMK003_317'));
                    $('#tab-panel > .tabs-content').removeClass('left-122');
                    $('#tab-panel > .tabs-content').addClass('left-178');
                } else{
                    $($('.tabs-list')[0]).find('label:nth-child(2)').find('span').html(nts.uk.resource.getText('KMK003_18'));
                    $('#tab-panel > .tabs-content').removeClass('left-178');
                    $('#tab-panel > .tabs-content').addClass('left-122');
                }
            }

            /**
             * Validate all input
             */
            private validateInput(): void {
                let self = this;
                let commonDayoff = nts.uk.util.isNullOrEmpty(self.backupCommonSetting)?null : self.backupCommonSetting.subHolTimeSet[0].subHolTimeSet;
                let commonOvertime = nts.uk.util.isNullOrEmpty(self.backupCommonSetting)?null : self.backupCommonSetting.subHolTimeSet[1].subHolTimeSet;
                self.clearAllError();
                $('.nts-editor').each((index, element) => {
                    if (!element.id) {
                        element.id = nts.uk.util.randomId();
                    } 
                    
                    $('#' + element.id).ntsEditor('validate');
                })
                $('.time-range-editor').each((index, element) => {
                    $('#' + element.id).validateTimeRange();
                });
                //validate disabled item tab 1
                self.validatetab1();
                
                //validate disabled item tab 7
                self.validatetab7();
                
                //validate disabled item tab 11
                if (!nts.uk.util.isNullOrEmpty(self.backupCommonSetting)) {
                    if (self.mainSettingModel.tabMode() == TabMode.DETAIL) {
                        self.validateTab11(commonDayoff, commonOvertime);
                    }
                    else {
                        self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.certainTime(commonDayoff.certainTime);
                        self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.certainTime(commonOvertime.certainTime);
                        self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.designatedTime.oneDayTime(commonOvertime.designatedTime.oneDayTime);
                        self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.designatedTime.halfDayTime(commonOvertime.designatedTime.halfDayTime);
                    }
                }
            }
            
            private validatetab1() {
                let self = this;
                if (self.mainSettingModel.flexWorkSetting.coreTimeSetting.timesheet() == 0) {
                    //assign value avoid exception
                    self.mainSettingModel.flexWorkSetting.coreTimeSetting.coreTimeSheet.startTime(0);
                    self.mainSettingModel.flexWorkSetting.coreTimeSetting.coreTimeSheet.endTime(0);
                    $('#coreTimeStart').ntsError('clear');
                    $('#coreTimeEnd').ntsError('clear');
                }
                
                if (!self.mainSettingModel.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.useAtr())
                {
                    $('#shiftTwoStart').ntsError('clear');
                    $('#shiftTwoEnd').ntsError('clear');
                    self.mainSettingModel.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.start(null);
                    self.mainSettingModel.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.end(null);
                }
            }
            
            private validatetab7()
            {
                let self = this;
                if (!self.mainSettingModel.flowWorkSetting.offdayWorkTimezone.restTimeZone.flowRestTimezone.useHereAfterRestSet()) {
                    $('#nts-fix-table-a7-flow-notuse-2').find('.nts-input').ntsError('clear');
                }
                if (!self.mainSettingModel.flowWorkSetting.offdayWorkTimezone.restTimeZone.flowRestTimezone.useHereAfterRestSet()) {
                    $('#nts-fix-table-a7-flex-notuse-2').find('.nts-input').ntsError('clear');
                }
            }
            private validateTab11(commonDayoff: SubHolTransferSetDto,commonOvertime: SubHolTransferSetDto) {
                let self = this;
                if (self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.useDivision()) {
                    if (self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.subHolTransferSetAtr() == 0) {
                        //一定時間
                        $('#certainDayTimeHol').ntsError('clear');
                        self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.certainTime(commonDayoff.certainTime);
                    }
                    else {//指定時間
                        self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.designatedTime.oneDayTime(commonDayoff.designatedTime.oneDayTime);
                        self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.designatedTime.halfDayTime(commonDayoff.designatedTime.halfDayTime);
                        $('.oneDayTimeHol').ntsError('clear');
                        $('#haflDayTimeHol').ntsError('clear');
                    }
                }
                else {
                    $('#certainDayTimeHol').ntsError('clear');
                    $('.oneDayTimeHol').ntsError('clear');
                    $('#haflDayTimeHol').ntsError('clear');
                    self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.certainTime(commonDayoff.certainTime);
                    self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.designatedTime.oneDayTime(commonDayoff.designatedTime.oneDayTime);
                    self.mainSettingModel.commonSetting.getWorkDayOffTimeSet().subHolTimeSet.designatedTime.halfDayTime(commonDayoff.designatedTime.halfDayTime);
                }
                if (self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.useDivision()) {
                    if (self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.subHolTransferSetAtr() == 0) {
                        //一定時間
                        self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.certainTime(commonOvertime.certainTime);
                        $('#certainDayTimeOT').ntsError('clear');
                    }
                    else {//指定時間
                        self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.designatedTime.oneDayTime(commonOvertime.designatedTime.oneDayTime);
                        self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.designatedTime.halfDayTime(commonOvertime.designatedTime.halfDayTime);
                        $('#haflDayTimeOT').ntsError('clear');
                        $('#oneDayTimeOT').ntsError('clear');
                    }
                }
                else {
                    $('#certainDayTimeOT').ntsError('clear');
                    $('#haflDayTimeOT').ntsError('clear');
                    $('#oneDayTimeOT').ntsError('clear');
                    self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.certainTime(commonOvertime.certainTime);
                    self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.designatedTime.oneDayTime(commonOvertime.designatedTime.oneDayTime);
                    self.mainSettingModel.commonSetting.getOverTimeSet().subHolTimeSet.designatedTime.halfDayTime(commonOvertime.designatedTime.halfDayTime);
                }
                
                // Validate Msg_770
                let shiftTwo: TimezoneModel = self.mainSettingModel.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo;
                if (shiftTwo.useAtr() && (shiftTwo.start() >= shiftTwo.end())) {
                    $('#shiftTwoStart').ntsError('set', {messageId:'Msg_770',messageParams:[nts.uk.resource.getText('KMK003_216')]});
                }
            }
            
            //save worktime data
            public save() {
                let self = this;
                // re validate
                self.validateInput();

                // stop function if has error.
                if ($('.nts-editor').ntsError('hasError') || $('.time-range-editor').ntsError('hasError')) {
                    return;
                }
                
                if (self.langId() == 'ja') {
                    self.isClickSave(true);
                    //for dialog F mode simple
                    self.bindFDialogSimpleMode();
                    
                    self.mainSettingModel.save()
                        .done(() => {
                            // recheck abolish condition of list worktime
                            // self.workTimeSettingLoader.isAbolish(self.mainSettingModel.workTimeSetting.isAbolish());
                            self.workTimeSettingLoader.isAbolish(true);
                            // reload
                            self.reloadAfterSave();
                            self.isClickSave(false);
                            self.loadWorktimeSetting(self.selectedWorkTimeCode());
                        }).fail((err) => {
                            self.isClickSave(false);
                            let errors = _.uniqBy(err.errors, (v: any) => {
                                let key = v.messageId;
                                for (let param of v.parameterIds) {
                                    key = key + ' ' + param;
                                }
                                return key;
                            });

                            // close current error dialog
                            const buttonCloseDialog = $('#functions-area-bottom>.ntsClose');
                            if (!nts.uk.util.isNullOrEmpty(buttonCloseDialog)) {
                                buttonCloseDialog.click();
                            }

                            err.errors = errors;
                            self.showMessageError(err);
                        });
                    
                } else {
                    self.insertWorkTimeLanguage(self.langId());
                }
            }
            
            /**
             * showMessageError
             */
            public showMessageError(res: any) {
                let dfd = $.Deferred<any>();
                
                // check error business exception
                if (!res.businessException) {
                    return;
                }
                
                
                // show error message
                if (Array.isArray(res.errors) && !_.isEmpty(res.errors)) {
                    nts.uk.ui.dialog.bundledErrors(res);
                } else {
                    if (res.messageId === "Msg_2143") {
                        nts.uk.ui.dialog.info({ messageId: res.messageId });
                    } else {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds });
                    }
                }
            }

            /**
             * Reload worktime list after save
             */
            public reloadAfterSave(): void {
                let self = this;
                let loader = self.workTimeSettingLoader;
                let wts = self.mainSettingModel.workTimeSetting;
                let leftAtr = loader.workTimeDivision.workTimeDailyAtr;
                let leftMethod = loader.workTimeDivision.workTimeMethodSet;
                let rightAtr = wts.workTimeDivision.workTimeDailyAtr;
                let rightMethod = wts.workTimeDivision.workTimeMethodSet;

                let isSameWorkDivision = leftAtr() == rightAtr() && leftMethod() == rightMethod();

                if (leftAtr() == EnumWorkForm.REGULAR && rightAtr() == EnumWorkForm.REGULAR) {
                    if (leftMethod() == SettingMethod.ALL) {
                        isSameWorkDivision = true;
                    }
                }
                
                if (leftAtr() == EnumWorkForm.FLEX && rightAtr() == EnumWorkForm.FLEX) {
                    isSameWorkDivision = true;
                }
                
                // reload list work time
                if (loader.isAllWorkAtr() || isSameWorkDivision) {
                    _.defer(() => self.loadListWorktime(self.mainSettingModel.workTimeSetting.worktimeCode()));
                } else {
                    loader.selectAll();
                }
            }

            /**
             * Enter new mode
             */
            public enterNewMode(): void {
                let self = this;
                self.clearAllError();
                self.isLoading(false);
                // clear all errors

                // reset data
                self.mainSettingModel.resetData();
                self.settingEnum.workTimeMethodSet = _.filter(self.settingEnum.workTimeMethodSet, item => item.fieldName != 'DIFFTIME_WORK');
                const predTimeSetting = self.mainSettingModel.predetemineTimeSetting;
                self.mainSettingModel.updatePeriod(predTimeSetting.startDateClock(), predTimeSetting.rangeTimeDay());
                // set screen mode
                self.screenMode(ScreenMode.NEW);

                // set simple mode
                self.enterSimpleMode();
                
                // deselect current worktimecode
                self.selectedWorkTimeCode('');

                // focus worktimecode
                $('#inp-worktimecode').focus();
                
                self.isLoading(true);
                
                //fix ST #5
                self.selectedTab('tab-1');
            }

            /**
             * Enter simple mode
             */
            public enterSimpleMode(): void {
                let self = this;
                self.tabMode(TabMode.SIMPLE);
            }

            /**
             * Enter detail mode
             */
            public enterDetailMode(): void {
                let self = this;
                self.tabMode(TabMode.DETAIL);
            }

            /**
             * Enter copy mode
             */
            public enterCopyMode(): void {
                let self = this;
                
                self.settingEnum.workTimeMethodSet = _.filter(self.settingEnum.workTimeMethodSet, item => item.fieldName != 'DIFFTIME_WORK');
                // set screen mode
                self.screenMode(ScreenMode.COPY);
                // clear current worktimecode
                self.mainSettingModel.workTimeSetting.worktimeCode('');

                // deselect current worktimecode
                self.selectedWorkTimeCode('');
                
                self.mainSettingModel.workTimeSetting.workTimeDisplayName.workTimeName('');
                self.mainSettingModel.workTimeSetting.workTimeDisplayName.workTimeAbName('');
                self.mainSettingModel.workTimeSetting.memo('');
                self.mainSettingModel.workTimeSetting.note('');
                //clear isAbolish
                self.mainSettingModel.workTimeSetting.isAbolish(false);

                // do interlock if simple mode
                if (self.tabMode() === TabMode.SIMPLE) {
                    self.mainSettingModel.isInterlockDialogJ(true);
                    self.mainSettingModel.updateStampValue();
                }
                self.mainSettingModel.predetemineTimeSetting.predTime.addTime.oneDay(self.mainSettingModel.predetemineTimeSetting.predTime.predTime.oneDay());
                self.mainSettingModel.predetemineTimeSetting.predTime.addTime.oneDay(self.mainSettingModel.predetemineTimeSetting.predTime.predTime.oneDay());
                self.mainSettingModel.predetemineTimeSetting.predTime.addTime.oneDay(self.mainSettingModel.predetemineTimeSetting.predTime.predTime.oneDay());
                // focus worktimecode
                $('#inp-worktimecode').focus();
            }

            /**
             * Clear all errors
             */
            public clearAllError(): void {
                nts.uk.ui.errors.clearAll();
            }

            /**
             * Enter update mode
             */
            public enterUpdateMode(): void {
                let self = this;
                // set screen mode
                self.screenMode(ScreenMode.UPDATE);

                // set detail mode
                //self.enterDetailMode();
            }

             /**
             * remove selected worktime
             */
            public removeWorkTime(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                // check selected code
                if (!self.selectedWorkTimeCode()) {
                    return;
                }
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    // block ui.
                    _.defer(() => nts.uk.ui.block.invisible());

                    // get selected code
                    let selectedCode = self.selectedWorkTimeCode();

                    service.removeWorkTime(selectedCode, self.langId()).done(function() {
                        let currentIndex = _.findIndex(self.workTimeSettings(), item => item.worktimeCode === selectedCode);
                        nts.uk.ui.dialog.info({ messageId: 'Msg_16' })
                            .then(() => self.loadListWorktime(null, currentIndex)); // reload list work time

                        // resolve
                        dfd.resolve();
                    }).fail(function(error) {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => _.defer(() => nts.uk.ui.block.clear()));
					
					let param = {
						action : 0,
						workingTimesheetCode: selectedCode,
						bonusPaySettingCode: ""
					}
					service.saveBonusPaySetting(param) 
						.done(() => dfd.resolve())
	                    .fail(err => dfd.reject(err));
                	});
                return dfd.promise();
            }
           
            private bindFDialogSimpleMode(): void {
                let self = this;
                if (self.tabMode() == TabMode.SIMPLE) {
                    //set main screen to dialog  
                    self.mainSettingModel.predetemineTimeSetting.predTime.addTime.oneDay(self.mainSettingModel.predetemineTimeSetting.predTime.predTime.oneDay());
                    self.mainSettingModel.predetemineTimeSetting.predTime.addTime.morning(self.mainSettingModel.predetemineTimeSetting.predTime.predTime.morning());
                    self.mainSettingModel.predetemineTimeSetting.predTime.addTime.afternoon(self.mainSettingModel.predetemineTimeSetting.predTime.predTime.afternoon());
                }
            }
            
            private exportExcel(): void {
                let self = this;
                nts.uk.ui.block.grayout();
                service.saveAsExcel(self.langId()).done(function() {
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
            }
            //end view model

            private subString(value: string): string {
              const length = __viewContext.primitiveValueConstraints.WorkTimeAbName.maxLength;
              let maxCountHalfSizeCharacter = length;
              let valueTemp = "";
              const valueSplit = value.split("");
              valueSplit.forEach((character: string) => {
                  maxCountHalfSizeCharacter -= nts.uk.text.countHalf(character);
                  if (maxCountHalfSizeCharacter >= 0) {
                      valueTemp += character;
                  }
              });
              return valueTemp;
          }
            
        }

        /**
         * Tab Item
         */
        export class TabItem { 
            id: string;
            title: string;
            content: string; 
            enable: KnockoutObservable<boolean>; 
            visible: KnockoutObservable<boolean>; 
            
            constructor(id: string, title: string, content: string, enable: boolean, visible: boolean) {
                this.id = id;
                this.title = title;
                this.content = content;
                this.enable = ko.observable(enable);
                this.visible = ko.observable(visible);
            }
            
            public setVisible(visible: boolean): void {
                this.visible(visible);
            }

            public setTitle(title: string) : void {
                this.title = title;
            }
        }
        
        /**
         * Store all Setting Model, use for tab data binding
         */
        export class MainSettingModel {
            workTimeSetting: WorkTimeSettingModel;
            predetemineTimeSetting: PredetemineTimeSettingModel;
            manageEntryExit: ManageEntryExitModel;
            
            //dientx add for common
            commonSetting: WorkTimezoneCommonSetModel;
                        
            fixedWorkSetting: FixedWorkSettingModel;
            flowWorkSetting: FlowWorkSettingModel;
            diffWorkSetting: DiffTimeWorkSettingModel;
            flexWorkSetting: FlexWorkSettingModel;
            
            isChangeItemTable: KnockoutObservable<boolean>;
            useHalfDayOptions: KnockoutObservable<any>
            useHalfDayWorking: KnockoutObservable<boolean>;
            useHalfDayOverTime: KnockoutObservable<boolean>;
            useHalfDayBreak: KnockoutObservable<boolean>;
            tabMode: KnockoutObservable<number>;
            addMode: KnockoutComputed<boolean>;
            isNewMode: KnockoutObservable<boolean>;
            
            // Interlock dialog J
            isInterlockDialogJ: KnockoutObservable<boolean>;
            
            constructor(tabMode: KnockoutObservable<number>, isNewOrCopyMode: KnockoutComputed<boolean>,
                        useHalfDayOptions: KnockoutObservable<any>,
                        halfDayWorking: KnockoutObservable<boolean>,
                        halfDayOverTime: KnockoutObservable<boolean>,
                        halfDayBreak: KnockoutObservable<boolean>,
                        isNewMode: KnockoutObservable<boolean>) {
                let self = this;
                self.isChangeItemTable = ko.observable(false);
                self.useHalfDayOptions = useHalfDayOptions;
                self.useHalfDayWorking = halfDayWorking;
                self.useHalfDayOverTime = halfDayOverTime;
                self.useHalfDayBreak = halfDayBreak;
                self.isInterlockDialogJ = ko.observable(true);
                self.tabMode = tabMode;
                
                self.addMode = isNewOrCopyMode;
                self.isNewMode = isNewMode;
                
                self.workTimeSetting = new WorkTimeSettingModel();
                self.manageEntryExit = new ManageEntryExitModel();
                self.predetemineTimeSetting = new PredetemineTimeSettingModel();
                self.commonSetting = new WorkTimezoneCommonSetModel();
                self.fixedWorkSetting = new FixedWorkSettingModel(self.tabMode);
                self.flowWorkSetting = new FlowWorkSettingModel();
                self.diffWorkSetting = new DiffTimeWorkSettingModel(self.tabMode);
                self.flexWorkSetting = new FlexWorkSettingModel(self.tabMode);

                self.initSubscriber();

            }

            private initSubscriber(): void {
                let self = this;
                const shiftOne = self.predetemineTimeSetting.prescribedTimezoneSetting.shiftOne;
                self.fixedWorkSetting.initSubscriberForTab2(shiftOne);
                self.flexWorkSetting.initSubscriberForTab2(shiftOne);
                self.diffWorkSetting.initSubscriberForTab2(shiftOne);

                self.workTimeSetting.worktimeCode.subscribe(worktimeCode => {
                    self.predetemineTimeSetting.workTimeCode(worktimeCode);
                    self.fixedWorkSetting.workTimeCode(worktimeCode);
                    self.flowWorkSetting.workingCode(worktimeCode);
                    self.diffWorkSetting.workTimeCode(worktimeCode);
                    self.flexWorkSetting.workTimeCode(worktimeCode);
                });
            }

            onSaveSuccess(dfd: JQueryDeferred<any>): void {
                nts.uk.ui.dialog.info({ messageId: 'Msg_15' });
                dfd.resolve();
            }

            save(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                // block ui.
                _.defer(() => nts.uk.ui.block.invisible());
                
                // do interlock if simple mode
                if (self.tabMode() === TabMode.SIMPLE) {
                    self.isInterlockDialogJ(true);
                    self.updateStampValue();
                }

				let param = {
					action : 1,
					workingTimesheetCode: "",
					bonusPaySettingCode: ""
				}

                if (self.workTimeSetting.isFlex()) {
                    service.saveFlexWorkSetting(self.toFlexCommand())
                        .done(() => self.onSaveSuccess(dfd))
                        .fail(err => dfd.reject(err))
                        .always(() => _.defer(() => nts.uk.ui.block.clear()));
					param.workingTimesheetCode = self.toFlexCommand().worktimeSetting.worktimeCode;
					param.bonusPaySettingCode = self.toFlexCommand().flexWorkSetting.commonSetting.raisingSalarySet;
                }
                if (self.workTimeSetting.isFixed()) {
                    service.saveFixedWorkSetting(self.toFixedCommand())
                        .done(() => self.onSaveSuccess(dfd))
                        .fail(err => dfd.reject(err))
                        .always(() => _.defer(() => nts.uk.ui.block.clear()));
					param.workingTimesheetCode = self.toFixedCommand().worktimeSetting.worktimeCode;
					param.bonusPaySettingCode = self.toFixedCommand().fixedWorkSetting.commonSetting.raisingSalarySet;
                }                
                if (self.workTimeSetting.isFlow()) {
                    service.saveFlowWorkSetting(self.toFlowCommand())
                        .done(() => self.onSaveSuccess(dfd))
                        .fail(err => dfd.reject(err))
                        .always(() => _.defer(() => nts.uk.ui.block.clear()));
					param.workingTimesheetCode = self.toFlowCommand().worktimeSetting.worktimeCode;
					param.bonusPaySettingCode = self.toFlowCommand().flowWorkSetting.commonSetting.raisingSalarySet;
                }

                if (self.workTimeSetting.isDiffTime()) {
                    service.saveDiffTimeWorkSetting(self.toDiffTimeCommand())
                        .done(() => self.onSaveSuccess(dfd))
                        .fail(err => dfd.reject(err))
                        .always(() => _.defer(() => nts.uk.ui.block.clear()));
					param.workingTimesheetCode = self.toDiffTimeCommand().worktimeSetting.worktimeCode;
					param.bonusPaySettingCode = self.toDiffTimeCommand().diffTimeWorkSetting.commonSet.raisingSalarySet;
                }
                service.saveBonusPaySetting(param) 
						.done(() => dfd.resolve())
	                    .fail(err => dfd.reject(err));
                
				return dfd.promise();
            }

            //auto generate data

            //半日の勤務時間帯を作成する
            autoCreateHalfDayWT(timeZones: any): any {
                let _self = this;
                let workTimezones = timeZones;
                workTimezones.sort((w1, w2) => w1.timezone.start() - w2.timezone.start());
                let presSetting = _self.predetemineTimeSetting.prescribedTimezoneSetting;
                let morningEnd = presSetting.morningEndTime();
                let afterStart = presSetting.afternoonStartTime();

                let morningTimes: any = [];
                let afternoonTimes:any = [];

                let morningNo = 1;
                let afternoonNo = 1;
                //morning
                workTimezones
                    .filter(w => w.timezone.start() <= morningEnd)
                    .map(w => {
                        if (w.timezone.end() <= morningEnd) {
                            let dto = w.toDto();
                            dto.employmentTimeFrameNo = morningNo++;
                            morningTimes.push(dto);
                        } else { //split time zone
                            if (w.timezone.start() <= morningEnd) {
                                let dto = w.toDto();
                                dto.employmentTimeFrameNo = morningNo++;
                                dto.timezone.end = morningEnd;
                                morningTimes.push(dto);
                            }

                            if (w.timezone.end() >= afterStart) {
                                let dto = w.toDto();
                                dto.employmentTimeFrameNo = afternoonNo++;
                                dto.timezone.start = afterStart;
                                afternoonTimes.push(dto);
                            }
                        }
                    })

                //afternoon
                workTimezones
                    .filter(w => w.timezone.start() >= afterStart)
                    .map(w => {
                        let dto = w.toDto();
                        dto.employmentTimeFrameNo = afternoonNo++;
                        afternoonTimes.push(dto);
                    })

                return {
                    morning: morningTimes,
                    afternoon: afternoonTimes
                }
            }

            //半日の残業時間帯を作成する
            autoCreateHalfDayOT(timeZones: any): any {
                let _self = this;
                let OTTimezones = timeZones;
                OTTimezones.sort((w1, w2) => w1.timezone.start() - w2.timezone.start());
                let presSetting = _self.predetemineTimeSetting.prescribedTimezoneSetting;
                let morningEnd = presSetting.morningEndTime();
                let afterStart = presSetting.afternoonStartTime();

                let morningTimes: any = [];
                let afternoonTimes:any = [];

                let morningNo = 1;
                let afternoonNo = 1;
                //morning
                OTTimezones
                    .filter(w => w.timezone.start() <= morningEnd)
                    .map(w => {
                        if (w.timezone.end() <= morningEnd) {
                            let dto = w.toDto();
                            dto.workTimezoneNo = morningNo++;
                            morningTimes.push(dto);
                        } else { //split time zone
                            if (w.timezone.start() < morningEnd) {
                                let dto = w.toDto();
                                dto.workTimezoneNo = morningNo++;
                                dto.timezone.end = morningEnd;
                                morningTimes.push(dto);
                            }
                            // if (w.timezone.end() > afterStart) {
                            //     let dto = w.toDto();
                            //     dto.workTimezoneNo = afternoonNo++;
                            //     dto.timezone.start = afterStart;
                            //     afternoonTimes.push(dto);
                            // }
                        }
                    })

                //afternoon
                OTTimezones
                    .filter(w => w.timezone.start() >= afterStart)
                    .map(w => {
                        let dto = w.toDto();
                        dto.workTimezoneNo = afternoonNo++;
                        afternoonTimes.push(dto);
                    })

                return {
                    morning: morningTimes,
                    afternoon: afternoonTimes
                }
            }

            //半日の休憩時間帯を作成する
            autoCreateHalfDayBreak(timeZones: any): any {
                let self = this;
                let timezones = timeZones;
                timezones.sort((w1, w2) => w1.start() - w2.start());
                let presSetting = self.predetemineTimeSetting.prescribedTimezoneSetting;
                let morningEnd = presSetting.morningEndTime();
                let afterStart = presSetting.afternoonStartTime();

                let morningTimes: any = [];
                let afternoonTimes:any = [];

                //morning
                timezones
                    .filter(w => w.start() <= morningEnd)
                    .map(w => {
                        if (w.end() <= morningEnd) {
                            let dto = w.toDto();
                            morningTimes.push(dto);
                        } else { //split time zone
                            if (w.start() < morningEnd) {
                                let dto = w.toDto();
                                dto.end = morningEnd;
                                morningTimes.push(dto);
                            }
                            if (w.end() > afterStart) {
                                let dto = w.toDto();
                                dto.start = afterStart;
                                afternoonTimes.push(dto);
                            }
                        }
                    })

                //afternoon
                timezones
                    .filter(w => w.start() >= afterStart)
                    .map(w => {
                        let dto = w.toDto();
                        afternoonTimes.push(dto);
                    })

                return {
                    morning: morningTimes,
                    afternoon: afternoonTimes
                }
            }

            //半日の休憩時間帯を作成する Flex
            autoCreateHalfDayRestFlex(): any {
                let self = this;
                let timezones = self.flexWorkSetting.getHDWtzOneday().restTimezone.flowRestTimezone.flowRestSets();
                timezones.sort((w1, w2) => w1.flowPassageTime() - w2.flowPassageTime());
                let presSetting = self.predetemineTimeSetting.prescribedTimezoneSetting;
                let morningEnd = presSetting.morningEndTime();
                let afterStart = presSetting.afternoonStartTime();

                let morningTimes: any = [];
                let afternoonTimes:any = [];

                //morning
                timezones
                    .filter(w => w.flowPassageTime() <= morningEnd)
                    .map(w => {
                        if (w.flowRestTime() <= morningEnd) {
                            let dto = w.toDto();
                            morningTimes.push(dto);
                        } else { //split time zone
                            if (w.flowPassageTime() < morningEnd) {
                                let dto = w.toDto();
                                dto.flowRestTime = morningEnd;
                                morningTimes.push(dto);
                            }
                            if (w.flowRestTime() > afterStart) {
                                let dto = w.toDto();
                                dto.flowPassageTime = afterStart;
                                afternoonTimes.push(dto);
                            }
                        }
                    })

                //afternoon
                timezones
                    .filter(w => w.flowRestTime() >= afterStart)
                    .map(w => {
                        let dto = w.toDto();
                        afternoonTimes.push(dto);
                    })

                return {
                    morning: morningTimes,
                    afternoon: afternoonTimes
                }
            }

            /**
             * Collect fixed data and convert to command dto
             */
            toFixedCommand(): FixedWorkSettingSaveCommand {
                let _self = this;
                let command: FixedWorkSettingSaveCommand = {
                    addMode: _self.addMode(),
                    predseting: _self.predetemineTimeSetting.toDto(),
                    worktimeSetting: _self.workTimeSetting.toDto(),
                    fixedWorkSetting: _self.fixedWorkSetting.toDto(_self.commonSetting),
                    screenMode: _self.tabMode()
                };

                if (_self.tabMode() === TabMode.SIMPLE) {
                    let temp: EmTimeZoneSetModel = new EmTimeZoneSetModel();
                    temp.employmentTimeFrameNo(1);
                    temp.timezone.start(_self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezoneSimpleMode()[0].timeRange().startTime);
                    temp.timezone.end(_self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezoneSimpleMode()[0].timeRange().endTime);
                    temp.timezone.rounding.rounding(_self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezoneSimpleMode()[0].rounding());
                    temp.timezone.rounding.roundingTime(_self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezoneSimpleMode()[0].roundingTime());
                    _self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezone([temp]);
                }

                let workTimes = _self.autoCreateHalfDayWT(_self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezone());

                command.fixedWorkSetting.lstHalfDayWorkTimezone[1].workTimezone.lstWorkingTimezone = workTimes.morning;
                command.fixedWorkSetting.lstHalfDayWorkTimezone[2].workTimezone.lstWorkingTimezone = workTimes.afternoon;

                if (!_self.useHalfDayOverTime()) {
                    let OTTimes = _self.autoCreateHalfDayOT(_self.fixedWorkSetting.getHDWtzOneday().workTimezone.lstOTTimezone());

                    command.fixedWorkSetting.lstHalfDayWorkTimezone[1].workTimezone.lstOTTimezone = OTTimes.morning;
                    command.fixedWorkSetting.lstHalfDayWorkTimezone[2].workTimezone.lstOTTimezone = OTTimes.afternoon;
                }

                /*if (_self.isNewMode() && !_self.useHalfDayBreak()) {
                    let restTimes = _self.autoCreateHalfDayBreak(_self.fixedWorkSetting.getHDWtzOneday().restTimezone.timezones());

                    command.fixedWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.timezones = restTimes.morning;
                    command.fixedWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.timezones = restTimes.afternoon;
                }*/

				if (!_self.useHalfDayBreak()){
					let amTimes : any = [], pmTimes : any = [];
					
					_.forEach(command.fixedWorkSetting.lstHalfDayWorkTimezone[0].restTimezone.timezones, (z : any) => {
						let checkAdd = 0;
						if (_.inRange(z.start, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1) &&
						_.inRange(z.end, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1)){
							checkAdd = 1;
							amTimes.push({
								start: z.start, 
								end: z.end
							})
						}
						
						if (_.inRange(z.start, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1) &&
						_.inRange(z.end, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1)){
							checkAdd = 2;
							pmTimes.push({
								start: z.start, 
								end: z.end
							})
						}
						if (checkAdd == 0) {
							amTimes.push({
								start: z.start, 
								end: z.end
							})
							
							pmTimes.push({
								start: z.start, 
								end: z.end
							})
						}
					});
					
                    command.fixedWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.timezones = amTimes;
                    command.fixedWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.timezones = pmTimes;
				}

                return command;
            }

            toFlowCommand(): FlowWorkSettingSaveCommand {
                let _self = this;
                let command: FlowWorkSettingSaveCommand = {
                    addMode: _self.addMode(),
                    predseting: _self.predetemineTimeSetting.toDto(),
                    worktimeSetting: _self.workTimeSetting.toDto(),
                    flowWorkSetting: _self.flowWorkSetting.toDto(_self.commonSetting),
                    screenMode: _self.tabMode()
                };
                return command;  
            }
            
            /**
             * Collect flex data and convert to command dto
             */
            toFlexCommand(): FlexWorkSettingSaveCommand {
                let self = this;
                let command: FlexWorkSettingSaveCommand;
                const oneDayFlex = _.map(self.flexWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezone(),item=>item.toDto());
                const flexWorkSetting = self.flexWorkSetting.toDto(self.commonSetting);
                flexWorkSetting.lstHalfDayWorkTimezone[0].workTimezone.lstWorkingTimezone = oneDayFlex;
                command = {
                    addMode: self.addMode(),
                    screenMode: self.tabMode(),
                    flexWorkSetting: flexWorkSetting,
                    predseting: self.predetemineTimeSetting.toDto(),
                    worktimeSetting: self.workTimeSetting.toDto(),
                };

                if (!self.useHalfDayWorking()) {
                    //auto generate data for lstTimezone morning and afternoon in a2 if it was hidden
                    let times = self.autoCreateHalfDayWT(self.flexWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezone());

                    command.flexWorkSetting.lstHalfDayWorkTimezone[1].workTimezone.lstWorkingTimezone =  times.morning;
                    command.flexWorkSetting.lstHalfDayWorkTimezone[2].workTimezone.lstWorkingTimezone =  times.afternoon;
                }

                if (!self.useHalfDayOverTime()) {
                    let OTTimes = self.autoCreateHalfDayOT(self.flexWorkSetting.getHDWtzOneday().workTimezone.lstOTTimezone());

                    command.flexWorkSetting.lstHalfDayWorkTimezone[1].workTimezone.lstOTTimezone = OTTimes.morning;
                    command.flexWorkSetting.lstHalfDayWorkTimezone[2].workTimezone.lstOTTimezone = OTTimes.afternoon;
                }

                if (self.isNewMode() && !self.useHalfDayBreak()) {
                    let breakTimes = self.autoCreateHalfDayBreak(self.flexWorkSetting.getHDWtzOneday().restTimezone.fixedRestTimezone.timezones());

                    command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.fixedRestTimezone.timezones = breakTimes.morning;
                    command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.fixedRestTimezone.timezones = breakTimes.afternoon;

                    /*let restTimeFlex = self.autoCreateHalfDayRestFlex();
                    command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.flowRestSets = restTimeFlex.morning;
                    command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.flowRestSets = restTimeFlex.afternoon;*/
                }
				// ver24.2 start
				// A22_23 休憩時間を固定にする (休憩時間帯を固定にする)
				let fixRestTime = command.flexWorkSetting.lstHalfDayWorkTimezone[0].restTimezone.fixRestTime;
				// A19_3_2 半日勤務の時間帯の設定＝するの場合
				if (self.useHalfDayBreak()) {
					if (fixRestTime == false) {
						command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.fixedRestTimezone.timezones = [];
						command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.fixedRestTimezone.timezones = [];
					}
				}
				// A19_3_2 半日勤務の時間帯の設定＝しないの場合
				if (!self.useHalfDayBreak()) {
					let amTimes : any = [], pmTimes : any = [];
					let workTimes = self.autoCreateHalfDayWT(self.flexWorkSetting.getHDWtzOneday().workTimezone.lstWorkingTimezone());
					let useHereAfterRestSet = command.flexWorkSetting.lstHalfDayWorkTimezone[0].restTimezone.flowRestTimezone.useHereAfterRestSet;
					if (fixRestTime == false) {
						_.forEach(command.flexWorkSetting.lstHalfDayWorkTimezone[0].restTimezone.flowRestTimezone.flowRestSets, (z : any) => {
							let checkAdd = 0;
							if (_.inRange(z.flowPassageTime, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1) &&
							_.inRange(z.flowRestTime, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1)){
								checkAdd = 1;
								amTimes.push({
									flowPassageTime: z.flowPassageTime, 
									flowRestTime: z.flowRestTime
								})
							}
							
							if (_.inRange(z.flowPassageTime, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1) &&
							_.inRange(z.flowRestTime, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1)){
								checkAdd = 2;
								pmTimes.push({
									flowPassageTime: z.flowPassageTime, 
									flowRestTime: z.flowRestTime
								})
							}
							
							if (checkAdd == 0){
								amTimes.push({
									flowPassageTime: z.flowPassageTime, 
									flowRestTime: z.flowRestTime
								})
								
								pmTimes.push({
									flowPassageTime: z.flowPassageTime, 
									flowRestTime: z.flowRestTime
								})
							}
						});
						command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.flowRestSets = amTimes;
						command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.useHereAfterRestSet = useHereAfterRestSet;
						
						command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.flowRestSets = pmTimes;
						command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.useHereAfterRestSet = useHereAfterRestSet;
						
						if (useHereAfterRestSet == true) {
							let z = command.flexWorkSetting.lstHalfDayWorkTimezone[0].restTimezone.flowRestTimezone.hereAfterRestSet
							
							if (_.inRange(z.flowPassageTime, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1) &&
							_.inRange(z.flowRestTime, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1)){
								command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.hereAfterRestSet = z;
							} else {
								command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.useHereAfterRestSet = false;
								command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.hereAfterRestSet = { flowPassageTime: 0 ,flowRestTime: 0}
							}
							
							if (_.inRange(z.flowPassageTime, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1) &&
							_.inRange(z.flowRestTime, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1)){
								command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.hereAfterRestSet = z;
							} else {
								command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.useHereAfterRestSet = false;
								command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.hereAfterRestSet = { flowPassageTime: 0 ,flowRestTime: 0}
							}
						} else {
							command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.flowRestTimezone.hereAfterRestSet = { flowPassageTime: 0 ,flowRestTime: 0}
							command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.flowRestTimezone.hereAfterRestSet = { flowPassageTime: 0 ,flowRestTime: 0}
						}
						command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.fixedRestTimezone.timezones = [];
						command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.fixedRestTimezone.timezones = [];
					} else {
						_.forEach(command.flexWorkSetting.lstHalfDayWorkTimezone[0].restTimezone.fixedRestTimezone.timezones, (z : any) => {
							let checkAdd = 0;
							if (_.inRange(z.start, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1) &&
							_.inRange(z.end, workTimes.morning[0].timezone.start - 0.1, workTimes.morning[0].timezone.end + 0.1)){
								checkAdd = 1;
								amTimes.push({
									start: z.start, 
									end: z.end
								})
							}
							
							if (_.inRange(z.start, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1) &&
							_.inRange(z.end, workTimes.afternoon[0].timezone.start - 0.1, workTimes.afternoon[0].timezone.end + 0.1)){
								checkAdd = 2;
								pmTimes.push({
									start: z.start, 
									end: z.end
								})
							}
							
							if (checkAdd == 0) {
								amTimes.push({
									start: z.start, 
									end: z.end
								})
								
								pmTimes.push({
									start: z.start, 
									end: z.end
								})
							}
						})
						
						command.flexWorkSetting.lstHalfDayWorkTimezone[1].restTimezone.fixedRestTimezone.timezones = amTimes;
						command.flexWorkSetting.lstHalfDayWorkTimezone[2].restTimezone.fixedRestTimezone.timezones = pmTimes;
					}
				}

                return command;
            }
            
            /**
             * Collect difftime data and convert to command dto
             */
            toDiffTimeCommand(): DiffTimeSettingSaveCommand {
                let self = this;
                let command: DiffTimeSettingSaveCommand;
                command = {
                    addMode: self.addMode(),
                    screenMode: self.tabMode(),
                    diffTimeWorkSetting: self.diffWorkSetting.toDto(self.commonSetting),
                    predseting: self.predetemineTimeSetting.toDto(),
                    worktimeSetting: self.workTimeSetting.toDto(),
                };
                return command;
            }

            updateData(worktimeSettingInfo: WorkTimeSettingInfoDto, worktimeCode : any, langId?: string, workTimeLanguage?: any): JQueryPromise<void> {
                let self = this, dfd = $.Deferred<void>();
                
                if(langId != 'ja'){
                    $('#inp-worktimename').focus();
                    // set other language for name and abbreviationName
                    worktimeSettingInfo.worktimeSetting.workTimeDisplayName.workTimeName = (workTimeLanguage ? workTimeLanguage.name : '');
                    worktimeSettingInfo.worktimeSetting.workTimeDisplayName.workTimeAbName = (workTimeLanguage ? workTimeLanguage.abbreviationName : '');
                }
                
                self.workTimeSetting.updateData(worktimeSettingInfo.worktimeSetting);
                self.predetemineTimeSetting.updateData(worktimeSettingInfo.predseting);    
                self.manageEntryExit.updateData(worktimeSettingInfo.manageEntryExit);                          
                self.tabMode(worktimeSettingInfo.displayMode.displayMode);
				nts.uk.at.view.kmk003.a10.service.getWTBPSetting(worktimeCode).done((data : any) => {
	                if (self.workTimeSetting.isFlex()) {
	                    self.flexWorkSetting.updateData(worktimeSettingInfo.flexWorkSetting);
						
						if (worktimeSettingInfo.flexWorkSetting != null && worktimeSettingInfo.flexWorkSetting.commonSetting != null)
						worktimeSettingInfo.flexWorkSetting.commonSetting.raisingSalarySet = _.isNil(data) ? "000" : data.bonusPaySettingCode;
						
	                    self.commonSetting.updateData(worktimeSettingInfo.flexWorkSetting.commonSetting);
	
	                    // set useHalfDay to mainScreen model
	                    self.useHalfDayWorking(worktimeSettingInfo.flexWorkSetting.useHalfDayShift.workingTimes);
	                    self.useHalfDayOverTime(worktimeSettingInfo.flexWorkSetting.useHalfDayShift.overTime);
	                    self.useHalfDayBreak(worktimeSettingInfo.flexWorkSetting.useHalfDayShift.breakTime);
	
	                    // reset data of other mode
	                    self.flowWorkSetting.resetData();
	                    self.diffWorkSetting.resetData();
	                    self.fixedWorkSetting.resetData();
	                }
	                if (self.workTimeSetting.isFlow()) {
	                    self.flowWorkSetting.updateData(worktimeSettingInfo.flowWorkSetting);
						
						if (worktimeSettingInfo.flowWorkSetting != null && worktimeSettingInfo.flowWorkSetting.commonSetting != null)
						worktimeSettingInfo.flowWorkSetting.commonSetting.raisingSalarySet = _.isNil(data) ? "000" : data.bonusPaySettingCode;
						
	                    self.commonSetting.updateData(worktimeSettingInfo.flowWorkSetting.commonSetting);
	
	                    // reset data of other mode
	                    self.flexWorkSetting.resetData();
	                    self.diffWorkSetting.resetData();
	                    self.fixedWorkSetting.resetData();
	                }
	                if (self.workTimeSetting.isFixed()) {
	                    self.fixedWorkSetting.updateData(worktimeSettingInfo.fixedWorkSetting);

						if (worktimeSettingInfo.fixedWorkSetting != null && worktimeSettingInfo.fixedWorkSetting.commonSetting != null)
						worktimeSettingInfo.fixedWorkSetting.commonSetting.raisingSalarySet = _.isNil(data) ? "000" : data.bonusPaySettingCode;
						
	                    self.commonSetting.updateData(worktimeSettingInfo.fixedWorkSetting.commonSetting);
	
	                    // set useHalfDay to mainScreen model
	                    self.useHalfDayWorking(worktimeSettingInfo.fixedWorkSetting.useHalfDayShift.workingTimes);
	                    self.useHalfDayOverTime(worktimeSettingInfo.fixedWorkSetting.useHalfDayShift.overTime);
	                    self.useHalfDayBreak(worktimeSettingInfo.fixedWorkSetting.useHalfDayShift.breakTime);
	
	                    // reset data of other mode
	                    self.flowWorkSetting.resetData();
	                    self.diffWorkSetting.resetData();
	                    self.flexWorkSetting.resetData();
	                }
	                
	                if (self.workTimeSetting.isDiffTime()) {
	                    self.diffWorkSetting.updateData(worktimeSettingInfo.diffTimeWorkSetting);

						if (worktimeSettingInfo.diffTimeWorkSetting != null && worktimeSettingInfo.diffTimeWorkSetting.commonSet != null)
						worktimeSettingInfo.diffTimeWorkSetting.commonSet.raisingSalarySet = _.isNil(data) ? "000" : data.bonusPaySettingCode;
						
	                    self.commonSetting.updateData(worktimeSettingInfo.diffTimeWorkSetting.commonSet);
	
	                    // set useHalfDay to mainScreen model
	                    self.useHalfDayWorking(worktimeSettingInfo.diffTimeWorkSetting.useHalfDayShift.workingTimes);
	                    self.useHalfDayOverTime(worktimeSettingInfo.diffTimeWorkSetting.useHalfDayShift.overTime);
	                    self.useHalfDayBreak(worktimeSettingInfo.diffTimeWorkSetting.useHalfDayShift.breakTime);
	
	                    // reset data of other mode
	                    self.flowWorkSetting.resetData();
	                    self.flexWorkSetting.resetData();
	                    self.fixedWorkSetting.resetData();
	                }      
	                
	                self.updateInterlockDialogJ();
	                self.updateStampValue();
	                dfd.resolve();
				})
				return dfd.promise();
            }
            
            resetData(isNewMode?: boolean) {
                let self = this;
                self.useHalfDayWorking(false);
                self.useHalfDayOverTime(false);
                self.useHalfDayBreak(false);
                self.fixedWorkSetting.resetData(isNewMode);
                self.flowWorkSetting.resetData();
                self.flexWorkSetting.resetData();
                self.diffWorkSetting.resetData(isNewMode);
                if (!isNewMode) {
                    //check change mode to convert data
                    self.commonSetting.resetData();
                    self.predetemineTimeSetting.resetData();
                    self.workTimeSetting.resetData();
                    self.workTimeSetting.resetWorkTimeDivision();
                }
                self.isInterlockDialogJ(true);
            }
            
            updateStampValue() {
                let _self = this;   
                
                // Update stamp data
                if (_self.isInterlockDialogJ()) {
                    if (_self.workTimeSetting.isFixed()) {                     
                        let workStart: number = _self.predetemineTimeSetting.startDateClock();
                        let workEnd: number = _self.predetemineTimeSetting.startDateClock() + (_self.predetemineTimeSetting.rangeTimeDay());
                        let endWork1: number = _self.predetemineTimeSetting.prescribedTimezoneSetting.shiftOne.end();
                        let startWork2: number = _self.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.start();
                                                                
                        if (_self.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.useAtr() && _self.tabMode() === TabMode.DETAIL) {
                            _self.fixedWorkSetting.getGoWork1Stamp().startTime(workStart);
                            _self.fixedWorkSetting.getLeaveWork1Stamp().startTime(workStart);                            
                            _self.fixedWorkSetting.getGoWork2Stamp().endTime(workEnd);
                            _self.fixedWorkSetting.getLeaveWork2Stamp().endTime(workEnd);
                                                                                   
                            _self.fixedWorkSetting.getGoWork1Stamp().endTime(endWork1);
                            _self.fixedWorkSetting.getGoWork2Stamp().startTime(endWork1 + 1);
                            _self.fixedWorkSetting.getLeaveWork1Stamp().endTime(startWork2);
                            _self.fixedWorkSetting.getLeaveWork2Stamp().startTime(startWork2 + 1);
                        } else {                           
                            _self.fixedWorkSetting.getGoWork1Stamp().startTime(workStart);
                            _self.fixedWorkSetting.getLeaveWork1Stamp().startTime(workStart);                            
                            _self.fixedWorkSetting.getGoWork1Stamp().endTime(workEnd);
                            _self.fixedWorkSetting.getLeaveWork1Stamp().endTime(workEnd);
                            
                            _self.fixedWorkSetting.getGoWork2Stamp().startTime(null);
                            _self.fixedWorkSetting.getLeaveWork2Stamp().startTime(null);                            
                            _self.fixedWorkSetting.getGoWork2Stamp().endTime(null);
                            _self.fixedWorkSetting.getLeaveWork2Stamp().endTime(null);
                        }
                    }   
                    if (_self.workTimeSetting.isFlex()) {
                        let workStart: number = _self.predetemineTimeSetting.startDateClock();
                        let workEnd: number = _self.predetemineTimeSetting.startDateClock() + (_self.predetemineTimeSetting.rangeTimeDay());
                        _self.flexWorkSetting.getGoWork1Stamp().startTime(workStart);
                        _self.flexWorkSetting.getGoWork1Stamp().endTime(workEnd);
                        _self.flexWorkSetting.getLeaveWork1Stamp().startTime(workStart);
                        _self.flexWorkSetting.getLeaveWork1Stamp().endTime(workEnd);
                    }  
                    if (_self.workTimeSetting.isFlow()) {
                        let workStart: number = _self.predetemineTimeSetting.startDateClock();
                        let workEnd: number = _self.predetemineTimeSetting.startDateClock() + (_self.predetemineTimeSetting.rangeTimeDay());
                        _self.flowWorkSetting.stampReflectTimezone.getGoWorkFlowStamp().startTime(workStart);
                        _self.flowWorkSetting.stampReflectTimezone.getGoWorkFlowStamp().endTime(workEnd);
                        _self.flowWorkSetting.stampReflectTimezone.getLeaveWorkFlowStamp().startTime(workStart);
                        _self.flowWorkSetting.stampReflectTimezone.getLeaveWorkFlowStamp().endTime(workEnd);
                    } 
                    if (_self.workTimeSetting.isDiffTime()) {
                        let workStart: number = _self.predetemineTimeSetting.startDateClock();
                        let workEnd: number = _self.predetemineTimeSetting.startDateClock() + (_self.predetemineTimeSetting.rangeTimeDay());
                        _self.diffWorkSetting.stampReflectTimezone.getGoWork1Stamp().startTime(workStart);
                        _self.diffWorkSetting.stampReflectTimezone.getGoWork1Stamp().endTime(workEnd);
                        _self.diffWorkSetting.stampReflectTimezone.getLeaveWork1Stamp().startTime(workStart);
                        _self.diffWorkSetting.stampReflectTimezone.getLeaveWork1Stamp().endTime(workEnd);
                    }  
                }             
            }
            
            updateInterlockDialogJ() {
                let _self = this;   
                             
                // Update interlock value
                if (_self.workTimeSetting.isFixed()) {
                    let workStart: number = _self.predetemineTimeSetting.startDateClock();
                    let workEnd: number = _self.predetemineTimeSetting.startDateClock() + _self.predetemineTimeSetting.rangeTimeDay();
                    let endWork1: number = _self.predetemineTimeSetting.prescribedTimezoneSetting.shiftOne.end();
                    let startWork2: number = _self.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.start();
                    
                    if (_self.predetemineTimeSetting.prescribedTimezoneSetting.shiftTwo.useAtr()) {
                        if ((_self.fixedWorkSetting.getGoWork1Stamp().startTime() == workStart) 
                            && (_self.fixedWorkSetting.getGoWork1Stamp().endTime() == endWork1)
                            && (_self.fixedWorkSetting.getLeaveWork1Stamp().startTime() == workStart)
                            && (_self.fixedWorkSetting.getLeaveWork1Stamp().endTime() == startWork2)
                            && (_self.fixedWorkSetting.getGoWork2Stamp().startTime() == endWork1 + 1)
                            && (_self.fixedWorkSetting.getGoWork2Stamp().endTime() == workEnd)
                            && (_self.fixedWorkSetting.getLeaveWork2Stamp().startTime() == startWork2 + 1)
                            && (_self.fixedWorkSetting.getLeaveWork2Stamp().endTime() == workEnd)) {
                            _self.isInterlockDialogJ(true);
                        } else {
                            _self.isInterlockDialogJ(false);    
                        }
                    } else {                                                   
                        if ((_self.fixedWorkSetting.getGoWork1Stamp().startTime() == workStart) 
                            && (_self.fixedWorkSetting.getGoWork1Stamp().endTime() == workEnd)
                            && (_self.fixedWorkSetting.getLeaveWork1Stamp().startTime() == workStart)
                            && (_self.fixedWorkSetting.getLeaveWork1Stamp().endTime() == workEnd)) {
                            _self.isInterlockDialogJ(true);
                        } else {
                            _self.isInterlockDialogJ(false);    
                        }
                    }                    
                    return;
                }              
                if (_self.workTimeSetting.isFlex()) {    
                    let workStart: number = _self.predetemineTimeSetting.startDateClock();
                    let workEnd: number = _self.predetemineTimeSetting.startDateClock() + _self.predetemineTimeSetting.rangeTimeDay();               
                    if ((_self.flexWorkSetting.getGoWork1Stamp().startTime() == workStart) 
                        && (_self.flexWorkSetting.getGoWork1Stamp().endTime() == workEnd)
                        && (_self.flexWorkSetting.getLeaveWork1Stamp().startTime() == workStart)
                        && (_self.flexWorkSetting.getLeaveWork1Stamp().endTime() == workEnd)) {
                        _self.isInterlockDialogJ(true);
                    } else {
                        _self.isInterlockDialogJ(false);    
                    }
                    return;
                }
                if (_self.workTimeSetting.isFlow()) {
                    let workStart: number = _self.predetemineTimeSetting.startDateClock();
                    let workEnd: number = _self.predetemineTimeSetting.startDateClock() + _self.predetemineTimeSetting.rangeTimeDay();  
                    if ((_self.flowWorkSetting.stampReflectTimezone.getGoWorkFlowStamp().startTime() == workStart) 
                        && (_self.flowWorkSetting.stampReflectTimezone.getGoWorkFlowStamp().endTime() == workEnd)
                        && (_self.flowWorkSetting.stampReflectTimezone.getLeaveWorkFlowStamp().startTime() == workStart)
                        && (_self.flowWorkSetting.stampReflectTimezone.getLeaveWorkFlowStamp().endTime() == workEnd)) {
                        _self.isInterlockDialogJ(true);
                    } else {
                        _self.isInterlockDialogJ(false);    
                    }
                    return;
                }                            
                if (_self.workTimeSetting.isDiffTime()) {
                    let workStart: number = _self.predetemineTimeSetting.startDateClock();
                    let workEnd: number = _self.predetemineTimeSetting.startDateClock() + _self.predetemineTimeSetting.rangeTimeDay();  
                    if ((_self.diffWorkSetting.stampReflectTimezone.getGoWork1Stamp().startTime() == workStart) 
                        && (_self.diffWorkSetting.stampReflectTimezone.getGoWork1Stamp().endTime() == workEnd)
                        && (_self.diffWorkSetting.stampReflectTimezone.getLeaveWork1Stamp().startTime() == workStart)
                        && (_self.diffWorkSetting.stampReflectTimezone.getLeaveWork1Stamp().endTime() == workEnd)) {
                        _self.isInterlockDialogJ(true);
                    } else {
                        _self.isInterlockDialogJ(false);    
                    }
                    return;
                }               
            }

            updatePeriod(startDateClock: number, rangeTimeDay: number) {
              const vm = this;
              if (vm.addMode()) {
                const model = vm.fixedWorkSetting.offdayWorkTimezone.lstWorkTimezone();
                if (model.length < 1) return;
                const timezone = model[0].timezone;
                timezone.start(startDateClock);
                timezone.end(startDateClock + rangeTimeDay);
                const dtos = _.map(model, data => {
                  return {
                    workTimeNo: data.workTimeNo(),
                    timezone: ko.toJS(data.timezone),
                    isLegalHolidayConstraintTime: data.isLegalHolidayConstraintTime(),
                    inLegalBreakFrameNo: data.inLegalBreakFrameNo(),
                    isNonStatutoryDayoffConstraintTime: data.isNonStatutoryDayoffConstraintTime(),
                    outLegalBreakFrameNo: data.outLegalBreakFrameNo(),
                    isNonStatutoryHolidayConstraintTime: data.isNonStatutoryHolidayConstraintTime(),
                    outLegalPubHDFrameNo: data.outLegalPubHDFrameNo()
                  };
                });
                vm.fixedWorkSetting.offdayWorkTimezone.updateHDTimezone(dtos);
              }
            }
        }

        export class WorkTimeSettingLoader extends WorkTimeSettingModel {
            workTimeAtrEnums: EnumConstantDto[];
            workTimeMethodEnums: EnumConstantDto[];
            loadListWorktime: (selectedCode?: string, selectedIndex?: number) => JQueryPromise<void>;
            selectedWorktimeCode: KnockoutObservable<string>;
            constructor(selectedWorktimeCode: KnockoutObservable<string>) {
                super();
                let self = this;
                this.selectedWorktimeCode = selectedWorktimeCode;
                this.isAbolish(true); // initial value in specs = clear, update default show value, remove A3_6
                this.workTimeDivision.workTimeDailyAtr(3);
                this.workTimeDivision.workTimeMethodSet(3);
                this.workTimeDivision.workTimeDailyAtr.subscribe(() => {
                    this.loadListWorktime(self.selectedWorktimeCode());
                });
                this.workTimeDivision.workTimeMethodSet.subscribe(() => {
                    this.loadListWorktime(self.selectedWorktimeCode());
                });
                this.isAbolish.subscribe(() => {
                    this.loadListWorktime(self.selectedWorktimeCode());
                });
            }

            public getCondition(): WorkTimeSettingCondition {
                let self = this;
                let cond = <WorkTimeSettingCondition>{};
                cond.workTimeDailyAtr = self.workTimeDivision.workTimeDailyAtr();
                cond.workTimeMethodSet = self.workTimeDivision.workTimeMethodSet();
                cond.isAbolish = self.isAbolish();

                // in case of all work atr
                if (self.isAllWorkAtr()) {
                    cond.workTimeDailyAtr = null;
                    cond.workTimeMethodSet = null;
                }

                // in case of flex or all work method
                if (self.isFlex() || self.isAllWorkMethod()) {
                    cond.workTimeMethodSet = null;
                }
                return cond;
            }

            public setEnums(enums: WorkTimeSettingEnumDto): void {
                let self = this;
                self.workTimeAtrEnums = _.cloneDeep(enums.workTimeDailyAtr);
                self.workTimeMethodEnums = _.cloneDeep(_.filter(enums.workTimeMethodSet, (item) => {return item.value != 1}));
                let all = <EnumConstantDto>{};
                all.value = 3;
                all.localizedName = "全て";
                self.workTimeAtrEnums.unshift(all);
                self.workTimeMethodEnums.unshift(all);
            }

            public selectAll(): void {
                let self = this;
                self.workTimeDivision.workTimeDailyAtr(3);
                self.workTimeDivision.workTimeMethodSet(3);
            }

            public isAllWorkAtr(): boolean {
                let self = this;
                return self.workTimeDivision.workTimeDailyAtr() == 3;
            }

            public isAllWorkMethod(): boolean {
                let self = this;
                return self.workTimeDivision.workTimeMethodSet() == 3;
            }
        }
        
        export enum EnumWorkForm {
            REGULAR,
            FLEX
        }
        
        export enum SettingMethod {
            FIXED,
            DIFFTIME,
            FLOW,
            ALL
        }
        
        export enum TabMode {
            DETAIL,
            SIMPLE
        }
        
        export enum ScreenMode {
            NEW,
            UPDATE,
            COPY
        }
        
        export class TabID {
            static TAB1 = "tab-1";
            static TAB2 = "tab-2";
            static TAB3 = "tab-3";
            static TAB4 = "tab-4";
            static TAB5 = "tab-5";
            static TAB6 = "tab-6";
            static TAB7 = "tab-7";
            static TAB8 = "tab-8";
            static TAB9 = "tab-9";
            static TAB10 = "tab-10";
            static TAB11 = "tab-11";
            static TAB12 = "tab-12";
            static TAB13 = "tab-13";
            static TAB14 = "tab-14";
            static TAB15 = "tab-15";
            static TAB16 = "tab-16";
            static TAB17 = "tab-17";
        }
        
        interface IWorkTimeLanguage {
            workTimeCode: string;
            langId: string;
            name: string;
            abbreviationName: string
        }

        class WorkTimeLanguage {
            workTimeCode: string;
            langId: string;
            name: string;
            abbreviationName: string;

            constructor(param: IWorkTimeLanguage) {
                let self = this;
                this.workTimeCode = param.workTimeCode;
                this.langId = param.langId;
                this.name = param.name;
                this.abbreviationName = param.abbreviationName;
            }
        }
    }
}