module cmm015.a {
    __viewContext.ready(function() { 
    var screenModel = new viewmodel.ScreenModel();
        screenModel.start().done(function(){
            __viewContext.bind(screenModel);
        });
    });
}