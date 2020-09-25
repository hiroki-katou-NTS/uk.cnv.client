/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.memento {

	export interface Options {
		size: number;
		replace?: (data: any, replacer: any) => void;
	}

	interface Memento {
		size: number;
		stopWatch: boolean;
		undo: KnockoutObservableArray<any>;
		redo: KnockoutObservableArray<any>;
	}

	const defaultMemento = (size: number): Memento => ({
		size,
		stopWatch: false,
		undo: ko.observableArray([]),
		redo: ko.observableArray([])
	});

	const resetMemento = (memento: Memento, size: number) => {
		memento.size = size;
		memento.stopWatch = false;
		memento.undo([]);
		memento.redo([]);
	};

	const stripMemory = (memory: KnockoutObservableArray<any>, size: number) => {
		while (ko.unwrap(memory).length > size) {
			memory.shift();
		}
	};

	const memento = function (target: KnockoutObservableArray<any>, options: Options) {
		if (!options) {
			options = {
				size: 9999
			};
		}

		if (options.size < 1) {
			options.size = 9999;
		}

		const $memento: Memento = defaultMemento(options.size);

		target
			.subscribe((data: any[]) => {
				if (!$memento.stopWatch) {
					// reset redo data
					$memento.redo([]);

					// push old data to memories
					$memento.undo.unshift(data);

					// remove old record when memory size has large than config
					stripMemory($memento.undo, options.size);
				}
			}, null, 'beforeChange');

		_.extend(target, {
			mock: function $mock(data: any[]) {
				$memento.stopWatch = true;

				target(data);

				$memento.stopWatch = false;
			},
			reset: function $reset(data?: any[]) {
				if (data) {
					target(data);
				}

				resetMemento($memento, options.size);
			},
			undo: function $undo() {
				if (ko.unwrap($memento.undo).length) {
					const current = ko.unwrap(target);
					const preview = $memento.undo.shift();

					$memento.redo.unshift(current);

					// remove old record when memory size has large than config
					stripMemory($memento.redo, options.size);

					$memento.stopWatch = true;

					if (!options.replace) {
						target(preview);
					} else {
						options.replace(current, preview);
						target(current);
					}

					$memento.stopWatch = false;
				}
			},
			undoAble: ko.computed(() => !!ko.unwrap($memento.undo).length),
			redo: function $redo() {
				if (ko.unwrap($memento.redo).length) {
					const current = ko.unwrap(target);
					const forward = $memento.redo.shift();

					$memento.undo.unshift(current);

					// remove old record when memory size has large than config
					stripMemory($memento.undo, options.size);

					$memento.stopWatch = true;

					if (!options.replace) {
						target(forward);
					} else {
						options.replace(current, forward);
						target(current);
					}

					$memento.stopWatch = false;
				}
			},
			redoAble: ko.computed(() => !!ko.unwrap($memento.redo).length)
		});

		_.extend(window, { memento: { target, $memento } });

		return target;
	};

	_.extend(ko.extenders, { memento });
}

interface MementoObservableArray<T> extends KnockoutObservableArray<T> {
	undo(): void;
	redo(): void;
	mock(data: any): void;
	reset(data?: any[]): void;
	undoAble: KnockoutComputed<boolean>;
	redoAble: KnockoutComputed<boolean>;
}

