module nts.uk.at.view.kdp010.j {
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import info = nts.uk.ui.dialog.info;
    import error = nts.uk.ui.dialog.error;
    import confirm = nts.uk.ui.dialog.confirm;
	import ajax = nts.uk.request.ajax;
	import getIcon = nts.uk.at.view.kdp.share.getIcon;
	import checkType = nts.uk.at.view.kdp.share.checkType;
	
	export module viewmodel {
		const paths: any = {
			getSettingCommonStamp: "at/record/stamp/timestampinputsetting/getSettingCommonStamp",
	        getData: "at/record/stamp/timestampinputsetting/smartphonepagelayoutsettings/get",
	        save: "at/record/stamp/timestampinputsetting/saveStampPage",
	        del: "at/record/stamp/timestampinputsetting/smartphonepagelayoutsettings/del"
	    }
		export class ScreenModel {
            stampPageLayout:StampPageLayout = new StampPageLayout();
            isDel = ko.observable(false);
			optionPopup: KnockoutObservableArray<any> = ko.observableArray([
				{ code: 1, name: getText("KDP010_336")},
				{ code: 2, name: getText("KDP010_337")},
				{ code: 3, name: getText("KDP010_338")},
				{ code: 4, name: getText("KDP010_339", ['{#Com_Workplace}'])}
			]);
			hasFocus: KnockoutObservable<boolean> = ko.observable(false);
			settingsStampUse: any;
			constructor() {
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
			public startPage(): JQueryPromise<any> {
				let self = this;
                let dfd = $.Deferred();
				block.grayout();
				$.when(self.getData(), self.getSettingCommonStamp()).done(function() {
					dfd.resolve();
					$(document).ready(function() {
                        self.hasFocus(true);
                    });
				}).always(function() {
                    block.clear();
                });
                return dfd.promise();
			}
			
			getData(): JQueryPromise<any> {
				let self = this;
                let dfd = $.Deferred();
                let param = {pageNo:1};
				$.when(self.getSettingCommonStamp()).done(()=>{
	                ajax("at", paths.getData, param).done(function(data: any) {
	                    if (data) {
							_.forEach(data.lstButtonSet, (btn:any) => {
								if(self.checkNotUseBtnSupport(btn.buttonType)){
									btn.usrArt = 0;								
								}
							});
	                        self.stampPageLayout.update(data);
	                        self.isDel(true);
	                    }
	                    dfd.resolve();
	                }).fail(function (res: any) {
	                    error({ messageId: res.messageId });
	                });
                });
                return dfd.promise();
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
			
			popupSelected(selected: any){
				let self = this;
				self.stampPageLayout.setTemplate(selected.code);
				$(".popup-area").ntsPopup("hide");
				setTimeout(() => {
                	$('#pageComment').focus();
            	}, 100);
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
				return false;
			}
            
			public save() {
				let self = this;
                if(nts.uk.ui.errors.hasError()){
                    return;
                }else{
                    block.grayout();
                    ajax("at", paths.save, ko.toJS(self.stampPageLayout)).done(function() {
                        self.isDel(true);
                        info({ messageId: "Msg_15" }).then(()=>{
                            $(document).ready(function() {
                                $('#pageComment').focus();
                            });    
                        });
                    }).fail(function (res: any) {
                        error({ messageId: res.messageId }).then(()=>{
                            $(document).ready(function() {
                                $('#pageComment').focus();
                            });
                        });
                    }).always(function () {
                        block.clear();
                    });
                }
			}
            
			public deleteSetting() {
				let self = this;
                confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    block.grayout();
                    ajax("at", paths.del).done(function() {
                        self.stampPageLayout.clear();
						self.isDel(false);
                        info({ messageId: "Msg_16" }).then(()=>{
                            $(document).ready(function() {
                                $('#pageComment').focus();
                            });    
                        });
                    }).fail(function (res: any) {
                        error({ messageId: res.messageId });
                    }).always(function () {
                        block.clear();
                    });
                }).ifNo(()=>{
                    $('#pageComment').focus();
                });
			}
            
            public closeDialog(): void {
				nts.uk.ui.windows.close();
			}
		}

        class StampPageLayout {
            pageNo = 1;
            stampPageName = getText("KDP010_236");
            stampPageComment = new StampPageComment();
            buttonLayoutType = 0;
            lstButtonSet: any = [];
			//3:スマホ打刻
			stampMeans = 3;
            btn1 = new ButtonSettings();
            btn2 = new ButtonSettings();
            btn3 = new ButtonSettings();
            btn4 = new ButtonSettings();
            btn5 = new ButtonSettings();
            btn6 = new ButtonSettings();
            constructor(){}
            update(data?:any){
                let self = this;
                if(data){
                    self.stampPageComment.update(data.stampPageComment);
                    _.forEach(data.lstButtonSet, (item) => {
                        if(item.buttonPositionNo == 1){
                            self.btn1.update(item);
                            self.lstButtonSet.push(self.btn1);
                        }else if(item.buttonPositionNo == 2){
                            self.btn2.update(item);
                            self.lstButtonSet.push(self.btn2);
                        }else if(item.buttonPositionNo == 3){
                            self.btn3.update(item);
                            self.lstButtonSet.push(self.btn3);
                        }else if(item.buttonPositionNo == 4){
                            self.btn4.update(item);
                            self.lstButtonSet.push(self.btn4);
                        }else if(item.buttonPositionNo == 5){
                            self.btn5.update(item);
                            self.lstButtonSet.push(self.btn5);
                        }else if(item.buttonPositionNo == 6){
                            self.btn6.update(item);
                            self.lstButtonSet.push(self.btn6);
                        }
                    });
                }
            }
			setTemplate(selectedCode: number){
				let self = this;
				self.btn1.clear();
	            self.btn2.clear();
				self.btn3.clear();
	            self.btn4.clear();
				self.btn5.clear();
	            self.btn6.clear();
				self.lstButtonSet = [];
				_.forEach(GetStampTemplate(selectedCode, null), (item) => {
                    if(item.buttonPositionNo == 1){
                        self.btn1.update(item);
                        self.lstButtonSet.push(self.btn1);
                    }else if(item.buttonPositionNo == 2){
                        self.btn2.update(item);
                        self.lstButtonSet.push(self.btn2);
                    }else if(item.buttonPositionNo == 3){
                        self.btn3.update(item);
                        self.lstButtonSet.push(self.btn3);
                    }else if(item.buttonPositionNo == 4){
                        self.btn4.update(item);
                        self.lstButtonSet.push(self.btn4);
                    }else if(item.buttonPositionNo == 5){
                        self.btn5.update(item);
                        self.lstButtonSet.push(self.btn5);
                    }else if(item.buttonPositionNo == 6){
                        self.btn6.update(item);
                        self.lstButtonSet.push(self.btn6);
                    }
                });
			}
			clear(){
				let self = this;
            	self.stampPageComment.clear();
            	self.lstButtonSet = [];
	            self.btn1.clear();
	            self.btn2.clear();
				self.btn3.clear();
	            self.btn4.clear();
				self.btn5.clear();
	            self.btn6.clear();
			}
            
