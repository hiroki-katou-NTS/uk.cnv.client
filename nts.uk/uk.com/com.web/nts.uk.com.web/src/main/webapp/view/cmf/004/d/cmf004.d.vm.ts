module nts.uk.com.view.cmf004.d {
    export module viewmodel {
        import block = nts.uk.ui.block;
        import close = nts.uk.ui.windows.close;
        import getText = nts.uk.resource.getText;
        import setShared = nts.uk.ui.windows.setShared;
        import getShared = nts.uk.ui.windows.getShared;
        import dialog = nts.uk.ui.dialog;
        export class ScreenModel {
            isSuccess: KnockoutObservable<boolean> = ko.observable(true);

            fileName: KnockoutObservable<string> = ko.observable('');
            fileId: KnockoutObservable<string> = ko.observable('');
            password: KnockoutObservable<string> = ko.observable('');
            processingId: string = nts.uk.util.randomId();
            timeLabel: KnockoutObservable<string>;
            statusLabel: KnockoutObservable<string>;
            statusUpload: KnockoutObservable<string>;
            statusDecom: KnockoutObservable<string>;
            statusCheck: KnockoutObservable<string>;
            timeStart: any;
            constructor() {
                let self = this;
                self.timeStart = new Date();
                self.timeLabel = ko.observable("00:00:00");
                self.statusLabel = ko.observable("");
                self.statusUpload = ko.observable("");
                self.statusDecom = ko.observable("");
                self.statusCheck = ko.observable("");
                let fileInfo = getShared("CMF004_D_PARAMS");
                if (fileInfo) {
                    self.fileId(fileInfo.fileId);
                    self.fileName(fileInfo.fileName);
                    self.password(fileInfo.password);
                }
            }

            startPage(): JQueryPromise<any> {

                var self = this;
                let dfd = $.Deferred();
                let fileInfo = {
                    processingId: self.processingId,
                    fileId: self.fileId(),
                    fileName: self.fileName(),
                    password: self.password()
                };
                service.extractData(fileInfo).done(function(result) {
                    dfd.resolve();
                    block.invisible();
                    self.taskId = result.id;
                    // 1秒おきに下記を実行
                    nts.uk.deferred.repeat(conf => conf
                        .task(() => {
                            return nts.uk.request.asyncTask.getInfo(self.taskId).done(function(res: any) {
                                // update state on screen
                                let status;
                                if (res.taskDatas.length >0){
                                    status = JSON.parse(res.taskDatas[0].valueAsString);
                                    self.statusLabel(getText(status.conditionName));
                                }
                                
                                if (res.succeeded || res.failed) {
                                    if(status){
                                        self.convertToDisplayStatus(status);
                                        if (status.processingType == 3 && status.processingStatus == 2) {
                                            self.isSuccess(true);
                                        } else {
                                            self.isSuccess(false);
                                            if (status.processingStatus == 1) {
                                                dialog.alertError({ messageId: status.messageId });
                                            }
                                        }
                                    }
                                    block.clear();
                                }
                                if (res.running) {
                                    // 経過時間＝現在時刻－開始時刻
                                    self.timeNow = new Date();
                                    let over = (self.timeNow.getSeconds() + self.timeNow.getMinutes() * 60 + self.timeNow.getHours() * 60) - (self.timeStart.getSeconds() + self.timeStart.getMinutes() * 60 + self.timeStart.getHours() * 60);
                                    let time = new Date(null);
                                    time.setSeconds(over); // specify value for SECONDS here
                                    let result = time.toISOString().substr(11, 8);
                                    self.timeLabel(result);
                                    if (status){ 
                                        self.convertToDisplayStatus(status);
                                    }
                                }
                            });
                        }).while(infor => {
                            return infor.pending || infor.running;
                        }).pause(1000));
                }).fail(function(result) {
                    dfd.reject();
                });
                return dfd.promise();
            }

            closeUp() {
                close();
            }
            continueProcessing() {
                let self = this;
                let fileInfo = {
                    fileId: self.fileId(),
                    fileName: self.fileName(), 
                    password: self.password()
                };
                setShared("CMF004_E_PARAMS", {processingId: self.processingId, fileInfo: fileInfo});
                close();
            }
            convertToDisplayStatus(status){
                let self = this;
                if (status.processingType == 1){
                    self.statusUpload(self.convertToName(status.processingStatus));
                }
                if (status.processingType == 2){
                    // If status is 2, upload is compelete
                    self.statusUpload(self.convertToName(2));
                    self.statusDecom(self.convertToName(status.processingStatus));
                }
                if (status.processingType == 3){
                    // If status is 3, upload and extract is compelete
                    self.statusUpload(self.convertToName(2));
                    self.statusDecom(self.convertToName(2));
                    self.statusCheck(self.convertToName(status.processingStatus));
                }
            }
            convertToName(processingType){
                switch (processingType) {
                    //処理中
                    case 0: return getText("CMF004_302");
                    // 失敗
                    case 1: return getText("CMF004_303");
                    // 完了
                    case 2: return getText("CMF004_304");
                }
            }
        }
    }
}