import { Vue } from '@app/provider';
import { component, Watch, Prop } from '@app/core/component';
import { characteristics } from "@app/utils/storage";
import { _ } from "@app/provider";

@component({
    route: '/ccg/007/f',
    style: require('./style.scss'),
    resource: require('./resources.json'),
    template: require('./index.html'),
    validations: {
        model: {
            comp: {
                required: true
            },
            employeeCode: {
                required: true
            }
        }
    }, 
    name: 'mailSent'
})
export class MailSentComponent extends Vue {
    
    @Prop({ default: () => ({}) })
    params!: any;
    
    contractCode: string = '';
    compapyCode: string = '';
    employeeCode: string = '';
    
    created() {
        this.contractCode = this.params.contractCode;
        this.compapyCode = this.params.companyCode;
        this.employeeCode = this.params.employeeCode;
    }

    toLogin() {
        this.$goto({ name: 'login', params: { companyCode: this.compapyCode, 
                                                        employeeCode: this.employeeCode,
                                                        contractCode: this.contractCode
                                                } });
    }
}