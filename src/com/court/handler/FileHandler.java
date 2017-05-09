/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.filechooser.FileSystemView;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

/**
 *
 * @author Shanaka P
 */
public class FileHandler {

    public static final String MEMBER_IMG_PATH = "\\court_system\\img\\member_img\\";
    public static final String DOC_PDF_PATH = "\\court_system\\docs_pdf\\";
    public static final String MEMBER_DEFAULT_IMG = "/com/court/asserts/default.jpg";
    public static final String TITLEBAR_ICO = "/com/court/asserts/icon.png";
    public static final String LOADING_DEFAULT_GIF = "/com/court/asserts/loading.gif";
    public static final String LOADING_LOGIN_GIF = "/com/court/asserts/ellipsis.gif";

    public static final String USER_PATH = "\\court_system\\img\\user_img\\";

    /**
     * This method use to set member image to the image view and write it in my
     * documents directory
     *
     * @param fileChoosen
     * @param image_name
     * @param path
     * @return ImageWithString
     * @throws java.io.IOException
     */
    public static ImageWithString getImageBy(File fileChoosen, String image_name, String path) throws IOException {
        String copyPath = FileSystemView.getFileSystemView().
                getDefaultDirectory().getPath() + path + image_name + ".png";
        File copyingFile = new File(copyPath);
        Files.copy(fileChoosen.toPath(), copyingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Image image = new Image(copyingFile.toURI().toURL().toString());
        return new ImageWithString(image, copyingFile.toPath());
    }

    /**
     * This method use to copy chosen pdf file to given path
     *
     *
     * @param docChoosen
     * @param DOC_PDF_PATH
     * @param pdf_name
     * @return string value of copying path
     * @throws java.io.IOException
     */
    public static String copyPdfToLocation(File docChoosen, String DOC_PDF_PATH, String pdf_name) throws IOException {
        String copyPath = FileSystemView.getFileSystemView().
                getDefaultDirectory().getPath() + DOC_PDF_PATH + pdf_name + ".pdf";
        File copyingFile = new File(copyPath);
        Files.copy(docChoosen.toPath(), copyingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return copyPath;
    }

    /**
     * This method use to convert selected pdf file required page to FXImage
     *
     * @param page int page number
     * @param pdf PdfDecoder
     * @param imgV
     * @throws org.jpedal.exception.PdfException
     */
    public static void setGivenPdfPageAsImage(int page, PdfDecoder pdf, ImageView imgV) throws PdfException {
        if (page > pdf.getPageCount()) {
            return;
        }
        if (page < 1) {
            return;
        }
        imgV.setImage(new Image(LOADING_DEFAULT_GIF));
        Task<Image> imageDisplay = new Task<Image>() {

            {
                setOnSucceeded(e -> {
                    imgV.setImage(getValue());
                    pdf.closePdfFile();
                });
                setOnFailed(f -> getException().printStackTrace());
            }

            @Override
            protected Image call() throws Exception {
                return SwingFXUtils.toFXImage(pdf.getPageAsImage(page), null);
            }
        };

        Thread decodingThread = new Thread(imageDisplay, "imge-display");
        decodingThread.setDaemon(true);
        decodingThread.start();
    }

    /*
     * This method use to create all the directories software need
     *
     * @throws java.io.IOException
     *
     */
    public static void createSoftwarePath() throws IOException {

        String document_path = FileSystemView.getFileSystemView().
                getDefaultDirectory().getPath() + "\\court_system\\";
        if (!Files.isDirectory(Paths.get(document_path))) {
            Path path = Paths.get(document_path + "docs_pdf\\..\\img\\member_img\\..\\user_img");
            Files.createDirectories(path);
        }
    }

}
