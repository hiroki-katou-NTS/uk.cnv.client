import { Vue, _ } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { ApprovedComponent } from '@app/components';
import { IApprovalPhase, AppDetailScreenInfo } from 'views/cmm/s45/shr/index.d';
import { Phase } from 'views/cmm/s45/shr/index';
import { AppType, AppTypeName } from 'views/kaf/s00/shr';

import {
    CmmS45ComponentsApp2Component,
    CmmS45ComponentsApp3Component,
    CmmS45ComponentsApp4Component,
    CmmS45ComponentsApp5Component,
    CmmS45ComponentsApp9Component,
    CmmS45ShrComponentsApp7Component,
    CmmS45ShrComponentsApp15Component,
    CmmS45ShrComponentsAppsampleComponent,
    CmmS45ShrComponentsApp0Component,
    CmmS45ShrComponentsApp8Component,
    CmmS45ShrComponentsApp10Component,
    CmmS45ShrComponentsApp6Component,
    Reason
} from 'views/cmm/s45/shr/components';

import { CmmS45ShrComponentsApp1Component } from 'views/cmm/s45/shr/components/app1/index';

@component({
    name: 'cmms45c',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: [],
    components: {
        // khai báo virtual tag name
        'approved': ApprovedComponent,
        'appsample': CmmS45ShrComponentsAppsampleComponent,
        'app2': CmmS45ComponentsApp2Component,
        'app3': CmmS45ComponentsApp3Component,
        'app4': CmmS45ComponentsApp4Component,
        'app5': CmmS45ComponentsApp5Component,
        'app9': CmmS45ComponentsApp9Component,
        'app7': CmmS45ShrComponentsApp7Component,
        'app15': CmmS45ShrComponentsApp15Component,
        'app0': CmmS45ShrComponentsApp0Component,
        'app8': CmmS45ShrComponentsApp8Component,
        'app1': CmmS45ShrComponentsApp1Component,
        'app10': CmmS45ShrComponentsApp10Component,
        'app6': CmmS45ShrComponentsApp6Component,
        'render': {
            template: `<div class="">{{params.id}} {{params.name}}</div>`,
            props: ['params']
        }
    }
})
export class CmmS45CComponent extends Vue {
    @Prop({ default: () => ({ listAppMeta: [], currentApp: '' }) })
    public readonly params: { listAppMeta: Array<string>, currentApp: string, action: number };
    public title: string = 'CmmS45C';
    public showApproval: boolean = false;
    public appCount: number = 0;
    public selected: number = 0;
    public listAppMeta: Array<string> = [];
    public currentApp: string = '';
    // 承認ルートインスタンス
    public phaseLst: Array<Phase> = [];
    public appState: { appStatus: number, reflectStatus: number, version: number, pastApp: boolean } = { appStatus: null, reflectStatus: null, version: null, pastApp: null };
    public appType: number = 99;
    public appTransferData: any = {
        appDispInfoStartupOutput: null,
        appDetail: null,
        isDetailMode: true
    };
    // 差し戻し理由
    public reversionReason: string = '';
    public isLoadingComplete = false;
    public reasons: Array<Reason> = null;

    public opAppStartDate: Date;
    public opAppEndDate: Date;

    public created() {
        let self = this;
        self.listAppMeta = self.params.listAppMeta;
        self.currentApp = self.params.currentApp;
        self.appCount = _.indexOf(self.listAppMeta, self.currentApp);
        Object.defineProperty(self.appState, 'getName', {
            get() {
                switch (this.appStatus) {
                    case ReflectedState.NOTREFLECTED: return 'CMMS45_7'; // 反映状態 = 未反映
                    case ReflectedState.WAITREFLECTION: return 'CMMS45_8'; // 反映状態 = 反映待ち
                    case ReflectedState.REFLECTED: return 'CMMS45_9'; // 反映状態 = 反映済
                    case ReflectedState.CANCELED: return 'CMMS45_10'; // 反映状態 = 取消済
                    case ReflectedState.REMAND: return 'CMMS45_36'; // 反映状態 = 差し戻し
                    case ReflectedState.DENIAL: return 'CMMS45_11'; // 反映状態 = 否認
                    default: break;
                }
            }
        });

        Object.defineProperty(self.appState, 'getClass', {
            get() {
                switch (this.appStatus) {
                    case ReflectedState.NOTREFLECTED: return 'apply-unapproved'; // 反映状態 = 未反映
                    case ReflectedState.WAITREFLECTION: return 'apply-approved'; // 反映状態 = 反映待ち
                    case ReflectedState.REFLECTED: return 'apply-reflected'; // 反映状態 = 反映済
                    case ReflectedState.CANCELED: return 'apply-cancel'; // 反映状態 = 取消済
                    case ReflectedState.REMAND: return 'apply-return'; // 反映状態 = 差し戻し
                    case ReflectedState.DENIAL: return 'apply-denial'; // 反映状態 = 否認
                    default: break;
                }
            }
        });

        Object.defineProperty(self.appState, 'getNote', {
            get() {
                switch (this.appStatus) {
                    case ReflectedState.NOTREFLECTED: return 'CMMS45_39'; // 反映状態 = 未反映
                    case ReflectedState.WAITREFLECTION: return 'CMMS45_37'; // 反映状態 = 反映待ち
                    case ReflectedState.REFLECTED: return 'CMMS45_38'; // 反映状態 = 反映済
                    case ReflectedState.CANCELED: return 'CMMS45_42'; // 反映状態 = 取消済
                    case ReflectedState.REMAND: return 'CMMS45_40'; // 反映状態 = 差し戻し
                    case ReflectedState.DENIAL: return 'CMMS45_41'; // 反映状態 = 否認
                    default: break;
                }
            }
        });
    }

