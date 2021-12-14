
import { component, Prop, Watch } from '@app/core/component';
import { _, Vue, moment } from '@app/provider';
import * as $ from 'jquery';
// import { model } from 'views/new/bundles.js';
import {
    WorkAvailabilityOfOneDayDto,
    WorkAvailabilityDisplayInfoDto,
    DataDisplay,
    DataOneDateScreenKsuS02,
    SubmitWorkRequestCmd
} from 'views/ksu/s02/component/a';

@component({
    // name: 'calendarBComponent',
    style: require('./main.scss'),
    template: require('./index.vue'),
    resource: require('./resources.json'),
    validations: {
        yearMonth: {
            required: true
        },
        memoCurent: {
            constraint: 'WorkAvailabilityMemo'
        }
    },
    constraints: [
        'nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityMemo'
    ]
})
export class CalendarBComponent extends Vue {

    @Prop({
        default: () => ({
            dataFromParent: null
        })
    })
    public params!: { dataFromParent: any };

    public yearMonth = moment().format('YYYYMM');

    public dataStartPage: any = null;

    public listDataDisplay: any = [];

    public idCurent: string = '';

    public memoCurent: string = '';

    public showPopup: any = false;

    public showConfirmDialog: any = false;

    public showInforDialog: any = false;

    public isCurrentMonth: any = true;

    public startDate: string = '';

    public endDate: string = '';

    public isCaretLeft = true;

    public endRight = true;

    public endLeft = true;

    private startWork = '';

    public created() {
        let vm = this;
        vm.dataStartPage = vm.params.dataFromParent.data;
        vm.startWork = vm.dataStartPage.startWork;
        vm.getData();
    }
    public mounted() {
        let self = this;
        self.updateDataRegister();
    }

    @Watch('yearMonth')
    public changeYearMonth(yearMonth: any) {
        $($(document.body)[0]).find('td.cell-focus').removeClass('cell-focus');
        this.showPopup = false;
        if (yearMonth == null) {
            return;
        }
        let self = this;
        let year = parseInt((yearMonth / 100).toString());
        let month = yearMonth % 100;
        if (year > parseInt(self.startWork.substring(0, 4)) || year == parseInt(self.startWork.substring(0, 4)) && month >= parseInt(self.startWork.substring(5, 7))) {
            self.isCurrentMonth = true;
        } else {
            self.isCurrentMonth = false;
        }
        let yearChange = year - parseInt(self.startDate.substring(0, 4));
        let monthChange = month - parseInt(self.startDate.substring(5, 7)) + yearChange * 12;
        let data = self.getDatePeriodDto(self.startDate, self.endDate, monthChange, self.dataStartPage.shiftWorkUnit);
        self.startDate = data.startDate;
        self.endDate = data.endDate;
        this.$emit('changeMonth', { startDate: self.startDate, endDate: self.endDate });

    }

    @Watch('params.dataFromParent')
    public change(data: any) {
        let self = this;
        this.dataStartPage = data.data;
        self.getData();
        self.updateDataRegister();
        if (data.checkRegister) {
            self.closePopup();
            self.showPopup = false;
        }

    }

    public createDataSubmitWorkRequestCmd() {
        let self = this;
        let startDate = new Date(self.dataStartPage.startWork);
        let endDate = new Date(self.dataStartPage.endWork);
        let listDataD = [];
        for (let date = startDate; date <= endDate; date.setDate(date.getDate() + 1)) {
            for (let i = 0; i < self.listDataDisplay.length; i++) {
                if (self.listDataDisplay[i].workAvailabilityOfOneDayDto != null
                    && moment(date).format('YYYY/MM/DD') == self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvailabilityDate
                    && self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod == 0) { // enum AssignmentMethod == HOLIDAY(0), // 休日
                    let temp: DataOneDateScreenKsuS02 = {
                        date: self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvailabilityDate,
                        memo: self.listDataDisplay[i].workAvailabilityOfOneDayDto.memo,
                        assignmentMethod: self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod,
                        nameList: self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList,
                        timeZoneList: self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.timeZoneList
                    };
                    listDataD.push(temp);
                    break;
                }
            }
        }
        let result: SubmitWorkRequestCmd = {
            listData: listDataD,
            startPeriod: self.dataStartPage.startWork,
            endPeriod: self.dataStartPage.endWork
        };

        return result;
    }

    public openNextMonth() {
        let self = this;
        if (self.yearMonth != null) {
            self.yearMonth = moment(self.yearMonth, 'YYYYMM').add(1, 'M').format('YYYYMM');
        }


    }
    public openPrevMonth() {
        let self = this;
        if (self.yearMonth != null) {
            self.yearMonth = moment(self.yearMonth, 'YYYYMM').add(-1, 'M').format('YYYYMM');
        }

    }

