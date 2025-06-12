package com.example.demo3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class HelloApplication extends Application {
    private TextArea textArea = new TextArea();
    private Label statusBar = new Label("Ready");
    private File currentFile = null;
    private double zoom = 1.0;
    private boolean bold = false;
    private boolean italic = false;
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        Scene scene = new Scene(root, 900, 600);

        textArea.setWrapText(true);
        textArea.setFont(Font.font("Consolas", 14));
        textArea.textProperty().addListener((obs, oldVal, newVal) -> updateStatus());

        MenuBar menuBar = new MenuBar();

        // === File Menu ===
        Menu fileMenu = new Menu("File");
        fileMenu.setStyle("-fx-background-color: #ffcccb;");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");

        newItem.setOnAction(e -> {
            textArea.clear();
            currentFile = null;
            statusBar.setText("New File Created");
        });

        openItem.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    textArea.clear();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.appendText(line + "\n");
                    }
                    currentFile = file;
                    statusBar.setText("Opened: " + file.getName());
                } catch (IOException e1) {
                    showError("Could not open file.");
                }
            }
        });

        saveItem.setOnAction(event -> {
            if (currentFile == null) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                currentFile = chooser.showSaveDialog(stage);
            }
            if (currentFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                    writer.write(textArea.getText());
                    statusBar.setText("Saved: " + currentFile.getName());
                } catch (IOException e1) {
                    showError("Could not save file.");
                }
            }
        });

        exitItem.setOnAction(e -> stage.close());
        fileMenu.getItems().addAll(newItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);

        // === Edit Menu ===
        Menu editMenu = new Menu("Edit");
        editMenu.setStyle("-fx-background-color: #d0f0c0;");
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem selectAllItem = new MenuItem("Select All");
        MenuItem clearItem = new MenuItem("Clear All");

        cutItem.setOnAction(e -> textArea.cut());
        copyItem.setOnAction(e -> textArea.copy());
        pasteItem.setOnAction(e -> textArea.paste());
        selectAllItem.setOnAction(e -> textArea.selectAll());
        clearItem.setOnAction(e -> textArea.clear());

        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, new SeparatorMenuItem(), selectAllItem, clearItem);

        // === View Menu ===
        Menu viewMenu = new Menu("View");
        viewMenu.setStyle("-fx-background-color: #add8e6;");
        CheckMenuItem wrapItem = new CheckMenuItem("Word Wrap");
        MenuItem zoomInItem = new MenuItem("Zoom In");
        MenuItem zoomOutItem = new MenuItem("Zoom Out");

        wrapItem.setSelected(true);
        wrapItem.setOnAction(e -> textArea.setWrapText(wrapItem.isSelected()));
        zoomInItem.setOnAction(e -> {
            zoom += 0.1;
            updateTextAreaFont();
        });
        zoomOutItem.setOnAction(e -> {
            zoom = Math.max(0.5, zoom - 0.1);
            updateTextAreaFont();
        });

        Menu bgColorMenu = new Menu("Background Colors");
        MenuItem whiteBg = new MenuItem("White");
        MenuItem yellowBg = new MenuItem("Light Yellow");
        MenuItem blueBg = new MenuItem("Light Blue");
        MenuItem greenBg = new MenuItem("Light Green");
        MenuItem grayBg = new MenuItem("Light Gray");
        MenuItem pinkBg = new MenuItem("Pink");
        MenuItem cyanBg = new MenuItem("Cyan");
        MenuItem orangeBg = new MenuItem("Orange");

        whiteBg.setOnAction(e -> setTextAreaBackground(Color.WHITE));
        yellowBg.setOnAction(e -> setTextAreaBackground(Color.LIGHTYELLOW));
        blueBg.setOnAction(e -> setTextAreaBackground(Color.LIGHTBLUE));
        greenBg.setOnAction(e -> setTextAreaBackground(Color.LIGHTGREEN));
        grayBg.setOnAction(e -> setTextAreaBackground(Color.LIGHTGRAY));
        pinkBg.setOnAction(e -> setTextAreaBackground(Color.PINK));
        cyanBg.setOnAction(e -> setTextAreaBackground(Color.CYAN));
        orangeBg.setOnAction(e -> setTextAreaBackground(Color.ORANGE));

        bgColorMenu.getItems().addAll(whiteBg, yellowBg, blueBg, greenBg, grayBg, pinkBg, cyanBg, orangeBg);
        viewMenu.getItems().addAll(wrapItem, zoomInItem, zoomOutItem, new SeparatorMenuItem(), bgColorMenu);

        // === Format Menu ===
        Menu formatMenu = new Menu("Format");
        MenuItem increaseFont = new MenuItem("Increase Font Size");
        MenuItem decreaseFont = new MenuItem("Decrease Font Size");
        CheckMenuItem boldToggle = new CheckMenuItem("Bold");
        CheckMenuItem italicToggle = new CheckMenuItem("Italic");
        CheckMenuItem underlineToggle = new CheckMenuItem("Underline");
        CheckMenuItem strikethroughToggle = new CheckMenuItem("Strikethrough");



        increaseFont.setOnAction(e -> {
            zoom += 0.1;
            updateTextAreaFont();
        });
        decreaseFont.setOnAction(e -> {
            zoom = Math.max(0.5, zoom - 0.1);
            updateTextAreaFont();
        });
        boldToggle.setOnAction(e -> {
            bold = boldToggle.isSelected();
            updateTextAreaFont();
        });
        italicToggle.setOnAction(e -> {
            italic = italicToggle.isSelected();
            updateTextAreaFont();
        });

        formatMenu.getItems().addAll(increaseFont, decreaseFont, boldToggle, italicToggle);

        // === Theme Menu ===
        Menu themeMenu = new Menu("Theme");
        MenuItem lightTheme = new MenuItem("Light Theme");
        MenuItem darkTheme = new MenuItem("Dark Theme");
        MenuItem blueTheme = new MenuItem("Blue Theme");
        MenuItem greenTheme = new MenuItem("Green Theme");


        lightTheme.setOnAction(e -> {
            root.setStyle("-fx-background-color: white;");
            textArea.setStyle("-fx-text-fill: black; -fx-control-inner-background: white;");
        });
        darkTheme.setOnAction(e -> {
            root.setStyle("-fx-background-color: #2b2b2b;");
            textArea.setStyle("-fx-text-fill: white; -fx-control-inner-background: #3c3f41;");
        });

        themeMenu.getItems().addAll(lightTheme, darkTheme);

        // === Help Menu ===
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        MenuItem helpItem = new MenuItem("How to Use");

        aboutItem.setOnAction(e -> showInfo("JavaFX Text Editor", "Created by M. Harini Mohan\nBuilt using JavaFX."));
        helpItem.setOnAction(e -> showInfo("Help", "Use the File menu to create, open, or save text files.\nUse Edit for basic text editing.\nUse View, Format, and Theme to customize appearance."));

        helpMenu.getItems().addAll(aboutItem, helpItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, formatMenu, themeMenu, helpMenu);

        root.setTop(menuBar);
        root.setCenter(textArea);
        root.setBottom(statusBar);

        stage.setTitle("HARINI JAVAFX EDITOR");
        stage.setScene(scene);
        stage.show();
    }

    private void setTextAreaBackground(Color color) {
        String rgb = String.format("#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        textArea.setStyle("-fx-control-inner-background: " + rgb + ";");
    }

    private void updateTextAreaFont() {
        String weight = bold ? "bold" : "normal";
        String posture = italic ? "italic" : "normal";
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: " + (14 * zoom) +
                "px; -fx-font-weight: " + weight + "; -fx-font-style: " + posture + ";");
    }

    private void updateStatus() {
        int words = textArea.getText().isEmpty() ? 0 : textArea.getText().trim().split("\\s+").length;
        int chars = textArea.getText().length();
        statusBar.setText("Words: " + words + " | Characters: " + chars);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
