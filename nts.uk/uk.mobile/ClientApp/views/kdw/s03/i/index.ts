import { Vue, _} from '@app/provider';
import { component, Prop } from '@app/core/component';
import { storage } from '@app/utils';

@component({
    name: 'kdws03i',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {},
    constraints: []
})
export class KdwS03IComponent extends Vue {
    @Prop({
        default: () => ({
            isShowNoSelectRow: false,
            selectedCode: null
        })
    })

    public readonly params!: IParameter;
    public allData: Array<any> = [];
    public data: Array<any> = [];
    public activeNoSelect: boolean = true;
    public searchText: string = '';
    public selectedCode: string = '';

    public created() {
        let self = this;
        self.loadData();
    }

    public mounted() {
        this.$mask('show', { message: true });
    }

    public loadData() {
        let self = this;
        self.selectedCode = self.params.selectedCode;
        let cache: any = storage.session.getItem('selectedCode');
        self.$http.post('at', servicePath.selectFormatCode).then((lstData: any) => {
            if (!_.isEmpty(lstData)) {
                self.allData = _.orderBy(lstData.data, ['dailyPerformanceFormatCode'], ['asc']);
                self.data = self.allData;     
                if (cache) {
                    self.selectedCode = cache.selectedCode;
                    if (_.isEmpty( _.filter(self.allData, (item) => item.dailyPerformanceFormatCode == cache.selectedCode))) {
                        storage.session.removeItem('selectedCode');
                    }
                }
            } else {
                this.$modal.error({ messageId: 'Msg_1566', messageParams: ['Com_Class'] }).then(() => self.$close());
            }               
            self.$mask('hide');
        }).catch((res: any) => {
            this.$modal.error(res.messageId).then(() => self.$close());
        });     
           
    }
    public searchList() {
        this.data = _.filter(this.allData, (item) => item.dailyPerformanceFormatCode.indexOf(this.searchText) != -1 || item.dailyPerformanceFormatName.indexOf(this.searchText) != -1 );
    }

    public editFormat(code: string) {
        let self = this;
        self.selectedCode = code;
        storage.session.setItem('selectedCode', {selectedCode: code});
        self.loadData();   
    }

    public closeDialog() {
        this.$close(this.selectedCode);
    }
}

interface IParameter {
    isShowNoSelectRow: boolean;
    selectedCode: string;
}

const servicePath = {
    // selectFormatCode: 'screen/at/dailyperformance/correction/dailyPerfFormat/getFormatList'
    selectFormatCode: 'screen/at/correctionofdailyperformance/getFormatList'
};