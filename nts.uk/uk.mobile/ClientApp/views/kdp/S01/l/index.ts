import { component, Prop } from '@app/core/component';
import { _, Vue } from '@app/provider';
import { model } from 'views/kdp/S01/shared/index.d';

const API = {
    GET_EMPLOYEE_TASKS: 'at/record/stamp/employee_work_by_stamping'
};

interface IParam {
    employeeId: string;
}

interface ITaskParam {
    sid: string; //社員ID
    workFrameNo: number; //作業枠NO
    upperFrameWorkCode: string; //上位作業コード
}

@component({
    name: 'kdpS01l',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    constraints: []
})

export class KdpS01LComponent extends Vue {

    @Prop({ default: () => ({}) })
    public params!: IParam;

    public tasks: TaskInfo [] = [];
    public setting: TaskFrameSetting [] = [];
    public frameName: string = '';
    public taskButtons!: TaskInfo[][];
    public framePosition: number = 0;
    public selectedCode: string =  '';
    public frameNo: number = 1;
    public taskNameCd: string = '';
    public taskArray: TaskInfo[][] = [];
    public checkBack: boolean = false;
    public checkNext: boolean = false;
    public checkReturn: boolean = false;
    public wordCodeMap = new Map<number, string>();
    public workGroup: WorkGroup = new WorkGroup({
        workCode1: null,
        workCode2: null,
        workCode3: null,
        workCode4: null,
        workCode5: null
    });


    public created() {
        let vm = this;
        vm.initTask();
    }

    public mounted() {
        let vm = this;

        vm.reloadData();
    }

    public reloadData() {
        let vm = this;

        if (vm.taskArray.length > 0) {
            if (vm.framePosition <= vm.taskArray.length - 1) {
                vm.reload(vm.framePosition);
            }

            if (vm.framePosition == 0) {
                vm.checkBack = false;
            } else {
                vm.checkBack = true;
            }

            if (vm.framePosition < vm.taskArray.length - 1) {
                vm.checkNext = true;
            } else {
                vm.checkNext = false;
            }

            if (vm.frameNo == 1) {
                vm.checkReturn = false;
            } else {
                vm.checkReturn = true;
            }
        }
    }

    public initTask() {
        let vm = this;
        let param: ITaskParam = {sid: vm.params.employeeId, workFrameNo: 1, upperFrameWorkCode: ''};
        vm.$mask('show');
        vm.$http.post('at', API.GET_EMPLOYEE_TASKS, param).then((result: any) => {
            vm.$mask('hide');
            vm.tasks = result.data.task;
            vm.setting = result.data.taskFrameUsageSetting.taskFrameSetting;
            vm.taskArray = _.chunk(vm.tasks, 6);
            vm.frameName = vm.getFrameName(1);
        }).then(() => {
            vm.reload(0);
            vm.reloadData();
        });

    }

    public getFrameName(frameNo: number) {
        let vm = this;

        if (frameNo > 0 && vm.setting.length > 0) {
            let s: TaskFrameSetting = _.find((vm.setting), ['taskFrameNo', frameNo]);

            return s.taskFrameName;
        } else {
            return '';
        }
        
    }

    public reload(index: number) {
        let vm = this;
        vm.tasks = vm.taskArray[index];
    }

    public onClickTask(code: string) {
        let vm = this;
        vm.selectedCode = code;
        vm.framePosition = 0;
        let param: ITaskParam = {sid: vm.params.employeeId, workFrameNo: vm.frameNo + 1, upperFrameWorkCode: vm.selectedCode};
        vm.$mask('show');
        vm.$http.post('at', API.GET_EMPLOYEE_TASKS, param).then((result: any) => {
            vm.$mask('hide');

            if (result) {
                vm.frameNo = _.find(vm.tasks, ['code', code]).frameNo;
                vm.frameNo ++;
                vm.wordCodeMap.set(vm.frameNo - 1, vm.selectedCode);

                if (result.data.task.length == 0) {
                    vm.closeDialogL();
                } else {
                    vm.tasks = result.data.task;
                    vm.setting = result.data.taskFrameUsageSetting.taskFrameSetting;
                    vm.frameName = vm.getFrameName(vm.frameNo);
                    vm.taskArray = _.chunk(result.data.task, 6);
                    vm.reloadData();
                    vm.taskNameCd = '';
                }

            }
        });

        setTimeout(function () {
            document.getElementsByTagName('input')[0].focus();
        }, 200);

    }

