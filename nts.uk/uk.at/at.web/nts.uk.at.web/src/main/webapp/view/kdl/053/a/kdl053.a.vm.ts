module nts.uk.at.view.kdl053.a {
    import getText = nts.uk.resource.getText;
    import getShared = nts.uk.ui.windows.getShared;

    const Paths = {
        GET_ATENDANCENAME_BY_IDS:"at/record/attendanceitem/daily/getattendnamebyids",
        EXPORT_CSV:"screen/at/kdl053/exportCsv"
    };
    
    @bean()
    class Kdl053ViewModel extends ko.ViewModel {
        hasError: KnockoutObservable<boolean> = ko.observable(true);
        registrationErrorList: KnockoutObservable<any> = ko.observable([]);
        registrationErrorListCsv: KnockoutObservable<any> = ko.observable([]);
        dispItemCol: true;
        constructor(private params: any) {
            super();
            const self = this;
            self.loadScheduleRegisterErr(); 
        }
        mounted(){
            const self = this;
            if(self.hasError()){
                self.$window.size(560, 820);
            } else {
                self.$window.size(500, 820);
            }
        }

        loadScheduleRegisterErr(): void {
            const self = this;
            let errorRegistrationListTmp: any;
            let employeeIds: any;
            let errorRegistrationList: any = [], errorRegistrationListCsv: any =[];
            let countNo: number = 1;
            let data: any;
            
            if (getShared('dataShareDialogKDL053')) {
                data = getShared('dataShareDialogKDL053');
            } else if (self.params) {
                data = self.params
            }

            if (_.isNull(data) || _.isEmpty(data)) {
                self.closeDialog();
            }
            if (!nts.uk.util.isNullOrUndefined(data.dispItemCol)) {
                self.dispItemCol = data.dispItemCol;
            } else {
                self.dispItemCol = true;
            }
			if(_.isNil(data.errorRegistrationList)){
				if(!_.isNil(data.dataShareDialogKDL053.errorRegistrationList)) {
                    errorRegistrationListTmp = data.dataShareDialogKDL053.errorRegistrationList;
                }
				employeeIds = data.dataShareDialogKDL053.employeeIds;
			} else {
				errorRegistrationListTmp = data.errorRegistrationList;
				employeeIds = data.employeeIds;
			}
              
            _.each(employeeIds, id =>{      
                let temp: any = [];
                _.each(errorRegistrationListTmp, err => {                   
                    if(err.sid == id) {                        
                        err.employeeCdName = err.scd + " " + err.empName;
                        temp.push(err);
                    }
                })               
                errorRegistrationList = _.union(errorRegistrationList, _.sortBy(temp, item => item.date));
            });
            errorRegistrationList = _.sortBy(errorRegistrationList, ['scd', 'date'])

            self.hasError(data.isRegistered == 1); 

            _.each(errorRegistrationList, errorLog => {
                errorLog.id = countNo;
                switch (self.getDayfromDate(errorLog.date)) {
                    case 0:
                        errorLog.dateCss = '<span>' + errorLog.date + '<span style="color:red">' + " (" + moment.weekdaysShort(0) + ')</span></span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(0) + ")";
                        break;
                    case 1:
                        errorLog.dateCss = '<span>' + errorLog.date + " (" + moment.weekdaysShort(1) + ')</span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(1) + ")";
                        break;
                    case 2:
                        errorLog.dateCss = '<span>' + errorLog.date + " (" + moment.weekdaysShort(2) + ')</span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(2) + ")";
                        break;
                    case 3:
                        errorLog.dateCss = '<span>' + errorLog.date + " (" + moment.weekdaysShort(3) + ')</span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(3) + ")";
                        break;
                    case 4:
                        errorLog.dateCss = '<span>' + errorLog.date + " (" + moment.weekdaysShort(4) + ')</span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(4) + ")";
                        break;
                    case 5:
                        errorLog.dateCss = '<span>' + errorLog.date + " (" + moment.weekdaysShort(5) + ')</span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(5) + ")";
                        break;
                    case 6:
                        errorLog.dateCss = '<span>' + errorLog.date + '<span style="color:blue">'+ " (" + moment.weekdaysShort(6) + ')</span></span>';
                        errorLog.dateCsv =  errorLog.date + " (" + moment.weekdaysShort(6) + ")";
                        break;
                }
                countNo = countNo + 1;
            })

            self.registrationErrorListCsv(errorRegistrationList);
            self.registrationErrorList(errorRegistrationList);

            if (self.dispItemCol) {
                //勤怠項目に対応する名称を生成する
                this.$blockui("invisible");
                let listIds: Array<any> = _.map(errorRegistrationList, item => { return item.attendanceItemId });
                self.$ajax(Paths.GET_ATENDANCENAME_BY_IDS, listIds).done((data: Array<any>) => {
                    if (data && data.length > 0) {
                        let index = 0, idx = 0;
                        _.each(errorRegistrationList, item => {
                            idx++;
                            _.each(data, itemName => {
                                if (item.attendanceItemId == itemName.attendanceItemId) {
                                    errorRegistrationList[index].errName = itemName.attendanceItemName;
                                    index++;
                                }
                            })
                        })
                        self.registrationErrorListCsv(errorRegistrationList);
                        self.registrationErrorList(errorRegistrationList);
                    } else {
                        _.each(errorRegistrationList, item => {
                            item.errName = "";
                        })
                        self.registrationErrorListCsv(errorRegistrationList);
                        self.registrationErrorList(errorRegistrationList);
                    }
                    self.initGrid();
                    self.$blockui("hide");
                }).always(() => {
                        self.$blockui("hide");
                    });
            } else {
                self.initGrid();
            }
        }

        initGrid(): void {
            let self = this;
            let gridColumns = [
                {headerText: "", key: "id", dataType: "string", hidden: true},
                {headerText: getText('KDL053_5'), key: "employeeCdName", dataType: "string", width: "30%"},
                {headerText: getText('KDL053_6'), key: "dateCss", dataType: "string", width: "16%"},
                {headerText: getText('KDL053_7'), key: "errName", dataType: "string", width: "18%"},
                {headerText: getText('KDL053_8'), key: "errorMessage", width: "34%"}
            ];
            if (!self.dispItemCol) {
                gridColumns.splice(3, 1);
            }
            $("#grid").igGrid({
                width: "780px",
                height: "330px",
                dataSource: self.registrationErrorList(),
                dataSourceType: "json",
                primaryKey: "id",
                autoGenerateColumns: false,
                responseDatakey: "results",
                columns: gridColumns,
                features: [
                    {
                        name: 'Paging',
                        type: "local",
                        pageSize: 10
                    },
                    {
                        name: 'Resizing',
                        columnSettings: [
                            {columnKey: "employeeCdName", allowResizing: true},
                            {columnKey: "dateCss", allowResizing: true},
                            {columnKey: "errName", allowResizing: true},
                            {columnKey: "errorMessage", allowResizing: true}
                        ],
                    }
                ]
            });
            $('#btnClose').focus();
        }

        exportCsv(): void {
            const self = this;
            self.$blockui("invisible"); 
            nts.uk.request.exportFile(Paths.EXPORT_CSV, self.registrationErrorList()).always(() => {
                self.$blockui("clear");
            });
        }

        closeDialog(): void {
             const vm = this;
             vm.$window.close();
        }

        getDayfromDate(fromDate: string): number {
            let date = new Date(fromDate);
            return date.getDay();
        }        
    }
}