import { _} from '@app/provider';
import { TrackRecordAtr, OverTimeShiftNight, BreakTime, TimeZoneNew, TimeZoneWithWorkNo, AppOverTime, ParamCalculateMobile, ParamSelectWorkMobile, InfoWithDateApplication, ParamStartMobile, OvertimeAppAtr, Model, DisplayInfoOverTime, NotUseAtr, ApplicationTime, OvertimeApplicationSetting, AttendanceType, HolidayMidNightTime, StaturoryAtrOfHolidayWork, ParamBreakTime, WorkInformation, WorkHoursDto, AppDateContradictionAtr, ExcessState } from '../a/define.interface';
import { component, Prop, Watch } from '@app/core/component';
import { StepwizardComponent } from '@app/components';
import { KafS05Step1Component } from '../step1';
import { HolidayTime, KafS05Step2Component } from '../step2';
import { KafS05Step3Component } from '../step3';
import { KDL002Component } from '../../../kdl/002';
import { Kdl001Component } from '../../../kdl/001';
import { KafS00ShrComponent, AppType, Application, InitParam } from 'views/kaf/s00/shr';
import { OverTime } from '../step2/index';
import { OverTimeWorkHoursDto } from '../../s00/sub/p2';
import { CmmS45CComponent } from '../../../cmm/s45/c/index';

@component({
    name: 'kafs05a',
    route: '/kaf/s05/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: [],
    components: {
        'step-wizard': StepwizardComponent,
        'kafS05Step1Component': KafS05Step1Component,
        'kafS05Step2Component': KafS05Step2Component,
        'kafS05Step3Component': KafS05Step3Component,
        'worktype': KDL002Component,
        'worktime': Kdl001Component,
        'cmms45c': CmmS45CComponent
    }

})
export class KafS05Component extends KafS00ShrComponent {

    private numb: number = 1;
    public text1: string = null;
    public text2: string = 'step2';
    public isValidateAll: boolean = true;
    public user: any = null;
    public modeNew: boolean = true;
    public application: Application = null;

    public model: Model = {} as Model;

    public date: string;

    public overTimeClf: number;
    public appId: string;

    public isMsg_1562: Boolean = false;

    public isMsg_1556: boolean = false;

    @Prop()
    public params: InitParam;

    public get getoverTimeClf(): number {
        const self = this;

        return self.overTimeClf;
    }

    // @Watch('numb', {deep: true})
    // public changeoverTimeClf(data: any) {
    //     const self = this;

    //     if (self.numb == 1) {
    //         self.pgName = 'kafs05step1';
    //     } else if (self.numb == 2) {
    //         self.pgName = 'kafs05step2';
    //     } else {
    //         self.pgName = 'kafs05step3';
    //     }
    // }
   

    public get step() {

        const self = this;
        

        return `step_${self.numb}`;
    }
    public get overTimeWorkHoursDto(): OverTimeWorkHoursDto {
        const self = this;
        let model = self.model as Model;
        
        return _.get(model, 'displayInfoOverTime.infoNoBaseDate.agreeOverTimeOutput') || null;
    }

    // 「残業申請の表示情報．基準日に関係しない情報．残業申請設定．残業休出申請共通設定．時間外表示区分」＝する
    public get c1() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeAppSet.overtimeLeaveAppCommonSetting.extratimeDisplayAtr');

