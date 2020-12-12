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
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.BaseClass;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataProviderCSV implements DataProvider {
    private final String PATH="csv_path";
    private final String DATA_PATH="dataPath";

    private final String FILE_EXTENTION="csv";

    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderCSV () {}

    @Override
    public String getName () {
        return "CSV";
    }

    private String getFilePath (CollectionType collectionType) {
        try {
            return String.format("%s/%s/%s.%s",
                    ConfigurationUtil.getConfigurationEntry(DATA_PATH),
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION),
                    collectionType,
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    ///                      Common section                                 \\\

    @Override
    public <T> Optional<List> getCollection (CollectionType collectionType, Class cl) {
        try {
            FileReader fileReader;
            try {
                log.debug("[getCollection] Attempt to get collection: " + collectionType);
                Optional optionalFileReader = Optional.ofNullable(new FileReader(getFilePath(collectionType)));
                if (optionalFileReader.isPresent()) {
                    fileReader = (FileReader) optionalFileReader.get();
                    CSVReader csvReader = new CSVReader(fileReader);
                    CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                            .withType(cl)
                            .withSeparator(',')
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
                    Optional<List> optionalCollection = Optional.ofNullable(csvToBean.parse());
                    log.info("[getCollection] Collection was retrieved: " + collectionType);
                    return optionalCollection;
                } else {
                    log.info("[getCollection] Unable to get data source: " + collectionType);
                    return Optional.empty();
                }
            } catch (FileNotFoundException e) {
                log.error(e);
                log.error(String.format(Constants.MSG_ERROR_DATA_SOURCE, collectionType));
                return Optional.empty();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public <T> Status writeCollection(List list, Class cl) {
        try {
            log.debug(String.format("[writeCollection] Attempt to write in %s", cl.getSimpleName().toLowerCase()));
            CollectionType collectionType = CollectionType.valueOf(cl.getSimpleName().toLowerCase());
            String path = getFilePath(collectionType);
            FileWriter filePath = new FileWriter(path);
            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).withSeparator(',').withApplyQuotesToAll(false).build();
            beanToCsv.write(list);
            writer.close();
            log.info("[writeCollection] Collection was wrote");
            return Status.success;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
            log.error("[writeCollection] Unable to write collection");
            return Status.error;
        }
    }

    @Override
    public <T extends BaseClass> Status removeRecordById (CollectionType collectionType, Class cl, UUID id) {
        try {
            log.debug(String.format("[removeRecordById] Removing elements from collection: %s", collectionType));
            List <T> collection = getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
            List <T> updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
            if (updatedCollection.size() == collection.size()) {
                log.error("[removeRecordById] Unable to find element with provided id: " + id);
                return Status.error;
            }
            writeCollection(updatedCollection, cl);
            log.info("[removeRecordById] Element was successfully removed: " + id);
            return Status.success;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[removeRecordById] Unable to remove element from collection");
            return Status.error;
        }
    }

    public <T extends BaseClass> Optional<T> getInstanceById (Class cl, CollectionType collectionType, HashMap arguments) {
        UUID id = UUID.fromString((String) arguments.get("id"));
        try {
            List<T> listInstance = getCollection(collectionType, cl).orElse(new ArrayList());
            log.debug("Attempt to find presentation: " + id);
            Optional<T> instance = listInstance.stream()
                    .filter(el -> el.getId().equals(id)).findFirst();
            return instance;
        } catch (NoSuchElementException e) {
            log.error(e);
            return Optional.empty();
        }
    }


    @Override
    public <T extends BaseClass> Boolean isIdInUse (String id, List<T> list) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<T> item = list.stream().filter(el -> el.getId().equals(uuid)).findFirst();
            if (item.isPresent()) {
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return false;
        }
    }

//    @Override
//    public <T> Status addCollectionRecord (T record, UUID id) {
//        try {
//            String collectionType = record.getClass().getName().toLowerCase();
//            List itemsList = getCollection(CollectionType.valueOf(collectionType), record.getClass()).orElse(new ArrayList());
//            itemsList.add(record);
//            Status result = writeCollection(itemsList, record.getClass());
//            if (result == Status.success) {
//                log.info(String.format("[addCollectionRecord] Item was successfully added: %s %s", collectionType, id));
//                return Status.success;
//            } else {
//                log.error("[addCollectionRecord] Unable to create presentation");
//                return Status.error;
//            }
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            log.error(e);
//            log.error("[addCollectionRecord] Unable to add collection record ");
//            return Status.error;
//        }
//    }


    ///                      Presentations section                          \\\

    @Override
    public Result createPresentation(HashMap args) {
        try {
            List<Presentation> listPresentations = getCollection(CollectionType.presentation, Presentation.class)
                    .orElse(new ArrayList());
            if (args.get("id") != null) {
                String id = (String) args.get("id");
                if (isIdInUse(id, listPresentations)) return new Result(Status.error, ErrorConstants.ID_IN_USE);
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args);
            if (!optionalPresentation.isPresent()) {
                log.error(ErrorConstants.ARGUMENTS_ERROR);
                return new Result(Status.error, ErrorConstants.ARGUMENTS_ERROR);
            }
            Presentation presentation = optionalPresentation.get();
            listPresentations.add(presentation);
            Status result = writeCollection(listPresentations, Presentation.class);
            if (result == Status.success) {
                log.info(SuccessConstants.PRESENTATION_CREATE + presentation.getId());
                return new Result(Status.success, presentation.getId());
            } else {
                log.error(ErrorConstants.PRESENTATION_CREATE);
                return new Result(Status.error, ErrorConstants.PRESENTATION_CREATE);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error(e);
            log.error(ErrorConstants.PRESENTATION_CREATE);
            return new Result(Status.error, ErrorConstants.PRESENTATION_CREATE);
        }
    }

    public Result getPresentationById (HashMap arguments) {
        Optional <Presentation> presentation = getInstanceById(Presentation.class, CollectionType.presentation, arguments);
        return presentation.isPresent() ?
                new Result(Status.success, presentation.get()) :
                new Result(Status.error, ErrorConstants.PRESENTATION_GET);
    }

    public Result removePresentationById (HashMap arguments) {
        try {
            if (arguments.get("id") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            } else {
                UUID id = UUID.fromString((String) arguments.get("id"));
                Status status = removeRecordById(CollectionType.presentation, Presentation.class, id);
                if (status == Status.success) {
                    return new Result(Status.success, "ok");
                } else {
                    return new Result(Status.error, ErrorConstants.PRESENTATION_REMOVE);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ErrorConstants.PRESENTATION_REMOVE);
        }
    }

    public Result editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        try {
            UUID id = UUID.fromString((String) arguments.getOrDefault("id", null));
            if (id == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            }
            Boolean validId = getInstanceById(Presentation.class, CollectionType.presentation, arguments).isPresent();
            if (validId) {
                List<Presentation> list = getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().map(el -> {
                    if (el.getId().equals(id)) {
                        el.setFillColor((String) arguments.getOrDefault("fillColor", el.getFillColor()));
                        el.setFontFamily((String) arguments.getOrDefault("fontFamily", el.getFontFamily()));
                        el.setName((String) arguments.getOrDefault("name", el.getName()));
                    } return el;
                }).collect(Collectors.toList());
                Status result = writeCollection(updatedList, Presentation.class);
                if (result == Status.success) {
                    log.info(SuccessConstants.PRESENTATION_UPDATE + id);
                    return new Result(Status.success, SuccessConstants.PRESENTATION_UPDATE + id);
                }
                return new Result(Status.error, ErrorConstants.PRESENTATION_UPDATE + id);
            } else {
                log.info("[editPresentationOptions] Unable to find presentation: " + id);
                return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND + id);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Unable to edit presentation options");
            return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND);
        }
    }


    ///                         Slides section                          \\\

    public Result getPresentationSlides (HashMap arguments) {
        try {
            if (arguments.get("presentationId") == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }
            UUID presentationId = UUID.fromString((String) arguments.get("presentationId"));
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put("id", String.valueOf(presentationId));
            Optional<Presentation> presentation = getInstanceById(Presentation.class, CollectionType.presentation, getPresentationByIdParams);
            if (presentation.isPresent()) {
                Optional<List> listSlides = getCollection(CollectionType.slide, Slide.class);
                Optional<List> presentationSlides = Optional.empty();
                if (listSlides.isPresent()) {
                    log.debug("[getPresentationSlides] Attempt to find presentation slides for: " + presentationId);
                    List<Slide> list = listSlides.get();
                    presentationSlides = Optional.of(list.stream().filter(slide -> slide.getPresentationId().equals(presentationId)).collect(Collectors.toList()));
                }
                log.debug("[getPresentationSlides] Found presentation slides: " + presentationSlides.orElse(new ArrayList()));
                return new Result(Status.success, presentationSlides.orElse(new ArrayList()));
            } else {
                log.error(ErrorConstants.PRESENTATION_NOT_FOUND + presentationId);
                return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND + presentationId);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ErrorConstants.SLIDES_GET);;
            return new Result(Status.error, ErrorConstants.SLIDES_GET);
        }
    }

    public Result createPresentationSlide (HashMap arguments) {
        try {
            if (arguments.get("presentationId") == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put("id", String.valueOf(arguments.get("presentationId")));
            Optional<Presentation> optionalPresentation = getInstanceById(Presentation.class, CollectionType.presentation, getPresentationByIdParams);
            if (optionalPresentation.isPresent()) {
                log.info(arguments.entrySet());
                List<Slide> slides = (List<Slide>) getPresentationSlides(arguments).getReturnValue();
                arguments.put("index", slides.size());
                Optional<Slide> optionalSlide = (Optional<Slide>) new Creator().create(Slide.class, arguments);
                Slide slide = optionalSlide.orElse(new Slide());
                log.info("[createPresentationSlide] Create slide: " + slide.toString());
                log.debug("[createPresentationSlide] For presentation: " + slide.getPresentationId());
                slides.add(slide);
                Status status = writeCollection(slides, Slide.class);
                if (status == Status.success) {
                    log.info(SuccessConstants.SLIDE_CREATE + arguments.get("presentationId"));
                    return new Result(Status.success, slide.getId());
                } else {
                    log.error(ErrorConstants.SLIDE_CREATE + arguments.get("presentationId"));
                    return new Result(Status.error, ErrorConstants.SLIDE_CREATE + arguments.get("presentationId"));
                }
            } else {
                return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND + arguments.get("presentationId"));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ErrorConstants.SLIDE_CREATE);
        }
    }
}
