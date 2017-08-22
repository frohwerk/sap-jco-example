package de.frohwerk.ipm.functions;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
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

import static com.google.common.base.Strings.repeat;
import static de.frohwerk.ipm.core.Criterias.exclude;
import static de.frohwerk.ipm.core.Criterias.include;
import static de.frohwerk.ipm.core.Option.CP;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MaterialServiceTest {
    public MaterialServiceTest() throws JCoException {
        this.destination = JCoDestinationManager.getDestination("Standard");
        this.jcoServiceFactory = new JcoServiceFactory(destination);
        this.materialService = jcoServiceFactory.createService(MaterialService.class);
        this.bomService = jcoServiceFactory.createService(BomService.class);
    }

    @Test
    public void stuff() throws Exception {
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
        logger.info("Ausgangsmaterial:");
        logger.info("{} {} {} {} {}", komponente.getDescription(), 1, komponente.getBasismengeneinheit(), komponente.getNettogewicht(), komponente.getGewichtseinheit());
        resolveBom(1, "MS1700BABYKPL", "0001", "1");
    }

    private void resolveBom(final int stufe, final String matnr, final String werk, final String verwendung) throws AbapException {
        // No Warning => Everything peachy
        if (Character.MIN_VALUE == bomService.checkBom(matnr, werk, verwendung)) {
            final List<BomPosition> positions = bomService.read(matnr, werk, verwendung);
            for (BomPosition pos : positions) {
                final MaterialDetail material = materialService.getDetail(pos.getMatnr());
                logger.info(format("%-10s %6s %-2s %s", repeat("..", stufe), pos.getKomponentenMenge(), pos.getKomponentenEinheit(), material.getDescription()));
                resolveBom(stufe + 1, pos.getMatnr(), werk, verwendung);
            }
        }
    }

    private final JCoDestination destination;
    private final JcoServiceFactory jcoServiceFactory;
    private final MaterialService materialService;
    private final BomService bomService;

    private static final Logger logger = LoggerFactory.getLogger(MaterialServiceTest.class);
}


