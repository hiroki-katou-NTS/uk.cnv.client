module nts.uk.at.view.kdp002.a {
	export module viewmodel {
		const DATE_AND_DAY_FORMAT = 'D(ddd)';

		export class EmbossGridInfo {
			columns: KnockoutObservableArray<any> = ko.observableArray([]);
			currentCode: KnockoutObservable<any> = ko.observable({});
			items: KnockoutObservableArray<any> = ko.observableArray([]);
			displayMethod: KnockoutObservable<any>;
			displayType: any = { HIDE: 0, DISPLAY: 1, SHOW_TIME_CARD: 2 };
			dateValue: KnockoutObservable<{ startDate: string; endDate: string; }>;
			yearMonth: KnockoutObservable<any> = ko.observable('');
			workManagementMultiple: KnockoutObservable<boolean> = ko.observable(false);
			systemDate: KnockoutObservable<any> = ko.observable('');

			constructor(start: IStartPage, workManagementMultiple: boolean) {
				let self = this;
				let vm = new ko.ViewModel();
				let setting = start.stampSetting;
				
				self.displayMethod = ko.observable(setting.historyDisplayMethod);
				self.dateValue = ko.observable({ startDate: moment().add(-3, 'days').format('YYYY/MM/DD'), endDate: moment().format('YYYY/MM/DD') });
				
				vm.$ajax('at', '/server/time/now')
				.then((c) => {
					const sysDate = moment(c).format('YYYY/MM/DD');;
					const yearMonth = moment(c).format('YYYY/MM');

					self.yearMonth(yearMonth);
					self.systemDate(sysDate);
                    self.bindItemData(start.stampDataOfEmployees);
				});
				self.workManagementMultiple(workManagementMultiple);

				if (self.displayMethod() == self.displayType.DISPLAY) {
					self.columns([
						{ headerText: 'コード', key: 'code', width: 100, hidden: true },
						{ headerText: nts.uk.resource.getText('KDP002_30'), key: 'stampDate', width: 140 },
						{ headerText: nts.uk.resource.getText('KDP002_31'), key: 'stampHowAndTime', width: 90 },
						{ headerText: nts.uk.resource.getText('KDP002_32'), key: 'timeStampType', width: 90 }
					]);
					//self.bindItemData(start.stampDataOfEmployees);
				} else if (self.displayMethod() == self.displayType.SHOW_TIME_CARD) {
					if(workManagementMultiple) {
						self.columns([
							{ headerText: 'コード', key: 'code', width: 100, hidden: true },
							{ headerText: nts.uk.resource.getText('KDP002_30'), key: 'date', width: 60 },
							{ headerText: nts.uk.resource.getText('KDP002_33', ['#Com_WorkIn']), key: 'workIn1', width: 90 },
							{ headerText: nts.uk.resource.getText('KDP002_34', ['#Com_WorkOut']), key: 'workOut1', width: 100 }
						]);
						//self.bindItemData(start.timeCard.listAttendances);
					} else {
						self.columns([
							{ headerText: 'コード', key: 'code', width: 100, hidden: true },
							{ headerText: nts.uk.resource.getText('KDP002_30'), key: 'date', width: 60 },
							{ headerText: nts.uk.resource.getText('KDP002_33', ['#Com_WorkIn']), key: 'workIn1', width: 90 },
							{ headerText: nts.uk.resource.getText('KDP002_34', ['#Com_WorkOut']), key: 'workOut1', width: 90 },
							{ headerText: nts.uk.resource.getText('KDP002_35', ['#Com_WorkIn']), key: 'workIn2', width: 90 },
							{ headerText: nts.uk.resource.getText('KDP002_36', ['#Com_WorkOut']), key: 'workOut2', width: 110 }
						]);
						//self.bindItemData(start.timeCard.listAttendances);
					}
				}

				self.yearMonth.subscribe(() => {
					self.currentCode(null);
				})
			}

			setTimeStampType(stampData) {
				let value = stampData.buttonValueType;

				if (ButtonType.GOING_TO_WORK == value || ButtonType.RESERVATION_SYSTEM == value) {
					stampData.timeStampType = `<div class='full-width' style='text-align: left'>` + stampData.stampArtName + '</div>';
					return;
				}
				if (ButtonType.WORKING_OUT == value) {
					stampData.timeStampType = `<div class='full-width' style='text-align: right'>` + stampData.stampArtName + '</div>';
					return;
				}
				stampData.timeStampType = stampData.stampArtName ? `<div class='full-width' style='text-align: center'>` + stampData.stampArtName + '</div>' : '';
			}

			bindItemData(items: Array<any>) {
				const model = this;

				if (model.displayMethod() == model.displayType.DISPLAY) {
					let idx = 1;

					items = _.orderBy(items, ['stampTimeWithSec'], ['desc']);
					items.forEach(stampData => {
						stampData.code = ++idx;
						let formatedStamp = nts.uk.time.applyFormat("Short_YMDW", stampData.stampDate);

						if (moment(stampData.stampDate).day() == 6) {
							formatedStamp = "<span class='color-schedule-saturday'> " + formatedStamp + "</span>";
						} else if (moment(stampData.stampDate).day() == 0) {
							formatedStamp = "<span class='color-schedule-sunday'> " + formatedStamp + "</span>";
						} else {
							formatedStamp = "<span class=''> " + formatedStamp + "</span>";
						}
						stampData.stampDate = formatedStamp;
						stampData.stampHowAndTime = "<div class='inline-bl'>" + stampData.stampHow + "</div>" + stampData.stampTime;
						model.setTimeStampType(stampData);
					});
					model.items(items);
				} else if (model.displayMethod() == model.displayType.SHOW_TIME_CARD) {
					let idx = 1;

					items.forEach(timeCard => {
						timeCard.code = ++idx;
						let formatedCardTime = moment(timeCard.date).format(DATE_AND_DAY_FORMAT);
						if (moment(timeCard.date).day() == 6) {
							formatedCardTime = "<span class='color-schedule-saturday' >" + formatedCardTime + "</span>";
						} else if (moment(timeCard.date).day() == 0) {
							formatedCardTime = "<span class='color-schedule-sunday'>" + formatedCardTime + "</span>";
						} else {
							formatedCardTime = "<span class=''>" + formatedCardTime + "</span>";
						}
						
						let systemDate = model.systemDate();
						
						if (timeCard.date === systemDate && systemDate.substr(0, 7) === model.yearMonth()) {
							model.currentCode(timeCard.code);
							self.setScroll(timeCard.code);
						}
						
						timeCard.date = formatedCardTime;
						timeCard.workIn1 = timeCard.workIn1 ? nts.uk.time.format.byId("ClockDay_Short_HM", timeCard.workIn1) : null;
						timeCard.workOut1 = timeCard.workOut1 ? nts.uk.time.format.byId("ClockDay_Short_HM", timeCard.workOut1) : null;
						timeCard.workIn2 = timeCard.workIn2 ? nts.uk.time.format.byId("ClockDay_Short_HM", timeCard.workIn2) : null;
						timeCard.workOut2 = timeCard.workOut2 ? nts.uk.time.format.byId("ClockDay_Short_HM", timeCard.workOut2) : null;
						
					});
					model.items(items);
				}
			}
		}

		enum ButtonType {
			// 系
			GOING_TO_WORK = 1,
			// 系
			WORKING_OUT = 2,
			// "外出系"
			GO_OUT = 3,
			// 戻り系
			RETURN = 4,
			// 予約系
			RESERVATION_SYSTEM = 5
		}

		interface IStartPage {
			timeCard: ITimeCard;
			stampSetting: IStampSettingPerson;
			stampDataOfEmployees: IStampHistoryInfo;
			stampToSuppress: IStampToSuppress;
			stampResultDisplay: IStampResultDisplay;
		}

		interface IStampResultDisplay {
			companyId: string;
			notUseAttr: number;
			displayItemId: Array<number>;
		}

		interface IStampToSuppress {
			isUse: boolean;
			goingToWork: boolean;
			departure: boolean;
			goOut: boolean;
			turnBack: boolean;
		}

		interface ITimeCard {
			employeeId: string;
			IAttendanceOneDay: Array<IAttendanceOneDay>;
		}

		interface IAttendanceOneDay {
			date: string;
			workIn1: string;
			workOut1: string;
			workIn2: string;
			workOut2: string;
		}

		interface ITimeActualStamp {
			numberOfReflectionStamp: number;

			actualAfterRoundingTime: string;
			actualTimeWithDay: string;
			actualLocationCode: string;
			actualStampSourceInfo: number;

			stampAfterRoundingTime: string;
			stampTimeWithDay: string;
			stampLocationCode: string;
			stampStampSourceInfo: number;
		}

		interface IStampHistoryInfo {
			code: string;
			stampDate: string;
			stampHowAndTime: string;
			timeStampType: string;
		}

		interface IStampSettingPerson {
			buttonEmphasisArt: boolean;
			historyDisplayMethod: number;
			correctionInterval: number;
			textColor: string;
			backGroundColor: string;
		}
	}
}

var setScroll = function(currentCode: number){
	$( "#time-card-list_scrollContainer" ).scrollTop(24*(currentCode-3));
}