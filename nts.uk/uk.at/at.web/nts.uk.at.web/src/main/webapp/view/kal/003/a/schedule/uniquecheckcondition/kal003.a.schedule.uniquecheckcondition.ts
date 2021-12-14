module nts.uk.at.view.kal003.a.tab {
    import windows = nts.uk.ui.windows;
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import model = nts.uk.at.view.kal003.share.model;
    import shareutils = nts.uk.at.view.kal003.share.kal003utils;

    export class ScheUniqueCheckConditionTab {
        listFixedConditionWorkRecord: KnockoutObservableArray<model.FixedConditionWorkRecord> = ko.observableArray([]);
        tmpListFixedConditionWorkRecord: [];
        isAllfixedCheck: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            let self = this;
            
            self.initData();
            self.isAllfixedCheck = ko.pureComputed({
                read: function() {
                    let l = self.listFixedConditionWorkRecord().length;
                    if (self.listFixedConditionWorkRecord().filter((x) => { return x.useAtr() }).length == l && l > 0) {
                        return true;
                    } else {
                        return false;
                    }
                },
                write: function(value) {
                    for (var i = 0; i < self.listFixedConditionWorkRecord().length; i++) {
                        self.listFixedConditionWorkRecord()[i].useAtr(value);
                    }
                },
                owner: self
            });

            $("#table-schedule-fixed").ntsFixedTable({ width: 820 });
        }//end constructor
        
        initData(category): void {
            let self = this;
            let serviceRequest = service.getScheduleFixItemDaily();
            if (category == model.CATEGORY.SCHEDULE_MONTHLY) {
                serviceRequest = service.getScheduleFixItemMonthly();
            }
            serviceRequest.done((data: Array<any>) => {
                if (data && data.length) {
                    let _list: Array<model.FixedConditionWorkRecord> = _.map(data, acc => {
                        return new model.FixedConditionWorkRecord({ eralarmAtr: acc.alarmCheckCls, checkName: acc.dailyCheckName, fixConWorkRecordNo: acc.fixedCheckDayItems, message: acc.initMsg, useAtr: false });
                    });
                    self.listFixedConditionWorkRecord(_list);
                    self.tmpListFixedConditionWorkRecord = data;
                } else {
                    self.listFixedConditionWorkRecord([]);
                    self.tmpListFixedConditionWorkRecord = [];
                }
            });
        }
        
        setListFixedConditionWorkRecord(listFixedConditionWorkRecordDb): void {
            var self = this;
            if (listFixedConditionWorkRecordDb && listFixedConditionWorkRecordDb.length > 0) {
                for (var i = 0; i < self.tmpListFixedConditionWorkRecord.length; i++) {
                    var itemFixCondItem = _.find(listFixedConditionWorkRecordDb, function(o) { return o.fixConWorkRecordNo == self.tmpListFixedConditionWorkRecord[i].fixedCheckDayItems; });
                    if (itemFixCondItem) {
                        self.listFixedConditionWorkRecord()[i].message(itemFixCondItem.message);
                        self.listFixedConditionWorkRecord()[i].dailyAlarmConID = itemFixCondItem.dailyAlarmConID;
                        self.listFixedConditionWorkRecord()[i].useAtr(itemFixCondItem.useAtr);
                    }
                }
            } else {
                if (self.tmpListFixedConditionWorkRecord && self.tmpListFixedConditionWorkRecord.length) {
                    let _list: Array<model.FixedConditionWorkRecord> = _.map(self.tmpListFixedConditionWorkRecord, acc => {
                        return new model.FixedConditionWorkRecord({ eralarmAtr: acc.alarmCheckCls, checkName: acc.dailyCheckName, fixConWorkRecordNo: acc.fixedCheckDayItems, message: acc.initMsg, useAtr: false });
                    });
                    self.listFixedConditionWorkRecord(_list);
                }
            }
        }
        
    }//end FixedCheckConditionTab
}//end tab



