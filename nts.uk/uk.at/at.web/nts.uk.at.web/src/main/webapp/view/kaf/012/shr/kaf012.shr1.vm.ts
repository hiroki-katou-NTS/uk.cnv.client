module nts.uk.at.view.kaf012.shr.viewmodel1 {
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;

    const template = `
    <div id="kaf012-share-component1">
        <div class="cell" style="font-weight: bold" data-bind="text: $i18n('KAF012_2')"></div>
        <div class="space-between-table ">
            <!-- ko if: display1() -->
            <div class="row-underline" style="display: flex; justify-content: space-between">
                <div data-bind="ntsFormLabel: {}, text: $i18n('KAF012_3')"></div>
                <a class="hyperlink" href="" data-bind="text: substituteRemaining, click: openKDL005"></a>
            </div>
            <!-- /ko -->
            <!-- ko if: display2() -->
            <div class="row-underline" style="display: flex; justify-content: space-between">
                <div data-bind="ntsFormLabel: {}, text: $i18n('KAF012_4')"></div>
                <a class="hyperlink" href="" data-bind="text: annualRemaining, click: openKDL020"></a>
            </div>
            <!-- /ko -->
            <!-- ko if: display3() -->
            <div class="row-underline" style="display: flex; justify-content: space-between">
                <div data-bind="ntsFormLabel: {}, text: $i18n('Com_ChildNurseHoliday')"></div>
                <a class="hyperlink" href="" data-bind="text: childNursingRemaining, click: openKDL051"></a>
            </div>
            <!-- /ko -->
            <!-- ko if: display4() -->
            <div class="row-underline"  style="display: flex; justify-content: space-between">
                <div data-bind="ntsFormLabel: {}, text: $i18n('Com_CareHoliday')"></div>
                <a class="hyperlink" href="" data-bind="text: nursingRemaining, click: openKDL052"></a>
            </div>
            <!-- /ko -->
            <!-- ko if: display5() -->
            <div class="row-underline" style="display: flex; justify-content: space-between">
                <div data-bind="ntsFormLabel: {}, text: $i18n('Com_ExsessHoliday')"></div>
                <a class="hyperlink" href="" data-bind="text: super60HRemaining, click: openKDL017"></a>
            </div>
            <!-- /ko -->
            <!-- ko if: display6() -->
            <div class="row-underline"  style="display: flex; justify-content: space-between">
                <div data-bind="ntsFormLabel: {}, text: $i18n('KAF012_46')"></div>
                <span data-bind="text: specialRemaining"></span>
            </div>
            <!-- /ko -->
        </div>
        <div class="end-line"></div>
    </div>`;

    @component({
        name: 'kaf012-share-component1',
        template: template
    })
    class Kaf012Share1ViewModel extends ko.ViewModel {
        reflectSetting: KnockoutObservable<ReflectSetting>;
        timeLeaveManagement: KnockoutObservable<TimeLeaveManagement>;
        timeLeaveRemaining: KnockoutObservable<TimeLeaveRemaining>;
        leaveType: KnockoutObservable<number>;
        application: KnockoutObservable<any>;
        specialLeaveFrame: KnockoutObservable<number>;

        display1: KnockoutObservable<boolean>;
        substituteRemaining: KnockoutObservable<string>;

        display2: KnockoutObservable<boolean>;
        annualRemaining: KnockoutObservable<string>;

        display3: KnockoutObservable<boolean>;
        childNursingRemaining: KnockoutObservable<string>;

        display4: KnockoutObservable<boolean>;
        nursingRemaining: KnockoutObservable<string>;

        display5: KnockoutObservable<boolean>;
        super60HRemaining: KnockoutObservable<string>;

        display6: KnockoutObservable<boolean>;
        specialRemaining: KnockoutObservable<string>;

        created(params: Params) {
            const vm = this;
            vm.reflectSetting = params.reflectSetting;
            vm.timeLeaveManagement = params.timeLeaveManagement;
            vm.timeLeaveRemaining = params.timeLeaveRemaining;
            vm.leaveType = params.leaveType;
            vm.application = params.application;
            vm.specialLeaveFrame = params.specialLeaveFrame;
            vm.display1 = ko.computed(() => {
                return !!vm.timeLeaveManagement()
                    && vm.timeLeaveManagement().timeSubstituteLeaveMng.timeSubstituteLeaveMngAtr
                    && !!vm.reflectSetting()
                    && vm.reflectSetting().condition.substituteLeaveTime == 1;
            });
            vm.substituteRemaining = ko.computed(() => {
                return vm.timeLeaveRemaining()
                    ? nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().subTimeLeaveRemainingTime)
                    : "0:00";
            });
            vm.display2 = ko.computed(() => {
                return !!vm.timeLeaveManagement()
                    && vm.timeLeaveManagement().timeAnnualLeaveMng.timeAnnualLeaveMngAtr
                    && !!vm.reflectSetting()
                    && vm.reflectSetting().condition.annualVacationTime == 1;
            });
            vm.annualRemaining = ko.computed(() => {
                if (vm.timeLeaveRemaining()) {
                    if (vm.timeLeaveRemaining().annualTimeLeaveRemainingDays <= 0)
                        return nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().annualTimeLeaveRemainingTime);
                    else if (vm.timeLeaveRemaining().annualTimeLeaveRemainingTime <= 0)
                        return vm.$i18n("KAF012_49", [vm.timeLeaveRemaining().annualTimeLeaveRemainingDays.toString()]);
                    else
                        return vm.$i18n(
                            "KAF012_50",
                            [
                                vm.timeLeaveRemaining().annualTimeLeaveRemainingDays.toString(),
                                nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().annualTimeLeaveRemainingTime)
                            ]);
                }
                return "0:00";
            });
            vm.display3 = ko.computed(() => {
                return !!vm.timeLeaveManagement()
                    && vm.timeLeaveManagement().nursingLeaveMng.timeChildCareLeaveMngAtr
                    && !!vm.reflectSetting()
                    && vm.reflectSetting().condition.childNursing == 1;
            });
            vm.childNursingRemaining = ko.computed(() => {
                if (vm.timeLeaveRemaining()) {
                    if (vm.timeLeaveRemaining().childCareRemainingDays <= 0)
                        return nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().childCareRemainingTime);
                    else if (vm.timeLeaveRemaining().childCareRemainingTime <= 0)
                        return vm.$i18n("KAF012_49", [vm.timeLeaveRemaining().childCareRemainingDays.toString()]);
                    else
                        return vm.$i18n(
                            "KAF012_50",
                            [
                                vm.timeLeaveRemaining().childCareRemainingDays.toString(),
                                nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().childCareRemainingTime)
                            ]);
                }
                return "0:00";
            });
            vm.display4 = ko.computed(() => {
                return !!vm.timeLeaveManagement()
                    && vm.timeLeaveManagement().nursingLeaveMng.timeCareLeaveMngAtr
                    && !!vm.reflectSetting()
                    && vm.reflectSetting().condition.nursing == 1;
            });
            vm.nursingRemaining = ko.computed(() => {
                if (vm.timeLeaveRemaining()) {
                    if (vm.timeLeaveRemaining().careRemainingDays <= 0)
                        return nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().careRemainingTime);
                    else if (vm.timeLeaveRemaining().careRemainingTime <= 0)
                        return vm.$i18n("KAF012_49", [vm.timeLeaveRemaining().careRemainingDays.toString()]);
                    else
                        return vm.$i18n(
                            "KAF012_50",
                            [
                                vm.timeLeaveRemaining().careRemainingDays.toString(),
                                nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().careRemainingTime)
                            ]);
                }
                return "0:00";
            });
            vm.display5 = ko.computed(() => {
                return !!vm.timeLeaveManagement()
                    && vm.timeLeaveManagement().super60HLeaveMng.super60HLeaveMngAtr
                    && !!vm.reflectSetting()
                    && vm.reflectSetting().condition.superHoliday60H == 1;
            });
            vm.super60HRemaining = ko.computed(() => {
                return vm.timeLeaveRemaining()
                    ? nts.uk.time.format.byId("Time_Short_HM", vm.timeLeaveRemaining().super60HRemainingTime)
                    : "0:00";
            });
            vm.display6 = ko.computed(() => {
                return (
                        vm.leaveType() == LeaveType.SPECIAL
                        || (
                            vm.leaveType() == LeaveType.COMBINATION
                            && !!vm.timeLeaveManagement()
                            && vm.timeLeaveManagement().timeSpecialLeaveMng.timeSpecialLeaveMngAtr
                            && !!vm.reflectSetting()
                            && vm.reflectSetting().condition.specialVacationTime == 1
                        )
                    )
                    && (vm.timeLeaveRemaining() && !_.isEmpty(vm.timeLeaveRemaining().specialTimeFrames));
            });
            vm.specialRemaining = ko.computed(() => {
                if (vm.timeLeaveRemaining() && vm.timeLeaveRemaining().specialTimeFrames.length > 0) {
                    const tmp = _.find(vm.timeLeaveRemaining().specialTimeFrames, i => i.specialFrameNo == vm.specialLeaveFrame());
                    if (tmp) {
                        if (tmp.dayOfSpecialLeave <= 0)
                            return nts.uk.time.format.byId("Time_Short_HM", tmp.timeOfSpecialLeave);
                        else if (tmp.timeOfSpecialLeave <= 0)
                            return vm.$i18n("KAF012_49", [tmp.dayOfSpecialLeave.toString()]);
                        else
                            return vm.$i18n(
                                "KAF012_50",
                                [
                                    tmp.dayOfSpecialLeave.toString(),
                                    nts.uk.time.format.byId("Time_Short_HM", tmp.timeOfSpecialLeave)
                                ]);
                    }
                }
                return "0:00";
            });
            // $("#remaining-table").ntsFixedTable({});
        }

        openKDL005() {
            const vm = this;
            var param: any = {
                employeeIds: vm.application().employeeIDLst().length == 0 ? [vm.$user.employeeId] : vm.application().employeeIDLst(),
                baseDate: nts.uk.time.formatDate(new Date(vm.timeLeaveRemaining().remainingStart), "yyyyMMdd")
            };
            setShared('KDL005_DATA', param);
            if(param.employeeIds.length > 1) {
                modal("/view/kdl/005/a/multi.xhtml");
            } else {
                modal("/view/kdl/005/a/single.xhtml");
            }
        }

        openKDL020() {
            let vm = this;
            const data = {
                employeeIds: vm.application().employeeIDLst().length == 0 ? [vm.$user.employeeId] : vm.application().employeeIDLst(),
                baseDate: new Date(vm.timeLeaveRemaining().remainingStart).toISOString()
            };
            setShared('KDL020A_PARAM', data);
            if(data.employeeIds.length > 1 ) {
                modal("/view/kdl/020/a/multi.xhtml");
            } else {
                modal("/view/kdl/020/a/single.xhtml");
            }
        }

        openKDL051() {
            let vm = this;
            const data = {
                employeeIds: vm.application().employeeIDLst().length == 0 ? [vm.$user.employeeId] : vm.application().employeeIDLst(),
                baseDate: new Date(vm.timeLeaveRemaining().remainingStart)
            };
            setShared('KDL051A_PARAM', data);
            if(data.employeeIds.length > 1 ) {
                modal("/view/kdl/051/multi.xhtml");
            } else {
                modal("/view/kdl/051/single.xhtml");
            }
        }

        openKDL052() {
            const vm = this;
            const data = {
                employeeList: vm.application().employeeIDLst().length == 0 ? [vm.$user.employeeId] : vm.application().employeeIDLst(),
                baseDate: new Date(vm.timeLeaveRemaining().remainingStart).toISOString()
            };
            if (data.employeeList.length > 1) {
                vm.$window.modal('/view/kdl/052/multi.xhtml', data);
            } else {
                vm.$window.modal('/view/kdl/052/single.xhtml', data);
            }
        }

        openKDL017() {
            const vm = this;
            const data = {
                employeeIds: vm.application().employeeIDLst().length == 0 ? [vm.$user.employeeId] : vm.application().employeeIDLst(),
                baseDate: nts.uk.time.formatDate(new Date(vm.timeLeaveRemaining().remainingStart), "yyyyMMdd")
            };
            nts.uk.ui.windows.setShared('KDL017_PARAM', data);
            if(data.employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/017/a/multiple.xhtml");
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/017/a/single.xhtml");
            }
        }
    }

    export enum LeaveType {
        SUBSTITUTE = 0, // 時間代休
        ANNUAL = 1, // 時間年休
        CHILD_NURSING = 2, // 子看護
        NURSING = 3, // 介護
        SUPER_60H = 4, // 60H超休
        SPECIAL = 5, // 時間特別休暇
        COMBINATION = 6, // 組合せ利用
    }

    interface Params {
        reflectSetting: KnockoutObservable<ReflectSetting>,
        timeLeaveManagement: KnockoutObservable<TimeLeaveManagement>,
        timeLeaveRemaining: KnockoutObservable<TimeLeaveRemaining>,
        leaveType: KnockoutObservable<number>,
        application: KnockoutObservable<any>,
        specialLeaveFrame: KnockoutObservable<number>
    }

    export interface ReflectSetting {
        condition: {
            annualVacationTime: number,
            childNursing: number,
            nursing: number,
            specialVacationTime: number,
            substituteLeaveTime: number,
            superHoliday60H: number
        },
        destination: {
            firstAfterWork: number,
            firstBeforeWork: number,
            privateGoingOut: number,
            secondAfterWork: number,
            secondBeforeWork: number,
            unionGoingOut: number
        },
        reflectActualTimeZone: number
    }

    export interface TimeLeaveManagement {
        nursingLeaveMng: {
            timeCareLeaveMngAtr: boolean,
            timeCareLeaveUnit: number,
            timeChildCareLeaveMngAtr: boolean,
            timeChildCareLeaveUnit: number,
        },
        super60HLeaveMng: {
            super60HLeaveMngAtr: boolean,
            super60HLeaveUnit: number,
        },
        timeAnnualLeaveMng: {
            timeAnnualLeaveMngAtr: boolean,
            timeAnnualLeaveUnit: number,
        },
        timeSpecialLeaveMng: {
            listSpecialFrame: Array<any>,
            timeSpecialLeaveMngAtr: boolean,
            timeSpecialLeaveUnit: number
        },
        timeSubstituteLeaveMng: {
            timeSubstituteLeaveMngAtr: boolean,
            timeSubstituteLeaveUnit: number,
        }
    }

    export interface TimeLeaveRemaining {
        annualTimeLeaveRemainingDays: number,
        annualTimeLeaveRemainingTime: number,
        careRemainingDays: number,
        careRemainingTime: number,
        childCareRemainingDays: number,
        childCareRemainingTime: number,
        specialTimeFrames: Array<any>,
        subTimeLeaveRemainingTime: number,
        super60HRemainingTime: number,
        remainingStart: string,
        remainingEnd: string
    }
}