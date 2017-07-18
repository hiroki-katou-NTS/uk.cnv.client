module nts.uk.at.view.ksm004.a {
    import flat = nts.uk.util.flatArray;
    import getText = nts.uk.resource.getText;
    import aService = nts.uk.at.view.ksm004.a.service;
    export module viewmodel {
        export class ScreenModel {
            yearMonthPicked: KnockoutObservable<number> = ko.observable(Number(moment(new Date()).format('YYYY01')));
            calendarPanel: ICalendarPanel = {
                optionDates: ko.observableArray([]),
                yearMonth: this.yearMonthPicked,
                firstDay: 0,
                startDate: 1,
                endDate: 31,
                workplaceId: ko.observable("0"),
                workplaceName: ko.observable(""),
                eventDisplay: ko.observable(true),
                eventUpdatable: ko.observable(true),
                holidayDisplay: ko.observable(true),
                cellButtonDisplay: ko.observable(false)
            }
            kcpTreeGrid: ITreeGrid = {
                treeType: 1,
                selectType: 1,
                isDialog: false,
                isMultiSelect: false,
                isShowAlreadySet: true,
                isShowSelectButton: false,
                baseDate: ko.observable(new Date()),
                selectedWorkplaceId: undefined,
                alreadySettingList: ko.observableArray([])
            };
            kcpGridlist: IGridList = {
                listType: 2,
                selectType: 2,
                isDialog: false,
                isMultiSelect: false,
                isShowAlreadySet: true,
                isShowNoSelectRow: false,
                selectedCode: ko.observableArray([]),
                alreadySettingList: ko.observableArray([])
            };
            currentCalendarWorkPlace: KnockoutObservable<SimpleObject> = ko.observable(new SimpleObject('',''));
            currentCalendarClass: KnockoutObservable<SimpleObject> = ko.observable(new SimpleObject('',''));
            currentWorkingDayAtr: number = null;
            isUpdate: KnockoutObservable<boolean> = ko.observable(true);
            constructor() {
                var self = this;
                self.yearMonthPicked.subscribe(value => {
                    let i = $("#sidebar").ntsSideBar("getCurrent");
                    switch(i) {
                        case 1:
                            self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key().toString());
                            break;
                        case 2:
                            self.getCalendarClassById(self.currentCalendarClass().key().toString());
                            break;
                        default:
                            self.getAllCalendarCompany();
                            break;
                    }                
                });
                self.kcpTreeGrid.selectedWorkplaceId = self.currentCalendarWorkPlace().key;
                self.kcpGridlist.selectedCode =  self.currentCalendarClass().key;
                self.calendarPanel.yearMonth(self.yearMonthPicked());
                $("#calendar").ntsCalendar("init", {
                    cellClick: function(date) {
                        nts.uk.ui._viewModel.content.setWorkingDayAtr(date);
                    }
                });
                $("#calendar1").ntsCalendar("init", {
                    cellClick: function(date) {
                        nts.uk.ui._viewModel.content.setWorkingDayAtr(date);
                    }
                });
                $("#calendar2").ntsCalendar("init", {
                    cellClick: function(date) {
                        nts.uk.ui._viewModel.content.setWorkingDayAtr(date);
                    }
                });
                $('#tree-grid').ntsTreeComponent(self.kcpTreeGrid).done(() => {
                    $('#classification-list-setting').ntsListComponent(self.kcpGridlist).done(() => {
                        self.currentCalendarWorkPlace().key(_.first($('#tree-grid')['getDataList']()).workplaceId);
                        self.currentCalendarClass().key(_.first($('#classification-list-setting')['getDataList']()).code);
                        self.currentCalendarWorkPlace().key.subscribe(value => {
                            let data: Array<any> = flat($('#tree-grid')['getDataList'](), 'childs');
                            let item = _.find(data, m => m.workplaceId == value);
                            if (item) {
                                self.currentCalendarWorkPlace().name(item.name);
                            } else {
                                self.currentCalendarWorkPlace().name('');
                            }    
                            self.getCalenderWorkPlaceByCode(value);
                        });
                        self.currentCalendarClass().key.subscribe(value => {
                            let data: Array<any> = $('#classification-list-setting')['getDataList']();
                            let item = _.find(data, m => m.code == value);
                            if (item) {
                                self.currentCalendarClass().name(item.name);
                            } else {
                                self.currentCalendarClass().name('');
                            }    
                            self.getCalendarClassById(value);
                        });
                    });
                });
                $("#sidebar").ntsSideBar("init", {
                    active: 0,
                    activate: (event, info) => {
                        switch(info.newIndex) {
                            case 1:
                                self.yearMonthPicked(Number(moment(new Date()).format('YYYY01')));
                                self.yearMonthPicked.valueHasMutated();
                                self.currentWorkingDayAtr = null;
                                break;
                            case 2:
                                self.yearMonthPicked(Number(moment(new Date()).format('YYYY01')));
                                self.yearMonthPicked.valueHasMutated();
                                self.currentWorkingDayAtr = null;
                                break;
                            default:
                                self.yearMonthPicked(Number(moment(new Date()).format('YYYY01')));
                                self.yearMonthPicked.valueHasMutated();
                                self.currentWorkingDayAtr = null;
                        }
                    }
                });
                
            }
            
            setWorkingDayAtr(date){
                var self = this;
                if(self.currentWorkingDayAtr!=null) {
                    let dateData = self.calendarPanel.optionDates();
                    let existItem = _.find(dateData, item => item.start == date);   
                    if(existItem!=null) {
                        existItem.changeListText(self.currentWorkingDayAtr);   
                    } else {
                        dateData.push(new CalendarItem(date,self.currentWorkingDayAtr));    
                    }
                }
                self.calendarPanel.optionDates.valueHasMutated();
            }
            
            start(): JQueryPromise<any> { 
                var self = this;
                return self.getAllCalendarCompany();
            }
            
            submitCalendar(value){
                var self = this;
                let dayOfMonth: number = moment(self.yearMonthPicked(), "YYYYMM").daysInMonth(); 
                if(self.calendarPanel.optionDates().length<dayOfMonth){
                    nts.uk.ui.dialog.confirm({ messageId: 'Msg_140' }).ifYes(function(){ 
                        self.processData(value, true);    
                    }).ifNo(function(){
                        // do nothing    
                    });
                } else {
                    self.processData(value, false);
                }
            }
            
            processData(value, autoFill){
                var self = this;
                if(self.isUpdate()){
                    switch(value) {
                        case 1:
                            self.updateCalendarWorkPlace(self.convertToCommand(self.calendarPanel.optionDates(),autoFill));
                            break;
                        case 2:
                            self.updateCalendarClass(self.convertToCommand(self.calendarPanel.optionDates(),autoFill));
                            break;
                        default:
                            self.updateCalendarCompany(self.convertToCommand(self.calendarPanel.optionDates(),autoFill));
                            break;
                    }    
                } else {
                    switch(value) {
                        case 1:
                            self.insertCalendarWorkPlace(self.convertToCommand(self.calendarPanel.optionDates(),autoFill));
                            break;
                        case 2:
                            self.insertCalendarClass(self.convertToCommand(self.calendarPanel.optionDates(),autoFill));
                            break;
                        default:
                            self.insertCalendarCompany(self.convertToCommand(self.calendarPanel.optionDates(),autoFill));
                            break;
                    } 
                }        
            }
            
            removeCalendar(value){
                var self = this;
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function(){ 
                    switch(value) {
                        case 1:
                            self.deleteCalendarWorkPlace(self.convertToCommand(self.calendarPanel.optionDates(),false));
                            break;
                        case 2:
                            self.deleteCalendarClass(self.convertToCommand(self.calendarPanel.optionDates(),false));
                            break;
                        default:
                            self.deleteCalendarCompany({yearMonth: self.yearMonthPicked().toString()});
                            break;
                    }  
                }).ifNo(function(){
                    // do nothing           
                });
            }
            
            getAllCalendarCompany(): JQueryPromise<any>{
                nts.uk.ui.block.invisible();
                var self = this; 
                var dfd = $.Deferred();
                aService.getAllCalendarCompany(self.yearMonthPicked().toString())
                    .done((dataCompany) => {
                        self.calendarPanel.optionDates.removeAll();
                        let a = [];
                        if(!nts.uk.util.isNullOrEmpty(dataCompany)){
                            _.forEach(dataCompany,(companyItem)=>{
                                a.push(new CalendarItem(companyItem.dateId,companyItem.workingDayAtr));
                            });   
                            self.calendarPanel.optionDates(a);
                            self.isUpdate(true);
                        } else {
                            self.isUpdate(false);     
                        }
                        self.calendarPanel.optionDates.valueHasMutated();
                        nts.uk.ui.block.clear(); 
                        dfd.resolve();  
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                        dfd.reject();
                    });
                return dfd.promise();    
            }
            
            getCalenderWorkPlaceByCode(value): JQueryPromise<any>{
                nts.uk.ui.block.invisible();
                var self = this; 
                var dfd = $.Deferred();
                aService.getCalendarWorkPlaceByCode(value,self.yearMonthPicked().toString())
                    .done((dataWorkPlace) => {
                        self.calendarPanel.optionDates.removeAll();
                        let a = [];
                        if(!nts.uk.util.isNullOrEmpty(dataWorkPlace)){
                            _.forEach(dataWorkPlace,(workPlaceItem)=>{
                                a.push(new CalendarItem(workPlaceItem.dateId,workPlaceItem.workingDayAtr));
                            });   
                            self.calendarPanel.optionDates(a);
                            self.isUpdate(true); 
                        } else {
                            self.isUpdate(false);      
                        }
                        self.calendarPanel.optionDates.valueHasMutated();
                        nts.uk.ui.block.clear();
                        dfd.resolve();  
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                        dfd.reject();
                    });
                return dfd.promise();  
            }
            
            getCalendarClassById(value): JQueryPromise<any>{
                nts.uk.ui.block.invisible();
                var self = this; 
                var dfd = $.Deferred();
                aService.getCalendarClassById(value,self.yearMonthPicked().toString())
                    .done((dataClass) => {
                        self.calendarPanel.optionDates.removeAll();
                        let a = [];
                        if(!nts.uk.util.isNullOrEmpty(dataClass)){
                            _.forEach(dataClass,(companyItem)=>{
                                a.push(new CalendarItem(companyItem.dateId,companyItem.workingDayAtr));
                            });  
                            self.calendarPanel.optionDates(a);
                            self.isUpdate(true); 
                        } else {
                            self.isUpdate(false);      
                        }
                        self.calendarPanel.optionDates.valueHasMutated();
                        nts.uk.ui.block.clear();
                        dfd.resolve();  
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                        dfd.reject();
                    });
                return dfd.promise();
            }
            
            insertCalendarCompany(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.insertCalendarCompany(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getAllCalendarCompany();    
                        nts.uk.ui.block.clear();  
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            insertCalendarWorkPlace(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.insertCalendarWorkPlace(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key());  
                        nts.uk.ui.block.clear();    
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            insertCalendarClass(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.insertCalendarClass(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getCalendarClassById(self.currentCalendarClass().key());   
                        nts.uk.ui.block.clear(); 
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            updateCalendarCompany(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.updateCalendarCompany(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getAllCalendarCompany();      
                        nts.uk.ui.block.clear();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            updateCalendarWorkPlace(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.updateCalendarWorkPlace(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key());   
                        nts.uk.ui.block.clear();   
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            updateCalendarClass(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.updateCalendarClass(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getCalendarClassById(self.currentCalendarClass().key());    
                        nts.uk.ui.block.clear();  
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            deleteCalendarCompany(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.deleteCalendarCompany(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                        self.getAllCalendarCompany();      
                        nts.uk.ui.block.clear();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            deleteCalendarWorkPlace(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.deleteCalendarWorkPlace(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                        self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key());      
                        nts.uk.ui.block.clear();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            deleteCalendarClass(value){
                nts.uk.ui.block.invisible();
                var self = this; 
                aService.deleteCalendarClass(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                        self.getCalendarClassById(self.currentCalendarClass().key());      
                        nts.uk.ui.block.clear();
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                    });
            }
            
            convertToCommand(inputArray: Array<CalendarItem>, autoFill: boolean){
                var self = this;
                let dayOfMonth: number = moment(self.yearMonthPicked(), "YYYYMM").daysInMonth(); 
                let a = [];
                if(!autoFill) {
                    _.forEach(inputArray, item => {
                        a.push({
                            dateId: Number(moment(item.start).format('YYYYMMDD')),
                            workingDayAtr: self.convertEnumNametoNumber(item.listText[0]),
                            classId: self.currentCalendarClass().key(),
                            workPlaceId: self.currentCalendarWorkPlace().key()
                        });    
                    });
                } else {
                    for(let i=1; i<=dayOfMonth; i++){
                        let indexDate = self.yearMonthPicked().toString()+("00"+i).slice(-2);
                        let result = _.find(self.calendarPanel.optionDates(), o => o.start == moment(indexDate).format('YYYY-MM-DD'));
                        if(result == null) {
                            a.push({
                                dateId: Number(indexDate),
                                workingDayAtr: 0,
                                classId: self.currentCalendarClass().key(),
                                workPlaceId: self.currentCalendarWorkPlace().key()
                            });        
                        } else {
                            a.push({
                                dateId: Number(moment(result.start).format('YYYYMMDD')),
                                workingDayAtr: self.convertEnumNametoNumber(result.listText[0]),
                                classId: self.currentCalendarClass().key(),
                                workPlaceId: self.currentCalendarWorkPlace().key()
                            });    
                        }    
                    }    
                }
                return a;
            }
            
            convertEnumNametoNumber(name){
                let n = '';
                switch(name) {
                    case '非稼働日（法内）': n = 1; break;
                    case '非稼働日（法外）': n = 2; break;
                    default: n = 0;
                }         
                return n;
            }
            
            changeWorkingDayAtr(value){
                var self = this;
                $('.labelSqr').css("border","3px solid #999999");
                if(value!=null) {
                    self.currentWorkingDayAtr = value-1;
                    $('.labelSqr'+value).css("border","3px dashed #008000");
                }
            }
            
            openDialogC() {
                var self = this;
                nts.uk.ui.windows.setShared('KSM004_C_PARAM', 
                {
                    yearMonth: self.yearMonthPicked()
                });
                nts.uk.ui.windows.sub.modal("/view/ksm/004/c/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {});  
            }
            
            openDialogD() {
                var self = this;
                nts.uk.ui.windows.setShared('KSM004_D_PARAM', 
                {
                    classification: $("#sidebar").ntsSideBar("getCurrent"),
                    yearMonth: self.yearMonthPicked(),
                    workPlaceId: self.currentCalendarWorkPlace().key(),
                    classCD: self.currentCalendarClass().key()
                });
                nts.uk.ui.windows.sub.modal("/view/ksm/004/d/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {}); 
            }
        }
        
        interface ICalendarPanel{
            optionDates: KnockoutObservableArray<any>; 
            yearMonth: KnockoutObservable<number>; 
            firstDay: number;
            startDate: number; 
            endDate: number;
            workplaceId: KnockoutObservable<string>;
            workplaceName: KnockoutObservable<string>;
            eventDisplay: KnockoutObservable<boolean>;
            eventUpdatable: KnockoutObservable<boolean>;
            holidayDisplay: KnockoutObservable<boolean>;
            cellButtonDisplay: KnockoutObservable<boolean>;  
        }
        
        interface ITreeGrid {
            treeType: number;
            selectType: number;
            isDialog: boolean;
            isMultiSelect: boolean;
            isShowAlreadySet: boolean;
            isShowSelectButton: boolean;
            baseDate: KnockoutObservable<any>;
            selectedWorkplaceId: KnockoutObservable<any>;
            alreadySettingList: KnockoutObservableArray<any>;
        }
        
        interface IGridList {
            listType: number;
            selectType: number;
            isDialog: boolean;
            isMultiSelect: boolean;
            isShowAlreadySet: boolean;
            isShowNoSelectRow: boolean;
            selectedCode: KnockoutObservable<string>;
            alreadySettingList: KnockoutObservableArray<any>;
        }
        
        class SimpleObject {
            key: KnockoutObservable<string>;
            name: KnockoutObservable<string>;
            constructor(key: string, name: string){
                this.key = ko.observable(key);
                this.name = ko.observable(name);
            }      
        }
        
        class CalendarItem {
            start: string;
            textColor: string;
            backgroundColor: string;
            listText: [];
            constructor(start: number, listText: number) {
                this.start = moment(start.toString()).format('YYYY-MM-DD');
                this.backgroundColor = 'white';
                switch(listText) {
                    case 1:
                        this.textColor = '#FF3B3B';
                        this.listText = [WorkingDayAtr.WorkingDayAtr_WorkPlace.toString()];
                        break;
                    case 2:
                        this.textColor = '#FF3B3B';
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Class.toString()];
                        break;
                    default:
                        this.textColor = '#31859C';
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Company.toString()];
                        break;
                }        
            }
            changeListText(value: number){
                switch(value) {
                    case 1:
                        this.textColor = '#FF3B3B';
                        this.listText = [WorkingDayAtr.WorkingDayAtr_WorkPlace.toString()];
                        break;
                    case 2:
                        this.textColor = '#FF3B3B';
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Class.toString()];
                        break;
                    default:
                        this.textColor = '#31859C';
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Company.toString()];
                        break;
                }         
            }
        }
        
        export enum WorkingDayAtr {
            WorkingDayAtr_Company = '稼働日',
            WorkingDayAtr_WorkPlace = '非稼働日（法内）',
            WorkingDayAtr_Class = '非稼働日（法外）'
        }
    }
}