package socialnetwork.service;

import javafx.scene.control.CheckBox;
import socialnetwork.domain.Event;
import socialnetwork.domain.EventUserDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.EventDBRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.ChangeEventType;

import socialnetwork.utils.events.EventChangeEvent;
import socialnetwork.utils.events.RequestsChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class EventService implements Observable<EventChangeEvent> {
    private EventDBRepository eventRepo;


    public EventService(EventDBRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    private List<Observer<EventChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<EventChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EventChangeEvent> e) {
        //observers.remove(e);

    }

    @Override
    public void notifyObservers(EventChangeEvent t) {
        observers.stream().forEach(x->x.update(t));

    }

    public Event addEvent(Event event){
        try{
            eventRepo.save(event);
            notifyObservers(new EventChangeEvent(ChangeEventType.ADD,event));
            return event;
        }catch(ValidationException e){throw new ServiceException(e.getMessage()); }

    }
    public void deleteEvent(long eventID){
        if(eventRepo.findOne(eventID)==null) throw new ServiceException("There is no event with the given id\n");
        try{
            eventRepo.delete(eventID);
            notifyObservers(new EventChangeEvent(ChangeEventType.ADD,eventRepo.findOne(eventID)));

        }catch(ValidationException e){throw new ServiceException(e.getMessage()); }

    }
    public Event getEventByID(long eventID){
        return eventRepo.findOne(eventID);
    }
    public void participateToEvent(Long userID,Long eventID){

        eventRepo.participateToEvent(userID,eventID);
        notifyObservers(new EventChangeEvent(ChangeEventType.UPDATE, eventRepo.findOne(eventID)));

    }

    public void stopParticipatingToEvent(Long userID,Long eventID){
        eventRepo.stopParticipatingToEvent(userID,eventID);
        notifyObservers(new EventChangeEvent(ChangeEventType.UPDATE, eventRepo.findOne(eventID)));

    }

    public List<Long> getAllEventsParticipatingTo(long userID){
        return StreamSupport.stream(eventRepo.getAllEventsParticipatingTo(userID).spliterator(),false)
                .collect(Collectors.toList());
    }

    public List<Utilizator> getAllParticipantsToEvent(long eventID){
        return StreamSupport.stream(eventRepo.getAllParticipantsToEvent(eventID).spliterator(),false)
        .collect(Collectors.toList());
    }

    public void notifyEvent(long userID,long eventID){

        eventRepo.notifyEvent(userID,eventID);
        notifyObservers(new EventChangeEvent(ChangeEventType.UPDATE, eventRepo.findOne(eventID)));

    }

    public void stopNotifyingEvent(long userID,long eventID) {

        eventRepo.stopNotifyingEvent(userID,eventID);
        notifyObservers(new EventChangeEvent(ChangeEventType.UPDATE, eventRepo.findOne(eventID)));

    }
    public List<Long> getAllNotifyingEvents(long userID) {
       return StreamSupport.stream(eventRepo.getAllNotifyingEvents(userID).spliterator(),false)
                .collect(Collectors.toList());
    }
    public List<EventUserDTO> getAllEventsDTO(long userID){
        List<Long> eventsToParticipate=StreamSupport.stream(eventRepo.getAllEventsParticipatingTo(userID).spliterator(),false)
                .collect(Collectors.toList());
        List<Long> notifyingEvents=StreamSupport.stream(eventRepo.getAllNotifyingEvents(userID).spliterator(),false)
                .collect(Collectors.toList());
        return StreamSupport.stream(eventRepo.findAll().spliterator(),false)
                .map(x->{
                    EventUserDTO dto;
                    Long notification=0L;
                    if(notifyingEvents.contains(x.getId())) notification=1L;
                    if(eventsToParticipate.contains(x.getId()))
                        dto=new EventUserDTO(x.getTitle(),x.getStart_date(),x.getId(),1L,notification);
                    else dto=new EventUserDTO(x.getTitle(),x.getStart_date(),x.getId(),0L,notification);
                    return dto;

                })
                .collect(Collectors.toList());
    }
    public List<CheckBox> getAllParticipatingCheckBox(long userID){
        return StreamSupport.stream(getAllEventsDTO(userID).spliterator(),false)
                .map(x->{
                    return x.getParticipating();
                })
                .collect(Collectors.toList());
    }


    //paging
    private int page = -1;
    private int size = 1;

    public void setPageSize(int size) {
        this.size = size;
    }
    public int getPageSize(){return this.size;}
    public List<EventUserDTO> getNextEvents(long lastID,long userID) {
        this.page++;
        return getEventsOnPage(this.page,userID,lastID);
    }

    public List<EventUserDTO> getEventsOnPage(int page,long userID,long lastID) {
        this.page=page;
        List<Long> eventsToParticipate=StreamSupport.stream(eventRepo.getAllEventsParticipatingTo(userID).spliterator(),false)
                .collect(Collectors.toList());
        List<Long> notifyingEvents=StreamSupport.stream(eventRepo.getAllNotifyingEvents(userID).spliterator(),false)
                .collect(Collectors.toList());
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Event> eventPage = eventRepo.findAllPage(pageable,lastID);
        return eventPage.getContent()
                .map(x->{
                    EventUserDTO dto;
                    Long notification=0L;
                    if(notifyingEvents.contains(x.getId())) notification=1L;
                    if(eventsToParticipate.contains(x.getId()))
                        dto=new EventUserDTO(x.getTitle(),x.getStart_date(),x.getId(),1L,notification);
                    else dto=new EventUserDTO(x.getTitle(),x.getStart_date(),x.getId(),0L,notification);
                    return dto;

                })
                .collect(Collectors.toList());
    }


}
