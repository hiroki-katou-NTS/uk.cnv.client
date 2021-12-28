module nts.uk.at.view.kaf020.c.viewmodel {
    import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import OptionalItemApplicationContent = nts.uk.at.view.kaf020.shr.viewmodel.Content;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    @component({
        name: 'kaf020-c',
        template: `/nts.uk.at.web/view/kaf/020/c/index.html`,
        })

    export class Kaf020CViewModel extends ko.ViewModel {
        appType: KnockoutObservable<number> = ko.observable(AppType.OPTIONAL_ITEM_APPLICATION);
        dataFetch: KnockoutObservable<DetailSreenInfo> = ko.observable({
            applicationContents: ko.observableArray([]),
            code: ko.observable(''),
            name: ko.observable(''),
            appDispInfoStartupOutput: ko.observable(null)
        });
        isSendMail: KnockoutObservable<Boolean>;
        application: KnockoutObservable<Application>;
        printContent: any;
        approvalReason: KnockoutObservable<string>;
        appDispInfoStartupOutput: any;

        created(params: {
            appType: any,
            application: any,
            printContentOfEachAppDto: PrintContentOfEachAppDto,
            approvalReason: any,
            appDispInfoStartupOutput: any,
            eventUpdate: (evt: () => void) => void,
            eventReload: (evt: () => void) => void
        }) {
            const vm = this;
            vm.application = params.application;
            vm.appType = params.appType;
            vm.printContent = params.printContentOfEachAppDto;
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            vm.createParamKAF020();
            vm.approvalReason = params.approvalReason;
            params.eventUpdate(vm.update.bind(vm));
            params.eventReload(vm.reload.bind(vm));
            vm.isSendMail = ko.observable(true);
        }

        // 起動する
        createParamKAF020() {
            let vm = this;
            vm.$blockui('show');
            vm.$ajax(PATH_API.getDetail, {
                companyId: vm.$user.companyId,
                applicationId: vm.application().appID()
            }).done((applicationDto: any) => {
                if (applicationDto) {
                    let code = applicationDto.application.code;
                    let name = applicationDto.application.name;
                    let contents: Array<OptionalItemApplicationContent> = [];
                    applicationDto.optionalItems.forEach((optionalItem: any) => {
                        let item: any = _.find(applicationDto.application.optionalItems, {itemNo: optionalItem.optionalItemNo});
                        // let controlOfAttendanceItem: any = _.find(applicationDto.controlOfAttendanceItems, {itemDailyID: optionalItem.optionalItemNo + 640});
                        contents.push({
                            optionalItemName: optionalItem.optionalItemName,
                            optionalItemNo: optionalItem.optionalItemNo,
                            optionalItemAtr: optionalItem.optionalItemAtr,
                            unit: optionalItem.unit,
                            inputCheckbox: optionalItem.inputCheck,
                            inputUnitOfItem: vm.getInputUnit(optionalItem.optionalItemAtr, optionalItem.calcResultRange),
                            description: optionalItem.description,
                            timeUpper: optionalItem.calcResultRange.timeUpper != null ? nts.uk.time.format.byId("Time_Short_HM", optionalItem.calcResultRange.timeUpper) : null,
                            timeLower: optionalItem.calcResultRange.timeLower != null ? nts.uk.time.format.byId("Time_Short_HM", optionalItem.calcResultRange.timeLower) : null,
                            amountLower: optionalItem.calcResultRange.amountLower,
                            amountUpper: optionalItem.calcResultRange.amountUpper,
                            numberLower: optionalItem.calcResultRange.numberLower,
                            numberUpper: optionalItem.calcResultRange.numberUpper,
                            upperCheck: optionalItem.calcResultRange.upperCheck,
                            lowerCheck: optionalItem.calcResultRange.lowerCheck,
                            time: ko.observable(item ? item.time : null),
                            times: ko.observable(item && !optionalItem.inputCheck ? item.times : null),
                            amount: ko.observable(item ? item.amount : null),
                            timesChecked: ko.observable(item && optionalItem.inputCheck ? (item.times != 0 && item.times != null) : null),
                            detail: '',
                            dispOrder: optionalItem.dispOrder
                        });
                    });
                    vm.dataFetch({
                        applicationContents: ko.observableArray(_.sortBy(contents, ["dispOrder"])),
                        code: code,
                        name: name,
                        appDispInfoStartupOutput: ko.observable(vm.appDispInfoStartupOutput())
                    });
                    vm.printContent.opOptionalItemOutput = ko.toJS({
                        code: vm.dataFetch().code,
                        optionalItems: vm.dataFetch().applicationContents,
                        name: vm.dataFetch().name,
                    });
                }
            }).always(() => {
                vm.$blockui("hide");
            });
        }

        getInputUnit(optionalItemAtr: number, calcResultRange: any): string  {
            const vm = this;
            if (optionalItemAtr == 0) {
                switch (calcResultRange.timeInputUnit) {
                    case 0: return vm.$i18n("KMK002_141");
                    case 1: return vm.$i18n("KMK002_142");
                    case 2: return vm.$i18n("KMK002_143");
                    case 3: return vm.$i18n("KMK002_144");
                    case 4: return vm.$i18n("KMK002_145");
                    case 5: return vm.$i18n("KMK002_146");
                    default: return null;
                }
            }
            if (optionalItemAtr == 1) {
                switch (calcResultRange.numberInputUnit) {
                    case 0: return vm.$i18n("KMK002_150");
                    case 1: return vm.$i18n("KMK002_151");
                    case 2: return vm.$i18n("KMK002_152");
                    case 3: return vm.$i18n("KMK002_153");
                    default: return null;
                }
            }
            if (optionalItemAtr == 2) {
                switch (calcResultRange.amountInputUnit) {
                    case 0: return vm.$i18n("KMK002_160");
                    case 1: return vm.$i18n("KMK002_161");
                    case 2: return vm.$i18n("KMK002_162");
                    case 3: return vm.$i18n("KMK002_163");
                    case 4: return vm.$i18n("KMK002_164");
                    default: return null;
                }
            }
            return null;
        }

        reload() {
            const vm = this;
            if (vm.appType() === AppType.OPTIONAL_ITEM_APPLICATION) {
                vm.createParamKAF020();
            }
        }

        update() {
            const vm = this;
            let application = vm.appDispInfoStartupOutput().appDetailScreenInfo.application;
            application.opAppReason = ko.toJS(vm.application().opAppReason);
            application.opAppStandardReasonCD = ko.toJS(vm.application().opAppStandardReasonCD);
            application.opReversionReason = ko.toJS(vm.application().opReversionReason);
            let dataFetch = ko.toJS(vm.dataFetch);
            let optionalItems: Array<any> = [];
            dataFetch.applicationContents.forEach((item: OptionalItemApplicationContent) => {
                optionalItems.push({
                    itemNo: item.optionalItemNo,
                    times: item.inputCheckbox ? (item.timesChecked ? 1 : null) : item.times,
                    amount: item.amount,
                    time: item.time
                });
            });
            let command = {
                application: application,
                appDispInfoStartup: ko.toJS(vm.appDispInfoStartupOutput()),
                optItemAppCommand: {
                    code: dataFetch.code,
                    optionalItems
                },
            };
            vm.$blockui("show");

            return vm.$validate('.nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason')
                .then((valid: boolean) => {
                    if (valid) {
                        return vm.$ajax(PATH_API.update, command);
                    }
                }).done(res => {
                    if (res) {
                        vm.printContent.opOptionalItemOutput = dataFetch.opOptionalItemOutput;
                        vm.$dialog.info({messageId: "Msg_15"}).then(() => {
							CommonProcess.handleMailResult(res, vm);
						});
                    }
                }).fail(err => {
                    if (err.errors) {
                        err.errors.forEach((error: any) => {
                            let id = '#' + error.parameterIds[1];
                            vm.$errors({
                                [id]: error
                            });
                        });
                    } else if (err.messageId == "Msg_236" || err.messageId == "Msg_324" || err.messageId == "Msg_237" || err.messageId == "Msg_238") {
                        vm.$dialog.error(err);
                    }
                }).always(() => {
                    vm.$blockui("hide");
                });
        }

    }

    interface DetailSreenInfo {
        applicationContents: KnockoutObservableArray<OptionalItemApplicationContent>,
        code: KnockoutObservable<string>,
        name: KnockoutObservable<string>,
        appDispInfoStartupOutput: KnockoutObservable<any>
    }

    const PATH_API = {
        getDetail: 'ctx/at/request/application/optionalitem/getDetail',
        updateOptionalItem: 'ctx/at/request/application/optionalitem/checkBeforeRegister',
        update: 'ctx/at/request/application/optionalitem/update',
    }
}