module nts.uk.ui.at.ksu002.calendar {
	const D_FORMAT = 'YYYYMM';
	const COMPONENT_NAME = 'scheduler';
	const COMPONENT_TEMP = `
        <div class="filter cf">
            <label class="filter-title" data-bind="i18n: 'KSU002_30'"></label>
            <div data-bind="ntsComboBox: {
                width: '100px',
                name: $component.$i18n('KSU002_30'),
                value: $component.startDate,
                options: $component.dateOptions,
                optionsValue: 'id',
                optionsText: 'title',
                editable: false,
                selectFirstIfNull: true,
                columns: [
                    { prop: 'title', length: 10 },
                ]}"></div>
        </div>
        <div class="calendar-container">
            <div data-bind="if: !!ko.unwrap($component.showBaseDate), css: { 'title': !!ko.unwrap($component.showBaseDate) }">
                <div data-bind="ntsDatePicker: { value: baseDate, dateFormat: 'yearmonth', valueFormat: 'YYYYMM', showJumpButtons: true }"></div>
            </div>
            <div class="calendar title">
                <div class="week cf" data-bind="foreach: { data: titleDays, as: 'day' }">
                    <div class="day" data-bind="scheduler-class: day">
                        <div class="status cf">
                            <span data-bind="date: day, format: 'ddd'"></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="calendar" data-bind="foreach: { data: schedules, as: 'days' }">
                <div class="week cf" data-bind="foreach: { data: days, as: 'day' }">
                    <div class="day" data-bind="scheduler-class: day">
                        <div class="status cf">
                            <span data-bind="scheduler-date: day"></span>
                            <svg data-bind="scheduler-daisy: day, click: function() { $component.data.clickCell.apply($vm, ['event', day]); }"></svg>
                        </div>
                        <div class="status cf" data-bind="scheduler-holiday: day, click: function() { $component.data.clickCell.apply($vm, ['holiday', day]); }"></div>
                        <div class="data-info" data-bind="scheduler-data-info: day, click: function() { $component.data.clickCell.apply($vm, ['info', day]); }"></div>
                    </div>
                </div>
            </div>
        </div>
        <style type="text/css" rel="stylesheet">
            .scheduler {
                display: inline-block;
            }
            .scheduler .filter {
                padding-bottom: 5px;
            }
            .scheduler .filter-title {
                float: left;
                margin-right: 10px;
                line-height: 32px;
            }
            .scheduler .calendar-container {
                float: left;
                box-sizing: border-box;
                border: 1px solid #808080;
            }
            .scheduler .calendar-container .title {
                padding: 5px 0;
                background-color: #C7F391;
                border-bottom: 1px solid #808080;
            }
            .scheduler .calendar-container .title .nts-datepicker-wrapper {
                margin: 0 calc(50% - 76px);
            }
			.scheduler .calendar-container .title .nts-datepicker-wrapper>input,
			.scheduler .calendar-container .title .nts-datepicker-wrapper>button {
				vertical-align: top;
			}
			.scheduler .calendar-container .title .nts-datepicker-wrapper>input {
				height: 20px;
			}
			.scheduler .calendar-container .title .nts-datepicker-wrapper>button {
				height: 29px;
			}
			.scheduler .calendar-container .title .nts-datepicker-wrapper.arrow-bottom:before,
			.scheduler .calendar-container .title .nts-datepicker-wrapper.arrow-bottom:after {
				left: 45px;
			}
            .scheduler .calendar-container .calendar.title {
                padding: 0;
            }
            .scheduler .calendar-container .calendar.title .week .day .status {
                background-color: #C7F391 !important;
            }
            .scheduler .calendar-container .calendar.title .week .day .status span {
                color: #000;
                font-size: 11px;
                font-weight: 300;
            }
            .scheduler .calendar-container .calendar.title .week .day.sunday .status span {
                color: #f00;
            }
            .scheduler .calendar-container .calendar.title .week .day.saturday .status span {
                color: #0000ff;
            }
            .scheduler .calendar-container .calendar .week {
                box-sizing: border-box;
            }
            .scheduler .calendar-container .calendar .week:not(:last-child)  {
                border-bottom: 1px solid #808080;
            }
            .scheduler .calendar-container .calendar .week .day {
                float: left;
				width: 100px;
                box-sizing: border-box;
            }
            .scheduler .calendar-container .calendar .week .day:not(:last-child) {
                border-right: 1px solid #808080;
            }
            .scheduler .calendar-container .calendar .week .day .status {
				position: relative;
                text-align: center;
                background-color: #EDFAC2;
            }
            .scheduler .calendar-container .calendar .week .day .status span {
                color: gray;
                font-size: 9px;
                font-weight: 600;
            }
            .scheduler .calendar-container .calendar .week .day .status span+svg {
			    top: 1px;
			    right: 1px;
			    position: absolute;
			    width: 14px;
			    height: 14px;
            }
            .scheduler .calendar-container .calendar .week .day .status+.status {
                border-top: 1px solid #808080;
                border-bottom: 1px solid #808080;
            }
            .scheduler .calendar-container .calendar .week .day.sunday .status {
                background-color: #FABF8F;
            }
            .scheduler .calendar-container .calendar .week .day.saturday .status {
                background-color: #9BC2E6;
            }
            .scheduler .calendar-container .calendar .week .day .data-info{
                width: 100%;
                height: 40px;
                box-sizing: border-box;
            }
            .scheduler .calendar-container .calendar .week .day.diff-month .data-info {
                background-color: #d9d9d9;
            }
            .scheduler .calendar-container .calendar .week .day.same-month .data-info {
                background-color: #ffffff;
            }
        </style>
        <style type="text/css" rel="stylesheet" data-bind="html: $component.style"></style>
	`;

