module nts.uk.at.view.kal003.a.tab {
	import windows = nts.uk.ui.windows;
	import getText = nts.uk.resource.getText;
	import block = nts.uk.ui.block;
	import model = nts.uk.at.view.kal003.share.model;
	import shareutils = nts.uk.at.view.kal003.share.kal003utils;

	export class FixedCheckConditionTab {
		listFixedConditionWorkRecord: KnockoutObservableArray<model.FixedConditionWorkRecord> = ko.observableArray([]);
		isAllfixedCheck: KnockoutObservable<boolean> = ko.observable(false);

		constructor(listFixedConditionWorkRecord?: Array<model.FixedConditionWorkRecord>) {
			let self = this;
			service.getAllFixedConData().done((data: Array<any>) => {
				if (data && data.length) {
					let _list: Array<model.FixedConditionWorkRecord> = _.map(data, acc => {
						return new model.FixedConditionWorkRecord({ dailyAlarmConID: "", checkName: acc.fixConWorkRecordName, fixConWorkRecordNo: acc.fixConWorkRecordNo, message: acc.message, useAtr: false, eralarmAtr: acc.eralarmAtr });
					});
					self.listFixedConditionWorkRecord(_list);
				}
			});

			if (listFixedConditionWorkRecord) {
				self.listFixedConditionWorkRecord.removeAll();
				self.listFixedConditionWorkRecord(listFixedConditionWorkRecord);
				for (var i = 0; i < self.listFixedConditionWorkRecord().length; i++) {
					self.listFixedConditionWorkRecord()[i].fixConWorkRecordNo(i + 1);
				}
			}

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

			$("#table-fixed").ntsFixedTable({ width: 510, height: 220 });
		}//end constructor
	}//end FixedCheckConditionTab
}//end tab



