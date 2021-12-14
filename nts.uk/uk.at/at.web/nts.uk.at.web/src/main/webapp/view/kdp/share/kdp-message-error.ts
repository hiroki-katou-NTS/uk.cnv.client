/// <reference path="../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kdp.share {

    export interface MessageParam {
        events?: ClickEvent;
        notiSet: KnockoutObservable<FingerStampSetting>;
        messageNoti: KnockoutObservable<IMessage>;
        viewShow: String;
    }

    const API = {
    };

    const template = `
    <div class="company" data-bind="style: { 'background-color': $component.headOfficeNotice.backGroudColor }">
        <div class="title-company" data-bind="i18n: $component.headOfficeNotice.title,
            style: { 'color': $component.headOfficeNotice.textColor }"></div>
        <span class="text-company" data-bind="i18n: $component.headOfficeNotice.contentMessager,
            style: { 'color': $component.headOfficeNotice.textColor }"></span>
    </div>
    <div data-bind="style: { 'background-color': $component.workplaceNotice.backGroudColor }">
        <div class="workPlace">
            <div class="title">
                <div class="name-title">
                    <div style:"box-sizing: border-box" data-bind="i18n: $component.workplaceNotice.title,
                        style: { 'color': $component.workplaceNotice.textColor}"></div>
                </div>
                <div class="btn-title">
                    <button style="background-color: transparent;" 
                            class="icon" 
                            data-bind="ntsIcon: { no: 160, width: 30, height: 30 }, click: events.registerNoti.click">
                    </button>
                </div>
            </div>
            <div class="content">
                <div class="text-content" data-bind="i18n: $component.workplaceNotice.contentMessager,
                    style: { 'color': $component.workplaceNotice.textColor}"></div>
                    <button class="btn-content" data-bind="ntsIcon: { no: 161, width: 30, height: 30 }, click: events.shoNoti.click">
                    </button>
            <div>
        </div>
    </div>
    <style>
        .kdp-message-error {
            max-width: 700px;
            padding: 10px 10px 0 10px;
        }

        .kdp-message-error .company {
            padding: 5px 3px;
            height: 47px;
            max-height: 47px;
            word-break: break-all;
            text-overflow: ellipsis;
            overflow: hidden;
            box-sizing: border-box;
        }

        .kdp-message-error .company .title-company {
            box-sizing: border-box;
            float: left;
            height: 100px;
        }

        .kdp-message-error .workPlace {
            padding: 5px 3px;
            margin-top: 5px;
            height: 47px;
            max-height: 47px;
            position: relative;
        }

        .kdp-message-error .workPlace .title {
            box-sizing: border-box;
            width: auto;
            height: 65px;
            float: left;
        }

        .kdp-message-error .company .text-company {
            box-sizing: border-box;
            margin-right:40px;
            max-height:47px;
            text-overflow: ellipsis;
            overflow: hidden;
            word-break: break-all;
        }

        .kdp-message-error .workPlace .title .name-title {
            box-sizing: border-box;
        }

        .kdp-message-error .workPlace .title .btn-title {
            position: absolute;
            top: 25px;
        }
        
        .kdp-message-error .workPlace .content {
            box-sizing: border-box;
            position: relative;
            margin-left: 40px;
        }

        .kdp-message-error .workPlace .text-content {
            box-sizing: border-box;
            margin-right:40px;
            max-height:47px;
            text-overflow: ellipsis;
            overflow: hidden;
            word-break: break-all;
        }

        .kdp-message-error .workPlace .btn-content {
            position: absolute;
            top: 0px;
            right: 6px;
            border: none;
            background-color: transparent;
            box-shadow: 0 0px rgb(0 0 0 / 40%);
        }

        .icon {
            border: none;
            background-color: transparent;
            box-shadow: 0 0px rgb(0 0 0 / 40%);
        }
    </style>
    `;

    @component({
        name: 'kdp-message-error',
        template
    })

    class BoxYear extends ko.ViewModel {

        // R1 本部見内容
        headOfficeNotice: Model = new Model(DestinationClassification.ALL);

        // R2 職場メッセージ
        workplaceNotice: Model = new Model(DestinationClassification.WORKPLACE);


        messageNoti: KnockoutObservable<IMessage> = ko.observable();
        notiSet: KnockoutObservable<FingerStampSetting> = ko.observable();

        events!: ClickEvent;


        created(params?: MessageParam) {
            const vm = this;

            if (params) {
                vm.messageNoti = params.messageNoti;

                if (ko.unwrap(params.notiSet)) {
                    vm.notiSet = params.notiSet;
                }
                const { events } = params;

                if (events) {
                    // convert setting event to binding object
                    if (_.isFunction(events.registerNoti)) {
                        const click = events.registerNoti;

                        events.registerNoti = {
                            click
                        } as any;
                    }

                    // convert company event to binding object
                    if (_.isFunction(events.shoNoti)) {
                        const click = events.shoNoti;

                        events.shoNoti = {
                            click
                        } as any;
                    }

                    vm.events = events;
                } else {
                    vm.events = {
                        registerNoti: {
                            click: () => { }
                        } as any,
                        shoNoti: {
                            click: () => { }
                        } as any
                    };
                }
            }

            vm.messageNoti.subscribe(() => {
                vm.reload();
            });
        }

        mounted() {
            const vm = this;

            if (ko.unwrap(vm.messageNoti)) {
                vm.reload();
            }
        }

        reload() {
            const vm = this;

            vm.$blockui('invisible')
                .then(() => {

                    let headOfficeNoticeList = _.filter(
                        ko.unwrap(ko.unwrap(vm.messageNoti)).messageNotices,
                        n => n.targetInformation.destination == DestinationClassification.ALL);
                    let workplaceNoticeList = _.filter(
                        ko.unwrap(ko.unwrap(vm.messageNoti)).messageNotices,
                        n => n.targetInformation.destination == DestinationClassification.WORKPLACE);

                    if (headOfficeNoticeList.length > 0) {

                        if (ko.unwrap(vm.notiSet)) {
                            vm.headOfficeNotice.update(DestinationClassification.ALL,
                                headOfficeNoticeList[0].notificationMessage,
                                ko.unwrap(vm.notiSet).noticeSetDto);
                        } else {
                            vm.headOfficeNotice.update(DestinationClassification.ALL,
                                headOfficeNoticeList[0].notificationMessage);
                        }

                    } else {

                        if (ko.unwrap(vm.notiSet)) {
                            vm.headOfficeNotice.update(DestinationClassification.ALL,
                                '',
                                ko.unwrap(vm.notiSet).noticeSetDto);
                        } else {
                            vm.headOfficeNotice.update(DestinationClassification.ALL,
                                '');
                        }
                    }

                    if (workplaceNoticeList.length > 0) {

                        if (ko.unwrap(vm.notiSet)) {
                            vm.workplaceNotice.update(DestinationClassification.WORKPLACE,
                                workplaceNoticeList[0].notificationMessage,
                                ko.unwrap(vm.notiSet).noticeSetDto);
                        } else {
                            vm.workplaceNotice.update(DestinationClassification.WORKPLACE,
                                workplaceNoticeList[0].notificationMessage);
                        }
                    } else {

                        if (ko.unwrap(vm.notiSet)) {
                            vm.workplaceNotice.update(DestinationClassification.WORKPLACE,
                                '',
                                ko.unwrap(vm.notiSet).noticeSetDto);
                        } else {
                            vm.workplaceNotice.update(DestinationClassification.WORKPLACE,
                                '');
                        }
                    }
                })
                .then(() => {
                    vm.$blockui('clear');
                });
        }
    }

    class Model {
        title: KnockoutObservable<string> = ko.observable('本部より');
        contentMessager: KnockoutObservable<string> = ko.observable('');
        textColor: KnockoutObservable<string> = ko.observable('#E2F0D9');
        backGroudColor: KnockoutObservable<string> = ko.observable('#E2F0D9');

        constructor(type: DestinationClassification) {
            const vm = this;

            vm.update(type)
        }

        public update(type: DestinationClassification, content?: string, setting?: INoticeSet) {
            const vm = this;

            if (type == DestinationClassification.ALL) {
                if (setting) {
                    vm.title(setting.companyTitle + ' ');
                    vm.textColor(setting.comMsgColor.textColor);
                    vm.backGroudColor(setting.comMsgColor.backGroundColor);
                }
            }

            if (type == DestinationClassification.WORKPLACE) {
                if (setting) {

                    vm.title(setting.wkpTitle + ' ');
                    vm.textColor(setting.wkpMsgColor.textColor);
                    vm.backGroudColor(setting.wkpMsgColor.backGroundColor);
                }
            }

            if (content) {
                vm.contentMessager(content);
            } else {
                vm.contentMessager('');
            }
        }
    }

    export interface ClickEvent {
        registerNoti: () => void | {
            click: () => void;
        };
        shoNoti: () => void | {
            click: () => void;
        };
    }

    interface INoticeSet {
        comMsgColor: IColorSetting;
        companyTitle: string;
        personMsgColor: IColorSetting;
        wkpMsgColor: IColorSetting;
        wkpTitle: string;
        displayAtr: number;
    }

    interface IColorSetting {
        textColor: string;
        backGroundColor: string;
    }

    interface IMessage {
        messageNotices: IMessageNotice[];
    }

    interface IMessageNotice {
        creatorID: string;
        inputDate: Date;
        modifiedDate: Date;
        targetInformation: ITargetInformation;
        startDate: Date;
        endDate: Date;
        employeeIdSeen: string[];
        notificationMessage: string;
    }

    interface ITargetInformation {
        targetSIDs: string[];
        targetWpids: string[];
        destination: number | null;
    }

    enum DestinationClassification {
        // 0 全社員
        ALL = 0,
        // 1 職場選択
        WORKPLACE = 1,
        // 2 社員選択
        EMPLOYEE = 2
    }

    interface FingerStampSetting {
        stampResultDisplay: any;
        stampSetting: any;
        noticeSetDto: INoticeSet;
    }
}
