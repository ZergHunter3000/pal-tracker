package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {


    private final CounterService counter;
    private final GaugeService gauge;
    private TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository,
                               CounterService counter,
                               GaugeService gauge) {

        this.timeEntryRepository = timeEntryRepository;
        this.counter = counter;
        this.gauge = gauge;
    }


    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntry) {

        TimeEntry savedEntry = timeEntryRepository.create(timeEntry);
        counter.increment("TimeEntry.created");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());

        return new ResponseEntity<TimeEntry>(savedEntry, HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable Long id) {

        TimeEntry readEntry = timeEntryRepository.find(id);

        if (readEntry == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        counter.increment("TimeEntry.read");

        return new ResponseEntity<TimeEntry>(readEntry, HttpStatus.OK);

    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody TimeEntry expected) {

        TimeEntry updateEntry = timeEntryRepository.update(id, expected);

        if (updateEntry == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        counter.increment("TimeEntry.updated");

        return new ResponseEntity<TimeEntry>(updateEntry, HttpStatus.OK);
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable Long id) {

        timeEntryRepository.delete(id);

        counter.increment("TimeEntry.deleted");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());

        return new ResponseEntity<TimeEntry>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {

        List<TimeEntry> listEntry = timeEntryRepository.list();

        counter.increment("TimeEntry.listed");

        return new ResponseEntity<List<TimeEntry>>(listEntry, HttpStatus.OK);
    }
}
