module common.error.system {
    @bean()
    export class ScreenModel extends ko.ViewModel {
        constructor() {
            super();

            const vm = this;

            vm.$window.header(false);
        }

        public contractEntered() {
            var contract: ContractInfo = {
                tenantCode: $("#tenantCode").val(),
                tenantPassword: $("#tenantPass").val(),
                issueUrl: location.href,
                requestUrl: this.getParam("requestUrl", location.href)
            }
            this.contractAuthentication(contract);
        }

        gotoLogin() {
            nts.uk.characteristics
                .restore("loginMode")
                .done(mode => {
                    let rgc = nts.uk.ui.windows.rgc();

                    if (mode) {
                        rgc.nts.uk.request.login.jumpToUsedSSOLoginPage();
                    } else {
                        rgc.nts.uk.request.login.jumpToUsedLoginPage();
                    }
                });
        }
    }
}