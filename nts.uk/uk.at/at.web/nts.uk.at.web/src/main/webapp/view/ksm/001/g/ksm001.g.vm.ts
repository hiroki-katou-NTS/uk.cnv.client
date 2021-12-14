module nts.uk.at.view.ksm001.g {

    import AggregateSettingDto = service.model.AggregateSettingDto;
    import MonthlyWorkingDaySettingDto = service.model.MonthlyWorkingDaySettingDto;
    import PremiumItemDto = service.model.PremiumItemDto;

    export module viewmodel {

        export class ScreenModel {
            itemsSwap: KnockoutObservableArray<ItemModel>;
            columns: KnockoutObservableArray<any>;
            currentCodeListSwap: KnockoutObservableArray<ItemModel>;
            
            yearHdAtr: KnockoutObservable<boolean>;
            havyHdAtr: KnockoutObservable<boolean>;
            sphdAtr: KnockoutObservable<boolean>;
            halfDayAtr: KnockoutObservable<boolean>;
           
            constructor() {
                let _self = this;
                _self.itemsSwap = ko.observableArray([]);
                _self.currentCodeListSwap = ko.observableArray([]);
    
                _self.columns = ko.observableArray([
                   { headerText: 'コード', key: 'code', width: 100, hidden: true},
                   { headerText: nts.uk.resource.getText('KSM001_92'), key: 'name', width: 100 }
                ]);
    
                _self.yearHdAtr = ko.observable(false);
                _self.havyHdAtr = ko.observable(false);
                _self.sphdAtr = ko.observable(false);
                _self.halfDayAtr = ko.observable(false);
             }

            /**
            * Start page data 
            */
            public startPage(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred();
                
                $.when(_self.loadListPremium()).done(() => {
                    
                    // load setting
                    _self.findAggregateSetting().done(() => {

                        dfd.resolve();
                    });
                });

                return dfd.promise();
            }
            
            /**
             * function on click save CommonGuidelineSetting
             */
            public save(): void {
                let _self = this;

                if (_self.currentCodeListSwap().length == 0) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_676" });
                    return;
                }
                nts.uk.ui.block.invisible();
                service.saveAggregateSetting(_self.toJsObject()).done(() => {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        _self.closeDialog();
                    });
                }).fail((res: any) => {
                    nts.uk.ui.block.clear();
                    
                    // show message error
                    _self.showMessageError(res);
                });
            }

            /**
             * Event on click cancel button.
             */
            public closeDialog(): void {
                nts.uk.ui.windows.close();
            }

            /**
             * toJson data
             */
            private toJsObject(): AggregateSettingDto {
                let _self = this;
                
                let lstPremiumNo: Array<number> = _self.currentCodeListSwap().map(item => item.code);
                
                let monthlyWorkingDaySettingDto: MonthlyWorkingDaySettingDto = new MonthlyWorkingDaySettingDto(_self.getValue(_self.halfDayAtr()),
                    _self.getValue(_self.yearHdAtr()), _self.getValue(_self.sphdAtr()), _self.getValue(_self.havyHdAtr()));
                
                return new AggregateSettingDto(lstPremiumNo, monthlyWorkingDaySettingDto);
            }
            
            /**
             * Find aggregate setting
             */
            private findAggregateSetting(): JQueryPromise<AggregateSettingDto> {
                let _self = this;
                let dfd = $.Deferred();
                
                nts.uk.ui.block.invisible();
                service.findAggregateSetting().done((setting: AggregateSettingDto) => {
                    nts.uk.ui.block.clear();
                    
                    // set data
                    if (setting) {
                        _self.currentCodeListSwap(_self.itemsSwap().filter(item => setting.premiumNo.indexOf(item.code) != -1));
                        _self.halfDayAtr(_self.convertValue(setting.monthlyWorkingDaySettingDto.halfDayAtr));
                        _self.yearHdAtr(_self.convertValue(setting.monthlyWorkingDaySettingDto.yearHdAtr));
                        _self.sphdAtr(_self.convertValue(setting.monthlyWorkingDaySettingDto.sphdAtr));
                        _self.havyHdAtr(_self.convertValue(setting.monthlyWorkingDaySettingDto.havyHdAtr));
                    } else {
                        _self.currentCodeListSwap(_self.itemsSwap());
                        _self.itemsSwap([]);
                    }
                    
                    dfd.resolve();
                });
                 return dfd.promise();
            }
            
            /**
             * Load list Premium
             */
            private loadListPremium(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred();

                nts.uk.ui.block.invisible();
                service.getListPremium().done((data: Array<PremiumItemDto>) => {
                    nts.uk.ui.block.clear();
                    
                    // set data
                    let itemList: Array<ItemModel> = [];
                    _.forEach(data, item => {
                        itemList.push(new ItemModel(item.displayNumber, item.name));
                    });
                    
                    // set data source swap list
                    _self.itemsSwap(itemList);
                    
                    dfd.resolve();
                });
                return dfd.promise();
            }
            
            /**
             * Convert value from boolean to number
             */
            private getValue(value: boolean): number {
                return !value ? UseAtr.NOT_USE : UseAtr.USE;
            }
            
            /**
             * Convert value from number to boolean
             */
            private convertValue(value: UseAtr): boolean {
                return value == UseAtr.USE ? true : false;
            }
            
            /**
             * showMessageError
             */
            private showMessageError(res: any) {
                let dfd = $.Deferred<any>();
                
                // check error business exception
                if (!res.businessException) {
                    return;
                }
                
                // show error message
                if (Array.isArray(res.errors)) {
                    nts.uk.ui.dialog.bundledErrors(res);
                } else {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds });
                }
            }
        }
        
        export class ItemModel {
           code: number;
           name: string;
           constructor(code: number, name: string) {
               this.code = code;
               this.name = name;
           }
       }
        
       //  目安利用条件
       export enum EstimatedCondition {
            // 条件1
            CONDITION_1ST = 1,
            // 条件2
            CONDITION_2ND = 2,
            // 条件3
            CONDITION_3RD = 3,
            // 条件4
            CONDITION_4TH = 4,
            // 条件5
            CONDITION_5TH = 5,
        }
        
        /**
         * するしない区分
         */
        enum UseAtr {
            NOT_USE = 0,
            USE = 1
        }
    }
}