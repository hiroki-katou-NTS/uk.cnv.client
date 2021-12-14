module nts.uk.com.view.cmm051 {
    export module base {
        export class WorkplaceManager {
            wkpId: string;
            employeeId: string;
            startDate: string;
            endDate: string;
            wkpManagerId: string;
            employeeInfo: KnockoutObservable<EmployeeInfo> = ko.observable(null);
            roleList : any = ko.observableArray([]);
            nodeText : string;
            constructor(x: WorkplaceManager) {
                let self = this;
                if (x) {
                    self.wkpId = x.wkpId == '' ? '' : x.wkpId;
                    self.employeeId = x.employeeId == '' ? '' : x.employeeId;
                    self.startDate = x.startDate == '' ? '' : x.startDate;
                    self.endDate = x.endDate == '' ? '' : x.endDate;
                    self.wkpManagerId = x.wkpManagerId == '' ? '' : x.wkpManagerId;
                    self.employeeInfo(new EmployeeInfo(x.employeeInfo));
                    self.roleList(x.roleList || []);
                    self.nodeText = x.startDate + ' ～ ' + x.endDate;
                } else {
                    self.wkpId = '';
                    self.employeeId = '';
                    self.startDate = '';
                    self.endDate = '';
                    self.wkpManagerId = '';
                    self.employeeInfo(new EmployeeInfo(null));
                    self.roleList = [];
                    self.nodeText = '';
                }
            }
        }
        
        export interface IEmployeeInfo {
            employeeId: string;
            employeeCode: string;
            namePerson: string;
        }
        
        export class EmployeeInfo {
            employeeId: string;
            employeeCode: KnockoutObservable<string> = ko.observable('');
            namePerson: string;
            
            constructor(x: IEmployeeInfo) {
                let self = this;
                if (x) {
                    self.employeeId = x.employeeId == '' ? '' : x.employeeId;
                    self.employeeCode(x.employeeCode || '');
                    self.namePerson = x.namePerson == '' ? '' : x.namePerson;
                } else {
                    self.employeeId = '';
                    self.employeeCode('');
                    self.namePerson = '';
                }
            }
        }
        
        export class FunctionPermission {
            functionNo: number;
            initialValue: boolean;
            displayName: string;
            displayOrder: number;
            description: string;
            availability: KnockoutObservable<boolean> = ko.observable(false);
            
            constructor(x: FunctionPermission) {
                let self = this;
                if (x) {
                    self.functionNo = x.functionNo == null ? null : x.functionNo;
                    self.initialValue = x.initialValue;
                    self.displayName = x.displayName == '' ? '' : x.displayName;
                    self.displayOrder = x.displayOrder == null ? null : x.displayOrder;
                    self.description = x.description == '' ? '' : x.description;
                    self.availability(x.availability());
                } else {
                    self.functionNo = null;
                    self.initialValue = false;
                    self.displayName = '';
                    self.displayOrder = null;
                    self.description = '';
                    self.availability(false);
                }
            }
        }
    }
}