module nts.uk.com.view.cps001.g.vm {
    import getText = nts.uk.resource.getText;
    import info = nts.uk.ui.dialog.info;
    import alert = nts.uk.ui.dialog.alert;
    import block = nts.uk.ui.block.grayout;
    import unblock = nts.uk.ui.block.clear;
    import clearError = nts.uk.ui.errors.clearAll;
    import error = nts.uk.ui.dialog.alertError;
    import getShared = nts.uk.ui.windows.getShared;
    export class ScreenModel {

        // Store create/update mode
        createMode: KnockoutObservable<boolean>;
        columns: KnockoutObservableArray<any>;
        currentValue: KnockoutObservable<string> = ko.observable('');
        date: KnockoutObservable<string>;
        listExpirationStatus: KnockoutObservableArray<any>;
        value: KnockoutObservable<number>;
        checked: KnockoutObservable<boolean>;

        alllist: KnockoutObservableArray<AnnualLeaveGrantRemainingData> = ko.observableArray([]);

        listAnnualLeaveGrantRemainData: KnockoutObservableArray<AnnualLeaveGrantRemainingData> = ko.observableArray([]);
        currentItem: KnockoutObservable<AnnualLeaveGrantRemainingData> = ko.observable(new AnnualLeaveGrantRemainingData(<IAnnualLeaveGrantRemainingData>{}));
        grantMinutesH: KnockoutObservable<boolean>;
        usedMinutesH: KnockoutObservable<boolean>;
        remainingMinutesH: KnockoutObservable<boolean>;

        //
        nameDateGrantInp: KnockoutObservable<string> = ko.observable('');
        nameDeadlineDateInp: KnockoutObservable<string> = ko.observable('');

        nameDayNumberOfGrant: KnockoutObservable<string> = ko.observable('');
        namegrantTime: KnockoutObservable<string> = ko.observable('');

        nameDayNumberOfUse: KnockoutObservable<string> = ko.observable('');
        nameUseTime: KnockoutObservable<string> = ko.observable('');

        nameDayNumberOfRemain: KnockoutObservable<string> = ko.observable('');
        nameTimeReam: KnockoutObservable<string> = ko.observable('');

        nameExpiredAnnualLeave: KnockoutObservable<string> = ko.observable('');
        nameAnnualUseStatus: KnockoutObservable<string> = ko.observable('');

        itemDefs: any = [];

        sid: KnockoutObservable<string> = ko.observable(null);

        constructor() {
            let _self = this,
                data: any = getShared('CPS001GHI_VALUES');
            _self.sid(data.sid);

            _self.createMode = ko.observable(null);

            _self.checked = ko.observable(false);
            _self.listExpirationStatus = ko.observableArray([
                { code: EXPIRED_STATUS.AVAILABLE, name: '使用可能' },
                { code: EXPIRED_STATUS.EXPIRED, name: '期限切れ' }
            ]);

            // Subsribe table
            _self.currentValue.subscribe(value => {
                if (value) {
                    clearError();
                    _self.createMode(false);
                    service.getDetail(value).done((result: IAnnualLeaveGrantRemainingData) => {
                        if (result) {
                            let x: IAnnualLeaveGrantRemainingData = result;
                            if (x.grantMinutes == null) x.grantMinutes = 0;
                            if (x.usedMinutes == null) x.usedMinutes = 0;
                            if (x.remainingMinutes == null) x.remainingMinutes = 0;
                            _self.currentItem(new AnnualLeaveGrantRemainingData(x));
                            _self.loadItemDef();
                        }
                        clearError();
                        $('#idGrantDate').focus();
                    });

                }
            });

            // Subscribe checkbox
            _self.checked.subscribe(value => {
                _self.changeFollowExpSta(value);

                if (_self.listAnnualLeaveGrantRemainData().length) {
                    _self.createMode(false);
                    // Set focus
                    let index = _.findIndex(_self.listAnnualLeaveGrantRemainData(), (item) => { return item.annLeavID == _self.currentValue(); });

                    if (index == -1) {
                        _self.currentValue(_self.listAnnualLeaveGrantRemainData()[0].annLeavID);
                    }
                } else {
                    _self.create();
                }

                $('#idGrantDate').focus();
            });


        }

