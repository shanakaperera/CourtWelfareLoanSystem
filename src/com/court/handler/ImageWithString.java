/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import java.nio.file.Path;
import javafx.scene.image.Image;

/**
 *
 * @author Shanaka P
 */
public class ImageWithString {

    private Image img;
    private Path img_path;

    public ImageWithString() {
    }

    
    public ImageWithString(Image img, Path img_path) {
        this.img = img;
        this.img_path = img_path;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public Path getImg_path() {
        return img_path;
    }

    public void setImg_path(Path img_path) {
        this.img_path = img_path;
    }

}
