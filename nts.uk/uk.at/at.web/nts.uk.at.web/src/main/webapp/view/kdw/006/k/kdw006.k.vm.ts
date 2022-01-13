/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmk006.k {

    const API = {
        LIST_WORK_INFO: 'at/record/kdw006/view-k/get-list-work',
        LIST_HISTORY: 'at/record/kdw006/view-k/get-list-history',
        WORK_INFO_DATAIL: 'at/record/kdw006/view-k/get-work-info-detail',
        WORK_INFO_DATAIL_BY_HISTORYS: 'at/record/kdw006/view-k/get-list-work-info-detail',
        REGISTER: 'at/record/kdw006/view-k/register-new-obtion',
        UPDATE: 'at/record/kdw006/view-k/update-obtion',
        REMOTE: 'at/record/kdw006/view-k/delete'
    }

    const DATE_FORMAT = 'YYYY/MM/DD';

    @bean()
    export class ViewModel extends ko.ViewModel {

        public itemWorks: KnockoutObservableArray<IWorkInfo> = ko.observableArray([]);
        public selectedItemWork: KnockoutObservable<String> = ko.observable('');
        public isEnable: KnockoutObservable<boolean>;
        public isEditable: KnockoutObservable<boolean>;
        public historys: KnockoutObservableArray<IWorkDetail> = ko.observableArray([]);
        public currentCode: KnockoutObservable<String> = ko.observable('');
        public screenMode: KnockoutObservable<number> = ko.observable(SCREEN_MODE.UPDATE);
        public historyLocals: IHistory[] = [];
        public model: WorkDetail = new WorkDetail();
        public modelHistory: HistoryInfo = new HistoryInfo();
        public disable: KnockoutObservable<boolean> = ko.observable(true);

        created() {
            const vm = this;

            const inputListWork = {
                cId: vm.$user.companyId,
                items: [25, 26, 27, 28, 29]
            }

            vm.modelHistory.startDate.subscribe(() => {
                if (ko.unwrap(ko.unwrap(vm.modelHistory).startDate) === null) {
                    vm.disable(false);
                    vm.screenMode(SCREEN_MODE.NOT_HISTORY);
                } else {
                    vm.disable(true);
                }
            });
            vm.selectedItemWork.subscribe((itemId: string) => {
                vm.historys([]);
                vm.model.update({
                    historyId: ko.unwrap(vm.modelHistory.historyId),
                    itemId: '',
                    code: '',
                    name: '',
                    externalCode: ''
                });
                vm.changeItemId(itemId);
                vm.$errors('clear');
            });

            vm.currentCode.subscribe(() => {
                vm.reloadData();
                vm.screenMode(SCREEN_MODE.UPDATE);
                vm.screenMode.valueHasMutated();
            });

            vm.modelHistory.historyId.subscribe(() => {
                vm.reloadList();
                vm.$errors('clear');
            });

            vm.$blockui('invisible')
                .then(() => {
                    vm.$ajax('at', API.LIST_WORK_INFO, inputListWork)
                        .then((data: IWorkInfo[]) => {
                            if (data.length === 0) {
                                vm.screenMode(SCREEN_MODE.NOT_HISTORY);
                            } else {
                                vm.screenMode(SCREEN_MODE.UPDATE);
                                vm.itemWorks(data);
                                vm.selectedItemWork(data[0].itemId);
                            }
                        })
                })
                .then(() => {
                    vm.changeItemId();
                })
                .always(() => {
                    vm.$blockui('clear');
                });

            vm.isEnable = ko.observable(true);
            vm.isEditable = ko.observable(true);
            vm.modelHistory.startDate.valueHasMutated();
        }

        mounted() {
            const vm = this;
            vm.selectedItemWork.valueHasMutated();
            $('#combo-box').focus();
            vm.$errors('clear');
        }

        changeItemId(itemId?: string) {
            const vm = this;
            vm.$ajax('at', API.LIST_HISTORY)
                .then((history: IHistory[]) => {
                    if (history.length > 0) {
                        _.forEach(history, ((item: IHistory) => {
                            item.dateHistoryItems = _.orderBy(item.dateHistoryItems, ['startDate'], ['desc']);
                        }));
                        vm.historyLocals = history;
                        if (itemId) {
                            const exist = _.find(history, ((value: IHistory) => { return value.itemId === itemId }));
                            if (exist) {
                                if (exist.dateHistoryItems.length > 0) {
                                    vm.modelHistory.update(exist.dateHistoryItems[0]);
                                } else {
                                    vm.model.update({
                                        historyId: ko.unwrap(vm.modelHistory.historyId),
                                        itemId: '',
                                        code: '',
                                        name: '',
                                        externalCode: ''
                                    });
                                    vm.modelHistory.update({
                                        historyId: ko.unwrap(vm.modelHistory.historyId),
                                        startDate: null,
                                        endDate: null,
                                    })
                                }
                            } else {
                                vm.model.update({
                                    historyId: ko.unwrap(vm.modelHistory.historyId),
                                    itemId: '',
                                    code: '',
                                    name: '',
                                    externalCode: ''
                                });
                                vm.modelHistory.update({
                                    historyId: ko.unwrap(vm.modelHistory.historyId),
                                    startDate: null,
                                    endDate: null,
                                });
                            }
                        } else {
                            vm.modelHistory.update(history[0].dateHistoryItems[0]);
                            vm.modelHistory.historyId.valueHasMutated();
                        }
                    } else {
                        vm.screenMode(SCREEN_MODE.NOT_HISTORY);
                    }
                })
                .then(() => {
                    vm.modelHistory.historyId.valueHasMutated();
                });
        }

        reloadList(index?: number) {
            const vm = this;
            var listHistorys: IHistoryInfo[] = [];
            var historyDateils: IWorkDetail[] = [];
            let dfdGetAllData = $.Deferred();

            vm.$blockui('invisible')
                .then(() => {
                    _.forEach(vm.historyLocals, ((value) => {
                        if (value.itemId === ko.unwrap(vm.selectedItemWork)) {
                            _.forEach(value.dateHistoryItems, ((item: IHistoryInfo) => {
                                if (item.historyId === ko.unwrap(vm.modelHistory.historyId)) {
                                    listHistorys.push(item);
                                }
                            }))
                        }
                    }));
                })
                .then(() => {
                    _.forEach(listHistorys, ((item: IHistoryInfo) => {
                        vm.$ajax('at', API.WORK_INFO_DATAIL, { historyId: item.historyId })
                            .done((data: IWorkDetail[]) => {
                                _.forEach(data, ((value: IWorkDetail) => {
                                    historyDateils.push(value);
                                }));
                                dfdGetAllData.resolve();
                            })
                    }));
                })
                .always(() => vm.$blockui('clear'));
            $.when(dfdGetAllData)
                .done(() => {
                    vm.historys(_.orderBy(historyDateils, ['code'], ['asc']));
                    if (ko.unwrap(vm.historys).length > 0) {
                        if (index) {
                            vm.currentCode(ko.unwrap(vm.historys)[index].code);
                        } else {
                            vm.currentCode(historyDateils[0].code);
                        }
                    } else {
                        vm.currentCode('');
                        vm.model.update({
                            historyId: ko.unwrap(vm.modelHistory.historyId),
                            itemId: ko.unwrap(vm.model.itemId),
                            code: '',
                            name: '',
                            externalCode: ''
                        });
                        vm.screenMode(SCREEN_MODE.NEW);
                        // $('.inputCode').focus();
                    }
                    if (ko.unwrap(vm.currentCode) !== '') {
                        vm.currentCode.valueHasMutated();
                    }
                })
                .then(() => {
                    vm.$blockui('clear');
                });
        }

        reloadData() {
            const vm = this;
            const exist = _.find(ko.unwrap(vm.historys), ((value: IWorkDetail) => { return value.code === ko.unwrap(vm.currentCode) }));

            if (exist) {
                vm.model.update(exist);
                vm.screenMode(SCREEN_MODE.UPDATE);
            }
        }

        openViewL() {
            const vm = this;
            const exist = _.find(vm.historyLocals, ((value: IHistory) => { return value.itemId === ko.unwrap(vm.selectedItemWork) }));
            const param = {
                itemId: ko.unwrap(vm.selectedItemWork),
                history: exist
            }
            vm.$window.modal('at', '/view/kdw/006/l/index.xhtml', param)
                .then((hisId: string) => {
                    if (hisId) {
                        vm.$blockui('invisible')
                            .then(() => {
                                vm.$ajax(API.LIST_HISTORY)
                                    .done((history: IHistory[]) => {
                                        if (history.length > 0) {
                                            _.forEach(history, ((item: IHistory) => {
                                                item.dateHistoryItems = _.orderBy(item.dateHistoryItems, ['startDate'], ['desc']);
                                            }))
                                            vm.historyLocals = history;
                                        } else {
                                            vm.screenMode(SCREEN_MODE.NOT_HISTORY);
                                            vm.historyLocals = history;
                                        }
                                    })
                                    .then(() => {
                                        const exist = _.find(vm.historyLocals, ((item: IHistory) => {
                                            return item.itemId === ko.unwrap(vm.selectedItemWork);
                                        }));
                                        if (exist) {
                                            const history = _.find(exist.dateHistoryItems, ((value: IHistoryInfo) => {
                                                return value.historyId === hisId;
                                            }));
                                            if (history) {
                                                vm.modelHistory.update(history);
                                            }
                                        }
                                    })
                            })
                    } else {
                        vm.$ajax(API.LIST_HISTORY)
                            .done((history: IHistory[]) => {
                                if (history.length > 0) {
                                    _.forEach(history, ((item: IHistory) => {
                                        item.dateHistoryItems = _.orderBy(item.dateHistoryItems, ['startDate'], ['desc']);
                                    }))
                                    vm.historyLocals = history;
                                } else {
                                    vm.screenMode(SCREEN_MODE.NOT_HISTORY);
                                    vm.historyLocals = history;
                                }
                            })
                            .then(() => {
                                const exist = _.find(vm.historyLocals, ((item: IHistory) => {
                                    return item.itemId === ko.unwrap(vm.selectedItemWork);
                                }));
                                if (exist) {
                                    const history = _.find(exist.dateHistoryItems, ((value: IHistoryInfo) => {
                                        return value.historyId === ko.unwrap(ko.unwrap(vm.modelHistory).historyId);
                                    }));
                                    if (history) {
                                        vm.modelHistory.update(history);
                                    } else {
                                        vm.modelHistory.update(exist.dateHistoryItems[0]);
                                    }
                                } else {
                                    vm.model.update({
                                        historyId: ko.unwrap(vm.modelHistory.historyId),
                                        itemId: '',
                                        code: '',
                                        name: '',
                                        externalCode: ''
                                    });
                                    vm.modelHistory.update({
                                        historyId: ko.unwrap(vm.modelHistory.historyId),
                                        startDate: null,
                                        endDate: null,
                                    });
                                }
                            })
                    }
                })
                .always(() => vm.$blockui('clear'));
        }

        new() {
            const vm = this;
            vm.model.update({
                historyId: ko.unwrap(vm.modelHistory.historyId),
                itemId: ko.unwrap(vm.model.itemId),
                code: '',
                name: '',
                externalCode: ''
            });
            vm.screenMode(SCREEN_MODE.NEW);
            // $('.inputCode').focus();
        }

        addOrUpdate() {
            const vm = this;
            const param = {
                historyId: ko.unwrap(vm.model.historyId),
                choiceCode: ko.unwrap(vm.model.code),
                optionName: ko.unwrap(vm.model.name),
                eternalCodeOfChoice: ko.unwrap(vm.model.externalCode),
                itemId: ko.unwrap(vm.selectedItemWork)
            }
            const index = _.map(ko.unwrap(vm.historys), m => m.code).indexOf(ko.unwrap(vm.model.code));

            vm.validate()
                .then((valid: boolean) => {
                    if (valid) {
                        if (ko.unwrap(vm.screenMode) === SCREEN_MODE.NEW) {
                            vm.$blockui('invisible')
                                .then(() => {
                                    vm.$ajax('at', API.REGISTER, param)
                                        .done(() => {
                                            vm.$dialog.info({ messageId: 'Msg_15' });
                                            vm.reloadList();
                                            vm.screenMode(SCREEN_MODE.UPDATE);
                                        })
                                        .fail((data: any) => {
                                            vm.$dialog.info({ messageId: data.messageId });
                                        });
                                })
                                .always(() => {
                                    vm.$blockui('clear');
                                });
                        }
                        if (ko.unwrap(vm.screenMode) === SCREEN_MODE.UPDATE) {
                            vm.$blockui('invisible')
                                .then(() => {
                                    vm.$ajax('at', API.UPDATE, param)
                                        .done(() => {
                                            vm.$dialog.info({ messageId: 'Msg_15' });
                                            vm.reloadList(index);
                                        })
                                        .fail((data: any) => {
                                            vm.$dialog.error(data.messageId);
                                        });
                                })
                                .always(() => {
                                    vm.$blockui('clear');
                                });
                        }
                    }
                })
        }

        delete() {
            const vm = this;
            const param = {
                historyId: ko.unwrap(vm.model.historyId),
                choiceCode: ko.unwrap(vm.model.code)
            }, oldIndex = _.map(ko.unwrap(vm.historys), m => m.code).indexOf(ko.unwrap(vm.model.code)),
                newIndex = oldIndex == ko.unwrap(vm.historys).length - 1 ? oldIndex - 1 : oldIndex;

            nts.uk.ui.dialog
                .confirm({ messageId: "Msg_18" })
                .ifYes(() => {
                    vm.$blockui("invisible")
                        .then(() => {
                            vm.$ajax('at', API.REMOTE, param)
                                .then(() => vm.$dialog.info({ messageId: "Msg_16" }))
                                .then(() => vm.reloadList(newIndex))
                                .always(() => vm.$blockui('clear'))
                        })
                })
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
    }

    enum SCREEN_MODE {
        NEW = 1,
        UPDATE = 2,
        NOT_HISTORY = 3
    }

    interface IWorkInfo {
        /** 項目ID*/
        itemId: String;
        /** 名称*/
        name: String;
        /** フォーマット設定に表示する*/
        useAtr: boolean;
    }

    interface IHistory {
        /** 項目ID */
        itemId: String;
        /** 履歴項目 */
        dateHistoryItems: IHistoryInfo[];
    }

    interface IHistoryInfo {
        historyId: string;
        startDate: Date;
        endDate: Date;
    }

    class HistoryInfo {
        historyId: KnockoutObservable<string> = ko.observable('');
        startDate: KnockoutObservable<Date | null> = ko.observable(null);;
        endDate: KnockoutObservable<Date | null> = ko.observable(null);
        period: KnockoutObservable<String> = ko.observable('');

        constructor(param?: IHistoryInfo) {
            if (param) {
                this.historyId(param.historyId);
                this.startDate(param.startDate);
                this.endDate(param.endDate);
                if (param.startDate || param.endDate) {
                    this.period(param.startDate.toString() + ' ~ ' + param.endDate.toString());
                } else {
                    this.period('');
                }
            }
        }

        public update(param?: IHistoryInfo) {
            if (param) {
                this.historyId(param.historyId);
                this.startDate(param.startDate);
                this.endDate(param.endDate);
                if (param.startDate || param.endDate) {
                    this.period(param.startDate.toString() + ' ~ ' + param.endDate.toString());
                } else {
                    this.period('');
                }
            }
        }
    }

    interface IWorkDetail {
        /** 履歴ID */
        historyId: String;
        /** 項目ID */
        itemId: String;
        /** コード */
        code: String;
        /** 名称 */
        name: String;
        /** 外部コード */
        externalCode: String;
    }

    class WorkDetail {
        historyId: KnockoutObservable<String> = ko.observable('');
        itemId: KnockoutObservable<String> = ko.observable('');
        code: KnockoutObservable<String> = ko.observable('');
        name: KnockoutObservable<String> = ko.observable('');
        externalCode: KnockoutObservable<String> = ko.observable('');

        constructor(param?: IWorkDetail) {
            if (param) {
                this.historyId(param.historyId);
                this.itemId(param.itemId);
                this.code(param.code);
                this.name(param.name);
                this.externalCode(param.externalCode);
            }
        }

        public update(param?: IWorkDetail) {
            if (param) {
                this.historyId(param.historyId);
                this.itemId(param.itemId);
                this.code(param.code);
                this.name(param.name);
                this.externalCode(param.externalCode);
            }
        }
    }
}
