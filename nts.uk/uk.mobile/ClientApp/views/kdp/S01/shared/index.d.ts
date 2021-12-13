
export module model {
    export interface ISettingSmartPhone {
        //スマホ打刻の打刻設定
        setting: ISettingsSmartphoneStamp;

        //打刻後の実績表示
        resulDisplay: IStampResultDisplay;

        //抑制する打刻
        stampToSuppress: IStampToSuppress;
    }

    export interface ISettingsSmartphoneStamp {
        // 会社ID
        cid: string;

        // 打刻画面の表示設定
        displaySettingsStampScreen: IDisplaySettingsStampScreenDto;

        // ページレイアウト設定
        pageLayoutSettings: Array<IStampPageLayoutDto>;

        // 打刻ボタンを抑制する
        buttonEmphasisArt: boolean;

        // 位置情報を利用する
	    locationInfoUse: boolean;

    }

    export interface IStampPageLayoutDto {
        /** ページNO */
        pageNo: number;

        /** ページ名 */
        stampPageName: string;

        /** ページコメント */
        stampPageComment: IStampPageCommentDto;

        /** ボタン配置タイプ */
        buttonLayoutType: number;

        /** ボタン詳細設定リスト */
        lstButtonSet: Array<ButtonSettingsDto>;
    }

    export interface IStampPageCommentDto {
        /** コメント */
        pageComment: string;

        /** コメント色 */
        commentColor: string;
    }

    export interface ButtonSettingsDto {
        /** ボタン位置NO */
        buttonPositionNo: number;

        /** ボタンの表示設定 */
        buttonDisSet: IButtonDisSetDto; 

        buttonValueType: number;

        usrArt: number;

        buttonType: IButtonTypeDto;
        
        icon: string;

        taskChoiceArt: number;
    }

    export interface IButtonTypeDto {
        /** 予約区分 */
	    reservationArt: number;
        
        /** 打刻種類 */
        stampType: IStampTypeDto;
    }

    export interface IStampTypeDto {
        /** 勤務種類を半休に変更する */
        changeHalfDay: Boolean;

        /** 外出区分 */
        goOutArt: number;

        /** 所定時刻セット区分 */
        setPreClockArt: number;

        /** 時刻変更区分 */
        changeClockArt: number;

        /** 計算区分変更対象 */
        changeCalArt: number;
    }

    export interface IButtonDisSetDto {
        /** ボタン名称設定 */
        buttonNameSet: IButtonNameSetDto;

        /** 背景色 */
        backGroundColor: string;

        displayBackGroundColor: string;
    }

    export interface IButtonNameSetDto {
        /** 文字色 */
        textColor: string;

        /** ボタン名称 */
        buttonName: string;
    }

    export interface IDisplaySettingsStampScreenDto {
        /** 打刻画面のサーバー時刻補正間隔 */
        serverCorrectionInterval: number;

        /** 打刻画面の日時の色設定 */
        settingDateTimeColor: ISettingDateTimeColorOfStampScreenDto;

        /** 打刻結果自動閉じる時間 */
        resultDisplayTime: number;

    }

    export interface ISettingDateTimeColorOfStampScreenDto {
        /** 文字色 */
        textColor: String;

        /** 背景色 */
        backgroundColor: String;

    }



    export interface IStampResultDisplay {
        /** 会社ID */
        companyId: string;

        /** 使用区分 */
        usrAtrValue: number;

        /** 表示項目一覧 */
        lstDisplayItemId: Array<IStampAttenDisplay>;

    }

    export interface IStampAttenDisplay {
        /** 会社ID */
        companyId: string;

        /** 表示項目一覧 */
        displayItemId: number;
    }

    export interface IStampToSuppress {
        // 出勤
        goingToWork: boolean;

        // 退勤
        departure: boolean;

        // 外出 
        goOut: boolean;

        // 戻り
        turnBack: boolean;
    }

    export interface ICheckStampResult {

        // 打刻カード番号
        cardNumber: string;
        // 打刻利用可否
        used: number;
    }
    export interface IRegisterSmartPhoneStampCommand {
        /**
         * 打刻日時
         */
        stampDatetime: string;
        /**
         * 打刻ボタン(ページNO,ボタンNO)
         */
        stampButton: IStampButtonCommand;
        /**
         * 地理座標
         */
        geoCoordinate: IGeoCoordinate;

        /**
         * 実績への反映内容
         */

        refActualResult: IRefectActualResultCommand;

    }
    export interface IStampButtonCommand {
        /** ページNO */
        pageNo: number;

