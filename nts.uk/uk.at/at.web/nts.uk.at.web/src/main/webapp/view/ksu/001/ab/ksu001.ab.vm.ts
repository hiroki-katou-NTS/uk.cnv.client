module nts.uk.at.view.ksu001.ab.viewmodel {
    import setShare = nts.uk.ui.windows.setShared;
    import getShare = nts.uk.ui.windows.getShared;
    import formatById = nts.uk.time.format.byId;
    import alertError = nts.uk.ui.dialog.alertError;
    import getText = nts.uk.resource.getText;
    import characteristics = nts.uk.characteristics;

    export class ScreenModel {

        listWorkType: KnockoutObservableArray<ksu001.a.viewmodel.IWorkTypeDto> = ko.observableArray([]);
        listWorkTime: KnockoutObservableArray<any> = ko.observableArray([]);
        listWorkTime2 : any;
        selectedWorkTypeCode: KnockoutObservable<string>;
        objWorkTime: any;
        input: any
        dataCell: any; // data để paste vào grid
        enableWorkTime : KnockoutObservable<boolean> = ko.observable(true);
        workTimeCode:KnockoutObservable<string>;
        enableListWorkType: KnockoutObservable<boolean> = ko.observable(true);
        KEY: string = 'ksu001Data';
        width: KnockoutObservable<number>;
        tabIndex: KnockoutObservable<number | string>;
        filter: KnockoutObservable<boolean> = ko.observable(false);
        disabled: KnockoutObservable<boolean>;
        workplaceIdKCP013: KnockoutObservable<string> = ko.observable('');
        selected: KnockoutObservable<string> | KnockoutObservableArray<string>;
        dataSources: KnockoutObservableArray<WorkTimeModel>;
        showMode: KnockoutObservable<SHOW_MODE>;
        check: KnockoutObservable<boolean>;

        constructor(data) {
            let self = this;
            
            let workTypeCodeSave = !_.isNil(data)  ? data.workTypeCodeSelected : '';
            let workTimeCodeSave = !_.isNil(data)  ? data.workTimeCodeSelected : '';
            
            let workTimeCode = '';
            if (workTimeCodeSave != '') {
                if (workTimeCodeSave === 'none') {
                    workTimeCode = '';
                } else if (workTimeCodeSave === 'deferred') {
                    workTimeCode = ' ';
                } else {
                    workTimeCode = workTimeCodeSave;
                }
            }
            self.listWorkType = ko.observableArray([]);
            
            self.width    = ko.observable(775);
            self.tabIndex = ko.observable('');
            self.disabled = ko.observable(false);
            self.selected = ko.observable(workTimeCodeSave != '' ? workTimeCode : '');
            self.dataSources = ko.observableArray([]);
            self.showMode = ko.observable(SHOW_MODE.BOTTLE);
            self.check    = ko.observable(false);

            self.dataCell = {};
            
            self.selectedWorkTypeCode = ko.observable(workTypeCodeSave);
            self.workTimeCode = ko.observable(workTimeCodeSave);
            self.selectedWorkTypeCode.subscribe((newValue) => {
                if (newValue == null || newValue == undefined)
                    return;
                __viewContext.viewModel.viewA.userInfor.workTypeCodeSelected = newValue;
                characteristics.save(self.KEY, __viewContext.viewModel.viewA.userInfor);
                
                let workType = _.filter(self.listWorkType(), function(o) { return o.workTypeCode == newValue; });
                if (workType.length > 0) {
                    if (workType[0].workTimeSetting == 2) {
                        self.disabled(true);
                    } else {
                        self.disabled(false);
                    }
                }

                self.updateDataCell(self.objWorkTime);
            });
            
            self.selected.subscribe((wkpTimeCd) => {
                if(_.isNil(wkpTimeCd) || wkpTimeCd == '')
                    return;
                
                __viewContext.viewModel.viewA.userInfor.workTimeCodeSelected = wkpTimeCd;
                characteristics.save(self.KEY, __viewContext.viewModel.viewA.userInfor);
                
                let ds = ko.unwrap(self.dataSources);
                self.listWorkTime2 = ds;

                let itemSelected;
                if(wkpTimeCd === 'none'){
                    itemSelected = _.find(ds, f => f.id === 'none');
                }else if(wkpTimeCd === 'deferred'){
                    itemSelected = _.find(ds, f => f.id === 'deferred');
                }else{
                    itemSelected = _.find(ds, f => f.code === wkpTimeCd);
                }

                if (!itemSelected) {
                    self.updateDataCell(null);
                    self.objWorkTime = null;
                } else {
                    self.updateDataCell(itemSelected);
                    self.objWorkTime = itemSelected;
                }
            });
        }

        // set stick data
        updateDataCell(objWorkTime: any) {
            let self = this;
            
            if (objWorkTime == undefined || objWorkTime == null)
                return;

            let objWorkType = _.filter(self.listWorkType(), function(o) { return o.workTypeCode == self.selectedWorkTypeCode(); });
            
            self.dataCell = {
                objWorkType : objWorkType[0],
                objWorkTime : objWorkTime
            };
            __viewContext.viewModel.viewA.dataCell = self.dataCell;

            if (__viewContext.viewModel.viewA.selectedModeDisplayInBody() == 'time') {
                //貼り付けのパターン1
                if (objWorkType[0].workTimeSetting == 2) {
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName", "startTime", "endTime"]);
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: null,
                        workTimeName: null,
                        startTime   : '',
                        endTime     : '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                } else if (objWorkType[0].workTimeSetting != 2 && self.dataCell.objWorkTime.id === "none") {
                    //貼り付けのパターン2
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName", "startTime", "endTime"]);
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: null,
                        workTimeName: null,
                        startTime   : '',
                        endTime     : '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                } else if (objWorkType[0].workTimeSetting != 2 && self.dataCell.objWorkTime.id === "deferred") {
                    if (objWorkType[0].workStyle == 0) {
                        // HOLIDAY
                        $("#extable").exTable("stickFields", ["workTypeName", "startTime", "endTime"]);
                        // 貼り付けのパターン3
                        $("#extable").exTable("stickData", {
                            workTypeCode: objWorkType[0].workTypeCode,
                            workTypeName: objWorkType[0].abbName,
                            workTimeCode: null,
                            workTimeName: null,
                            startTime: '',
                            endTime: '',
                            achievements: false,
                            workHolidayCls: objWorkType[0].workStyle
                        });
                    } else {
                        $("#extable").exTable("stickFields", ["workTypeName"]);
                        // 貼り付けのパターン3
                        $("#extable").exTable("stickData", {
                            workTypeCode: objWorkType[0].workTypeCode,
                            workTypeName: objWorkType[0].abbName,
                            workTimeCode: null,
                            workTimeName: null,
                            startTime: '',
                            endTime: '',
                            achievements: false,
                            workHolidayCls: objWorkType[0].workStyle
                        });
                    }
                } else if (objWorkType[0].workTimeSetting != 2 && objWorkType[0].workStyle == 0) { // HOLIDAY
                    $("#extable").exTable("stickFields", ["workTypeName", "workTimeName", "startTime", "endTime"]);
                    // 貼り付けのパターン3
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: (objWorkTime != null) ? (objWorkTime.code) : null,
                        workTimeName: (objWorkTime != null && objWorkTime.code != '') ? (objWorkTime.nameAb) : null,
                        startTime: '',
                        endTime: '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                } else {
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName", "startTime", "endTime"]);

                    let startTime = objWorkTime.tzStart1 == null ? '' : formatById("Clock_Short_HM", objWorkTime.tzStart1);
                    let endTime   = objWorkTime.tzEnd1   == null ? '' : formatById("Clock_Short_HM", objWorkTime.tzEnd1);
                    
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: (objWorkTime != null)    ? (objWorkTime.code) : null,
                        workTimeName: (objWorkTime != null     && objWorkTime.code != '') ? (objWorkTime.nameAb) : null,
                        startTime   : (objWorkTime != null > 0 && objWorkTime.code != '') ? (startTime) : '',
                        endTime     : (objWorkTime != null > 0 && objWorkTime.code != '') ? (endTime) : '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                }

            } else if (__viewContext.viewModel.viewA.selectedModeDisplayInBody() == 'shortName') {
                //貼り付けのパターン1
                if (objWorkType[0].workTimeSetting == 2) {
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName"]);
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: null,
                        workTimeName: null,
                        startTime   : '',
                        endTime     : '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                } else if (objWorkType[0].workTimeSetting != 2 && self.dataCell.objWorkTime.code == '') {
                    //貼り付けのパターン2
                    $("#extable").exTable("stickFields", ["workTypeName","workTimeName"]);
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: null,
                        workTimeName: null,
                        startTime   : '',
                        endTime     : '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                } else if (objWorkType[0].workTimeSetting != 2 && self.dataCell.objWorkTime.code == ' ') {
                    $("#extable").exTable("stickFields", ["workTypeName"]);
                    // 貼り付けのパターン3
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: null,
                        workTimeName: null,
                        startTime   : '',
                        endTime     : '',
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                } else {
                    $("#extable").exTable("stickFields", ["workTypeName", "workTimeName"]);
                    $("#extable").exTable("stickData", {
                        workTypeCode: objWorkType[0].workTypeCode,
                        workTypeName: objWorkType[0].abbName,
                        workTimeCode: (objWorkTime != null) ? (objWorkTime.code) : null,
                        workTimeName: (objWorkTime != null &&  objWorkTime.code != '') ? (objWorkTime.nameAb) : null,
                        achievements: false,
                        workHolidayCls: objWorkType[0].workStyle
                    });
                }
            }
            
            __viewContext.viewModel.viewA.setStyler();
        }
    }
    
    enum AttendanceHolidayAttr {
        FULL_TIME = 3, //(3, "１日出勤系"),
        MORNING = 1, //(1, "午前出勤系"),
        AFTERNOON = 2, //(2, "午後出勤系"),
        HOLIDAY = 0, //(0, "１日休日系");
    }

    enum SHOW_MODE {
        // not has any option
        NOT_SET = 0,
        // show none option
        NONE = 1,
        // show deffered option
        DEFFERED = 2,
        // show none & deffered option
        BOTTLE = 3
    }

    interface WorkTimeModel {
        id: string;
        code: string;
        name: string;
        tzStart1: number;
        tzEnd1: number;
        tzStart2: number;
        tzEnd2: number;
        workStyleClassfication: string;
        remark: string;
        useDistintion: number;
        tzStartToEnd1: string;
        tzStartToEnd2: string;
    }
}