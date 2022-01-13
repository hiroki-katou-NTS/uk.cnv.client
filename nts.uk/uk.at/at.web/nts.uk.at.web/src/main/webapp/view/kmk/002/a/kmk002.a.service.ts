module nts.uk.at.view.kmk002.a {
    export module service {
        import exportFile = nts.uk.request.exportFile;
        /**
         *  Service paths
         */
        let servicePath: any = {
            findOptionalItemDetail: 'ctx/at/record/optionalitem/find',
            findOptionalItemHeaders: 'ctx/at/record/optionalitem/findall/{0}',
            saveOptionalItem: 'ctx/at/record/optionalitem/save',
            getOptItemEnum: 'ctx/at/record/optionalitem/getenum',
            findByLangId: "ctx/at/record/optionalitem/findNameOther/{0}",
        };

        export function saveAsExcel(languageId: string): JQueryPromise<any> {
            let program = __viewContext.program.programName;
            let domainType = "KMK002";
            if (program.length > 1) {
                domainType = domainType + program;
            }
            return exportFile('/masterlist/report/print', { domainId: "CalFormulasItem", domainType: domainType, languageId: languageId, reportType: 0 });
        }

        /**
         * Call service to get optional item enum
         */
        export function getOptItemEnum(): JQueryPromise<model.OptItemEnumDto> {
            return nts.uk.request.ajax(servicePath.getOptItemEnum);
        }

        /**
         * Call service to save optional item
         */
        export function saveOptionalItem(command: model.OptionalItemDto): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.saveOptionalItem, command);
        }

        /**
         * Call service to get optional item detail
         */
        export function findOptionalItemDetail(itemNo: number, langId: string) {
            return nts.uk.request.ajax(servicePath.findOptionalItemDetail + '/' + itemNo+"/" + langId);
        }

        /**
         * Call service to get optional item header
         */
        export function findOptionalItemHeaders(langId: string): JQueryPromise<Array<model.OptionalItemHeader>> {
            var path = nts.uk.text.format(servicePath.findOptionalItemHeaders, langId);
            return nts.uk.request.ajax(path);
        }
        
        export function findByLangId(langId: string): JQueryPromise<any> {
            var path = nts.uk.text.format(servicePath.findByLangId, langId);
            return nts.uk.request.ajax("at", path);
        }
        /**
         * Data Model
         */
        export module model {
            export interface OptionalItemHeader {
                itemNo: number;
                itemName: string;
                usageAtr: number;
                performanceAtr: number;
                nameNotJP: string;
            }

            /**
             * OptionalItemDto
             */
            export interface OptionalItemDto {
                optionalItemNo: number;
                optionalItemName: string;
                optionalItemAtr: number;
                usageAtr: number;
                empConditionAtr: number;
                performanceAtr: number;
                calcResultRange: any;
                unit: string;
                formulas: Array<FormulaDto>;
                calAtr: number;
                note: string;
                description: string;
                inputCheck: boolean;
            }
            /**
             * CalcResultRangeDto
             */
            export interface CalcResultRangeDto {
                upperCheck: boolean;
                lowerCheck: boolean;

                numberUpperDay: number;
                numberLowerDay: number;
                amountUpperDay: number;
                amountLowerDay: number;
                timeUpperDay: number;
                timeLowerDay: number;

                numberUpperMonth: number;
                numberLowerMonth: number;
                amountUpperMonth: number;
                amountLowerMonth: number;
                timeUpperMonth: number;
                timeLowerMonth: number;
            }
            /**
             * CalcFormulaDto
             */
            export interface FormulaDto {
                formulaId: string;
                formulaName: string;
                optionalItemNo: number;
                orderNo: number;
                symbolValue: string;
                formulaAtr: number;
                calcAtr: number;
                formulaSetting: FormulaSettingDto;
                itemSelection: ItemSelectionDto;
                monthlyRounding: RoundingDto;
                dailyRounding: RoundingDto;
            }
            /**
             * RoundingDto
             */
            export interface RoundingDto {
                numberRounding: number;
                numberUnit: number;
                timeRounding: number;
                timeUnit: number;
                amountRounding: number;
                amountUnit: number;
            }
            /**
             * FormulaSettingDto
             */
            export interface FormulaSettingDto {
                minusSegment: number;
                operator: number;
                leftItem: SettingItemDto;
                rightItem: SettingItemDto;
            }
            /**
             * SettingItemDto
             */
            export interface SettingItemDto {
                settingMethod: number;
                dispOrder: number;
                inputValue: number;
                formulaItemId: string;
            }
            /**
             * ItemSelectionDto
             */
            export interface ItemSelectionDto {
                minusSegment: number;
                attendanceItems: Array<AttendanceItemDto>;
            }
            /**
             * SelectedAttendanceItemDto
             */
            export interface AttendanceItemDto {
                attendanceItemId: number;
                operator: number;
                operatorText?: string;
                attendanceItemName?: string;
                attendanceItemDisplayNumber?: number;
            }
            export interface RoundingDto {
                unit: EnumConstantDto[];
                rounding: EnumConstantDto[];
            }
            export interface OptItemEnumDto {
                itemAtr: EnumConstantDto[];
                useAtr: EnumConstantDto[];
                empConditionAtr: EnumConstantDto[];
                performanceAtr: EnumConstantDto[];
                formulaAtr: EnumConstantDto[];
                calcAtr: EnumConstantDto[];
                minusSegment: EnumConstantDto[];
                operatorAtr: EnumConstantDto[];
                settingMethod: EnumConstantDto[];
                dispOrder: EnumConstantDto[];
                addSubAtr: EnumConstantDto[];
                amountRounding: RoundingDto;
                timeRounding: RoundingDto;
                numberRounding: RoundingDto;
            }
            export interface EnumConstantDto {
                value: number;
                fieldName: string;
                localizedName: string;
            }

            export class EnumAdaptor {

                /**
                 * Get number value of fieldName.
                 * @param: list enum
                 */
                public static valueOf(fieldName: string, enumConstant: EnumConstantDto[]): number {
                    let result;
                    let found = _.find(enumConstant, item => item.fieldName == fieldName);
                    if (found) {
                        result = found.value;
                    }
                    return result;
                }

                /**
                 * Get localizedName of number value.
                 * @param: list enum
                 */
                public static localizedNameOf(value: number, enumConstant: EnumConstantDto[]): string {
                    let result;
                    let found = _.find(enumConstant, item => item.value == value);
                    if (found) {
                        result = found.localizedName;
                    }
                    return result;
                }
            }



        }
    }


}
