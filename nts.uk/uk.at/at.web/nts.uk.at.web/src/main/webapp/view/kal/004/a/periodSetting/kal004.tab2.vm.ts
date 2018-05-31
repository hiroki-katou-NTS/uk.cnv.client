module nts.uk.at.view.kal004.tab2.viewModel {
    import getText = nts.uk.resource.getText;
    import share = nts.uk.at.view.kal004.share.model;

    export class ScreenModel {
        listCheckConditionCode: KnockoutObservableArray<share.CheckConditionCommand> = ko.observableArray([]);
        listCheckCondition: KnockoutObservableArray<share.CheckConditionCommand> = ko.observableArray([]);
        listStorageCheckCondition: KnockoutObservableArray<share.CheckConditionCommand> = ko.observableArray([]);
        ListView: KnockoutObservableArray<ModelCheckConditonCode>;
        isCreateMode: KnockoutObservable<boolean>;

        // this variable only use for screen G        
        selectedTab: KnockoutObservable<string> = ko.observable('tab-1');
        constructor() {
            var self = this;

            self.listCheckConditionCode.subscribe((newList) => {
                self.changeCheckCondition(newList);
            });
            self.ListView = ko.observableArray([]);
            $("#fixed-table").ntsFixedTable({ height: 320, width: 830 });
            self.isCreateMode = ko.observable(true);
        }


        private changeCheckCondition(listCheckCode: Array<share.CheckConditionCommand>): void {
            var self = this;
            var listConverToview = [];
            var listCheckConditionDto = [];
            if (listCheckCode.length == 0) {
                self.ListView.removeAll();
                self.listCheckCondition.removeAll();
                return;
            } else if (self.listStorageCheckCondition().length == 0) {
                _.forEach(listCheckCode, (category: share.CheckConditionCommand) => {
                    let checkCondition = new share.CheckConditionCommand(category.alarmCategory, category.checkConditionCodes, category.extractionPeriodDaily, category.extractionPeriodUnit,
                        category.listExtractionMonthly, category.extractionYear);
                    listCheckConditionDto.push(checkCondition);
                    listConverToview.push(new ModelCheckConditonCode(checkCondition, self.selectedTab()));
                    self.listStorageCheckCondition.push(category);
                });
            } else {
                _.forEach(listCheckCode, (category: share.CheckConditionCommand) => {
                    let checkCondition = new share.CheckConditionCommand(category.alarmCategory, category.checkConditionCodes, category.extractionPeriodDaily, category.extractionPeriodUnit,
                        category.listExtractionMonthly, category.extractionYear);
                    var check = _.find(self.listStorageCheckCondition(), ['alarmCategory', category.alarmCategory]);
                    if (nts.uk.util.isNullOrUndefined(check)) {
                        listCheckConditionDto.push(checkCondition);
                        listConverToview.push(new ModelCheckConditonCode(checkCondition, self.selectedTab()));
                        self.listStorageCheckCondition.push(category);
                    } else {
                        let checkConditionUpDate = new share.CheckConditionCommand(category.alarmCategory, category.checkConditionCodes, check.extractionPeriodDaily, category.extractionPeriodUnit,
                            category.listExtractionMonthly, category.extractionYear);
                        listCheckConditionDto.push(checkConditionUpDate);
                        listConverToview.push(new ModelCheckConditonCode(checkConditionUpDate, self.selectedTab()));
                    }
                });
            }
            self.listCheckCondition(listCheckConditionDto);
            self.ListView(_.orderBy(listConverToview, 'categoryId', 'asc'));
        }
        private openDialog(modelCheck: ModelCheckConditonCode): void {
            var self = this;

            var categoryId = modelCheck.categoryId;
            if (categoryId == 7) {

                let paramMonthly = _.find(modelCheck.listExtractionMonthly, ['unit', 3]);
                let extractionMonthDto = {
                    extractionId: paramMonthly.extractionId,
                    extractionRange: paramMonthly.extractionRange,
                    strMonth: paramMonthly.strMonth,
                    endMonth: paramMonthly.endMonth
                };
                nts.uk.ui.windows.setShared("extractionMonthDto", extractionMonthDto);
                nts.uk.ui.windows.setShared("categoryId", categoryId);
                nts.uk.ui.windows.setShared("categoryName", modelCheck.categoryName);
                nts.uk.ui.windows.sub.modal("../d/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared("extractionMonthly");
                    if (!nts.uk.util.isNullOrUndefined(data)) {
                        let extractMonthlyCommand = {
                            extractionId: data.extractionId,
                            extractionRange: data.extractionRange,
                            unit: 3,
                            strSpecify: 1,
                            yearType: 2,
                            specifyMonth: 0,
                            strMonth: data.strMonth,
                            strCurrentMonth: 1,
                            strPreviousAtr: 0,
                            endSpecify: 2,
                            extractPeriod: 12,
                            endMonth: data.endMonth,
                            endCurrentMonth: 1,
                            endPreviousAtr: 0
                        }
                        self.changeExtractionMonthly(extractMonthlyCommand, categoryId);
                    }
                });
            }
            else if (categoryId == 2) {
                let paramUnit = modelCheck.extractionPeriodUnit.segmentationOfCycle;
                nts.uk.ui.windows.setShared("segmentationOfCycle", paramUnit);
                nts.uk.ui.windows.setShared("categoryName", modelCheck.categoryName);
                nts.uk.ui.windows.sub.modal("../f/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared("segmentationOfCycle");
                    if (!nts.uk.util.isNullOrUndefined(data) && !nts.uk.util.isNullOrUndefined(data.decisionEnum)) {
                        self.extractionPeriodUnit(categoryId, modelCheck.extractionPeriodUnit.extractionId, data.decisionEnum);
                    }
                });
            } else if (categoryId == 5 || categoryId == 13) {
                let paramDaily = modelCheck.extractionPeriodDaily;
                let extractionDailyDto = {
                    extractionId: paramDaily.extractionId,
                    extractionRange: paramDaily.extractionRange,
                    strSpecify: paramDaily.strSpecify,
                    strPreviousDay: paramDaily.strPreviousDay,
                    strMakeToDay: paramDaily.strMakeToDay,
                    strDay: paramDaily.strDay,
                    strPreviousMonth: paramDaily.strPreviousMonth,
                    strCurrentMonth: paramDaily.strCurrentMonth,
                    strMonth: paramDaily.strMonth,
                    endSpecify: paramDaily.endSpecify,
                    endPreviousDay: paramDaily.endPreviousDay,
                    endMakeToDay: paramDaily.endMakeToDay,
                    endDay: paramDaily.endDay,
                    endPreviousMonth: paramDaily.endPreviousMonth,
                    endCurrentMonth: paramDaily.endCurrentMonth,
                    endMonth: paramDaily.endMonth
                };
                nts.uk.ui.windows.setShared("extractionDailyDto", extractionDailyDto);
                nts.uk.ui.windows.setShared("categoryId", categoryId);
                nts.uk.ui.windows.setShared("categoryName", modelCheck.categoryName);
                nts.uk.ui.windows.sub.modal("../b/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared("extractionDaily");
                    if (!nts.uk.util.isNullOrUndefined(data)) {
                        self.changeExtractionDaily(data, categoryId);
                    }
                });
            } else if (categoryId == 12) {

                let daily36 = modelCheck.extractionPeriodDaily;
                let listMonthly36 = modelCheck.listExtractionMonthly;
                let yearly36 = modelCheck.extractionYear;

                nts.uk.ui.windows.setShared("categoryId", categoryId);
                nts.uk.ui.windows.setShared("categoryName", modelCheck.categoryName);
                nts.uk.ui.windows.setShared("selectedTab", self.selectedTab());
                nts.uk.ui.windows.setShared("daily36", daily36);
                nts.uk.ui.windows.setShared("listMonthly36", listMonthly36);
                nts.uk.ui.windows.setShared("yearly36", yearly36);

                nts.uk.ui.windows.sub.modal("../g/index.xhtml").onClosed(() => {
                    let daily36Share = nts.uk.ui.windows.getShared("daily36Share");
                    let listMonth36Share = nts.uk.ui.windows.getShared("listMonth36Share");
                    let yearly36Share = nts.uk.ui.windows.getShared("yearly36Share");
                    let selectedTab = nts.uk.ui.windows.getShared("selectedTab");
                    if (!nts.uk.util.isNullOrUndefined(selectedTab)) {
                        self.selectedTab(selectedTab);
                    }

                    if (!nts.uk.util.isNullOrUndefined(listMonth36Share)) {
                        self.changeExtraction36Agreement(listMonth36Share, daily36Share, yearly36Share, categoryId);
                    }
                });
            }
        }

        private changeExtraction36Agreement(listMonth36Share: Array<share.ExtractionPeriodMonthlyDto>,
            daily36Share: share.ExtractionDailyDto, yearly36Share: share.ExtractionRangeYearDto, categoryId: number) {
            let self = this;
            let oldItem = _.find(self.listStorageCheckCondition(), ['alarmCategory', categoryId]);
            let monthlyDtos: Array<share.ExtractionPeriodMonthlyCommand> = oldItem.listExtractionMonthly;
            _.remove(monthlyDtos, (item) => { return item.unit != 3 });
            listMonth36Share.forEach((monthlyItem) => {
                monthlyDtos.push(new share.ExtractionPeriodMonthlyCommand(monthlyItem));
            });

            oldItem.setExtractPeriod(new share.ExtractionPeriodDailyCommand(daily36Share));
            oldItem.setExtractYearly(new share.ExtractionRangeYearCommand(yearly36Share));

            let listCheckConditionDto: Array<share.CheckConditionCommand> = [];

            _.forEach(self.listCheckCondition(), (checkCondition: share.CheckConditionCommand) => {
                if (checkCondition.alarmCategory == categoryId) {

                    _.remove(checkCondition.listExtractionMonthly, (item) => { return item.unit != 3 });
                    listMonth36Share.forEach((monthlyItem) => {
                        checkCondition.listExtractionMonthly.push(new share.ExtractionPeriodMonthlyCommand(monthlyItem));
                    });

                    checkCondition.setExtractPeriod(new share.ExtractionPeriodDailyCommand(daily36Share));
                    checkCondition.setExtractYearly(new share.ExtractionRangeYearCommand(yearly36Share));

                }
                listCheckConditionDto.push(checkCondition);
            });
            self.changeCheckCondition(listCheckConditionDto);


        }


        private changeExtractionMonthly(extractionMonthlyDto: share.ExtractionPeriodMonthlyDto, categoryId: number): void {
            let self = this;
            let oldItem = _.find(self.listStorageCheckCondition(), ['alarmCategory', categoryId]);
            let monthlyDto: Array<share.ExtractionPeriodMonthlyCommand> = oldItem.listExtractionMonthly;
            _.remove(monthlyDto, (item) => { return item.unit == 3 });
            monthlyDto.push(new share.ExtractionPeriodMonthlyCommand(extractionMonthlyDto));

            let listCheckConditionDto: Array<share.CheckConditionCommand> = [];
            _.forEach(self.listCheckCondition(), (category: share.CheckConditionCommand) => {
                if (category.alarmCategory == categoryId) {

                    _.remove(category.listExtractionMonthly, (item) => { return item.unit == 3 });
                    category.listExtractionMonthly.push(new share.ExtractionPeriodMonthlyCommand(extractionMonthlyDto));
                }
                listCheckConditionDto.push(category);
            });
            self.changeCheckCondition(listCheckConditionDto);
        }

        private changeExtractionDaily(extractionDailyDto: share.ExtractionDailyDto, categoryId: number): void {
            var self = this;
            var oldItem = _.find(self.listStorageCheckCondition(), ['alarmCategory', categoryId]);
            oldItem.setExtractPeriod(new share.ExtractionPeriodDailyCommand(extractionDailyDto));
            var listCheckConditionDto: Array<share.CheckConditionCommand> = [];
            _.forEach(self.listCheckCondition(), (category: share.CheckConditionCommand) => {
                if (category.alarmCategory == categoryId) {
                    category.setExtractPeriod(new share.ExtractionPeriodDailyCommand(extractionDailyDto));
                }
                listCheckConditionDto.push(category);
            });
            self.changeCheckCondition(listCheckConditionDto);
        }

        private extractionPeriodUnit(categoryId: number, extractionId: string, segmentationOfCycle: number): void {
            var self = this;
            var oldItem = _.find(self.listStorageCheckCondition(), ['alarmCategory', categoryId]);
            oldItem.setExtractUnit(new share.PeriodUnitCommand({ extractionId: extractionId, extractionRange: 3, segmentationOfCycle: segmentationOfCycle }));
            var listCheckConditionDto: Array<share.CheckConditionCommand> = [];
            _.forEach(self.listCheckCondition(), (category: share.CheckConditionCommand) => {
                if (category.alarmCategory == categoryId) {
                    category.setExtractUnit(new share.PeriodUnitCommand({ extractionId: extractionId, extractionRange: 3, segmentationOfCycle: segmentationOfCycle }));
                }
                listCheckConditionDto.push(category);
            });
            self.changeCheckCondition(listCheckConditionDto);
        }

    }
    export class ModelCheckConditonCode {
        categoryId: number;
        categoryName: string;
        extractionPeriod: string;
        ListSpecifiedMonth: Array<any> = __viewContext.enums.SpecifiedMonth;
        PreviousClassification: Array<any> = __viewContext.enums.PreviousClassification
        ListAlarmCategory: Array<any> = __viewContext.enums.AlarmCategory;
        SegmentationOfCycle: Array<any> = __viewContext.enums.SegmentationOfCycle;
        standardMonth: Array<any> = __viewContext.enums.StandardMonth;

        extractionPeriodDaily: share.ExtractionPeriodDailyCommand;
        extractionPeriodUnit: share.PeriodUnitCommand;
        listExtractionMonthly: Array<share.ExtractionPeriodMonthlyCommand>;
        extractionYear: share.ExtractionRangeYearCommand;

        constructor(checkCondition: share.CheckConditionCommand, selectedTab: string) {
            let self = this;
            this.categoryId = checkCondition.alarmCategory;
            this.categoryName = _.find(self.ListAlarmCategory, ['value', checkCondition.alarmCategory]).name;

            this.extractionYear = checkCondition.extractionYear;
            this.extractionPeriodDaily = checkCondition.extractionPeriodDaily;
            this.extractionPeriodUnit = checkCondition.extractionPeriodUnit;
            this.listExtractionMonthly = checkCondition.listExtractionMonthly;


            if (nts.uk.util.isNullOrUndefined(checkCondition.extractionPeriodDaily) && checkCondition.alarmCategory == 2) {
                this.extractionPeriod = _.find(self.SegmentationOfCycle, ['value', checkCondition.extractionPeriodUnit.segmentationOfCycle]).name;

            } else if (checkCondition.alarmCategory == 5 || checkCondition.alarmCategory == 13) {
                this.setDailyText(checkCondition);

            } else if (checkCondition.alarmCategory == 7) {
                this.extractionPeriod = _.find(self.standardMonth, ['value', checkCondition.listExtractionMonthly[0].strMonth]).name + ' ' + getText('KAL004_30') + ' ' +
                    _.find(self.standardMonth, ['value', checkCondition.listExtractionMonthly[0].endMonth]).name;

            } else if (checkCondition.alarmCategory == 12) {

                this.set36AgreementText(checkCondition, selectedTab);
            } else {
                this.extractionPeriod = "";
            }

        }


        private setDailyText(checkCondition: share.CheckConditionCommand) {
            let self = this;
            let str, end;
            if (checkCondition.extractionPeriodDaily.strSpecify == 0) {
                str = getText('KAL004_32') + checkCondition.extractionPeriodDaily.strDay + getText('KAL004_34') + _.find(self.PreviousClassification, ['value', checkCondition.extractionPeriodDaily.strPreviousDay]).name;
            } else {
                let strMonth = _.find(self.ListSpecifiedMonth, ['value', checkCondition.extractionPeriodDaily.strMonth]);
                str = strMonth.name + getText('KAL004_37');
            }
            if (checkCondition.extractionPeriodDaily.endSpecify == 0) {
                end = getText('KAL004_32') + checkCondition.extractionPeriodDaily.endDay + getText('KAL004_34') + _.find(self.PreviousClassification, ['value', checkCondition.extractionPeriodDaily.endPreviousDay]).name;
            } else {
                let endMonth = _.find(self.ListSpecifiedMonth, ['value', checkCondition.extractionPeriodDaily.endMonth]);
                end = endMonth.name + getText('KAL004_43');
            }
            this.extractionPeriod = str + ' ' + getText('KAL004_30') + ' ' + end;
        }

        private set36AgreementText(checkCondition: share.CheckConditionCommand, selectedTab: string) {
            let self = this;
            let str, end;
            if (selectedTab == "tab-1") {
                if (checkCondition.extractionPeriodDaily.strSpecify == 0) {
                    str = getText('KAL004_77') + checkCondition.extractionPeriodDaily.strDay + getText('KAL004_79') + _.find(self.PreviousClassification, ['value', checkCondition.extractionPeriodDaily.strPreviousDay]).name;
                } else {
                    let strMonth = _.find(self.standardMonth, ['value', checkCondition.extractionPeriodDaily.strMonth]);
                    str = strMonth.name + getText('KAL004_81');
                }
                if (checkCondition.extractionPeriodDaily.endSpecify == 0) {
                    end = getText('KAL004_82') + checkCondition.extractionPeriodDaily.endDay + getText('KAL004_84') + _.find(self.PreviousClassification, ['value', checkCondition.extractionPeriodDaily.endPreviousDay]).name;
                } else {
                    let endMonth = _.find(self.standardMonth, ['value', checkCondition.extractionPeriodDaily.endMonth]);
                    end = endMonth.name + getText('KAL004_86');
                }

            } else if (selectedTab == "tab-2") {
                let month2: share.ExtractionPeriodMonthlyCommand = _.find(self.listExtractionMonthly, ['unit', 0]);
                str = _.find(self.standardMonth, ['value', month2.strMonth]).name;
                end = _.find(self.standardMonth, ['value', month2.endMonth]).name;
    
            } else if (selectedTab == 'tab-3') {
                let month3: share.ExtractionPeriodMonthlyCommand = _.find(self.listExtractionMonthly, ['unit', 1]);
                if (month3.strSpecify == 1) {
                    str = _.find(self.standardMonth, ['value', month3.strMonth]).name;
                } else {
                    str = getText('KAL004_96') + month3.specifyMonth + getText('KAL004_98');
                }
                end = _.find(self.standardMonth, ['value', month3.endMonth]).name;

            } else if (selectedTab == 'tab-4') {
                let month4: share.ExtractionPeriodMonthlyCommand = _.find(self.listExtractionMonthly, ['unit', 2]);
                if (month4.strSpecify == 1) {
                    str = _.find(self.standardMonth, ['value', month4.strMonth]).name;
                } else {
                    str = getText('KAL004_104') + month4.specifyMonth + getText('KAL004_106');
                }
                end = _.find(self.standardMonth, ['value', month4.endMonth]).name;

            } else if (selectedTab == 'tab-5') {
                if (this.extractionYear.thisYear) str = getText('KAL004_110');
                else str = this.extractionYear.year + getText('KAL004_109');

            }

            if (selectedTab != 'tab-5') {
                this.extractionPeriod = str + ' ' + getText('KAL004_30') + ' ' + end;
            } else {
                this.extractionPeriod = str;
            }
        }
    }
}