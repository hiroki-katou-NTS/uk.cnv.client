/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

const kDP002RequestUrl = {

    getAllStampingResult: "at/record/workrecord/stamp/management/getAllStampingResult/",
    getInfo: 'ctx/sys/auth/grant/rolesetperson/getempinfo/',
    NOTIFICATION_STAMP: 'at/record/stamp/notification_by_stamp',
    SETTING_NIKONIKO: 'at/record/stamp/setting_emoji_stamp',
    SEND_EMOJI: 'at/record/stamp/regis_emotional_state',
    GET_SETTING: 'at/record/stamp/settingNoti',
    WORKPLACE_INFO: "screen/at/kdp003/workplace-info",
    FINGER_STAMP_SETTING: 'at/record/stamp/finger/get-finger-stamp-setting'
}

interface TimeClock {
    tick: number;
    now: KnockoutObservable<Date>;
    style: KnockoutObservable<string>;
    displayTime: KnockoutObservable<number>;
}

const initTime = (): TimeClock => ({
    tick: -1,
    now: ko.observable(new Date()),
    style: ko.observable(''),
    displayTime: ko.observable(10)
});

@bean()
class KDP002BViewModel extends ko.ViewModel {

    modeNikoNiko: KnockoutObservable<boolean | null> = ko.observable(null);
    modeZeroTime: KnockoutObservable<boolean | null> = ko.observable(true);
    // B2_2
    employeeCodeName: KnockoutObservable<string> = ko.observable("基本給");
    // B3_2
    dayName: KnockoutObservable<string> = ko.observable("基本給");
    // B3_3
    timeName: KnockoutObservable<string> = ko.observable("基本給");
    // G4_2
    checkHandName: KnockoutObservable<string> = ko.observable("基本給");
    // G5_2
    numberName: KnockoutObservable<string> = ko.observable("基本給");
    // G6_2
    laceName: KnockoutObservable<string> = ko.observable("基本給");

    workPlace: KnockoutObservable<string> = ko.observable('');

    time: TimeClock = initTime();

    items: KnockoutObservableArray<ItemModels> = ko.observableArray([]);
    columns2: KnockoutObservableArray<any> = ko.observableArray([
        { headerText: "id", key: 'id', width: 100, hidden: true },
        { headerText: "<div style='text-align: center;'>" + nts.uk.resource.getText("KDP002_45") + "</div>", key: 'stampDate', width: 135 },
        { headerText: "<div style='text-align: center;'>" + nts.uk.resource.getText("KDP002_46") + "</div>", key: 'stampHowAndTime', width: 90 },
        { headerText: "<div style='text-align: center;'>" + nts.uk.resource.getText("KDP002_47") + "</div>", key: 'timeStampType', width: 180 }
    ]);
    currentCode: KnockoutObservable<any> = ko.observable();
    currentCodeList: KnockoutObservableArray<any>;

    listStampRecord: KnockoutObservableArray<any> = ko.observableArray([]);
    currentDate: KnockoutObservable<string> = ko.observable(moment(new Date()).add(-3, 'days').format("YYYY/MM/DD") + " ～ " + moment(new Date()).format("YYYY/MM/DD"));
    currentStampData: KnockoutObservable<any> = ko.observable({ stampDate: null, stampTime: null, stampArtName: null, cardNumberSupport: null, workLocationCD: null });
    resultDisplayTime: KnockoutObservable<number> = ko.observable(0);
    disableResultDisplayTime: KnockoutObservable<boolean> = ko.observable(true);
    interval: KnockoutObservable<number> = ko.observable(0);
    infoEmpFromScreenA: any;
    notificationStamp: KnockoutObservableArray<IMsgNotices> = ko.observableArray([]);
    modeShowPointNoti: KnockoutObservable<boolean | null> = ko.observable(null);
    showBtnNoti: KnockoutObservable<boolean | null> = ko.observable(null);
    activeViewU: KnockoutObservable<boolean> = ko.observable(false);
    noticeSetting: KnockoutObservable<INoticeSet> = ko.observable(null);
	stampTime: KnockoutObservable<string> = ko.observable('');

    constructor() {
        super();
    }

