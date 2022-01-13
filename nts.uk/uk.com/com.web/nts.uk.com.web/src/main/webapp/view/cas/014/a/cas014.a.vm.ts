module nts.uk.com.view.cas014.a {
    var paths = {
        getAllData: "ctx/sys/auth/grant/rolesetjob/start",
        registerData: "ctx/sys/auth/grant/rolesetjob/register"
    };

    @bean()
    class ScreenModel extends ko.ViewModel {
        date: KnockoutObservable<string>;
        roleSetList: KnockoutObservableArray<RoleSet>;
        jobTitleList: KnockoutObservableArray<JobTitle>;
        roleSetJobTitle: KnockoutObservable<RoleSetJobTitle>;

        constructor() {
            super();
            let self = this;
            self.date = ko.observable(new Date().toISOString());
            self.roleSetList = ko.observableArray([]);
            self.jobTitleList = ko.observableArray([]);
            self.roleSetJobTitle = ko.observable(new RoleSetJobTitle(false, self.jobTitleList(), self.roleSetList()));
            $("#A4").ntsFixedTable({ height: 405 });
        }

        created() {
            const vm = this;
            vm.startPage();
        }

        startPage(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.$blockui("show");
            self.roleSetList.removeAll();
            self.jobTitleList.removeAll();
            self.$ajax("com", paths.getAllData, {refDate: new Date(self.date()).toISOString()}).done(function(data: any) {
                if (data) {
                    let _rsList: Array<RoleSet> = _.map(data.listRoleSetDto, (rs: any) => {
                        return new RoleSet(rs.code, rs.name);
                    });
                    self.roleSetList(_rsList);

                    let _jtList: Array<JobTitle> = _.map(data.listJobTitleDto, (jt: any) => {
                        return new JobTitle(jt.id, jt.code, jt.name);
                    });
                    self.jobTitleList(_jtList);

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

                if (!_.isEmpty($("#A4 .ui-igcombo-wrapper"))) $("#A4 .ui-igcombo-wrapper")[0].focus();
                dfd.resolve();
            }).fail(function(error) {
                self.$dialog.error(error).then(() => {
                    if (error.messageId == "Msg_713" || error.messageId == "Msg_712" || error.messageId == "Msg_1103") {
                        nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
                    }
                });
                dfd.reject();
            }).always(() => {
                self.$blockui("hide");
            });
            return dfd.promise();
        }

        findBtnClick(): void {
            const vm = this;
            $("#A3_4").trigger("validate");
            if (!nts.uk.ui.errors.hasError()) {
                vm.startPage();
            }
        }

        register() {
            let self = this,
                data: RoleSetJobTitle = ko.toJS(self.roleSetJobTitle),
                regDetails: Array<any> = [];

            data.details.forEach((d: RoleSetJobTitleDetail, index: number) => {
                if (_.isEmpty(d.roleSetCd)) {
                    $("#a4m3_" + index).ntsError("set", { messageId: "Msg_2190", messageParams: [d.jobTitle.code, d.jobTitle.name] });
                } else {
                    regDetails.push({ roleSetCd: d.roleSetCd, jobTitleId: d.jobTitleId });
                }
            });

            if (nts.uk.ui.errors.hasError()) {
                return;
            }

            let command: any = {
                applyToConcurrentPerson: data.applyToConcurrentPerson,
                details: regDetails
            };

            self.$blockui("show");
            self.$ajax("com", paths.registerData, command).done(function() {
                self.$dialog.info({ messageId: "Msg_15" }).then(() => {
                    if (!_.isEmpty($("#A4 .ui-igcombo-wrapper"))) $("#A4 .ui-igcombo-wrapper")[0].focus();
                });
            }).fail(error => {
                self.$dialog.error(error);
            }).always(() => {
                self.$blockui("hide");
            });
        }

        openSpecialSettingDialog() {
            let vm = this;
            vm.$window.modal("/view/cas/014/b/index.xhtml");
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
            this.roleSetCd = ko.observable(null);
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

