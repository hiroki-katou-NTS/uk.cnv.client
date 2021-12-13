import { Vue, moment } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { ParamB } from '../a/index';

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

    @Prop({ default: () => ({ name: '' }) })
    public params!: ParamB;

    public startDate: string = '';
    public endDate: string = '';

    public totalWorkingTime: number = 0;
    public totalWorkingTimeMinutes: number = 0;
    public estimatedSalaryMonthly: number = 0;
    public estimatedSalaryCumulative: number = 0;

    public created() {
        let self = this;
    }

    public mounted() {
        let self = this;

        self.startDate = moment(self.params.targetPeriod.start, 'YYYY/MM/DD').format('M月D日');
        self.endDate = moment(self.params.targetPeriod.end, 'YYYY/MM/DD').format('M月D日');
        let closingDay = moment(self.params.targetPeriod.end, 'YYYY/MM/DD').format('DD');
        let command = {
            // listWorkSchedule: self.params.listWorkSchedule,
            startDate: self.params.targetPeriod.start,
            endDate : self.params.targetPeriod.end,
            baseYM: parseInt(self.params.baseYM),
            closingDay: parseInt(closingDay),
        };
        self.$mask('show');
        self.$http.post('at', API.start, command).then((res: any) => {
            let data: InforInitialDto = res.data;

            self.totalWorkingTime = Math.floor(data.totalWorkingTime / 60);
            self.totalWorkingTimeMinutes = data.totalWorkingTime - self.totalWorkingTime * 60;
            self.estimatedSalaryMonthly = data.estimatedSalaryMonthly;
            self.estimatedSalaryCumulative = data.estimatedSalaryCumulative;
        }).catch((error: any) => {
            // self.errorHandler(error);
        }).then(() => self.$mask('hide'));
    }

    public formatNumberSeparated(num): string {
        return num.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',');
    }
}

const API = {
    start: 'screen/at/ksus01/b/getinforinitial',
};

export interface AttendanceDto {
    attendanceStamp: string;
    leaveStamp: string;
}

export interface InforInitialDto {
    totalWorkingTime: number;
    estimatedSalaryMonthly: number;
    estimatedSalaryCumulative: number;
}