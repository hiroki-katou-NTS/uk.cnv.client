module nts.uk.at.view.kdl045.a {

    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
    import shareModelData = nts.uk.at.view.kdl045.share.model;
    import commonKmk003 = nts.uk.at.view.kmk003.a.service.model.common;

    export module viewmodel {
        export class ScreenModel {
            tabs: KnockoutObservableArray<any>;
            selectedTab: KnockoutObservable<string>;

            timeRange: KnockoutObservable<any>;

            dataSourceTime: KnockoutObservableArray<any>;

            timeRange1Value: KnockoutObservable<any>;
            timeRange2Value: KnockoutObservable<any>;
            isShowTimeRange2: boolean = true;

            //A4_4,A4_5,A4_6,A4_7
            workType: KnockoutObservable<string>;
            workTypeName: KnockoutObservable<string>;
            workTime: KnockoutObservable<string>;
            workTimeName: KnockoutObservable<string>;


            //A1_5
            basedate: KnockoutObservable<string>;

            employee: KnockoutObservable<shareModelData.ParamKsu003>;
            timeA10_1: KnockoutObservable<string> = ko.observable("");
            timeA10_2: KnockoutObservable<string> = ko.observable("");

            //A5_2,A5_3
            directAtr: KnockoutObservable<boolean>;
            bounceAtr: KnockoutObservable<boolean>;

            //A8_10
            showLineA8_10: boolean = false;

            //A8_13,A8_15
            childcareNo1: string;
            childcareNo2: string;
            showChildCare: boolean = false;
            //A8_18,A8_20
            nursingNo1: string;
            nursingNo2: string;
            showNursing: boolean = false;

            //A8_6_1,A8_6_2,A8_6_3,A8_6_4,A8_6_5,A8_6_6
            atWork1: string;
            atWork2: string;
            offWork1: string;
            offWork2: string;
            privateTime: string;
            unionTime: string;

            listPrivateTime :any;
            listUnionTime :any;

            //A9
            atWork1A9: any;
            atWork2A9: any;
            offWork1A9: any;
            offWork2A9: any;
            privateTimeA9: any;
            unionTimeA9: any;

            atWork1showA9: KnockoutObservable<boolean> = ko.observable(true);
            atWork2showA9: KnockoutObservable<boolean> = ko.observable(true);
            offWork1showA9: KnockoutObservable<boolean> = ko.observable(true);
            offWork2showA9: KnockoutObservable<boolean> = ko.observable(true);
            privateTimeshowA9: KnockoutObservable<boolean> = ko.observable(true);
            unionTimeshowA9: KnockoutObservable<boolean> = ko.observable(true);

            atWork1showA9_6: boolean = false;
            atWork2showA9_6: boolean = false;
            offWork1showA9_6: boolean = false;
            offWork2showA9_6: boolean = false;
            privateTimeshowA9_6: boolean = false;
            unionTimeshowA9_6: boolean = false;

            //A87
            atWork1A87: KnockoutObservable<string> = ko.observable("");
            atWork2A87: KnockoutObservable<string> = ko.observable("");
            offWork1A87: KnockoutObservable<string> = ko.observable("");
            offWork2A87: KnockoutObservable<string> = ko.observable("");
            privateTimeA87: KnockoutObservable<string> = ko.observable("");
            unionTimeA87: KnockoutObservable<string> = ko.observable("");

            //A5_14,A5_16
            dayShiftTime: KnockoutObservable<string> = ko.observable("");
            nightShiftTime: KnockoutObservable<string> = ko.observable("");

            //A1_7
            assignmentMethodName: KnockoutObservable<string> = ko.observable("");

            //A1_10
            listDetail: KnockoutObservableArray<string> = ko.observableArray([]);

            //修正可能 : ０：参照 or １：修正
            canModified: boolean;
            // check exist workType (workTime);
            isExistWorkType: KnockoutObservable<boolean> = ko.observable(true);
            isExistWorkTime: boolean = true;

            //A1_8 have 1 value
            textDisableA1_8: KnockoutObservable<string> = ko.observable("");

            //enable          
            isEnableA4_2: KnockoutObservable<boolean> = ko.observable(true);
            isEnableA5_2: KnockoutObservable<boolean> = ko.observable(true);
            isEnableA5_3: KnockoutObservable<boolean> = ko.observable(true);
            isEnableA5_5: KnockoutObservable<boolean> = ko.observable(true);
            isEnableA5_9: KnockoutObservable<boolean> = ko.observable(true);

            //disable
            isDisableA1_7: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA1_8: KnockoutObservable<boolean> = ko.observable(false);
            isTextDisableA1_8: KnockoutObservable<boolean> = ko.observable(false);
            isDisableA4_3: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA4_6: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_21: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_23: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_26: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_27: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA11_3: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA13_1: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA13_2: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA13_3: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_1920: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA8_6_5_1 : boolean = true;
            isDisableA8_6_6_1 : boolean = true;
            
            //disable A5_19 ~ A5_25
            fixBreakTime : KnockoutObservable<boolean> = ko.observable(true);
            //A11_1,A11_2,A11_3
            columnsA11: KnockoutObservableArray<NtsGridListColumn>;
            currentNo: KnockoutObservable<any>;
            listDataA11: KnockoutObservable<any>;
            
            isEnableAllControl : KnockoutObservable<boolean> = ko.observable(true);

            //勤務タイプを含む場合 - 
            includingWorkType : KnockoutObservable<boolean> = ko.observable(true);
            
            informationStartup: any;
            moreInformation: any;
            constructor() {
                let self = this;

                self.employee = ko.observable(getShared('kdl045Data'));
                self.canModified = self.employee().canModified == 1 ? true : false;
                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: getText('KDL045_6'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: getText('KDL045_7'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) }
                ]);
                self.selectedTab = ko.observable('tab-1');

                self.dataSourceTime = ko.observableArray([]);
                let breakTimeNo = self.employee().employeeInfo.employeeWorkScheduleDto.listBreakTimeZoneDto[0].breakTimeSheets;
                for (let i = 0; i < breakTimeNo.length; i++) {
                    let tempBreakTime = breakTimeNo[i];
                    self.dataSourceTime().push({ range1: ko.observable({ startTime: tempBreakTime.startTime, endTime: tempBreakTime.endTime, breakFrameNo: tempBreakTime.breakFrameNo,
                                                                         name : self.showTimeByPeriod(tempBreakTime.startTime,tempBreakTime.endTime)
                    }) });
                }
                self.fixBreakTime(self.employee().employeeInfo.fixedWorkInforDto.fixBreakTime ==1 ? true:false);
                self.includingWorkType(_.includes(self.employee().scheCorrection, self.employee().employeeInfo.fixedWorkInforDto.workType));
                //listData
                self.timeRange = ko.observable({
                    maxRow: 10,
                    minRow: 0,
                    maxRowDisplay: 10,
                    isShowButton: self.isDisableA5_1920,
                    dataSource: self.dataSourceTime,
                    isMultipleSelect: true,
                    columns: self.settingColumns(),
                    tabindex :9,
                    isEnableAllControl : self.isEnableAllControl
                });

                self.timeRange1Value = ko.observable({
                    startTime: self.employee().employeeInfo.employeeWorkScheduleDto.startTime1,
                    endTime: self.employee().employeeInfo.employeeWorkScheduleDto.endTime1
                });
                self.timeRange2Value = ko.observable({ 
                    startTime: self.employee().employeeInfo.employeeWorkScheduleDto.startTime2, 
                    endTime: self.employee().employeeInfo.employeeWorkScheduleDto.endTime2 });
                self.isShowTimeRange2 = (self.employee().targetInfor == 1 ? true : false);
                //A10_1,A10_2
                self.timeA10_1(self.showTimeByPeriod(self.employee().employeeInfo.employeeWorkScheduleDto.startTime1, self.employee().employeeInfo.employeeWorkScheduleDto.endTime1));
                self.timeA10_2(self.showTimeByPeriod(self.employee().employeeInfo.employeeWorkScheduleDto.startTime2, self.employee().employeeInfo.employeeWorkScheduleDto.endTime2));
                
                
                //A4_4,A4_5,A4_6,A4_7
                self.workType = ko.observable(self.employee().employeeInfo.employeeWorkScheduleDto.workTypeCode);
                self.workTypeName = ko.observable(self.employee().employeeInfo.fixedWorkInforDto.workTypeName);
                self.workTime = ko.observable(self.employee().employeeInfo.employeeWorkScheduleDto.workTimeCode);
                self.workTimeName = ko.observable(self.employee().employeeInfo.fixedWorkInforDto.workTimeName);

                //A1_5
                let shortW = moment(self.employee().employeeInfo.employeeWorkInfoDto.date).format("dd");
                if (shortW == "土") {
                    shortW = "<span style='color:#0000ff;'>" + getText('KDL045_64') + shortW + getText('KDL045_65') + "</span>";
                } else if (shortW == "日") {
                    shortW = "<span style='color:#ff0000;'>" + getText('KDL045_64') + shortW + getText('KDL045_65') + "</span>";
                } else {
                    shortW = getText('KDL045_64') + shortW + getText('KDL045_65');
                }
                self.basedate = self.employee().employeeInfo.employeeWorkInfoDto.date + shortW;





                //A5_2,A5_3
                self.directAtr = ko.observable(self.employee().employeeInfo.employeeWorkInfoDto.directAtr == 1 ? true : false);
                self.bounceAtr = ko.observable(self.employee().employeeInfo.employeeWorkInfoDto.bounceAtr == 1 ? true : false);

                //A8_13,A8_15,A8_18,A8_20
                let shortTime = self.employee().employeeInfo.employeeWorkInfoDto.shortTime;
                for (let i = 0; i < shortTime.length; i++) {
                    self.showLineA8_10 = true;
                    if (shortTime[i].shortTimeNo == 1 && shortTime[i].shortTimeAtr == 0) { // shortTimeAtr : (0:育児,1:介護)
                        self.childcareNo1 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showChildCare = true;
                        continue;
                    }
                    if (shortTime[i].shortTimeNo == 2 && shortTime[i].shortTimeAtr == 0) {
                        self.childcareNo2 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showChildCare = true;
                        continue;
                    }
                    if (shortTime[i].shortTimeNo == 1 && shortTime[i].shortTimeAtr == 1) {
                        self.nursingNo1 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showNursing = true;
                        continue;
                    }
                    if (shortTime[i].shortTimeNo == 2 && shortTime[i].shortTimeAtr == 1) {
                        self.nursingNo2 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showNursing = true;
                        continue;
                    }
                }

                //A8_6_1,A8_6_2,A8_6_3,A8_6_4,A8_6_5,A8_6_6
                //tương ứng vs A8_6_1~A8_6_4  thì 時間帯リスト chỉ có 1 phần tử
                //tương ứng vs A8_6_5~A8_6_6  thì 時間帯リスト có nhiều phần tử
                let listTimeVacationAndType = self.employee().employeeInfo.employeeWorkInfoDto.listTimeVacationAndType;
                for (let i = 0; i < listTimeVacationAndType.length; i++) {
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK) {
                        self.atWork1 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[0].endTime.time);
                        self.atWork1A9 = listTimeVacationAndType[i].timeVacation.usageTime;
                        for (let j = 0; j < self.atWork1A9.length; j++) {
                            if (self.atWork1A9[j].specialHolidayDisplay != '') {
                                self.atWork1showA9_6 = false;
                                break;
                            }
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK2) {
                        self.atWork2 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[0].endTime.time);
                        self.atWork2A9 = listTimeVacationAndType[i].timeVacation.usageTime;
                        for (let j = 0; j < self.atWork2A9.length; j++) {
                            if (self.atWork2A9[j].specialHolidayDisplay != '') {
                                self.atWork2showA9_6 = false;
                                break;
                            }
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK) {
                        self.offWork1 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[0].endTime.time);
                        self.offWork1A9 = listTimeVacationAndType[i].timeVacation.usageTime;
                        for (let j = 0; j < self.offWork1A9.length; j++) {
                            if (self.offWork1A9[j].specialHolidayDisplay != '') {
                                self.offWork1showA9_6 = true;
                                break;
                            }
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK2) {
                        self.offWork2 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[0].endTime.time);
                        self.offWork2A9 = listTimeVacationAndType[i].timeVacation.usageTime;
                        for (let j = 0; j < self.offWork2A9.length; j++) {
                            if (self.offWork2A9[j].specialHolidayDisplay != '') {
                                self.offWork2showA9_6 = true;
                                break;
                            }
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.PRIVATE) {
                        let listPrivateTime :any = [];
                        for(let k = 0;k< listTimeVacationAndType[i].timeVacation.timeZone.length;k++){
                            let tempData = getText('KDL045_49')+ self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[k].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[k].endTime.time)
                            listPrivateTime.push(tempData);
                        }
                        self.listPrivateTime = listPrivateTime;
                        if(self.listPrivateTime.length <2){
                            self.isDisableA8_6_5_1 = false;    
                        }
                        self.privateTime = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[0].endTime.time);
                        self.privateTimeA9 = listTimeVacationAndType[i].timeVacation.usageTime;
                        for (let j = 0; j < self.privateTimeA9.length; j++) {
                            if (self.privateTimeA9[j].specialHolidayDisplay != '') {
                                self.privateTimeshowA9_6 = true;
                                break;
                            }
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.UNION) {
                        let listUnionTime :any = [];
                        for(let k = 0;k< listTimeVacationAndType[i].timeVacation.timeZone.length;k++){
                            let tempData = getText('KDL045_49')+ self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[k].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[k].endTime.time)
                            listUnionTime.push(tempData);
                        }
                        self.listUnionTime = listUnionTime;
                        if(self.listUnionTime.length <2){
                            self.isDisableA8_6_6_1 = false;    
                        }
                        self.unionTime = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].startTime.time, listTimeVacationAndType[i].timeVacation.timeZone[0].endTime.time);
                        self.unionTimeA9 = listTimeVacationAndType[i].timeVacation.usageTime;
                        for (let j = 0; j < self.unionTimeA9.length; j++) {
                            if (self.unionTimeA9[j].specialHolidayDisplay != '') {
                                self.unionTimeshowA9_6 = true;
                                break;
                            }
                        }
                        continue;
                    }
                }
                //A11_1 
                this.columnsA11 = ko.observableArray([
                    { headerText: 'NO', key: 'breakFrameNo', width: 100, hidden: true },
                    { headerText: getText('KDL045_30'), key: 'name', width: 150 }
                ]);
                this.currentNo = ko.observable();
                self.listDataA11 = ko.observableArray([]);;
                self.setListData();

                self.informationStartup = null;
                //////////////////////////////////////////////////////////////// show/hide control
                if (_.isNil(self.workType()) || self.workType() == "") {
                        self.isExistWorkType(false);
                        self.isDisableA4_3(false);
                    }
                    if (_.isNil(self.workTime()) || self.workTime() == "" || self.isExistWorkType() == false) {
                        self.isExistWorkTime = false;
                        self.enableDisableByWorkTime(false);
                    }
                if (self.canModified) { //修正 (mode update)

                } else { // (mode read only)
                    self.isEnableA5_2(false);
                    self.isEnableA5_3(false);
                    self.isEnableA4_2(false);
                    
                    
                }

                self.workTime.subscribe(workTime => {
                    if (_.isNil(self.workTime()) || self.workTime() == "" || self.isExistWorkType() == false) {
                        self.enableDisableByWorkTime(false);
                    } else {
                        self.enableDisableByWorkTime(true);
                    }

                });
                self.includingWorkType.subscribe(value =>{
                    self.displayByIncludingWorkType(value);
                });
            }
            
            setListData(): void{
                let self = this;
                self.listDataA11([]);
                let temp:any = [];
                for (let i = 0; i < self.dataSourceTime().length; i++) {
                    temp.push({name : self.dataSourceTime()[i].range1().name,breakFrameNo : self.dataSourceTime()[i].range1().breakFrameNo } )
                }
                self.listDataA11(temp);
            }




            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                self.getInformationStartup();
                $.when(self.getInformationStartup()).done(function() {
                    self.displayInformationStartup();
                    self.workTime.valueHasMutated();
                    self.displayByIncludingWorkType(self.includingWorkType());
                    dfd.resolve();
                });

                return dfd.promise();
            }

            enableDisableByWorkTime(isExistWorkTime: boolean): void {
                let self = this;
                if (!isExistWorkTime) {
                    //disable
                    self.isDisableA4_6(false);
                    self.isDisableA5_23(false);
                    self.isDisableA11_3(false);

                    //enable
                    self.isEnableA5_2(false);
                    self.isEnableA5_3(false);
                    self.isEnableA5_5(false); //enable A5_6,A5_7
                    self.isEnableA5_9(false); //enable A5_10,A5_11
                    //enable A5_19,A5_20
                    //A5_21
                    self.dataSourceTime([]);
                    self.listDataA11([]);
                    self.isEnableAllControl(false);
                    //A5_14,A5_16
                    self.dayShiftTime(self.showTimeByMinuteHaveValue0(0));
                    self.nightShiftTime(self.showTimeByMinuteHaveValue0(0));
                    
                    //A5_6,A5_7,A5_10,A5_11 = 0
                    self.timeRange1Value(0,0);
                    self.timeRange2Value(0,0);

                    self.timeA10_1(self.showTimeByPeriod(0, 0));
                    self.timeA10_2(self.showTimeByPeriod(0, 0));
                } else {
                    //disable
                    self.isDisableA4_6(true);
                    self.isDisableA5_23(true);
                    self.isDisableA11_3(true);

                    //enable
                    self.isEnableA5_2(true);
                    self.isEnableA5_3(true);
                    self.isEnableA5_5(true); //enable A5_6,A5_7
                    self.isEnableA5_9(true); //enable A5_10,A5_11
                    //enable A5_19,A5_20
                    self.isEnableAllControl(true);
                    //ko.cleanNode(document.getElementById('kdl045'));
                    //ko.applyBindings(self, document.getElementById('kdl045'));
                }
                setTimeout(function() {
                    nts.uk.ui.errors.clearAll();
                }, 100);


            }

            public openDialogKDL003(): void {
                let self = this;
                // set update data input open dialog kdl003
                nts.uk.ui.windows.setShared('parentCodes', {
                    workTypeCodes: [],
                    selectedWorkTypeCode: self.employee().employeeInfo.employeeWorkScheduleDto.workTypeCode,
                    workTimeCodes: [],
                    selectedWorkTimeCode: self.employee().employeeInfo.employeeWorkScheduleDto.workTimeCode
                }, true);

                nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed(function(): any {
                    //view all code of selected item 
                    let childData = nts.uk.ui.windows.getShared('childData');
                    if (childData) {
                        self.isExistWorkType(true);
                        self.employee().employeeInfo.employeeWorkScheduleDto.workTypeCode = childData.selectedWorkTypeCode;
                        self.employee().employeeInfo.employeeWorkScheduleDto.workTimeCode = childData.selectedWorkTimeCode;
                        self.employee().employeeInfo.fixedWorkInforDto.workTypeName = childData.selectedWorkTypeName;
                        self.employee().employeeInfo.fixedWorkInforDto.workTimeName = childData.selectedWorkTimeName;
                        self.workType(childData.selectedWorkTypeCode);
                        self.workTypeName(childData.selectedWorkTypeName);
                        self.workTime(childData.selectedWorkTimeCode);
                        self.workTimeName(childData.selectedWorkTimeName);

                        //value
                        //A5_6,A5_7,A5_10,A5_11 
                        let timeRange1ScreenModel = $("#a5-5").data("screenModel");
                        if (timeRange1ScreenModel) {
                            timeRange1ScreenModel.startTime(childData.first.start != null ? childData.first.start : 0);
                            timeRange1ScreenModel.endTime(childData.first.end != null ? childData.first.end : 0);
                        }

                        let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                        if (timeRange2ScreenModel) {
                            timeRange2ScreenModel.startTime(childData.second.start != null ? childData.second.start : 0);
                            timeRange2ScreenModel.endTime(childData.second.end != null ? childData.second.end : 0);
                        }
                        setTimeout(function() {
                            nts.uk.ui.errors.clearAll();
                        }, 100);
                        self.workTime.valueHasMutated();
                        if(childData.selectedWorkTimeCode != ""){
                            //<<ScreenQuery>> 詳細情報を取得する 
                            self.getMoreInformationOutput(childData.selectedWorkTypeCode, childData.selectedWorkTimeCode);
                            self.displayMoreInformationOutput();
                        }
                    }
                });
            }

            public getInformationStartup(): any {
                let self = this;

                let timeRoundingSettingDto1 = {
                    roundingTime: 100,
                    rounding: 120
                };

                let timeRoundingSettingDto2 = {
                    roundingTime: 100,
                    rounding: 120
                };

                let intervalTimeDto = {
                    intervalTime: 1,
                    rounding: timeRoundingSettingDto2
                };
                let intervalTimeSettingDto = {
                    useIntervalExemptionTime: true,
                    intervalExemptionTimeRound: timeRoundingSettingDto1,
                    intervalTime: intervalTimeDto,
                    useIntervalTime: true,
                };

                let workTimezoneMedicalSetDto1 = {
                    roundingSet: timeRoundingSettingDto1,
                    workSystemAtr: 0,
                    applicationTime: 33
                }
                let workTimezoneMedicalSetDto2 = {
                    roundingSet: timeRoundingSettingDto2,
                    workSystemAtr: 1,
                    applicationTime: 40
                }
                let medicalSet: any = [workTimezoneMedicalSetDto1, workTimezoneMedicalSetDto2];

                let workTimezoneCommonSet = {
                    zeroHStraddCalculateSet: true,
                    intervalSet: intervalTimeSettingDto,
                    subHolTimeSet: [],
                    raisingSalarySet: "ABC",
                    medicalSet: medicalSet,
                    goOutSet: null,
                    stampSet: null,
                    lateNightTimeSet: null,
                    shortTimeWorkSet: null,
                    extraordTimeSet: null,
                    lateEarlySet: null,
                    holidayCalculation: null
                };

                //
                let listUsageTimeAndType: any = [];
                listUsageTimeAndType.push(new shareModelData.UsageTimeAndType(0, 0));
                listUsageTimeAndType.push(new shareModelData.UsageTimeAndType(1, 101));
                listUsageTimeAndType.push(new shareModelData.UsageTimeAndType(2, 102));
                listUsageTimeAndType.push(new shareModelData.UsageTimeAndType(3, 103));
                listUsageTimeAndType.push(new shareModelData.UsageTimeAndType(4, 104));
                listUsageTimeAndType.push(new shareModelData.UsageTimeAndType(5, 105));

                //
                let timeZoneList = [];
                timeZoneList.push(new shareModelData.TimeSpanForCalcDto(50, 60));
                timeZoneList.push(new shareModelData.TimeSpanForCalcDto(60, 70));
                let listName = ["name2"];

                let workAvailabilityDisplayInfoDto = new shareModelData.WorkAvailabilityDisplayInfoDto(
                    shareModelData.AssignmentMethod.TIME_ZONE,
                    listName,
                    timeZoneList
                );

                let workAvailabilityOfOneDayDto = new shareModelData.WorkAvailabilityOfOneDayDto(
                    "employeeId", "2020/01/01", "memo abc", workAvailabilityDisplayInfoDto
                );
                self.informationStartup = new shareModelData.GetInformationStartupOutput(
                    workTimezoneCommonSet, listUsageTimeAndType, true, workAvailabilityOfOneDayDto
                );
            }

            public getMoreInformationOutput(workType: string, workTime: string): any {
                let self = this;

                let timeRoundingSettingDto1 = {
                    roundingTime: 100,
                    rounding: 120
                };

                let timeRoundingSettingDto2 = {
                    roundingTime: 100,
                    rounding: 120
                };

                let intervalTimeDto = {
                    intervalTime: 1,
                    rounding: timeRoundingSettingDto2
                };
                let intervalTimeSettingDto = {
                    useIntervalExemptionTime: true,
                    intervalExemptionTimeRound: timeRoundingSettingDto1,
                    intervalTime: intervalTimeDto,
                    useIntervalTime: true,
                };

                let workTimezoneMedicalSetDto1 = {
                    roundingSet: timeRoundingSettingDto1,
                    workSystemAtr: 0,
                    applicationTime: 55
                }
                let workTimezoneMedicalSetDto2 = {
                    roundingSet: timeRoundingSettingDto2,
                    workSystemAtr: 1,
                    applicationTime: 66
                }
                let medicalSet: any = [workTimezoneMedicalSetDto1, workTimezoneMedicalSetDto2];

                let timeZoneList = [];
                timeZoneList.push(new shareModelData.TimeSpanForCalcDto(100, 120));
                timeZoneList.push(new shareModelData.TimeSpanForCalcDto(121, 130));

                let breakTime = new shareModelData.BreakTimeKdl045Dto(
                    true, timeZoneList
                );
                let workTimezoneCommonSet = {
                    zeroHStraddCalculateSet: true,
                    intervalSet: intervalTimeSettingDto,
                    subHolTimeSet: [],
                    raisingSalarySet: "ABC",
                    medicalSet: medicalSet,
                    goOutSet: null,
                    stampSet: null,
                    lateNightTimeSet: null,
                    shortTimeWorkSet: null,
                    extraordTimeSet: null,
                    lateEarlySet: null,
                    holidayCalculation: null
                };
                self.moreInformation = new shareModelData.GetMoreInformationOutput(
                    workTimezoneCommonSet,
                    breakTime,
                    5//not data
                );
            }

            displayMoreInformationOutput(): void {
                let self = this;
                //A5_14,A5_16
                let medicalSets = self.moreInformation.workTimezoneCommonSet.medicalSet;

                for (let i = 0; i < medicalSets.length; i++) {
                    if (medicalSets[i].workSystemAtr == 0) {//日勤
                        self.dayShiftTime(self.showTimeByMinuteHaveValue0(medicalSets[i].applicationTime));
                        continue;
                    }
                    if (medicalSets[i].workSystemAtr == 1) {//夜勤
                        self.nightShiftTime(self.showTimeByMinuteHaveValue0(medicalSets[i].applicationTime));
                        continue;
                    }
                }
                self.dataSourceTime([]);
                let listData: any = [];
                if (self.moreInformation.breakTime != null) {
                    //A5_23
                    let breakTime = self.moreInformation.breakTime.timeZoneList;
                    for (let i = 0; i < self.moreInformation.breakTime.timeZoneList.length; i++) {
                        let tempBreakTime = breakTime[i];
                        listData.push({
                            range1: ko.observable({
                                startTime: tempBreakTime.start, endTime: tempBreakTime.end, breakFrameNo: i + 1,
                                name: self.showTimeByPeriod(tempBreakTime.start, tempBreakTime.end)
                            })
                        });
                    }
                    self.dataSourceTime(listData);
                }
                self.setListData();
                
                self.fixBreakTime(self.moreInformation.breakTime.fixBreakTime);
                self.includingWorkType(_.includes(self.employee().scheCorrection, self.moreInformation.workTimeForm));
                self.includingWorkType.valueHasMutated();
            }
            
            //4  「List<時刻修正可能>」と「勤務タイプ」と「休憩時間帯を固定にする」にもとづいて、表示方                                                                                                              
            displayByIncludingWorkType(includingWorkType : boolean):void{
                let self = this;
                if(includingWorkType ){
                    if (self.workTime()){
                        self.isEnableA5_5(true);
                        self.isEnableA5_9(true);
                        self.isEnableAllControl(true);
                    }
                }else{
                    self.isEnableA5_5(false);
                    self.isEnableA5_9(false);
                    self.isEnableAllControl(false);
                    self.isEnableA5_2(false);
                    self.isEnableA5_3(false);
                }
                
            }

            displayInformationStartup(): void {
                let self = this;
                //displayA87
                for (let i = 0; i < self.informationStartup.listUsageTimeAndType.length; i++) {
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK) {
                        self.atWork1A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime == 0) {
                            self.atWork1showA9(false);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK2) {
                        self.atWork2A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime == 0) {
                            self.atWork2showA9(false);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK) {
                        self.offWork1A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime == 0) {
                            self.offWork1showA9(false);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK2) {
                        self.offWork2A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime == 0) {
                            self.offWork2showA9(false);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.PRIVATE) {
                        self.privateTimeA87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime == 0) {
                            self.privateTimeshowA9(false);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.UNION) {
                        self.unionTimeA87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime == 0) {
                            self.unionTimeshowA9(false);
                        }
                        continue;
                    }
                }

                //A5_14,A5_16
                let medicalSets = self.informationStartup.workTimezoneCommonSet.medicalSet;
                for (let i = 0; i < medicalSets.length; i++) {
                    if (medicalSets[i].workSystemAtr == 0) {//日勤
                        self.dayShiftTime(self.showTimeByMinuteHaveValue0(medicalSets[i].applicationTime));
                        continue;
                    }
                    if (medicalSets[i].workSystemAtr == 1) {//夜勤
                        self.nightShiftTime(self.showTimeByMinuteHaveValue0(medicalSets[i].applicationTime));
                        continue;
                    }
                }

                //A1_7
                let assignmentMethod = self.informationStartup.workAvaiOfOneDayDto.workAvaiByHolidayDto.assignmentMethod;
                self.isDisableA1_7(self.informationStartup.showYourDesire);
                if (assignmentMethod == shareModelData.AssignmentMethod.HOLIDAY) {
                    self.assignmentMethodName("休日");
                    self.isDisableA1_8(false);
                } else if (assignmentMethod == shareModelData.AssignmentMethod.SHIFT) {
                    self.assignmentMethodName("シフト");
                    let nameList = self.informationStartup.workAvaiOfOneDayDto.workAvaiByHolidayDto.nameList;
                    for (let i = 0; i < nameList.length; i++) {
                        self.listDetail.push(getText('KDL045_49') + nameList[i]);
                    }
                } else {
                    self.assignmentMethodName("時間帯");
                    let timeZoneList = self.informationStartup.workAvaiOfOneDayDto.workAvaiByHolidayDto.timeZoneList;
                    for (let i = 0; i < timeZoneList.length; i++) {
                        self.listDetail.push(getText('KDL045_49') + self.showTimeByPeriod(timeZoneList[i].start, timeZoneList[i].end));
                    }
                }
                self.listDetail();
                if (self.listDetail().length < 1) {
                    self.isDisableA1_8(false);
                } else if (self.listDetail().length == 1) {
                    if (self.informationStartup.showYourDesire == true) {
                        self.isDisableA1_8(false);
                        self.isTextDisableA1_8(true);
                        self.textDisableA1_8(self.listDetail()[0]);
                    }
                } else {
                    if (self.informationStartup.showYourDesire == true) {
                        self.isDisableA1_8(true);
                        self.isTextDisableA1_8(false);
                    }
                }
            }


            closeDialog() {
                nts.uk.ui.windows.close();
            }

            buttonDecision() {
                let self = this;
                nts.uk.ui.errors.clearAll();
                if (self.validate()) {
                    return;
                }
                $('.nts-input').trigger('change');
            }

            validate(): boolean {
                let self = this;
                let checkError = false;
                
                if (_.isNil(self.workType()) || self.workType() == "") {
                    $('#a4-2').ntsError('set',{ messageId: 'Msg_218', messageParams: [getText('KDL045_8')] });
                    $('#a4-2').focus();
                    checkError =  true;
                }
                if(self.isEnableA5_9() && self.timeRange2Value().startTime <= self.timeRange1Value().endTime && self.timeRange2Value().endTime > self.timeRange1Value().startTime ){
                    $('#a5-5').ntsError('set',{ messageId: 'Msg_515', messageParams: [getText('KDL045_12')] });
                    $('#a5-5').focus();
                    checkError =  true;
                }
                
                if(self.isEnableA5_9() && self.timeRange2Value().startTime <= self.timeRange1Value().startTime && self.timeRange2Value().endTime <= self.timeRange1Value().startTime ){
                    $('#a5-9').ntsError('set',{ messageId: 'Msg_772' });
                    $('#a5-9').focus();
                    checkError =  true;
                }
                
                if(self.checkDataSourceTime()){
                    $('#kdl045').ntsError('set',{ messageId: 'Msg_1793' });
                    $('#kdl045').focus();
                }
                
                return checkError;
//
//                for (let i = 0; i < self.dataSourceTime().length - 1; i++) {
//                    if (self.dataSourceTime()[i].range1().endTime > self.dataSourceTime()[i + 1].range1().startTime) {
//                        nts.uk.ui.dialog.alertError({ messageId: 'Msg_515', messageParams: [getText('KDL045_12')] });
//                        return true;
//                    }
//                }
//                
//                for (let i = 0; i < self.dataSourceTime().length; i++) {
//                    if (self.dataSourceTime()[i].range1().endTime > self.dataSourceTime()[i + 1].range1().startTime) {
//                        nts.uk.ui.dialog.alertError({ messageId: 'Msg_515', messageParams: [getText('KDL045_12')] });
//                        return true;
//                    }
//                }


            }
            //kiểm tra xem các khoảng break time có thuộc khoảng tg của WorkNO hay k
            private checkDataSourceTime():boolean{
                let self = this;
                //Trường hợp tồn tại workNo 1
                if(self.isEnableA5_5() && !self.isEnableA5_9()){
                    for(let i = 0; i <self.dataSourceTime().length;i++){
                        if(! (self.dataSourceTime()[i].range1().startTime >= self.timeRange1Value().startTime 
                                && self.dataSourceTime()[i].range1().endTime <= self.timeRange1Value().endTime)){
                            return true;
                        }
                    }
                }
                //Trường hợp tồn tại workNo 1 và 2
                if(self.isEnableA5_5() && self.isEnableA5_9()){
                    for(let i = 0; i <self.dataSourceTime().length;i++){
                        if(!( (self.dataSourceTime()[i].range1().startTime >= self.timeRange1Value().startTime 
                                && self.dataSourceTime()[i].range1().endTime <= self.timeRange1Value().endTime) ||
                               (self.dataSourceTime()[i].range1().startTime >= self.timeRange2Value().startTime 
                                    && self.dataSourceTime()[i].range1().endTime <= self.timeRange2Value().endTime)
                             )){
                            return true;
                        }
                    }
                }
                return false;
                
            }

            private showTimeByPeriod(startTime: number, endTime: number): string {
                let start = (startTime != null || startTime <= 0) ?
                    (startTime / 60 >= 10 ? Math.floor(startTime / 60) + '' : '0' + Math.floor(startTime / 60))
                    + ':' + (startTime % 60 >= 10 ? startTime % 60 + '' : '0' + startTime % 60) : '';
                let end = (endTime != null || endTime <= 0) ?
                    (endTime / 60 >= 10 ? Math.floor(endTime / 60) + '' : '0' + Math.floor(endTime / 60))
                    + ':' + (endTime % 60 >= 10 ? endTime % 60 + '' : '0' + endTime % 60) : '';
                return start + getText('KDL045_50') + end;
            }

            private showTimeByMinute(time: number): string {
                let timeShow = (time != null && time > 0) ?
                    (time / 60 >= 10 ? Math.floor(time / 60) + '' : '0' + Math.floor(time / 60))
                    + ':' + (time % 60 >= 10 ? time % 60 + '' : '0' + time % 60) : '';
                return timeShow;
            }
            private showTimeByMinuteHaveValue0(time: number): string {
                let timeShow = time != null ?
                    (time / 60 >= 10 ? Math.floor(time / 60) + '' : '0' + Math.floor(time / 60))
                    + ':' + (time % 60 >= 10 ? time % 60 + '' : '0' + time % 60) : '';
                return timeShow;
            }
            private settingColumns(): Array<any> {
                let self = this;
                return [
                    {
                        headerText: nts.uk.resource.getText("KDL045_30"), key: "range1", defaultValue: ko.observable({ startTime: 0, endTime: 0,breakFrameNo: 0 }),
                        width: 243, template:
                        `<div data-bind="ntsTimeRangeEditor: {required: true,
                            enable: true,
                            inputFormat: 'time',
                            startTimeNameId: '#[KDL045_32]',
                            endTimeNameId: '#[KDL045_33]',
                            startConstraint: 'TimeWithDayAttr',
                            endConstraint: 'TimeWithDayAttr',
                            paramId: 'KDL045_31'
                                }
                            "/>`
                    }
                ];
            }

        }


    }
}