module nts.uk.at.view.kdp010.h {
	import getShared = nts.uk.ui.windows.getShared;
    import block = nts.uk.ui.block;
    import info = nts.uk.ui.dialog.info;
	import error = nts.uk.ui.dialog.error;
    import errors = nts.uk.ui.errors;
	import ajax = nts.uk.request.ajax;
	import getText = nts.uk.resource.getText;
	import getIcon = nts.uk.at.view.kdp.share.getIcon;
	import checkType = nts.uk.at.view.kdp.share.checkType;
	import GetStampTemplate = nts.uk.at.view.kdp010.GetStampTemplate;
	
	export module viewmodel {
		const paths: any = {
	        saveStampPage: "at/record/stamp/timestampinputsetting/saveStampPage",
	        getStampPage: "at/record/stamp/management/getStampPage",
			getSettingCommonStamp: "at/record/stamp/timestampinputsetting/getSettingCommonStamp",
	        deleteStampPage: "at/record/stamp/management/delete"
	    }
		export class ScreenModel {
			// G2_2
			optionPage: KnockoutObservableArray<any> = ko.observableArray([
				{ code: 1, name: getText("KDP010_98", "1") },
				{ code: 2, name: getText("KDP010_98", "2") },
				{ code: 3, name: getText("KDP010_98", "3") },
				{ code: 4, name: getText("KDP010_98", "4") },
				{ code: 5, name: getText("KDP010_98", "5") }]);
			optionPopup: KnockoutObservableArray<any> = ko.observableArray([
				{ code: 1, name: getText("KDP010_336")},
				{ code: 2, name: getText("KDP010_337")},
				{ code: 3, name: getText("KDP010_338")},
				{ code: 4, name: getText("KDP010_339", ['{#Com_Workplace}'])}
			]);
			
			selectedPage: KnockoutObservable<number> = ko.observable(1);
			// G3_2
			pageName: KnockoutObservable<string> = ko.observable("");
			// G4_2
			optionLayout: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ButtonLayoutType);
			selectedLayout: KnockoutObservable<number> = ko.observable(0);
			// G5_2
			commentDaily: KnockoutObservable<string> = ko.observable("");
			// G6_2
			letterColors: KnockoutObservable<string> = ko.observable("#7F7F7F");
			dataKdpH: any;
			dataShare: any = null;
			isDel: KnockoutObservable<boolean> = ko.observable(false);
			buttonInfo: KnockoutObservableArray<model.ButtonDisplay> = ko.observableArray([]);
			checkLayout: KnockoutObservable<boolean> = ko.observable(false);
			currentSelectLayout: KnockoutObservable<number> = ko.observable(0);
			settingsStampUse: any;
            
             /**
             * 運用方法 (0:共有打刻 1:個人利用 2:ICカード 3:スマホ打刻)
             * 0: Shared stamp, 1: Personal use, 2: IC card, 3: smartphone engraving 5:RICOH打刻"										
             */
            mode: number;

			templateClicked: boolean = false;

