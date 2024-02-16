package com.codigo.msregistro.infraestructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "empresa")
public class EmpresaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private  Long idEmpresa;
    @Column(name = "num_docu", nullable = false, length = 15)
    private String numDocu;
    @Column(name = "razon_social")
    private  String razonSocial;
    @Column(name = "nom_comercial")
    private  String nomComercial;
    private int estado;
    @Column(name = "usua_cre")
    private  String usuaCrea;
    @Column(name = "date_create")
    private Timestamp dateCreate;
    @Column(name = "usua_modif", length = 45)
    private String usuaModif;

    @Column(name = "date_modif")
    private Timestamp dateModif;

    @Column(name = "usua_delet", length = 45)
    private String usuaDelet;

    @Column(name = "date_delet")
    private Timestamp dateDelet;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumentoEntity tipoDocumento;
}
