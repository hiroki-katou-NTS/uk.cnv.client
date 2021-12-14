import { Vue, _, moment } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { vmOf } from 'vue/types/umd';
import { AppType } from 'views/kaf/s00';

@component({
    name: 'kafs00b',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        date: {
            selectCheck: {
                test(value: any) {
                    const vm = this;
                    if (!_.get(vm.params, 'newModeContent.initSelectMultiDay')) {
                        if (vm.params.isChangeDateError) {
                            return false;
                        }
                    }

                    return true;
                },
                messageId: 'MsgB_30'
            },
            required: true
        },
        dateRange: {
            selectCheck: {
                test(value: any) {
                    const vm = this;
                    if (_.get(vm.params, 'newModeContent.initSelectMultiDay')) {
                        if (vm.params.isChangeDateError) {
                            return false;
                        }
                    }

                    return true;
                },
                messageId: 'MsgB_30'
            },
            required: true,
            dateRange: true
        },
        prePostAtr: {
            selectCheck: {
                test(value: number) {
                    const vm = this;
                    if (value == null || value < 0 || value > 1) {
                        document.getElementById('prePostSelect').className += ' invalid';

                        return false;
                    }
                    let prePostSelectElement = document.getElementById('prePostSelect');
                    if (!_.isNull(prePostSelectElement)) {
                        prePostSelectElement.classList.remove('invalid');
                    }

                    return true;
                },
                messageId: 'MsgB_30'
            }
        }
    },
    constraints: []
})
export class KafS00BComponent extends Vue {
    @Prop({ default: () => ({}) })
    public params: KAFS00BParams;
    public prePostResource: Array<Object> = [];
    public dateSwitchResource: Array<Object> = [];
    public prePostAtr: number = null;
    public date: Date = null;
    public dateRange: any = {
        start: null,
        end: null
    };

    public created() {
        const self = this;
        self.prePostResource = [{
            code: 0,
            text: 'KAFS00_10'
        }, {
            code: 1,
            text: 'KAFS00_11'
        }];
        self.dateSwitchResource = [{
            code: false,
            text: 'KAFS00_12'
        }, {
            code: true,
            text: 'KAFS00_13'
        }];
        self.initFromParams();
    }

    @Watch('params')
    public paramsWatcher() {
        const self = this;
        self.initFromParams();
    }

    private initFromParams() {
        const self = this;
        if (!self.params) {
            return;
        }
        if (self.params.newModeContent) {
            if (self.params.newModeContent.appTypeSetting[0].displayInitialSegment != 2) {
                self.prePostAtr = self.params.newModeContent.appTypeSetting[0].displayInitialSegment;
            } else {
                self.prePostAtr = null;
            }
            if (self.params.newModeContent.initSelectMultiDay) {
                if (self.params.newModeContent.dateRange) {
                    self.dateRange = self.params.newModeContent.dateRange;
                } else {
                    self.dateRange = {
                        start: null,
                        end: null
                    };
                }
                self.$updateValidator('dateRange', { validate: true });
                self.$updateValidator('date', { validate: false });
            } else {
                if (self.params.newModeContent.appDate) {
                    self.date = self.params.newModeContent.appDate;
                } else {
                    self.date = null;
                }
                self.$updateValidator('dateRange', { validate: false });
                self.$updateValidator('date', { validate: true });
            }
            if (self.displayPrePost) {
                self.$updateValidator('prePostAtr', { validate: true });
            } else {
                self.$updateValidator('prePostAtr', { validate: false });
            }
        }
        if (self.params.detailModeContent) {
            self.prePostAtr = self.params.detailModeContent.prePostAtr;
            self.$updateValidator('dateRange', { validate: false });
            self.$updateValidator('date', { validate: false });
            self.$updateValidator('prePostAtr', { validate: false });
        }
    }

