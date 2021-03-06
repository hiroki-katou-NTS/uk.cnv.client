/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.kdp003.s {
	interface Params {
		employeeId: string;
	}

	const API = {
		GET_STAMP_MANAGEMENT: '/at/record/stamp/management/personal/stamp/getStampData'
	};

	@bean()
	export class ViewModel extends ko.ViewModel {
		filter: Filter = {
			day: ko.observable(parseInt(moment().format('YYYYMM'))),
			engraving: ko.observable('1')
		};

		dataSources: DataSource = {
			all: ko.observableArray([])
		};

		constructor(private params: Params) {
			super();

			if (!params) {
				this.params = { employeeId: '' };
			}
		}

		created() {
			const vm = this;
			const { randomId } = nts.uk.util;

			vm.dataSources.filtereds = ko.computed({
				read: () => {
					const filtereds: StampDisp[] = [];
					const allStamps: StampData[] = ko.unwrap(vm.dataSources.all);
					const engraving: ENGRAVING = ko.unwrap(vm.filter.engraving);

					_.chain(allStamps)
						.orderBy(['stampDate', 'stampTime'], ['asc', 'asc'])
						.each((item: StampData) => {
							const d = moment(item.stampDate, 'YYYY/MM/DD');
							const day = d.clone().locale('en').format('dddd');
							
							let value = item.buttonValueType;

							const pushable = {
								id: randomId(),
								time: `${item.stampHow} ${item.stampTime}`,
								date: `<div class="color-schedule-${day.toLowerCase()}">${d.format('YYYY/MM/DD(dd)')}</div>`,
								name: `<div style="text-align: 
								${(ButtonType.GOING_TO_WORK == value || ButtonType.RESERVATION_SYSTEM == value) ? 'left' :
								ButtonType.WORKING_OUT == value ? 'right' 
								: 'center'};">${item.stampArt}</div>`
							};

							// S1 bussiness logic
							switch (engraving) {
								default:
								case '1':
									filtereds.push(pushable);
									break;
								case '2':
									if ([ChangeClockArt.GOING_TO_WORK, ChangeClockArt.WORKING_OUT].indexOf(item.changeClockArt) > -1) {
										filtereds.push(pushable);
									}
									break;
								case '3':
									if ([ChangeClockArt.GO_OUT, ChangeClockArt.RETURN].indexOf(item.changeClockArt) > -1) {
										filtereds.push(pushable);
									}
									break;
								case '4':
									if ([ChangeClockArt.FIX, ChangeClockArt.END_OF_SUPPORT, ChangeClockArt.SUPPORT, ChangeClockArt.TEMPORARY_SUPPORT_WORK].indexOf(item.changeClockArt) > -1) {
										filtereds.push(pushable);
									}
									break;
							}
						})
						.value();

					return filtereds;
				},
				owner: vm
			})

			vm.filter.day
				.subscribe((value: number) => {
					const fm = 'YYYY/MM/DD';
					const { employeeId } = vm.params;
					const baseDate = moment(`${value}`, 'YYYYMM');
					const endDate = baseDate.endOf('month').format(fm);
					const startDate = baseDate.startOf("month").format(fm);

					vm.$ajax(API.GET_STAMP_MANAGEMENT, { employeeId, endDate, startDate })
						.then((data: StampData[]) => {
							if (ko.toJS(vm.filter.day) === value) {
								vm.dataSources.all(data);
							}
						});
				});

			vm.filter.day.valueHasMutated();
		}
		
		mounted() {
			$('.nts-datepicker-wrapper').first().find('input').focus();
		}

		closeDialog() {
			const vm = this;

			vm.$window.close();
		}
	}

	export type ENGRAVING = '1' | '2' | '3' | '4';
	
	export enum ButtonType {
		// ???

		GOING_TO_WORK = 1,
		// ???

		WORKING_OUT = 2,
		// "?????????"

		GO_OUT = 3,
		// ?????????

		RETURN = 4,
		// ?????????

		RESERVATION_SYSTEM = 5
	}

	export enum ChangeClockArt {
		/** 0. ?????? */
		GOING_TO_WORK = 0,

		/** 1. ?????? */
		WORKING_OUT = 1,

		/** 2. ?????? */
		OVER_TIME = 2,

		/** 3. ?????? */
		BRARK = 3,

		/** 4. ?????? */
		GO_OUT = 4,

		/** 5. ?????? */
		RETURN = 5,

		/** 6. ???????????? */
		FIX = 6,

		/** 7. ???????????? */
		TEMPORARY_WORK = 7,

		/** 8. ???????????? */
		END_OF_SUPPORT = 8,

		/** 9. ???????????? */
		TEMPORARY_LEAVING = 9,

		/** 10. PC???????????? */
		PC_LOG_ON = 10,

		/** 11. PC???????????? */
		PC_LOG_OFF = 11,

		/** 12. ???????????? */
		SUPPORT = 12,

		/** 13. ??????+???????????? */
		TEMPORARY_SUPPORT_WORK = 13
	}

	export enum ChangeCalArt {
		/** N: ?????? */
		NONE = 0,

		/** N: ?????? */
		EARLY_APPEARANCE = 1,

		/** N: ?????? */
		OVER_TIME = 2,

		/** N: ?????? */
		BRARK = 3,

		/** N: ??????????????? */
		FIX = 4
	}

	export enum ContentsStampType {
		/** 1: ?????? */
		WORK = 1,

		/** 2: ??????????????? */
		WORK_STRAIGHT = 2,

		/** 3: ??????????????? */
		WORK_EARLY = 3,

		/** 4: ??????????????? */
		WORK_BREAK = 4,

		/** 5: ?????? */
		DEPARTURE = 5,

		/** 6: ??????????????? */
		DEPARTURE_BOUNCE = 6,

		/** 7: ??????????????? */
		DEPARTURE_OVERTIME = 7,

		/** 8: ?????? */
		OUT = 8,

		/** 9: ?????? */
		RETURN = 9,

		/** 10: ?????? */
		GETTING_STARTED = 10,

		/** 11: ?????? */
		DEPAR = 11,

		/** 12: ???????????? */
		TEMPORARY_WORK = 12,

		/** 13: ???????????? */
		TEMPORARY_LEAVING = 13,

		/** 14: ???????????? */
		START_SUPPORT = 14,

		/** 15: ???????????? */
		END_SUPPORT = 15,

		/** 16: ??????????????? */
		WORK_SUPPORT = 16,

		/** 17: ????????????????????? */
		START_SUPPORT_EARLY_APPEARANCE = 17,

		/** 18: ????????????????????? */
		START_SUPPORT_BREAK = 18,

		/** 19: ?????? */
		RESERVATION = 19,

		/** 20: ????????????  */
		CANCEL_RESERVATION = 20
	}

	export interface Filter {
		day: KnockoutObservable<number>;
		engraving: KnockoutObservable<ENGRAVING>;
	}

	export interface DataSource {
		all: KnockoutObservableArray<StampData>;
		filtereds?: KnockoutComputed<StampDisp[]>;
	}

	export interface StampData {
		attendanceTime: string;
		authcMethod: number;
		cardNumberSupport: string;
		changeCalArt: ChangeCalArt;
		changeClockArt: ChangeClockArt;
		changeClockArtName: string;
		changeHalfDay: boolean;
		corectTtimeStampType: string;
		correctTimeStampValue: ContentsStampType;
		buttonValueType:number;
		empInfoTerCode: string;
		goOutArt: string;
		latitude: number;
		locationInfor: string;
		longitude: number;
		outsideAreaAtr: string;
		overLateNightTime: string;
		overTime: string;
		reflectedCategory: boolean;
		revervationAtr: number;
		setPreClockArt: number;
		stampArt: string;
		stampArtName: string;
		stampDate: string;
		stampHow: string;
		stampMeans: number;
		stampNumber: string;
		stampTime: string;
		stampTimeWithSec: string;
		timeStampType: string;
		workLocationCD: string;
		workTimeCode: string;
	}

	export interface StampDisp {
		id: string;
		time: string;
		date: string;
		name: string;
	}
}
