module nts.uk.ui.at.kdw013 {
	export type DisplayManHrRecordItem 			= { itemId: number; order: any; }
	export type ITaskItemValue 					= { itemId: number; value: any; }
	export type IManHrTaskDetail 				= { supNo: number; taskItemValues: ITaskItemValue[]; }
	export type ITimeSpanForCalc 				= { start: Date; end: Date; }
	export type IManHrPerformanceTaskBlock 		= { caltimeSpan: ITimeSpanForCalc; taskDetails: IManHrTaskDetail[]; }
	export type IDailyActualManHoursActualWork 	= { date: string; taskBlocks: IManHrPerformanceTaskBlock[]; }
    
    export type RegisterWorkContentCommand = {
        /** 対象者 */
        employeeId: string;
        // 工数実績変換結果
        manHrlst: Array<ManHrRecordConvertResultCommand>;

        // 日別勤怠(Work)
        integrationOfDailys: Array<IntegrationOfDailyCommand>;

        /** 編集状態<Enum.日別勤怠の編集状態> */
        editStateSetting: number;

        /** List<年月日,List<作業詳細>> */
        workDetails: Arrays<WorkDetailCommand>;

        /** 確認モード */
        mode: number;

        /** 変更対象日 */
        changedDates: Array<Date>;
    }
    
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
    }
    
    export type TimeZoneCommand = {
        // 開始
        start: number;

        // 終了
        end: number;
    }
    
    export type WorkDetailsParamCommand = {
        // 応援勤務枠No: 応援勤務枠No
        supportFrameNo: number;

        // 時間帯: 時間帯
        timeZone: TimeZoneCommand;
        // 作業グループ
        workGroup: WorkGroupCommand;

        // 備考: 作業入力備考
        remarks: String;

        // 勤務場所: 勤務場所コード
        workLocationCD: String;
    }
    
    export type WorkDetailCommand = {
        /** 年月日 */
        date: Date;

        /** List<作業詳細> */
        lstWorkDetailsParamCommand: Array<WorkDetailsParamCommand>;
    }
    
    export type WorkInformationCommand = {
        // 勤務種類コード
        workType: String;
        // 就業時間帯コード
        workTime: String;
    }
    
    export type ScheduleTimeSheetCommand = {
        workNo: number;

        attendance: number;

        leaveWork: number;
    }
    
    export type NumberOfDaySuspensionCommand = {

        // 振休振出日数
        days: number;

        // 振休振出区分
        classifiction: number;
    }

    export type WorkInfoOfDailyAttendanceCommand = {
        // 勤務実績の勤務情報
        recordInfoDto: WorkInformationCommand;

        // 計算状態
        calculationState: number;

        // 直行区分
        goStraightAtr: number;

        // 直帰区分
        backStraightAtr: number;

        // 曜日
        dayOfWeek: number;

        // 始業終業時間帯
        scheduleTimeSheets: Array<ScheduleTimeSheetCommand>;

        // 振休振出として扱う日数
        numberDaySuspension: NumberOfDaySuspensionCommand;
    }
    
    export type IntegrationOfDailyCommand = {
        // 社員ID
        employeeId: string;

        // 年月日
        ymd: Date;

        // 勤務情報: 日別勤怠の勤務情報
        workInformationDto: WorkInfoOfDailyAttendanceCommand;
    }
    
    export type ManHrRecordConvertResultCommand = {
        /** 年月日*/
        ymd: Date;

        /** 作業リスト*/
        taskList: Array<ManHrTaskDetailCommand>;

        /** 実績内容 */
        manHrContents: Array<ItemValueCommand>;
    }
    
    export type ManHrTaskDetailCommand = {
        /** 作業項目値*/
        taskItemValues: Array<TaskItemValueCommand>;

        /** 応援勤務枠No*/
        supNo: number;
    }
    
    export type ItemValueCommand = {
        value: string;

        valueType: number;

        layoutCode: string;

        itemId: number;
        pathLink: string;
        isFixed: boolean;
    }
    
    export type TaskItemValueCommand = {
        /** 工数実績項目ID*/
        itemId: number;

        /** 値*/
        value: string;
    }

	export type TaskBlockDetailContentDto = {
        // 開始時刻
        startTime: number;
        // 終了時刻
        endTime: number;
        //作業詳細
        taskContents: TaskContentForEachSupportFrameDto[];
    }

	export type TaskContentDto = {
        // 項目ID: 工数実績項目ID
        itemId: number;

        // 作業コード
        taskCode: string;
    };

    export type FavoriteTaskItemDto = {
        // 社員ID
        employeeId: string;
        // お気に入りID
        favoriteId: string;
        // お気に入り作業名称
        taskName: string;
        // お気に入り内容
        favoriteContents: TaskContentDto[]; 
    }

    export type StartTaskFavoriteRegisterParam = {
        // お気に入りID
        favId: string | null;
    }

    export type RegisterFavoriteCommand = {
        taskName: string;
	    contents: TaskContentDto[];
    }

    export type UpdateFavNameCommand = {
        favId: string;
        favName: string;
    }
    export type TaskContentForEachSupportFrameDto = {
        // 応援勤務枠No
        frameNo: number;
        // 作業内容
        taskContent: TaskContentDto[];
		// 作業時間
		attendanceTime: number;
    }

    export type OneDayFavoriteSetDto = {
        // 社員ID
        sId: string;
        // お気に入りID
        favId: string;
        // お気に入り作業名称
        taskName: string;
        // お気に入り内容
        taskBlockDetailContents: TaskBlockDetailContentDto[];
    }
	export type RegisterFavoriteForOneDayCommand = {
        employeeId: string;
        taskName: string;
        contents: TaskBlockDetailContentDto[];
    }

    export type TaskInfo = {
        // 作業枠NO 
        workNo: number;
        // 作業名
        name: string;
        // 作業時間
        time: number;
    }
    
    // 日別勤怠の応援作業時間
    export type OuenWorkTimeOfDailyAttendance = {
        // 応援勤務枠No
        workNo: number;
        // 勤務時間
        workTime: OuenAttendanceTimeEachTimeSheet ;
    }

    export type OuenAttendanceTimeEachTimeSheet = {
        // 総労働時間: 勤怠時間
        totalTime: number;
    }

    // 日別勤怠の応援作業時間帯
    export type OuenWorkTimeSheetOfDailyAttendance = {
        // 応援勤務枠No
        workNo: number;
        // 作業内容
        workContent: WorkContent;
		//時間帯
		timeSheet: TimeSheetOfAttendanceEachOuenSheet;
    }

	export type TimeSheetOfAttendanceEachOuenSheet = {
		// 応援勤務枠No
        workNo: number;
		// 開始
		start: WorkTimeInformationCommand;
		// 終了
		end: WorkTimeInformationCommand;
	}

	export type WorkTimeInformationCommand = {
		// 時刻
		timeWithDay: number;
	}

    export type WorkContent = {
        // 作業
        work: WorkGroupDto;
    }
	
	//日別実績の工数実績作業
	export class DailyActualManHoursActualWork {
		date: KnockoutObservable<string>;
		taskBlocks: KnockoutObservableArray<ManHrPerformanceTaskBlock>;

		constructor(dailyActualManHoursActualWork: IDailyActualManHoursActualWork) {
			this.date = ko.observable(dailyActualManHoursActualWork.date);
			this.taskBlocks = ko.observableArray(_.map(dailyActualManHoursActualWork.taskBlocks, (t: IManHrPerformanceTaskBlock) => new ManHrPerformanceTaskBlock(t)));
		}

		update(dailyActualManHoursActualWork: IDailyActualManHoursActualWork) {
			this.date(dailyActualManHoursActualWork.date);
			this.taskBlocks(_.map(dailyActualManHoursActualWork.taskBlocks, (t: IManHrPerformanceTaskBlock) => new ManHrPerformanceTaskBlock(t)));
		}
	}

	// 工数実績作業ブロック
	export class ManHrPerformanceTaskBlock {
		caltimeSpan: TimeSpanForCalc;
		taskDetails: KnockoutObservableArray<ManHrTaskDetail>;
		constructor(taskBlocks: IManHrPerformanceTaskBlock) {
			this.caltimeSpan = new TimeSpanForCalc(taskBlocks.caltimeSpan);
			this.taskDetails = ko.observableArray(_.map(taskBlocks.taskDetails, (t: IManHrTaskDetail) => new ManHrTaskDetail(t)));
		}
	}

	// 計算用時間帯
	export class TimeSpanForCalc {
		start: Date;
		end: Date;
		workingHours: null
		constructor(caltimeSpan: ITimeSpanForCalc) {
			this.start = caltimeSpan.start;
			this.end = caltimeSpan.end;
		}
	}


	//工数実績作業詳細
	export class ManHrTaskDetail {
		supNo: number;
		taskItemValues: KnockoutObservableArray<TaskItemValue>;
		constructor(manHrTaskDetail: IManHrTaskDetail, data?: StartWorkInputPanelDto, displayManHrRecordItem?: DisplayManHrRecordItem[]) {
			let vm = this;
			vm.supNo = manHrTaskDetail.supNo;
			if(!vm.supNo){
				vm.supNo = 1;
			}
			_.forEach([1,2,3,4,5,6,7,8], (i:number) => {
				let item = _.find(manHrTaskDetail.taskItemValues, e => e.itemId == i);
				if(!item){
					manHrTaskDetail.taskItemValues.push({itemId: i, value: null});
				}
			});
			
			//sap xep
			if (displayManHrRecordItem && data) {
				// sap xep item co dinh
				let taskItemValues: TaskItemValue[] = 
				_.map(_.sortBy(_.filter(manHrTaskDetail.taskItemValues, (i: ITaskItemValue) => { 
							return i.itemId < 9 
						}),
					['itemId']), 
				(t: ITaskItemValue) => new TaskItemValue(t));
				
				// sap xep thu tu item tuy y
				_.forEach(_.sortBy(displayManHrRecordItem, ['order']), (itemOrder: DisplayManHrRecordItem) => {
					let itemLink = _.find(data.manHourRecordAndAttendanceItemLink, link => link.frameNo == vm.supNo && link.itemId == itemOrder.itemId);
					let attendanceItem = _.find(data.attendanceItems, i => i.attendanceItemId == itemLink.attendanceItemId);
					let item: ITaskItemValue = _.find(manHrTaskDetail.taskItemValues, (i: ITaskItemValue) => i.itemId == itemOrder.itemId);
					if(item){
						let t: TaskItemValue = new TaskItemValue(item, '', attendanceItem.dailyAttendanceAtr);
						taskItemValues.push(t);
					}
				});
				vm.taskItemValues = ko.observableArray(taskItemValues);
			} else {
				vm.taskItemValues = ko.observableArray(
					_.map(manHrTaskDetail.taskItemValues, (t: ITaskItemValue) => new TaskItemValue(t)));
			}
		}
	}

	//作業項目値
	export class TaskItemValue {
		itemId: number;
		lable: KnockoutObservable<string> = ko.observable('');
		use: KnockoutObservable<boolean> = ko.observable(true);
		type: number;
		value: KnockoutObservable<string>;
		//options only use C screen
		options: KnockoutObservableArray<c.DropdownItem | any> = ko.observableArray([]);
		primitiveValue: string;
		constructor(taskItemValues: ITaskItemValue, name?: string, type?: number) {
			this.itemId = taskItemValues.itemId;
			this.value = ko.observable(taskItemValues.value);
			this.lable(name);
			this.type = type;
		}
	}
}