	const DAISY_FLOWER = [
		'M62.554,27.198c-0.365-4.156-3.362-7.441-7.416-8.286c-2.35-0.49-4.705-0.172-6.996,0.558',
		'c1.847-2.143,3.115-4.718,3.35-7.787C51.96,5.559,47.31,0.54,41.25,0.047c-8.246-0.671-13.233,5.999-13,12.672',
		'c-1.551-2.083-3.583-3.782-6.097-4.799c-4.517-1.829-10.644-1.262-13.788,2.847c-2.79,3.647-1.927,8.734,0.641,12.231',
		'c0.702,0.956,1.884,2.224,3.276,3.38C5.801,26.25-0.824,29.148,0.343,36.63c0.775,4.968,3.562,9.088,8.722,9.993',
		'c2.043,0.359,4.279,0.127,6.333-0.608c-3.3,5.777-3.781,13.294,2.912,15.989c4.314,1.737,10.382,0.547,13.524-2.857',
		'c1.537-1.666,2.448-3.566,2.943-5.58c0.881,2.464,2.283,4.682,4.524,6.336c4.148,3.062,9.536,2.332,13.332-1.006',
		'c6.733-5.921,2.833-15.803-3.361-21.481c2.442,0.385,4.932,0.214,7.357-0.864C60.378,34.888,62.922,31.386,62.554,27.198z',
		'M42.155,2.356c6.386,1.269,8.813,8.055,5.839,13.568c-2.005,3.717-5.749,5.917-9.479,7.57c-0.25,0.111-0.412,0.271-0.501,0.454',
		'c-0.472-0.375-0.992-0.678-1.531-0.949c0.985-2.425,3.023-4.156,5.514-4.922c0.447-0.137,0.251-0.743-0.188-0.681',
		'c-2.848,0.398-5.133,2.544-5.941,5.267c0.011-0.117-0.002-0.24-0.059-0.367c-0.094-0.209-0.252-0.367-0.44-0.496',
		'c0.275-1.104,0.7-2.17,1.229-3.177c0.225-0.429-0.381-0.779-0.647-0.378c-0.67,1.009-1.075,2.083-1.283,3.231',
		'c-0.272-0.085-0.554-0.147-0.806-0.206c-0.116-0.112-0.221-0.232-0.331-0.348c0.745-2.439,0.496-5.058-0.75-7.309',
		'c-0.195-0.352-0.81-0.114-0.665,0.28c0.756,2.059,0.991,4.081,0.684,6.18C27.226,12.869,31.515,0.242,42.155,2.356z M11.551,10.541',
		'c8.182-5.844,16.398,2.854,18.081,10.437c-1.223,0.167-2.4,0.548-3.502,1.077c-1.062-1.325-1.684-2.942-1.817-4.643',
		'c-0.039-0.496-0.764-0.508-0.78,0c-0.061,1.91,0.613,3.63,1.852,5.019c-0.364,0.206-0.721,0.423-1.061,0.665',
		'c-1.118-0.797-2.163-1.672-3.199-2.572c-0.251-0.218-0.696,0.065-0.489,0.377c0.779,1.172,1.871,2.032,3.137,2.619',
		'c-0.347,0.279-0.684,0.57-0.996,0.886c-1.477-1.689-3.909-3.182-6.201-3.095c-0.366,0.014-0.599,0.584-0.205,0.757',
		'c0.968,0.425,1.993,0.706,2.951,1.151c1.045,0.485,1.963,1.153,2.866,1.861c-0.785,0.928-1.439,1.968-1.88,3.121',
		'c-0.071-0.005-0.143-0.011-0.215-0.018c-1.198-0.622-2.621-1.099-4.149-1.406C10.001,23.423,5.234,15.052,11.551,10.541z',
		'M11.508,44.627c-4.51,0.134-7.939-2.757-8.822-7.214c-1.741-8.783,7.546-10.259,14.03-8.382c0.16,0.054,0.319,0.106,0.478,0.149',
		'c0.767,0.247,1.49,0.539,2.14,0.875c0.149,0.077,0.295,0.112,0.435,0.119c-0.033,0.195-0.075,0.387-0.097,0.587',
		'c-0.051,0.461-0.055,0.92-0.038,1.378c-1.157-0.228-2.222-0.741-3.183-1.431c-0.419-0.301-0.852,0.337-0.53,0.687',
		'c0.997,1.086,2.329,1.592,3.781,1.625c0.073,0.585,0.186,1.165,0.352,1.731c-2.104,0.363-4.01,0.039-6.074-0.679',
		'c-0.471-0.164-0.706,0.518-0.325,0.771c1.962,1.302,4.463,1.427,6.661,0.708c0.02,0.053,0.046,0.103,0.066,0.155',
		'c-1.365,0.158-2.721,1.3-2.695,2.699c0.009,0.465,0.726,0.737,0.943,0.256c0.254-0.563,0.488-1.14,0.966-1.555',
		'c0.332-0.289,0.722-0.465,1.135-0.582c0.466,0.971,1.063,1.883,1.773,2.698c-1.478,0.58-2.916,1.609-4.206,2.923',
		'C16.186,43.352,14.089,44.55,11.508,44.627z M21.042,60.332c-6.233-1.001-6.587-8.169-4.305-12.828',
		'c1.555-3.174,4.252-4.972,6.624-7.395c0.795,0.743,1.696,1.362,2.66,1.853c-1.729,1.33-3.585,1.647-5.788,1.674',
		'c-0.521,0.006-0.633,0.769-0.123,0.912c2.375,0.665,5-0.466,6.607-2.254c0.322,0.136,0.647,0.266,0.982,0.368',
		'c-0.662,1.146-1.302,2.366-1.537,3.644c-0.096,0.52,0.592,0.872,0.887,0.374c0.683-1.156,1.001-2.526,1.259-3.859',
		'c0.343,0.082,0.69,0.152,1.044,0.196c0.421,0.053,0.842,0.068,1.263,0.067c-1.1,1.363-1.627,3.583-0.464,5.048',
		'c0.323,0.407,0.927-0.041,0.82-0.477c-0.179-0.725-0.466-1.355-0.442-2.123c0.026-0.815,0.293-1.543,0.758-2.207',
		'c0.061-0.087,0.064-0.178,0.036-0.261c0.682-0.049,1.357-0.164,2.017-0.341c0.001,1.055,0.022,2.141,0.081,3.235',
		'C32.979,53.527,30.835,61.905,21.042,60.332z M30.455,40.831c-4.608-0.068-8.627-4.418-8.67-8.967',
		'c-0.051-5.346,4.955-8.969,9.915-8.875c0.34,0.007,0.579-0.127,0.744-0.318c0.275,0.157,0.615,0.247,0.955,0.348',
		'c0.312,0.092,0.67,0.218,1.028,0.285c2.708,1.957,4.933,4.84,5.129,8.177C39.846,36.441,35.461,40.904,30.455,40.831z',
		'M53.708,53.54c-1.832,7.378-11.802,7.658-15.656,2.046c-2.597-3.782-2.679-8.614-3.006-13.012',
		'c-0.012-0.163-0.067-0.294-0.138-0.409c0.427-0.184,0.837-0.403,1.239-0.637c0.482,1.485,0.56,2.958,0.429,4.57',
		'c-0.039,0.483,0.685,0.544,0.818,0.111c0.521-1.69,0.258-3.554-0.543-5.125c0.41-0.278,0.808-0.575,1.182-0.899',
		'c0.26,0.428,0.506,0.865,0.725,1.314c0.393,0.81,0.656,1.689,1.095,2.474c0.208,0.372,0.911,0.235,0.855-0.232',
		'c-0.182-1.501-1.089-2.919-2.133-4.067c0.511-0.502,0.957-1.061,1.368-1.643c1.333,1.321,3.128,2.239,4.964,2.441',
		'c0.474,0.052,0.538-0.68,0.109-0.808c-1.767-0.525-3.232-1.228-4.62-2.289c0.524-0.868,0.931-1.806,1.18-2.798',
		'c1.106,0.611,2.258,1.171,3.438,1.641C50.346,40.066,55.321,47.042,53.708,53.54z M53.726,35.333',
		'c-4.191,0.86-8.251-0.871-11.85-2.827c0.019-0.501-0.003-0.997-0.059-1.486c1.285,0.175,2.47,0.679,3.574,1.358',
		'c0.3,0.184,0.66-0.259,0.383-0.497c-1.151-0.987-2.546-1.421-4.045-1.441c-0.05-0.289-0.107-0.576-0.178-0.86',
		'c1.605-0.102,3.189-0.233,4.773-0.53c0.378-0.071,0.294-0.696-0.091-0.673c-1.634,0.095-3.225,0.359-4.831,0.653',
		'c-0.149-0.483-0.319-0.957-0.531-1.41c1.761,0.104,3.476-0.728,4.312-2.377c0.195-0.385-0.314-0.659-0.565-0.33',
		'c-0.983,1.289-2.594,2.04-4.2,1.845c-0.254-0.435-0.531-0.855-0.843-1.246c6.152-2.513,16.981-9.619,20.48-0.082',
		'C61.81,30.208,58.353,34.383,53.726,35.333z'
	].join(' ');

