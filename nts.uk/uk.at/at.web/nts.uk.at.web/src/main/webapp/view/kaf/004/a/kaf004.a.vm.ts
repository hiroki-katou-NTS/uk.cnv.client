/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf004_ref.a.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
    import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
    import WorkManagement = nts.uk.at.view.kaf004_ref.shr.common.viewmodel.WorkManagement;
    import ApplicationDto = nts.uk.at.view.kaf004_ref.shr.common.viewmodel.ApplicationDto;
    import LateOrEarlyInfo = nts.uk.at.view.kaf004_ref.shr.common.viewmodel.LateOrEarlyInfo;
    import ArrivedLateLeaveEarlyInfo = nts.uk.at.view.kaf004_ref.shr.common.viewmodel.ArrivedLateLeaveEarlyInfo;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;

    @bean()
    class KAF004AViewModel extends Kaf000AViewModel {
        appType: KnockoutObservable<number> = ko.observable(AppType.EARLY_LEAVE_CANCEL_APPLICATION);
		isAgentMode : KnockoutObservable<boolean> = ko.observable(false);
        application: KnockoutObservable<Application>;
        workManagement: WorkManagement;
        workManagementTemp: WorkManagement;
        arrivedLateLeaveEarlyInfo: any;
        appDispInfoStartupOutput: any;
        managementMultipleWorkCycles: KnockoutObservable<Boolean>;
        isSendMail: KnockoutObservable<boolean>;
        cancalAppDispSet: KnockoutObservable<boolean> = ko.observable(true);
        delete1: KnockoutObservable<boolean> = ko.observable(false);
        delete2: KnockoutObservable<boolean> = ko.observable(false);
        delete3: KnockoutObservable<boolean> = ko.observable(false);
        delete4: KnockoutObservable<boolean> = ko.observable(false);
        cancelAtr: KnockoutObservable<number>;
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
				dateLst: Array<string> = [];
            vm.application = ko.observable(new Application(vm.appType()));
            vm.workManagement = new WorkManagement('--:--', '--:--', '--:--', '--:--', null, null, null, null);
            vm.workManagementTemp = new WorkManagement('--:--', '--:--', '--:--', '--:--', null, null, null, null);
            vm.arrivedLateLeaveEarlyInfo = ko.observable(ArrivedLateLeaveEarlyInfo.initArrivedLateLeaveEarlyInfo());
            vm.appDispInfoStartupOutput = ko.observable(CommonProcess.initCommonSetting());

            vm.managementMultipleWorkCycles = ko.observable(false);
            vm.isSendMail = ko.observable(false);

			let screenCode: number = null;
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
            vm.$blockui('show');
            let dates: string[] = [];
            if (ko.toJS(vm.application().appDate)) {
                dates.push(ko.toJS(vm.application().appDate));
            }
			let paramKAF000 = {
				empLst, 
				dateLst, 
				appType: vm.appType(),
				screenCode
			};
            vm.loadData(paramKAF000)
                .then((loadDataFlag: any) => {
                    if (loadDataFlag) {
                        vm.application().employeeIDLst(empLst);
                        let appType = vm.appType,
                            appDates = dates,
                            appDispInfoStartupDto = ko.toJS(vm.appDispInfoStartupOutput),
                            command = { appType, appDates, appDispInfoStartupDto };
                        return vm.$ajax(API.initPage, command);
                    }
                }).then((successData: any) => {
                    if (successData) {
                        vm.cancalAppDispSet(successData.lateEarlyCancelAppSet.cancelAtr !== 0);
                        vm.cancelAtr = ko.observable(successData.lateEarlyCancelAppSet.cancelAtr);

                        if (this.cancelAtr() == 2) {
                            vm.delete1(true);
                            vm.delete2(true);
                            vm.delete3(true);
                            vm.delete4(true);
                        }

                        vm.arrivedLateLeaveEarlyInfo(successData);
                        vm.appDispInfoStartupOutput(successData.appDispInfoStartupOutput);

                        if (!vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput
                            || !vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput
                            || !vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.actualContentDisplay
                            || !vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.actualContentDisplay.achievementDetail
                            || !vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.actualContentDisplay.achievementDetail.achievementEarly) {
                            vm.workManagement.scheAttendanceTime("--:--");
                            vm.workManagement.scheAttendanceTime2("--:--");
                            vm.workManagement.scheWorkTime("--:--");
                            vm.workManagement.scheWorkTime2("--:--");
                        } else {
                            vm.workManagement.scheAttendanceTime(vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.achievementDetail.achievementEarly.scheAttendanceTime1);
                            vm.workManagement.scheAttendanceTime2(vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.actualContentDisplay.achievementDetail.achievementEarly.scheAttendanceTime2);
                            vm.workManagement.scheWorkTime(vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.actualContentDisplay.achievementDetail.achievementEarly.scheDepartureTime1);
                            vm.workManagement.scheWorkTime2(vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.actualContentDisplay.achievementDetail.achievementEarly.scheDepartureTime2);

                            vm.workManagement.workTime = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.startTime1;
                            vm.workManagement.workTime2 = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.startTime2;
                            vm.workManagement.leaveTime = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.endTime1;
                            vm.workManagement.leaveTime2 = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.endTime2;

                            vm.workManagementTemp.workTime = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.startTime1;
                            vm.workManagementTemp.workTime2 = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.startTime2;
                            vm.workManagementTemp.leaveTime = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.endTime1;
                            vm.workManagementTemp.leaveTime2 = vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput.opAchievementOutputLst.endTime2;
                        }

                        if (!vm.workManagement.scheAttendanceTime) {
                            vm.workManagement.scheAttendanceTime("--:--");
                        }
                        if (!vm.workManagement.scheAttendanceTime2) {
                            vm.workManagement.scheAttendanceTime2("--:--");
                        }
                        if (!vm.workManagement.scheWorkTime) {
                            vm.workManagement.scheWorkTime("--:--");
                        }
                        if (!vm.workManagement.scheWorkTime2) {
                            vm.workManagement.scheWorkTime2("--:--");
                        }
                    }
					if (!_.isEmpty(params)) {
						if (!_.isEmpty(params.baseDate)) {
							vm.application().appDate.valueHasMutated();
						}
					}
                }).fail((failData: any) => {
                    console.log(failData);
                    if (failData.messageId === "Msg_43") {
						vm.$dialog.error(failData).then(() => { vm.$jump("com", "/view/ccg/008/a/index.xhtml"); });

					} else {
						vm.$dialog.error(failData);
					}
                }).always(() => vm.$blockui("hide"));

            vm.application().appDate.subscribe(() => {
                vm.$blockui("show");

                let command = {
                    appType: vm.application().appType,
                    appDates: [ko.toJS(vm.application().appDate)],
                    baseDate: vm.application().appDate(),
                    appDispNoDate: vm.appDispInfoStartupOutput().appDispInfoNoDateOutput,
                    appDispWithDate: vm.appDispInfoStartupOutput().appDispInfoWithDateOutput,
                    setting: vm.arrivedLateLeaveEarlyInfo().lateEarlyCancelAppSet
                }
                vm.$ajax(API.changeAppDate, command).done((success: any) => {
                    console.log(success);

                    if (success.errorInfo) {
                        if (vm.application().prePostAtr() === 1) {
                            const message: any = {
                                messageId: success.errorInfo,
                                messageParams: [ko.toJS(vm.application().appDate)]
                            };
                            vm.$errors("#kaf000-a-component4-singleDate", message);
                        }
                        vm.workManagement.scheWorkTime("--:--");
                        vm.workManagement.scheWorkTime2("--:--");
                        vm.workManagement.scheAttendanceTime("--:--");
                        vm.workManagement.scheAttendanceTime2("--:--");
                        vm.workManagement.workTime(null);
                        vm.workManagement.workTime2(null);
                        vm.workManagement.leaveTime(null);
                        vm.workManagement.leaveTime2(null);

                        vm.workManagementTemp.workTime(null);
                        vm.workManagementTemp.leaveTime(null);
                        vm.workManagementTemp.workTime2(null);
                        vm.workManagementTemp.leaveTime2(null);

                        vm.arrivedLateLeaveEarlyInfo().info = success.errorInfo;
                    } else {
                        vm.arrivedLateLeaveEarlyInfo().info = null;

                        vm.appDispInfoStartupOutput().appDispInfoWithDateOutput = success.appDispInfoWithDateOutput;

                        success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheAttendanceTime1 == null ? vm.workManagement.scheAttendanceTime('--:--') :
                        vm.workManagement.scheAttendanceTime(nts.uk.time.format.byId("Clock_Short_HM", success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheAttendanceTime1));
                        success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheDepartureTime1 == null ? vm.workManagement.scheWorkTime('--:--') :
                        vm.workManagement.scheWorkTime(nts.uk.time.format.byId("Clock_Short_HM", success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheDepartureTime1));
                        success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheAttendanceTime2 == null ? vm.workManagement.scheAttendanceTime2('--:--') :
                        vm.workManagement.scheAttendanceTime2(nts.uk.time.format.byId("Clock_Short_HM", success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheAttendanceTime2));
                        success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheDepartureTime2 == null ? vm.workManagement.scheWorkTime2('--:--') :
                        vm.workManagement.scheWorkTime2(nts.uk.time.format.byId("Clock_Short_HM", success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheDepartureTime2));

                        vm.workManagement.workTime(null);
                        vm.workManagement.leaveTime(null);
                        vm.workManagement.workTime2(null);
                        vm.workManagement.leaveTime2(null);

                        if(vm.application().prePostAtr() == 1) {
                            if (success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.trackRecordAtr === 0) {
                                vm.workManagement.workTime(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opWorkTime);
                                vm.workManagement.leaveTime(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opLeaveTime);
                                vm.workManagement.workTime2(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opWorkTime2);
                                vm.workManagement.leaveTime2(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opDepartureTime2);
                            }
                        }

                        if (success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.trackRecordAtr === 0) {
                            vm.workManagementTemp.workTime(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opWorkTime);
                            vm.workManagementTemp.leaveTime(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opLeaveTime);
                            vm.workManagementTemp.workTime2(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opWorkTime2);
                            vm.workManagementTemp.leaveTime2(success.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opDepartureTime2);
                        }

                        vm.arrivedLateLeaveEarlyInfo().appDispInfoStartupOutput.appDispInfoWithDateOutput = success.appDispInfoWithDateOutput;
                    }

                }).fail((error: any) => {
                    console.log(error);
                    const message: any = {
                        messageId: error.messageId,
                        messageParams: [ko.toJS(vm.application().appDate)]
                    };
                    vm.arrivedLateLeaveEarlyInfo().info = error.messageId;
                    vm.$dialog.error(message);
                }).always(() => vm.$blockui("hide"));
            });

        }

        mounted() {
            const vm = this;

            vm.application().prePostAtr.subscribe(() => {
                if (vm.application().prePostAtr() === 0) {
                    vm.workManagement.workTime(null);
                    vm.workManagement.leaveTime(null);
                    vm.workManagement.workTime2(null);
                    vm.workManagement.leaveTime2(null);

                    vm.$errors("clear", ["#kaf000-a-component4-singleDate"]);
                } else {
                    vm.workManagement.workTime(vm.workManagementTemp.workTime());
                    vm.workManagement.leaveTime(vm.workManagementTemp.leaveTime());
                    vm.workManagement.workTime2(vm.workManagementTemp.workTime2());
                    vm.workManagement.leaveTime2(vm.workManagementTemp.leaveTime2());

                    if (vm.arrivedLateLeaveEarlyInfo().info && vm.arrivedLateLeaveEarlyInfo().info === "Msg_1707") {
                        const message: any = {
                            messageId: vm.arrivedLateLeaveEarlyInfo().info,
                            messageParams: [ko.toJS(vm.application().appDate)]
                        };
                        vm.$errors("#kaf000-a-component4-singleDate", message);
                    }
                }
            });
        }

        register() {
            const vm = this;

            if (vm.application().prePostAtr() === 1 && vm.arrivedLateLeaveEarlyInfo().info && vm.arrivedLateLeaveEarlyInfo().info === "Msg_1707") {
                const message: any = {
                    messageId: vm.arrivedLateLeaveEarlyInfo().info,
                    messageParams: [ko.toJS(vm.application().appDate)]
                };
                vm.$errors("#kaf000-a-component4-singleDate", message);

                return;
            }
            vm.$validate([
                '.ntsControl',
                '.nts-input'
            ]).then((valid: boolean) => {
                if (valid) {

                    vm.application.prototype.inputDate = ko.observable(moment(new Date()).format("yyyy/MM/dd HH:mm:ss"));

                    let lateCancelation = [];
                    let lateOrLeaveEarlies = [];

                    if (ko.toJS(vm.workManagement.workTime) != null && ko.toJS(vm.workManagement.workTime) != "") {
                        lateOrLeaveEarlies.push({
                            workNo: 1,
                            lateOrEarlyClassification: 0,
                            timeWithDayAttr: ko.toJS(vm.workManagement.workTime())
                        })
                    }
                    if (ko.toJS(vm.workManagement.leaveTime) != null && ko.toJS(vm.workManagement.leaveTime) != "") {
                        lateOrLeaveEarlies.push({
                            workNo: 1,
                            lateOrEarlyClassification: 1,
                            timeWithDayAttr: ko.toJS(vm.workManagement.leaveTime())
                        })
                    }
                    if (ko.toJS(vm.workManagement.workTime2) != null && ko.toJS(vm.workManagement.workTime2) != "" && vm.condition2()) {
                        lateOrLeaveEarlies.push({
                            workNo: 2,
                            lateOrEarlyClassification: 0,
                            timeWithDayAttr: ko.toJS(vm.workManagement.workTime2())
                        })
                    }
                    if (ko.toJS(vm.workManagement.leaveTime2) != null && ko.toJS(vm.workManagement.leaveTime2) != "" && vm.condition2()) {
                        lateOrLeaveEarlies.push({
                            workNo: 2,
                            lateOrEarlyClassification: 1,
                            timeWithDayAttr: ko.toJS(vm.workManagement.leaveTime2())
                        })
                    }
                    if (ko.toJS(vm.application().prePostAtr) === 1) {
                        if (vm.delete1()) {
                            lateCancelation.push({
                                workNo: 1,
                                lateOrEarlyClassification: 0
                            }),
                                _.remove(lateOrLeaveEarlies, (x) => {
                                    return (x.workNo === 1 && x.lateOrEarlyClassification === 0);
                                });
                        }
                        if (vm.delete2()) {
                            lateCancelation.push({
                                workNo: 1,
                                lateOrEarlyClassification: 1
                            })
                            _.remove(lateOrLeaveEarlies, (x) => {
                                return (x.workNo === 1 && x.lateOrEarlyClassification === 1);
                            });
                        }
                        if (vm.delete3() && vm.condition2()) {
                            lateCancelation.push({
                                workNo: 2,
                                lateOrEarlyClassification: 0
                            })
                            _.remove(lateOrLeaveEarlies, (x) => {
                                return (x.workNo === 2 && x.lateOrEarlyClassification === 0);
                            });
                        }
                        if (vm.delete4() && vm.condition2()) {
                            lateCancelation.push({
                                workNo: 2,
                                lateOrEarlyClassification: 1
                            })
                            _.remove(lateOrLeaveEarlies, (x) => {
                                return (x.workNo === 2 && x.lateOrEarlyClassification === 1);
                            });
                        }
                    }
                    let arrivedLateLeaveEarly = {
                        lateCancelation: lateCancelation,
                        lateOrLeaveEarlies: lateOrLeaveEarlies
                    }

                    vm.arrivedLateLeaveEarlyInfo().arrivedLateLeaveEarly = arrivedLateLeaveEarly;

                    let application: ApplicationDto = new ApplicationDto(null, null, ko.toJS(vm.application().prePostAtr), vm.application().employeeIDLst()[0],
                        ko.toJS(vm.application().appType), ko.toJS(vm.application().appDate), null, null, null, null, ko.toJS(vm.application().opReversionReason), ko.toJS(vm.application().appDate), ko.toJS(vm.application().appDate), ko.toJS(vm.application().opAppReason), ko.toJS(vm.application().opAppStandardReasonCD));
                    let command = {
                        agentAtr: true,
                        isNew: true,
                        application: application,
                        infoOutput: ko.toJS(vm.arrivedLateLeaveEarlyInfo)
                    };

                    vm.$blockui("show");
                    vm.$validate("#kaf000-a-component4-singleDate", ".nts-input", "#kaf000-a-component3-prePost")
                        .then(isValid => {
                            if (isValid) {
                                return true;
                            }
                        }).then(result => {
                            if (result) {
                                vm.$ajax(API.getMsgList + "/" + ko.toJS(vm.application().appType), command
                                ).done((success: any) => {
                                    if (success) {
                                        console.log(success);

                                        vm.showConfirmResult(success, vm);

                                        this.afterRegister(application);
                                    } else {
                                        this.afterRegister(application);
                                    }
                                }).fail((fail: any) => {
                                    console.log(fail);
                                    if (fail) {
                                        const message: any = {
                                            messageId: fail.messageId,
                                            messageParams: [ko.toJS(vm.application().appDate)]
                                        };
                                        vm.$dialog.error(message);
                                    }
                                })
                            }
                        }).always(() => vm.$blockui("hide"));
                }
            });


        }

        private afterRegister(params?: any) {
            const vm = this;

            vm.arrivedLateLeaveEarlyInfo().earlyInfos = [];

            vm.$blockui("show");
            vm.$ajax(API.register,
                {
                    appType: ko.toJS(vm.application().appType),
                    application: params,
                    infoOutput: ko.toJS(vm.arrivedLateLeaveEarlyInfo)
                }).then((success: any) => {
                    if (success) {
                        vm.$dialog.info({ messageId: "Msg_15" }).then(() => {
							nts.uk.request.ajax("at", API.reflectApp, success.reflectAppIdLst);
							CommonProcess.handleAfterRegister(success, vm.isSendMail(), vm, false, vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
                        });
                    }
                }).fail((fail: any) => {
                    console.log(fail);
                    const message: any = {
                        messageId: fail.messageId,
                        messageParams: [ko.toJS(vm.application().appDate)]
                    };
                    vm.$dialog.error(message);

                    return;
                }).always(() => {
                    vm.$blockui("hide");
                })
        }

        // ※2
        public condition2(): boolean {
            const vm = this;

            vm.managementMultipleWorkCycles(vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.managementMultipleWorkCycles);

            // 「遅刻早退取消申請起動時の表示情報」.申請表示情報.申請設定（基準日関係なし）.複数回勤務の管理＝true
            return ko.toJS(vm.managementMultipleWorkCycles());
        }

        // ※8
        public condition8() {
            // 事前事後区分に「事後」に選択している場合　（事後モード）
            return this.application().prePostAtr() === 1 && this.cancalAppDispSet();
        }

        public showConfirmResult(messages: Array<any>, vm: any) {

				if(_.isEmpty(messages)) {
					return $.Deferred().resolve(true);
				}
				let msg = messages[0].value,
					type = messages[0].type;
				return vm.$dialog.confirm(msg).then((result: 'no' | 'yes' | 'cancel') => {
					if (result === 'yes') {
		            	return $.Deferred().resolve(vm.showConfirmResult(_.slice(messages, 1), vm));
		            }
	        	});
		}
    }

    const API = {
        initPage: "at/request/application/lateorleaveearly/initPage",
        changeAppDate: "at/request/application/lateorleaveearly/changeAppDate",
        getMsgList: "at/request/application/lateorleaveearly/getMsgList",
        register: "at/request/application/lateorleaveearly/register",
		reflectApp: "at/request/application/reflect-app"
    };

    export class IdItem {
        public static readonly A6_7: number = 1;
        public static readonly A6_13: number = 2;
        public static readonly A6_19: number = 3;
        public static readonly A6_25: number = 4
    }
}
