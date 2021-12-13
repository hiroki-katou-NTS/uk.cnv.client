module nts.uk.at.view.kmk013.h {
    
    import blockUI = nts.uk.ui.block;
    
    export module viewmodel {
        export class ScreenModel {
            itemListH3_2: KnockoutObservableArray<any>;
            selectedIdH3_2: KnockoutObservable<number>;
            
            itemListH4_2: KnockoutObservableArray<any>;
            selectedIdH4_2: KnockoutObservable<number>;
            
            selectedIdH7_2: KnockoutObservable<boolean>;
            
            selectedIdH5_2: KnockoutObservable<boolean>;
            
            itemListH8_2: KnockoutObservableArray<any>;
            selectedIdH8_2: KnockoutObservable<number>;
            
            itemListH9_2: KnockoutObservableArray<any>;
            selectedIdH9_2: KnockoutObservable<number>;

            itemListH4_6: KnockoutObservableArray<any>;
            selectedIdH4_6: KnockoutObservable<number>;

            goOutMaxUsage: KnockoutObservable<number>;
            selectedReasonGoOut: KnockoutObservable<number>;
            goOutReasonOptions: KnockoutObservableArray<any>;
            tempMaxUsage: KnockoutObservable<number>;
            timeTreatTemporarySame: KnockoutObservable<number>;
            isEnabledTempMaxUsage: KnockoutObservable<boolean>;
            
            constructor() {
                const self = this;
                self.itemListH3_2 = ko.observableArray([
                    new BoxModel(1, nts.uk.resource.getText('KMK013_279')),
                    new BoxModel(0, nts.uk.resource.getText('KMK013_280'))
                ]);
                self.selectedIdH3_2 = ko.observable(0);
                
                self.itemListH4_2 = ko.observableArray([
                    new BoxModel(1, nts.uk.resource.getText('KMK013_283')),
                    new BoxModel(0, nts.uk.resource.getText('KMK013_284'))
                ]);
                self.selectedIdH4_2 = ko.observable(0);

                self.selectedIdH7_2 = ko.observable(false);
                
                self.selectedIdH5_2 = ko.observable(false);
                
                self.itemListH8_2 = ko.observableArray([
                    new BoxModel(0, nts.uk.resource.getText('KMK013_300')),
                    new BoxModel(1, nts.uk.resource.getText('KMK013_301'))
                ]);
                self.selectedIdH8_2 = ko.observable(0);
                
                self.itemListH9_2 = ko.observableArray([
                    new BoxModel(1, nts.uk.resource.getText('KMK013_304')),
                    new BoxModel(0, nts.uk.resource.getText('KMK013_305'))
                ]);
                self.selectedIdH9_2 = ko.observable(0);

                self.itemListH4_6 = ko.observableArray([
                  new BoxModel(0, nts.uk.resource.getText("KMK013_576")),
                  new BoxModel(1, nts.uk.resource.getText("KMK013_577"))
                ]);
                self.selectedIdH4_6 = ko.observable(0);

                self.goOutMaxUsage = ko.observable(3);
                self.selectedReasonGoOut = ko.observable(0);
                self.goOutReasonOptions = ko.observableArray(__viewContext.enums.GoingOutReason);
                self.tempMaxUsage = ko.observable(0);
                self.timeTreatTemporarySame = ko.observable(0);
                self.isEnabledTempMaxUsage = ko.observable(true);
            }
            
            startPage(): JQueryPromise<any> {
                const self = this;
                const dfd = $.Deferred();
                blockUI.invisible();
                $.when(service.findByCompanyId(), service.findUsageData(), service.getCreatingDailyResultsCondition())
                .done((stampData: any, usageData: any, isCreatingFutureDay: boolean) => {
                    if (stampData) {
                        self.selectedIdH3_2(stampData.autoStampReflectionClass);
                        self.selectedIdH4_2(stampData.autoStampForFutureDayClass);
                        self.selectedIdH7_2(stampData.goBackOutCorrectionClass == 1);
                        self.selectedIdH5_2(stampData.reflectWorkingTimeClass == 1);
                        self.selectedIdH8_2(stampData.actualStampOfPriorityClass);
                        self.selectedIdH9_2(stampData.breakSwitchClass);
                    }
                    if (usageData.outManage) {
                      self.goOutMaxUsage(usageData.outManage.maxUsage);
                      self.selectedReasonGoOut(usageData.outManage.initValueReasonGoOut);
                    }
                    if (usageData.tempWorkManage) {
                      self.tempMaxUsage(usageData.tempWorkManage.maxUsage);
                      self.timeTreatTemporarySame(usageData.tempWorkManage.timeTreatTemporarySame);
                    }
                    self.selectedIdH4_6(isCreatingFutureDay ? 1 : 0);
                    self.isEnabledTempMaxUsage(usageData.tempWorkUseManageAtr === 1);
                    dfd.resolve();
                }).fail(error => {
                    dfd.reject();
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                });
                return dfd.promise();
            }

            saveData(): void {
                const self = this;
                $('.nts-input').trigger('validate');
                if (nts.uk.ui.errors.hasError()) {
                    return;
                }

                blockUI.invisible();
                let data: any = {
                    autoStampReflectionClass: self.selectedIdH3_2(),
                    autoStampForFutureDayClass: self.selectedIdH4_2(),
                    goBackOutCorrectionClass: self.selectedIdH7_2() ? 1 : 0,
                    reflectWorkingTimeClass: self.selectedIdH5_2() ? 1 : 0,
                    actualStampOfPriorityClass: self.selectedIdH8_2(),
                    breakSwitchClass: self.selectedIdH9_2()
                };
                $.when(service.save(data), self.saveUsageData(), service.saveCreatingFutureDay(self.selectedIdH4_6())).done(() => {
                    nts.uk.ui.dialog.info({messageId: 'Msg_15'}).then(() => {
                        $("#h3_2").focus();
                    });
                }).fail(error => {
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                });
                
            }

            saveUsageData(): JQueryPromise<any> {
              const self = this;
              const goOutData = {
                maxUsage: self.goOutMaxUsage(),
                initValueReasonGoOut: self.selectedReasonGoOut()
              };
              const tempWorkData = {
                maxUsage: self.tempMaxUsage(),
                timeTreatTemporarySame: self.timeTreatTemporarySame()
              };
              return $.when(service.saveGoOutManage(goOutData), service.saveTempWorkManage(tempWorkData));
            }
        }
        
        class BoxModel {
            id: number;
            name: string;
            constructor(id: number, name: string){
                var self = this;
                self.id = id;
                self.name = name;
            }
        }
    }
}