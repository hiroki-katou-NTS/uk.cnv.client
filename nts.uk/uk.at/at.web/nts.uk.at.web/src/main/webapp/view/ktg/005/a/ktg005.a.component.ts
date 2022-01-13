module nts.uk.ui.ktg005.a {

	const REST_API = {
		startScreenA: 'screen/at/ktg/ktg005/start_screen_a',
		getOptionalWidgetDisplay: "screen/at/OptionalWidget/getOptionalWidgetDisplay"
	};

	@component({
		name: 'ktg-005-a',
		template: `
		<div class="widget-title">
			<table class="ktg005-fontsize-larger" style="width: 100%">
				<colgroup>
					<col width="auto" />
					<col width="90px" />
					<col width="30px" />
				</colgroup>
				<thead>
					<tr data-bind="with: $component.executionAppResult">
						<!-- A1_1 -->
						<th>
							<div data-bind="ntsFormLabel: { required: false, text: topPagePartName }"></div>
						</th>
						<!-- A1_2 -->
						<th class="ktg005-linklabel">
							<div data-bind="
								click: function() { $component.jumpToCmm045() },
								ntsFormLabel: { required: false, text: $component.$i18n('KTG005_1') }">
							</div>
						</th>
						<th>
							<!-- A1_4 -->
							<button class="icon ktg001-no-border" data-bind="
									click: function() { $component.openScreenB() },
									visible: employeeCharge
								">
								<i data-bind="ntsIcon: { no: 5, width: 25, height: 25 }"></i>
							</button>
						</th>
					</tr>
				</thead>
			</table>
		</div>
		<div class="ktg-005-a ktg005-fontsize ktg005-border" data-bind="widget-content: 100">
			<div data-bind="with: $component.executionAppResult" style="padding: 0px 40px 0px 30px;">
				<table style="width: 100%">
					<colgroup>
						<col width="auto" />
						<col width="180px" />
					</colgroup>
					<tbody data-bind="foreach: { data: appSettings, as: 'row' }">
						<tr>
							<td>
								<div data-bind="ntsFormLabel: { required: false, text: $component.getLabel(item) }"></div>
							</td>
							<td class="text-right" data-bind="i18n: $component.getText(item)"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<style rel="stylesheet">
			.ktg-005-a table tr {
				height: 30px !important;
			}
			.ktg-005-a .text-right {
				text-align: right;
			}
			.ktg005-fontsize div.form-label>span.text {
				font-size: 1rem !important;
				padding-left: 5px;
			}
			.ktg005-fontsize-larger div.form-label>span.text {
				font-size: 1.2rem !important;
			}
			.ktg005-linklabel div.form-label>span.text {
				color: #0D86D1;
				text-decoration: underline;
				width: 80px;
				cursor: pointer;
				font-size: 1rem !important;
			}
			.ktg005-border table tr td,
			.ktg005-border table tr th {
				border-width: 0px;
				border-bottom: 1px solid #BFBFBF;
			}
			.ktg-005-a tr:last-child td {
				border: none !important;
			}
		</style>
		`
	})
	export class KTG005AComponent extends ko.ViewModel {
		widget: string = 'KTG005A';

		executionAppResult: KnockoutObservable<IExecutionAppResult> = ko.observable({
			topPagePartName: '',
			numberApprovals: 0,
			numberNotApprovals: 0,
			numberDenials: 0,
			numberRemand: 0,
			dueDate: '0',
			deadlineSetting: false,
			appSettings: [],
			employeeCharge: false
		});


		created() {
			const vm = this;
			const { employeeId, companyId } = vm.$user;
			const topPagePartCode = $(location).attr('search').split('=')[1];

			vm.$blockui('invisibleView')
				.then(() => vm.$ajax('at', REST_API.startScreenA))
				.then((setting: IExecutionAppResult) => {
					setting.appSettings = _
						.chain(setting.appSettings)
						.sortBy(['item'])
						.filter(['displayType', 1])
						.value();

					vm.executionAppResult(setting);
				})
				.fail((error) => this.$dialog.alert({ messageId: error.messageId }))
				.always(() => this.$blockui("clearView"));
		}

		getText(itemType: ApplicationStatusWidgetItem) {
			const vm = this;
			const {
				deadlineSetting,
				dueDate,
				numberApprovals,
				numberNotApprovals,
				numberDenials,
				numberRemand
			} = vm.executionAppResult();
			const {
				ApplicationDeadlineForThisMonth,
				NumberOfApprovedCases,
				NumberOfDenial,
				NumberOfRemand,
				NumberOfUnApprovedCases
			} = ApplicationStatusWidgetItem;


			switch (itemType) {
				case NumberOfApprovedCases:
					return vm.$i18n.text("KTG005_7", [`${numberApprovals}`]);
				case NumberOfUnApprovedCases:
					return vm.$i18n.text("KTG005_7", [`${numberNotApprovals}`]);
				case NumberOfDenial:
					return vm.$i18n.text("KTG005_7", [`${numberDenials}`]);
				case NumberOfRemand:
					return vm.$i18n.text("KTG005_7", [`${numberRemand}`]);
				case ApplicationDeadlineForThisMonth:
					if (deadlineSetting === true) {
						return moment(dueDate, 'yyyy/MM/DD HH:mm:ss').format('MM/DD(dd)')
					}

					return vm.$i18n('KTG005_8');
				default:
					return '';
			}
		}

		getLabel(itemType: ApplicationStatusWidgetItem) {
			const vm = this;
			const {
				ApplicationDeadlineForThisMonth,
				NumberOfApprovedCases,
				NumberOfDenial,
				NumberOfRemand,
				NumberOfUnApprovedCases
			} = ApplicationStatusWidgetItem;

			switch (itemType) {
				case NumberOfApprovedCases:
					return vm.$i18n('KTG005_3');
				case NumberOfUnApprovedCases:
					return vm.$i18n('KTG005_4');
				case NumberOfDenial:
					return vm.$i18n('KTG005_5');
				case NumberOfRemand:
					return vm.$i18n('KTG005_2');
				case ApplicationDeadlineForThisMonth:
					return vm.$i18n('KTG005_6');
				default:
					return '';
			}
		}

		jumpToCmm045() {
			let vm = this;

			vm.$jump('at', '/view/cmm/045/a/index.xhtml?a=0');
		}

		openScreenB() {
			let vm = this;

			vm.$window
				.modal('at', '/view/ktg/005/b/index.xhtml')
				.then(() => vm.created());
		}
	}

	export interface IOptionalWidgetDisplay {
		datePeriodDto: IDatePeriodDto;
		optionalWidgetImport: IOptionalWidgetImport;
	}

	export interface IDatePeriodDto {
		endCurrentMonth: string;
		endNextMonth: string;
		strCurrentMonth: string;
		strNextMonth: string;
	}

	export interface IOptionalWidgetImport {
		height: number;
		topPageCode: string;
		topPageName: string;
		topPagePartID: string;
		widgetDisplayItemExport: Array<IWidgetDisplayItemExport>;
		width: number
	}

	export interface IWidgetDisplayItemExport {
		displayItemType: number;
		notUseAtr: number;
	}

	export interface IExecutionAppResult {
		//名称
		topPagePartName: string;
		//承認件数
		numberApprovals: number;
		//未承認件数
		numberNotApprovals: number;
		//否認件数
		numberDenials: number;
		//差し戻し件数
		numberRemand: number;
		//締め切り日									
		dueDate: string;
		//申請締切利用設定
		deadlineSetting: boolean;
		//申請状況の詳細設定
		appSettings: Array<IDetailSettingAppStatus>;
		//勤怠担当者である
		employeeCharge: boolean;
	}

	export interface IDetailSettingAppStatus {
		// 表示区分
		displayType: number;

		// 項目
		item: number;
	}

	enum ApplicationStatusWidgetItem {
		//承認された件数
		NumberOfApprovedCases = 0,
		//未承認件数
		NumberOfUnApprovedCases = 1,
		//否認された件数
		NumberOfDenial = 2,
		//差し戻し件数
		NumberOfRemand = 3,
		//今月の申請締め切り日
		ApplicationDeadlineForThisMonth = 4
	}
}