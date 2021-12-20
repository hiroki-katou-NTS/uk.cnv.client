module nts.uk.at.view.kal001.c {
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
     import block = nts.uk.ui.block;
    export module viewmodel {
        export class ScreenModel {
            //table
            columns: Array<any>;//nts.uk.ui.NtsGridListColumn
            //data
            listEmployee: Array<modeldto.EmployeeSendEmail> = [];
            processId : string; 
            
            constructor(processId: string) {
                let self = this;
                self.processId  = processId;                                                                                
            }

            /**
             * functiton start pagea
             */
            startPage(): JQueryPromise<any>  {
                let self = this;
                let dfd = $.Deferred(); 
                self.columns = [
                    { headerText: '',  dataType: 'string', key: 'GUID' },
                    { headerText: getText('KAL001_23'),  dataType: 'boolean', key: 'isSendToMe', showHeaderCheckbox: true, width: "100", ntsControl: 'isSendToMe' },
                    { headerText: getText('KAL001_24'),  dataType: 'boolean',  key: 'isSendToManager', showHeaderCheckbox: true, width: "100", ntsControl: 'isSendToManager' },
                    { headerText: getText('KAL001_27'), key: 'workplaceName', width: "138" },
                    { headerText: getText('KAL001_25'), key: 'employeeCode', width: "138" },
                    { headerText: getText('KAL001_26'), key: 'employeeName', width: "204" }
                ];
                
                service.getEmployeeSendEmail(self.processId).done((listEmployeeDto: Array<modeldto.EmployeeDto>) => {
                    _.map(listEmployeeDto, (e) => {
                        self.listEmployee.push(new modeldto.EmployeeSendEmail(e));
                    });
                    // create table
                    $("#grid").ntsGrid({
                        width : '700px',
                        height: 380,
                        dataSource: self.listEmployee,
                        hidePrimaryKey: true,
                        primaryKey: 'GUID',
                        virtualization: true,
                        rowVirtualization: true,
                        virtualizationMode: 'continuous',
                        columns: self.columns,
                        features: [],

                        ntsControls: [
                            { name: 'isSendToMe', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                            { name: 'isSendToManager', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                        ]
                    });
                    dfd.resolve();
                }).fail((error) => {
                    alertError(error);
                });
                return dfd.promise();                
            }//end start page 

             closeDialog(){
                 nts.uk.ui.windows.close();
            }
            
            //Start send mail
            sendEmail(): JQueryPromise<any> {
                let self = this;
                self.doSendEmail().done(function() { });
            }
            
            doSendEmail(){
                 let self = this;
                 let dfd = $.Deferred<any>();
                 block.grayout();
                
                 // check status
                 let isHaveChecked = _.find(self.listEmployee, item => item.isSendToMe || item.isSendToManager) !== undefined;
                 
                 if(isHaveChecked){
                     let listEmployeeSendTaget = [], listManagerSendTaget = [];
                     let listManager : Array<modeldto.ManagerTagetDto> = [];
                     _.forEach(self.listEmployee, function(item: modeldto.EmployeeSendEmail) {
                        if (item.isSendToMe == true ) {
                             //set to employeee list taget
                             listEmployeeSendTaget.push(item.employeeId);
                         }
                         if (item.isSendToManager == true) {
                             //set to Manager list taget
                             listManagerSendTaget.push(item.employeeId);
                             listManager.push(new modeldto.ManagerTagetDto(item.employeeId, item.workplaceId));
                         }
                     });
                     let params ={
                       listEmployeeSendTaget: listEmployeeSendTaget,
                       listManagerSendTaget: listManagerSendTaget,
                       processId: self.processId,
                         listManagerSelected : listManager
                     };
                     // call service send mail
                     service.alarmListSendEmail(params).done(function(data: string) {
                        if(data.length > 0){
                            info({ message: data });
                            block.clear();
//                         let returnParam = data.split(";");
//                         let isSendMailError = returnParam[0];
//                         let errorStr = returnParam[1];
//                         if (errorStr.length > 0) {
////                             let strDisplay = nts.uk.resource.getMessage('Msg_965') + "<br/>" + errorStr;
//                             info({ message: strDisplay });
//                             block.clear();
//                         } else {
//                             info({ messageId: 'Msg_207' });
//                             block.clear();
//                         }
                        }else {
                            info({ messageId: 'Msg_207' });
                            block.clear();
                        }
                     }).always(() => {
                         block.clear();
                     }).fail(function(error) {
                         alertError(error);
                         block.clear();
                         dfd.resolve();
                     });
                 } else {
                     alertError({ messageId: 'Msg_657' });
                     block.clear();
                 }     
                 return dfd.promise();
            }

        }//end screenModel
    }//end viewmodel  

    //module model
    export module modeldto {

        export class EmployeeSendEmail {
            GUID: string;
            isSendToMe:  boolean;
            isSendToManager: boolean;
            workplaceId: string;
            workplaceName: string;
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            constructor(e: EmployeeDto ) {
                this.GUID = nts.uk.util.randomId().replace(/-/g, "_");
                this.isSendToMe = false;
                this.isSendToManager = false;
                this.workplaceId = e.workplaceId;
                this.workplaceName = e.workplaceName;
                this.employeeId = e.employeeId;
                this.employeeCode = e.employeeCode;
                this.employeeName = e.employeeName;
            }

        }

        export class ManagerTagetDto {
            employeeID: string;
            workplaceID: string;

            constructor(employeeID: string, workplaceID: string) {
                this.employeeID = employeeID;
                this.workplaceID = workplaceID;
            }
        }
        
        export interface EmployeeDto{
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            workplaceId: string;
            workplaceName: string;   
        }
        
    }//end module model
    
}//end module