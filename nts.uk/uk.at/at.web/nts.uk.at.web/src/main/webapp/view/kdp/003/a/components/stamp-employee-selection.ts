/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.kdp003.a {
	const stampEmployeeSelectionTemplate = `
		<div data-bind="ntsDatePicker: { value: $component.options.baseDate }"></div>
		<div class="button-group-filter" data-bind="foreach: buttons">
			<button class="small filter" data-bind="
					i18n: text, 
					css: { 'extend': width === 2, selected: ko.toJS($component.button) === text },
					click: function() { $component.button(text); }
				"></button>
		</div>
		<div class="list-group">
			<button class="check-out-of-list" data-bind="
					i18n: 'KDP003_112',
					click: loginEmployeeNotInList,
					css: { 
						'active': ko.toJS(options.selectedId) === null
					}"></button>
			<div class="list-employee">
				<table id="grid-employee"></table>
			</div>
		</div>
	`;


	const ROW_HEIGHT = 45;
	const MIN_HEIGHT = ROW_HEIGHT * 7;
	const COMPONENT_NAME = 'stamp-employee-selection';

	enum CHARACTER {
		ALL = '全員',
		A = 'ア',
		KA = 'カ',
		SA = 'サ',
		TA = 'タ',
		NA = 'ナ',
		HA = 'ハ',
		MA = 'マ',
		YA = 'ヤ',
		RA = 'ラ',
		WA = 'ワ'
	};

	const CHARACTERS = {
		A: ['ア', 'イ', 'ウ', 'エ', 'オ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ヴ'],
		KA: ['カ', 'キ', 'ク', 'ケ', 'コ', 'ガ', 'ギ', 'グ', 'ゲ', 'ゴ', 'ヵ', 'ヶ'],
		SA: ['サ', 'シ', 'ス', 'セ', 'ソ', 'ザ', 'ジ', 'ズ', 'ゼ', 'ゾ'],
		TA: ['タ', 'チ', 'ツ', 'テ', 'ト', 'ダ', 'ヂ', 'ヅ', 'デ', 'ド', 'ッ'],
		NA: ['ナ', 'ニ', 'ヌ', 'ネ', 'ノ'],
		HA: ['ハ', 'ヒ', 'フ', 'ヘ', 'ホ', 'バ', 'ビ', 'ブ', 'ベ', 'ボ', 'パ', 'ピ', 'プ', 'ペ', 'ポ'],
		MA: ['マ', 'ミ', 'ム', 'メ', 'モ'],
		YA: ['ヤ', 'ユ', 'ヨ', 'ャ', 'ュ', 'ョ'],
		RA: ['ラ', 'リ', 'ル', 'レ', 'ロ'],
		WA: ['ワ', 'ヲ', 'ン', 'ヮ']
	};

	@handler({
		bindingName: COMPONENT_NAME,
		validatable: true,
		virtual: false
	})
	export class EmployeeComponentBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => any, __ab: KnockoutAllBindingsAccessor, ___vm: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext) {
			const name = COMPONENT_NAME;
			const params = valueAccessor();

			ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);

			return { controlsDescendantBindings: true };
		}
	}

	@component({
		name: COMPONENT_NAME,
		template: stampEmployeeSelectionTemplate
	})
	export class StampEmployeeSelectionComponent extends ko.ViewModel {
		$grid!: JQuery;

		button: KnockoutObservable<string> = ko.observable('KDP003_111');

		buttons: KnockoutObservableArray<Button> = ko.observableArray([]);

		constructor(private options: EmployeeListParam) {
			super();

			const vm = this;

			if (!vm.options) {
				vm.options = {
					employees: ko.observableArray([]),
					selectedId: ko.observable(undefined),
					nameSelectArt: ko.observable(true),
					baseDate: ko.observable(null)
				};
			} else {
				if (!_.has(vm.options, 'employees')) {
					vm.options.employees = ko.observableArray([]);
				}

				if (!_.has(vm.options, 'selectedId')) {
					vm.options.selectedId = ko.observable(undefined);
				}

				if (!_.has(vm.options, 'baseDate')) {
					vm.options.baseDate = ko.observable(null);
				}
			}
		}


		/**
		 * order list employee by name and code
		 */
		private orderedData(data: Employee[]) {
			// return _.orderBy(data, ['employeeName', 'employeeCode'], ['asc', 'asc'])
			return _.orderBy(data, ['employeeNameKana', 'employeeCode'], ['asc','asc']);
		}

		created() {
			const vm = this;

			// initial list button filter
			const buttons: Button[] = _.range(1, 12, 1)
				.map(m => ({
					width: m === 11 ? 2 : 1,
					text: `KDP003_1${_.padStart(`${m}`, 2, '0')}`
				}));

			vm.buttons(buttons);

			ko.computed({
				read: () => {
					const $grid = vm.$grid;
					const type = ko.unwrap(vm.button);
					const dataSource: Employee[] = ko.unwrap(vm.options.employees);

					const filtereds: Employee[] = [];
					const doFilter = (codes: string[]) => {
						return _.filter(dataSource, (record: Employee) => codes.indexOf(record.employeeNameKana[0]) > -1);
					};

					if ($grid && $grid.data('igGrid')) {
						switch (vm.$i18n(type)) {
							default:
							case CHARACTER.ALL:
								filtereds.push(...dataSource);
								break;
							case CHARACTER.A:
								filtereds.push(...doFilter(CHARACTERS.A));
								break;
							case CHARACTER.KA:
								filtereds.push(...doFilter(CHARACTERS.KA));
								break;
							case CHARACTER.SA:
								filtereds.push(...doFilter(CHARACTERS.SA));
								break;
							case CHARACTER.TA:
								filtereds.push(...doFilter(CHARACTERS.TA));
								break;
							case CHARACTER.NA:
								filtereds.push(...doFilter(CHARACTERS.NA));
								break;
							case CHARACTER.HA:
								filtereds.push(...doFilter(CHARACTERS.HA));
								break;
							case CHARACTER.MA:
								filtereds.push(...doFilter(CHARACTERS.MA));
								break;
							case CHARACTER.YA:
								filtereds.push(...doFilter(CHARACTERS.YA));
								break;
							case CHARACTER.RA:
								filtereds.push(...doFilter(CHARACTERS.RA));
								break;
							case CHARACTER.WA:
								filtereds.push(...doFilter(CHARACTERS.WA));
								break;
						}

						vm.options.selectedId(undefined);
						$grid.igGridSelection('clearSelection');

						$grid.igGrid('option', 'dataSource', vm.orderedData(filtereds));
					}
				}
			});
		}

		mounted() {
			const vm = this;
			const $grid = vm.$grid = $(vm.$el).find('#grid-employee');

			$(vm.$el).attr('id', 'stamp-employee-selection');

			$(vm.$el).find('.nts-datepicker-wrapper input').attr('readonly', 'readonly');

			$grid
				.igGrid({
					showHeader: false,
					columns: [
						{ headerText: "", key: "employeeId", dataType: "string", hidden: true },
						{ headerText: "", key: "employeeCode", width: '80px', dataType: "string" },
						{ headerText: "", key: "employeeName", dataType: "string" }
					],
					features: [
						{
							name: "Tooltips",
							columnSettings: [
								{ columnKey: "employeeId", allowTooltips: false },
								{ columnKey: "employeeCode", allowTooltips: false },
								{ columnKey: "employeeName", allowTooltips: true }
							],
							visibility: "always",
							showDelay: 1000,
							hideDelay: 500
						},
						{
							name: "Selection",
							mode: "row",
							rowSelectionChanged: function (__: any, ui: any) {
								const { index } = ui.row;
								const dataSources: Employee[] = $grid.igGrid('option', 'dataSource');

								if (dataSources[index]) {
									vm.options.selectedId(dataSources[index].employeeId);
								} else {
									vm.options.selectedId(undefined);

									if ($grid && $grid.data('igGrid')) {
										$grid.igGridSelection('clearSelection');
									}
								}
							}
						}
					],
					width: "240px",
					height: `${MIN_HEIGHT}px`,
					dataSource: vm.orderedData(ko.toJS(vm.options.employees))
				});

			/**
			 * trigger resize and show row on grid by window height
			 */
			// $(window)
			// 	.on('resize', () => {
			// 		const grid = $grid.get(0);

			// 		if (grid && $grid.data('igGrid')) {
			// 			const top = grid.getBoundingClientRect().top;
			// 			const maxHeight = window.innerHeight - top - 25;

			// 			$grid.igGrid('option', 'height', `${Math.max(MIN_HEIGHT, maxHeight)}px`);
			// 		}
			// 	})
			// 	.trigger('resize');
			
			var paramSize = 0;
			
			setInterval(function () {

				const grid = $grid.get(0);

				if (grid && $grid.data('igGrid')) {
					
					var bottomMasterWrapper = $('#contents-area')[0].getBoundingClientRect().bottom;
			        var topGridEmployee = $('.list-employee')[0].getBoundingClientRect().top;
			        var param = bottomMasterWrapper - topGridEmployee - 20;
					if (param < 45) param = 45;

					if (paramSize !== param) {
						paramSize = param;
						$grid.igGrid('option', 'height', param + 'px');
						$grid.data("height", param + 'px');
					}
				}
			}, 100);

		}

		loginEmployeeNotInList() {
			const vm = this;
			const $grid = vm.$grid;

			vm.options.selectedId(null);

			if ($grid && $grid.data('igGrid')) {
				$grid.igGridSelection('clearSelection');
			}
		}
	}

	interface Button {
		text: string;
		width: 1 | 2;
	}

	export interface Employee {
		employeeId: string;
		employeeCode: string;
		employeeName: string;
		employeeNameKana: string;
	}

	export interface EmployeeListData {
		employees: Employee[];
		/**
		* employeeId
		* string: selected
		* null: select to 一覧にない社員で打刻する
		* undefined: not select
		*/
		selectedId: string | null | undefined;
		nameSelectArt: boolean;
		baseDate: Date;
	}

	export interface EmployeeListParam {
		employees: KnockoutObservableArray<Employee>;
		selectedId: KnockoutObservable<string | null | undefined>;
		nameSelectArt: KnockoutObservable<boolean>;
		baseDate: KnockoutObservable<Date | null>;
	}
}