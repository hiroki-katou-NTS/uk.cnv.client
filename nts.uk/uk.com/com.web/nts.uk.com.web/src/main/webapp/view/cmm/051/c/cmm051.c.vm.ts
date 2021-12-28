module nts.uk.com.view.cmm051.c {
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;

    @bean()
    class ViewModel extends ko.ViewModel {
        periodDate: KnockoutObservable<any> = ko.observable(null);
        startDate: KnockoutObservable<any> = ko.observable(moment());
        endDate: KnockoutObservable<any> = ko.observable(moment());
        required: KnockoutObservable<boolean>;
        isUpdate: boolean = false;

        constructor() {
            super();
            let vm = this;
            vm.required = ko.observable(true);

            let prams = getShared('dataToScreenC');
            if (!isNullOrUndefined(prams)) {
                vm.isUpdate = prams.isUpdate;
                if (vm.isUpdate) {
                    vm.periodDate(
                        {
                            startDate: prams.startDate,
                            endDate: prams.endDate
                        });
                }
            }

        }
        created() {
        }
        mounted() {
            $("#daterangepicker").find(".ntsStartDatePicker").focus();
        }

        execution() {
            let vm = this;
            $(".nts-input").trigger("validate");
            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            setShared("dataToScreenA",{
                startDate: vm.periodDate().startDate.format("YYYY/MM/DD"),
                endDate: vm.periodDate().endDate.format("YYYY/MM/DD"),
            });
            nts.uk.ui.windows.close();
        }

        cancel_Dialog(): void {
            nts.uk.ui.windows.close();
        }
    }

}