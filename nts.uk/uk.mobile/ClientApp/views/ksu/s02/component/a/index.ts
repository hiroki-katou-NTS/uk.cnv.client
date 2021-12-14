import { component, Prop, Watch } from '@app/core/component';
import { _, Vue, moment } from '@app/provider';
import * as $ from 'jquery';
// import { model } from 'views/new/bundles.js';

@component({
    // name: 'calendarAComponent',
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
    ],
    beforeDestroy() {
        document.body.style.removeProperty('position');
    }
})
export class CalendarAComponent extends Vue {

    @Prop({
        default: () => ({
            dataFromParent: null
        })
    })
    public params!: { dataFromParent: any };
    public clnLst = [];

    private startWork = '';

    public created() { // gioongs contructor    
        let vm = this;
        vm.dataStartPage = vm.params.dataFromParent.data;
        vm.startWork = vm.dataStartPage.startWork;
    }

    @Watch('params.dataFromParent')
    public change(data: any) {
        let self = this;
        this.dataStartPage = data.data;
        self.getData();
        self.updateDataRegister();
        if (data.checkRegister) {
            self.closePopup();
        }

    }

    //A2 : 
    public idCurent: string = '';

    public memoCurent: string = '';

    public yearMonth = moment().format('YYYYMM');

    public dataStartPage: any = null;

    public listDataDisplay: any = [];

    public listShiftMasterInfo: any = [];

    public checked2s: Array<number> = [];

    public showPopup: any = false;

    public slide: any = false;

    public maxDataID: any = 0;

    public showInforDialog: any = false;

    public showMemoArea: any = false;

    public isCurrentMonth: any = true;

    public idFirst = 0;

    public nameListInforCurrent: any = [];

    public startDate: string = '';

    public endDate: string = '';

    public showCheckboxs = true;
    public firstShow = true;

    public screenSmall = true;

    public loadData() {
        let self = this;
        self.getData();
        self.updateDataRegister();

    }