    public getData() {
        let self = this;
        if (self.dataStartPage == null || self.dataStartPage.startWork == undefined) {
            return;
        }
        let startDate = new Date(self.dataStartPage.startWork);
        let startDateClone = new Date(startDate.getTime());
        if (parseInt(moment(new Date()).format('YYYYMM')) < parseInt(moment(startDate, 'YYYYMM').format('YYYYMM'))) {
            self.yearMonth = moment(startDate, 'YYYYMM').format('YYYYMM');
        }
        let numberStartDisplay = moment(startDate).day(); // 0  -> 6 :  0(chủ nhật),1(thứ 2)... 6:(thứ 7)
        let endDate = new Date(self.dataStartPage.endWork);
        self.startDate = self.dataStartPage.startWork;
        self.endDate = self.dataStartPage.endWork;
        //list data display 
        let listData = [];
        for (let i = 0; i < numberStartDisplay; i++) {
            let dataDisplay: DataDisplay = {
                id: 'd' + i,
                numberDisplay: i,
                dayOfWeek: '',
                disable: true,
                dateDisplay: '',
                workAvailabilityOfOneDayDto: null,
                showMemo: false,
                nameListInfor: [],
                canUpdateCell: false
            };
            listData.push(dataDisplay);
        }
        for (let date = startDate; date <= endDate; date.setDate(date.getDate() + 1)) {
            let dataDate = _.find(self.dataStartPage.displayInfoListWorkOneDay, function (o) { return o.workAvailabilityDate == moment(date).format('YYYY/MM/DD'); });

            if (dataDate == null) {
                dataDate = self.getDateInfoDefault(moment(date).format('YYYY/MM/DD'));
            }
            if (dataDate.workAvaiByHolidayDto.assignmentMethod != 0) {
                dataDate.memo = '';
            }
            let classDisplayToDay = '';
            if (moment(date).format('YYYY/MM/DD') == moment().format('YYYY/MM/DD')) {
                classDisplayToDay = 'class=\"uk-bg-schedule-that-day\"';
                // console.log(moment().format('YYYY/MM/DD'));
            }
            let dateDisplayD = date.getDate() == 1 ?
                (date.getMonth() + 1).toString() + '/' +
                date.getDate().toString() : date.getDate().toString();

            let isHoliday = _.find(self.dataStartPage.listDateIsHoliday, function (o) { return o == moment(date).format('YYYY/MM/DD'); });
            if (isHoliday != null) {
                dateDisplayD = '<span style =\"color: #FF0000;\" ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
            } else {
                if (date.getDay() == 0) {
                    dateDisplayD = '<span style =\"color: #FF0000;\" ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                } else if (date.getDay() == 6) {
                    dateDisplayD = '<span style =\"color: #0000FF;\" ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                } else {
                    dateDisplayD = '<span ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                }
            }

            let checkMemo = false;
            if (dataDate.memo != null && dataDate.memo != '') {
                checkMemo = true;
            }
            let nameListInforData = [];

            let dataDisplay: DataDisplay = {
                id: 'd' + numberStartDisplay,
                numberDisplay: numberStartDisplay,
                dayOfWeek: moment(moment(date).format('YYYY/MM/DD')).format('dd'),
                disable: false,
                dateDisplay: dateDisplayD,
                workAvailabilityOfOneDayDto: dataDate,
                showMemo: checkMemo,
                nameListInfor: nameListInforData,
                canUpdateCell: true
            };
            listData.push(dataDisplay);
            numberStartDisplay = numberStartDisplay + 1;
        }
        // shiftWorkUnit 1：一ヶ月間	　　　0：一週間"
        if (self.dataStartPage.shiftWorkUnit == 0) {
            let startD = new Date(endDate.getTime());
            let endD = new Date(endDate.getTime());
            startD.setDate(startD.getDate() + 1);
            endD.setDate(endD.getDate() + 21);

            for (let date = startD; date <= endD; date.setDate(date.getDate() + 1)) {
                let classDisplayToDay = '';
                if (moment(date).format('YYYY/MM/DD') == moment().format('YYYY/MM/DD')) {
                    classDisplayToDay = 'class=\"uk-bg-schedule-that-day\"';
                    // console.log(moment().format('YYYY/MM/DD'));
                }
                let dateDisplayD = date.getDate() == 1 ?
                    (date.getMonth() + 1).toString() + '/' +
                    date.getDate().toString() : date.getDate().toString();
                let isHoliday = _.find(self.dataStartPage.listDateIsHoliday, function (o) { return o == moment(date).format('YYYY/MM/DD'); });
                if (isHoliday != null) {
                    dateDisplayD = '<span style =\"color: #FF0000;\" ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                } else {
                    if (date.getDay() == 0) {
                        dateDisplayD = '<span style =\"color: #FF0000;\" ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                    } else if (date.getDay() == 6) {
                        dateDisplayD = '<span style =\"color: #0000FF;\" ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                    } else {
                        dateDisplayD = '<span ' + classDisplayToDay + '>' + dateDisplayD + '</span>';
                    }
                }
                let dataDisplay: DataDisplay = {
                    id: 'd' + numberStartDisplay,
                    numberDisplay: numberStartDisplay,
                    dayOfWeek: '',
                    disable: false,
                    dateDisplay: dateDisplayD,
                    workAvailabilityOfOneDayDto: null,
                    showMemo: false,
                    nameListInfor: [],
                    canUpdateCell: false
                };
                listData.push(dataDisplay);
                numberStartDisplay = numberStartDisplay + 1;
            }
        }
        for (let i = listData.length; i < 42; i++) {
            let dataDisplay: DataDisplay = {
                id: 'd' + i,
                numberDisplay: i,
                dayOfWeek: '',
                disable: true,
                dateDisplay: '',
                workAvailabilityOfOneDayDto: null,
                showMemo: true,
                nameListInfor: [],
                canUpdateCell: false
            };
            listData.push(dataDisplay);
        }
        self.listDataDisplay = listData;
        // console.log(self.listDataDisplay);
    }

    public getDateInfoDefault(date: string) {
        let self = this;
        let workAvailabilityDisplayInfoDefault: WorkAvailabilityDisplayInfoDto = {
            assignmentMethod: null, //AssignmentMethod enum
            nameList: [],
            timeZoneList: []
        };
        let workAvailabilityOfOneDayDto: WorkAvailabilityOfOneDayDto = {
            employeeId: null,
            workAvailabilityDate: date,
            memo: null,
            workAvaiByHolidayDto: workAvailabilityDisplayInfoDefault
        };

        return workAvailabilityOfOneDayDto;
    }

    public cellFocus(el) {
        let self = this;
        if (self.showPopup) {
            self.showPopup = false;

            return;
        }
        self.showPopup = false;
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == el.currentTarget.id; });
        self.memoCurent = '';
        if (dataClick != null) {
            //dataClick.showMemo = (dataClick.workAvailabilityOfOneDayDto.memo != '' && dataClick.workAvailabilityOfOneDayDto.memo != null) ? true : false;
            self.memoCurent = dataClick.workAvailabilityOfOneDayDto.memo;
        }
        self.idCurent = el.currentTarget.id;
        let popup = $($(document.body)[0]).find('.nts-help-button-image');
        $($(document.body)[0]).find('td.cell-focus').removeClass('cell-focus');
        $('#' + el.currentTarget.id).addClass('cell-focus');
        if (!self.isCurrentMonth) {
            this.memoInput();
            this.setData(el);
            // $('nts-text-area').attr('disabled', 'disabled');

            return;
        }
        if (dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod == 0) {
            self.showPopup = true;
            let offset = $('#' + el.currentTarget.id).offset();
            let offsetLeft = offset.left - 10;
            if (self.idCurent == 'd5' || self.idCurent == 'd12' || self.idCurent == 'd19' || self.idCurent == 'd26' || self.idCurent == 'd33' || self.idCurent == 'd40') {
                offsetLeft = offset.left - 20;
            }

            if (self.idCurent == 'd6' || self.idCurent == 'd13' || self.idCurent == 'd20' || self.idCurent == 'd27' || self.idCurent == 'd34' || self.idCurent == 'd41') {
                offsetLeft = offset.left - 75;
                self.isCaretLeft = false;
                self.endLeft = false;
                self.endRight = true;
            } else if (self.idCurent == 'd0' || self.idCurent == 'd7' || self.idCurent == 'd14' || self.idCurent == 'd21' || self.idCurent == 'd28' || self.idCurent == 'd35') {
                offsetLeft = offset.left + 10;
                self.isCaretLeft = false;
                self.endLeft = true;
                self.endRight = false;
            } else {
                self.isCaretLeft = true;
                self.endLeft = false;
                self.endRight = false;
            }
            $(popup)
                .fadeIn()
                .css({
                    left: offsetLeft,
                    top: offset.top - 73
                });

            return;
        } else {
            dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod = 0;
            dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList = [];
            dataClick.workAvailabilityOfOneDayDto.memo = '';
        }
        this.setMonthDay(el);
        this.setData(el);
        self.updateDataRegister();
    }

    public setData(el) {

        let items = $($(document.body)[0]).find('span.holiday');

        let cln = items[items.length - 1].cloneNode(true) as HTMLElement;
        $(cln).addClass('config-btn');
        // clear data
        // let dataArea = $(el).find('.data-area');
        let dataArea = $('#' + el.currentTarget.id).find('.data-area');
        $(dataArea).empty();
        dataArea.append(cln);
    }

    public updateDataRegister() {
        let dataRegister = this.createDataSubmitWorkRequestCmd();
        this.$emit('passDataToParent', dataRegister);
    }

    public clearAll() {
        let self = this;
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        if (dataClick != null) {
            dataClick.workAvailabilityOfOneDayDto.memo = '';
            dataClick.showMemo = false;
            dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod = null;
        }
        self.memoCurent = '';
        this.showPopup = false;

        let itemsInCell = $($(document.body)[0]).find('td.cell-focus');
        $(itemsInCell[0]).find('i').remove();
        $(itemsInCell[0].children[1]).empty();
        $(itemsInCell[0].children[2]).empty();
        self.updateDataRegister();
    }

    public memoInput() {
        this.showPopup = false;

        let el = $($(document.body)[0]).find('td.cell-focus')[0];
        this.showConfirmDialog = true;
        this.setMonthDay(el);
    }

    public closePopup() {
        let self = this;
        this.showConfirmDialog = false;
        self.updateDataRegister();
    }

    public setMemo() {
        let self = this;
        if (!self.$valid) {

            return;
        }
        let checkExistMemo = false;
        if (self.memoCurent != null && self.memoCurent != '') {
            checkExistMemo = true;
        }
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        if (dataClick != null) {
            dataClick.workAvailabilityOfOneDayDto.memo = self.memoCurent;
            dataClick.showMemo = (dataClick.workAvailabilityOfOneDayDto.memo != '' && dataClick.workAvailabilityOfOneDayDto.memo != null) ? true : false;
        }

        let memoIcon = document.createElement('small');
        $(memoIcon).css('position', 'absolute');
        $(memoIcon).css('color', 'black');
        if (!checkExistMemo) {
            $(memoIcon).prepend('<i class=\'far fa-sticky-note memo-css\'></i>');
        }
        let el = $($(document.body)[0]).find('td.cell-focus')[0];
        let offset = $(el).offset();
        $(memoIcon)
            .fadeIn()
            .css({
                left: offset.left + 35,
                top: offset.top + 4
            });
        // append row
        if (self.memoCurent != '' && self.memoCurent != null) {
            $(el).append(memoIcon);
        }

        this.closePopup();
    }

    public clearModalBackdrop() {
        // setTimeout(() => {
        //     $($(document.body)[0]).find('.modal-backdrop').addClass('hidden-popup');
        // }, 10);
        this.showInforDialog = false;
    }

    public setMonthDay(el) {
        let self = this;
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        let text = parseInt(dataClick.workAvailabilityOfOneDayDto.workAvailabilityDate.substring(5, 7)) + '月'
            + parseInt(dataClick.workAvailabilityOfOneDayDto.workAvailabilityDate.substring(8, 10)) + '日（' + dataClick.dayOfWeek + '）';
        $($(document.body)[0]).find('span#monthday')[0].innerHTML = text;
    }

    /**
     * 
     * @param startDate  
     * @param endDate 
     * @param monthChange number month change
     * @param shiftWorkUnit mode month(1) or mode week(0)
     */
    public getDatePeriodDto(startDate: string, endDate: string, monthChange: number, shiftWorkUnit: number) {
        let startD = '';
        let startE = '';
        let yearMonth = startD.substring(0, 4) + startD.substring(5, 7);
        if (shiftWorkUnit == 1) { //mode month
            if (parseInt(startDate.substring(8, 10)) == 1) {
                startD = moment(startDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
                let lastDate = new Date(parseInt(startD.substring(0, 4)), parseInt(startD.substring(5, 7)), 0).getDate();
                startE = startD.substring(0, 8) + lastDate;
            } else {
                startD = moment(startDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
                startE = moment(endDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
                yearMonth = moment(yearMonth, 'YYYYMM').add(1, 'M').format('YYYYMM');
                let lastDate = new Date(parseInt(yearMonth.substring(0, 4)), parseInt(yearMonth.substring(4, 6)), 0).getDate();
                let endNextMonth = new Date(yearMonth.substring(0, 4) + '/' + yearMonth.substring(4, 6) + '/' + lastDate);
                let date = new Date(startE);
                if (date < endNextMonth) {
                    startE = startE.substring(0, 8) + '/' + lastDate;
                }
            }
        } else { //mode week
            startD = moment(startDate).add(monthChange, 'M').format('YYYY/MM/DD').toString();
            startE = moment(startD).add(6, 'days').format('YYYY/MM/DD').toString();

        }

        let dataResult = {
            startDate: startD,
            endDate: startE
        };

        return dataResult;
    }

    public register() {
        this.showInforDialog = true;
    }

    public closeModal() {
        this.showInforDialog = false;

        this.showConfirmDialog = false;

    }


}