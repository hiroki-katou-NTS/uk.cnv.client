/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
// import result = require("lodash/result");

module kdl012.test.viewmodel {
    import getShared = nts.uk.ui.windows.getShared;

    @bean()
    export class ScreenMode extends ko.ViewModel {
        roundingModes: KnockoutObservableArray<any>;
        selectedModeCode: any;
        showExpireDateOptions: KnockoutObservableArray<any>;
        selectedShowExpireDateCode: any;

        isMultiple: KnockoutObservable<boolean>;
        showExpireDate: KnockoutObservable<boolean>;
        workFrameNoSelection: KnockoutObservable<number>;
        referenceDate: KnockoutObservable<string>
        selectionCodeTxt: KnockoutObservable<string>
        selectionCodeList: KnockoutObservableArray<string>;
        items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        header: KnockoutObservableArray<any> = ko.observableArray([]);
        // selectionTaskList: KnockoutObservableArray<ItemModel> = ko.observableArray([]);

        currentCodeList: KnockoutObservableArray<any> = ko.observableArray([]);
        
        sid: KnockoutObservable<string>
        taskCode: KnockoutObservable<string>

        constructor() {
            super()
            let self = this;
            self.roundingModes = ko.observableArray([
                {code: '0', name: 'Single'},
                {code: '1', name: 'Multiple'}
            ]);
            self.selectedModeCode = ko.observable(0);
            self.isMultiple = ko.observable(self.selectedModeCode() === '1');
            self.showExpireDateOptions = ko.observableArray([
                {code: '0', name: 'Not show expireDate'},
                {code: '1', name: 'Show ExpireDate'}
            ]);
            self.selectedShowExpireDateCode = ko.observable(1);

            self.workFrameNoSelection = ko.observable(1);
            self.referenceDate = ko.observable(moment.utc().format("YYYY/MM/DD"));
            self.showExpireDate = ko.observable(false);
            self.selectionCodeTxt = ko.observable("B0000000000000000001, B0000000000000000002");
            self.selectionCodeList = ko.observableArray([]);

            self.header([
                {headerText: self.$i18n('KDL012_4'), prop: 'code', width: 150},
                {
                    headerText: self.$i18n('KDL012_5'),
                    prop: 'taskName',
                    width: 150,
                    columnCssClass: 'limited-label',
                    formatter: _.escape
                },
                {headerText: self.$i18n('KDL012_6'), prop: 'expireDate', width: 250},
                {
                    headerText: self.$i18n('KDL012_7'),
                    prop: 'remark',
                    width: 100,
                    formatter: _.escape,
                    columnCssClass: 'limited-label'
                }
            ]);
            
            self.sid = ko.observable(__viewContext.user.employeeId);
            self.taskCode = ko.observable('FS0001              ');

            self.startPage();
        }

        startPage(): JQueryPromise<any> {
            let dfd = $.Deferred();
            dfd.resolve();

            return dfd.promise();
        }

        openDialog() {
            let self = this;
            nts.uk.ui.block.invisible();
            self.$validate(".nts-input").then((valid: boolean) => {
                if (!valid) {
                    return;
                }

                if (self.selectionCodeTxt() !== undefined) {
                    let temp = _.split(self.selectionCodeTxt(), ", ");
                    // If isMultiple = false
                    if (self.selectedModeCode() == '0') {
                        let tempArr: string[] = [];
                        tempArr.push(temp[0]);
                        self.selectionCodeList(tempArr);
                    }
                    else {
                        self.selectionCodeList(temp);
                    }
                }

                let request = {
                    isMultiple: self.selectedModeCode() === "1",
                    showExpireDate: self.selectedShowExpireDateCode() === "1",
                    workFrameNoSelection: self.workFrameNoSelection(),
                    referenceDate: moment(self.referenceDate()).format("YYYY/MM/DD"),
                    selectionCodeList: self.selectionCodeList(),
                    sid: self.sid(),
                    taskCode: self.taskCode()
                };
                nts.uk.ui.windows.setShared('KDL012Params', request);

                nts.uk.ui.windows.sub.modal("/view/kdl/012/index.xhtml", {dialogClass: "no-close"}).onClosed(() => {
                    let self = this;
                    let curentCode: any = getShared('KDL012Output');
                    let selectionTaskList: any = getShared('KDL012OutputList');

                    if (curentCode !== undefined) {
                        if (_.isArray(curentCode)) {
                            self.selectionCodeList(curentCode);
                            self.currentCodeList(curentCode);
                            self.selectionCodeTxt(curentCode.join(', '));
                        } else {
                            self.selectionCodeList([curentCode]);
                            self.currentCodeList([curentCode]);
                            self.selectionCodeTxt(curentCode.toString());
                        }
                    } else {
                        self.selectionCodeList([]);
                        self.selectionCodeTxt("");
                        self.currentCodeList([]);
                    }

                    if (selectionTaskList.length > 0 && selectionTaskList !== undefined) {
                        self.items(selectionTaskList);
                    }
                    nts.uk.ui.block.clear();
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

        constructor(code: string | number, taskName: string, expirationStartDate?: string, expirationEndDate?: string, remark?: string) {
            this.code = code;
            this.taskName = taskName;
            this.taskAbName = this.taskAbName;
            this.expirationStartDate = expirationStartDate;
            this.expirationEndDate = expirationEndDate;
            this.expireDate = expirationStartDate + ' ～ ' + expirationEndDate;
            this.remark = remark;
        }
    }
}