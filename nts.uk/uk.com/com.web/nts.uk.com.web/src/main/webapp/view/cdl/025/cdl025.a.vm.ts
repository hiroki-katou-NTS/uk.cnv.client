module nts.uk.com.view.cdl025.a {
    import getText = nts.uk.resource.getText;
    import ccg = nts.uk.com.view.ccg025.a;
    import model = nts.uk.com.view.ccg025.a.component.model;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    
    export module viewmodel {
        export class ScreenModel {
            component: ccg.component.viewmodel.ComponentModel;
            roleType: number;
            multiple: boolean;
            currentCode: any;
            roleAtr : number;
            
            private searchMode: string;
            
            constructor() {
                let self = this;
                let param = nts.uk.ui.windows.getShared("paramCdl025"); 
                if(!nts.uk.util.isNullOrUndefined(param)){
                    self.roleType = param.roleType;
                    self.multiple = param.multiple;
                    self.currentCode = param.currentCode;
                    self.roleAtr = param.roleAtr;
                }
                
                self.component = new ccg.component.viewmodel.ComponentModel({ 
                    roleType: self.roleType,
                    multiple: self.multiple,
                    selectedId: self.currentCode,
                    showEmptyItem: true,
                    roleAtr : self.roleAtr,
                    onDialog: true
                });
            }

            /** Start page */
            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                self.component.startPage().done(function(){
                    dfd.resolve();    
                });
                return dfd.promise();
            }//end start page
            
            /** btn decision*/
            decision(){
                let self = this;
                nts.uk.ui.windows.setShared("dataCdl025", self.component.currentRoleId());
                nts.uk.ui.windows.close();
            }
            
            /** btn cancel*/
            cancel(){
                nts.uk.ui.windows.close();
            }
            
        }//end screenModel
    }//end viewmodel
}//end module