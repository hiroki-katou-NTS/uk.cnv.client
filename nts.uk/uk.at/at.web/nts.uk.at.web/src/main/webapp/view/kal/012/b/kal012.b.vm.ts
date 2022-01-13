/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kal012.b {

    const API = {
        register: "at/function/alarm/exmail/settings/register",
        init: "at/function/alarm/exmail/settings/init",
        get_role_name: "ctx/sys/auth/role/get/rolename/by/roleids"
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string>;
        isUpdateMode: KnockoutObservable<boolean>;
        model: Model = new Model();
        roundingRules: KnockoutObservableArray<any>;
        normal: ExMailSettingsNormalAuto;
        auto: ExMailSettingsNormalAuto;
        sendingRole: SendingRole;
        isNormalSet: KnockoutObservable<boolean>;
        isAutoSet: KnockoutObservable<boolean>;

        constructor(params: any) {
            super();
            const vm = this;
            vm.tabs = ko.observableArray([
                {
                    id: 'tab-1',
                    title: vm.$i18n('KAL012_16'),
                    content: '.tab-content-1',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-2',
                    title: vm.$i18n('KAL012_17'),
                    content: '.tab-content-2',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
            ]);
            vm.selectedTab = ko.observable('tab-1');
            vm.model.b42(vm.$i18n('KAL012_13'));
            vm.model.b41(vm.$i18n('KAL012_13'));
            vm.roundingRules = ko.observableArray([
                {code: true, name: vm.$i18n('KAL012_19')},
                {code: false, name: vm.$i18n('KAL012_20')}
            ]);
            vm.isUpdateMode = ko.observable(false);
            vm.isNormalSet = ko.observable(false);
            vm.isAutoSet = ko.observable(false);
        }

        created() {
            const vm = this;
            _.extend(window, {vm});
            vm.init().done(() => {
                window.setTimeout(function () {
                    document.getElementById('B1_2').focus();
                }, 0);
            });
            vm.isNormalSet.subscribe((newValue: any) => {
                if (newValue) {
                    vm.model.b41(vm.$i18n('KAL012_14'))
                } else {
                    vm.model.b41(vm.$i18n('KAL012_13'))
                }
            });
            vm.isAutoSet.subscribe((newValue: any) => {
                if (newValue) {
                    vm.model.b42(vm.$i18n('KAL012_14'))
                } else {
                    vm.model.b42(vm.$i18n('KAL012_13'))
                }
            });
            vm.model.selectedRuleCode.subscribe((newValue: any) => {
                vm.$errors("clear");
            });

            vm.model.rolesId.subscribe((value: Array<string>) => {
                vm.$errors("clear");
                vm.$ajax("com", API.get_role_name, value)
                    .done(function (listRole: Array<RoleDto>) {
                        vm.model.targetRuleName(_.join(_.map(listRole, i => i.name), '、'));
                    });
            })
        }

        setNormalAuto(normalAutoClassify: number) {
            let vm = this;
            let normalAuto = {
                "individualWkpClassify": 1,
                "normalAutoClassify": normalAutoClassify,
                "personalManagerClassify": 1,
                "mailContents": {
                    "subject": "",
                    "text": "",
                    "mailAddressBCC": [],
                    "mailAddressCC": [],
                    "mailRely": ""
                },
                "senderAddress": "",
                "sendResult": true
            };
            if (normalAutoClassify === 0) {
                vm.normal = normalAuto;
            }

            if (normalAutoClassify === 1) {
                vm.auto = normalAuto;
            }
        }

        /**
         * init screen
         */
        init(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred<any>();
            vm.$blockui("invisible");
            vm.setNormalAuto(0);// init for normal
            vm.setNormalAuto(1); // init for auto
            vm.$ajax(API.init).done((data) => {
                let isNormalSet = false;
                let isAutoSet = false;
                if (data) {
                    if (data.mailSettings.exMailSettingsList.length > 0) {
                        for (let item of data.mailSettings.exMailSettingsList) {
                            if (item.normalAutoClassify === 0 && (item.mailContents != null)) {
                                vm.normal = item;
                                isNormalSet = true;
                            }
                            if (item.normalAutoClassify) {
                                vm.auto = item;
                                if(item.mailContents != null){
                                    isAutoSet = true;
                                }
                            }
                        }
                    }
                    vm.isAutoSet(isAutoSet);
                    vm.isNormalSet(isNormalSet);
                    vm.model.isMailAlreadySet(data.mailSettings.preConfigured);

                    if (data.sendingRole) {
                        vm.sendingRole = data.sendingRole;
                        vm.model.selectedRuleCode(vm.sendingRole.roleSetting)
                        vm.model.checked(vm.sendingRole.sendResult);
                        vm.model.rolesId(vm.sendingRole.roleIds);
                    }
                }
                dfd.resolve();
            }).fail(function (error) {
                dfd.reject();
            }).always(() => {
                vm.$blockui("clear");
            });
            return dfd.promise();
        }

        mounted() {
            const vm = this;
        }

        /**
         * function for back to screen
         */
        backToAScreen() {
            let vm = this;
        }

        /**
         * click register button
         * */
        clickRegistrationButton() {
            let vm = this;
            vm.$validate("#B7_5").then((valid) => {
                if (valid) {
                    let mailSettingList: any = [];
                    let command = {
                        mailSettingList: [vm.normal, vm.auto],
                        sendingRole: {
                            individualWkpClassify: 1,
                            roleSetting: vm.model.selectedRuleCode(),
                            sendResult: vm.model.checked(),
                            roleIds: vm.model.rolesId()
                        }
                    }
                    vm.$blockui("grayout");
                    vm.$ajax(API.register, command).done((res) => {
                        vm.$dialog.info({messageId: "Msg_15"}).then(() => {
                            vm.isUpdateMode(true);
                            vm.init();
                        });
                    }).fail((err) => {
                        vm.$dialog.error(err);
                    }).always(() => vm.$blockui("clear"));
                }
            });
        }

        /**
         * openCDL025
         * */
        openCDL025() {
            let vm = this;
            let param = {
                currentCode: vm.model.rolesId(),
                roleType: 3,
                multiple: true,
                roleAtr: 1
            };
            nts.uk.ui.windows.setShared("paramCdl025", param);
            nts.uk.ui.windows.sub.modal("com", "/view/cdl/025/index.xhtml").onClosed(() => {
                let data: Array<string> = nts.uk.ui.windows.getShared("dataCdl025");
                if (!nts.uk.util.isNullOrUndefined(data)) {
                    vm.model.rolesId(data);
                }
            });
        }

        /**
         * open modal cgg027 for auto settings
         * */
        setMailAutoAd() {
            var vm = this;
            nts.uk.ui.windows.setShared("sendingAddressCheck", true);
            vm.setPara(true);
            if (vm.auto.mailContents == null) {
                vm.auto.mailContents = {
                    "subject": "",
                    "text": "",
                    "mailAddressBCC": [],
                    "mailAddressCC": [],
                    "mailRely": ""
                };
            }
            nts.uk.ui.windows.setShared("senderAddress", vm.auto.senderAddress)
            nts.uk.ui.windows.setShared("MailSettings", vm.auto.mailContents);
            nts.uk.ui.windows.sub.modal("com", "view/ccg/027/a/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("MailSettings");
                if (data != null) {
                    vm.auto.mailContents = data;
                    vm.auto.senderAddress = nts.uk.ui.windows.getShared("senderAddress");
                }
            });
        }

        /**
         * open modal cgg027 for manual settings
         * */
        setMailManual() {
            var vm = this;
            nts.uk.ui.windows.setShared("sendingAddressCheck", true);
            vm.setPara(false);
            if (vm.normal.mailContents == null) {
                vm.normal.mailContents = {
                    "subject": "",
                    "text": "",
                    "mailAddressBCC": [],
                    "mailAddressCC": [],
                    "mailRely": ""
                };
            }
            nts.uk.ui.windows.setShared("senderAddress", vm.normal.senderAddress)
            nts.uk.ui.windows.setShared("MailSettings", vm.normal.mailContents);
            nts.uk.ui.windows.sub.modal("com", "view/ccg/027/a/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("MailSettings");
                if (data != null) {
                    vm.normal.mailContents = data;
                    vm.normal.senderAddress = nts.uk.ui.windows.getShared("senderAddress");
                }
            });
        }

        /**
         * initial cgg027 modal params *
         *
         * */
        setPara(isAuto: boolean) {
            nts.uk.ui.windows.setShared("SetCC", true);
            nts.uk.ui.windows.setShared("SetBCC", true);
            nts.uk.ui.windows.setShared("SetReply", true);
            nts.uk.ui.windows.setShared("SetSubject", true);
            nts.uk.ui.windows.setShared("SetBody", true);
            nts.uk.ui.windows.setShared("wording", "");
            if (isAuto) {
                nts.uk.ui.windows.setShared("sendingAddressCheck", true);
            } else {
                nts.uk.ui.windows.setShared("sendingAddressCheck", false);
            }
        }
    }

    interface RoleDto {
        // Id
        roleId: string;
        // コード
        roleCode: string;
        // ロール種類
        roleType: number;
        // 参照範囲
        employeeReferenceRange: number;
        // ロール名称
        name: string;
        // 契約コード
        contractCode: string;
        // 担当区分
        assignAtr: number;
        // 会社ID
        companyId: string;
    }

    export interface SendingRole {
        individualWkpClassify?: number;
        roleSetting?: boolean;
        sendResult: boolean;
        roleIds: Array<string>;
    }

    export interface ExMailSettingsNormalAuto {
        individualWkpClassify?: number;
        normalAutoClassify?: number;
        personalManagerClassify?: number;
        mailContents?: MailSettingsDto;
        senderAddress?: string;
        sendResult?: boolean;
    }

    export interface MailSettingsDto {
        subject?: string;
        text?: string;
        mailAddressBCC: Array<string>;
        mailAddressCC: Array<string>;
        mailRely?: string;
    }

    class Model {
        isMailAlreadySet: KnockoutObservable<boolean>;
        isMailSet: KnockoutObservable<boolean>;
        b41: KnockoutObservable<any>;
        b42: KnockoutObservable<any>;
        checked: KnockoutObservable<any>;
        selectedRuleCode: KnockoutObservable<boolean>;
        targetRuleName: KnockoutObservable<any>;
        rolesId: KnockoutObservableArray<any>;

        constructor() {
            this.isMailAlreadySet = ko.observable(false);
            this.isMailSet = ko.observable(false);
            this.b41 = ko.observable("");
            this.b42 = ko.observable("");
            this.checked = ko.observable(false);
            this.selectedRuleCode = ko.observable(false);
            this.targetRuleName = ko.observable('');
            this.rolesId = ko.observableArray([]);
        }
    }
}