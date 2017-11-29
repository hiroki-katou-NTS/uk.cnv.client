module nts.uk.com.view.cas014.b {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import textUK = nts.uk.text;
    import block = nts.uk.ui.block;
    export module viewmodel {
        export class ScreenModel {

            roleSetList: KnockoutObservableArray<RoleSet>;
            roleSetPersonList: KnockoutObservableArray<RoleSetPerson>;
            dateValue: KnockoutObservable<any>
            selectedRoleSet: KnockoutObservable<string>;
            selectedEmployeeId: KnockoutObservable<string>;
            roleSetPerson: KnockoutObservable<RoleSetPerson>;
            screenMode: KnockoutObservable<number>;
            constructor() {
                let self = this;
                self.screenMode = ko.observable(ScreenMode.UPDATE);
                self.dateValue = ko.observable({});
                self.roleSetList = ko.observableArray([]);
                self.roleSetPersonList = ko.observableArray([]);
                self.selectedRoleSet = ko.observable('');
                self.selectedEmployeeId = ko.observable('');
                self.roleSetPerson = ko.observable(new RoleSetPerson('', '', '', '', '', ''));
                self.selectedEmployeeId.subscribe(function(data: any) {
                    if (data) {
                        let item = _.find(ko.toJS(self.roleSetPersonList), (x: RoleSetPerson) => x.employeeId == data);
                        if (item) {
                            self.roleSetPerson(item);
                            self.screenMode(ScreenMode.UPDATE);
                            $(".ntsStartDatePicker").focus();
                        } else {
                            self.getEmployeeInfo(data);
                            self.screenMode(ScreenMode.NEW);
                        }
                    }
                });
                self.selectedRoleSet.subscribe(function(data: any) {
                    if (data) {
                        self.loadRoleSetHolder(data);
                    } else {
                        self.roleSetPersonList.removeAll();
                    }
                });
                self.dateValue.subscribe((data: any) => {
                    if (self.roleSetPerson) {
                        self.roleSetPerson().startDate = moment.utc(data.startDate, "YYYY/MM/DD").toISOString();
                        self.roleSetPerson().endDate = moment.utc(data.endDate, "YYYY/MM/DD").toISOString();
                    }
                });
                self.roleSetPerson.subscribe((data: RoleSetPerson) => {
                    self.dateValue({ startDate: data.startDate, endDate: data.endDate });
                });

            }

            startPage(): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                block.invisible();

                new service.Service().getAllRoleSet().done(function(data: Array<any>) {
                    if (data && data.length) {
                        self.roleSetList.removeAll();
                        let _rsList: Array<RoleSet> = _.map(data, rs => {
                            return new RoleSet(rs.code, rs.name);
                        });
                        _rsList = _.sortBy(_rsList, ['code']);
                        _.each(_rsList, rs => self.roleSetList.push(rs));

                        //select first role set
                        self.selectedRoleSet(self.roleSetList()[0].code);
                    } else {
                        alertError({ messageId: "Msg_713" }).then(() => {
                            nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                        });
                    }
                    dfd.resolve();
                }).fail(function(error) {
                    alertError({ messageId: error.message });
                    dfd.reject();
                }).always(() => {
                    block.clear();
                });

                return dfd.promise();
            }

            loadRoleSetHolder(rsCode: string, empId?: string) {
                let self = this;
                new service.Service().getAllRoleSetPerson(rsCode).done(function(data: Array<any>) {
                    self.roleSetPersonList.removeAll();
                    if (data && data.length) {
                        let _rspList: Array<RoleSetPerson> = _.map(data, rsp => {
                            return new RoleSetPerson(rsp.roleSetCd, rsp.employeeId, rsp.employeeCd, rsp.employeeName, rsp.startDate, rsp.endDate);
                        });
                        _rspList = _.sortBy(_rspList, ['employeeCd']);
                        _.each(_rspList, rsp => self.roleSetPersonList.push(rsp));

                        if (empId) {
                            //select first 
                            self.selectedEmployeeId(empId);
                            //self.selectedEmployeeId.valueHasMutated();
                        } else {
                            //select first 
                            self.selectedEmployeeId(self.roleSetPersonList()[0].employeeId);
                            //self.selectedEmployeeId.valueHasMutated();
                        }

                        self.screenMode(ScreenMode.UPDATE);
                        $("#B4_2").focus();
                    } else {
                        self.createNewRoleSetPerson();
                        $("#B3_2").focus();
                    }
                }).fail(function(error) {
                    alertError({ messageId: error.message });
                });
            }

            getEmployeeInfo(empId: string) {
                let self = this, _data = self.roleSetPerson();
                new service.Service().getEmployeeInfo(empId).done(function(data: any) {
                    if (data) {
                        self.roleSetPerson(new RoleSetPerson(self.selectedRoleSet(), empId, data.employeeCode, data.personalName, _data.startDate, _data.endDate));
                    }
                }).fail(function(error) {
                    alertError({ messageId: error.message });
                });
            }

            createNewRoleSetPerson() {
                let self = this;
                self.selectedEmployeeId('');
                self.roleSetPerson(new RoleSetPerson('', '', '', '', '', ''));
                self.dateValue({});
                self.screenMode(ScreenMode.NEW);
                $("#B5_1_1").focus();
            }

            registerRoleSetPerson() {
                let self = this, data: RoleSetPerson = ko.toJS(self.roleSetPerson);
                $(".ntsDateRange_Component").trigger("validate");
                if (!nts.uk.ui.errors.hasError() && data.employeeId) {
                    let command: any = {
                        roleSetCd: data.roleSetCd,
                        employeeId: data.employeeId,
                        startDate: data.startDate,
                        endDate: data.endDate,
                        mode: self.screenMode()
                    };

                    block.invisible();

                    new service.Service().registerData(command).done(function() {
                        //display registered data in selected state
                        self.loadRoleSetHolder(self.selectedRoleSet(), data.employeeId);

                        info({ messageId: "Msg_15" }).then(() => {
                            $("#B4_2").focus();
                        });
                    }).fail(error => {
                        alertError({ messageId: error.message });
                    }).always(() => {
                        block.clear();
                    });
                }
            }

            deleteRoleSetPerson() {
                let self = this, data: RoleSetPerson = ko.toJS(self.roleSetPerson);

                let command: any = {
                    employeeId: data.employeeId
                };

                confirm({ messageId: "Msg_18" }).ifYes(() => {
                    // call service remove
                    block.invisible();
                    let indexItemDelete = _.findIndex(ko.toJS(self.roleSetPersonList), function(item: any) { return item.employeeId == data.employeeId; });
                    new service.Service().deleteData(command).done(function() {
                        //select after delete
                        self.loadRoleSetHolder(self.selectedRoleSet());
                        if (self.roleSetPersonList().length == 0) {
                            self.createNewRoleSetPerson();
                        } else {
                            if (indexItemDelete == self.roleSetPersonList().length) {
                                self.selectedEmployeeId(self.roleSetPersonList()[indexItemDelete - 1].employeeId);
                            } else {
                                self.selectedEmployeeId(self.roleSetPersonList()[indexItemDelete].employeeId);
                            }
                        }
                        info({ messageId: "Msg_16" }).then(() => {
                            block.clear();
                        });
                    }).fail(error => {
                        alertError({ messageId: error.message });
                    }).always(() => {
                        block.clear();
                    });

                }).ifCancel(() => {
                });

            }

            openDialogCDL009() {
                let self = this;

                setShared('CDL009Params', {
                    isMultiSelect: false,
                    baseDate: moment(new Date()).toDate(),
                    target: TargetClassification.DEPARTMENT
                }, true);

                modal("/view/cdl/009/a/index.xhtml").onClosed(function() {
                    var isCancel = getShared('CDL009Cancel');
                    if (isCancel) {
                        return;
                    }
                    var output = getShared('CDL009Output');
                    self.selectedEmployeeId(output);
                    $(".ntsStartDatePicker").focus();
                });
            }

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
            this.displayDateRange = (start && end) ? this.startDate.slice(0, 10).replace(/-/g, "/") + ' ' + getText('CAS014_38') + ' ' + this.endDate.slice(0, 10).replace(/-/g, "/") : '';
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
}

