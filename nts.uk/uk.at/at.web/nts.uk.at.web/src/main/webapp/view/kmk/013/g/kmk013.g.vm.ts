module nts.uk.at.view.kmk013.g {
    
    import blockUI = nts.uk.ui.block;
    
    export module viewmodel {
        export class ScreenModel {
            daysOfWeekOptions: KnockoutObservableArray<any>;

            midNightStartTime: KnockoutObservable<number>;
            midNightEndTime: KnockoutObservable<number>;

            startOfWeek: KnockoutObservable<number>;
            
            constructor() {
                const self = this;
                self.daysOfWeekOptions = ko.observableArray([
                    new ItemModel(1, nts.uk.resource.getText("Enum_DayOfWeek_Monday")),
                    new ItemModel(2, nts.uk.resource.getText("Enum_DayOfWeek_Tuesday")),
                    new ItemModel(3, nts.uk.resource.getText("Enum_DayOfWeek_Wednesday")),
                    new ItemModel(4, nts.uk.resource.getText("Enum_DayOfWeek_Thursday")),
                    new ItemModel(5, nts.uk.resource.getText("Enum_DayOfWeek_Friday")),
                    new ItemModel(6, nts.uk.resource.getText("Enum_DayOfWeek_Saturday")),
                    new ItemModel(7, nts.uk.resource.getText("Enum_DayOfWeek_Sunday")),
                ]);
                
                // Set default setting to Not use
                self.midNightStartTime = ko.observable(0);
                self.midNightEndTime = ko.observable(60);

                self.startOfWeek = ko.observable(0);
            }

            // Start Page
            public startPage(): JQueryPromise<void> {
                const dfd = $.Deferred<void>();
                const self = this;
                blockUI.invisible();
                $.when(
                    service.loadMidnightTime(),
                    service.loadWeekManage(),
                ).done((midnightTime, weekManage) => {
                    if (!_.isEmpty(midnightTime)) {
                        self.midNightStartTime(midnightTime[0].startTime);
                        self.midNightEndTime(midnightTime[0].endTime);
                    }
                    if (weekManage) {
                        self.startOfWeek(weekManage.dayOfWeek);
                    }
                    dfd.resolve();
                }).fail(error => {
                    dfd.reject();
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                    self.midNightStartTime.subscribe(value => {
                        const control = $('#G3_2');
                        if (self.midNightEndTime() < value) {
                            if (nts.uk.ui.errors.errorsViewModel().errors().filter(e => e.$control[0] == control[0]).length == 0)
                                control.ntsError('set', {messageId:"Msg_1022"});
                        } else {
                            control.ntsError('clear');
                            $('#G3_4').ntsError('clear');
                        }
                    });
                    self.midNightEndTime.subscribe(value => {
                        const control = $('#G3_4');
                        if (value < self.midNightStartTime()) {
                            if (nts.uk.ui.errors.errorsViewModel().errors().filter(e => e.$control[0] == control[0]).length == 0)
                                control.ntsError('set', {messageId:"Msg_1022"});
                        } else {
                            control.ntsError('clear');
                            $('#G3_2').ntsError('clear');
                        }
                    });
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
                // Register to DB
                $.when(
                    service.regMidnightTimeMng({start: self.midNightStartTime(), end: self.midNightEndTime()}),
                    service.regWeekManage({dayOfWeek: self.startOfWeek()})
                ).done(() => {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).fail(error => {
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                    $("#G3_2").focus();
                });
            }
        }
        
        // Class ItemModel
        class ItemModel {
            id: number;
            name: string;
            constructor(id: number, name: string) {
                this.id = id;
                this.name = name;
            }
        }
    }
}