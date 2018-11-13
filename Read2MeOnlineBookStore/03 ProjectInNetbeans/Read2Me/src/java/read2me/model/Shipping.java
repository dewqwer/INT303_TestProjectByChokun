/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read2me.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dew2018
 */
@Entity
@Table(name = "SHIPPING")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Shipping.findAll", query = "SELECT s FROM Shipping s")
    , @NamedQuery(name = "Shipping.findByShippingid", query = "SELECT s FROM Shipping s WHERE s.shippingid = :shippingid")
    , @NamedQuery(name = "Shipping.findByShippingcost", query = "SELECT s FROM Shipping s WHERE s.shippingcost = :shippingcost")
    , @NamedQuery(name = "Shipping.findByShippingdate", query = "SELECT s FROM Shipping s WHERE s.shippingdate = :shippingdate")
    , @NamedQuery(name = "Shipping.findByStatus", query = "SELECT s FROM Shipping s WHERE s.status = :status")})
public class Shipping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SHIPPINGID")
    private Long shippingid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SHIPPINGCOST")
    private int shippingcost;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SHIPPINGDATE")
    @Temporal(TemporalType.DATE)
    private Date shippingdate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "STATUS")
    private String status;
    @JoinColumn(name = "ADDRESSID", referencedColumnName = "ADDRESSID")
    @ManyToOne(optional = false)
    private Address addressid;
    @JoinColumn(name = "ORDERID", referencedColumnName = "ORDERID")
    @OneToOne(optional = false)
    private Orders orderid;

    public Shipping() {
    }

    public Shipping(Long shippingid) {
        this.shippingid = shippingid;
    }

    public Shipping(Long shippingid, int shippingcost, Date shippingdate, String status) {
        this.shippingid = shippingid;
        this.shippingcost = shippingcost;
        this.shippingdate = shippingdate;
        this.status = status;
    }

    public Long getShippingid() {
        return shippingid;
    }

    public void setShippingid(Long shippingid) {
        this.shippingid = shippingid;
    }

    public int getShippingcost() {
        return shippingcost;
    }

    public void setShippingcost(int shippingcost) {
        this.shippingcost = shippingcost;
    }

    public Date getShippingdate() {
        return shippingdate;
    }

    public void setShippingdate(Date shippingdate) {
        this.shippingdate = shippingdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Address getAddressid() {
        return addressid;
    }

    public void setAddressid(Address addressid) {
        this.addressid = addressid;
    }

    public Orders getOrderid() {
        return orderid;
    }

    public void setOrderid(Orders orderid) {
        this.orderid = orderid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (shippingid != null ? shippingid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shipping)) {
            return false;
        }
        Shipping other = (Shipping) object;
        if ((this.shippingid == null && other.shippingid != null) || (this.shippingid != null && !this.shippingid.equals(other.shippingid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "read2me.model.Shipping[ shippingid=" + shippingid + " ]";
    }
    
}
