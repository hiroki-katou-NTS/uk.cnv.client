    // blockui all ajax request on layout
    $(document)
        .ajaxStart(() => {
            $.blockUI({
                message: null,
                overlayCSS: { opacity: 0.1 }
            });
        }).ajaxStop(() => {
            $.unblockUI();
        });


module nts.uk.com.view.cas001.a.viewmodel {
    import alert = nts.uk.ui.dialog.alert;
    import getText = nts.uk.resource.getText;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import dialog = nts.uk.ui.dialog.info;
    import ccg = nts.uk.com.view.ccg025.a;
    import model = nts.uk.com.view.ccg025.a.component.model;
    import color = nts.uk.ui.jqueryExtentions.ntsGrid.color;
    import block = nts.uk.ui.block;
    
    export class ScreenModel {
        langId: KnockoutObservable<string> = ko.observable('ja');
        personRoleList: KnockoutObservableArray<PersonRole> = ko.observableArray([]);
        currentRole: KnockoutObservable<PersonRole> = ko.observable(new PersonRole({ roleId: "0001", 
        roleCode: "001", name: "A", 
        allowMapBrowse: 1, allowMapUpload: 1, 
        allowDocUpload: 1, allowDocRef: 1, 
        allowAvatarUpload: 1, allowAvatarRef: 1 }));
        currentRoleId: KnockoutObservable<string> = ko.observable('');
        roundingRules: KnockoutObservableArray<any> = ko.observableArray([
            { code: 1, name: getText('Enum_PersonInfoPermissionType_YES') },
            { code: 0, name: getText('Enum_PersonInfoPermissionType_NO') }
        ]);
        itemListCbb: KnockoutObservableArray<any> = ko.observableArray([
            { code: 1, name: getText('CAS001_49') },
            { code: 2, name: getText('CAS001_50') },
            { code: 3, name: getText('CAS001_51') }
        ]);
        anotherSelectedAll: KnockoutObservable<number> = ko.observable(1);
        seftSelectedAll: KnockoutObservable<number> = ko.observable(1);
        currentCategoryId: KnockoutObservable<string> = ko.observable('');
        allowPersonRef: KnockoutObservable<number> = ko.observable(1);
        allowOtherRef: KnockoutObservable<number> = ko.observable(1);
        roleCategoryList: KnockoutObservableArray<PersonRoleCategory> = ko.observableArray([]);
        checkboxSelectedAll: KnockoutObservable<boolean> = ko.observable(false);
        component: ccg.component.viewmodel.ComponentModel = new ccg.component.viewmodel.ComponentModel({
            roleType: 8,
            multiple: false,
            isResize: true,
            rows: 5,
            tabindex: 5,
            onDialog: true
        });
        listRole: KnockoutObservableArray<PersonRole> = ko.observableArray([]);
        ctgColumns: KnockoutObservableArray<any> = ko.observableArray([
            { headerText: 'コード', key: 'categoryId', width: 205, hidden: true },
            { headerText: getText('CAS001_11'), key: 'categoryName', width: 170 },
            {
                headerText: getText('CAS001_69'), key: 'setting', width: 80, formatter: makeIcon
            }
        ]);
        isFromCPS018: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            let self = this;

            let params = getShared("CAS001A_PARAMS") || { isFromCPS018: false };
            self.isFromCPS018(params.isFromCPS018);
            nts.uk.sessionStorage.removeItem(nts.uk.request.STORAGE_KEY_TRANSFER_DATA);

            block.grayout();
            self.component.columns([
                { headerText: getText("CCG025_3"), prop: 'roleId', width: 50, hidden: true },
                { headerText: getText("CCG025_3"), prop: 'roleCode', width: 50 },
                { headerText: getText("CCG025_4"), prop: 'name', width: 205 }
            ]);
            self.component.startPage().done(() =>{
                self.personRoleList.removeAll();
                self.personRoleList(_.map(self.component.listRole(), (x: any) => new PersonRole(x)));
                self.start();
            });

            self.component.listRole.subscribe(value => {
                self.personRoleList.removeAll();
                self.personRoleList(_.map(value, (x: any) => new PersonRole(x)));
                self.start();
            });
            
            self.component.currentRoleId.subscribe(function(newRoleId: string) {
                if (self.personRoleList().length < 1) {
                    return;
                }
                self.currentRoleId(newRoleId);
            });

