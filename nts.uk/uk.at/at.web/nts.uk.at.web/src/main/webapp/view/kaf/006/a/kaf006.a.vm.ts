/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;
import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
import ApplicationDto = nts.uk.at.view.kaf006.shr.viewmodel.ApplicationDto;
import WorkType = nts.uk.at.view.kaf006.shr.viewmodel.WorkType;
import Kaf006ShrViewModel = nts.uk.at.view.kaf006.shr.viewmodel.Kaf006ShrViewModel;
import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

module nts.uk.at.view.kaf006_ref.a.viewmodel {

    @bean()
    export class Kaf006AViewModel extends Kaf000AViewModel {
        appType: KnockoutObservable<number> = ko.observable(AppType.ABSENCE_APPLICATION);
        isAgentMode: KnockoutObservable<boolean> = ko.observable(false);
        isSendMail: KnockoutObservable<boolean> = ko.observable(false);
        application: KnockoutObservable<Application> = ko.observable(new Application(this.appType()));
        data: any = null;
        hdAppSet: KnockoutObservableArray<any> = ko.observableArray([]);
        hdAppSetTmp: KnockoutObservableArray<any> = ko.observableArray([]);
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
        isFromOther: boolean = false;
        isEnableSwitchBtn: boolean = true;
        updateMode: KnockoutObservable<boolean> = ko.observable(true);
        isDispTime2ByWorkTime: KnockoutObservable<boolean> = ko.observable(false);
        grantDate: KnockoutObservable<string> = ko.observable(null);
        grantDays: KnockoutObservable<number> = ko.observable(0);
        grantDaysOfYear: KnockoutComputed<string>;

        // appDate
        checkAppDate: KnockoutObservable<boolean> = ko.observable(true);

        yearRemain: KnockoutObservable<string> = ko.observable();
        subHdRemain: KnockoutObservable<string> = ko.observable();
        // subVacaRemain: KnockoutObservable<string> = ko.observable();
        remainingHours: KnockoutObservable<string> = ko.observable();

        over60HHourRemain: KnockoutObservable<string> = ko.observable();
        subVacaHourRemain: KnockoutObservable<string> = ko.observable();
        subVacaRemain: KnockoutObservable<string> = ko.observable();
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

        // Condition
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
        condition10Substi: KnockoutObservable<boolean> = ko.observable(false);
        condition10Annual: KnockoutObservable<boolean> = ko.observable(false);
        condition10Accum: KnockoutObservable<boolean> = ko.observable(false);


        created(params: AppInitParam) {
            const vm = this;
            let dataTransfer: DataTransfer;
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					dataTransfer = __viewContext.transferred.value; // from spr		
					params = __viewContext.transferred.value;
				}
			}

            let empLst: Array<string> = [],
                dateLst: Array<string> = [];
            if (!_.isNil(_.get(dataTransfer, 'appDate'))) {
                dateLst.push(dataTransfer.appDate);
            }
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

            if (!_.isNil(dataTransfer)) {
                vm.application().appDate(dataTransfer.appDate);
                vm.application().opAppStartDate(dataTransfer.appDate);
                vm.application().opAppEndDate(dataTransfer.appDate);
            }
            if (!_.isNil(params)) {
                if (!_.isNil(params.baseDate)) {
                    vm.application().appDate(moment(params.baseDate).format('YYYY/MM/DD'));
                }
            }

