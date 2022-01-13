module nts.uk.at.view.kaf005.shr.viewmodel {
	const template = `
<div class="cf" style="margin-top: -2px" data-bind="with: $parent">
	<!--A15-1 multiple overtime content-->
	<div class="cf valign-top control-group table" data-bind="if: opOvertimeAppAtr() == 3">
		<div class="cm-column cell" style="display: inline-block; width: 119px">
			<div class="lblTitle pull-left" data-bind="text: $i18n('KAF005_349'), ntsFormLabel: {required: true}"></div>
		</div>
		<div class="cell table-time" style="margin-top: 5px;">
			<div class="valign-center" id="A15_2" data-bind="style: {width: 330 + (appDispInfoStartupOutput().appDispInfoNoDateOutput.displayStandardReason == 1 ? 210 : 0) + (appDispInfoStartupOutput().appDispInfoNoDateOutput.displayAppReason == 1 ? 210 : 0) + 'px'}">
				<div style="width: 107px; display: inline-block; text-align: center;">
					<div data-bind="ntsFormLabel: {text: $i18n('KAF005_351')}"></div>
				</div>
			    <div style="width: 107px; margin-left: 35px; display: inline-block; text-align: center;">
					<div data-bind="ntsFormLabel: {text: $i18n('KAF005_352')}"></div>
				</div>
			    <div style="display: inline-block; text-align: center;"
					data-bind="if: appDispInfoStartupOutput().appDispInfoNoDateOutput.displayStandardReason == 1, 
							   style: {
							   		width: appDispInfoStartupOutput().appDispInfoNoDateOutput.displayStandardReason == 1 ? '200px' : '0',
							   		marginLeft: appDispInfoStartupOutput().appDispInfoNoDateOutput.displayStandardReason == 1 ? '10px' : '0'
							   }">
					<div data-bind="ntsFormLabel: {
										text: $i18n('KAF000_51'), 
										required: ko.computed(function() { return appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting && appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting.appLimitSetting.standardReasonRequired;})
									}"></div>		
				</div>
			    <div style="display: inline-block; text-align: center;"
			    	data-bind="if: appDispInfoStartupOutput().appDispInfoNoDateOutput.displayAppReason == 1,
			    			   style: {
							   		width: appDispInfoStartupOutput().appDispInfoNoDateOutput.displayAppReason == 1 ? '200px' : '0',
							   		marginLeft: appDispInfoStartupOutput().appDispInfoNoDateOutput.displayAppReason == 1 ? '10px' : '0'
							   }">
					<div data-bind="ntsFormLabel: {
										text: $i18n('KAF000_52'), 
										constraint: 'AppReason',
										required: ko.computed(function() { return appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting && appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting.appLimitSetting.requiredAppReason;})
									}"></div>
				</div>
            </div>	
			<div data-bind="foreach: multipleOvertimeContents">
				<div class="control-group valign-center" data-bind="style: {width: 330 + ($parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.displayStandardReason == 1 ? 210 : 0) + ($parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.displayAppReason == 1 ? 210 : 0) + 'px'}">
					<input class="inputTime-kaf005 right-content" 
							data-bind="ntsTimeWithDayEditor: {
											name: '#[KAF005_333]', 
											value: start, 
											constraint:'TimeWithDayAttr', 
											enable: ($parent.visibleModel.c7() && $parent.outputMode()),
											option: {width: '85px', timeWithDay: true}
                                        }, attr: {id: 'A15_3_' + $index()}" />
					<span data-bind="text: $parent.$i18n('KAF005_38')" style="padding-left: 10px; padding-right: 10px;"></span>
					<input class="inputTime-kaf005 right-content" 
							data-bind="ntsTimeWithDayEditor: {
											name: '#[KAF005_334]', 
											value: end, 
											constraint:'TimeWithDayAttr', 
											enable: ($parent.visibleModel.c7() && $parent.outputMode()),
											option: {width: '85px', timeWithDay: true}
                                        }, attr: {id: 'A15_5_' + $index()}" />
					</td>
					<div class="multiple-reason" style="width: 200px; margin-left: 10px;"
						data-bind="ntsComboBox: {
										name: $parent.$i18n('KAF005_87'),
										options: $parent.reasonTypeItemLst,
										optionsValue: 'appStandardReasonCD',
										optionsText: 'reasonForFixedForm',
										value: fixedReasonCode,
										columns: [{ prop: 'reasonForFixedForm', length: 20 }],
										enable: ($parent.visibleModel.c7() && $parent.outputMode()),
										required: (!!start() || !!end()) && $parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting && $parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting.appLimitSetting.standardReasonRequired 
									},
									visible: $parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.displayStandardReason == 1">
					</div>
					<input class="multiple-reason" style="margin-left: 10px;"
							data-bind="ntsTextEditor: {            
											 name: $parent.$i18n('KAF005_88'),           
											 value: appReason,            
											 constraint: 'AppReason',
											 enable: ($parent.visibleModel.c7() && $parent.outputMode()),            
											 required: ko.computed(function() { return (!!start() || !!end()) && $parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting && $parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.applicationSetting.appLimitSetting.requiredAppReason; }),
											 option: {width: '200'}          
									 },
									 visible: $parent.appDispInfoStartupOutput().appDispInfoNoDateOutput.displayAppReason == 1"/>
					<button style="width: 30px; border: none; box-shadow: none; padding: 5px;" data-bind="click: $parent.removeMultipleRow.bind($parent, $data, $element), enable: ($parent.visibleModel.c7() && $parent.outputMode())">
					    <i data-bind="ntsIcon: { no: 237 }"></i>
                    </button>				
				</div>
			</div>
			<button id="A15_9" style="width: 100px; border: none; box-shadow: none; padding: 5px;" 
					data-bind="click: addMultipleRow, visible: multipleOvertimeContents().length < 10, enable: (visibleModel.c7() && outputMode())">
				<i data-bind="ntsIcon: { no: 236 }" style="background-position: left center;"></i>
				<span style="position: relative; top: -27px; left: 10px;" data-bind="text: $i18n('KAF005_350')"></span>
			</button>
		</div>
	</div>
	
	<div class="cf valign-top control-group"
		data-bind="visible: visibleModel.c7()">
		<!--A5_1 休憩時間ラベル-->
		<div class="cm-column" style="display: inline-block; width: 100px">
			<div class="lblTitle pull-left"
				data-bind="text: $i18n('KAF005_40'), ntsFormLabel: {}"></div>
		</div>
		<div class="table-time" style="margin-left: 16px">
			<table id="fixed-table">
				<colgroup>
					<col width="25px" />
					<col width="115px" />
					<col width="115px" />
				</colgroup>
				<thead>
					<tr>
						<th class="ui-widget-header" rowspan="2"></th>
						<!--A5_3 開始ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_41')"></th>
						<!--A5_4 終了ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_42')"></th>
					</tr>
				</thead>
				<tbody data-bind="foreach: restTime">
					<tr>
						<!--A5_5 休憩時間順序-->
						<td class="headerKAF005" data-bind="text: String(frameNo)"></td>
						<!--A5_6 開始時刻-->
						<td><input class="right-content"
							data-bind="
								ntsTimeWithDayEditor: {
									name: '#[KAF005_337]', 
									value: start, 
									constraint:'TimeWithDayAttr', 
									enable: ($parent.opOvertimeAppAtr() != 3 && $parent.visibleModel.c7() && $parent.outputMode()),
									option: {width: '85px', timeWithDay: true}}" /></td>
						<!--A5_7 終了時刻-->
						<td><input class="right-content"
							data-bind="
								ntsTimeWithDayEditor: {
									name: '#[KAF005_338]', 
									value: end, 
									constraint:'TimeWithDayAttr', 
									enable: ($parent.opOvertimeAppAtr() != 3 && $parent.visibleModel.c7() && $parent.outputMode()),
									option: {width: '85px', timeWithDay: true}}" /></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	
	<!-- calculate button A5_8-->
	<div data-bind="if: visibleModel.c7()" style="margin-bottom: 20px; margin-top: 12px;">
		<button style="width: 100px; margin-left: 200px"
			data-bind="text: $i18n('KAF005_43'), click: calculate, enable: outputMode()"
			class="caret-bottom caret-inline"></button>
	</div>

	<!-- over time hours -->
	<div class="cf valign-top control-group" data-bind="visible: true" style="margin-bottom: 2px; margin-top: 22px;">
		<!--A6_1 残業時間ラベル-->
		<div class="cm-column" style="display: inline-block; width: 100px">
			<div class="lblTitle pull-left"
				data-bind="text: $i18n('KAF005_50'), ntsFormLabel: {required: true}"></div>
		</div>
		<div class="table-time overTime1" style="margin-left: 16px">
			<table id="fixed-overtime-hour-table">
				<colgroup>
					<col width="109px" />
					<col width="115px" />
					<col width="115px" />
					<col width="115px" />
				</colgroup>
				<thead>
					<tr>
						<th class="ui-widget-header" rowspan="2"></th>
						<!--A6_3 申請時間ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_51')"></th>
						<!--A6_4 事前申請ラベル-->
						<th class="ui-widget-header hoangnd" rowspan="2"
							data-bind="text: $i18n('KAF005_52')"></th>
						<!--A6_6 実績時間ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_54')"></th>
					</tr>
				</thead>
				<tbody data-bind="foreach: overTime">
					<tr data-bind="if: visible()">
						<!--A6_7 残業時間名称-->
						<td class="headerKAF005">
							<span data-bind="text: displayNo" class="limited-label" style="width: 110px"></span>
						</td>
						<!--A6_8 残業申請時間入力-->
						<td data-bind="style: {'background-color': backgroundColor()}"><input
							class="right-content overtimeHoursCheck"
							data-bind=" 
								style: {'background-color': backgroundColor()},
								ntsTimeEditor: { 
									value: applicationTime,
									option: {width: '85px', timeWithDay: true},
									inputFormat: 'time',
									mode: 'time',
									name: $component.getNameByType(type),
									constraint:'OvertimeAppPrimitiveTime',
									enable: $parent.visibleModel.c28() && $parent.outputMode()}" />
						</td>
						<!--A6_9 残業事前申請時間-->
						<td class="right-content hoangnd"
							data-bind="text: $parent.getFormatTime(ko.toJS(preTime))"></td>
						<!--A6_11 実績時間-->
						<td class="right-content"
							data-bind="text: $parent.getFormatTime(ko.toJS(actualTime))"></td>
					</tr>
				</tbody>
			</table>
		</div>



		<div class="table-time overTime2" style="margin-left: 16px; margin-bottom: 0px;">
			<table id="fixed-overtime-hour-table-1">
				<colgroup>
					<col width="109px" />
					<col width="115px" />
				</colgroup>
				<thead>
					<tr>
						<th class="ui-widget-header" rowspan="2"></th>
						<!--A6_3 申請時間ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_51')"></th>
					</tr>
				</thead>
				<tbody data-bind="foreach: overTime">
					<tr data-bind="if: visible()">
						<!--A6_7 残業時間名称-->
						<td class="headerKAF005">
							<span data-bind="text: displayNo" class="limited-label" style="width: 110px"></span>
						</td>
						<!--A6_8 残業申請時間入力-->
						<td data-bind="style: {'background-color': backgroundColor()}"><input
							class="right-content overtimeHoursCheck"
							data-bind=" 
								style: {'background-color': backgroundColor()},
								ntsTimeEditor: { 
									value: applicationTime,
									option: {width: '85px', timeWithDay: true},
									inputFormat: 'time',
									mode: 'time',
									name: $component.getNameByType(type),
									constraint:'OvertimeAppPrimitiveTime',
									enable: $parent.visibleModel.c28() && $parent.outputMode()}" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>


	<!-- holiday time -->
	<div class="cf valign-top control-group"
		data-bind="visible: visibleModel.c30()" style="margin-top: 13px; margin-bottom: 2px">
		<!--A5_1 休憩時間ラベル-->
		<div class="cm-column" style="display: inline-block; width: 100px">
			<div class="lblTitle pull-left"
				data-bind="text: $i18n('KAF005_70'), ntsFormLabel: {required: true}"></div>
		</div>

		<div class="table-time holidayTime1" style="margin-left: 16px; margin-bottom: 0px;">
			<table id="fixed-table-holiday">
				<colgroup>
					<col width="109px" />
					<col width="115px" />
					<col width="115px" />
					<col width="115px" />
				</colgroup>
				<thead>
					<tr>
						<th class="ui-widget-header" rowspan="2"></th>
						<!--A5_3 開始ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_71')"></th>
						<!--A5_4 終了ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_72')"></th>
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_54')"></th>
					</tr>
				</thead>
				<tbody data-bind="foreach: holidayTime">
					<tr data-bind="if: visible()">
						<!--A5_5 休憩時間順序-->
						<td class="headerKAF005">
							<span data-bind="text: displayNo" class="limited-label" style="width: 110px"></span>
						</td>
						<!--A5_6 開始時刻-->
						<td data-bind="style: {'background-color': backgroundColor()}">
						<input class="right-content"
							data-bind="
								style: {'background-color': backgroundColor()},
								ntsTimeEditor: {
									name: $component.getNameByType(type), 
									value: start, 
									constraint:'OvertimeAppPrimitiveTime',
									inputFormat: 'time',
									enable: $parent.visibleModel.c28() && $parent.outputMode(),
									mode: 'time',
									option: {width: '85px', timeWithDay: true}}" /></td>
						<!--A5_7 終了時刻-->
						<td class="right-content"
							data-bind="text: $parent.getFormatTime(ko.toJS(preApp))"></td>
						<td class="right-content"
							data-bind="text: $parent.getFormatTime(ko.toJS(actualTime))"></td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="table-time holidayTime2" style="margin-left: 16px; margin-bottom: 0px;">
			<table id="fixed-table-holiday-1">
				<colgroup>
					<col width="109px" />
					<col width="115px" />
				</colgroup>
				<thead>
					<tr>
						<th class="ui-widget-header" rowspan="2"></th>
						<!--A5_3 開始ラベル-->
						<th class="ui-widget-header" rowspan="2"
							data-bind="text: $i18n('KAF005_71')"></th>
					</tr>
				</thead>
				<tbody data-bind="foreach: holidayTime">
					<tr data-bind="if: visible()">
						<!--A5_5 休憩時間順序-->
						<td class="headerKAF005">
							<span data-bind="text: displayNo" class="limited-label" style="width: 110px"></span>
						</td>
						<!--A5_6 開始時刻-->
						<td data-bind="style: {'background-color': backgroundColor()}">
						<input class="right-content"
							data-bind="
								style: {'background-color': backgroundColor()},
								ntsTimeEditor: {
									name: $component.getNameByType(type), 
									value: start, 
									constraint:'OvertimeAppPrimitiveTime',
									inputFormat: 'time',
									enable: $parent.visibleModel.c28() && $parent.outputMode(),
									mode: 'time',
									option: {width: '85px', timeWithDay: true}}" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
	`
	@component({
        name: 'kaf005-share',
		template: template
    })
	class KAF005ShrModel extends ko.ViewModel {
		
		// restTime: KnockoutObservableArray<RestTime> = ko.observableArray([]);
		
		// holidayTime: KnockoutObservableArray<HolidayTime> = ko.observableArray([]);
		
		// overTime: KnockoutObservableArray<OverTime> = ko.observableArray([]);
		visibleModel: any;
		overtTimeMountTable1: boolean = false;
		overtTimeMountTable2: boolean = false;
		
		holidayTimeMountTable1: boolean = false;
		holidayTimeMountTable2: boolean = false;
		
		backgroundColor: KnockoutObservable<Boolean> = ko.observable(false);
		
		isAgentMode: KnockoutObservable<Boolean> = ko.observable(false);
		
		created(params: any) {
			const self = this;
			self.visibleModel = params.visibleModel;
			self.isAgentMode = params.agent;
			// self.restTime = params.restTime;
			// self.holidayTime = params.holidayTime;
			// self.overTime = params.overTime;
			
		}
		
		mounted() {
			const self = this;
			$(".overTime2").hide();
			$(".overTime1").hide();
			$("#fixed-table").ntsFixedTable({ height: 120 });
			self.visibleModel.c15_3.subscribe((value: any) => {
				if (!_.isNil(value)) {
					if (value && !self.isAgentMode()) {
						
						$(".overTime2").hide();
						$(".overTime1").show();
						$(".holidayTime2").hide();
						$(".holidayTime1").show();
						
						if (!self.overtTimeMountTable1) {
							$("#fixed-overtime-hour-table").ntsFixedTable({ height: 216 });
							self.overtTimeMountTable1 = true;
						}
						if (!self.holidayTimeMountTable1) {
							$("#fixed-table-holiday").ntsFixedTable({ height: 120 });
							self.holidayTimeMountTable1 = true;
						}
						
					} else {
						
						$(".overTime1").hide();
						$(".overTime2").show();
						$(".holidayTime1").hide();
						$(".holidayTime2").show();
						
						if (!self.overtTimeMountTable2) {
							$("#fixed-overtime-hour-table-1").ntsFixedTable({ height: 216 });
							self.overtTimeMountTable2 = true;
						}
						
						if (!self.holidayTimeMountTable2) {
							$("#fixed-table-holiday-1").ntsFixedTable({ height: 120 });
							self.holidayTimeMountTable2 = true;
						}
						
					}
				}
			})
			
		}
		
		public getNameByType(appType: AttendanceType) {
			const self = this;
			
			if (appType === AttendanceType.NORMALOVERTIME) {
				
				return self.$i18n('KAF005_55');
			} else if (appType === AttendanceType.MIDNIGHT_OUTSIDE) {
				
				return self.$i18n('KAF005_64');
				
			} else if (appType === AttendanceType.FLEX_OVERTIME) {
				
				return self.$i18n('KAF005_66');
			} else if (appType === AttendanceType.BREAKTIME) {
				
				return self.$i18n('KAF005_70');
				
			} else if (appType === AttendanceType.MIDDLE_BREAK_TIME) {
				
				return self.$i18n('KAF005_341');
				
			} else if (appType === AttendanceType.MIDDLE_EXORBITANT_HOLIDAY) {
				
				return self.$i18n('KAF005_342');
				
			} else if (appType === AttendanceType.MIDDLE_HOLIDAY_HOLIDAY) {
				
				return self.$i18n('KAF005_343');
				
			} else {
				
				return '';
			}
			
		}
	}
	enum AttendanceType {

		NORMALOVERTIME,
		BREAKTIME,
		BONUSPAYTIME,
		BONUSSPECIALDAYTIME,
		MIDNIGHT,
		SHIFTNIGHT,
		MIDDLE_BREAK_TIME,
		MIDDLE_EXORBITANT_HOLIDAY,
		MIDDLE_HOLIDAY_HOLIDAY,
		FLEX_OVERTIME,
		MIDNIGHT_OUTSIDE		
	}
	export interface OverTime {
		frameNo: string;
		displayNo: KnockoutObservable<string>;
		applicationTime?: KnockoutObservable<number>;
		preTime?: KnockoutObservable<number>;
		actualTime?: KnockoutObservable<number>;
		type: AttendanceType;
		visible: KnockoutObservable<Boolean>;
		backgroundColor: KnockoutObservable<string>;
	}
	export interface RestTime {
		frameNo: string;
		displayNo: KnockoutObservable<string>;
		start?: KnockoutObservable<number>;
		end?: KnockoutObservable<number>;
	}

	export class MultipleOvertimeContent {
		frameNo: number;
		start: KnockoutObservable<number>;
		end: KnockoutObservable<number>;
		fixedReasonCode: KnockoutObservable<number>;
		appReason: KnockoutObservable<string>;

		constructor(sub: any, frameNo: number, start?: number, end?: number, fixedReasonCode?: number, appReason?: string) {
			this.frameNo = frameNo;
			this.start = ko.observable(start);
			this.end = ko.observable(end);
			this.fixedReasonCode = ko.observable(fixedReasonCode);
			this.appReason = ko.observable(appReason);
			this.start.subscribe(value => {
				if (sub) sub();
			});
            this.end.subscribe(value => {
				if (sub) sub();
            });
		}
	}

	export interface HolidayTime {
		frameNo: string;
		displayNo: KnockoutObservable<string>;
		start?: KnockoutObservable<number>;
		preApp?: KnockoutObservable<number>;
		actualTime?: KnockoutObservable<number>;
		type: AttendanceType;
		visible: KnockoutObservable<Boolean>;
		backgroundColor: KnockoutObservable<string>;
	}
	
	
}