    public mounted() {
        let self = this;
        self.isLoadingComplete = false;
        self.$mask('show');
        self.initData();
    }

    // lấy dữ liệu ban đầu
    public initData() {
        let self = this;
        self.selected = 0;
        self.$http.post('at', API.getDetailMob, self.currentApp)
            .then((successData: any) => {
                self.appTransferData.appDispInfoStartupOutput = successData.data;
                let appDetailScreenInfoDto: AppDetailScreenInfo = successData.data.appDetailScreenInfo;
                self.createPhaseLst(appDetailScreenInfoDto.approvalLst);
                self.appState.appStatus = appDetailScreenInfoDto.reflectPlanState;
                self.appState.reflectStatus = appDetailScreenInfoDto.reflectPlanState;
                self.appState.version = appDetailScreenInfoDto.application.version;
                self.appState.pastApp = appDetailScreenInfoDto.pastApp;
                self.reversionReason = appDetailScreenInfoDto.application.opReversionReason;
                self.appType = appDetailScreenInfoDto.application.appType;
                self.opAppStartDate = appDetailScreenInfoDto.application.opAppStartDate;
                self.opAppEndDate = appDetailScreenInfoDto.application.opAppEndDate;
                
                //self.$mask('hide');
            }).catch((res: any) => {
                // self.$mask('hide');
                if (res.messageId == 'Msg_426') {
                    self.$modal.error('Msg_426').then(() => {
                        self.back();
                    });
                } else {
                    // self.$modal.error(res.message).then(() => {
                    //     self.back();
                    // }); 
                    let promise;
                    if (res.messageId) {
                        promise = self.$modal.error({ messageId: res.messageId });
                    } else {

                        if (_.isArray(res.errors)) {
                            promise = self.$modal.error({ messageId: res.errors[0].messageId });
                        } else {
                            promise = self.$modal.error({ messageId: res.errors.messageId });
                        }
                    }
                    promise.then(() => {
                        self.back();
                    });
                }
            });
    }

    // tạo dữ liệu người phê duyệt
    public createPhaseLst(listPhase: Array<IApprovalPhase>): void {
        let self = this;
        let phaseLstConvert: Array<Phase> = [];
        for (let i: number = 1; i <= 5; i++) {
            let containPhase: IApprovalPhase = _.find(listPhase, (phase: IApprovalPhase) => phase.phaseOrder == i);
            phaseLstConvert.push(new Phase(containPhase));
        }
        self.phaseLst = phaseLstConvert;
        self.selected = self.getSelectedPhase();
    }

    // lấy phase chỉ định 
    private getSelectedPhase(): number {
        let self = this;
        let denyPhase: Phase = _.find(self.phaseLst, (phase: Phase) => phase.approvalAtrValue == 2);
        if (denyPhase) {
            return denyPhase.phaseOrder - 1;
        }
        let returnPhase: Phase = _.find(self.phaseLst, (phase: Phase) => phase.approvalAtrValue == 3);
        if (returnPhase) {
            return returnPhase.phaseOrder - 1;
        }
        let unapprovePhaseLst: Array<Phase> = _.filter(self.phaseLst,
            (phase: Phase) => phase.approvalAtrValue == 0 || phase.approvalAtrValue == 4);
        if (unapprovePhaseLst.length > 0) {
            return _.sortBy(unapprovePhaseLst, 'phaseOrder').reverse()[0].phaseOrder - 1;
        }
        let approvePhaseLst: Array<Phase> = _.filter(self.phaseLst, (phase: Phase) => phase.approvalAtrValue == 1);
        if (approvePhaseLst.length > 0) {
            return _.sortBy(approvePhaseLst, 'phaseOrder')[0].phaseOrder - 1;
        }

        return 0;
    }

