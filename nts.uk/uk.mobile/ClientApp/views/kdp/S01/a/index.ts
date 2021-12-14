import { component, Prop } from '@app/core/component';
import { _, Vue, moment } from '@app/provider';
import { model } from 'views/kdp/S01/shared/index.d';
import { KdpS01BComponent } from '../b/index';
import { KdpS01CComponent } from '../c/index';
import { KdpS01SComponent } from '../s/index';
import { KdpS01TComponent } from '../t/index';

const basePath = 'at/record/stamp/smart-phone/';

const servicePath = {
    checkCanUseStamp: basePath + 'check-can-use-stamp',
    getSetting: basePath + 'get-setting/',
    registerStamp: basePath + 'register-stamp',
    getSuppress: basePath + 'get-suppress',
    getOmission: basePath + 'get-omission',
    getSettingStampCommon: 'at/record/stamp/settings_stamp_common'
};

@component({
    name: 'kdpS01a',
    route: '/kdp/s01/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    constraints: [],
    components: {
        'screenB': KdpS01BComponent,
        'screenC': KdpS01CComponent,
        'screenS': KdpS01SComponent,
        'screenT': KdpS01TComponent
    }
})


export class KdpS01AComponent extends Vue {
    @Prop({ default: () => ({}) })
    public params!: any;
    public setting: ISetting = {
        displaySettingsStampScreen: {
            serverCorrectionInterval: 10,
            settingDateTimeColor: {
                textColor: '',
                backgroundColor: ''
            },
            resultDisplayTime: 10
        },
        lstDisplayItemId: [],
        usrAtrValue: 1,
        stampPageComment: {
            pageComment: '',
            commentColor: '#fabf8f'
        },
        buttons: [
        ]
    };

    public settingStampCommon: ISettingsStampCommon = {
        supportUse: false,
        temporaryUse: false,
        workUse: false
    };

    public locationInfoUse: boolean = false;

    public get textComment() {
        const vm = this;
        const { setting } = vm;

        if (setting) {
            return _.escape(_.get(setting, 'stampPageComment.pageComment', '')).replace(/\n/g, '<br />');
        }

        return '';
    }

    public created() {
        let vm = this;
        vm.startPage();
    }

    private startPage() {
        let vm = this,
            param = {};

        vm.$mask('show');
        vm.$http.post('at', servicePath.checkCanUseStamp, param).then((result: any) => {

            vm.$mask('hide');

            let used: CanEngravingUsed = result.data.used;

            if (used !== CanEngravingUsed.AVAILABLE) {

                vm.$modal.error({ messageId: vm.getErrorMsg(used), messageParams: ['KDPS01_1'] }).then(() => {

                    vm.$goto('ccg008a');
                });
            } else {
                vm.$mask('show');
                vm.$http.post('at', servicePath.getSetting).then((result: any) => {
                    vm.$mask('hide');
                    let data: model.ISettingSmartPhone = result.data;

                    vm.$http.post('at', servicePath.getSettingStampCommon).then((result: any) => {  
                        vm.settingStampCommon.supportUse = result.data.supportUse;
                        vm.settingStampCommon.temporaryUse = result.data.temporaryUse;
                        vm.locationInfoUse = data.setting.locationInfoUse;
                    
                        if (_.has(data, 'setting.pageLayoutSettings') && data.setting.pageLayoutSettings.length > 0) {                          

                            let page = _.find(data.setting.pageLayoutSettings, ['pageNo', 1]) as model.IStampPageLayoutDto;

                            if (page) {

                                if (page.lstButtonSet.length > 0) {
                                    vm.setting.buttons = vm.getLstButton(page.lstButtonSet, data.stampToSuppress);
                                }

                                vm.setting.stampPageComment = page.stampPageComment;

                            } else {
                                vm.$modal.error('Not Found Button Data');
                            }

                        } else {
                            vm.$modal.error('Not Found Button Data');
                        }

                        if (_.has(data, 'setting.displaySettingsStampScreen')) {
                            vm.setting.displaySettingsStampScreen = data.setting.displaySettingsStampScreen;
                            vm.InitCountTime();

                        }

                        if (data.resulDisplay) {
                            vm.setting.usrAtrValue = data.resulDisplay.usrAtrValue;
                            vm.setting.lstDisplayItemId = _.map(data.resulDisplay.lstDisplayItemId, (x) => x.displayItemId);
                        }
                    }).catch((res: any) => {
                        vm.showError(res);
                    });
                }).catch((res: any) => {
                    vm.showError(res);
                });
            }

        }).catch((res: any) => {
            vm.showError(res);
        });
    }

