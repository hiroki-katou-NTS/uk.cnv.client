module a6 {
    import WorkTimeSettingEnumDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeSettingEnumDto;
    import FlTimeSettingDto = nts.uk.at.view.kmk003.a.service.model.flowset.FlTimeSettingDto;
    import TimeRoundingSettingDto = nts.uk.at.view.kmk003.a.service.model.common.TimeRoundingSettingDto;
    import TimeZoneRoundingDto = nts.uk.at.view.kmk003.a.service.model.common.TimeZoneRoundingDto;
    import HDWorkTimeSheetSettingDto = nts.uk.at.view.kmk003.a.service.model.common.HDWorkTimeSheetSettingDto;
    import FlWorkHdTimeZoneDto = nts.uk.at.view.kmk003.a.service.model.flowset.FlWorkHdTimeZoneDto;
    import MainSettingModel = nts.uk.at.view.kmk003.a.viewmodel.MainSettingModel;
    import WorkDayoffFrameFindDto = nts.uk.at.view.kmk003.a6.service.model.WorkDayoffFrameFindDto;
    class ScreenModel {

        fixTableOptionFlow: any;
        fixTableOptionFlex: any;
        fixTableOptionFixed: any;
        fixTableOptionDiffTime: any;
        selectedTab: KnockoutObservable<string>;
        isFlowMode: KnockoutComputed<boolean>;
        isFlexMode: KnockoutComputed<boolean>;
        isFixedMode: KnockoutComputed<boolean>;
        isDiffTimeMode: KnockoutComputed<boolean>;
        dataSourceFlow: KnockoutObservableArray<any>;
        dataSourceFlex: KnockoutObservableArray<any>;
        dataSourceFixed: KnockoutObservableArray<any>;
        dataSourceDiffTime: KnockoutObservableArray<any>;
        settingEnum: WorkTimeSettingEnumDto;
        mainSettingModel: MainSettingModel;
        
        //update specs 7.6
//        lstOvertimeWorkFrame: OvertimeWorkFrameFindDto[];
        lstWorkDayOffFrame: WorkDayoffFrameFindDto[];

        /**
        * Constructor.
        */
        constructor(settingEnum: WorkTimeSettingEnumDto, mainSettingModel: MainSettingModel) {
            let self = this;
            self.settingEnum = settingEnum;
            self.mainSettingModel = mainSettingModel;
            self.updateDataByUpdateModel();
        }

        /**
         * function init data model
         */
        public initDataModel(): void {
            var self = this;
            self.fixTableOptionFlow = {
                maxRow: 5,
                minRow: 0,
                maxRowDisplay: 5,
                isShowButton: true,
                dataSource: self.dataSourceFlow,
                isMultipleSelect: true,
                columns: self.columnSettingFlow(),
                tabindex: 90
            };
            self.fixTableOptionFlex = {
                maxRow: 10,
                minRow: 1,
                maxRowDisplay: 10,
                isShowButton: true,
                dataSource: self.dataSourceFlex,
                isMultipleSelect: true,
                columns: self.columnSettingOtherFlow(),
                tabindex: 89
            };
            self.fixTableOptionFixed = {
                maxRow: 10,
                minRow: 1,
                maxRowDisplay: 10,
                isShowButton: true,
                dataSource: self.dataSourceFixed,
                isMultipleSelect: true,
                columns: self.columnSettingOtherFlow(),
                tabindex: 89
            };
            self.fixTableOptionDiffTime = {
                maxRow: 10,
                minRow: 1,
                maxRowDisplay: 10,
                isShowButton: true,
                dataSource: self.dataSourceDiffTime,
                isMultipleSelect: true,
                columns: self.columnSettingOtherFlow(),
                tabindex: 89
            };
        }
        
        /**
         * function update data by update model
         */
        private updateDataByUpdateModel(): void {
            var self = this;
            self.isFlowMode = self.mainSettingModel.workTimeSetting.isFlow;
            self.isFlexMode = self.mainSettingModel.workTimeSetting.isFlex;
            self.isFixedMode = self.mainSettingModel.workTimeSetting.isFixed;
            self.isDiffTimeMode = self.mainSettingModel.workTimeSetting.isDiffTime;

            self.dataSourceFlow = self.mainSettingModel.flowWorkSetting.offdayWorkTimezone.convertedList;
            self.dataSourceFlex = self.mainSettingModel.flexWorkSetting.offdayWorkTime.convertedList;
            self.dataSourceFixed = self.mainSettingModel.fixedWorkSetting.offdayWorkTimezone.convertedList;
            self.dataSourceDiffTime = self.mainSettingModel.diffWorkSetting.dayoffWorkTimezone.convertedList;
        }

        /**
        * init array setting column option leave time mode
        */
        private columnSettingOtherFlow(): Array<any> {
            let self = this;
            return [
                {
                    headerText: nts.uk.resource.getText("KMK003_54"),
                    key: "timezone",
                    defaultValue: ko.observable({ startTime: 0, endTime: 0 }),
                    width: 250,
                    template: `<div class= "fixtable" data-bind="ntsTimeRangeEditor: { 
                        startConstraint: 'TimeWithDayAttr', endConstraint: 'TimeWithDayAttr',
                        startTimeNameId: '#[KMK003_163]', endTimeNameId: '#[KMK003_164]',
                        required: true, enable: true, inputFormat: 'time', paramId: 'KMK003_90'}"/>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_77"),
                    key: "inLegalBreakFrameNo",
                    dataSource: self.lstWorkDayOffFrame,
                    defaultValue: ko.observable(1),
                    width: 130,
                    template: `<div data-key="workdayoffFrNo" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'workdayoffFrNo',
                                    visibleItemsCount: 10,
                                    optionsText: 'workdayoffFrName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'workdayoffFrName', length: 12 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_78"),
                    key: "outLegalBreakFrameNo",
                    dataSource: self.lstWorkDayOffFrame,
                    defaultValue: ko.observable(1),
                    width: 130,
                    template: `<div data-key="workdayoffFrNo" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'workdayoffFrNo',
                                    visibleItemsCount: 10,
                                    optionsText: 'workdayoffFrName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'workdayoffFrName', length: 12 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_79"),
                    key: "outLegalPubHolFrameNo",
                    defaultValue: ko.observable(1),
                    dataSource: self.lstWorkDayOffFrame,
                    width: 170,
                    template: `<div data-key="workdayoffFrNo" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'workdayoffFrNo',
                                    visibleItemsCount: 10,
                                    optionsText: 'workdayoffFrName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'workdayoffFrName', length: 12 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_56"),
                    key: "roundingTime",
                    dataSource: self.settingEnum.roundingTime,
                    defaultValue: ko.observable(0),
                    width: 80,
                    template: `<div data-key="value" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'value',
                                    visibleItemsCount: 8,
                                    optionsText: 'localizedName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'localizedName', length: 10 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_57"),
                    key: "rounding",
                    isRoudingColumn: true,
                    unitAttrName: 'roundingTime',
                    dataSource: self.settingEnum.rounding,
                    defaultValue: ko.observable(0),
                    width: 200,
                    template: `<div data-key="value" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'value',
                                    visibleItemsCount: 3,
                                    optionsText: 'localizedName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'localizedName', length: 10 }]}">
                                </div>`
                }
            ];
        }
        /**
         * init array setting column option close work
         */
        private columnSettingFlow(): Array<any> {
            let self = this;
            return [
                {
                    headerText: nts.uk.resource.getText("KMK003_174"),
                    key: "elapsedTime",
                    defaultValue: ko.observable(0),
                    width: 100,
                    template: `<input data-bind="ntsTimeEditor: {
                            name: '#[KMK003_174]',
                            constraint: 'AttendanceTime',
                            mode: 'time',
                            inputFormat: 'time',
                            required: true }" />`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_77"),
                    key: "inLegalBreakFrameNo",
                    dataSource: self.lstWorkDayOffFrame,
                    defaultValue: ko.observable(1),
                    width: 130,
                    template: `<div data-key="workdayoffFrNo" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'workdayoffFrNo',
                                    visibleItemsCount: 10,
                                    optionsText: 'workdayoffFrName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'workdayoffFrName', length: 12 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_78"),
                    key: "outLegalBreakFrameNo",
                    dataSource: self.lstWorkDayOffFrame,
                    defaultValue: ko.observable(1),
                    width: 130,
                    template: `<div data-key="workdayoffFrNo" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'workdayoffFrNo',
                                    visibleItemsCount: 10,
                                    optionsText: 'workdayoffFrName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'workdayoffFrName', length: 12 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_79"),
                    key: "outLegalPubHolFrameNo",
                    dataSource: self.lstWorkDayOffFrame,
                    defaultValue: ko.observable(1),
                    width: 160,
                    template: `<div data-key="value" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'workdayoffFrNo',
                                    visibleItemsCount: 10,
                                    optionsText: 'workdayoffFrName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'workdayoffFrName', length: 12 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_56"),
                    key: "roundingTime",
                    dataSource: self.settingEnum.roundingTime,
                    defaultValue: ko.observable(0),
                    width: 80,
                    template: `<div data-key="value" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'value',
                                    visibleItemsCount: 8,
                                    optionsText: 'localizedName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'localizedName', length: 10 }]}">
                                </div>`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_57"),
                    key: "rounding",
                    isRoudingColumn: true,
                    unitAttrName: 'roundingTime',
                    dataSource: self.settingEnum.rounding,
                    defaultValue: ko.observable(0),
                    width: 200,
                    template: `<div data-key="value" class="column-combo-box" data-bind="ntsComboBox: {
                                    optionsValue: 'value',
                                    visibleItemsCount: 3,
                                    optionsText: 'localizedName',
                                    editable: false,
                                    enable: true,
                                    columns: [{ prop: 'localizedName', length: 10 }]}">
                                </div>`
                }
            ];
        }


    }

    export interface SettlementOrder {
        code: number;
        name: string;
    }
    
    class KMK003A6BindingHandler implements KnockoutBindingHandler {
        /**
         * Constructor.
         */
        constructor() {
        }

        /**
         * Init.
         */
        init(element: any, valueAccessor: () => any): void {
            var webserviceLocator = nts.uk.request.location.siteRoot
                .mergeRelativePath(nts.uk.request.WEB_APP_NAME["at"] + '/')
                .mergeRelativePath('/view/kmk/003/a6/index.xhtml').serialize();
            //get data
            let input = valueAccessor();
            var settingEnum: WorkTimeSettingEnumDto = input.enum;
            var mainSettingModel: MainSettingModel = input.mainModel;
            nts.uk.at.view.kmk003.a6.service.findAllUsedWorkDayoffFrame().done(function(data) {
                let screenModel = new ScreenModel(settingEnum, mainSettingModel);
                screenModel.lstWorkDayOffFrame = data;
                screenModel.initDataModel();
                _.defer(() => {
                    $(element).load(webserviceLocator, function() {
                        ko.cleanNode($(element)[0]);
                        ko.applyBindingsToDescendants(screenModel, $(element)[0]);
                    });
                });
            });
        }

    }
    ko.bindingHandlers['ntsKMK003A6'] = new KMK003A6BindingHandler();

}
