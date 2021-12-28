/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmp001.c {
	const template = `
		<div id="functions-area">
			<a class="goback" data-bind="ntsLinkButton: { jump: '/view/kmp/001/h/index.xhtml' },text: $i18n('KMP001_100')"></a>
			<button class="proceed" data-bind= "text: $i18n('KMP001_5'), click: addStampCard"></button>
		</div>
		<div class="view-kmp">
			<div class="list-component float-left">
				<div class="layout-date">
					<table>
						<tbody>
							<tr>
								<td>
									<div data-bind="ntsFormLabel: { text: $i18n('KMP001_14'), required: true }"></div>
								</td>
								<td>
									<div 
										tabindex="1"
										id="daterangepicker"
										style="margin-left: 20px"
										data-bind="
											ntsDateRangePicker: { 
												require: true,
												showNextPrevious: true,
												value: dateRange
											}"/>		
								</td>
								<td>
									<button 
										style="margin-left: 20px" 
										class="caret-bottom" 
										data-bind= "
											text: $i18n('KMP001_15'), 
											click: getAllData">
									</button>
								</td>
							<tr>
						</tbody>
					</table>
				</div>
				<div class="caret-right caret-background bg-green" style="padding: 10px;">
					<div style="width: 540px"
						data-bind="ntsSearchBox: {
							label: $i18n('KMP001_22'),
							searchMode: 'filter',
							targetKey: 'stampNumber',
							comId: 'card-list', 
							items: items,
							selected: stampNumber,
							selectedKey: 'stampNumber',
							fields: ['stampNumber', 'stampAtr'],
							mode: 'igGrid'
							}"></div>
					<div>
						<table id="card-list" 
							data-bind="ntsGridList: {
								height: 310,
								dataSource: items,
								optionsValue: 'stampNumber',
								columns: [
						            { headerText: $i18n('KMP001_22'), prop: 'stampNumber', width: 180 },
						            { headerText: $i18n('KMP001_27'), prop: 'infoLocation', width: 160 },
						            { headerText: $i18n('KMP001_28'), prop: 'stampAtr', width: 80 },
			 						{ headerText: $i18n('KMP001_29'), prop: 'stampDatetime', width: 180 }
						        ],
								multiple: false,
								enable: true,
								value: model.stampNumber
							}">
						</table>
					</div>
				</div>
			</div>
			<div class="model-component float-left ">
				<table class="table_content">
					<tbody>
						<tr>
							<td class="label-column-left">
								<div id="td-bottom">
									<div style="border: 0; font-size: 18px" data-bind="ntsFormLabel: { text: $i18n('KMP001_22')}"></div>
								</div>
							</td>
							<td class="data">
								<div id="td-bottom" data-bind="text: model.stampNumber"></div>
							</td>
						</tr>
						<tr>
							<td class="label-column-left">
								<div id="td-bottom">
									<div data-bind="ntsFormLabel: { text: $i18n('KMP001_9'), required: true}"></div>
									<button data-bind="text: $i18n('KMP001_26'), click: openDialogCDL009a">Normal</button>
								</div>
							</td>
							<td class="data">
								<div id="td-bottom" data-bind="text: employee.employeeCode"></div>
								<div style="margin-left: 15px; margin-bottom:15px;" data-bind="text: employee.businessName"></div>
							</td>
						</tr>
						<tr>
							<td class="label-column-left">
								<div id="td-bottom">
									<div style="border: 0; font-size: 18px" data-bind="ntsFormLabel: { text: $i18n('KMP001_20') }"></div>
								</div>
							</td>
							<td class="data">
								<div id="td-bottom" data-bind="text: employee.entryDate"></div>
							</td>
						</tr>
						<tr>
							<td class="label-column-left">
								<div id="td-bottom">
									<div style="border: 0; font-size: 18px" data-bind="ntsFormLabel: { text: $i18n('KMP001_21') }"></div>
								</div>
							</td>
							<td class="data">
								<div id="td-bottom" data-bind="text: employee.retiredDate"></div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	`;

	interface Params {

	}

	interface DateRange {
		startDate?: string;
		endDate?: string;
	}

	const KMP001C_API = {
		GET_STAMP_CARD: 'screen/pointCardNumber/getAllCardUnregister/',
		GET_INFO_EMPLOYEE: 'screen/pointCardNumber/getEmployeeInformationViewC/',
		ADD_STAMP_CARD: 'at/record/register-stamp-card/view-c/save'
	};


	const DIALOG = {
		A: '/view/cdl/009/a/index.xhtml'
	};

	const DATE_FORMAT = 'YYYY/MM/DD';

	@component({
		name: 'view-c',
		template
	})
	export class ViewCComponent extends ko.ViewModel {

		public params!: Params;

		public items: KnockoutObservableArray<IStampCardC> = ko.observableArray([]);
		public stampNumber: KnockoutObservable<string> = ko.observable('');
		public model: StampCardC = new StampCardC();
		public employee: EmployeeVIewC = new EmployeeVIewC();

		public dateRange: KnockoutObservable<DateRange> = ko.observable({});

		created(params: Params) {
			const vm = this;
			vm.params = params;
			const format = DATE_FORMAT,
				endDate = moment().format(format),
				startDate = moment().subtract(3, 'month').format(format);

			vm.dateRange({ startDate, endDate });

			vm.reloadData(0);

			vm.employee.employeeId
				.subscribe((c: string) => {

					if (c != '') {
						vm.$ajax(KMP001C_API.GET_INFO_EMPLOYEE + ko.toJS(c))
							.then((data: IEmployeeVIewC[]) => {
								
								if(moment(data.retiredDate).format(DATE_FORMAT) === "9999/12/31"){
									data.retiredDate = null;
								}
								
								vm.employee.update(ko.toJS(data));
							})
					}
				})
		}

		getAllData() {
			const vm = this;

			vm.reloadData(0);
		}

		reloadData(selectedIndex: number = 0) {
			const vm = this;
			const { startDate, endDate } = ko.toJS(vm.dateRange);

			vm.model.clear();
			if (startDate != null && endDate != null) {
				vm.$blockui("invisible");

				const start = moment.utc(startDate, DATE_FORMAT).format("YYYY-MM-DD");
				const end = moment.utc(endDate, DATE_FORMAT).format("YYYY-MM-DD");

				vm.$ajax(KMP001C_API.GET_STAMP_CARD + start + "/" + end)
					.then((data: IStampCardC[]) => {
						// convert string to date format
						_.each(data, (d) => {
							d.stampDatetime = d.stampDatetime.replace(/T/, ' ').replace(/Z/, '');
						});
					
						vm.items(data);

						if (selectedIndex >= 0) {
							const record = data[selectedIndex || 0];

							if (record) {
								vm.model.stampNumber(record.stampNumber);
							}
						}
					})
					.always(() => {
						vm.$blockui("clear");
					});
			}
		}

		openDialogCDL009a() {
			const vm = this;
			const params = { selectedIds: [], isMultiple: false, baseDate: new Date(), target: 1 };

			vm.$window
				.storage('CDL009Params', params)
				.then(() => vm.$window.modal('com', DIALOG.A))
				.then(() => vm.$window.storage('CDL009Output'))
				.then((data: string | string[]) => {
					if (data != '') {
						vm.employee.employeeId(ko.toJS(data))
					}
				});
		}

		addStampCard() {
			const vm = this,
				command = { employeeId: ko.toJS(vm.employee.employeeId), cardNumber: ko.toJS(vm.model.stampNumber) },
				oldIndex = _.map(ko.unwrap(vm.items), m => m.stampNumber).indexOf(command.cardNumber),
				newIndex = oldIndex == ko.unwrap(vm.items).length - 1 ? oldIndex - 1 : oldIndex;

			if (ko.toJS(vm.employee.employeeId) == '' || ko.toJS(vm.model.stampNumber) == '' || ko.toJS(vm.employee.employeeId) == undefined ) {
				vm.$dialog.info({ messageId: "Msg_1680" });
			} else {
				vm.$blockui("invisible");

				vm.$ajax(KMP001C_API.ADD_STAMP_CARD, command)
					.then(() => vm.$dialog.info({ messageId: "Msg_15" }))
					.then(() => vm.reloadData(newIndex))
					.then(() => vm.employee.clear())
					.then(() => vm.$blockui("clear"));
			}
		}
	}

	export interface IStampCardC {
		stampNumber: string;
		infoLocation: string;
		stampAtr: string;
		stampDatetime: string;
	}

	export class StampCardC {
		stampNumber: KnockoutObservable<string> = ko.observable('');
		infoLocation: KnockoutObservable<string> = ko.observable('');
		stampAtr: KnockoutObservable<string> = ko.observable('');
		stampDatetime: KnockoutObservable<string> = ko.observable('');

		constructer(params?: IStampCardC) {
			const self = this;

			if (params) {
				if (params.stampNumber) {
					self.stampNumber(params.stampNumber);
				}
				self.update(params);
			}
		}

		update(params: IStampCardC) {
			const self = this;

			self.infoLocation(params.infoLocation);
			self.stampAtr(params.stampAtr);
			self.stampDatetime(params.stampDatetime);
		}

		clear() {
			const self = this;

			self.stampNumber('');
		}
	}

	interface IEmployeeVIewC {
		businessName: string;
		employeeCode: string;
		employeeId: string;
		entryDate: Date | null;
		retiredDate: Date | null;
	}

	class EmployeeVIewC {
		businessName: KnockoutObservable<string> = ko.observable('');
		employeeCode: KnockoutObservable<string> = ko.observable('');
		employeeId: KnockoutObservable<string> = ko.observable('');
		entryDate: KnockoutObservable<Date | null> = ko.observable(null);
		retiredDate: KnockoutObservable<Date | null> = ko.observable(null);

		constructor(params?: IEmployeeVIewC) {
			const seft = this;

			if (params) {
				seft.employeeId(params.employeeId);
				seft.update(params);
			}
		}

		update(params: IEmployeeVIewC) {
			const seft = this;

			seft.employeeCode(params.employeeCode);
			seft.businessName(params.businessName);
			seft.entryDate(params.entryDate);
			seft.retiredDate(params.retiredDate);
		}

		clear() {
			const self = this;
			self.employeeId('');
			self.update({ employeeCode: '', businessName: '', employeeId: '', entryDate: null, retiredDate: null })
		}
	}
}