    private InitCountTime() {

        let vm = this,
            interval = _.get(vm, 'setting.displaySettingsStampScreen.serverCorrectionInterval', 1) as number * 60000;

        vm.$dt.interval(interval);

        setInterval(() => {
            vm.getStampToSuppress();
        }, interval);
    }

    private getStampToSuppress() {
        let vm = this;

        vm.$http.post('at', servicePath.getSuppress).then((result: any) => {

            let stampToSuppress: model.IStampToSuppress = result.data;

            _.forEach(vm.setting.buttons, function (button) {

                vm.setBtnColor(button, stampToSuppress);

            });
        });

    }

    private getLstButton(lstButtonSet: Array<model.ButtonSettingsDto>, stampToSuppress: model.IStampToSuppress) {

        let vm = this,
            buttonCount = 6,
            resultList: Array<model.ButtonSettingsDto> = [];


        for (let i = 1; i <= buttonCount; i++) {

            let button = _.find(lstButtonSet, { 'buttonPositionNo': i, 'usrArt': 1 }),
                buttonSetting: model.ButtonSettingsDto = {
                    buttonValueType: -1,
                    buttonPositionNo: i,
                    buttonDisSet: {
                        buttonNameSet: {
                            textColor: '',
                            buttonName: ''
                        },
                        backGroundColor: '',
                        displayBackGroundColor: ''
                    },
                    usrArt: 1,
                    buttonType: null,
                    icon:''
                };
            
            if (button) {
                let btnType = vm.checkType(button.buttonType.stampType == null ? null : button.buttonType.stampType.changeClockArt, 
                    button.buttonType.stampType == null ? null : button.buttonType.stampType.changeCalArt, 
                    button.buttonType.stampType == null ? null : button.buttonType.stampType.setPreClockArt, 
                    button.buttonType.stampType == null ? null : button.buttonType.stampType.changeHalfDay, 
                    button.buttonType.reservationArt);
                
                // 応援利用＝Trueの場合				
                if (vm.settingStampCommon.supportUse === true && _.includes ([14, 15, 16, 17, 18], btnType)) {
                    buttonSetting = button;
                    
                }
                // 臨時利用＝Trueの場合
                if (vm.settingStampCommon.temporaryUse === true && _.includes ([12, 13], btnType)) {
                    buttonSetting = button;
                }

                if (_.includes ([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 19, 20], btnType)) {
                    buttonSetting = button;
                }

                buttonSetting.icon = vm.getIcon(button.buttonType.stampType == null ? null : button.buttonType.stampType.changeClockArt, 
                    button.buttonType.stampType == null ? null : button.buttonType.stampType.changeCalArt, 
                    button.buttonType.stampType == null ? null : button.buttonType.stampType.setPreClockArt, 
                    button.buttonType.stampType == null ? null : button.buttonType.stampType.changeHalfDay, 
                    button.buttonType.reservationArt) + '.png';
            }
            vm.setBtnColor(buttonSetting, stampToSuppress);
            resultList.push(buttonSetting);
        }

        return resultList;
    }

    public stampClick(button: model.ButtonSettingsDto) {
        const STAMP_MEANS_SMARTPHONE = 5;
        let vm = this,
            command: model.IRegisterSmartPhoneStampCommand = {
                stampDatetime: moment(vm.$dt.now).format('YYYY/MM/DD HH:mm:ss'),
                stampButton: { pageNo: 1, buttonPositionNo: button.buttonPositionNo, stampMeans: STAMP_MEANS_SMARTPHONE },
                geoCoordinate: { latitude: null, longitude: null },
                refActualResult: { cardNumberSupport: null, overtimeDeclaration: null, workLocationCD: null, workTimeCode: null }
            };

        let latitude = null,
            longitude = null;
        navigator.geolocation.getCurrentPosition((position) => {
            if (position) {

                if (vm.locationInfoUse) {
                    latitude = position.coords.latitude;
                    longitude = position.coords.longitude;
                }

                command.geoCoordinate = { latitude, longitude };
                vm.$mask('show');

                vm.$http.post('at', servicePath.registerStamp, command)
                    .then((result: any) => {
                        vm.$mask('hide');
                        vm.getStampToSuppress();

                        if (!_.has(button, 'buttonType.stampType.changeClockArt')) {

                            vm.openDialogB(command.stampButton);
                        } else {
                            let changeClockArt = button.buttonType.stampType.changeClockArt;
                            switch (changeClockArt) {
                                case 1:
                                    if (vm.setting.usrAtrValue === 1) {
                                        vm.openDialogC(command.stampButton);
                                    } else {
                                        vm.openDialogB(command.stampButton);
                                    }
                                    break;
                                default:
                                    vm.openDialogB(command.stampButton);
                                    break;
                            }
                        }
                    }).catch((res: any) => {
                        vm.showError(res);
                    });
            }
        }, (error) => {
            command.geoCoordinate = { latitude, longitude };
            vm.$mask('show');

            vm.$http.post('at', servicePath.registerStamp, command)
                .then((result: any) => {
                    vm.$mask('hide');
                    vm.getStampToSuppress();

                    if (!_.has(button, 'buttonType.stampType.changeClockArt')) {

                        vm.openDialogB(command.stampButton);
                    } else {
                        let changeClockArt = button.buttonType.stampType.changeClockArt;
                        switch (changeClockArt) {
                            case 1:
                                if (vm.setting.usrAtrValue === 1) {
                                    vm.openDialogC(command.stampButton);
                                } else {
                                    vm.openDialogB(command.stampButton);
                                }
                                break;
                            default:
                                vm.openDialogB(command.stampButton);
                                break;
                        }
                    }
                }).catch((res: any) => {
                    vm.showError(res);
                });
        });

    }

