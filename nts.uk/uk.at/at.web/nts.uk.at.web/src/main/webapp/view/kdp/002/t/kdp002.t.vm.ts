module nts.uk.at.view.kdp002.t {
	export module viewmodel {
		export class ScreenModel {
			dataShare: KnockoutObservableArray<any> = ko.observableArray([]);
			labelNames: KnockoutObservable<string> = ko.observable('');
			labelColor: KnockoutObservable<string> = ko.observable('');
			buttonInfo: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
			share: KnockoutObservableArray<any> = ko.observableArray([]);
			messageContent: KnockoutObservable<string> = ko.observable('');
			messageColor: KnockoutObservable<string> = ko.observable('');
			errorDate: KnockoutObservable<string> = ko.observable('');
			errorDateStr: KnockoutObservable<string> = ko.observable('');
			constructor() {
			}
			/**
			 * start page  
			 */
			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();
				self.share = nts.uk.ui.windows.getShared('KDP010_2T');
				let lstButton: ItemModel[] = [];
				for (let i = 1; i < 6; i++) {
					lstButton.push(new ItemModel('', '', ''));
				}
				self.buttonInfo(lstButton);
				if (!self.share) {
					self.dataShare = {
						messageContent: '',
						messageColor: '',
						listRequired: []
					}

				} else {
					self.share.dailyAttdErrorInfos = _.orderBy(self.share.dailyAttdErrorInfos, ['lastDateError'], ['desc']);
					let error = self.share.dailyAttdErrorInfos[0];
					self.messageContent(error.messageContent);
					self.messageColor(error.messageColor);
					self.errorDate(error.lastDateError);
					self.errorDateStr(nts.uk.resource.getText('KDP002_102') + error.lastDateError);
					let listRequired = [];
					let length = error.listRequired.length > 6 ? 6 : error.listRequired.length;
					for (let idx = 0; idx < length; idx++) {
						let btn = self.getBtn(error.listRequired[idx]);
						if (btn) {
							listRequired.push(btn);
							// if (btn.appType == 1 || btn.appType == 2 || btn.appType == 3 || btn.appType == 4 || btn.appType == 5 || btn.appType == 6) {
							// 	listRequired.push(btn);
							// }
						}
					}
					self.dataShare = {
						listRequired: listRequired
					}
				}

				dfd.resolve();

				return dfd.promise();
			}

			public getBtn(errorType: number) {
				let self = this;
				let btn = {};
				let transfer = { appDate: self.errorDate() };
				let app = _.find(self.share.appDispNames, (app) => { return app.appType === errorType });
				if (!app) {
					return null;
				}
				btn.buttonName = app ? ko.observable(app.dispName) : ko.observable('');
				btn.appType = app.appType;
				btn.screen = app.url;

				btn.transfer = transfer;
				return btn;
			}

			/**
			 * Close dialog
			 */
			public closeDialog(): void {
				let self = this;
				let shareG = {
					messageContent: self.labelNames(),
					messageColor: self.labelColor(),
					errorDate: self.errorDate(),
					listRequired: self.dataShare.listRequired,
					isClose: true
				};
				nts.uk.ui.windows.setShared('KDP010_T', shareG);
				nts.uk.ui.windows.close();
			}

			/**
			 * Close dialog
			 */
			public jumpScreen(data, vm): void {
				let shareG = {
					messageContent: vm.labelNames(),
					messageColor: vm.labelColor(),
					errorDate: vm.errorDate(),
					btn: data
				};
				nts.uk.ui.windows.setShared('KDP010_T', shareG);
				nts.uk.ui.windows.close();
			}

		}

	}
	export class ItemModel {
		buttonName: string;
		buttonColor: string;
		textColor: string;

		constructor(buttonName: string, buttonColor: string, textColor: string) {
			this.buttonName = ko.observable('') || '';
			this.buttonColor = ko.observable('') || '';
			this.textColor = ko.observable('') || '';
		}
	}
}