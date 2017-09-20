module nts.uk.at.view.kmf004.e.viewmodel {
    
    export class ScreenModel {
        // radio box
        itemList: KnockoutObservableArray<any>;
        // selected radio box
        selectedId: KnockoutObservable<number>;
        display: KnockoutObservable<boolean>;
        // list business type A2_2
        lstPer: KnockoutObservableArray<Per>;
        // column in list
        gridListColumns: KnockoutObservableArray<any>;
        // selected code 
        selectedCode: KnockoutObservable<string>;
        // selected item
        selectedOption: KnockoutObservable<Per>;
        // binding to text box name A3_3
        selectedName: KnockoutObservable<string>;
        // binding to text box code A3_2
        codeObject: KnockoutObservable<string>;
        // check new mode or not
        check: KnockoutObservable<boolean>;
        // check update or insert
        checkUpdate: KnockoutObservable<boolean>;
        // year month date
        items: KnockoutObservableArray<Item>;
        constructor() {
            let self = this;
            self.itemList = ko.observableArray([
                    new BoxModel(0, nts.uk.resource.getText("KMF004_95")),
                    new BoxModel(1, nts.uk.resource.getText("KMF004_96"))
                ]);
            self.gridListColumns = ko.observableArray([
                { headerText: nts.uk.resource.getText("KMF004_7"), key: 'yearServiceCode', width: 100 },
                { headerText: nts.uk.resource.getText("KMF004_8"), key: 'yearServiceName', width: 200, formatter: _.escape}
            ]);
            self.selectedId = ko.observable(0);
            self.lstPer = ko.observableArray([]);
            self.selectedCode = ko.observable("");
            self.selectedName = ko.observable("");
            self.selectedOption = ko.observable(null);
            self.check = ko.observable(false);
            self.codeObject = ko.observable("");
            self.checkUpdate = ko.observable(true);
            self.display = ko.observable(false);
            self.items = ko.observableArray([]);
//            self.selectedId.subscribe((value) => {
//                    if (value == 0) {
//                        self.display(false);
//                    }
//                    else {
//                        self.display(true);
//                    }
//                });
            self.selectedCode.subscribe((code) => {   
                if (code) {
                    let foundItem: Per = _.find(self.lstPer(), (item: Per) => {
                        return (ko.toJS(item.yearServiceCode) == code);
                    });
                    self.checkUpdate(true);
                    self.selectedOption(foundItem);
                    self.selectedId(self.selectedOption().yearServiceCls);
                    self.selectedOption().yearServicePerSets;
                    self.items([]);
                    _.forEach(self.selectedOption().yearServicePerSets, o => {
                        self.items.push(ko.mapping.fromJS(o));    
                    });
                    for (let i = 0; i < 20; i++) {
                        if(self.items()[i] == undefined){
                            let t : item = {
                            yearServiceNo: ko.mapping.fromJS(i),
                            month: ko.mapping.fromJS(null),
                            year: ko.mapping.fromJS(null),
                            date: ko.mapping.fromJS(null)
                            }
                            self.items.push(new Item(t));
                        }
                    }
                    self.selectedName(self.selectedOption().yearServiceName);
                    self.codeObject(ko.toJS(self.selectedOption().yearServiceCode));
                    self.check(false);
                }
            });
        }

        /** get data to list **/
        getData(): JQueryPromise<any>{
            let self = this;  
            let dfd = $.Deferred();
            service.getAll().done((lstData: Array<viewmodel.Per>) => {
                if(lstData.length == 0){
                    self.check(true);
                }
                let sortedData = _.orderBy(lstData, ['yearServiceCode'], ['asc']);
                self.lstPer(sortedData);
                dfd.resolve();
            }).fail(function(error){
                    dfd.reject();
                    $('#inpCode').ntsError('set', error);
                });
              return dfd.promise();      
        }

        /** get data when start dialog **/
        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            service.getAll().done((lstData: Array<Per>) => {
                if(lstData.length == 0){
                    self.check(true);
                    self.codeObject(null);
                    self.selectedName(null);
                    self.checkUpdate(false);
                    self.items([]);
                    for (let i = 0; i < 20; i++) {
                        if(self.items()[i] == undefined){
                            let t : item = {
                            yearServiceNo: ko.mapping.fromJS(i),
                            month: ko.mapping.fromJS(null),
                            year: ko.mapping.fromJS(null),
                            date: ko.mapping.fromJS(null)
                            }
                            self.items.push(new Item(t));
                        }
                    }
                }else{
                    let sortedData : KnockoutObservableArray<any> = ko.observableArray([]);
                    sortedData(_.orderBy(lstData, ['yearServiceCode'], ['asc']));
                    self.lstPer(sortedData());
                    self.selectedOption(self.lstPer()[0]);
                    self.selectedId(self.selectedOption().yearServiceCls);
                    self.selectedCode(ko.toJS(self.lstPer()[0].yearServiceCode));
                    self.selectedName(self.lstPer()[0].yearServiceName);
                    self.codeObject(ko.toJS(self.lstPer()[0].yearServiceCode));
                    dfd.resolve();
                }
            }).fail(function(error) {
                dfd.reject();
                $('#inpCode').ntsError('set', error);
            });
            return dfd.promise();
        }  
        
        getListItems(){
            let self = this;
            let data = self.items();
            let lstReturn = _.filter(data, function(item){
                    return item.date() || item.month() || item.year();
                });
            for(let i = 0; i < lstReturn.length; i++){
                lstReturn[i].yearServiceNo(i);
            }  
            return lstReturn;
        }
        
        /** update or insert data when click button register **/
        register() {   
            let self = this;
            $("#inpCode").trigger("validate");
            $("inpPattern").trigger("validate");
            if (nts.uk.ui.errors.hasError()) {
                return;       
            }
            let code = "";    
            var items = _.filter(self.items(), function(tam: item) {
                    return tam.date() || tam.month() || tam.year();
                });
            let listSet = {
                specialHolidayCode: nts.uk.ui.windows.getShared('KMF004D_SPHD_CD'),
                lengthServiceYearAtr: self.selectedId(),
                yearServiceSets: ko.toJS(items)
            }
            let dataTransfer = {
                specialHolidayCode: nts.uk.ui.windows.getShared('KMF004D_SPHD_CD'),
                yearServiceCode: ko.toJS(self.codeObject()), 
                yearServiceName: self.selectedName(),
                yearServiceCls: self.selectedId(),
                yearServicePerSets: ko.toJS(self.getListItems()),   
            }
            
            code = self.codeObject();
            _.defer(() => {
                if (nts.uk.ui.errors.hasError() === false) {
                    // update item to list  
                    if(self.checkUpdate() == true){
                        service.update(dataTransfer).done(function(errors: Array<string>){
                            self.getData().done(function(){
                                self.selectedCode(code);    
                                if (errors && errors.length > 0) {
                                    self.addListError(errors);
                                }else{
                                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }); 
                                }
                            });
              
                        }).fail(function(res){
                            $('#inpCode').ntsError('set', res);
                            });
                    }
                    else{
                        code = self.codeObject();
                        self.selectedOption(null);
                        // insert item to list
                        service.add(dataTransfer).done(function(){
                            self.getData().done(function(){
                                nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                                self.selectedCode(code);  
                            });
                        }).fail(function(res){
                            $('#inpCode').ntsError('set', res);
                        });
                    }
                }
            });    
        } 
        
        //  new mode 
        newMode() {
//            var t0 = performance.now(); 
            let self = this;
            self.check(true);
            self.checkUpdate(false);
            self.selectedCode("");
            self.codeObject("");
            self.selectedName("");
            self.items([]);
            for (let i = 0; i < 20; i++) {
                    let t : item = {
                    yearServiceNo: ko.mapping.fromJS(i),
                    month: ko.mapping.fromJS(null),
                    year: ko.mapping.fromJS(null),
                    date: ko.mapping.fromJS(null)
                    }
                    self.items.push(new Item(t));
            }

//            $("#inpCode").focus(); 
//            $("#inpCode").ntsError('clear');
            nts.uk.ui.errors.clearAll();                 
//            var t1 = performance.now();
//            console.log("Selection process " + (t1 - t0) + " milliseconds.");
        }
        /** remove item from list **/
        remove() {
            let self = this;
            let count = 0;
            for (let i = 0; i <= self.lstPer().length; i++){
                if(ko.toJS(self.lstPer()[i].yearServiceCode) == self.selectedCode()){
                    count = i;
                    break;
                }
            }
            nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => { 
                service.remove(self.selectedOption()).done(function(){
                    self.getData().done(function(){
                        // if number of item from list after delete == 0 
                        if(self.lstPer().length==0){
                            self.newMode();
                            return;
                        }
                        // delete the last item
                        if(count == ((self.lstPer().length))){
                            self.selectedCode(ko.toJS(self.lstPer()[count-1].yearServiceCode));
                            return;
                        }
                        // delete the first item
                        if(count == 0 ){
                            self.selectedCode(ko.toJS(self.lstPer()[0].yearServiceCode));
                            return;
                        }
                        // delete item at mediate list 
                        else if(count > 0 && count < self.lstPer().length){
                            self.selectedCode(ko.toJS(self.lstPer()[count].yearServiceCode));    
                            return;
                        }
                    })
                })
                nts.uk.ui.dialog.info({ messageId: "Msg_16" });
            }).ifCancel(() => {     
            }); 
