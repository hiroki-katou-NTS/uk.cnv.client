module nts.uk.at.view.kdw003.cg {
    import getText = nts.uk.resource.getText;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
	import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    const Paths = {
        GET_TARGET_EMPLOYEE_INFO: "screen/at/task/getTargetEmployeeInfo",
        GET_TASK_ITEM_INFO: "screen/at/task/getTaskItemInfo",
        GET_TASK_INITIAL_SEL_SETTING: "screen/at/task/getTaskInitialSettingHist",
        REGISTER_TASK_INITIAL_SEL_SETTING: "screen/at/task/register",
        UPDATE_TASK_INITIAL_SEL_SETTING: "screen/at/task/update",
        DELETE_TASK_INITIAL_SEL_SETTING: "screen/at/task/remove",
        COPY_TASK_INITIAL_SEL_SETTING: "screen/at/task/copy",
        CHECK_SETTING: "screen/at/task/checkSetting",
        GET_CHILD_TASK_ITEM_INFO: "screen/at/task/getListChildTask"

    };

    @bean()
    class Kdw003gViewModel extends ko.ViewModel {        
        //Declare employee filter component
        ccg001ComponentOption: any;
        showinfoSelectedEmployee: KnockoutObservable<boolean> = ko.observable(false);
        employeeList: KnockoutObservableArray<UnitModel>;
        multiSelectedCode: KnockoutObservableArray<string>;

        listComponentOption: ComponentOption;
        lstEmployee: KnockoutObservableArray<EmployeeModel> = ko.observableArray([]);
        selectedEmployee: KnockoutObservable<any> = ko.observable(null);
        systemReference: KnockoutObservable<number>  = ko.observable(SystemType.EMPLOYMENT);
        isDisplayOrganizationName: KnockoutObservable<boolean>  = ko.observable(true);        
        selectedItem: KnockoutObservable<string> = ko.observable(null);
       
        columns: KnockoutObservableArray<any>;
        listHistPeriod: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedHist: KnockoutObservable<string>= ko.observable('');
        listTaskItemInfo: KnockoutObservableArray<TaskItemModel> = ko.observableArray([]);

        ENDDATE_LATEST: string = '9999/12/31';
        startDate: KnockoutObservable<string> = ko.observable(new Date().toString());
        startDatePeriod: KnockoutObservable<string> = ko.observable('');
        endDate: KnockoutObservable<string> = ko.observable(this.ENDDATE_LATEST);

        enableNewBtn: KnockoutObservable<boolean> = ko.observable(true);
        enableDeleteBtn: KnockoutObservable<boolean> = ko.observable(false); 
        isReload: KnockoutObservable<boolean> = ko.observable(false); 

        enableTaskFrame1: KnockoutObservable<boolean> = ko.observable(false);
        enableTaskFrame2: KnockoutObservable<boolean> = ko.observable(false);
        enableTaskFrame3: KnockoutObservable<boolean> = ko.observable(false);
        enableTaskFrame4: KnockoutObservable<boolean> = ko.observable(false);
        enableTaskFrame5: KnockoutObservable<boolean> = ko.observable(false);

        frameName1: KnockoutObservable<string> = ko.observable('');
        frameName2: KnockoutObservable<string> = ko.observable('');
        frameName3: KnockoutObservable<string> = ko.observable('');
        frameName4: KnockoutObservable<string> = ko.observable('');
        frameName5: KnockoutObservable<string> = ko.observable('');

        taskListFrame1: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        taskListFrame2: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        taskListFrame3: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        taskListFrame4: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        taskListFrame5: KnockoutObservableArray<TaskModel> = ko.observableArray([]);

        listTaskFrame1: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        listTaskFrame2: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        listTaskFrame3: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        listTaskFrame4: KnockoutObservableArray<TaskModel> = ko.observableArray([]);
        listTaskFrame5: KnockoutObservableArray<TaskModel> = ko.observableArray([]);

        selectedTaskCode1: KnockoutObservable<string> = ko.observable("");
        selectedTaskCode2: KnockoutObservable<string> = ko.observable("");
        selectedTaskCode3: KnockoutObservable<string> = ko.observable("");
        selectedTaskCode4: KnockoutObservable<string> = ko.observable("");
        selectedTaskCode5: KnockoutObservable<string> = ko.observable("");
        first: TaskModel = new TaskModel('', getText('KDW003_143'));
      
        constructor() {
            super();
            const self = this;
            // let first: TaskModel = new TaskModel('', getText('KDW003_143'));

            let ccg001ComponentOption: any = {
                /** Common properties */
                systemType: 2, // システム区分
                showEmployeeSelection: false, // 検索タイプ
                showQuickSearchTab: true, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: true, // 基準日利用
                showClosure: false, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: false, // 対象期間利用
                periodFormatYM: false, // 対象期間精度

                /** Required parameter */
                baseDate: moment().toISOString(), // 基準日
                // periodStartDate: moment.utc("1900/01/01", "YYYY/MM/DD").toISOString(), // 対象期間開始日
                periodStartDate: moment.utc(getShared("dataShareKdw003g").baseDate, "YYYY/MM/DD").toISOString(), // 対象期間開始日
                
                periodEndDate: moment.utc("9999/12/31", "YYYY/MM/DD").toISOString(), // 対象期間終了日
                // dateRangePickerValue: self.dateValue,
                inService: true, // 在職区分
                leaveOfAbsence: true, // 休職区分
                closed: true, // 休業区分
                retirement: false, // 退職区分

                /** Quick search tab options */
                showAllReferableEmployee: true, // 参照可能な社員すべて
                showOnlyMe: true, // 自分だけ
                showSameWorkplace: true, // 同じ職場の社員
                showSameWorkplaceAndChild: true, // 同じ職場とその配下の社員

                /** Advanced search properties */
                showEmployment: true, // 雇用条件
                showWorkplace: true, // 職場条件
                showClassification: true, // 分類条件
                showJobTitle: true, // 職位条件
                showWorktype: false, // 勤種条件
                isMutipleCheck: true, // 選択モード

                /** Return data */
                returnDataFromCcg001: function (data: Ccg001ReturnedData) {
                    self.showinfoSelectedEmployee(true);
                    //Convert list Object from server to view model list
                    let items = _.map(data.listEmployee, item => {
                        return {
                            id: item.employeeId,
                            code: item.employeeCode,
                            businessName: item.employeeName,
                            workplaceName: item.affiliationName,
                            workplaceId: item.affiliationId,
                            depName: '',
                            isAlreadySetting: true
                        }
                    });
                    self.lstEmployee(_.orderBy(items, ['code'], ['asc']));
                   
                    self.selectedEmployee(self.lstEmployee()[0].id);
                    self.loadKcp009();
                    //Fix bug 42, bug 43
                    let selectList = _.map(data.listEmployee, item => {
                        return item.employeeId;
                    });
                }
            }
            $('#ccgcomponent').ntsGroupComponent(ccg001ComponentOption);  

            self.selectedHist.subscribe(histId => {     
                // self.findDetail(self.selectedEmployee(), true);  
                if(histId != '') {
                    self.loadData();    
                    self.findDetail(self.selectedEmployee(), true);  
                    self.findTaskItemDetail(histId);
                }                   
                self.enableNewBtn(true);
            });
            

            self.columns = ko.observableArray([
                { headerText: 'No', key: 'id', width: 50, hidden: true }, 
                { headerText: getText('KDW003_128'), key: 'dateRange', width: 200 }
            ]);

            self.selectedEmployee.subscribe((employeeCode) => {
                self.findDetail(employeeCode, false);                
                
            });

            self.startDate.subscribe(value => {
                let date = moment(value).format('YYYY/MM/DD');
                
                let listTaskFrame1 = _.filter(self.taskListFrame1(), task => { return (date <= task.endDate && date >= task.startDate) });
                let listTaskFrame2 = _.filter(self.taskListFrame2(), task => { return (date <= task.endDate && date >= task.startDate) });
                let listTaskFrame3 = _.filter(self.taskListFrame3(), task => { return (date <= task.endDate && date >= task.startDate) });
                let listTaskFrame4 = _.filter(self.taskListFrame4(), task => { return (date <= task.endDate && date >= task.startDate) });
                let listTaskFrame5 = _.filter(self.taskListFrame5(), task => { return (date <= task.endDate && date >= task.startDate) });   

                listTaskFrame1.unshift(self.first);
                listTaskFrame2.unshift(self.first);
                listTaskFrame3.unshift(self.first);
                listTaskFrame4.unshift(self.first);
                listTaskFrame5.unshift(self.first);

                self.listTaskFrame1(listTaskFrame1);
                self.listTaskFrame2(listTaskFrame2);
                self.listTaskFrame3(listTaskFrame3);
                self.listTaskFrame4(listTaskFrame4);
                self.listTaskFrame5(listTaskFrame5);               
            });            

            self.loadData();

            self.selectedTaskCode1.subscribe(code => {
                if(!_.isNil(code))
                    self.getChildTask(1, code);
            });

            self.selectedTaskCode2.subscribe(code => {
                if(!_.isNil(code))
                    self.getChildTask(2, code);
            });

            self.selectedTaskCode3.subscribe(code => {
                if(!_.isNil(code))
                    self.getChildTask(3, code);
            });

            self.selectedTaskCode4.subscribe(code => {
                if(!_.isNil(code))
                    self.getChildTask(4, code);
            });
        }

        find(taskCode: string, listTask: Array<TaskModel>): boolean {
            return  _.find(listTask, task => task.taskCode == taskCode) != undefined;          
        }

        loadKcp009() {
            let self = this;
            self.listComponentOption = {
                systemReference: self.systemReference(),
                isDisplayOrganizationName: self.isDisplayOrganizationName(),
                employeeInputList: self.lstEmployee,
                targetBtnText: getText("KCP009_3"),
                selectedItem: self.selectedEmployee,
                tabIndex: 7
            };
            $('#emp-component').ntsLoadListComponent(self.listComponentOption);
        }

        loadData(): void {
            const self = this;            
            let dataShare = getShared("dataShareKdw003g");
            let empList: Array<EmployeeModel> = [], taskLst: Array<TaskModel> = [],  
                taskLst1: Array<TaskModel> = [], taskLst2: Array<TaskModel> = [],
                taskLst3: Array<TaskModel> = [], taskLst4: Array<TaskModel> = [], 
                taskLst5: Array<TaskModel> = [],
                listFrameUseAtr: Array<number>;

            self.$blockui("invisible");
            self.$ajax(Paths.GET_TASK_ITEM_INFO).done((taskList: Array<TaskModel>) => {
                self.$ajax(Paths.GET_TARGET_EMPLOYEE_INFO, dataShare).done((dataList: Array<EmployeeModel>) =>{
                    if (!_.isNull(dataList) && !_.isEmpty(dataList)){
                        _.each(dataList, data => {
                            empList.push({ id: data.id, code: data.code, businessName: data.businessName, depName: data.depName, workplaceName: data.workplaceName });
                        });
                        self.lstEmployee(_.sortBy(empList,['id']));
                        self.loadKcp009();
                        self.selectedEmployee(self.lstEmployee()[0].id);
                    } 
                }).fail((res) => {
                    self.$dialog.error({messageId: res.messageId}).then(() => {
                        self.closeDialog();
                    });
                }).always(() => {
                    self.$blockui("hide");
                });
                
                if(!_.isNull(taskList) && !_.isEmpty(taskList)){                    
                    _.each(taskList, task => {
                        taskLst.push(new TaskModel(task.taskCode, task.taskCode + " " + task.taskName, task.taskName,  task.frameNo, task.startDate, task.endDate ));
                    });
                    let temp = taskList[0];
                    listFrameUseAtr = temp.listFrameNoUseAtr;

                    self.enableTaskFrame1(taskList[0].listFrameNoUseAtr[0] == 1);
                    self.enableTaskFrame2(taskList[0].listFrameNoUseAtr[1] == 1);
                    self.enableTaskFrame3(taskList[0].listFrameNoUseAtr[2] == 1);
                    self.enableTaskFrame4(taskList[0].listFrameNoUseAtr[3] == 1);
                    self.enableTaskFrame5(taskList[0].listFrameNoUseAtr[4] == 1);

                    self.frameName1(taskList[0].listFrameName[0]);
                    self.frameName2(taskList[0].listFrameName[1]);
                    self.frameName3(taskList[0].listFrameName[2]);
                    self.frameName4(taskList[0].listFrameName[3]);
                    self.frameName5(taskList[0].listFrameName[4]);
                    
                    taskLst1 = _.filter(taskLst, item => { return item.frameNo == 1; });
                    taskLst2 = _.filter(taskLst, item => { return item.frameNo == 2; });
                    taskLst3 = _.filter(taskLst, item => { return item.frameNo == 3; });
                    taskLst4 = _.filter(taskLst, item => { return item.frameNo == 4; }); 
                    taskLst5 = _.filter(taskLst, item => { return item.frameNo == 5; });  

                    self.taskListFrame1(taskLst1);
                    self.taskListFrame2(taskLst2);
                    self.taskListFrame3(taskLst3);
                    self.taskListFrame4(taskLst4);
                    self.taskListFrame5(taskLst5);
                }                
            }).fail((res) => {
                self.$dialog.alert({messageId: res.messageId}).then(() => {
                    self.closeDialog();
                });
            }).always(() => {
                self.$blockui("hide");
            });  
        }
        
        findDetail(employeeId: string, reload: boolean): void {
            const self = this;
            let lstHist: Array<ListHistory> = [], listTaskItem: Array<TaskItemModel> = [],
                task1: string, task2: string, task3: string, task4: string, task5: string;
            self.$blockui("invisible");
            self.$ajax(Paths.GET_TASK_INITIAL_SEL_SETTING + "/" + employeeId).done((dataHist: ITaskInitialSelSettingModel) => {
                if (!_.isNull(dataHist.ids) && !_.isEmpty(dataHist.ids)) {
                    for (let i = 0; i < dataHist.ids.length; i++) {
                        task1 = dataHist.lstTaskItem[i*5];
                        task2 = dataHist.lstTaskItem[i*5 + 1];
                        task3 = dataHist.lstTaskItem[i*5 + 2];
                        task4 = dataHist.lstTaskItem[i*5 + 3];
                        task5 = dataHist.lstTaskItem[i*5 + 4];

                        lstHist.push(new ListHistory(dataHist.ids[i], dataHist.lstStartDate[i] + '～' + dataHist.lstEndDate[i], dataHist.lstStartDate[i], dataHist.lstEndDate[i]));                        

                        listTaskItem.push(new TaskItemModel(dataHist.ids[i], dataHist.lstStartDate[i], dataHist.lstEndDate[i], task1, task2, task3, task4, task5));                        
                    }
                    self.listTaskItemInfo(listTaskItem); 
                    self.listHistPeriod(lstHist);
                    if(!reload){
                        self.selectedHist(self.listHistPeriod()[0].id);
                        self.findTaskItemDetail(self.listHistPeriod()[0].id);
                    }  

                    // self.selectedHist.valueHasMutated(); 
                    $('#startDate').focus();

                } else {
                    self.listHistPeriod([]);
                    self.resetData();                    
                }
            }).always(() => {
                self.$blockui("hide");
            });
        }

        findTaskItemDetail(id: string): void {     
            let self = this, taskItem: TaskItemModel = null;

            if (id === "") return;

            self.enableDeleteBtn(id == self.listHistPeriod()[0].id);
            taskItem = _.find(self.listTaskItemInfo(), itemInfo => itemInfo.id == id );

            self.startDate(taskItem.startDate);
            self.endDate(taskItem.endDate);
            self.startDatePeriod(taskItem.startDate);

            let listTaskFrame1 = self.listTaskFrame1(),
                listTaskFrame2 = self.listTaskFrame2(),
                listTaskFrame3 = self.listTaskFrame3(),
                listTaskFrame4 = self.listTaskFrame4(),
                listTaskFrame5 = self.listTaskFrame5();

            if (!_.isEmpty(listTaskFrame1) && _.isEmpty(_.filter(listTaskFrame1, code => { return code.taskCode === taskItem.task1 }))) {
                if (taskItem.task1 != null && taskItem.task1 != "" && !self.find(taskItem.task1, self.taskListFrame1())) {
                    listTaskFrame1.push(new TaskModel(taskItem.task1, taskItem.task1 + " " + getText("KDW003_81")));                    
                } else {
                    listTaskFrame1.push(_.find(self.taskListFrame1(), x => x.taskCode === taskItem.task1)); 
                }
            }            
            self.listTaskFrame1(listTaskFrame1);
            self.selectedTaskCode1(taskItem.task1);

            if (!_.isEmpty(listTaskFrame2) && _.isEmpty(_.filter(listTaskFrame2, code => { return code.taskCode === taskItem.task2 }))) {
                if (taskItem.task2 != null && taskItem.task2 != "" && !self.find(taskItem.task2, self.taskListFrame2())) {
                    listTaskFrame2.push(new TaskModel(taskItem.task2, taskItem.task2 + " " + getText("KDW003_81")));
                } else {
                    listTaskFrame1.push(_.find(self.taskListFrame2(), x => x.taskCode === taskItem.task2)); 
                }
            }
            self.listTaskFrame2(listTaskFrame2);
            self.selectedTaskCode2(taskItem.task2);

            if (!_.isEmpty(listTaskFrame3) && _.isEmpty(_.filter(listTaskFrame3, code => { return code.taskCode === taskItem.task3 }))) {
                if (taskItem.task3 != null && taskItem.task3 != "" && !self.find(taskItem.task3, self.taskListFrame3())) {
                    listTaskFrame3.push(new TaskModel(taskItem.task3, taskItem.task3 + " " + getText("KDW003_81")));
                } else {
                    listTaskFrame1.push(_.find(self.taskListFrame3(), x => x.taskCode === taskItem.task3)); 
                }         
            }
            self.listTaskFrame3(listTaskFrame3);
            self.selectedTaskCode3(taskItem.task3);

            if (!_.isEmpty(listTaskFrame4) && _.isEmpty(_.filter(listTaskFrame4, code => { return code.taskCode === taskItem.task4 }))) {
                if (taskItem.task4 != null && taskItem.task4 != "" && !self.find(taskItem.task4, self.taskListFrame4())) {
                    listTaskFrame4.push(new TaskModel(taskItem.task4, taskItem.task4 + " " + getText("KDW003_81")));
                } else {
                    listTaskFrame1.push(_.find(self.taskListFrame4(), x => x.taskCode === taskItem.task4)); 
                }
            }
            self.listTaskFrame4(listTaskFrame4);
            self.selectedTaskCode4(taskItem.task4);

            if (!_.isEmpty(listTaskFrame5) && _.isEmpty(_.filter(listTaskFrame5, code => { return code.taskCode === taskItem.task5 }))) {
                if (taskItem.task5 != null && taskItem.task5 != "" && !self.find(taskItem.task5, self.taskListFrame5())) {
                    listTaskFrame5.push(new TaskModel(taskItem.task5, taskItem.task5 + " " + getText("KDW003_81")));
                } else {
                    listTaskFrame1.push(_.find(self.taskListFrame5(), x => x.taskCode === taskItem.task5)); 
                }
            }
            self.listTaskFrame5(listTaskFrame5);
            self.selectedTaskCode5(taskItem.task5);

            $('#startDate').focus();
        }

        getChildTask(taskFrameNo: number, taskCode: string):void {
            const self = this;           
            let request: any = {
                taskFrameNo : taskFrameNo,
                taskCode: taskCode
            };
            let listTask2: Array<TaskModel> = [],
            listTask3: Array<TaskModel> = [],
            listTask4: Array<TaskModel> = [],
            listTask5: Array<TaskModel> = [];
            self.$blockui("invisible");
            self.$ajax(Paths.GET_CHILD_TASK_ITEM_INFO, request).done((data: any) => {
                if (!_.isNull(data)) {                    
                   switch(request.taskFrameNo){
                        case 1:
                            if(_.isEmpty(data)){
                                self.startDate.valueHasMutated();
                            } else {
                                self.listTaskFrame2().splice(0,self.listTaskFrame2().length);
                                _.each(data, task => {
                                    listTask2.push(new TaskModel(task.taskCode, task.taskCode + " " + task.taskName, task.taskName,  task.frameNo, task.startDate, task.endDate));
                                });        
                                listTask2.unshift(self.first);
                                self.listTaskFrame2(listTask2);        
                            }                            
                            
                            break;
                        case 2:
                            if(_.isEmpty(data)){
                                self.startDate.valueHasMutated();
                            } else {
                                self.listTaskFrame3().splice(0,self.listTaskFrame3().length);
                                _.each(data, task => {
                                    listTask3.push(new TaskModel(task.taskCode, task.taskCode + " " + task.taskName, task.taskName,  task.frameNo, task.startDate, task.endDate));
                                });               
                                listTask3.unshift(self.first);
                                self.listTaskFrame3(listTask3);      
                            } 
                            break;
                        case 3:
                            if(_.isEmpty(data)){
                                self.startDate.valueHasMutated();
                            } else {
                                self.listTaskFrame4().splice(0,self.listTaskFrame4().length);
                                _.each(data, task => {
                                    listTask4.push(new TaskModel(task.taskCode, task.taskCode + " " + task.taskName, task.taskName,  task.frameNo, task.startDate, task.endDate));
                                });                     
                                listTask4.unshift(self.first);  
                                self.listTaskFrame4(listTask4); 
                            } 
                            break;
                        case 4:
                            if(_.isEmpty(data)){
                                self.startDate.valueHasMutated();
                            } else {                               
                                self.listTaskFrame5().splice(0,self.listTaskFrame5().length);
                                _.each(data, task => {
                                    listTask5.push(new TaskModel(task.taskCode, task.taskCode + " " + task.taskName, task.taskName,  task.frameNo, task.startDate, task.endDate));
                                });          
                                listTask5.unshift(self.first);
                                self.listTaskFrame5(listTask5);   
                            } 
                            break;
                   }
                }
            }).always(() => {
                self.$blockui("hide");
            });
        }

        resetData(): void {
            let self = this;
            self.startDate(moment(new Date()).format("YYYY/MM/DD"));
            self.startDate.valueHasMutated();
            self.startDatePeriod('');
            self.endDate(self.ENDDATE_LATEST);
            self.selectedTaskCode1("");
            self.selectedTaskCode2("");
            self.selectedTaskCode3("");
            self.selectedTaskCode4("");
            self.selectedTaskCode5("");
            self.selectedHist("");
            self.enableDeleteBtn(false);
            self.enableNewBtn(false);
            $('#taskFrame1').focus();
        }

        registerOrUpdate(): void {
            let self = this; 
            if(self.selectedHist() === "") {
                self.register();
            } else {
                self.update();
            }            
        }

        register(): void {
            let self = this;            
            let command: any = {};
            command.employeeId = self.selectedEmployee();
            command.startDate = moment(self.startDate()).format("YYYY/MM/DD");
            command.endDate = moment(self.endDate()).format("YYYY/MM/DD");
            command.lstTask = [
                self.selectedTaskCode1() === '' ? null : self.selectedTaskCode1(), 
                self.selectedTaskCode2() === '' ? null : self.selectedTaskCode2(), 
                self.selectedTaskCode3() === '' ? null : self.selectedTaskCode3(), 
                self.selectedTaskCode4() === '' ? null : self.selectedTaskCode4(), 
                self.selectedTaskCode5() === '' ? null : self.selectedTaskCode5()
            ]
            self.$blockui("invisible");
            
            self.$ajax(Paths.REGISTER_TASK_INITIAL_SEL_SETTING, command).done(() => {
                self.$dialog.info({ messageId: 'Msg_15' }).then(() => {
                    self.isReload(false);
                    self.findDetail(self.selectedEmployee(), self.isReload());
                    self.enableNewBtn(true);
                });
                
            }).fail((res) => {
                self.$dialog.info({ messageId: res.messageId});
            }).always(() => {
                self.$blockui("hide");
            });               
          
            self.$blockui("hide");
        }

        update(): void {
            let self = this;            
            let command: any = {};
            command.employeeId = self.selectedEmployee();
            command.startDate = moment(self.startDate()).format("YYYY/MM/DD");
            command.endDate = moment(self.endDate()).format("YYYY/MM/DD");
            command.oldStartDate =  moment(self.startDatePeriod()).format("YYYY/MM/DD");
            command.lstTask = [
                self.selectedTaskCode1() === '' ? null : self.selectedTaskCode1(), 
                self.selectedTaskCode2() === '' ? null : self.selectedTaskCode2(), 
                self.selectedTaskCode3() === '' ? null : self.selectedTaskCode3(), 
                self.selectedTaskCode4() === '' ? null : self.selectedTaskCode4(), 
                self.selectedTaskCode5() === '' ? null : self.selectedTaskCode5()
            ]
            self.$blockui("invisible");
            self.$ajax(Paths.UPDATE_TASK_INITIAL_SEL_SETTING, command).done(() => {
                self.$dialog.info({ messageId: 'Msg_15' }).then(() => {
                    self.isReload(true);
                    self.findDetail(self.selectedEmployee(), self.isReload());
                    self.enableNewBtn(true);
                });                
            }).fail((res) => {
                self.$dialog.info({ messageId: res.messageId});
            }).always(() => {
                self.$blockui("hide");
            });
            self.$blockui("hide");
        }

        public remove(): void {
            let self = this;
            self.$dialog.confirm({messageId: "Msg_18"}).then((result: 'no' | 'yes') =>{
                self.$blockui("invisible");
                let command: any = {
                    employeeId: self.selectedEmployee(),
                    startDate : moment(self.startDate()).format("YYYY/MM/DD"),
                    endDate : moment(self.endDate()).format("YYYY/MM/DD"),
                    lstTask : [
                        self.selectedTaskCode1(), 
                        self.selectedTaskCode2(), 
                        self.selectedTaskCode3(), 
                        self.selectedTaskCode4(), 
                        self.selectedTaskCode5()
                    ]
                }
                
                if(result === 'yes'){
                    self.$ajax(Paths.DELETE_TASK_INITIAL_SEL_SETTING, command).done(() =>{
                        self.$dialog.info({messageId: "Msg_16"}).then(() =>{                           
                            // self.selectedHist(self.listHistPeriod()[0].id);
                            self.isReload(false);
                            self.findDetail(self.selectedEmployee(), self.isReload());    
                            self.enableNewBtn(true);
                        });
                    }).fail((res) => {
                        self.$dialog.info({ messageId: res.messageId});
                    }).always(() =>{
                        self.$blockui("hide");
                    });    
                    self.$blockui("hide");                
                }
                if(result === 'no'){
                    self.$blockui("hide");
                }
            });            
        }

        openDialogCDL023() {
            let self = this;
            self.$ajax(Paths.CHECK_SETTING).done((data: any) => {
                let params: IObjectDuplication = {
                    code: self.lstEmployee().filter(i => i.id == self.selectedEmployee()).map(i => i.code)[0],
                    name: self.lstEmployee().filter(i => i.id == self.selectedEmployee()).map(i => i.businessName)[0],
                    targetType: TargetType.WORKPLACE_PERSONAL,
                    itemListSetting: data,
                    // baseDate: moment('YYYY/MM/DD').toDate(),
                    baseDate: new Date()
                };
    
                nts.uk.ui.windows.setShared("CDL023Input", params);
                // open dialog
                nts.uk.ui.windows.sub.modal('com', 'view/cdl/023/a/index.xhtml').onClosed(() => {
                    let prams: any = getShared("CDL023Output");   
                    if (!nts.uk.util.isNullOrUndefined(prams)) {
                        self.copyTaskInitialSelHist(prams, self.selectedEmployee());
                    }
                });
            } );           
        }
        copyTaskInitialSelHist(dataTarget: Array<string>, dataSource: string) {
            let self = this;
            
            let command = {
                empIdSource: dataSource,
                empIdDes: dataTarget
            };
            self.$blockui("invisible");
            self.$ajax(Paths.COPY_TASK_INITIAL_SEL_SETTING, command).done(() => {
                self.$dialog.info({messageId: 'Msg_15'}).then(() => {
                    self.isReload(false);
                    self.findDetail(command.empIdDes[0], self.isReload());
                    let empDes = _.intersection(_.map(self.lstEmployee(), emp => emp.id), dataTarget);
                    if(!_.isEmpty(empDes))
                        self.selectedEmployee(empDes[0]);
                    self.enableNewBtn(true);
                });
            }).fail((error) => {
                self.$dialog.error(error);
            }).always(() => {
                $('#A6_2').focus();
                self.$blockui('hide');
            });
        }

        closeDialog(): void {
            const self = this;
            self.$window.close();
        }
    }

    class EmployeeModel {
        id: string;
        code: string;
        businessName: string;
        depName?: string;
        workplaceName?: string;
        
        constructor(id: string, code: string, businessName: string, depName?: string, workplaceName?: string){
            this.id = id;
            this.code = code;
            this.businessName = businessName;
            this.depName = depName;
            this.workplaceName = workplaceName;
        }
    }

    class TaskItemModel {
        id: string;
        startDate: string;
        endDate: string;
        task1: string;
        task2: string;
        task3: string;
        task4: string;
        task5: string;
        
        constructor(id: string, startDate: string, endDate: string, task1: string, task2: string, task3: string, task4: string, task5: string){
            this.id = id;
            this.startDate = startDate;
            this.endDate = endDate;
            this.task1 = task1;
            this.task2 = task2;
            this.task3 = task3;
            this.task4 = task4;
            this.task5 = task5;
        }
    }

    class TaskModel {
        taskCode: string;
        taskName: string;
        frameNo: number;
        listFrameNoUseAtr: Array<number>;
        listFrameName: Array<string>;
        startDate: string;
        endDate: string;
        displayName: string;
        constructor(taskCode: string, displayName: string, taskName?: string,  frameNo?: number, 
                                    startDate?: string, endDate?: string, frameNoUseAtr?: Array<number>, frameName?: Array<string>){
            this.taskCode = taskCode;
            this.taskName = taskName; 
            this.displayName = displayName;
            this.frameNo = frameNo;        
            this.listFrameNoUseAtr = frameNoUseAtr;  
            this.listFrameName = frameName;
            this.startDate = startDate;
            this.endDate = endDate;           
        }
    }
    
    class ListHistory {
        id: string;
        dateRange: string;
        startDate: string;
        endDate: string;
        constructor(id: string, dateRange: string, startDate: string, endDate: string) {
            this.id = id;
            this.dateRange = dateRange;
            this.startDate = startDate;
            this.endDate = endDate;
        }  
    }
    

    interface ITaskInitialSelSettingModel {
        employeeId: string;
        ids: Array<string>;
        lstStartDate: Array<string>;
        lstEndDate: Array<string>;
        lstTaskItem: Array<string>;
    }

    class TaskInitialSelSettingModel {
        employeeId: KnockoutObservable<string> = ko.observable('');
        ids: KnockoutObservableArray<string> = ko.observableArray([]);
        lstStartDate: KnockoutObservableArray<string> = ko.observableArray([]);
        lstEndDate: KnockoutObservableArray<string> = ko.observableArray([]);
        lstTaskItem: KnockoutObservableArray<string> = ko.observableArray([]);
        constructor(params ?: ITaskInitialSelSettingModel){
            let self = this;
            if(params){
                self.employeeId(params.employeeId);
                self.ids(params.ids);
                self.lstStartDate(params.lstStartDate);
                self.lstEndDate(params.lstEndDate);
                self.lstTaskItem(params.lstTaskItem);
            }
        }
    }

    
    
    export class SystemType {
        static EMPLOYMENT = 1;
        static SALARY = 2;
        static PERSONNEL = 3;
        static ACCOUNTING = 4;
        static OH = 6;
    }

    export enum TargetType {
        // 雇用
        EMPLOYMENT = 1,
        // 分類
        CLASSIFICATION = 2,
        // 職位
        JOB_TITLE = 3,
        // 職場
        WORKPLACE = 4,
        // 部門
        DEPARTMENT = 5,
        // 職場個人
        WORKPLACE_PERSONAL = 6,
        // 部門個人
        DEPARTMENT_PERSONAL = 7,
        // ロール
        ROLE = 8,
        // 勤務種別
        WORK_TYPE = 9,
        //
        WORK = 10
    }

    interface IObjectDuplication {
        code: string;
        name: string;
        targetType: string | number;
        itemListSetting: Array<string>;
        baseDate?: Date; // needed when target type: 職場 or 部門 or 職場個人 or 部門個人
        roleType?: number; // needed when target type: ロール,
        workFrameNoSelection?: number //ver6
    }
    
}