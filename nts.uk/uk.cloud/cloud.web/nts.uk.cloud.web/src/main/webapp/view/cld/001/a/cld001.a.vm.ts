module nts.uk.cloud.view.cld001.a {
    export module viewmodel {
        import blockUI = nts.uk.ui.block;
        import RegistTenantDto = service.registTenantDto;
        import generatePasswordDto = service.generatePasswordDto;

        export class ScreenModel{

        	tenantCode: KnockoutObservable<string>;
        	startDate: KnockoutObservable<string>;
        	password: KnockoutObservable<string>;

        	tenantManagerID: KnockoutObservable<string>;
        	tenantManagerPassword: KnockoutObservable<string>;
        	companyName: KnockoutObservable<string>;

        	billingTypes: KnockoutObservableArray<any> = ko.observableArray([
                { code: 0, name: '従量制' },
                { code: 1, name: '定額制' }
            ]);
        	billingType: KnockoutObservable<number>;

        	useCheckDigit_tenantCode: KnockoutObservable<boolean>;
        	useCheckDigit_optionCode: KnockoutObservable<boolean>;
        	optionCode: KnockoutObservable<string>;

        	hrContractCode: KnockoutObservable<string>;

        	numberOfLicence_at: KnockoutObservable<number>;
        	numberOfLicence_hr: KnockoutObservable<number>;
        	numberOfLicence_pr: KnockoutObservable<number>;
        	numbereditor_at: any;
        	numbereditor_hr: any;
        	numbereditor_pr: any;

        	useSSO_SAML: KnockoutObservable<boolean>;

            constructor() {
                let self = this;
                self.clearPage();
            }

            private clearPage() {
                let self = this;

                self.tenantCode = ko.observable('');
                self.useCheckDigit_tenantCode = ko.observable(true);
                self.startDate = ko.observable('');
                self.password = ko.observable('');
                self.tenantManagerID = ko.observable('');
                self.tenantManagerPassword = ko.observable('');
                self.companyName = ko.observable('');

                self.billingType = ko.observable(0);

                self.optionCode = ko.observable('');
                self.useCheckDigit_optionCode = ko.observable(true);

                self.hrContractCode = ko.observable('');

                self.numbereditor_at = {
            			numberOfLicence_at: ko.observable(0),
            			constraint: '',
            			option: new nts.uk.ui.option.NumberEditorOption({
            				grouplength: 3,
            				symbolChar: "人"
            			}),
            			enable: ko.observable(true),
            			readonly: ko.observable(false)
            		};
                self.numbereditor_hr = {
            			numberOfLicence_hr: ko.observable(0),
            			constraint: '',
            			option: new nts.uk.ui.option.NumberEditorOption({
            				grouplength: 3,
            				symbolChar: "人"
            			}),
            			enable: ko.observable(true),
            			readonly: ko.observable(false)
            		};
                self.numbereditor_pr = {
            			numberOfLicence_pr: ko.observable(0),
            			constraint: '',
            			option: new nts.uk.ui.option.NumberEditorOption({
            				grouplength: 3,
            				symbolChar: "人"
            			}),
            			enable: ko.observable(true),
            			readonly: ko.observable(false)
            		};
                self.useSSO_SAML = ko.observable(true);
            }

            start(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<void>();

                dfd.resolve();
                return dfd.promise();
            }

            generatePassword() {

            }

            regist() {
                var self = this;
                if (self.useCheckDigit_tenantCode()) {
                    $("#txtTenantCode").trigger("validate");
                }

                //if (self.useCheckDigit_optionCode()) {
                //    $("#txtOptionCode").trigger("validate");
                //}

                if (nts.uk.ui.errors.hasError()) {
                    return;
                }

                var command = <RegistTenantDto> {};
                command.tenanteCode = self.tenantCode();
                command.tenantPassword = self.password();
                command.tenantStartDate = self.startDate();
                command.administratorLoginId = self.tenantManagerID();
                command.administratorPassword = self.tenantManagerPassword();
                command.optionCode = self.optionCode();
				command.companyName = self.companyName();
                service.registTenant(command)
                .done(function(res: any) {
                    nts.uk.ui.dialog.info({
                        message: nts.uk.resource.getMessage("Msg_1148", []).replace(/Com_Company/g, "会社")
                    }).then(function() {
                        self.executionMasterCopyData();
                    });
                }).fail(function(rej: any){
                    nts.uk.ui.dialog.alert(rej.errorMessage);
                    console.log(rej);
                });

            }

            public executionMasterCopyData(): void {
                var self = this;

                nts.uk.ui.block.grayout();
                service.executionMasterCopyData({
                    tenantCode: self.tenantCode()
                }).done(function(res: any) {
                    self.updateState(res.id, self.tenantCode());
                }).fail(function(res: any) {
                    console.log(res);
                    nts.uk.ui.block.clear();
                });
            }

            private updateState(id: string, tenantCode: string): void {
                let self = this;
                nts.uk.deferred.repeat(conf => conf
                    .task(() => {
                        return service.getTaskInfo(id, tenantCode).done(function(res: any) {
                            if (res.succeeded || res.failed || res.cancelled || res.status == "REQUESTED_CANCEL") {
                                let numberSuccess = res.taskDatas.find((taskData:TaskData) => taskData.key == 'NUMBER_OF_SUCCESS').valueAsString;
                                let numberFail = res.taskDatas.find((taskData:TaskData) => taskData.key == 'NUMBER_OF_ERROR').valueAsString;
                                nts.uk.ui.dialog.info({
                                    message: nts.uk.text.format("処理が完了しました。正常：{0}件、エラー：{1}件",numberSuccess, numberFail)
                                });
                                nts.uk.ui.block.clear();
                            }
                        });
                    }).while(infor => {
                        return (infor.pending || infor.running) && infor.status != "REQUESTED_CANCEL";
                    }).pause(1000));
            }
        }

        class TaskData {
            key: string;
            valueAsBoolean: boolean;
            valueAsNumber: number;
            valueAsString: string;
        }
    }
}