			constructor() {
				let self = this;
                self.mode = getShared('STAMP_MEANS');
                
				self.selectedLayout.subscribe((newValue) => {
					if(!self.templateClicked){
						self.getInfoButton(null);
						self.getData(newValue);	
					}else{
						self.templateClicked = false;
					}
					self.checkLayout(true);
					errors.clearAll();
				});

				self.pageName.subscribe(function() {
					self.pageName($.trim(self.pageName()));
				});
				
				self.selectedPage.subscribe(() => {
					block.grayout();
					self.checkLayout(false);
					self.startPage();
					errors.clearAll();
					block.clear();
				});
				// Init popup
				$(".popup-area").ntsPopup({
					trigger: ".popupButton",
				    position: {
				        my: "left top",
				        at: "left bottom",
				        of: ".popupButton"
				    },
				    showOnStart: false,
				    dismissible: true
				});
			}
            /**
             * start page  
             */
			public startPage(): JQueryPromise<any> {
				let self = this;
				let lstButton: model.ButtonDisplay[] = [];
				for (let i = 1; i < 9; i++) {
					lstButton.push(new model.ButtonDisplay());
				}
				self.buttonInfo(lstButton);
				return self.getData(self.selectedLayout());
			}
			getSettingCommonStamp(): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred();
				block.invisible();
				ajax(paths.getSettingCommonStamp).done(function(data: any) {
					self.settingsStampUse = data; 
					if(!data.supportUse){
						self.optionPopup([{ code: 1, name: getText("KDP010_336")}]);
					}
					dfd.resolve();
				}).fail(function(res:any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
				return dfd.promise();
			}
			getData(newValue: number): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred();
                let param: any = {mode: self.mode, pageNo: self.selectedPage()};
				block.invisible();
				$.when(self.getSettingCommonStamp()).done(()=>{
					ajax("at", paths.getStampPage, param).done(function(totalTimeArr: any) {
						if (totalTimeArr && (newValue == totalTimeArr.buttonLayoutType)) {
							_.forEach(totalTimeArr.lstButtonSet, (btn:any) => {
								if(self.checkNotUseBtnSupport(btn.buttonType)){
									btn.usrArt = 0;								
								}
							});
							self.pageName(totalTimeArr.stampPageName);
							self.commentDaily(totalTimeArr.stampPageComment.pageComment);
							self.letterColors(totalTimeArr.stampPageComment.commentColor);
							if (totalTimeArr.lstButtonSet != null) {
								self.getInfoButton(totalTimeArr.lstButtonSet);
							}
							self.dataShare = totalTimeArr;
							self.isDel(true);
						} else {
							self.setColor("#D9D9D9", ".btn-name");
							self.getInfoButton(null);
							self.dataShare = null;
							self.isDel(false);
							if (self.checkLayout() == false) {
								self.pageName("");
								self.commentDaily("");
								self.letterColors("#7F7F7F");
							}
						}
	
						if (totalTimeArr) {
							if (self.checkLayout() == false)
								self.selectedLayout(totalTimeArr.buttonLayoutType);
							else
								self.selectedLayout(newValue);
						} else {
							if (self.checkLayout() == false)
								self.selectedLayout(0);
							else
								self.selectedLayout(newValue);
						}
						$('#combobox').focus();
						dfd.resolve();
					}).fail(function(error: any) {
						alert(error.message);
						dfd.reject(error);
					}).always(() => {
						block.clear();
				});	
				});
				return dfd.promise();
			}
			
			popupSelected(selected: any){
				let self = this;
				self.templateClicked = true;
				self.selectedLayout(0);
				self.templateClicked = false;
				let data = {
					pageNo: self.selectedPage(),
					stampPageName: self.pageName(),
					stampPageComment: {
						pageComment: self.commentDaily(),
						commentColor: self.letterColors(),
					},
					buttonLayoutType: self.selectedLayout(),
					lstButtonSet: GetStampTemplate(selected.code, self.mode)
				};
				self.dataShare = data;
				self.getInfoButton(self.dataShare.lstButtonSet);
				$(".popup-area").ntsPopup("hide");
				setTimeout(() => {
                	$('#correc-text').focus();
            	}, 100);
				
			}
			
			registration() {
				let self = this;
				if (!self.dataShare || self.dataShare.lstButtonSet.length == 0) {
					error({ messageId: "Msg_1627" });
					return;
				}
				self.registrationLayout();

			}