            self.currentRoleId.subscribe(function(newRoleId) {

                if (newRoleId == "" || self.personRoleList().length < 1) {
                    return;
                }
                let newPersonRole = _.find(self.personRoleList(), (role) => { return role.roleId === newRoleId });
                if (newPersonRole) {
                    self.currentRole(newPersonRole);
                    block.grayout();
                    newPersonRole.loadRoleCategoriesList(newRoleId, false).done(() => {
                        if (!self.currentCategoryId()) {
                            newPersonRole.setCtgSelectedId(self.roleCategoryList());
                        }
                    }).always(() => {
                        block.clear();
                    });
                }
            });

            self.currentCategoryId.subscribe((categoryId) => {
                if (!categoryId) {
                    return;
                }
                let newCategory = _.find(self.roleCategoryList(), (roleCategory) => {
                    return roleCategory.categoryId === categoryId;
                });
                block.grayout();
                service.getCategoryAuth(self.currentRoleId(), categoryId).done((result: IPersonRoleCategory) => {
                    newCategory.loadRoleItems(self.currentRoleId(), categoryId).done(() => {
                        newCategory.setCategoryAuth(result);
                        self.currentRole().currentCategory(newCategory);
                        let currentCategory = _.find(self.roleCategoryList(), roleCate => roleCate.categoryId == categoryId);
                        if (currentCategory && currentCategory.categoryCode == 'CS00100') {
                            self.itemListCbb([
                                { code: 1, name: getText('CAS001_49') },
                                { code: 3, name: getText('CAS001_51') }
                            ]);
                            if (self.anotherSelectedAll() == 2) {
                                self.anotherSelectedAll(1);
                            }
                            if (self.seftSelectedAll() == 2) {
                                self.seftSelectedAll(1);
                            }
                        } else {
                            self.itemListCbb([
                                { code: 1, name: getText('CAS001_49') },
                                { code: 2, name: getText('CAS001_50') },
                                { code: 3, name: getText('CAS001_51') }
                            ]);
                        }
                    }).always(() => {
                        block.clear(); 
                    });
                });
            });

            self.checkboxSelectedAll.subscribe((newValue) => {
                if (!self.currentRole().currentCategory()) {
                    return;
                }
                let currentList = self.currentRole().currentCategory().roleItemList();
                _.forEach(currentList, (item) => {
                    item.isChecked = newValue;
                });
            });

            self.allowOtherRef.subscribe((newValue) => {

                let grid = $("#item_role_table_body");
                let ds = grid.igGrid("option", "dataSource");
                if (ds == null) {
                    return;
                }

                grid.ntsGrid(newValue == 0 ? "disableNtsControls" : "enableNtsControls", "otherAuth", "SwitchButtons");
                grid.ntsGrid(self.isDisableAll() ? "disableNtsControls" : "enableNtsControls", "isChecked", "CheckBox", true);

                if (newValue == 0) {
                    $("#anotherSelectedAll_auth > button.nts-switch-button").prop('disabled', true);
                } else {
                    $("#anotherSelectedAll_auth > button.nts-switch-button").prop('disabled', false);
                }
            });

            self.allowPersonRef.subscribe((newValue) => {

                let grid = $("#item_role_table_body");
                let ds = grid.igGrid("option", "dataSource");
                if (ds == null) {
                    return;
                }

                grid.ntsGrid(newValue == 0 ? "disableNtsControls" : "enableNtsControls", "selfAuth", "SwitchButtons");
                grid.ntsGrid(self.isDisableAll() ? "disableNtsControls" : "enableNtsControls", "isChecked", "CheckBox", true);

                if (newValue == 0) {
                    $("#seftSelectedAll_auth > button.nts-switch-button").prop('disabled', true);
                } else {
                    $("#seftSelectedAll_auth > button.nts-switch-button").prop('disabled', false);
                }
            });

        }

        switchModeAllowOtherRef = ko.computed(()=>{
            let vm =this;
            return vm.allowOtherRef()===1;
        });

        switchModeAllowPersonRef = ko.computed(()=>{
            let vm =this;
            return vm.allowPersonRef()===1;
        });

