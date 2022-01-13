import { IAppDispInfoStartupOutput, IApplication } from "../../s04/a/define";

export interface IOptionalItem {
    controlOfAttendanceItemsDto: IControlOfAttendanceItemsDto;
    optionalItemDto: IOptionalItemDto;
}

export interface IOptionalItemDto {
    calcResultRange: {
        amountRange: {
            dailyAmountRange: {
                lowerLimit: number | null;
                upperLimit: number | null;
            }
        }
        numberRange: {
            dailyNumberRange: {
                lowerLimit: number | null;
                upperLimit: number | null;
            }
        }
        timeRange:{
            dailyTimeRange: {
                lowerLimit: number | null;
                upperLimit: number | null;
            }
        }
        lowerCheck: true;
        upperCheck: boolean;
    };
    description: string | null;
    empConditionAtr: number | null;
    formulas: any[];
    dispOrder: number | null;
    optionalItemAtr: number | null;
    optionalItemName: string;
    optionalItemNo: number | null;
    performanceAtr: number | null;
    unit: string;
    usageAtr: number | null;
    inputCheck: boolean;
}

export interface IControlOfAttendanceItemsDto {
    companyID: string;
    headerBgColorOfDailyPer: string;
    inputUnitOfTimeItem: number | null;
    itemDailyID: number | null;
}

export interface IOptionalItemAppSet {
    code: string;
    name: string;
    useAtr: number | null;
    note: string;
    settingItems: IOptItemSet[];
}

export interface IOptItemSet {
    no: number | null;
    dispOrder: number | null;
}

export interface OptionalItemApplication {
    amountLower: number | null;
    amountUpper: number | null;
    lowerCheck: boolean | null;
    numberLower: number | null;
    numberUpper: number | null;
    timeLower: number | null;
    timeUpper: number | null;
    upperCheck: boolean | null;
    unit: string;
    inputUnitOfItem: number | null;
    optionalItemName: string;
    optionalItemNo: number | null;
    optionalItemAtr: number | null;
    time: number | null;
    number: number | null;
    amount: number | null;
    description: string | null;
    dispOrder: number | null;
    inputCheckbox: boolean;
}

interface optionalItems {
    itemNo: number | null;
    times: number | null;
    amount: number | null;
    time: number | null;
}

export interface IParams {
    appDetail: {
        application: {
            code: string | null;
            name: string | null;
            note: string | null;
            optionalItems: optionalItems[];
        }
        controlOfAttendanceItems: IControlOfAttendanceItemsDto[];
        optionalItems: any[];
    };
    appDispInfoStartupOutput: IAppDispInfoStartupOutput;
}
