module nts.uk.at.kha003.b {

    const API = {
        getData: 'at/screen/kha003/b/get-data'
    };

    @bean()
    export class ViewModel extends ko.ViewModel {
        enable: KnockoutObservable<boolean>;
        required: KnockoutObservable<boolean>;
        dateValue: KnockoutObservable<any>;
        startDateString: KnockoutObservable<string>;
        endDateString: KnockoutObservable<string>;
        isCsvOutPut: KnockoutObservable<boolean>;
        totalUnit: KnockoutObservable<any>;
        code: string;

        constructor() {
            super();
            const vm = this;
            vm.enable = ko.observable(true);
            vm.required = ko.observable(true);
            var date = new Date();
            var firstDay = new Date(date.getFullYear(), date.getMonth(), 1).toDateString();
            var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0).toDateString();
            vm.startDateString = ko.observable("");
            vm.endDateString = ko.observable("");
            vm.dateValue = ko.observable({startDate: vm.formatDate(firstDay), endDate: vm.formatDate(lastDay)});
            vm.isCsvOutPut = ko.observable(false);
            vm.totalUnit = ko.observable(null);
            vm.startDateString.subscribe(function (value) {
                vm.dateValue().startDate = value;
                vm.dateValue.valueHasMutated();
            });
            vm.endDateString.subscribe(function (value) {
                vm.dateValue().endDate = value;
                vm.dateValue.valueHasMutated();
            });
        }

        public formatDate(date: any) {
            var d = new Date(date),
                month = '' + (d.getMonth() + 1),
                day = '' + d.getDate(),
                year = d.getFullYear();

            if (month.length < 2)
                month = '0' + month;
            if (day.length < 2)
                day = '0' + day;

            return [year, month, day].join('/');
        }

        public startOfMonth(date: any): any {
            return new Date(date.getFullYear(), date.getMonth(), 1).toDateString();

        }

        created() {
            const vm = this;
            _.extend(window, {vm});
            vm.$window.storage('kha003AShareData').done((data: any) => {
                vm.isCsvOutPut(data.isCsvOutPut);
                vm.totalUnit(data.totalUnit);
                vm.code = data.code;
                window.setTimeout(function () {
                    document.getElementById('btn-f-close').focus();
                }, 0);
            })
        }

        mounted() {
            const vm = this;
        }

        /**
         * Event on click cancel button.
         */
        public cancel(): void {
            const vm = this;
            let shareData = {
                isCancel: true
            }
            vm.$window.storage('kha003BShareData', shareData).then(() => {
                nts.uk.ui.windows.close();
            })
        }

        /**
         * Event on click decide button.
         */
        public decide(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred<any>();
            //  $("#I6_3").focus();
            vm.$blockui("invisible");
            let command = {
                startDate: vm.totalUnit() == 0 ? vm.dateValue().startDate : null,
                endDate: vm.totalUnit() == 0 ? vm.dateValue().endDate : null,
                yearMonthStart: vm.totalUnit() == 1 ? vm.dateValue().startDate.replace('/','') : null,
                yearMonthEnd: vm.totalUnit() == 1 ? vm.dateValue().endDate.replace('/','') : null,
                totalUnit: vm.totalUnit()
            };
            vm.$ajax(API.getData, command).then(data => {
                data.datePriod = vm.dateValue().startDate + " ~ " + vm.dateValue().endDate;
                data.isCancel = false;
                data.dateRange=vm.dateValue();
                vm.$window.storage('kha003BShareData', data).then(() => {
                    nts.uk.ui.windows.close();
                });
                dfd.resolve();
            }).fail(err => {
                dfd.reject();
            }).always(() => vm.$blockui("clear"));
            if (!vm.isCsvOutPut()) {
                vm.$window.modal("/view/kha/003/c/index.xhtml").then(() => {

                });
            }
            return dfd.promise();
        }
    }
}


