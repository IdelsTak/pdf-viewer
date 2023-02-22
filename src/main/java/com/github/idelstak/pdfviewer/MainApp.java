/*
 * MIT License
 *
 * Copyright (c) 2023 Hiram K
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.idelstak.pdfviewer;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("PDF Viewer");
        stage.setScene(mainScene(stage));
        stage.show();
    }

    private static Scene mainScene(Stage stage) {
        var mainPane = new BorderPane(openPdfButton(stage));
        mainPane.setPrefSize(350, 150);

        return new Scene(mainPane);
    }

    private static Button openPdfButton(Stage stage) {
        var openButton = new Button("Open PDF file...");

        openButton.setOnAction(event -> MainApp.openPdfFile(event, stage));

        return openButton;
    }

    private static void openPdfFile(ActionEvent event, Stage stage) {
        if (selectedPdf(stage) != null) {
            new PDFViewer().showPDF(stage, selectedPdf(stage));
        }

        event.consume();
    }

    private static File selectedPdf(Stage stage) {
        var fileChooser = new FileChooser();
        
        fileChooser.setTitle("Open PDF");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PDF Files", "*.pdf"));
        
        return fileChooser.showOpenDialog(stage);
    }
}
