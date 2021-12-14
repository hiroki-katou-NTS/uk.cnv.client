module nts.uk.at.view.kdl005.a {

    export module viewmodel {

        @bean()
        export class ScreenModel extends ko.ViewModel {
            //Grid data
            listComponentOption: any;
            selectedCode: KnockoutObservable<string> = ko.observable('');
            multiSelectedCode: KnockoutObservableArray<string>;
            isShowAlreadySet: KnockoutObservable<boolean>;
            alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
            isDialog: KnockoutObservable<boolean>;
            isShowNoSelectRow: KnockoutObservable<boolean>;
            isMultiSelect: KnockoutObservable<boolean>;
            isShowWorkPlaceName: KnockoutObservable<boolean>;
            isShowSelectAllButton: KnockoutObservable<boolean>;
            employeeList: KnockoutObservableArray<UnitModel>;

            legendOptions: any;
            kdl005Data: any;
            employeeInfo: KnockoutObservable<string> = ko.observable("");

            isFirstLoaded: KnockoutObservable<boolean> = ko.observable(false);
            isManagementSection: KnockoutObservable<boolean> = ko.observable(null);
            dataItems: KnockoutObservableArray<any> = ko.observableArray([]);

            value01: KnockoutObservable<string> = ko.observable("");
            value02: KnockoutObservable<string> = ko.observable("");
            hint02: KnockoutObservable<string> = ko.observable("");
            value03: KnockoutObservable<string> = ko.observable("");
            hint03: KnockoutObservable<string> = ko.observable("");
            value04: KnockoutObservable<string> = ko.observable("");
            hint04: KnockoutObservable<string> = ko.observable("");
            expirationDateText: KnockoutObservable<string> = ko.observable("");

            // 119848
            isManageTime: boolean;

            created(params: any) {
                const vm = this;
                vm.kdl005Data = nts.uk.ui.windows.getShared("KDL005_DATA");
                vm.employeeInfo = ko.observable("");
                vm.dataItems = ko.observableArray([]);
                vm.legendOptions = {
                    items: [
                        { labelText: nts.uk.resource.getText("KDL005_20") },
                        { labelText: nts.uk.resource.getText("KDL005_22") }
                    ]
                };
            }

            mounted() {
                const vm = this;
                vm.$blockui('grayout');
                service.getEmployeeList(vm.kdl005Data)
                    .then((data: any) => {
                        if (data.employeeBasicInfo.length > 1) {
                            vm.selectedCode.subscribe((value) => {
                                let itemData: any = _.find(data.employeeBasicInfo, ['employeeCode', value]);
                                vm.onSelectEmployee(
                                    itemData.employeeId,
                                    vm.kdl005Data.baseDate,
                                    itemData.employeeCode,
                                    itemData.businessName,
                                );
                            });

                            // vm.baseDate = ko.observable(new Date());
                            vm.selectedCode(data.employeeBasicInfo[0].employeeCode);
                            vm.multiSelectedCode = ko.observableArray([]);
                            vm.isShowAlreadySet = ko.observable(false);
                            vm.alreadySettingList = ko.observableArray([
                                {code: '1', isAlreadySetting: true},
                                {code: '2', isAlreadySetting: true}
                            ]);
                            vm.isDialog = ko.observable(false);
                            vm.isShowNoSelectRow = ko.observable(false);
                            vm.isMultiSelect = ko.observable(false);
                            vm.isShowWorkPlaceName = ko.observable(false);
                            vm.isShowSelectAllButton = ko.observable(false);
                            vm.employeeList = ko.observableArray<UnitModel>(_.map(data.employeeBasicInfo,
                                (x: any) => {
                                    return {code:x.employeeCode ,name:x.businessName};
                                }));
                            vm.listComponentOption = {
                                isShowAlreadySet: vm.isShowAlreadySet(),
                                isMultiSelect: vm.isMultiSelect(),
                                listType: ListType.EMPLOYEE,
                                employeeInputList: vm.employeeList,
                                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                                selectedCode: vm.selectedCode,
                                isDialog: vm.isDialog(),
                                isShowNoSelectRow: vm.isShowNoSelectRow(),
                                alreadySettingList: vm.alreadySettingList,
                                isShowWorkPlaceName: vm.isShowWorkPlaceName(),
                                isShowSelectAllButton: vm.isShowSelectAllButton(),
                                maxRows: 12
                            };
                            $('#component-items-list').ntsListComponent(vm.listComponentOption)
                            .done(() => {
                                $('#component-items-list').focusComponent();
                                // Employment List
                                vm.employeeList($('#component-items-list').getDataList());
                            });
                        } else if (data.employeeBasicInfo.length == 1) {
                            // vm.employeeInfo(nts.uk.resource.getText("KDL009_25", [data.employeeBasicInfo[0].employeeCode, data.employeeBasicInfo[0].businessName]));
                            vm.onSelectEmployee(
                                data.employeeBasicInfo[0].employeeId,
                                vm.kdl005Data.baseDate,
                                data.employeeBasicInfo[0].employeeCode,
                                data.employeeBasicInfo[0].businessName,
                            );
                        } else {
                            vm.employeeInfo(nts.uk.resource.getText("KDL009_25", ["", ""]));
                        }
                    })
                    .always(() => vm.$blockui('clear'));

                    service.findComLeaveSetting().then((result: any) => vm.isManageTime = result.compensatoryDigestiveTimeUnit.isManageByTime === 1);
            }

            // 社員リストの先頭を選択
            onSelectEmployee(id: string, baseDate: any, employeeCode: string, employeeName: string) {
                const vm = this;
                // Show employee name
                vm.employeeInfo(nts.uk.resource.getText("KDL009_25", [employeeCode, employeeName]));
                vm.$blockui('grayout');
                // Get employee data
                vm.isFirstLoaded(false);
                service.getDetailsConfirm(id, baseDate)
                    .then((data) => {
                        vm.isFirstLoaded(true);
                        if (data.deadLineDetails && data.deadLineDetails.isManaged) {
                            vm.expirationDateText(ExpirationDate[data.deadLineDetails.expirationTime]);
                        }
                        vm.isManagementSection(data.isManagementSection);
                        vm.bindTimeData(data);
                        vm.bindSummaryData(data);
                        // Render table
                        if (data.isManagementSection) {
                            vm.$nextTick(() => $("#date-fixed-table").ntsFixedTable({ height: 150 }));
                        }
                    })
                    .always(() => vm.$blockui('clear'));
            }

            startPage(): JQueryPromise<any> {
                const vm = this;
                var dfd = $.Deferred();

                dfd.resolve();

                return dfd.promise();
            }

            private bindTimeData(data: AcquisitionNumberRestDayDto) {
                const vm = this;

                vm.dataItems.removeAll();

                // Convert to list item
                ko.utils.arrayPushAll(vm.dataItems, vm.convertDetailToItem(data.listRemainNumberDetail, data.listPegManagement));
            }

            private convertDetailToItem(listDetail: RemainNumberDetailDto[], listPeg: PegManagementDto[]): DataItems[] {
                const vm = this;
                let listItem: DataItems[] = [];
                let itemId: number = 0;
                const mapAddedOccurrenceDate = {};
                const mapAddedDigestionDate = {};

                // Convert listPeg to mutiple row item in final list
                for (const peg of listPeg) {
                    const existedItem = _.find(listItem, (item) => {
                        return _.findIndex(item.listOccurrence, (itemOccurrence) => itemOccurrence.occurrenceDate === peg.occurrenceDate) > -1
                            || _.findIndex(item.listDigestion, (itemDigestion) => itemDigestion.digestionDate === peg.usageDate) > -1;
                    });
                    if (existedItem) {
                        // Add to existed mutiple row item
                        // Check if adding to listOccurrence or listDigestion
                        if (!_.find(existedItem.listOccurrence, (item) => item.occurrenceDate === peg.occurrenceDate)) {
                            const itemOccurrenceDto: RemainNumberDetailDto = _.find(listDetail, (item) => item.occurrenceDate === peg.occurrenceDate);
                            if (!itemOccurrenceDto) {
                                continue;
                            }
                            mapAddedOccurrenceDate[peg.occurrenceDate] = itemOccurrenceDto;
                            const newlistOccurrence: RemainNumberDetailModel[] = existedItem.listOccurrence;
                            newlistOccurrence.push(vm.convertDetailDtoToModel(itemOccurrenceDto));
                            existedItem.isMultiOccurrence = true;
                            existedItem.listOccurrence = newlistOccurrence;
                            listItem = _.map(listItem, (item) => item.itemId === existedItem.itemId ? existedItem : item);
                        } else if (!_.find(existedItem.listDigestion, (item) => item.digestionDate === peg.usageDate)) {
                            const itemDigestionDto: RemainNumberDetailDto = _.find(listDetail, (item) => item.digestionDate === peg.usageDate);
                            if (!itemDigestionDto) {
                                continue;
                            }
                            mapAddedDigestionDate[peg.usageDate] = itemDigestionDto;
                            const newlistDigestion: RemainNumberDetailModel[] = existedItem.listDigestion;
                            newlistDigestion.push(vm.convertDetailDtoToModel(itemDigestionDto));
                            existedItem.isMultiDigestion = true;
                            existedItem.listDigestion = newlistDigestion;
                            listItem = _.map(listItem, (item) => item.itemId === existedItem.itemId ? existedItem : item);
                        }
                    } else {
                        const itemOccurrenceDto: RemainNumberDetailDto = _.find(listDetail, (item) => item.occurrenceDate === peg.occurrenceDate);
                        const itemDigestionDto: RemainNumberDetailDto = _.find(listDetail, (item) => item.digestionDate === peg.usageDate);
                        if (!itemOccurrenceDto || !itemDigestionDto) {
                            continue;
                        }
                        mapAddedOccurrenceDate[peg.occurrenceDate] = itemOccurrenceDto;
                        mapAddedDigestionDate[peg.usageDate] = itemDigestionDto;
                        // Create new mutiple row item
                        const itemOccurrence = vm.convertDetailDtoToModel(itemOccurrenceDto);
                        const itemDigestion = vm.convertDetailDtoToModel(itemDigestionDto);
                        listItem.push(new DataItems({
                            itemId: itemId,
                            isMultiOccurrence: false,
                            isMultiDigestion: false,
                            listOccurrence: [itemOccurrence],
                            listDigestion: [itemDigestion],
                        }));
                        // Increase id
                        itemId++;
                    }
                }

                // Convert listDetail to single row item in final list
                for (const detail of listDetail) {
                    if (detail.occurrenceDate) {
                        // Check if added
                        if (mapAddedOccurrenceDate[detail.occurrenceDate]) {
                            // Added them skip
                            continue;
                        }
                        mapAddedOccurrenceDate[detail.occurrenceDate] = detail;
                        // Create new mutiple row item
                        const itemOccurrence = vm.convertDetailDtoToModel(detail);
                        listItem.push(new DataItems({
                            itemId: itemId,
                            isMultiOccurrence: false,
                            isMultiDigestion: false,
                            singleRowDetail: itemOccurrence,
                        }));
                        // Increase id
                        itemId++;
                    } else if (detail.digestionDate) {
                        // Check if added
                        if (mapAddedDigestionDate[detail.digestionDate]) {
                            // Added them skip
                            continue;
                        }
                        mapAddedDigestionDate[detail.digestionDate] = detail;
                        // Create new mutiple row item
                        const itemDigestion = vm.convertDetailDtoToModel(detail);
                        listItem.push(new DataItems({
                            itemId: itemId,
                            isMultiOccurrence: false,
                            isMultiDigestion: false,
                            singleRowDetail: itemDigestion,
                        }));
                        // Increase id
                        itemId++;
                    }
                }
                return listItem;
            }

            private convertDetailDtoToModel(item: RemainNumberDetailDto): RemainNumberDetailModel {
                const vm = this;
                let digestionDateText: string = item.digestionDate ? (nts.uk.time as any).applyFormat("Short_YMDW", [item.digestionDate]) : '';
                let occurrenceDateText: string = item.occurrenceDate ? (nts.uk.time as any).applyFormat("Short_YMDW", [item.occurrenceDate]) : '';
                // 代休残数.休出代休残数詳細.管理データ状態区分をチェック
                if ([2, 3].indexOf(item.managementDataStatus) !== -1) {
                    occurrenceDateText = occurrenceDateText ? nts.uk.resource.getText("KDL005_36", [occurrenceDateText]) : '';
                    digestionDateText = digestionDateText ? nts.uk.resource.getText("KDL005_36", [digestionDateText]) : '';
                }
                let expirationDateText: string = item.expirationDate ? (nts.uk.time as any).applyFormat("Short_YMDW", [item.expirationDate]) : '';
                // 代休残数.休出代休残数詳細.当月で期限切れをチェック
                if (item.expiredInCurrentMonth) {
                    expirationDateText = expirationDateText ? nts.uk.resource.getText("KDL005_38", [expirationDateText]) : '';
                } else {
                    expirationDateText = expirationDateText ? nts.uk.resource.getText("KDL005_37", [expirationDateText]) : '';
                }
                // 「日数」の場合
                let occurrenceNumberText = '';
                if (item.occurrenceNumber === 0.5) {
                    occurrenceNumberText = nts.uk.resource.getText("KDL005_27", [item.occurrenceNumber]);
                }
                if (item.occurrenceHour && vm.isManageTime) {
                    occurrenceNumberText += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", item.occurrenceHour)}`;
                }

                let digestionNumberText = '';
                if (item.digestionNumber === 0.5) {
                    digestionNumberText = nts.uk.resource.getText("KDL005_27", [item.digestionNumber]);
                }
                if (item.digestionHour && vm.isManageTime) {
                    digestionNumberText += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", item.digestionHour)}`;
                }
                return new RemainNumberDetailModel({
                    expirationDate: item.expirationDate,
                    expirationDateText: expirationDateText,
                    digestionNumber: item.digestionNumber,
                    digestionDate: item.digestionDate,
                    digestionHour: item.digestionHour ? item.digestionHour.toString() : '',
                    digestionNumberText: digestionNumberText,
                    digestionDateText: digestionDateText,
                    digestionHourText: item.digestionHour ? item.digestionHour.toString() : '',
                    occurrenceNumber: item.occurrenceNumber,
                    occurrenceDate: item.occurrenceDate,
                    occurrenceHour: item.occurrenceHour ? item.occurrenceHour.toString() : '',
                    occurrenceNumberText: occurrenceNumberText,
                    occurrenceDateText: occurrenceDateText,
                    occurrenceHourText: item.occurrenceHour ? item.occurrenceHour.toString() : '',
                });
            }

            bindSummaryData(data: AcquisitionNumberRestDayDto) {
                const vm = this;
                const numberFormat = new nts.uk.ui.option.NumberEditorOption({ decimallength: 1 });

                let carryForwardDay = nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.carryForwardDay, numberFormat)]);
                if (data.carryForwardHour && vm.isManageTime) {
                    carryForwardDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.carryForwardHour)}`;
                }
                vm.value01(carryForwardDay);

                let occurrenceDay = nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.occurrenceDay, numberFormat)]);
                if (data.occurrenceHour && vm.isManageTime) {
                    occurrenceDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.occurrenceHour)}`;
                }
                vm.value02(occurrenceDay);

                let scheduleOccurrencedDay = nts.uk.resource.getText("KDL005_33", [nts.uk.ntsNumber.formatNumber(data.scheduleOccurrencedDay, numberFormat)]);
                if (data.scheduleOccurrencedHour && vm.isManageTime) {
                    scheduleOccurrencedDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.scheduleOccurrencedHour)}`;
                }
                vm.hint02(scheduleOccurrencedDay);

                let usageDay = nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.usageDay, numberFormat)]);
                if (data.usageHour && vm.isManageTime) {
                    usageDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.usageHour)}`;
                }
                vm.value03(usageDay);

                let scheduledUsageDay = nts.uk.resource.getText("KDL005_34", [nts.uk.ntsNumber.formatNumber(data.scheduledUsageDay, numberFormat)]);
                if (data.scheduledUsageHour && vm.isManageTime) {
                    scheduledUsageDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.scheduledUsageHour)}`;
                }
                vm.hint03(scheduledUsageDay);

                let remainingDay = nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.remainingDay, numberFormat)]);
                if (data.remainingHour && vm.isManageTime) {
                    remainingDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.remainingHour)}`;
                }
                vm.value04(remainingDay);

                let scheduledRemainingDay = nts.uk.resource.getText("KDL005_35", [nts.uk.ntsNumber.formatNumber(data.scheduledRemainingDay, numberFormat)]);
                if (data.scheduledRemainingHour && vm.isManageTime) {
                    scheduledRemainingDay += `　${(nts.uk.time as any).format.byId("Clock_Short_HM", data.scheduledRemainingHour)}`;
                }
                vm.hint04(scheduledRemainingDay);
            }

            cancel() {
                nts.uk.ui.windows.close();
            }

        }

        export enum ExpirationDate {
            "当月",//0
            "無期限",//1
            "年度末クリア",//2
            "1ヶ月",//3
            "2ヶ月",//4
            "3ヶ月",//5
            "4ヶ月",//6
            "5ヶ月",//7
            "6ヶ月",//8
            "7ヶ月",//9
            "8ヶ月",//10
            "9ヶ月",//11
            "10ヶ月",//12
            "11ヶ月",//13
            "1年",//14
        }

        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export interface UnitModel {
            code: string;
            name?: string;
            workplaceName?: string;
            isAlreadySetting?: boolean;
        }

        export class SelectType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
        }

        export interface UnitAlreadySettingModel {
            code: string;
            isAlreadySetting: boolean;
        }

        class RemainNumberDetailModel {
            expirationDate: string;
            expirationDateText: string;
            digestionNumber: number;
            digestionDate: string;
            digestionHour: string;
            digestionNumberText: string;
            digestionDateText: string;
            digestionHourText: string;
            occurrenceNumber: number;
            occurrenceDate: string;
            occurrenceHour: string;
            occurrenceNumberText: string;
            occurrenceDateText: string;
            occurrenceHourText: string;

            constructor(init?: Partial<RemainNumberDetailModel>) {
                $.extend(this, init);
            }
        }

        class DataItems {
            itemId: number;
            isMultiOccurrence: boolean;
            isMultiDigestion: boolean;
            listOccurrence: RemainNumberDetailModel[];
            listDigestion: RemainNumberDetailModel[];
            singleRowDetail: RemainNumberDetailModel;

            constructor(init?: Partial<DataItems>) {
                $.extend(this, init);
            }

            public isSingleRow() {
                return !this.isMultiOccurrence && !this.isMultiDigestion;
            }

            public isListOccurrenceExisted() {
                return this.listOccurrence && this.listOccurrence.length;
            }

            public isListDigestionExisted() {
                return this.listDigestion && this.listDigestion.length;
            }
        }
    }
}