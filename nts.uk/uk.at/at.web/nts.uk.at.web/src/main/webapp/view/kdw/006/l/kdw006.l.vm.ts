/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmk006.l {

    const DATE_FORMAT = 'YYYY/MM/DD';

    const API = {
        LIST_HISTORY: 'at/record/kdw006/view-k/get-list-history',
        RESISTER: 'at/record/kdw006/view-l/register-history',
        UPDATE: 'at/record/kdw006/view-l/update-history',
        REMOTE: 'at/record/kdw006/view-l/remote-history'
    }

    @bean()
    export class ViewModel extends ko.ViewModel {
        screenMode: KnockoutObservable<number> = ko.observable(SCREEN_MODE.SELECT);
        initMode: KnockoutObservable<number> = ko.observable(INIT_MODE.WORKPLACE);
        lstWpkHistory: KnockoutObservableArray<HistoryItem> = ko.observableArray([]);
        selectedHistoryId: KnockoutObservable<string> = ko.observable('');
        bkHistoryId: string;
        bkStartDate: string;
        bkEndDate: string;
        selectedStartDateInput: KnockoutObservable<string | null> = ko.observable('');
        selectedStartDateText: KnockoutObservable<string | null> = ko.observable('');
        selectedEndDate: KnockoutObservable<string> = ko.observable('');
        copyPreviousConfig: KnockoutObservable<boolean> = ko.observable(false);
        isLatestHistory: KnockoutObservable<boolean> = ko.observable(true);
        textTitle: KnockoutObservable<string> = ko.observable('');
        public model: HistoryItem = new HistoryItem();
        public itemId: String = '';

        created(param: any) {
            const vm = this;

            vm.screenMode.subscribe(() => {
                vm.setTextTitle();
            });
            vm.screenMode.valueHasMutated();
            var hisLocal: HistoryItem[] = [];

            if (param) {
                vm.itemId = param.itemId;
                if (param.history) {
                    if (param.history.dateHistoryItems) {
                        _.forEach(param.history.dateHistoryItems, ((value: IHistoryInfo) => {
                            hisLocal.push(new HistoryItem({
                                historyId: value.historyId,
                                startDate: moment(value.startDate).format(DATE_FORMAT),
                                endDate: moment(value.endDate).format(DATE_FORMAT)
                            }));
                        }));
                        vm.lstWpkHistory(_.orderBy(hisLocal, ['startDate'], ['desc']));
                    }
                }
            }

            vm.selectedHistoryId.subscribe((value: string) => {
                vm.model.update(_.find(ko.unwrap(vm.lstWpkHistory), ((f: HistoryItem) => { return f.historyId === value; })));
                vm.selectedEndDate(vm.model.endDate);
                vm.selectedStartDateText(vm.model.startDate);
                vm.screenMode(SCREEN_MODE.SELECT);
                if (value !== ko.unwrap(vm.lstWpkHistory)[0].historyId) {
                    vm.isLatestHistory(false);
                } else {
                    vm.isLatestHistory(true);
                }
            });

            if (ko.unwrap(vm.lstWpkHistory).length > 0) {
                vm.selectedHistoryId(ko.unwrap(vm.lstWpkHistory)[0].historyId);
            } else {
                vm.screenMode(SCREEN_MODE.NEW);
                vm.setEndDate();
            }
        }

        mounted() {
            const vm = this;
            $('.list-history #single-list_container').focus();
            vm.$errors('clear');
        }

        addHistory() {
            const vm = this;
            vm.screenMode(SCREEN_MODE.ADD);
            vm.selectedStartDateInput(null);
            vm.setEndDate();
        }

        updateHistory() {
            const vm = this;
            vm.screenMode(SCREEN_MODE.UPDATE);
            vm.selectedStartDateInput(moment(vm.model.startDate).format(DATE_FORMAT));
        }

        deleteHistory() {
            const vm = this;
            const param = { historyId: ko.unwrap(vm.model.historyId), itemId: vm.itemId }
            // const oldIndex = _.map(ko.unwrap(vm.lstWpkHistory), m => m.historyId).indexOf(ko.unwrap(vm.model.historyId));
            // const newIndex = oldIndex == ko.unwrap(vm.lstWpkHistory).length - 1 ? oldIndex - 1 : oldIndex;

            nts.uk.ui.dialog
                .confirm({ messageId: "Msg_18" })
                .ifYes(() => {
                    vm.$blockui("invisible")
                        .then(() => {
                            vm.$ajax('at', API.REMOTE, param)
                                .done(() => vm.$dialog.info({ messageId: "Msg_16" }))
                                .then(() => {
                                    vm.reload();
                                })
                                .fail((fail: any) => vm.$dialog.info({ messageId: fail.messageId }))
                                .always(() => vm.$blockui('clear'))
                        })
                })
        }

        reload() {
            const vm = this;
            var hisLocal: HistoryItem[] = [];

            vm.$blockui('invisible')
                .then(() => {
                    vm.$ajax(API.LIST_HISTORY)
                        .done((data: IHistory[]) => {
                            const exist = _.find(data, (item: IHistory) => { return item.itemId === vm.itemId });
                            if (exist) {
                                _.forEach(exist.dateHistoryItems, ((value: IHistoryInfo) => {
                                    hisLocal.push(new HistoryItem({
                                        historyId: value.historyId,
                                        startDate: moment(value.startDate).format(DATE_FORMAT),
                                        endDate: moment(value.endDate).format(DATE_FORMAT)
                                    }));
                                }));
                                vm.lstWpkHistory(_.orderBy(hisLocal, ['startDate'], ['desc']));
                            } else {
                                hisLocal = [];
                                vm.lstWpkHistory([]);
                                vm.screenMode(SCREEN_MODE.NEW);
                                vm.setEndDate();
                                vm.selectedStartDateInput('');
                            }
                        })
                        .then(() => {
                            vm.selectedHistoryId(ko.unwrap(vm.lstWpkHistory)[0].historyId);
                            vm.selectedHistoryId.valueHasMutated();
                            vm.screenMode(SCREEN_MODE.SELECT);
                        })
                })
                .always(() => vm.$blockui('clear'));
        }

        cancel() {
            const vm = this;
            vm.$window.close();
        }

        validate(action: 'clear' | undefined = undefined) {
            if (action === 'clear') {
                return $.Deferred().resolve()
                    .then(() => $('.nts-input').ntsError('clear'));
            } else {
                return $.Deferred().resolve()
                    .then(() => $('.nts-input').trigger("validate"))
                    .then(() => !$('.nts-input').ntsError('hasError'));
            }
        }

        AddOrUpdate() {
            const vm = this;
            const param = {
                itemId: vm.itemId,
                historyId: ko.unwrap(vm.model.historyId),
                startDate: new Date(ko.unwrap(vm.selectedStartDateInput)),
                endDate: new Date(ko.unwrap(vm.selectedEndDate))
            }
            var hisLocal: HistoryItem[] = ko.unwrap(vm.lstWpkHistory);

            if (ko.unwrap(vm.screenMode) === SCREEN_MODE.ADD || ko.unwrap(vm.screenMode) === SCREEN_MODE.NEW) {
                vm.$blockui('invisible')
                    .then(() => {
                        vm.validate()
                            .then((valid: boolean) => {
                                if (valid) {
                                    vm.$ajax('at', API.RESISTER, param)
                                        .done((hisId: any) => {
                                            vm.$errors('clear');
                                            vm.$dialog.info({ messageId: 'Msg_15' })
                                                .then(() => {
                                                    hisLocal.push(new HistoryItem({
                                                        historyId: hisId.hisId,
                                                        startDate: moment(ko.unwrap(vm.selectedStartDateInput)).format(DATE_FORMAT),
                                                        endDate: moment(ko.unwrap(vm.selectedEndDate)).format(DATE_FORMAT)
                                                    }));
                                                    vm.reload();
                                                });
                                        })
                                        .fail((data: any) => {
                                            vm.$dialog.info({ messageId: data.messageId })
                                        })
                                }
                            })
                    })
                    .always(() => vm.$blockui('clear'));
            } else
                if (ko.unwrap(vm.screenMode) === SCREEN_MODE.UPDATE) {
                    vm.$blockui('invisible')
                        .then(() => {
                            vm.$ajax('at', API.UPDATE, param)
                                .done(() => {
                                    vm.$errors('clear');
                                    vm.$dialog.info({ messageId: 'Msg_15' })
                                        .then(() => {
                                            _.remove(hisLocal, (item: HistoryItem) => { return item.historyId === ko.unwrap(vm.model.historyId) })
                                            hisLocal.push(new HistoryItem({
                                                historyId: ko.unwrap(vm.model.historyId),
                                                startDate: moment(ko.unwrap(vm.selectedStartDateInput)).format(DATE_FORMAT),
                                                endDate: moment(ko.unwrap(vm.selectedEndDate)).format(DATE_FORMAT)
                                            }));
                                            vm.reload();
                                        });
                                })
                                .fail((data: any) => {
                                    vm.$dialog.info({ messageId: data.messageId })
                                })
                        })
                        .always(() => vm.$blockui('clear'));
                } else {
                    vm.$window.close(ko.unwrap(vm.model.historyId));
                }
        }

        setEndDate(param?: string) {
            const vm = this;
            switch (ko.unwrap(vm.screenMode)) {
                case SCREEN_MODE.SELECT:
                case SCREEN_MODE.UPDATE:
                    vm.selectedEndDate(param);
                    break
                case SCREEN_MODE.NEW:
                case SCREEN_MODE.ADD:
                    vm.selectedEndDate('9999/12/31');
                    break;
            }
        }

        setTextTitle() {
            const vm = this;
            switch (ko.unwrap(vm.screenMode)) {
                case SCREEN_MODE.SELECT:
                case SCREEN_MODE.NEW:
                    vm.textTitle(vm.$i18n('KDW006_343'));
                    break
                case SCREEN_MODE.ADD:
                    vm.textTitle(vm.$i18n('KDW006_344'));
                    break
                case SCREEN_MODE.UPDATE:
                    vm.textTitle(vm.$i18n('KDW006_345'));
                    break;
            }
        }

    }

    enum SCREEN_MODE {
        SELECT = 0,
        NEW = 1,
        ADD = 2,
        UPDATE = 3
    }

    enum INIT_MODE {
        WORKPLACE = 0,
        DEPARTMENT = 1
    }

    class HistoryItem {
        historyId: string = '';
        startDate: string = '';
        endDate: string = '';
        displayText: string = '';

        constructor(params?: IHistoryItem) {
            const vm = new ko.ViewModel();
            if (params) {
                this.historyId = params.historyId;
                this.startDate = params.startDate;
                this.endDate = params.endDate;
                this.displayText = params.startDate + " " + vm.$i18n("CMM011_125") + " " + params.endDate;
            }
        }

        public update(params?: IHistoryItem) {
            const vm = new ko.ViewModel();
            if (params) {
                this.historyId = params.historyId;
                this.startDate = params.startDate;
                this.endDate = params.endDate;
                this.displayText = params.startDate + " " + vm.$i18n("CMM011_125") + " " + params.endDate;
            }
        }
    }

    interface IHistoryItem {
        historyId: string;
        startDate: string;
        endDate: string;
    }

    interface IHistory {
        /** 項目ID */
        itemId: String;
        /** 履歴項目 */
        dateHistoryItems: IHistoryInfo[];
    }

    class History {
        itemId: KnockoutObservable<String> = ko.observable('');
        dateHistoryItems: KnockoutObservableArray<HistoryInfo> = ko.observableArray([]);

        constructor(param: IHistory) {
            this.itemId(param.itemId);
            var historyInfos: HistoryInfo[] = [];
            _.forEach(param.dateHistoryItems, ((value: IHistoryInfo) => {
                historyInfos.push(new HistoryInfo(value));
            }));
            this.dateHistoryItems(historyInfos);
        }
    }

    interface IHistoryInfo {
        historyId: string;
        startDate: Date;
        endDate: Date;
    }

    class HistoryInfo {
        historyId: KnockoutObservable<String> = ko.observable('');
        startDate: KnockoutObservable<Date> = ko.observable(new Date);;
        endDate: KnockoutObservable<Date> = ko.observable(new Date);
        period: KnockoutObservable<String> = ko.observable('');

        constructor(param: IHistoryInfo) {
            this.historyId(param.historyId);
            this.startDate(param.startDate);
            this.endDate(param.endDate);
            this.period(param.startDate.toString() + ' ~ ' + param.endDate.toString());
        }
    }
}
