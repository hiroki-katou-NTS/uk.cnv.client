/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kdp002.c {

	const kDP002RequestUrl = {
		FINGER_STAMP_SETTING: 'at/record/stamp/finger/get-finger-stamp-setting'
	}

	interface TimeClock {
		tick: number;
		now: KnockoutObservable<Date>;
		style: KnockoutObservable<string>;
		displayTime: KnockoutObservable<number>;
	}

	const initTime = (date: any): TimeClock => ({
		tick: -1,
		now: ko.observable(date),
		style: ko.observable(''),
		displayTime: ko.observable(10)
	});

	export module viewmodel {
		import a = nts.uk.at.view.kdp002.a;

		// display items type (require from other screen)
		type DISPLAY_ITEM_IDS = number[];

		// employeedata (require from other screen)
		interface EmployeeParam {
			employeeId: string;
			employeeCode: string;
			mode: a.Mode;
			error: any;
		}

		export class ScreenModel extends ko.ViewModel {

			// B2_2
			employeeCodeName: KnockoutObservable<string> = ko.observable("");
			// B3_2
			dayName: KnockoutObservable<string> = ko.observable("");
			// B3_3
			timeName: KnockoutObservable<string> = ko.observable("");
			// G4_2
			checkHandName: KnockoutObservable<string> = ko.observable("");
			// G5_2
			numberName: KnockoutObservable<string> = ko.observable("");
			// G6_2
			laceName: KnockoutObservable<string> = ko.observable("");

			workName1: KnockoutObservable<string> = ko.observable("");

			workName2: KnockoutObservable<string> = ko.observable("");

			timeName1: KnockoutObservable<string> = ko.observable("");

			timeName2: KnockoutObservable<string> = ko.observable("");

			time: TimeClock = initTime(this.$date.now());

			items: KnockoutObservableArray<model.ItemModels> = ko.observableArray([]);
			columns2: KnockoutObservableArray<NtsGridListColumn>;
			currentCode: KnockoutObservable<any> = ko.observable();
			currentCodeList: KnockoutObservableArray<any>;
			permissionCheck: KnockoutObservable<boolean> = ko.observable(false);
			displayButton: KnockoutObservable<boolean> = ko.observable(true);
			timeView: KnockoutObservable<Date> = ko.observable();

			notificationStamp: KnockoutObservableArray<IMsgNotices> = ko.observableArray([]);
			modeShowPointNoti: KnockoutObservable<boolean | null> = ko.observable(null);

			infoEmpFromScreenA!: EmployeeParam;

			item1: KnockoutObservable<string> = ko.observable('');
			item2: KnockoutObservable<string> = ko.observable('');
			item3: KnockoutObservable<string> = ko.observable('');
			item4: KnockoutObservable<string> = ko.observable('');
			item5: KnockoutObservable<string> = ko.observable('');
			value1: KnockoutObservable<string> = ko.observable('');
			value2: KnockoutObservable<string> = ko.observable('');
			value3: KnockoutObservable<string> = ko.observable('');
			value4: KnockoutObservable<string> = ko.observable('');
			value5: KnockoutObservable<string> = ko.observable('');

			showBtnNoti: KnockoutObservable<boolean | null> = ko.observable(null);
			noticeSetting: KnockoutObservable<INoticeSet> = ko.observable(null);

			constructor() {
				super();

				let self = this;
				const vm = new ko.ViewModel();

				self.columns2 = ko.observableArray([
					{ headerText: nts.uk.resource.getText("KDP002_59"), key: 'itemId', width: 200, hidden: true },
					{ headerText: nts.uk.resource.getText("KDP002_59"), key: 'name', width: 175 },
					{ headerText: nts.uk.resource.getText("KDP002_60"), key: 'value', width: 175 }
				]);
				self.showBtnNoti.subscribe(() => {
					self.setSizeDialog();
				});
				self.laceName.subscribe(() => {
					self.setSizeDialog();
				});
				self.$ajax(kDP002RequestUrl.FINGER_STAMP_SETTING)
					.then((data: any) => {
						self.noticeSetting(data.noticeSetDto);
					});

				vm.$ajax("at", "server/time/now").then((output: any) => {
					let data: Date = moment(output).utc();
					self.timeView(data)
				})
			}

			setSizeDialog() {
				let self = this;
				if (self.laceName() == '') {
					if (self.showBtnNoti()) {
						self.$window.size(565, 450);
					} else {
						self.$window.size(535, 450);
					}
				} else {
					if (self.showBtnNoti()) {
						self.$window.size(600, 450);
					} else {
						self.$window.size(565, 450);
					}
				}
			}

			getWorkPlacwName(workPlaceId: string) {
				const vm = new ko.ViewModel();
				const self = this;

				const param = { sid: self.infoEmpFromScreenA.employeeId, workPlaceIds: [workPlaceId] };
				vm.$ajax('at', 'screen/at/kdp003/workplace-info', param)
					.then((data: any) => {
						if (data) {
							if (data.workPlaceInfo[0].displayName === 'コード削除済') {
								self.laceName('');
							} else {
								self.laceName(data.workPlaceInfo[0].displayName);
							}
						}
					});
			}

			/**
			 * start page  
			 */
			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();
				let itemIds: DISPLAY_ITEM_IDS = nts.uk.ui.windows.getShared("KDP010_2C");
				self.infoEmpFromScreenA = nts.uk.ui.windows.getShared("infoEmpToScreenC");

				self.getWorkPlacwName(self.infoEmpFromScreenA.workPlaceId);

				let data = {
					employeeId: self.infoEmpFromScreenA.employeeId,
					stampDate: moment().format("YYYY/MM/DD"),
					attendanceItems: itemIds
				}

				self.getEmpInfo();

				service.startScreen(data).done((res) => {
					let itemIds = ["TIME", "AMOUNT", "TIME_WITH_DAY", "DAYS", "COUNT", "CLOCK"];

					if (res) {

						if (_.size(res.stampRecords) > 0) {
							res.stampRecords = _.orderBy(res.stampRecords, ['stampTimeWithSec'], ['desc']);
							let record = res.stampRecords[0];
							let dateDisplay = record.stampDate;
							if (moment(record.stampDate).day() == 6) {
								dateDisplay = "<span class='color-schedule-saturday' >" + dateDisplay + "</span>";
							} else if (moment(record.stampDate).day() == 0) {
								dateDisplay = "<span class='color-schedule-sunday' >" + dateDisplay + "</span>";
							}
							self.checkHandName(res.stampRecords.length > 0 ? record.stampArtName : 0);
							self.numberName();
							if (ko.unwrap(self.laceName) === '') {
								self.laceName((res.workPlaceName || ''));
							}
							self.dayName(dateDisplay);
							self.timeName(record.stampTime);

							self.timeName1(res.attendance ? nts.uk.time.format.byId("ClockDay_Short_HM", parseInt(res.attendance)) + " ~ " : null);
							self.timeName2(res.leave ? nts.uk.time.format.byId("ClockDay_Short_HM", parseInt(res.leave)) : null);
							self.workName1(res.workTypes.length > 0 ? res.workTypes[0].name : '');
							self.workName2(res.workTimeTypes.length > 0 ? res.workTimeTypes[0].name : '');

							if (res.itemValues) {
								// C4	実績の属性と表示書式について
								var resutl: any[] = [];

								res.itemValues.forEach(item => {
									if (item.itemId == 28 || item.itemId == 29 || item.itemId == 31 || item.itemId == 34) {
										item.value = '';
									} else if ((item.valueType == "TIME") && item.value) {
										item.value = nts.uk.time.format.byId("Clock_Short_HM", parseInt(item.value));
									} else if (item.valueType == "AMOUNT") {
										item.value = nts.uk.ntsNumber.formatNumber(item.value, new nts.uk.ui.option.NumberEditorOption({ grouplength: 3, decimallength: 2 }));
									} else if ((item.valueType == "TIME_WITH_DAY" || item.valueType == "CLOCK") && item.value) {
										item.value = nts.uk.time.format.byId("ClockDay_Short_HM", parseInt(item.value));
									} else if ((item.valueType == "DAYS" || item.valueType == "COUNT") && item.value) {
										item.valueType = nts.uk.ntsNumber.formatNumber(parseFloat(item.valueType), new nts.uk.ui.option.NumberEditorOption({ grouplength: 3, decimallength: 1 }));
									}

									const exist = _.find(data.attendanceItems, ((valueFind) => {
										return valueFind == item.itemId;
									}));

									if (exist) {
										resutl.push(item);
									}
								});

								self.item1(resutl.length > 0 ? resutl[0].name : '');
								self.item2(resutl.length > 1 ? resutl[1].name : '');
								self.item3(resutl.length > 2 ? resutl[2].name : '');
								self.item4(resutl.length > 3 ? resutl[3].name : '');
								self.item5(resutl.length > 4 ? resutl[4].name : '');

								self.value1(resutl.length > 0 ? resutl[0].value : '');
								self.value2(resutl.length > 1 ? resutl[1].value : '');
								self.value3(resutl.length > 2 ? resutl[2].value : '');
								self.value4(resutl.length > 3 ? resutl[3].value : '');
								self.value5(resutl.length > 4 ? resutl[4].value : '');
							}

							self.items(res.itemValues);
						}
					}
					if (res.confirmResult) {
						if (res.confirmResult.permissionCheck == 1)
							self.permissionCheck(res.confirmResult.permissionCheck == 1);
					} else {
						self.displayButton(false);
					}

					if (ko.unwrap(self.permissionCheck)) {
						if (res.setting == 2) {
							if (self.infoEmpFromScreenA.error && self.infoEmpFromScreenA.error.dailyAttdErrorInfos && self.infoEmpFromScreenA.error.dailyAttdErrorInfos.length > 0) {
								self.permissionCheck(false);
							} else {
								self.permissionCheck(true);
							}
						}
					}
				});

				self.$window.shared("screenC").done((nameScreen: any) => {
					switch (nameScreen.screen) {
						case 'KDP001':
						case 'KDP002':
							self.showBtnNoti(false);
							break
						case 'KDP003':
						case 'KDP004':
						case 'KDP005':
							self.getNotification();
							break
					}
				});

				dfd.resolve();
				return dfd.promise();
			}

			public isNoData() {
				const vm = this;
				let itemData = _.filter(vm.items(), 'value');
				return !vm.timeName1() && !vm.timeName2() && !itemData.length && !vm.workName1() && !vm.workName2();
			}
			getEmpInfo(): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred();
				let employeeId = self.infoEmpFromScreenA.employeeId;
				service.getEmpInfo(employeeId).done(function (data) {
					self.employeeCodeName(data.employeeCode + " " + data.personalName);
					dfd.resolve();
				});
				return dfd.promise();
			}

			getNotification() {
				const vm = this;
				const mockvm = new ko.ViewModel();

				const param = {
					startDate: mockvm.$date.now(),
					endDate: mockvm.$date.now(),
					sid: vm.infoEmpFromScreenA.employeeId
				}

				service.getNotificationSetting()
					.done((data: boolean) => {
						if (data) {
							service.getNotification(param)
								.done((data: IMsgNotices[]) => {

									vm.notificationStamp(data);

									var isShow = 0;
									var isShowPoint = 0;
									_.forEach(data, ((value) => {
										_.forEach(value, ((value1) => {
											if (value1.message.targetInformation.destination == 2) {
												isShow++;
											}
											if (value1.message.targetInformation.destination == 2 && value1.flag) {
												isShowPoint++;
											}
										}));
									}));

									if (isShow > 0) {
										vm.showBtnNoti(true);

										if (isShowPoint > 0) {
											vm.modeShowPointNoti(true);
										} else {
											vm.modeShowPointNoti(false);
										}
									} else {
										vm.showBtnNoti(false);
									}
								});
						}
						else {
							vm.showBtnNoti(false);
						}
					});
			}

			openDialogU() {
				const vm = this;
				const params = { sid: vm.infoEmpFromScreenA.employeeId, data: ko.unwrap(vm.notificationStamp), setting: ko.unwrap(vm.noticeSetting) };
				vm.$window.modal('/view/kdp/002/u/index.xhtml', params)
					.then(() => {
						vm.modeShowPointNoti(false);
						vm.getNotification();
					});
			}

			/**
			 * Close dialog
			 */
			public closeDialog(): void {
				nts.uk.ui.windows.close();
			}

			/**
			 * Close dialog
			 */
			public registerDailyIdentify(): void {
				const vm = this;
				const param = { sid: vm.infoEmpFromScreenA.employeeId };

				service.registerDailyIdentify(param).done(() => {
					nts.uk.ui.dialog.info({ messageId: "Msg_15" })
						.then(() => {
							nts.uk.ui.windows.close();
						});
				});
			}
		}
	}
	export module model {
		export class ItemModels {
			itemId: string;
			name: string;
			value: string;
			constructor(code: string, name: string, value: string) {
				this.code = code;
				this.name = name;
				this.value = value;
			}
		}
	}

	interface IMsgNotices {
		creator: string;
		flag: boolean;
		message: IEmployeeIdSeen;
	}

	interface IEmployeeIdSeen {
		endDate: string,
		inputDate: Date
		modifiedDate: Date
		notificationMessage: string
		startDate: Date
		targetInformation: ITargetInformation
	}

	interface ITargetInformation {
		destination: string;
		targetSIDs: string;
		targetWpids: string[];
	}
}