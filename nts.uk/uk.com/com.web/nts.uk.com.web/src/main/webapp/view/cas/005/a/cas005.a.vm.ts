module nts.uk.com.view.cas005.a.viewmodel {
    import ComponentModelCCG025 = nts.uk.com.view.ccg025.a.component.viewmodel.ComponentModel;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;

    const API = {
        addRoleCas005 :"screen/sys/auth/cas005/addrolecas005",
        updateRoleCas005 :"screen/sys/auth/cas005/updaterolecas005",
        deleteRoleCas005 :"screen/sys/auth/cas005/deleterolecas005",
        findAllMenu : "sys/portal/webmenu/findallmenu", // ①<<ScreenQuery>> Webメニューリストを取得する
        findRoleAndWebMenu : "sys/portal/webmenu/findroleandwebmenu" // ②<<ScreenQuery>>ロールとWebメニュー紐付けを取得する
    };

    @bean()
    class Cas005AViewModel extends ko.ViewModel {
        selectedRole: Role;
        listRole: KnockoutObservableArray<IRole>;
        enumRange: KnockoutObservableArray<EnumConstantDto>;
        webMenuList: KnockoutObservableArray<any>;

        component025: ComponentModelCCG025 = new ComponentModelCCG025({
            roleType: 3,
            multiple: false,
            rows: 15,
            tabindex: 0,
            onDialog:true
        });

        created() {
            const vm = this;
            vm.selectedRole = new Role();
            vm.listRole = ko.observableArray([]);
            vm.enumRange = ko.observableArray([
                {value: 1, localizedName: vm.$i18n("CAS005_11")},
                {value: 2, localizedName: vm.$i18n("CAS005_12")},
                {value: 3, localizedName: vm.$i18n("CAS005_13")}
            ]);
            vm.webMenuList = ko.observableArray([]);

            _.extend(vm, {
                listRole: vm.component025.listRole
            });

            _.extend(vm.selectedRole, {
                roleId: vm.component025.currentRoleId,
                assignAtr: vm.component025.roleClassification
            });

            // subscribe and change data
            vm.selectedRole.roleId.subscribe(rid => {
                let roles = ko.toJS(vm.listRole),
                    exist: IRole = _.find(roles, (r: IRole) => _.isEqual(r.roleId, rid));

                if (exist) {
                    vm.$blockui("show");
                    vm.selectedRole.roleName(exist.name);
                    vm.selectedRole.roleCode(exist.roleCode);

                    vm.selectedRole.employeeReferenceRange(exist.employeeReferenceRange || 0);

                    vm.$ajax("com", API.findRoleAndWebMenu, {roleId: rid}).done((data: any) => {
                        if (vm.selectedRole.assignAtr() == 0) {
                            vm.selectedRole.approvalAuthority(null);
                            vm.selectedRole.webMenuCd(data.roleByRoleTies ? data.roleByRoleTies.webMenuCd : 1);
                        } else {
                            let app = data.role.approvalAuthority == true ? 1: 0;
                            vm.selectedRole.approvalAuthority(app);
                            vm.selectedRole.webMenuCd(null);
                        }
                    }).fail(error => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });
                } else {
                    vm.selectedRole.roleName('');
                    vm.selectedRole.roleCode('');

                    vm.selectedRole.employeeReferenceRange(_.isEqual(vm.selectedRole.assignAtr(), 0) ? 0 : 1);
                    if (vm.selectedRole.assignAtr() == 0) {
                        vm.selectedRole.approvalAuthority(null);
                        vm.selectedRole.webMenuCd(null);
                    } else {
                        vm.selectedRole.approvalAuthority(1);
                        vm.selectedRole.webMenuCd(null);
                    }
                }

                // subscribe for focus and clear errors
                _.defer(() => {
                    if (_.isEmpty(rid)) {
                        vm.selectedRole.roleCodeFocus(true);
                    } else {
                        vm.selectedRole.roleNameFocus(true);
                    }

                    // clear all error
                    nts.uk.ui.errors.clearAll();
                });
            });

            // call reload data
            vm.start();
        }

        /** Start Page */
        start() {
            let vm = this, role = vm.selectedRole;

            vm.$blockui("show");

            $.when(vm.getListRole(), vm.$ajax("com", API.findAllMenu)).done((result1, result2) => {
                let roles = ko.toJS(vm.listRole);

                if (!_.size(roles)) {
                    vm.createNew();
                }
                vm.webMenuList(result2);
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("hide");
                nts.uk.ui.errors.clearAll();
            });
        };

        getListRole(roleId?: string, roleCode?: string) {
            let vm = this, dfd = $.Deferred();

            vm.component025.startPage(roleId, roleCode).done(() => {
                let roles: Array<IRole> = ko.toJS(vm.listRole),
                    roleIds: Array<string> = _.map(roles, (x: IRole) => x.roleId);

                if (!_.size(roleIds)) {
                    vm.createNew();
                }
                dfd.resolve();
            });

            return dfd.promise();
        }

        // create new mode
        createNew() {
            let vm = this;
            if (vm.selectedRole.roleId() != undefined)
                vm.selectedRole.roleId(undefined);
            else
                vm.selectedRole.roleId.valueHasMutated();
        }

        // save change of role
        save() {
            let vm = this,
                role: Role = vm.selectedRole,
                command = ko.toJS(role);

            $(".nts-input").trigger("validate");
            if ($(".nts-input").ntsError("hasError")) {
                return;
            }

            vm.$blockui("show");

            // fix name
            _.extend(command, {
                name: command.roleName,
                roleType: 3
            });

            vm.$ajax("com", _.isEmpty(command.roleId) ? API.addRoleCas005 : API.updateRoleCas005, command).done(() => {
                vm.$dialog.info({ messageId: "Msg_15" }).then(() => {
                    vm.getListRole(command.roleId, command.roleCode).done(() => {

                    }).always(() => {
                        vm.$blockui("hide");
                        nts.uk.ui.errors.clearAll();
                    });
                });
            }).fail((error) => {
                vm.$dialog.error(error);
                vm.$blockui("hide");
                nts.uk.ui.errors.clearAll();
            });
        }

        // remove selected role
        remove = () => {
            let vm = this,
                roles: Array<IRole> = ko.toJS(vm.listRole),
                role: IRole = ko.toJS(vm.selectedRole),
                index: number = _.findIndex(roles, ["roleId", role.roleId]);

            index = _.min([_.size(roles) - 2, index]);

            if (!_.isNil(role.roleId)) {
                vm.$dialog.confirm({ messageId: "Msg_18" }).then(value => {
                    if (value == "yes") {
                        vm.$blockui("show");
                        vm.$ajax("com", API.deleteRoleCas005, _.pick(role, ["roleId", "assignAtr"])).done(() => {
                            vm.$dialog.info({messageId: "Msg_16"}).then(() => {
                                let roles: Array<IRole> = ko.toJS(vm.listRole),
                                    selected: IRole = roles[index];
                                if(isNullOrUndefined(selected)){
                                    vm.getListRole();
                                    vm.$blockui("hide");
                                    nts.uk.ui.errors.clearAll();
                                }else {
                                    vm.getListRole(selected.roleId).done(() => {
                                    }).always(() => {
                                        vm.$blockui("hide");
                                        nts.uk.ui.errors.clearAll();
                                    });
                                }
                            });
                        }).fail((error) => {
                            vm.$dialog.error(error);
                            vm.$blockui("hide");
                            nts.uk.ui.errors.clearAll();
                        });
                    }
                });
            }
        }
    }

    interface EnumConstantDto {
        value: number;
        fieldName?: string;
        localizedName: string;
    }

    interface IRole {
        name: string;
        roleId: string;
        roleCode: string;
        assignAtr: number;
        createMode?: boolean;
        referFutureDate?: boolean;
        employeeReferenceRange: number;
    }

    class Role {
        roleId: KnockoutObservable<string> = ko.observable('');
        roleCode: KnockoutObservable<string> = ko.observable('');
        roleName: KnockoutObservable<string> = ko.observable('');

        assignAtr: KnockoutObservable<number> = ko.observable(1);
        employeeReferenceRange: KnockoutObservable<number> = ko.observable(1);
        approvalAuthority: KnockoutObservable<number> = ko.observable(1);
        webMenuCd: KnockoutObservable<string> = ko.observable(null);

        roleCodeFocus: KnockoutObservable<boolean> = ko.observable(true);
        roleNameFocus: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            let self = this;
        }
    }
}