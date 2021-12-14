import { Vue } from '@app/provider';
import { component } from '@app/core/component';

@component({
    name: 'ksus01b',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class KSUS01BComponent extends Vue {
    public title: string = 'ksus01b';
}