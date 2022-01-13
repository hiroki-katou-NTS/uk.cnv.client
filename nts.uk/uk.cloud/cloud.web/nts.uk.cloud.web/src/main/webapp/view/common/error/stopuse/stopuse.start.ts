module common.error.system {
    __viewContext.ready(function() {
        let screenModel = new ScreenModel();
        screenModel.getMessage().then(function() {
            __viewContext.bind(screenModel);
        });
    });

    class ScreenModel {
        message: KnockoutObservable<string>;

        constructor() {
            this.message = ko.observable("empty!");
        }

        getMessage() {
            let self = this;
            let dfd = $.Deferred<any>();
            nts.uk.request.ajax("com", "operation/stop/message").done(function(data) {
                self.message(data.message);
            }).always(function () {
                dfd.resolve();
            });
            return dfd.promise();
        }

        gotoLogin() {
            nts.uk.ui.windows.rgc().nts.uk.request.login.jumpToUsedLoginPage();
        }
    }
}