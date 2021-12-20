/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.b {

    const API = {
        export: "ctx/at/schedule/personal/by-individual/export",
    };

    @bean()
    export class ViewModel extends ko.ViewModel {
        model: Model = new Model();
        roundingRules: KnockoutObservableArray<any>;
        datePeriod: any;
        enableTotal : KnockoutObservable<boolean>;

        constructor() {
            super();
            const vm = this;
            vm.roundingRules = ko.observableArray([
                {code: false, name: vm.$i18n('KSU002_54')},
                {code: true, name: vm.$i18n('KSU002_55')}
            ]);
            vm.datePeriod = ko.observable("");
            vm.enableTotal = ko.observable(false);
        }

        created() {
            const vm = this;
            _.extend(window, {vm});
            vm.$window.storage("ksu002B_params").done((data) => {
                vm.model.employeeCode(data.employeeCode);
                vm.model.employeeName(data.employeeName);
                vm.model.periodStart(data.startDate);
                vm.model.periodEnd(data.endDate);
                vm.model.targetDate(data.targetDate);
                vm.model.startDay(data.startDay);
                vm.datePeriod(vm.getDatePeriod());
                vm.enableTotal(data.isStartingDayOfWeek);
            });

            vm.loadTotalDislay();

            /*  $(document).ready(function () {
                  setTimeout(function () {
                     // $('#B4_2').children(":first").children(":first").focus();
                      if (vm.model.isTotalDisplay()) {
                          $($('#B4_2').children()[1]).first().focus();
                      } else {
                          $('#B4_2').first().first().focus();
                      }
                  }, 500);
              });*/
        }

        getDatePeriod(): string {
            let vm = this;
            return vm.model.periodStart() + " " + vm.$i18n('KSU002_51') + " " + vm.model.periodEnd()
        }

        mounted() {
            const vm = this;
        }

        loadTotalDislay(): void {
            let vm = this;
            vm.$window.storage("ksu002B_old").then((data) => {
                if (ko.unwrap(vm.enableTotal())) {
                    if (_.has(data, 'isTotalDisplay')) {
                        vm.model.isTotalDisplay(data.isTotalDisplay);
                        setTimeout(function () {
                            $('#B4_2 input:radio:checked').focus();
                        }, 500);
                    }
                }
            });
        }

        close() {
            let vm = this;
            let shareData = {
                isTotalDisplay: vm.model.isTotalDisplay()
            };
            vm.$window.storage("ksu002B_old", shareData).then(() => {
            })
        }

        public exportFile(): void {
            let vm = this;
            let query = {
                employeeCode: vm.model.employeeCode(),
                employeeName: vm.model.employeeName(),
                date: vm.model.targetDate(),
                period: {
                    startDate: vm.model.periodStart(),
                    endDate: vm.model.periodEnd()
                },
                startDate: vm.model.startDay(),
                isTotalDisplay: vm.model.isTotalDisplay(),

            };
            vm.$blockui("invisible");
            nts.uk.request.exportFile(API.export, query).done((success: any) => {
                vm.close();
            }).fail((error: any) => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("clear");
            });
        }

        public closeDialog(): void {
            const vm = this;
            vm.$window.close();
        }
    }

    class Model {
        /** 社員コード*/
        employeeCode: KnockoutObservable<string>;
        /** 社員名*/
        employeeName: KnockoutObservable<string>;
        /** 期間_開始         */
        periodStart: KnockoutObservable<string>;
        /** 期間_終了*/
        periodEnd: KnockoutObservable<string>;
        /**週合計判定*/
        isTotalDisplay: KnockoutObservable<boolean>;

        /** 対象年月*/
        targetDate: KnockoutObservable<string>;
        /**開始曜日*/
        startDay: KnockoutObservable<any>;

        constructor() {
            this.employeeCode = ko.observable("");
            this.employeeName = ko.observable("");
            this.periodStart = ko.observable("");
            this.periodEnd = ko.observable("");
            this.targetDate = ko.observable("");
            this.startDay = ko.observable("");
            this.isTotalDisplay = ko.observable(false);
        }
    }
}