    @Watch('yearMonth')
    public changeYearMonth(yearMonth: any) {
        $($(document.body)[0]).find('td.cell-focus').removeClass('cell-focus');
        if (yearMonth == null) {
            return;
        }
        let self = this;
        self.closePopup();
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
    @Watch('memoCurent')
    public changeMemo(memo: any) {
        let self = this;
        if (memo.length > 100) {

            return;
        }
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        if (dataClick != null) {
            dataClick.workAvailabilityOfOneDayDto.memo = self.memoCurent;
            dataClick.showMemo = (dataClick.workAvailabilityOfOneDayDto.memo != '' && dataClick.workAvailabilityOfOneDayDto.memo != null) ? true : false;
        }
        $('#tbody1row tr  td.cell-focus #memo-area').remove();
        if (memo !== '') {
            $('#tbody1row tr  td.cell-focus #header').append('<i id="memo-area" class="far fa-sticky-note memo-css"></i>');
        }
        self.updateDataRegister();

    }

    public mounted() {
        let self = this;
        let container = document.querySelector('#tbody1row');

        container.addEventListener('touchstart', this.startTouch, false);
        container.addEventListener('touchend', this.endTouch, false);
        container.addEventListener('touchmove', this.moveTouch, false);
        self.loadData();
    }


    public cellFocus(el) {
        let self = this;
        self.showPopup = true;
        self.firstShow = true;
        //close Memo area
        // self.showMemoArea = true;  
        //clear and set color focus
        $($(document.body)[0]).find('td.cell-focus').removeClass('cell-focus');
        let id = $(el).attr('id') != null ? $(el).attr('id') : el.currentTarget.id;
        let tdAddFocusLst = $($(document.body)[0]).find('td#' + id);
        for (let i = 0; i < tdAddFocusLst.length; i++) {
            tdAddFocusLst[i].classList.add('cell-focus');
        }


        // first time to show popup
        if (self.showPopup && !self.slide) {
            window.scrollTo(0, 0);
            let offset = $('#' + el.currentTarget.id).offset();
            let wi = $('.container-fluid').width();
            let offset1 = $('#d0').offset();
            $('#detailPopup').fadeIn().css({ left: offset1.left, top: offset1.top, width: wi });
            // append row
            let trCln = $('#' + el.currentTarget.id).parent()[0].cloneNode(true) as HTMLElement;
            $($($($(document.body)[0]).find('#tbody1row'))[0]).empty().append(trCln);
            let tdAddFocusLst = $($(document.body)[0]).find('td#' + el.currentTarget.id);
            for (let i = 0; i < tdAddFocusLst.length; i++) {
                tdAddFocusLst[i].classList.add('cell-focus');
            }

            this.showMemo();
            if (screen.height < 650) {
                $('#scroll_area').css('height', '29vh');
                self.screenSmall = true;
            } else {
                $('#scroll_area').css('height', '43vh');
                self.screenSmall = false;
            }
            $('#scroll_area').css('overflow-y', 'scroll');
            setTimeout(() => { $('body').css('position', 'fixed'); }, 200);
        }
        self.checked2s = [];
        self.memoCurent = '';
        self.nameListInforCurrent = [];
        let dataClick = _.find(self.listDataDisplay, function (o) {
            let id = el.currentTarget == undefined ? tdAddFocusLst[1].id : el.currentTarget.id;

            return o.id == id;
        });
        if (dataClick != null) {
            self.checked2s = dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList;
            self.memoCurent = dataClick.workAvailabilityOfOneDayDto.memo != null ? dataClick.workAvailabilityOfOneDayDto.memo : '';
            for (let i = 0; i < self.checked2s.length; i++) {
                let dataShift = _.find(self.listShiftMasterInfo, function (o) { return o.shiftMaster.shiftMasterCode == self.checked2s[i]; });
                if (dataShift != null) {
                    self.nameListInforCurrent.push(dataShift);
                }
            }
        }
        this.setCheckedForCheckBox(el, self);
        self.idCurent = el.currentTarget == undefined ? tdAddFocusLst[1].id : el.currentTarget.id;
        this.setMonthDay(el);
        if (!self.isCurrentMonth) {
            $('textArea').attr('disabled', 'disabled');
        }
    }

    public closePopup() {
        let self = this;
        if (self.checkClear) {
            self.checkClear = false;

            return;
        }
        self.updateDataRegister();
        if ($($(document.body)[0]).find('textarea').val() != '') { this.setMemo(); }
        this.showPopup = false;
        this.slide = false;
        let el3 = $($(document.body)[0]).find('#plus-minus');
        this.showMemoArea = false;
        el3.removeClass('fa-minus-circle');
        el3.addClass('fa-plus-circle');
        $('body').css('overflow', 'auto');
        $('body').css('position', '');
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
                    && self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod == 1) { // enum AssignmentMethod == SHIFT(1), // シフト

                    let listShift = self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList;
                    let temp: DataOneDateScreenKsuS02 = {
                        date: self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvailabilityDate,
                        memo: self.listDataDisplay[i].workAvailabilityOfOneDayDto.memo,
                        assignmentMethod: self.listDataDisplay[i].workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod,
                        nameList: listShift,
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

    public detailDisplay(el) {
        let popupClass = $($(document.body)[0]).find('#detailPopup')[0];

        if ($(popupClass).hasClass('hidden-popup')) {
            // visible
            $(popupClass).removeClass('hidden-popup');
            $(popupClass).addClass('visible-popup');

            let offset = $('#' + el.currentTarget.id).offset();
            $('#detailPopup').fadeIn().css({ left: 0, top: 140 });

        } else {
            // hidden
            $(popupClass).removeClass('visible-popup');
            $(popupClass).addClass('hidden-popup');
        }

        //
        let trCln = $('#' + el.currentTarget.id).parent()[0].cloneNode(true) as HTMLElement;
        //let trCln = $($($(document.body)[0]).find('#detailPopup')[0]).removeClass('hidden-popup');
        $($($($(document.body)[0]).find('#tbody1row'))[0]).empty().append(trCln);

    }

    public setDataDisplay(el) {
        let self = this;
        if (el.target.type != 'checkbox' && el.target.classList.value != 'form-check') {

            return;
        }
        if (el.target.classList.value == 'form-check') {
            let d = $(el.target.getElementsByTagName('input'));
            if (d.prop('checked')) {
                _.remove(self.checked2s, function (n) {
                    return n == el.target.getElementsByTagName('input')[0]._value;
                });
                d.prop('checked', false);
            } else {
                self.checked2s.push(el.target.getElementsByTagName('input')[0]._value);
                d.prop('checked', true);
            }
        }

        let items = $($(document.body)[0]).find('input.form-check-input');
        if (items.length == 0) { return; }


        for (let i = 0; i < items.length; i++) {
            let itemss = items[i] as HTMLInputElement;
            if (itemss.checked) {
                //add data
                let cln = $(items[i]).next()[0].cloneNode(true);
                $(cln).removeClass('select-el').css('margin', 'auto');
                $(cln).css('float', 'none');
                this.clnLst.push(cln);
            }
        }
        //
        this.configItem(this.clnLst);


        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        let listShiftResult = [];
        for (let i = 0; i < self.checked2s.length; i++) {
            let data = _.find(self.dataStartPage.listShiftInfor, function (o) { return o.shiftMaster.shiftMasterCode == self.checked2s[i]; });
            if (data != null && data != undefined) {
                listShiftResult.push(self.checked2s[i]);
            }
        }
        self.checked2s = listShiftResult;
        dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList = self.checked2s;

        if (self.checked2s.length >= 0) {
            let nameListInforData = [];
            for (let i = 0; i < self.checked2s.length; i++) {
                let dataShift = _.find(self.listShiftMasterInfo, function (o) { return o.shiftMaster.shiftMasterCode == self.checked2s[i]; });
                if (dataShift != null) {
                    nameListInforData.push(dataShift.shiftMaster);
                }
            }
            dataClick.nameListInfor = nameListInforData;
            dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod = 1; // SHIFT(1), // シフト
        }

        if (dataClick.nameListInfor.length > 0 && self.isCurrentMonth == true) {
            $('textArea').removeAttr('disabled');
        } else {
            self.memoCurent = '';
            $('textArea').attr('disabled', 'disabled');
        }

        let seletedCellLst = $($(document.body)[0]).find('td.cell-focus');

        for (let i = 1; i < seletedCellLst.length; i++) {
            // clear data
            let dataArea = $(seletedCellLst[i]).find('#data-area');
            $(dataArea).empty();
            if (dataClick.nameListInfor.length == 0) {
                let memoArea = $(seletedCellLst[i]).find('#memo-area');
                $(memoArea).empty();
            }
            //$(dataArea).append(clnLst);
            //$(this.clnLst).clone().appendTo($(dataArea));
            if (dataClick.nameListInfor.length > 2) {
                for (let i = 0; i < 2; i++) {
                    let element = document.createElement('span');
                    element.innerHTML = dataClick.nameListInfor[i].shiftMasterName;
                    element.classList.add('form-control');
                    element.classList.add('select-el');
                    element.classList.add('shiftcode');
                    $(element).css('backgroundColor', dataClick.nameListInfor[i].colorSmartphone);
                    $(element).css('color', dataClick.nameListInfor[i].colorText);
                    $(dataArea).append(element);
                }
                let element = document.createElement('span');
                element.innerHTML = this.$i18n('KSUS02_24');
                element.classList.add('font-size-8px');
                // element.classList.add('point-css');
                $(dataArea).append(element);
            } else {
                for (let i = 0; i < dataClick.nameListInfor.length; i++) {
                    let element = document.createElement('span');
                    element.innerHTML = dataClick.nameListInfor[i].shiftMasterName;
                    element.classList.add('form-control');
                    element.classList.add('select-el');
                    element.classList.add('shiftcode');
                    $(element).css('backgroundColor', dataClick.nameListInfor[i].colorSmartphone);
                    $(element).css('color', dataClick.nameListInfor[i].colorText);
                    $(dataArea).append(element);
                }

            }
        }

        self.updateDataRegister();
    }

    public configItem(items) {
    }

    public updateDataRegister() {
        let dataRegister = this.createDataSubmitWorkRequestCmd();
        this.$emit('passDataToParent', dataRegister);
    }
    private checkClear = false;
    public clearAll() {
        let self = this;
        self.checkClear = true;
        self.idCurent;
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        self.checked2s = [];
        self.memoCurent = '';
        if (dataClick != null) {
            dataClick.workAvailabilityOfOneDayDto.memo = null;
            dataClick.showMemo = (dataClick.workAvailabilityOfOneDayDto.memo != '' && dataClick.workAvailabilityOfOneDayDto.memo != null) ? true : false;
            dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList = [];
            dataClick.nameListInfor = [];
            dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.assignmentMethod = null;
        }
        // clear item in cell of calendar
        let itemsInCell = $($(document.body)[0]).find('td.cell-focus');
        if (itemsInCell[1] != undefined) {
            let dataArea = $(itemsInCell[1]).find('#data-area');
            let memoArea = $(itemsInCell[1]).find('#memo-area');
            $(dataArea).empty();
            $(memoArea).empty();
        }
        // clear all checked 
        $('textArea').attr('disabled', 'disabled');
        $('textArea').val('');
        this.clearChecked();
        self.updateDataRegister();
    }

    public clearChecked() {
        let items = $($(document.body)[0]).find('input.form-check-input');
        for (let i = 0; i < items.length; i++) {
            let itemss = items[i] as HTMLInputElement;
            if (itemss.checked) {
                //add data
                itemss.checked = false;
            }
        }
    }

    public setCheckedForCheckBox(el, self) {

        if (self.checked2s.length == 0) {
            $('textArea').attr('disabled', 'disabled');
            $('textArea').val('');

            return;
        } else {
            $('textArea').removeAttr('disabled');
            if (el.childElementCount == 3) { $('textArea').val('メモがありました。'); }
        }
    }

    public setMonthDay(el) {
        let self = this;
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == self.idCurent; });
        let text = parseInt(dataClick.workAvailabilityOfOneDayDto.workAvailabilityDate.substring(5, 7)) + '月'
            + parseInt(dataClick.workAvailabilityOfOneDayDto.workAvailabilityDate.substring(8, 10)) + '日（' + dataClick.dayOfWeek + '）';
        $($(document.body)[0]).find('span#monthday')[0].innerHTML = text;
    }

    // private checkEndTouch = true;
    public getNextBackWeek(el) {
        let id = +$($($(document.body)[0]).find('td.cell-focus')[0]).attr('id').slice(1) + el;
        let curent = +$($($(document.body)[0]).find('td.cell-focus')[0]).attr('id').slice(1);

        // if (id <= -1 || id > (this.maxDataID + 7)) { 
        //     this.checkEndTouch = true;

        //     return;
        // }
        if (this.dataStartPage.shiftWorkUnit == 0) {
            if (el > 0) {
                if (curent > 6) {
                    id = curent;
                } else {
                    id = this.endModeWeek - 1;
                }
            } else {
                if (curent < 6) {
                    id = curent;
                } else {
                    id = this.idFirst;
                }

            }
        }

        let newTr, newFocusCell = null;
        if (id < this.idFirst) {
            newTr = $($(document.body)[0]).find('#d1').parent()[0].cloneNode(true) as HTMLElement;
            newFocusCell = $($(document.body)[0]).find('#d' + this.idFirst)[0];
        } else if (id >= this.maxDataID) {
            newTr = $($(document.body)[0]).find('#d' + (this.maxDataID - 1)).parent()[0].cloneNode(true) as HTMLElement;
            let newTrs = $(newTr).find('#' + $($($(document.body)[0]).find('td.cell-focus')[0]).attr('id'));
            if (newTrs.length == 0) {
                newFocusCell = $($(document.body)[0]).find('#d' + (this.maxDataID - 1))[0];
            } else {
                // this.checkEndTouch = true;

                return;
            }
        } else {
            newTr = $($(document.body)[0]).find('#d' + id).parent()[0].cloneNode(true) as HTMLElement;
            newFocusCell = $($(document.body)[0]).find('#d' + id)[0];
        }

        $($($($(document.body)[0]).find('#tbody1row'))[0]).empty().append(newTr);
        //$($(newTr).find('small')).addClass('topCell');
        this.cellFocus(newFocusCell);
    }

    public register() {
        let self = this;
        self.showInforDialog = true;
    }

    public closeModal() {
        this.showInforDialog = false;
    }

    public openNextMonth() {
        let self = this;
        if (self.yearMonth != null) {
            self.startDate = moment(self.startDate).add(1, 'M').format('YYYY/MM/DD');
            self.endDate = moment(self.endDate).add(1, 'M').format('YYYY/MM/DD');
            self.yearMonth = moment(self.yearMonth, 'YYYYMM').add(1, 'M').format('YYYYMM');
        }


    }
    public openPrevMonth() {
        let self = this;
        if (self.yearMonth != null) {
            self.startDate = moment(self.startDate).add(-1, 'M').format('YYYY/MM/DD');
            self.endDate = moment(self.endDate).add(-1, 'M').format('YYYY/MM/DD');
            self.yearMonth = moment(self.yearMonth, 'YYYYMM').add(-1, 'M').format('YYYYMM');
        }

    }

    public showMemo() {
        let self = this;
        let el3 = $($(document.body)[0]).find('#plus-minus');
        if (!self.isCurrentMonth) {
            self.showMemoArea = true;
            self.showCheckboxs = true;
        } else {
            if (self.firstShow) {
                self.showCheckboxs = true;
                self.showMemoArea = false;
                self.firstShow = false;

                return;
            }
            if (!self.showMemoArea) {
                self.showMemoArea = true;
                self.showCheckboxs = false;
                el3.removeClass('fa-plus-circle');
                el3.addClass('fa-minus-circle');
            } else {
                self.showMemoArea = false;
                self.showCheckboxs = true;
                el3.removeClass('fa-minus-circle');
                el3.addClass('fa-plus-circle');
            }
        }

    }

    public showCard1() {
        let el3 = $($(document.body)[0]).find('#plus-minus');
        if (!this.isCurrentMonth) {
            this.showCheckboxs = true;
        } else {
            if (this.showCheckboxs) {
                this.showCheckboxs = false;
                this.showMemoArea = true;
                el3.removeClass('fa-plus-circle');
                el3.addClass('fa-minus-circle');
            } else {
                this.showCheckboxs = true;
                this.showMemoArea = false;
                el3.removeClass('fa-minus-circle');
                el3.addClass('fa-plus-circle');
            }
        }
    }

    public setMemo() {
    }

    /**
     * Convert a template string into HTML DOM nodes
     * @param  {String} str The template string
     * @return {Node}       The template HTML
     */
    public stringToHTML = function (str) {
        let parser = new DOMParser();
        let doc = parser.parseFromString(str, 'text/html');

        return doc.body;
    };


    // Swipe Up / Down / Left / Right
    public initialX = null;
    public initialY = null;

    public startTouch(e) {
        // if (this.dataStartPage.shiftWorkUnit == 0) {
        //     return;
        // }
        this.initialX = e.touches[0].clientX;
        this.initialY = e.touches[0].clientY;

    }

    public endTouch(e) {
        let self = this;
        // if (self.checkEndTouch) {
        //     self.checkEndTouch = false;

        //     return;
        // }
        this.initialX = e.changedTouches[0].clientX;
        this.initialY = e.changedTouches[0].clientY;
        // let classList = e.target.id != '' ? e.target.classList : $(e.currentTarget).find('td.cell-focus')[0].classList;
        if (e.changedTouches[0].target.classList.contains('uk-bg-white-smoke')
            || (self.isCurrentMonth && e.changedTouches[0].target.closest('td').classList.contains('uk-bg-silver'))) { return; }
        //clear and set color focus
        $($(document.body)[0]).find('td.cell-focus').removeClass('cell-focus');
        let id = e.changedTouches[0].target.id != '' && e.changedTouches[0].target.id != 'memo-area' && e.changedTouches[0].target.id != 'header' ? e.changedTouches[0].target.id : e.changedTouches[0].target.closest('td').id;
        let tdAddFocusLst = $($(document.body)[0]).find('td#' + id);
        for (let i = 0; i < tdAddFocusLst.length; i++) {
            tdAddFocusLst[i].classList.add('cell-focus');
        }
        let dataClick = _.find(self.listDataDisplay, function (o) { return o.id == e.changedTouches[0].target.closest('td').id; });
        self.memoCurent = '';
        self.nameListInforCurrent = [];
        if (dataClick != null) {
            self.checked2s = dataClick.workAvailabilityOfOneDayDto.workAvaiByHolidayDto.nameList;
            self.memoCurent = dataClick.workAvailabilityOfOneDayDto.memo != null ? dataClick.workAvailabilityOfOneDayDto.memo : '';
            for (let i = 0; i < self.checked2s.length; i++) {
                let dataShift = _.find(self.listShiftMasterInfo, function (o) { return o.shiftMaster.shiftMasterCode == self.checked2s[i]; });
                if (dataShift != null) {
                    self.nameListInforCurrent.push(dataShift);
                }
            }
        }
        self.idCurent = e.changedTouches[0].target.closest('td').id;
        if (!self.isCurrentMonth) {
            $('textArea').attr('disabled', 'disabled');
        } else {
            if (dataClick.nameListInfor.length > 0) {
                $('textArea').removeAttr('disabled');
            } else {
                self.memoCurent = '';
                $('textArea').attr('disabled', 'disabled');
            }
        }
        this.setMonthDay(e.changedTouches[0]);
    }

    public moveTouch(e) {
        if (this.initialX === null) {
            return;
        }

        if (this.initialY === null) {
            return;
        }
        // if (e.target.classList.contains('uk-bg-white-smoke')) { return; }

        this.slide = true;
        let currentX = e.touches[0].clientX;
        let currentY = e.touches[0].clientY;

        let diffX = this.initialX - currentX;
        let diffY = this.initialY - currentY;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            // sliding horizontally
            if (diffX > 0) {
                // swiped left
                this.getNextBackWeek(7);
            } else {
                // swiped right
                this.getNextBackWeek(-7);
            }
        }

        this.initialX = null;
        this.initialY = null;

        e.preventDefault();
    }

