package com.ustadmobile.port.sharedse.p2p;

/**
 * Created by kileha3 on 05/03/2017.
 */

public abstract class P2PTask {

    private P2PNode node;

    private String downloadUri;

    private String destinationPath;

    private int downloadStatus;

    private int bytesDownloadedSoFar;

    private int downloadTotalBytes;

    private final Object bytesDownloadedLock = new Object();

    protected P2PTaskListener listener;



    /**
     * The task is getting an index of the available contents from another node
     */
    public static final int TYPE_INDEX = 0;

    /**
     * The task is getting course content from another node
     */
    public static final int TYPE_COURSE = 1;

    private int CURRENT_TASK_TYPE;




    public int getBytesDownloadedSoFar() {
        synchronized (bytesDownloadedLock) {
            return bytesDownloadedSoFar;
        }
    }

    protected void setBytesDownloadedSoFar(int bytesDownloadedSoFar) {
        synchronized (bytesDownloadedLock) {
            this.bytesDownloadedSoFar = bytesDownloadedSoFar;
        }
    }

    public int getDownloadTotalBytes() {
        return downloadTotalBytes;
    }

    public void setDownloadTotalBytes(int downloadTotalBytes) {
        this.downloadTotalBytes = downloadTotalBytes;
    }


    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }



    public P2PTask(P2PNode node, String downloadUri) {
        this.node = node;
        this.downloadUri = downloadUri;
    }

    public void setP2PTaskListener(P2PTaskListener listener) {
        this.listener = listener;
    }

    /**
     * Runs the task
     */
    public synchronized void start() {

    }




    public P2PNode getNode() {
        return node;
    }

    public void setNode(P2PNode node) {
        this.node = node;
    }

    /**
     * The file to download from the node as a relative path e.g. /path/to/file
     *
     * @return The file to be downloaded from the node
     */
    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    /**
     * The path to download the file to e.g. /path/to/file
     *
     * @return
     */
    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    protected void fireTaskEnded() {
        if(listener != null)
            listener.taskEnded(this);
    }

    public void setTaskType(int taskType){
        if(taskType==TYPE_COURSE){
            CURRENT_TASK_TYPE=TYPE_COURSE;
        }else if(taskType==TYPE_INDEX){
            CURRENT_TASK_TYPE=TYPE_INDEX;
        }
    }


    public int getTaskType(){
        return CURRENT_TASK_TYPE;
    }

    public abstract void disconnect(String [] currentConnectedNetwork);


}
