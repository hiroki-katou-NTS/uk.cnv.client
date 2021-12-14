module nts.uk.at.view.ksc001.h {
    import blockUI = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;

    export module viewmodel {
        export class ScreenModel {
            executionId: KnockoutObservable<string>;
            startDate: KnockoutObservable<string>;
            endDate: KnockoutObservable<string>;
            targetRange: KnockoutObservable<string>;
            detailContentMethod: KnockoutObservableArray<string>;
            executionContent: KnockoutObservableArray<string>;
            executionDateTime: KnockoutObservable<string>;
            exeStart: KnockoutObservable<string>;
            exeEnd: KnockoutObservable<string>;
            executionNumber: KnockoutObservable<string>;
            errorNumber: KnockoutObservable<string>;
            countError: KnockoutObservable<number>;
            countexecutionNumber: KnockoutObservable<number>;
            controlExecution: KnockoutObservable<boolean>;
            controlError: KnockoutObservable<boolean>;
            constructor() {
                var self = this;
                self.executionId = ko.observable('');
                self.startDate = ko.observable('');
                self.endDate = ko.observable('');
                self.targetRange = ko.observable('');
                self.detailContentMethod = ko.observableArray([]);
                self.executionContent = ko.observableArray([]);
                self.exeStart = ko.observable('');
                self.exeEnd = ko.observable('');
                self.executionDateTime = ko.observable('');
                self.executionNumber = ko.observable('');
                self.errorNumber = ko.observable('');
                self.countError = ko.observable(0);
                self.controlExecution = ko.observable(true);
                self.controlError = ko.observable(true);
                self.countexecutionNumber = ko.observable(0);
            }

            /**
             * get data on start page
             */
            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                blockUI.invisible();
                self.executionId(nts.uk.ui.windows.getShared("executionData").executionId);
                self.loadDetailData(self.executionId()).done(function() {
                    dfd.resolve();
                    blockUI.clear();
                });
                $("#fixed-table").ntsFixedTable({});
                return dfd.promise();
            }

            /**
             * Load excution detail data
             */
            private loadDetailData(executionId: string): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                service.findExecutionDetail(executionId).done(function(data: any) {
                    if (data) {
                        //define format datetime string
                        let dateTimeFormat = "YYYY/MM/DD HH:mm:ss";
                        let timeFormat = "HH:mm:ss";
                        self.startDate(data.startDate);
                        self.endDate(data.endDate);
                        self.targetRange = ko.observable(getText("KSC001_46", [data.startDate, data.endDate]));
                        self.detailContentMethod = ko.observableArray(self.loadDetailCreateMethod(data));
                        self.executionContent = ko.observableArray(self.loadDetailContentString(data));
                        self.exeStart = ko.observable(moment.utc(data.executionStart).format(dateTimeFormat));
                        self.exeEnd = ko.observable(moment.utc(data.executionEnd).format(dateTimeFormat));
                        //get diff time execution
                        let diffTime = moment.utc(moment.utc(data.executionEnd, dateTimeFormat).diff(moment.utc(data.executionStart, dateTimeFormat))).format(timeFormat);
                        self.executionDateTime = ko.observable(diffTime);
                        self.executionNumber = ko.observable(getText("KSC001_47", [data.countExecution]));
                        self.errorNumber = ko.observable(getText("KSC001_47", [data.countError]));
                        self.countError(data.countError);
                        self.countexecutionNumber(data.countExecution);
                        data.countExecution == 0 ? self.controlExecution(false) : self.controlExecution(true);
                        data.countError == 0 ? self.controlError(false) : self.controlError(true);
                    }
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
             * open EmployeeTargetListDialog
             */
            openEmployeeTargetListDialog(): void {
                let self = this;
                if (self.controlExecution()) {
                    //send data to dialog
                    var data: any = {
                        executionId: self.executionId(),
                        startDate: self.startDate(),
                        endDate: self.endDate()
                    }
                    blockUI.invisible();
                    nts.uk.ui.windows.setShared('dataFromDetailDialog', data);
                    nts.uk.ui.windows.sub.modal("/view/ksc/001/i/index.xhtml", { dialogClass: "no-close" }).onClosed(() => {
                    });
                    blockUI.clear();
                }
            }

            /**
             * open ErrorContentDialog
             */
            openErrorContentDialog(): void {
                let self = this;
                if (self.controlError()) {
                    blockUI.invisible();
                    //send data to dialog
                    var data: any = {
                        executionId: self.executionId(),
                        startDate: self.startDate(),
                        endDate: self.endDate(),
                        countError: self.countError()
                    }
                    nts.uk.ui.windows.setShared('dataFromDetailDialog', data);
                    nts.uk.ui.windows.sub.modal("/view/ksc/001/k/index.xhtml", { dialogClass: "no-close" }).onClosed(() => {
                    });
                    blockUI.clear();
                }
            }

            /**
            * close dialog 
            */
            closeDialog(): void {
                let self = this;
                nts.uk.ui.windows.close();
            }

            /**
             * function to load detail content string
             */
            private loadDetailContentString(data: any): string[] {
                let self = this;
                let str: string[] = [];
                let spaceString = "　";
                //実施区分= 通常作成
                if (data.implementAtr == ImplementAtr.GENERALLY_CREATED) {
                    str.push(getText("KSC001_74"));
                } else {//実施区分= 再作成
                    str.push(getText("KSC001_75"));
                    if (!data.reTargetAtr) {
                        str.push(getText("KSC001_37") + getText("KSC001_89"));
                    } else {
                        str.push(getText("KSC001_37") + getText("KSC001_90"));
                        if (data.transfer) {
                            str.push("　" + getText("KSC001_38") + getText("KSC001_91"));
                        }
                        if (data.leaveOfAbsence) {
                            str.push("　" + getText("KSC001_38") + getText("KSC001_92"));
                        }
                        if (data.shortWorkingHours) {
                            str.push("　" + getText("KSC001_38") + getText("KSC001_93"));
                        }
                        if (data.changedWorkingConditions) {
                            str.push("　" + getText("KSC001_38") + getText("KSC001_94"));
                        }
                    }
                    if (data.reOverwriteConfirmed || data.reOverwriteRevised) {
                        str.push(getText("KSC001_37") + getText("KSC001_104"));
                        if (data.reOverwriteConfirmed) {
                            str.push("　" + getText("KSC001_38") + getText("KSC001_105"));
                        }
                        if (data.reOverwriteRevised) {
                            str.push("　" + getText("KSC001_38") + getText("KSC001_106"));
                        }
                    }
                }

                return str;
            }

            /**
             * function to detail create method
             */
            private loadDetailCreateMethod(data: any): string[] {
                let self = this;
                let str: string[] = [];
                if (data.createMethodAtr == 0) {
                    str.push(getText("KSC001_22"));
                } else if (data.createMethodAtr == 1) {
                    str.push(getText("KSC001_23"));
                    if (data.referenceMaster == 0) {
                        str.push(getText("KSC001_37") + getText("KSC001_113") + getText("KSC001_114") + getText("KSC001_108"));
                    } else if (data.referenceMaster == 1) {
                        str.push(getText("KSC001_37") + getText("KSC001_113") + getText("KSC001_114") + getText("KSC001_109"));
                    } else if (data.referenceMaster == 2) {
                        str.push(getText("KSC001_37") + getText("KSC001_113") + getText("KSC001_114") + getText("KSC001_110"));
                    } else if (data.referenceMaster == 3) {
                        str.push(getText("KSC001_37") + getText("KSC001_113") + getText("KSC001_114") + getText("KSC001_111"));
                        str.push(getText("KSC001_37") + getText("KSC001_111") + data.monthlyPatternCode + "　" + data.monthlyPatternName);
                    }
                } else if (data.createMethodAtr == 2) {
                    str.push(getText("KSC001_24"));
                    str.push(getText("KSC001_37") + getText("KSC001_26") + getText("KSC001_114") + data.copyStartDate);
                }
                if (data.confirm) {
                    str.push(getText("KSC001_17"));
                }
                return str;
            }

        }

        // 実施区分
        export enum ImplementAtr {
            // 通常作成
            GENERALLY_CREATED = 0,
            // 再作成
            RECREATE = 1
        }

        // 再作成区分
        export enum ReCreateAtr {
            // 全件
            ALLCASE = 0,
            // 未確定データのみ
            ONLYUNCONFIRM = 1
        }

        // 作成方法区分
        export enum CreateMethodAtr {
            // 個人情報
            PERSONAL_INFO = 0,
            // パターンスケジュール
            PATTERN_SCHEDULE = 1,
            // 過去スケジュールコピー
            COPY_PAST_SCHEDULE = 2
        }

        // 処理実行区分
        export enum ProcessExecutionAtr {
            // もう一度作り直す 
            REBUILD = 0,
            // 再設定する
            RECONFIG = 1
        }

        // 利用区分
        export enum UseAtr {
            // 使用しない
            NOTUSE = 0,
            // 使用する
            USE = 1
        }
    }
}