            /**
             * Pass Data to A
             */
			public registrationLayout() {
				let self = this;
				$('#correc-text').trigger("validate");
				if (errors.hasError()) {
					return;
				}
				block.invisible();
				if ((self.dataKdpH == undefined || self.dataKdpH.length == 0) && self.isDel() == true) {
					let data = {
						dataShare: self.dataShare,
						buttonPositionNo: self.dataShare.lstButtonSet[0].buttonPositionNo
					}
					self.dataKdpH = data;
				}
				if ((self.dataKdpH == undefined) && self.isDel() == false) {
					let data = {
						dataShare: new Array(),
						buttonPositionNo: self.selectedLayout()
					}
					self.dataKdpH = data;
				}
				
				if(!_.isNil(self.dataKdpH) && self.dataKdpH.dataShare.pageNo != self.dataShare.pageNo) {
					let data = {
						dataShare: self.dataShare,
						buttonPositionNo: self.dataShare.lstButtonSet[0].buttonPositionNo
					}
					self.dataKdpH = data;
				}
				// Data from Screen 
				let lstButton = new Array<model.ButtonSettingsCommand>();
				let stampTypes = null;
				if (self.dataKdpH.buttonPositionNo != undefined) {
					_.forEach(self.dataKdpH.dataShare.lstButtonSet, (item) => {
						if (item.buttonType.reservationArt == 0) {
							stampTypes = new model.StampTypeCommand({
								changeHalfDay: item.buttonType.stampType.changeHalfDay == 0 ? false : true,
								goOutArt: item.buttonType.stampType.goOutArt,
								setPreClockArt: item.buttonType.stampType.setPreClockArt,
								changeClockArt: item.buttonType.stampType.changeClockArt,
								changeCalArt: item.buttonType.stampType.changeCalArt
							});
							
						} else {
							stampTypes = null
						};
						let lstButtonSet = new model.ButtonSettingsCommand({
							buttonPositionNo: item.buttonPositionNo,
							buttonDisSet: new model.ButtonDisSetCommand({
								buttonNameSet: new model.ButtonNameSetCommand({
									textColor: item.buttonDisSet.buttonNameSet.textColor,
									buttonName: item.buttonDisSet.buttonNameSet.buttonName
								}),
								backGroundColor: item.buttonDisSet.backGroundColor
							}),
							buttonType: new model.ButtonTypeCommand({
								reservationArt: item.buttonType.reservationArt,
								stampType: stampTypes
							}),
							usrArt: item.usrArt,
							audioType: item.audioType,
							supportWplSet: item.supportWplSet,
							taskChoiceArt: item.taskChoiceArt
						});
						lstButton.push(lstButtonSet);
					});
				}

				let data = new model.StampPageLayoutCommand({
					pageNo: self.selectedPage(),
					stampPageName: self.pageName(),
					stampPageComment: new model.StampPageCommentCommand({
						pageComment: self.commentDaily(),
						commentColor: self.letterColors(),
					}),
					buttonLayoutType: self.selectedLayout(),
					lstButtonSet: lstButton,
					stampMeans: self.mode
				});
				ajax(paths.saveStampPage, data).done(function() {
					self.isDel(true);
					self.currentSelectLayout(self.selectedLayout());
					info({ messageId: "Msg_15" }).then(() => {
						self.dataKdpH = undefined;
						$(document).ready(function() {
							$('#combobox').focus();
						});
					});

				}).fail(function(res:any) {
					error({ messageId: res.messageId });
				}).always(() => {
					block.clear();
				});
			}
            
			public getInfoButton(lstButtonSet: any) {
				let self = this;
				if (lstButtonSet == null) {
					for (let i = 0; i < 8; i++) {
						self.buttonInfo()[i].clean();
					}
				} else {
					for (let i = 0; i < 8; i++) {
						self.buttonInfo()[i].buttonName(lstButtonSet.filter((x: any) => x.buttonPositionNo == i + 1)[0] == null ? "" : lstButtonSet.filter((x:any) => x.buttonPositionNo == i + 1)[0].buttonDisSet.buttonNameSet.buttonName);
						let lstBtn = lstButtonSet.filter((x: any) => x.buttonPositionNo == i + 1);
						if (lstBtn[0] == null || (lstBtn[0] != null && lstBtn[0].usrArt == 0)) {
							self.buttonInfo()[i].clean();
						} else {
							let btn = lstButtonSet.filter((x: any) => x.buttonPositionNo == i + 1)[0];
							self.buttonInfo()[i].update(
								btn.usrArt, 
								btn.buttonDisSet.buttonNameSet.buttonName, 
								btn.buttonDisSet.backGroundColor, 
								btn.buttonDisSet.buttonNameSet.textColor, 
								self.getUrlImg(btn.buttonType));
						}
					}
				}
			}
			
