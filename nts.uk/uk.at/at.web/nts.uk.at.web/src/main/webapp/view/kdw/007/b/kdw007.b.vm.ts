module nts.uk.at.view.kdw007.b.viewmodel {

    export class ScreenModel {
        enumConditionAtr: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: "回数" },
            { code: 1, name: "時間" },
            { code: 2, name: "時刻" },
            { code: 3, name: "金額" },
        ]);

        enumConditionType: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: "固定値", enable: true },
            { code: 1, name: "勤怠項目", enable: true }
        ]);

        enumCompareOperator: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: "等しい（＝）" },
            { code: 1, name: "等しくない（≠）" },
            { code: 2, name: "より大きい（＞）" },
            { code: 3, name: "以上（≧）" },
            { code: 4, name: "より小さい（＜）" },
            { code: 5, name: "以下（≦）" },
            { code: 6, name: "範囲の間（境界値を含まない）（＜＞）" },
            { code: 7, name: "範囲の間（境界値を含む）（≦≧）" },
            { code: 8, name: "範囲の外（境界値を含まない）（＞＜）" },
            { code: 9, name: "範囲の外（境界値を含む）（≧≦）" }
        ]);

        currentAtdItemCondition: any;
        displayTargetAtdItems: KnockoutObservable<string> = ko.observable("");
        displayCompareAtdItems: KnockoutObservable<string> = ko.observable("");

        constructor() {
            let self = this,
                caic = self.currentAtdItemCondition,
                param = nts.uk.ui.windows.getShared("KDW007BParams");

            /*param.countableAddAtdItems = _.values(param.countableAddAtdItems || []);
            param.countableSubAtdItems = _.values(param.countableSubAtdItems || []);*/

            ko.utils.extend(param, {
                countableAddAtdItems: _.values(param.countableAddAtdItems || []),
                countableSubAtdItems: _.values(param.countableSubAtdItems || [])
            });

            self.currentAtdItemCondition = caic = ko.mapping.fromJS(param);

            caic.conditionAtr.subscribe(v => {

                caic.uncountableAtdItem(null);
                caic.countableAddAtdItems([]);
                caic.countableSubAtdItems([]);
                caic.conditionType(0);
                caic.compareOperator(0);
                caic.singleAtdItem(null);
                caic.compareStartValue(0);
                caic.compareEndValue(0);

                self.fillTextDisplayTarget();
                self.fillTextDisplayComparison();
            });

            caic.compareOperator.subscribe((value) => {
                if (value > 5) {
                    self.enumConditionType([
                        { code: 0, name: "固定値", enable: true },
                        { code: 1, name: "勤怠項目", enable: false }
                    ]);

                    caic.conditionType(0);
                } else {
                    self.enumConditionType([
                        { code: 0, name: "固定値", enable: true },
                        { code: 1, name: "勤怠項目", enable: true }
                    ]);
                }
            });

            caic.conditionType.subscribe((value) => {
                if (value === 0) {
                    $('#display-compare-item').ntsError('clear');
                    $(".value-input").trigger("validate");
                } else {
                    $('.value-input').ntsError('clear');
                    $("#display-compare-item").trigger("validate");
                }
            });

            caic.conditionAtr.subscribe((value) => {
                $(".value-input").ntsError("clear");
                caic.compareStartValue(0);
                caic.compareEndValue(0);
            });

            caic.compareOperator.subscribe((value) => {
                self.validateRange();
            });

            $(".value-input").blur(() => {
                self.validateRange();
            });

            self.fillTextDisplayTarget();
            self.fillTextDisplayComparison();

            // validate
            caic.compareStartValue.subscribe(v => {
                let s = ko.toJS(caic.compareStartValue),
                    e = ko.toJS(caic.compareEndValue),
                    t = ko.toJS(caic.compareOperator);

                nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_927');
                nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');

                setTimeout(() => {
                    switch (t) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            if (s > e || s == e) {
                                nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_927');
                                nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
                                $('#startValue').ntsError('set', { messageId: "Msg_927" });
                            }
                            else {
                            }
                        case 7:
                            if (s > e) {
                                nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_927');
                                nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
                                $('#startValue').ntsError('set', { messageId: "Msg_927" });
                            }
                            else {
                            }
                        case 8:
                            if (s > e || s == e) {
                                nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_927');
                                nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
                                $('#startValue').ntsError('set', { messageId: "Msg_927" });
                            }
                            else {
                            }
                        case 9:
                            if (s > e) {
                                nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_927');
                                nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
                                $('#startValue').ntsError('set', { messageId: "Msg_927" });
                            }
                            else {
                            }
                    }
                }, 25);
            });

            caic.compareEndValue.subscribe(v => caic.compareStartValue.valueHasMutated());
        }

        fillTextDisplayTarget() {
            let self = this;
            self.displayTargetAtdItems("");
            if (self.currentAtdItemCondition.conditionAtr() === 2) {
                if (self.currentAtdItemCondition.uncountableAtdItem()) {
                    service.getAttendanceItemByCodes([self.currentAtdItemCondition.uncountableAtdItem()]).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            self.displayTargetAtdItems(lstItems[0].attendanceItemName);
                            $("#display-target-item").trigger("validate");
                        }
                    });
                }
            } else {
                if (self.currentAtdItemCondition.countableAddAtdItems().length > 0) {
                    service.getAttendanceItemByCodes(self.currentAtdItemCondition.countableAddAtdItems()).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            for (let i = 0; i < lstItems.length; i++) {
                                let operator = (i === (lstItems.length - 1)) ? "" : " + ";
                                self.displayTargetAtdItems(self.displayTargetAtdItems() + lstItems[i].attendanceItemName + operator);
                            }
                            $("#display-target-item").trigger("validate");
                        }
                    }).then(() => {
                        if (self.currentAtdItemCondition.countableSubAtdItems().length > 0) {
                            service.getAttendanceItemByCodes(self.currentAtdItemCondition.countableSubAtdItems()).done((lstItems) => {
                                if (lstItems && lstItems.length > 0) {
                                    for (let i = 0; i < lstItems.length; i++) {
                                        let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                        let beforeOperator = (i === 0) ? " - " : "";
                                        self.displayTargetAtdItems(self.displayTargetAtdItems() + beforeOperator + lstItems[i].attendanceItemName + operator);
                                    }
                                    $("#display-target-item").trigger("validate");
                                }
                            })
                        }
                    });
                } else if (self.currentAtdItemCondition.countableSubAtdItems().length > 0) {
                    service.getAttendanceItemByCodes(self.currentAtdItemCondition.countableSubAtdItems()).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            for (let i = 0; i < lstItems.length; i++) {
                                let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                let beforeOperator = (i === 0) ? " - " : "";
                                self.displayTargetAtdItems(self.displayTargetAtdItems() + beforeOperator + lstItems[i].attendanceItemName + operator);
                            }
                            $("#display-target-item").trigger("validate");
                        }
                    })
                }

            }
        }

        fillTextDisplayComparison() {
            let self = this;
            self.displayCompareAtdItems("");
            if (self.currentAtdItemCondition.singleAtdItem()) {
                service.getAttendanceItemByCodes([self.currentAtdItemCondition.singleAtdItem()]).done((lstItems) => {
                    if (lstItems && lstItems.length > 0) {
                        self.displayCompareAtdItems(lstItems[0].attendanceItemName);
                        $("#display-compare-item").trigger("validate");
                    }
                });
            }
        }

        getListItemByAtr() {
            let self = this;
            let dfd = $.Deferred<any>();
            if (self.currentAtdItemCondition.conditionAtr() === 0) {
                //With type 回数 - Times
                service.getAttendanceItemByAtr(2).done((lstAtdItem) => {
                    service.getOptItemByAtr(1).done((lstOptItem) => {
                        for (let i = 0; i < lstOptItem.length; i++) {
                            lstAtdItem.push(lstOptItem[i]);
                        }
                        dfd.resolve(lstAtdItem);
                    });
                });
            } else if (self.currentAtdItemCondition.conditionAtr() === 1) {
                //With type 時間 - Time
                service.getAttendanceItemByAtr(5).done((lstAtdItem) => {
                    service.getOptItemByAtr(0).done((lstOptItem) => {
                        for (let i = 0; i < lstOptItem.length; i++) {
                            lstAtdItem.push(lstOptItem[i]);
                        }
                        dfd.resolve(lstAtdItem);
                    });
                });
            } else if (self.currentAtdItemCondition.conditionAtr() === 2) {
                //With type 時刻 - TimeWithDay
                service.getAttendanceItemByAtr(6).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (self.currentAtdItemCondition.conditionAtr() === 3) {
                //With type 金額 - AmountMoney
                service.getAttendanceItemByAtr(3).done((lstAtdItem) => {
                    service.getOptItemByAtr(2).done((lstOptItem) => {
                        for (let i = 0; i < lstOptItem.length; i++) {
                            lstAtdItem.push(lstOptItem[i]);
                        }
                        dfd.resolve(lstAtdItem);
                    });
                });
            }
            return dfd.promise();
        }

        openSelectAtdItemDialogTarget() {
            let self = this;
            self.getListItemByAtr().done((lstItem) => {
                let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                if (self.currentAtdItemCondition.conditionAtr() === 2) {
                    //Open dialog KDL021
                    nts.uk.ui.windows.setShared('Multiple', false);
                    nts.uk.ui.windows.setShared('AllAttendanceObj', lstItemCode);
                    nts.uk.ui.windows.setShared('SelectedAttendanceId', [self.currentAtdItemCondition.uncountableAtdItem()]);
                    nts.uk.ui.windows.sub.modal("at", "/view/kdl/021/a/index.xhtml").onClosed(() => {
                        let output = nts.uk.ui.windows.getShared("selectedChildAttendace");
                        if (output) {
                            self.currentAtdItemCondition.uncountableAtdItem(parseInt(output));
                            self.fillTextDisplayTarget();
                        }
                    });
                } else {
                    //Open dialog KDW007C
                    let param = {
                        lstAllItems: lstItemCode,
                        lstAddItems: self.currentAtdItemCondition.countableAddAtdItems(),
                        lstSubItems: self.currentAtdItemCondition.countableSubAtdItems()
                    };
                    nts.uk.ui.windows.setShared("KDW007Params", param);
                    nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/c/index.xhtml").onClosed(() => {
                        let output = nts.uk.ui.windows.getShared("KDW007CResults");
                        if (output) {
                            self.currentAtdItemCondition.countableAddAtdItems(output.lstAddItems.map((item) => { return parseInt(item); }));
                            self.currentAtdItemCondition.countableSubAtdItems(output.lstSubItems.map((item) => { return parseInt(item); }));
                            self.fillTextDisplayTarget();
                        }
                    });
                }
            });
        }

        openSelectAtdItemDialogComparison() {
            let self = this;
            //Open dialog KDL021
            self.getListItemByAtr().done((lstItem) => {
                let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                nts.uk.ui.windows.setShared('Multiple', false);
                // example wait
                nts.uk.ui.windows.setShared('AllAttendanceObj', lstItemCode);
                nts.uk.ui.windows.setShared('SelectedAttendanceId', [self.currentAtdItemCondition.singleAtdItem()]);
                nts.uk.ui.windows.sub.modal("at", "/view/kdl/021/a/index.xhtml").onClosed(() => {
                    let output = nts.uk.ui.windows.getShared("selectedChildAttendace");
                    if (output) {
                        self.currentAtdItemCondition.singleAtdItem(parseInt(output));
                        self.fillTextDisplayComparison();
                    }
                });
            });
        }

        validateRange() {
            let self = this,
                caic = ko.toJS(self.currentAtdItemCondition);

            $('.value-input').ntsError('clear');
            //$(".value-input").trigger("validate");

            if (caic.conditionType === 0 && [7, 9].indexOf(caic.compareOperator) > -1) {
                setTimeout(() => {
                    if (parseInt(caic.compareStartValue) > parseInt(caic.compareEndValue)) {
                        $('#startValue').ntsError('set', { messageId: "Msg_927" });
                        $('#endValue').ntsError('set', { messageId: "Msg_927" });
                    }
                }, 25);
            } else if (caic.conditionType === 0 && [6, 8].indexOf(caic.compareOperator) > -1) {
                setTimeout(() => {
                    if (parseInt(caic.compareStartValue) >= parseInt(caic.compareEndValue)) {
                        $('#startValue').ntsError('set', { messageId: "Msg_927" });
                        $('#endValue').ntsError('set', { messageId: "Msg_927" });
                    }
                }, 25);
            }
        }

        returnData() {
            let self = this;

            $(".need-check").trigger("validate");
            self.validateRange();

            if (!nts.uk.ui.errors.hasError()) {
                let param = ko.mapping.toJS(self.currentAtdItemCondition);
                param.countableAddAtdItems = _.values(param.countableAddAtdItems);
                param.countableSubAtdItems = _.values(param.countableSubAtdItems);
                nts.uk.ui.windows.setShared('KDW007BResult', param);
                nts.uk.ui.windows.close();
            }
        }

        closeDialog() {
            nts.uk.ui.windows.close();
        }

        startPage(): JQueryPromise<any> {
            var dfd = $.Deferred();
            dfd.resolve();
            return dfd.promise();
        }
    }

    export class ErAlAtdItemCondition {

        targetNO: KnockoutObservable<number>;
        conditionAtr: KnockoutObservable<number>;
        useAtr: KnockoutObservable<boolean>;
        uncountableAtdItem: KnockoutObservable<number>;
        countableAddAtdItems: KnockoutObservableArray<number>;
        countableSubAtdItems: KnockoutObservableArray<number>;
        conditionType: KnockoutObservable<number>;
        compareOperator: KnockoutObservable<number>;
        singleAtdItem: KnockoutObservable<number>;
        compareStartValue: KnockoutObservable<number>;
        compareEndValue: KnockoutObservable<number>;

        constructor(param) {
            this.targetNO = ko.observable(param.targetNO);
            this.conditionAtr = ko.observable(param.conditionAtr);
            this.useAtr = ko.observable(param.useAtr);
            this.uncountableAtdItem = ko.observable(param.uncountableAtdItem);
            this.countableAddAtdItems = ko.observableArray(param.countableAddAtdItems);
            this.countableSubAtdItems = ko.observableArray(param.countableSubAtdItems);
            this.conditionType = ko.observable(param.conditionType);
            this.singleAtdItem = ko.observable(param.singleAtdItem);
            this.compareStartValue = ko.observable(param.compareStartValue);
            this.compareEndValue = ko.observable(param.compareEndValue);
            this.compareOperator = ko.observable(param.compareOperator);

            this.compareEndValue.subscribe(v => {
                console.log(v);
            });
        }
    }

}