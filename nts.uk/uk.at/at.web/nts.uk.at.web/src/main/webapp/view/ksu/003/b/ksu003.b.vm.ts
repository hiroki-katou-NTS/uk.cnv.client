module nts.uk.at.view.ksu003.b {
    import getText = nts.uk.resource.getText;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const Paths = {
        GET_ALL_TASK_PALETTE: "at/schedule/task/taskpalette/getAll",
        GET_TASK_PALETTE_ORGNIZATION: "at/schedule/task/taskpalette/findOne",
        REGISTER_TASK_PALETTE_ORGNIZATION: "at/schedule/task/taskpalette/register",
        DELETE_TASK_PALETTE_ORGNIZATION: "at/schedule/task/taskpalette/remove",
    };

    @bean()
    class Ksu003bViewModel extends ko.ViewModel {
        displayName: KnockoutObservable<string> = ko.observable('');
        selectedPage: KnockoutObservable<number> = ko.observable();
        groupName: KnockoutObservable<string> = ko.observable('');
        textButtonArr: KnockoutObservableArray<any> = ko.observableArray([
            { name: ko.observable(getText("KSU003_84", [1])), id: 0 },
            { name: ko.observable(getText("KSU003_84", [2])), id: 1 },
            { name: ko.observable(getText("KSU003_84", [3])), id: 2 },
            { name: ko.observable(getText("KSU003_84", [4])), id: 3 },
            { name: ko.observable(getText("KSU003_84", [5])), id: 4 }           
        ]);
        
        selectedLinkButton: KnockoutObservable<number> = ko.observable(1);
        contextMenu: Array<any>;
        textName: KnockoutObservable<string> = ko.observable(null);
        tooltip: KnockoutObservable<string> = ko.observable(null);
        dataWorkPairSet: KnockoutObservableArray<any> = ko.observableArray([]);
        tasks: KnockoutObservableArray<any> = ko.observableArray([]);        
        enableDelete: KnockoutObservable<boolean> = ko.observable(true);
        isEditing: KnockoutObservable<boolean> = ko.observable(false);

        targetId: KnockoutObservable<string> = ko.observable('');
        targetUnit: KnockoutObservable<number> = ko.observable();
        page: KnockoutObservable<number> = ko.observable();


        taskPaletteOrgnization: KnockoutObservable<TaskPaletteOrgnization> = ko.observable(new TaskPaletteOrgnization());       
        sourceEmpty: any[] = [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}];
        isClickLink: KnockoutObservable<boolean> = ko.observable(false);
        endStatus: KnockoutObservable<string> = ko.observable("Cancel"); 

		dataShareA : any = getShared("dataShareKsu003b");

        constructor() {
            super();
            const self = this;
            self.contextMenu = [
                { id: "openDialog", text: getText("作業選択"), action: self.openDialogKdl012.bind(self,event)},
                { id: "delete", text: getText("作業削除"), action: self.deleteTask.bind(self,event) }
            ];

            $("#task").ntsButtonTable("init", {row: 2, column: 5, source: self.tasks(), contextMenu: self.contextMenu, mode: "normal"});
            $("#task").bind("getdatabutton", function(evt, data) {
                self.textName(data.text);
                self.tooltip(data.tooltip);
                self.dataWorkPairSet(data.data);
            });
            $("#task").bind("getdatabutton1");

            self.selectedPage.subscribe((value) => {
                self.loadData();
                self.loadDetail(value);
                self.handleClickLinkButton(value - 1);
            });

            self.loadData();
        }

        resetTextBtn(): void {
            const self = this;_         
            for (let i = 1; i <= 5; i++) {
                self.textButtonArr()[i-1].name((getText("KSU003_84",[i])));
            }
        }
        loadData(): void {
            const self = this; 
            let request = self.dataShareA;        
            self.targetUnit(request.targetUnit);
            self.targetId(request.targetId)

            self.$blockui("invisible");
            self.resetTextBtn();
            self.$ajax(Paths.GET_ALL_TASK_PALETTE, request).done((data: Array<ITaskPalette>) => {
                if(data && data.length > 0){                    
                    for(let i=0; i< data.length; i++){
                        if(data[i].page != null) {
                            self.textButtonArr()[data[i].page - 1].name(nts.uk.text.padRight(data[i].name, ' ', 6));
                        }                        
                    }   
                    self.displayName(data[0].displayName);                                   
                }
                self.selectedPage(request.page);

            }).always(() => {
                self.$blockui("hide");
            });
            $('input#pageName').focus();
        }

        loadDetail(page?: number): void {
            const self = this;  
            let request = self.dataShareA;
            request.page = page;
            let dataSource: Array<any> = _.clone(self.sourceEmpty), idxExp: Array<number> = [], idxDel: Array<number> = [];
            self.$blockui("invisible");
            self.isEditing(false);
            self.enableDelete(false);
            self.$ajax(Paths.GET_TASK_PALETTE_ORGNIZATION, request).done((data: ITaskPaletteOrgnization) => {
                if (!_.isNull(data) && !_.isEmpty(data)) {
                    if (data.targetId != null) {
                        self.taskPaletteOrgnization().updateData(data);
                        self.isEditing(true);
                        self.enableDelete(true);
                    } else {
                        self.taskPaletteOrgnization().resetData();
                    }
                    if (!_.isNull(self.taskPaletteOrgnization().keys()) && !_.isEmpty(self.taskPaletteOrgnization().keys())) {
                        for (let i = 0; i < self.taskPaletteOrgnization().keys().length; i++) {
                            if(self.taskPaletteOrgnization().listTaskStatus()[i] == 0){
                                dataSource.splice(self.taskPaletteOrgnization().keys()[i] - 1, 1, {
                                    text: self.taskPaletteOrgnization().taskAbNames()[i], 
                                    tooltip: self.taskPaletteOrgnization().taskNames()[i] });
                            } else if(self.taskPaletteOrgnization().listTaskStatus()[i] == 1){
                                dataSource.splice(self.taskPaletteOrgnization().keys()[i] - 1, 1, {
                                    text: getText("KSU003_70"), 
                                    tooltip: ''});
                                    idxDel.push(self.taskPaletteOrgnization().keys()[i] - 1);
                            } else if(self.taskPaletteOrgnization().listTaskStatus()[i] == 2){
                                dataSource.splice(self.taskPaletteOrgnization().keys()[i] - 1, 1, {
                                    text: getText("KSU003_82"), 
                                    tooltip: ''});

                                    idxExp.push(self.taskPaletteOrgnization().keys()[i] - 1);                              
                            }                            
                        }                        
                    } else {
                        if(!self.isClickLink()){
                            self.selectedPage(1);
                        }                        
                        self.enableDelete(false);
                    }                   
                    self.tasks(dataSource);
                    if(idxExp && idxExp.length > 0) {
                        _.each(idxExp, idx => {
                            $($('#task button')[idx]).css("border","1px solid red");
                        });                       
                    }

                    if(idxDel && idxDel.length > 0) {
                        _.each(idxDel, idx => {
                            $($('#task button')[idx]).css("border","1px solid red");
                        });                       
                    }                   
                }
            }).always(() => {
                self.$blockui("hide");
            });
            $('input#pageName').focus();           
           
        }
        
        registerOrUpdate(): void {
            const self = this;
            if (self.validateAll()) {
                return;
            }

            if (!self.condition()) {
                self.$dialog.error({ messageId: 'Msg_2082' });
                return;
            }

            self.$blockui("invisible");
            let command: any = {
                "unit": self.targetUnit(),
                "targetId": self.targetId(),
                "page": self.selectedPage(),
                "name": self.taskPaletteOrgnization().name(),
                "remarks": self.taskPaletteOrgnization().remarks(),
                "position": self.taskPaletteOrgnization().keys(),
                "taskCode": self.taskPaletteOrgnization().taskCodes()
            }

           
            self.$ajax(Paths.REGISTER_TASK_PALETTE_ORGNIZATION, command).done(() => {
                self.$dialog.info({ messageId: 'Msg_15' }).then(() => {
                    self.selectedPage.valueHasMutated();
                });
            }).fail((res) => {
                if (res.messageId == 'Msg_3') {
                    self.selectedPage();
                }
            }).always(() => {
                self.$blockui("hide");
            });

            
            self.endStatus('Update');
            self.enableDelete(true);
            $('input#pageName').focus();           
        }

        remove(): void {
            const self = this;
            self.$dialog.confirm({messageId: "Msg_18"}).then((result: 'no' | 'yes') =>{
                self.$blockui("invisible");
                let command: any = {
                    "unit": self.taskPaletteOrgnization().targetUnit(),
                    "targetId": self.taskPaletteOrgnization().targetId(),
                    "page": self.selectedPage()
                }
                
                if(result === 'yes'){
                    self.$ajax(Paths.DELETE_TASK_PALETTE_ORGNIZATION, command).done(() =>{
                        self.$dialog.info({messageId: "Msg_16"}).then(() =>{                        
                            self.selectedPage.valueHasMutated(); 
                        });          
                        self.endStatus('Update');             
                    }).always(() =>{
                        self.$blockui("hide");
                    });    
                    self.$blockui("hide");      
                    $('#pageName').focus();          
                }
                if(result === 'no'){
                    self.$blockui("hide");
                    $('#pageName').focus();
                }
            }); 
            self.isClickLink(true);
            $('input#pageName').focus(); 
        }

       
        clickLinkButton(element?: any, param?: any): void {         
            let self = this, index: number = param();           
            self.selectedPage(index + 1);
            self.selectedPage.valueHasMutated();
            self.selectedLinkButton(index);
            self.handleClickLinkButton(index);     
            self.isClickLink(true);             
            $('input#pageName').focus();     
        }

        handleClickLinkButton(index: number): void {       
            const self = this;                 
            nts.uk.ui.errors.clearAll();
            _.find($('#group-link-button a.hyperlink.color-gray'), (a) => {
                $(a).removeClass('color-gray');
            });
            $($('a.hyperlink')[index]).addClass('color-gray'); 
        }

        deleteTask(data: any, event: any): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let index: number;

            if (data && data != undefined) {
                index = _.indexOf(self.taskPaletteOrgnization().keys(), Number(data.target.dataset.idx) + 1);
            } else if (event && event != undefined) {
                index = _.indexOf(self.taskPaletteOrgnization().keys(), Number($(event)[0].dataset.idx) + 1);
            }
            self.tasks().splice(index, 1);
            self.taskPaletteOrgnization().keys.splice(index, 1);
            self.taskPaletteOrgnization().taskNames.splice(index, 1);
            self.taskPaletteOrgnization().taskCodes.splice(index, 1);
            self.taskPaletteOrgnization().taskNames.splice(index, 1);
            dfd.resolve()
            return dfd.promise();
        }

        openDialogKdl012(data: any, event: any): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let position: number, index: number;

            self.clearError();
            if (data && data != undefined) {
                index = _.indexOf(self.taskPaletteOrgnization().keys(), Number(data.target.dataset.idx) + 1);
            } else if (event && event != undefined) {
                index = _.indexOf(self.taskPaletteOrgnization().keys(), Number($(event)[0].dataset.idx) + 1);
            }
            // self.textName(data ? data.text : null);
            // self.tooltip(data ? data.tooltip : null);

            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            let dataShare = self.dataShareA;
            data ? position = Number(data.target.dataset.idx) + 1 : position = Number($(event)[0].dataset.idx) + 1

            let request = {
                isMultiple: false,
                showExpireDate: true,
                workFrameNoSelection: 1,
                referenceDate: moment(dataShare.referenceDate).format("YYYY/MM/DD"),
                selectionCodeList: [self.taskPaletteOrgnization().taskCodes()[index]]
            };
            setShared('KDL012Params', request);

            nts.uk.ui.windows.sub.modal("/view/kdl/012/index.xhtml").onClosed(() => {
                let dataFromKdl012 = getShared("KDL012OutputList");
                if (dataFromKdl012) {
                    self.textName(dataFromKdl012[0].taskAbName);
                    self.taskPaletteOrgnization().keys.push(position);
                    self.taskPaletteOrgnization().taskCodes.push(dataFromKdl012[0].code);
                    self.taskPaletteOrgnization().taskNames.push(dataFromKdl012[0].taskName);
                    self.taskPaletteOrgnization().taskAbNames.push(dataFromKdl012[0].taskAbName);
                    dfd.resolve({ text: self.textName(), tooltip: self.tooltip(), data: dataFromKdl012[0] });
                }
            });
            return dfd.promise();
        }

        openPopup(button): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            let positionButton = $(button).data().idx;
            let dt = self.tasks()[positionButton];
            $("#task").trigger("getdatabutton", { text: dt.text, tooltip: dt.tooltip, data: dt.data });
            $("#task").trigger("getdatabutton1");
            $("#popup-area").css('visibility', 'visible');
            let buttonWidth = button.outerWidth(true) - 30;
            $("#popup-area").position({ "of": button, my: "left+" + buttonWidth + " top", at: "left+" + buttonWidth + " top" });
            $("#task").bind("namechanged", function (evt, data) {
                $("#task").unbind("namechanged");
                if (!nts.uk.util.isNullOrUndefined(data)) {
                    dfd.resolve(data);
                } else {
                    dfd.resolve(button.parent().data("cell-data"));
                }
            });
            return dfd.promise();
        }

        decision(): void {
            let self = this;
            $("#popup-area").css('visibility', 'hidden');
            $("#task").trigger("namechanged", { text: self.textName(), tooltip: self.tooltip(), data: self.dataWorkPairSet() });
        }

        condition(): boolean {
            const self = this;
            if(_.isNull(self.taskPaletteOrgnization().keys()) || _.isEmpty(self.taskPaletteOrgnization().keys())){
                return false;
            }
            return true;
        }

        closePopup(): void {
            nts.uk.ui.errors.clearAll()
            $("#popup-area").css('visibility', 'hidden');
            $("#task").trigger("namechanged", undefined);
        }
        closeDialog(): void {
            const self = this;  
            self.endStatus() === 'Update' ? setShared('dataShareFromKsu003b', self.selectedPage()) :setShared('dataShareFromKsu003b',null);
            self.$window.close();
        }

        private validateAll(): boolean {
            $('#pageName').ntsEditor('validate');          
            if (nts.uk.ui.errors.hasError()) {                    
                return true;
            }
            return false;
        }

        private clearError(): void {
            $('#pageName').ntsError('clear');
        }
    }   

    interface ITaskPalette {      
        displayName: string;
        name: string;        
        page: number;
    }

    interface ITaskPaletteOrgnization {
        targetUnit: number;
        targetId: string;
        displayName: string;
        name: string;
        remarks: string;
        page: number;
        keys: Array<number>;
        taskCodes: Array<string>;
        taskNames: Array<string>;
        taskAbNames: Array<string>;
        listTaskStatus: Array<number>;
    }

    class TaskPaletteOrgnization {
        targetUnit: KnockoutObservable<number> = ko.observable();
        targetId: KnockoutObservable<string> = ko.observable();
        displayName: KnockoutObservable<string> = ko.observable();
        name: KnockoutObservable<string> = ko.observable();
        remarks: KnockoutObservable<string> = ko.observable();
        page: KnockoutObservable<number> = ko.observable();      
        keys: KnockoutObservableArray<number> = ko.observableArray([]);
        taskCodes: KnockoutObservableArray<string> = ko.observableArray([]);
        taskNames: KnockoutObservableArray<string> = ko.observableArray([]);
        taskAbNames: KnockoutObservableArray<string> = ko.observableArray([]);
        tasks: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        listTaskStatus: KnockoutObservableArray<number> = ko.observableArray([]);

        updateData(param: ITaskPaletteOrgnization){
            const self = this;
            self.targetUnit(param.targetUnit);
            self.targetId(param.targetId);
            self.displayName(param.displayName);
            self.name(param.name);
            self.remarks(param.remarks);
            self.page(param.page);
            self.keys(param.keys);
            self.taskCodes(param.taskCodes);
            self.taskNames(param.taskNames);
            self.taskAbNames(param.taskAbNames);       
            self.listTaskStatus(param.listTaskStatus);   
        }

        resetData() {
            const self = this;
            self.name('');
            self.remarks('');
            self.keys([]);
            self.taskCodes([]);
            self.taskNames([]);
            self.taskAbNames([]);
            self.listTaskStatus([]);
        }
    }   

    class ItemModel {
        key: string;
        value: string;

        constructor(key: string, value: string) {
            this.key = key;
            this.value = value;
        }
    }
}