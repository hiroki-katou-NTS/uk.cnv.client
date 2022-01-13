import { component, Prop } from '@app/core/component';
import * as _ from 'lodash';
import { IParams, IOptionalItemAppSet, OptionalItemApplication, optionalItems, IControlOfAttendanceItemsDto, IOptionalItemDto } from '../a/define';
import { KafS00AComponent } from '../../s00/a';
import { KafS00BComponent } from '../../s00/b';
import { KafS00CComponent } from '../../s00/c';
import { AppType, Application, KafS00ShrComponent } from '../../s00/shr';
import { CmmS45CComponent } from '../../../cmm/s45/c/index';
import {KafS20AmountInputComponent, KafS20NumberInputComponent, KafS20TimeInputComponent} from '../components';

@component({
    name: 'kafs20a2',
    route: '/kaf/s20/a2',
    style: require('./style.scss'),
    template: require('./index.vue'),
    components: {
        'KafS00AComponent': KafS00AComponent,
        'KafS00BComponent': KafS00BComponent,
        'KafS00CComponent': KafS00CComponent,
        'cmms45c': CmmS45CComponent,
        'kafs20-amount-input': KafS20AmountInputComponent,
        'kafs20-number-input': KafS20NumberInputComponent,
        'kafs20-time-input': KafS20TimeInputComponent
    },
    resource: require('./resources.json')
})
export class KafS20A2Component extends KafS00ShrComponent {
    public title: string = 'KafS20A2';
    public application: Application;
    public optionalItemApplication: OptionalItemApplication[] = [];
    public isValidateAll: boolean = true;
    public user: any = null;

    @Prop({ default: () => true })
    public mode: boolean;

    @Prop({ default: () => [] })
    public readonly settingItems: IOptionalItemAppSet;

    @Prop({default : () => {}})
    public params: IParams;

