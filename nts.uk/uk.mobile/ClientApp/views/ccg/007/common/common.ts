import { characteristics } from '@app/utils/storage';
import { Vue, _ } from '@app/provider';

export class CCG007Login extends Vue {

    protected login(servicePath: string, submitData: LoginParam, resetForm: Function, saveInfo: boolean) {
        let self = this;

        self.$validate();
        if (!self.$valid) {
            return;
        }
        
        self.$mask('show');
        self.$http.post(servicePath, submitData).then((res: { data: CheckChangePass }) => {
            if (res.data.showContract) {
                this.authenticateContract(self);
            } else {
                if (!_.isEmpty(res.data.successMsg)) {
                    self.$modal.info({ messageId: res.data.successMsg }).then(() => {
                        this.processAfterLogin(res, self, submitData, resetForm, saveInfo);
                    });
                } else {
                    this.processAfterLogin(res, self, submitData, resetForm, saveInfo);
                }
            }
        }).catch((res: any) => {
            // Return Dialog Error
            self.$mask('hide');
            if (!_.isEqual(res.message, 'can not found message id')) {
                if (_.isEmpty(res.messageId)) {
                    self.$modal.error(res.message);
                } else {
                    self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
                }
            } else {
                self.$modal.error(res.messageId);
            }
        });
    }

    protected processAfterLogin(res: any, self: any, submitData: LoginParam, resetForm: Function, saveInfo: boolean) {
        self.$mask('hide');
        characteristics.remove('companyCode')
                    .then(() => characteristics.save('companyCode', submitData.companyCode))
                    .then(() => characteristics.remove('employeeCode'))
                    .then(() => {
                        if (saveInfo) {
                            characteristics.save('employeeCode', submitData.employeeCode);
                        }
                    }).then(() => {
                        if (!_.isEmpty(res.data.msgErrorId) && res.data.msgErrorId === 'Msg_1517' && !submitData.loginDirect) {
                            // 確認メッセージ（Msg_1517）を表示する{0}【残り何日】
                            self.$modal.confirm({ messageId: res.data.msgErrorId, messageParams: [res.data.spanDays.toString()]}).then((code) => {
                                if (code === 'yes') {
                                    self.$goto({ name: 'changepass', params: _.merge({}, 
                                        submitData, 
                                        { oldPassword: submitData.password, mesId: res.data.msgErrorId, saveInfo, changePassReason: 'Msg_1523', spanDays: res.data.spanDays }) 
                                    });
                                } else {
                                    submitData.loginDirect = true;
                                    this.login(CCG007Login.SUBMIT_LOGIN, submitData, resetForm, saveInfo);
                                }
                            });
                        } else {
                            // check MsgError
                            if ((!_.isEmpty(res.data.msgErrorId) && res.data.msgErrorId !== 'Msg_1517') || res.data.showChangePass) {
                                if (res.data.showChangePass) {
                                    self.$goto({ name: 'changepass', params: _.merge({}, 
                                                submitData, 
                                                { oldPassword: submitData.password, mesId: res.data.msgErrorId, saveInfo, 
                                                    changePassReason: res.data.changePassReason }) 
                                            });
                                } else {
                                    resetForm();
                                    /** TODO: wait for dialog error method */
                                    self.$modal.error({ messageId: res.data.msgErrorId });
                                }
                            } else {
                                this.toHomePage(self);
                            }
                        }
                    });
    }

    protected toHomePage(self) {
        self.$mask('hide');
        self.$goto({ name: 'HomeComponent', params: { screen: 'login' } });
    }

    protected authenticateContract(self) {
        self.$goto({ name: 'contractAuthentication' });
    }
    
    protected static readonly SUBMIT_LOGIN: string =  'ctx/sys/gateway/login/submit/mobile';
}

interface CheckChangePass {
    showChangePass: boolean;
    msgErrorId: string;
    showContract: boolean;
    successMsg: string;
    spanDays: number;
    changePassReason: string;
}

interface LoginParam {
    companyCode: string;
    employeeCode: string;
    password: string;
    contractCode: string;
    contractPassword: string;
    loginDirect: boolean;
}