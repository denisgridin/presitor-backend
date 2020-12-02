package ru.sfedu.course_project.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DataProviderCSV implements DataProvider {
    private final String PATH="csv_path";

    private final String FILE_EXTENTION="csv";

    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    @Override
    public String getName () {
        return "CSV";
    }

    private String getFilePath (String object) {
        try {
            return ConfigurationUtil.getConfigurationEntry(PATH) +
            object.toLowerCase() +
            ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UUID createPresentation(HashMap args) {
        try {
            Presentation presentation = new Presentation(args);
            List<Presentation> listPresentations = new ArrayList();
            listPresentations.add(presentation);

            String path = this.getFilePath("presentation");
            FileWriter filePath = new FileWriter(path);
            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<Presentation> beanToCsv = new StatefulBeanToCsvBuilder<Presentation>(writer).withSeparator(',').withApplyQuotesToAll(false).build();
            beanToCsv.write(listPresentations);
            log.info("Presentation was successfully created: " + presentation.getId());
            writer.close();
            return presentation.getId();
        } catch (IndexOutOfBoundsException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            log.error(e);
            log.error("Unable to create presentation");
            return null;
        }
    }

    public Presentation getPresentationById (UUID id) throws IOException {
        FileReader fileReader = new FileReader(this.getFilePath("presentation"));
        CSVReader csvReader = new CSVReader(fileReader);
        CsvToBean<Presentation> csvToBean = new CsvToBeanBuilder<Presentation>(csvReader)
                .withType(Presentation.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        log.debug(csvToBean);
        List<Presentation> listPresentation = csvToBean.parse();
        try {
            Presentation presentation = listPresentation.stream()
                    .filter(el -> el.getId() == id).limit(1).findFirst().get();
            return presentation;
        } catch (NoSuchElementException e) {
            log.error(e);
            return null;
        }
    }
}
