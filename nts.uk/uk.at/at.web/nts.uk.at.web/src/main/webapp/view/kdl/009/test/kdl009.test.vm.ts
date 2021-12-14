module kdl009.test {
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
                    employeeIds: empIds,
                    baseDate: self.date().split("T")[0].replace('-','').replace('-','')
                };
                
                nts.uk.ui.windows.setShared('KDL009_DATA', param);
                
                if(param.employeeIds.length > 1) {
                    nts.uk.ui.windows.sub.modal("/view/kdl/009/a/multi.xhtml");
                } else {
                    nts.uk.ui.windows.sub.modal("/view/kdl/009/a/single.xhtml");
                }             
            }
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