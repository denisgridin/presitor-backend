package ru.sfedu.course_project.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.collections.set.PredicatedSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.Comment;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.BaseClass;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import javax.lang.model.type.ArrayType;
import javax.swing.text.html.Option;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.sfedu.course_project.enums.CollectionType.*;

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
    public <T> Status writeCollection(List list, Class cl, CollectionType collectionType) {
        try {
            log.debug(String.format("[writeCollection] Attempt to write in %s", cl.getSimpleName().toLowerCase()));

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
    public Status removeRecordById (CollectionType collectionType, Class cl, UUID id) {
        try {
            log.debug(String.format("[removeRecordById] Removing elements from collection: %s", collectionType));
            List updatedCollection = new ArrayList();
            Integer collectionSize = 0;
            switch (collectionType) {
                case presentation: {
                    List<Presentation> collection = (ArrayList<Presentation>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case slide: {
                    List<Slide> collection = (ArrayList<Slide>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case comment: {
                    List<Comment> collection = (ArrayList<Comment>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case element:
                    // TODO
                    break;
                case error:
                    break;
            }
            if (updatedCollection.size() == collectionSize) {
                log.error("[removeRecordById] Unable to find element with provided id: " + id);
                return Status.error;
            }
            writeCollection(updatedCollection, cl, collectionType);
            log.info("[removeRecordById] Element was successfully removed: " + id);
            return Status.success;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[removeRecordById] Unable to remove element from collection");
            return Status.error;
        }
    }

    public Result updateRecordInCollection (Class cls, CollectionType collectionType, Object instance, UUID instanceId) {
        try {
            log.debug("[updateRecordInCollection] Update: " + instance.toString());
            ArrayList updatedCollection = new ArrayList();
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType, cls).orElse(new ArrayList());
                    updatedCollection = (ArrayList) listInstance
                            .stream()
                            .map(el -> {
                                if (el.getId().equals(instanceId)) {
                                    el = (Presentation) instance;
                                }
                                return el;
                            }).collect(Collectors.toList());
                    break;
                }
                case slide: {
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType, cls).orElse(new ArrayList());
                    updatedCollection = (ArrayList) listInstance
                            .stream()
                            .map(el -> {
                                if (el.getId().equals(instanceId)) {
                                    el = (Slide) instance;
                                }
                                return el;
                            }).collect(Collectors.toList());
                    break;
                }
                case comment: {
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType, cls).orElse(new ArrayList());
                    updatedCollection = (ArrayList) listInstance
                            .stream()
                            .map(el -> {
                                if (el.getId().equals(instanceId)) {
                                    el = (Comment) instance;
                                }
                                return el;
                            }).collect(Collectors.toList());
                    break;
                }
                case element:
                    // TODO
                    break;
                case error:
                    break;
            }
            Status status = writeCollection(updatedCollection, cls, collectionType);
            log.debug("[updateRecordInCollection] Update status: " + status);
            return new Result(status, "");
        } catch (RuntimeException e) {
            log.error(e);
            return new Result(Status.error, ErrorConstants.INSTANCE_UPDATE);
        }
    }

    @Override
    public Optional getInstanceById (Class cl, CollectionType collectionType, HashMap arguments) {
        UUID id = UUID.fromString((String) arguments.get("id"));
        try {
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug("Attempt to find: " + id);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case slide: {
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug("Attempt to find: " + id);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case comment: {
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug("Attempt to find: " + id);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case template: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug("Attempt to find: " + id);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                default: {
                    return Optional.empty();
                }
            }
        } catch (NoSuchElementException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    public Optional getInstanceExistenceByField (CollectionType collectionType, Class cls, String idField, String id) {
        try {
            log.debug("[getInstanceExistenceByField] Get: " + cls.getSimpleName() + "; id: " + id);
            HashMap args = new HashMap();
            args.put(idField, id);
            Optional optionalInstance = getInstanceById(cls, collectionType, args);
            return optionalInstance;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.INSTANCE_NOT_FOUND);
            return Optional.empty();
        }
    }

// TODO убрать BaseClass

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
            List<Presentation> listPresentations = getCollection(presentation, Presentation.class)
                    .orElse(new ArrayList());
            if (args.get("id") != null) {
                String id = (String) args.get("id");
                if (isIdInUse(id, listPresentations)) return new Result(Status.error, ErrorConstants.ID_IN_USE);
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
            if (!optionalPresentation.isPresent()) {
                log.error(ErrorConstants.ARGUMENTS_ERROR);
                return new Result(Status.error, ErrorConstants.ARGUMENTS_ERROR);
            }
            Presentation presentation = optionalPresentation.get();
            listPresentations.add(presentation);
            Status result = writeCollection(listPresentations, Presentation.class, CollectionType.presentation);

            Boolean asTemplate = Boolean.valueOf( (String) args.getOrDefault("asTemplate", "false"));
            if (asTemplate) {
                addPresentationInTemplate(presentation);
            }

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

    @Override
    public Result addPresentationInTemplate (Presentation presentation) {
        try {
            HashMap args = new HashMap();
            args.put("id", String.valueOf(presentation.getId()));
            Optional<Presentation> optionalTemplate = getInstanceById(Presentation.class, template, args);
            if (optionalTemplate.isPresent()) {
                log.error(ErrorConstants.TEMPLATE_EXISTS + presentation.getId());
                return new Result(Status.error, ErrorConstants.TEMPLATE_EXISTS + presentation.getId());
            }
            ArrayList templates = (ArrayList) getCollection(template, Presentation.class).orElse(new ArrayList());
            templates.add(presentation);
            Status statusWrite = writeCollection(templates, Presentation.class, template);
            if (statusWrite == Status.success) {
                log.info(SuccessConstants.TEMPLATE_ADD + presentation.getId());
                return new Result(Status.success, SuccessConstants.TEMPLATE_ADD + presentation.getId());
            } else {
                log.error(ErrorConstants.TEMPLATE_ADD + presentation.getId());
                return new Result(Status.error, ErrorConstants.TEMPLATE_ADD + presentation.getId());
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.TEMPLATE_ADD + presentation.getId());
            return new Result(Status.error, ErrorConstants.TEMPLATE_ADD + presentation.getId());
        }
    }

    @Override
    public Result getPresentations () {
        try {
            Optional<List> optionalPresentations = getCollection(presentation, Presentation.class);
            log.debug(ConstantsInfo.PRESENTATIONS_GET);
            if (optionalPresentations.isPresent()) {
                return new Result(Status.success, optionalPresentations.get());
            } else {
                return new Result(Status.success, new ArrayList());
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.PRESENTATIONS_GET);
            return new Result(Status.error, ErrorConstants.PRESENTATIONS_GET);
        }
    }

    @Override
    public Result getPresentationById (HashMap arguments) {
        try {
            Optional <Presentation> presentation = getInstanceById(Presentation.class, CollectionType.presentation, arguments);
            return presentation.isPresent() ?
                    new Result(Status.success, presentation.get()) :
                    new Result(Status.error, ErrorConstants.PRESENTATION_GET);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.PRESENTATION_GET);
            return new Result(Status.error, ErrorConstants.PRESENTATION_GET);
        }
    }

    @Override
    public Result removePresentationById (HashMap arguments) {
        try {
            if (arguments.get("id") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            } else {
                UUID id = UUID.fromString((String) arguments.get("id"));
                Status status = removeRecordById(presentation, Presentation.class, id);
                Status statusRemoveSlides = removePresentationSlides(id);
                if (status == Status.success && statusRemoveSlides == Status.success) {
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

    public Status removePresentationSlides (UUID presentationId) {
        try {
            ArrayList slidesList = (ArrayList) getCollection(slide, Slide.class).orElse(new ArrayList());
            ArrayList updatedList = (ArrayList) slidesList.stream().filter(el -> {
                Slide slide = (Slide) el;
                return !slide.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug(ConstantsInfo.SLIDES_REMOVE);
            Status status = writeCollection(updatedList, Slide.class, slide);
            if (status == Status.success) {
                log.info(SuccessConstants.SLIDES_REMOVE);
            } else {
                log.info(ErrorConstants.SLIDES_REMOVE);
            }
            return status;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.SLIDES_REMOVE);
            return Status.error;
        }
    }

    @Override
    public Result editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        try {
            UUID id = UUID.fromString((String) arguments.getOrDefault("id", null));
            if (id == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            }
            Boolean validId = getInstanceById(Presentation.class, presentation, arguments).isPresent();
            if (validId) {
                List<Presentation> list = getCollection(presentation, Presentation.class).orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().map(el -> {
                    if (el.getId().equals(id)) {
                        String fillColor = (String) arguments.getOrDefault("fillColor", el.getFillColor());
                        String fontFamily = (String) arguments.getOrDefault("fontFamily", el.getFontFamily());
                        String name = (String) arguments.getOrDefault("name", el.getName());
                        Boolean asTemplate = Boolean.valueOf( (String) arguments.getOrDefault("asTemplate", "false"));
                        ArrayList slides = (ArrayList) arguments.getOrDefault("slides", el.getSlides());
                        ArrayList comments = (ArrayList) arguments.getOrDefault("comments", el.getComments());
                        log.debug(ConstantsInfo.FIELD_EDIT + "fillColor " + fillColor);
                        el.setFillColor(fillColor); // TODO вынести названия полей в const
                        log.debug(ConstantsInfo.FIELD_EDIT + "fontFamily " + fontFamily);
                        el.setFontFamily(fontFamily);
                        log.debug(ConstantsInfo.FIELD_EDIT + "name " + name);
                        el.setName(name);
                        log.debug(ConstantsInfo.FIELD_EDIT + "slides " + slides);
                        el.setSlides(slides);

                        log.debug(ConstantsInfo.FIELD_EDIT + "comments " + comments);
                        el.setComments(comments);
                        if (asTemplate) {
                            addPresentationInTemplate(el);
                        }
                    } return el;
                }).collect(Collectors.toList());
                Status result = writeCollection(updatedList, Presentation.class, presentation);
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

    @Override
    public Result createPresentationSlide (HashMap arguments) {
        try {
            if (arguments.get("presentationId") == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put("id", String.valueOf(arguments.get("presentationId")));
            Optional<Presentation> optionalPresentation = getInstanceById(Presentation.class, presentation, getPresentationByIdParams);
            if (optionalPresentation.isPresent()) {
                log.info(arguments.entrySet());

                List<Slide> slides = (List<Slide>) getPresentationSlides(arguments).getReturnValue();
                arguments.put("index", slides.size());
                Optional<Slide> optionalSlide = (Optional<Slide>) new Creator().create(Slide.class, arguments).getReturnValue();
                Slide slide = optionalSlide.orElse(new Slide());

                log.info("[createPresentationSlide] Create slide: " + slide.toString());
                log.debug("[createPresentationSlide] For presentation: " + slide.getPresentationId());


                ArrayList allSlides = (ArrayList) getCollection(CollectionType.slide, Slide.class).orElse(new ArrayList());
                allSlides.add(slide);
                Status statusCreateSlide = writeCollection(allSlides, Slide.class, CollectionType.slide);
//                Status statusAddSlideInPresentation = addPresentationSlide(slide, optionalPresentation.get());

//                if (statusCreateSlide == Status.success && statusAddSlideInPresentation == Status.success) {
                if (statusCreateSlide == Status.success) {
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

//    public Status addPresentationSlide (Slide slide, Presentation presentation ) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
//        try {
//            String id = String.valueOf(presentation.getId());
//            ArrayList<UUID> slides = presentation.getSlides();
//            ArrayList<UUID> updatedSlides = slides;
//            updatedSlides.add(slide.getId());
//            HashMap args = new HashMap();
//            args.put("id", id);
//            args.put("slides", updatedSlides);
//            log.info("[addPresentationSlide] Attempt to add slide " + slide.toString());
//            log.info("[addPresentationSlide] To presentation " + presentation.toString());
//            log.debug("[addPresentationSlide] Add slides: " + updatedSlides);
//            Result result = editPresentationOptions(args);
//            return result.getStatus();
//        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
//            log.error("[addPresentationSlide] Unable to add slide in presentation");
//            return Status.error;
//        }
//    }

    @Override
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
                Optional<List> listSlides = getCollection(slide, Slide.class);
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

    @Override
    public Result getSlideById (HashMap args) {
        try {
            if (args.get("presentationId") == null) {
                log.error("[getSlideById] Presentation id is not provided");
                return new Result(Status.error, "Presentation id is not provided");
            }
            if (args.get("id") == null) {
                log.error("[getSlideById] Slide id is not provided");
                return new Result(Status.error, "Slide id is not provided");
            }
            UUID presentationId = UUID.fromString((String) args.get("presentationId"));
            UUID slideId = UUID.fromString((String) args.get("id"));
            Optional<List> optionalSlides = getCollection(slide, Slide.class);
            if (optionalSlides.isPresent()) {
                Optional<Slide> slide = optionalSlides.get().stream().filter(el -> {
                    Slide item = (Slide) el;
                    return item.getPresentationId().equals(presentationId) && item.getId().equals(slideId);
                }).limit(1).findFirst();
                if (optionalSlides.isPresent()) {
                    return new Result(Status.success, slide.get());
                } else {
                    return new Result(Status.error, ErrorConstants.SLIDE_GET);
                }
            } else {
                log.error("[getSlideById] Unable to find slide for presentation: " + args.get("presentationId"));
                return new Result(Status.success, ErrorConstants.SLIDE_NOT_FOUND_IN_PRESENTATION + args.get("presentationId"));
            }
        } catch (RuntimeException e) {
            log.error("[getSlideById] " + ErrorConstants.SLIDE_GET);
            return new Result(Status.error, ErrorConstants.SLIDE_GET);
        }
    }

    @Override
    public Result editPresentationSlideById (HashMap args) {
        try {
            if (args.get("presentationId") == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }
            if (args.get("id") == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            }
            UUID presentationId = UUID.fromString((String) args.get("presentationId"));
            UUID slideId = UUID.fromString((String) args.get("id"));
            Optional<Slide> optionalSlide = getInstanceById(Slide.class, slide, args);
            if (optionalSlide.isPresent()) {
                Optional<List> slides = getCollection(slide, Slide.class);
                if (slides.isPresent()) {
                    ArrayList updatedSlides = (ArrayList) slides.get().stream().map(el -> {
                        Slide slide = (Slide) el;
                        if (slide.getPresentationId().equals(presentationId) && slide.getId().equals(slideId)) {
                            String name = (String) args.getOrDefault("name", slide.getName());
                            String index = (String) args.getOrDefault("index", String.valueOf(slide.getIndex()));
                            ArrayList elements = (ArrayList) args.getOrDefault("elements", slide.getElements());
                            slide.setName(name);
                            log.debug(ConstantsInfo.FIELD_EDIT + "name " + name);
                            slide.setIndex(Integer.valueOf(index));
                            log.debug(ConstantsInfo.FIELD_EDIT + "index " + index);
                            slide.setElements(elements);
                            log.debug(ConstantsInfo.FIELD_EDIT + "elements " + elements);
                        }
                        return slide;
                    }).collect(Collectors.toList());
                    Status status = writeCollection(updatedSlides, Slide.class, slide);
                    if (status == Status.success) {
                        log.debug(SuccessConstants.SLIDE_EDIT + slideId);
                        return new Result(status, SuccessConstants.SLIDE_EDIT + slideId);
                    } else {
                        log.debug(ErrorConstants.SLIDE_EDIT + slideId);
                        return new Result(status, ErrorConstants.SLIDE_EDIT + slideId);
                    }
                } else {
                    log.error(ErrorConstants.SLIDES_GET);
                    return new Result(Status.error, ErrorConstants.SLIDES_GET);
                }
            } else {
                log.error(ErrorConstants.SLIDE_NOT_FOUND_IN_PRESENTATION);
                return new Result(Status.error, ErrorConstants.SLIDE_NOT_FOUND_IN_PRESENTATION);
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.SLIDE_EDIT);
            return new Result(Status.error, ErrorConstants.SLIDE_EDIT);
        }
    }

    @Override
    public Result removePresentationSlideById (HashMap arguments) {
        try {
            if (arguments.get("id") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            }
            if (arguments.get("presentationId") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }
            UUID id = UUID.fromString((String) arguments.get("id"));
            String presentationId = (String) arguments.get("presentationId");
            log.debug("[removePresentationSlideById] Attempt to remove slide: " + id);
            Status statusRemoveFromPresentation = removeSlideFromPresentation(id, presentationId);
            Status status = removeRecordById(slide, Slide.class, id);
            log.debug("[removePresentationSlideById] Removed from data source: " + status);
            log.debug("[removePresentationSlideById] Remove from presentation: " + statusRemoveFromPresentation);
            if (status == Status.success && statusRemoveFromPresentation == Status.success) {
                return new Result(Status.success, "ok");
            } else {
                return new Result(Status.error, ErrorConstants.SLIDE_REMOVE + id);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ErrorConstants.SLIDE_REMOVE);
        }
    }

    public Status removeSlideFromPresentation (UUID id, String presentationId) {
        try {
            HashMap args = new HashMap();
            args.put("id", presentationId);
            Optional<Presentation> presentation = getInstanceById(Presentation.class, CollectionType.presentation, args);
            if (presentation.isPresent()) {
                log.debug("[removeSlideFromPresentation] Remove slide: " + id);
                log.debug("[removeSlideFromPresentation] From presentation: " + presentationId);
                ArrayList slides = presentation.get().getSlides();
                log.debug("[removeSlideFromPresentation] Presentation slides: " + slides);
                log.debug("[removeSlideFromPresentation] Remove slide: " + id);
                ArrayList updatedSlides = (ArrayList) slides.stream().filter(el -> el.equals(id)).collect(Collectors.toList());
                log.debug("[removeSlideFromPresentation] Updated list: " + updatedSlides);
                HashMap updateOptionsParams = new HashMap();
                updateOptionsParams.put("id", presentationId);
                updateOptionsParams.put("slides", updatedSlides);
                Result result = editPresentationOptions(updateOptionsParams);
                return result.getStatus();
            } else {
                log.error(ErrorConstants.PRESENTATION_NOT_FOUND + presentationId);
                return Status.error;
            }
        } catch (RuntimeException | CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            log.error(e);
            log.error("[removeSlideFromPresentation] Unable to remove slide from presentation");
            return Status.error;
        }
    }
    public Status updatePresentationCollection (Presentation presentation) {
        Optional<List> optionalList = getCollection(CollectionType.presentation, Presentation.class);
        if (optionalList.isPresent()) {
            ArrayList updatedCollection = (ArrayList) optionalList.get().stream().map(el -> {
                Presentation item = (Presentation) el;
                if (item.getId().equals(presentation.getId())) {
                    item.setSlides(presentation.getSlides());
                }
                return item;
            }).collect(Collectors.toList());
            return Status.success;
        } else {
            log.error("[updatePresentationCollection] Unable to get presentations collection");
            return Status.error;
        }
    }

    @Override
    public Result commentPresentation (HashMap arguments) {
        try {
            if (arguments.get("text") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "text");
            }
            if (arguments.get("presentationId") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }
            HashMap params = new HashMap();
            params.put("id", arguments.get("presentationId"));
            Optional<Presentation> optionalPresentation = getInstanceById(Presentation.class, presentation, params);
            if (optionalPresentation.isPresent()) {
                Result result = new Creator().create(Comment.class, arguments);
                if (result.getStatus() == Status.success) {
                    Comment comment = (Comment) result.getReturnValue();
                    if (optionalPresentation.isPresent()) {
                        log.info(ConstantsInfo.PRESENTATIONS_GET + optionalPresentation.get().toString());
//                        Status resultAddComment = addPresentationComment(comment, optionalPresentation.get());
                        Status resultWrite = writeCommentsCollection(comment);
//                        if (resultWrite == Status.success && resultAddComment == Status.success) {
                        if (resultWrite == Status.success) {
                            return new Result(Status.success, comment.getId());
                        } else {
                            return new Result(Status.success, ErrorConstants.UNEXPECTED_ERROR);
                        }
                    } else {
                        log.error(ErrorConstants.PRESENTATION_NOT_FOUND);
                        return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND);
                    }
                } else {
                    return result;
                }
            } else {
                log.error(ErrorConstants.PRESENTATION_NOT_FOUND + arguments.get("presentationId"));
                return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND + arguments.get("presentationId"));
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.COMMENT_CREATE);
            return new Result(Status.error, ErrorConstants.COMMENT_CREATE);
        }
    }

    @Override
    public Result getPresentationComments (HashMap args) {
        try {
            log.info(ConstantsInfo.COMMENTS_GET);

            if (args.get("presentationId") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "presentationId");
            }

            HashMap params = new HashMap();
            params.put("id", args.get("presentationId"));
            Result resultGetPres = getPresentationById(params);


            if (resultGetPres.getStatus() != Status.success) {
                return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND + params.get("id"));
            }

            UUID presentationId = UUID.fromString((String) args.get("presentationId"));

            ArrayList<Comment> comments = (ArrayList<Comment>) getCollection(comment, Comment.class).orElse(new ArrayList());
            ArrayList<Comment> presentationComments = (ArrayList<Comment>) comments.stream().filter(el -> {
                Comment item = el;
                return item.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.info(SuccessConstants.COMMENTS_GET + presentationComments);
            return new Result(Status.success, presentationComments);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.COMMENTS_GET);
            return new Result(Status.error, ErrorConstants.COMMENTS_GET);
        }
    }

    public Status writeCommentsCollection (Comment comment) {
        try {
            ArrayList comments = (ArrayList) getCollection(CollectionType.comment, Comment.class).orElse(new ArrayList());
            comments.add(comment);
            Status status = writeCollection(comments, Comment.class, CollectionType.comment);
            return status;
        } catch (RuntimeException e) {
            log.error(e);
            return Status.error;
        }
    }

//    public Status addPresentationComment (Comment comment, Presentation presentation) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
//        try {
//            String id = String.valueOf(presentation.getId());
//            ArrayList<UUID> comments = presentation.getComments();
//            comments.add(comment.getId());
//            HashMap args = new HashMap();
//            args.put("id", id);
//            args.put("comments", comments);
//            log.info("[addPresentationComment] Attempt to add comment " + comment.toString());
//            log.info("[addPresentationComment] To presentation " + presentation.toString());
//            log.debug("[addPresentationComment] Add comments: " + comments);
//            Result result = editPresentationOptions(args);
//            return result.getStatus();
//        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
//            log.error("[addPresentationComment] Unable to add comment in presentation");
//            return Status.error;
//        }
//    }

    @Override
    public Result editPresentationComment (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add("presentationId");
            fields.add("id");
            fields.add("text");
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = getInstanceExistenceByField(presentation, Presentation.class, "id", (String) arguments.get("presentationId"));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ErrorConstants.INSTANCE_NOT_FOUND);
            }

            Optional<Comment> optionalComment = getInstanceExistenceByField(comment, Comment.class, "id", (String) arguments.get("id"));
            if (!optionalComment.isPresent()) {
                return new Result(Status.error, ErrorConstants.INSTANCE_NOT_FOUND);
            }


            Comment comment = optionalComment.get();
            comment.setText((String) arguments.get("text"));
            Result resultUpdate = updateRecordInCollection(Comment.class, CollectionType.comment, comment, comment.getId());
            return resultUpdate;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.COMMENT_EDIT);
            return new Result(Status.error, ErrorConstants.COMMENT_EDIT);
        }
    }


    @Override
    public Result removePresentationComment (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add("presentationId");
            fields.add("id");
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = getInstanceExistenceByField(presentation, Presentation.class, "id", (String) arguments.get("presentationId"));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ErrorConstants.INSTANCE_NOT_FOUND);
            }

            Optional<Comment> optionalComment = getInstanceExistenceByField(comment, Comment.class, "id", (String) arguments.get("id"));
            if (!optionalComment.isPresent()) {
                return new Result(Status.error, ErrorConstants.INSTANCE_NOT_FOUND);
            }

            Comment comment = optionalComment.get();
            Status status = removeRecordById(CollectionType.comment, Comment.class, comment.getId());
            if (status == Status.success) {
                log.info(SuccessConstants.COMMENT_REMOVE);
                return new Result(Status.success, SuccessConstants.COMMENT_REMOVE + comment.getId());
            } else {
                log.error(ErrorConstants.COMMENT_REMOVE);
                return new Result(Status.error, ErrorConstants.COMMENT_REMOVE + comment.getId());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(ErrorConstants.COMMENT_REMOVE);
            return new Result(Status.error, ErrorConstants.COMMENT_REMOVE);
        }
    }


//    @Override
//    public Result removePresentationComment (HashMap arguments) {
////        try {
////
////        } catch (RuntimeException e) {
////
////        }
//    }




//    @Override
//    public Result editPresentationSlideOptionsById (HashMap arguments) {
//
//    }
}
