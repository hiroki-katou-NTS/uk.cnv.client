module nts.uk.com.view.ccg025.a.component {
    import getText = nts.uk.resource.getText;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;

    export interface Option {
        roleType?: number;
        multiple?: boolean;
        currentCode?: any;
        showEmptyItem?: boolean;
        tabindex?: number;
        roleAtr?: number;
        isResize?: boolean;
        rows?: number;
        isAlreadySetting?: any;
    }

    export module viewmodel {
        export class ComponentModel {
            listRole: KnockoutObservableArray<model.Role>;
            currentCode: any;
            isAlreadySetting: KnockoutObservable<boolean>;
            private columns: KnockoutObservableArray<any>;
            private defaultOption: Option = {
                multiple: true,
                showEmptyItem: false,
                isResize: false
            }
            private setting: Option;
            private searchMode: string;

            constructor(option: Option) {
                let self = this;
                self.setting = $.extend({}, self.defaultOption, option);
                //                self.searchMode = (self.setting.multiple) ? "highlight" : "filter";
                self.listRole = ko.observableArray([]);
                self.isAlreadySetting = ko.observable(option.isAlreadySetting);
                if (self.setting.multiple) {
                    self.currentCode = ko.observableArray([]);
                    self.columns = ko.observableArray([
                        { headerText: '', prop: 'roleId', width: 100, hidden: true },
                        { headerText: getText("CCG025_3"), prop: 'roleCode', width: 100 },
                        { headerText: getText("CCG025_4"), prop: 'roleName', width: 180, formatter: _.escape },
                        {
                            headerText: getText("CCG025_9"), prop: 'configured', width: 80, hidden: !self.isAlreadySetting(),
                            template: '{{if ${configured} == 1 }}<div class="cssDiv"><i  class="icon icon icon-78 cssI"></i></div>{{/if}}'
                        }

                    ]);
                } else {
                    self.currentCode = ko.observable("");
                    self.columns = ko.observableArray([
                        { headerText: '', prop: 'roleId', width: 100, hidden: true },
                        { headerText: getText("CCG025_3"), prop: 'roleCode', width: 60 },
                        { headerText: getText("CCG025_4"), prop: 'roleName', width: 233, formatter: _.escape },
                        {
                            headerText: getText("CCG025_9"), prop: 'configured', width: 80, hidden: !self.isAlreadySetting(),
                            template: '{{if ${configured} == 1 }}<div class="cssDiv"><i  class="icon icon icon-78 cssI"></i></div>{{/if}}'
                        }
                    ]);
                }
            }

            /** 
            functiton start page */

            /**
             * truyen them 2 param tu man KSP001 sang
             */
            startPage(listRoleId?: any, selectedRoleId?: string): JQueryPromise<any> {
                let self = this;
                return listRoleId ?
                    self.getListRoleByRoleType(self.setting.roleType, self.setting.roleAtr, listRoleId, selectedRoleId) : 
                    self.getListRoleByRoleType(self.setting.roleType, self.setting.roleAtr, [], null);
            }

            /** Get list Role by Type */
            private getListRoleByRoleType(roleType: number, roleAtr: number, listRoleId: any, selectedRoleId: string): JQueryPromise<Array<model.Role>> {
                let self = this;
                let dfd = $.Deferred();
                service.getListRoleByRoleType(roleType, roleAtr).done((data: Array<model.Role>) => {
                    data = _.orderBy(data, ['assignAtr', 'roleCode'], ['asc', 'asc']);
                    self.listRole(_.map(data, (x) => {
                        return new model.Role(
                            x.roleId, x.roleCode, x.roleType,
                            x.employeeReferenceRange, x.name,
                            x.contractCode, x.assignAtr, x.companyId, _.includes(listRoleId, x.roleId) ? 1 : 0);
                    }));
                    self.addEmptyItem();

                    // Select item base on param code
                    if (!isNullOrUndefined(self.setting.currentCode)) {
                        self.currentCode(self.setting.currentCode);
                    } else if (!!selectedRoleId) {
                        self.currentCode(selectedRoleId);
                    } else {
                        self.selectFirstItem();
                    }
                    dfd.resolve(data);
                }).fail(function(res: any) {
                    dfd.reject();
                    nts.uk.ui.dialog.alertError(res.message).then(() => {
                        nts.uk.ui.block.clear();
                    });
                });
                return dfd.promise();
            }

            private addEmptyItem(): void {
                var self = this;
                if (self.setting.showEmptyItem && !self.setting.multiple) {
                    self.listRole.unshift(new model.Role("", "", 0, 0, "選択なし", "", 0, "", 0));
                }
            }

            /** Select first item */
            private selectFirstItem(): void {
                var self = this;
                if (self.listRole().length > 0) {
                    self.currentCode(self.listRole()[0].roleId);
                }
            }

        }//end screenModel
    }//end viewmodel

    //module model
    export module model {

        export interface IRole {
            roleId: string;
            roleCode: string;
            roleType: number;
            employeeReferenceRange: number;
            roleName: string;
            name: string;
            contractCode: string;
            assignAtr: number;
            companyId: string;
        }

        /** class Role */
        export class Role {
            roleId: string;
            roleCode: string;
            roleType: number;
            employeeReferenceRange: number;
            roleName: string;
            name: string;
            contractCode: string;
            assignAtr: number;
            companyId: string;
            configured: number;

            constructor(roleId: string, roleCode: string,
                roleType: number, employeeReferenceRange: number, roleName: string,
                contractCode: string, assignAtr: number, companyId: string, configured: number) {
                this.roleId = roleId;
                this.roleCode = roleCode;
                this.roleType = roleType;
                this.employeeReferenceRange = employeeReferenceRange;
                this.name = roleName;
                this.roleName = roleName;
                this.contractCode = contractCode;
                this.assignAtr = assignAtr;
                this.companyId = companyId;
                this.configured = configured;
            }
        }//end class Role


    }//end module model

}//end module