        /**
         * Run after page loaded
         */
        public startPage(annID?: string): JQueryPromise<any> {
            let _self = this;
            block();
            _self.getItemDef().done(() => {
                _self.alllist.removeAll();
                _self.listAnnualLeaveGrantRemainData.removeAll();
                service.getAllList(_self.sid()).done((data: Array<IAnnualLeaveGrantRemainingData>) => {
                    if (data && data.length > 0) {
                        // Set to update mode
                        _self.createMode(false);
                        _self.alllist(data);
                        _self.changeFollowExpSta(_self.checked());

                        let currentIndex = _.findIndex(_self.listAnnualLeaveGrantRemainData(), function (item: IAnnualLeaveGrantRemainingData) {
                            return item.annLeavID == annID;
                        });
                        // Set focus
                        if (annID && currentIndex > 0) {
                            _self.currentValue(annID);
                        } else if (_self.listAnnualLeaveGrantRemainData().length > 0) {
                            _self.currentValue(_self.listAnnualLeaveGrantRemainData()[0].annLeavID);
                        } else {
                            _self.create();
                        }
                    } else {
                        // Set to cr eate mode
                        _self.create();
                    }


                    unblock();
                }).fail((_data) => {
                    unblock();
                });
            });
        }

        changeFollowExpSta(value: boolean) {
            let _self = this;

            if (value) {
                _self.listAnnualLeaveGrantRemainData(_self.alllist());
            } else {
                _self.listAnnualLeaveGrantRemainData(_.filter(_self.alllist(), function (item) {
                    return item.expirationStatus === EXPIRED_STATUS.AVAILABLE;
                }));
            }
        }

        loadItemDef() {
            let _self = this;
            if (_self.itemDefs.length > 0) {
                _self.setItemDefValue(_self.itemDefs);
            }
        }
        getItemDef(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            if (self.itemDefs.length > 0) {
                self.setItemDefValue(self.itemDefs);
                dfd.resolve();
            } else {
                service.getItemDef().done((data) => {
                    if (!data[6].display && !data[9].display && !data[12].display) {
                        var currentDialog = nts.uk.ui.windows.getSelf();
                        currentDialog.$dialog.dialog('option', 'width', 595);
                    }

                    self.itemDefs = data;
                    self.setItemDefValue(data).done(() => {
                        self.setGridList();
                    });
                    dfd.resolve();
                });
            }
            return dfd.promise();
        }
        setItemDefValue(data: any): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            $("td[data-itemCode]").each(function () {
                let itemCodes = $(this).attr('data-itemCode');
                if (itemCodes) {
                    let itemCodeArray = itemCodes.split(" ");
                    _.forEach(itemCodeArray, (itemCode) => {
                        let itemDef = _.find(data, (item) => {
                            return item.itemCode == itemCode;
                        });
                        if (itemDef) {
                            if (!['IS00385', 'IS00386', 'IS00387', 'IS00388'].includes(itemDef.itemCode)) {
                                if (itemDef.display) {
                                    $(this).children().first().html("<label>" + itemDef.itemName + "</label>");
                                } else {
                                    $(this).parent().css("display", "none");
                                }
                            }
                            let timeType = itemCodeArray[itemCodeArray.length - 1];
                            switch (timeType) {
                                case "IS00385":
                                    self.nameDateGrantInp(itemDef.itemName);
                                    break;
                                case "IS00386":
                                    self.nameDeadlineDateInp(itemDef.itemName);
                                    break;
                                case "IS00387":
                                    self.nameExpiredAnnualLeave(itemDef.itemName);
                                    break;
                                case "IS00388":
                                    self.nameAnnualUseStatus(itemDef.itemName);
                                    break;
                                case "IS00390":
                                    self.nameDayNumberOfGrant(itemDef.itemName);
                                    break;
                                case "IS00393":
                                    self.nameDayNumberOfUse(itemDef.itemName);
                                    break;
                                case "IS00396":
                                    self.nameDayNumberOfRemain(itemDef.itemName);
                                    break;
                                case "grantMinutes":
                                    self.grantMinutesH = ko.observable(!itemDef.display);
                                    self.namegrantTime(itemDef.itemName);
                                    break;
                                case "usedMinutes":
                                    self.usedMinutesH = ko.observable(!itemDef.display);
                                    self.nameUseTime(itemDef.itemName);
                                    break;
                                case "remainingMinutes":
                                    self.remainingMinutesH = ko.observable(!itemDef.display);
                                    self.nameTimeReam(itemDef.itemName);
                                    break;

                            }
                        }
                    });
                }
                dfd.resolve();
            });

