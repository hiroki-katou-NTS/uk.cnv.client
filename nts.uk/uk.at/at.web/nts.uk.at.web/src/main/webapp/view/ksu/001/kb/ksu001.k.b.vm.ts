module nts.uk.at.view.ksu001.k.b {
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
        columns: KnockoutObservableArray<any>;
        columns3: KnockoutObservableArray<any>;
        selectedCode: KnockoutObservable<string>= ko.observable('');
        selectedPerson: KnockoutObservable<any> = ko.observable();
        tabs: KnockoutObservableArray<any>;
        selectedTab: KnockoutObservable<string>;
        addSelected: any;        
        lstAddColInfo: KnockoutObservableArray<any>;
        isEditing: KnockoutObservable<boolean> = ko.observable(false);
        enableDelete: KnockoutObservable<boolean> = ko.observable(true);

        itemsSwap: KnockoutObservableArray<any> = ko.observableArray([]);
        currentCodeListSwap: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedCodeListSwap: KnockoutObservableArray<any> = ko.observableArray([]);

        lstWorkInfo: KnockoutObservableArray<any>;
        workSelected: any;
        checked: KnockoutObservable<boolean>;
        displayShiftBackgroundColor: KnockoutObservable<number> = ko.observable(0);

        itemList: KnockoutObservableArray<ScreenItem> = ko.observableArray([]);

        countNumberRow: number = 1;
        checkAll: KnockoutObservable<boolean> = ko.observable(false); 
        checkOne: KnockoutObservable<boolean> = ko.observable(false);

        personalInfoItems: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.ScheduleTablePersonalInfoItem.filter(i => i.value != 0).map(i => ({value: i.value, name: this.$i18n(i.name)})));
	    attendanceItems: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.ScheduleTableAttendanceItem.map(i => ({value: i.value, name: this.$i18n(i.name)})));
        workplaceCounterCategories: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.WorkplaceCounterCategory.map(i => ({value: i.value, name: this.$i18n(i.name)})));
        personalCounterCategory: KnockoutObservableArray<any> =  ko.observableArray(__viewContext.enums.PersonalCounterCategory.map(i => ({value: i.value, name: this.$i18n(i.name)})));

        isEnableAddBtn: KnockoutComputed<boolean>;
        isEnableDelBtn :KnockoutObservable<boolean> = ko.observable(false);
        isEnableAdditionInfo :KnockoutObservable<boolean> = ko.observable(true);
        // isEnableAttendanceItem :KnockoutObservable<boolean> = ko.observable(true);
        isEnableDisplayShiftBackgroundColor: KnockoutComputed<boolean>;
        scheduleTableOutputSetting: KnockoutObservable<ScheduleTableOutputSetting> = ko.observable(new ScheduleTableOutputSetting());        
        isReload :KnockoutObservable<boolean> = ko.observable(false);
        exitStatus: KnockoutObservable<string> = ko.observable("Cancel"); 

        constructor(code: string) {
            super();
            const self = this;                    
            self.personalInfoItems.splice(0, 0, {value: -1 , name:getText('KSU005_68')});
            self.attendanceItems.splice(0, 0, {value: -1 , name:getText('KSU005_68')});
            self.personalCounterCategory.splice(0, 0, {value: -1 , name:getText('KSU005_68')});

            self.columns = ko.observableArray([
                { headerText: getText('KSU001_4093'), key: 'code', width: 60 },
                { headerText: getText('KSU001_4094'), key: 'name', width: 150}
            ]);
 
            self.columns3 = ko.observableArray([     
                { headerText: "", key: 'value', width: 0, hidden: true},
                { headerText: getText('KSU001_4122'), key: 'name', width: 150}
            ]);

            self.tabs = ko.observableArray([
                {id: 'tab-1', title: getText('KSU001_4098'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true)},
                {id: 'tab-2', title: getText('KSU001_4099'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true)},
                {id: 'tab-3', title: getText('KSU001_4100'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true)}
            ]);

            self.selectedTab = ko.observable('tab-1');

            self.lstAddColInfo = ko.observableArray([
                { code: 1, name: getText('KSU001_4102') },
                { code: 0, name: getText('KSU001_4103') }
            ]);

            self.addSelected = ko.observable(0);

            self.checked = ko.observable(false);

            self.lstWorkInfo = ko.observableArray([
                { code: 0, name: getText('KSU001_4142') },
                { code: 1, name: getText('KSU001_4143') }
            ]);
            self.workSelected = ko.observable(0);

            $("#fixed-table").ntsFixedTable({ height: 196 });

            self.scheduleTableOutputSetting().shiftBackgroundColor.subscribe((value)=>{
                self.displayShiftBackgroundColor(value);
            });
            // self.displayShiftBackgroundColor.subscribe((value) => {
            //     if(self.isReload()) {
            //         self.isReload(false);
            //         return;
            //     } else {
            //         if (value == 1) {
            //             self.loadDetail(self.selectedCode(), self.scheduleTableOutputSetting().additionalColumn() == 1, true);
            //             self.isEnableDelBtn(false);
            //             // self.isEnableAttendanceItem(false);
            //         } else {
            //             self.loadDetail(self.selectedCode(), self.scheduleTableOutputSetting().additionalColumn() == 1, false);
            //             // self.isEnableAttendanceItem(true);
            //         }
            //     }
            // });

            self.checkAll.subscribe((value) => {     
                isCheckAll = true;           
                let temp = self.itemList();
                if (value) {
                    for (let i = 1; i < temp.length; i++) {
                        temp[i].checked(true);
                    }      
                    self.itemList().length > 1 ? self.isEnableDelBtn(true) : self.isEnableDelBtn(false);
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
                self.selectedTab('tab-1');
                if (_.isEmpty(code)) {
                    self.clearData();
                } else {
                    self.findDetail(code);
                    self.isEnableDelBtn(false);
                }
                self.$blockui("hide");
            });

            self.isEnableAddBtn = ko.computed(() => {
                return self.itemList().length < 10;
            });
            self.isEnableDisplayShiftBackgroundColor = ko.computed(() => {
                const rowOne: ScreenItem = _.find(self.itemList(), (i: ScreenItem) => i.attendanceItem() == "0");
                return !!rowOne;
            });
            self.loadData(code);
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
                        let personalInfoItemsSize = _.size(data.personalInfo);
                        let additionalItemsSize = _.size(data.attendanceItem);
                        let attendanceItemSize = _.size(data.attendanceItem);
                        let maxLength = Math.max(personalInfoItemsSize, additionalItemsSize, attendanceItemSize);
                        let datas = [];
                        let tempSelected: Array<any> = [];
                        let tempSelectedCode: Array<any> = [];

                        
                        self.scheduleTableOutputSetting().isEnableCode(false);
                        if(self.displayShiftBackgroundColor() != data.shiftBackgroundColor){                               
                            self.isReload(true);            
                            self.displayShiftBackgroundColor(data.shiftBackgroundColor);
                        }                        
                        // self.isEnableAttendanceItem(self.displayShiftBackgroundColor() == 0);
    
                        datas.push(new ScreenItem(true, self.countNumberRow, data.personalInfo[0] != null ? data.personalInfo[0].toString() : __viewContext.enums.ScheduleTablePersonalInfoItem[0].value,
                            data.additionalInfo[0] != null ? data.additionalInfo[0].toString() : null,
                            data.attendanceItem[0] != null ? data.attendanceItem[0].toString() : null));
                        for (let i = 1; i < maxLength; i++) {
                            self.countNumberRow = self.countNumberRow + 1;
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
                        self.selectedPerson(_.isEmpty(data.personalCounterCategories) ? -1 : data.personalCounterCategories );
                        self.isEditing(true);
                        self.enableDelete(true);
                        $('#outputSettingName').focus();
                    }
                }).always(() => {
                    self.$blockui("hide");
                })                
            }            
        }

        // loadDetail(code?: string, checkAddInfo?: boolean, checkShiftBgColor?: boolean): void {
        //     const self = this;
        //     self.checkAll(false);
        //     self.countNumberRow = 1;
        //     self.clearError();
        //     let datas: Array<any> = [];
        //
        //     // if(checkShiftBgColor) {
        //     //     self.isEnableDelBtn(false);
        //     // }
        //     checkAddInfo ? self.isEnableAdditionInfo(true) : self.isEnableAdditionInfo(false);
        //
        //     datas.push(new ScreenItem(true, self.countNumberRow,
        //                         __viewContext.enums.ScheduleTablePersonalInfoItem[0].value,
        //                         self.itemList()[0].additionInfo(),
        //                         self.attendanceItems()[0].value));
        //
        //     self.itemList(datas);
        //     $('#outputSettingName').focus();
        // }
       
        initialData(): void {
            const self = this;
            let data = [];
            self.countNumberRow = 1;
            data.push(new ScreenItem(true, self.countNumberRow, __viewContext.enums.ScheduleTablePersonalInfoItem[0].value, self.attendanceItems()[0].value, self.attendanceItems()[0].value));
            self.itemList.removeAll();
            self.itemList (data);

            self.selectedTab('tab-1');

            self.itemsSwap.removeAll();
            self.itemsSwap((_.cloneDeep(self.workplaceCounterCategories())));
            self.currentCodeListSwap([]);

            self.persons(self.personalCounterCategory());
            self.selectedPerson([-1]);
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

            if (self.condition4()) {
                self.$dialog.error({messageId: 'Msg_2209'});
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
                shiftBackgroundColor: self.displayShiftBackgroundColor(),
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
                        self.exitStatus('Update');               
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
                    // self.selectedCode(command.code);
                    // if(!command.shiftBackgroundColor || command.shiftBackgroundColor == self.scheduleTableOutputSetting().shiftBackgroundColor()){
                    //     self.reloadData(command.code);
                    // } else {
                    //     self.loadDetail(command.code, command.additionalColumn == 1, self.displayShiftBackgroundColor() == 1);
                    // }
                    // self.$dialog.info({messageId: "Msg_15"}).then(function() {
                    //     $('#outputSettingName').focus();
                    // });
                    // self.exitStatus('Update');
                    self.$dialog.info({messageId: 'Msg_15'}).then(() => {
                        self.selectedCode(command.code);
                        self.reloadData(command.code);
                        self.exitStatus('Update');
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
                        self.exitStatus('Update');  
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

        loadData(code: string): void {
            const self = this;
            let dataList: Array<ItemModel> = [];
            self.$blockui("invisible");
            self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID).done((data: Array<IScheduleTableOutputSetting>) => {
                if(data && data.length > 0){
                    _.each(data, item =>{
                        if(item.code){
                            dataList.push(new ItemModel(item.code, item.name));
                        }                        
                    });            
                    self.items(_.sortBy(dataList, item => item.code));
                    if (code) {
                        self.selectedCode(code);
                    } else {
                        if (self.items().length > 0) self.selectedCode(self.items()[0].code);
                        else self.clearData();
                    }
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
            let item: ScreenItem = new ScreenItem(false, self.countNumberRow);
            if(self.checkAll()){
                item.checked(true);
                self.checkOne(false);
                self.isEnableDelBtn(true);
            } 
            self.itemList.push(item);
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
        }

        clearData(): void {
            let self = this;
            self.selectedCode("");         
            self.scheduleTableOutputSetting().resetData();  
            self.isEditing(false);
            self.enableDelete(false);
            self.isEnableDelBtn(false);
            // self.isEnableAttendanceItem(true);
            self.displayShiftBackgroundColor(0);
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
                    if(self.isEnableAdditionInfo()){
                        if (parseInt(x.personalInfo()) == -1 && parseInt(x.additionInfo()) == -1 && parseInt(x.attendanceItem()) == -1) {
                            count = count + 1;
                        }     
                    } else {
                        if (parseInt(x.personalInfo()) == -1 && parseInt(x.attendanceItem()) == -1) {
                            count = count + 1;
                        }   
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
            let data: any = [];

            if(self.isEnableAdditionInfo()){
                _.each(self.itemList(), x => {
                    if(parseInt(x.personalInfo()) != -1){
                        data.push(parseInt(x.personalInfo()));
                    }                
                    if(parseInt(x.additionInfo()) != -1){
                        data.push(parseInt(x.additionInfo()));
                    }  
                });
            } else {
                _.each(self.itemList(), x => {
                    if(parseInt(x.personalInfo()) != -1){
                        data.push(parseInt(x.personalInfo()));
                    } 
                });
            }

            for (let i = 0; i < data.length - 1; i++) {
                for (let j = i + 1; j < data.length; j++) {
                    if (data[i] == data[j]) {
                        count = count + 1;
                    }
                }
            }           
            if(count >= 1){
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

        private condition4(): boolean {
            const self = this;
            const rowShift: ScreenItem = _.find(self.itemList(), (i: ScreenItem) => i.attendanceItem() == '0');
            const rowWorkingTimeZone: ScreenItem = _.find(self.itemList(), (i: ScreenItem) => i.attendanceItem() == '2');
            if (rowShift && rowWorkingTimeZone) return true;
            return false;
        }

        clearError(): void {
            $('#outputSettingCode').ntsError('clear');
            $('#outputSettingName').ntsError('clear');
        }

        closeDialog(): void {
            const self = this;
            self.$window.close(self.exitStatus() === 'Update' ? {code: self.selectedCode(), name: self.scheduleTableOutputSetting().name()} : null);
        }

        openDialog(): void {
            const self = this;		
            let request: any = {};
            request.copySourceCode = self.scheduleTableOutputSetting().code();
            request.copySourceName = self.scheduleTableOutputSetting().name();

            self.$window.modal('/view/ksu/001/kc/index.xhtml', request).then((newCode) => {
                if (newCode) {
                    self.reloadData(newCode);
                    self.exitStatus('Update');
                }
            });
        }
    }

    class ScreenItem {
        rowNo: KnockoutObservable<number>;
        checked: KnockoutObservable<boolean>;
        
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
        /** コード */
        code: KnockoutObservable<string> = ko.observable('');
        /** 名称ド */
        name: KnockoutObservable<string> = ko.observable('');
        /** 追加列情報 */
        additionalColumn: KnockoutObservable<number> = ko.observable();
        /** シフト表利用 */
        shiftBackgroundColor: KnockoutObservable<number> = ko.observable();
        /** 勤務情報 */
        dailyDataDisplay: KnockoutObservable<number> = ko.observable();
        /** 個人情報 */
        personalInfo: KnockoutObservableArray<number> = ko.observableArray([]);
        /** 追加列情報_1 */
        additionalInfo: KnockoutObservableArray<number> = ko.observableArray([]);
        /** 表示項目_1 */
        attendanceItem: KnockoutObservableArray<number> = ko.observableArray([]);

        personalInfoItems: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        additionalItems: KnockoutObservableArray<ItemModel> = ko.observableArray([]);

        /** 職場計出力設定名称 */
        workplaceCounterCategories: KnockoutObservableArray<number> = ko.observableArray([]);
        /** 個人計出力設定名称 */
        personalCounterCtegories: KnockoutObservableArray<number> = ko.observableArray([]);
       
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