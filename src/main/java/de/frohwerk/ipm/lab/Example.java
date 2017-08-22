package de.frohwerk.ipm.lab;

import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Example {

    public static final String TEST_FUNCTION = "STFC_CONNECTION";

    public static void main(String[] args) throws JCoException {
        final JCoDestination destination = JCoDestinationManager.getDestination("Standard");
        final JCoRepository repository = destination.getRepository();
        final JCoFunction function = checkNotNull(repository.getFunction("STFC_CONNECTION"), "Function %s not found", TEST_FUNCTION);
        function.getImportParameterList().setValue("REQUTEXT", "Hallo SAP!");
        try {
            function.execute(destination);
            logger.info("Function {} executed", TEST_FUNCTION);
            final JCoListMetaData listMetaData = function.getExportParameterList().getListMetaData();
            for (int i = 0; i < listMetaData.getFieldCount(); i++) {
                logger.info("Export parameter {}: {}",
                        listMetaData.getName(i),
                        function.getExportParameterList().getValue(i)
                );
            }
        } catch (AbapException ex) {
            logger.error("Function execution failed", ex);
        }
//        printCompanyCodeList(destination, repository);
        final JCoFunction materialGetList = checkNotNull(repository.getFunction("BAPI_MATERIAL_GETLIST"), "Function BAPI_MATERIAL_GETLIST not found");
        materialGetList.getImportParameterList().setValue("MAXROWS", 10);
        final JCoTable matnrSelection = materialGetList.getTableParameterList().getTable("MATNRSELECTION");
        matnrSelection.appendRow();
        matnrSelection.setValue("SIGN", "I");
        matnrSelection.setValue("OPTION", "EQ");
        matnrSelection.setValue("MATNR_LOW", "MS1700BABYKPL");
        materialGetList.execute(destination);
        final JCoTable matnrlist = materialGetList.getTableParameterList().getTable("MATNRLIST");
        for (int i = 0; i < matnrlist.getNumRows(); i++) {
            logger.info("Found material: {}\t{}", matnrlist.getString("MATERIAL"), matnrlist.getString("MATL_DESC"));
        }
        //
        // BAPI_MATERIAL_GET_DETAIL
        //
        final JCoFunction materialDetail = checkNotNull(repository.getFunction("BAPI_MATERIAL_GET_DETAIL"), "Function BAPI_MATERIAL_GET_DETAIL not found");
        materialDetail.getImportParameterList().setValue("MATERIAL", "MS1700BABYKPL");
        materialDetail.getImportParameterList().setValue("PLANT", "0001");
        materialDetail.execute(destination);
        final JCoStructure data = materialDetail.getExportParameterList().getStructure("MATERIAL_GENERAL_DATA");
        logger.info("Materialdetail: {} Basismengeneinheit: {} Gewicht: {}{} brutto {}{} netto",
                data.getString("MATL_DESC"),
                data.getString("BASE_UOM"),
                data.getString("GROSS_WT"),
                data.getString("UNIT_OF_WT"),
                data.getString("NET_WEIGHT"),
                data.getString("UNIT_OF_WT")
        );
        //
        // BAPI_MATERIAL_GETALL
        //
//        materialDetail.getImportParameterList().setValue("STORAGELOCATION", "0001");
    }

    @JcoService("BAPI_MATERIAL")
    interface MaterialService {
        @JcoFunction("GETLIST")
        @JcoResult("MATNRLIST")
        List<MaterialRef> getList(@JcoSelector("MATNR") final String filter);
    }

    @interface JcoFunction {
        String value();
    }

    @interface JcoService {
        String value();
    }

    @interface JcoType {
    }

    @interface JcoSelector {
        String value();
    }

    @interface JcoResult {
        String value();
    }

    @interface JcoProperty {
        String value();
    }

    class MaterialRef {
        public String getMatnr() {
            return matnr;
        }

        public void setMatnr(String matnr) {
            this.matnr = matnr;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @JcoProperty("MATERIAL")
        private String matnr;
        @JcoProperty("MATL_DESC")
        private String description;
    }

    // @JcoService
    // interface MaterialService {
    //     @Query("s")
    //     List<Material> getList()
    // }

    private static void printCompanyCodeList(JCoDestination destination, JCoRepository repository) throws JCoException {
        final JCoFunction companyCodeGetList = checkNotNull(repository.getFunction("BAPI_COMPANYCODE_GETLIST"), "Function BAPI_COMPANYCODE_GETLIST not found");
        companyCodeGetList.execute(destination);
        final JCoStructure result = companyCodeGetList.getExportParameterList().getStructure("RETURN");
        final JCoTable codes = companyCodeGetList.getTableParameterList().getTable("COMPANYCODE_LIST");
        for (int i = 0; i < codes.getNumRows(); i++) {
            codes.setRow(i);
            logger.info("{}\t{}", codes.getString("COMP_CODE"), codes.getString("COMP_NAME"));
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Example.class);
}
