/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.cli002.a {
    import service = nts.uk.com.view.cli002.a.service;

    @bean()
    export class ScreenModel extends ko.ViewModel {
        public systemList: KnockoutObservableArray<systemType> = ko.observableArray([
            new systemType(0, this.$i18n("Enum_SystemType_PERSON_SYSTEM")),
            new systemType(1, this.$i18n("Enum_SystemType_ATTENDANCE_SYSTEM")),
            new systemType(2, this.$i18n("Enum_SystemType_PAYROLL_SYSTEM")),
            new systemType(3, this.$i18n("Enum_SystemType_OFFICE_HELPER")),
        ]);

        public dataSourceItem: KnockoutObservableArray<any> = ko.observableArray([]);
        public selectedSystemCode: KnockoutObservable<number> = ko.observable(0);

        public systemColumns = [
            {
                headerText: "",
                prop: "index",
                width: 160,
                hidden: true,
            },
            {
                headerText:this.$i18n("CLI002_2"),
                prop: "localizedName",
                width: 160,
            }
        ];

        public columnsItem = [
            {
                headerText: this.$i18n(""),
                prop: "index",
                width: 30,
            },
            {
                headerText: this.$i18n("CLI002_7"),
                prop: "functionName",
                width: 180,
            },
            {
                headerText: this.$i18n("CLI002_4"),
                prop: "loginHistory",
                width: 180,
            },
            {
                headerText: this.$i18n("CLI002_5"),
                prop: "bootHistory",
                width: 180,
            },
            {
                headerText: this.$i18n("CLI002_6"),
                prop: "revisionHistory",
                width: 180,
            },
        ];

        constructor() {
            super();
            const vm = this;
            vm.getData(0);
        }

        mounted() {
            const vm = this;
            vm.selectedSystemCode.subscribe((newValue) => {
                vm.getData(newValue);
            });

            this.getItemList();
        }

        private getData(systemType: number) {
            const vm = this;
            vm.$blockui("grayout");
            service.findBySystem(systemType).done((response: PGList[]) => {
                const pgInfomation: Array<PGInfomation> = [];
                _.forEach(response, function(item: PGList, index) {
                    pgInfomation.push(new PGInfomation(index + 1, item.functionName, false, false, false));
                })
                vm.dataSourceItem(pgInfomation);
                this.getItemList();
            }).always(() => vm.$blockui("clear")); 
        }

        private getItemList() {
            const vm = this;
            new nts.uk.ui.mgrid.MGrid($("#item-list")[0], {
                height: "400px",
                headerHeight: '60px',
                primaryKey: "number",
                primaryKeyDataType: "displayName",
                rowVirtualization: true,
                virtualization: true,
                virtualizationMode: 'continuous',
                enter: 'right',
                autoFitWindow: false,
                dataSource: vm.dataSourceItem(),
                columns: [
                    { headerText: "", key: "rowNumber", dataType: "number", width: "30px"},
                    { headerText: this.$i18n("CLI002_7"), key: "functionName", dataType: "string", width: "180px"},
                    { headerText: this.$i18n("CLI002_4"),
                        group: [
                            {headerText: "", key: "logLoginDisplay", dataType: "boolean", width: "180px", ntsControl: "Checkbox", checkbox: true, border: false}
                        ] 
                    },
                    { headerText: this.$i18n("CLI002_5"),
                        group: [
                            {headerText: "", key: "logStartDisplay", dataType: "boolean", width: "180px", ntsControl: "Checkbox", checkbox: true, border: false}
                        ] 
                    },
                    { headerText: this.$i18n("CLI002_6"),
                        group: [
                            {headerText: "", key: "logUpdateDisplay", dataType: "boolean", width: "180px", ntsControl: "Checkbox", checkbox: true, border: false}
                        ]
                    },
                ],
                ntsControls: [
                    { name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true, onChange: function() {}}
                ],
                features: []
            }).create();
        }
    }
    class systemType {
        index: number;
        localizedName: string;
        constructor(index: number, localizedName: string) {
            this.index = index;
            this.localizedName = localizedName;
        }
    }

    class TargetSetting {
        usageCategory: number;
        activeCategory: number;
        constructor(usageCategory: number, activeCategory: number) {
            this.usageCategory = usageCategory;
            this.activeCategory = activeCategory;
        }
    }

    export interface PGList {
        functionName: string,
        loginHistoryRecord: TargetSetting,
        bootHistoryRecord: TargetSetting,
        editHistoryRecord: TargetSetting
    }

    class PGInfomation {
        rowNumber: number;
        functionName: string;
        logLoginDisplay: boolean;
        logStartDisplay: boolean;
        logUpdateDisplay: boolean;
        constructor(rowNumber: number, functionName: string, logLoginDisplay: boolean, logStartDisplay: boolean, logUpdateDisplay: boolean) {
            this.functionName = functionName;
            this.logLoginDisplay = logLoginDisplay;
            this.logStartDisplay = logStartDisplay;
            this.logUpdateDisplay = logUpdateDisplay;
        }
    }
}
