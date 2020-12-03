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
import ru.sfedu.course_project.api.DataProvider;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DataProviderCSV implements DataProvider {
    private final String PATH="csv_path";

    private final String FILE_EXTENTION="csv";

    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderCSV () {}

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
            List<Presentation> listPresentations = getAllPresentations();
            if (args.get("id") != null) {
                String id = (String) args.get("id");
                if (!validatePresentationId(id, listPresentations)) return null;
                // TODO return Error;
            }
            Presentation presentation = new Presentation(args);
            listPresentations.add(presentation);
            String path = getFilePath("presentation");
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

    public Boolean validatePresentationId (String id, List<Presentation> presentations) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional presentation = presentations.stream().filter(el -> el.getId().equals(uuid)).findFirst();
            if (presentation.isPresent()) {
                log.warn("[createPresentation] Provided presentation id is already in use: " + id);
                return false;
            }
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return false;
        }
    }

    public Presentation getPresentationById (HashMap arguments) throws IOException {
        UUID id = UUID.fromString((String) arguments.get("id"));
        try {
            List<Presentation> listPresentations = getAllPresentations();
            log.debug("Attempt to find presentation: " + id);
            Optional<Presentation> presentation = listPresentations.stream()
                    .filter(el -> el.getId().equals(id)).findFirst();
            Presentation result = presentation.isPresent() ? presentation.get() : null;
            if (presentation.isPresent()) {
                log.info("[getPresentationById] Result: " + result.toString());
            } else {
                log.error("[getPresentationById] Unable to get presentation: " + id);
            }
            return result;
        } catch (NoSuchElementException e) {
            log.error(e);
            log.error("Unable to get presentation");
            return null;
        }
    }

    public List<Presentation> getAllPresentations () {
        try {
            FileReader fileReader = new FileReader(getFilePath("presentation"));
            CSVReader csvReader = new CSVReader(fileReader);
            CsvToBean<Presentation> csvToBean = new CsvToBeanBuilder<Presentation>(csvReader)
                    .withType(Presentation.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Optional<List> listPresentation = Optional.ofNullable(csvToBean.parse());
            List<Presentation> result = listPresentation.isPresent() ? listPresentation.get() : new ArrayList<>();
            if (listPresentation.isPresent()) {
                log.info("[getAllPresentations] Result: " + result.toString());
            } else {
                log.error("[getAllPresentations] Unable to get presentations");
            }
            return result;
        } catch (RuntimeException | FileNotFoundException e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }
}