        return value == NotUseAtr.USE;
    }
    //「残業申請の表示情報．基準日に関係しない情報．残業休日出勤申請の反映．残業申請．実績の勤務情報へ反映する」＝する
    public get c2() {
        const self = this;
        let model = self.model as Model;
        let c2 = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeReflect.overtimeWorkAppReflect.reflectActualWorkAtr') == NotUseAtr.USE;
        
        return c2;
    }

    // 「残業申請の表示情報．基準日に関係しない情報．残業申請設定．申請詳細設定．時刻計算利用区分」＝する
    public get c3() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeAppSet.applicationDetailSetting.timeCalUse');
        
        return value == NotUseAtr.USE;
    }
    // 「残業申請の表示情報．基準日に関係しない情報．残業申請設定．申請詳細設定．時間入力利用区分」= する
    public get c3_disable() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeAppSet.applicationDetailSetting.timeInputUse');
        
        return value == NotUseAtr.USE;
    }
    // ※表15 = × AND「残業申請の表示情報．基準日に関係しない情報．残業休日出勤申請の反映．残業申請．事前．休憩・外出を申請反映する」＝する
    // ※表15 = ○ AND「残業申請の表示情報．基準日に関係しない情報．残業休日出勤申請の反映．残業申請．事後．休憩・外出を申請反映する」= する
    public get c3_1_1() {
        const self = this;
        let model = self.model as Model;
        let value1 = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeReflect.overtimeWorkAppReflect.reflectBeforeBreak');
        let value2 = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeReflect.overtimeWorkAppReflect.reflectBreakOuting');

        return ((!self.c15 && value1 == NotUseAtr.USE) || (self.c15 && value2 == NotUseAtr.USE));
    }
    // ※表3 = ○　OR　※表3-1-1 = ○
    // public get c3_1() {
    //     const self = this;

    //     return self.c3_1_1 || self.c3;
    // }
    //  c3 AND「残業申請の表示情報．申請表示情報．申請表示情報(基準日関係なし)．複数回勤務の管理」＝true"
    public get c3_1() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.appDispInfoStartup.appDispInfoNoDateOutput.managementMultipleWorkCycles');

        return value && self.c3;
    }



    // 「残業申請の表示情報．基準日に関する情報．残業申請で利用する残業枠．残業枠一覧」 <> empty
    public get c4() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.infoBaseDateOutput.quotaOutput.overTimeQuotaList');

        return !_.isEmpty(value);
    }

    public get c4_1() {
        const self = this;
        let prePost;
        if (self.modeNew) {
            prePost = self.application.prePostAtr;
        } else {
            prePost = self.model.displayInfoOverTime.appDispInfoStartup.appDetailScreenInfo.application.prePostAtr;
        }

        return prePost == 1;
    }
    // 「残業申請の表示情報．基準日に関係しない情報．残業休日出勤申請の反映．時間外深夜時間を反映する」= する
    public get c5() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.infoNoBaseDate.overTimeReflect.nightOvertimeReflectAtr');

        return value == NotUseAtr.USE;
    }
    // 「残業申請の表示情報．基準日に関する情報．残業申請で利用する残業枠．フレックス時間表示区分」= true
    public get c6() {
        const self = this;
        let model = self.model as Model;
        let value = _.get(model, 'displayInfoOverTime.infoBaseDateOutput.quotaOutput.flexTimeClf');

        return value;
    }
    public get c12() {
        const self = this;

        return self.c13 || self.c14;
    }
    // 「残業申請の表示情報．基準日に関係しない情報．利用する乖離理由．NO = 1」 <> empty　AND 乖離理由を選択肢から選ぶ = true
    public get c13() {
        const self = this;
        let model = self.model as Model;
        let findResult = _.find(_.get(model, 'displayInfoOverTime.infoNoBaseDate.divergenceReasonInputMethod'), { divergenceTimeNo: 1 });
        let c13_1 = !_.isNil(findResult);
        let c13_2 = c13_1 ? findResult.divergenceReasonSelected : false;

        return c13_1 && c13_2;
    }
    // 「残業申請の表示情報．基準日に関係しない情報．利用する乖離理由．NO = 1」 <> empty　AND　乖離理由を入力する = true
    public get c14() {
        const self = this;
        let model = self.model as Model;
        let findResult = _.find(_.get(model, 'displayInfoOverTime.infoNoBaseDate.divergenceReasonInputMethod'), { divergenceTimeNo: 1 });
        let c14_1 = !_.isNil(findResult);
        let c14_2 = c14_1 ? findResult.divergenceReasonInputed : false;

        return c14_1 && c14_2;
    }
    // （「事前事後区分」が表示する　AND　「事前事後区分」が「事後」を選択している）　OR
    // （「事前事後区分」が表示しない　AND 「残業申請の表示情報．申請表示情報．申請表示情報(基準日関係あり)．事前事後区分」= 「事後」）
    public get c15() {
        const self = this;
        let model = self.model as Model;
        let c15 = false;
        if (!self.modeNew) {

            return _.get(self.model, 'displayInfoOverTime.appDispInfoStartup.appDetailScreenInfo.application.prePostAtr') == 1;
        }
        if (_.get(model, 'displayInfoOverTime.appDispInfoStartup.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.prePostDisplayAtr') == 0) {				
            let prePost = _.get(model, 'displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.prePostAtr');
            if (prePost == 1) {
                c15 = true;					
            } else {
                c15 = false;
            }
            
            return c15;
        } else {
            let prePost = _.get(self.application, 'prePostAtr');
            if (prePost == 1) {
                c15 = true;					
            } else {
                c15 = false;
            }

            return c15;
        }
    }
    // ※表16-1 = ○　OR　※表16-2 = ○　OR　※表16-3 = ○　OR　※表16-4 = ○
    public get c16() {
        const self = this;

        return self.c16_1 || self.c16_2 || self.c16_3 || self.c16_4;
    }
    // 「残業申請の表示情報．計算結果．申請時間．申請時間．type」= 休出時間 があるの場合
    public get c16_1() {
        const self = this;
        let model = self.model as Model;
        let c16_1 = false;
        if (!_.isNil(_.get(model, 'displayInfoOverTime.calculationResultOp'))) {
            _.forEach(model.displayInfoOverTime.calculationResultOp.applicationTimes, (i: ApplicationTime) => {
                _.forEach(i.applicationTime, (item: OvertimeApplicationSetting) => {
                    if (item.attendanceType == AttendanceType.BREAKTIME) {

                        c16_1 = true;
                    }
                });
            });
        }

        return c16_1;
    }
    // ※表15 = ○　　AND　※表16-1 = ○　
    public get c16_1_2() {
        const self = this;

        return self.c15 && self.c16_1;
    }

    // ※表5 = ○ AND「残業申請の表示情報．計算結果．申請時間．就業時間外深夜時間．休出深夜時間．法定区分」= 法定内休出 があるの場合
    public get c16_2() {
        const self = this;
        let model = self.model as Model;
        let c16_2 = false;
        let midNightHolidayTimes = _.get(model.displayInfoOverTime, 'calculationResultOp.applicationTimes[0].overTimeShiftNight.midNightHolidayTimes');
        _.forEach(midNightHolidayTimes, (item: HolidayMidNightTime) => {
            if (item.legalClf == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork && item.attendanceTime > 0) {

                c16_2 = true && self.c5;
            }
        });

        return c16_2;
    }
    // ※表15 = ○　　AND　※表16-2 = ○　
    public get c16_2_2() {
        const self = this;

        return self.c15 && self.c16_2;
    }
    // ※表5 = ○ AND「残業申請の表示情報．計算結果．申請時間．就業時間外深夜時間．休出深夜時間．法定区分」= 法定外休出 があるの場合
    public get c16_3() {
        const self = this;
        let model = self.model as Model;
        let c16_3 = false;
        let midNightHolidayTimes = _.get(model.displayInfoOverTime, 'calculationResultOp.applicationTimes[0].overTimeShiftNight.midNightHolidayTimes');
        _.forEach(midNightHolidayTimes, (item: HolidayMidNightTime) => {
            if (item.legalClf == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork && item.attendanceTime > 0) {

                c16_3 = true && self.c5;
            }
        });

        return c16_3;
    }
    // ※表15 = ○　　AND　※表16-3 = ○　
    public get c16_3_2() {
        const self = this;

        return self.c15 && self.c16_3;
    }

    // ※表5 = ○ AND「残業申請の表示情報．計算結果．申請時間．就業時間外深夜時間．休出深夜時間．法定区分」= 祝日休出 があるの場合
    public get c16_4() {
        const self = this;
        let model = self.model as Model;
        let c16_4 = false;
        let midNightHolidayTimes = _.get(model.displayInfoOverTime, 'calculationResultOp.applicationTimes[0].overTimeShiftNight.midNightHolidayTimes');
        _.forEach(midNightHolidayTimes, (item: HolidayMidNightTime) => {
            if (item.legalClf == StaturoryAtrOfHolidayWork.PublicHolidayWork && item.attendanceTime > 0) {

                c16_4 = true && self.c5;
            }
        });

        return c16_4;
    }
    // ※表15 = ○　　AND　※表16-4 = ○　
    public get c16_4_2() {
        const self = this;

        return self.c5 && self.c16_4;
    }
    // ※表20＝○ OR ※表21＝○
    public get c19() {
        const self = this;

        return self.c20 || self.c21;
    }
    // 「残業申請の表示情報．基準日に関係しない情報．利用する乖離理由．NO = 2」 <> empty　AND 乖離理由を選択肢から選ぶ = true
    public get c20() {
        const self = this;
        let model = self.model as Model;
        let findResult = _.find(_.get(model, 'displayInfoOverTime.infoNoBaseDate.divergenceReasonInputMethod'), { divergenceTimeNo: 2 });
        let c20_1 = !_.isNil(findResult);
        let c20_2 = c20_1 ? findResult.divergenceReasonSelected : false;

        return c20_1 && c20_2;
    }
    // 「残業申請の表示情報．基準日に関係しない情報．利用する乖離理由．NO = 2」 <> empty　AND　乖離理由を入力する = true
    public get c21() {
        const self = this;
        let model = self.model as Model;
        let findResult = _.find(_.get(model, 'displayInfoOverTime.infoNoBaseDate.divergenceReasonInputMethod'), { divergenceTimeNo: 2 });
        let c21_1 = !_.isNil(findResult);
        let c21_2 = c21_1 ? findResult.divergenceReasonInputed : false;

        return c21_1 && c21_2;
    }




    public created() {
        const vm = this;


        

        if (!_.isNil(vm.params) && vm.params.isDetailMode) {
            vm.modeNew = false;
            let model = {} as Model;
            model.appOverTime = vm.params.appDetail.appOverTime as AppOverTime;
            model.displayInfoOverTime = vm.params.appDetail.displayInfoOverTime as DisplayInfoOverTime;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
            vm.model = model;
        }

        if (vm.modeNew) {
            if (vm.$route.query.overworkatr == '0') {
                vm.overTimeClf = 0;
            } else if (vm.$route.query.overworkatr == '1') {
                vm.overTimeClf = 1;
            } else {
                vm.overTimeClf = 2;
            }
        } else {
            vm.overTimeClf = _.get(vm.model, 'appOverTime.overTimeClf');
        }

        if (_.isNil(vm.overTimeClf)) {
            vm.overTimeClf = 2;
        }
        if (vm.overTimeClf == 0) {
            vm.pgName = 'kafs05step1';
        } else if (vm.overTimeClf == 1) {
            vm.pgName = 'kafs05step2';
        } else {
            vm.pgName = 'kafs05step3';
        }
    }
    public mounted() {
        const vm = this;
        vm.fetchData();
    }
    public fetchData() {
        const vm = this;
        vm.$mask('show');
        if (!vm.modeNew) {
            vm.date = vm.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;
        }
        if (vm.modeNew) {
            if (vm.$route.query.overworkatr == '0') {
                vm.overTimeClf = 0;
            } else if (vm.$route.query.overworkatr == '1') {
                vm.overTimeClf = 1;
            } else {
                vm.overTimeClf = 2;
            }
        } else {
            vm.overTimeClf = _.get(vm.model, 'appOverTime.overTimeClf') || 2;
        }
        if (vm.modeNew) {
            vm.application = vm.createApplicationInsert(AppType.OVER_TIME_APPLICATION);
            vm.application.employeeIDLst = _.get(vm.params, 'employeeID') ? [_.get(vm.params, 'employeeID')] : [];
            vm.application.appDate = _.get(vm.params, 'date');
        } else {
            vm.application = vm.createApplicationUpdate(vm.appDispInfoStartupOutput.appDetailScreenInfo);
        }
        vm.$auth.user.then((user: any) => {
            vm.user = user;
        }).then(() => {
            if (vm.modeNew) {
                return vm.loadCommonSetting(
                    AppType.OVER_TIME_APPLICATION, 
                    _.isEmpty(vm.application.employeeIDLst) ? null : vm.application.employeeIDLst[0], 
                    null, 
                    vm.application.appDate ? [vm.application.appDate] : [], 
                    vm.overTimeClf);
            }

            return true;
        }).then((loadData: any) => {
            if (loadData) {
                vm.updateKaf000_A_Params(vm.user);
                vm.kaf000_A_Params.opOvertimeAppAtr = vm.overTimeClf;
                vm.updateKaf000_B_Params(vm.modeNew, vm.application.appDate);
                vm.updateKaf000_C_Params(vm.modeNew);
                if (vm.modeNew) {
                    vm.kaf000_B_Params.newModeContent.useMultiDaySwitch = false;
                }
                let command = {} as ParamStartMobile;
                let url = self.location.search.split('=')[1];
                command.mode = vm.modeNew;
                command.companyId = vm.user.companyId;
                command.employeeIdOptional = !_.isEmpty(vm.application.employeeIDLst) ? vm.application.employeeIDLst[0] : vm.user.employeeId;
                command.overtimeAppAtr = vm.overTimeClf;
                command.appDispInfoStartupOutput = vm.appDispInfoStartupOutput;
                command.agent = false;
                command.dateOptional = vm.application.appDate;

                if (vm.modeNew) {
                    return vm.$http.post('at', API.start, command);
                }

                return true;
            }
        }).then((result: any) => {
            if (result) {
                if (vm.modeNew) {
                    let modelClone = {} as Model;
                    modelClone.displayInfoOverTime = result.data.displayInfoOverTime;
                    vm.model = modelClone;
                }
                let step1 = vm.$refs.step1 as KafS05Step1Component;
                step1.loadData(vm.model.displayInfoOverTime);
                // 勤務種類リストと就業時間帯リストがない場合エラーを返す
                if (vm.model.displayInfoOverTime) {
                    
                    if (_.isEmpty(vm.model.displayInfoOverTime.infoBaseDateOutput.worktypes)
                        && vm.model.displayInfoOverTime.infoNoBaseDate.overTimeAppSet.applicationDetailSetting.timeCalUse == NotUseAtr.USE
                    ) {
                        // msg_1567
                        vm.$modal.error({ messageId: 'Msg_1567'});	
                    }
                    if (vm.model.displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst) {
                        
                        if (_.isEmpty(vm.model.displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst)
                        && vm.model.displayInfoOverTime.infoNoBaseDate.overTimeAppSet.applicationDetailSetting.timeCalUse == NotUseAtr.USE        
                        ) {
                            vm.$modal.error({ messageId: 'Msg_1568'});	
                        }
                    } else {
                        vm.$modal.error({ messageId: 'Msg_1568'});	
                    }
                }
            }
        }).catch((error: any) => {
            vm.handleErrorCustom(error).then((result) => {
                if (result) {
                    vm.handleErrorCommon(error);
                }
            });
        }).then(() => vm.$mask('hide'));
    }


    public changeDate(date: string) {
        const self = this;
        if (!self.modeNew) {

            return;
        }
        self.$mask('show');
        let command = {
            companyId: self.user.companyId,
            date,
            displayInfoOverTime: self.model.displayInfoOverTime,
            agent: false
        };
        self.$http.post('at',
            API.changeDate,
            command
        )
            .then((res: any) => {
                self.model.displayInfoOverTime = res.data;
                let step1 = self.$refs.step1 as KafS05Step1Component;
                step1.loadData(self.model.displayInfoOverTime);
                step1.createHoursWorkTime();
                // エラーメッセージ(Msg_1556)を画面項目「A_A3_1」に表示する
                // 「残業申請の表示情報．申請表示情報．申請表示情報(基準日関係あり)．表示する実績内容．実績詳細」== empty
                let c1 = _.isNil(_.get(self.model, 'displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail'));
                // 残業申請の表示情報．申請表示情報．申請表示情報(基準日関係あり)．表示する実績内容．実績詳細．実績スケ区分」= スケジュール）
                let c2 = false;
                if (!c1) {
                    c2 = _.get(self.model, 'displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.trackRecordAtr') == TrackRecordAtr.SCHEDULE;
                }
                // 「残業申請の表示情報．基準日に関係しない情報．残業申請設定．残業休出申請共通設定．実績超過区分」= チェックする（登録不可）
                let c3 = _.get(self.model, 'displayInfoOverTime.infoNoBaseDate.overTimeAppSet.overtimeLeaveAppCommonSetting.performanceExcessAtr') == AppDateContradictionAtr.CHECKNOTREGISTER;
                if ((c1 || c2) && c3) {
                    self.isMsg_1556 = true;
                } else {
                    self.isMsg_1556 = false;
                }
                self.$mask('hide');
            })
            .catch((res: any) => {
                self.$nextTick(() => {
                    self.$mask('hide');
                });
                // xử lý lỗi nghiệp vụ riêng
                self.handleErrorCustom(res).then((result: any) => {
                    if (result) {
                        // xử lý lỗi nghiệp vụ chung
                        self.handleErrorCommon(res);
                    }
                });
            });
    }

    public kaf000BChangeDate(objectDate) {
        const self = this;
        console.log('emit' + objectDate);
        if (objectDate.startDate) {
            if (self.modeNew) {
                self.application.appDate = self.$dt.date(objectDate.startDate, 'YYYY/MM/DD');
                self.application.opAppStartDate = self.$dt.date(objectDate.startDate, 'YYYY/MM/DD');
                self.application.opAppEndDate = self.$dt.date(objectDate.endDate, 'YYYY/MM/DD');
                self.date = objectDate.startDate;
            }
            self.changeDate(objectDate.startDate);
        }
    }

    public kaf000BChangePrePost(prePostAtr) {
        const self = this;
        console.log('emit' + prePostAtr);
        self.application.prePostAtr = prePostAtr;
    }

    public kaf000CChangeReasonCD(opAppStandardReasonCD) {
        const self = this;
        console.log('emit' + opAppStandardReasonCD);
        self.application.opAppStandardReasonCD = opAppStandardReasonCD;
    }

    public kaf000CChangeAppReason(opAppReason) {
        const self = this;
        console.log('emit' + opAppReason);
        self.application.opAppReason = opAppReason;
    }
    public toAppOverTime() {
        const self = this;
        let step1 = self.$refs.step1 as KafS05Step1Component;
        let appOverTimeInsert = self.modeNew ? {} as AppOverTime : self.model.appOverTime;
        if (step1) {
            if (self.modeNew) {
                if (self.model.displayInfoOverTime.appDispInfoStartup.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.prePostDisplayAtr != 1) {
                    self.application.prePostAtr = self.model.displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.prePostAtr;
                }
                appOverTimeInsert.application = self.application as any;
                appOverTimeInsert.overTimeClf = self.overTimeClf;
            } else {
                appOverTimeInsert.application = self.model.displayInfoOverTime.appDispInfoStartup.appDetailScreenInfo.application;
            }
            if (step1.getWorkType() && self.c3) {
                appOverTimeInsert.workInfoOp = {} as WorkInformation;
                appOverTimeInsert.workInfoOp.workType = step1.getWorkType();
                appOverTimeInsert.workInfoOp.workTime = step1.getWorkTime() == '' ? null : step1.getWorkTime();
            }
            appOverTimeInsert.workHoursOp = [] as Array<TimeZoneWithWorkNo>;
            {   
                if (self.c3) {
                    let start = _.get(step1.getWorkHours1(), 'start');
                    let end = _.get(step1.getWorkHours1(), 'end');
                    if (_.isNumber(start) && _.isNumber(end)) {
                        let timeZone = {} as TimeZoneWithWorkNo;
                        timeZone.workNo = 1;
                        timeZone.timeZone = {} as TimeZoneNew;
                        timeZone.timeZone.startTime = start;
                        timeZone.timeZone.endTime = end;
                        appOverTimeInsert.workHoursOp.push(timeZone);
                    }
                }
                if (self.c3_1) {
                    let start = _.get(step1.getWorkHours2(), 'start');
                    let end = _.get(step1.getWorkHours2(), 'end');
                    if (_.isNumber(start) && _.isNumber(end)) {
                        let timeZone = {} as TimeZoneWithWorkNo;
                        timeZone.workNo = 2;
                        timeZone.timeZone = {} as TimeZoneNew;
                        timeZone.timeZone.startTime = start;
                        timeZone.timeZone.endTime = end;
                        appOverTimeInsert.workHoursOp.push(timeZone);
                    }
                }
            }
            if (self.c3) {
                appOverTimeInsert.breakTimeOp = [] as Array<TimeZoneWithWorkNo>;
                let breakTimes = step1.getBreakTimes() as Array<BreakTime>;
                _.forEach(breakTimes, (item: BreakTime, index: number) => {
                    let start = _.get(item, 'valueHours.start');
                    let end = _.get(item, 'valueHours.end');
                    if (_.isNumber(start) && _.isNumber(end)) {
                        let timeZone = {} as TimeZoneWithWorkNo;
                        timeZone.workNo = index + 1;
                        timeZone.timeZone = {} as TimeZoneNew;
                        timeZone.timeZone.startTime = start;
                        timeZone.timeZone.endTime = end;
                        appOverTimeInsert.breakTimeOp.push(timeZone);
                    }
                });
            }

        }


        return appOverTimeInsert;
    }
    public toAppOverTimeForRegister() {
        const self = this;
        let appOverTime = self.model.appOverTime as AppOverTime;
        let step2 = self.$refs.step2 as KafS05Step2Component;

        let overTimes = step2.overTimes as Array<OverTime>;
        let holidayTimes = step2.holidayTimes as Array<HolidayTime>;
        let applicationTime = appOverTime.applicationTime = {} as ApplicationTime;
        let applicationTimes = applicationTime.applicationTime = [] as Array<OvertimeApplicationSetting>;
        _.forEach(overTimes, (item: OverTime) => {
            // AttendanceType.NORMALOVERTIME
            if (item.type == AttendanceType.NORMALOVERTIME && item.applicationTime > 0) {
                let overtimeApplicationSetting = {} as OvertimeApplicationSetting;
                overtimeApplicationSetting.attendanceType = AttendanceType.NORMALOVERTIME;
                overtimeApplicationSetting.frameNo = Number(item.frameNo);
                overtimeApplicationSetting.applicationTime = item.applicationTime;
                applicationTimes.push(overtimeApplicationSetting);
            }
            if (item.type == AttendanceType.MIDNIGHT_OUTSIDE && item.applicationTime > 0) {
                if (_.isNil(applicationTime.overTimeShiftNight)) {
                    applicationTime.overTimeShiftNight = {} as OverTimeShiftNight;
                }
                applicationTime.overTimeShiftNight.overTimeMidNight = item.applicationTime;
            }
            if (item.type == AttendanceType.FLEX_OVERTIME && item.applicationTime > 0) {
                applicationTime.flexOverTime = item.applicationTime;
            }

        });
        _.forEach(holidayTimes, (item: HolidayTime) => {
            // AttendanceType.BREAKTIME
            if (item.type == AttendanceType.BREAKTIME && item.applicationTime > 0) {
                let overtimeApplicationSetting = {} as OvertimeApplicationSetting;
                overtimeApplicationSetting.attendanceType = AttendanceType.BREAKTIME;
                overtimeApplicationSetting.frameNo = Number(item.frameNo);
                overtimeApplicationSetting.applicationTime = item.applicationTime;
                applicationTimes.push(overtimeApplicationSetting);
            }
            if (item.type == AttendanceType.MIDDLE_BREAK_TIME && item.applicationTime > 0) {
                self.toHolidayMidNightTime(item, applicationTime);
            } else if (item.type == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY && item.applicationTime > 0) {
                self.toHolidayMidNightTime(item, applicationTime);
            } else if (item.type == AttendanceType.MIDDLE_HOLIDAY_HOLIDAY && item.applicationTime > 0) {
                self.toHolidayMidNightTime(item, applicationTime);
            }

        });
        appOverTime.applicationTime.reasonDissociation = step2.getReasonDivergence();
        if (!self.modeNew) {
            appOverTime.application.opAppReason = self.application.opAppReason || self.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppReason as any;
            appOverTime.application.opAppStandardReasonCD = self.application.opAppStandardReasonCD || self.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppStandardReasonCD as any;
        }

        // assign value to overtime and holidaytime
        return appOverTime;
    }
    public toHolidayMidNightTime(overTime: HolidayTime, applicationTime: ApplicationTime) {
        if (_.isNil(applicationTime.overTimeShiftNight)) {
            applicationTime.overTimeShiftNight = {} as OverTimeShiftNight;
        }
        if (_.isNil(applicationTime.overTimeShiftNight.midNightHolidayTimes)) {
            applicationTime.overTimeShiftNight.midNightHolidayTimes = [] as Array<HolidayMidNightTime>;
        }
        let holidayMidNightTime = {} as HolidayMidNightTime;
        if (overTime.type == AttendanceType.MIDDLE_BREAK_TIME) {
            holidayMidNightTime.legalClf = StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork;
        } else if (overTime.type == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY) {
            holidayMidNightTime.legalClf = StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork;
        } else {
            holidayMidNightTime.legalClf = StaturoryAtrOfHolidayWork.PublicHolidayWork;
        }
        holidayMidNightTime.attendanceTime = overTime.applicationTime;

        return applicationTime.overTimeShiftNight.midNightHolidayTimes.push(holidayMidNightTime);
    }

    public toStep(value: number) {
        const vm = this;
        // step 1 -> step 2
        if (vm.numb == 1 && value == 2) {
            vm.$mask('show');
            let step1 = vm.$refs.step1 as KafS05Step1Component;
            if (
                (_.isNumber(_.get(step1, 'workHours2.start')) && !_.isNumber(_.get(step1, 'workHours2.end'))) 
                || (_.isNumber(_.get(step1, 'workHours2.end') && !_.isNumber(_.get(step1, 'workHours2.start'))))
            ) {

                vm.$nextTick(() => {
                    vm.$mask('hide');
                });
                vm.$modal.error({ messageId: 'Msg_307'})
                    .then(() => {
                        
                    });

                return;
            }
            vm.isValidateAll = vm.customValidate(step1);
            step1.$validate();
            window.scrollTo(500, 0);
            if (!vm.$valid || !vm.isValidateAll || !step1.$valid) {
                vm.$nextTick(() => {
                    vm.$mask('hide');
                });

                return;
            }
            let command = {

            } as ParamCalculateMobile;
            vm.model.appOverTime = vm.toAppOverTime();
            command.companyId = vm.user.companyId;
            command.employeeId = vm.user.employeeId;
            command.mode = vm.modeNew;
            command.displayInfoOverTime = vm.model.displayInfoOverTime;
            command.agent = false;
            if (vm.modeNew) {
                command.appOverTimeInsert = vm.model.appOverTime;
            } else {
                command.appOverTimeUpdate = vm.model.appOverTime;
            }
            command.dateOp = vm.modeNew ? vm.date : vm.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;
            vm.$http.post(
                'at',
                API.calculate,
                command
            )
                .then((res: any) => {
                    vm.model.displayInfoOverTime.calculationResultOp = res.data.calculationResultOp;
                    vm.model.displayInfoOverTime.workdayoffFrames = res.data.workdayoffFrames;
                    vm.model.displayInfoOverTime.calculatedFlag = res.data.calculatedFlag;
                    let step2 = vm.$refs.step2 as KafS05Step2Component;
                    // 計算後の「残業申請の表示情報．計算結果．事前申請・実績の超過状態．実績状態」 = 超過エラー
                    let c1 = _.get(vm.model, 'displayInfoOverTime.calculationResultOp.overStateOutput.achivementStatus') == ExcessState.EXCESS_ERROR;
                    if (c1) {
                        vm.isMsg_1556 = true;
                    } else {
                        vm.numb = value;
                        vm.isMsg_1556 = false;
                    }
                    step2.loadAllData();
                    vm.$nextTick(() => {
                        vm.$mask('hide');
                        step2.$forceUpdate();
                    });
                })
                .catch((res: any) => {
                    vm.$nextTick(() => {
                        vm.$mask('hide');
                    });
                    // xử lý lỗi nghiệp vụ riêng
                    vm.handleErrorCustom(res).then((result: any) => {
                        if (result) {
                            // xử lý lỗi nghiệp vụ chung
                            vm.handleErrorCommon(res);
                        }
                    });
                });
        } else if (vm.numb == 2 && value == 1) { // step 2 -> step 1
            let step2 = vm.$refs.step2 as KafS05Step2Component;
            step2.overTimes = [];
            step2.holidayTimes = [];
            vm.numb = value;
        } else if (vm.numb == 2 && value == 3) {
            let step3 = vm.$refs.step3 as KafS05Step3Component;
            // step3.setParam();
            vm.numb = value;
        } else {
            vm.numb = value;

        }

    }

    public customValidate(viewModel: any) {
        const vm = this;
        let validAllChild = true;
        for (let child of viewModel.$children) {
            let validChild = true;
            if (child.$children) {
                validChild = vm.customValidate(child);
            }
            child.$validate();
            if (!child.$valid || !validChild) {
                validAllChild = false;
            }
        }

        return validAllChild;
    }


    public register() {
        const vm = this;
        vm.$mask('show');
        let step2 = vm.$refs.step2 as KafS05Step2Component;
        vm.isValidateAll = vm.customValidate(step2);
        // step2.$validate();
        if (!step2.$valid || !vm.isValidateAll) {
            window.scrollTo(500, 0);
            vm.$nextTick(() => vm.$mask('hide'));

            return;
        }
        vm.model.appOverTime = vm.toAppOverTimeForRegister();
        vm.$http.post('at', API.checkBeforeRegister, {
            require: true,
            companyId: vm.user.companyId,
            displayInfoOverTime: vm.model.displayInfoOverTime,
            appOverTime: vm.model.appOverTime
        })
            .then((result: any) => {
                if (result) {
                    // xử lý confirmMsg
                    return vm.handleConfirmMessage(result.data);
                }
            }).then((result: any) => {
                if (result) {
                    // đăng kí 
                    return vm.$http.post('at', API.register, {
                        companyId: vm.user.companyId,
                        mode: vm.modeNew,
                        appOverTimeInsert: vm.model.appOverTime,
                        appOverTimeUpdate: vm.model.appOverTime,
                        isMailServer: vm.model.displayInfoOverTime.appDispInfoStartup.appDispInfoNoDateOutput.mailServerSet,
                        appDispInfoStartupOutput: vm.model.displayInfoOverTime.appDispInfoStartup
                    }).then((result: any) => {
                        vm.$http.post('at', API.reflectApp, result.data.reflectAppIdLst);
                        vm.appId = result.data.appIDLst[0];
                        vm.toStep(3);
                    });
                }
            }).then((result: any) => {
                if (result) {
                    return true;
                }
            }).catch((failData) => {
                // xử lý lỗi nghiệp vụ riêng
                vm.handleErrorCustom(failData).then((result: any) => {
                    if (result) {
                        // xử lý lỗi nghiệp vụ chung
                        vm.handleErrorCommon(failData);
                    }
                });
            }).then(() => {
                vm.$nextTick(() => vm.$mask('hide'));
            });
    }

    public handleErrorCustom(failData: any): any {
        const vm = this;

        return new Promise((resolve) => {
            if (failData.messageId == 'Msg_197') {
                vm.$modal.error({ messageId: 'Msg_197', messageParams: [] }).then(() => {
                    let appID = vm.appDispInfoStartupOutput.appDetailScreenInfo.application.appID;
                    vm.$modal('cmms45c', { 'listAppMeta': [appID], 'currentApp': appID }).then((newData: InitParam) => {
                        vm.params = newData;
                        if (!_.isNil(vm.params)) {
                            vm.modeNew = false;
                            let model = {} as Model;
                            model.appOverTime = vm.params.appDetail.appOverTime as AppOverTime;
                            model.displayInfoOverTime = vm.params.appDetail.displayInfoOverTime as DisplayInfoOverTime;
                            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
                            vm.model = model;
                        }
                        vm.overTimeClf = _.get(vm.model, 'appOverTime.overTimeClf');
                        if (_.isNil(vm.overTimeClf)) {
                            vm.overTimeClf = 2;
                        }
                        if (vm.overTimeClf == 0) {
                            vm.pgName = 'kafs05step1';
                        } else if (vm.overTimeClf == 1) {
                            vm.pgName = 'kafs05step2';
                        } else {
                            vm.pgName = 'kafs05step3';
                        }
                        vm.fetchData();
                    });
                });
    
                return resolve(false);
            }

            if (failData.messageId == 'Msg_26') {
                vm.$modal.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
                    .then(() => {
                        vm.$goto('ccg008a');
                    });

                return resolve(false);
            }

            return resolve(true);
        });
    }

    public handleConfirmMessage(listMes: any): any {
        const vm = this;

        return new Promise((resolve) => {
            if (_.isEmpty(listMes)) {
                return resolve(true);
            }
            let msg = listMes[0];

            return vm.$modal.confirm({ messageId: msg.msgID, messageParams: msg.paramLst })
                .then((value) => {
                    if (value === 'yes') {
                        return vm.handleConfirmMessage(_.drop(listMes)).then((result) => {
                            if (result) {
                                return resolve(true);
                            }

                            return resolve(false);
                        });
                    }

                    return resolve(false);
                });
        });
    }
    public handleErrorMessage(res: any) {
        const self = this;
        self.$mask('hide');
        if (res.messageId) {
            return self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {

            if (_.isArray(res.errors)) {
                return self.$modal.error({ messageId: res.errors[0].messageId, messageParams: res.parameterIds });
            } else {
                return self.$modal.error({ messageId: res.errors.messageId, messageParams: res.parameterIds });
            }
        }
    }

    public openKDL002(name: string) {
        const self = this;
        let step1 = self.$refs.step1 as KafS05Step1Component;
        if (name == 'worktype') {
            self.$modal('worktype', {

                seledtedWkTypeCDs: _.map(_.uniqBy(self.model.displayInfoOverTime.infoBaseDateOutput.worktypes, (e: any) => e.workTypeCode), (item: any) => item.workTypeCode),
                selectedWorkTypeCD: step1.getWorkType(),
                seledtedWkTimeCDs: _.map(self.model.displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst, (item: any) => item.worktimeCode),
                selectedWorkTimeCD: step1.getWorkTime(),
                isSelectWorkTime: 1,
            })
                .then((f: any) => {
                    let workTypeCode;
                    let workTimeCode;
                    if (!f) {

                        return;
                    }
                    workTypeCode = f.selectedWorkType.workTypeCode;
                    workTimeCode = f.selectedWorkTime.code;
                    step1.setWorkCode(
                        workTypeCode,
                        f.selectedWorkType.name,
                        workTimeCode,
                        f.selectedWorkTime.name,
                        f.selectedWorkTime.workTime1,
                        self.model.displayInfoOverTime
                    );
                    let command = {

                    } as ParamSelectWorkMobile;
                    command.companyId = self.user.companyId;
                    command.employeeId = self.modeNew ? self.user.employeeId : self.appDispInfoStartupOutput.appDetailScreenInfo.application.employeeID;
                    command.dateOp = self.modeNew ? self.date : self.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;
                    command.workTypeCode = step1.workInfo.workType.code;
                    command.workTimeCode = step1.workInfo.workTime.code;
                    command.actualContentDisplay = _.get(self.model.displayInfoOverTime, 'appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst[0]');
                    command.overtimeAppSet = self.model.displayInfoOverTime.infoNoBaseDate.overTimeAppSet;
                    command.agent = false;
                    self.$mask('show');

                    return self.$http.post(
                        'at',
                        API.selectWorkInfo,
                        command
                    );

                })
                .then((res: any) => {
                    if (res) {
                        let step1 = self.$refs.step1 as KafS05Step1Component;
                        // call API select work info
                        let infoWithDateApplicationOp = _.get(self.model.displayInfoOverTime, 'infoWithDateApplicationOp') as InfoWithDateApplication;
                        if (!_.isNil(infoWithDateApplicationOp)) {
                            infoWithDateApplicationOp.breakTime = res.data.breakTimeZoneSetting;
                            infoWithDateApplicationOp.applicationTime = res.data.applicationTime;
                            infoWithDateApplicationOp.workHours = res.data.workHours;
                            infoWithDateApplicationOp.workTypeCD = step1.workInfo.workType.code;
                            infoWithDateApplicationOp.workTimeCD = step1.workInfo.workTime.code;
                        } else {
                            infoWithDateApplicationOp = {} as InfoWithDateApplication;
                            infoWithDateApplicationOp.applicationTime = res.data.applicationTime;
                            infoWithDateApplicationOp.workHours = res.data.workHours;
                            infoWithDateApplicationOp.breakTime = res.data.breakTimeZoneSetting;
                            infoWithDateApplicationOp.workTypeCD = step1.workInfo.workType.code;
                            infoWithDateApplicationOp.workTimeCD = step1.workInfo.workTime.code;
                            self.model.displayInfoOverTime.infoWithDateApplicationOp = infoWithDateApplicationOp;
                        }
                        step1.loadData(self.model.displayInfoOverTime, true, true);
                        step1.createHoursWorkTime();
                    }
                

                })
                .catch((res: any) => {
                    self.$nextTick(() => {
                        self.$mask('hide');
                    });
                    // xử lý lỗi nghiệp vụ riêng
                    self.handleErrorCustom(res).then((result: any) => {
                        if (result) {
                            // xử lý lỗi nghiệp vụ chung
                            self.handleErrorCommon(res);
                        }
                    });
                })
                .then(() => self.$mask('hide'));
        } else {
            self.$modal(
                'worktime',
                {
                    isAddNone: 1,
                    seledtedWkTimeCDs: _.map(self.model.displayInfoOverTime.appDispInfoStartup.appDispInfoWithDateOutput.opWorkTimeLst, (item: any) => item.worktimeCode),
                    selectedWorkTimeCD: step1.getWorkTime(),
                    isSelectWorkTime: 1
                }
            ).then((f: any) => {
                if (f) {
                    if (!f) {

                        return;
                    }
                    step1.setWorkTime(
                        f.selectedWorkTime.code,
                        f.selectedWorkTime.name,
                        f.selectedWorkTime.workTime1,
                    );

                    let command = {

                    } as ParamSelectWorkMobile;
                    command.companyId = self.user.companyId;
                    command.employeeId = self.modeNew ? self.user.employeeId : self.appDispInfoStartupOutput.appDetailScreenInfo.application.employeeID;
                    command.dateOp = self.modeNew ? self.date : self.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;

                    command.workTypeCode = step1.workInfo.workType.code;
                    command.workTimeCode = step1.workInfo.workTime.code;
                    command.actualContentDisplay = _.get(self.model.displayInfoOverTime, 'appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst[0]');
                    command.overtimeAppSet = self.model.displayInfoOverTime.infoNoBaseDate.overTimeAppSet;
                    self.$mask('show');

                    return self.$http.post(
                        'at',
                        API.selectWorkInfo,
                        command
                    );
                }
            })
                .then((res: any) => {
                    if (res) {
                        // call API select work info
                        let step1 = self.$refs.step1 as KafS05Step1Component;
                        let infoWithDateApplicationOp = _.get(self.model.displayInfoOverTime, 'infoWithDateApplicationOp') as InfoWithDateApplication;
                        if (!_.isNil(infoWithDateApplicationOp)) {
                            infoWithDateApplicationOp.breakTime = res.data.breakTimeZoneSetting;
                            infoWithDateApplicationOp.applicationTime = res.data.applicationTime;
                            infoWithDateApplicationOp.workHours = res.data.workHours;
                            infoWithDateApplicationOp.workTypeCD = step1.workInfo.workType.code;
                            infoWithDateApplicationOp.workTimeCD = step1.workInfo.workTime.code;
                        } else {
                            infoWithDateApplicationOp = {} as InfoWithDateApplication;
                            infoWithDateApplicationOp.applicationTime = res.data.applicationTime;
                            infoWithDateApplicationOp.workHours = res.data.workHours;
                            infoWithDateApplicationOp.breakTime = res.data.breakTimeZoneSetting;
                            infoWithDateApplicationOp.workTypeCD = step1.workInfo.workType.code;
                            infoWithDateApplicationOp.workTimeCD = step1.workInfo.workTime.code;
                            self.model.displayInfoOverTime.infoWithDateApplicationOp = infoWithDateApplicationOp;
                        }
                        step1.loadData(self.model.displayInfoOverTime, true, true);
                        step1.createHoursWorkTime();
                    }

                })
                .catch((res: any) => {
                    self.$nextTick(() => {
                        self.$mask('hide');
                    });
                    // xử lý lỗi nghiệp vụ riêng
                    self.handleErrorCustom(res).then((result: any) => {
                        if (result) {
                            // xử lý lỗi nghiệp vụ chung
                            self.handleErrorCommon(res);
                        }
                    });
                })
                .then(() => self.$mask('hide'));
        }

    }

    public getBreakTime(command: ParamBreakTime) {
        const self = this;
        self.$mask('show');
        self.$http.post(
            'at',
            API.getBreakTime,
            command
        )
            .then((res: any) => {
                let infoWithDateApplicationOp = _.get(self.model.displayInfoOverTime, 'infoWithDateApplicationOp') as InfoWithDateApplication;
                if (!_.isNil(infoWithDateApplicationOp)) {
                    infoWithDateApplicationOp.breakTime = infoWithDateApplicationOp.breakTime || {} as any;
                    infoWithDateApplicationOp.breakTime.timeZones = res.data.timeZones;
                } else {
                    infoWithDateApplicationOp = {} as InfoWithDateApplication;
                    infoWithDateApplicationOp.breakTime = {} as any;
                    infoWithDateApplicationOp.breakTime.timeZones = res.data.timeZones;
                }
                let step1 = self.$refs.step1 as KafS05Step1Component;
                if (!_.isNil(step1)) {
                    step1.createBreakTime(res.data.timeZones);
                }

            })
            .catch((res: any) => {
                self.handleErrorMessage(res);
            })
            .then(() => self.$mask('hide'));
    }

    public backToStep1(res: InitParam) {
        const vm = this;

        
        vm.toStep(1);
        vm.modeNew = false;
        let model = {} as Model;
        model.appOverTime = res.appDetail.appOverTime as AppOverTime;
        model.displayInfoOverTime = res.appDetail.displayInfoOverTime as DisplayInfoOverTime;
        vm.appDispInfoStartupOutput = res.appDispInfoStartupOutput;
        vm.model = model;

        if (vm.modeNew) {
            if (vm.$route.query.overworkatr == '0') {
                vm.overTimeClf = 0;
            } else if (vm.$route.query.overworkatr == '1') {
                vm.overTimeClf = 1;
            } else {
                vm.overTimeClf = 2;
            }
        } else {
            vm.overTimeClf = _.get(vm.model, 'appOverTime.overTimeClf');
        }
        if (_.isNil(vm.overTimeClf)) {
            vm.overTimeClf = 2;
        }
        if (vm.overTimeClf == 0) {
            vm.pgName = 'kafs05step1';
        } else if (vm.overTimeClf == 1) {
            vm.pgName = 'kafs05step2';
        } else {
            vm.pgName = 'kafs05step3';
        }
        vm.fetchData();
    }

    @Watch('params')
    public paramsWatcher() {
        const vm = this;
        if (!_.isNil(vm.params) && vm.params.isDetailMode) {
            vm.modeNew = false;
            let model = {} as Model;
            model.appOverTime = vm.params.appDetail.appOverTime as AppOverTime;
            model.displayInfoOverTime = vm.params.appDetail.displayInfoOverTime as DisplayInfoOverTime;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
            vm.model = model;
        } else {
            vm.modeNew = true;
        }

        if (vm.modeNew) {
            if (vm.$route.query.overworkatr == '0') {
                vm.overTimeClf = 0;
            } else if (vm.$route.query.overworkatr == '1') {
                vm.overTimeClf = 1;
            } else {
                vm.overTimeClf = 2;
            }
        } else {
            vm.overTimeClf = _.get(vm.model, 'appOverTime.overTimeClf');
        }

        if (_.isNil(vm.overTimeClf)) {
            vm.overTimeClf = 2;
        }
        if (vm.overTimeClf == 0) {
            vm.pgName = 'kafs05step1';
        } else if (vm.overTimeClf == 1) {
            vm.pgName = 'kafs05step2';
        } else {
            vm.pgName = 'kafs05step3';
        }
        vm.fetchData();
    }

}
const API = {
    start: 'at/request/application/overtime/mobile/start',
    changeDate: 'at/request/application/overtime/mobile/changeDate',
    getBreakTime: 'at/request/application/overtime/mobile/breakTimes',
    selectWorkInfo: 'at/request/application/overtime/mobile/selectWorkInfo',
    calculate: 'at/request/application/overtime/mobile/calculate',
    checkBeforeRegister: 'at/request/application/overtime/mobile/checkBeforeInsert',
    register: 'at/request/application/overtime/mobile/insert',
    reflectApp: 'at/request/application/reflect-app',

    sendMailAfterRegisterSample: ''
};

