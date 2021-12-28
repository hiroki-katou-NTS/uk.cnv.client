module nts.uk.at.view.kaf022.o.viewmodel {
    import alert = nts.uk.ui.dialog.alert;
    import getText = nts.uk.resource.getText;

    export class ScreenModelO {
        flexWorkOptions: KnockoutObservableArray<ItemModel>;

        // フレックス勤務者区分
        selectedFlexWorkAtr: KnockoutObservable<number>;

        overtimeAppOptions: KnockoutObservableArray<ItemModel>;

        // 残業申請区分
        selectedOvertimeAppAtr: KnockoutObservable<number>;

        columns = ko.observableArray([
            { headerText: "No", key: 'code', width: 150, hidden: true },
            { headerText: getText("KAF022_691"), key: 'name', width: 150 }
        ]);

        // 残業枠
        overtimeWorkFrames: KnockoutObservableArray<OTWorkFrame>;
        overTimeQuotaSettings: KnockoutObservableArray<OTQuota>;

        manualChange: boolean = false;

        allChecked: KnockoutObservable<boolean>;

        enableRegister: KnockoutObservable<boolean>;

        constructor() {
            let self = this;
            self.flexWorkOptions = ko.observableArray([
                new ItemModel(0, "フレックス勤務者以外"),
                new ItemModel(1, "フレックス勤務者")
            ]);
            self.selectedFlexWorkAtr = ko.observable(0);

            self.overtimeAppOptions = ko.observableArray([
                new ItemModel(OVERTIME.EARLY, getText("Enum_APP_OVERTIME_EARLY")),
                new ItemModel(OVERTIME.NORMAL, getText("Enum_APP_OVERTIME_NORMAL")),
                new ItemModel(OVERTIME.EARLY_NORMAL, getText("Enum_APP_OVERTIME_EARLY_NORMAL")),
                new ItemModel(OVERTIME.MULTIPLE, getText("Enum_APP_OVERTIME_MULTIPLE"))
            ]);
            self.selectedOvertimeAppAtr = ko.observable(OVERTIME.EARLY);

            self.overtimeWorkFrames = ko.observableArray([]);

            self.overTimeQuotaSettings = ko.observableArray([]);

            $("#fixed-table-o4").ntsFixedTable({});

            self.selectedOvertimeAppAtr.subscribe(value => {
                if (value != null) {
                    self.manualChange = true;
                    nts.uk.ui.block.invisible();
                    self.getData(value, self.selectedFlexWorkAtr()).done(() => {
                        self.overtimeWorkFrames().forEach((frame: OTWorkFrame) => {
                            if (frame.no != -1) {
                                frame.checked(self.overTimeQuotaSettings().map(q => q.overTimeFrame).indexOf(frame.no) >= 0);
                                // frame.enable(value != OVERTIME.EARLY_NORMAL || !_.find(allOtQuotaSettings, s => s.flexAtr == self.selectedFlexWorkAtr() && s.overtimeAtr != OVERTIME.EARLY_NORMAL && s.overTimeFrame == frame.no));
                                frame.enable(true);
                            }
                        });
                        self.manualChange = false;
                    }).fail(() => {
                        self.manualChange = false;
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                } else {
                    self.overtimeWorkFrames().forEach((frame: OTWorkFrame) => {
                        if (frame.no != -1) {
                            frame.checked(false);
                            frame.enable(false);
                        }
                    });
                }
            });
            self.selectedFlexWorkAtr.subscribe(value => {
                self.manualChange = true;
                nts.uk.ui.block.invisible();
                self.getData(self.selectedOvertimeAppAtr(), value).done(() => {
                    self.overtimeWorkFrames().forEach(frame => {
                        if (frame.no != -1) {
                            frame.checked(self.overTimeQuotaSettings().map(q => q.overTimeFrame).indexOf(frame.no) >= 0);
                            // frame.enable(self.selectedOvertimeAppAtr() != OVERTIME.EARLY_NORMAL || !_.find(allOtQuotaSettings, s => s.flexAtr == value && s.overtimeAtr != OVERTIME.EARLY_NORMAL && s.overTimeFrame == frame.no));
                        }
                    });
                    self.manualChange = false;
                }).fail(() => {
                    self.manualChange = false;
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            });

            self.allChecked = ko.pureComputed({
                read: function () {
                    return self.overtimeWorkFrames().length > 0 && self.overtimeWorkFrames().filter(m => m.checked()).length == self.overtimeWorkFrames().length;
                },
                write: function (value) {
                    self.overtimeWorkFrames().forEach((m: OTWorkFrame) => {
                        if (m.enable() && m.no != -1) m.checked(value);
                    });
                },
                owner: self
            });

            self.enableRegister = ko.computed(() => {
                return self.overTimeQuotaSettings()
                    .filter(q => q.overtimeAtr == self.selectedOvertimeAppAtr() && q.flexAtr == self.selectedFlexWorkAtr())
                    .length > 0;
            });
        }

        handleCheck = (checked: boolean, frameNo: number): void => {
            const self = this;
            if (!self.manualChange) {
                if (checked) {
                    self.overTimeQuotaSettings.push(new OTQuota(self.selectedOvertimeAppAtr(), self.selectedFlexWorkAtr(), frameNo));
                } else {
                    self.overTimeQuotaSettings.remove(q => {
                        return q.overtimeAtr == self.selectedOvertimeAppAtr()
                            && q.flexAtr == self.selectedFlexWorkAtr()
                            && q.overTimeFrame == frameNo;
                    });
                }
            }
        };

        startPage(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            service.getOTFrames().done((otFrames: Array<any>) => {
                self.overtimeWorkFrames(_.sortBy(otFrames, ["overtimeWorkFrNo"]).map(f => {
                    return new OTWorkFrame(
                        false,
                        f.overtimeWorkFrNo,
                        f.overtimeWorkFrName,
                        true,
                        self.handleCheck
                    );
                }));
                self.overtimeWorkFrames.push(new OTWorkFrame(
                    true,
                    -1,
                    getText("KAF022_797"),
                    false,
                    self.handleCheck
                ));
                self.selectedOvertimeAppAtr.valueHasMutated();
                dfd.resolve();
            }).fail((error: any) => {
                dfd.reject();
                alert(error);
            }).always(() => {
                nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }

        getData(overtimeAtr: number, flexWorkAtr: number): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            service.getOTQuotaByAtr(overtimeAtr, flexWorkAtr).done((otQuotaSettings: Array<OTQuota>) => {
                self.overTimeQuotaSettings(otQuotaSettings.filter(o => !!_.find(self.overtimeWorkFrames(), f => f.no == o.overTimeFrame)));
                dfd.resolve();
            }).fail((error: any) => {
                dfd.reject();
                alert(error);
            });
            return dfd.promise();
        }

        saveOTQuotaSet() {
            const self = this;
            nts.uk.ui.block.invisible();
            const data = self.overTimeQuotaSettings().filter(i => !!_.find(self.overtimeWorkFrames(), s => s.no != -1 && s.no == i.overTimeFrame));
            service.registerOTQuota(ko.toJS(data)).done(() => {
                nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                    // self.closeDialog();
                });
            }).fail(error => {
                alert(error);
            }).always(() => {
                nts.uk.ui.block.clear();
            });
        }

        closeDialog() {
            nts.uk.ui.windows.close();
        }
    }

    class ItemModel {
        code: number;
        name: string;
        constructor(value: number, label: string) {
            this.code = value;
            this.name = label;
        }
    }

    class OTWorkFrame {
        checked: KnockoutObservable<boolean>;
        no: number;
        name: string; // 残業枠名称
        enable: KnockoutObservable<boolean>;
        constructor(checked: boolean, no: number, name: string, enable: boolean, handleCheck: any) {
            this.checked = ko.observable(checked);
            this.no = no;
            this.name = name;
            this.enable = ko.observable(enable);
            this.checked.subscribe(value => {
                handleCheck(value, this.no);
            });
        }
    }

    class OTQuota {
        overtimeAtr: number; // 残業申請区分
        flexAtr: number; // フレックス勤務者区分
        overTimeFrame: number; // 対象残業枠
        constructor(overtimeAtr: number, flexAtr: number, overTimeFrame: number) {
            this.overtimeAtr = overtimeAtr;
            this.flexAtr = flexAtr;
            this.overTimeFrame = overTimeFrame;
        }
    }

    enum OVERTIME {
        EARLY = 0,
        NORMAL = 1,
        EARLY_NORMAL = 2,
        MULTIPLE = 3
    }

}