    // tiến tới đơn tiếp theo
    public toNextApp(): void {
        let self = this;
        self.$el.scrollTop = 0;
        self.showApproval = false;
        self.appCount++;
        self.currentApp = self.listAppMeta[self.appCount];
        self.reasons = null;
        self.isLoadingComplete = false;
        self.$mask('show');
        self.initData();
    }

    // quay về đơn trước
    public toPreviousApp(): void {
        let self = this;
        self.$el.scrollTop = 0;
        self.showApproval = false;
        self.appCount--;
        self.currentApp = self.listAppMeta[self.appCount];
        self.reasons = null;
        self.isLoadingComplete = false;
        self.$mask('show');
        self.initData();
    }

    // kiểm tra có phải đơn đầu tiên không
    public isFirstApp(): boolean {
        let self = this;

        return self.appCount == 0;
    }

    // kiểm tra có phải đơn cuối cùng không
    public isLastApp(): boolean {
        let self = this;

        return self.appCount == self.listAppMeta.length - 1;
    }

    // kiểm tra list đơn xin rỗng
    public isEmptyApp(): boolean {
        return this.listAppMeta.length == 0;
    }

    // ẩn hiện người phê duyệt
    public reverseApproval(): void {
        let self = this;
        self.showApproval = !self.showApproval;
    }

    // quay về màn CMMS45A
    public back(reloadValue?: boolean) {
        let self = this;
        if (self.$router.currentRoute.name == 'cmms45a') {
            self.$close(self.params);
        } else {
            self.$goto('cmms45a', { 'CMMS45_FromMenu': true });
        }
    }
    public loadingComplete(reasons?: any) {
        const self = this;
        self.reasons = reasons;
        self.$nextTick(() => {
            self.$mask('hide');
            self.isLoadingComplete = true;
        });

    }

    // kích hoạt nút xóa đơn
    public deleteApp(): void {
        let self = this;
        self.$modal.confirm('Msg_18')
            .then((v) => {
                if (v == 'yes') {
                    self.$mask('show');
                    let hdsubRecLinkData: any = null;

                    // delete for KAFS11: START

                    if (self.appType == 10) {
                        let appDetailKAFS11 = self.appTransferData.appDetail;
                        if (appDetailKAFS11.abs && appDetailKAFS11.rec) {
                            hdsubRecLinkData = {
                                absId: appDetailKAFS11.abs.application.appID, 
                                recId: appDetailKAFS11.rec.application.appID,
                                linkApp: appDetailKAFS11.appDispInfoStartup.appDetailScreenInfo.application.appID == appDetailKAFS11.abs.application.appID 
                                    ? appDetailKAFS11.rec.application : appDetailKAFS11.abs.application
                            };
                        }
                    }

                    // delete for KAFS11: END


                    self.$http.post('at', API.delete, {
                        appDispInfoStartupOutput: self.appTransferData.appDispInfoStartupOutput, 
                        hdsubRecLinkData
                    }).then((resDelete: any) => {
                        self.$mask('hide');
                        self.$modal.info('Msg_16').then(() => {
                            self.params.action = 1;
                            self.back();
                        });
                    }).catch((res: any) => {
                        self.$mask('hide');
                        self.$modal.error(res.messageId).then(() => {
                            self.back();
                        });
                    });
                }
            });
    }

    // hiển thị nút xóa đơn
    public get displayDeleteButton() {
        let self = this;
        if (self.appState.reflectStatus == null) {
            return true;
        }

        return self.appState.reflectStatus == ReflectedState.NOTREFLECTED || self.appState.reflectStatus == ReflectedState.REMAND;
    }

    // hiển thị nút cập nhật đơn
    public get displayUpdateButton() {
        let self = this;
        if (self.appState.reflectStatus == null) {
            return true;
        }

        return self.appState.reflectStatus == ReflectedState.NOTREFLECTED || self.appState.reflectStatus == ReflectedState.REMAND;
    }

    // hiển thị menu chỉnh sửa đơn
    public get displayEditFloat() {
        let self = this;
        if (self.appState.pastApp) {
            return false;        
        }

        return self.displayDeleteButton || self.displayUpdateButton || self.displayCancelButton;
    }

