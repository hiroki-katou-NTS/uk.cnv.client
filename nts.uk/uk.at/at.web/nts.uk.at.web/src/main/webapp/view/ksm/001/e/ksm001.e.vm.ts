module nts.uk.at.view.ksm001.e {

    import UsageSettingDto = nts.uk.at.view.ksm001.a.service.model.UsageSettingDto;
    import UsageSettingModel = nts.uk.at.view.ksm001.a.viewmodel.UsageSettingModel;

    export module viewmodel {

        export class ScreenModel {
            usageSettingModel: UsageSettingModel;

            constructor() {
                var self = this;
                self.usageSettingModel = new UsageSettingModel();
            }

            /**
            * start page data 
            */
            public startPage(): JQueryPromise<void> {
                var self = this;
                
                var dfd = $.Deferred();

                nts.uk.ui.block.invisible();
                nts.uk.at.view.ksm001.a.service.findCompanySettingEstimate().done(function(data){
                    self.usageSettingModel.updateData(data);
                    dfd.resolve();
                    dfd.resolve();
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
                
                return dfd.promise();
            }
            

            /**
             * call service save button action click
             */
            private saveUsageSettingModel(): void {
                var self = this;

                nts.uk.ui.block.invisible();

                service.saveCompanySettingEstimate(self.usageSettingModel.toDto()).done(function() {
                    // show message 15
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                        // close windows
                        nts.uk.ui.windows.close();
                    });
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error);
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
            }

            /**
             * event on click cancel button.
             */
            private cancelSaveUsageSettingModel(): void {
                nts.uk.ui.windows.close();
            }

        }

    }
}