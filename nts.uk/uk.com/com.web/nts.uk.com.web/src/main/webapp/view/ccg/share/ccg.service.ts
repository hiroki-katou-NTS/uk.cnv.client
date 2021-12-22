module nts.uk.com.view.ccg.share.ccg {


    export module service {

        // Service paths.
        var servicePath = {
            searchEmployeeByLogin: "query/employee/find/currentlogin",
            searchWorkplaceOfEmployee: "basic/organization/employee/workplaceemp",
            searchAllWorkType: "at/record/businesstype/findAll",
            getEmploymentCodeByClosureId: "ctx/at/shared/workrule/closure/findEmpByClosureId",
            getRefRangeBySysType: "ctx/sys/auth/role/getrefrangebysystype",
            getClosuresByBaseDate: "ctx/at/shared/workrule/closure/getclosuresbybasedate",
            getClosureByCurrentEmployee: "ctx/at/shared/workrule/closure/getclosurebycurrentemployee",
            calculatePeriod: "ctx/at/shared/workrule/closure/calculateperiod",
            getClosureTiedByEmployment: "ctx/at/shared/workrule/closure/getclosuretiedbyemployment",
            getCurrentHistoryItem: "bs/employee/employment/history/getcurrenthistoryitem",
            getPersonalRoleFuturePermit: "ctx/sys/auth/grant/roleindividual/get/futurerefpermit",
            getEmploymentRoleFuturePermit: "at/auth/workplace/employmentrole/get/futurerefpermit",
            getListWorkplaceId: "ctx/sys/auth/role/getListWorkplaceId",
            findRegulationInfoEmployee: "query/employee/find",
            searchByName: "query/employee/find/name",
            searchByCode: "query/employee/find/code",
            searchByEntryDate: "query/employee/find/entrydate",
            searchByRetirementDate: "query/employee/find/retirementdate",
			getCanManageWpkForLoginUser: "at/auth/workplace/manager/find/loginnedUser",
        };

        /**
         * Find regulation info employee
         */
        export function findRegulationInfoEmployee(query: model.EmployeeQueryParam): JQueryPromise<Array<model.EmployeeSearchDto>> {
            return nts.uk.request.ajax('com', servicePath.findRegulationInfoEmployee, query);
        }
         
        export function getCanManageWpkForLoginUser(): JQueryPromise<Array<any>> {
            return nts.uk.request.ajax('com', servicePath.getCanManageWpkForLoginUser);
        }

        export function searchByCode(query: model.SearchEmployeeQuery): JQueryPromise<Array<model.EmployeeSearchDto>> {
            return nts.uk.request.ajax('com', servicePath.searchByCode, query);
        }

        export function searchByName(query: model.SearchEmployeeQuery): JQueryPromise<Array<model.EmployeeSearchDto>> {
            return nts.uk.request.ajax('com', servicePath.searchByName, query);
        }
        export function searchByEntryDate(query: model.SearchEmployeeQuery): JQueryPromise<Array<model.EmployeeSearchDto>> {
            return nts.uk.request.ajax('com', servicePath.searchByEntryDate, query);
        }
        export function searchByRetirementDate(query: model.SearchEmployeeQuery): JQueryPromise<Array<model.EmployeeSearchDto>> {
            return nts.uk.request.ajax('com', servicePath.searchByRetirementDate, query);
        }

        /**
         * Get personal role future permit
         */
        export function getPersonalRoleFuturePermit(): JQueryPromise<boolean> {
            return nts.uk.request.ajax('com', servicePath.getPersonalRoleFuturePermit);
        }

        /**
         * Get personal role future permit
         */
        export function getEmploymentRoleFuturePermit(): JQueryPromise<boolean> {
            return nts.uk.request.ajax('at', servicePath.getEmploymentRoleFuturePermit);
        }

        /**
         * Get current history item.
         */
        export function getCurrentHistoryItem(): JQueryPromise<any> {
            return nts.uk.request.ajax('com', servicePath.getCurrentHistoryItem);
        }

        /**
         * Get Reference Range By System Type
         */
        export function getRefRangeBySysType(sysType: number): JQueryPromise<number> {
            return nts.uk.request.ajax('com', servicePath.getRefRangeBySysType + '/' + sysType);
        }

        /**
         * Get list closure by base date
         */
        export function getClosuresByBaseDate(baseDate: string): JQueryPromise<Array<any>> {
            return nts.uk.request.ajax('at', servicePath.getClosuresByBaseDate + '/' + baseDate);
        }

        /**
         * Get closure id by current login employee
         */
        export function getClosureByCurrentEmployee(baseDate: string): JQueryPromise<number> {
            return nts.uk.request.ajax('at', servicePath.getClosureByCurrentEmployee + '/' + baseDate);
        }
        
        /**
         * Get Employment Code By ClosureId
         */
        export function getEmploymentCodeByClosureId(closureId: number): JQueryPromise<Array<string>> {
            return nts.uk.request.ajax('at', servicePath.getEmploymentCodeByClosureId + '/' + closureId);
        }

        /**
         * Get closure tied by employment
         */
        export function getClosureTiedByEmployment(employmentCd: string): JQueryPromise<number> {
            return nts.uk.request.ajax('at', servicePath.getClosureTiedByEmployment + '/' + employmentCd);
        }

        /**
         * Get employee range selection
         */
        export function getEmployeeRangeSelection(): JQueryPromise<model.EmployeeRangeSelection> {
            const key = __viewContext.user.employeeId + '' + __viewContext.user.companyId;
            return nts.uk.characteristics.restore(key);
        }

        /**
         * Save employee range selection
         */
        export function saveEmployeeRangeSelection(data: model.EmployeeRangeSelection): JQueryPromise<void> {
            const key = data.userId + '' + data.companyId;
            return nts.uk.characteristics.save(key, data);
        }

        /**
         * Calculate period
         */
        export function calculatePeriod(closureId: number, yearMonth: number): JQueryPromise<model.DatePeriodDto> {
            const param = '/' + closureId + '/' + yearMonth;
            return nts.uk.request.ajax('at', servicePath.calculatePeriod + param);
        }
        
         /**
         * Calculate period
         */
        export function calculatePeriod105458(closureId: number): JQueryPromise<model.DatePeriodDto> {
            const param = '/' + closureId;
            return nts.uk.request.ajax('at', servicePath.calculatePeriod + param);
        }

        /**
         * call service get employee by login
         */
        
        export function searchEmployeeByLogin(query): JQueryPromise<model.EmployeeSearchDto> {
            return nts.uk.request.ajax('com', servicePath.searchEmployeeByLogin, query);
        }



        /**
         * search WorkPlace of Employee
         */
        export function searchWorkplaceOfEmployee(baseDate: Date): JQueryPromise<string[]> {
            return nts.uk.request.ajax('com', servicePath.searchWorkplaceOfEmployee, baseDate);
        }
        
        /**
         * search all worktype
         */
        export function searchAllWorkType(): JQueryPromise<Array<model.BusinessType>> {
            return nts.uk.request.ajax('at', servicePath.searchAllWorkType);
        }
        
        /**
         * get List WorkPlaceId
         */
        export function getListWorkplaceId(baseDate: string, referenceRange: number): JQueryPromise<any> {
            return nts.uk.request.ajax('com', servicePath.getListWorkplaceId, { baseDate: baseDate, referenceRange: referenceRange });
        }
        
        export module model{

            export interface SearchEmployeeQuery {
                systemType: number;
                code?: string;
                name?: string;
                useClosure?: boolean;
                closureId?: number;
                period?: any;
                referenceDate: any;
				employeesDoNotManageSchedules: boolean;
            }

            export class EmployeeSearchDto {
                employeeId: string;
                employeeCode: string;
                employeeName: string;
                affiliationCode: string;
                affiliationId: string;
                affiliationName: string;
            }

            export interface GroupOption {

                /** Common properties */
                showEmployeeSelection?: boolean; // 検索タイプ
                systemType: number; // システム区分
				employeesDoNotManageSchedules?: KnockoutObservable<boolean>; // スケジュール管理しない社員を取り除く
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
                periodStartDate?: any; // 対象期間開始日 KnockoutObservable<string> or string
                periodEndDate?: any; // 対象期間終了日 KnockoutObservable<string> or string
                dateRangePickerValue?: KnockoutObservable<any>;
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
                showWorkplace?: boolean; // 職場条件
                showClassification?: boolean; // 分類条件
                showJobTitle?: boolean; // 職位条件
                showWorktype?: boolean; // 勤種条件
                isMutipleCheck?: boolean; // 選択モード
                showDepartment: boolean; // 部門条件
                // showDelivery: boolean; not covered

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

            export class SelectedInformation {
                sortOrder: number; // 前回選択していた並び順
                selectedClosureId: number; // 前回選択していた締め
                constructor() {
                    let self = this;
                    self.sortOrder = null;
                    self.selectedClosureId = null;
                }
            }

            export interface EmployeeQueryParam {
                roleId: string;
                baseDate: string;
                referenceRange: number;
                filterByEmployment: boolean;
                employmentCodes: Array<string>;
                filterByDepartment: boolean;
                departmentCodes: Array<number>;
                filterByWorkplace: boolean;
                workplaceCodes: Array<string>;
                filterByClassification: boolean;
                classificationCodes: Array<any>;
                filterByJobTitle: boolean;
                jobTitleCodes: Array<string>;
                filterByWorktype: boolean;
                worktypeCodes: Array<string>;
                filterByClosure: boolean;
                closureIds: Array<number>;

                periodStart: string;
                periodEnd: string;

                includeIncumbents: boolean;
                includeWorkersOnLeave: boolean;
                includeOccupancy: boolean;
                includeRetirees: boolean;
                retireStart: string;
                retireEnd: string;

                sortOrderNo: number;
                nameType: number;
                systemType: number;
				employeesDoNotManageSchedules: boolean;
            }

            export interface DatePeriodDto {
                startDate: string;
                endDate: string
            }

            export interface BusinessType {
                businessTypeCode: string;
                businessTypeName: string;
            }

            export class EmployeeRangeSelection {
                userId: String; // ユーザID
                companyId: String; // 会社ID
                humanResourceInfo: SelectedInformation; // 人事の選択している情報
                personalInfo: SelectedInformation; // 個人情報の選択している情報
                employmentInfo: SelectedInformation; // 就業の選択している情報
                salaryInfo: SelectedInformation; // 給与の選択している情報
                constructor() {
                    let self = this;
                    self.userId = __viewContext.user.employeeId;
                    self.companyId = __viewContext.user.companyId;
                    self.humanResourceInfo = new SelectedInformation();
                    self.personalInfo = new SelectedInformation();
                    self.employmentInfo = new SelectedInformation();
                    self.salaryInfo = new SelectedInformation();
                }
            }
            
            export class EmployeeDto {
                employeeID: string;
                employeeCode: string;
                hireDate: string;
                classificationCode: string;
                name: string;
                jobTitleCode: string;
                workplaceCode: string;
                departmentCode: string;
                employmentCode: string;
            }
        }
    }
}