        changeAll(parrentId, changeValue) {
            let self = this,
                grid = $("#item_role_table_body"),
                currentList = $("#item_role_table_body").ntsGrid("updatedCells"),
                itemCheckLst = _.filter(currentList, { columnKey: "isChecked", value: true }),
                changeVal = changeValue < 1 ? 1 : changeValue > 3 ? 3 : changeValue,
                itemLst2E = self.currentRole().currentCategory().itemLst2E;

            if (itemCheckLst.length <= 0) {
                dialog({ messageId: "Msg_664" });
                return;
            }

            _.forEach(itemCheckLst, (item) => {
                if (item.value) {
                    grid.ntsGrid("updateRow", item.rowId, parrentId === 'anotherSelectedAll_auth' ? { otherAuth: String(changeVal) } : { selfAuth: String(changeVal) });
                }
            });

            _.forEach(itemLst2E, (item2E) => {
                _.forEach(itemCheckLst, (item) => {
                    if (item.value && item.rowId === item2E.personItemDefId) {
                        changeVal = (changeValue < 1) ? 1 : (changeValue > 2) ? 2 : changeValue;
                        setTimeout(function() {
                            grid.ntsGrid("updateRow", item2E.personItemDefId, parrentId === 'anotherSelectedAll_auth' ? { otherAuth: String(changeVal) } : { selfAuth: String(changeVal) });
                        }, 1);
                    }
                });
            });

        }

        openDModal() {

            let self = this;

            setShared('personRole', self.currentRole());

            nts.uk.ui.windows.sub.modal('/view/cas/001/d/index.xhtml', { title: '' }).onClosed(function(): any {

                if (!getShared('isCanceled')) {
                    self.reload().always(() => {
                    });
                }
            });
        }
        

        openCModal() {

            let self = this,
                currentRole = {
                    roleList: self.personRoleList(),
                    personRole: self.currentRole()
                };

            setShared('currentRole', currentRole);

            nts.uk.ui.windows.sub.modal('/view/cas/001/c/index.xhtml', { title: '' }).onClosed(function(): any {

                let objSetofScreenC = getShared('isCanceled');
                if (!objSetofScreenC.isCancel) {
                    self.reload().always(() => {
                        if (objSetofScreenC.id !== null && objSetofScreenC.id != undefined) {
                            self.component.currentRoleId(objSetofScreenC.id);
                        }
                    });
                }
            });
        }

        reload(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred(),
                personRole = self.currentRole(),
                selectedId = self.currentCategoryId(),
                grid = $("#item_role_table_body");;
            nts.uk.ui.block.grayout();
            personRole.loadRoleCategoriesList(personRole.roleId, true).done(function() {

                if (self.roleCategoryList().length > 0) {

                    self.currentRole().currentCategory().loadRoleItems(self.currentRoleId(), selectedId).done(function() {
                        self.currentCategoryId.valueHasMutated();
                        self.checkboxSelectedAll(false);
                        let allowPerson = self.allowPersonRef(),
                            allowOther = self.allowOtherRef();
                        self.allowPersonRef(!allowPerson);
                        self.allowPersonRef(allowPerson);
                        self.allowOtherRef(!allowOther);
                        self.allowOtherRef(allowOther);
                    });

                } else {
                    dialog({ messageId: "Msg_217" });
                }
                block.clear(); 
                dfd.resolve();
            });

            return dfd.promise();
        }

        start() {
            let self = this;
                if (self.personRoleList().length > 0) {
                    let selectedId = self.currentRoleId() !== '' ? self.currentRoleId() : self.personRoleList()[0].roleId;
                    self.currentRoleId(selectedId);
                } else {
                    dialog({ messageId: "Msg_364" }).then(function() {
                        nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                    });
                }
        }

        saveData() {
            let self = this,

                command = self.createSaveCommand();

            service.savePersonRole(command).done(function() {

                dialog({ messageId: "Msg_15" }).then(function() {

                    self.reload().always(() => {

                    });
                });

            }).fail(function(res) {

                alert(res);

            });
        }
        createSaveCommand() {

            let self = this;

            return new PersonRoleCommand(self.currentRole());

        }

        isHistoryNotCons() {
            let self = this,
                currentCtg = self.currentRole().currentCategory();
            return (currentCtg.categoryType !== 1 && currentCtg.personEmployeeType === 2);
        }

        genCategoryTypeText() {
            let self = this,
                currentCtgType = self.currentRole().currentCategory().categoryType;

            switch (currentCtgType) {
                case 1: return getText('Enum_CategoryType_SINGLE_INFO');
                case 2: return getText('Enum_CategoryType_MULTI_INFO');
                case 3: return getText('Enum_CategoryType_CONTINUOUS_HISTORY');
                case 4: return getText('Enum_CategoryType_NODUPLICATE_HISTORY');
                case 5: return getText('Enum_CategoryType_DUPLICATE_HISTORY');
                case 6: return getText('Enum_CategoryType_CONTINUOUS_HISTORY');
                default: return '';

            }
        }

