module nts.uk.com.view.ccg022.a.screenModel {

    import dialog = nts.uk.ui.dialog.info;
    import text = nts.uk.resource.getText;
    import formatDate = nts.uk.time.formatDate;
    import block = nts.uk.ui.block;
    import jump = nts.uk.request.jump;
    import alError = nts.uk.ui.dialog.alertError;
    import modal = nts.uk.ui.windows.sub.modal;
    import format = nts.uk.text.format;

    export class ViewModel {

        isSystemSelected: KnockoutObservable<number> = ko.observable(1);
        title: KnockoutObservable<string> = ko.observable(text("CCG022_10"));
        isAdmin: KnockoutObservable<boolean> = ko.observable(false);
        systemMode = ko.observableArray([
            //A2_1
            { id: 1, name: text('CCG022_13') },
            //A3_1
            { id: 2, name: text('CCG022_14') },
            //A4_1
            { id: 3, name: text('CCG022_21') },
        ]);
        selectedSystemMode: KnockoutObservable<number> = ko.observable(1);
        infoLbl1: KnockoutObservable<string> = ko.observable("");
        infoLbl2: KnockoutObservable<string> = ko.observable("");
        usageStopMessage: KnockoutObservable<string> = ko.observable("");
        stopMode = ko.observableArray([
            //A4_2
            { id: 1, name: text('CCG022_22') },
            //A4_3
            { id: 2, name: text('CCG022_23') }
        ]);
        selectedStopMode: KnockoutObservable<number> = ko.observable(1);
        stopMessage: KnockoutObservable<string> = ko.observable("");
        isSelectedStop:KnockoutObservable<boolean> = ko.observable(false);
        	
        constructor() {
            let self = this;
            self.isSystemSelected.subscribe((state) => {
                self.title(state ? text("CCG022_10") : text("CCG022_11"));
                if (self.isAdmin()) {
                    self.loadData(state);

                }
            });
            self.selectedSystemMode.subscribe((value) => {
                $("#stop_message_txt").ntsError("clear");
                $("#in_progress_message_txt").ntsError("clear");
                self.isSelectedStop(value==3);
                
                ko.applyBindingsToNode($("#in_progress_message_txt")[0],{ntsMultilineEditor: { value: self.stopMessage, 
                                                                name: '#[CCG022_20]', 
                                                                constraint: 'StopMessage', 
                                                                option: { width: '500px'},
                                                                enable:value==2,
                                                                required:value==2
                                                                }});
                
                ko.applyBindingsToNode($("#stop_message_txt")[0],{ntsMultilineEditor: { value: self.usageStopMessage, 
                                                                name: '#[CCG022_25]', 
                                                                constraint: 'StopMessage', 
                                                                option: { width: '500px'},
                                                                enable:value==3,
                                                                required:value==3
                                                                }});
            });
        }

        loadData(state) {
            let self = this;
            block.invisible();
            service.find(state).done((data) => {
                self.setData(data);
            }).fail((error) => { alError({ messageId: error.messageId, messageParams: error.parameterIds }); })
                .always(() => {
                    block.clear();
                });
        }

        setData(data: IStopSetting) {
            let self = this;
            nts.uk.ui.errors.clearAll()
            if (data) {
                if (!data.admin) {
                    self.isSystemSelected(0);
                    $("#sidebar").ntsSideBar("disable", 0);
                }
                let state = self.isSystemSelected();
                let setting: IStopBySystem = state == 1 ? data.system : data.company;
                self.isAdmin(data.admin);
                self.selectedSystemMode(setting ? setting.systemStatus : 1);
                if (state == 1) {
                    self.infoLbl1(self.genLbl(true, data.stopCompanys));
                    self.infoLbl2(self.genLbl(false, data.inProgressCompanys));
                } else {
                    self.infoLbl1(self.genStopText(data.system));
                    self.infoLbl2("");
                }
                self.usageStopMessage(setting ? setting.usageStopMessage : "");
                self.selectedStopMode(setting ? setting.stopMode : 1);
                self.stopMessage(setting ? setting.stopMessage : "");
                self.isSelectedStop(setting ? setting.systemStatus==3 : false);
            }
        }

        saveData() {
            let self = this,
                command: ISaveStopSettingCommand = {
                    isSystem: self.isSystemSelected(),
                    stopCommand: {
                        systemStatus: self.selectedSystemMode(),
                        stopMessage: self.stopMessage(),
                        stopMode: self.selectedStopMode(),
                        usageStopMessage: self.usageStopMessage(),
                    }
                };
            $("#stop_message_txt,#in_progress_message_txt").trigger("validate");
            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            block.invisible();
            service.save(command).done(() => {
                self.loadData(command.isSystem);
                dialog({ messageId: 'Msg_15' }).then(function() { });
            }).always(() => {
                block.clear();
            });

        }
        genLbl(isStop: boolean, data: Array<IStopByCompany>) {
            let tag = `<span class='limited-label company-code'>{0}</span>`,
                textResult = "";

            let inputText = isStop == true ? "CCG022_30" : "CCG022_31";
            if (data.length) {
                textResult = text(inputText, [format(tag, _.map(data, (item) => { return item.companyCd; }).toString())]);
            }
            return textResult;
        }
        genStopText(setting) {
            let status;
            if (setting == null) {
                return "";
            } else {
                status = setting.systemStatus;
            }
            if (status == 2) {
                return text("CCG022_33");
            }
            if (status == 3) {
                return text("CCG022_32");
            }
            return "";
        }



        startPage(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred(),
                state = self.isSystemSelected();
            block.invisible();
            service.find(state).done((data) => {
                self.setData(data);
            }).fail((error) => { alError({ messageId: error.messageId, messageParams: error.parameterIds }); })
                .always(() => {
                    block.clear();
                });
            dfd.resolve();
            return dfd.promise();
        }
        findSystem() {
            let self = this;
            if (self.isAdmin()) {
                self.isSystemSelected(1);
            }
        }

        findCompany() {
            let self = this;
            if (self.isAdmin()) {
                self.isSystemSelected(0);
            }
        }

    }
    export interface IStopSetting {
        admin: boolean;
        system: IStopBySystem;
        company: IStopByCompany;
        stopCompanys: Array<IStopByCompany>;
        inProgressCompanys: Array<IStopByCompany>;
    }

    export interface IStopBySystem {
        /** 契約コード */
        contractCd: string;
        /** 利用停止モード */
        stopMode: number;
        /** 利用停止のメッセージ */
        usageStopMessage: string;
        /** システム利用状態 */
        systemStatus: number;
        /** 停止予告のメッセージ */
        stopMessage: string;
    }
    export interface IStopByCompany {
        /** 契約コード */
        contractCd: string;
        /** 会社コード */
        companyCd: string
        /** 利用停止モード */
        stopMode: number;
        /** 利用停止のメッセージ */
        usageStopMessage: string;
        /** システム利用状態 */
        systemStatus: number;
        /** 停止予告のメッセージ */
        stopMessage: string;

    }
    export interface ISaveStopSettingCommand {
        isSystem: number;
        stopCommand: IStopCommand;

    }
    export interface IStopCommand {
        /** システム利用状態 */
        systemStatus: number;
        /** 停止予告のメッセージ */
        stopMessage: string;
        /** 利用停止モード */
        stopMode: number;
        /** 利用停止のメッセージ */
        usageStopMessage: string;
    }


}