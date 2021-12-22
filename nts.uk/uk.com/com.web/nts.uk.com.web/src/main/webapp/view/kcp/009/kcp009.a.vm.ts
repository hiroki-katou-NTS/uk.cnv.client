module kcp009.a.viewmodel {
    import ComponentOption = kcp009.viewmodel.ComponentOption;
    import EmployeeModel = kcp009.viewmodel.EmployeeModel;
    import SystemType = kcp009.viewmodel.SystemType;
    
    export class ScreenModel {
        empList: KnockoutObservableArray<EmployeeModel>;
        systemType: KnockoutObservable<number>;
        isDisplayOrganizationName: KnockoutObservable<boolean>;
        setBaseDate: KnockoutObservable<boolean>;
        baseDate: KnockoutObservable<Date>;
        targetBtnText: string;
        isDisplayNumberOfEmployee: KnockoutObservable<boolean>;

        systemReferenceList: KnockoutObservableArray<any>;
        selectedSystem: KnockoutObservable<number>;
        
        listComponentOption: ComponentOption;
        listComponentOption1: ComponentOption;
        selectedItem: KnockoutObservable<string>;
        selectedItem1: KnockoutObservable<string>;
        tabindex: number;
        
        isLoading: boolean;
        
        constructor() {
            let self = this;
            self.isLoading = true;
//            self.empList = ko.observableArray([]);
            self.empList = ko.observableArray([
            {id: '000426a2-181b-4c7f-abc8-6fff9f4f983a', code: '000000000001', businessName: 'Test 1', workplaceName: 'Webメニューの設定', depName: '部門2'},
            {id: '90000000-0000-0000-0000-000000000001', code: '000000000002', businessName: 'Test 2', workplaceName: 'テスト情報', depName: '部門3'},
            {id: '90000000-0000-0000-0000-000000000002', code: '000000000003', businessName: 'Test 3', workplaceName: 'カレンダーの登録', depName: '部門4'},
            {id: '90000000-0000-0000-0000-000000000003', code: '000000000004', businessName: 'Test 4', workplaceName: 'カレンダーの登録', depName: '部門5'},
            {id: '90000000-0000-0000-0000-000000000004', code: '000000000005', businessName: 'Test 5', workplaceName: 'ベトナムレビュー', depName: '部門6'},
            {id: '90000000-0000-0000-0000-000000000005', code: '000000000006', businessName: 'Test 6', workplaceName: '雇用名称', depName: '部門7'},
            {id: '90000000-0000-0000-0000-000000000009', code: '000000000007', businessName: 'Test 7', workplaceName: '外部予算実績受入職場ID', depName: '部門8'},
            {id: '90000000-0000-0000-0000-000000000007', code: '000000000008', businessName: 'Test 8', workplaceName: '算実績受入年月日', depName: '部門9'},
            {id: '90000000-0000-0000-0000-000000000008', code: '000000000009', businessName: 'Test 9', workplaceName: '個人情報レイアウトコード', depName: '部門10'},
            {id: '90000000-0000-0000-0000-000000000010', code: '000000000010', businessName: 'Test 10', workplaceName: '個人情報レイアウト名称', depName: '部門11'},
            {id: '90000000-0000-0000-0000-000000000011', code: '000000000011', businessName: 'Test 11', workplaceName: '個人情報', depName: '部門12' },
            {id: '90000000-0000-0000-0000-000000000012', code: '000000000012', businessName: 'Test 12', workplaceName: '名称', depName: '部門13'},
            {id: '90000000-0000-0000-0000-000000000013', code: '000000000013', businessName: 'Test 13', workplaceName: '個人情報', depName: '部門14'}]);
            self.systemType = ko.observable(SystemType.EMPLOYMENT);
            self.isDisplayOrganizationName = ko.observable(true);
            self.isDisplayOrganizationName.subscribe(function(value: boolean) {
                self.reloadComponent();
            });
            
            self.setBaseDate = ko.observable(true);
            self.setBaseDate.subscribe(function(value: boolean) {
                self.reloadComponent();
            });
            
            self.baseDate = ko.observable(moment(new Date()).toDate());
//            self.baseDate.subscribe(function(value: Date) {
//                if (!self.isLoading) {
//                    self.reloadComponent();
//                }
//            });

            self.isDisplayNumberOfEmployee = ko.observable(true);
            self.isDisplayNumberOfEmployee.subscribe(() => self.reloadComponent());
            
            self.targetBtnText = nts.uk.resource.getText("KCP009_3");
            self.selectedItem = ko.observable(null);
            self.selectedItem1 = ko.observable(null);
            self.systemReferenceList = ko.observableArray([
                { code: 1, name: 'Employment System' },
                { code: 2, name: 'Other Systems' },
            ]);
            self.selectedSystem = ko.observable(1);
            self.selectedSystem.subscribe(function(value: number) {
                if (value == 1) {
                    // System Type = Employment
                    self.systemType(SystemType.EMPLOYMENT);
                } else {
                    // Other System Type
                    self.systemType(SystemType.SALARY);
                }
                // Reload Component
                self.reloadComponent();
            });
            self.tabindex = 1;
            
            // Initial listComponentOption
            self.listComponentOption = {
                systemReference: self.systemType(),
                isDisplayOrganizationName: self.isDisplayOrganizationName(),
                employeeInputList: self.empList,
                targetBtnText: self.targetBtnText,
                selectedItem: self.selectedItem,
                tabIndex: self.tabindex,
                baseDate: self.baseDate,
                isDisplayNumberOfEmployee: self.isDisplayNumberOfEmployee()
            };
            
            // Initial listComponentOption
            self.listComponentOption1 = {
                systemReference: self.systemType(),
                isDisplayOrganizationName: self.isDisplayOrganizationName(),
                employeeInputList: self.empList,
                targetBtnText: self.targetBtnText,
                selectedItem: self.selectedItem1,
                tabIndex: self.tabindex,
                baseDate: self.baseDate,
                isDisplayNumberOfEmployee: self.isDisplayNumberOfEmployee()
            };
            
            self.isLoading = false;
            
        }
        
        // Reload component
        private reloadComponent() {
            let self = this;
            self.listComponentOption.systemReference = self.systemType();
            self.listComponentOption.isDisplayOrganizationName = self.isDisplayOrganizationName();
            if(self.setBaseDate()) {
               self.listComponentOption.baseDate = self.baseDate;
            } else {
                self.listComponentOption.baseDate = null;
            }
            self.listComponentOption.targetBtnText = self.targetBtnText;
            self.listComponentOption.employeeInputList(self.empList());
            self.listComponentOption.isDisplayNumberOfEmployee = self.isDisplayNumberOfEmployee();
            // Load listComponent
            $('#emp-component').ntsLoadListComponent(self.listComponentOption);
            
            self.listComponentOption1.systemReference = self.systemType();
            self.listComponentOption1.isDisplayOrganizationName = self.isDisplayOrganizationName();
            if(self.setBaseDate()) {
               self.listComponentOption1.baseDate = self.baseDate;
            } else {
                self.listComponentOption.baseDate = null;
            }
            self.listComponentOption1.targetBtnText = self.targetBtnText;
            self.listComponentOption1.employeeInputList(self.empList());
            self.listComponentOption1.isDisplayNumberOfEmployee = self.isDisplayNumberOfEmployee();
            // Load listComponent
            $('#emp-component1').ntsLoadListComponent(self.listComponentOption1);
        }
        
    }

}