            setting(buttonPositionNo: number){
                let self = this;
                let dataI = {
                    dataShare: ko.toJS(self),
                    buttonPositionNo: buttonPositionNo
                }
                nts.uk.ui.windows.setShared('KDP010_G', dataI);
                nts.uk.ui.windows.sub.modal("/view/kdp/010/i/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared('KDP010_H');
                    if (data) {
                        _.forEach(data.dataShare.lstButtonSet, (item) => {
                            let buttonSet:ButtonSettings = _.find(self.lstButtonSet,['buttonPositionNo',item.buttonPositionNo]);
                            if(buttonSet){
                                buttonSet.update(item);
                            }else if(item.buttonPositionNo == 1){
                                self.btn1.update(item);
                                self.lstButtonSet.push(self.btn1);
                            }else if(item.buttonPositionNo == 2){
                                self.btn2.update(item);
                                self.lstButtonSet.push(self.btn2);
                            }else if(item.buttonPositionNo == 3){
                                self.btn3.update(item);
                                self.lstButtonSet.push(self.btn3);
                            }else if(item.buttonPositionNo == 4){
                                self.btn4.update(item);
                                self.lstButtonSet.push(self.btn4);
                            }else if(item.buttonPositionNo == 5){
                                self.btn5.update(item);
                                self.lstButtonSet.push(self.btn5);
                            }else if(item.buttonPositionNo == 6){
                                self.btn6.update(item);
                                self.lstButtonSet.push(self.btn6);
                            }
                        }); 
                    }
                    $('#btnSave').focus();
                });    
            }

			
        }
        
        class StampPageComment{
            pageComment = ko.observable("");
            commentColor = ko.observable("#000000");
            constructor(){}
            update(data?:any){
                let self = this;
                if(data){
                    self.pageComment(data.pageComment);
                    self.commentColor(data.commentColor);
                }
            }
			clear(){
				let self = this;
				self.pageComment("");
            	self.commentColor("#000000");
			}
        }
        
        class ButtonSettings {
            buttonPositionNo: number;
            buttonDisSet = new ButtonDisSet();
            buttonType: any = null;
            usrArt = ko.observable(0);
            audioType = 0;
			icon: KnockoutObservable<string> = ko.observable();
			supportWplSet: number;
            constructor(){}
            update(param: any){
                let self = this;
                if(param){
                    self.buttonPositionNo = param.buttonPositionNo;
                    self.buttonDisSet.update(param.buttonDisSet);
                    self.buttonType = param.buttonType;
					self.icon(self.getUrlImg(self.buttonType));
                    self.usrArt(param.usrArt);
                    self.supportWplSet = param.supportWplSet;
                }
            }

			clear(){
				let self = this;
                self.buttonDisSet.clear();
                self.buttonType = null;
				self.icon("");
                self.usrArt(0);
                self.supportWplSet = null;
			}

			getUrlImg(buttonType: any/*ButtonType sample on server */): string{
				if(buttonType == null) return "";
				return window.location.origin + "/nts.uk.com.js.web/lib/nittsu/ui/style/stylesheets/images/icons/numbered/" + getIcon(buttonType.stampType ? buttonType.stampType.changeClockArt: null, 
								buttonType.stampType ? buttonType.stampType.changeCalArt : null, 
								buttonType.stampType ? buttonType.stampType.setPreClockArt: null, 
								buttonType.stampType ? buttonType.stampType.changeHalfDay: null, 
								buttonType.reservationArt) + ".png";
			}
        }
        
        class ButtonDisSet{
            backGroundColor = ko.observable('');
            buttonNameSet = new ButtonNameSet();
            constructor(){}
            update(param: any){
                let self = this;
                self.backGroundColor(param.backGroundColor);
                self.buttonNameSet.update(param.buttonNameSet);
            }
			clear(){
				let self = this;
                self.backGroundColor("");
                self.buttonNameSet.clear();
			}
        }
        
        class ButtonNameSet{
            buttonName = ko.observable('');
            textColor = ko.observable('');
            constructor(){}
            update(param: any){
                let self = this;
                self.buttonName(param.buttonName);
                self.textColor(param.textColor);
            }
			clear(){
				let self = this;
                self.buttonName("");
                self.textColor("");
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
