import { component, Prop } from '@app/core/component';
import { _, Vue } from '@app/provider';

@component({
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class Kdls12Component extends Vue {
    @Prop({ default: () => ({}) })
    public params!: any;
    public listTask: Array<TaskManager> = [];
    public listTaskDisplay: Array<TaskManager> = [];
    public taskFrameNo: string;
    public baseDate: string;
    public sid: string;
    public taskCode: string;
    public selectedTask = {};
    public selectedTaskCD: string;

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

        self.baseDate = params.baseDate || self.baseDate;
        self.taskFrameNo = params.taskFrameNo || self.taskFrameNo;
        self.sid = params.sid || self.sid;
        self.taskCode = params.taskCode || self.taskCode;
        self.selectedTaskCD = _.isNil(params.selectionCodeList) ? '' :  ( params.selectionCodeList.length > 0 ? params.selectionCodeList[0] : '' );       
    }

    public back() {
        let self = this;

        self.$close();
    }

    private startPage() {
        let self = this;
        let param = {
            baseDate: self.baseDate,
            taskFrameNo: self.taskFrameNo,
            sid: self.sid,
            taskCode: self.taskCode
        };
        self.$http.post('at', servicePath.getTaskMaster, param).then((result: { data: Array<TaskManager> }) => {
            self.$mask('hide');

            self.setData(result.data);

            self.setSelectedItem();

        }).catch((res: any) => {
            self.showError(res);
        });
    }

    private showError(res: any) {
        let self = this;
        self.$mask('hide');
        if (!_.isEqual(res.message, 'can not found message id')) {
            self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds }).then(() => {
                self.$close();
            });
        } else {
            self.$modal.error(res.message).then(() => {
                self.$close();
            });
        }
    }

    private setData(data: any) {
        let self = this;
        let result: Array<TaskManager> = [];
        if (data) {
            for (let i = 0; i < data.length; i++) {
                result.push(new TaskManager(data[i].code, data[i].taskName, data[i].taskAbName, data[i].expirationStartDate, data[i].expirationEndDate, data[i].remark));
              }        
           
            self.listTask = result;
            self.listTaskDisplay = result;
        }

    }

    public searchEvent(value: string) {

        let self = this;
        if (value == '') {
            self.listTaskDisplay = self.listTask;
        } else {
            self.listTaskDisplay = _.filter(self.listTask, (valueOrg) => {
                return valueOrg.taskName.includes(value);
            });
        }
    }

    private setSelectedItem() {
        let self = this;

        let selectedItem = _.find(self.listTask, function (item) { return item.code == self.selectedTaskCD; });
        self.selectedTask = selectedItem ? selectedItem : '';
    }

    public chooseTask(item: TaskManager) {
        let self = this;
        this.selectedTask = item;

        self.$close({
            selectedTask: self.selectedTask
        });
    }
}

const servicePath = {
    getTaskMaster: 'at/shared/scherec/taskmanagement/taskmaster/tasks'
};
const CONCAT_DATE = ' ～ ';

class TaskManager {
    public code: string;
    public taskName: string;
    public taskAbName: string;
    public expirationStartDate: string;
    public expirationEndDate: string;
    public expireDate: string;
    public remark?: string;
    constructor(code: string, taskName: string, taskAbName: string, expirationStartDate?: string, expirationEndDate?: string, remark?: string) {
        this.code = code;
        this.taskName = taskName;
        this.taskAbName = taskAbName;
        this.expirationStartDate = expirationStartDate;
        this.expirationEndDate = expirationEndDate;
        this.expireDate = expirationStartDate + CONCAT_DATE + expirationEndDate;
        this.remark = remark;
    }
}
interface IParam {
    taskFrameNo: string;
    baseDate: string;
    sid: string;
    taskCode: string;
    selectionCodeList: Array<string>;
}