        isDisableAll() {
            let self = this;
            return self.allowPersonRef() === 0 && self.allowOtherRef() === 0;
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
        /**
         * check role
         */
        hasPermission(): boolean {
            if (__viewContext.user.role.attendance == "null" && __viewContext.user.role.payroll == "null"
                && __viewContext.user.role.personnel == "null"  && __viewContext.user.role.officeHelper == "null"){
                return false;
            }
            
            return true;
        }
    }
    export interface IPersonRole {
        roleId: string;
        roleCode: string;
        name: string;
        allowMapBrowse: number;
        allowMapUpload: number;
        allowDocUpload: number;
        allowDocRef: number;
        allowAvatarUpload: number;
        allowAvatarRef: number;
    }
    export interface IPersonRoleCategory {
        categoryId: string;
        categoryCode: string;
        categoryName: string;
        setting: boolean;
        categoryType: number;
        personEmployeeType: number;
        allowPersonRef: number;
        allowOtherRef: number;
        allowOtherCompanyRef: number;
        selfPastHisAuth: number;
        selfFutureHisAuth: number;
        selfAllowDelHis: number;
        selfAllowAddHis: number;
        otherPastHisAuth: number;
        otherFutureHisAuth: number;
        otherAllowDelHis: number;
        otherAllowAddHis: number;
        selfAllowDelMulti: number;
        selfAllowAddMulti: number;
        otherAllowDelMulti: number;
        otherAllowAddMulti: number;

    }
    export interface IPersonRoleItem {
        personItemDefId: string;
        setting: boolean;
        requiredAtr: string;
        itemName: string;
        parrentCd: string;
        otherAuth: number;
        selfAuth: number;
        dataType: number;
        isConvert: boolean;
    }

    export class PersonRole {
        roleId: string;
        roleCode: string;
        roleName: string;
        currentCategory: KnockoutObservable<PersonRoleCategory> = ko.observable(new PersonRoleCategory({
            categoryId: "",
            categoryCode: "",
            categoryName: "",
            setting: true,
            categoryType: -1,
            personEmployeeType: 2,
            allowPersonRef: 1,
            allowOtherRef: 1,
            allowOtherCompanyRef: 1,
            selfPastHisAuth: 1,
            selfFutureHisAuth: 1,
            selfAllowDelHis: 1,
            selfAllowAddHis: 1,
            otherPastHisAuth: 1,
            otherFutureHisAuth: 1,
            otherAllowDelHis: 1,
            otherAllowAddHis: 1,
            selfAllowDelMulti: 1,
            selfAllowAddMulti: 1,
            otherAllowDelMulti: 1,
            otherAllowAddMulti: 1
        }));

        constructor(param: IPersonRole) {
            let self = this;
            self.roleId = param ? param.roleId : '';
            self.roleCode = param ? param.roleCode : '';
            self.roleName = param ? param.name : '';

        }

        setRoleAuth(param: IPersonRole) {
            let self = this;
        }


        loadRoleCategoriesList(RoleId, isReload): JQueryPromise<any> {
            var self = this,
                dfd = $.Deferred();
            let screenModel = __viewContext['screenModel'];

            service.getCategoryRoleList(RoleId).done(function(result: Array<IPersonRoleCategory>) {

                if (result.length <= 0) {
                    screenModel.roleCategoryList(_.map(result, x => new PersonRoleCategory(x)));
                    dialog({ messageId: "Msg_217" });
                    dfd.resolve();
                }

                if (!isReload) {
                    if (screenModel.currentCategoryId()) {

                        self.setCtgSelectedId(result);
                    }
                }
                screenModel.roleCategoryList(_.map(result, x => new PersonRoleCategory(x)));




                dfd.resolve();
            });
            return dfd.promise();
        }

        setCtgSelectedId(result) {
            let screenModel = __viewContext['screenModel'];
            let oldValue = screenModel.currentCategoryId();

            screenModel.currentCategoryId(result[0].categoryId);

            if (screenModel.currentCategoryId() == oldValue) {

                screenModel.currentCategoryId.valueHasMutated();

            }
        }
    }

    export class PersonRoleCategory {

