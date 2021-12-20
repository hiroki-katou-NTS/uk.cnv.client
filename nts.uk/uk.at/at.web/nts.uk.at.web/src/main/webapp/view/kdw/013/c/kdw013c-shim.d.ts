module nts.uk.ui.at.kdw013.c {

    export type API = {
        readonly START: string;
        readonly SELECT: string;
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
        sId: string;

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
        /** 利用可能作業1リスト */
        taskListDto1: TaskDto[];

        /** 利用可能作業2リスト */
        taskListDto2: TaskDto[];

        /** 利用可能作業3リスト */
        taskListDto3: TaskDto[];

        /** 利用可能作業4リスト */
        taskListDto4: TaskDto[];

        /** 利用可能作業5リスト */
        taskListDto5: TaskDto[];
    };


    export type EventModel = {
        timeRange: KnockoutObservable<share.TimeRange>;

        task1: KnockoutObservable<string>;
        task2: KnockoutObservable<string>;
        task3: KnockoutObservable<string>;
        task4: KnockoutObservable<string>;
        task5: KnockoutObservable<string>;

        workplace: KnockoutObservable<string>;
        descriptions: KnockoutObservable<string>;
    };

    export type ConfirmContent = {
        messageId: string;
        messageParams?: string[];
    };

    export type SelectWorkItemParam = {
        //社員ID
        sId: string;

        //基準日
        refDate: string;

        //作業枠NO
        taskFrameNo: number;

        //上位枠作業コード
        taskCode: string;
    };
}