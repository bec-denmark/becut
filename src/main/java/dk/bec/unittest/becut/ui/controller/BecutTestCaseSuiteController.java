package dk.bec.unittest.becut.ui.controller;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;

import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.model.ExternalCallIteration;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.testcase.model.ParameterLiteral;
import dk.bec.unittest.becut.testcase.model.TestResult;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.ExternalCallDisplayable;
import dk.bec.unittest.becut.ui.model.ExternalCallIterationDisplayable;
import dk.bec.unittest.becut.ui.model.FileControlDisplayable;
import dk.bec.unittest.becut.ui.model.ParameterDisplayable;
import dk.bec.unittest.becut.ui.model.PostConditionDisplayable;
import dk.bec.unittest.becut.ui.model.PreConditionDisplayable;
import dk.bec.unittest.becut.ui.model.UnitTest;
import dk.bec.unittest.becut.ui.model.UnitTestSuite;
import dk.bec.unittest.becut.ui.model.UnitTestTreeObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class BecutTestCaseSuiteController implements Initializable {
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
		UnitTestSuite testSuite = BECutAppContext.getContext().getUnitTestSuite();
		TreeItem<UnitTestTreeObject> root = new TreeItem<UnitTestTreeObject>(testSuite);
		unitTestTreeTableView.setRoot(root);
		unitTestTreeTableView.setEditable(true);
		name.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
		type.setCellValueFactory(param -> param.getValue().getValue().typeProperty());
		value.setCellValueFactory(param -> {
			return param.getValue().getValue().valueProperty();
		});

		BECutAppContext.getContext().getEventBus().register(new Object() {
		    @Subscribe
		    public void event(ExternalCallLineEvent event) {
				LinkedList<TreeItem<UnitTestTreeObject>> queue = new LinkedList<>();
				queue.add(unitTestTreeTableView.getRoot());
				while(!queue.isEmpty()) {
					TreeItem<UnitTestTreeObject> node = queue.pop();
					if(node.getValue() instanceof ExternalCallDisplayable) {
						Integer line = ((ExternalCallDisplayable)node.getValue()).getExternalCall().getLineNumber();
						if(event.getLine().equals(line)) {
							node.setExpanded(true);
							TreeItem<UnitTestTreeObject> parent = node.getParent();
							while(parent != null) {
								parent.setExpanded(true);
								parent = parent.getParent();
							}
							unitTestTreeTableView.getSelectionModel().select(node);
							unitTestTreeTableView.scrollTo(unitTestTreeTableView.getSelectionModel().getSelectedIndex());
							return;
						}
					}
					queue.addAll(node.getChildren());
				}
		    }
		});
		
		unitTestTreeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            TreeItem<UnitTestTreeObject> selectedItem = newValue;
            if(selectedItem != null && selectedItem.getValue() instanceof ExternalCallDisplayable) {
            	ExternalCall ec = ((ExternalCallDisplayable)selectedItem.getValue()).getExternalCall();
            	BECutAppContext.getContext().getEventBus().post(new SourceLineEvent(ec.getLineNumber()));
            }
		});
		
		unitTestTreeTableView.setRowFactory(ttv -> {
			ContextMenu cmExternalCall = new ContextMenu();
			MenuItem dupExternalCall = new MenuItem("Duplicate");
			MenuItem delExternalCall = new MenuItem("Delete");
			cmExternalCall.getItems().addAll(dupExternalCall, delExternalCall);

			ContextMenu cmFileControl = new ContextMenu();
			MenuItem editFileControl = new MenuItem("Edit");
			cmFileControl.getItems().addAll(editFileControl);

			ContextMenu cmTestCase = new ContextMenu();
			MenuItem dupTestCase = new MenuItem("Duplicate");
			MenuItem delTestCase = new MenuItem("Delete");
			cmTestCase.getItems().addAll(dupTestCase, delTestCase);
			
			TreeTableRow<UnitTestTreeObject> row = new TreeTableRow<UnitTestTreeObject>() {
			   @Override
			   protected void updateItem(UnitTestTreeObject item, boolean empty) {
				   super.updateItem(item, empty);
				   if(item instanceof ExternalCallIterationDisplayable) {
					   this.setContextMenu(cmExternalCall);
				   } else if (item instanceof FileControlDisplayable) {
					   this.setContextMenu(cmFileControl);
					   this.setEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
						   if(e.getClickCount() == 2) {
							   openFileEditor(unitTestTreeTableView.getSelectionModel());
						   }
					   });
				   } else if (item instanceof UnitTest) {
					   this.setContextMenu(cmTestCase);
				   } else {
					   //seems that children inherit context menu from parent; it is undesirable here
					   this.setContextMenu(null);
					   //FIXME is this the right way to prevent dblclick for items other FileControlDisplayable?
					   this.setEventHandler(MouseEvent.MOUSE_CLICKED, null);
				   }
			   }				
			};

		    dupExternalCall.setOnAction(event -> {
		    	ExternalCallIteration callIteration = ((ExternalCallIterationDisplayable) row.getItem()).getExternalCallIteration();
		    	ExternalCallIteration copy = ExternalCallIteration.mkCopy(callIteration);

		    	TreeTableViewSelectionModel<UnitTestTreeObject> sm = unitTestTreeTableView.getSelectionModel();
		    	int index = sm.getSelectedIndex();
		    	TreeItem<UnitTestTreeObject> item = sm.getModelItem(index);
		    	TreeItem<UnitTestTreeObject> parent = item.getParent();
		    	
		    	Map<String, ExternalCallIteration> iterations = 
		    			((ExternalCallDisplayable) parent.getValue()).getExternalCall().getIterations();
		    	String name = String.format("iteration_%s", iterations.size());
		    	copy.setName(name);
		    	copy.setNumericalOrder(iterations.size());
		    	iterations.put(name, copy);
	
				populateUnitTestParts(parent, copy);
		    });
			
		    delExternalCall.setOnAction(event -> {
		    	TreeTableViewSelectionModel<UnitTestTreeObject> sm = unitTestTreeTableView.getSelectionModel();
		    	int index = sm.getSelectedIndex();
		    	TreeItem<UnitTestTreeObject> item = sm.getModelItem(index);
		    	TreeItem<UnitTestTreeObject> parent = item.getParent();
		    	List<TreeItem<UnitTestTreeObject>> items = parent.getChildren();
		    	//TODO when only iteration is removed call should have a context menu for adding one, 
		    	//for now let's just prevent deleting the only one 
		    	if(items.size() > 1) {
		    		parent.getChildren().remove(item);
		    	}
		    	
//		    	ExternalCallDisplayable ecd = (ExternalCallDisplayable) row.getItem();
//		    	currentUnitTest.getBecutTestCase().removeExternalCall(ecd.getExternalCall());
//		    	externalCallHeader.getChildren().remove(row.getItem());
		    });

		    editFileControl.setOnAction(event -> {
		    	openFileEditor(unitTestTreeTableView.getSelectionModel());
		    });
		    
		    dupTestCase.setOnAction(event -> {
		    	TreeTableViewSelectionModel<UnitTestTreeObject> sm = unitTestTreeTableView.getSelectionModel();
		    	TreeItem<UnitTestTreeObject> item = sm.getModelItem(sm.getSelectedIndex());
		    	assert item.getValue() instanceof UnitTest;
		    	try {
		    		String testCaseName = item.getValue().valueProperty().getValue();
		    		String newTestCaseName = testCaseName + "-copy";
		    		
		    		Path newTestCasePath = Paths.get(
		    				BECutAppContext.getContext().getUnitTestSuiteFolder().toString(),
		    				newTestCaseName);
		    		if (!Files.exists(newTestCasePath)) {
		    			Files.createDirectory(newTestCasePath);
		    		}

		    		Files.list(Paths.get(BECutAppContext.getContext().getUnitTestSuiteFolder().toString(), testCaseName))
		    			.filter(p -> !Files.isDirectory(p))
		    			.forEach(path -> {
		    				Path copyTo = Paths.get(newTestCasePath.toString(), path.getFileName().toString());
		    				try {
								Files.copy(path, copyTo);
							} catch (FileAlreadyExistsException e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("Warning Dialog");
								alert.setContentText(copyTo + " already exists.");
								alert.showAndWait();								
								return;
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
		    			});
		    		
					try(FileInputStream fileInputStream = new FileInputStream(Paths.get(newTestCasePath.toString(), "test_case.json").toFile())) {
						BecutTestCase becutTestCase = new ObjectMapper().readValue(fileInputStream, BecutTestCase.class);
						becutTestCase.setTestCaseName(newTestCaseName);
						testSuite.getBecutTestCaseSuite().get().add(becutTestCase);
						TreeItem<UnitTestTreeObject> testCaseNode = new TreeItem<>(new UnitTest(becutTestCase), 
								new ImageView(new Image(getClass().getResourceAsStream("unknown.png"))));
						addTestResultObserver(becutTestCase, testCaseNode);
						root.getChildren().add(testCaseNode);
						populateTestCaseNode(testCaseNode, becutTestCase);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		    });

		    delTestCase.setOnAction(event -> {
		    	TreeTableViewSelectionModel<UnitTestTreeObject> sm = unitTestTreeTableView.getSelectionModel();
		    	TreeItem<UnitTestTreeObject> item = sm.getModelItem(sm.getSelectedIndex());
		    	assert item.getValue() instanceof UnitTest;
		    	UnitTest ut = (UnitTest)item.getValue();
	    		String testCaseName = ut.getBecutTestCase().getTestCaseName();
	    		Path testCasePath = Paths.get(BECutAppContext.getContext().getUnitTestSuiteFolder().toString(), testCaseName);
		    	try {
		    		Files.walk(testCasePath)
		    			.sorted(Comparator.reverseOrder())
		    		    //.peek(System.out::println)
		    		    .forEach(p -> {
							try {
								Files.delete(p);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						});
					root.getChildren().remove(item);
					testSuite.getBecutTestCaseSuite().get().remove(ut.getBecutTestCase());
				} catch (NoSuchFileException e) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText(testCasePath + " doesn't exist.");
					alert.showAndWait();								
					return;
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					root.getChildren().remove(item);
					testSuite.getBecutTestCaseSuite().get().remove(ut.getBecutTestCase());
				}
		    });
		    
		    return row;
		});
		
		value.setCellFactory(
				new Callback<TreeTableColumn<UnitTestTreeObject, String>, TreeTableCell<UnitTestTreeObject, String>>() {
					@Override
					public TreeTableCell<UnitTestTreeObject, String> call(TreeTableColumn<UnitTestTreeObject, String> param) {
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
							    	TreeTableViewSelectionModel<UnitTestTreeObject> sm = unitTestTreeTableView.getSelectionModel();
							    	TreeItem<UnitTestTreeObject> item2 = sm.getModelItem(sm.getSelectedIndex());
							    	if(item2.getValue() instanceof UnitTest) {
								    	UnitTest ut = (UnitTest)item2.getValue();
								    	
								    	Path oldTestCasePath = Paths.get(
							    				BECutAppContext.getContext().getUnitTestSuiteFolder().toString(),
							    				event.getOldValue());
							    		Path newTestCasePath = Paths.get(
							    				BECutAppContext.getContext().getUnitTestSuiteFolder().toString(),
							    				event.getNewValue());
							    		
							    		try {
											Files.move(oldTestCasePath, newTestCasePath);
											ut.getBecutTestCase().setTestCaseName(event.getNewValue());
											event.getRowValue().getValue().updateValue(event.getNewValue());
							    		} catch (FileAlreadyExistsException e) {
											Alert alert = new Alert(AlertType.WARNING);
											alert.setTitle("Warning Dialog");
											alert.setContentText(newTestCasePath + " already exists.");
											alert.showAndWait();
											event.getTreeTableView().refresh();
											return;
										} catch (IOException e) {
											throw new RuntimeException(e);
										}
							    	} else {
							    		event.getRowValue().getValue().updateValue(event.getNewValue());
							    	}
								});
								setEditable(isEditable);
							};

						};
					}
				});

		testSuite.getBecutTestCaseSuite().addListener(
			(observable, oldValue, newValue) -> populateRoot(newValue)
		);
	}

	private void populateRoot(BecutTestCaseSuite becutTestCaseSuite) {
		TreeItem<UnitTestTreeObject> root = unitTestTreeTableView.getRoot(); 
		root.getChildren().clear();

		becutTestCaseSuite
			.stream()
			.forEach(becutTestCase -> {
				TreeItem<UnitTestTreeObject> testCaseNode = new TreeItem<>(new UnitTest(becutTestCase), 
						new ImageView(new Image(getClass().getResourceAsStream("unknown.png"))));
				addTestResultObserver(becutTestCase, testCaseNode);
				root.getChildren().add(testCaseNode);
				populateTestCaseNode(testCaseNode, becutTestCase);
			});
	}

	private void addTestResultObserver(BecutTestCase becutTestCase, final TreeItem<UnitTestTreeObject> testCaseNode) {
		BECutAppContext.getContext().getEventBus().register(new Object() {
		    @Subscribe
		    public void event(TestResult result) {
				if(becutTestCase == result.getTestCase()) {
					switch(result.getStatus()) {
					case OK :
						testCaseNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("ok.png"))));
						break;
					case NOK :
						testCaseNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("nok.png"))));
						break;
					default :
						testCaseNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("unknown.png"))));
					}
				};
		    }
		});
	}

	private void populateTestCaseNode(TreeItem<UnitTestTreeObject> node, BecutTestCase becutTestCase) {
		TreeItem<UnitTestTreeObject> fileControlHeader = new TreeItem<>(new UnitTestTreeObject("File Control"));
		TreeItem<UnitTestTreeObject> preConditionHeader = new TreeItem<>(new UnitTestTreeObject("Preconditions"));
		TreeItem<UnitTestTreeObject> externalCallHeader = new TreeItem<>(new UnitTestTreeObject("External Calls"));
		TreeItem<UnitTestTreeObject> postConditionHeader = new TreeItem<>(new UnitTestTreeObject("Postconditions"));

		node.getChildren().add(fileControlHeader);
		node.getChildren().add(preConditionHeader);
		node.getChildren().add(externalCallHeader);
		node.getChildren().add(postConditionHeader);

		populateUnitTestParts(fileControlHeader, becutTestCase.getFileControlAssignments());
		
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("File Section"),
				becutTestCase.getPreCondition().getFileSection());
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("Working Storage"),
				becutTestCase.getPreCondition().getWorkingStorage());
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("Local Storage"),
				becutTestCase.getPreCondition().getLocalStorage());
		populateUnitTestParts(preConditionHeader, new PreConditionDisplayable("Linkage Section"),
				becutTestCase.getPreCondition().getLinkageSection());

		for (ExternalCall externalCall : becutTestCase.getExternalCalls()) {
			populateUnitTestParts(externalCallHeader, externalCall);
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
	
	private void populateUnitTestParts(TreeItem<UnitTestTreeObject> parent, Map<String, String> getFileControlAssignments) {
		getFileControlAssignments.forEach((k, v) ->
			parent.getChildren().add(
					new TreeItem<UnitTestTreeObject>(new FileControlDisplayable(k, v))));
	}	
	
	private void populateUnitTestParts(TreeItem<UnitTestTreeObject> parent, UnitTestTreeObject treeObject,
			List<Parameter> parameters) {
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(treeObject);
		for (Parameter parameter : parameters) {
			treeItem.getChildren().add(populateParameters(parameter));
		}
		parent.getChildren().add(treeItem);
	}

	private void populateUnitTestParts(TreeItem<UnitTestTreeObject> parent, ExternalCall externalCall) {
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(new ExternalCallDisplayable(externalCall));
		for (ExternalCallIteration callIteration : externalCall.getIterations().values()) {
			populateUnitTestParts(treeItem, callIteration);
		}
		parent.getChildren().add(treeItem);
	}

	private void populateUnitTestParts(TreeItem<UnitTestTreeObject> parent, ExternalCallIteration callIteration) {
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(new ExternalCallIterationDisplayable(callIteration));
		for (Parameter parameter : callIteration.getParameters()) {
			treeItem.getChildren().add(populateParameters(parameter));
		}
		parent.getChildren().add(treeItem);
	}
	
	private TreeItem<UnitTestTreeObject> populateParameters(Parameter parameter) {
		TreeItem<UnitTestTreeObject> treeItem = new TreeItem<UnitTestTreeObject>(new ParameterDisplayable(parameter));
		for (Parameter p : parameter.getSubStructure()) {
			treeItem.getChildren().add(populateParameters(p));
		}
		return treeItem;
	}
	
	private void openFileEditor(TreeTableViewSelectionModel<UnitTestTreeObject> sm) {
    	TreeItem<UnitTestTreeObject> item = sm.getModelItem(sm.getSelectedIndex());
    	assert item.getValue() instanceof FileControlDisplayable;
    	try {
    		//TODO make it more complicated ;-), for now it is a name of the test case
    		String testCaseName = item.getParent().getParent().getValue().valueProperty().getValue();
    		
    		Path testCasePath = Paths.get(
    				BECutAppContext.getContext().getUnitTestSuiteFolder().toString(),
    				testCaseName);
    		if (!Files.exists(testCasePath)) {
    			Files.createDirectory(testCasePath);
    		}
    		
    		Path path = Paths.get(
    				testCasePath.toString(),
    				item.getValue().getValue() + ".txt");
    		if (!Files.exists(path)) {
    		    Files.createFile(path);
    		}
			Desktop.getDesktop().open(path.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
