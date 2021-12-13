module nts.uk.at.view.kaf006_ref.b.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
    import WorkType = nts.uk.at.view.kaf006.shr.viewmodel.WorkType;
    import Kaf006ShrViewModel = nts.uk.at.view.kaf006.shr.viewmodel.Kaf006ShrViewModel;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

    @component({
        name: 'kaf006-b',
        template: `/nts.uk.at.web/view/kaf/006/b/index.html`
    })
    export class Kaf006BViewModel extends ko.ViewModel {
        appType: KnockoutObservable<number> = ko.observable(AppType.ABSENCE_APPLICATION);
        application: KnockoutObservable<Application>;
        appDispInfoStartupOutput: any;
        data: any = null;
		workTypeOrigin: any = null;
		workTimeOrigin: any = null;
		hdAppSet: KnockoutObservableArray<any> = ko.observableArray([]);
		selectedType: KnockoutObservable<any> = ko.observable();
		workTypeLst: KnockoutObservableArray<any> = ko.observableArray([]);
		selectedWorkTypeCD: KnockoutObservable<string> = ko.observable(null);
		selectedWorkType: KnockoutObservable<WorkType> = ko.observable(new WorkType({workTypeCode: '', name: ''}));
		selectedWorkTimeCD: KnockoutObservable<string> = ko.observable();
		selectedWorkTimeName: KnockoutObservable<string> = ko.observable();
		selectedWorkTimeDisp: KnockoutComputed<string>;
		dateSpecHdRelationLst: KnockoutObservableArray<any> = ko.observableArray([]);
		selectedDateSpec: KnockoutObservable<any> = ko.observable();
		relationshipReason: KnockoutObservable<string> = ko.observable();
		maxNumberOfDay: KnockoutComputed<any>;
		specAbsenceDispInfo: KnockoutObservable<any> = ko.observable();
		isDispMourn: any = ko.observable(false);
		isCheckMourn: any = ko.observable(false);
		requiredVacationTime: KnockoutObservable<number> = ko.observable(0);
		timeRequired: KnockoutObservable<string> = ko.observable();
		leaveComDayOffManas: KnockoutObservableArray<any> = ko.observableArray([]);
		payoutSubofHDManagements: KnockoutObservable<any> = ko.observableArray([]);
		workTypeBefore: KnockoutObservable<any> = ko.observable();
		workTypeAfter: KnockoutObservable<any> = ko.observable();
		isEnableSwitchBtn: boolean = true;
		updateMode: KnockoutObservable<boolean> = ko.observable(true);
		dateBeforeChange: KnockoutObservable<string> = ko.observable(null);
		isDispTime2ByWorkTime: KnockoutObservable<boolean> = ko.observable(true);
		isInit: KnockoutObservable<boolean> = ko.observable(true);
		grantDate: KnockoutObservable<string> = ko.observable(null);
        grantDays: KnockoutObservable<number> = ko.observable(0);
        grantDaysOfYear: KnockoutComputed<string>;

		checkAppDate: KnockoutObservable<boolean> = ko.observable(true);

		yearRemain: KnockoutObservable<string> = ko.observable();
		subHdRemain: KnockoutObservable<string> = ko.observable();
		// subVacaRemain: KnockoutObservable<string> = ko.observable();
		remainingHours: KnockoutObservable<string> = ko.observable();

		over60HHourRemain: KnockoutObservable<string> = ko.observable();
		subVacaRemain: KnockoutObservable<string> = ko.observable();
		subVacaHourRemain: KnockoutObservable<string> = ko.observable();
		timeYearLeave: KnockoutObservable<string> = ko.observable();
		childNursingRemain: KnockoutObservable<string> = ko.observable();
		nursingRemain: KnockoutObservable<string> = ko.observable();
		isChangeWorkHour: KnockoutObservable<boolean> = ko.observable(false);
		startTime1: KnockoutObservable<number> = ko.observable();
        endTime1: KnockoutObservable<number> = ko.observable();
        startTime2: KnockoutObservable<number> = ko.observable();
		endTime2: KnockoutObservable<number> = ko.observable();
		
        // 60H超休
        over60H: KnockoutObservable<number> = ko.observable();
        // 時間代休
        timeOff: KnockoutObservable<number> = ko.observable();
        // 時間年休
        annualTime: KnockoutObservable<number> = ko.observable();
        // 子の看護
        childNursing: KnockoutObservable<number> = ko.observable();
        // 介護時間
        nursing: KnockoutObservable<number> = ko.observable();

        isSendMail: KnockoutObservable<Boolean>;
        approvalReason: KnockoutObservable<string>;
        printContentOfEachAppDto: KnockoutObservable<PrintContentOfEachAppDto>;

        // Condition
		condition10: KnockoutObservable<boolean> = ko.observable(true);
		condition11: KnockoutObservable<boolean> = ko.observable(true);
		condition30: KnockoutObservable<boolean> = ko.observable(true);
		condition12: KnockoutObservable<boolean> = ko.observable(true);
		condition19Over60: KnockoutObservable<boolean> = ko.observable(true);
		condition19Substitute: KnockoutObservable<boolean> = ko.observable(true);
		condition19Annual: KnockoutObservable<boolean> = ko.observable(true);
		condition19ChildNursing: KnockoutObservable<boolean> = ko.observable(true);
		condition19Nursing: KnockoutObservable<boolean> = ko.observable(true);
		condition14: KnockoutObservable<boolean> = ko.observable(true);
		condition15: KnockoutObservable<boolean> = ko.observable(true);
		condition21: KnockoutObservable<boolean> = ko.observable(true);
		condition22: KnockoutObservable<boolean> = ko.observable(true);
		condition23: KnockoutObservable<boolean> = ko.observable(true);
		condition24: KnockoutObservable<boolean> = ko.observable(true);

		condition1_0: KnockoutObservable<boolean> = ko.observable(true);
		condition1_1: KnockoutObservable<boolean> = ko.observable(true);
		condition1_2: KnockoutObservable<boolean> = ko.observable(true);
		condition1_3: KnockoutObservable<boolean> = ko.observable(true);
		condition1_4: KnockoutObservable<boolean> = ko.observable(true);
		condition1_5: KnockoutObservable<boolean> = ko.observable(true);
		condition1_6: KnockoutObservable<boolean> = ko.observable(true);

		condition6: KnockoutObservable<boolean> = ko.observable(true);
		condition7: KnockoutObservable<boolean> = ko.observable(true);
		condition8: KnockoutObservable<boolean> = ko.observable(true);
		condition9: KnockoutObservable<boolean> = ko.observable(true);
		condition31: KnockoutObservable<boolean> = ko.observable(true);
		condition32: KnockoutObservable<boolean> = ko.observable(false);
		flowWorkFlag: KnockoutObservable<boolean> = ko.observable(false);
        
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

            vm.isSendMail = ko.observable(true);
            vm.printContentOfEachAppDto = ko.observable(params.printContentOfEachAppDto);
            vm.approvalReason = params.approvalReason;
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            vm.application = params.application;
			vm.appType = params.appType;
			vm.updateMode(vm.appDispInfoStartupOutput().appDetailScreenInfo.outputMode === 0 ? false : true);
			vm.createParamKAF006();
            // gui event con ra viewmodel cha
            // nhớ dùng bind(vm) để ngữ cảnh lúc thực thi
            // luôn là component
            params.eventUpdate(vm.update.bind(vm));
			params.eventReload(vm.reload.bind(vm));

			vm.maxNumberOfDay = ko.computed(() => {
				let data = vm.$i18n("KAF006_44").concat("\n");
				if (vm.specAbsenceDispInfo()) {
					if (vm.isDispMourn() && vm.isCheckMourn()) {
						let param = vm.specAbsenceDispInfo().maxDay + vm.specAbsenceDispInfo().dayOfRela;
						data = data + vm.$i18n("KAF006_46", [param.toString()]);
					} else {
						let param = vm.specAbsenceDispInfo().maxDay;
						data = data + vm.$i18n("KAF006_46", [param.toString()]);
					}

				}
				return data;
            });
            
            vm.selectedWorkTimeDisp = ko.computed(() => {
				const vm = this;

				if (vm.selectedWorkTimeCD()) {
					if (vm.selectedWorkTimeName()) {
						return vm.selectedWorkTimeCD() + " " + vm.selectedWorkTimeName();
					} else {
						return vm.selectedWorkTimeCD() + " " + "マスタ未登録";
					}
				}

				return vm.$i18n("KAF006_21");
            });

			vm.grantDaysOfYear = ko.computed(() => {
                if (vm.grantDate()) {
                    return vm.$i18n('KAF006_98') + moment(vm.grantDate()).format('YYYY/MM/DD') + '　' + vm.grantDays() + '日';
                }

                return vm.$i18n('KAF006_98') + vm.$i18n('KAF006_99');
            })

			vm.selectedDateSpec.subscribe(() => {
				if (vm.selectedType() !== 3 || vm.dateSpecHdRelationLst().length === 0) {
					return;
				}

				if ($('#relaReason').ntsError('hasError')) {
                    $('#relaReason').ntsError('clear');
                }

				let command = {
					frameNo: vm.specAbsenceDispInfo() ? vm.specAbsenceDispInfo().frameNo : null,
					specHdEvent: vm.specAbsenceDispInfo() ? vm.specAbsenceDispInfo().specHdEvent : null,
					relationCD: vm.selectedDateSpec()
				};

				vm.$blockui("show");
                vm.$ajax(API.changeRela, command).done((success) => {
					if (success) {
						if (vm.specAbsenceDispInfo()) {
							vm.specAbsenceDispInfo().maxDay = success.maxDayObj.maxDay;
							vm.specAbsenceDispInfo().dayOfRela = success.maxDayObj.dayOfRela;
							vm.specAbsenceDispInfo.valueHasMutated();
							vm.checkCondition8(vm.data);
						}
					}
                }).fail((error) => {
					if (error) {
						vm.$dialog.error({ messageId: error.messageId, messageParams: error.parameterIds });
					}
                }).always(() => {
                    vm.$blockui("hide");
                })
			});

			// check selected item
            vm.selectedType.subscribe(() => {
				if (vm.isInit()) {
					return;
				}

				// vm.selectedWorkTimeCD(null);
				// vm.selectedWorkTimeName(null);
				// vm.startTime1(null);
				// vm.startTime2(null);
				// vm.endTime1(null);
				// vm.endTime2(null);

				// vm.data.selectedWorkTypeCD = null;
                // vm.data.selectedWorkTimeCD = null;

				// vm.$errors("clear");
				nts.uk.ui.errors.clearAll()
				
				let appDates = [];
				if (_.isNil(vm.application().opAppStartDate())) {
					appDates.push(vm.application().opAppStartDate());
				}
				if (_.isNil(vm.application().opAppEndDate()) && vm.application().opAppStartDate() !== vm.application().opAppEndDate()) {
					appDates.push(vm.application().opAppEndDate());
				}

                let command = {
					companyID: __viewContext.user.companyId,
					appDates: appDates,
					startInfo: vm.data,
					holidayAppType: vm.selectedType()
				};

				command.startInfo.leaveComDayOffManas = _.map(command.startInfo.leaveComDayOffManas, (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				});
				command.startInfo.payoutSubofHDManas = _.map(command.startInfo.payoutSubofHDManas, (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				});

                vm.$blockui("show");
                vm.$ajax(API.getAllAppForLeave, command).done((result) => {
					vm.specAbsenceDispInfo(result.specAbsenceDispInfo);
					return result;
                }).then((data) => {
					if (data) {
						vm.fetchData(data);
						// vm.selectedWorkTimeCD(null);
                        // vm.selectedWorkTimeName(null);
                        // vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", 0));
						vm.appDispInfoStartupOutput(data.appDispInfoStartupOutput);
						$("#work-type-combobox").focus()
						return data;
					}
				}).then((data) => {
					if (data) {
						vm.checkCondition(data);
						vm.selectedWorkTypeCD(vm.data.selectedWorkTypeCD);
						return data;
					}
				}).fail((error) => {
					if (error) {
						vm.$dialog.error({ messageId: error.messageId, messageParams: error.parameterIds });
					}
                }).always(() => {
                    vm.$blockui("hide");
                })
			});

			// Subscribe workType value after change
			vm.selectedWorkTypeCD.subscribe(() => {
				if (vm.isInit()) {
					return;
				}
				if (_.isNil(vm.selectedWorkTypeCD()) || _.isEmpty(vm.workTypeLst())) {
					return;
				}

				if ($('#relaReason').ntsError('hasError')) {
                    $('#relaReason').ntsError('clear');
                }
				
				if (_.filter(vm.workTypeLst(), { 'workTypeCode': vm.selectedWorkTypeCD() }).length === 0) {
					return;
				}

				let wtAfter = _.filter(vm.data.workTypeLst, { 'workTypeCode': vm.selectedWorkTypeCD() }).length > 0 ? 
					_.filter(vm.data.workTypeLst, { 'workTypeCode': vm.selectedWorkTypeCD() })[0] : null;

				if (wtAfter === null) {
					return;
				}
				// return;
				let commandCheckTyingManage = {
					wtBefore: vm.workTypeBefore(),
					wtAfter: wtAfter,
					leaveComDayOffMana: vm.leaveComDayOffManas(),
					payoutSubofHDManagements: vm.payoutSubofHDManagements()
				};

				commandCheckTyingManage.leaveComDayOffMana = _.map(commandCheckTyingManage.leaveComDayOffMana, (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				});
				commandCheckTyingManage.payoutSubofHDManagements = _.map(commandCheckTyingManage.payoutSubofHDManagements, (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				});

				// Check vacation tying manage
				// 休暇紐付管理をチェックする
				vm.$blockui("show");
				vm.$ajax(API.checkVacationTyingManage, commandCheckTyingManage)
					.done((success) => {
						if (success) {
							if (success.clearManageSubsHoliday) {
								vm.leaveComDayOffManas([]);
								vm.data.leaveComDayOffManas = [];
							}
							if (success.clearManageHolidayString) {
								vm.payoutSubofHDManagements([]);
								vm.data.payoutSubofHDManas = [];
							}
						}
					}).fail((error) => {
						if (error) {
							vm.$dialog.error({ messageId: error.messageId, messageParams: error.parameterIds });
						}
					}).always(() => {
						vm.$blockui("hide");
					});
					
					let dates = [];
					if (vm.application().opAppStartDate()) {
						dates.push(vm.application().opAppStartDate());
					}
					if (vm.application().opAppEndDate() && (vm.application().opAppEndDate() !== vm.application().opAppStartDate())) {
						dates.push(vm.application().opAppEndDate());
					}
					
					let commandChangeWorkType = {
						appDates: dates,
						startInfo: vm.data,
						holidayAppType: vm.selectedType(),
						workTypeCd: vm.selectedWorkTypeCD()
					};
					commandChangeWorkType.startInfo.leaveComDayOffManas = _.map(commandChangeWorkType.startInfo.leaveComDayOffManas, (x: any) => {
						x.dateOfUse = new Date(x.dateOfUse).toISOString();
						x.outbreakDay = new Date(x.outbreakDay).toISOString();
						return x;
					});
					commandChangeWorkType.startInfo.payoutSubofHDManas = _.map(commandChangeWorkType.startInfo.payoutSubofHDManas, (x: any) => {
						x.dateOfUse = new Date(x.dateOfUse).toISOString();
						x.outbreakDay = new Date(x.outbreakDay).toISOString();
						return x;
					});
					// Process change workType
					// 勤務種類変更時処理
					vm.$blockui("show");
					vm.$ajax(API.changeWorkType, commandChangeWorkType)
					.done((success) => {
						if (success) {
							vm.specAbsenceDispInfo(success.specAbsenceDispInfo);
							return success;
						}
					}).then((data) => {
						if (data) {
							vm.fetchData(data);

							let workTimeLst = data.workTimeLst;
							if (workTimeLst.length > 0) {
								if (_.filter(workTimeLst, {'workNo': 1}).length > 0) {
									let workTime1: any = _.filter(workTimeLst, {'workNo': 1})[0];
									vm.startTime1(workTime1.startTime);
									vm.endTime1(workTime1.endTime);
								} else {
									vm.startTime1(null);
                                	vm.endTime1(null);
								}
								if (_.filter(workTimeLst, {'workNo': 2}).length > 0) {
									let workTime2: any = _.filter(workTimeLst, {'workNo': 2})[0];
									if (workTime2.useAtr === 0) {
										vm.isDispTime2ByWorkTime(false);
									} else {
										vm.isDispTime2ByWorkTime(true);
										vm.startTime2(workTime2.startTime);
										vm.endTime2(workTime2.endTime);
									}
								} else {
									vm.isDispTime2ByWorkTime(false);
									vm.startTime2(null);
                                	vm.endTime2(null);
								}
							} else {
								vm.startTime1(null);
								vm.endTime1(null);
								vm.startTime2(null);
								vm.endTime2(null);
							}
							return data;
						}
					}).then((data) => {
						if (data) {
							vm.checkCondition(data);
							return data;
						}
					}).fail((error) => {
						if (error) {
							vm.$dialog.error({ messageId: error.messageId, messageParams: error.parameterIds });
						}
					}).always(() => {
						vm.$blockui("hide");
					});
			});

			vm.isChangeWorkHour.subscribe((value) => {
                const vm = this;
                
                vm.data.workTimeChange = value;
				
                if (vm.application().opAppStartDate()) {
					vm.$blockui("show");
                    let command = {
                        sId: vm.application().employeeIDLst()[0], 
                        date: vm.application().opAppStartDate(), 
                        workTypeCd: vm.selectedWorkTypeCD(), 
                        workTimeCd: value ? vm.selectedWorkTimeCD() : null, 
                        appAbsenceStartInfo: vm.data
                    };

					command.appAbsenceStartInfo.leaveComDayOffManas = _.map(command.appAbsenceStartInfo.leaveComDayOffManas, (x: any) => {
						x.dateOfUse = new Date(x.dateOfUse).toISOString();
						x.outbreakDay = new Date(x.outbreakDay).toISOString();
			
						return x;
					});
					command.appAbsenceStartInfo.payoutSubofHDManas = _.map(command.appAbsenceStartInfo.payoutSubofHDManas, (x: any) => {
						x.dateOfUse = new Date(x.dateOfUse).toISOString();
						x.outbreakDay = new Date(x.outbreakDay).toISOString();
						
						return x;
					});
    
                    vm.$ajax(API.changeUseingWorkTime, command).then((res) => {
                        if (res) {
                            vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", res.requiredVacationTime));
                        }
                    }).fail((error) => {
                        if (error) {
                            vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
                        }
                    }).always(() => {
                        vm.$blockui("hide");
                    });
                }
            })

			// Subscribe work time after change
			vm.selectedWorkTimeCD.subscribe(() => {
				if (vm.isInit()) {
					return;
				}
				
				if (_.isNil(vm.selectedWorkTimeCD())) {
					return;
				}

				let commandChangeWorkTime = {
					date: vm.application().appDate(),
					workTypeCode: vm.selectedWorkTypeCD(),
					workTimeCode: vm.selectedWorkTimeCD(),
					appAbsenceStartInfoDto: vm.data
				};

				commandChangeWorkTime.appAbsenceStartInfoDto.leaveComDayOffManas = _.map(commandChangeWorkTime.appAbsenceStartInfoDto.leaveComDayOffManas, (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				});
				commandChangeWorkTime.appAbsenceStartInfoDto.payoutSubofHDManas = _.map(commandChangeWorkTime.appAbsenceStartInfoDto.payoutSubofHDManas, (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				});

				vm.$blockui("show");
				vm.$ajax(API.changeWorkTime, commandChangeWorkTime)
					.done((success) => {
						if (success) {
							vm.specAbsenceDispInfo(success.specAbsenceDispInfo);
							return success;
						}
					}).then((data) => {
						if (data) {
							vm.fetchData(data);
							vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", vm.requiredVacationTime()));

							let workTimeLst = data.workTimeLst;
							if (workTimeLst.length > 0) {
								if (_.filter(workTimeLst, { 'workNo': 1 }).length > 0) {
									let workTime1: any = _.filter(workTimeLst, { 'workNo': 1 })[0];
									vm.startTime1(workTime1.startTime);
									vm.endTime1(workTime1.endTime);
								} else {
									vm.startTime1(null);
                                	vm.endTime1(null);
								}
								if (_.filter(workTimeLst, { 'workNo': 2 }).length > 0) {
									let workTime2: any = _.filter(workTimeLst, { 'workNo': 2 })[0];
									if (workTime2.useAtr === 0) {
										vm.isDispTime2ByWorkTime(false);
									} else {
										vm.isDispTime2ByWorkTime(true);
										vm.startTime2(workTime2.startTime);
										vm.endTime2(workTime2.endTime);
									}
								} else {
									vm.isDispTime2ByWorkTime(false);
									vm.startTime2(null);
                                	vm.endTime2(null);
								}
							}
							return data;
						}
					}).then((data) => {
						if (data) {
							vm.checkCondition(data);
							return data;
						}
					}).fail((error) => {
						if (error) {
							vm.$dialog.error({ messageId: error.messageId, messageParams: error.parameterIds });
						}
					}).always(() => {
						vm.$blockui("hide");
					});
			});

			// disply condtion for item A10_3
			vm.isDispMourn = ko.computed(() => {
				if (vm.specAbsenceDispInfo()) {
					if (vm.specAbsenceDispInfo().specHdForEventFlag && vm.specAbsenceDispInfo().specHdEvent.maxNumberDay === 2 && vm.specAbsenceDispInfo().specHdEvent.makeInvitation === 1) {
						return true;
					}
				}

				return false;
			});
        };
		
        mounted() {
			const vm = this;

        };

        reload() {
			const vm = this;
			if(vm.appType() === AppType.ABSENCE_APPLICATION) {
				vm.updateMode(vm.appDispInfoStartupOutput().appDetailScreenInfo.outputMode === 0 ? false : true);
				// vm.selectedDateSpec.valueHasMutated();
				// vm.selectedType.valueHasMutated();
				// vm.selectedWorkTypeCD.valueHasMutated();
				// vm.selectedWorkTimeCD.valueHasMutated();
				vm.createParamKAF006();
				// vm.checkCondition(vm.data);
			}
        };

        update() {
			const vm = this;

			// validate
			if (!vm.validate()) {
				return;
			}

			// Create data Vacation Request/ 休暇申請
			// vm.createDataVacationApp();
			let appDates: any[] = [];

			let holidayAppDates = [];

			let commandCheckUpdate = {
				appAbsenceStartInfoDto: vm.data,
				applyForLeave: this.createDataVacationApp(),
				agentAtr: false,
				application: ko.toJS(vm.appDispInfoStartupOutput().appDetailScreenInfo.application)
			};

			commandCheckUpdate.appAbsenceStartInfoDto.leaveComDayOffManas = _.map(commandCheckUpdate.appAbsenceStartInfoDto.leaveComDayOffManas, (x: any) => {
				x.dateOfUse = new Date(x.dateOfUse).toISOString();
				x.outbreakDay = new Date(x.outbreakDay).toISOString();
				return x;
			});
			commandCheckUpdate.appAbsenceStartInfoDto.payoutSubofHDManas = _.map(commandCheckUpdate.appAbsenceStartInfoDto.payoutSubofHDManas, (x: any) => {
				x.dateOfUse = new Date(x.dateOfUse).toISOString();
				x.outbreakDay = new Date(x.outbreakDay).toISOString();
				return x;
			});

			if ((!(vm.workTypeOrigin == null && vm.workTimeOrigin == null))) {
				commandCheckUpdate.appAbsenceStartInfoDto.workInfomationForApplication = {workTypeCode: vm.workTypeOrigin, workTimeCode: vm.workTimeOrigin};
			}

			commandCheckUpdate.application.opAppReason = vm.application().opAppReason();
            commandCheckUpdate.application.opAppStandardReasonCD = vm.application().opAppStandardReasonCD();
            commandCheckUpdate.application.opReversionReason = vm.application().opReversionReason();

			let appTypeSettingLst = vm.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting;
			let qr = _.filter(appTypeSettingLst, { 'appType': vm.application().appType });


			let commandUpdate = {
				application: ko.toJS(vm.appDispInfoStartupOutput().appDetailScreenInfo.application),
				applyForLeave: this.createDataVacationApp(),
				appDispInfoStartupOutput: ko.toJS(vm.appDispInfoStartupOutput),
				holidayAppDates: appDates,
				leaveComDayOffMana: _.map(vm.leaveComDayOffManas(), (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				}),
				payoutSubofHDManagements: _.map(vm.payoutSubofHDManagements(), (x: any) => {
					x.dateOfUse = new Date(x.dateOfUse).toISOString();
					x.outbreakDay = new Date(x.outbreakDay).toISOString();
					return x;
				})
			};

			commandUpdate.application.opAppReason = vm.application().opAppReason();
            commandUpdate.application.opAppStandardReasonCD = vm.application().opAppStandardReasonCD();
            commandUpdate.application.opReversionReason = vm.application().opReversionReason();

			vm.$blockui("show");
			let dfd = $.Deferred();
			vm.$validate('#kaf000-a-component4 .nts-input', '#kaf000-a-component3-prePost', 
			'#kaf000-a-component5-comboReason', '#kaf000-a-component5-textReason', '#combo-box', '#inpReasonTextarea', '#work-type-combobox')
			.then((valid) => {
                if (valid) {
                    if (vm.selectedType() === 3 && vm.condition6()) {
                        return vm.$validate('#relation-list');
                    }
                    return true;
                }
            }).then((valid) => {
				if (valid) {
					if (vm.selectedType() === 6) {
						return 	vm.$validate('#over60H', '#timeOff', '#annualTime', '#childNursing', '#nursing');
					} else {
						return true;
					}
				}
			}).then((isValid) => {
				if (isValid) {
					// validate riêng cho màn hình
                    if (vm.selectedType() === 3 && vm.condition8() && vm.updateMode()) {
                        return vm.$validate('#relaReason');
                    }
					return true;
				}
			}).then((isValid) => {
				if (isValid) {
					return vm.$ajax('at', API.checkBeforeUpdate, commandCheckUpdate);
				}
			})
			.then((result) => {
				if (result) {
					holidayAppDates = result.holidayDateLst;
					commandUpdate.holidayAppDates = holidayAppDates;
					// xử lý confirmMsg
					return vm.handleConfirmMessage(result.confirmMsgLst);
				}
			}).then((result) => {
				if(result) {
					// update 
					return vm.$ajax('at', API.update, commandUpdate);
				}
			}).then((result) => {
				if (result) {
					return vm.$dialog.info({ messageId: "Msg_15"}).then(() => {
						return CommonProcess.handleMailResult(result, vm);
					});	
				}
			}).then((result) => {
                if(result) {
					return dfd.resolve(true);
				}	
				return dfd.resolve(result);
            }).fail((failData) => {
				// xử lý lỗi nghiệp vụ riêng
				vm.handleErrorCustom(failData).then((result: any) => {
					if(result) {
						return dfd.reject(failData);	
					}	
					return dfd.reject(false);
				});
			});
			return dfd.promise();
        };
		
		/**
		 * Start screen B
		 */
        private createParamKAF006() {
            const vm = this;

			
			vm.isInit = ko.observable(true);
            let command = {
				appID: vm.application().appID(),
				appDispInfoStartupOutput: vm.appDispInfoStartupOutput()
            };

            vm.$blockui("show");
            vm.$ajax(API.initPageB, command)
                .done((success) => {
					vm.data = success.appAbsenceStartInfo;
					vm.flowWorkFlag(success.flowWorkFlag);
					let hdAppSetInput: any[] = vm.data.hdAppSet.dispNames;
					vm.workTypeOrigin = success.appAbsenceStartInfo.selectedWorkTypeCD;
					vm.workTimeOrigin = success.appAbsenceStartInfo.selectedWorkTimeCD;
					// B3_2
					vm.selectedType(success.applyForLeave.vacationInfo.holidayApplicationType);
					vm.fetchData(success.appAbsenceStartInfo);
					vm.appDispInfoStartupOutput(success.appAbsenceStartInfo.appDispInfoStartupOutput);
					vm.fetchDataAppForLeave(success.applyForLeave);
					vm.checkCondition32(success.applyForLeave);
					if (hdAppSetInput && hdAppSetInput.length > 0) {
						const hdAppSetTemp = _.filter(hdAppSetInput, (x) => {
							return x.holidayAppType !== 6
						});
						vm.hdAppSet(hdAppSetTemp);
					}
					
					vm.printContentOfEachAppDto().opPrintContentApplyForLeave = {
						appAbsenceStartInfoOutput: vm.data,
						applyForLeave: success.applyForLeave
					};
					

					if (vm.printContentOfEachAppDto().opPrintContentApplyForLeave.appAbsenceStartInfoOutput.leaveComDayOffManas.length > 0) {
						vm.data.leaveComDayOffManas = _.map(vm.printContentOfEachAppDto().opPrintContentApplyForLeave.appAbsenceStartInfoOutput.leaveComDayOffManas, (x: any) => {
							x.dateOfUse = new Date(x.dateOfUse).toISOString();
							x.outbreakDay = new Date(x.outbreakDay).toISOString();
							return x;
						});
					}
					if (vm.printContentOfEachAppDto().opPrintContentApplyForLeave.appAbsenceStartInfoOutput.payoutSubofHDManas.length > 0) {
						vm.data.payoutSubofHDManas = _.map(vm.printContentOfEachAppDto().opPrintContentApplyForLeave.appAbsenceStartInfoOutput.payoutSubofHDManas, (x: any) => {
							x.dateOfUse = new Date(x.dateOfUse).toISOString();
							x.outbreakDay = new Date(x.outbreakDay).toISOString();
							return x;
						});
					}
					vm.checkCondition(vm.data);

					if (vm.data.workTypeNotRegister) {
						vm.workTypeLst().push(new WorkType({workTypeCode: vm.data.selectedWorkTypeCD, name: vm.data.selectedWorkTypeCD + ' マスタ未登録'}))
						vm.workTypeLst(_.sortBy(vm.workTypeLst(), ['workTypeCode']));
						vm.selectedWorkTypeCD(vm.data.selectedWorkTypeCD);
					}

					vm.isInit(false);
                }).fail((error) => {
                    vm.$dialog.error({ messageId: error.messageId, messageParams: error.parameterIds });
                }).always(() => vm.$blockui('hide'));
		};
		
		private fetchDataAppForLeave(param: any) {
			const vm = this;
			
			// B4_2
			vm.selectedWorkTypeCD(param.reflectFreeTimeApp.workInfo.workType);
			// B5_1
			vm.isChangeWorkHour(param.reflectFreeTimeApp.workChangeUse === 0 ? false : true);
			// B5_5
			// 就業時間帯コード
			vm.selectedWorkTimeCD(param.reflectFreeTimeApp.workInfo.workTime);

			// 表示名
			if (vm.selectedWorkTimeCD()) {
				let qr: any[] = _.filter(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst, { 'worktimeCode': vm.selectedWorkTimeCD() });
				if (qr.length > 0) {
					vm.selectedWorkTimeName(qr[0].workTimeDisplayName.workTimeName);
				} else {
					vm.selectedWorkTimeName(null);
				}
			}

			let time1: any[] = _.filter(param.reflectFreeTimeApp.workingHours, { 'workNo': 1 });
			if (time1.length > 0) {
				// B5_7
				vm.startTime1(time1[0].timeZone.startTime);
				// B5_9
				vm.endTime1(time1[0].timeZone.endTime);
			}
			let time2: any[] = _.filter(param.reflectFreeTimeApp.workingHours, { 'workNo': 2 });
			if (time2.length > 0) {
				// B5_11
				vm.startTime2(time2[0].timeZone.startTime);
				// B5_13
				vm.endTime2(time2[0].timeZone.endTime);
			}

			if (vm.selectedType() === 6) {
				// B8_3
				vm.over60H(param.reflectFreeTimeApp.timeDegestion.overtime60H);
				// B8_5
				vm.timeOff(param.reflectFreeTimeApp.timeDegestion.timeOff);
				// B8_7
				vm.annualTime(param.reflectFreeTimeApp.timeDegestion.timeAnualLeave);
				// B8_9
				vm.childNursing(param.reflectFreeTimeApp.timeDegestion.childTime);
				// B8_11
				vm.nursing(param.reflectFreeTimeApp.timeDegestion.nursingTime);
			} else {
				// B8_3
				vm.over60H(null);
				// B8_5
				vm.timeOff(null);
				// B8_7
				vm.annualTime(null);
				// B8_9
				vm.childNursing(null);
				// B8_11
				vm.nursing(null);
			}

			if (vm.selectedType() === 3 && param.vacationInfo.info.applyForSpeLeave != null) {
				// B9_2
				if (param.vacationInfo.info.applyForSpeLeave.relationshipCD !== null) {
					vm.selectedDateSpec(param.vacationInfo.info.applyForSpeLeave.relationshipCD);
				}
				// B9_3
				vm.isCheckMourn(param.vacationInfo.info.applyForSpeLeave.mournerFlag);
				// B9_5
				vm.relationshipReason(param.vacationInfo.info.applyForSpeLeave.relationshipReason);
			}

			if (param.vacationInfo.info.datePeriod) {
				let textDate = "";

				if (param.vacationInfo.info.datePeriod.startDate) {
					textDate = param.vacationInfo.info.datePeriod.startDate;
				}
				if (param.vacationInfo.info.datePeriod.endDate && (param.vacationInfo.info.datePeriod.endDate !== param.vacationInfo.info.datePeriod.startDate)) {
					textDate = textDate.concat("～").concat(param.vacationInfo.info.datePeriod.endDate);
				}
				vm.dateBeforeChange(vm.$i18n('KAF006_85', [textDate]));
			}
		};

        private fetchData(data: any) {
			const vm = this;
			let workTypeLstOutput = data.workTypeLst;
			vm.flowWorkFlag(data.flowWorkFlag);
			
			// Get value workType before change workType List
			let workTypesBefore = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});
			vm.workTypeBefore(workTypesBefore.length > 0 ? workTypesBefore[0] : null);
			
			vm.data = data;
			vm.workTypeLst(_.map(workTypeLstOutput, item => new WorkType({workTypeCode: item.workTypeCode, name: item.workTypeCode + ' ' + item.name})));

			let workTypesAfter = _.filter(vm.data.workTypeLst, {'workTypeCode': data.selectedWorkTypeCD});
			vm.workTypeAfter(workTypesAfter.length > 0 ? workTypesAfter[0] : null);

			// vm.appDispInfoStartupOutput(data.appDispInfoStartupOutput);
			vm.specAbsenceDispInfo(data.specAbsenceDispInfo);

			if (data.requiredVacationTime) {
				vm.requiredVacationTime(data.requiredVacationTime);
			}

			if (data.remainVacationInfo) {
				// vm.yearRemain(data.remainVacationInfo.yearRemain);
				// vm.subHdRemain(data.remainVacationInfo.subHdRemain);
				// vm.subVacaRemain(data.remainVacationInfo.subVacaRemain);
				// vm.remainingHours(data.remainVacationInfo.remainingHours);
				vm.grantDate(data.remainVacationInfo.grantDate);
                vm.grantDays(data.remainVacationInfo.grantDays);
				vm.fetchRemainTime(data.remainVacationInfo);
			}

			vm.requiredVacationTime(data.requiredVacationTime);

			if (vm.selectedType() === 6) {
				vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", vm.requiredVacationTime()));
			}

			if (vm.specAbsenceDispInfo()) {
				vm.dateSpecHdRelationLst(vm.specAbsenceDispInfo().dateSpecHdRelationLst ? vm.specAbsenceDispInfo().dateSpecHdRelationLst : []);
				
				if (vm.dateSpecHdRelationLst() && vm.dateSpecHdRelationLst().length > 0 && !vm.selectedDateSpec()) {
					vm.selectedDateSpec(vm.dateSpecHdRelationLst()[0].relationCD);
				}
			}

			if (data.leaveComDayOffManas) {
				vm.leaveComDayOffManas(data.leaveComDayOffManas);
			}
			if (data.payoutSubofHDManas) {
				vm.payoutSubofHDManagements(data.payoutSubofHDManas);
			}
		};
		
		// fetchRemainTime(remainVacationInfo: any) {
		// 	const vm = this;

		// 	// set over60HHourRemain
		// 	if (remainVacationInfo.over60HHourRemain) {
		// 		vm.over60HHourRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.over60HHourRemain));
		// 	} else {
        //         vm.over60HHourRemain(nts.uk.time.format.byId("Time_Short_HM", 0));
        //     }

		// 	// set subVacaHourRemain
		// 	if (remainVacationInfo.subVacaHourRemain) {
		// 		vm.subVacaHourRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.subVacaHourRemain));
		// 	} else {
        //         vm.subVacaHourRemain(nts.uk.time.format.byId("Time_Short_HM", 0));
        //     }

		// 	// set yearRemain
		// 	if (remainVacationInfo.yearRemain && remainVacationInfo.yearRemain > 0) {
		// 		if (remainVacationInfo.yearHourRemain && remainVacationInfo.yearHourRemain > 0) {
		// 			vm.timeYearLeave(remainVacationInfo.yearRemain.toString().concat("日と").concat(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.yearHourRemain)));
		// 		} else {
		// 			vm.timeYearLeave(remainVacationInfo.yearRemain.toString().concat("日"));
		// 		}
		// 	} else {
		// 		vm.timeYearLeave(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.yearHourRemain));
		// 	}

		// 	// set childNursingRemain
		// 	if (remainVacationInfo.childNursingRemain && remainVacationInfo.childNursingRemain > 0) {
		// 		if (remainVacationInfo.childNursingHourRemain && remainVacationInfo.childNursingHourRemain > 0) {
		// 			vm.childNursingRemain(remainVacationInfo.childNursingRemain.toString().concat("日と").concat(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.childNursingHourRemain)));
		// 		} else {
		// 			vm.childNursingRemain(remainVacationInfo.childNursingRemain.toString().concat("日"));
		// 		}
		// 	} else {
		// 		vm.childNursingRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.childNursingHourRemain));
		// 	}

		// 	// set nursingRemain
		// 	if (remainVacationInfo.nursingRemain && remainVacationInfo.nursingRemain > 0) {
		// 		if (remainVacationInfo.nursingRemain && remainVacationInfo.nirsingHourRemain > 0) {
		// 			vm.nursingRemain(remainVacationInfo.nursingRemain.toString().concat("日と").concat(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.nirsingHourRemain)));
		// 		} else {
		// 			vm.nursingRemain(remainVacationInfo.nursingRemain.toString().concat("日"));
		// 		}
		// 	} else {
		// 		vm.nursingRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.nirsingHourRemain));
		// 	}
		// };

		fetchRemainTime(remainVacationInfo: any) {
            const vm = this;

            // set over60HHourRemain
            vm.over60HHourRemain(vm.formatRemainNumber(0, remainVacationInfo.over60HHourRemain));

            // set subVacaHourRemain
            vm.subVacaHourRemain(vm.formatRemainNumberSubVaca(remainVacationInfo.subHdRemain, remainVacationInfo.subVacaHourRemain, remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement));

            // set subVacaRemain
            vm.subVacaRemain(vm.formatRemainNumber(remainVacationInfo.subVacaRemain, 0));

            // set yearRemain
            vm.timeYearLeave(vm.formatRemainNumber(remainVacationInfo.yearRemain, remainVacationInfo.yearHourRemain));

            // set childNursingRemain
            vm.childNursingRemain(vm.formatRemainNumber(remainVacationInfo.childNursingRemain, remainVacationInfo.childNursingHourRemain));

            // set nursingRemain
            vm.nursingRemain(vm.formatRemainNumber(remainVacationInfo.nursingRemain, remainVacationInfo.nirsingHourRemain));

            // set yearRemain
            vm.yearRemain(vm.formatRemainNumber(remainVacationInfo.yearRemain, remainVacationInfo.yearHourRemain));

            // set remainingHours
            vm.remainingHours(vm.formatRemainNumber(remainVacationInfo.remainingHours, 0));

            // set subHdRemain
            vm.subHdRemain(vm.formatSubHdRemain(remainVacationInfo.subHdRemain, 0, remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement));
        }

		formatRemainNumberSubVaca(day: any, time: any, manage: any): string {
            const vm = this;
            if (time) {
                let timeString = nts.uk.time.format.byId("Time_Short_HM", time);
                if (day) {
                    return vm.$i18n('KAF006_100', [day.toString(), timeString]);
                }
                return timeString;
            }

            return manage ? nts.uk.time.format.byId("Time_Short_HM", time) : vm.$i18n('KAF006_46', [day.toString()]);
        }

        formatRemainNumber(day: any, time: any): string {
            const vm = this;
            if (time) {
                let timeString = nts.uk.time.format.byId("Time_Short_HM", time);
                if (day) {
                    return vm.$i18n('KAF006_100', [day.toString(), timeString]);
                }
                return timeString;
            }

            return vm.$i18n('KAF006_46', [day.toString()]);
        }

		formatSubHdRemain(day: any, time: any, manage: any) {
            const vm = this;
             if (manage) {
                 return nts.uk.time.format.byId("Time_Short_HM", time);
             } else {
                 return vm.$i18n('KAF006_46', [day.toString()]);
             }
        }

		public openKDL003() {
			const vm = this;
			let workTypeCodes = _.map(vm.data.workTypeLst, 'workTypeCode');
			let workTimeCodes = _.map(vm.data.workTimeLst, 'workNo');

			nts.uk.ui.windows.setShared('parentCodes', {
                workTypeCodes: workTypeCodes,
                selectedWorkTypeCode: vm.selectedWorkTypeCD(),
                workTimeCodes: [],
                selectedWorkTimeCode: vm.selectedWorkTimeCD(),
			}, true);
			
			nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed(function(): any {
                //view all code of selected item 
                let childData = nts.uk.ui.windows.getShared('childData');
                if (childData) {
					vm.selectedWorkTypeCD(childData.selectedWorkTypeCode);
					vm.selectedWorkTimeCD(childData.selectedWorkTimeCode);
					vm.selectedWorkTimeName(childData.selectedWorkTimeName);

					// vm.startTime1(childData.first.start);
					// vm.endTime1(childData.first.end);
					// vm.startTime2(childData.second.start);
					// vm.endTime2(childData.second.end);
                }
            });
		};

		public openKDL035() {
			const vm = this;

			let workType = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});

			let params: any = {
				// 社員ID
				employeeId: ko.toJS(vm.application().employeeIDLst()[0]),

				// 申請期間
				period: {startDate: vm.application().opAppStartDate(), endDate: vm.application().opAppEndDate()},

				// 日数単位（1.0 / 0.5）
				daysUnit: workType[0].workAtr === 0 ? 1.0 : 0.5,

				// 対象選択区分（自動 / 申請 / 手動
				targetSelectionAtr: 1,

				// List<表示する実績内容>
				actualContentDisplayList: vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst,

				// List<振出振休紐付け管理>
				managementData: ko.toJS(vm.payoutSubofHDManagements)
			};
			Kaf006ShrViewModel.openDialogKDL035(params, vm);
			// vm.payoutSubofHDManagements(payoutMana);
		}

		public openKDL036() {
			const vm = this;

			let workType = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});

			let params: any = {
				// 社員ID
				employeeId: ko.toJS(vm.application().employeeIDLst()[0]),

				// 申請期間
				period: {startDate: vm.application().opAppStartDate(), endDate: vm.application().opAppEndDate()},

				// 日数単位（1.0 / 0.5）
				daysUnit: workType[0].workAtr === 0 ? 1.0 : 0.5,

				// 対象選択区分（自動 / 申請 / 手動
				targetSelectionAtr: 1,

				// List<表示する実績内容>
				actualContentDisplayList: vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opActualContentDisplayLst,

				// List<振出振休紐付け管理>
				managementData: ko.toJS(vm.leaveComDayOffManas)
			};
			Kaf006ShrViewModel.openDialogKDL036(params, vm);
			// vm.leaveComDayOffManas(leaveMana);
		}

		checkCondition(data: any) {
			const vm = this;

			vm.checkCondition21(data);
			vm.checkCondition22(data);
			vm.checkCondition23(data);
			vm.checkCondition24(data);
			
			vm.checkCondition19(data);
			vm.checkCondition11(data);
			vm.checkCondition12(data);
			vm.checkCondition14(data);
			vm.checkCondition15(data);


			vm.checkCondition30(data);
			vm.checkCondition1(data);
			vm.checkCondition31(data);
	
			if (vm.selectedType() === 3) {
				vm.checkCondition6(data);
				vm.checkCondition7(data);
				vm.checkCondition8(data);
				vm.checkCondition9(data);
			}
		}

		validate() {
			const vm = this;
			if (vm.condition11() && vm.condition30()) {
				// if (vm.isChangeWorkHour() && vm.selectedWorkTimeCD()) {
				// 	if (!vm.checkTimeValid(vm.startTime1) && !vm.checkTimeValid(vm.endTime1)) {
				// 		vm.$dialog.error({messageId: "Msg_307"});
				// 		return false;
				// 	}
				// }
				if (!vm.checkTimeValid(vm.startTime1) && vm.checkTimeValid(vm.endTime1)) {
					vm.$dialog.error({messageId: "Msg_307"});
					return false;
				}
				if (vm.checkTimeValid(vm.startTime1) && !vm.checkTimeValid(vm.endTime1)) {
					vm.$dialog.error({messageId: "Msg_307"});
					return false;
				}
				if (vm.startTime1() > vm.endTime1()) {
					vm.$dialog.error({messageId: "Msg_307"});
					return false;
				}

				if (vm.condition12()) {
					if (vm.startTime2() > vm.endTime2()) {
						vm.$dialog.error({messageId: "Msg_307"});
						return false;
					}
					if (vm.checkTimeValid(vm.startTime2) && vm.endTime1() > vm.startTime2()) {
						vm.$dialog.error({messageId: "Msg_581"});
						return false;
					}
					if (!vm.checkTimeValid(vm.startTime2) && vm.checkTimeValid(vm.endTime2)) {
						vm.$dialog.error({messageId: "Msg_307"});
						return false;
					}
					if (vm.checkTimeValid(vm.startTime2) && !vm.checkTimeValid(vm.endTime2)) {
						vm.$dialog.error({messageId: "Msg_307"});
						return false;
					}
				}
			}

			return true;
		}

		private checkTimeValid(time: KnockoutObservable<number>): boolean {
			if (_.isNil(time()) || time() === "") {
				return false;
			}
			return true;
		}

		/**
		 * Create Data for for Vacation Application
		 */
		createDataVacationApp(): any {
			const vm = this;
			
			// application common

			// A4_2
			// Holiday Type
			let holidayAppType = vm.selectedType();

			// A5_2
			// List of workType
			let workType = vm.selectedWorkTypeCD();

			// A6_5
			// worktTime
			let workTime = vm.selectedWorkTimeCD();

			// A6_1
			let workChangeUse = vm.isChangeWorkHour();

			let startTime1 = vm.startTime1();
			let endTime1 = vm.endTime1();
			let startTime2 = vm.startTime2();
			let endTime2 = vm.endTime2();

			let workingHours = [];

			if (startTime1 != null && endTime1 != null && startTime1 !== "" && endTime1 !== "") {
				workingHours.push({
					workNo: 1,
					timeZone: {
						startTime: startTime1,
						endTime: endTime1
					}
				});
			}
			if (startTime2 != null && endTime2 != null && startTime2 !== "" && endTime2 !== "") {
				workingHours.push({
					workNo: 2,
					timeZone: {
						startTime: startTime2,
						endTime: endTime2
					}
				});
			}

			let timeDegestion = {};
			if (vm.selectedType() === 6) {
				timeDegestion = {
					overtime60H: vm.over60H(),
					nursingTime: vm.nursing(),
					childTime: vm.childNursing(),
					timeOff: vm.timeOff(),
					timeSpecialVacation: null,
					timeAnualLeave: vm.annualTime(),
					specialVacationFrameNO: null
				};
			}

			let applyForSpeLeaveOptional = null;
			if (vm.selectedType() === 3) {
                applyForSpeLeaveOptional = {
                    mournerFlag: null,
                    relationshipCD: null,
                    relationshipReason: null
                };
                if (vm.condition6()) {
                    applyForSpeLeaveOptional.relationshipCD = vm.selectedDateSpec();
                }
                if (vm.condition7()) {
                    applyForSpeLeaveOptional.mournerFlag = vm.isCheckMourn();
                }
                if (vm.condition8()) {
                    applyForSpeLeaveOptional.relationshipReason = vm.relationshipReason();
                }
            }


			let appAbsence = {
				reflectFreeTimeApp: {
					workingHours: workingHours,
					timeDegestion: timeDegestion,
					workInfo: {
						workType: workType,
						workTime: workTime
					},
					workChangeUse: workChangeUse ? 1 : 0
				},
				vacationInfo: {
					holidayApplicationType: holidayAppType,
					info: {
						datePeriod: null,
						applyForSpeLeave: applyForSpeLeaveOptional
					}
				}
			};

			return appAbsence;
		}


		/**
		 * Update data for AppAbsenceStartInfo
		 */
		updateAppAbsenceStartInfo() {
			const vm = this;

			if (vm.selectedType() === 1) {
				if (vm.leaveComDayOffManas().length > 0) {
					vm.data.leaveComDayOffManas = _.map(vm.leaveComDayOffManas(), (x: any) => {
						x.dateOfUse = new Date(x.dateOfUse).toISOString();
						x.outbreakDay = new Date(x.outbreakDay).toISOString();
						return x;
					});
				}
				if (vm.payoutSubofHDManagements().length > 0) {
					vm.data.payoutSubofHDManas = _.map(vm.payoutSubofHDManagements(), (x: any) => {
						x.dateOfUse = new Date(x.dateOfUse).toISOString();
						x.outbreakDay = new Date(x.outbreakDay).toISOString();
						return x;
					});
				}
			}
		}
		
		handleErrorCustom(failData: any): any {
			const vm = this;
			if (failData) {
				if(failData.messageId == "Msg_26") {
					vm.$dialog.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
					.then(() => {
						vm.$jump("com", "/view/ccg/008/a/index.xhtml");	
					});
					return $.Deferred().resolve(false);		
				}
				vm.$dialog.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
				return $.Deferred().resolve(false);		
			}
			return $.Deferred().resolve(true);
		}

		handleConfirmMessage(listMes: any): any {
			const vm = this;
			if(_.isEmpty(listMes)) {
				return $.Deferred().resolve(true);
			}
			let msg = listMes[0];

			return vm.$dialog.confirm({ messageId: msg.msgID, messageParams: msg.paramLst })
			.then((value) => {
				if (value === 'yes') {
					return vm.handleConfirmMessage(_.drop(listMes));
				} else {
					return $.Deferred().resolve(false);
				}
			});
		}

		openKAF006C() {
			const vm = this;
			nts.uk.ui.windows.setShared('KAF006C_PARAMS', { appAbsenceStartInfoOutput: vm.data, applyForLeave: vm.createDataVacationApp() });
			nts.uk.ui.windows.sub.modal("/view/kaf/006/c/index.xhtml").onClosed(() => {
				let result = nts.uk.ui.windows.getShared('KAF006C_RESULT'),
					viewModel = nts.uk.ui._viewModel.content;
				if(result) {
					let cacheLst = __viewContext.transferred.value.listAppMeta,
						index = _.indexOf(cacheLst, viewModel.currentApp()) + 1,
					 	preLst = _.slice(cacheLst, 0, index),
						afterLst = _.slice(cacheLst, index);
					__viewContext.transferred.value.listAppMeta = _.concat(_.concat(preLst, [result.appID]), afterLst);
					viewModel.listApp(__viewContext.transferred.value.listAppMeta);
					viewModel.currentApp(result.appID);
					viewModel.loadData();
				}
			});
		}

		checkCondition11(data: any) {
			const vm = this;
			if (vm.data && vm.data.workHoursDisp) {
				vm.condition11(true);
				return true;
			}
			vm.condition11(false);
			return false;
		}

		checkCondition12(data: any) {
            const vm = this;
			let isDisplayWorkTime2 = _.filter(vm.data.workTimeLst, { 'workNo': 2 }).length > 0;
            if (vm.data && vm.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles && vm.selectedWorkTimeCD() && vm.isDispTime2ByWorkTime() && isDisplayWorkTime2) {
                vm.condition12(true);
                return true;
            }
            vm.condition12(false);
            return false;
        }

		// checkCondition30(data: any) {
		// 	const vm = this;

		// 	if (vm.data && vm.data.vacationApplicationReflect && vm.data.vacationApplicationReflect.workAttendanceReflect.reflectAttendance === 1) {
		// 		vm.condition30(true);
		// 		return true;
		// 	}
		// 	vm.condition30(false);
		// 	return false;
		// }
		checkCondition30(data: any) {
            const vm = this;

            let listAbs = [1, 2, 3, 4, 5, 6, 8, 9, 12, 13];
            let workTypeList = vm.data.workTypeLst;
            let workTypeFilter = _.filter(workTypeList, (x: any) => x.workTypeCode === vm.selectedWorkTypeCD());
            if (workTypeFilter.length > 0) {
                let workType = workTypeFilter[0];
                // 選択している勤務種類.1日の勤務.勤務区分　＝　1日
                if (workType.workAtr == 0) {
					vm.condition30(false);
                    return false;
                } else {
                    // 午前の勤務種類　AND　午後の勤務種類が休み（休日、年休、積立年休、特別休暇、欠勤、代休、振休、時間消化休暇、休職、休業）
                    if (_.includes(listAbs, workType.morningCls) && _.includes(listAbs, workType.afternoonCls)) {
                        vm.condition30(false);
						return false;
                    }
                }

                // 上記以外
				vm.condition30(true);
                return true;
            }

			vm.condition30(false);
            return false;
        }

		checkCondition19(data: any) {
			const vm = this;
			if (vm.data && vm.data.vacationApplicationReflect) {
				if (vm.data.vacationApplicationReflect.timeLeaveReflect.superHoliday60H === 1 
					&& vm.data.remainVacationInfo.overtime60hManagement.overrest60HManagement === 1) {
						vm.condition19Over60(true);
					} else {
						vm.condition19Over60(false);
						vm.over60H(null);
					}
				if (vm.data.vacationApplicationReflect.timeLeaveReflect.substituteLeaveTime === 1 
					&& vm.data.remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement === 1) {
						vm.condition19Substitute(true);
					} else {
						vm.condition19Substitute(false);
						vm.timeOff(null);
					}
				if (vm.data.vacationApplicationReflect.timeLeaveReflect.annualVacationTime === 1 
					&& vm.data.remainVacationInfo.annualLeaveManagement.timeAnnualLeaveManage === 1) {
						vm.condition19Annual(true);
					} else {
						vm.condition19Annual(false);
						vm.annualTime(null);
					}
				if (vm.data.vacationApplicationReflect.timeLeaveReflect.childNursing === 1 
					&& vm.data.remainVacationInfo.nursingCareLeaveManagement.childNursingManagement === 1) {
						vm.condition19ChildNursing(true);
					} else {
						vm.condition19ChildNursing(false);
						vm.childNursing(null);
					}
				if (vm.data.vacationApplicationReflect.timeLeaveReflect.nursing === 1 
					&& vm.data.remainVacationInfo.nursingCareLeaveManagement.longTermCareManagement === 1) {
						vm.condition19Nursing(true);
					} else {
						vm.condition19Nursing(false);
						vm.nursing(null);
					}
			}
		}

		checkCondition6(data: any) {
			const vm = this;
			if (vm.data && vm.data.specAbsenceDispInfo && vm.data.specAbsenceDispInfo.specHdForEventFlag && vm.data.specAbsenceDispInfo.specHdEvent.maxNumberDay === 2) {
				vm.condition6(true);
				return true;
			}
			vm.condition6(false);
		}

		checkCondition7(data: any) {
			const vm = this;
			if (vm.data && vm.data.specAbsenceDispInfo && vm.data.specAbsenceDispInfo.specHdForEventFlag && vm.data.specAbsenceDispInfo.specHdEvent.maxNumberDay === 2 && vm.data.specAbsenceDispInfo.specHdEvent.makeInvitation) {
				vm.condition7(true);
				return true;
			}
			vm.condition7(false);
		}

		checkCondition8(data: any) {
			const vm = this;
			if (vm.data && vm.data.specAbsenceDispInfo) {
				let dateSpecHdRelationLst = vm.data.specAbsenceDispInfo.dateSpecHdRelationLst;
				let selectedRela = _.filter(dateSpecHdRelationLst, { 'relationCD': vm.selectedDateSpec() });

				if (vm.selectedDateSpec() && selectedRela.length > 0 && selectedRela[0].threeParentOrLess) {
					vm.condition8(true);
					return true;
				}
			}
			vm.condition8(false);
		}

		checkCondition9(data: any) {
			const vm = this;
			if (vm.data && vm.data.specAbsenceDispInfo && vm.data.specAbsenceDispInfo.specHdForEventFlag) {
				vm.condition9(true);
				return true;
			}
			vm.condition9(false);
		}

		checkCondition15(data: any) {
			const vm = this;

			let workType = null;
			if (!vm.selectedWorkTypeCD()) {
				vm.condition15(false);
				return false;
			} else {
				let workTypes = _.filter(vm.data.workTypeLst, { 'workTypeCode': vm.selectedWorkTypeCD() });
				if (workTypes.length > 0) {
					workType = workTypes[0];
				}
			}

			if (vm.data && vm.data.remainVacationInfo.substituteLeaveManagement.linkingManagement === 1 && workType) {
				if (workType.workAtr === 0 && workType.oneDayCls === 6) {
					vm.condition15(true);
					return true;
				}
				if (workType.workAtr === 1 && (workType.morningCls === 6 || workType.afternoonCls === 6)) {
					vm.condition15(true);
					return true;
				}
			}
			vm.condition15(false);
		}

		checkCondition14(data: any) {
			const vm = this;

			let workType = null;
			if (!vm.selectedWorkTypeCD()) {
				vm.condition14(false);
				return false;
			} else {
				let workTypes = _.filter(vm.data.workTypeLst, { 'workTypeCode': vm.selectedWorkTypeCD() });
				if (workTypes.length > 0) {
					workType = workTypes[0];
				}
			}

			if (vm.data && workType) {
				if (workType.workAtr === 0 && workType.oneDayCls === 8) {
					vm.condition14(true);
					return true;
				}
				if (workType.workAtr === 1 && (workType.morningCls === 8 || workType.afternoonCls === 8)) {
					vm.condition14(true);
					return true;
				}
			}
			vm.condition14(false);
		}

		checkCondition21(data: any) {
			const vm = this;
			if (vm.data && vm.data.remainVacationInfo.annualLeaveManagement.annualLeaveManageDistinct === 1) {
				vm.condition21(true);
				return true;
			}
			vm.condition21(false);
			// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 0}));
		}

		checkCondition22(data: any) {
			const vm = this;
			if (vm.data && vm.data.remainVacationInfo.substituteLeaveManagement.substituteLeaveManagement === 1) {
				vm.condition22(true);
				return true;
			}
			vm.condition22(false);
			// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 1}));
		}

		checkCondition23(data: any) {
			const vm = this;
			if (vm.data && vm.data.remainVacationInfo.holidayManagement.holidayManagement === 1) {
				vm.condition23(true);
				return true;
			}
			vm.condition23(false);
		}

		checkCondition24(data: any) {
			const vm = this;
			if (vm.data && vm.data.remainVacationInfo.accumulatedRestManagement.accumulatedManage === 1) {
				vm.condition24(true);
				return true;
			}
			vm.condition24(false);
			// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 4}));
		}

		checkCondition1(data: any) {
			const vm = this;
			if (vm.data && vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet && vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst) {
				let targetWorkType = _.filter(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst, { 'appType': 1 });
				if (targetWorkType.length > 0) {
					if (_.filter(targetWorkType, { 'opHolidayAppType': 0 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 0 })[0].opHolidayTypeUse) {
						vm.condition1_0(true);
					} else {
						vm.condition1_0(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 0}));
					}

					if (_.filter(targetWorkType, { 'opHolidayAppType': 1 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 1 })[0].opHolidayTypeUse) {
						vm.condition1_1(true);
					} else {
						vm.condition1_1(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 1}));
					}

					if (_.filter(targetWorkType, { 'opHolidayAppType': 2 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 2 })[0].opHolidayTypeUse) {
						vm.condition1_2(true);
					} else {
						vm.condition1_2(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 2}));
					}

					if (_.filter(targetWorkType, { 'opHolidayAppType': 3 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 3 })[0].opHolidayTypeUse) {
						vm.condition1_3(true);
					} else {
						vm.condition1_3(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 3}));
					}

					if (_.filter(targetWorkType, { 'opHolidayAppType': 4 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 4 })[0].opHolidayTypeUse) {
						vm.condition1_4(true);
					} else {
						vm.condition1_4(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 4}));
					}

					if (_.filter(targetWorkType, { 'opHolidayAppType': 5 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 5 })[0].opHolidayTypeUse) {
						vm.condition1_5(true);
					} else {
						vm.condition1_5(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 5}));
					}

					if (_.filter(targetWorkType, { 'opHolidayAppType': 6 }).length > 0 && !_.filter(targetWorkType, { 'opHolidayAppType': 6 })[0].opHolidayTypeUse) {
						vm.condition1_6(true);
					} else {
						vm.condition1_6(false);
						// vm.hdAppSet(_.filter(vm.hdAppSet(), (x) => {return x.holidayAppType !== 6}));
					}
				}
			}
		}

		checkCondition31(data: any) {
			const vm = this;
			if (vm.selectedType() === 1) {
				let reflLst = vm.appDispInfoStartupOutput().appDetailScreenInfo.application.reflectionStatus.listReflectionStatusOfDay;
				if (_.filter(reflLst, { 'actualReflectStatus': 3 }).length === 0) {
					vm.condition31(true);
					return true;
				}
			}
			vm.condition31(false);
		}

		checkCondition32(data: any) {
			const vm = this;
			if (data.vacationInfo.info.datePeriod && (vm.checkTimeValid(ko.observable(data.vacationInfo.info.datePeriod.startDate)) || vm.checkTimeValid(ko.observable(data.vacationInfo.info.datePeriod.endDate)))) {
				vm.condition32(true);
				return true;
			}

			vm.condition32(false);
		}

		openKDL020() {
            let vm = this;
			nts.uk.ui.windows.setShared('KDL020_DATA', vm.application().employeeIDLst());
			if (vm.application().employeeIDLst().length > 1)
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 1040, height: 660 });
			else
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 730, height: 660 });
        }

        openKDL029() {
            let vm = this;
			nts.uk.ui.windows.setShared('KDL029_DATA', vm.application().employeeIDLst());
			if (vm.application().employeeIDLst().length > 1)
				nts.uk.ui.windows.sub.modal("/view/kdl/029/a/index.xhtml",{  width: 1060, height: 600 });
			else
				nts.uk.ui.windows.sub.modal("/view/kdl/029/a/index.xhtml",{  width: 710, height: 600 });
        }

        openKDL005() {
            let vm = this;
            let data = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: moment(new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)).format("YYYYMMDD")
            }
            nts.uk.ui.windows.setShared('KDL005_DATA', data.employeeIds);
            if (data.employeeIds.length > 1) {
				nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml", {  width: 1160, height: 640 });
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml",{  width: 860, height: 640 });
            }
        }

        public openKDL051() {
            let vm = this;
            let param = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)
            };

            Kaf006ShrViewModel.openKDL051(param);
        }

        public openKDL052() {
            let vm = this;
            let param = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: moment(new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)).format("YYYYMMDD")
            };

            Kaf006ShrViewModel.openKDL052(param);
        }

        public openKDL017() {
            let vm = this;
            let param = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: moment(new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)).format("YYYYMMDD")
            };
            
            Kaf006ShrViewModel.openKDL017(param);
        }

        public openKDL009() {
            let vm = this;
            let param = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: moment(new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)).format("YYYYMMDD")
            };

            Kaf006ShrViewModel.openKDL009(param); 
        }
    };

    const API = {
		initPageB: 'at/request/application/appforleave/getAppForLeaveStartB',
		getAllAppForLeave: 'at/request/application/appforleave/getAllAppForLeave',
		changeRela: 'at/request/application/appforleave/changeRela',
		changeWorkTime: 'at/request/application/appforleave/findChangeWorkTime',
		changeUseingWorkTime: 'at/request/application/appforleave/findChangeUsingWorkTime',
		checkVacationTyingManage: 'at/request/application/appforleave/checkVacationTyingManage',
		changeWorkType: 'at/request/application/appforleave/findChangeWorkType',
		checkBeforeUpdate: 'at/request/application/appforleave/checkBeforeUpdate',
		update: 'at/request/application/appforleave/update'
    };
}