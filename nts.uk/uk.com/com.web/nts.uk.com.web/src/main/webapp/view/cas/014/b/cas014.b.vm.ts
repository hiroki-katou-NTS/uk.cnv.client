module nts.uk.com.view.cas014.b {
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    var paths = {
        initScreen: "screen/com/cas014/get-data-init",
        getRoleSetGrantedToEmployee: "screen/com/cas014/get-role-set-grandted-person/",
        getEmpInfo: "ctx/sys/auth/grant/rolesetperson/getempinfo/",
        registerData: "ctx/sys/auth/grant/rolesetperson/register",
        deleteData: "ctx/sys/auth/grant/rolesetperson/delete"
    };

    @bean()
    class ScreenModel extends ko.ViewModel {
        screenMode: KnockoutObservable<number>;
        roleSetList: KnockoutObservableArray<RoleSet>;
        roleSetPersonList: KnockoutObservableArray<any>;
        grantedRoleColSource: KnockoutObservableArray<any>;
        listComponentOption: any;

        selectedEmployeeId: KnockoutObservable<string>;
        selectedEmployeeCode: KnockoutObservable<string>;
        selectedEmployeeName: KnockoutObservable<string>;
        selectedRoleCode: KnockoutObservable<string>;
        dateValue: KnockoutObservable<any>;
        backFromCDL009: boolean = false;

        constructor() {
            super();
            let self = this;
            self.screenMode = ko.observable(ScreenMode.NEW);
            self.dateValue = ko.observable({});
            self.roleSetList = ko.observableArray([]);
            self.roleSetPersonList = ko.observableArray([]);
            self.selectedEmployeeId = ko.observable(null);
            self.selectedEmployeeCode = ko.observable(null);
            self.selectedEmployeeName = ko.observable(null);
            self.selectedEmployeeCode.subscribe(function(data: any) {
                if(!isNullOrUndefined(data)){
                    let item = _.find(ko.toJS(self.roleSetPersonList), (x: any) => x.code == data);
                    if (item) {
                        self.backFromCDL009 = false;
                        self.getEmployeeInfo(item.id);
                        self.selectedEmployeeName(item.name);
                        self.screenMode(ScreenMode.UPDATE);
                        $("#B4_2").focus();
                    } else {
                        self.screenMode(ScreenMode.NEW);
                        $("#B3_2").focus();
                        if (!self.backFromCDL009) {
                            self.selectedEmployeeId(null);
                            self.selectedEmployeeName(null);
                            self.selectedRoleCode(null);
                            self.dateValue({});
                            self.openDialogCDL009();
                        } else {
                            self.backFromCDL009 = false;
                        }
                    }
                }
            });
            self.selectedRoleCode = ko.observable(null);
            self.grantedRoleColSource = ko.observableArray([]);
            self.listComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: false,
                listType: 4, //ListType.EMPLOYEE,
                employeeInputList: self.roleSetPersonList,
                selectType: 4, // NO_SELECT, manual handle
                selectedCode: self.selectedEmployeeCode,
                isDialog: true,
                isShowNoSelectRow: false,
                isShowWorkPlaceName: false,
                isShowSelectAllButton: false,
                disableSelection : false,
                showOptionalColumn: true,
                optionalColumnName: self.$i18n("CAS014_58"),
                optionalColumnDatasource: self.grantedRoleColSource,
                maxRows: 15,
                tabindex: 4
            };
        }

        created() {
            const vm = this;
            $('#component-items-list').ntsListComponent(vm.listComponentOption);
            vm.startPage();
        }

        startPage(employeeId?: string): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.$blockui("show");
            self.$ajax("com", paths.initScreen).done(function(data: any) {
                if (data) {
                    let _rsList: Array<RoleSet> = _.map(data.roleSetList, (rs: any) => {
                        return new RoleSet(rs.roleSetCd, rs.roleSetName);
                    });
                    self.roleSetList(_.sortBy(_rsList, ['code']));

                    self.roleSetPersonList(_.sortBy(data.employeeInfoExportList, ["scd"]).map((e: any) => ({
                        id: e.sid,
                        code: e.scd,
                        name: e.bussinessName
                    })));

                    self.grantedRoleColSource(data.grantedPersonList.map((i: any) => {
                        let tmp = _.find(_rsList, r => r.code == i.roleSetCd);
                        let tmp2 = _.find(data.employeeInfoExportList, (e: any) => e.sid == i.employeeID);
                        return {
                            empId: tmp2 ? tmp2.scd : "",
                            content: tmp ? tmp.name : ""
                        }
                    }));
                    _.defer(() => {
                        if (_.isEmpty(self.roleSetPersonList())) {
                        //    self.selectedEmployeeCode() == null ? self.selectedEmployeeCode.valueHasMutated() : self.selectedEmployeeCode(null);
                            $("#B3_2").focus();
                        } else {
                            if (employeeId) {
                                const emp = _.find(ko.toJS(self.roleSetPersonList), (x: any) => x.id == employeeId);
                                if (emp) {
                                    emp.code == self.selectedEmployeeCode() ? self.selectedEmployeeCode.valueHasMutated() : self.selectedEmployeeCode(emp.code);
                                } else {
                                    self.selectedEmployeeCode() == self.roleSetPersonList()[0].code ? self.selectedEmployeeCode.valueHasMutated() : self.selectedEmployeeCode(self.roleSetPersonList()[0].code);
                                }
                            } else {
                                self.selectedEmployeeCode() == self.roleSetPersonList()[0].code ? self.selectedEmployeeCode.valueHasMutated() : self.selectedEmployeeCode(self.roleSetPersonList()[0].code);
                            }
                        }
                    });
                } else {
                    nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                }
                dfd.resolve();
            }).fail(function(error) {
                self.$dialog.error(error).then(() => {
                    if (error.messageId == "Msg_713") {
                        nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                    }
                });
                dfd.reject();
            }).always(() => {
                self.$blockui("hide");
            });

            return dfd.promise();
        }

        getEmployeeInfo(empId: string) {
            let self = this;
            self.$ajax("com", paths.getRoleSetGrantedToEmployee + empId).done(function(data: any) {
                if (data) {
                    self.selectedEmployeeId(data.employeeID);
                    self.selectedRoleCode(data.roleSetCd);
                    self.dateValue({
                        startDate: data.startDate,
                        endDate: data.endDate
                    });
                }
            }).fail(function(error) {
                self.$dialog.error(error);
            });
        }
        createNewRoleSetPerson() {
            let self = this;
            nts.uk.ui.errors.clearAll();
            self.screenMode(ScreenMode.NEW);
          //  self.backFromCDL009 = false;
            self.selectedEmployeeCode(null);
            self.selectedEmployeeName(null);
            self.selectedRoleCode(null);
            self.dateValue({});
            self.openDialogCDL009();
        }

        registerRoleSetPerson() {
            let self = this;
            $(".ntsDateRange_Component").trigger("validate");
            if (!nts.uk.ui.errors.hasError()) {
                let command: any = {
                    roleSetCd: self.selectedRoleCode(),
                    employeeId: self.selectedEmployeeId(),
                    startDate: new Date(self.dateValue().startDate).toISOString(),
                    endDate: new Date(self.dateValue().endDate).toISOString(),
                    mode: self.screenMode()
                };

                self.$blockui("show");
                self.$ajax("com", paths.registerData, command).done(function() {
                    self.$dialog.info({ messageId: "Msg_15" }).then(() => {
                        self.startPage(command.employeeId);
                    });
                }).fail(error => {
                    self.$dialog.error(error);
                }).always(() => {
                    self.$blockui("hide");
                });
            }
        }

        deleteRoleSetPerson() {
            let self = this;

            let command: any = {
                employeeId: self.selectedEmployeeId()
            };

            self.$dialog.confirm({ messageId: "Msg_18" }).then((value) => {
                if (value == "yes") {
                    // call service remove
                    self.$blockui("show");
                    let indexItemDelete = _.findIndex(ko.toJS(self.roleSetPersonList), (item: any) => item.id == command.employeeId);
                    self.$ajax("com", paths.deleteData, command).done(function() {
                        self.$dialog.info({ messageId: "Msg_16" }).then(() => {
                            self.startPage().then(() => {
                                if (self.roleSetPersonList().length == 0) {
                                    self.backFromCDL009 = true;
                                    self.selectedEmployeeId(null);
                                    self.selectedEmployeeName(null);
                                    self.selectedRoleCode(null);
                                    self.dateValue({});
                                    self.createNewRoleSetPerson();
                                } else {
                                    self.selectedEmployeeCode(self.roleSetPersonList()[Math.min(indexItemDelete, self.roleSetPersonList().length - 1)].code);
                                }
                            })
                        });
                    }).fail(error => {
                        self.$dialog.error(error);
                    }).always(() => {
                        self.$blockui("hide");
                    });
                }
            });

        }

        openDialogCDL009() {
            let self = this;

            nts.uk.ui.windows.setShared('CDL009Params', {
                isMultiSelect: false,
                baseDate: moment(new Date()).toDate(),
                target: TargetClassification.DEPARTMENT
            }, true);

            nts.uk.ui.windows.sub.modal("/view/cdl/009/a/index.xhtml").onClosed(function() {
                self.backFromCDL009 = true;
                var isCancel = nts.uk.ui.windows.getShared('CDL009Cancel');
                if (isCancel) {
                    return;
                }
                var output = nts.uk.ui.windows.getShared('CDL009Output');
                self.$blockui("show");
                self.$ajax("com", paths.getEmpInfo + output).done(data => {
                    self.selectedEmployeeId(output);
                    self.selectedEmployeeCode(data.employeeCode);
                    self.selectedEmployeeName(data.personalName);
                }).fail(error => {
                    self.$dialog.error(error);
                }).always(() => {
                    self.$blockui("hide");
                    $("#B3_2").focus();
                });
            });
        }

        closeDialog() {
            nts.uk.ui.windows.close();
        }
    }

    export class RoleSet {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    export class RoleSetPerson {
        roleSetCd: string;
        employeeId: string;
        employeeCd: string;
        employeeName: string;
        startDate: string;
        endDate: string;
        displayDateRange: string;

        constructor(roleSetCd: string, employeeId: string, employeeCd: string, employeeName: string, start: string, end: string) {
            this.roleSetCd = roleSetCd;
            this.employeeId = employeeId;
            this.employeeCd = employeeCd;
            this.employeeName = employeeName;
            this.startDate = start;
            this.endDate = end;
            this.displayDateRange = (start && end) ? this.startDate.slice(0, 10).replace(/-/g, "/") + ' ' + nts.uk.resource.getText('CAS014_38') + ' ' + this.endDate.slice(0, 10).replace(/-/g, "/") : '';
        }

    }

    export class TargetClassification {
        static WORKPLACE = 1;
        static DEPARTMENT = 2;
    }

    export class ScreenMode {
        static NEW = 0;
        static UPDATE = 1;
    }

    class ItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
}