        categoryId: string;
        categoryCode: string;
        categoryName: string;
        categoryType: number;
        setting: boolean;
        personEmployeeType: number;
        allowOtherCompanyRef: KnockoutObservable<number>;
        selfPastHisAuth: KnockoutObservable<number>;
        selfFutureHisAuth: KnockoutObservable<number>;
        selfAllowDelHis: KnockoutObservable<number>;
        selfAllowAddHis: KnockoutObservable<number>;
        otherPastHisAuth: KnockoutObservable<number>;
        otherFutureHisAuth: KnockoutObservable<number>;
        otherAllowDelHis: KnockoutObservable<number>;
        otherAllowAddHis: KnockoutObservable<number>;
        selfAllowDelMulti: KnockoutObservable<number>;
        selfAllowAddMulti: KnockoutObservable<number>;
        otherAllowDelMulti: KnockoutObservable<number>;
        otherAllowAddMulti: KnockoutObservable<number>;
        roleItemList: KnockoutObservableArray<PersonRoleItem> = ko.observableArray([]);
        roleItemDatas: KnockoutObservableArray<PersonRoleItem> = ko.observableArray([]);
        itemListCbb: KnockoutObservableArray<any> = ko.observableArray([
            { code: 1, name: getText('CAS001_49') },
            { code: 2, name: getText('CAS001_50') },
            { code: 3, name: getText('CAS001_51') }
        ]);

        anotherSelectedAll: KnockoutObservable<number> = ko.observable(1);
        seftSelectedAll: KnockoutObservable<number> = ko.observable(1);
        allowPersonRef: KnockoutObservable<number> = ko.observable(1);
        allowOtherRef: KnockoutObservable<number> = ko.observable(1);
        itemLst2E: Array<any> = [];

        constructor(param: IPersonRoleCategory) {
            let self = this;
            self.categoryId = param ? param.categoryId : '';
            self.categoryCode = param ? param.categoryCode : '';
            self.categoryName = param ? param.categoryName : '';
            self.categoryType = param ? param.categoryType : 0;
            self.setting = param ? param.setting : false;
            self.personEmployeeType = param ? param.personEmployeeType : 0;
            self.allowOtherCompanyRef = ko.observable(param ? param.allowOtherCompanyRef : 0);
            self.selfPastHisAuth = ko.observable(param ? param.selfPastHisAuth : 1);
            self.selfFutureHisAuth = ko.observable(param ? param.selfFutureHisAuth : 1);
            self.selfAllowDelHis = ko.observable(param ? param.selfAllowDelHis : 0);
            self.selfAllowAddHis = ko.observable(param ? param.selfAllowAddHis : 0);
            self.otherPastHisAuth = ko.observable(param ? param.otherPastHisAuth : 1);
            self.otherFutureHisAuth = ko.observable(param ? param.otherFutureHisAuth : 1);
            self.otherAllowDelHis = ko.observable(param ? param.otherAllowDelHis : 0);
            self.otherAllowAddHis = ko.observable(param ? param.otherAllowAddHis : 0);
            self.selfAllowDelMulti = ko.observable(param ? param.selfAllowDelMulti : 0);
            self.selfAllowAddMulti = ko.observable(param ? param.selfAllowAddMulti : 0);
            self.otherAllowDelMulti = ko.observable(param ? param.otherAllowDelMulti : 0);
            self.otherAllowAddMulti = ko.observable(param ? param.otherAllowAddMulti : 0);
        }

        setCategoryAuth(param: IPersonRoleCategory) {

            let self = this,
                screenModel = __viewContext['screenModel'];
            screenModel.allowPersonRef(param ? param.allowPersonRef : 0);
            screenModel.allowOtherRef(param ? param.allowOtherRef : 0);
            let allowPerson = screenModel.allowPersonRef(),
                allowOther = screenModel.allowOtherRef();
            screenModel.allowPersonRef(!allowPerson);
            screenModel.allowPersonRef(allowPerson);
            screenModel.allowOtherRef(!allowOther);
            screenModel.allowOtherRef(allowOther);

            self.allowOtherCompanyRef = ko.observable(param ? param.allowOtherCompanyRef : 0);
            self.selfPastHisAuth = ko.observable(param ? param.selfPastHisAuth : 1);
            self.selfFutureHisAuth = ko.observable(param ? param.selfFutureHisAuth : 1);
            self.selfAllowDelHis = ko.observable(param ? param.selfAllowDelHis : 0);
            self.selfAllowAddHis = ko.observable(param ? param.selfAllowAddHis : 0);
            self.otherPastHisAuth = ko.observable(param ? param.otherPastHisAuth : 1);
            self.otherFutureHisAuth = ko.observable(param ? param.otherFutureHisAuth : 1);
            self.otherAllowDelHis = ko.observable(param ? param.otherAllowDelHis : 0);
            self.otherAllowAddHis = ko.observable(param ? param.otherAllowAddHis : 0);
            self.selfAllowDelMulti = ko.observable(param ? param.selfAllowDelMulti : 0);
            self.selfAllowAddMulti = ko.observable(param ? param.selfAllowAddMulti : 0);
            self.otherAllowDelMulti = ko.observable(param ? param.otherAllowDelMulti : 0);
            self.otherAllowAddMulti = ko.observable(param ? param.otherAllowAddMulti : 0);
        }