            // Load data common
            vm.$blockui("show");
            vm.loadData(empLst, dateLst, vm.appType())
                .then((loadDataFlag: any) => {
                    if (loadDataFlag) {
                        let appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput);
                        return vm.$ajax(API.startNew, appDispInfoStartupOutput);
                    }
                }).then((successData: any) => {
                if (successData) {
                    vm.data = successData;
                    let hdAppSetInput: any[] = vm.data.hdAppSet.dispNames;
                    if (hdAppSetInput && hdAppSetInput.length > 0) {
                        vm.hdAppSetTmp(hdAppSetInput);
                    }

                    vm.checkCondition(vm.data);
                    vm.hdAppSet(ko.toJS(vm.hdAppSetTmp));
                }
            }).fail((error: any) => {
                if (error) {
                    vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
                }
            }).always(() => {
                vm.$blockui("hide");
            });

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
                    return vm.selectedWorkTimeCD() + " " + vm.selectedWorkTimeName();
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
                        vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
                    }
                }).always(() => {
                    vm.$blockui("hide");
                })
            });

            // check selected item
            vm.selectedType.subscribe(() => {
                // vm.selectedWorkTimeCD(null);
                // vm.selectedWorkTimeName(null);
                // vm.startTime1(null);
                // vm.startTime2(null);
                // vm.endTime1(null);
                // vm.endTime2(null);

                // vm.data.selectedWorkTypeCD = null;
                // vm.data.selectedWorkTimeCD = null;

                nts.uk.ui.errors.clearAll()

                let appDates = [];

                if (vm.checkTimeValid(vm.application().opAppStartDate)) {
                    appDates.push(vm.application().opAppStartDate());
                }
                if (vm.checkTimeValid(vm.application().opAppEndDate) && vm.application().opAppStartDate() !== vm.application().opAppEndDate()) {
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
                        $("#work-type-combobox").focus()
                        vm.fetchData(data);
                        // vm.selectedWorkTimeCD(null);
                        // vm.selectedWorkTimeName(null);
                        vm.appDispInfoStartupOutput(data.appDispInfoStartupOutput);
                        return data;
                    }
                }).then((data) => {
                    if (data) {
                        vm.checkCondition(data);
                        return data;
                    }
                }).then((data) => {
                    if (data) {
                        vm.selectedWorkTypeCD(vm.data.selectedWorkTypeCD);

                        return data;
                    }
                }).fail((error) => {
                    if (error) {
                        vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
                    }
                }).always(() => {
                    vm.$blockui("hide");
                })
            });

            // Subscribe workType value after change
            vm.selectedWorkTypeCD.subscribe(() => {
                if (_.isNil(vm.selectedWorkTypeCD()) || _.isEmpty(vm.workTypeLst())) {
                    return;
                }

                if ($('#relaReason').ntsError('hasError')) {
                    $('#relaReason').ntsError('clear');
                }

                // return;
                let commandCheckTyingManage = {
                    wtBefore: vm.workTypeBefore(),
                    wtAfter: vm.workTypeAfter(),
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
                        vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
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
                        vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
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
                        vm.$dialog.error({messageId: error.messageId, messageParams: error.parameterIds});
                    }
                }).always(() => {
                    vm.$blockui("hide");
                });
            });

            vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", vm.requiredVacationTime()));


            // disply condtion for item A10_3
            vm.isDispMourn = ko.computed(() => {
                if (vm.specAbsenceDispInfo()) {
                    if (vm.specAbsenceDispInfo().specHdForEventFlag && vm.specAbsenceDispInfo().specHdEvent.maxNumberDay === 2 && vm.specAbsenceDispInfo().specHdEvent.makeInvitation === 1) {
                        return true;
                    }
                }

                return false;
            });

            // change appDate for app type
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

        mounted() {
            const vm = this;

        }

        fetchData(data: any) {
            const vm = this;
            let workTypeLstOutput = data.workTypeLst;

            // Get value workType before change workType List
            let workTypesBefore = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});
            vm.workTypeBefore(workTypesBefore.length > 0 ? workTypesBefore[0] : null);

            vm.data = data;
            vm.workTypeLst(_.map(workTypeLstOutput, item => new WorkType({
                workTypeCode: item.workTypeCode,
                name: item.workTypeCode + ' ' + item.name
            })));

            let workTypesAfter = _.filter(vm.data.workTypeLst, {'workTypeCode': data.selectedWorkTypeCD});
            vm.workTypeAfter(workTypesAfter.length > 0 ? workTypesAfter[0] : null);

            // vm.selectedWorkTypeCD(data.selectedWorkTypeCD);


            // vm.appDispInfoStartupOutput(data.appDispInfoStartupOutput);

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
            if (data.requiredVacationTime) {
                vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", data.requiredVacationTime));
            } else {
                vm.timeRequired(nts.uk.time.format.byId("Time_Short_HM", 0));
            }

            vm.requiredVacationTime(data.requiredVacationTime);

            // vm.specAbsenceDispInfo(data.specAbsenceDispInfo);
            if (vm.specAbsenceDispInfo()) {
                vm.dateSpecHdRelationLst(vm.specAbsenceDispInfo().dateSpecHdRelationLst ? vm.specAbsenceDispInfo().dateSpecHdRelationLst : []);

                if (vm.dateSpecHdRelationLst() && vm.dateSpecHdRelationLst().length > 0) {
                    vm.selectedDateSpec(vm.dateSpecHdRelationLst()[0].relationCD);
                }
            }
        }

        checkCondition(data: any) {
            const vm = this;

            vm.checkCondition10(data);
            vm.checkCondition11(data);
            vm.checkCondition12(data);
            vm.checkCondition30(data);
            vm.checkCondition19(data);
            vm.checkCondition14(data);
            vm.checkCondition15(data);
            vm.checkCondition21(data);
            vm.checkCondition22(data);
            vm.checkCondition23(data);
            vm.checkCondition24(data);
            vm.checkCondition1(data);

            if (vm.selectedType() === 3) {
                vm.checkCondition6(data);
                vm.checkCondition7(data);
                vm.checkCondition8(data);
                vm.checkCondition9(data);
            }
        }

        // Register data
        register() {
            const vm = this;

            if (vm.selectedType() == undefined) {
                vm.$dialog.error({messageId: 'Msg_2202'});
                return;
            }

            // validate
            if (!vm.validate()) {
                return;
            }

            // Update appAbsenceStartInfo
            vm.updateAppAbsenceStartInfo();

            // Create data Vacation Request/ 休暇申請
            // vm.createDataVacationApp();
            let appDates = [];
            if (vm.application().opAppStartDate()) {
                appDates.push(vm.application().opAppStartDate());
            }
            ;
            if (vm.application().opAppEndDate()) {
                appDates.push(vm.application().opAppEndDate());
            }
            ;

            let holidayAppDates = [];

            let application: ApplicationDto = new ApplicationDto( 
                null,
                null,
                ko.toJS(vm.application().prePostAtr),
                vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst[0].sid,
                ko.toJS(vm.application().appType),
                ko.toJS(vm.application().appDate),
                vm.$user.employeeId,
                null,
                null,
                null,
                ko.toJS(vm.application().opReversionReason),
                ko.toJS(vm.application().opAppStartDate),
                ko.toJS(vm.application().opAppEndDate),
                ko.toJS(vm.application().opAppReason),
                ko.toJS(vm.application().opAppStandardReasonCD));

            let commandCheckRegister = {
                appAbsenceStartInfoDto: vm.data,
                applyForLeave: this.createDataVacationApp(),
                agentAtr: false,
                application: application
            };

            commandCheckRegister.appAbsenceStartInfoDto.leaveComDayOffManas = _.map(commandCheckRegister.appAbsenceStartInfoDto.leaveComDayOffManas, (x: any) => {
                x.dateOfUse = new Date(x.dateOfUse).toISOString();
                x.outbreakDay = new Date(x.outbreakDay).toISOString();
                return x;
            });
            commandCheckRegister.appAbsenceStartInfoDto.payoutSubofHDManas = _.map(commandCheckRegister.appAbsenceStartInfoDto.payoutSubofHDManas, (x: any) => {
                x.dateOfUse = new Date(x.dateOfUse).toISOString();
                x.outbreakDay = new Date(x.outbreakDay).toISOString();
                return x;
            });

            let appTypeSettingLst = vm.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting;
            let qr = _.filter(appTypeSettingLst, {'appType': vm.application().appType});


            let commandRegister = {
                applyForLeave: this.createDataVacationApp(),
                appDates: appDates,
                leaveComDayOffMana: _.map(vm.leaveComDayOffManas(), (x: any) => {
                    x.dateOfUse = new Date(x.dateOfUse).toISOString();
                    x.outbreakDay = new Date(x.outbreakDay).toISOString();
                    return x;
                }),
                payoutSubofHDManagements: _.map(vm.payoutSubofHDManagements(), (x: any) => {
                    x.dateOfUse = new Date(x.dateOfUse).toISOString();
                    x.outbreakDay = new Date(x.outbreakDay).toISOString();
                    return x;
                }),
                mailServerSet: vm.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.mailServerSet,
                application: application,
                approvalRoot: vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opListApprovalPhaseState,
                apptypeSetting: qr.length > 0 ? qr[0] : null
            };

			vm.$blockui("show");
			// validate chung KAF000
            vm.$validate('#kaf000-a-component4 .nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason', '#kaf000-a-component5-textReason', '#work-type-combobox')
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
			}).then((result) => {
				// check trước khi đăng kí
				if(result) {
					return vm.$ajax('at', API.checkBeforeRegister, commandCheckRegister);
				}
			}).then((result) => {
				if (result) {
					holidayAppDates = result.holidayDateLst;
					commandRegister.appDates = holidayAppDates;
					// xử lý confirmMsg
					return vm.handleConfirmMessage(result.confirmMsgLst);
				}
			}).then((result) => {
				if(result) {
					// đăng kí 
					return vm.$ajax('at', API.register, commandRegister);
				}
			}).done((result) => {
				if (result) {
					return vm.$dialog.info({ messageId: "Msg_15"}).then(() => {
						nts.uk.request.ajax("at", API.reflectApp, result.reflectAppIdLst);
						return CommonProcess.handleAfterRegister(result, vm.isSendMail(), vm, false, vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
					});	
				}
			}).fail((failData) => {
				// xử lý lỗi nghiệp vụ riêng
				vm.handleErrorCustom(failData).then((result: any) => {
					if(result) {
						// xử lý lỗi nghiệp vụ chung
						vm.handleErrorCommon(failData);
					}
				});
			}).always(() => {
				vm.$blockui("hide");	
			});
		}

        validate() {
            const vm = this;
            if (vm.condition11() && vm.condition30()) {
                // if (vm.isChangeWorkHour() && vm.selectedWorkTimeCD()) {
                //     if (!vm.checkTimeValid(vm.startTime1) && !vm.checkTimeValid(vm.endTime1)) {
                //         vm.$dialog.error({messageId: "Msg_307"});
                //         return false;
                //     }
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

            if (startTime1 !== null && endTime1 !== null && startTime1 !== "" && endTime1 !== "") {
                workingHours.push({
                    workNo: 1,
                    timeZone: {
                        startTime: startTime1,
                        endTime: endTime1
                    }
                });
            }
            if (startTime2 !== null && endTime2 !== null && startTime2 !== "" && endTime2 !== "") {
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
            if (failData.messageId == "Msg_26") {
                vm.$dialog.error({messageId: failData.messageId, messageParams: failData.parameterIds})
                    .then(() => {
                        vm.$jump("com", "/view/ccg/008/a/index.xhtml");
                    });
                return $.Deferred().resolve(false);
            }
            if (failData.messageId === "Msg_478") {

            }
            return $.Deferred().resolve(true);
        }

        handleConfirmMessage(listMes: any): any {
            const vm = this;
            if (_.isEmpty(listMes)) {
                return $.Deferred().resolve(true);
            }
            let msg = listMes[0];

            return vm.$dialog.confirm({messageId: msg.msgID, messageParams: msg.paramLst})
                .then((value) => {
                    if (value === 'yes') {
                        return vm.handleConfirmMessage(_.drop(listMes));
                    } else {
                        return $.Deferred().resolve(false);
                    }
                });
        }


        validateAppDate(start: string, end: string) {
            let startDate = moment(start);
            let endDate = moment(end);
            if (startDate.isValid() && endDate.isValid()) {
                return true;
            }
            return false;
        }

        changeAppDate() {
            const vm = this;

            let startDate = vm.application().opAppStartDate(),
                endDate = vm.application().opAppEndDate();
            let appDates = []
            appDates.push(startDate, endDate);
            let command = {
                companyID: __viewContext.user.companyId,
                appDates: appDates,
                startInfo: vm.data,
                holidayAppType: vm.selectedType(),
                appWithDate: vm.appDispInfoStartupOutput().appDispInfoWithDateOutput
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

            vm.$validate([
                '#kaf000-a-component4 .nts-input'
            ]).then((valid: boolean) => {
                if (valid) {
                    return vm.$blockui("show").then(() => vm.$ajax(API.changeAppDate, command));
                }
            }).done((res: any) => {
                if (res) {
                    vm.fetchData(res);
                    vm.payoutSubofHDManagements([]);
                    vm.leaveComDayOffManas([]);
                }
            }).fail(err => {
                if (err.messageId === "Msg_43") {
                    vm.$dialog.error(err).then(() => {
                        vm.$jump("com", "/view/ccg/008/a/index.xhtml");
                    });

                } else {
                    vm.$dialog.error(err);
                }
            }).always(() => vm.$blockui("hide"));
        }

        // fetchRemainTime(remainVacationInfo: any) {
        //     const vm = this;

        //     // set over60HHourRemain
        //     if (remainVacationInfo.over60HHourRemain) {
        //         vm.over60HHourRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.over60HHourRemain));
        //     } else {
        //         vm.over60HHourRemain(nts.uk.time.format.byId("Time_Short_HM", 0));
        //     }

        //     // set subVacaHourRemain
        //     if (remainVacationInfo.subVacaHourRemain) {
        //         vm.subVacaHourRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.subVacaHourRemain));
        //     } else {
        //         vm.subVacaHourRemain(nts.uk.time.format.byId("Time_Short_HM", 0));
        //     }

        //     // set yearRemain
        //     if (remainVacationInfo.yearRemain && remainVacationInfo.yearRemain > 0) {
        //         if (remainVacationInfo.yearHourRemain && remainVacationInfo.yearHourRemain > 0) {
        //             vm.timeYearLeave(remainVacationInfo.yearRemain.toString().concat("日と").concat(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.yearHourRemain)));
        //         } else {
        //             vm.timeYearLeave(remainVacationInfo.yearRemain.toString().concat("日"));
        //         }
        //     } else {
        //         vm.timeYearLeave(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.yearHourRemain));
        //     }

        //     // set childNursingRemain
        //     if (remainVacationInfo.childNursingRemain && remainVacationInfo.childNursingRemain > 0) {
        //         if (remainVacationInfo.childNursingHourRemain && remainVacationInfo.childNursingHourRemain > 0) {
        //             vm.childNursingRemain(remainVacationInfo.childNursingRemain.toString().concat("日と").concat(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.childNursingHourRemain)));
        //         } else {
        //             vm.childNursingRemain(remainVacationInfo.childNursingRemain.toString().concat("日"));
        //         }
        //     } else {
        //         vm.childNursingRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.childNursingHourRemain));
        //     }

        //     // set nursingRemain
        //     if (remainVacationInfo.nursingRemain && remainVacationInfo.nursingRemain > 0) {
        //         if (remainVacationInfo.nursingRemain && remainVacationInfo.nirsingHourRemain > 0) {
        //             vm.nursingRemain(remainVacationInfo.nursingRemain.toString().concat("日と").concat(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.nirsingHourRemain)));
        //         } else {
        //             vm.nursingRemain(remainVacationInfo.nursingRemain.toString().concat("日"));
        //         }
        //     } else {
        //         vm.nursingRemain(nts.uk.time.format.byId("Time_Short_HM", remainVacationInfo.nirsingHourRemain));
        //     }
        // }

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
                return vm.$i18n('KAF006_100', [day.toString(), timeString]);
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

        checkCondition10(data: any) {
            const vm = this;
            if (vm.data && vm.data.remainVacationInfo) {
                if (vm.data.remainVacationInfo.substituteLeaveManagement.substituteLeaveManagement === 1) {
                    vm.condition10Substi(true);
                } else {
                    vm.condition10Substi(false);
                }

                if (vm.data.remainVacationInfo.annualLeaveManagement.annualLeaveManageDistinct === 1) {
                    vm.condition10Annual(true);
                } else {
                    vm.condition10Annual(false);
                }

                if (vm.data.remainVacationInfo.accumulatedRestManagement.accumulatedManage === 1) {
                    vm.condition10Accum(true);
                } else {
                    vm.condition10Accum(false);
                }
            }
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

        checkCondition30(data: any) {
            const vm = this;
            if (vm.data && vm.data.vacationApplicationReflect && vm.data.vacationApplicationReflect.workAttendanceReflect.reflectAttendance === 1) {
                vm.condition30(true);
                return true;
            }
            vm.condition30(false);
            return false;
        }

        checkCondition12(data: any) {
            const vm = this;
            if (vm.data && vm.data.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles && vm.selectedWorkTimeCD() && vm.isDispTime2ByWorkTime()) {
                vm.condition12(true);
                return true;
            }
            vm.condition12(false);
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
                }
                if (vm.data.vacationApplicationReflect.timeLeaveReflect.substituteLeaveTime === 1
                    && vm.data.remainVacationInfo.substituteLeaveManagement.timeAllowanceManagement === 1) {
                    vm.condition19Substitute(true);
                } else {
                    vm.condition19Substitute(false);
                }
                if (vm.data.vacationApplicationReflect.timeLeaveReflect.annualVacationTime === 1
                    && vm.data.remainVacationInfo.annualLeaveManagement.timeAnnualLeaveManage === 1) {
                    vm.condition19Annual(true);
                } else {
                    vm.condition19Annual(false);
                }
                if (vm.data.vacationApplicationReflect.timeLeaveReflect.childNursing === 1
                    && vm.data.remainVacationInfo.nursingCareLeaveManagement.childNursingManagement === 1) {
                    vm.condition19ChildNursing(true);
                } else {
                    vm.condition19ChildNursing(false);
                }
                if (vm.data.vacationApplicationReflect.timeLeaveReflect.nursing === 1
                    && vm.data.remainVacationInfo.nursingCareLeaveManagement.longTermCareManagement === 1) {
                    vm.condition19Nursing(true);
                } else {
                    vm.condition19Nursing(false);
                }
            }
        }

        checkCondition15(data: any) {
            const vm = this;
            let workType = null;
            if (!vm.selectedWorkTypeCD()) {
                vm.condition15(false);
                return false;
            } else {
                let workTypes = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});
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
                let workTypes = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});
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
            vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                return x.holidayAppType !== 0
            }));
        }

        checkCondition22(data: any) {
            const vm = this;
            if (vm.data && vm.data.remainVacationInfo.substituteLeaveManagement.substituteLeaveManagement === 1) {
                vm.condition22(true);
                return true;
            }
            vm.condition22(false);
            vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                return x.holidayAppType !== 1
            }));
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
            vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                return x.holidayAppType !== 4
            }));
        }

        checkCondition1(data: any) {
            const vm = this;
            if (vm.data && vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet && vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst) {
                let targetWorkType = _.filter(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.opEmploymentSet.targetWorkTypeByAppLst, {'appType': 1});
                if (targetWorkType.length > 0) {
                    if (_.filter(targetWorkType, {'opHolidayAppType': 0}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 0})[0].opHolidayTypeUse) {
                        vm.condition1_0(true);
                    } else {
                        vm.condition1_0(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 0
                        }));
                    }

                    if (_.filter(targetWorkType, {'opHolidayAppType': 1}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 1})[0].opHolidayTypeUse) {
                        vm.condition1_1(true);
                    } else {
                        vm.condition1_1(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 1
                        }));
                    }

                    if (_.filter(targetWorkType, {'opHolidayAppType': 2}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 2})[0].opHolidayTypeUse) {
                        vm.condition1_2(true);
                    } else {
                        vm.condition1_2(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 2
                        }));
                    }

                    if (_.filter(targetWorkType, {'opHolidayAppType': 3}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 3})[0].opHolidayTypeUse) {
                        vm.condition1_3(true);
                    } else {
                        vm.condition1_3(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 3
                        }));
                    }

                    if (_.filter(targetWorkType, {'opHolidayAppType': 4}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 4})[0].opHolidayTypeUse) {
                        vm.condition1_4(true);
                    } else {
                        vm.condition1_4(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 4
                        }));
                    }

                    if (_.filter(targetWorkType, {'opHolidayAppType': 5}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 5})[0].opHolidayTypeUse) {
                        vm.condition1_5(true);
                    } else {
                        vm.condition1_5(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 5
                        }));
                    }

                    if (_.filter(targetWorkType, {'opHolidayAppType': 6}).length > 0 && !_.filter(targetWorkType, {'opHolidayAppType': 6})[0].opHolidayTypeUse) {
                        vm.condition1_6(true);
                    } else {
                        vm.condition1_6(false);
                        vm.hdAppSetTmp(_.filter(vm.hdAppSetTmp(), (x) => {
                            return x.holidayAppType !== 6
                        }));
                    }

                    // if (targetWorkType.opHolidayTypeUse) {
                    // 	vm.condition1(false);
                    // 	return false;
                    // }
                }
            }
            // vm.condition1(true);
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
                let selectedRela = _.filter(dateSpecHdRelationLst, {'relationCD': vm.selectedDateSpec()});

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

        public openKDL035() {
            const vm = this;

            let workType = _.filter(vm.data.workTypeLst, {'workTypeCode': vm.selectedWorkTypeCD()});

            let params: any = {
                // 社員ID
                // employeeId: __viewContext.user.employeeId,
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
                // employeeId: __viewContext.user.employeeId,
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

            nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed(function (): any {
                //view all code of selected item 
                let childData = nts.uk.ui.windows.getShared('childData');
                if (childData) {
                    vm.selectedWorkTypeCD(childData.selectedWorkTypeCode);
                    vm.selectedWorkTimeCD(childData.selectedWorkTimeCode);
                    vm.selectedWorkTimeName(childData.selectedWorkTimeName);
                }
            });
        }

        openKDL020() {
            let vm = this;
            var employeeIds = [];
            employeeIds.push(__viewContext.user.employeeId);
            nts.uk.ui.windows.setShared('KDL020A_PARAM', {
                baseDate: new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate), 
                employeeIds: vm.application().employeeIDLst()});
            if (employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/020/a/multi.xhtml");
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/020/a/single.xhtml");
            }
        }

        openKDL029() {
            let vm = this;
            let param = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: moment(new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)).format("YYYY/MM/DD")
            }
            nts.uk.ui.windows.setShared('KDL029_PARAM', param);
            nts.uk.ui.windows.sub.modal('/view/kdl/029/a/index.xhtml');
        }

        openKDL005() {
            let vm = this;
            let data = {
                employeeIds: vm.application().employeeIDLst(),
                baseDate: moment(new Date(vm.data.appDispInfoStartupOutput.appDispInfoWithDateOutput.baseDate)).format("YYYYMMDD")
            }
            nts.uk.ui.windows.setShared('KDL005_DATA', data);
            if (data.employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/multi.xhtml");
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/single.xhtml");
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
    }

    const API = {
        startNew: 'at/request/application/appforleave/getAppForLeaveStart',
        getAllAppForLeave: 'at/request/application/appforleave/getAllAppForLeave',
        changeAppDate: 'at/request/application/appforleave/findChangeAppdate',
        checkBeforeRegister: 'at/request/application/appforleave/checkBeforeRegister',
        register: 'at/request/application/appforleave/insert',
        checkVacationTyingManage: 'at/request/application/appforleave/checkVacationTyingManage',
        changeWorkType: 'at/request/application/appforleave/findChangeWorkType',
        changeWorkTime: 'at/request/application/appforleave/findChangeWorkTime',
        changeUseingWorkTime: 'at/request/application/appforleave/findChangeUsingWorkTime',
        changeRela: 'at/request/application/appforleave/changeRela',
		reflectApp: "at/request/application/reflect-app"
    }

    interface DataTransfer {
        startTime: number;
        endTime: number;
        employeeID: string;
        appDate: string;
        applicationReason: string;
    }
}