    public created() {
        const vm = this;
        if (vm.params) {
            vm.mode = false;
            vm.appDispInfoStartupOutput = vm.params.appDispInfoStartupOutput;
            vm.application = vm.appDispInfoStartupOutput.appDetailScreenInfo.application;
            vm.optionalItemApplication = [];

            const { optionalItems } = vm.params.appDetail.application;

            vm.params.appDetail.optionalItems.forEach((optionalItem: any) => {
                let item = _.find(optionalItems, {itemNo: optionalItem.optionalItemNo});
                const { calcResultRange, optionalItemAtr, optionalItemName, optionalItemNo, unit, description, dispOrder, inputCheck } = optionalItem;
                const { lowerCheck, upperCheck, amountLower, amountUpper, numberLower, numberUpper, timeLower, timeUpper } = calcResultRange;

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
            vm.optionalItemApplication.sort((a, b) => a.dispOrder - b.dispOrder);
        } else {
            vm.application = vm.createApplicationInsert(AppType.OPTIONAL_ITEM_APPLICATION);
        }
        vm.initService();
    }

    public initService() {
        const vm = this;
        vm.$mask('show');
        vm.$auth.user.then((user: any) => {
            vm.user = user;
        }).then(() => {
            return vm.mode ? vm.loadCommonSetting(AppType.OPTIONAL_ITEM_APPLICATION) : true;
        }).then((loadData: any) => {
            if (loadData) {
                vm.updateKaf000_A_Params(vm.user);
                vm.updateKaf000_B_Params(vm.mode);
                vm.updateKaf000_C_Params(vm.mode);
                if (vm.mode) {
                    vm.kaf000_B_Params.newModeContent.useMultiDaySwitch = false;

                    let settingNoItems = vm.settingItems.settingItems.map((settingNoItem) => settingNoItem.no);
                    let params = {
                        optionalItemNos: settingNoItems,
                    };
                    vm.$http.post('at', API.getListItemNo, params).then((res: any) => {
                        let optionalNoItems: IOptionalItemDto[] = res.data;
                        settingNoItems.forEach((itemNo: number) => {
                            let optionalItem = optionalNoItems.find((optionalItem) => optionalItem.optionalItemNo == itemNo);
                            const {calcResultRange, optionalItemAtr, optionalItemName, optionalItemNo, unit, description, inputCheck} = optionalItem;
                            const {lowerCheck, upperCheck, amountRange, numberRange, timeRange} = calcResultRange;
                            const {dailyAmountRange} = amountRange;
                            const {dailyNumberRange} = numberRange;
                            const {dailyTimeRange} = timeRange;

                            vm.optionalItemApplication.push({
                                lowerCheck,
                                upperCheck,
                                amountLower: dailyAmountRange.lowerLimit,
                                amountUpper: dailyAmountRange.upperLimit,
                                numberLower: dailyNumberRange.lowerLimit,
                                numberUpper: dailyNumberRange.upperLimit,
                                timeLower: dailyTimeRange.lowerLimit,
                                timeUpper: dailyTimeRange.upperLimit,
                                amount: null,
                                number: null,
                                time: null,
                                inputUnitOfItem: vm.getInputUnit(optionalItemAtr, calcResultRange),
                                optionalItemAtr,
                                optionalItemName,
                                optionalItemNo,
                                unit,
                                description,
                                dispOrder: null,
                                inputCheckbox: inputCheck
                            });
                        });
                        vm.$mask('hide');
                    }).catch((error) => {
                        vm.handleErrorMessage(error);
                        vm.$mask('hide');
                    });
                }
            } else {
                vm.$mask('hide');
            }
        });
    }

    private getInputUnit(optionalItemAtr: number, calcResultRange: any) {
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

    public backToStep1() {
        const vm = this;

        vm.$emit('backToStep1');
    }

    public nextToStep3() {
        const vm = this;

        let validAll: boolean = true;
        //check validate all
        vm.$mask('show');
        for (let child of vm.$children) {
            child.$validate();
            if (!child.$valid) {
                validAll = false;
            }
        }
        vm.isValidateAll = validAll;
        vm.$validate();
        if (!vm.$valid || !validAll) {
            vm.$nextTick(() => {
                vm.$mask('hide');
            });

            window.scrollTo(50, 100);

            return;
        }

        vm.register();
    }

    public register() {
        const vm = this;

        let optionalItems: optionalItems[] = [];
        vm.optionalItemApplication.forEach((item) => {
            optionalItems.push(({
                amount: item.amount,
                time: item.time,
                times: item.number,
                itemNo: item.optionalItemNo,
            }));
        });
        let params = {
            application: vm.application,
            appDispInfoStartup: vm.appDispInfoStartupOutput,
            optItemAppCommand: {
                code: vm.settingItems.code,
                optionalItems
            }
        };
        vm.$mask('show');

        vm.$http.post('at', API.register, params).then((res: any) => {
            vm.$mask('hide');
            vm.$http.post('at', API.reflectApp, res.data.reflectAppIdLst);
            vm.$emit('nextToStep3', res);
        }).catch((error) => {
            vm.$mask('hide');
            vm.handleErrorMessage(error);
        });

    }

    public updateOptionalItem() {
        const vm = this;

        const { params } = vm;
        const { appDispInfoStartupOutput } = params;
        const { appDetailScreenInfo } = appDispInfoStartupOutput;

        const { application } = appDetailScreenInfo;

        application.opAppReason = vm.application.opAppReason;
        // application.opReversionReason = vm.application.opReversionReason;
        application.opAppStandardReasonCD = vm.application.opAppStandardReasonCD;
        let optionalItems: optionalItems[] = [];
        vm.optionalItemApplication.forEach((item) => {
            optionalItems.push({
                amount: item.amount,
                itemNo: item.optionalItemNo,
                time: item.time,
                times: item.number,
            });
        });
        let paramsUpdate = {
            application,
            appDispInfoStartup: appDispInfoStartupOutput,
            optItemAppCommand: {
                code: vm.settingItems.code,
                optionalItems
            }
        };
        vm.$mask('show');
        vm.$http.post('at', API.update, paramsUpdate).then((res: any) => {
            vm.$mask('hide');
            vm.$emit('nextToStep3', res);
        }).catch((error: any) => {
            vm.$mask('hide');
            vm.handleErrorMessage(error);
        });
    }

    //handle mess dialog
    public handleErrorMessage(res: any) {
        const vm = this;
        vm.$mask('hide');
        if (res.messageId == 'Msg_197') {
            vm.$modal.error({ messageId: 'Msg_197', messageParams: [] }).then(() => {
                let appID = vm.params.appDispInfoStartupOutput.appDetailScreenInfo.application.appID;
                vm.$modal('cmms45c', { 'listAppMeta': [appID], 'currentApp': appID }).then((newData: IParams) => {
                    vm.params = newData;
                    vm.initService();
                });
            });

            return;
        }
        if (_.isArray(res.errors)) {
            res.errors.forEach((error) => {
                document.querySelector('.item-' + error.parameterIds[1] + ' input').classList.add('is-invalid');
                document.querySelector('.item-' + error.parameterIds[1] + ' .invalid-feedback').innerHTML += '<span>' + error.errorMessage + '</span>';
            });
        } else {
            return vm.$modal.error(res);
        }
    }

    public handleKaf00BChangePrePost(prePost) {
        const vm = this;
        vm.application.prePostAtr = prePost;
    }

    public handleKaf00BChangeDate(changeDate) {
        const vm = this;
        vm.application.opAppStartDate = vm.$dt.date(changeDate.startDate, 'YYYY/MM/DD');
        vm.application.opAppEndDate = vm.$dt.date(changeDate.endDate, 'YYYY/MM/DD');
        vm.application.appDate = vm.$dt.date(changeDate.startDate, 'YYYY/MM/DD');
    }

    public handleKaf00CChangeAppReason(appReason) {
        const vm = this;
        vm.application.opAppReason = appReason;
    }

    public handleKaf00CChangeReasonCD(appReasonCD) {
        const vm = this;
        vm.application.opAppStandardReasonCD = appReasonCD;
    }
}

const API = {
    register: 'ctx/at/request/application/optionalitem/register',
    getControlAttendance: 'ctx/at/request/application/optionalitem/getControlAttendance',
    getListItemNo: 'ctx/at/record/optionalitem/findByListItemNo',
    update: 'ctx/at/request/application/optionalitem/update',
    reflectApp: 'at/request/application/reflect-app'
};