//            $("#inpPattern").focus();
        } 
        
        
        closeDialog(){
            var t0 = performance.now(); 
            nts.uk.ui.windows.close();
             var t1 = performance.now();
            console.log("Selection process " + (t1 - t0) + " milliseconds.");    
        }
        
        /**
             * Set error
             */
            addListError(errorsRequest: Array<string>) {
                var messages = {};
                _.forEach(errorsRequest, function(err) {
                    messages[err] = nts.uk.resource.getMessage(err);
                });
    
                var errorVm = {
                    messageId: errorsRequest,
                    messages: messages
                };
    
                nts.uk.ui.dialog.bundledErrors(errorVm);
            }
        
        
    } 
    export interface Per{
        specialHolidayCode: string
        yearServiceCode: string;
        yearServiceName: string;
        yearServiceCls: number;
        yearServicePerSets: Array<Item>;
    }
    
    export interface item{
        yearServiceNo: KnockoutObservable<number>;
        month: KnockoutObservable<number>;
        year: KnockoutObservable<number>;
        date: KnockoutObservable<number>;
    }
    
    
    export class Item {
            yearServiceNo: KnockoutObservable<number>;
            month: KnockoutObservable<number>;
            year: KnockoutObservable<number>;
            date: KnockoutObservable<number>;

            constructor(param: item) {
                var self = this;
                self.yearServiceNo = ko.observable(param.yearServiceNo());
                self.month = ko.observable(param.month());
                self.year = ko.observable(param.year());
                self.date = ko.observable(param.date());
            }
        }
    
    
    class BoxModel {
        id: number;
        name: string;
        constructor(id, name) {
            var self = this;
            self.id = id;
            self.name = name;
        }
    }
}




