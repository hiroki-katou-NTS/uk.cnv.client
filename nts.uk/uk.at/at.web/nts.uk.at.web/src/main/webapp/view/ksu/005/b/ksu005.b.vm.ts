module nts.uk.at.view.ksu005.b {
    import getText = nts.uk.resource.getText;
    import setShare = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const Paths = {
        GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID:"ctx/at/schedule/scheduletable/getall",
        GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CODE:"ctx/at/schedule/scheduletable/getone", 
        REGISTER_SCHEDULE_TABLE_OUTPUT_SETTING:"ctx/at/schedule/scheduletable/register",
        UPDATE_SCHEDULE_TABLE_OUTPUT_SETTING:"ctx/at/schedule/scheduletable/update",
        DELETE_SCHEDULE_TABLE_OUTPUT_SETTING:"ctx/at/schedule/scheduletable/delete",
    };
    let checkbox : KnockoutObservable<boolean>= ko.observable(false);
    let isCheckAll: boolean = false;
    @bean()
    class Ksu005bViewModel extends ko.ViewModel {
        currentScreen: any = null;
        items: KnockoutObservableArray<ItemModel> =  ko.observableArray([]);
        persons: KnockoutObservableArray<any> = ko.observableArray([]);
        columns: KnockoutObservableArray<NtsGridListColumn>;
        columns3: KnockoutObservableArray<NtsGridListColumn>;
        selectedCode: KnockoutObservable<string>= ko.observable('');
        selectedPerson: KnockoutObservable<any> = ko.observable();
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string>;
        addSelected: any;        
        lstAddColInfo: KnockoutObservableArray<any>;
        isEditing: KnockoutObservable<boolean> = ko.observable(false);
        enableDelete: KnockoutObservable<boolean> = ko.observable(true);

        itemsSwap: KnockoutObservableArray<any> = ko.observableArray([]);
        columns2: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
        currentCodeListSwap: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedCodeListSwap: KnockoutObservableArray<any> = ko.observableArray([]);

        lstWorkInfo: KnockoutObservableArray<any>;
        workSelected: any;
        checked: KnockoutObservable<boolean>;
        isShiftBackgroundColor: KnockoutObservable<boolean> = ko.observable(false);

        itemList: KnockoutObservableArray<ScreenItem> = ko.observableArray([]);

        countNumberRow: number = 1;
        checkAll: KnockoutObservable<boolean> = ko.observable(false); 
        checkOne: KnockoutObservable<boolean> = ko.observable(false); 
        screenItem: KnockoutObservableArray<ScreenItem> = ko.observableArray([]);

        personalInfoItems: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.ScheduleTablePersonalInfoItem); 
	    attendanceItems: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.ScheduleTableAttendanceItem);
        workplaceCounterCategories: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.WorkplaceCounterCategory);
        personalCounterCategory: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.PersonalCounterCategory);
        
        selectedPersonalInfoItem: KnockoutObservable<string> = ko.observable("");        
        selectedAdditionalInfoItem: KnockoutObservable<string> = ko.observable("");
        selectedAttendanceItem: KnockoutObservable<string> = ko.observable("");

        isEnableAddBtn :KnockoutObservable<boolean> = ko.observable(true);
        isEnableDelBtn :KnockoutObservable<boolean> = ko.observable(false);
        isEnableAdditionInfo :KnockoutObservable<boolean> = ko.observable(true);
        isEnableAttendanceItem :KnockoutObservable<boolean> = ko.observable(true);
        scheduleTableOutputSetting: KnockoutObservable<ScheduleTableOutputSetting> = ko.observable(new ScheduleTableOutputSetting());        
        isReload :KnockoutObservable<boolean> = ko.observable(false);

        constructor() {            
            super();
            const self = this;                    
            self.personalInfoItems.splice(0, 0, {value: -1 , name:getText('KSU005_68')})
            self.attendanceItems.splice(0, 0, {value: -1 , name:getText('KSU005_68')}) 
            self.personalCounterCategory.splice(0, 0, {value: -1 , name:getText('KSU005_68')});

            self.columns = ko.observableArray([
                { headerText: getText('KSU005_18'), key: 'code', width: 60 },
                { headerText: getText('KSU005_19'), key: 'name', width: 150}
            ]);

            self.columns2 = ko.observableArray([
                { headerText: getText('KSU005_43'), key: 'name', width: 160 },
                { headerText: 'No', key: 'value', width: 50, hidden: true }, 
            ]);
 
            self.columns3 = ko.observableArray([     
                { headerText: "", key: 'value', width: 30, hidden: true},          
                { headerText: getText('KSU005_48'), key: 'name', width: 150}
            ]);

            self.tabs = ko.observableArray([
                {id: 'tab-1', title: getText('KSU005_23'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true)},
                {id: 'tab-2', title: getText('KSU005_24'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true)},
                {id: 'tab-3', title: getText('KSU005_25'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true)}
            
            ]);

            self.selectedTab = ko.observable('tab-1');

            self.lstAddColInfo = ko.observableArray([
                { code: '1', name: getText('KSU005_27') },
                { code: '0', name: getText('KSU005_28') }
            ]);

            self.addSelected = ko.observable(0);

            self.checked = ko.observable(false);

            self.lstWorkInfo = ko.observableArray([
                { code: '0', name: getText('KSU005_73') },
                { code: '1', name: getText('KSU005_74') }
            ]);
            self.workSelected = ko.observable(0);

            $("#fixed-table").ntsFixedTable({ height: 400, width: 660 }); 

            self.scheduleTableOutputSetting().shiftBackgroundColor.subscribe((value)=>{
                if(value ==1 ){
                    self.isShiftBackgroundColor(true);
                } else {
                    self.isShiftBackgroundColor(false);
                }
            });
            self.isShiftBackgroundColor.subscribe((value) => {     
                if(self.isReload()) {
                    self.isReload(false);
                    return;
                } else {
                    if(value){
                        self.itemList.removeAll();
                        if(self.selectedCode()== ""){
                            self.initialData();
                        } else {
                            self.loadDetail(self.selectedCode(), false, true);
                        }
                        
                        self.isEnableAttendanceItem(false);
                        self.isEnableAddBtn(false);
                    } else {                    
                        self.loadDetail(self.selectedCode(), true, value);
                        self.isEnableAttendanceItem(true);
                        self.isEnableAddBtn(true);
                    }
                    if(self.countNumberRow < 10){
                        value ? self.isEnableAddBtn(false): self.isEnableAddBtn(true);
                    } else {
                        self.isEnableAddBtn(false)
                    }      
                }                
            });            

            self.checkAll.subscribe((value) => {     
                isCheckAll = true;           
                let temp = self.itemList();
                if (value) {
                    for (let i = 1; i < temp.length; i++) {
                        temp[i].checked(true);
                    }                        
                    self.isEnableDelBtn(true);
                    self.itemList(temp);
                    self.checkOne(false);
                } else {
                    if(!self.checkOne()){
                        for (let i = 1; i < temp.length; i++) {
                            temp[i].checked(false);
                        }
                        self.itemList(temp);
                        self.checkOne(false);                        
                        self.isEnableDelBtn(false);
                    } 
                }
                isCheckAll = false; 
            });

            self.checked.subscribe((value) => {
                self.isEnableDelBtn(value);
            });

            checkbox.subscribe((v) => {               
                let evens = _.filter(self.itemList(), function (n) {
                    return n.checked();
                });
                self.checkOne(true);
                
                if (evens.length > 0) {
                    self.isEnableDelBtn(true);
                }
                if(evens.length == self.itemList().length - 1){
                    self.checkAll(true);
                    isCheckAll = false;
                } 

                if (evens.length < self.itemList().length -1) {
                if (evens.length == 0 ) {                        
                    self.isEnableDelBtn(false);
                    self.checkAll(false);
                    isCheckAll = false;
                } else if(evens.length < self.itemList().length -1) {
                    self.checkAll(false);
                    isCheckAll = false;
                }}
            });

            self.scheduleTableOutputSetting().additionalColumn.subscribe((value) => {
                if(value == 1 ){
                    self.isEnableAdditionInfo(true);
                } else {
                    self.isEnableAdditionInfo(false);
                }
            });
            
            self.selectedCode.subscribe((code: string) => { 
                if (_.isEmpty(code)) {
                    self.clearData();
                } else {
                    self.findDetail(code);
                    self.isEnableDelBtn(false);
                }
                self.$blockui("hide");
            });        
            self.loadData();
        }

        findDetail(code: string): void {
            const self = this;
            self.checkAll(false);                                
            if (code != null && !_.isEmpty(code)) {
                self.$blockui("invisible");
                self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CODE + "/" + code).done((data: IScheduleTableOutputSetting) => {
                    self.countNumberRow = 1;
                    if (!_.isNull(data) && !_.isEmpty(data)) {
                        self.clearError();
                        let personalInfoItemsSize = data.personalInfo.length;
                        let additionalItemsSize = data.attendanceItem.length;
                        let attendanceItemSize = data.attendanceItem.length;
                        let maxLength = Math.max(personalInfoItemsSize, additionalItemsSize, attendanceItemSize);
                        let datas = [];
                        let tempSelected: Array<any> = [];
                        let tempSelectedCode: Array<any> = [];

                        
                        self.scheduleTableOutputSetting().isEnableCode(false);
                        self.isEnableAddBtn(data.shiftBackgroundColor == 0);
                        if(self.isShiftBackgroundColor() != (data.shiftBackgroundColor ==1)){                               
                            self.isReload(true);            
                            self.isShiftBackgroundColor(data.shiftBackgroundColor == 1);
                        }                        
                        self.isEnableAttendanceItem(!self.isShiftBackgroundColor());
    
                        datas.push(new ScreenItem(true, self.countNumberRow, data.personalInfo[0] != null ? data.personalInfo[0].toString() : null,
                            data.additionalInfo[0] != null ? data.additionalInfo[0].toString() : null,
                            data.attendanceItem[0] != null ? data.attendanceItem[0].toString() : null));
                        for (let i = 1; i < maxLength; i++) {
                            self.countNumberRow = self.countNumberRow + 1;
                            if(self.countNumberRow >= 10) {
                                self.isEnableAddBtn(false);
                            }
                            datas.push(new ScreenItem(false, self.countNumberRow, data.personalInfo[i] != null ? data.personalInfo[i].toString() : null,
                                data.additionalInfo[i] != null ? data.additionalInfo[i].toString() : null,
                                data.attendanceItem[i] != null ? data.attendanceItem[i].toString() : null));                                    
    
                        }
                        self.itemList(datas);
                        self.itemsSwap.removeAll();
                        self.currentCodeListSwap.removeAll();
                        self.itemsSwap((_.cloneDeep(self.workplaceCounterCategories())));

                        _.each(data.workplaceCounterCategories, code => {
                            _.each(self.itemsSwap(), y => {
                                if (y.value == code) {
                                    tempSelected.push(new SwapModel(y.value, y.name));
                                    tempSelectedCode.push(y.value);
                                }
                            })
                        });

                        self.scheduleTableOutputSetting().updateData(data);
                        self.currentCodeListSwap(tempSelected);
                        self.selectedCodeListSwap(tempSelectedCode);
                        self.persons(self.personalCounterCategory());
                        self.selectedPerson(data.personalCounterCategories);
                        self.isEditing(true);
                        self.enableDelete(true);
                        $('#outputSettingName').focus();
                    }
                }).always(() => {
                    self.$blockui("hide");
                })
            }            
        }

        loadDetail(code?: string, checkAddInfo?: boolean , checkShiftBgColor?: boolean): void {
            const self = this;
            self.checkAll(false);
            if (code != null && !_.isEmpty(code)) {
                self.$blockui("invisible");
                self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CODE + "/" + code).done((data: IScheduleTableOutputSetting) => {
                    self.countNumberRow = 1;
                    if (!_.isNull(data) && !_.isEmpty(data)) {
                        self.clearError();                    
                        
                        let personalInfoItemsSize = data.personalInfo.length;
                        let additionalItemsSize = data.attendanceItem.length;
                        let attendanceItemSize = data.attendanceItem.length;
                        let maxLength = Math.max(personalInfoItemsSize, additionalItemsSize, attendanceItemSize);
                        let datas = [];
                        let tempSelected: Array<any> = [];
                        let tempSelectedCode: Array<any> = [];


                        self.scheduleTableOutputSetting().updateData(data);
                        self.scheduleTableOutputSetting().isEnableCode(false);
                        self.isEnableAddBtn(checkAddInfo);
                        self.isShiftBackgroundColor(checkShiftBgColor);

                        datas.push(new ScreenItem(true, self.countNumberRow, data.personalInfo[0] != null ? data.personalInfo[0].toString() : null,
                            data.additionalInfo[0] != null ? data.additionalInfo[0].toString() : null,
                            data.attendanceItem[0] != null ? data.attendanceItem[0].toString() : null));
                                                    
                        self.itemList(datas);
                        self.itemsSwap.removeAll();
                        self.currentCodeListSwap.removeAll();
                        self.itemsSwap((_.cloneDeep(self.workplaceCounterCategories())));

                        _.each(data.workplaceCounterCategories, code => {
                            _.each(self.itemsSwap(), y => {
                                if (y.value == code) {
                                    tempSelected.push(new SwapModel(y.value, y.name));
                                    tempSelectedCode.push(y.value);
                                }
                            })
                        });

                        self.currentCodeListSwap(tempSelected);
                        self.selectedCodeListSwap(tempSelectedCode);
                        self.persons(self.personalCounterCategory());
                        self.selectedPerson(data.personalCounterCategories);
                        self.isEditing(true);
                        self.enableDelete(true);
                        $('#outputSettingName').focus();
                    }
                }).always(() => {
                    self.$blockui("hide");
                })
            }
        }
        initialData(): void {
            const self = this;
            let data = [];
            self.countNumberRow = 1;
            data.push(new ScreenItem(true, self.countNumberRow, self.personalInfoItems()[1].value, self.attendanceItems()[1].value, self.attendanceItems()[1].value));
            self.itemList.removeAll();
            self.itemList (data);

            self.itemsSwap.removeAll();
            self.itemsSwap((_.cloneDeep(self.workplaceCounterCategories())));
            self.currentCodeListSwap([]);

            self.persons(self.personalCounterCategory());
            self.selectedPerson([]);
        }

        public registerOrUpdate(): void {        
            const self = this;            
            if (self.validateAll()) {
                return;
            }
            if(self.condition1()) {
                self.$dialog.error({messageId: 'Msg_1130'});
                return;                                
            }

            if(self.condition2()) {
                self.$dialog.error({messageId: 'Msg_1972'});
                return;                                
            }

            if(self.condition3()) {
                self.$dialog.error({messageId: 'Msg_1973'});
                return;                                
            }

            self.$blockui("invisible");
            let personalInfo: Array<number> = [],
                additionalInfo: Array<number> = [],
                attendanceItem: Array<number> = [],
                personalCounterCategories: Array<number> = [],
                workplaceCounterCategories: Array<number> = [];

            let command:any  = {
                code: self.scheduleTableOutputSetting().code(),
                name: self.scheduleTableOutputSetting().name(),
                additionalColumn: self.scheduleTableOutputSetting().additionalColumn(),
                shiftBackgroundColor: self.isShiftBackgroundColor() ? 1 : 0,
                dailyDataDisplay: self.scheduleTableOutputSetting().dailyDataDisplay(),
                workplaceCounterCategories: self.selectedCodeListSwap(),
            }
            _.each(self.currentCodeListSwap(), x => {
                workplaceCounterCategories.push(Math.floor(x.value));
            });   
            
            if(self.selectedPerson().length > 0 && self.selectedPerson() != -1){
                personalCounterCategories.push(Math.floor(self.selectedPerson()));
            }


            _.each(self.itemList(), x => {                
                    personalInfo.push(parseInt(x.personalInfo()));
                    additionalInfo.push(parseInt(x.additionInfo()));
                    attendanceItem.push(parseInt(x.attendanceItem()));
            });

            command.personalCounterCategories = personalCounterCategories;
            command.workplaceCounterCategories = workplaceCounterCategories;
            command.personalInfo = personalInfo;
            command.additionalInfo = additionalInfo;
            command.attendanceItem = attendanceItem;
            if(!self.isEditing()){
                self.$ajax(Paths.REGISTER_SCHEDULE_TABLE_OUTPUT_SETTING, command).done(() =>{
                    self.$dialog.info({messageId: 'Msg_15'}).then(() => {
                        self.selectedCode(command.code);
                        self.reloadData(command.code);                        
                    });  
                }).fail((res) => {
                    if(res.messageId == 'Msg_3' || res.messageId == 'Msg_1971'){
                        self.selectedCode('');
                        $('#outputSettingCode').ntsError('set',{messageId: res.messageId});
                    }
                }).always(() =>{
                    self.$blockui("hide");
                });
            } else {              
                self.$ajax(Paths.UPDATE_SCHEDULE_TABLE_OUTPUT_SETTING, command).done(() =>{
                    self.selectedCode(command.code);
                    if(!command.shiftBackgroundColor || command.shiftBackgroundColor == self.scheduleTableOutputSetting().shiftBackgroundColor()){
                        self.reloadData(command.code);
                    } else {
                        self.loadDetail(command.code, command.additionalColumn, self.isShiftBackgroundColor());
                    } 
                    self.$dialog.info({messageId: "Msg_15"}).then(function() {
                        $('#outputSettingName').focus();
                    }); 
                }).fail((res) => {
                    self.$dialog.alert({messageId: res.messageId});
                }).always(() =>{
                    self.$blockui("hide");
                });
            }           
            self.$blockui("hide");
        }

        public remove(): void {
            const self = this;
            self.$dialog.confirm({messageId: "Msg_18"}).then((result: 'no' | 'yes') =>{
                self.$blockui("invisible");
                let command: any = {
                    code: self.scheduleTableOutputSetting().code()
                }
                
                if(result === 'yes'){
                    self.$ajax(Paths.DELETE_SCHEDULE_TABLE_OUTPUT_SETTING, command).done(() =>{
                        self.$dialog.info({messageId: "Msg_16"}).then(() =>{
                            if(self.items().length == 1) {
                                self.items([]);
                                self.selectedCode('');
                                $('#outputSettingCode').focus();
                            } else {
                                let indexSelected: number;
                                for(let index = 0; index < self.items().length; index++){
                                    if(self.items()[index].code == self.selectedCode()) {
                                        indexSelected = (index == self.items().length - 1)? index -1: index;
                                        self.items.splice(index, 1);
                                        break;
                                    }
                                }
                                self.enableDelete(true);
                                self.selectedCode(self.items()[indexSelected].code);
                                $('#outputSettingName').focus();
                            }
                        });
                    }).always(() =>{
                        self.$blockui("hide");
                    });    
                    self.$blockui("hide");                
                }
                if(result === 'no'){
                    self.$blockui("hide");
                    $('#outputSettingName').focus();
                }
            });            
        }

        loadData(): void {
            const self = this;
            let dataList: Array<ItemModel> = [];
            let code = getShared('dataShareKSU005a');
            let newCode = getShared('dataShareKSU005c');
            self.$blockui("invisible");
            self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID).done((data: Array<IScheduleTableOutputSetting>) => {
                if(data && data.length > 0){
                    _.each(data, item =>{
                        if(item.code){
                            dataList.push(new ItemModel(item.code, item.name));
                        }                        
                    });            
                    self.items(_.sortBy(dataList, item => item.code));
                    newCode ? self.selectedCode(newCode) : self.selectedCode(code);       
                } else {
                    self.clearData();
                }
                
            }).always(() => {
                self.$blockui("hide");
            });
        }

        reloadData(code: string): void {
            const self = this;
            let dataList: Array<ItemModel> = [];
            self.$blockui("invisible");
            self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID).done((data: Array<IScheduleTableOutputSetting>) => {
                if(data && data.length > 0){
                    _.each(data, item =>{
                        dataList.push(new ItemModel(item.code, item.name));
                    });            
                    self.items(_.sortBy(dataList, item => item.code));
                    self.selectedCode(code);       
                } else {
                    self.clearData();
                }                
                $('#outputSettingName').focus();
            }).always(() => {
                self.$blockui("hide");
            });
        }

        addItem(){
            let self = this;
            self.countNumberRow = self.countNumberRow + 1;
            if(self.countNumberRow >= 10) {
                self.isEnableAddBtn(false);
            }
            self.itemList.push(new ScreenItem(false, self.countNumberRow));    
        }
        
        removeItem(){
            let self = this;
            let evens = _.remove(self.itemList(), function(n) {
                return !n.checked();
              });
              for (let i = 0; i < evens.length; i++) {
                evens[i].rowNo(i + 1);
              }
            self.countNumberRow = evens.length;
            self.itemList(evens);    
            self.checkAll(false);
            self.isEnableDelBtn(false);
            self.isEnableAddBtn(true);
        }

        clearData(): void {
            let self = this;
            self.selectedCode("");         
            self.scheduleTableOutputSetting().resetData();  
            self.isEditing(false);
            self.enableDelete(false);
            self.isEnableDelBtn(false);
            self.isEnableAddBtn(true);
            self.isEnableAttendanceItem(true);
            self.isShiftBackgroundColor(false);
            self.initialData();
            self.clearError();
            self.checkAll(false);
            $('#outputSettingCode').focus();              
        }

        private validateAll(): boolean {
            $('#outputSettingCode').ntsEditor('validate');
            $('#outputSettingName').ntsEditor('validate');         
            if (nts.uk.ui.errors.hasError()) {                    
                return true;
            }
            return false;
        }

        private condition1(): boolean {
            const self = this;
            let count: number = 0; 
            if(self.itemList().length > 1){
                _.each(self.itemList(), x => {
                    if (parseInt(x.personalInfo()) == -1 && parseInt(x.additionInfo()) == -1 && parseInt(x.attendanceItem()) == -1) {
                        count = count + 1;
                    }               
                });
            }
            
            if(count >= 1){
                return true;
            }
            return false;
        }

        private condition2(): boolean {
            const self = this;
            let count: number = 0; 
            // for (let i = 0; i < self.itemList().length - 1; i++) {
            //     for (let j = i + 1; j < self.itemList().length; j++) {
            //         if (self.itemList()[i].personalInfo() == self.itemList()[j].personalInfo() 
            //             && self.itemList()[i].additionInfo() == self.itemList()[j].additionInfo()) {
            //             count = count + 1;
            //         }
            //     }
            // }           

            _.each(self.itemList(), x => {
                if (parseInt(x.personalInfo()) == parseInt(x.additionInfo())) {
                    count = count + 1;
                }               
            });
            if(count == self.itemList().length){
                return true;
            }
            return false;
        }

        
        private condition3(): boolean {
            const self = this;
            let count: number = 0; 
            for (let i = 0; i < self.itemList().length - 1; i++) {
                for (let j = i + 1; j < self.itemList().length; j++) {
                    if (self.itemList()[i].attendanceItem() == self.itemList()[j].attendanceItem() && parseInt(self.itemList()[i].attendanceItem()) != -1 ) {
                        count = count + 1;
                    }
                }
            }           
            if(count >= 1){
                return true;
            }
            return false;
        }

        clearError(): void {
            $('#outputSettingCode').ntsError('clear');
            $('#outputSettingName').ntsError('clear');
        }

        closeDialog(): void {
            const self = this;
            let request: any = {
                code: self.selectedCode(),
                name: self.scheduleTableOutputSetting().name()
            };
            setShare('dataShareCloseKSU005b', request);
            self.$window.close();            
        }

        openDialog(): void {
            const self = this;		
            let request: any = {};
            request.copySourceCode = self.scheduleTableOutputSetting().code();
            request.copySourceName = self.scheduleTableOutputSetting().name();

            setShare('dataShareKSU005b', request);
            self.currentScreen = nts.uk.ui.windows.sub.modal('/view/ksu/005/c/index.xhtml').onClosed(() =>{
                let newCode = getShared('dataShareKSU005c');
                newCode ? self.reloadData(newCode) : self.reloadData(self.scheduleTableOutputSetting().code());
            });
        }

        mounted(){
            $("#swap-list-grid2_name").html(getText('KSU005_45'));
        }
    }

    class ScreenItem {
        rowNo: KnockoutObservable<number>;
        checked: KnockoutObservable<boolean>;
        
        personalInfos: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        attendanceItems: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        selectedCode: KnockoutObservable<string>;
        isNumberOne: KnockoutObservable<boolean> = ko.observable(false);
        personalInfo: KnockoutObservable<string> = ko.observable("-1");
        additionInfo: KnockoutObservable<string> = ko.observable("-1");
        attendanceItem: KnockoutObservable<string> = ko.observable("-1");
        
        constructor(isNumberOne: any, rowNo: number, personalInfo?: string, additionInfo?: string, attendanceItem?: string) {
            var self = this;
            self.rowNo = ko.observable(rowNo);
            self.checked = ko.observable(false);
            self.checked.subscribe((value)=> {
                if (isCheckAll) return;
                if (checkbox()) {
                    checkbox(false);
                } else if (!checkbox()) {
                    checkbox(true);
                }
            });
            self.isNumberOne = ko.observable(isNumberOne);            
            self.selectedCode = ko.observable('1');             
            if(personalInfo != null) {            
                self.personalInfo = ko.observable(personalInfo);
            }
            if(additionInfo != null) {                
                self.additionInfo = ko.observable(additionInfo);
            }
            if(attendanceItem != null) {
                self.attendanceItem = ko.observable(attendanceItem);
            }            
        }
    }
    class ItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
    
    class SwapModel {
        value: string;
        name: string;

        constructor(code: string, name: string) {
            this.value = code;
            this.name = name;
        }
    }

    interface IScheduleTableOutputSetting {
        code: string;
        name: string;
        additionalColumn: number;
        shiftBackgroundColor: number;
        dailyDataDisplay: number;
        personalInfo: Array<number>;
        additionalInfo: Array<number>;
        attendanceItem: Array<number>;

        personalInfoItems: Array<ItemModel>;
        additionalItems: Array<ItemModel>;
        workplaceCounterCategories: Array<number>;
        personalCounterCategories: Array<number>;
 
    }
    class ScheduleTableOutputSetting {
        code: KnockoutObservable<string> = ko.observable('');
        name: KnockoutObservable<string> = ko.observable('');
        additionalColumn: KnockoutObservable<number> = ko.observable();
        shiftBackgroundColor: KnockoutObservable<number> = ko.observable();
        dailyDataDisplay: KnockoutObservable<number> = ko.observable();
        personalInfo: KnockoutObservableArray<number> = ko.observableArray([]);
        additionalInfo: KnockoutObservableArray<number> = ko.observableArray([]);
        attendanceItem: KnockoutObservableArray<number> = ko.observableArray([]);

        personalInfoItems: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        additionalItems: KnockoutObservableArray<ItemModel> = ko.observableArray([]);

        workplaceCounterCategories: KnockoutObservableArray<number> = ko.observableArray([]);
        personalCounterCtegories: KnockoutObservableArray<number> = ko.observableArray([]);
        listWkpCounterCategories : KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.WorkplaceCounterCategory);
       
        isEnableCode: KnockoutObservable<boolean> = ko.observable(false);
        constructor(params?: IScheduleTableOutputSetting) {
            const self = this;
            if(params){
                self.code(params.code);
                self.name(params.name);
                self.additionalColumn(params.additionalColumn);
                self.shiftBackgroundColor(params.shiftBackgroundColor);
                self.dailyDataDisplay(params.dailyDataDisplay);
                self.personalInfo(params.personalInfo);
                self.additionalInfo(params.additionalInfo);
                self.attendanceItem(params.attendanceItem);
                self.additionalItems(params.additionalItems);
                self.personalInfoItems(params.personalInfoItems);
                self.workplaceCounterCategories(params.workplaceCounterCategories);
                self.personalCounterCtegories(params.personalInfo);
               
            } else {
                self.code('');
                self.name('');
                self.additionalColumn(1);
                self.shiftBackgroundColor(0);
                self.dailyDataDisplay(0);
                self.personalInfo([]);
                self.additionalInfo([]);
                self.attendanceItem([]);
                self.additionalItems([]);
                self.personalInfoItems([]);
                self.workplaceCounterCategories([]);
                self.personalCounterCtegories([]);
            }
        }

        public resetData() {
            let self = this;
            self.code("");
            self.name("");
            self.additionalColumn(1);
            self.additionalInfo();
            self.attendanceItem();
            self.dailyDataDisplay(0);
            self.shiftBackgroundColor(0);
            self.personalInfo();
            self.personalCounterCtegories();
            self.workplaceCounterCategories();
            self.isEnableCode(true);
        }
        public updateData(param: IScheduleTableOutputSetting){
            const self = this;
            self.code(param.code);
            self.name(param.name);
            self.additionalColumn(param.additionalColumn);
            self.additionalInfo(param.additionalInfo);
            self.attendanceItem(param.attendanceItem);
            self.dailyDataDisplay(param.dailyDataDisplay);
            self.shiftBackgroundColor(param.shiftBackgroundColor);
            self.personalInfo(param.personalInfo);
            self.personalCounterCtegories(param.personalCounterCategories);
            self.workplaceCounterCategories(param.workplaceCounterCategories);
        } 
    }
}