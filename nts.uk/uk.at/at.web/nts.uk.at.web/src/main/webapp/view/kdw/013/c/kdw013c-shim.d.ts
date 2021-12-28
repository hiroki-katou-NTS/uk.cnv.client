module nts.uk.ui.at.kdw013 {

    export type API = {
        readonly START: string;
        readonly SELECT: string;
        readonly START_F: string;
        readonly ADD_FAV_TASK_F: string;
    };

    export type TASK_FRAME_NO = 1 | 2 | 3 | 4 | 5;

    export type TaskDto = {
        code: string;
        taskFrameNo: TASK_FRAME_NO | null;
        childTaskList: string[];
        expirationStartDate: string;
        expirationEndDate: string;
        displayInfo: TaskDisplayInfoDto;
        cooperationInfo: ExternalCooperationInfoDto;
    };

    export type ExternalCooperationInfoDto = {
        /**
         * 外部コード1
         */
        externalCode1: string;

        /**
         * 外部コード2
         */
        externalCode2: string;

        /**
         * 外部コード3
         */
        externalCode3: string;

        /**
         * 外部コード4
         */
        externalCode4: string;

        /**
         * 外部コード5
         */
        externalCode5: string;
    };

    export type TaskDisplayInfoDto = {
        /**
         * 名称
         */
        taskName: string;

        /**
         * 略名
         */
        taskAbName: string;

        /**
         * 作業色
         */
        color: string;

        /**
         * 備考
         */
        taskNote: string;
    };

    export type StartWorkInputPanelParam = {
        // 社員ID
        employeeId: string;

        // 基準日
        refDate: string;

        // 作業グループ
        workGroupDto: WorkGroupDto;
    }

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

    export type StartWorkInputPanelDto = {
        /** List<作業マスタ情報> */
        frameNoVsTaskFrameNos: FrameNoVsTaskFrameNosDto[];

		/** List<勤務場所> */
        workLocation: a.WorkLocationDto[];

		//List<作業補足情報の選択肢詳細>
		taskSupInfoChoicesDetails: TaskSupInfoChoicesDetailDto[];
		
		//List<工数実績項目>
		manHourRecordItems: ManHourRecordItemDto[];
		
		//List<日次の勤怠項目>
		attendanceItems: DailyAttendanceItemDto[];
	
		//List<工数実績項目と勤怠項目の紐付け>
		manHourRecordAndAttendanceItemLink: ManHourRecordAndAttendanceItemLinkDto[];
		
    };

	export type FrameNoVsTaskFrameNosDto = {
		//応援勤務枠No
		frameNo: number;
        /** 利用可能作業1リスト */
        taskFrameNo1: TaskDto[];

        /** 利用可能作業2リスト */
        taskFrameNo2: TaskDto[];

        /** 利用可能作業3リスト */
        taskFrameNo3: TaskDto[];

        /** 利用可能作業4リスト */
        taskFrameNo4: TaskDto[];

        /** 利用可能作業5リスト */
        taskFrameNo5: TaskDto[];
    };

	export type DailyAttendanceItemDto = {
		/* 勤怠項目ID */
		 attendanceItemId: number;
	
		/* 勤怠項目名称 */
		 attendanceName: string;
	
		/* 表示番号 */
		 displayNumber: number;
	
		/* ユーザーが値を変更できる */
		 userCanUpdateAtr: number;
	
		/* 勤怠項目属性 */
		 dailyAttendanceAtr: number;
	
		/* 名称の改行位置*/
		 nameLineFeedPosition: number;
		
		/* マスタの種類 */
		masterType: number;

		/* 怠項目のPrimitiveValue */
		primitiveValue: number;

		/* 表示名称 */
		displayName: string;
	}
	
	export type ManHourRecordAndAttendanceItemLinkDto = {
		/** 応援勤務枠No*/
		frameNo: number;
		
		/** 工数実績項目ID*/
		itemId: number;
		
		/** 勤怠項目ID*/
		attendanceItemId: number;
	}


	export type ManHourRecordItemDto = {
		/** 項目ID*/
        itemId: number;
		/** 名称*/
        name: string;
		/** フォーマット設定に表示する*/
        useAtr: number;
    };

	export type TaskSupInfoChoicesDetailDto = {
		/** 履歴ID */
		historyId: string;
		/** 項目ID */
		 itemId;
		/** コード */
		 code: string;
		/** 名称 */
		 name: string;
		/** 外部コード */
		 externalCode: string;
    };

    export type EventModel = {
        timeRange: KnockoutObservable<ITimeSpanForCalc>;
        task1: KnockoutObservable<string>;
        task2: KnockoutObservable<string>;
        task3: KnockoutObservable<string>;
        task4: KnockoutObservable<string>;
        task5: KnockoutObservable<string>;
    };

    export type ConfirmContent = {
        messageId: string;
        messageParams?: string[];
    };

    export type SelectWorkItemParam = {
        //社員ID
        employeeId: string;

        //基準日
        refDate: string;

        //作業枠NO
        taskFrameNo: number;

        //上位枠作業コード
        taskCode: string;
    };
}