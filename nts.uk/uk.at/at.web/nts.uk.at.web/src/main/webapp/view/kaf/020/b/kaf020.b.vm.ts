module nts.uk.at.view.kaf020.b {
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import OptionalItemApplicationContent = nts.uk.at.view.kaf020.shr.viewmodel.Content;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    const PATH_API = {
        register: 'ctx/at/request/application/optionalitem/register',
        getControlAttendance: 'ctx/at/request/application/optionalitem/getControlAttendance',
        listOptionalItem: 'ctx/at/record/optionalitem/findByListItemNo',
		reflectApp: "at/request/application/reflect-app"
    }

    @bean()
    export class Kaf020BViewModel extends Kaf000AViewModel {
        appType: KnockoutObservable<number> = ko.observable(AppType.OPTIONAL_ITEM_APPLICATION);
        isSendMail: KnockoutObservable<boolean> = ko.observable(false);
        application: KnockoutObservable<Application> = ko.observable(new Application(this.appType()));
        isAgentMode: KnockoutObservable<boolean> = ko.observable(false);
        code: string;
        dataFetch: KnockoutObservable<DetailSreenInfo> = ko.observable({
            applicationContents: ko.observableArray([]),
            name: "",
            appDispInfoStartupOutput: ko.observable(null)
        });
        allOptional: any = [];
		isFromOther: boolean = false;
		empLst: Array<string> = [];
        dateLst: Array<string> = [];
        baseDate: string;

        constructor(props: any) {
            super();
        }
        

        created(params: any) {
            const vm = this;
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					params = __viewContext.transferred.value;
				}
			}
            if (params && params.isAgentMode) vm.isAgentMode(params.isAgentMode);
            if (params != undefined && params.optionalItem) {
                nts.uk.characteristics.save("KAF020InitParams", params.optionalItem);
                vm.code = params.optionalItem.code;

            } else {
                nts.uk.characteristics.restore("KAF020InitParams").then((cacheParams: any) => {
                    if (cacheParams != undefined) {
                        vm.code = cacheParams.code;
                    }
                })
            }
            if (params && params.empLst) vm.empLst = params.empLst;
            if (params && params.baseDate) {
                vm.baseDate = params.baseDate;
                let paramDate = moment(params.baseDate).format('YYYY/MM/DD');
                vm.dateLst = [paramDate];
                vm.application().appDate(paramDate);
                vm.application().opAppStartDate(paramDate);
                vm.application().opAppEndDate(paramDate);
            }
            vm.$blockui("show");
            vm.loadData(vm.empLst, vm.dateLst, vm.appType()).then((loadFlag) => {
                if (loadFlag) {
                    if (params != undefined && params.optionalItem) {
                        return vm.fetchData(params.optionalItem);
                    } else {
                        nts.uk.characteristics.restore("KAF020InitParams").then((cacheParams: any) => {
                            return vm.fetchData(cacheParams);
                        });
                    }
                }
            }).then((response: any) => {

            }).always(() => {
                vm.$blockui("hide");
            });
        }

        fetchData(params: any) {
            const vm = this;
            let itemNoList = params.settingItems.map((item: any) => item.no);
            $.when(vm.$ajax(PATH_API.getControlAttendance, {optionalItemNos: itemNoList}), vm.$ajax(PATH_API.listOptionalItem, {optionalItemNos: itemNoList})).done((controlAttendance: any, optionalItems: any) => {
                let contents: Array<OptionalItemApplicationContent> = [];
                params.settingItems.forEach((opItem: any) => {
                    let optionalItem: OptionalItem = _.find(optionalItems, {optionalItemNo: opItem.no});
                    let controlOfAttendanceItem: any = _.find(controlAttendance, {itemDailyID: opItem.no + 640});
                    if (optionalItem) {
                        contents.push({
                            optionalItemName: optionalItem.optionalItemName,
                            optionalItemNo: optionalItem.optionalItemNo,
                            optionalItemAtr: optionalItem.optionalItemAtr,
                            unit: optionalItem.unit,
                            inputUnitOfTimeItem: controlOfAttendanceItem ? controlOfAttendanceItem.inputUnitOfTimeItem : null,
                            description: optionalItem.description,
                            timeUpper: optionalItem.calcResultRange.timeRange.dailyTimeRange.upperLimit != null ? nts.uk.time.format.byId("Time_Short_HM", optionalItem.calcResultRange.timeRange.dailyTimeRange.upperLimit) : null,
                            timeLower: optionalItem.calcResultRange.timeRange.dailyTimeRange.lowerLimit != null ? nts.uk.time.format.byId("Time_Short_HM", optionalItem.calcResultRange.timeRange.dailyTimeRange.lowerLimit) : null,
                            amountLower: optionalItem.calcResultRange.amountRange.dailyAmountRange.lowerLimit,
                            amountUpper: optionalItem.calcResultRange.amountRange.dailyAmountRange.upperLimit,
                            numberLower: optionalItem.calcResultRange.numberRange.dailyNumberRange.lowerLimit,
                            numberUpper: optionalItem.calcResultRange.numberRange.dailyNumberRange.upperLimit,
                            upperCheck: optionalItem.calcResultRange.upperCheck,
                            lowerCheck: optionalItem.calcResultRange.lowerCheck,
                            time: ko.observable(''),
                            times: ko.observable(),
                            amount: ko.observable(),
                            detail: '',
                            dispOrder: opItem.dispOrder
                        });
                    }
                });
                vm.dataFetch({applicationContents: ko.observableArray(contents), name: params.name, appDispInfoStartupOutput: ko.observable(vm.appDispInfoStartupOutput())});
            }).then(() => {
                vm.focusDate();
            }).always(() => {
                vm.$blockui("hide");
            });
        }

        focusDate() {
            const vm = this;
            let dateItem = $(vm.$el).find('#kaf000-a-component4-rangeDate');
            if (dateItem.length) {
                dateItem.focus();
            } else {
                $(vm.$el).find('#kaf000-a-component4-singleDate').focus();
            }
        }

        goBack() {
            const vm = this;
            vm.$jump('../a/index.xhtml', {
                fromB: true,
                employeeIds: vm.empLst,
                baseDate: vm.baseDate,
                isAgentMode: vm.isAgentMode()
            });
        }

        register() {
            const vm = this;
            let optionalItems = new Array();
            let applicationDto = ko.toJS(vm.application());
            applicationDto.employeeID = vm.application().employeeIDLst()[0];
            vm.dataFetch().applicationContents().forEach((item: OptionalItemApplicationContent) => {
                optionalItems.push({
                    itemNo: item.optionalItemNo,
                    times: item.times(),
                    amount: item.amount(),
                    time: item.time()
                });
            });
            let command = {
                application: applicationDto,
                appDispInfoStartup: vm.appDispInfoStartupOutput(),
                optItemAppCommand: {
                    code: vm.code,
                    optionalItems
                }
            }
            vm.$validate([
                '.ntsControl',
                '.nts-input'
            ]).then((valid: boolean) => {
                if (valid) {
                    vm.$ajax(PATH_API.register, command).done(result => {
                        if (result != undefined) {
                            vm.$dialog.info({messageId: "Msg_15"}).then(() => {
                                nts.uk.request.ajax("at", PATH_API.reflectApp, result.reflectAppIdLst);
								CommonProcess.handleAfterRegister(result, vm.isSendMail(), vm, false, vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
							});
                            // let contents: Array<OptionalItemApplicationContent> = [];
                            // vm.dataFetch().applicationContents().forEach(item => {
                            //     contents.push({
                            //         ...item,
                            //         time: ko.observable(''),
                            //         times: ko.observable(),
                            //         amount: ko.observable(),
                            //     })
                            // })
                            // vm.dataFetch({applicationContents: ko.observableArray(contents), name: vm.dataFetch().name});
                        }
                    }).fail(err => {
                        if (err && _.isArray(err.errors)) {
                            const errors: any = {};
                            err.errors.forEach((e: any) => {
                                let id = '#' + e.parameterIds[1];
                                errors[id] = e;
                            });
                            vm.$errors(errors);
                        } else {
                            vm.$dialog.error(err);
                        }
                    });
                }
            });
        }

    }

    interface OptionalItemData {
        optionalItem: OptionalItem,
        controlOfAttendanceItems: {
            inputUnitOfTimeItem: number,
        },
    }

    interface OptionalItem {
        itemNo: number,
        calcResultRange: {
            timeRange: {
                dailyTimeRange: {
                    upperLimit: number,
                    lowerLimit: number,
                },
            },
            numberRange: {
                dailyNumberRange: {
                    upperLimit: number,
                    lowerLimit: number,
                },
            },
            amountRange: {
                dailyAmountRange: {
                    upperLimit: number,
                    lowerLimit: number,
                },
            },
            upperCheck: boolean,
            lowerCheck: boolean,
        },
        optionalItemName: string,
        optionalItemNo: number,
        optionalItemAtr: number
        unit: string
        description: string,
    }

    interface DetailSreenInfo {
        applicationContents: KnockoutObservableArray<OptionalItemApplicationContent>,
        name: string,
        appDispInfoStartupOutput: KnockoutObservable<any>
    }
}