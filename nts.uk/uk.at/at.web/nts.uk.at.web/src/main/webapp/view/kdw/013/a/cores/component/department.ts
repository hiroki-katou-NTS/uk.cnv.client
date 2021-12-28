module nts.uk.ui.at.kdw013.department {
    @handler({
        bindingName: 'kdw013-department'
    })
    export class Kdw013DepartmentBindingHandler implements KnockoutBindingHandler {
        init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
            const name = componentName();
            const mode: KnockoutObservable<boolean> = allBindingsAccessor.get('mode');
            const params = { ...allBindingsAccessor() };
            const subscribe = (mode: boolean) => {

                if (mode) {
                    ko.cleanNode(element);

                    element.innerHTML = '';
                } else {
                    ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);
                }
            };

            mode.subscribe(subscribe);

            subscribe(mode());

            return { controlsDescendantBindings: true };
        }
    }

    type EmployeeDepartmentParams = {
        mode: KnockoutObservable<boolean>;
        employee: KnockoutObservable<string>;
        $settings: KnockoutObservable<a.StartProcessDto | null>;
        screenA: nts.uk.ui.at.kdw013.a.ViewModel;
};

@component({
    name: 'kdw013-department',
    template: `
            <div id='department-acc' data-bind="ntsAccordion: {event:'keypress', active: 0}">
                <h3>
                    <label data-bind="i18n: 'KDW013_4'"></label>
                    <i class='open-k-icon' style='float:right;' data-bind="ntsIcon: { no: 233, width: 25, height: 25 }" ></i>
                </h3>
        
                <div class='fc-employees'>
                        <div data-bind="ntsComboBox: {
                        name: $component.$i18n('KDW013_5'),
                        options: $component.departments,
                        visibleItemsCount: 14,
                        value: $component.department,
                        editable: true,
                        selectFirstIfNull: true,
                        optionsValue: 'workplaceId',
                        optionsText: 'wkpDisplayName',
                        columns: [
                            { prop: 'workplaceCode', length: 4 },
                            { prop: 'wkpDisplayName', length: 10 }
                        ]
                    }"></div>
                <ul class="list-employee" data-bind="foreach: { data: $component.employees, as: 'item' }">
                    <li class="item" data-bind="
                        click: function() { $component.selectEmployee(item.employeeId) },
                        timeClick: -1,
                        css: {
                            'selected': ko.computed(function() { return item.employeeId === ko.unwrap($component.params.employee); })
                        }">
                        <div data-bind="text: item.employeeCode"></div>
                        <div data-bind="text: item.employeeName"></div>
                    </li>
                </ul>
                </div>
            </div>
<style>
.open-k-icon:hover{
        background-color: rgb(229, 242, 255);
}
.fc-employees .ui-accordion-header{
        padding: 0.4rem 0rem 0.4rem 0.4rem !important;
}
</style>
            `
})
export class EmployeeDepartmentComponent extends ko.ViewModel {
    department: KnockoutObservable<string> = ko.observable('');

    employees!: KnockoutComputed<EmployeeBasicInfoDto[]>;
departments!: KnockoutComputed<WorkplaceInfoDto[]>;

constructor(private params: EmployeeDepartmentParams) {
    super();

    const vm = this;
    const { $settings, mode } = params;

    vm.employees = ko.computed({
        read: () => {
            const loaded = ko.unwrap(mode);
            const $sets = ko.unwrap($settings);
            const $dept = ko.unwrap(vm.department);

            if ($sets) {
                const { employeeInfos ,lstEmployeeInfo } = $sets;

                if (employeeInfos && lstEmployeeInfo) {
                    let emps = _.filter(employeeInfos, { 'workplaceId': $dept });
                    // updating
                    return loaded ? [] : _.filter(lstEmployeeInfo, (o) => {
                        return !!_.find(emps, { 'employeeId': o.employeeId });
                    });
                }
            }

            return [];
        },
        write: (value: any) => {

        }
    });


    vm.department.subscribe((value) => {
        const emps = ko.unwrap(vm.employees);
        if (vm.employees().length) {

            const [first] = emps;
            vm.params.employee(first.employeeId);
        }
    });



    vm.departments = ko.computed({
        read: () => {
            const loaded = ko.unwrap(mode);
            const $sets = ko.unwrap($settings);

            if ($sets) {
                const { workplaceInfos } = $sets;

                if (workplaceInfos) {

                    return loaded ? [] : workplaceInfos;
                }
            }

            return [];
        },
        write: (value: any) => {

        }
    });

    vm.departments
        .subscribe((deps) => {
            if (!_.isEmpty(deps)) {

                let empInfo = _.find(vm.params.$settings().employeeInfos, { 'employeeId': vm.$user.employeeId });

                if (empInfo) {

                    let selectedWkp = _.find(deps, { 'workplaceId': empInfo.workplaceId });

                    if (selectedWkp) {

                        vm.department(selectedWkp.workplaceId);
                    }
                }

            }
        });

    vm.employees
        .subscribe((emps: EmployeeBasicInfoDto[]) => {
            if (emps.length && !ko.unwrap(vm.params.employee)) {

                let emp = _.find(emps, { 'employeeId': vm.$user.employeeId });
                if (emp) {
                    vm.params.employee(emp.employeeId);
                } else {
                    const [first] = emps;
                    vm.params.employee(first.employeeId);
                }

            }
        });
}

OpenKDialog(){
    const vm = this;
    
    vm.$window.modal('at', '/view/kdw/013/k/index.xhtml', { date: vm.params.screenA.inputDate() || vm.$date.now() }).then((result) => {
        if (result)
            vm.params.screenA.inputDate(result);
    });
}

mounted() {
    const vm = this;
    const { $el } = vm;

    $($el)
        .removeAttr('data-bind')
        .find('[data-bind]')
        .removeAttr('data-bind');
    
       vm.event = (evt, vm) => {
        const vm = this;
        const tg = evt.target as HTMLElement;
           //phải làm trò  này để tách biệt 2 event khi click vào header và icon trên cái header đó
        if ($(tg).closest('.department h3').length > 0 && $(tg).is('i')) {
            vm.OpenKDialog();
        }else{
            if ($(tg).closest('.department .ui-accordion-header').length > 0) {
                $('.department .ui-accordion-header').keypress();
            }
        }
        
    };
    
    $(document).on('click', vm.event);
}

public selectEmployee(id: string) {
    const vm = this;
    const { department } = vm;
    const { employee } = vm.params;

    employee(id);

    //department.valueHasMutated();
}
        }
}