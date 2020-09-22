import { Vue } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { KAFS08A1Component } from '../../s08/a1';
import { KafS08A2Component } from '../../s08/a2';
import { KafS08CComponent } from '../../s08/c';
import { StepwizardComponent } from '@app/components';
import { vmOf } from 'vue/types/umd';

@component({
    name: 'kafs08a',
    route: '/kaf/s08/a',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        derpartureTime: {
            required: true,
        },
        returnTime: {
            required: true
        }
    },
    components: {
        'kafs08a1': KAFS08A1Component,
        'kafs08a2': KafS08A2Component,
        'kafs08c': KafS08CComponent,
        'step-wizard': StepwizardComponent,
    },
    constraints: []
})

export class KafS08AComponent extends Vue {
    public step: string = 'KAFS08_10';

    @Prop({default : () => {}}) public readonly params!: any;

    //public paramsFromA1: any | null = null;
    public achievementDetails: [] = [];
    public comment: Object = {};
    public derpartureTime: number;
    public returnTime: number;
    public businessTripInfoOutput: Object = {};
    public application: Object = {};
    public kafs00BParams: Object = {};
    public appID: string = ' ';
    public listDate: any[] = [];
    public appReason: string = '';
    public startDate;


    public created() {
        const vm = this;
        console.log(vm.params);
    }

    //thực hiện emit từ component con A1
    public ProcessNextToStepTwo(listDate,
                                application,
                                businessTripInfoOutput,
                                departureTime, returnTime,
                                achievementDetails,
                                comment,
                                appReason,
    ) {
        const vm = this;
        //Object date có được ở màn hình A1
        vm.derpartureTime = departureTime;
        vm.returnTime = returnTime;
        //table có được ở màn hình A1 chuyển lên.
        vm.achievementDetails = achievementDetails;
        //lấy giá trị comment set ở A1
        vm.comment = comment;
        //nhan businessTripInfoOutput tu man hinh start
        vm.businessTripInfoOutput = businessTripInfoOutput;
        //nhan application tu man hinh start
        vm.application = application;
        //nhan listDate tu man hinh start
        vm.listDate = listDate;
        //nhan ve appReason tu A1
        vm.appReason = appReason;
         //nhảy sang step A2 
        vm.step = 'KAFS08_11';
    }

    //thực hiện emit từ component con A2 đến C
    public ProcessNextToStepThree(appID) {
        const vm = this;
        vm.appID = appID;
        vm.step = 'KAFS08_12';
    }

    //thực hiện emit từ component con A2 quay trở lại A1
    public ProcessPrevStepOne(departureTime, returnTime, appReason) {
        const vm = this;
        vm.derpartureTime = departureTime;
        vm.returnTime = returnTime;
        vm.appReason = appReason;
        vm.step = 'KAFS08_10';
    }
}