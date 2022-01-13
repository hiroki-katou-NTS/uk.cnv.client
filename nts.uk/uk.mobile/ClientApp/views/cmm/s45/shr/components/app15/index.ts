import { Vue } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { IAppDispInfoStartupOutput } from '../../../../../kaf/s04/a/define';
import { IControlOfAttendanceItemsDto, IOptionalItemDto, OptionalItemApplication } from '../../../../../kaf/s20/a/define';

@component({
    name: 'CmmS45ComponentsApp15Component',
    style: require('./style.scss'),
    template: require('./index.vue'),
    validations: {},
    constraints: []
})
export class CmmS45ShrComponentsApp15Component extends Vue {

    public optionalItemApplication: OptionalItemApplication[] | null = [];
    public appDetail: IAppDetail | null = null;
    public settingItems: ISettings | null = {
        code: '',
        name: '',
        note: '',
        optionalItems: null
    };
    
    @Prop({
        default: () => ({
            appDispInfoStartupOutput: null,
            appDetail: null
        })
    })
    public readonly params: {
        appDispInfoStartupOutput: IAppDispInfoStartupOutput,
        appDetail: IAppDetail,
    };

    @Watch('params.appDispInfoStartupOutput')
    public appDispInfoStartupOutputWatcher(value: any) {
        const vm = this;
        vm.optionalItemApplication = [];
        vm.fetchData(value);
    }

    public created() {
        const vm = this;
        vm.fetchData(vm.params.appDispInfoStartupOutput);
    }

    public fetchData(appDispInfoStartupOutput: any) {
        const vm = this;

        vm.$auth.user.then((user: any) => {
            const { companyId } = user;
            const { appDetailScreenInfo } = appDispInfoStartupOutput;
            const { application } = appDetailScreenInfo;
            const { appID } = application;

            vm.$http.post('at', API.startBScreen, {
                companyId,
                applicationId: appID
            }).then((res: any) => {
                vm.params.appDetail = res.data;
                let optionalItemSetting = vm.params.appDetail.application.optionalItems;
                
                const { params } = vm;
                const { appDetail } = params;
                const { controlOfAttendanceItems, optionalItems, application } = appDetail;

                vm.settingItems = application;

                optionalItems.forEach((optionalItem) => {
                    const item = optionalItemSetting.find((o) => o.itemNo == optionalItem.optionalItemNo);
                    // const controlAttendance = controlOfAttendanceItems.find((controlAttendance) => optionalItem.optionalItemNo == controlAttendance.itemDailyID - 640);
                    const { calcResultRange, optionalItemAtr, optionalItemName, optionalItemNo, unit, description,dispOrder, inputCheck } = optionalItem;
                    const { lowerCheck, upperCheck,amountLower,amountUpper,numberLower,numberUpper,timeLower,timeUpper } = calcResultRange;

                    vm.optionalItemApplication.push({
                        lowerCheck,
                        upperCheck,
                        amountLower,
                        amountUpper,
                        numberLower,
                        numberUpper,
                        timeLower,
                        timeUpper,
                        amount: item ? item.amount : null,
                        number: item ? item.times : null,
                        time: item ? item.time : null,
                        inputUnitOfItem: vm.getInputUnit(optionalItemAtr, calcResultRange),
                        optionalItemAtr,
                        optionalItemName,
                        optionalItemNo,
                        unit,
                        description,
                        dispOrder,
                        inputCheckbox: inputCheck
                    });
                });
                vm.optionalItemApplication.sort((a,b) => a.dispOrder - b.dispOrder );
                vm.$emit('loading-complete');
            });
        });
    }

    private getInputUnit(optionalItemAtr: number, calcResultRange: any): number {
        const vm = this;
        if (optionalItemAtr == 0) {
            switch (calcResultRange.timeInputUnit) {
                case 0: return parseInt(vm.$i18n('KMK002_141'));
                case 1: return parseInt(vm.$i18n('KMK002_142'));
                case 2: return parseInt(vm.$i18n('KMK002_143'));
                case 3: return parseInt(vm.$i18n('KMK002_144'));
                case 4: return parseInt(vm.$i18n('KMK002_145'));
                case 5: return parseInt(vm.$i18n('KMK002_146'));
                default: return null;
            }
        }
        if (optionalItemAtr == 1) {
            switch (calcResultRange.numberInputUnit) {
                case 0: return parseFloat(vm.$i18n('KMK002_150'));
                case 1: return parseFloat(vm.$i18n('KMK002_151'));
                case 2: return parseFloat(vm.$i18n('KMK002_152'));
                case 3: return parseFloat(vm.$i18n('KMK002_153'));
                default: return null;
            }
        }
        if (optionalItemAtr == 2) {
            switch (calcResultRange.amountInputUnit) {
                case 0: return parseInt(vm.$i18n('KMK002_160'));
                case 1: return parseInt(vm.$i18n('KMK002_161'));
                case 2: return parseInt(vm.$i18n('KMK002_162'));
                case 3: return parseInt(vm.$i18n('KMK002_163'));
                case 4: return parseInt(vm.$i18n('KMK002_164'));
                default: return null;
            }
        }

        return null;
    }
}

const API = {
    startBScreen: 'ctx/at/request/application/optionalitem/getDetail'
};

export interface ISettings {
    code: string;
    name: string;
    note: string;
    optionalItems: IOptionalItems[];
    
}

export interface IAppDetail {
    application: ISettings;
    controlOfAttendanceItems: IControlOfAttendanceItemsDto[];
    optionalItems: any[];
}

export interface IOptionalItems {
    amount: number | null;
    itemNo: number | null;
    times: number | null;
    time: number | null;
}
