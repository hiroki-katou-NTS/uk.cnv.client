module ccg030.b {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage();
        __viewContext.bind(screenModel);
    });
}