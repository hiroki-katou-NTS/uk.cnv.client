module nts.uk.com.view.cas012.c.viewmodel {
    export class ScreenModel {
        checkSetAdminFlag: KnockoutObservable<boolean>;
        listCompany: KnockoutObservableArray<any>;
        selectedCompany: KnockoutObservable<string>;
        isRequired :  KnockoutObservable<boolean>;
        constructor() {
            let self = this;
            self.checkSetAdminFlag = ko.observable(false);
            self.isRequired = ko.observable(false);
            self.listCompany = ko.observableArray([]);
            self.selectedCompany = ko.observable("");
            self.checkSetAdminFlag.subscribe(function (value) {
                self.isRequired(value);
            });
            nts.uk.ui.windows.setShared('CAS012CResult', {
                decisionCompanyID: null,
                setRoleAdminFlag: false,
                isCancel : true
            });
        }

        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getAll().done(function(data) {
                self.listCompany(data.listCompany);
                self.selectedCompany(data.listCompany[0].companyId);
                dfd.resolve();    
            });
            return dfd.promise();
        }
        
        decision() {
            let self = this;
            let dataSetShare = {
                decisionCompanyID: self.selectedCompany(),
                setRoleAdminFlag: self.checkSetAdminFlag(),
                isCancel : false  
            };
            nts.uk.ui.windows.setShared('CAS012CResult', dataSetShare);
            nts.uk.ui.windows.close();
        } 

        cancel_Dialog(): any {
            let self = this;
            nts.uk.ui.windows.setShared('CAS012CResult', {
                decisionCompanyID: null,
                setRoleAdminFlag: false,
                isCancel : true 
            });
            nts.uk.ui.windows.close();
        }
    }
}