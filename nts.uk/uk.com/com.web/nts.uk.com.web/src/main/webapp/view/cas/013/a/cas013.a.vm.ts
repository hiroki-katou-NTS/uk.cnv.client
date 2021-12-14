module nts.uk.com.view.cas013.a.viewmodel {
    import modal = nts.uk.ui.windows.sub.modal;
    import block = nts.uk.ui.block;
    import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    export class ScreenModel {
        langId: KnockoutObservable<string> = ko.observable('ja');
        // Metadata
        isCreateMode: KnockoutObservable<boolean> = ko.observable(false);
        isSelectedUser: KnockoutObservable<boolean> = ko.observable(false);
        isDelete: KnockoutObservable<boolean> = ko.observable(false);

        //ComboBOx RollType
        listRoleType: KnockoutObservableArray<RollType>;
        selectedRoleType: KnockoutObservable<string>;

        //list Roll
        listRole: KnockoutObservableArray<Role>;
        selectedRole: KnockoutObservable<string>;
        columns: KnockoutObservableArray<NtsGridListColumn>;

        //list Role Individual Grant    
        listRoleIndividual: KnockoutObservableArray<RoleIndividual>;
        selectedRoleIndividual: KnockoutObservable<string>;
        columnsIndividual: KnockoutObservableArray<NtsGridListColumn>;

        userName: KnockoutObservable<string>;

        //Date pick
        dateValue: KnockoutObservable<any>;

        constructor() {
            var self = this;
            self.listRoleType = ko.observableArray([]);
            self.listRole = ko.observableArray([]);
            self.selectedRoleType = ko.observable('');
            self.selectedRole = ko.observable('');
            self.listRoleIndividual = ko.observableArray([]);
            self.columns = ko.observableArray([
                { headerText: '', key: 'roleId', hidden: true },
                { headerText: nts.uk.resource.getText("CAS013_11"), key: 'roleCode', width: 60 },
                { headerText: nts.uk.resource.getText("CAS013_12"), key: 'name', width: 200 },
            ]);
            self.columnsIndividual = ko.observableArray([
                { headerText: '', key: 'userId', hidden: true },
                { headerText: nts.uk.resource.getText("CAS013_15"), key: 'loginId', width: 120 },
                { headerText: nts.uk.resource.getText("CAS013_16"), key: 'name', width: 120 },
                { headerText: nts.uk.resource.getText("CAS013_17"), key: 'datePeriod', width: 210 },
            ]);
            self.selectedRoleIndividual = ko.observable('');
            self.userName = ko.observable('');
            self.dateValue = ko.observable({});


            self.selectedRoleType.subscribe((roleType: string) => {
                self.getRoles(roleType.toString());
                self.isCreateMode(false);
                self.isSelectedUser(false);
                self.isDelete(false);
            });

            self.selectedRole.subscribe((roleId: string) => {
                self.selectRole(roleId.toString(), '');
                self.isSelectedUser(false);
                self.isDelete(false);
            });
            self.selectedRoleIndividual.subscribe((userId: string) => {
                self.selectRoleGrant(userId.toString());
            });
        }


        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred(); 
            
            // initial screen
            new service.Service().getRoleTypes().done(function(data: Array<RollType>) {
                if(data){
                    if(nts.uk.util.isNullOrUndefined(data)){
                        self.backToTopPage();   
                    }else{
                        self.listRoleType(data);
                        self.selectedRoleType(data[0].value);
                    }
                }else{
                     nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                }
                dfd.resolve(); 
            });
            return dfd.promise();
        }

        /**
         * back to top page - トップページに戻る
         */
        backToTopPage() {
            nts.uk.ui.windows.sub.modeless("/view/ccg/008/a/index.xhtml");
        }

        private getRoles(roleType: string): void {
            var self = this;
            if (roleType != '') {
                new service.Service().getRole(roleType).done(function(data: any) {
                    if (data != null && data.length > 0) {
                        data = _.orderBy(data, ['assignAtr', 'roleCode'], ['asc', 'asc']);
                        self.listRole(data);
                        self.selectedRole(data[0].roleId);
                    }
                    else {
                        self.listRole([]);
                        self.selectedRole('')
                    }
                });
            } else {
                self.listRole([]);
                self.selectedRole('');
            }
        }

        private selectRole(roleId: string, userIdSelected: string): void {
            var self = this;
            if (roleId != '') {
                self.selectedRoleIndividual('');
                new service.Service().getRoleGrants(roleId).done(function(data: any) {
                    if (data != null && data.length > 0) {
                        let items = [];
                        for (let entry of data) {
                            items.push(new RoleIndividual(entry.userID, entry.loginID, entry.userName, entry.startValidPeriod, entry.endValidPeriod))
                        }
                        self.listRoleIndividual(items);
                        if (nts.uk.text.isNullOrEmpty(userIdSelected)) {
                            self.selectedRoleIndividual(items[0].userId);
                        } else {
                            self.selectedRoleIndividual(userIdSelected);
                        }
                    } else {
                        self.listRoleIndividual([]);
                        self.selectedRoleIndividual('');
                        self.userName('');
                        self.dateValue({});
                        self.New();
                    }
                });
            } else {
                self.listRoleIndividual([]);
                self.selectedRoleIndividual('');
                self.userName('');
                self.dateValue({});
            }
        }
        private selectRoleGrant(UserId: string): void {
            var self = this;
            var roleId = self.selectedRole();
            if (roleId != '' && UserId != '') {
                var userSelected = _.find(self.listRoleIndividual(), ['userId',UserId]); 
                self.userName(userSelected.name);
                new service.Service().getRoleGrant(roleId, UserId).done(function(data: any) {
                    if (data != null) {
                        self.dateValue(new datePeriod(data.startValidPeriod, data.endValidPeriod));
                        self.isCreateMode(false);
                        self.isSelectedUser(false);
                        self.isDelete(true);
                    }
                });
            }else{
                self.isDelete(false);    
            }
        }
        New(): void {
            var self = this;
            self.isCreateMode(true);
            self.isDelete(false);
            self.isSelectedUser(false);
            self.selectedRoleIndividual('');
            self.userName('');
            self.dateValue({});
            nts.uk.ui.errors.clearAll();
        }
        openBModal(): void {
            var self = this;
            nts.uk.ui.windows.setShared("roleType", self.selectedRoleType());
            nts.uk.ui.windows.sub.modal("../b/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("UserInfo");
                if (data != null) {
                    self.userName(data.decisionName);
                    self.isSelectedUser(true);
                    self.selectedRoleIndividual(data.decisionUserID);
                }
            });
        }
        save(): void {
            var self = this;
            if (!nts.uk.text.isNullOrEmpty(self.selectedRoleType())
                && !nts.uk.text.isNullOrEmpty(self.selectedRole())
                && !nts.uk.util.isNullOrUndefined(self.dateValue().startDate)
                && !nts.uk.util.isNullOrUndefined(self.dateValue().endDate)
                && self.userName() != ''
                && !nts.uk.ui.errors.hasError()) {
                if (self.isSelectedUser() && self.isCreateMode()) {
                    self.insert();
                } else {
                    self.upDate();
                }
            } else if (nts.uk.text.isNullOrEmpty(self.userName())) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_218", messageParams: [nts.uk.resource.getText("CAS013_19")] });
            } else if (nts.uk.util.isNullOrUndefined(self.dateValue().startDate) || nts.uk.util.isNullOrUndefined(self.dateValue().endDate)) {
                $(".nts-input").trigger("validate");
            }

        }
        private insert(): void {
            var self = this;
            var roleTpye = self.selectedRoleType();
            var roleId = self.selectedRole();
            var userId = self.selectedRoleIndividual();
            var start = nts.uk.time.parseMoment(self.dateValue().startDate).format();
            var end = nts.uk.time.parseMoment(self.dateValue().endDate).format();
            block.invisible();
            new service.Service().insertRoleGrant(roleTpye, roleId, userId, start, end).done(function(data: any) {
                if (!nts.uk.util.isNullOrUndefined(data)) {
                    self.selectedRoleIndividual("");
                    self.selectRole(roleId, data);
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    self.isCreateMode(false);
                } else {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_716", messageParams: [nts.uk.resource.getText("CAS013_11")] });
                }
            }).always(() => {
                block.clear();
            });
        }
        private upDate(): void {
            var self = this;
            var roleTpye = self.selectedRoleType();
            var roleId = self.selectedRole();
            var userId = self.selectedRoleIndividual();
            var start = nts.uk.time.parseMoment(self.dateValue().startDate).format();
            var end = nts.uk.time.parseMoment(self.dateValue().endDate).format();
            block.invisible();
            new service.Service().upDateRoleGrant(roleTpye, roleId, userId, start, end).done(function(data: any) {
                if (!nts.uk.util.isNullOrUndefined(data)) {
                    self.selectRole(roleId, data);
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                } else {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_716", messageParams: [nts.uk.resource.getText("CAS013_11")] });
                }
                self.isCreateMode(false);
            }).always(() => {
                block.clear();
            });
        }
        Delete(): void {
            if (!nts.uk.ui.errors.hasError()) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                    var self = this;
                    var roleTpye = self.selectedRoleType();
                    var userId = self.selectedRoleIndividual();
                    block.invisible();
                    new service.Service().deleteRoleGrant(roleTpye, userId).done(function() {
                        self.selectedRoleIndividual('');
                        self.selectRole(self.selectedRole(), '');
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    }).always(() => {
                        block.clear();
                    });
                });
            }
        }
        
        exportExcel(): void {
            let self = this;
            let params = {
                date: null,
                mode: 1
            };
            if (!nts.uk.ui.windows.getShared("CDL028_INPUT")) {
                nts.uk.ui.windows.setShared("CDL028_INPUT", params);
            }
            nts.uk.ui.windows.sub.modal("../../../../../nts.uk.com.web/view/cdl/028/a/index.xhtml").onClosed(function() {
                var result = getShared('CDL028_A_PARAMS');
                if (result.status) {
                    nts.uk.ui.block.grayout();
                    let langId = self.langId();
                    let date = moment.utc(result.standardDate, "YYYY/MM/DD");
                    new service.Service().saveAsExcel(langId, date).done(function() {
                    }).fail(function(error) {
                        nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                    }).always(function() {
                        nts.uk.ui.block.clear();
                    });
                }
            });
        }

    }



    class RollType {
        value: string;
        nameId: string;
        description: string;

        constructor(value: string, nameId: string, description: string) {
            this.value = value;
            this.nameId = nameId;
            this.description = description;
        }
    }
    class Role {
        roleId: string;
        roleCode: string;
        name: string;
        assignAtr: string;

        constructor(roleId: string, roleCode: string, name: string, assignAtr: string) {
            this.roleId = roleId;
            this.roleCode = roleCode;
            this.name = name;
            this.assignAtr = assignAtr;
        }
    }
    class RoleIndividual {
        userId: string;
        loginId: string;
        name: string;
        start: string;
        end: string;
        datePeriod: string;

        constructor(userId: string, loginId: string, name: string, start: string, end: string) {
            this.userId = userId;
            this.loginId = loginId;
            this.name = name;
            this.start = start;
            this.end = end;
            this.datePeriod = start + ' ~ ' + end;
        }
    }
    class ObjectRole {
        roleDtos: Array<Role>;
        roleIndividualGrantDtos: Array<RoleIndividual>;
        roleTypeDtos: Array<RollType>;
        constructor(roleDtos: Array<Role>, roleIndividualGrantDtos: Array<RoleIndividual>, roleTypeDtos: Array<RollType>) {
            this.roleDtos = roleDtos;
            this.roleIndividualGrantDtos = roleIndividualGrantDtos;
            this.roleTypeDtos = roleTypeDtos;

        }
    }
    class datePeriod {
        startDate: string;
        endDate: string;
        constructor(startDate: string, endDate: string) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

}