	@handler({
		bindingName: 'scheduler-daisy'
	})
	export class DaisyBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => DayData): void {
			const day = ko.unwrap(valueAccessor());

			element.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
			element.setAttribute('viewBox', '0 0 64 64');

			if (!day.inRange) {
				element.innerHTML = '';
			} else {
				const p = document.createElementNS('http://www.w3.org/2000/svg', 'path');

				p.setAttribute('style', 'fill: #333');
				p.setAttributeNS(null, 'd', DAISY_FLOWER);

				element.appendChild(p);
			}
		}
	}

	@handler({
		bindingName: 'scheduler-class'
	})
	export class ClassDayComponentBindingHandler implements KnockoutBindingHandler {
		update(element: any, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, _bindingContext: KnockoutBindingContext): void {
			const day = ko.unwrap(valueAccessor());

			if (_.isDate(day)) {
				element.classList.add(moment(day).locale('en').format('dddd').toLowerCase());
			} else {
				if (!day.inRange) {
					element.classList.add('diff-month');
				} else {
					element.classList.add('same-month');
				}

				element.classList.add(moment(day.date).locale('en').format('dddd').toLowerCase());
			}
		}
	}

	@handler({
		bindingName: 'scheduler-holiday'
	})
	export class SchedulerHolidayBindingHandler implements KnockoutBindingHandler {
		update(element: any, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, _bindingContext: KnockoutBindingContext): void {
			const data = ko.unwrap(valueAccessor());

			if (data.inRange) {
				element.innerHTML = '&nbsp;';
			}
			else {
				element.innerHTML = '&nbsp;'; //moment(data.date).format('D');
			}
		}
	}

	@handler({
		bindingName: 'scheduler-date'
	})
	export class SchedulerDateBindingHandler implements KnockoutBindingHandler {
		update(element: any, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, _bindingContext: KnockoutBindingContext): void {
			const data = ko.unwrap(valueAccessor());

			if (!data.inRange) {
				element.innerHTML = '&nbsp;';
			}
			else {
				if (!data.startDate) {
					element.innerHTML = moment(data.date).format('D');
				} else {
					element.innerHTML = moment(data.date).format('M/D');
				}
			}
		}
	}

	@handler({
		bindingName: 'scheduler-data-info'
	})
	export class SchedulerInfoBindingHandler implements KnockoutBindingHandler {
		update(element: any, valueAccessor: () => DayData, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, _bindingContext: KnockoutBindingContext): void {
			const data = ko.unwrap(valueAccessor());

			if (data.inRange) {
				element.innerHTML = '&nbsp;';
			}
			else {
				element.innerHTML = '&nbsp;'; //moment(data.date).format('D');
			}
		}
	}

	@handler({
		bindingName: COMPONENT_NAME,
		validatable: true,
		virtual: false
	})
	export class CalendarComponentBindingHandler implements KnockoutBindingHandler {
		init(element: any, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const name = COMPONENT_NAME;
			const schedules = valueAccessor();
			const width = allBindingsAccessor.get('width');
			const baseDate = allBindingsAccessor.get('baseDate');
			const clickCell = allBindingsAccessor.get('click-cell');
			const params = { width, baseDate, schedules, clickCell };
			const component = { name, params };

			element.classList.add('cf');
			element.classList.add('scheduler');

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
		baseDate: KnockoutObservable<string | null> = ko.observable(null);
		startDate: KnockoutObservable<number | null> = ko.observable(null);
		dateOptions: KnockoutObservableArray<DateOption> = ko.observableArray([]);

		schedules!: KnockoutComputed<DayData[][]>;
		titleDays!: KnockoutComputed<Date[]>;

		showBaseDate!: KnockoutComputed<boolean>;

		constructor(private data: Parameter) {
			super();

			const vm = this;
			const date = new Date();

			if (!vm.data) {
				vm.data = {
					width: ko.observable(630),
					baseDate: ko.observable(date),
					schedules: ko.observableArray([]).extend({ memento: { size: 20 } }) as any,
					clickCell: () => { }
				};
			}

			const { width, baseDate, schedules, clickCell } = vm.data;

			if (!ko.unwrap(width)) {
				if (ko.isObservable(width)) {
					vm.data.width(630);
				} else {
					vm.data.width = ko.observable(630)
				}
			}

			if (!ko.unwrap(baseDate)) {
				if (ko.isObservable(baseDate)) {
					vm.data.baseDate(date);
				} else {
					vm.data.baseDate = ko.observable(date)
				}
			}

			if (!ko.unwrap(schedules)) {
				if (ko.isObservable(schedules)) {
					vm.data.schedules([]);

					if (!vm.data.schedules.mock) {
						vm.data.schedules.extend({ memento: { size: 20 } });
					}
				} else {
					vm.data.schedules = ko.observableArray([]).extend({ memento: { size: 20 } }) as any;
				}
			}

			if (!clickCell) {
				vm.data.clickCell = () => { };
			}
		}

		created() {
			const vm = this;
			const { data } = vm;

			const startDate = moment().startOf('week');
			const listDates = _.range(0, 7)
				.map(m => startDate.clone().add(m, 'day'))
				.map(d => ({ id: d.get('day'), title: d.format('dddd') }));

			vm.dateOptions(listDates);

			ko.computed({
				read: () => {
					const date = ko.unwrap(data.baseDate);

					if (_.isDate(date)) {
						vm.baseDate(moment(date).format(D_FORMAT));
					}
				}
			});

			vm.baseDate
				.subscribe((date: string) => {
					if (date && date.match(/^\d{6}$/)) {
						data.baseDate(moment(date, D_FORMAT).toDate());
					}
				});

			vm.style = ko.computed({
				read: () => {
					const width = ko.unwrap(data.width);

					return (`.scheduler .calendar-container .calendar .week .day { width: ${width || 85}px !important; }`)
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
					const startDate = ko.unwrap(vm.startDate);
					const schedules = ko.unwrap(data.schedules);
					const locale = moment.locale();

					if (!schedules.length) {
						return [];
					}

					moment.updateLocale(locale, {
						week: {
							dow: ko.unwrap(startDate),
							doy: 0
						}
					});

					const [begin] = schedules;
					const [finsh] = schedules.slice(-1);

					const initRange = (start: moment.Moment, diff: number) => _.range(0, Math.abs(diff), 1)
						.map((d) => start.clone().add(d, 'day'))
						.map((d) => ({
							date: d.toDate(),
							inRange: false,
							startDate: false,
							data: ko.observable({})
						}));

					const start1 = moment(begin.date).startOf('week');
					const start2 = moment(finsh.date).add(1, 'day');
					const diff1 = start1.diff(begin.date, 'day');

					const befores = initRange(start1, diff1);
					const afters = initRange(start2, 42 - befores.length - schedules.length);

					moment.updateLocale(locale, {
						week: {
							dow: 0,
							doy: 0
						}
					});

					vm.$nextTick(() => $(vm.$el).find('[data-bind]').removeAttr('data-bind'));

					return _.chunk([...befores, ...schedules, ...afters], 7);
				},
				owner: vm
			});

			vm.titleDays = ko.computed({
				read: () => {
					const scheds = ko.unwrap(vm.schedules);
					const dates = (scheds[0] || []).map((m) => m.date);

					return dates;
				},
				owner: vm
			});

			vm.showBaseDate = ko.computed({
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
							return moment(baseDate.begin).isSame(d.startOf('day'));
						}

						return false;
					};
					const initRange = (diff: number) => {
						const daysOfMonth = _.range(0, diff + 1, 1)
							.map((d) => start.clone().add(d, 'day'))
							.map((d) => ({
								date: d.toDate(),
								inRange: true,
								startDate: isStartDate(d),
								data: ko.observable({})
							}));

						data.schedules.reset(daysOfMonth);
					};

					if (_.isDate(baseDate)) {
						if (!start.isSame(first.date, 'month')) {
							const start = moment(baseDate).startOf('month').startOf('day');
							const end = moment(baseDate).endOf('month').endOf('day');
							const diff = start.diff(end, 'day');

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

		mounted() {
			const vm = this;
		}
	}

	interface DateOption {
		id: number;
		title: string;
	}

	export interface DayData {
		date: Date;
		data: KnockoutObservable<any>;
		inRange: boolean;
		startDate: boolean;
	}

	export interface DateRange {
		begin: Date;
		finish: Date;
	}

	export interface Parameter {
		width: KnockoutObservable<number>;
		baseDate: KnockoutObservable<Date | DateRange>;
		schedules: MementoObservableArray<DayData>;
		clickCell: (target: 'title' | 'event' | 'holyday' | 'body', day: DayData) => void;
	}
}