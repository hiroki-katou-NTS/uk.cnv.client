module nts.uk.ui.at.kdw013.a {
    export type API = {
        readonly ADD: string;
        readonly START: string;
        readonly DELETE: string;
        readonly REGISTER: string;
        readonly CHANGE_DATE: string;
        readonly START_F: string;
        readonly ADD_FAV_TASK_F: string;
        readonly UPDATE_TASK_NAME_F: string;
        readonly START_G: string;
        readonly ADD_FAV_TASK_G: string;
        readonly UPDATE_TASK_NAME_G: string;
    };

    export type StartProcessDto = {
        // 工数入力を起動する
        startManHourInputResultDto: StartManHourInputResultDto;

        // 参照可能職場・社員を取得する
        refWorkplaceAndEmployeeDto: GetRefWorkplaceAndEmployeeResultDto;

		isChange: boolean;
    };

    export type ProcessInitialStartDto = {
        // 工数入力を起動する
        startManHourInputResultDto: StartManHourInputResultDto;

        // 参照可能職場・社員を取得する
        refWorkplaceAndEmployeeDto: GetRefWorkplaceAndEmployeeDto;
    };

    export type StartManHourInputResultDto = {
        /** 作業枠利用設定 */
        taskFrameUsageSetting: TaskFrameUsageSettingDto;

        /** List<作業> */
        tasks: c.TaskDto[];

        /** List<勤務場所> */
        workLocations: WorkLocationDto[];
    };

    export type GetRefWorkplaceAndEmployeeResultDto = {
        /** 社員の所属情報(Map<社員ID,職場ID>)*/
        employeeInfos: {
            [key: string]: string;
        };

        /** List＜社員ID（List）から社員コードと表示名を取得＞*/
        lstEmployeeInfo: EmployeeBasicInfoDto[];

        /** List＜職場情報一覧＞ */
        workplaceInfos: WorkplaceInfoDto[];
    }

    export type WorkplaceInfoDto = {
        /** The history id. */
        historyId: string;

        /** The workplace code. */
        workplaceCode: string;

        /** The workplace name. */
        workplaceName: string;

        /** The wkp generic name. */
        wkpGenericName: string;

        /** The wkp display name. */
        wkpDisplayName: string;

        /** The outside wkp code. */
        outsideWkpCode: string;
    }

    export type GetRefWorkplaceAndEmployeeDto = {
        /** 社員の所属情報(Map<社員ID,職場ID>)*/
        employeeInfos: {
            [key: string]: string;
        };

        /** List＜社員ID（List）から社員コードと表示名を取得＞*/
        lstEmployeeInfo: EmployeeBasicInfoDto[];

        /** List＜職場情報一覧＞ */
        workplaceInfos: WorkplaceInfo[];
    };

    export type EmployeeBasicInfoDto = {
        /**
         * 社員ID
         */
        employeeId: string;
        /**
         * 社員コード
         */
        employeeCode: string;
        /**
         * 社員名
         */
        employeeName: string;
        /**
         * 所属CD
         */
        affiliationCode: string;
        /**
         * 所属ID
         */
        affiliationId: string;
        /**
         * 所属名称
         */
        affiliationName: string;
    };

    export type WorkplaceInfo = {
        /** 職場ID */
        workplaceId: string;

        /** The workplace code. */
        workplaceCode: string;

        /** The workplace name. */
        workplaceName: string;

        /** The wkp generic name. */
        wkpGenericName: string;

        /** The wkp display name. */
        wkpDisplayName: string;

        /** The outside wkp code. */
        outsideWkpCode: string;
    };

    export type TaskFrameUsageSettingDto = {
        frameSettingList: TaskFrameSettingDto[];
    };

    export type TaskFrameSettingDto = {
        frameNo: number;

        frameName: string | null;

        useAtr: number;
    };

    export type WorkLocationDto = {
        /** 契約コード */
        contractCode: string;

        /** コード */
        workLocationCD: string;

        /** 名称 */
        workLocationName: string;

        /** 打刻範囲 . 半径*/
        radius: number;

        /** 打刻範囲.地理座標.緯度*/
        latitude: number;

        /** 打刻範囲.地理座標.経度*/
        longitude: number;

        /** IPアドレス一覧*/
        listIPAddress: Ipv4AddressDto[];

        /** 職場*/
        listWorkplace: WorkplacePossibleCmd[];
    };

    export type Ipv4AddressDto = {
        /** net1 */
        net1: number;

        /** net2 */
        net2: number;

        /** host1 */
        host1: number;

        /** host2 */
        host2: number;
    };

    export type WorkplacePossibleCmd = {
        /**
         * 会社ID
         */
        companyId: string;

        /**
         * 職場ID
         */
        workpalceId: string;
    };

    export type RegisterWorkContentParam = {
        /** 対象日 */
        date: string;

        /** 対象者 */
        employeeId: string;

        /** 編集状態<Enum.日別勤怠の編集状態> */
        editStateSetting: EditStateSetting;

        /** List<年月日,List<作業詳細>> */
        workDetails: WorkDetailDto[];
    };

    /**
     * 日別勤怠の編集状態
     */
    enum EditStateSetting {
        /** 手修正（本人） */
        HAND_CORRECTION_MYSELF = 0,
        /** 手修正（他人） */
        HAND_CORRECTION_OTHER = 1,
        /** 申請反映 */
        REFLECT_APPLICATION = 2,
        /** 打刻反映 */
        IMPRINT = 3,
        /** 申告反映 */
        DECLARE_APPLICATION = 4

    };

    export type WorkDetailDto = {
        /** 年月日 */
        date: string;

        /** List<作業詳細> */
        lstWorkDetailsParam: WorkDetailsParamDto[];
    };

    export type WorkDetailsParamDto = {
        // 応援勤務枠No: 応援勤務枠No
        supportFrameNo: number;

        // 時間帯: 時間帯
        timeZone: TimeZoneDto;

        // 作業グループ
        workGroup: WorkGroupDto;

        // 備考: 作業入力備考
        remarks: string;

        // 勤務場所: 勤務場所コード
        workLocationCD: string;
    };

    export type TimeZoneDto = {

        // 開始時刻: 時刻(日区分付き)
        start: WorkTimeInformationDto;

        // 終了時刻: 時刻(日区分付き)
        end: WorkTimeInformationDto;

        // 作業時間: 勤怠時間
        workingHours: number | null;

    };

    export type WorkGroupDto = {
        /** 作業CD1 */
        workCD1: string;

        /** 作業CD2 */
        workCD2: string;

        /** 作業CD3 */
        workCD3: string;

        /** 作業CD4 */
        workCD4: string;

        /** 作業CD5 */
        workCD5: string;
    };

    export type WorkTimeInformationDto = {
        // 時刻変更理由
        reasonTimeChange: ReasonTimeChangeDto;

        // 時刻  
        timeWithDay: number | null;
    };

    export type ReasonTimeChangeDto = {
        //時刻変更手段
        timeChangeMeans: number | null;

        //打刻方法
        engravingMethod: number | null;
    };

    export type SelectTargetEmployeeParam = {
        // 社員ID
        employeeId: string;

        // 基準日
        refDate: string;

        //表示期間
        displayPeriod: DatePeriodDto;
    };

    export type ChangeDateDto = SelectTargetEmployeeDto;

    export type SelectTargetEmployeeDto = {

        // List<作業グループ>
        workGroupDtos: WorkGroupDto[];

        // 修正可能開始日付
        workCorrectionStartDate: string;

        // List＜確認者>
        lstComfirmerDto: ConfirmerDto[];

        // List<作業実績詳細>
        lstWorkRecordDetailDto: WorkRecordDetailDto[];
    };

    export type WorkRecordDetailDto = {

        // 年月日
        date: string;

        // 社員ID
        employeeId: string;

        // 作業詳細リスト
        lstWorkDetailsParamDto: WorkDetailsParamDto[];

        // 実績内容
        actualContent: ActualContentDto;

    };

    export type ActualContentDto = {

        // 休憩リスト
        breakTimeSheets: BreakTimeSheetDto[];

        // 休憩時間
        breakHours: number | null;

        // 終了時刻
        end: WorkTimeInformationDto;

        // 総労働時間
        totalWorkingHours: number | null;

        // 開始時刻
        start: WorkTimeInformationDto;
    };

    export type BreakTimeSheetDto = {
        /** 開始: 勤怠打刻 */
        start: number | null;

        /** 終了: 勤怠打刻 */
        end: number | null;

        /** 休憩時間: 勤怠打刻 */
        breakTime: number | null;

        /** 休憩枠NO: 休憩枠NO */
        no: number;
    };

    export type WorkTimeInformationDto = {
        // 時刻変更理由
        reasonTimeChange: ReasonTimeChangeDto;
        // 時刻 
        timeWithDay: number | null;
    };

    export type ReasonTimeChangeDto = {
        //時刻変更手段
        timeChangeMeans: number | null;

        //打刻方法
        engravingMethod: number | null;
    };

    export type ConfirmerDto = {
        /** 社員ID */
        confirmSID: string;

        /** 社員コード */
        confirmSCD: string;

        /** ビジネスネーム */
        businessName: string;

        /** 確認日時 */
        confirmDateTime: string;
    };

    export type IntegrationOfDailyDto = {
        // 社員ID
        employeeId: string;

        // 年月日
        ymd: string;

        // 勤務情報: 日別勤怠の勤務情報
        workInformationDto: WorkInfoOfDailyAttendanceDto;
    };

    export type WorkInfoOfDailyAttendanceDto = {
        // 勤務実績の勤務情報
        recordInfoDto: WorkInformationDto;

        // 計算状態
        calculationState: number;

        // 直行区分
        goStraightAtr: number;

        // 直帰区分
        backStraightAtr: number;

        // 曜日
        dayOfWeek: number;

        // 始業終業時間帯
        scheduleTimeSheets: ScheduleTimeSheetDto;

        // 振休振出として扱う日数
        numberDaySuspension: NumberOfDaySuspensionDto;
    };

    export type WorkInformationDto = {
        // 勤務種類コード
        workType: string;
        // 就業時間帯コード
        workTime: string;
    };

    export type ScheduleTimeSheetDto = {
        workNo: number | null;

        attendance: number;

        leaveWork: number;
    };

    export type NumberOfDaySuspensionDto = {
        //振休振出日数
        days: number | null;

        //振休振出区分
        classifiction: number;
    };

    export type RegisterWorkContentCommand = {
        /** 対象者 */
        employeeId: string;

        /** 編集状態<Enum.日別勤怠の編集状態> */
        editStateSetting: EditStateSetting;

        /** List<年月日,List<作業詳細>> */
        workDetails: WorkDetailCommand[];

        /** 確認モード */
        mode: number;

        /** 変更対象日 */
        changedDate: string;
    };

    export type WorkDetailCommand = {
        /** 年月日 */
        date: string;

        /** List<作業詳細> */
        lstWorkDetailsParamCommand: WorkDetailsParamCommand[];
    };

    export type WorkDetailsParamCommand = {
        // 応援勤務枠No: 応援勤務枠No
        supportFrameNo: number;

        // 備考: 作業入力備考
        remarks: string;

        // 勤務場所: 勤務場所コード
        workLocationCD: string;

        // 時間帯: 時間帯
        timeZone: TimeZoneCommand;

        // 作業グループ
        workGroup: WorkGroupCommand;
    };

    export type TimeZoneCommand = {
        // 開始
        start: number;

        // 終了
        end: number;
    };

    export type WorkGroupCommand = {
        /** 作業CD1 */
        workCD1: string;

        /** 作業CD2 */
        workCD2: string;

        /** 作業CD3 */
        workCD3: string;

        /** 作業CD4 */
        workCD4: string;

        /** 作業CD5 */
        workCD5: string;
    };

    export type AddWorkRecodConfirmationCommand = {
        // 対象者
        employeeId: string;

        // 対象日
        date: string;

        // 確認者
        confirmerId: string;
    };

    export type DeleteWorkResultConfirmationCommand = AddWorkRecodConfirmationCommand;

    export type ChangeDateParam = {

        // 社員ID
        employeeId: string;

        // 基準日
        refDate: string;

        // 表示期間
        displayPeriod: DatePeriodDto;
    };

    export type DatePeriodDto = {
        /** 開始日 */
        start: string;

        /** 終了日 */
        end: string;
    };

    export type AddAttendanceTimeZoneParam = {
        /** 対象者 */
        employeeId: string;

        /** 編集状態<Enum.日別勤怠の編集状態> */
        editStateSetting: EditStateSetting;

        /** List<年月日,List<作業詳細>> */
        workDetails: WorkDetailDto[];
    };

    export type RegisterWorkContentDto = {
        // エラー一覧
        lstErrorMessageInfo: ErrorMessageInfoDto[];
        // List<残業休出時間>
        lstOvertimeLeaveTime: OvertimeLeaveTime[];
    };

    export type ErrorMessageInfoDto = {
        /**
         * 会社ID
         */
        companyID: string;
        /**
         * 社員ID
         */
        employeeID: string;
        /**
         * 処理日
         */
        processDate: string;
        /**
         * 実施内容
         */
        executionContent: number;
        /**
         * リソースID
         */
        resourceID: string;
        /**
         * エラーメッセージ
         */
        messageError: string;
    };

    export type OvertimeLeaveTime = {
        //年月日
        date: string;

        //時間
        time: number;

        //残業休出区分
        overtimeLeaveAtr: OverTimeLeaveAtr;
    };

    enum OverTimeLeaveAtr {
        // 0: 残業申請
        OVER_TIME_APPLICATION = 0,

        //1: 休日出勤申請
        HOLIDAY_WORK_APPLICATION = 1
    };

    export type ErrorMessage = {
        atTime: string;
        businessException: boolean;
        errorMessage: string;
        message: string;
        messageId: string;
        parameterIds: string[];
        responseStatus: string;
    }
}