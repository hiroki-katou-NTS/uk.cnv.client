import { moment, Vue, DirectiveBinding } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { StepwizardComponent } from '@app/components';
import { FixTableComponent } from '@app/components/fix-table';
import { KafS08DComponent } from '../../../kaf/s08/d';
import { KafS00ShrComponent, AppType } from 'views/kaf/s00/shr';
import { vmOf } from 'vue/types/umd';
import * as _ from 'lodash';


// import abc from './mock_data.json';

@component({
    name: 'kafs08a2',
    route: '/kaf/s08/a2',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    components: {
        'step-wizard': StepwizardComponent,
        'fix-table': FixTableComponent,
    },
    directives: {
        'date': {
            bind(el: HTMLElement, binding: DirectiveBinding) {
                const mm = moment(binding.value, 'YYYY/MM/DD');
                el.innerHTML = mm.format('MM/DD(ddd)');
                el.className = mm.clone().locale('en').format('dddd').toLocaleLowerCase();
            }
        }
    },
    constraints: [],
})
export class KafS08A2Component extends KafS00ShrComponent {
    //lúc không truyển props thì nó nhảy vào default
    @Prop({ default: () => ({}) })

    //A2 nhận về là props là array table
    @Prop({ default: () => [] }) public readonly table!: [];

    //A2 nhận về props params là một Object ITimes
    @Prop({ default: ' ' }) public readonly departureTime!: string;

    @Prop({ default: ' ' }) public readonly returnTime!: string;

    //A2 nhận về props comment là một Object comment
    @Prop({ default: {} }) public readonly comment!: Object;

    //A2 nhan ve props businessTripInfoOutput
    @Prop({default : { }}) public readonly businessTripInfoOutput !: Object;

    //public readonly params!: any;
    public name: string = 'hello my dialog';
    //public date: Date = new Date(2020,2,14);
    public mtable = require('./mock_data.json');
    public mode: boolean = true;
    public user: any;
    public data: any;

    public created() {
        const vm = this;
        vm.fetchStart();
        //vm.register();
    }


    //hàm khởi động màn hình
    public fetchStart() {
        const vm = this;
        vm.mode = false;
        vm.$mask('show');
        vm.$auth.user.then((usr: any) => {
            vm.user = usr;
        }).then(() => {
            return vm.loadCommonSetting(AppType.BUSINESS_TRIP_APPLICATION);
        }).then((loadData: any) => {
            if (loadData) {
                return vm.$http.post('at', API.startKAFS08, {
                    mode: vm.mode,
                    companyId: vm.user.companyId,
                    employeeId: vm.user.employeeId,
                    listDates: ['2020/03/12', '2020/03/13', '2020/03/14', '2020/03/15', '2020/03/16'],
                    businessTripInfoOutput: null,
                    businessTrip: { departureTime: vm.departureTime, returnTime: vm.returnTime, tripInfos: vm.table }
                }).then((res: any) => {
                    if (!res) {
                        return;
                    }
                    vm.data = res.data;
                    vm.$mask('hide');
                });
            }
        }).catch((err: any) => {
            vm.$mask('hide');
        });
    }

    // public showDialog() {
    //     const vm = this;
    //     vm.params.returnTime
    //     this.$modal(KafS08DComponent,{name},{title: '戻る'})
    //     .then((data) => {
    //         console.log(data);
    //     });
    // }

    //hàm xử lý gọi dialog
    public selectRowDate(data) {
        const vm = this;
        vm.$modal(KafS08DComponent, data);
    }

    //nhảy đến step three với các điều kiện
    public nextToStepThree() {
        const vm = this;
        vm.registerData();
        //vm.checkBeforeRegister();
        vm.toggleErrorAlert();
        this.$emit('nextToStepThree');
    }
    //hàm xử lý ẩn/hiện alert error
    public toggleErrorAlert() {
        let x = document.getElementById('error');
        if (x.style.display === 'none') {
            x.style.display = 'block';
        } else {
            x.style.display = 'none';
        }
    }

    //quay trở lại step one
    public prevStepOne() {
        this.$emit('prevStepOne', {});
    }

    //hàm check trước khi register
    public checkBeforeRegister() {
        const vm = this;
        // check before registering application
        this.$http.post('at', API.checkBeforeApply, {
            businessTripInfoOutputDto: vm.data.businessTripInfoOutput,
            businessTripDto: {
                departureTime: vm.departureTime,
                returnTime: vm.returnTime,
                tripInfos: vm.table
            }
        }).then((res: any) => {
            //nếu không có lỗi gọi hàm register
            vm.registerData;
        }).catch((res: any) => {
            vm.handleErrorMessage(res).then((msgId: any) => {
                if (res.messageId == 'Msg_426') {
                }
            });
        });
    }

    //handle mess dialog
    public handleErrorMessage(res: any) {
        const vm = this;
        vm.$mask('hide');
        if (res.messageId) {
            return vm.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {

            if (_.isArray(res.errors)) {
                return vm.$modal.error({ messageId: res.errors[0].messageId, messageParams: res.parameterIds });
            } else {
                return vm.$modal.error({ messageId: res.errors.messageId, messageParams: res.parameterIds });
            }
        }
    }

    //hàm register when click A50_2 button
    public registerData() {
        const vm = this;
        this.$mask('show');
        let paramsBusinessTrip = {
            departureTime: vm.departureTime,
            returnTime: vm.returnTime,
            tripInfos: vm.table,
        };
        this.$http.post('at', API.register, {
            businessTrip: paramsBusinessTrip,
            businessTripInfoOutput: vm.businessTripInfoOutput,
            //application: this.application
        }).then((res: any) => {
            this.$mask('hide');
            // KAFS00_D_申請登録後画面に移動する
            this.$modal('kafs00d', { mode: this.mode ? ScreenMode.NEW : ScreenMode.DETAIL, appID: res.appID });
        }).catch((res: any) => {
            vm.handleErrorMessage(res);
        });
    }
}

export enum ScreenMode {
    // 新規モード
    NEW = 0,
    // 詳細モード
    DETAIL = 1
}

const API = {
    startKAFS08: 'at/request/application/businesstrip/mobile/startMobile',
    checkBeforeApply: 'at/request/application/businesstrip/mobile/checkBeforeRegister',
    register: 'at/request/application/businesstrip/mobile/register'
};

