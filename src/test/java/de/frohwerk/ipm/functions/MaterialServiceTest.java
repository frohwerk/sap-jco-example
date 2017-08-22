package de.frohwerk.ipm.functions;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import de.frohwerk.ipm.bom.BomPosition;
import de.frohwerk.ipm.bom.BomService;
import de.frohwerk.ipm.core.JcoServiceFactory;
import de.frohwerk.ipm.material.MaterialDetail;
import de.frohwerk.ipm.material.MaterialRef;
import de.frohwerk.ipm.material.MaterialService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.frohwerk.ipm.core.Criterias.exclude;
import static de.frohwerk.ipm.core.Criterias.include;
import static de.frohwerk.ipm.core.Option.CP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MaterialServiceTest {
    @Test
    public void stuff() throws Exception {
        final JCoDestination destination = JCoDestinationManager.getDestination("Standard");
        final JcoServiceFactory classUnderTest = new JcoServiceFactory(destination);
        final MaterialService materialService = classUnderTest.createService(MaterialService.class);
        final List<MaterialRef> materials = materialService.getList(include(CP, "MS1700*"), exclude(CP, "MS1700TEST*"));
        assertThat(materials, is(not(empty())));
        for (final MaterialRef ref : materials) {
            final MaterialDetail materialDetail = materialService.getDetail(ref.getMatnr());
            assertThat(materialDetail.getDescription(), is(notNullValue()));
        }
    }

    @Test
    public void bomService() throws Exception {
        final JCoDestination destination = JCoDestinationManager.getDestination("Standard");
        final JcoServiceFactory classUnderTest = new JcoServiceFactory(destination);
        final MaterialService materialService = classUnderTest.createService(MaterialService.class);
        final BomService bomService = classUnderTest.createService(BomService.class);
        final MaterialDetail komponente = materialService.getDetail("MS1700BABYKPL");
        logger.info("Ausgangsmaterial: {}", komponente.getDescription());
        final List<BomPosition> positions = bomService.read("MS1700BABYKPL", "0001", "1");
        for (BomPosition pos : positions) {
            final MaterialDetail material = materialService.getDetail(pos.getMatnr());
            logger.info("{} {} {}", pos.getKomponentenMenge(), pos.getKomponentenEinheit(), material.getDescription());
        }
        logger.info("BAPI_MAT_BOM_EXISTENCE_CHECK(MS1700MOLKE) => {}", bomService.hasBom("MS1700BABYKPL", "0001", "1"));
        logger.info("BAPI_MAT_BOM_EXISTENCE_CHECK(MS1700MOLKE) => {}", bomService.hasBom("MS1700MOLKE", "0001", "1"));
    }

    private static final Logger logger = LoggerFactory.getLogger(MaterialServiceTest.class);
}