			checkNotUseBtnSupport(buttonType: any): boolean{
				let self = this;
				let value: number = checkType(buttonType.stampType ? buttonType.stampType.changeClockArt: null, 
								buttonType.stampType ? buttonType.stampType.changeCalArt : null, 
								buttonType.stampType ? buttonType.stampType.setPreClockArt: null, 
								buttonType.stampType ? buttonType.stampType.changeHalfDay: null, 
								buttonType.reservationArt);
				if(value == 14 || value == 15 || value == 16 || value == 17 || value == 18){
					return !self.settingsStampUse.supportUse;
				}
				if(value == 12 || value == 13){
					return !self.settingsStampUse.temporaryUse;
				}
				if(value == 10 || value == 11) {
					return !self.settingsStampUse.entranceExitUse;
				}
				return false;
			}
			
			getUrlImg(buttonType: any/*ButtonType sample on server */): string{
				return window.location.origin + "/nts.uk.com.js.web/lib/nittsu/ui/style/stylesheets/images/icons/numbered/" + getIcon(buttonType.stampType ? buttonType.stampType.changeClockArt: null, 
								buttonType.stampType ? buttonType.stampType.changeCalArt : null, 
								buttonType.stampType ? buttonType.stampType.setPreClockArt: null, 
								buttonType.stampType ? buttonType.stampType.changeHalfDay: null, 
								buttonType.reservationArt) + ".png";
			}
			

			public setColor(color: string, name: string) {
				$(name).css("background", color);
			}

