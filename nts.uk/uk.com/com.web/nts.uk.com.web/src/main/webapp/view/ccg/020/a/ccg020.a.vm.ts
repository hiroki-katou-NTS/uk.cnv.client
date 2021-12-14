/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.com.view.ccg020.a {

  import ukText = nts.uk.text;

  const API = {
    get10LastResults: "sys/portal/generalsearch/history/get-10-last-result",
    getByContent: "sys/portal/generalsearch/history/get-by-content",
    saveHistorySearch: 'sys/portal/generalsearch/history/save',
    removeHistorySearch: 'sys/portal/generalsearch/history/remove',
    getAvatar: 'ctx/bs/person/avatar/get',
    isDisplayWarning: 'ctx/sys/gateway/system/is-display-warning',
    isDisplayNewNotice: 'sys/portal/notice/is-new-notice',
    checkSearchManual: 'sys/portal/generalsearch/check-search-manual'
  };
  
  @component({
    name: 'ccg020-component',
    template: `<div id="ccg020"><div id="search-bar" class="cf">
    <ccg003-component></ccg003-component>
    <div id="ccg002-panel" class="panel ccg002-panel">
      <i id="search-icon" data-bind="ntsIcon: { no: 1, width: 28, height: 25 }" class="img-icon"></i>
      <!-- <i id="ccg002-arrow-icon" data-bind="ntsIcon: { no: 135, width: 10, height: 23 }"></i> -->
    </div>
    <!-- This input is CCG002 -->
    <input id="search-input" autocomplete="off" data-bind="ntsTextEditor: {
      value: valueSearch,
      enterkey: submit,
      option: ko.mapping.fromJS(new nts.uk.ui.option.TextEditorOption({
        textmode: 'text',
        width: '162px',
        placeholder: searchPlaceholder
      }))
    },
    click: eventClickSearch" />
    <div id="popup-search-category">
      <div id="radio-search-category" data-bind="ntsRadioBoxGroup: {
        options: searchCategoryList,
        optionsValue: 'id',
        optionsText: 'name',
        value: searchCategory,
        enable: true
        }" class="ntsControl radio-wrapper reset-element" tabindex="0">
      </div>
    </div>
    <div id="popup-result"></div>
    <div id="popup-search"></div>
  </div>
  `
  })
  export class CCG020Screen extends ko.ViewModel {
    treeMenu: KnockoutObservableArray<TreeMenu> = ko.observableArray([]);
    treeMenuResult: KnockoutObservableArray<TreeMenu> = ko.observableArray([]);
    dataDisplay: KnockoutObservableArray<any> = ko.observableArray([]);
    generalSearchHistory: KnockoutObservable<GeneralSearchHistoryCommand> = ko.observable(null);
    valueSearch: KnockoutObservable<string> = ko.observable('');
    searchPlaceholder: KnockoutObservable<string> = ko.observable();
    searchCategory: KnockoutObservable<number> = ko.observable(0);
    searchCategoryList: KnockoutObservableArray<any> = ko.observableArray([]);
    isDisplayWarningMsg: KnockoutObservable<boolean> = ko.observable(false);
    isDisplayNewNotice: KnockoutObservable<boolean> = ko.observable(false);
    isEmployee: KnockoutComputed<boolean> = ko.computed(() => __viewContext.user.isEmployee);
    warningMsg: KnockoutObservable<string> = ko.observable('');

    created() {
      const vm = this;
      vm.checkCanSearchManual();
      vm.searchPlaceholder(vm.$i18n('CCG002_6'));
    }

    mounted() {
      const vm = this;
      vm.addSearchBar();
      vm.getListMenu();
      
      $('#radio-search-category').on('click', () => {
        $("#popup-search-category").ntsPopup("hide");
      });
      vm.searchCategory.subscribe((value) => {
        vm.searchPlaceholder(value === 0 ? vm.$i18n('CCG002_6') : vm.$i18n('CCG002_7'));
      });
      $("#search-icon").on('click', () => {
        $("#popup-search-category").ntsPopup("toggle");
      });
      $("#ccg002-arrow-icon").on('click', () => {
        $("#popup-search-category").ntsPopup("toggle");
      });

      $(window).on('wd.setAvatar', () => vm.getAvatar());
    }

    private getAvatar() {
      const vm = this;
      const setAvatarByName = () => 
        vm.$ajax('com', '/sys/portal/webmenu/username')
        .then(username => {
          $('<div/>')
          .attr('id', 'avatar_id_ccg020')
          .text(username.replace(/\s/g, '').substring(0, 2))
          .appendTo($('#notice-msg'));
        });
      if (!__viewContext.user.isEmployee) {
        setAvatarByName();
        return;
      }
      vm.$ajax('com', API.getAvatar)
        .then((data) => {
          if (data && data.fileId !== null) {
            $('<img/>')
              .attr('id', 'img-avatar')
              .attr('src', (nts.uk.request as any).liveView(data.fileId))
              .appendTo($('#notice-msg'));
            $('#search-bar').attr('style', 'position: relative;');
          } else {
            setAvatarByName();
          }

          $(window).trigger('wd.resize');
        })
    }

    initWarningMsg() {
      $('.ccg020-warning').ntsPopup({
        showOnStart: false,
        dismissible: true,
        position: { of: $('#master-wrapper') }
      });
    }

    public addEventClickWarningBtn() {
      const vm = this;
      vm.$ajax('com', 'ctx/sys/gateway/system/is-display-warning').then((response: WarningMessageDto) => {
        if (!response || !response.display) {
          $('.ccg020-warning').ntsPopup('hide');
          return;
        }
        vm.warningMsg(response.message);
        $('.ccg020-warning').ntsPopup('toggle');
      })
    }

    /* Screen CCG002 */
    private addSearchBar() {
      $('#popup-result').ntsPopup({
        showOnStart: false,
        dismissible: true,
        position: {
          my: 'right top',
          at: 'right bottom',
          of: '#ccg002-panel'
        }
      });
      $('#popup-search').ntsPopup({
        showOnStart: false,
        dismissible: true,
        position: {
          my: 'left top',
          at: 'left bottom',
          of: '#search-input'
        }
      });
      $('#popup-search-category').ntsPopup({
        showOnStart: false,
        dismissible: false,
        position: {
          my: 'right top',
          at: 'right bottom',
          of: '#ccg002-arrow-icon'
        }
      });

      $('#popup-result #list-box').on('selectionChanging', (event: any) => {
        window.location.href = event.detail.url;
      });
    }

    private getListMenu() {
      const vm = this;
      const menuSet = JSON.parse(sessionStorage.getItem('nts.uk.session.MENU_SET').value);
      let treeMenu = _.flatMap(
        menuSet,
        i => _.flatMap(
          i.menuBar,
          ii => _.flatMap(
            ii.titleMenu,
            iii => iii.treeMenu
          )
        )
      );
      // Ver1: queryStringがNotEmptyの場合：URL　＝　url　+　’?’　+　queryString
      _.forEach(treeMenu, item => item.url = !_.isEmpty(item.queryString) ? `${item.url}?${item.queryString}` : item.url);
      treeMenu = _.uniqBy(treeMenu, 'url');
      _.forEach(treeMenu, (item: TreeMenu) => {
        item.name = item.displayName === item.defaultName
          ? item.displayName
          : `${item.displayName} (${item.defaultName})`;
      });
      vm.treeMenu(treeMenu);
    }

    private eventClickSearch() {
      const vm = this;
      vm.get10LastResults();
      $('#popup-search').ntsPopup('show');
      $("#popup-search-category").ntsPopup("hide");
    }

    submit() {
      const vm = this;
      $('#list-box').remove();
      $('#popup-search').ntsPopup('hide');
      if (_.isEmpty(vm.treeMenu())) {
        vm.getListMenu();
      }
      vm.treeMenuResult([]);
      vm.$validate('#search-input')
        .then((valid) => {
          if (!valid) {
            return;
          }
          
          if (vm.valueSearch() !== '') {
            const valueSearch = vm.valueSearch();
            if (ukText.countHalf(valueSearch) > 50) {
              vm.valueSearch(vm.subString(valueSearch, 50));
            }

            vm.treeMenuResult(vm.filterItems(vm.valueSearch(), vm.treeMenu()));
            const $tableResult = $('<div/>').attr('id', 'list-box');
            const list = vm.treeMenuResult();
            if (list.length > 0) {
              vm.addHistoryResult();
              $tableResult.append(
                $('<p/>')
                  .addClass('result-search custom-limited-label')
                  .text(nts.uk.text.format(nts.uk.resource.getText('CCG002_8'), vm.valueSearch())
                )
              );

              _.forEach(list, (item) => {
                const $ul = $('<a/>')
                  .addClass('result-search custom-limited-label blue-link-ccg020')
                  .attr('href', item.url)
                  .text(item.name);
                $tableResult.append($ul);
              });
            } else {
              $tableResult.text(nts.uk.resource.getText('CCG002_9'));
            }
            $('#popup-result')
              .append($tableResult)
              .ntsPopup('show');
          }
        });
    }

    private subString(value: string, length: number): string {
      let maxCountHalfSizeCharacter = length;
      let valueTemp = "";
      const valueSplip = value.split("");
      valueSplip.forEach((character: string) => {
          maxCountHalfSizeCharacter -= ukText.countHalf(character);
          if (maxCountHalfSizeCharacter >= 0) {
              valueTemp += character;
          }
      });
      return valueTemp;
  }

    private addHistoryResult() {
      const vm = this;
      const command: GeneralSearchHistoryCommand = new GeneralSearchHistoryCommand({
        searchCategory: vm.searchCategory(),
        contents: vm.valueSearch()
      });
      vm.$ajax('com', API.saveHistorySearch, command)
        .then(() => vm.get10LastResults())
    }

    private removeHistoryResult(command: GeneralSearchHistoryCommand) {
      const vm = this;
      vm.$ajax('com', API.removeHistorySearch, command)
        .then(() => vm.get10LastResults())
    }

    private get10LastResults() {
      const vm = this;
      $('#list-box-search').remove();
      vm.$ajax('com', `${API.get10LastResults}/${vm.searchCategory()}`)
        .then((response) => {
          vm.dataDisplay(response);
          vm.displayResultSearchHistory();
        })
    }

    private displayResultSearchHistory() {
      const vm = this;
      const $tableSearch = $('<ul/>').attr('id', 'list-box-search');
      const list = vm.dataDisplay();
      if (list.length > 0) {
        _.forEach(list, (item) => {
          const $iconClose = $('<i/>')
            .attr('id', item.contents)
            .addClass('icon icon-close hide-class')
            .attr('style', 'float: right;');
          const $textLi = $('<span/>')
            .addClass('text-li custom-limited-label')
            .text(item.contents);
          const $li = $('<li/>')
            .addClass('result-search-history')
            .append($textLi)
            .append($iconClose)
            .on('click', (event) => vm.selectItemSearch(item))
            .appendTo($tableSearch)
            .hover(() => {
              $(`#${item.contents}`).removeClass('hide-class');
            }, () => {
              $(`#${item.contents}`).addClass('hide-class');
            });
          $iconClose.hover(() => {
            $iconClose.on('click', (event) => vm.removeHistoryResult(item));
            $li.off('click');
          }, () => {
            $iconClose.off('click');
            $li.on('click', (event) => vm.selectItemSearch(item));
          });
        });
      } else {
        $tableSearch.text(nts.uk.resource.getText('CCG002_5'));
      }
      const $popup = $('#popup-search');
      $popup.append($tableSearch);
    }

    private selectItemSearch(data: any) {
      const vm = this;
      vm.valueSearch(data.contents);
      $('#popup-search').ntsPopup('hide');
      vm.submit();
    }

    private filterItems(query: any, list: any) {
      return _.filter(list, (el: any) => _.includes(_.lowerCase(el.name), _.lowerCase(query)));
    }

    private isDisplayWarning() {
      const vm = this;
      vm.$ajax('com', API.isDisplayWarning)
        .then((response: WarningMessageDto) => {
          if (!response || !response.display) {
            const ccg020w = $('#ccg020').width();
            $('#ccg020').width(ccg020w - 50);
          }
          vm.isDisplayWarningMsg(response.display);
        })
    }

    private checkCanSearchManual() {
      const vm = this;
      if (!__viewContext.user.companyId) {
        return;
      }
      vm.$ajax('com', API.checkSearchManual)
        .then((response) => {
          if (response) {
            vm.searchCategoryList([
              { id: 0, name: vm.$i18n('CCG002_2') },
              { id: 1, name: vm.$i18n('CCG002_3') }
            ]);
          } else {
            vm.searchCategoryList([{ id: 0, name: vm.$i18n('CCG002_2') }]);
          }
        })
    }
  }

  export interface WarningMessageDto {
    display: boolean;
	  message: string;
  }

  export class MenuSet {
    companyId: string;
    webMenuCode: number;
    webMenuName: string;
    defaultMenu: number;
    menuBar: MenuBar[];
    constructor(init?: Partial<MenuSet>) {
      $.extend(this, init);
    }
  }

  export class MenuBar {
    menuBarId: string;
    code: number;
    displayOrder: number;
    link: string;
    menuBarName: string;
    menuCls: number;
    selectedAttr: number;
    system: number;
    textColor: string;
    titleMenu: TitleMenu[];
    constructor(init?: Partial<MenuBar>) {
      $.extend(this, init);
    }
  }

  export class TitleMenu {
    backgroundColor: string;
    displayOrder: number;
    imageFile: string;
    textColor: string;
    titleMenuAtr: number;
    titleMenuCode: string;
    titleMenuId: string;
    titleMenuName: string;
    treeMenu: TreeMenu[];
    constructor(init?: Partial<TitleMenu>) {
      $.extend(this, init);
    }
  }

  export class TreeMenu {
    afterLoginDisplay: number;
    classification: number;
    code: string;
    defaultName: string;
    displayName: string;
    displayOrder: number;
    menuAttr: number;
    programId: string;
    queryString: string;
    screenId: string;
    system: number;
    url: string;
    name: string;
    constructor(init?: Partial<TreeMenu>) {
      $.extend(this, init);
    }
  }

  export class GeneralSearchHistoryCommand {
    contents: string;
    searchCategory: number;
    constructor(init?: Partial<GeneralSearchHistoryCommand>) {
      $.extend(this, init);
    }
  }

  export class AvatarDto {
    /** 個人ID
    */
    personalId: string;
    /**
     * 顔写真ファイルID
     */
    fileId: string;
    constructor(init?: Partial<AvatarDto>) {
      $.extend(this, init);
    }
  }
}