            return dfd.promise();
        }
        setGridList() {
            let self = this;
            self.columns = ko.observableArray([
                { type: 'string', key: 'annLeavID', hidden: true },
                { headerText: getText('CPS001_118'), type: 'date', key: 'grantDate', width: 100 },
                { headerText: getText('CPS001_119'), type: 'date', key: 'deadline', width: 100 },
                { headerText: getText('CPS001_120'), formatter: formatDate, key: 'grantDays', width: 80 },
                { headerText: getText('CPS001_128'), key: 'grantMinutes', formatter: formatTime, width: 80, hidden: self.grantMinutesH() },
                { headerText: getText('CPS001_121'), formatter: formatDate, key: 'usedDays', width: 80 },
                { headerText: getText('CPS001_122'), key: 'usedMinutes', formatter: formatTime, width: 80, hidden: self.usedMinutesH() },
                { headerText: getText('CPS001_123'), formatter: formatDate, key: 'remainingDays', width: 80 },
                { headerText: getText('CPS001_124'), key: 'remainingMinutes', formatter: formatTime, width: 100, hidden: self.remainingMinutesH() },
                { headerText: getText('CPS001_129'), formatter: formatEnum, key: 'expirationStatus', width: 100 }
            ]);
            let table: string = '<table tabindex="6" id="single-list" data-bind="ntsGridList: { dataSource: listAnnualLeaveGrantRemainData,  primaryKey: \'annLeavID\', columns: columns, multiple: false,value: currentValue, rows:10}"></table>';
            $("#tbl").html(table);
            ko.applyBindings(self, $("#tbl")[0]);
        }

        public create(): void {
            let _self = this;
            _self.createMode(true);
            _self.currentItem(new AnnualLeaveGrantRemainingData(<IAnnualLeaveGrantRemainingData>{
                expirationStatus: EXPIRED_STATUS.AVAILABLE
            }));
            _self.loadItemDef();
            _self.currentValue('');
            $('#idGrantDate').focus();
            clearError();
        }
        /**
         * Save sequence
         */
        public save(): void {
            let _self = this,
                command = ko.toJS(_self.currentItem());

            $("#grantDate").trigger("validate");
            $("#deadline").trigger("validate");
            $("#grantDays").trigger("validate");
            $("#usedDays").trigger("validate");
            $("#remainingDays").trigger("validate");

            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            block();
            if (_self.createMode()) {

                service.add(command).done((data: Array<string>) => {
                    info({ messageId: "Msg_15" }).then(function () {
                        _self.startPage(data[0]);
                    });
                    unblock();
                }).fail((res) => {
                    if (res.messageId == 'Msg_1023') {
                        $('#idGrantDate').ntsError('set', { messageId: res.messageId });
                    } else if (res.messageId == 'Msg_1456') {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    }
                    unblock();
                });
            } else {
                service.update(command).done((data) => {
                    info({ messageId: "Msg_15" }).then(function () {
                        _self.startPage(ko.toJS(_self.currentItem()).annLeavID);
                    });
                    unblock();
                }).fail((res) => {
                    if (res.messageId == 'Msg_1456') {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    } else if (res.messageId != 'Msg_1023') {
                        $('#idGrantDate').ntsError('set', { messageId: res.messageId });
                    }
                    unblock();
                });
            }
        }

        /**
         * Close this dialog
         */
        public close(): void {
            nts.uk.ui.windows.close();
        }

