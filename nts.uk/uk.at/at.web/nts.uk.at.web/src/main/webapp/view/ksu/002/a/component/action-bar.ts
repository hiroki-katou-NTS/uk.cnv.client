/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.a {
	import k = nts.uk.ui.at.kcp013.shared;

	const template = `
	<div class="btn-action">
		<div class="cf">
			<button class="small btn-copy" data-bind="
					i18n: 'KSU002_10',
					css: {
						active: ko.unwrap($component.data.mode) === 'copy'
					},
					timeClick: -1,
					attr: {
						tabindex: $component.data.tabIndex
					},
					click: function() { $component.data.mode('copy'); }
				"></button>
			<button class="small btn-edit" data-bind="
					i18n: 'KSU002_11',
					css: {
						active: ko.unwrap($component.data.mode) === 'edit'
					},
					timeClick: -1,
					attr: {
						tabindex: $component.data.tabIndex
					},
					click: function() { $component.data.mode('edit'); }
				"></button>
		</div>
		<div class="cf">
			<button class="small btn-undo" data-bind="
					icon: 44,
					enable: $component.data.clickable.undo,
					timeClick: -1,
					click: function() { $component.data.clickBtn.apply($vm, ['undo']); },
					attr: {
						tabindex: $component.data.tabIndex
					}
				"></button>
			<button class="small btn-redo" data-bind="
					icon: 154,
					enable: $component.data.clickable.redo,
					timeClick: -1,
					click: function() { $component.data.clickBtn.apply($vm, ['redo']); },
					attr: {
						tabindex: $component.data.tabIndex
					}
				"></button>
			<button class="small btn-help" data-bind="
					i18n: 'KSU002_27',
					attr: {
						tabindex: $component.data.tabIndex
					}
				"></button>
		</div>
	</div>
	<div class="component-action">
		<div>
			<label data-bind="i18n: 'KSU002_12'"></label>
	        <div data-bind="
				attr: {
					tabindex: $component.data.tabIndex
				},
				ntsComboBox: {
					width: '430px',
					name: $component.$i18n('KSU002_12'),
					value: $component.workTypeData.selected,
					options: $component.workTypeData.dataSources,
					optionsValue: 'workTypeCode',
					enable: ko.unwrap($component.data.mode) === 'copy',
					editable: false,
					selectFirstIfNull: true,
					columns: [
						{ prop: 'workTypeCode', length: 4 },
						{ prop: 'name', length: 10 },
						{ prop: 'memo', length: 10 },
					]
				}"></div>
		</div>
		<div>
			<label data-bind="i18n: 'KSU002_13'"></label>
			<div data-bind="
					kcp013: $component.workTimeData.selected,
					dataSources: $component.workTimeData.dataSources,
					filter: ko.observable(true),
					show-mode: ko.observable(0),
					disabled: ko.computed(function() { return ko.unwrap($component.data.mode) !== 'copy' }),
					tabindex: $component.data.tabIndex,
					width: 520,
					workplace-id: $component.data.workplaceId
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
			width: 24px;
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

			ko.applyBindingsToNode(element, { component }, bindingContext);

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
			dataSources: KnockoutObservableArray<WorkType>;
			currentItem: KnockoutComputed<WorkType | null>;
		};

		workTimeData!: {
			selected: KnockoutObservable<string>;
			dataSources: KnockoutObservableArray<k.WorkTimeModel>;
			currentItem: KnockoutComputed<k.WorkTimeModel | null>;
		};

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
					workplaceId: ko.observable(''),
					selected: ko.observable(null)
				};
			}

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
				vm.data.workplaceId = ko.observable('');
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
			const dataSourcesWtype: KnockoutObservableArray<WorkType> = ko.observableArray([]);

			vm.workTypeData = {
				selected: selectedWtype,
				dataSources: dataSourcesWtype,
				currentItem: ko.computed({
					read: () => {
						const sl = ko.unwrap(selectedWtype);
						const ds = ko.unwrap(dataSourcesWtype);

						return _.find(ds, f => f.workTypeCode === sl);
					},
					owner: vm
				})
			};

			vm.workTimeData = {
				selected: selectedWtime,
				dataSources: dataSourcesWtime,
				currentItem: ko.computed({
					read: () => {
						const sl = ko.unwrap(selectedWtime);
						const ds = ko.unwrap(dataSourcesWtime);

						return _.find(ds, f => f.code === sl);
					},
					owner: vm
				})
			};

			ko.computed({
				read: () => {
					const { data, } = vm;
					const d = ko.unwrap(data.mode) === 'copy';

					const wtypec = ko.unwrap(selectedWtype);
					const wtyped = ko.unwrap(dataSourcesWtype);

					const wtimec = ko.unwrap(selectedWtime);
					const wtimed = ko.unwrap(dataSourcesWtime);

					if (!d) {
						data.selected(null);
					} else {
						const wtime = _.find(wtimed, w => w.code === wtimec);
						const wtype = _.find(wtyped, w => w.workTypeCode === wtypec);

						data.selected({
							wtype: {
								code: wtype ? wtype.workTypeCode : '',
								name: wtype ? wtype.name : '',
							},
							wtime: {
								code: wtime ? wtime.code : '',
								name: wtime ? wtime.name : '',
								value: {
									begin: wtime ? wtime.tzStart1 : null,
									finish: wtime ? wtime.tzEnd1 : null
								}
							}
						});
					}
				},
				owner: vm
			});
		}

		created() {
			const vm = this;

			vm.workTimeData.currentItem
				.subscribe(c => {
					console.log(c);
				});

			vm.workTypeData.currentItem
				.subscribe(c => {
					console.log(c);
				});

			vm.$ajax('at', API.WTYPE)
				.then((response: WorkType[]) => {
					vm.workTypeData.dataSources(response.map((m) => ({ ...m, memo: vm.$i18n(m.memo) })));
				});
		}

		mounted() {
			const vm = this;

			$(vm.$el).find('[data-bind]').removeAttr('data-bind');
		}

		workHourSelect(input: any[], source: any[]) {
			console.log(input, source);
		}
	}

	export type EDIT_MODE = 'edit' | 'copy';

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

	interface WorkType {
		memo: string;
		name: string;
		workTypeCode: string;
	}

	export interface WorkData {
		wtype: {
			code: string;
			name: string;
		};
		wtime: {
			code: string;
			name: string;
			value: {
				begin: number | null;
				finish: number | null;
			}
		}
	}
}