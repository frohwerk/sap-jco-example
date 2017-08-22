package de.frohwerk.ipm.bom;

import com.sap.conn.jco.AbapException;
import de.frohwerk.ipm.core.*;

import java.util.List;

public interface BomService {
    @JcoFunction("CSEP_MAT_BOM_READ")
    @JcoTableResult(parameter = "T_STPO", type = BomPosition.class)
    List<BomPosition> read(@JcoImportParameter("MATERIAL") final String matnr,
                           @JcoImportParameter("PLANT") final String werk,
                           @JcoImportParameter("BOM_USAGE") final String verwendung) throws AbapException;

    @JcoFunction("BAPI_MAT_BOM_EXISTENCE_CHECK")
    @JcoExportResult(parameter = "RETURN")
    @JcoProperty("TYPE")
    char checkBom(@JcoImportParameter("MATERIAL") final String matnr,
                  @JcoImportParameter("PLANT") final String werk,
                  @JcoImportParameter("BOMUSAGE") final String verwendung) throws AbapException;
}