    private endModeWeek = 0;

    public getData() {
        let self = this;
        if (self.dataStartPage == null || self.dataStartPage.startWork == undefined) {
            return;
        }
        if (!self.isCurrentMonth) {
            $('#only-shift').addClass('disabled');
            $('textArea').attr('disabled', 'disabled');
            $('#plus-minus').css('visibility', 'hidden');
        } else {
            $('#only-shift').removeClass('disabled');
            $('textArea').removeAttr('disabled');
            $('#plus-minus').css('visibility', 'visible');
        }
        self.listShiftMasterInfo = self.dataStartPage.listShiftInforData;
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
        self.idFirst = numberStartDisplay;
        for (let date = startDate; date <= endDate; date.setDate(date.getDate() + 1)) {
            let dataDate = _.find(self.dataStartPage.displayInfoListWorkOneDay, function (o) { return o.workAvailabilityDate == moment(date).format('YYYY/MM/DD'); });

            if (dataDate == null) {
                dataDate = self.getDateInfoDefault(moment(date).format('YYYY/MM/DD'));
            }
            if (dataDate.workAvaiByHolidayDto.assignmentMethod != 1) {
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
            self.listShiftMasterInfo = self.dataStartPage.listShiftInfor;
            let nameListInforData = [];
            for (let i = 0; i < dataDate.workAvaiByHolidayDto.nameList.length; i++) {
                let dataShift = _.find(self.listShiftMasterInfo, function (o) { return o.shiftMaster.shiftMasterCode == dataDate.workAvaiByHolidayDto.nameList[i]; });
                if (dataShift != null) {
                    nameListInforData.push(dataShift.shiftMaster);
                } else {
                    nameListInforData.push(self.getDefaultShiftMater());
                }
            }

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
        //shiftWorkUnit 1：一ヶ月間	　　　0：一週間"
        if (self.dataStartPage.shiftWorkUnit == 0) {
            self.endModeWeek = listData.length;
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
        this.maxDataID = listData.length;
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
        // console.log(self.listShiftMasterInfo);
    }

    public getDefaultShiftMater() {
        let shiftMasterDf: ShiftMasterDto = {
            shiftMasterName: this.$i18n('KSUS02_22'),
            shiftMasterCode: null,
            color: '#999',
            colorSmartphone: '#999',
            remark: 'remark',
            workTypeCd: '009',
            workTypeName: 'worktype1',
            workTimeCd: '009',
            workTimeName: 'workTime1',
            workTime1: '7:00～16:00',
            workTime2: '17:00～22:00',
            colorText: 'black'
        };

        return shiftMasterDf;
    }


    /**
     * getDateInfoDefault
     */
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
}
export interface SubmitWorkRequestCmd {
    listData: Array<DataOneDateScreenKsuS02>;
    startPeriod: string;
    endPeriod: string;
}
export interface DataOneDateScreenKsuS02 {
    //年月日
    date: string;
    //勤務希望のメモ
    memo: string;
    //勤務希望の指定方法
    assignmentMethod: number;
    //List<シフトマスタコード>
    nameList: Array<string>;
    //List<時間帯>
    timeZoneList: Array<TimeSpanForCalcSharedDto>;
}
interface TimeSpanForCalcSharedDto {
    startTime: number;
    endTime: number;
}



interface IStampPageLayoutDto {
    /** ページNO */
    pageNo: number;

    /** ページ名 */
    stampPageName: string;

    /** ページコメント */
    stampPageComment: number;

    /** ボタン配置タイプ */
    buttonLayoutType: number;

    /** ボタン詳細設定リスト */
    lstButtonSet: Array<number>;
}

export interface DataDisplay {
    id: string;

    numberDisplay: number;

    dayOfWeek: string;

    disable: boolean;

    dateDisplay: string;

    workAvailabilityOfOneDayDto: WorkAvailabilityOfOneDayDto;

    showMemo: boolean;

    nameListInfor: Array<ShiftMasterDto>;

    canUpdateCell: boolean;
}



//最初情報dto
export interface FirstInformationDto {
    specifyWorkPre: number;//勤務希望の指定方法  0：休日　1：シフト　

    shiftWorkUnit: number;//シフト勤務単位

    deadlineForWork: string;//勤務希望の締切日

    startWork: string;//勤務希望の期間.start

    endWork: string;//勤務希望の期間.end

    displayInfoListWorkOneDay: Array<WorkAvailabilityOfOneDayDto>;//一日分の勤務希望の表示情報リスト

    listShiftInfor: Array<ShiftMasterAndWorkInfoScheTime>;//シフト情報リスト

    listDateIsHoliday: Array<string>;

}



//一日分の勤務希望の表示情報 Dto
export interface WorkAvailabilityOfOneDayDto {
    employeeId: string;//社員ID
    workAvailabilityDate: string;//希望日
    memo: string;//メモ
    workAvaiByHolidayDto: WorkAvailabilityDisplayInfoDto; // 勤務希望
}

//List<シフトマスタ, 勤務情報と補正済み所定時間帯>
export interface ShiftMasterAndWorkInfoScheTime {
    shiftMaster: ShiftMasterDto;//シフトマスタCode
    workInfoAndScheTime: WorkInfoAndScheTime;
    workStyle: number;
}

//シフトマスタDto
export interface ShiftMasterDto {
    shiftMasterName: string;//名称
    shiftMasterCode: string; //コード
    color: string;//色
    colorSmartphone: string; //スマホ表示用の色
    remark: string;//備考
    workTypeCd: string;
    workTypeName: string;
    workTimeCd: string;
    workTimeName: string;
    workTime1: string;
    workTime2: string;
    colorText: string;
}
export interface WorkInfoAndScheTime {
    workType: WorkTypeDto;//勤務種類
    workTimeSettingDto: WorkTimeSettingDto; //就業時間帯 
    timeZones: Array<TimeZoneDto>;//補正済み所定時間帯
}

//package nts.uk.ctx.at.shared.app.find.worktype;
//勤務種類 Dto
export interface WorkTypeDto {
    /* 会社ID */
    companyId: string;
    /* 勤務種類コード */
    workTypeCode: string;
    /* 勤務種類名称 */
    name: string;
    /* 勤務種類略名 */
    abbreviationName: string;
    /* 勤務種類記号名 */
    symbolicName: string;
    /* 廃止区分 */
    abolishAtr: number;
    /* 勤務種類備考 */
    memo: string;
    /* 勤務の単位 */
    workAtr: number;
    /* 1日 */
    oneDayCls: number;
    /* 午前 */
    morningCls: number;
    /* 午後 */
    afternoonCls: number;
    /* 出勤率の計算方法 */
    calculatorMethod: number;

    dispOrder: number;
}

//nts.uk.ctx.at.shared.app.find.worktime.worktimeset.dto
//補正済み所定時間帯 Dto
export interface WorkTimeSettingDto {
    /** The company id. */
    companyId: string;

    /** The worktime code. */
    worktimeCode: string;

    /** The work time division. */
    // public WorkTimeDivisionDto workTimeDivision;

    /** The is abolish. */
    isAbolish: boolean;

    /** The color code. */
    colorCode: string;

    /** The work time display name. */
    // public WorkTimeDisplayNameDto workTimeDisplayName;

    /** The memo. */
    memo: string;

    /** The note. */
    note: string;
}

//TimeZone
export interface TimeZoneDto {
    start: number;//開始時刻 
    end: number;//終了時刻
}


//勤務希望の表示情報 Dto
export interface WorkAvailabilityDisplayInfoDto {
    assignmentMethod: number; //AssignmentMethod enum
    nameList: Array<string>;
    timeZoneList: Array<TimeSpanForCalcDto>;
}

//計算時間帯 Dto
export interface TimeSpanForCalcDto {
    startTime: number;
    endTime: number;
}






