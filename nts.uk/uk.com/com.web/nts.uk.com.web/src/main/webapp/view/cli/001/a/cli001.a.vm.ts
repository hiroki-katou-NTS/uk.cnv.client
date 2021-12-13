module nts.uk.com.view.cli001.a {
    import LockOutDataUserDto = nts.uk.com.view.cli001.a.service.model.LockOutDataUserDto;
    import LockOutDataDto = nts.uk.com.view.cli001.a.service.model.LockOutDataDto;

    export module viewmodel {

        export class ScreenModel {
            items: KnockoutObservableArray<any>;
            columns: KnockoutObservableArray<any>;
            currentCodeList: KnockoutObservableArray<any>;

            constructor() {
                var self = this;
                self.items = ko.observableArray([]);
                this.columns = ko.observableArray([
                    { headerText: nts.uk.resource.getText(""), key: "userId", dataType: "string", hidden: true },
                    { headerText: nts.uk.resource.getText("CLI001_12"), key: "loginId", dataType: "string", width: 120 },
                    { headerText: nts.uk.resource.getText('CLI001_27'), key: 'companyCode', width: 80 },
                    { headerText: nts.uk.resource.getText('CLI001_28'), key: 'employeeCode', width: 180 },
                    { headerText: nts.uk.resource.getText("CLI001_13"), key: "userName", dataType: "string", width: 170 },
                    { headerText: nts.uk.resource.getText("CLI001_14"), key: "lockOutDateTime", dataType: "string", width: 200 },
                    { headerText: nts.uk.resource.getText("CLI001_15"), key: "logType", dataType: "string", width: 250,
                        formatter: v => v == 1 ? '強制ロック' : '自動ロック'
                    },
                ]);
                this.currentCodeList = ko.observableArray([]);
            }

            /**
             * Start page
             */
            public startPage(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred<any>();
                service.findAll().done((data: Array<LockOutDataUserDto>) => {
                    data = _.uniqBy(data, 'userId');
                    data.forEach(item => {
                        item.lockOutDateTime = moment.utc(item.lockOutDateTime).format('YYYY/MM/DD HH:mm:ss');
                    });

                    _self.items(_.sortBy(data, item => item.loginId));

                    dfd.resolve();
                }).fail((res: any) => {

                    dfd.reject();
                });

                return dfd.promise();
            }

            /**
            * open dialog
            */
            public openDialogAddLockOutData() {
                let _self = this;
                nts.uk.ui.windows.sub.modal("/view/cli/001/b/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared("dataCd001.a");
                    if (!_.isNil(data)) {
                        $('#tableGrid').focus();
                        let userId = { userId: data.userID };
                        service.findByUserId(data.userID).done((dto: LockOutDataDto) => {
                            _self.items.push({ logType: dto.lockType, loginId: data.loginID, userId: dto.userId, userName: data.userName, lockOutDateTime: moment.utc(dto.logoutDateTime).format('YYYY/MM/DD HH:mm:ss')});
                            _self.items(_.sortBy(_self.items(), item => item.loginId));
                            if (!_.isEmpty($('.ntsSearchBox ')[0].value)) {
                                $('.search-btn').click();
                            }
                        });
                    }
                    nts.uk.ui.block.clear();
                });
            }

            /**
            * Set focus
            */
            public setInitialFocus(): void {
                let self = this;

                if (_.isEmpty(self.items())) {
                    $('#add-Lock').focus();
                } else {
                    $('#tableGrid').focus();
                }
            }


            /**
            * Save
            */
            public unLock() {

                var self = this;
                if (_.isEmpty(self.currentCodeList())) {
                    $('#add-Lock').focus();
                    nts.uk.ui.dialog.error({ messageId: "Msg_218", messageParams: [nts.uk.resource.getText('CLI001_25')] });
                }
                else {
                    $('#tableGrid').focus();
                    nts.uk.ui.dialog.confirm({ messageId: "Msg_1347" })
                        .ifYes(() => {
                            let command = { lstUserId: self.currentCodeList() };
                            service.removeLockOutData(command).done(() => {
                                nts.uk.ui.dialog.info({ messageId: 'Msg_221' }).then(() => {
                                    //Search again and display the screen
                                    service.findAll().done((data: Array<LockOutDataUserDto>) => {
                                        data = _.uniqBy(data, 'userId');
                                        data.forEach(item => {
                                            item.lockOutDateTime = moment.utc(item.lockOutDateTime).format('YYYY/MM/DD HH:mm:ss');
                                        });
                                        self.items(_.sortBy(data, item => item.loginId));
                                        self.currentCodeList([]);
                                        if(_.isEmpty(self.items())){
                                            $('#add-Lock').focus();
                                        }
                                    });
                                });
                            });
                        });
                }
            }

        }
    }
}
