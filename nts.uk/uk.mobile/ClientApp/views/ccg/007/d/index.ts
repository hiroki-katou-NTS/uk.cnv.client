import { Vue, _ } from '@app/provider';
import { SideMenu, NavMenu } from '@app/services';
import { component, Prop } from '@app/core/component';

@component({
    route: '/ccg/007/d',
    style: require('./style.scss'),
    template: require('./index.vue'),
    validations: {
        companyCode: {
            required: true
        },
        employeeCode: {
            required: true,
            anyHalf: true
        }
    },
    name: 'ccg007d'
})
export class Ccg007dComponent extends Vue {
    @Prop({ default: () => ({}) })
    public readonly params!: any;

    public companies: Array<ICompany> = [];
    public contractCode: string = '';
    public companyCode: string = '';
    public contractPass: string = '';
    public employeeCode: string = '';

    public created() {
        let self = this,
            params: any = self.params;

        Promise.resolve()
            .then(() => {
                // get contract code from param or storage
                if (params.contractCode) {
                    self.contractCode = params.contractCode;
                } else {
                    self.$auth.contract
                        .then((v: { code: string }) => {
                            self.contractCode = v && v.code || '000000000000';
                        });
                }

                // get contract password from param or storage
                if (params.contractPass) {
                    self.contractPass = params.contractPass;
                } else {
                    self.$auth.contract
                        .then((v: { password: string }) => {
                            self.contractPass = v && v.password || '';
                        });
                }
            })
            .then(() => {
                self.checkEmpCodeAndCompany();
            });
    }

    public mounted() {
        // Hide top & side menu
        NavMenu.visible = false;
        SideMenu.visible = false;
    }

    public checkEmpCodeAndCompany() {
        const vm = this;
        const { params, contractCode } = vm;

        // get list company from param or service
        if (params.companies && params.companies.length) {
            vm.companies = params.companies;
        } else if (contractCode) {
            vm.$http
                .post(`/${API.getAllCompany}/${contractCode}`)
                .then((response: { data: Array<ICompany> }) => {
                    vm.companies = response.data;
                });
        }

        // get compCode from param or storage
        if (params.companyCode) {
            vm.companyCode = params.companyCode;
        } else {
            vm.$auth.user
                .then((user: null | { companyCode: string }) => {
                    vm.companyCode = user && user.companyCode;
                });
        }

        // get emplCode from param or storage
        if (params.employeeCode) {
            vm.employeeCode = params.employeeCode;
        } else {
            vm.$auth.user
                .then((user: null | { employeeCode: string }) => {
                    vm.employeeCode = user && user.employeeCode;
                });
        }
    }


    public destroyed() {
        // Show menu
        NavMenu.visible = true;
        SideMenu.visible = true;
    }

    public sendMail() {
        let self = this,
            submitData: any = {};

        self.$validate();

        if (!self.$valid) {
            return;
        }

        submitData.companyCode = self.companyCode;
        submitData.employeeCode = self.employeeCode;
        submitData.contractCode = self.contractCode;
        submitData.contractPassword = self.contractPass;

        self.$mask('show');

        self.$http
            .post(API.sendMail, submitData)
            .then((result: { data: Array<SendMailReturn> }) => {
                self.$mask('hide');

                if (!_.isEmpty(result.data)) {
                    self.$goto
                        .password
                        .mail({
                            companyCode: self.companyCode,
                            employeeCode: self.employeeCode,
                            contractCode: self.contractCode
                        });
                }
            }).catch((res: any) => {
                // Return Dialog Error
                self.$mask('hide');

                if (!_.isEqual(res.message, 'can not found message id')) {
                    /** TODO: wait for dialog error method */
                    self.$modal.error({
                        messageId: res.messageId,
                        messageParams: res.parameterIds
                    });
                    // self.$modal.error(res.message);
                } else {
                    self.$modal.error(res.message);
                }
            });
    }

    public goBack() {
        let self = this,
            params: any = _.pick(self, ['companyCode', 'employeeCode', 'contractCode', 'contractPass', 'companies']);

        self.$goto.login(params);
    }
}

const API = {
    getAllCompany: 'ctx/sys/gateway/login/getcompany/',
    sendMail: 'ctx/sys/gateway/sendmail/mobile'
};

interface ICompany {
    companyCode: string;
    companyId: string;
    companyName: string;
}

interface CallerParameter {
    loginId: string;
    contractCode: string;
    contractPassword: string;
}

interface SendMailReturn {
    url: string;
}