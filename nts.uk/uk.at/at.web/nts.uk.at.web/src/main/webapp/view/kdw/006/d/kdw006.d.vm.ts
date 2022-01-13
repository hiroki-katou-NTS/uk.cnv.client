module nts.uk.at.view.kdw006.d.viewmodel {
    import getText = nts.uk.resource.getText;
    import ccg = nts.uk.com.view.ccg025.a;
    import model = nts.uk.com.view.ccg025.a.component.model;
    export class ScreenModelD extends ko.ViewModel {
        functionalRestriction: KnockoutObservableArray<any>;
        columns2: KnockoutObservableArray<NtsGridListColumn>;

        componentCcg025: ccg.component.viewmodel.ComponentModel = new ccg.component.viewmodel.ComponentModel({ 
            tabindex: 4,
            roleType: 3, //就業
            multiple: false,
            showEmptyItem: false,
            rows: 15,
            isAlreadySetting: true,
        });
        selectedRole: Role = new Role();
        listRole: KnockoutObservableArray<RoleItem> = ko.observableArray([]);
        listRoleId: KnockoutObservableArray<IRole> = ko.observableArray([]);
        
        mode: KnockoutObservable<MODE>;
        constructor() {
            super();

            var self = this;
            self.mode = ko.observable(MODE.NEW);
            self.functionalRestriction = ko.observableArray([]);
            
            window.onresize = function(evt){
                $('#grid2_displayContainer').height(window.innerHeight - 235);
                $('#grid2_container').height(window.innerHeight - 235);
                $('#grid2_virtualContainer').height(window.innerHeight - 235);  
                $('#grid2_scrollContainer').height(window.innerHeight - 235);
            }

            _.extend(self, {
                listRole: self.componentCcg025.listRole
            });

            _.extend(self.selectedRole, {
                roleId: self.componentCcg025.currentRoleId
            });

            self.selectedRole.roleId.subscribe(rid => {
                self.getFuncRest(rid);
            });
        }

        initGrid() {
            let self = this;
            $("#grid2").ntsGrid({
                //width: 780,
                height: window.innerHeight - 213,
                dataSource: self.functionalRestriction(),
                primaryKey: 'functionNo',
                virtualization: true,
                virtualizationMode: 'continuous',
                hidePrimaryKey: true,
                columns: [
                    { headerText: getText('KDW006_44'), key: 'functionNo', dataType: 'number', width: '10px' },
                    { headerText: getText('KDW006_49'), key: 'displayName', dataType: 'string', width: '320px' },
                    { headerText: getText('KDW006_50'), key: 'availability', dataType: 'boolean', width: ' 80px', ntsControl: 'Checkbox' },
                    { headerText: getText('KDW006_51'), key: 'description', dataType: 'string', width: '390px' }
                ],
                features: [{
                    name: 'Selection',
                    mode: 'row',
                    multipleSelection: true
                }],
                // ntsFeatures: [{ name: 'CopyPaste' }],
                ntsControls: [{ name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true }]
            });
        }

        saveData() {
            let self = this;
            self.$blockui("show");
            self.$validate().then((valid: boolean) => {
                if (valid) {
                    self.$blockui("show");
                    service.register(self.selectedRole.roleId(), self.functionalRestriction()).done(function(res: Array<RoleItem>) { 
                        self.$dialog.info({ messageId: "Msg_15" });
                        //self.getFuncRest(self.selectedItem());
                        service.getRoleIds().done(function(res) { //#119057
                            self.listRoleId(res);
                            self.getListRoleCcg025();
                        });
                        self.mode(MODE.UPDATE);
                    }).always(() => {
                        self.$blockui("hide");
                    });
                }
            }).always(() => {
                self.$blockui("hide");
            });
        }
        
        jumpTo() {
            let self = this;
            nts.uk.request.jump("/view/kdw/006/a/index.xhtml");
        }


        start(): JQueryPromise<any> {
            let self = this;
            self.$blockui("grayout");
            let dfd = $.Deferred();
            service.getRoleList().done(function(res: Array<RoleItem>) {
                self.listRole(_.sortBy(res, ['roleCode']));
                self.selectedRole.roleId(self.listRole()[0].roleId);
                self.getFuncRest(self.selectedRole.roleId()).done(function() {
                    if (self.functionalRestriction().length == 0) {
                        self.$dialog.alert({ messageId: "Msg_398" });
                    }
                    service.getRoleIds().done(function(res) { //#119057
                        self.listRoleId(res);
                        self.getListRoleCcg025();
                    });
                    self.initGrid();
                    $("#grid2").igGrid("option", "dataSource", self.functionalRestriction());
                    dfd.resolve();
                });

            }).fail(function(res) {
                self.$dialog.alert(res.message);
            }).always(() => {
                nts.uk.ui.errors.clearAll();
                self.$blockui("hide");
            });
            return dfd.promise();
        }

        getFuncRest(roleId: string): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            if(!roleId || roleId.length === 0) return dfd.promise();

            service.findFuncRest(roleId).done(function(res: Array<FuncRestItem>) {
                if (res.every((el: FuncRestItem) => el.availability == null)) {
                    self.mode(MODE.NEW);
                    res.map((el: FuncRestItem) => el.availability = false);
                } else {
                    self.mode(MODE.UPDATE); 
                }
                self.functionalRestriction(res);
                self.initGrid();
                dfd.resolve();
            }).fail(function(res) {

            });
            return dfd.promise();
        }

        copyData() {
            let self = this;
            self.$blockui("show");

            let selectedRoleItem: RoleItem = _.find(self.listRole(), el => el.roleId == self.selectedRole.roleId());

            service.getRoleIds().done(function(res) {
                let listRoleId: Array<string> = res;
                let param = {
                    code: selectedRoleItem.roleCode,
                    name: selectedRoleItem.roleName,
                    targetType: 8,// ロール
                    itemListSetting: listRoleId,
                    roleType: 3, //就業
                };
                console.log(param, 'param');

                nts.uk.ui.windows.setShared("CDL023Input", param);
                nts.uk.ui.windows.sub.modal("com", "/view/cdl/023/a/index.xhtml").onClosed(() => {
                    self.$blockui("show");
                    let data = nts.uk.ui.windows.getShared("CDL023Output");
                    if (!nts.uk.util.isNullOrUndefined(data)) {
                        let command = {
                            selectedRole: selectedRoleItem.roleId,
                            targetRoleList: data,
                        }
                        service.copyDaiPerfAuth(command).done(() => {
                            self.$dialog.info({ messageId: "Msg_15" }).then(function() {
                                service.getRoleIds().done(function(res) { //#119057
                                    self.listRoleId(res);
                                    self.getListRoleCcg025();
                                });
                                // self.start();
                                self.$blockui("hide");
                            });
                            self.mode(MODE.UPDATE);
                        }).fail(function(res: any) {
                            self.$dialog.alert({ messageId: res.messageId, messageParams: res.parameterIds }).then(function() {
                                self.$blockui("hide");
                            });
                        }).always(() => {
                            self.$blockui("hide");
                        });
                    }
                    self.$blockui("hide");
                });
            });
        }

        /**
         * #119057
         * truyen listRoleId cho CCG025
         */
         getListRoleCcg025(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            self.$blockui("grayout");
            self.componentCcg025.startPage(self.listRoleId(), self.selectedRole.roleId()).done(() => {
                self.getFuncRest(self.selectedRole.roleId());
                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
            }).always(() => {
                self.$blockui("hide");
            });

            return dfd.promise();
        }
    }

    class RoleItem {
        roleId: string;
        roleCode: string;
        roleName: string;
        constructor(roleId: string, roleCode: string, roleName: string) {
            this.roleId = roleId;
            this.roleCode = roleCode;
            this.roleName = roleName;
        }
    }

    class FuncRestItem {
        functionNo: number;
        displayName: string;
        availability: boolean;
        description: string;
        constructor(functionNo: number, displayName: string, availability: boolean, description: string) {
            this.functionNo = functionNo;
            this.displayName = displayName;
            this.availability = availability;
            this.description = description;
        }
    }

    interface IRole {
        name: string;
        roleId: string;
        roleCode: string;
    }

    class Role {
        roleId: KnockoutObservable<string>;
        roleCode: KnockoutObservable<string>;
        roleName: KnockoutObservable<string>;

        constructor() {
            this.roleId = ko.observable("");
            this.roleCode = ko.observable("");
            this.roleName = ko.observable("");
        }
    }

    export enum MODE {
        NEW,
        UPDATE
    }
}
