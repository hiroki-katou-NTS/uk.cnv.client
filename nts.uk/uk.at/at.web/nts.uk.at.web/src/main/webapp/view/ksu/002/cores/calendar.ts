/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.calendar {
	type BindingKey = 'date' | 'daisy' | 'holiday' | 'dataInfo';

	export type CLICK_CELL = 'event' | 'holiday' | 'info';

	export const KSU_USER_DATA = 'KSU002.USER_DATA';

	export interface StorageData {
		fdate: number;
		wtypec: string;
		wtimec: string;
	}

	export interface DayData<T = DataInfo> {
		date: Date;
		inRange: boolean;
		startDate: boolean;
		data: T;
		binding?: string | Record<BindingKey, string | null>;
		className: KnockoutObservableArray<string>;
	}

	export interface DataInfo<T = KnockoutObservable<string | null>> {
		event: T;
		holiday: T;
		specialDay: KnockoutObservable<boolean>;
	}

	export interface DateRange {
		begin: Date;
		finish: Date;
	}

	export interface Parameter {
		tabIndex: string;
		width: KnockoutObservable<number>;
		baseDate: KnockoutObservable<Date | DateRange>;
		schedules: KnockoutObservableArray<DayData>;
		clickCell: (target: CLICK_CELL, day: DayData) => void;
	}

	export enum COLOR_CLASS {
		EVENT = 'event',
		CURRENT = 'current',
		SPECIAL = 'special',
		HOLIDAY = 'holiday',
		SUNDAY = 'sunday',
		SATURDAY = 'saturday',
		CONFIRMED = 'confirmed',
		SELF_ALTER_WTYPE = 'self-alter-wtype',
		SELF_ALTER_WTIME = 'self-alter-wtime',
		SELF_ALTER_WTIME_BEGIN = 'self-alter-wtime-begin',
		SELF_ALTER_WTIME_FINISH = 'self-alter-wtime-finish',
		OTHER_ALTER_WTYPE = 'other-alter-wtype',
		OTHER_ALTER_WTIME = 'other-alter-wtime',
		OTHER_ALTER_WTIME_BEGIN = 'other-alter-wtime-begin',
		OTHER_ALTER_WTIME_FINISH = 'other-alter-wtime-finish',
		REFLECTED_WTYPE = 'reflected-wtype',
		REFLECTED_WTIME = 'reflected-wtime',
		REFLECTED_WTIME_BEGIN = 'reflected-wtime-begin',
		REFLECTED_WTIME_FINISH = 'reflected-wtime-finish',
		DIFF_MONTH = 'diff-month',
		SAME_MONTH = 'same-month',
		NEED2WORK = 'need-2work',
		ACHIEVEMENT = 'achievement',
		CLASSIFICATION_FULLTIME = 'classification-fulltime',
		CLASSIFICATION_MORNING = 'classification-morning',
		CLASSIFICATION_AFTERNOON = 'classification-afternoon',
		CLASSIFICATION_HOLIDAY = 'classification-holiday',
		IMPRINT_WTYPE = 'imprint-wtype',
		IMPRINT_WTIME = 'imprint-wtime',
		IMPRINT_WTIME_BEGIN = 'imprint-wtime-begin',
		IMPRINT_WTIME_FINISH = 'imprint-wtime-finsh',
	}

	const D_FORMAT = 'YYYYMM';
	const COMPONENT_NAME = 'calendar';
	const COMPONENT_TEMP = `
        <div class="filter cf">
            <label class="filter-title" data-bind="i18n: 'KSU002_30'"></label>
			<div data-bind="
				attr: {
					'tabindex': $component.data.tabIndex
				},
				ntsComboBox: {
					width: '100px',
					name: $component.$i18n('KSU002_30'),
					value: $component.baseDate.start,
					options: $component.baseDate.options,
					optionsValue: 'id',
					optionsText: 'title',
					editable: false,
					enable: !!ko.unwrap($component.data.schedules).length,
					selectFirstIfNull: true,
					visibleItemsCount: 7,
					columns: [
						{ prop: 'title', length: 10 },
					]
				}"></div>
		</div>
        <div class="calendar-container">
            <div data-bind="if: !!ko.unwrap($component.baseDate.show), css: { 'title': !!ko.unwrap($component.baseDate.show) }">
                <div data-bind="ntsDatePicker: { value: $component.baseDate.model, dateFormat: 'yearmonth', valueFormat: 'YYYYMM', showJumpButtons: true }"></div>
            </div>
            <div class="month title">
                <div class="week cf" data-bind="foreach: { data: ko.unwrap($component.schedules).titles, as: 'day' }">
                    <div class="day" data-bind="calendar-day: day">
                        <div class="status cf">
                            <span data-bind="date: day.date, format: 'ddd'"></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="month" data-bind="foreach: { data: ko.unwrap($component.schedules).days, as: 'days' }">
                <div class="week cf" data-bind="foreach: { data: days, as: 'day' }">
                    <div class="day" data-bind="calendar-day: day">
                        <div class="status cf">
                            <span data-bind="calendar-date: day"></span>
                            <span class="sakura" data-bind="calendar-daisy: day, timeClick: -1, click: function(evt) { $component.data.clickCell.apply($vm, ['event', day, evt]); }"></span>
                        </div>
                        <div class="status cf" data-bind="calendar-holiday: day, timeClick: -1, click: function(evt) { $component.data.clickCell.apply($vm, ['holiday', day, evt]); }"></div>
                        <div class="data-info" data-bind="calendar-data-info: day, timeClick: -1, click: function(evt) { $component.data.clickCell.apply($vm, ['info', day, evt]); }"></div>
                    </div>
                </div>
            </div>
		</div>
		<div data-bind="popper: true"></div>
        <style type="text/css" rel="stylesheet">
            .calendar {
                display: inline-block;
            }
            .calendar .filter {
			    height: 35px;
                padding-bottom: 5px;
            }
            .calendar .filter-title {
                float: left;
                margin-right: 10px;
                line-height: 32px;
            }
            .calendar .calendar-container {
                float: left;
                box-sizing: border-box;
                border: 1px solid #808080;
            }
            .calendar .calendar-container .title {
                padding: 5px 0;
                background-color: #C7F391;
                border-bottom: 1px solid #808080;
            }
            .calendar .calendar-container .title .nts-datepicker-wrapper {
                margin: 0 calc(50% - 76px);
            }
			.calendar .calendar-container .title .nts-datepicker-wrapper>input,
			.calendar .calendar-container .title .nts-datepicker-wrapper>button {
				vertical-align: top;
			}
			.calendar .calendar-container .title .nts-datepicker-wrapper>input {
				height: 20px;
			}
			.calendar .calendar-container .title .nts-datepicker-wrapper>button {
				height: 29px;
			}
			.calendar .calendar-container .title .nts-datepicker-wrapper.arrow-bottom:before,
			.calendar .calendar-container .title .nts-datepicker-wrapper.arrow-bottom:after {
				left: 45px;
			}
            .calendar .calendar-container .month.title {
				padding: 0;
				min-height: 18px;
            }
            .calendar .calendar-container .month.title .week .day .status {
                background-color: #C7F391 !important;
				font-size: 11px;
            }
            .calendar .calendar-container .month.title .week .day .status span {
                color: #404040;
                font-size: 11px;
                font-weight: 300;
            }
            .calendar .calendar-container .month.title .week .day.sunday .status span {
                color: #f00;
            }
            .calendar .calendar-container .month.title .week .day.saturday .status span {
                color: #0000ff;
            }
            .calendar .calendar-container .month .week {
                box-sizing: border-box;
            }
            .calendar .calendar-container .month .week:not(:last-child)  {
                border-bottom: 1px solid #808080;
            }
            .calendar .calendar-container .month .week .day {
                float: left;
				width: 100px;
                box-sizing: border-box;
            }
            .calendar .calendar-container .month .week .day:not(:last-child) {
                border-right: 1px solid #808080;
            }
            .calendar .calendar-container .month .week .day .status {
				position: relative;
                text-align: center;
            }
            .calendar .calendar-container .month .week .day .status span {
                color: #404040;
                font-size: 9px;
				font-weight: 600;
				display: block;
    			line-height: 18px;
            }
            .calendar .calendar-container .month .week .day .status span+span.sakura {
			    top: 0px;
			    right: 0px;
			    width: 16px;
			    height: 16px;
			    position: absolute;
            }
            .calendar .calendar-container .month .week .day .status+.status {
				height: 18px;
				font-size: 11px;
				overflow: hidden;
                border-top: 1px solid #808080;
                border-bottom: 1px solid #808080;
            }
            .calendar .calendar-container .month .week .day .status {
                background-color: #EDFAC2;
			}
            .calendar .calendar-container .month .week .day.saturday .status {
                background-color: #8bd8ff;
            }
            .calendar .calendar-container .month .week .day.sunday .status,
            .calendar .calendar-container .month .week .day.holiday .status {
                background-color: #FABF8F;
            }
            .calendar .calendar-container .month .week .day.special .status {
                background-color: rgb(255, 192, 203);
			}
            .calendar .calendar-container .month .week .day.current .status {
                background-color: #ffff00;
			}
            .calendar .calendar-container .month:not(.title) .week .day.holiday .status,
            .calendar .calendar-container .month:not(.title) .week .day.holiday .status span {
                color: #f00;
			}
            .calendar .calendar-container .month .week .day .data-info {
                width: 100%;
                min-height: 40px;
                box-sizing: border-box;
            }
            .calendar .calendar-container .month .week .day.same-month .data-info {
                background-color: #ffffff;
			}
			.calendar .calendar-container .month .week .day.diff-month .data-info {
				background-color: #ddddd2;
			}
			.calendar .event-popper {
				top: 0px;
				left: 0px;
				z-index: -1;
				position: absolute;
				visibility: hidden;
				border: 2px solid #ddd;
				background-color: #ccc;
				border-radius: 5px;
				padding: 5px;
				width: 0;
				height: 0;
			}
			.calendar .event-popper>.epc {
				position: relative;
				width: 220px;
			}
			.calendar .event-popper:not(.hide-arrow)>.epc:before {
				content: '';
				top: 2px;
				left: -14px;
				height: 0px;
				display: block;
				position: absolute;
				border-left: 0;
				border-right: 9px solid #ccc;
				border-top: 7px solid transparent;
				border-bottom: 7px solid transparent;
			}
			.calendar .event-popper>.epc>.data {
				overflow: hidden;
			}
			.calendar .event-popper>.epc>.data table {
				width: 100%;
			}
			.calendar .event-popper>.epc>.data td:first-child {
				text-align: left;
    			vertical-align: top;
			}
			.calendar .event-popper.show {
				z-index: 9;
				position: fixed;
				visibility: visible;
				width: 220px;
			}
        </style>
        <style type="text/css" rel="stylesheet" data-bind="html: $component.style"></style>
	`;

	@handler({
		bindingName: 'popper'
	})
	export class SChedulerRefBindingHandler implements KnockoutBindingHandler {
		init($$popper: HTMLElement, _valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			// attach element to binding context
			_.extend(bindingContext, { $$popper });

			$$popper.classList.add('event-popper');

			return { controlsDescendantBindings: true };
		}
	}

	@handler({
		bindingName: 'calendar-day'
	})
	export class CalendarDayComponentBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const vmm = new ko.ViewModel();
			const dayData = ko.unwrap(valueAccessor());

			const { date, inRange, className, binding } = dayData;

			if (!inRange) {
				className.push(COLOR_CLASS.DIFF_MONTH);
			} else {
				if (moment(date).isSame(vmm.$date.now(), 'date')) {
					className.push(COLOR_CLASS.CURRENT);
				}

				className.push(COLOR_CLASS.SAME_MONTH);
			}

			if (binding) {
				if (_.isString(binding)) {
					ko.applyBindingsToNode(element, { [binding]: dayData }, bindingContext);

					return { controlsDescendantBindings: true };
				}
			}

			className.push(moment(date).locale('en').format('dddd').toLowerCase());

			ko.computed({
				read: () => {
					element.className = 'day';

					ko.unwrap(className)
						.filter((c: string) => !!c)
						.forEach((c: string) => element.classList.add(c));
				},
				owner: dayData,
				disposeWhenNodeIsRemoved: element
			});
		}
	}

	@handler({
		bindingName: 'calendar-daisy'
	})
	export class DaisyBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext & { $$popper: HTMLElement }): void | { controlsDescendantBindings: boolean; } {
			const dayData = valueAccessor();
			const { $$popper } = bindingContext;
			const { inRange, binding, data, className } = dayData;

			if (inRange) {
				if (data) {
					ko.computed({
						read: () => {
							const event = ko.unwrap(data.event);
							const specs = ko.unwrap(data.specialDay);

							if (event) {
								className.push(COLOR_CLASS.EVENT);
							} else {
								className.remove(COLOR_CLASS.EVENT);
							}

							if (specs) {
								className.push(COLOR_CLASS.SPECIAL);
							} else {
								className.remove(COLOR_CLASS.SPECIAL);
							}

							ko.applyBindingsToNode(element, { icon: !!event ? 120 : 121 });
						},
						owner: dayData,
						disposeWhenNodeIsRemoved: element
					});

					$(element)
						.on('mouseover', () => {
							const event = ko.unwrap(data.event);

							if (event !== null) {
								const { width, top, left } = element.getBoundingClientRect();

								$.Deferred()
									.resolve(true)
									.then(() => {
										$$popper.innerHTML = `<div class="epc"><div class="data">${event}</div></div>`;
									})
									.then(() => {
										const pbound = $$popper.getBoundingClientRect();
										const epc = $($$popper).find('.epc').get(0);
										const ebound = epc.getBoundingClientRect();

										const top1 = top - 7;
										const top2 = window.innerHeight - ebound.height - 7;

										const left1 = left + width + 7;
										const left2 = window.innerWidth - pbound.width - 30;

										$$popper.style.top = `${Math.min(top1, top2)}px`;
										$$popper.style.left = `${Math.min(left1, left2)}px`;
										$$popper.style.height = `${ebound.height}px`;

										if (top1 >= top2 || left1 >= left2) {
											$$popper.classList.add('hide-arrow');
										} else {
											$$popper.classList.remove('hide-arrow');
										}
									})
									.then(() => {
										const pbound = $$popper.getBoundingClientRect();

										if (pbound.top !== 0 && pbound.left !== 0) {
											$$popper.classList.add('show');
										} else {
											$$popper.classList.remove('show');
										}
									});
							}
						})
						.on('mouseout', () => {
							$.Deferred()
								.resolve(true)
								.then(() => {
									$$popper.innerHTML = '';

									$$popper.classList.remove('show');
								}).then(() => {
									$$popper.style.top = '0px';
									$$popper.style.left = '0px';
									$$popper.style.height = '0px';

									$$popper.classList.remove('show');
								});
						});
				}

				if (binding) {
					if (!_.isString(binding)) {
						const { daisy } = binding;

						if (daisy) {
							ko.applyBindingsToNode(element, { [daisy]: dayData }, bindingContext);
						}
					}
				}

				return { controlsDescendantBindings: true };
			}
		}
	}

	@handler({
		bindingName: 'calendar-holiday'
	})
	export class CalendarHolidayBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const dayData = valueAccessor();
			const { binding, inRange, data, className } = dayData;

			if (!inRange) {
				element.innerHTML = '&nbsp;';
			} else {
				if (data) {
					ko.computed({
						read: () => {
							const holiday = ko.unwrap(data.holiday);

							if (holiday) {
								element.innerHTML = holiday;
								className.push(COLOR_CLASS.HOLIDAY);
							} else {
								element.innerHTML = '&nbsp;';
								className.remove(COLOR_CLASS.HOLIDAY);
							}
						},
						owner: dayData,
						disposeWhenNodeIsRemoved: element
					});
				} else {
					element.innerHTML = '&nbsp;';
				}

				if (binding) {
					if (!_.isString(binding)) {
						const { holiday } = binding;

						if (holiday) {
							ko.applyBindingsToNode(element, { [holiday]: dayData }, bindingContext);

							return { controlsDescendantBindings: true };
						}
					}
				}
			}
		}
	}

	@handler({
		bindingName: 'calendar-date'
	})
	export class CalendarDateBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const dayData = ko.unwrap(valueAccessor());
			const { date, startDate, binding, inRange } = dayData;

			if (!inRange) {
				element.innerHTML = '&nbsp;';
			} else {
				const mm = moment(date);

				if (!startDate) {
					element.innerHTML = mm.format('D');
				} else {
					element.innerHTML = mm.format('M/D');
				}

				if (binding) {
					if (!_.isString(binding)) {
						const { date } = binding;

						if (date) {
							ko.applyBindingsToNode(element, { [date]: dayData }, bindingContext);

							return { controlsDescendantBindings: true };
						}
					}
				}
			}
		}
	}

	@handler({
		bindingName: 'calendar-data-info'
	})
	export class CalendarInfoBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => DayData, allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const dayData = ko.unwrap(valueAccessor());
			const { inRange, binding } = dayData;

			element.innerHTML = '&nbsp;';

			if (inRange) {
				if (binding) {
					if (!_.isString(binding)) {
						const { dataInfo } = binding;

						if (dataInfo) {
							ko.applyBindingsToNode(element, { [dataInfo]: dayData }, bindingContext);

							return { controlsDescendantBindings: true };
						}
					}
				}
			}
		}
	}

	@handler({
		bindingName: COMPONENT_NAME,
		validatable: true,
		virtual: false
	})
	export class CalendarComponentBindingHandler implements KnockoutBindingHandler {
		init(element: any, valueAccessor: () => KnockoutObservableArray<DayData>, allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const name = COMPONENT_NAME;
			const schedules = valueAccessor();
			const width = allBindingsAccessor.get('width');
			const baseDate = allBindingsAccessor.get('baseDate');
			const clickCell = allBindingsAccessor.get('click-cell');
			const tabIndex = element.getAttribute('tabindex') || allBindingsAccessor.get('tabindex') || '1';
			const params = { width, baseDate, schedules, tabIndex, clickCell };
			const component = { name, params };

			element.classList.add('cf');
			element.classList.add('calendar');

			ko.applyBindingsToNode(element, { component }, bindingContext);

			return { controlsDescendantBindings: true };
		}
	}

	@component({
		name: COMPONENT_NAME,
		template: COMPONENT_TEMP
			.replace(/\n{1,}/g, ' ')
			.replace(/\t{1,}/g, ' ')
			.replace(/\s{1,}/g, ' ')
			.replace(/\s{\s/g, '{')
			.replace(/\s}\s/g, '}')
			.replace(/;}/g, '}')
			.replace(/;}/g, '}')
			.replace(/:\s/g, ':')
			.replace(/;\s/g, ';')
	})
	export class CalendarComponent extends ko.ViewModel {
		style: KnockoutObservable<string> = ko.observable('');

		baseDate: BaseDate = {
			show: ko.computed(() => true),
			model: ko.observable(null),
			start: ko.observable(null),
			options: ko.observableArray([])
		};

		schedules!: KnockoutComputed<Schedule>;

		constructor(private data: Parameter) {
			super();

			const vm = this;
			const date = new Date();

			if (!vm.data) {
				vm.data = {
					tabIndex: '1',
					width: ko.observable(630),
					baseDate: ko.observable(date),
					schedules: ko.observableArray([]),
					clickCell: () => { }
				};
			}

			const { tabIndex, width, baseDate, schedules, clickCell } = vm.data;

			if (!tabIndex) {
				vm.data.tabIndex = '1';
			}

			if (!ko.unwrap(width)) {
				if (ko.isObservable(width)) {
					vm.data.width(630);
				} else {
					vm.data.width = ko.observable(630);
				}
			}

			if (ko.unwrap(baseDate) && !ko.isObservable(baseDate)) {
				vm.data.baseDate = ko.observable(date);
			}

			if (!ko.unwrap(schedules)) {
				if (ko.isObservable(schedules)) {
					vm.data.schedules([]);
				} else {
					vm.data.schedules = ko.observableArray([]);
				}
			}

			if (!clickCell) {
				vm.data.clickCell = () => { };
			}
		}

		created() {
			const vm = this;
			const { data, baseDate } = vm;
			const { options, start } = baseDate;

			const startDate = moment().startOf('week');
			const listDates = _.range(0, 7)
				.map(m => startDate.clone().add(m, 'day'))
				.map(d => ({
					id: d.get('day'),
					title: d.format('dddd')
				}));

			vm.$window
				.storage(KSU_USER_DATA)
				.then((v: StorageData) => {
					options(listDates);

					return v;
				})
				.then((v: StorageData) => vm.baseDate.start(v.fdate));

			ko.computed({
				read: () => {
					const date = ko.unwrap(data.baseDate);

					if (!_.isDate(date)) {
						vm.baseDate.model(moment().format(D_FORMAT));
					} else {
						vm.baseDate.model(moment(date).format(D_FORMAT));
					}
				},
				owner: vm
			});

			vm.baseDate.start
				.subscribe(fdate => {
					vm.$window
						.storage(KSU_USER_DATA)
						.then((store: undefined | StorageData) => {
							if (store) {
								const { wtypec, wtimec } = store;

								vm.$window.storage(KSU_USER_DATA, { fdate, wtimec, wtypec });
							} else {
								vm.$window.storage(KSU_USER_DATA, { fdate, wtimec: null, wtypec: null });
							}
						});
				});

			vm.baseDate.model
				.subscribe((date: string) => {
					if (date && date.match(/^\d{6}$/)) {
						data.baseDate(moment(date, D_FORMAT).toDate());
					}
				});

			vm.style = ko.computed({
				read: () => {
					const width = ko.unwrap(data.width);

					return (`.calendar .calendar-container .month .week .day { width: ${width || 85}px !important; }`)
						.replace(/\n{1,}/g, ' ')
						.replace(/\t{1,}/g, ' ')
						.replace(/\s{1,}/g, ' ')
						.replace(/(px){2,}/g, 'px')
						.replace(/%px/g, '%');
				},
				owner: vm
			});

			vm.schedules = ko.computed({
				read: () => {
					const locale = moment.locale();
					const raws = ko.unwrap(data.schedules);
					const startDate = ko.unwrap(vm.baseDate.start);

					moment.updateLocale(locale, {
						week: {
							dow: ko.unwrap(startDate),
							doy: 0
						}
					});

					if (!raws.length) {
						const start = moment.utc().startOf('week');

						const raws: any[] = [];
						const days = _.chain(_.range(0, 42, 1))
							.map((d) => ({
								date: start.clone().add(d, 'day').toDate(),
								inRange: false,
								startDate: false,
								data: null,
								binding: null,
								className: ko.observableArray([])
							}))
							.chunk(7)
							.value();

						const [titles] = days;

						return {
							raws,
							days,
							titles
						};
					}

					const [begin] = raws;
					const [finsh] = raws.slice(-1);

					const initRange = (start: moment.Moment, diff: number): DayData[] => _.range(0, Math.abs(diff), 1)
						.map((d) => start.clone().add(d, 'day'))
						.map((d) => ({
							date: d.toDate(),
							inRange: false,
							startDate: false,
							data: null,
							binding: null,
							className: ko.observableArray([])
						}));

					const start1 = moment(begin.date).startOf('week');
					const start2 = moment(finsh.date).add(1, 'day');
					const diff1 = start1.diff(begin.date, 'day');

					const befores = initRange(start1, diff1);
					const afters = initRange(start2, 42 - befores.length - raws.length);

					moment.updateLocale(locale, {
						week: {
							dow: 0,
							doy: 0
						}
					});

					vm.$nextTick(() => $(vm.$el).find('[data-bind]').removeAttr('data-bind'));

					const days = _.chunk([...befores, ...raws, ...afters], 7);
					const [titles] = days;

					return { raws, days, titles };
				},
				owner: vm
			});

			vm.baseDate.show = ko.computed({
				read: () => {
					const bDate = ko.unwrap(data.baseDate);

					return _.isDate(bDate);
				},
				owner: vm
			});

			data.baseDate
				.subscribe((baseDate) => {
					const schedules = ko.unwrap(data.schedules);
					const [first] = schedules;
					const [last] = schedules.slice(-1);
					const start = moment(_.isDate(baseDate) ? baseDate : baseDate.begin).startOf('day');
					const isStartDate = (d: moment.Moment) => {
						if (d.get('date') === 1) {
							return true;
						}

						if (!_.isDate(baseDate)) {
							return moment(baseDate.begin).isSame(d.startOf('day'), 'date');
						}

						return false;
					};
					const initRange = (diff: number) => {
						const daysOfMonth = _.range(0, Math.abs(diff + 1), 1)
							.map((d) => start.clone().add(d, 'day'))
							.map((d) => ({
								date: d.toDate(),
								inRange: true,
								startDate: isStartDate(d),
								data: null,
								binding: null,
								className: ko.observableArray([])
							}));

						data.schedules(daysOfMonth);
					};

					if (_.isDate(baseDate)) {
						if (!schedules.length || !start.isSame(first.date, 'month')) {
							const start = moment(baseDate).startOf('month').startOf('day');
							const end = moment(baseDate).endOf('month').endOf('day');
							const diff = end.diff(start, 'day');

							initRange(diff);
						}
					} else {
						const { begin, finish } = baseDate;
						const initDate = () => {
							const diff = moment(finish).diff(begin, 'day');

							initRange(diff);
						};

						if (!first || !last) {
							initDate();
						} else {
							const notStart = !moment(begin).isSame(first.date, 'date');
							const notEnd = !moment(finish).isSame(last.date, 'date');

							if (notStart && notEnd) {
								initDate();
							}
						}
					}
				});
		}
	}

	interface DateOption {
		id: number;
		title: string;
	}

	interface Schedule {
		raws: DayData[];
		days: DayData[][];
		titles: DayData[];
	}

	interface BaseDate {
		show: KnockoutComputed<boolean>;
		model: KnockoutObservable<string | null>;
		start: KnockoutObservable<number | null>;
		options: KnockoutObservableArray<DateOption>;
	}
}