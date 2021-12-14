module nts.uk.at.view.ksu001.la {
    import blockUI = nts.uk.ui.block;    
    import getText = nts.uk.resource.getText;
    
    export module viewmodel {
        export class ScreenModel {
            listComponentOption: any;
            itemsLeft: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
            itemsRight: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
            listScheduleTeam: KnockoutObservableArray<ScheduleTeam> = ko.observableArray([]);
            columns: KnockoutObservableArray<any>;
            columnsLeft: KnockoutObservableArray<any>;
            columnsRight: KnockoutObservableArray<any>;
            currentCodeListLeft: KnockoutObservableArray<any> = ko.observableArray([]);
            currentCodeListRight: KnockoutObservableArray<any> = ko.observableArray([]);
            selectedCode: KnockoutObservable<string> = ko.observable("");
            workplaceGroupId: KnockoutObservable<string> = ko.observable("");
            workplaceGroupName: KnockoutObservable<string> = ko.observable("");
            enableDelete: KnockoutObservable<boolean> = ko.observable(true);
            isEditing: KnockoutObservable<boolean> = ko.observable(false);
            baseDate: KnockoutObservable<string> = ko.observable("");
            exitStatus: KnockoutObservable<string> = ko.observable("Cancel");  
            placeHolders: string = "";
            scheduleTeamModel: KnockoutObservable<ScheduleTeamModel> = ko.observable(new ScheduleTeamModel("", "", "", "",[]));

            constructor() {
                var self = this;
                self.columns = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KSU001_3208'), key: 'code', width: 100 },
                    { headerText: nts.uk.resource.getText('KSU001_3209'), key: 'name', width: 150 }
                ]);

                self.columnsRight = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KSU001_3208'), key: 'employeeCd', width: 105 },
                    { headerText: nts.uk.resource.getText('KSU001_3209'), key: 'businessName', width: 160 }
                ]);

                self.columnsLeft = ko.observableArray([
                    { headerText: nts.uk.resource.getText('KSU001_3208'), key: 'employeeCd', width: 90 },
                    { headerText: nts.uk.resource.getText('KSU001_3209'), key: 'businessName', width: 145 },
                    { headerText: nts.uk.resource.getText('KSU001_3215'), key: 'teamName', width: 65 }
                ]);      
                
                let holders = getText('KSU001_3208') + '・'
                    + getText('KSU001_3209') + '・'
                    + getText('KSU001_3215') + 'で検索…';                    
                self.placeHolders = holders.replace("１", "");
                self.selectedCode.subscribe((code: string) => {
                    let dfd = $.Deferred();
                    blockUI.invisible();
                    if (_.isEmpty(code)) {
                        self.clearData();
                        dfd.resolve();
                    } else {
                        service.findDetail(self.workplaceGroupId(), code).done((dataDetail: any) => {
                            if(!_.isNull(dataDetail) && !_.isEmpty(dataDetail)){
                                self.clearError();
                                self.enableDelete(true);
                                self.scheduleTeamModel().updateData(dataDetail);
                                self.scheduleTeamModel().isEnableCode(false);
                                self.scheduleTeamModel().workplaceGroupId(self.workplaceGroupId());
                                self.isEditing(true);
                                self.currentCodeListLeft([]);
                                self.currentCodeListRight([]);
                                self.getEmpOrgInfo();
                            }                           
                            $('#scheduleTeamName').focus();                            
                            dfd.resolve();
                        })                        
                    }
                    blockUI.clear();
                    return dfd.promise();
                });

                // Initial listComponentOption
                self.listComponentOption = {
                    isMultiSelect: false,
                    listType: ListType.EMPLOYMENT,
                    selectType: SelectType.SELECT_BY_SELECTED_CODE,
                    selectedCode: self.selectedCode,
                    isDialog: false
                };                
            }

            public startPage(): JQueryPromise<any> {
                let self = this;
                var dfd = $.Deferred();               
                let baseDate = nts.uk.ui.windows.getShared("baseDate");
                self.baseDate(baseDate);
                blockUI.invisible();
                let dateRequest: any = {baseDate: self.baseDate()};   
                service.findWorkplaceGroup(dateRequest).done((data: WorkplaceGroup) => {
                    if(data){
                        let workplaceGroup = ko.toJS(data);
                        self.workplaceGroupName(workplaceGroup.workplaceGroupName);
                        self.workplaceGroupId(workplaceGroup.workplaceGroupId);
                        service.findAll(workplaceGroup.workplaceGroupId).done((listScheduleTeam: Array<ScheduleTeam>) => {
                            if (!_.isEmpty(listScheduleTeam) && !_.isNull(listScheduleTeam)) {                           
                                self.listScheduleTeam(listScheduleTeam);                               
                                self.selectedCode(listScheduleTeam[0].code);                                
                            } else {
                                self.isEditing(false);
                                self.clearData();
                            }
                        }).fail((res) => {
                            nts.uk.ui.dialog.error({ messageId: res.messageId});
                            blockUI.clear(); 
                        });
                        self.getEmpOrgInfo();
                        blockUI.clear();
                        dfd.resolve();
                    }                    
                }).fail((res) =>{
                    nts.uk.ui.dialog.error({ messageId: "Msg_1867" }).then(function(){
                        self.exitStatus("Cancel");
                        self.closeDialog();
                    });
                    blockUI.clear(); 
                }).always(() =>{
                    blockUI.clear();
                });
                return dfd.promise();
            }

            private getEmpOrgInfo(): void {
                const self = this;
                let request:any = {};
                let itemLeft: any = {};  
                request.baseDate = self.baseDate();
                request.workplaceGroupId = self.workplaceGroupId(); 
                service.findEmpOrgInfo(request).done((dataAll: Array<ItemModel>)=>{
                    _.each(dataAll, x =>{
                        if(x.teamName === ""){
                            x.teamName = nts.uk.resource.getText('KSU001_3223');
                        } 
                    });
                    if(self.selectedCode()){
                        itemLeft = _.filter(dataAll, x =>{
                            return x.teamCd != self.selectedCode();
                        });             
                        self.itemsLeft(_.sortBy(itemLeft, [function (item: { employeeCd: any; }) { return item.employeeCd; }]));
                        self.itemsRight(_.sortBy(_.difference(dataAll, itemLeft), [function (item: { employeeCd: any; }) { return item.employeeCd; }]));
                    } else {
                        self.itemsLeft(_.sortBy(dataAll, [function(item: { employeeCd: any; }){return item.employeeCd;}]));    
                    }                    
                }).fail((res) => {
                    nts.uk.ui.dialog.error({ messageId: res.messageId });
                });
            }
            public registerOrUpdate(): void {
                let self = this;                
                let employeeIds: string[] = [];                 
                    _.each(self.itemsRight(), (item) =>{
                        employeeIds.push(item.employeeId);
                    });

                self.scheduleTeamModel().employeeIds(employeeIds);
                self.scheduleTeamModel().workplaceGroupId(self.workplaceGroupId());
                if (self.validateAll()) {
                    return;
                }
                blockUI.invisible();
                if (!self.isEditing()) {                    
                    //register                    
                    service.register(ko.toJSON(self.scheduleTeamModel)).done(() => {
                        nts.uk.ui.dialog.info({messageId: "Msg_15"}).then(function(){
                            service.findAll(self.workplaceGroupId()).done((listScheduleTeam: Array<ScheduleTeam>) => {
                                self.listScheduleTeam(listScheduleTeam);                            
                                self.selectedCode(self.scheduleTeamModel().code());
                            });
                        });           
                        blockUI.clear();   
                    }).fail((res) => {
                        blockUI.clear();
                        if(res.messageId == "Msg_3"){
                            self.selectedCode("");
                            $('#scheduleTeamCd').ntsError('set',{messageId: res.messageId});
                        }                        
                    });
                } else {
                    //update      
                    service.update(ko.toJSON(self.scheduleTeamModel)).done(()=>{                        
                        self.listScheduleTeam(_.map(self.listScheduleTeam(), function(el: ScheduleTeam){
                            if(el.code == self.scheduleTeamModel().code()){
                                return new ScheduleTeam(self.scheduleTeamModel().code(), self.scheduleTeamModel().name());
                            }
                            return el;
                        }));                                             
                        self.getEmpOrgInfo();                        
                        nts.uk.ui.dialog.info({messageId: "Msg_15"}).then(function() {
                            $('#scheduleTeamName').focus();
                        }); 
                        blockUI.clear();                                           
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({messageId: res.messageId});                        
                    }).always (()=>{
                        blockUI.clear();
                    });
                }
                self.currentCodeListLeft([]);
                self.currentCodeListRight([]);
                self.exitStatus("Update");
            }

            public remove(): void {
                let self = this;               
                nts.uk.ui.dialog.confirm({messageId: "Msg_18"}).ifYes(function(){
                    let command: any = {};
                    command.code = self.scheduleTeamModel().code();
                    command.workplaceGroupId = self.workplaceGroupId();
                    blockUI.invisible();
                    service.remove(command).done(function(){
                        nts.uk.ui.dialog.info({messageId: "Msg_16"}).then(function(){  
                            if(self.listScheduleTeam().length == 1){
                                self.listScheduleTeam([]);
                                self.selectedCode("");
                            } else {
                                let indexSelected: number;
                                for(let index:number = 0; index < self.listScheduleTeam().length; index++){
                                    if(self.listScheduleTeam()[index].code == self.selectedCode()){
                                        indexSelected = (index == self.listScheduleTeam().length - 1)? index -1: index;
                                        self.listScheduleTeam().splice(index,1);
                                        break;
                                    }
                                }                                
                                self.enableDelete(true);
                                self.selectedCode(self.listScheduleTeam()[indexSelected].code);
                            }
                        });
                    }).always(function(){
                        blockUI.clear();
                    });
                }).ifNo(function(){
                    blockUI.clear();
                    $('#scheduleTeamName').focus();
                });
                self.exitStatus("Update");
            }

            public moveItemLeftToRight(): void {
                const vm = this;
                let empListLeft = ko.toJS(vm.itemsLeft);
                let empListRight = ko.toJS(vm.itemsRight);

                // get value and index from gridlist
                let idAs = $('#emp-list-left').ntsGridList("getSelected");               
                let itemChosen = [];
                let employeeCdChosen = [];
                _.each(idAs, id => {
                    // get item by index from gridlist
                    itemChosen.push(empListLeft[id.index]);
                    employeeCdChosen.push(id.id);
                });

                var temp = _.difference(empListLeft, itemChosen);
                vm.itemsLeft(temp);
                vm.itemsRight(_.union(empListRight, _.sortBy(itemChosen, [function (o: { employeeCd: any; }) { return o.employeeCd; }])) );
                vm.currentCodeListRight(employeeCdChosen);
            }

            public moveItemRightToLeft(): void {
                const vm = this;
                let empListRight = ko.toJS(vm.itemsRight);
                let empListLeft = ko.toJS(vm.itemsLeft);

                // get value and index from gridlist
                let idBs = $('#emp-list-right').ntsGridList("getSelected");
                let itemChosen = [];
                let employeeCdChosen = [];
                _.each(idBs, id => {
                    if( empListRight[id.index].teamCd == vm.selectedCode()){
                        empListRight[id.index].teamName = nts.uk.resource.getText('KSU001_3223');
                    }                    
                    itemChosen.push(empListRight[id.index]);
                    employeeCdChosen.push(id.id);
                });
                vm.itemsRight(_.difference(empListRight, itemChosen));
                vm.itemsLeft(_.sortBy(_.union(empListLeft, itemChosen), [function (o: { employeeCd: any; }) { return o.employeeCd; }]));
                vm.currentCodeListLeft(employeeCdChosen);
            }

            public closeDialog(): void {
                let self = this;
                nts.uk.ui.windows.setShared("ksu001la-result", self.exitStatus());
                nts.uk.ui.windows.close();
            }

            public clearData(): void {
                let self = this;
                self.selectedCode("");
                self.enableDelete(false);
                self.isEditing(false);
                self.scheduleTeamModel().resetData(); 
                // let temp = _.union(self.itemsLeft(), self.itemsRight());
                // self.itemsLeft(_.sortBy(temp, [function (o: { employeeCd: any; }) { return o.employeeCd; }]));
                
                self.itemsRight([]); 
                self.currentCodeListLeft([]);
                self.currentCodeListRight([]);
                self.clearError(); 
                self.getEmpOrgInfo();
                $('#scheduleTeamCd').focus();              
            }

            private validateAll(): boolean {
                $('#scheduleTeamCd').ntsEditor('validate');
                $('#scheduleTeamName').ntsEditor('validate');
                $('#scheduleTeamRemarks').ntsEditor('validate');
                if (nts.uk.ui.errors.hasError()) {                    
                    return true;
                }
                return false;
            }

            private clearError(): void {
                $('#scheduleTeamCd').ntsError('clear');
                $('#scheduleTeamName').ntsError('clear');
                $('#scheduleTeamRemarks').ntsError('clear');
            }
        }

        class ScheduleTeamModel {
            code: KnockoutObservable<string>;
            name: KnockoutObservable<string>;
            note: KnockoutObservable<string>;
            workplaceGroupId: KnockoutObservable<string> = ko.observable("");
            employeeIds: KnockoutObservableArray<string> = ko.observableArray([]);
            isEnableCode: KnockoutObservable<boolean>;

            constructor(teamCode: string, teamName: string, teamRemarks: string, workplaceGroupId: string, employeeIds: string[]) {
                this.code = ko.observable(teamCode);
                this.name = ko.observable(teamName);
                this.note = ko.observable(teamRemarks);
                this.isEnableCode = ko.observable(false);
                this.workplaceGroupId = ko.observable(workplaceGroupId);  
                this.employeeIds = ko.observableArray(employeeIds);             
            }
            public resetData() {
                let self = this;
                self.code("");
                self.name("");
                self.note("");
                self.employeeIds(null);
                self.isEnableCode(true);                
            }
            public updateData(scheduleTeamDto: any) {
                let self = this;                
                self.code(scheduleTeamDto.code);
                self.name(scheduleTeamDto.name);
                self.note(scheduleTeamDto.note);
            }
        }
    }

    class WorkplaceGroup {
        workplaceGroupName: string;
        workplaceGroupId: string;
    }

    class ScheduleTeamDto {
        code: string;
        name: string;
        note: string;
        constructor(code: string, name: string, remarks: string) {
            this.code = code;
            this.name = name;
            this.note = remarks;
        }
    }
    class ScheduleTeam {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }



    /**
       * List Type
       */
    export class ListType {
        static EMPLOYMENT = 1;
        static CLASSIFICATION = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    /**
     * SelectType
     */
    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }

    class ItemModel {
        employeeId: string;
        employeeCd: string;
        businessName: string;
        teamCd: string;
        teamName: string;

        constructor(id: string, code: string, name: string, teamName: string, teamCd: string) {
            this.employeeId = id;
            this.employeeCd = code;
            this.businessName = name;
            this.teamName = teamName;
            this.teamCd = teamCd;
        }
    }    
}