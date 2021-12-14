module nts.uk.at.view.kaf008_ref.b.viewmodel {
    //import Kaf000BViewModel = nts.uk.at.view.kaf000.b.viewmodel.Kaf000BViewModel;
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
    import BusinessTripOutput = nts.uk.at.view.kaf008_ref.shr.viewmodel.BusinessTripOutput;
    import BusinessTripContent = nts.uk.at.view.kaf008_ref.shr.viewmodel.BusinessTripContent;
    import Mode = nts.uk.at.view.kaf008_ref.shr.viewmodel.Mode;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    @component({
        name: 'kaf008-b',
        template: `/nts.uk.at.web/view/kaf/008/b/index.html`
    })
    class Kaf008BViewModel extends ko.ViewModel {

        appType: KnockoutObservable<number> = ko.observable(AppType.BUSINESS_TRIP_APPLICATION);
        approvalReason: KnockoutObservable<string>;
        appDispInfoStartupOutput: any;
        application: KnockoutObservable<Application>;
        mode: number = Mode.Edit;
        dataFetch: KnockoutObservable<DetailSreenInfo> = ko.observable({
            businessTripContent: {
                departureTime: ko.observable(null),
                returnTime: ko.observable(null),
                tripInfos: []
            },
            businessTripOutput: null
        });
        printContent: any;
        isSendMail: KnockoutObservable<Boolean>;

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
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            vm.application = params.application;
			vm.appType = params.appType;
            vm.createParamKAF008();
            vm.printContent = params.printContentOfEachAppDto;
            vm.approvalReason = params.approvalReason;
            vm.isSendMail = ko.observable(true);

            // gui event con ra viewmodel cha
            // nhớ dùng bind(vm) để ngữ cảnh lúc thực thi
            // luôn là component
            params.eventUpdate(vm.update.bind(vm));
			params.eventReload(vm.reload.bind(vm));
        }

        mounted() {
            const vm = this;
            vm.appDispInfoStartupOutput.subscribe(value => {
                if (value) {
                    vm.dataFetch().businessTripOutput.appDispInfoStartup = value;
                }
            });

//            vm.application().appID.subscribe(appID => {
//                if(vm.application().appType === AppType.BUSINESS_TRIP_APPLICATION) {
//                    vm.createParamKAF008();
//                }
//            });

        }
		
		reload() {
			const vm = this;
			if(vm.appType() === AppType.BUSINESS_TRIP_APPLICATION) {
				vm.createParamKAF008();	
			}
		}

        // 起動する
        createParamKAF008() {
            let vm = this;
            vm.$blockui('show');
            vm.$ajax(API.getDetail, {
                companyId: vm.$user.companyId,
                applicationId: vm.application().appID()
            }).done(res => {
                if (res) {
                    let businessTripContent = res.businessTripDto;
                    let eachDetail: Array<any> = _.map(businessTripContent.tripInfos, function (detail: any) {
                        const workInfo = res.businessTripInfoOutputDto.infoBeforeChange;
                        const timeInfo = res.businessTripInfoOutputDto.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst;
                        let workName = "";
                        let timeName = "";

                        if (!workName) {
                            let wkDayInfo = _.filter(ko.toJS(workInfo), function (item: any) {
                                return item.date == detail.date;
                            });
                            if (wkDayInfo.length != 0 && wkDayInfo[0].workTypeDto) {
                                workName = wkDayInfo[0].workTypeDto.name;
                            } else {
                                workName = "マスタ未登録";
                            }
                        }

                        if (!timeName) {
                            let wkTimeInfo = _.filter(ko.toJS(timeInfo), function (item: any) {
                                return item.worktimeCode == detail.wkTimeCd;
                            });
                            if (wkTimeInfo.length != 0 && wkTimeInfo[0].workTimeDisplayName) {
                                timeName = wkTimeInfo[0].workTimeDisplayName.workTimeName;
                            } else {
                                timeName = !detail.wkTimeCd ? "" : "マスタ未登録";
                            }
                        }

                        return {
                            date: detail.date,
                            wkTimeCd: detail.wkTimeCd,
                            wkTimeName: timeName,
                            wkTypeCd: detail.wkTypeCd,
                            wkTypeName: workName,
                            startWorkTime: detail.startWorkTime,
                            endWorkTime: detail.endWorkTime
                        };
                    });

                    vm.dataFetch().businessTripContent.departureTime(businessTripContent.departureTime);
                    vm.dataFetch().businessTripContent.returnTime(businessTripContent.returnTime);
                    vm.dataFetch().businessTripContent.tripInfos = eachDetail;
                    vm.dataFetch().businessTripOutput = res.businessTripInfoOutputDto;
                    vm.dataFetch.valueHasMutated();

                    vm.printContent.opBusinessTripInfoOutput = ko.toJS(vm.dataFetch().businessTripContent);
                }
            }).fail(err => {
                vm.$dialog.error({messageId: err.msgId});
            }).always(() => {
                vm.$blockui('hide')
                $('#kaf008-share #A5_3').focus();
            });

        }

        // 出張申請を更新登録で更新する
        // event update cần gọi lại ở button của view cha
        update() {
            const vm = this;

            let dataFetch = ko.toJS(vm.dataFetch);
            let screenContent: any = _.map(dataFetch.businessTripContent.tripInfos, (i: any) => {
                return {
                    date: i.date,
                    workTypeName: i.wkTypeName,
                    workTimeName: i.wkTimeName
                }
            });

            let command = {
                businessTrip: dataFetch.businessTripContent,
                businessTripInfoOutput: dataFetch.businessTripOutput,
                application: ko.toJS(vm.application()),
                screenContent: screenContent
            };

            vm.$blockui("show");

            return vm.$validate('.nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason')
                .then((valid: boolean) => {
                    if (valid) {
                        return vm.$ajax(API.updateBusinessTrip, command)
                    }
                }).done(res => {
                    if (res) {
                        if (res) {
                            vm.printContent.opBusinessTripInfoOutput = dataFetch.businessTripContent;
                            vm.$dialog.info({messageId: "Msg_15"}).then(() => {
								CommonProcess.handleMailResult(res, vm);
							});
                        }
                    }
                }).fail(err => {
                    vm.handleError(err);
                }).always(() => {
                    // vm.$errors("clear");
                    vm.$blockui("hide");
                });
        }

        dispose() {
            const vm = this;

        }

        handleError(err: any) {
            const vm = this;

            if (err && err.messageId) {

                if ( _.includes(["Msg_23","Msg_24","Msg_1913","Msg_457","Msg_1685"], err.messageId)) {
                    err.message = err.parameterIds[0] + err.message;
                }

                switch (err.messageId) {
                    case "Msg_23":
                    case "Msg_24":
                    case "Msg_457": {
                        let id = '#' + err.parameterIds[0].replace(/\//g, "") + '-wkCode';
                        vm.$errors({
                            [id]: err
                        });
                        break;
                    }
                    case "Msg_1715": {
                        let id = '#' + err.parameterIds[1].replace(/\//g, "") + '-wkCode';
                        vm.$errors({
                            [id]: err
                        });
                        break;
                    }
                    case "Msg_1685":
                    case "Msg_1913": {
                        let id = '#' + err.parameterIds[0].replace(/\//g, "") + '-tmCode';
                        vm.$errors({
                            [id]: err
                        });
                        break;
                    }
                    default: {
                        vm.$dialog.error(err).then(() => {
                            if (err.messageId == 'Msg_197') {
                                location.reload();
                            }
                        });
                    }
                }
            }
        }

    }

    interface DetailSreenInfo {
        businessTripContent: BusinessTripContent;
        businessTripOutput: BusinessTripOutput;
    }

    export interface TripInfoDetail {
        date: string;
        wkTimeCd: string;
        wkTimeName: string;
        wkTypeCd: string;
        wkTypeName: string;
        startWorkTime: number;
        endWorkTime: number;
    }

    const API = {
        getDetail: "at/request/application/businesstrip/getDetailPC",
        updateBusinessTrip: "at/request/application/businesstrip/updateBusinessTrip"
    }
}