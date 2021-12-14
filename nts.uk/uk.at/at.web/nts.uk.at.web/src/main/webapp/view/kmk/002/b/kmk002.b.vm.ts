module nts.uk.at.view.kmk002.b {
    export module viewmodel {

        import EmpConditionDto = service.model.EmpConditionDto;
        import ConditionDto = service.model.ConditionDto;

        export class ScreenModel {
            empCondition: EmpCondition;

            constructor() {
                this.empCondition = new EmpCondition();
            }

            /**
             * Start page.
             */
            public startPage(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                nts.uk.ui.block.invisible();

                // Get param from parent screen
                let itemNo = nts.uk.ui.windows.getShared("paramForB");

                self.loadEmpCondition(itemNo)
                    .done(() => dfd.resolve())
                    .always(() => nts.uk.ui.block.clear());
                return dfd.promise();
            }

            /**
             * Load employment applicable condition.
             */
            private loadEmpCondition(itemNo: string): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.find(itemNo).done(res => {

                    // Convert to viewmodel
                    self.empCondition.fromDto(res);

                    dfd.resolve();
                });
                return dfd.promise();

            }

            /**
             * Triggered on click button apply all.
             */
            public applyAll(): void {
                let self = this;
                self.empCondition.applyAll();
                self.empCondition.updateNtsGrid();
            }

            /**
             * Triggered on click button not apply all.
             */
            public notApplyAll(): void {
                let self = this;
                self.empCondition.notApplyAll();
                self.empCondition.updateNtsGrid();
            }

            /**
             * Triggered On click button save.
             */
            public save(): void {
                let self = this;
                let command = self.empCondition.toDto();
                nts.uk.ui.block.invisible();
                service.save(command)
                    .done(() => {
                        nts.uk.ui.block.clear();
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => self.close());
                    });
            }

            /**
             * Close dialog
             */
            public close(): void {
                nts.uk.ui.windows.close();
            }

        }

        class EmpCondition {
            optionalItemNo: number;
            empConditions: Array<Condition>;

            switchds: Array<any>;

            constructor() {
                this.optionalItemNo = 0;
                this.empConditions = new Array();
                this.switchds = [
                    { code: 1, name: nts.uk.resource.getText("KMK002_51") }, // apply
                    { code: 0, name: nts.uk.resource.getText("KMK002_52") } // not apply
                ];
            }

            public fromDto(dto: EmpConditionDto): void {
                let self = this;
                self.optionalItemNo = dto.optionalItemNo;
                self.empConditions = dto.empConditions.map(item => new Condition(item));
            }

            public toDto(): EmpConditionDto {
                let self = this;
                let dto = <EmpConditionDto>{};

                dto.optionalItemNo = self.optionalItemNo;
                dto.empConditions = self.empConditions.map(item => item.toDto());

                return dto;
            }

            /**
             * Apply all employment
             */
            public applyAll(): void {
                let self = this;
                _.each(self.empConditions, item => item.apply());
            }

            /**
             * Not apply all employment
             */
            public notApplyAll(): void {
                let self = this;
                _.each(self.empConditions, item => item.notApply());
            }

            /**
             * Update nts grid.
             */
            public updateNtsGrid(): void {
                let self = this;
                _.each(self.empConditions, item => {
                    let data = { empName: item.empName, empApplicableAtr: item.empApplicableAtr };
                    $("#grid-emp-condition").ntsGrid("updateRow", item.empCd, data);
                });
            }

            /**
             * Init nts grid.
             */
            public initNtsGrid(): void {
                let self = this;
                $("#grid-emp-condition").ntsGrid({
                    height: '315px',
                    dataSource: this.empConditions,
                    primaryKey: 'empCd',
                    virtualization: true,
                    virtualizationMode: 'continuous',
                    columns: [
                        { headerText: '', key: 'empCd', dataType: 'string', width: '0px', hidden: true },
                        {
                            headerText: nts.uk.resource.getText("KMK002_49"), width: '160px',
                            key: 'empName', dataType: 'string'
                        },
                        {
                            headerText: nts.uk.resource.getText("KMK002_50"), width: '164px',
                            key: 'empApplicableAtr', dataType: 'number', ntsControl: 'SwitchButtons'
                        },
                    ],
                    features: [],
                    ntsFeatures: [],
                    ntsControls: [{
                        name: 'SwitchButtons', options: this.switchds,
                        optionsValue: 'code', optionsText: 'name', controlType: 'SwitchButtons', enable: true
                    }]
                });
            }

        }

        class Condition {
            empCd: string;
            empName: string;
            empApplicableAtr: number;

            constructor(dto: ConditionDto) {
                this.empCd = dto.empCd;
                this.empName = dto.empName;
                this.empApplicableAtr = dto.empApplicableAtr;
            }

            /**
             * Set emp to apply
             */
            public apply(): void {
                this.empApplicableAtr = 1;
            }

            /**
             * Set emp to not apply 
             */
            public notApply(): void {
                this.empApplicableAtr = 0;
            }

            public toDto(): ConditionDto {
                let self = this;
                let dto = <ConditionDto>{};
                dto.empCd = self.empCd;
                dto.empName = self.empName;
                dto.empApplicableAtr = self.empApplicableAtr;
                return dto;
            }

        }
    }
}