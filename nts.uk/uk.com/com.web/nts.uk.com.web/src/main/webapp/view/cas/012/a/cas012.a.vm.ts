/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.cas012.a {
    import block = nts.uk.ui.block;
    import dialog = nts.uk.ui.dialog;
    import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;

    let format = nts.uk.text.format;
    const API = {
        getCompanyIdOfLoginUser: "ctx/sys/auth/roleset/companyidofloginuser",
        getDataInit: "screen/com/cas012/a/get-data-init",
        getListEmployee: "screen/com/cas012/a/get-employee",
        getEmployeeInfo: "screen/com/cas012/a/get-employee-info",
        delete: "ctx/sys/auth/grant/cas012/a/delete",
        update: "ctx/sys/auth/grant/cas012/a/update",
        addNew: "ctx/sys/auth/grant/cas012/a/add",
        deleteCompanySys: "ctx/sys/auth/grant/cas012/a/sys/company/delete",
        updateCompanySys: "ctx/sys/auth/grant/cas012/a/sys/company/update",
        addNewCompanySys: "ctx/sys/auth/grant/cas012/a/sys/company/add",

    };

    @bean()
    class ViewModel extends ko.ViewModel {
        //A51
        langId: KnockoutObservable<string> = ko.observable('ja');
        // Metadata
        isCreateMode: KnockoutObservable<boolean> = ko.observable(false);
        isSelectedUser: KnockoutObservable<boolean> = ko.observable(false);
        isDelete: KnockoutObservable<boolean> = ko.observable(false);

        //
        listCompany: KnockoutObservableArray<CompanyInfo> = ko.observableArray([]);
        enableListCompany: KnockoutObservable<boolean> = ko.observable(false);
        selectedCid: KnockoutObservable<string> = ko.observable('');

        listRoleType: KnockoutObservableArray<RollType>;
        selectedRoleType: KnockoutObservable<number> = ko.observable(0);

        //list Roll
        selectedUserID: KnockoutObservable<string>;
        columns: KnockoutObservableArray<NtsGridListColumn>;

        //list Role Individual Grant
        listRoleIndividual: KnockoutObservableArray<RoleIndividual>;
        columnsIndividual: KnockoutObservableArray<NtsGridListColumn>;

        // A7_3
        loginID: KnockoutObservable<string>;
        userName: KnockoutObservable<string>;

        //Date pick
        dateValue: KnockoutObservable<any>;

        // A8
        selectedTab: KnockoutObservable<string>;

        // Start declare KCP005
        listComponentOption: any;
        multiSelectedCode: KnockoutObservable<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        alreadySettingPersonal: KnockoutObservableArray<UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        disableSelection: KnockoutObservable<boolean>;

        employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray<UnitModel>([]);

        baseDate: KnockoutObservable<Date>;

        employyeCode: KnockoutObservable<string>;
        employyeName: KnockoutObservable<string>;
        companyId: KnockoutObservable<string>;
        companyCode: KnockoutObservable<string>;
        companyName: KnockoutObservable<string>;
        workplaceCode: KnockoutObservable<string>;
        workplaceName: KnockoutObservable<string>;
        jobTitleCode: KnockoutObservable<string>;
        jobTitleName: KnockoutObservable<string>;
        EmployeeIDList: KnockoutObservableArray<any>;
        checkFirt: KnockoutObservable<number>;

        constructor(params: any) {
            super();
            let vm = this;
            let dfd = $.Deferred();

            vm.employyeCode = ko.observable('');
            vm.employyeName = ko.observable('');
            vm.workplaceCode = ko.observable('');
            vm.workplaceName = ko.observable('');
            vm.checkFirt = ko.observable(0);

            vm.companyId = ko.observable('');
            vm.companyCode = ko.observable('');
            vm.companyName = ko.observable('');
            vm.jobTitleCode = ko.observable('');
            vm.jobTitleName = ko.observable('');
            vm.EmployeeIDList = ko.observableArray([]);
            vm.listRoleType = ko.observableArray([]);
            vm.selectedUserID = ko.observable('');
            vm.listRoleIndividual = ko.observableArray([]);
            vm.multiSelectedCode = ko.observable();
            vm.alreadySettingPersonal = ko.observableArray([]);
            vm.columns = ko.observableArray([
                {headerText: '', key: 'id', hidden: true},
                {headerText: nts.uk.resource.getText("CAS012_11"), key: 'employeeCodeAndName', width: 252},
                {headerText: nts.uk.resource.getText("CAS012_12"), key: 'period', width: 203},
                {headerText: nts.uk.resource.getText("CAS012_13"), key: 'companyCode', width: 90}
            ]);
            //A41

            vm.loginID = ko.observable('');
            vm.userName = ko.observable('');
            vm.dateValue = ko.observable({});

            block.invisible();
            vm.$ajax('com', API.getCompanyIdOfLoginUser).done((companyId: any) => {
                if (!companyId) {
                    vm.backToTopPage();
                    dfd.resolve();
                } else {
                    // initial screen
                    vm.initialScreen(dfd, '');
                }
                dfd.resolve();
            }).fail(() => {
                vm.backToTopPage();
                dfd.reject();
            }).always(() => {
            });
        }

        created(params: any) {
            let vm = this;
        }
        mounted() {
            const vm = this;
            vm.selectedRoleType.subscribe((roleType: number) => {
                if (roleType == ListType.SYSTEM_MANAGER) {
                    vm.enableListCompany(false);
                    vm.selectCid('', roleType, "")
                } else if (roleType == ListType.COMPANY_MANAGER) {
                    vm.enableListCompany(true);
                    vm.selectedCid.valueHasMutated();
                }
                vm.isCreateMode(false);
                vm.isSelectedUser(false);
                vm.isDelete(false);
            });
            vm.selectedCid.subscribe((cid) => {
                if (!isNullOrUndefined(cid) && vm.selectedRoleType() == ListType.COMPANY_MANAGER) {
                    let roleType = vm.selectedRoleType();
                    vm.selectCid(cid, roleType, "")
                }
            });
            vm.multiSelectedCode.subscribe((e) => {
                if (!isNullOrUndefined(e)) {
                    vm.selectRoleEmployee(e);
                }
            });
            $('#combo-box').focus();
        }

        //A51
        validate() {
            $("#combo-box").trigger("validate");
        }

        initialScreen(deferred: any, roleSetCd: string) {
            let vm = this;
            let dfd = $.Deferred();
            block.invisible();
            let roleTypes: any[] = [];
            vm.$ajax('com', API.getDataInit).done((data) => {
                if (!isNullOrUndefined(data)) {
                    let enumRoleType = data.enumRoleType;
                    for (let i = 0; i < enumRoleType.length; i++) {
                        roleTypes.push(new RollType(enumRoleType[i].value, enumRoleType[i].localizedName))
                    }
                    vm.listRoleType(roleTypes);
                    vm.listCompany(data.listCompany)
                } else {
                    vm.backToTopPage();
                }
                dfd.resolve();
            }).fail((error) => {
                dialog.alertError({messageId: error.messageId}).then(() => {
                    vm.backToTopPage();
                });
                dfd.reject();
            }).always(() => {
            });
            return dfd.promise();
        }

        backToTopPage() {
            nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
        }

        setFocus() {
            let vm = this;
            if (vm.isCreateMode() || vm.checkFirt() == 1) {
                $('#combo-box').focus();
            } else {
                if (vm.listRoleType().length > 0) {
                    $("#daterangepicker").find(".ntsStartDatePicker").focus();
                }
            }
        }

        private selectCid(cid: string, roleType: number, id: string): void {
            let vm = this;
            let employeeSearchs: UnitModel[] = [];
            let index = _.findIndex(vm.employeeList(), (e) => {
                return e.id == id
            });
            block.invisible();
            let params = {
                cid: cid,
                roleType: roleType
            };
            vm.$ajax('com', API.getListEmployee, params).done(function (data: any) {
                if (!isNullOrEmpty(data)) {
                    for (let entry of data) {
                        employeeSearchs.push(new UnitModel(entry))
                    }
                    if (isNullOrEmpty(employeeSearchs)) {
                        vm.multiSelectedCode();
                        vm.dateValue({});
                        vm.companyId('');
                        vm.companyName('');
                        vm.companyCode('');
                        vm.workplaceCode('');
                        vm.workplaceName('');
                        vm.jobTitleCode('');
                        vm.jobTitleName('');
                        vm.employyeCode('');
                        vm.employyeName('');
                    }
                    vm.employeeList(employeeSearchs);
                    let indexNew = _.findIndex(vm.employeeList(), (e) => {
                        return e.id == id
                    });
                    if (index >= 0 && index < vm.employeeList().length && indexNew < 0) {
                        vm.multiSelectedCode(vm.employeeList()[index].id);
                    }
                    if ((index) == vm.employeeList().length && indexNew < 0) {
                        vm.multiSelectedCode(vm.employeeList()[index - 1].id);
                    }
                    if (index < 0 && indexNew >= 0) {
                        vm.multiSelectedCode(vm.employeeList()[indexNew].id);
                        // vm.multiSelectedCode.valueHasMutated();
                    }
                    if (index == indexNew && index < 0) {
                        vm.multiSelectedCode(vm.employeeList()[0].id);
                        // vm.multiSelectedCode.valueHasMutated();
                    }
                    if (index == indexNew && index > 0) {
                        vm.multiSelectedCode(vm.employeeList()[index].id);
                        // vm.multiSelectedCode.valueHasMutated();
                    }
                } else {
                    vm.employeeList([]);//KCP005
                    vm.listRoleIndividual([]);
                    vm.loginID('');
                    vm.userName('');
                    vm.dateValue({});
                    vm.new();

                }
            }).always(() => {
                block.clear();
            }).fail((res) => {
                block.clear();
                vm.showMessageError(res);
            });
        }

        private selectRoleEmployee(id: string): void {
            let vm = this;

            let userEmployee = _.find(vm.employeeList(), (i) => i.id == id.toString());
            let number = vm.checkFirt();
            if (!isNullOrEmpty(userEmployee)) {
                number += 1;
                vm.checkFirt(number);
                vm.employyeCode(userEmployee.employeeCode);
                vm.employyeName(userEmployee.employeeName);
                vm.companyId(userEmployee.companyID);
                vm.companyName(userEmployee.companyName);
                vm.companyCode(userEmployee.companyCode);
                vm.selectedUserID(userEmployee.userID);
                vm.dateValue(new datePeriod(userEmployee.startValidPeriod, userEmployee.endValidPeriod));
                block.invisible();
                let wpl = {
                    cid: userEmployee.companyID,
                    sid: userEmployee.employeeId
                };
                $.when(
                    vm.$ajax('com', API.getEmployeeInfo, wpl))
                    .done((data) => {
                        if (!isNullOrUndefined(data)) {
                            vm.jobTitleCode(data.jobTitleCode);
                            vm.jobTitleName(data.jobTitleName);
                            vm.workplaceCode(data.workplaceCode);
                            vm.workplaceName(data.workplaceName);
                        } else {
                            vm.jobTitleCode('');
                            vm.jobTitleName('');
                            vm.workplaceCode('');
                            vm.workplaceName('');
                        }
                        vm.setFocus();
                    }).always(() => {
                    block.clear();
                });
                vm.isCreateMode(false);
                vm.isSelectedUser(false);
                vm.isDelete(true);

            } else {
                vm.isCreateMode(true);
                vm.jobTitleCode('');
                vm.jobTitleName('');
                vm.workplaceCode('');
                vm.workplaceName('');
                vm.companyId('');
                vm.companyName('');
                vm.companyCode('');
                vm.setFocus();
            }
        }
        new(): void {
            let vm = this;
            vm.isCreateMode(true);
            vm.isDelete(false);
            vm.isSelectedUser(true);
            vm.loginID('');
            vm.userName('');
            vm.dateValue({});
            vm.companyId('');
            vm.companyName('');
            vm.companyCode('');
            vm.workplaceCode('');
            vm.workplaceName('');
            vm.jobTitleCode('');
            vm.jobTitleName('');
            vm.employyeCode('');
            vm.employyeName('');
            vm.multiSelectedCode("");
            $('#combo-box').focus();
            nts.uk.ui.errors.clearAll();
        }

        createNew(): void {
            let vm = this;
            vm.new();
            vm.openBModal();
        }

        openBModal(): void {
            let vm = this;
            nts.uk.ui.windows.setShared("cid_from_a",{
                listCompany:vm.listCompany(),
                cid:vm.companyId()
            });
            nts.uk.ui.errors.clearAll();
            nts.uk.ui.windows.sub.modal('/view/cas/012/b/index.xhtml').onClosed(() => {
                let employeeInf = nts.uk.ui.windows.getShared("employeeInf");
                let cidSelected = nts.uk.ui.windows.getShared("cid");
                if (!isNullOrUndefined(employeeInf)) {
                    vm.employyeCode(employeeInf.employeeCode);
                    vm.employyeName(employeeInf.businessName);
                    vm.jobTitleCode(employeeInf.jobTitleCode);
                    vm.jobTitleName(employeeInf.jobTitleName);
                    vm.workplaceCode(employeeInf.workplaceCode);
                    vm.workplaceName(employeeInf.workplaceName);
                    vm.selectedUserID(employeeInf.userId);
                    $("#daterangepicker").find(".ntsStartDatePicker").focus();
                }
                if (!isNullOrUndefined(cidSelected)) {
                    vm.companyName(cidSelected.name);
                    vm.companyCode(cidSelected.code);
                    vm.companyId(cidSelected.id);
                }

            });
        }

        save(): void {
            let vm = this;
            $("#daterangepicker").find(".ntsStartDatePicker").trigger("validate");
            $("#daterangepicker").find(".ntsEndDatePicker").trigger("validate");
            if (!nts.uk.util.isNullOrUndefined(vm.employyeCode())
                && !nts.uk.util.isNullOrUndefined(vm.employyeName())
                && !nts.uk.util.isNullOrUndefined(vm.companyCode())
                && !nts.uk.util.isNullOrUndefined(vm.dateValue().startDate)
                && !nts.uk.util.isNullOrUndefined(vm.dateValue().endDate)
                && vm.employyeName() != ''
                && !nts.uk.ui.errors.hasError()) {
                if (vm.isSelectedUser() && vm.isCreateMode()) {
                    vm.insert();
                } else {
                    vm.update();
                }
            }
            else if (nts.uk.util.isNullOrUndefined(vm.employyeName()) || vm.employyeName() == '') {
                nts.uk.ui.dialog.alertError({
                    messageId: "Msg_218",
                    messageParams: [nts.uk.resource.getText("CAS013_10")]
                });
            }
        }
        private showMessageError(res: any) {
            if (res.businessException) {
                nts.uk.ui.dialog.alertError({messageId: res.messageId, messageParams: res.parameterIds});
            }
        }
        private insert(): void {
            let vm = this;
            let roleType = vm.selectedRoleType();
            let userId = vm.selectedUserID();
            let start = nts.uk.time.parseMoment(vm.dateValue().startDate).format();
            let end = nts.uk.time.parseMoment(vm.dateValue().endDate).format();
            let cid = vm.selectedCid();
            block.invisible();
            if (roleType == ListType.SYSTEM_MANAGER) {
                let roleGrant = {
                    uId: userId,
                    startDate: start,
                    endDate: end
                };
                block.invisible();
                vm.$ajax('com', API.addNew, roleGrant).done(function () {
                    vm.selectedUserID(userId);
                    vm.selectCid("",roleType,userId);
                    vm.isCreateMode(false);
                    nts.uk.ui.dialog.info({messageId: "Msg_15"});
                }).always(() => {
                }).fail((res) => {
                    block.clear();
                    vm.showMessageError(res);
                });
            }
            else if (roleType == ListType.COMPANY_MANAGER) {
                let roleGrant = {
                    cId: cid,
                    uId: userId,
                    startDate: start,
                    endDate: end,
                    roleType:roleType
                };
                block.invisible();
                vm.$ajax('com', API.addNewCompanySys, roleGrant).done(function () {
                    vm.selectCid(cid,roleType,userId);
                    vm.selectedUserID(userId);
                    nts.uk.ui.dialog.info({messageId: "Msg_15"});
                    vm.isCreateMode(false);
                }).always(() => {
                }).fail((res) => {
                    block.clear();
                    vm.showMessageError(res);
                });
            }


        }

        private update(): void {
            let vm = this,
                userId = vm.selectedUserID(),
                roleType = vm.selectedRoleType(),
                cid = vm.selectedCid();
            let start = nts.uk.time.parseMoment(vm.dateValue().startDate).format();
            let end = nts.uk.time.parseMoment(vm.dateValue().endDate).format();
            if (roleType == ListType.SYSTEM_MANAGER) {
                let roleGrant = {
                    uId: userId,
                    startDate: start,
                    endDate: end,
                };
                block.invisible();
                vm.$ajax('com', API.update, roleGrant).done(() => {
                    vm.selectCid("",roleType,userId);
                    vm.selectedUserID(userId);
                    vm.isCreateMode(false);
                    nts.uk.ui.dialog.info({messageId: "Msg_15"});
                }).always(() => {
                }).fail((res) => {
                    block.clear();
                    vm.showMessageError(res);
                });
            } else if (roleType == ListType.COMPANY_MANAGER) {
                let roleGrant = {
                    cId: cid,
                    uId: userId,
                    startDate: start,
                    endDate: end,
                    roleType:roleType
                };
                block.invisible();
                vm.$ajax('com', API.updateCompanySys, roleGrant).done(() => {
                    vm.selectCid(cid,roleType,userId);
                    vm.selectedUserID(userId);
                    vm.isCreateMode(false);
                    nts.uk.ui.dialog.info({messageId: "Msg_15"});
                }).always(() => {
                }).fail((res) => {
                    block.clear();
                    vm.showMessageError(res);
                });
            }

        }

        remove(): void {
            let vm = this,
                roleType = vm.selectedRoleType();
            if (!nts.uk.ui.errors.hasError()) {
                nts.uk.ui.dialog.confirm({messageId: "Msg_18"}).ifYes(() => {
                    let vm = this;
                    let userId = vm.selectedUserID();
                    block.invisible();
                    let cid = vm.selectedCid();
                    if (roleType == ListType.SYSTEM_MANAGER) {
                        let roleGrant = {
                            userId: userId
                        };
                        vm.$ajax('com', API.delete, roleGrant).done(function () {
                            nts.uk.ui.dialog.info({messageId: "Msg_16"});
                            vm.selectCid("",roleType,userId);
                            vm.selectedUserID(userId);
                        }).always(() => {
                        }).fail((res)=>{
                            block.clear();
                            vm.showMessageError(res);
                        });
                    }
                    else if (roleType == ListType.COMPANY_MANAGER) {
                        let roleGrant = {
                            cId: cid,
                            uId: userId,
                            roleType: roleType
                        };
                        vm.$ajax('com', API.deleteCompanySys, roleGrant).done(function () {
                            nts.uk.ui.dialog.info({messageId: "Msg_16"});
                            vm.selectCid(cid,roleType,userId);
                            vm.selectedUserID(userId);
                        }).always(() => {
                        }).fail((res)=>{
                            block.clear();
                            vm.showMessageError(res);
                        });
                    }

                });
            }
            vm.setFocus();
        }
    }

    class RollType {
        value: string;
        description: string;

        constructor(value: string, description: string) {
            this.value = value;
            this.description = description;
        }
    }

    class CompanyInfo {
        companyId: string;
        companyCode: string;
        companyName: string;
        isAbolition: number;

        constructor(cid: string, companyCode: string, companyName: string, isAbolition: number) {
            this.companyCode = companyCode;
            this.companyName = companyName;
            this.companyId = this.companyId;
            this.isAbolition = isAbolition;
        }
    }

    class Role {
        roleId: string;
        roleCode: string;
        name: string;
        assignAtr: string;
        companyId: string;

        constructor(roleId: string, roleCode: string, name: string, assignAtr: string, companyId: string) {
            this.roleId = roleId;
            this.roleCode = roleCode;
            this.name = name;
            this.assignAtr = assignAtr;
            this.companyId = this.companyId;
        }
    }

    class RoleIndividual {
        userId: string;
        loginId: string;
        name: string;
        start: string;
        end: string;
        datePeriod: string;
        employeeId: string;
        employeeCode: string;
        businessName: string;
        cid: string;

        constructor(userId: string, loginId: string, name: string, start: string,
                    end: string, employeeId: string, employeeCode: string, businessName: string, cid: string) {
            this.userId = userId;
            this.loginId = loginId;
            this.name = name;
            this.start = start;
            this.end = end;
            this.datePeriod = start + ' ~ ' + end;
            this.employeeId = employeeId;
            this.employeeCode = employeeCode;
            this.businessName = businessName;
            this.cid = cid;
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

    export class ListType {
        static SYSTEM_MANAGER = 0;
        static COMPANY_MANAGER = 1;
    }

    export class UnitModel {
        id?: string;
        companyID?: string;
        companyCode?: string;
        companyName?: string;
        userID?: string;
        employeeId?: string;
        employeeCode?: string;
        employeeName?: string;
        period?: string;
        startValidPeriod?: string;
        endValidPeriod?: string;
        employeeCodeAndName?: string

        constructor(data: any) {
            this.id = data.id;
            this.companyID = data.companyID;
            this.companyCode = data.companyCode;
            this.companyName = data.companyName;
            this.userID = data.userID;
            this.employeeId = data.employeeId;
            this.employeeCode = data.employeeCode;
            this.employeeName = data.employeeName;
            this.period = data.startValidPeriod + " ~ " + data.endValidPeriod;
            this.startValidPeriod = data.startValidPeriod;
            this.endValidPeriod = data.endValidPeriod;
            this.employeeCodeAndName = data.employeeCode + " " + data.employeeName;
        }
    }

    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }

    export interface UnitAlreadySettingModel {
        userID: string;
        isAlreadySetting: boolean;
    }
    export interface Cas012DeleteCommand{
        uId:string;
    }

}