/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author Cuneyt
 */
@Entity
@Table(name = "peer")
public class Peer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "ADDRESS", nullable = false, unique = true)
    private String address;
    @Column(name = "SHAREPORT", nullable = false)
    private int sharePort;
    @Column(name = "PINGPORT", nullable = false)
    private int pingPort;
    @JoinTable(name = "filepeers", joinColumns = {
        @JoinColumn(name = "peerid", referencedColumnName = "id")},
    inverseJoinColumns = {
        @JoinColumn(name = "fileid", referencedColumnName = "id")})
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST,
        org.hibernate.annotations.CascadeType.MERGE})
    @ManyToMany
    private List<FFile> fileList;

    /**
     * The empty constructor used by the persistence API
     */
    public Peer() {
        super();
    }

    /**
     * The client who shares files in the system
     * @param address the IP address of the client
     * @param port the port on which Peer shares files
     * @param pingPort the port which the server pings for crash handling
     */
    public Peer(String address, int port, int pingPort) {
        this.address = address;
        this.sharePort = port;
        this.pingPort = pingPort;
        this.fileList = new ArrayList<FFile>();
    }

    /**
     * Gets the id of the Peer
     * @return the id of the Peer
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the Peer
     * @param id the new id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the port on which the client listens for server pings
     * @return the port number
     */
    public int getPingPort() {
        return pingPort;
    }

    /**
     * Sets the port on which the client listens for server pings
     * @param pingPort the port number
     */
    public void setPingPort(int pingPort) {
        this.pingPort = pingPort;
    }

    /**
     * Gets the IP address of the client
     * @return the IP address as plain string
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the IP address of the client
     * @param address the IP address as plain string
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the port number on which the client listens for file downloads
     * @return the port number
     */
    public int getSharePort() {
        return sharePort;
    }

    /**
     * Sets the port number on which the client listens for file downloads
     * @param port the port number
     */
    public void setSharePort(int port) {
        this.sharePort = port;
    }

    /**
     * Gets the list of files this Peer shares
     * @return the list of files
     */
    public List<FFile> getFileList() {
        return fileList;
    }

    /**
     * Sets the list of files this Peer shares
     * @param fileList the list of files
     */
    public void setFileList(List<FFile> fileList) {
        this.fileList = fileList;
    }

    /**
     * Removes the given file from the list of shared files of this Peer
     * @param f the FFile to remove
     * @return true if the given FFile exists and removed, false otherwise
     */
    public boolean removeFile(FFile f) {
        return this.fileList.remove(f);
    }

    /**
     * Adds the given file to the list of shared files of this Peer
     * @param f the FFile to be added to the shared files list
     */
    public void addFile(FFile f) {
        this.fileList.add(f);
    }

    @Override
    public String toString() {
        return address + "," + sharePort;
    }
}
