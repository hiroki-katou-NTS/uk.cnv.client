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
                showEmployeeSelection?: boolean; // ???????????????
                systemType: number; // ??????????????????
				employeesDoNotManageSchedules?: KnockoutObservable<boolean>; // ??????????????????????????????????????????????????????
                showQuickSearchTab?: boolean; // ??????????????????
                showAdvancedSearchTab?: boolean; // ????????????
                showBaseDate?: boolean; // ???????????????
                showClosure?: boolean; // ?????????????????????
                showAllClosure?: boolean; // ???????????????
                showPeriod?: boolean; // ??????????????????
                periodFormatYM?: boolean; // ??????????????????
                maxPeriodRange?: string; // ????????????
                showSort?: boolean; // ???????????????
                nameType?: number; // ???????????????

                /** Required parameter */
                baseDate?: any; // ????????? KnockoutObservable<string> or string
                periodStartDate?: any; // ????????????????????? KnockoutObservable<string> or string
                periodEndDate?: any; // ????????????????????? KnockoutObservable<string> or string
                dateRangePickerValue?: KnockoutObservable<any>;
                inService: boolean; // ????????????
                leaveOfAbsence: boolean; // ????????????
                closed: boolean; // ????????????
                retirement: boolean; // ????????????

                /** Quick search tab options */
                showAllReferableEmployee?: boolean; // ??????????????????????????????
                showOnlyMe?: boolean; // ????????????
                showSameDepartment?: boolean; //?????????????????????
                showSameDepartmentAndChild?: boolean; // ????????????????????????????????????
                showSameWorkplace?: boolean; // ?????????????????????
                showSameWorkplaceAndChild?: boolean; // ????????????????????????????????????

                /** Advanced search properties */
                showEmployment?: boolean; // ????????????
                showWorkplace?: boolean; // ????????????
                showClassification?: boolean; // ????????????
                showJobTitle?: boolean; // ????????????
                showWorktype?: boolean; // ????????????
                isMutipleCheck?: boolean; // ???????????????
                showDepartment: boolean; // ????????????
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
                baseDate: string; // ?????????
                closureId?: number; // ??????ID
                periodStart: string; // ?????????????????????)
                periodEnd: string; // ????????????????????????
                listEmployee: Array<EmployeeSearchDto>; // ????????????
            }

            export class SelectedInformation {
                sortOrder: number; // ?????????????????????????????????
                selectedClosureId: number; // ??????????????????????????????
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
                userId: String; // ?????????ID
                companyId: String; // ??????ID
                humanResourceInfo: SelectedInformation; // ?????????????????????????????????
                personalInfo: SelectedInformation; // ???????????????????????????????????????
                employmentInfo: SelectedInformation; // ?????????????????????????????????
                salaryInfo: SelectedInformation; // ?????????????????????????????????
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