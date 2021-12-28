module nts.uk.com.view.cmm051.b {
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;

    @bean()
    class ViewModel extends ko.ViewModel {
        periodDate: KnockoutObservable<any> = ko.observable(null);
        startDate: KnockoutObservable<any> = ko.observable(null);
        endDate: KnockoutObservable<any> = ko.observable(null);
        required: KnockoutObservable<boolean>;
        isCreate: boolean = false;

        constructor() {
            super();
            let vm = this;
            vm.required = ko.observable(true);
            let prams = getShared('dataToScreenB');
            if (!isNullOrUndefined(prams)) {
                vm.isCreate = prams.isCreate;
                if (vm.isCreate) {
                    vm.periodDate(
                        {
                            startDate: vm.startDate(),
                            endDate: vm.endDate()
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