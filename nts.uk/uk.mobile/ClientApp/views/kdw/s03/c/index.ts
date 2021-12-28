import { _, Vue } from '@app/provider';
import { component } from '@app/core/component';
import { storage } from '@app/utils';
import { KdwS03DComponent } from 'views/kdw/s03/d';

@component({
    name: 'kdws03c',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    components: {
        'kdws03d': KdwS03DComponent,
    }
})
export class KdwS03CComponent extends Vue {
    public title: string = 'KdwS03C';
    public dailyCorrectionState: any;
    public displayData: any;
    public createDone: boolean = false;

    //起動
    public created() {
        this.dailyCorrectionState = storage.session.getItem('dailyCorrectionState');
        if (this.dailyCorrectionState.displayFormat == '1') {
            this.displayData = _.filter(this.dailyCorrectionState.cellDataLst, (x) => x.ERAL.includes('ｴﾗｰ') || x.ERAL.includes('ｱﾗｰﾑ'));
        }
        this.createDone = true;
        this.$mask('hide');
    }

    //マスクの作成
    public mounted() {
        if (!this.createDone) {
            this.$mask('show', { message: true });
        }
        if (this.displayData.length == 0) {
            this.$mask('show', 0.5);
            this.$modal.error({ messageId: 'Msg_1553' }).then(() => {
                this.$close();
                this.$mask('hide');
            });
        }
    }

    //エラーリストを取得する
    public openErrorList(employeeId: any) {
        this.$modal('kdws03d', {
            employeeID: employeeId,
            employeeName: (_.find(this.dailyCorrectionState.lstEmployee, (x) => x.id == employeeId)).businessName,
            startDate: this.dailyCorrectionState.dateRange.startDate,
            endDate: this.dailyCorrectionState.dateRange.endDate
        }).then((paramOpenB: any) => {
            if (paramOpenB != undefined && paramOpenB.openB) {
                this.$close(paramOpenB);
            }
        });

    }
}