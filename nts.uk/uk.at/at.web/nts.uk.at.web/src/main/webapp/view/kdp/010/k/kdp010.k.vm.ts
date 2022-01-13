module nts.uk.at.view.kdp010.k {
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import info = nts.uk.ui.dialog.info;
    import error = nts.uk.ui.dialog.error;
    import errors = nts.uk.ui.errors;
    import ajax = nts.uk.request.ajax;

    export module viewmodel {
        const paths: any = {
            getData: "at/record/stamp/timestampinputsetting/stampsettingofRICOHcopier/get",
            save: "at/record/stamp/timestampinputsetting/stampsettingofRICOHcopier/save"
        }
        export class ScreenModel {
            stampSettingOfRICOHCopier = new StampSettingOfRICOHCopier();
            constructor() {
                __viewContext.primitiveValueConstraints.PasswordForRICOH.charType = "Numeric";
            }

            start(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                block.grayout();
                ajax(paths.getData).done(function (data: any) {
                    if (data) {
                        self.stampSettingOfRICOHCopier.update(data);
                    }
                    dfd.resolve();
                    $(document).ready(function () {
                        $('#c-serverCorrectionInterval').focus();
                    });
                }).fail(function (res: any) {
                    error({ messageId: res.messageId });
                }).always(function () {
                    block.clear();
                });
                return dfd.promise();
            }

            checkSetStampPageLayout() {
                let self = this;
                block.grayout();
                ajax(paths.getData).done(function (data: any) {
                    if (data) {
                        self.stampSettingOfRICOHCopier.pageLayoutSettings(data.pageLayoutSettings || []);
                    }
                }).fail(function (res: any) {
                    error({ messageId: res.messageId });
                }).always(function () {
                    block.clear();
                });
            }

            save() {
                $('.nts-input').trigger("validate");
                if (errors.hasError()) {
                    return;
                } else {
                    let self = this;
                    block.grayout();
                    ajax("at", paths.save, ko.toJS(self.stampSettingOfRICOHCopier)).done(function () {
                        info({ messageId: "Msg_15" });
                    }).fail(function (res: any) {
                        error({ messageId: res.messageId });
                    }).always(function () {
                        block.clear();
                    });
                }
            }

            openHDialog() {
                let self = this;
                nts.uk.ui.windows.setShared('STAMP_MEANS', 5);
                nts.uk.ui.windows.sub.modal("/view/kdp/010/h/index.xhtml").onClosed(() => {
                    self.checkSetStampPageLayout();
                });
            }
        }
        class SettingDateTimeClorOfStampScreen {
            textColor: KnockoutObservable<string> = ko.observable("#7F7F7F");
            constructor() { }
            update(data?: any) {
                let self = this;
                if (data) {
                    self.textColor(data.textColor);
                }
            }
        }

        class DisplaySettingsStampScreen {
            serverCorrectionInterval: KnockoutObservable<number> = ko.observable(10);
            settingDateTimeColor = new SettingDateTimeClorOfStampScreen();
            resultDisplayTime: KnockoutObservable<number> = ko.observable(3);
            constructor() { }
            update(data?: any) {
                let self = this;
                if (data) {
                    self.serverCorrectionInterval(data.serverCorrectionInterval);
                    self.resultDisplayTime(data.resultDisplayTime);
                    self.settingDateTimeColor.update(data.settingDateTimeColor);
                }
            }
        }

        class StampSettingOfRICOHCopier {
            displaySettingsStampScreen = new DisplaySettingsStampScreen();
            pageLayoutSettings: KnockoutObservableArray<any> = ko.observableArray([]);
            icCardPassword: KnockoutObservable<string> = ko.observable('00000000');
            constructor() { }
            update(data?: any) {
                console.log(data);
                
                let self = this;
                if (data) {
                    self.displaySettingsStampScreen.update(data.displaySettingsStampScreen);
                    self.pageLayoutSettings(data.pageLayoutSettings || []);
                    if (data.icCardPassword != null && data.icCardPassword !== '') {
                        self.icCardPassword(data.icCardPassword);
                    }
                }
            }
        }
    }
    __viewContext.ready(function () {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.start().done(function () {
            __viewContext.bind(screenModel);
        });
    });
}