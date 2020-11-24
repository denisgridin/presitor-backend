package ru.sfedu.course_project.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.enums.DataType;

public class DataProvider {
    private static Logger log = LogManager.getLogger(DataProvider.class);

    public DataProvider () {}

    public DataProvider (DataType type) {
        try {
            switch (type) {
                case csv: {
                    DataProvider provider = new DataProviderCSV();
                    break;
                }
                case xml: {
                    log.debug("xml");
                    break;
                }
            }
            log.info(String.format("Created %s data provider", type));
        } catch (RuntimeException e) {
            log.error(String.format("Unable to create %s data provider", type));
        }
    }

}
