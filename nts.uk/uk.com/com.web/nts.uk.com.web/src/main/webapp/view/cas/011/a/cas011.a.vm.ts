module nts.uk.com.view.cas011.a.viewmodel {
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    import dialog = nts.uk.ui.dialog;
    import windows = nts.uk.ui.windows;
    import resource = nts.uk.resource;
    import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;

    export class ScreenModel {
        langId: KnockoutObservable<string> = ko.observable('ja');
        //list of Role Set
        listRoleSets: KnockoutObservableArray<IRoleSet> = ko.observableArray([]);
        listWebMenus: KnockoutObservableArray<IWebMenu> = ko.observableArray([]);
        listAllWebMenus: Array<IWebMenu> = [];
        listCurrentRoleIds: Array<string> = [];
        currentRoleSet: KnockoutObservable<RoleSet> = ko.observable(new RoleSet({
            companyId: ''
            , roleSetCd: ''
            , roleSetName: ''
            , salaryRoleId: ''
            , myNumberRoleId: ''
            , personInfRoleId: ''
            , employmentRoleId: ''
            , officeHelperRoleId: ''
            , approvalAuthority: true
            , humanResourceRoleId: ''
            , webMenus: []
        }));

        selectedRoleSetCd: KnockoutObservable<string> = ko.observable('');

        hRRoleName: KnockoutObservable<string>;
        salaryRoleName: KnockoutObservable<string>;
        myNumberRoleName: KnockoutObservable<string>;
        personInfRoleName: KnockoutObservable<string>;
        employmentRoleName: KnockoutObservable<string>;
        officeHelperRoleName: KnockoutObservable<string>;

        isNewMode: KnockoutObservable<boolean>;
        roleSetCount: KnockoutObservable<number> = ko.observable(0);
        swApprovalAuthority: KnockoutObservableArray<any>;
        gridColumns: KnockoutObservableArray<NtsGridListColumn>;
        swapColumns: KnockoutObservableArray<NtsGridListColumn>;

        constructor() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();

            // A2_003, A2_004, A2_005, A2_006 
            self.gridColumns = ko.observableArray([
                { headerText: resource.getText('CAS011_9'), key: 'roleSetCd', formatter: _.escape, width: 65 },
                { headerText: resource.getText('CAS011_10'), key: 'roleSetName', formatter: _.escape, width: 215 }
            ]);

            self.swapColumns = ko.observableArray([
                { headerText: resource.getText('CAS011_9'), key: 'webMenuCode', width: 65 },
                { headerText: resource.getText('CAS011_34'), key: 'webMenuName', width: 135 }
            ]);

            // ---A3_024, A3_025 
            self.swApprovalAuthority = ko.observableArray([
                { code: true, name: resource.getText('CAS011_22') },
                { code: false, name: resource.getText('CAS011_23') }
            ]);

            self.hRRoleName = ko.observable(resource.getText('CAS011_23'));
            self.salaryRoleName = ko.observable(resource.getText('CAS011_23'));
            self.myNumberRoleName = ko.observable(resource.getText('CAS011_23'));
            self.personInfRoleName = ko.observable(resource.getText('CAS011_23'));
            self.employmentRoleName = ko.observable(resource.getText('CAS011_23'));
            self.officeHelperRoleName = ko.observable(resource.getText('CAS011_23'));

            self.isNewMode = ko.observable(true);
            /**
             *Subscribe: 項目変更→項目 
             */
            self.selectedRoleSetCd.subscribe(roleSetCd => {
                errors.clearAll();
                let listRoleSet = self.listRoleSets();
                // do not process anything if it is new mode.
                //if (roleSetCd) {
                if (roleSetCd && listRoleSet && listRoleSet.length > 0) {

                    let index: number = 0;
                    if (roleSetCd) {
                        index = _.findIndex(listRoleSet, function(x: IRoleSet)
                        { return x.roleSetCd == roleSetCd });
                        if (index === -1) index = 0;
                    }
                    let _roleSet = listRoleSet[index];
                    if (_roleSet && _roleSet.roleSetCd) {

                        //service.getRoleSetByRoleSetCd(roleSetCd).done ((_roleSet : IRoleSet) => {
                        //if (_roleSet && _roleSet.roleSetCd) {
                        self.createCurrentRoleSet(_roleSet);
                        self.settingUpdateMode(_roleSet.roleSetCd);
                    } else {
                        //self.settingCreateMode();
                        self.initialScreen(null, '');
                    }
                    //});
                    // }
                } else {
                    self.createNewCurrentRoleSet();
                    self.settingCreateMode();
                }
            });
        }

        /**
         * 開始
         **/
        start(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred(),
                listRoleSets = self.listRoleSets,
                currentRoleSet: RoleSet = self.currentRoleSet();

            /**
             *実行時情報をチェックする- check runtime
             */
            service.getCompanyIdOfLoginUser().done((companyId: any) => {
                if (!companyId) {
                    self.backToTopPage();
                    dfd.resolve();
                } else {
                    // initial screen
                    self.initialScreen(dfd, '');
                }
            }).fail(error => {
                self.backToTopPage();
                dfd.resolve();
            });

            return dfd.promise();
        }

        /**
         * back to top page - トップページに戻る
         */
        backToTopPage() {
            windows.sub.modeless("/view/ccg/008/a/index.xhtml");
        }

        /**
         * Initial screen
         * - アルゴリズム「ロールセットをすべて取得する」を実行する - Execute the algorithm Get all Roll Set
         * - 先頭のロールセットを選択する - Select the first roll set
         * - 画面を新規モードで起動する - Start screen in new mode
         */
        initialScreen(deferred: any, roleSetCd: string) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet(),
                listRoleSets = self.listRoleSets;

            listRoleSets.removeAll();
            errors.clearAll();
            // initial screen
            self.getAllWebMenus();

            service.getAllRoleSet().done((itemList: Array<IRoleSet>) => {
                // in case number of RoleSet is greater then 0
                if (itemList && itemList.length > 0) {
                    listRoleSets(itemList);
                    /**
                     * 先頭のロールセットを選択する
                     */
                    self.roleSetCount(itemList.length);
                    let index: number = 0;
                    if (roleSetCd) {
                        index = _.findIndex(listRoleSets(), function(x: IRoleSet)
                        { return x.roleSetCd == roleSetCd });
                        if (index === -1) index = 0;
                    }
                    let _roleSet = listRoleSets()[index];
                    self.createCurrentRoleSet(_roleSet);
                    self.settingUpdateMode(_roleSet.roleSetCd);
                } else { //in case number of RoleSet is zero
                    /**
                     * 画面を新規モードで起動する
                     */
                    self.createNewCurrentRoleSet();
                    self.settingCreateMode();
                }
            }).fail(error => {
                /**
                 * 画面を新規モードで起動する
                 */
                self.createNewCurrentRoleSet();
                self.settingCreateMode();
            }).always(() => {
                self.roleSetCount(self.listRoleSets().length);
                if (deferred) {
                    deferred.resolve();
                }
            });
        }

        /**
         * Save
         */
        saveRoleSet() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            $('.nts-input').trigger("validate");
            if (errors.hasError() === false) {
                block.invisible();
                if (self.isNewMode()) {
                    // create new role set
                    service.addRoleSet(ko.toJS(currentRoleSet)).done((roleSetCd) => {
                        dialog.info({ messageId: "Msg_15" });
                        // refresh - initial screen
                        self.initialScreen(null, currentRoleSet.roleSetCd());
                    }).fail(function(error) {

                        if (error.messageId == 'Msg_583') {
                            dialog.alertError({ messageId: error.messageId, messageParams: ["メニュー"] });
                        } else {
                            if (error.messageId == 'Msg_3') {
                                $('#inpRoleSetCd').ntsError('set', error);
                                $('#inpRoleSetCd').focus();
                            }
                            dialog.alertError({ messageId: error.messageId });
                        }
                    }).always(function() {
                        block.clear();
                    });
                } else {
                    // update
                    service.updateRoleSet(ko.toJS(currentRoleSet)).done((roleSetCd) => {
                        dialog.info({ messageId: "Msg_15" });
                        // refresh - initial screen
                        self.initialScreen(null, currentRoleSet.roleSetCd());

                    }).fail(function(error) {
                        if (error.messageId == 'Msg_583') {
                            dialog.alertError({ messageId: error.messageId, messageParams: ["メニュー"] });
                        } else {
                            dialog.alertError({ messageId: error.messageId });
                        }
                    }).always(function() {
                        block.clear();
                    });
                }
            }
        }

        /**
         * delete the role set
         */
        deleteRoleSet() {
            let self = this,
                listRoleSets = self.listRoleSets,
                currentRoleSet: RoleSet = self.currentRoleSet();
            block.invisible();
            /**
             * 確認メッセージ（Msg_18）を表示する
             */
            dialog.confirmDanger({ messageId: "Msg_18" }).ifYes(() => {
                if (currentRoleSet.roleSetCd()) {
                    var object: any = { roleSetCd: currentRoleSet.roleSetCd() };
                    service.removeRoleSet(ko.toJS(object)).done(function() {
                        dialog.info({ messageId: "Msg_16" });
                        //select next Role Set
                        let index: number = _.findIndex(listRoleSets(), function(x: IRoleSet)
                        { return x.roleSetCd == currentRoleSet.roleSetCd() });
                        // remove the deleted item out of list
                        if (index > -1) {
                            self.listRoleSets.splice(index, 1);
                            if (index >= listRoleSets().length) {
                                index = listRoleSets().length - 1;
                            }
                            if (listRoleSets().length > 0) {
                                self.settingUpdateMode(listRoleSets()[index].roleSetCd);
                            } else {
                                self.settingCreateMode();
                            }
                        }
                    }).fail(function(error) {
                        dialog.alertError({ messageId: error.messageId });
                    }).always(function() {
                        block.clear();
                    });
                } else {
                    block.clear();
                }
            }).then(() => {
                block.clear();
            });;
        }

        /**
         * setting focus base on screen mode
         */
        setFocus() {
            let self = this;
            if (self.isNewMode()) {
                $('#inpRoleSetCd').focus();
            } else {
                $('#inpRoleSetName').focus();
            }
            errors.clearAll();
        }

        /** ダイアログ
          * Open dialog CLD025 
         */
        openDialogCLD025(roleType: number, roleId: String) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            if (!roleType && roleType < 0) {
                return;
            }
            block.invisible();
            let param = {
                roleType: roleType,
                multiple: false,
                currentCode: roleId,
                roleAtr: 1
            };
            windows.setShared('paramCdl025', param);
            windows.sub.modal('/view/cdl/025/index.xhtml', { title: '' }).onClosed(function(): any {
                //get data from share window
                var roleId = windows.getShared('dataCdl025');
                if (roleId != undefined) {
                    self.setRoleId(roleType, roleId);
                }
                self.setFocusAfterSelectRole(roleType);
                block.clear();
            });
        }

        /**
         * ダイアログ - Open dialog C
         * 「設定」ボタンをクリック - Click "Setting" button
        */
        openDialogSettingC() {
            let self = this;
            block.invisible();
            let dialogTile = resource.getText('CAS011_3');
            windows.sub.modal('/view/cas/011/c/index.xhtml',
                { title: dialogTile }).onClosed(function(): any {
                    block.clear();
                    $('#inpRoleSetCd').focus();
                });
        }

        /**
         * create a new Role Set
         * 画面を新規モードで起動する
         */
        settingCreateMode() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            // clear selected role set
            self.selectedRoleSetCd('');
            // Set new mode
            self.isNewMode(true);

            //focus
            self.setFocus();
        }

        /**
         * Setting selected role set.
        */
        settingUpdateMode(selectedRoleSetCd) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            self.selectedRoleSetCd(selectedRoleSetCd);
            if (selectedRoleSetCd) {
                //Setting update mode
                self.isNewMode(false);
                //focus
                self.setFocus();
            }
        }

        /**
         * BindNoData to currentRoleSet
         */
        createNewCurrentRoleSet() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            if (currentRoleSet.roleSetCd() === '') {
                return;
            }
            currentRoleSet.roleSetCd('');
            currentRoleSet.roleSetName('');
            currentRoleSet.salaryRoleId('');
            currentRoleSet.myNumberRoleId('');
            currentRoleSet.personInfRoleId('');
            currentRoleSet.employmentRoleId('');
            currentRoleSet.approvalAuthority(true);
            currentRoleSet.officeHelperRoleId('');
            currentRoleSet.humanResourceRoleId('');

            currentRoleSet.webMenus([]);
            // build swap web menu
            self.buildSwapWebMenu();

            //build role Name
            self.listCurrentRoleIds = [];
            self.buildRoleName();
        }
        /**
         * BindData to currentRoleSet
         * @param _roleSet
         */
        createCurrentRoleSet(_roleSet: IRoleSet) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            if (currentRoleSet.roleSetCd() === _roleSet.roleSetCd) {
                return;
            }
            currentRoleSet.companyId(_roleSet.companyId);
            currentRoleSet.roleSetCd(_roleSet.roleSetCd);
            currentRoleSet.roleSetName(_roleSet.roleSetName);
            currentRoleSet.salaryRoleId(_roleSet.salaryRoleId);
            currentRoleSet.myNumberRoleId(_roleSet.myNumberRoleId);
            currentRoleSet.personInfRoleId(_roleSet.personInfRoleId);
            currentRoleSet.employmentRoleId(_roleSet.employmentRoleId);
            currentRoleSet.approvalAuthority(_roleSet.approvalAuthority);
            currentRoleSet.officeHelperRoleId(_roleSet.officeHelperRoleId);
            currentRoleSet.humanResourceRoleId(_roleSet.humanResourceRoleId);
            currentRoleSet.webMenus(_roleSet.webMenus || []);

            // build swap web menu
            self.buildSwapWebMenu();

            //build role Name
            self.listCurrentRoleIds = [];
            self.listCurrentRoleIds.push(_roleSet.salaryRoleId);
            self.listCurrentRoleIds.push(_roleSet.myNumberRoleId);
            self.listCurrentRoleIds.push(_roleSet.personInfRoleId);
            self.listCurrentRoleIds.push(_roleSet.employmentRoleId);
            self.listCurrentRoleIds.push(_roleSet.officeHelperRoleId);
            self.listCurrentRoleIds.push(_roleSet.humanResourceRoleId);
            self.listCurrentRoleIds = self.listCurrentRoleIds.filter(function(roleid) {
                return roleid ? true : false;
            });
            self.buildRoleName();
        }

        /**
         * build swap web menu
         */
        buildSwapWebMenu() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();

            self.listWebMenus.removeAll();
            if (self.listAllWebMenus && self.listAllWebMenus.length > 0) {
                self.listWebMenus(self.listAllWebMenus.filter(item1 => !self.isSelectedWebMenu(item1.webMenuCode)));
                // get Web Menu Name for Web menu
                let listWebMenuRight = self.listAllWebMenus.filter(item1 => self.isSelectedWebMenu(item1.webMenuCode));
                //currentRoleSet.webMenus.removeAll();
                currentRoleSet.webMenus(listWebMenuRight);
            }
        }

        /**
         * build Role name base on list Role id
         */
        buildRoleName() {
            let self = this;
            self.clearRoleName();
            if (self.listCurrentRoleIds && self.listCurrentRoleIds.length > 0) {
                service.getRoleNameByListId(self.listCurrentRoleIds).done((itemList) => {
                    if (itemList && itemList.length > 0) {
                        for (var i = 0; i < itemList.length; i++) {
                            self.setRoleName(itemList[i].roleType, itemList[i].name);
                        }
                    }
                });
            }
        }

        clearRoleName() {
            let self = this;
            let emName = resource.getText('CAS011_23');
            self.employmentRoleName(emName);
            self.hRRoleName(emName);
            self.salaryRoleName(emName);
            self.personInfRoleName(emName);
            self.myNumberRoleName(emName);
            self.officeHelperRoleName(emName);
        }
        /**
         * Execute get all web menu.
         */
        getAllWebMenus() {
            let self = this;
            service.getAllWebMenu().done((itemList: Array<IWebMenu>) => {
                if (itemList && itemList.length > 0) {
                    self.listAllWebMenus = itemList;
                }
            }).fail(function(error) {
            });
        }
        /**
         * Check and return true if the Web menu code existed in current selected web menu list.
         * 
         */
        isSelectedWebMenu = function(_webMenuCode: string): boolean {
            let self = this,
                currentRoleSet: RoleSet = this.currentRoleSet();

            if (!_webMenuCode || !currentRoleSet
                || !currentRoleSet.webMenus() || currentRoleSet.webMenus().length === 0) {
                return false;
            }
            let index: number = _.findIndex(currentRoleSet.webMenus(), function(x: IWebMenu) { return x.webMenuCode === _webMenuCode });
            return (index > -1);
        }

        /**
         * Build RoleName by Role Id
         * @param roleId
         */
        settingRoleNameByRoleId(roleType: number, roleId: string): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            if (!roleId) {
                //self.setRoleName(roleType, resource.getText('CAS011_23'));
                dfd.resolve(resource.getText('CAS011_23'));
                return dfd.promise();
            }

            service.getRoleById(roleId).done((item) => {
                if (item) {
                    //self.setRoleName(roleType, item.name);
                    dfd.resolve(item.name);
                } else {
                    //reset
                    //self.setRoleId(roleType, '');
                    dfd.resolve(resource.getText('CAS011_23'));
                }

            }).fail(function(error) {
                //reset
                //self.setRoleId(roleType, '');
                dfd.resolve(resource.getText('CAS011_23'));
            });

            return dfd.promise();
        }

        /**
         * Set role type data
         */
        setRoleId(roleType: number, roleId: string) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            switch (roleType) {
                case ROLE_TYPE.EMPLOYMENT: // A3_6
                    currentRoleSet.employmentRoleId(roleId);
                    self.settingRoleNameByRoleId(ROLE_TYPE.EMPLOYMENT, roleId).done((name) => {
                        self.setRoleName(ROLE_TYPE.EMPLOYMENT, name);
                    });
                    break;
                case ROLE_TYPE.HR: // A3-9
                    currentRoleSet.humanResourceRoleId(roleId);
                    self.settingRoleNameByRoleId(ROLE_TYPE.HR, roleId).done((name) => {
                        self.setRoleName(ROLE_TYPE.HR, name);
                    });
                    break;
                case ROLE_TYPE.SALARY: //A3-12
                    currentRoleSet.salaryRoleId(roleId);
                    self.settingRoleNameByRoleId(ROLE_TYPE.SALARY, roleId).done((name) => {
                        self.setRoleName(ROLE_TYPE.SALARY, name);
                    });
                    break;
                case ROLE_TYPE.PERSON_INF: //A3-15
                    currentRoleSet.personInfRoleId(roleId);
                    self.settingRoleNameByRoleId(ROLE_TYPE.PERSON_INF, roleId).done((name) => {
                        self.setRoleName(ROLE_TYPE.PERSON_INF, name);
                    });
                    break;
                case ROLE_TYPE.MY_NUMBER: //A3-18
                    currentRoleSet.myNumberRoleId(roleId);
                    self.settingRoleNameByRoleId(ROLE_TYPE.MY_NUMBER, roleId).done((name) => {
                        self.setRoleName(ROLE_TYPE.MY_NUMBER, name);
                    });
                    break;
                case ROLE_TYPE.OFFICE_HELPER: //A3-21
                    currentRoleSet.officeHelperRoleId(roleId);
                    self.settingRoleNameByRoleId(ROLE_TYPE.OFFICE_HELPER, roleId).done((name) => {
                        self.setRoleName(ROLE_TYPE.OFFICE_HELPER, name);
                    });
                    break;
                default:
                    break;
            }
        }

        /**
         * setFocusAfterSelectRole
         */
        setFocusAfterSelectRole(roleType: number) {
            switch (roleType) {
                case ROLE_TYPE.EMPLOYMENT: // A3_6
                    $('#A3_009').focus();
                    break;
                case ROLE_TYPE.HR: // A3-9
                    $('#A3_012').focus();
                    break;
                case ROLE_TYPE.SALARY: //A3-12
                    $('#A3_015').focus();
                    break;
                case ROLE_TYPE.PERSON_INF: //A3-15
                    $('#A3_018').focus();
                    break;
                case ROLE_TYPE.MY_NUMBER: //A3-18
                    $('#A3_021').focus();
                    break;
                case ROLE_TYPE.OFFICE_HELPER: //A3-21
                    $('#swApprovalAuthority').focus();
                    break;
                default:
                    break;
            }
        }

        /**
         * setRoleName
         */
        setRoleName(roleType: number, roleName: string) {
            let self = this;
            switch (roleType) {
                case ROLE_TYPE.EMPLOYMENT: // A3_6
                    self.employmentRoleName(roleName);
                    break;
                case ROLE_TYPE.HR: // A3-9
                    self.hRRoleName(roleName);
                    break;
                case ROLE_TYPE.SALARY: //A3-12
                    self.salaryRoleName(roleName);
                    break;
                case ROLE_TYPE.PERSON_INF: //A3-15
                    self.personInfRoleName(roleName);
                    break;
                case ROLE_TYPE.MY_NUMBER: //A3-18
                    self.myNumberRoleName(roleName);
                    break;
                case ROLE_TYPE.OFFICE_HELPER: //A3-21
                    self.officeHelperRoleName(roleName);
                    break;
                default:
                    break;
            }
        }
        
        
         private exportExcel(): void { 
            var self = this;
            nts.uk.ui.block.grayout();
            let langId = self.langId();
            service.saveAsExcel(langId).done(function() {
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
            }).always(function() {
                nts.uk.ui.block.clear();
            });
        }
        
    }

    /**
     * The enum of ROLE TYPE 
     */
    export enum ROLE_TYPE {
        EMPLOYMENT = 3,
        SALARY = 4,
        HR = 5,
        OFFICE_HELPER = 6,
        MY_NUMBER = 7,
        PERSON_INF = 8
    }

    // The Web menu
    export interface IWebMenu {
        webMenuCode: string;
        webMenuName: string;
    }

    export class WebMenu {
        webMenuCode: KnockoutObservable<string> = ko.observable('');
        webMenuName: KnockoutObservable<string> = ko.observable('');

        constructor(param: IWebMenu) {
            let self = this;
            self.webMenuCode(param.webMenuCode || '');
            self.webMenuName(param.webMenuName || '');
        }
    }

    // The Role Set
    export interface IRoleSet {
        companyId: string;
        roleSetCd: string;
        roleSetName: string;
        salaryRoleId: string;
        myNumberRoleId: string;
        personInfRoleId: string;
        employmentRoleId: string;
        approvalAuthority: boolean;
        officeHelperRoleId: string;
        humanResourceRoleId: string;
        webMenus: Array<IWebMenu>;
    }

    export class RoleSet {
        companyId: KnockoutObservable<string> = ko.observable('');
        roleSetCd: KnockoutObservable<string> = ko.observable('');
        roleSetName: KnockoutObservable<string> = ko.observable('');
        salaryRoleId: KnockoutObservable<string> = ko.observable('');
        myNumberRoleId: KnockoutObservable<string> = ko.observable('');
        personInfRoleId: KnockoutObservable<string> = ko.observable('');
        employmentRoleId: KnockoutObservable<string> = ko.observable('');
        approvalAuthority: KnockoutObservable<boolean> = ko.observable(true);
        officeHelperRoleId: KnockoutObservable<string> = ko.observable('');
        humanResourceRoleId: KnockoutObservable<string> = ko.observable('');
        webMenus: KnockoutObservableArray<IWebMenu> = ko.observableArray([]);

        constructor(param: IRoleSet) {
            let self = this;
            self.companyId(param.companyId);
            self.roleSetCd(param.roleSetCd || '');
            self.roleSetName(param.roleSetName || '');
            self.salaryRoleId(param.salaryRoleId || '');
            self.webMenus(param.webMenus || []);
            self.myNumberRoleId(param.myNumberRoleId || '');
            self.personInfRoleId(param.personInfRoleId || '');
            self.employmentRoleId(param.employmentRoleId || '');
            self.officeHelperRoleId(param.officeHelperRoleId || '');
            self.approvalAuthority(param.approvalAuthority || true);
            self.humanResourceRoleId(param.humanResourceRoleId || '');
        }
    }
}