    created(params: any) {
        const vm = this;
		vm.stampTime(params.stampTime);
        vm.$window.shared("resultDisplayTime").done(displayTime => {
            vm.resultDisplayTime(displayTime);
	
            vm.$window.shared("infoEmpToScreenB").done(infoEmp => {

                vm.infoEmpFromScreenA = infoEmp;

                vm.disableResultDisplayTime(vm.resultDisplayTime() > 0 ? true : false);

                vm.startPage();

                vm.getWorkPlacwName(infoEmp.workPlaceId);

                // if (infoEmp.workLocationName) {
                //     vm.workPlace(infoEmp.workLocationName);
                // }

            });
            vm.$window.shared("screenB").done((nameScreen: any) => {
                switch (nameScreen.screen) {
                    case 'KDP001':
                    case 'KDP002':
                        // vm.showBtnNoti(false);
                        break
                    case 'KDP003':
                    case 'KDP004':
                    case 'KDP005':
                        vm.getNotification();
                        break
                }
            });
        });;

        vm.$ajax(kDP002RequestUrl.SETTING_NIKONIKO)
            .then((data: boolean) => {
                vm.modeNikoNiko(data);
            });

        vm.resultDisplayTime.subscribe(() => {
            if (!ko.unwrap(vm.modeNikoNiko)) {
                if (ko.unwrap(vm.resultDisplayTime) < 0) {
                    vm.closeDialog();
                }
            }
        });

        // vm.showBtnNoti.subscribe(() => {
        //     vm.settingSizeView();
        // })
        vm.workPlace.subscribe(() => {
            vm.settingSizeView();
        });

        vm.$ajax(kDP002RequestUrl.FINGER_STAMP_SETTING)
            .then((data: any) => {
                vm.noticeSetting(data.noticeSetDto);
            });
    }

    mounted() {
        const vm = this;
        setTimeout(() => {
            if (ko.unwrap(vm.modeNikoNiko)) {
                $(document).ready(function () {
                    $('.btn-happy').focus();
                });
            } else {
                $(document).ready(function () {
                    $('#close-button').focus();
                });
            }
            vm.settingSizeView();
        }, 300);

        vm.showBtnNoti.subscribe(() => {
            vm.settingSizeView();
        });

        vm.modeNikoNiko.subscribe(() => {
            vm.settingSizeView();
        });

        if (vm.modeNikoNiko) {
            if (ko.unwrap(vm.resultDisplayTime) == 0) {
                vm.modeZeroTime(false);
            }
        }

        vm.showBtnNoti.valueHasMutated();
    }

    getWorkPlacwName(workPlaceId: string) {
        const vm = new ko.ViewModel();
        const self = this;

        const param = { sid: self.infoEmpFromScreenA.employeeId, workPlaceIds: [workPlaceId] };
        vm.$ajax(kDP002RequestUrl.WORKPLACE_INFO, param)
            .then((data: any) => {

                if (data) {
                    if (data.workPlaceInfo[0].displayName === 'コード削除済') {
                        self.workPlace('');
                    } else {
                        self.workPlace(data.workPlaceInfo[0].displayName);
                    }
                }
            })
    }

    settingSizeView() {
        const vm = this;

        if (!ko.unwrap(vm.showBtnNoti)) {
            if (!ko.unwrap(vm.modeNikoNiko)) {
                if (vm.workPlace() != "") {
                    if (ko.unwrap(vm.modeZeroTime)) {
                        vm.$window.size(555, 470);
                    }else {
                        vm.$window.size(525, 470);
                    }
                } else {
                    if (ko.unwrap(vm.modeZeroTime)) {
                        vm.$window.size(530, 470);
                    }else {
                        vm.$window.size(500, 470);
                    }
                }
            } else {
                if (vm.workPlace() != "") {
                    vm.$window.size(565, 470);
                } else {
                    vm.$window.size(543, 470);
                }
            }
        } else {
            if (!ko.unwrap(vm.modeNikoNiko)) {
                if (vm.workPlace() != "") {
                    vm.$window.size(590, 470);
                } else {
                    vm.$window.size(568, 470);
                }
            } else {
                if (vm.workPlace() != "") {
                    vm.$window.size(610, 470);
                } else {
                    vm.$window.size(588, 470);
                }
            }
        }
    }

    startPage(): JQueryPromise<any> {
        const vm = this,
            dfd = $.Deferred();
        let dfdGetAllStampingResult = vm.getAllStampingResult();
        let dfdGetEmpInfo = vm.getEmpInfo();
        $.when(dfdGetAllStampingResult, dfdGetEmpInfo).done((dfdGetAllStampingResultData, dfdGetEmpInfoData) => {
            if (vm.resultDisplayTime() > 0) {
                setInterval(() => {
                    if (!ko.unwrap(vm.activeViewU)) {
                        vm.resultDisplayTime(vm.resultDisplayTime() - 1);
                    }
                }, 1000);
            }
            dfd.resolve();
        });

        return dfd.promise();
    }

