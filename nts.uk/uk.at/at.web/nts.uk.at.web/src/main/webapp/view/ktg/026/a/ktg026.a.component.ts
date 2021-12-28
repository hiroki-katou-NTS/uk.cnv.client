module nts.uk.at.view.ktg026.a {
    import time = nts.uk.time;

    declare const Chart: any | undefined;

    const API = {
        startScreen: 'screen/at/ktg26/startScreen',
        extractOvertime: 'screen/at/ktg26/extractOvertime'
    };
    // chartjs script cdn
    const SCR_URL = 'https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js';

    // options for chartjs
    const options = (max: number, type: 'head' | 'body' = 'body') => ({
        responsive: true,
        maintainAspectRatio: false,
        legend: {
            display: false
        },
        tooltips: {
            callbacks: {
                label: (item: { value: string }) => {
                    const { format } = time as any;
                    const numb = parseInt(item.value);

                    if (typeof numb === 'number') {
                        return format.byId('Clock_Short_HM', numb);
                    }

                    return '';
                }
            }
        },
        scales: {
            xAxes: [{
                ticks: {
                    min: 0,
                    max: 6000,// Math.min(max, 6000),
                    maxTicksLimit: 4,
                    display: true,
                    beginAtZero: true,
                    callback: function (value: number, index: number, data: number[]) {
                        const { format } = time as any;

                        if (!value) {
                            return '0';
                        }

                        // if (value === 2700) {
                        //     return '45:00';
                        // }

                        return format.byId('Clock_Short_HM', value);
                    },
                    stepSize: 2700,
                    fontSize: 14,
                    fontColor: '#C6C6D1',
                    fontFamily: 'Meiryo UI'
                },
                stacked: true,
                position: 'top',
                afterFit: (scale: any) => {
                    scale.height = type === 'body' ? 0 : 41;
                },
                gridLines: {
                    drawBorder: false,
                    color: '#BFBFBF',
                    z: 1,
                    borderDash: [3, 2],
                    zeroLineColor: 'rgba(0, 0, 0, 0)'
                }
            }],
            yAxes: [{
                ticks: {
                    display: false
                },
                gridLines: { display: false },
                stacked: true
            }]
        }
    });

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
            return 'bg-exceed-special-upperlimit color-exceed-special-upperlimit'
        }

        return '';
    }

    @handler({
        bindingName: 'time',
        validatable: true,
        virtual: false
    })
    export class TimeBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLCanvasElement, valueAccessor: () => number | undefined) {
            const numb = valueAccessor();
            const { format } = time as any;

            if (typeof numb !== 'number') {
                element.innerText = '';

                return;
            }

            element.innerText = format.byId('Clock_Short_HM', numb);
        }
    }

    @handler({
        bindingName: 'ktg-026-chart',
        validatable: true,
        virtual: false
    })
    export class Ktg026ChartBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLCanvasElement, valueAccessor: () => KnockoutObservableArray<DataRow>, allBindingsAccessor: KnockoutAllBindingsAccessor) {
            let chart: any = null;
            const dataSource = valueAccessor();
            const type: 'head' | 'body' | KnockoutObservable<'head' | 'body'> = allBindingsAccessor.get('type');

            const ie = !!navigator.userAgent.match(/MSIE|Trident/g);

            $.Deferred()
                .resolve(true)
                .then(() => _.get(window, 'Chart') || $.getScript(SCR_URL))
                .then(() => {
                    ko.computed({
                        read: () => {
                            const data = ko.unwrap<DataRow[]>(dataSource) || [];
                            const tpe = ko.unwrap<'head' | 'body'>(type) || 'body';

                            // destroy old chart instance
                            if (chart && typeof chart.destroy === 'function') {
                                chart.destroy();
                            }

                            const max = _
                                .chain(data)
                                .reduce((p, c) => {
                                    if (!c) return p;
                                    const { time } = c;
                                    const { ot, wh } = time;
                                    const tt = ot + wh;

                                    return p >= tt ? p : tt;
                                }, 0)
                                .value();

                            // create new chart (draw with new data)
                            chart = new Chart(element.getContext("2d"), {
                                type: 'horizontalBar',
                                data: {
                                    labels: tpe === 'head' ? [] : data.map(({ date }) => date),
                                    datasets: tpe === 'head' ? [] : [{
                                        data: data.map(({ time }) => Math.min(time.ot, 6000)),
                                        backgroundColor: data.map(() => "#99FF66")
                                    }, {
                                        data: data.map(({ time }) => time.wh),
                                        backgroundColor: data.map(() => "#00CC00")
                                    }]
                                },
                                options: options(max || 4800, tpe)
                            });

                            if (ie) {
                                $(element).closest('.widget-container').addClass('ie')
                            } else {
                                $(element).closest('.widget-container').removeClass('ie');
                            }
                        },
                        disposeWhenNodeIsRemoved: element
                    })
                });
        }
    }

    @handler({
        bindingName: 'ktg-chart',
        validatable: true,
        virtual: false
    })
    export class Ktg0267ChartBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLTableCellElement, valueAccessor: () => KnockoutObservableArray<DataRow>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            element.removeAttribute('data-bind');

            if (element.tagName !== 'TD') {
                element.innerText = 'This binding work with only [TD] tag.';

                return { controlsDescendantBindings: false };
            }

            const data = valueAccessor();
            const index = bindingContext.$index();

            if (index !== 0) {
                element.classList.add('hidden');

                return { controlsDescendantBindings: false };
            }

            const canvas = $('<canvas>').appendTo(element).get(0);

            ko.applyBindingsToNode(canvas, { 'ktg-026-chart': data, type: 'body' }, bindingContext);

            ko.computed({
                read: () => {
                    const rows = ko.unwrap<DataRow[]>(data);

                    element.rowSpan = rows.length;
                },
                disposeWhenNodeIsRemoved: element
            });

            return { controlsDescendantBindings: true };
        }
    }

    @component({
        name: 'ktg-026-a',
        template: `
            <div class="ktg-026-a widget-title">
                <table style="width: 100%;">
                    <colgroup>
                        <col width="auto" />
                        <col width="155px" />
                        <col width="65px" />
                    </colgroup>
                    <thead>
                        <tr>
                            <th class="ktg026-fontsize">
                                <!-- A1_1 -->
                                <div class="ktg026-title" data-bind="ntsFormLabel: { required: false, text: $component.$i18n('KTG026_5') }"></div>
                            </th>
                            <th style="padding-right: 5px;font-size: medium;font-weight: normal;">
                                <div id="ktg026-datepick" style="float: right;" data-bind="ntsDatePicker: {
                                    name: $component.$i18n('KTG026_1'),
                                    value: $component.targetYear,
                                    dateFormat: 'YYYY',
                                    valueFormat: 'YYYY',
                                    showJumpButtons: true
                                }"></div>
                                <!-- A1_7 -->
                                <button style="float: right; width: 75px;" class="ktg026-hidden" data-bind="click: $component.close, i18n: 'KTG026_8'"></button>
                            </th>
                            <th>
                                <!-- A1_9 -->
                                <button data-bind="ntsLegendButton: legendOptions"></button>
                            </th>
                        </tr>
                    </thead>
                </table>
            </div>
            <div class="ktg-026-a widget-content ktg026-border-top">
                <table>
                    <colgroup>
                        <col width="auto" />
                        <col width="180px" />
                    </colgroup>
                    <tbody>
                        <tr class="ktg026-hidden">
                            <td colspan="2">
                                <!-- A1_6 -->
                                <label class="ktg026-employee-name" data-bind="text: $component.employeeName"></label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="ktg026-1rem" style="padding-left: 8px;">
                                <!-- A1_5 -->
                                <div data-bind="ntsFormLabel: { required: false, text: $component.textExceededNumber }" style="padding: 0"></div>
                                <label data-bind="text: $component.exceededNumber"></label>
                            </td>
                        </tr>
                        
                    </tbody>
                </table>
            </div>
            <div class="ktg-026-a widget-content widget-fixed scroll-padding ui-resizable ktg026-1rem">
                <div>
                    <table>
                        <colgroup>
                            <col width="25%" />
                            <col width="20%" />
                            <col width="55%" />
                        </colgroup>
                        <head>
                            <tr>
                                <th class="text-center">
                                    <!-- A2_1 -->
                                    <div data-bind="ntsFormLabel: { required: false, text: $component.$i18n('KTG026_7') }"></div>
                                </th>
                                <th class="text-center">
                                    <!-- A2_2 -->
                                    <div style="padding-right: 0px;" data-bind="ntsFormLabel: { required: false, text: $component.$i18n('KTG026_4') }"></div>
                                </th>
                                <td rowspan="1"style="padding-left: 10px;">
                                    <canvas data-bind="ktg-026-chart: $component.dataTable, type: 'head'"></canvas>
                                </td>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="ktg-026-a" data-bind="widget-content: 110, default: 220">
                <div>
                    <table>
                        <colgroup>
                            <col width="45%" />
                            <col width="55%" />
                        </colgroup>
                        <tbody data-bind="foreach: { data: $component.dataTable, as: 'row' }">
                            <tr style="height: 35px;">
                                <td>
                                    <div data-bind="if: row">
                                        <div class="text-center" data-bind="text: row.date" style="float:left; width: 60%;"></div>
                                        <div class="text-right" data-bind="time: row.time.tt, css: row.state" style="height: 25px; padding-right: 5px;"></div>
                                    </div>
                                </td>
                                <td style="padding-left: 10px;" data-bind="ktg-chart: $component.dataTable"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <style>
                .ktg026-border-top {
                    border-top: none !important;
                }
                .ktg026-border-top::before {
                    width: 100%;
                    height: 1px;
                    background: #b1b1b1;
                    content: "";
                    position: absolute;
                    top: 0;
                }
                .ktg026-1rem div.form-label>span.text {
                    font-size: 1rem;
                }
                .ktg026-fontsize div.form-label>span.text {
                    font-size: 1.2rem !important;
                }
                .ktg026-hidden {
                    display: none;
                }
                .ktg-026-a .outside {
                    color: #e05f4e;
                }
                .ktg-026-a .statutory {
                    color: #49bfa8;
                }
                .ktg-026-a.widget-content.ui-resizable {
                    border-top: 0;
                }
                .ktg-026-a.widget-content:not(.ui-resizable) {
                    padding-bottom: 0;
                }
                .ktg-026-a.widget-content:not(.ui-resizable) td {
                    border-bottom: 0;
                }
                .ktg-026-a.widget-content.ui-resizable th,
                .ktg-026-a.widget-content.ui-resizable td {
                    border-bottom: none !important;
                }
                .ktg-026-a.widget-content.ui-resizable td[rowspan] {
                    padding: 0;
                    overflow: hidden;
                }
                .ktg-026-a.widget-content.ui-resizable td[rowspan] canvas {
                    width: 230px !important;
                }
                .ktg-026-a.widget-content.ui-resizable.widget-fixed {
                    padding-bottom: 0px;
                }
                .ktg-026-a.widget-content.ui-resizable.widget-fixed>div {
                    padding-bottom: 0px;
                }
                .ktg-026-a.widget-content.ui-resizable.widget-fixed td[rowspan] canvas {
                    height: 41px !important;
                }
                .widget-container.ie .ktg-026-a.widget-content.ui-resizable td[rowspan] canvas {
                    margin-top: -1px;
                }
                .widget-container.has-scroll .ktg-026-a.scroll-padding {
                    padding-right: 17px !important;
                }
                /* 限度アラーム時間超過 */
                .ktg-026-a.widget-content.ui-resizable table tr td div.exceeding-limit-alarm {
                    background-color: #FFFF99; /* 36協定アラーム */
                    color: #FF9900; /* 36協定アラーム文字 */
                }
                /* 限度エラー時間超過 */
                .ktg-026-a.widget-content.ui-resizable table tr td div.exceeding-limit-error {
                    background-color: #FF99CC; /* 36協定エラー */
                    color: #ffffff; /* 36協定エラー文字 */
                }
                /* 限度アラーム時間超過（特例あり） */
                .ktg-026-a.widget-content.ui-resizable table tr td div.special-exceeding-limit {
                    background-color: #eb9152; /* 36協定特例 */
                }
                /* 限度エラー時間超過（特例あり） */
                .ktg-026-a.widget-content.ui-resizable table tr td div.special-exceeding-limit-error {
                    background-color: #eb9152; /* 36協定特例 */
                }
                /* 特例限度アラーム時間超過 */
                .ktg-026-a.widget-content.ui-resizable table tr td div.special-exceeded-limit-alarm  {
                    background-color: #FFFF99; /* 36協定アラーム */
                    color: #FF9900; /* 36協定アラーム文字 */
                }
                /* 特例限度エラー時間超過 */
                .ktg-026-a.widget-content.ui-resizable table tr td div.special-exceeded-limit-error {
                    background-color: #FF99CC; /* 36協定エラー */
                    color: #ffffff; /* 36協定エラー文字 */
                }
            </style>
            <style data-bind="html: $component.chartStyle"></style>
        `
    })
    export class KTG026AComponent extends ko.ViewModel {
        widget: string = 'KTG026A';

        // A1_2 対象年選択
        targetYear: KnockoutObservable<string> = ko.observable('');
        excessTimes: KnockoutObservable<number> = ko.observable(0);
        // A1_5 超過回数
        exceededNumber!: KnockoutComputed<string>;
        textExceededNumber!: KnockoutComputed<string>;

        // A1_6 社員名
        employeeName: KnockoutObservable<String> = ko.observable('');

        dataTable: KnockoutObservableArray<DataRow> = ko.observableArray([]);
        chartStyle!: KnockoutComputed<string>;

        employeesOvertime!: any;
        isDialog: boolean = false;
        dialogParam: any;

        legendOptions: any;
        cache: any;

        constructor() {
            super();
            const vm = this;

            vm.chartStyle = ko.computed({
                read: () => {
                    const data = ko.unwrap<DataRow[]>(vm.dataTable);

                    if (!data.length) {
                        return '.ktg-026-a.widget-content:not(.widget-fixed) td[rowspan] canvas { height: 0px !important; }';
                    }

                    return `.ktg-026-a.widget-content:not(.widget-fixed) td[rowspan] canvas { height: ${data.length * 35}px !important; }`;
                }
            });

            vm.textExceededNumber = ko.computed({
                read: () => {
                    const year = ko.unwrap<string>(vm.targetYear);

                    return `${vm.$i18n('KTG026_6', [`${year}`])}`;
                }
            });
            vm.exceededNumber = ko.computed({
                read: () => {
                    const time = ko.unwrap<number>(vm.excessTimes);

                    return `${vm.$i18n('KTG026_9', [`${time}`])}`;
                }
            });
        }

        created(param?: any) {
            const vm = this;
            vm.legendOptions = {
                items: [
                    { colorCode: '#99FF66', labelText: vm.$i18n('KTG026_2') },
                    { colorCode: '#00CC00', labelText: vm.$i18n('KTG026_3') },
                ],
                template :
                '<div class="legend-item-label">'
                + '<div style="color: #{colorCode};" data-bind="ntsFormLabel: { required: false }">#{labelText}</div>'
                + '</div>'
            };
            
            const startScreen = () => {
            vm.isDialog = param && !!param.mode;
            vm.dialogParam = vm.isDialog ? param : null;
            const { $user, cache } = vm;
            const { employeeId } = vm.isDialog ? vm.dialogParam:  $user;
            // 1: 従業員参照モード 2: 上長参照モード
            const { currentOrNextMonth, closureId, processDate } = cache;
            const targetDate: any = vm.isDialog ? vm.dialogParam.targetDate : null;
            let targetYear: any = vm.isDialog ? vm.dialogParam.targetYear : null;

            const command = { closureID: closureId, employeeId, targetDate, targetYear, currentOrNextMonth, processingYm: processDate };

            vm
                .$blockui('invisibleView')
                .then(() => vm.$ajax('at', API.startScreen, command))
                .then((response: EmployeesOvertimeDisplay) => {
                    if (!!response) {
                        vm.employeesOvertime = response;
                        const { displayYear } = response;

                        // ???
                        vm.employeeName(response.empInfo.businessName);
                        vm.$window.storage('KTG026_INITIAL_DATA')
                            .then((rs: {isRefresh: boolean, target: any}) => {
                                targetYear = rs && rs.isRefresh && rs.target ? rs.target : null;
                                const year = !_.isNil(targetYear) && !_.isEmpty(targetYear)
                                ? targetYear
                                : displayYear;
    
                                vm.targetYear(year.toString());
                            })

                        vm.displayDataTable(response);
                    }
                })
                .fail(() => vm.dataTable([]))
                .always(() => vm.$blockui('clearView'))
                .always(() => {
                    vm.$nextTick(() => {
                        $(vm.$el)
                            .find('[data-bind]')
                            .removeAttr('data-bind');

                        $('.ktg-026-a.ui-resizable').trigger('wg.resize');
                    });
                });
            };

            // Rq609 is called here, by cache of ccg008
            vm.$window.storage('cache')
                .then(obj => vm.cache = obj)
                .then(() => startScreen());
        }

        mounted() {
            const vm = this;
            const { employeeId } = vm.isDialog ? vm.dialogParam : vm.$user;

            vm.targetYear
                .subscribe((targetYear) => {
                    vm.$validate('#ktg026-datepick').then(valid => {
                        if (!valid || _.isEmpty(targetYear)) return;

                        vm.$window.storage('KTG026_INITIAL_DATA', {
                            isRefresh: false,
                            target: targetYear
                        });
                        const { employeesOvertime } = vm;
                        const { closureID, closingPeriod } = employeesOvertime;
                        const { processingYm } = closingPeriod || { processingYm: moment().format('YYYYMM') };
    
                        const requestBody = {
                            employeeId,
                            targetYear,
                            processingYm,
                            closingId: closureID
                        };
    
                        vm.extractOvertime(requestBody);
                    });
                    
                });

            $('.ktg-026-a.ui-resizable').trigger('wg.resize');
        }

        private extractOvertime(command: any): void {
            const vm = this;

            vm
                .$blockui('invisibleView')
                // 指定する年と指定する社員の時間外時間と超過回数の取得
                .then(() => vm.$ajax('at', API.extractOvertime, command))
                .then((response: EmployeesOvertimeDisplay) => {
                    if (!!response) {
                        vm.displayDataTable(response);
                        vm.excessTimes(response.agreeInfo.excessTimes);
                    }
                })
                .fail(() => vm.dataTable([]))
                .always(() => vm.$blockui('clearView'))
                .always(() => {
                    vm.$nextTick(() => {
                        $(vm.$el)
                            .find('[data-bind]')
                            .removeAttr('data-bind');
                    });
                });
        }

        private displayDataTable(data: EmployeesOvertimeDisplay): void {
            const vm = this;
            // clear table
            vm.dataTable([]);

            vm.$nextTick(() => {
                const { ymOvertimes } = data;

                if (!!ymOvertimes) {
                    const data = _
                        .chain(ymOvertimes)
                        .map(({ yearMonth, agreeTime }) => {
                            const date = moment(yearMonth, 'YYYYMM').format('YYYY/MM');
                            const { agreMax: am, agreementTime: at, state } = agreeTime;

                            return {
                                date,
                                time: {
                                    // tt =  ot + wh
                                    // total = overtime + work with holiday
                                    tt: at.agreementTime || 0,
                                    ot: Math.min(6000, at.agreementTime),
                                    wh: at.agreementTime >= 6000 ? 0 : Math.max((am.agreementTime || 0) - (at.agreementTime || 0), 0)
                                },
                                state: timeStyle(state)
                            };
                        })
                        .value();

                    // Min length to show full height of tooltip. Resolve chartjs's problem
                    if (data.length === 2 || data.length === 3) data.length = 4;
                    if (data.length === 1) data.length = 3;

                    // rebind new data to table & chart
                    vm.dataTable(data);
                }

                vm.$nextTick(() => $('.ktg-026-a.ui-resizable').trigger('wg.resize'));
            });
        }

        close() {
            const vm = this;

            vm.$window.close();
        }
    }

    interface YearMonthOverTime {
        yearMonth: number;
        agreeTime?: AgreeTime;
    }

    interface AgreeTime {
        state: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7;
        agreMax: AgreTime;
        agreementTime: AgreTime;
    }

    interface AgreTime {
        agreementTime: number;
        alarmTime: number;
        errorTime: number;
        exceptionLimitTime: number;
    }

    interface DataRow {
        date: string;
        time: {
            tt: number;
            ot: number;
            wh: number;
        };
        state: string;
    }

    interface EmployeesOvertimeDisplay {
        // ログイン者の締めID
        closureID: number;
        // 対象社員の個人情報
        empInfo: any;
        // 対象社員の各月の時間外時間
        ymOvertimes: YearMonthOverTime[];
        // 対象社員の年間超過回数
        agreeInfo: any;
        // 当月の締め情報
        closingPeriod: any;
        // 当月含む年
        yearIncludeThisMonth: number;
        // 翌月含む年
        yearIncludeNextMonth: number;
        // 表示する年
        displayYear: number;
    }

}