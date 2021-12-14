import { _, Vue } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { vmOf } from 'vue/types/umd';

@component({
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class KdlS36Component extends Vue {
    @Prop({ default: () => ({}) })
    public params!: any;
    public title: string = 'KdlS36';
    public startDate: string = null;
    public endDate: string = null;
    public daysUnit: number = 0;
    public targetSelectionAtr: TargetSelectionAtr = 0;
    public actualContentDisplayList: any[] = null;
    public managementData: HolidayWorkSubHolidayLinkingMng[] = [];
    public employeeId: string = '';
    public substituteHolidayList: string[] = [];
    public holidayWorkInfoList: ISubstituteWorkInfo[] = [];

    public created() {
        const vm = this;

        vm.setParam(vm.params);
        vm.startPage();
    }

    private setParam(params: IParam) {
        const vm = this;

        if (!params) {

            return;
        }

        vm.employeeId = params.employeeId || vm.employeeId;
        vm.startDate = params.period.startDate || vm.startDate;
        vm.endDate = params.period.endDate || vm.endDate;
        vm.daysUnit = params.daysUnit || vm.daysUnit;
        vm.targetSelectionAtr = params.targetSelectionAtr || vm.targetSelectionAtr;
        vm.actualContentDisplayList = params.actualContentDisplayList || vm.actualContentDisplayList;
        vm.managementData = params.managementData || vm.managementData;
    }

    get displayedRequiredNumberOfDays() {
        const vm = this;

        return vm.requiredNumberOfDays;
    }

    get requiredNumberOfDays() {
        const vm = this;

        const { holidayWorkInfoList, substituteHolidayList, daysUnit } = vm;

        const counted = holidayWorkInfoList
            .map((m) => m.checked ? m.remainingNumber : 0)
            .reduce((p, c) => p -= c, substituteHolidayList.length * daysUnit);

        return Math.max(counted, 0);
    }

    public checkRequirementOfDay(item: ISubstituteWorkInfo) {
        const vm = this;
        const { daysUnit, substituteHolidayList, holidayWorkInfoList } = vm;
        const required = substituteHolidayList.length * daysUnit;
        const counted = holidayWorkInfoList
            .filter((c) => !_.isEqual(c, item))
            .map((m) => m.checked ? m.remainingNumber : 0)
            .reduce((p, c) => p -= c, required);

        if (Math.max(counted, 0) === 0 && item.checked) {
            vm.$modal
                .warn({ messageId: 'Msg_1758' })
                .then(() => {
                    item.checked = false;
                    item.index = -1;
                });
        } else if (item.checked) {
            item.index = _.filter(holidayWorkInfoList,(item) => item.checked === true ).length;
        } else {
            item.index = -1;
        }
    }

    get itemDecided() {
        const vm = this;
        const { daysUnit, substituteHolidayList, holidayWorkInfoList } = vm;
        const required = substituteHolidayList.length * daysUnit;
        // tinh lai counted
        const counted = holidayWorkInfoList
            .map((m) => m.checked ? m.remainingNumber : 0)
            .reduce((p, c) => p -= c, required);
        const lastIndex = holidayWorkInfoList.filter((f) => f.checked).length;

        return holidayWorkInfoList
            .map((m) => ({
                ...m,
                // tinh toan lai gia tri remain cua thang cuoi cung duoc check
                remainingNumber: counted < 0 && lastIndex === m.index ? m.remainingNumber + counted :  m.remainingNumber
            }));
    }

    public checkRequirementOfDayWithCheck(item: ISubstituteWorkInfo) {
        const vm = this;

        if (item.enable) {
            item.checked = !item.checked;

            vm.checkRequirementOfDay(item);
        }
    }

    public back() {
        const vm = this;

        vm.$close();
    }

    private startPage() {
        const vm = this;

        const initParams = {
            employeeId: vm.employeeId,
            startDate: new Date(vm.startDate).toISOString(),
            endDate: new Date(vm.endDate).toISOString(),
            daysUnit: vm.daysUnit,
            targetSelectionAtr: vm.targetSelectionAtr,
            actualContentDisplayList: vm.actualContentDisplayList,
            managementData: _.cloneDeep(vm.managementData),
        };

        initParams.managementData.forEach((e) => {
            e.outbreakDay = new Date(e.outbreakDay).toISOString();
            e.dateOfUse = new Date(e.dateOfUse).toISOString();
        });

        vm.$http.post('at', servicesPath.init, initParams).then((result: { data: ParamsData }) => {
            vm.$mask('hide');

            vm.startDate = vm.$dt(new Date(vm.startDate), 'YYYY/MM/DD');
            vm.endDate = vm.$dt(new Date(vm.endDate), 'YYYY/MM/DD');
            const { data } = result;
            const { daysUnit, targetSelectionAtr, substituteHolidayList, holidayWorkInfoList } = data;

            vm.daysUnit = daysUnit;
            vm.targetSelectionAtr = targetSelectionAtr;
            vm.substituteHolidayList = substituteHolidayList;
            vm.holidayWorkInfoList = holidayWorkInfoList
                .map((m, index) => ({
                    ...m,
                    checked: !!_.find(vm.managementData, (i) => i.outbreakDay == m.holidayWorkDate),
                    enable: new Date(m.expirationDate).getTime() >= new Date(vm.startDate).getTime(),
                    get icon() {
                        const { dataType, expiringThisMonth } = m;

                        if (expiringThisMonth === true) {
                            return 'fas fa-exclamation-triangle';
                        }

                        if (dataType === 1) {
                            return 'fas fa-calendar-check';
                        }

                        return '';
                    },
                    handleChange(event) {
                        console.log(event);
                    }
                }));

            //const managementDataTmp = vm.managementData.map((management) => management.outbreakDay);
        }).catch((error: any) => {
            vm.showError(error);
        });
    }

    public handleChange(arg) {
        console.log(arg);
    }

    private showError(res: any) {
        const vm = this;

        vm.$mask('hide');
        if (!_.isEqual(res.message, 'can not found message id')) {
            vm.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {
            vm.$modal.error(res.message);
        }
    }

    public mounted() {
        const vm = this;
    }

    public decide() {
        const vm = this;

        if (vm.requiredNumberOfDays > 0) {
            vm.$modal.warn({ messageId: 'Msg_1759' });

            return;
        }

        const data: ParamsData = {
            daysUnit: vm.daysUnit,
            employeeId: vm.employeeId,
            substituteHolidayList: vm.substituteHolidayList
                .map((m) => new Date(m).toISOString()),
            targetSelectionAtr: vm.targetSelectionAtr,
            holidayWorkInfoList: vm.itemDecided
                .filter((item) => item.checked)
                .map((m) => ({ ...m }))
        };
        data.holidayWorkInfoList.forEach((f) => {
            f.expirationDate = new Date(f.expirationDate).toISOString();
            f.holidayWorkDate = new Date(f.holidayWorkDate).toISOString();
        });

        vm.$mask('show');
        vm.$http
            .post('at', servicesPath.associate, data)
            .then((result: { data: HolidayWorkSubHolidayLinkingMng[] }) => {
                vm.$mask('hide');
                vm.$close({
                    mngDisp: result.data
                });

            })
            .catch((error: any) => {
                vm.showError(error);
            });
    }
}

interface IParam {
    //社員ID								
    employeeId: string;

    //申請期間
    period: DatePeriod;

    //日数単位 (0.5/1.0)
    daysUnit: number;

    //対象選択区分
    targetSelectionAtr: TargetSelectionAtr;

    //List<表示する実績内容>
    actualContentDisplayList: any[];

    //List<振休振出紐付け管理>
    managementData: HolidayWorkSubHolidayLinkingMng[];
}

interface DatePeriod {
    startDate: string;
    endDate: string;
}

enum TargetSelectionAtr {
    //自動
    AUTOMATIC = 0,

    //申請
    REQUEST = 1,

    //手動
    MANUAL = 2,
}

interface HolidayWorkSubHolidayLinkingMng {
    // 社員ID
    employeeId: string;

    // 逐次休暇の紐付け情報 . 発生日
    outbreakDay: string;

    // 逐次休暇の紐付け情報 . 使用日
    dateOfUse: string;

    //逐次休暇の紐付け情報 . 使用日数
    dayNumberUsed: number;

    //逐次休暇の紐付け情報 . 対象選択区分
    targetSelectionAtr: number;
}

interface ParamsData {
    employeeId: string;

    // 日数単位
    daysUnit: number;

    // 振休日リスト
    substituteHolidayList: string[];

    // 対象選択区分
    targetSelectionAtr: TargetSelectionAtr;

    // List<振出データ>
    holidayWorkInfoList: Array<ISubstituteWorkInfo>;
}

interface ISubstituteWorkInfo {
    dataType: DataType;
    expirationDate: string;
    expiringThisMonth: boolean;
    remainingNumber: number;
    holidayWorkDate: string;
    checked: boolean;
    enable: boolean;
    index: number;
}

enum DataType {
    ACTUAL = 0,
    APPLICATION_OR_SCHEDULE = 1
}

const servicesPath = {
    init: 'screen/at/kdl036/init',
    associate: 'screen/at/kdl036/associate',
};