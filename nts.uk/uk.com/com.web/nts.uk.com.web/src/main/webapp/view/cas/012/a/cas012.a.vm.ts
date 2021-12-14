module nts.uk.com.view.cas012.a.viewmodel {
    import block = nts.uk.ui.block;
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;

    export class ScreenModel {

        // Metadata
        isCreate: KnockoutObservable<boolean> = ko.observable(false);

        //ComboBox RollType
        listRoleType: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedRoleType: KnockoutObservable<number> = ko.observable(null);

        //ComboBox Company
        listCompany: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedCompany: KnockoutObservable<string> = ko.observable('');

        //list Role Individual Grant    
        listRoleIndividual: KnockoutObservableArray<RoleIndividualDto> = ko.observableArray([]);
        columns: KnockoutObservableArray<any>;
        currentCode: KnockoutObservable<any> = ko.observable();
        selectRoleIndividual: KnockoutObservable<RoleIndividual>;

        // Valid Period
        datePeriod: KnockoutObservable<any> = ko.observable({});

        constructor() {
            var self = this;
            self.columns = ko.observableArray([
                { headerText: 'GUID', key: 'GUID', width: 100, hidden: true },
                { headerText: nts.uk.resource.getText("CAS012_13"), key: 'loginID', width: 120, columnCssClass: "colStyle" },
                { headerText: nts.uk.resource.getText("CAS012_14"), key: 'userName', width: 150, columnCssClass: "colStyle" },
                { headerText: nts.uk.resource.getText("CAS012_15"), key: 'datePeriod', width: 230 }
            ]);
            self.selectRoleIndividual = ko.observable(self.buildNewRoleIndividual());
        }

        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            service.getMetadata().done((data) => {
                if (data) {
                    self.listRoleType(data.enumRoleType);
                    self.listCompany(data.listCompany);
                    // Select first item
                    self.selectedRoleType(data.enumRoleType[0].value);
                    self.selectedCompany(data.listCompany[0].companyId);

                    self.getData().done(() => {
                        self.createSubscribe();
                        self.selectRoleByIndex(0);

                    });
                } else {
                    nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                }
                dfd.resolve();
            });

            return dfd.promise();
        }

        private createSubscribe(): void {
            var self = this;
            self.selectedRoleType.subscribe((value) => {
                block.invisible();
                self.getData().done(() => {
                    self.selectRoleByIndex(0);
                    block.clear();
                });
            });
            self.selectedCompany.subscribe((value) => {
                block.invisible();
                self.getData().done(() => {
                    self.selectRoleByIndex(0);
                    block.clear();
                });
            });
            self.currentCode.subscribe((value) => {
                self.findRoleById(value);
            });
        }


        private exportExcel(): void {
            let self = this;
            let params: any = {
                date: null,
                mode: 1
            };

            setShared("CDL028_INPUT", params);

            nts.uk.ui.windows.sub.modal("/view/cdl/028/a/index.xhtml").onClosed(function() {
                var result = getShared('CDL028_A_PARAMS');
                if (result.status) {
                    nts.uk.ui.block.grayout();
                    let date = moment(result.standardDate, "YYYY/MM/DD");
                    service.exportExcel(date).done(function() {

                    }).fail(function(error) {
                        nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                    }).always(function() {
                        nts.uk.ui.block.clear();
                    });
                }
            });

        }

        openCAS012_B() {
            let self = this;
            nts.uk.ui.windows.sub.modal("/view/cas/012/b/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("UserInfo");
                if (data !== undefined) {
                    self.selectRoleIndividual().userID(data.decisionUserID);
                    self.selectRoleIndividual().userName(data.decisionName);
                }
            });
        }

        openCAS012_C() {
            let self = this
            nts.uk.ui.windows.sub.modal("/view/cas/012/c/index.xhtml").onClosed(() => {
                let returnDataScreenC = nts.uk.ui.windows.getShared("ReturnData");
                self.selectedCompany(returnDataScreenC.decisionCompanyID);
            });
        }

        createBtn() {
            let self = this;
            self.isCreate(true);
            self.currentCode("");
            self.selectRoleIndividual().userName("");
            self.datePeriod({});
            nts.uk.ui.errors.clearAll();
        }

        registryBtn() {
            let self = this;
            $(".nts-input").trigger("validate");
            if (!$(".nts-input").ntsError("hasError")) {
                if (self.isCreate()) {
                    self.createRole();
                } else {
                    self.updateRole();
                }
            }
        }

        private createRole(): void {
            var self = this;
            var param: RoleIndividualGrantBaseCommand = new RoleIndividualGrantBaseCommand(self.selectRoleIndividual());
            param.roleType = self.selectedRoleType();
            param.companyID = self.selectedCompany();
            param.startValidPeriod = nts.uk.time.parseMoment(self.datePeriod().startDate).format();
            param.endValidPeriod = nts.uk.time.parseMoment(self.datePeriod().endDate).format();

            block.invisible();
            if (self.selectedRoleType() == 0) {
                nts.uk.ui.windows.sub.modal("/view/cas/012/c/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared("CAS012CResult");
                    param.setRoleAdminFlag = data.setRoleAdminFlag;
                    param.decisionCompanyID = data.decisionCompanyID;
                    if (!data.isCancel) {
                        self.createRoleProcess(param);
                    }
                });
            } else {
                self.createRoleProcess(param);
            }
        }

        private createRoleProcess(param: RoleIndividualGrantBaseCommand): void {
            var self = this;
            if (nts.uk.text.isNullOrEmpty(param.userID)) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_218", messageParams: [nts.uk.resource.getText("CAS012_24")] })
                block.clear();

            } else {
                service.create(param).done((data: any) => {
                    if (nts.uk.text.isNullOrEmpty(param.userID)) {
                    }
                    self.getData().done(() => {
                        self.selectRoleByKey(data.companyID, data.userID, data.roleType);
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    });
                }).fail((res) => {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                }).always(() => {
                    block.clear();
                });
            }
        }

        private updateRole(): void {
            var self = this;
            var param: RoleIndividualGrantBaseCommand = new RoleIndividualGrantBaseCommand(self.selectRoleIndividual());
            param.startValidPeriod = nts.uk.time.parseMoment(self.datePeriod().startDate).format();
            param.endValidPeriod = nts.uk.time.parseMoment(self.datePeriod().endDate).format();

            block.invisible();
            service.update(param).done(() => {
                self.getData().done(() => {
                    self.selectRoleByKey(param.companyID, param.userID, param.roleType);
                });
                nts.uk.ui.dialog.info({ messageId: "Msg_15" });
            }).fail((res) => {
                nts.uk.ui.dialog.alertError({ messageId: res.messageId });
            }).always(() => {
                block.clear();
            });
        }

        deleteBtn() {
            var self = this;
            if (!nts.uk.text.isNullOrEmpty(self.currentCode())) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(function() {
                    block.invisible();
                    let param: RoleIndividualGrantBaseCommand = new RoleIndividualGrantBaseCommand(self.selectRoleIndividual());
                    param.startValidPeriod = nts.uk.time.parseMoment(self.datePeriod().startDate).format();
                    param.endValidPeriod = nts.uk.time.parseMoment(self.datePeriod().endDate).format();
                    service.deleteRoleIndividual(param).done(() => {
                        let index = _.findIndex(self.listRoleIndividual(), ['GUID', self.currentCode()]);
                        index = _.min([self.listRoleIndividual().length - 2, index]);
                        self.getData().done(() => {
                            self.selectRoleByIndex(index);
                            nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                        });
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    }).always(() => {
                        block.clear();
                    });
                })
            }

        }

        private getData(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            service.getAll(self.selectedRoleType(), self.selectedCompany()).done((data: any) => {
                let listGrantDto = _.map(data.listGrantDto, (item: IRoleIndividual) => {
                    return new RoleIndividualDto(item);
                });
                self.listRoleIndividual(listGrantDto);
                dfd.resolve(data);
            });
            return dfd.promise();
        }

        private buildNewRoleIndividual(): RoleIndividual {
            return new RoleIndividual({
                GUID: "",
                loginID: "",
                companyID: "",
                roleID: "",
                roleType: 0,
                userID: "",
                userName: "",
                startValidPeriod: "",
                endValidPeriod: "",
            });
        }

        private findRoleById(id: string): void {
            var self = this;
            nts.uk.ui.errors.clearAll();
            var selectedRole: RoleIndividualDto = _.find(self.listRoleIndividual(), ['GUID', id]);
            if (selectedRole !== undefined) {
                self.isCreate(false);
                self.selectRoleIndividual(new RoleIndividual(selectedRole));
                self.datePeriod({
                    startDate: selectedRole.startValidPeriod,
                    endDate: selectedRole.endValidPeriod
                });
            }
            else {
                self.isCreate(true);
                self.selectRoleIndividual(self.buildNewRoleIndividual());
                self.datePeriod({});
            }
        }

        private selectRoleByKey(companyID: string, userID: string, roleType: number) {
            var self = this;
            nts.uk.ui.errors.clearAll();
            var selectedRole: RoleIndividualDto = _.find(self.listRoleIndividual(), (item) => {
                return item.companyID == companyID && item.userID == userID && item.roleType == roleType;
            });
            var GUID = (selectedRole) ? selectedRole.GUID : "";
            self.currentCode(GUID);
            self.findRoleById(GUID);
        }

        private selectRoleByIndex(index: number) {
            var self = this;
            var selectedRole: RoleIndividualDto = _.nth(self.listRoleIndividual(), index);
            var GUID = (selectedRole) ? selectedRole.GUID : "";
            self.currentCode(GUID);
            self.findRoleById(GUID);
        }
    }

    export interface IRoleIndividual {
        GUID: string;
        loginID: string
        companyID: string;
        roleID: string,
        roleType: number;
        userID: string;
        userName: string;
        startValidPeriod: string;
        endValidPeriod: string;
    }

    export class RoleIndividualDto implements IRoleIndividual {
        GUID: string;
        loginID: string
        companyID: string;
        roleID: string;
        roleType: number;
        userID: string;
        userName: string;
        startValidPeriod: string;
        endValidPeriod: string;
        datePeriod: string;

        constructor(param: IRoleIndividual) {
            this.GUID = param.GUID || nts.uk.util.randomId();
            this.loginID = (param.loginID);
            this.userID = (param.userID);
            this.roleID = param.roleID;
            this.roleType = (param.roleType);
            this.companyID = (param.companyID);
            this.userName = (param.userName);
            this.startValidPeriod = param.startValidPeriod;
            this.endValidPeriod = param.endValidPeriod;
            this.datePeriod = param.startValidPeriod + " ～ " + param.endValidPeriod;
        }
    }

    export class RoleIndividual {
        GUID: string;
        loginID: KnockoutObservable<string>;
        userID: KnockoutObservable<string>;
        userName: KnockoutObservable<string>;
        roleID: KnockoutObservable<string>;
        roleType: KnockoutObservable<number>;
        companyID: KnockoutObservable<string>;

        constructor(param: IRoleIndividual) {
            var self = this;
            this.GUID = param.GUID || nts.uk.util.randomId();
            this.userID = ko.observable(param.userID);
            this.userName = ko.observable(param.userName);
            this.roleID = ko.observable(param.roleID);
            this.roleType = ko.observable(param.roleType);
            this.companyID = ko.observable(param.companyID);
        }
    }

    export class RoleIndividualGrantBaseCommand {
        userID: string;
        roleID: string;
        roleType: number;
        companyID: string;
        startValidPeriod: string;
        endValidPeriod: string;
        setRoleAdminFlag: boolean = false;
        decisionCompanyID: string = "";

        constructor(data: RoleIndividual) {
            this.userID = data.userID();
            this.roleID = data.roleID();
            this.roleType = data.roleType();
            this.companyID = data.companyID();
        }
    }
}

