
module nts.uk.at.view.kal003.a.tab {
	import model = nts.uk.at.view.kal003.share.model;

	export class AppFixedCheckConditionTab {
		listFixedConditionWorkRecord: KnockoutObservableArray<model.ApprovalFixedConditionWorkRecord> = ko.observableArray([]);
		isAllfixedCheck: KnockoutObservable<boolean> = ko.observable(false);

		load(){
			let self = this;
			service.getAllFixedApprovalItem().done((data: Array<any>) => {
				if (data && data.length) {
					let _list: Array<model.ApprovalFixedConditionWorkRecord> = _.map(data, acc => {
						return new model.ApprovalFixedConditionWorkRecord({ appAlarmConId: "", name: acc.name, no: acc.no, displayMessage: acc.displayMessage, useAtr: false, erAlAtr: acc.erAlAtr });
					});
					self.listFixedConditionWorkRecord(_list);
				}
			});
		}

		constructor(listFixedConditionWorkRecord?: Array<model.ApprovalFixedConditionWorkRecord>) {
			let self = this;
			self.load();

			if (listFixedConditionWorkRecord) {
				self.listFixedConditionWorkRecord.removeAll();
				self.listFixedConditionWorkRecord(listFixedConditionWorkRecord);
				for (var i = 0; i < self.listFixedConditionWorkRecord().length; i++) {
					self.listFixedConditionWorkRecord()[i].no(i + 1);
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

			$("#table-fixed-app").ntsFixedTable({ width: 512 }); 
		}//end constructor
	}//end FixedCheckConditionTab
}//end tab