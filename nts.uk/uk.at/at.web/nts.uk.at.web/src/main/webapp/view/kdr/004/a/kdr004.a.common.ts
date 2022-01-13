module nts.uk.at.view.kdr004 {
    export module common {

        const API = {

        };

        export class ItemModel {
            code: string;
            name: string;

            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }

        export class BoxModel {
            id: number;
            name: string;
            constructor(id: number, name: string) {
                var vm = this;
                vm.id = id;
                vm.name = name;
            }
        }

        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export class SelectType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
        }

        // start CCG001
        export interface GroupOption {
            /** Common properties */
            showEmployeeSelection?: boolean; // 検索タイプ
            systemType: number; // システム区分
            showQuickSearchTab?: boolean; // クイック検索
            showAdvancedSearchTab?: boolean; // 詳細検索
            showBaseDate?: boolean; // 基準日利用
            showClosure?: boolean; // 就業締め日利用
            showAllClosure?: boolean; // 全締め表示
            showPeriod?: boolean; // 対象期間利用
            periodFormatYM?: boolean; // 対象期間精度
            maxPeriodRange?: string; // 最長期間
            showSort?: boolean; // 並び順利用
            nameType?: number; // 氏名の種類

            /** Required parameter */
            baseDate?: any; // 基準日 KnockoutObservable<string> or string
            inService: boolean; // 在職区分
            leaveOfAbsence: boolean; // 休職区分
            closed: boolean; // 休業区分
            retirement: boolean; // 退職区分

            /** Quick search tab options */
            showAllReferableEmployee?: boolean; // 参照可能な社員すべて
            showOnlyMe?: boolean; // 自分だけ
            showSameDepartment?: boolean; //同じ部門の社員
            showSameDepartmentAndChild?: boolean; // 同じ部門とその配下の社員
            showSameWorkplace?: boolean; // 同じ職場の社員
            showSameWorkplaceAndChild?: boolean; // 同じ職場とその配下の社員

            /** Advanced search properties */
            showEmployment?: boolean; // 雇用条件
            showDepartment?: boolean; // 部門条件
            showWorkplace?: boolean; // 職場条件
            showClassification?: boolean; // 分類条件
            showJobTitle?: boolean; // 職位条件
            showWorktype?: boolean; // 勤種条件
            isMutipleCheck?: boolean; // 選択モード

            /** Optional properties */
            isInDialog?: boolean;
            showOnStart?: boolean;
            isTab2Lazy?: boolean;
            tabindex?: number;

            /** Data returned */
            returnDataFromCcg001: (data: Ccg001ReturnedData) => void;
        }

        export interface Ccg001ReturnedData {
            baseDate: string; // 基準日
            closureId?: number; // 締めID
            periodStart: string; // 対象期間（開始)
            periodEnd: string; // 対象期間（終了）
            listEmployee: Array<EmployeeSearchDto>; // 検索結果
        }

        export interface EmployeeSearchDto {
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            workplaceId: string;
            workplaceName: string;
        }

        export interface UnitModel {
            id?: string;
            code: string;
            name?: string;
            affiliationName?: string;
            isAlreadySetting?: boolean;
            optionalColumn?: any;
        }

        export interface UnitAlreadySettingModel {
            code: string;
            isAlreadySetting: boolean;
        }

        export interface UserSpecificInformation {
            itemSelection?: string; //項目選択の選択肢
            zeroDisplayClassification?: string; //ゼロ表示区分
            pageBreakSpecification?: string; //改ページ指定
        }
    }
}