        // hàm khởi tạo
        loadRoleItems(roleId, CategoryId): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred(),
                grid = $("#item_role_table_body"),
                screenModel = __viewContext['screenModel'],
                switchString = `<div id='{0}_auth' class='selected_all_auth'
                                    data-bind="ntsSwitchButton: {
                                        options: itemListCbb,
                                        optionsValue:'code',
                                        optionsText: 'name',
                                        value: {0},
                                        enable: {1} }" tabindex={2}></div>
                                <span id='selected_all_caret' class='caret-bottom outline'></span>`,
                selectedAllString = nts.uk.text.format(switchString, 'anotherSelectedAll', '!!allowOtherRef','22'),
                selfSelectedAllString = nts.uk.text.format(switchString, 'seftSelectedAll', '!!allowPersonRef', '23');
            

            let array2E = [{
                value: '1',
                text: getText('Enum_PersonInfoAuthTypes_HIDE')
            }, {
                    value: '2',
                    text: getText('Enum_PersonInfoAuthTypes_REFERENCE')
                }],
                array3E = [{
                    value: '1',
                    text: getText('CAS001_49')
                }, {
                        value: '2',
                        text: getText('CAS001_50')
                    }, {
                        value: '3',
                        text: getText('CAS001_51')
                    }];

            let currentCategory = _.find(screenModel.roleCategoryList(), (roleCate: PersonRoleCategory) => roleCate.categoryId == CategoryId);
            if (currentCategory && currentCategory.categoryCode == 'CS00100') {
                array3E = [{
                    value: '1',
                    text: getText('CAS001_49')
                }, {
                        value: '3',
                        text: getText('CAS001_51')
                    }]
            }

