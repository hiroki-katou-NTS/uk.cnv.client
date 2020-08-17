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
    getOmission: basePath + 'get-omission'
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
            {
                buttonValueType: -1,
                buttonPositionNo: 1,
                buttonDisSet: {
                    buttonNameSet: {
                        textColor: '',
                        buttonName: ''
                    },
                    backGroundColor: ''
                },
                usrArt: 1
            },
            {
                buttonValueType: -1,
                buttonPositionNo: 2,
                buttonDisSet: {
                    buttonNameSet: {
                        textColor: '',
                        buttonName: ''
                    },
                    backGroundColor: ''
                },
                usrArt: 1
            },
            {
                buttonValueType: -1,
                buttonPositionNo: 3,
                buttonDisSet: {
                    buttonNameSet: {
                        textColor: '',
                        buttonName: ''
                    },
                    backGroundColor: ''
                },
                usrArt: 1
            },
            {
                buttonValueType: -1,
                buttonPositionNo: 4,
                buttonDisSet: {
                    buttonNameSet: {
                        textColor: '',
                        buttonName: ''
                    },
                    backGroundColor: ''
                },
                usrArt: 1
            },
            {
                buttonValueType: -1,
                buttonPositionNo: 5,
                buttonDisSet: {
                    buttonNameSet: {
                        textColor: '',
                        buttonName: ''
                    },
                    backGroundColor: ''
                },
                usrArt: 1
            },
            {
                buttonValueType: -1,
                buttonPositionNo: 6,
                buttonDisSet: {
                    buttonNameSet: {
                        textColor: '',
                        buttonName: ''
                    },
                    backGroundColor: ''
                },
                usrArt: 1
            }
        ]
    };

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
            }

        }).catch((res: any) => {
            vm.showError(res);
        });
    }

    private InitCountTime() {

        let vm = this;

        setInterval(() => {

            vm.getStampToSuppress();
        }, vm.setting.displaySettingsStampScreen.serverCorrectionInterval * 1000);
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
                        backGroundColor: ''
                    },
                    usrArt: 1
                };


            if (button) {
                buttonSetting = button;
            }

            vm.setBtnColor(buttonSetting, stampToSuppress);

            resultList.push(buttonSetting);
        }

        return resultList;
    }

    public stampClick(button: model.ButtonSettingsDto) {
        let vm = this,
            command: model.IRegisterSmartPhoneStampCommand = {
                stampDatetime: moment(vm.$dt.now).format('YYYY/MM/DD HH:mm:ss'),
                stampButton: { pageNo: 1, buttonPositionNo: button.buttonPositionNo },
                geoCoordinate: { latitude: null, longitude: null },
                refActualResult: { cardNumberSupport: null, overtimeDeclaration: null, workLocationCD: null, workTimeCode: null }
            };
        navigator.geolocation.getCurrentPosition((position) => {

            if (position) {
                let latitude = position.coords.latitude,
                    longitude = position.coords.longitude;

                command.geoCoordinate = { latitude, longitude };
                vm.$mask('show');

                vm.$http.post('at', servicePath.registerStamp, command)
                    .then((result: any) => {
                        vm.$mask('hide');
                        vm.getStampToSuppress();

                        switch (button.buttonValueType) {
                            case 1:
                            case 3:
                            case 4:
                                vm.openDialogB(result, command.stampButton);
                                break;

                            case 2: {
                                if (vm.setting.usrAtrValue === 1) {
                                    vm.openDialogC(result, command.stampButton);
                                } else {
                                    vm.openDialogB(result, command.stampButton);
                                }
                                break;
                            }
                            default:
                                break;
                        }

                    }).catch((res: any) => {
                        vm.showError(res);
                    });
            }
        });
    }

    private openDialogB(date: Date, stampButton: model.IStampButtonCommand) {

        let vm = this;
        vm.$auth.user.then((userInfo) => {
            vm.$modal('screenB', {
                stampDate: date,
                resultDisplayTime: vm.setting.displaySettingsStampScreen.resultDisplayTime,
                employeeId: userInfo.employeeId,
                employeeCode: userInfo.employeeCode
            }).then(() => {

                vm.$http.post('at', servicePath.getOmission, stampButton).then((result: any) => {
                    let data: model.IGetOmissionContentDto = result.data;
                    if (data && data.errorInfo && data.errorInfo.length > 0) {
                        vm.openDialogT(data);
                    }
                });
            });
        });
    }

    private openDialogC(date: Date, stampButton: model.IStampButtonCommand) {
        let vm = this;
        vm.$auth.user.then((userInfo) => {
            vm.$modal('screenC', {
                attendanceItemIds: vm.setting.lstDisplayItemId
            }).then(() => {

                vm.$http.post('at', servicePath.getOmission, stampButton).then((result: any) => {
                    let data: model.IGetOmissionContentDto = result.data;

                    if (data && data.errorInfo && data.errorInfo.length > 0) {
                        vm.openDialogT(data);
                    }
                });
            });
        });
    }

    public openDialogS() {
        let vm = this;
        vm.$modal('screenS').then(() => {

        });
    }

    public openDialogT(data) {
        let vm = this;
        vm.$modal('screenT', data).then(() => {

        });
    }

    private setBtnColor(buttonSetting: model.ButtonSettingsDto, stampToSuppress: model.IStampToSuppress) {

        const DEFAULT_GRAY = '#E8E9EB';

        if (buttonSetting.buttonValueType === ButtonType.GOING_TO_WORK) {
            // 出勤
            buttonSetting.buttonDisSet.backGroundColor = !stampToSuppress.goingToWork ? buttonSetting.buttonDisSet.backGroundColor : DEFAULT_GRAY;
        }

        if (buttonSetting.buttonValueType === ButtonType.WORKING_OUT) {
            // 退勤
            buttonSetting.buttonDisSet.backGroundColor = !stampToSuppress.departure ? buttonSetting.buttonDisSet.backGroundColor : DEFAULT_GRAY;
        }

        if (buttonSetting.buttonValueType === ButtonType.GO_OUT) {
            // 外出
            buttonSetting.buttonDisSet.backGroundColor = !stampToSuppress.goOut ? buttonSetting.buttonDisSet.backGroundColor : DEFAULT_GRAY;
        }

        if (buttonSetting.buttonValueType === ButtonType.RETURN) {
            // 戻り
            buttonSetting.buttonDisSet.backGroundColor = !stampToSuppress.turnBack ? buttonSetting.buttonDisSet.backGroundColor : DEFAULT_GRAY;
        }
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