    get displayPrePost() {
        const self = this;

        return self.params.appDisplaySetting.prePostDisplayAtr == 0 ? false : true;
    }

    get enablePrePost() {
        const self = this;

        return self.params.newModeContent.appTypeSetting[0].canClassificationChange;
    }

    get displayMultiDaySwitch() {
        const self = this;

        return self.params.newModeContent.useMultiDaySwitch;
    }

    get ScreenMode() {
        return ScreenMode;
    }

    get prePostAtrName() {
        const self = this;

        return _.find(self.prePostResource, (o: any) => o.code == self.params.detailModeContent.prePostAtr).text;
    }

    @Watch('params.newModeContent.initSelectMultiDay')
    public initSelectMultiDayWatcher(value: any) {
        const self = this;
        new Promise((resolve) => {
            self.$validate('clear');
            setTimeout(() => {
                resolve(true);
            }, 300);
        }).then(() => {
            if (self.params.mode == ScreenMode.NEW) {
                if (value) {
                    self.$updateValidator('dateRange', { validate: true });
                    self.$updateValidator('date', { validate: false });
                    self.$validate('dateRange');
                    if (self.$valid) {
                        self.$emit('kaf000BChangeDate',
                            {
                                startDate: self.dateRange.start,
                                endDate: self.dateRange.end
                            });
                    }

                } else {
                    self.$updateValidator('dateRange', { validate: false });
                    self.$updateValidator('date', { validate: true });
                    self.$validate('date');
                    if (self.$valid) {
                        self.$emit('kaf000BChangeDate',
                            {
                                startDate: self.date,
                                endDate: self.date
                            });
                    }
                }
            }
        });
    }

    @Watch('date')
    public dateWatcher(value) {
        const self = this;
        if (!value) {
            self.$emit('kaf000BChangeDate',
            {
                startDate: value,
                endDate: value
            }); 

            return;
        }
        if (!self.params.appDispInfoStartupOutput) {
            self.$emit('kaf000BChangeDate',
            {
                startDate: value,
                endDate: value
            }); 

            return;
        }
        self.params.isChangeDateError = null;
        let appDate = moment(value).format('YYYY/MM/DD'), 
            startDate = moment(value).format('YYYY/MM/DD'),
            endDate = moment(value).format('YYYY/MM/DD'),
            appDispInfoStartupOutput = self.params.appDispInfoStartupOutput,
            opOvertimeAppAtr = null;
        self.$mask('show');
        self.$http.post('at', API.changeAppDate, { appDate, startDate, endDate, appDispInfoStartupOutput, opOvertimeAppAtr }).then((data: any) => {
            self.$nextTick(() => self.$mask('hide'));
            let appDispInfoWithDateOutput = data.data;
            self.params.isChangeDateError = !self.validateChangeDate(appDispInfoWithDateOutput);
            self.$validate('date');
            if (self.$valid) {
                appDispInfoStartupOutput.appDispInfoWithDateOutput = appDispInfoWithDateOutput;
                self.$emit('kaf000BChangeDate', { startDate, endDate, appDispInfoStartupOutput });   
            }
        }).catch((res: any) => {
            if (res.messageId == 'Msg_426') {
                self.$modal.error('Msg_426').then(() => {
                    self.$goto('ccg008a');
                });    
            } else {
                self.$modal.error(res.message).then(() => {
                    self.$goto('ccg008a');
                }); 
            }
        });
    }