            service.getPersonRoleItemList(roleId, CategoryId).done(function(result: any) {
                self.roleItemDatas(_.map(result.itemLst, x => new PersonRoleItem(x)));
                self.roleItemList(_.filter(_.map(result.itemLst, x => new PersonRoleItem(x)), ['parrentCd', null]));
                self.itemLst2E = result.itemReadLst;
                if (self.roleItemList().length < 1) {
                    dialog({ messageId: "Msg_217" });
                }
                let option2E: string = "{ ";

                for (var i = 0; i < result.itemReadLst.length; i++) {
                    if (i === (result.itemReadLst.length - 1)) {
                        option2E = option2E + '"' + result.itemReadLst[i].personItemDefId + '"' + ':' + '["1","2"]' + '}';
                    } else {
                        option2E = option2E + '"' + result.itemReadLst[i].personItemDefId + '"' + ':' + '["1","2"], ';
                    }
                }

                if ($("#item_role_table_body").data("igGrid")) {
                    $("#item_role_table_body").ntsGrid("destroy");
                }
                $("#item_role_table_body").ntsGrid({
                    width: '835px',
                    height: '315px',
                    dataSource: self.roleItemList(),
                    primaryKey: 'personItemDefId',
                    //                    hidePrimaryKey: true,
                    rowVirtualization: true,
                    virtualization: true,
                    virtualizationMode: 'continuous',
                    columns: [
                        { headerText: '', key: 'personItemDefId', dataType: 'string', width: '34px', hidden: true },
                        { headerText: '', key: 'isChecked', dataType: 'boolean', width: '48px', ntsControl: 'Checkbox', showHeaderCheckbox: true },
                        { headerText: getText('CAS001_69'), key: 'setting', dataType: 'string', width: '48px', formatter: makeIcon },
                        { headerText: getText('CAS001_47'), key: 'itemName', dataType: 'string', width: '255px' },
                        { headerText: getText('CAS001_48') + selectedAllString, key: 'otherAuth', dataType: 'string', width: '232px', ntsControl: 'SwitchButtons1' },
                        { headerText: getText('CAS001_52') + selfSelectedAllString, key: 'selfAuth', dataType: 'string', width: '232', ntsControl: 'SwitchButtons2' },
                    ],
                    
                    ntsControls: [
                        { name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                        {
                            name: 'SwitchButtons1',
                            options: array3E,
                            optionsValue: 'value',
                            optionsText: 'text',
                            controlType: 'SwitchButtons',
                            enable: true,
                            distinction: option2E == "{ " ? {} : JSON.parse(option2E)
                        },
                        {
                            name: 'SwitchButtons2',
                            options: array3E,
                            optionsValue: 'value',
                            optionsText: 'text',
                            controlType: 'SwitchButtons',
                            enable: true,
                            distinction: option2E == "{ " ? {} : JSON.parse(option2E)
                        }
                    ],
                    features: [

                        {
                            name: 'Selection',
                            mode: 'row',
                            multipleSelection: true
                        }
                    ],
                    ntsFeatures: [{
                        name: 'CellState',
                        rowId: 'rowId',
                        columnKey: 'columnKey',
                        state: 'state',
                        states: result.itemRequired
                    }],
                });

                let allowOther = __viewContext['screenModel'].allowOtherRef(),
                    allowPerson = __viewContext['screenModel'].allowPersonRef();
                __viewContext['screenModel'].allowOtherRef(!allowOther);
                __viewContext['screenModel'].allowOtherRef(allowOther);
                __viewContext['screenModel'].allowPersonRef(!allowPerson);
                __viewContext['screenModel'].allowPersonRef(allowPerson);

                // Ä‘oáº¡n bind láº¡i header
                ko.applyBindings(__viewContext['screenModel'], nts.uk.ui.ig.grid.header.getCell('item_role_table_body', 'otherAuth')[0]);
                ko.applyBindings(__viewContext['screenModel'], nts.uk.ui.ig.grid.header.getCell('item_role_table_body', 'selfAuth')[0]);


                dfd.resolve();

            }).always(() => {
                //register click change all event
                $(() => {
                    let currentCategory = _.find(screenModel.roleCategoryList(), (roleCate: PersonRoleCategory) => roleCate.categoryId == self.categoryId);
                    $('#anotherSelectedAll_auth').on('click', 'label input', (e) => {
                        // find index of selected input
                        const index = Array.prototype.indexOf.call($('#anotherSelectedAll_auth')[0].childNodes, $(e.currentTarget).parent()[0]);
                        screenModel.changeAll('anotherSelectedAll_auth', currentCategory && currentCategory.categoryCode == 'CS00100' && index == 1 ? 3 : index + 1);
                    });

                    $('#seftSelectedAll_auth').on('click', 'label input', (e) => {
                        // find index of selected input
                        const index = Array.prototype.indexOf.call($('#seftSelectedAll_auth')[0].childNodes, $(e.currentTarget).parent()[0]);
                        screenModel.changeAll('seftSelectedAll_auth', currentCategory && currentCategory.categoryCode == 'CS00100' && index == 1 ? 3 : index + 1);
                    });

                    $('.ui-iggrid-header').on('focus', function() {

                        if ($(this).find('label input').is(':enabled')) {
                            $(this).find('.selected_all_auth').focus();
                        }
                    });
                });
                 $("#item_role_table_body_isChecked > span > div > label > span").attr("tabindex", 21);

            });

            return dfd.promise();
        }
    }

    export class PersonRoleItem {
        personItemDefId: string;
        isChecked: boolean = false;
        setting: boolean;
        requiredAtr: string;
        itemName: string;
        parrentCd: string;
        otherAuth: string;
        selfAuth: string;
        itemCd: string;
        dataType: number;
        isConvert: boolean = false;

        constructor(param: IPersonRoleItem) {
            let self = this;
            self.personItemDefId = param ? param.personItemDefId : "";//_.replace(param.personItemDefId, new RegExp("-", "g"), "_") : '';
            self.setting = param ? param.setting : false;
            self.requiredAtr = param ? param.requiredAtr : 'false';
            self.itemName = param ? param.itemName : '';
            self.parrentCd = param ? param.parrentCd : '';
            self.itemCd = param ? param.itemCd : '';
            self.otherAuth = this.setting === true ? param ? param.otherAuth : 1 : 1;
            self.selfAuth = this.setting === true ? param ? param.selfAuth : 1 : 1;
            self.dataType = param ? param.dataType : '';
            self.isConvert = param ? (param.personItemDefId.search("CS") > -1 ? false : true) : false;
        }
    }

