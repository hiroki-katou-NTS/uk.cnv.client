module nts.uk.at.view.kdl034.a.viewmodel {
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import dialog = nts.uk.ui.dialog;
    import getText = nts.uk.resource.getText;
    import getMessage = nts.uk.resource.getMessage;
    import ApplicationDto = nts.uk.at.view.kaf000.b.viewmodel.model.ApplicationDto;
    import block = nts.uk.ui.block;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
    export class ScreenModel {
        listApprover: KnockoutObservableArray<Approver> = ko.observableArray([]);
        errorFlag: number = -1;
        selectedApproverId: KnockoutObservable<string> = ko.observable(null);
        returnReason: KnockoutObservable<string> = ko.observable("");
        version: number = 0;
        applicationContent: ApplicationDto;
        appID: Array<string> = [];
        sName: string = '';
        constructor() {
            let self = this;
            let param = getShared("KDL034_PARAM");
            self.appID = param.appID;
            self.version = param.version;
        }
        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            block.invisible();
            if (_.isEmpty(self.appID)) {
                return dfd.promise();;
            }
            service.getAppInfoByAppID(self.appID).done(function(result) {
                block.clear();
                if (!result) {
                    return dfd.promise();;
                }
                let approvalFrame = _.sortBy(result.lstApprover, ['phaseOrder']);
                let listApprover: Array<Approver> = [];
                self.sName = result.applicantName;
                let applicant = new Approver(result.applicantId, result.applicantName, null, result.applicantJob, false);
                listApprover.push(applicant);
                let groupByPhase = _.groupBy(approvalFrame, 'phaseOrder');
                let arrayPhase = [];
                for(var key in groupByPhase) {arrayPhase.push(groupByPhase[key])}
                arrayPhase.forEach(function(approverLst) {
                    let name = "";
                    for (let i = 0; i < approverLst.length; i++) {
                        if (result.phaseLogin == 0 || approverLst[i].phaseOrder > result.phaseLogin) {
                            if (approverLst[i].agentFlag) {
                                name = name.concat(getText('KDL034_20', [approverLst[i].approverName]));
                            } else {
                                name = name.concat(approverLst[i].approverName)
                            }
                            if (i != approverLst.length - 1) {
                                name = name.concat('、');
                            }
                        }
                    }
                    if (approverLst.length > 0 && name != "") {
                        listApprover.push(new Approver(approverLst[0].approverID, name,
                            approverLst[0].phaseOrder, approverLst[0].jobTitle, false));
                    }
                });
                // approvalFrame.forEach(function(approver) {
                //     if(result.phaseLogin == 0 || approver.phaseOrder > result.phaseLogin){
                //         listApprover.push(new Approver(approver.approverID, approver.approverName,
                //         approver.phaseOrder, approver.jobTitle, approver.agentFlag));
                //     }
                // });
                self.listApprover(listApprover);
                self.selectedApproverId(result.applicantId);
                dfd.resolve();
            }).fail(function(res: any) {
                block.clear();
            });
            return dfd.promise();
        }

        submitAndCloseDialog() {
            let self = this;
            dialog.confirm({ messageId: "Msg_384" }).ifYes(() => {
                block.invisible();
                let idSelected = self.selectedApproverId().split('__');
                let currentApprover = _.find(self.listApprover(), x => { return x.id === idSelected[0] && x.phaseOrder + "" === idSelected[1] });
                let command = {
                    appID: self.appID,
                    version: self.version,
                    order: currentApprover.phaseOrder,//差し戻し先
                    remandReason: self.returnReason(),//差し戻しコメント
                    applicaintName: self.sName//申請本人の名前
                }
                service.remand(command).done(function(result) {
                    block.clear();
                    let autoSuccessMail: Array<string> = [];
                    let autoFailMail: Array<string> = [];
					let autoFailServer: Array<string> = [];
                    if (result) {
                        // メール送信時のエラーチェック
                        if (result.autoSuccessMail) {
                            _.forEach(result.autoSuccessMail, x => {
                                autoSuccessMail.push(x);
                            });
                        }
                        if (result.autoFailMail) {
                            _.forEach(result.autoFailMail, x => {
                                autoFailMail.push(x);
                            });
                        }
						autoFailServer = result.autoFailServer;
                    }
                    setShared("KDL034_PARAM_RES", command);
                    //情報メッセージ（Msg_223）
                    dialog.info({ messageId: "Msg_223" }).then(() => {
                        self.handleSendMailResult(autoSuccessMail, autoFailMail, autoFailServer);
                    });
                }).fail(function(res) {
                    block.clear();
                    //エラーメッセージ(Msg_197) - sai version
                    dialog.alertError({ messageId: res.messageId }).then(() => {
                        nts.uk.ui.windows.close();
                    });
                });
            }).ifNo(() => {});
        }
        
        handleSendMailResult(autoSuccessMail: Array<string>, autoFailMail: Array<string>, autoFailServer: Array<string>) {
            let numOfSuccess = autoSuccessMail.length;
            let numOfFailed = autoFailMail.length;
			if(!_.isEmpty(autoFailServer)) {
				dialog.alertError({ messageId: "Msg_1057" }).then(() => {
                    nts.uk.ui.windows.close();
                });
				return;
			}
            let sucessListAsStr = "";
            if (numOfSuccess == 0 && numOfFailed == 0) {
                nts.uk.ui.windows.close();
            } else if (numOfSuccess != 0 && numOfFailed == 0) {
                let msg = getMessage('Msg_392');
                dialog.info({ message: msg.replace("{0}", autoSuccessMail.join('\n')), messageId: "Msg_392" }).then(() => {
                    nts.uk.ui.windows.close();
                });
            } else if (numOfSuccess == 0 && numOfFailed != 0) {
                dialog.alertError({ message: getMessage('Msg_769') + "\n" + autoFailMail.join('\n'), messageId: "Msg_769" }).then(() => {
                    nts.uk.ui.windows.close();
                });
            } else {
                let msg = getMessage('Msg_392');
                dialog.info({ message: msg.replace("{0}", autoSuccessMail.join('\n')), messageId: "Msg_392" }).then(() => {
                    dialog.alertError({ message: getMessage('Msg_769') + "\n" + autoFailMail.join('\n'), messageId: "Msg_769" }).then(() => {
                        nts.uk.ui.windows.close();
                    });
                });
            }
        }
        closeDialog() {
            nts.uk.ui.windows.close();
        }

    }
    export class Applicant {
        id: string;
        name: string;
        constructor(id: string, name: string) {
            this.id = id;
            this.name = name;
        }
    }

    export class Approver {
        id: string;
        name: string;
        phaseOrder: number;
        jobTitle: string;
        dispApprover: string;
        idAndPhase: string;
        constructor(id: string, name: string, phaseOrder: number, jobTitle: string, agent: boolean) {
            this.id = id;
            this.name = name;
            this.phaseOrder = phaseOrder;
            this.jobTitle = jobTitle;
            if (_.isNull(phaseOrder)) {//申請者
                this.dispApprover = getText('KDL034_18') + name;
            } else {//フェーズ
                let nameDis = agent == true ? name + ' ' + '代行' : name;
                // this.dispApprover = "フェーズ" + phaseOrder + "の承認者：　" + jobTitle + "　" + nameDis;
                this.dispApprover = getText('KDL034_19', [phaseOrder]) + nameDis;
            }
            this.idAndPhase = id + "__" + phaseOrder;
        }

    }
    interface KDL034_PARAM {
        listApprover: KnockoutObservableArray<Approver>;
    }
}