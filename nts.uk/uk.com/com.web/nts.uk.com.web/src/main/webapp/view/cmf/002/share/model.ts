module nts.uk.com.view.cmf002.share.model {
    import getText = nts.uk.resource.getText;

    export enum SCREEN_MODE {
        NEW = 0,
        UPDATE = 1
    }

    export enum STANDARD_ATR {
        USER = 0,
        STANDARD = 1
    }

    export enum NOT_USE_ATR {
        NOT_USE = 0,
        USE = 1
    }

    export enum ROUNDING_METHOD {
        TRUNCATION = 1,
        ROUND_UP = 2,
        DOWN_4_UP_5 = 0
    }

    export enum FORMAT_SELECTION {
        NO_DECIMAL = 0,
        DECIMAL = 1
    }

    export enum DECIMAL_POINT_CLASSIFICATION {
        NO_OUTPUT_DECIMAL_POINT = 0,
        OUTPUT_DECIMAL_POINT = 1
    }

    export enum FIXED_LENGTH_EDITING_METHOD {
        ZERO_BEFORE = 0,
        ZERO_AFTER = 1,
        SPACE_BEFORE = 2,
        SPACE_AFTER = 3
    }

    export enum RESULT_STATUS {
        SUCCESS = 0,
        INTERRUPTION = 1,
        FAILURE
    }

    export enum DATA_FORMAT_SETTING_SCREEN_MODE {
        INDIVIDUAL = 0,
        INIT = 1
    }

    export enum ITEM_TYPE {
        NUMERIC = 0, // 数値型
        CHARACTER = 1, // 文字型
        DATE = 2, // 日付型
        TIME = 3, // 時間型
        INS_TIME = 4, // 時刻型
        AT_WORK_CLS = 7// 在職区分
    }

    export enum SYMBOL {
        AND = 0, // &
        PLUS = 1, // +
        MINUS = 2 // -
    }

    export enum SYMBOL_OPRERATION {
        PLUS = 0, // +
        MINUS = 1 // -
    }
    // export enum SYMBOL_OPRERATION {
    //     PLUS = 0, // +
    //     MINUS = 1 // -
    // }
    export enum CATEGORY_SETTING {
        /**
         * 出力しない
         */
        CATEGORY_SETTING = 0,
        /**
         * データ系タイプ
         */
        DATA_TYPE = 6,
        /**
         * マスタ系タイプ(基準日のみ設定)
         */
        MASTER_TYPE = 7

    }


    export enum CONDITION_SYMBOL {
        CONTAIN = 0,// 含む
        BETWEEN = 1,// 範囲内
        IS = 2,// 同じ
        IS_NOT = 3,// 同じでない
        GREATER = 4,// より大きい
        LESS = 5,//より小さい
        GREATER_OR_EQUAL = 6,//以上
        LESS_OR_EQUAL = 7,//以下
        IN = 8,//同じ(複数)
        NOT_IN = 9 //同じでない(複数)
    }

    export class AcceptanceCodeConvert {
        convertCode: KnockoutObservable<string>;
        convertName: KnockoutObservable<string>;
        dispConvertCode: string;
        dispConvertName: string;
        acceptCodeWithoutSettings: KnockoutObservable<number>;

        constructor(code: string, name: string, acceptWithoutSettings: number) {
            this.convertCode = ko.observable(code);
            this.convertName = ko.observable(name);
            this.dispConvertCode = code;
            this.dispConvertName = name;
            this.acceptCodeWithoutSettings = ko.observable(acceptWithoutSettings);
        }
    }

    export class CodeConvertDetail {
        lineNumber: KnockoutObservable<number>;
        outputItem: KnockoutObservable<string>;
        systemCode: KnockoutObservable<string>;

        constructor(lineNumber: number, output: string, sysCode: string) {
            this.lineNumber = ko.observable(lineNumber);
            this.outputItem = ko.observable(output);
            this.systemCode = ko.observable(sysCode);
        }
    }

    export interface INumberDataFormatSetting {
        formatSelection: number,
        decimalDigit: number,
        decimalPointClassification: number,
        decimalFraction: number,
        outputMinusAsZero: number,
        fixedValueOperation: number,
        fixedValueOperationSymbol: number,
        fixedCalculationValue: number,
        fixedLengthOutput: number,
        fixedLengthIntegerDigit: number,
        fixedLengthEditingMethod: number,
        nullValueReplace: number,
        valueOfNullValueReplace: string,
        fixedValue: number,
        valueOfFixedValue: string
    }

    export class NumberDataFormatSetting {
        formatSelection: KnockoutObservable<number> = ko.observable(null);
        decimalDigit: KnockoutObservable<number> = ko.observable(null);
        decimalPointClassification: KnockoutObservable<number> = ko.observable(null);
        decimalFraction: KnockoutObservable<number> = ko.observable(null);
        outputMinusAsZero: KnockoutObservable<number> = ko.observable(null);
        fixedValueOperation: KnockoutObservable<number> = ko.observable(null);
        fixedValueOperationSymbol: KnockoutObservable<number> = ko.observable(null);
        fixedCalculationValue: KnockoutObservable<number> = ko.observable(null);
        fixedLengthOutput: KnockoutObservable<number> = ko.observable(null);
        fixedLengthIntegerDigit: KnockoutObservable<number> = ko.observable(null);
        fixedLengthEditingMethod: KnockoutObservable<number> = ko.observable(null);
        nullValueReplace: KnockoutObservable<number> = ko.observable(null);
        valueOfNullValueReplace: KnockoutObservable<string> = ko.observable(null);
        fixedValue: KnockoutObservable<number> = ko.observable(null);
        valueOfFixedValue: KnockoutObservable<string> = ko.observable(null);
        constructor(params: INumberDataFormatSetting) {
            this.formatSelection(params ? params.formatSelection : null);
            this.decimalDigit(params ? params.decimalDigit : null);
            this.decimalPointClassification(params ? params.decimalPointClassification : null);
            this.decimalFraction(params ? params.decimalFraction : null);
            this.outputMinusAsZero(params ? params.outputMinusAsZero : null);
            this.fixedValueOperation(params ? params.fixedValueOperation : null);
            this.fixedValueOperationSymbol(params ? params.fixedValueOperationSymbol : null);
            this.fixedCalculationValue(params ? params.fixedCalculationValue : null);
            this.fixedLengthOutput(params ? params.fixedLengthOutput : null);
            this.fixedLengthIntegerDigit(params ? params.fixedLengthIntegerDigit : null);
            this.fixedLengthEditingMethod(params ? params.fixedLengthEditingMethod : null);
            this.nullValueReplace(params ? params.nullValueReplace : null);
            this.valueOfNullValueReplace(params ? params.valueOfNullValueReplace : null);
            this.fixedValue(params ? params.fixedValue : null);
            this.valueOfFixedValue(params ? params.valueOfFixedValue : null);
        }
    }

    export interface ICharacterDataFormatSetting {
        effectDigitLength: number;
        startDigit: number;
        endDigit: number;
        cdEditting: number;
        cdEditDigit: number;
        cdEdittingMethod: number;
        spaceEditting: number;
        cdConvertCd: string;
        cdConvertName: string;
        nullValueReplace: number;
        valueOfNullValueReplace: string;
        fixedValue: number;
        valueOfFixedValue: string;
    }

    export class CharacterDataFormatSetting {
        effectDigitLength: KnockoutObservable<number> = ko.observable(null);
        startDigit: KnockoutObservable<number> = ko.observable(null);
        endDigit: KnockoutObservable<number> = ko.observable(null);
        cdEditting: KnockoutObservable<number> = ko.observable(null);
        cdEditDigit: KnockoutObservable<number> = ko.observable(null);
        cdEdittingMethod: KnockoutObservable<number> = ko.observable(null);
        spaceEditting: KnockoutObservable<number> = ko.observable(null);
        cdConvertCd: KnockoutObservable<string> = ko.observable(null);
        cdConvertName: KnockoutObservable<string> = ko.observable(null);
        nullValueReplace: KnockoutObservable<number> = ko.observable(null);
        valueOfNullValueReplace: KnockoutObservable<string> = ko.observable(null);
        fixedValue: KnockoutObservable<number> = ko.observable(null);
        valueOfFixedValue: KnockoutObservable<string> = ko.observable(null);
        constructor(params: ICharacterDataFormatSetting) {
            this.effectDigitLength(params.effectDigitLength);
            this.startDigit(params.startDigit);
            this.endDigit(params.endDigit);
            this.cdEditting(params.cdEditting);
            this.cdEditDigit(params.cdEditDigit);
            this.cdEdittingMethod(params.cdEdittingMethod);
            this.spaceEditting(params.spaceEditting);
            this.cdConvertCd(params.cdConvertCd);
            this.cdConvertName(params.cdConvertName);
            this.nullValueReplace(params.nullValueReplace);
            this.valueOfNullValueReplace(params.valueOfNullValueReplace);
            this.fixedValue(params.fixedValue);
            this.valueOfFixedValue(params.valueOfFixedValue);
        }
    }

    export class TimeDataFormatSetting {
        nullValueSubs: KnockoutObservable<number> = ko.observable(null);
        outputMinusAsZero: KnockoutObservable<number> = ko.observable(null);
        fixedValue: KnockoutObservable<number> = ko.observable(null);
        valueOfFixedValue: KnockoutObservable<string> = ko.observable(null);
        fixedLengthOutput: KnockoutObservable<number> = ko.observable(null);
        fixedLongIntegerDigit: KnockoutObservable<number> = ko.observable(null);
        fixedLengthEditingMethod: KnockoutObservable<number> = ko.observable(null);
        delimiterSetting: KnockoutObservable<number> = ko.observable(null);
        selectHourMinute: KnockoutObservable<number> = ko.observable(null);
        minuteFractionDigit: KnockoutObservable<number> = ko.observable(null);
        decimalSelection: KnockoutObservable<number> = ko.observable(null);
        fixedValueOperationSymbol: KnockoutObservable<number> = ko.observable(null);
        fixedValueOperation: KnockoutObservable<number> = ko.observable(null);
        fixedCalculationValue: KnockoutObservable<number> = ko.observable(null);
        valueOfNullValueSubs: KnockoutObservable<string> = ko.observable(null);
        minuteFractionDigitProcessCls: KnockoutObservable<number> = ko.observable(null);
        constructor(params: ITimeDataFormatSetting) {
            this.nullValueSubs(params ? params.nullValueSubs : null);
            this.outputMinusAsZero(params ? params.outputMinusAsZero : null);
            this.fixedValue(params ? params.fixedValue : null);
            this.valueOfFixedValue(params ? params.valueOfFixedValue : null);
            this.fixedLengthOutput(params ? params.fixedLengthOutput : null);
            this.fixedLongIntegerDigit(params ? params.fixedLongIntegerDigit : null);
            this.fixedLengthEditingMethod(params ? params.fixedLengthEditingMethod : null);
            this.delimiterSetting(params ? params.delimiterSetting : null);
            this.selectHourMinute(params ? params.selectHourMinute : null);
            this.minuteFractionDigit(params ? params.minuteFractionDigit : null);
            this.decimalSelection(params ? params.decimalSelection : null);
            this.fixedValueOperationSymbol(params ? params.fixedValueOperationSymbol : null);
            this.fixedValueOperation(params ? params.fixedValueOperation : null);
            this.fixedCalculationValue(params ? params.fixedCalculationValue : null);
            this.valueOfNullValueSubs(params ? params.valueOfNullValueSubs : null);
            this.minuteFractionDigitProcessCls(params ? params.minuteFractionDigitProcessCls : null);
        }
    }

    export class InTimeDataFormatSetting {
        nullValueSubs: KnockoutObservable<number> = ko.observable(null);
        outputMinusAsZero: KnockoutObservable<number> = ko.observable(null);
        fixedValue: KnockoutObservable<number> = ko.observable(null);
        valueOfFixedValue: KnockoutObservable<string> = ko.observable(null);
        timeSeletion: KnockoutObservable<number> = ko.observable(null);
        fixedLengthOutput: KnockoutObservable<number> = ko.observable(null);
        fixedLongIntegerDigit: KnockoutObservable<number> = ko.observable(null);
        fixedLengthEditingMethod: KnockoutObservable<number> = ko.observable(null);
        delimiterSetting: KnockoutObservable<number> = ko.observable(2);
        previousDayOutputMethod: KnockoutObservable<string> = ko.observable(null);
        nextDayOutputMethod: KnockoutObservable<number> = ko.observable(null);
        minuteFractionDigit: KnockoutObservable<number> = ko.observable(null);
        decimalSelection: KnockoutObservable<number> = ko.observable(null);
        minuteFractionDigitProcessCls: KnockoutObservable<number> = ko.observable(null);
        valueOfNullValueSubs: KnockoutObservable<number> = ko.observable(null);
        constructor(params: IInTimeDataFormatSetting) {
            this.nullValueSubs(params ? params.nullValueSubs : null);
            this.outputMinusAsZero(params ? params.outputMinusAsZero : null);
            this.fixedValue(params ? params.fixedValue : null);
            this.valueOfFixedValue(params ? params.valueOfFixedValue : null);
            this.timeSeletion(params ? params.timeSeletion : null);
            this.fixedLengthOutput(params ? params.fixedLengthOutput : null);
            this.fixedLongIntegerDigit(params ? params.fixedLongIntegerDigit : null);
            this.fixedLengthEditingMethod(params ? params.fixedLengthEditingMethod : null);
            this.delimiterSetting(params ? params.delimiterSetting : null);
            this.previousDayOutputMethod(params ? params.previousDayOutputMethod : null);
            this.nextDayOutputMethod(params ? params.nextDayOutputMethod : null);
            this.minuteFractionDigit(params ? params.minuteFractionDigit : null);
            this.decimalSelection(params ? params.decimalSelection : null);
            this.minuteFractionDigitProcessCls(params ? params.minuteFractionDigitProcessCls : null);
            this.valueOfNullValueSubs(params ? params.valueOfNullValueSubs : null);
        }
    }

    export interface ITimeDataFormatSetting {
        nullValueSubs: number;
        outputMinusAsZero: number;
        fixedValue: number;
        valueOfFixedValue: string;
        fixedLengthOutput: number;
        fixedLongIntegerDigit: number;
        fixedLengthEditingMethod: number;
        delimiterSetting: number;
        selectHourMinute: number;
        minuteFractionDigit: number;
        decimalSelection: number;
        fixedValueOperationSymbol: number;
        fixedValueOperation: number;
        fixedCalculationValue: number;
        valueOfNullValueSubs: string;
        minuteFractionDigitProcessCls: number;
    }

    export interface IInTimeDataFormatSetting {
        nullValueSubs: number;
        outputMinusAsZero: number;
        fixedValue: number;
        valueOfFixedValue: string;
        timeSeletion: number;
        fixedLengthOutput: number;
        fixedLongIntegerDigit: number;
        fixedLengthEditingMethod: number;
        delimiterSetting: number;
        previousDayOutputMethod: string;
        nextDayOutputMethod: number;
        minuteFractionDigit: number;
        decimalSelection: number;
        minuteFractionDigitProcessCls: number;
        valueOfNullValueSubs: number;
    }

    export class ItemModel {
        code: number;
        name: string;

        constructor(code: number, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    export interface IStandardOutputItem {
        outItemCd: string;
        condSetCd: string;
        outItemName: string;
        itemType: number;
        dispOrder: number;
        categoryItems: Array<ICategoryItem>;
    }

    export interface ICategoryItem {
        categoryItemNo: number;
        categoryItemName: string;
        categoryId: number;
        operationSymbol: number;
        displayOrder: number;
    }

    export class StandardOutputItem {
        isNewMode: boolean;
        outItemCd: KnockoutObservable<string>;
        dispOutputItemCode: string;
        outItemName: KnockoutObservable<string>;
        dispOutputItemName: string;
        condSetCd: KnockoutObservable<string>;
        dispOrder: KnockoutObservable<number>;
        itemType: KnockoutObservable<number>;
        categoryItems: KnockoutObservableArray<CategoryItem>;
        atWorkDataOutputItem: KnockoutObservable<model.AtWorkDataOutputItem> = ko.observable(null);
        characterDataFormatSetting: KnockoutObservable<model.CharacterDataFormatSetting> = ko.observable(null);
        dateDataFormatSetting: KnockoutObservable<model.DateDataFormatSetting> = ko.observable(null);
        inTimeDataFormatSetting: KnockoutObservable<model.InTimeDataFormatSetting> = ko.observable(null);
        numberDataFormatSetting: KnockoutObservable<model.NumberDataFormatSetting> = ko.observable(null);
        timeDataFormatSetting: KnockoutObservable<model.TimeDataFormatSetting> = ko.observable(null);

        constructor(outItemCd: string, outItemName: string, condSetCd: string,
            itemType: number, categoryItems: Array<CategoryItem>) {
            this.outItemCd = ko.observable(outItemCd);
            this.dispOutputItemCode = outItemCd;
            this.outItemName = ko.observable(outItemName);
            this.dispOutputItemName = outItemName;
            this.condSetCd = ko.observable(condSetCd);
            this.itemType = ko.observable(itemType);
            this.categoryItems = ko.observableArray(categoryItems);
        }
    }

    export class CategoryItem {
        categoryId: KnockoutObservable<number>;
        categoryItemNo: KnockoutObservable<number>;
        dispCategoryItemNo: number;
        categoryItemName: KnockoutObservable<string>;
        dispCategoryItemName: string;
        operationSymbol: KnockoutObservable<number>;
        dispOperationSymbol: string;
        displayOrder: any;

        constructor(categoryId: number, categoryItemNo: number, categoryItemName: string,
            operationSymbol: number, displayOrder: number) {
            this.categoryId = ko.observable(categoryId);
            this.categoryItemNo = ko.observable(categoryItemNo);
            this.dispCategoryItemNo = categoryItemNo
            this.categoryItemName = ko.observable(categoryItemName);
            this.dispCategoryItemName = categoryItemName;
            this.operationSymbol = ko.observable(operationSymbol);
            this.displayOrder = displayOrder.toString();
            this.dispOperationSymbol = this.getOperationSymbolText(operationSymbol);
            let self = this;
            self.operationSymbol.subscribe((value) => {
                self.dispOperationSymbol = self.getOperationSymbolText(value);
            });
        }

        getOperationSymbolText(operationSymbol: number): string {
            switch (operationSymbol) {
                case model.SYMBOL.AND:
                    return getText('CMF002_91');
                case model.SYMBOL.PLUS:
                    return getText('CMF002_92');
                case model.SYMBOL.MINUS:
                    return getText('CMF002_93');
                default:
                    return "";
            }
        }
    }

    export interface IAtWorkDataOutputItem {
        closedOutput: string;
        absenceOutput: string;
        fixedValue: number;
        valueOfFixedValue: string;
        atWorkOutput: string;
        retirementOutput: string;
    }

    export class AtWorkDataOutputItem {
        closedOutput: KnockoutObservable<string> = ko.observable(null);
        absenceOutput: KnockoutObservable<string> = ko.observable(null);
        fixedValue: KnockoutObservable<number> = ko.observable(null);
        valueOfFixedValue: KnockoutObservable<string> = ko.observable(null);
        atWorkOutput: KnockoutObservable<string> = ko.observable(null);
        retirementOutput: KnockoutObservable<string> = ko.observable(null);

        constructor(params: IAtWorkDataOutputItem) {
            this.closedOutput(params.closedOutput);
            this.absenceOutput(params.absenceOutput);
            this.fixedValue(params.fixedValue);
            this.valueOfFixedValue(params.valueOfFixedValue);
            this.atWorkOutput(params.atWorkOutput);
            this.retirementOutput(params.retirementOutput);
        }
    }

    export class ExternalOutputCategoryItemData {
        itemNo: KnockoutObservable<any>;
        dispItemNo: any;
        itemName: KnockoutObservable<string>;
        dispitemName: string;
        keywordAtr: number;
        isCheck: KnockoutObservable<boolean>;

        constructor(itemNo: any, itemName: string, keywordAtr: number) {
            this.itemNo = ko.observable(itemNo);
            this.dispItemNo = itemNo.toString();
            this.itemName = ko.observable(itemName);
            this.dispitemName = itemName;
            this.keywordAtr = keywordAtr;
            this.isCheck = ko.observable(false);
        }
    }

    export function getFixedLengthEditingMethod(): Array<ItemModel> {
        return [
            new model.ItemModel(model.FIXED_LENGTH_EDITING_METHOD.ZERO_BEFORE, getText('Enum_FixedLengthEditingMethod_ZERO_BEFORE')),
            new model.ItemModel(model.FIXED_LENGTH_EDITING_METHOD.ZERO_AFTER, getText('Enum_FixedLengthEditingMethod_ZERO_AFTER')),
            new model.ItemModel(model.FIXED_LENGTH_EDITING_METHOD.SPACE_BEFORE, getText('Enum_FixedLengthEditingMethod_SPACE_BEFORE')),
            new model.ItemModel(model.FIXED_LENGTH_EDITING_METHOD.SPACE_AFTER, getText('Enum_FixedLengthEditingMethod_SPACE_AFTER'))
        ];
    }

    export function getOperationSymbol(): Array<ItemModel> {
        return [
            new model.ItemModel(model.SYMBOL_OPRERATION.PLUS, getText('CMF002_389')),
            new model.ItemModel(model.SYMBOL_OPRERATION.MINUS, getText('CMF002_390'))
        ];
    }

    export function getNotUseAtr(): Array<ItemModel> {
        return [
            new model.ItemModel(model.NOT_USE_ATR.USE, getText('CMF002_149')),
            new model.ItemModel(model.NOT_USE_ATR.NOT_USE, getText('CMF002_150'))
        ];
    }

    export function getRounding(): Array<ItemModel> {
        return [
            new model.ItemModel(1, getText('CMF002_384')),
            new model.ItemModel(2, getText('CMF002_385')),
            new model.ItemModel(0, getText('CMF002_386'))
        ];
    }

    export function getTimeSelected(): Array<ItemModel> {
        return [
            new model.ItemModel(0, getText('CMF002_194')),
            new model.ItemModel(1, getText('CMF002_195'))
        ];
    }

    export function getDecimalSelect(): Array<ItemModel> {
        return [
            new model.ItemModel(0, getText('CMF002_201')),
            new model.ItemModel(1, getText('CMF002_202'))
        ];
    }

    export function getSeparator(): Array<ItemModel> {
        return [
            new model.ItemModel(0, getText('CMF002_398')),
            new model.ItemModel(1, getText('CMF002_399')),
            new model.ItemModel(2, getText('CMF002_400'))
        ];
    }

    export function getItemTypes(): Array<ItemModel> {
        return [
            new ItemModel(ITEM_TYPE.NUMERIC, getText('CMF002_366')),
            new ItemModel(ITEM_TYPE.CHARACTER, getText('CMF002_367')),
            new ItemModel(ITEM_TYPE.DATE, getText('CMF002_368')),
            new ItemModel(ITEM_TYPE.TIME, getText('CMF002_369')),
            new ItemModel(ITEM_TYPE.INS_TIME, getText('CMF002_370')),
            new ItemModel(ITEM_TYPE.AT_WORK_CLS, getText('CMF002_371'))
        ];
    }

    export function getConditonSymbol(searchValueCd: string, itemType: ITEM_TYPE): Array<ItemModel> {
        let list = [];
        if (searchValueCd == null || searchValueCd.length == 0) {
            if (itemType == ITEM_TYPE.CHARACTER) {
                list.push(new ItemModel(CONDITION_SYMBOL.CONTAIN, getText('CMF002_372')));
            }
            list.push(new ItemModel(CONDITION_SYMBOL.BETWEEN, getText('CMF002_373')));
            list.push(new ItemModel(CONDITION_SYMBOL.IS, getText('CMF002_374')));
            list.push(new ItemModel(CONDITION_SYMBOL.IS_NOT, getText('CMF002_375')));
            list.push(new ItemModel(CONDITION_SYMBOL.GREATER, getText('CMF002_376')));
            list.push(new ItemModel(CONDITION_SYMBOL.LESS, getText('CMF002_377')));
            list.push(new ItemModel(CONDITION_SYMBOL.GREATER_OR_EQUAL, getText('CMF002_378')));
            list.push(new ItemModel(CONDITION_SYMBOL.LESS_OR_EQUAL, getText('CMF002_379')));
        } else {
            list.push(new ItemModel(CONDITION_SYMBOL.IN, getText('CMF002_380')));
            list.push(new ItemModel(CONDITION_SYMBOL.NOT_IN, getText('CMF002_381')));
        }
        return list
    }

    export class OutputCodeConvert {
        convertCode: KnockoutObservable<string>;
        convertName: KnockoutObservable<string>;
        acceptWithoutSetting: KnockoutObservable<number>;
        dispConvertCode: string;
        dispConvertName: string;

        constructor(code: string, name: string, acceptWithoutSetting: number) {
            this.convertCode = ko.observable(code);
            this.convertName = ko.observable(name);
            this.acceptWithoutSetting = ko.observable(acceptWithoutSetting);
            this.dispConvertCode = code;
            this.dispConvertName = name;
        }
    }

    export function getNextDay(): Array<ItemModel> {
        return [
            new model.ItemModel(0, getText('CMF002_401')),
            new model.ItemModel(1, getText('CMF002_402'))
        ];
    }

    export function getPreDay(): Array<ItemModel> {
        return [
            new model.ItemModel(0, getText('CMF002_403')),
            new model.ItemModel(1, getText('CMF002_404')),
            new model.ItemModel(2, getText('CMF002_405'))
        ];
    }


    export enum EXIOOPERATIONSTATE {

        PERPAKING = 0,

        EXPORTING = 1,

        IMPORTING = 2,

        TEST_FINISH = 3,

        INTER_FINISH = 4,

        FAULT_FINISH = 5,

        CHECKING = 6,

        EXPORT_FINISH = 7,

        IMPORT_FINISH = 8
    }

    export function getStatusEnumS(): Array<ItemModel> {
        return [
            new model.ItemModel(EXIOOPERATIONSTATE.PERPAKING, getText('CMF002_515')),
            new model.ItemModel(EXIOOPERATIONSTATE.EXPORTING, getText('CMF002_516')),
            new model.ItemModel(EXIOOPERATIONSTATE.IMPORTING, getText('CMF002_517')),
            new model.ItemModel(EXIOOPERATIONSTATE.TEST_FINISH, getText('CMF002_518')),
            new model.ItemModel(EXIOOPERATIONSTATE.INTER_FINISH, getText('CMF002_519')),
            new model.ItemModel(EXIOOPERATIONSTATE.FAULT_FINISH, getText('CMF002_520')),
            new model.ItemModel(EXIOOPERATIONSTATE.CHECKING, getText('CMF002_521')),
            new model.ItemModel(EXIOOPERATIONSTATE.EXPORT_FINISH, getText('CMF002_522')),
            new model.ItemModel(EXIOOPERATIONSTATE.IMPORT_FINISH, getText('CMF002_523')),
        ];
    }

    export class DateDataFormatSetting {
        formatSelection: KnockoutObservable<number> = ko.observable(null);
        nullValueSubstitution: KnockoutObservable<number> = ko.observable(null);
        fixedValue: KnockoutObservable<number> = ko.observable(null);
        valueOfNullValueSubs: KnockoutObservable<string> = ko.observable(null);
        valueOfFixedValue: KnockoutObservable<string> = ko.observable(null);

        constructor(params: IDateDataFormatSetting) {
            this.formatSelection(params ? params.formatSelection : null);
            this.nullValueSubstitution(params ? params.nullValueSubstitution : null);
            this.valueOfNullValueSubs(params ? params.valueOfNullValueSubs : null);
            this.fixedValue(params ? params.fixedValue : null);
            this.valueOfFixedValue(params ? params.valueOfFixedValue : null);
        }
    }

    export interface IDateDataFormatSetting {
        formatSelection: number;
        nullValueSubstitution: number;
        fixedValue: number;
        valueOfNullValueSubs: string;
        valueOfFixedValue: string;
    }
}