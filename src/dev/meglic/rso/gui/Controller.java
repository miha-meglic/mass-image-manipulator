package dev.meglic.rso.gui;

import dev.meglic.rso.utils.ImageProcessing;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
	
	@FXML
	VBox root;
	
	@FXML
	TextField tfInFolder;
	@FXML
	TextField tfOutFolder;
	
	@FXML
	CheckBox cbResizeL;
	@FXML
	CheckBox cbResizeW;
	@FXML
	CheckBox cbScale;
	@FXML
	CheckBox cbGrayscale;
	
	@FXML
	Spinner<Integer> spLongedge;
	@FXML
	Spinner<Integer> spWidth;
	@FXML
	Spinner<Double> spScale;
	
	
	/*
	Initialization method for FXML
	 */
	@FXML
	public void initialize() {
		initSpinners();
	}
	
	/*
	Sets ValueFactories for spinners (not working in FXML)
	 */
	private void initSpinners() {
		spLongedge.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 8000, 800, 100));
		spWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 8000, 800, 100));
		spScale.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 2.5, 0.5, 0.1));
	}
	
	/*
	Handles Process button click
	1. Data validation
	2. Image processing
	 */
	@FXML
	private void handleProcess() {
		// Validation - CheckBox
		if (cbResizeL.isSelected() | cbResizeW.isSelected() | cbScale.isSelected()) {
			// Verify only one of the options is selected
			if (handleResizeScale())
				return;
		}
		// Validation - IO Folders (have to be selected)
		if (tfInFolder.getText().trim().isEmpty() || tfOutFolder.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING,
					"Please select IN and OUT folders!", ButtonType.OK);
			alert.setHeaderText(null);
			alert.showAndWait();
			return;
		}
		// Validation - IO Folders (aren't the same)
		if (tfInFolder.getText().trim().equalsIgnoreCase(tfOutFolder.getText().trim())) {
			Alert alert = new Alert(Alert.AlertType.WARNING,
					"Please select different IN and OUT folders!", ButtonType.OK);
			alert.setHeaderText(null);
			alert.showAndWait();
			return;
		}
		
		// Processing
		Task process = new Task() {
			@Override
			protected Object call () throws Exception {
				File inFolder = new File(tfInFolder.getText());
				File outFoler = new File(tfOutFolder.getText());
				if (inFolder.isDirectory() && outFoler.isDirectory()) {
					ArrayList<File> images = new ArrayList<File>();
					File[] files = inFolder.listFiles();
					
					// Sort out only .jpg .png and .jpeg files
					if (files != null) {
						for (File f : files) {
							if (f.isFile()){
								String fEnd = f.getName().substring(f.getName().lastIndexOf('.')).toLowerCase();
								if (fEnd.equals(".jpg") || fEnd.equals(".png") || fEnd.equals(".jpeg"))
									images.add(f);
							}
						}
					}
					
					try {
						for (File f : images) {
							BufferedImage img = ImageIO.read(f);
							
							if (cbResizeL.isSelected() && img != null)
								img = ImageProcessing.resizeLongedge(img, spLongedge.getValue());
							if (cbResizeW.isSelected() && img != null)
								img = ImageProcessing.resizeWidth(img, spWidth.getValue());
							if (cbScale.isSelected() && img != null)
								img = ImageProcessing.scale(img, spScale.getValue());
							if (cbGrayscale.isSelected() && img != null)
								img = ImageProcessing.grayscale(img);
							
							if (img != null)
								ImageIO.write(img, f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase(),
										new File(outFoler.getPath()+"\\"+f.getName()));
						}
					} catch (IOException e) {
						Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong!", ButtonType.OK);
						alert.setHeaderText(null);
						alert.showAndWait();
					}
				}
				return null;
			}
		};
		
		// Start processing thread
		new Thread(process).start();
	}
	
	/*
	Folder selection
	Opens pop-up explorer
	 */
	@FXML
	private void handleInFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select IN Folder:");
		File selectedDir = chooser.showDialog(root.getScene().getWindow());
		
		if (tfInFolder != null)
			tfInFolder.setText(selectedDir.getPath());
	}
	
	/*
	Folder selection
	Opens pop-up explorer
	 */
	@FXML
	private void handleOutFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select OUT Folder:");
		File selectedDir = chooser.showDialog(root.getScene().getWindow());
		
		if (tfOutFolder != null)
			tfOutFolder.setText(selectedDir.toString());
	}
	
	
	/*
	Makes sure conflicting options can't be selected
	Opens a pop-up with a warning and deselects all options
	 */
	@FXML
	private boolean handleResizeScale () {
		if (cbResizeL.isSelected() && cbResizeW.isSelected() || cbResizeL.isSelected() && cbScale.isSelected()
				|| cbScale.isSelected() && cbResizeW.isSelected()) {
			Alert alert = new Alert(Alert.AlertType.WARNING,
					"You can only choose one resizing/scaling option!", ButtonType.OK);
			alert.setHeaderText(null);
			alert.showAndWait();
			
			cbResizeL.setSelected(false);
			cbResizeW.setSelected(false);
			cbScale.setSelected(false);
			
			return true;
		}
		return false;
	}
	
	@FXML
	private void exit() {
		Platform.exit();
	}
}
