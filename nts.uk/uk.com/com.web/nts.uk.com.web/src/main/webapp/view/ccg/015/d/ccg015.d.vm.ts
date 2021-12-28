/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.com.view.ccg015.d {
  import ntsFile = nts.uk.request.file; 

  @bean()
  export class ScreenModel extends ko.ViewModel {
    params: any = {};
    flowMenuSelectedCode: KnockoutObservable<string> = ko.observable('');
    toppageSelectedCode: KnockoutObservable<string> = ko.observable('');
    listFlowMenu: KnockoutObservableArray<FlowMenuItem> = ko.observableArray<FlowMenuItem>([]);
    listTopPagePart: KnockoutObservableArray<TopPagePartItem> = ko.observableArray<TopPagePartItem>([]);
    columnsFlowMenu: KnockoutObservableArray<any> = ko.observableArray([]);
    columnsTopPagePart: KnockoutObservableArray<any> = ko.observableArray([]);
    itemList: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
    isRequired: KnockoutObservable<boolean> = ko.observable(true);
    contentFlowMenu: KnockoutObservable<boolean> = ko.observable(true);
    contentTopPagePart: KnockoutObservable<boolean> = ko.observable(false);
    contentUrl: KnockoutObservable<boolean> = ko.observable(false);
    urlIframe1: KnockoutObservable<string> = ko.observable('');
    urlIframe2: KnockoutObservable<string> = ko.observable('');
    urlIframe3: KnockoutObservable<string> = ko.observable('');
    isNewMode: KnockoutObservable<boolean> = ko.observable(true);
    topPageCode: KnockoutObservable<string> = ko.observable('');
    layoutNo: KnockoutObservable<number> = ko.observable(0);
    layoutType: KnockoutObservable<number> = ko.observable(null);
    flowMenuCd: KnockoutObservable<string> = ko.observable('');
    flowMenuUpCd: KnockoutObservable<string> = ko.observable('');
    url: KnockoutObservable<string> = ko.observable('');
    fileID: KnockoutObservable<string> = ko.observable('');
    filePath: KnockoutObservable<string> = ko.observable('');

    created(params: any) {
      const vm = this;
      vm.params = params;
      vm.topPageCode(params.topPageModel.topPageCode);
      vm.getFlowmenuUpload();
      vm.getFlowmenu();
      vm.checkDataLayout(vm.params)
      vm.itemList([
        new ItemModel(LayoutType.FLOW_MENU, vm.$i18n('Enum_LayoutType_FLOW_MENU')),
        new ItemModel(LayoutType.FLOW_MENU_UPLOAD, vm.$i18n('Enum_LayoutType_FLOW_MENU_UPLOAD')),
        new ItemModel(LayoutType.EXTERNAL_URL, vm.$i18n('Enum_LayoutType_EXTERNAL_URL')),
      ]);
      vm.columnsFlowMenu([
        { headerText: vm.$i18n('CCG015_68'), width: "50px", key: 'flowCode' },
        { headerText: vm.$i18n('CCG015_69'), width: "300px", key: 'flowName' },
      ]);
      vm.columnsTopPagePart([
        { headerText: vm.$i18n('CCG015_68'), width: "50px", key: 'flowCode' },
        { headerText: vm.$i18n('CCG015_69'), width: "300px", key: 'flowName' },
      ]);
      vm.layoutType.subscribe(value => {
        vm.$errors('clear');
        vm.$blockui('grayout');
        vm.changeLayout(value)
          .then(() => {
            if (value === LayoutType.FLOW_MENU) {
              vm.contentFlowMenu(true);
              vm.contentTopPagePart(false);
              vm.contentUrl(false);
              const flowMenuChoose = _.findIndex(vm.listFlowMenu(), (item: FlowMenuItem) => { return item.flowCode === vm.flowMenuSelectedCode() });
              const fileIdChoose: string = vm.listFlowMenu()[flowMenuChoose].fileId;

              if (!!fileIdChoose) {
                vm.$blockui('grayout');
                vm.$ajax('sys/portal/createflowmenu/extract/' + fileIdChoose).then((item: any) => {
                if (!_.isEmpty(item)) {
                  vm.renderHTML(item.htmlContent, 'frame1');
                  _.each((document.getElementById("frameF1") as any ).contentDocument.getElementsByTagName("a"), link => {
                    link.addEventListener('click', (event: Event) => event.preventDefault());
                  })
                }
                }).always(() => vm.$blockui('clear'));
              } else {
                const ifr = document.getElementById('frameF1');
                const iframedoc = (ifr as any).contentDocument || (ifr as any).contentWindow.document;
                iframedoc.body.innerHTML = "";
              }
            } else if (value === LayoutType.FLOW_MENU_UPLOAD) {
              vm.contentFlowMenu(false);
              vm.contentTopPagePart(true);
              vm.contentUrl(false);
              const topPagePartChoose = _.findIndex(vm.listTopPagePart(), (item: TopPagePartItem) => { return item.flowCode ===  vm.toppageSelectedCode()});
              const fileIdChoose: string = vm.listTopPagePart()[topPagePartChoose].fileId;
              vm.fileID(fileIdChoose);
              vm.filePath(ntsFile.liveViewUrl(fileIdChoose, 'index.htm'));
              $('#frameF2').on('load', function(){
                vm.$nextTick(() => {
                  _.each((document.getElementById("frameF2") as any ).contentDocument.getElementsByTagName("a"), link => {
                    link.removeAttribute('onclick');
                    link.removeAttribute('href')
                  })
                });
              });
              
            } else {
              vm.contentFlowMenu(false);
              vm.contentTopPagePart(false);
              vm.contentUrl(true);
            }
          })
          .always(() => vm.$blockui('clear'));
      });

      vm.flowMenuSelectedCode.subscribe(data => {
        const flowMenuChoose = _.findIndex(vm.listFlowMenu(), (item: FlowMenuItem) => { return item.flowCode === data });
        vm.flowMenuSelectedCode(vm.listFlowMenu()[flowMenuChoose].flowCode);
        const fileIdChoose: string = vm.listFlowMenu()[flowMenuChoose].fileId;
        if(!!fileIdChoose) {
          vm.$blockui('grayout');
          vm.$ajax('sys/portal/createflowmenu/extract/' + fileIdChoose).then((item: any) => {
            
            if (!_.isEmpty(item)) {
              vm.renderHTML(item.htmlContent, 'frame1');
              _.each((document.getElementById("frameF1") as any ).contentDocument.getElementsByTagName("a"), link => {
                link.addEventListener('click', (event: Event) => event.preventDefault());
              })
            }
          }).fail(() => {
              const ifr = document.getElementById('frameF1');
              const iframedoc = (ifr as any).contentDocument || (ifr as any).contentWindow.document;
              iframedoc.body.innerHTML = "";
          }).always(() => vm.$blockui('clear'));
        } else {
          const ifr = document.getElementById('frameF1');
          const iframedoc = (ifr as any).contentDocument || (ifr as any).contentWindow.document;
          iframedoc.body.innerHTML = "";
        }
      })

      vm.toppageSelectedCode.subscribe(data => {
        const topPagePartChoose = _.findIndex(vm.listTopPagePart(), (item: TopPagePartItem) => { return item.flowCode ===  data});
        vm.toppageSelectedCode(vm.listTopPagePart()[topPagePartChoose].flowCode);
        const fileIdChoose: string = vm.listTopPagePart()[topPagePartChoose].fileId;
        vm.fileID(fileIdChoose);
        vm.filePath(ntsFile.liveViewUrl(fileIdChoose, 'index.htm'));

        $('#frameF2').on('load', function(){
          vm.$nextTick(() => {
            _.each((document.getElementById("frameF2") as any ).contentDocument.getElementsByTagName("a"), link => {
              link.removeAttribute('onclick');
              link.removeAttribute('href')
            })
          });
        });
      })
    }

    mounted() {
      const vm = this;
      $('#D2_2').focus();
      vm.checkDataLayout(vm.params)
    }

    private changeLayout(layoutType: number): JQueryPromise<any> {
      const vm = this;
      const data = {
        topPageCd: vm.topPageCode(),
        layoutType: layoutType,
      };
      return vm.$ajax('/toppage/changeFlowMenu', data)
        .then((result: any) => {
          if (result && layoutType === LayoutType.FLOW_MENU) {
            vm.listFlowMenu(_.orderBy(result,['flowCode'],['asc']));
            if(vm.flowMenuSelectedCode() === ''){
             vm.flowMenuSelectedCode(vm.listFlowMenu()[0].flowCode);
            }
          } else if (result && layoutType === LayoutType.FLOW_MENU_UPLOAD) {
            if(result.length > 0){
              vm.listTopPagePart(_.orderBy(result,['flowCode'],['asc']));
              if(vm.toppageSelectedCode() === ''){
                vm.toppageSelectedCode(vm.listTopPagePart()[0].flowCode);
              }
            }
          } else {
            // Do nothing
          }
        });
    }

    private getFlowmenuUpload() {
      const vm = this;
      const data = {
        topPageCd: vm.topPageCode(),
        layoutType: 1,
      };
      return vm.$ajax('/toppage/changeFlowMenu', data)
        .then((result: any) => {
          if (result) {
              vm.listTopPagePart(_.orderBy(result,['flowCode'],['asc']));
            }
        });
    }

    private getFlowmenu() {
      const vm = this;
      const data = {
        topPageCd: vm.topPageCode(),
        layoutType: 0,
      };
      return vm.$ajax('/toppage/changeFlowMenu', data)
        .then((result: any) => {
          if (result) {
              vm.listFlowMenu(_.orderBy(result,['flowCode'],['asc']));
            }
        });
    }

    private checkDataLayout(params: any) {
      const vm = this;
      if (params && params.frame === 1) {
        vm.layoutNo(LayoutType.FLOW_MENU);
      }
      const layoutRquest = {
        topPageCode: vm.topPageCode(),
        layoutNo: vm.layoutNo()
      }
      vm.$blockui("grayout");
      vm.$ajax('/toppage/getLayout', layoutRquest)
        .then((result: any) => {
          if (result) {
            vm.isNewMode(false)
            vm.layoutType(result.layoutType);
            if (result.flowMenuCd && vm.layoutType() === 0) {
              const flowMenuChoose = _.findIndex(vm.listFlowMenu(), (item: FlowMenuItem) => { return item.flowCode === result.flowMenuCd });
              vm.flowMenuSelectedCode(vm.listFlowMenu()[flowMenuChoose].flowCode);
            }
            if (result.flowMenuUpCd && vm.layoutType() === 1) {
              const topPagePartChoose = _.findIndex(vm.listTopPagePart(), (item: TopPagePartItem) => { return item.flowCode === result.flowMenuUpCd });
              vm.toppageSelectedCode(vm.listTopPagePart()[topPagePartChoose].flowCode);
            }
            if(result.url && result.url !== '') {
              vm.url(result.url);
            }
            vm.urlIframe3(result.url);
          } else {
            vm.isNewMode(true);
            vm.layoutType(0);
          }
        })
        .always(() => vm.$blockui("clear"));
    }

    private renderHTML(htmlSrc: string, frame: any) {
      const vm = this;
      let $iframe: any;
      if(frame === 'frame1') {
       $iframe = $("#frameF1");
      } else {
        $iframe = $("#frameF2");
      } 
      // If browser supports srcdoc for iframe
      // then add src to srcdoc attr
      if ("srcdoc" in $iframe) {
        $iframe.attr("srcdoc", htmlSrc);
      } else {
        // Fallback to IE... (doesn't support srcdoc)
        // Write directly into iframe body
        const ifr = document.getElementById('frameF1');
        const iframedoc = (ifr as any).contentDocument || (ifr as any).contentWindow.document;
        iframedoc.body.innerHTML = htmlSrc;
        const width = iframedoc.activeElement.scrollWidth + 20;
        const height = iframedoc.activeElement.scrollHeight + 20;
        (ifr as any).width = `${width.toString()}px`;
        (ifr as any).height = `${height.toString()}px`;
      }
      nts.uk.com.view.ccg034.share.model.customload.loadLimitedLabelForIframe();
    }

    checkSaveLayout() {
      const vm = this;
      console.log();
      
      if (vm.layoutType() === LayoutType.EXTERNAL_URL) {
        vm.$errors('clear');
        vm.$validate()
          .then((valid: boolean) => {
            if (valid) {
              vm.saveLayout();
            }
          });
      } else {
        vm.saveLayout();
      }
    }

    saveLayout() {
      const vm = this;
      if(vm.layoutType() !== LayoutType.FLOW_MENU){
        vm.flowMenuSelectedCode('');
      }
      if(vm.layoutType() !== LayoutType.FLOW_MENU_UPLOAD) {
        vm.toppageSelectedCode('');
      }
      if(vm.layoutType() !== LayoutType.EXTERNAL_URL) {
        vm.url('');
      }
      const data: any = {
        widgetSettings: null,
        topPageCode: vm.topPageCode(),
        layoutNo: vm.layoutNo(),
        layoutType: vm.layoutType(),
        cid: null,
        flowMenuCd: vm.flowMenuSelectedCode(),
        flowMenuUpCd: vm.toppageSelectedCode(),
        url: vm.url()
      };
      vm.$blockui("grayout");
      vm.$ajax('/toppage/saveLayoutFlowMenu', data)
        .then(() => {
          vm.isNewMode(false);
          vm.$blockui('clear');
          vm.$dialog.info({ messageId: "Msg_15" }).then(() => $('#D2_2').focus());
        })
        .always(() => vm.$blockui('clear'));
    }

 

    // URLの内容表示するを
    showUrl() {
      const vm = this;
      vm.urlIframe3(vm.url());
    }

    close() {
      const vm = this;
      vm.$window.close();
    }
  }

  class ItemModel {
    code: number;
    name: string;

    constructor(code: number, name: string) {
      this.code = code;
      this.name = name;
    }
  }

  interface FlowMenuItem {
    fileId: string;
    flowCode: string;
    flowName: string;
  }

  interface TopPagePartItem {
    fileId: string;
    flowCode: string;
    flowName: string;
  }

  export enum LayoutType {
    FLOW_MENU = 0,
    FLOW_MENU_UPLOAD = 1,
    EXTERNAL_URL = 2,
  }
}