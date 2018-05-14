module nts.uk.com.view.cmf003.f {
    
    import getShared = nts.uk.ui.windows.getShared;
    
    export module viewmodel {
        export class ScreenModel {
            // interval 1000ms request to server
            interval: any;
            
            // received storeProcessingId from E
            storeProcessingId: string;
            
            // time when start process
            timeStart: any;
            
            // F1_6
            timeOver: KnockoutObservable<string>;
            
            // F1_7
            status: KnockoutObservable<string>;
            categoryCount: KnockoutObservable<number>;
            categoryTotalCount: KnockoutObservable<number>;
            errorCount: KnockoutObservable<number>;
            
            // F2_2
            dataSaveSetName : string;
            dayStartValue : string;
            dayEndValue : string;
            monthStartValue : string;
            monthEndValue : string;
            yearStartValue : string;
            yearStartValue : string;
            
            // received dataStorageMng from server
            dataStorageMng: KnockoutObservable<string>;
            
            // dialog mode
            dialogMode: KnockoutObservable<string>;
            
            constructor() {
                let self = this;
                var params =  nts.uk.ui.windows.getShared("CMF001_E_PARAMS");
                
                self.startTime = moment.utc();
                self.timeOver = ko.observable('00:00:00');
                self.dataStorageMng = ko.observable({});
                //self.storeProcessingId = params.storeProcessingId;
                self.storeProcessingId = "111111111111111111111111111111111116";
                self.dataSaveSetName = params.dataSaveSetName;
                self.dayStartValue = moment.utc(params.dayValue.startDate, 'YYYY/MM/DD').format("YYYY/MM/DD");
                self.dayEndValue = moment.utc(params.dayValue.endDate, 'YYYY/MM/DD').format("YYYY/MM/DD");
                self.monthStartValue = moment.utc(params.monthValue.startDate, 'YYYY/MM/DD').format("YYYY/MM");
                self.monthEndValue = moment.utc(params.monthValue.endDate, 'YYYY/MM/DD').format("YYYY/MM");
                self.yearStartValue = moment.utc(params.yearValue.startDate, 'YYYY/MM/DD').format("YYYY");
                self.yearStartValue = moment.utc(params.yearValue.endDate, 'YYYY/MM/DD').format("YYYY");
                
                // init F1_7
                self.status = ko.observable('');
                self.categoryCount = ko.observable(0);
                self.categoryTotalCount = ko.observable(0);
                self.errorCount = ko.observable(0);
                self.dialogMode = ko.observable("saving");
            }
            
            //開始
            start(): JQueryPromise<any> {
                let self = this,
                dfd = $.Deferred();

                //データ保存監視処理: 
                self.interval = setInterval(self.confirmProcess(), 1000);
                
                dfd.resolve();
                return dfd.promise();
            }
            
            /**
            * confirm process after 1s
            */
            public confirmProcess(): void {
                let storeProcessingId = self.storeProcessingId;
                service.findDataStorageMng(storeProcessingId).done(function(res: any) {
                    var storageMng = res;
                    
                    // F1_6 set time over 
                    var timeNow = new Date();
                    let over = (self.timeNow.getSeconds()+self.timeNow.getMinutes()*60+ self.timeNow.getHours()*60) - (self.timeStart.getSeconds()+self.timeStart.getMinutes()*60+ self.timeStart.getHours()*60);
                    let time = new Date(null);
                    time.setSeconds(over); // specify value for SECONDS here
                    let result = time.toISOString().substr(11, 8);
                    self.timeOver(result);
                    
                    // F1_7
                    self.status(getStatusEnum(storageMng.operatingCondition));
                    self.categoryCount(storageMng.categoryCount);
                    self.categoryTotalCount(storageMng.categoryTotalCount);
                    self.errorCount(storageMng.errorCount);
                    
                    // update mode when end: DONE, INTERRUPTION_END, ABNORMAL_TERMINATION
                    // 完了, 中断終了, 異常終了
                    if((storageMng.operatingCondition == 4) || (storageMng.operatingCondition == 5) || (storageMng.operatingCondition == 6)) {
                        // stop auto request to server
                        clearInterval(self.interval);
                        
                        // end: update dialog to Error/Interrupt mode
                        if((storageMng.operatingCondition == 5) || (storageMng.operatingCondition == 6)) {
                            self.dialogMode("error_interrupt");
                        }
                        
                        // end: update dialog to complete mode
                        if(storageMng.operatingCondition == 4) {
                            self.dialogMode("done");
                            
                            // comfirm down load when done
                            nts.uk.ui.dialog.confirm({ messageId: "Msg_334" })
                            .ifYes(() => {
                                service.findResultOfSaving(storeProcessingId).done(function(res: any) {
                                    //TODO download by file id 
                                }).fail(function(res: any) {
                                    console.log(res);
                                });                            
                            })
                            .ifNo(() => {
                                return;
                            });
                        }
                        
                        // delete dataStorageMng of process when end
                        let dataStorageMng = new DataStorageMng(storeProcessingId, 0, 0, 0, 0, 0);
                        service.deleteDataStorageMng(dataStorageMng).done(function(res: any) {
                            console.log(res);
                        }).fail(function(res: any) {
                            console.log(res);
                        });
                    }
                }).fail(function(res: any) {
                    console.log(res);
                });
            }
            
            // interrupt process when click button
            public interrupt(): void {
                let self = this;
                let dataStorageMng = new DataStorageMng(self.storeProcessingId, 1, 0, 0, 0, 5);
                
                nts.uk.ui.dialog.confirm({ messageId: "Msg_387" })
                .ifYes(() => {
                    service.setInterruptSaving(dataStorageMng).done(function(res: any) {
                        console.log(res);
                    }).fail(function(res: any) {
                        console.log(res);
                    });
                })
                .ifNo(() => {
                    return;
                });
            }
            
            public download(): void {
                // comfirm down load when click button
                nts.uk.ui.dialog.confirm({ messageId: "Msg_388" })
                .ifYes(() => {
                    service.findResultOfSaving(self.storeProcessingId).done(function(res: any) {
                        //TODO download by file id 
                    }).fail(function(res: any) {
                        console.log(res);
                    });
                })
                .ifNo(() => {
                    return;
                });
            }
        }
        
        class DataStorageMng {
            storeProcessingId : string;
            doNotInterrupt: number;
            categoryCount: number;
            categoryTotalCount: number;
            errorCount: number;
            operatingCondition: number;
        
            constructor(storeProcessingId : string, doNotInterrupt: number, categoryCount: number,
                    categoryTotalCount: number, errorCount: number, operatingCondition: number) {
                this.storeProcessingId = storeProcessingId;
                this.doNotInterrupt = doNotInterrupt;
                this.categoryCount = categoryCount;
                this.categoryTotalCount = categoryTotalCount;
                this.errorCount = errorCount;
                this.operatingCondition = operatingCondition;
            }
        }
        
        function getStatusEnum(value) {
            if (value && value === '0') {
                return getText('Enum_OperatingCondition_INPREPARATION');
            } else if (value && value === '1') {
                return getText('Enum_OperatingCondition_SAVING');
            } else if (value && value === '2') {
                return getText('Enum_OperatingCondition_INPROGRESS');
            } else if (value && value === '3') {
                return getText('Enum_OperatingCondition_DELETING');
            } else if (value && value === '4') {
                return getText('Enum_OperatingCondition_DONE');
            } else if (value && value === '5') {
                return getText('Enum_OperatingCondition_INTERRUPTION_END');
            } else if (value && value === '6') {
                return getText('Enum_OperatingCondition_ABNORMAL_TERMINATION');
            }
        }
    }
}