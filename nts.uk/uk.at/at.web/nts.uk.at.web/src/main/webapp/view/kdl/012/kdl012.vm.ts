/// <reference path="../../../lib/nittsu/viewcontext.d.ts" />
// import result = require("lodash/result");

module nts.uk.at.view.kdl012 {

    import getShared = nts.uk.ui.windows.getShared;
    import _date = moment.unitOfTime._date;

    const CONCAT_DATE = ' ～ ';
    const PATH = {
        getTaskMaster: 'at/shared/scherec/taskmanagement/taskmaster/tasks'
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        currentCode: KnockoutObservable<any> = ko.observable(null);
        currentCodeList: KnockoutObservableArray<any> = ko.observableArray([]);
        header: KnockoutObservableArray<any> = ko.observableArray([]);

        //input from parent screen
        isMultiple: boolean = true;
        isShowExpireDate: KnockoutObservable<boolean> = ko.observable(false);
        selectionCodeList: KnockoutObservableArray<string> = ko.observableArray([]);
        referenceDate: KnockoutObservable<string> = ko.observable();
        workFrameNoSelection: KnockoutObservable<number> = ko.observable(null);
        listHeight: KnockoutComputed<number>;
        sid: KnockoutObservable<number> = ko.observable(null);                        
        taskCode: KnockoutObservable<number> = ko.observable(null);
        
        constructor(params: ParamModel) {
            super();
            const vm = this;

            let object: ParamModel = getShared('KDL012Params') || params;

            if (object) {
                vm.isMultiple = object.isMultiple; //選択モード single or multiple
                vm.isShowExpireDate(object.showExpireDate); //表示モード	show/hide expire date
                vm.referenceDate(object.referenceDate ? object.referenceDate : moment().utc().add(1, "d").toISOString()); //システム日付
                vm.workFrameNoSelection(object.workFrameNoSelection);//作業枠NO選択
                vm.selectionCodeList(object.selectionCodeList);
                vm.currentCodeList(object.selectionCodeList); //初期選択コードリスト
                vm.currentCode(_.isEmpty(object.selectionCodeList) ? null : object.selectionCodeList[0]);
                vm.sid(object.sid);
                vm.taskCode(object.taskCode);
            }

            if (vm.isShowExpireDate()) {
                vm.header([
                    {headerText: vm.$i18n('KDL012_4'), prop: 'code', width: 200},
                    {
                        headerText: vm.$i18n('KDL012_5'),
                        prop: 'taskName',
                        width: 330,
                        columnCssClass: 'limited-label',
                        formatter: _.escape
                    },
                    {headerText: vm.$i18n('KDL012_6'), prop: 'expireDate', width: 210},
                    {
                        headerText: vm.$i18n('KDL012_7'),
                        prop: 'remark',
                        width: vm.isMultiple ? 200 : 215,
                        formatter: _.escape,
                        columnCssClass: 'limited-label'
                    }
                ]);
            } else {
                vm.header([
                    {headerText: vm.$i18n('KDL012_4'), prop: 'code', width: 200},
                    {
                        headerText: vm.$i18n('KDL012_5'),
                        prop: 'taskName',
                        width: 350,
                        columnCssClass: 'limited-label',
                        formatter: _.escape
                    },
                    {
                        headerText: vm.$i18n('KDL012_7'),
                        prop: 'remark',
                        width: vm.isMultiple ? 380 : 405,
                        formatter: _.escape,
                        columnCssClass: 'limited-label'
                    }
                ]);
            }

            vm.listHeight = ko.computed(() => {
                const isIE = !!document.documentMode;
                if (vm.isMultiple && isIE) {
                    return 244 + 25 + 1;
                }
                return 240 + 25 + 1;
            });
            //get data from api
            vm.getWorkFrameSetting();
        }

        created(params: any) {
            const vm = this;
        }

        mounted() {
            const vm = this;
        }

        proceed() {
            const vm = this;

            if (vm.isMultiple) {
                let selectionList: Array<any> = _.filter(vm.items(), (x) => {
                    return _.includes(vm.currentCodeList(), x.code)
                });

                if (selectionList.length === 0) {
                    vm.$dialog.error({messageId: 'Msg_2092'}).then(() => {
                    });
                } else {
                    let currentCodeList: Array<any> = selectionList.map(i => i.code);
                    nts.uk.ui.windows.setShared('KDL012Output', currentCodeList);
                    nts.uk.ui.windows.setShared('KDL012OutputList', selectionList);
                    //new
                    vm.$window.close({setShareKDL012: currentCodeList});
                }
            } else {
                let selectionList: Array<any> = _.filter(vm.items(), (x) => {
                    return _.isEqual(vm.currentCode(), x.code);
                });

                if (_.isEmpty(vm.currentCode()) || _.isEmpty(selectionList)) {
                    vm.$dialog.error({messageId: 'Msg_2092'});
                } else {
                    nts.uk.ui.windows.setShared('KDL012Output', vm.currentCode());
                    
                    nts.uk.ui.windows.setShared('KDL012OutputList', selectionList);
                    //new
                    vm.$window.close({setShareKDL012: vm.currentCode()});
                }
            }
        }

        closeDialog() {
            const vm = this;
            nts.uk.ui.windows.setShared('KDL012Cancel', null); //using for CDL023
            vm.$window.close({setShareKDL012: null});
        }

        private getWorkFrameSetting() {
            const vm = this;
            vm.$blockui('show');
            let date = moment(vm.referenceDate()).format("YYYY/MM/DD");
            let param : RequestModel = {baseDate: date, taskFrameNo: vm.workFrameNoSelection(), sid: vm.sid(), taskCode: vm.taskCode() };
            vm.$ajax(PATH.getTaskMaster, param).done((data: Array<ItemModel>) => {
                let result: Array<ItemModel> = [];
                if (data) {
                    data.forEach(item => {
                        result.push(new ItemModel(item.code, item.taskName, item.taskAbName, item.expirationStartDate, item.expirationEndDate, item.remark));
                    });
                    vm.items(result);
                }
                vm.$blockui('hide');
            }).fail((error) => {
                vm.$dialog.error({messageId: error.messageId}).then(() => {
                    vm.$blockui('hide');
                });
            });
        }
    }

