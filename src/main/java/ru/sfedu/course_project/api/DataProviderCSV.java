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
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    ///                      Presentations section                          \\\

    @Override
    public UUID createPresentation(HashMap args) {
        try {
            List<Presentation> listPresentations = getAllPresentations().orElse(new ArrayList());
            if (args.get("id") != null) {
                String id = (String) args.get("id");
                if (isPresentationIdInUse(id, listPresentations)) return null;
                // TODO return Error;
            }
            Presentation presentation = new Presentation(args);
            listPresentations.add(presentation);
            writePresentationList(listPresentations);
            log.info("Presentation was successfully created: " + presentation.getId());
            return presentation.getId();
        } catch (IndexOutOfBoundsException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            log.error(e);
            log.error("Unable to create presentation");
            return null;
        }
    }

    private void writePresentationList (List<Presentation> list) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try {
            String path = getFilePath("presentation");
            FileWriter filePath = new FileWriter(path);
            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<Presentation> beanToCsv = new StatefulBeanToCsvBuilder<Presentation>(writer).withSeparator(',').withApplyQuotesToAll(false).build();
            beanToCsv.write(list);
            writer.close();
            log.info("[writePresentationList] Presentation list was wrote");
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
            log.error("[writePresentationList] Unable to write presentation list");
        }
    }

    public Boolean isPresentationIdInUse (String id, List<Presentation> presentations) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<Presentation> presentation = presentations.stream().filter(el -> el.getId().equals(uuid)).findFirst();
            if (presentation.isPresent()) {
                log.warn("[createPresentation] Provided presentation id is already in use: " + id);
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }

    public Optional<Presentation> getPresentationById (HashMap arguments) throws IOException {
        UUID id = UUID.fromString((String) arguments.get("id"));
        try {
            List<Presentation> listPresentations = getAllPresentations().orElse(new ArrayList());
            log.debug("Attempt to find presentation: " + id);
            Optional<Presentation> presentation = listPresentations.stream()
                    .filter(el -> el.getId().equals(id)).findFirst();
            if (presentation.isPresent()) {
                log.info("[getPresentationById] Result: " + presentation.get().toString());
            } else {
                log.error("[getPresentationById] Unable to get presentation: " + id);
            }
            return presentation;
        } catch (NoSuchElementException e) {
            log.error(e);
            log.error("Unable to get presentation");
            return Optional.empty();
        }
    }

    public Optional<List> getAllPresentations () {
        try {
            FileReader fileReader = new FileReader(getFilePath("presentation"));
            CSVReader csvReader = new CSVReader(fileReader);
            CsvToBean<Presentation> csvToBean = new CsvToBeanBuilder<Presentation>(csvReader)
                    .withType(Presentation.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Optional<List> listPresentation = Optional.ofNullable(csvToBean.parse());
            if (listPresentation.isPresent()) {
                log.info("[getAllPresentations] Result: " + listPresentation.get().toString());
            } else {
                log.error("[getAllPresentations] Unable to get presentations");
            }
            return listPresentation;
        } catch (RuntimeException | FileNotFoundException e) {
            e.printStackTrace();
            log.error(e);
            return Optional.empty();
        }
    }

    public Status removePresentationById (HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        try {
            if (arguments.get("id") == null) {
                log.error("[removePresentationById] Presentation id is not provide");
                return Status.error;
            } else {
                List<Presentation> presentationList = getAllPresentations().orElse(new ArrayList());
                if (presentationList.size() > 0) {
                    UUID id = UUID.fromString((String) arguments.get("id"));
                    List<Presentation> updatedList = presentationList.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    if (updatedList.size() == presentationList.size()) {
                        log.error("[removePresentationById] Unable to find presentation with provided id: " + id);
                        return Status.error;
                    }
                    writePresentationList(updatedList);
                    log.info("[removePresentationById] Presentation was successfully removed: " + id);
                    return Status.success;
                } else {
                    log.info("[removePresentationById] Unable to find presentation by provided id");
                    return Status.error;
                }
            }
        } catch ( CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
            log.error(e);
            return Status.error;
        }
    }

    public Status editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        try {
            UUID id = UUID.fromString((String) arguments.getOrDefault("id", null));
            if (id == null) {
                log.error("[editPresentationOptions] Presentation id is not provided");
                return Status.error;
            }
            Boolean validId = getPresentationById(arguments).isPresent();
            if (validId) {
                List<Presentation> list = getAllPresentations().orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().map(el -> {
                    if (el.getId().equals(id)) {
                        el.setFillColor((String) arguments.getOrDefault("fillColor", el.getFillColor()));
                        el.setFontFamily((String) arguments.getOrDefault("fontFamily", el.getFontFamily()));
                        el.setName((String) arguments.getOrDefault("name", el.getName()));
                    } return el;
                }).collect(Collectors.toList());
                writePresentationList(updatedList);
                log.info("[editPresentationOptions] Presentation options was successfully updated: " + id);
                return Status.success;
            } else {
                log.info("[editPresentationOptions] Unable to find presentation: " + id);
                return Status.error;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Unable to edit presentation options");
            return Status.error;
        }
    }

    ///                         Slides section                          \\\

    public Optional<List> getPresentationSlides (HashMap arguments) {
        try {
            FileReader fileReader;
            try {
                fileReader = Optional.of(new FileReader(getFilePath("slide"))).orElse(null);
            } catch (FileNotFoundException e) {
                log.error("[getPresentationSlides] Unable to get slides data source");;
                return Optional.empty();
            }
            if (fileReader != null) {
                CSVReader csvReader = new CSVReader(fileReader);
                CsvToBean<Slide> csvToBean = new CsvToBeanBuilder<Slide>(csvReader)
                        .withType(Slide.class)
                        .withSeparator(',')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                Optional<List> listSlides = Optional.ofNullable(csvToBean.parse());
                Optional<List> presentationSlides = Optional.empty();
                if (listSlides.isPresent()) {
                    UUID presentationId = UUID.fromString((String) arguments.get("presentationId"));
                    log.debug("[getPresentationSlides] Attempt to find presentation slides for: " + presentationId);
                    List<Slide> list = listSlides.get();
                    presentationSlides = Optional.of(list.stream().filter(slide -> slide.getPresentationId().equals(presentationId)).collect(Collectors.toList()));
                }
                log.debug("[getPresentationSlides] Found presentation slides: " + presentationSlides.orElse(new ArrayList()));
                log.info("[getPresentationSlides] Found " + arguments.get("id") + " slides for presentation: " + arguments.get("presentationId"));
                return presentationSlides;
            } else {
                return Optional.empty();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[getPresentationSlides] Unable to get presentation slides");;
            return Optional.empty();
        }
    }

    public Object createPresentationSlide (HashMap arguments) {
        try {
            if (arguments.get("presentationId") == null) {
                log.error("[createPresentationSlide] Argument is required: presentationId");
                return null;
            }
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put("id", arguments.get("presentationId"));
            Optional<Presentation> optionalPresentation = getPresentationById(getPresentationByIdParams);
            if (optionalPresentation.isPresent()) {
                log.info(arguments.entrySet());
                List<Slide> slides = getPresentationSlides(arguments).orElse(new ArrayList());
                arguments.put("index", slides.size());
                Slide slide = new Slide(arguments);
                log.info("[createPresentationSlide] Create slide: " + slide.toString());
                log.debug("[createPresentationSlide] For presentation: " + slide.getPresentationId());
                slides.add(slide);
                writeSlidesList(slides);
                return slide.getId();
            } else {
                log.error("[createPresentationSlide] Unable to find presentation with provided id");
                return Status.error;
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[createPresentationSlide] Unable to create slide");
            return Status.error;
        }
    }

    public Status writeSlidesList (List list) {
        try {
            String path = getFilePath("slide");
            FileWriter filePath = new FileWriter(path);
            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<Presentation> beanToCsv = new StatefulBeanToCsvBuilder<Presentation>(writer).withSeparator(',').withApplyQuotesToAll(false).build();
            log.debug("[writeSlidesList] Attempt to write slides list");
            beanToCsv.write(list);
            writer.close();
            log.debug("[writeSlidesList] Slides list was successfully wrote");
            return Status.success;
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[writeSlidesList] Unable to write slides list");
            return Status.error;
        }
    }
}
