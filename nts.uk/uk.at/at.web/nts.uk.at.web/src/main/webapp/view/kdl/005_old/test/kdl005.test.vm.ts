module kdl005.test {
    export module viewmodel {
        export class ScreenModel {
            date: KnockoutObservable<string>;
            empList: KnockoutObservableArray<string> = ko.observableArray([]);
			

            constructor(){
                var self = this;

                self.date = ko.observable('');
                self.empList = ko.observableArray([]);                
            }
            
            openDialog() {
                var self = this;
                
                var empIds = _.remove(self.empList().length > 0 ? self.empList().split(",") : [], function(n) {
                    return n != "";
                });
                
                var param: IEmployeeParam = {
                    employeeIds: ["xxxxxx000000000003-0004-000000000001","xxxxxx000000000003-0004-000000000001","xxxxxx000000000003-0004-000000000002",
"xxxxxx000000000003-0004-000000000002","xxxxxx000000000003-0004-000000000003","xxxxxx000000000003-0004-000000000004","xxxxxx000000000003-0004-000000000005","xxxxxx000000000003-0004-000000000006",
"xxxxxx000000000003-0004-000000000007","xxxxxx000000000003-0004-000000000008","xxxxxx000000000003-0004-000000000009","xxxxxx000000000003-0004-0000000000010","xxxxxx000000000003-0004-0000000000011"],
                    baseDate: self.date().split("T")[0].replace('-','').replace('-','')
                };
                
                nts.uk.ui.windows.setShared('KDL005_DATA', param);
                
                if(param.employeeIds.length > 1) {
                    nts.uk.ui.windows.sub.modal("/view/kdl/005_old/a/multi.xhtml");
                } else {
                    nts.uk.ui.windows.sub.modal("/view/kdl/005_old/a/single.xhtml");
                }
            }
        }
        
        export class DataParam {
            employeeBasicInfo: Array<EmployeeBasicInfoDto>;
            baseDate: string;
            
            constructor(param: IDataParam) {
                var self = this;
                self.employeeBasicInfo = ko.observable(param.employeeBasicInfo);
                self.baseDate = ko.observable(param.baseDate);
            }
        }
        
        export interface IDataParam {
            employeeBasicInfo: Array<EmployeeBasicInfoDto>;
            baseDate: string;
        }
        
        export class EmployeeBasicInfoDto {
            personId: string;
            employeeId: string;
            businessName: string;
            gender: number;
            birthday: string;
            employeeCode: string;
            jobEntryDate: string;
            retirementDate: string;
            
            constructor(param: IEmployeeBasicInfoDto) {
                var self = this;
                self.personId = ko.observable(param.personId);
                self.employeeId = ko.observable(param.employeeId);
                self.businessName = ko.observable(param.businessName);
                self.gender = ko.observable(param.gender);
                self.birthday = ko.observable(param.birthday);
                self.employeeCode = ko.observable(param.employeeCode);
                self.jobEntryDate = ko.observable(param.jobEntryDate);
                self.retirementDate = ko.observable(param.retirementDate);
            }
        }
        
        export interface IEmployeeBasicInfoDto {
            personId: string;
            employeeId: string;
            businessName: string;
            gender: number;
            birthday: string;
            employeeCode: string;
            jobEntryDate: string;
            retirementDate: string;
        }
        
        export class EmployeeParam {
            employeeIds: Array<string>;
            baseDate: string;
            
            constructor(param: IEmployeeParam) {
                var self = this;
                self.employeeIds = ko.observable(param.employeeIds);
                self.baseDate = ko.observable(param.baseDate);
            }
        }
        
        export interface IEmployeeParam {
            employeeIds: Array<string>;
            baseDate: string;
        }
    }
}