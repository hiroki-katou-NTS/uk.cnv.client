module nts.uk.at.view.kdl009.a {
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
            kdl009Data: any;
            employeeInfo: KnockoutObservable<string>;

            isFirstLoaded: KnockoutObservable<boolean> = ko.observable(false);
            isManagementSection: KnockoutObservable<boolean> = ko.observable(null);
            dataItems: KnockoutObservableArray<DataItems>;

            value01: KnockoutObservable<string> = ko.observable("");
            value02: KnockoutObservable<string> = ko.observable("");
            hint02: KnockoutObservable<string> = ko.observable("");
            value03: KnockoutObservable<string> = ko.observable("");
            hint03: KnockoutObservable<string> = ko.observable("");
            value04: KnockoutObservable<string> = ko.observable("");
            hint04: KnockoutObservable<string> = ko.observable("");
            expirationDateText: KnockoutObservable<string> = ko.observable("");

            created(params: any) {
                const vm = this;
                vm.kdl009Data = nts.uk.ui.windows.getShared("KDL009_DATA");
                vm.employeeInfo = ko.observable("");
                vm.dataItems = ko.observableArray([]);
                vm.legendOptions = {
                    items: [
                        { labelText: nts.uk.resource.getText("KDL009_29") },
                        { labelText: nts.uk.resource.getText("KDL009_30") }
                    ]
                };
            }

            mounted() {
                const vm = this;
                vm.$blockui('grayout');
                service.getEmployee(vm.kdl009Data)
                    .then((data: any) => {
                        if (data.employeeBasicInfo.length > 1) {
                            vm.selectedCode.subscribe((value) => {
                                const itemSelected: any = _.find(data.employeeBasicInfo, ['employeeCode', value]);
                                vm.onEmployeeSelect(
                                    data.baseDate,
                                    itemSelected.employeeId,
                                    itemSelected.employeeCode,
                                    itemSelected.businessName,
                                );
                            });
                            vm.bindEmpList(data.employeeBasicInfo);
                        } else if (data.employeeBasicInfo.length == 1) {
                            const itemSelected: any = data.employeeBasicInfo[0];
                            vm.onEmployeeSelect(
                                data.baseDate,
                                itemSelected.employeeId,
                                itemSelected.employeeCode,
                                itemSelected.businessName,
                            );
                        } else {
                            vm.employeeInfo(nts.uk.resource.getText("KDL009_25", ["", ""]));
                            nts.uk.ui.dialog.alertError({ messageId: "Msg_918" });
                        }
                    })
                    .fail(vm.onError)
                    .always(() => vm.$blockui('clear'));
            }

            // On select employee
            private onEmployeeSelect(baseDate: any, employeeId: string, employeeCode: string, employeeName: string) {
                const vm = this;
                vm.employeeInfo(nts.uk.resource.getText("KDL009_25", [employeeCode, employeeName]));
                vm.$blockui('grayout');
                // アルゴリズム「振休残数情報の取得」を実行する(thực hiện thuật toán 「振休残数情報の取得」)
                vm.isFirstLoaded(false);
                service.getAcquisitionNumberRestDays(employeeId, baseDate)
                    .then((data) => {
                        vm.isFirstLoaded(true);
                        vm.isManagementSection(data.isManagementSection);
                        vm.expirationDateText(ExpirationDate[data.expiredDay]);
                        vm.bindTimeData(data);
                        vm.bindSummaryData(data);
                        // Render table
                        if (data.isManagementSection) {
                            vm.$nextTick(() => $("#date-fixed-table").ntsFixedTable({ height: 150 }));
                        }
                    })
                    .fail(vm.onError)
                    .always(() => vm.$blockui('clear'));
            }

            // On error
            private onError(res: any) {
                nts.uk.ui.dialog.alertError({ messageId: res.messageId });
            }

            startPage(): JQueryPromise<any> {
                var dfd = $.Deferred();
                dfd.resolve();
                return dfd.promise();
            }

            bindEmpList(data: any) {
                const vm = this;

                // self.baseDate = ko.observable(new Date());
                vm.selectedCode(data[0].employeeCode);
                vm.multiSelectedCode = ko.observableArray([]);
                vm.isShowAlreadySet = ko.observable(false);
                vm.alreadySettingList = ko.observableArray([
                    { code: '1', isAlreadySetting: true },
                    { code: '2', isAlreadySetting: true }
                ]);
                vm.isDialog = ko.observable(false);
                vm.isShowNoSelectRow = ko.observable(false);
                vm.isMultiSelect = ko.observable(false);
                vm.isShowWorkPlaceName = ko.observable(false);
                vm.isShowSelectAllButton = ko.observable(false);
                vm.employeeList = ko.observableArray<UnitModel>(_.map(data, (x: any) => ({ code: x.employeeCode, name: x.businessName })));
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

                $('#component-items-list').ntsListComponent(vm.listComponentOption);
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

                //fix bug #115282
                return vm.sortListItem(listItem);
            }

            //fix bug #115282
            private sortListItem(listItem: DataItems[]): DataItems[] {

                const dto = listItem.map((s1: DataItems) => {

                    const listOccurrenceS1 = s1.listOccurrence ? (s1.listOccurrence[0].occurrenceDate ? s1.listOccurrence[0].occurrenceDate : undefined) : undefined;
                    const listDigestionS1 = s1.listDigestion ? (s1.listDigestion[0].occurrenceDate ? s1.listDigestion[0].occurrenceDate : undefined) : undefined;
                    const singleRowDetailS1 =  s1.singleRowDetail ? (s1.singleRowDetail.occurrenceDate ? s1.singleRowDetail.occurrenceDate : undefined) : undefined;
               
                    if(listOccurrenceS1) {
                        return {
                            itemId: s1.itemId,
                            occurrenceDate: listOccurrenceS1
                        }
                    }
                    if(listDigestionS1) {
                        return {
                            itemId: s1.itemId,
                            occurrenceDate: listDigestionS1
                        }
                    }
                    if(singleRowDetailS1) {
                        return {
                            itemId: s1.itemId,
                            occurrenceDate: singleRowDetailS1
                        }
                    }
                    return {
                        itemId: s1.itemId,
                        occurrenceDate: ""
                    }
                });

                const sortedDto = _.orderBy(dto, ["occurrenceDate"]);

                const sortedList = sortedDto.map(item => _.find(listItem, i => i.itemId === item.itemId));

                return sortedList;
            }

            private convertDetailDtoToModel(item: RemainNumberDetailDto): RemainNumberDetailModel {
                let occurrenceDateText: string = item.occurrenceDate ? (nts.uk.time as any).applyFormat("Short_YMDW", [item.occurrenceDate]) : '';
                let digestionDateText: string = item.digestionDate ? (nts.uk.time as any).applyFormat("Short_YMDW", [item.digestionDate]) : '';
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
                let digestionNumberText = '';
                if (item.digestionNumber === 0.5) {
                    digestionNumberText = nts.uk.resource.getText("KDL005_27", [item.digestionNumber]);
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
                vm.value01(nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.carryForwardDay, numberFormat)]));
                vm.value02(nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.occurrenceDay, numberFormat)]));
                vm.hint02(nts.uk.resource.getText("KDL005_33", [nts.uk.ntsNumber.formatNumber(data.scheduleOccurrencedDay, numberFormat)]));
                vm.value03(nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.usageDay, numberFormat)]));
                vm.hint03(nts.uk.resource.getText("KDL005_34", [nts.uk.ntsNumber.formatNumber(data.scheduledUsageDay, numberFormat)]));
                vm.value04(nts.uk.resource.getText("KDL005_27", [nts.uk.ntsNumber.formatNumber(data.remainingDay, numberFormat)]));
                vm.hint04(nts.uk.resource.getText("KDL005_35", [nts.uk.ntsNumber.formatNumber(data.scheduledRemainingDay, numberFormat)]));
            }

            cancel() {
                nts.uk.ui.windows.close();
            }
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

        export class RemainNumberDetailModel {
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

        export class DataItems {
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
    }
}