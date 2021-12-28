module nts.uk.at.view.kaf002_ref.a.viewmodel {
    
    import GridItem = nts.uk.at.view.kaf002_ref.m.viewmodel.GridItem;
    import TimePlaceOutput = nts.uk.at.view.kaf002_ref.m.viewmodel.TimePlaceOutput;
    import STAMPTYPE = nts.uk.at.view.kaf002_ref.m.viewmodel.STAMPTYPE;
    import TabM = nts.uk.at.view.kaf002_ref.m.viewmodel.TabM;
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
    import alertError = nts.uk.ui.dialog.alertError;
    import GoOutTypeDispControl = nts.uk.at.view.kaf002_ref.m.viewmodel.GoOutTypeDispControl;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    @bean()
    class Kaf002AViewModel extends Kaf000AViewModel {
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel> = ko.observableArray(null);
        isSendMail: KnockoutObservable<boolean> = ko.observable(false);
		appType: KnockoutObservable<number> = ko.observable(AppType.STAMP_APPLICATION);
		isAgentMode : KnockoutObservable<boolean> = ko.observable(false);
        dataSourceOb: KnockoutObservableArray<any> = null;
        application: KnockoutObservable<Application>;
        selectedTab: KnockoutObservable<string> = ko.observable('');
        isM: KnockoutObservable<boolean> = ko.observable(false);
        comment1: KnockoutObservable<Comment> = ko.observable(new Comment('', true, ''));
        comment2: KnockoutObservable<Comment> = ko.observable(new Comment('', true, ''));
        // select tab M
        selectedCode: KnockoutObservable<number> = ko.observable(0);
        tabMs: KnockoutObservableArray<TabM> = ko.observableArray([new TabM(this.$i18n('KAF002_29'), true, true),
                          new TabM(this.$i18n('KAF002_31'), true, true),
                          new TabM(this.$i18n('KAF002_76'), true, true),
                          new TabM(this.$i18n('KAF002_32'), true, true),
                          new TabM(this.$i18n('KAF002_33'), true, true),
                          new TabM(this.$i18n('KAF002_34'), false, true)]);

        isVisibleComlumn: boolean = true;
        isPreAtr: KnockoutObservable<boolean> = ko.observable(false);
        mode: KnockoutObservable<number> = ko.observable(0); // 0 ->a, 1->b, 2->b(view)
        reasonList: Array<GoOutTypeDispControl>;
        date: KnockoutObservable<string> = ko.observable(null);
        errorList: KnockoutObservableArray<any> = ko.observableArray([]);

//    ※M2.1_1
//    打刻申請起動時の表示情報.申請設定（基準日関係なし）.複数回勤務の管理　＝　true
    
        isCondition2: boolean = true;

//    打刻申請起動時の表示情報.臨時勤務利用　＝　true
    
        isCondition3: boolean = true;
    
//  ※1打刻申請起動時の表示情報.打刻申請の反映.出退勤を反映する　＝　する
    
        isCondition4: boolean = true;
//   ※2打刻申請起動時の表示情報.打刻申請の反映.外出時間帯を反映する　＝　する
    
        isCondition5: boolean = true;
    
//    ※3打刻申請起動時の表示情報.打刻申請の反映.休憩時間帯を反映する　＝　する
    
        isCondition6: boolean = true;
//    ※4打刻申請起動時の表示情報.打刻申請の反映.育児時間帯を反映する　＝　する
    
        isCondition7: boolean = true;
//    ※5打刻申請起動時の表示情報.打刻申請の反映.介護時間帯を反映する　＝　する
    
        isCondition8: boolean = true;
//    ※6打刻申請起動時の表示情報.打刻申請の反映.終了を反映する　＝　する
    
        isCondition9: boolean = true;
        data : any;
		isFromOther: boolean = false;

    bindComment(data: any) {
        const self = this;
        _.forEach(self.data.appStampSetting.settingForEachTypeLst, i => {
           if (i.stampAtr == ko.toJS(self.selectedCode)) {
               let commentBot = i.bottomComment;
               self.comment2(new Comment(commentBot.comment, commentBot.bold, commentBot.colorCode));
               let commentTop = i.topComment;
               self.comment1(new Comment(commentTop.comment, commentTop.bold, commentTop.colorCode));
           }
        });
    }    
    created(params: AppInitParam) {
        const self = this;
		if(nts.uk.request.location.current.isFromMenu) {
			sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
		} else {
			if(!_.isNil(__viewContext.transferred.value)) {
				self.isFromOther = true;
				params = __viewContext.transferred.value;
			}	
		}
		
		let empLst: Array<string> = [],
			dateLst: Array<string> = [];
        self.application = ko.observable(new Application(self.appType()));
		self.application().opStampRequestMode(0);
        self.selectedTab.subscribe(value => {
           if (!_.isNull(value)) {
               self.selectedCode(Number(value));
           }
        });
        self.selectedCode.subscribe(value => {
            if (!_.isNull(value) && self.data) {
                self.bindComment(self.data);
            }
        });
		let screenCode: number = null;
		if (!_.isEmpty(params)) {
			if (!nts.uk.util.isNullOrUndefined(params.screenCode)) {
				screenCode = params.screenCode;
			}
			if (!_.isEmpty(params.employeeIds)) {
				empLst = params.employeeIds;
			}
			if (!_.isEmpty(params.baseDate)) {
				let paramDate = moment(params.baseDate).format('YYYY/MM/DD');
				dateLst = [paramDate];
				self.application().appDate(paramDate);
				self.application().opAppStartDate(paramDate);
                self.application().opAppEndDate(paramDate);
			}
			if (params.isAgentMode) {
				self.isAgentMode(params.isAgentMode);
			}
		}
		let paramKAF000 = { 
			empLst, 
			dateLst, 
			appType: self.appType(),
			screenCode
		};
        self.loadData(paramKAF000)
		.then((loadDataFlag: any) => {
            // self.appDispInfoStartupOutput.subscribe(value => {
            //     if (value) { 
            //         self.changeDate();
            //     }
            // });
            
            if (!_.isNull(ko.toJS(self.application().prePostAtr))) {
                self.isPreAtr(self.application().prePostAtr() == 0);                
            }
            self.application().prePostAtr.subscribe(value => {
                if (!_.isNull(value)) {
					self.$errors('clear');
                    self.isPreAtr(value == 0);
                }
            });
            self.application().appDate.subscribe(value => {
                self.date(value);

                let command = {
                    companyId: self.$user.companyId,
                    appStampOutputDto: self.data,
                    date: [moment(value).format('YYYY/MM/DD')],
                    recorderFlag: false,
                }

                self.$blockui('show');
                self.$ajax(API.changeAppDate, command).done((res) => {
                    if (res) {
                        // console.log(res);
                        self.errorList(res.errorListOptional);
                        self.changeDate();
                    }
                }).fail((err) => {
                    if (err) {
                        self.$dialog.error({ messageId: err.messageId, messageParams: err.parameterIds });
                    }
                })
            });

            if(loadDataFlag) {
				self.application().employeeIDLst(empLst);
                let companyId = self.$user.companyId;
                let command = { 
                        appDispInfoStartupDto: ko.toJS(self.appDispInfoStartupOutput),
                        recoderFlag: RECORD_FLAG_STAMP,
                        companyId
                };
            
                return self.$ajax(API.start, command);
            }
        }).done((res: any) => {
            if (res) {
                self.data = res;
                self.isVisibleComlumn = self.data.appStampSetting.useCancelFunction == 1;
                self.bindReasonList(self.data);
                self.bindTabM(self.data);
                self.bindComment(self.data);
				if (!_.isEmpty(params)) {
					if (!_.isEmpty(params.baseDate)) {
						self.changeDate();
					}
				}
                let el = document.getElementById('kaf000-a-component4-singleDate');
                if (el) {
                    el.focus();                                                    
                }
            }
        }).fail(res => {
            self.showError(res);
        }).always(() => {
            self.$blockui('hide');
        });
        // do not have actual data, or date is not selected
        self.initData();
    }
    bindReasonList(data: any) {
        const self = this;
        let reasonList = data.appStampSetting.goOutTypeDispControl;
        reasonList = _.filter(reasonList, (e: GoOutTypeDispControl) => {
            return e.display == 1;
        })
        if (_.isEmpty(reasonList)) {
            reasonList = [{display: 1, goOutType: 0}];
        }
        self.reasonList = reasonList;
        
    }
    
    bindTabM(data: any) {
        const self = this;
        let reflect = data.appStampReflectOptional;
        // visible 勤務時間 2
        
        let attendenceCommon = data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles as boolean;
        
        if (reflect.temporaryAttendence == 0) {
            self.dataSourceOb()[0].pop();
            self.dataSourceOb()[0].pop();
            self.dataSourceOb()[0].pop();
        }
        if (reflect.attendence == 0) {
            self.dataSourceOb()[0].shift();
            self.dataSourceOb()[0].shift();
        } else {
            if (!attendenceCommon) {
                _.remove(self.dataSourceOb()[0], (e: GridItem) => {
                    return e.typeStamp == STAMPTYPE.ATTENDENCE && e.id == 2;
                });       
            }
        }
        self.isM(true);
        self.tabs.subscribe(value => {
           if (value) {
             if (data.appStampReflectOptional && self.tabs()) {
                 let reflect = data.appStampReflectOptional;
//                 打刻申請起動時の表示情報.打刻申請の反映.出退勤を反映する　＝　する || 「打刻申請起動時の表示情報.打刻申請の反映.臨時出退勤を反映する　＝　true　AND　打刻申請起動時の表示情報.臨時勤務利用　＝　true」
                
                 self.tabs()[0].visible(reflect.attendence == 1 || (reflect.temporaryAttendence == 1 && data.useTemporary) );
                 self.tabs()[1].visible(reflect.outingHourse == 1);
                 self.tabs()[2].visible(reflect.breakTime == 1);
                 self.tabs()[3].visible(reflect.parentHours == 1);
                 self.tabs()[4].visible(reflect.nurseTime == 1);
                 // not use
                 self.tabs()[5].visible(false);
             
             } 
           } 
        });
        


    }
    changeDataSource() {
       
    }
    
    public handleConfirmMessage(listMes: any, res: any) {
        let vm = this;
        if (!_.isEmpty(listMes)) {
            let item = listMes.shift();
            return vm.$dialog.confirm({ messageId: item.msgID, messageParams: item.paramLst }).then((value) => {
                if (value == 'yes') {
                    if (_.isEmpty(listMes)) {
                         return vm.registerData(res);
                    } else {
                         return vm.handleConfirmMessage(listMes, res);
                    }

                }
            });
        }
    }
    registerData(command) {
        let self = this; 
        return self.$ajax(API.register, command);       
    }
    
    public createCommandCheckRegister() {
        const self = this;
        let data = _.clone(self.data);
        data.appStampOptional = self.createAppStamp();
        let companyId = self.$user.companyId;
        let agentAtr = false;
        self.application().enteredPerson = self.$user.employeeId;
        self.application().employeeID = self.application().employeeIDLst()[0];
//        self.application().prePostAtr(0);
        let command = {
                companyId,
                agentAtr,
                appStampOutputDto: data,
                applicationDto: ko.toJS(self.application)
                
        };
        self.$blockui("show");
        self.$validate('.nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason')
        .then(isValid => {
            if ( isValid ) {
                return true;
            }
        }).then(result => {
            if(!result) return;
            return self.$ajax(API.checkRegister, command);
             
        }).then(res => {
            if (res == undefined) return;
            let command = {
                    applicationDto: ko.toJS(self.application),
                    appStampDto: data.appStampOptional,
                    appStampOutputDto: self.data,
                    recoderFlag: RECORD_FLAG_STAMP
            };
            if (_.isEmpty(res)) {
                return self.$ajax(API.register, command);
            } else {
                let listConfirm = _.clone(res);
                return self.handleConfirmMessage(listConfirm, command);
            }

        }).then(result => {
            if (result != undefined) {
                return self.$dialog.info( { messageId: "Msg_15" } ).then(() => {
					nts.uk.request.ajax("at", API.reflectApp, result.reflectAppIdLst);
                	return result;
                });                
            }
        }).then((result) => {
			if(result) {
				CommonProcess.handleAfterRegister(result, self.isSendMail(), self, false, self.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
			}
		}).fail(res => {
            self.showError(res);
        })
        .always(err => {
            self.$blockui("hide");
         })
        
    }
    showError(res: any) {
        const self = this;
        if (res) {
            let  param = {
                     messageId: res.messageId,
                     messageParams: res.parameterIds
             }
            self.$dialog.error(param).then(() => {
                if (res.messageId == 'Msg_1757') {
                    self.$jump("com", "/view/ccg/008/a/index.xhtml")
                }
            });
         }
    }
    public register() {
        const self = this;
        self.createCommandCheckRegister();
    }
    
    
    changeDate() {
        const self = this;
        self.bindActualData();
        let dataClone = _.clone(self.data);
        if (dataClone) {

            self.data.appDispInfoStartupOutput = ko.toJS(self.appDispInfoStartupOutput);

            return;
        }
        self.$blockui( "show" );
        let companyId = self.$user.companyId;
        let command = { 
                appDispInfoStartupDto: ko.toJS(self.appDispInfoStartupOutput),
                recoderFlag: RECORD_FLAG_STAMP,
                companyId
        };
        self.$ajax(API.start, command)
            .done((res: any) => {
                if (res) {
                    self.data = res;
                    self.initData();
                    self.isVisibleComlumn = self.data.appStampSetting.useCancelFunction == 1;
                    self.bindReasonList(self.data);
                    self.bindTabM(self.data);
                    self.bindComment(self.data);
                    let el = document.getElementById('kaf000-a-component4-singleDate');
                    if (el) {
                        el.focus();                                                    
                    }
                }
            }).fail(res => {
                self.showError(res);
            }).always(() => {
                self.$blockui('hide');
            });
    }
    bindActualData() {
        const self = this;
        let opActualContentDisplayLst = ko.toJS(self.appDispInfoStartupOutput).appDispInfoWithDateOutput.opActualContentDisplayLst;
        let opAchievementDetail = opActualContentDisplayLst[0].opAchievementDetail;
        if (_.isNull(opAchievementDetail)) {
            // should reload component
            self.initData();
            return;
        }
        let stampRecord = opAchievementDetail.stampRecordOutput;
        
        let items1 = (function() {
            let list = [];
            let timePlaceList = stampRecord.workingTime;
            for (let i = 1; i < 3; i++) {
                let dataObject = new TimePlaceOutput(i);
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 0, 'stampFrameNo': i, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 0, 'stampFrameNo': i, 'startEndClassification': 1 });
                _.forEach(timePlaceList, item => {
                    if (item.frameNo == i) {
                        dataObject.opStartTime = item.opStartTime;
                        dataObject.opEndTime = item.opEndTime;
                        dataObject.opWorkLocationCD = item.opWorkLocationCD;
                        dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        dataObject.errorStart = errStartFilter.length > 0;
                        dataObject.errorEnd = errEndFilter.length > 0;
                    }
                });
                list.push(new GridItem(dataObject, STAMPTYPE.ATTENDENCE)); 
            }
            
            return list;
        })();
        let items2 = (function() {
            let list = [];
            let extraordinaryTime = stampRecord.extraordinaryTime;
            for (let i = 3; i < 6; i++) {
                let dataObject = new TimePlaceOutput(i);
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 1, 'stampFrameNo': i - 2, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 1, 'stampFrameNo': i - 2, 'startEndClassification': 1 });
                _.forEach(extraordinaryTime, item => {
                    if (item.frameNo + 2 == i) {
                        dataObject.opStartTime = item.opStartTime;
                        dataObject.opEndTime = item.opEndTime;
                        dataObject.opWorkLocationCD = item.opWorkLocationCD;
                        dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        dataObject.errorStart = errStartFilter.length > 0;
                        dataObject.errorEnd = errEndFilter.length > 0;
                    }
                });
                list.push(new GridItem(dataObject, STAMPTYPE.EXTRAORDINARY));
    
            }
            
            return list;
        })();
        
        let items3 = ( function() {
            let list = [];
            let outingTime = stampRecord.outingTime;
            for ( let i = 1; i < 11; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 2, 'stampFrameNo': i, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 2, 'stampFrameNo': i, 'startEndClassification': 1 });
                _.forEach(outingTime, item => {
                    if (item.frameNo == i) {
                        dataObject.opStartTime = item.opStartTime;
                        dataObject.opEndTime = item.opEndTime;
                        dataObject.opWorkLocationCD = item.opWorkLocationCD;
                        dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        dataObject.errorStart = errStartFilter.length > 0;
                        dataObject.errorEnd = errEndFilter.length > 0;
                    }
                });
                list.push( new GridItem( dataObject, STAMPTYPE.GOOUT_RETURNING ) );
            }

            return list;
        } )();

        let items4 = ( function() {
            let list = [];
            let breakTime = stampRecord.breakTime;
            for ( let i = 1; i < 11; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                _.forEach(breakTime, item => {
                    if (item.frameNo == i) {
                        dataObject.opStartTime = item.opStartTime;
                        dataObject.opEndTime = item.opEndTime;
                        dataObject.opWorkLocationCD = item.opWorkLocationCD;
                        dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                    }
                });
                list.push( new GridItem( dataObject, STAMPTYPE.BREAK ) );
            }

            return list;
        } )();

        let items5 = ( function() {
            let list = [];
            let parentingTime = stampRecord.parentingTime;
            for ( let i = 1; i < 3; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                _.forEach(parentingTime, item => {
                    if (item.frameNo == i) {
                        dataObject.opStartTime = item.opStartTime;
                        dataObject.opEndTime = item.opEndTime;
                        dataObject.opWorkLocationCD = item.opWorkLocationCD;
                        dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                    }
                });
                list.push( new GridItem( dataObject, STAMPTYPE.PARENT ) );
            }

            return list;
        } )();
        
        let items6 = (function() {
            let list = [];
            let nursingTime = stampRecord.nursingTime;
            for (let i = 1; i < 3; i++) {
                let dataObject = new TimePlaceOutput(i);
                _.forEach(nursingTime, item => {
                    if (item.frameNo == i) {
                        dataObject.opStartTime = item.opStartTime;
                        dataObject.opEndTime = item.opEndTime;
                        dataObject.opWorkLocationCD = item.opWorkLocationCD;
                        dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                    }
                });
                list.push(new GridItem(dataObject, STAMPTYPE.NURSE));
            }
            
            return list;
        })();
        
        
        let dataSource = [];
        
        // array 1 
        let item1_temp = [];
        // case change date
        if (self.data) {
            if (self.data.appStampReflectOptional.attendence) {
                item1_temp = item1_temp.concat(items1);
                let attendenceCommon = self.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles as boolean;
                if (!attendenceCommon) {
                    _.remove(item1_temp, (e: GridItem) => {
                        return e.typeStamp == STAMPTYPE.ATTENDENCE && e.id == 2;
                    });       
                }
            }
            if (self.data.appStampReflectOptional.temporaryAttendence) {
                item1_temp = item1_temp.concat(items2);   
            } 
        }
        
        dataSource.push(item1_temp);
        dataSource.push(items3);
        dataSource.push(items4);
        dataSource.push(items5);
        dataSource.push(items6);
        dataSource.push([]);
        self.dataSourceOb(dataSource);
    }
    initData() {
            const self = this;
            // reload component at changeData without not actual data
            if (_.isNull(self.dataSourceOb)) {
                self.dataSourceOb = ko.observableArray( [] );
                
            } 
    
            let items1 = (function() {
                let list = []; 
                for (let i = 1; i < 3; i++) {
                    let dataObject = new TimePlaceOutput(i);
                    list.push(new GridItem(dataObject, STAMPTYPE.ATTENDENCE)); 
                    
                }
                
                return list;
            })();
            let items2 = (function() {
                let list = [];
                for (let i = 3; i < 6; i++) {
                    let dataObject = new TimePlaceOutput(i);
                    list.push(new GridItem(dataObject, STAMPTYPE.EXTRAORDINARY));
        
                }
                
                return list;
            })();
            
            let items3 = ( function() {
                let list = [];
    
                for ( let i = 1; i < 11; i++ ) {
                    let dataObject = new TimePlaceOutput( i );
                    list.push( new GridItem( dataObject, STAMPTYPE.GOOUT_RETURNING ) );
                }
    
                return list;
            } )();
    
            let items4 = ( function() {
                let list = [];
    
                for ( let i = 1; i < 11; i++ ) {
                    let dataObject = new TimePlaceOutput( i );
                    list.push( new GridItem( dataObject, STAMPTYPE.BREAK ) );
                }
    
                return list;
            } )();
    
            let items5 = ( function() {
                let list = [];
                for ( let i = 1; i < 3; i++ ) {
                    let dataObject = new TimePlaceOutput( i );
                    list.push( new GridItem( dataObject, STAMPTYPE.PARENT ) );
                }
    
                return list;
            } )();
            
            let items6 = (function() {
                let list = [];
                for (let i = 1; i < 3; i++) {
                    let dataObject = new TimePlaceOutput(i);
                    list.push(new GridItem(dataObject, STAMPTYPE.NURSE));
                }
                
                return list;
            })();
            
            let items7 = (function() {
                let list = [];
                for (let i = 1; i < 3; i++) {
                    let dataObject = new TimePlaceOutput(i);
                    list.push(new GridItem(dataObject, STAMPTYPE.NURSE));
                }
                
                return list;
            })();
            let dataSource = [];
         // array 1 
            let item1_temp = [];
            // case change date
            if (self.data) {
                if (self.data.appStampReflectOptional.attendence) {
                    item1_temp = item1_temp.concat(items1);
                    let attendenceCommon = self.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles as boolean;
                    if (!attendenceCommon) {
                        _.remove(item1_temp, (e: GridItem) => {
                            return e.typeStamp == STAMPTYPE.ATTENDENCE && e.id == 2;
                        });       
                    }
                }
                if (self.data.appStampReflectOptional.temporaryAttendence) {
                    item1_temp = item1_temp.concat(items2);   
                } 
                dataSource.push(item1_temp);
            } else {
                dataSource.push( items1.concat(items2) );       
            }
            dataSource.push( items3 );
            dataSource.push( items4 );
            dataSource.push( items5 );
            dataSource.push( items6 );
            dataSource.push([]);
            self.dataSourceOb( dataSource );
        }
    
    
        public createAppStamp() {
            const self = this;
            let appStamp = new AppStampDto();
            let listTimeStampApp: Array<TimeStampAppDto> = [],
                listDestinationTimeApp: Array<DestinationTimeAppDto> = [],
                listTimeStampAppOther: Array<TimeStampAppOtherDto> = [],
                listDestinationTimeZoneApp: Array<DestinationTimeZoneAppDto> = [];
			let length = _.filter(self.dataSourceOb()[0], (i: GridItem) => i.typeStamp == STAMPTYPE.ATTENDENCE).length;
            _.forEach(self.dataSourceOb(), (items: GridItem, index) => {
//                出勤／退勤 , 外出
                if (index == 0 || index == 1) {                    
                    _.forEach(items, (el: GridItem) => {                       
                        if (!ko.toJS(el.flagObservable)) {
                            if (!_.isNull(ko.toJS(el.startTimeRequest)) && ko.toJS(el.startTimeRequest) !== '') {
                                let timeStampAppDto = new TimeStampAppDto();
                                let destinationTimeApp = new DestinationTimeAppDto();
                                destinationTimeApp.timeStampAppEnum = el.convertTimeStampAppEnum();
                                destinationTimeApp.startEndClassification = START_CLASSIFICATION;
                                destinationTimeApp.engraveFrameNo = (el.typeStamp == STAMPTYPE.EXTRAORDINARY ? el.id - length : el.id);
                                timeStampAppDto.destinationTimeApp = destinationTimeApp;
                                timeStampAppDto.timeOfDay = ko.toJS(el.startTimeRequest);
                                timeStampAppDto.workLocationCd = null;
                                if (!_.isNull(el.typeReason)) {
                                    timeStampAppDto.appStampGoOutAtr = Number(el.typeReason);                                    
                                }
                                listTimeStampApp.push(timeStampAppDto);
                                
                            }
                            
                            if (!_.isNull(ko.toJS(el.endTimeRequest)) && ko.toJS(el.endTimeRequest) !== '') {
                                let timeStampAppDto = new TimeStampAppDto();
                                let destinationTimeApp = new DestinationTimeAppDto();
                                destinationTimeApp.timeStampAppEnum = el.convertTimeStampAppEnum();
                                destinationTimeApp.startEndClassification = END_CLASSIFICATION;
                                destinationTimeApp.engraveFrameNo = (el.typeStamp == STAMPTYPE.EXTRAORDINARY ? el.id - length : el.id);
                                timeStampAppDto.destinationTimeApp = destinationTimeApp;
                                timeStampAppDto.timeOfDay = ko.toJS(el.endTimeRequest);
                                timeStampAppDto.workLocationCd = null;
                                if (!_.isNull(el.typeReason)) {
                                    timeStampAppDto.appStampGoOutAtr = Number(el.typeReason);                                    
                                }
                                listTimeStampApp.push(timeStampAppDto);
                            }
                        } else {
                            if (el.startTimeActual) {
                                let destinationTimeApp = new DestinationTimeAppDto();
                                destinationTimeApp.timeStampAppEnum = el.convertTimeStampAppEnum();
                                destinationTimeApp.startEndClassification = START_CLASSIFICATION;
                                destinationTimeApp.engraveFrameNo = (el.typeStamp == STAMPTYPE.EXTRAORDINARY ? el.id - length : el.id);
                                listDestinationTimeApp.push(destinationTimeApp)
                            }
                            if (el.endTimeActual) {
                                let destinationTimeApp = new DestinationTimeAppDto();
                                destinationTimeApp.timeStampAppEnum = el.convertTimeStampAppEnum();
                                destinationTimeApp.startEndClassification = END_CLASSIFICATION;
                                destinationTimeApp.engraveFrameNo  = (el.typeStamp == STAMPTYPE.EXTRAORDINARY ? el.id - length : el.id);
                                listDestinationTimeApp.push(destinationTimeApp)
                            }
                        }   
                    })
                    
                } else {
                    _.forEach(items, (el: GridItem) => {
                        if (!ko.toJS(el.flagObservable)) {
                            if ((!_.isNull(ko.toJS(el.startTimeRequest)) && ko.toJS(el.startTimeRequest)) || (!_.isNull(ko.toJS(el.endTimeRequest) && ko.toJS(el.endTimeRequest) !== ''))) {
                                let timeStampAppOtherDto = new TimeStampAppOtherDto();
                                let tz = new TimeZone();
                                let destinationTimeZoneAppDto = new DestinationTimeZoneAppDto();
                                destinationTimeZoneAppDto.timeZoneStampClassification = el.convertTimeZoneStampClassification();
                                destinationTimeZoneAppDto.engraveFrameNo = el.id;
                                timeStampAppOtherDto.destinationTimeZoneApp = destinationTimeZoneAppDto;
                                timeStampAppOtherDto.timeZone = tz;
                                if (ko.toJS(el.startTimeRequest) !== '' && !_.isNull(ko.toJS(el.startTimeRequest))) {
                                    tz.startTime = ko.toJS(el.startTimeRequest);
                                    
                                }
                                if (ko.toJS(el.endTimeRequest) !== '' && !_.isNull(ko.toJS(el.endTimeRequest))) {
                                    tz.endTime = ko.toJS(el.endTimeRequest);                             
                                }
                                listTimeStampAppOther.push(timeStampAppOtherDto);                               
                            }
                        } else {
                            let destinationTimeZoneAppDto = new DestinationTimeZoneAppDto();
                            destinationTimeZoneAppDto.timeZoneStampClassification = el.convertTimeZoneStampClassification();
                            destinationTimeZoneAppDto.engraveFrameNo = el.id;
                            listDestinationTimeZoneApp.push(destinationTimeZoneAppDto);
                        }
                    });
                }
            });
            appStamp.listTimeStampApp = listTimeStampApp;
            appStamp.listDestinationTimeApp = listDestinationTimeApp;
            appStamp.listTimeStampAppOther = listTimeStampAppOther;
            appStamp.listDestinationTimeZoneApp = listDestinationTimeZoneApp;
            return appStamp;
        }
    
    
    
    }
    
    
    
    export class AppStampDto {
        public listTimeStampApp: Array<TimeStampAppDto>;
        public listDestinationTimeApp: Array<DestinationTimeAppDto>;
        public listTimeStampAppOther: Array<TimeStampAppOtherDto>;
        public listDestinationTimeZoneApp: Array<DestinationTimeZoneAppDto>;
        
        public AppStampDto() {
            
        }
    
    }
    export class TimeStampAppDto {
        public destinationTimeApp: DestinationTimeAppDto;
        public timeOfDay: number;
        public workLocationCd?: string;
        public appStampGoOutAtr?: number;
        public TimeStampAppDto(destinationTimeApp: DestinationTimeAppDto, timeOfDay: number, workLocationCd?: string, appStampGoOutAtr?: number) {
            this.destinationTimeApp = destinationTimeApp;
            this.timeOfDay = timeOfDay;
            this.workLocationCd = workLocationCd;
            this.appStampGoOutAtr = appStampGoOutAtr;
        }        
    }
    export class DestinationTimeAppDto {
        
        public timeStampAppEnum: number;
        public engraveFrameNo: number;
        public startEndClassification: number;
        public supportWork?: number;
        
        public DestinationTimeAppDto(timeStampAppEnum: number, engraveFrameNo: number, startEndClassification: number, supportWork?: number) {
            this.timeStampAppEnum = timeStampAppEnum;
            this.engraveFrameNo = engraveFrameNo;
            this.startEndClassification = startEndClassification;
            this.supportWork = supportWork;
        }
    }
    class Comment{
        public content: string;
        public isBold: boolean;
        public color: string;
        constructor( content: string, isBold: boolean, color: string) {
            this.content = content;
            this.isBold = isBold;
            this.color = color;
        }
        toHtml() {
            const self = this;
            return '<div style= {}>' + self.content + '</div>'
        }
        
    }
    export class TimeStampAppOtherDto {
        public destinationTimeZoneApp: DestinationTimeZoneAppDto;
        public timeZone: TimeZone;
    }
    export class TimeZone {
        public startTime: number;
        public endTime: number;
    }
    export class DestinationTimeZoneAppDto {
        public timeZoneStampClassification: number;
        public engraveFrameNo: number;
    }
    const START_CLASSIFICATION = 0;
    const END_CLASSIFICATION = 1;
    const API = {
            start: "at/request/application/stamp/startStampApp",
            checkRegister: "at/request/application/stamp/checkBeforeRegister",
            register: "at/request/application/stamp/register",
            reflectApp: "at/request/application/reflect-app", 
            changeAppDate: "at/request/application/stamp/changeAppDateMobile"
        }
    const RECORD_FLAG_STAMP = false;
    
}