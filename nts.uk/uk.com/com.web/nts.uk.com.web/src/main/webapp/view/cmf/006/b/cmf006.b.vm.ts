/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.cmf006.b {
    import setShared = nts.uk.ui.windows.setShared;
    import getText = nts.uk.resource.getText;
    import ccg025 = nts.uk.com.view.ccg025.a;
    import util = nts.uk.util;

    const fetch = {
        copy: "exio/exo/condset/exOutCtgAuthSet/copy"
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        componentCcg025: ccg025.component.viewmodel.ComponentModel;
        listRole: KnockoutObservableArray<Role> = ko.observableArray([]);
        roleType: KnockoutObservable<number> = ko.observable(3);
        sourceRoleId: KnockoutObservable<string> = ko.observable(null);
        sourceRoleCode: KnockoutObservable<string> = ko.observable(null);
        sourceRoleName: KnockoutObservable<string> = ko.observable(null);
        destinationRoleId: KnockoutObservable<string> = ko.observable(null);
        overwrite: KnockoutObservable<boolean> = ko.observable(false);

        constructor(params: any) {
            super();
            const vm = this;

            vm.$window.shared("dataShareCMF006B").done((data: IDataFromCMF006A) => {
                if (data) {
                    vm.roleType(data.roleType);
                    vm.sourceRoleId(data.sourceRoleId);
                    vm.sourceRoleCode(data.sourceRoleCode);
                    vm.sourceRoleName(data.sourceRolName);
                }
            });
            vm.listRole = ko.observableArray([]);
            vm.componentCcg025 = new ccg025.component.viewmodel.ComponentModel({
                roleType: vm.roleType(),
                multiple: false,
                isResize: true,
                rows: 5,
                tabindex: 1,
                onDialog:true
            });
            vm.$blockui("grayout");
            vm.componentCcg025.columns([
                { headerText: getText("CCG025_3"), prop: 'roleId', width: 50, hidden: true },
                { headerText: getText("CCG025_3"), prop: 'roleCode', width: 50 },
                { headerText: getText("CCG025_4"), prop: 'name', width: 205 }
            ]);
            vm.fetchRoleList();
        }

        created(params: any) {
            const vm = this;

            vm.componentCcg025.currentRoleId.subscribe((roleId: any) => {
                if (vm.listRole().length <= 0) vm.listRole(vm.componentCcg025.listRole());
                vm.destinationRoleId(roleId);
            });
        }

        mounted() {
            const vm = this;
            $("#multi-list_container").focus();
        }

        /**
         * CCG025
         * @returns {JQueryPromise<any>}
         */
        fetchRoleList(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred();
            vm.componentCcg025.startPage().done(() => {
                vm.listRole(vm.componentCcg025.listRole());
            });
            dfd.resolve();
            return dfd.promise();
        }

        copy(): void {
            const vm = this;
            vm.$blockui("invisible");
            if (_.isEqual(vm.sourceRoleId(), vm.destinationRoleId())){
                vm.$dialog.error({ messageId: 'Msg_828' });
                vm.$blockui("clear");
                return;
            }
            if (util.isNullOrEmpty(vm.destinationRoleId())) {
                vm.$dialog.error({ messageId: 'Msg_865' });
                vm.$blockui("clear");
                return;
            }
            let command: IDuplicateExOutCtgAuthCommand = {
                sourceRoleId: vm.sourceRoleId(),
                destinationRoleId: vm.destinationRoleId(),
                overWrite: vm.overwrite()
            };
            vm.$ajax(fetch.copy, command).then((data: any) => {
                if (data.success) {
                    let result = {
                        copyDestinationRoleId: data.copyDestinationRoleId,
                        overwrite: data.overwrite,
                        success: data.success
                    };
                    setShared('dataShareCMF006A', result);
                    vm.$dialog.info({messageId: 'Msg_15'}).then(function () {
                        vm.cancel();
                    });
                }
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("clear");
            });
        }

        cancel(): void {
            const vm = this;
            vm.$window.close();
        }
    }

    export interface IDuplicateExOutCtgAuthCommand {
        sourceRoleId: string;
        destinationRoleId: string;
        overWrite: boolean;
    }

    export interface IDataFromCMF006A {
        roleType: number;
        sourceRoleId: string;
        sourceRoleCode: string;
        sourceRolName: string;
    }

    export class Role extends ccg025.component.model.Role {
    }
}