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

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("PDF Viewer");
        stage.setScene(pdfDisplayScene(stage));
        stage.show();
    }

    private static Scene pdfDisplayScene(Window window) {
        var pdfViewer = new PDFViewer(window);
        var pdfFileChooser = new PdfFileChooser(window);
        var pdfFileChoice = new PdfFileChoice(pdfViewer, pdfFileChooser);
        
        EventHandler<ActionEvent> openPdfFileEvent = new OpenPdfFileEvent(pdfFileChoice);
        Node openPdfFileButton = new OpenPdfFileButton(openPdfFileEvent);
        Parent pdfDisplayPane = new PdfDisplayPane(openPdfFileButton);

        return new PdfDisplayScene(pdfDisplayPane);
    }

}
