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
            .then((result: { data: any }) => {
                self.$mask('hide');
                // Query \n newline of dialog
                // Send the correct email and return the message
                const message = result.data.message + '';
                if ( message.indexOf('\n') > -1 ) {
                    const content1 = message.split('\n')[0];
                    const content2 = message.split('\n')[1];
                    const element1 = document.createElement('div');
                    element1.appendChild(document.createTextNode(content1));
                    const element2 = document.createElement('div');
                    element2.appendChild(document.createTextNode(content2));
                    self.$modal.info('');
                    const element = document.getElementsByClassName('text-justify')[0];
                    element.appendChild(element1);
                    element.appendChild(element2);
                } else {
                    self.$modal.info(result.data.message);  
                }
            }).catch((res: any) => {
                self.$mask('hide');
                // Query \n newline of dialog
                // Sending wrong email returns message
                const message = res.message + '';
                if ( message.indexOf('\n' ) > -1) {
                    const content1 = message.split('\n')[0];
                    const content2 = message.split('\n')[1];
                    const element1 = document.createElement('div');
                    element1.appendChild(document.createTextNode(content1));
                    const element2 = document.createElement('div');
                    element2.appendChild(document.createTextNode(content2));
                    self.$modal.error('');
                    const element = document.getElementsByClassName('text-justify')[0];
                    element.appendChild(element1);
                    element.appendChild(element2);
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