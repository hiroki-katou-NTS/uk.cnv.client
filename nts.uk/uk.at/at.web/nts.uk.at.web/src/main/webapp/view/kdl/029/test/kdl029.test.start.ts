module nts.uk.at.view.kdl029.test {
__viewContext.ready(function() {
		service.getEmployeeList().done((data : any) => {
			let screenModel = new viewmodel.ScreenModel(data);
			__viewContext.bind(screenModel);
		})
    });
}