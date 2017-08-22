package de.frohwerk.ipm.material;

import de.frohwerk.ipm.core.*;

import java.util.List;

@JcoNamespace("BAPI_MATERIAL")
public interface MaterialService {
    @JcoFunction("GETLIST")
    @JcoTableResult(parameter = "MATNRLIST", type = MaterialRef.class)
    List<MaterialRef> getList(@JcoTableCriteria(parameter = "MATNRSELECTION", field = "MATNR") final Criteria... criteria);

    @JcoFunction("GET_DETAIL")
    @JcoExportResult(parameter = "MATERIAL_GENERAL_DATA")
    MaterialDetail getDetail(@JcoImportParameter("MATERIAL") final String matnr);
}
