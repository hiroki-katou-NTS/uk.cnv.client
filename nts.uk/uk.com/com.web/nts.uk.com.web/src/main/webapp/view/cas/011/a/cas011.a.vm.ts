module nts.uk.com.view.cas011.a.viewmodel {
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    import dialog = nts.uk.ui.dialog;
    import windows = nts.uk.ui.windows;
    import resource = nts.uk.resource;
    import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;

    export class ScreenModel {
        //list of Role Set
        listRoleSets: KnockoutObservableArray<IRoleSet> = ko.observableArray([]);
        listWebMenus: KnockoutObservableArray<IWebMenu> = ko.observableArray([]);

        currentRoleSet: KnockoutObservable<RoleSet> = ko.observable(new RoleSet({
                companyId: ''
                ,roleSetCd: ''
                , roleSetName:'' 
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

        hRRoleName:         KnockoutObservable<string>;
        salaryRoleName:     KnockoutObservable<string>;
        myNumberRoleName:   KnockoutObservable<string>;
        personInfRoleName:  KnockoutObservable<string>;
        employmentRoleName: KnockoutObservable<string>;
        officeHelperRoleName: KnockoutObservable<string>;

        isNewMode:           KnockoutObservable<boolean>;
        roleSetCount:        KnockoutObservable<number> = ko.observable(0);
        swApprovalAuthority: KnockoutObservableArray<any>;    
        gridColumns:         KnockoutObservableArray<NtsGridListColumn>;
        swapColumns:         KnockoutObservableArray<NtsGridListColumn>;

        constructor() {
            let self = this,
            currentRoleSet: RoleSet = self.currentRoleSet();

            // A2_003, A2_004, A2_005, A2_006 
            self.gridColumns = ko.observableArray([
                                                {headerText: resource.getText('CAS011_09'), key: 'roleSetCd', formatter: _.escape, width: 40},
                                                {headerText: resource.getText('CAS011_10'), key: 'roleSetName', formatter: _.escape, width: 180}
                                           ]);

            self.swapColumns = ko.observableArray([
                                               { headerText: resource.getText('CAS011_9'), key: 'webMenuCode', width: 100 },
                                               { headerText: resource.getText('CAS011_34'), key: 'webMenuName', width: 150 }
                                           ]);

            // ---A3_024, A3_025 
            self.swApprovalAuthority = ko.observableArray([
                                              { code: true, name: resource.getText('CAS011_22') },
                                              { code: false, name: resource.getText('CAS011_23') }
                                          ]);

            self.hRRoleName         = ko.observable(resource.getText('CAS011_23'));
            self.salaryRoleName     = ko.observable(resource.getText('CAS011_23'));
            self.myNumberRoleName   = ko.observable(resource.getText('CAS011_23'));
            self.personInfRoleName  = ko.observable(resource.getText('CAS011_23'));
            self.employmentRoleName = ko.observable(resource.getText('CAS011_23'));
            self.officeHelperRoleName = ko.observable(resource.getText('CAS011_23'));

            
            self.isNewMode = ko.observable(true);

            /**
             *Subscribe: 項目変更→項目 
             */
            self.selectedRoleSetCd.subscribe(roleSetCd => {
                errors.clearAll();
                // do not process anything if it is new mode.
                if (roleSetCd) {
                    service.getRoleSetByRoleSetCd(roleSetCd).done ((_roleSet : IRoleSet) => {
                        if (_roleSet && _roleSet.roleSetCd) {
                            self.createCurrentRoleSet(_roleSet);
                            self.settingUpdateMode(_roleSet.roleSetCd);
                        } else {
                            self.settingCreateMode();
                        }
                    });
                } else {
                    self.createNewCurrentRoleSet();
                    self.settingCreateMode();
                }
                self.setFocus();
            });

            //Setting role name
            currentRoleSet.humanResourceRoleId.subscribe(hRRoleId => {
                self.settingRoleNameByRoleId(ROLE_TYPE.HR, hRRoleId);
            });
            currentRoleSet.salaryRoleId.subscribe(salaryRoleId => {
                self.settingRoleNameByRoleId(ROLE_TYPE.SALARY, salaryRoleId);
            });
            currentRoleSet.myNumberRoleId.subscribe(myNumberRoleId => {
                self.settingRoleNameByRoleId(ROLE_TYPE.MY_NUMBER, myNumberRoleId);
            });
            currentRoleSet.personInfRoleId.subscribe(personInfRoleId => {
                self.settingRoleNameByRoleId(ROLE_TYPE.PERSON_INF, personInfRoleId);
            });
            currentRoleSet.employmentRoleId.subscribe(employmentRoleId => {
                self.settingRoleNameByRoleId(ROLE_TYPE.EMPLOYMENT, employmentRoleId);
            });
            currentRoleSet.officeHelperRoleId.subscribe(officeHelperRoleId => {
                self.settingRoleNameByRoleId(ROLE_TYPE.OFFICE_HELPER, officeHelperRoleId);
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

            listRoleSets.removeAll();
            errors.clearAll();

            /**
             *実行時情報をチェックする- check runtime
             */
            service.getCompanyIdOfLoginUser().done((companyId: any) => {
                if (!companyId) {
                    self.backToTopPage();
                    dfd.reject();
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
        initialScreen(deferred : any, roleSetCd : string) {
            let self = this,
                        currentRoleSet: RoleSet = self.currentRoleSet(),
                        listRoleSets = self.listRoleSets;

            listRoleSets.removeAll();
            errors.clearAll();
            service.getAllRoleSet().done((itemList: Array<IRoleSet>) => {
                // in case number of RoleSet is greater then 0
                if (itemList && itemList.length > 0) {
                    listRoleSets(itemList);
                    /**
                     * 先頭のロールセットを選択する
                     */
                    self.roleSetCount(itemList.length);
                    let index : number = 0;
                    if (roleSetCd) {
                        index = _.findIndex(listRoleSets(), function (x: IRoleSet) 
                                { return x.roleSetCd == roleSetCd});
                        if (index === -1) index = 0;
                    }
                    self.settingUpdateMode(listRoleSets()[index].roleSetCd);
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
            }).always(()=> {
                self.roleSetCount(self.listRoleSets().length);
                if (deferred) {
                    deferred.resolve();
                }
                // set focus
                self.setFocus();
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
                        }   else {
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
                        }   else {
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
             dialog.confirm({messageId: "Msg_18"}).ifYes(() => {
                if (currentRoleSet.roleSetCd()) {
                    var object : any = {roleSetCd : currentRoleSet.roleSetCd()}; 
                    service.removeRoleSet(ko.toJS(object)).done(function() {
                        dialog.info({ messageId: "Msg_16" });
                        //select next Role Set
                        let index: number = _.findIndex(listRoleSets(), function (x: IRoleSet) 
                                                { return x.roleSetCd == currentRoleSet.roleSetCd()});
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
        }

        /** ダイアログ
          * Open dialog CLD025 
         */
        openDialogCLD025(roleType: number) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();
            if (!roleType && roleType < 0) {
                return;
            }
            block.invisible();
            let param = {
                    roleType : roleType,
                    multiple :  false
                };
            windows.setShared('paramCdl025', param);
            windows.sub.modal('/view/cdl/025/index.xhtml', { title: '' }).onClosed(function(): any {
              //get data from share window
                var data = windows.getShared('dataCdl025');
                var roleId = '';
                if (data) {
                    roleId = data.currentCode;
                }

                self.setRoleId(roleType, roleId);
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
                   {title: dialogTile}).onClosed(function(): any {
               block.clear();
               $('#inpRoleSetCd').focus();
               //self.setFocus();
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
        }

        /**
         * Setting selected role set.
        */
       settingUpdateMode(selectedRoleSetCd) {
           let self = this,
               currentRoleSet: RoleSet = self.currentRoleSet();
           self.selectedRoleSetCd(selectedRoleSetCd);
           //Setting update mode
           self.isNewMode(false);
       }

        /**
         * BindNoData to currentRoleSet
         */
        createNewCurrentRoleSet() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();

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

        }
        /**
         * BindData to currentRoleSet
         * @param _roleSet
         */
        createCurrentRoleSet(_roleSet: IRoleSet) {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();

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
            currentRoleSet.webMenus(_roleSet.webMenus);

            // build swap web menu
            self.buildSwapWebMenu();
        }
 
        /**
         * build swap web menu
         */
        buildSwapWebMenu() {
            let self = this,
                currentRoleSet: RoleSet = self.currentRoleSet();

            self.listWebMenus.removeAll();
            service.getAllWebMenu().done((itemList: Array<IWebMenu>) => {
                if (itemList && itemList.length > 0) {
                    self.listWebMenus(itemList.filter(item1 => !self.isSelectedWebMenu(item1.webMenuCode)));
                    // get Web Menu Name for Web menu
                    let listWebMenuRight = itemList.filter(item1 => self.isSelectedWebMenu(item1.webMenuCode));
                    currentRoleSet.webMenus.removeAll();
                    currentRoleSet.webMenus(listWebMenuRight);
                }
             }).fail(function(error) {
                 dialog.alertError({ messageId: error.messageId });
             });  
        }

        /**
         * Check and return true if the Web menu code existed in current selected web menu list.
         * 
         */
        isSelectedWebMenu = function(_webMenuCode : string) : boolean {
            let self = this,
            currentRoleSet: RoleSet = this.currentRoleSet();

            if (!_webMenuCode || !currentRoleSet
                    || !currentRoleSet.webMenus() || currentRoleSet.webMenus().length === 0){
                return false;
            }
            let index: number = _.findIndex(currentRoleSet.webMenus(), function (x: IWebMenu) { return x.webMenuCode === _webMenuCode});
            return (index > -1);            
        }

        /**
         * Build RoleName by Role Id
         * @param roleId
         */
        settingRoleNameByRoleId(roleType : number, roleId : string){
            let self = this;
            if (!roleId) {
                self.setRoleName(roleType, resource.getText('CAS011_23'));
                return;
            }
            service.getRoleById(roleId).done((item) => {
                if (item) {
                    self.setRoleName(roleType, item.name);
                } else {
                    //reset
                    self.setRoleId(roleType, '');
                }
            }).fail(function(error) {
              //reset
                self.setRoleId(roleType, '');
            });
        }
        
        /**
         * Set role type data
         */
        setRoleId(roleType : number, roleId : string) {
            let self = this,
            currentRoleSet : RoleSet = self.currentRoleSet();
            switch (roleType) {
            case ROLE_TYPE.EMPLOYMENT: // A3_6
                currentRoleSet.employmentRoleId(roleId);
                break;
            case ROLE_TYPE.HR: // A3-9
                currentRoleSet.humanResourceRoleId(roleId);
                break;
            case ROLE_TYPE.SALARY: //A3-12
                currentRoleSet.salaryRoleId(roleId);
                break;
            case ROLE_TYPE.PERSON_INF: //A3-15
                currentRoleSet.personInfRoleId(roleId);
                break;
            case ROLE_TYPE.MY_NUMBER: //A3-18
                currentRoleSet.myNumberRoleId(roleId);
                break;
            case ROLE_TYPE.OFFICE_HELPER: //A3-21
                currentRoleSet.officeHelperRoleId(roleId);
                break;
            default:
                break;
            }
        }

        /**
         * setFocusAfterSelectRole
         */
         setFocusAfterSelectRole(roleType : number) {
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
         setRoleName(roleType : number, roleName : string) {
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
}
    
    /**
     * The enum of ROLE TYPE 
     */
    export enum ROLE_TYPE {
        EMPLOYMENT  = 3,
        SALARY      = 4,
        HR          = 5,
        OFFICE_HELPER = 6,
        MY_NUMBER   = 7,
        PERSON_INF  = 8
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
        companyId:          KnockoutObservable<string> = ko.observable('');
        roleSetCd:          KnockoutObservable<string> = ko.observable('');
        roleSetName:        KnockoutObservable<string> = ko.observable('');
        salaryRoleId:       KnockoutObservable<string> = ko.observable(null);
        myNumberRoleId:     KnockoutObservable<string> = ko.observable(null);
        personInfRoleId:    KnockoutObservable<string> = ko.observable(null);
        employmentRoleId:   KnockoutObservable<string> = ko.observable(null);
        approvalAuthority:  KnockoutObservable<boolean> = ko.observable(true);
        officeHelperRoleId: KnockoutObservable<string> = ko.observable(null);
        humanResourceRoleId: KnockoutObservable<string> = ko.observable(null);
        webMenus:           KnockoutObservableArray<IWebMenu> = ko.observableArray([]);

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

