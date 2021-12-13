module nts.uk.at.view.ksu003.c {
    import getText = nts.uk.resource.getText;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const Paths = {
        GET_AVAILABLE_TASK_MASTER: "at/shared/scherec/taskmanagement/taskmaster/tasks",
        REGISTET_TASK_SCHEDULE: "at/schedule/task/taskschedule/register"
    };

    @bean()
    class Ksu003cViewModel extends ko.ViewModel {
       
        listComponentOption: any;
        selectedCode: KnockoutObservable<string>;
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean> = ko.observable(true);
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        disableSelection: KnockoutObservable<boolean>;
        enableTimeZone: KnockoutObservable<boolean> = ko.observable(false);

        listEmp: KnockoutObservableArray<EmployeeModel> = ko.observableArray([]);
        date: KnockoutObservable<string> = ko.observable("");
        baseDate: KnockoutObservable<string> = ko.observable("");
        selectedMode: KnockoutObservable<number> = ko.observable(0);
        startTime: KnockoutObservable<number> = ko.observable();
        endTime: KnockoutObservable<number> = ko.observable();

        taskMasterList: KnockoutObservableArray<TaskMaster> = ko.observableArray([]);
        selectedTaskCode: KnockoutObservable<string> = ko.observable("");
        isEnable: KnockoutObservable<boolean>;
        isEditable: KnockoutObservable<boolean>;
        isReload: KnockoutObservable<boolean> = ko.observable(false);
        singleSelectedCode: any;

        endStatus: KnockoutObservable<string> = ko.observable("Cancel"); 

        constructor() {
            super();
            const self = this;
            self.selectedCode = ko.observable('1');
           
            self.isShowAlreadySet = ko.observable(false);            
            self.isDialog = ko.observable(false);
            self.isShowNoSelectRow = ko.observable(false);
          
            self.isShowWorkPlaceName = ko.observable(false);
            self.isShowSelectAllButton = ko.observable(false);
            self.disableSelection = ko.observable(false);
            
            self.isEnable = ko.observable(true);
            self.isEditable = ko.observable(true);

            self.singleSelectedCode = ko.observable(null);
            self.loadData();
            self.listComponentOption = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultiSelect: self.isMultiSelect(),
                listType: ListType.EMPLOYEE,
                employeeInputList: self.listEmp,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedCode,
                isDialog: true,
                isShowNoSelectRow: self.isShowNoSelectRow(),               
                isShowWorkPlaceName: self.isShowWorkPlaceName(),
                isShowSelectAllButton: self.isShowSelectAllButton(),                     
                maxRows: 10,        
                disableSelection: self.disableSelection()
            };
            $('#component-items-list').ntsListComponent(self.listComponentOption).done(function(){
                $('#component-items-list').focusComponent();
            });

            self.selectedMode.subscribe((mode) => {
                if(mode == 0){
                    self.clearError();
                    self.enableTimeZone(false); 
                } else {
                    self.enableTimeZone(true);
                }
            });
            self.startTime.subscribe((start) => {
                self.clearError();
                if (start >= self.endTime()) {
                    $('#startTime').ntsError('set', { messageId: 'Msg_770', messageParams: getText("KSU003_92") }); 
                }
            });

            self.endTime.subscribe((end) => {
                self.clearError();
                if (end <= self.startTime()) {                    
                    $('#endTime').ntsError('set', { messageId: 'Msg_770', messageParams: getText("KSU003_92") });
                }
            });
        }

        mounted(){
            $('#task-list').focus();
            setTimeout(function(){ 
                let g = $('[tabindex=0]');
                for (let i = 0; i< g.length; i++) 
                    {
                    $(g[i]).attr("tabindex" , -1);
                    }},200);
        }
        
        loadData(): void {
            const self = this;            
            let dataShare = getShared("dataShareKsu003c");
            let empList: any = [], taskList: any = [];

            for(let i=0; i< dataShare.employeeCodes.length; i++){
                empList.push(new EmployeeModel(dataShare.employeeIds[i], dataShare.employeeCodes[i], dataShare.employeeNames[i]));
            }
            self.listEmp(empList);
            self.baseDate(dataShare.date);
            self.date(self.converDateToDateCss(dataShare.date));
            let commamd : any ={
                "baseDate": dataShare.date,
                "taskFrameNo": 1
            } ;

            self.$blockui("invisible");
            self.$ajax(Paths.GET_AVAILABLE_TASK_MASTER, commamd).done((data: Array<TaskMaster>) =>{
                if (!_.isNull(data) && !_.isEmpty(data)){
                    _.each(data, item => {
                        taskList.push(new TaskMaster(item.code, item.taskName));                        
                    });            
                    self.taskMasterList(_.sortBy(taskList, task => task.code ));   
                    if(!self.isReload()) {
                        self.selectedTaskCode(data[0].code);
                    }                                        
                }
            }).always(() => {
                self.$blockui("hide");
            });
            $('#task-list').focus();
        }
        
        registerOrUpdate(): void {
            const self = this;            
            let listEmpId: Array<string> = [], command: any = {}; 

            _.each(self.selectedCode(), empCode => {
                _.map(self.listEmp(), emp => {
                    if(emp.code == empCode) {
                        listEmpId.push(emp.id);
                    }
                });
            });            

            if(_.isEmpty(listEmpId)) { 
                self.$dialog.error({ messageId: 'Msg_2147', messageParams: [getText("設定できる社員リスト")]});               
                return;
            }            

            if (self.selectedMode() == 1) {
                if (self.validateAll()) {
                    return;
                }
                command.startTime = self.startTime();
                command.endTime = self.endTime();
            }

            command.mode = self.selectedMode();
            command.date = self.baseDate();
            command.employeeIds = listEmpId;
            command.taskCode = self.selectedTaskCode();
            
            self.$blockui("invisible");
            self.$ajax(Paths.REGISTET_TASK_SCHEDULE, command).done(() => {
                self.$dialog.info({ messageId: 'Msg_15'}).then(() => {
                    self.isReload(true);
                    self.loadData();
                });
                self.endStatus('Update');
            }).fail((res) => {
                self.$dialog.info({ messageId: res.messageId});
            }).always(() => {
                self.$blockui("hide");
            });

        }

        converDateToDateCss(fromDate: string): string {

            let date = new Date(fromDate), dateCss: string;
            switch (date.getDay()) {
                case 0:
                    dateCss =  fromDate  + '<span style="color:#ff0000">(' + moment.weekdaysShort(0) + ')</span>';                    
                    break;
                case 1:
                    dateCss =  fromDate + "(" + moment.weekdaysShort(1) + ')';                   
                    break;
                case 2:
                    dateCss =  fromDate + "(" + moment.weekdaysShort(2) + ')';
                    break;
                case 3:
                    dateCss =  fromDate + "(" + moment.weekdaysShort(3) + ')';
                    break;
                case 4:
                    dateCss =  fromDate + "(" + moment.weekdaysShort(4) + ')';
                    break;
                case 5:
                    dateCss =  fromDate + "(" + moment.weekdaysShort(5) + ')';
                    break;
                case 6:
                    dateCss =  fromDate + '<span style="color:#0000ff">(' + moment.weekdaysShort(6) + ')</span>';
                    break;
            }
            return dateCss;
        }   

        closeDialog(): void {
            const self = this;
            setShared('dataShareFromKsu003c', self.endStatus());
            self.$window.close();
        }

        private validateAll(): boolean {
            const self = this;
            $('#startTime').ntsEditor('validate');
            $('#endTime').ntsEditor('validate');
            if (nts.uk.ui.errors.hasError()) {                    
                return true;
            }
            return false;
        }

        private clearError(): void {
            $('#startTime').ntsError('clear');
            $('#endTime').ntsError('clear');
        }
    }
    

    class TaskMaster {
        code: string;
        taskName: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.taskName = name;
        }
    }
    class EmployeeModel {
        id: string;
        code: string;
        name: string;
        constructor(id: string, code: string, name: string) {
            this.id = id;
            this.code = code;
            this.name = name;
        }
    }

   

    export class ListType {
        static EMPLOYMENT = 1;
        static CLASSIFICATION = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

     export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }
}