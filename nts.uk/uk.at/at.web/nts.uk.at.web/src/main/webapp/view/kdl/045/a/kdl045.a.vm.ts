module nts.uk.at.view.kdl045.a {

    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
    import shareModelData = nts.uk.at.view.kdl045.share.model;
    import commonKmk003 = nts.uk.at.view.kmk003.a.service.model.common;
	import formatById = nts.uk.time.format.byId;

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
			workTypeAbName: KnockoutObservable<string>;
            workTimeAbName: KnockoutObservable<string>;

            coreTimeFlexSetting: any;
            //A1_5
            basedate: KnockoutObservable<string> = ko.observable("");

            employee: KnockoutObservable<shareModelData.ParamKsu003>;
            timeA10_1: KnockoutObservable<string> = ko.observable("");
            timeA10_2: KnockoutObservable<string> = ko.observable("");

            //A5_2,A5_3
            directAtr: KnockoutObservable<boolean>;
            bounceAtr: KnockoutObservable<boolean>;

            //A8_10
            showLineA8_10: boolean = false;

            //A8_13,A8_15
            childcareNo1: string ="";
            childcareNo2: string ="";
            showChildCare: boolean = false;
            //A8_18,A8_20
            nursingNo1: string ="";
            nursingNo2: string ="";
            showNursing: boolean = false;

            //A8_6_1,A8_6_2,A8_6_3,A8_6_4,A8_6_5,A8_6_6
            atWork1: string ="";
            atWork2: string ="";
            offWork1: string ="";
            offWork2: string ="";
            privateTime: string ="";
            unionTime: string="";

            listPrivateTime :any = [];
            listUnionTime :any = [];

            //A9
            atWork1A9: any = null;
            atWork2A9: any= null;
            offWork1A9: any = null;
            offWork2A9: any = null;
            privateTimeA9: any = null;
            unionTimeA9: any = null;

            atWork1showA9: KnockoutObservable<boolean> = ko.observable(false);
            atWork2showA9: KnockoutObservable<boolean> = ko.observable(false);
            offWork1showA9: KnockoutObservable<boolean> = ko.observable(false);
            offWork2showA9: KnockoutObservable<boolean> = ko.observable(false);
            privateTimeshowA9: KnockoutObservable<boolean> = ko.observable(false);
            unionTimeshowA9: KnockoutObservable<boolean> = ko.observable(false);

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
            isDisableA1_7: KnockoutObservable<boolean> = ko.observable(false);
            isDisableA1_8: KnockoutObservable<boolean> = ko.observable(false);
            isTextDisableA1_8: KnockoutObservable<boolean> = ko.observable(false);
            isDisableA4_3: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA4_6: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_21: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_23: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_26: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA11_3: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA13_1: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA13_2: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA13_3: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA5_1920: KnockoutObservable<boolean> = ko.observable(true);
            isDisableA8_6_5_1 : boolean = false;
            isDisableA8_6_6_1 : boolean = false;
            
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
            workTimeForm : KnockoutObservable<number>;
            workStyle :KnockoutObservable<number> = ko.observable(null);
            
            checkIsHoliday   : KnockoutObservable<boolean> = ko.observable(false); 
            
            disableA10  : KnockoutObservable<boolean> = ko.observable(false);
            
            constructor() {
                let self = this;

                self.employee = ko.observable(getShared('dataShareTo045'));
                self.canModified = (self.employee().canModified == 1 && self.employee().employeeInfo.workInfoDto.isConfirmed == 0) ? true : false;
                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: getText('KDL045_6'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: getText('KDL045_7'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) }
                ]);
                self.selectedTab = ko.observable('tab-1');

                self.dataSourceTime = ko.observableArray([]);

				if(self.employee().employeeInfo.workScheduleDto != null){
					let breakTimeNo = self.employee().employeeInfo.workScheduleDto.listBreakTimeZoneDto.sort((x,y) => {return x.startTime - y.startTime});
					for (let i = 0; i < breakTimeNo.length; i++) {
						let tempBreakTime : any = breakTimeNo[i];
						self.dataSourceTime().push({ range1: ko.observable({ startTime: !_.isNil(tempBreakTime.startTime) ? tempBreakTime.startTime : tempBreakTime.start, 
																	endTime: !_.isNil(tempBreakTime.endTime) ? tempBreakTime.endTime : tempBreakTime.end, breakFrameNo: i + 1,
																			 name : self.showTimeByPeriod(
                                                                             !_.isNil(tempBreakTime.startTime) ? tempBreakTime.startTime : tempBreakTime.start,
                                                                             !_.isNil(tempBreakTime.endTime) ? tempBreakTime.endTime : tempBreakTime.end)
						}) });
					}
				}
				// do có trường hợp fixedWorkInforDto = null nên đang fix tạm - TQP
                self.fixBreakTime(self.employee().employeeInfo.fixedWorkInforDto != null ? self.employee().employeeInfo.fixedWorkInforDto.fixBreakTime ==1 ? true:false : false);
                if(self.fixBreakTime()){
                    self.isEnableAllControl(true);
                }else{
                    self.isEnableAllControl(false);    
                }
                self.fixBreakTime.subscribe(value => {
                    if(value){
                        self.isEnableAllControl(true);
                    }else{
                        self.isEnableAllControl(false);    
                    }
                });
                self.workTimeForm = self.employee().employeeInfo.fixedWorkInforDto != null ?  ko.observable(self.employee().employeeInfo.fixedWorkInforDto.workType) : ko.observable();
                self.includingWorkType(_.includes(self.employee().scheCorrection, self.workTimeForm()));
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
                    startTime: self.employee().employeeInfo.workScheduleDto != null ? self.employee().employeeInfo.workScheduleDto.startTime1 : null,
                    endTime: self.employee().employeeInfo.workScheduleDto != null ? self.employee().employeeInfo.workScheduleDto.endTime1 : null
                });
                self.timeRange2Value = ko.observable({ 
                    startTime: self.employee().employeeInfo.workScheduleDto != null && self.employee().employeeInfo.workScheduleDto.startTime2 != '' ? self.employee().employeeInfo.workScheduleDto.startTime2  : null, 
                    endTime: self.employee().employeeInfo.workScheduleDto != null && self.employee().employeeInfo.workScheduleDto.endTime2 != '' ? self.employee().employeeInfo.workScheduleDto.endTime2 : null});
                self.isShowTimeRange2 = (self.employee().targetInfor == 1 ? true : false);
                if(self.employee().employeeInfo.workScheduleDto == null || (self.employee().employeeInfo.workScheduleDto.startTime2 == 0 || self.employee().employeeInfo.workScheduleDto.endTime2 == 0)
					|| (self.employee().employeeInfo.workScheduleDto.startTime2 == null || self.employee().employeeInfo.workScheduleDto.endTime2 == null) || self.workTimeForm() == 1
					){
                    self.isEnableA5_9(false);
                }
                
				
                self.timeRange1Value.subscribe(value => {
                    console.log("TU");

                });
                self.timeRange2Value.subscribe(value => {
                    console.log("TU2");

                });
                
                //A10_1,A10_2
                self.timeA10_1(self.showTimeByPeriod(self.employee().employeeInfo.workScheduleDto != null ? self.employee().employeeInfo.workScheduleDto.startTime1 : null, 
					self.employee().employeeInfo.workScheduleDto != null ? self.employee().employeeInfo.workScheduleDto.endTime1 : null));
                self.timeA10_2(self.showTimeByPeriod(self.employee().employeeInfo.workScheduleDto != null ? self.employee().employeeInfo.workScheduleDto.startTime2 : null, 
					self.employee().employeeInfo.workScheduleDto != null ? self.employee().employeeInfo.workScheduleDto.endTime2 : null));
                
                
                //A4_4,A4_5,A4_6,A4_7
				self.workType = ko.observable();
                self.workTypeName = ko.observable();
                self.workTime = ko.observable();
                self.workTimeName = ko.observable();
				self.workTypeAbName = ko.observable();
				self.workTimeAbName = ko.observable();
                self.coreTimeFlexSetting = null;

				if(self.employee().employeeInfo.workScheduleDto != null){
					self.workType = ko.observable(self.employee().employeeInfo.workScheduleDto.workTypeCode);
	                self.workTime = ko.observable(self.employee().employeeInfo.workScheduleDto.workTimeCode);
				}
                
               /* if(self.employee().employeeInfo.fixedWorkInforDto != null){
                    self.workTypeName = ko.observable(self.employee().employeeInfo.fixedWorkInforDto.workTypeName);
                    self.workTimeName = ko.observable(self.employee().employeeInfo.fixedWorkInforDto.workTimeName);
                }*/

                //A1_5
                let shortW = moment(self.employee().employeeInfo.workInfoDto.date).format("dd");
                if (shortW == "土") {
                    shortW = "<span style='color:#0000ff;'>（"  + shortW + "）</span>";
                } else if (shortW == "日") {
                    shortW = "<span style='color:#ff0000;'>（" + shortW + "）</span>";
                } else {
                    shortW = 　"（" + shortW + "）" ;
                }
                self.basedate(self.employee().employeeInfo.workInfoDto.date + shortW);

                //A5_2,A5_3
                self.directAtr = ko.observable(self.employee().employeeInfo.workInfoDto.directAtr == 1 ? true : false);
                self.bounceAtr = ko.observable(self.employee().employeeInfo.workInfoDto.bounceAtr == 1 ? true : false);

                //A8_13,A8_15,A8_18,A8_20
                let shortTime = self.employee().employeeInfo.workInfoDto.shortTime;
                for (let i = 0; i < shortTime.length; i++) {
                    self.showLineA8_10 = true;
                    if (shortTime[i].workNo == 1 && shortTime[i].dayDivision == 0) { // shortTimeAtr : (0:育児,1:介護)
                        self.childcareNo1 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showChildCare = true;
                        continue;
                    }
                    if (shortTime[i].workNo == 2 && shortTime[i].dayDivision == 0) {
                        self.childcareNo2 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showChildCare = true;
                        continue;
                    }
                    if (shortTime[i].workNo == 1 && shortTime[i].dayDivision == 1) {
                        self.nursingNo1 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showNursing = true;
                        continue;
                    }
                    if (shortTime[i].workNo == 2 && shortTime[i].dayDivision == 1) {
                        self.nursingNo2 = self.showTimeByPeriod(shortTime[i].startTime, shortTime[i].endTime);
                        self.showNursing = true;
                        continue;
                    }
                }

                //A8_6_1,A8_6_2,A8_6_3,A8_6_4,A8_6_5,A8_6_6 
                //tương ứng vs A8_6_1~A8_6_4  thì 時間帯リスト chỉ có 1 phần tử
                //tương ứng vs A8_6_5~A8_6_6  thì 時間帯リスト có nhiều phần tử
                let listTimeVacationAndType = self.employee().employeeInfo.workInfoDto.listTimeVacationAndType;
                for (let i = 0; i < listTimeVacationAndType.length; i++) {
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK) {
                        self.atWork1 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].start, listTimeVacationAndType[i].timeVacation.timeZone[0].end);
                        let tempData = listTimeVacationAndType[i].timeVacation.usageTime;
                        self.atWork1A9 = new shareModelData.DailyAttdTimeVacationDto(
                            tempData.timeAnnualLeaveUseTime,
                            tempData.timeCompensatoryLeaveUseTime,
                            tempData.sixtyHourExcessHolidayUseTime,
                            tempData.timeSpecialHolidayUseTime,
                            tempData.specialHolidayFrameNo,
                            tempData.timeChildCareHolidayUseTime,
                            tempData.timeCareHolidayUseTime
                         );
                        self.employee().employeeInfo.workInfoDto.listTimeVacationAndType[i].timeVacation.usageTime = self.atWork1A9;
                        if (self.atWork1A9.specialHolidayDisplay != '') {
                            self.atWork1showA9_6 = false;
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK2) {
                        self.atWork2 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].start, listTimeVacationAndType[i].timeVacation.timeZone[0].end);
                        let tempData = listTimeVacationAndType[i].timeVacation.usageTime;
                        self.atWork2A9 = new shareModelData.DailyAttdTimeVacationDto(
                            tempData.timeAnnualLeaveUseTime,
                            tempData.timeCompensatoryLeaveUseTime,
                            tempData.sixtyHourExcessHolidayUseTime,
                            tempData.timeSpecialHolidayUseTime,
                            tempData.specialHolidayFrameNo,
                            tempData.timeChildCareHolidayUseTime,
                            tempData.timeCareHolidayUseTime
                         );
                        self.employee().employeeInfo.workInfoDto.listTimeVacationAndType[i].timeVacation.usageTime = self.atWork2A9;
                        if (self.atWork2A9.specialHolidayDisplay != 0) {
                            self.atWork2showA9_6 = false;
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK) {
                        self.offWork1 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].start, listTimeVacationAndType[i].timeVacation.timeZone[0].end);
                        let tempData = listTimeVacationAndType[i].timeVacation.usageTime;
                        self.offWork1A9 = new shareModelData.DailyAttdTimeVacationDto(
                            tempData.timeAnnualLeaveUseTime,
                            tempData.timeCompensatoryLeaveUseTime,
                            tempData.sixtyHourExcessHolidayUseTime,
                            tempData.timeSpecialHolidayUseTime,
                            tempData.specialHolidayFrameNo,
                            tempData.timeChildCareHolidayUseTime,
                            tempData.timeCareHolidayUseTime
                         );
                        self.employee().employeeInfo.workInfoDto.listTimeVacationAndType[i].timeVacation.usageTime = self.offWork1A9;
                        if (self.offWork1A9.specialHolidayDisplay != '') {
                            self.offWork1showA9_6 = true;
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK2) {
                        self.offWork2 = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].start, listTimeVacationAndType[i].timeVacation.timeZone[0].end);
                        let tempData = listTimeVacationAndType[i].timeVacation.usageTime;
                        self.offWork2A9 = new shareModelData.DailyAttdTimeVacationDto(
                            tempData.timeAnnualLeaveUseTime,
                            tempData.timeCompensatoryLeaveUseTime,
                            tempData.sixtyHourExcessHolidayUseTime,
                            tempData.timeSpecialHolidayUseTime,
                            tempData.specialHolidayFrameNo,
                            tempData.timeChildCareHolidayUseTime,
                            tempData.timeCareHolidayUseTime
                         );
                        self.employee().employeeInfo.workInfoDto.listTimeVacationAndType[i].timeVacation.usageTime = self.offWork2A9;
                        if (self.offWork2A9.specialHolidayDisplay != '') {
                            self.offWork2showA9_6 = true;
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.PRIVATE) {
                        let listPrivateTime :any = [];
                        for(let k = 0;k< listTimeVacationAndType[i].timeVacation.timeZone.length;k++){
                            let tempData = self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[k].start, listTimeVacationAndType[i].timeVacation.timeZone[k].end)
                            listPrivateTime.push(tempData);
                        }
                        self.listPrivateTime = listPrivateTime;
                        if(self.listPrivateTime.length >1){
                            self.isDisableA8_6_5_1 = true;    
                        }
                        self.privateTime = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].start, listTimeVacationAndType[i].timeVacation.timeZone[0].end);
                        let tempData = listTimeVacationAndType[i].timeVacation.usageTime;
                        self.privateTimeA9 = new shareModelData.DailyAttdTimeVacationDto(
                            tempData.timeAnnualLeaveUseTime,
                            tempData.timeCompensatoryLeaveUseTime,
                            tempData.sixtyHourExcessHolidayUseTime,
                            tempData.timeSpecialHolidayUseTime,
                            tempData.specialHolidayFrameNo,
                            tempData.timeChildCareHolidayUseTime,
                            tempData.timeCareHolidayUseTime
                         );
                        self.employee().employeeInfo.workInfoDto.listTimeVacationAndType[i].timeVacation.usageTime = self.privateTimeA9;
                        if (self.privateTimeA9.specialHolidayDisplay != '') {
                            self.privateTimeshowA9_6 = true;
                        }
                        continue;
                    }
                    if (listTimeVacationAndType[i].typeVacation == shareModelData.TimeVacationType.UNION) {
                        let listUnionTime :any = [];
                        for(let k = 0;k< listTimeVacationAndType[i].timeVacation.timeZone.length;k++){
                            let tempData = self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[k].start, listTimeVacationAndType[i].timeVacation.timeZone[k].end)
                            listUnionTime.push(tempData);
                        }
                        self.listUnionTime = listUnionTime;
                        if(self.listUnionTime.length >1){
                            self.isDisableA8_6_6_1 = true;
                        }
                        self.unionTime = listTimeVacationAndType[i].timeVacation.timeZone.length == 0?"": self.showTimeByPeriod(listTimeVacationAndType[i].timeVacation.timeZone[0].start, listTimeVacationAndType[i].timeVacation.timeZone[0].end);
                        let tempData = listTimeVacationAndType[i].timeVacation.usageTime;
                        self.unionTimeA9 = new shareModelData.DailyAttdTimeVacationDto(
                            tempData.timeAnnualLeaveUseTime,
                            tempData.timeCompensatoryLeaveUseTime,
                            tempData.sixtyHourExcessHolidayUseTime,
                            tempData.timeSpecialHolidayUseTime,
                            tempData.specialHolidayFrameNo,
                            tempData.timeChildCareHolidayUseTime,
                            tempData.timeCareHolidayUseTime
                         );
                        self.employee().employeeInfo.workInfoDto.listTimeVacationAndType[i].timeVacation.usageTime = self.unionTimeA9;
                        if (self.unionTimeA9.specialHolidayDisplay != '') {
                            self.unionTimeshowA9_6 = true;
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
                if (!self.canModified) { //修正 (mode update)
                     // (mode read only)
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
                self.workStyle.subscribe(style =>{
                    if(style != null && style !=0){
                        self.checkIsHoliday(false);    
                    }else{
                        self.checkIsHoliday(true);
                    }
                    self.displayByWorkStyle(style);
                });
				setTimeout(() => {
					self.displayByIncludingWorkType(self.includingWorkType());
				}, 100);
            }
            
            displayByWorkStyle(style: number):void{
                let self = this;
                if(style == null || style == 0){
                    //disable
                    self.isDisableA5_23(false);
                    self.isDisableA11_3(false);
                    self.disableA10(false);
    
                    //enable
                    self.directAtr(false);
                    self.bounceAtr(false);
                    self.isEnableA5_2(false);
                    self.isEnableA5_3(false);
                    self.isEnableA5_5(false); //disable A5_6,A5_7
                    self.isEnableA5_9(false); //disable A5_10,A5_11
                    //enable A5_19,A5_20
                    //A5_21
                    self.dataSourceTime([]);
                    self.listDataA11([]);
                    self.isEnableAllControl(false);
                    //A5_14,A5_16
                    self.dayShiftTime(self.showTimeByMinuteHaveValue0(0));
                    self.nightShiftTime(self.showTimeByMinuteHaveValue0(0));

                    //A5_6,A5_7,A5_10,A5_11 = 0
//                    self.timeRange1Value({ startTime: null, endTime: null });
//                    self.timeRange2Value({ startTime: null, endTime: null });
                    setTimeout(() => {
                        let timeRange1ScreenModel = $("#a5-5").data("screenModel");
                        if (timeRange1ScreenModel) {
                            timeRange1ScreenModel.startTime(null);
                            timeRange1ScreenModel.endTime(null);
                        }

                        let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                        if (timeRange2ScreenModel) {
                            timeRange2ScreenModel.startTime(null);
                            timeRange2ScreenModel.endTime(null);
                        }
                    }, 200);
                    
                }else{
					self.disableA10(true);
                    if(self.isEnableA5_9()  == false){
                        setTimeout(() => {
                            let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                            if (timeRange2ScreenModel) {
                                timeRange2ScreenModel.startTime(null);
                                timeRange2ScreenModel.endTime(null);
                            }
                        }, 200);
                    }
                }
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
                block.grayout();
                $.when(self.getInformationStartup()).done(function() {
                    self.displayInformationStartup();
                    if(self.workStyle() !=null && self.workStyle() !=0){
                        self.checkIsHoliday(false);
                        self.workTime.valueHasMutated();
                        self.displayByIncludingWorkType(self.includingWorkType());
                    }else{
                        self.checkIsHoliday(true);
                    }
                    block.clear();
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
                    self.directAtr(false);
                    self.bounceAtr(false);
                    self.isEnableA5_2(false);
                    self.isEnableA5_3(false);
                    self.isEnableA5_5(false); //disable A5_6,A5_7
                    self.isEnableA5_9(false); //disable A5_10,A5_11
                    //enable A5_19,A5_20
                    //A5_21
                    self.dataSourceTime([]);
                    self.listDataA11([]);
                    self.isEnableAllControl(false);
                    //A5_14,A5_16
                    self.dayShiftTime(self.showTimeByMinuteHaveValue0(0));
                    self.nightShiftTime(self.showTimeByMinuteHaveValue0(0));
                    
                    //A5_6,A5_7,A5_10,A5_11 = 0
                    self.timeRange1Value({startTime: null, endTime: null});
                    self.timeRange2Value({startTime: null, endTime: null});
                     let timeRange1ScreenModel = $("#a5-5").data("screenModel");
                    if (timeRange1ScreenModel) {
                        timeRange1ScreenModel.startTime(null);
                        timeRange1ScreenModel.endTime(null);
                    }

                    let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                    if (timeRange2ScreenModel) {
                        timeRange2ScreenModel.startTime(null);
                        timeRange2ScreenModel.endTime(null);
                    }

                    self.timeA10_1(self.showTimeByPeriod(0, 0));
                    self.timeA10_2(self.showTimeByPeriod(0, 0));
                } else {
                    //disable
                    self.isDisableA4_6(true);
                    self.isDisableA5_23(true);
                    self.isDisableA11_3(true);

                    //enable
                    if(self.canModified){
                        self.isEnableA5_2(true);
                        self.isEnableA5_3(true);
                    }
                    self.isEnableA5_5(true); //enable A5_6,A5_7
                    self.isEnableA5_9(true); //enable A5_10,A5_11
                    //enable A5_19,A5_20
                    self.isEnableAllControl(true);
                    if( (self.timeRange2Value().startTime == 0 && self.timeRange2Value().endTime == 0) || (self.timeRange2Value().startTime == "" && self.timeRange2Value().endTime == "") 
                        || self.workTimeForm() == 1 ){
                    	self.isEnableA5_9(false); //disenable A5_10,A5_11
                    	let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                        if (timeRange2ScreenModel) {
                            timeRange2ScreenModel.startTime(null);
                            timeRange2ScreenModel.endTime(null);
                        }
                    }
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
                    selectedWorkTypeCode: self.workType(),
                    workTimeCodes: [],
                    selectedWorkTimeCode: self.workTime()
                }, true);

                let param = {
					disAbleWkTypeCodeLst : self.employee().disAbleWkTypeCodeLst,
                    disWkTypeCon: self.employee().disWkTypeCon
				}
                setShared('paramKsu003Kdl003', param);
                block.grayout();
                nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed(function(): any {
                    //view all code of selected item 
                    let childData = nts.uk.ui.windows.getShared('childData');
                    if (childData) {
                        self.isExistWorkType(true);
                        self.workType(childData.selectedWorkTypeCode);
                        self.workTime(childData.selectedWorkTimeCode);

                        //value
                        //A5_6,A5_7,A5_10,A5_11 
                        let timeRange1ScreenModel = $("#a5-5").data("screenModel");
                        if (timeRange1ScreenModel) {
                            timeRange1ScreenModel.startTime(childData.first.start != null ? childData.first.start : 0);
                            timeRange1ScreenModel.endTime(childData.first.end != null ? childData.first.end : 0);
                        }

                        let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                        if (timeRange2ScreenModel) {
                            timeRange2ScreenModel.startTime(childData.second.start != null ? childData.second.start : "");
                            timeRange2ScreenModel.endTime(childData.second.end != null ? childData.second.end : "");
                        }
                        self.timeRange1Value({ startTime: childData.first.start != null ? childData.first.start : null, endTime: childData.first.end != null ? childData.first.end : null }); 
                        self.timeRange2Value({ startTime: childData.second.start != null ? childData.second.start : null, endTime: childData.second.end != null ? childData.second.end : null });
                        
                        setTimeout(function() {
                            nts.uk.ui.errors.clearAll();
                        }, 100);
                        self.workTime.valueHasMutated();
                        if(childData.selectedWorkTimeCode != ""){
                            //<<ScreenQuery>> 詳細情報を取得する 
                            $.when(self.getMoreInformationOutput(childData.selectedWorkTypeCode, childData.selectedWorkTimeCode)).done(function() {
                                if(self.workStyle() != null && self.workStyle() != 0){ 
                                    self.displayMoreInformationOutput();
                                    if(childData.second.start == null && childData.second.end == null){
                                        self.isEnableA5_9(false);   
                                        let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                                        if (timeRange2ScreenModel) {
                                            timeRange2ScreenModel.startTime(null);
                                            timeRange2ScreenModel.endTime(null);
                                        } 
                                    }
                                }
                               block.clear(); 
                            });
                        }else{
							self.workStyle(null);
                            block.clear();
						}
                    }
                });
            }

            public getInformationStartup(): any {
                let self = this;
                let dfd = $.Deferred();
                let command = {
                    employeeId : self.employee().employeeInfo.workInfoDto.employeeId,
                    baseDate : self.employee().employeeInfo.workInfoDto.date,
                    listTimeVacationAndType : self.employee().employeeInfo.workInfoDto.listTimeVacationAndType,
                    workTimeCode : self.workTime(),
                    workTypeCode : self.workType(),
                    targetOrgIdenInforDto: new shareModelData.TargetOrgIdenInforDto(
                        self.employee().unit,
                        self.employee().unit ==0?self.employee().targetId:null,
                        self.employee().unit ==1?self.employee().targetId:null)
                }
                service.getInformationStartup(command).done(function (result) {
					self.workTypeAbName(result.workTypeSettingName !=null?result.workTypeSettingName.workTypeAbName:null);
        			self.workTimeAbName(result.workTimeSettingName !=null?result.workTimeSettingName.workTimeAbName:null);
                    self.informationStartup = new shareModelData.GetInformationStartupOutput(
                        result.workTimezoneCommonSet, result.listUsageTimeAndType, result.showYourDesire, result.workAvaiOfOneDayDto ,result.workStyle,result.workTypeSettingName,result.workTimeSettingName
                    );
                    dfd.resolve();
                }).fail(function (res: any) {
                    alertError({ messageId: "" });
                });

                
                
                return dfd.promise();
            }
            
            public checkTimeIsIncorrect(): any{
                 let self = this;
                let dfd = $.Deferred();
                let command = {
                    workType : self.workType(),
                    workTime : self.workTime(),
                    workTime1: new shareModelData.TimeZoneDto(new shareModelData.TimeOfDayDto(0,self.timeRange1Value().startTime),new shareModelData.TimeOfDayDto(0,self.timeRange1Value().endTime)),
                    workTime2: self.isShowTimeRange2 && self.isEnableA5_9()?new shareModelData.TimeZoneDto(new shareModelData.TimeOfDayDto(0,self.timeRange2Value().startTime),new shareModelData.TimeOfDayDto(0,self.timeRange2Value().endTime)):null
                }
                block.grayout();
                service.checkTimeIsIncorrect(command).done(function (result) {
                    for(let i = 0;i< result.length;i++){
                        if(!result[i].check){
                            let itemSelect = result[i].workNo1?'#a5-5':'#a5-9';
                            if(result[i].timeSpan == null){
                                $(itemSelect).ntsError('set',{ messageId: 'Msg_439', messageParams: [getText('KDL045_12')] });    
                            }else{
                                if(result[i].timeSpan.startTime == result[i].timeSpan.endTime){
                                    $(itemSelect).ntsError('set',{ messageId: 'Msg_2058', messageParams: [result[i].nameError,formatById("Clock_Short_HM", result[i].timeSpan.startTime)] });
                                }else{
                                    $(itemSelect).ntsError('set',{ messageId: 'Msg_1772', messageParams: [result[i].nameError,formatById("Clock_Short_HM", result[i].timeSpan.startTime),formatById("Clock_Short_HM", result[i].timeSpan.endTime)] });    
                                }
                            }
                        }    
                    }
                    dfd.resolve();
                }).fail(function (res: any) {
                    alertError({ messageId: res.messageId, messageParams: res.parameterIds  });
                }).always(function() {
                    block.clear();
                    dfd.resolve();
                });
                return dfd.promise();
            } 

            public getMoreInformationOutput(workType: string, workTime: string): any {
                let self = this;
                let dfd = $.Deferred();
                let command = {
                    employeeId : self.employee().employeeInfo.workInfoDto.employeeId,
                    workType : self.workType(),
                    workTimeCode : self.workTime()
                }
                block.grayout();
                service.getMoreInformation(command).done(function (result) {
					self.workTypeName(result.workTypeSettingName == null?null : result.workTypeSettingName.workTypeName);
	            	self.workTimeName(result.workTimeSettingName == null?null :result.workTimeSettingName.workTimeName);
					self.workTypeAbName(result.workTypeSettingName == null?null : result.workTypeSettingName.workTypeAbName);
	            	self.workTimeAbName(result.workTimeSettingName == null?null :result.workTimeSettingName.workTimeAbName);
                    self.workStyle(result.workStyle);
                    self.workStyle.valueHasMutated();
                    self.disableA10(false);
                    if(self.workStyle() !=null && self.workStyle() !=0){
                        self.disableA10(true);
                        self.moreInformation = new shareModelData.GetMoreInformationOutput(
                            result.workTimezoneCommonSet,
                            result.breakTime,
                            result.workTimeForm,
                            result.coreTimeFlexSetting
                        );
                    }
                    self.coreTimeFlexSetting = result.coreTimeFlexSetting;
                    dfd.resolve();
                }).fail(function (res: any) {
                    alertError({ messageId: "" });
                    block.clear();
                }).always(function() {
                    block.clear();
                    dfd.resolve();
                });
                

               
                return dfd.promise();
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
                    let breakTime = self.moreInformation.breakTime.timeZoneList.sort((x : any,y : any) => {return x.start - y.start});
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
					self.fixBreakTime(self.moreInformation.breakTime.fixBreakTime);
                }
                self.setListData();
                
                
                self.workTimeForm(self.moreInformation.workTimeForm);
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
                        if((self.timeRange2Value().startTime == 0 && self.timeRange2Value().endTime == 0)
                            || (self.timeRange2Value().startTime == "" && self.timeRange2Value().endTime == "")|| (self.timeRange2Value().startTime == null && self.timeRange2Value().endTime == null) || self.workTimeForm() == 1){
                            self.isEnableA5_9(false); //disenable A5_10,A5_11
                            let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                            if (timeRange2ScreenModel) {
                                timeRange2ScreenModel.startTime(null);
                                timeRange2ScreenModel.endTime(null);
                            } 
                        }
                    }
                }else{
                    self.isEnableA5_5(false);
                    self.isEnableA5_9(false);
                    self.isEnableAllControl(false);
//                    let timeRange1ScreenModel = $("#a5-5").data("screenModel");
//                                        if (timeRange1ScreenModel) {
//                                            timeRange1ScreenModel.startTime(null);
//                                            timeRange1ScreenModel.endTime(null);
//                                        } 
//                    let timeRange2ScreenModel = $("#a5-9").data("screenModel");
//                    if (timeRange2ScreenModel) {
//                        timeRange2ScreenModel.startTime(null);
//                        timeRange2ScreenModel.endTime(null);
//                    } 
                   if((self.timeRange2Value().startTime == 0 && self.timeRange2Value().endTime == 0)
                            || (self.timeRange2Value().startTime == "" && self.timeRange2Value().endTime == "") || self.workTimeForm() == 1){
                            self.isEnableA5_9(false); //disenable A5_10,A5_11
                            let timeRange2ScreenModel = $("#a5-9").data("screenModel");
                            if (timeRange2ScreenModel) {
                                timeRange2ScreenModel.startTime(null);
                                timeRange2ScreenModel.endTime(null);
                            } 
                        }
                }
                
            }

            displayInformationStartup(): void {
                let self = this;
				self.workTypeName(self.informationStartup.workTypeSettingName == null?null: self.informationStartup.workTypeSettingName.workTypeName);
	            self.workTimeName(self.informationStartup.workTimeSettingName == null?null: self.informationStartup.workTimeSettingName.workTimeName);
                //displayA87
                for (let i = 0; i < self.informationStartup.listUsageTimeAndType.length; i++) {
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK) {
                        self.atWork1A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime != 0) {
                            self.atWork1showA9(true);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.ATWORK2) {
                        self.atWork2A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime != 0) {
                            self.atWork2showA9(true);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK) {
                        self.offWork1A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime != 0) {
                            self.offWork1showA9(true);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.OFFWORK2) {
                        self.offWork2A87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime != 0) {
                            self.offWork2showA9(true);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.PRIVATE) {
                        self.privateTimeA87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime != 0) {
                            self.privateTimeshowA9(true);
                        }
                        continue;
                    }
                    if (self.informationStartup.listUsageTimeAndType[i].typeVacation == shareModelData.TimeVacationType.UNION) {
                        self.unionTimeA87(self.showTimeByMinuteHaveValue0(self.informationStartup.listUsageTimeAndType[i].totalTime));
                        if (self.informationStartup.listUsageTimeAndType[i].totalTime != 0) {
                            self.unionTimeshowA9(true);
                        }
                        continue;
                    }
                }

                //A5_14,A5_16
                if(self.informationStartup.workTimezoneCommonSet !=null){
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
                }else{
                    self.dayShiftTime(self.showTimeByMinuteHaveValue0(0));
                    self.nightShiftTime(self.showTimeByMinuteHaveValue0(0));
                }
                self.isDisableA1_7(self.informationStartup.showYourDesire==1?true:false);
                self.assignmentMethodName(getText('KDL045_66'));
                if(self.informationStartup.workAvaiOfOneDayDto !=null){
                    //A1_7
                    let assignmentMethod = self.informationStartup.workAvaiOfOneDayDto.workAvaiByHolidayDto.assignmentMethod;
                    if (assignmentMethod == shareModelData.AssignmentMethod.HOLIDAY) {
                        self.assignmentMethodName(getText('KDL045_46'));
                        //nts.uk.resource.getText('Enum_AssignmentMethod_TIME_ZONE')
                        self.isDisableA1_8(false);
                    } else if (assignmentMethod == shareModelData.AssignmentMethod.SHIFT) {
                        self.assignmentMethodName(getText('KDL045_47'));
                        let nameList = self.informationStartup.workAvaiOfOneDayDto.workAvaiByHolidayDto.nameList;
                        for (let i = 0; i < nameList.length; i++) {
                            self.listDetail.push(nameList[i]);
                        }
                    }else if (assignmentMethod == shareModelData.AssignmentMethod.TIME_ZONE) {
                        self.assignmentMethodName(getText('KDL045_48'));
                        let timeZoneList = self.informationStartup.workAvaiOfOneDayDto.workAvaiByHolidayDto.timeZoneList;
                        for (let i = 0; i < timeZoneList.length; i++) {
                            self.listDetail.push(self.showTimeByPeriod(timeZoneList[i].start, timeZoneList[i].end));
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
                }else{
                    self.assignmentMethodName(getText('KDL045_66'));
                    self.isDisableA1_8(false);
                }
//                self.workStyle(self.informationStartup.workStyle);
            }


            closeDialog() {
                nts.uk.ui.windows.close();
            }

            buttonDecision() {
                let self = this;
                if (_.isNil(self.workType()) || self.workType() == "") {
                    $('#a4-2').ntsError('set',{ messageId: 'Msg_218', messageParams: [getText('KDL045_8')] });
                    $('#a4-2').focus();
                    return;
                }
                nts.uk.ui.errors.clearAll();
                //workStyle không phải là ngày nghỉ 
                if(self.workStyle() != null && self.workStyle() != 0){ 
                    if (self.validate()) {
                        return;
                    }
//                    $('.nts-input').trigger('change');
                    let trigger1 = $(_.filter( $('.nts-input'),o =>{ return o.attributes.disabled == undefined}));
                    trigger1.trigger('change');
                    if (nts.uk.ui.errors.hasError()){
                        return;
                    }
                
                
                    $.when(self.checkTimeIsIncorrect()).done(function() {
                        if (nts.uk.ui.errors.hasError()){
                            return;
                        }
                        let listBreakTimeZoneDto = [];
                        for(let i =0;i<self.dataSourceTime().length;i++){
                            let temp = {
                                start : self.dataSourceTime()[i].range1().startTime,
                                end  : self.dataSourceTime()[i].range1().endTime,
                                breakFrameNo : i+1     
                            }
                            listBreakTimeZoneDto.push(temp);
                        }
                        //対象社員の社員勤務予定dto
                        let workScheduleDto = {
                                workTypeCode : self.workType(),//勤務種類コード
                                workTimeCode : self.workTime()==""?null:self.workTime(),//就業時間帯コード
                                startTime1 : self.timeRange1Value().startTime,//開始時刻１
                                endTime1 : self.timeRange1Value().endTime,//終了時刻１
                                startTime2 : self.isShowTimeRange2 ? self.timeRange2Value().startTime:null,//開始時刻2
                                endTime2 :  self.isShowTimeRange2 ?self.timeRange2Value().endTime:null,//終了時刻2
                                listBreakTimeZoneDto : listBreakTimeZoneDto //List<休憩時間帯>
                            };
                        
                        let workInfoDto = {
                                directAtr : self.directAtr()?1:0,//直行区分
                                bounceAtr : self.bounceAtr()?1:0 //直帰区分
                            };
                        
                        let fixedWorkInforDto = {
                                workTypeName : self.workTypeAbName() ,//勤務種類名称
                                workTimeName : self.workTimeAbName(), //就業時間帯名称
                                workType : self.workTimeForm(),//勤務タイプ
                                fixBreakTime : self.fixBreakTime(),
								isHoliday : self.workStyle() == 0 ? true : false,
                                coreTimeFlexSetting : self.coreTimeFlexSetting 
                            };
                        
                        let resultKdl045 = {
                            workScheduleDto: workScheduleDto,
                            workInfoDto: workInfoDto,
                            fixedWorkInforDto: fixedWorkInforDto
                        };
        
        
                        setShared('dataFromKdl045', resultKdl045);
                        nts.uk.ui.windows.close();
                    });
                }else{
                    //対象社員の社員勤務予定dto
                    let workScheduleDto = {
                            workTypeCode : self.workType(),//勤務種類コード
                            workTimeCode : null,//就業時間帯コード
                            startTime1 : null,//開始時刻１
                            endTime1 : null,//終了時刻１
                            startTime2 : null,//開始時刻2
                            endTime2 :  null,//終了時刻2
                            listBreakTimeZoneDto : [] //List<休憩時間帯>
                        };
                    
                    let workInfoDto = {
                            directAtr : 0,//直行区分
                            bounceAtr : 0 //直帰区分
                        };
                    
                    let fixedWorkInforDto = {
                            workTypeName : self.workTypeAbName(),//勤務種類名称
                            workTimeName : null, //就業時間帯名称
                            workType : null,//勤務タイプ
                            fixBreakTime : false,
                            isHoliday : self.workStyle() == 0 ? true : false
                        };
                    
                    let resultKdl045 = {
                        workScheduleDto: workScheduleDto,
                        workInfoDto: workInfoDto,
                        fixedWorkInforDto: fixedWorkInforDto
                    };
    
                    setShared('dataFromKdl045', resultKdl045);
                    nts.uk.ui.windows.close();
                }
            }

            validate(): boolean {
                let self = this;
                let checkError = false;
                if(self.isShowTimeRange2){
                    if(self.isEnableA5_9() && self.timeRange2Value().startTime < self.timeRange1Value().endTime && self.timeRange2Value().endTime > self.timeRange1Value().startTime ){
                        $('#a5-5').ntsError('set',{ messageId: 'Msg_515', messageParams: [getText('KDL045_12')] });
                        checkError =  true;
                    }
                
                    if(self.isEnableA5_9() && self.timeRange2Value().startTime <= self.timeRange1Value().startTime && self.timeRange2Value().endTime <= self.timeRange1Value().startTime ){
                        $('#a5-9').ntsError('set',{ messageId: 'Msg_772' });
                        $('#a5-9').focus();
                        checkError =  true;
                    }
                }  
                
//                if(self.checkDataSourceTime()){
//                    $('#kdl045').ntsError('set',{ messageId: 'Msg_1793' });
//                    $('#kdl045').focus();
//                }
                
                return checkError;

            }
            //kiểm tra xem các khoảng break time có thuộc khoảng tg của WorkNO hay k
//            private checkDataSourceTime():boolean{
//                let self = this;
//                //Trường hợp tồn tại workNo 1
//                if(self.isEnableA5_5() && (!self.isEnableA5_9() || !self.isShowTimeRange2)){
//                    for(let i = 0; i <self.dataSourceTime().length;i++){
//                        if(! (self.dataSourceTime()[i].range1().startTime >= self.timeRange1Value().startTime 
//                                && self.dataSourceTime()[i].range1().endTime <= self.timeRange1Value().endTime)){
//                            return true;
//                        }
//                    }
//                }
//                //Trường hợp tồn tại workNo 1 và 2
//                if(self.isEnableA5_5() && self.isEnableA5_9() && self.isShowTimeRange2){
//                    for(let i = 0; i <self.dataSourceTime().length;i++){
//                        if(!( (self.dataSourceTime()[i].range1().startTime >= self.timeRange1Value().startTime 
//                                && self.dataSourceTime()[i].range1().endTime <= self.timeRange1Value().endTime) ||
//                               (self.dataSourceTime()[i].range1().startTime >= self.timeRange2Value().startTime 
//                                    && self.dataSourceTime()[i].range1().endTime <= self.timeRange2Value().endTime)
//                             )){
//                            return true;
//                        }
//                    }
//                }
//                return false;
//                
//            }

            private showTimeByPeriod(startTime: number, endTime: number): string {
                let start = (startTime != null || startTime <= 0) ?
                    Math.floor(startTime / 60)
                    + ':' + (startTime % 60 >= 10 ? startTime % 60 + '' : '0' + startTime % 60) : '';
                let end = (endTime != null || endTime <= 0) ?
                    Math.floor(endTime / 60)
                    + ':' + (endTime % 60 >= 10 ? endTime % 60 + '' : '0' + endTime % 60) : '';
                return start + getText('KDL045_50') + end;
            }

            private showTimeByMinute(time: number): string {
                let timeShow = (time != null && time > 0) ?
                    Math.floor(time / 60)
                    + ':' + (time % 60 >= 10 ? time % 60 + '' : '0' + time % 60) : '';
                return timeShow;
            }
            private showTimeByMinuteHaveValue0(time: number): string {
                let timeShow = time != null ?
                    Math.floor(time / 60)
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