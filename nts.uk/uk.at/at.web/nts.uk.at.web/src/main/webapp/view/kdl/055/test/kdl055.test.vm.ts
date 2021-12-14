/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
import setShared = nts.uk.ui.windows.setShared;
import getShared = nts.uk.ui.windows.getShared;


module nts.uk.at.view.kdl055.test.viewmodel {

    @bean()
    export class KDL055TestViewModel extends ko.ViewModel {
        dateValue: KnockoutObservable<any>;
        listSid: KnockoutObservableArray<string> = ko.observableArray([]);
        listEmp: KnockoutObservableArray<any> = ko.observableArray([]);
        baseDate: KnockoutObservable<string> = ko.observable(null);
        startDate: KnockoutObservable<string> = ko.observable(null);
        endDate: KnockoutObservable<string> = ko.observable(null);
        listComponentOption: any;

        created() {
            const vm = this;
            vm.baseDate(moment().format());
            vm.startDate(moment().clone().startOf('month').format('YYYY-MM-DD hh:mm'));
            vm.endDate(moment().clone().endOf('month').format('YYYY-MM-DD hh:mm'));

            vm.dateValue = ko.observable({});
            
            vm.startDate.subscribe(function(value){
                vm.dateValue().startDate = value;
                vm.dateValue.valueHasMutated();        
            });
            
            vm.endDate.subscribe(function(value){
                vm.dateValue().endDate = value;   
                vm.dateValue.valueHasMutated();      
            });

            
            vm.listComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: ko.observable(true),
                listType: 4,
                employeeInputList: vm.listEmp,
                selectedCode: ko.observableArray([]),
                selectType: 1,
                isDialog: ko.observable(true),
                isShowNoSelectRow: ko.observable(false),
                isShowWorkPlaceName: false,
                isShowSelectAllButton: ko.observable(true),
                disableSelection : ko.observable(false),
                maxRows: 25
            };

            // bind CCG001 component
            // Set component option
            let ccg001ComponentOption = {
                /** Common properties */
                systemType: 1,
                showEmployeeSelection: true,
                showQuickSearchTab: true,
                showAdvancedSearchTab: true,
                showBaseDate: false,
                showClosure: true,
                showAllClosure: true,
                showPeriod: false,
                periodFormatYM: false,
                
                /** Required parameter */
                baseDate: vm.baseDate,
                periodStartDate: vm.startDate,
                periodEndDate: vm.endDate,
                inService: true,
                leaveOfAbsence: true,
                closed: true,
                retirement: true,
                
                /** Quick search tab options */
                showAllReferableEmployee: true,
                showOnlyMe: true,
                showSameDepartment: true,
                showSameDepartmentAndChild: true,
                showSameWorkplace: true,
                showSameWorkplaceAndChild: true,
                
                /** Advanced search properties */
                showEmployment: true,
                showDepartment: true,
                showWorkplace: true,
                showClassification: true,
                showJobTitle: true,
                showWorktype: true,
                isMutipleCheck: true,
                
                /**
                * Self-defined function: Return data from CCG001
                * @param: data: the data return from CCG001
                */
                returnDataFromCcg001: function(data: any) {
                    vm.listEmp(_.map(data.listEmployee, (item: any) => {
                        return {id: item.employeeId, code: item.employeeCode, name: item.employeeName, workplaceName: item.affiliationName};
                    }));
                    if (_.isEmpty(vm.listEmp())) {
                        vm.listComponentOption.selectedCode(vm.listEmp()[0].code);
                        $('#com-kcp005').ntsListComponent(vm.listComponentOption);
                    }
                    vm.baseDate(data.baseDate);
                    vm.startDate(data.periodStart);
                    vm.endDate(data.periodEnd);
                }
            }

            $('#com-ccg001').ntsGroupComponent(ccg001ComponentOption);

            // bind KCP005 component
            $('#com-kcp005').ntsListComponent(vm.listComponentOption);

            
            vm.dateValue.subscribe(function(value){
                vm.startDate(value.startDate);
                vm.endDate(value.endDate);
                vm.baseDate(vm.startDate());
                ccg001ComponentOption.baseDate = vm.baseDate;
                ccg001ComponentOption.periodStartDate = vm.startDate;
                ccg001ComponentOption.periodEndDate = vm.endDate;
                $('#com-ccg001').ntsGroupComponent(ccg001ComponentOption);
            })
        }
        
        mounted() {

        }

        openDialog() {
            const vm = this;
            let emps = $('#com-kcp005').ntsListComponentApi("getSelectedRecords");
            if (emps.length > 0) {
                _.map(emps, (emp: any) => {
                    vm.listSid().push(emp.id);
                })
            } else {
                vm.$dialog.error('No employee selected');
                return;
            }
            let param: any = {sIDs: vm.listSid(), startDate: moment(vm.startDate()).format('YYYY/MM/DD'), endDate: moment(vm.endDate()).format('YYYY/MM/DD')};

            /**
            * Using this to open screen A after close screen B
            * START
            */
            vm.$window.modal('at', '/view/kdl/055/a/index.xhtml', param)
            .then(() => {
                let paramB = getShared('paramB');
                if (paramB) {
                    return vm.$window.modal('at', '/view/kdl/055/b/index.xhtml', paramB).then(() => {
                        vm.openDialog();
                    });
                }
            });
            /**
            * Using this to open screen A after close screen B
            * END
            */
        }
    }

    const paths = {
        getDataStartScreen: "screen/at/schedule/start"
    }
}