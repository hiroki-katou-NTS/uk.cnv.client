import { Vue, _ } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { NavMenu, SideMenu } from '@app/services';
import { ccg007 } from "../common/common";

@component({
    route: '/ccg/007/c',
    style: require('./style.scss'),
    resource: require('./resources.json'),
    template: require('./index.html'),
    validations: {
        model: {
            currentPassword: {
                required: true
            },
            newPassword: {
                required: true
                // checkSame: {
                //     test(value){
                //         return value === this.model.newPasswordConfirm;
                //     }, message: '新しいパスワードと新しいパスワード（確認用）はマッチしてない'
                // }
            },
            newPasswordConfirm: {
                required: true
                // checkSame: {
                //     test(value){
                //         return value === this.model.newPassword;
                //     }, message: '新しいパスワードと新しいパスワード（確認用）はマッチしてない'
                // }
            }
        }
    }, 
    name: 'changepass'
})
export class ChangePassComponent extends Vue {

    @Prop({ default: () => ({}) })
    params!: any;

    mesId: string;
    userId: string;

    policy = {
        lowestDigits: 0,
        alphabetDigit: 0,
        numberOfDigits: 0,
        symbolCharacters: 0,
        historyCount: 0,
        validPeriod: 0,
        isUse: false
    }

    model = {
        currentPassword: '',
        newPassword: '',
        newPasswordConfirm: '',
        userName: ''
    }

    created() {
        let self = this;
        self.mesId = self.$i18n(self.params.mesId);
        Promise.all([this.$http.post(servicePath.getPasswordPolicy + self.params.contractCode), 
                    this.$http.post(servicePath.getUserName, {  contractCode: self.params.contractCode, 
                                                                employeeCode: self.params.employeeCode, 
                                                                companyCode: self.params.companyCode })])
                .then((values: Array<any>) => {
            let policy: PassWordPolicy = values[0].data, user: LoginInfor = values[1].data;
            self.model.userName = user.userName;
            self.policy.lowestDigits = policy.lowestDigits;
            self.policy.alphabetDigit = policy.alphabetDigit;
            self.policy.numberOfDigits = policy.numberOfDigits;
            self.policy.symbolCharacters = policy.symbolCharacters;
            self.policy.historyCount = policy.historyCount;
            self.policy.validPeriod = policy.validityPeriod;
            self.policy.isUse  = policy.isUse;
            self.userId = user.userId;
        });
        
        // Hide top & side menu
        NavMenu.visible = false;
        SideMenu.visible = false;
    }

    destroyed() {
        // Show menu
        NavMenu.visible = true;
        SideMenu.visible = true;
    }

    changePass() {
        this.$validate();
        if (!this.$valid) {
            return;                   
        }

        let self = this, 
            command: ChangePasswordCommand = new ChangePasswordCommand(self.model.currentPassword, 
                                                                        self.model.newPassword, 
                                                                        self.model.newPasswordConfirm,
                                                                        self.userId);

        self.$mask("show");
        
        //submitChangePass
        self.$http.post(servicePath.changePass, command).then((res) => {
            ccg007.login(this, {    companyCode : self.params.companyCode,
                                    employeeCode: self.params.employeeCode,
                                    password: command.newPassword,
                                    contractCode : self.params.contractCode,
                                    contractPassword : self.params.contractPassword
            }, () => {
                self.model.currentPassword = "";
                self.model.newPassword = "";
                self.model.newPasswordConfirm = "";
            }, self.params.saveInfo);
        }).catch((res) => {
            //Return Dialog Error
            self.$mask("hide");
            self.showMessageError(res);
        });
    }

    showMessageError(res: any) {
        // check error business exception
        if (!res.businessException) {
            return;
        }
        /** TODO: show error message */
        if (_.isArray(res.errors) && !_.isEmpty(res.errors)) {
            //nts.uk.ui.dialog.bundledErrors(res);
            /** TODO: show multi line message */
            this.$modal.error(res.message);
        } else {
            this.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        }
    }
}

const servicePath = {
    getPasswordPolicy: "ctx/sys/gateway/changepassword/getPasswordPolicy/",
    changePass: "ctx/sys/gateway/changepassword/submitchangepass/mobile",
    getUserName: "ctx/sys/gateway/changepassword/username/mobile"
}

class ChangePasswordCommand {
    oldPassword: string;
    newPassword: string;
    confirmNewPassword: string;
    userId: string;
    
    constructor(oldPassword: string, newPassword: string, confirmNewPassword: string, userId: string) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.userId = userId;
    }
}

interface LoginInfor {
    loginId: string;
    userName: string;
    userId: string;
    contractCode: string;
}

interface PassWordPolicy {
    notificationPasswordChange: number;
    loginCheck: boolean;
    initialPasswordChange: boolean;
    isUse: boolean;
    historyCount: number;
    lowestDigits: number;
    validityPeriod: number;
    numberOfDigits: number;
    symbolCharacters: number;
    alphabetDigit: number; 
}