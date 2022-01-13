module nts.uk.at.view.kdl009.a {
  export module service {
    const paths: any = {
      getEmployee: "at/request/dialog/employmentsystem/getEmployee",
      getAcquisitionNumberRestDays: "at/request/dialog/employmentsystem/getAcquisitionNumberRestDays/{0}/{1}"
    }

    export function getEmployee(param: EmployeeParamDto): JQueryPromise<Array<EmployeeBasicInfoDto>> {
      return nts.uk.request.ajax(paths.getEmployee, param);
    }

    export function getAcquisitionNumberRestDays(employeeId: string, baseDate: string): JQueryPromise<AcquisitionNumberRestDayDto> {
      const path = nts.uk.text.format(paths.getAcquisitionNumberRestDays, employeeId, baseDate);
      return nts.uk.request.ajax(path);
    }
  }

  export interface EmployeeParamDto {
    employeeIds: Array<string>,
    baseDate: string
  }

  export interface EmployeeBasicInfoDto {
    personId: string,
    employeeId: string,
    businessName: string,
    gender: number,
    birthday: string,
    employeeCode: string,
    jobEntryDate: string,
    retirementDate: string
  }

  export interface AcquisitionNumberRestDayDto {
    // field 残数詳細
    listRemainNumberDetail: RemainNumberDetailDto[];
    // field 管理区分
    isManagementSection: boolean;
    // field 紐付け管理
    listPegManagement: PegManagementDto[];
    // field 予定使用日数
    scheduledUsageDay: number;
    // field 予定使用時間
    scheduledUsageHour: number;
    // field 予定残数
    scheduledRemainingDay: number;
    // field 予定残数時間
    scheduledRemainingHour: number;
    // field 予定発生日数
    scheduleOccurrencedDay: number;
    // field 予定発生時間
    scheduleOccurrencedHour: number;
    // field 使用日数
    usageDay: number;
    // field 使用時間
    usageHour: number;
    // field 使用期限
    expiredDay: number;
    // field 残数
    remainingDay: number;
    // field 残数時間
    remainingHour: number;
    // field 発生日数
    occurrenceDay: number;
    // field 発生時間
    occurrenceHour: number;
    // field 繰越日数
    carryForwardDay: number;
    // field 繰越時間
    carryForwardHour: number;
  }

  export interface RemainNumberDetailDto {
    // field 当月で期限切れ
    expiredInCurrentMonth: boolean;
    // field 期限日
    expirationDate: string; // yyyy/mm/dd
    // field 消化数
    digestionNumber: number;
    // field 消化日
    digestionDate: string; // yyyy/mm/dd
    // field 消化時間
    digestionHour: number;
    // field 発生数
    occurrenceNumber: number;
    // field 発生日
    occurrenceDate: string; // yyyy/mm/dd
    // field 発生時間
    occurrenceHour: number;
    // field 管理データ状態区分
    managementDataStatus: number;
  }

  export interface PegManagementDto {
    // field 使用日
    usageDate: any;
    // field 使用日数
    usageDay: number;
    // field 使用時間数
    usageHour: number;
    // field 開発日
    occurrenceDate: any;
  }

}