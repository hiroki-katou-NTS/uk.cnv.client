module nts.uk.at.view.kaf012.a.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;
	import DataModel = nts.uk.at.view.kaf012.shr.viewmodel2.DataModel;
    import AppTimeType = nts.uk.at.view.kaf012.shr.viewmodel2.AppTimeType;
    import GoingOutReason = nts.uk.at.view.kaf012.shr.viewmodel2.GoingOutReason;
    import LeaveType = nts.uk.at.view.kaf012.shr.viewmodel1.LeaveType;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    const API = {
        startNew: "at/request/application/timeLeave/init",
        changeAppDate: "at/request/application/timeLeave/changeAppDate",
        checkRegister: "at/request/application/timeLeave/checkBeforeRegister",
        register: "at/request/application/timeLeave/register",
		reflectApp: "at/request/application/reflect-app"
    };

    @bean()
    class Kaf012AViewModel extends Kaf000AViewModel {
		appType: KnockoutObservable<number> = ko.observable(AppType.ANNUAL_HOLIDAY_APPLICATION);
		isAgentMode : KnockoutObservable<boolean> = ko.observable(false);
        isSendMail: KnockoutObservable<boolean>;
		application: KnockoutObservable<Application>;
        reflectSetting: KnockoutObservable<any> = ko.observable(null);
        timeLeaveManagement: KnockoutObservable<any> = ko.observable(null);
        timeLeaveRemaining: KnockoutObservable<any> = ko.observable(null);
        leaveType: KnockoutObservable<number> = ko.observable(null);

        applyTimeData: KnockoutObservableArray<DataModel>;
        specialLeaveFrame: KnockoutObservable<number>;
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
            vm.isSendMail = ko.observable(false);
            vm.application = ko.observable(new Application(vm.appType()));
			if (!_.isEmpty(params)) {
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
            vm.applyTimeData = ko.observableArray([]);
            for (let i = 0; i < 5; i++) {
                vm.applyTimeData.push(new DataModel(i, vm.reflectSetting, vm.appDispInfoStartupOutput, vm.application));
            }
            vm.specialLeaveFrame = ko.observable(null);
            vm.$blockui("show");
            vm.loadData(empLst, dateLst, vm.appType())
            .then((loadDataFlag: any) => {
                if(loadDataFlag) {
					vm.application().employeeIDLst(empLst);
                    const appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput);
                    return vm.$ajax(API.startNew, {appDispInfoStartupOutput: appDispInfoStartupOutput});
                }
            }).then((res: any) => {
                if(res) {
                    vm.reflectSetting(res.reflectSetting);
                    vm.timeLeaveRemaining(res.timeLeaveRemaining);
                    vm.timeLeaveManagement(res.timeLeaveManagement);
                    if (vm.applyTimeData().filter(i => i.display()).length == 0 && _.isEmpty(vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opMsgErrorLst)) {
                        vm.$dialog.error({messageId: "Msg_474"}).then(() => {
                            nts.uk.request.jumpToTopPage();
                        });
                    }
                }
                if (!_.isEmpty(params) && !_.isEmpty(params.baseDate)) {
                    vm.handleChangeAppDate(params.baseDate);
                }
            }).fail((error: any) => {
                if (_.isEmpty(vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opMsgErrorLst)) {
                    vm.$dialog.error(error).then(() => {
                        if (error.messageId == "Msg_474") {
                            nts.uk.request.jumpToTopPage();
                        }
                    });
                }
            }).always(() => {
                vm.$blockui("hide");
                $(vm.$el).find('#kaf000-a-component4-singleDate').focus();
            });

        }

        mounted() {
            const vm = this;
            vm.application().appDate.subscribe(value => {
                vm.handleChangeAppDate(value);
                vm.applyTimeData().forEach((row : DataModel) => {
                    row.applyTime.forEach(apply => {
                        apply.substituteAppTime(0);
                        apply.annualAppTime(0);
                        apply.careAppTime(0);
                        apply.childCareAppTime(0);
                        apply.super60AppTime(0);
                        apply.specialAppTime(0);
                        apply.calculatedTime(0);
                    });
                });
            });
            vm.appDispInfoStartupOutput.subscribe(value => {
                if (vm.application().prePostAtr() == 1 && value) {
                    vm.updateInputTime(value);
                }
            });
            vm.application().prePostAtr.subscribe(value => {
                if (value == 1 && vm.appDispInfoStartupOutput()) {
                    vm.updateInputTime(vm.appDispInfoStartupOutput());
                }
            });
            vm.leaveType.subscribe(value => {
                vm.applyTimeData().forEach((row : DataModel) => {
                    row.applyTime.forEach(apply => {
                        apply.substituteAppTime(0);
                        apply.annualAppTime(0);
                        apply.careAppTime(0);
                        apply.childCareAppTime(0);
                        apply.super60AppTime(0);
                        apply.specialAppTime(0);
                        apply.calculatedTime(0);
                    });
                });
            });
        }

        updateInputTime(value: any) {
            const vm = this;
            if (value.appDispInfoWithDateOutput.opActualContentDisplayLst && value.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail) {
                vm.applyTimeData()[AppTimeType.ATWORK].timeZones[0].startTime(value.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opWorkTime);
                vm.applyTimeData()[AppTimeType.OFFWORK].timeZones[0].startTime(value.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opLeaveTime);
                vm.applyTimeData()[AppTimeType.ATWORK2].timeZones[0].startTime(value.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opWorkTime2);
                vm.applyTimeData()[AppTimeType.OFFWORK2].timeZones[0].startTime(value.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.opDepartureTime2);
                vm.applyTimeData()[4].timeZones.forEach(tz => {
                    tz.startTime(null);
                    tz.endTime(null);
                    tz.appTimeType(GoingOutReason.PRIVATE);
                });
                let maxWorkNoHasData = 3;
                const outingTimes = value.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.stampRecordOutput.outingTime || [];
                outingTimes.forEach((time: any) => {
                    maxWorkNoHasData = Math.max(maxWorkNoHasData, time.frameNo);
                    vm.applyTimeData()[4].timeZones[time.frameNo - 1].startTime(time.opStartTime);
                    vm.applyTimeData()[4].timeZones[time.frameNo - 1].endTime(time.opEndTime);
                    vm.applyTimeData()[4].timeZones[time.frameNo - 1].appTimeType(time.opGoOutReasonAtr);
                });

                for (let no = 1; no <= maxWorkNoHasData; no++) {
                    if (!vm.applyTimeData()[4].timeZones[no - 1].display()) {
                        vm.applyTimeData()[4].timeZones[no - 1].display(true);
                    }
                }
                if (maxWorkNoHasData >= 10) {
                    vm.applyTimeData()[4].displayShowMore(false);
                }
            }
        }

        handleChangeAppDate(value: string) {
            const vm = this;
            vm.$validate(['#kaf000-a-component4 .nts-input']).then((valid: boolean) => {
                if (valid) {
                    const command = {
                        appDate: new Date(value).toISOString(),
                        appDisplayInfo: {
                            appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                            timeLeaveManagement: vm.timeLeaveManagement(),
                            reflectSetting: vm.reflectSetting()
                        }
                    };
                    vm.$blockui("show").then(() => {
                        return vm.$ajax(API.changeAppDate, command);
                    }).done((res: any) => {
                        if (res) {
                            vm.timeLeaveManagement(res.timeLeaveManagement);
                        }
                    }).fail((error: any) => {
                        if (_.isEmpty(vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opMsgErrorLst)) {
                            vm.$dialog.error(error);
                        }
                    }).always(() => {
                        vm.$blockui("hide")
                    });
                }
            });
        }

        public handleConfirmMessage(listMes: any, res: any): any {
            let vm = this;
            if (!_.isEmpty(listMes)) {
                let item = listMes.shift();
                return vm.$dialog.confirm({ messageId: item.msgID, messageParams: item.paramLst }).then((value) => {
                    if (value == 'yes') {
                        if (_.isEmpty(listMes)) {
                            return vm.registerData(res);
                        } else {
                            return vm.handleConfirmMessage(listMes, res);
                        }

                    }
                });
            }
        }

        registerData(details: Array<any>) {
            const vm = this;
            const paramsRegister = {
                timeLeaveAppDisplayInfo: {
                    appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                    timeLeaveManagement: vm.timeLeaveManagement(),
                    timeLeaveRemaining: vm.timeLeaveRemaining(),
                    reflectSetting: vm.reflectSetting()
                },
                application: ko.toJS(vm.application),
                details: details
            };
            paramsRegister.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart = new Date(paramsRegister.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart).toISOString();
            paramsRegister.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd = new Date(paramsRegister.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd).toISOString();
            return vm.$ajax(API.register, paramsRegister);
        }

        register() {
            const vm = this;
            const details: Array<any> = [];
            let errors: any[] = [];
            vm.applyTimeData().forEach((row : DataModel) => {
                if (row.display()) {
                    if (row.appTimeType < 4) {
                        const applyTime = {
                            substituteAppTime: vm.leaveType() == LeaveType.SUBSTITUTE || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].substituteAppTime() : 0,
                            annualAppTime: vm.leaveType() == LeaveType.ANNUAL || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].annualAppTime() : 0,
                            childCareAppTime: vm.leaveType() == LeaveType.CHILD_NURSING || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].childCareAppTime() : 0,
                            careAppTime: vm.leaveType() == LeaveType.NURSING || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].careAppTime() : 0,
                            super60AppTime: vm.leaveType() == LeaveType.SUPER_60H || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].super60AppTime() : 0,
                            specialAppTime: vm.leaveType() == LeaveType.SPECIAL || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].specialAppTime() : 0,
                            specialLeaveFrameNo: vm.leaveType() == LeaveType.SPECIAL || (vm.leaveType() == LeaveType.COMBINATION && row.applyTime[0].specialAppTime() > 0) ? vm.specialLeaveFrame() : null,
                        };
                        if (applyTime.substituteAppTime > 0
                            || applyTime.annualAppTime > 0
                            || applyTime.childCareAppTime > 0
                            || applyTime.careAppTime > 0
                            || applyTime.super60AppTime > 0
                            || applyTime.specialAppTime > 0) {
                                let detail = {
                                    appTimeType: row.appTimeType,
                                    timeZones: [{
                                        workNo: row.appTimeType == AppTimeType.ATWORK || row.appTimeType == AppTimeType.OFFWORK ? 1 : 2,
                                        startTime: row.appTimeType == AppTimeType.ATWORK || row.appTimeType == AppTimeType.ATWORK2 ? row.scheduledTime() : row.timeZones[0].startTime(),
                                        endTime: row.appTimeType == AppTimeType.ATWORK || row.appTimeType == AppTimeType.ATWORK2 ? row.timeZones[0].startTime() : row.scheduledTime(),
                                    }],
                                    applyTime: applyTime
                                }
                                if (_.filter(detail.timeZones, timeZone => timeZone.endTime && timeZone.startTime).length === 0) {
                                    detail.timeZones = [];
                                }
                                details.push(detail);
                        }
                    } else {
                        const privateTimeZones = row.timeZones.filter(z => z.appTimeType() == GoingOutReason.PRIVATE && z.enableInput() && (!!z.startTime() || !!z.endTime()));
                        const privateApplyTime = {
                            substituteAppTime: vm.leaveType() == LeaveType.SUBSTITUTE || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].substituteAppTime() : 0,
                            annualAppTime: vm.leaveType() == LeaveType.ANNUAL || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].annualAppTime() : 0,
                            childCareAppTime: vm.leaveType() == LeaveType.CHILD_NURSING || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].childCareAppTime() : 0,
                            careAppTime: vm.leaveType() == LeaveType.NURSING || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].careAppTime() : 0,
                            super60AppTime: vm.leaveType() == LeaveType.SUPER_60H || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].super60AppTime() : 0,
                            specialAppTime: vm.leaveType() == LeaveType.SPECIAL || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[0].specialAppTime() : 0,
                            specialLeaveFrameNo: vm.leaveType() == LeaveType.SPECIAL || (vm.leaveType() == LeaveType.COMBINATION && row.applyTime[0].specialAppTime() > 0) ? vm.specialLeaveFrame() : null,
                        };
                        _.forEach(privateTimeZones, (privateTimeZone: any) => {
                            if ((privateTimeZone.startTime() && !privateTimeZone.endTime()) || (!privateTimeZone.startTime() && privateTimeZone.endTime())) {
                                if (_.filter(errors, { 'messageId': 'Msg_2294' }).length == 0) {
                                    errors.push({
                                        message: this.$i18n.message('Msg_2294'), 
                                        messageId: 'Msg_2294',
                                        supplements: {}
                                    })
                                }
                            }
                            if ((privateTimeZone.startTime() && privateTimeZone.endTime()) && (privateTimeZone.startTime() > privateTimeZone.endTime())) {
                                if (_.filter(errors, { 'messageId': 'Msg_857' }).length == 0) {
                                    errors.push({ 
                                        message: this.$i18n.message('Msg_857'), 
                                        messageId: 'Msg_857', 
                                        supplements: {}
                                     })
                                }
                            }
                        });
                        if (privateApplyTime.substituteAppTime > 0
                            || privateApplyTime.annualAppTime > 0
                            || privateApplyTime.childCareAppTime > 0
                            || privateApplyTime.careAppTime > 0
                            || privateApplyTime.super60AppTime > 0
                            || privateApplyTime.specialAppTime > 0) {
                            details.push({
                                appTimeType: AppTimeType.PRIVATE,
                                timeZones: privateTimeZones.map(z => ({workNo: z.workNo, startTime: z.startTime(), endTime: z.endTime()})),
                                applyTime: privateApplyTime
                            });
                        }
                        const unionTimeZones = row.timeZones.filter(z => z.appTimeType() == GoingOutReason.UNION && z.enableInput() && (!!z.startTime() || !!z.endTime()));
                        const unionApplyTime = {
                            substituteAppTime: vm.leaveType() == LeaveType.SUBSTITUTE || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[1].substituteAppTime() : 0,
                            annualAppTime: vm.leaveType() == LeaveType.ANNUAL || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[1].annualAppTime() : 0,
                            childCareAppTime: vm.leaveType() == LeaveType.CHILD_NURSING || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[1].childCareAppTime() : 0,
                            careAppTime: vm.leaveType() == LeaveType.NURSING || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[1].careAppTime() : 0,
                            super60AppTime: vm.leaveType() == LeaveType.SUPER_60H || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[1].super60AppTime() : 0,
                            specialAppTime: vm.leaveType() == LeaveType.SPECIAL || vm.leaveType() == LeaveType.COMBINATION ? row.applyTime[1].specialAppTime() : 0,
                            specialLeaveFrameNo: vm.leaveType() == LeaveType.SPECIAL || (vm.leaveType() == LeaveType.COMBINATION && row.applyTime[1].specialAppTime() > 0) ? vm.specialLeaveFrame() : null,
                        };
                        _.forEach(unionTimeZones, (unionTimeZone: any) => {
                            if ((unionTimeZone.startTime() && !unionTimeZone.endTime()) || (!unionTimeZone.startTime() && unionTimeZone.endTime())) {
                                if (_.filter(errors, { 'messageId': 'Msg_2294' }).length == 0) {
                                    errors.push({
                                        message: this.$i18n.message('Msg_2294'), 
                                        messageId: 'Msg_2294',
                                        supplements: {}
                                    })
                                }
                            }
                            if ((unionTimeZone.startTime() && unionTimeZone.endTime()) && (unionTimeZone.startTime() > unionTimeZone.endTime())) {
                                if (_.filter(errors, { 'messageId': 'Msg_857' }).length == 0) {
                                    errors.push({ 
                                        message: this.$i18n.message('Msg_857'), 
                                        messageId: 'Msg_857', 
                                        supplements: {}
                                     })
                                }
                            }
                        });
                        if (unionApplyTime.substituteAppTime > 0
                            || unionApplyTime.annualAppTime > 0
                            || unionApplyTime.childCareAppTime > 0
                            || unionApplyTime.careAppTime > 0
                            || unionApplyTime.super60AppTime > 0
                            || unionApplyTime.specialAppTime > 0) {
                            details.push({
                                appTimeType: AppTimeType.UNION,
                                timeZones: unionTimeZones.map(z => ({workNo: z.workNo, startTime: z.startTime(), endTime: z.endTime()})),
                                applyTime: unionApplyTime
                            });
                        }
                    }
                }
            });

            if (errors.length > 0) {
                nts.uk.ui.dialog.bundledErrors({ errors: errors });
                return;
            }

            vm.$validate('.nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason').then(isValid => {
                if (isValid && !nts.uk.ui.errors.hasError()) {
                    vm.$blockui("show").then(() => {
                        if (_.isEmpty(vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opMsgErrorLst)) {
                            return vm.$ajax(API.changeAppDate, {
                                appDate: new Date(vm.application().appDate()).toISOString(),
                                appDisplayInfo: {
                                    appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                                    timeLeaveManagement: vm.timeLeaveManagement(),
                                    reflectSetting: vm.reflectSetting()
                                }
                            });
                        }
                    }).then(() => {
                        const params = {
                            timeDigestAppType: vm.leaveType(),
                            applicationNew: ko.toJS(vm.application),
                            details: details,
                            timeLeaveAppDisplayInfo: {
                                appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                                timeLeaveManagement: vm.timeLeaveManagement(),
                                timeLeaveRemaining: vm.timeLeaveRemaining(),
                                reflectSetting: vm.reflectSetting()
                            },
                            agentMode: vm.isAgentMode()
                        };
                        params.applicationNew.employeeID = _.isEmpty(vm.application().employeeIDLst()) ? null : vm.application().employeeIDLst()[0];
                        params.applicationNew.enteredPerson = vm.$user.employeeId;
                        params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart = new Date(params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart).toISOString();
                        params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd = new Date(params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd).toISOString();
                        return vm.$ajax(API.checkRegister, params);
                    }).then(res => {
                        if (res == undefined) return;
                        if (_.isEmpty(res)) {
                            return vm.registerData(details);
                        } else {
                            let listTemp = _.clone(res);
                            vm.handleConfirmMessage(listTemp, details);
                        }
                    }).done(result => {
                        if (result != undefined) {
                            vm.$dialog.info({messageId: "Msg_15"}).then(() => {
								nts.uk.request.ajax("at", API.reflectApp, result.reflectAppIdLst);
                            	CommonProcess.handleAfterRegister(result, vm.isSendMail(), vm, vm.isAgentMode());
                            });
                        }
                    }).fail(err => {
                        vm.$dialog.error(err).then(() => {
                            if (err.messageId == "Msg_1695") {
                                $(vm.$el).find('#kaf000-a-component4-singleDate').focus();
                            }
                            if (err.messageId == "Msg_1687") {
                                $(vm.$el).find('#leave-type-switch').focus();
                            }
                        });
                    }).always(() => {
                        vm.$blockui("hide")
                    });
                }
            });
        }

    }

}