        /**
         * Remove sequence
         */
        public remove(): void {
            let _self = this,
                currentIndex = _.findIndex(_self.listAnnualLeaveGrantRemainData(), function (item: IAnnualLeaveGrantRemainingData) {
                    return item.annLeavID == ko.toJS(_self.currentItem()).annLeavID;
                }),
                finalIndex = _self.listAnnualLeaveGrantRemainData().length - 1;
            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            nts.uk.ui.dialog.confirm({ messageId: "Msg_18" })
                .ifYes(() => {
                    block();
                    let command = {
                        annLeavID: ko.toJS(_self.currentItem()).annLeavID,
                        employeeId: _self.sid(),
                        grantDate: ko.toJS(_self.currentItem()).grantDate
                    };
                    service.deleteLeav(command).done((message: string) => {
                        info({ messageId: "Msg_16" }).then(function () {

                            // set focus
                            if (currentIndex === 0 && currentIndex === finalIndex) {
                                _self.create();
                            } else if (currentIndex !== 0 && currentIndex === finalIndex) {
                                currentIndex = currentIndex - 1;
                            } else {
                                currentIndex = currentIndex + 1;
                            }
                            _self.startPage(_self.listAnnualLeaveGrantRemainData()[currentIndex].annLeavID);
                        });
                        unblock();
                    }).fail((error: any) => {
                        unblock();
                    });
                }).ifNo(() => {
                    // Nothing happen
                });
        }
    }
    export interface IAnnualLeaveGrantRemainingData {
        annLeavID: string;
        employeeId: string;
        grantDate: Date;
        deadline: Date;
        expirationStatus: number;
        grantDays: number;
        grantMinutes: number;
        usedDays: number;
        usedMinutes: number;
        remainingDays: number;
        remainingMinutes: number;
    }

    export class AnnualLeaveGrantRemainingData {
        annLeavID: KnockoutObservable<string> = ko.observable(null);
        employeeId: KnockoutObservable<string> = ko.observable(null);
        grantDate: KnockoutObservable<Date> = ko.observable(null);
        deadline: KnockoutObservable<Date> = ko.observable(null);
        expirationStatus: KnockoutObservable<number> = ko.observable(null);
        grantDays: KnockoutObservable<number> = ko.observable(null);
        grantMinutes: KnockoutObservable<number> = ko.observable(null);
        usedDays: KnockoutObservable<number> = ko.observable(null);
        usedMinutes: KnockoutObservable<number> = ko.observable(null);
        remainingDays: KnockoutObservable<number> = ko.observable(null);
        remainingMinutes: KnockoutObservable<number> = ko.observable(null);
        constructor(param?: IAnnualLeaveGrantRemainingData) {
            let self = this,
                data: any = getShared('CPS001GHI_VALUES');
            if (param) {
                self.annLeavID(param.annLeavID || null);
                self.grantDate(moment.utc(param.grantDate, "YYYY/MM/DD"));
                self.deadline(moment.utc(param.deadline, "YYYY/MM/DD"));
                self.expirationStatus(param.expirationStatus);
                self.grantDays(param.grantDays);
                self.grantMinutes(param.grantMinutes);
                self.usedDays(param.usedDays);
                self.usedMinutes(param.usedMinutes);
                self.remainingDays(param.remainingDays);
                self.remainingMinutes(param.remainingMinutes);
                self.employeeId(data.sid);
            }
            // Subcribe grantDate
            self.grantDate.subscribe(value => {
                if (value && __viewContext.viewModel.createMode()) {

                    if (nts.uk.ui.errors.hasError()) {
                        return;
                    }
                    service.lostFocus(value).done((data: Date) => {
                        if (data) {
                            self.deadline(moment.utc(data, "YYYY/MM/DD"));
                        }
                    });
                }
            });
        }
    }

    enum EXPIRED_STATUS {
        AVAILABLE = 1,
        EXPIRED = 0
    }
    function formatTime(value, row) {
        if (value) {
            let hour = Math.floor(Math.abs(value) / 60);
            let minutes = Math.floor(Math.abs(value) % 60);
            let result = hour + ':' + (minutes < 10 ? ("0" + minutes) : minutes);
            return value >= 0 ? "&nbsp;" + result : '-' + result;
        } else {
            return "&nbsp;0:00";
        }
    }

    function formatDate(value, row) {
        if (value) {
            return value >= 0 ? "&nbsp;" + value + '日' : value + '日';
        } else {
            return "&nbsp;0日";
        }
    }

    function formatEnum(value, row) {
        if (value && value === EXPIRED_STATUS.AVAILABLE.toString()) {
            return '使用可能';
        } else if (value && value === EXPIRED_STATUS.EXPIRED.toString()) {
            return '期限切れ';
        }
    }

}