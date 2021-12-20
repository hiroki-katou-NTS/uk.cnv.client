module nts.uk.com.view.cmm049.a {
  const API = {
    findByCid: "query/cmm049userinformationsetting/get",
    insertOrUpdate: "sys/env/userinformationusermethod/insertorupdate",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {
    $grid!: JQuery;

    public tabs: KnockoutObservableArray<any>;
    public selectedTab: KnockoutObservable<string>;

    public profileCheckList: KnockoutObservableArray<CheckboxModel> = ko.observableArray([]);
    public profileSelectedId: KnockoutObservable<number> = ko.observable(2); //#114200

    public passwordCheckList: KnockoutObservableArray<CheckboxModel> = ko.observableArray([]);
    public passwordSelectedId: KnockoutObservable<number> = ko.observable(2); //#114200

    public noticeCheckList: KnockoutObservableArray<CheckboxModel> = ko.observableArray([]);
    public noticeSelectedId: KnockoutObservable<number> = ko.observable(2); //#114200

    public speechCheckList: KnockoutObservableArray<CheckboxModel> = ko.observableArray([]);
    public speechSelectedId: KnockoutObservable<number> = ko.observable(2); //#113760

    // A4
    public contactName1: KnockoutObservable<string> = ko.observable("");
    public contactName2: KnockoutObservable<string> = ko.observable("");
    public contactName3: KnockoutObservable<string> = ko.observable("");
    public contactName4: KnockoutObservable<string> = ko.observable("");
    public contactName5: KnockoutObservable<string> = ko.observable("");

    public companyMobilePhoneDisplay: KnockoutObservable<boolean> = ko.observable();
    public companyMobilePhoneUpdatable: KnockoutObservable<boolean> = ko.observable();

    public personalMobilePhoneDisplay: KnockoutObservable<boolean> = ko.observable();
    public personalMobilePhoneUpdatable: KnockoutObservable<boolean> = ko.observable();

    public emergencyNumber1Display: KnockoutObservable<boolean> = ko.observable();
    public emergencyNumber1Updatable: KnockoutObservable<boolean> = ko.observable();

    public emergencyNumber2Display: KnockoutObservable<boolean> = ko.observable();
    public emergencyNumber2Updatable: KnockoutObservable<boolean> = ko.observable();

    public dialInNumberDisplay: KnockoutObservable<boolean> = ko.observable();
    public dialInNumberUpdatable: KnockoutObservable<boolean> = ko.observable();

    public extensionNumberDisplay: KnockoutObservable<boolean> = ko.observable();
    public extensionNumberUpdatable: KnockoutObservable<boolean> = ko.observable();

    public companyEmailAddressDisplay: KnockoutObservable<boolean> = ko.observable();
    public companyEmailAddressUpdatable: KnockoutObservable<boolean> = ko.observable();

    public companyMobileEmailAddressDisplay: KnockoutObservable<boolean> = ko.observable();
    public companyMobileEmailAddressUpdatable: KnockoutObservable<boolean> = ko.observable();

    public personalEmailAddressDisplay: KnockoutObservable<boolean> = ko.observable();
    public personalEmailAddressUpdatable: KnockoutObservable<boolean> = ko.observable();

    public personalMobileEmailAddressDisplay: KnockoutObservable<boolean> = ko.observable();
    public personalMobileEmailAddressUpdatable: KnockoutObservable<boolean> = ko.observable();

    public otherContact1Display: KnockoutObservable<boolean> = ko.observable();
    public otherContact1ContactName: KnockoutObservable<string> = ko.observable("");

    public otherContact2Display: KnockoutObservable<boolean> = ko.observable();
    public otherContact2ContactName: KnockoutObservable<string> = ko.observable("");

    public otherContact3Display: KnockoutObservable<boolean> = ko.observable();
    public otherContact3ContactName: KnockoutObservable<string> = ko.observable("");

    public otherContact4Display: KnockoutObservable<boolean> = ko.observable();
    public otherContact4ContactName: KnockoutObservable<string> = ko.observable("");

    public otherContact5Display: KnockoutObservable<boolean> = ko.observable();
    public otherContact5ContactName: KnockoutObservable<string> = ko.observable("");

    public mailFunctionDtos: KnockoutObservableArray<MailFunctionDto> = ko.observableArray([]);
    public userInformationUseMethodDto: KnockoutObservable<UserInformationUseMethodDto> = ko.observable();
    public otherContact1: KnockoutObservable<string> = ko.observable("");
    public otherContact2: KnockoutObservable<string> = ko.observable("");
    public otherContact3: KnockoutObservable<string> = ko.observable("");
    public otherContact4: KnockoutObservable<string> = ko.observable("");
    public otherContact5: KnockoutObservable<string> = ko.observable("");

    public selectedEmailAddress: KnockoutObservable<number> = ko.observable(0);

    //fix bug 112907
    otherContact1Required: KnockoutObservable<boolean> = ko.observable(false);
    otherContact2Required: KnockoutObservable<boolean> = ko.observable(false);
    otherContact3Required: KnockoutObservable<boolean> = ko.observable(false);
    otherContact4Required: KnockoutObservable<boolean> = ko.observable(false);
    otherContact5Required: KnockoutObservable<boolean> = ko.observable(false);
    /**
     * Key: emailClassification, value: listfunctionId
     */
    public emailData: any = {};

    /**
     * Giá trị emailClassification đang được chọn
     */
    public selectedEmailClassification: number = null;

    /**
     * Các functionId được check theo key emailClassification
     */
    public mailFunctionDataSource: MailFunctionModel[] = [];

    public emailColumns: any[] = [
      {
        headerText: "",
        prop: "index",
        width: 160,
        hidden: true,
      },
      {
        headerText: this.$i18n("CMM049_66"),
        prop: "emailAddress",
        width: 180,
      },
    ];

    public emailDataSource: KnockoutObservableArray<EmailModel> = ko.observableArray([
      new EmailModel({ index: 0, emailAddress: this.$i18n("CMM049_49") }),
      new EmailModel({ index: 1, emailAddress: this.$i18n("CMM049_50") }),
      new EmailModel({ index: 2, emailAddress: this.$i18n("CMM049_51") }),
      new EmailModel({ index: 3, emailAddress: this.$i18n("CMM049_52") }),
    ]);

    constructor() {
      super();
      const vm = this;
      vm.tabs = ko.observableArray([
        {
          id: "tab-1",
          title: vm.$i18n("CMM049_24"),
          content: "#A4",
          enable: ko.observable(true),
          visible: ko.observable(true),
        },
        {
          id: "tab-2",
          title: vm.$i18n("CMM049_25"),
          content: "#A5",
          enable: ko.observable(true),
          visible: ko.observable(true),
        },
        {
          id: "tab-3",
          title: vm.$i18n("CMM049_26"),
          content: "#A6",
          enable: ko.observable(true),
          visible: ko.observable(true),
        },
        {
          id: "tab-4",
          title: vm.$i18n("CMM049_27"),
          content: "#A7",
          enable: ko.observable(false), //#113760
          visible: ko.observable(false), //#113760
        },
        {
          id: "tab-5",
          title: vm.$i18n("CMM049_28"),
          content: "#A8",
          enable: ko.observable(false),
          visible: ko.observable(false),
        },
      ]);
      vm.selectedTab = ko.observable("tab-1");

      // tab 1 - profile function
      vm.profileCheckList = ko.observableArray([
        new CheckboxModel({
          id: 1,
          name: vm.$i18n("CMM049_31"),
        }),
        new CheckboxModel({
          id: 2,
          name: vm.$i18n("CMM049_32"),
        }),
      ]);

      // tab 2 - password
      vm.passwordCheckList = ko.observableArray([
        new CheckboxModel({
          id: 1,
          name: vm.$i18n("CMM049_31"),
        }),
        new CheckboxModel({
          id: 2,
          name: vm.$i18n("CMM049_32"),
        }),
      ]);

      // tab 3 - notice
      vm.noticeCheckList = ko.observableArray([
        new CheckboxModel({
          id: 1,
          name: vm.$i18n("CMM049_31"),
        }),
        new CheckboxModel({
          id: 2,
          name: vm.$i18n("CMM049_32"),
        }),
      ]);

      // tab 4 - speech
      vm.speechCheckList = ko.observableArray([
        new CheckboxModel({
          id: 1,
          name: vm.$i18n("CMM049_31"),
        }),
        new CheckboxModel({
          id: 2,
          name: vm.$i18n("CMM049_32"),
        }),
      ]);
    }

    mounted() {
      const vm = this;

      vm.$grid = $("#A8_4");

      /**
       * メールアドレスを選択する時
       */
      vm.selectedEmailAddress.subscribe((newValue) => {
        /**
         * Lọc toàn bộ các functionId được check theo emailClassification
         */
        vm.emailData[vm.selectedEmailClassification] = _.chain(vm.mailFunctionDataSource)
          .filter((item) => item.isChecked)
          .map((item) => item.functionId)
          .value();
        if (vm.$grid.data("igGrid")) {
          vm.$grid.ntsGrid("destroy");
        }
        vm.initGrid(newValue, vm.mailFunctionDtos());
      });

      /**
       * ユーザ情報の設定を起動する
       */
      vm.getData();

      //fix bug #112907
      vm.profileSelectedId.subscribe(newVal => {
        if (newVal === 2) {
          $('#A4_4_33').ntsError('clear');
          $('#A4_4_36').ntsError('clear');
          $('#A4_4_39').ntsError('clear');
          $('#A4_4_42').ntsError('clear');
          $('#A4_4_45').ntsError('clear');
        }
      });

      vm.otherContact1Display.subscribe((newValue: boolean) => {
        if (!newValue) {
          $('#A4_4_33').ntsError('clear');
        }
      });

      vm.otherContact2Display.subscribe((newValue: boolean) => {
        if (!newValue) {
          $('#A4_4_36').ntsError('clear');
        }
      });

      vm.otherContact3Display.subscribe((newValue: boolean) => {
        if (!newValue) {
          $('#A4_4_39').ntsError('clear');
        }
      });

      vm.otherContact4Display.subscribe((newValue: boolean) => {
        if (!newValue) {
          $('#A4_4_42').ntsError('clear');
        }
      });

      vm.otherContact5Display.subscribe((newValue: boolean) => {
        if (!newValue) {
          $('#A4_4_45').ntsError('clear');
        }
      });
    }

    public setCheckboxLine1(response: UserInformationSettingDto): void {
      const vm = this;
      const companyMobilePhone =
        response.userInformationUseMethodDto.settingContactInformationDto
          .companyMobilePhone.contactUsageSetting;
      switch (companyMobilePhone) {
        case 0: {
          vm.companyMobilePhoneDisplay(false);
          break;
        }
        case 1: {
          vm.companyMobilePhoneDisplay(true);
          break;
        }
        case 2: {
          vm.companyMobilePhoneDisplay(true);
          break;
        }
        default: break;
      }
      vm.companyMobilePhoneUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .companyMobilePhone.updatable === 1
      );
    }

    public setCheckboxLine2(response: UserInformationSettingDto): void {
      const vm = this;
      const personalMobilePhone =
        response.userInformationUseMethodDto.settingContactInformationDto
          .personalMobilePhone.contactUsageSetting;
      switch (personalMobilePhone) {
        case 0: {
          vm.personalMobilePhoneDisplay(false);
          break;
        }
        case 1: {
          vm.personalMobilePhoneDisplay(true);
          break;
        }
        case 2: {
          vm.personalMobilePhoneDisplay(true);
          break;
        }
        default: break;
      }
      vm.personalMobilePhoneUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .personalMobilePhone.updatable === 1
      );
    }

    public setCheckboxLine3(response: UserInformationSettingDto): void {
      const vm = this;
      const emergencyNumber1 =
        response.userInformationUseMethodDto.settingContactInformationDto
          .emergencyNumber1.contactUsageSetting;
      switch (emergencyNumber1) {
        case 0: {
          vm.emergencyNumber1Display(false);
          break;
        }
        case 1: {
          vm.emergencyNumber1Display(true);
          break;
        }
        case 2: {
          vm.emergencyNumber1Display(true);
          break;
        }
        default: break;
      }
      vm.emergencyNumber1Updatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .emergencyNumber1.updatable === 1
      );
    }

    public setCheckboxLine4(response: UserInformationSettingDto): void {
      const vm = this;
      const emergencyNumber2 =
        response.userInformationUseMethodDto.settingContactInformationDto
          .emergencyNumber2.contactUsageSetting;
      switch (emergencyNumber2) {
        case 0: {
          vm.emergencyNumber2Display(false);
          break;
        }
        case 1: {
          vm.emergencyNumber2Display(true);
          break;
        }
        case 2: {
          vm.emergencyNumber2Display(true);
          break;
        }
        default: break;
      }
      vm.emergencyNumber2Updatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .emergencyNumber2.updatable === 1
      );
    }

    public setCheckboxLine5(response: UserInformationSettingDto): void {
      const vm = this;
      const dialInNumber =
        response.userInformationUseMethodDto.settingContactInformationDto
          .dialInNumber.contactUsageSetting;
      switch (dialInNumber) {
        case 0: {
          vm.dialInNumberDisplay(false);
          break;
        }
        case 1: {
          vm.dialInNumberDisplay(true);
          break;
        }
        case 2: {
          vm.dialInNumberDisplay(true);
          break;
        }
        default: break;
      }
      vm.dialInNumberUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .dialInNumber.updatable === 1
      );
    }

    public setCheckboxLine6(response: UserInformationSettingDto): void {
      const vm = this;
      const extensionNumber =
        response.userInformationUseMethodDto.settingContactInformationDto
          .extensionNumber.contactUsageSetting;
      switch (extensionNumber) {
        case 0: {
          vm.extensionNumberDisplay(false);
          break;
        }
        case 1: {
          vm.extensionNumberDisplay(true);
          break;
        }
        case 2: {
          vm.extensionNumberDisplay(true);
          break;
        }
        default: break;
      }
      vm.extensionNumberUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .extensionNumber.updatable === 1
      );
    }

    public setCheckboxLine7(response: UserInformationSettingDto): void {
      const vm = this;
      const companyEmailAddress =
        response.userInformationUseMethodDto.settingContactInformationDto
          .companyEmailAddress.contactUsageSetting;
      switch (companyEmailAddress) {
        case 0: {
          vm.companyEmailAddressDisplay(false);
          break;
        }
        case 1: {
          vm.companyEmailAddressDisplay(true);
          break;
        }
        case 2: {
          vm.companyEmailAddressDisplay(true);
          break;
        }
        default: break;
      }
      vm.companyEmailAddressUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .companyEmailAddress.updatable === 1
      );
    }

    public setCheckboxLine8(response: UserInformationSettingDto): void {
      const vm = this;
      const companyMobileEmailAddress =
        response.userInformationUseMethodDto.settingContactInformationDto
          .companyMobileEmailAddress.contactUsageSetting;
      switch (companyMobileEmailAddress) {
        case 0: {
          vm.companyMobileEmailAddressDisplay(false);
          break;
        }
        case 1: {
          vm.companyMobileEmailAddressDisplay(true);
          break;
        }
        case 2: {
          vm.companyMobileEmailAddressDisplay(true);
          break;
        }
        default: break;
      }
      vm.companyMobileEmailAddressUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .companyMobileEmailAddress.updatable === 1
      );
    }

    public setCheckboxLine9(response: UserInformationSettingDto): void {
      const vm = this;
      const personalEmailAddress =
        response.userInformationUseMethodDto.settingContactInformationDto
          .personalEmailAddress.contactUsageSetting;
      switch (personalEmailAddress) {
        case 0: {
          vm.personalEmailAddressDisplay(false);
          break;
        }
        case 1: {
          vm.personalEmailAddressDisplay(true);
          break;
        }
        case 2: {
          vm.personalEmailAddressDisplay(true);
          break;
        }
        default: break;
      }
      vm.personalEmailAddressUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .personalEmailAddress.updatable === 1
      );
    }

    public setCheckboxLine10(response: UserInformationSettingDto): void {
      const vm = this;
      const personalMobileEmailAddress =
        response.userInformationUseMethodDto.settingContactInformationDto
          .personalMobileEmailAddress.contactUsageSetting;
      switch (personalMobileEmailAddress) {
        case 0: {
          vm.personalMobileEmailAddressDisplay(false);
          break;
        }
        case 1: {
          vm.personalMobileEmailAddressDisplay(true);
          break;
        }
        case 2: {
          vm.personalMobileEmailAddressDisplay(true);
          break;
        }
        default: break;
      }
      vm.personalMobileEmailAddressUpdatable(
        response.userInformationUseMethodDto.settingContactInformationDto
          .personalMobileEmailAddress.updatable === 1
      );
    }

    public setCheckboxLine11(response: UserInformationSettingDto): void {
      const vm = this;
      const otherContact1 = vm.getOtherContact(1, response);
      switch (otherContact1.contactUsageSetting) {
        case 0: {
          vm.otherContact1Display(false);
          break;
        }
        case 1: {
          vm.otherContact1Display(true);
          break;
        }
        case 2: {
          vm.otherContact1Display(true);
          break;
        }
        default: break;
      }
    }

    public setCheckboxLine12(response: UserInformationSettingDto): void {
      const vm = this;
      const otherContact2 = vm.getOtherContact(2, response);
      switch (otherContact2.contactUsageSetting) {
        case 0: {
          vm.otherContact2Display(false);
          break;
        }
        case 1: {
          vm.otherContact2Display(true);
          break;
        }
        case 2: {
          vm.otherContact2Display(true);
          break;
        }
        default: break;
      }
    }

    public setCheckboxLine13(response: UserInformationSettingDto): void {
      const vm = this;
      const otherContact3 = vm.getOtherContact(3, response);
      switch (otherContact3.contactUsageSetting) {
        case 0: {
          vm.otherContact3Display(false);
          break;
        }
        case 1: {
          vm.otherContact3Display(true);
          break;
        }
        case 2: {
          vm.otherContact3Display(true);
          break;
        }
        default: break;
      }
    }

    public setCheckboxLine14(response: UserInformationSettingDto): void {
      const vm = this;
      const otherContact4 = vm.getOtherContact(4, response);
      switch (otherContact4.contactUsageSetting) {
        case 0: {
          vm.otherContact4Display(false);
          break;
        }
        case 1: {
          vm.otherContact4Display(true);
          break;
        }
        case 2: {
          vm.otherContact4Display(true);
          break;
        }
        default: break;
      }
    }

    public setCheckboxLine15(response: UserInformationSettingDto): void {
      const vm = this;
      const otherContact5 = vm.getOtherContact(5, response);
      switch (otherContact5.contactUsageSetting) {
        case 0: {
          vm.otherContact5Display(false);
          break;
        }
        case 1: {
          vm.otherContact5Display(true);
          break;
        }
        case 2: {
          vm.otherContact5Display(true);
          break;
        }
        default: break;
      }
    }

    public setButtonGroup(response: UserInformationSettingDto): void {
      const vm = this;
      vm.profileSelectedId(response.userInformationUseMethodDto.useOfProfile === 1 ? 1 : 2);
      vm.passwordSelectedId(response.userInformationUseMethodDto.useOfPassword === 1 ? 1 : 2);
      vm.noticeSelectedId(response.userInformationUseMethodDto.useOfNotice === 1 ? 1 : 2);
      vm.speechSelectedId(2); //#113760
    }

    public setContactNameInput(response: UserInformationSettingDto): void {
      const vm = this;
      vm.contactName1(vm.getOtherContact(1, response).contactName);
      vm.contactName2(vm.getOtherContact(2, response).contactName);
      vm.contactName3(vm.getOtherContact(3, response).contactName);
      vm.contactName4(vm.getOtherContact(4, response).contactName);
      vm.contactName5(vm.getOtherContact(5, response).contactName);
    }

    public getData() {
      const vm = this;
      vm.$blockui("grayout")
        .then(() => vm.$ajax(API.findByCid))
        .then((response: UserInformationSettingDto) => {
          vm.mailFunctionDtos(response.mailFunctionDtos);
          vm.userInformationUseMethodDto(response.userInformationUseMethodDto);

          // binding button group (tab-1-2-3-4)
          vm.setButtonGroup(response);

          // binding contact name input (tab-1)
          vm.setContactNameInput(response);

          // binding data (tab-1)
          // line 1
          vm.setCheckboxLine1(response);

          // line 2
          vm.setCheckboxLine2(response);

          // line 3
          vm.setCheckboxLine3(response);

          // line 4
          vm.setCheckboxLine4(response);

          // line 5
          vm.setCheckboxLine5(response);

          // line 6
          vm.setCheckboxLine6(response);

          // line 7
          vm.setCheckboxLine7(response);

          // line 8
          vm.setCheckboxLine8(response);

          // line 9
          vm.setCheckboxLine9(response);

          // line 10
          vm.setCheckboxLine10(response);

          // line 11
          vm.setCheckboxLine11(response);

          // line 12
          vm.setCheckboxLine12(response);

          // line 13
          vm.setCheckboxLine13(response);

          // line 14
          vm.setCheckboxLine14(response);

          // line 15
          vm.setCheckboxLine15(response);

          /**
           * Gán key (emailClassification) và value (List functionIds) cho emailData
           */
          _.forEach(
            response.userInformationUseMethodDto.emailDestinationFunctionDtos,
            (email: EmailDestinationFunctionDto) =>
              (vm.emailData[email.emailClassification] = email.functionIds)
          );

          vm.initGrid(vm.selectedEmailAddress(), vm.mailFunctionDtos());
        })
        .always(() => vm.$blockui("clear"));
    }

    private getOtherContact(no: number, response: any): OtherContactDto {
      return _.find(
        response.userInformationUseMethodDto.settingContactInformationDto.otherContacts,
        (item: any) => item.no === no
      );
    }

    /**
     * Create Table target email
     * @param emailClassification // email đang được chọn
     * @param mailFunctionList // List mailFunction (dữ liệu bảng bên phải)
     */
    private initGrid(emailClassification: number, mailFunctionList: MailFunctionDto[]) {
      const vm = this;
      /**
       * Tìm List functionId theo key emailClassification
       */
      const selectedFundtionIds: number[] = vm.emailData[emailClassification];

      /**
       * Lưu giá trị emailClassification đang được chọn
       */
      vm.selectedEmailClassification = emailClassification;

      /**
       * Lưu những functionId được check
       */
      vm.mailFunctionDataSource = _.map(mailFunctionList, (item) => {
        const model = new MailFunctionModel(item);
        model.isChecked = selectedFundtionIds.indexOf(item.functionId) !== -1;
        return model;
      });

      vm.$grid
        .ntsGrid({
          primaryKey: "functionId",
          height: "270px",
          dataSource: vm.mailFunctionDataSource,
          rowVirtualization: true,
          virtualization: true,
          virtualizationMode: "continuous",
          columns: [
            {
              headerText: "",
              key: "functionId",
              dataType: "number",
              hidden: true,
            },
            {
              headerText: "",
              key: "isChecked",
              dataType: "boolean",
              width: "35px",
              ntsControl: "Checkbox",
              showHeaderCheckbox: true,
            },
            {
              headerText: vm.$i18n("CMM049_21"),
              key: "functionName",
              dataType: "string",
              width: "300x",
            },
          ],
          features: [
            {
              name: "Selection",
              mode: "row",
              multipleSelection: true,
              activation: true,
            },
          ],
          ntsFeatures: [],
          ntsControls: [
            {
              name: "Checkbox",
              options: { value: 1, text: "" },
              optionsValue: "value",
              optionsText: "text",
              controlType: "CheckBox",
              enable: true,
            },
          ],
        });

      const checkbox = $(vm.$el).find('#A8_4_isChecked input[type="checkbox"]');
      const isCheckall = vm.mailFunctionDataSource.filter(({ isChecked }) => !isChecked).length === 0;

      if (checkbox.length) {
        checkbox.prop('checked', isCheckall);
      }
    }

    /**
     * ユーザ情報へ戻る
     */
    public closeDialog() {
      const vm = this;
      vm.$window.close();
    }

    public getUserInformationUseMethodDto(otherContactDtos: OtherContactDto[]): UserInformationUseMethodDto {
      const vm = this;
      return new UserInformationUseMethodDto(
        {
          useOfProfile: vm.profileSelectedId() === 1 ? 1 : 0,
          useOfPassword: vm.passwordSelectedId() === 1 ? 1 : 0,
          useOfNotice: vm.noticeSelectedId() === 1 ? 1 : 0,
          useOfLanguage: vm.speechSelectedId() === 1 ? 1 : 0,
          companyId: vm.userInformationUseMethodDto().companyId,
          settingContactInformationDto: new SettingContactInformationDto({
            companyEmailAddress: new ContactSettingDto({
              contactUsageSetting: vm.companyEmailAddressDisplay() ? 1 : 0,
              updatable: vm.companyEmailAddressUpdatable() ? 1 : 0,
            }),
            companyMobileEmailAddress: new ContactSettingDto({
              contactUsageSetting:  vm.companyMobileEmailAddressDisplay() ? 1 : 0,
              updatable: vm.companyMobileEmailAddressUpdatable() ? 1 : 0,
            }),
            companyMobilePhone: new ContactSettingDto({
              contactUsageSetting: vm.companyMobilePhoneDisplay() ? 1 : 0,
              updatable: vm.companyMobilePhoneUpdatable() ? 1 : 0,
            }),
            dialInNumber: new ContactSettingDto({
              contactUsageSetting: vm.dialInNumberDisplay() ? 1 : 0,
              updatable: vm.dialInNumberUpdatable() ? 1 : 0,
            }),
            emergencyNumber1: new ContactSettingDto({
              contactUsageSetting: vm.emergencyNumber1Display() ? 1 : 0,
              updatable: vm.emergencyNumber1Updatable() ? 1 : 0,
            }),
            emergencyNumber2: new ContactSettingDto({
              contactUsageSetting: vm.emergencyNumber2Display() ? 1 : 0,
              updatable: vm.emergencyNumber2Updatable() ? 1 : 0,
            }),
            extensionNumber: new ContactSettingDto({
              contactUsageSetting: vm.extensionNumberDisplay() ? 1 : 0,
              updatable: vm.extensionNumberUpdatable() ? 1 : 0,
            }),
            personalEmailAddress: new ContactSettingDto({
              contactUsageSetting: vm.personalEmailAddressDisplay() ? 1 : 0,
              updatable: vm.personalEmailAddressUpdatable() ? 1 : 0,
            }),
            personalMobileEmailAddress: new ContactSettingDto({
              contactUsageSetting: vm.personalMobileEmailAddressDisplay() ? 1 : 0,
              updatable: vm.personalMobileEmailAddressUpdatable() ? 1 : 0,
            }),
            personalMobilePhone: new ContactSettingDto({
              contactUsageSetting: vm.personalMobilePhoneDisplay() ? 1 : 0,
              updatable: vm.personalMobilePhoneUpdatable() ? 1 : 0,
            }),
            otherContacts: otherContactDtos,
          }),
          emailDestinationFunctionDtos: [
            new EmailDestinationFunctionDto({
              emailClassification: 0,
              functionIds: vm.emailData[0]
            }),
            new EmailDestinationFunctionDto({
              emailClassification: 1,
              functionIds: vm.emailData[1]
            }),
            new EmailDestinationFunctionDto({
              emailClassification: 2,
              functionIds: vm.emailData[2]
            }),
            new EmailDestinationFunctionDto({
              emailClassification: 3,
              functionIds: vm.emailData[3]
            }),
          ],
        }
      );
    }

    public getOtherContactDtos(): OtherContactDto[] {
      const vm = this;
      return [
        new OtherContactDto({
          no: 1,
          contactName: vm.contactName1(),
          contactUsageSetting: vm.otherContact1Display() ? 1 : 0,
        }),
        new OtherContactDto({
          no: 2,
          contactName: vm.contactName2(),
          contactUsageSetting: vm.otherContact2Display() ? 1 : 0,
        }),
        new OtherContactDto({
          no: 3,
          contactName: vm.contactName3(),
          contactUsageSetting: vm.otherContact3Display() ? 1 : 0,
        }),
        new OtherContactDto({
          no: 4,
          contactName: vm.contactName4(),
          contactUsageSetting: vm.otherContact4Display() ? 1 : 0,
        }),
        new OtherContactDto({
          no: 5,
          contactName: vm.contactName5(),
          contactUsageSetting: vm.otherContact5Display() ? 1 : 0,
        }),
      ];
    }

    /**
     * 新規登録する
     * 変更登録する
     */
    public register() {
      const vm = this;
      //fix bug #112907
      vm.otherContact1Required(vm.otherContact1Display());
      vm.otherContact2Required(vm.otherContact2Display());
      vm.otherContact3Required(vm.otherContact3Display());
      vm.otherContact4Required(vm.otherContact4Display());
      vm.otherContact5Required(vm.otherContact5Display());
      if (vm.profileSelectedId() === 2) {
        vm.otherContact1Required(false);
        vm.otherContact2Required(false);
        vm.otherContact3Required(false);
        vm.otherContact4Required(false);
        vm.otherContact5Required(false);
      }
      //fix bug #112907 end
      // check error
      vm.$validate().then((valid: boolean) => {
        if (valid) {
          vm.emailData[vm.selectedEmailClassification] = _.chain(vm.mailFunctionDataSource)
            .filter((item) => item.isChecked)
            .map((item) => item.functionId)
            .value();
          const userInformationUseMethodDto: UserInformationUseMethodDto = vm.getUserInformationUseMethodDto(vm.getOtherContactDtos());

          const command = new UserInformationUseMethodSaveCommand({
            userInformationUseMethodDto: userInformationUseMethodDto,
          });
          vm.$blockui("grayout");
          vm.$ajax(API.insertOrUpdate, command)
            .then(() => {
              return vm.$dialog.info({ messageId: 'Msg_15' });
            })
            .always(() => {
              vm.$blockui("clear");
              this.closeDialog();
            });
        }
      });
    }
  }

  export class CheckboxModel {
    id: number;
    name: string;

    constructor(init?: Partial<CheckboxModel>) {
      $.extend(this, init);
    }
  }

  export class EmailModel {
    index: number;
    emailAddress: string;

    constructor(init?: Partial<EmailModel>) {
      $.extend(this, init);
    }
  }

  export class UserInformationSettingDto {
    userInformationUseMethodDto: UserInformationUseMethodDto;
    mailFunctionDtos: MailFunctionDto[];

    constructor(init?: Partial<UserInformationSettingDto>) {
      $.extend(this, init);
    }
  }

  export class UserInformationUseMethodDto {
    /**
     * お知らせの利用
     */
    useOfNotice: number;

    /**
     * パスワードの利用
     */
    useOfPassword: number;

    /**
     * プロフィールの利用
     */
    useOfProfile: number;

    /**
     * 言語の利用
     */
    useOfLanguage: number;

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

    constructor(init?: Partial<UserInformationUseMethodDto>) {
      $.extend(this, init);
    }
  }

  export interface MailFunctionDto {
    //機能ID
    functionId: number;

    //機能名
    functionName: string;

    //メール送信設定可否区分
    proprietySendMailSettingAtr: boolean;

    //並び順
    sortOrder: number;
  }

  export class MailFunctionModel {
    //機能ID
    functionId: number;

    //機能名
    functionName: string;

    //メール送信設定可否区分
    proprietySendMailSettingAtr: boolean;

    //並び順
    sortOrder: number;

    isChecked: boolean;

    constructor(init?: Partial<MailFunctionModel>) {
      $.extend(this, init);
    }
  }

  export class EmailDestinationFunctionDto {
    /**
     * メール分類
     */
    emailClassification: number;

    /**
     * 機能ID
     */
    functionIds: number[];

    constructor(init?: Partial<EmailDestinationFunctionDto>) {
      $.extend(this, init);
    }
  }

  export class SettingContactInformationDto {
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

    constructor(init?: Partial<SettingContactInformationDto>) {
      $.extend(this, init);
    }
  }

  export class ContactSettingDto {
    /**
     * 連絡先利用設定
     */
    contactUsageSetting: number;

    /**
     * 更新可能
     */
    updatable: number;

    constructor(init?: Partial<ContactSettingDto>) {
      $.extend(this, init);
    }
  }

  export class OtherContactDto {
    /**
     * NO
     */
    no: number;

    /**
     * 連絡先利用設定
     */
    contactUsageSetting: number;

    /**
     * 連絡先名
     */
    contactName: string;

    constructor(init?: Partial<OtherContactDto>) {
      $.extend(this, init);
    }
  }

  export class MailFunctionData {
    functionId: number;
    dataAvailable: boolean;
    targetMenu: string;

    constructor(init?: Partial<MailFunctionData>) {
      $.extend(this, init);
    }
  }

  export class UserInformationUseMethodSaveCommand {
    userInformationUseMethodDto: UserInformationUseMethodDto;

    constructor(init?: Partial<UserInformationUseMethodSaveCommand>) {
      $.extend(this, init);
    }
  }
}
