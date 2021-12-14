module nts.uk.at.view.kdp010.i {
	import block = nts.uk.ui.block;
    import info = nts.uk.ui.dialog.info;
	import error = nts.uk.ui.dialog.error;
    import errors = nts.uk.ui.errors;
	import ajax = nts.uk.request.ajax;
	export module viewmodel {
		const paths: any = {
	        getSettingCommonStamp: "at/record/stamp/timestampinputsetting/getSettingCommonStamp"
	    }
		export class ScreenModel {
			// H1_2
			optionHighlight: KnockoutObservableArray<any> = ko.observableArray([
				{ id: 1, name: nts.uk.resource.getText("KDP010_108") },
				{ id: 0, name: nts.uk.resource.getText("KDP010_109") }
			]);
			
			selectedHighlight: KnockoutObservable<number> = ko.observable(1);
			
			// H2_2
			contentsStampType: KnockoutObservableArray<any> = ko.observableArray([]);
			selectedDay: KnockoutObservable<number> = ko.observable(1);
			selectedDayOld: KnockoutObservable<number> = ko.observable(1);
			
			supportWplSetOption: KnockoutObservableArray<any> = ko.observableArray([
				{ id: 0, name: nts.uk.resource.getText("KDP010_341") },
				{ id: 1, name: nts.uk.resource.getText("KDP010_342") }
			]);
			supportWplSet: KnockoutObservable<number> = ko.observable(0);
			supportWplSetEnable: KnockoutObservable<boolean> = ko.computed(():boolean => {
				return this.selectedHighlight() == 1 && (this.selectedDay() == 14 || this.selectedDay() == 16 || this.selectedDay() == 17 || this.selectedDay() == 18);
			});
			
			// H3_2
			optionStamping: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.GoingOutReason);
			selectedStamping: KnockoutObservable<number> = ko.observable(0);
			selectedStampingEnable: KnockoutObservable<boolean> = ko.computed(():boolean => {
				return this.selectedHighlight() == 1 && this.selectedDay() == 8;
			});

			// H4_2
			simpleValue: KnockoutObservable<string> = ko.observable("");

			// H5_2
			letterColors: KnockoutObservable<string> = ko.observable('#ffffff');

			// H6_2
			backgroundColors: KnockoutObservable<string> = ko.observable('#127D09');

			// H7_2
			optionAudio: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.AudioType);
			selectedAudio: KnockoutObservable<number> = ko.observable(0);

			defaultData: KnockoutObservable<number> = ko.observable(null);

			buttonPositionNo: KnockoutObservable<number> = ko.observable('');
			dataStampPage: KnockoutObservableArray<StampPageCommentCommand> = ko.observable(new StampPageCommentCommand({}));
			dataShare: KnockoutObservableArray<any> = ko.observableArray([]);

			lstChangeClock: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ChangeClockArt);
			lstChangeCalArt: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ChangeCalArt);
			lstContents: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ContentsStampType);
			lstDataShare: KnockoutObservableArray<any> = ko.observableArray();
			lstData: StampTypeCommand = null;
			isFocus: KnockoutObservable<boolean> = ko.observable(true);
			isChange: KnockoutObservable<number> = ko.observable(0);
			checkGoOut :  KnockoutObservable<number> = ko.observable(0);
            
            showSelectedAudio = ko.observable(false);
			constructor() {
				let self = this;
				self.selectedDay.subscribe((newValue) => {
					self.getDataFromContents(newValue);
					if (self.isChange() == 0) {
						let name = _.find(self.lstContents(), function(itemEmp) { return itemEmp.value == newValue; });
						self.simpleValue(name.name);
					}
					
					if(!_.isNil(newValue) && newValue == 8 && self.checkGoOut() != 1){
						self.selectedStamping(0);
					}
					
					if(!_.isNil(newValue) && newValue != 8){
						self.selectedStamping(0);
					}
					
				})

				self.simpleValue.subscribe(function(codeChanged: string) {
						self.simpleValue($.trim(self.simpleValue()));
					});

				self.selectedHighlight.subscribe((newValue) => {
					if (self.selectedHighlight() == 1)
						nts.uk.ui.errors.clearAll();
				})
				$('.ntsRadioBox').focus();
			}
            /**
             * start page  
             */
			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();
				self.isChange(1);
				self.getDataStamp();
				self.isChange(0);
				self.getDataFromContents(self.selectedDay());
				block.invisible();
				let tg = __viewContext.enums.ContentsStampType;
				ajax(paths.getSettingCommonStamp).done(function(data: any) {
					if(!data.supportUse){
						_.remove(tg, (n:any) => {return n.value == 14 || n.value == 15 || n.value == 16 || n.value == 17 || n.value == 18;});
					}
					if(!data.temporaryUse){
						_.remove(tg, (n:any) => {return n.value == 12 || n.value == 13;});
					}
					self.contentsStampType(tg);
					dfd.resolve();
				}).fail(function(res:any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
				return dfd.promise();
			}
			
			public getDataStamp() {

				let self = this;
				self.dataShare = nts.uk.ui.windows.getShared('KDP010_G');
                self.showSelectedAudio(self.dataShare.fromScreen === 'A');
				self.buttonPositionNo(self.dataShare.buttonPositionNo);
				if (self.dataShare.dataShare != undefined) {
					let data = self.dataShare.dataShare.lstButtonSet ? self.dataShare.dataShare.lstButtonSet.filter(x => x.buttonPositionNo == self.dataShare.buttonPositionNo)[0] : self.dataShare.dataShare;
					if (data) {
						self.checkGoOut(1);
						self.letterColors(data.buttonDisSet.buttonNameSet.textColor);
						self.simpleValue(data.buttonDisSet.buttonNameSet.buttonName);
						self.backgroundColors(data.buttonDisSet.backGroundColor);
						self.selectedStamping((data.buttonType.stampType == undefined || data.buttonType.stampType.goOutArt == null) ? 0 : data.buttonType.stampType.goOutArt);
						self.selectedAudio(data.audioType);
						self.selectedHighlight(data.usrArt);
						self.getTypeButton(data);
						self.supportWplSet((data.supportWplSet == null || data.supportWplSet == undefined) ? 0 : data.supportWplSet);
					} else {
						let name = _.find(self.lstContents(), function(itemEmp) { return itemEmp.value == 1; });
						self.simpleValue(name.name);
					}
				}
				$(document).ready(function() {
					$('#highlight-radio').focus();
				});
			}

			public getSimpleValue(value: number) {
				let self = this;
				let name = _.find(self.lstContents(), function(itemEmp) { return itemEmp.value == 1; });
				self.simpleValue(name.name);
			}

            /**
             * Pass Data to A
             */
			public passData(): void {
				let self = this;
				$('#correc').trigger("validate");
				if (nts.uk.ui.errors.hasError() && self.selectedHighlight() == 1) {
					return;
				}
				self.dataStampPage = ({
					buttonPositionNo: self.buttonPositionNo(),
					buttonDisSet: ({
						buttonNameSet: ({
							textColor: self.letterColors(),
							buttonName: self.simpleValue()
						}),
						backGroundColor: self.backgroundColors()
					}),
					buttonType: ({
						reservationArt: self.lstData.reservationArt,
						stampType: self.selectedDay() == 20 || self.selectedDay() == 19 ? ({}) : ({
							changeHalfDay: self.lstData.changeHalfDay,
							goOutArt: self.selectedDay() == 8 ? self.selectedStamping() : null,
							setPreClockArt: self.lstData.setPreClockArt,
							changeClockArt: self.lstData.changeClockArt,
							changeCalArt: self.lstData.changeCalArt
						})
					}),
					usrArt: self.selectedHighlight(),
					audioType: self.selectedAudio(),
					supportWplSet: self.supportWplSetEnable()?self.supportWplSet():null
				});

				if (self.dataShare.dataShare == undefined || self.dataShare.dataShare.lstButtonSet == undefined) {
					self.dataShare = self.dataStampPage;
				}

				if (self.dataShare.dataShare != undefined) {
					let selectedIndex = _.findIndex(self.dataShare.dataShare.lstButtonSet, (obj) => { return obj.buttonPositionNo == self.buttonPositionNo(); });
					if (selectedIndex >= 0) {
						self.dataShare.dataShare.lstButtonSet[selectedIndex] = self.dataStampPage;
					}
					else {
						self.dataShare.dataShare.lstButtonSet.push(self.dataStampPage);
					}
				}

				let shareG = self.dataShare;
				nts.uk.ui.windows.setShared('KDP010_H', shareG);
				nts.uk.ui.windows.close();
			}
			public getTypeButton(data: any): void {
				let self = this,
					changeClockArt = data.buttonType.stampType == null ? null: data.buttonType.stampType.changeClockArt,
					changeCalArt = data.buttonType.stampType == null ? null: data.buttonType.stampType.changeCalArt,
					setPreClockArt = data.buttonType.stampType == null ? null: data.buttonType.stampType.setPreClockArt,
					changeHalfDay = data.buttonType.stampType == null ? null: data.buttonType.stampType.changeHalfDay,
					reservationArt = data.buttonType.reservationArt;
				let typeNumber = self.checkType(changeClockArt, changeCalArt, setPreClockArt, changeHalfDay, reservationArt);
				self.selectedDay(typeNumber);
				self.selectedDayOld(typeNumber);
			}


			public checkType(changeClockArt: number, changeCalArt: number, setPreClockArt: number, changeHalfDay: any, reservationArt: number): number {
				if (changeCalArt == 0 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
					if (changeClockArt == 0)
						return 1;

					if (changeClockArt == 1)
						return 5;

					if (changeClockArt == 4)
						return 8;

					if (changeClockArt == 5)
						return 9;

					if (changeClockArt == 2)
						return 10;

					if (changeClockArt == 3)
						return 11;

					if (changeClockArt == 7)
						return 12;

					if (changeClockArt == 9)
						return 13;

					if (changeClockArt == 6)
						return 14;

					if (changeClockArt == 8)
						return 15;

					if (changeClockArt == 12)
						return 16;
				}
				if (changeClockArt == 0 && changeCalArt == 0 && setPreClockArt == 1 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0)
					return 2;

				if (changeCalArt == 1 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
					if (changeClockArt == 0)
						return 3;

					if (changeClockArt == 6)
						return 17;
				}

				if (changeCalArt == 3 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
					if (changeClockArt == 0)
						return 4;

					if (changeClockArt == 6)
						return 18;
				}

				if (changeClockArt == 1 && changeCalArt == 0 && setPreClockArt == 2 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0)
					return 6;

				if (changeClockArt == 1 && changeCalArt == 2 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0)
					return 7;

				if ((changeClockArt == "" || changeClockArt == null) && (changeCalArt == "" || changeCalArt == null) && (setPreClockArt == "" || setPreClockArt == null) && (changeHalfDay == "" || changeHalfDay == null) && reservationArt == 1)
					return 19;

				if ((changeClockArt == "" || changeClockArt == null) && (changeCalArt == "" || changeCalArt == null) && (setPreClockArt == "" || setPreClockArt == null) && (changeHalfDay == "" || changeHalfDay == null) && reservationArt == 2)
					return 20;
			}

			public getDataFromContents(value: number): void {
				let self = this;
				switch (self.selectedDay()) {
					case 1:
						self.lstData = { changeClockArt: 0, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 2:
						self.lstData = { changeClockArt: 0, changeCalArt: 0, setPreClockArt: 1, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 3:
						self.lstData = { changeClockArt: 0, changeCalArt: 1, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 4:
						self.lstData = { changeClockArt: 0, changeCalArt: 3, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 5:
						self.lstData = { changeClockArt: 1, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 6:
						self.lstData = { changeClockArt: 1, changeCalArt: 0, setPreClockArt: 2, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 7:
						self.lstData = { changeClockArt: 1, changeCalArt: 2, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 8:
						self.lstData = { changeClockArt: 4, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 9:
						self.lstData = { changeClockArt: 5, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 10:
						self.lstData = { changeClockArt: 2, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 11:
						self.lstData = { changeClockArt: 3, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 12:
						self.lstData = { changeClockArt: 7, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 13:
						self.lstData = { changeClockArt: 9, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 14:
						self.lstData = { changeClockArt: 6, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 15:
						self.lstData = { changeClockArt: 8, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 16:
						self.lstData = { changeClockArt: 12, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 17:
						self.lstData = { changeClockArt: 6, changeCalArt: 1, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 18:
						self.lstData = { changeClockArt: 6, changeCalArt: 3, setPreClockArt: 0, changeHalfDay: 0, reservationArt: 0 };
						break;
					case 19:
						self.lstData = { changeClockArt: "", changeCalArt: "", setPreClockArt: "", changeHalfDay: "", reservationArt: 1 };
						break;
					case 20:
						self.lstData = { changeClockArt: "", changeCalArt: "", setPreClockArt: "", changeHalfDay: "", reservationArt: 2 };
						break;
				}

			}

            /**
             * Close dialog
             */
			public closeDialog(): void {
				let self = this;
				nts.uk.ui.windows.close();
			}

		}

	}

	// StampPageCommentCommand
	export class StampPageCommentCommand {

		/** ページNO */
		pageComment: string;
		/** ページ名 */
		commentColor: string;

		constructor(param: IStampPageCommentCommand) {
			this.pageComment = param.pageComment;
			this.commentColor = param.commentColor;
		}

	}

	interface IStampPageCommentCommand {
		pageComment: string;
		commentColor: string;
	}

	// ButtonSettingsCommand
	export class StampPageCommentCommand {
		/** ボタン位置NO */
		buttonPositionNo: number;
		/** ボタンの表示設定 */
		buttonDisSet: ButtonDisSetCommand;
		/** ボタン種類 */
		buttonType: ButtonTypeCommand;
		/** 使用区分 */
		usrArt: number;
		/** 音声使用方法 */
		audioType: number;

		constructor(param: IStampPageCommentCommand) {
			this.buttonPositionNo = param.buttonPositionNo;
			this.buttonDisSet = param.buttonDisSet;
			this.buttonType = param.buttonType;
			this.usrArt = param.usrArt;
			this.audioType = param.audioType;
		}

	}

	interface IStampPageCommentCommand {
		/** ボタン位置NO */
		buttonPositionNo: number;
		/** ボタンの表示設定 */
		buttonDisSet: ButtonDisSetCommand;
		/** ボタン種類 */
		buttonType: ButtonTypeCommand;
		/** 使用区分 */
		usrArt: number;
		/** 音声使用方法 */
		audioType: number;
	}

	// ButtonDisSetCommand
	export class ButtonDisSetCommand {

		/** ボタン名称設定 */
		buttonNameSet: ButtonNameSetCommand;
		/** 背景色 */
		backGroundColor: string;

		constructor(param: IButtonDisSetCommand) {
			this.buttonNameSet = param.buttonNameSet;
			this.backGroundColor = param.backGroundColor;
		}

	}

	interface IButtonDisSetCommand {
		buttonNameSet: ButtonNameSetCommand;
		backGroundColor: string;
	}

	// ButtonNameSetCommand
	export class ButtonNameSetCommand {

		/** ボタン名称設定 */
		textColor: string;
		/** 背景色 */
		buttonName: string;

		constructor(param: IButtonNameSetCommand) {
			this.textColor = param.textColor;
			this.buttonName = param.buttonName;
		}

	}

	interface IButtonNameSetCommand {
		textColor: string;
		buttonName: string;
	}

	// ButtonTypeCommand
	export class ButtonTypeCommand {

		/** 予約区分 */
		reservationArt: number;
		/** 打刻種類 */
		stampType: StampTypeCommand;

		constructor(param: IButtonTypeCommand) {
			this.reservationArt = param.reservationArt;
			this.stampType = param.stampType;
		}

	}

	interface IButtonTypeCommand {
		reservationArt: number;
		stampType: StampTypeCommand;
	}

	// StampTypeCommand
	export class StampTypeCommand {

		/** 勤務種類を半休に変更する */
		changeHalfDay: any;
		/** 外出区分 */
		goOutArt: number;
		/** 所定時刻セット区分 */
		setPreClockArt: number;
		/** 時刻変更区分 */
		changeClockArt: number;
		/** 計算区分変更対象 */
		changeCalArt: number;

		constructor(param: IStampTypeCommand) {
			this.changeHalfDay = param.changeHalfDay;
			this.goOutArt = param.goOutArt;
			this.setPreClockArt = param.setPreClockArt;
			this.changeClockArt = param.changeClockArt;
			this.changeCalArt = param.changeCalArt;
		}

	}

	interface IStampTypeCommand {
		changeHalfDay: any;
		goOutArt: number
		setPreClockArt: number;
		changeClockArt: number;
		changeCalArt: number;
	}
	__viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        });   
    });
}