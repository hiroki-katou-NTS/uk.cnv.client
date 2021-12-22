module nts.uk.at.view.kmk002.d {
    export module viewmodel {

        import FormulaSettingDto = nts.uk.at.view.kmk002.a.service.model.FormulaSettingDto;
        import SettingItemDto = nts.uk.at.view.kmk002.a.service.model.SettingItemDto;
        import EnumConstantDto = nts.uk.at.view.kmk002.a.service.model.EnumConstantDto;
        import FormulaDto = nts.uk.at.view.kmk002.a.service.model.FormulaDto;
        import ParamToD = nts.uk.at.view.kmk002.a.viewmodel.ParamToD;
        import EnumAdaptor = nts.uk.at.view.kmk002.a.service.model.EnumAdaptor;
        import OptItemEnumDto = nts.uk.at.view.kmk002.a.service.model.OptItemEnumDto;
        import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
        import isNullOrEmpty = nts.uk.util.isNullOrEmpty;

        export class ScreenModel {
            formulaSetting: FormulaSetting;
            constructor() {
                let self = this;
                this.formulaSetting = new FormulaSetting();
            }
            /**
             * Start page.
             */
            public startPage(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();

                // Get params
                let dto = nts.uk.ui.windows.getShared('paramToD');

                // Set params to view model


                self.formulaSetting.rightItem.formulaItemId.subscribe((rightId)=>{
                    if(!isNullOrUndefined(rightId)) {
                        let leftId =  self.formulaSetting.leftItem.formulaItemId();
                        let operator =  self.formulaSetting.operator();
                        let isTimeD29 = self.formulaSetting.checkD2_9(leftId,operator,rightId);
                        let isTimeD49 = self.formulaSetting.checkD4_9(leftId,operator,rightId);
                        if(!isNullOrUndefined(isTimeD29)){
                            if(self.formulaSetting.isD29() == !isTimeD29){
                                self.formulaSetting.leftItem.inputValue(null);
                            }
                            self.formulaSetting.isD29(isTimeD29);
                        }

                        if(!isNullOrUndefined(isTimeD49)){
                            if(self.formulaSetting.isD49()  == !isTimeD49){
                                self.formulaSetting.rightItem.inputValue(null);
                            }
                            self.formulaSetting.isD49(isTimeD49);
                        }

                    }
                });

                self.formulaSetting.leftItem.formulaItemId.subscribe((leftId)=>{
                    if(!isNullOrUndefined(leftId)) {
                        let rightId =  self.formulaSetting.rightItem.formulaItemId();
                        let operator =  self.formulaSetting.operator();
                        let isTimeD29 = self.formulaSetting.checkD2_9(leftId,operator,rightId);
                        let isTimeD49 = self.formulaSetting.checkD4_9(leftId,operator,rightId);
                        if(!isNullOrUndefined(isTimeD29)){
                            if(self.formulaSetting.isD29() == !isTimeD29){
                                self.formulaSetting.leftItem.inputValue(null);
                            }
                            self.formulaSetting.isD29(isTimeD29);
                        }

                        if(!isNullOrUndefined(isTimeD49)){
                            if(self.formulaSetting.isD49() == !isTimeD49){
                                self.formulaSetting.rightItem.inputValue(null);
                            }
                            self.formulaSetting.isD49(isTimeD49);
                        }

                    }
                });
                self.formulaSetting.operator.subscribe((operator)=>{
                    if(!isNullOrUndefined(operator)) {
                        let rightId =  self.formulaSetting.rightItem.formulaItemId();
                        let leftId =  self.formulaSetting.leftItem.formulaItemId();
                        let operator =  self.formulaSetting.operator();
                        let isTimeD29 = self.formulaSetting.checkD2_9(leftId,operator,rightId);
                        let isTimeD49 = self.formulaSetting.checkD4_9(leftId,operator,rightId);

                        if(!isNullOrUndefined(isTimeD29)){
                            if(self.formulaSetting.isD29()  == !isTimeD29){
                                self.formulaSetting.leftItem.inputValue(null);
                            }
                            self.formulaSetting.isD29(isTimeD29);
                        }

                        if(!isNullOrUndefined(isTimeD49)){
                            if(self.formulaSetting.isD49() ==!isTimeD49){
                                self.formulaSetting.rightItem.inputValue(null);
                            }
                            self.formulaSetting.isD49(isTimeD49);
                        }

                    }
                });

                self.formulaSetting.fromDto(dto);

                dfd.resolve();
                return dfd.promise();
            }

            /**
             * Return data to caller screen & close dialog.
             */
            public apply(): void {
                let self = this;
                if (self.formulaSetting.isValid()) {
                    nts.uk.ui.windows.setShared('returnFromD', self.formulaSetting.toDto());
                    self.close();
                }
            }


            /**
             * Close dialog
             */
            public close(): void {
                nts.uk.ui.windows.close();
            }
        }
        class FormulaSetting {
            formulaId: string;
            formulaName: string;
            formulaAtr: string;
            formulaNumber: number;
            selectableFormulas: KnockoutObservableArray<FormulaDto>;

            minusSegment: KnockoutObservable<boolean>;
            operator: KnockoutObservable<number>;
            leftItem: FormulaSettingItem;
            rightItem: FormulaSettingItem;

            // enable / disable flag
            isLeftCbbEnable: KnockoutComputed<boolean>;
            isRightCbbEnable: KnockoutComputed<boolean>;
            isFormulasEmpty: KnockoutComputed<boolean>;

            // datasource
            operatorDatasource: KnockoutObservableArray<EnumConstantDto>;
            isTime : KnockoutObservable<boolean> = ko.observable(false);
            isD49 : KnockoutObservable<boolean> = ko.observable(false);
            isD29 : KnockoutObservable<boolean> = ko.observable(false);
            enums: any;

            constructor() {
                this.minusSegment = ko.observable(false);
                this.operator = ko.observable(0);
                this.leftItem = new FormulaSettingItem();
                this.rightItem = new FormulaSettingItem();
                this.selectableFormulas = ko.observableArray([]);

                // enable / disable flag
                this.isFormulasEmpty = ko.computed(() => {
                    if (nts.uk.util.isNullOrEmpty(this.selectableFormulas())) {
                        return true;
                    }
                    return false;
                });
                this.isLeftCbbEnable = ko.computed(() => {
                    if (this.isFormulasEmpty() || this.leftItem.isInputValue()) {
                        return false;
                    }
                    return true;
                });
                this.isRightCbbEnable = ko.computed(() => {
                    if (this.isFormulasEmpty() || this.rightItem.isInputValue()) {
                        return false;
                    }
                    return true;
                });

                // fixed 
                this.leftItem.dispOrder = 1;
                this.leftItem.settingMethod(0);
                this.rightItem.dispOrder = 2;
                this.rightItem.settingMethod(1);

                this.operatorDatasource = ko.observableArray([]);

                // default value
                this.formulaName = '';
                this.formulaAtr = '';
                this.formulaNumber = 1;
                this.enums = null;


            }

            /**
             * Data input validation
             */
            public isValid(): boolean {
                let self = this;

                // Check divide by zero
                if (self.rightItem.settingMethod() == 1 && self.operator() == 3 && self.rightItem.inputValue() == 0) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_638" });
                    return false;
                }

                // both item setting method = number.
                if (self.isBothNumberInput()) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_420" });
                    return false;
                }

                // Check required input of right item
                if (self.rightItem.settingMethod() == SettingMethod.NUMBER_INPUT ) {
                    // if(self.rightItem.inputValue() == null){
                    //     $('#inp-right-item2').trigger("validate");
                    // }
                    $('#inp-right-item2').ntsEditor('validate');
                    $('#inp-right-item').ntsEditor('validate');
                }

                // Check required input of left item
                if (self.leftItem.settingMethod() == SettingMethod.NUMBER_INPUT) {
                    // if(self.leftItem.inputValue() == null){
                    //     $('#inp-left-item_1').trigger("validate");
                    // }
                    $('#inp-left-item_1').ntsEditor('validate');
                    $('#inp-left-item').ntsEditor('validate');
                }

                // Validate calculation
                // If operator is '+' or '-' , 
                // both item must have the same attribute if setting method is both item selection 
                if (self.operator() == 0 || self.operator() == 1) {
                    if (self.isBothItemSelect() && self.isDifferentAtr()) {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_114" });
                        return false;
                    }
                }

                // Validate number input.
                if ($('.nts-editor').ntsError('hasError')) {
                    return false;
                }

                return true;

            }
            // Check item time atr;
            public isTimeSelected(formulaNumber: number): boolean {
                let seft = this;
                if(!isNullOrUndefined(seft.enums))
                return formulaNumber == EnumAdaptor.valueOf('TIME', seft.enums.formulaAtr);
            }

            /**
             * Check whether number atr is selected
             */
            public isNumberSelected(formulaNumber: number): boolean {
                let seft = this;
                if(!isNullOrUndefined(seft.enums))
                return formulaNumber == EnumAdaptor.valueOf('NUMBER', seft.enums.formulaAtr);
            }

            /**
             * Check whether amount atr is selected
             */
            public isAmountSelected(formulaNumber: number): boolean {
                let seft = this;
                if(!isNullOrUndefined(seft.enums))
                return formulaNumber == EnumAdaptor.valueOf('AMOUNT', seft.enums.formulaAtr);
            }
            public checkD2_9(leftId : string,operator:number,rightId:string):boolean {
                let seft = this;
                // let leftItem: FormulaDto = seft.formulaSetting.findFormulaById(
                //     seft.formulaSetting.leftItem.formulaItemId());
                let rightItem: FormulaDto = seft.findFormulaById(rightId);
                let formulaNumber = seft.formulaNumber;
                //0 : +
                //1 : -
                //2 : *
                //3 : /
                if(!isNullOrUndefined(rightItem) &&!isNullOrUndefined(formulaNumber)&&!isNullOrUndefined(operator)){
                    if((seft.isTimeSelected(formulaNumber) && seft.isTimeSelected(rightItem.formulaAtr) && (operator == 0 ||operator==1))
                        ||(seft.isTimeSelected(formulaNumber) && seft.isNumberSelected(rightItem.formulaAtr) && (operator == 2 ))
                        ||(seft.isNumberSelected(formulaNumber) && seft.isTimeSelected(rightItem.formulaAtr) && (operator == 3))){
                        return true
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }

            }
            public checkD4_9(leftId : string,operator:number,rightId:string):boolean {
                let seft = this;
                 let leftItem: FormulaDto = seft.findFormulaById(leftId);
                //      seft.formulaSetting.leftItem.formulaItemId());
                //      let rightItem: FormulaDto = seft.findFormulaById(rightId);
                let formulaNumber = seft.formulaNumber;
                //0 : +
                //1 : -
                //2 : *
                //3 : /
                if(!isNullOrUndefined(leftItem) &&!isNullOrUndefined(formulaNumber)&&!isNullOrUndefined(operator)){
                    if((seft.isTimeSelected(formulaNumber) && seft.isTimeSelected(leftItem.formulaAtr) && (operator == 0 ||operator==1))
                        ||(seft.isTimeSelected(formulaNumber) && seft.isNumberSelected(leftItem.formulaAtr) && (operator == 2 ))
                        ||(seft.isNumberSelected(formulaNumber) && seft.isTimeSelected(leftItem.formulaAtr) && (operator == 3))){
                        return true
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }

            }

            /**
             * Check if attribute of left item and right item is different.
             */
            private isDifferentAtr(): boolean {
                let self = this;
                let leftItem: FormulaDto = self.findFormulaById(self.leftItem.formulaItemId());
                let rightItem: FormulaDto = self.findFormulaById(self.rightItem.formulaItemId());

                if (leftItem.formulaAtr != rightItem.formulaAtr) {
                    return true;
                }

                return false;
            }

            /**
             * Check if both item's method setting is item selection
             */
            private isBothItemSelect(): boolean {
                let self = this;
                if (self.leftItem.settingMethod() == SettingMethod.ITEM_SELECTION
                    && self.rightItem.settingMethod() == SettingMethod.ITEM_SELECTION) {
                    return true;
                }
                return false;
            }

            /**
             * Check if both item's method setting is numerical input.
             */
            private isBothNumberInput(): boolean {
                let self = this;
                if (self.leftItem.settingMethod() == SettingMethod.NUMBER_INPUT
                    && self.rightItem.settingMethod() == SettingMethod.NUMBER_INPUT) {
                    return true;
                }
                return false;
            }

            /**
             * find formula by id.
             */
            public findFormulaById(id: string): any {
                let self = this;
                let f = _.find(self.selectableFormulas(), item => item.formulaId == id);
                return f;
            }

            /**
             * Convert to viewmodel
             */
            public fromDto(dto: ParamToD): void {
                let self = this;
                if(!isNullOrEmpty(dto.selectableFormulas)){
                    for (let i = 0; i < dto.selectableFormulas.length;i++){
                        let item :FormulaDto = dto.selectableFormulas[i];
                        let type  = EnumAdaptor.localizedNameOf(item.formulaAtr, dto.enums.formulaAtr);
                        let name = item.symbolValue + " " + type;
                        item.symbolValue = name;
                    }
                }
                self.formulaId = dto.formulaId;
                self.formulaName = dto.formulaName;
                self.formulaAtr = dto.formulaAtr;
                self.minusSegment(dto.formulaSetting.minusSegment == 1 ? true: false);
                self.operator(dto.formulaSetting.operator);
                self.leftItem.fromDto(dto.formulaSetting.leftItem);
                self.rightItem.fromDto(dto.formulaSetting.rightItem);
                self.selectableFormulas(dto.selectableFormulas);
                self.operatorDatasource(dto.operatorDatasource);
                self.enums = dto.enums;
                self.formulaNumber = dto.formulaNumber;
                if(!isNullOrEmpty(dto.selectableFormulas)){
                    self.isD29(self.checkD2_9(self.leftItem.formulaItemId(),self.operator(),self.rightItem.formulaItemId()));
                    self.isD49(self.checkD4_9(self.leftItem.formulaItemId(),self.operator(),self.rightItem.formulaItemId()));
                }

            }

            /**
             * convert viewmodel to dto
             */
            public toDto(): FormulaSettingDto {
                let self = this;
                let dto: FormulaSettingDto = <FormulaSettingDto>{};

                dto.minusSegment = self.minusSegment() == true ? 1 : 0;
                dto.operator = self.operator();
                dto.leftItem = self.leftItem.toDto();
                dto.rightItem = self.rightItem.toDto();

                return dto;
            }

        }
        /**
         * Formula setting item
         */
        class FormulaSettingItem {
            settingMethod: KnockoutObservable<number>;
            dispOrder: number;
            inputValue: KnockoutObservable<number>;
            formulaItemId: KnockoutObservable<string>;
            settingItemStash: SettingItemDto;

            constructor() {
                this.settingMethod = ko.observable(0);
                this.inputValue = ko.observable(0);
                this.formulaItemId = ko.observable('');
            }

            /**
             * is input value check
             */
            public isInputValue(): boolean {
                if (this.settingMethod() == SettingMethod.ITEM_SELECTION) {
                    return false;
                }
                return true;
            }

            /**
             * convert dto to viewmodel
             */
            public fromDto(dto: SettingItemDto): void {
                this.settingMethod(dto.settingMethod);
                this.dispOrder = dto.dispOrder;
                this.inputValue(dto.inputValue);
                this.formulaItemId(dto.formulaItemId);

                // save data to stash
                this.settingItemStash = jQuery.extend(true, {}, dto);
            }

            /**
             * convert viewmodel to dto
             */
            public toDto(): SettingItemDto {
                let self = this;
                let dto: SettingItemDto = <SettingItemDto>{};

                dto.settingMethod = this.settingMethod();
                dto.dispOrder = this.dispOrder;
                dto.inputValue = this.inputValue();
                dto.formulaItemId = this.formulaItemId();

                // get original data from stash if control is disabled
                if (this.settingMethod() == SettingMethod.ITEM_SELECTION) {
                    // reset input value
                    dto.inputValue = this.settingItemStash.inputValue;
                } else {
                    // reset formulaItem id
                    dto.formulaItemId = this.settingItemStash.formulaItemId;
                }

                return dto;
            }
        }

        enum SettingMethod {
            ITEM_SELECTION = 0,
            NUMBER_INPUT = 1
        }
        class Enums {
            static ENUM_OPT_ITEM: OptItemEnumDto;
        }
    }
}