    @Watch('dateRange')
    public dateRangeWatcher(value) {
        const self = this;
        if (!self.params.appDispInfoStartupOutput) {
            self.$emit('kaf000BChangeDate',
            {
                startDate: value.start,
                endDate: value.end
            });  

            return;
        }
        self.params.isChangeDateError = null;
        let appDate = moment(value.start).format('YYYY/MM/DD'), 
            startDate = moment(value.start).format('YYYY/MM/DD'),
            endDate = moment(value.end).format('YYYY/MM/DD'),
            appDispInfoStartupOutput = self.params.appDispInfoStartupOutput,
            opOvertimeAppAtr = null;
        new Promise((resolve) => {
            self.$validate('clear');
            setTimeout(() => {
                resolve(true);
            }, 200);
        })
        .then(() => self.$validate('dateRange'))
        .then(() => self.$valid)
        .then((valid: boolean) => {
            if (valid) {
                self.$mask('show');
                self.$http.post('at', API.changeAppDate, { appDate, startDate, endDate, appDispInfoStartupOutput, opOvertimeAppAtr }).then((data: any) => {
                    self.$nextTick(() => self.$mask('hide'));
                    let appDispInfoWithDateOutput = data.data;
                    self.params.isChangeDateError = !self.validateChangeDate(appDispInfoWithDateOutput);
                    self.$validate('dateRange');
                    if (self.$valid) {
                        appDispInfoStartupOutput.appDispInfoWithDateOutput = appDispInfoWithDateOutput;
                        self.$emit('kaf000BChangeDate', { startDate, endDate, appDispInfoStartupOutput });   
                    }
                }).catch((res: any) => {
                    if (res.messageId == 'Msg_426') {
                        self.$modal.error('Msg_426').then(() => {
                            self.$goto('ccg008a');
                        });    
                    } else {
                        self.$modal.error(res.message).then(() => {
                            self.$goto('ccg008a');
                        }); 
                    }
                });
            }
        });
    }

    @Watch('prePostAtr')
    public prePostAtrWatcher() {
        const self = this;
        if (self.displayPrePost) {
            self.$emit('kaf000BChangePrePost', self.prePostAtr);
        }
    }

    private validateChangeDate(appDispInfoWithDateOutput: any) {
        const self = this;
        let useDivision = appDispInfoWithDateOutput.approvalFunctionSet.appUseSetLst[0].useDivision,
            opErrorFlag = appDispInfoWithDateOutput.opErrorFlag,
            msgID = '';
        if (useDivision == 0) {
            self.$modal.error('Msg_323');

            return true;
        }
        
        if (_.isNull(opErrorFlag)) {
            return true;    
        }
        switch (opErrorFlag) {
            case 1:
                msgID = 'Msg_324';
                break;
            case 2: 
                msgID = 'Msg_238';
                break;
            case 3:
                msgID = 'Msg_237';
                break;
            default: 
                break;
        }  
        if (_.isEmpty(msgID)) { 
            return true;
        }
        self.$modal.error({ messageId: msgID });
        
        return true;
    }

    @Watch('$errors')
    public errorWatcher() {
        const self = this;
        self.$emit('kafs00BValid', self.$valid);
    }
}

// 画面モード
export enum ScreenMode {
    // 新規モード
    NEW = 0,
    // 詳細モード
    DETAIL = 1
}

// 新規モード内容
interface NewModeContent {
    // 申請種類別設定
    appTypeSetting: any;
    // 複数日切り替えを利用する
    useMultiDaySwitch: boolean;
    // 複数日を初期選択する
    initSelectMultiDay: boolean;
    // 申請日
    appDate?: Date;
    // 申請日期間
    dateRange?: any;
}

// 詳細モード内容
interface DetailModeContent {
    // 事前事後区分
    prePostAtr: number;
    // 申請者名
    employeeName: string;
    // 申請開始日
    startDate: string;
    // 申請終了日
    endDate: string;
}

// KAFS00_B_起動情報
export interface KAFS00BParams {
    // 画面モード
    mode: ScreenMode;
    // 申請表示設定
    appDisplaySetting: any;
    // 新規モード内容
    newModeContent?: NewModeContent;
    // 詳細モード内容
    detailModeContent?: DetailModeContent;

    appDispInfoStartupOutput?: any;
    isChangeDateError?: boolean;
}

const API = {
    changeAppDate: 'at/request/application/changeAppDate'
};