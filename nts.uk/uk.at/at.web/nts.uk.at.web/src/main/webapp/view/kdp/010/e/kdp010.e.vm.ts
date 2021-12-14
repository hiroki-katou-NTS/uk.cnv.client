module nts.uk.at.view.kdp010.e {
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import info = nts.uk.ui.dialog.info;
    import error = nts.uk.ui.dialog.error;
	import ajax = nts.uk.request.ajax;

    export module viewmodel {
		const paths: any = {
	       	getData: "at/record/stamp/timestampinputsetting/portalstampsettings/get",
        	save: "at/record/stamp/timestampinputsetting/portalstampsettings/save"
	    }
        export class ScreenModel {
            suppressStampBtnOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 1, name: getText("KDP010_202") },
                { id: 0, name: getText("KDP010_203") }
            ]);
            useTopMenuLinkOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 1, name: getText("KDP010_271") },
                { id: 0, name: getText("KDP010_272") }
            ]);
			displayStampListOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 1, name: getText("KDP010_278") },
                { id: 0, name: getText("KDP010_279") }
            ]);
			gooutUseAtrOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 1, name: getText("KDP010_309") },
                { id: 0, name: getText("KDP010_310") }
            ]);
            goOutArtOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 0, name: getText("KDP010_112") },
                { id: 1, name: getText("KDP010_113") },
                { id: 2, name: getText("KDP010_114") },
                { id: 3, name: getText("KDP010_115") }
            ]);
            portalStampSettings = new PortalStampSettings();
            constructor(){
                let self = this;
            }
            
            start(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                block.grayout();
                ajax("at", paths.getData).done(function(data: any) {
                    if (data) {
//                        console.log(data);
                        self.portalStampSettings.update(data);
                    }
                    dfd.resolve();
                    $(document).ready(function() {
                        $('#d-serverCorrectionInterval').focus();
                    });
                }).fail(function (res: any) {
                    error({ messageId: res.messageId });
                }).always(function () {
                    block.clear();
                });
                return dfd.promise();
            }
            
            save(){
                let self = this;
                block.grayout();
                ajax("at", paths.save, ko.toJS(self.portalStampSettings)).done(function() {
                    info({ messageId: "Msg_15"});
                }).fail(function (res: any) {
                    error({ messageId: res.messageId });
                }).always(function () {
                    block.clear();
                });
            }
        }
        
        class ButtonNameSet{
            buttonName: KnockoutObservable<string>;
            textColor: KnockoutObservable<string> = ko.observable("#ffffff");
            constructor(buttonPositionNo: number){
                let self = this;
                if(buttonPositionNo == 1){
                    self.buttonName = ko.observable(getText("KDP010_212"));    
                }else if(buttonPositionNo == 2){
                    self.buttonName = ko.observable(getText("KDP010_213"));    
                }else if(buttonPositionNo == 3){
                    self.buttonName = ko.observable(getText("KDP010_214"));    
                }else if(buttonPositionNo == 4){
                    self.buttonName = ko.observable(getText("KDP010_215"));    
                }
            }
            update(param: any){
                let self = this;
                self.buttonName(param.buttonName);
                self.textColor(param.textColor);
            }
        }
        
        class ButtonDisSet{
            backGroundColor: KnockoutObservable<string> = ko.observable("#127D09");
            buttonNameSet: ButtonNameSet;
            constructor(buttonPositionNo: number){
                let self = this;
                self.buttonNameSet = new ButtonNameSet(buttonPositionNo)
            }
            update(param: any){
                let self = this;
                self.backGroundColor(param.backGroundColor);
                self.buttonNameSet.update(param.buttonNameSet);
            }
        }
        
        class ButtonSettings {
            buttonPositionNo: number;
            buttonDisSet: any;
            buttonType: any;
            usrArt: KnockoutObservable<number> = ko.observable(1);
            audioType: number = 0;
            goOutArt: KnockoutObservable<number> = ko.observable(null);
            constructor(buttonPositionNo: number, goOutUseAtr: KnockoutObservable<number>){
                let self = this;
                self.buttonPositionNo = buttonPositionNo;
                self.buttonDisSet = new ButtonDisSet(self.buttonPositionNo);
                if(self.buttonPositionNo == 1){
                    self.buttonType = {
                        stampType: { changeClockArt: 0, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: false, goOutArt: null },
                        reservationArt: 0
                    }
                }else if(self.buttonPositionNo == 2){
                    self.buttonType = {
                        stampType: { changeClockArt: 1, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: false, goOutArt: null },
                        reservationArt: 0
                    }
                }else if(self.buttonPositionNo == 3){
					self.usrArt = goOutUseAtr;
                    self.buttonType = {
                        stampType: { changeClockArt: 4, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: false, goOutArt: 0},
                        reservationArt: 0
                    }
                    self.goOutArt(0);
                    self.goOutArt.subscribe((newValue) => {
                        self.buttonType.stampType.goOutArt = newValue;
                    });
                }else if(self.buttonPositionNo == 4){
					self.usrArt = goOutUseAtr;
                    self.buttonType = {
                        stampType: { changeClockArt: 5, changeCalArt: 0, setPreClockArt: 0, changeHalfDay: false, goOutArt: null },
                        reservationArt: 0
                    }
                }
            }
            update(param: any){
                let self = this;
                if(param){
                    self.buttonDisSet.update(param.buttonDisSet);
                    if(param.buttonPositionNo == 3 && param.buttonType.stampType){
                        self.goOutArt(param.buttonType.stampType.goOutArt);
                    }
                }
            }
        }
        
        class SettingDateTimeClorOfStampScreen {
            textColor: KnockoutObservable<string> = ko.observable("#7F7F7F");
            constructor(){}
            update(data?: any){
                let self = this;
                if(data){
                    self.textColor(data.textColor);
                }
            }
        }
        
        class DisplaySettingsStampScreen {
            serverCorrectionInterval: KnockoutObservable<number> = ko.observable(10);
            resultDisplayTime: KnockoutObservable<number> = ko.observable(3);
            settingDateTimeColor = new SettingDateTimeClorOfStampScreen();
            constructor(){}
            update(data?:any){
                let self = this;
                if(data){
                    self.serverCorrectionInterval(data.serverCorrectionInterval);
                    self.resultDisplayTime(data.resultDisplayTime);
                    self.settingDateTimeColor.update(data.settingDateTimeColor);
                }
            }
        }
        
        class PortalStampSettings {
            displaySettingsStampScreen = new DisplaySettingsStampScreen();
            suppressStampBtn: KnockoutObservable<number> = ko.observable(0);
            useTopMenuLink: KnockoutObservable<number> = ko.observable(0);
			goOutUseAtr: KnockoutObservable<number> = ko.observable(0);
			goOutUseAtrEnable: KnockoutObservable<boolean> = ko.computed(():boolean => {
				return this.goOutUseAtr() == 1;
			});
			displayStampList: KnockoutObservable<number> = ko.observable(0);
            buttonSettings: any;
            constructor(){
                let self = this;
                let buttonSettingsArray = [];
                for(let i = 1; i <= 4; i++){
                    buttonSettingsArray.push(new ButtonSettings(i, self.goOutUseAtr));
                }
                self.buttonSettings = buttonSettingsArray;
            }
            update(data?:any){
                let self = this;
                if(data){
                    self.displaySettingsStampScreen.update(data.displaySettingsStampScreen);
                    self.suppressStampBtn(data.suppressStampBtn);
                    self.useTopMenuLink(data.useTopMenuLink);
					self.goOutUseAtr(data.goOutUseAtr);
					self.displayStampList(data.displayStampList);
                    _.forEach(self.buttonSettings, function(buttonSetting) {
                        let setting = _.find(data.buttonSettings, function(item: any) { return item.buttonPositionNo == buttonSetting.buttonPositionNo; });
                        if(setting){
                            buttonSetting.update(setting);
                        }
                    });
                }
            }
        }  
    }
	__viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.start().done(function() {
            __viewContext.bind(screenModel);
        });
    });
}