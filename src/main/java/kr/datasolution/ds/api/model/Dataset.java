package kr.datasolution.ds.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "data_info")
public class Dataset {

    @Id
    @Column(name = "ds_id")
    Integer dsId;

    @Column(name = "ds_table_name")
    String dsTableName;

    @Column(name = "ds_code")
    String dsCode;

    @Column(name = "ds_name")
    String dsName;

    @Column(name = "ds_desc")
    String dsDesc;

    @Column(name = "ds_ref")
    String dsRef;

    @Column(name = "ds_ver_major")
    BigDecimal dsVerMajor;

    @Column(name = "ds_ver_minor")
    BigDecimal dsVerMinor;

    @Column(name = "ds_source")
    String dsSource;

    @Column(name = "ds_period")
    String dsPeriod;

    @Column(name = "ds_start_ddtt")
    Timestamp dsStartDdtt;

    @Column(name = "ds_end_ddtt")
    Timestamp dsEndDdtt;

    @Column(name = "ds_count")
    Integer dsCount;

    @Column(name = "ds_size")
    Integer dsSize;

    @Column(name = "domain_id")
    Integer domainId;

    @Column(name = "inter_ddtt")
    Timestamp insertDdtt;

    @Column(name = "update_ddtt")
    Timestamp updateDdtt;
}
