package ru.larnerweb.vkloader.services;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.larnerweb.vkloader.entity.FriendListBD;
import ru.larnerweb.vkloader.repository.FriendListBDRepository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class InMemoryGraphService {

    @Autowired
    FriendListBDRepository friendsListBDRepository;

    private static final Logger log = LoggerFactory.getLogger(InMemoryGraphService.class);

    private static int[][] adjacencyList;
    private static String storePath;
    private static int appGraphSize;
    public static int nodeCount;
    public static long edgeCount;
    Kryo kryo;

    @Value("${app.graph.storePath}")
    public void setStorePath(String storePath) {
        InMemoryGraphService.storePath = storePath;
    }

    @Value("${app.graph.size}")
    public void setSize(int size) {
        InMemoryGraphService.appGraphSize = size;
    }

    public InMemoryGraphService() {
        kryo = new Kryo();
        this.kryo.register(int[].class);
        this.kryo.register(int[][].class);
    }


    /**
     * Check if selected Id exests in array
     * @param id - id to search
     * @return true or false
     */
    public boolean isExists(int id) {
        return adjacencyList[id] != null;
    }


    /**
     * add id and corresponding friend list to array
     * @param id - user Id
     * @param friends - user's friend list
     */
    public void set(int id, int[] friends){
        adjacencyList[id] = friends;
    }

    /**
     * Get friend list by Id
     * @param id - user Id
     * @return  - user's friend list
     */
    public int[] getFriends(int id){
        return adjacencyList[id];
    }

    /** TODO: + int[] skipList
     * Search path in graph between two people
     * @param from - start person id
     * @param to - target person id
     * @return - list of people
     */
    public List<Integer> bfs(int from, int to){
        long startTime = System.currentTimeMillis();
        log.info("Starting search path between {} and {}",  from, to);


        Map<Integer, Integer> trace = new HashMap<>();
        List<Integer> result = new LinkedList<>();
        LinkedList<Integer> queue = new LinkedList<>();

        queue.add(from);
        trace.put(from, null);

        if (isExists(from) && isExists(to)) {
            boolean found = false;
            int iteration = 0;
            while (!found) {
                iteration++;

                if (iteration % 100000 == 0)
                    log.info("BFS ({}->???->{}) | iteration: {},\tqueue size: {},\ttrace\\cache size: {}", from, to, iteration, queue.size(), trace.size());

                int processedId = queue.remove();
                int[] friends = getFriends(processedId);
                if (friends == null) {
                    trace.put(processedId, null);
                    continue;
                }

                for (int i : friends) {
                    // check if target found
                    if (i == to) {
                        result.addAll(traverse(trace, processedId));
                        result.add(to);
                        found = true;
                        break;
                    }
                    // add to queue
                    if (!trace.containsKey(i)) {
                        queue.add(i);
                        trace.put(i, processedId);
                    }
                }
            }

            log.info("Path between {} and {} found: {}. Process took {} seconds.",  from, to, result.stream().map(String::valueOf).collect(Collectors.joining(" -> ")), (System.currentTimeMillis() - startTime)/1000);
            return result;
        } else {
            log.info("{} or {} not found in graph",  from, to);
            return null;
        }
    }

    /**
     *
     * @param trace
     * @param id
     * @return
     */
    private List<Integer> traverse(Map<Integer, Integer> trace, int id){
        LinkedList<Integer> result = new LinkedList<>();
        boolean rootReached = false;
        Integer nextId = id;

        while (!rootReached){
            result.addFirst(nextId);
            Integer parent = trace.get(nextId);

            if (parent != null)
                nextId = parent;
            else
                rootReached = true;
            log.info("Traverse {}", result);
        }

        return result;
    }


    /**
     * dump array to the disk using {storePath} variable
     */
    @Scheduled(cron = "0 0 6 * * ?")  // run every 6:00
    public void serialize(){
        final String SUFFIX = ".tmp";
        String dumpTmpPath = storePath + SUFFIX;
        try {
            // serialize and create dump
            log.info("Dump to {} is started...", dumpTmpPath);
            Output output = new Output(new FileOutputStream(dumpTmpPath));
            kryo.writeObject(output, adjacencyList);
            output.close();
            log.info("Dump is saved to temporary file {}...", dumpTmpPath);

            // delete old dump if exists
            File currentFile = new File(storePath);
            if (currentFile.exists()) {

                boolean deleted = currentFile.delete();
                if (!deleted)
                    throw new IOException("Cannot delete old dump file!");
            }

            // rename new dump
            File newFile = new File(storePath + SUFFIX);
            boolean renamed = newFile.renameTo(new File(storePath));
            if (!renamed)
                throw new IOException("Cannot rename new dump file!");
            else
                log.info("Dump to {} is finished!", storePath);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * read array from disk using {storePath} variable
     */
    public void deserialize(){
        log.info("Dump restore from {} is started...", storePath);
        try {
            adjacencyList = null;
            Input input = new Input(new FileInputStream(storePath));
            adjacencyList = kryo.readObject(input, int[][].class);
            input.close();
            log.info("Dump restore from {} is finished!", storePath);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }


    /**
     * Service initialization
     */
    @PostConstruct
    private void init() {
        log.info("Service instantiation...");

        // If dump exists
        File currentDump = new File(storePath);
        if (currentDump.exists()) {
            log.info("Dump file found, restoration...");
            deserialize();
        }
        else {
            log.info("Dump file not found, start loading records from database...");
            adjacencyList = new int[appGraphSize][];
            updateFromDB();
            log.info("Creating fresh dump...");
            serialize();
        }
        updateGraphInfo();

        log.info("Service instantiation... done!");
    }

    /**
     * Update nodeCount and edgeCount variables
     */
    private void updateGraphInfo() {
        log.info("Updating graph info...");
        for (int[] i : adjacencyList) {
            if (i != null) {
                nodeCount++;
                edgeCount += i.length;
            }
        }
    }

    /**
     * Read all rows from DB and update/replace items in adjacencyList
     */
    @Scheduled(cron = "0 0 3 * * ?")  // run every 3:00
    public void updateFromDB() {
        int currentPage = 0;
        while (true) {

            Page<FriendListBD> page =
                    friendsListBDRepository.findAll(PageRequest.of(currentPage, 1_000_000, Sort.by("id")));
            log.info("Fetching page {} of {}", currentPage, page.getTotalPages());
            if (currentPage > page.getTotalPages()) break;

            for (FriendListBD f : page) {
                set(f.getId(), f.getFriends());
            }
            currentPage++;
        }
        updateGraphInfo();
    }


    @Override
    public String toString() {
        return "InMemoryGraphRepresentation{" +
                "adjacencyListLength=" + adjacencyList.length +
                ", storePath='" + storePath + '\'' +
                '}';
    }
}
