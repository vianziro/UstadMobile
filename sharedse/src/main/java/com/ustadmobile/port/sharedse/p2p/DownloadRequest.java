package com.ustadmobile.port.sharedse.p2p;

/**
 * Created by kileha3 on 05/02/2017.
 */
public class DownloadRequest {

    String fileDestination;
    String fileSource;
    String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileDestination() {
        return fileDestination;
    }

    public void setFileDestination(String fileDestination) {
        this.fileDestination = fileDestination;
    }
    public String getFileSource() {
        return fileSource;
    }

    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }


}
