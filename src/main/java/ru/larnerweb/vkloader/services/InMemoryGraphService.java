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


//@Service
public class InMemoryGraphService {

    @Autowired
    FriendListBDRepository friendsListBDRepository;

    private static final Logger log = LoggerFactory.getLogger(InMemoryGraphService.class);

    private int[][] adjacencyList;
    private static String storePath;
    private static int size;
    Kryo kryo;

    @Value("${app.graph.storePath}")
    public void setStorePath(String storePath) {
        InMemoryGraphService.storePath = storePath;
    }

    @Value("${app.graph.size}")
    public void setSize(int size) {
        InMemoryGraphService.size = size;
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
        if (adjacencyList[id] != null)
            return true;
        else
            return false;
    }


    /**
     * add id and corresponding friend list to array
     * @param id - user Id
     * @param friends - user's friend list
     */
    public void set(int id, int[] friends){
        adjacencyList[id] = friends;
    }

    public int[] get(int id){
        return adjacencyList[id];
    }


    /**
     * dump array to the disk using {storePath} variable
     */
    @Scheduled(cron = "0 0 * * * ?")  // run every hour
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
            adjacencyList = new int[size][];
            readFromDB();
            log.info("Creating fresh dump...");
            serialize();
        }

        log.info("Service instantiation... done!");
    }

    public void readFromDB() {
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
    }


    @Override
    public String toString() {
        return "InMemoryGraphRepresentation{" +
                "adjacencyListLength=" + adjacencyList.length +
                ", storePath='" + storePath + '\'' +
                '}';
    }
}