    getDataById(id: any) {
        let self = this;
        for (let j = 0; j < _.size(self.items()); j++) {
            if (self.items()[j].id == id) {
                for (let i = 0; i < _.size(self.listStampRecord()); i++) {
                    if (self.listStampRecord()[i].stampDate == self.items()[j].date && self.listStampRecord()[i].stampTime == self.items()[j].time) {
                        self.currentStampData(self.listStampRecord()[i]);
                        if (ko.unwrap(self.modeNikoNiko)) {
                            self.setModeNikoNikoByStampType(ko.unwrap(self.currentStampData).correctTimeStampValue);
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    getAllStampingResult(): JQueryPromise<any> {
        const vm = this;
        let dfd = $.Deferred();
        let sid = vm.infoEmpFromScreenA.employeeId;

        vm.$ajax("at", kDP002RequestUrl.getAllStampingResult + sid).then(function (data) {

            if (data && data.length > 0) {
                if (ko.unwrap(vm.workPlace) === '') {
                    vm.workPlace(data[0].workPlaceName);
                }
            }

            _.forEach(data, (a) => {
                let items = _.orderBy(a.stampDataOfEmployeesDto.stampRecords, ['stampTimeWithSec'], ['desc']);
                _.forEach(items, (sr) => {
                    vm.listStampRecord.push(sr);
                });
            });
            if (_.size(vm.listStampRecord()) > 0) {
                vm.laceName(data[0].workPlaceName);
                vm.listStampRecord(_.orderBy(vm.listStampRecord(), ['stampTimeWithSec'], ['desc']));
                let items = vm.items();
                _.forEach(vm.listStampRecord(), (sr) => {

                    let changeClockArtDisplay = vm.getTextAlign(sr);

                    let dateDisplay = nts.uk.time.applyFormat("Short_YMDW", sr.stampDate);
                    if (moment(sr.stampDate).day() == 6) {
                        dateDisplay = "<span class='color-schedule-saturday' >" + dateDisplay + "</span>";
                        sr.stampDate = "<span class='color-schedule-saturday' >" + sr.stampDate + "</span>";
                    } else if (moment(sr.stampDate).day() == 0) {
                        dateDisplay = "<span class='color-schedule-sunday'>" + dateDisplay + "</span>";
                        sr.stampDate = "<span class='color-schedule-sunday'>" + sr.stampDate + "</span>";
                    }
                    items.push(new ItemModels(
                        dateDisplay,
                        "<div class='inline-bl'>" + sr.stampHow + "</div>" + sr.stampTime,
                        changeClockArtDisplay,
                        sr.stampDate,
                        sr.stampTime
                    ));

                });

                vm.items(items);
                vm.getDataById(vm.items()[0].id);
                dfd.resolve();
            } else {
                nts.uk.ui.dialog.alertError("Stamp Data Not Found!!!").then(() => {
                    nts.uk.ui.windows.close();
                });

            }
        });
        return dfd.promise();
    }

    getTextAlign(sr: any): string {

        let value = sr.buttonValueType;
        if (ButtonType.GOING_TO_WORK == value || ButtonType.RESERVATION_SYSTEM == value) {

            return `<div class='full-width' style='text-align: left' >` + sr.stampArtName + '</div>';

        }

        if (ButtonType.WORKING_OUT == value) {

            return `<div class='full-width' style='text-align: right'>` + sr.stampArtName + '</div>';

        }

        return sr.stampArtName ? `<div class='full-width' style='text-align: center'>` + sr.stampArtName + '</div>' : '';

    }

    setModeNikoNikoByStampType(param: number) {
        const vm = this;
        switch (param) {
            case 1:
                vm.modeNikoNiko(true);
                break
            case 2:
                vm.modeNikoNiko(true);
                break
            case 3:
                vm.modeNikoNiko(true);
                break
            case 4:
                vm.modeNikoNiko(true);
                break
            case 16:
                vm.modeNikoNiko(true);
                break
            default:
                vm.modeNikoNiko(false);
                break;
        }
    }

    getNotification() {
        const vm = this;
        const param = {
            startDate: vm.$date.now(),
            endDate: vm.$date.now(),
            sid: vm.infoEmpFromScreenA.employeeId
        }

        vm.$blockui('invisible')
            .then(() => {
                vm.$ajax(kDP002RequestUrl.GET_SETTING)
                    .then((data: boolean) => {
                        if (data) {
                            vm.$ajax(kDP002RequestUrl.NOTIFICATION_STAMP, param)
                                .done((data: IMsgNotices[]) => {

                                    vm.notificationStamp(data);

                                    var isShow = 0;
                                    var isShowPoint = 0;
                                    _.forEach(data, ((value) => {
                                        _.forEach(value, ((value1) => {
                                            if (value1.message.targetInformation.destination == 2) {
                                                isShow++;
                                            }
                                            if (value1.message.targetInformation.destination == 2 && value1.flag) {
                                                isShowPoint++;
                                            }
                                        }));
                                    }));

                                    if (isShow > 0) {
                                        vm.showBtnNoti(true);

                                        if (isShowPoint > 0) {
                                            vm.modeShowPointNoti(true);
                                        } else {
                                            vm.modeShowPointNoti(false);
                                        }
                                    } else {
                                        vm.showBtnNoti(false);
                                    }
                                });
                        } else {
                            vm.showBtnNoti(false);
                        }
                    })
            })
            .then(() => {
                vm.showBtnNoti.valueHasMutated();
            })
            .always(() => {
                vm.$blockui('clear');
            });

    }

    getEmpInfo(): JQueryPromise<any> {
        const vm = this;
        let dfd = $.Deferred();
        let employeeId = vm.infoEmpFromScreenA.employeeId;
        vm.$ajax('com', kDP002RequestUrl.getInfo + employeeId).done(function (data) {
            vm.employeeCodeName(data.employeeCode + " " + data.personalName);
            dfd.resolve();
        });
        return dfd.promise();
    }

    openDialogU() {
        const vm = this;
        const params = { sid: vm.infoEmpFromScreenA.employeeId, data: ko.unwrap(vm.notificationStamp), setting: ko.unwrap(vm.noticeSetting) };
        vm.activeViewU(true);
        vm.$window
            .modal('/view/kdp/002/u/index.xhtml', params)
            .then(() => {
                vm.activeViewU(false);
                vm.modeShowPointNoti(false);
                vm.getNotification();
            });
    }

    public closeDialog(): void {
        nts.uk.ui.windows.close();
    }

    weary() {
        const vm = this;
        vm.sendStatusEmojs(Emoji.WEARY);
    }

    sad() {
        const vm = this;
        vm.sendStatusEmojs(Emoji.SAD);
    }

    average() {
        const vm = this;
        vm.sendStatusEmojs(Emoji.AVERAGE);
    }

    good() {
        const vm = this;
        vm.sendStatusEmojs(Emoji.GOOD);
    }

    happy() {
        const vm = this;
        vm.sendStatusEmojs(Emoji.HAPPY);
    }

    sendStatusEmojs(param: Emoji) {
        const vm = this;
        const input = {
            sid: vm.infoEmpFromScreenA.employeeId,
            emoji: param.valueOf(),
            date: new Date()
        }

        vm.$ajax(kDP002RequestUrl.SEND_EMOJI, input)
            .always(() => {
                vm.$window.close();
            });
    }

}

class ItemModels {
    id: string;
    stampDate: string;
    stampHowAndTime: string;
    timeStampType: string;
    date: string;
    time: string
    constructor(stampDate: string, stampHowAndTime: string, timeStampType: string, date: string, time: string) {
        this.id = nts.uk.util.randomId();
        this.stampDate = stampDate;
        this.stampHowAndTime = stampHowAndTime;
        this.timeStampType = timeStampType;
        this.date = date;
        this.time = time;
    }
}

interface IMsgNotices {
    creator: string;
    flag: boolean;
    message: IEmployeeIdSeen;
}

interface IEmployeeIdSeen {
    endDate: string,
    inputDate: Date
    modifiedDate: Date
    notificationMessage: string
    startDate: Date
    targetInformation: ITargetInformation
    employeeIdSeen: string[];
}

interface ITargetInformation {
    destination: string;
    targetSIDs: string;
    targetWpids: string[];
}

enum ButtonType {
    // 系

    GOING_TO_WORK = 1,
    // 系

    WORKING_OUT = 2,
    // "外出系"

    GO_OUT = 3,
    // 戻り系

    RETURN = 4,
    // 予約系

    RESERVATION_SYSTEM = 5
}

enum Emoji {

    // どんより
    WEARY = 0,

    // ゆううつ
    SAD = 1,

    // 普通
    AVERAGE = 2,

    // ぼちぼち
    GOOD = 3,

    // いい感じ
    HAPPY = 4
}

interface INoticeSet {
    personMsgColor: IColorSettingDto; //個人メッセージ色
}

interface IColorSettingDto {
    textColor: string; //文字色
    backGroundColor: string //背景色
}