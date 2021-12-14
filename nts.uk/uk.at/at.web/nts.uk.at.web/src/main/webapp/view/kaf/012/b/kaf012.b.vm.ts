module nts.uk.at.view.kaf012.b.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
	import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
    import DataModel = nts.uk.at.view.kaf012.shr.viewmodel2.DataModel;
    import AppTimeType = nts.uk.at.view.kaf012.shr.viewmodel2.AppTimeType;
    import GoingOutReason = nts.uk.at.view.kaf012.shr.viewmodel2.GoingOutReason;
    import LeaveType = nts.uk.at.view.kaf012.shr.viewmodel1.LeaveType;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    const API = {
        checkRegister: "at/request/application/timeLeave/checkBeforeRegister",
        updateApplication: "at/request/application/timeLeave/update",
        getDetail: "at/request/application/timeLeave/init"
    };

    @component({
        name: 'kaf012-b',
        template: '/nts.uk.at.web/view/kaf/012/b/index.html'
    })
    class Kaf012BViewModel extends ko.ViewModel {

		appType: KnockoutObservable<number> = ko.observable(AppType.ANNUAL_HOLIDAY_APPLICATION);
        appDispInfoStartupOutput: KnockoutObservable<any>;
        approvalReason: KnockoutObservable<string>;
        printContentOfEachAppDto: KnockoutObservable<PrintContentOfEachAppDto>;
        application: KnockoutObservable<Application>;
        reflectSetting: KnockoutObservable<any> = ko.observable(null);
        timeLeaveManagement: KnockoutObservable<any> = ko.observable(null);
        timeLeaveRemaining: KnockoutObservable<any> = ko.observable(null);
        leaveType: KnockoutObservable<number> = ko.observable(null);
        isReload: boolean = true;

        applyTimeData: KnockoutObservableArray<DataModel> = ko.observableArray([]);
        specialLeaveFrame: KnockoutObservable<number> = ko.observable(null);

        childCalcEvent: () => any;
        eventCalc: any;

        created(
            params: {
				appType: any,
				application: any,
				printContentOfEachAppDto: PrintContentOfEachAppDto,
            	approvalReason: any,
                appDispInfoStartupOutput: any,
                eventUpdate: (evt: () => void ) => void,
				eventReload: (evt: () => void) => void
            }
        ) {
            const vm = this;
            vm.printContentOfEachAppDto = ko.observable(params.printContentOfEachAppDto);
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            vm.application = params.application;
			vm.appType = params.appType;
			vm.approvalReason = params.approvalReason;
            for (let i = 0; i < 5; i++) {
                vm.applyTimeData.push(new DataModel(i, vm.reflectSetting, vm.appDispInfoStartupOutput, vm.application));
            }
            vm.getAppData();

            // gui event con ra viewmodel cha
            // nhớ dùng bind(vm) để ngữ cảnh lúc thực thi
            // luôn là component
            params.eventUpdate(vm.update.bind(vm));
			params.eventReload(vm.reload.bind(vm));

			vm.eventCalc = function(a: any) { vm.getChildCalcEvent.apply(vm, [a]) };
        }

        mounted() {
            const vm = this;
            vm.leaveType.subscribe(value => {
                if (!vm.isReload) {
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
                }
            });
        }

		reload() {
			const vm = this;
			if(vm.appType() === AppType.ANNUAL_HOLIDAY_APPLICATION) {
			    // clear old data
                vm.applyTimeData().forEach(row => {
                    row.timeZones.forEach(z => {
                        z.startTime(null);
                        z.endTime(null);
                    });
                    row.applyTime.forEach(i => {
                        i.substituteAppTime(0);
                        i.childCareAppTime(0);
                        i.careAppTime(0);
                        i.super60AppTime(0);
                        i.annualAppTime(0);
                        i.specialAppTime(0);
                    })
                });
                vm.applyTimeData()[4].timeZones.forEach(i => {
                    i.display(i.workNo < 4);
                });
                vm.applyTimeData()[4].displayShowMore(true);
                vm.isReload = true;
				vm.getAppData();
			}
		}

        getAppData() {
            let vm = this;
            vm.$blockui('show');
            return vm.$ajax(API.getDetail, {
                appId: vm.application().appID(),
                appDispInfoStartupOutput: vm.appDispInfoStartupOutput()
            }).done(res => {
                if (res) {
                    vm.reflectSetting(res.reflectSetting);
                    vm.timeLeaveRemaining(res.timeLeaveRemaining);
                    let totalAppTime: Array<number> = [0, 0, 0, 0, 0, 0], specialFrame: number = null, maxWorkNoHasData = 3;
                    res.details.forEach((detail: TimeLeaveAppDetail) => {
                        detail.timeZones.forEach(z => {
                            const index = detail.appTimeType < 4 ? 0 : z.workNo - 1;
                            const startTime = detail.appTimeType >= 4
                                            || detail.appTimeType == AppTimeType.OFFWORK
                                            || detail.appTimeType == AppTimeType.OFFWORK2 ? z.startTime : z.endTime;
                            const endTime = detail.appTimeType < 4 ? null : z.endTime;
                            if (detail.appTimeType >= 4) {
                                maxWorkNoHasData = Math.max(maxWorkNoHasData, index + 1);
                                vm.applyTimeData()[4].timeZones[index].appTimeType(detail.appTimeType == AppTimeType.PRIVATE ? GoingOutReason.PRIVATE : GoingOutReason.UNION);
                                vm.applyTimeData()[4].timeZones[index].startTime(startTime);
                                vm.applyTimeData()[4].timeZones[index].endTime(endTime);
                            } else {
                                vm.applyTimeData()[detail.appTimeType].timeZones[index].startTime(startTime);
                                vm.applyTimeData()[detail.appTimeType].timeZones[index].endTime(endTime);
                            }
                        });
                        const index1 = Math.min(detail.appTimeType, 4);
                        const index2 = detail.appTimeType <= 4 ? 0 : 1;
                        vm.applyTimeData()[index1].applyTime[index2].substituteAppTime(detail.applyTime.substituteAppTime);
                        vm.applyTimeData()[index1].applyTime[index2].annualAppTime(detail.applyTime.annualAppTime);
                        vm.applyTimeData()[index1].applyTime[index2].childCareAppTime(detail.applyTime.childCareAppTime);
                        vm.applyTimeData()[index1].applyTime[index2].careAppTime(detail.applyTime.careAppTime);
                        vm.applyTimeData()[index1].applyTime[index2].super60AppTime(detail.applyTime.super60AppTime);
                        vm.applyTimeData()[index1].applyTime[index2].specialAppTime(detail.applyTime.specialAppTime);
                        vm.applyTimeData()[index1].applyTime[index2].calculatedTime(
                            detail.applyTime.substituteAppTime
                            + detail.applyTime.annualAppTime
                            + detail.applyTime.childCareAppTime
                            + detail.applyTime.careAppTime
                            + detail.applyTime.super60AppTime
                            + detail.applyTime.specialAppTime
                        );
                        totalAppTime[LeaveType.SUBSTITUTE] += detail.applyTime.substituteAppTime;
                        totalAppTime[LeaveType.ANNUAL] += detail.applyTime.annualAppTime;
                        totalAppTime[LeaveType.CHILD_NURSING] += detail.applyTime.childCareAppTime;
                        totalAppTime[LeaveType.NURSING] += detail.applyTime.careAppTime;
                        totalAppTime[LeaveType.SUPER_60H] += detail.applyTime.super60AppTime;
                        totalAppTime[LeaveType.SPECIAL] += detail.applyTime.specialAppTime;
                        if (detail.applyTime.specialAppTime > 0) specialFrame = detail.applyTime.specialLeaveFrameNo;
                    });
                    if (totalAppTime.filter(i => i > 0).length > 1) {
                        vm.leaveType(LeaveType.COMBINATION);
                    } else {
                        vm.leaveType(_.findIndex(totalAppTime, i => i > 0));
                    }
                    vm.timeLeaveManagement(res.timeLeaveManagement);
                    if (specialFrame != null) vm.specialLeaveFrame(specialFrame);
                    vm.printContentOfEachAppDto().opPrintContentOfTimeLeave = res.details;

                    if(vm.leaveType() == LeaveType.COMBINATION && _.isFunction(vm.childCalcEvent)) {
                        vm.childCalcEvent();
                    }

                    if (vm.application().prePostAtr() == 1
                        && vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst
                        && vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail) {
                        const outingTimes = vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.stampRecordOutput.outingTime || [];
                        outingTimes.forEach((time: any) => {
                            if (!vm.applyTimeData()[4].timeZones[time.frameNo - 1].startTime() && !vm.applyTimeData()[4].timeZones[time.frameNo - 1].endTime()) {
                                maxWorkNoHasData = Math.max(maxWorkNoHasData, time.frameNo);
                                vm.applyTimeData()[4].timeZones[time.frameNo - 1].startTime(time.opStartTime);
                                vm.applyTimeData()[4].timeZones[time.frameNo - 1].endTime(time.opEndTime);
                                vm.applyTimeData()[4].timeZones[time.frameNo - 1].appTimeType(time.opGoOutReasonAtr);
                            }
                        });
                    }

                    for (let no = 1; no <= maxWorkNoHasData; no++) {
                        if (!vm.applyTimeData()[4].timeZones[no - 1].display()) {
                            vm.applyTimeData()[4].timeZones[no - 1].display(true);
                        }
                    }
                    if (maxWorkNoHasData >= 10) {
                        vm.applyTimeData()[4].displayShowMore(false);
                    }

                    vm.$nextTick(() => {
                        $("#updateKAF000").focus();
                    });
                }
            }).fail(err => {
                vm.$dialog.error(err).then(() => {
                    if (err.messageId == "Msg_474") {
                        nts.uk.request.jumpToTopPage();
                    }
                });
            }).always(() => {
                vm.isReload = false;
                vm.$blockui('hide')
            });
        }

        // event update cần gọi lại ở button của view cha
        update() {
            const vm = this;
            if (!vm.appDispInfoStartupOutput().appDetailScreenInfo) {
                return;
            }
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
            
            let dfd = $.Deferred();
            if (errors.length > 0) {
                nts.uk.ui.dialog.bundledErrors({ errors: errors });
                dfd.reject(false);
                return dfd.promise();
            }

            vm.$blockui("show");
            return vm.$validate('.nts-input', '#kaf000-b-component3-prePost', '#kaf000-b-component5-comboReason')
                .then(isValid => {
                    if (isValid && !nts.uk.ui.errors.hasError()) {
                        const params = {
                            timeDigestAppType: vm.leaveType(),
                            applicationUpdate: ko.toJS(vm.application),
                            details: details,
                            timeLeaveAppDisplayInfo: {
                                appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                                timeLeaveManagement: vm.timeLeaveManagement(),
                                timeLeaveRemaining: vm.timeLeaveRemaining(),
                                reflectSetting: vm.reflectSetting()
                            },
                            agentMode: false
                        };
                        params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart = new Date(params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart).toISOString();
                        params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd = new Date(params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd).toISOString();
                        return vm.$ajax(API.checkRegister, params);
                    }
                }).then(res => {
                    if (res == undefined) return;
                    if ( _.isEmpty( res ) ) {
                        return vm.registerData( details );
                    } else {
                        let listTemp = _.clone( res );
                        return vm.handleConfirmMessage( listTemp, details );

                    }
                }).done(result => {
                    if (result != undefined) {
                        vm.$dialog.info( { messageId: "Msg_15" } ).then(() => {
							CommonProcess.handleMailResult(result, vm);
						});
                    }
                }).then((result: any) => {
                    if (result) {
                        return dfd.resolve(true);
                    }
                }).fail(err => {
                    // vm.handleError(err);
                    if (err.messageId == "Msg_1687") {
                        $(vm.$el).find('leave-type-switch').focus();
                    }
                    if (err) {
                        return dfd.reject(false);
                    }
                }).always(() => {
                    vm.$blockui("hide");
                    return dfd.promise();
                });
                
        }

        handleConfirmMessage(listMes: any, res: any): any {
            let vm = this;
            if (!_.isEmpty(listMes)) {
                let item = listMes.shift();
                return vm.$dialog.confirm({ messageId: item.msgID }).then((value) => {
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
            return vm.$ajax(API.updateApplication, paramsRegister);
        }

        // public handleError(err: any) {
        //     const vm = this;
        //     let param;
        //     if (err.message && err.messageId) {
        //         param = {messageId: err.messageId, messageParams: err.parameterIds};
        //     } else {
        //         if (err.message) {
        //             param = {message: err.message, messageParams: err.parameterIds};
        //         } else {
        //             param = {messageId: err.messageId, messageParams: err.parameterIds};
        //         }
        //     }
        //     vm.$dialog.error(param).then((err: any) => {
        //         if (err.messageId == 'Msg_197') {
        //         	ko.contextFor($('#contents-area')[0]).$vm.loadData();
        //         }
        //     });
        // }

        getChildCalcEvent(evt: () => void) {
            const vm = this;
            vm.childCalcEvent = evt;
        }
    }

    interface TimeLeaveAppDetail {
        appTimeType: number,
        applyTime: {
            annualAppTime: number,
            super60AppTime: number,
            careAppTime: number,
            childCareAppTime: number,
            substituteAppTime: number,
            specialAppTime: number,
            specialLeaveFrameNo: number
        }
        timeZones: Array<{workNo: number, startTime: number, endTime: number}>
    }

}