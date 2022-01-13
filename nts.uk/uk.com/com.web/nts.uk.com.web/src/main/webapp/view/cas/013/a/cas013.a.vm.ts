/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cas013.a {
    import block = nts.uk.ui.block;
    import dialog = nts.uk.ui.dialog;
    import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;

    let format = nts.uk.text.format;
    const API = {
        getCompanyIdOfLoginUser: "ctx/sys/auth/roleset/companyidofloginuser",
        getRoleType: "ctx/sys/auth/grant/roleindividual/getRoleType",

        getRole: "ctx/sys/auth/grant/roleindividual/getRoles/incharge/{0}",
        getRoleGrants: "ctx/sys/auth/grant/roleindividual/getRoleGrants",
        getRoleGrant: "ctx/sys/auth/grant/roleindividual/getRoleGrant",

        insertRoleGrant: "ctx/sys/auth/grant/roleindividual/insertRoleGrant",
        upDateRoleGrant: "ctx/sys/auth/grant/roleindividual/upDateRoleGrant",
        deleteRoleGrant: "ctx/sys/auth/grant/roleindividual/deleteRoleGrant",

        getCompanyInfo: "ctx/sys/auth/grant/roleindividual/getCompanyInfo",
        getWorkPlaceInfo: "ctx/sys/auth/grant/roleindividual/getWorkPlaceInfo",
        getJobTitle: "ctx/sys/auth/grant/roleindividual/getJobTitle"

    };

    @bean()
    class ViewModel extends ko.ViewModel {
        //A51
        selectRoleCheckbox: KnockoutObservable<string> = ko.observable('');

        langId: KnockoutObservable<string> = ko.observable('ja');
        // Metadata
        isCreateMode: KnockoutObservable<boolean> = ko.observable(false);
        isSelectedUser: KnockoutObservable<boolean> = ko.observable(false);
        isDelete: KnockoutObservable<boolean> = ko.observable(false);

        //ComboBOx RollType
        listRoleType: KnockoutObservableArray<RollType>;
        selectedRoleType: KnockoutObservable<string> = ko.observable('');

        //list Roll
        listRole: KnockoutObservableArray<Role> = ko.observableArray([]);
        selectedRole: KnockoutObservable<string> = ko.observable('');
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
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
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

        employeeList: KnockoutObservableArray<UnitModel> =  ko.observableArray<UnitModel>([]);

        optionalColumnDatasource: KnockoutObservableArray<any> = ko.observableArray([]);
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
            //A51

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
            vm.listRole = ko.observableArray([]);
            vm.selectedUserID = ko.observable('');
            vm.listRoleIndividual = ko.observableArray([]);
            vm.multiSelectedCode = ko.observable();
            vm.alreadySettingPersonal = ko.observableArray([]);
            vm.columns = ko.observableArray([
                {headerText: '', key: 'id', hidden: true},
                {headerText: nts.uk.resource.getText("CAS013_35"), key: 'employeeCodeAndName', width: 252},
                {headerText: nts.uk.resource.getText("CAS013_32"), key: 'period', width: 203},
                {headerText: nts.uk.resource.getText("CAS013_36"), key: 'companyCode', width: 90}
            ]);

            vm.columnsIndividual = ko.observableArray([
                {headerText: '', key: 'userId', hidden: true},
                {headerText: nts.uk.resource.getText("CAS013_15"), key: 'loginId', width: 120},
                {headerText: nts.uk.resource.getText("CAS013_16"), key: 'name', width: 120},
                {headerText: nts.uk.resource.getText("CAS013_17"), key: 'datePeriod', width: 210},
            ]);
            //A41

            vm.loginID = ko.observable('');
            vm.userName = ko.observable('');
            vm.dateValue = ko.observable({});
            //tab
            vm.tabs = ko.observableArray([
                {
                    id: 'tab-1',
                    title: nts.uk.resource.getText("CAS013_13"),
                    content: '.tab-content-1',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-2',
                    title: nts.uk.resource.getText("CAS013_14"),
                    content: '.tab-content-2',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-3',
                    title: nts.uk.resource.getText("CAS013_15"),
                    content: '.tab-content-3',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
            ]);
            vm.selectedTab = ko.observable('tab-1');
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
            vm.selectedRole.subscribe((roleId: string) => {
                vm.selectRole(roleId.toString(), '');
                vm.isSelectedUser(false);
                vm.isDelete(false);
            });
        }

        created(params: any) {
            let vm = this;
        }

        mounted() {
            const vm = this;
            vm.selectedRoleType.subscribe((roleType: string) => {
                vm.getRoles(roleType);
                vm.isCreateMode(false);
                vm.isSelectedUser(false);
                vm.isDelete(false);
            });

            vm.selectRoleCheckbox.subscribe((e) => {
                let itemRole = _.find(vm.listRole(), (i) => {
                    return i.roleCode == e;
                });
                if (!isNullOrUndefined(itemRole)) {
                    vm.selectedRole(itemRole.roleId);
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
        setDefault() {
            let vm = this;
            nts.uk.util.value.reset($("#combo-box, #A_SEL_001"), vm.defaultValue() !== '' ? vm.defaultValue() : undefined);
        }

        //A51
        validate() {
            $("#combo-box").trigger("validate");
        }

        initialScreen(deferred: any, roleSetCd: string) {
            let vm = this;
            let dfd = $.Deferred();
            block.invisible();
            vm.$ajax('com', API.getRoleType).done(function (data: Array<RollType>) {
                if (!isNullOrEmpty(data)) {
                    vm.listRoleType(data);
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
                if (vm.listRole().length > 0) {
                    $("#daterangepicker").find(".ntsStartDatePicker").focus();
                }
            }
        }

        private getRoles(roleType: string): void {
            let vm = this;
            let dfd = $.Deferred();
            if (roleType != '') {
                let _path = format(API.getRole, roleType);
                block.invisible();
                vm.$ajax('com', _path).done(function (data: any) {
                    if (data != null && data.length > 0) {
                        vm.listRole(data);
                        vm.selectedRole(data[0].roleId);
                        vm.selectRoleCheckbox(data[0].roleId);
                    }
                    else {
                        vm.listRole([]);
                        vm.selectedRole('');
                        vm.selectRoleCheckbox('');
                        block.clear();
                    }

                    dfd.resolve();
                }).always(() => {
                }).fail(() => {
                    dfd.reject();
                });
            } else {
                vm.listRole([]);
                vm.selectedRole('');
                vm.selectRoleCheckbox('');
                block.clear();
            }
        }

        private selectRole(roleId: string, userIdSelected: string): void {
            let vm = this;
            let employeeSearchs: UnitModel[] = [];
            let index = _.findIndex(vm.employeeList(), (e) => {
                return e.id == userIdSelected
            });
            if (roleId != '') {
                block.invisible();
                vm.$ajax('com', API.getRoleGrants, roleId).done(function (data: any) {
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
                            return e.id == userIdSelected
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
                        vm.New();

                    }
                }).always(() => {
                    block.clear();
                }).fail(() => {

                });
            } else {
                vm.employeeList([]);//KCP005
                vm.listRoleIndividual([]);
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
                block.clear()
            }
        }

        private selectRoleEmployee(id: string): void {
            let vm = this;
            let roleId = vm.selectedRole();
            if (roleId != '') {
                let userEmployee = _.find(vm.employeeList(), (i) => i.id == id.toString());

                let number = vm.checkFirt();
                if(!nts.uk.text.isNullOrEmpty(userEmployee)){
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
                        employeeID:userEmployee.employeeId };
                    $.when(
                        vm.$ajax('com', API.getWorkPlaceInfo,wpl),
                        vm.$ajax('com', API.getJobTitle, userEmployee.employeeId))
                        .done((workPlace: any,job:any)=>{

                            if (workPlace != null) {
                                vm.workplaceCode(workPlace.workPlaceCode);
                                vm.workplaceName(workPlace.workPlaceName);
                            } else {
                                vm.workplaceCode('');
                                vm.workplaceName('');
                            }
                            if (job != null) {
                                vm.jobTitleCode(job.jobTitleCode);
                                vm.jobTitleName(job.jobTitleName);
                            } else {
                                vm.jobTitleCode('');
                                vm.jobTitleName('');
                            }
                            vm.setFocus();
                        }).always(()=>{
                        block.clear();
                    });
                    vm.isCreateMode(false);
                    vm.isSelectedUser(false);
                    vm.isDelete(true);


                }else {
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
            } else {
                vm.isDelete(false);
            }
        }
        New(): void {
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
            vm.New();
            vm.openBModal();
        }

        openBModal(): void {
            let vm = this;
            nts.uk.ui.windows.setShared("cid_from_a", vm.companyId());
            nts.uk.ui.errors.clearAll();
            nts.uk.ui.windows.sub.modal('/view/cas/013/b/index.xhtml').onClosed(() => {
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
                    vm.upDate();
                }
            }
            else if (nts.uk.util.isNullOrUndefined(vm.employyeName()) || vm.employyeName() == '') {
                nts.uk.ui.dialog.alertError({
                    messageId: "Msg_218",
                    messageParams: [nts.uk.resource.getText("CAS013_10")]
                });
            }
        }

        private insert(): void {
            let vm = this;
            let roleType = vm.selectedRoleType();
            let roleId = vm.selectedRole();
            let userId = vm.selectedUserID();
            let start = nts.uk.time.parseMoment(vm.dateValue().startDate).format();
            let end = nts.uk.time.parseMoment(vm.dateValue().endDate).format();
            let cid =__viewContext.user.companyId;
            block.invisible();
            let roleGrant = {
                userID: userId,
                roleID: roleId,
                roleType: roleType,
                startValidPeriod: start,
                endValidPeriod: end,
                companyID:cid
            };
            vm.$ajax('com', API.insertRoleGrant, roleGrant).done(function (data: any) {
                if (!nts.uk.util.isNullOrUndefined(data)) {
                    vm.selectedUserID("");
                    vm.selectRole(roleId, data);
                    nts.uk.ui.dialog.info({messageId: "Msg_15"});
                    vm.isCreateMode(false);
                } else {
                    nts.uk.ui.dialog.alertError({
                        messageId: "Msg_61",
                        messageParams: [nts.uk.resource.getText("CAS013_11")]
                    });
                }
            }).always(() => {
                block.clear();
            });
        }

        private upDate(): void {
            let vm = this;
            let roleTpye = vm.selectedRoleType();
            let roleId = vm.selectedRole();
            let userId = vm.selectedUserID();
            let start = nts.uk.time.parseMoment(vm.dateValue().startDate).format();
            let end = nts.uk.time.parseMoment(vm.dateValue().endDate).format();
            let cid =__viewContext.user.companyId;
            let roleGrant = {
                userID: userId,
                roleID: roleId,
                roleType: roleTpye,
                startValidPeriod: start,
                endValidPeriod: end,
                companyID: cid

            };
            vm.$ajax('com', API.upDateRoleGrant, roleGrant).done(function (data: any) {
                if (!nts.uk.util.isNullOrUndefined(data)) {
                    vm.selectRole(roleId, data);
                    nts.uk.ui.dialog.info({messageId: "Msg_15"});
                } else {
                    nts.uk.ui.dialog.alertError({
                        messageId: "Msg_61",
                        messageParams: [nts.uk.resource.getText("CAS013_11")]
                    });
                }
                vm.isCreateMode(false);
            }).always(() => {
                block.clear();
            });
        }

        Delete(): void {
            let vm = this;
            if (!nts.uk.ui.errors.hasError()) {
                nts.uk.ui.dialog.confirm({messageId: "Msg_18"}).ifYes(() => {
                    block.invisible();
                    let vm = this;
                    let roleTpye = vm.selectedRoleType();
                    let userId = vm.selectedUserID();
                    let cid =__viewContext.user.companyId;
                    let roleGrant = {
                        userID: userId,
                        roleType: roleTpye,
                        companyID: cid
                    };
                    let id = cid+"_"+userId+"_"+roleTpye;
                    vm.$ajax('com', API.deleteRoleGrant, roleGrant).done(function () {
                        nts.uk.ui.dialog.info({messageId: "Msg_16"});
                    }).always(() => {
                        block.clear();
                    });
                    vm.selectRole(vm.selectedRole(), id);
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
        compCode: string;
        compName: string;

        constructor(compCode: string, compName: string) {
            this.compCode = compCode;
            this.compName = compName;
        }
    }

    class WorkPlaceInfo {
        workplaceCode: string;
        workplacepName: string;

        constructor(workplaceCode: string, workplacepName: string) {
            this.workplaceCode = workplaceCode;
            this.workplacepName = workplacepName;
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

    class JobTitle {
        jobTitleCode: string;
        jobTitleName: string;

        constructor(jobTitleCode: string, jobTitleName: string) {
            this.jobTitleCode = jobTitleCode;
            this.jobTitleName = jobTitleName;
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
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }
    export class UnitModel {
        id?:string;
        companyID?:string;
        companyCode?: string;
        companyName?: string;
        userID?: string;
        employeeId?:string;
        employeeCode?: string;
        employeeName?: string;
        period?: string;
        startValidPeriod?: string;
        endValidPeriod?: string;
        employeeCodeAndName?:string

        constructor(data: any) {
            this.id = data.id;
            this.companyID = data.companyID;
            this.companyCode= data.companyCode;
            this.companyName= data.companyName;
            this.userID= data.userID;
            this.employeeId= data.employeeId;
            this.employeeCode= data.employeeCode;
            this.employeeName= data.employeeName;
            this.period= data.startValidPeriod+ " ~ " +data.endValidPeriod;
            this.startValidPeriod= data.startValidPeriod;
            this.endValidPeriod = data.endValidPeriod;
            this.employeeCodeAndName = data.employeeCode + " "+data.employeeName;
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

}