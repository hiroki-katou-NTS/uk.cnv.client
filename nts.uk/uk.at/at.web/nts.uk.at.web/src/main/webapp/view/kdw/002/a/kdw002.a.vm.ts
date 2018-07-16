module nts.uk.at.view.kdw002.a {
    export module viewmodel {
        import href = nts.uk.request.jump;
        import getText = nts.uk.resource.getText;
        import infor = nts.uk.ui.dialog.info;
        export class ScreenModel {
            headerColorValue: KnockoutObservable<string>;
            unitRoundings: KnockoutObservableArray<any>;
            selectedOption: KnockoutObservable<any>;
            attendanceItems: KnockoutObservableArray<any>;
            aICurrentCode: KnockoutObservable<any>;
            attendanceItemColumn: KnockoutObservableArray<any>;
            txtItemId: KnockoutObservable<any>;
            txtItemName: KnockoutObservable<any>;
            timeInputCurrentCode: KnockoutObservable<any>;
            linebreak: KnockoutObservable<any>;
            timeInputEnable: KnockoutObservable<boolean>;
            //
            isDaily: boolean;
            isSave : KnockoutObservable<boolean>;
            sideBar :  KnockoutObservable<number>;
            constructor(dataShare: any) {
                var self = this;
                //
                self.isSave = ko.observable(true);
                self.isDaily = dataShare.ShareObject;
                self.sideBar =  ko.observable(1);
                if(!self.isDaily){
                    self.sideBar(2);
                }                
                self.headerColorValue = ko.observable('');
                self.linebreak = ko.observable(0);
                self.unitRoundings = ko.observableArray([]);
                self.timeInputCurrentCode = ko.observable();
                self.txtItemId = ko.observable(null);
                self.txtItemName = ko.observable('');
                self.attendanceItems = ko.observableArray([]);
                self.aICurrentCodes = ko.observableArray([]);
                self.timeInputEnable = ko.observable(true);
                self.aICurrentCode = ko.observable(null);
                self.aICurrentCode.subscribe(attendanceItemId => {
                    if(attendanceItemId){
                        self.isSave(true);
                        var attendanceItem = _.find(self.attendanceItems(), { attendanceItemId: Number(attendanceItemId) });
                        self.txtItemName(attendanceItem.attendanceItemName);
                        self.txtItemId(attendanceItemId);
                        // self.txtItemName(cAttendanceItem.attandanceItemName);
                        self.unitRoundings([
                            { timeInputValue: 0, timeInputName: '1分' }, { timeInputValue: 1, timeInputName: '5分' }, { timeInputValue: 2, timeInputName: '10分' },
                            { timeInputValue: 3, timeInputName: '15分' }, { timeInputValue: 4, timeInputName: '30分' }
                            , { timeInputValue: 5, timeInputName: '60分' }]);
                        self.timeInputCurrentCode(0);
                        if (self.isDaily) {
                            if (attendanceItem.dailyAttendanceAtr == 5) {
                                self.timeInputEnable(true);
                            } else {
                                self.timeInputEnable(false);
                            }
                        } else {
                            if (attendanceItem.dailyAttendanceAtr == 1) {
                                self.timeInputEnable(true);
                            } else {
                                self.timeInputEnable(false);
                            }
                        }
    
                        self.linebreak(attendanceItem.nameLineFeedPosition);
                        if (self.isDaily) {
                            service.getControlOfDailyItem(attendanceItemId).done(cAttendanceItem => {
                                if (!nts.uk.util.isNullOrUndefined(cAttendanceItem)) {
                                    self.txtItemId(cAttendanceItem.itemDailyID);
                                    self.headerColorValue(cAttendanceItem.headerBgColorOfDailyPer);
                                    self.timeInputCurrentCode(cAttendanceItem.inputUnitOfTimeItem);
                                } else {
                                    self.headerColorValue(null);
                                    self.timeInputCurrentCode(0);
                                }
                            });
                        } else {
                            service.getControlOfMonthlyItem(attendanceItemId).done(cAttendanceItem => {
                                if (!nts.uk.util.isNullOrUndefined(cAttendanceItem)) {
                                    self.txtItemId(cAttendanceItem.itemMonthlyId);
                                    self.headerColorValue(cAttendanceItem.headerBgColorOfMonthlyPer);
                                    self.timeInputCurrentCode(cAttendanceItem.inputUnitOfTimeItem);
                                } else {
                                    self.headerColorValue(null);
                                    self.timeInputCurrentCode(0);
                                }
                            });
                        }
                    }else{
                        self.isSave(false);
                        self.txtItemName("");
                        self.txtItemId(attendanceItemId);
                        self.headerColorValue(null);
                        self.timeInputCurrentCode(0);
                    }
                });

                self.attendanceItemColumn = ko.observableArray([
                    { headerText: 'コード', key: 'attendanceItemId', width: 50, dataType: "number" },
                    { headerText: '名称', key: 'attendanceItemName', width: 230, dataType: "string", formatter: _.escape },
                    { key: 'dailyAttendanceAtr', dataType: "number", hidden: true },
                    { key: 'nameLineFeedPosition', dataType: "number", hidden: true }
                ]);
                $(".clear-btn").hide();
                var attendanceItems = [];
                if (self.isDaily) {
                    service.getListDailyAttdItem().done(atItems => {
                        if (!nts.uk.util.isNullOrUndefined(atItems)) {
                            let listAttdID = _.map(atItems,item =>{return item.attendanceItemId; });
                            service.getNameDaily(listAttdID).done(function(dataNew) {
                                for(let i =0;i<atItems.length;i++){
                                    for(let j = 0;j<=dataNew.length; j++){
                                        if(atItems[i].attendanceItemId == dataNew[j].attendanceItemId ){
                                            atItems[i].attendanceName = dataNew[j].attendanceItemName;
                                            break;
                                        }  
                                    }    
                                }
                                
                                atItems.forEach(attendanceItem => {
                                    attendanceItems.push({ 
                                        attendanceItemId: attendanceItem.attendanceItemId,
                                        attendanceItemName: attendanceItem.attendanceName,
                                        dailyAttendanceAtr: attendanceItem.dailyAttendanceAtr, 
                                        nameLineFeedPosition: attendanceItem.nameLineFeedPosition });
                                });
                                self.attendanceItems(attendanceItems);
                                self.aICurrentCode(atItems[0].attendanceItemId);
                            });
                            
                        }
                    });
                } else {
                    service.getListMonthlyAttdItem().done(atItems => {
                        if (!nts.uk.util.isNullOrUndefined(atItems)) {
                            let listAttdID = _.map(atItems,item =>{return item.attendanceItemId; });
                            service.getNameMonthly(listAttdID).done(function(dataNew) {
                                for(let i =0;i<atItems.length;i++){
                                    for(let j = 0;j<=dataNew.length; j++){
                                        if(atItems[i].attendanceItemId == dataNew[j].attendanceItemId ){
                                            atItems[i].attendanceName = dataNew[j].attendanceItemName;
                                            break;
                                        }  
                                    }    
                                }
                                
                                atItems.forEach(attendanceItem => {
                                attendanceItems.push({ attendanceItemId: attendanceItem.attendanceItemId, attendanceItemName: attendanceItem.attendanceItemName, dailyAttendanceAtr: attendanceItem.dailyAttendanceAtr, nameLineFeedPosition: attendanceItem.nameLineFeedPosition });
                            });
                            self.attendanceItems(attendanceItems);
                            self.aICurrentCode(atItems[0].attendanceItemId);
                            });
                        }
                    });
                }

            }

            navigateView(): void {
                var self = this; 
                var path = "/view/kdw/006/a/index.xhtml";
                href(path);
            }
            
            jumpToHome(sidebar): void {
                let self = this;
                nts.uk.request.jump("/view/kdw/006/a/index.xhtml", { ShareObject : sidebar() });
            }

            submitData(): void {
                var self = this;
                var AtItems = {};
                AtItems.companyID = "";

                if (self.headerColorValue()) {
                    AtItems.headerBgColorOfDailyPer = self.headerColorValue();
                }
                if (self.timeInputEnable()) {
                    AtItems.inputUnitOfTimeItem = self.timeInputCurrentCode();
                }
                if (self.isDaily) {
                    AtItems.itemDailyID = self.aICurrentCode();
                    if (self.headerColorValue()) {
                        AtItems.headerBgColorOfDailyPer = self.headerColorValue();
                    }
                    service.updateDaily(AtItems).done(x => {
                        infor(nts.uk.resource.getMessage("Msg_15", []));
                    });
                } else {
                    AtItems.itemMonthlyID = self.aICurrentCode();
                    if (self.headerColorValue()) {
                        AtItems.headerBgColorOfMonthlyPer = self.headerColorValue();
                    }
                    service.updateMonthly(AtItems).done(x => {
                        infor(nts.uk.resource.getMessage("Msg_15", []));
                    });
                }
            }

//            interface IAttendanceItem {
//                attendanceItemId: number;
//                attendanceItemName: string;
//            }
//            class AttendanceItem {
//                attendanceItemId: number;
//                attendanceItemName: string;
//    
//                constructor(params: IAttendanceItem) {
//                    var self = this;
//                    self.attendanceItemId = params.attendanceItemId;
//                    self.attendanceItemName = params.attendanceItemName;
//                }
//            }
        }
    }
}
