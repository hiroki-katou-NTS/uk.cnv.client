import { Vue, _, moment } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';

@component({
    name: 'kafs00b',
    style: require('./style.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        date: {
            required: true  
        },
        dateRange: {
            required: true,
            dateRange: true
        }
    },
    constraints: []
})
export class KafS00BComponent extends Vue {
    @Prop({ default: () => ({}) })
    public params: { 
        // KAFS00_B_起動情報
        input: {
            // 画面モード
            mode: ScreenMode;
            // 申請表示設定
            appDisplaySetting: any;
            // 新規モード内容
            newModeContent?: NewModeContent;
            // 詳細モード内容
            detailModeContent?: DetailModeContent;
        },
        output: {
            // 事前事後区分
            prePostAtr: number;
            // 申請開始日
            startDate: Date;
            // 申請終了日
            endDate: Date;
        }
    };
    public prePostResource: Array<Object> = [];
    public dateSwitchResource: Array<Object> = [];
    public date: Date = null;
    public dateRange: any = {};

    public created() {
        const self = this;
        self.prePostResource = [{
            code: 0,
            text: 'KAFS00_10'
        }, {
            code: 1,
            text: 'KAFS00_11'
        }];
        self.dateSwitchResource = [{
            code: false,
            text: 'KAFS00_12'
        }, {
            code: true,
            text: 'KAFS00_13'
        }];
        self.dateRange = {
            start: null,
            end: null,
        };
        if (self.$input.newModeContent.appTypeSetting.displayInitialSegment != 2) {
            self.$output.prePostAtr = self.$input.newModeContent.appTypeSetting.displayInitialSegment;
        }
        if (self.$input.newModeContent.initSelectMultiDay) {
            self.$updateValidator('dateRange', { validate: true });
            self.$updateValidator('date', { validate: false });
        } else {
            self.$updateValidator('dateRange', { validate: false });
            self.$updateValidator('date', { validate: true });
        }
    }

    get $input() {
        const self = this;

        return self.params.input;
    }

    get $output() {
        const self = this;

        return self.params.output;
    }

    get displayPrePost() {
        const self = this;

        return self.$input.appDisplaySetting.prePostDisplayAtr == 0 ? false : true;
    }

    get enablePrePost() {
        const self = this;

        return self.$input.newModeContent.appTypeSetting.canClassificationChange;
    }

    get displayMultiDaySwitch() {
        const self = this;

        return self.$input.newModeContent.useMultiDaySwitch;
    }

    get ScreenMode() {
        return ScreenMode;
    }

    get prePostAtrName() {
        const self = this;
        
        return _.find(self.prePostResource, (o: any) => o.code == self.$input.detailModeContent.prePostAtr).text;
    }

    @Watch('$input.newModeContent.initSelectMultiDay')
    public initSelectMultiDayWatcher(value: any) {
        const self = this;
        if (value) {
            self.$updateValidator('dateRange', { validate: true });
            self.$updateValidator('date', { validate: false });
        } else {
            self.$updateValidator('dateRange', { validate: false });
            self.$updateValidator('date', { validate: true });
        }
    } 

    @Watch('date')
    public dateWatcher() {
        const self = this;
        self.$output.startDate = self.date;
        self.$output.endDate = self.date;
    } 

    @Watch('dateRange')
    public dateRangeWatcher() {
        const self = this;
        self.$output.startDate = self.dateRange.start;
        self.$output.endDate = self.dateRange.end;
    } 
}

// 画面モード
export enum ScreenMode {
    // 新規モード
    NEW = 0,
    // 詳細モード
    DETAIL = 1
}

// 新規モード内容
interface NewModeContent {
    // 申請種類別設定
    appTypeSetting: any;
    // 複数日切り替えを利用する
    useMultiDaySwitch: boolean;
    // 複数日を初期選択する
    initSelectMultiDay: boolean;
    // 申請日
    appDate?: Date;
    // 申請日期間
    dateRange?: any;
}

// 詳細モード内容
interface DetailModeContent {
    // 事前事後区分
    prePostAtr: string;
    // 申請者名
    employeeName: string;
    // 申請開始日
    startDate: string;
    // 申請終了日
    endDate: string;       
}