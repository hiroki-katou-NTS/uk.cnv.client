import { Vue } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { KafS00SubP1Component, ExcessTimeStatus } from 'views/kaf/s00/sub/p1/index';
import { AppTimeType } from 'views/kaf/s12/shr/index';

@component({
    name: 'kafs20-amount-input',
    template: require('./index.vue'),
    validations: {
        item: {
            amount: {
                constraint: 'AnyItemAmount'
            }
        }
    },
    constraints: [
        'nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemAmount'
    ]
})
export class KafS20AmountInputComponent extends Vue {
    @Prop({ default: {} })
    public readonly item: any;

    public created() {
        const self = this;
    }
}
