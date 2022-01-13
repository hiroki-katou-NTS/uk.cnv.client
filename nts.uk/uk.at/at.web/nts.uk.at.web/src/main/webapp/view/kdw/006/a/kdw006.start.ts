module nts.uk.at.view.kdw006 {  
    __viewContext.ready(function() {
        let dataShare:any;
         this.transferred.ifPresent(data => {
            console.log(data);
             dataShare = data;
        });
        
        let screenModel = new viewmodel.ScreenModel(dataShare);
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            // let sidebar = 0;
            // if(dataShare){
            //     sidebar = dataShare.ShareObject || 0;
            // }
                
            $('#A1_5').focus();
        });
    });
}