    class ItemModel {
        code: string | number;
        taskName: string;
        taskAbName: string;
        expirationStartDate: string;
        expirationEndDate: string;
        expireDate: string;
        remark: string;

        constructor(code: string | number, taskName: string, taskAbName: string, expirationStartDate?: string, expirationEndDate?: string, remark?: string) {
            this.code = code;
            this.taskName = taskName;
            this.taskAbName = taskAbName;
            this.expirationStartDate = expirationStartDate;
            this.expirationEndDate = expirationEndDate;
            this.expireDate = expirationStartDate + CONCAT_DATE + expirationEndDate;
            this.remark = remark;
        }
    }

    interface RequestModel {
        baseDate: string;
        taskFrameNo: number;
        sid: string;//社員ID                         
        taskCode: string;
    }

    class ParamModel {
        isMultiple: boolean;
        showExpireDate: boolean;
        referenceDate: string;
        workFrameNoSelection: number;
        selectionCodeList: string[];
        currentCodeList: string[];
        sid: string;//社員ID                         
        taskCode: string; //上位枠作業コード

        constructor(isMultiple: boolean, showExpireDate: boolean, referenceDate: string, workFrameNoSelection: number,
                    selectionCodeList?: string[], currentCodeList?: string[], sid?: string, taskCode?: string) {
            this.isMultiple = isMultiple;
            this.showExpireDate = showExpireDate;
            this.referenceDate = referenceDate;
            this.workFrameNoSelection = workFrameNoSelection;
            this.selectionCodeList = selectionCodeList;
            this.currentCodeList = currentCodeList;
            this.sid = sid;
            this.taskCode = taskCode;
        }
    }
}