package kr.datastation.api.model.datastation;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "dataset_info", schema = "datastation_a")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ds_id")
    Integer dsId;

    @ManyToOne
    @JoinColumn(name = "ctrg_id") // column name in DB
    Category category;

    @Column(name = "ds_table_name")
    String dsTableName;

    @Column(name = "ds_code")
    String dsCode;

    @Column(name = "ds_name")
    String dsName;

    @Column(name = "ds_desc")
    String dsDesc;

    @Column(name = "ds_keyword")
    String dsKeyword;

    @Column(name = "ds_ref")
    String dsRef;

    @Column(name = "ds_ver_major")
    BigDecimal dsVerMajor;

    @Column(name = "ds_ver_minor")
    BigDecimal dsVerMinor;

    @Column(name = "ds_format")
    String dsFormat;

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

    @Column(name = "insert_ddtt")
    Timestamp insertDdtt;

    @Column(name = "update_ddtt")
    Timestamp updateDdtt;
}