    export class PersonRoleCommand {
        roleId: string;
        roleCode: string;
        roleName: string;
        currentCategory: PersonRoleCategoryCommand = null;
        constructor(param: PersonRole) {
            this.roleId = param.roleId;
            this.roleCode = param.roleCode;
            this.roleName = param.roleName;
            this.currentCategory = new PersonRoleCategoryCommand(param.currentCategory());
        }
    }
    export class PersonRoleCategoryCommand {
        categoryId: string;
        categoryCode: string;
        categoryName: string;
        categoryType: number;
        allowPersonRef: number;
        allowOtherRef: number;
        allowOtherCompanyRef: number;
        selfPastHisAuth: number;
        selfFutureHisAuth: number;
        selfAllowDelHis: number;
        selfAllowAddHis: number;
        otherPastHisAuth: number;
        otherFutureHisAuth: number;
        otherAllowDelHis: number;
        otherAllowAddHis: number;
        selfAllowDelMulti: number;
        selfAllowAddMulti: number;
        otherAllowDelMulti: number;
        otherAllowAddMulti: number;
        items: Array<PersonRoleItemCommand> = [];
        constructor(param: PersonRoleCategory) {

            let sm: ScreenModel = __viewContext['screenModel'];

            this.categoryId = param.categoryId;
            this.categoryCode = param.categoryCode;
            this.categoryName = param.categoryName;
            this.categoryType = param.categoryType;
            this.allowPersonRef = sm.allowPersonRef();
            this.allowOtherRef = sm.allowOtherRef();
            this.allowOtherCompanyRef = param.allowOtherCompanyRef();
            this.selfPastHisAuth = param.selfPastHisAuth();
            this.selfFutureHisAuth = param.selfFutureHisAuth();
            this.selfAllowDelHis = param.selfAllowDelHis();
            this.selfAllowAddHis = param.selfAllowAddHis();
            this.otherPastHisAuth = param.otherPastHisAuth();
            this.otherFutureHisAuth = param.otherFutureHisAuth();
            this.otherAllowDelHis = param.otherAllowDelHis();
            this.otherAllowAddHis = param.otherAllowAddHis();
            this.selfAllowDelMulti = param.selfAllowDelMulti();
            this.selfAllowAddMulti = param.selfAllowAddMulti();
            this.otherAllowDelMulti = param.otherAllowDelMulti();
            this.otherAllowAddMulti = param.otherAllowAddMulti();
            //add parrent item

            let cellsUpdated = $("#item_role_table_body").ntsGrid("updatedCells"),
                dataSource = $("#item_role_table_body").igGrid("option", "dataSource"),
                itemGroup = _.groupBy(cellsUpdated, 'rowId'),
                itemLst: Array<any> = [];


            itemLst = _.map(dataSource, function(c: any) {
                _.each(itemGroup, function(i) {
                    if (i.length > 0) {
                        let personItemDefId: string = i[0].rowId;
                    }
                    if (c.personItemDefId === personItemDefId) {
                        _.each(i, function(x) {
                            if (x.columnKey == "otherAuth") {
                                c.otherAuth = x.value !== undefined ? x.value : c.otherAuth;
                            }
                            if (x.columnKey == "selfAuth") {
                                c.selfAuth = x.value !== undefined ? x.value : c.selfAuth;
                            }

                        });
                    }
                    return c;

                });
                return c;


            });

            this.items = _.map(itemLst, x => new PersonRoleItemCommand(x));
            //add child item
            this.addChildItem(this.items);
        }

        addChildItem(items: Array<PersonRoleItemCommand>) {
            let sm: ScreenModel = __viewContext['screenModel'],
                itemDatas = sm.currentRole().currentCategory().roleItemDatas();
            //create loop parent for get child item 
            _.forEach(items, function(parentItem: PersonRoleItemCommand) {
                let childItems = _.filter(itemDatas, { parrentCd: parentItem.itemCd });
                //set atr same parent item
                _.forEach(childItems, function(childItem: PersonRoleItem) {
                    let subItems = _.filter(itemDatas, { parrentCd: childItem.itemCd });
                    //set atr same child item
                    _.forEach(subItems, function(obj: PersonRoleItem) {
                        obj.selfAuth = parentItem.selfAuth;
                        obj.otherAuth = parentItem.otherAuth;
                        items.push(new PersonRoleItemCommand(obj));
                    })
                    childItem.selfAuth = parentItem.selfAuth;
                    childItem.otherAuth = parentItem.otherAuth;

                    items.push(new PersonRoleItemCommand(childItem));
                });


            });



        }

    }
    export class PersonRoleItemCommand {
        personItemDefId: string;
        itemCd: string;
        parrentCd: string;
        otherAuth: number;
        selfAuth: number;
        constructor(param: PersonRoleItem) {
            this.personItemDefId = param.personItemDefId;
            this.parrentCd = param.parrentCd;
            this.otherAuth = param.otherAuth;
            this.selfAuth = param.selfAuth;
            this.itemCd = param.itemCd;
        }

    }

}

function makeIcon(value, row) {
    if (value == "true")
        return "●";
    return '';
}


