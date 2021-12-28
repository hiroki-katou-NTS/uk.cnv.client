module nts.uk.at.view.ktg027.a {
    const API = {
        CHANGE_DATE: "/screen/at/overtimehours/onChangeDate",
        GET_DATA_INIT: "/screen/at/overtimehours/getOvertimedDisplayForSuperiorsDto",
    };

    // 色	※色定義-就業.xlsxを参照
    const timeStyle = (state: number): string => {
        if (state === 1) {
            return 'exceeding-limit-error';
        }

        if (state === 2) {
            return 'exceeding-limit-alarm';
        }

        if (state === 3) {
            return 'special-exceeded-limit-error';
        }

        if (state === 4) {
            return 'special-exceeded-limit-alarm';
        }

        if (state === 6) {
            return 'special-exceeding-limit-error';
        }

        if (state === 7) {
            return 'special-exceeding-limit';
        }

        if (state === 8) {
            return 'bg-exceed-special-upperlimit color-exceed-special-upperlimit';
        }

        return '';
    }

    @component({
        name: 'ktg-027-a',
        template: `
            <div class="ktg-027-a widget-title">
                <table style="width: 100%;">
                    <colgroup>
                        <col width="auto" />
                        <col width="155px" />
                        <col width="65px" />
                    </colgroup>
                    <thead>
                        <tr>
                            <th class="ktg027-fontsize">
                                <div data-bind="ntsFormLabel: { required: false, text: $component.$i18n('KTG027_5') }"></div>
                            </th>
                            <th style="padding-right: 5px;">
                                <div id="ktg027-datepick" data-bind="ntsDatePicker: {
                                    name: $component.$i18n('KTG027_1'),
                                    value: $component.targetYear,
                                    dateFormat: 'yearmonth',
                                    valueFormat: 'YYYYMM',
                                    showJumpButtons: true
                                }"></div>
                            </th>
                            <th>
                                <button data-bind="ntsLegendButton: legendOptions"></button>
                            </th>
                        </tr>
                    </thead>
                </table>
            </div>
            <div class="ktg-027-a widget-content widget-fixed scroll-padding ui-resizable ktg027-border-top ktg027-1rem">
                <div>
                    <table>
                        <colgroup>
                            <col width="25%" />
                            <col width="17%" />
                            <col width="58%" />
                        </colgroup>
                        <head>
                            <tr>
                                <th class="text-center">
                                    <div data-bind="ntsFormLabel: { required: false, text: $component.$i18n('Com_Person') }"></div>
                                </th>
                                <th class="text-center" style="white-space: nowrap;">
                                    <div style="padding-right: 0;" data-bind="
                                        ntsFormLabel: { required: false, text: $component.$i18n('KTG027_4') }">
                                    </div>
                                </th>
                                <td style="padding-left: 10px;" rowspan="1">
                                    <canvas data-bind="ktg-026-chart: $component.dataTable, type: 'head'"></canvas>
                                </td>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="ktg-027-a" data-bind="widget-content: 110, default: 425">
                <div>
                    <table>
                        <colgroup>
                            <col width="42%" />
                            <col width="58%" />
                        </colgroup>
                        <tbody data-bind="foreach: { data: $component.dataTable, as: 'row' }">
                            <tr style="height: 35px;">
                                <td>
                                    <div class="text-underline limited-label" data-bind="
                                        text: row.businessName,
                                        click: function() { $component.openKTG026(row) }"
                                        style="float:left; width: 60%; padding-left: 5px;">
                                    </div>
                                    <div class="text-right text-underline" data-bind="
                                        time: row.time.tt,
                                        click: function() { $component.openKDW003(row) },
                                        css: row.state"
                                        style="height: 25px; padding-right: 5px;">
                                    </div>
                                </td>
                                <td style="padding-left: 10px;" data-bind="ktg-chart: $component.dataTable"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <style>
                .text-underline {
                    text-decoration: underline;
                }
                .ktg-027-a table tr th,
                .ktg-027-a table tr td {
                    border-bottom: none !important;
                }
                .ktg-027-a table tr td.text-right div.text-underline {
                    line-height: 30px;
                    width: 60px;
                    float: right;
                }
                .ktg027-1rem div.form-label>span.text {
                    font-size: 1rem;
                }
                .ktg027-border-top {
                    border-top: none !important;
                }
                .ktg027-border-top::before {
                    width: 100%;
                    height: 1px;
                    background: #b1b1b1;
                    content: "";
                    position: absolute;
                    top: 0;
                }
                .ktg027-fontsize div.form-label>span.text {
                    font-size: 1.2rem !important;
                }
                .ktg-027-a .outside {
                    color: #e05f4e;
                }
                .ktg-027-a .statutory {
                    color: #49bfa8;
                }
                .ktg-027-a.widget-content.ui-resizable {
                    border-top: 0;
                }
                .ktg-027-a.widget-content:not(.ui-resizable) {
                    padding-bottom: 0;
                }
                .ktg-027-a.widget-content:not(.ui-resizable) td {
                    border-bottom: 0;
                }
                .ktg-027-a.widget-content.ui-resizable th:first-child,
                .ktg-027-a.widget-content.ui-resizable td:first-child {
                    overflow: hidden;
                    white-space: nowrap;
                    max-width: 100px;
                    padding-left: 0px;
                    padding-right: 0px;
                    text-overflow: ellipsis;
                }
                .ktg-027-a.widget-content.ui-resizable td:nth-child(1),
                .ktg-027-a.widget-content.ui-resizable td:nth-child(2) {
                    cursor: pointer;
                }
                .ktg-027-a.widget-content.ui-resizable td[rowspan] {
                    padding: 0;
                    overflow: hidden;
                }
                .ktg-027-a.widget-content.ui-resizable td[rowspan] canvas {
                    width: 230px !important;
                }
                .ktg-027-a.widget-content.ui-resizable.widget-fixed {
                    padding-bottom: 0px;
                }
                .ktg-027-a.widget-content.ui-resizable.widget-fixed>div {
                    padding-bottom: 0px;
                }
                .ktg-027-a.widget-content.ui-resizable.widget-fixed td[rowspan] canvas {
                    height: 41px !important;
                }
                .widget-container.ie .ktg-027-a.widget-content.ui-resizable td[rowspan] canvas {
                    margin-top: -1px;
                }
                .widget-container.has-scroll .ktg-027-a.scroll-padding {
                    padding-right: 17px;
                }
                /* 限度アラーム時間超過 */
                .ktg-027-a.widget-content.ui-resizable .exceeding-limit-alarm {
                    background-color: #FFFF99; /* 36協定アラーム */
                    color: #FF9900; /* 36協定アラーム文字 */
                }
                /* 限度エラー時間超過 */
                .ktg-027-a.widget-content.ui-resizable .exceeding-limit-error {
                    background-color: #FF99CC; /* 36協定エラー */
                    color: #ffffff; /* 36協定エラー文字 */
                }
                /* 限度アラーム時間超過（特例あり） */
                .ktg-027-a.widget-content.ui-resizable .special-exceeding-limit {
                    background-color: #eb9152; /* 36協定特例 */
                }
                /* 限度エラー時間超過（特例あり） */
                .ktg-027-a.widget-content.ui-resizable .special-exceeding-limit-error {
                    background-color: #eb9152; /* 36協定特例 */
                }
                /* 特例限度アラーム時間超過 */
                .ktg-027-a.widget-content.ui-resizable .special-exceeded-limit-alarm  {
                    background-color: #FFFF99; /* 36協定アラーム */
                    color: #FF9900; /* 36協定アラーム文字 */
                }
                /* 特例限度エラー時間超過 */
                .ktg-027-a.widget-content.ui-resizable .special-exceeded-limit-error {
                    background-color: #FF99CC; /* 36協定エラー */
                    color: #ffffff; /* 36協定エラー文字 */
                }
            </style>
            <style data-bind="html: $component.chartStyle"></style>
        `
    })
    export class KTG027AComponent extends ko.ViewModel {
        widget: string = 'KTG027A';

        // A1_2 対象年選択
        targetYear: KnockoutObservable<string | null> = ko.observable(null);

        employees: KnockoutObservableArray<PersonalInfo> = ko.observableArray([]);
        overtimeSubor: KnockoutObservableArray<OvertimeSubordinate> = ko.observableArray([]);
        personalSubor: KnockoutObservableArray<PersonalInfoSubordinate> = ko.observableArray([]);

        dataTable!: KnockoutComputed<any[]>;
        chartStyle!: KnockoutComputed<string>;
        legendOptions: any;
		firstLoad: boolean = true;
        cache: any;
        isRefresh: boolean = false;

        constructor() {
            super();
            const vm = this;

            vm.dataTable = ko.computed({
                read: () => {
                    const employees = ko.unwrap<PersonalInfo[]>(vm.employees);
                    const overtimeSubor = ko.unwrap<OvertimeSubordinate[]>(vm.overtimeSubor);
                    const personalSubor = ko.unwrap<PersonalInfoSubordinate[]>(vm.personalSubor);

                    return _
                        .chain(employees)
                        .filter((emp: PersonalInfo) => {
                            const os = _.find(overtimeSubor, ({ employeeId }) => employeeId === emp.employeeId);
                            if (os) {
                                return true;
                            }
                            return false;
                        })
                        .map((emp: PersonalInfo) => {
                            const os = _.find(overtimeSubor, ({ employeeId }) => employeeId === emp.employeeId);
                            const ps = _.find(personalSubor, ({ employeeId }) => employeeId === emp.employeeId);

                            if (os) {
                                const { agreementTime: at, agreMax: am, state } = os;

                                return _.extend(emp, {
                                    // title of chart
                                    date: emp.businessName,
                                    time: {
                                        tt: at.agreementTime || 0,
                                        ot: Math.min(6000, at.agreementTime),
                                        wh: at.agreementTime >= 6000 ? 0 : Math.max((am.agreementTime || 0) - (at.agreementTime || 0), 0)
                                    },
                                    state: timeStyle(state)
                                });
                            }

                            return _.extend(emp, { time: { tt: 0, ot: 0, wh: 0 }, state: 'a' });
                        })
                        // trigger rerender table & chart
                        .filter(() => employees.length && overtimeSubor.length && personalSubor.length)
                        .value();
                }
            });

            vm.chartStyle = ko.computed({
                read: () => {
                    const data = ko.unwrap<PersonalInfo[]>(vm.dataTable);

                    if (!data.length) {
                        return '.ktg-027-a.widget-content:not(.widget-fixed) td[rowspan] canvas { height: 0px !important; }';
                    }

                    return `.ktg-027-a.widget-content:not(.widget-fixed) td[rowspan] canvas { height: ${data.length * 35}px !important; }`;
                }
            });

            vm.dataTable
                .subscribe(() => {
                    vm.$nextTick(() => {
                        $('.ktg-027-a.ui-resizable').trigger('wg.resize');

                        $(vm.$el)
                            .find('.ktg-027-a.widget-content.ui-resizable td:first-child')
                            .each((__: number, el: HTMLElement) => {
                                if (el.offsetWidth < el.scrollWidth) {
                                    el.title = el.innerText;
                                }
                            });

                        $(vm.$el).find('[data-bind]').removeAttr('data-bind');
                    })
                });
        }

        created() {
            const vm = this;
            vm.legendOptions = {
                items: [
                    { colorCode: '#99FF66', labelText: vm.$i18n('KTG027_2') },
                    { colorCode: '#00CC00', labelText: vm.$i18n('KTG027_3') },
                ],
                template :
                '<div class="legend-item-label">'
                + '<div style="color: #{colorCode};" data-bind="ntsFormLabel: { required: false }">#{labelText}</div>'
                + '</div>'
            };

            const startScreen = () => {
            
            const { closureId, currentOrNextMonth, endDate, processDate, startDate } = vm.cache;
            const bodyParams = { closingId: closureId, currentOrNextMonth, startDate, endDate, processingYm: processDate };
            vm
                .$blockui('invisibleView')
                .then(() => vm.$ajax('at', API.GET_DATA_INIT, bodyParams))
                .then((response: DataInit) => {
                    const { closureId, personalInformationOfSubordinateEmployees, closingInformationForCurrentMonth, closingInformationForNextMonth, overtimeOfSubordinateEmployees } = response;

                    vm.employees(personalInformationOfSubordinateEmployees);

                    vm.targetYear
                        .subscribe((ym: string | null) => {
                            vm.$validate('#ktg027-datepick').then(valid => {
                                if (!valid || _.isEmpty(ym)) return;

                                if (typeof ym === 'string') {
                                    vm.$window.storage('KTG027_INITIAL_DATA', {
                                        isRefresh: false,
                                        target: ym
                                    });
                                    if (vm.firstLoad && !vm.isRefresh) {
                                        vm.loadData(ym, closureId, {overtimeOfSubordinateEmployees, personalInformationOfSubordinateEmployees});
                                        vm.firstLoad = false;
                                    } else {
                                        vm.isRefresh = false;
                                        vm.loadData(ym, closureId);	
                                    }
                                }
                            });
                        });

                    vm.$window.storage('KTG027_INITIAL_DATA').then((rs: {isRefresh: boolean, target: any}) => {
                        if (rs && rs.isRefresh && rs.target) {
                            vm.isRefresh = true;
                            vm.targetYear(rs.target);
                            return;
                        }

                        // update targetYear by closure data
                        if (closingInformationForNextMonth) {
                            const { processingYm } = closingInformationForNextMonth;

                            vm.targetYear(`${processingYm}`);
                            return;
                        }

                        const { processingYm } = closingInformationForCurrentMonth;

                        vm.targetYear(`${processingYm}`);
                    });
                })
                .fail((message: { messageId: string }) => {
                    vm.$dialog.error(message).then(() => vm.$blockui('clearView'));
                });
                }

            // Rq609 is called here, by cache of ccg008
            vm.$window.storage('cache')
                .then(obj => vm.cache = obj)
                .then(() => startScreen());
        }

        // load data by change ym
        private loadData(ym: string, closureId: number, overtimeParam?: OverTimeResponse) {
            const vm = this;
			if(overtimeParam) {
				const { overtimeOfSubordinateEmployees, personalInformationOfSubordinateEmployees } = overtimeParam;
	
                vm.overtimeSubor(overtimeOfSubordinateEmployees);
                vm.personalSubor(personalInformationOfSubordinateEmployees);
				vm.$blockui('clearView')
			}else{
	            const { CHANGE_DATE: getDataWhenChangeDate } = API;
	
	            if (ym) {
	                vm.overtimeSubor([]);
	                vm.personalSubor([]);
	
	                vm
	                    .$blockui('invisibleView')
	                    .then(() => vm.$ajax('at', `${getDataWhenChangeDate}/${closureId}/${ym}`))
	                    .then((overtime: OverTimeResponse) => {
	                        const { overtimeOfSubordinateEmployees, personalInformationOfSubordinateEmployees } = overtime;
	
	                        vm.overtimeSubor(overtimeOfSubordinateEmployees);
	                        vm.personalSubor(personalInformationOfSubordinateEmployees);
	                    })
	                    .always(() => vm.$blockui('clearView'));
	            }
			}
        }

        openKTG026(item: any) {
            const vm = this;
            const { $user } = vm;
            let companyID: any = ko.observable(__viewContext.user.companyId);
            let paramKTG026 = {
                companyId: companyID,
                employeeId: item.employeeId,
                targetDate: vm.targetYear(),
                targetYear: "",
                mode: "Superior",
            };
            vm.$window
                .modal('at', '/view/ktg/026/a/superior.xhtml', paramKTG026);
        }

        openKDW003(item: any) {
            const vm = this;
            const { $user } = vm;
            let paramKDW003 = {
                initParam: {
                    errorRefStartAtr: false,
                    changePeriodAtr: true,
                    screenMode: 0, //normal
                    lstEmployee: [item.employeeId],
                    transitionDesScreen: '',
                    yearMonth: vm.targetYear(),
                },
                extractionParam: {
                    displayFormat: 0, //individual
                    startDate: vm.targetYear(),
                    endDate: vm.targetYear(),
                    dateTarget: vm.targetYear(),
                    lstExtractedEmployee: [item.employeeId],
                    individualTarget: item.employeeId,

                },
            };
            vm.$jump('at', "/view/kdw/003/a/index.xhtml", paramKDW003);
        }

        destroyed() {
            const vm = this;

            vm.dataTable.dispose();
            vm.chartStyle.dispose();
        }
    }

    interface DataInit {
        closureId: number;
        closingInformationForCurrentMonth: ClosingInfor | null;
        closingInformationForNextMonth: ClosingInfor | null;
        personalInformationOfSubordinateEmployees: PersonalInfo[];
		overtimeOfSubordinateEmployees: OvertimeSubordinate[];
    }

    interface ClosingInfor {
        processingYm: number;
        closureStartDate: string; // YYYY/MM/DD
        closureEndDate: string;   // YYYY/MM/DD
    }

    interface PersonalInfo {
        personId: string; //"1dec5ac6-e0f8-44c4-8784-c8bc5ccdee6b",
        employeeId: string; //"1e09912b-7edf-454c-89df-9c6da54a8f71",
        businessName: string; //"ＹＳ　００１１",
        gender: 0 | 1 | 2;
        birthday: string; //"2000/01/01",
        employeeCode: string; //"YS0011",
        jobEntryDate: string; //"2020/01/01",
        retirementDate: string; //"9999/12/31"
    }

    interface OverTimeResponse {
        personalInformationOfSubordinateEmployees: PersonalInfoSubordinate[];
        overtimeOfSubordinateEmployees: OvertimeSubordinate[];
    }

    interface PersonalInfoSubordinate {
        personId: string;
        employeeId: string;
        businessName: string;
        gender: 0 | 1 | 2;
        birthday: string; //"2000/01/01",
        employeeCode: string;
        jobEntryDate: string; //"2020/01/01",
        retirementDate: string; //"9999/12/31"
    }

    interface OvertimeSubordinate {
        employeeId: string;
        yearMonth: number;
        agreementTime: {
            agreementTime: number;
            alarmTime: number;
            errorTime: number;
            exceptionLimitTime: number;
        },
        breakdown: {
            overTime: number;
            transferOverTime: number;
            legalHolidayWorkTime: number;
            legalTransferTime: number;
            holidayWorkTime: number;
            transferTime: number;
            flexLegalTime: number;
            flexIllegalTime: number;
            withinPrescribedPremiumTime: number;
            weeklyPremiumTime: number;
            monthlyPremiumTime: number;
            temporaryTime: number;
        },
        agreMax: {
            agreementTime: number;
            alarmTime: number;
            errorTime: number;
            exceptionLimitTime: number;
        },
        maxBreakdown: string | null;
        state: number;
        haveData: boolean;
        closureDate: string | null;
        closureID: number;
    }
}