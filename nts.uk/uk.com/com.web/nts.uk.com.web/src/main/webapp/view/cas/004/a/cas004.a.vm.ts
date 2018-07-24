module nts.uk.com.view.cas004.a {
    import blockUI = nts.uk.ui.block;
    import windows = nts.uk.ui.windows;
    export module viewModel {
        export class ScreenModel {

            userList: KnockoutObservableArray<model.UserDto>;
            comList: KnockoutObservableArray<any>;
            currentCode: KnockoutObservable<any>;
            columns: KnockoutObservableArray<any>;
            companyCode: KnockoutObservable<any>;
            currentUserDto: KnockoutObservable<model.UserDto>;
            currentLoginID: KnockoutObservable<string>;
            currentMailAddress: KnockoutObservable<string>;
            currentUserName: KnockoutObservable<string>;
            currentPass: KnockoutObservable<string>;
            currentPeriod: KnockoutObservable<string>;
            isSpecial: KnockoutObservable<boolean>;
            isMultiCom: KnockoutObservable<boolean>;

            constructor() {

                let self = this;
                self.userList = ko.observableArray([]);
                self.comList = ko.observableArray([]);
                self.currentCode = ko.observable('');
                self.currentCode.subscribe(function(codeChanged) {
                    if (codeChanged == "" || codeChanged == null || codeChanged == undefined)
                        return;
                    let currentUser = self.userList().filter(i => i.userID === codeChanged)[0];
                    self.currentUserDto(currentUser);
                    self.currentLoginID(currentUser.loginID);
                    self.currentMailAddress(currentUser.mailAddress);
                    self.currentUserName(currentUser.userName);
                    self.currentPass(currentUser.password);
                    self.currentPeriod(currentUser.expirationDate);
                    self.isSpecial(currentUser.specialUser);
                    self.isMultiCom(currentUser.multiCompanyConcurrent);
                });
                self.columns = ko.observableArray([
                    { headerText: '', key: 'userID', width: 0, hidden: true },
                    { headerText: nts.uk.resource.getText('CAS004_13'), prop: 'loginID', width: '30%' },
                    { headerText: nts.uk.resource.getText('CAS004_14'), prop: 'userName', width: '70%' }
                ]);
                self.companyCode = ko.observable('');
                self.companyCode.subscribe(function(codeChanged) {
                    if (codeChanged == undefined) {
                        return;
                    }
                    self.companyCode(codeChanged);
                    let currentComId = self.comList().filter(i => i.companyCode === codeChanged)[0].companyId;
                    let dfd = $.Deferred();
                    self.loadUserGridList(currentComId).done(function() {
                        dfd.resolve();
                    });
                });
                self.currentUserDto = ko.observable(null);
                self.currentLoginID = ko.observable('');
                self.currentMailAddress = ko.observable('');
                self.currentUserName = ko.observable('');
                self.currentPass = ko.observable('');
                self.currentPeriod = ko.observable('');
                self.isSpecial = ko.observable(false);
                self.isMultiCom = ko.observable(false);

            }

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                self.loadCompanyList();
                self.loadUserGridList(null);
                dfd.resolve();
                return dfd.promise();
            }

            newMode(): void {
                let self = this;
                nts.uk.ui.errors.clearAll();
                blockUI.clear();
                self.companyCode(null);
                self.resetData();
            }
            
            private resetData() {
                let self = this;
                self.currentCode('');
                self.currentUserDto(null);
                self.currentLoginID('');
                self.currentMailAddress('');
                self.currentUserName('');
                self.currentPass('');
                self.currentPeriod('');
                self.isSpecial(false);
                self.isMultiCom(false);
            }

            register(): void {
                let self = this;

                $('.nts-input').trigger("validate");
                _.defer(() => {
                    if (!$('.nts-editor').ntsError("hasError")) {
                        let userId = self.currentCode();
                        let personalId;
                        if(userId == "" || userId == null || userId == undefined) {
                            let userNew = new model.UserDto(null, self.currentLoginID(), self.currentUserName(), self.currentPass(), self.currentPeriod(), self.currentMailAddress(), personalId, self.isSpecial(), self.isMultiCom());
                            service.registerUser(userNew).done(function(userId) {
                                self.currentCode(userId);
                            });
                        } else {
                            let updateUser = new model.UserDto(self.currentCode(), self.currentLoginID(), self.currentUserName(), self.currentPass(), self.currentPeriod(), self.currentMailAddress(), personalId, self.isSpecial(), self.isMultiCom());
                            service.registerUser(updateUser).done(function(userId) {
                                self.currentCode(userId);
                            });
                        }
                    }
                });
            }

            deleteItem(): void {
                let self = this;
                blockUI.invisible();
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    let userId = self.currentUserDto().userID;
                    let deleteCmd = new model.DeleteCmd(userId, "");
                    service.deleteUser(deleteCmd).done(function() {
                        blockUI.clear();
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
                            blockUI.clear();
                            self.loadUserGridList(null);
                        });
                    });

                }).ifNo(function() {
                    blockUI.clear();
                    return;
                })
            }
            
            openDialogB() {
                let self = this;
                blockUI.invisible();
                let currentComId = null;
                let currentCom = self.comList().filter(i => i.companyCode === self.companyCode())[0]
                if(currentCom != null || currentCom != undefined) {
                    currentComId = currentCom..companyId;
                };
                windows.setShared('companyId', currentComId);
                windows.sub.modal('/view/cas/004/b/index.xhtml', { title: '' }).onClosed(function(): any {
                    //get data from share window
                    var employee = windows.getShared('EMPLOYEE');
                    blockUI.clear();
                });

            }

            private loadCompanyList(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                service.getCompanyImportList().done(function(companies) {
                    let comList: Array<model.CompanyImport> = [];
                    companies.forEach((item) => { comList.push(new model.CompanyImport(item.companyCode, item.companyName, item.companyId)) });
                    self.comList(comList);
                });
                return dfd.promise();
            }

            private loadUserGridList(cid): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                if (cid != null) {
                    service.getUserListByCid(cid).done(function(users) {
                        let userList: Array<model.UserDto> = [];
                        if (users.length != 0) {
                            users.forEach((item) => { userList.push(new model.UserDto(item.userID, item.loginID, item.userName, item.password, item.expirationDate, item.mailAddress, item.personID, item.specialUser, item.multiCompanyConcurrent)) });
                            self.userList(userList);
                            self.currentCode(self.userList()[0].userID);
                            self.currentUserDto(self.userList()[0]);
                        }
                        else {
                            self.userList([]);
                            self.resetData();
                        }
                    });
                } else {
                    service.getAllUser().done(function(users) {
                        let userList: Array<model.UserDto> = [];
                        if (users.length != 0) {
                            users.forEach((item) => { userList.push(new model.UserDto(item.userID, item.loginID, item.userName, item.password, item.expirationDate, item.mailAddress, item.personID, item.specialUser, item.multiCompanyConcurrent)) });
                            self.userList(userList);
                            self.currentCode(self.userList()[0].userID);
                            self.currentUserDto(self.userList()[0]);
                        }
                        else {
                            self.userList(userList);
                            self.resetData();
                        }
                    });
                }
                return dfd.promise();
            }
        }
    }
}