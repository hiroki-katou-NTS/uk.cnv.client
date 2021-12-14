module nts.uk.at.view.kal003.a.tab {
	import windows = nts.uk.ui.windows;
	import getText = nts.uk.resource.getText;
	import block = nts.uk.ui.block;
	import model = nts.uk.at.view.kal003.share.model;
	import shareutils = nts.uk.at.view.kal003.share.kal003utils;

	export class MasterCheckFixedConTab {
		listFixedMasterCheckCondition: KnockoutObservableArray<model.MasterCheckFixedCon> = ko.observableArray([]);
		isAllfixedCheck: KnockoutObservable<boolean> = ko.observable(false);

		constructor(listFixedMasterCheckCondition?: Array<model.MasterCheckFixedCon>) {
			let self = this;
			service.getAllFixedMasterCheckItem().done((data: Array<any>) => {
				if (data && data.length) {
					let _list: Array<model.MasterCheckFixedCon> = _.map(data, acc => {
						return new model.MasterCheckFixedCon({ errorAlarmCheckId: "", name: acc.name, no: acc.no, message: acc.message, useAtr: false, erAlAtr: acc.erAlAtr });
					});
					self.listFixedMasterCheckCondition(_list);
				}
			});

			if (listFixedMasterCheckCondition) {
				self.listFixedMasterCheckCondition.removeAll();
				self.listFixedMasterCheckCondition(listFixedMasterCheckCondition);
				for (var i = 0; i < self.listFixedMasterCheckCondition().length; i++) {
					self.listFixedMasterCheckCondition()[i].no(i + 1);
				}
			}

			self.isAllfixedCheck = ko.pureComputed({
				read: function() {
					let l = self.listFixedMasterCheckCondition().length;
					if (self.listFixedMasterCheckCondition().filter((x) => { return x.useAtr() }).length == l && l > 0) {
						return true;
					} else {
						return false;
					}
				},
				write: function(value) {
					for (var i = 0; i < self.listFixedMasterCheckCondition().length; i++) {
						self.listFixedMasterCheckCondition()[i].useAtr(value);
					}
				},
				owner: self
			});

			$("#table-fixed11").ntsFixedTable({ width: 512 });
		}//end constructor
	}//end MasterCheckFixedConTab
}//end tab