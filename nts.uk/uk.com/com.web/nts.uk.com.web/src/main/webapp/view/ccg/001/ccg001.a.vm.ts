module nts.uk.com.view.ccg001.a {  

    import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
    export module viewmodel {
        export class ScreenModel {
           
            ccgcomponent: GroupOption;
            selectedCode: KnockoutObservableArray<string>;
            showinfoSelectedEmployee: KnockoutObservable<boolean>;

            // Options
            isQuickSearchTab: KnockoutObservable<boolean>;
            isAdvancedSearchTab: KnockoutObservable<boolean>;
            isAllReferableEmployee: KnockoutObservable<boolean>;
            isOnlyMe: KnockoutObservable<boolean>;
            isEmployeeOfWorkplace: KnockoutObservable<boolean>;
            isEmployeeWorkplaceFollow: KnockoutObservable<boolean>;
            isMutipleCheck: KnockoutObservable<boolean>;
            isSelectAllEmployee: KnockoutObservable<boolean>;
            periodStartDate: KnockoutObservable<Date>;
            periodEndDate: KnockoutObservable<Date>;
            baseDate: KnockoutObservable<Date>;                
            selectedEmployee: KnockoutObservableArray<EmployeeSearchDto>;
            showEmployment: KnockoutObservable<boolean>; // 雇用条件
            showWorkplace: KnockoutObservable<boolean>; // 職場条件
            showClassification: KnockoutObservable<boolean>; // 分類条件
            showJobTitle: KnockoutObservable<boolean>; // 職位条件
            showWorktype: KnockoutObservable<boolean>; // 勤種条件
            inService: KnockoutObservable<boolean>; // 在職区分
            leaveOfAbsence: KnockoutObservable<boolean>; // 休職区分
            closed: KnockoutObservable<boolean>; // 休業区分
            retirement: KnockoutObservable<boolean>; // 退職区分
            systemType: KnockoutObservable<number>;
            showClosure: KnockoutObservable<boolean>; // 就業締め日利用
            showBaseDate: KnockoutObservable<boolean>; // 基準日利用
            showAllClosure: KnockoutObservable<boolean>; // 全締め表示
            showPeriod: KnockoutObservable<boolean>; // 対象期間利用
            periodAccuracy: KnockoutObservable<number>; // 対象期間精度

            constructor() {
                var self = this;
                self.selectedCode = ko.observableArray([]);
                self.selectedEmployee = ko.observableArray([]);
                self.showinfoSelectedEmployee = ko.observable(false);
                
                // set ccg options
                self.setCcgOption();
                
                // Init component.
                self.applyView();
                
                self.isQuickSearchTab.subscribe(function(){
                   self.applyView(); 
                });
                
                self.isAdvancedSearchTab.subscribe(function(){
                    self.applyView();    
                });
                
                self.isAllReferableEmployee.subscribe(function() {
                    self.applyView();
                });
                
                
                self.isOnlyMe.subscribe(function(){
                    self.applyView();   
                });
                
                self.isEmployeeOfWorkplace.subscribe(function(){
                   self.applyView(); 
                });
                
                self.isEmployeeWorkplaceFollow.subscribe(function(){
                   self.applyView(); 
                });
                
                self.isMutipleCheck.subscribe(function(){
                    self.applyView();
                });
                
                self.isSelectAllEmployee.subscribe(function(){
                   self.applyView(); 
                });
            }

            private setCcgOption(): void {
                let self = this;
                self.isQuickSearchTab = ko.observable(true);
                self.isAdvancedSearchTab = ko.observable(true);
                self.isAllReferableEmployee = ko.observable(true);
                self.isOnlyMe = ko.observable(true);
                self.isEmployeeOfWorkplace = ko.observable(true);
                self.isEmployeeWorkplaceFollow = ko.observable(true);
                self.isMutipleCheck = ko.observable(true);
                self.isSelectAllEmployee = ko.observable(true);
                self.baseDate = ko.observable(new Date());
                self.periodStartDate = ko.observable(new Date());
                self.periodEndDate = ko.observable(new Date());
                self.showEmployment = ko.observable(true); // 雇用条件
                self.showWorkplace = ko.observable(true); // 職場条件
                self.showClassification = ko.observable(true); // 分類条件
                self.showJobTitle = ko.observable(true); // 職位条件
                self.showWorktype = ko.observable(true); // 勤種条件
                self.inService = ko.observable(true); // 在職区分
                self.leaveOfAbsence = ko.observable(true); // 休職区分
                self.closed = ko.observable(true); // 休業区分
                self.retirement = ko.observable(true); // 退職区分
                self.systemType = ko.observable(1);
                self.showClosure = ko.observable(true); // 就業締め日利用
                self.showBaseDate = ko.observable(true); // 基準日利用
                self.showAllClosure = ko.observable(true); // 全締め表示
                self.showPeriod = ko.observable(true); // 対象期間利用
                self.periodAccuracy = ko.observable(1); // 対象期間精度
            }

            /**
             * apply view
             */
            public applyView(): void {
                var self = this;
                self.baseDate(new Date());
                self.ccgcomponent = {
                    /** Common properties */
                    systemType: self.systemType(), // システム区分
                    isSelectAllEmployee: self.isSelectAllEmployee(), // 検索タイプ
                    isQuickSearchTab: self.isQuickSearchTab(), // クイック検索
                    isAdvancedSearchTab: self.isAdvancedSearchTab(), // 詳細検索
                    showBaseDate: self.showBaseDate(), // 基準日利用
                    showClosure: self.showClosure(), // 就業締め日利用
                    showAllClosure: self.showAllClosure(), // 全締め表示
                    showPeriod: self.showPeriod(), // 対象期間利用
                    periodAccuracy: self.periodAccuracy(), // 対象期間精度

                    /** Required parameter */
                    baseDate: self.baseDate().toString(), // 基準日
                    periodStartDate: self.periodStartDate().toString(), // 対象期間開始日
                    periodEndDate: self.periodEndDate().toString(), // 対象期間終了日
                    inService: self.inService(), // 在職区分
                    leaveOfAbsence: self.leaveOfAbsence(), // 休職区分
                    closed: self.closed(), // 休業区分
                    retirement: self.retirement(), // 退職区分

                    /** Quick search tab options */
                    isAllReferableEmployee: self.isAllReferableEmployee(), // 参照可能な社員すべて
                    isOnlyMe: self.isOnlyMe(), // 自分だけ
                    isEmployeeOfWorkplace: self.isEmployeeOfWorkplace(), // 同じ職場の社員
                    isEmployeeWorkplaceFollow: self.isEmployeeWorkplaceFollow(), // 同じ職場とその配下の社員

                    /** Advanced search properties */
                    showEmployment: self.showEmployment(), // 雇用条件
                    showWorkplace: self.showWorkplace(), // 職場条件
                    showClassification: self.showClassification(), // 分類条件
                    showJobTitle: self.showJobTitle(), // 職位条件
                    showWorktype: self.showWorktype(), // 勤種条件
                    isMutipleCheck: self.isMutipleCheck(), // 選択モード

                    onSearchAllClicked: function(dataList: EmployeeSearchDto[]) {
                        self.showinfoSelectedEmployee(true);
                        self.selectedEmployee(dataList);
                    },
                    onSearchOnlyClicked: function(data: EmployeeSearchDto) {
                        self.showinfoSelectedEmployee(true);
                        var dataEmployee: EmployeeSearchDto[] = [];
                        dataEmployee.push(data);
                        
                        
                        self.selectedEmployee(dataEmployee);
                    },
                    onSearchOfWorkplaceClicked: function(dataList: EmployeeSearchDto[]) {
                        self.showinfoSelectedEmployee(true);
                        self.selectedEmployee(dataList);
                    },

                    onSearchWorkplaceChildClicked: function(dataList: EmployeeSearchDto[]) {
                        self.showinfoSelectedEmployee(true);
                        self.selectedEmployee(dataList);
                    },
                    onApplyEmployee: function(dataEmployee: EmployeeSearchDto[]) {
                        self.showinfoSelectedEmployee(true);
                        self.selectedEmployee(dataEmployee);
                    }

                }
                $('#ccgcomponent').ntsGroupComponent(self.ccgcomponent);
            }
            
            
        }
    }
}