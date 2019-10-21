import { _, Vue } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';

@component({
    name: 'cdls24a',
    template: require('./index.vue'),
    resource: require('./resources.json'),
})
export class CdlS24AComponent extends Vue {
    @Prop({
        default: () => ({
            selectedCode: ''
        })
    })
    public readonly params!: {selectedCode: string};
    public allData: Array<any> = [];
    public data: Array<any> = [];
    public searchText: string = '';

    public created() {
        let self = this;
        self.$http.post('at', servicePath.getAll).then((result: any) => {
            if (_.isEmpty(result.data)) {
                this.$modal.error({ messageId: 'Msg_1566', messageParams: ['CDLS24_5'] }).then(() => self.$close());
            } else {
                self.allData = _.orderBy(result.data, ['code'], ['asc']);
                self.data = self.allData;
            }
        });
    }

    public searchList() {
        this.data = _.filter(this.allData, (item) => item.code.indexOf(this.searchText) != -1 || item.name.indexOf(this.searchText) != -1);
    }

}
const servicePath = {
    getAll: 'at/record/worktypeselection/getall'
};