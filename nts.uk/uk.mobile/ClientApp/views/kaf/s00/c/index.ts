import { Vue, _ } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';

@component({
    name: 'kafs00c',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        opAppReason: {
            constraint: 'AppReason'
        } 
    },
    constraints: [
        'nts.uk.ctx.at.request.dom.application.AppReason'  
    ]
})
export class KafS00CComponent extends Vue {
    @Prop({ default: () => ({}) })
    public params: KAFS00CParams;
    public dropdownList: Array<any> = [];
    public opAppStandardReasonCD: any = '';
    public opAppReason: string = '';

    public created() {
        const self = this;
        self.initFromParams();    
    }

    @Watch('params')
    public paramsWatcher() {
        const self = this;
        self.initFromParams();
    }

    @Watch('params')
    private initFromParams() {
        const self = this;
        if (!self.params) {
            return;
        }
        if (self.params.appLimitSetting.standardReasonRequired) {
            self.dropdownList = [];
        } else {
            self.dropdownList = [{
                appStandardReasonCD: '',
                displayOrder: 0,
                defaultValue: false,
                reasonForFixedForm: self.$i18n('KAFS00_23'),   
            }];
        }
        _.forEach(self.params.reasonTypeItemLst, (value) => {
            self.dropdownList.push({
                appStandardReasonCD: value.appStandardReasonCD,
                displayOrder: value.displayOrder,
                defaultValue: value.defaultValue,
                reasonForFixedForm: value.appStandardReasonCD + ' ' + value.reasonForFixedForm,     
            });   
        });

        if (self.params.opAppStandardReasonCD) {
            self.opAppStandardReasonCD = _.find(self.dropdownList, (o: ReasonTypeItemDto) => {
                                                    return o.appStandardReasonCD == self.params.opAppStandardReasonCD;
                                                }).appStandardReasonCD;
        } else {
            let defaultReasonCD = _.find(self.dropdownList, (o: ReasonTypeItemDto) => o.defaultValue);
            if (defaultReasonCD) {
                self.opAppStandardReasonCD = defaultReasonCD.appStandardReasonCD;  
            } else {
                let headItem = _.head(self.dropdownList);
                self.opAppStandardReasonCD = headItem ? headItem.appStandardReasonCD : '';
            }
        }
        if (self.params.opAppReason) {
            self.opAppReason = self.params.opAppReason;
        }

        if (self.displayFixedReason) {
            if (self.params.appLimitSetting.standardReasonRequired) {
                self.$updateValidator('opAppStandardReasonCD', { required: true });    
            } else {
                self.$updateValidator('opAppStandardReasonCD', { required: false });
            }
            self.$updateValidator('opAppStandardReasonCD', { validate: true });
        } else {
            self.$updateValidator('opAppStandardReasonCD', { validate: false });
        }
        if (self.displayAppReason) {
            if (self.params.appLimitSetting.requiredAppReason) {
                self.$updateValidator('opAppReason', { required: true });
            } else {
                self.$updateValidator('opAppReason', { required: false });
            }
            self.$updateValidator('opAppReason', { validate: true });
        } else {
            self.$updateValidator('opAppReason', { validate: false });
        }
    }

    get displayFixedReason() {
        const self = this;

        return self.params.displayFixedReason == 0 ? false : true;
    }

    get displayAppReason() {
        const self = this;

        return self.params.displayAppReason == 0 ? false : true;
    }

    get dispReason() {
        const self = this;

        return self.displayFixedReason || self.displayAppReason;
    }

    get standardReasonRequired() {
        const self = this;

        return self.params.appLimitSetting.standardReasonRequired;
    }

    get requiredAppReason() {
        const self = this;

        return self.params.appLimitSetting.requiredAppReason;
    }

    @Watch('opAppStandardReasonCD')
    public opAppStandardReasonCDWatcher(value) {
        const self = this;
        self.$emit('kaf000CChangeReasonCD', value);
    }

    @Watch('opAppReason')
    public opAppReasonWatcher(value) {
        const self = this;
        self.$emit('kaf000CChangeAppReason', value);
    }

    @Watch('$errors')
    public errorWatcher() {
        const self = this;
        self.$emit('kafs00CValid', self.$valid);
    }

}

interface ReasonTypeItemDto {
    appStandardReasonCD: any;
    displayOrder: number;
    defaultValue: boolean;
    reasonForFixedForm: string;     
}

// KAFS00_C_????????????
export interface KAFS00CParams {
    // ?????????????????????
    displayFixedReason: number;
    // ?????????????????????
    displayAppReason: number;
    // ??????????????????
    reasonTypeItemLst: Array<ReasonTypeItemDto>;
    // ??????????????????
    appLimitSetting: any;
    // ????????????????????????
    opAppStandardReasonCD?: number | string;
    // ????????????????????????
    opAppReason?: string;
}