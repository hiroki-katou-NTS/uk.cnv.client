module nts.uk.ui.ktg004.a {
    export const STORAGE_KEY_TRANSFER_DATA = "nts.uk.request.STORAGE_KEY_TRANSFER_DATA";

    const KTG004_API = {
        GET_DATA: 'screen/at/ktg004/getData'
    };

    const DIALOGS = {
        KTG004B: '/view/ktg/004/b/index.xhtml'
    };

    const WINDOWS = {
        KTG003A: '/view/kdw/003/a/index.xhtml'
    };

    const convertToTime = (data: string | number): string => {
        if (['0', 0, null, undefined, ''].indexOf(data) > -1) {
            return '00:00';
        } else {
            const numb = Number(data);
            const negative = numb < 0;
            const hour = numb < 0 ? Math.floor((numb < 0 ? numb * -1: numb) / 60)
									: Math.floor(numb / 60);
            const minute = Math.floor(numb % 60);

            return `${negative ? '-' : ''}${Math.abs(hour)}:${_.padStart(Math.abs(minute) + '', 2, '0')}`;
        }
    }

    @component({
        name: 'ktg-004-a',
        template: `
            <div class="widget-title">
                <table style="width: 100%;">
                    <colgroup>
                        <col width="auto" />
                        <col width="41px" />
                    </colgroup>
                    <thead>
                        <!-- A1_1 -->
                        <th class="ktg004-fontsize-larger">
                            <div data-bind="ntsFormLabel: { required: false, text: $component.name }"></div>
                        </th>
                        <!-- A1_2 -->
                        <th data-bind="if: $component.detailedWorkStatusSettings">
                            <button class="icon ktg004-no-border" data-bind="click: $component.setting">
                                <i data-bind="ntsIcon: { no: 5, width: 25, height: 25 }"></i>
                            </button>
                        </th>
                    </thead>
                </table>
            </div>
            <div class="ktg-004-a ktg004-fontsize ktg004-border" data-bind="widget-content: 100">
                <div style="padding: 0px 40px 0px 30px;">
                    <table class="widget-table" style="width: 100%;">
                        <colgroup>
                            <col width="auto" />
                            <col width="auto" />
                        </colgroup>
                        <tbody data-bind="foreach: { data: $component.itemsDisplay, as: 'row' }">
                            <tr class="row-show-ktg004">
								<td>
									<div style="display: flex">
		                                <div style="position: relative; width: 50%">
		                                    <div data-bind="if: row.btn" style="float: left; position: relative;">
		                                        <!-- A2_2 -->
		                                        <button class="icon ktg004-no-border" data-bind="
		                                            click: function() { $component.openKDW003() },
		                                            ntsIcon: { no: 201, width: 25, height: 28 },
		                                            enable: row.canClick">
		                                        </button>
		                                        <!-- A2_3 -->
		                                        <i style="position: absolute; left: 13px; bottom: 0px; cursor: pointer;"
		                                            data-bind="visible: row.canClick, ntsIcon: { no: 165, width: 13, height: 13 }, click: function() { $component.openKDW003() }">
		                                        </i>
		                                    </div>
		                                    <div data-bind="ntsFormLabel: { required: false, text: row.name }"></div>
		                                </div>
										<div style="width: 50%; margin-top: 5px ">
			                                <div class="text-right" data-bind="i18n: row.text"></div>										
										</div>
									</div>
									<div data-bind="if: row.name == 'KTG004_9'">
										<div data-bind="ntsFormLabel: { required: false, text: row.grantDay }"></div>									
									</div>
								</td>
                            </tr>
                        </tbody>
                        <tbody data-bind="foreach: { data: $component.specialHolidaysRemainings, as: 'row'}"> 
                            <tr class="row-show-ktg004">
                                <td>
									<div style="display: flex">
	                                    <div style="position: relative; width: 50%" data-bind=" ntsFormLabel: { required: false, text: row.name }"></div>
		                                <div style="width: 50%; margin-top: 5px " class="text-right" data-bind="i18n: row.specialResidualNumber"></div>									
									</div>
                                </td>
                            </tr>
                        </tbody>
                    </table>	
                </div>
            </div>
            <style rel="stylesheet">
                .ktg-004-a table tr {
                    height: 30px !important;
                }
                .ktg-004-a .text-right {
                    text-align: right;
                }
                .text-right span {
                    color: black;
                }
                .ktg004-fontsize div.form-label>span.text {
                    font-size: 1rem !important;
                    padding-left: 5px;
                }
                .ktg004-fontsize-larger div.form-label>span.text {
                    font-size: 1.2rem !important;
                }
                .ktg004-no-border {
                    border: none !important;
                }
                .ktg004-no-border td {
                    border: none !important;
                }
                .ktg004-border table tr td,
			    .ktg004-border table tr th {
                    border-width: 0px;
                    border-bottom: 1px solid #BFBFBF;
			    }
            </style>
        `
    })
    export class KTG004AComponent extends ko.ViewModel {
        widget: string = 'KTG004A';

        name: KnockoutObservable<string> = ko.observable('');

        detailedWorkStatusSettings = ko.observable(false);

        specialHolidaysRemainings: KnockoutObservableArray<SpecialHolidaysRemaining> = ko.observableArray([]);
        itemsDisplay: KnockoutObservableArray<ItemDisplay> = ko.observableArray([]);

        constructor(private params: { currentOrNextMonth: 1 | 2; }) {
            super();

            if (this.params === undefined) {
                this.params = { currentOrNextMonth: 1 };
            }

            if (this.params.currentOrNextMonth === undefined) {
                this.params.currentOrNextMonth = 1;
            }
        }

        created() {
            const vm = this;
            const { params } = vm;
            const { currentOrNextMonth } = params || { currentOrNextMonth: 1 };

            vm.$blockui('invisibleView')
                .then(() => vm.$ajax("at", KTG004_API.GET_DATA, { currentOrNextMonth: currentOrNextMonth }))
                .then(function (data: ResponseData) {
                    const {
                        name,
                        detailedWorkStatusSettings,
                        itemsSetting,
                        attendanceInfor,
                        remainingNumberInfor, 
                        vacationSetting
                    } = data;
                    const {
                        dailyErrors,
                        early,
                        flexCarryOverTime,
                        flexTime,
                        holidayTime,
                        late,
                        nigthTime,
                        overTime
                    } = attendanceInfor;

                    const {
                        longTermCareRemainingNumber,
                        numberAccumulatedAnnualLeave,
                        numberOfAnnualLeaveRemain,
                        numberOfSubstituteHoliday,
                        nursingRemainingNumberOfChildren,
                        remainingHolidays,
                        specialHolidaysRemainings,
						grantDate,
						grantDays, 
                        subHolidayTimeManage
                    } = remainingNumberInfor;

                    const {
                        holiday60HManage,
                        nursingManage,
                        publicHolidayManage,
                        childCaremanage,
                        accomoManage,
                        accumAnnualManage,
                        substituteManage,
                        substituteTimeManage,
                        annualManage,
                    } = vacationSetting;

                    const itemsDisplay: ItemDisplay[] = [];
                    const itemsHolidaysRemainings: SpecialHolidaysRemaining[] = [];

                    const items: number[] = _
                        .chain(itemsSetting)
                        .orderBy(['item'], ['asc'])
                        .filter(({ displayType }) => !!displayType)
                        .map(({ item }) => item)
                        .value();

                    vm.name(name || "");
                    vm.detailedWorkStatusSettings(detailedWorkStatusSettings);

                    _
                        .chain(items)
                        .each((item: number) => {
                            switch (item) {
                                case 21:
                                    itemsDisplay
                                        .push({
                                            name: 'KTG004_1',
                                            text: '',
                                            btn: true,
                                            canClick: dailyErrors ? true : false
                                        })
                                    break;
                                case 22:
                                    itemsDisplay
                                        .push({
                                            name: 'KTG004_2',
                                            text: convertToTime(overTime)
                                        })
                                    break;
                                case 23:
                                    itemsDisplay
                                        .push({
                                            name: 'KTG004_3',
                                            text: `${convertToTime(flexTime)}${vm.$i18n('KTG004_4', [convertToTime(flexCarryOverTime)])}`
                                        })
                                    break;
                                case 24:
                                    itemsDisplay
                                        .push({
                                            name: 'KTG004_5',
                                            text: convertToTime(nigthTime)
                                        })
                                    break;
                                case 25:
                                    itemsDisplay
                                        .push({
                                            name: 'KTG004_6',
                                            text: convertToTime(holidayTime)
                                        })
                                    break;
                                case 26:
                                    itemsDisplay
                                        .push({
                                            name: 'KTG004_7',
                                            text: vm.$i18n('KTG004_8', [late, early])
                                        })
                                    break;
                                case 27:
                                    if (vacationSetting.annualManage) {
                                        itemsDisplay
                                            .push({
                                                name: 'KTG004_9',
                                                text: 
                                                    numberOfAnnualLeaveRemain.time == ZERO_TIME
                                                    ?
                                                    vm.$i18n('KTG004_15', [`${numberOfAnnualLeaveRemain.day}`])
                                                    :
                                                    vm.$i18n('KTG004_28', [`${numberOfAnnualLeaveRemain.day}`, `${numberOfAnnualLeaveRemain.time}`]),
                                                grantDay:
                                                    _.isNil(grantDate)
                                                    ?
                                                    `${vm.$i18n('KTG004_26')} ${vm.$i18n('KTG004_27')}`
                                                    :
                                                    `${vm.$i18n('KTG004_26')}${moment(grantDate).format("YYYY/MM/DD")}　${grantDays}日`
                                                    
                                                        
                                            })
                                    }
                                    break;
                                case 28:
                                    if (vacationSetting.accumAnnualManage) {
                                        itemsDisplay
                                            .push({
                                                name: 'KTG004_10',
                                                text: vm.$i18n('KTG004_15', [`${numberAccumulatedAnnualLeave}`])
                                            })
                                    }
                                    break;
                                case 29:
                                    if (vacationSetting.substituteManage) {
                                        itemsDisplay
                                            .push({
                                                name: 'KTG004_11',
                                                text: 
                                                numberOfSubstituteHoliday.day <= 0 && numberOfSubstituteHoliday.time != ZERO_TIME
                                                ?
                                                `${numberOfSubstituteHoliday.time}`
                                                : numberOfSubstituteHoliday.day == 0 && numberOfSubstituteHoliday.time == ZERO_TIME && subHolidayTimeManage == 1 ?
                                                `${numberOfSubstituteHoliday.time}` :
                                                vm.$i18n('KTG004_15', [`${numberOfSubstituteHoliday.day}`])
                                            })
                                    }
                                    break;
                                case 30:
                                    if (vacationSetting.accomoManage) {
                                        itemsDisplay
                                            .push({
                                                name: 'KTG004_12',
                                                text: vm.$i18n('KTG004_15', [`${remainingHolidays}`])
                                            })
                                    }
                                    break;
                                case 31:
                                    if (vacationSetting.childCaremanage) {
                                        itemsDisplay
                                            .push({
                                                name: 'KTG004_13',
                                                text: 
                                                    nursingRemainingNumberOfChildren.time == ZERO_TIME
                                                    ?
                                                    vm.$i18n('KTG004_15', [`${nursingRemainingNumberOfChildren.day}`])
                                                    :
                                                    vm.$i18n('KTG004_28', [`${nursingRemainingNumberOfChildren.day}`, `${nursingRemainingNumberOfChildren.time}`])
                                            })
                                    }
                                    break;
                                case 32:
                                    if (vacationSetting.nursingManage) {
                                        itemsDisplay
                                            .push({
                                                name: 'KTG004_14',
                                                text:
                                                    longTermCareRemainingNumber.time == ZERO_TIME
                                                    ? 
                                                    vm.$i18n('KTG004_15', [`${longTermCareRemainingNumber.day}`])
                                                    :
                                                    vm.$i18n('KTG004_28', [`${longTermCareRemainingNumber.day}`, `${longTermCareRemainingNumber.time}`])
                                            })
                                    }
                                    break;
                            }
                        })
                        .value();

                    _
                        .chain(specialHolidaysRemainings)
                        .filter(({ code }) => items.indexOf(code) > -1)
                        .each(({
                            code,
                            name,
                            specialResidualNumber
                        }) => {
                            itemsHolidaysRemainings
                                .push({
                                    code,
                                    name,
                                    specialResidualNumber: 
											specialResidualNumber.time == ZERO_TIME
											?
											vm.$i18n('KTG004_15', [`${specialResidualNumber.day}`])
											:
											vm.$i18n('KTG004_28', [`${specialResidualNumber.day}`, `${specialResidualNumber.time}`])
                                });
                        })
                        .value();

                    vm.itemsDisplay(itemsDisplay);
                    vm.specialHolidaysRemainings(itemsHolidaysRemainings);

                    vm.$nextTick(() => {
                        $(vm.$el)
                            .find('[data-bind]')
                            .removeAttr('data-bind');
                        _.forEach($(".row-show-ktg004 td"), element => $(element).removeClass("ktg004-no-border"));    
                        $(".row-show-ktg004").last().addClass("ktg004-no-border");
                    });
                })
                .always(() => vm.$blockui('clearView'));
        }

        public setting() {
            const vm = this;

            vm.$window
                .modal('at', DIALOGS.KTG004B)
                .then(() => {
                    vm.$window
                        .shared("KTG004B")
                        .then((data: any) => {
                            if (data) {
                                vm.created();
                            }
                        });
                });
        }

        public openKDW003() {
            const vm = this;

            vm.$jump('at', WINDOWS.KTG003A);
        }

		
    }
	const ZERO_TIME = `0:00`;

    interface ItemDisplay {
        name: string;
        text: string;
        btn?: boolean;
        canClick?: boolean;
		grantDay?: string;
    }

    interface SpecialHolidaysRemaining {
        //特別休暇コード
        code: number;
        //特別休暇名称
        name: string;
        //特休残数
        specialResidualNumber: string;
    }

    interface ResponseData {
        attendanceInfor: Attendance;
        closingDisplay: Closing;
        closingThisMonth: Closing;
        closureId: number;
        detailedWorkStatusSettings: boolean;
        itemsSetting: ItemSetting[];
        name: any | null;
        nextMonthClosingInformation: any | null;

        remainingNumberInfor: RemainingNumberInfor;
        vacationSetting: VacationSetting;
    }

    interface ItemSetting {
        displayType: boolean;
        item: 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 | 32;
        name: string;
    }

    interface Closing {
        endDate: string;
        processingYm: number;
        startDate: string;
    }

    interface NumberOfShift {
        day: number;
        time: string;
    }

    interface Attendance {
        dailyErrors: boolean;
        early: string;
        flexCarryOverTime: string;
        flexTime: string;
        holidayTime: string
        late: string;
        nigthTime: string;
        overTime: string;
    }

    interface RemainingNumberInfor {
        longTermCareRemainingNumber: NumberOfShift;
        numberAccumulatedAnnualLeave: number;
        numberOfAnnualLeaveRemain: NumberOfShift;
        numberOfSubstituteHoliday: NumberOfShift;
        nursingRemainingNumberOfChildren: NumberOfShift;
        remainingHolidays: number;
        specialHolidaysRemainings: SpecialHolidaysRemainings[];
		grantDate?: string;
		grantDays: number;
        subHolidayTimeManage: number;

    }

    interface VacationSetting {
		// 60H超休残数管理する
		holiday60HManage: boolean;
    
		// 介護残数管理する
		nursingManage: boolean;
		
		// 公休残数管理する
		publicHolidayManage: boolean;
		
		// 子の看護残数管理する
		childCaremanage: boolean;
		
		// 振休残数管理する
		accomoManage: boolean;
		
		// 積立年休残数管理する
		accumAnnualManage: boolean;
		
		// 代休残数管理する
		substituteManage: boolean;
		
		// 代休時間残数管理する
		substituteTimeManage: boolean;
		
		// 年休残数管理する
		annualManage: boolean;
	}

    interface SpecialHolidaysRemainings {
        code: number;
        name: string;
        specialResidualNumber: NumberOfShift;
    }
}