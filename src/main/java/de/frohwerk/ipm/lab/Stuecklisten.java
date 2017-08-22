package de.frohwerk.ipm.lab;

import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class Stuecklisten {

    public static final String TEST_FUNCTION = "STFC_CONNECTION";

    public static void main(String[] args) throws JCoException {
        final JCoDestination destination = JCoDestinationManager.getDestination("Standard");
        final JCoRepository repository = destination.getRepository();
        final JCoFunction function = checkNotNull(repository.getFunction("CSEP_MAT_BOM_READ"), "Function %s not found", "CSEP_MAT_BOM_READ");
        function.getImportParameterList().setValue("MATERIAL", "MS1700BABYKPL");
        function.getImportParameterList().setValue("PLANT", "0001");
        function.getImportParameterList().setValue("BOM_USAGE", "1");
        try {
            function.execute(destination);
            final JCoListMetaData listMetaData = function.getExportParameterList().getListMetaData();
            for (int i = 0; i < listMetaData.getFieldCount(); i++) {
                logger.info("Export parameter {}: {}",
                        listMetaData.getName(i),
                        function.getExportParameterList().getValue(i)
                );
            }
            final JCoTable stpo = function.getTableParameterList().getTable("T_STPO");
            if (!stpo.isEmpty()) do {
                final JCoRecordMetaData recordMetaData = stpo.getRecordMetaData();
                final StringBuilder record = new StringBuilder();
                for (int i = 0; i < recordMetaData.getFieldCount(); i++) {
                    final String fieldName = recordMetaData.getName(i);
                    record.append(fieldName).append(": ").append(stpo.getValue(fieldName)).append("\t");
                }
                logger.info("{}", record);
            } while (stpo.nextRow());
        } catch (AbapException ex) {
            logger.error("Function execution failed", ex);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Stuecklisten.class);
}
