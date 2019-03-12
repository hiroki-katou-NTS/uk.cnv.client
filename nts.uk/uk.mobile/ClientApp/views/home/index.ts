import { Vue } from '@app/provider';
import { component } from '@app/core/component';

import { focus } from '@app/directives';
import { SampleComponent } from '@app/components';

import { LanguageBar } from '@app/plugins';

@component({
    route: '/',
    style: require('./style.scss'),
    template: require('./index.html'),
    resource: require('./resources.json'),
    directives: {
        'focus': focus
    },
    components: {
        'vuong': SampleComponent,
        'language-bar': LanguageBar
    },
    validations: {
        title: {
            required: true,
            minLength: 10
        },
        resource: {
            required: true,
            alpha: true
        },
        model: {
            name: {
                required: true
            },
            addrs: {
                required: true,
                self_validator: {
                    test: /^\d{3,5}$/,
                    message: 'xxxx'
                }
            },
            address: {
                province: {
                    required: true
                },
                district: {
                    required: true
                }
            },
            office: {
                head: {
                    required: true
                }
            }
        }
    }
})
export class HomeComponent extends Vue {
    title: string = 'home';

    resource: string = '';

    model = {
        name: '',
        addrs: 'world'
    };


    disabled: boolean = false;

    constructor() {
        super();
        let self = this;

        window['v'] = Vue;
        window['h'] = self;
    }

    alertNow() {
        //console.log(this.validations);
        //alert(this.$i18n(this.title));
        //this.$router.push({ path: '/about/me' });
        
        this.disabled = true;
        /*
        this.$http
            .get('/about/me')
            .then((value: any) => {
                debugger;
            });

        this.$modal('vuong')
            .onClose((data: any) => {
                console.log(data);
            });*/
    }
}