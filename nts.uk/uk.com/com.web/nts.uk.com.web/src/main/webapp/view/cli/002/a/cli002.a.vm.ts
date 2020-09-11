/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.cli002.a {
    import service = nts.uk.com.view.cli002.a.service;

    @bean()
    export class ScreenModel extends ko.ViewModel {
        public logSettings: KnockoutObservableArray<LogSetting> = ko.observableArray([]);

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

        constructor() {
            super();
            const vm = this;
            vm.getData(vm.selectedSystemCode());
        }

        mounted() {
            const vm = this;
            vm.selectedSystemCode.subscribe((newValue) => {
                vm.getData(newValue);
            });
        }

        public register() {
            const vm = this;
            vm.logSettings = ko.observableArray([]);
            _.forEach(vm.dataSourceItem, function(item: PGList) {
                vm.logSettings.push(new LogSetting(vm.selectedSystemCode(), item.programId, item.menuClassification, item.loginHistoryRecord.usageCategory,
                    item.editHistoryRecord.usageCategory, item.bootHistoryRecord.usageCategory));
            });
            service.updateLogSetting(vm.logSettings).then(() => {
                vm.$dialog.alert({ messageId: 'Msg_15' });
            });
        }

        private getData(systemType: number) {
            const vm = this;
            vm.dataSourceItem = ko.observableArray([]);
            vm.$blockui("grayout");
            service.findBySystem(systemType).done((response: Array<PGList>) => {
                let listPG: any[] = response.map((item, index) => {
                    return new PGInfomation(index + 1, item.functionName, false, false, false, item.programId, item.menuClassification);
                });
                vm.dataSourceItem(listPG);
                vm.getItemList(response);

            }).always(() => vm.$blockui("clear")); 
        }

        private getItemList(response: Array<PGList>) {
            const vm = this;
            const statesTable = [];
            response.forEach((item: PGList, index) => {
                if (item.loginHistoryRecord.activeCategory == 0) {
                    statesTable.push(new CellState(index, "logLoginDisplay", [(nts.uk.ui as any).mgrid.color.Lock]));
                }
                if (item.bootHistoryRecord.activeCategory == 0) {
                    statesTable.push(new CellState(index, "logStartDisplay", [(nts.uk.ui as any).mgrid.color.Lock]));
                }
                if (item.editHistoryRecord.activeCategory == 0) {
                    statesTable.push(new CellState(index, "logUpdateDisplay", [(nts.uk.ui as any).mgrid.color.Lock]));
                }
            })
            console.log(statesTable);
            if ($("#item-list").data("mGrid")) $("#item-list").mGrid("destroy");
            new (nts.uk.ui as any).mgrid.MGrid($("#item-list")[0], {
                subHeight: "450px",
                headerHeight: '60px',
                primaryKey: "functionName",
                primaryKeyDataType: "string",
                rowVirtualization: true,
                virtualization: true,
                virtualizationMode: 'continuous',
                enter: 'right',
                autoFitWindow: true,
                dataSource: vm.dataSourceItem(),
                columns: [
                    { headerText: "", key: "rowNumber", dataType: "number", width: "30px"},
                    { headerText: this.$i18n("CLI002_7"), key: "functionName", dataType: "string", width: "180px"},
                    { headerText: this.$i18n("CLI002_4"),
                        group: [
                            {headerText: "", key: "logLoginDisplay", dataType: "boolean", width: "180px", ntsControl: "Checkbox", checkbox: true, hidden: false}
                        ] 
                    },
                    { headerText: this.$i18n("CLI002_5"),
                        group: [
                            {headerText: "", key: "logStartDisplay", dataType: "boolean", width: "180px", ntsControl: "Checkbox", checkbox: true, hidden: false}
                        ] 
                    },
                    { headerText: this.$i18n("CLI002_6"),
                        group: [
                            {headerText: "", key: "logUpdateDisplay", dataType: "boolean", width: "180px", ntsControl: "Checkbox", checkbox: true, hidden: false}
                        ]
                    },
                ],
                
                features: [
                    { 
                        name: 'CellStyles',
                        states: statesTable
                    },
                    {   
                        name: 'ColumnFixing',
                        fixingDirection: 'left',
                        showFixButtons: false,
                        columnSettings: [
                            { columnKey: 'rowNumber', isFixed: true }
                        ]
                    }
                ],

                ntsFeatures: [],

                ntsControls: [
                    { name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true, onChange: function() {}}
                ],
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

    export interface TargetSetting {
        usageCategory: number,
        activeCategory: number
    }

    export interface PGList {
        functionName: string,
        loginHistoryRecord: TargetSetting,
        bootHistoryRecord: TargetSetting,
        editHistoryRecord: TargetSetting,
        programId: string,
        menuClassification: number
    }

    class PGInfomation {
        rowNumber: number;
        functionName: string;
        logLoginDisplay: boolean;
        logStartDisplay: boolean;
        logUpdateDisplay: boolean;
        programId: string;
        menuClassification: number;
        constructor(rowNumber: number, functionName: string, logLoginDisplay: boolean, logStartDisplay: boolean, logUpdateDisplay: boolean, programId: string, menuClassification: number) {
            this.rowNumber = rowNumber;
            this.functionName = functionName;
            this.logLoginDisplay = logLoginDisplay;
            this.logStartDisplay = logStartDisplay;
            this.logUpdateDisplay = logUpdateDisplay;
            this.programId = programId;
            this.menuClassification = menuClassification;
        }
    }

    class LogSetting {
        system: number;
        programId: string;
        menuClassification: number;
        loginHistoryRecord: number;
        editHistoryRecord: number;
        bootHistoryRecord: number;
        constructor(system: number,
            programId: string,
            menuClassification: number,
            loginHistoryRecord: number,
            editHistoryRecord: number,
            bootHistoryRecord: number) {
                this.system = system;
                this.programId = programId;
                this.menuClassification = menuClassification;
                this.loginHistoryRecord = loginHistoryRecord;
                this.editHistoryRecord = editHistoryRecord;
                this.bootHistoryRecord= bootHistoryRecord;
        }
    }

    class CellState {
        rowId: number;
        columnKey: string;
        state: Array<any>
        constructor(rowId: number, columnKey: string, state: Array<any>) {
            this.rowId = rowId;
            this.columnKey = columnKey;
            this.state = state;
        }
    }
}
