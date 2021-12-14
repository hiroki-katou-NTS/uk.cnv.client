__viewContext.ready(function() {
        var screenModel = new kdl005.test.viewmodel.ScreenModel();
        //screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);   
        //});
    });