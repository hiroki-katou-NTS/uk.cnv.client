/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.cmm048.a {

  const API = {
    find: "query/cmm048userinformation/find",
    updateEmployeeContact: "ctx/bs/employee/data/management/contact/update",
    updatePersonInformation: "ctx/bs/person/personal/information/update",
    changeOwnLoginPassword: "ctx/sys/gateway/login/password/userpassword/changeOwn"
  };

  const ID_AVATAR_CHANGE = 'avatar-change';

  @bean()
  export class ViewModel extends ko.ViewModel {

    //A
    ////A4
    avatarFileId: KnockoutObservable<string> = ko.observable('');
    ////A5
    businessName: KnockoutObservable<string> = ko.observable('');
    ////A6
    empCode: KnockoutObservable<string> = ko.observable('');
    wkpDisplayName: KnockoutObservable<string> = ko.observable('');
    positionName: KnockoutObservable<string> = ko.observable('');
    hireDate: KnockoutObservable<string> = ko.observable('');
    ////A7
    empContactPhone: KnockoutObservable<string> = ko.observable('');
    psContactPhone: KnockoutObservable<string> = ko.observable('');
    psContactEmergencyContact1Phone: KnockoutObservable<string> = ko.observable('');
    psContactEmergencyContact2Phone: KnockoutObservable<string> = ko.observable('');
    empContactSeatDialIn: KnockoutObservable<string> = ko.observable('');
    empContactSeatExtensionNum: KnockoutObservable<string> = ko.observable('');
    empContactMailAddr: KnockoutObservable<string> = ko.observable('');
    empContactMobileMailAddr: KnockoutObservable<string> = ko.observable('');
    psContactMailAddr: KnockoutObservable<string> = ko.observable('');
    psContactMobileMailAddr: KnockoutObservable<string> = ko.observable('');

    ////A9
    ListOtherContact: KnockoutObservableArray<OtherContactViewModel> = ko.observableArray([]);

    //B
    passChangeLog: KnockoutObservable<string> = ko.observable('');
    currentPassword: KnockoutObservable<string> = ko.observable('');
    newPassword: KnockoutObservable<string> = ko.observable('');
    confirmPassword: KnockoutObservable<string> = ko.observable('');

    //// Password policy (B6)
    passPolicyLowestDigits: KnockoutObservable<string> = ko.observable('');
    passPolicyAlphabetDigit: KnockoutObservable<string> = ko.observable('');
    passPolicyNumberOfDigits: KnockoutObservable<string> = ko.observable('');
    passPolicySymbolCharacters: KnockoutObservable<string> = ko.observable('');
    passPolicyHistoryCount: KnockoutObservable<string> = ko.observable('');
    passPolicyValidityPeriod: KnockoutObservable<string> = ko.observable('');

    //C 
    anniverNoticeDayOptions: KnockoutObservableArray<ItemCbxModel> = ko.observableArray([
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_ZERO_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_ZERO_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_ONE_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_ONE_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_TWO_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_TWO_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_THREE_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_THREE_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_FOUR_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_FOUR_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_FIVE_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_FIVE_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_SIX_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_SIX_DAY') }),
      new ItemCbxModel({ code: REMIND_DATE.BEFORE_SEVEN_DAY, name: this.$i18n('Enum_NoticeDay_BEFORE_SEVEN_DAY') })
    ]);
    listAnniversary: KnockoutObservableArray<AnniversaryNotificationViewModel> = ko.observableArray([]);
    monthDaysName: KnockoutObservable<string> = ko.observable(this.$i18n('CMM048_58'));
    //D
    language: KnockoutObservable<number> = ko.observable(0);
    languageOptions: KnockoutObservableArray<ItemCbxModel> = ko.observableArray([
      new ItemCbxModel({ code: LANGUAGE.JAPANESE, name: this.$i18n('Enum_Language_JAPANESE') }),
      new ItemCbxModel({ code: LANGUAGE.ENGLISH, name: this.$i18n('Enum_Language_ENGLISH') }),
      new ItemCbxModel({ code: LANGUAGE.OTHER, name: this.$i18n('Enum_Language_OTHER') })
    ]);

    //condition to show off
    isInCharge: KnockoutObservable<boolean> = ko.observable(false);
    cPhoneUseable: KnockoutObservable<boolean> = ko.observable(false);
    cPhoneUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    psPhoneUseable: KnockoutObservable<boolean> = ko.observable(false);
    psPhoneUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    emergencyNum1Useable: KnockoutObservable<boolean> = ko.observable(false);
    emergencyNum1Updateable: KnockoutObservable<boolean> = ko.observable(false);
    emergencyNum2Useable: KnockoutObservable<boolean> = ko.observable(false);
    emergencyNum2Updateable: KnockoutObservable<boolean> = ko.observable(false);
    dialInNumUseable: KnockoutObservable<boolean> = ko.observable(false);
    dialInNumUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    extensionNumUseable: KnockoutObservable<boolean> = ko.observable(false);
    extensionNumUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    cEmailAddrUseable: KnockoutObservable<boolean> = ko.observable(false);
    cEmailAddrUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    psEmailAddrUseable: KnockoutObservable<boolean> = ko.observable(false);
    psEmailAddrUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    cMobileEmailAddrUseable: KnockoutObservable<boolean> = ko.observable(false);
    cMobileEmailAddrUpdateable: KnockoutObservable<boolean> = ko.observable(false);
    psMobileEmailAddrUseable: KnockoutObservable<boolean> = ko.observable(false);
    psMobileEmailAddrUpdateable: KnockoutObservable<boolean> = ko.observable(false);

    //general
    tabs: KnockoutObservableArray<any> = ko.observableArray([{
      id: 'tab-1',
      title: this.generateTitleTab(this.$i18n('CMM048_92'), 'setting'),
      content: '.tab-content-1',
      enable: ko.observable(true),
      visible: ko.observable(true)
    },
    {
      id: 'tab-2',
      title: this.generateTitleTab(this.$i18n('CMM048_93'), 'security'),
      content: '.tab-content-2',
      enable: ko.observable(true),
      visible: ko.observable(true),
    },
    {
      id: 'tab-3',
      title: this.generateTitleTab(this.$i18n('CMM048_94'), 'notice'),
      content: '.tab-content-3',
      enable: ko.observable(true),
      visible: ko.observable(true),
    },
    {
      id: 'tab-4',
      title: this.generateTitleTab(this.$i18n('CMM048_95'), 'language'),
      content: '.tab-content-4',
      enable: ko.observable(true),
      visible: ko.observable(true),
    }]);
    selectedTab: KnockoutObservable<string> = ko.observable('tab-1');
    //code
    companyId: KnockoutObservable<string> = ko.observable('');
    employeeId: KnockoutObservable<string> = ko.observable('');
    personId: KnockoutObservable<string> = ko.observable('');

    //#113902
    isUseOfProfile: KnockoutObservable<boolean> = ko.observable(false);
    isUseOfPassword: KnockoutObservable<boolean> = ko.observable(false);
    isUseOfNotice: KnockoutObservable<boolean> = ko.observable(false);
    isUseOfLanguage: KnockoutObservable<boolean> = ko.observable(false);

    //#113841
    passwordPolicyVisible: KnockoutObservable<boolean> = ko.observable(false);
    passPolicyLowestDigitsVisible: KnockoutObservable<boolean> = ko.observable(false);
    passPolicyDigitVisible: KnockoutObservable<boolean> = ko.observable(false);
    passPolicyAlphabetDigitVisible: KnockoutObservable<boolean> = ko.observable(false);
    passPolicyNumberOfDigitsVisible: KnockoutObservable<boolean> = ko.observable(false);
    passPolicySymbolCharactersVisible: KnockoutObservable<boolean> = ko.observable(true);
    passPolicyHistoryCountVisible: KnockoutObservable<boolean> = ko.observable(false);
    passPolicyValidityPeriodVisible: KnockoutObservable<boolean> = ko.observable(false);

    mounted() {
      const vm = this;
      vm.init();
    }

    private setDataTabA(data: UserInformationDto) {
      const vm = this;
      ////Handle avatar
      vm.avatarFileId(data.userAvatar.fileId);
      const businessName: string = data.person.personNameGroup.businessName;
      const avatarFileId: string = data.userAvatar.fileId;
      if (avatarFileId) {
        $(`#${ID_AVATAR_CHANGE}`)
          .append($("<img/>")
            .attr("alt", 'Avatar')
            .attr("class", 'avatar')
            .attr("id", 'A4_1')
            .attr("src", (nts.uk.request as any).liveView(avatarFileId))
          );
      } else {
        $(`#${ID_AVATAR_CHANGE}`).ready(() => {
          $(`#${ID_AVATAR_CHANGE}`).append(
            `<div class='avatar' id='A4_1_no_avatar'>${businessName.replace(/\s/g, '').substring(0, 2)}</div>`
          );
        });
      }
      //set data
      vm.businessName(businessName);
      vm.empCode(data.employeeDataMngInfo.employeeCode);
      vm.wkpDisplayName(data.wkpDisplayName);
      vm.positionName(data.positionName);
      vm.hireDate(data.hireDate);
      vm.empContactPhone(data.employeeContact.cellPhoneNumber);
      vm.psContactPhone(data.personalContact.phoneNumber);
      vm.psContactEmergencyContact1Phone(data.personalContact.emergencyContact1.phoneNumber);
      vm.psContactEmergencyContact2Phone(data.personalContact.emergencyContact2.phoneNumber);
      vm.empContactSeatDialIn(data.employeeContact.seatDialIn);
      vm.empContactSeatExtensionNum(data.employeeContact.seatExtensionNumber);
      vm.empContactMailAddr(data.employeeContact.mailAddress);
      vm.empContactMobileMailAddr(data.employeeContact.mobileMailAddress);
      vm.psContactMailAddr(data.personalContact.mailAddress);
      vm.psContactMobileMailAddr(data.personalContact.mobileEmailAddress)

      //set list other contact
      vm.setListOtherContact(data);
    }

    private setListOtherContact(data: UserInformationDto) {
      const vm = this;
      const listOtherContactPs = data.personalContact.otherContacts;
      const listOtherContactSetting = data.settingInformation.settingContactInformationDto.otherContacts;
      for (let i = 1; i < 6; i++) {
        const otherContactSetting: OtherContactDto = _.find(listOtherContactSetting, (contact: OtherContactDto) => contact.no === i);
        const otherContactPs: OtherContactDtoPs = _.find(listOtherContactPs, (contact: OtherContactDtoPs) => contact.otherContactNo === i);
        vm.ListOtherContact.push(
          new OtherContactViewModel(
            i,
            vm.$i18n("CMM048_86", [otherContactSetting.contactName]),
            otherContactPs ? otherContactPs.address : '',
            otherContactSetting.contactUsageSetting === 1,
          )
        );
      }
    }

    private setDataTabB(data: UserInformationDto) {
      const vm = this;
      if (data.passwordChangeLog) {
        const today = moment().utc();
        const changePassDay = moment(data.passwordChangeLog.modifiedDate, 'YYYY/MM/DD HH:mm:ss').utc();
        const lastChangePass = moment.duration(today.diff(changePassDay)).humanize();
        if (!data.passwordPolicy.validityPeriod) {
          const cmm4897: string = vm.$i18n('CMM048_97', [lastChangePass]);
          vm.passChangeLog(cmm4897);
        } else {
          const timeLeft = Math.round(data.passwordPolicy.validityPeriod - moment.duration(today.diff(changePassDay)).asDays());
          const cmm4899: string = vm.$i18n('CMM048_99', [lastChangePass, String(timeLeft)]);
          vm.passChangeLog(cmm4899);
        }
      } else {
        vm.passChangeLog(vm.$i18n('CMM048_98'));
      }
      //validityPeriod
      const lowestDigits = data.passwordPolicy.lowestDigits;
      const alphabetDigit = data.passwordPolicy.alphabetDigit;
      const numberOfDigits = data.passwordPolicy.numberOfDigits;
      const symbolCharacters = data.passwordPolicy.symbolCharacters;
      const historyCount = data.passwordPolicy.historyCount;
      const validityPeriod = data.passwordPolicy.validityPeriod;

      vm.passPolicyLowestDigits(vm.$i18n('CMM048_13', [String(lowestDigits)]));
      vm.passPolicyAlphabetDigit(vm.$i18n('CMM048_15', [String(alphabetDigit)]));
      vm.passPolicyNumberOfDigits(vm.$i18n('CMM048_16', [String(numberOfDigits)]));
      vm.passPolicySymbolCharacters(vm.$i18n('CMM048_17', [String(symbolCharacters)]));
      vm.passPolicyHistoryCount(vm.$i18n('CMM048_19', [String(historyCount)]));
      vm.passPolicyValidityPeriod(vm.$i18n('CMM048_21', [String(validityPeriod)]));

      //#113841
      vm.passwordPolicyVisible(data.passwordPolicy.isUse);
      vm.passPolicyLowestDigitsVisible(lowestDigits > 0);
      vm.passPolicyDigitVisible((alphabetDigit > 0) || (numberOfDigits > 0) || (symbolCharacters > 0));
      vm.passPolicyAlphabetDigitVisible(alphabetDigit > 0);
      vm.passPolicyNumberOfDigitsVisible(numberOfDigits > 0);
      vm.passPolicySymbolCharactersVisible(symbolCharacters > 0);
      vm.passPolicyHistoryCountVisible(historyCount > 0);
      vm.passPolicyValidityPeriodVisible(validityPeriod > 0);
    }

    private setDataTabC(data: UserInformationDto) {
      const vm = this;
      if (data.anniversaryNotices.length !== 0) {
        const list: AnniversaryNotificationViewModel[] = [];
        _.map(data.anniversaryNotices, (anniversary: AnniversaryNoticeDto) => {
          const newItem: AnniversaryNotificationViewModel =
            new AnniversaryNotificationViewModel(
              anniversary.anniversary,
              anniversary.anniversaryTitle,
              anniversary.notificationMessage,
              anniversary.noticeDay
            );
          list.push(newItem);
        });
        vm.listAnniversary(list);
      } else {
        vm.listAnniversary.push(new AnniversaryNotificationViewModel("", "", "", 0));
      }
    }

    private setDataTabD(data: UserInformationDto) {
      const vm = this;
      vm.language(data.user.language);
    }

    private setCondition(data: UserInformationDto) {
      const vm = this;
      vm.isInCharge(data.isInCharge);

      vm.isUseOfProfile(data.settingInformation.useOfProfile === IS_USE.USE);
      vm.isUseOfPassword(data.settingInformation.useOfPassword === IS_USE.USE);
      vm.isUseOfNotice(data.settingInformation.useOfNotice === IS_USE.USE);
      vm.isUseOfLanguage(data.settingInformation.useOfLanguage === IS_USE.USE);

      const displaySetting = data.settingInformation.settingContactInformationDto;
      vm.cPhoneUseable(displaySetting.companyMobilePhone.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.cPhoneUpdateable(displaySetting.companyMobilePhone.updatable === IS_USE.USE);
      vm.psPhoneUseable(displaySetting.personalMobilePhone.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.psPhoneUpdateable(displaySetting.personalMobilePhone.updatable === IS_USE.USE);
      vm.emergencyNum1Useable(displaySetting.emergencyNumber1.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.emergencyNum1Updateable(displaySetting.emergencyNumber1.updatable === IS_USE.USE);
      vm.emergencyNum2Useable(displaySetting.emergencyNumber2.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.emergencyNum2Updateable(displaySetting.emergencyNumber2.updatable === IS_USE.USE);
      vm.dialInNumUseable(displaySetting.dialInNumber.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.dialInNumUpdateable(displaySetting.dialInNumber.updatable === IS_USE.USE);
      vm.extensionNumUseable(displaySetting.extensionNumber.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.extensionNumUpdateable(displaySetting.extensionNumber.updatable === IS_USE.USE);
      vm.cEmailAddrUseable(displaySetting.companyEmailAddress.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.cEmailAddrUpdateable(displaySetting.companyEmailAddress.updatable === IS_USE.USE);
      vm.psEmailAddrUseable(displaySetting.personalEmailAddress.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.psEmailAddrUpdateable(displaySetting.personalEmailAddress.updatable === IS_USE.USE);
      vm.cMobileEmailAddrUseable(displaySetting.companyMobileEmailAddress.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.cMobileEmailAddrUpdateable(displaySetting.companyMobileEmailAddress.updatable === IS_USE.USE);
      vm.psMobileEmailAddrUseable(displaySetting.personalMobileEmailAddress.contactUsageSetting !== CONTACT_USAGE.DO_NOT_USE);
      vm.psMobileEmailAddrUpdateable(displaySetting.personalMobileEmailAddress.updatable === IS_USE.USE);
     
      //Make tab visible
      _.map(vm.tabs(), (tab: any) => {
        switch (tab.id) {
          case 'tab-1':
            tab.enable(vm.isUseOfProfile());
            tab.visible(vm.isUseOfProfile());
            break;
          case 'tab-2':
            tab.enable(vm.isUseOfPassword());
            tab.visible(vm.isUseOfPassword());
            break;
          case 'tab-3':
            tab.enable(vm.isUseOfNotice());
            tab.visible(vm.isUseOfNotice());
            break;
          case 'tab-4':
            tab.enable(vm.isUseOfLanguage());
            tab.visible(vm.isUseOfLanguage());
            break;
          default: break;
        }
      });

      if (vm.isUseOfProfile()) {
        vm.selectedTab('tab-1');
      } else if (vm.isUseOfPassword()) {
        vm.selectedTab('tab-2');
      } else if (vm.isUseOfNotice()) {
        vm.selectedTab('tab-3');
      } else {
        vm.selectedTab('tab-4');
      }
    }

    private init() {
      const vm = this;

      //make empty
      vm.currentPassword("");
      vm.newPassword("");
      vm.confirmPassword("");
      vm.listAnniversary([]);
      vm.ListOtherContact([]);

      //clear error
      vm.$errors('clear');

      //data binding
      vm.$blockui('grayout');
      vm.$ajax(API.find).then((data: UserInformationDto) => {
        $('#master-content').css({ 'display': '' });
        //set code
        vm.companyId(data.employeeDataMngInfo.companyId);
        vm.employeeId(data.employeeDataMngInfo.employeeId);
        vm.personId(data.employeeDataMngInfo.personId);

        //set data for tab A
        vm.setDataTabA(data);

        //set data for tab B
        vm.setDataTabB(data);

        //set data for tab C
        vm.setDataTabC(data);

        //set data for tab D
        vm.setDataTabD(data);

        //condition to show off
        vm.setCondition(data);
      })
        .fail((error: any) => {
          vm.$blockui('clear');
          $('#master-content').css({ 'display': 'none' });
          if (error.messageId === "Msg_1775") {
            vm.$dialog.error(error).then(() => {
              vm.openDialogCmm049();
            });
          } else if (error.messageId === "Msg_1774") {
            vm.$dialog.error(error).then(() => {
              vm.$jump("/view/ccg/008/a/index.xhtml");
            });
          }
        })
        .always(() => {
          vm.$blockui('clear');
        });
    }

    private generateTitleTab(rsCode: string, icon: string): string {
      return (
        `<span>
          <img class="tab-icon" src="./resource/${icon}.png" />
          <span>${rsCode}</span>
        </span>`
      );
    }

    public openDialogE() {
      const vm = this;
      vm.$window.modal("/view/cmm/048/e/index.xhtml", vm.avatarFileId()).then((fileId: string) => {
        if (fileId) {
          vm.avatarFileId(fileId);
          $(`#${ID_AVATAR_CHANGE}`).html("").ready(() => {
            $(`#${ID_AVATAR_CHANGE}`).append(
              $("<img/>")
                .attr("alt", 'Avatar')
                .attr("class", 'avatar')
                .attr("id", 'A4_1')
                .attr("src", (nts.uk.request as any).liveView(vm.avatarFileId()))
            );
          });
        }
      });
    }

    public openDialogCmm049() {
      const vm = this;
      vm.$window.modal("/view/cmm/049/a/index.xhtml").then(() => {
        $(`#${ID_AVATAR_CHANGE}`).html("");
        vm.init();
      });
    }

    public addNewAnniversary() {
      const vm = this;
      vm.listAnniversary.push(new AnniversaryNotificationViewModel("", "", "", 0));
    }

    public removeAnniversary(anniversary: AnniversaryNotificationViewModel, index: number) {
      const vm = this;
      $(`#month-day-${index}`).ntsError('clear');
      $(`#anniversary-title-${index}`).ntsError('clear');
      $(`#notification-message-${index}`).ntsError('clear');
      vm.listAnniversary.remove(anniversary);
    }

    private getUserCommand(): UserCommand {
      const vm = this;
      return new UserCommand({
        currentPassword: vm.currentPassword(),
        newPassword: vm.newPassword(),
        confirmPassword: vm.confirmPassword(),
        language: vm.language()
      });
    }

    private getUserAvatarCommand(): UserAvatarCommand {
      const vm = this;
      return new UserAvatarCommand({
        personalId: vm.personId(),
        fileId: vm.avatarFileId()
      });
    }

    private getAnniversaryNoticeCommandList(): AnniversaryNoticeCommand[] {
      const vm = this;
      const list: AnniversaryNoticeCommand[] = [];
      _.map(vm.listAnniversary(), (item: AnniversaryNotificationViewModel) => {
        let anniversary = String(item.anniversaryDay());
        //handle monthDay
        if (Number(anniversary) < 1000) {
          anniversary = '0' + anniversary;
        }
        if (anniversary.length === 4) {
          list.push(new AnniversaryNoticeCommand({
            personalId: vm.personId(),
            noticeDay: item.anniversaryNoticeBefore(),
            anniversary: anniversary,
            anniversaryTitle: item.anniversaryName(),
            notificationMessage: item.anniversaryRemark(),
          }));
        }
      });
      return list;
    }

    private getPersonalContactCommand(): PersonalContactCommand {
      const vm = this;
      const list: OtherContactCommand[] = [];
      _.map(vm.ListOtherContact(), (contact: OtherContactViewModel) => {
        list.push(new OtherContactCommand({
          otherContactNo: contact.contactNo,
          address: contact.contactAdress()
        }));
      });
      return new PersonalContactCommand({
        personalId: vm.personId(),
        mailAddress: vm.psContactMailAddr(),
        mobileEmailAddress: vm.psContactMobileMailAddr(),
        phoneNumber: vm.psContactPhone(),
        emergencyContact1: new EmergencyContactCommand({
          phoneNumber: vm.psContactEmergencyContact1Phone()
        }),
        emergencyContact2: new EmergencyContactCommand({
          phoneNumber: vm.psContactEmergencyContact2Phone()
        }),
        otherContacts: list,
      });
    }

    private getEmployeeContactCommand(): EmployeeContactCommand {
      const vm = this;
      return new EmployeeContactCommand({
        employeeId: vm.employeeId(),
        mailAddress: vm.empContactMailAddr(),
        seatDialIn: vm.empContactSeatDialIn(),
        seatExtensionNumber: vm.empContactSeatExtensionNum(),
        mobileMailAddress: vm.empContactMobileMailAddr(),
        cellPhoneNumber: vm.empContactPhone(),
      });
    }

    private validateAnniversary(item: AnniversaryNotificationViewModel, index: number): boolean {
      const vm = this;

      let checkEmptyAnniver = false;

      //「月・日・タイトル・内容」は１件以上が入力しました場合、他の件も入力しなければならない
      if (Number(item.anniversaryDay()) !== 0 || (Number(item.anniversaryDay()) % 100 !== 0) || !(_.isEmpty(item.anniversaryName())) || !(_.isEmpty(item.anniversaryRemark()))) {

        if (Number(item.anniversaryDay()) % 100 === 0) {
          $(`#month-day-${index}`).trigger('validate');
          checkEmptyAnniver = true;
        }

        if (_.isEmpty(item.anniversaryName())) {
          $(`#anniversary-title-${index}`).ntsError('set', { messageId: "MsgB_1", messageParams: [vm.$i18n('CMM048_59')] });
          checkEmptyAnniver = true;
        }

        if (_.isEmpty(item.anniversaryRemark())) {
          $(`#notification-message-${index}`).ntsError('set', { messageId: "MsgB_1", messageParams: [vm.$i18n('CMM048_74')] });
          checkEmptyAnniver = true;
        }
      }
      return checkEmptyAnniver;
    }

    getMonthDayJapanText(monthDay: string): string {
      const anniverDay = Number(monthDay);
      const month = Math.floor(anniverDay / 100);
      const day = anniverDay % 100;
      return String(month + '月' + day + '日');
    }

    public save() {
      const vm = this;

      vm.$validate().then((valid: boolean) => {
        if (valid) {

          //fix bug #115144 start
          const handleDuplicateAnniver = _.groupBy(vm.listAnniversary(), (item) => item.anniversaryDay());
          const listAnniverError = [];
          for (const annivers in handleDuplicateAnniver) {
            if (handleDuplicateAnniver[annivers].length > 1) {
              listAnniverError.push(vm.getMonthDayJapanText(annivers));
            }
          }

          if (!_.isEmpty(listAnniverError)) {
            return vm.$dialog.error({ messageId: 'Msg_2156', messageParams: [listAnniverError.join('\n')] });
          }
          //fix bug #115144 end

          //fix bug #114058 start
          if (vm.isUseOfNotice()) {
            let checkEmptyAnniver = false;
            _.map(vm.listAnniversary(), (item: AnniversaryNotificationViewModel, index: number) => {
              const check = vm.validateAnniversary(item, index);
              if (check) checkEmptyAnniver = true;
            });
            if (checkEmptyAnniver) return;
          }
          //fix bug #114058 end

          const userChange = vm.getUserCommand();
          const avatar = vm.getUserAvatarCommand();
          const listAnniversary = vm.getAnniversaryNoticeCommandList();
          const personalContact = vm.getPersonalContactCommand();
          const employeeContact = vm.getEmployeeContactCommand();

          const personalCommand = new PersonalCommand({
            avatar: avatar,
            anniversaryNotices: listAnniversary,
            personalContact: personalContact,
            useOfProfile: vm.isUseOfProfile(),
            useOfNotice: vm.isUseOfNotice()
          });
          const contactCommand = new ContactCommand({
            employeeContact: employeeContact,
            useOfProfile: vm.isUseOfProfile()
          });
          const changeOwnLoginPasswordCommand = new ChangeOwnLoginPasswordCommand({
            currentPassword: vm.currentPassword(),
            newPassword: vm.newPassword(),
            confirmPassword: vm.confirmPassword(),
          });
          vm.$blockui('grayout');
          $.when(
            vm.$ajax(API.updateEmployeeContact, contactCommand),
            vm.$ajax(API.updatePersonInformation, personalCommand),
            vm.changeOwnLoginPassword(changeOwnLoginPasswordCommand)
          ).then(() => {
            vm.$blockui('clear');
            vm.$dialog.info({ messageId: 'Msg_15' });
          }).fail((error: any) => {
            vm.$blockui('clear');
            vm.$dialog.error(error);
          })
            .always(() => vm.$blockui('clear'));
        }
      });
    }

    private changeOwnLoginPassword(command: ChangeOwnLoginPasswordCommand): JQueryPromise<any> {
      const vm = this;
      if (vm.isUseOfPassword() && (!nts.uk.text.isNullOrEmpty(command.newPassword) || !nts.uk.text.isNullOrEmpty(command.confirmPassword))) {
        return vm.$ajax(API.changeOwnLoginPassword, command);
      }
    }
  }

  enum LANGUAGE {
    /**
   * 日本語
   */
    JAPANESE = 0,
    /**
     * 英語
     */
    ENGLISH = 1,
    /**
    * その他
    */
    OTHER = 2
  }

  enum REMIND_DATE {

    //０：当日
    BEFORE_ZERO_DAY = 0,

    //１：1日前
    BEFORE_ONE_DAY = 1,

    //２：2日前
    BEFORE_TWO_DAY = 2,

    //３：3日前
    BEFORE_THREE_DAY = 3,

    //４：4日前
    BEFORE_FOUR_DAY = 4,

    //５：5日前
    BEFORE_FIVE_DAY = 5,

    //６：6日前
    BEFORE_SIX_DAY = 6,

    //７：7日前
    BEFORE_SEVEN_DAY = 7
  }

  enum CONTACT_USAGE {

    // 利用しない
    DO_NOT_USE = 0,

    // 利用する
    USE = 1,
  }

  enum IS_USE {
    NOT_USE = 0,
    USE = 1
  }

  enum DELETE_STATUS {

    /** 0 - 削除していない **/
    NOTDELETED = 0,

    /** 1 - 一時削除 **/
    TEMPDELETED = 1,

    /** 2 - 完全削除 **/
    PURGEDELETED = 2
  }

  enum BLOOD_TYPE {

    /* O RH+ */
    ORhPlus = 3,

    /* O RH- */
    ORhSub = 7,

    /* A RH+ */
    ARhPlus = 1,

    /* A RH- */
    ARhSub = 5,

    /* B RH+ */
    BRhPlus = 2,

    /* B RH- */
    BRhSub = 6,

    /* AB RH+ */
    ABRhPlus = 4,

    /* AB RH- */
    ABRhSub = 8
  }

  enum GENDER {

    /* 男 */
    Male = 1,

    /* 女 */
    Female = 2
  }

  enum DISABLED_SEGMENT {
    FALSE = 0, // なし
    TRUE = 1 // あり
  }

  enum PASSWORD_STATUS {

    /** 正式 */
    Official = 0,

    /** 初期パスワード */
    InitPasswor = 1,

    /** リセット */
    Reset = 2
  }

  enum EMAIL_CLASSIFICATION {
    /**
   * 会社メールアドレス
   */
    COMPANY_EMAIL_ADDRESS = 0,

    /**
     * 会社携帯メールアドレス
     */
    COMPANY_MOBILE_EMAIL_ADDRESS = 1,

    /**
     * 個人メールアドレス
     */
    PERSONAL_EMAIL_ADDRESS = 2,

    /**
     * 個人携帯メールアドレス
     */
    PERSONAL_MOBILE_EMAIL_ADDRESS = 3
  }
  class ItemCbxModel {
    code: number;
    name: string;
    constructor(init?: Partial<ItemCbxModel>) {
      $.extend(this, init);
    }
  }

  class AnniversaryNotificationViewModel {
    anniversaryDay: KnockoutObservable<string>;
    anniversaryName: KnockoutObservable<string>;
    anniversaryRemark: KnockoutObservable<string>;
    anniversaryNoticeBefore: KnockoutObservable<number>;

    constructor(
      anniversaryDay: string,
      anniversaryName: string,
      anniversaryRemark: string,
      anniversaryNoticeBefore: number
    ) {
      this.anniversaryDay = ko.observable(anniversaryDay);
      this.anniversaryName = ko.observable(anniversaryName);
      this.anniversaryRemark = ko.observable(anniversaryRemark);
      this.anniversaryNoticeBefore = ko.observable(anniversaryNoticeBefore)
    }
  }

  class OtherContactViewModel {
    contactNo: number;
    contactName!: string;
    contactAdress!: KnockoutObservable<string>;
    contactUsage!: boolean;
    constructor(
      contactNo: number,
      contactName: string,
      contactAdress: string,
      contactUsage: boolean,
    ) {
      this.contactNo = contactNo;
      this.contactName = contactName;
      this.contactAdress = ko.observable(contactAdress);
      this.contactUsage = contactUsage;
    }
  }

 /**
   * Command ユーザーのログインパスワードを変更する
   */
  class ChangeOwnLoginPasswordCommand {

    /**
     * 現行のパスワード
     */
    currentPassword: string;

    /**
     * 新しいパスワード
     */
    newPassword: string;

    /**
     * 新しいパスワード（確認）
     */
    confirmPassword: string;

    constructor(init?: Partial<ChangeOwnLoginPasswordCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command アカウント情報を登録する
   */
  class ContactCommand {
    /**
     * 社員連絡先を登録する
     */
    employeeContact: EmployeeContactCommand;

    //fix bug #113902
    useOfProfile: boolean;

    constructor(init?: Partial<ContactCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command アカウント情報を登録する
   */
  class PersonalCommand {
    /**
     * 個人の顔写真を登録する
     */
    avatar: UserAvatarCommand;

    /**
     * 記念日を削除する + 個人の記念日情報を登録する
     */
    anniversaryNotices: AnniversaryNoticeCommand[];

    /**
     * 個人連絡先を登録する
     */
    personalContact: PersonalContactCommand;

    //fix bug #113902
    useOfProfile: boolean;

    //fix bug #113902
    useOfNotice: boolean

    constructor(init?: Partial<PersonalCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command dto ユーザ
   */
  class UserCommand {

    /**
     * 現行のパスワード
     */
    currentPassword: string;

    /**
     * 新しいパスワード
     */
    newPassword: string;

    /**
     * 新しいパスワード（確認）
     */
    confirmPassword: string;

    /**
     * 言語
     */
    language: number;

    constructor(init?: Partial<UserCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command dto 個人の顔写真
   */
  class UserAvatarCommand {

    /**
     * 個人ID
     */
    personalId: string;

    /**
     * 顔写真ファイルID
     */
    fileId: string;

    constructor(init?: Partial<UserAvatarCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command dto 個人の記念日情報
   */
  class AnniversaryNoticeCommand {
    /**
     * 個人ID
     */
    personalId: string;

    /**
     * 日数前の通知
     */
    noticeDay: number;

    /**
     * 最後見た記念日
     */
    seenDate: string;

    /**
     * 記念日
     */
    anniversary: string;

    /**
     * 記念日のタイトル
     */
    anniversaryTitle: string;

    /**
     * 記念日の内容
     */
    notificationMessage: string;

    constructor(init?: Partial<AnniversaryNoticeCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command dto 個人連絡先
   */
  class OtherContactCommand {
    /**
     * NO
     */
    otherContactNo: number;

    /**
     * 在席照会に表示するか
     */
    isDisplay: boolean;

    /**
     * 連絡先のアドレス
     */
    address: string;

    constructor(init?: Partial<OtherContactCommand>) {
      $.extend(this, init);
    }
  }
  /**
   * Command dto 社員連絡先
   */
  class EmployeeContactCommand {
    /**
     * 社員ID
     */
    employeeId: string;

    /**
     * メールアドレス
     */
    mailAddress: string;

    /**
     * メールアドレスが在席照会に表示するか
     */
    isMailAddressDisplay: boolean;

    /**
     * 座席ダイヤルイン
     */
    seatDialIn: string;

    /**
     * 座席ダイヤルインが在席照会に表示するか
     */
    isSeatDialInDisplay: boolean;

    /**
     * 座席内線番号
     */
    seatExtensionNumber: string;

    /**
     * 座席内線番号が在席照会に表示するか
     */
    isSeatExtensionNumberDisplay: boolean;

    /**
     * 携帯メールアドレス
     */
    mobileMailAddress: string;

    /**
     * 携帯メールアドレスが在席照会に表示するか
     */
    isMobileMailAddressDisplay: boolean;

    /**
     * 携帯電話番号
     */
    cellPhoneNumber: string;

    /**
     * 携帯電話番号が在席照会に表示するか
     */
    isCellPhoneNumberDisplay: boolean;

    constructor(init?: Partial<EmployeeContactCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Command dto 個人連絡先
   */
  class PersonalContactCommand {

    /**
     * 個人ID
     */
    personalId: string;

    /**
     * メールアドレス
     */
    mailAddress: string;

    /**
     * メールアドレスが在席照会に表示するか
     */
    isMailAddressDisplay: boolean;

    /**
     * 携帯メールアドレス
     */
    mobileEmailAddress: string;

    /**
     * 携帯メールアドレスが在席照会に表示するか
     */
    isMobileEmailAddressDisplay: boolean;

    /**
     * 携帯電話番号
     */
    phoneNumber: string;

    /**
     * 携帯電話番号が在席照会に表示するか
     */
    isPhoneNumberDisplay: boolean;

    /**
     * 緊急連絡先１
     */
    emergencyContact1: EmergencyContactCommand;

    /**
     * 緊急連絡先１が在席照会に表示するか
     */
    isEmergencyContact1Display: boolean;

    /**
     * 緊急連絡先２
     */
    emergencyContact2: EmergencyContactCommand;

    /**
     * 緊急連絡先２が在席照会に表示するか
     */
    isEmergencyContact2Display: boolean;

    /**
     * 他の連絡先
     */
    otherContacts: OtherContactCommand[];

    constructor(init?: Partial<PersonalContactCommand>) {
      $.extend(this, init);
    }
  }

  /**
 * Command dto 個人連絡先
 */
  class EmergencyContactCommand {
    /**
     * メモ
     */
    remark: string;

    /**
     * 連絡先名
     */
    contactName: string;

    /**
     * 電話番号
     */
    phoneNumber: string;

    constructor(init?: Partial<EmergencyContactCommand>) {
      $.extend(this, init);
    }
  }

  /**
   * Dto ユーザ情報の表示
   */
  interface UserInformationDto {
    /**
  * パスワードポリシー
  */
    passwordPolicy: PasswordPolicyDto;

    /**
     * ログイン者が担当者か
     */
    isInCharge: boolean;

    /**
     * ユーザー情報の使用方法
     */
    settingInformation: UserInfoUseMethodDto;

    /**
     * 入社日
     */
    hireDate: string;

    /**
     * ユーザ
     */
    user: UserDto;

    /**
     * 個人
     */
    person: PersonDto;

    /**
     * 個人連絡先
     */
    personalContact: PersonalContactDto;

    /**
     * 社員データ管理情報
     */
    employeeDataMngInfo: EmployeeDataMngInfoDto;

    /**
     * 社員連絡先
     */
    employeeContact: EmployeeContactDto;

    /**
     * パスワード変更ログ
     */
    passwordChangeLog: PasswordChangeLogDto;

    /**
     * 個人の記念日情報
     */
    anniversaryNotices: AnniversaryNoticeDto[];

    /**
     * 個人の顔写真
     */
    userAvatar: UserAvatarDto;

    /**
     * 職位名称
     */
    positionName: string;

    /**
     * 職場表示名
     */
    wkpDisplayName: string;
  }

  /**
   * Dto パスワードポリシー
   */
  interface PasswordPolicyDto {
    /**
     * 契約コード
     */
    contractCode: string;

    /**
     * パスワード変更通知
     */
    notificationPasswordChange: number;

    /**
     * ログイン時にパスワードに従っていない場合変更させる
     */
    loginCheck: boolean;

    /**
     * 最初のパスワード変更
     */
    initialPasswordChange: boolean;

    /**
     * 利用する
     */
    isUse: boolean;

    /**
     * 履歴回数
     */
    historyCount: number;

    /**
     * 最低桁数
     */
    lowestDigits: number;

    /**
     * 有効期間
     */
    validityPeriod: number;

    /**
     * 複雑さ
     */
    numberOfDigits: number;

    /**
     *
     */
    symbolCharacters: number;

    /**
     *
     */
    alphabetDigit: number;
  }

  /**
   * Dto ユーザー情報の使用方法
   */
  interface UserInfoUseMethodDto {
    /**
    * お知らせの利用
    */
    useOfNotice: IS_USE;

    /**
     * パスワードの利用
     */
    useOfPassword: IS_USE;

    /**
     * プロフィールの利用
     */
    useOfProfile: IS_USE;

    /**
     * 言語の利用
     */
    useOfLanguage: IS_USE;

    /**
     * 会社ID
     */
    companyId: string;

    /**
     * メール送信先機能
     */
    emailDestinationFunctionDtos: EmailDestinationFunctionDto[];

    /**
     * 連絡先情報の設定
     */
    settingContactInformationDto: SettingContactInformationDto;
  }

  /**
   * Dto メール送信先機能
   */
  interface EmailDestinationFunctionDto {
    /**
     * メール分類
     */
    emailClassification: EMAIL_CLASSIFICATION;

    /**
     * 機能ID
     */
    functionIds: number[];
  }

  /**
 * Dto 連絡先情報の設定
 */
  interface SettingContactInformationDto {
    /**
   * ダイヤルイン番号
   */
    dialInNumber: ContactSettingDto;

    /**
     * 会社メールアドレス
     */
    companyEmailAddress: ContactSettingDto;

    /**
     * 会社携帯メールアドレス
     */
    companyMobileEmailAddress: ContactSettingDto;

    /**
     * 個人メールアドレス
     */
    personalEmailAddress: ContactSettingDto;

    /**
     * 個人携帯メールアドレス
     */
    personalMobileEmailAddress: ContactSettingDto;

    /**
     * 内線番号
     */
    extensionNumber: ContactSettingDto;

    /**
     * 携帯電話（会社用）
     */
    companyMobilePhone: ContactSettingDto;

    /**
     * 携帯電話（個人用）
     */
    personalMobilePhone: ContactSettingDto;

    /**
     * 緊急電話番号1
     */
    emergencyNumber1: ContactSettingDto;

    /**
     * 緊急電話番号2
     */
    emergencyNumber2: ContactSettingDto;

    /**
     * 他の連絡先
     */
    otherContacts: OtherContactDto[];
  }

  /**
 * Dto 連絡先の設定
 */
  interface ContactSettingDto {
    /**
   * 連絡先利用設定
   */
    contactUsageSetting: CONTACT_USAGE;

    /**
     * 更新可能
     */
    updatable: IS_USE;
  }

  /**
   * Dto 他の連絡先
   */
  interface OtherContactDto {
    /**
   * NO
   */
    no: number;

    /**
     * 連絡先利用設定
     */
    contactUsageSetting: CONTACT_USAGE;

    /**
     * 連絡先名
     */
    contactName: string;
  }

  /**
 * Dto ユーザ
 */
  interface UserDto {
    /**
    * ユーザID
    */
    userId: string;

    /**
     * デフォルトユーザ
     */
    defaultUser: boolean;

    /**
     * パスワード
     */
    password: string;

    /**
     * ログインID
     */
    loginID: string;

    /**
     * 契約コード
     */
    contractCode: string;

    /**
     * 有効期限
     */
    expirationDate: string;

    /**
     * 特別利用者
     */
    specialUser: DISABLED_SEGMENT;

    /**
     * 複数会社を兼務する
     */
    multiCompanyConcurrent: DISABLED_SEGMENT;

    /**
     * メールアドレス
     */
    mailAddress: string;

    /**
     * ユーザ名
     */
    userName: string;

    /**
     * 紐付け先個人ID
     */
    associatedPersonID: string;

    /**
     * パスワード状態
     */
    passStatus: PASSWORD_STATUS;

    /**
     * 言語
     */
    language: LANGUAGE;
  }


  /**
   * Dto 個人
   */
  interface PersonDto {
    /**
     * 個人ID
     */
    personId: string;

    /**
     * 生年月日
     */

    birthDate: string;

    /**
     * 血液型
     */
    bloodType: BLOOD_TYPE;

    /**
     * 性別
     */
    gender: GENDER;

    /**
     * 個人名グループ
     */
    personNameGroup: PersonNameGroupDto;
  }

  /**
 * Dto 個人名グループ
 */
  interface PersonNameGroupDto {
    /**
    * ビジネスネーム
    */
    businessName: string;

    /**
     * ビジネスネームカナ
     */
    businessNameKana: string;

    /**
     * ビジネスネームその他
     */
    businessOtherName: string;

    /**
     * ビジネスネーム英語
     */
    businessEnglishName: string;

    /**
     * 個人名
     */
    personName: FullNameSetDto;

    /**
     * 個人名多言語
     */
    PersonalNameMultilingual: FullNameSetDto;

    /**
     * 個人名ローマ字
     */
    personRomanji: FullNameSetDto;

    /**
     * 個人届出名称
     */
    todokedeFullName: FullNameSetDto;

    /**
     * 個人旧氏名
     */
    oldName: FullNameSetDto;
  }

  interface FullNameSetDto {
    /**
   * 氏名
   */
    fullName: string;

    /**
     * 氏名カナ
     */
    fullNameKana: string;
  }

  /**
   * Dto 個人連絡先
   */
  interface PersonalContactDto {
    /**
   * 個人ID
   */
    personalId: string;

    /**
     * メールアドレス
     */
    mailAddress: string;

    /**
     * 携帯メールアドレス
     */
    mobileEmailAddress: string;

    /**
     * 携帯電話番号
     */
    phoneNumber: string;

    /**
     * 緊急連絡先１
     */
    emergencyContact1: EmergencyContactDto;

    /**
     * 緊急連絡先２
     */
    emergencyContact2: EmergencyContactDto;

    /**
     * 他の連絡先
     */
    otherContacts: OtherContactDtoPs[];
  }

  /**
   * Dto 緊急連絡先
   */
  interface EmergencyContactDto {
    /**
      * メモ
      */
    remark: string;

    /**
     * 連絡先名
     */
    contactName: string;

    /**
     * 電話番号
     */
    phoneNumber: string;
  }

  /**
   * Dto 他の連絡先
   */
  interface OtherContactDtoPs {
    /**
    * NO
    */
    otherContactNo: number;

    /**
     * 連絡先のアドレス
     */
    address: string;
  }

  /**
 * Dto 社員データ管理情報
 */
  interface EmployeeDataMngInfoDto {
    /**
    * 会社ID
    */
    companyId: string;

    /**
     * 個人ID
     */
    personId: string;

    /**
     * 社員ID
     */
    employeeId: string;

    /**
     * 社員コード
     */
    employeeCode: string;

    /**
     * 削除状況
     */
    deletedStatus: DELETE_STATUS;

    /**
     * 一時削除日時
     */
    deleteDateTemporary: string;

    /**
     * 削除理由
     */
    removeReason: string;

    /**
     * 外部コード
     */
    externalCode: string;
  }

  /**
   * Dto 社員連絡先
   */
  interface EmployeeContactDto {
    /**
   * 社員ID
   */
    employeeId: string;

    /**
     * メールアドレス
     */
    mailAddress: string;

    /**
     * 座席ダイヤルイン
     */
    seatDialIn: string;

    /**
     * 座席内線番号
     */
    seatExtensionNumber: string;

    /**
     * 携帯メールアドレス
     */
    mobileMailAddress: string;

    /**
     * 携帯電話番号
     */
    cellPhoneNumber: string;
  }

  /**
   * Dto パスワード変更ログ
   */
  interface PasswordChangeLogDto {
    /**
   * ログID
   */
    logId: string;

    /**
     * ユーザID
     */
    userId: string;

    /**
     * 変更日時
     */
    modifiedDate: string;

    /**
     * パスワード
     */
    password: string;
  }

  /**
   * Dto 個人の記念日情報
   */
  interface AnniversaryNoticeDto {
    /**
    * 個人ID
    */
    personalId: string;

    /**
     * 日数前の通知
     */
    noticeDay: REMIND_DATE;

    /**
     * 最後見た記念日
     */
    seenDate: string;

    /**
     * 記念日
     */
    anniversary: string;

    /**
     * 記念日のタイトル
     */
    anniversaryTitle: string;

    /**
     * 記念日の内容
     */
    notificationMessage: string;
  }

  /**
   * Dto 個人の顔写真
   */
  interface UserAvatarDto {
    /**
   * 個人ID
   */
    personalId: string;

    /**
     * 顔写真ファイルID
     */
    fileId: string;
  }
}
