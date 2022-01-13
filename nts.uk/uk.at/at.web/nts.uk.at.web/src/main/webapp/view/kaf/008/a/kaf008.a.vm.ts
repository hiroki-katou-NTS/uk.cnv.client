module nts.uk.at.view.kaf008_ref.a.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
    import BusinessTripInfoDetail = nts.uk.at.view.kaf008_ref.shr.viewmodel.BusinessTripInfoDetail;
    import BusinessTripOutput = nts.uk.at.view.kaf008_ref.shr.viewmodel.BusinessTripOutput;
    import BusinessTripContent = nts.uk.at.view.kaf008_ref.shr.viewmodel.BusinessTripContent;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    @bean()
    class Kaf008AViewModel extends Kaf000AViewModel {

        appType: KnockoutObservable<number> = ko.observable(AppType.BUSINESS_TRIP_APPLICATION);
		isAgentMode : KnockoutObservable<boolean> = ko.observable(false);
        application: KnockoutObservable<Application> = ko.observable(new Application(this.appType()));
        mode: number = 1;
        isSendMail: KnockoutObservable<boolean>;
        businessTripOutput: KnockoutObservable<BusinessTripOutput> = ko.observable(null);

        dataFetch: KnockoutObservable<DetailSreenInfo> = ko.observable({
            businessTripContent: {
                departureTime: ko.observable(null),
                returnTime: ko.observable(null),
                tripInfos: []
            },
            businessTripOutput: null
        });

        appDate: KnockoutObservable<any> = ko.observable(null);
		isFromOther: boolean = false;

        created(params: AppInitParam) {
            const vm = this;
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					params = __viewContext.transferred.value;
				}
			}
			let empLst: Array<string> = [],
				dateLst: Array<string> = [],
				screenCode: number = null;
            vm.isSendMail = ko.observable(false);
			if (!_.isEmpty(params)) {
				if (!nts.uk.util.isNullOrUndefined(params.screenCode)) {
					screenCode = params.screenCode;
				}
				if (!_.isEmpty(params.employeeIds)) {
					empLst = params.employeeIds;
				}
				if (!_.isEmpty(params.baseDate)) {
					let paramDate = moment(params.baseDate).format('YYYY/MM/DD');
					dateLst = [paramDate];
					vm.application().appDate(paramDate);
					vm.application().opAppStartDate(paramDate);
                    vm.application().opAppEndDate(paramDate);
				}
				if (params.isAgentMode) {
					vm.isAgentMode(params.isAgentMode);
				}
			}
            // 起動する
			let paramKAF000 = {
				empLst, 
				dateLst, 
				appType: vm.appType(),
				screenCode
			};
            vm.$blockui("show");
            vm.loadData(paramKAF000)
                .then((loadDataFlag: boolean) => {
                    if (loadDataFlag) {
						vm.application().employeeIDLst(empLst);
                        const applicantList = empLst;
                        const appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput);
                        const command = {applicantList, dateLst, appDispInfoStartupOutput};

                        return vm.$ajax(API.startNew, command);
                    }

                    return null;
                }).then((successData: Model | null) => {
                if (successData) {
                    const {result, businessTripInfoOutputDto, confirmMsgOutputs} = successData;

                    if (businessTripInfoOutputDto) {
                        let cloneData = _.clone(vm.dataFetch());
                        cloneData.businessTripOutput = businessTripInfoOutputDto;
                        vm.dataFetch(cloneData);
                        vm.businessTripOutput(businessTripInfoOutputDto)
                    }


					if (!_.isEmpty(params)) {
						if (!_.isEmpty(params.baseDate)) {
							vm.changeAppDate();
						}
					}
                }
            }).fail((err: any) => {
                vm.handleError(err)
            }).then(() =>{
                vm.focusDate();
            }).always(() => vm.$blockui("hide"));
        }

        mounted() {
            const vm = this;

            vm.application.subscribe(app => {
               if (app) {
                   let startDate = app.opAppStartDate();
                   let endDate = app.opAppEndDate();
                   let checkFormat = vm.validateAppDate(startDate, endDate);

                   if (checkFormat) {
                       vm.changeAppDate();
                   }
               }
            });
        }

        validateAppDate(start:string , end: string) {
            let startDate = moment(start);
            let endDate = moment(end);
            if (startDate.isValid() && endDate.isValid()) {
                return true;
            }
            return false;
        }

        // 申請日を変更する
        changeAppDate() {
            const vm = this;

            let applicationDto = ko.toJS(vm.application);
            let businessTripInfoOutputDto = ko.toJS(vm.dataFetch().businessTripOutput);

            businessTripInfoOutputDto.appDispInfoStartup = ko.toJS(vm.appDispInfoStartupOutput);

            let command = {
                businessTripInfoOutputDto, applicationDto
            };

            // vm.$errors("clear");
            if ($('#A10_D4 input').ntsError('hasError')) {
                $('#A10_D4 input').ntsError('clear');
            }

            vm.$validate([
                '#kaf000-a-component4 .nts-input'
            ]).then((valid: boolean) => {
                if (valid) {
                    return vm.$blockui("show").then(() => vm.$ajax(API.changeAppDate, command));
                }
            }).done((res: any) => {
                if (res.result) {
                    let output = res.businessTripInfoOutputDto;

                    vm.dataFetch().businessTripOutput = output;
                    vm.dataFetch.valueHasMutated();
                }
            }).fail(err => {
                vm.dataFetch().businessTripOutput.businessTripActualContent = [];
                vm.dataFetch.valueHasMutated();
                if (_.isEmpty(vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opMsgErrorLst)) {
                    vm.handleError(err);
                }
            }).always(() => vm.$blockui("hide"));
        }

        // 出張申請を新規登録で登録する
        register() {
            const vm = this;
            const dataFetch = ko.toJS(vm.dataFetch);
            const tripOutput = dataFetch.businessTripOutput;
            const tripContent = dataFetch.businessTripContent;

            let mapScreenContent: Array<ScreenContent> = [];
            let lstContent: Array<BusinessTripInfoDetail> = [];
            _.forEach(tripOutput.businessTripActualContent, function (i: any) {
                mapScreenContent.push({
                    date: i.date,
                    workTypeName: i.opAchievementDetail.opWorkTypeName,
                    workTimeName: i.opAchievementDetail.opWorkTimeName,
                });
                lstContent.push({
                        date: i.date,
                        wkTypeCd: i.opAchievementDetail.workTypeCD,
                        wkTimeCd: i.opAchievementDetail.workTimeCD,
                        startWorkTime: i.opAchievementDetail.opWorkTime,
                        endWorkTime: i.opAchievementDetail.opLeaveTime
                });
            });
            let businessTripDto: any = {
                departureTime: tripContent.departureTime,
                returnTime: tripContent.returnTime,
                tripInfos: lstContent
            };

            let applicationDto = ko.toJS(vm.application);
			applicationDto.employeeID = vm.application().employeeIDLst()[0];
            let command = {
                businessTrip: businessTripDto,
                businessTripInfoOutput: tripOutput,
                application: applicationDto,
                screenDetails: mapScreenContent
            };

            vm.$blockui( "show" );
            vm.$validate([
                '.ntsControl',
                '.nts-input'
            ]).then((valid: boolean) => {
                if (valid) {
                    vm.$ajax(API.checkBeforeRegister, command).done((res: any) => {
                        if ( _.isEmpty( res ) ) {
                            vm.registerData(command);
                        } else {
                            vm.handleConfirmMessage(res, command);
                        }
                    }).fail(err => {
                        vm.handleError(err);
                    });
                }
            }).always(() => vm.$blockui("hide"));
        }

        registerData(command: any) {
            const vm = this;
            vm.$blockui("show");
            return vm.$ajax(API.register, command).done( data => {
                if (data) {
                    vm.$dialog.info({messageId: "Msg_15"})
                        .then(() => {
							nts.uk.request.ajax("at", API.reflectApp, data.reflectAppIdLst);
   							CommonProcess.handleAfterRegister(data, vm.isSendMail(), vm, false, vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
                        });
                }
            }).fail(res => {
                vm.handleError(res);
            }).always(() => vm.$blockui("hide"));
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

        public handleConfirmMessage(listMes: any, res: any) {
            let vm = this;
            if (!_.isEmpty(listMes)) {
                let item = listMes.shift();
                vm.$dialog.confirm({ messageId: item.msgID }).then((value) => {
                    if (value == 'yes') {
                        if (_.isEmpty(listMes)) {
                            vm.registerData(res);
                        } else {
                            vm.handleConfirmMessage(listMes, res);
                        }

                    }
                });
            }
        }

        handleError(err: any) {
            const vm = this;

            if (err && err.messageId) {
                // 年月日＋#Msg_ID
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
						if (err.messageId == 'Msg_277') {
                        	vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst = [];
							vm.appDispInfoStartupOutput.valueHasMutated();
                        }
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

    interface Model {
        result: boolean;
        confirmMsgOutputs: Array<MessageOutput>;
        businessTripInfoOutputDto: BusinessTripInfoOutput;
    }

    interface DetailSreenInfo {
        businessTripContent: BusinessTripContent;
        businessTripOutput: BusinessTripOutput;
    }

    // 出張申請設定
    interface Setting {
        cid: string;
        appComment: any;
    }

    export interface BusinessTripInfoOutput {
        setting: Setting; // 出張申請設定
        appDispInfoStartup: any; // 申請表示情報
        holidays: any; // 休日勤務種類リスト
        workdays: any; // 出勤日勤務種類リスト
        businessTripActualContent: any; // 出張の実績内容
        infoBeforeChange: any; // 変更前勤務種類
        infoAfterChange: any; // 変更後勤務種類
    }

    interface MessageOutput {
        msgID: string;
        paramLst: Array<string>
    }

    interface ScreenContent {
        date: string;
        workTypeName: string;
        workTimeName: string;
    }

    const API = {
        startNew: "at/request/application/businesstrip/start",
        checkBeforeRegister: "at/request/application/businesstrip/checkBeforeRegister",
        register: "at/request/application/businesstrip/register",
        changeAppDate: "at/request/application/businesstrip/changeAppDate",
		reflectApp: "at/request/application/reflect-app"
    };

}