    private openDialogB(stampButton: model.IStampButtonCommand) {

        let vm = this;
        vm.$auth.user.then((userInfo) => {
            vm.$modal(KdpS01BComponent, {
                resultDisplayTime: vm.setting.displaySettingsStampScreen.resultDisplayTime,
                employeeId: userInfo.employeeId,
                employeeCode: userInfo.employeeCode
            }).then(() => {
                vm.getStampToSuppress();
                vm.$http.post('at', servicePath.getOmission, stampButton).then((result: any) => {
                    let data: model.IGetOmissionContentDto = result.data;
                    if (data && data.errorInfo && data.errorInfo.length > 0) {
                        vm.openDialogT(data);
                    }
                });
            });
        });
    }

    private openDialogC(stampButton: model.IStampButtonCommand) {
        let vm = this;
        vm.$auth.user.then((userInfo) => {
            vm.$modal(KdpS01CComponent, {
                attendanceItemIds: vm.setting.lstDisplayItemId
            }
            ).then(() => {
                vm.getStampToSuppress();
                vm.$http.post('at', servicePath.getOmission, stampButton).then((result: any) => {
                    let data: model.IGetOmissionContentDto = result.data;

                    if (data && data.errorInfo && data.errorInfo.length > 0) {
                        console.log(data);
                        vm.openDialogT(data);
                    }
                });
            });
        });
    }

    public openDialogS() {
        let vm = this;
        vm.$modal(KdpS01SComponent, null, { title: 'KDPS01_22' }).then(() => {
        });
    }

    public openDialogT(data) {
        let vm = this;
        vm.$modal(KdpS01TComponent, data, { title: 'KDPS01_23' }).then(() => {

        });
    }

    private setBtnColor(buttonSetting: model.ButtonSettingsDto, stampToSuppress: model.IStampToSuppress) {

        const DEFAULT_GRAY = '#E8E9EB';
        let backGroundColor = _.get(buttonSetting, 'buttonDisSet.backGroundColor', DEFAULT_GRAY),
            valueType = backGroundColor;

        switch (buttonSetting.buttonValueType) {
            case ButtonType.GOING_TO_WORK:
                valueType = !stampToSuppress.goingToWork ? backGroundColor : DEFAULT_GRAY;
                break;

            case ButtonType.WORKING_OUT:
                valueType = !stampToSuppress.departure ? backGroundColor : DEFAULT_GRAY;
                break;

            case ButtonType.GO_OUT:
                valueType = !stampToSuppress.goOut ? backGroundColor : DEFAULT_GRAY;
                break;

            case ButtonType.RETURN:
                valueType = !stampToSuppress.turnBack ? backGroundColor : DEFAULT_GRAY;
                break;

            default:
                valueType = backGroundColor;
                break;
        }
        buttonSetting.buttonDisSet.backGroundColor = backGroundColor;
        buttonSetting.buttonDisSet.displayBackGroundColor = valueType;
    }

    private getErrorMsg(used: CanEngravingUsed) {
        const msgs = [{ value: CanEngravingUsed.NOT_PURCHASED_STAMPING_OPTION, msg: 'Msg_1644' },
        { value: CanEngravingUsed.ENGTAVING_FUNCTION_CANNOT_USED, msg: 'Msg_1645' },
        { value: CanEngravingUsed.UNREGISTERED_STAMP_CARD, msg: 'Msg_1619' }];

        let item = _.find(msgs, ['value', used]);

        return item ? item.msg : '';
    }

