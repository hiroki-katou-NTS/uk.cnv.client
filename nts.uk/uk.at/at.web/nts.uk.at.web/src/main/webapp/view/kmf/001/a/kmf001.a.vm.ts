module nts.uk.at.view.kmf001.a.viewmodel {
    export class ScreenModel {

        constructor() {
            var self = this;

        }

        startPage(): JQueryPromise<any> {
            var self = this;
            setTimeout(() => $("#A2_3").focus(), 1000);
            var dfd = $.Deferred();
            dfd.resolve();
            return dfd.promise();
        }


        // 優先順位の設定
        public openKMF001B(): void {
            nts.uk.ui.windows.sub.modal('/view/kmf/001/b/index.xhtml');
        }


        // 年次有給休暇
        public openKMF001C(): void {
            nts.uk.request.jump("/view/kmf/001/c/index.xhtml", {});
        }
        // 積立年休
        public openKMF001D(): void {
            nts.uk.request.jump("/view/kmf/001/d/index.xhtml", {});
        }
        //Screen F, G
        public openKMF001F(): void {
            nts.uk.request.jump("/view/kmf/001/f/index.xhtml", {});
        }
        //Screen H, I
        public openKMF001H(): void {
            nts.uk.request.jump("/view/kmf/001/h/index.xhtml", {});
        }
        // 60H超休
        public openKMF001J(): void {
            nts.uk.request.jump("/view/kmf/001/j/index.xhtml", {});
        }
        // 看護介護休暇
        public openKMF001L(): void {
            nts.uk.request.jump("/view/kmf/001/l/index.xhtml", {});
        }
        // 特別休暇の設定
        public openKMF001N(): void {
          nts.uk.ui.windows.sub.modal("/view/kmf/001/n/index.xhtml", {});
        }
        
       public openKMK007B(): void {
           var self = this;
            nts.uk.ui.windows.setShared("KMK007_ITEM_ID", 5);      
             nts.uk.ui.windows.sub.modal("/view/kmk/007/b/index.xhtml", {});
           
        }
        public openKMK007(): void {
           var self = this;
            nts.uk.ui.windows.setShared("KMK007_ITEM_ID", 4);      
             nts.uk.ui.windows.sub.modal("/view/kmk/007/b/index.xhtml", {});
           
        }
       public openKMF004A(): void {
            nts.uk.request.jump("/view/kmf/004/a/index.xhtml", {});
        }
       public openKMK004I(): void {
            nts.uk.request.jump("/view/kmf/004/i/index.xhtml", {});
        } 
       
       public openKMF003(): void {
           nts.uk.request.jump("/view/kmf/003/a/index.xhtml", {});
       }


        // Export Excel
        public exportExcel() {
            nts.uk.at.view.kmf001.a.service.exportExcel().done(function(data) {

            }).fail(function(res: any) {
                nts.uk.ui.dialog.alertError(res).then(function() {
                    nts.uk.ui.block.clear();
                });
            }).always(() => {
                block.clear();
            });
        }

    }
}