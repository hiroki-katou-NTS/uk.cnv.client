module nts.uk.at.view.kdl053 {
    import getText = nts.uk.resource.getText;
    import getShared = nts.uk.ui.windows.getShared;

    const Paths = {
        GET_ATENDANCENAME_BY_IDS:"at/record/attendanceitem/daily/getattendnamebyids",
        EXPORT_CSV:"screen/at/kdl053/exportCsv"
    };
    
    @bean()
    class Kdl053ViewModel extends ko.ViewModel {		
        period: string = "";
        isRegistered: KnockoutObservable<boolean> = ko.observable(true);
        registrationErrorList: KnockoutObservable<any> = ko.observable();
        constructor(params: any) {
            super();
            const self = this;
            self.loadScheduleRegisterErr(); 
        }
        loadScheduleRegisterErr(): void {
            const self = this;

            let data = getShared('dataShareDialogKDL053');
            let errorRegistrationList = data.errorRegistrationList;
            let employeeIds = data.employeeIds;
            self.isRegistered(data.isRegistered == 1);        
            _.each(errorRegistrationList, errorLog =>{
                switch (self.getDayfromDate(errorLog.date)){
                    case 0: 
                        errorLog.date = '<span style="color:red">'+ errorLog.date +'(日)' + '</span>';
                        break;
                    case 1: 
                        errorLog.date = '<span>'+ errorLog.date +'(月)'+ '</span>';
                        break;
                    case 2: 
                        errorLog.date = '<span>'+ errorLog.date +'(火)'+ '</span>';
                        break;
                    case 3: 
                        errorLog.date = '<span>'+ errorLog.date +'(水)'+ '</span>';
                        break;
                    case 4: 
                        errorLog.date = '<span>'+ errorLog.date +'(木)'+ '</span>';
                        break;
                    case 5: 
                        errorLog.date = '<span>'+ errorLog.date +'(金)'+ '</span>';
                        break;
                    case 6: 
                        errorLog.date = '<span style="color:blue">'+ errorLog.date +'(土)' + '</span>';
                        break;                    
                }
            })
            let listIds: Array<any> = _.map(errorRegistrationList, item => { return item.errId });            
            this.$blockui("invisible");
            self.$ajax(Paths.GET_ATENDANCENAME_BY_IDS,listIds).done((data: Array<any>)=>{
                if(data && data.length > 0){
                    let index = 0, idx = 0;                   
                    _.each(errorRegistrationList, item => {
                        item.id = idx;
                        idx++;
                        _.each(data, itemName =>{
                            if(item.errId == itemName.attendanceItemId ){
                                errorRegistrationList[index].errName = itemName.attendanceItemName;
                                index++;
                            }                           
                        })
                    })      
                    self.registrationErrorList(errorRegistrationList);             
                }                
                $("#grid").igGrid({
                    width: "780px",
                    height: "330px",
                    dataSource: self.registrationErrorList(),
                    dataSourceType: "json",
                    primaryKey: "id",
                    autoGenerateColumns: false,                        
                    responseDatakey: "results",
                    columns: [
                        { headerText: "STT", key: "id", dataType: "number", hidden: true },
                        { headerText: getText('KDL053_5'), key: "employeeCdName", dataType: "string", width: "25%" },                          
                        { headerText: getText('KDL053_6'), key: "date", dataType: "string", width: "16%" },
                        { headerText: getText('KDL053_7'), key: "errName", dataType: "string", width: "18%" },
                        { headerText: getText('KDL053_8'), key: "errMsg", width:"35%" }                                   
                    ],
                    features: [
                        {
                            name : 'Paging',
                            type: "local",
                            pageSize : 10
                        }
                    ]
                });
            })
            .always(() => {
                self.$blockui("clear");
            });
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

    interface IScheduleRegisterErr {
        /** コード／名称*/
        employeeCdName: string;

        date: string;

        errName: string;

        errMsg: string;
       
    }

    class ScheduleRegisterErr{
        employeeCdName: string;
        date: string;
        errName: string;
        errMsg: string;
        // constructor(scheduleRegisterErr: IScheduleRegisterErr) {
        //     this.employeeCdName = scheduleRegisterErr.employeeCdName;
        //     this.date = scheduleRegisterErr.date;
        //     this.errName = scheduleRegisterErr.errName;
        //     this.errMsg = scheduleRegisterErr.errMsg;
        // }

        constructor(employeeCdName: string, date: string, errName: string, errMsg: string) {
            this.employeeCdName = employeeCdName;
            this.date = date;
            this.errName = errName;
            this.errMsg = errMsg;
        }
    }
}