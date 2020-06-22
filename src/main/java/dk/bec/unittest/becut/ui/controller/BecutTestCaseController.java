package dk.bec.unittest.becut.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.model.ExternalCallIteration;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.testcase.model.ParameterLiteral;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.ExternalCallDisplayable;
import dk.bec.unittest.becut.ui.model.ExternalCallIterationDisplayable;
import dk.bec.unittest.becut.ui.model.ParameterDisplayable;
import dk.bec.unittest.becut.ui.model.PostConditionDisplayable;
import dk.bec.unittest.becut.ui.model.PreConditionDisplayable;
import dk.bec.unittest.becut.ui.model.UnitTest;
import dk.bec.unittest.becut.ui.model.UnitTestTreeObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class BecutTestCaseController implements Initializable {

	@FXML
	private TreeTableView<UnitTestTreeObject> unitTestTreeTableView;

	@FXML
	private TreeTableColumn<UnitTestTreeObject, String> name;

	@FXML
	private TreeTableColumn<UnitTestTreeObject, String> type;

	@FXML
	private TreeTableColumn<UnitTestTreeObject, String> value;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		UnitTest currentUnitTest = BECutAppContext.getContext().getUnitTest();
		TreeItem<UnitTestTreeObject> root = new TreeItem<UnitTestTreeObject>(currentUnitTest);
		unitTestTreeTableView.setRoot(root);
		unitTestTreeTableView.setEditable(true);
		name.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
		type.setCellValueFactory(param -> param.getValue().getValue().typeProperty());
		value.setCellValueFactory(param -> {
			return param.getValue().getValue().valueProperty();
		});

		value.setCellFactory(
				new Callback<TreeTableColumn<UnitTestTreeObject, String>, TreeTableCell<UnitTestTreeObject, String>>() {

					@Override
					public TreeTableCell<UnitTestTreeObject, String> call(
							TreeTableColumn<UnitTestTreeObject, String> param) {
						return new TextFieldTreeTableCell<UnitTestTreeObject, String>(new DefaultStringConverter()) {

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								boolean isEditable = true;

								if (getTreeTableRow() != null) {
									UnitTestTreeObject currentItem = getTreeTableRow().getItem();
									// Check if cell should be editable or not
									if (currentItem instanceof ParameterDisplayable) {
										ParameterDisplayable parameterDisplayable = (ParameterDisplayable) getTreeTableRow()
												.getItem();
										if (parameterDisplayable.getParameter() instanceof ParameterLiteral
												|| DataType.EIGHTYEIGHT.equals(parameterDisplayable.getParameter().getDataType())
												|| DataType.GROUP.equals(parameterDisplayable.getParameter().getDataType())
												|| parameterDisplayable.getParameter().getIsSeventySeven()
												|| parameterDisplayable.getParameter().getName().equals("FILLER")
												) {
//									getTreeTableRow().setStyle("-fx-background-color:lightgrey");
											isEditable = false;
										}
									} else if (currentItem == null || currentItem instanceof ExternalCallDisplayable
											|| currentItem instanceof PreConditionDisplayable
											|| currentItem instanceof PostConditionDisplayable
											|| currentItem.getClass().isAnonymousClass()) {
//								getTreeTableRow().setStyle("-fx-background-color:lightgrey");
										isEditable = false;
									} else {
//								getTreeTableRow().setStyle("-fx-background-color:white");
										isEditable = true;
									}

								}
								getTableColumn().setOnEditCommit(event -> {
									event.getRowValue().getValue().updateValue(event.getNewValue());
								});
								setEditable(isEditable);
							};

						};
					}
				});

		currentUnitTest.becutTestCaseProperty().addListener(new ChangeListener<BecutTestCase>() {
			public void changed(ObservableValue<? extends BecutTestCase> observable, BecutTestCase oldValue,
					BecutTestCase newValue) {
				populateRoot(newValue);
			};
		});
	}

	private void populateRoot(BecutTestCase becutTestCase) {
		unitTestTreeTableView.getRoot().getValue().setName("Test Case: " + becutTestCase.getTestCaseName());
		;
		unitTestTreeTableView.getRoot().getValue().setValue(becutTestCase.getTestCaseId());
		;
		unitTestTreeTableView.getRoot().getChildren().clear();

		TreeItem<UnitTestTreeObject> preConditionHeader = new TreeItem<UnitTestTreeObject>(
				new UnitTestTreeObject("Preconditions", "", "") {
				});
		TreeItem<UnitTestTreeObject> externalCallHeader = new TreeItem<UnitTestTreeObject>(
				new UnitTestTreeObject("External Calls", "", "") {
				});
		TreeItem<UnitTestTreeObject> postConditionHeader = new TreeItem<UnitTestTreeObject>(
				new UnitTestTreeObject("Postconditions", "", "") {
				});

		unitTestTreeTableView.getRoot().getChildren().add(preConditionHeader);
		unitTestTreeTableView.getRoot().getChildren().add(externalCallHeader);
		unitTestTreeTableView.getRoot().getChildren().add(postConditionHeader);

		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("File Section"),
				becutTestCase.getPreCondition().getFileSection());
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("Working Storage"),
				becutTestCase.getPreCondition().getWorkingStorage());
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("Local Storage"),
				becutTestCase.getPreCondition().getLocalStorage());
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("Linkage Section"),
				becutTestCase.getPreCondition().getLinkageSection());

		for (ExternalCall externalCall : becutTestCase.getExternalCalls()) {
			populateUnitTestParts(externalCallHeader, new ExternalCallDisplayable(externalCall), externalCall);
		}

		populateUnitTestParts(postConditionHeader, new PostConditionDisplayable("File Section"),
				becutTestCase.getPostCondition().getFileSection());
		populateUnitTestParts(postConditionHeader, new PostConditionDisplayable("Working Storage"),
				becutTestCase.getPostCondition().getWorkingStorage());
		populateUnitTestParts(postConditionHeader, new PostConditionDisplayable("Local Storage"),
				becutTestCase.getPostCondition().getLocalStorage());
		populateUnitTestParts(postConditionHeader, new PostConditionDisplayable("Linkage Section"),
				becutTestCase.getPostCondition().getLinkageSection());
	}

	private void populateUnitTestParts(TreeItem<UnitTestTreeObject> parent, UnitTestTreeObject treeObject,
			List<Parameter> parameters) {
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(treeObject);
		for (Parameter parameter : parameters) {
			treeItem.getChildren().add(populateParameters(parameter));
		}
		parent.getChildren().add(treeItem);
	}

	private void populateUnitTestParts(TreeItem<UnitTestTreeObject> parent, UnitTestTreeObject treeObject,
			ExternalCall externalCall) {
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(treeObject);
		for (ExternalCallIteration callIteration : externalCall.getIterations().values()) {
			ExternalCallIterationDisplayable callIterationDisplayable = new ExternalCallIterationDisplayable(
					callIteration);
			TreeItem<UnitTestTreeObject> ti = new TreeItem<UnitTestTreeObject>(callIterationDisplayable);
			treeItem.getChildren().add(ti);
			for (Parameter parameter : callIteration.getParameters()) {
				ti.getChildren().add(populateParameters(parameter));
			}
		}
		parent.getChildren().add(treeItem);
	}

	private TreeItem<UnitTestTreeObject> populateParameters(Parameter parameter) {
		ParameterDisplayable parameterDisplayable = new ParameterDisplayable(parameter);
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(parameterDisplayable);
		for (Parameter p : parameter.getSubStructure()) {
			treeItem.getChildren().add(populateParameters(p));
		}
		return treeItem;
	}
}
