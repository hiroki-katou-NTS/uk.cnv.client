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
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    export module viewmodel {
        export class ScreenModel {
            
            langId: KnockoutObservable<string> = ko.observable('ja');
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
                            $(".ntsStartDatePicker").first().focus();
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

                service.getAllRoleSet().done(function(data: Array<any>) {
                    if (data && data.length) {
                        self.roleSetList.removeAll();
                        let _rsList: Array<RoleSet> = _.map(data, rs => {
                            return new RoleSet(rs.code, rs.name);
                        });
                        _rsList = _.sortBy(_rsList, ['code']);
                        _.each(_rsList, rs => self.roleSetList.push(rs));

                        //select first role set
                        if (self.selectedRoleSet() == self.roleSetList()[0].code) 
                            self.selectedRoleSet.valueHasMutated();
                        else
                            self.selectedRoleSet(self.roleSetList()[0].code);
                    } else {
                        nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                    }
                    dfd.resolve();
                }).fail(function(error) {
                    alertError(error).then(() => {
                        if (error.messageId == "Msg_713") {
                            nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                        }
                    });
                    dfd.reject();
                }).always(() => {
                    block.clear();
                });

                return dfd.promise();
            }

            loadRoleSetHolder(rsCode: string, empId?: string): JQueryPromise<any> {
                let self = this;
                block.invisible();
                let dfd = service.getAllRoleSetPerson(rsCode);
                dfd.done(function(data: Array<any>) {
                    self.roleSetPersonList.removeAll();
                    if (data && data.length) {
                        let _rspList: Array<RoleSetPerson> = _.map(data, rsp => {
                            return new RoleSetPerson(rsp.roleSetCd, rsp.employeeId, rsp.employeeCd, rsp.employeeName, rsp.startDate, rsp.endDate);
                        });
                        _rspList = _.sortBy(_rspList, ['employeeCd']);
                        _.each(_rspList, rsp => self.roleSetPersonList.push(rsp));

                        if (empId) {
                            //select empId 
                            if (self.selectedEmployeeId() == empId)
                                self.selectedEmployeeId.valueHasMutated();
                            else
                                self.selectedEmployeeId(empId);
                        } else {
                            //select first
                            if (self.selectedEmployeeId() == self.roleSetPersonList()[0].employeeId)
                                self.selectedEmployeeId.valueHasMutated();
                            else
                                self.selectedEmployeeId(self.roleSetPersonList()[0].employeeId);
                        }

                        self.screenMode(ScreenMode.UPDATE);
                        $("#B4_2").focus();
                    } else {
                        self.createNewRoleSetPerson();
                        $("#B3_2").focus();
                    }
                }).fail(function(error) {
                    alertError(error);
                }).always(() => {
                    block.clear();
                });
                return dfd;
            }

            getEmployeeInfo(empId: string) {
                let self = this, _data = self.roleSetPerson();
                service.getEmployeeInfo(empId).done(function(data: any) {
                    if (data) {
                        self.roleSetPerson(new RoleSetPerson(self.selectedRoleSet(), empId, data.employeeCode, data.personalName, _data.startDate, _data.endDate));
                    }
                }).fail(function(error) {
                    alertError(error);
                });
            }

            createNewRoleSetPerson() {
                let self = this;
                nts.uk.ui.errors.clearAll();
                self.selectedEmployeeId('');
                self.roleSetPerson(new RoleSetPerson('', '', '', '', '', ''));
                self.dateValue({});
                self.screenMode(ScreenMode.NEW);
                $("#B5_1_1").focus();
            }

            registerRoleSetPerson() {
                let self = this, data: RoleSetPerson = ko.toJS(self.roleSetPerson);
                $(".ntsDateRange_Component").trigger("validate");
                if (!nts.uk.ui.errors.hasError()) {
                    let command: any = {
                        roleSetCd: data.roleSetCd,
                        employeeId: data.employeeId,
                        startDate: data.startDate,
                        endDate: data.endDate,
                        mode: self.screenMode()
                    };

                    block.invisible();

                    service.registerData(command).done(function() {
                        //display registered data in selected state
                        self.loadRoleSetHolder(self.selectedRoleSet(), data.employeeId).done(() => {
                            info({ messageId: "Msg_15" }).then(() => {
                                $("#B4_2").focus();
                            });
                        });

                    }).fail(error => {
                        alertError(error);
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
                    service.deleteData(command).done(function() {
                        //select after delete
                        self.loadRoleSetHolder(self.selectedRoleSet()).done(() => {
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
                                //$(".ntsStartDatePicker").first().focus();
                                //block.clear();
                            });
                        });

                    }).fail(error => {
                        alertError(error);
                    }).always(() => {
                        block.clear();
                    });

                }).ifNo(() => {
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
            
            saveAsExcel(): void {
                let self = this;
                let params = {
                    date: null,
                    mode: 1
                };
                if (!nts.uk.ui.windows.getShared("CDL028_INPUT")) {
                    nts.uk.ui.windows.setShared("CDL028_INPUT", params);
                }
                nts.uk.ui.windows.sub.modal("../../../../../nts.uk.com.web/view/cdl/028/a/index.xhtml").onClosed(function() {
                    var result = getShared('CDL028_A_PARAMS');
                    if (result.status) {
                        nts.uk.ui.block.grayout();
                        let langId = self.langId();
                        let date = moment.utc(result.standardDate, "YYYY/MM/DD");
                        service.saveAsExcel(langId, date).done(function() {
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                        }).always(function() {
                            nts.uk.ui.block.clear();
                        });
                    }
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

