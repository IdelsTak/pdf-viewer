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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFViewer extends BorderPane {

    private static final String HEADER_TEXT = "Page %d of %d";
    private Scene scene;
    private Stage pdfStage;
    private String windowTitle;
    private Label header;
    private Button prevButton;
    private Button nextButton;
    private PDDocument document;
    private int pageNum;
    private int pages;
    private byte[] pdf;
    private final Window window;

    public PDFViewer(Window window) {
        this.window = window;
        
        var borderStroke = new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT
        );

        setBorder(new Border(borderStroke));
        setPadding(new Insets(5, 0, 5, 0));
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public byte[] getPdf() {
        return defensiveCopy(pdf);
    }

    public void showPDF(File selectedFile) {
        showPDF(toByteArray(selectedFile));
    }

    public void showPDF(byte[] pdf) {
        this.pdf = defensiveCopy(pdf);
        
        try {
            document = PDDocument.load(pdf);
            pages = document.getPages().getCount();
            
            if (pages == 1) {
                initializeSinglePage();
            } else {
                initializeMultiPage();
            }
            
            pdfStage.show();
            pageNum = 0;
            
            showPage();
        } catch (IOException e) {
            throw new PDFViewerException("Error rendering PDF", e);
        }
    }

    private static byte[] defensiveCopy(byte[] bytes) {
        return Arrays.copyOf(bytes, bytes.length);
    }

    private void initializeSinglePage() {
        Region parent = PDFViewer.this;
        parent.setPrefWidth(620);
        parent.setPrefHeight(920);

        initializeScene();

        this.setRight(null);
        this.setLeft(null);
        this.setTop(null);
    }

    private void initializeMultiPage() {
        Region parent = PDFViewer.this;
        parent.setPrefWidth(737);
        parent.setPrefHeight(965);

        initializeScene();

        nextButton = new PagingButton("\u00BB");
        nextButton.setOnAction(e -> nextPage());
        this.setRight(nextButton);
        prevButton = new PagingButton("\u00AB");
        prevButton.setOnAction(e -> previousPage());
        prevButton.setDisable(true);
        this.setLeft(prevButton);

        header = new Label();
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        this.setTop(header);
    }

    private void initializeScene() {
        if (scene == null) {
            scene = new Scene(this);
            pdfStage = new Stage();
            pdfStage.initModality(Modality.APPLICATION_MODAL);
            pdfStage.initOwner(window);
            pdfStage.setAlwaysOnTop(true);
            /*pdfStage.getIcons().add(ImagesMap.get("Citroen.png"));*/
            pdfStage.setScene(scene);
        }
    }

    private void previousPage() {
        pageNum--;
        newPage();
    }

    private void nextPage() {
        pageNum++;
        newPage();
    }

    private void newPage() {
        nextButton.setDisable(pageNum == pages - 1);
        prevButton.setDisable(pageNum == 0);
        showPage();
    }

    private void showPage() {
        try {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImage(pageNum);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageView imageView = new ImageView(fxImage);
            this.setCenter(imageView);
            if (pages > 1) {
                header.setText(String.format(HEADER_TEXT, pageNum + 1, pages));
            }
        } catch (IOException e) {
            Thread.currentThread().interrupt();
            throw new PDFViewerException("Error getting page", e);
        }
    }

    private byte[] toByteArray(File selectedFile) {
        try (FileInputStream fis = new FileInputStream(selectedFile)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            throw new PDFViewerException(e);
        }
    }

    private static class PagingButton extends Button {

        PagingButton(String text) {
            initialize(text);
        }

        private void initialize(String text) {
            setText(text);
            setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + "-fx-background-radius: 5em; "
                    + "-fx-min-width: 30px; " + "-fx-min-height: 30px;"
                    + "-fx-max-width: 30px; -fx-alignment: top-center;"
                    + "-fx-max-height: 30px; -fx-padding: 0px;");
            BorderPane.setAlignment(this, Pos.CENTER);
            BorderPane.setMargin(this, new Insets(15));
        }

    }

}
