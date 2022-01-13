import { component, Prop } from '@app/core/component';
import { _, Vue } from '@app/provider';

@component({
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class Kdl001Component extends Vue {
    @Prop({ default: () => ({}) })
    public params!: any;
    public wkTimes: Array<IWorkTime> = [];
    public wkDisplayTimes: Array<IWorkTime> = [];
    public selectedWorkType = {};
    public selectedWorkTime = {};
    public isAddNone = false;
    public seledtedWkTimeCDs = [];
    public selectedWorkTimeCD: string;
    public workplaceID: string;
    public referenceDate: string;

    public created() {
        let self = this;

        self.setParam(this.params);

        self.startPage();
    }

    public mounted() {
        this.$mask('show', { message: true, opacity: 0.75 });
    }

    private setParam(params: IParam) {
        let self = this;

        if (!params) {
            return;
        }

        self.isAddNone = params.isAddNone || self.isAddNone;
        self.seledtedWkTimeCDs = params.seledtedWkTimeCDs || self.seledtedWkTimeCDs;
        self.selectedWorkTimeCD = params.selectedWorkTimeCD || self.selectedWorkTimeCD;
        self.selectedWorkType = params.selectedWorkType || self.selectedWorkType;
        self.workplaceID = params.workplaceID || self.workplaceID;
        self.referenceDate = params.referenceDate || self.referenceDate;
    }

    public back() {
        let self = this;

        self.$close();
    }

    private startPage() {
        let self = this;
        let param = {
            codes: self.seledtedWkTimeCDs,
            workTimeSelected: self.selectedWorkTimeCD,
            workPlaceId: self.workplaceID,
            referenceDate: self.referenceDate,
            display: self.isAddNone
        };
        self.$http.post('at', servicePath.getAllWkTime, param).then((result: { data: Array<IWorkTime> }) => {
            self.$mask('hide');

            self.setData(result.data);

            self.setSelectedItem();

        }).catch((res: any) => {
            self.showError(res);
        });
    }

    private checkEmptyData(data: IWorkTime[]) {
        let self = this;

        if (_.isEmpty(data)) {
            self.$modal.error({ messageId: 'Msg_1525' }).then(() => {
                self.$close();

                return false;
            });
        }

        return true;
    }

    private showError(res: any) {
        let self = this;
        self.$mask('hide');
        if (res.messageId == 'Msg_1525') {
            self.$modal.error({ messageId: 'Msg_1525' }).then(() => {
                self.$close();
            });
        } else if (!_.isEqual(res.message, 'can not found message id')) {
            self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {
            self.$modal.error(res.message);
        }
    }

    private setData(data: IWorkTime[]) {
        let self = this;

        if (self.isAddNone) {

            Array.prototype.push.apply(self.wkTimes, data);
            self.wkDisplayTimes = self.wkTimes;
        } else {

            self.wkTimes = data;
            self.wkDisplayTimes = data;
        }
    }

    public searchInList(startTime: number, endTime: number) {

        let self = this;
        if (self.checkInputSearch(startTime, endTime)) {
            //「選択可能な就業時間帯」のうち対応する「就業時間帯」に絞りこむ
            if (this.isBothTimeNotNull(startTime, endTime)) {

                self.wkDisplayTimes = _.filter(self.wkTimes, (wktime) => {
                    return wktime.firstStartTime == startTime && wktime.firstEndTime == endTime;
                });

                return;
            }

            if (endTime == null) {
                self.wkDisplayTimes = _.filter(self.wkTimes, (wktime) => {
                    return wktime.firstStartTime == startTime;
                });

                return;
            }

            if (startTime == null) {
                self.wkDisplayTimes = _.filter(self.wkTimes, (wktime) => {
                    return wktime.firstEndTime == endTime;
                });

                return;
            }

        }
    }

    private checkInputSearch(startTime: number, endTime: number) {

        //アルゴリズム「時刻の入力チェック」を実行する
        if (this.isTimeNull(startTime, endTime)) {
            
            this.wkDisplayTimes = this.wkTimes;

            return false;
        }

        if (this.isBothTimeNotNull(startTime, endTime)) {
            if (this.isStartAfterEnd(startTime, endTime)) {

                this.$modal.error({ messageId: 'Msg_54' });

                return false;
            }
        }

        return true;
    }

    private isStartAfterEnd(startTime, endTime) {
        return startTime > endTime;
    }

    private isTimeNull(startTime: number, endTime: number) {

        return startTime == null && endTime == null;
    }

    private isBothTimeNotNull(startTime: number, endTime: number) {

        return startTime != null && endTime != null;
    }

    private setSelectedItem() {
        let self = this;

        let selectedItem = _.find(self.wkTimes, function (item) { return item.code == self.selectedWorkTimeCD; });
        self.selectedWorkTime = selectedItem ? selectedItem : self.wkTimes[0];
    }

    private addNone() {
        let self = this;

        if (!self.isAddNone) {
            return;
        }

        self.wkTimes.push({
            code: '',
            name: '選択なし',
            workTime1: '',
            workTime2: '',
            workAtr: '',
            remark: '',
            firstStartTime: 0,
            firstEndTime: 0,
            secondStartTime: 0,
            secondEndTime: 0
        });

    }

    public chooseWorkTime(item: IWorkTime) {
        let self = this;
        this.selectedWorkTime = item;

        self.$close({
            selectedWorkType: self.selectedWorkType,
            selectedWorkTime: self.selectedWorkTime
        });
    }
}

const servicePath = {
    getAllWkTime: 'at/shared/worktimesetting/get_worktimes_kdls01'
};

interface IWorkTime {
    code: string;
    name: string;
    workTime1: string;
    workTime2: string;
    workAtr: string;
    remark?: string;
    firstStartTime: number;
    firstEndTime: number;
    secondStartTime: number;
    secondEndTime: number;
}

interface IParam {
    isAddNone: boolean;
    seledtedWkTimeCDs: Array<string>;
    selectedWorkTimeCD: string;
    selectedWorkType: any;
    parentPage: string;
    workplaceID: string;
    referenceDate: string;
}