    // tiến tới màn chi tiết KAF005
    public updateApp(): void {
        const self = this;
        _.set(self.appTransferData.appDetail, 'isDetailMode', true);
        switch (self.appType) {
            case 2:
                if (self.$router.currentRoute.name == 'kafs07a') {
                    self.$close(self.appTransferData.appDetail);
                } else {
                    self.$goto('kafs07a', self.appTransferData.appDetail);
                }
                break;
            case 3:
                if (self.$router.currentRoute.name == 'kafs08a') {
                    self.$close(self.appTransferData.appDetail);
                } else {
                    self.$goto('kafs08a', self.appTransferData.appDetail);
                }
                break;
            case 4:
                if (self.$router.currentRoute.name == 'kafs09a') {
                    self.$close(self.appTransferData.appDetail);
                } else {
                    self.$goto('kafs09a', self.appTransferData.appDetail);
                }
                break;
            case 7:
                if (self.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.opStampRequestMode == 0) {
                    if (self.$router.currentRoute.name == 'kafs02a') {
                        self.$close(self.appTransferData.appDetail);
                    } else {
                        self.$goto('kafs02a', self.appTransferData.appDetail);
                    }
                }
                if (self.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.opStampRequestMode == 1) {
                    if (self.$router.currentRoute.name == 'kafs02c') {
                        self.$close(self.appTransferData.appDetail);
                    } else {
                        self.$goto('kafs02c', self.appTransferData.appDetail);
                    }
                    self.$goto('kafs02c', self.appTransferData.appDetail);
                }
                break;
            case 8:
                if (self.$router.currentRoute.name == 'kafs12a') {
                    self.$close(self.appTransferData);
                } else {
                    self.$goto('kafs12a', self.appTransferData);
                }
                break;
            case 9:
                if (self.$router.currentRoute.name == 'kafs04a') {
                    self.$close(self.appTransferData.appDetail);
                } else {
                    self.$goto('kafs04a', self.appTransferData.appDetail);
                }
                break;
            case 15:
                if (self.$router.currentRoute.name == 'kafs20a') {
                    self.$close(self.appTransferData);
                } else {
                    self.$goto('kafs20a', self.appTransferData);
                }
                break;
            case 0: 
                if (self.$router.currentRoute.name == 'kafs05a') {
                    self.$close(self.appTransferData);
                } else {
                    self.$goto('kafs05a', self.appTransferData);
                }
                break;
            case 1: 
                if (self.$router.currentRoute.name == 'kafs06a') {
                    self.$close(self.appTransferData);
                } else {
                    self.$goto('kafs06a', self.appTransferData);
                }
                break;  
            case 10: 
                if (self.$router.currentRoute.name == 'kafs11a') {
                    self.$close(self.appTransferData);
                } else {
                    self.$goto('kafs11a', self.appTransferData);
                }
                break;  
            case 6: 
                if (self.$router.currentRoute.name == 'kafs10a') {
                    self.$close(self.appTransferData);
                } else {
                    self.$goto('kafs10a', self.appTransferData);
                }
                break;
            default:
                break;
        }

        // if (self.$router.currentRoute.name == 'kafS05b') {
        //     self.$close({ appID: self.currentApp });
        // } else {
        //     self.$goto('kafS05b', { appID: self.currentApp }); 
        // }
    }

    public cancelApp() {
        const self = this;
        self.$modal.confirm('Msg_249')
            .then((v) => {
                if (v == 'yes') {
                    self.$mask('show');
                    self.$http.post('at', API.cancel, self.appTransferData.appDispInfoStartupOutput)
                    .then((resCancel: any) => {
                        self.$mask('hide');
                        if (resCancel.data) {
                            self.$modal.info('Msg_224').then(() => { self.initData(); });
                        } else {
                            self.$modal.info('Msg_2188').then(() => { self.initData(); });
                        }
                    }).catch((failCancel: any) => {
                        self.$mask('hide');
                        if (failCancel.messageId == 'Msg_197') {
                            self.$modal.error(failCancel.messageId)
                                .then(() => {
                                    self.initData();
                                });
                        } else {
                            self.$modal.error(failCancel.messageId)
                                .then(() => {
                                    self.back();
                                });
                        }
                    });           
                }
            });
    }

    get displayCancelButton() {
        const vm = this;
        if (vm.appState.reflectStatus == null) {
            return true;
        }

        return vm.appState.reflectStatus != ReflectedState.CANCELED;
    }

