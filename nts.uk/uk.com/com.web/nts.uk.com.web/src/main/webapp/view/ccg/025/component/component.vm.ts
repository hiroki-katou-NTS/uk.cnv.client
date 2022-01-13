module nts.uk.com.view.ccg025.a.component {
    import getText = nts.uk.resource.getText;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;

    export interface Option {
        roleType?: number;
        multiple?: boolean;
        selectedId?: any;
        showEmptyItem?: boolean;
        tabindex?: number;
        roleAtr?: number;
        isResize?: boolean;
        rows?: number;
        isAlreadySetting?: any;
        alreadySetList?: any;
        selectType?: number;
        onDialog?: boolean;
        hasFocus?: boolean;
    }

    export module viewmodel {
        export class ComponentModel {
            listRole: KnockoutObservableArray<model.Role>;
            listAlreadySet: KnockoutObservableArray<any>;
            currentRoleId: any;
            isAlreadySetting: KnockoutObservable<boolean>;
            private columns: KnockoutObservableArray<any>;
            private defaultOption: Option = {
                multiple: true,
                showEmptyItem: false,
                isResize: false,
                rows: 15,
                selectType: 3
            };
            private setting: Option;
            displayRoleClassification: KnockoutObservable<boolean>;
            roleClassification: KnockoutObservable<number>;
            switchFocus: KnockoutObservable<boolean> = ko.observable(false);
            listFocus: KnockoutObservable<boolean> = ko.observable(false);
            init: boolean = true;

            constructor(option: Option) {
                let self = this;
                self.setting = $.extend({}, self.defaultOption, option);
                if (isNullOrUndefined(option.selectType) && !isNullOrUndefined(self.setting.selectedId)) self.setting.selectType = 1;
                if (self.setting.roleAtr != 0 && self.setting.roleAtr != 1) self.setting.roleAtr = undefined;
                self.displayRoleClassification = ko.observable(self.setting.roleAtr != 0 && self.setting.roleAtr != 1);
                if (self.setting.hasFocus) {
                    if (self.displayRoleClassification()) self.switchFocus(true); else self.listFocus(true);
                }
                self.roleClassification = ko.observable(self.setting.roleAtr || 0);
                self.listRole = ko.observableArray([]);
                self.isAlreadySetting = ko.observable(self.setting.isAlreadySetting);
                if (!_.isEmpty(self.setting.alreadySetList)) {
                    if (ko.isObservable(self.setting.alreadySetList)) self.listAlreadySet = self.setting.alreadySetList;
                    else self.listAlreadySet = ko.observableArray(self.setting.alreadySetList);
                } else {
                    self.listAlreadySet = ko.observableArray([]);
                }
                if (self.setting.multiple) {
                    self.currentRoleId = ko.observableArray([]);
                    self.columns = ko.observableArray([
                        { headerText: '', prop: 'roleId', width: 100, hidden: true },
                        { headerText: getText("CCG025_6"), prop: 'roleCode', width: 100 },
                        { headerText: getText("CCG025_7"), prop: 'roleName', width: 180, formatter: _.escape },
                        {
                            headerText: getText("CCG025_8"), prop: 'configured', width: 80, hidden: !self.isAlreadySetting(),
                            template: '{{if ${configured} == 1 }}<div class="cssDiv"><i  class="icon icon icon-78 cssI"></i></div>{{/if}}'
                        }

                    ]);
                } else {
                    self.currentRoleId = ko.observable("");
                    self.columns = ko.observableArray([
                        { headerText: '', prop: 'roleId', width: 100, hidden: true },
                        { headerText: getText("CCG025_6"), prop: 'roleCode', width: 60 },
                        { headerText: getText("CCG025_7"), prop: 'roleName', width: 233, formatter: _.escape },
                        {
                            headerText: getText("CCG025_8"), prop: 'configured', width: 80, hidden: !self.isAlreadySetting(),
                            template: '{{if ${configured} == 1 }}<div class="cssDiv"><i  class="icon icon icon-78 cssI"></i></div>{{/if}}'
                        }
                    ]);
                }
                self.roleClassification.subscribe(value => {
                    nts.uk.ui.block.invisible();
                    self.getListRoleByRoleType(self.setting.roleType, value).always(() => {
                        nts.uk.ui.block.clear();
                    });
                });
            }

            /** 
            functiton start page */

            /**
             * truyen them 2 param tu man KSP001 sang
             */
            startPage(selectedRoleId?: string, selectedRoleCode?: string): JQueryPromise<any> {
                let self = this;
                return self.getListRoleByRoleType(self.setting.roleType, self.roleClassification(), selectedRoleId, selectedRoleCode);
            }

            /** Get list Role by Type */
            private getListRoleByRoleType(roleType: number, roleAtr: number, selectedRoleId?: string, selectedRoleCode?: string): JQueryPromise<Array<model.Role>> {
                let self = this;
                let dfd = $.Deferred();
                service.getListRoleByRoleType(roleType, roleAtr).done((data: Array<model.Role>) => {
                    data = _.orderBy(data, ['assignAtr', 'roleCode'], ['asc', 'asc']);
                    self.listRole(_.map(data, (x) => {
                        return new model.Role(
                            x.roleId, x.roleCode, x.roleType,
                            x.employeeReferenceRange, x.name,
                            x.contractCode, x.assignAtr, x.companyId, _.includes(self.listAlreadySet(), x.roleId) ? 1 : 0);
                    }));
                    self.addEmptyItem();

                    if (!!selectedRoleId) {
                        self.setting.multiple ? self.currentRoleId([selectedRoleId]) : self.currentRoleId(selectedRoleId);
                    } else if (!!selectedRoleCode) {
                        const role = _.find(self.listRole(), (r: any) => _.isEqual(r.roleCode, selectedRoleCode));
                        self.setting.multiple ? self.currentRoleId(role ? [role.roleId] : []) : self.currentRoleId(role ? role.roleId : undefined);
                    } else {
                        switch (self.setting.selectType) {
                            case 1: // selected list
                                if (self.setting.multiple) {
                                    if (self.listRole().filter(r => (self.setting.selectedId || []).indexOf(r.roleId) >= 0).length > 0) {
                                        self.currentRoleId(self.setting.selectedId || [])
                                    } else {
                                        self.selectFirstItem();
                                    }
                                } else {
                                    if (self.listRole().filter(r => self.setting.selectedId == r.roleId).length > 0) {
                                        self.currentRoleId(self.setting.selectedId)
                                    } else {
                                        self.selectFirstItem();
                                    }
                                }
                                break;
                            case 2: // select all
                                if (self.setting.multiple) {
                                    self.currentRoleId(self.listRole().map(r => r.roleId));
                                }
                                break;
                            case 3: // select first
                                self.selectFirstItem();
                                break;
                            case 4: // select none
                                self.setting.multiple ? self.currentRoleId([]) : self.currentRoleId(undefined);
                                break;
                            default:
                                break;
                        }
                    }

                    if (self.init) {
                        if (self.listFocus()) $("#ccg025-list_container").focus();
                        self.init = false;
                    }
                    dfd.resolve(data);
                }).fail(function(res: any) {
                    dfd.reject();
                    nts.uk.ui.dialog.alertError(res.message).then(() => {
                        nts.uk.ui.block.clear();
                    });
                });
                return dfd.promise() as JQueryPromise<Array<model.Role>>;
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
                    self.setting.multiple
                        ? self.currentRoleId([self.listRole()[0].roleId])
                        : self.currentRoleId(self.listRole()[0].roleId);
                } else {
                    self.setting.multiple
                        ? self.currentRoleId([])
                        : self.currentRoleId(undefined);
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