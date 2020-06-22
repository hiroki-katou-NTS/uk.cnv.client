/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

const template = `
<div class="sidebar-content-header">
	<span class="title" data-bind= "text: $i18n('KMP001_3')"></span>
	<button class="proceed" data-bind= "text: $i18n('KMP001_5')"></button>
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
							<div style="margin-left: 20px" id="daterangepicker" tabindex="1" data-bind="ntsDateRangePicker: { 
								enable: enable, 
								showNextPrevious: true,
								value: dateValue, 
								maxRange: 'oneMonth'}"/>		
						</td>
						<td>
							<button style="margin-left: 20px" class="caret-bottom" data-bind= "text: $i18n('KMP001_15')"'></button>
						</td>
					<tr>
				</tbody>
			</table>
		</div>
		<div class="caret-right caret-background bg-green" style="padding: 10px;">
			<div style="width: 460px"
				data-bind="ntsSearchBox: {
					label: $i18n('KMP001_22'),
					searchMode: 'filter',
					targetKey: 'code',
					comId: 'card-list', 
					items: items,
					selected: currentCode,
					selectedKey: 'code',
					fields: ['name', 'code','code1'],
					mode: 'igGrid'
					}"></div>
			<div>
				<table id="card-list" 
					data-bind="ntsGridList: {
						height: 300,
						options: items,
						optionsValue: 'code',
						columns: [
				            { headerText: $i18n('KMP001_22'), prop: 'code', width: 150 },
				            { headerText: $i18n('KMP001_27'), prop: 'code1', width: 100 },
				            { headerText: $i18n('KMP001_28'), prop: 'name', width: 80 },
	 						{ headerText: $i18n('KMP001_29'), prop: 'name', width: 130 }
				        ],
						multiple: false,
						enable: true,
						value: currentCode
					}">
				</table>
			</div>
		</div>
	</div>
	<div class="model-component float-left " id= "info-employee">
		<table>
			<tbody>
				<tr>
					<td class="label-column-left">
						<div id="td-bottom">
							<div style="border: 0; font-size: 18px" data-bind="ntsFormLabel: { text: $i18n('KMP001_22') }"></div>
						</div>
					</td>
					<td class="data">
						<div id="td-bottom">0000000000004</div>
					</td>
				</tr>
				<tr>
					<td class="label-column-left">
						<div id="td-bottom">
							<div data-bind="ntsFormLabel: { text: $i18n('KMP001_9'), required: true }"></div>
							<button data-bind="text: $i18n('KMP001_26')">Normal</button>
						</div>
					</td>
					<td class="data">
						<div id="td-bottom">00000002</div>
						<div style="margin-left: 15px; margin-bottom:15px;">日通　社員2</div>
					</td>
				</tr>
				<tr>
					<td class="label-column-left">
						<div id="td-bottom">
							<div style="border: 0; font-size: 18px" data-bind="ntsFormLabel: { text: $i18n('KMP001_20') }"></div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="label-column-left">
						<div id="td-bottom">
							<div style="border: 0; font-size: 18px" data-bind="ntsFormLabel: { text: $i18n('KMP001_21') }"></div>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
`;

interface Params {
	
}

@component({
	name: 'view-c',
	template
})
class ViewCComponent extends ko.ViewModel {
	public params!: Params;

	public items: KnockoutObservableArray<any> = ko.observableArray([
		{ code: '001', code1: '001',  name: 'Nittsu', startDate: '2000/01/01', endDate: '2000/01/01'},
		{ code: '002', code1: '002',  name: 'Nittsu', startDate: '2000/01/01', endDate: '2000/01/01'},
		{ code: '003', code1: '003', name: 'Nittsu', startDate: '2000/01/01', endDate: '2000/01/01' },
		{ code: '004', code1: '004', name: 'Nittsu', startDate: '2000/01/01', endDate: '2000/01/01' },
		{ code: '005', code1: '005', name: 'Nittsu', startDate: '2000/01/01', endDate: '2000/01/01' }
	]);
	public currentCode: KnockoutObservable<string> = ko.observable('');
	
		enable: KnockoutObservable<boolean>;
        required: KnockoutObservable<boolean>;
        dateValue: KnockoutObservable<any>;
        startDateString: KnockoutObservable<string>;
        endDateString: KnockoutObservable<string>;

	created(params: Params) {
		this.params = params;
		
		var self = this;
            self.enable = ko.observable(true);
            self.required = ko.observable(true);
            
            self.startDateString = ko.observable("");
            self.endDateString = ko.observable("");
            self.dateValue = ko.observable({});
            
            self.startDateString.subscribe(function(value){
                self.dateValue().startDate = value;
                self.dateValue.valueHasMutated();        
            });
            
            self.endDateString.subscribe(function(value){
                self.dateValue().endDate = value;   
                self.dateValue.valueHasMutated();      
            });
	}
}