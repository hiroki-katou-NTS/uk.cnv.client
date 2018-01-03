module a5 {
    import FixTableOption = nts.uk.at.view.kmk003.base.fixedtable.FixTableOption;
    import FlowRestTimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.common.FlowRestTimezoneModel;
    import DeductionTimeModel = nts.uk.at.view.kmk003.a.viewmodel.common.DeductionTimeModel;
    import DeductionTimeDto = nts.uk.at.view.kmk003.a.service.model.common.DeductionTimeDto;
    import WorkTimeSettingModel = nts.uk.at.view.kmk003.a.viewmodel.worktimeset.WorkTimeSettingModel;
    import FixHalfDayWorkTimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.fixedset.FixHalfDayWorkTimezoneModel;
    import DiffTimeHalfDayWorkTimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.difftimeset.DiffTimeHalfDayWorkTimezoneModel;
    import FlexHalfDayWorkTimeModel = nts.uk.at.view.kmk003.a.viewmodel.flexset.FlexHalfDayWorkTimeModel;
    import MainSettingModel = nts.uk.at.view.kmk003.a.viewmodel.MainSettingModel;

    class ScreenModel {

        // flex timezones
        oneDayFlexTimezones: KnockoutObservableArray<any>;
        morningFlexTimezones: KnockoutObservableArray<any>;
        afternoonFlexTimezones: KnockoutObservableArray<any>;

        // diff time timezones
        oneDayDiffTimezones: KnockoutObservableArray<any>;
        morningDiffTimezones: KnockoutObservableArray<any>;
        afternoonDiffTimezones: KnockoutObservableArray<any>;

        // fixed timezones
        oneDayFixedTimezones: KnockoutObservableArray<any>;
        morningFixedTimezones: KnockoutObservableArray<any>;
        afternoonFixedTimezones: KnockoutObservableArray<any>;

        // flow timezones
        flowTimezones: KnockoutObservableArray<any>;

        // flex restSet
        oneDayFlexRestSet: FlowRestTimezoneModel;
        morningFlexRestSet: FlowRestTimezoneModel;
        afternoonFlexRestSet: FlowRestTimezoneModel;

        // flow restSet
        flowRestSet: FlowRestTimezoneModel;

        // ntsFixTableCustom options

        // flex timezones option
        oneDayFlexTimezoneOption: FixTableOption;
        morningFlexTimezoneOption: FixTableOption;
        afternoonFlexTimezoneOption: FixTableOption;

        // difftime timezones option
        oneDayDiffTimezoneOption: FixTableOption;
        morningDiffTimezoneOption: FixTableOption;
        afternoonDiffTimezoneOption: FixTableOption;

        // fixed timezones option
        oneDayFixedTimezoneOption: FixTableOption;
        morningFixedTimezoneOption: FixTableOption;
        afternoonFixedTimezoneOption: FixTableOption;

        // flex rest set option
        oneDayFlexRestSetOption: FixTableOption;
        morningFlexRestSetOption: FixTableOption;
        afternoonFlexRestSetOption: FixTableOption;

        // flow timezones and rest set option
        flowTimezoneOption: FixTableOption;
        flowRestSetOption: FixTableOption;

        // switch
        switchDs: Array<any>;
        flexFixedRestTime: KnockoutObservable<boolean>;
        flowFixedRestTime: KnockoutObservable<boolean>;

        // flag
        isFlex: KnockoutObservable<boolean>;
        isFlow: KnockoutObservable<boolean>;
        isFixed: KnockoutObservable<boolean>;
        isDiffTime: KnockoutObservable<boolean>;

        // show/hide
        //isFlexOrFlow: KnockoutComputed<boolean>; // a5_2 flex or a5_4 flow *19
        //isTzOfFlexOrFixedOrDiff: KnockoutComputed<boolean>; // ( flex and suru ) or (fix or diff) *23
        isFlowTimezone: KnockoutComputed<boolean>; // flow and suru *24
        isFlowRestTime: KnockoutComputed<boolean>; // flow and nashi *25
        isFlexTimezone: KnockoutComputed<boolean>; // flex and suru *26
        isFlexRestTime: KnockoutComputed<boolean>; // flex and nashi *26
        display27: KnockoutComputed<boolean>; // A23_7 is checked *27

        constructor(valueAccessor: any) {
            let self = this;
            // switch button
            self.switchDs = [
                { code: true, name: nts.uk.resource.getText("KMK003_142") },
                { code: false, name: nts.uk.resource.getText("KMK003_143") }
            ];

            // flag
            self.flexFixedRestTime = ko.observable(true); // initial value = lead
            self.flowFixedRestTime = ko.observable(true); // initial value = lead

            // load data from main setting model
            self.loadData(valueAccessor.mainSettingModel);

            // fix table option
            self.setFixedTableOption();

        }

        /**
         * Load data from main screen
         */
        public loadData(mainSettingModel: MainSettingModel): void {
            let self = this;

            let flex = mainSettingModel.flexWorkSetting;

            let flexOneday = flex.getHDWtzOneday();
            let flexMorning = flex.getHDWtzMorning();
            let flexAfternoon = flex.getHDWtzAfternoon();

            let fixedOneday = mainSettingModel.fixedWorkSetting.getHDWtzOneday();
            let fixedMorning = mainSettingModel.fixedWorkSetting.getHDWtzMorning();
            let fixedAfternoon = mainSettingModel.fixedWorkSetting.getHDWtzAfternoon();

            // set flex timezones
            self.oneDayFlexTimezones = flexOneday.restTimezone.fixedRestTimezone.listTimeRange;

            self.morningFlexTimezones = flexMorning.restTimezone.fixedRestTimezone.listTimeRange;

            self.afternoonFlexTimezones = flexAfternoon.restTimezone.fixedRestTimezone.listTimeRange;

            // set fixed timezones
            self.oneDayFixedTimezones = fixedOneday.restTimezone.listTimeRange;

            self.morningFixedTimezones = fixedMorning.restTimezone.listTimeRange;

            self.afternoonFixedTimezones = fixedAfternoon.restTimezone.listTimeRange;

            // set flex rest set value
            self.oneDayFlexRestSet = flexOneday.restTimezone.flowRestTimezone;
            self.morningFlexRestSet = flexMorning.restTimezone.flowRestTimezone;
            self.afternoonFlexRestSet = flexAfternoon.restTimezone.flowRestTimezone;

            // set flow rest set value
            //TODO: chua lam

            // computed value initial
            self.initComputed(mainSettingModel.workTimeSetting);
        }

        /**
         * Initial computed.
         */
        private initComputed(workTimeSetting: WorkTimeSettingModel): void {
            let self = this;

            // set flag
            self.isFlex = workTimeSetting.isFlex;
            self.isFlow = workTimeSetting.isFlow;
            self.isFixed = workTimeSetting.isFixed;
            self.isDiffTime = workTimeSetting.isDiffTime;

            self.isFlowTimezone = ko.computed(() => {
                //return self.isFlow() && self.flowFixedRestTime() == 1;//TODO: hien tai chi lam flex va fixed.
                return false;
            });
            self.isFlowRestTime = ko.computed(() => {
                //return self.isFlow() && self.flowFixedRestTime() == 0; //TODO: hien tai chi lam flex va fixed.
                return false;
            });
            self.isFlexTimezone = ko.computed(() => {
                return self.isFlex() && self.flexFixedRestTime() == true;
            });
            self.isFlexRestTime = ko.computed(() => {
                return self.isFlex() && self.flexFixedRestTime() == false;
            });
        }

        /**
         * Set fixed table option
         */
        private setFixedTableOption(): void {
            let self = this;

            // flex timezone option
            self.oneDayFlexTimezoneOption = self.getDefaultTimezoneOption();
            self.oneDayFlexTimezoneOption.dataSource = self.oneDayFlexTimezones;
            self.morningFlexTimezoneOption = self.getDefaultTimezoneOption();
            self.morningFlexTimezoneOption.dataSource = self.morningFlexTimezones;
            self.afternoonFlexTimezoneOption = self.getDefaultTimezoneOption();
            self.afternoonFlexTimezoneOption.dataSource = self.afternoonFlexTimezones;

            // difftime timezone option
            self.oneDayDiffTimezoneOption = self.getDefaultTimezoneOption();
            self.oneDayDiffTimezoneOption.dataSource = self.oneDayDiffTimezones;
            self.morningDiffTimezoneOption = self.getDefaultTimezoneOption();
            self.morningDiffTimezoneOption.dataSource = self.morningDiffTimezones;
            self.afternoonDiffTimezoneOption = self.getDefaultTimezoneOption();
            self.afternoonDiffTimezoneOption.dataSource = self.afternoonDiffTimezones;

            // fixed timezone option
            self.oneDayFixedTimezoneOption = self.getDefaultTimezoneOption();
            self.oneDayFixedTimezoneOption.dataSource = self.oneDayFixedTimezones;
            self.morningFixedTimezoneOption = self.getDefaultTimezoneOption();
            self.morningFixedTimezoneOption.dataSource = self.morningFixedTimezones;
            self.afternoonFixedTimezoneOption = self.getDefaultTimezoneOption();
            self.afternoonFixedTimezoneOption.dataSource = self.afternoonFixedTimezones;

            // flex restSet option
            self.oneDayFlexRestSetOption = self.getDefaultRestSetOption();
            self.oneDayFlexRestSetOption.dataSource = self.oneDayFlexRestSet.flowRestSets;
            self.morningFlexRestSetOption = self.getDefaultRestSetOption();
            self.morningFlexRestSetOption.dataSource = self.morningFlexRestSet.flowRestSets;
            self.afternoonFlexRestSetOption = self.getDefaultRestSetOption();
            self.afternoonFlexRestSetOption.dataSource = self.afternoonFlexRestSet.flowRestSets;

            // flow timezone option
            self.flowTimezoneOption = self.getDefaultTimezoneOption();
            self.flowTimezoneOption.dataSource = self.flowTimezones;

            // flow restSet option
            //self.flowRestSetOption = self.getDefaultRestSetOption();
            //self.flowRestSetOption.dataSource = self.flowRestSet.flowRestSets;
        }

        private getDefaultTimezoneOption(): FixTableOption {
            let self = this;
            return {
                maxRow: 10,
                minRow: 0,
                maxRowDisplay: 10,
                isShowButton: true,
                dataSource: ko.observableArray([]),
                isMultipleSelect: true,
                columns: self.getTimezoneColumns(),
                tabindex: -1
            };
        }

        private getDefaultRestSetOption(): FixTableOption {
            let self = this;
            return {
                maxRow: 5,
                minRow: 0,
                maxRowDisplay: 5,
                isShowButton: true,
                dataSource: ko.observableArray([]),
                isMultipleSelect: true,
                columns: self.getRestSetColumns(),
                tabindex: -1
            };
        }

        private getRestSetColumns(): Array<any> {
            let self = this;
            return [
                {
                    headerText: nts.uk.resource.getText("KMK003_174"),
                    key: "startCol",
                    defaultValue: ko.observable(0),
                    width: 110,
                    template: `<input data-bind="ntsTimeEditor: { constraint: 'AttendanceTime', value: flowRestTime,
                        required: true, inputFormat: 'time', mode: 'time', enable: true }" />`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_176"),
                    key: "endCol",
                    defaultValue: ko.observable(0),
                    width: 110,
                    template: `<input data-bind="ntsTimeEditor: { constraint: 'AttendanceTime', value: flowPassageTime,
                        required: true, inputFormat: 'time', mode: 'time', enable: true }" />`
                }
            ];
        }

        private getTimezoneColumns(): Array<any> {
            let self = this;
            return [
                {
                    headerText: nts.uk.resource.getText("KMK003_54"),
                    key: "column1",
                    defaultValue: ko.observable({ startTime: 0, endTime: 0 }),
                    width: 243,
                    template: `<div data-bind="ntsTimeRangeEditor: { 
                        startConstraint: 'TimeWithDayAttr', endConstraint: 'TimeWithDayAttr',
                        required: true, enable: true, inputFormat: 'time'}"/>`
                }
            ];
        }

    }

    class KMK003A5BindingHandler implements KnockoutBindingHandler {

        constructor() {
        }

        init(element: any,
            valueAccessor: () => any): void {
            let webserviceLocator = nts.uk.request.location.siteRoot
                .mergeRelativePath(nts.uk.request.WEB_APP_NAME["at"] + '/')
                .mergeRelativePath('/view/kmk/003/a5/index.xhtml').serialize();

            let screenModel = new ScreenModel(valueAccessor());

            $(element).load(webserviceLocator, function() {
                ko.cleanNode($(element)[0]);
                ko.applyBindingsToDescendants(screenModel, $(element)[0]);
            });
        }

    }
    ko.bindingHandlers['ntsKMK003A5'] = new KMK003A5BindingHandler();

}
