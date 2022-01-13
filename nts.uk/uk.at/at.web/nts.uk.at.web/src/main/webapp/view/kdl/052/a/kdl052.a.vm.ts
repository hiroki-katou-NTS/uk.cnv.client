module nts.uk.at.view.kdl052.a {
    import getText = nts.uk.resource.getText;
    import getShared = nts.uk.ui.windows.getShared;

    const Paths = {
        GET_CHILD_NURSING_LEAVE: "at/record/monthly/nursingleave/getChildNursingLeave",
        GET_CHILD_NURSING_LEAVE_BY_EMPID:"at/record/monthly/nursingleave/getDeitalInfoNursingByEmp"
    };

    @bean()
    class Kdl052ViewModel extends ko.ViewModel {
        listEmpId : any;
        dataOneEmp : KnockoutObservable<any> = ko.observable(null);
        employeeNameSelect: KnockoutObservable<string> = ko.observable('');
        employeeCodeSelect: KnockoutObservable<string> = ko.observable('');
        maxNumberOfYear: KnockoutObservable<string> = ko.observable('');
        upperLimitStartDate: KnockoutObservable<string> = ko.observable('');
        upperLimitEndDate: KnockoutObservable<string> = ko.observable('');
        maxNumberOfDays: KnockoutObservable<string> = ko.observable('');
        numberOfUse: KnockoutObservable<string> = ko.observable('');
        
        // search
        items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        searchText: KnockoutObservable<string> = ko.observable('');        

        //kcp
        employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray<UnitModel>([]);
        listComponentOption: any = [];
        selectedCode: KnockoutObservable<string> = ko.observable('1');
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean> = ko.observable(false);
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean> = ko.observable(false);
        isShowNoSelectRow: KnockoutObservable<boolean> = ko.observable(false);;
        isMultiSelect: KnockoutObservable<boolean> = ko.observable(false);
        isShowWorkPlaceName: KnockoutObservable<boolean> = ko.observable(false);
        isShowSelectAllButton: KnockoutObservable<boolean> = ko.observable(false);
        disableSelection: KnockoutObservable<boolean> = ko.observable(false);

        listEmployeeImport: any = [];

        //data table top
        managementCheck: KnockoutObservable<number> =  ko.observable(2);
        currentRemainNumberSelect: KnockoutObservable<string> =  ko.observable("");
        expiredWithinMonthSelect: KnockoutObservable<string> =  ko.observable("");
        dayCloseDeadlineSelect: KnockoutObservable<string> =  ko.observable("");

        // grid
        itemLts: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        columns: KnockoutObservableArray<NtsGridListColumn>;
        currentCode: KnockoutObservable<any> = ko.observable('');
			

        constructor(private params: any) {            
            super();
            let self = this; 
            self.listEmpId = getShared('KDL052A_PARAM').employeeIds;
          
            self.listComponentOption = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultiSelect: false,
                listType: 4,
                employeeInputList: self.employeeList,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedCode,
                isDialog: self.isDialog(),
                isShowNoSelectRow: self.isShowNoSelectRow(),
                alreadySettingList: self.alreadySettingList,
                isShowWorkPlaceName: self.isShowWorkPlaceName(),
                isShowSelectAllButton: self.isShowSelectAllButton(),
                disableSelection: self.disableSelection(),
                maxRows: 15,
                tabindex: -1
            };  

            self.columns = ko.observableArray([
                { headerText: getText('KDL052_25'), key: 'digestionDate', width: 170, formatter: v => {
                    return "<div style='margin-right: 30px; display: flex; float:right;'>" + v + " </div>";}
                },                
                { headerText: getText('KDL052_26'), key: 'numberOfUse', width: 80, formatter: v => {
                    return '<div style="margin-left: 10px;">' + v + '</div>';}
                } 
            ]); 

            self.selectedCode.subscribe((code: string) => {
                let emp: any = _.find(self.dataOneEmp().lstEmployee, x => x.employeeCode == code);
                self.employeeCodeSelect(emp.employeeCode);
                self.employeeNameSelect(emp.employeeName);
                self.findDetail(emp.employeeId);
            });
            self.loadData(self.listEmpId);
        }

        mounted(){
            const self = this;
            if (self.listEmpId.length > 1) {
                self.$window.size(580, 900);
                $("#left-content").show();
            } else {
                self.$window.size(580, 560);
                $("#left-content").hide();
            }  
            $("#btnClose").focus();
        }


        loadData(listEmp: any): void {
            let self = this;     
            self.$ajax(Paths.GET_CHILD_NURSING_LEAVE, listEmp).done((data: any) => {
                self.dataOneEmp(data);
                self.listEmployeeImport = data.lstEmployee;
                _.forEach(data.lstEmployee, (a: any, ind) => {
                    self.employeeList.push({ id: ind, code: a.employeeCode, name: a.employeeName });
                });                
                self.selectedCode(data.lstEmployee[0].employeeCode);
                $('#component-items-list').ntsListComponent(self.listComponentOption);
                
            }).fail((res) => {
                self.$dialog.alert({ messageId: res.messageId });
            }); 
        }

        findDetail(employeeId: string): void {
            let self = this;     
            let listDigestionDetails : Array<ItemModel> = [];
			self.managementCheck(2);
            self.$ajax(Paths.GET_CHILD_NURSING_LEAVE_BY_EMPID + '/' + employeeId).done((data: any) => {
               self.managementCheck(data.managementSection ? 1 : 0);
               self.maxNumberOfYear(data.maxNumberOfYear);
               self.upperLimitEndDate(data.upperLimitEndDate);
               self.upperLimitStartDate(data.upperLimitStartDate);
               self.maxNumberOfDays(data.maxNumberOfDays);
               self.numberOfUse(data.numberOfUse);
               
               _.forEach(data.listDigestionDetails, item => {
                    listDigestionDetails.push(new ItemModel(item.digestionStatus + ' ' + item.digestionDate, item.numberOfUse));                
               });
               self.itemLts(_.sortBy(listDigestionDetails, ['digestionDate']));
            }).fail((res) => {
                self.$dialog.alert({ messageId: res.messageId });
            }); 
        }

        findData(data: any): void {
            let self = this;            
            let text = $("input.ntsSearchBox.nts-editor.ntsSearchBox_Component").val()
            if (text == "") {
                nts.uk.ui.dialog.info({ messageId: "MsgB_24" });
            } else {
                let lstFil = _.filter(self.employeeList(), (z: any) => {
                    return _.includes(z.code, text);
                });

                if (lstFil.length > 0) {              
                    let index = _.findIndex(lstFil, (z : any) => _.isEqual(z.code,self.listComponentOption.selectedCode()));
                      if (index == -1 || index == lstFil.length - 1){
                          self.listComponentOption.selectedCode(lstFil[0].code);
                      } else {
                          self.listComponentOption.selectedCode(lstFil[index + 1].code);
                     }
                    let scroll: any = _.findIndex(self.employeeList(), (z: any) => _.isEqual(z.code, self.listComponentOption.selectedCode())),
                    id = _.filter($("table > tbody > tr > td > div"), (x: any) => {
                        return _.includes(x.id, "_scrollContainer") && !_.includes(x.id, "single-list");
                    })
                $("#" + id[0].id).scrollTop(scroll * 24);
                } else {
                    nts.uk.ui.dialog.info({ messageId: "MsgB_25" });
                }
            }
        }

        closeDialog(): void {
            let self = this;
            self.$window.close();
        }
    }

    export interface UnitModel {
		id?: string;
		code: string;
		name?: string;
		workplaceName?: string;
		isAlreadySetting?: boolean;
		optionalColumn?: any;
	}

    class ItemModel {
        digestionDate: string;
        numberOfUse: string;      
        digestionStatus: string;   
        constructor(date: string, numberOfUes: string, status?: string) {
            this.digestionDate = date;
            this.numberOfUse = numberOfUes; 
            this.digestionStatus = status;           
        }
    }

    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }
}