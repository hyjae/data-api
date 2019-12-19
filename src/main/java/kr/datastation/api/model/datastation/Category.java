package kr.datastation.api.model.datastation;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Data
@Entity
@Table(name = "category_info", schema = "datastation_a")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ctgr_id")
    Integer ctgrId;

    @Column(name = "ctgr_name")
    String ctgrName;

    @Column(name = "ctgr_code")
    String ctgrCode;

    @Column(name = "parent_id")
    Integer parentId;

    @OneToMany(mappedBy = "dsId") // look for an attribute(variable name) in the target entity(Dataset.class)
    Set<Dataset> datasets;

    @Column(name = "insert_ddtt")
    Timestamp insertDdtt;

    @Column(name = "update_ddtt")
    Timestamp updateDdtt;
}
