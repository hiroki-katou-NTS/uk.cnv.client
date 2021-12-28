module nts.uk.at.view.ksm004.a {
    import flat = nts.uk.util.flatArray;
    import getText = nts.uk.resource.getText;
    import aService = nts.uk.at.view.ksm004.a.service;
    export module viewmodel {
        export class ScreenModel {
            cssRangerYM = ko.observable({});
            cssRangerYM1 = ko.observable({});
            cssRangerYM2 = ko.observable({});
            yearMonthPicked: KnockoutObservable<number> = ko.observable(Number(moment(new Date()).format('YYYY01')));
            yearMonthPicked1: KnockoutObservable<number> = ko.observable(Number(moment(new Date()).format('YYYY01')));
            yearMonthPicked2: KnockoutObservable<number> = ko.observable(Number(moment(new Date()).format('YYYY01')));
            currentCalendarWorkPlace: KnockoutObservable<SimpleObject> = ko.observable(new SimpleObject('',''));
            currentCalendarClass: KnockoutObservable<SimpleObject> = ko.observable(new SimpleObject('',''));
            removeFlg: KnockoutObservable<boolean> = ko.observable(false);
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
                cellButtonDisplay: ko.observable(false),
				hidden: false
            }
            calendarPanel1: ICalendarPanel = {
                optionDates: ko.observableArray([]),
                yearMonth: this.yearMonthPicked1,
                firstDay: 0,
                startDate: 1,
                endDate: 31,
                workplaceId: this.currentCalendarWorkPlace().key,
                workplaceName: this.currentCalendarWorkPlace().name,
                eventDisplay: ko.observable(true),
                eventUpdatable: ko.observable(true),
                holidayDisplay: ko.observable(true),
                cellButtonDisplay: ko.observable(false),
				hidden: true
            }
            calendarPanel2: ICalendarPanel = {
                optionDates: ko.observableArray([]),
                yearMonth: this.yearMonthPicked2,
                firstDay: 0,
                startDate: 1,
                endDate: 31,
                workplaceId: ko.observable("0"),
                workplaceName: ko.observable(""),
                eventDisplay: ko.observable(true),
                eventUpdatable: ko.observable(false),
                holidayDisplay: ko.observable(true),
                cellButtonDisplay: ko.observable(false),
				hidden: true
            }
            kcpTreeGrid: ITreeGrid = {
                treeType: 1,
                selectType: 3,
                isDialog: false,
                isMultiSelect: false,
                isShowAlreadySet: false,
                isShowSelectButton: false,
                baseDate: ko.observable(new Date()),
                selectedId: this.currentCalendarWorkPlace().key,
                alreadySettingList: ko.observableArray([]),
                systemType : 2
            };
            kcpGridlist: IGridList = {
                listType: 2,
                selectType: 3,
                isDialog: false,
                isMultiSelect: false,
                isShowAlreadySet: false,
                isShowNoSelectRow: false,
                selectedCode: this.currentCalendarClass().key,
                alreadySettingList: ko.observableArray([])
            };
            currentWorkingDayAtr: number = null;
            isUpdate: KnockoutObservable<boolean> = ko.observable(true);
            isShowDatepicker: boolean = false;
            constructor() {
                var self = this;
//                self.showExportBtn();
                self.yearMonthPicked.subscribe(value => {
                    if (!nts.uk.util.isNullOrEmpty(value)) {
                        if ($("#yearMonthPicker1").ntsError("hasError")) {
                            return;
                        }
                        nts.uk.ui.block.invisible();
                        self.calendarPanel.optionDates(self.getBlankOptionDate());
                        self.getAllCalendarCompany().done(() => {
                            if (Math.floor(value / 100) != Number(Object.keys(self.cssRangerYM())[0])) {
                                self.getCalendarCompanySet()
                                    .done(() => {
                                        $("#yearMonthPicker1").datepicker("hide");
                                        if (self.isShowDatepicker) $("#yearMonthPicker1").datepicker("show");
                                        self.isShowDatepicker = true;
                                        nts.uk.ui.block.clear();
                                    })
                                    .fail((res) => {
                                        nts.uk.ui.dialog.alertError(res.message).then(() => { nts.uk.ui.block.clear(); });
                                    });
                            } else nts.uk.ui.block.clear();
                        }).fail((res) => {
                            nts.uk.ui.dialog.alertError(res.message).then(() => { nts.uk.ui.block.clear(); });
                        });
                    }
                });
                self.yearMonthPicked1.subscribe(value => {
                    if(!nts.uk.util.isNullOrEmpty(value)){
                        if ($("#yearMonthPicker2").ntsError("hasError")) {
                            return;
                        }
                        nts.uk.ui.block.invisible();
                        self.calendarPanel1.optionDates(self.getBlankOptionDate());
                        self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key()).done(()=>{
                            if(Math.floor(value/100)!=Number(Object.keys(self.cssRangerYM1())[0])){
                                self.getCalendarWorkplaceSet(self.currentCalendarWorkPlace().key())
                                .done(()=>{ 
                                    $("#yearMonthPicker2").datepicker("hide");
                                    if(self.isShowDatepicker) $("#yearMonthPicker2").datepicker("show");
                                    self.isShowDatepicker = true;
                                    nts.uk.ui.block.clear(); })
                                .fail((res) => {
                                    nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                                });        
                            } else nts.uk.ui.block.clear();    
                        }).fail((res) => {
                            nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                        });  
                    }       
                });
                self.yearMonthPicked2.subscribe(value => {
                    if(!nts.uk.util.isNullOrEmpty(value)){
                        if ($("#yearMonthPicker3").ntsError("hasError")) {
                            return;
                        }
                        nts.uk.ui.block.invisible();
                        self.calendarPanel2.optionDates(self.getBlankOptionDate());
                        self.getCalendarClassById(self.currentCalendarClass().key()).done(()=>{
                            if(Math.floor(value/100)!=Number(Object.keys(self.cssRangerYM2())[0])){
                                self.getCalendarClassSet(self.currentCalendarClass().key())
                                .done(()=>{ 
                                    $("#yearMonthPicker3").datepicker("hide");
                                    if(self.isShowDatepicker) $("#yearMonthPicker3").datepicker("show");
                                    self.isShowDatepicker = true;
                                    nts.uk.ui.block.clear(); })
                                .fail((res) => {
                                    nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                                });        
                            } else nts.uk.ui.block.clear();    
                        }).fail((res) => {
                            nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                        });  
                    }
                });
                
                // calendar cell click event handler
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
                        nts.uk.ui.block.invisible();
                        self.calendarPanel.optionDates(self.getBlankOptionDate());
                        $.when(self.getCalendarCompanySet(), self.getAllCalendarCompany())
                        .done(()=>{
                            $("#yearMonthPicker1").datepicker("hide");
                            if(self.isShowDatepicker) $("#yearMonthPicker1").datepicker("show");
                            self.isShowDatepicker = true;
                            nts.uk.ui.block.clear(); 
                        })
                        .fail((res) => {
                            nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                        });     
                        if(!nts.uk.util.isNullOrEmpty(self.currentCalendarWorkPlace().key())){
                            self.currentCalendarWorkPlace().name(_.first($('#tree-grid')['getDataList']()).name);   
                        }
                        if(!nts.uk.util.isNullOrEmpty(self.currentCalendarClass().key())){
                            self.currentCalendarClass().name(_.first($('#classification-list-setting')['getDataList']()).name);
                        }
                        // get new Data when treegrid Work Place key change
                        self.currentCalendarWorkPlace().key.subscribe(value => {
                            nts.uk.ui.block.invisible();
                            let treeGridData = $('#tree-grid')['getDataList'](); 
                            let data: Array<any> = flat(treeGridData, 'children');
                            let item = _.find(data, m => m.id == value);
                            if (item) {
                                self.currentCalendarWorkPlace().name(item.name);
                            } else {
                                self.currentCalendarWorkPlace().name('');
                            }
                            self.calendarPanel1.optionDates(self.getBlankOptionDate());
                            $.when(self.getCalendarWorkplaceSet(value),self.getCalenderWorkPlaceByCode(value))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });
                        });
                        
                        // get new Data when gridlist Class key change
                        self.currentCalendarClass().key.subscribe(value => {
                            // nts.uk.ui.block.invisible();   // error when use block ???
                            let data: Array<any> = $('#classification-list-setting')['getDataList']();
                            let item = _.find(data, m => m.code == value);
                            if (item) {
                                self.currentCalendarClass().name(item.name);
                            } else {
                                self.currentCalendarClass().name('');
                            }
                            self.calendarPanel2.optionDates(self.getBlankOptionDate());
                            $.when(self.getCalendarClassSet(value),self.getCalendarClassById(value))
                            .done(()=>{nts.uk.ui.block.clear();})
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });
                        });    
                          
                    });
                });
                
                // sidebar change event handler
                $("#sidebar").ntsSideBar("init", {
                    active: 0,
                    activate: (event, info) => {
                        switch(info.newIndex) {
                            case 1:
                                // select tab Work Place
                                self.removeFlg(true);
                                self.isShowDatepicker = false;
								self.calendarPanel1.hidden = false;
								self.calendarPanel.hidden = true;
								self.calendarPanel2.hidden = true;
                                self.yearMonthPicked1(Number(moment(new Date()).format('YYYY01')));
                                self.yearMonthPicked1.valueHasMutated();
                                self.changeWorkingDayAtr(null);
                                break;
                            case 2:
                                // select tab Class
                                self.removeFlg(true);
                                self.isShowDatepicker = false;
								self.calendarPanel2.hidden = false;
								self.calendarPanel.hidden = true;
								self.calendarPanel1.hidden = true;
                                self.yearMonthPicked2(Number(moment(new Date()).format('YYYY01')));
                                self.yearMonthPicked2.valueHasMutated();
                                self.changeWorkingDayAtr(null);
                                break;
                            default:
                                // select tab Company
                                self.removeFlg(false);
                                self.isShowDatepicker = false;
								self.calendarPanel.hidden = false;
								self.calendarPanel1.hidden = true;
								self.calendarPanel2.hidden = true;
                                self.yearMonthPicked(Number(moment(new Date()).format('YYYY01')));
                                self.yearMonthPicked.valueHasMutated();
                                self.changeWorkingDayAtr(null);
                        }
                    }
                });
            }
            
            /*
                setting date Wokring Day Atr event
            */
            setWorkingDayAtr(date){
                var self = this;
                if(self.currentWorkingDayAtr!=null) {
                    let i = $("#sidebar").ntsSideBar("getCurrent");
                    let dateData;
                    switch(i) {
                        case 1:
                            // select tab Work Place
                            dateData = self.calendarPanel1.optionDates;
                            break;
                        case 2:
                            // select tab Class
                            dateData = self.calendarPanel2.optionDates;
                            break;
                        default:
                            // select tab Company
                            dateData = self.calendarPanel.optionDates;
                            break;
                    }   
                    
                    let existItem = _.find(dateData(), item => item.start == date);   
                    if(existItem!=null) {
                        existItem.changeListText(self.currentWorkingDayAtr);   
                    } else {
                        let item = new CalendarItem(date,self.currentWorkingDayAtr);
                        item.insertText = true;
                        dateData().push(item);    
                    }
                    dateData.valueHasMutated();
                }
            }
            
            /*
                register button handler
            */
            submitCalendar(value){
                var self = this;
                if(value==1){
                    if(nts.uk.util.isNullOrEmpty(self.currentCalendarWorkPlace().key())){
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_339" }).then(()=>{nts.uk.ui.block.clear();});      
                    } else {
                        $(".yearMonthPicker").trigger("validate");
                        if (!nts.uk.ui.errors.hasError()) {
                            let dayOfMonth: number = moment(self.yearMonthPicked1(), "YYYYMM").daysInMonth(); 
                            let daySetnumber = self.calendarPanel1.optionDates().length;
                            if(daySetnumber<dayOfMonth){
                                // when at least 1 day is not select
                                // confirm auto fill data 
                                nts.uk.ui.dialog.confirm({ messageId: 'Msg_140' }).ifYes(function(){ 
                                    self.processData(value, true);    
                                }).ifNo(function(){
                                    // do nothing    
                                });
                            } else {
                                self.processData(value, false);
                            }
                        }        
                    }    
                } else if(value==2){
                    if(nts.uk.util.isNullOrEmpty(self.currentCalendarClass().key())){
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_339" }).then(()=>{nts.uk.ui.block.clear();});   
                    } else {
                        $(".yearMonthPicker").trigger("validate");
                        if (!nts.uk.ui.errors.hasError()) {
                            let dayOfMonth: number = moment(self.yearMonthPicked2(), "YYYYMM").daysInMonth(); 
                            let daySetnumber = self.calendarPanel2.optionDates().length;
                            if(daySetnumber<dayOfMonth){
                                // when at least 1 day is not select
                                // confirm auto fill data 
                                nts.uk.ui.dialog.confirm({ messageId: 'Msg_140' }).ifYes(function(){ 
                                    self.processData(value, true);    
                                }).ifNo(function(){
                                    // do nothing    
                                });
                            } else {
                                self.processData(value, false);
                            }
                        }     
                    } 
                } else {
                    $(".yearMonthPicker").trigger("validate");
                    if (!nts.uk.ui.errors.hasError()) {
                        let dayOfMonth: number = moment(self.yearMonthPicked(), "YYYYMM").daysInMonth(); 
                        let daySetnumber = self.calendarPanel.optionDates().length;
                        if(daySetnumber<dayOfMonth){
                            // when at least 1 day is not select
                            // confirm auto fill data 
                            nts.uk.ui.dialog.confirm({ messageId: 'Msg_140' }).ifYes(function(){ 
                                self.processData(value, true);    
                            }).ifNo(function(){
                                // do nothing    
                            });
                        } else {
                            self.processData(value, false);
                        }
                    }
                }
            }
            
            /*
                process Data when insert/update
            */
            processData(value, autoFill){
                nts.uk.ui.block.invisible();
                var self = this;
                if(self.isUpdate()){
                    // update case
                    switch(value) {
                        case 1:
                            // select tab Work Place
                            self.updateCalendarWorkPlace(self.convertToCommand(self.calendarPanel1.optionDates(),autoFill))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });;
                            break;
                        case 2:
                            // select tab Class
                            self.updateCalendarClass(self.convertToCommand(self.calendarPanel2.optionDates(),autoFill))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });;
                            break;
                        default:
                            // select tab Company
                            self.updateCalendarCompany(self.convertToCommand(self.calendarPanel.optionDates(),autoFill))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });;
                            break;
                    }    
                } else {
                    // insert case
                    switch(value) {
                        case 1:
                            // select tab Work Place
                            self.insertCalendarWorkPlace(self.convertToCommand(self.calendarPanel1.optionDates(),autoFill))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });;
                            break;
                        case 2:
                            // select tab Class
                            self.insertCalendarClass(self.convertToCommand(self.calendarPanel2.optionDates(),autoFill))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });;
                            break;
                        default:
                            // select tab Company
                            self.insertCalendarCompany(self.convertToCommand(self.calendarPanel.optionDates(),autoFill))
                            .done(()=>{ nts.uk.ui.block.clear(); })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });;
                            break;
                    } 
                }        
            }
            
            /*
                remove button handler
            */
            removeCalendar(value){
                var self = this;
                $(".yearMonthPicker").trigger("validate");
                if (!nts.uk.ui.errors.hasError()) {
                    nts.uk.ui.block.invisible();
                    // confirm delete
                    nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function(){ 
                        switch(value) {
                            case 1:
                                // select tab Work Place
                                self.deleteCalendarWorkPlace({
                                    yearMonth: self.yearMonthPicked1().toString(),
                                    workPlaceId: self.currentCalendarWorkPlace().key()
                                }).done(()=>{ nts.uk.ui.block.clear(); })
                                .fail((res) => {
                                    nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                                });;
                                break;
                            case 2:
                                // select tab Class
                                self.deleteCalendarClass({
                                    yearMonth: self.yearMonthPicked2().toString(),
                                    classId: self.currentCalendarClass().key()
                                }).done(()=>{ nts.uk.ui.block.clear(); })
                                .fail((res) => {
                                    nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                                });;
                                break;
                            default:
                                // select tab Company
                                self.deleteCalendarCompany({yearMonth: self.yearMonthPicked().toString()})
                                .done(()=>{ nts.uk.ui.block.clear(); })
                                .fail((res) => {
                                    nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                                });;
                                break;
                        }  
                    }).ifNo(function(){ 
                        nts.uk.ui.block.clear();           
                    });
                }
            }
            
            getCalendarCompanySet(): JQueryPromise<any>{
                var self = this;
                var dfd = $.Deferred();
                aService.getCalendarCompanySet(self.yearMonthPicked().toString())
                .done(data => {
                    if(!nts.uk.util.isNullOrEmpty(data)) {
                        let a = {};
                        a[Math.floor(self.yearMonthPicked()/100)] = data;
                        self.cssRangerYM(a);
                        $("#yearMonthPicker1").datepicker("hide");
                        $("#yearMonthPicker1").datepicker("show");
                    }
                    dfd.resolve(); 
                }).fail(res => {
                    dfd.reject(res);       
                });        
                return dfd.promise(); 
            }
            
            getCalendarWorkplaceSet(value): JQueryPromise<any>{
                var self = this;
                var dfd = $.Deferred();
                aService.getCalendarWorkplaceSet(value,self.yearMonthPicked1().toString())
                .done(data => {
                    if(!nts.uk.util.isNullOrEmpty(data)) {
                        let a = {};
                        a[Math.floor(self.yearMonthPicked()/100)] = data;
                        self.cssRangerYM1(a);
                    }else{
                        self.cssRangerYM1([]);
                    }
                    $("#yearMonthPicker2").datepicker("hide");
                    $("#yearMonthPicker2").datepicker("show");
                    dfd.resolve(); 
                }).fail(res => {
                    dfd.reject(res);        
                });       
                return dfd.promise();  
            }
            
            getCalendarClassSet(value): JQueryPromise<any>{
                var self = this;
                var dfd = $.Deferred();
                aService.getCalendarClassSet(value,self.yearMonthPicked2().toString())
                .done(data => {
                    if(!nts.uk.util.isNullOrEmpty(data)) {
                        let a = {};
                        a[Math.floor(self.yearMonthPicked()/100)] = data;
                        self.cssRangerYM2(a);
                    }else{
                        self.cssRangerYM2([]);
                    }
                    $("#yearMonthPicker3").datepicker("hide");
                    $("#yearMonthPicker3").datepicker("show");
                    dfd.resolve(); 
                }).fail(res => {
                    dfd.reject(res);              
                });    
                return dfd.promise();     
            }
            
            /*
                get Calendar Company by year month
            */
            getAllCalendarCompany(): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.getAllCalendarCompany(self.yearMonthPicked().toString())
                    .done((dataCompany) => {
                        //self.calendarPanel.optionDates.removeAll();
                        let a = [];
                        if(!nts.uk.util.isNullOrEmpty(dataCompany)){
                            _.forEach(dataCompany,(companyItem)=>{
                                a.push(new CalendarItem(companyItem.date,companyItem.workingDayAtr));
                            });   
                            self.calendarPanel.optionDates(a);
                            self.isUpdate(true);
                        } else {
                            self.isUpdate(false);     
                        }
                     //  self.calendarPanel.optionDates.valueHasMutated();
                        $("#yearMonthPicker1").datepicker("hide");
                        if(self.isShowDatepicker) $("#yearMonthPicker1").datepicker("show");
                        dfd.resolve();  
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();    
            }
            
            /*
                get Calendar Work Place by code and year month
            */
            getCalenderWorkPlaceByCode(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.getCalendarWorkPlaceByCode(value,self.yearMonthPicked1().toString())
                    .done((dataWorkPlace) => {
                        let a = [];
                        if(!nts.uk.util.isNullOrEmpty(dataWorkPlace)){
                            _.forEach(dataWorkPlace,(workPlaceItem)=>{
                                a.push(new CalendarItem(workPlaceItem.date,workPlaceItem.workingDayAtr));
                            });   
                            self.calendarPanel1.optionDates(a);
                            self.isUpdate(true); 
                        } else {
                            self.isUpdate(false);      
                        }
                        $("#yearMonthPicker2").datepicker("hide");
                        if(self.isShowDatepicker) $("#yearMonthPicker2").datepicker("show");
                        dfd.resolve();  
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();  
            }
            
            /*
                get Calendar Class by Id and year month
            */
            getCalendarClassById(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.getCalendarClassById(value,self.yearMonthPicked2().toString())
                    .done((dataClass) => {
                        let a = [];
                        if(!nts.uk.util.isNullOrEmpty(dataClass)){
                            _.forEach(dataClass,(companyItem)=>{
                                a.push(new CalendarItem(companyItem.date,companyItem.workingDayAtr));
                            });  
                            self.calendarPanel2.optionDates(a);
                            self.isUpdate(true); 
                        } else {
                            self.isUpdate(false);      
                        }
                        $("#yearMonthPicker3").datepicker("hide");
                        if(self.isShowDatepicker) $("#yearMonthPicker3").datepicker("show");
                        dfd.resolve();  
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                insert Calendar Company
            */
            insertCalendarCompany(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.insertCalendarCompany(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() =>{
                            self.calendarPanel.optionDates(self.getBlankOptionDate());
                        $.when(self.getCalendarCompanySet(), self.getAllCalendarCompany())
                        .done(()=>{ dfd.resolve(); })
                        .fail((res) => { dfd.reject(res); });  
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                insert Calendar Work Place
            */
            insertCalendarWorkPlace(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.insertCalendarWorkPlace(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() =>{
                            self.calendarPanel1.optionDates(self.getBlankOptionDate());
                        $.when(
                            self.getCalendarWorkplaceSet(self.currentCalendarWorkPlace().key()),
                            self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key())
                        ).done(()=>{ dfd.resolve(); })
                        .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                insert Calendar Class
            */
            insertCalendarClass(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.insertCalendarClass(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            self.calendarPanel2.optionDates(self.getBlankOptionDate());
                            $.when(
                                self.getCalendarClassSet(self.currentCalendarClass().key()),
                                self.getCalendarClassById(self.currentCalendarClass().key())
                            ).done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                update Calendar Company
            */
            updateCalendarCompany(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.updateCalendarCompany(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            self.calendarPanel.optionDates(self.getBlankOptionDate());
                            $.when(self.getCalendarCompanySet(), self.getAllCalendarCompany())
                                .done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                update Calendar Work Place
            */
            updateCalendarWorkPlace(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.updateCalendarWorkPlace(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            self.calendarPanel1.optionDates(self.getBlankOptionDate());
                            $.when(
                                self.getCalendarWorkplaceSet(self.currentCalendarWorkPlace().key()),
                                self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key())
                            ).done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                update Calendar Class
            */
            updateCalendarClass(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.updateCalendarClass(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            self.calendarPanel2.optionDates(self.getBlankOptionDate());
                            $.when(
                                self.getCalendarClassSet(self.currentCalendarClass().key()),
                                self.getCalendarClassById(self.currentCalendarClass().key())
                            ).done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                delete Calendar Company
            */
            deleteCalendarCompany(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.deleteCalendarCompany(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                            self.calendarPanel.optionDates(self.getBlankOptionDate());
                            $.when(self.getCalendarCompanySet(), self.getAllCalendarCompany())
                                .done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                delete Calendar Work Place
            */
            deleteCalendarWorkPlace(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.deleteCalendarWorkPlace(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                            self.calendarPanel1.optionDates(self.getBlankOptionDate());
                            $.when(
                                self.getCalendarWorkplaceSet(self.currentCalendarWorkPlace().key()),
                                self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key())
                            ).done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                delete Calendar Class
            */
            deleteCalendarClass(value): JQueryPromise<any>{
                var self = this; 
                var dfd = $.Deferred();
                aService.deleteCalendarClass(value)
                    .done(() => {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                            self.calendarPanel2.optionDates(self.getBlankOptionDate());
                            $.when(
                                self.getCalendarClassSet(self.currentCalendarClass().key()),
                                self.getCalendarClassById(self.currentCalendarClass().key())
                            ).done(() => { dfd.resolve(); })
                                .fail((res) => { dfd.reject(res); });
                        });
                    }).fail((res) => {
                        dfd.reject(res);
                    });
                return dfd.promise();
            }
            
            /*
                create Command Data for insert/update
            */
            convertToCommand(inputArray: Array<CalendarItem>, autoFill: boolean){
                var self = this;
                let dayOfMonth = 0;
                let yearMonth = "";
                switch($("#sidebar").ntsSideBar("getCurrent")) {
                    case 1:
                        // select tab Work Place
                        dayOfMonth = moment(self.yearMonthPicked1(), "YYYYMM").daysInMonth(); 
                        yearMonth = self.yearMonthPicked1().toString();
                        break;
                    case 2:
                        // select tab Class
                        dayOfMonth = moment(self.yearMonthPicked2(), "YYYYMM").daysInMonth(); 
                        yearMonth = self.yearMonthPicked2().toString();
                        break;
                    default:
                        // select tab Company
                        dayOfMonth = moment(self.yearMonthPicked(), "YYYYMM").daysInMonth(); 
                        yearMonth = self.yearMonthPicked().toString();
                }
                let a = [];
                if(!autoFill) {
                     _.forEach(inputArray, item => {
                         if ((moment(item.start).format('YYYY/MM/DD')) != "Invalid date") {
                             a.push({
                                 date: (moment(item.start).format('YYYY/MM/DD')),
                                 workingDayAtr: self.convertEnumNametoNumber(item.listText[0]),
                                 classId: self.currentCalendarClass().key(),
                                 workPlaceId: self.currentCalendarWorkPlace().key()
                             });
                         }
                    });
                } else {
                    for(let i=1; i<=dayOfMonth; i++){
                        let indexDate = yearMonth+("00"+i).slice(-2);
                        let result = _.find(inputArray, o => o.start == moment(indexDate).format('YYYY-MM-DD'));
                        if(result == null) {
                            a.push({
                                date: moment(indexDate).format('YYYY/MM/DD'),
                                workingDayAtr: 0,
                                classId: self.currentCalendarClass().key(),
                                workPlaceId: self.currentCalendarWorkPlace().key()
                            });        
                        } else {
                            a.push({
                                date: (moment(result.start).format('YYYY/MM/DD')),
                                workingDayAtr: self.convertEnumNametoNumber(result.listText[0]),
                                classId: self.currentCalendarClass().key(),
                                workPlaceId: self.currentCalendarWorkPlace().key()
                            });    
                        }    
                    }    
                }
                return a;
            }
            
            /*
                convert enum string nam to number value
            */
            convertEnumNametoNumber(name){
                let n: number;
                switch(name) {
                    case WorkingDayAtr.WorkingDayAtr_WorkPlace: n = 1; break;
                    case WorkingDayAtr.WorkingDayAtr_Class: n = 2; break;
                    default: n = 0;
                }         
                return n;
            }
            
            /*
                change Style when change selected Working Day
            */
            changeWorkingDayAtr(value){
                $('.panel-frame-btn').css("background-color","white");
                const self = this;
                if(value!=null) {
                    if (value == 1) {
                        $('.button-sqr1').css("background-color","#DCE6F2");
                    } else {
                        $('.button-sqr'+value).css("background-color","#FDEADA");
                    }
                    self.currentWorkingDayAtr = value-1;
                } else {
                    self.currentWorkingDayAtr = value;   
                }
            }
            
            /*
                open Dialog KDL058, set param = {yearMonth} 
            */
            openDialogKdl058() {
                nts.uk.ui.block.invisible()
                var self = this;
                nts.uk.ui.windows.setShared('KDL058_A_PARAM', 
                {
                    yearMonth: self.yearMonthPicked().toString().substring(0,4)
                });
                nts.uk.ui.windows.sub.modal("/view/kdl/058/a/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {
                    self.isShowDatepicker = false;
					self.yearMonthPicked.valueHasMutated();
//                    $.when(self.getCalendarCompanySet(), self.getAllCalendarCompany())
//                    .done(()=>{
//                        self.isShowDatepicker = true;
//                        nts.uk.ui.block.clear(); 
//                    })
//                    .fail((res) => {
//                        nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
//                    });      
                });  
            }
            
            /*
                open Dialog D, set param = {classification,yearMonth,workPlaceId,classCD}
            */
            openDialogD(value) {
                nts.uk.ui.block.invisible()
                var self = this;
                if(value==1){
                    if(nts.uk.util.isNullOrEmpty(self.currentCalendarWorkPlace().key())){
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_339" }).then(()=>{nts.uk.ui.block.clear();});      
                    } else {
                        nts.uk.ui.windows.setShared('KSM004_D_PARAM', 
                        {
                            classification: value,
                            yearMonth: self.yearMonthPicked1(),
                            workPlaceId: self.currentCalendarWorkPlace().key()
                        }); 
                        nts.uk.ui.windows.sub.modal("/view/ksm/004/d/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {
                            self.isShowDatepicker = false;
                            $.when(
                                self.getCalendarWorkplaceSet(self.currentCalendarWorkPlace().key()),
                                self.getCalenderWorkPlaceByCode(self.currentCalendarWorkPlace().key())
                            ).done(()=>{ 
                                self.isShowDatepicker = true; 
                                nts.uk.ui.block.clear(); 
                            })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });        
                        });     
                    }    
                } else if(value==2){
                    if(nts.uk.util.isNullOrEmpty(self.currentCalendarClass().key())){
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_339" }).then(()=>{nts.uk.ui.block.clear();});  
                    } else {
                        nts.uk.ui.windows.setShared('KSM004_D_PARAM', 
                        {
                            classification: value,
                            yearMonth: self.yearMonthPicked2(),
                            classCD: self.currentCalendarClass().key()
                        }); 
                        nts.uk.ui.windows.sub.modal("/view/ksm/004/d/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {
                            self.isShowDatepicker = false;
                            $.when(
                                self.getCalendarClassSet(self.currentCalendarClass().key()),
                                self.getCalendarClassById(self.currentCalendarClass().key())
                            ).done(()=>{ 
                                self.isShowDatepicker = true; 
                                nts.uk.ui.block.clear(); 
                            })
                            .fail((res) => {
                                nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                            });        
                        });       
                    } 
                } else {
                    nts.uk.ui.windows.setShared('KSM004_D_PARAM', 
                    {
                        classification: value,
                        yearMonth: self.yearMonthPicked()
                    }); 
                    nts.uk.ui.windows.sub.modal("/view/ksm/004/d/index.xhtml", { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function() {
                        self.isShowDatepicker = false;
                        $.when(self.getCalendarCompanySet(), self.getAllCalendarCompany())
                        .done(()=>{ 
                            self.isShowDatepicker = true;
                            nts.uk.ui.block.clear(); 
                        })
                        .fail((res) => {
                            nts.uk.ui.dialog.alertError(res.message).then(()=>{nts.uk.ui.block.clear();});
                        });        
                    }); 
                }
            }

            public openDialogF(value) {
                const vm = this;
                nts.uk.ui.block.invisible();
                if(value == 1) {
                    nts.uk.ui.windows.setShared('KSM004_F_PARAM',
                        {
                            classification: value,
                            yearMonth: vm.yearMonthPicked1(),
                            workPlaceId: vm.currentCalendarWorkPlace().key()
                        });
                    nts.uk.ui.windows.sub.modal("/view/ksm/004/f/index.xhtml", { title: "hello", dialogClass: "no-close" }).onClosed(function() {
                        vm.isShowDatepicker = false;
                    });
                } else if(value == 2) {
                    if(nts.uk.util.isNullOrEmpty(vm.currentCalendarClass().key())){
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_339" }).then(()=>{nts.uk.ui.block.clear();});
                    } else {
                        nts.uk.ui.windows.setShared('KSM004_F_PARAM', {
                            classification: value,
                            yearMonth: vm.yearMonthPicked2(),
                            classId: vm.currentCalendarClass().key()
                        });
                        nts.uk.ui.windows.sub.modal("/view/ksm/004/f/index.xhtml", {
                            title: "hello",
                            dialogClass: "no-close"
                        }).onClosed(function () {
                            vm.isShowDatepicker = false;
                        });
                    }
                } else {
                    nts.uk.ui.windows.setShared('KSM004_F_PARAM',
                        {
                            classification: value,
                            yearMonth: vm.yearMonthPicked(),
                        });
                    nts.uk.ui.windows.sub.modal("/view/ksm/004/f/index.xhtml", { title: "hello", dialogClass: "no-close" }).onClosed(function() {
                        vm.isShowDatepicker = false;
                    });
                }
                nts.uk.ui.block.clear();
            }
            
            //Init blank calendar option date
            getBlankOptionDate(): any{
                return [{ start: "", textColor: "", backgroundColor: "", listText: [], insertText: false }];
            }
            
            /**
             * show export button by role
             */
            showExportBtn() {
                if (nts.uk.util.isNullOrUndefined(__viewContext.user.role.attendance)
                    && nts.uk.util.isNullOrUndefined(__viewContext.user.role.payroll)
                    && nts.uk.util.isNullOrUndefined(__viewContext.user.role.officeHelper)
                    && nts.uk.util.isNullOrUndefined(__viewContext.user.role.personnel)) {
                    // $("#print-button_1").hide();
                    // $("#print-button_2").hide();
                    // $("#print-button_3").hide();
                } else {
                    // $("#print-button_1").show();
                    // $("#print-button_2").show();
                    // $("#print-button_3").show();
                }
            }

            /**
             * closeDialog
             */
            public opencdl028Dialog() {
                var self = this;
                let params = {
                    date: moment(new Date()).toDate(),
                    mode: 2 //YEAR_PERIOD_FINANCE
                };
                nts.uk.ui.windows.setShared("CDL028_INPUT", params);
                nts.uk.ui.windows.sub.modal("com", "/view/cdl/028/a/index.xhtml").onClosed(function() {
                    var params = nts.uk.ui.windows.getShared("CDL028_A_PARAMS");
                    if (params.status) {
                        self.exportExcel(params.mode, params.startDateFiscalYear, params.endDateFiscalYear);
                    }
                });
            }

            /**
             * Print file excel
             */
            exportExcel(mode: string, startDate: string, endDate: string) : void {
                var self = this;
                nts.uk.ui.block.grayout();
                aService.saveAsExcel(mode, startDate, endDate).done(function() {
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
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
			reloadOnSameYM?: boolean; 
			hidden?: boolean;
        }
        
        interface ITreeGrid {
            treeType: number;
            selectType: number;
            isDialog: boolean;
            isMultiSelect: boolean;
            isShowAlreadySet: boolean;
            isShowSelectButton: boolean;
            baseDate: KnockoutObservable<any>;
            selectedId: KnockoutObservable<any>;
            alreadySettingList: KnockoutObservableArray<any>;
            systemType : number;
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
            listText: Array<any>;
            insertText: boolean;
            constructor(start: number, listText: number) {
                this.start = moment(start.toString()).format('YYYY-MM-DD');
                this.backgroundColor = 'white';
                switch(listText) {
                    case 1:
                        this.textColor = '#FF0000'; //RBG(255,0,0)
                        this.listText = [WorkingDayAtr.WorkingDayAtr_WorkPlace.toString()];
                        break;
                    case 2:
                        this.textColor = '#FF0000'; //RBG(255,0,0)
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Class.toString()];
                        break;
                    default:
                        this.textColor = '#558ED5'; //RBG(85,142,213)
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Company.toString()];
                        break;
                }
                this.insertText = false;        
            }
            changeListText(value: number){
                switch(value) {
                    case 1:
                        this.textColor = '#FF0000'; //RBG(255,0,0)
                        this.listText = [WorkingDayAtr.WorkingDayAtr_WorkPlace.toString()];
                        break;
                    case 2:
                        this.textColor = '#FF0000'; //RBG(255,0,0)
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Class.toString()];
                        break;
                    default:
                        this.textColor = '#558ED5'; //RBG(85,142,213)
                        this.listText = [WorkingDayAtr.WorkingDayAtr_Company.toString()];
                        break;
                }
                this.insertText = true;              
            }
        }
        
        export enum WorkingDayAtr {
            WorkingDayAtr_Company = '稼働日',
            WorkingDayAtr_WorkPlace = '法定休日',
            WorkingDayAtr_Class = '法定外休日'
        }
    }
}