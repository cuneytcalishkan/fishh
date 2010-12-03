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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author CUNEYT
 */
@Entity
@Table(name = "file")
public class FFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST,
        org.hibernate.annotations.CascadeType.MERGE})
    @ManyToMany(mappedBy = "fileList")
    private List<Peer> peerList;

    /**
     *The empty constructor used by the persistence API.
     */
    public FFile() {
        super();
    }

    /**
     * File to be shared
     *
     * @param name the name of the file to be shared
     */
    public FFile(String name) {
        this.name = name;
        this.peerList = new ArrayList<Peer>();
    }

    /**
     *  Get the id of the file
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id of the file
     * @param id the id of the file
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the name of the file
     * @return the file name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the file
     * @param name the name of the file
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the peers of the file
     * @return list of the peers of this FFile object
     */
    public List<Peer> getPeerList() {
        return peerList;
    }

    /**
     * Set the peers of the file
     * @param peerList the peers
     */
    public void setPeerList(List<Peer> peerList) {
        this.peerList = peerList;
    }

    /**
     * Adds a new Peer to the list of peers of this file
     * @param p Peer to be added to the list
     */
    public void addPeer(Peer p) {
        this.peerList.add(p);
    }

    /**
     * Removes the given Peer from the list of peers of the file
     * @param p the Peer object to be removed
     * @return true if this Peer exists and is removed, false otherwise
     */
    public boolean removePeer(Peer p) {
        return this.peerList.remove(p);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