    private showError(res: any) {
        let vm = this;
        vm.$mask('hide');
        if (!_.isEqual(res.message, 'can not found message id')) {
            vm.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {
            vm.$modal.error(res.message);
        }
    }

    public mounted() {
        this.pgName = 'KDPS01_1';
    }

    public getIcon = (changeClockArt: any, changeCalArt: any, setPreClockArt: any, changeHalfDay: any, reservationArt: any) => {
        let vm = this;

        switch (vm.checkType(changeClockArt, changeCalArt, setPreClockArt, changeHalfDay, reservationArt)) {
            case 1: {
                return 205;
            }
            case 2: {
                return 206;
            }
            case 3: {
                return 207;
            }
            case 4: {
                return 208;
            }
            case 5: {
                return 209;
            }
            case 6: {
                return 210;
            }
            case 7: {
                return 211;
            }
            case 8: {
                return 212;
            }
            case 9: {
                return 213;
            }
            case 10: {
                return 214;
            }
            case 11: {
                return 215;
            }
            case 12: {
                return 216;
            }
            case 13: {
                return 217;
            }
            case 14: {
                return 218;
            }
            case 15: {
                return 219;
            }
            case 16: {
                return 220;
            }
            case 17: {
                return 221;
            }
            case 18: {
                return 222;
            }
            case 19: {
                return 223;
            }
            case 20: {
                return 224;
            }
            default: {
                return '';
            }
                
        }
    }

    public checkType = (changeClockArt: any, changeCalArt: any, setPreClockArt: any, changeHalfDay: any, reservationArt: any) => {
        if (changeCalArt == 0 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
            if (changeClockArt == 0) {
                return 1;
            }

            if (changeClockArt == 1) {
                return 5;
            }
                
            if (changeClockArt == 4) {
                return 8;
            }

            if (changeClockArt == 5) {
                return 9;
            }

            if (changeClockArt == 2) {
                return 10;
            }

            if (changeClockArt == 3) {
                return 11;
            }

            if (changeClockArt == 7) {
                return 12;
            }

            if (changeClockArt == 9) {
                return 13;
            }
                
            if (changeClockArt == 6) {
                return 14;
            }

            if (changeClockArt == 8) {
                return 15;
            }

            if (changeClockArt == 12) {
                return 16;
            }
                
        }

        if (changeClockArt == 0 && changeCalArt == 0 && setPreClockArt == 1 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
            return 2;
        }

        if (changeCalArt == 1 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
            if (changeClockArt == 0) {
                return 3;
            }

            if (changeClockArt == 6) {
                return 17;
            }
        }

        if (changeCalArt == 3 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
            if (changeClockArt == 0) {
                return 4;
            }

            if (changeClockArt == 6) {
                return 18;
            }
        }

        if (changeClockArt == 1 && changeCalArt == 0 && setPreClockArt == 2 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
            return 6;
        }

        if (changeClockArt == 1 && changeCalArt == 2 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
            return 7;
        }

        if ((changeClockArt == '' || changeClockArt == null) && (changeCalArt == '' || changeCalArt == null) && (setPreClockArt == '' || setPreClockArt == null) && (changeHalfDay == '' || changeHalfDay == null) && reservationArt == 1) {
            return 19;
        }
            
        if ((changeClockArt == '' || changeClockArt == null) && (changeCalArt == '' || changeCalArt == null) && (setPreClockArt == '' || setPreClockArt == null) && (changeHalfDay == '' || changeHalfDay == null) && reservationArt == 2) {
            return 20;
        }
            
    }
}

enum CanEngravingUsed {
    // 0 利用できる
    AVAILABLE = 0,

    // 1 打刻オプション未購入
    NOT_PURCHASED_STAMPING_OPTION = 1,

    // 2 打刻機能利用不可
    ENGTAVING_FUNCTION_CANNOT_USED = 2,

    // 3 打刻カード未登録
    UNREGISTERED_STAMP_CARD = 3

}

enum ButtonType {
    // 出勤系
    GOING_TO_WORK = 1,
    // 退勤系
    WORKING_OUT = 2,
    // "外出系"
    GO_OUT = 3,
    // 戻り系
    RETURN = 4
}

interface ITime {
    date: string;
    time: string;
}

interface ISetting {
    buttons: Array<model.ButtonSettingsDto>;
    stampPageComment: model.IStampPageCommentDto;
    lstDisplayItemId: Array<number>;
    usrAtrValue: number;
    displaySettingsStampScreen: model.IDisplaySettingsStampScreenDto;

}

interface ISettingsStampCommon {
    //応援利用
    supportUse: boolean; 
    //臨時利用
    temporaryUse: boolean;
    //作業利用
    workUse: boolean;
}