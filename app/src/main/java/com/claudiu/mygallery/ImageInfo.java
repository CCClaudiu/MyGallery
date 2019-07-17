package com.claudiu.mygallery;

public class ImageInfo {
    private String fileName;
    private String pathString;
    public  ImageInfo()
    {

    }
    public ImageInfo(String paramFileName,String paramPathString)
    {
        this.fileName=paramFileName;
        this.pathString=paramPathString;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPathString() {
        return pathString;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
    }
}
