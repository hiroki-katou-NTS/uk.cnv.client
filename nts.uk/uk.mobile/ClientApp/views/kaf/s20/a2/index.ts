import { component, Prop, Watch } from '@app/core/component';
import { KafS00AComponent, KAFS00AParams } from '../../s00/a';
import { KafS00BComponent, KAFS00BParams } from '../../s00/b';
import { KafS00CComponent, KAFS00CParams } from '../../s00/c';
import { AppType, KafS00ShrComponent } from '../../s00/shr';
import { IAppDispInfoStartupOutput, IApplication } from '../../s04/a/define';

@component({
    name: 'kafs20a2',
    route: '/kaf/s20/a2',
    style: require('./style.scss'),
    template: require('./index.vue'),
    components: {
        'KafS00AComponent': KafS00AComponent,
        'KafS00BComponent': KafS00BComponent,
        'KafS00CComponent': KafS00CComponent,
    },
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class KafS20A2Component extends KafS00ShrComponent {
    public title: string = 'KafS20A2';
    public kafS00AParams: KAFS00AParams | null = null;
    public kafS00BParams: KAFS00BParams | null = null;
    public kafS00CParams: KAFS00CParams | null = null;
    public appDispInfoStartupOutput: IAppDispInfoStartupOutput | null = null;

    public application!: IApplication;

    @Prop({ default: () => [] })
    public readonly settingNoItems!: number[];

    @Watch('appDispInfoStartupOutput', { deep: true, immediate: true })
    public appDispInfoStartupOutputWatcher(value: IAppDispInfoStartupOutput | null) {
        const vm = this;

        vm.$auth.user.then((user: any) => {
            if (value) {
                const { companyId, employeeId } = user;
                const { appDispInfoWithDateOutput, appDispInfoNoDateOutput } = value;
                const { approvalFunctionSet, empHistImport } = appDispInfoWithDateOutput;

                const { appUseSetLst } = approvalFunctionSet;
                const { employmentCode } = empHistImport;
                const { applicationSetting } = appDispInfoNoDateOutput;

                const { receptionRestrictionSetting } = applicationSetting;

                vm.kafS00AParams = {
                    applicationUseSetting: appUseSetLst[0],
                    companyID: companyId,
                    employeeID: employeeId,
                    employmentCD: employmentCode,
                    receptionRestrictionSetting: receptionRestrictionSetting[0],
                    opOvertimeAppAtr: null,
                };
                vm.kafS00BParams = {
                    appDisplaySetting: value.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting,
                    newModeContent: {
                        useMultiDaySwitch: true,
                        initSelectMultiDay: false,
                        appTypeSetting: value.appDispInfoNoDateOutput.applicationSetting.appTypeSetting,
                        appDate: null,
                        dateRange: null,
                    },
                    mode: 0,
                    detailModeContent: null
                };
            }
        });
    }

    public beforeCreate() {
        const vm = this;

        vm.application = {
            appDate: '',
            appID: '',
            appType: 0,
            employeeID: '',
            enteredPerson: '',
            inputDate: '',
            opAppEndDate: '',
            opAppReason: '',
            opAppStandardReasonCD: 0,
            opAppStartDate: '',
            opReversionReason: null,
            opStampRequestMode: null,
            prePostAtr: 0,
            reflectionStatus: null,
            version: null,
        };
    }

    public created() {
        const vm = this;
        const { OPTIONAL_ITEM_APPLICATION } = AppType;

        vm.$auth.user
            .then((user: any) => {
            }).then(() => {
                return vm.loadCommonSetting(OPTIONAL_ITEM_APPLICATION);
            }).then((loadData: any) => {
                if (loadData) {
                    vm.$mask('show');
                    let params = {
                        settingItemNoList: vm.settingNoItems
                    };

                    vm.$http
                        .post('at', API.startA2Screen, params)
                        .then((res: any) => {
                            vm.$mask('hide');
                        }).catch(() => {
                            vm.$mask('hide');
                        });
                }
            });
    }
}

const API = {
    startA2Screen: 'screen/at/kaf020/b/get',
    register: 'screen/at/kaf020/b/register',
};