    public onClickSearch() {
        let vm = this;

        if (vm.taskNameCd == '') {
            vm.$modal.error({ messageId: 'MsgB_24' });
        } else {

            let param: ITaskParam = {sid: vm.params.employeeId, workFrameNo: vm.frameNo, upperFrameWorkCode: vm.selectedCode };

            vm.$mask('show');
            vm.$http.post('at', API.GET_EMPLOYEE_TASKS, param).then((result: any) => {
                vm.$mask('hide');
    
                if (result) {
                    if (result.data.taskFrameUsageSetting) {
                        vm.setting = result.data.taskFrameUsageSetting.taskFrameSetting;
                    }
                    vm.tasks = result.data.task;
                }

            }).then(() => {
            
                let results =_.filter(vm.tasks, function(item) {
                    return item.displayInfo.taskName.indexOf(vm.taskNameCd) > -1 || item.code.indexOf(vm.taskNameCd) > -1 ;
                    });
                
                // L2_1の文字を含む作業見つからなかった場合
                if (results.length == 0) {
                    vm.$modal.error({ messageId: 'MsgB_25' });
                    vm.taskArray = _.chunk(vm.tasks, 6);
                } else {
                    vm.tasks = results;
                    vm.taskArray = _.chunk(vm.tasks, 6);
                }

                vm.reload(0);
                vm.framePosition = 0;
                vm.reloadData();

            });

        }
    }

    public onClickCancel() {
        let vm = this;
        vm.taskNameCd = '';
        let param: ITaskParam = {sid: vm.params.employeeId, workFrameNo: vm.frameNo, upperFrameWorkCode: vm.selectedCode};
        vm.$mask('show');
        vm.$http.post('at', API.GET_EMPLOYEE_TASKS, param).then((result: any) => {
            vm.$mask('hide');
            vm.taskArray = _.chunk(result.data.task, 6);
        }).then(() => {
            vm.reload(0);
            vm.reloadData();
        });

    }

    public onClickBack() {
        let vm = this;
        vm.framePosition --;
        vm.reloadData();
    }

    public onClickNext() {
        let vm = this;
        vm.framePosition ++;
        vm.reloadData();
    }

    public onClickReturn() {
        let vm = this;
        vm.frameNo --;

        vm.taskNameCd = '';
        vm.frameName = vm.getFrameName(vm.frameNo);

        if (vm.frameNo != 1) {
            let upperFrameWorkCd = vm.wordCodeMap.get(vm.frameNo - 1);
            let param: ITaskParam = {sid: vm.params.employeeId, workFrameNo: vm.frameNo, upperFrameWorkCode: upperFrameWorkCd};

            vm.$mask('show');
            vm.$http.post('at', API.GET_EMPLOYEE_TASKS, param).then((result: any) => {
                vm.$mask('hide');

                if (result.data) {

                    if (result.data.taskFrameUsageSetting) {
                        vm.setting = result.data.taskFrameUsageSetting.taskFrameSetting;
                        vm.frameName = vm.getFrameName(vm.frameNo);
                    }

                    vm.tasks = result.data.task;
                    
                    vm.taskArray = _.chunk(result.data.task, 6);
                    vm.reloadData();
                    vm.framePosition = 0;
                }
        
        });
        } else {
            vm.framePosition = 0;
            vm.initTask();
            vm.frameNo = 1;
            vm.reloadData();
        }

        vm.wordCodeMap.set(vm.frameNo, null);
        setTimeout(function () {
            document.getElementsByTagName('input')[0].focus();
        }, 200);
        
    }

    public closeDialogL() {
        let vm = this;

        vm.workGroup = new WorkGroup({
            workCode1 : vm.wordCodeMap.get(1) ? vm.wordCodeMap.get(1) : null,
            workCode2 : vm.wordCodeMap.get(2) ? vm.wordCodeMap.get(2) : null,
            workCode3 : vm.wordCodeMap.get(3) ? vm.wordCodeMap.get(3) : null,
            workCode4 : vm.wordCodeMap.get(4) ? vm.wordCodeMap.get(4) : null,
            workCode5 : vm.wordCodeMap.get(5) ? vm.wordCodeMap.get(5) : null
        });

        vm.$close(vm.workGroup);
    }

}

interface TaskInfo {
    code: string; // コード
    frameNo: number; // 作業枠NO
    displayInfo: TaskDisplayInfo; // 表示情報 : 作業表示情報
    //childTaskList: string []; // 子作業一覧
}

interface TaskDisplayInfo {
    taskName: string; //名称
}

interface TaskFrameSetting {
    taskFrameNo: number; // 作業枠NO
    taskFrameName: string; // 作業枠名
    useAtr: number; // するしない区分
}

interface TaskFrameSet {
    taskFrameSetting: TaskFrameSetting [];
}

interface Result {
    task: TaskInfo []; //List＜作業＞
    taskFrameUsageSetting: TaskFrameSet; //作業枠利用設定

}

class WorkGroup {
    public workCode1: string | null;
    public workCode2: string | null;
    public workCode3: string | null;
    public workCode4: string | null;
    public workCode5: string | null;

    constructor(w: IWorkGroup) {
       this.workCode1 = w.workCode1;
       this.workCode2 = w.workCode2;
       this.workCode3 = w.workCode3;
       this.workCode4 = w.workCode4;
       this.workCode5 = w.workCode5;
    } 
}

interface IWorkGroup {
    workCode1: string;
    workCode2: string;
    workCode3: string;
    workCode4: string;
    workCode5: string;
}

