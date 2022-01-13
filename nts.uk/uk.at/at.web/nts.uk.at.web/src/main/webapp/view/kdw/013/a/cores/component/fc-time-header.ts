module nts.uk.ui.at.kdw013.timeheader {
    @component({
        name: 'fc-times',
        template:
        `<td data-bind="i18n: 'KDW013_25'"></td>
                <!-- ko foreach: { data: $component.params.timesSet, as: 'time' } -->
                    <td class="fc-day" style='position: relative;' data-bind="html: $component.formatTime(time), attr: { 'data-date': time.date }"></td>
                <!-- /ko -->
                <style rel="stylesheet">
                    .warningIcon {
                            cursor: pointer;
                            position: absolute;
                            left: calc(100% - 22px);
                            bottom: calc(100% - 17px);
                        }
                    .warningIcon:hover {
                         background-color: rgb(229, 242, 255);
                        }
                </style>
                `
    })
    export class FullCalendarTimesHeaderComponent extends ko.ViewModel {
        today: string = moment().format('YYYY-MM-DD');

        constructor(private params: TimeHeaderParams) {
            super();

            if (!this.params || !this.params.timesSet) {
                this.params.timesSet = ko.computed(() => []);
            }
        }

        mounted() {
            const vm = this;
            const { $el, params } = vm;
            const {timesSet} = params;

            ko.computed({
                read: () => {
                    const ds = ko.unwrap(timesSet);

                    if (ds.length) {
                        $el.style.display = null;
                    } else {
                        $el.style.display = 'none';
                    }

                    $($el).find('[data-bind]').removeAttr('data-bind');
                },
                disposeWhenNodeIsRemoved: $el
            });

            // fix display on ie
            vm.$el.removeAttribute('style');
        }
        
        regisPopup(time) {
            const className =  'wrn-'+time.date;
            $(".popup-area-i").ntsPopup({
                trigger: '.' + className,
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: '.' + className
                },
                showOnStart: false,
                dismissible: true
            });
        }
        
        OpenIDialog(vm, time) {
            let screenA = vm.params.screenA;
            let {$settings, $datas} = screenA;
                screenA.taskSettings(_.get($settings(), 'taskFrameUsageSetting.frameSettingList', []));
                
                let iod = _.find(_.get($datas(), 'lstIntegrationOfDaily', []), id => { return moment(id.ymd).isSame(moment(time.date), 'days') });
                 // 作業リスト
                screenA.taskDtos($settings().tasks);

                // 日別勤怠の応援作業時間帯
                screenA.ouenWorkTimeSheets(_.get(iod,'ouenTimeSheet'));
                
                // 日別勤怠の応援作業時間
                screenA.ouenWorkTimes(_.get(iod,'ouenTime'));

                //対象日
                screenA.targetDate(moment(time.date).toDate());
        }
        
        

        formatTime(time) {
            const vm = this;
            
            if (!time) {
                return '&nbsp;';
            }
            const className = 'wrn-' + time.date;

            let icon = vm.isHasWarning(time.date) ? `<i class='warningIcon ` + className + `'> </i>` : '';
            
            if (vm.isNoCvrTaskList(time.date)) {
                return '&nbsp;' + icon;
            }
            
            setTimeout(()=> { 
                ko.applyBindingsToNode($('.' + className).not('.img-icon'), { ntsIcon: { no: 228, size: '16px', width: 16, height: 16 }, click: () => { vm.OpenIDialog(vm, time); } }); 
                $('.' + className).on('mousedown', () => { vm.regisPopup(time); });
            }, 300);
            
            let timeString = vm.getTimeString(time.date);


            return (timeString != null ? nts.uk.time.format.byId("Clock_Short_HM", timeString) : '') + icon;
        }
        
        getTimeString(date){
            const vm = this;
            const datas = ko.unwrap(vm.params.screenA.$datas);
            let convert = _.find(_.get(datas, 'convertRes', []), cvr => { return moment(cvr.ymd).isSame(moment(date), 'days'); });
            let listItems = [1305, 1349, 1393, 1437, 1481, 1525, 1569, 1613, 1657, 1701, 1745, 1789, 1833, 1877, 1921, 1965, 2009, 2053, 2097, 2141];
            let sumList = [];
            _.forEach(_.get(convert, 'manHrContents', []), hrc => {
                if (listItems.indexOf(hrc.itemId) != -1 && !!hrc.value) {
                    sumList.push(hrc.value);
                }
            });
            return _.sum(_.map(sumList, s => Number(s)));
        }
        
        isNoCvrTaskList(date) {
            const vm = this;
            const datas = ko.unwrap(vm.params.screenA.$datas);

            let convert = _.find(_.get(datas, 'convertRes', []), cvr => { return moment(cvr.ymd).isSame(moment(date), 'days'); });
            return !_.get(convert, 'taskList');
        }
        
        isHasWarning(date) {
            let vm = this;
            let datas = ko.unwrap(vm.params.screenA.$datas);

            if (!datas) {
                return false;
            }

            let manHrTask = _.find(_.get(datas, 'dailyManHrTasks', []), hr => { return moment(hr.date).isSame(moment(date), 'days'); });

            let id = _.find(_.get(datas, 'lstIntegrationOfDaily', []), id => { return moment(id.ymd).isSame(moment(date), 'days'); });

            let ouenTimeSheet = _.get(id, 'ouenTimeSheet', []);

            let taskBlocks = _.get(manHrTask, 'taskBlocks', []);

            if (!id || !ouenTimeSheet.length) {
                return false;
            }

            for (let i = 0; i < ouenTimeSheet.length; i++) {
                let workNo = _.get(ouenTimeSheet[i], 'workNo');
                
                if (!_.find(taskBlocks, tb => _.find(tb.taskDetails, ['supNo', workNo]))) {
                    return true;
                }
            }

            return false;
        }
        
        
    }
    type TimeHeaderParams = {
        items: KnockoutObservableArray<any>;
        mode: KnockoutComputed<boolean>;
        screenA: nts.uk.ui.at.kdw013.a.ViewModel;
    }; 
}