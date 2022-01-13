/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.a {
	import c = nts.uk.ui.calendar;
	import k = nts.uk.ui.at.kcp013.shared;

	const AB_API = {
		CR_WT_HD: '/screen/ksu/ksu002/correctWorkTimeHalfDay'
	};

	const template = `
	<div class="btn-action">
		<div class="cf">
			<button class="small btn-copy" data-bind="
					i18n: 'KSU002_10',
					css: {
						active: ko.unwrap($component.data.mode) === 'copy' && !!$component.data.workplaceId()
					},
					timeClick: -1,
					attr: {
						tabindex: $$tabindex
					},
					click: function() { $component.data.mode('copy'); },
					enable: !!$component.data.workplaceId()
				"></button>
			<button class="small btn-edit" data-bind="
					i18n: 'KSU002_11',
					css: {
						active: ko.unwrap($component.data.mode) === 'edit' && !!$component.data.workplaceId()
					},
					timeClick: -1,
					attr: {
						tabindex: $$tabindex
					},
					click: function() { $component.data.mode('edit'); },
					enable: !!$component.data.workplaceId()
				"></button>
		</div>
		<div class="cf">
			<button class="small btn-undo" data-bind="
					icon: 44,
					enable: $component.data.clickable.undo(),
					timeClick: -1,
					click: function() { $component.data.clickBtn.apply($vm, ['undo']); },
					attr: {
						tabindex: $$tabindex
					}
				"></button>
			<button class="small btn-redo" data-bind="
					icon: 154,
					enable: $component.data.clickable.redo(),
					timeClick: -1,
					click: function() { $component.data.clickBtn.apply($vm, ['redo']); },
					attr: {
						tabindex: $$tabindex
					}
				"></button>
			<button class="small btn-help" data-bind="
					i18n: 'KSU002_27',
					enable: false,
					attr: {
						tabindex: $$tabindex
					}
				"></button>
		</div>
	</div>
	<div class="component-action">
		<div>
			<label data-bind="i18n: 'KSU002_12'"></label>
	        <div data-bind="
				attr: {
					tabindex: $$tabindex
				},
				ntsComboBox: {
					width: 520,
					name: $component.$i18n('KSU002_12'),
					value: $component.workTypeData.selected,
					options: $component.workTypeData.dataSources,
					optionsValue: 'workTypeCode',
					enable: workTypeEnable,
					editable: false,
					selectFirstIfNull: true,
					visibleItemsCount: 10,
					columns: [
						{ prop: 'workTypeCode', length: 5 },
						{ prop: 'name', length: 12 },
						{ prop: 'memo', length: 1 },
					]
				}"></div>
		</div>
		<div>
			<label data-bind="i18n: 'KSU002_13'"></label>
			<div data-bind="
					kcp013: $component.workTimeData.selected,
					dataSources: $component.workTimeData.dataSources,
					filter: ko.observable(true),
					show-mode: ko.observable(3),
					disabled: $component.workTimeData.disabled,
					tabindex: $$tabindex,
					width: 520,
					workplace-id: $component.data.workplaceId,
					check: ko.observable(false)
				"></div>
		</div>
	</div>
	<style type="text/css" rel="stylesheet">
		.action-bar {
			margin-bottom: 5px;
			padding: 6px;
			border-radius: 5px;
			display: inline-block;
			border: 1px solid #cccccc;
			background-color: rgb(219, 238, 244);
		}
		.action-bar .btn-action {
			float: left;
		    padding-right: 15px;
			border-right: 2px solid #ccc;
		}
		.action-bar .btn-action>div:first-child {
			margin-bottom: 5px;
		}
		.action-bar .btn-action>div>button {
			float: left;
		    width: 50px;
			white-space: pre-line;
			padding: 1px 10px;
		}
		.action-bar .btn-action>div>button:not(:first-child) {
			margin-left: 5px;
		}
		.action-bar .btn-action>div:first-child>button {
			height: 40px;
		}
		.action-bar .btn-action>div:last-child>button {
			height: 29px
		}
		.action-bar .btn-action>div:last-child>button.btn-help {
			width: 30px;
			height: 24px;
			margin-top: 5px;
		}
		.action-bar .component-action {
			float: left;
			padding-left: 15px;			
		}
		.action-bar .component-action>div:first-child {
			padding-top: 4px;
			padding-bottom: 4px;
			margin-bottom: 5px;
		}
		.action-bar .component-action>div>label {
			min-width: 100px;
			line-height: 32px;
			display: block;
			float: left;
		}
		.action-bar .btn-action .btn-copy.active,
		.action-bar .btn-action .btn-edit.active {
			color: #fff;
			background-color: #007fff;
		}
		.action-bar .btn-action .btn-copy.active:focus,
		.action-bar .btn-action .btn-edit.active:focus {
			box-shadow: 0 3px rgba(0, 0, 0, 0.4);
		}
	</style>`;

	const COMPONENT_NAME = 'action-bar';

	const API = {
		WTYPE: '/screen/ksu/ksu002/getWorkType'
	};

	@handler({
		bindingName: COMPONENT_NAME,
		validatable: true,
		virtual: false
	})
	export class ActionBarComponentBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => KnockoutObservable<null | WorkData>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const name = COMPONENT_NAME;
			const selected = valueAccessor();
			const tabIndex = element.getAttribute('tabindex') || '1';
			const mode = allBindingsAccessor.get('mode');
			const clickable = allBindingsAccessor.get('clickable');
			const clickBtn = allBindingsAccessor.get('click-btn');
			const workplaceId = allBindingsAccessor.get('workplace-id');

			const params = { selected, clickable, clickBtn, tabIndex, mode, workplaceId };
			const component = { name, params };

			element.classList.add('cf');
			element.classList.add('action-bar');
			element.removeAttribute('tabindex');

			const childContext = bindingContext
				.extend({
					$$tabindex: tabIndex
				});

			ko.applyBindingsToNode(element, { component }, childContext);

			return { controlsDescendantBindings: true };
		}
	}

	@component({
		name: COMPONENT_NAME,
		template
	})
	export class ActionBarComponent extends ko.ViewModel {
		workTypeData!: {
			selected: KnockoutObservable<string>;
			dataSources: KnockoutObservableArray<WorkTypeModel>;
		};

		workTimeData!: {
			selected: KnockoutObservable<string>;
			dataSources: KnockoutObservableArray<k.WorkTimeModel>;
			disabled: KnockoutComputed<boolean>;
		};
		workTypeEnable: KnockoutObservable<boolean> = ko.observable(false);

		constructor(private data: Parameter) {
			super();

			const vm = this;
			
			if (!data) {
				vm.data = {
					tabIndex: "1",
					clickBtn: () => { },
					clickable: {
						redo: ko.computed(() => true),
						undo: ko.computed(() => true)
					},
					mode: ko.observable('copy'),
					workplaceId: ko.observable(null),
					selected: ko.observable(null)
				};
			}
			vm.data.workplaceId.subscribe((v) => {
				vm.workTypeEnable(data.mode() === 'copy' &&  v != null);
			});
			
			vm.data.mode.subscribe((v) => {
				vm.workTypeEnable(data.workplaceId() != null &&  v === 'copy');
			});

			const { selected, clickable, clickBtn, mode, workplaceId } = vm.data;

			if (selected === undefined) {
				vm.data.selected = ko.observable(null);
			}

			if (mode === undefined) {
				vm.data.mode = ko.observable('edit');
			}

			if (clickBtn === undefined) {
				vm.data.clickBtn = () => { };
			}

			if (clickable === undefined) {
				vm.data.clickable = {
					redo: ko.computed(() => true),
					undo: ko.computed(() => true)
				};
			}

			if (workplaceId === undefined) {
				vm.data.workplaceId = ko.observable(null);
			}

			const { redo, undo } = vm.data.clickable;

			if (!redo) {
				vm.data.clickable.redo = ko.computed(() => true);
			}

			if (!undo) {
				vm.data.clickable.undo = ko.computed(() => true);
			}

			const selectedWtime: KnockoutObservable<string> = ko.observable('');
			const dataSourcesWtime: KnockoutObservableArray<k.WorkTimeModel> = ko.observableArray([]);

			const selectedWtype: KnockoutObservable<string> = ko.observable('');
			const dataSourcesWtype: KnockoutObservableArray<WorkTypeModel> = ko.observableArray([]);

			vm.workTypeData = {
				selected: selectedWtype,
				dataSources: dataSourcesWtype
			};

			vm.workTimeData = {
				selected: selectedWtime,
				dataSources: dataSourcesWtime,
				disabled: ko.computed({
					read: () => {
						const wtypec = ko.unwrap(selectedWtype);
						const wtyped = ko.unwrap(dataSourcesWtype);
						const wplace = ko.unwrap(data.workplaceId);
						const wtype = _.find(wtyped, w => w.workTypeCode === wtypec);

						return vm.data.mode() === 'edit' || !wplace || !wtype || wtype.type === WORKTIME_SETTING.NOT_REQUIRED; // || wtype.style === WORK_STYLE.HOLIDAY;
					},
					owner: vm
				}),
			};

			selectedWtype
				.subscribe((wtypec) => {
					const wtyped = ko.unwrap(dataSourcesWtype);
					const wtype = _.find(wtyped, w => w.workTypeCode === wtypec);

					if (wtype && wtype.type === WORKTIME_SETTING.NOT_REQUIRED) {
						vm.$window
							.storage(c.KSU_USER_DATA)
							.then((stg: undefined | c.StorageData) => {
								if (stg) {
									if (stg.wtimec) {
										selectedWtime(stg.wtimec);
									} else {
										selectedWtime('none');
									}
								}
							});
					}
				});

			ko.computed({
				read: () => {
					const { data } = vm;
					const hwt = !vm.workTimeData.disabled();

					const mode = ko.unwrap(data.mode) === 'copy';

					const wtypec = ko.unwrap(selectedWtype);
					const wtyped = ko.unwrap(dataSourcesWtype);

					const wtimec = ko.unwrap(selectedWtime);
					const wtimed = ko.unwrap(dataSourcesWtime);

					if (!mode) {
						data.selected(null);
					} else {
						const wtime = _.find(wtimed, w => w.id === wtimec);
						const wtype = _.find(wtyped, w => w.workTypeCode === wtypec);

						if (!wtype || !wtime) {
							data.selected(null);
						} else {
							const noD = ['none', 'deferred'].indexOf(wtimec) > -1;

							$.Deferred()
								.resolve(wtype.style)
								.then((wt: WORK_STYLE) => {
									// 勤務種類の出勤休日区分 == 午前出勤系 or 午後出勤系
									if ([WORK_STYLE.MORNING, WORK_STYLE.AFTERNOON].indexOf(wt) !== -1) {
										const workTypeCode = wtypec;
										const workTimeCode = noD ? null : wtimec;
										const command = { workTimeCode, workTypeCode };

										return vm.$blockui('invisible')
											.then(() => vm.$ajax('at', AB_API.CR_WT_HD, command).done(() => vm.$blockui('clear')));
									}

									return null;
								})
								.then((wtp: WorkTimePassedValue) => {
									vm.$window
										.storage(c.KSU_USER_DATA)
										.then((v: undefined | c.StorageData) => {
											if (v === undefined) {
												vm.$window.storage(c.KSU_USER_DATA, { wtypec, wtimec });
											} else {
												const { fdate } = v;

												vm.$window.storage(c.KSU_USER_DATA, { fdate, wtypec, wtimec });
											}
										});

									data.selected({
										wtype: {
											code: wtypec,
											name: wtype.abbName,
											type: wtype.type,
											style: wtype.style
										},
										wtime: {
											code: hwt ? wtimec : null,
											name: hwt ? wtime.nameAb : null,
											value: {
												begin: !hwt || noD || wtype.style === WORK_STYLE.HOLIDAY ? null : (wtp ? wtp.startTime : wtime.tzStart1),
												finish: !hwt || noD || wtype.style === WORK_STYLE.HOLIDAY ? null : (wtp ? wtp.endTime : wtime.tzEnd1)
											}
										}
									});
								});
						}
					}
				},
				owner: vm
			});
		}

		mounted() {
			const vm = this;

			$(vm.$el).find('[data-bind]').removeAttr('data-bind');

			vm.$ajax('at', API.WTYPE)
				.then((response: WorkTypeResponse[]) => {
					vm.workTypeData
						.dataSources(response.map((m) => ({
							...m.workTypeDto,
							style: m.workStyle,
							type: m.workTimeSetting,
							memo: vm.$i18n(m.workTypeDto.memo)
						})));
				})
				.then(() => vm.$window.storage(c.KSU_USER_DATA))
				.then((stg: undefined | c.StorageData) => {
					if (stg) {
						if (stg.wtypec) {
							vm.workTypeData.selected(stg.wtypec);
						}

						if (stg.wtimec) {
							vm.workTimeData.selected(stg.wtimec);
						}
					}
				});

			$(window)
				// undo, redo by hot key
				.on('zur.keydown', (__: JQueryEventObject, evt2: JQueryEventObject) => {
					if (evt2 && evt2.ctrlKey) {
						if (evt2.keyCode === 90) {
							// $('.action-bar button.btn-undo').trigger('click');
						} else if (evt2.keyCode === 89) {
							// $('.action-bar button.btn-redo').trigger('click');
						}
					}
				})
				.on('keydown', (evt) => $(window).trigger('zur.keydown', [evt]));
		}

		destroyed() {
			$(window).off('zur.keydown');
		}
	}

	export type EDIT_MODE = 'edit' | 'copy';
	export type WTIME_CODE = 'none' | 'deferred' | string;

	type WorkTimePassedValue = { startTime: number | null; endTime: number | null; } | null;

	interface Parameter {
		selected: KnockoutObservable<null | WorkData>;
		mode: KnockoutObservable<EDIT_MODE>;
		tabIndex: string;
		clickable: {
			undo: KnockoutComputed<boolean>;
			redo: KnockoutComputed<boolean>;
		};
		clickBtn: (btn: 'undo' | 'redo') => void;
		workplaceId: KnockoutObservable<string>;
	}

	interface WorkTypeResponse {
		workStyle: WORK_STYLE;
		workTimeSetting: WORKTIME_SETTING;
		workTypeDto: WorkType;
	}

	interface WorkType {
		memo: string;
		name: string;
		abbName: string;
		workTypeCode: string;
	}

	interface WorkTypeModel extends WorkType {
		style: WORK_STYLE;
		type: WORKTIME_SETTING;
		memo: string;
	}

	export enum WORKTIME_SETTING {
		REQUIRED = 0,
		OPTIONAL = 1,
		NOT_REQUIRED = 2
	}

	export interface WorkData {
		wtype: {
			code: string;
			name: string;
			style: WORK_STYLE;
			type: WORKTIME_SETTING;
		};
		wtime: {
			code: WTIME_CODE;
			name: string;
			value: {
				begin: number | null;
				finish: number | null;
			}
		};
	}
}