module nts.uk.at.kmk004.components.flex {


	export interface ISidebarButtonParam {
		isShowCopyButton: boolean;
	}

	//フレックス勤務所定労働時間取得
	export interface IGetFlexPredWorkTime {
		//参照先
		reference: number;
	}

	//会社別フレックス勤務集計方法
	export interface IComFlexMonthActCalSet {
		//不足設定
		insufficSet: IShortageFlexSetting;
		//集計方法
		aggrMethod: number;
		//フレックス時間の扱い
		flexTimeHandle: IFlexTimeHandle;
		//法定内集計設定
		legalAggrSet: IAggregateTimeSetting;

	}

	//法定内集計設定
	export interface IAggregateTimeSetting {
		//集計設定 
		aggregateSet: number;
	}

	//フレックス時間の扱い
	export interface IFlexTimeHandle {
		//残業時間をフレックス時間に含める 
		includeOverTime: boolean;
		//法定外休出時間をフレックス時間に含める
		includeIllegalHdwk: boolean;
	}

	//不足設定
	export interface IShortageFlexSetting {
		// 清算期間
		settlePeriod: number;
		//開始月
		startMonth: number;
		//期間 
		period: number;
		//繰越設定
		carryforwardSet: number;
	}

	export class FlexScreenData {
		//会社別フレックス勤務集計方法
		comFlexMonthActCalSet: KnockoutObservable<IComFlexMonthActCalSet> = ko.observable(
			{
				insufficSet: {
					settlePeriod: 0,
					startMonth: 0,
					period: 0,
					carryforwardSet: 0
				},
				aggrMethod: 1,
				flexTimeHandle: {
					includeOverTime: true,
					includeIllegalHdwk: true
				},
				legalAggrSet: { aggregateSet: 0 }
			}
		);
		//フレックス勤務所定労働時間取得
		getFlexPredWorkTime: KnockoutObservable<IGetFlexPredWorkTime> = ko.observable({ reference: 0 });
		yearList: KnockoutObservableArray<YearItem> = ko.observableArray([new YearItem(2020) ,new YearItem(2019)]);
		selectedYear: KnockoutObservable<YearItem> = ko.observable(this.yearList()[0]);
		monthlyWorkTimeSetComs: KnockoutObservableArray<MonthlyWorkTimeSetCom> = ko.observableArray([
			new MonthlyWorkTimeSetCom({
				month: 1,
				laborTime: {
					withinLaborTime: 1,
					legalLaborTime: 2,
					weekAvgTime: 3
				}
			}),
			new MonthlyWorkTimeSetCom({
				month: 2,
				laborTime: {
					withinLaborTime: 2,
					legalLaborTime: 3,
					weekAvgTime: 4
				}
			})
		]);

		constructor(param?: IScreenData) {
			let self = this;
			if (param) {
				this.yearList(_.chain(param.yearList).map((item) => { return new YearItem(item); }).orderBy(['year'], ['desc']).value());
				this.monthlyWorkTimeSetComs(_.map(param.monthlyWorkTimeSetComs, (item) => { return new MonthlyWorkTimeSetCom(item); }));
				this.comFlexMonthActCalSet(param.comFlexMonthActCalSet);
				this.getFlexPredWorkTime(param.getFlexPredWorkTime);
			}
			self.selectedYear.subscribe((value) => {
				console.log(value);
			});

		}

		updateData(param: IScreenData) {
			this.yearList(_.chain(param.yearList).map((item) => { return new YearItem(item); }).orderBy(['year'], ['desc']).value());
			this.monthlyWorkTimeSetComs(_.map(param.monthlyWorkTimeSetComs, (item) => { return new MonthlyWorkTimeSetCom(item); }));
			this.comFlexMonthActCalSet(param.comFlexMonthActCalSet);
			this.getFlexPredWorkTime(param.getFlexPredWorkTime);
		}
	}

	export class YearItem {
		year: number;
		yearName: string;
		constructor(year: number) {
			this.year = year;
			this.yearName = year.toString() + '年度';
		}
	}



	export class MonthlyWorkTimeSetCom {
		month: number;
		laborTime: KnockoutObservable<MonthlyLaborTime>;

		constructor(param: IMonthlyWorkTimeSetCom) {
			this.month = param.month;
			this.laborTime = ko.observable(new MonthlyLaborTime(param.laborTime));
		}
	}

	export class MonthlyLaborTime {
		//所定労働時間
		withinLaborTime: KnockoutObservable<number> = ko.observable();
		//法定労働時間
		legalLaborTime: KnockoutObservable<number> = ko.observable();
		//週平均時間
		weekAvgTime: KnockoutObservable<number> = ko.observable();

		checkbox: KnockoutObservable<boolean> = ko.observable(true);

		constructor(param: IMonthlyLaborTime) {
			this.withinLaborTime(param.withinLaborTime);
			this.legalLaborTime(param.legalLaborTime);
			this.weekAvgTime(param.weekAvgTime);
		}
	}



	export interface IScreenData {
		//会社別フレックス勤務集計方法
		comFlexMonthActCalSet: IComFlexMonthActCalSet
		//フレックス勤務所定労働時間取得
		getFlexPredWorkTime: IGetFlexPredWorkTime;

		isShowCheckbox: boolean;
		//年度リスト
		yearList: Array<number>;
		//会社別月単位労働時間
		monthlyWorkTimeSetComs: Array<IMonthlyWorkTimeSetCom>;
	}
	//会社別月単位労働時間 
	export interface IMonthlyWorkTimeSetCom {
		//年月．月
		month: number;
		//労働時間
		laborTime: IMonthlyLaborTime;
	}

	//月労働時間
	export interface IMonthlyLaborTime {
		//所定労働時間
		withinLaborTime: number;
		//法定労働時間
		legalLaborTime: number;
		//週平均時間
		weekAvgTime: number;
	}
}