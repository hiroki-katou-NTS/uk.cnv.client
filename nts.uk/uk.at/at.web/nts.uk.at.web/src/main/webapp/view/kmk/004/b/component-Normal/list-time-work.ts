/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmk004.b {

    interface Params {
    }

    const API = {
        GET_WORK_TIME: 'screen/at/kmk004/getWorkingHoursByCompany'
    };

    const TYPE = 0;

    const template = `
        <div class="list-time">
            <table>
                <tbody>
                    <tr>
                        <td class= "label-row1">
                            <div data-bind="i18n: 'KMK004_221'"></div>
                        </td>
                        <td class= "label-row2">
                            <div data-bind="i18n: 'KMK004_222'"></div>
                        </td>
                    </tr>
                    <!-- ko foreach: workTimes -->
                    <tr>
                        <!-- ko if: check -->
                            <td>
                                Chung dep trai
                            </td>
                        <!-- /ko -->
                        <td class="label-column1" data-bind="text: $data.month"></td>
                        <td class="label-column2">
                            <input type="number" maxlength="6" class="lable-input" data-bind="ntsTextEditor: {value: $data.legalLaborTime}" />
                        </td>
                    </tr>
                    <!-- /ko -->
                    <tr>    
                        <td class="label-column1">
                            <div data-bind="i18n: 'KMK004_223'"></div>
                        </td>
                        <td class="label-column2">
                            <div data-bind="text: total"></div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    `;

    @component({
        name: 'time-work',
        template
    })

    class ListTimeWork extends ko.ViewModel {

        public check: KnockoutObservable<boolean> = ko.observable(true);
        public check1: KnockoutObservable<boolean> = ko.observable(true);
        public workTimes: KnockoutObservableArray<WorkTime> = ko.observableArray([]);
        public total: KnockoutObservable<number| null> = ko.observable(null);


        created() {
            const vm = this;
            const input = { workType: TYPE, year: 2020 };
            var s = 0;

            vm.init();

            vm.$ajax(API.GET_WORK_TIME, input)
                .then((data: IWorkTime[]) => {
                    if (data.length > 0) {
                        vm.workTimes(data.map(m => new WorkTime({ ...m, parrent: vm.workTimes })));
                    }
                });

            vm.workTimes.subscribe((wts) => {
                
                const total:number = wts.reduce((p, c) => p += Number(c.legalLaborTime()), 0);
                if (total > 0) {
                    vm.total(total)
                }
            });
        }

        init() {
            const vm = this;
            const IWorkTime1:IWorkTime[] = [{ check: true, month: "1月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "2月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "3月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "4月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "5月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "6月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "7月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "8月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "9月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "10月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "11月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null },
            { check: true, month: "12月度", legalLaborTime: null, withinLaborTime: null, weekAvgTime: null }];

            vm.workTimes(IWorkTime1.map(m => new WorkTime({ ...m, parrent: vm.workTimes })));
        }
    }

    interface IWorkTime {
        check: boolean;
        month: string;
        legalLaborTime: number;
        withinLaborTime: number;
        weekAvgTime: number;
    }

    class WorkTime {
        check: KnockoutObservable<boolean> = ko.observable(false);
        month: KnockoutObservable<string> = ko.observable('');
        legalLaborTime: KnockoutObservable<number | null> = ko.observable(null);
        withinLaborTime: KnockoutObservable<number | null> = ko.observable(null);
        weekAvgTime: KnockoutObservable<number | null> = ko.observable(null);

        constructor(params?: IWorkTime & { parrent: KnockoutObservableArray<WorkTime> }) {
            const md = this;

            md.create(params);

            this.legalLaborTime.subscribe(c => params.parrent.valueHasMutated());
        }

        public create(param?: IWorkTime) {
            this.check(param.check)
            this.month(param.month);
            this.legalLaborTime(param.legalLaborTime);
            this.withinLaborTime(param.withinLaborTime);
            this.weekAvgTime(param.weekAvgTime);
        }
    }
}
