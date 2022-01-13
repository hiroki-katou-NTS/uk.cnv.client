/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.a {
	import getText = nts.uk.resource.getText;
	
	const template = `
    <div class="workhour-info">
        <span data-bind="i18n: 'KSU002_33'"></span>
        <span data-bind="i18n: 'KSU002_7'"></span>
        <span data-bind="text: monthlyEstimateTime"></span>
        <span style="margin-left: 15px;" data-bind="i18n: 'KSU002_34'"></span>
        <span data-bind="i18n: 'KSU002_7'"></span>
        <span data-bind="text: weeklyEstimateTime"></span>
    </div>
    <table>
        <tbody>
            <tr>
                <th colspan="2" data-bind="i18n: 'KSU002_14'"></th>
            </tr>
            <tr>
                <td colspan="2" data-bind="text: compareWorkingHours"></td>
            </tr>
            <tr>
                <th data-bind="i18n: 'KSU002_18'"></th>
                <th data-bind="i18n: 'KSU002_19'"></th>
            </tr>
            <tr>
                <td data-bind="text: workingDays"></td>
                <td data-bind="text: holidayDay"></td>
            </tr>
        </tbody>
    </table>
    <style type="text/css" rel="stylesheet">
        .work-hour-info {
            margin-top: 10px;
        }
        .work-hour-info table {
            margin-top: 10px;
            width: 400px;
        }
        .work-hour-info table,
        .work-hour-info table th,
        .work-hour-info table td {
            border: 1px solid #808080;
        }
        .work-hour-info table th,
        .work-hour-info table td {
            width: 50%;
            padding: 5px 7px;
        }
		.work-hour-info table td {
			height: 33px;
		}
        .work-hour-info table th {
            background-color: #CFF1A5;
        }
	</style>`;

	const COMPONENT_NAME = 'work-hour';

	const API = {
		WTYPE: '/screen/ksu/ksu002/getWorkType'
	};

	@handler({
		bindingName: COMPONENT_NAME,
		validatable: true,
		virtual: false
	})
	export class WorkHourOfMonthComponentBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
			const name = COMPONENT_NAME;


			const params = { viewModel };
			const component = { name, params };

			element.classList.add('cf');
			element.classList.add('work-hour-info');
			element.removeAttribute('tabindex');

			ko.applyBindingsToNode(element, { component }, bindingContext);

			return { controlsDescendantBindings: true };
		}
	}

	@component({
		name: COMPONENT_NAME,
		template
	})
	export class WorkHourOfMonthComponent extends ko.ViewModel {
		monthlyEstimateTime: KnockoutObservable<string> = ko.observable('');
		weeklyEstimateTime: KnockoutObservable<string> = ko.observable('');
		compareWorkingHours: KnockoutObservable<string> = ko.observable('');
		workingDays: KnockoutObservable<string> = ko.observable('');
		holidayDay: KnockoutObservable<string> = ko.observable('');
		constructor(data: any) {
			super();
			let self = this;
			self.compareWorkingHours('');
			data.viewModel.legalworkinghours.subscribe((v: any) => {
				if(v){
					let {monthlyEstimateTime, weeklyEstimateTime} = v;
					self.monthlyEstimateTime(converNumberToTime(monthlyEstimateTime));
					self.weeklyEstimateTime(converNumberToTime(weeklyEstimateTime));
				}else{
					self.monthlyEstimateTime('');
					self.weeklyEstimateTime('');
				}
			});
			data.viewModel.plansResultsData.subscribe((v: any) => {
				if(v){
					let {workingHoursMonth, numberWorkingDaysCurrentMonth, numberHolidaysCurrentMonth} = v;
					self.compareWorkingHours(converNumberToTime(workingHoursMonth) + ' '+ getText('KSU002_15') + converNumberToTime(workingHoursMonth - data.viewModel.legalworkinghours().monthlyEstimateTime) + getText('KSU002_16'));
					self.workingDays(numberWorkingDaysCurrentMonth);
					self.holidayDay(numberHolidaysCurrentMonth);
				}else{
					self.compareWorkingHours('');
					self.workingDays('');
					self.holidayDay('');
				}
			});
		}
	}

	interface Parameter {
	} 
}