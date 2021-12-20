module nts.uk.at.view.ksp001.b.viewmodel {
    import info = nts.uk.ui.dialog.info;
    import alert = nts.uk.ui.dialog.alert;
    import alertError = nts.uk.ui.dialog.alertError;
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    import openDialog = nts.uk.ui.windows.sub.modal;
    import getText = nts.uk.resource.getText;
    import jump = nts.uk.request.jump;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    import ComponentModelCCG025 = nts.uk.com.view.ccg025.a.component.viewmodel.ComponentModel;

    export class ScreenModel {
        selectedRole: Role = new Role();
        listRole: KnockoutObservableArray<IRole> = ko.observableArray([]);
        lstSPMenuEmpDto: KnockoutObservableArray<any> = ko.observableArray([]);
        listMenuCd: any = [];
        listRoleId: KnockoutObservableArray<IRole> = ko.observableArray([]);
        items: KnockoutObservableArray<SPMenu> = ko.observableArray([]);
        displayAtrArr: KnockoutObservableArray<any> = ko.observableArray([
            { code: 1, name: getText('KSP001_17') },
            { code: 0, name: getText('KSP001_18') }
        ]);
        // gọi tới ccg025 với param đc set bên dưới
        component025: ComponentModelCCG025 = new ComponentModelCCG025({
            roleType: 3, // 就業 
            multiple: false,
            isAlreadySetting: true,
            alreadySetList: this.listRoleId
        });
        isCallGetDataByRoleId: boolean = true;
        listMenuCdNoData: KnockoutObservableArray<string> = ko.observableArray([]);
        isEnable: KnockoutObservable<boolean> = ko.observable(true);

        constructor() {
            let self = this, role = self.selectedRole;

            _.extend(self, {
                listRole: self.component025.listRole
            });

            _.extend(role, {
                roleId: self.component025.currentRoleId
            });

            // khi roleId thay đổi, get lại data cho table bên phải
            role.roleId.subscribe(rid => {
                if (self.listMenuCd.length > 0 && self.isCallGetDataByRoleId) {
                    self.getDataByRoleId();
                }
            });
        }
        
        /**
         *  get data cho table ben phai roi truyen list roleId lay duoc de hien thi setting cho table ben trai
         */
        startPage(): JQueryPromise<any> {
            block.grayout();
            let self = this, role = self.selectedRole, dfd = $.Deferred();
            $.when(self.getListMenu()).done(() => {
                self.getListRoleCcg025();
                dfd.resolve();
            }).fail(() => {
                dfd.reject();
            }).always(() => {
                block.clear();
            });

            return dfd.promise();
        }
        
        /**
         * get data cho table ben phai tu bang SPTMT_SP_MENU_K
         */
        getListMenu(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            service.getListMenu().done((data) => {
                let arr: any = [],
                    arr1: any = [];
                _.forEach(data.lstSPMenuDto, (x) => {
                    self.listMenuCd.push(x.menuCd);
                    arr1.push(new SPMenu({
                        menuCd: x.menuCd,
                        displayName: x.displayName,
                        displayOrder: x.displayOrder,
                        isChild: x.child,
                        lstChild: x.lstChild
                    }));
                });
                self.items(arr1);
                self.lstSPMenuEmpDto(data.lstSPMenuEmpDto);
                _.each(data.lstSPMenuEmpDto, data => {
                    arr.push(data.employmentRole);
                });
                // add vao listRoleId(loai bo roleId trung nhau)
                self.listRoleId(_.uniq(arr));

                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
            });
            
            return dfd.promise();
        }
        
        /**
         * truyen listRoleId cho CCG025
         */
        getListRoleCcg025(): JQueryPromise<any> {
            block.grayout();
            let self = this, dfd = $.Deferred();
            // set isCallGetDataByRoleId = false để không gọi vào hàm getDataByRoleId() 2 lần liên tiếp
            self.isCallGetDataByRoleId = false;
            self.component025.startPage(self.selectedRole.roleId()).done(() => {
                self.isEnable(self.listRole().length ? true : false);
                self.isCallGetDataByRoleId = true;
                self.getDataByRoleId();
                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
            }).always(() => {
                block.clear();
            });

            return dfd.promise();
        }
        
        /**
         * get data cho table ben phai tu table STANDAR_MENU
         */
        getListMenuRole(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            
            service.getListMenuRole().done((data) => {
                self.lstSPMenuEmpDto(data.lstSPMenuEmpDto);
                let arr: any = []
                _.each(data.lstSPMenuEmpDto, data => {
                    arr.push(data.employmentRole);
                });
                self.listRoleId(_.uniq(arr));

                self.getListRoleCcg025();
                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
            });

            return dfd.promise();
        }
        
        /**
         * Khi select roleId moi ben table ben phai thi se lay lai data cho table ben trai
         */
        getDataByRoleId(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            block.grayout();
            let listMenuCd: any = [];
            if (self.listMenuCd.length > 0) {
                service.getDataByRoleId({ roleId: self.selectedRole.roleId(), lstMenuCd: self.listMenuCd }).done((data) => {
                    self.listMenuCdNoData([]);
                    _.each(self.items(), item => {
                        let param = _.find(data, ['menuCd', ko.toJS(item).menuCd]);
                        if (param) {
                            item.displayAtr(param.displayAtr);
                        } else {
                            item.displayAtr(0);  // set defaul = 0: 使用しない
                            self.listMenuCdNoData().push(ko.toJS(item).menuCd); // add menuCd khong co data tuong ung vs roleId dang dc select
                        }
                    });
                    $($('div.swBtn')[0]).focus();
                    dfd.resolve();
                }).fail((error) => {
                    dfd.reject();
                }).always(() => {
                    block.clear();
                });
            } else {
                _.each(self.items(), item => {
                    item.displayAtr(0);  // set defaul = 0: 使用しない
                });
                $($('div.swBtn')[0]).focus();
                block.clear();
                dfd.resolve();
            }

            return dfd.promise();
        }
        
        /**
         * save data
         */
        saveData(): JQueryPromise<any> {
            block.grayout();
            let self = this, dfd = $.Deferred();
            let command: any = [];
            _.forEach(ko.toJS(self.items()), item => {
                command.push({
                    menuCd: item.menuCd,
                    employmentRole: self.selectedRole.roleId(),
                    displayAtr: item.displayAtr
                });
            });
            service.saveData({ lstSPMenuEmp: command }).done(() => {
                // get lai list data moi cho table ben phai
                self.getListMenuRole().done(() => {
                    info({ messageId: "Msg_15" });
                    $($('div.swBtn')[0]).focus();
                    dfd.resolve();
                }).fail((error) => {
                    dfd.reject();
                });
            }).fail((error) => {
                dfd.reject();
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }
        
        /**
         * open C
         */
        openC(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            openDialog("/view/ksp/001/c/index.xhtml").onClosed(() => {
                // get data to change menuName
                service.getMenuSpecial().done((data) => {
                    _.forEach(data.lstSPMenuDto, dt => {
                        _.find(self.items(), (item) => {
                            return item.menuCd() == dt.menuCd;
                        }).displayName(dt.displayName);
                    });
                    dfd.resolve();
                }).fail((err) => {
                    dfd.reject();
                });
            });
            return dfd.promise();
        }
        
        /**
         * copy setting cua roleId cho cac roleId khac
         */
        copy(): JQueryPromise<any> {
            let self = this,
                role = _.find(self.listRole(), ['roleId', self.selectedRole.roleId()]),
                dfd = $.Deferred();
            if (!role.configured) {
                alertError({ messageId: "Msg_1510" });
                return;
            }

            let object: any = {
                code: role.roleCode,
                name: role.name,
                targetType: 8, // ロール
                itemListSetting: self.listRoleId(),
                roleType: 3
            };
            setShared("CDL023Input", object);
            nts.uk.ui.windows.sub.modal('com', '/view/cdl/023/a/index.xhtml').onClosed(function() {
                block.grayout();
                let lstSelection = getShared("CDL023Output");
                if (nts.uk.util.isNullOrEmpty(lstSelection)) {
                    dfd.resolve();
                    block.clear();
                    return;
                }

                let arrToSave: any = [];
                // không lấy lại domain ドメインモデル「スマホメニュー（就業）」を取得する mà dùng data trên màn hình cho đỡ respone
                // lọc data nhận được để xem roleId đã setting =>  update, chưa setting => insert
                _.forEach(lstSelection, id => {
                    _.each(ko.toJS(self.items()), x => {
                        // menu co data(khong nam trong listMenuCdNoData) thi moi duoc insert
                        if (!_.includes(self.listMenuCdNoData(), x.menuCd)) {
                            arrToSave.push({
                                menuCd: x.menuCd,
                                employmentRole: id,
                                displayAtr: x.displayAtr
                            });
                        }
                    });
                });

                service.saveData({ lstSPMenuEmp: arrToSave }).done(data => {
                    self.getListMenuRole().done(() => {
                        info({ messageId: "Msg_926" });
                        $($('div.swBtn')[0]).focus();
                        dfd.resolve();
                    }).fail((error) => {
                        dfd.reject();
                    });
                }).fail(err => {
                    dfd.reject();
                }).always(() => {
                    block.clear();
                });
            });

            return dfd.promise();
        }

    }

    export interface ISPMenu {
        menuCd: string;
        displayName: string;
        displayOrder: number;
        isChild: boolean;
        lstChild: any;
    }

    export class SPMenu {
        menuCd: KnockoutObservable<string>;
        displayName: KnockoutObservable<string>;
        displayAtr: KnockoutObservable<number>;
        displayOrder: KnockoutObservable<number>;
        isChild: KnockoutObservable<boolean>;
        isParent: KnockoutObservable<boolean>;

        constructor(param: ISPMenu) {
            let self = this;
            self.menuCd = ko.observable(param.menuCd);
            self.displayName = ko.observable(param.displayName);
            self.displayAtr = ko.observable(0); // set defaul = 0: 使用しない
            self.displayOrder = ko.observable(param.displayOrder);
            self.isChild = ko.observable(param.isChild);
            self.isParent = ko.observable(!!param.lstChild.length);
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

        constructor(params: IRole) {
            let self = this;
            //            self.roleId = ko.observable(params.roleId);
            //            self.roleCode = ko.observable(params.roleCode);
            //            self.roleName = ko.observable(params.name);
        }
   
    }
}
