module nts.uk.at.view.kaf002_ref.c.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import GridItem = nts.uk.at.view.kaf002_ref.m.viewmodel.GridItem;
    import TimePlaceOutput = nts.uk.at.view.kaf002_ref.m.viewmodel.TimePlaceOutput;
    import STAMPTYPE = nts.uk.at.view.kaf002_ref.m.viewmodel.STAMPTYPE;
    import TabM = nts.uk.at.view.kaf002_ref.m.viewmodel.TabM;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
    import AppStampDto = nts.uk.at.view.kaf002_ref.a.viewmodel.AppStampDto;
    import TimeStampAppDto = nts.uk.at.view.kaf002_ref.a.viewmodel.TimeStampAppDto;
    import DestinationTimeAppDto = nts.uk.at.view.kaf002_ref.a.viewmodel.DestinationTimeAppDto;
    import TimeStampAppOtherDto = nts.uk.at.view.kaf002_ref.a.viewmodel.TimeStampAppOtherDto;
    import TimeZone = nts.uk.at.view.kaf002_ref.a.viewmodel.TimeZone;
    import DestinationTimeZoneAppDto = nts.uk.at.view.kaf002_ref.a.viewmodel.DestinationTimeZoneAppDto;
    import NtsTabPanelModel = nts.uk.ui.NtsTabPanelModel;
    import GoOutTypeDispControl = nts.uk.at.view.kaf002_ref.m.viewmodel.GoOutTypeDispControl;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
    const template = `
	<div id="kaf002-c">
            <div id="contents-area"
                style="background-color: inherit; height: calc(100vh - 138px);">
                <div class="two-panel" style="height: 100%; width: 1260px">
                    <div class="left-panel"
                        style="padding-bottom: 5px; width: calc(1260px - 388px); height: inherit;">
                        <div style="border: 1px solid #CCC; height: inherit; overflow-y: auto; background-color: #fff; padding: 0 10px;overflow-x: hidden;">
                            <div class="table"
                                style="border-bottom: 2px solid #B1B1B1; padding-bottom: 30px; margin-bottom: 30px; width: 100%;">
                                <div class="cell" style="vertical-align: middle;">
                                    <div
                                        data-bind="component: { name: 'kaf000-b-component4',
                                                        params: {
                                                            appType: appType,
                                                            application: application,
                                                            appDispInfoStartupOutput: appDispInfoStartupOutput
                                                        } }"></div>
                                </div>
                                <div class="cell"
                                    style="text-align: right; vertical-align: middle;">
                                    <div
                                        data-bind="component: { name: 'kaf000-b-component8', 
                                                        params: {
                                                            appType: appType,
                                                            appDispInfoStartupOutput: appDispInfoStartupOutput
                                                        } }"></div>
                                </div>
                            </div>
							<div data-bind="component: { name: 'kaf000-b-component2', 
														params: {
															appType: appType,
															appDispInfoStartupOutput: appDispInfoStartupOutput
														} }"></div>
                            <div
                                data-bind="component: { name: 'kaf000-b-component5', 
                                                        params: {
                                                            appType: appType,
                                                            application: application,
                                                            appDispInfoStartupOutput: appDispInfoStartupOutput
                                                        } }"></div>
                            <div 
                                data-bind="component: { name: 'kaf000-b-component6', 
                                                        params: {
                                                            appType: appType,
                                                            application: application,
                                                            appDispInfoStartupOutput: appDispInfoStartupOutput
                                                        } }"
                                style="width: fit-content; display: inline-block; vertical-align: middle; margin-top: -16px"></div>



                            <div style="margin-top: -1px">
                                <div class="table"></div>

                                <!-- C5 -->

                                <div style="display: block; margin-top: -13px">
                                    <!-- C6_1 -->
                                    <div id="labelM"
                                        data-bind="ntsFormLabel: {}, text: $i18n('KAF002_17')"></div>
                                    <!-- C6_2 -->
                                    <div data-bind="if: isM">
                                        <div
                                            data-bind="
                            component: {
                                name: 'kaf002-m',
                                params: {
                                            mode: mode,
                                            selectedTab: selectedTab,
                                            tabs: tabs,
                                            dataSourceOb: dataSourceOb,
                                            tabMs: tabMs,
                                            isVisibleComlumn: isVisibleComlumn,
                                            isPreAtr: isPreAtr,
                                            date: date,
                                            comment1: comment1,
                                            comment2: comment2
                                        }
                            }
                            "
                                            id="componentM"></div>
                                    </div>
                                </div>



                            </div>


                            <div style="margin-top: 13px"
                                data-bind="component: { name: 'kaf000-b-component7', 
                                                        params: {
                                                            appType: appType,
                                                            application: application,
                                                            appDispInfoStartupOutput: appDispInfoStartupOutput
                                                        } }"></div>
                                                        
                                                        <div style="padding-top: 30px;">
                                        
                            </div>  
                        </div>
                    </div>
                    <div class="right-panel" style="width: 388px; padding-bottom: 5px; height: inherit; padding-right: 0px">
                        <div style="border: 1px solid #CCC; height: inherit; background-color: #fff; overflow-y: auto; overflow-x: hidden">
                            <div 
                                data-bind="component: { name: 'kaf000-b-component1', 
                                    params: {
                                        appType: appType,
                                        appDispInfoStartupOutput: appDispInfoStartupOutput	
                                    } }"></div>
                            <div 
                                data-bind="component: { name: 'kaf000-b-component9',
                                    params: {
                                        appType: appType,
                                        application: application,
                                        appDispInfoStartupOutput: $vm.appDispInfoStartupOutput
                                    } }"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
`
    
    @component({
        name: 'kaf002-c',
        template: template
    })
    class Kaf002CViewModel extends ko.ViewModel {
       printContentOfEachAppDto: KnockoutObservable<PrintContentOfEachAppDto>;
       tabs: KnockoutObservableArray<NtsTabPanelModel> = ko.observableArray(null);
       appType: KnockoutObservable<number> = ko.observable(AppType.STAMP_APPLICATION);
       appDispInfoStartupOutput: any;
       approvalReason: KnockoutObservable<string>;
       application: KnockoutObservable<Application>;
       dataSourceOb: KnockoutObservableArray<any>;
       selectedTab: KnockoutObservable<string> = ko.observable('');
       name: KnockoutObservable<string> = ko.observable('hhdhd');
       // display condition
       isM: KnockoutObservable<boolean> = ko.observable(false);
       // select tab M
       selectedCode: KnockoutObservable<number> = ko.observable(0);
       tabMs: KnockoutObservableArray<TabM> = ko.observableArray([new TabM(this.$i18n('KAF002_29'), true, true),
                              new TabM(this.$i18n('KAF002_31'), true, true),
                              new TabM(this.$i18n('KAF002_76'), true, true),
                              new TabM(this.$i18n('KAF002_32'), true, true),
                              new TabM(this.$i18n('KAF002_33'), true, true),
                              new TabM(this.$i18n('KAF002_34'), false, true)]);
       
    //  ※M2.1_2 = ※M
    //  打刻申請起動時の表示情報.打刻申請設定.取消の機能の使用する　＝　使用する(use)
      // set visible for flag column
      isVisibleComlumn: boolean = true;
      isPreAtr: KnockoutObservable<boolean> = ko.observable(true);
      date: KnockoutObservable<string> = ko.observable(null);
      comment1: KnockoutObservable<Comment> = ko.observable(new Comment('', true, ''));
      comment2: KnockoutObservable<Comment> = ko.observable(new Comment('', true, ''));
      // tab visible condition
      isAttendence = false;
      isAttendence2 = false;
      isTemporaryAttendence = false;
      isOutingHourse = false;
      isBreakTime = false;
      isParentHours = false;
      isNurseTime = false;
      data: any;
      mode: KnockoutObservable<number> = ko.observable(1); // 0 ->a, 1->b, 2->b(view)
      reasonList: Array<GoOutTypeDispControl> = [];
      errorList: KnockoutObservableArray<any> = ko.observableArray([])
    
    
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
       fetchData() {
            const self = this;
            self.$blockui('show');
            let appplication = ko.toJS(self.application) as Application;
            let appId = appplication.appID;
            let companyId = self.$user.companyId;
            let appDispInfoStartupDto = ko.toJS(self.appDispInfoStartupOutput);
            let recoderFlag = false;
            let command = {
                    appId,
                    companyId,
                    appDispInfoStartupDto,
                    recoderFlag
            }
            self.$ajax(API.getDetail, command)
                .done(res => {
                    if (!res) return;
                    self.data = res;
                    self.appDispInfoStartupOutput().appDetailScreenInfo.outputMode == 0 ? self.mode(2) : self.mode(1);
                    self.checkExistData();
                    self.isVisibleComlumn = self.data.appStampSetting.useCancelFunction == 1;
                    self.errorList(res.errorListOptional);
                    self.bindActualData();                        
                    self.bindTabM(self.data);
                    self.bindComment(self.data);
                    self.printContentOfEachAppDto().opAppStampOutput = res;
                }).fail(res => {
                    self.showError(res);
                }).always(() => {
                    self.$blockui('hide');
                });
            
            
        }
       
       bindTabM(data: any) {
           const self = this;
           let reflect = data.appStampReflectOptional;
           let attendenceCommon = data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles as boolean;

           if (reflect.temporaryAttendence == 0 && !self.isTemporaryAttendence) {
               self.dataSourceOb()[0].pop();
               self.dataSourceOb()[0].pop();
               self.dataSourceOb()[0].pop();
           }
           if (reflect.attendence == 0 && !self.isAttendence) {
               self.dataSourceOb()[0].shift();
               self.dataSourceOb()[0].shift();
           } else {
               if (!attendenceCommon && !self.isAttendence2) {
                   _.remove(self.dataSourceOb()[0], (e: GridItem) => {
                       return e.typeStamp == STAMPTYPE.ATTENDENCE && e.id == 2;
                   });       
               }
           }
            _.forEach(self.dataSourceOb()[0], i => {
                self.bindDataRequest(i, 1);
            })
            self.isM(true);
            self.tabs.subscribe(value => {
                if (value) {
                    if (data.appStampReflectOptional && self.tabs()) {
                        let reflect = data.appStampReflectOptional;
                        self.tabs()[0].visible(reflect.attendence == 1 || (reflect.temporaryAttendence == 1 && data.useTemporary) || self.isAttendence || self.isTemporaryAttendence );
                        self.tabs()[1].visible(reflect.outingHourse == 1 || self.isOutingHourse);
                        self.tabs()[2].visible(reflect.breakTime == 1 || self.isBreakTime);
                        self.tabs()[3].visible(reflect.parentHours == 1 || self.isParentHours);
                        self.tabs()[4].visible(reflect.nurseTime == 1 || self.isNurseTime);
                        // not use
                        self.tabs()[5].visible(false);
                    
                    } 
                    if (data.appStampOptional) {
                        if (data.appStampOptional.listTimeStampApp) {
                            let dataTab0 = _.filter(data.appStampOptional.listTimeStampApp, (item: any) => item.destinationTimeApp.timeStampAppEnum === 0 || item.destinationTimeApp.timeStampAppEnum === 1);
                            let dataTab1 = _.filter(data.appStampOptional.listTimeStampApp, (item: any) => item.destinationTimeApp.timeStampAppEnum === 2);

                            if (dataTab0.length === 0) {
                                dataTab0 = _.filter(self.data.appStampOptional.listDestinationTimeApp, (item: any) => item.timeStampAppEnum === 0 || item.timeStampAppEnum === 1);
                            }
                            if (dataTab1.length === 0) {
                                dataTab1 = _.filter(self.data.appStampOptional.listDestinationTimeApp, (item: any) => item.timeStampAppEnum === 2);
                            }

                            if (self.tabs()[0].visible()) self.tabs()[0].visible(dataTab0.length > 0); self.tabMs()[0].visible(dataTab0.length > 0);
                            if (self.tabs()[1].visible()) self.tabs()[1].visible(dataTab1.length > 0); self.tabMs()[1].visible(dataTab1.length > 0);
                        }
                        if (data.appStampOptional.listTimeStampAppOther) {
                            let dataTab2 = _.filter(data.appStampOptional.listTimeStampAppOther, (item: any) => item.destinationTimeZoneApp.timeZoneStampClassification === 2);
                            let dataTab3 = _.filter(data.appStampOptional.listTimeStampAppOther, (item: any) => item.destinationTimeZoneApp.timeZoneStampClassification === 0);
                            let dataTab4 = _.filter(data.appStampOptional.listTimeStampAppOther, (item: any) => item.destinationTimeZoneApp.timeZoneStampClassification === 1);

                            if (dataTab2.length === 0) {
                                dataTab2 = _.filter(self.data.appStampOptional.listDestinationTimeZoneApp, (item: any) => item.timeZoneStampClassification === 2);
                            }
                            if (dataTab3.length === 0) {
                                dataTab3 = _.filter(self.data.appStampOptional.listDestinationTimeZoneApp, (item: any) => item.timeZoneStampClassification === 0);
                            }
                            if (dataTab4.length === 0) {
                                dataTab4 = _.filter(self.data.appStampOptional.listDestinationTimeZoneApp, (item: any) => item.timeZoneStampClassification === 1);
                            }

                            if (self.tabs()[2].visible()) self.tabs()[2].visible(dataTab2.length > 0); self.tabMs()[2].visible(dataTab2.length > 0);
                            if (self.tabs()[3].visible()) self.tabs()[3].visible(dataTab3.length > 0); self.tabMs()[3].visible(dataTab3.length > 0);
                            if (self.tabs()[4].visible()) self.tabs()[4].visible(dataTab4.length > 0); self.tabMs()[4].visible(dataTab4.length > 0);
                        }
                        self.tabMs.valueHasMutated();
                    }
                } 
            });
       } 
       
       
       setExistCondition1(stamp: number, frame?: number) {
           const self = this;
           if (stamp == 0) {
               if (frame == 2) {
                   self.isAttendence2 = true;
               }
               self.isAttendence = true;
           } else if (stamp == 2) {
               self.isOutingHourse = true;
           } else if (stamp == 3) {
               
           } else if (stamp == 1) {
               self.isTemporaryAttendence = true;
           }
       }
       setExistCondition2(stamp: number) {
           const self = this;
           if (stamp == 0) {
               self.isParentHours = true;
           } else if (stamp == 1) {
               self.isNurseTime = true;
           } else if (stamp == 2) {
               self.isBreakTime = true;
           } 
       }
    checkExistData() {
        const self = this;
        let appStampDto = self.data.appStampOptional as AppStampDto,
        timeStampAppDto = appStampDto.listTimeStampApp as Array<TimeStampAppDto>,
        destinationTimeAppDto = appStampDto.listDestinationTimeApp as Array<DestinationTimeAppDto>,
        timeStampAppOtherDto = appStampDto.listTimeStampAppOther as Array<TimeStampAppOtherDto>,
        destinationTimeZoneAppDto = appStampDto.listDestinationTimeZoneApp as Array<DestinationTimeZoneAppDto>;
        if (timeStampAppDto) {
            _.forEach(timeStampAppDto, (i: TimeStampAppDto) => {
                self.setExistCondition1(i.destinationTimeApp.timeStampAppEnum, i.destinationTimeApp.engraveFrameNo);
            });
        }
        if (destinationTimeAppDto) {
            _.forEach(destinationTimeAppDto, i => {
                self.setExistCondition1(i.timeStampAppEnum);
            })
        }
        
        if (timeStampAppOtherDto) {
            _.forEach(timeStampAppOtherDto, (i: TimeStampAppOtherDto) => {
                self.setExistCondition2(i.destinationTimeZoneApp.timeZoneStampClassification);
            });
        }
        if (destinationTimeZoneAppDto) {
            _.forEach(destinationTimeZoneAppDto, i => {
                self.setExistCondition2(i.timeZoneStampClassification);
            })
        }
    }   
    bindDataRequest(element: GridItem, type: number) {
       const self = this;
       let appStampDto = self.data.appStampOptional as AppStampDto,
           timeStampAppDto = appStampDto.listTimeStampApp as Array<TimeStampAppDto>,
           destinationTimeAppDto = appStampDto.listDestinationTimeApp as Array<DestinationTimeAppDto>,
           timeStampAppOtherDto = appStampDto.listTimeStampAppOther as Array<TimeStampAppOtherDto>,
           destinationTimeZoneAppDto = appStampDto.listDestinationTimeZoneApp as Array<DestinationTimeZoneAppDto>;
	   let length = _.filter(self.dataSourceOb()[0], (i: GridItem) => i.typeStamp == STAMPTYPE.ATTENDENCE).length;
       if (type ==1) {
           if (timeStampAppDto) {
               let items = _.filter(timeStampAppDto, (x: TimeStampAppDto) => {
                   let destinationTimeAppDto = x.destinationTimeApp as DestinationTimeAppDto;
                   return destinationTimeAppDto.timeStampAppEnum == element.convertTimeStampAppEnum()
                           && destinationTimeAppDto.engraveFrameNo == (element.typeStamp == STAMPTYPE.EXTRAORDINARY ? element.id - 2 : element.id);
               }) as Array<TimeStampAppDto>;
               _.forEach(items, (x: TimeStampAppDto) => {
                     if (x) {
                         let desItem = x.destinationTimeApp as DestinationTimeAppDto;
                         if (x.appStampGoOutAtr) {
                             element.typeReason = String(x.appStampGoOutAtr);
                         }
                         if (desItem.startEndClassification == 0) {
                             element.startTimeRequest(x.timeOfDay);
                         }
                         if (desItem.startEndClassification == 1) {
                             element.endTimeRequest(x.timeOfDay);
                         }             
                     }
               });

           }
           
           if (destinationTimeAppDto) {
               let itemDes = _.findLast(destinationTimeAppDto, (x: any) => {
                   return x.timeStampAppEnum == element.convertTimeStampAppEnum()
                   && x.engraveFrameNo == (element.typeStamp == STAMPTYPE.EXTRAORDINARY ? element.id - 2 : element.id);
               }) as DestinationTimeAppDto;
               
               if (itemDes) {
                   element.flagObservable(true);
               }
           }
           
       } else if (type == 2) {
           if (timeStampAppOtherDto) {
               let items = _.filter(timeStampAppOtherDto, (x: TimeStampAppOtherDto) => {
                   let destinationTimeZoneAppDto = x.destinationTimeZoneApp as DestinationTimeZoneAppDto;
                   return destinationTimeZoneAppDto.timeZoneStampClassification == element.convertTimeZoneStampClassification() 
                           && destinationTimeZoneAppDto.engraveFrameNo == element.id;
               }) as Array<TimeStampAppOtherDto>;
               if (items.length > 0) {
                   let item = items[0] as TimeStampAppOtherDto;
                   element.startTimeRequest(item.timeZone.startTime);
                   element.endTimeRequest(item.timeZone.endTime);
               }
           }
           if (destinationTimeZoneAppDto) {
               let itemDes = _.findLast(destinationTimeZoneAppDto, (x: any) => {
                   return x.timeZoneStampClassification == element.convertTimeZoneStampClassification()
                   && x.engraveFrameNo == element.id;
               })
               if (itemDes) {
                   element.flagObservable(true);
               }
           }
       }
        
    }
    bindActualData() {
        const self = this;
        let opActualContentDisplayLst = ko.toJS(self.appDispInfoStartupOutput).appDispInfoWithDateOutput.opActualContentDisplayLst;
        let opAchievementDetail;
        if (opActualContentDisplayLst) {
            opAchievementDetail = opActualContentDisplayLst[0].opAchievementDetail;
        }
        
        let stampRecord = opAchievementDetail ? opAchievementDetail.stampRecordOutput : null;
        
        let items1 = (function() {
            let list = [];
            let timePlaceList = stampRecord ? stampRecord.workingTime : null;
            for (let i = 1; i < 3; i++) {
                let dataObject = new TimePlaceOutput(i);
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 0, 'stampFrameNo': i, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 0, 'stampFrameNo': i, 'startEndClassification': 1 });
                if (!self.isPreAtr()) {
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
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.ATTENDENCE) as GridItem;
                self.bindDataRequest(gridItem, 1);
                list.push(gridItem); 
            }   
            return list;
        })();
        let items2 = (function() {
            let list = [];
            let extraordinaryTime = stampRecord ? stampRecord.extraordinaryTime : null;
            for (let i = 3; i < 6; i++) {
                let dataObject = new TimePlaceOutput(i);
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 1, 'stampFrameNo': i - 2, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 1, 'stampFrameNo': i - 2, 'startEndClassification': 1 });
                if (!self.isPreAtr()) {
                    _.forEach(extraordinaryTime, item => {
                        if (item.frameNo +2  == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                            dataObject.errorStart = errStartFilter.length > 0;
                            dataObject.errorEnd = errEndFilter.length > 0;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.EXTRAORDINARY);
                self.bindDataRequest(gridItem, 1);
                list.push(gridItem);
    
            }
            
            return list;
        })();
        
        let items3 = ( function() {
            let list = [];
            let outingTime = stampRecord ? stampRecord.outingTime : null;
            for ( let i = 1; i < 11; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 2, 'stampFrameNo': i, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 2, 'stampFrameNo': i, 'startEndClassification': 1 });
                if (!self.isPreAtr()) {
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
                    
                }
                let gridItem = new GridItem( dataObject, STAMPTYPE.GOOUT_RETURNING );
                self.bindDataRequest(gridItem, 1);
                list.push(gridItem);
            }

            return list;
        } )();

        let items4 = ( function() {
            let list = [];
            let breakTime = stampRecord ? stampRecord.breakTime : null;
            for ( let i = 1; i < 11; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                if (!self.isPreAtr()) {
                    _.forEach(breakTime, item => {
                        if (item.frameNo == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.BREAK);
                self.bindDataRequest(gridItem, 2);
                list.push(gridItem);
            }

            return list;
        } )();

        let items5 = ( function() {
            let list = [];
            let parentingTime = stampRecord ? stampRecord.parentingTime : null;
            for ( let i = 1; i < 3; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                if (!self.isPreAtr()) {
                    _.forEach(parentingTime, item => {
                        if (item.frameNo == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.PARENT);
                self.bindDataRequest(gridItem, 2);
                list.push(gridItem);
            }

            return list;
        } )();
        
        let items6 = (function() {
            let list = [];
            let nursingTime = stampRecord ? stampRecord.nursingTime : null;
            for (let i = 1; i < 3; i++) {
                let dataObject = new TimePlaceOutput(i);
                if (!self.isPreAtr()) {
                    _.forEach(nursingTime, item => {
                        if (item.frameNo == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.NURSE);
                self.bindDataRequest(gridItem, 2);
                list.push(gridItem);
            }
            
            return list;
        })();
        
        
        let dataSource = [];
     // case change date
//        if (self.data.appStampReflectOptional) {
//            if (self.data.appStampReflectOptional.temporaryAttendence == 0) {
//                dataSource.push(items1);
//                
//            } else {
//                dataSource.push(items1.concat(items2));
//            }
//        }
        dataSource.push(items1.concat(items2));
        dataSource.push( items3 );
        dataSource.push( items4 );
        dataSource.push( items5 );
        dataSource.push( items6 );
        self.dataSourceOb( dataSource );
    }
    
    bindReload() {
        const self = this;
        let opActualContentDisplayLst = ko.toJS(self.appDispInfoStartupOutput).appDispInfoWithDateOutput.opActualContentDisplayLst;
        let opAchievementDetail;
        if (opActualContentDisplayLst) {
            opAchievementDetail = opActualContentDisplayLst[0].opAchievementDetail;
        }
        
        let stampRecord = opAchievementDetail ? opAchievementDetail.stampRecordOutput : null;
        
        let items1 = (function() {
            let list = [];
            let timePlaceList = stampRecord ? stampRecord.workingTime : null;
            for (let i = 1; i < 3; i++) {
                let dataObject = new TimePlaceOutput(i);
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 0, 'stampFrameNo': i, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 0, 'stampFrameNo': i, 'startEndClassification': 1 });
                if (!self.isPreAtr()) {
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
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.ATTENDENCE) as GridItem;
                self.bindDataRequest(gridItem, 1);
                list.push(gridItem); 
            }   
            return list;
        })();
        let items2 = (function() {
            let list = [];
            let extraordinaryTime = stampRecord ? stampRecord.extraordinaryTime : null;
            for (let i = 3; i < 6; i++) {
                let dataObject = new TimePlaceOutput(i);
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 1, 'stampFrameNo': i - 2, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 1, 'stampFrameNo': i - 2, 'startEndClassification': 1 });
                if (!self.isPreAtr()) {
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
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.EXTRAORDINARY);
                self.bindDataRequest(gridItem, 1);
                list.push(gridItem);
    
            }
            
            return list;
        })();
        
        let items3 = ( function() {
            let list = [];
            let outingTime = stampRecord ? stampRecord.outingTime : null;
            for ( let i = 1; i < 11; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                let errStartFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 2, 'stampFrameNo': i, 'startEndClassification': 0 });
                let errEndFilter = _.filter(self.errorList(), { 'timeStampAppEnum': 2, 'stampFrameNo': i, 'startEndClassification': 1 });
                if (!self.isPreAtr()) {
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
                    
                }
                let gridItem = new GridItem( dataObject, STAMPTYPE.GOOUT_RETURNING );
                self.bindDataRequest(gridItem, 1);
                list.push(gridItem);
            }

            return list;
        } )();

        let items4 = ( function() {
            let list = [];
            let breakTime = stampRecord ? stampRecord.breakTime : null;
            for ( let i = 1; i < 11; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                if (!self.isPreAtr()) {
                    _.forEach(breakTime, item => {
                        if (item.frameNo == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.BREAK);
                self.bindDataRequest(gridItem, 2);
                list.push(gridItem);
            }

            return list;
        } )();

        let items5 = ( function() {
            let list = [];
            let parentingTime = stampRecord ? stampRecord.parentingTime : null;
            for ( let i = 1; i < 3; i++ ) {
                let dataObject = new TimePlaceOutput( i );
                if (!self.isPreAtr()) {
                    _.forEach(parentingTime, item => {
                        if (item.frameNo == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.PARENT);
                self.bindDataRequest(gridItem, 2);
                list.push(gridItem);
            }

            return list;
        } )();
        
        let items6 = (function() {
            let list = [];
            let nursingTime = stampRecord ? stampRecord.nursingTime : null;
            for (let i = 1; i < 3; i++) {
                let dataObject = new TimePlaceOutput(i);
                if (!self.isPreAtr()) {
                    _.forEach(nursingTime, item => {
                        if (item.frameNo == i) {
                            dataObject.opStartTime = item.opStartTime;
                            dataObject.opEndTime = item.opEndTime;
                            dataObject.opWorkLocationCD = item.opWorkLocationCD;
                            dataObject.opGoOutReasonAtr = item.opGoOutReasonAtr;
                        }
                    });
                    
                }
                let gridItem = new GridItem(dataObject, STAMPTYPE.NURSE);
                self.bindDataRequest(gridItem, 2);
                list.push(gridItem);
            }
            
            return list;
        })();
        
        
        let dataSource = [];
     // case change date
//        if (self.data.appStampReflectOptional) {
//            if (self.data.appStampReflectOptional.temporaryAttendence == 0) {
//                dataSource.push(items1);
//                
//            } else {
//                dataSource.push(items1.concat(items2));
//            }
//        }
        dataSource.push(items1.concat(items2));
        dataSource.push( items3 );
        dataSource.push( items4 );
        dataSource.push( items5 );
        dataSource.push( items6 );
        return dataSource;
    }
    
    
    
        created(
                params: { 
					appType: any,
                    application: any,
                    printContentOfEachAppDto: PrintContentOfEachAppDto,
                    approvalReason: any,
                    appDispInfoStartupOutput: any, 
                    eventUpdate: (evt: () => void ) => void,
					eventReload: (evt: () => void) => void
                }
                ) {
            const self = this;
            self.printContentOfEachAppDto = ko.observable(params.printContentOfEachAppDto);
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
            self.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            self.application = params.application;
			self.appType = params.appType;
            self.approvalReason = params.approvalReason;
            
            self.isPreAtr(self.appDispInfoStartupOutput().appDetailScreenInfo.application.prePostAtr == 0);
            self.date(self.appDispInfoStartupOutput().appDetailScreenInfo.application.appDate);
            self.dataSourceOb = ko.observableArray( [] );
            self.fetchData();

            params.eventUpdate(self.update.bind(self));
			params.eventReload(self.reload.bind(self));
            
        }

		reload() {
			
		    const self = this;
			if (self.appType() != AppType.STAMP_APPLICATION && self.application().opStampRequestMode() != StampRequestMode.STAMP_ADDITIONAL) {
				
				return;
			}	
            self.$blockui('show');
            let appplication = ko.toJS(self.application) as Application;
            let appId = appplication.appID;
            let companyId = self.$user.companyId;
            let appDispInfoStartupDto = ko.toJS(self.appDispInfoStartupOutput);
            let recoderFlag = false;
            let command = {
                    appId,
                    companyId,
                    appDispInfoStartupDto,
                    recoderFlag
            }
            self.$ajax(API.getDetail, command)
                .done(res => {
                    if (!res) return;
                    self.data = res;
                    self.isPreAtr(self.appDispInfoStartupOutput().appDetailScreenInfo.application.prePostAtr == 0);
                    self.appDispInfoStartupOutput().appDetailScreenInfo.outputMode == 0 ? self.mode(2) : self.mode(1);
                    self.isAttendence = false;
                    self.isAttendence2 = false;
                    self.isTemporaryAttendence = false;
                    self.isOutingHourse = false;
                    self.isBreakTime = false;
                    self.isParentHours = false;
                    self.isNurseTime = false;
                    self.checkExistData();
                    self.isVisibleComlumn = self.data.appStampSetting.useCancelFunction == 1;
                    self.errorList(res.errorListOptional);
                    let dataSources = self.bindReload()
                    let reflect = self.data.appStampReflectOptional;
                    let attendenceCommon = self.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles as boolean;

                    if (reflect.temporaryAttendence == 0 && !self.isTemporaryAttendence) {
                        dataSources[0].pop();
                        dataSources[0].pop();
                        dataSources[0].pop();
                    }
                    if (reflect.attendence == 0 && !self.isAttendence) {
                        dataSources[0].shift();
                        dataSources[0].shift();
                    } else {
                        if (!attendenceCommon && !self.isAttendence2) {
                            _.remove(dataSources[0], (e: GridItem) => {
                                return e.typeStamp == STAMPTYPE.ATTENDENCE && e.id == 2;
                            });       
                        }
                    }
                    self.dataSourceOb(dataSources);
                    if (self.data.appStampReflectOptional && self.tabs()) {
                        let reflect = self.data.appStampReflectOptional;
                        self.tabs()[0].visible(reflect.attendence == 1 || (reflect.temporaryAttendence == 1 && self.data.useTemporary) || self.isAttendence || self.isTemporaryAttendence );
                        self.tabs()[1].visible(reflect.outingHourse == 1 || self.isOutingHourse);
                        self.tabs()[2].visible(reflect.breakTime == 1 || self.isBreakTime);
                        self.tabs()[3].visible(reflect.parentHours == 1 || self.isParentHours);
                        self.tabs()[4].visible(reflect.nurseTime == 1 || self.isNurseTime);
                        // not use
                        self.tabs()[5].visible(false);
                    
                    } 

                    if (self.data.appStampOptional) {
                        if (self.data.appStampOptional.listTimeStampApp) {
                            let dataTab0 = _.filter(self.data.appStampOptional.listTimeStampApp, (item: any) => item.destinationTimeApp.timeStampAppEnum === 0 || item.destinationTimeApp.timeStampAppEnum === 1);
                            let dataTab1 = _.filter(self.data.appStampOptional.listTimeStampApp, (item: any) => item.destinationTimeApp.timeStampAppEnum === 2);

                            if (dataTab0.length === 0) {
                                dataTab0 = _.filter(self.data.appStampOptional.listDestinationTimeApp, (item: any) => item.timeStampAppEnum === 0 || item.timeStampAppEnum === 1);
                            }
                            if (dataTab1.length === 0) {
                                dataTab1 = _.filter(self.data.appStampOptional.listDestinationTimeApp, (item: any) => item.timeStampAppEnum === 2);
                            }

                            if (self.tabs()[0].visible()) self.tabs()[0].visible(dataTab0.length > 0); self.tabMs()[0].visible(dataTab0.length > 0);
                            if (self.tabs()[1].visible()) self.tabs()[1].visible(dataTab1.length > 0); self.tabMs()[1].visible(dataTab1.length > 0);
                        }
                        if (self.data.appStampOptional.listTimeStampAppOther) {
                            let dataTab2 = _.filter(self.data.appStampOptional.listTimeStampAppOther, (item: any) => item.destinationTimeZoneApp.timeZoneStampClassification === 2);
                            let dataTab3 = _.filter(self.data.appStampOptional.listTimeStampAppOther, (item: any) => item.destinationTimeZoneApp.timeZoneStampClassification === 0);
                            let dataTab4 = _.filter(self.data.appStampOptional.listTimeStampAppOther, (item: any) => item.destinationTimeZoneApp.timeZoneStampClassification === 1);

                            if (dataTab2.length === 0) {
                                dataTab2 = _.filter(self.data.appStampOptional.listDestinationTimeZoneApp, (item: any) => item.timeZoneStampClassification === 2);
                            }
                            if (dataTab3.length === 0) {
                                dataTab3 = _.filter(self.data.appStampOptional.listDestinationTimeZoneApp, (item: any) => item.timeZoneStampClassification === 0);
                            }
                            if (dataTab4.length === 0) {
                                dataTab4 = _.filter(self.data.appStampOptional.listDestinationTimeZoneApp, (item: any) => item.timeZoneStampClassification === 1);
                            }

                            if (self.tabs()[2].visible()) self.tabs()[2].visible(dataTab2.length > 0); self.tabMs()[2].visible(dataTab2.length > 0);
                            if (self.tabs()[3].visible()) self.tabs()[3].visible(dataTab3.length > 0); self.tabMs()[3].visible(dataTab3.length > 0);
                            if (self.tabs()[4].visible()) self.tabs()[4].visible(dataTab4.length > 0); self.tabMs()[4].visible(dataTab4.length > 0);
                        }
                        self.tabMs.valueHasMutated();
                    }
                    self.bindComment(self.data);
                    self.printContentOfEachAppDto().opAppStampOutput = res;
                }).fail(res => {
                    self.showError(res);
                }).always(() => {
                    self.$blockui('hide');
                });
		    
		}

        showError(res: any) {
            const self = this;
            if (res) {
                let  param = {
                         messageId: res.messageId,
                         messageParams: res.parameterIds
                 }
                self.$dialog.error(param).then(() => {
                    if (res.messageId == 'Msg_197') {
                        window.location.reload();
                    }
                });
             }
        }
        update() {
            const self = this;
            if (!self.appDispInfoStartupOutput().appDetailScreenInfo) {
                return;
            }
            let application = ko.toJS(self.application);
            let applicationDto = self.appDispInfoStartupOutput().appDetailScreenInfo.application as any;
            applicationDto.prePostAtr = application.prePostAtr;
            applicationDto.opAppReason = application.opAppReason;
            applicationDto.opAppStandardReasonCD = application.opAppStandardReasonCD;    
            applicationDto.opReversionReason = application.opReversionReason;
            let recoderFlag = false;
            let appStampOutputDto = self.data;
            let appStampDto = {} as AppStampDto;
            appStampDto = self.createAppStamp() as AppStampDto;
            let command = {
                    applicationDto,
                    recoderFlag,
                    appStampDto,
                    appStampOutputDto
            }
            let data = _.clone(self.data);
            data.appStampOptional = appStampDto;
            let companyId = self.$user.companyId
            let agentAtr = false;
            let commandCheck = {
                    companyId,
                    agentAtr,
                    appStampOutputDto: data,
                    applicationDto: applicationDto
            }
            
            return self.$validate('.nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason', '#inputTimeKAF002')
            .then(isValid => {
                if ( isValid ) {
                    return true;
                }
            }).then(result => {
                if(!result) return; 
                self.$blockui("show");
                return self.$ajax(API.checkUpdate, commandCheck);
            }).then(res => {
                if (res == undefined) return;
                if (_.isEmpty(res)) {
                  return self.$ajax(API.update, command);
                 } else {
                      let listConfirm = _.clone(res);
                      return self.handleConfirmMessage(listConfirm, command);
                 }
            }).done(result => {
                if (result != undefined) {
                    return self.$dialog.info({messageId: "Msg_15"}).then(() => {
						return CommonProcess.handleMailResult(result, self);
					});    
                }
            })
            .fail(res => {
                self.showError(res);
            })
            .always(err => {
                self.$blockui("hide");
             });
            
            
        }
        
        public handleConfirmMessage(listMes: any, res: any) {
            let self = this;
            if (!_.isEmpty(listMes)) {
                let item = listMes.shift();
                return self.$dialog.confirm({ messageId: item.msgID, messageParams: item.paramLst }).then((value) => {
                    if (value == 'yes') {
                        if (_.isEmpty(listMes)) {
                             return self.registerData(res);
                        } else {
                             return self.handleConfirmMessage(listMes, res);
                        }

                    }
                });
            }
        }
        registerData(command) {
            let self = this; 
            return self.$ajax(API.update, command);              
        }
        
        public createAppStamp() {
            const self = this;
            let appStamp = {} as AppStampDto;
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
                                let timeStampAppDto = {} as TimeStampAppDto;
                                let destinationTimeApp = {} as DestinationTimeAppDto;
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
                                let timeStampAppDto = {} as TimeStampAppDto;
                                let destinationTimeApp = {} as DestinationTimeAppDto;
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
                                let destinationTimeApp = {} as DestinationTimeAppDto;
                                destinationTimeApp.timeStampAppEnum = el.convertTimeStampAppEnum();
                                destinationTimeApp.startEndClassification = START_CLASSIFICATION;
                                destinationTimeApp.engraveFrameNo = el.id;
                                listDestinationTimeApp.push(destinationTimeApp)
                            }
                            if (el.endTimeActual) {
                                let destinationTimeApp = {} as DestinationTimeAppDto;
                                destinationTimeApp.timeStampAppEnum = el.convertTimeStampAppEnum();
                                destinationTimeApp.startEndClassification = END_CLASSIFICATION;
                                destinationTimeApp.engraveFrameNo = el.id;
                                listDestinationTimeApp.push(destinationTimeApp)
                            }
                        }   
                    })
                    
                } else {
                    _.forEach(items, (el: GridItem) => {
                        if (!ko.toJS(el.flagObservable)) {
                            if ((!_.isNull(ko.toJS(el.startTimeRequest)) && ko.toJS(el.startTimeRequest)) || (!_.isNull(ko.toJS(el.endTimeRequest) && ko.toJS(el.endTimeRequest) !== ''))) {
                                let timeStampAppOtherDto = {} as TimeStampAppOtherDto;
                                let tz = {} as TimeZone;
                                let destinationTimeZoneAppDto = {} as DestinationTimeZoneAppDto;
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
                            let destinationTimeZoneAppDto = {} as DestinationTimeZoneAppDto;
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
	enum StampRequestMode {
		STAMP_ADDITIONAL,
		STAMP_ONLINE_RECORD
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
        
    }
    const API = {
            getDetail: "at/request/application/stamp/detailAppStamp",
            update: "at/request/application/stamp/updateNew",
            checkUpdate: "at/request/application/stamp/checkBeforeUpdate"
            
        }
    const START_CLASSIFICATION = 0;
    const END_CLASSIFICATION = 1;
}