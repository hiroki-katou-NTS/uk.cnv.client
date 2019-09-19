import { Vue, _ } from '@app/provider';
import { component, Prop } from '@app/core/component';
import { storage } from '@app/utils';
import { KdwS03CComponent } from 'views/kdw/s03/c';
import { KdwS03DComponent } from 'views/kdw/s03/d';
import { KdwS03FComponent } from 'views/kdw/s03/f';
import { KdwS03GComponent } from 'views/kdw/s03/g';

@component({
    name: 'kdws03amenu',
    style: require('../style.scss'),
    template: require('./index.vue'),
    resource: require('../resources.json'),
    components: {
        'kdws03c': KdwS03CComponent,
        'kdws03d': KdwS03DComponent,
        'kdws03f': KdwS03FComponent,
        'kdws03g': KdwS03GComponent,
    }
})
export class KdwS03AMenuComponent extends Vue {
    @Prop({ default: () => ({}) })
    public params: MenuParam;
    public dailyCorrectionState: any = null;

    public title: string = 'KdwS03AMenu';

    public created() {
        this.dailyCorrectionState = _.cloneWith(storage.session.getItem('dailyCorrectionState'));
    }

    public openErrorList() {
        if (this.params.displayFormat == '1') {
            this.$modal('kdws03c', {}, { type: 'dropback' })
                .then((v: any) => {
                    if (v != undefined && v.openB) {
                        this.$close(v);
                    }
                    if (v != 'NotCloseMenu') {
                        this.$close();
                    }
                });
        } else {
            this.$modal('kdws03d', {
                employeeID: this.dailyCorrectionState.selectedEmployee,
                employeeName: (_.find(this.dailyCorrectionState.lstEmployee, (x) => x.id == this.dailyCorrectionState.selectedEmployee)).businessName,
                startDate: this.dailyCorrectionState.dateRange.startDate,
                endDate: this.dailyCorrectionState.dateRange.endDate
            }, { type: 'dropback' })
                .then((v: any) => {
                    if (v != undefined && v.openB) {
                        this.$close(v);
                    }
                    if (v != 'NotCloseMenu') {
                        this.$close();
                    }
                });
        }

    }
    public openKdws03f(param: number) {
        this.$modal('kdws03f', {}, { type: 'dropback' });
    }
    public openKdws03g(param: number) {
        console.log(param);
        this.$modal('kdws03g', { 'remainOrtime36': param }, { type: 'dropback' });
    }

    public processConfirmAll(processFlag: string) {
        this.$mask('show', 0.5);
        let dataCheckSign: Array<any> = [];
        let dateRange: any = this.dailyCorrectionState.dateRange;
        if (this.$dt.fromString(dateRange.startDate) <= new Date()) {
            _.forEach(this.dailyCorrectionState.cellDataLst, (x) => {
                if (!x.confirmDisable) {
                    dataCheckSign.push({
                        rowId: x.id,
                        itemId: 'sign',
                        value: processFlag == 'confirm' ? true : false,
                        employeeId: this.dailyCorrectionState.selectedEmployee,
                        date: this.$dt.fromString(x.dateDetail),
                        flagRemoveAll: false
                    });
                }
            });

            this.$http.post('at', servicePath.confirmAll, dataCheckSign).then((result: { data: any }) => {
                if (processFlag == 'confirm') {
                    this.$modal.info({ messageId: 'Msg_15' }).then((v: any) => {
                        window.location.reload();
                    });
                } else {
                    this.$modal.info({ messageId: 'Msg_1445' }).then((v: any) => {
                        window.location.reload();
                    });
                }
            }).catch((res: any) => {
                this.$modal.error({ messageId: res.messageId }).then((v: any) => {
                    this.$mask('hide');
                });
            });

        } else {
            if (processFlag == 'confirm') {
                this.$modal.error({ messageId: 'Msg_1545' }).then((v: any) => {
                    this.$mask('hide');
                });
            } else {
                this.$modal.error({ messageId: 'Msg_1546' }).then((v: any) => {
                    this.$mask('hide');
                });
            }

        }
    }
}
interface MenuParam {
    displayFormat: string;
    errorReferButtonDis: boolean;
    restReferButtonDis: boolean;
    monthActualReferButtonDis: boolean;
    timeExcessReferButtonDis: boolean;
    allConfirmButtonDis: boolean;
}
const servicePath = {
    confirmAll: 'screen/at/correctionofdailyperformance/confirmAll'
};