			public deleteStamp(): JQueryPromise<any> {
				let self = this;
				let dfd = $.Deferred();
				let data = {
					pageNo: self.selectedPage(),
                    mode: self.mode
				};
				nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
					ajax(paths.deleteStampPage, data).done(function() {
						info({ messageId: "Msg_16" }).then(() => {
							$(document).ready(function() {
								$('#combobox').focus();
							});
						});
						self.getData(self.selectedLayout());
						self.pageName("");
						self.commentDaily("");
						self.letterColors("#7F7F7F");
						dfd.resolve();
					}).fail(function(error: any) {
						alert(error.message);
						dfd.reject(error);
					});
				}).ifNo(function() {
					block.clear();
				});
				return dfd.promise();
			}

            /**
             * Close dialog
             */
			public closeDialog(): void {
				nts.uk.ui.windows.close();
			}

			openHDialog(enumVal: number): void {
				let self = this;
				let shareH = {
					pageNo: self.selectedPage(),
					stampPageName: self.pageName(),
					stampPageComment: {
						pageComment: self.commentDaily(),
						commentColor: self.letterColors(),
					},

					buttonLayoutType: self.selectedLayout(),
					lstButtonSet: new Array()
				};
				let dataG = {
					dataShare: self.dataShare == null ? shareH : self.dataShare,
					buttonPositionNo: enumVal,
                    fromScreen: self.mode == 0? 'A': '',
					stampMeans: self.mode
				}
				nts.uk.ui.windows.setShared('KDP010_G', dataG);
				nts.uk.ui.windows.sub.modal("/view/kdp/010/i/index.xhtml").onClosed(() => {
					self.dataKdpH = nts.uk.ui.windows.getShared('KDP010_H');
					if (self.dataKdpH) {
						self.dataShare = self.dataKdpH.dataShare == undefined ? self.dataKdpH : self.dataKdpH.dataShare;
						if (self.dataKdpH.dataShare) {
							let dataH = self.dataKdpH.dataShare.lstButtonSet.filter((x:any) => x.buttonPositionNo == self.dataKdpH.buttonPositionNo)[0];
							if (dataH.usrArt == 0) {
								self.buttonInfo()[self.dataKdpH.buttonPositionNo - 1].usrArt(0);
								return;
							}
							let btn = dataH ? dataH : self.dataKdpH;
							self.buttonInfo()[self.dataKdpH.buttonPositionNo - 1].update(
								btn.usrArt, 
								btn.buttonDisSet.buttonNameSet.buttonName, 
								btn.buttonDisSet.backGroundColor, 
								btn.buttonDisSet.buttonNameSet.textColor, 
								self.getUrlImg(btn.buttonType));
						}
					}
				});
			}
		}

	}
	export module model {
		// StampPageLayoutCommand
		export class StampPageLayoutCommand {

			/** ページNO */
			pageNo: number;
			/** ページ名 */
			stampPageName: string;
			/** ページコメント */
			stampPageComment: StampPageCommentCommand;
			/** ボタン配置タイプ */
			buttonLayoutType: number;
			/** ボタン詳細設定リスト */
			lstButtonSet: Array<ButtonSettingsCommand>;
			
			stampMeans: number;

			constructor(param: IStampPageLayoutCommand) {
				this.pageNo = param.pageNo;
				this.stampPageName = param.stampPageName;
				this.stampPageComment = param.stampPageComment;
				this.buttonLayoutType = param.buttonLayoutType;
				this.lstButtonSet = param.lstButtonSet;
				this.stampMeans = param.stampMeans;
			}
		}

		interface IStampPageLayoutCommand {
			pageNo: number;
			stampPageName: string;
			stampPageComment: StampPageCommentCommand;
			buttonLayoutType: number;
			lstButtonSet: Array<ButtonSettingsCommand>;
			stampMeans: number;
		}

		// StampPageCommentCommand
		export class StampPageCommentCommand {

			/** ページNO */
			pageComment: string;
			/** ページ名 */
			commentColor: string;

			constructor(param?: IStampPageCommentCommand) {
				this.pageComment = param?param.pageComment:"";
				this.commentColor = param?param.commentColor:"";
			}
		}

		interface IStampPageCommentCommand {
			pageComment: string;
			commentColor: string;
		}

		// ButtonSettingsCommand
		export class ButtonSettingsCommand {
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
			
			/** 応援職場設定方法 */
			supportWplSet: number;

			/** 作業指定方法 */
			taskChoiceArt: number;

			constructor(param: IButtonSettingsCommand) {
				this.buttonPositionNo = param.buttonPositionNo;
				this.buttonDisSet = param.buttonDisSet;
				this.buttonType = param.buttonType;
				this.usrArt = param.usrArt;
				this.audioType = param.audioType;
				this.supportWplSet = param.supportWplSet;
				this.taskChoiceArt = param.taskChoiceArt;
			}
		}

		interface IButtonSettingsCommand {
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
			/** 応援職場設定方法 */
			supportWplSet : number;
			/** 作業指定方法 */
			taskChoiceArt: number;
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
			changeHalfDay: boolean;
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
			changeHalfDay: boolean;
			goOutArt: number;
			setPreClockArt: number;
			changeClockArt: number;
			changeCalArt: number;
		}

		export enum ButtonLayoutType {
			//就業
			大2小4 = 0,
			小8 = 1
		}
		export class ButtonDisplay {
			buttonName: KnockoutObservable<string> = ko.observable('');
			buttonColor: KnockoutObservable<string> = ko.observable('#D9D9D9');
			textColor: KnockoutObservable<string> = ko.observable('#D9D9D9');
			icon: KnockoutObservable<string> = ko.observable(null);
			usrArt: KnockoutObservable<number> = ko.observable(0);
			constructor() {
				let self = this;
				self.usrArt.subscribe((v: number) => {
					if(v == 0){
						self.buttonColor('#D9D9D9');
						self.textColor('#D9D9D9');
					}
				});
			}
			update(usrArt: number, buttonName: string, buttonColor: string, textColor: string, icon: string){
				this.buttonName(buttonName);
				this.buttonColor(buttonColor);
				this.textColor(textColor);
				this.icon(icon);
				this.usrArt(usrArt);
			}
			clean(){
				let self = this;
				self.usrArt(0);
				self.buttonName('');
				self.icon('');
			}
		}
	}
	__viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        });   
    });
}
