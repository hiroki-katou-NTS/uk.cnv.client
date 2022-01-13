/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kal002.c {
    const PATH_API = {
        init: "at/function/alarm/kal002/emailSetting/init",
        register: "at/function/alarm/kal002/emailSetting/register",
        get_role_name: "ctx/sys/auth/role/get/rolename/by/roleids"
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        // TabPanel Component
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string>;
        // Switch button Component
        roundingRules: KnockoutObservableArray<any>;
        selectedRoleSetting: any;
        // Declare object
        alarmMailSendingRole: IAlarmMailSendingRole;
        manualPerson: IAlarmListExecutionMailSetting;
        manualAdmin: IAlarmListExecutionMailSetting;
        autoPerson: IAlarmListExecutionMailSetting;
        autoAdmin: IAlarmListExecutionMailSetting;
        // Flag check already configured for each type
        isConfiguredManualPerson: KnockoutObservable<boolean> = ko.observable(false);
        isConfiguredManualAdmin: KnockoutObservable<boolean> = ko.observable(false);
        isConfiguredAutoPerson: KnockoutObservable<boolean> = ko.observable(false);
        isConfiguredAutoAdmin: KnockoutObservable<boolean> = ko.observable(false);
        statusConfigManualPerson: KnockoutObservable<string> = ko.observable(this.$i18n('KAL012_13'));
        statusConfigManualAdmin: KnockoutObservable<string> = ko.observable(this.$i18n('KAL012_13'));
        statusConfigAutoPerson: KnockoutObservable<string> = ko.observable(this.$i18n('KAL012_13'));
        statusConfigAutoAdmin: KnockoutObservable<string> = ko.observable(this.$i18n('KAL012_13'));
        // Role setting
        enableRoleSetting: KnockoutObservable<boolean> = ko.observable(false);
        // roles: KnockoutObservableArray<IRole> = ko.observableArray([]);
        roleName: KnockoutObservable<any> = ko.observable('');
        currentRoleCodes: KnockoutObservableArray<any> = ko.observableArray([]);

        constructor(params: any) {
            super();
            const vm = this;
            // TabPanel
            vm.tabs = ko.observableArray([
                {
                    id: 'tab-1',
                    title: vm.$i18n('KAL002_16'),
                    content: '.tab-content-1',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-2',
                    title: vm.$i18n('KAL002_17'),
                    content: '.tab-content-2',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                }
            ]);
            vm.selectedTab = ko.observable('tab-1');
            // Switch button: USE(0), NOT_USE(1)
            vm.roundingRules = ko.observableArray([
                {code: 0, name: vm.$i18n('KAL002_19')},
                {code: 1, name: vm.$i18n('KAL002_20')}
            ]);
            vm.selectedRoleSetting = ko.observable(1);
            //Init object mail setting
            vm.manualPerson = ExecutionMailSetting.create(0, 0);
            vm.manualAdmin = ExecutionMailSetting.create(0, 1);
            vm.autoPerson = ExecutionMailSetting.create(1, 0);
            vm.autoAdmin = ExecutionMailSetting.create(1, 1);
        }

        created(params: any) {
            const vm = this;
            vm.init();
            vm.isConfiguredManualPerson.subscribe((newValue: any) => {
                if (newValue)
                    vm.statusConfigManualPerson(vm.$i18n('KAL002_14'));
                else
                    vm.statusConfigManualPerson(vm.$i18n('KAL002_13'));
            });
            vm.isConfiguredManualAdmin.subscribe((newValue: any) => {
                if (newValue)
                    vm.statusConfigManualAdmin(vm.$i18n('KAL002_14'));
                else
                    vm.statusConfigManualAdmin(vm.$i18n('KAL002_13'));
            });
            vm.isConfiguredAutoPerson.subscribe((newValue: any) => {
                if (newValue)
                    vm.statusConfigAutoPerson(vm.$i18n('KAL002_14'));
                else
                    vm.statusConfigAutoPerson(vm.$i18n('KAL002_13'));
            });
            vm.isConfiguredAutoAdmin.subscribe((newValue: any) => {
                if (newValue)
                    vm.statusConfigAutoAdmin(vm.$i18n('KAL002_14'));
                else
                    vm.statusConfigAutoAdmin(vm.$i18n('KAL002_13'));
            });
            vm.selectedRoleSetting.subscribe(function (newValue: any) {
                vm.$errors("clear");
                newValue === 0 ? vm.enableRoleSetting(true) : vm.enableRoleSetting(false);
            });
            vm.currentRoleCodes.subscribe((newValue: Array<string>) => {
                vm.$ajax("com", PATH_API.get_role_name, newValue)
                    .done(function (listRole: Array<IRole>) {
                        vm.roleName(_.join(_.map(listRole, i => i.name), '、'));
                    }).fail(function (err) {
                    console.log(err);
                });
            });
        }

        mounted() {
            const vm = this;
            $('#B1_2').focus();
        }

        /**
         * Check already configured mail setting
         * @param {nts.uk.at.view.kal002.c.IMailSetting} mailSetting
         * @returns {boolean}
         */
        isAlreadyConfigured(mailSetting: IMailSetting): boolean {
            return !_.isNil(mailSetting.subject) || !_.isNil(mailSetting.text) || !_.isNil(mailSetting.mailRely)
                || !_.isEmpty(mailSetting.mailAddressBCC) || !_.isEmpty(mailSetting.mailAddressCC);
        }

        /**
         * init data when start screen
         * @returns {JQueryPromise<any>}
         */
        init(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred<any>();
            vm.$blockui("invisible");
            vm.$ajax(PATH_API.init).done((data) => {
                if (data) {
                    if (!_.isEmpty(data.alarmExecutionMailSetting)) {
                        for (let item of data.alarmExecutionMailSetting) {
                            if (item.mailSettingInfo.normalAutoClassify === 0 && item.mailSettingInfo.personalManagerClassify === 0) {
                                vm.manualPerson = item.mailSettingInfo;
                                item.alreadyConfigured ? vm.isConfiguredManualPerson(true) : vm.isConfiguredManualPerson(false);
                            } else if (item.mailSettingInfo.normalAutoClassify === 0 && item.mailSettingInfo.personalManagerClassify === 1) {
                                vm.manualAdmin = item.mailSettingInfo;
                                item.alreadyConfigured ? vm.isConfiguredManualAdmin(true) : vm.isConfiguredManualAdmin(false);
                            } else if (item.mailSettingInfo.normalAutoClassify === 1 && item.mailSettingInfo.personalManagerClassify === 0) {
                                vm.autoPerson = item.mailSettingInfo;
                                item.alreadyConfigured ? vm.isConfiguredAutoPerson(true) : vm.isConfiguredAutoPerson(false);
                            } else if (item.mailSettingInfo.normalAutoClassify === 1 && item.mailSettingInfo.personalManagerClassify === 1) {
                                vm.autoAdmin = item.mailSettingInfo;
                                item.alreadyConfigured ? vm.isConfiguredAutoAdmin(true) : vm.isConfiguredAutoAdmin(false);
                            }
                        }
                    }

                    if (!_.isNil(data.alarmMailSendingRole)) {
                        vm.alarmMailSendingRole = data.alarmMailSendingRole;
                        vm.alarmMailSendingRole.roleSetting ? vm.selectedRoleSetting(0) : vm.selectedRoleSetting(1);
                        vm.currentRoleCodes(data.alarmMailSendingRole.roleIds);
                    }
                    if (!_.isEmpty(data.roleList)) {
                        // vm.roles(data.roleList);
                        vm.roleName(_.join(_.map(data.roleList, 'roleName'), '、'));
                    }
                }
                dfd.resolve();
            }).fail(function (error) {
                dfd.reject();
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("clear");
            });
            return dfd.promise();
        }

        /**
         * Registration button
         */
        registration() {
            let vm = this;
            vm.$validate(".nts-input").then((valid) => {
                if (valid) {
                    let executionMailSettingCommand: Array<IAlarmListExecutionMailSetting> = [vm.manualPerson, vm.manualAdmin, vm.autoPerson, vm.autoAdmin];
                    let alarmMailSendingRoleCommand = new AlarmMailSendingRoleCommand(
                        0,
                        vm.enableRoleSetting(),  //B8_2, B8_3
                        false,
                        vm.currentRoleCodes() //B8_5
                    );

                    let command = {executionMailSettingCommand, alarmMailSendingRoleCommand};
                    vm.$blockui("grayout");
                    vm.$ajax(PATH_API.register, command).done((res) => {
                        vm.$dialog.info({messageId: "Msg_15"}).then(() => {
                            vm.init();
                        });
                    }).fail((err) => {
                        vm.$dialog.error(err);
                    }).always(() => vm.$blockui("clear"));
                }
            });
        }

        /**
         * Manual mail setting for personal
         */
        manualMailSettingForPersonal() {
            let vm = this;
            vm.setParamCCG027(false);
            nts.uk.ui.windows.setShared("senderAddress", vm.manualPerson.senderAddress);
            nts.uk.ui.windows.setShared("MailSettings", vm.manualPerson.contentMailSettings);

            nts.uk.ui.windows.sub.modal("com", "view/ccg/027/a/index.xhtml").onClosed(() => {
                let mailSettingContent = nts.uk.ui.windows.getShared("MailSettings");
                if (!_.isNil(mailSettingContent))
                    vm.manualPerson.contentMailSettings = mailSettingContent;
                let sender = nts.uk.ui.windows.getShared("senderAddress");
                if (!_.isNil(sender))
                    vm.manualPerson.senderAddress = sender === "" ? null : sender;
            });
        }

        /**
         * Manual mail setting for manager
         */
        manualMailSettingForManager() {
            let vm = this;
            vm.setParamCCG027(false);
            nts.uk.ui.windows.setShared("senderAddress", vm.manualAdmin.senderAddress);
            nts.uk.ui.windows.setShared("MailSettings", vm.manualAdmin.contentMailSettings);

            nts.uk.ui.windows.sub.modal("com", "view/ccg/027/a/index.xhtml").onClosed(() => {
                let mailSettingContent = nts.uk.ui.windows.getShared("MailSettings");
                if (!_.isNil(mailSettingContent))
                    vm.manualAdmin.contentMailSettings = mailSettingContent;
                let sender = nts.uk.ui.windows.getShared("senderAddress");
                if (!_.isNil(sender))
                    vm.manualAdmin.senderAddress = sender === "" ? null : sender;
            });
        }

        /**
         * Automatic mail setting for personal
         */
        automaticMailSettingForPersonal() {
            let vm = this;
            vm.setParamCCG027(true);
            nts.uk.ui.windows.setShared("senderAddress", vm.autoPerson.senderAddress);
            nts.uk.ui.windows.setShared("MailSettings", vm.autoPerson.contentMailSettings);

            nts.uk.ui.windows.sub.modal("com", "view/ccg/027/a/index.xhtml").onClosed(() => {
                let mailSettingContent = nts.uk.ui.windows.getShared("MailSettings");
                if (!_.isNil(mailSettingContent))
                    vm.autoPerson.contentMailSettings = mailSettingContent;
                let sender = nts.uk.ui.windows.getShared("senderAddress");
                if (!_.isNil(sender))
                    vm.autoPerson.senderAddress = sender === "" ? null : sender;
            });
        }

        /**
         * Automatic mail setting for manager
         */
        automaticMailSettingForManager() {
            let vm = this;
            vm.setParamCCG027(true);
            nts.uk.ui.windows.setShared("senderAddress", vm.autoAdmin.senderAddress);
            nts.uk.ui.windows.setShared("MailSettings", vm.autoAdmin.contentMailSettings);

            nts.uk.ui.windows.sub.modal("com", "view/ccg/027/a/index.xhtml").onClosed(() => {
                let mailSettingContent = nts.uk.ui.windows.getShared("MailSettings");
                if (!_.isNil(mailSettingContent))
                    vm.autoAdmin.contentMailSettings = mailSettingContent;
                let sender = nts.uk.ui.windows.getShared("senderAddress");
                if (!_.isNil(sender))
                    vm.autoAdmin.senderAddress = sender === "" ? null : sender;
            });
        }

        /**
         * Set relate parameters before get dialog CCG027
         */
        setParamCCG027(isAuto: boolean) {
            nts.uk.ui.windows.setShared("sendingAddressCheck", isAuto);
            nts.uk.ui.windows.setShared("SetCC", true);
            nts.uk.ui.windows.setShared("SetBCC", true);
            nts.uk.ui.windows.setShared("SetReply", true);
            nts.uk.ui.windows.setShared("SetSubject", true);
            nts.uk.ui.windows.setShared("SetBody", true);
            nts.uk.ui.windows.setShared("wording", "");
        }
        
        public openDialogCDL011() {
          nts.uk.ui.windows.setShared("CDL011_PARAM", 9);
          nts.uk.ui.windows.sub.modal("com", "/view/cdl/011/a/index.xhtml");
        }

        /**
         * open Dialog CDL025 RoleList
         */
        openDialogRoleList() {
            let vm = this;
            let param = {
                roleType: 3,
                multiple: true,
                currentCode: vm.currentRoleCodes(),
                roleAtr: 1
            };
            nts.uk.ui.windows.setShared("paramCdl025", param);
            nts.uk.ui.windows.sub.modal("com", "/view/cdl/025/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("dataCdl025");
                if (!nts.uk.util.isNullOrUndefined(data))
                    vm.currentRoleCodes(data);
            });
        }
    }

    export interface IAlarmListExecutionMailSetting {
        individualWkpClassify?: number;
        normalAutoClassify?: number;
        personalManagerClassify?: number;
        contentMailSettings?: IMailSetting;
        senderAddress?: string;
        sendResult?: boolean;
    }

    export interface IMailSetting {
        subject?: string;
        text?: string;
        mailAddressBCC: Array<string>;
        mailAddressCC: Array<string>;
        mailRely?: string;
    }

    export interface IAlarmMailSendingRole {
        individualWkpClassify?: number;
        roleSetting?: boolean;
        sendResult: boolean;
        roleIds: Array<string>;
    }

    export interface IRole {
        roleId: string;
        name: string;
    }

    class ExecutionMailSetting {
        individualWkpClassify?: number;
        normalAutoClassify?: number;
        personalManagerClassify?: number;
        contentMailSettings?: ContentMailSetting;
        senderAddress?: string;
        sendResult?: boolean;

        constructor(individualWkpClassify: number, normalAutoClassify: number, personalManagerClassify: number, contentMailSettings: ContentMailSetting, senderAddress: string, sendResult: boolean) {
            this.individualWkpClassify = individualWkpClassify;
            this.normalAutoClassify = normalAutoClassify;
            this.personalManagerClassify = personalManagerClassify;
            this.contentMailSettings = contentMailSettings;
            this.senderAddress = senderAddress;
            this.sendResult = sendResult;
        }

        static create(normalAutoClassify: number, personManagerClassify: number) {
            return new ExecutionMailSetting(
                0,
                normalAutoClassify,
                personManagerClassify,
                new ContentMailSetting(
                    null,
                    null,
                    [],
                    [],
                    null
                ),
                null,
                false
            );
        }
    }

    class ContentMailSetting {
        subject?: string;
        text?: string;
        mailAddressBCC: Array<string>;
        mailAddressCC: Array<string>;
        mailRely?: string;

        constructor(subject: string, text: string, mailAddressBCC: Array<string>, mailAddressCC: Array<string>, mailRely: string) {
            this.subject = subject;
            this.text = text;
            this.mailAddressBCC = mailAddressBCC;
            this.mailAddressCC = mailAddressCC;
            this.mailRely = mailRely;
        }
    }

    class AlarmMailSendingRoleCommand {
        individualWkpClassify?: number;
        roleSetting: boolean;
        sendResult: boolean;
        roleIds: Array<string>;

        constructor(individualWkpClassify: number, roleSetting: boolean, sendResult: boolean, roleIds: Array<string>) {
            this.individualWkpClassify = individualWkpClassify;
            this.roleSetting = roleSetting;
            this.sendResult = sendResult;
            this.roleIds = roleIds;
        }
    }
}