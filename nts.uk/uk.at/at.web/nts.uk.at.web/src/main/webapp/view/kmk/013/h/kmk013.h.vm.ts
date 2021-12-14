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
            }
            
            startPage(): JQueryPromise<any> {
                const self = this;
                const dfd = $.Deferred();
                blockUI.invisible();
                service.findByCompanyId().done((data: any) => {
                    if (data) {
                        self.selectedIdH3_2(data.autoStampReflectionClass);
                        self.selectedIdH4_2(data.autoStampForFutureDayClass);
                        self.selectedIdH7_2(data.goBackOutCorrectionClass == 1);
                        self.selectedIdH5_2(data.reflectWorkingTimeClass == 1);
                        self.selectedIdH8_2(data.actualStampOfPriorityClass);
                        self.selectedIdH9_2(data.breakSwitchClass);
                    }
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
                service.save(data).done(() => {
                    nts.uk.ui.dialog.info({messageId: 'Msg_15'}).then(() => {
                        $("#h3_2").focus();
                    });
                }).fail(error => {
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                });
                
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