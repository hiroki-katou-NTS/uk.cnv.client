module nts.uk.pr.view.ccg007.g {
    export module viewmodel {
        type CallerParameter = service.CallerParameter;

        @bean()
        export class ScreenModel extends ko.ViewModel {
            companyName: KnockoutObservable<string | null> = ko.observable(null);
            employeeCode: KnockoutObservable<string | null> = ko.observable(null);

            constructor(private callerParameter: CallerParameter) {
                super();
            }

            /**
             * Start page.
             */
            mounted() {
                const vm = this;

                vm
                    .$blockui('show')
                    .then(() => {
                        //set infor sendMail
                        vm.companyName(vm.callerParameter.companyName);
                        vm.employeeCode(vm.callerParameter.employeeCode);
                    })
                    .then(() => $('#subSendMail').focus())
                    .always(() => vm.$blockui('clear'));
            }

            /**
             * Submit
             */
            public submit(): void {
                const vm = this;
                const { callerParameter } = vm;

                vm.$blockui('grayout')
                    .then(() => service.submitSendMail(callerParameter))
                    //sendMail
                    .then((data: any[]) => {
                        const [first] = data;

                        if (first && !_.isNil(first.url)) {
                            vm.$dialog.info({ messageId: "Msg_207" }).then(() => vm.closeDialog());
                        } else {
                            vm.closeDialog();
                        }
                    })
                    .fail(function (res) {
                        //Return Dialog Error
                        vm.$dialog.error({ messageId: res.messageId, messageParams: res.parameterIds });
                    })
                    .always(() => vm.$blockui('clear'));
            }

            /**
             * close dialog
             */
            public closeDialog(): void {
                const vm = this;

                vm.$window.close();
            }
        }
    }
}