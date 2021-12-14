module a5 {
    import FixTableOption = nts.fixedtable.FixTableOption;
    import FlowRestTimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.common.FlowRestTimezoneModel;
    import DeductionTimeModel = nts.uk.at.view.kmk003.a.viewmodel.common.DeductionTimeModel;
    import DeductionTimeDto = nts.uk.at.view.kmk003.a.service.model.common.DeductionTimeDto;
    import WorkTimeSettingModel = nts.uk.at.view.kmk003.a.viewmodel.worktimeset.WorkTimeSettingModel;
    import FixHalfDayWorkTimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.fixedset.FixHalfDayWorkTimezoneModel;
    import DiffTimeHalfDayWorkTimezoneModel = nts.uk.at.view.kmk003.a.viewmodel.difftimeset.DiffTimeHalfDayWorkTimezoneModel;
    import FlexHalfDayWorkTimeModel = nts.uk.at.view.kmk003.a.viewmodel.flexset.FlexHalfDayWorkTimeModel;
    import DialogHParam = nts.uk.at.view.kmk003.h.viewmodel.DialogHParam;
    import MainSettingModel = nts.uk.at.view.kmk003.a.viewmodel.MainSettingModel;
    import WorkTimeSettingEnumDto = nts.uk.at.view.kmk003.a.service.model.worktimeset.WorkTimeSettingEnumDto;

    class ScreenModel {
        mainSettingModel: MainSettingModel;

        //use half day
        useHalfDay: KnockoutObservable<boolean>;

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
        isFlex: KnockoutComputed<boolean>;
        isFlow: KnockoutComputed<boolean>;
        isFixed: KnockoutComputed<boolean>;
        isDiffTime: KnockoutComputed<boolean>;
        isDetailMode: KnockoutComputed<boolean>;

        // show/hide
        //isFlexOrFlow: KnockoutComputed<boolean>; // a5_2 flex or a5_4 flow *19
        //isTzOfFlexOrFixedOrDiff: KnockoutComputed<boolean>; // ( flex and suru ) or (fix or diff) *23
        isFlowTimezone: KnockoutComputed<boolean>; // flow and suru *24
        isFlowRestTime: KnockoutComputed<boolean>; // flow and nashi *25
        isFlexTimezone: KnockoutComputed<boolean>; // flex and suru *26
        isFlexRestTime: KnockoutComputed<boolean>; // flex and nashi *26
        display27: KnockoutComputed<boolean>; // A23_7 is checked *27
        enumSetting: WorkTimeSettingEnumDto;

        //accordion
        oneDayActive: KnockoutObservable<number>;
        morningActive: KnockoutObservable<number>;
        afternoonActive: KnockoutObservable<number>;

        constructor(valueAccessor: any) {
            let self = this;

            //accordion start up
            self.oneDayActive = ko.observable(0);
            self.morningActive = ko.observable(0);
            self.afternoonActive = ko.observable(0);

            // switch button
            self.switchDs = [
                {code: true, name: nts.uk.resource.getText("KMK003_142")},
                {code: false, name: nts.uk.resource.getText("KMK003_143")}
            ];

            // load data from main setting model
            self.mainSettingModel = valueAccessor.mainSettingModel;
            self.isDetailMode = valueAccessor.isDetailMode;

            //use halfDay
            self.useHalfDay = valueAccessor.useHalfDayBreak; //A19_2_2 initial false

            self.loadData();

            // fix table option
            self.setFixedTableOption();
            self.enumSetting = valueAccessor.enum;
        }

        /**
         * Open dialog flow break setting
         */
        public openFlowBreakSetting(): void {
            let self = this;

            let input = <DialogHParam>{};
            input.isFlow = self.isFlow();
            input.lstEnum = self.enumSetting;
            if (self.isFlex()) {
                //休憩中に退勤した場合の休憩時間の計算方法
                input.calcMethod = self.mainSettingModel.flexWorkSetting.restSetting.commonRestSetting.calculateMethod();
                //流動休憩設定
                input.useStamp = self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStamp();
                //外出の計上方法
                input.useStampCalcMethod = self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStampCalcMethod();
                //優先設定
                input.timeManagerSetAtr = self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowRestSetting.timeManagerSetAtr();
                //1勤務目と2勤務目の間を休憩として扱うか
                input.useRest = self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime();
                //休憩として扱う場合の単位
                input.roundUnit = self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime();
                //休憩として扱う場合の端数処理
                input.roundType = self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding();
            }
            if (self.isFlow()) {
                input.calcMethod = self.mainSettingModel.flowWorkSetting.restSetting.commonRestSetting.calculateMethod();
                input.useStamp = self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStamp();
                input.useStampCalcMethod = self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStampCalcMethod();
                input.timeManagerSetAtr = self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowRestSetting.timeManagerSetAtr();
                input.useRest = self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime();
                input.roundUnit = self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime();
                input.roundType = self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding();
            }

            let dialogHPath = (self.isFlex() && !self.flexFixedRestTime()) || (self.isFlow() && !self.flowFixedRestTime()) ? "/view/kmk/003/h/index2.xhtml" : "/view/kmk/003/h/index.xhtml";
            nts.uk.ui.windows.setShared("KMK003_DIALOG_H_INPUT", input);
            _.defer(() => nts.uk.ui.windows.sub.modal(dialogHPath).onClosed(() => {
                let dto: DialogHParam = nts.uk.ui.windows.getShared("KMK003_DIALOG_H_OUTPUT");
                if (!dto) {
                    return;
                }
                if (self.isFlow()) {
                    self.mainSettingModel.flowWorkSetting.restSetting.commonRestSetting.calculateMethod(dto.calcMethod);
                    self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStamp(dto.useStamp);
                    self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStampCalcMethod(dto.useStampCalcMethod);
                    self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowRestSetting.timeManagerSetAtr(dto.timeManagerSetAtr);
                    self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime(dto.useRest);
                    self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime(dto.roundUnit);
                    self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding(dto.roundType);
                }
                if (self.isFlex()) {
                    self.mainSettingModel.flexWorkSetting.restSetting.commonRestSetting.calculateMethod(dto.calcMethod);
                    self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStamp(dto.useStamp);
                    self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowRestSetting.useStampCalcMethod(dto.useStampCalcMethod);
                    self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowRestSetting.timeManagerSetAtr(dto.timeManagerSetAtr);
                    self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime(dto.useRest);
                    self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime(dto.roundUnit);
                    self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding(dto.roundType);
                }
            }));
        }

        /**
         * Load data from main screen
         */
        public loadData(): void {
            let self = this;

            let flex = self.mainSettingModel.flexWorkSetting;
            let flow = self.mainSettingModel.flowWorkSetting;
            let diff = self.mainSettingModel.diffWorkSetting;

            // Flex
            let flexOneday = flex.getHDWtzOneday();
            let flexMorning = flex.getHDWtzMorning();
            let flexAfternoon = flex.getHDWtzAfternoon();

            // Diff
            let diffOneday = diff.getHDWtzOneday();
            let diffMorning = diff.getHDWtzMorning();
            let diffAfternoon = diff.getHDWtzAfternoon();

            let fixedOneday = self.mainSettingModel.fixedWorkSetting.getHDWtzOneday();
            let fixedMorning = self.mainSettingModel.fixedWorkSetting.getHDWtzMorning();
            let fixedAfternoon = self.mainSettingModel.fixedWorkSetting.getHDWtzAfternoon();

            // set switch button value
            self.flexFixedRestTime = flex.fixRestTime;
            self.flowFixedRestTime = flow.halfDayWorkTimezone.restTimezone.fixRestTime;

            self.flexFixedRestTime.subscribe((v) => {
                self.clearTabError();
            })

            self.flowFixedRestTime.subscribe(v => {
                self.clearTabError();
            })

            //=================== TIMEZONE ===================
            // set flex timezones
            self.oneDayFlexTimezones = flexOneday.restTimezone.fixedRestTimezone.convertedList;
            self.morningFlexTimezones = flexMorning.restTimezone.fixedRestTimezone.convertedList;
            self.afternoonFlexTimezones = flexAfternoon.restTimezone.fixedRestTimezone.convertedList;

            // set fixed timezones
            self.oneDayFixedTimezones = fixedOneday.restTimezone.convertedList;
            self.morningFixedTimezones = fixedMorning.restTimezone.convertedList;
            self.afternoonFixedTimezones = fixedAfternoon.restTimezone.convertedList;

            // difftime timezone option
            self.oneDayDiffTimezones = diffOneday.restTimezone.convertedList;
            self.morningDiffTimezones = diffMorning.restTimezone.convertedList;
            self.afternoonDiffTimezones = diffAfternoon.restTimezone.convertedList;

            // flow timezone option
            self.flowTimezones = flow.halfDayWorkTimezone.restTimezone.fixedRestTimezone.convertedList;

            //=================== RESTSET ===================
            // set flex rest set value
            self.oneDayFlexRestSet = flexOneday.restTimezone.flowRestTimezone;
            self.morningFlexRestSet = flexMorning.restTimezone.flowRestTimezone;
            self.afternoonFlexRestSet = flexAfternoon.restTimezone.flowRestTimezone;

            // set flow rest set value
            self.flowRestSet = flow.halfDayWorkTimezone.restTimezone.flowRestTimezone;

            // computed value initial
            self.initComputed();
        }

        /**
         * Initial computed.
         */
        private initComputed(): void {
            let self = this;
            let workTimeSetting = self.mainSettingModel.workTimeSetting;

            // set flag
            self.isFlex = workTimeSetting.isFlex;
            self.isFlow = workTimeSetting.isFlow;
            self.isFixed = workTimeSetting.isFixed;
            self.isDiffTime = workTimeSetting.isDiffTime;

            self.isFlowTimezone = ko.computed(() => {
                return self.isFlow() && self.flowFixedRestTime();
            });
            self.isFlowRestTime = ko.computed(() => {
                return self.isFlow() && !self.flowFixedRestTime();
            });
            self.isFlexTimezone = ko.computed(() => {
                return self.isFlex() && self.flexFixedRestTime();
            });
            self.isFlexRestTime = ko.computed(() => {
                return self.isFlex() && !self.flexFixedRestTime();
            });
        }

        private clearTabError() {
            $('#flexOneDay').find('.time-range-editor').ntsError('clear');
            $('#flexOneDay2').find('.time-range-editor').ntsError('clear');
            $('#flexMorning').find('.time-range-editor').ntsError('clear');
            $('#flexAfternoon').find('.time-range-editor').ntsError('clear');

            $('#flexRestOneDay').find('.nts-editor').ntsError('clear');
            $('#flexRestOneDay2').find('.nts-editor').ntsError('clear');
            $('#flexRestMorning').find('.nts-editor').ntsError('clear');
            $('#flexRestAfternoon').find('.nts-editor').ntsError('clear');

            $('#flowTz').find('.time-range-editor').ntsError('clear');
            $('#flowRt').find('.nts-editor').ntsError('clear');

            $('#fixedOneDay').find('.time-range-editor').ntsError('clear');
            $('#fixedOneDay2').find('.time-range-editor').ntsError('clear');
            $('#fixedMorning').find('.time-range-editor').ntsError('clear');
            $('#fixedAfternoon').find('.time-range-editor').ntsError('clear');

        }

        public setTabIndexTable() {
            let self = this;

            //=================== TIMEZONE ===================
            // flex timezone
            self.oneDayFlexTimezoneOption.tabindex = 71;
            self.morningFlexTimezoneOption.tabindex = 72;
            self.afternoonFlexTimezoneOption.tabindex = 73;

            // diff
            self.oneDayDiffTimezoneOption.tabindex = 71;
            self.morningDiffTimezoneOption.tabindex = 72;
            self.afternoonDiffTimezoneOption.tabindex = 73;

            // fixed
            self.oneDayFixedTimezoneOption.tabindex = 71;
            self.morningFixedTimezoneOption.tabindex = 72;
            self.afternoonFixedTimezoneOption.tabindex = 73;

            // flow timezone
            self.flowTimezoneOption.tabindex = 83;

            //=================== RESTSET ===================
            // flex rest set
            self.oneDayFlexRestSetOption.tabindex = 74;
            self.morningFlexRestSetOption.tabindex = 77;
            self.afternoonFlexRestSetOption.tabindex = 80;

            // flow rest set
            self.flowRestSetOption.tabindex = 85;
        }

        /**
         * Set fixed table option
         */
        private setFixedTableOption(): void {
            let self = this;

            //=================== TIMEZONE ===================
            // flex timezone option
            self.oneDayFlexTimezoneOption = self.getDefaultTimezoneOption();
            self.oneDayFlexTimezoneOption.dataSource = self.oneDayFlexTimezones;
            self.morningFlexTimezoneOption = self.getDefaultTimezoneOption();
            self.morningFlexTimezoneOption.dataSource = self.morningFlexTimezones;
            self.afternoonFlexTimezoneOption = self.getDefaultTimezoneOption();
            self.afternoonFlexTimezoneOption.dataSource = self.afternoonFlexTimezones;

            // difftime timezone option
            self.oneDayDiffTimezoneOption = self.getDiffTimezoneOption();
            self.oneDayDiffTimezoneOption.dataSource = self.oneDayDiffTimezones;
            self.morningDiffTimezoneOption = self.getDiffTimezoneOption();
            self.morningDiffTimezoneOption.dataSource = self.morningDiffTimezones;
            self.afternoonDiffTimezoneOption = self.getDiffTimezoneOption();
            self.afternoonDiffTimezoneOption.dataSource = self.afternoonDiffTimezones;

            // fixed timezone option
            self.oneDayFixedTimezoneOption = self.getDefaultTimezoneOption();
            self.oneDayFixedTimezoneOption.dataSource = self.oneDayFixedTimezones;
            self.morningFixedTimezoneOption = self.getDefaultTimezoneOption();
            self.morningFixedTimezoneOption.dataSource = self.morningFixedTimezones;
            self.afternoonFixedTimezoneOption = self.getDefaultTimezoneOption();
            self.afternoonFixedTimezoneOption.dataSource = self.afternoonFixedTimezones;

            // flow timezone option
            self.flowTimezoneOption = self.getDefaultTimezoneOption();
            self.flowTimezoneOption.dataSource = self.flowTimezones;

            //=================== RESTSET ===================
            // flex restSet option
            self.oneDayFlexRestSetOption = self.getDefaultRestSetOption();
            self.oneDayFlexRestSetOption.dataSource = self.oneDayFlexRestSet.convertedList;
            self.morningFlexRestSetOption = self.getDefaultRestSetOption();
            self.morningFlexRestSetOption.dataSource = self.morningFlexRestSet.convertedList;
            self.afternoonFlexRestSetOption = self.getDefaultRestSetOption();
            self.afternoonFlexRestSetOption.dataSource = self.afternoonFlexRestSet.convertedList;

            // flow restSet option
            self.flowRestSetOption = self.getDefaultRestSetOption();
            self.flowRestSetOption.dataSource = self.flowRestSet.convertedList;
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
                tabindex: 1,
            };
        }

        private getDiffTimezoneOption(): FixTableOption {
            let self = this;
            let columns = self.getTimezoneColumns();
            columns.push({
                headerText: nts.uk.resource.getText("KMK003_129"),
                key: "isUpdateStartTime",
                defaultValue: ko.observable(false),
                width: 50,
                template: `<div data-bind="ntsCheckBox: { enable: true }">`
            });

            return {
                maxRow: 10,
                minRow: 0,
                maxRowDisplay: 10,
                isShowButton: true,
                dataSource: ko.observableArray([]),
                isMultipleSelect: true,
                columns: columns,
                tabindex: 1,
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
                tabindex: 1,
                helpTextId: 'KMK003_327'
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
                    template: `<input class="time-edior-column" data-bind="ntsTimeEditor: { constraint: 'AttendanceTime', value: flowPassageTime,
                        required: true, inputFormat: 'time', mode: 'time', enable: true, name: '#[KMK003_174]' }" />`
                },
                {
                    headerText: nts.uk.resource.getText("KMK003_176"),
                    key: "endCol",
                    defaultValue: ko.observable(0),
                    width: 110,
                    template: `<input class="time-edior-column" data-bind="ntsTimeEditor: { constraint: 'AttendanceTime', value: flowRestTime,
                        required: true, inputFormat: 'time', mode: 'time', enable: true, name: '#[KMK003_176]' }" />`
                }
            ];
        }

        private getTimezoneColumns(): Array<any> {
            let self = this;
            return [
                {
                    headerText: nts.uk.resource.getText("KMK003_54"),
                    key: "column1",
                    defaultValue: ko.observable({startTime: 0, endTime: 0}),
                    width: 243,
                    template: `<div data-bind="ntsTimeRangeEditor: { 
                        startConstraint: 'TimeWithDayAttr', endConstraint: 'TimeWithDayAttr',
                        required: true, enable: true, inputFormat: 'time',
                        startTimeNameId: '#[KMK003_163]', endTimeNameId: '#[KMK003_164]',paramId: 'KMK003_20'}"/>`
                }
            ];
        }

        public openDialogG() {
            //open dialog G 
            var self = this;
            //if flex or flow
            if (self.isFlex() || self.isFlow()) {
                let dataFlexFlow: any = null;
                if (self.isFlow()) {//flow
                    dataFlexFlow = {
                        workForm: EnumWorkForm.FLEX,
                        settingMethod: SettingMethod.FLOW,
                        isFlow: true,
                        lstEnum: self.enumSetting,
                        //1勤務目と2勤務目の間を休憩として扱うか
                        useRest: self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime() ? 1 : 0,
                        //休憩として扱う場合の単位
                        roundUnit: self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime(),
                        //休憩として扱う場合の端数処理
                        roundType: self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding(),
                        //休憩中に退勤した場合の休憩時間の計算方法
                        calcMethod: self.mainSettingModel.flowWorkSetting.restSetting.commonRestSetting.calculateMethod(),
                        //固定の場合の実績の休憩計算方法
                        fixedCalcMethod: self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateMethod(),
                        //私用外出の計上方法
                        usePrivateGoOutRest: self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.usePrivateGoOutRest(),
                        //組合外出の計上方法
                        useAssoGoOutRest: self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.useAssoGoOutRest(),
                    }
                } else {//flex
                    dataFlexFlow = {
                        workForm: EnumWorkForm.FLEX,
                        settingMethod: SettingMethod.FLOW,
                        isFlow: false,
                        lstEnum: self.enumSetting,
                        useRest: self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime() ? 1 : 0,
                        roundUnit: self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime(),
                        roundType: self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding(),
                        fixedCalcMethod: self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateMethod(),
                        calcMethod: self.mainSettingModel.flexWorkSetting.restSetting.commonRestSetting.calculateMethod(),
                        usePrivateGoOutRest: self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.usePrivateGoOutRest(),
                        useAssoGoOutRest: self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.useAssoGoOutRest()
                    }
                }

                nts.uk.ui.windows.setShared('KMK003_DIALOG_G_INPUT_DATA', dataFlexFlow);
                let dialogGHeight= self.isFlex() ? 550 : 700;
                nts.uk.ui.windows.sub.modal("/view/kmk/003/g/index.xhtml", {height: dialogGHeight}).onClosed(() => {
                    var returnObject = nts.uk.ui.windows.getShared('KMK003_DIALOG_G_OUTPUT_DATA');
                    //if case flex
                    if (self.isFlex()) {
                        self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime(returnObject.useRest == 1);
                        self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding(returnObject.roundType);
                        self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime(returnObject.roundUnit);
                        self.mainSettingModel.flexWorkSetting.restSetting.commonRestSetting.calculateMethod(returnObject.calcMethod);
                        self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.usePrivateGoOutRest(returnObject.usePrivateGoOutRest);
                        self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.useAssoGoOutRest(returnObject.useAssoGoOutRest);
                        self.mainSettingModel.flexWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateMethod(returnObject.fixedCalcMethod);
                    } else//case flow
                    {
                        self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.usePluralWorkRestTime(returnObject.useRest == 1);
                        self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.rounding(returnObject.roundType);
                        self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.roundingBreakMultipleWork.roundingTime(returnObject.roundUnit);
                        self.mainSettingModel.flowWorkSetting.restSetting.commonRestSetting.calculateMethod(returnObject.calcMethod);
                        self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.usePrivateGoOutRest(returnObject.usePrivateGoOutRest);
                        self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateFromStamp.useAssoGoOutRest(returnObject.useAssoGoOutRest);
                        self.mainSettingModel.flowWorkSetting.restSetting.flowRestSetting.flowFixedRestSetting.calculateMethod(returnObject.fixedCalcMethod);
                    }
                });
            } else{
                let  dataFixed = {
                        calcMethod: self.mainSettingModel.fixedWorkSetting.commonRestSet.calculateMethod(),
                        isFlow: false,
                        workForm: EnumWorkForm.REGULAR,
                        settingMethod: SettingMethod.FIXED
                    }
                nts.uk.ui.windows.setShared('KMK003_DIALOG_G_INPUT_DATA', dataFixed);
                nts.uk.ui.windows.sub.modal("/view/kmk/003/g/index2.xhtml").onClosed(() => {
                    let returnObject = nts.uk.ui.windows.getShared('KMK003_DIALOG_G_OUTPUT_DATA');
                    self.mainSettingModel.fixedWorkSetting.commonRestSet.calculateMethod(returnObject.calcMethod);
                });
            }
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

    export enum FixedCalcMethod {

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

            $(element).load(webserviceLocator, function () {
                ko.cleanNode($(element)[0]);
                ko.applyBindingsToDescendants(screenModel, $(element)[0]);
                _.defer(() => screenModel.setTabIndexTable()); // set tabindex
            });
        }

    }

    ko.bindingHandlers['ntsKMK003A5'] = new KMK003A5BindingHandler();

}