        /** ボタン位置NO */
        buttonPositionNo: number;

        stampMeans: number;
    }
    interface IRefectActualResultCommand {


        cardNumberSupport: string;

        /**
         * 打刻場所コード 勤務場所コード old
         */

        workLocationCD: string;

        /**
         * 就業時間帯コード
         */

        workTimeCode: string;

        /**
         * 時間外の申告
         */

        overtimeDeclaration: IOvertimeDeclarationComamnd;

        /**
         * 作業グループ
         */
         workGroup: IWorkGroup; 
    }

    interface IOvertimeDeclarationComamnd {
        overTime: number;

        /**
         * 時間外深夜時間
         */
        overLateNightTime: number;
    }

    interface IGeoCoordinate {
        /** 緯度 */
        latitude: number;

        /** 経度 */
        longitude: number;

    }

    export interface IScreenCParam {
        attendanceItemIds: Array<number>;
    }

    export interface IScreenBParam {
        resultDisplayTime: number;
        employeeId: string;
        employeeCode: string;

    }

    export interface IScreenBResult {
        empDatas: Array<IEmployeeStampInfo>;
        workLocationName: string;
        workLocationCd: string;
        empInfo: IEmployeeRecordImport;
        workplaceName: string;
    }

    interface IEmployeeRecordImport {
        pname: string;
        employeeCode: string;
    }

    export interface IEmployeeStampInfo {
        /**
             * 社員ID
             */
        employeeId: string;

        /**
         * 年月日
         */
        date: Date;

        /**
         * 打刻情報リスト
         */
        listStampInfoDisp: Array<IStampInfoDisp>;
    }

    export interface IStampInfoDisp {
        /**
	 * 打刻カード番号
	 */
        stampNumber: string;

        /**
         * 打刻日時
         */
        stampDatetime: Date;

        /**
         * 打刻区分
         */
        stampAtr: string;

        /**
         * 打刻
         */
        stamp: Array<IStamp>;
    }
    interface IStamp {
        /**
	 * 契約コード
	 * ver2　属性追加
	 */
        contractCode: string;

        /**
         * 打刻カード番号 カード番号(old)
         */
        cardNumber: string;

        /**
         * 打刻日時 年月日 (old) + 勤怠時刻 (old)
         * 
         */
        stampDateTime: Date;

        /**
         * 打刻場所情報
         */
        locationInfor: IGeoCoordinate;
    }

    interface IDisplayConfirmStampResultScreenCDto {
        /**
             * ドメインモデル：社員の打刻情報
             */
        stampings: Array<any>;

        /**
         * 日の実績の確認状況
         */
        confirmResult: IConfirmStatusActualResultDto;
        /**
         * 表示可能項目＜勤怠項目ID、名称、属性、PrimitiveValue＞
         */
        lstItemDisplayed: Array<any>;

        /**
         * 実績値＜勤怠項目ID、値、年月日＞
         */
        itemValues: Array<any>;
        /**
         * 勤務種類名
         */
        workTypeName: string;

        /**
         * 就業時間帯名 ←就業時間帯の設定.表示名
         */
        workTimeName: string;

        empInfo: IEmployeeRecordImport;

        workplaceName: string;

    }

    interface IConfirmStatusActualResultDto {

        /**
         * 対象社員
         */
        employeeId: string;

        /**
         * 対象年月日
         */
        date: Date;

        /**
         * 確認状態 or 承認状態
         */
        status: boolean;

        /**
         * 実施可否
         */
        permissionCheck: number;

        /**
         * 解除可否
         */
        permissionRelease: number;
    }

    export interface IRegisterVerifiDailyResultCommand {
        /**
         * 社員ID
         */
        employeeId: string;

        /**
         * 本人確認内容
         */
        confirmDetails: Array<IConfirmDetailCommand>;
    }

    interface IConfirmDetailCommand {
        /**
         * 年月日
         */
        ymd: Date;

        /**
         * 本人確認状況
         */
        identityVerificationStatus: Boolean;
    }

    export interface IStampButtonCommand {
        /** ページNO */
        pageNo: number;

        /** ボタン位置NO */
        buttonPositionNo: number;
    }

    export interface IGetOmissionContentDto {
        errorInfo: any;
        appDispNames: Array<any>;
    }

    // 作業グループ
    interface IWorkGroup {
        workCode1: string; // 作業グループ.作業CD1
        workCode2: string; // 作業グループ.作業CD2
        workCode3: string; // 作業グループ.作業CD3
        workCode4: string; // 作業グループ.作業CD4
        workCode5: string; // 作業グループ.作業CD5
    }
}