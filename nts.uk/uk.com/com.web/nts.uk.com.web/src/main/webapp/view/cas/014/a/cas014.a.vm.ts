module nts.uk.com.view.cas014.a {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import textUK = nts.uk.text;
    import block = nts.uk.ui.block;
    import error = nts.uk.ui.errors;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    export module viewmodel {
        export class ScreenModel {
            langId: KnockoutObservable<string> = ko.observable('ja');
            date: KnockoutObservable<string>;

            roleSetList: KnockoutObservableArray<RoleSet>;
            jobTitleList: KnockoutObservableArray<JobTitle>;
            roleSetJobTitle: KnockoutObservable<RoleSetJobTitle>;

            viewmodelB = new cas014.b.viewmodel.ScreenModel();
            firstLoadTab2: KnockoutObservable<boolean> = ko.observable(true);
            constructor() {
                let self = this;
                self.date = ko.observable(new Date().toISOString());
                self.roleSetList = ko.observableArray([]);
                self.jobTitleList = ko.observableArray([]);
                self.roleSetJobTitle = ko.observable(new RoleSetJobTitle(false, self.jobTitleList(), self.roleSetList()));
                $("#A4").ntsFixedTable({ height: 287 });
                self.date.subscribe((data) => {
                    if (!data) {
                        self.date(new Date().toISOString());
                    }
                });
            }

            startPage(): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                block.invisible();
                self.roleSetList.removeAll();
                self.jobTitleList.removeAll();
                service.getAllData(self.date()).done(function(data: any) {
                    if (data) {
                        let _rsList: Array<RoleSet> = _.map(data.listRoleSetDto, (rs: any) => {
                            return new RoleSet(rs.code, rs.name);
                        });
                        _.each(_rsList, rs => self.roleSetList.push(rs));

                        let _jtList: Array<JobTitle> = _.map(data.listJobTitleDto, (jt: any) => {
                            return new JobTitle(jt.id, jt.code, jt.name);
                        });
                        _.each(_jtList, jt => self.jobTitleList.push(jt));

                        self.roleSetJobTitle(new RoleSetJobTitle(false, self.jobTitleList(), self.roleSetList()));
                        if (data.roleSetGrantedJobTitleDto) {
                            self.roleSetJobTitle().applyToConcurrentPerson(data.roleSetGrantedJobTitleDto.applyToConcurrentPerson);
                            let details = self.roleSetJobTitle().details();
                            _.each(details, (d: any) => {
                                _.each(data.roleSetGrantedJobTitleDto.details, (dd: any) => {
                                    if (d.jobTitleId == dd.jobTitleId) {
                                        d.roleSetCd(dd.roleSetCd);
                                    }
                                });
                            });
                            self.roleSetJobTitle().details(details);
                        }
                    } else {
                        nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                    }
                    
                    $("#A4").focus();
                    dfd.resolve();
                }).fail(function(error) {
                    alertError(error).then(() => {
                        if (error.messageId == "Msg_713" || error.messageId == "Msg_712") {
                            nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                        }
                    });
                    dfd.reject();
                }).always(() => {
                    block.clear();
                });
                return dfd.promise();
            }

            findBtnClick(): void {
                $("#A3_4").trigger("validate");
                if (!error.hasError()) {
                    this.startPage();
                }
            }

            register() {
                if (nts.uk.ui.errors.hasError()) {
                    return;
                }
                let self = this, data: RoleSetJobTitle = ko.toJS(self.roleSetJobTitle), regDetails = [];

                _.each(data.details, (d: any) => regDetails.push({ roleSetCd: d.roleSetCd, jobTitleId: d.jobTitleId }));

                let command: any = {
                    applyToConcurrentPerson: data.applyToConcurrentPerson,
                    details: regDetails
                };

                block.invisible();

                service.registerData(command).done(function() {
                    info({ messageId: "Msg_15" }).then(() => {
                        $("#A4").focus();
                    });
                }).fail(error => {
                    alertError(error);
                }).always(() => {
                    block.clear();
                });
            }
            
            loadTab2() {
                let self = this;
                error.clearAll();
                if (self.firstLoadTab2()) {
                    self.viewmodelB.startPage();
                    self.firstLoadTab2(false);
                }
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

    export class JobTitle {
        id: string;
        code: string;
        name: string;

        constructor(id: string, code: string, name: string) {
            this.id = id;
            this.code = code;
            this.name = name;
        }
    }

    export class RoleSetJobTitleDetail {
        roleSetCd: KnockoutObservable<string>;
        jobTitleId: string;
        roleSetList: Array<RoleSet>;
        jobTitle: JobTitle;

        constructor(jobTitle: JobTitle, roleSetList: Array<RoleSet>) {
            this.roleSetCd = ko.observable(roleSetList[0].code);
            this.jobTitleId = jobTitle.id;
            this.jobTitle = jobTitle;
            this.roleSetList = roleSetList;
        }
    }

    export class RoleSetJobTitle {
        applyToConcurrentPerson: KnockoutObservable<boolean>;
        details: KnockoutObservableArray<RoleSetJobTitleDetail>;

        constructor(applyToConcurrentPerson: boolean, jobTitleList: Array<JobTitle>, roleSetList: Array<RoleSet>) {
            this.applyToConcurrentPerson = ko.observable(applyToConcurrentPerson);
            this.details = ko.observableArray([]);
            _.each(jobTitleList, j => this.details.push(new RoleSetJobTitleDetail(j, roleSetList)));
        }
    }

}