    get applicant() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }
        let applicantID = vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.employeeID,
            employeeInfoLst = vm.appTransferData.appDispInfoStartupOutput.appDispInfoNoDateOutput.employeeInfoLst,
            empInfo = _.find(employeeInfoLst, (o: any) => o.sid == applicantID);
        if (empInfo) {
            return empInfo.bussinessName;
        }

        return '';
    }

    get representerDisp() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return false;
        }
        let employeeID = vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.employeeID,
            enteredPerson = vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.enteredPerson;
        if (employeeID == enteredPerson) {
            return false;
        } else {
            return true;
        }
    }

    get representer() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return false;
        }
        if (vm.representerDisp) {
            return vm.appTransferData.appDispInfoStartupOutput.appDispInfoNoDateOutput.opEmployeeInfo.bussinessName;
        }

        return '';
    }

    get appDate() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }
        let appDate = vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate;

        return vm.$dt(new Date(appDate), 'YYYY/MM/DD(dd)');
    }

    get appTypeName() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }
        switch (vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.appType) {
            case AppType.OVER_TIME_APPLICATION:
                return AppTypeName.OVER_TIME_APPLICATION;
                break;
            case AppType.ABSENCE_APPLICATION:
                return AppTypeName.ABSENCE_APPLICATION;
                break;
            case AppType.WORK_CHANGE_APPLICATION:
                return AppTypeName.WORK_CHANGE_APPLICATION;
                break;
            case AppType.BUSINESS_TRIP_APPLICATION:
                return AppTypeName.BUSINESS_TRIP_APPLICATION;
                break;
            case AppType.GO_RETURN_DIRECTLY_APPLICATION:
                return AppTypeName.GO_RETURN_DIRECTLY_APPLICATION;
                break;
            case AppType.LEAVE_TIME_APPLICATION:
                return AppTypeName.LEAVE_TIME_APPLICATION;
                break;
            case AppType.STAMP_APPLICATION:
                return AppTypeName.STAMP_APPLICATION;
                break;
            case AppType.ANNUAL_HOLIDAY_APPLICATION:
                return AppTypeName.ANNUAL_HOLIDAY_APPLICATION;
                break;
            case AppType.EARLY_LEAVE_CANCEL_APPLICATION:
                return AppTypeName.EARLY_LEAVE_CANCEL_APPLICATION;
                break;
            case AppType.COMPLEMENT_LEAVE_APPLICATION:
                return AppTypeName.COMPLEMENT_LEAVE_APPLICATION;
                break;
            case AppType.OPTIONAL_ITEM_APPLICATION:
                return AppTypeName.OPTIONAL_ITEM_APPLICATION;
                break;
            default:
                return '';
                break;
        }
    }

    get prePost() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }
        let prePostResource = [{
            code: 0,
            text: 'KAFS00_10'
        }, {
            code: 1,
            text: 'KAFS00_11'
        }];

        return _.find(prePostResource, (o: any) => o.code == vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.prePostAtr).text;
    }

    get inputDate() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }
        let appDate = vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.inputDate;

        return vm.$dt(new Date(appDate), 'YYYY/MM/DD HH:mm');
    }

    get comboReasonDisp() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return false;
        }

        return vm.appTransferData.appDispInfoStartupOutput.appDispInfoNoDateOutput.displayStandardReason == 0 ? false : true;
    }

    get textReasonDisp() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return false;
        }

        return vm.appTransferData.appDispInfoStartupOutput.appDispInfoNoDateOutput.displayAppReason == 0 ? false : true;
    }

    get comboReason() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }
        let dropdownList = vm.appTransferData.appDispInfoStartupOutput.appDispInfoNoDateOutput.reasonTypeItemLst,
            opComboReason = _.find(dropdownList, (o: any) => {
                return o.appStandardReasonCD == vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppStandardReasonCD;
            });
        if (opComboReason) {
            return opComboReason.reasonForFixedForm;
        }
        if (_.isEmpty(vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppStandardReasonCD)) {
            return '';
        }

        return vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppStandardReasonCD + ' ' + vm.$i18n('CMMS45_87');
    }

    get textReason() {
        const vm = this;
        if (!vm.appTransferData.appDispInfoStartupOutput) {
            return '';
        }

        return _.escape(vm.appTransferData.appDispInfoStartupOutput.appDetailScreenInfo.application.opAppReason).replace(/\n/g, '<br/>');
    }

}

export enum ReflectedState {
    NOTREFLECTED = 0, // 未反映
    WAITREFLECTION = 1, // 反映待ち
    REFLECTED = 2, // 反映済
    CANCELED = 3, // 取消済
    REMAND = 4, // 差し戻し
    DENIAL = 5 // 否認
}

const API = {
    delete: 'at/request/application/deleteapp',
    getDetailMob: 'at/request/app/smartphone/getDetailMob',
    cancel: 'at/request/application/cancelapp'
};