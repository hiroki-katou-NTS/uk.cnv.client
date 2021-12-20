module nts.uk.at.view.kmw006.a.viewmodel {
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
    import confirmProceed = nts.uk.ui.dialog.confirmProceed;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    export class ScreenModel {
        itemList: KnockoutObservableArray<ItemModel>;
        listClosureInfo: KnockoutObservableArray<ClosureInfor>;
        selectedClosureId: KnockoutObservable<number>;
        dispSelectedCurrentMonth: KnockoutObservable<string> = ko.observable(null);
        dispSelectedPeriod: KnockoutObservable<string> = ko.observable(null);
        targetYm: KnockoutObservable<number> = ko.observable(null);
        executionDate: KnockoutObservable<string> = ko.observable(null);
        dispLatestClosure: KnockoutObservable<string>;
        dispClosureInfo1: KnockoutObservable<string>;
        dispClosureInfo2: KnockoutObservable<string>;
        dispClosureInfo3: KnockoutObservable<string>;
        dispClosureInfo4: KnockoutObservable<string>;
        dispClosureInfo5: KnockoutObservable<string>;
        dispClosureCurrentMonth1: KnockoutObservable<string>;
        dispClosureCurrentMonth2: KnockoutObservable<string>;
        dispClosureCurrentMonth3: KnockoutObservable<string>;
        dispClosureCurrentMonth4: KnockoutObservable<string>;
        dispClosureCurrentMonth5: KnockoutObservable<string>;
        dispClosurePeriod1: KnockoutObservable<string>;
        dispClosurePeriod2: KnockoutObservable<string>;
        dispClosurePeriod3: KnockoutObservable<string>;
        dispClosurePeriod4: KnockoutObservable<string>;
        dispClosurePeriod5: KnockoutObservable<string>;
        executable: KnockoutObservable<boolean> = ko.observable(true);
        displayClosurePeriod: KnockoutObservable<boolean>;
        first: KnockoutObservable<boolean> = ko.observable(true);
        totalCount: KnockoutObservable<number> = ko.observable(0);
        processedCount: KnockoutObservable<number> = ko.observable(0);
        isOverTime: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            var self = this;
            self.itemList = ko.observableArray([]);
            self.listClosureInfo = ko.observableArray([]);
            self.selectedClosureId = ko.observable(null);
            $("#A1_1").ntsFixedTable({});
            $("#A1_10").ntsFixedTable({});
            $("#A1_14").ntsFixedTable({});
            self.dispClosureInfo1 = ko.computed(() => {
                let r = self.listClosureInfo()[0];
                if (r)
                    return getText("KMW006_55", [r.closureId, r.closureName]);
                else
                    return null;
            });
            self.dispClosureInfo2 = ko.computed(() => {
                let r = self.listClosureInfo()[1];
                if (r)
                    return getText("KMW006_55", [r.closureId, r.closureName]);
                else
                    return null;
            });
            self.dispClosureInfo3 = ko.computed(() => {
                let r = self.listClosureInfo()[2];
                if (r)
                    return getText("KMW006_55", [r.closureId, r.closureName]);
                else
                    return null;
            });
            self.dispClosureInfo4 = ko.computed(() => {
                let r = self.listClosureInfo()[3];
                if (r)
                    return getText("KMW006_55", [r.closureId, r.closureName]);
                else
                    return null;
            });
            self.dispClosureInfo5 = ko.computed(() => {
                let r = self.listClosureInfo()[4];
                if (r)
                    return getText("KMW006_55", [r.closureId, r.closureName]);
                else
                    return null;
            });
            self.dispClosureCurrentMonth1 = ko.computed(() => {
                let r = self.listClosureInfo()[0];
                if (r)
                    return nts.uk.time.formatYearMonth(r.currentMonth);
                else
                    return null;
            });
            self.dispClosureCurrentMonth2 = ko.computed(() => {
                let r = self.listClosureInfo()[1];
                if (r)
                    return nts.uk.time.formatYearMonth(r.currentMonth);
                else
                    return null;
            });
            self.dispClosureCurrentMonth3 = ko.computed(() => {
                let r = self.listClosureInfo()[2];
                if (r)
                    return nts.uk.time.formatYearMonth(r.currentMonth);
                else
                    return null;
            });
            self.dispClosureCurrentMonth4 = ko.computed(() => {
                let r = self.listClosureInfo()[3];
                if (r)
                    return nts.uk.time.formatYearMonth(r.currentMonth);
                else
                    return null;
            });
            self.dispClosureCurrentMonth5 = ko.computed(() => {
                let r = self.listClosureInfo()[4];
                if (r)
                    return nts.uk.time.formatYearMonth(r.currentMonth);
                else
                    return null;
            });
            self.dispClosurePeriod1 = ko.computed(() => {
                let r = self.listClosureInfo()[0];
                if (r)
                    return getText("KMW006_54", [r.periodStart, r.periodEnd]);
                else
                    return null;
            });
            self.dispClosurePeriod2 = ko.computed(() => {
                let r = self.listClosureInfo()[1];
                if (r)
                    return getText("KMW006_54", [r.periodStart, r.periodEnd]);
                else
                    return null;
            });
            self.dispClosurePeriod3 = ko.computed(() => {
                let r = self.listClosureInfo()[2];
                if (r)
                    return getText("KMW006_54", [r.periodStart, r.periodEnd]);
                else
                    return null;
            });
            self.dispClosurePeriod4 = ko.computed(() => {
                let r = self.listClosureInfo()[3];
                if (r)
                    return getText("KMW006_54", [r.periodStart, r.periodEnd]);
                else
                    return null;
            });
            self.dispClosurePeriod5 = ko.computed(() => {
                let r = self.listClosureInfo()[4];
                if (r)
                    return getText("KMW006_54", [r.periodStart, r.periodEnd]);
                else
                    return null;
            });
            self.dispLatestClosure = ko.computed(() => {
                if (!nts.uk.text.isNullOrEmpty(self.executionDate()) && nts.uk.ntsNumber.isNumber(self.targetYm(), false))
                    return getText("KMW006_7", [nts.uk.time.formatYearMonth(self.targetYm()), moment.utc(self.executionDate()).format("YYYY/MM/DD")]);
                else
                    return null;
            });
            self.displayClosurePeriod = ko.computed(() => {
                return self.listClosureInfo().length >= 2;
            });

            self.selectedClosureId.subscribe((val) => {
                let closureInfo: ClosureInfor = _.find(self.listClosureInfo(), (x: ClosureInfor) => x.closureId == val);
                if (closureInfo) {
                    self.dispSelectedCurrentMonth(nts.uk.time.formatYearMonth(closureInfo.currentMonth));
                    self.dispSelectedPeriod(getText("KMW006_54", [closureInfo.periodStart, closureInfo.periodEnd]));
                    self.targetYm(closureInfo.targetYm);
                    self.executionDate(closureInfo.executionDate);
                    for (var i = 0; i < self.listClosureInfo().length; i++) {
                        let info = self.listClosureInfo()[i];
                        if (moment.utc(info.periodEnd, "YYYY/MM/DD") < moment.utc(closureInfo.periodEnd, "YYYY/MM/DD")) {
                            $("#A1_14 tr:nth-child(" + (i + 1) + ") td span").addClass("dipsRed");
                        } else {
                            $("#A1_14 tr:nth-child(" + (i + 1) + ") td span").removeClass("dipsRed");
                        }
                    }
                }
            });
        }

        startPage(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            block.invisible();
            self.listClosureInfo.removeAll();
            self.itemList.removeAll();
            let listEmpId = [];
            if (localStorage.getItem("MonthlyClosureListEmpId") != null) {
                listEmpId = localStorage.getItem("MonthlyClosureListEmpId").split(',');
            }
            let screenParams = {
                monthlyClosureUpdateLogId: localStorage.getItem("MonthlyClosureUpdateLogId"),
                listEmployeeId: listEmpId,
                closureId: localStorage.getItem("MonthlyClosureId"),
                startDT: localStorage.getItem("MonthlyClosureStartDT")
            }
            service.getInfors(screenParams).done((results: any) => {

                if (results.listInfor != null) {
                    for (let i = 0; i < results.listInfor.length; i++) {
                        let r = results.listInfor[i];
                        self.itemList.push(new ItemModel(r.closureId, r.closureName));
                        self.listClosureInfo.push(new ClosureInfor(r.closureId, r.closureName, r.closureMonth, r.periodStart, r.periodEnd, r.targetYm, r.executionDt));
                    }
                    self.executable(results.executable);
                    if (results.selectClosureId == self.selectedClosureId())
                        self.selectedClosureId.valueHasMutated();
                    else
                        self.selectedClosureId(results.selectClosureId);
                    
                    block.clear();
                } else {
                    block.grayout();
                    var currentTime = new Date(moment.utc(results.screenParams.currentDT).format('YYYY-MM-DDTHH:mm:ss'));
                    var startDT = new Date(moment.utc(results.screenParams.startDT).format('YYYY-MM-DDTHH:mm:ss'));
                    var duration = (currentTime.getTime() - startDT.getTime()) / 1000;
                    if(results.screenParams.executionStatus != 2 && duration < 14400){
                        // open dialog F
                        $.when(self.getAllPersonNo(results.screenParams)).done(() => {
                            results.screenParams.totalCount = self.totalCount();
                            self.openKmw006fDialog(results.screenParams);
                            self.first(false);
                            block.clear();
                        });
                    } else if ((results.screenParams.executionStatus == 0 || results.screenParams.executionStatus == 1) && duration > 14400) {
                        nts.uk.ui.dialog.info({ messageId: "Msg_1515" });
                        self.isOverTime(true);
                        // get data for screen A
                        // update status = 2 cho all nhan vien trong cong ty co thoi gian xu ly > 4h ma chua done
                        service.getInforsWithNoParams().done(results => {
                            for (let i = 0; i < results.listInfor.length; i++) {
                                let r = results.listInfor[i];
                                self.itemList.push(new ItemModel(r.closureId, r.closureName));
                                self.listClosureInfo.push(new ClosureInfor(r.closureId, r.closureName, r.closureMonth, r.periodStart, r.periodEnd, r.targetYm, r.executionDt));
                            }
                            self.executable(results.executable);
                            if (results.selectClosureId == self.selectedClosureId())
                                self.selectedClosureId.valueHasMutated();
                            else
                                self.selectedClosureId(results.selectClosureId);
                            // remove item of localStorage de khi excute se tao ra xu ly moi
                            localStorage.removeItem("MonthlyClosureUpdateLogId");
                            localStorage.removeItem("MonthlyClosureListEmpId");
                            localStorage.removeItem("MonthlyClosureId");
                            localStorage.removeItem("MonthlyClosureExecutionDateTime");
                            localStorage.removeItem("MonthlyClosureTaskId");
                            localStorage.removeItem("MonthlyClosureExecutionEndDate");
                        });
                        
                        block.clear();
                    }
                    block.clear(); 
                }
                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
                alertError(error);
                block.clear();
            });
            return dfd.promise();
        }

        private executeClick() {
            let self = this;
            let listEmpId = [];
            if (localStorage.getItem("MonthlyClosureListEmpId") != null) {
                listEmpId = localStorage.getItem("MonthlyClosureListEmpId").split(',');
            }
            confirmProceed({ messageId: "Msg_2296" }).ifYes(() => {
              confirmProceed({ messageId: "Msg_1355" }).ifYes(() => {
                block.invisible();
                var screenParams = {
                    monthlyClosureUpdateLogId: localStorage.getItem("MonthlyClosureUpdateLogId"),
                    listEmployeeId: listEmpId,
                    closureId: localStorage.getItem("MonthlyClosureId"),
                    startDT: localStorage.getItem("MonthlyClosureExecutionDateTime"),
                    endDT: localStorage.getItem("MonthlyClosureExecutionEndDate")
                }
                service.checkStatus({ closureId: self.selectedClosureId(), screenParams: screenParams, overTime: self.isOverTime() }).done((result) => {
                    if (result) {
                        let periodStart: string = result.periodStart;
                        let periodEnd: string = result.periodEnd;
                        result.periodStart = moment.utc(periodStart, "YYYY/MM/DD").toISOString();
                        result.periodEnd = moment.utc(periodEnd, "YYYY/MM/DD").toISOString();
                        localStorage.setItem("MonthlyClosureUpdateLogId", result.monthlyClosureUpdateLogId);
                        localStorage.setItem("MonthlyClosureListEmpId", result.listEmployeeId);
                        localStorage.setItem("MonthlyClosureId", result.closureId);
                        localStorage.setItem("MonthlyClosureStartDT", result.startDT);
                        localStorage.setItem("MonthlyClosureEndDT", result.endDT);
                        localStorage.setItem("MonthlyClosureCurrentMonth", result.currentMonth);
                        localStorage.setItem("MonthlyClosureDay", result.closureDay);
                        localStorage.setItem("MonthlyClosureDayOfMonth", result.isLastDayOfMonth);
                        localStorage.setItem("MonthlyClosurePeriodStart", result.periodStart);
                        localStorage.setItem("MonthlyClosurePeriodEnd", result.periodEnd);
                    }
                    self.isOverTime(false);
                    self.openKmw006fDialog(result);
                }).fail((error) => {
                    alertError(error);
                }).always(() => {
                    block.clear();
                });
              });
            });
        }

        private openKmw006fDialog(params: any) {
            let self = this;
            setShared("kmw006fParams", params);
            modal("/view/kmw/006/f/index.xhtml").onClosed(() => {
                let check = getShared("kmw006fConfirm");
                if (check || !self.first()) self.startPage();
            });
        }

        private openKmw006cDialog() {
            modal("/view/kmw/006/c/index.xhtml").onClosed(() => {
            });
        }
        
        getAllPersonNo(params: any): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            let data = {
                closureId: params.closureId,
                yearMonth: params.currentMonth
            }
            service.getAllPersonNo(data).done(no => {
                self.totalCount(no);
                dfd.resolve();
            });
            return dfd.promise();
        }

    }

    class ItemModel {
        code: number;
        name: string;

        constructor(code: number, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    class ClosureInfor {
        closureId: number;
        closureName: string;
        currentMonth: number;
        periodStart: string;
        periodEnd: string;
        targetYm: number;
        executionDate: string;

        constructor(closureId: number, closureName: string, currentMonth: number, periodStart: string, periodEnd: string, targetYm: number, executionDate: string) {
            this.closureId = closureId;
            this.closureName = closureName;
            this.currentMonth = currentMonth;
            this.periodStart = periodStart;
            this.periodEnd = periodEnd;
            this.targetYm = targetYm;
            this.executionDate = executionDate;
        }
    }

}